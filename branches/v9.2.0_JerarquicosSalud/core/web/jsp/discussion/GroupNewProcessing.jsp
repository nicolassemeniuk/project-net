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
|   $Revision: 20065 $
|       $Date: 2009-10-06 13:28:30 -0300 (mar, 06 oct 2009) $
|
|   new discussion group processing page
|   
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="New discussion group processing.. omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.*, net.project.security.*" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="request" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:setProperty name="discussion" property="*" />

<%
	// bfd - 2994 issue
	if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		discussion.setDescription("");
	}	
	if ((request.getParameter("charter") == null) || (request.getParameter("charter").equals(""))) {
		discussion.setCharter("");
	}
%>

<%
// Make sure a security check has been passed to create a new discussion group
int module = securityProvider.getCheckedModuleID();
int action = securityProvider.getCheckedActionID();
String id = securityProvider.getCheckedObjectID();

if (id.length() == 0) 
    {
    if ((module != net.project.base.Module.DISCUSSION) 
            || (action != net.project.security.Action.CREATE)) 
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.discussion.security.validationfailed.message"));
    }
else 
    {
    if ((module != net.project.base.Module.DISCUSSION) 
        || (action != net.project.security.Action.MODIFY)) 
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.discussion.security.validationfailed.message"));
    }

// validate request data
if ((discussion.getName() == null) || (discussion.getName().length() == 0)) 
    throw new net.project.base.PnetException("Discussion group must have a name");

// store the new discussion group
discussion.setSpace(user.getCurrentSpace());
discussion.setUser(user);
discussion.store();

// set attributes for security check
request.setAttribute("module", Integer.toString(net.project.base.Module.DISCUSSION));
request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
request.setAttribute("id", "");
//Avinash:------------------------------------------------------------------------
	ServletSecurityProvider.setAndCheckValues(request);
//Avinash:------------------------------------------------------------------------
response.sendRedirect(SessionManager.getJSPRootURL()+ "/discussion/Main.jsp?module="+net.project.base.Module.DISCUSSION);
%>

