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
    info="Profile Name"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.admin.RegistrationBean,
            net.project.base.Module,
            net.project.security.AuthorizationFailedException,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.security.User,
            net.project.resource.IPersonAttributes,
            net.project.persistence.PersistenceException,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>
<%
    // Grab some useful values
    String jspRootURL = SessionManager.getJSPRootURL();
    String module = String.valueOf(net.project.base.Module.APPLICATION_SPACE);
	String orgLink = (String)session.getAttribute("orgLink");
%>

<%
    // Currently, only allow actual app admin to make changes
    if (!user.isApplicationAdministrator()) {
        throw new AuthorizationFailedException("Failed security validation: This functionality is only available to application administrators.");
    }
%>
<%--
    End of Security Section
--%>

<%
    //Grab the userID; this is the user to be edited
    String userID = request.getParameter("userID");
    if (userID == null || userID.length() == 0) {
        throw new net.project.base.PnetException("Missing parameter in ProfileName.jsp");
    }

    // Clear out registration bean to ensure we're editing the correct user
    registration.clear();
    //Load pre-existing information for this user
    registration.setID(userID);

    try {
        registration.load();
        // Update the registration bean from the directory entry
        registration.populateFromDirectoryEntry();
    } catch (PersistenceException pe) {
        //Perhaps it was only the directory store that couldn't be contacted, try
        //to load only the local information and see if that works.
        registration.loadLocalInformation();
        response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/profile/" +
            "ProfileLoginController.jsp?module="+module+"&userID="+userID);
    }

%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkEmail.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
    var updatedProfile = false;

    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= jspRootURL %>';  

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    theForm = self.document.forms[0];
    isLoaded = true;
}

function cancel()    { self.document.location = JSPRootURL + "<%=orgLink%>"; }
function reset()    { theForm.reset(); }
function submit() {		
	applyChanges(); 	
}

function validateForm(frm) {
    if (!checkTextbox(frm.ecom_ShipTo_Postal_Name_First,"First Name is a required field")) return false;
    if (!checkTextbox(frm.ecom_ShipTo_Postal_Name_Last,"Last Name is a required field")) return false;
    if (!checkTextbox(frm.displayName,"Display Name is a required field")) return false;
    if (!checkEmail(frm.ecom_ShipTo_Online_Email,"Email must be a valid address. \neg: yourname@domain.com")) return false;
        
	// checking for Skills/Bio field value length which should contain <= 300 characters
	frm.skillsBio.value = trim(frm.skillsBio.value);
	if(frm.skillsBio.value != null && frm.skillsBio.value != '' && frm.skillsBio.value.length > 300) {
		extAlert('Error Message', '<%=PropertyProvider.get("prm.personal.profile.name.validskillsbio.message")%>', Ext.MessageBox.ERROR);
		return false;
	}
	
    return true;
}

function applyChanges() {
    if ((document.registration.nextPage.value == null) || (document.registration.nextPage.value == "")){
        document.registration.nextPage.value = '<%="/admin/profile/ProfileName.jsp?userID=" + userID + "&module=" + module+"&action="+Action.MODIFY%>';
	}
    if (updatedProfile) {
	    Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.profileaddress.confirm")%>', function(btn) { 
			if(btn == 'yes'){
				if (validateForm(document.registration)){
		            document.registration.submit();
		          }  
				}
		});
    } else {
        document.location = JSPRootURL + document.registration.nextPage.value;
    }
}

function setUpdated(updated){
    updatedProfile = updated;
}

function help()
{
    var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_profile&section=name";
    openwin_help(helplocation);
}


function highlightError(){
<% if (session.getValue("errorObj") != null) { %>
    document.registration.<%=session.getValue("errorObj")%>.focus();
<% } else { %>
    document.registration.ecom_ShipTo_Postal_Name_Prefix.focus();
<% } %>
    return true;
}

function tabClick(nextPage) {
    document.registration.nextPage.value = nextPage + '?module=<%=module%>&userID=<%=userID%>&action=<%=Action.MODIFY%>';
    applyChanges();
}

</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0 onLoad="setup();highlightError();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />


<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.profile.module.history">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:page display="Profile" 
                    jspPage='<%=jspRootURL + "/admin/profile/ProfileName.jsp"%>'
                    queryString='<%="userID="+userID+"&module="+module+"&action=" + net.project.security.Action.MODIFY%>' />
            <history:page level="1" display="Name" />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<br />

<%-- Tab Bar --%>
<tab:tabStrip>
    <tab:tab label="Name" href="javascript:tabClick('/admin/profile/ProfileName.jsp');" selected="true" />
    <tab:tab label="Address" href="javascript:tabClick('/admin/profile/ProfileAddress.jsp');" />
    <tab:tab label="Login" href="javascript:tabClick('/admin/profile/ProfileLoginController.jsp');" />
    <tab:tab label="License" href="javascript:tabClick('/admin/profile/ProfileLicense.jsp');" />
</tab:tabStrip>

<form name="registration" action="<%=jspRootURL + "/admin/profile/ProfileNameProcessing.jsp"%>" method="post">
    <input type="hidden" name="module" value="<%=module%>">
    <input type="hidden" name="action" value="<%="" + Action.MODIFY%>">
    <input type="hidden" name="nextPage" value="">
    <input type="hidden" name="theAction" value="apply">
    <input type="hidden" name="userID" value="<%=userID%>">

<div align="center">
    <table width="600" cellpadding=0 cellspacing=0 border=0>
    <tr>
        <td colspan="4" class="tableHeader">
             <%=PropertyProvider.get("prm.project.admin.profile.provide.information.label") %><BR>
            <%=PropertyProvider.get("prm.project.admin.profile.see.our.strict.label") %>
             <a href="<%= jspRootURL %>/help/HelpDesk.jsp?page=privacy" target="_blank">
             <%=PropertyProvider.get("prm.project.admin.profile.privacy.policy.label") %>
             </A> and <a href="<%=jspRootURL%>/help/HelpDesk.jsp?page=security_general" target="_blank">Security Policy</a>.<p>
        </td>
    </tr>
    <tr>
        <td colspan="4" class="tableHeader">
            <%=PropertyProvider.get("prm.project.admin.profile.fileds.required.label") %>
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
    <td colspan="4">
        <table border="0" width="100%" cellpadding="0" cellspacing="0">
        <tr>
        <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
        <td colspan="2" valign="middle" class="ActionBar">
        	<%=PropertyProvider.get("prm.project.admin.profile.name.email.information.label") %>
        	</td>
        <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
        </tr>
        </table>
        </td>
    </tr>

    <tr align="left">
        <td nowrap>&nbsp;</td>
        <td nowrap class="fieldNonRequired" align="right"> 
        	<%=PropertyProvider.get("prm.project.admin.profile.name.prefix.label") %>
        &nbsp;&nbsp;</td>
        <td nowrap>
            <input type="text" name="ecom_ShipTo_Postal_Name_Prefix" size="8" maxlength="40" onchange="setUpdated('true');" value='<c:out value="${registration.namePrefix}"/>'>
            <span class="fieldNonRequired">&nbsp;  
            	<%=PropertyProvider.get("prm.project.admin.profile.mr.ms.label") %>
            </span>
            <%=flagDirectoryProvided(registration, IPersonAttributes.PREFIX_ATTRIBUTE)%>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left">
        <td nowrap>&nbsp;</td>
        <td nowrap class="fieldRequired" align="right">
        	<%=PropertyProvider.get("prm.project.admin.profile.first.name.label") %>
        &nbsp;&nbsp;</td>
        <td nowrap>
            <input type="text" name="ecom_ShipTo_Postal_Name_First" size="20" maxlength="80" onchange="setUpdated('true');" value='<c:out value="${registration.firstName}"/>'>
            <%=flagDirectoryProvided(registration, IPersonAttributes.FIRSTNAME_ATTRIBUTE)%>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left">
    <td nowrap>&nbsp;</td>
        <td nowrap class="fieldNonRequired" align="right">
        	<%=PropertyProvider.get("prm.project.admin.profile.middle.name.label") %>
        &nbsp;&nbsp;</td>

        <td nowrap>
            <input type="text" name="ecom_ShipTo_Postal_Name_Middle" size="20" maxlength="80" onchange="setUpdated('true');" value='<c:out value="${registration.middleName}" />'>
            <%=flagDirectoryProvided(registration, IPersonAttributes.MIDDLENAME_ATTRIBUTE)%>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left">
    <td nowrap>&nbsp;</td>
        <td nowrap class="fieldRequired" align="right">
        	<%=PropertyProvider.get("prm.project.admin.profile.last.name.label") %>
        &nbsp;&nbsp;</td>

        <td nowrap>
            <input type="text" name="ecom_ShipTo_Postal_Name_Last" size="20" maxlength="80" onchange="setUpdated('true');" value='<c:out value="${registration.lastName}" />'>
            <%=flagDirectoryProvided(registration, IPersonAttributes.LASTNAME_ATTRIBUTE)%>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left">
    <td nowrap>&nbsp;</td>
        <td nowrap class="fieldNonRequired" align="right">
        	<%=PropertyProvider.get("prm.project.admin.profile.name.suffix.label") %>
        &nbsp;&nbsp;</td>

        <td nowrap>
            <input type="text" name="ecom_ShipTo_Postal_Name_Suffix" size="8" maxlength="40" onchange="setUpdated('true');" value='<c:out value="${registration.nameSuffix}" />'>
            <span class="fieldNonRequired">&nbsp;
            <%=PropertyProvider.get("prm.project.admin.profile.jr.mba.label") %></span>
            <%=flagDirectoryProvided(registration, IPersonAttributes.SUFFIX_ATTRIBUTE)%>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left">
        <td nowrap>&nbsp;</td>
        <td nowrap class="fieldRequired" align="right">
        	<%=PropertyProvider.get("prm.project.admin.profile.display.name.label") %>
        &nbsp;&nbsp;</td>

        <td nowrap>
            <input type="text" name="displayName" size="20" maxlength="80" onchange="setUpdated('true');" value='<c:out value="${registration.fullName}" />'>
            <span class="fieldNonRequired">&nbsp;
				<%=PropertyProvider.get("prm.project.admin.profile.how.user.appear.to.other.label") %>
			</span>
            <%=flagDirectoryProvided(registration, IPersonAttributes.DISPLAYNAME_ATTRIBUTE)%>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left">
        <td nowrap>&nbsp;</td>
        <td nowrap class="fieldRequired" align="right">
        	<%=PropertyProvider.get("prm.project.admin.profile.email.address.label") %>
        &nbsp;&nbsp;</td>
        <td nowrap>
            <input type="text" name="ecom_ShipTo_Online_Email" size="40" maxlength="250" onchange="setUpdated('true');" value='<c:out value="${registration.email}" />'>
            <%=flagDirectoryProvided(registration, IPersonAttributes.EMAIL_ATTRIBUTE)%>
        </td>            
        <td nowrap>&nbsp;</td>
    </tr>
    
	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldNonRequired" align="right">
			<%=PropertyProvider.get("prm.project.admin.profile.alternative.email1.label") %>
		&nbsp;&nbsp;</td>
		<td nowrap>
			<input type="text" name="alternateEmail1" size="40" maxlength="240" onchange="setUpdated('true');" value='<c:out value="${registration.alternateEmail1}" />'>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>


	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldNonRequired" align="right">
			<%=PropertyProvider.get("prm.project.admin.profile.alternative.email2.label") %>
		&nbsp;&nbsp;</td>

		<td nowrap>
			<input type="text" name="alternateEmail2" size="40" maxlength="240" onchange="setUpdated('true');" value='<c:out value="${registration.alternateEmail2}" />'>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>

	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldNonRequired" align="right">
			<%=PropertyProvider.get("prm.project.admin.profile.skype.label") %>
			&nbsp;&nbsp;</td>
		<td nowrap>
			<input type="text" name="skype" size="40" maxlength="240" onchange="setUpdated('true');" value="<c:out value="${registration.skype}"/>">
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	
	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldNonRequired" align="right">
			<%=PropertyProvider.get("prm.project.admin.profile.skills.bio.label") %>
		&nbsp;&nbsp;</td>

		<td nowrap>
			<textarea name="skillsBio" cols="80" rows="5" maxlength="300" onchange="setUpdated('true');" ><c:out value="${registration.skillsBio}"/> </textarea>
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