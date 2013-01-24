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

<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Envelope Properties" 
    language="java" 
    errorPage="../WorkflowErrors.jsp"
    import="net.project.base.PnetException,
			net.project.base.property.PropertyProvider,
			net.project.security.*, 
			net.project.security.AuthorizationFailedException,
			net.project.space.Space,
			net.project.project.*, 
			net.project.workflow.*"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<jsp:useBean id="envelopeBean" class="net.project.workflow.EnvelopeBean" scope="request" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<% 
	envelopeBean.clear();
	envelopeBean.setUser(user);
	envelopeBean.setSpace(user.getCurrentSpace());

	String envelopeID = null;
	if (request.getParameter("id") != null) {
		envelopeID = request.getParameter("id");
	} else if (envelopeManagerBean.getCurrentEnvelope() != null) {
		envelopeID = envelopeManagerBean.getCurrentEnvelope().getID();
	} else {
		throw new PnetException("Missing parameter 'id' to EnvelopeProperties.jsp");
	}
	envelopeBean.setID(envelopeID);
	envelopeBean.load();
	envelopeManagerBean.setCurrentEnvelope(envelopeBean);
%>
<%
	/* Security Check: If not at current step and not an admin, no access */
	if (!envelopeManagerBean.isUserAtCurrentStep(envelopeID, user.getID()) &&
		!securityProvider.isUserSpaceAdministrator()) {
		throw new AuthorizationFailedException(PropertyProvider.get("prm.workflow.envelope.properties.message"));
	}
	/* End of Security */
%>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var theTransitionForm;
	var theContentForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms["propertiesForm"];
	theTransitionForm = self.document.forms["transitionForm"];
	theContentForm = self.document.forms["contentForm"];
	isLoaded = true;
}

function reset() { 
	self.document.location = JSPRootURL + "/workflow/envelope/EnvelopeProperties.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.VIEW%>&id=<%=envelopeBean.getID()%>";
}

function properties() {
	theAction("properties");
	theForm.submit();
}

// Called from TransitionsSelect page
function performTransition(transitionID) {
	theTransitionForm.transitionID.value = transitionID;
	theTransitionForm.theAction.value = "transition_perform";
	theTransitionForm.submit();
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_envelope&section=properties";
	openwin_help(helplocation);
}
</script>

</head>
<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.global.tool.workflow.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module displayToken="@prm.workflow.envelope.properties.workflow.module.history" />
			<history:page displayToken="@prm.workflow.envelope.properties.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/envelope/EnvelopeProperties.jsp?module=" + net.project.base.Module.WORKFLOW + "&action=1"%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
	</tb:band>
</tb:toolbar>

<div id='content'>

<%-- Envelope Properties --%>
<form name="propertiesForm" method="post" action="<session:getJSPRootURL />/workflow/envelope/EnvelopePropertiesProcessing.jsp">
	<input type="hidden" name="theAction">
	<jsp:include page="include/EnvelopeProperties.jsp" flush="true" />
</form>
<br />
<%-- Transition portion --%>
<form name="transitionForm" method="post" action="<session:getJSPRootURL />/workflow/envelope/EnvelopePropertiesProcessing.jsp">
	<input type="hidden" name="theAction" />
	<input type="hidden" name="transitionID" />
	<jsp:include page="include/TransitionSelect.jsp" flush="true" />
	<br />
	<%-- Contents of envelope --%>
	<jsp:include page="include/EnvelopeContent.jsp" flush="true">
		<jsp:param name="htmlFormName" value="transitionForm" />
	</jsp:include>
</form>
<br />
<%-- Envelope version information --%>
<jsp:include page="include/EnvelopeVersionList.jsp" flush="true" />

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>