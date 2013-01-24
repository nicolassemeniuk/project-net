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
| Handles license create
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="License Creator Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.license.LicenseKeyUniquenessException,
			net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="externalLicenseCreator" class="net.project.license.create.ExternalLicenseCreator" scope="session" />

<security:verifyAccess module="<%=net.project.base.Module.APPLICATION_SPACE%>" 
                       action="modify" />

<%
	if (request.getParameter("theAction").equals("next")) {
%>
        <jsp:setProperty name="externalLicenseCreator" property="enteredLicense" />
<%  	try {
        	externalLicenseCreator.buildLicense();
        	pageContext.forward("/admin/license/LicenseCreatorPreview.jsp");
		} catch (LicenseKeyUniquenessException lkue) {
			pageContext.forward("/admin/license/LicenseCreatorResult.jsp?licenseCreate=fail");
		}
    } else {
        throw new net.project.base.PnetException("Unhandled or missing action in LicenseCreatorProcessing.jsp");
        
    }
%>
