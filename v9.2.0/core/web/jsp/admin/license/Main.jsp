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
|     $Author: umesha $
|
| Licensing Main
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Licensing Main"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
			net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>					   
<%
    //Clear out all session attributes for saved variables
    session.setAttribute("licenseType", "all");
    session.setAttribute("licenseStatus", "all");
    session.setAttribute("searchLicenseKey", "");
    session.setAttribute("userName", "");
    session.setAttribute("folName", "");
    session.setAttribute("emailID", "");
    session.setAttribute("displayLicenses", "false");


	session.setAttribute("orgPage", "/admin/license/Main.jsp");
	session.setAttribute("userIdentity", "applicationAdministrator");
%>
<jsp:forward page="/admin/license/LicenseListView.jsp">
	<jsp:param name="module" value='<%= net.project.base.Module.APPLICATION_SPACE%>'/>
	<jsp:param name="action" value='<%= Action.VIEW%>'/>
</jsp:forward>
