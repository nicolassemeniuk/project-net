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
|--------------------------------------------------------------------%>
<%@ page
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp"%>

<template:getDoctype />

<%@page import="net.project.base.property.PropertyProvider"%><html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript" type="text/javascript">
function cancel() {
    self.location = "<%=SessionManager.getJSPRootURL() + "/admin/Main.jsp?module=" + Module.APPLICATION_SPACE%>";
}

function reset() {
    self.location = "<%=SessionManager.getJSPRootURL() + "/admin/utilities/ConnectionInfo.jsp?module="+Module.APPLICATION_SPACE%>";
}
</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="Diagnostics">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="Database Connection Info"
					jspPage='<%=SessionManager.getJSPRootURL() + "/admin/utilities/ConnectionInfo.jsp" %>'
					queryString='<%="module="+Module.APPLICATION_SPACE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
		<tb:button type="refresh"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<br>
<%--
    Commented out due to switch back to 9 jdbc driver
    OracleConnectionCacheManager cacheMgr = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
%>
<span class="tableContent">Available Connections: <%=cacheMgr.getNumberOfActiveConnections(OracleConnectionProvider.PNET_CONNECTION_CACHE_NAME)%></span><br/>
<span class="tableContent">Active Connections: <%=cacheMgr.getNumberOfAvailableConnections(OracleConnectionProvider.PNET_CONNECTION_CACHE_NAME)%></span><br/>
--%>
<SPAN class="tableContent"><%=PropertyProvider.get("prm.project.admin.utilities.currently.jdbc.driver.label") %></SPAN>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
            <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
