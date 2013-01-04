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
| Start of Envelope Wizard
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="EnvelopeWizardStart.jsp  Omits no output." 
    language="java" 
    errorPage="../WorkflowErrors.jsp"
    import="net.project.security.*, net.project.workflow.*, net.project.base.PnetException" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<%
	envelopeManagerBean.setSpace(user.getCurrentSpace());
	envelopeManagerBean.setUser(user);

	String objectID = null;
	if (request.getParameter("id") != null) {
		objectID = request.getParameter("id");
    } else if (request.getAttribute("id") != null) {
		objectID = (String) request.getAttribute("id");
	}
	if (objectID != null) {
		/* Create new envelope */
		Envelope envelope = new Envelope();
		envelope.addObject(objectID);
		envelope.setUser(user);
		envelopeManagerBean.setCurrentEnvelope(envelope);
	} else {
		throw new PnetException("Missing parameter 'id' to EnvelopeWizardStart.jsp");
	}
%>
	<%-- Go to first page of wizard --%>
	<jsp:forward page="EnvelopeWizardPage1.jsp" />
	
