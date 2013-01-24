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
<a name="project_info_logo"></a>

<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Logo</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
You can display a corporate or project-specifc logo with a project. This
logo appears on the Project Home page.
<h3>
<a name="add_logo"></a>To add a logo to a project</h3>

<ol>
<li>
Go to the Project home page.</li>

<li>
Select the project where the logo will be attached.</li>

<li>
Click on Project Info.</li>

<li>
Select the General tab.</li>

<li>
In the lower, right corner click Modify.</li>

<li>
Click Change Logo in the Description tab.</li>

<li>
The Change Logo combo box opens.</li>

<li>
Navigate to where the logo is stored through the Browse option.</li>

<li>
Click Upload Logo when the logo has been selected.</li>
</ol>
The only&nbsp; restriction on the logo is that is should be no larger than
120 High X 150 wide.
