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

<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />

<%@page import="net.project.security.Action"%><html>
<head>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Business Setup"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
			net.project.base.Module,
			net.project.base.property.PropertyProvider,
            net.project.security.User,
            net.project.space.SpaceTypes,
            net.project.hibernate.service.ServiceFactory"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<title><display:get name="prm.global.application.title" /></title>
	<%-- Import CSS --%>
	<template:getSpaceCSS />
	
	<template:import type="javascript" src="/src/standard_prototypes.js" />
	<%-- Validation JS Libraries --%>
	<template:import type="javascript" src="/src/checkComponentForms.js" />
	<template:import type="javascript" src="/src/errorHandler.js" />
	
	<script language="javascript">
		var theForm;
		var isLoaded = false;
		var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
		
		function help() {
			var helplocation=JSPRootURL+"/help/Help.jsp?page=financial_setup"
			openwin_help(helplocation);
		}
	</script>

</head>
<body class="main" id="bodyWithFixedAreasSupport" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%--------------------------------------------------------------------------------------------------------
  --  Toolbar & History                                                                                   
  ------------------------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.setup">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module displayToken="@prm.financial.setup.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/financial/Setup.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.FINANCIAL_SPACE%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<%--------------------------------------------------------------------------------------------------------
  --  Page Content                                                                                        
  ------------------------------------------------------------------------------------------------------%>
<%--
<div class="financial-setup-header"><%= PropertyProvider.get("prm.business.setup.channel.personalsettings.title")%></div>

<display:if name="prm.business.setup.notification.isenabled">
	<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/notification/ManageSubscriptions.jsp?spaceID=<%=user.getCurrentSpace().getID()%>&module=<%=net.project.base.Module.BUSINESS_SPACE%>"><display:get name="prm.business.setup.notification.link" /></a></div>
	<display:get name="prm.business.setup.notification.label" /></div> 
</display:if>
--%>

<%-- Administrator Section --%>
<div class="financial-setup-header">
	<display:get name="prm.financial.setup.channel.financialadministrator.title" />
</div>

<%-- 
<display:if name="prm.business.setup.editbusiness.isenabled">
	<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/business/ModifyBusiness.jsp?module=<%=net.project.base.Module.BUSINESS_SPACE%>&action=<%=net.project.security.Action.MODIFY%>&referer=Setup.jsp?module=<%=Module.BUSINESS_SPACE%>"><display:get name="prm.business.setup.editbusiness.link" /></a></div>
	<display:get name="prm.business.setup.editbusiness.label" /></div> 
</display:if>
--%>

<display:if name="prm.financial.setup.directory.isenabled">
	<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/financial/DirectorySetup.jsp?module=<%=net.project.base.Module.DIRECTORY%>&referer=financial/Setup.jsp"><display:get name="prm.financial.setup.directory.link" /></a></div>
	<display:get name="prm.financial.setup.directory.label" /></div> 
</display:if>

<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/security/SecurityModuleMain.jsp?module=<%= net.project.base.Module.SECURITY%>&referer=financial/Setup.jsp"><display:get name="prm.financial.setup.securitysettings.link" /></a></div>
<display:get name="prm.financial.setup.securitysettings.label" /></div> 

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
