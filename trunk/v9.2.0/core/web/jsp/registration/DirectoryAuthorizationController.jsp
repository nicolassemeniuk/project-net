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
|   $Revision: 20348 $
|       $Date: 2010-01-29 11:23:05 -0300 (vie, 29 ene 2010) $
|
| Controller for displaying Directory Authorization pages
| and navigating to next part of the Registration Wizard
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Registration Directory Authorization Controller; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
    		net.project.util.HTMLUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%
    // Redirect to start of process if registration not started
    if (!registration.isStarted()) {
        response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/registration/Register.jsp");
        return;
    }
	
	request.setCharacterEncoding("UTF-8");
    // Capture the direction that registration is moving in
    String direction = request.getParameter("direction");
    
    // Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
    String theAction = request.getParameter("theAction");

    if (fromPage != null && fromPage.equals("authorizationPage")) {
    	 fromPage = HTMLUtils.escape(fromPage).replaceAll("'", "&acute;");
        // We were submitted to from the end of the directory authorization process
        // We can assume theAction is valid

        if (theAction != null && theAction.equals("next")) {
        	theAction = HTMLUtils.escape(theAction).replaceAll("'", "&acute;");
            boolean isInfoValid = false;

            // Check to see if email and username are available
            net.project.admin.RegistrationBean.RegistrationResult result = 
                registration.checkAvailability();

    		if (!result.isSuccess()) {
                // Some condition preventing registration from continuing
                // Go to directory-specific controller to display error
                request.setAttribute("registrationResult", result);
                navigateDirectoryAuthorization(request, response, pageContext, registration);

            } else {
                if (!registration.isRegisteringUserLicensed()) {
                    // OK to proceed to next step
                    pageContext.forward("/registration/LicenseController.jsp");
                } else {
                    pageContext.forward("/registration/ProfileController.jsp");
                }
            }

        } else if (theAction != null && theAction.equals("back")) {
            // Go back to previous part of registration
            pageContext.forward("/registration/DomainSelectionController.jsp?direction=backward");

        } else {
            // All other actions are implied to be a "cancel"
            response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/Login.jsp");

        }

    } else {
        // Likely that we are forwarded to from previous part of registration
        // or backwards navigation from next part of registration
        // Regardless, we always display the the first page

        if (registration.getUserDomainID() == null) {
            throw new net.project.base.PnetException("Missing or empty domainID in DirectoryAuthorizationController.jsp");
        }
        navigateDirectoryAuthorization(request, response, pageContext, registration);
    }
%>

<%!
    void navigateDirectoryAuthorization(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.jsp.PageContext pageContext, net.project.admin.RegistrationBean registration) 
            throws javax.servlet.ServletException, java.io.IOException, net.project.persistence.PersistenceException, net.project.base.directory.DirectoryException {

        // Get domain for id
        net.project.security.domain.UserDomain domain = registration.getUserDomain();

        // Determine the appropriate registration authorization page from the current directory provider type
        net.project.base.directory.DirectoryConfigurator configurator = domain.getDirectoryProviderType().newConfigurator();
        configurator.serviceRegistrationAuthorization(request, response, pageContext, registration);

    }
%>
