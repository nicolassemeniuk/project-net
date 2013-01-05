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
	boolean ssoEnabled = PropertyProvider.getBoolean("prm.global.login.sso.allowSSO");
	if (!ssoEnabled) {
		//SSO is disabled
		pageContext.forward("/Login.jsp");
		return;
	}
	
	String remoteUser = request.getHeader("Remote-User");
	if ((remoteUser == null)||(remoteUser.equals(""))) {
		//no header. this is system error.
		pageContext.forward("/sso/NoHeader.jsp");
		return;
	}
	
	
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
    String username = remoteUser;
    String password = "any";
    String userDomain = "1000"; // user should be in Global Domane name space

    // Pass to login manager and complete the login process
    loginManager.setLicenseProperties(net.project.license.system.LicenseProperties.getInstance(request));
    loginManager.createLoginContext(username, password, userDomain);
    LoginStatusCode statusCode = loginManager.completeLogin(false, true);

    // Handle the login status
    // Only in the event of LoginStatusCode.
    if (statusCode.equals(LoginStatusCode.MISSING_PARAMETER)) {
        // username, password or domainID missing
        request.setAttribute("SaSecurityError",loginManager.getErrorMessageHTML());
	    pageContext.forward("/sso/NoAccount.jsp");

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
	    pageContext.forward("/sso/NoAccount.jsp");

    } else if (statusCode.equals(LoginStatusCode.INACTIVE_USER)) {
        // Invalid User
        request.setAttribute("SaSecurityError",loginManager.getErrorMessageHTML());
	    pageContext.forward("/sso/NoAccount.jsp");

    } else if (statusCode.equals(LoginStatusCode.UNCONFIRMED_USER)) {
        // Unconfirmed User
        request.setAttribute("SaSecurityError",loginManager.getErrorMessageHTML());
	    pageContext.forward("/sso/NoAccount.jsp");

    } else if (statusCode.equals(LoginStatusCode.INVALID_LICENSE) || 
               statusCode.equals(LoginStatusCode.SUCCESS)) {

        // User is authenticated successfully
        // Need to populate the user in the session since it may be needed
        // by the license updater
        User user = loginManager.getAuthenticatedUser();
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
