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
            net.project.discussion.Post,
            net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="discussions" class="net.project.discussion.DiscussionManager" scope="session" />
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session" />
<jsp:useBean id="post" class="net.project.discussion.PostBean" scope="session" />

<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.DISCUSSION%>"/>
<%
    Post postToDelete = discussion.getPost(request.getParameter("objectID"));
    String errorMessage = "";
    String docModule = request.getParameter("docModule");
    //A final custom security check to make sure someone other than the space
    //administrator isn't trying to use deleteType="recursive"
    String deleteType = (request.getParameter("deleteType") == null ?
        "single" : request.getParameter("deleteType"));
    if ((!SessionManager.getUser().isSpaceAdministrator()) && (deleteType.equals("recursive"))) {
            //Unable to delete post.  Only space administrators can delete all
            //replies to a post automatically.
            errorMessage = PropertyProvider.get("prm.discussion.deletepost.onlyadminsdeletethreads.message");
    } else if ((!SessionManager.getUser().isSpaceAdministrator()) &&
         (!postToDelete.getAuthorID().equals(SessionManager.getUser().getID()))) {
        //Unable to delete post.  Only space administrators can delete posts
        //written by other users.
        errorMessage = PropertyProvider.get("prm.discussion.deletepost.onlydeleteyourownposts.message");
    } else {
        //Remove the post
        if (deleteType.equals("single")) {
            discussion.removePost(postToDelete, false);
        } else {
            discussion.removePost(postToDelete, true);
        }
    }

    //Determine if this is a popup window, by default true
    String notPopup = request.getParameter("notPopup");
    notPopup = (notPopup == null ? "false" : "true");

    //Determine the location to which we are redirecting.
    String redirectLocation = SessionManager.getJSPRootURL() +
        "/discussion/ThreadList.jsp?module="+Module.DISCUSSION+"&action="+
        Action.VIEW+"&id="+discussion.getID()+"&postid="+
        discussion.getCurrentPostID()+"&docModule="+docModule;
    if (!Validator.isBlankOrNull(errorMessage)) {
        redirectLocation = redirectLocation + "&errorMessage=" + errorMessage;
    }

    //Set the post we should be looking at
    post.setReference(discussion.getPost(discussion.getCurrentPostID()));

    //Different handling depending on whether this is a popup window or not.
    if (notPopup.equals("true")) {
        response.sendRedirect(redirectLocation);
    } else {
        //This is a popup window, send the redirect through javascript and close the window
%>
<SCRIPT language="javascript">
window.open("", "new_window").close();
this.document.location="<%=redirectLocation%>";
</SCRIPT>
<% } %>
