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
    info="New post processing.. omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.*, net.project.security.*,
            net.project.util.Validator,
            net.project.base.PnetException"
%>

<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session" />
<jsp:useBean id="post" class="net.project.discussion.PostBean" scope="session" />
<jsp:useBean id="newPost" class="net.project.discussion.Post" scope="request" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
// Make sure a security check has been passed to create a new post
int module = securityProvider.getCheckedModuleID();
String id = securityProvider.getCheckedObjectID();
int action = securityProvider.getCheckedActionID();
if ((module != net.project.base.Module.DISCUSSION) 
    || (action != net.project.security.Action.CREATE)
    || (!id.equals(discussion.getID()))) 
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.discussion.security.validationfailed.message"));

// validate the parent id
String reply = request.getParameter("reply");
String docModule = request.getParameter("docModule");
if ((reply != null) && (reply.length() > 0) && (Integer.parseInt(reply) < 0))
    throw new net.project.base.PnetException("Invalid parent id");
%>
<jsp:setProperty name="newPost" property="*" />
<jsp:setProperty name="newPost" property="parentid" param="reply" />
<%
if ((newPost.getSubject() == null) || (newPost.getSubject().length() == 0)) 
    throw new PnetException("New Post must include a subject");

newPost.setDiscussionGroup(discussion);    
discussion.newPost(newPost);
post.setReference(discussion.getPost(newPost.getID()));
%>
<SCRIPT language="javascript">
opener.document.location= "ThreadList.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>&id=<%= discussion.getID() %>&postid=<%= newPost.getID() %>&docModule=<%=docModule%>";
window.open("", "new_window").close();
</SCRIPT>
