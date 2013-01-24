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
	<td nowrap class="channelHeader"><nobr>Schedule</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
This tool adds tasks and milestones to a project. These items can be added
directly or imported from an <a href="#import_schedule">Microsoft Project MPD File</a>.
Once an item has been Scheduled it appears automatically on the Project
pages. It will also appear on a project member's Personal pages if they
have accepted the task associated with schedule.
<p>The schedule main page, by default, shows all tasks and milestones in the 
Project.  The order that the information appears in can be sorted by clicking on
the column headers for the data.
<p>The schedule allows searching through the use of the normal toolbar button 
for searching.  In addition, a "Quick Filter" is available by clicking the "plus"
sign in the left of the dark grey box above the tasks.  This provides a variety
of filtering methods.
<br>&nbsp;
<h3>
<a name="view_schedule_item"></a>To view a Schedule item</h3>

<ul>
<li>
Just click on the name of the task in the task list and all pertinant 
information will be displayed.  From this "Task View" page, detailed information
is available regarding who is assigned to the task, as well as an audit trail of
changes that have been made to the task over time.</li>
</ul>

<h3>
<a name="update_schedule_item"></a>To update a Schedule item</h3>

<ol>
<li>Select the item you wish to update and click the Update icon from the 
Project.net tool bar.</li>
<li>Make any necessary changes to the task.</li>
<li>Click the "Submit" button</li>
</ol>

<h4>Schedule Times</h4>
When editing a task, work and work complete fields appear.  The values for
these fields appear as "hours", "days", or "weeks".  Note that in this context,
days refer to an 8-hour day and a week refers to a 40-hour week.
<h3>
<a name="import_schedule"></a>Import Schedule File</h3>

<ol>
<li>From the scheduling tool, click on Import MS Project Database</li>
<li>Click on the browse button to choose the file that you wish to import.  This
file must be a Microsoft MPD project file.  (These files can be created from
MPP by opening the file in Microsoft Project and resaving them with a file name 
that ends with .MPD)  Click the "Next" button to continue.</li>
<li>Choose a project name that appears in your MPD file.  Then choose the items 
you'd like to import.  Click the "Next" button to continue.</li>
<li>The next page contains a list of resources (persons or machinery) that 
appears in the MPD schedule you've selected.  For each person listed, choose 
someone in your current workspace that will be reponsible for this work.  If you
do not choose someone in the workspace responsible for the work, the tasks 
assigned to that resource will be unassigned when they are imported.  Click the
"Next" button to continue.</li>
<li>You will now be presented with the "Import Summary".  This screen gives a 
summary of all the items that are about to be imported.  If you are satisfied 
with what is listed, click on the "Import" button to import the items into 
Project.net.  For large schedules, be aware that the import process can take
some time.</li>
</ol>
