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
|   MainProcessing.jsp
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
| Provides handling for Workflow Main JSP
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="MainProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/workflow/WorkflowErrors.jsp"
    import="net.project.base.PnetException,
			net.project.workflow.*" 
%>
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" />
<%
	String theAction = request.getParameter("theAction");
	
	if (theAction.equals("create")) {
		workflowBean.clear();
		pageContext.forward("WorkflowCreate.jsp");

	} else {
		throw new PnetException("Unknown action '" + theAction + "' in MainProcessing.jsp");
	}
%>
