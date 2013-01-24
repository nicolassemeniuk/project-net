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
    info="FieldsManagerProcessing.  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User, net.project.form.*" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="formFieldDesigner" class="net.project.form.FormFieldDesigner" scope="session" />

<%
       
	// Toolbar Action: remove
	if (request.getParameter("theAction").equals("remove"))
	{
		if (formDesigner.getFields().size() > 1 || !formDesigner.hasLists()) {
			formFieldDesigner.setID(request.getParameter("selected"));
			formFieldDesigner.setForm(formDesigner);
			formDesigner.removeField(formFieldDesigner);
			formDesigner.setStatus(Form.PENDING);
			formFieldDesigner.clear();
		} else {
			request.setAttribute("error", true);	
		}
		pageContext.forward("FieldsManager.jsp");
	}
	
	// Toolbar Action: create
	else if (request.getParameter("theAction").equals("create"))
	{
		formFieldDesigner.clear();
		formFieldDesigner.setForm(formDesigner);
		//formFieldDesigner.setUser(user);
		formFieldDesigner.setIsNewField(true);
		pageContext.forward("FieldEdit.jsp");
	}
	
	// Toolbar Action: modify
	else if (request.getParameter("theAction").equals("modify"))
	{
		pageContext.forward("FieldEdit.jsp?fieldID=" + request.getParameter("selected"));
	}
	
	// Toolbar Action: properties
	else if (request.getParameter("theAction").equals("properties"))
	{
		pageContext.forward("FieldEdit.jsp?fieldID=" + request.getParameter("selected"));
	}
	
	// Toolbar Action: copy
	else if (request.getParameter("theAction").equals("copy"))
	{
		pageContext.forward("FieldCopyProcessing.jsp?fieldID=" + request.getParameter("selected"));
	} 

	else if (request.getParameter("theAction").equals("hideInEaf"))
	{
		formFieldDesigner.setID(request.getParameter("selected"));
		formFieldDesigner.setForm(formDesigner);
		formDesigner.hideField(formFieldDesigner);
		formFieldDesigner.clear();
		pageContext.forward("FieldsManager.jsp");
	}
%>
