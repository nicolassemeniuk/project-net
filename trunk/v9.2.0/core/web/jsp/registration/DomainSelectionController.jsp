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
    info="Registration DomainSelectionController; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
    		net.project.util.HTMLUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%
    // Initializes the Properties, if this page is directly accessed (click on link in an email). 
    PropertyProvider.setContextFromRequest (request, application);

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

    if (fromPage != null && fromPage.equals("domainSelect")) {
    	fromPage = HTMLUtils.escape(fromPage).replaceAll("'", "&acute;");
        // We were submitted to from the "domainSelect" page
        // We can assume theAction is valid
        
        if (theAction != null && theAction.equals("next")) {
        	theAction = HTMLUtils.escape(theAction).replaceAll("'", "&acute;");
            // User sumbmitted a domain selection

            String domainID = request.getParameter("domainID");
            if (domainID == null || domainID.trim().length() == 0) {
                throw new net.project.base.PnetException("Missing domainID in DomainSelectionController.jsp");
            } else {
                registration.setUserDomain(domainID);
                pageContext.forward("/registration/DirectoryAuthorizationController.jsp");
            }

        } else if (theAction != null && theAction.equals("back")) {
            pageContext.forward("/registration/TOUController.jsp?direction=backward");

        } else {
            // All other actions are implied to be a "cancel"
            response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/Login.jsp");

        }

    } else {
        // We were not submitted to from the "domainSelect" page
        // Likely that we are forwarded to from previous part of registration
        // Decide whether to display "domainSelect" page

        // Get domains for current configuration
        String configurationID = PropertyProvider.getActiveConfigurationID();
        net.project.security.domain.UserDomainCollection domainCollection = new net.project.security.domain.UserDomainCollection();
        domainCollection.loadForConfigurationID(configurationID);

        if (domainCollection.size() > 1) {
            // Place Domains in the request for the DomainSelection page
            // to display
            request.setAttribute("availableDomains", domainCollection);
            pageContext.forward("/registration/DomainSelection.jsp");

        } else {
            // Go to previous or next part of registration based on direction
            if (direction != null && direction.equals("backward")) {
                pageContext.forward("/registration/TOUController.jsp");
            } else {
               // Grab the first (and only) domain's ID and continue to the next page
               String domainID = ((net.project.security.domain.UserDomain) domainCollection.get(0)).getID();
               registration.setUserDomain(domainID);
               pageContext.forward("/registration/DirectoryAuthorizationController.jsp?domainID=" + domainID);
            }
        }
    }
%>
    
