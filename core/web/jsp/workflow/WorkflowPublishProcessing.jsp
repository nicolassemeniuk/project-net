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
| Provides handling for Workflow Publish JSP
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="WorkflowPublishProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.security.*, net.project.workflow.*, net.project.base.PnetException" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" />
<jsp:useBean id="transitionBean" class="net.project.workflow.TransitionBean" scope="session" />
<%
    workflowBean.setUser(user);
    workflowBean.setSpace(user.getCurrentSpace());

	if (request.getParameter("theAction").equals("submit")) {
		if (request.getParameter("nextPage") != null && !request.getParameter("nextPage").equals("")) {
			// clear transitionBean errors, if any
			if (request.getParameter("nextPage").equals("TransitionList.jsp")) {
				transitionBean.clearErrors();
			}
			pageContext.forward(request.getParameter("nextPage"));
		} else {
	        pageContext.forward("WorkflowDesigner.jsp");
		}

    } else if (request.getParameter("theAction").equals("publish")) {
		/* Publish the workflow and go back to the Publish page if there was a problem */
		workflowBean.clearErrors();
		workflowBean.publish();
		if (workflowBean.hasErrors()) {
			pageContext.forward("WorkflowPublish.jsp");
		} else {
			workflowBean.store();
			pageContext.forward("WorkflowPublish.jsp");
		}
		
    } else if (request.getParameter("theAction").equals("unpublish")) {
		/* Unpublish the workflow and go back to the Publish page if there was a problem (unlikely) */
		workflowBean.clearErrors();
		workflowBean.unpublish();
		if (workflowBean.hasErrors()) {
			pageContext.forward("WorkflowPublish.jsp");
		} else {
			workflowBean.store();
			pageContext.forward("WorkflowPublish.jsp");
		}

	} else {
		throw new PnetException("Unknown action '" + request.getParameter("theAction") + "' passed to WorkflowPublishProcessing");
	}
%>
<%-- End of action handling --%>
