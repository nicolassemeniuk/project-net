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
<a name="calendar_personal"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Personal Calendar</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
Your Personal Calendar displays events, tasks, and meetings for all projects
of which you are a member. Because this information is shared through the
database, items that appear on your calendar also appear on the calendars
of all other project members. Similarly, shared events appear on all calendars.
While events, tasks, and meetings appear in your Personal Space, Tasks
and Meetings can only be created in the Project Space. Within the Personal
Space, you can create Events.
<h3>
<a name="compose_calendar_event"></a>To Compose a new Event</h3>

<ol>
<li>
In the Personal Space calendar, click Compose New.</li>

<li>
Key in the data, being sure to fill in the required fields.</li>

<li>
The Event now appears on your personal Calendar.</li>
</ol>
Events can also be created in <a href="Help.jsp?page=calendar">Project
Space</a>.