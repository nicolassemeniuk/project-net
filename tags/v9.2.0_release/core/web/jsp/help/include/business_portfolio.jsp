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
<a name="business_portfolio"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Business Listing Page</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
Typically, the main Business is a collection of channels which indicate,
at a corporate level, all the current project activities and their status.
As such, you can match the site to the way you work, using business to
business cooperation for the entire project lifecycle. By managing the
deliverables and providing a central point for the diverse and complementary
teams across the project life cycle, you can ensure coordination of all
aspects of a project. For example, you can start a project with architects
in the initial planning stages, add contractors for site preparation, concrete,
structural, plumbing, HVAC, and electrical, establish a design approval
mechanism, and schedule meetings. With the tools in the Project.net site, the
meetings may be virtual with WebEx, take place in a conference room, or
take place on-line in a discussion group.
<p>The Business Space allows you to see both who is in your organization
and what they are working on. In addition to considering the people and
the projects separately, the Project.net Business Space will allow you to combine
the two. For example, you can view not only a list of all projects in your
firm, but also who in your firm is working on what project. And with the
project information you can also see time lines and status reports, allowing
you to project more realistic deadlines, and allocate resources appropriately.
<p>Business Space is not a static “read-only” tool. You can create projects
and then staff them from your firm's complete roster. As you can sort the
employees by their skill sets and departments, teams can quickly be created,
with just the right employees. And because you also have access to the
current project lists, you can avoid possible “double-booking” and assign
staff based on availability.
<p>Other controls within the Business Space will allow you to control who
has access to what information, especially on shared projects. While department
managers may need complete status information, co-workers could be restricted
viewing just those documents you determine are required. This access control
helps protect sensitive information by limiting who is allowed to view
or change information on the site. For example, on one project you may
be working with a rival firm. In this case, you only want them to see your
organization's status on those phases where there are joint activities.
You do not want a rival firm to be able to view proprietary financial and
staffing details. With the built-in <a href="Help.jsp?page=security_project">site
security</a>, you can control precisely what any use can see.
<p>In summary, the business space gives you control over your staff, their
projects, project timelines, and access to information. By combining this
information on a single site, operational efficiency is greatly increased,
resulting in a bigger bottom line.
<p>The Channels are:
<ul>
<li>
Business Summary – Lists the Businesses you are a member of. For each business,
you can also see the Business type, Projects and People. In this tab is
the <a href="#create_business">Create Business</a> wizard.</li>

<li>
Directory -- The two tabs are People (lists Business members by name)
and Groups (lists groups by name) and includes a Group email. You can
<a href="#add_team_member"> Invite Participants </a>or <a
href="#create_group">Create Groups</a>.</li>

<li>
Projects -- Lists all current projects. This tab includes Status, and Completion
Percentage. You can also <a href="#create_project">Create a Project</a>
in the selected Business.</li>

<li>
Security -- Allows you control access to Business data by setting Module
and Default Object permissions. See <a href="Help.jsp?page=security_project">Security</a>.</li>
</ul>

<h3>
<a name="create_business"></a>To Create a Business</h3>

<ol>
<li>
Click the Create Business icon on the lower right of the Business home
page.</li>

<li>
Fill in the blanks on the first page (Business Identity), making sure to
fill the required <b>bold</b> fields. If you wish, you can add a <a href="Help.jsp?page=project_info&section=logo">logo</a>
to the business.</li>

<li>
Click Next to proceed.</li>

<li>
Add in the physical contact information in the Business Address page.</li>

<li>
Click Finish when done.</li>

<li>
The new Business appears on the Summary page.</li>
</ol>

<h3>
<a name="add_team_member"></a>To add a Team Member</h3>

<ol>
<li>
In the People tab, click Invite Participant to start adding a new team member.</li>

<li>
Key in the Required and optional information.</li>

<li>
Click next.</li>

<li>
Fill in the information on the person's responsibilities and add a brief
message explaining that person's role.</li>

<li>
To add more Participants, click Add Another.</li>

<li>
When done adding Participants, click Finish.</li>
</ol>

<h3>
<a name="create_group"></a>To Create a Group</h3>

<ol>
<li>
Go to the Groups tab in Business > Directory.</li>

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
<a name="create_project"></a>To Create a Project in a Business</h3>

<ol>
<li>
In the Business Space, click on Projects.</li>

<li>
Click on Create Project.</li>

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
The project appears on the Business Space lists and the dates appear on
all Project member's calendars.</li>
</ol>