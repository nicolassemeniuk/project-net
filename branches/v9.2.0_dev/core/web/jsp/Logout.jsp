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
    info="Logout page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
			net.project.base.property.PropertyProvider,
			net.project.security.User,
            org.apache.log4j.Logger"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
    String loginURL = SessionManager.getJSPRootURL() + "/Login.jsp";
    String userURL = request.getParameter("redirect");
    boolean ssoEnabled = PropertyProvider.getBoolean("prm.global.login.sso.allowSSO");
    if (ssoEnabled) {
    	userURL = SessionManager.getJSPRootURL() + "/sso/LoggedOut.jsp";
    }

    // Log user out, killing user's session.
	SessionManager.logout(request, user);
    
    // If passed, go to user-specified page after logout
	if (userURL != null) {
       Logger logger = Logger.getLogger("Logout.jsp");
       logger.debug("Logout redirecting to: " + userURL);
       response.sendRedirect(userURL);
    } else {
    // Otherwise, go back to login page
       if (request.getParameter("t")!= null && request.getParameter("t").equals("1")) {
    	   loginURL += "?t=1";
       }
       response.sendRedirect(loginURL);    	   
    }
%>