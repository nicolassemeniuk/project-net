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
    info="Display a documents discussion group" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*, 
    net.project.discussion.*,
    net.project.security.*,
    net.project.base.property.PropertyProvider,
    net.project.base.Module"

%>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="document" class="net.project.document.DocumentBean" /> 
<jsp:useBean id="discManager" class="net.project.discussion.DiscussionManager" scope="request" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
// security check
int module = securityProvider.getCheckedModuleID();
int action = securityProvider.getCheckedActionID();
String id = securityProvider.getCheckedObjectID();

document.setID ( id );
document.setUser ( docManager.getUser() );	
if(module == Module.TRASHCAN)
    document.setListDeleted();
else
    document.unSetListDeleted();

document.load();

if ((module != docManager.getModuleFromContainerID(document.getContainerID())) || (action != net.project.security.Action.VIEW))
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.document.discuss.authorizationfailed.message"));
%>                  
<%
// get the name for the discussion group
String name = document.getName();

discManager.setUser(user);
discManager.setObjectId(document.getID());
java.util.ArrayList list = discManager.getGroupList();

DiscussionGroup discussion = (DiscussionGroup)list.get(0);
if (request.getParameter("showHistory") != null) { 
    response.sendRedirect(SessionManager.getJSPRootURL() + "/discussion/GroupView.jsp?"+
                          "module=" + net.project.base.Module.DISCUSSION + 
                          "&action=" + net.project.security.Action.VIEW +
                          "&docModule=" + module +
                          "&id=" + discussion.getID()+"&docName="+java.net.URLEncoder.encode(document.getName()));
} else {
    response.sendRedirect(SessionManager.getJSPRootURL() + "/discussion/GroupView.jsp?"+
                          "module=" + net.project.base.Module.DISCUSSION + 
                          "&action=" + net.project.security.Action.VIEW +
                          "&docModule=" + module +
                          "&id=" + discussion.getID() +
                          "&noHistory=1"+"&docName="+java.net.URLEncoder.encode(document.getName()));
}
%>