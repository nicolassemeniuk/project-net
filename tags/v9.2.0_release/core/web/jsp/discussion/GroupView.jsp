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
|   GroupView.jsp
|   This page provides a frameset for a discussion group.  The top
|   frame provides a list of posts in the group.  The bottom frame
|   is utilized to display posts and post information.  The bottom
|   is created but not populated by this jsp page.
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Discussion - Frameset for thread list and view post"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.DiscussionGroupBean,
            net.project.space.Space,
            net.project.base.Module,
            org.apache.log4j.Logger,
            net.project.security.*"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session" />

<jsp:setProperty name="discussion" property="*" />

<%
Logger.getLogger("GroupView").debug("Welcome to the GroupView page!");
String idtype = request.getParameter("idtype");
String docName = request.getParameter("docName");
String docModule = request.getParameter("docModule");
// configure the discussion group bean
discussion.setSpace(user.getCurrentSpace());
discussion.setUser(user);

if ( docName != null &&  !docName.trim().equals("")) {
	discussion.setName(docName);
}

//if ((idtype == null) || (!idtype.equals("post"))) {
    discussion.setID(request.getParameter("id"));
    discussion.load();
    // load group properties (name/description/charter by ID)
	discussion.loadGroup(request.getParameter("id"));
//} else {
//    postId = discussion.getID();
//    discussion.loadFromPostID();

    // Note: we must do an explicit discussion group id security check here since
    // we only validated that the user has permission to view the post
    if (!securityProvider.isActionAllowed(discussion.getID(), Integer.toString(Module.DISCUSSION), Action.VIEW))
        throw new AuthorizationFailedException(PropertyProvider.get("prm.discussion.security.validationfailed.message"));
//}

if (request.getParameter("noHistory") != null)
    session.setAttribute("Discussion_ShowHistory", new Boolean(false));
else
    session.setAttribute("Discussion_ShowHistory", new Boolean(true));

Boolean showHistory = (Boolean)request.getSession().getValue("Discussion_ShowHistory");
if ((showHistory != null) && (showHistory.booleanValue() == true)) { %>
		<history:history displayHere="false">
			<history:module display='<%=PropertyProvider.get("prm.discussion.main.module.history")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/discussion/Main.jsp"%>'
					queryString='<%="module=" + Module.DISCUSSION +"&action="+Action.VIEW%>' />
		</history:history>
<%	}

final String postId = request.getParameter("postid");
if ((postId != null) && (!postId.equals(""))) {
	response.sendRedirect(SessionManager.getJSPRootURL() + "/discussion/ThreadList.jsp?postid="+postId+"&action=" + Action.VIEW + "&module=" + Module.DISCUSSION);
	return;
}

if (true) {
	response.sendRedirect(SessionManager.getJSPRootURL() + "/discussion/ThreadList.jsp?module=" + Module.DISCUSSION + "&action=" + Action.VIEW + "&id=" + discussion.getID() + "&postid=" + (postId != null ? postId : "")+"&docModule=" + (docModule != null ? docModule : ""));
	return;
}
%>

<%--
	Note: the id checked could potentially be a discussion group id or a post id.  In either
	scenario it will have been set in the discussion object so we can still use its getID
	method --%>
<security:verifyAccess action="view"
					   module="<%=Module.DISCUSSION%>"
					   objectID="<%=discussion.getID()%>"/>

<html>
<head>
<meta http-equiv="expires" content="0">

<%-- Import Javascript --%>
<template:getSpaceJS />
<script language="javascript">
   var theForm;
   var isLoaded = false;


function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    isLoaded = true;
    theForm = self.document.forms[0];
}

</script>
<title><%=PropertyProvider.get("prm.discussion.groupviewpage.title")%></title>
</head>
<form method="post" action="<%= SessionManager.getJSPRootURL() %>/toolbar/ProcessAction.jsp">
<input type="hidden" name="theAction">

<frameset ROWS="40%,*" cols="*" BORDER="4" FRAMEBOARDER="no" onLoad="setup();">
    <frame SRC="<%=SessionManager.getJSPRootURL()%>/discussion/ThreadList.jsp?module=<%=Module.DISCUSSION %>&action=<%= Action.VIEW %>&id=<%= discussion.getID() %>&postid=<%= postId != null ? postId :"" %>&docModule=<%= docModule != null ? docModule : "" %>" name="upper_frame" bordercolor="#DDDDDD">
    <frame SRC="<%=SessionManager.getJSPRootURL()%>/discussion/blank.jsp?module=<%=Module.DISCUSSION %>&action=<%= Action.VIEW %>&id=<%= discussion.getID() %>" name="lower_frame" bordercolor="#DDDDDD">
</frameset>
</form>
</html>

