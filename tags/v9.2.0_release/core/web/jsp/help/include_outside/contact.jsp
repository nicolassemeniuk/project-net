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
    info="include_outside.contact.jsp"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
			net.project.base.property.PropertyProvider"
%>
<a name="contact"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Contact Information</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>

<dl>
<dt><b>Headquarters:</b>
<dd>Project.net, Inc.<br>
<dd>54 Middlesex Turnpike<br>
<dd>Bedford, MA 01730
<br>
<dd>Phone: +1 617.621.0060<br>
<dd>Fax: 617.621.9555<br><br>
<dd><a href="http://www.project.net/">www.project.net</a><br><br>


<dt><b>Email:</b>
<dd><a href="mailto:<%=PropertyProvider.get("prm.global.help.info.emailaddress") %>"><%=PropertyProvider.get("prm.global.help.info.emailaddress") %></a><br>
<dd><a href="mailto:<%=PropertyProvider.get("prm.global.help.sales.emailaddress") %>"><%=PropertyProvider.get("prm.global.help.sales.emailaddress") %></a><br>
<dd><a href="mailto:<%=PropertyProvider.get("prm.global.help.support.emailaddress") %>"><%=PropertyProvider.get("prm.global.help.support.emailaddress") %></a><br><br>

</dl>

