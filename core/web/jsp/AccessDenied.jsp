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
<%@
	page info="Access Denied Page"
    contentType="text/html; charset=UTF-8"
    isErrorPage="true"
    import="net.project.security.SessionManager,
    		net.project.base.property.PropertyProvider,
    		net.project.space.Space,
    		net.project.project.ProjectSpaceBean"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="groupProvider" class="net.project.security.group.GroupProvider" scope="page" />

<%
	if(exception == null)
		exception = new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.failed.validation"));

    net.project.space.Space accessDeniedSpace = null;
	net.project.resource.PersonList adminGroupMembers = null;
    boolean isAdminGroupMemberList = false;
	String errorMessage = null;
	boolean disableBackLink = false;
	disableBackLink = Boolean.valueOf(request.getParameter("disableBackLink")).booleanValue();
    // First determine in which space access was denied
    // If the exception is a net.project.security.AuthorizationFailedException,
    // and it has a non-null space, use that one
    // Otherwise, use the user's current space
    if (exception instanceof net.project.security.AuthorizationFailedException) {
        net.project.security.AuthorizationFailedException e = (net.project.security.AuthorizationFailedException) exception;
        accessDeniedSpace = e.getSpace();
    }
    if (accessDeniedSpace == null) {
        accessDeniedSpace = user.getCurrentSpace();
    }

    // Now determine the Space Admin group members in that space
    // and place in page scope for use by XML taglib
    try {
    	if(request.getParameter("spaceId") != null){
	    	Space space = new ProjectSpaceBean();
	    	space.setID(request.getParameter("spaceId"));
			adminGroupMembers = groupProvider.getSpaceAdminGroup(space).getPersonMembers();
    	} else {
	        adminGroupMembers = groupProvider.getSpaceAdminGroup(accessDeniedSpace).getPersonMembers();
    	}
        pageContext.setAttribute("adminGroupMembers", adminGroupMembers, PageContext.PAGE_SCOPE);
        isAdminGroupMemberList = true;

    } catch (net.project.security.group.GroupException ge) {
        // No Space Admin group in that space??
        // Generally not possible; but handle it gracefully
        isAdminGroupMemberList = false;
    }

    // Make sure the user gets a default error message if the exception does not provide one.
    if (exception.getMessage() == null) {
    	errorMessage = PropertyProvider.get("prm.project.main.not.permissions.failed");
    } else {
    	errorMessage = exception.getMessage();
    }
%>
<html>
<head>
<title>Access Denied</title>
<%-- Import CSS --%>
<template:getSpaceCSS space="personal" />

<style type="text/css">
    #hidden { position: static; visibility: hidden; }
    #shown { position: static; visibility: visible; }
    #footer { position: relative; }
</style>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/window_functions.js" />
<template:import type="javascript" src="/src/dhtml/findDOM.js" />
<template:import type="javascript" src="/src/dhtml/visibility.js"/>
<script language="javascript">
	var t_standard;
	var theForm;
	var page = false;
    var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';

function setup() {
    page=true;
    theForm = self.document.forms[0];
    isLoaded = true;
    //Make the exception hidden by default
    setVisibility('stackTrace', 'hidden');
    setVisibility('exceptionNavigation', 'hidden');
    setVisibility('normalNavigation', 'visible');
}

function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp";
	openwin_help(helplocation);
}

function return_to_page() {
	if(parent.main)
		document.location=history.back();
	else
		document.close();
}

function toggleExceptionVisibility() {
    toggleVisibility('stackTrace');
    toggleVisibility('exceptionNavigation');
    toggleVisibility('normalNavigation');
}

</script>
</head>
<body class="main" onLoad="setup();">
<form method="post">
	<input type="hidden" name="theAction">
</form>

<center>
<table border=0 cellpadding=0 cellspacing=0 width=80%>
	<tr>
		<% if(!disableBackLink){ %>
			<td width=1%><a href="#" onClick="toggleExceptionVisibility()"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/accessdenied-top-left.gif" width=41 height=41 alt="" border="0"></a></td>
		<%} else { %>
			<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
		<%} %>
		<td class="errorBanner" width=98%>Access Denied</td>
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-right.gif" width=41 height=41 alt="" border="0"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="channelContentLarge"><%= errorMessage %></td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td colspan=3>&nbsp;</td>
	</tr>
<%  if (isAdminGroupMemberList) { %>
	<tr>
		<td>&nbsp;</td>
		<td class="channelContent">If you need access to this item, please contact one of the following 'Space Administrators' for this workspace:</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="channelContent">
            <pnet-xml:transform name="adminGroupMembers" scope="page" stylesheet="/security/xsl/access-denied-admins.xsl" >
            <pnet-xml:property name="JSPRootURL" value="<%= SessionManager.getJSPRootURL()%>" />
            </pnet-xml:transform>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td colspan=3>&nbsp;</td>
	</tr>
<%  } %>
</table>
<table id="normalNavigation" class="shown" border=0 cellpadding=0 cellspacing=0 width=80%>
	<tr>
		<td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
			<%if(!disableBackLink){ %>
				<td class="errorBanner" width=98% align=right><a href="javascript:history.back();" class="errorLink">Return</a></td>
				<td><a href="javascript:history.back();" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/accessdenied-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
			<%} else {%>
				<td class="errorBanner" width=98% align=right></td>
				<td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-right.gif" width=41 height=41 alt="" border="0"></td>
			<%} %>
	</tr>
</table>
<table id="stackTrace" class="hidden" border=0 cellpadding="4" cellspacing="0">
    <tr><td>
        <div class="channelContentLarge">Stack Trace<BR></div>
        <pre><%=getStackTrace(exception)%></pre>
    </td></tr>
</table>
<table id="exceptionNavigation" class="hidden" border=0 cellpadding=0 cellspacing=0 width=80%>
	<tr>
		<td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
		<td class="errorBanner" width=98% align=right><a href="javascript:history.back();" class="errorLink"><%=PropertyProvider.get("prm.project.main.return.label") %></a></td>
		<td><a href="javascript:history.back();" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/accessdenied-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
	</tr>
</table>
</center>

<%@ include file="/help/include_outside/footer.jsp" %>

<!-- The Exception is:
<%= ((exception == null) ? "null" : exception.toString()) %>
 -->

</body>
</html>
<%!
	String getStackTrace(Throwable t) {
		java.io.StringWriter writer = new java.io.StringWriter();
		t.printStackTrace(new java.io.PrintWriter(writer));
		return writer.toString();
	}
%>
