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
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Personal Profile -- Address"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.admin.RegistrationBean,
            net.project.base.Module,
            net.project.resource.ProfileCodes,
            net.project.security.AuthorizationFailedException,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.security.User,
            java.util.TimeZone,
            net.project.resource.IPersonAttributes,
            net.project.persistence.PersistenceException,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="brandManager" class="net.project.brand.BrandManager" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<%
    // Grab some useful values
    String jspRootURL = SessionManager.getJSPRootURL();
    String module = String.valueOf(net.project.base.Module.APPLICATION_SPACE);
	String orgLink = (String)session.getAttribute("orgLink");
%>

<%-- 
    Security Section
    Verify correct permissions were checked
--%>
<security:verifyAccess 
                module="<%=Integer.valueOf(module).intValue()%>"
                action="modify" 
/>
<%
    // Currently, only allow actual app admin to make changes
    if (!user.isApplicationAdministrator()) {
        throw new AuthorizationFailedException(PropertyProvider.get("prm.project.admin.profile.failed.security.validation.message"));
    }
%>
<%--
    End of Security Section
--%>

<%
    //Grab the userID; this is the user to be edited
    String userID = request.getParameter("userID");
    if (userID == null || userID.length() == 0) {
        throw new net.project.base.PnetException(PropertyProvider.get("prm.project.admin.profile.missing.parameter.message"));
    }

    // Clear out registration bean to ensure we're editing the correct user
    registration.clear();
    //Load pre-existing information for this user
    registration.setID(userID);
    try {
        registration.load();
    } catch (PersistenceException pe) {
        //Perhaps it was only the directory store that couldn't be contacted, try
        //to load only the local information and see if that works.
        registration.loadLocalInformation();
        response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/profile/" +
            "ProfileLoginController.jsp?module="+module+"&userID="+userID);
    }

    // Load values used for dropdowns
    String statelist = ProfileCodes.getStateCodeOptionList(registration.getState());
    String countrylist = ProfileCodes.getCountryCodeOptionList(registration.getCountry());
    String timezonelist = ProfileCodes.getTimeZoneOptionList(registration.getTimeZoneCode());
%>

<%----- Page-wide variables ------------------------------------------%>
<template:getDoctype />
<html>
<head>

<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= jspRootURL %>';
    var updatedProfile = false;

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    theForm = self.document.forms[0];
    isLoaded = true;
}

function cancel()    { self.document.location = JSPRootURL + "<%=orgLink%>"; }
function reset()    { document.registration.reset(); }
function submit()    { applyChanges(); }
function help()
{
    var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_profile&section=address";
    openwin_help(helplocation);
}
function validateForm(frm)
{
    if (!checkTextbox(frm.ecom_ShipTo_Postal_Street_Line1,"Address is a required field")) return false;
    if (!checkTextbox(frm.ecom_ShipTo_Postal_City,"City is a required field")) return false;
    if (!checkDropdown(frm.ecom_ShipTo_Postal_StateProv,"State/Province is a required field")) return false;
    if (!checkTextbox(frm.ecom_ShipTo_Postal_PostalCode,"Postal Code is a required field")) return false;
    if (!checkDropdown(frm.ecom_ShipTo_Postal_CountryCode,"Country is a required field")) return false;
    if (!checkDropdown(frm.timeZoneCode,"Time Zone is a required field")) return false;
    if (!checkDropdown(frm.languageCode,"Language is a required field")) return false;
    if (!checkTextbox(frm.ecom_ShipTo_Telecom_Phone_Number,"A Phone Number is a required field")) return false;
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
    if ((document.registration.nextPage.value == null) || (document.registration.nextPage.value == "")){
        document.registration.nextPage.value = '<%= "/admin/profile/ProfileAddress.jsp?userID=" + userID + "&module=" + module%>&action=<%=Action.MODIFY%>';
	  }	

    if (updatedProfile) {
    	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.profileaddress.updateprofile.confirm")%>', function(btn) { 			
			if( btn == 'yes' ){
				if (validateForm(document.registration)){
            		document.registration.submit();
            	}
			}		
		});
    } else {
        document.location = JSPRootURL+document.registration.nextPage.value;
    }
   
}

function tabClick(nextPage) {
    document.registration.nextPage.value = nextPage + '?module=<%=module%>&userID=<%=userID%>&action=<%=Action.MODIFY%>';
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


<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.profile.module.history">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:page display="Profile" 
                    jspPage='<%=jspRootURL + "/admin/profile/ProfileName.jsp"%>'
                    queryString='<%="userID="+userID+"&module="+module+"&action=" + net.project.security.Action.MODIFY%>' />
            <history:page level="1" display="Address" />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<br />
<tab:tabStrip>
    <tab:tab label="Name" href="javascript:tabClick('/admin/profile/ProfileName.jsp');" />
    <tab:tab label="Address" href="javascript:tabClick('/admin/profile/ProfileAddress.jsp');" selected="true" />
    <tab:tab label="Login" href="javascript:tabClick('/admin/profile/ProfileLoginController.jsp');" />
    <tab:tab label="License" href="javascript:tabClick('/admin/profile/ProfileLicense.jsp');" />
</tab:tabStrip>
<form name="registration" action="<%=jspRootURL + "/admin/profile/ProfileAddressProcessing.jsp"%>" method="post">
    <input type="hidden" name="module" value="<%=module%>">
    <input type="hidden" name="action" VALUE="<%="" + Action.MODIFY%>">
    <input type="hidden" name="theAction" value="apply">
    <input type="hidden" name="nextPage" value="">
    <input type="hidden" name="userID" value="<%=userID%>">
    <div align="center">
<table width="600" cellpadding=0 cellspacing=0 border=0>
    <tr>
        <td colspan="4" class="tableHeader">
            FIELDS IN BLACK ARE REQUIRED.
        </td>
    </tr>
<%  if (session.getValue("errorMsg") != null) { %>
    <tr align="left">
        <td colspan="4" align="left">
            <span class="warningTextRed"><%=session.getValue("errorMsg")%></span>
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
                    <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
                    <td colspan="2" valign="middle" class="ActionBar">
						<%=PropertyProvider.get("prm.project.admin.profile.address.information.label") %>
					</td>
                    <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldRequired">
        	<%=PropertyProvider.get("prm.project.admin.profile.address.line1.label") %>&nbsp;&nbsp;
        </td>
        <td nowrap>
            <input type="text" name="ecom_ShipTo_Postal_Street_Line1" size="40" maxlength="80" onchange="setUpdated('true');" value="<c:out value="${registration.address1}"/>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.ADDRESS1_ATTRIBUTE)%>
        </td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldNonRequired">
        	<%=PropertyProvider.get("prm.project.admin.profile.address.line2.label") %>
        	&nbsp;&nbsp;
        </td>
        <td nowrap>
            <input type="text" name="ecom_ShipTo_Postal_Street_Line2" size="40" maxlength="80" onchange="setUpdated('true');" value="<%=registration.getAddress2() == null ? "" :  registration.getAddress2() %>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.ADDRESS2_ATTRIBUTE)%>
        </td>
    </tr>
    <tr>
        <td nowrap></td>
        <td align="right" nowrap class="fieldRequired">
        	<%=PropertyProvider.get("prm.project.admin.profile.city.label") %>&nbsp;&nbsp;
        </td>
        <td nowrap>
            <input type="text" name="ecom_ShipTo_Postal_City" size="25" maxlength="80" onchange="setUpdated('true');" value="<c:out value="${registration.city}"/>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.CITY_ATTRIBUTE)%>
        </td>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldRequired">
        	<%=PropertyProvider.get("prm.project.admin.profile.state.province.label") %>
        &nbsp;&nbsp;</td>
        <td nowrap>
            <select name="ecom_ShipTo_Postal_StateProv" onchange="setUpdated('true');">
                <option value="">
                	<%=PropertyProvider.get("prm.project.admin.profile.select.state.label") %>
                </option>
                <%=statelist%>
            </select>
            <%=flagDirectoryProvided(registration, IPersonAttributes.STATE_ATTRIBUTE)%>
        </td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldRequired">
        	<%=PropertyProvider.get("prm.project.admin.profile.zip.postal.code.label") %>
        &nbsp;&nbsp;</td>
        <td nowrap>
            <input type="text" name="ecom_ShipTo_Postal_PostalCode" size="15" maxlength="20" onchange="setUpdated('true');" value="<%=registration.getZipcode() == null ? "" :  registration.getZipcode() %>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.ZIPCODE_ATTRIBUTE)%>
        </td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldRequired">
        	<%=PropertyProvider.get("prm.project.admin.profile.country.label") %>
        	&nbsp;&nbsp;</td>
        <td nowrap>
            <select name="ecom_ShipTo_Postal_CountryCode" onchange="setUpdated('true');">
                <option value="">
                	<%=PropertyProvider.get("prm.project.admin.profile.select.country.label") %>
                	</option>
                <%=countrylist%>
            </select>
            <%=flagDirectoryProvided(registration, IPersonAttributes.COUNTRY_ATTRIBUTE)%>
        </td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldRequired">
			<%=PropertyProvider.get("prm.project.admin.profile.work.phone.number.label") %>
		&nbsp;&nbsp;</td>
        <td nowrap>
            <input type="text" name="ecom_ShipTo_Telecom_Phone_Number" size="20" maxlength="20" onchange="setUpdated('true');" value="<c:out value="${registration.officePhone}"/>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.OFFICE_PHONE_ATTRIBUTE)%>
        </td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldNonRequired">
        	<%=PropertyProvider.get("prm.project.admin.profile.work.fax.number.label") %>
        &nbsp;&nbsp;</td>
        <td nowrap>
            <input type="text" name="faxPhone" size="20" maxlength="20" onchange="setUpdated('true');" value="<c:out value="${registration.faxPhone}"/>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.FAX_PHONE_ATTRIBUTE)%>
        </td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldNonRequired">
        	<%=PropertyProvider.get("prm.project.admin.profile.mobile.phone.number.label") %>
        &nbsp;&nbsp;</td>
        <td nowrap>
            <input type="text" name="mobilePhone" size="20" maxlength="20" onchange="setUpdated('true');" value="<c:out value="${registration.mobilePhone}"/>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.MOBILE_PHONE_ATTRIBUTE)%>
        </td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldNonRequired">
        	<%=PropertyProvider.get("prm.project.admin.profile.pager.number.label") %>
        &nbsp;&nbsp;</td>
        <td nowrap>
            <input type="text" name="pagerPhone" size="20" maxlength="20" onchange="setUpdated('true');" value="<c:out value="${registration.pagerPhone}"/>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.PAGER_PHONE_ATTRIBUTE)%>
        </td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldNonRequired">
			<%=PropertyProvider.get("prm.project.admin.profile.pager.email.label") %>
		&nbsp;&nbsp;</td>
        <td nowrap>
            <input type="text" name="pagerEmail" size="20" maxlength="20" onchange="setUpdated('true');" value="<c:out value="${registration.pagerEmail}"/>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.PAGER_EMAIL_ATTRIBUTE)%>
        </td>
    </tr>

    <%-- ************************** LOCALIZATION PREFERENCES ***************************************** --%>

    <tr class="actionBar">
        <td Colspan="4">
            <table border="0" width="100%" cellpadding="0" cellspacing="0">
                <tr>
                    <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
                    <td colspan="2" valign="middle" class="ActionBar">Localization Preferences</td>
                    <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
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
            <!-- a href='javascript:popupHelp("profile_language");' border="0"><img src="<%=SessionManager.getJSPRootURL()%>/images/help/popuphelp.gif" border="0" width="17" height="15"></a -->
        </td>
        <td nowrap>&nbsp;</td>
	</tr>
    <tr>
	    <td nowrap>&nbsp;</td>
	    <td align="right" nowrap class="fieldRequired"><display:get name="prm.global.registration.locale.label" />:&nbsp;&nbsp;</td>
        <td nowrap>
    		<select name="localeCode" onChange="setUpdated('true');">
                <%=ProfileCodes.getLocaleOptionList (registration.getLocaleCode())%>
		    </select>
            &nbsp;
            <!--  a href='javascript:popupHelp("profile_locale");' border="0"><img src="<%=SessionManager.getJSPRootURL()%>/images/help/popuphelp.gif" border="0" width="17" height="15"></a -->
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr>
        <td nowrap>&nbsp;</td>
        <td align="right" nowrap class="fieldRequired">
        	<%=PropertyProvider.get("prm.project.admin.profile.time.zone.label") %>
        	&nbsp;&nbsp;</td>
        <td nowrap>
            <select name="timeZoneCode" onChange="setUpdated('true');">
                <%=timezonelist%>
            </select>
        <!-- a href='javascript:popupHelp("profile_timezone");' border="0"><img src="<%=SessionManager.getJSPRootURL()%>/images/help/popuphelp.gif" border="0" width="17" height="15"></a -->
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