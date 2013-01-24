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
    import="net.project.report.IReport,
            net.project.xml.XMLFormatter,
            net.project.security.SessionManager,
            net.project.util.HttpUtils,
            net.project.base.property.PropertyProvider,
            net.project.report.ReportType"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="report" type="net.project.report.IReport" scope="session" />
<%
    String reportParameters = HttpUtils.getRedirectParameterString(request);
    ReportType reportType = ReportType.getForID(request.getParameter("reportType"));
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<template:getSpaceCSS />
<!-- Order is important here -->
<template:import type="css" src="/styles/report.css"/>

<script language="javascript" type="text/javascript">
function cancel() {
    history.back();
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/HelpDesk.jsp?page=report_html";
	openwin_help(helplocation);
}

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
			<history:page display='<%=report.getReportName()%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/report/ShowHTMLReport.jsp"%>'
					queryString='<%=reportParameters%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<%
    XMLFormatter xmlFormatter = new XMLFormatter();
    xmlFormatter.setXML(report.getXML());
    xmlFormatter.setStylesheet(reportType.getXSLPath());
    out.println(xmlFormatter.getPresentation());
%>

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