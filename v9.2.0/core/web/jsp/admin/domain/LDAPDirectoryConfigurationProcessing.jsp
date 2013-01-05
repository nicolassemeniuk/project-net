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
    info="LDAP Configuration Processing" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domain" class="net.project.security.domain.UserDomain" scope="session" /> 
<jsp:useBean id="directoryConfiguration" class="net.project.base.directory.ldap.LDAPDirectoryConfiguration" scope="session" /> 
<jsp:useBean id="attributeMapEditor" class="net.project.base.directory.ldap.LDAPAttributeMapEditor" scope="request" /> 

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
    String theAction = request.getParameter("theAction");

    if (theAction != null && 
        (theAction.equals("submit") || theAction.equals("lookupUsername") || theAction.equals("test")) ) {

        // First set all the properties in the configuration
%>
        <jsp:setProperty name="directoryConfiguration" property="hostnameValues" />
        <jsp:setProperty name="directoryConfiguration" property="secureHostnameValues" />
        <jsp:setProperty name="directoryConfiguration" property="useSSL" />
        <jsp:setProperty name="directoryConfiguration" property="searchBaseDN" />
        <jsp:setProperty name="directoryConfiguration" property="searchTypeID" />
        <jsp:setProperty name="directoryConfiguration" property="searchSubtrees" />
        <jsp:setProperty name="directoryConfiguration" property="usernameAttribute" />
        <jsp:setProperty name="directoryConfiguration" property="nonAuthenticatedAccessTypeID" />
        <jsp:setProperty name="directoryConfiguration" property="specificUserDN" />
        <jsp:setProperty name="directoryConfiguration" property="specificUserPassword" />
        <jsp:setProperty name="directoryConfiguration" property="availableForDirectorySearch" />
        <jsp:setProperty name="directoryConfiguration" property="directorySearchDisplayName" />
        <jsp:setProperty name="directoryConfiguration" property="searchFilterExpression" />
        <% directoryConfiguration.setAllowsAutomaticRegistration(request.getParameter("automaticRegistration")); %>
<%
        // Now initialize the attribute map editor based on the current
        // configuration (this is necessary to read the new configuration settings)
        // And read and update all the submitted map values; this will update the configuration's map
        attributeMapEditor.initialize(directoryConfiguration);
        attributeMapEditor.processRequest(request);

        // Store the directory configuration (which also stores the map)
        // Then pass the configuration back to the domain
        directoryConfiguration.store();
        domain.setDirectoryConfiguration(directoryConfiguration);

        boolean returnToEditPage = false;
        String nextPage = null;

        // Check to ensure all require attributes are mapped
        // Note that even if not all are mapped, we continue to perform the action
        // This is for the case where the user is actually trying to lookup the
        // attributes
        if (!attributeMapEditor.isCompleteMapping()) {
            request.setAttribute("errorMsgAttributeMap", "All required attribute mappings have not been specified");
            returnToEditPage = true;
        }
        

        if (theAction.equals("lookupUsername")) {
            // We're looking up the LDAP attributes for the specified username

            String username = request.getParameter("lookupUsername");
            if (username == null || username.trim().length() == 0) {
                throw new net.project.base.PnetException("Missing parameter lookupUsername in LDAPDirectoryConfigurationProcessing");
            }

            attributeMapEditor.lookupForUsername(username);
            // Go back to same page
            nextPage = "/admin/domain/LDAPDirectoryConfigurationEdit.jsp?module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY + "&domainID=" + domain.getID();

        } else if (theAction.equals("test")) {
            // We're testing the configuration
            nextPage = "/admin/domain/ldap/ConfigurationTest.jsp?module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY;

        } else {
            // Submit
            // We'll get redirected below
        }

        if (returnToEditPage) {
            // Have to go back to edit page for error display
            pageContext.forward("/admin/domain/LDAPDirectoryConfigurationEdit.jsp?module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY + "&domainID=" + domain.getID());
        
        } else if (nextPage != null) {
            // Go to specific page
            pageContext.forward(nextPage);
        
        } else {
            // Done submitting, go back to domain page
            response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/domain/DomainEdit.jsp?module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY + "&domainID=" + domain.getID());
        }

    } else {
        throw new net.project.base.PnetException("Missing or unknown action in LDAPDirectoryConfigurationProcessing.jsp");
    }
%>

