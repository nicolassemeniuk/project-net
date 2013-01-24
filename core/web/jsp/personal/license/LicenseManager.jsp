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
|   $Revision: 19034 $
|       $Date: 2009-03-24 13:14:25 -0300 (mar, 24 mar 2009) $
|     $Author: avinash $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.license.LicenseManager,
            net.project.base.Module,
            net.project.license.system.MasterProperties,
            net.project.license.system.PropertyName"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>

<script language="javascript" type="text/javascript">
var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";
function cancel() {
    self.document.location = '<%=SessionManager.getJSPRootURL()+"/personal/Main.jsp?module="+Module.PERSONAL_SPACE%>';
}
function reset() {
}
function help() {
  	var helplocation = JSPRootURL + "/help/HelpDesk.jsp?page=personal_setup-licensing";
 	openwin_help(helplocation);
}
</script>

</head>

<body class="main" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
<tb:toolbar style="tooltitle" groupTitle="prm.personal.licensemanager.pagetitle">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.personal.licensemanager.pagetitle")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "" %>'
					queryString='<%="module="+net.project.base.Module.PERSONAL_SPACE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
	</tb:band>
</tb:toolbar>

<div id='content'>

<%--------------------------------------------------------------------------------------------------------
  --  Page Content
  ------------------------------------------------------------------------------------------------------%>
<table border="0" width="100%">
<tr><td colspan="4">
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr class="channelHeader">
            <td class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 border="0" alt="" hspace="0" vspace="0"></td>
            <td class="channelHeader" colspan="2" nowrap><%=PropertyProvider.get("prm.personal.setup.channel.licenses.title")%></td>
            <td class="channelHeader" width="1%" align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" border="0" alt="" hspace="0" vspace="0"></td>
        </tr>
    </table>
</td></tr>
<% if (LicenseManager.isUserResponsibleForLicense(user)) {
        //Clear out all session attributes for saved variables for licensing
        session.setAttribute("licenseType", "all");
        session.setAttribute("licenseStatus", "all");
        session.setAttribute("searchLicenseKey", "");
        session.setAttribute("userName", "");
        session.setAttribute("folName", "");
        session.setAttribute("emailID", "");
        session.setAttribute("displayLicenses", "false");

        session.setAttribute("orgPage", "/personal/Main.jsp");
        session.setAttribute("userIdentity", "responsibleUser");
%>
<tr>
    <td></td>
    <td class="tableContentFontOnly"><a href="<%=SessionManager.getJSPRootURL()%>/admin/license/LicenseListView.jsp?module=<%= net.project.base.Module.APPLICATION_SPACE %>"><%=PropertyProvider.get("prm.personal.setup.managelicenses.link")%></a></td>
    <td class="tableContentFontOnly"><%=PropertyProvider.get("prm.personal.setup.managelicenses.description")%></td>
    <td></td>
</tr>
<% } %>
<%  MasterProperties props = MasterProperties.getInstance();
    if (props.get(PropertyName.CREDIT_CARD_ENABLED) != null &&
        Boolean.valueOf(props.get(PropertyName.CREDIT_CARD_ENABLED).getValue()).booleanValue() &&
        (user.getUserDomain().supportsCreditCardPurchases())) { %>
<tr>
    <td></td>
    <td class="tableContentFontOnly"><a href="<%=SessionManager.getJSPRootURL()%>/personal/license/PurchaseLicenses.jsp?module=<%= net.project.base.Module.APPLICATION_SPACE %>"><%=PropertyProvider.get("prm.personal.setup.purchaselicenses.link")%></a></td>
    <td class="tableContentFontOnly"><%=PropertyProvider.get("prm.personal.setup.purchaselicenses.description")%></td>
    <td></td>
</tr>
<% } %>
</table>

<tb:toolbar style="action" showLabels="true" width="100%" bottomFixed="true">
	<tb:band name="action">
	</tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
