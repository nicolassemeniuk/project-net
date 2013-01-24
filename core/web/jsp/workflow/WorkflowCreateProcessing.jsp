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
    info="WorkflowCreateProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.security.*, net.project.base.PnetException,
	net.project.workflow.*" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" />
<%
    workflowBean.setUser(user);
    workflowBean.setSpace(user.getCurrentSpace());

    if (request.getParameter("theAction").equals("submit")) {
%>
		<jsp:setProperty name="workflowBean" property="spaceID" />
		<jsp:setProperty name="workflowBean" property="name" />
		<jsp:setProperty name="workflowBean" property="description" />
		<jsp:setProperty name="workflowBean" property="ownerID" />
		<jsp:setProperty name="workflowBean" property="strictnessID" />
<%
		workflowBean.addObjectType(request.getParameter("objectTypeName"), request.getParameter("subTypeID"));
		workflowBean.clearErrors();
		workflowBean.validateName();
		workflowBean.validateDescription();
		workflowBean.validateOwnerID();
		workflowBean.validateStrictnessID();
		workflowBean.validateObjectTypes();
		if (workflowBean.hasErrors()) {
			pageContext.forward("WorkflowCreate.jsp");
		} else {
			workflowBean.store();
			// Set the workflowID that was just created
			managerBean.setCurrentWorkflowID(workflowBean.getID());
			request.setAttribute("id", workflowBean.getID());
			request.setAttribute("action", new Integer(net.project.security.Action.MODIFY).toString());
//Avinash:-----------------------------------------------------------------------
			ServletSecurityProvider.setAndCheckValues(request);
//Avinash:-----------------------------------------------------------------------
			if (request.getParameter("nextPage") != null && !request.getParameter("nextPage").equals("")) {		
				pageContext.forward(request.getParameter("nextPage"));
			} else {
		        pageContext.forward("StepList.jsp");
			}

		}
	} else {
		throw new PnetException("Unknown action '" + request.getParameter("theAction") + "' passed to WorkflowCreateProcessing.jsp");
	}
%>
<%-- End of action handling --%>
