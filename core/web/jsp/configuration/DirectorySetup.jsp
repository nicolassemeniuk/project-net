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
|--------------------------------------------------------------------%>


<title>Directory Setup</title>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Business Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.configuration.ConfigurationMemberWizard,
		    net.project.security.SessionManager,
		    net.project.base.Module,
		    net.project.security.Action,
			net.project.security.User"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />


<%
     ConfigurationMemberWizard memberWizard = new ConfigurationMemberWizard();

     // now add the appropriate flavor of space member wizard to the session
     session.removeValue ("spaceInvitationWizard");
     session.putValue ("spaceInvitationWizard", memberWizard);

     // set a number of properties which will be used by the directory module
     pageContextManager.setProperty ("space.main.url", SessionManager.getJSPRootURL() + "/configuration/Main.jsp");
	 pageContextManager.setProperty ("space.invite.acceptrequired.url", 
			SessionManager.getJSPRootURL() + 
			"/configuration/ConfigurationRegister.jsp" + 
			"?id=" + user.getCurrentSpace().getID() +
			"&module=" + net.project.base.Module.PERSONAL_SPACE +
			"&action=" + net.project.security.Action.VIEW +
			"&external=1");
	 pageContextManager.setProperty ("space.invite.autoaccept.url", 
	 		SessionManager.getJSPRootURL() +
			"/configuration/Main.jsp" +
			"?id=" + user.getCurrentSpace().getID() +
			"&module=" + net.project.base.Module.BUSINESS_SPACE +
			"&action=" + net.project.security.Action.VIEW +
			"&external=1");
%>


<%
     // these probably won't need changed
     pageContextManager.setProperty ("directory.url", SessionManager.getJSPRootURL() + "/roster/Directory.jsp");
     pageContextManager.setProperty ("directory.url.queryString", "module=" + Module.DIRECTORY + "&action=" + Action.VIEW);
     pageContextManager.setProperty ("directory.url.complete", pageContextManager.getProperty ("directory.url") + "?" +pageContextManager.getProperty ("directory.url.queryString"));


     pageContext.forward ("/roster/Directory.jsp");
%>

