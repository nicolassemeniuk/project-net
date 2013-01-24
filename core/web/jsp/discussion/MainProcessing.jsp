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
    info="Discussion Main Page Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.*, net.project.security.*" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroup" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="discussions" class="net.project.discussion.DiscussionManager" scope="session" />
<%
// Make sure a security check has been passed to view a discussion group
int module = securityProvider.getCheckedModuleID();
String id = securityProvider.getCheckedObjectID();
int action = securityProvider.getCheckedActionID();
if ((id.length() == 0) || (module != net.project.base.Module.DISCUSSION))
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.discussion.security.validationfailed.message"));	
%>
<%
String theAction = request.getParameter("theAction");
if(theAction.equals("security"))
    {
    // we don't need to validate the id because we will use the one that passed the security
    if (action != net.project.security.Action.MODIFY_PERMISSIONS)
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.discussion.security.validationfailed.message"));

    session.putValue("objectID", request.getParameter("id"));
    request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
    request.setAttribute("module", Integer.toString(net.project.base.Module.SECURITY));
//Avinash:------------------------------------------------------------------------
	ServletSecurityProvider.setAndCheckValues(request);
//Avinash:------------------------------------------------------------------------
    pageContext.forward ("/security/SecurityMain.jsp");
    }
else if (theAction.equals("remove")) 
    {
    // we don't need to validate the id because we will use the one that passed the security
    if (action != net.project.security.Action.DELETE)
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.discussion.security.validationfailed.message"));
    // configure the discussion group bean
    //Ivana: bug 4461 added property loading of group that is to be deleted
    discussion = discussions.getDiscussionGroup(id);
    discussion.setSpace(user.getCurrentSpace());
    discussion.setUser(user);
    discussion.setID(id);
    //discussion.load();
    discussion.remove();
    // add attributes for Main.jsp security check
    request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
    request.setAttribute("id", "");
//Avinash:------------------------------------------------------------------------
	ServletSecurityProvider.setAndCheckValues(request);
//Avinash:------------------------------------------------------------------------
    pageContext.forward("/discussion/Main.jsp");
    }

%>