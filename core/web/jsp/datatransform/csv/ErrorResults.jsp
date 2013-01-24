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
			java.util.*,
			java.io.*"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="csv" class="net.project.datatransform.csv.CSV" scope="session" />
<jsp:useBean id="attributes" class="net.project.base.attribute.AttributeCollection" scope="session" />
<jsp:useBean id="csvWizard" class="net.project.datatransform.csv.CSVWizard" scope="session" />
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function back() {
    history.back();
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=csv_import&section=errors";
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
			<history:module display='<%=PropertyProvider.get("prm.form.csvimport.module.history")%>' 
							jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/Main.jsp?module=" + net.project.base.Module.WORKFLOW + "&action=1"%>' />
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
		<td nowrap class="channelHeader" align="left" colspan="4">&nbsp;<%=PropertyProvider.get("prm.form.csvimport.errorresults.channel.results.title")%></td>
		<td width=1% align=right class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td align="left" class="tableHeader"><%=PropertyProvider.get("prm.form.csvimport.errorresults.value.column")%></td>
		<td align="left" class="tableHeader"><%=PropertyProvider.get("prm.form.csvimport.errorresults.columnnumber.column")%></td>
		<td align="left" class="tableHeader"><%=PropertyProvider.get("prm.form.csvimport.errorresults.rownumber.column")%></td>
		<td align="left" class="tableHeader"><%=PropertyProvider.get("prm.form.csvimport.errorresults.description.column")%></td>
		<td>&nbsp;</td>
	</tr>
<%
	Iterator itr =csv.getCSVErrorCellCollection().getCSVErrorCells().iterator();
	while(itr.hasNext()){
	CSVErrorCell csvErrorCell=(CSVErrorCell)itr.next();
%>
	<tr>	
		<td>&nbsp;</td>
		<td align="left" class="tableContent"><%=csvErrorCell.getCSVDataValue().getValue()%></td>
		<td align="left" class="tableContent"><%=Integer.valueOf(csvErrorCell.getCSVColumnNumber().getCSVColumnNumberValue()).intValue()+1%></td>
		<td align="left" class="tableContent"><%=csvErrorCell.getCSVRowNumber().getCSVRowNumberValue()%></td>
		<td align="left" class="tableContent"><%=csvErrorCell.getErrorDescription()%></td>
		<td>&nbsp;</td>
	</tr>
<%	
	}
%>	
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="back" />
	</tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
