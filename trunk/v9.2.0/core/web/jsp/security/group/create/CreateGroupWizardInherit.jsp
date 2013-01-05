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
            net.project.space.SpaceManager,
            net.project.security.AuthorizationFailedException"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="createWizard" class="net.project.security.group.CreateGroupWizard" scope="session" />

<%
    // Necessary security check since personal space has all modules listed
    // for it
	if (user.getCurrentSpace().getType().equalsIgnoreCase(Space.PERSONAL_SPACE)) {
    	throw new AuthorizationFailedException(PropertyProvider.get("prm.directory.roles.groupedit.rolesnotaccessible.message"));
	}
%>
<security:verifyAccess action="create"
					   module="<%=Module.DIRECTORY%>" /> 

<%
    // Set the current page
    createWizard.setCurrentPageForName(CreateGroupWizard.SELECT_SPACE_PAGE);
    CreateGroupWizard.SelectSpacePage currentPage = (CreateGroupWizard.SelectSpacePage) createWizard.getCurrentPage();

    SpaceList parentSpaces = null;

    boolean isInheritFromWorkspace = currentPage.isInheritFromWorkspace();
    boolean isInheritFromDomain = currentPage.isInheritFromDomain();
    
    if (isInheritFromWorkspace) {
        // Get the parent spaces for this space
        parentSpaces = SpaceManager.getRelatedParentSpaces(user.getCurrentSpace());
        parentSpaces.loadSpaces();
        currentPage.setWorkspaces(parentSpaces);
        pageContext.setAttribute("parentSpaces", parentSpaces, PageContext.PAGE_SCOPE);
        
    }

    if (isInheritFromDomain) {
        // Load the domain lists
        // currentPage.setDomains();
    }

%>
<html>
<head>
<title><%=PropertyProvider.get("prm.directory.roles.groupcreate.inheritpage.title")%></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  

function setup() {
    isLoaded = true;
    theForm = self.document.forms["main"];
}

function next() 
{
	if(validateForm()) {
        theAction("next");
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

<form method="post" name="main" action="<%=SessionManager.getJSPRootURL() + "/security/group/create/CreateGroupWizardInheritProcessing.jsp"%>">
	<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
	<input type="hidden" name="action" value="<%=Action.CREATE%>">
	<input type="hidden" name="theAction" value="" >

<table width="100%" border="0" cellspacing="0" cellpadding="0">

<%  if (isInheritFromWorkspace) { %>
<tr class="channelHeader">
    <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
    <td  nowrap  class="channelHeader" colspan="2"><nobr><%=PropertyProvider.get("prm.directory.roles.groupcreate.inherit.channel.workspace.title")%></nobr></td>
    <td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr><td colspan="4"><%=currentPage.getAllErrorMessages()%></td></tr>
<tr>
    <td>&nbsp;</td>
    <td colspan="2">
        <pnet-xml:transform stylesheet="/security/group/create/xsl/InheritSpaces.xsl" content="<%=currentPage.getWorkspaces().getXMLBodyProperties()%>">
            <pnet-xml:property name="JSPRootURL" value="<%=SessionManager.getJSPRootURL()%>" />
        </pnet-xml:transform>
    </td>
    <td>&nbsp;</td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<%  } %>

<%  if (isInheritFromDomain) { %>
<tr class="channelHeader">
    <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
    <td  nowrap  class="channelHeader" colspan="2"><nobr><%=PropertyProvider.get("prm.directory.roles.groupcreate.inherit.channel.domain.title")%></nobr></td>
    <td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td>&nbsp;</td>
    <td colspan="2">
        <h4>Todo: Insert list of domains that user can see</h4>
    </td>
    <td>&nbsp;</td>
</tr>
<%  } %>

</table> 
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="back" />
		<tb:button type="next" />
	</tb:band>
</tb:toolbar>
</form>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
<%currentPage.clearErrors();%>