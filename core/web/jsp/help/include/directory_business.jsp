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
<a name="directory_business"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Business Directory</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
The Directory appears for the Business space appears differently from the
Projects directory. This page allows you to view <a href="#people_view">People</a>
(the complete roster for a Project or Business) and <a href="#groups_view">Groups</a>.
<br>&nbsp;
<h3>
<a name="people_view"></a>People view</h3>
In People, you can view rosters to quickly obtain names and contact information.
Lists of participants can be filtered by Project with the Invited Users
flag. You can also sort the columns (Name, Responsibilities, Office Phone,
and email).
<p>Clicking Create allows you add new team member(s).
<h3>
<a name="groups_view"></a>Groups view</h3>
In Groups you see the Group Name, number of people in that Group, a Group
Description and a single email address for that group.
<p>Clicking Create allows you add new Group(s).
