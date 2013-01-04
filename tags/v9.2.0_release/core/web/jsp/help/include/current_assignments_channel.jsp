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
	<td nowrap class="channelHeader"><nobr>Show Current Tasks Only</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>

The "Assignments - In Progress, Late, or Starting within 2 days" channel is a list
of time-sensitive assignments that you are currently assigned to.  The assignments 
shown will match the following criteria:

<ul>
<li>They will have already stated</li>
<li>They should have already started (that is, their start date is in the past.)</li>
<li>They should be starting within in the next 2 days</li>
</ul>
The assignment list colors tasks red if their due date is in the past.  Late tasks
can be removed from this list by updating your assignment completion and completing
all required work.<br>
<br>
To update your completion of these tasks, click on the "modify" button.  This will 
bring up the "Update Assignment" page, which is a timesheet.<br>
<br>&nbsp;


