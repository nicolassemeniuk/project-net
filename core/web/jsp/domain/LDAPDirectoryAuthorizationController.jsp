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
    info="Registration LDAP Directory Authorization Controller; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.base.directory.Directory,
            net.project.base.directory.AuthenticationContext,
            net.project.base.directory.ldap.LDAPDirectoryEntry"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%

    // Capture the direction that registration is moving in
    String direction = request.getParameter("direction");
    
    // Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
    String theAction = request.getParameter("theAction");

    if (fromPage != null && fromPage.equals("LDAPAuthorization")) {
        // We were submitted to from the "LDAPAuthorization" page
        // We can assume theAction is valid
        
        if (theAction != null && theAction.equals("next")) {
            // User proceeding to next part of registration

            // Get LDAP Directory instance
            String login = request.getParameter("login");
            String clearTextPassword = request.getParameter("password");
            Directory directory = Directory.getInstance(registration.getUserDomain().getDirectoryProviderType(), registration.getUserDomain().getDirectoryConfiguration());
            directory.setAuthenticationContext(new AuthenticationContext(registration.getUserDomainID(), login, clearTextPassword));
            LDAPDirectoryEntry entry = (LDAPDirectoryEntry) directory.getAuthenticatedDirectoryEntry();

            if (directory.isAuthenticated()) {
                // Now give registration appropriate values
                registration.setLogin(login);

                // Make sure the entry really does provide an email address
                if (entry.getEmail() == null || entry.getEmail().trim().length() == 0) {
                    throw new net.project.base.directory.DirectoryException(PropertyProvider.get("prm.domain.ldapdirectoryauthorization.emailvalidation.message"));
                }
                registration.setEmail(entry.getEmail());
                registration.setDirectoryEntry(entry);
                registration.populateFromDirectoryEntry();
				
				response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/domain/DirectoryAuthorizationController.jsp?fromPage=authorizationPage&theAction=" + theAction+"&module="+net.project.base.Module.PERSONAL_SPACE);
            } else {
                request.setAttribute("errorMessage", PropertyProvider.get("prm.domain.ldapdirectoryauthorization.usernamepasswordvalidation.message"));
                pageContext.forward("/domain/LDAPDirectoryAuthorization.jsp?module="+net.project.base.Module.PERSONAL_SPACE);
            }


        }

        // All actions result in handling in the main DirectoryAuthorizationController page
        // We redirect to that page in order to reset any request parameters received here
        response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/domain/DirectoryAuthorizationController.jsp?fromPage=authorizationPage&theAction=" + theAction+"&module="+net.project.base.Module.PERSONAL_SPACE);

    } else {
        // Arriving from main DirectoryAuthorizationController.jsp

//        if (registration.getDirectoryEntry() instanceof net.project.base.directory.ldap.LDAPDirectoryEntry) {
//            // If we already have an LDAPDirectoryEntry (already visited this step)
//            // then re-use it
//            pageContext.setAttribute("ldapDirectoryEntry", registration.getDirectoryEntry(), PageContext.REQUEST_SCOPE);
//        } else {
            // Place new instance in request
//            pageContext.setAttribute("ldapDirectoryEntry", new net.project.base.directory.ldap.LDAPRegistrationEditor(), PageContext.REQUEST_SCOPE);
//        }

        pageContext.forward("/domain/LDAPDirectoryAuthorization.jsp?module="+net.project.base.Module.PERSONAL_SPACE);
    }
%>
