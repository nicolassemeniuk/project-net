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
| Controller for displaying Directory-specific pages
| for Forgotten Info wizard
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Forgotten Info Directory Controller; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%
    // Capture the direction that wizard is moving in
    String direction = request.getParameter("direction");
    // Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
    String theAction = request.getParameter("theAction");

    if (fromPage != null && fromPage.equals("directoryPage")) {
        // We were submitted to from the end of the directory process
        // We can assume theAction is valid

        if (direction != null && direction.equals("backward")) {
            // Go back to Domain selection
            pageContext.forward("/registration/ForgottenInfoDomainSelectionController.jsp");

        } else {
            // We're done
            pageContext.forward("/registration/ForgottenInfoWizard.jsp?theAction=cancel");
        }

    } else {
        // Likely that we are forwarded to from previous part of forgotten info wizard
        // or backwards navigation from next part of forgotten info wizard
        // Regardless, we always display the the first page
        String domainID = request.getParameter("domainID");
        
        if ( domainID == null || domainID.length() == 0) {
            throw new net.project.base.PnetException("Missing or empty domainID in ForgottenInfoDirectoryController.jsp");
        }
        navigateDirectory(request, response, pageContext, domainID);
    }
%>

<%!
    void navigateDirectory(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.jsp.PageContext pageContext, String domainID) 
            throws javax.servlet.ServletException, java.io.IOException, net.project.persistence.PersistenceException, net.project.base.directory.DirectoryException {

        // Get domain for id
        net.project.security.domain.UserDomain domain = new net.project.security.domain.UserDomain(domainID);
        domain.load();
        request.setAttribute("userDomain", domain);

        // Determine the appropriate registration authorization page from the current directory provider type
        net.project.base.directory.DirectoryConfigurator configurator = domain.getDirectoryProviderType().newConfigurator();
        configurator.serviceForgottenInfo(request, response, pageContext);

    }
%>
