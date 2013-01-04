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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Collect credit card information for registration"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.creditcard.CreditCardType,
            net.project.resource.ProfileCodes,
            net.project.base.property.PropertyProvider,
            net.project.xml.XMLFormatter,
            net.project.util.Validator,
            net.project.base.PnetException,
            java.util.Map,
            net.project.security.User,
            net.project.util.CalendarUtils,
            net.project.util.CalendarUtils"
%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>
<jsp:useBean id="creditCardProcessor" class="net.project.creditcard.CreditCardPageProcessor" scope="session"/>

<html>
<head>
<title><display:get name="prm.global.creditcard.collectcreditcardinfo.pagetitle"/></title>

<%-- Import CSS --%>

<%
    Map param = creditCardProcessor.getCcInfoParameters();

    //Try to find the correct css to display.  (This might be more difficult than it seems.)
    if (creditCardProcessor.isShowRegistrationHeader()) { %>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<%  } else if (SessionManager.getUser() == null) { %>
<template:getSpaceCSS space="personal"/>
<%  } else { %>
<template:getSpaceCSS/>
<%  } %>

<%
    //Determine values (if any) that will be filled in
    Map parameters = creditCardProcessor.getCcInfoParameters();
    String licenseType = (String)parameters.get("licenseType");
    String singleLicenseTypeValue, multipleLicenseTypeValue, numberOfLicensesValue;

    if (licenseType != null && licenseType.equalsIgnoreCase("multiple")) {
        singleLicenseTypeValue = "";
        multipleLicenseTypeValue = " checked=\"true\"";
        numberOfLicensesValue = " value=\""+parameters.get("numberOfLicenses")+"\"";
    } else {
        numberOfLicensesValue = "";
        multipleLicenseTypeValue = "";
        singleLicenseTypeValue = " checked=\"true\"";
    }

    CreditCardType creditCardTypeValue = (Validator.isBlankOrNull((String)parameters.get("creditCardType")) ? null : CreditCardType.getForID((String)parameters.get("creditCardType")));
    String creditCardNumberValue = (Validator.isBlankOrNull((String)parameters.get("creditCardNumber")) ? "" : " value=\""+parameters.get("creditCardNumber")+"\"");
    String expirationMonthValue = (Validator.isBlankOrNull((String)parameters.get("expirationMonth")) ? "" : (String)parameters.get("expirationMonth"));
    String expirationYearValue = (Validator.isBlankOrNull((String)parameters.get("expirationYear")) ? "" : (String)parameters.get("expirationYear"));
    String billingDisplayNameValue = (Validator.isBlankOrNull((String)parameters.get("billingDisplayName")) ? "" : " value=\""+parameters.get("billingDisplayName")+"\"");
    String billingAddress1Value = (Validator.isBlankOrNull((String)parameters.get("billingAddress1")) ? "" : " value=\""+parameters.get("billingAddress1")+"\"");
    String billingAddress2Value = (Validator.isBlankOrNull((String)parameters.get("billingAddress2")) ? "" : " value=\""+parameters.get("billingAddress2")+"\"");
    String billingCityValue = (Validator.isBlankOrNull((String)parameters.get("billingCity")) ? "" : " value=\""+parameters.get("billingCity")+"\"");
    String billingStateValue = (Validator.isBlankOrNull((String)parameters.get("billingState")) ? "" : (String)parameters.get("billingState"));
    String billingZipValue = (Validator.isBlankOrNull((String)parameters.get("billingZip")) ? "" : " value=\""+parameters.get("billingZip")+"\"");
    String billingCountryValue = (Validator.isBlankOrNull((String)parameters.get("billingCountry")) ? "" : (String)parameters.get("billingCountry"));
%>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/window_functions.js" />

<script language="javascript" type="text/javascript">
var theForm;

function setup() {
    theForm = self.document.forms[0];
}

function fillForm() {
<% if (creditCardProcessor.isShowUseProfileInfoCheckbox()) { %>
    if (theForm.fillFormWithProfileInfo.checked) {
        theForm.billingDisplayName.value = '<%=creditCardProcessor.getUser().getDisplayName()%>';
        theForm.billingAddress1.value = '<%=creditCardProcessor.getUser().getAddress1()%>';
        theForm.billingCity.value = '<%=creditCardProcessor.getUser().getCity()%>';
        selectSelect(theForm.billingState, '<%=creditCardProcessor.getUser().getState()%>');
        theForm.billingZip.value = '<%=creditCardProcessor.getUser().getZipcode()%>';
        selectSelect(theForm.billingCountry, '<%=creditCardProcessor.getUser().getCountry()%>');
    } else {
        theForm.billingDisplayName.value = '';
        theForm.billingAddress1.value = '';
        theForm.billingCity.value = '';
        selectSelect(theForm.billingState, '');
        theForm.billingZip.value = '';
        selectSelect(theForm.billingCountry, '');
    }
<% } %>
}

function validateForm() {
    if (!checkDropdown(theForm.creditCardType, "<%=PropertyProvider.get("prm.global.creditcard.validation.pleaseselectcctype.message")%>")) return false;
    if (!checkTextbox(theForm.creditCardNumber, "<%=PropertyProvider.get("prm.global.creditcard.validation.ccnumberrequired.message")%>")) return false;
    if (!checkCC(theForm.creditCardNumber, "<%=PropertyProvider.get("prm.global.creditcard.validation.numberisinvalid.message")%>")) return false;
    if (!checkDropdown(theForm.expirationMonth, "<%=PropertyProvider.get("prm.global.creditcard.validation.expmonthrequired.message")%>")) return false;
    if (!checkDropdown(theForm.expirationYear, "<%=PropertyProvider.get("prm.global.creditcard.validation.expyearrequired.message")%>")) return false;
    if (!checkTextbox(theForm.billingDisplayName, "<%=PropertyProvider.get("prm.global.creditcard.validation.fullnamerequired.message")%>")) return false;
    if (!checkTextbox(theForm.billingAddress1, "<%=PropertyProvider.get("prm.global.creditcard.validation.address1required.message")%>")) return false;
    if (!checkDropdown(theForm.billingState, "<%=PropertyProvider.get("prm.global.creditcard.validation.staterequired.message")%>")) return false;
    if (!checkTextbox(theForm.billingZip, "<%=PropertyProvider.get("prm.global.creditcard.validation.zipcoderequired.message")%>")) return false;
    if (!checkDropdown(theForm.billingCountry, "<%=PropertyProvider.get("prm.global.creditcard.validation.countryrequired.message")%>")) return false;

    return true;
}

function checkCC(formField, errorMessage) {
  var s = formField.value;
  var i, n, c, r, t;
  var returnValue;

  // First, reverse the string and remove any non-numeric characters.
  r = "";
  for (i = 0; i < s.length; i++) {
    c = parseInt(s.charAt(i), 10);
    if (c >= 0 && c <= 9)
      r = c + r;
  }

  // Check for a bad string.
  if (r.length <= 1) {
    return errorHandler(formField,errorMessage);
  }

  // Now run through each single digit to create a new string. Even digits
  // are multiplied by two, odd digits are left alone.
  t = "";
  for (i = 0; i < r.length; i++) {
    c = parseInt(r.charAt(i), 10);
    if (i % 2 != 0)
      c *= 2;
    t = t + c;
  }

  // Finally, add up all the single digits in this string.

  n = 0;
  for (i = 0; i < t.length; i++) {
    c = parseInt(t.charAt(i), 10);
    n = n + c;
  }

  // If the resulting sum is an even multiple of ten (but not zero), the
  // card number is good.

  if (n != 0 && n % 10 == 0)
    returnValue = true;
  else
    returnValue = false;

  if (returnValue == false) {
    return errorHandler(formField,errorMessage);
  }

  return returnValue;
}

function back() {
    theAction("previous");
    theForm.submit();
}

function cancel() {
    theAction("cancel");
    theForm.submit();
}

function submit() {
    if (validateForm(theForm)) {
        theAction("purchaseSummary");
        theForm.submit();
    }
}

</script>

</head>

<body class="main" onLoad="setup();">

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

<br />

<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/creditcard/CreditCardController.jsp"%>" method="post">
<input type="hidden" name="theAction" />

<div align="center">

<table width="600" cellpadding=0 cellspacing=0 border=0>
    <tr>
        <td colspan="5"><errors:show clearAfterDisplay="true"/></td>
    </tr>

	<tr class="actionBar">
		<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="3" valign="middle" class="ActionBar"><display:get name="prm.global.creditcard.collectcreditcardinfo.numberoflicenses.name"/></td>
		<td width="1%" align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
    </tr>
    <tr>
        <td></td>
        <td colspan="3" class="fieldRequired"><input type="radio" name="licenseType" value="single" <%=singleLicenseTypeValue%>><display:get name="prm.global.creditcard.collectcreditcardinfo.purchasesinglelicense.name"/></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td colspan="3" class="tableContent"><display:get name="prm.global.creditcard.collectcreditcardinfo.purchasesinglelicense.description"/></td>
        <td></td>
    </tr>
    </tr>
        <td></td>
        <td colspan="3" class="fieldRequired"><input type="radio" name="licenseType" value="multiple"<%=multipleLicenseTypeValue%>><%=PropertyProvider.get("prm.global.creditcard.collectcreditcardinfo.purchasemultiplelicense.name", "<input type=\"text\" name=\"numberOfLicenses\" "+numberOfLicensesValue+"size=\"3\" maxlength=\"10\">")%></td>
        <td></td>
    <tr>
    <tr>
        <td></td>
        <td colspan="3" class="tableContent">
        <display:get name="prm.global.creditcard.collectcreditcardinfo.purchasemultiplelicense.description"/>
        </td>
        <td></td>
    </tr>
    </tr>
	<tr class="actionBar">
		<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="3" valign="middle" class="ActionBar"><display:get name="prm.global.creditcard.collectcreditcardinfo.ccinfo.name"/></td>
		<td width="1%" align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
    </tr>
    <tr>
        <td></td>
        <td class="fieldRequired"><display:get name="prm.global.creditcard.types.name"/></td>
        <td>&nbsp;</td>
        <td>
            <select name="creditCardType">
                <option value=""><display:get name="prm.global.creditcard.types.pleaseselecttype.message"/></option>
                <%=CreditCardType.getHTMLOptionList(creditCardTypeValue)%>
            </select>
        </td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="fieldRequired"><display:get name="prm.global.creditcard.number.name"/></td>
        <td>&nbsp;</td>
        <td><input type="text" name="creditCardNumber" size="20" maxlength="19"<%=creditCardNumberValue%>></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="fieldRequired"><display:get name="prm.global.creditcard.expmonth.name"/></td>
        <td>&nbsp;</td>
        <td>
            <select name="expirationMonth">
            <option value=""></option>
            <%=CalendarUtils.getMonthOptionList(creditCardProcessor.getUser(), expirationMonthValue)%>
            </select>
        </td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="fieldRequired"><display:get name="prm.global.creditcard.expyear.name"/></td>
        <td>&nbsp;</td>
        <td>
            <select name="expirationYear">
            <option value=""></option>
            <%=CalendarUtils.getCreditCardYearOptionList(creditCardProcessor.getUser(), expirationYearValue)%>
            </select>
        </td>
        <td></td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>
	<tr class="actionBar">
		<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="3" valign="middle" class="ActionBar"><display:get name="prm.global.creditcard.collectcreditcardinfo.billinginfo.name"/></td>
		<td width="1%" align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
    </tr>
    <% if (creditCardProcessor.isShowUseProfileInfoCheckbox()) { %>
    <tr>
        <td></td>
        <td class="fieldRequired" colspan="3"><input type="checkbox" name="fillFormWithProfileInfo" onchange="javascript:fillForm();" onclick="javascript:fillForm();"><display:get name="prm.global.creditcard.collectcreditcardinfo.useuserprofileinfo.name"/></td>
        <td></td>
    </tr>
    <% } %>
    <tr>
        <td></td>
        <td class="fieldRequired"><display:get name="prm.global.creditcard.billinginfo.fullname.name"/></td>
        <td>&nbsp;</td>
        <td><input type="text" name="billingDisplayName" size="20" maxlength="240"<%=billingDisplayNameValue%>></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="fieldRequired"><display:get name="prm.global.creditcard.billinginfo.address1.name"/></td>
        <td>&nbsp;</td>
        <td><input type="text" name="billingAddress1" size="40" maxlength="80"<%=billingAddress1Value%>></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="fieldNonRequired"><display:get name="prm.global.creditcard.billinginfo.address2.name"/></td>
        <td>&nbsp;</td>
        <td><input type="text" name="billingAddress2" size="40" maxlength="80"<%=billingAddress2Value%>></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="fieldNonRequired"><display:get name="prm.global.creditcard.billinginfo.city.name"/></td>
        <td>&nbsp;</td>
        <td><input type="text" name="billingCity" size="25" maxlength="50"<%=billingCityValue%>></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="fieldNonRequired"><display:get name="prm.global.creditcard.billinginfo.state.name"/></td>
        <td>&nbsp;</td>
        <td>
            <select name="billingState">
      		    <option value=""><display:get name="prm.registration.addressinfo.stateprovince.option.selectstate.value"/></option> <!-- Select State -->
		        <%=ProfileCodes.getStateCodeOptionList(billingStateValue)%>
            </select>
        </td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="fieldRequired"><display:get name="prm.global.creditcard.billinginfo.zip.name"/></td>
        <td>&nbsp;</td>
        <td><input type="text" name="billingZip" size="15" maxlength="20"<%=billingZipValue%>></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="fieldRequired"><display:get name="prm.global.creditcard.billinginfo.country.name"/></td>
        <td>&nbsp;</td>
        <td>
            <select name="billingCountry">
		      <%=ProfileCodes.getCountryCodeOptionList(billingCountryValue)%>
            </select>
        </td>
        <td></td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true">
				<tb:band name="action">
                    <% if (creditCardProcessor.isCancelButtonEnabled()) {%>
					<tb:button type="cancel" />
                    <% } %>
					<tb:button type="next" function="javascript:submit();" />
				</tb:band>
</tb:toolbar>

</div>

<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>
