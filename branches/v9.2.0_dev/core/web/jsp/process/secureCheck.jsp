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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $  
|
|   page for intermediate security check for modifying_permissions
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Process Security Checker"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SecurityProvider,
	net.project.security.ServletSecurityProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
	String id = request.getParameter("id");
%>
<security:verifyAccess action="modify_permissions"
					   module="<%=net.project.base.Module.PROCESS%>"
					   objectID='<%=id%>' /> 
<%
	session.putValue("objectID", id);
	request.setAttribute("action", Integer.toString(net.project.security.Action.MODIFY_PERMISSIONS));
	request.setAttribute("module", Integer.toString(net.project.base.Module.SECURITY));
	ServletSecurityProvider.setAndCheckValues(request);
	pageContext.forward ("/security/SecurityMain.jsp");
%>