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
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Login Profile"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager,
			net.project.base.directory.DirectoryProviderType"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<jsp:useBean id="nativeDirectoryEntry" class="net.project.base.directory.nativedir.NativeProfileEditor" scope="request" />
<%
	if (registration.getID() == null && !registration.isLoaded()) {
		registration.setID(user.getID());
		registration.load();
	}
    
    String errorMessage = (String) request.getAttribute("errorMessage");
    boolean isError = (errorMessage != null && errorMessage.trim().length() > 0);
    
    String successMessage = (String) request.getAttribute("successMessage");    
    boolean isSuccessful = (successMessage != null && successMessage.trim().length() > 0);
    
%>

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
<template:import type="javascript" src="/src/checkAlphaNumeric.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
    var updatedProfile;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
	theForm = self.document.forms["registration"];
    focusFirstField(theForm);
	isLoaded = true;
}

function cancel() { 
    document.registration.nextPage.value = "";
    theAction("cancel");
    theForm.submit();
}

function reset()	{ 
    document.registration.reset(); 
}

function submit() {
    document.registration.nextPage.value = "/personal/ProfileLoginController.jsp";
    applyChanges();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=profile_personal&section=native_login";
	openwin_help(helplocation);
}
function validateForm(frm) {
    if (!checkTextbox(frm.currentLogin, '<%=PropertyProvider.get("prm.personal.profile.login.enterpassword.message")%>')) return false;
    if (!checkTextbox(frm.currentClearTextPassword, '<%=PropertyProvider.get("prm.personal.profile.login.enterpassword.message")%>')) return false;

    <%if(!SessionManager.getUser().isApplicationAdministrator()) {%>
	    if (frm.isChangeLoginCheckbox.checked){
		    if (!checkLength(frm.newLogin, <%=PropertyProvider.get("prm.global.profile.username.minsize")%>, '<%=PropertyProvider.get("prm.personal.profile.login.loginrange.message")%>')) return false;
	        if (!checkAlphaNumericLowercase(frm.newLogin, '<%=PropertyProvider.get("prm.personal.profile.login.loginchars.message")%>')) return false;
	    }
    <%}%>
    if (frm.isChangePasswordCheckbox.checked){
    	<% if(!SessionManager.getUser().getUserDomain().getDirectoryProviderTypeID().equals(DirectoryProviderType.LDAP_ID)){ %>
			if (!checkMaxMinLength(frm.newPassword, 20, 6, '<%=PropertyProvider.get("prm.personal.profile.login.passwordrange.message")%>')) return false;
		<% } %>
		if (!checkPasswords(frm.newPassword, frm.newPasswordRetype, '<%=PropertyProvider.get("prm.personal.profile.login.passwordmatch.message")%>')) return false;
	}
    if (frm.isChangeHintsCheckbox.checked){
        if (!checkTextbox(frm.newClearTextHintPhrase, '<%=PropertyProvider.get("prm.personal.profile.login.enterjogquestion.message")%>')) return false;
        if (!checkTextbox(frm.newClearTextHintAnswer, '<%=PropertyProvider.get("prm.personal.profile.login.enterjoganswer.message")%>')) return false;
    }
    return true;
}


function applyChanges() {
    if (updatedProfile) {
    	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>','<%=PropertyProvider.get("prm.personal.profile.login.update.message")%>', function(btn) { 
			if(btn == 'yes'){ 
				if (validateForm(document.registration)) {
           			theAction("submit");
             		theForm.submit();
        	 	}
			}else{
		 		theAction("cancel");
        		theForm.submit();
			}
		});
    } else {
        theAction("cancel");
        theForm.submit();
    }
}

function setUpdated(updated){
   updatedProfile = updated;
}

function tabClick(nextPage) {
    document.registration.nextPage.value = nextPage;
	applyChanges();
}

function toggle(cbField, field) {
    // Set field to "true" or "false" depending on value of cbField
    if (cbField && field) {
        field.value = (cbField.checked ? 'true' : 'false');
        setUpdated('true');
    }
}

</script>


</head>
<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.profile.module.history" showSpaceDetails="false">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page level="1" display='<%=PropertyProvider.get("prm.personal.profile.module.history")%>' 
					jspPage='<%=SessionManager.getJSPRootURL() + "/personal/ProfileLoginController.jsp"%>'
					queryString='<%="module=" + net.project.base.Module.PERSONAL_SPACE%>' />
			 <history:page displayToken="prm.personal.profile.login.tab" />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br />
<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.name.tab")%>' href="javascript:tabClick('/personal/ProfileName.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.address.tab")%>' href="javascript:tabClick('/personal/ProfileAddress.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.login.tab")%>' href="javascript:tabClick('/personal/ProfileLoginController.jsp');" selected="true" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.license.tab")%>' href="javascript:tabClick('/personal/ProfileLicense.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.personal.profile.domain.tab")%>' href="javascript:tabClick('/personal/ProfileDomainMigration.jsp');" />
</tab:tabStrip>
<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/personal/NativeProfileLoginController.jsp"%>" method="post">
    <input type="hidden" name="module" value="<%= net.project.base.Module.PERSONAL_SPACE %>">
    <input type="hidden" name="nextPage" value="">
	<input type="hidden" name="theAction" value="">
	<input type="hidden" name="fromPage" value="nativeProfileLogin">

<div align="center">
<table width="90%" cellpadding="0" cellspacing="0" border="0">
    <tr>
        <td colspan="5" class="tableHeader"><display:get name="prm.global.display.requiredfield" /></td>
    </tr>
    <tr><td colspan="5" class="tableHeader">&nbsp;</td></tr>
    <tr>
        <td width=1% class="ActionBar"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
        <td colspan="3" valign="middle" class="ActionBar">
            <%=PropertyProvider.get("prm.personal.profile.login.channel.nameandpassword.title")%>
        </td>
        <td width=1% align=right class="ActionBar"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
    </tr>
    <tr align="left">
        <td>&nbsp;</td>
        <td colspan="3" class="tableHeader">
			<%=PropertyProvider.get("prm.personal.profile.login.youmaychange.message")%>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5" class="tableHeader">&nbsp;</td></tr>
<%
    if (isError) {
%>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" class="errorMessage" align="left">
            <%=errorMessage%>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>

<%  } %>

<%  if (isSuccessful) { %>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" class="successMessage" align="left">
            <%=successMessage%>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>
<%  }  %>
    <tr align="left">
        <td nowrap>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td nowrap align="right"><%=PropertyProvider.get("prm.personal.profile.login.currentlogin.label")%>&nbsp;&nbsp;</td>
        <td nowrap>
            <input type="text" size="20" maxlength="80" name="currentLogin" value="<jsp:getProperty name="nativeDirectoryEntry" property="currentLogin" />">
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left">
        <td nowrap>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td nowrap class="fieldRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.login.currentpassword.label")%>&nbsp;&nbsp;</td>
        <td nowrap>
          <input type="password" name="currentClearTextPassword" size="20" onchange="setUpdated('true');" value="">
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>

    <%-- New Login Name --%>
    <%if(!SessionManager.getUser().isApplicationAdministrator()) {%>
	    <tr>
	        <td>&nbsp;</td>
	        <td colspan="3" class="tableContent">
	            <input type="hidden" name="changeLogin" value="<jsp:getProperty name="nativeDirectoryEntry" property="changeLogin"/>">
	            <input type="checkbox" name="isChangeLoginCheckbox" onClick="toggle(this, this.form.changeLogin);" <%=nativeDirectoryEntry.isChangeLogin() ? "selected" : ""%>>&nbsp;
	            <%=PropertyProvider.get("prm.personal.profile.login.changelogin.label")%>
	        </td>
	        <td>&nbsp;</td>
	    </tr>
	    <tr>
        	<td width="10%">&nbsp;</td>
	        <td width="10%">&nbsp;</td>
	        <td class="fieldNonRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.login.newlogin.label")%>&nbsp;&nbsp;</td>
	        <td class="fieldNonRequired">
	            <input type="text" name="newLogin" size="20" maxlength="80" onchange="setUpdated('true');this.form.isChangeLoginCheckbox.checked='true';toggle(this.form.isChangeLoginCheckbox, this.form.changeLogin);" value='<c:out value="${nativeDirectoryEntry.newLogin}" />'>
	        </td>
	        <td>&nbsp;</td>
	    </tr>
	    <tr><td colspan="5">&nbsp;</td></tr>
	<%}%>
    <%-- New Password --%>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" class="tableContent">
            <input type="hidden" name="changePassword" value="<jsp:getProperty name="nativeDirectoryEntry" property="changePassword"/>">
            <input type="checkbox" name="isChangePasswordCheckbox" onClick="toggle(this, this.form.changePassword);" <%=nativeDirectoryEntry.isChangePassword() ? "selected" : ""%>>&nbsp;
            <%=PropertyProvider.get("prm.personal.profile.login.changepassword.label")%>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td class="fieldNonRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.login.newpassword.label")%>&nbsp;&nbsp;</td>
        <td class="fieldNonRequired">
            <input type="password" name="newPassword" onchange="setUpdated('true');this.form.isChangePasswordCheckbox.checked='true';toggle(this.form.isChangePasswordCheckbox, this.form.changePassword);" size="20" value="">
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td class="fieldNonRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.login.retypenewpassword.label")%>&nbsp;&nbsp;</td>
        <td class="fieldNonRequired">
            <input type="password" name="newPasswordRetype" onchange="setUpdated('true');this.form.isChangePasswordCheckbox.checked='true';toggle(this.form.isChangePasswordCheckbox, this.form.changePassword);" size="20" value="">
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>

    <%-- Jog Question and Answer --%>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" class="tableContent">
            <input type="hidden" name="changeHints" value="<jsp:getProperty name="nativeDirectoryEntry" property="changeHints"/>">
            <input type="checkbox" name="isChangeHintsCheckbox" onClick="toggle(this, this.form.changeHints);" <%=nativeDirectoryEntry.isChangeHints() ? "selected" : ""%>>&nbsp;
            <%=PropertyProvider.get("prm.personal.profile.login.changejog.label")%>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td colspan="2" class="tableHeader">
            <%=PropertyProvider.get("prm.personal.profile.login.jogquestion.message")%>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td class="fieldNonRequired" align="right"><%=PropertyProvider.get("prm.personal.profile.login.jogquestion.label")%>&nbsp;&nbsp;</td>
        <td class="fieldNonRequired">
            <input type="text" name="newClearTextHintPhrase" size="40" maxlength="80" onchange="setUpdated('true');this.form.isChangeHintsCheckbox.checked='true';toggle(this.form.isChangeHintsCheckbox, this.form.changeHints);" value='<c:out value="${nativeDirectoryEntry.newClearTextHintPhrase}" />'>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td class="fieldNonRequired" align="right">
            <%=PropertyProvider.get("prm.personal.profile.login.joganswer.label")%>&nbsp;&nbsp;
        </td>
        <td class="fieldNonRequired">
            <input type="text" name="newClearTextHintAnswer" size="20" maxlength="80" onchange="setUpdated('true');this.form.isChangeHintsCheckbox.checked='true';toggle(this.form.isChangeHintsCheckbox, this.form.changeHints);" value='<c:out value="${nativeDirectoryEntry.newClearTextHintAnswer}" />'>
        </td>
        <td>&nbsp;</td>
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
