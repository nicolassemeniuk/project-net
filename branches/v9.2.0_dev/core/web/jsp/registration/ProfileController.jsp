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
    info="Registration Profile Controller; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.util.Validator,
            net.project.license.create.LicenseSelectionType,
            net.project.security.SessionManager,
            net.project.license.system.LicenseProperties,
            net.project.hibernate.service.ServiceFactory,
            net.project.resource.PersonSalaryBean,
            java.util.Date"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="personSalaryBean" class="net.project.resource.PersonSalaryBean" scope="session" />

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

    if (fromPage != null && fromPage.equals("profile")) {
        // We were submitted to from the "profile" page
        // We can assume theAction is valid

        if (theAction != null && theAction.equals("finish")) {
            net.project.admin.RegistrationBean.RegistrationResult result = null;
            LicenseProperties licenseProps = LicenseProperties.getInstance();
            %>
            <jsp:setProperty name="registration" property="*" />
            <%
            // One more availability check
            result = registration.checkAvailability();
            if (!result.isSuccess()) {
                if (result.getFirstErrorField() != null) {
                    session.putValue("errorObj", result.getFirstErrorField());
                }
                session.putValue("errorMsg", result.getErrorMessagesFormatted());
                pageContext.forward("/registration/Profile.jsp");

		    } else {
                // Now try completing registration
                result = registration.completeRegistration(licenseProps);

        		if (result.isSuccess()) {
        	%>		
        			<jsp:setProperty name="personSalaryBean" property="personId" value='<%= registration.getID() %>' />
        			<jsp:setProperty name="personSalaryBean" property="startDate" value='<%= new Date() %>' />
        			<jsp:setProperty name="personSalaryBean" property="costByHour" />
        			<jsp:setProperty name="personSalaryBean" property="user" value='<%= registration %>' />        			
        	<%
            		// Save the first person salary
        			ServiceFactory.getInstance().getPnPersonSalaryService().saveFirstPersonSalary(personSalaryBean);	
                    // Go to Verification Code controller to determine
                    // whether the user needs to verify
                    pageContext.forward("/registration/VerifyRegistrationController.jsp");

                } else {
                    if (result.getFirstErrorField() != null) {
                        session.putValue("errorObj", result.getFirstErrorField());
                    }
      				session.putValue("errorMsg", result.getErrorMessagesFormatted());
                    pageContext.forward("/registration/Profile.jsp");

                }
            }
        } else if (theAction != null && theAction.equals("next") &&
            registration.getLicenseContext().getSelectionType().equals(LicenseSelectionType.CREDIT_CARD)) {
            %>
            <jsp:setProperty name="registration" property="*" />
            <jsp:forward page="/creditcard/CreditCardController.jsp">
                <jsp:param name="theAction" value="initialize"/>
                <jsp:param name="nextPage" value='<%=SessionManager.getJSPRootURL() + "/registration/VerifyRegistrationController.jsp"%>'/>
                <jsp:param name="previousPage" value='<%=SessionManager.getJSPRootURL() + "/registration/ProfileController.jsp?direction=backward"%>'/>
                <jsp:param name="cancelPage" value='<%=SessionManager.getJSPRootURL() + "/Login.jsp"%>'/>
                <jsp:param name="registrationHeader" value="true"/>
                <jsp:param name="completeRegistration" value="true"/>
            </jsp:forward>
            <%
        } else if (theAction != null && theAction.equals("back")) {
            // Go back to previous part of registration (licensing)
            pageContext.forward("/registration/LicenseController.jsp?direction=backward");

        } else {
            // All other actions are implied to be a "cancel"
            response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/Login.jsp");

        }

    } else {
        // We were not submitted to from the "profile" page
        // Likely that we are forwarded to from previous part of registration
        // Display the profile page
        pageContext.forward("/registration/Profile.jsp");

    }
%>
