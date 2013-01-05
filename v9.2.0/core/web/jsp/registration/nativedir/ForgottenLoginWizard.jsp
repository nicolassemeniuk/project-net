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
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Forgotten Login Wizard"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.base.directory.nativedir.LoginPasswordHelper"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="loginPasswordHelper" class="net.project.base.directory.nativedir.LoginPasswordHelper" scope="session" />

<%
    // Grab the loaded user object
    LoginPasswordHelper.LoginPasswordHelperUser helperUser = loginPasswordHelper.getUser();
%>
<html>
<head>
<title><%=PropertyProvider.get("prm.registration.forgottenloginwizardpage.title")%></title>

<%-- Import CSS --%>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<template:getSpaceCSS space="personal"/>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />


<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
    function setup() {
        theForm = self.document.forms["forgotten"];
        focusFirstField(theForm);
    	isLoaded = true;
    }

    function validate() {
        if (!checkTextbox(theForm.firstName, '<%=PropertyProvider.get("prm.registration.forgottenloginwizard.firstname.message")%>')) return false;
        if (!checkTextbox(theForm.lastName, '<%=PropertyProvider.get("prm.registration.forgottenloginwizard.lastname.message")%>')) return false;
        if (!checkTextbox(theForm.hintAnswer, '<%=PropertyProvider.get("prm.registration.forgottenloginwizard.hint.message")%>')) return false;
        return true;
    }
    
    function next() {
        if (validate()) {
            theAction("next");
            theForm.submit();
        }
    }

    function back() {
        theAction("back");
        theForm.submit();
    }
    
    function cancel() {
        self.document.location = JSPRootURL + '/Login.jsp';
    }

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">

<div id="topframe">
<table width="100%" cellpadding="1" cellspacing="0" border="0">
 	<tr>
 		<td>&nbsp;<display:img src="@prm.global.registration.header.logo" alt="" border="0" /></td>
 		<td align="right" class="regBanner"><%=PropertyProvider.get("prm.registration.forgottenloginwizard.pagetitle")%>&nbsp;</td>
 	</tr>
</table>
</div>

<div id='content'>

<br />

<form name="forgotten" action="<%=SessionManager.getJSPRootURL()%>/registration/nativedir/ForgottenLoginWizardProcessing.jsp" method="post">
    <input type="hidden" name="theAction">

<div align="center">
<table width="80%" cellpadding=0 cellspacing=0 border=0>
	<tr>
		<td colspan="4" class="tableHeader">
		<display:get name="prm.global.display.requiredfield" />
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr class="actionBar">
		<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width="8" height="27" alt="" border="0"></td>
		<td colspan="2" valign="middle" class="actionBar">&nbsp;</td>
		<td width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width="8" height="27" alt="" border="0"></td>
	</tr>

<% if (request.getAttribute("errorMsg") != null) { %>
    <tr>
        <td>&nbsp;</td>
        <td colspan=2 class="fieldWithError"><%=request.getAttribute("errorMsg")%></td>
        <td>&nbsp;</td>
    </tr>
<% } %>
    <tr>
        <td>&nbsp;</td>
        <td colspan="2"><%=PropertyProvider.get("prm.registration.forgottenloginwizard.answer.instruction")%></td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td nowrap width="10%" class="fieldRequired"><%=PropertyProvider.get("prm.registration.forgottenloginwizard.firstname.label")%></td>
        <td><input type="text" name="firstName"></td>
        <td>&nbsp;</td>
    </tr>
    <tr> 
        <td>&nbsp;</td> 
	    <td class="fieldRequired"><%=PropertyProvider.get("prm.registration.forgottenloginwizard.lastname.label")%></td>
	    <td><input type="text" name="lastName"></td>
	    <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td nowrap class="fieldNon"><%=PropertyProvider.get("prm.registration.forgottenloginwizard.question.label")%>&nbsp;&nbsp;</td>
        <td class="tableHeader"><%=helperUser.getCleartextHintPhrase()%></td>
	<td>&nbsp;</td>
    <tr>
        <td>&nbsp;</td>
        <td nowrap class="fieldRequired"><%=PropertyProvider.get("prm.registration.forgottenloginwizard.answer.label")%></td>
        <td valign="Bottom"><input type="text" name="hintAnswer"></td>
    	<td>&nbsp;</td>
    </tr>
</table>

</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="back" />
		<tb:button type="finish" function="javascript:next();" />
	</tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS space="personal"/>
</body>
</html>
