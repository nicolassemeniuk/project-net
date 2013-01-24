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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.enterprise.EnterpriseSpace,
            net.project.channel.ChannelManager,
            net.project.channel.Channel,
            net.project.base.property.PropertyProvider,
            net.project.space.Space,
            net.project.project.ProjectSpaceBean,
            net.project.base.Module,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="enterpriseSpace" class="net.project.enterprise.EnterpriseSpace" scope="session"/>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />


<%--------------------------------------------------------------------------------------------------------
  -- Configure Objects
  ------------------------------------------------------------------------------------------------------%>
<%
	// Set user's current space to this Project
    user.setCurrentSpace(enterpriseSpace);
%>

<%--------------------------------------------------------------------------------------------------------
  -- Security Check
  ------------------------------------------------------------------------------------------------------%>
<%
    Space oldSpace = securityProvider.getSpace();

    // Security Check: Is user allowed access to requested space?
    Space testSpace = new EnterpriseSpace();
    securityProvider.setSpace(testSpace);

    if (!securityProvider.isActionAllowed(null, Integer.toString(Module.ENTERPRISE_SPACE), Action.VIEW)) {
        // Failed Security Check
        securityProvider.setSpace(oldSpace);
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.authorizationfailed.message"), testSpace);
    }

	request.setAttribute("id", "");
	request.setAttribute("module", Integer.toString(net.project.base.Module.ENTERPRISE_SPACE));
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>

<script language="javascript" type="text/javascript">

function setup() {
   var spaceID = '<%=enterpriseSpace.getID()%>';
   load_menu(spaceID);
   load_header();
}

function cancel() {
    history.back();
}
</script>

</head>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.application.nav.space.enterprise">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:enterprise display="<%=enterpriseSpace.getName()%>"
					jspPage='<%=SessionManager.getJSPRootURL() + "/enterprise/Main.jsp" %>'
					queryString='<%="module="+net.project.base.Module.ENTERPRISE_SPACE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
		<tb:button type="refresh"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<br>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
            <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
