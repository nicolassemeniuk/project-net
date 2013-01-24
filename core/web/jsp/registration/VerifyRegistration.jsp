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
    info="New User Registration page 4"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
    net.project.base.property.PropertyProvider"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<%------------------------------------------------------------------------
  -- Get Verification Code from URL if able
  ----------------------------------------------------------------------%>

<%
      String emailAddress = "";
	  String verificationCode = "";

	  // get email from url or bean
	  if (request.getParameter("email")!=null) emailAddress = request.getParameter("email");

	  // get verification code from url or bean
	  if (request.getParameter("code")!=null) verificationCode = request.getParameter("code");
%>



<html>
<head>
<title><%=PropertyProvider.get("prm.registration.verifypage.title")%></title>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<template:getSpaceCSS space="personal"/>

<template:import type="javascript" src="/src/checkEmail.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/cookie.js" />
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/checkAlphaNumeric.js" />
<template:import type="javascript" src="/src/browser.js" />


<script language="javascript">
// Do a cookie check, if javascript is turned off the user will be notifed
today=new Date();
SetCookie("testcookie","Cookies On!",null,"/");
if(GetCookie("testcookie")==null)
	top.location.href = "../CookieRequired.jsp"
	
detectBrowser("../BadBrowser.jsp");
	
function validateForm(frm){
    if (!checkEmail(frm.ecom_ShipTo_Online_Email,'<%=PropertyProvider.get("prm.registration.verify.emailmustbevalid.message")%>')) return false;
    //    if (!checkTextbox(frm.ecom_ShipTo_Online_Email,"You must enter the Verfication Code that was emailed to you")) return false; 
    //    if (!checkTextbox(frm.verificationCode,"You must enter the Verfication Code that was emailed to you")) return false; 
    frm.theAction.value = 'next';
    frm.submit();
}

function cancel() {
    self.location = '<%=SessionManager.getJSPRootURL()%>/Login.jsp';
}

function submit() {
    processForm();
}

function processForm() {
	if(validateForm(document.registration)) {
		document.registration.submit();
    }
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/HelpDesk.jsp?page=registration";
	openwin_help(helplocation);
}

</script>
</head>

<body id="bodyWithFixedAreasSupport" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0 class="main">

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

<form name="registration" action="VerifyRegistrationProcessing.jsp" method="post" onSubmit="return validateForm(this);">
	<input type="hidden" name="theAction">
	<div align="center">
<table width=700 cellpadding=0 cellspacing=0 border=0>
<tr>
	<td colspan="4" class="tableHeader"><%=PropertyProvider.get("prm.registration.verify.completing.message")%></td>
</tr>
<tr>		    	
	<td class="fieldNonRequired" colspan="4">
		<%=PropertyProvider.get("prm.registration.verify.emailvalidation.message")%>
		<br><br>
		<%=PropertyProvider.get("prm.registration.verify.reghelp.1.text")%><a href="javascript:help();"><%=PropertyProvider.get("prm.registration.verify.reghelp.2.link")%></a><%=PropertyProvider.get("prm.registration.verify.reghelp.3.text")%>
	</td>
</tr>
<tr>
	<td colspan="4">
		<noscript><b><%=PropertyProvider.get("prm.registration.turnonjavascript.1.text")%><a href="<%= SessionManager.getAppURL() %>/help/HelpDesk.jsp?page=browser_requirements"><%=PropertyProvider.get("prm.registration.turnonjavascript.2.link")%></a><%=PropertyProvider.get("prm.registration.turnonjavascript.3.text")%></b></noscript>
	</td>
</tr>


<%
if (session.getValue("errorMsg") != null)
{
%>
	<tr align="left">
		<td colspan="4">
			<font color="#FF0000"><b><%= session.getValue("errorMsg") %></b></font>
		</td>
	</tr>
<%
}
%>
	<tr class="actionBar">
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><%=PropertyProvider.get("prm.registration.verify.channel.verification.title")%></td>		
		<td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>	
	<tr>
	<td nowrap>&nbsp;</td>
		<td nowrap class="fieldRequired"><%=PropertyProvider.get("prm.registration.verify.email.label")%></td>
		
		<td nowrap><input type="text" name="ecom_ShipTo_Online_Email" size="40" maxlength="240" value="<%= emailAddress %>"></td>
        	<td nowrap>&nbsp;</td>
	</tr>
	<tr>
	<td nowrap>&nbsp;</td>
		<td nowrap class="fieldRequired"><%=PropertyProvider.get("prm.registration.verify.code.label")%></td>
		<td nowrap>
			<input type="text" name="verificationCode" size="40" maxlength="80" value="<%= verificationCode %>">
		</td>
	<td nowrap>&nbsp;</td>        
	</tr>
	</table>
</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" label='<%=PropertyProvider.get("prm.registration.verify.button.later.label")%>' order="0"/>
		<tb:button type="submit" label='<%=PropertyProvider.get("prm.registration.verify.button.now.label")%>' order="1" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS space="personal"/>
</body>
</html>
