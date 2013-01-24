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
	info="Initiate Migration Processing -- Step 1 Processing"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.configuration.ConfigurationSpace,
			net.project.security.domain.DomainMigrationStatus,
			net.project.security.domain.DomainMigration,java.util.Iterator,
			net.project.security.SessionManager"
%>

<jsp:useBean id="domainMigration" class="net.project.security.domain.DomainMigration" scope="session"/>
<jsp:useBean id="userDomainMigrationManager" class="net.project.security.domain.UserDomainMigrationManager" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/> 
<%
		int status = Integer.parseInt(request.getParameter("selected"));
		
		String wizardMode = (String)pageContext.getAttribute("WizardMode" , pageContext.SESSION_SCOPE);
		
		if (status == DomainMigrationStatus.NEVER_STARTED){
			pageContext.forward("DirectoryAuthorizationController.jsp");
		} else if (status == DomainMigrationStatus.SKIP_FOR_NOW) {
		
			if(wizardMode != null && wizardMode.equals("PopUp")) 
				out.println("<script>self.close()</script>");
			else	
				response.sendRedirect(SessionManager.getJSPRootURL()+"/NavigationFrameset.jsp");
				
		} else if (status == DomainMigrationStatus.USER_CANCELLED || status == DomainMigrationStatus.REMIND_LATER ) {
		
			userDomainMigrationManager.setUserMigrationStatus(status);
			
			if(wizardMode != null && wizardMode.equals("PopUp")) 
				out.println("<script>self.close()</script>");
			else	
				response.sendRedirect(SessionManager.getJSPRootURL()+"/NavigationFrameset.jsp");
		} 
   		
%>
