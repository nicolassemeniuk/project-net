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
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Profile License Processing. Omits no output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager"
%>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<%
    String theAction = request.getParameter("theAction");
    
    if (theAction != null && theAction.equals("assignLicense")) {
%>
    <jsp:useBean id="licenseUpdater" class="net.project.license.create.LicenseUpdater" scope="session" />
<%
        licenseUpdater.setUser(registration);
        licenseUpdater.setLicenseProperties(net.project.license.system.LicenseProperties.getInstance(request));
        pageContext.forward("/personal/LicenseUpdater.jsp");
    } else {
        throw new net.project.base.PnetException("Missing or unknown action '" + theAction + "' in ProfileLicenseProcessing.jsp");
    }
%>