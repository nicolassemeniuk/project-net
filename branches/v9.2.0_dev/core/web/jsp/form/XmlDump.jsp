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
    info="Form List" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.form.*, net.project.project.*, net.project.security.*" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<jsp:useBean id="formMenu" class="net.project.form.FormMenu" scope="page" />
<jsp:useBean id="fieldPropertySheet" class="net.project.form.FieldPropertySheet" scope="session" />
<jsp:useBean id="formFieldDesigner" class="net.project.form.FormFieldDesigner" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="formListDesigner" class="net.project.form.FormListDesigner" scope="session" />

<%
	//formMenu.setSpace(user.getCurrentSpace());
	//formMenu.setUser(user);
	//formMenu.setDisplayPending(true);
	//formMenu.load();
%>
<%-- commented because formmenu does not have xml property-->
<%--jsp:getProperty name="formMenu" property="xml" /--%>