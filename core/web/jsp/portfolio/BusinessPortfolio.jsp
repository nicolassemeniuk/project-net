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
|   $Revision: 20826 $
|       $Date: 2010-05-10 11:42:15 -0300 (lun, 10 may 2010) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*, 
			net.project.project.*, 
			net.project.space.PersonalSpaceBean, 			
			net.project.business.BusinessSpaceBean,
			net.project.portfolio.ProjectPortfolioBean,
			net.project.space.Space,
			net.project.base.Module,
			net.project.document.DocumentManagerBean,
			net.project.search.SearchManager,
            net.project.space.ISpaceTypes"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="businessSpace" class="net.project.business.BusinessSpaceBean" scope="session" />
<jsp:useBean id="projectPortfolio" class="net.project.portfolio.ProjectPortfolioBean" scope="request" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />

<%
	String id = request.getParameter("id");
%>
<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.BUSINESS_SPACE%>" /> 

<%
	// Switch to personal space if not currently in Personal, Business or Project space
	int currentModule = 0;
	Space currentSpace = user.getCurrentSpace();
	if (currentSpace.isTypeOf(Space.PERSONAL_SPACE)) {
		currentModule = Module.PERSONAL_SPACE;
	} else if (currentSpace.isTypeOf(Space.BUSINESS_SPACE)) {
		currentModule = Module.BUSINESS_SPACE;
	} else if (currentSpace.isTypeOf(Space.PROJECT_SPACE)) {
		currentModule = Module.PROJECT_SPACE;
    } else if (currentSpace.isTypeOf(Space.FINANCIAL_SPACE)) {
        currentModule = Module.FINANCIAL_SPACE;   		
	} else {
		user.setCurrentSpace(personalSpace);
		docManager.getNavigator().put("TopContainerReturnTo", SessionManager.getJSPRootURL() + "/personal/Main.jsp"); 
		currentModule = Module.PERSONAL_SPACE;
	}

	projectPortfolio.clear();
	projectPortfolio.setID(businessSpace.getProjectPortfolioID("owner"));
    projectPortfolio.setUser(user);
    projectPortfolio.setProjectMembersOnly(true);
	projectPortfolio.load();
	
	// Now set up the search object based on this portfolio
	search.clear();
	ProjectSpace entry = null;
	java.util.Iterator it = projectPortfolio.iterator();
	while (it.hasNext()) {
		entry = (ProjectSpace) it.next();
		search.addSearchSpace(entry.getID(), entry.getName(), entry.getDescription());
	}
%>
<%
	String refLink = "/portfolio/BusinessPortfolio.jsp?module=" + currentModule;
	String refLinkEncoded = java.net.URLEncoder.encode(refLink);
%>
<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0"> 
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="business" />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/standard_prototypes.js" />
<template:getSpaceJS space="business" />
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {

	theForm = self.document.forms[0];
	isLoaded = true;
	applyColorToEvenRows();
	
}

function cancel() { self.document.location = JSPRootURL + "/business/Main.jsp"; }
function reset() { self.document.location = JSPRootURL + "/portfolio/BusinessPortfolio.jsp?module=<%=currentModule%>"; }
function create() { self.document.location = JSPRootURL + "/project/ProjectCreate.jsp?module=<%=currentModule%>&action=<%=net.project.security.Action.CREATE%>&parent=<%=user.getCurrentSpace().getID() %>"; }

function search() {
	self.document.location = JSPRootURL + "/search/Search.jsp?module=<%=currentModule%>&refLink=<%=refLinkEncoded%>&spaceType=<%=ISpaceTypes.BUSINESS_SPACE%>";
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=business_project_list";
	openwin_help(helplocation);
}
</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<template:getSpaceNavBar space="business"/>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.business.project.portfolio.module.history">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.business.project.portfolio.module.history")%>' 
					jspPage='<%=SessionManager.getJSPRootURL() + "/poradmin/admin/portfolio/BusinessPortfolio.jsp"%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" label='<%=PropertyProvider.get("prm.business.project.portfolio.create.tooltip")%>' />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form action="<%=SessionManager.getJSPRootURL()%>/business/RemoveProjectProcessing.jsp" method="post">
	<input type="hidden" name="theAction">

<%-- Portfolio Table --%>
<table  border="0" cellpadding="0" cellspacing="0"  width="100%">
<tbody>
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td valign="middle" align="left" class="channelHeader"><%=PropertyProvider.get("prm.business.project.portfolio.channel.projectsowned.title")%></td>
	<td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td class="tableContent">
        <pnet-xml:transform name="projectPortfolio" scope="request" stylesheet="/portfolio/xsl/project-portfolio.xsl">
            <%-- Radio Option omitted since there is no use for it at this time --%>
            <%--<pnet-xml:property name="radioOptionName" value="selected" />--%>
        </pnet-xml:transform>
    </td>
    <td>&nbsp;</td>
</tr>
</tbody>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action" />
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>