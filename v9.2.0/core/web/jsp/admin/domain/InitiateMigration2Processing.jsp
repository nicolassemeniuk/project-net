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
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
	info="Initiate Domain Migration -- Final Step  Processing"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.User,
            net.project.configuration.ConfigurationSpace,
			net.project.security.domain.DomainMigration,
            java.util.Iterator,
			net.project.security.SessionManager"
%>

<jsp:useBean id="domainMigration" class="net.project.security.domain.DomainMigration" scope="session"/>
<jsp:useBean id="domainMigrationManager" class="net.project.security.domain.DomainMigrationManager" scope="request"/>
<jsp:useBean id="csManager" class="net.project.configuration.ConfigurationSpaceManager" scope="request"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/> 
<jsp:useBean id="userDomain" class="net.project.security.domain.UserDomain" scope="session"/> 

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%

	String action = request.getParameter("theAction");
		
	if (action != null && action.equals("back")) {
		pageContext.forward("InitiateMigration1.jsp?module="+net.project.base.Module.APPLICATION_SPACE+"&action="+net.project.security.Action.MODIFY+"&selected="+domainMigration.getSourceDomainID());	
	} else if (action != null && action.equals("next")) {
	
			if(request.getParameter("configurations") != null && request.getParameter("selected") != null && request.getParameter("selected").trim().equals("1")) {
				
				String str[] = request.getParameterValues("configurations");
				
				for (int i = 0 ; i < str.length ; i++) {
					userDomain.addSupportedConfigurations(str[i]);
				}
				
			}	
				
			pageContext.forward("InitiateMigration3.jsp?module="+net.project.base.Module.APPLICATION_SPACE+"&action="+net.project.security.Action.MODIFY);
	}
%>
