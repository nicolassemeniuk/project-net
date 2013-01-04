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
    info="MainProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager"
%>
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<%
	// Abort Envelope
	if ((request.getParameter("theAction") != null) && request.getParameter("theAction").equals("abortEnvelope")
			&& request.getParameter("id") != null){
		envelopeManagerBean.abortTransition(request.getParameter("id"));
	}
	String refUrl = request.getParameter("referingURL");
	if(refUrl == null ) refUrl = SessionManager.getJSPRootURL() + "/personal/Main.jsp";
	response.sendRedirect(refUrl);
%>