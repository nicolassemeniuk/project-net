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
    info="Edit Form" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.form.*,
			java.net.URLEncoder, 
			net.project.security.*, 
			net.project.base.Module" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<html>
<head>
<title><%=PropertyProvider.get("prm.form.csvimportpage.title")%></title>
<%
		securityProvider.securityCheck(request.getParameter("load"), Integer.toString(Module.FORM), Action.MODIFY);
				
		FormData formData = new FormData(form);
		formData.setCurrentSpace(user.getCurrentSpace());
		session.setAttribute("csvImportObjectName", formData);
		response.sendRedirect(SessionManager.getJSPRootURL()+"/datatransform/csv/FileUpload.jsp?"+
        	"referrer="+URLEncoder.encode("/form/FormList.jsp?module=" + Module.FORM, SessionManager.getCharacterEncoding()));
%>
