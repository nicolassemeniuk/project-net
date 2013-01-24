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
    info="Registration Terms of Use"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<html>
<head>

<title><%=PropertyProvider.get("prm.registration.register.pagetitle")%></title>
	
<%-- Import CSS --%>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<template:getSpaceCSS space="personal"/>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/browser.js" />
<template:import type="javascript" src="/src/cookie.js" />

<script language="javascript" type="text/javascript">
	// Do a cookie check, if javascript is turned off the user will be notifed
	today=new Date();
	SetCookie("testcookie","Cookies On!",null,"/");
	if(GetCookie("testcookie")==null)
		top.location.href = "../CookieRequired.jsp"

	detectBrowser("../BadBrowser.jsp");

	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
    function setup() {
        theForm = self.document.forms["registration"];
    	isLoaded = true;
    }

	function decline() {
		theAction("decline");
        theForm.submit();
	}
	
	function accept() {
        theAction("accept");
        theForm.submit();
	}
	
</script>

</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">

<div id="topframe">
<%-- Terms of Use page header --%>
<table width="100%" cellpadding="1" cellspacing="0" border="0">
 	<tr>
 		<td>&nbsp;<display:img src="@prm.global.registration.header.logo" alt="" border="0" /></td>
 		<td align="center" class="regBanner"><display:get name="prm.global.registration.termsofuse.banner" />&nbsp;</td>
 	</tr>
</table>
</div>

<div id='content'>

<br>

<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/registration/TOUController.jsp"%>" method="post">
    <input type="hidden" name="theAction">
    <input type="hidden" name="fromPage" value="tou">

<div align="center">
<table width="80%" cellpadding=0 cellspacing=0 border=0>
<tr>
    <td align="center">

<%-- Terms of Use in textbox --%>
<h2><display:get name="prm.global.registration.termsofuse.header" /></h2>
<textarea cols="100" rows="20" WRAP="Virtual" readonly><display:get name="prm.global.legal.termsofuse.registration" /></textarea>

<%-- Link to printable version of Terms above --%>
<p><%=PropertyProvider.get("prm.registration.register.licenseagreement.1.text")%><a href="<%=PropertyProvider.get("prm.global.legal.eula.href")%>"><%=PropertyProvider.get("prm.registration.register.licenseagreement.2.link")%></a><%=PropertyProvider.get("prm.registration.register.licenseagreement.3.text")%>

    </td>
</tr>
</table>
</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action"> 
		<tb:button type="submit" label='<%=PropertyProvider.get("prm.registration.register.button.accept.label")%>' function="javascript:accept();" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS space="personal"/>
</body>
</html>
