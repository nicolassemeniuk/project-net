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
<a name="process_main"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Process</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
The Process area in the Project Space is where you define the tasks and
deliverables that form the heart of the project. In this section you create
a <a href="#process_definition">Process</a>, Phases, Gates and Deliverables.
<br>&nbsp;
<h2>
<a name="process_definition"></a>Process</h2>
A process is a formalized methodology for task completion. These are the
steps you go through, the sequence of these steps, and what you should
have at the end of each step. The formalized methodology provides a convenient
way of tracking your progess that allows others to see where you are and
what you have left to do.
<br>&nbsp;
<h3>
<a name="process_create"></a>To Create a Process</h3>

<ol>
<li>
In the Project area, click on Process.</li>

<li>
Next, click on Create.</li>

<li>
Fill in the blanks that describe and define the Process.</li>

<li>
When you are done. Click Submit.</li>
</ol>

<h2>
<a name="phase_definition"></a>Phase</h2>
A Phase allows for additional granularity in defining a project. For example,
you can have Planning, Site Preparation, Steel Construction, and Interior
construction Phases for project.
<h3>
<a name="process_create"></a>To Create a Phase</h3>

<ol>
<li>
In the Phases tool bar, click on the Modify/Create icon.</li>

<li>
Fill in the blanks that describe and define the Phase. Note that you must
include status, percentage complete, and date information. This data allows
other Project.net users to quickly track your progress.</li>

<li>
When you are done. Click Submit.</li>
</ol>
<h3><a name="create_process_gate"></a>To Create a Process Gate</h3>

<ol>
<li>
In the Process Phase page, click on ADD GATE.</li>

<li>
Key in a Gate Name, description, status (choices are: Not Scheduled, Scheduled,
Passed, Rescheduled, Stopped, or Waived.)</li>

<li>
Click Submit and the Gate is now displayed.</li>

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
On the Projects Page, click on the Create symbol in the Deliverables area
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
<br>&nbsp;

<p>See <a href="Help.jsp?page=project_portfolio">Create Link</a>
