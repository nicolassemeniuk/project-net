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
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="WorkflowEditConfirmProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.security.*, net.project.workflow.*, net.project.base.PnetException" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" />
<%
    workflowBean.setUser(user);
    workflowBean.setSpace(user.getCurrentSpace());

	String theAction = request.getParameter("theAction");
    if (theAction.equals("change_status")) {
		/* Unpublish the workflow and go back to the Publish page if there was a problem (unlikely) */
		workflowBean.clearErrors();
		
		
		workflowBean.unpublish();
		if (workflowBean.hasErrors()) {
			pageContext.forward("WorkflowPublish.jsp");
		} else {
			workflowBean.store();
			pageContext.forward("WorkflowEdit.jsp");
		}
		
	} else if (theAction.equals("view_only")) {
		/* Leave alone.  All future store operations will check to make sure it is not published */
		pageContext.forward("WorkflowEdit.jsp");	
		
    } else {
		// Problem
		throw new PnetException("Unknown action passed to WorkflowEditConfirmProcessing: " + theAction);
	}
%>
<%-- End of action handling --%>
