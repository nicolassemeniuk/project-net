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

<%@ page import="net.project.base.IUniqueTransaction,
                 net.project.util.UniqueTransaction,
                 net.project.base.property.PropertyProvider,
                 net.project.security.SessionManager"%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Registration Begin Page. Omits no output"
    language="java"
    errorPage="/errors.jsp"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%
	// verify if extenal SSO authentication is on	
	boolean ssoEnabled = PropertyProvider.getBoolean("prm.global.login.sso.allowSSO");
	if (ssoEnabled) {
		response.sendRedirect(SessionManager.getJSPRootURL() +"/sso/SSOLogin.jsp");
		return;
	}
	
    //remove registration information if loaded.  This is important so that if one user registers
    //at a show or something, another user can't come behind and see any of the other users
    //registration information.  We have links to this page that DON'T go through the Login page
    //(which would otherwise do this for us).
    session.removeValue("registration");
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%
    // clear the current user bean.
    user.clear();
    if ((request.getParameter("email") != null)&&(!request.getParameter("email").trim().equals(""))) {
    	registration.setEmail(request.getParameter("email"));
    }
    // Indicate that registration has begun
    registration.setStarted(true);

    //Create a globally unique transaction for this registration.  This prevents
    //any credit card transactions from being submitted twice.
    IUniqueTransaction uniqueTransaction = new UniqueTransaction(request.getRemoteAddr());
    session.setAttribute("uniqueTransaction", uniqueTransaction);

    // Capture the direction that registration is moving in
    String direction = request.getParameter("direction");

    if (direction != null && direction.equals("backward")) {
        // Moving backwards in registration
        // When we reach here, registration cannot go back any further
        // Simply go back to Login page
        response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/Login.jsp");

    } else {
        // Simply forward to the first part of registration
        pageContext.forward("/registration/TOUController.jsp");
    }
    
%>
