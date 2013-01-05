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
    info="Configuration Menu Processing. Omits no output." 
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
	String action = request.getParameter("theAction");
	String configurationID = request.getParameter("id");
	configurationSpace.setUser(user);
%>

<%
	if (action != null && action.equals("copy")) {
		// Copy the configuration
%>
		<security:verifyAccess action="create"
							   module="<%=Module.CONFIGURATION_SPACE%>" /> 
<%
		String newConfigurationID = null;
		ArrayList portfolioMembershipIDList = new ArrayList();
		portfolioMembershipIDList.add(applicationSpace.getDefaultPortfolioID());
		
		newConfigurationID = ConfigurationSpaceManager.copyPersistent(user, configurationID, null, portfolioMembershipIDList);

		request.removeAttribute("module");
		request.removeAttribute("action");
		request.removeAttribute("id");
		request.setAttribute("module", "" + Module.APPLICATION_SPACE);
		request.setAttribute("action", "" + Action.VIEW);
		pageContext.forward("/admin/ConfigurationMenu.jsp");

// Remove capability moved to inside space
//	} else if (action != null && action.equals("remove")) { 
//		// Remove the configuration
%>
<%--		<security:verifyAccess action="delete"
							   module="<%=Module.CONFIGURATION_SPACE%>"
							   objectID="<%=configurationID%>" /> 
--%>
<%
//		configurationSpace.setID(configurationID);
//		configurationSpace.load();
//		configurationSpace.remove();
//		request.removeAttribute("module");
//		request.removeAttribute("action");
//		request.removeAttribute("id");
//		request.setAttribute("module", "" + Module.APPLICATION_SPACE);
//		request.setAttribute("action", "" + Action.VIEW);
//		pageContext.forward("ConfigurationMenu.jsp");

	} else {
		throw new PersistenceException("Unknown action '" + action + "' in ConfigurationMenuProcessing.jsp");
	}
%>

