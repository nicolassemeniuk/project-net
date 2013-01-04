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
<html>
<head>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Profile Name"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
			net.project.license.LicenseManager,
			net.project.security.SessionManager,
            net.project.security.User,
            net.project.license.system.LicenseProperties,
            net.project.license.system.MasterProperties"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/standard_prototypes.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function reset() {
    self.document.location = JSPRootURL + "/personal/Setup.jsp?module=<%=net.project.base.Module.PERSONAL_SPACE%>";
}

function cancel()	{ self.document.location = JSPRootURL + "/personal/Main.jsp"; }
function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=personal_setup";
	openwin_help(helplocation);
}
</script>
<%--Avinash: bfd 3234  Cancel button in the Personal Profile page navigates to Personal Work Space--%>
<%session.removeAttribute("referer"); %>
</head>

<body class="main" id="bodyWithFixedAreasSupport" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%--------------------------------------------------------------------------------------------------------
  --  Toolbar & History                                                                                   
  ------------------------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.setup.module.history">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.personal.setup.module.history")%>' 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/personal/Setup.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.PERSONAL_SPACE + "&action=" + net.project.security.Action.MODIFY%>'
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

<%-- Personal Setup Section --%>

<div class="personal-setup-header"><display:get name="prm.personal.setup.channel.personal.title" /></div>

<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/personal/ProfileName.jsp?module=<%= net.project.base.Module.PERSONAL_SPACE %>"><display:get name="prm.personal.setup.personalprofile.link" /></a></div>
<display:get name="prm.personal.setup.personalprofile.description" /></div> 

<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/personal/workcalendar/WorkDay?module=<%=net.project.base.Module.PERSONAL_SPACE %>"><display:get name="prm.personal.setup.workingtimecalendar.link" /></a></div>
<display:get name="prm.personal.setup.workingtimecalendar.description" /></div>

<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/notification/ManageSubscriptions.jsp?disabled=true"><display:get name="prm.personal.setup.notifications.link" /></a></div>
<display:get name="prm.personal.setup.notifications.description" /></div>

<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/personal/InterfaceSettings.jsp?module=<%=net.project.base.Module.PERSONAL_SPACE %>"><display:get name="prm.personal.setup.interfacesettings.link" /></a></div> 
<display:get name="prm.personal.setup.interfacesettings.description" /></div>

<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/form/designer/Main.jsp?module=<%=Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>&referer=personal/Setup.jsp"><display:get name="prm.project.setup.manageforms.link" /></a></div>
<display:get name="prm.personal.setup.manageforms.label" /></div>

<%-- sjmittal: TODO ViewDeletedDocuments.jsp to be changed to Main.jsp in future once objects other than documents are displayed in trashcan --%>
<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/trashcan/ViewDeletedDocuments.jsp?module=<%=Module.TRASHCAN%>&action=<%=net.project.security.Action.LIST_DELETED%>&referer=personal/Setup.jsp"><display:get name="prm.personal.setup.trashcan.link" /></a></div>
<display:get name="prm.personal.setup.trashcan.view.label" /></div>


<% if (MasterProperties.masterPropertiesExist() && LicenseProperties.getInstance().isLicenseRequiredAtLogin()) { %>

<div class="personal-setup-header"><display:get name="@prm.personal.nav.licensing" /></div>

<div class="setup-item"><div><a href="<%=SessionManager.getJSPRootURL()%>/personal/license/LicenseManager.jsp?module=<%=Module.PERSONAL_SPACE%>"><display:get name="@prm.personal.nav.licensing" /></a>
<display:get name="prm.personal.setup.channel.licenses.title" /></div>

</div>
<% } %>


<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
