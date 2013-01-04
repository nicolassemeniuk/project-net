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
    info="ActivateEditProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*,
			net.project.form.*" 
%>

<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
	// Toolbar: Submit button
	if (request.getParameter("theAction").equals("submit"))
	{
	 	formDesigner.setShareFormVisibilty(request.getParameter("formStatus").equals("in_use"), user.getCurrentSpace().getID());	
	}	
    //If the user is activating the form, reload the left menu just in case
    //they changed the "show in tools menu" option
    session.putValue("reloadMenu", "true");    
	pageContext.forward("SharedFormActivationEdit.jsp");
%>
