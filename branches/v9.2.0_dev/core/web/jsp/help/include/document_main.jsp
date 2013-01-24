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
<a name="document_main"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Documents</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>

The Project.net document vault supports uploading, downloading and version control of any document type.
Project.net does not provide any file viewer or editor tools.  
You will need the appropriate application on your computer for viewing and editing documents.

Once uploaded, every document falls under the Project.net version control system for check out/in.

<p>
Information and procedures detailed are:
<ul>
<li><a href="#document_tool_bar">Document Tool Bar</a></li>
<li><a href="#create_document_from_upload">Create a document to be uploaded</a></li>
<li><a href="#document_check_out">Check-out and Check-in a document</a></li>
<li><a href="#discussion_post">Discussion Post</a></li>
<li><a href="#document_properties_tabs_main">Document Properties tabs</a></li>
</ul>

<h2><a name="document_tool_bar"></a>Document Tool Bar</h2>
The document tool bar contains the tools to create and manipulate documents.
The privileges of others to view and manipulate these documents is controlled
by the security levels that you have set through that interface.
<p>The tools are:
<br>&nbsp;
<ul>
<li><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/checkout.gif" > Check Out -- Lock the document
(prevent other users from making changes) and open the document for review
and modification.</li>

<li><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/checkin.gif" > Check In -- Save all changes
and check the document back in to the revision control system.</li>

<li><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/view.gif" > View -- Open the document for
review with the appropriate tool. This is a read-only mode.</li>

<li><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/cancelcheckout.gif" > Undo Checkout -- Cancel
all changes and release the document from the Check Out process. Document
is left unchanged and no entries are made in the revision control system.</li>

<li><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/newfolder.gif" > New Folder -- Open a new
folder to help organize the documents in a project.</li>

<li><img SRC="<%=SessionManager.getJSPRootURL()%>/images/help/move.gif" > Move -- Place the document in
another location or folder on the site.</li>
</ul>

<h2>
<a name="create_document_from_upload"></a>To Create a document from an
upload</h2>

<ol>
<li>
Go to the Personal Space.</li>

<li>
Click on the Create Icon and the Document Create icon in the Project.net tool
bar. When you start to create a document, note that the Document tool bar
appears to the left of the Project.net tool bar.</li>

<li>
In the Document Create GUI, fill in all the required fields. To help track
the status of a project, you will assign a status for the document. These
options include: Not Started, In Process, Pending, and Complete.</li>

<li>
To complete the process, click the Submit button on the lower right.</li>

<li>
Once submitted, this Document appears in your Personal Space.</li>
</ol>
<h2><a name="document_check_out"></a>To Check Out (and Check In) a Document</h2>

<ol>
<li>
In the Document tab in either personal Space or Project Space, select Check
Out icon and then the document to be checked out.</li>

<li>
Fill the blanks on the Check Out form. The Estimated Return Date helps
in tracking the document.</li>

<li>
Click Check Out to finish the process.</li>

<li>
The checked out document opens in the appropriate tool to allow editing.</li>

<li>
Click Check In to return the document.</li>

<li>
Fill in the form indicating the changes made in the document.</li>

<li>
Click Submit and document is again free for Check Out and the version has
be increased by one.</li>
</ol>

<h2>
<a name="discussion_post"></a>Discussion Post</h2>
Each document, by default, has a Discussion area. In this discussion area,
you can create, and add a series of postings to create a near real-time
dialog on any document. To promote a free exchange of information, this
area is typically off-limits to project managers, so the discussion takes
place outside the "fishbowl". This capability is fully described in <a href="Help.jsp?page=discussion_main">Discussion
Post</a>.
<br>&nbsp;
<h2>
<a name="document_properties_tabs_main"></a>Document properties tabs</h2>
When you select a document and click Properties from the Project.net tool bar,
there are four Document properties tabs. They are:
<ul>
<li>
<a href="#document_properties_tab">Properties</a></li>

<li>
<a href="#document_version_tab">Versions</a></li>

<li>
<a href="#document_activity_log_tab">Activity log</a></li>

<li>
<a href="#document_discuss_tab">Discuss</a></li>
</ul>

<h3>
<a name="document_properties_tab"></a>Properties tab</h3>
This tab shows basic document properties and type and also lists the number
of times it has been viewed. It also shows whether or not the document
is currently checked out, and if so, who it is checked out by and what
the estimated return date is.
<h3>
<a name="document_version_tab"></a>Version tab</h3>
The version tab lists version numbers, file names, version dates, format,
and file size.
<h3>
<a name="document_activity_log_tab"></a>Activity Log tab</h3>
The activity log tab shows the Actions (Views, Check-out, Check-in), comment
for the action (where applicable), who performed the action, and when.
All this allows you accurately track the history of adocument and determine
who made changes, and when.
<h3>
<a name="document_discuss_tab"></a>Discuss tab</h3>
This tab provides a capsule histroy for the associated Discussion post
on this item. This appears the same as <a href="Help.jsp?page=discussion_main">Discussion
Post</a>.
<br>
