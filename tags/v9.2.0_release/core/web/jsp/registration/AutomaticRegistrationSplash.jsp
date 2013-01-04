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
    info="New User - Automatic Registration Splash Page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
    net.project.base.property.PropertyProvider,
            net.project.security.domain.UserDomain,
            net.project.base.directory.Directory,
            net.project.base.directory.AuthenticationContext,
            net.project.security.domain.DomainException,
            net.project.base.directory.IDirectoryEntry,
            net.project.resource.IPersonAttributes,
            net.project.admin.RegistrationManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="loginManager" class="net.project.security.login.LoginManager" scope="session" />


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

function cancel() {
    self.location = '<%=SessionManager.getJSPRootURL()%>/Login.jsp';
}

function next() {
    theAction("selectLicense");
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

<form name="autoRegistration" action="<%= SessionManager.getJSPRootURL() %>/registration/AutomaticRegistrationController.jsp" method="post">
	<input type="hidden" name="theAction">
    <input type="hidden" name="fromPage" value="autoRegistrationSplash">
	<div align="center">
<table width=700 cellpadding=0 cellspacing=0 border=0>
<tr>
	<td colspan="4" class="tableHeader"> <display:get name="prm.global.registration.auto.splash.header"/></td>
</tr>
<tr>
	<td class="fieldNonRequired" colspan="4">
		<%=PropertyProvider.get("prm.global.registration.auto.splash.instructions")%>
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
		<tb:button type="cancel" order="0"/>
		<tb:button type="next" label='Continue' order="1" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS space="personal"/>
</body>
</html>
