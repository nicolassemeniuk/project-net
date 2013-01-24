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

<%----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Configuration Remove Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="java.util.ArrayList,
			net.project.persistence.PersistenceException,
			net.project.security.User,
			net.project.security.SessionManager,
			net.project.security.Action,
			net.project.base.Module,
			net.project.admin.ApplicationSpace,
			net.project.configuration.ConfigurationSpace,
			net.project.configuration.ConfigurationSpaceManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="configurationSpace" class="net.project.configuration.ConfigurationSpace" scope="session" />

<%
	// If we have a configurationID parameter then use that to load the configuration
	// to be removed.  This parameter is passed when the configuration to be
	// removed is not in session.
	String configurationID = request.getParameter("configurationID");
	if (configurationID != null && configurationID.length() > 0) {
		configurationSpace.setID(configurationID);
		configurationSpace.load();
	}
	configurationSpace.setUser(user);

	String refLink, refLinkEncoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");
%>


<%
	// The ability to remove the in-session configuration space is based on being
	// a space admin of that space (or application admin).
%>
<security:verifyAccess action="delete"
					   module="<%=Module.CONFIGURATION_SPACE%>" /> 

<%
	// Check that the user has space admin access.  Throws a security exception
	// if they do not.
	configurationSpace.securityCheckSpaceAdministrator(user, "No permission to remove configuration");
%>

<%
	//
	// Remove the configuration
	//
	configurationSpace.remove();

	//
	// Return to referring page
	//
	response.sendRedirect(SessionManager.getJSPRootURL() + refLink);
%>

