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
| Allows selection of workflows for a form
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="WorkflowSelectProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.*, net.project.form.*" 
%>

<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />

<%
	// Toolbar: Submit button
	if (request.getParameter("theAction").equals("submit") ||
		request.getParameter("theAction").equals("select_workflow")) {

		if (request.getParameter("theAction").equals("select_workflow")) {
			System.out.println("*** WorkflowID is " + request.getParameter("workflowID"));
			if (request.getParameter("workflowID") == null || request.getParameter("workflowID").equals("")) {
				// Explicitly deselect workflow
				formDesigner.setWorkflowID("");
			} else {
%>
				<jsp:setProperty name="formDesigner" property="workflowID" />
<%
			}
		}
		formDesigner.store();
		if ((request.getParameter("nextPage") != null) && !request.getParameter("nextPage").equals(""))
			pageContext.forward(request.getParameter("nextPage"));
		else
			pageContext.forward("WorkflowSelect.jsp");
	}
%>
