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
    info="html page to hold the 2 gantt frames (tasks and image)"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.base.Module,
            java.util.List,
            java.util.Iterator,
            net.project.schedule.ScheduleEntry,
            net.project.util.DateUtils,
            java.util.Date,
            net.project.calendar.PnCalendar,
            org.apache.log4j.Logger,
            net.project.schedule.gantt.TimeScale,
            net.project.schedule.TaskType,
            net.project.util.Validator,
            net.project.schedule.gantt.pathinfo.LinkLinePathCalculator,
            net.project.gui.history.History,
            net.project.resource.PersonProperty,
            net.project.channel.ScopeType,
            net.project.util.DateFormat,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session"/>
<%!
String baseUrl = SessionManager.getJSPRootURL();
%>
<html><head>

<template:import type="css" src="/styles/gantt.css" />
<template:import type="javascript" src="/src/gantt/task.js"/>
<template:import type="javascript" src="/src/gantt/path.js"/>
<template:import type="javascript" src="/src/gantt/ganttchart.js"/>
<template:import type="javascript" src="/src/gantt/utils.js"/>
<template:import type="javascript" src="/src/gantt/column.js"/>
     
<script type="text/javascript">
function load_next(frame) {
if (frame == 2)
    window.taskFrame.location.href = '<%=SessionManager.getJSPRootURL()+"/schedule/gantt/TaskFrame.jsp?module="+Module.SCHEDULE%>';
else if (frame == 3)
    window.ganttFrame.location.href = '<%=SessionManager.getJSPRootURL()+"/schedule/gantt/GanttFrame.jsp?module="+Module.SCHEDULE+"&timeScaleID="+TimeScale.DAILY.getID()%>';
}
</script>
</head>

<frameset onload="load_next(2)" marginwidth="0" marginheight="0" leftmargin="0" topmargin="0" framespacing="0" rows="25,*" border="1">
    <frame border="0" name="toolbar" noresize="true" marginwidth="0" marginheight="0" leftmargin="0" topmargin="0" scrolling="no" src='<%=SessionManager.getJSPRootURL()+"/schedule/gantt/Toolbar.jsp?module="+Module.SCHEDULE%>'/> 
    <frameset cols="40%,*" framespacing="5" topmargin="0" border="1">
        <frame id="taskFrame" name="taskFrame" src="#" scrolling="yes"/>
        <frame id="ganttFrame" name="ganttFrame" src="#" scrolling="yes"/>
    </frameset>
</frameset>


</html>