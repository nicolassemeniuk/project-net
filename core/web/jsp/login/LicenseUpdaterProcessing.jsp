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
| Controller for processing license update
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="License Updater Processing - this page does not emit any output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.util.Validator,
            net.project.license.create.LicenseSelectionType,
            net.project.base.Module"
%>
<jsp:useBean id="licenseUpdater" class="net.project.license.create.LicenseUpdater" scope="session" />
<jsp:useBean id="licenseContext" class="net.project.license.create.LicenseContext" scope="session" />
<%
	if (request.getParameter("theAction").equals("submit")) {
%>
        <%-- Set the values from the html form.  Note, by setting in this order
             the license context can determine whether to use the chargeCode or
             enteredLicenseKey based on the selectionTypeID
        --%>
        <jsp:setProperty name="licenseContext" property="chargeCode" />
        <jsp:setProperty name="licenseContext" property="enteredLicenseKey" />
        <jsp:setProperty name="licenseContext" property="selectionTypeID" />
<%
        String licenseType = request.getParameter("selectionTypeID");

        // Validation requires a current person id context
        licenseContext.setCurrentPersonID(licenseUpdater.getUser().getID());
        licenseContext.validate();
        if (licenseContext.hasErrors()) {
            // Go back to license page
            pageContext.forward("/login/LicenseUpdater.jsp");

        } else if ((!Validator.isBlankOrNull(licenseType)) &&
            (licenseType.equals(LicenseSelectionType.CREDIT_CARD.getID()))) {
                %>
                <jsp:forward page="/creditcard/CreditCardController.jsp">
                    <jsp:param name="theAction" value="initialize"/>
                    <jsp:param name="nextPage" value='<%=SessionManager.getJSPRootURL() + "/Login.jsp"%>'/>
                    <jsp:param name="cancelPage" value='<%=SessionManager.getJSPRootURL() + "/Login.jsp"%>'/>
                    <jsp:param name="updateLicense" value="true"/>
                </jsp:forward>
                <%
        } else {
            // Got all the license parameters
            // Now update the user's license
            licenseUpdater.setLicenseContext(licenseContext);
            licenseUpdater.updateLicense();
            response.sendRedirect(SessionManager.getJSPRootURL() + "/Login.jsp");
        }

    } else {
        throw new net.project.base.PnetException("Unhandled or missing action in LicenseUpdaterProcessing.jsp");

    }
%>