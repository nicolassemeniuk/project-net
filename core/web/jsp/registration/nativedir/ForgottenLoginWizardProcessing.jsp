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
|   Processing page for forgotten login
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Forgotten Login Wizard Processing"
    language="java" 
    errorPage="/errors.jsp"
    import="org.apache.log4j.Logger,
        	net.project.base.property.PropertyProvider,
			net.project.notification.NotificationException,
			net.project.admin.ForgottenLoginNotification,
            net.project.base.directory.nativedir.LoginPasswordHelper"
%>
<jsp:useBean id="loginPasswordHelper" class="net.project.base.directory.nativedir.LoginPasswordHelper" scope="session" />
<%
    // Grab the loaded user object
    LoginPasswordHelper.LoginPasswordHelperUser helperUser = loginPasswordHelper.getUser();
%>

<%
    String enteredFirstName = request.getParameter("firstName");
    String enteredLastName = request.getParameter("lastName");
    String enteredJogAnswer = request.getParameter("hintAnswer");
    
    String theAction = request.getParameter("theAction");
    
    if (theAction != null && theAction.equals("next")) {

        if (helperUser.isMatching(enteredFirstName, enteredLastName, enteredJogAnswer)) {
            // All entered information is correct
            // Now send an email containing the login name
    		boolean isNotificationSuccessful = false;

     		ForgottenLoginNotification fln = new ForgottenLoginNotification();
    		fln.init(helperUser.getUser());

    		try {
    			fln.post();
    			isNotificationSuccessful = true;

    		} catch (NotificationException ne) {
    			Logger.getLogger(this.getClass()).error("ForgottenLoginWizardProcessing, notification failed: " + ne + "\n" + fln.getNotificationXML());
    		}

    		if (isNotificationSuccessful) {
                // Go to confirmation page to indicate that a notification was sent
    			pageContext.forward("/registration/nativedir/ForgottenLoginWizardConfirmation.jsp");

    		} else {
    			request.setAttribute("errorMsg", PropertyProvider.get("prm.registration.forgottenloginwizard.error.sendinginfo.message"));
    			pageContext.forward("/registration/nativedir/ForgottenLoginWizard.jsp");
    		}

    	} else {
            // Entered values do not match currently loaded user
    		request.setAttribute("errorMsg", PropertyProvider.get("prm.registration.forgottenloginwizard.error.unabletoverify.message"));
    		pageContext.forward("/registration/nativedir/ForgottenLoginWizard.jsp");
    	}

    } else if (theAction != null && theAction.equals("back")) {
        pageContext.forward("/registration/nativedir/ForgottenInfoWizard.jsp?direction=backward");

    } else {
        // Assumed to be cancel
        pageContext.forward("/registration/ForgottenInfoWizard.jsp?theAction=cancel");
    }

%>

