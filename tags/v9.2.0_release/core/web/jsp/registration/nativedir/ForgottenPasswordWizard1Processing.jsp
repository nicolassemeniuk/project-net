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
|   Processing page for forgotten password
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Forgotten Password Wizard"
    language="java" 
    errorPage="/errors.jsp"
    import="org.apache.log4j.Logger,
        	net.project.base.property.PropertyProvider,
			net.project.notification.NotificationException,
			net.project.admin.ForgottenPasswordNotification,
            net.project.base.directory.nativedir.LoginPasswordHelper"
%>
<jsp:useBean id="loginPasswordHelper" class="net.project.base.directory.nativedir.LoginPasswordHelper" scope="session" />
<%
    // Grab the loaded user object
    LoginPasswordHelper.LoginPasswordHelperUser helperUser = loginPasswordHelper.getUser();
%>

<%
    String enteredLogin = request.getParameter("login");
    String enteredJogAnswer = request.getParameter("hintAnswer");

    String theAction = request.getParameter("theAction");
    
    if (theAction != null && theAction.equals("next")) {
    
        if (helperUser.isMatching(enteredLogin, enteredJogAnswer)) {
            // Matching login and jog answer

    		// REMOVED TO PREVENT RESETTING OF VERIFICATION CODE CAUSE IT IS USED FOR WEBEX
    		//registration.setVerificationCode(registration.generateVerificationCode(6));

    		// SEND A VERIFICATION EMAIL with verification code
    		boolean isNotificationSuccessful = false;

    		// Send email with verification code
     		ForgottenPasswordNotification fpn = new ForgottenPasswordNotification();
    		fpn.init(helperUser.getUser());
    		try {
    			fpn.post();
    			isNotificationSuccessful = true;

    		} catch (NotificationException ne) {
    			Logger.getLogger(this.getClass()).error("ForgottenPasswordWizard1Processing, notification failed: " + ne + "\n" + fpn.getNotificationXML());
    		}

    		if (isNotificationSuccessful) {
                // Go to verification page
    			pageContext.forward("/registration/nativedir/ForgottenPasswordWizard2.jsp");

    		} else {
                // Display error
    			request.setAttribute("errorMsg", PropertyProvider.get("prm.registration.forgottenpasswordwizard.error.sendinginfo.message"));
    			pageContext.forward("/registration/nativedir/ForgottenPasswordWizard1.jsp");
    		}

    	} else {
    		request.setAttribute("errorMsg", PropertyProvider.get("prm.registration.forgottenpasswordwizard.error.unabletoverify.message"));
    		pageContext.forward("/registration/nativedir/ForgottenPasswordWizard1.jsp");
    	}

    } else if (theAction != null && theAction.equals("back")) {
        pageContext.forward("/registration/nativedir/ForgottenInfoWizard.jsp");

    } else {
        // All other actions assumed to be cancel
        pageContext.forward("/registration/ForgottenInfoWizard.jsp?theAction=cancel");
    }
%>
