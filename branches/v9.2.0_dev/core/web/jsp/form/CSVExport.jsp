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
    info="Download CSV data"
    language="java"
    errorPage="/errors.jsp"
	import="net.project.security.SessionManager,
            net.project.form.FormListCSVDownload,
            net.project.form.Form"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<jsp:useBean id="formListCSVDownload" class="net.project.form.FormListCSVDownload" scope="session" />

<security:verifyAccess module="<%=net.project.base.Module.FORM%>" 
                       action="view"
                       objectID='<%=form.getID()%>' />

<%
    formListCSVDownload.setFormList(form.getDisplayList());
    response.sendRedirect(SessionManager.getJSPRootURL() + "/servlet/Download?downloadableObjectAttributeName=formListCSVDownload&cleanup=true");
%>
