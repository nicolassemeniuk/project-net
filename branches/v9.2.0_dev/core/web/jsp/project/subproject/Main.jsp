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
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.*, 
            net.project.project.ProjectSpaceBean,
            net.project.portfolio.ProjectPortfolio,
            net.project.portfolio.PortfolioManager,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.space.Space,
            net.project.space.SpaceManager,
            net.project.space.SpaceList,
            net.project.space.PersonalSpaceBean,
            net.project.document.DocumentManagerBean,
            net.project.search.SearchManager,
            net.project.xml.XMLFormatter,
            net.project.project.ProjectSpace"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />

<%!
	SpaceList spaceList = null;
	ProjectPortfolio projectPortfolio = null;
	
	String refLink = "/project/subproject/Main.jsp?module=" + Module.PERSONAL_SPACE ;
	String refLinkEncoded = java.net.URLEncoder.encode(refLink);
%>

<%
	// SECURITY
	// No security check necessary since this is the project's subproject portfolio
	// Access will be checked upon entry of the subproject.

	// PORTFOLIO
	spaceList = SpaceManager.getSubprojects(projectSpace);
	projectPortfolio = (ProjectPortfolio) PortfolioManager.makePortfolioFromSpaceList(spaceList);
	projectPortfolio.setUser(user);
	projectPortfolio.load();

	// SEARCH
	// Now set up the search object based on this subproject portfolio
	search.clear();
	
	// add the parent project to the search list.
	search.addSearchSpace(projectSpace.getID(), projectSpace.getName() + " (Parent Project)", projectSpace.getDescription());
	
	ProjectSpace entry = null;
	java.util.Iterator it = projectPortfolio.iterator();
	while (it.hasNext()) {
		entry = (ProjectSpace) it.next();
		search.addSearchSpace(entry.getID(), entry.getName(), entry.getDescription());
	}
%>


<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0"> 
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="project" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  
	var parentProjectURL = JSPRootURL + "/project/Dashboard?id=<%=projectSpace.getID()%>";  
	
function setup() {

	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	isLoaded = true;
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=project_portfolio&section=subprojects";
	openwin_help(helplocation);
}

function cancel() { self.document.location = JSPRootURL + "/project/Main.jsp"; }
function reset() { self.document.location = JSPRootURL + "/project/subproject/Main.jsp?module=<%= Module.PROJECT_SPACE %>"; }
function create() { self.document.location = JSPRootURL + "/project/ProjectCreate.jsp?module=<%= Module.PROJECT_SPACE %>&action=<%=Action.CREATE%>&parent=<%=projectSpace.getID()%>&business=<%=projectSpace.getParentBusinessID()%>"; }

function remove() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		var m_url = JSPRootURL + "/portfolio/PersonalPortfolioDelete.jsp?selected="+ getSelection(theForm) +"&module=<%= Module.PROJECT_SPACE %>"+"&action=<%= net.project.security.Action.VIEW %>";
		//var redirect_url = JSPRootURL +"/project/Main.jsp?id="+ getSelection(theForm)+"&page="+ escape(m_url);
		var link_win = openwin_linker(m_url);
		link_win.focus();
	}
}
function search() { self.document.location = JSPRootURL + "/search/SearchController.jsp?module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.VIEW%>"; }

function gotoParent() {
	self.document.location = parentProjectURL;
}

function properties() { 
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		self.document.location = JSPRootURL + "/project/Properties.jsp?module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.VIEW%>&selected="+getSelectedValue(theForm); 
	}
}
</script>
</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<template:getSpaceNavBar space="project"/>
<%---------------------------------------------------------------------------------------
  -- Draw toolbar & History                                                       
  -------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" groupTitle="prm.project.subproject.module.history">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%= PropertyProvider.get("prm.project.subproject.module.history") %>'
					jspPage='<%=SessionManager.getJSPRootURL()  + "/project/subproject/Main.jsp"%>'
					queryString='<%="module="+net.project.base.Module.PROJECT_SPACE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
		<tb:button type="create" label='<%= PropertyProvider.get("prm.project.subproject.create.button.tooltip") %>' />
		<tb:button type="remove" label='<%= PropertyProvider.get("prm.project.subproject.remove.button.tooltip") %>' />
		<tb:button type="properties" label='<%= PropertyProvider.get("prm.project.subproject.properties.button.tooltip") %>' />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form action="<%=SessionManager.getJSPRootURL()%>/project/RemoveProjectProcessing.jsp" method="post">
    <input type="hidden" name="module" value="<%= Module.PERSONAL_SPACE %>">
	<input type="hidden" name="theAction">

<%-- Portfolio Table --%>
<table  border="0" cellpadding="0" cellspacing="0"  width="97%">
<tr>
	<td align="left" colspan="3" class="pageHeader"><display:get name="prm.project.subproject.subprojectsof.label" /> &nbsp; <%=net.project.util.HTMLUtils.escape(projectSpace.getName())%></td>
</tr>
<tr>
	<td colspan="3" class="tableContent">&nbsp;</td>
</tr>
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td valign="middle" align="left" class="channelHeader"><display:get name="prm.project.subproject.channel.subprojectsof.title" />
	</td>
	<td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td colspan="2">
            <pnet-xml:transform content="<%=projectPortfolio.getXMLBody()%>"  stylesheet="/project/subproject/xsl/subproject-portfolio.xsl">
                <pnet-xml:property name="JSPRootURL" value="<%=SessionManager.getJSPRootURL()%>" />
                <pnet-xml:property name="radioOptionName" value="selected" />
            </pnet-xml:transform>
	</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="back" show="false" label='<%= PropertyProvider.get("prm.project.subproject.back.button.label") %>' function="javascript:gotoParent();" />
		<tb:button type="add" label='<%= PropertyProvider.get("prm.project.subproject.subprojectsof.add.button.label") %>' function="javascript:create();" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS space="project" />
</body>
</html>
