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
    info="Directory Search" 
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
    boolean isError = (session.getValue("errorMsgDirectory") != null);
%>			   

<table border="0" align="left" cellpadding="0" cellspacing="0" width="100%">
	<tr align="left" class="tableHeader">
		<td width=1%>&nbsp;</td>
		<td colspan="4" class="tableHeader"><%=PropertyProvider.get("prm.license.inviteuser.search.searchdirectory.label")%></td>
		<td width=1%>&nbsp;</td>
	</tr> 
	
    <tr align="left" class="tableContent">
    	<td nowrap>&nbsp;</td>
    	<td colspan="4" class="tableContent"><%=PropertyProvider.get("prm.license.inviteuser.search.instruction")%></td>
    	<td nowrap>&nbsp;</td>
    </tr>

	<%	if (isError) { %>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr align="left">
		<td colspan="4" class="fieldWithError">
	            <b><%= session.getValue("errorMsgDirectory") %></b>
		</td>
	</tr>
	<%	} %>    
	
    <tr align="left" class="tableContent"> 
    	<td nowrap colspan="6">&nbsp;</td>
    </tr>

	<tr align="left">
		<td nowrap>&nbsp;</td>
        <td nowrap class="tableHeader" width="15%"><%=PropertyProvider.get("prm.license.inviteuser.search.directory.label")%></td>
        <td nowrap colspan="3" class="tableContent"> 
            <select name="directoryID">
            <!-- Avinash: empty value  -->
			   <c:out value="${invitationManager.directoryOptionList}"/>
			</select>
        </td>
        <td nowrap>&nbsp;</td>
    </tr>

    <tr align="left">
    	<td nowrap>&nbsp;</td>
        <td nowrap class="tableHeader"><%=PropertyProvider.get("prm.license.inviteuser.search.name.label")%></td>
        <td nowrap colspan="3" class="tableContent"> 
        <!-- Avinash: empty textbox  -->
        	<input type="text" name="searchName" size="30" maxlength="80" value="<c:out value="${invitationManager.searchName}"/>" onFocus="this.select();">
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
    <tr align="left">
    	<td nowrap>&nbsp;</td>
        <td align="right" colspan="4" class="tableContent"> 
			<a href="javascript:wizSearch();">
            <img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-search_on.gif" alt="Search" border=0 align="absmiddle">
            &nbsp;<%=PropertyProvider.get("prm.license.inviteuser.search.search.link")%>
            </a> 
        </td>
        <td nowrap>&nbsp;</td>
    </tr>
</table>     

