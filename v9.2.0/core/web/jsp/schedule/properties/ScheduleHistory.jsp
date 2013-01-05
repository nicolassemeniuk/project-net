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
            net.project.base.Module,
            net.project.security.Action,
            net.project.xml.XMLFormatter"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />
<jsp:useBean id="scheduleHistory" class="net.project.schedule.ScheduleHistory" scope="request" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:import type="css" src="/styles/schedule.css" />
<template:getSpaceCSS />

<script language="javascript" type="text/javascript">
var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

function reset() {
    self.document.location = "<%=SessionManager.getJSPRootURL()%>/schedule/properties/ScheduleHistory.jsp?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>";
}

function cancel() {
    self.document.location = "<%=SessionManager.getJSPRootURL()%>/workplan/taskview?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>";
}

function helpMe() {
  	var helplocation = JSPRootURL + "/help/HelpDesk.jsp?page=schedule_history";
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
			<history:page displayToken='prm.schedule.properties.History'
					jspPage='<%=SessionManager.getJSPRootURL() + "" %>'
					queryString='<%="module="+net.project.base.Module.REPORT%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>

<tab:tabStrip width="97%">
    <tab:tab labelToken='prm.schedule.properties.pagetitle' href='<%=SessionManager.getJSPRootURL() + "/schedule/properties/ScheduleProperties.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY %>'/>
    <tab:tab labelToken='prm.schedule.properties.changeworkingtimes.link' href='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/WorkingTime/List?module="+Module.SCHEDULE+"&action="+Action.VIEW%>'/>
    <tab:tab labelToken='prm.schedule.properties.history.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/ScheduleHistory.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>' selected="true"/>
    <tab:tab labelToken='prm.schedule.properties.baseline' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/Baseline/List?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.tasklistdecorating.pagetitle' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/TaskListDecorating.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <display:if name="@prm.crossspace.isenabled">
        <tab:tab labelToken='prm.schedule.properties.sharing' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/ScheduleProperties/Sharing?module="+Module.SCHEDULE+"&action="+Action.SHARE%>'/>
    </display:if>
    <tab:tab labelToken='prm.schedule.properties.tools.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/Tools.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
</tab:tabStrip>

<%
    scheduleHistory.setPlanID(schedule.getID());
    scheduleHistory.load();

    XMLFormatter formatter = new XMLFormatter();
    formatter.setStylesheet("/schedule/properties/xsl/schedule-history-list.xsl");
    out.println(formatter.getPresentation(scheduleHistory.getXML()));
%>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
    	<tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
