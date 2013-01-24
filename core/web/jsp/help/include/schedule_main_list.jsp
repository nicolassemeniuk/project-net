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
<a name="schedule_main_list"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Schedule List</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
Schedule List shows all current tasks, milestones, and other metrics by
which projects are tracked. This is another view that allows you to quickly
track detailed project status. Included are estimates for the Work, Duration
of work, Start Date, End Date, and percentage completion. This data can
be modified by&nbsp; selecting the radio button and clicking the Modify
icon if you have the required <a href="#procedure_grant_team_permission">Modify
permissions</a>.
<p>Projects and tasks can be added with the Create button, which opens
the Project Creation wizard. With this wizard, you can also allocate personnel
resources.
<p>The project space can also be populated with project planning software
from vendors such as Microsoft and their Microsoft Project software and
Primavera with their P3e software. The importing of MPX files is covered
in <a href="schedule_main.html#import_schedule">Importing MPX files</a>.
<p>Procedures and notes in this document include:
<ul>
<li>
<a href="#create_task">Create a Task</a></li>

<li>
<a href="#assign_task">Assign a Task</a></li>

<li>
<a href="#privileges_update_task_status">Update Task Status</a></li>

<li>
<a href="#procedure_grant_team_permission">Change Permission</a></li>
</ul>

<h3>
<a name="create_task"></a>To Create a Task</h3>

<ol>
<li>
In Project Space > Scheduling > List, click Create.</li>

<li>
The New Task wizard opens.</li>

<li>
Fill in the Required fields for task Name, Description, and Priority.</li>

<li>
To track progress, you may also wish to fill in the data to indicate the
scope of the work (in Hours, days, or Weeks), and dates for planned start
and finish and actual dates for start and finish.</li>

<li>
Click Next when done entering data.</li>

<li>
The filled out form appears, allowing you make changes or Assign the task.</li>
</ol>
Once a task it is Created, it must be Assigned to get done. Use the following
procedure to Assign a Task.
<h3>
<a name="assign_task"></a>To Assign a Task to a Project Member</h3>

<ol>
<li>
In Projects > Schedule > List > Edit Task, click Change Assignments. (This
page automatically appears when you have created a task, as shown above).</li>

<li>
In the tab, Assign People to a Task, click on None and a scrolling list
of project members appears. Select a name from this list.</li>

<li>
Add data such as % Assigned, Owner, Status, and Role.</li>

<li>
Click Submit when done.</li>
</ol>

<h4>
Note: Tasks can only be assigned to project members. See <a href="Help.jsp?page=directory_project">Create
Person</a> to add a project member.</h4>

<h3>
<a name="privileges_update_task_status"></a>Update Task Status</h3>
When a task is assigned to a Team Member, the Default Object permissions
do not allow the owner of a task to Modify that task. So, if you want the
Owner of a task to be able to modify the status to indicate progress on
that task, the Object Permissions have to be changed for that object.
<h3>
<a name="procedure_grant_team_permission"></a>To Grant Permission for team
members to Update Task Status</h3>

<ol>
<li>
On the Projects page, select the project on which you will allow team members
to update Task status.</li>

<li>
Click on Directory in the left navigation bar.</li>

<li>
Click the Groups tab.</li>

<li>
Click Security.</li>

<li>
Select the Person/Group. This list appears under the Module Permissions
tab.</li>

<li>
Select the Default Object Permissions tab.</li>

<li>
Click on the Modify column for Schedule task or Milestone.</li>

<li>
Click Apply to save the change in permissions.</li>
</ol>
Please allow at least two minutes for the changes to propagate through
the system.
<p>You would follow a similar procedure to change the privileges for other
objects such as Gates, Phases, Process, Calendar, or the other object types.

