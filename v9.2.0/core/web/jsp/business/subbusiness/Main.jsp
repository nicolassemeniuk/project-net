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
				net.project.business.BusinessSpaceBean,
				net.project.business.BusinessSpace,
				net.project.portfolio.BusinessPortfolio,
				net.project.portfolio.PortfolioManager,
				net.project.base.Module,
				net.project.space.Space,
				net.project.space.SpaceManager,
				net.project.space.SpaceList,
				net.project.document.DocumentManagerBean,
				net.project.search.SearchManager,
				net.project.base.property.PropertyProvider,
				net.project.xml.XMLFormatter" 
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="businessSpace" class="net.project.business.BusinessSpaceBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />

<%!
	SpaceList spaceList = null;
	BusinessPortfolio businessPortfolio = null;
	
	String refLink = "/business/subbusiness/Main.jsp?module=" + Module.PERSONAL_SPACE ;
	String refLinkEncoded = java.net.URLEncoder.encode(refLink);
%>

<%
	// SECURITY
	// No security check necessary since this is the business's subbusiness portfolio
	// Access will be checked upon entry of the subbusiness.

	// PORTFOLIO
	spaceList = SpaceManager.getSubbusinesses(businessSpace);
	businessPortfolio = (BusinessPortfolio) PortfolioManager.makePortfolioFromSpaceList(spaceList);
	businessPortfolio.load();

	// SEARCH
	// Now set up the search object based on this subproject portfolio
	search.clear();
	
	// add the parent project to the search list.
	search.addSearchSpace(businessSpace.getID(), businessSpace.getName() + " (Parent Business)", businessSpace.getDescription());
	
	BusinessSpace entry = null;
	java.util.Iterator it = businessPortfolio.iterator();
	while (it.hasNext()) {
		entry = (BusinessSpace) it.next();
		search.addSearchSpace(entry.getID(), entry.getName(), entry.getDescription());
	}
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="business" />

<%-- Import Javascript --%>
<template:getSpaceJS space="business" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  
	
function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	isLoaded = true;
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=business_subbusiness";
	openwin_help(helplocation);
}

function cancel() { self.document.location = JSPRootURL + "/business/Main.jsp"; }
function reset() { self.document.location = JSPRootURL + "/business/subbusiness/Main.jsp?module=<%= Module.BUSINESS_SPACE %>"; }
function create() { self.document.location = JSPRootURL + "/business/CreateBusiness1.jsp?module=<%= Module.BUSINESS_SPACE %>&parent=<%=businessSpace.getID()%>"; }

function search() {
	self.document.location = JSPRootURL + "/search/Search.jsp?module=<%=Module.PERSONAL_SPACE%>&refLink=<%=refLinkEncoded%>";
}

function gotoParent() {
	self.document.location = parentProjectURL;
}

</script>
</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<template:getSpaceNavBar space="business"/>
<%---------------------------------------------------------------------------------------
  -- Draw toolbar & History                                                       
  -------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" groupTitle="prm.business.subbusiness.business.label">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%= PropertyProvider.get("prm.business.subbusiness.module.history") %>' 
					jspPage='<%=SessionManager.getJSPRootURL()  + "/project/subproject/Main.jsp"%>'
					queryString='<%="module="+net.project.base.Module.PROJECT_SPACE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
		<% if(user.isSpaceAdministrator()/*is ADMINISTRATOR*/ || user.isSpaceGroupMember(net.project.security.group.GroupTypeID.POWER_USER)/*is POWER_USER*/) { %>
			<tb:button type="create" label='<%= PropertyProvider.get("prm.business.subbusiness.create.button.tooltip") %>' />
		<% } %>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form action="<%=SessionManager.getJSPRootURL()%>/project/RemoveProjectProcessing.jsp" method="post">
    <input type="hidden" name="module" value="<%= Module.PERSONAL_SPACE %>">
	<input type="hidden" name="theAction">

<%-- Portfolio Table --%>
<table  border="0" cellpadding="0" cellspacing="0"  width="97%">
<tr>
	<td align="left" colspan="3" class="pageHeader"><display:get name="prm.business.subbusiness.subbusinessesof.label" /> &nbsp; <%=businessSpace.getName()%></td>
</tr>
<tr>
	<td colspan="3" class="tableContent">&nbsp;</td>
</tr>
<tr class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td valign="middle" align="left" class="channelHeader"><display:get name="prm.business.subbusiness.channel.subbusinessesof.title" />
	</td>
	<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td colspan="2">
		<%-- Apply stylesheet to format project portfolio rows for Personal Space --%>
		<%
    		XMLFormatter formatter = new XMLFormatter();
			formatter.setStylesheet("/business/subbusiness/xsl/subbusiness-portfolio.xsl");
    		out.print(formatter.getPresentation(businessPortfolio.getXML()));
		%>
	</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="back" show="false" label='<%= PropertyProvider.get("prm.business.subbusiness.back.button.label") %>' function="javascript:gotoParent();" />
		<!-- tb:button type="add" label='<%= PropertyProvider.get("prm.business.subbusiness.create.button.label") %>' function="javascript:create();" -->
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>
