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
    info="Manual Invite Include Page" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.resource.SpaceInvitationManager, 
			net.project.security.Action,
			net.project.security.SecurityProvider,
            net.project.security.SessionManager,
			net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="spaceInvitationWizard" type="net.project.resource.SpaceInvitationManager" scope="session" />
<jsp:useBean id="lastInvitee" class="net.project.resource.Invitee" scope="session" />

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.DIRECTORY%>" /> 

<%
    boolean isError = (session.getValue("errorMsg") != null);
%>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr align="left" class="tableHeader">
		<td width=1%>&nbsp;</td>
		<td colspan="4" class="tableHeader"><display:get name="prm.directory.invite.memberaddition.inviteby.label" /></td>
		<td width=1%>&nbsp;</td>
	</tr> 
    <tr align="left" class="tableContent">
    	<td nowrap>&nbsp;</td>
    	<td colspan="4" class="tableContent"><display:get name="prm.directory.invite.memberaddition.invitation.instruction" /></td>
    	<td nowrap>&nbsp;</td>
    </tr>
<%	if (isError) { %>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr align="left">
		<td colspan="4" class="fieldWithError">
	            <b><%= session.getValue("errorMsg") %></b>
		</td>
	</tr>
<%	} %>          
    <tr align="left" class="tableContent"> 
        <td nowrap colspan="6" class="tableContent">&nbsp; </td>
    </tr>
    <tr align="left"> 
        <td nowrap width="1%">&nbsp;</td>
        <td nowrap class="fieldRequired" width="15%"><display:get name="prm.directory.invite.memberaddition.firstname.label" />&nbsp;</td>
        <td colspan="3" class="tableContent"> 
            <input type="text" name="inviteeFirstName" size="20" maxlength="80" value='<c:out value="${lastInvitee.firstName}" />'>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left"> 
        <td nowrap>&nbsp;</td>
        <td nowrap class="fieldRequired"><display:get name="prm.directory.invite.memberaddition.lastname.label" />&nbsp;</td>
        <td colspan="3" class="tableContent"> 
            <input type="text" name="inviteeLastName" size="20" maxlength="80" value='<c:out value="${lastInvitee.lastName}" />'>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left" > 
        <td nowrap>&nbsp;</td>
        <td nowrap class="fieldRequired"><display:get name="prm.directory.invite.memberaddition.emailaddress.label" />&nbsp;</td>
        <td colspan="3" class="tableContent"> 
            <input type="text" name="inviteeEmail" size="20" maxlength="80" value='<c:out value="${lastInvitee.email}" />'>
            &nbsp;&nbsp;<a href="javascript:add();"><display:get name="prm.directory.invite.memberaddition.addinvitee.link" /></a>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
</table>

