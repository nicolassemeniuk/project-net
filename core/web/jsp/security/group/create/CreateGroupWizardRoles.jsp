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
    info="Role - Create a new role" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
			net.project.security.User,
			net.project.security.Action,
			net.project.security.SessionManager,
			net.project.security.group.CreateGroupWizard,
			net.project.space.Space,
            net.project.space.SpaceList,
            net.project.space.SpaceManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="createWizard" class="net.project.security.group.CreateGroupWizard" scope="session" />

<%
    // Necessary security check since personal space has all modules listed
    // for it
	if (user.getCurrentSpace().getType().equalsIgnoreCase(Space.PERSONAL_SPACE)) {
    	throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.roles.groupedit.rolesnotaccessible.message"));
	}
%>
<security:verifyAccess action="create"
					   module="<%=Module.DIRECTORY%>" /> 

<%
    // Set the current page
    createWizard.setCurrentPageForName(CreateGroupWizard.SELECT_ROLE_PAGE);
    CreateGroupWizard.SelectRolePage currentPage = (CreateGroupWizard.SelectRolePage) createWizard.getCurrentPage();

%>
<html>
<head>
<title><%=PropertyProvider.get("prm.directory.roles.groupcreate.rolespage.title")%></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  

function setup() {
    isLoaded = true;
    theForm = self.document.forms["main"];
    selectRadio("permissionSelection", "<%=currentPage.getPermissionSelection()%>");
}

function selectRadio(name, value) {
    var theRadio = theForm.elements[name];
    if (theRadio) {
        for (var i=0; i<theRadio.length; i++) {
            if (theRadio[i].value == value) {
                theRadio[i].checked = true;
                break;
            }
        }
    }
}
function finish() 
{
	if(validateForm()) {
        theAction("finish");
        theForm.target = "_self";
        theForm.submit();
    }
}

function back() 
{
	if(validateForm()) {
        theAction("back");
        theForm.target = "_self";
        theForm.submit();
    }
}

function validateForm()
{
    return true;
}

function cancel() {
    theAction("cancel");
    theForm.target = "_self";
    theForm.submit();
}
</script>

</head>

<body class="main" onLoad="setup();">
<span class="pageTitle"><%=PropertyProvider.get("prm.directory.roles.groupcreate.inherit.pagetitle")%></span>

<form method="post" name="main" action="<%=SessionManager.getJSPRootURL() + "/security/group/create/CreateGroupWizardRolesProcessing.jsp"%>">
	<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
	<input type="hidden" name="action" value="<%=Action.CREATE%>">
	<input type="hidden" name="theAction" value="" >

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
    <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
    <td nowrap class="channelHeader" colspan="2"><nobr><%=PropertyProvider.get("prm.directory.roles.groupcreate.roles.channel.select.title")%></nobr></td>
    <td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td>&nbsp;</td>
    <td colspan="2" class="tableHeader">
        <%=PropertyProvider.get("prm.directory.roles.groupcreate.roles.apply.label")%>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- BFD 3061 Avinash Addition of the Option to inherit the role --%>
<tr>
    <td>&nbsp;</td>
    <td width="10%">&nbsp;</td>
    <td class="tableContent">
        <input type="radio" name="permissionSelection" value="inherit" />&nbsp;<%=PropertyProvider.get("prm.directory.roles.groupcreate.roles.option.inheritpermissions.name")%>
    </td>
    <td>&nbsp;</td>
</tr>
<%-- BFD 3061 Avinash Addition of the Option to inherit the role--%>
<tr>
    <td>&nbsp;</td>
    <td width="10%">&nbsp;</td>
    <td class="tableContent">
        <input type="radio" name="permissionSelection" value="default" />&nbsp;<%=PropertyProvider.get("prm.directory.roles.groupcreate.roles.option.defaultpermissions.name")%>
    </td>
    <td>&nbsp;</td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td width="10%">&nbsp;</td>
    <td class="tableContent">
        <input type="radio" name="permissionSelection" value="view" />&nbsp;<%=PropertyProvider.get("prm.directory.roles.groupcreate.roles.option.viewpermissions.name")%>
    </td>
    <td>&nbsp;</td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td width="10%">&nbsp;</td>
    <td class="tableContent">
        <input type="radio" name="permissionSelection" value="none" />&nbsp;<%=PropertyProvider.get("prm.directory.roles.groupcreate.roles.option.nopermissions.name")%>
    </td>
    <td>&nbsp;</td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr><td colspan="4"><%=currentPage.getErrorMessage("selectedGroups")%></td></tr>
<tr>
    <td>&nbsp;</td>
    <td colspan="2">
        <pnet-xml:transform stylesheet="/security/group/create/xsl/SpaceGroups.xsl" content="<%=currentPage.getSpaceGroupsXMLBody()%>">
            <pnet-xml:property name="JSPRootURL" value="<%=SessionManager.getJSPRootURL()%>" />
        </pnet-xml:transform>
    </td>
    <td>&nbsp;</td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
</table> 
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="back" />
		<tb:button type="finish" />
	</tb:band>
</tb:toolbar>
</form>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
<%currentPage.clearErrors();%>