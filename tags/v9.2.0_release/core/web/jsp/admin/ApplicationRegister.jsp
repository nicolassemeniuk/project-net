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
|     $Author: avinash $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
	info="Application Space" 
	language="java" 
	errorPage="/errors.jsp"
	import="net.project.security.User, 
			net.project.security.SessionManager, 
			net.project.admin.ApplicationAssignmentManager,
			net.project.admin.ApplicationSpace" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="applicationAssignmentManager" class="net.project.admin.ApplicationAssignmentManager" scope="session" />

<%--
	No need to implement a standard security validation for this page.
	Security will be implemented by logic itself since it is only possible for a user
	to affect his own assignments.
--%>
<template:getDoctype />

<%@page import="net.project.base.property.PropertyProvider"%><html>
<head>

<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="application" />

<%
// This page will be accessed via external notifications.  We need to make sure that
// if the user is already logged in they are sent through the notification frameset
if ((request.getParameter("external") != null) && (request.getParameter("inframe") == null)) {
    session.putValue("requestedPage", request.getRequestURI() + "?id=" + request.getParameter("id") + "&module=" + request.getParameter("module"));
    response.sendRedirect(SessionManager.getJSPRootURL() + "/NavigationFrameset.jsp");
    return;
}
%>

<%
	applicationAssignmentManager.setUser(user);
	applicationAssignmentManager.setApplicationID(request.getParameter("id"));
	applicationAssignmentManager.load();

	// If the assignment is not loaded, probably wasn't their assignment
	if (!applicationAssignmentManager.isLoaded()) {
	    throw new net.project.security.AuthorizationFailedException("Failed security validation");
	}
%>

<script language="javascript">
	function setAction(strAction){
       document.main.theAction.value=strAction;
       document.main.submit();
    }
	
	function cancel() {
		document.location = '<%=SessionManager.getJSPRootURL()%>/personal/Main.jsp';
	}
	
</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.dashboard.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display="Accept Assignment" />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" method="post" action="ApplicationRegisterProcessing.jsp">
	<input type="hidden" name="module" value="<%= net.project.base.Module.PERSONAL_SPACE %>" >
	<input type="hidden" name="theAction" value="">
</form>

<table width="97%" border="0" cellspacing="0" cellpadding="0">
	<tr class="channelHeader">
		<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
		<td nowrap class="channelHeader" width="85%"><jsp:getProperty name="applicationAssignmentManager" property="applicationName" /></td>
		<td align=right class="channelHeader" width="13%">&nbsp;</td>
		<td width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
	</tr>

	<tr valign="top">
		<td class="channelContent">&nbsp;</td>
		<td colspan=3 class="channelContent">

		<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tbody>
		<tr>
			<td align="left" class="tableHeader" width="20%"><%=PropertyProvider.get("prm.project.admin.description.label") %></td>
			<td class="tableContent">
				<jsp:getProperty name="applicationAssignmentManager" property="applicationDescription" />
			</td>
		</tr>
		<tr>
			<td align="left" class="tableHeader" width="20%"><%=PropertyProvider.get("prm.project.admin.responsabilities.label")%></td>
			<td class="tableContent">
				<jsp:getProperty name="applicationAssignmentManager" property="responsibilities" />
			</td>
		</tr>
		</tbody>
		</table>
		
		</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="accept" function="javascript:setAction('Accepted');" />
		<tb:button type="decline" function="javascript:setAction('Rejected');" />
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>

</div>

<template:getSpaceJS />
</body>
</html>

