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

<%@ page contentType="text/html; charset=UTF-8" info="Create Salary Processing" language="java" errorPage="/errors.jsp"
	import="net.project.security.*, 
    		net.project.base.Module,
    		net.project.resource.PersonSalaryBean,
			net.project.hibernate.service.ServiceFactory,
			java.util.Date"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="ownerUser" class="net.project.security.User" scope="session" />
<jsp:useBean id="personSalaryBean" class="net.project.resource.PersonSalaryBean" scope="session" />

<jsp:setProperty name="personSalaryBean" property="personId" value='<%= ownerUser.getID() %>' />
<jsp:setProperty name="personSalaryBean" property="startDate" value='<%= ownerUser.getDateFormatter().parseDateString(request.getParameter("startDate")) %>' />
<jsp:setProperty name="personSalaryBean" property="costByHour" />
<jsp:setProperty name="personSalaryBean" property="user" value='<%= user %>' />

<security:verifyAccess action="create" module="<%= Module.SALARY %>" />

<%
	ServiceFactory.getInstance().getPnPersonSalaryService().savePersonSalary(personSalaryBean);	
	
	out.println("<script language=\"javascript\">");
	out.println("opener.location='" + SessionManager.getJSPRootURL() + "/salary/PersonalSalary.jsp?module=" + Module.SALARY + "&user=" + ownerUser.getID() + "&mode=edit';");
	out.println("self.close();");
	out.println("</script>");
%>