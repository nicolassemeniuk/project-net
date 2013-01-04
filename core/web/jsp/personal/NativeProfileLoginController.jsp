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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|	 $Author: umesha $
| Controller for managing native directory profile login editing
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Profile Login Info - Native; omits no output"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.base.directory.nativedir.NativeProfileEditor"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%
    // Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
    String theAction = request.getParameter("theAction");

    if (fromPage != null && fromPage.equals("nativeProfileLogin")) {
        // We were submitted to from the "nativeProfileLogin" page
        // We can assume theAction is valid
        String nextPage = request.getParameter("nextPage");

        if (theAction != null && theAction.equals("submit")) {
            // User submitted profile login modifications
            boolean isInfoValid = false;

            // Grab the current profile-edit directory entry from the registration bean
            // and place it in request scope
            NativeProfileEditor nativeDirectoryEntryBean = (NativeProfileEditor) registration.getDirectoryEntry();
			// Clear the existing errors
			nativeDirectoryEntryBean.clearErrors();
			
            pageContext.setAttribute("nativeDirectoryEntry", nativeDirectoryEntryBean, PageContext.REQUEST_SCOPE);
%>
<jsp:useBean id="nativeDirectoryEntry" type="net.project.base.directory.nativedir.NativeProfileEditor" scope="request" />

            <jsp:setProperty name="nativeDirectoryEntry" property="currentLogin" />
            <jsp:setProperty name="nativeDirectoryEntry" property="currentClearTextPassword" />

            <jsp:setProperty name="nativeDirectoryEntry" property="changeLogin" />
            <jsp:setProperty name="nativeDirectoryEntry" property="changePassword" />
            <jsp:setProperty name="nativeDirectoryEntry" property="changeHints" />

            <jsp:setProperty name="nativeDirectoryEntry" property="newLogin" />
            <jsp:setProperty name="nativeDirectoryEntry" property="newPassword" />
            <jsp:setProperty name="nativeDirectoryEntry" property="newPasswordRetype" />
            <jsp:setProperty name="nativeDirectoryEntry" property="newClearTextHintPhrase" />
            <jsp:setProperty name="nativeDirectoryEntry" property="newClearTextHintAnswer" />
<%
            // Ensure all required values present for selected options
            nativeDirectoryEntry.validate();
            if (nativeDirectoryEntry.hasErrors()) {
                request.setAttribute("errorMessage", nativeDirectoryEntry.getAllErrorMessages());
                isInfoValid = false;

            } else {
                // Validate ok
                if (nativeDirectoryEntry.isChangeLogin()) {
                    // Changing login name, so check for duplicates
                    if (!registration.isUsernameAvailable(nativeDirectoryEntry.getNewLogin())) {
                        request.setAttribute("errorMessage", PropertyProvider.get("prm.personal.profile.login.nameinuse.message"));
                        isInfoValid = false;
                    } else {
                        // New username checked out ok
                        isInfoValid = true;
                    }
                } else {
                    // Not changing login name
                    isInfoValid = true;
                }
            }
            
            if (isInfoValid) {
                // Everything is OK
                // Indicate that changes are ok
                nativeDirectoryEntry.approveChanges();

                // Now update user and directory entry
                registration.setLogin(nativeDirectoryEntry.getLogin());
                registration.updateRegistration();

                // Now clear out directory entry so it gets reloaded and refreshes the registration object
				String registrationID = registration.getID();
				registration.clear();
				registration.setID(registrationID);
				registration.load();
               	registration.populateFromDirectoryEntry();

                String successMessage = java.net.URLEncoder.encode(PropertyProvider.get("prm.personal.profile.login.infochanged.message"));
                
                // Next page to navigate to is handled by the main ProfileLoginController page
                // We redirect to that page in order to reset any request parameters received here
                response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/personal/ProfileLoginController.jsp?fromPage=profileLogin&module=" + net.project.base.Module.PERSONAL_SPACE + "&theAction=" + theAction + "&nextPage=" + nextPage + "&successMessage=" + successMessage);

            } else {
                // Some problem; go back to display error message
                pageContext.forward("/personal/NativeProfileLogin.jsp");
            }

        } else {
            // All other actions are handled by main page
            response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/personal/ProfileLoginController.jsp?fromPage=profileLogin&module=" + net.project.base.Module.PERSONAL_SPACE + "&theAction=" + theAction + "&nextPage=" + nextPage);
        }

    } else {
        // Arriving from main ProfileLoginController.jsp

        if (registration.getDirectoryEntry() instanceof net.project.base.directory.nativedir.NativeDirectoryEntry) {
            // Cast it to a more specific type and save it
            registration.setDirectoryEntry(new NativeProfileEditor((net.project.base.directory.nativedir.NativeDirectoryEntry) registration.getDirectoryEntry()));
            // Put in request for next page
            pageContext.setAttribute("nativeDirectoryEntry", registration.getDirectoryEntry(), PageContext.REQUEST_SCOPE);
        }

        //If there was a success message created, be sure to display it
        if (request.getParameter("successMessage") != null) {
            pageContext.setAttribute("successMessage", request.getParameter("successMessage"), PageContext.REQUEST_SCOPE);
        }
        
        //Forward to the login information page
        pageContext.forward("/personal/NativeProfileLogin.jsp");
    }
%>
