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

<%-- This page is for jsp:includes only --%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="securityGroup.jsp include file" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User,
            net.project.security.DisplayGroupList,
            net.project.security.SecurityManager,
            net.project.security.group.GroupCollection,
            net.project.security.SessionManager" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityManager" class="net.project.security.SecurityManager" scope="session" />

<%
	DisplayGroupList displayGroupListBean = securityManager.getNonPrincipalGroups();
    pageContext.setAttribute("displayGroupList", displayGroupListBean, PageContext.PAGE_SCOPE);
%>
<jsp:useBean id="displayGroupList" type="net.project.security.DisplayGroupList" scope="page" />

<table width="97%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<tr>
	<th>&nbsp;</th>
	<th align="left" class="tableHeader"><%=PropertyProvider.get("prm.security.editrole.roles.name.column")%></th>
	<th align="left" class="tableHeader"><%=PropertyProvider.get("prm.security.editrole.roles.description.column")%></th>
</tr>
<tr class="tableLine">
	<td  colspan="3" class="tableLine"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
</tr>
	<jsp:setProperty name="displayGroupList" property="stylesheet" value="/security/include/securityGroup-list.xsl" />
	<jsp:getProperty name="displayGroupList" property="presentation" /> 

</table>

	