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
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Discussion"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.discussion.DiscussionGroup,
            net.project.security.Action,
            net.project.base.Module,
            net.project.security.SessionManager,
            net.project.util.HttpUtils,
            net.project.discussion.Post,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="discussions" class="net.project.discussion.DiscussionManager" scope="session" />
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session" />

<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.DISCUSSION%>"/>

<%
    String docModule = request.getParameter("docModule");
    //More custom security stuff.  You will only be allowed to see this page
    //if you are a space administrator.  Otherwise, you'd be able to delete
    //posts that didn't belong to you.
    if (!user.isSpaceAdministrator()) {
        response.sendRedirect(SessionManager.getJSPRootURL()+
            "/discussion/RemovePostProcessing.jsp?deleteType=single&"+
            HttpUtils.getRedirectParameterString(request));
    }
%>

<%
    //If a post doesn't have any replies, they don't need to see this screen,
    //they are just going to delete a single post.
    Post post = discussion.getPost(request.getParameter("objectID"));
    if (!post.hasReplies()) {
        response.sendRedirect(SessionManager.getJSPRootURL()+
            "/discussion/RemovePostProcessing.jsp?deleteType=single&"+
            HttpUtils.getRedirectParameterString(request));
    }
%>

<html>
<head>
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="JavaScript">
var theForm;

function setup() {
    theForm = self.document.forms[0];
}

function cancel() {
    window.close();
}

function submit() {
    theForm.submit();
}
</script>
</head>
<body onLoad="setup();">

<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr class="actionBar">
	<td width="1%" class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
	<td align="left" class="actionBar" nowrap>&nbsp;<%=PropertyProvider.get("prm.discussion.deletepost.options.name")%></td>
	<td width="1%" align=right class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
</tr>
</table>

<form name="removePost" action="RemovePostProcessing.jsp" method="POST" target="upper_frame" onsubmit="return validate();">
<input type="hidden" name="module" value="<%=Module.DISCUSSION%>">
<input type="hidden" name="action" value="<%=Action.MODIFY%>">
<input type="hidden" name="objectID" value="<%=request.getParameter("objectID")%>">
<input type="hidden" name="docModule" value="<%=docModule%>">
<input type="radio" name="deleteType" value="single" checked><span class="fieldRequired"><%=PropertyProvider.get("prm.discussion.deletepost.deletesinglepost.title")%></span><br>
<span class="tableContent"><%=PropertyProvider.get("prm.discussion.deletepost.deletesinglepost.description")%></span><br>
<input type="radio" name="deleteType" value="recursive"><span class="fieldRequired"><%=PropertyProvider.get("prm.discussion.deletepost.deletethread.name")%></span><br>
<span class="tableContent"><%=PropertyProvider.get("prm.discussion.deletepost.deletethread.description")%></span><br>
<br>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="submit" />
	</tb:band>
</tb:toolbar>

</form>
<template:getSpaceJS />
</body>
</html>
