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
<a name="project_portfolio"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Project Listing Page</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
Project Space lists all the currently active projects in which you are
a participant or, if you are a manager, displays all projects. As with the preferences
on all parts of the Project.net site, you can control what information gets
displayed. 
<p>When you have selected a project by clicking on the radio button, and
you click Projects again, you are taken to that project's home page. This
home page is identified by the Business (if there is one) and the Project
name. There are three tabs on this page and the left navigation bar. The
meanings of each are explained below.
<p>The three tabs on the Project Home page indicate teammates on line,
documents modified with the past 7 days, and upcoming meetings.
<p>The left navigation bar for the project space includes a suite of tools
for setting up and viewing project progress, planning and scheduling, and
the creation of tasks and milestones where explicit personnel assignments
and key project dates are established. Help for these items is available
for each tool.
<p>Tasks covered on this page are:
<ul>
<li>
<a href="#create_project_project">Create Project (Project Space)</a></li>

<li>
<a href="#create_project_business">Create project (Business Space)</a></li>

<li>
<a href="#create_process">Create Process</a></li>

<li>
<a href="#create_process_phase">Create a Process Phase</a></li>

<li>
<a href="#create_process_gate">Create a Process Gate</a></li>

<li>
<a href="#create_process_deliverable">Create a Deliverable</a></li>

<li>
<a href="#add_link">Add a Link</a></li>

<li>
<a href="#add_person_to_project">Add a Participant</a></li>

<li>
<a href="#add_group_to_project">Add a Group</a></li>
</ul>

<h3>
<a name="create_project_project"></a>To Create a Project (in a Project
space)</h3>

<ol>
<li>
In the top left corner of the Project.net frame, click on Projects.</li>

<li>
Click on Create Project. You can use either the Project.net tool bar or the channel bar.</li>

<li>
Enter your MicroStation Product serial number (available from Help/About)
OR an Authorization code.</li>

<li>
Click Next.</li>

<li>
Key in Project Name, Project Status, and optional information (if desired).
Click Next to continue.</li>

<li>
Project information appears exactly as entered so you can verify the data.
Go Back to edit or click finish to continue.</li>

<br>The project appears on the Project Space lists and the dates appear
on all Project member's calendars.</ol>

<h3>
<a name="create_project_business"></a>To Create a Project (in a Business)</h3>

<ol>
<li>
In the Business Space, click on Projects.</li>

<li>
Click on Create Project. You can use either the Project.net tool bar or the channel bar.</li>

<li>
Enter your MicroStation Product serial number (available from Help/About)
OR an Authorization code.</li>

<li>
Click Next.</li>

<li>
Key in Project Name, Project Status, and optional information (if desired).
Click Next to continue.</li>

<li>
Project information appears exactly as entered so you can verify the data.
Go Back to edit or click finish to continue.</li>

<li>
The project appears on the Project Space lists and the dates appear on
all Project member's calendars.</li>
</ol>
Click here to see how to <a href="Help.jsp?page=discussion_main&section=create">Create
a Discussion post.</a>
<br>&nbsp;
<h3>
<a name="create_process"></a>To Create a Process, first time for a project</h3>

<ol>
<li>
In the Project Space, Click on Process.</li>

<li>
The Create Process GUI opens for the selected project.</li>

<li>
Key in a process Name and description.</li>

<li>
Click Submit and the Process is added to the list, along with the Phases.</li>
</ol>

<h3>
<a name="create_process_phase"></a>To Create a Process Phase</h3>

<ol>
<li>
In the Phases tab area of the Projects page, click Create Phase.</li>

<li>
The Create Phase GUI opens.</li>

<li>
Key in phase Name, phase description, Status, Start date, End Date, Sequence
(What order this Phase should be completed in), and an initial percentage
complete.</li>

<li>
Click Submit and the Phase appears in the Project space.</li>

<li>
Changes in the Phase can be made by clicking on the Modify icon.</li>
</ol>
Once a Phase has been created, you can create Gates within this Phase.
Gates are the key acceptance criteria with respect to required Phase completion
dates.
<h3>
<a name="create_process_gate"></a>To Create a Process Gate</h3>

<ol>
<li>
In the Process Phase tab, click on the Phase where the Gate will be added.</li>

<li>
This GUI has a tab for Phase Info and a tab for Deliverables. On this page,
click Add Gate (in the lower right corner of the page).</li>

<li>
Key in a Gate Name, description, status (choices are: Not Scheduled, Scheduled,
Passed, Rescheduled, Stopped, or Waived.)</li>

<li>
Click Submit and the Gate is now displayed on a tab below Phase Info.</li>

<li>
Changes in the Gate can be made by clicking on the Modify icon</li>
</ol>
A deliverable is the next step in the process. Possible deliverables include
architectural renderings, site plans, structural plans, plumbing, HVAC,
and all the other virtual or physical documents associated with the construction
process.
<h3>
<a name="create_process_deliverable"></a>To Create a Deliverable</h3>

<ol>
<li>
On the Projects Page, click on the Create symbol in the Deliverables tab
on the Project Phase page.</li>

<li>
Key in data for Name, description, Comments, Status, and whether the deliverable
is optional.</li>

<li>
When you click Submit, the Deliverable now appears on the Projects page.</li>

<li>
Changes in the Deliverable can be made by clicking on the Modify icon.</li>
</ol>
As many deliverables contain graphic information and are associated with
files, you can add a Link to a deliverable. This link takes you directly
to a document, greatly reducing any time spent searching and ensures you
only access the correct document.
<h3>
<a name="add_link"></a>To add a link</h3>
<ol>
<li>
Click on the Deliverable.</li>

<li>
In the Links Channel bar, click Modify.</li>

<li>
Click the Add New Link button.</li>

<li>
Select the Link Type from the options Document, Deliverable, Post, Task,
or Event.</li>

<li>
Browse or Search for the Link</li>

<li>
Click Add Link.</li>

<li>
Click Finish.</li>

<li>
The link appears in the selected context.</li>
</ol>

<h3>
<a name="add_person_to_project"></a>To add a Person to a Project</h3>

<ol>
<li>
From Projects > Directory > People, click the Invite Participant
button and the Invite Participant Wizard opens.</li>

<li>
Once the individual is selected and their information is added, an email
is sent, asking if that person wants to accept the invitation to a project.</li>

<li>
When an acceptance is received at the project site, that project (or the
new member) is added to the site.</li>
</ol>

<h3>
<a name="add_group_to_project"></a>To add a new Group</h3>

<ol>
<li>
In the Groups tabs, click the Create button.</li>

<li>
Key in the necessary information to define the Group.</li>

<br>After you click Submit, this Group name now appears in the group list.</ol>
