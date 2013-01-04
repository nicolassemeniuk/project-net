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
|   $Revision: 19724 $
|       $Date: 2009-08-12 09:14:00 -0300 (mié, 12 ago 2009) $
|     $Author: uroslates $
|
| Provides handling for Step List JSP
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="StepListProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.security.*, net.project.workflow.*, net.project.base.PnetException" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<script type="text/javascript">
	 javascript:window.history.forward(-1);
</script>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="stepBean" class="net.project.workflow.StepBean" scope="session" />
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<jsp:useBean id="transitionBean" class="net.project.workflow.TransitionBean" scope="session" />
<%
	stepBean.setUser(user);
	
	String theAction = request.getParameter("theAction");
    if (theAction.equals("submit")) {
		if (request.getParameter("nextPage") != null && !request.getParameter("nextPage").equals("")) {		
			// clear transitionBean errors, if any  
			if (request.getParameter("nextPage").equals("TransitionList.jsp")) {
				transitionBean.clearErrors();
			}
			pageContext.forward(request.getParameter("nextPage"));
		} else {
	        pageContext.forward("WorkflowDesigner.jsp");
		}

	} else if (theAction.equals("step_create")) {
		// Create new step
		stepBean.clear();
		pageContext.forward("StepCreate.jsp");

	} else if (theAction.equals("step_modify")) {
		// Modify existing step
		pageContext.forward("StepEdit.jsp");

	} else if (theAction.equals("step_remove")) {
		stepBean.clear();
		stepBean.setID(request.getParameter("step_id"));
		stepBean.load();
		managerBean.setCurrentStepID(stepBean.getID());
		managerBean.setCurrentStepName(stepBean.getName());
%>
		<security:verifyAccess objectID="<%=stepBean.getWorkflowID()%>"
							   action="modify"
							   module="<%=net.project.base.Module.WORKFLOW%>"
		/> 
<%
		stepBean.clearErrors();
		stepBean.prepareRemove();
		if (stepBean.isRemovePermitted()) {
			stepBean.remove();

			if (stepBean.isRemoveSuccessful()) {
				pageContext.forward("StepList.jsp?action=" + net.project.security.Action.MODIFY);
			} else {
		        pageContext.forward("StepRemoveResult.jsp?mode=removeError");
			}

		} else {
		        pageContext.forward("StepRemoveResult.jsp?mode=prepareError");
		}

	} else if (theAction.equals("step_properties")) {
		throw new PnetException("Step property viewing functionality not available.");

    } else {
		throw new PnetException("Unknown action passed to StepListProcessing: " + theAction);
	}
%>
<%-- End of action handling --%>
