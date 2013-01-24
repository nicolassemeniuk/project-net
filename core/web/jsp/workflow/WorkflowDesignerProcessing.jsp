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
    info="WorkflowDesignerProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.security.*, net.project.workflow.*, net.project.base.PnetException" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" />
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<%-- Action Handling --%>
<%
	// Set current user and space
    workflowBean.setUser(user);
    workflowBean.setSpace(user.getCurrentSpace());

	// Grab parameters
	String theAction = request.getParameter("theAction");
	String workflowID = request.getParameter("id");
	
	// Handle parameters
    if (theAction.equals("create")) {
		workflowBean.clear();
		pageContext.forward("WorkflowCreate.jsp");
		
    } else if (theAction.equals("modify")) {
		if (workflowID == null) {
			throw new PnetException("workflowID is null");
		}
		workflowBean.clear();
		pageContext.forward("WorkflowEditConfirm.jsp");
		
	} else if (theAction.equals("remove")) {
		if (workflowID == null) {
			throw new PnetException("workflowID is null");
		}
		
		// Load workflow for ID, check security, prepare for remove
		// If removal is permitted, performs remove
		// Otherwise goes to WorkflowRemoveConfirm to display problem
		workflowBean.clear();
        workflowBean.setID(workflowID);
        workflowBean.load();
		managerBean.setCurrentWorkflowID(workflowBean.getID());
		managerBean.setCurrentWorkflowName(workflowBean.getName());
%>
		<security:verifyAccess objectID="<%=workflowBean.getID()%>"
							   action="delete"
							   module="<%=net.project.base.Module.WORKFLOW%>" />
<%
		workflowBean.clearErrors();
		workflowBean.prepareRemove();
		if (workflowBean.isRemovePermitted()) {
			workflowBean.remove();
			
			if (workflowBean.isRemoveSuccessful()) {
// Avinash: setting action to modify after removal----------------------------------------
				request.setAttribute("action",Integer.toString(net.project.security.Action.MODIFY));	
				ServletSecurityProvider.setAndCheckValues(request);
// Avinash:-------------------------------------------------------------------------------			
				pageContext.forward("WorkflowDesigner.jsp?action=" + net.project.security.Action.MODIFY);
			} else {
		        pageContext.forward("WorkflowRemoveResult.jsp?mode=removeError");
			}

		} else {
			pageContext.forward("WorkflowRemoveResult.jsp?mode=prepareError");
		}
	
	} else if (theAction.equals("properties")) {
		if (workflowID == null) {
			throw new PnetException("workflowID is null");
		}
		workflowBean.clear();
		pageContext.forward("Properties.jsp");
		
	} else if (theAction.equals("security")) {
		// Check security now
%>
		<security:verifyAccess objectID="<%=workflowID%>"
							   action="modify_permissions"
					   		   module="<%=net.project.base.Module.WORKFLOW%>"
		/> 
<%
		// Security JSP requires these attributes		
		request.setAttribute("module", Integer.toString(net.project.base.Module.SECURITY));
		request.setAttribute("action", Integer.toString(net.project.security.Action.MODIFY_PERMISSIONS));
		// Security JSP requires this session value
		session.setAttribute("objectID", workflowID);
		
//  Avinash: setting action to modify after removal---------------------
		ServletSecurityProvider.setAndCheckValues(request);
//	Avinash:------------------------------------------------------------	
		// Security JSP requires this request parameter
		pageContext.forward("/security/SecurityMain.jsp?objectType=workflow");
	
	} else {
		throw new PnetException("Invalid action '" + theAction + "' passed to WorkflowDesignerProcessing.jsp");
	}	
%>
<%-- End of action handling --%>
