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
    info="Registration License Controller; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.util.Validator,
            net.project.license.create.LicenseSelectionType"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%
    // Redirect to start of process if registration not started
    if (!registration.isStarted()) {
        response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/registration/Register.jsp");
        return;
    }

    // Capture the direction that registration is moving in
    String direction = request.getParameter("direction");

    // Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
    String theAction = request.getParameter("theAction");

    if (fromPage != null && fromPage.equals("license")) {
        // We were submitted to from the "license" page
        // We can assume theAction is valid

        if (theAction != null && theAction.equals("next")) {
            net.project.license.create.LicenseContext licenseContextBean = registration.getLicenseContext();
            pageContext.setAttribute("licenseContext", licenseContextBean, PageContext.PAGE_SCOPE);
%>
<jsp:useBean id="licenseContext" type="net.project.license.create.LicenseContext" scope="page" />

            <%-- Set the values from the html form.  Note, by setting in this order
                 the license context can determine whether to use the chargeCode or
                 enteredLicenseKey based on the selectionTypeID
             --%>
            <jsp:setProperty name="licenseContext" property="chargeCode" />
            <jsp:setProperty name="licenseContext" property="enteredLicenseKey" />
            <jsp:setProperty name="licenseContext" property="selectionTypeID" />
<%
            licenseContext.setCurrentPersonID(registration.getID());
            licenseContext.validate();
            if (licenseContext.hasErrors()) {
                // Go back to license page
                pageContext.forward("/registration/LicenseSelect.jsp");
            } else {

                // They have specified the appropriate values and we can preliminarily
                // grant the license
                // Continue with registration

                String returnPage = request.getParameter("returnPage");
                if (returnPage != null && returnPage.equals("autoRegistrationController")) {
                     response.sendRedirect(SessionManager.getJSPRootURL() + "/registration/AutomaticRegistrationController.jsp?fromPage=licenseController");
                } else {
                    pageContext.forward("/registration/ProfileController.jsp");
                }
            }

        } else if (theAction != null && theAction.equals("back")) {
            pageContext.forward("/registration/DirectoryAuthorizationController.jsp?direction=backward");

        } else {
            // All other actions are implied to be a "cancel"
            response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/Login.jsp");

        }

    } else if (fromPage != null && fromPage.equals("autoRegistration")) {

        net.project.license.system.LicenseProperties licenseProps = net.project.license.system.LicenseProperties.getInstance(request);

        if (licenseProps.isLicenseRequiredAtRegistration()) {
            response.sendRedirect(SessionManager.getJSPRootURL() + "/registration/LicenseSelect.jsp?domainSource=registrationBean&returnPage=autoRegistrationController");
        } else {
            response.sendRedirect(SessionManager.getJSPRootURL() + "/registration/AutomaticRegistrationController.jsp?fromPage=licenseController");
        }

    } else {
        // We were not submitted to from the "license" page
        // Likely that we are forwarded to from previous part of registration
        // Decide whether to display "license" page

        net.project.license.system.LicenseProperties licenseProps = net.project.license.system.LicenseProperties.getInstance(request);

        if (licenseProps.isLicenseRequiredAtRegistration()) {
            response.sendRedirect(SessionManager.getJSPRootURL() + "/registration/LicenseSelect.jsp");

        } else {
            // No need to display License
            // Go to previous or next part of registration based on direction
            if (direction != null && direction.equals("backward")) {
                // Backward navigation, returns to Directory Authorization
                pageContext.forward("/registration/DirectoryAuthorizationController.jsp");

            } else {
                // Forward navigation goes to Profile
                pageContext.forward("/registration/ProfileController.jsp");

            }
        }

    }
%>
