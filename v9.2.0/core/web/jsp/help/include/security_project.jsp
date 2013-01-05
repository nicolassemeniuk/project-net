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
<a name="security_project"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Security</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
Security is implicit on the Project.net site. All site access is controlled
on several levels. SSL (Secure Socket Layer) ensures that all information
including passwords and other identifying information as well as all document
transactions are encrypted. No plain text is ever transmitted. On the site
itself, initial access is by password and invitation, to ensure that only
authorized can parties enter the site. With the security on the site, access
can be controlled by Project, Group, and Object. Read, Write, and Modify
<a href="#permitted_actions">permissions</a> can also be assigned to Status
items, Schedules, and Meetings, ensuring that only those individuals who
should change project-related information, can.
<p>Clicking on the Lock Icon as either Project Owner or Site Administrator
gives you access to the full range of options in controlling security.
<p>At the highest level is the Space Administrator. This person has full
access all of the interfaces for controlling the flow of information.
<p><a name="security_states"></a>In the overall Security interface, as
Site Administrator, there are three states:
<ul>
<li>
Have permission -- The user has authorization to perform one or more of
the <a href="#permitted_actions">permitted actions</a>.</li>

<li>
Do not have permission -- The user does not have permission to perform
the requested action.</li>

<li>
Undefined -- Explicit permission has not been defined for the user for
this action. In this case, the Security assigned to that individual are
those of the group in which the user is a Team Member.</li>
</ul>

<h4>
Other Key points</h4>

<ul>
<li>
<a href="#precedence_rules">Precedence </a>-- Rules that help determine
how to set security.</li>

<li>
<a href="#permitted_actions">Permitted Actions </a>-- The effects of the
possible states on privileges.</li>

<li>
<a href="#business_security">Business Security</a> -- The effects of the
tools in the Business Space on the Project.net site.</li>

<li>
<a href="#project_security">Project Security</a> -- The effects of the
tools in the Project Space on the Project.net site.</li>

<li>
<a href="#personal_security">Personal Security</a> -- The effects of the
tools in the Personal Space on the Project.net site.</li>
</ul>

<h3>
<a name="precedence_rules"></a>Precedence</h3>
In establishing a security scheme, you must remember that the permissions
established for an individual take precedence over those established by
the Group or a Project. For example, if all group members were assigned
View only access and as an individual you had Modify Permission, you could
also Modify, Delete, or change the permissions of those members in your
group if you created a document.
<br>&nbsp;
<h3>
<a name="permitted_actions"></a>The Permitted Actions are:</h3>

<ul>
<li>
View – Read or otherwise examine the contents of a document.</li>

<li>
Modify – Change the contents of a document. Additionally, the document
must first be checked out.</li>

<li>
Create – Create a posting or add a document to this module. Users are typically
allowed to Create Folders or Projects.</li>

<li>
Delete – Remove a document.</li>

<li>
Modify Permission – Allows a user to define the permissions of others with
respect to a document. Conceivably, a user could make it impossible for
others to view a document.</li>

<li>
Permissions Active – This box must be checked to explicitly assign permissions.
If not checked, the security state is Undefined and whatever settings were
attached to the individual or the Group/Project are active.</li>
</ul>

<h2>
<a name="business_security"></a>Business Security</h2>
When you click on the Security icon in the tool bar or the Side Navigation
bar, there are two tabs: Module Permission and Default Object Permissions.
Module Permission controls the security on the Business Directory, Business
Space, and Security itself (so you can determine who has control over Security).
With Default Object Permissions, you can control the security for Deliverables,
Discussion Groups, Gates, Calendar Events, and the other objects appearing
on the site.
<p>Security can also be defined for Team Members or Individuals. When setting
Individual security, please keep in mind that rules in <a href="#precedence_rules">Precedence</a>
may make it possible for an individual other than the Site Administrator
to set the Security for objects created by a site member.
<h2>
<a name="project_security"></a>Project Security</h2>
This covers the tabs for Module Permission and Default Object Permissions.
As with Business Space, Module Permission controls the security on the
Business Directory, Business Space, and Security itself (so you can determine
who has control over Security).&nbsp; With Default Object Permissions,
you can control the security for Deliverables, Discussion Groups, Gates,
Calendar Events, and the other objects appearing on the site. Note that
the Project and Business security options are identical.
<h2>
<a name="personal_security"></a>Personal Security</h2>
You can apply security privileges to all items you have created. As a project
member, you will likely create Documents to fulfill your project requirements.
As the "owner" of these documents, you set the privileges for these objects.
As in Business security, there are tabs for People and Group, allowing
you quickly set privileges for the project.

