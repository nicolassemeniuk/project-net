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
| Start of forgotten info wizard
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="ForgottenInfo Wizard; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<%
    // Capture the direction that registration is moving in
    String direction = request.getParameter("direction");
    String theAction = request.getParameter("theAction");

    if (theAction != null && theAction.equals("cancel")) {
        // Cancelling wizard
        response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/Login.jsp");
    } else {
        if (direction != null && direction.equals("backward")) {
            // Backward is same as cancelling
            response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/Login.jsp");
        } else {
            // Go to domain selection
            pageContext.forward("/registration/ForgottenInfoDomainSelectionController.jsp");
        }
    }
%>
    
