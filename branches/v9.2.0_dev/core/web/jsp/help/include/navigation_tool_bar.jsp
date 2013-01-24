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
<a name="tool_bar"></a>

<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Toolbar</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
This tool bar is present on most pages within the Project.net application.  
The tool bar's functions will vary slightly between the various user spaces, 
and only the applicable functions are live.
<p><a name="project_complete_tool_list"></a>The tools are:
<ul>
<li>
<a name="new_document"></a><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/create.gif" >
Create a new object. Depending on where the object is created, there will be a different fly-over.</li>

<li>
<a name="modify_document"></a><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/modify.gif" >
Modify an existing document (change information that appears on the page).
Pressing the OK key will submit the document and propagate the changes.</li>

<li>
<a name="remove_document"></a><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/remove.gif" >
Remove this document. Note that you must have appropriate levels of permission
to remove a document. One of the security features of the system is that
an electronic "paper trail" is created for all tasks on a project.</li>

<li>
<a name="reset_icon"></a><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/reset.gif" >
Clears all fields on the current active form and allows you start over.</li>

<li>
<a name="document_properties_icon"></a><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/properties.gif" >Lists
document information, such as size, creation date, author, project, and
all required information to allow you to sort and find documents by their
properties.</li>

<li>
<a name="link_icon"></a><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/link.gif" > Establish
a link to the current page for placement in an email or other posting at
another location</li>

<li>
<a name="search_icon"></a><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/search.gif" >
Search for a name, project, business, or keyword on this site.</li>

<li>
phone symbol -- Notify. Will send a message to the selected recipient to
review, process, or respond to the selected item.</li>

<li>
<a name="help_icon"></a><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/help.gif" > Brings
up an explanation of how to use a function or the contents or intent of
the area where you have selected help.(Context sensitivity). There is also
a searchable index of all available help and a table of contents.</li>

<li>
<a name="security_icon"></a><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/security.gif" >
Security. Shows your current level of access and/or the required security
to view, access, or review the selected document.</li>
</ul>
