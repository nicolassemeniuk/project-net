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

<%
/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
| This page loads a form list based on a form_list_id, extracts its
| form then forwards onto the FormList.jsp for rendering
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Form List Load.  No output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.security.SessionManager,
			net.project.form.*, 
			net.project.base.PnetException" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<% 
	FormList formList = new FormList();

	if (request.getParameter("id") != null && !request.getParameter("id").equals("")) {
		formList.setID(request.getParameter("id"));
		formList.setCurrentSpace(user.getCurrentSpace());
		formList.load();

		// Now retrieve the form which will have been loaded and stick in session
		Form form = formList.getForm();
		form.loadLists(true);
		form.setDisplayListID(formList.getID());
		pageContext.setAttribute("form", form, PageContext.SESSION_SCOPE);

		// Now go to FormList, telling it not to reload form object
		pageContext.forward("/form/FormList.jsp?load=false&id=" + form.getID());

	} else {
		throw new PnetException("Missing parameter 'id' in FormListLoad.jsp");
	}
%>
