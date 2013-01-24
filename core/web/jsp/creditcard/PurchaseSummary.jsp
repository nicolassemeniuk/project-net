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
            net.project.base.money.Money,
            java.util.Currency,
            net.project.base.property.PropertyProvider,
            net.project.license.License,
            net.project.util.NumberFormat,
            net.project.security.User,
            java.math.BigDecimal"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="creditCardProcessor" class="net.project.creditcard.CreditCardPageProcessor" scope="session"/>

<%
    User user = creditCardProcessor.getUser();
%>

<html>
<head>
<title><display:get name="prm.global.creditcard.purchasesummary.pagetitle"/></title>

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
var theForm;

function setup() {
    theForm = self.document.forms[0];
}

function cancel() {
    theAction("cancel");
    theForm.submit();
}

function back() {
    theAction("collectCreditCardInfo");
    theForm.submit();
}

function next() {
    theAction("completePurchase");
    theForm.submit();
}
</script>

</head>

<body class="main" onLoad="setup();">

<% if (creditCardProcessor.isShowRegistrationHeader()) {%>
<table width="100%" cellpadding="1" cellspacing="0" border="0">
 	<tr>
 		<td>&nbsp;<display:img src="@prm.global.registration.header.logo" alt="" border="0" /></td>
 		<td align="right" class="regBanner"><display:get name="prm.global.creditcard.purchasesummary.pagetitle"/></td>
 	</tr>
    <tr>
        <td class="navBg" colspan="2">&nbsp;</td>
    </tr>
</table>
<% } %>


<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/creditcard/CreditCardController.jsp"%>" method="post">
<input type="hidden" name="theAction">

<div align='center'>

<table width="600" border="0" cellpadding="0" cellspacing="0">
    <tr><td colspan="7">
    <table cellpadding="0" cellspacing="0" width="100%">
        <tr class="actionBar">
            <td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
            <td valign="middle" class="ActionBar"><display:get name="prm.global.creditcard.purchasesummary.pagetitle"/></td>
            <td width="1%" align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
        </tr>
    </table>
    </td></tr>
    <tr>
        <td width="1%"></td>
        <td class="tableHeader"><display:get name="prm.global.creditcard.receipt.qty.columnheader"/></td>
        <td class="tableHeader"><display:get name="prm.global.creditcard.receipt.item.columnheader"/></td>
        <td class="tableHeader"><display:get name="prm.global.creditcard.receipt.price.columnheader"/></td>
        <td></td>
        <td class="tableHeader"><display:get name="prm.global.creditcard.receipt.subtotal.columnheader"/></td>
        <td width="1%"></td>
    </tr>
    <tr>
        <td></td>
        <td class="tableLine" colspan="5"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" height="2" width="1" border="0"></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="tableContent"><%=NumberFormat.getInstance().formatNumber(creditCardProcessor.getNumberOfLicenses())%></td>
        <td class="tableContent"><display:get name="prm.global.creditcard.purchasesummary.lineitem.licensefees.description"/></td>
        <td class="tableContent"><%=creditCardProcessor.getUnitPrice().format(creditCardProcessor.getUser())%></td>
        <td></td>
        <td class="tableContent"><%=creditCardProcessor.getLicenseCosts().format(creditCardProcessor.getUser())%></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="tableLine" colspan="5"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" height="1" width="1" border="0"></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="tableContent"><%=NumberFormat.getInstance().formatNumber(creditCardProcessor.getNumberOfLicenses())%></td>
        <td class="tableContent"><display:get name="prm.global.creditcard.purchasesummary.lineitem.maintenancefees.description"/></td>
        <td class="tableContent"><%=creditCardProcessor.getMaintenanceFeeUnitPrice().format(creditCardProcessor.getUser())%></td>
        <td></td>
        <td class="tableContent"><%=creditCardProcessor.getMaintenanceFee().format(creditCardProcessor.getUser())%></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="tableLine" colspan="5"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" height="1" width="1" border="0"></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td colspan="5">&nbsp;</td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="tableLine" colspan="5"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" height="1" width="1" border="0"></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td colspan="5">&nbsp;</td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="tableLine" colspan="5"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" height="2" width="1" border="0"></td>
        <td></td>
    </tr>
    <tr>
        <td colspan="4" class="tableContent" align="right"><display:get name="prm.global.creditcard.purchasesummary.subtotal.name"/>&nbsp;&nbsp;</td>
        <td>&nbsp;</td>
        <td class="tableContent"><%=creditCardProcessor.getSubtotal().format(user)%></td>
        <td></td>
    </tr>
    <% if (!(creditCardProcessor.getCreditCardSurcharge().getValue().floatValue() == 0)) { %>
    <tr>
        <td colspan="4" class="tableContent" align="right"><display:get name="prm.global.creditcard.purchasesummary.ccservicecharge.name"/>&nbsp&nbsp;</td>
        <td>&nbsp;</td>
        <td class="tableContent"><%=creditCardProcessor.getCreditCardSurcharge().format(user)%></td>
        <td></td>
    </tr>
    <% } %>
    <tr>
        <td colspan="4" class="tableHeader" align="right"><display:get name="prm.global.creditcard.purchasesummary.total.name"/>&nbsp;&nbsp;</td>
        <td>&nbsp;</td>
        <td class="tableHeader"><%=creditCardProcessor.getTotal().format(user)%></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="tableLine" colspan="5"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" height="2" width="1" border="0"></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td colspan="5">&nbsp;</td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td colspan="5" class="tableContent">
        <%=PropertyProvider.get("prm.global.creditcard.purchasesummary.agreement.message",
              creditCardProcessor.getTotal().format(user),
              creditCardProcessor.getCreditCard().getProtectedCreditCardNumber())%>
        <td></td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" width="100%">
                <tb:band name="action">
                        <tb:button type="cancel"/>
                        <tb:button type="back"/>
                        <tb:button type="next"/>
                </tb:band>
</tb:toolbar>

</form>

</div>

<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>
