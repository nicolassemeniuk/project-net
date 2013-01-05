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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
| Provides handling for Workflow Edit JSP
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="WorkflowEditProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.PnetException,
    		net.project.base.Module,
    		net.project.security.Action" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" />
<%
    workflowBean.setUser(user);
    workflowBean.setSpace(user.getCurrentSpace());

	String theAction = request.getParameter("theAction");
    if (theAction.equals("submit")) {
		// Submitting modified Workflow
		
%>
		<%-- Set all properteies --%>
		<jsp:setProperty name="workflowBean" property="*" />
<%
		if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		    workflowBean.setDescription("");
		}
			workflowBean.addObjectType(request.getParameter("objectTypeName"), request.getParameter("subTypeID"));
			workflowBean.clearErrors();
			workflowBean.validateName();
			workflowBean.validateDescription();
			workflowBean.validateOwnerID();
			workflowBean.validateStrictnessID();
			workflowBean.validateObjectTypes();
			workflowBean.validateUnpublished();
			if (workflowBean.hasErrors()) {
				pageContext.forward("WorkflowEdit.jsp");
			} else {
		        workflowBean.store();
				managerBean.setCurrentWorkflowID(workflowBean.getID());

				if (request.getParameter("nextPage") != null && !request.getParameter("nextPage").equals("")) {		
					pageContext.forward(request.getParameter("nextPage"));
				} else {
			        response.sendRedirect("WorkflowDesigner.jsp?module="+Module.WORKFLOW+"&action="+Action.MODIFY);
				}
			}
			
    } else {
		// Problem
		throw new PnetException("Unknown action passed to WorkflowEditProcessing: " + theAction);
	}
%>
<%-- End of action handling --%>
