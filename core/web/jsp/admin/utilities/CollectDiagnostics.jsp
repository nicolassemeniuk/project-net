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
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp"%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript" type="text/javascript">
function cancel() {
    history.back();
}
</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id='content'>

<tb:toolbar style="tooltitle" bottomFixed="true" groupTitle="Diagnostics">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="Diagnostics"
					jspPage='<%=SessionManager.getJSPRootURL() + "/admin/utilities/CollectDiagnostics.jsp" %>'
					queryString='<%="module="+Module.APPLICATION_SPACE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
		<tb:button type="refresh"/>
	</tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
