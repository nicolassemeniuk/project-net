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
<a name="admin_userlist"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
    <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
    <td nowrap class="channelHeader"><nobr>Administration Space License list</nobr></td>
    <td align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
</tr>
</table>
<p>
The License List View allows you to:
<br>&nbsp;
<ul>
<li>Search a license based on a username, first-name or last-name, email-address, license-key.</li>
<li>Filter licenses that would be displayed based on the type of license -- Trial/Non-Trial/All,
    Enabled/Disabled/All.</li>
<li>View some license properties like license-key, type, status, validity date, payment-information.</li>	
<li>Sort the listed licenses by clicking on the column headings.</li>
<li>Click on a license-key and see the detail information on that license.</li>


<br>