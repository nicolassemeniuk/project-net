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
|   $Revision: 19971 $
|       $Date: 2009-09-15 13:10:38 -0300 (mar, 15 sep 2009) $
|     $Author: nilesh $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="html page to hold the 2 gantt frames (tasks and image)"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
            net.project.schedule.ScheduleEntry,
            net.project.util.DateFormat,
            net.project.util.DateUtils,
            net.project.util.HTMLUtils,
            net.project.calendar.PnCalendar,
            org.apache.log4j.Logger,
            net.project.schedule.gantt.TimeScale,
            net.project.schedule.TaskType,
            net.project.schedule.TaskList,
            net.project.schedule.Schedule,
            net.project.util.Validator,
            net.project.schedule.gantt.pathinfo.LinkLinePathCalculator,
            net.project.gui.history.History,
            java.util.*"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session"/>
<html> <head>
<title></title>

<script language="JavaScript">
var ie = (document.all);
var n6 = (window.sidebar);

function scrolled() {
    var ganttFrame = parent.ganttFrame;

    if (n6) {
        ganttFrame.scrollTo(ganttFrame.pageXOffset, window.pageYOffset);
    } else {
        ganttFrame.document.body.scrollTop = document.body.scrollTop;
    }
}

window.onscroll = scrolled;
</script>

<script language="javascript">
var view = 1;

function zoomIn() {
}

function zoomOut() {
}

function getData() {
    var taskList = [<%DateFormat df = DateFormat.getInstance();
	    TaskList taskLista = new TaskList();
	    Schedule newSchedule = (Schedule)schedule.clone();

	    List tasks = schedule.getEntries();        
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        String allTasks = "";
		String expand = "";
		String colapse = "";
		String expandParameter = "";
		String colapseParameter = "";
		Date scheduleStartDate = null;
        if (!schedule.getEntries().isEmpty()) {
            scheduleStartDate = schedule.getScheduleStartDate();
            if (schedule.getEarliestTaskStartTimeMS() != -1) {
                scheduleStartDate = DateUtils.min(scheduleStartDate, new Date(schedule.getEarliestTaskStartTimeMS()));
            }
            scheduleStartDate = cal.startOfDay(scheduleStartDate);
            expand = request.getParameter("expand") == null ? "" : request.getParameter("expand");
			colapse = request.getParameter("colapse") == null ? "" : request.getParameter("colapse");
			expandParameter = request.getParameter("expand") == null ? "" : request.getParameter("expand");
			colapseParameter = request.getParameter("colapse") == null ? "" : request.getParameter("colapse");
			
			String taskId = request.getParameter("taskId") == null ? "" : request.getParameter("taskId");
			boolean is = false;
			boolean next = true;
			// taskCounter is plain counter
			int taskCounter = 0, i = 0;
			// previos levels collapsed in the hierarchy
			List levelsCollapsed = new ArrayList();
			
			List colapsedList = new ArrayList();
			StringTokenizer st = new StringTokenizer(colapse, ",");
			while(st.hasMoreTokens()) {
			    colapsedList.add(0,st.nextToken());
			}

			String[] colapsed = new String[colapsedList.size()];
			for(int ii = 0 ; ii < colapsedList.size() ; ii++) {
				colapsed[ii] = (String) colapsedList.get(ii);
			}
			

			Iterator it = tasks.iterator();
			boolean embeddedExpand = false;
			int sequenceNumber = 1;
            while (it.hasNext()) {
                ScheduleEntry scheduleEntry = (ScheduleEntry) it.next();
                java.util.Date startDate = scheduleEntry.getStartTime();
                java.util.Date endDate = scheduleEntry.getEndTime();

                taskCounter++;
                
				if (is) {
					boolean flag = false;
					for(int jj = 0 ; jj < levelsCollapsed.size() ; jj ++) {
						 Integer level = Integer.valueOf(scheduleEntry.getHierarchyLevel());
						 Integer previousLevel = (Integer) levelsCollapsed.get(jj);
						 if(level.compareTo(previousLevel) > 0) {
							flag = true;
						    break;
						 }
					}
					if(flag) {
						next = false;
						embeddedExpand = true;
					}
					else {
						//remove the levels as we need to start fresh for a new hierarchy
						levelsCollapsed.clear();
						next = true;
						is = false;
						embeddedExpand = false;
					}
				} else {			 				    
				    next = true;
				    is = false;
				    embeddedExpand = false;
				}
                	
				
                // here we check is the current taskId in colpase hidden parameter, if it is than that node is colapsed
                // and we must hide all subtasks
    			if (colapsed != null ){
    			    for (int k = 0; k < colapsed.length; k++  ){
						if (scheduleEntry.getID().equals(colapsed[k])){
						    is = true;
						    // here we set counter on next task, because next task we don't want to show
						    //i=taskCounter+1;
						    if (!embeddedExpand){
						        next = true;
						    }						    
						    levelsCollapsed.add(Integer.valueOf(scheduleEntry.getHierarchyLevel()));
						    break;
						}
    			    }
    			}				
				
                // here is the part which prints tasks which are shown in the Gantt chart.
                if ( next ){
                    taskLista.add(scheduleEntry);
                    scheduleEntry.setSequenceNumber(sequenceNumber);
                    sequenceNumber++;
					String taskLine =
						" new Task(" +
						"\"" + scheduleEntry.getID() + "\", " +
						"'" + HTMLUtils.jsEscape(scheduleEntry.getName()) + "', " +
						scheduleEntry.getPercentComplete() + ", " +
						"\"" + scheduleEntry.getDurationTQ().toString() + "\", " +
						"\"" + df.formatDate(scheduleEntry.getStartTime()) + "\", " +
						"\"" + df.formatDate(scheduleEntry.getEndTime()) + "\", " +
						"\"" + scheduleEntry.getPredecessors().getSeqCSVString() + "\", " +
						"\"" + scheduleEntry.getAssigneeString() + "\", " +
						(DateUtils.daysBetweenGantt(cal, scheduleStartDate, startDate)) + ", "+
						(DateUtils.daysBetweenGantt(cal, endDate, startDate)) + ", "+
						(scheduleEntry.getTaskType().equals(TaskType.SUMMARY) ? "true" : "false") + ", " +
						(scheduleEntry.isMilestone() ? "true" : "false") + ", " +
						scheduleEntry.getHierarchyLevel() + 
						", \"" + (is ? "true" : "false" ) + "\"" +
						((it.hasNext()  ) ? "), " : "), ");
					out.println(taskLine);
					allTasks += taskLine + "\n";			
				}               
            }
        }
        Logger.getLogger("Gantt.jsp").debug(allTasks);%> null];
    return taskList;
}

function getLinkPaths() {
<%
	Map clonedEntryMap = new LinkedHashMap();
	for (Iterator it = taskLista.iterator(); it.hasNext();) {
	    ScheduleEntry scheduleEntry2 = (ScheduleEntry)it.next();
	    ScheduleEntry clonedEntry = (ScheduleEntry)scheduleEntry2.clone();
	    clonedEntryMap.put(clonedEntry.getID(), clonedEntry);
	}
	newSchedule.setEntryMap(clonedEntryMap);

	String pathInfo = "";
    if (!newSchedule.getEntries().isEmpty()) {
        LinkLinePathCalculator pathCalc = new LinkLinePathCalculator();
        pathCalc.calculateLinkLines(newSchedule);
        pathInfo = pathCalc.toJavaScript();
        Logger.getLogger("Gantt.jsp").debug(pathInfo);
    }
    session.setAttribute("newSchedule", newSchedule);
%>
    var linkPaths = [<%=pathInfo%>];
    return linkPaths;
}
</script>

<template:import type="css" src="/styles/gantt.css" />
<template:import type="javascript" src="/src/gantt/tasklist.js"/>
<template:import type="javascript" src="/src/gantt/column.js"/>
<template:import type="javascript" src="/src/gantt/task.js"/>
<template:import type="javascript" src="/src/gantt/path.js"/>


</head>
<body onLoad="parent.load_next(3)">

<script language="javascript">
var infoColumn = new Column("", "50px", true, "center");
infoColumn.setImage("<%=SessionManager.getJSPRootURL()%>/images/info.gif", 15, 15);
var columnList = [
    new Column(undefined, "35px", true),
    infoColumn,
    new Column('<display:get name="prm.schedule.ganttview.taskname.column.label" />', "100px", true),
    new Column('<display:get name="prm.schedule.ganttview.duration.column.label" />', "50px", true),
    new Column('<display:get name="prm.schedule.ganttview.start.column.label" />', "50px", true),
    new Column('<display:get name="prm.schedule.ganttview.finish.column.label" />', "50px", true),
    new Column('<display:get name="prm.schedule.ganttview.predecessors.column.label" />', "50px", true),
    new Column('<display:get name="prm.schedule.ganttview.resourcenames.column.label" />', "50px", true)
];

var taskListTable = new TaskListTable();
taskListTable.setColumns(columnList);
taskListTable.setTaskList(getData());
taskListTable.display();
window.parent.ganttFrame.location.reload();
</script>

<form name="t" action="" method="post" id="t">
 <input type="hidden" name="taskId" value=""  />
 <input type="hidden" name="expand" value="<%=expandParameter%>" />
 <input type="hidden" name="colapse" value="<%=colapseParameter%>" />
</form>


</body>
</html>
