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
    info="Master Properties Preview Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="masterPropertiesUpdater" class="net.project.license.system.MasterPropertiesUpdater" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
	if (request.getParameter("theAction").equals("finish")) {
        masterPropertiesUpdater.store();
        //Avinash:-------------------------------------------------------------------------
        request.setAttribute("action",String.valueOf(net.project.security.Action.VIEW));
        net.project.security.ServletSecurityProvider.setAndCheckValues(request);
        //Avinash:-------------------------------------------------------------------------
        pageContext.forward("/admin/license/Main.jsp?action=" + net.project.security.Action.VIEW);
        
    } else {
        throw new net.project.base.PnetException("Unhandled or missing action in MasterPropertiesPreviewProcessing.jsp");
        
    }
%>
