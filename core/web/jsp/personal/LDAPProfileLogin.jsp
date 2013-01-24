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
    import="net.project.admin.RegistrationBean,
            net.project.security.SessionManager,
            net.project.security.User"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />

<%
	if (registration.getID() == null && !registration.isLoaded()) {
		registration.setID(user.getID());
		registration.load();
	}
%>

<%@page import="net.project.base.property.PropertyProvider"%><html>
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
    var updatedProfile;
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
    document.registration.nextPage.value = "/personal/ProfileLoginController.jsp";
    applyChanges();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=profile_personal&section=ldap_login";
	openwin_help(helplocation);
}

function validateForm(frm) {
    return true;
}

function applyChanges() {
	var message = '<%=PropertyProvider.get("prm.project.personal.update.profile.label")%>';
    if (updatedProfile) {
        Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', message, function(btn) { 
			if(btn == 'yes') { 
				if (validateForm(document.registration)) {
					theAction("submit");
					theForm.submit();
				 }
			} else {
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

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=profile_personal&section=ldap_login";
	openwin_help(helplocation);
}

</script>


</head>

<body class="main" onLoad="setup();" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.profile.module.history">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="prm.personal.profile.module.history" 
					jspPage='<%=SessionManager.getJSPRootURL() + "/personal/ProfileLoginController.jsp"%>'
					queryString='<%="module=" + net.project.base.Module.PERSONAL_SPACE%>' />
			<history:page level="1" display="Login" />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>
<br />
<tab:tabStrip>
	<tab:tab label="Name" href="javascript:tabClick('/personal/ProfileName.jsp');" />
	<tab:tab label="Address" href="javascript:tabClick('/personal/ProfileAddress.jsp');" />
	<tab:tab label="Login" href="javascript:tabClick('/personal/ProfileLoginController.jsp');" selected="true" />
	<tab:tab label="License" href="javascript:tabClick('/personal/ProfileLicense.jsp');" />
	<tab:tab label="Domain Migration" href="javascript:tabClick('/personal/ProfileDomainMigration.jsp');" />
</tab:tabStrip>
<form name="registration" action="<%=SessionManager.getJSPRootURL() + "/personal/LDAPProfileLoginController.jsp"%>" method="post">
    <input type="hidden" name="module" value="<%= net.project.base.Module.PERSONAL_SPACE %>">
    <input type="hidden" name="nextPage" value="">
	<input type="hidden" name="theAction" value="">
	<input type="hidden" name="fromPage" value="ldapProfileLogin">

<div align="center">
<table width="90%" cellpadding="0" cellspacing="0" border="0">
    <tr>
        <td width=1% class="ActionBar"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
        <td colspan="3" valign="middle" class="ActionBar">
            <%=PropertyProvider.get("prm.project.personal.login.name.and.password.label")%>
        </td>
        <td width=1% align=right class="ActionBar"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
    </tr>
    <tr align="left">
        <td>&nbsp;</td>
        <td colspan="3" class="tableHeader">
			<%=PropertyProvider.get("prm.project.personal.message1") %>
        </td>
        <td>&nbsp;</td> 
    </tr>
    <tr><td colspan="5" class="tableHeader">&nbsp;</td></tr>

    <tr align="left">
        <td nowrap>&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td nowrap class="fieldRequired" align="right">
        	<%=PropertyProvider.get("prm.project.personal.current.login.name.label")%>&nbsp;&nbsp;</td>
        <td nowrap width="50%">
            <jsp:getProperty name="registration" property="login" />
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr><td colspan="5" class="tableHeader">&nbsp;</td></tr>

	<tr>
		<td colspan="5">
<%-- Action Bar --%>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
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
