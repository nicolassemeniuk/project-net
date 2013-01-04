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
    info="Forms Administration Page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>
<html>
<head>
<%
// Toolbar: Remove
	if (request.getParameter("theAction").equals("remove")){
		formDesigner.setID(request.getParameter("selected"));
		formDesigner.remove();
		response.sendRedirect(SessionManager.getJSPRootURL()+"/admin/form/FormsAdmin1.jsp?module=" + net.project.base.Module.APPLICATION_SPACE+"&action=" + net.project.security.Action.MODIFY);
	} 
	else
	response.sendRedirect(SessionManager.getJSPRootURL()+"/admin/form/FormsAdmin2.jsp?module=" + net.project.base.Module.APPLICATION_SPACE+"&action=" + net.project.security.Action.MODIFY + "&status=Active"+"&FormID="+request.getParameter("FormID"));
%>

