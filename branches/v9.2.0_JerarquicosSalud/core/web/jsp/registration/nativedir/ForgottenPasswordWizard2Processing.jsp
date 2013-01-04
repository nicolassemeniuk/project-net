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
|   $Revision: 19979 $
|       $Date: 2009-09-16 12:35:03 -0300 (mié, 16 sep 2009) $
|     $Author: avinash $
|
| Processing page for forgotten password verification
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Forgotten Password Wizard" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager,
            net.project.base.directory.nativedir.NativeDirectoryEntry,
            net.project.base.directory.nativedir.NativeProfileEditor"
%>      

<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="page" />
<%
    String errorMessage = "";
    boolean isInfoValid = false;

    String theAction = request.getParameter("theAction");

    if (theAction != null && theAction.equals("next")) {

        String email = request.getParameter("email");
        String verificationCode = request.getParameter("verificationCode");

        if (email == null || email.trim().length() == 0) {
            errorMessage += PropertyProvider.get("prm.registration.forgottenpasswordwizard2.error.email.message")+"<br>";
            isInfoValid = false;
    
        } else if (verificationCode == null || verificationCode.trim().length() == 0) {
            errorMessage += PropertyProvider.get("prm.registration.forgottenpasswordwizard2.error.code.message")+"<br>";
            isInfoValid = false;
    
        } else {
            // Email and verification code are present
            // Try to load the user for the email address
            registration.loadForEmail(email);
    
            if (!(registration.isLoaded() && registration.getVerificationCode().trim().equalsIgnoreCase(verificationCode.trim()))) {
                // Either email or verification code doesn't match
                errorMessage += PropertyProvider.get("prm.registration.forgottenpasswordwizard2.error.invalid.message")+"<br>";
                isInfoValid = false;

            } else {
                // Verification code matches that of loaded user
                // Now update the password

                // Grab the current profile-edit directory entry from the registration bean
                // Cast it down then give it back to RegistrationBean
                NativeProfileEditor nativeDirectoryEntryBean = new NativeProfileEditor((NativeDirectoryEntry) registration.getDirectoryEntry());
                registration.setDirectoryEntry(nativeDirectoryEntryBean);
                pageContext.setAttribute("nativeDirectoryEntry", nativeDirectoryEntryBean, PageContext.PAGE_SCOPE);
%>
                <jsp:useBean id="nativeDirectoryEntry" type="net.project.base.directory.nativedir.NativeProfileEditor" scope="page" />
                <jsp:setProperty name="nativeDirectoryEntry" property="changePassword" value="true" />

                <jsp:setProperty name="nativeDirectoryEntry" property="newPassword" param="password" />
                <jsp:setProperty name="nativeDirectoryEntry" property="newPasswordRetype" param="password_2" />
<%
                // Ensure all required values present for selected options
                nativeDirectoryEntry.validateNewPassword();
                nativeDirectoryEntry.validateNewPasswordRetype();
                nativeDirectoryEntry.validateMatchingNewPassword();

                if (nativeDirectoryEntry.hasErrors()) {
                    errorMessage += nativeDirectoryEntry.getAllErrorMessages();
                    isInfoValid = false;

                } else {
                    // Validate ok

                    // Indicate that changes are ok
                    nativeDirectoryEntry.approveChanges();
                    
                    // Set new verification code after successfully verified old code
                    // to prevent verification of same code more than once
                    registration.generateVerificationCode();
                    
                    // Now update user and directory entry
                    registration.updateRegistration();

                    // Now clear out directory entry so it gets reloaded
                    registration.setDirectoryEntry(null);
                    isInfoValid = true;
                }

            }
        
        }

        if (isInfoValid) {
            // Everything was OK.  Go to confirmation screen.
        	pageContext.forward("/registration/nativedir/ForgottenPasswordConfirmation.jsp");

        } else {
            // Some problem; go back to display error message
            request.setAttribute("errorMsg", errorMessage);
        	pageContext.forward("/registration/nativedir/ForgottenPasswordWizard2.jsp");
        }
        
    } else if (theAction != null && theAction.equals("back")) {
        pageContext.forward("/registration/nativedir/ForgottenPasswordWizard1.jsp");
        
    } else {
        // All other actions assumed to be cancel
        pageContext.forward("/registration/ForgottenInfoWizard.jsp?theAction=cancel");
    }
%>
