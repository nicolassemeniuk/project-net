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
    info="ListsManagerProcessing.  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User, net.project.form.*" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="formListDesigner" class="net.project.form.FormListDesigner" scope="session" />

<%
	// Set context for a new or existing Form
	formDesigner.setUser(user);
	formDesigner.setSpace(user.getCurrentSpace());
	formDesigner.setID(form.getID());
	formDesigner.load();
	formDesigner.loadLists(true);


	// Toolbar Action: remove
	if (request.getParameter("theAction").equals("remove"))
	{
		formListDesigner.setID(request.getParameter("selected"));
		formDesigner.removeList(formListDesigner);
		formListDesigner.clear();
		pageContext.forward("ListManager.jsp");
	}
	
	// Toolbar Action: create
	else if (request.getParameter("theAction").equals("create"))
	{
		System.out.println("************* New list");
		formListDesigner.clear();
		formListDesigner.setForm(formDesigner);
		formListDesigner.setIsNewList(true);
		pageContext.forward("designer/ListEdit.jsp?role=user");
	}
	
	// Toolbar Action: modify
	else if (request.getParameter("theAction").equals("modify"))
	{
		// the id parameter gets passed along by the forward??
		pageContext.forward("designer/ListEdit.jsp?listID=" + request.getParameter("selected") + "&role=user");
	}
	
	// Toolbar Action: properties
	else if (request.getParameter("theAction").equals("properties"))
	{
		pageContext.forward("designer/ListEdit.jsp?listID=" + request.getParameter("selected"));
	}
%>
