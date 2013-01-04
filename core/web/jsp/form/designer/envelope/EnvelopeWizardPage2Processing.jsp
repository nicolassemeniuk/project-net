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
|   $Revision: 15404 $
|       $Date: 2006-08-28 20:20:09 +0530 (Mon, 28 Aug 2006) $
|     $Author: deepak $
|
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="EnvelopeWizardPage2Processing.jsp  Omits no output." 
    language="java" 
    errorPage="../WorkflowErrors.jsp"
    import="net.project.security.*, net.project.workflow.*, net.project.base.PnetException" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<%
    String theAction = request.getParameter("theAction");

    Envelope envelopeBean = envelopeManagerBean.getCurrentEnvelope();
    pageContext.setAttribute("envelope", envelopeBean, PageContext.PAGE_SCOPE);
%>
<jsp:useBean id="envelope" type="net.project.workflow.Envelope" scope="page" />
<%
    if (theAction.equals("finish")) {
%>
		<%-- Set all properties --%>
		<jsp:setProperty name="envelope" property="*" />
		<%
			// bfd - 2994 issue
			if ((request.getParameter("comments") == null) || (request.getParameter("comments").equals(""))) {
				envelope.setComments("");
			}
		%>
<%
		envelope.clearErrors();
		envelope.validateStrictnessID();
		envelope.validatePriorityID();
		envelope.validateStatusID();
		envelope.validateComments();
		if (envelope.hasErrors()) {
%>
			<jsp:forward page="EnvelopeWizardPage2.jsp" />
<%
		} else {
			/* Store changes */
			envelopeManagerBean.createEnvelope();
%>
			<jsp:forward page="EnvelopeWizardEnd.jsp" />
<%
		}
%>
<%
	} else if (theAction.equals("back")) {
%>
		<%-- Set all properties, but no error checking --%>
		<jsp:setProperty name="envelope" property="*" />
		<%
			/// bfd - 2994 
			if((request.getParameter("comments")==null)||(request.getParameter("comments").equals(""))) {
				envelope.setComments("");
			}
		%>
		<jsp:forward page="EnvelopeWizardPage1.jsp" />
<%
    } else if (theAction.equals("cancel")) {
		/* Simply close the window */
%>
		<jsp:forward page="EnvelopeWizardEnd.jsp" />
<%
    } else {
		// Problem
		throw new PnetException("Unknown action passed to EnvelopeWizardPage1Processing: " + theAction);
	}
%>
<%-- End of action handling --%>
