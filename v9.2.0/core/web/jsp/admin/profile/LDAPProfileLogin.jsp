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
|   $Revision: 15794 $
|       $Date: 2007-04-07 10:41:46 +0530 (Sat, 07 Apr 2007) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Login Profile"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.admin.RegistrationBean,
            net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.security.User,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
    // Page should only be forwarded to from ProfileLoginController or NativeProfileLoginController
    // Assumes current registration bean is loaded with selected user
    String userID = registration.getID();
%>
<html>
<head>

<title>Login Profile</title>
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
    var updatedProfile = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms["registration"];
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
    theAction("submit");
    document.registration.nextPage.value = "/admin/profile/ProfileLoginController.jsp";
    applyChanges();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=profile_personal&section=ldap_login";
	openwin_help(helplocation);
}

function validateForm(frm) {
	var message = '<%=PropertyProvider.get("prm.project.admin.profile.username.required.js")%>';
    if (!checkTextbox(frm.username, message)) return false;
    return true;
}

function applyChanges() {
    if (updatedProfile) {
    	var message = '<%=PropertyProvider.get("prm.project.admin.profile.update.profile.js")%>';
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

</script>


</head>

<body class="main" onLoad="setup();" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>

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
<br />
<tab:tabStrip>
	<tab:tab label="Name" href="javascript:tabClick('/admin/profile/ProfileName.jsp');" />
	<tab:tab label="Address" href="javascript:tabClick('/admin/profile/ProfileAddress.jsp');" />
	<tab:tab label="Login" href="javascript:tabClick('/admin/profile/ProfileLoginController.jsp');" selected="true" />
    <tab:tab label="License" href="javascript:tabClick('/admin/profile/ProfileLicense.jsp');" />
</tab:tabStrip>
<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/admin/profile/LDAPProfileLoginController.jsp"%>" method="post">
    <input type="hidden" name="module" value="<%= net.project.base.Module.APPLICATION_SPACE %>">
    <input type="hidden" name="nextPage" value="">
	<input type="hidden" name="theAction" value="">
	<input type="hidden" name="fromPage" value="ldapProfileLogin">
	<input type="hidden" name="userID" value="<%=userID%>">


<div align="center">
<table width="90%" cellpadding="0" cellspacing="0" border="0">
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
			<%=PropertyProvider.get("prm.project.admin.profile.username.message")%>
        </td>
        <td>&nbsp;</td>
    </tr>
<%
String error = request.getParameter("error");
if (Validator.isBlankOrNull(error)) {
    error = (String)request.getAttribute("error");
}

if (!Validator.isBlankOrNull(error)) {
    out.println("<tr><td>&nbsp;</td><td colspan=\"3\" class=\"errorMessage\">"+error+"</td><td>&nbsp;</td></tr>");
} 
%>
    <tr><td colspan="5" class="tableHeader">&nbsp;</td></tr>

    <tr align="left">
        <td nowrap>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td nowrap class="fieldRequired" align="right"><%=PropertyProvider.get("prm.project.admin.current.login.name")%>&nbsp;&nbsp;</td>
        <td nowrap width="50%">
            <input type="text" name="username" value="<%=registration.getLogin()%>" onchange="setUpdated('true');"/>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr><td colspan="5" class="tableHeader">&nbsp;</td></tr>

	<tr>
		<td colspan="5">
<%-- Action Bar --%>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="submit" />
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>
		</td>
	</tr>
</table>
</div>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
