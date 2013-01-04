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
    info="Registration License Select"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.User,
        	net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.admin.RegistrationBean,
			net.project.license.LicenseException,
            net.project.license.create.LicenseSelectionType"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="licenseContext" class="net.project.license.create.LicenseContext" scope="session" />
<%
    //net.project.license.create.LicenseContext licenseContext = registration.getLicenseContext();
	session.setAttribute("licenseContext", registration.getLicenseContext());
%>
<html>
<head>
<title><%=PropertyProvider.get("prm.registration.licensepage.title")%></title>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<template:getSpaceCSS space="personal"/>

<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/errorHandler.js" />


<script language="javascript">
	var theForm;
	var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
    isLoaded = true;
    theForm = self.document.forms["registration"];
    selectRadio(theForm.selectionTypeID, "<%=licenseContext.getSelectionTypeID()%>");
}
    
function validateForm(frm) {
    document.registration.theAction.value = "next";
    if (!checkRadio(frm.selectionTypeID, '<%=PropertyProvider.get("prm.registration.license.selectoption.message")%>')) return false;
    return true;
}

function processForm() {
    if(validateForm(document.registration))
	    document.registration.submit();
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
    theAction("cancel");
    theForm.submit();
}

</script>
</head>

    
<%------------------------------------------------------------------------
  -- Start of Form Body
  ----------------------------------------------------------------------%>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">

<div id="topframe">
<table width="100%" cellpadding="1" cellspacing="0" border="0">
 	<tr>
 		<td>&nbsp;<display:img src="@prm.global.registration.header.logo" alt="" border="0" /></td>
 		<td align="right" class="regBanner"><display:get name="prm.global.registration.license.create.banner" />&nbsp;</td>
 	</tr>
</table>
</div>

<div id='content'>

<br />

<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/registration/LicenseController.jsp"%>" method="post" onSubmit="return validateForm(this);">
    <input type="hidden" name="theAction" />
    <input type="hidden" name="fromPage" value="license" />
    <input type="hidden" name="returnPage" value="<%=request.getParameter("returnPage")%>" />

<div align="center">
<table width="600" cellpadding=0 cellspacing=0 border=0>
	<tr class="actionBar">
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><display:get name="prm.global.license.create.header" /></td>
		<td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
    </tr>
	<tr align="left">
        <td>&nbsp;</td>
		<td class="tableContent" align="left" colspan="2">
            <%=PropertyProvider.get("prm.registration.license.selectlicenseoption.message")%>
        </td>
        <td>&nbsp;</td>
	</tr>
    <tr><td colspan="4"></td></tr>
	<tr align="left">
		<td>&nbsp;</td>
        <td colspan="2">
		<jsp:include page="/admin/license/include/LicenseSelect.jsp">
            <jsp:param name="domainSource" value="registrationBean" />
        </jsp:include>

        </td>
		<td>&nbsp;</td>
    </tr>
</table>

</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="back" />
		<tb:button type="next" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS space="personal"/>
</body>
</html>
<%
    // Clear any errors so they don't get re-displayed
    licenseContext.clearErrors();
%>