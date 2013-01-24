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
    info="User List Processing.  Emits no output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.AuthorizationFailedException,
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

<security:verifyAccess action="modify"
                       module="<%=net.project.base.Module.APPLICATION_SPACE%>"/> 

<%
    String theAction = request.getParameter("theAction");
    String userID = request.getParameter("selected");
	String orgLink = "/admin/UserList.jsp?module=" + Module.APPLICATION_SPACE;
	
    if (theAction != null && theAction.equals("modify")) {
        // Modify a user; go to first profile page
        request.setAttribute("action", String.valueOf(Action.MODIFY));
		request.setAttribute("module" , String.valueOf(net.project.base.Module.APPLICATION_SPACE));
		//Need to keep track of the the originating page link since the profile pages have multiple entry points
		
		String strlink = (String) session.getAttribute("orgLink");
		if(strlink == null)
			session.setAttribute("orgLink", orgLink);
			
        pageContext.forward("/admin/profile/ProfileName.jsp?userID=" + userID);

    } else if (theAction != null && theAction.equals("remove")) {
        // Delete a user - Yes - permanently DELETE user
        //User deletedUser = new User (userID);
		DefaultDirectory.changePersonStatus (userID, net.project.resource.PersonStatus.DELETED.getID());
		response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/UserList.jsp?module=" + net.project.base.Module.APPLICATION_SPACE);

    } else if (theAction != null && theAction.equals("disable")) {
        // Disable user
		DefaultDirectory.changePersonStatus (userID, net.project.resource.PersonStatus.DISABLED.getID());
		response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/UserList.jsp?module=" + net.project.base.Module.APPLICATION_SPACE);

    } else if (theAction != null && theAction.equals("search")) {

        // set the display mode to search since that is the action we are performing
	userList.setDisplayMode("search");
	// set filter state
	userList.setFilter (request.getParameter("searchFilter"));
	userList.setUserStatusFilter (request.getParameterValues("userStatusFilter"));
	userList.setUserDomainFilter (request.getParameterValues("userDomainFilter"));
    userList.setLicenseFilter(request.getParameterValues("userLicenseFilter"));
	userList.setSortOrder (request.getParameter("sortOrder"));

	response.sendRedirect (SessionManager.getJSPRootURL() + "/admin/UserList.jsp?module=240");

    }else if (theAction != null && theAction.equals("register")){
		registration.clear();
		registration.setID(request.getParameter("selected"));
		registration.load();
		registration.generateVerificationCode();
		registration.logUserHistory();
   		registration.sendVerificationEmail();
       	registration.setUserStored();
		registration.store();
		response.sendRedirect(SessionManager.getJSPRootURL()+"/admin/UserList.jsp?module=240");

    } else if (theAction != null && theAction.length() == 0) {
        throw new net.project.base.PnetException("Missing action in UserListProcessing.jsp.");

	} else {
        throw new net.project.base.PnetException("Unknown action '" + theAction + "' in UserListProcessing.jsp");
    }
%>
    
