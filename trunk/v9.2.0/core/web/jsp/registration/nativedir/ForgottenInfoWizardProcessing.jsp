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
    info="Forgotten Password Wizard Processing"
    language="java" 
    errorPage="/errors.jsp"
	import="net.project.base.property.PropertyProvider"
%>
<jsp:useBean id="loginPasswordHelper" class="net.project.base.directory.nativedir.LoginPasswordHelper" scope="session" />

<%

    String theAction = request.getParameter("theAction");

    if (theAction != null && theAction.equals("next")) {
        String email = request.getParameter("email");

        if (email == null || email.trim().length() == 0) {
            // Email address is required
            // Go back with error
            request.setAttribute("errorMsg", PropertyProvider.get("prm.registration.forgotteninfowizard.emailrequired.message"));
            pageContext.forward("/registration/nativedir/ForgottenInfoWizard.jsp");

        } else {
            // Try loading user based on email address (and current domainID)
            loginPasswordHelper.loadUserForEmail(request.getParameter("email"));

            if (!loginPasswordHelper.isUserFound()) {
                // Didn't find a matching user
                // Go back with error
                request.setAttribute("errorMsg", PropertyProvider.get("prm.registration.forgotteninfowizard.emailnotregistered.message"));
                pageContext.forward("/registration/nativedir/ForgottenInfoWizard.jsp");

            } else {
                // We found a user with that email address
                String item = request.getParameter("item");
            	if (item != null && item.equals("password")) {
            		pageContext.forward("/registration/nativedir/ForgottenPasswordWizard1.jsp");

            	} else if (item != null && item.equals("login")) {
            		pageContext.forward("/registration/nativedir/ForgottenLoginWizard.jsp");

            	} else {
                    throw new net.project.base.PnetException("Missing or invalid selection in ForgottenInfoWizardProcessing.jsp");

                }

            }

        }
        
    } else if (theAction != null && theAction.equals("back")) {
        pageContext.forward("/registration/ForgottenInfoDirectoryController.jsp?direction=backward&fromPage=directoryPage");
    
    } else {
        // All other actions assumed to be cancel
        pageContext.forward("/registration/ForgottenInfoWizard.jsp?theAction=cancel");
    }
%>