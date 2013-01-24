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
    info="Registration Profile Controller; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager,
			net.project.admin.RegistrationException"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="userDomainMigrationManager" class="net.project.security.domain.UserDomainMigrationManager" scope="session" /> 

<%
    // Capture the direction that registration is moving in
    String direction = request.getParameter("direction");
    
    // Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
	String theAction = request.getParameter("theAction");

        // We were submitted to from the "profile" page
        // We can assume theAction is valid
        
   	if (theAction != null && theAction.equals("finish")) {
	
			userDomainMigrationManager.migrateUser(registration);
				  
		 	String wizardMode = (String)pageContext.getAttribute("WizardMode" , pageContext.SESSION_SCOPE);
			
			String targetDomainID = userDomainMigrationManager.getTargetDomain().getID();
			String login = java.net.URLEncoder.encode(registration.getLogin());
				
			if(wizardMode != null && wizardMode.equals("PopUp")) {
			
				out.println("<script>self.opener.parent.location='"+SessionManager.getJSPRootURL()+"/Login.jsp?userDomain="+targetDomainID+"&login="+login+"';"+"self.close();</script>");
				userDomainMigrationManager.clear();
				session.removeAttribute("userDomainMigrationManager");		
			} else { 
				userDomainMigrationManager.clear();
				session.removeAttribute("userDomainMigrationManager");
				response.sendRedirect(SessionManager.getJSPRootURL()+"/Login.jsp?userDomain="+targetDomainID+"&login="+login);
			}	
			
   	} else if (theAction != null && theAction.equals("back")) {
            // Go back to previous part of registration (licensing)
            pageContext.forward("/domain/Profile.jsp?theAction=back");

   	} else {
            // All other actions are implied to be a "cancel"
			userDomainMigrationManager.clear();
			session.removeAttribute("userDomainMigrationManager");
            response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/NavigationFrameset.jsp");

   	}

%>
