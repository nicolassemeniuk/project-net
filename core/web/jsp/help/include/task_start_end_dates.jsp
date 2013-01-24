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
	<td nowrap class="channelHeader"><nobr>Schedule Times</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>

Setting the start date or end date of a schedule entry is not the best
way to schedule a task.<br>  
<br>
When the start date or end date of a task is set to a specific date,
the task is not free to move in response to changes in the tasks that
must be completed before this task begins.<br>
<br>
Some alternatives to setting a specific start date include:

<h4>Don't enter a start date and let it be calculated for you</h4>

By not entering a start date, the workplan will analyze all tasks that
come before this task in order to find the earliest possible start
date.  More information about automatic calculation can be here.<br>
<br>
It may be necessary to enter a date (as you have already done) if the
task is dependent on a certain date due to external purposes, for
example if a piece of machinery is required that is only available on
that certain day.

<h4>Set a deadline</h4>

When a deadline is set, the workplan will show an exclamation point
icon when the deadline has not been met.  This will allow you to know
when your intended end dates have not been met without sacrificing the
ability to maintain accuracy in the schedule.<br>

<h4>Typing a specific constraint date</h4>

This is effectively the same thing that you have already done.  By typing in a 
date, you tell the system that you need to start your task on or before that 
date.  The "Constraint Type" and "Constraint Date" fields may allow you to choose
options that better reflect what you intend.<br>
<br>
When editing a task, work and work complete fields appear.  The values for
these fields appear as "hours", "days", or "weeks".  Note that in this context,
a day refers to an 8-hour day and a week refers to a 40-hour week.<br>
<br>&nbsp;
