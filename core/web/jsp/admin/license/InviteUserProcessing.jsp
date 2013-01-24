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
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Associate Users To Lcense"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,
			net.project.license.LicenseNotification,
			net.project.notification.NotificationException,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.security.User,
			net.project.space.PersonalSpace"
	    	
  %>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="license" class="net.project.license.License" scope="session" />

<%-- Unlike most of the pages in the application space, this page is not checking
     that you have access to the application space.  Instead it is just checking
     to see that you have access to the application module.  We do this because
     this page is shared between the application space and the personal space. --%>
<security:verifyAccess action="modify"
                       module="<%=net.project.base.Module.PERSONAL_SPACE%>" />
					   
<% 
	String email = request.getParameter("email");
	try {
		new LicenseNotification().inviteUserToAssociate(license, email);
		session.setAttribute("notifyUserMessage", "User with e-mail address " + email + " has been invited to join Project.net with this license.");
	} catch (NotificationException ne) {
		session.setAttribute("notifyUserMessage", "Invitation could not be sent to the user. <P> Please make sure that you enter a single, valid e-mail address.");
	}
%>					   

<jsp:forward page="InviteUser.jsp">
<jsp:param name="module" value="<%= Module.APPLICATION_SPACE %>"/>
<jsp:param name="action" value="<%= Action.VIEW %>"/>
</jsp:forward>					   
