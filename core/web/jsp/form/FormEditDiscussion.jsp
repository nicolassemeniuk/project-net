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
|   $Revision: 20051 $
|       $Date: 2009-10-02 13:36:06 -0300 (vie, 02 oct 2009) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Tab for discussions on a form item"
    language="java"
    errorPage="/errors.jsp"
    import="org.apache.log4j.Logger,
            java.util.ArrayList,
            net.project.base.Module,
            net.project.discussion.DiscussionGroup,
            net.project.discussion.DiscussionGroupBean,
            net.project.discussion.DiscussionManager,
            net.project.document.IContainerObject,
            net.project.form.Form,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.util.JSPUtils"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<jsp:useBean id="discManager" class="net.project.discussion.DiscussionManager" scope="request"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session"/>
<jsp:useBean id="form" class="net.project.form.Form" scope="session"/>
<%
String formDataID = request.getParameter("formDataID");
String postid = request.getParameter("postid");
String postListParam = "";

request.getSession().putValue("Discussion_ShowHistory", new Boolean(true));

if (!JSPUtils.isEmpty(formDataID)) {
    //Get to the discussion group from the document manager
    discManager.setUser(user);
    discManager.setObjectId(formDataID);
    ArrayList list = discManager.getGroupList();

    //Check to make sure we found a discussion group, if we didn't, create one.
    if (list.size() == 0) {
        //There aren't any discussion groups -- create one.
        discussion.clear();
        discussion.setSpace(user.getCurrentSpace());
        discussion.setObjectID(formDataID);
        discussion.setUser(user);
        discussion.setName(form.getName());
        discussion.setDescription("Form Discussion");

        //Store the discussion in the database
        discussion.store();
        discussion.load();
    } else {
        //Create the discussion bean based on the first post in the form.
        discussion.setUser(user);
        discussion.setName(form.getName());
        discussion.setID(((DiscussionGroup)list.get(0)).getID());
        discussion.load();
        discussion.setObjectID(formDataID);
        discussion.setDescription(((DiscussionGroup)list.get(0)).getDescription());
    }

    postListParam = "&id=" + discussion.getID()+"&formId="+ formDataID;
    Logger.getLogger(this.getClass()).error("postListParam=" + postListParam);

} else if (!JSPUtils.isEmpty(postid)) {
    //TODO -- we need to do a security check here.

    postListParam = "&postid=" + postid+"&formId="+ formDataID;
}
%>
<template:getSpaceCSS />
<template:getSpaceJS/>
<template:import type="javascript" src="/src/window_functions.js" />
<script language="javascript">
function create() {
    var theLocation = "<%=SessionManager.getJSPRootURL()%>/discussion/PostNew.jsp?module=<%=Module.DISCUSSION%>&action=<%=Action.CREATE%>&id=<%=discussion.getID()%>";
    create_window = window.open(theLocation, "new_window", "height=400,width=550,resizable");
    create_window.focus();
}
function reset() {
    self.location = "<%=SessionManager.getJSPRootURL()%>/form/FormEditDiscussion.jsp?module=<%=Module.DISCUSSION%>&action=<%=Action.VIEW%>&formDataID=<%=formDataID%>";
}
function search() {
    search_window = window.open("<%=SessionManager.getJSPRootURL() %>/discussion/SearchFrameSet.jsp?module=<%=Module.DISCUSSION%>&action=<%=Action.VIEW%>&id=<%=discussion.getID()%>", "search_window", "height=500,width=480,resizable");
    search_window.focus();
}
function help() {
    var helplocation = "<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=form_edit&section=discussion";
    openwin_help(helplocation);
}
function main_toolbar_logout() {
	self.location='<%=SessionManager.getJSPRootURL()%>/Logout.jsp';
}
</script>
</head>
<frameset ROWS="100%,*" cols="*" BORDER="0">
    <frame SRC="<%=SessionManager.getJSPRootURL()%>/discussion/ThreadList.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %><%=postListParam%>" name="upper_frame" bordercolor="#DDDDDD">
    <frame SRC="<%=SessionManager.getJSPRootURL()%>/discussion/blank.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %><%=postListParam%>" name="lower_frame" bordercolor="#DDDDDD">
</frameset>
</html>
