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
|     $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.util.Validator,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.space.Space"
%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="creditCardProcessor" class="net.project.creditcard.CreditCardPageProcessor" scope="session"/>

<html>
<head>
<title><display:get name="prm.global.creditcard.purchasecomplete.pagetitle"/></title>

<%-- Import CSS --%>
<%
    //Try to find the correct css to display.  (This might be more difficult than it seems.)
    if (creditCardProcessor.isShowRegistrationHeader()) { %>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<%  } else if (SessionManager.getUser() == null) { %>
<template:getSpaceCSS space="personal"/>
<%  } else { %>
<template:getSpaceCSS/>
<%  } %>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/window_functions.js" />

<script language="javascript" type="text/javascript">
function cancel() {
    history.back();
}

function finish() {
    self.document.location="<%=SessionManager.getJSPRootURL()%>/creditcard/CreditCardController.jsp?theAction=finish";
}
</script>

</head>

<body class="main">

<% if (creditCardProcessor.isShowRegistrationHeader()) {%>
<table width="100%" cellpadding="1" cellspacing="0" border="0">
 	<tr>
 		<td>&nbsp;<display:img src="@prm.global.registration.header.logo" alt="" border="0" /></td>
 		<td align="right" class="regBanner"><display:get name="prm.registration.creditcard.pagetitle"/>&nbsp;</td>
 	</tr>
    <tr>
        <td class="navBg" colspan="2">&nbsp;</td>
    </tr>
</table>
<% } %>

<br>

<div align="center">
<table width="600" cellpadding="0" cellspacing="0" border="0">
	<tr class="actionBar">
		<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td valign="middle" class="ActionBar"><display:get name="prm.global.creditcard.purchasecomplete.pagetitle"/></td>
		<td width="1%" align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
    </tr>
    <%  String duplicateSubmission = request.getParameter("duplicateSubmission");
        if (duplicateSubmission != null && new Boolean(duplicateSubmission).booleanValue()) { %>
    <tr>
        <td></td>
        <td class="warningTextRed">
        <display:get name="prm.registration.creditcard.duplicatesubmission.message"/>
        </td>
        <td></td>
    </tr>
    <tr><td colspan="3">&nbsp;</td></tr>
    <% } %>
    <tr>
        <td></td>
        <td class="tableContent">
            <%= PropertyProvider.get("prm.global.creditcard.purchasecomplete.paymentreceived.message",
                    creditCardProcessor.getTotal().format(creditCardProcessor.getUser()),
                    creditCardProcessor.getCreditCard().getProtectedCreditCardNumber())%><br>
            <%
                //Try to find the current module id.  If one is available, we
                //will show the user a link to go and see the new license.
                Space currentSpace = creditCardProcessor.getUser().getCurrentSpace();

                if (currentSpace != null && !Validator.isBlankOrNull(creditCardProcessor.getLicense().getKey().toDisplayString())) {
                    int module = Module.getModuleForSpaceType(currentSpace.getSpaceType().getID());
            %>
            <br>
            Click <a href="<%=SessionManager.getJSPRootURL()+"/admin/license/LicenseDetailView.jsp?module="+module+"&licenseKey="+creditCardProcessor.getLicense().getKey().toDisplayString()+"&cancelButton=useBack"%>">here</a> to view your newly created license.
            <%  } %>
        </td>
        <td></td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" width="100%">
                <tb:band name="action">
                        <tb:button type="finish"/>
                </tb:band>
</tb:toolbar>

</div>

<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>
