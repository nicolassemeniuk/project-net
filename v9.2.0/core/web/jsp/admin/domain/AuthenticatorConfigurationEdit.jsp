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
|   $RCSfile$
|   $Revision: 20087 $
|   $Date: 2009-10-26 11:11:06 -0300 (lun, 26 oct 2009) $
|   $Author: dpatil $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Edit Authenticator Configuration" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.User,
        	net.project.security.SessionManager,
            net.project.security.domain.UserDomain"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domain" class="net.project.security.domain.UserDomain" scope="session" /> 

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>
<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/> 

<%
    // First, load the domain for the current domain ID
    // If no domain is passed, assume domain object in session is the one to be edited
    // This allows for case where domain has just been created
    String domainID = request.getParameter("domainID");
    if (domainID == null || domainID.length() == 0 || "null".equals(domainID)) {
        domainID = domain.getID();
    }

    domain.clear();
    domain.setID(domainID);
    domain.load();

    // Determine the appropriate authenticator configuration page from the current directory provider type
    // in the domain
    net.project.base.directory.DirectoryConfigurator configurator = domain.getDirectoryProviderType().newConfigurator();
    configurator.serviceDirectoryConfiguration(request, response, pageContext, domain);
%>