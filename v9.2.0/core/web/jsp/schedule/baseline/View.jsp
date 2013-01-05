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
|   $Revision: 19034 $
|       $Date: 2009-03-24 13:14:25 -0300 (mar, 24 mar 2009) $
|     $Author: avinash $
|
|--------------------------------------------------------------------%>
<%@ page
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.security.Action,
            net.project.base.Module,
            net.project.util.DateFormat"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="baseline" class="net.project.schedule.Baseline" scope="request" />
<%
    DateFormat df = DateFormat.getInstance();
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>

<template:getSpaceCSS />

<%-- Import Javascript --%>

<script language="javascript" type="text/javascript">

var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function cancel() {
    self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/Baseline/List?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>";
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=schedule_baseline_properties";
	openwin_help(helplocation);
}
</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=net.project.base.property.PropertyProvider.get("prm.schedule.baseline.view.baseline.properties")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/Baseline/View" %>'
					queryString='<%="module="+Module.SCHEDULE+"&action="+Action.VIEW+"&baselineID="+baseline.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>

<channel:channel name="baselineModify" customizable="false">
    <channel:insert name="baseline" width="97%" minimizable="false"
        titleToken="prm.schedule.baseline.view.pagetitle">
        <table border="0" width="100%">
            <tr class="tableContent">
              <td class="tableHeader"><display:get name="prm.schedule.baseline.view.name"/></td>
              <td><%=net.project.util.HTMLUtils.escape(baseline.getName()) %></td>
            </tr>
            <tr class="tableContent">
              <td class="tableHeader"><display:get name="prm.schedule.baseline.view.description"/></td>
              <td><%=net.project.util.HTMLUtils.escape(baseline.getDescription()) %></td>
            </tr>
            <tr class="tableContent">
              <td class="tableHeader"><display:get name="prm.schedule.baseline.view.creationdate"/></td>
              <td><%=df.formatDateTime(baseline.getCreationDate())%></td>
            </tr>
            <tr class="tableContent">
              <td class="tableHeader"><display:get name="prm.schedule.baseline.view.lastmodified"/></td>
              <td><%=df.formatDateTime(baseline.getModifiedDate())%></td>
            </tr>
        </table>
    </channel:insert>
</channel:channel>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
    	<tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
