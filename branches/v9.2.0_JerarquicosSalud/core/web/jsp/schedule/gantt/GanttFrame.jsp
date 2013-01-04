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
    import="net.project.security.SessionManager,
            net.project.base.Module,
            net.project.schedule.gantt.TimeScale,
            org.apache.log4j.Logger,
            net.project.util.DateUtils,
            java.util.Date,
            net.project.calendar.PnCalendar,
            net.project.resource.PersonProperty,
            net.project.channel.ScopeType,
            java.util.List,
            java.util.Iterator,
            net.project.schedule.ScheduleEntry,
            net.project.util.DateFormat,
            net.project.schedule.TaskType,
            net.project.util.Validator,
            net.project.schedule.gantt.pathinfo.LinkLinePathCalculator,
            net.project.gui.history.History"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<html> <head>
<title></title>

<%
    PersonProperty properties = PersonProperty.getFromSession(session);
    properties.setScope(ScopeType.SPACE.makeScope(user));
    String[] showProgressLines = properties.get("prm.schedule.gantt", "showProgressLines");
    boolean bShowProgressLines = (showProgressLines != null && showProgressLines.length > 0 ? new Boolean(showProgressLines[0]).booleanValue() : false);

    String timeScaleID = request.getParameter("timeScaleID");
    TimeScale timeScale = TimeScale.getForID(timeScaleID);
    PnCalendar cal = new PnCalendar(SessionManager.getUser());

    Logger logger = Logger.getLogger("GanttFrame.jsp");
    logger.debug("TimescaleID: "+timeScaleID);

    Date scheduleStartDate = schedule.getScheduleStartDate();
    if (schedule.getEarliestTaskStartTimeMS() != -1) {
        scheduleStartDate = DateUtils.min(scheduleStartDate, new Date(schedule.getEarliestTaskStartTimeMS()));
    }
    scheduleStartDate = cal.startOfDay(scheduleStartDate);
    System.out.println(scheduleStartDate);
    long offset = timeScale.getOffset((net.project.schedule.Schedule)(session.getAttribute("newSchedule")));
%>
<template:import type="css" src="/styles/gantt.css" />
<template:import type="javascript" src="/src/gantt/ganttchart.js"/>
<template:import type="javascript" src="/src/gantt/utils.js"/>
<template:import type="javascript" src="/src/gantt/tasklist.js"/>
<template:import type="javascript" src="/src/gantt/column.js"/>
<template:import type="javascript" src="/src/gantt/task.js"/>
<template:import type="javascript" src="/src/gantt/path.js"/>
<script src="<%=SessionManager.getJSPRootURL()+"/servlet/HeaderServlet/"+timeScaleID+".js?pixelsPerDay="+timeScale.getPixelsPerDay()%>"></script>


<script language="JavaScript">

var ie = (document.all);
var n6 = (window.sidebar);
var ff = (navigator.userAgent.toLowerCase()).indexOf("firefox") != -1;
var RANGE_LIMIT = 190;// each task takes 19 scroll value, so for 50 task (19* 50) 950 scroll.
var startPoint = 0; 
var endPoint = 0;

function scrolled() {
    var taskFrame = parent.taskFrame;

    if (n6) {
        taskFrame.scrollTo(taskFrame.pageXOffset, window.pageYOffset);
    } else {
        taskFrame.document.body.scrollTop = document.body.scrollTop;
    }
    //In case of firefox we need to render task dependency on scroll.
    if(ff){
	    renderDependency(document.body.scrollTop);
}
}
//Renders dependency as per scroll position.
function renderDependency(scroll){
	//First check if scroll position is in the rendered range.
	if(startPoint < scroll && endPoint > scroll){
		return;
	}
	//initialize the start and end range which is rendered.
	startPoint = scroll;
	endPoint = scroll + (RANGE_LIMIT);
	//Now set get task no by the position
	//19 scroll value takes for one task, so by the start/end point we can get the task numbers for which dependency is to renders
	var _sPoint = startPoint/19;
	gantt.setStartPosition(_sPoint.toFixed(0));
	var _ePoint = endPoint/19;
	gantt.setEndPosition(_ePoint.toFixed(0));
	//Now set dependency list and draw it.
	gantt.setLinkPaths(window.parent.taskFrame.getLinkPaths());
	gantt.drawLinkPaths();
}

window.onscroll = scrolled;
</script>
</head>

<body>
<script language="javascript">
var gantt = new GanttChart();
gantt.setTaskList(window.parent.taskFrame.getData());
var dependencyCount = window.parent.taskFrame.getLinkPaths().length;
gantt.setLinkPaths(window.parent.taskFrame.getLinkPaths());
gantt.setViewLevel(<%=timeScaleID%>);
gantt.setTaskStartOffset(<%=offset%>);
gantt.setJSPRootURL('<%=SessionManager.getJSPRootURL()%>');
gantt.setToday(<%=(scheduleStartDate == null ? 0L : DateUtils.daysBetweenGantt(cal, scheduleStartDate, cal.startOfDay(new Date())))%>);
//gantt.showProgressLines(<%=bShowProgressLines%>);//sjmittal: since this line is commented above line has no meaning!!
gantt.showProgressLines(false);
gantt.display();
//If browser is firefox and dependency count is more than 500 render it on scrlling list.
if(ff && dependencyCount > 500){
	renderDependency(0);
}else {
//Else render all with page loading.
	gantt.setStartPosition(0);
	gantt.setEndPosition(dependencyCount);
	gantt.drawLinkPaths();
}
	
gantt.scrollIfNecessary();

//Don't worry if we can't contact the top bar the first time -- it has a
//reasonable default.  This might be necessary because of frames.  It might not
//have been loaded yet.
if (parent.toolbar && parent.toolbar.zoomComplete) {
    parent.toolbar.zoomComplete(<%=timeScaleID%>);
}
</script>
</body>
</html>
