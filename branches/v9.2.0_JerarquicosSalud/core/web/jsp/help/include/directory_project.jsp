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
<a name="directory_project"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Directory</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p><br>This page allows you to view People (the complete roster for a Project
or Business), Organization Charts, Assignments, and Groups.
<br>&nbsp;
<h2>
<a name="people_tab"></a>People Tab</h2>
In the People tab, you can view rosters to quickly obtain names and contact
information. Lists of participants can be filtered by Project with the
Invited Users flag. You can also sort the columns (Name, Responsibilities,
Office Phone, and email). You can also invite Team members to join a Project.
<p>Perhaps most important is the Create Person tool. With this tool, you
add and invite Team Members to a Project or Business.
<p>Procedures and topics covered on this page are:
<ul>
<li>
<a href="#invite_new_member">Add a Team Member with Create Person</a></li>

<li>
<a href="#remove_member">Remove a Team Member</a></li>

<li>
<a href="#org_charts_tab">Org Charts tab</a></li>

<li>
<a href="#assignments_tab">Assignments tab</a></li>

<li>
<a href="#groups_tab">Groups tab</a></li>

<li>
<a href="#create_goup">Create Group</a></li>

<li>
<a href="#sending_mail">Send mail</a></li>

<li>
<a href="#change_security_by_group">Change Security by Group</a></li>
</ul>

<h3>
<a name="invite_new_member"></a>To invite a New Member (Create Person)</h3>

<ol>
<li>
In the Project Space, Click on Directory on the left navigation bar.</li>

<li>
In the People tab, click Create Person to start adding a new team member.</li>

<li>
Key in the Required and optional information.</li>

<li>
Click next.</li>

<li>
Fill in the information on the person's responsibilities and add a brief
message explaining that person's role.</li>

<li>
To add more members, click Add Another.</li>

<li>
When done adding members, click Finish.</li>
</ol>

<h3>
<a name="remove_member"></a>To Remove a Member</h3>
Changes in staffing are inevitable. To remove a member who is no longer
with the firm or is in another department or on another project, use the
following procedure. You can also remove member who may not be able to
respond because you have not keyed in a valid email address.
<ol>
<li>
To Remove a member, go to the People Tab in Projects > Directory.</li>

<li>
Click the radio button next to that name to select that person.</li>

<li>
Click Remove from the Project.net tool bar.</li>
</ol>

<h2>
<a name="org_charts_tab"></a>Org Charts Tab</h2>
In Organization Chart, you can manipulate the organization's charts like
any other <a href="Help.jsp?page=document_main">Document</a> on
the site. These charts are under full version control, with check-out,
check-in, and viewing capability.
<br>&nbsp;
<h2>
<a name="assignments_tab"></a>Assignments Tab</h2>
The Assignments tab lists assignments of all team members on the project.
This pages gives you the current status at a glance.
<br>&nbsp;
<h2>
<a name="groups_tab"></a>Groups Tab</h2>
This tab displays the Team Members and the Space Administrator. Permitted
actions include creating a new group, sending mail, and changing Security.
Procedures for these tasks follow.
<h3>
<a name="create_goup"></a>Create Group</h3>
Groups are a convenient way of structuring large (or even small) projects.
You can use the Group to organize by the tasks at hand or to control the
access to proprietary information. Security can be easily controlled by
changing the security properties for that Group.
<ol>
<li>
To create a New Group, go to the Groups tab in Project > Directory.</li>

<li>
Click Create Group.</li>

<li>
Key in the Group name (Required).</li>

<li>
Key in a Description if you wish.</li>

<li>
Click Submit to finish the process.</li>
</ol>

<h3>
<a name="sending_mail"></a>Sending Mail</h3>
Mail can be sent to individuals in a group or to all members who have accepted.
<ol>
<li>
In the Project > Directory > Groups tab, click Send Mail.</li>

<li>
Your default email program opens, with the To field populated by the current
members of that group (or the individual's) email address.</li>

<li>
Compose your message and click Send.</li>
</ol>

<h3>
<a name="change_security_by_group"></a>Change Security by Group</h3>

<ol>
<li>
To change the access rights for a Group from the defaults, go to the Projects
> Directory > Groups tab.</li>

<li>
Click the radio button next to the Group where you want to change the rights.</li>

<li>
From the Project.net tool bar, click the Security (Lock) icon.</li>

<li>
Assign the necessary privileges to prevent (or allow) access to Viewing,
Modifying, Removing, or permission to Modify. These changes can be applied
to individuals or the Group.</li>

<li>
Click Apply All Changes when done.</li>
</ol>

