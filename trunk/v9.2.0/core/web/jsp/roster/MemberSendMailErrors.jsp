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

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Send Mail Errors" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
			net.project.base.property.PropertyProvider" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="spaceInvitationWizard" type="net.project.resource.SpaceInvitationManager" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.DIRECTORY%>" /> 
					   
<%
    String directoryPage = pageContextManager.getProperty ("directory.url.complete");
    String errorMessage= request.getParameter("errorMessage");
%>
<html>
<head>
<title><display:get name="prm.directory.membersendmailerrors.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<script language="javascript">
	var theForm;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  
	var isLoaded = false;
	
	var targetWindow;
	
function setup() {
    // Nothing to do
}

function cancel() { 
    self.document.location = "<%=pageContextManager.getProperty("directory.url.complete")%>";
}

</script>

</head>
<body class="main"  onLoad="setup();" id="bodyWithFixedAreasSupport" >
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
<div id='content'>
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<tr>
    <td>&nbsp;</td>
    <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        	<tr>
        		<td align="left" class="pageTitle"><display:get name="prm.directory.membersendmailerrors.column.sendemailerrors.name" /></td>
        		<td align="right" class="pageTitle">&nbsp;</td>
        	</tr>
        </table>
    </td>
    <td>&nbsp;</td>
<tr>
    <td colspan="3">&nbsp;</td>
</tr>
<tr>	
    <td>&nbsp;</td>
    <td class="tableContent">
        <pre class="tableContentFontOnly">
<%=net.project.util.HTMLUtils.escape(errorMessage)%>&nbsp;
        </pre>
    </td>
    <td>&nbsp;</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true">
        	<tb:band name="action">
        		<tb:button type="cancel" label='<%= PropertyProvider.get("prm.directory.membersendmailerrors.cancel.button.label") %>' />
        	</tb:band>
</tb:toolbar>

<br />
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>