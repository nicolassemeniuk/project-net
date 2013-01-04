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
<a name="document_main_applet"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Project.net Document Upload-Download Applet</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>

<a name="document_applet_description"></a>
This tool allows users to perform a bulk Check In and Check Out of large
numbers of files or individual files. The primary mode of operation is
drag and drop, where you drag files to be checked in (or checked out) from
a directory viewed in Windows Explorer into the Document Applet window.
<p>You can also move files by selecting and moving groups (or individual
files). Bulk deletions are also provided for routine housekeeping.
<p>When you first try to access the Document Applet, you may have to download
the Java 3 Runtime environment, v 1.3. Follow the instructions to load
and install this environment. Please note that you will have to restart
your computer to complete the installation of this software. As such, please
shut down all open windows, except Project.net.
<p>Each time you use the applet you may have to grant permission to run
the plug ins unless you click "Grant Always."
<p>The options in the Project.net Document Applet are:
<ul>
<li>
Delete Document -- Delete the selected dcoument(s).</li>
<li>
Check in</li>
<li>
Check Out</li>
<li>
Item next</li>
<li>
Item Next</li>
</ul>

