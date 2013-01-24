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
			net.project.security.*,
			net.project.datatransform.csv.map.*,
			net.project.datatransform.csv.transformer.*,
			net.project.datatransform.csv.*,
			net.project.base.attribute.*,
			java.util.*,java.io.*,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="csv" class="net.project.datatransform.csv.CSV" scope="session" />
<jsp:useBean id="attributes" class="net.project.base.attribute.AttributeCollection" scope="session" />
<jsp:useBean id="columnMaps" class="net.project.datatransform.csv.map.ColumnMaps" scope="session" />
<jsp:useBean id="dataResolutionProcessing" class="net.project.datatransform.csv.DataResolutionPageProcessor" scope="page" />
<jsp:useBean id="csvWizard" class="net.project.datatransform.csv.CSVWizard" scope="session" />
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<template:insert> 
<template:put name="title" content="CSV Import" direct="true" /> 

<template:getSpaceCSS />
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function help()
{
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=csv_import&section=validation_results";
	openwin_help(helplocation);
}

function cancel() { 
	var theLocation='<%=SessionManager.getJSPRootURL() +csvWizard.getReferrer()%>';
	self.location = theLocation;
}

function create() { 
	theAction("create");
	theForm.action.value = "<%=net.project.security.Action.CREATE%>";
	theForm.id.value = '';
	theForm.submit();
}

function next(){
	theForm = self.document.forms[0];
	theForm.submit();
}

function back(){
    history.go(-1);
}

</script><%-- Import Javascript --%>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />


<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle">
			<history:history>
			<history:page display='<%=PropertyProvider.get("prm.form.csvimport.validation.module.history")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/datatransform/csv/ValidationResults.jsp"%>' />
		</history:history>
	
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br />
<form action='<%=SessionManager.getJSPRootURL() + "/datatransform/csv/ValidationResultsStatus.jsp"%>' method="post">
	<input type="hidden" name="module" value="<%=net.project.base.Module.PROJECT_SPACE%>" />

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="channelHeader" width="1%"><img  src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
		<td nowrap class="channelHeader" align=left><%=PropertyProvider.get("prm.form.csvimport.validation.channel.results.title")%></td>
		<td width=1% align=right class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="tableContent" align="left">
<%
	if(csv.getCSVErrorCellCollection().getErrorRowsCount()>0){
%>	
<%=PropertyProvider.get("prm.form.csvimport.validation.results.invalid.1.text", new Object[] {
String.valueOf (csv.getCSVErrorCellCollection().getErrorRowsCount()), String.valueOf (csv.getCSVCellCollection().getRowsCount())})%><a href='<%=SessionManager.getJSPRootURL() +"/datatransform/csv/ErrorResults.jsp"%>'><%=PropertyProvider.get("prm.form.csvimport.validation.results.invalid.2.link")%></a></br>
<%=PropertyProvider.get("prm.form.csvimport.validation.results.invalid.3.text")%><a href='<%=SessionManager.getJSPRootURL() +"/datatransform/csv/ErrorenousRowWriter.jsp"%>'><%=PropertyProvider.get("prm.form.csvimport.validation.results.invalid.4.link")%></a><%=PropertyProvider.get("prm.form.csvimport.validation.results.invalid.5.text")%>
<%
	}
	else{
%>
<%=PropertyProvider.get("prm.form.csvimport.validation.results.successful.message")%>
<%
	}
%>	
		</td>
		<td>&nbsp;</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="back" />
		<tb:button label='<%=PropertyProvider.get("prm.form.csvimport.validation.import.button.label")%>' type="next" />
	</tb:band>
</tb:toolbar>

</form>		
</template:insert>

</div>

<template:getSpaceJS />
</body>
</html>