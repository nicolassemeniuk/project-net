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
    info="Registration TOUController; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider"
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

    if (fromPage != null && fromPage.equals("tou")) {
        // We were submitted to from the "tou" page
        // We can assume theAction is valid
        
        if (theAction != null && theAction.equals("accept")) {
            // User accepted the Terms Of Use
            pageContext.forward("/registration/DomainSelectionController.jsp");
        
        } else {
            // All other actions are implied to be a "decline"
            response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/Login.jsp");

        }

    } else {
        // We were not submitted to from the "tou" page
        // Likely that we are forwarded to from previous part of registration
        // Decide whether to display "tou" page
        
        // Determine which is the first Registration page
        boolean isTermsOfUseDisplayed = PropertyProvider.getBoolean("prm.global.registration.termsofuse.isenabled");

        if (isTermsOfUseDisplayed) {
            // We must display Terms Of Use
            pageContext.forward("/registration/TOU.jsp");

        } else {
            // No need to display Terms Of Use
            // Go to previous or next part of registration based on direction
            if (direction != null && direction.equals("backward")) {
                pageContext.forward("/registration/Register.jsp");
            } else {
                pageContext.forward("/registration/DomainSelectionController.jsp");
            }

        }
    }
%>
