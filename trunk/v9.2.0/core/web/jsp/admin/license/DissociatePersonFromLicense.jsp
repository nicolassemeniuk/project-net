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
| Dissociates a person from a license.
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Dissociate Person from License - this page does not emit any output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.SessionManager,
			net.project.resource.Person,
			net.project.security.Action,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="license" class="net.project.license.License" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<%  
	String userID = request.getParameter("userID");
	//System.out.println("DissociateFromLicense.jsp : userID is :" + userID);
	Person person = new Person(userID);
	person.load();

    // Force disassociation of the persons current license
    // Since a person has only one license, this will disassociate the
    // current license.
    net.project.license.LicenseManager licenseManager = new net.project.license.LicenseManager();
    licenseManager.disassociateCurrentLicense(person);
	
	pageContext.forward("/admin/license/LicenseDetailView.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.MODIFY);
%>