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

<%
/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
%>
<html>
<head>
<title>Form Access History</title>
<link rel="stylesheet" type="text/css" href="/styles/pnet.css">
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Edit Form" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.form.*, net.project.security.*, net.project.base.Module" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />

<%
	// Editing existing form instance
	if (request.getParameter("id") != null)
	{
	 	form.loadData(request.getParameter("id"));
	}
	// Creating new form instance
	else
	{
		form.clearData();
	}
%>

<template:getSpaceCSS />

<template:import type="javascript" src="/src/util.js"/>
<template:import type="javascript" src="/src/standard_prototypes.js"/>

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	var fieldCopyFailed = false;
	var form_popup;
	var popupField;
	
function setup() {
   theForm = self.document.forms[0];
   isLoaded = true;
}

function cancel() { self.document.location = JSPRootURL + "/form/FormList.jsp"; }

function submit() {
    theAction("submit");
    theForm.submit();
}

function reset() {
	theForm.reset();
}

function resizeTextArea(field, inc) {
	eval('theForm.' + field + '.rows +=' + inc);
}

function setFieldRef(field) {
	if (field == null)
		field = popupField;
		
	eval('form_popup.fieldRef = theForm.' + field);
}

function popupTextArea(field, label) {
	form_popup = window.open('TextAreaPopup.jsp?module=<%=Module.FORM%>&label='+label, 'form_popup', "height=700,width=800, resizable=yes");
	
	if (form_popup.isloaded == true) {
		setFieldRef(field);
		form_popup.copyFieldData();
		form_popup.focus();
	}
	else
	{
		fieldCopyFailed = true;
		popupField = field;
	}
}
</script>

</head>
<body class="main" onLoad="setup();">
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle">
		<history:display />
	</tb:setAttribute>
	<tb:band name="standard" />
</tb:toolbar>
<form method="post" action="FormEditProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="class_id" value="<jsp:getProperty name="form" property="ID" />">
	<input type="hidden" name="data_object_id" value="<%= request.getParameter("id") %>">

<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr bgcolor="#B2B2B2"> 
	<td> 
	  	<span class=smallHeaderBlack><jsp:getProperty name="form" property="name" /> 
        (<jsp:getProperty name="form" property="abbreviation" />)</span> </td>
	<td align="center">
	  	<a href="AccessHistory.jsp?id=<%=request.getParameter("id")%>">Access History</a> 
		&nbsp;&nbsp; 
		<a href="ChangeHistory.jsp?id=<%=request.getParameter("id")%>">Change History</a>
		&nbsp;&nbsp;
		<a href="FormEdit.jsp?id=<%=request.getParameter("id")%>">Form</a>
	</td>
	<td colspan="2" align="right"> 
		<span class=smallHeaderGrey><%= user.getCurrentSpace().getName() %></span> 
	</td>
    </tr>
	<tr><td>Not Implemented Yet.</td></tr>
  </table>
</form>
<%-- Action Bar --%>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		
	</tb:band>
</tb:toolbar>
<p />
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>

