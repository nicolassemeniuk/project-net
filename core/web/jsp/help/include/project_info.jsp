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
<a name="project_info"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Project Information</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
<h1>
<a name="project_space_top"></a>Project Info</h1>
The Project Info General tab lists status information and Planning dates
of the selected project. It also includes a brief description to aid in
identify a particular project. This data can be revised by clicking Modify.
<p>The Configuration tab includes more detailed project information with
industry standard nomenclature. Field in this tab include: Project Name,
Location, Customer, Square Footage, Stories, TIC, Use Classification, Project
Type, Site Condition, and Services. As with any portion of the Project.net site,
this data can be updated by clicking Modify.
<br><br>
<jsp:include page="project_info_logo.jsp" flush="true"/>
<br>

