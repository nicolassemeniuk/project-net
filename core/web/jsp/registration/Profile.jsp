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
|   $Revision: 20756 $
|   $Date: 2010-04-26 10:38:24 -0300 (lun, 26 abr 2010) $
|   $Author: ritesh $
|
|   Primary Registration Page
|--------------------------------------------------------------------%>

<%@ page
    contentType="text/html; charset=UTF-8"
    info="New User Registration"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.resource.ProfileCodes,
            net.project.persistence.PersistenceException,
        	net.project.security.SessionManager,
        	net.project.base.property.PropertyProvider,
            net.project.resource.IPersonAttributes,
			net.project.base.property.PropertyProvider,
            net.project.license.create.LicenseSelectionType"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="brandManager" class="net.project.brand.BrandManager" scope="session" />

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%
    // Grab any error messages and remove from session to prevent them
    // reappearing later
    String errorFieldName = (String) session.getValue("errorObj");
    String errorMessage = (String) session.getValue("errorMsg");
    session.removeValue("errorObj");
    session.removeValue("errorMsg");

    String query=null;
	String statelist = null;
	String countrylist = null;
	String timezonelist = null;
%>


<html>
<head>
<title><%=PropertyProvider.get("prm.registration.userprofilepage.title")%></title>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<template:getSpaceCSS space="personal"/>

<template:import type="javascript" src="/src/checkEmail.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/cookie.js" />
<template:import type="javascript" src="/src/checkAlphaNumeric.js" />
<template:import type="javascript" src="/src/browser.js" />

<script language="javascript">
	var theForm;
	var formSubmited = false;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
    function setup() {
        theForm = self.document.forms["registration"];
    	isLoaded = true;
    }

		// Do a cookie check, if javascript is turned off the user will be notifed
		today=new Date();
		SetCookie("testcookie","Cookies On!",null,"/");
		if(GetCookie("testcookie")==null)
			top.location.href = "../CookieRequired.jsp"
		
		detectBrowser("../BadBrowser.jsp");
		
		function validateForm(frm) {
			if (!checkTextbox(frm.ecom_ShipTo_Postal_Name_First,'<%=PropertyProvider.get("prm.registration.userprofile.firstnamerequired.message")%>')) return false;
			if (!checkTextbox(frm.ecom_ShipTo_Postal_Name_Last,'<%=PropertyProvider.get("prm.registration.userprofile.lastnamerequired.message")%>')) return false;
			if (!checkTextbox(frm.displayName,'<%=PropertyProvider.get("prm.registration.userprofile.displaynamerequired.message")%>')) return false;
			if (!checkTextbox(frm.ecom_ShipTo_Postal_Street_Line1,'<%=PropertyProvider.get("prm.registration.userprofile.addressrequired.message")%>')) return false;
			if (!checkTextbox(frm.ecom_ShipTo_Postal_City,'<%=PropertyProvider.get("prm.registration.userprofile.cityrequired.message")%>')) return false;
			if (!checkDropdown(frm.ecom_ShipTo_Postal_StateProv,'<%=PropertyProvider.get("prm.registration.userprofile.staterequired.message")%>')) return false;
			if (!checkDropdown(frm.ecom_ShipTo_Postal_CountryCode,'<%=PropertyProvider.get("prm.registration.userprofile.countryrequired.message")%>')) return false;
			if (!checkTextbox(frm.ecom_ShipTo_Telecom_Phone_Number,'<%=PropertyProvider.get("prm.registration.userprofile.phonerequired.message")%>')) return false;
			if (!checkDropdown(frm.timeZoneCode,'<%=PropertyProvider.get("prm.registration.userprofile.timezonerequired.message")%>')) return false;
            if (!checkDropdown(frm.localeCode,'<%=PropertyProvider.get("prm.registration.userprofile.localerequired.message")%>')) return false;
            if (!checkDropdown_NoSelect(frm.languageCode, 'Language is a required field')) return false;
            if (!checkPhoneNo(frm.ecom_ShipTo_Telecom_Phone_Number, '<%=PropertyProvider.get("prm.registration.userprofile.invalidphoneno.message")%>')) return false;
            if (!checkTextbox(frm.costByHour, '<%=PropertyProvider.get("prm.registration.userprofile.salaryamountrequired.message")%>')) return false;
    		if (!checkIsPositiveNumber(frm.costByHour,'<%=PropertyProvider.get("prm.registration.userprofile.salaryamountincorrect.message")%>')) return false;
                        
			/*if (!checkDropdown(frm.dateFormatID,"Date Format is a required field")) return false;
			if (!checkDropdown(frm.timeFormatID,"Time Format is a required field")) return false;
            */			
			return true;
		}

		function processForm()
		{
			if(validateForm(document.registration)){
				if (!formSubmited){
					formSubmited = true;
					document.registration.submit();
				} else {
					var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.general.registration.profile.already.submitted.error.message")%>';
					extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
				}
			}
		}
		function createDisplayName(){
			var displayName="";
			var fname = document.registration.ecom_ShipTo_Postal_Name_First.value;
			var lname = document.registration.ecom_ShipTo_Postal_Name_Last.value;

			if (fname != "") displayName += fname + " ";
			if (lname != "") displayName += lname;

			document.registration.displayName.value = displayName;
		}

		function appendSuffix(){
			document.registration.displayName.value = document.registration.displayName.value + " " + document.registration.Ecom_ShipTo_Postal_Name_Suffix.value;
		}

		function highlightError(){
		<%
        // Email field is now non-editable on this screen (specified elsewhere)
		if (errorFieldName != null && errorFieldName != "email"){
			out.print("document.registration." + errorFieldName + ".focus();");
		%>
		<% }else { %>
			document.registration.ecom_ShipTo_Postal_Name_Prefix.focus();
		<% } %>
			return true;
		}

		function cancel() {
			theAction("cancel");
            theForm.submit();
		}

		function back() {
			theAction("back");
            theForm.submit();
		}

        function next() {
            theAction("next");
            processForm();
        }

		function finish() {
            theAction("finish");
			processForm();
		}
        function popupHelp(page) {
    	    var helplocation="<%= SessionManager.getJSPRootURL() %>/help/HelpDesk.jsp?page="+page;
	        openwin_help(helplocation);
        }

</script>
</head>

<%------------------------------------------------------------------------
  -- Start of Form Body
  ----------------------------------------------------------------------%>
<body class="main" id="bodyWithFixedAreasSupport" leftmargin=0 topmargin=0 onLoad="setup(); highlightError();">

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

<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/registration/ProfileController.jsp"%>" method="post" onSubmit="return validateForm(this);">
    <input type="hidden" name="theAction">
    <input type="hidden" name="fromPage" value="profile">

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
	<tr>
		<td colspan="4">
			<noscript><b><%=PropertyProvider.get("prm.registration.turnonjavascript.1.text")%><a href="<%= SessionManager.getAppURL() %>/help/HelpDesk.jsp?page=browser_requirements"><%=PropertyProvider.get("prm.registration.turnonjavascript.2.link")%></a><%=PropertyProvider.get("prm.registration.turnonjavascript.3.text")%></b></noscript>
		</td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>

	<tr>
		<td colspan="4">
            <table width="100%" border="0">
            <tr>
            	<!-- Avinash: empty textbox  -->
                <td class="fieldNonRequired"><%=PropertyProvider.get("prm.registration.userprofile.username.label")%>&nbsp;</td>
                <td class="tableContent"><c:out value="${registration.login}" /></td>
                <td class="fieldNonRequired"><%=PropertyProvider.get("prm.registration.userprofile.email.label")%>&nbsp;</td>
                <td class="tableContent"><c:out value="${registration.email}" /></td>
            </tr>
            </table>
		</td>
        <td>&nbsp;</td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>

<%
if (errorMessage != null ) {
%>
	<tr align="left">
		<td colspan="4" align="left">
			<span class="warningTextRed"><%=errorMessage%></span>
		</td>
	</tr>
<%
}
%>
	<tr class="actionBar">
		<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><display:get name="prm.global.registration.personal.header" /></td>		
		<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>
			<tr align="left">
				<td nowrap>&nbsp;</td>
				<td nowrap class="fieldNonRequired" align="right"><display:get name="prm.global.registration.personal.nameprefix" />:&nbsp;&nbsp;</td>
				<td nowrap>
					<!-- Avinash: empty textbox  -->
					<input type="text" name="ecom_ShipTo_Postal_Name_Prefix" size="8" maxlength="20" onBlur="createDisplayName(this.value);" value='<c:out value="${registration.namePrefix}" />'>
				    <span class="fieldNonRequired">&nbsp; <display:get name="prm.global.registration.personal.nameprefix.example" /></span>
                    <%=flagDirectoryProvided(registration, IPersonAttributes.PREFIX_ATTRIBUTE)%>
				</td>
				<td nowrap>&nbsp;</td>
			</tr>
			<tr align="left">
			<td nowrap>&nbsp;</td>
				<td nowrap class="fieldRequired" align="right"><display:get name="prm.global.registration.personal.firstname" />:&nbsp;&nbsp;</td>
				<td nowrap>
					<!-- Avinash: empty textbox  -->
					<input type="text" name="ecom_ShipTo_Postal_Name_First" size="20" maxlength="40" onBlur="createDisplayName(this.value);" value='<c:out value="${registration.firstName}" />'>
                    <%=flagDirectoryProvided(registration, IPersonAttributes.FIRSTNAME_ATTRIBUTE)%>
				</td>
				<td nowrap>&nbsp;</td>
			</tr>
			<tr align="left">
			<td nowrap>&nbsp;</td>
				<td nowrap class="fieldNonRequired" align="right"><display:get name="prm.global.registration.personal.middlename" />:&nbsp;&nbsp;</td>
				<td nowrap>
					<!-- Avinash: empty textbox  -->
					<input type="text" name="ecom_ShipTo_Postal_Name_Middle" size="20" maxlength="40" onBlur="createDisplayName(this.value);" value='<c:out value="${registration.middleName}" />'>
                    <%=flagDirectoryProvided(registration, IPersonAttributes.MIDDLENAME_ATTRIBUTE)%>
				</td>
				<td nowrap>&nbsp;</td>
			</tr>
			<tr align="left">
			<td nowrap>&nbsp;</td>
				<td nowrap class="fieldRequired" align="right"><display:get name="prm.global.registration.personal.lastname" />:&nbsp;&nbsp;</td>
				
				<td nowrap>
					<!-- Avinash: empty textbox  -->
					<input type="text" name="ecom_ShipTo_Postal_Name_Last" size="20" maxlength="60" onBlur="createDisplayName(this.value);" value='<c:out value="${registration.lastName}" />'>
                    <%=flagDirectoryProvided(registration, IPersonAttributes.LASTNAME_ATTRIBUTE)%>
				</td>
				<td nowrap>&nbsp;</td>
			</tr>
			<tr align="left">
			<td nowrap>&nbsp;</td>
				<td nowrap class="fieldNonRequired" align="right"><display:get name="prm.global.registration.personal.namesuffix" />:&nbsp;&nbsp;</td>
				
				<td nowrap>
					<!-- Avinash: empty textbox  -->
					<input type="text" name="ecom_ShipTo_Postal_Name_Suffix" size="8" maxlength="20" onBlur="createDisplayName(this.value);"  value='<c:out value="${registration.nameSuffix}" />'>
				    <span class="fieldNonRequired">&nbsp; <display:get name="prm.global.registration.personal.namesuffix.example" /></span>
                    <%=flagDirectoryProvided(registration, IPersonAttributes.SUFFIX_ATTRIBUTE)%>
				</td>
				<td nowrap>&nbsp;</td>
			</tr>
			<tr align="left">
			<td nowrap>&nbsp;</td>
				<td nowrap class="fieldRequired" align="right"><display:get name="prm.global.registration.personal.displayname" />:&nbsp;&nbsp;</td>
				
				<td nowrap>
					<!-- Avinash: empty textbox  -->
					<input type="text" name="displayName" size="20" maxlength="240" onFocus="this.select();" value='<c:out value="${registration.fullName}" />'>
				    <span class="fieldNonRequired">&nbsp; <display:get name="prm.global.registration.personal.displayname.example" /></span>
                    <%=flagDirectoryProvided(registration, IPersonAttributes.DISPLAYNAME_ATTRIBUTE)%>                
				</td>
				<td nowrap>&nbsp;</td>
			</tr>

			<tr align="left">
					<td nowrap colspan="4">&nbsp;</td>
		    </tr>

	<tr class="actionBar">
		<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><display:get name="prm.global.registration.address.header" /></td>		
		<td width="1%" align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>
		  <tr>
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.address.line1" />:&nbsp;&nbsp;</td>

            <td nowrap> 
            	<!-- Avinash: empty textbox  -->
              <input type="text" name="ecom_ShipTo_Postal_Street_Line1" size="40" maxlength="80" value='<c:out value="${registration.address1}" />'> 
              <%=flagDirectoryProvided(registration, IPersonAttributes.ADDRESS1_ATTRIBUTE)%>
             </td>
             <td nowrap>&nbsp;</td>
          </tr>
          <tr>
          <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldNonRequired"><display:get name="prm.global.registration.address.line2" />:&nbsp;&nbsp;</td>
            <td nowrap> 
            	<!-- Avinash: empty textbox  -->
              <input type="text" name="ecom_ShipTo_Postal_Street_Line2" size="40" maxlength="80" value='<c:out value="${registration.address2}" />'> 
              <%=flagDirectoryProvided(registration, IPersonAttributes.ADDRESS2_ATTRIBUTE)%>
            </td>
            <td nowrap>&nbsp;</td>
          </tr>
          <tr> 
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.address.city" />:&nbsp;&nbsp;</td>
            
            <td nowrap> 
            	<!-- Avinash: empty textbox  -->
              <input type="text" name="ecom_ShipTo_Postal_City" size="25" maxlength="50" value='<c:out value="${registration.city}" />'> 
              <%=flagDirectoryProvided(registration, IPersonAttributes.CITY_ATTRIBUTE)%>
   		    </td>
            <td nowrap>&nbsp;</td>

		  <tr> 
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.address.province" />:&nbsp;&nbsp;</td>
            <td nowrap> 
		<select name="ecom_ShipTo_Postal_StateProv">
		<option value=""><%=PropertyProvider.get("prm.registration.addressinfo.stateprovince.option.selectstate.value")%></option> <!-- Select State -->
		      <%=ProfileCodes.getStateCodeOptionList(registration.getState())%>
		</select>
              <%=flagDirectoryProvided(registration, IPersonAttributes.STATE_ATTRIBUTE)%>
            </td>
            <td nowrap>&nbsp;</td>
          </tr>
          <tr> 
          <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldNonRequired"><%=PropertyProvider.get("prm.registration.userprofile.zipcode.label")%>&nbsp;&nbsp;</td>
            <td nowrap> 
            	<!-- Avinash: empty textbox  -->
              <input type="text" name="ecom_ShipTo_Postal_PostalCode" size="15" maxlength="20" value='<c:out value="${registration.zipcode}" />'> 
              <%=flagDirectoryProvided(registration, IPersonAttributes.ZIPCODE_ATTRIBUTE)%>
            </td>
            <td nowrap>&nbsp;</td>
          </tr>
          <tr> 
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.address.country" />:&nbsp;&nbsp;</td>
            <td nowrap> 		
		<select name="ecom_ShipTo_Postal_CountryCode">
		<!-- <option value="">Select Country</option>  -->
		      <%=ProfileCodes.getCountryCodeOptionList(registration.getCountry())%>
		</select>
              <%=flagDirectoryProvided(registration, IPersonAttributes.COUNTRY_ATTRIBUTE)%>
            </td>
            <td nowrap>&nbsp;</td>
          </tr>
          <tr> 
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.address.workphone" />:&nbsp;&nbsp;</td>
            <td nowrap> 
            	<!-- Avinash: empty textbox  -->
              <input type="text" name="ecom_ShipTo_Telecom_Phone_Number" size="20" maxlength="20" value="<%=registration.getOfficePhone()%>">  <span class="fieldNonRequired"><display:get name="prm.global.registration.address.workphone.example" /></span>
              <%=flagDirectoryProvided(registration, IPersonAttributes.OFFICE_PHONE_ATTRIBUTE)%>
            </td>
            <td nowrap>&nbsp;</td>          
          </tr>
          <tr> 
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldNonRequired"><display:get name="prm.registration.userprofile.faxnumber.label" />&nbsp;&nbsp;</td>
            
            <td nowrap> 
            	<!-- Avinash: empty textbox  -->
              <input type="text" name="faxPhone" size="20" maxlength="20" value='<c:out value="${registration.faxPhone}" />'> <span class="fieldNonRequired"><display:get name="prm.global.registration.address.workfax.example" /></span>
              <%=flagDirectoryProvided(registration, IPersonAttributes.FAX_PHONE_ATTRIBUTE)%>
            </td>
            <td nowrap>&nbsp;</td>
          </tr>
          <tr>
            <td nowrap colspan="4">&nbsp;</td>
          </tr>

	<tr class="actionBar">
		<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><display:get name="prm.global.registration.financial.header" /></td>		
		<td width="1%" align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>
		  <tr>
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.financial.salary" />:&nbsp;&nbsp;</td>

            <td nowrap> 
              <input type="text" name="costByHour" size="40" maxlength="80" value='<c:out value="${registration.salary.costByHour}" />'> <span class="fieldNonRequired"><display:get name="prm.global.registration.financial.salary.example" /></span>
             </td>
             <td nowrap>&nbsp;</td>
          </tr>
		  <tr>
            <td nowrap>&nbsp;</td>
            <td nowrap>&nbsp;</td>
            <td nowrap class="notice"><display:get name="prm.global.registration.financial.salary.notice" />            
            </td>
            <td nowrap>&nbsp;</td>
          </tr>          

     <%-- ************************** LOCALIZATION PREFERENCES ***************************************** --%>

	<tr class="actionBar">
		<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><display:get name="prm.global.registration.localization.header" /></td>
		<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.localization.language.label" />:&nbsp;&nbsp;</td>
        <td nowrap>
    		<select name="languageCode">
                <%=brandManager.getSupportedLanguageOptionList(registration.getLanguageCode())%>
    		</select>
            &nbsp;
            <a href='javascript:popupHelp("profile_language");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15"></a>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
	<tr>
	    <td nowrap>&nbsp;</td>
	    <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.locale.label" />&nbsp;&nbsp;</td>
        <td nowrap>
    		<select name="localeCode">
                <%=ProfileCodes.getLocaleOptionList (registration.getLocaleCode())%>
		    </select>
            &nbsp;
            <a href='javascript:popupHelp("profile_locale");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15"></a>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.localization.timezone" />&nbsp;&nbsp;</td>
        <td nowrap>
    		<select name="timeZoneCode">
                <%=ProfileCodes.getTimeZoneOptionList(registration.getTimeZoneCode())%>
		    </select>
            &nbsp;
            <a href='javascript:popupHelp("profile_timezone");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15"></a>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
</table>
<br>

</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="back" />
	                <% if (registration.getLicenseContext().getSelectionType().equals(LicenseSelectionType.CREDIT_CARD)) { %>
		<tb:button type="next" />
	                <% } else { %>
		<tb:button type="finish" label='<%=PropertyProvider.get("prm.registration.userprofile.button.register.label")%>' />
	                <% } %>
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS space="personal"/>
</body>
</html>
<%!
    String flagDirectoryProvided(net.project.admin.RegistrationBean registration, String profileAttributeID) {
        StringBuffer result = new StringBuffer();
        if (registration.getDirectoryEntry().isAttributeProvided(profileAttributeID)) {
            result.append("<span class=\"tableContent\">&nbsp;"+PropertyProvider.get("prm.registration.userprofile.loaded.text")+"</span>");
        }
        return result.toString();
    }
%>