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
|   $Revision: 10578 $
|       $Date: 2003-02-25 00:59:34 +0530 (Tue, 25 Feb 2003) $
|
|--------------------------------------------------------------------%>
<%@ page contentType="text/html; charset=UTF-8" info="Process Material Delete" language="java" errorPage="/errors.jsp"
	import="net.project.security.*, 
    		net.project.base.Module,
			net.project.base.PnetException,
			net.project.material.Material,
			net.project.hibernate.service.ServiceFactory"%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<security:verifyAccess action="delete" module="<%=Module.MATERIAL%>" />
<%
 	String selected = request.getParameter("selected");

	Material material = (Material) pageContext.getAttribute("material_to_disable" , pageContext.SESSION_SCOPE);

	if(selected != null  && selected.equals("disable")){
		ServiceFactory.getInstance().getMaterialService().disableMaterial(material.getMaterialId());
	}

	out.println("<script language=\"javascript\">");
	//out.println("opener.location.reload(true);");
	out.println("opener.location='" + SessionManager.getJSPRootURL() + "/material/Main.jsp?module=" + Module.MATERIAL+ "';");
	out.println("self.close();");
	out.println("</script>");
%>


