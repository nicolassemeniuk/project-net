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
    info="New User - Automatic Registration Controller"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.admin.RegistrationManager,
            net.project.base.compatibility.Compatibility"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="loginManager" class="net.project.security.login.LoginManager" scope="session" />

<%


    // Capture the page from which a submit was performed
    // and the action performed by that submit
    String fromPage = request.getParameter("fromPage");
    String theAction = request.getParameter("theAction");

    // FLOW -> Login -> authenticate -> auto registration -> license selection -> OK Page -> personal space

    if (fromPage != null && fromPage.equals("login")) {

        registration.reset();
        registration.setStarted(true);
        pageContext.forward("/registration/AutomaticRegistrationSplash.jsp");

    } else if (fromPage != null && fromPage.equals("autoRegistrationSplash")) {

   	 	RegistrationManager.validateAutomaticRegistration(loginManager);
   	  	registration.setUserDomain(loginManager.getDomainID());

        if (theAction != null && theAction.equals("selectLicense")) {
            pageContext.forward("/registration/LicenseController.jsp?fromPage=autoRegistration&theAction=selectLicense&domainSource=registrationBean");
        } else {
            pageContext.forward("/registration/AutomaticRegistrationSplash.jsp");
        }


    } else if (fromPage != null && fromPage.equals("licenseController")) {

        pageContext.forward("/registration/AutomaticRegistrationFinish.jsp");
    }
    else if (fromPage != null && fromPage.equals("autoRegistrationFinish")) {

        
        pageContext.forward("/LoginProcessing.jsp");

    } else {
        response.sendRedirect("/Login.jsp");
    }

%>