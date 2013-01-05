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
| Provides handling for Step Create JSP
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="StepCreateProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.PnetException" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" /> 
<jsp:useBean id="stepBean" class="net.project.workflow.StepBean" scope="session" />
<%
    stepBean.setUser(user);
    stepBean.setSpace(user.getCurrentSpace());

    if (request.getParameter("theAction").equals("submit")) {
%>
		<%-- Set all properteies --%>
		<jsp:setProperty name="stepBean" property="*" />
<%
		stepBean.clearErrors();
		stepBean.validate();
		
		if (stepBean.hasErrors()) {
			pageContext.forward("StepCreate.jsp");
		} else {
	        stepBean.store();
			managerBean.setCurrentStepID(stepBean.getID());
	        pageContext.forward("StepEdit.jsp?step_id=" + stepBean.getID());
		}
	} else {
		throw new PnetException("Unknown action '" + request.getParameter("theAction") + "' passed to StepCreateProcessing.jsp");
	}
%>
<%-- End of action handling --%>
