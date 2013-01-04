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
    info="TransitionProblemProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="../WorkflowErrors.jsp"
    import="net.project.security.*, net.project.workflow.*, net.project.base.PnetException" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<%
	String theAction = request.getParameter("theAction");
    if (theAction.equals("continue")) {
		// User wants to continue regardless of warnings... so ignore them when we redo the transiiotn
		envelopeManagerBean.setIgnoreWarnings(true);
%>
		<jsp:forward page="EnvelopePropertiesProcessing.jsp" />
<%
	} else if (theAction.equals("cancel")) {
%>
		<jsp:forward page="EnvelopeProperties.jsp" />
<%
    } else {
		// Problem
		throw new PnetException("Unknown action passed to TransitionProblemProcessing: " + theAction);
	}
%>
<%-- End of action handling --%>
