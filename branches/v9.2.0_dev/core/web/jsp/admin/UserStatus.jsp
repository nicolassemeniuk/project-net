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
    info="Change User Status"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.SessionManager,
            net.project.resource.ProfileCodes,
            net.project.security.Action,
            net.project.security.User,
            net.project.base.DefaultDirectory,
            net.project.base.Module,
            net.project.util.HTMLUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>


<%
    // first check that this person is a fully-registered user.  if not, redirect to user list with error message
    if (!DefaultDirectory.hasValidUserEntry(request.getParameter("personID"))) {
%>


<html>
<head>
<title>Close Window</title>
<script language="javascript">

    var targetWindow;

    function setup() {

    JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

    if (self.opener.name != null && self.opener.name == "main")
        targetWindow = self.opener;
        else
        targetWindow = parent.opener;

        theLocation = JSPRootURL + '/admin/UserList.jsp?module=240&errorMessage=<%=HTMLUtils.escape("The person you attempted to modify does not have a valid user entry.")%>';
        targetWindow.location = theLocation;
        targetWindow.focus();
        parent.close();
    }
</script>
</head>
<body onLoad="setup()">


</body>
</html>

        <%
    } else {
%>

<html>
<head>
<title>Change User Status</title>

        <%-- Import CSS --%>
        <template:getSpaceCSS space="application" />

<script language="javascript">

function submit () {
theForm = self.document.forms["userStatus"];

theAction("changeStatus");
theForm.submit();
}
</script>

</head>

<body class="main">

<form name="userStatus" action="<%=SessionManager.getJSPRootURL()%>/admin/UserStatusProcessing.jsp" method="post">

<input type="hidden" name="theAction">
<input type="hidden" name="personID" value="<%=request.getParameter("personID")%>">
<input type="hidden" name="module" value="<%=net.project.base.Module.APPLICATION_SPACE%>" />
<input type="hidden" name="action" value="<%=Action.MODIFY%>" />

<table border="0" cellspacing="0" cellpadding="0" align="center" width="97%">
<tr class="channelHeader">
<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
<td align="center"nowrap class="channelHeader" colspan="2"><nobr>Change User Status</nobr></td>
<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr>
<td nowrap align="left" colspan="4">&nbsp;</td>
</tr>
<tr>
<td nowrap align="left" >&nbsp;</td>
<td nowrap align="left" class="tableHeader">User Status:</td>
<td>
<select name="userStatus">
	<%= ProfileCodes.getUserStatusOptionList ( request.getParameter ("currentStatus") ) %>
    </select>
    </td>
    <td nowrap align="left" >&nbsp;</td>
    </tr>
    <tr>
    <td nowrap align="left" colspan="4">&nbsp; </td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="submit" label="Change User Status" />
	</tb:band>
</tb:toolbar>

</form>
        <%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS space="application" />        
</body>
</html>

        <%
    }
%>
