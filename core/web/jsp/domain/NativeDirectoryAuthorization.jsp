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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
| Provides capture of parameters needed to authorize registration
| when registering against the native directory
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Registration Native Directory Authorization"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
        	net.project.base.property.PropertyProvider,
            net.project.admin.RegistrationBean"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="nativeDirectoryEntry" class="net.project.base.directory.nativedir.NativeRegistrationEditor" scope="request" />
<%
	String wizardMode = (String)pageContext.getAttribute("WizardMode", pageContext.SESSION_SCOPE);
    RegistrationBean.RegistrationResult registrationResult = (RegistrationBean.RegistrationResult) request.getAttribute("registrationResult");
    if (registrationResult == null) {
        registrationResult = new RegistrationBean.RegistrationResult();
    }
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkEmail.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/checkAlphaNumeric.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
    function setup() {
        theForm = self.document.forms["registration"];
        focusFirstField(theForm);
    	isLoaded = true;
    }

    function validateForm(frm) {
        if (!checkTextbox(frm.login,'<display:get name="prm.global.profile.usernamerequired.message" />')) return false;
        if (!checkLength(frm.login,'<display:get name="prm.registration.userinfo.loginnamerange.message" />')) return false;
        if (!checkAlphaNumeric(frm.login,'<display:get name="prm.global.profile.usernamealphanumeric.message" />')) return false;
        if (!checkLength(frm.password,6,'<display:get name="prm.registration.userinfo.passwordrequired.message" />')) return false;
        if (!checkPasswords(frm.password, frm.password_2, '<display:get name="prm.global.profile.passwordsmatchvalidation.message" />')) return false;
        if (!checkEmail(frm.ecom_ShipTo_Online_Email, '<display:get name="prm.global.profile.emailvalidation.message" />')) return false;
        if (!checkPasswords(frm.ecom_ShipTo_Online_Email, frm.ecom_ShipTo_Online_Email2, '<display:get name="prm.global.profile.emailsmatchvalidation.message" />')) return false;
        if (!checkTextbox(frm.clearTextHintPhrase, '<display:get name="prm.global.profile.jogquestionvalidation.message" />')) return false;
        if (!checkTextbox(frm.clearTextHintAnswer, '<display:get name="prm.global.profile.joganswervalidation.message" />')) return false;
        return true;
    }

    function processForm() {
        if (validateForm(theForm)) {
            theForm.submit();
        }
    }

    function next() {
        theAction("next");
        processForm();
    }

    function back() {
        theAction("back");
        theForm.submit();
    }
    
    function cancel() { 
		
		<% 
			if(wizardMode != null && wizardMode.equals("PopUp")) {
		%>
			self.close();	
		<%
		 	} else {
		%>
			var m_url = JSPRootURL + "/NavigationFrameset.jsp";
			self.document.location = m_url;
		
		<%
			}
		%>	
	}

</script>
</head>
    
<%------------------------------------------------------------------------
  -- Start of Form Body
  ----------------------------------------------------------------------%>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id='content'>

<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/domain/NativeDirectoryAuthorizationController.jsp"%>" method="post">
	<input type="hidden" name="theAction">
    <input type="hidden" name="fromPage" value="nativeAuthorization">
	<input type="hidden" name="module" value="<%= net.project.base.Module.PERSONAL_SPACE %>">

<div align="center">
<table width="80%" cellpadding=0 cellspacing=0 border=0>
	<tr>
		<td colspan="4" class="tableHeader">
		<display:get name="prm.global.display.requiredfield" />
		</td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>

	<tr class="actionBar">
		<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width="8" height="27" alt="" border="0"></td>
		<td colspan="2" valign="middle" class="actionBar"><display:get name="prm.domain.nativedirectoryauthorization.channel.loginifo.title" /></td>
		<td width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width="8" height="27" alt="" border="0"></td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr align="left">
        <td>&nbsp;</td>
        <td colspan="2">
            <display:get name="prm.personal.domainmigration.nativelogin.directions" />
        </td>
    </tr>
	<tr><td colspan="4">&nbsp;</td></tr>

<%  
    // Display any errors from registration result
    // Not currently sophisticated enough to flag each field error
    // due to these kinds of errors
    if (!registrationResult.isSuccess()) {
%>
	<tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="fieldWithError">
            <%=registrationResult.getErrorMessagesFormatted()%>            
        </td>
    </tr>
	<tr><td colspan="4">&nbsp;</td></tr>
<%  } %>

	<tr align="left">
	    <td nowrap>&nbsp;</td>
        <td nowrap class="fieldRequired" align="right" valign="top">
            <%=nativeDirectoryEntry.getFlagError("login", PropertyProvider.get("prm.global.registration.login.username"))%>:&nbsp;&nbsp;
        </td>
		<td class="tableContent">
    	    <input type="text" name="login" size="20" maxlength='<display:get name="prm.global.profile.username.maxsize" />' value="<jsp:getProperty name="nativeDirectoryEntry" property="login" />">
            <br><%=nativeDirectoryEntry.getErrorMessage("login")%>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	<tr align="left">
	    <td nowrap>&nbsp;</td>
        <td nowrap class="fieldRequired" align="right" valign="top">
            <%=nativeDirectoryEntry.getFlagError("password", PropertyProvider.get("prm.global.registration.login.password"))%>:&nbsp;&nbsp;
        </td>
	    <td class="tableContent">
    		<input type="password" name="password" size="20" maxlength="16" value="">
            <br><%=nativeDirectoryEntry.getErrorMessage("password")%>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	<tr align="left">
	    <td nowrap>&nbsp;</td>
		<td nowrap class="fieldRequired" align="right" valign="top">
            <%=nativeDirectoryEntry.getFlagError("password2", PropertyProvider.get("prm.global.registration.login.password.retype"))%>:&nbsp;&nbsp;
        </td>			
	    <td class="tableContent">
			<input type="password" name="password_2" size="20" maxlength="16" value="">
            <br><%=nativeDirectoryEntry.getErrorMessage("password2")%>
		</td>
	    <td nowrap>&nbsp;</td>
	</tr>			
	<tr><td colspan="4">&nbsp;</td></tr>

	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldRequired" align="right" valign="top">
            <%=nativeDirectoryEntry.getFlagError("email", PropertyProvider.get("prm.global.registration.personal.email"))%>:&nbsp;&nbsp;
        </td>
		<td class="tableContent">
			<input type="text" name="ecom_ShipTo_Online_Email" size="40" maxlength="240" value="<jsp:getProperty name="nativeDirectoryEntry" property="email" />">
            <br><%=nativeDirectoryEntry.getErrorMessage("email")%>
        </td>
		<td nowrap>&nbsp;</td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td nowrap class="fieldRequired" align="right" valign="top">
            <%=nativeDirectoryEntry.getFlagError("email2", PropertyProvider.get("prm.global.registration.personal.email.retype"))%>:&nbsp;&nbsp;
        </td>
        <td class="tableContent">
			<input type="text" name="ecom_ShipTo_Online_Email2" size="40" maxlength="240" value="<jsp:getProperty name="nativeDirectoryEntry" property="emailRetype" />">
            <br><%=nativeDirectoryEntry.getErrorMessage("email2")%>
        </td>
		<td nowrap>&nbsp;</td>
    </tr>
	<tr><td colspan="4">&nbsp;</td></tr>
    
	<%-- Jog Question and Answer in case the user forgets his password --%>
	<tr align="left">
		<td colspan="4">
		    <display:get name="prm.global.registration.login.jog.example" />
		</td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap align="right" class="fieldRequired" valign="top">
            <%=nativeDirectoryEntry.getFlagError("clearTextHintPhrase", PropertyProvider.get("prm.global.registration.login.jog.question"))%>:&nbsp;&nbsp;
        </td>
		<td class="tableContent">
			<input type="text" name="clearTextHintPhrase" size="40" maxlength="80" value="<jsp:getProperty name="nativeDirectoryEntry" property="clearTextHintPhrase" />">
            <br><%=nativeDirectoryEntry.getErrorMessage("clearTextHintPhrase")%>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
    <tr align="left">
	    <td nowrap>&nbsp;</td>
	    <td nowrap align="right" class="fieldRequired" valign="top">
            <%=nativeDirectoryEntry.getFlagError("clearTextHintAnswer", PropertyProvider.get("prm.global.registration.login.jog.answer"))%>:&nbsp;&nbsp;
        </td>			
	    <td class="tableContent">
		    <input type="text" name="clearTextHintAnswer" size="20" maxlength="80" value="">
            <br><%=nativeDirectoryEntry.getErrorMessage("clearTextHintAnswer")%>
	    </td>
	    <td nowrap>&nbsp;</td>
	</tr>
</table>
</div>

<tb:toolbar style="action" showLabels="true" >
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="back" />
		<tb:button type="next" />
	</tb:band>
</tb:toolbar>
	
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
<%nativeDirectoryEntry.clearErrors();%>
