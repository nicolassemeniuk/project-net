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
			net.project.security.group.Group,
			net.project.security.group.UserDefinedGroup,
			net.project.space.Space,
            net.project.project.ProjectVisibility,
            net.project.space.SpaceType,
            net.project.space.ISpaceTypes,
            net.project.util.StringUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="createWizard" class="net.project.security.group.CreateGroupWizard" scope="session" />
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
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
    createWizard.setSpace(user.getCurrentSpace());
    createWizard.setUser(user);
    
    // Set the current page
    createWizard.setCurrentPageForName(CreateGroupWizard.CREATE_PAGE);
    CreateGroupWizard.CreatePage currentPage = (CreateGroupWizard.CreatePage) createWizard.getCurrentPage();

    // Get the current group
    Group groupBean = currentPage.getGroup();
    pageContext.setAttribute("group", groupBean, PageContext.PAGE_SCOPE);
%>
<jsp:useBean id="group" type="net.project.security.group.Group" scope="page" />

<html>
<head>
<title><%=PropertyProvider.get("prm.directory.roles.groupcreatepage.title")%></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />


<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  

function setup() {
    isLoaded = true;
    theForm = self.document.forms["NEWGROUP"];
    selectRadio("createAction", "<%=currentPage.getCreateAction()%>");
}

function next() 
{
	if(validateForm()) {
        theAction("next");
        theForm.target = "_self";
        theForm.submit();
    }
}

function validateForm()
{
	    if ((theForm.createAction && theForm.createAction[0].checked) &&
                !checkTextbox(theForm.name,'<%=PropertyProvider.get("prm.directory.roles.groupcreate.namerequired.message")%>')) {
            return false;
        }
        return true;
}

function cancel() {
    theAction("cancel");
    theForm.target = "_self";
    theForm.submit();
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
</script>

</head>

<body class="main" onLoad="setup();">
<span class="pageTitle"><%=PropertyProvider.get("prm.directory.roles.groupcreate.pagetitle")%></span>

<form method="post" name="NEWGROUP" action="<%=SessionManager.getJSPRootURL() + "/security/group/create/CreateGroupWizardStartProcessing.jsp"%>">
	<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
	<input type="hidden" name="action" value="<%=Action.CREATE%>">
	<input type="hidden" name="theAction" value="" >

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
    <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
    <td  nowrap  class="channelHeader" colspan="2"><nobr><%=PropertyProvider.get("prm.directory.roles.groupcreate.channel.create.title")%></nobr></td>
    <td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td>&nbsp;</td>
    <td colspan="2" class="fieldRequired">
        <input type="radio" name="createAction" value="create" />&nbsp;<%=PropertyProvider.get("prm.directory.roles.groupcreate.create.label")%>
    </td>
    <td>&nbsp;</td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td colspan="2" class="fieldNonRequired">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="10%">&nbsp;</td>
            <td align="left" class="fieldRequired"><%=group.getFlagError("name", PropertyProvider.get("prm.directory.roles.groupcreate.name.label"))%></td>
            <td align="left"><input type="text" name="name" size="40" maxlength="80" onChange='selectRadio("createAction", "create");' value="<%=net.project.util.HTMLUtils.escape(group.getName()) %>" /></td>
        </tr>
        <% if(StringUtils.isNotEmpty(group.getErrorMessage("name"))){ %>
        <tr>
        	<td width="10%">&nbsp;</td>
        	<td colspan="2" align="left"><%=group.getErrorMessage("name")%></td>
        </tr>
        <% } %>
        <tr>
            <td width="10%">&nbsp;</td>
    	    <td align="left" class="fieldNonRequired"><%=group.getFlagError("description", PropertyProvider.get("prm.directory.roles.groupcreate.description.label"))%></td>
        	<td align="left"><input type="text" name="description" size="40" maxlength="255" onChange='selectRadio("createAction", "create");' value="<%=net.project.util.HTMLUtils.escape(group.getDescription()) %>" /><%=group.getErrorMessage("description")%></td>
        </tr>
        </table>
    </td>
    <td>&nbsp;</td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td>&nbsp;</td>
    <td colspan="2" class="fieldRequired">
        <input type="radio" name="createAction" value="inherited" />&nbsp;<%=PropertyProvider.get("prm.directory.roles.groupcreate.selectfrom.label")%><br />
        <%=currentPage.getErrorMessage("inherit")%>
    </td>
    <td>&nbsp;</td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td colspan="2">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="10%">&nbsp;</td>
        	<td align="left" class="fieldNonRequired" colspan="2"><input type="checkbox" name="inheritFrom" value="workspace" onClick='selectRadio("createAction", "inherit");' <%=currentPage.getInheritFromChecked("workspace")%> />&nbsp;<%=PropertyProvider.get("prm.directory.roles.groupcreate.relatedworkspaces.label")%></td>
        </tr>
<%-- 11/12/2001 - Need to implement domain selection
        <tr>
            <td width="10%">&nbsp;</td>
        	<td align="left" class="fieldNonRequired" colspan="2"><input type="checkbox" name="inheritFrom" value="domain" onClick='selectRadio("createAction", "inherit");' <%=currentPage.getInheritFromChecked("domain")%> />&nbsp;Domain</td>
        </tr>
--%>
        </table>
    </td>
    <td>&nbsp;</td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<% if (PropertyProvider.getBoolean("prm.global.globalvisibility.isenabled")) { %>
<tr>
    <td>&nbsp;</td>
    <td colspan="2" class="fieldRequired">
        <input type="radio" name="createAction" value="everyone"/>&nbsp;<%=PropertyProvider.get("prm.directory.roles.groupcreate.everyone.label")%><br/>
        <%=currentPage.getErrorMessage("everyone")%>
    </td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td colspan="2" class="fieldRequired">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="10%">&nbsp;</td>
            <td align="left" class="fieldNonRequired" colspan="2">
                <display:get name="prm.directory.roles.groupcreate.everyone.whichusers.label"/>
            </td>
        </tr>
        <tr>
            <td width="10%">&nbsp;</td>
            <td align="left" class="fieldNonRequired" colspan="2">
                <input type="radio" name="visibilityID" value="<%=ProjectVisibility.PROJECT_PARTICIPANTS.getID()%>"><%=ProjectVisibility.PROJECT_PARTICIPANTS.getName()%>
            </td>
        </tr>
        <tr>
            <td width="10%">&nbsp;</td>
            <td align="left" class="fieldNonRequired" colspan="2">
                <input type="radio" name="visibilityID" value="<%=ProjectVisibility.OWNING_BUSINESS_PARTICIPANTS.getID()%>"><%=ProjectVisibility.OWNING_BUSINESS_PARTICIPANTS.getName()%>
            </td>
        </tr>
        <tr>
            <td width="10%">&nbsp;</td>
            <td align="left" class="fieldNonRequired" colspan="2">
                <input type="radio" name="visibilityID" value="<%=ProjectVisibility.GLOBAL.getID()%>"><%=ProjectVisibility.GLOBAL.getName()%>
            </td>
        </tr>
        </table>
    </td>
</tr>
<% } %>
<tr><td colspan="4">&nbsp;</td></tr>
</table>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="next" />
	</tb:band>
</tb:toolbar>
</form>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
<%
    group.clearErrors();
    currentPage.clearErrors();
%>