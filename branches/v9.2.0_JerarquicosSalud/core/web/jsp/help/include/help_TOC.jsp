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

<%@ page
    contentType="text/html; charset=UTF-8"
    info="include.help_TOC.jsp"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
			net.project.base.property.PropertyProvider"
%>

<SCRIPT LANGUAGE="JavaScript">
	function pingApp() {
 		var pinglocation="<%=SessionManager.getJSPRootURL()%>/support/LaunchAppletGetCookies.jsp";
 		openwin_pingapplet(pinglocation);
   }
</SCRIPT>

<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/window_functions.js"></script>

<a name="help_toc_document"></a><span class="pagetitle">Help Desk Table of Contents</span>

<p>
Online help is available for the following areas of the Project.net application.
For more detailed help, please see the Project.net User's Manual.
<br>&nbsp;<br>
<a href="HelpDesk.jsp?page=about_site" class="pageTitle">About This Site</a><br>
<%-- <a href="#support" class="pageTitle">Technical Support</a><br> --%>
<a href="HelpDesk.jsp?page=contact" class="pageTitle">Contact Us</a><br>
<a href="#general" class="pageTitle">General Information</a><br>
<a href="#navigation" class="pageTitle">Navigation</a><br>
<a href="#personal_space" class="pageTitle">Personal Workspace</a><br>
<a href="#project_space" class="pageTitle">Project Workspace</a><br>
<a href="#business_space" class="pageTitle">Business Workspace</a><br>
<br><br>


<a name="general"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><a href="Help.jsp?page=general" class="channelNoUnderLine"><nobr>General Information</nobr></a></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<UL>
<li><a href="HelpDesk.jsp?page=browser_requirements">Browser Requirements</a></li>
<li><a href="HelpDesk.jsp?page=registration">Registration</a></li>
<li><a href="HelpDesk.jsp?page=forgottenpwd">Forgotten Password or Login</a></li>
</UL>

<%--
<a name="support"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Technical Support</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
--%>

<a name="navigation"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><a href="Help.jsp?page=navigation" class="channelNoUnderLine"><nobr>Navigation</nobr></a></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>

<ul>
<li><a href="Help.jsp?page=navigation&section=tool_bar">Tool Bar</a></li>
<li><a href="Help.jsp?page=navigation&section=top">Top Navigation</a></li>
<li><a href="Help.jsp?page=navigation&section=personal">Personal Workspace Left Nav</a></li>
<li><a href="Help.jsp?page=navigation&section=project">Project Workspace Left Nav</a></li>
<li><a href="Help.jsp?page=navigation&section=business">Business Workspace Left Nav</a></li>
<li><a href="Help.jsp?page=navigation&section=site_map">Site Map</a></li>
</ul>

<a name="personal_space"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><a href="Help.jsp?page=personal_space" class="channelNoUnderLine"><nobr>Personal Workspace</nobr></a></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>

<ul>
<li><a href="Help.jsp?page=personal_main">Personal Workspace Main Page</a></li>
<li><a href="Help.jsp?page=calendar&section=personal">Calendar</li>
<li><a href="Help.jsp?page=directory_project&section=assignment">Assigments</li>
<li><a href="Help.jsp?page=document_main">Documents</li>
<li><a href="Help.jsp?page=profile_personal">Profile</li>
<li><a href="Help.jsp?page=channel_manager">Channel Manager</li>
</ul>


<a name="project_space"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><a href="Help.jsp?page=project_space" class="channelNoUnderLine"><nobr>Project Workspace</nobr></a></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>

<ul>
<li><a href="Help.jsp?page=project_portfolio">Project Portfolio Page</a></li>
<li><a href="Help.jsp?page=project_summary">Project Summary</a></li>
<li><a href="Help.jsp?page=project_info">Project Information</a></li>
<li><a href="Help.jsp?page=directory_project">Directory</a></li>
<li><a href="Help.jsp?page=document_main">Documents</a></li>
<li><a href="Help.jsp?page=discussion_main">Discussions</a></li>
<li><a href="Help.jsp?page=process_main">Process</a></li>
<li><a href="Help.jsp?page=schedule_main">Schedule</a></li>
<li><a href="Help.jsp?page=schedule_main&section=list">Schedule List</a></li>
<li><a href="Help.jsp?page=calendar">Calendar</a></li>
<li><a href="Help.jsp?page=security_project">Security</a></li>
</ul>


<a name="business_space"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><a href="Help.jsp?page=business_space" class="channelNoUnderLine"><nobr>Business Workspace</nobr></a></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<ul>
<li><a href="Help.jsp?page=business_portfolio">Business Listing Page</li>
<li><a href="Help.jsp?page=business_summary">Business Summary</li>
<li><a href="Help.jsp?page=directory_business">Directory</li>
<li><a href="Help.jsp?page=business_project_list">Projects Owned by a Business</li>
</ul>

