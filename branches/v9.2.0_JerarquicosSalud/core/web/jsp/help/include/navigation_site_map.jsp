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
<a name="site_map"></a>

<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Site Map</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>

<a href="Help.jsp?page=login_main" class="PageTitle"><nobr>Login Page</nobr></a>

<ul>
<li><a href="Help.jsp?page=login_main">Login help</a></li>
	<ul>
	<li> <a href="HelpDesk.jsp?page=registration">Registration </a></li>
	<li> <a href="HelpDesk.jsp?page=forgotten_password">Forgotten Password</a></li>
	</ul>

</ul>

<ul>

	<li> <a href="Help.jsp?page=navigation">Navigation</a></li>
		<ul>
		<li> Personal</li>
		<li> Project</li>
		<li> Business</li>
		<li> Toolbar</li>
		<li> Top Navigation</li>
		</ul>
</ul>

<a href="Help.jsp?page=personal_space" class="pageTitle"><nobr>Personal Space</nobr></a>
<ul>
<li> <a href="Help.jsp?page=personal_main">Main Personal Space page</a>  </li>
	<ul>
		<li> Customize Channel
	</ul>
<li> <a href="#calendar">Calendar Main Page</a>  (See expansion below)</li>
<li> <a href="#docs">Documents Main Page </a> (See expansion below)</li>
<li> <a href="Help.jsp?page=profile_personal">Profile</a> </li>
	<ul>
	<li> Profile Name </li>
	<li> Profile Address </li>
	<li> Profile Login</li>
	<li> Profile Business </li>
	</ul>
</ul>


 
<a href="Help.jsp?page=project_space" class="pageTitle"><nobr>Project Space</nobr></a>
<ul>
<li> <a href="Help.jsp?page=project_portfolio">Project Listing Page</a></li>
		<ul>
		<li> Create Project</li>
		</ul>
(Must select a project to get the rest of the option)
	<li>Project Home</li>
	<ul>
		<li><a href="Help.jsp?page=project_summary">Project Summary</a></li>
		<li><a href="Help.jsp?page=project_info">Project Info</a></li>
		<ul>
			<li>Project Info General</li>
			<li>Project Info Configure</li>
		</ul>
	</ul>

	<li> <a href="Help.jsp?page=directory_project">Directory</a></li>
		<ul>
		<li> People</li>
			<ul>
			<li> Create person</li>

			<li> View Person</li>

				<ul>
					<li>Modify Person</li>
				</ul>

			<li> Modify Person</li>
			</ul>
		<li> Org Charts</li>

		<li> Assignments</li>

		<li> Groups</li>
		</ul>

	<li> <a href="#docs">Documents Main Page</a> (See expansion below)</li>
	<li> <a href="#disc">Discussions Main Page</a>  (See expansion below)</li>
	<li> <a href="Help.jsp?page=process_main">Process Main page</a></li>
		<ul>
		<li> Create/Modify process</li>
		<li> Phase Main Page Help.jsp</li>
			<ul>
			<li> Create/Modify phase= Help.jsp</li>
				<ul>
				<li> Deliverable Main Page</li>
					<ul>
					<li>Deliverable Modify</li>
					<li><a href="#link">Link Manager</a> (See expansion below)
					</ul>
				<li> Deliverable Create/Modify</li>
				<li> Gate Create/Modify</li>
				</ul>
			</ul>
		</ul>
	<li><a href="Help.jsp?page=schedule"> Scheduling</a></li>
		<ul>
		<li><a href="Help.jsp?page=schedule_main">Schedule Main Page</a></li>
			<ul>
			<li> Import MPX</li>
				<ul>
				<li>Import Selection List</li>
				<li>Create Tasks</li>
				<li>Modify Tasks</li>
				</ul>
			</ul>
		
		<li> <a href="#calendar">Calendar</a> ( Expanded Below)</li>
		</ul>
	<li><a href="Help.jsp?page=planning_main">Planning</a> </li>
		<ul>
		<li> <a href="Help.jsp?page=budget_main">Budget</a></li>
		<li> Metrics</li>

		</ul>
	<li> <a href="Help.jsp?page=weather_main">Weather</a></li>
		<ul>
		<li>Weather Create/Modify</li>
		</ul>
	<li><a href="Help.jsp?page=security_project">Security</a></li>
		<ul>
		<li>Module Permissions</li>
		<li>Object Permissions</li>
		</ul>
	</ul>

</ul>


<a href="Help.jsp?page=business_space" class="pageTitle"><nobr>Business Space</nobr></a>
	<ul>
	<li><a href="Help.jsp?page=business_portfolio">Business Listing Page</a></li>
		<ul>
		<li> Create Business</li>
		</ul>
	(Must select a business to get the rest of the option)
	<li> <a href="Help.jsp?page= business_summary">Business Summary Page </a></li>
	<li> <a href="Help.jsp?page=directory_business">Directory</a></li>
		<ul>
		<li>Create Person</li>
		<li> Modify Person</li>	
		<li>View Person</li>
			<ul>
			<li> Modify Person</li>
			</ul>
		</ul>
	<li> <a href="Help.jsp?page=business_project_list">Projects</a> </li>
	<li> Security</li>
	</ul>



 <a name="docs"></a>
<a href="Help.jsp?page=document_main" class="pageTitle"><nobr>Documents</nobr></a>
	<ul>
	<li> <a href="Help.jsp?page=document_main">Document Main Page</a></li>
		<ul>
		<li>Document Create</li>
		<li>Document Properties</li>
		</ul>
	</ul>
<a name="disc"></a>
<a href="Help.jsp?page=discussion_main" class="pageTitle"><nobr>Discussion</nobr></a>
	<ul>
	<li> <a href="Help.jsp?page=discussion_main">Discussions Main Page</a></li>
		<ul>
		<li> <a href="Help.jsp?page=discussion_main&section=create">Create Page</a></li>
		<li> Discussion Group page</li>
		</ul>
	</ul>
	
<a name="calendar"></a>	
<a href="Help.jsp?page=calendar" class="pageTitle"><nobr>Calendar</nobr></a>
	<ul>
	<li> <a href="Help.jsp?page=calendar">Calendar Main Page</a> (this is the Day View)</li>
	<li> Calendar Week View</li>
	<li> Calendar Month View</li>
	<li> Calendar Year View</li>
	<li> Compose New Meeting</li>
		<ul>
		<li> Meeting View Attendee</li>
			<ul>
			<li> Create/Modify Attendee</li>
			</ul>
		<li>Meeting View Agenda</li>
			<ul>
			<li>Create/Modify Agenda</li>
			</ul>
		</ul>
	<li> Compose New Event</li>
	<li> Create New Task</li>
		<ul>
		<li> View Task History</li>
		<li> Change Assignments</li>
		</ul>
	<li> View Meeting</li>
		<ul>
		<li> Meeting View Attendee</li>
			<ul>
			<li> Create/Modify Attendee</li>
			</ul>
		<li>Meeting View Agenda</li>
			<ul>
			<li>Create/Modify Agenda</li>
			</ul>
		<li><a href="#link">Link Manager Main Page</a> (See expansion below)</li>
		</ul>
	<li> View Event</li>
	<li> View Task</li>
		<ul>
		<li>View Task History</li>
		</ul>

	</ul>
<a name="link"></a>
<a href="Help.jsp?page=link_main" class="pageTitle"><nobr>Link Manager</nobr></a>
<ul>
<li><a href="Help.jsp?page=link_main">Link Manager Main Page</a></li>
	<ul>
	<li> Add Link (Search)</li>
		<ul>
		<li> Link Selection List</li>
		</ul>
	</ul>	
</ul>	




