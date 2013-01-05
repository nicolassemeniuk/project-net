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
| Provides handling for Properties JSP
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="PropertiesProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.security.*, net.project.base.PnetException,
			net.project.workflow.*" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" />
<%
	String theAction = request.getParameter("theAction");
    if (theAction.equals("submit")) {
		if (request.getParameter("nextPage") != null && !request.getParameter("nextPage").equals("")) {		
			pageContext.forward(request.getParameter("nextPage"));
		} else {
	        pageContext.forward("Main.jsp");
		}

    } else if (theAction.equals("modify")) {
		workflowBean.clear();
		pageContext.forward("WorkflowEditConfirm.jsp");

    } else {
		// Problem
		throw new PnetException("Unknown action passed to PropertiesProcessing: " + theAction);
	}
%>
<%-- End of action handling --%>
