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
|   $Revision: 18995 $
|       $Date: 2009-03-05 08:36:26 -0200 (jue, 05 mar 2009) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Login Profile"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
    		net.project.base.property.PropertyProvider,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="nativeDirectoryEntry" class="net.project.base.directory.nativedir.NativeProfileEditor" scope="request" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
    // Page should only be forwarded to from ProfileLoginController or NativeProfileLoginController
    // Assumes current registration bean is loaded with selected user
    String userID = registration.getID();
    String errorMessage = (String) request.getAttribute("errorMessage");
    boolean isError = (errorMessage != null && errorMessage.trim().length() > 0);
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
    document.registration.nextPage.value = "/admin/profile/ProfileLoginController.jsp";
    applyChanges();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=profile_personal&section=native_login";
	openwin_help(helplocation);
}
function validateForm(frm) {
	 
    if (frm.isChangeLoginCheckbox.checked){
    	var message1 = '<%=PropertyProvider.get("prm.project.admin.profile.length.login.js")%>';
   	 	var message2 = '<%=PropertyProvider.get("prm.project.admin.profile.lenght.login.2.js")%>';
   	 	
	    if (!checkLength(frm.newLogin,<%=PropertyProvider.get("prm.global.profile.username.minsize")%>, message1)) return false;
        if (!checkAlphaNumericLowercase(frm.newLogin, message2)) return false;
    }
    if (frm.isChangePasswordCheckbox.checked){
    	var message3 = '<%=PropertyProvider.get("prm.project.admin.profile.lenght.password.js")%>';
   	 	var message4 = '<%=PropertyProvider.get("prm.project.admin.profile.lenght.password.and.confirmation.js")%>';
   	 
		if (!checkLength(frm.newPassword,6, message3)) return false;
		if (!checkPasswords(frm.newPassword, frm.newPasswordRetype, message4)) return false;
	}
    if (frm.isChangeHintsCheckbox.checked){
    	var message5 = '<%=PropertyProvider.get("prm.project.admin.profile.must.enter.question.js")%>';
    	var message6 = '<%=PropertyProvider.get("prm.project.admin.profile.must.enter.answer.js")%>';
    	 
        if (!checkTextbox(frm.newClearTextHintPhrase, message5)) return false;
        if (!checkTextbox(frm.newClearTextHintAnswer, message6)) return false;
    }
    return true;
}


function applyChanges() {
    if (updatedProfile) {
        var message = '<%=PropertyProvider.get("prm.project.admin.profile.update.profile.question.js")%>';
        Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', message, function(btn) { 
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


<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.profile.module.history">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="Profile" 
					jspPage='<%=SessionManager.getJSPRootURL() + "/admin/profile/ProfileLoginController.jsp"%>'
                    queryString='<%="module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY + "&userID=" + userID%>' />
			<history:page level="1" display="Login" />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br />
<tab:tabStrip>
	<tab:tab label="Name" href="javascript:tabClick('/admin/profile/ProfileName.jsp');" />
	<tab:tab label="Address" href="javascript:tabClick('/admin/profile/ProfileAddress.jsp');" />
	<tab:tab label="Login" href="javascript:tabClick('/admin/profile/ProfileLoginController.jsp');" selected="true" />
    <tab:tab label="License" href="javascript:tabClick('/admin/profile/ProfileLicense.jsp');" />
</tab:tabStrip>
<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/admin/profile/NativeProfileLoginController.jsp"%>" method="post">
    <input type="hidden" name="module" value="<%= net.project.base.Module.APPLICATION_SPACE %>">
    <input type="hidden" name="nextPage" value="">
	<input type="hidden" name="theAction" value="">
	<input type="hidden" name="fromPage" value="nativeProfileLogin">
	<input type="hidden" name="userID" value="<%=userID%>">

<div align="center">
<table width="90%" cellpadding="0" cellspacing="0" border="0">
    <tr>
        <td colspan="5" class="tableHeader"><display:get name="prm.global.display.requiredfield" /></td>
    </tr>
    <tr><td colspan="5" class="tableHeader">&nbsp;</td></tr>
    <tr>
        <td width=1% class="ActionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
        <td colspan="3" valign="middle" class="ActionBar">
            <%=PropertyProvider.get("prm.project.admin.profile.login.and.password.label") %>
        </td>
        <td width=1% align=right class="ActionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
    </tr>
    <tr align="left">
        <td>&nbsp;</td>
        <td colspan="3" class="tableHeader">
			<%=PropertyProvider.get("prm.project.admin.profile.may.change.login.name.label")%>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5" class="tableHeader">&nbsp;</td></tr>
<%
    if (isError) {
%>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" class="fieldWithError" align="left">
            <%=errorMessage%>
        </td>
        <td>&nbsp;</td>
    </tr>

<%  } %>
    <tr align="left">
        <td nowrap>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td nowrap class="fieldNonRequired" align="right">
        	<%=PropertyProvider.get("prm.project.admin.profile.current.login.name.label")%>&nbsp;&nbsp;</td>
        <td nowrap class="tableContent">
            <c:out value="${nativeDirectoryEntry.currentLogin}"/>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>

    <%-- New Login Name --%>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" class="tableContent">
            <input type="hidden" name="changeLogin" value="<c:out value="${nativeDirectoryEntry.changeLogin}"/>">
            <input type="checkbox" name="isChangeLoginCheckbox" onClick="toggle(this, this.form.changeLogin);" <%=nativeDirectoryEntry.isChangeLogin() ? "selected" : ""%>>&nbsp;
            <%=PropertyProvider.get("prm.project.admin.change.login.name.label")%>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td class="fieldNonRequired" align="right">
        	<%=PropertyProvider.get("prm.project.admin.profile.new.login.name.label")%>&nbsp;&nbsp;</td>
        <td class="fieldNonRequired">
            <input type="text" name="newLogin" size="20" maxlength="80" onchange="setUpdated('true');this.form.isChangeLoginCheckbox.checked='true';toggle(this.form.isChangeLoginCheckbox, this.form.changeLogin);" value="<c:out value="${nativeDirectoryEntry.newLogin}"/>">
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>
    

    <%-- New Password --%>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" class="tableContent">
            <input type="hidden" name="changePassword" value="<c:out value="${nativeDirectoryEntry.changePassword}"/>">
            <input type="checkbox" name="isChangePasswordCheckbox" onClick="toggle(this, this.form.changePassword);" <%=nativeDirectoryEntry.isChangePassword() ? "selected" : ""%>>&nbsp;
            <%=PropertyProvider.get("prm.project.admin.profile.change.password.label")%>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td class="fieldNonRequired" align="right">
        	<%=PropertyProvider.get("prm.project.admin.profile.new.password.label")%>
        	&nbsp;&nbsp;</td>
        <td class="fieldNonRequired">
            <input type="password" name="newPassword" onchange="setUpdated('true');this.form.isChangePasswordCheckbox.checked='true';toggle(this.form.isChangePasswordCheckbox, this.form.changePassword);" size="20" maxlength="20" value="">
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td class="fieldNonRequired" align="right">
        	<%=PropertyProvider.get("prm.project.admin.profile.retype.new.password.label")%>
        	&nbsp;&nbsp;</td>
        <td class="fieldNonRequired">
            <input type="password" name="newPasswordRetype" onchange="setUpdated('true');this.form.isChangePasswordCheckbox.checked='true';toggle(this.form.isChangePasswordCheckbox, this.form.changePassword);" size="20" maxlength="20" value="">
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>

    <%-- Jog Question and Answer --%>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" class="tableContent">
            <input type="hidden" name="changeHints" value="<c:out value="${nativeDirectoryEntry.changeHints}"/>">
            <input type="checkbox" name="isChangeHintsCheckbox" onClick="toggle(this, this.form.changeHints);" <%=nativeDirectoryEntry.isChangeHints() ? "selected" : ""%>>&nbsp;
            <%=PropertyProvider.get("prm.project.admin.profile.change.job.question.or.answer.label")%>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td colspan="2" class="tableHeader">
            <%=PropertyProvider.get("prm.project.admin.profile.job.question.message")%>
            <br>
            For example, Question: <i>What is my dog&acute;s name?</i> &nbsp;&nbsp;
            Answer: <i>spot</i>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td class="fieldNonRequired" align="right">
        	<%=PropertyProvider.get("prm.project.admin.profile.jog.question.label")%>
        	&nbsp;&nbsp;</td>
        <td class="fieldNonRequired">
            <input type="text" name="newClearTextHintPhrase" size="40" maxlength="80" onchange="setUpdated('true');this.form.isChangeHintsCheckbox.checked='true';toggle(this.form.isChangeHintsCheckbox, this.form.changeHints);" value="<c:out value="${nativeDirectoryEntry.newClearTextHintPhrase}"/>">
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td class="fieldNonRequired" align="right">
            <%=PropertyProvider.get("prm.project.admin.profile.jog.answer.label")%>
            &nbsp;&nbsp;
        </td>
        <td class="fieldNonRequired">
            <input type="text" name="newClearTextHintAnswer" size="20" maxlength="80" onchange="setUpdated('true');this.form.isChangeHintsCheckbox.checked='true';toggle(this.form.isChangeHintsCheckbox, this.form.changeHints);" value="<c:out value="${nativeDirectoryEntry.newClearTextHintAnswer}"/>">
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
