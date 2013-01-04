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
| Controller for displaying the LDAP Forgotten Info pages
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="LDAP Directory Forgotten Info Controller; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%
    // Capture the direction that registration is moving in
    String direction = request.getParameter("direction");
    
    // Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
    String theAction = request.getParameter("theAction");

    if (fromPage != null && fromPage.equals("ldapForgottenInfoPage")) {
        // We were submitted to from the end of the directory authorization process
        // We can assume theAction is valid

        if (theAction != null && theAction.equals("back")) {
            // Go back to previous part of forgotten info wizard
            pageContext.forward("/registration/ForgottenInfoDirectoryController.jsp?direction=backward&fromPage=directoryPage");

        } else {
            // All other actions are implied to be a "cancel"
            pageContext.forward("/registration/ForgottenInfoWizard.jsp?theAction=cancel");

        }

    } else {
        // Likely that we are forwarded to from previous part of forgotten info wizard
        pageContext.forward("/registration/ldap/ForgottenInfo.jsp");
    }
%>
