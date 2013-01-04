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
    info="Registration LDAP Directory Authorization"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
        	net.project.base.property.PropertyProvider,
            net.project.admin.RegistrationBean,
            net.project.util.StringUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%
    // Other error messages from LDAP directory authorization
    String errorMessage = (String) request.getAttribute("errorMessage");
    boolean hasErrors = (errorMessage != null && errorMessage.trim().length() > 0);
	
	String wizardMode = (String)pageContext.getAttribute("WizardMode" , pageContext.SESSION_SCOPE);

    RegistrationBean.RegistrationResult registrationResult = (RegistrationBean.RegistrationResult) request.getAttribute("registrationResult");
    if (registrationResult == null) {
        registrationResult = new RegistrationBean.RegistrationResult();
    }
%>

<html>
<head>
<title><display:get name="prm.domain.ldapdirectoryauthorization.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/window_functions.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

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
        if (!checkTextbox(frm.login,'<display:get name="prm.domain.ldapdirectoryauthorization.usernamerequired.message" />')) return false;
        if (!checkTextbox(frm.password,'<display:get name="prm.domain.ldapdirectoryauthorization.passwordrequired.message" />')) return false;
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
<body class="main" onLoad="setup();">

<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/domain/LDAPDirectoryAuthorizationController.jsp"%>" method="post">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%= net.project.base.Module.PERSONAL_SPACE %>">
    <input type="hidden" name="fromPage" value="LDAPAuthorization">

<div align="center">	
	<table width="80%" cellpadding=0 cellspacing=0 border=0>

	<tr><td colspan="4">&nbsp;</td></tr>
	<tr>
		<td colspan="4" class="tableHeader">
		<display:get name="prm.global.display.requiredfield" />
		</td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr class="actionBar">
		<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width="8" height="27" alt="" border="0"></td>
		<td colspan="2" valign="middle" class="actionBar"><display:get name="prm.domain.ldapdirectoryauthorization.channel.authenicationinformation.title" /></td>
		<td width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width="8" height="27" alt="" border="0"></td>
	</tr>
	<tr align="left">
	    <td nowrap>&nbsp;</td>
        <td colspan="2"><display:get name="prm.domain.ldapdirectoryauthorization.domainmigration.instruction" /></td>
		<td nowrap>&nbsp;</td>
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
<%
    }
    // Display other errors from ldap authorization checks
    if (hasErrors) {
%>
	<tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="fieldWithError">
            <%=errorMessage%>
        </td>
    </tr>
	<tr><td colspan="4">&nbsp;</td></tr>
<%  } %>
    
	<tr align="left">
	    <td nowrap>&nbsp;</td>
        <td nowrap class="fieldRequired" align="right"><display:get name="prm.global.registration.login.username" />:&nbsp;&nbsp;</td>
		<td nowrap>
			<%String loginName = registration.getLogin();%>
    	    <input type="text" name="login" size="20" maxlength='<display:get name="prm.global.profile.username.maxsize" />' value='<%=(StringUtils.isEmpty(loginName) || "null".equals(loginName)) ? "" : loginName%>'>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	<tr align="left">
	    <td nowrap>&nbsp;</td>
        <td nowrap class="fieldRequired" align="right"><display:get name="prm.global.registration.login.password" />:&nbsp;&nbsp;</td>
	    <td nowrap>
    		<input type="password" name="password" size="20" maxlength="16" value="">
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
    <tr>
	    <td colspan="4">
<tb:toolbar style="action" showLabels="true">
                <tb:band name="action">
                    <tb:button type="cancel" />
                    <tb:button type="back" />
            		<tb:button type="next" />
            	</tb:band>
</tb:toolbar>
		</td>
    </tr>
</table>
</div>
	
</form>
</body>
<template:getSpaceJS />
</html>
