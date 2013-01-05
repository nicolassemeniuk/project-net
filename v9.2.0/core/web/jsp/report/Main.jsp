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
    info="Report Module Main Page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,
            net.project.report.ReportType,
            net.project.security.SessionManager,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript" type="text/javascript">

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=report_main";
	openwin_help(helplocation);
}
<%-- bfd 3239: JS Error occured when refreshing the Reports Page --%>
function reset() {
}
</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.reports">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display="@prm.report.module.name"
					jspPage='<%=SessionManager.getJSPRootURL() + "/report/Main.jsp"%>'
					queryString='<%="module="+net.project.base.Module.REPORT%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'><br>

<channel:channel name="report_main" customizable="false">
	<channel:insert name="Main"  title='<%= PropertyProvider.get("prm.report.reportlist.title") %>' minimizable="false"
		width="100%" closeable="false"
		include="/report/include/ReportList.jsp?module=310" />
</channel:channel> 

<span class="tableContent">
<%@ include file="/help/include_outside/footer.jsp"%></span>

<template:getSpaceJS />
</body>
</html>
