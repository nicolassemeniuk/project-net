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

<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Profile Name"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.resource.IPersonAttributes"
%>
<%-- fix for bug-2188 (IE caching issues) --%>
<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", -1); //prevents caching at the proxy server
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<%
    // No security validation necessary since a user can only access their own Profile
	registration.setID(user.getID());
	registration.setEmail(user.getEmail());
	// Load the registration information and the directory entry
    registration.load();
    // Update the registration bean from the directory entry
    registration.populateFromDirectoryEntry();
	
	// Set the user record is being updated
	registration.setUpdating(true);

	String errorMessage = (String)request.getAttribute("errorMsg");
    String errorObject = (String)request.getAttribute("errorObj");
    
    String imagePath;
    if(user.getImageId() != 0){
    	imagePath = SessionManager.getJSPRootURL()+"/servlet/photo?id="+ user.getID() +"&size=medium&module="+Module.DIRECTORY;
    } else {
    	imagePath = SessionManager.getJSPRootURL()+"/images/NoPicture.gif";
    }
%>

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<style type="text/css">
.ext-el-mask{
	z-index:10;
}
	
html, body {
	font: normal 12px verdana;
	margin: 0;
	padding: 0;
	border: 0 none;
	overflow: hidden;
	height: 100%;
}
</style>
<template:getSpaceCSS />
<%-- Import Javascript --%>

<template:import type="javascript" src="/src/checkEmail.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/upload.js" />

<script language="javascript">
    var updatedProfile = false;

	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  
	var moduleId = <%=Module.PERSONAL_SPACE%>;
	var userId = <%=user.getID()%>;
	var confirmRemoveImageMessege = '<%=PropertyProvider.get("prm.personal.profile.confirmremoveimage.message") %>';
function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    theForm = self.document.forms[0];
    isLoaded = true;
}

// checking if invalid image file is uploaded
function checkForInvalidImageUpload(){
	if(!validImage){
		extAlert(errotAlertTitle, selectedInvalidImageMessage, Ext.MessageBox.ERROR);			
	}
}

function stripHTML( formId, errorMsg){
	var oldValue = formId.value;
	var auxValue = "";
	var re= /<\S[^><]*>/g
		for (i=0; i< formId.value.length; i++){
			auxValue = formId.value.replace(re, "")
		} 
	if (oldValue != auxValue) {
		extAlert(errorTitle, errorMsg , Ext.MessageBox.ERROR);
		return false;
	} else {
		return true;
	}
}

function trim( value ) {
	return value.replace(/^\s+|\s+$/g,"");
}

function cancel()	{ 
<%--Avinash: bfd 3214 and 3234  Cancel button in the Personal Profile page navigates to Personal Work Space--%>
var referer= "<%=(String)session.getAttribute("referer")%>";
if(referer=="" || referer=="null")
	referer = "<%= request.getParameter("referer")== null ?  SessionManager.getJSPRootURL() + "/personal/Main.jsp?module="+ Module.PERSONAL_SPACE  : SessionManager.getJSPRootURL() + "/" + request.getParameter("referer")+ "?module=" + Module.PROJECT_SPACE %>";
else
	referer = "<%= (String)session.getAttribute("referer")== null ?  SessionManager.getJSPRootURL() + "/personal/Main.jsp?module="+ Module.PERSONAL_SPACE  : SessionManager.getJSPRootURL() + "/" + (String)session.getAttribute("referer")+ "?module=" + Module.PROJECT_SPACE %>";
	self.document.location = referer ; 
}
function reset()	{ theForm.reset(); }
function submit() {
	//BFD-2062 Avinash Bhamare :  For Checking the How many Alternate Email Address are Modified Start
	if(true == chkModifiedAltEMails()){        
		applyChanges();
	}
}

// For validating special character from skype name
function isValidSkypeName(num) {
	if((num != null) && (num != "")) {
		var avoidSpecialChar = "~!@#$%^&*()',`/|;+=[]<>{}?\"&";
		for(var index = 0; index < num.length; index++) {
			if(avoidSpecialChar.indexOf(num.charAt(index)) != -1) {
				return false;
			}
		}
	}
	return true;
}

// For validating skype name
function validateSkype(){
	if(document.getElementById('skype').value.trim() != ''){
		if(isValidSkypeName(document.getElementById('skype').value.trim())){
			if(parseInt(document.getElementById('skype').value.trim().length) < 6
				 || parseInt(document.getElementById('skype').value.trim().length) > 32){
				extAlert(errorTitle, '<%=PropertyProvider.get("prm.personal.profile.skype.charrange.error.message")%>', Ext.MessageBox.ERROR); 
				return false;
			}else {
				return true;
			}
		}else {
			extAlert(errorTitle, '<%=PropertyProvider.get("prm.personal.profile.skype.invalid.error.message")%>', Ext.MessageBox.ERROR); 
		}
	}else {
		extAlert(errorTitle, '<%=PropertyProvider.get("prm.personal.profile.skype.spaces.error.message")%>', Ext.MessageBox.ERROR); 
		return false;
	}
}

function validateForm(frm) {
	var mailArray = new Array(); // For the main e-mail.
	var i = 0;
	if (!checkTextbox(frm.ecom_ShipTo_Postal_Name_First,'<%=PropertyProvider.get("prm.personal.profile.name.firstnamerequired.message")%>')) return false;
	if (!checkTextbox(frm.ecom_ShipTo_Postal_Name_Last,'<%=PropertyProvider.get("prm.personal.profile.name.lastnamerequired.message")%>')) return false;
	if (!checkTextbox(frm.displayName,'<%=PropertyProvider.get("prm.personal.profile.name.displaynamerequired.message")%>')) return false;
	frm.ecom_ShipTo_Online_Email.value = trim(frm.ecom_ShipTo_Online_Email.value);
	if (!checkEmail(frm.ecom_ShipTo_Online_Email,'<%=PropertyProvider.get("prm.personal.profile.name.validemail.message")%>')) return false;
	mailArray[i++] = frm.ecom_ShipTo_Online_Email;
	frm.alternateEmail1.value = trim(frm.alternateEmail1.value);
	if((frm.alternateEmail1.value).length > 0){
		if (!checkEmailAllowNull(frm.alternateEmail1,'<%=PropertyProvider.get("prm.personal.profile.name.validealt1email.message")%>')) return false;
		mailArray[i++] = frm.alternateEmail1;
		if(checkArrayDuplicates(mailArray, '<%=PropertyProvider.get("prm.personal.profile.name.validedupalt1email.message")%>')){
			return false;
		}
	}
	frm.alternateEmail2.value = trim(frm.alternateEmail2.value);
	if((frm.alternateEmail2.value).length > 0){
		if (!checkEmailAllowNull(frm.alternateEmail2,'<%=PropertyProvider.get("prm.personal.profile.name.validealt2email.message")%>')) return false;
		mailArray[i++] = frm.alternateEmail2;
		if(checkArrayDuplicates(mailArray, '<%=PropertyProvider.get("prm.personal.profile.name.validedupalt2email.message")%>')){
			return false;
		}
	}
	// checking for Skills/Bio field value length which should contain <= 300 characters
	frm.skillsBio.value = trim(frm.skillsBio.value);
	if(frm.skillsBio.value != null && frm.skillsBio.value != '' && frm.skillsBio.value.length > 300) {
		extAlert(errorTitle, '<%=PropertyProvider.get("prm.personal.profile.name.validskillsbio.message")%>', Ext.MessageBox.ERROR);
		return false;
	}
	if(checkTextboxwithoutHTMLTags()) {
		return true;
	} else {
		return false;
	}
}

function checkTextboxwithoutHTMLTags() {
	if (!stripHTML(document.registration.ecom_ShipTo_Postal_Name_First,'<%=PropertyProvider.get("prm.personal.profile.name.firstname.cannothtmltag.message")%>')) return false;
	if (!stripHTML(document.registration.ecom_ShipTo_Postal_Name_Last,'<%=PropertyProvider.get("prm.personal.profile.name.lastname.cannothtmltag.message")%>')) return false;
	if (!stripHTML(document.registration.displayName,'<%=PropertyProvider.get("prm.personal.profile.name.displayname.cannothtmltag.message")%>')) return false;
	if (!stripHTML(document.registration.ecom_ShipTo_Online_Email,'<%=PropertyProvider.get("prm.personal.profile.name.validemail.cannothtmltag.message")%>')) return false;
	if (!stripHTML(document.registration.skillsBio,'<%=PropertyProvider.get("prm.personal.profile.name.validskillsbio.cannothtmltag.message")%>')) return false;
	return true;
}

function applyChanges() {
   if ((document.registration.nextPage.value == null) || (document.registration.nextPage.value == ""))
      document.registration.nextPage.value = "ProfileName.jsp";
   if (updatedProfile) {
  	 Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.personal.profile.name.update.message")%>', function(btn) { 
			if(btn == 'yes') { 
				if (validateForm(document.registration)){
					if(document.getElementById('skype').value != ''){
						if(validateSkype())
							document.getElementById('skype').value = document.getElementById('skype').value.trim();
							document.registration.submit();
					}else if(document.getElementById('skype').value.trim() == ''){
						document.registration.submit();
					}
				}
			} else {
				 document.location = document.registration.nextPage.value+"?module=<%= net.project.base.Module.PERSONAL_SPACE %>";
			}
		});
   }else{
      document.location = document.registration.nextPage.value+"?module=<%= net.project.base.Module.PERSONAL_SPACE %>";
   }
}

function tabClick(nextPageVal){
   document.registration.nextPage.value = nextPageVal;
   applyChanges();
}


function setUpdated(updated){
   updatedProfile = updated;
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=profile_personal&section=name";
	openwin_help(helplocation);
}


function highlightError(){
<%
if (errorObject != null){
    out.print("document.registration." + errorObject + ".focus();");
%>
<% } else { %>
    document.registration.ecom_ShipTo_Postal_Name_Prefix.focus();
<% } %>
    return true;
}

// BFD-2062 Avinash Bhamare :  For Checking the How many Alternate Email Address are Modified Start
function chkModifiedAltEMails()
{
	var cngCount = 0;
	var message = "";
	var result = false;
	
	var altEmail1 = document.registration.alternateEmail1.value;
	var altEmail2 = document.registration.alternateEmail2.value;
	//var altEmail3 = document.registration.alternateEmail3.value;
	var curraltEmail1 = "<c:out value="${registration.alternateEmail1}"/>";
	var curraltEmail2 = "<c:out value="${registration.alternateEmail2}"/>";
	//var curraltEmail3 = "<c:out value="${registration.alternateEmail3}"/>";
	
	if (altEmail1 !='' && !checkEmail(document.registration.alternateEmail1,'<%=PropertyProvider.get("prm.personal.profile.name.validemail.message")%>')) return false;
	if (altEmail2 !='' && !checkEmail(document.registration.alternateEmail2,'<%=PropertyProvider.get("prm.personal.profile.name.validemail.message")%>')) return false;
	
	if(altEmail1 != curraltEmail1){
		cngCount = cngCount + 1;
	}
	if(altEmail2 != curraltEmail2){
		cngCount = cngCount + 1;
	}
	/*if(altEmail3 != curraltEmail3){
		cngCount = cngCount + 1;
	}*/

	if(cngCount == 0) result = true;
	if(cngCount == 1){
		message = "<%=PropertyProvider.get("prm.personal.profile.emailchangewarn")%>";
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', message, function(btn) {
			result = (btn == 'yes');

			if (result) {
		        var msg = "<%=PropertyProvider.get("prm.personal.profile.edit.changeemailconfirmation.message")%>";
		        Ext.MessageBox.confirm('Warning', msg, function(btn) {      // TODO - add warning to system properties
		            result = (btn == 'yes');
		        });
			}
        });
		
		return result;
	}else{
		if(cngCount > 1){
			message = "<%=PropertyProvider.get("prm.personal.profile.emailchangenotallowed")%>";
			extAlert(errorTitle, message, Ext.MessageBox.ERROR); 
			result = false ;
		}
	}
	return result;
}
// BFD 2062  For Checking the How many Alternate Email Address are Modified  End
</script>
<%-- bfd 3234: Cancel button in the Personal Profile page navigates to Personal Work Space --%>
<%if(request.getParameter("referer")!= null)session.setAttribute("referer",request.getParameter("referer")); %>
</head>

<body class="main" id="bodyWithFixedAreasSupport" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0 onLoad="setup();highlightError();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id="left-navbar">
	<div id="leftheading-person"  style="position: relative; margin-top: 15px;"><%=PropertyProvider.get("prm.personal.profile.module.history")%></div>
	<div class="profile-name" style="margin-top: 10px;"><c:out value="${registration.displayName}"/></div>
	<div class="profile-photo"> <img width="110px" src='<%=imagePath%>' id="personImage" alt='<%=PropertyProvider.get("prm.personal.profile.mypicture.alt")%>' /> </div>
	<div style="clear: both"></div>
	<div class='spacer-for-toolbox'></div>
	<div class='toolbox-heading'><display:get name="prm.global.toolbox.heading" /></div>
	<div id="toolbox-item" class="toolbox-item">
		<span id="blog-ItEnabled">
			<a onmouseover=" document.imgblogit.src = '<%= SessionManager.getJSPRootURL() %><%=PropertyProvider.get("all.global.toolbar.standard.blogit.image.over")%>'" onmouseout=" document.imgblogit.src = '<%= SessionManager.getJSPRootURL() %><%=PropertyProvider.get("all.global.toolbar.standard.blogit.image.on")%>'" href="javascript:blogit();">
				<img hspace="0" border="0" name="imgblogit" src="<%= SessionManager.getJSPRootURL() %><%=PropertyProvider.get("all.global.toolbar.standard.blogit.image.on")%>" title="Blog-it" alt="Blog-it"/>
				&nbsp;<display:get name="all.global.toolbar.standard.blogit" />
			</a>
		</span><br />
		<span>
			<a onmouseover="document.imgupload.src = '<%= SessionManager.getJSPRootURL() %><%=PropertyProvider.get("all.global.toolbar.standard.uploadpicture.image.over")%>'" onmouseout=" document.imgupload.src = '<%= SessionManager.getJSPRootURL() %><%= PropertyProvider.get("all.global.toolbar.standard.uploadpicture.image.on")%>'" href="javascript:showUploadPopup('<%= SessionManager.getJSPRootURL() %>/personal/UploadDocument',625,'null','setupProfilePage');" >
				<img hspace="0" border="0" name="imgupload" src="<%= SessionManager.getJSPRootURL() %><%= PropertyProvider.get("all.global.toolbar.standard.uploadpicture.image.on")%>" title="Edit Profile Info"/>
				<display:get name="prm.personal.profile.uploadpicture.link" />
			</a>
		</span><br />
		<span id="removeLink">
			<% if(user.getImageId() != 0){%>
				<a onmouseover="document.imgremove.src = '<%= SessionManager.getJSPRootURL() %><%=PropertyProvider.get("all.global.toolbar.standard.remove.image.over")%>'" onmouseout="document.imgremove.src = '<%= SessionManager.getJSPRootURL() %><%=PropertyProvider.get("all.global.toolbar.standard.remove.image.on")%>'" href="javascript:confirmRemoveImage();">
					<img hspace="0" border="0" name="imgremove" src="<%= SessionManager.getJSPRootURL() %><%=PropertyProvider.get("all.global.toolbar.standard.remove.image.on")%>" title="Remove Picture"/>
					<display:get name="prm.personal.profile.removepicture.link" />
				</a>
			<%}%>
		</span>
	</div>		
</div>
<div id='content'>
	<history:history>
		<history:module display='<%=PropertyProvider.get("prm.personal.profile.module.history")%>'
				jspPage='<%=SessionManager.getJSPRootURL() + "/personal/ProfileName.jsp"%>'
				queryString='<%="module=" + net.project.base.Module.PERSONAL_SPACE%>' />
	</history:history>
<br />
<%-- Tab Bar --%>
<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.name.tab")%>' href="javascript:tabClick('ProfileName.jsp');" selected="true" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.address.tab")%>' href="javascript:tabClick('ProfileAddress.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.login.tab")%>' href="javascript:tabClick('ProfileLoginController.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.license.tab")%>' href="javascript:tabClick('ProfileLicense.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.domain.tab")%>' href="javascript:tabClick('ProfileDomainMigration.jsp');" />
</tab:tabStrip>

<form name="registration" action="ProfileNameProcessing.jsp" method="post">
    <input type="hidden" name="module" value="<%= net.project.base.Module.PERSONAL_SPACE %>">
    <input type="hidden" name="nextPage" value="">
	<input type="hidden" name="theAction" value="apply">

<div align="center">
	<table width="600" cellpadding=0 cellspacing=0 border=0>
    <tr>
		<td colspan="4" class="tableHeader">
			<%=PropertyProvider.get("prm.global.display.requiredfield")%>
		</td>
	</tr>

<%
if (errorMessage != null)
{
%>
	<tr align="left">
		<td colspan="4" align="left">
			<span class="errorText"><%= errorMessage %></span>
		</td>
	</tr>
<%
}
%>
	<tr class="actionBar">
    <td colspan="4">
        <table border="0" width="100%" cellpadding="0" cellspacing="0">
        <tr>
        <td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><%=PropertyProvider.get("prm.personal.profile.name.channel.info.titile")%></td>
		<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
        </tr>
        </table>
        </td>
	</tr>

	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldNonRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.name.prefix.label")%>&nbsp;&nbsp;</td>
		<td nowrap>
			<input type="text" name="ecom_ShipTo_Postal_Name_Prefix" size="8" maxlength="40" onchange="setUpdated('true');" value="<c:out value="${registration.namePrefix}"/>">
		    <span class="fieldNonRequired">&nbsp;<%=PropertyProvider.get("prm.personal.profile.name.prefix.text")%></span>
            <%=flagDirectoryProvided(registration, IPersonAttributes.PREFIX_ATTRIBUTE)%>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.name.firstname.label")%>&nbsp;&nbsp;</td>
		<td nowrap>
			<input type="text" name="ecom_ShipTo_Postal_Name_First" size="20" maxlength="80" onchange="setUpdated('true');" value="<c:out value="${registration.firstName}"/>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.FIRSTNAME_ATTRIBUTE)%>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	<tr align="left">
	<td nowrap>&nbsp;</td>
		<td nowrap class="fieldNonRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.name.middlename.label")%>&nbsp;&nbsp;</td>

		<td nowrap>
			<input type="text" name="ecom_ShipTo_Postal_Name_Middle" size="20" maxlength="80" onchange="setUpdated('true');" value="<c:out value="${registration.middleName}"/>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.MIDDLENAME_ATTRIBUTE)%>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	<tr align="left">
	<td nowrap>&nbsp;</td>
		<td nowrap class="fieldRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.name.lastname.label")%>&nbsp;&nbsp;</td>

		<td nowrap>
			<input type="text" name="ecom_ShipTo_Postal_Name_Last" size="20" maxlength="80" onchange="setUpdated('true');" value="<c:out value="${registration.lastName}"/>">
            <%=flagDirectoryProvided(registration, IPersonAttributes.LASTNAME_ATTRIBUTE)%>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	<tr align="left">
	<td nowrap>&nbsp;</td>
		<td nowrap class="fieldNonRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.name.suffix.label")%>&nbsp;&nbsp;</td>

		<td nowrap>
			<input type="text" name="ecom_ShipTo_Postal_Name_Suffix" size="8" maxlength="40" onchange="setUpdated('true');" value="<c:out value="${registration.nameSuffix}"/>">
		    <span class="fieldNonRequired">&nbsp;<%=PropertyProvider.get("prm.personal.profile.name.suffix.text")%></span>
            <%=flagDirectoryProvided(registration, IPersonAttributes.SUFFIX_ATTRIBUTE)%>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.name.displayname.label")%>&nbsp;&nbsp;</td>

		<td nowrap>
			<input type="text" name="displayName" size="20" maxlength="80" onchange="setUpdated('true');" value="<c:out value="${registration.fullName}"/>">
		    <span class="fieldNonRequired">&nbsp;<%=PropertyProvider.get("prm.personal.profile.name.displayname.text")%></span>
            <%=flagDirectoryProvided(registration, IPersonAttributes.DISPLAYNAME_ATTRIBUTE)%>
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.name.primaryemail.label")%>&nbsp;&nbsp;</td>
		<td nowrap>
			<input type="text" name="ecom_ShipTo_Online_Email" size="40" maxlength="250" onchange="setUpdated('true');" value="<c:out value="${registration.email}"/>">
		    <span class="fieldNonRequired">&nbsp;<%=PropertyProvider.get("prm.personal.profile.name.primaryemail.text")%></span>
            <%=flagDirectoryProvided(registration, IPersonAttributes.EMAIL_ATTRIBUTE)%>
		<td nowrap>&nbsp;</td>
	</tr>

	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldNonRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.name.alt1email.label")%>&nbsp;&nbsp;</td>

		<td nowrap>
			<input type="text" name="alternateEmail1" size="40" maxlength="240" onchange="setUpdated('true');" value="<c:out value="${registration.alternateEmail1}"/>">
		</td>
		<td nowrap>&nbsp;</td>
	</tr>

	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldNonRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.name.alt2email.label")%>&nbsp;&nbsp;</td>

		<td nowrap>
			<input type="text" name="alternateEmail2" size="40" maxlength="240" onchange="setUpdated('true');" value="<c:out value="${registration.alternateEmail2}"/>">
		</td>
		<td nowrap>&nbsp;</td>
	</tr>


	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldNonRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.name.skype.label")%>&nbsp;&nbsp;</td>

		<td nowrap>
			<input type="text" id="skype" name="skype" size="40" maxlength="32" onchange="setUpdated('true');" value="<c:out value="${registration.skype}"/>">
		</td>
		<td nowrap>&nbsp;</td>
	</tr>
	
	
	<tr align="left">
		<td nowrap>&nbsp;</td>
		<td nowrap class="fieldNonRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.name.skillsbio.label")%>&nbsp;&nbsp;</td>

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
