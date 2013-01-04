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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Domain User List Processing.  Emits no output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.AuthorizationFailedException,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.admin.RegistrationBean,
            net.project.base.DefaultDirectory,
	    	net.project.resource.PersonListBean,
            net.project.security.Action,
            net.project.security.User"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="userList" class="net.project.resource.PersonListBean" scope="session" />


<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
    String theAction = request.getParameter("theAction");
    String userID = request.getParameter("selected");
		
	String orgLink = "/admin/domain/DomainUserList.jsp?module=" + Module.APPLICATION_SPACE+"&selected="+request.getParameter("userDomainFilter");;
	
    if (theAction != null && theAction.equals("modify")) {
        // Modify a user; go to first profile page
        request.setAttribute("action", String.valueOf(Action.MODIFY));
		request.setAttribute("module" , String.valueOf(net.project.base.Module.APPLICATION_SPACE));
		//Need to keep track of the the originating page link since the profile pages have multiple entry points
		session.removeAttribute("orgLink");
		session.setAttribute("orgLink", orgLink);
        pageContext.forward("/admin/profile/ProfileName.jsp?userID=" + userID);

    } else if (theAction != null && theAction.equals("remove")) {
        // Disable a user
        User deletedUser = new User(userID);
		DefaultDirectory.changePersonStatus (userID,"Disabled");
        //For now,let's not set a user's status to D when they are disabled using the top
        //nav bar.  Right now they can still be reactivated, but they still have that status.
		//deletedUser.remove();
		response.sendRedirect(SessionManager.getJSPRootURL() + orgLink);

    } else if (theAction != null && theAction.equals("search")) {

        // set the display mode to search since that is the action we are performing
	userList.setDisplayMode("search");
	// set filter state
	userList.setFilter (request.getParameter("searchFilter"));
	userList.setUserStatusFilter (request.getParameterValues("userStatusFilter"));
	userList.setUserDomainFilter (request.getParameterValues("userDomainFilter"));
	userList.setSortOrder (request.getParameter("sortOrder"));
		
	response.sendRedirect (SessionManager.getJSPRootURL() + orgLink);

    } else if (theAction != null && theAction.equals("register")){
		registration.clear();
		registration.setID(request.getParameter("selected"));
		registration.load();
		registration.generateVerificationCode();
		registration.logUserHistory();
   		registration.sendVerificationEmail();
       	registration.setUserStored();
		registration.store();
		response.sendRedirect(SessionManager.getJSPRootURL()+orgLink);

    } else if (theAction != null && theAction.length() == 0) {
        throw new net.project.base.PnetException("Missing action in DomainUserListProcessing.jsp.");

	} else {
        throw new net.project.base.PnetException("Unknown action '" + theAction + "' in DomainUserListProcessing.jsp");
    }
%>
    
