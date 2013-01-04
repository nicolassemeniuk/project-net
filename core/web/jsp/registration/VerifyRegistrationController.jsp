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
    info="Registration Verification Controller; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.resource.PersonStatus"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="page" /> 

<%
    // Redirect to start of process if registration not started
    if (!registration.isStarted()) {
        response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/registration/Register.jsp");
        return;
    }

    net.project.security.domain.UserDomain domain = registration.getUserDomain();
    
    if (domain.isVerificationRequired()) {
        // Go to verification code page
        // Important to redirect so that "refresh" doesn't resubmit to this page
        // Also, bookmarking verification page should be possible
        response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/registration/VerifyRegistration.jsp");

    } else {
        // Verification Not Required
        // We need to mark the user as active

        // Load the user based on the email address
        // And update their status
        user.clear();
        user.loadForEmail(registration.getEmail());

        user.setStatus(PersonStatus.ACTIVE);
        user.store();

        if (registration.isInvited()) {
            registration.checkUserInvitations(user.getID());
        }

        // Now Navigate to Login page
        // escaped the backslash because this string is substituted into
        // a Javascript string
        session.putValue("verified", PropertyProvider.get("prm.registration.verify.registrationcomplete.message"));
        response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/Login.jsp?userDomain=" + domain.getID());
    }

    session.removeValue("registration");
%>
