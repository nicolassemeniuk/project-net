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
			net.project.database.DBBean,
			net.project.license.LicenseException,
			net.project.license.PersonLicense,
			net.project.license.LicenseResult,
			net.project.license.LicenseResultCode,
			net.project.resource.Person,
		    net.project.resource.PersonListBean,
			net.project.security.Action,
            net.project.security.User"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="userList" class="net.project.resource.PersonListBean" scope="session" />
<jsp:useBean id="dbean" class="net.project.database.DBBean" scope="session" />
<jsp:useBean id="person" class="net.project.resource.Person" scope="session" />
<jsp:useBean id="license" class="net.project.license.License" scope="session" />
<jsp:useBean id="licenseManager" class="net.project.license.LicenseManager" scope="session"/>

<security:verifyAccess action="modify"
                       module="<%=net.project.base.Module.APPLICATION_SPACE%>" />

<%
    String theAction = request.getParameter("theAction");
    String userID = request.getParameter("selected");
	String orgLink = "/admin/license/LicenseDetailView.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW;
	session.setAttribute("addUserMessage", "");
	session.setAttribute("assignResponsibleUserMessage", "");
	
    if (theAction != null && theAction.equals("modify")) {
        // Modify a user; go to first profile page
        request.setAttribute("action", String.valueOf(Action.MODIFY));
		request.setAttribute("module" , String.valueOf(net.project.base.Module.APPLICATION_SPACE));
		//Need to keep track of the the originating page link since the profile pages have multiple entry points
		session.removeAttribute("orgLink");
		session.setAttribute("orgLink", orgLink);
        pageContext.forward("/admin/profile/ProfileLicense.jsp?userID=" + userID );
		
	   

    } else if (theAction != null && theAction.equals("remove")) {
        // Disable a user
        User deletedUser = new User(userID);
		DefaultDirectory.changePersonStatus (userID,"Disabled");
        //For now, let's not set a user's status to D when they are disabled using the top
        //nav bar.  Right now they can still be reactivated, but they still have that status.
		//deletedUser.remove();
		response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/UserList.jsp?module=" + net.project.base.Module.APPLICATION_SPACE);

    } else if (theAction != null && theAction.equals("search")) {

        // set the display mode to search since that is the action we are performing
	userList.setDisplayMode("search");
	// set filter state
	userList.setFilter (request.getParameter("searchFilter"));
	userList.setUserStatusFilter (request.getParameterValues("userStatusFilter"));
	userList.setUserDomainFilter (request.getParameterValues("userDomainFilter"));
	userList.setSortOrder (request.getParameter("sortOrder"));

	response.sendRedirect (SessionManager.getJSPRootURL() + "/admin/license/UserList.jsp?module=240");
	
	} else if (theAction != null && theAction.equals("register")){
	  	registration.clear();
		registration.setID(request.getParameter("selected"));
		registration.load();
		registration.generateVerificationCode();
		registration.logUserHistory();
   		registration.sendVerificationEmail();
       	registration.setUserStored();
		registration.store();
		response.sendRedirect(SessionManager.getJSPRootURL()+"/admin/UserList.jsp?module=240");

    } else if (theAction != null && theAction.equals("associate")){
	  
		LicenseResult licenseResult = licenseManager.checkLicenseAvailableForUseForDisplayKey(license.getKey().toDisplayString());
		session.removeAttribute("addUserMessage");
		if(licenseResult.getCode().equals(LicenseResultCode.VALID)){
			person.setID(request.getParameter("selected"));
			person.load();
			net.project.license.create.LicenseCreator licenseCreator = new net.project.license.create.LicenseCreator();
			licenseCreator.useExistingLicenseForKey((String)session.getAttribute("licenseKey"));
			try {
				licenseCreator.commitLicense(person, true);
				session.setAttribute("addUserMessage", "User " + person.getUserName() + " has been successfully associated with this license.");
			} catch (LicenseException le) {
				session.setAttribute("addUserMessage", "User " + person.getUserName() + " could not be associated with this license. \n" +
				"The error reported is : " + le.getMessage());
			}
			
		} else {
			session.setAttribute("addUserMessage", licenseResult.getMessage() + "\n" + " Failed to associate user with this license.");
		} 
		response.sendRedirect(SessionManager.getJSPRootURL()+"/admin/license/UserList.jsp?module=240");

	} else if (theAction != null && theAction.equals("assignResponsibility")){
	  	session.removeAttribute("assignResponsibleUserMessage");
		person.setID(request.getParameter("selected"));
		person.load();
		license.updateResponsiblePerson(person);
		session.setAttribute("assignResponsibleUserMessage", "User " + person.getUserName() + " is now the responsible user for this license.");
		response.sendRedirect(SessionManager.getJSPRootURL()+"/admin/license/UserList.jsp?module=240");
		
	} else if (theAction != null && theAction.equals("dissociate")){
		//System.out.println("UserListProcessing : userID :" + userID);
		response.sendRedirect(SessionManager.getJSPRootURL()+"/admin/license/DissociatePersonFromLicense.jsp?userID=" + userID + "&module=" + net.project.base.Module.APPLICATION_SPACE);

    } else if (theAction != null && theAction.length() == 0) {
        throw new net.project.base.PnetException("Missing action in UserListProcessing.jsp.");

	} else {
        throw new net.project.base.PnetException("Unknown action '" + theAction + "' in UserListProcessing.jsp");
    }
%>
    
