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
|   $Revision: 20770 $
|       $Date: 2010-04-29 09:23:10 -0300 (jue, 29 abr 2010) $
|
|--------------------------------------------------------------------%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Personal Profile -- Address"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User,
            net.project.security.SessionManager,
        	net.project.admin.RegistrationBean,
        	net.project.resource.ProfileCodes,
        	net.project.persistence.PersistenceException,
            net.project.resource.IPersonAttributes"
%>
<%!
    String query=null;
	String statelist = null;
	String countrylist = null;
String timezonelist = null;

%>

	<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
	<jsp:useBean id="user" class="net.project.security.User" scope="session" />
    <jsp:useBean id="brandManager" class="net.project.brand.BrandManager" scope="session" />


<%
	if (registration.getID() == null)
	{
		registration.setID(user.getID());
		registration.load();
	}

    //load the states
    try {
		statelist = ProfileCodes.getStateCodeOptionList(registration.getState());
    }
	catch(PersistenceException e) {
		statelist = "Error with States";
    }

   //load the counties
    try {
		countrylist = ProfileCodes.getCountryCodeOptionList(registration.getCountry());
    }
	catch(PersistenceException e) {
		countrylist = "Error with Countries";
    }

    timezonelist = ProfileCodes.getTimeZoneOptionList(registration.getTimeZoneCode());

%>

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />
<template:import type="javascript" src="/src/checkEmail.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
    var updatedProfile = false;

function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel()	{ 
<%--Avinash: bfd 3234  Cancel button in the Personal Profile page navigates to Personal Work Space--%>
	var referer = "<%= (String)session.getAttribute("referer")== null ?  SessionManager.getJSPRootURL() + "/personal/Main.jsp?module="+ net.project.base.Module.PERSONAL_SPACE  : SessionManager.getJSPRootURL() + "/" + (String)session.getAttribute("referer")+ "?module=" + net.project.base.Module.PROJECT_SPACE %>";
	self.document.location = referer ; 
}
function reset()	{ document.registration.reset(); }
function submit()	{ applyChanges(); }
function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=profile_personal&section=address";
	openwin_help(helplocation);
}
function validateForm(frm)
{
   if (!checkTextbox(frm.ecom_ShipTo_Postal_Street_Line1,'<%=PropertyProvider.get("prm.personal.profile.address.addressrequired.message")%>')) return false;
   if (!checkTextbox(frm.ecom_ShipTo_Postal_City,'<%=PropertyProvider.get("prm.personal.profile.address.cityrequired.message")%>')) return false;
   if (!checkDropdown(frm.ecom_ShipTo_Postal_StateProv,'<%=PropertyProvider.get("prm.personal.profile.address.staterequired.message")%>')) return false;
   if (!checkTextbox(frm.ecom_ShipTo_Postal_PostalCode,'<%=PropertyProvider.get("prm.personal.profile.address.postalcoderequired.message")%>')) return false;
   if (!checkDropdown(frm.ecom_ShipTo_Postal_CountryCode,'<%=PropertyProvider.get("prm.personal.profile.address.countryrequired.message")%>')) return false;
   if (!checkTextbox(frm.ecom_ShipTo_Telecom_Phone_Number,'<%=PropertyProvider.get("prm.personal.profile.address.phonerequired.message")%>')) return false;
   if (!checkDropdown(frm.timeZoneCode,'<%=PropertyProvider.get("prm.personal.profile.address.timezonerequired.message")%>')) return false;
   if (frm.pagerEmail.value != '') {
   		if (!checkEmail(frm.pagerEmail,'<%=PropertyProvider.get("prm.personal.profile.address.email.invalid.error.message")%>'))return false;
   }
   if(!checkIsNumber(frm.pagerPhone,'<%=PropertyProvider.get("prm.personal.profile.address.pagerphone.invalid.error.message")%>'))return false;
   return true;
}

function highlightError(){
   document.registration.ecom_ShipTo_Postal_Street_Line1.focus();
   return true;
}

function setUpdated(updated){
   updatedProfile = updated;
}

function applyChanges() {
   if ((document.registration.nextPage.value == null) || (document.registration.nextPage.value == ""))
		document.registration.nextPage.value = "ProfileAddress.jsp";
   if (updatedProfile) {
      	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.personal.profile.address.update.message")%>', function(btn) { 
			if(btn == 'yes') {
				if (validateForm(document.registration)){
				 	document.registration.submit();
      			}
			} else {
			 	document.location = document.registration.nextPage.value+"?module=<%= net.project.base.Module.PERSONAL_SPACE %>";
			}
		});
   }else{
   		document.location = document.registration.nextPage.value+"?module=<%= net.project.base.Module.PERSONAL_SPACE %>";
   }
}

function tabClick(nextPage) {
    document.registration.nextPage.value = nextPage;
	applyChanges();
}

function popupHelp(page) {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page="+page;
	openwin_help(helplocation);
}

</script>


</head>

<body class="main" id="bodyWithFixedAreasSupport" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0 onLoad="highlightError();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.profile.module.history" showSpaceDetails="false">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page level="1" display='<%=PropertyProvider.get("prm.personal.profile.module.history")%>' 
					jspPage='<%=SessionManager.getJSPRootURL() + "/personal/ProfileName.jsp"%>'
					queryString='<%="module=" + net.project.base.Module.PERSONAL_SPACE%>' />
			<history:page displayToken="prm.personal.profile.address.tab" />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br />
<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.name.tab")%>' href="javascript:tabClick('ProfileName.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.address.tab")%>' href="javascript:tabClick('ProfileAddress.jsp');" selected="true" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.login.tab")%>' href="javascript:tabClick('ProfileLoginController.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.license.tab")%>' href="javascript:tabClick('ProfileLicense.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.domain.tab")%>' href="javascript:tabClick('ProfileDomainMigration.jsp');" />
</tab:tabStrip>
<form name="registration" action="ProfileAddressProcessing.jsp" method="post">
    <input type="hidden" name="module" value="<%= net.project.base.Module.PERSONAL_SPACE %>">
    <input type="hidden" name="theAction" value="apply">
	<input type="hidden" name="nextPage" value="">
	<div align="center">
<table width="600" cellpadding=0 cellspacing=0 border=0>
    <tr>
		<td colspan="4" class="tableHeader">
			<%=PropertyProvider.get("prm.global.display.requiredfield")%>
		</td>
	</tr>
 
<%
if (session.getValue("errorMsg") != null)
{
%>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr align="left">
		<td colspan="4">
			<span class="warningTextRed"><%= session.getValue("errorMsg") %></span>
		</td>
	</tr>
<%
session.removeValue("errorMsg");
}
%>

	<tr class="actionBar">
    <td Colspan="4">
        <table border="0" width="100%" cellpadding="0" cellspacing="0">
        <tr>
		<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><%=PropertyProvider.get("prm.personal.profile.address.channel.info.title")%></td>
		<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
        </tr>
        </table>
        </td>
	</tr>

          <tr>
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.personal.profile.address.address1.label")%>&nbsp;&nbsp;</td>

            <td nowrap>
              <input type="text" name="ecom_ShipTo_Postal_Street_Line1" size="40" maxlength="80" onchange="setUpdated('true');" value="<jsp:getProperty name="registration" property="address1" />">
              <%=flagDirectoryProvided(registration, IPersonAttributes.ADDRESS1_ATTRIBUTE)%>
            </td>
           </tr>
          <tr>
          <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldNonRequired"><%=PropertyProvider.get("prm.personal.profile.address.address2.label")%>&nbsp;&nbsp;</td>
            <td nowrap>
              <input type="text" name="ecom_ShipTo_Postal_Street_Line2" size="40" maxlength="80" onchange="setUpdated('true');" value="<%=registration.getAddress2() == null ? "" : registration.getAddress2() %>">
              <%=flagDirectoryProvided(registration, IPersonAttributes.ADDRESS2_ATTRIBUTE)%>
            </td>
          </tr>
          <tr>
            <td nowrap></td>
            <td align="right" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.personal.profile.address.city.label")%>&nbsp;&nbsp;</td>

            <td nowrap>
              <input type="text" name="ecom_ShipTo_Postal_City" size="25" maxlength="80" onchange="setUpdated('true');" value="<jsp:getProperty name="registration" property="city" />">
              <%=flagDirectoryProvided(registration, IPersonAttributes.CITY_ATTRIBUTE)%>
            </td>
          </tr>
          <tr>
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.personal.profile.address.state.label")%>&nbsp;&nbsp;</td>
            <td nowrap>
		<select name="ecom_ShipTo_Postal_StateProv" onchange="setUpdated('true');">
		<option value=""><%=PropertyProvider.get("prm.personal.profile.address.state.option.select.name")%></option>
		<%
		out.print(statelist);
		%>
		</select>
              <%=flagDirectoryProvided(registration, IPersonAttributes.STATE_ATTRIBUTE)%>
            </td>
          </tr>
          <tr>
          <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.personal.profile.address.zip.label")%>&nbsp;&nbsp;</td>
            <td nowrap>
              <input type="text" name="ecom_ShipTo_Postal_PostalCode" size="15" maxlength="20" onchange="setUpdated('true');" value="<%=registration.getZipcode() == null ? "" : registration.getZipcode() %>">
              <%=flagDirectoryProvided(registration, IPersonAttributes.ZIPCODE_ATTRIBUTE)%>
            </td>
          </tr>
          <tr>
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.personal.profile.address.country.label")%>&nbsp;&nbsp;</td>

            <td nowrap>
		<select name="ecom_ShipTo_Postal_CountryCode" onchange="setUpdated('true');">
		<%
		// see if you can make this seem cleaner using just a query to put US first in the list
		out.print(countrylist);
        %>
		</select>
              <%=flagDirectoryProvided(registration, IPersonAttributes.COUNTRY_ATTRIBUTE)%>
            </td>
          </tr>
          <tr>
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.personal.profile.address.workphone.label")%>&nbsp;&nbsp;</td>

            <td nowrap>
              <input type="text" name="ecom_ShipTo_Telecom_Phone_Number" size="20" maxlength="20" onchange="setUpdated('true');" value="<jsp:getProperty name="registration" property="officePhone" />">
              <%=flagDirectoryProvided(registration, IPersonAttributes.OFFICE_PHONE_ATTRIBUTE)%>
            </td>
          </tr>
          <tr>
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldNonRequired"><%=PropertyProvider.get("prm.personal.profile.address.workfax.label")%>&nbsp;&nbsp;</td>

            <td nowrap>
              <input type="text" name="faxPhone" size="20" maxlength="20" onchange="setUpdated('true');" value='<c:out value="${registration.faxPhone}" />'>
              <%=flagDirectoryProvided(registration, IPersonAttributes.FAX_PHONE_ATTRIBUTE)%>
            </td>
          </tr>
          <tr>
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldNonRequired"><%=PropertyProvider.get("prm.personal.profile.address.mobilephone.label")%>&nbsp;&nbsp;</td>
            <td nowrap>
              <input type="text" name="mobilePhone" size="20" maxlength="20" onchange="setUpdated('true');" value='<c:out value="${registration.mobilePhone}" />'>
              <%=flagDirectoryProvided(registration, IPersonAttributes.MOBILE_PHONE_ATTRIBUTE)%>
            </td>
          </tr>
          <tr>
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldNonRequired"><%=PropertyProvider.get("prm.personal.profile.address.pagernumber.label")%>&nbsp;&nbsp;</td>
            <td nowrap>
              <input type="text" name="pagerPhone" size="20" maxlength="20" onchange="setUpdated('true');" value='<c:out value="${registration.pagerPhone}" />'>
              <%=flagDirectoryProvided(registration, IPersonAttributes.PAGER_PHONE_ATTRIBUTE)%>
            </td>
          </tr>
          <tr>
            <td nowrap>&nbsp;</td>
            <td align="right" nowrap class="fieldNonRequired"><%=PropertyProvider.get("prm.personal.profile.address.pageremail.label")%>&nbsp;&nbsp;</td>
            <td nowrap>
              <input type="text" name="pagerEmail" size="20" maxlength="240" onchange="setUpdated('true');" value='<c:out value="${registration.pagerEmail}" />'>
              <%=flagDirectoryProvided(registration, IPersonAttributes.PAGER_EMAIL_ATTRIBUTE)%>
            </td>
          </tr>

          <%-- ************************** LOCALIZATION PREFERENCES ***************************************** --%>

	<tr class="actionBar">
        <td Colspan="4">
            <table border="0" width="100%" cellpadding="0" cellspacing="0">
            <tr>
		        <td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		        <td colspan="2" valign="middle" class="ActionBar"><%=PropertyProvider.get("prm.personal.profile.address.channel.localization.title")%></td>
		        <td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
            </tr>
            </table>
        </td>
	</tr>
    <tr>
	    <td nowrap>&nbsp;</td>
	    <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.localization.language.label" />:&nbsp;&nbsp;</td>
        <td nowrap>
    		<select name="languageCode" onChange="setUpdated('true');">
                <%=brandManager.getSupportedLanguageOptionList(registration.getLanguageCode())%>
    		</select>
            &nbsp;
            <!-- a href='javascript:popupHelp("profile_language");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15"></a -->
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr>
	    <td nowrap>&nbsp;</td>
	    <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.locale.label" />&nbsp;&nbsp;</td>
        <td nowrap>
    		<select name="localeCode" onChange="setUpdated('true');">
                <%=ProfileCodes.getLocaleOptionList (registration.getLocaleCode())%>
		    </select>
            &nbsp;
            <!-- a href='javascript:popupHelp("profile_locale");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15"></a -->
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.personal.profile.address.timezone.label")%>&nbsp;&nbsp;</td>
        <td nowrap>
    		<select name="timeZoneCode" onChange="setUpdated('true');">
        		<%=timezonelist%>
    		</select>
            &nbsp;
            <!-- a href='javascript:popupHelp("profile_timezone");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15"></a -->
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
</table>
</div>

<%-- Action Bar --%>
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceJS />
</body>
</html>
<%!
    String flagDirectoryProvided(net.project.admin.RegistrationBean registration, String profileAttributeID) {
        StringBuffer result = new StringBuffer();
        if (registration.getDirectoryEntry().isAttributeProvided(profileAttributeID)) {
            result.append("<span class=\"tableContent\">&nbsp;*&nbsp; - Loaded from Directory</span>");
        }
        return result.toString();
    }
%>
