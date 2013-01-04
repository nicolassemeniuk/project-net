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
    info="New User - Automatic Registration Finish Page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.security.domain.UserDomain,
            net.project.base.directory.Directory,
            net.project.base.directory.AuthenticationContext,
            net.project.admin.RegistrationBean,
            net.project.security.domain.DomainException,
            net.project.base.directory.IDirectoryEntry,
            net.project.admin.RegistrationException,
            net.project.admin.AutomaticRegistrationException,
            net.project.license.system.LicenseProperties,
            net.project.resource.PersonStatus,
            net.project.security.User"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="loginManager" class="net.project.security.login.LoginManager" scope="session" />

<%
    UserDomain domain = new UserDomain (loginManager.getDomainID());
    domain.load();
    Directory directory = Directory.getInstance(domain.getDirectoryProviderType(), domain.getDirectoryConfiguration());
    directory.setAuthenticationContext(new AuthenticationContext(loginManager.getLoginContext()));

    // first, verify once again that this domain supports automatic registration
    if (!domain.allowsAutomaticRegistration()) {
        throw new DomainException ("This Domain does not support automatic registration");
    }

    try {

        // next, get an authenticated directory entry for this user.
        // This means that we will once again authenticate this user against the directory -- and assuming success
        // will use the information returned to register them.  Note, the user must have successfully authenticated
        // by this point, so this shouldn't be an issue other than in an exceptional case.
        IDirectoryEntry newUserEntry = directory.getAuthenticatedDirectoryEntry();

        if (!PropertyProvider.getBoolean("prm.global.login.sso.allowSSO") && !directory.isAuthenticated()) {
            throw new DomainException("The user has not successfully authenticated against the domain");
        }

        RegistrationBean.RegistrationResult result;

        // do not validate the registration as validation may fail for auto-registration
        registration.setValidateRegistration(false);
        // Further, we already know the username of the person -- and this username has been
        // used to succesfully authenticate.
        registration.setLogin(loginManager.getLoginContext().getUsername());

        // if the user domain does not require verification for registration
        // set the user status accordingly

        if (!domain.isVerificationRequired()) {
            registration.setStatus(PersonStatus.ACTIVE);
        }

        registration.setDirectoryEntry(newUserEntry);
        registration.populateLocalizationDefaults();
        
     	registration.populateFromDirectoryEntry();
        
        // one more availability check
        result = registration.checkAvailability();

        if (result.isSuccess()) {
            LicenseProperties licenseProps = LicenseProperties.getInstance();

            registration.completeRegistration(licenseProps);
            
          // Set person status active 
			User user = new User();
			user.loadForEmail(registration.getEmail());
			if (user.getStatus().equals(PersonStatus.UNCONFIRMED) || user.getStatus().equals(PersonStatus.UNREGISTERED)) {
				user.setStatus(PersonStatus.ACTIVE);
                user.store();
			}

        } else {
            throw new AutomaticRegistrationException ("There is already a user registered with the specified email address: " +
                    registration.getEmail());
        }

    } catch (RegistrationException re) {
        throw new DomainException ("There was an issue with the registration process.", re);
    }
%>


<html>
<head>
<title><%=PropertyProvider.get("prm.registration.verifypage.title")%></title>

<%------------------------------------------------------------------------
-- Import CSS and Javascript Files
----------------------------------------------------------------------%>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<template:getSpaceCSS space="personal"/>

<template:import type="javascript" src="/src/cookie.js" />
<template:import type="javascript" src="/src/browser.js" />

<script language="javascript">
// Do a cookie check, if javascript is turned off the user will be notifed
today=new Date();
SetCookie("testcookie","Cookies On!",null,"/");
if(GetCookie("testcookie")==null)
top.location.href = "../CookieRequired.jsp"



detectBrowser("../BadBrowser.jsp");
var theForm;

function setup() {
theForm = self.document.forms["autoRegistration"];
}

function next() {
theAction("autoRegistrationFinish");
theForm.submit();
}

function help() {
var helplocation="<%= SessionManager.getJSPRootURL() %>/help/HelpDesk.jsp?page=registration";
openwin_help(helplocation);
}

</script>
</head>

<body id="bodyWithFixedAreasSupport" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0 class="main" onload="setup();">

<div id="topframe">
	<table width="100%" cellpadding="1" cellspacing="0" border="0">
		<tr>
			<td>&nbsp;<display:img src="@prm.global.registration.header.logo" alt="" border="0" /></td>
			<td align="right" class="regBanner"><display:get name="prm.global.registration.main.banner" />&nbsp;</td>
		</tr>
	</table>
</div>

<div id='content'>

<br />

<form name="autoRegistration" action="AutomaticRegistrationController.jsp" method="post">
<input type="hidden" name="theAction">
<input type="hidden" name="fromPage" value="autoRegistrationFinish">
<div align="center">
<table width=700 cellpadding=0 cellspacing=0 border=0>
<tr>
<td colspan="4" class="tableHeader"> <display:get name="prm.global.registration.auto.splash.header"/></td>
</tr>
<tr>
<td class="fieldNonRequired" colspan="4">
<display:get name="prm.global.registration.auto.finish"/>
<br><br>
		<%=PropertyProvider.get("prm.registration.verify.reghelp.1.text")%><a href="javascript:help();"><%=PropertyProvider.get("prm.registration.verify.reghelp.2.link")%></a><%=PropertyProvider.get("prm.registration.verify.reghelp.3.text")%>
        </td>
</tr>
<tr>
<td colspan="4">
<noscript><b><%=PropertyProvider.get("prm.registration.turnonjavascript.1.text")%><a href="<%= SessionManager.getAppURL() %>/help/HelpDesk.jsp?page=browser_requirements"><%=PropertyProvider.get("prm.registration.turnonjavascript.2.link")%></a><%=PropertyProvider.get("prm.registration.turnonjavascript.3.text")%></b></noscript>
</td>
</tr>
</table>
</div>
            
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="next" label='Proceed to Login' order="1" />
	</tb:band>
</tb:toolbar>
            
</form>

<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS space="personal"/>
</body>
</html>
