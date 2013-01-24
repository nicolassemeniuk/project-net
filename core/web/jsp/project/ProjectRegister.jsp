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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|
|   Page designed to give basic project description before accept/reject
|   
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
       info="Project Register" 
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.security.SessionManager,
	   		   net.project.base.property.PropertyProvider,
	   		   net.project.util.DateFormat" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="projectassignmentmanager" class="net.project.project.ProjectAssignmentManager" scope="session" />
<html>
<head>
<META http-equiv="expires" content="0">

<title><display:get name="prm.project.projectregister.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS space="personal" />

<%--
/*Security Note:
No need to implement a standard security validation for this page.
Security will be implemented by logic itself since it is only possible for a user
to affect his own assignments.
*/
--%>
<%
// This page will be accessed via external notifications.  We need to make sure that
// if the user is already logged in they are sent through the notification frameset
if ((request.getParameter("external") != null) && (request.getParameter("inframe") == null))
    {
    session.putValue("requestedPage", request.getRequestURI() + "?projectid="+
                     request.getParameter("projectid") + "&module=" + request.getParameter("module"));
    response.sendRedirect(SessionManager.getJSPRootURL() + "/NavigationFrameset.jsp");
    return;
    }
%>
<%
DateFormat dateFormatter = user.getDateFormatter();
projectassignmentmanager.setUser(user);
projectassignmentmanager.setProjectID(request.getParameter("projectid"));
projectassignmentmanager.load();

java.util.Date endDate = projectassignmentmanager.getEndDate();
java.util.Date startDate = projectassignmentmanager.getStartDate();
String strStartDate = dateFormatter.formatDate(startDate);
String strEndDate = dateFormatter.formatDate(endDate);
// this is the only security check we need
if (projectassignmentmanager.getProjectName() == null)
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.projectregister.authorizationfailed.message"));
%>
<script language="javascript" src="/src/nav_personal.js"></script>

<script language="javascript">
    function setAction(strAction){
       document.projectregister.theAction.value=strAction;
       document.projectregister.submit();
    }
</script>
</head>
<body class="main">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td align="right">
	<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.main.project.label">
		<tb:band name="standard" />
	</tb:toolbar>
	</td>
</tr></table>
<table width="97%" border="0" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
<td nowrap class="channelHeader" width="85%"><jsp:getProperty name="projectassignmentmanager" property="projectName" /></td>
<td align=right class="channelHeader" width="13%">&nbsp;</td>
<td width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
</tr>

<tr valign="top">
<td class="channelContent">&nbsp;</td>
<td colspan=3 class="channelContent">
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tbody>
	<tr>
	<td align="left" class="tableHeader" width="20%"><display:get name="prm.project.projectregister.description.label" /></td>
	<td class="tableContent">
	<jsp:getProperty name="projectassignmentmanager" property="projectDescription" />
	</td>
	</tr>
	<tr>
	<td align="left" class="tableHeader" width="20%"><display:get name="prm.project.projectregister.responsibilities.label" /></td>
	<td class="tableContent">
	<jsp:getProperty name="projectassignmentmanager" property="responsibilities" />
	</td>
	</tr>
	<tr>
	<td align="left" class="tableHeader" width="20%"><display:get name="prm.project.projectregister.startdate.label" /></td>
	<td class="tableContent">
    <%= strStartDate %>
	</td>
	</tr>
	<tr>
	<td align="left" class="tableHeader" width="20%"><display:get name="prm.project.projectregister.enddate.label" /></td>
	<td class="tableContent">
    <%= strEndDate %>
	</td>
	</tr>



	</tbody>
	</table>
</td></tr>
</table>

<tb:toolbar style="action" showLabels="true">
		<tb:band name="action">
			<tb:button type="accept" function="javascript:setAction('Accepted');" />
			<tb:button type="decline" function="javascript:setAction('Rejected');" />
		</tb:band>
</tb:toolbar>

<form name="projectregister" method="post" action="ProjectRegisterProcessing.jsp">
<input type="hidden" name="module" value="<%= net.project.base.Module.PERSONAL_SPACE %>" >
<input type="hidden" name="theAction" value="">
<input type="hidden" name="external" value='<%=request.getParameter("external")%>' >
</form>
</body>
</html>
