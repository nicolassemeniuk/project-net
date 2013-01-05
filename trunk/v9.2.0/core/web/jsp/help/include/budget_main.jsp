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
<a name="budget_main"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Budget</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
This key area of the project is for storing the dollar values and the cost
per item information. All items in this area are&nbsp; <a href="Help.jsp?page=document_main">Documents</a>.
Other restricted costing and personnel cost can also be stored here. With
the options in <a href="Help.jsp?page=security_project">Security</a>,
you can easily tailor the privileges of the individuals who need to see
this data. Similarly, you can restrict data to read-only.
<p>Another option with respect to restricting the viewing of data, especially
when competitive firms may be working on the same project, may be to create
separate "Us" and "Them" project groupings. By defining a separate project
grouping, you can more easily control access to proprietary information.
<h3>
<a name="upload_budget"></a>To Upload a Budget</h3>

<ol>
<li>
In the Projects workspace with Budget selected from the left navigation
bar, click on the Create icon in the Project.net tool bar.</li>

<li>
In the Document Create GUI, fill in all the required fields (Document Name,
File to Upload, Author (you by default), Status and Description.. To help
track the progress of a project, status options for a document include:
Not Started, In Process, Pending, and Complete.</li>

<li>
To complete the process, click the Submit button on the lower right.</li>

<li>
Once submitted, this Document appears in the Project Space.</li>
</ol>
Once the Dcoument has been uploaded, normal <a href="Help.jsp?page=document_main">Check-out</a>
and Check-in procedures for Documents work.