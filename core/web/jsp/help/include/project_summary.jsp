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
<a name="project_summary"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Project Summary</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
<p><br>The Project Summary page is the main page of the Project space.
This summary page provides a collection of information channels for the
project. Included are a complete listing of the status and percentage of
all current projects. Other information on the page is:
<ul>
<li>
Teammates online -- Who from the project roster has recently or is currently
logged into Project.net.</li>

<li>
Documents modified within the past 7 days</li>

<li>
Documents within the project space that have been added or versioned in
the past 7 days.</li>

<li>
This week's meetings -- Project meetings scheduled for this week that you
are invited to.</li>

<li>
Site weather</li>
</ul>
