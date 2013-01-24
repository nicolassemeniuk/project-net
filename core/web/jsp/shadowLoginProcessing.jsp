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
    info="Login Processing - this page does not emit any output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.brand.BrandManager,
            net.project.security.SessionManager,
            net.project.security.User,
			net.project.util.DateUtils,
			net.project.security.domain.DomainMigrationStatus,
            net.project.security.login.LoginStatusCode,
			net.project.space.PersonalSpaceBean,
            java.util.Enumeration,
            net.project.security.login.LoginContext"
%>


<%------------------------------------------------------------------------
  --  The following section kills any existing session 
  --  objects to clean up from last login
  ----------------------------------------------------------------------%>
<%
    // There are some session values that should exist through the kill,
    // temporarily store them.
    String requestedPage = (String) session.getAttribute("requestedPage");
    String scheme = (String) session.getAttribute("SiteScheme");
    String host = (String) session.getAttribute("SiteHost");

    for (Enumeration e = session.getAttributeNames(); e.hasMoreElements(); ) {
        String nextName = (String) e.nextElement();
        if (!(nextName.equals(BrandManager.BRAND_MANAGER_SESSION_OBJECT_NAME) ||
			  nextName.equals(PropertyProvider.getPropertyBundleSessionName()))) {
             session.removeAttribute(nextName);
        }
    }

    session.setAttribute("requestedPage", requestedPage);
    session.setAttribute("SiteScheme", scheme);
    session.setAttribute("SiteHost", host);
%>
 

<%------------------------------------------------------------------------
  --  Necessary useBeans
  --  IMPORTANT: this must follow the session killer block
  ----------------------------------------------------------------------%>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="loginManager" class="net.project.security.login.LoginManager" scope="request" /> 
<jsp:useBean id="userDomainMigrationManager" class="net.project.security.domain.UserDomainMigrationManager" scope="session" /> 

<%------------------------------------------------------------------------
  --  Complete the login process
  ----------------------------------------------------------------------%>
<%    
    // Grab parameters from Login form
    String username = request.getParameter ("J_USERNAME");
    String password = request.getParameter ("J_PASSWORD");
    String userDomain = request.getParameter("userDomain");
	
	// bfd-2985 by Avinash Bhamare on 3rd Feb 206
	// get the hidden shadow username
	String shadowUser = request.getParameter("shadowUser");
	String shadowUserDomain = request.getParameter("shadowUserDomain");

    // Pass to login manager and complete the login process
    loginManager.setLicenseProperties(net.project.license.system.LicenseProperties.getInstance(request));
    loginManager.createLoginContext(username, password, userDomain);
    LoginStatusCode statusCode = loginManager.completeLogin();

    // Handle the login status
    // Only in the event of LoginStatusCode.
    if (statusCode.equals(LoginStatusCode.MISSING_PARAMETER)) {
        // username, password or domainID missing
        request.setAttribute("SaSecurityError",loginManager.getErrorMessageHTML());
	    pageContext.forward("/Login.jsp");

    } else if (statusCode.equals(LoginStatusCode.AUTOMATIC_REGISTRATION)) {
        // User attempted to authenticate against a domain for which they were not registered.
        // however, the domain does support auto-registration and their credentials were validated.
        // forward to finalizer for auto registration for licensing + final step(s)

        session.removeAttribute("loginManager");
        session.setAttribute("loginManager", loginManager);

        pageContext.forward("/registration/AutomaticRegistrationController.jsp?fromPage=login");

    } else if (statusCode.equals(LoginStatusCode.AUTHENTICATION_ERROR)) {
        // Error authenticating
        request.setAttribute("SaSecurityError",loginManager.getErrorMessageHTML());
	    pageContext.forward("/Login.jsp");

    } else if (statusCode.equals(LoginStatusCode.INACTIVE_USER)) {
        // Invalid User
        request.setAttribute("SaSecurityError",loginManager.getErrorMessageHTML());
	    pageContext.forward("/Login.jsp");

    } else if (statusCode.equals(LoginStatusCode.UNCONFIRMED_USER)) {
        // Unconfirmed User
        request.setAttribute("SaSecurityError",loginManager.getErrorMessageHTML());
	    pageContext.forward("/Login.jsp");

    } else if (statusCode.equals(LoginStatusCode.INVALID_LICENSE) || 
               statusCode.equals(LoginStatusCode.SUCCESS)) {

	// This page is only for performing shadow activity for administrator.
	// check that the user is admin or not... now
	User user = loginManager.getAuthenticatedUser();
	if(!user.isApplicationAdministrator())	{
        // Non admin user is trying to use shadow functionlity
        request.setAttribute("SaSecurityError",loginManager.getErrorMessageHTML());
	    pageContext.forward("/shadowLogin.jsp");
	}

    // Pass to login manager and complete the shadowLogin process for shadow user
	loginManager = new net.project.security.login.LoginManager();
	loginManager.setLicenseProperties(
						net.project.license.system.LicenseProperties.getInstance(request));
    loginManager.createLoginContext(shadowUser, password, shadowUserDomain);
    statusCode = loginManager.completeLogin(true);

		// User is authenticated successfully
        // Need to populate the user in the session since it may be needed
        // by the license updater
        user = loginManager.getAuthenticatedUser();
        //session.removeAttribute("user");
        session.setAttribute("user", user);
        if (statusCode.equals(LoginStatusCode.INVALID_LICENSE)) {
            // Error checking license / no license found
    	    pageContext.forward("/login/LicenseHandler.jsp");

        } else {
            // Continue to log in

    		// Create personal space		
    		PersonalSpaceBean personalSpace = new PersonalSpaceBean();
            personalSpace.setID(user.getID());
            personalSpace.load();
            //session.removeAttribute("personalSpace");
            session.setAttribute("personalSpace", personalSpace);

            user.setCurrentSpace (personalSpace);

    		if(PropertyProvider.getBoolean("prm.global.domainmigration.isenabled") && !user.isApplicationAdministrator())  {

    			userDomainMigrationManager.setUser(user);
    			userDomainMigrationManager.load();			
				
    			int domainMigrationStatus = userDomainMigrationManager.getUserMigrationStatus();

    			if(domainMigrationStatus == DomainMigrationStatus.NEVER_STARTED) {

    				if(userDomainMigrationManager.isDomainMigrationSupported()) {
    			 		pageContext.forward("/domain/UserDomainMigration1.jsp?module="+net.project.base.Module.PERSONAL_SPACE);
    				} else {
    					pageContext.forward("/domain/UserDomainMigration1Processing?module="+net.project.base.Module.PERSONAL_SPACE+"&selected="+DomainMigrationStatus.NOT_SUPPORTED);
    				}	
				
    			} else if ( domainMigrationStatus == DomainMigrationStatus.REMIND_LATER) {

    				java.util.Date lastActivityDate = userDomainMigrationManager.getLastActivityDate();

    				if(lastActivityDate != null && DateUtils.daysBetween(new java.util.Date() , lastActivityDate ) > PropertyProvider.getInt("prm.global.domainmigration.remindme.value") )
    					pageContext.forward("/domain/UserDomainMigration1.jsp?module="+net.project.base.Module.PERSONAL_SPACE);
    			}

    		}		    

    		//----------------------------------------------------------------------
    		//  REDIRECT                                                            
    		//  Check to see if the user requested a specific page before being     
            //  directed thru the Login page.                                       
    		//  Only certain pages support this redirection, make sure the requested
            //  page is allowed.                                                    
    		//  If redirection is not allowed, the user will be taken to their      
            //  Personal Page after login.                                          
    		//----------------------------------------------------------------------
    		requestedPage = (String) session.getAttribute("requestedPage");
    		if (requestedPage != null) {
    			System.out.println("DEBUG [LoginProcessing.jsp] Page Redirect requested: " + requestedPage);
    			if (!(requestedPage.startsWith(SessionManager.getJSPRootURL() + "/calendar/MeetingManager.jsp") || 
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/project/Main.jsp") || 
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/project/ProjectRegister.jsp") || 
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/business/Main.jsp") ||
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/business/BusinessRegister.jsp") ||
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/servlet/ScheduleController/TaskView") ||
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/discussion/GroupView.jsp") ||
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/admin/Main.jsp") ||
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/admin/ApplicationRegister.jsp") ||
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/configuration/Main.jsp") ||
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/configuration/ConfigurationRegister.jsp") ||
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/servlet/DownloadDocument") ||
                      requestedPage.startsWith(SessionManager.getJSPRootURL() + "/document/PropertyFrameset.jsp")
				 )) {

    				System.out.println("DEBUG [LoginProcessing.jsp] Removing redirect page.");
    				session.removeAttribute("requestedPage");
    			}
    		}

    		//----------------------------------------------------------------------
    		//  LOAD FRAMESET                                                       
    		//  The frameset will figure out if the main page should be the default 
            //  personal Space or a page cached prior to authentication.                           
    		//----------------------------------------------------------------------
    		HttpSession mySess=request.getSession();
    		mySess.setAttribute("insideHelp","yes");
    		response.sendRedirect(SessionManager.getJSPRootURL() + "/NavigationFrameset.jsp");
        }    

	} else {
        // Unknown login status code
        throw new net.project.base.PnetException("Unhandled login status " + statusCode + " during login.");
    }
%>
