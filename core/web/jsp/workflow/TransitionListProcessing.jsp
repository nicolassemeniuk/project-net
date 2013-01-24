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
| Provides handling for Transition List JSP
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="TransitionListProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.security.*, net.project.workflow.*, net.project.base.PnetException,
    		net.project.base.property.PropertyProvider" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="transitionBean" class="net.project.workflow.TransitionBean" scope="session" />
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<%
	transitionBean.setUser(user);
	
	String theAction = request.getParameter("theAction");
	if (theAction.equals("submit")) {
		if (request.getParameter("nextPage") != null && !request.getParameter("nextPage").equals("")) {		
			pageContext.forward(request.getParameter("nextPage"));
		} else {
	        pageContext.forward("WorkflowDesigner.jsp");
		}

	} else if (theAction.equals("transition_create")) {
		// bug-2355 fix
		transitionBean.clearErrors();

		transitionBean.setWorkflowID(request.getParameter("id"));
		transitionBean.validateUnpublished();
		if (transitionBean.hasErrors()) {
			transitionBean.getErrors().put("is_published", null, PropertyProvider.get("prm.workflow.ispublished.viewonly.validation.message"));
			pageContext.forward("TransitionList.jsp?action=" + net.project.security.Action.MODIFY
								+ "id=" + request.getParameter("id"));
		} else {
			// Create new transition
			transitionBean.clear();
			pageContext.forward("TransitionCreate.jsp");
		}

/*
		// Create new transition
		transitionBean.clear();
		pageContext.forward("TransitionCreate.jsp");
*/
    } else if (theAction.equals("transition_modify")) {
		// Modify transition
		pageContext.forward("TransitionEdit.jsp");

    } else if (theAction.equals("transition_remove")) {
		transitionBean.clear();
		transitionBean.setID(request.getParameter("transition_id"));
		transitionBean.load();
		managerBean.setCurrentTransitionID(transitionBean.getID());
		managerBean.setCurrentTransitionName(transitionBean.getTransitionVerb());
%>
		<security:verifyAccess objectID="<%=transitionBean.getWorkflowID()%>"
							   action="modify"
							   module="<%=net.project.base.Module.WORKFLOW%>"
		/> 
<%
		transitionBean.clearErrors();
		transitionBean.prepareRemove();
		if (transitionBean.isRemovePermitted()) {
			transitionBean.remove();

			if (transitionBean.isRemoveSuccessful()) {
				pageContext.forward("TransitionList.jsp?action=" + net.project.security.Action.MODIFY);
			} else {
		        pageContext.forward("TransitionRemoveResult.jsp?mode=removeError");
			}

		} else {
				pageContext.forward("TransitionRemoveResult.jsp?mode=prepareError");
		}
		

    } else if (theAction.equals("transition_properties")) {
		throw new PnetException("View transition properties functionality not yet implemented.");

	} else {
		throw new PnetException("Unknown action passed to TransitionListProcessing: " + theAction);
	}
%>
<%-- End of action handling --%>
