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
    info="MainProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.*, net.project.form.*" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="formFieldDesigner" class="net.project.form.FormFieldDesigner" scope="session" />
<jsp:useBean id="formListDesigner" class="net.project.form.FormListDesigner" scope="session" />

<%
	// Toolbar: Create
	if ((request.getParameter("theAction") != null) && request.getParameter("theAction").equals("create"))
	{
		formDesigner.clear();
		formDesigner.setID(null);
		formFieldDesigner.clear();
		formListDesigner.clear();
		
		formDesigner.setSpace(user.getCurrentSpace());
		formDesigner.setUser(user);
		
		// only "forms" supported now.  Checklists, etc. coming soon.
		//formDesigner.setClassTypeID(Form.FORM);
		formDesigner.setClassTypeID("100");
		
		// new forms have "Pending" status until applied. 
		formDesigner.setRecordStatus("P");
		
		pageContext.forward("/form/designer/DefinitionEdit.jsp");
	}
	else if ((request.getParameter("theAction") != null) && request.getParameter("theAction").equals("workflow"))
	{
		pageContext.forward("/form/designer/envelope/EnvelopeWizardStart.jsp?id=" + request.getParameter("selected"));
	}
%>