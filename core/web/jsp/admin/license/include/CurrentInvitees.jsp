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

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Current Invitees" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.Action,
			net.project.security.SecurityProvider,
            net.project.security.SessionManager,
			net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="invitationManager" type="net.project.resource.LicenseInvitationManager" scope="session" />

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.DIRECTORY%>" /> 

<%
    net.project.resource.InviteeList inviteeList = invitationManager.getInviteeList();
    pageContext.setAttribute("inviteeList", inviteeList, PageContext.PAGE_SCOPE);
%>

<table border="0" align="left" cellpadding="0" cellspacing="0" width="100%">
	<tr class="channelHeader">
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td nowrap colspan="4" class="channelHeader"><nobr><%=PropertyProvider.get("prm.license.inviteuser.search.channel.invitees.title")%></nobr></td>
		<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr> 
    <tr>
        <td>&nbsp;</td>
        <td colspan="4">
            <pnet-xml:transform name="inviteeList" scope="page" stylesheet="/roster/xsl/CurrentInvitees.xsl" />
        </td>
        <td>&nbsp;</td>
    </tr>
</table>     

