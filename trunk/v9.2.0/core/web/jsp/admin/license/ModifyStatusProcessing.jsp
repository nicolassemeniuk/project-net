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
    info="Modify Status Processing"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
			net.project.base.Module,
			net.project.license.DisabledReason,
			net.project.license.LicenseStatusCode,
			net.project.security.SessionManager,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="license" class="net.project.license.License" scope="session" />
<jsp:useBean id="statusReason" class="net.project.license.StatusReason" scope="page" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="delete"/>

<html>
<head>
<title>License Disable </title>
</head>

<%  // Grab the reason code parameters.
	String reasonCodeOption = request.getParameter("reasonCodeOption");
	String reasonCode = request.getParameter("reasonCode");
	String shortName = request.getParameter("shortName");
	String message = request.getParameter("message");
	
	// Get the license status parameters
	String licenseStatusCode = request.getParameter("licenseStatusCode");
	
	if (!licenseStatusCode.equals(String.valueOf(LicenseStatusCode.ENABLED.getCodeID()))) {
		if (reasonCodeOption.equals("newReasonCode")) {
			statusReason.setShortName(request.getParameter("shortName"));
			statusReason.setMessage(request.getParameter("message"));
			statusReason.store();
			reasonCode = String.valueOf(statusReason.getReasonCode());
		}    
	}
	license.modifyStatus(net.project.util.Conversion.toInt(licenseStatusCode), net.project.util.Conversion.toInt(reasonCode));
%>

<jsp:forward page='<%= "/admin/license/LicenseDetailView.jsp?module=" + Module.APPLICATION_SPACE + "%26action=" + Action.VIEW%>'/>
