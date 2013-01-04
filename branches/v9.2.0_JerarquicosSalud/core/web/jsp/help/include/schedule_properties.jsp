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

<%@page import="net.project.security.SessionManager"%>
<a name="schedule_main"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Schedule Properties</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>Schedule Properties allows you to alter properties that pertain to the 
schedule as a whole, as opposed to properties that pertain only to a single 
task.

<a name="schedule_propeties"></a>
<h3>Schedule Properties Tab</h3>

<br><b>Schedule Name</b><br>
This is the name that is used to refer to this schedule.  Users will see this 
name if they attempt to use the sharing tool from another space.

<br><b>Schedule Start Date</b><br>
<p>The Schedule Start Date is the date that all tasks will start on if they have 
not been given a specific start date or if their start date is not dependent on
other tasks.

<p>The start date can be set in one of two ways.  Either a specific start date 
can be specified, or the start date can be dependent on the completion of tasks
or schedules in other workspaces.  Click on the "Create" icon in the dependent
item list to add a new item that the schedule start date is dependent upon.

<br><b>Schedule End Date</b><br>
<p>The schedule end date is calculated from a combination of the schedule start 
date and the start and end dates of all tasks and milestones in the project.  

<p>If you have altered the start date and have not yet submitted the page, the 
end date is not correct.  Click on the "Recalculate" button to update the end
date.

<br><b>Default Time Zone</b><br>
<p>When tasks are created, a start date and end date is automatically calculated 
for them based on the schedule's best guess of when work will start and end for
the task.

<p>If no users are yet assigned, the schedule calculation engine will assume that
a single worker will be working in the time zone listed here.  This will provide
a default start and end date.

<p>Note that users will see the time that tasks will be accomplished according 
to their own time zone.  For example, if someone in the Pacific Time Zone in the 
US is scheduled to start a task at 8am Pacific Time, someone in the Eastern Time
Zone of the US would see that task as starting at 11am.  

<b>Automatically Calculate End Dates for Tasks Using Dependencies</b><br>
<p>This option allows the end date of the schedule to be determined by 
calculating the dependencies of each task.  A task will not start unless its 
dependencies (also known as predecessor tasks) are completed.  

<p>When this option is turned on, it is not necessary to enter start or end 
dates for most tasks.  Just create dependency links that reflect that a task 
needs to start after a previous one is completed.  The start and end dates will
be calculated automatically.

<a name="working_times"></a>

<a name="task_list_decoration"></a>
<h3>Task List Decoration Tab</h3>
<p>This tab allows you to call out individual tasks on the task list so it is
easier to see tasks of interest.  Tasks can be called out by changing their 
background color, or by selecting an icon that will appear next to them in the
task list.

<a name="sharing"></a>
<h3>Sharing Tab</h3>
<p>The Sharing tab allows the schedule to become available for other workspaces
to include in their schedule.