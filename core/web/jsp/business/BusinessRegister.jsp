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
|   Page designed to give basic business description before accept/reject
|   
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
       info="Personal Space" 
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.security.User, 
               net.project.space.PersonalSpaceBean,  
               net.project.business.BusinessAssignmentManager,
			   net.project.base.property.PropertyProvider,
               net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="businessassignmentmanager" class="net.project.business.BusinessAssignmentManager" scope="session" />
<html>
<head>
<META http-equiv="expires" content="0">

<title><display:get name="prm.business.businessregister.title" /></title>
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
    session.putValue("requestedPage", request.getRequestURI() + "?businessid="+
                     request.getParameter("projectid") + "&module=" + request.getParameter("module"));
    response.sendRedirect(SessionManager.getJSPRootURL() + "/NavigationFrameset.jsp");
    return;
    }
%>
<%
businessassignmentmanager.setUser(user);
businessassignmentmanager.setBusinessID(request.getParameter("businessid"));
businessassignmentmanager.load();
// this is the only security check we need
if (businessassignmentmanager.getBusinessName() == null)
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.business.businessregister.authorizationfailed.message"));
%>
<script language="javascript" src="<%= SessionManager.getJSPRootURL() %>/src/nav_personal.js"></script>

<script language="javascript">
    function setAction(strAction){
       document.businessregister.theAction.value=strAction;
       document.businessregister.submit();
    }
</script>
</head>
<body class=main>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td align="right">
	<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.space.business">
		<tb:band name="standard" />
	</tb:toolbar>
	</td>
</tr></table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
<td width="1%" class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
<td nowrap class="channelHeader" width="98%"><jsp:getProperty name="businessassignmentmanager" property="businessName" /></td>
   <td width="1%" class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
</tr>
<tr valign="top">
<td class="channelContent">&nbsp;</td>
<td colspan=2 class="channelContent">
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tbody>
	<tr>
	<td align="left" class="tableHeader"  width="20%"><display:get name="prm.business.businessregister.description.label" /></td>
	<td class="tableContent">&nbsp;
	<jsp:getProperty name="businessassignmentmanager" property="businessDescription" />
	</td>
	</tr>
	<tr>
	<td align="left" class="tableHeader"><display:get name="prm.business.businessregister.responsibilities.label" /></td>
	<td class="tableContent">&nbsp;
	<jsp:getProperty name="businessassignmentmanager" property="responsibilities" />
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

<form name="businessregister" method="post" action="BusinessRegisterProcessing.jsp">
<input type="hidden" name="module" value="<%= net.project.base.Module.PERSONAL_SPACE %>" >
<input type="hidden" name="external" value='<%=request.getParameter("external")%>' >
<input type="hidden" name="theAction" value="">
</form>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>
