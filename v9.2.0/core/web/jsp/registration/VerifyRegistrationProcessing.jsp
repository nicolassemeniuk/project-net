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
|   $RCSfile$
|   $Revision: 20348 $
|   $Date: 2010-01-29 11:23:05 -0300 (vie, 29 ene 2010) $
|   $Author: nilesh $
|
|   Primary Registration Page
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Verify Registration Processing - this page does not emit any output." 
    language="java" 
	errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.resource.Person,
			net.project.resource.PersonStatus,
			net.project.util.HTMLUtils"
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%------------------------------------------------------------------------
  -- Process the form submittal
  ----------------------------------------------------------------------%>

<jsp:setProperty name="registration" property="*" />

<%------------------------------------------------------------------------
  -- Setup Error Handling
  ----------------------------------------------------------------------%>

<%
	  boolean userLoaded = false;
	  String errorMessage = "";

	  // clear old error messages.
	  session.removeValue("errorMsg");
%>    

<%------------------------------------------------------------------------
  -- Create User
  ----------------------------------------------------------------------%>

<%
    String email = request.getParameter("ecom_ShipTo_Online_Email");
    String verificationCode = request.getParameter("verificationCode");
    
    String theAction = request.getParameter("theAction");
    if(theAction != null){
    	theAction = HTMLUtils.escape(theAction).replaceAll("'", "&acute;");
    }
    // Validate the RegisterWizard3 page, then create new user.
	if (theAction.equals("next")) {

	    // restore the registration from the database.
		if (email != null){

            // Loads based on email address
            registration.loadForEmail(email);
            if (!registration.isLoaded()) {
                // No registration loaded for the email address
                errorMessage += PropertyProvider.get("prm.registration.verify.error.emailinvalid.message");
                session.putValue("errorMsg", errorMessage);
                pageContext.forward("/registration/VerifyRegistration.jsp");
            
            } else {
                // The email address was found

                if (registration.getVerificationCode().equalsIgnoreCase(verificationCode)) {
                    // Load the user based on the email address
                    // And update their status
                    user.loadForEmail(registration.getEmail());
					 
                    if (user.getStatus().equals(PersonStatus.UNCONFIRMED) || user.getStatus().equals(PersonStatus.UNREGISTERED)) {
                        user.setStatus(PersonStatus.ACTIVE);
                        user.store();

                        if (registration.isInvited()) {
                            registration.checkUserInvitations(user.getID());
                        }
                    }

                    // Navigate to Login page
                    // escaped the backslash because this string is substituted into
                    // a Javascript string
                    session.putValue("verified", PropertyProvider.get("prm.registration.verify.verificationcomplete.message"));
                    response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/Login.jsp?userDomain=" + registration.getUserDomain().getID());
                    
					// fix for bfd-5132 
                    session.removeValue("registration");
                    session.removeValue("user");   
                } else {
                    // Bad verification code
                    errorMessage += PropertyProvider.get("prm.registration.verify.error.codeinvalide.message");
                    session.putValue("errorMsg", errorMessage);
                    pageContext.forward("/registration/VerifyRegistration.jsp");
                }

            }

        } else {
            // Missing email address parameter
            errorMessage+=  PropertyProvider.get("prm.registration.verify.error.emailblank.message");
            session.putValue("errorMsg", errorMessage);
            pageContext.forward("/registration/VerifyRegistration.jsp");
        }
    
    } else {
        throw new net.project.base.PnetException("Missing or unknown action '" + theAction + "' in VerifyRegistrationProcessing.jsp");

    }
%>
