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
    info="EnvelopePropertiesProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="../WorkflowErrors.jsp"
    import="net.project.security.*, net.project.workflow.*, net.project.base.PnetException,
			net.project.form.Form,
			net.project.util.StringUtils" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<%
	String comments = request.getParameter("comments");
	Envelope envelopeBean = envelopeManagerBean.getCurrentEnvelope();
	envelopeBean.setComments((StringUtils.isEmpty(comments) || "null".equals(comments) ) ? "" : comments);
    pageContext.setAttribute("envelope", envelopeBean, PageContext.PAGE_SCOPE);
	String theAction = request.getParameter("theAction");

    // Attempt to perform the transition.  "continue" is the 2nd time through when user
    // is overriding weak rule enforcement from TransitionProblemProcessing.jsp.
    if (theAction.equals("transition_perform") || theAction.equals("continue")) {
		// Perform a transition
		String transitionID = request.getParameter("transitionID");
		// Do not ignore warnings when the user initiates a transition.  If continuing, we allow the ignore.
		if (theAction.equals("transition_perform"))
            envelopeManagerBean.setIgnoreWarnings(false);
%>
        <jsp:useBean id="envelope" type="net.project.workflow.Envelope" scope="page" />
		<jsp:setProperty name="envelope" property="comments" />
<%
		try {
			envelopeManagerBean.performTransition(transitionID);
		} catch (RuleCheckException e) {
			pageContext.forward("TransitionProblem.jsp");
		}
		
		// At this point, transition successfully performed.
		// Now store any potential objects
		if (request.getParameter("submitObject") != null && !request.getParameter("submitObject").equals("")) {
			if (request.getParameter("submitObject").equals(net.project.base.ObjectType.FORM_DATA)) {
				%><jsp:useBean id="form" class="net.project.form.Form" scope="session" /><%
				form.processHttpPost(request);
				form.storeData();
			}
		}

		// Now back to the page from where the transition was performed 
		if ((request.getParameter("fromPage")) != null && request.getParameter("fromPage").equals("PropertiesDetail")) {
			pageContext.forward("EnvelopePropertiesDetail.jsp");
		} else {
			pageContext.forward("EnvelopeProperties.jsp");
		}

	} else if (theAction.equals("properties")) {
		// Detailed properties
		pageContext.forward("EnvelopePropertiesDetail.jsp");
    } else {
		// Problem
		throw new PnetException("Unknown action passed to EnvelopePropertiesProcessing: " + theAction);
	}
%>
<%-- End of action handling --%>
