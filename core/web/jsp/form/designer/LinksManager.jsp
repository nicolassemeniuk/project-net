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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------*/
%>

<%@page import="net.project.base.property.PropertyProvider"%><html>
<head>
<title>Form Designer -- Links</title>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Form Designer -- Definition" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.space.*, net.project.form.*, net.project.project.*, net.project.security.*" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formMenu" class="net.project.form.FormMenu" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>

<%
	// Set context for a new or existing Form
	formDesigner.setUser(user);
	formDesigner.setSpace(user.getCurrentSpace());
	 
	// Set the context for the form, and load from persistence.
	if (request.getParameter("id") != null)
	{
		formDesigner.setID(request.getParameter("id"));
		formDesigner.load();
	}

	String  refererURL = (String) pageContext.getAttribute("refererURL", pageContext.SESSION_SCOPE);
%>
<security:verifyAccess objectID='<%=formDesigner.getID()%>'
					   action="modify"
					   module="<%=net.project.base.Module.FORM%>"
/>
<template:getSpaceCSS/>
<template:import type="javascript" src="/src/window_functions.js" />
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/standard_prototypes.js" />
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {

    theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() { 
<%
	if(refererURL != null && !refererURL.trim().equals("")) {
%>
	self.document.location = '<%= refererURL %>';
<% 
	} else {
%>		
	self.document.location= JSPRootURL + "/form/designer/Main.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>"; 
<%	
	}
%>	
}

function reset()  { theForm.reset(); }

function submit() {
	theAction("submit");
	theForm.submit();
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=form_links_manager";
	openwin_help(helplocation);
}

</script>

</head>
<body class="main" onLoad="setup();">
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle"><history:display /></tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>
<form method="post" action="DefinitionEditProcessing.jsp">
<input type="hidden" name="theAction">

<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value="<jsp:getProperty name="formDesigner" property="ID"/>" />

<%-- Hardcoded to FORM type for now.  Must make this an option for supporting Property Sheets, Checklists, etc. later. --%>
<input type="hidden" name="classTypeID" value="<%= Form.FORM %>">

<%-- Header and Tab Bar --%>	
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<tr align="left">
	<td align="left" class="pageTitle">Form Designer</td>
	<td align="right" nowrap class="pageHeader">
		<span class="pageTitle"><jsp:getProperty name="formDesigner" property="name" /></span>
	</td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>
</table>
       
<tab:tabStrip>
	<tab:tab label="Definition" href='<%="DefinitionEdit.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY + "&id=" + formDesigner.getID()%>' />
	<tab:tab label="Fields" href='<%="FieldsManager.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY + "&id=" + formDesigner.getID()%>' />
	<tab:tab label="Preview" href='<%="Preview.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY + "&id=" + formDesigner.getID()%>' />
	<tab:tab label="Lists" href='<%="ListsManager.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY + "&id=" + formDesigner.getID()%>' />
<%-- Disabled for now since they are not implemented yet
	<tab:tab label="Search" href='<%="SearchEdit.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label="Links" href='<%="LinksManager.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY + "&id=" + formDesigner.getID()%>' selected="true" />
    <tab:tab label="Documentation" href='<%="DocumentationManager.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY + "&id=" + formDesigner.getID()%>' />
--%>
	<% if(!user.getCurrentSpace().getType().equals(net.project.space.Space.PERSONAL_SPACE)){%>
    <tab:tab label="Workflows" href='<%="WorkflowSelect.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <% } %>
    <tab:tab label="Activate" href='<%="ActivateEdit.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY + "&id=" + formDesigner.getID()%>' />
</tab:tabStrip>
<table border="0" width="100%" border="0" cellspacing="0" cellpadding="0">
<tr> 
	<td colspan="3">&nbsp;</td>
</tr>
<tr> 
	<td colspan="3" align="center"><i>
	<%=PropertyProvider.get("prm.project.form.designer.feature.not.implemented.label") %></i></td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>
<p />
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>
