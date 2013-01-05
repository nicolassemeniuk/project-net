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
| Controller for displaying Domain Selection pages
| and navigating to next part of forgotten info wizard
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Forgotten Info DomainSelectionController; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%
    // Capture the direction that the wizard is moving in
    String direction = request.getParameter("direction");

	// Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
    String theAction = request.getParameter("theAction");
    
    if (fromPage != null && fromPage.equals("domainSelect")) {
        // We were submitted to from the "domainSelect" page
        // We can assume theAction is valid
        
        if (theAction != null && theAction.equals("next")) {
            // User sumbmitted a domain selection
            String domainID = request.getParameter("domainID");
            
            if (domainID == null || domainID.trim().length() == 0) {
                throw new net.project.base.PnetException("Missing domainID in ForgottenInfoDomainSelectionController.jsp");
            } else {
                pageContext.forward("/registration/ForgottenInfoDirectoryController.jsp");
            }

        } else if (theAction != null && theAction.equals("back")) {
            pageContext.forward("/registration/ForgottenInfoWizard.jsp?direction=backward");

        } else {
            // All other actions are implied to be a "cancel"
            pageContext.forward("/registration/ForgottenInfoWizard.jsp?theAction=cancel");

        }

    } else {
        // We were not submitted to from the "domainSelect" page
        // Likely that we are forwarded to from previous part of wizard
        // Decide whether to display "domainSelect" page

        // Get domains for current configuration
        net.project.security.domain.UserDomainCollection domainCollection = new net.project.security.domain.UserDomainCollection();
        String configurationID = PropertyProvider.getActiveConfigurationID();
        domainCollection.loadForConfigurationID(configurationID);

        if (domainCollection.size() > 1) {
            // Place Domains in the request for the DomainSelection page
            // to display
            request.setAttribute("availableDomains", domainCollection);
            pageContext.forward("/registration/ForgottenInfoDomainSelection.jsp");

        } else {
            // No domain selection
            // Go to previous or next part of wizard based on direction
            if (direction != null && direction.equals("backward")) {
                pageContext.forward("/registration/ForgottenInfoWizard.jsp?directory=backward");
            } else {
               // Grab the first (and only) domain's ID and continue to the next page
               String domainID = ((net.project.security.domain.UserDomain) domainCollection.get(0)).getID();
               pageContext.forward("/registration/ForgottenInfoDirectoryController.jsp?domainID=" + domainID);
            }
        }
    }
%>

