<%--
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
--%>

<%@ page
    contentType="text/html; charset=UTF-8"
    info="Workflow"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.Space,
            net.project.security.User,
			net.project.security.SessionManager,
            net.project.form.*,
			java.util.ArrayList,
			net.project.datatransform.csv.*,
            net.project.base.attribute.*,                                                                              
            java.util.Iterator,
            net.project.base.Module,
            net.project.security.Action"

%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="csv" class="net.project.datatransform.csv.CSV" scope="session" />
<jsp:useBean id="csvImportObjectName" class="net.project.form.FormData" scope="session" />
<jsp:useBean id="csvWizard" class="net.project.datatransform.csv.CSVWizard" scope="session" />
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() {
	var theLocation='<%=SessionManager.getJSPRootURL() +csvWizard.getReferrer()%>';
	self.location = theLocation;
}

function create() {
	theAction("create");
	theForm.action.value = "<%=net.project.security.Action.VIEW%>";
	theForm.id.value = '';
	theForm.submit();
}

function next() {
	theForm.theAction.value="next";
	theForm.submit();
}

function back() {
	theForm.theAction.value="back";
	theForm.submit();
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=csv_import&section=columnmap";
	openwin_help(helplocation);
}
</script>
</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.form.csvimport.columnmap.module.history")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/datatransform/csv/ColumnMap.jsp"%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/datatransform/csv/ColumnMapProcessing.jsp">
<input type="hidden" name="module" value="<%=Module.FORM%>">
<input type="hidden" name="action" value="<%=Action.VIEW%>">
<input type="hidden" name="theAction">

<table border="0" cellspacing="0" cellpadding="0" vspace="0" width="100%">
<tr>
    <td class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15"></td>
    <td nowrap class="channelHeader" align=left><%=PropertyProvider.get("prm.form.csvimport.columnmap.channel.mapping.title")%></td>
    <td width="1%" align=right class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15"></td>
</tr>
</table>
<%
	AttributeCollection attrs = csvImportObjectName.getAttributes();

	session.setAttribute("attributes",attrs);
	Iterator pitr =attrs.iterator();
	CSVColumns ccols =csv.getCSVColumns();
	if(csvWizard.hasErrors()){
        out.println(csvWizard.getAllErrorMessages());
        csvWizard.clearErrors();
	}
%>
<table  border="0" vspace="0" width="100%">
<%	while(pitr.hasNext()) {
        IAttribute atr =(IAttribute)pitr.next();
		if (!(atr instanceof UnimportableAttribute) && atr != null) {
%>
<tr>
    <td width="20%"  class="tableContent" align="left"><%=atr.getDisplayName()%></td>
    <td width="80%"  align="left">
        <select name='<%="attribute_"+atr.getID()%>' size="1" style="width:100%">
        <option value='column_unassigned'><%=PropertyProvider.get("prm.form.csvimport.columnmap.option.unassigned.name")%></option>
<%
		    Iterator citr =ccols.iterator();

            while(citr.hasNext()) {
                CSVColumn csvColumn = (CSVColumn)citr.next();
                long colID=Integer.valueOf(csvColumn.getColumnID()).longValue()+1;
%>
        <option VALUE='<%="column_"+csvColumn.getColumnID()%>'><%=csvColumn.getColumnName()+" ("+colID+")"%></option>
<%          } %>
</select>
</td>
</tr>
<%      }
    }
%>
<tr></tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="back" />
		<tb:button type="next" />
	</tb:band>
</tb:toolbar>

</form>
</div>
<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceJS />
</body>
</html>
