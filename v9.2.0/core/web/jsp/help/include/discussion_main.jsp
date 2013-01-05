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
<a name="discussion_main"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Discussions</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
<a name="discussion_description"></a>Discussion Groups displays group names,
total number of posts, number
<br>posts the current user has not read, and a group description.
<p>The bottom frame contains the post header (from, date created, subject)
and the post body.&nbsp; URLs in the post body are hyperlinked so these
links are directly accessible to the reader. The post list can be traversed
from the bottom frame by using the "Previous Post" and "Next Post" links.
<br>&nbsp;
<p>
<a name="discussion_views"></a><span class="pageTitle">Available Views</span><br>
There are three different views available for the posts listing within
a discussion group.
<ul>
<li>
Threaded (default) -- In this default view, posts are displayed in a threaded
list where each reply is indented under its parent. This is an interactive
threaded display where the user can collapse/expand threads by clicking
on the appropriate post icon. Icons containing a (+) can be expanded.&nbsp;
Icons containing a (-) can be collapsed.</li>

<li>
Flat view -- All posts are displayed in a single linear list.</li>

<li>
Unread first view -- All posts are displayed in a single linear list. Unread
posts precede read posts.</li>
</ul>
Clicking on a link in the discussion group listing checks to determine
if the user has permission to view the discussion group.&nbsp; If a user
does have the appropriate permission, a split frame is presented. The top
frame contains a listing of posts in the group and the bottom frame contains
the current post.
<br>&nbsp;
<p>
<a name="post_listing_columns"></a><span class="pageTitle">Post Listing Columns</span><br>
The post listing columns are:
<ul>Subject -- Lists what the post covers.&nbsp; Hovering the mouse pointer
over the post subject results in a tip box containing the full subject
text.&nbsp; Note: the tip box only appears when using Internet Explorer
(IE)
<li>
From -- Displays the author of the post.</li>

<li>
Views -- Displays the number of users who have viewed the post at least
one time</li>

<br>Date -- The date and time of post creation. Date is displayed in the
users home time zone setting.</ul>


<a name="discussion_column_sorts"></a><span class="pageTitle">Column Sorts</span><br>
Column sorts are available on the Subject, from, and date fields.&nbsp;
Performing a column sort in threaded mode results in an automatic transfer
to flat mode. Clicking on a column heading results in a column sort. Clicking
on the heading a second time results in a column sort in the opposite direction.&lt;/para>
<br>&nbsp;
<p>
<a name="discussion_searches"></a><span class="pageTitle">Searches</span><br>
The discussion group can be searched with standard search icon on the toolbar.
The search dialog allows users to search on "from", "subject" and "post
body". The search results are displayed in the search dialog. Clicking
on a search result updates the top and bottom main frames. The bottom frame
contains the post.&nbsp; If the user is in threaded mode, the thread is
expanded if required to highlight the target post.
<br>&nbsp;
<p>
<a name="discussion_usage_examples"></a><span class="pageTitle">Using the Discussion Posts</span><br>
Features include:
<br>&nbsp;
<ul>
<li>
The post lists display posts the user has already read in a light gray
color. Unread posts are displayed in black.</li>

<li>
New posts can be created using the standard toolbar create icon if the
user has the required permission.</li>

<li>
Clicking on a post in the list marks that post as read by the user (if
not previously read) and displays the post in the bottom frame.</li>

<li>
A user can reply to a post by clicking on the reply link in the bottom
frame when reading a post.</li>

<li>
Clicking on the "info" link in the bottom frame will display post properties
and a reader history list. The history list contains a time-date stamp
for the first time each reader viewed the post.</li>
</ul>
<br><br>
<jsp:include page="discussion_main_create.jsp" flush="true"/>
