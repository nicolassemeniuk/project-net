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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
| Controller for processing license update
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="License Updater Processing - this page does not emit any output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.SessionManager,
            net.project.util.Validator,
            net.project.license.create.LicenseSelectionType,
            net.project.base.Module,
            net.project.security.Action,
            java.net.URLEncoder"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%@page import="org.hibernate.criterion.PropertyProjection"%>
<%@page import="net.project.base.property.PropertyProvider"%><jsp:useBean id="licenseUpdater" class="net.project.license.create.LicenseUpdater" scope="session" />
<jsp:useBean id="licenseContext" class="net.project.license.create.LicenseContext" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

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
            pageContext.forward("/admin/profile/LicenseUpdater.jsp");

        } else if ((!Validator.isBlankOrNull(licenseType)) &&
            (licenseType.equals(LicenseSelectionType.CREDIT_CARD.getID()))) {
                %>
                <jsp:forward page="/creditcard/CreditCardController.jsp">
                    <jsp:param name="theAction" value="initialize"/>
                    <jsp:param name="previousPage" value='<%=SessionManager.getJSPRootURL() + "/admin/profile/LicenseUpdater.jsp?module="+Module.APPLICATION_SPACE%>'/>
                    <jsp:param name="nextPage" value='<%=URLEncoder.encode(SessionManager.getJSPRootURL() + "/admin/profile/ProfileLicense.jsp?module="+Module.APPLICATION_SPACE+"&action="+Action.MODIFY+"&userID="+licenseUpdater.getUser().getID(), SessionManager.getCharacterEncoding())%>'/>
                    <jsp:param name="cancelPage" value='<%=SessionManager.getJSPRootURL() + "/admin/profile/LicenseUpdater.jsp?module="+Module.APPLICATION_SPACE%>'/>
                    <jsp:param name="updateLicense" value="true"/>
                </jsp:forward>
                <%
        } else {
            // Got all the license parameters
            // Now update the user's license
			licenseUpdater.setLicenseContext(licenseContext);
			try {
            	licenseUpdater.updateLicense();
				
			} catch (net.project.license.LicenseAlreadyAssociatedException laae) {
				// Go back to license page
				session.setAttribute("otherLicenseErrors", PropertyProvider.get("prm.project.admin.problem.message"));
				pageContext.forward("/admin/profile/LicenseUpdater.jsp");
			}
			
            pageContext.forward("/admin/profile/ProfileLicense.jsp?userID=" + licenseUpdater.getUser().getID());
        }
        
    } else {
        throw new net.project.base.PnetException(PropertyProvider.get("prm.project.admin.profile.unhandled.missing.action.message"));
        
    }
%>
