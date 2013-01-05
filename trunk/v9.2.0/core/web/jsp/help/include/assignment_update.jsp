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
<a name="assignments_update"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Assignments</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>

<h4>Instructions</h4>
Enter a number of hours worked for each task on days when work was completed.  
The hours on which work was completed will be calculated automatically.  They 
will be presumed to start at the beginning of the working day.  (Often, 8am.)  
If you wish to enter specific hours, please click on the clock icon next to the 
day you worked and enter specific times that were worked.<br> 
<br>
As hours are entered, the "% Complete" field will be populated to reflect the 
amount of work that has been completed.  If you believe that the percentage 
complete is incorrect, this might reflect that the amount of work required to 
complete the task is too much and may need to be reduced.  Changing the percentage
complete will perform this task automatically.<br>
<br>
For example: you have a task with 32 hours of work.  After entering 8 hours of 
work, you believe the task is 50% complete.  You change the "% complete" column
from 25% to 50%.  This is turn changes the amount of work on the task from 32 hours
to 16 hours.<br>
<br>
