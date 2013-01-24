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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
| Entry point for configuration space
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Configuration Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
            net.project.security.SecurityProvider,
			net.project.security.Action,
			net.project.space.Space,
			net.project.base.Module,
			net.project.configuration.ConfigurationSpace,
			net.project.base.property.PropertyManager,
			net.project.security.SessionManager" 
%>

<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="configurationSpace" class="net.project.configuration.ConfigurationSpace" scope="session" />

<%
// This page will be accessed via external notifications.  We need to make sure that
// it is sent they are sent through the navigation frameset
if (request.getParameter("external") != null && request.getParameter("inframe") == null) {
    session.putValue("requestedPage", request.getRequestURI() + 
			"?id="+ request.getParameter("id") + 
			"&module=" + request.getParameter("module"));
    response.sendRedirect(SessionManager.getJSPRootURL() + "/NavigationFrameset.jsp");
    return;
}
%>

<%
    String id = request.getParameter("id");
	
	// If an id is specified, we are switching spaces.
	if (id != null) {
		Space currentSpace = securityProvider.getSpace();
		Space newSpace = new ConfigurationSpace();

		newSpace.setID(id);
		securityProvider.setSpace(newSpace);

        // Security Check: Is user allowed access to requested space?
        if (securityProvider.isActionAllowed(null, Integer.toString(Module.CONFIGURATION_SPACE), Action.VIEW)) {
            // Passed Security Check
			configurationSpace.setID(id);
    	    configurationSpace.load();

		} else {
            // Failed Security Check.  Return to previous space.
            securityProvider.setSpace(currentSpace);
            throw new net.project.security.AuthorizationFailedException("Failed security verification", newSpace);
        }
    }

	// Set user's current space to configuration space
    user.setCurrentSpace(configurationSpace);
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
   load_menu('<%=user.getCurrentSpace().getID()%>');
   load_header();
   theForm = self.document.forms["main"];
   isLoaded = true;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=configuration_main";
	openwin_help(helplocation);
}

function reset() { self.document.location = JSPRootURL + "/configuration/Main.jsp?module=<%=Module.CONFIGURATION_SPACE%>"; }

function modify(id) {
	document.location = "<%=SessionManager.getJSPRootURL()%>/configuration/ConfigurationEdit.jsp?module=<%=Module.CONFIGURATION_SPACE%>&action=<%=Action.MODIFY%>";
}

function remove() {
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.removeconfiguration.confirm")%>', function(btn) { 
		if(btn == 'yes'){ 
			document.location = "<%=SessionManager.getJSPRootURL()%>/configuration/ConfigurationRemoveProcessing.jsp?module=<%=Module.CONFIGURATION_SPACE%>&action=<%=Action.DELETE%>&refLink=<%=java.net.URLEncoder.encode("/portfolio/ConfigurationPortfolio.jsp")%>";			
		}else {
			 return false;
		}
	});
}

</script>
</head>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.configuration">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:business display="<%=configurationSpace.getName()%>"
					 jspPage='<%=SessionManager.getJSPRootURL() + "/configuration/Main.jsp"%>'
					 queryString='<%="module=" + Module.CONFIGURATION_SPACE + "&id=" + configurationSpace.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="modify" />
		<tb:button type="remove" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" name="main" action="<%=SessionManager.getJSPRootURL()%>/configuration/MainProcessing.jsp">
	<input type="hidden" name="theAction">

<channel:channel name='<%="ConfigurationMain_" + configurationSpace.getName()%>' customizable="false">

	<channel:insert name='<%="ConfigurationSpaceProperties_" + configurationSpace.getName()%>'
					title="Properties" minimizable="false" closeable="false"
					include="include/Properties.jsp" >
	</channel:insert>

</channel:channel>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
