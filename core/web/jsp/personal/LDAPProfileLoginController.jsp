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
|   $Revision: 14743 $
|       $Date: 2006-02-06 22:26:39 +0530 (Mon, 06 Feb 2006) $
|
| Controller for managing LDAP directory profile login editing
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Profile Login Info - LDAP; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%
    // Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
    String theAction = request.getParameter("theAction");

    if (fromPage != null && fromPage.equals("ldapProfileLogin")) {
        // We were submitted to from the "ldapProfileLogin" page
        String nextPage = request.getParameter("nextPage");

        // All actions are handled by main page
        response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/personal/ProfileLoginController.jsp?fromPage=profileLogin&module=" + net.project.base.Module.PERSONAL_SPACE + "&theAction=" + theAction + "&nextPage=" + nextPage);

    } else {
        // Arriving from main ProfileLoginController.jsp
        pageContext.forward("/personal/LDAPProfileLogin.jsp");
    }
%>
