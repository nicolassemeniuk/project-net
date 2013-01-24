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
| Controller for navigating to directory-specific profile login
| page
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Profile Login Controller; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.property.PropertyProvider,
            net.project.persistence.PersistenceException"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
    // Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
    String theAction = request.getParameter("theAction");

    if (fromPage != null && fromPage.equals("profileLogin")) {
        // We were navigated to from the "profileLogin" page
        // We can assume theAction is valid
        String nextPage = request.getParameter("nextPage");
        String userID = request.getParameter("userID");

        if (theAction != null && theAction.equals("submit")) {
            // User submitted profile login
            // Reload user record (which is effectively the thing we just stored)
            user.load();
        }
        
        if (nextPage != null && nextPage.trim().length() > 0) {
            // Go to the requested page
            response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + 
                nextPage + "?module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY + "&userID=" + userID);

        } else {
            // Go to default page
            String orgLink = (String) session.getAttribute("orgLink");
            response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + orgLink);

        }

    } else {
        // We were not submitted to from the "profileLogin" page
        // Likely that we are forwarded to another tab

        // Currently, only allow actual app admin to make changes
        if (!user.isApplicationAdministrator()) {
            throw new net.project.security.AuthorizationFailedException("Failed security validation: This functionality is only available to application administrators.");
        }

        //Grab the userID; this is the user to be edited
        String userID = request.getParameter("userID");
        if (userID == null || userID.length() == 0) {
            throw new net.project.base.PnetException("Missing parameter in ProfileLoginController.jsp");
        }

        // Clear out registration bean to ensure we're editing the correct user
        registration.clear();
        //Load pre-existing information for this user
        registration.setID(userID);
        try {
            registration.load();
        } catch (PersistenceException e) {
            //If we couldn't load the record from directory store, try to load
            //it locally.  This is necessary for times when the LDAP username
            //is incorrect.
            registration.loadLocalInformation();
            //Show an error on the resultant page showing that we couldn't load
            //the information from the directory store.
            request.setAttribute("error",
                PropertyProvider.get("prm.application.userlist.modifyuser.login.directoryloaderror.message"));
        }
    
        // Go to appropriate profile login page
        navigateProfileLogin(request, response, pageContext, registration);
    }
%>

<%!
    void navigateProfileLogin(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.jsp.PageContext pageContext, net.project.admin.RegistrationBean registration)
            throws javax.servlet.ServletException, java.io.IOException, net.project.persistence.PersistenceException, net.project.base.directory.DirectoryException {

        // Get domain for id
        net.project.security.domain.UserDomain domain = registration.getUserDomain();
        // Determine the appropriate profile login page from the current directory provider type
        net.project.base.directory.DirectoryConfigurator configurator = domain.getDirectoryProviderType().newConfigurator();
        configurator.serviceProfileUpdateAdmin(request, response, pageContext, registration);

    }
%>

