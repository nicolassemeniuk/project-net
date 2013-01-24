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
| Provides list of domains from which user selects one
| Domain selection significantly alters the remainder of the
| registration process
|--------------------------------------------------------------------%>

<%@ page
    contentType="text/html; charset=UTF-8"
    info="Registration Domain Selection"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
        	net.project.base.property.PropertyProvider"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<jsp:useBean id="availableDomains" class="net.project.security.domain.UserDomainCollection" scope="request" />

<%
    // Grab any current selection
    String selectedDomainID = registration.getUserDomainID();
    if (selectedDomainID == null) {
        // Default to first domain id
        selectedDomainID = ((net.project.security.domain.UserDomain) availableDomains.get(0)).getID();
    }
%>
<html>
<head>
<title><%=PropertyProvider.get("prm.registration.domainselectpage.title")%></title>

<%-- Import CSS --%>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<template:getSpaceCSS space="personal"/>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkRadio.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
    function setup() {
        theForm = self.document.forms["registration"];
        selectRadio(theForm.domainID, '<%=selectedDomainID%>');
    	isLoaded = true;
    }

    function validateForm(frm) {
        if (!checkRadio(frm.domainID, '<%=PropertyProvider.get("prm.registration.domainselect.selectvalue.message")%>')) return false;
        return true;
    }

    function processForm() {
        if (validateForm(theForm)) {
            theForm.submit();
        }
    }

    function back() {
        theAction("back");
        theForm.submit();
    }

    function next() {
        theAction("next");
        processForm();
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
 		<td align="right" class="regBanner"><display:get name="prm.global.registration.main.banner" />&nbsp;</td>
 	</tr>
</table>
</div>

<div id='content'>

<br />

<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/registration/DomainSelectionController.jsp"%>" method="post">
	<input type="hidden" name="theAction">
    <input type="hidden" name="fromPage" value="domainSelect">

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
		<td colspan="2" valign="middle" class="actionBar"><%=PropertyProvider.get("prm.registration.domainselect.channel.select.title")%></td>
		<td width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width="8" height="27" alt="" border="0"></td>
	</tr>
	<tr align="left">
        <td>&nbsp;</td>
        <td colspan="2" class="instructions">
            <display:get name="prm.global.registration.domain.instructions" />
        </td>
        <td>&nbsp;</td>
    </tr>
	<tr><td colspan="4">&nbsp;</td></tr>
    
	<tr align="left">
        <td>&nbsp;</td>
        <td colspan="2">
            <%-- Display the selection of domains --%>
            <pnet-xml:transform name="availableDomains" scope="request" stylesheet="/registration/xsl/UserDomainSelect.xsl" />
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
