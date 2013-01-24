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
| Licensing Handler Controller during login
|--------------------------------------------------------------------%>

<%@ page
    contentType="text/html; charset=UTF-8"
    info="LicenseHandler"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.license.LicenseResult,
            net.project.license.LicenseResultCode,
			net.project.license.LicenseStatus,
			net.project.license.LicenseStatusCode"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="licenseUpdater" class="net.project.license.create.LicenseUpdater" scope="session" />
<jsp:useBean id="loginManager" class="net.project.security.login.LoginManager" scope="request" /> 

<% 
    // Assumes the user has been authenticated successfully
    // Pass the authenticated user to the licenseUpdater
    // Pass the result code there too
    licenseUpdater.setUser(loginManager.getAuthenticatedUser());
    licenseUpdater.setResult(loginManager.getLicenseResult());
	licenseUpdater.setStatus(loginManager.getLicenseStatus());
    licenseUpdater.setLicenseProperties(net.project.license.system.LicenseProperties.getInstance(request));
    // It is assumed that this handler is called only when the result/status is not valid
	// Get the result code of the license check
    LicenseResult result = licenseUpdater.getResult();
	// Get the status code of the license check
	
    LicenseStatus status = licenseUpdater.getStatus();
	
	String errorMessage = null;
	
	if (status == null) {
		// User is a registered user in the system and had a valid license at one point.
		// But may have been stripped of his license. Allow user to reassociate/associate with some other license.
		pageContext.forward("/login/LicenseUpdater.jsp");
	} else if (status.getCode().equals(LicenseStatusCode.DISABLED)) { 
		errorMessage = PropertyProvider.get("prm.license.error.disabled.message", new Object[] {status.getMessage()});
//		errorMessage = "Your license has been disabled/canceled by administrator. <br>" + status.getMessage() + " <br>Please contact your administrator.";
		request.setAttribute("SaSecurityError",errorMessage);
        pageContext.forward("/Login.jsp");
		 
    } else if (status.getCode().equals(LicenseStatusCode.CANCELED)) {
		// Allow user to associate with some other license
		pageContext.forward("/login/LicenseUpdater.jsp?licenseStatus=canceled");
	    
	} else if (result.getCode().equals(LicenseResultCode.FAILURE) ||
        result.getCode().equals(LicenseResultCode.CERTIFICATE_KEY_MISMATCH) ) {
        // Error with license; this requires attention, is not solvable by
        // the user
        if (result.getCode().equals(LicenseResultCode.FAILURE)) {
            errorMessage = PropertyProvider.get("prm.license.error.reading.message");
        } else if (result.getCode().equals(LicenseResultCode.CERTIFICATE_KEY_MISMATCH)) {
            errorMessage = PropertyProvider.get("prm.license.error.notvalid.message");
      	}else {
            errorMessage = PropertyProvider.get("prm.license.error.unknown.message");
        } 
        request.setAttribute("SaSecurityError",errorMessage);
        pageContext.forward("/Login.jsp");
        
	} else {
        // Some other error (missing or constraint exceeded)
        pageContext.forward("/login/LicenseUpdater.jsp");
	}
%>