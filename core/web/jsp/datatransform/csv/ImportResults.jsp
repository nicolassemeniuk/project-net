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
			net.project.form.*,
			net.project.datatransform.csv.transformer.*,
			net.project.datatransform.csv.*,
			net.project.base.attribute.*"
			
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="csv" class="net.project.datatransform.csv.CSV" scope="session" />
<jsp:useBean id="attributes" class="net.project.base.attribute.AttributeCollection" scope="session" />
<jsp:useBean id="columnMaps" class="net.project.datatransform.csv.map.ColumnMaps" scope="session" />
<jsp:useBean id="csvAVwriter" class="net.project.datatransform.csv.CSVAttributeValueWriter" scope="page" />
<jsp:useBean id="csvImportObjectName" class="net.project.form.FormData" scope="session" />
<jsp:useBean id="csvWizard" class="net.project.datatransform.csv.CSVWizard" scope="session" />
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<template:getSpaceCSS />
<%-- Import Javascript --%>
<script language="javascript">
	
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	isLoaded = true;
}


function create() { 
	theAction("create");
	theForm.action.value = "<%=net.project.security.Action.CREATE%>";
	theForm.id.value = '';
	theForm.submit();
}

function next(){
	var theLocation='<%=SessionManager.getJSPRootURL() +csvWizard.getReferrer()%>';
	self.location = theLocation;
}

function help()
{
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=csv_import&section=import_results";
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
			<history:page display='<%=PropertyProvider.get("prm.form.csvimport.results.module.history")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/datatransform/csv/ImportResults.jsp"%>' />
		</history:history>
	
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br />
<table border="0" cellspacing="0" cellpadding="0" vspace="0" width="100%">
	<tr>
		<td class="channelHeader" width="1%"><img  src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
		<td nowrap class="channelHeader" align=left><%=PropertyProvider.get("prm.form.csvimport.results.channel.results.title")%></td>
		<td width=1% align=right class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
<%
csvAVwriter.write(csv,csvImportObjectName);
if(csvAVwriter.getDatabaseErrorsCount()==0){
%>
<td align="left" class="tableContent"><%=PropertyProvider.get("prm.form.csvimport.results.allrowsinserted.message")%></td>
<%
}else{
%>
<td align="left" class="tableContent"><%=PropertyProvider.get("prm.form.csvimport.results.somerowsinserted.message", new Object[] {String.valueOf (csvAVwriter.getDatabaseInsertsCount()), String.valueOf (csvAVwriter.getDatabaseErrorsCount())})%>
<%
}
csvWizard.clear(session);
%>
		<td>&nbsp;</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="next" label='<%=PropertyProvider.get("prm.form.csvimport.results.close.button.label")%>' />
	</tb:band>
</tb:toolbar>

</div>

<template:getSpaceJS />
</body>
</html>