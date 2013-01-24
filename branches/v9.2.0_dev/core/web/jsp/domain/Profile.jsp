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
    info="New User Registration"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.resource.ProfileCodes,
            net.project.persistence.PersistenceException,
        	net.project.security.SessionManager,
			net.project.admin.RegistrationBean,
        	net.project.base.property.PropertyProvider,
            net.project.resource.IPersonAttributes,
			net.project.util.StringUtils"
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
	
	String theAction = request.getParameter("theAction");
	isflagDirectoryProvided = theAction != null && theAction.equals("back") ? false : true ; 
	
	RegistrationBean originalRegistration = new RegistrationBean();
	originalRegistration.setID(user.getID());
	originalRegistration.setEmail(user.getEmail());
	originalRegistration.load();
	
	String wizardMode = (String)pageContext.getAttribute("WizardMode" , pageContext.SESSION_SCOPE);
	
%>


<html>
<head>
<title><display:get name="prm.domain.profile.title" /></title>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>
<template:getSpaceCSS />

<template:import type="javascript" src="/src/checkEmail.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/cookie.js" />
<template:import type="javascript" src="/src/checkAlphaNumeric.js" />
<template:import type="javascript" src="/src/browser.js" />
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/window_functions.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>'; 
	
	function help(){
		var helplocation=JSPRootURL+"/help/Help.jsp?page=domainmigration_initiate&section=page1";
		openwin_help(helplocation);
	}   
	
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
			if (!checkTextbox(frm.ecom_ShipTo_Postal_Name_First,'<display:get name="prm.domain.profile.firstnamerequired.message" />')) return false;
			if (!checkTextbox(frm.ecom_ShipTo_Postal_Name_Last,'<display:get name="prm.domain.profile.lastnamerequired.message" />')) return false;
			if (!checkTextbox(frm.displayName,'<display:get name="prm.domain.profile.displaynamerequired.message" />')) return false;
			if (!checkTextbox(frm.ecom_ShipTo_Postal_Street_Line1,'<display:get name="prm.domain.profile.addressrequired.message" />')) return false;
			if (!checkTextbox(frm.ecom_ShipTo_Postal_City,'<display:get name="prm.domain.profile.cityrequired.message" />')) return false;
			if (!checkDropdown(frm.ecom_ShipTo_Postal_StateProv,'<display:get name="prm.domain.profile.staterequired.message" />')) return false;
			if (!checkDropdown(frm.ecom_ShipTo_Postal_CountryCode,'<display:get name="prm.domain.profile.countryrequired.message" />')) return false;
			if (!checkTextbox(frm.ecom_ShipTo_Telecom_Phone_Number,'<display:get name="prm.domain.profile.workphonerequired.message" />')) return false;
			if (!checkDropdown(frm.timeZoneCode,'<display:get name="prm.domain.profile.timezonerequired.message" />')) return false;
            if (!checkDropdown(frm.localeCode,'<display:get name="prm.domain.profile.localerequired.message" />')) return false;
            if (!checkDropdown(frm.languageCode,'<display:get name="prm.domain.profile.languagerequired.message" />')) return false;
			/*if (!checkDropdown(frm.dateFormatID,'<display:get name="prm.domain.profile.dateformatrequired.message" />')) return false;
			if (!checkDropdown(frm.timeFormatID,'<display:get name="prm.domain.profile.timeformatrequired.message" />')) return false;
            */
            if(!checkPhoneNo(frm.ecom_ShipTo_Telecom_Phone_Number, "<display:get name='prm.registration.userprofile.invalidphoneno.message'/>")) return false;			
			return true;
		}
		
		function processForm()
		{
			if(validateForm(document.registration))
				document.registration.submit();
		}
		function createDisplayName(){
			var displayName="";
			var fname = document.registration.ecom_ShipTo_Postal_Name_First.value;
			var lname = document.registration.ecom_ShipTo_Postal_Name_Last.value;
		
			if (fname != "") displayName += fname + " ";
			if (lname != "") displayName += lname + " ";
		
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

		function next() {
            theAction("next");
			processForm();
		}

		function back() {
            theAction("back");
			theForm.submit();
		}

        function popupHelp(page) {
        	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page="+page;
	        openwin_help(helplocation);
        }

</script>
</head>

<%------------------------------------------------------------------------
  -- Start of Form Body
  ----------------------------------------------------------------------%>
<body class="main" leftmargin=0 topmargin=0 onLoad="setup(); highlightError();">

<table width="100%" cellpadding="1" cellspacing="0" border="0">
    <tr>
        <td class="navBg" colspan="2">&nbsp;</td>
    </tr>
</table>

<br />

<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/domain/ProfileController.jsp"%>" method="post" onSubmit="return validateForm(this);">
    <input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%= net.project.base.Module.PERSONAL_SPACE %>">
    <input type="hidden" name="fromPage" value="profile">
	

<div align="center">
<table width="80%" cellpadding=0 cellspacing=0 border=0>
	<tr>
		<td colspan="4">
			<noscript><b><display:get name="prm.domain.profile.cookiesturnedonrequired.message" />
			<a href="<%= SessionManager.getAppURL() %>/help/HelpDesk.jsp?page=browser_requirements"><display:get name="prm.domain.profile.cookiesturnonhelp.link" /></a> </b></noscript>
		</td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr class="actionBar">
		<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><%=PropertyProvider.get("prm.personal.domainmigration.profilepage.channel.title") %></td>		
		<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>
	
	<tr>
		<td>&nbsp;</td>			
		<td colspan="4" wrap class="instructions"><%=PropertyProvider.get("prm.personal.domainmigration.profilepage.initial.instructions") %>
		</td>		
	</tr>
	<tr>
		<td colspan="4">
            <table width="100%" border="0">
            <tr>
                <td class="fieldNonRequired"><display:get name="prm.global.registration.username.label" />&nbsp;</td>
                <td class="tableContent"><jsp:getProperty name="registration" property="login" /></td>
                <td class="fieldNonRequired"><display:get name="prm.global.registration.emailaddress.label" />&nbsp;</td>
                <td class="tableContent"><jsp:getProperty name="registration" property="email" /></td>
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
					<%String namePrefix = registration.getNamePrefix();%>
					<input type="text" name="ecom_ShipTo_Postal_Name_Prefix" size="8" maxlength="20" onBlur="createDisplayName(this.value);" value='<%=(StringUtils.isEmpty(namePrefix) || "null".equals(namePrefix)) ? "" : namePrefix%>'>
				    <span class="fieldNonRequired">&nbsp; <display:get name="prm.global.registration.personal.nameprefix.example" /></span>
                    <%=flagDirectoryProvided(registration, IPersonAttributes.PREFIX_ATTRIBUTE)%>
				</td>
				<td nowrap>&nbsp;</td>
			</tr>
			<tr align="left">
			<td nowrap>&nbsp;</td>
				<td nowrap class="fieldRequired" align="right"><display:get name="prm.global.registration.personal.firstname" />:&nbsp;&nbsp;</td>
				<td nowrap>
					<input type="text" name="ecom_ShipTo_Postal_Name_First" size="20" maxlength="40" onBlur="createDisplayName(this.value);" value="<jsp:getProperty name="registration" property="firstName" />">
                    <%=flagDirectoryProvided(registration, IPersonAttributes.FIRSTNAME_ATTRIBUTE)%>
				</td>
				<td nowrap>&nbsp;</td>
			</tr>
			<tr align="left">
			<td nowrap>&nbsp;</td>
				<td nowrap class="fieldNonRequired" align="right"><display:get name="prm.global.registration.personal.middlename" />:&nbsp;&nbsp;</td>
				<td nowrap>
					<%String middleName = registration.getMiddleName();%>
					<input type="text" name="ecom_ShipTo_Postal_Name_Middle" size="20" maxlength="40" onBlur="createDisplayName(this.value);" value='<%=(StringUtils.isEmpty(middleName) || "null".equals(middleName)) ? "" : middleName%>'>
                    <%=flagDirectoryProvided(registration, IPersonAttributes.MIDDLENAME_ATTRIBUTE)%>
				</td>
				<td nowrap>&nbsp;</td>
			</tr>
			<tr align="left">
			<td nowrap>&nbsp;</td>
				<td nowrap class="fieldRequired" align="right"><display:get name="prm.global.registration.personal.lastname" />:&nbsp;&nbsp;</td>
				
				<td nowrap>
					<input type="text" name="ecom_ShipTo_Postal_Name_Last" size="20" maxlength="60" onBlur="createDisplayName(this.value);" value="<jsp:getProperty name="registration" property="lastName" />">
                    <%=flagDirectoryProvided(registration, IPersonAttributes.LASTNAME_ATTRIBUTE)%>
				</td>
				<td nowrap>&nbsp;</td>
			</tr>
			<tr align="left">
			<td nowrap>&nbsp;</td>
				<td nowrap class="fieldNonRequired" align="right"><display:get name="prm.global.registration.personal.namesuffix" />:&nbsp;&nbsp;</td>
				
				<td nowrap>
				    <%String nameSuffix = registration.getNameSuffix();%>
					<input type="text" name="ecom_ShipTo_Postal_Name_Suffix" size="8" maxlength="20" onBlur="createDisplayName(this.value);"  value='<%=(StringUtils.isEmpty(nameSuffix) || "null".equals(nameSuffix)) ? "" : nameSuffix%>'>
				    <span class="fieldNonRequired">&nbsp; <display:get name="prm.global.registration.personal.namesuffix.example" /></span>
                    <%=flagDirectoryProvided(registration, IPersonAttributes.SUFFIX_ATTRIBUTE)%>
				</td>
				<td nowrap>&nbsp;</td>
			</tr>
			<tr align="left">
			<td nowrap>&nbsp;</td>
				<td nowrap class="fieldRequired" align="right"><display:get name="prm.global.registration.personal.displayname" />:&nbsp;&nbsp;</td>
				
				<td nowrap>
					<input type="text" name="displayName" size="20" maxlength="240" onFocus="this.select();" value="<jsp:getProperty name="registration" property="fullName" />">
				    <span class="fieldNonRequired">&nbsp; <display:get name="prm.global.registration.personal.displayname.example" /></span>
                    <%=flagDirectoryProvided(registration, IPersonAttributes.DISPLAYNAME_ATTRIBUTE)%>                
				</td>
				<td nowrap>&nbsp;</td>
			</tr>

			<tr align="left">
					<td nowrap colspan="4">&nbsp;</td>
		    </tr>

	<tr class="actionBar">
		<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><display:get name="prm.global.registration.address.header" /></td>		
		<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>
		  <tr>
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.address.line1" />:&nbsp;&nbsp;</td>

            <td nowrap> 
              <input type="text" name="ecom_ShipTo_Postal_Street_Line1" size="40" maxlength="80" value="<jsp:getProperty name="registration" property="address1" />"> 
              <%=flagDirectoryProvided(registration, IPersonAttributes.ADDRESS1_ATTRIBUTE)%>
             </td>
             <td nowrap>&nbsp;</td>
          </tr>
          <tr>
          <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldNonRequired"><display:get name="prm.global.registration.address.line2" />:&nbsp;&nbsp;</td>
            <td nowrap> 
              <%String address2 = registration.getAddress2();%>
              <input type="text" name="ecom_ShipTo_Postal_Street_Line2" size="40" maxlength="80" value='<%=(StringUtils.isEmpty(address2) || "null".equals(address2)) ? "" : address2%>'>
              <%=flagDirectoryProvided(registration, IPersonAttributes.ADDRESS2_ATTRIBUTE)%>
            </td>
            <td nowrap>&nbsp;</td>
          </tr>
          <tr> 
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.address.city" />:&nbsp;&nbsp;</td>
            
            <td nowrap> 
              <input type="text" name="ecom_ShipTo_Postal_City" size="25" maxlength="50" value="<jsp:getProperty name="registration" property="city" />">
              <%=flagDirectoryProvided(registration, IPersonAttributes.CITY_ATTRIBUTE)%>
   		    </td>
            <td nowrap>&nbsp;</td>

		  <tr> 
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.address.province" />:&nbsp;&nbsp;</td>
            <td nowrap> 
		<select name="ecom_ShipTo_Postal_StateProv">
		<option value="">Select State</option>
		      <%=ProfileCodes.getStateCodeOptionList(registration.getState())%>
		</select>
              <%=flagDirectoryProvided(registration, IPersonAttributes.STATE_ATTRIBUTE)%>
            </td>
            <td nowrap>&nbsp;</td>
          </tr>
          <tr> 
          <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldNonRequired"><display:get name="prm.global.registration.address.postalcode" />:&nbsp;&nbsp;</td>
            <td nowrap> 
              <input type="text" name="ecom_ShipTo_Postal_PostalCode" size="15" maxlength="20" value="<jsp:getProperty name="registration" property="zipcode" />">
              <%=flagDirectoryProvided(registration, IPersonAttributes.ZIPCODE_ATTRIBUTE)%>
            </td>
            <td nowrap>&nbsp;</td>
          </tr>
          <tr> 
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.address.country" />:&nbsp;&nbsp;</td>
            <td nowrap> 		
		<select name="ecom_ShipTo_Postal_CountryCode">
		<option value="">Select Country</option> 
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
              <input type="text" name="ecom_ShipTo_Telecom_Phone_Number" size="20" maxlength="20" value="<jsp:getProperty name="registration" property="officePhone" />"> <span class="fieldNonRequired"><display:get name="prm.global.registration.address.workphone.example" /></span>
              <%=flagDirectoryProvided(registration, IPersonAttributes.OFFICE_PHONE_ATTRIBUTE)%>
            </td>
            <td nowrap>&nbsp;</td>          
          </tr>
          <tr> 
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldNonRequired"><display:get name="prm.global.registration.address.workfax" />:&nbsp;&nbsp;</td>
            
            <td nowrap> 
            <%String faxPhone = registration.getFaxPhone();%> 
              <input type="text" name="faxPhone" size="20" maxlength="20" value='<%=(StringUtils.isEmpty(faxPhone) || "null".equals(faxPhone)) ? "" : faxPhone%>'> <span class="fieldNonRequired"><display:get name="prm.global.registration.address.workfax.example" /></span>
              <%=flagDirectoryProvided(registration, IPersonAttributes.FAX_PHONE_ATTRIBUTE)%>
            </td>
            <td nowrap>&nbsp;</td>
          </tr>
          <tr>
            <td nowrap colspan="4">&nbsp;</td>
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
        <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.locale.label" />:&nbsp;&nbsp;</td>
        <td nowrap>
    		<select name="localeCode">
                <%=ProfileCodes.getLocaleOptionList (registration.getLocaleCode())%>
		    </select>
            &nbsp;
            <a href='javascript:popupHelp("profile_locale");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15" title='<%=PropertyProvider.get("prm.global.registration.localization.locale.tooltip.title")%>'></a>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.localization.timezone" />:&nbsp;&nbsp;</td>
        <td nowrap>
    		<select name="timeZoneCode">
		        <%=ProfileCodes.getTimeZoneOptionList(originalRegistration.getTimeZoneCode())%>
		    </select>
            &nbsp;
            <a href='javascript:popupHelp("profile_timezone");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15" title='<%=PropertyProvider.get("prm.global.registration.localization.timezone.tooltip.title")%>'></a>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true">
				<tb:band name="action">
					<tb:button type="cancel" />
					<tb:button type="back" />
					<tb:button type="next" />
				</tb:band>
</tb:toolbar>

</div>
	
</form>
</body>
<template:getSpaceJS />
</html>
<%!
	boolean isflagDirectoryProvided = false ;
    String flagDirectoryProvided(net.project.admin.RegistrationBean registration, String profileAttributeID) {
        StringBuffer result = new StringBuffer();
        if (isflagDirectoryProvided && registration.getDirectoryEntry().isAttributeProvided(profileAttributeID)) {
            result.append("<span class=\"tableContent\">&nbsp;*&nbsp; - Loaded from Directory</span>");
        }
        return result.toString();
    }
%>