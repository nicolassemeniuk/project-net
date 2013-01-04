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
| Displays envelope properties detailed view
+----------------------------------------------------------------------*/
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Envelope Properties -- Detailed" 
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
		throw new PnetException("Missing parameter 'id' to EnvelopePropertiesDetail.jsp");
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
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms[0];
	theTransitionForm = self.document.forms["transitionForm"];
	isLoaded = true;
}

function reset() { 
	self.document.location = JSPRootURL + "/workflow/envelope/EnvelopePropertiesDetail.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.VIEW%>&id=<%=envelopeBean.getID()%>";
}

// Called from TransitionsSelect page
function performTransition(transitionID) {
	theTransitionForm.transitionID.value = transitionID;
	theTransitionForm.theAction.value = "transition_perform";
	theTransitionForm.submit();
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_envelope&section=properties_details";
	openwin_help(helplocation);
}

</script>

</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%-- Transition portion --%>
<form name="transitionForm" method="post" action="<session:getJSPRootURL />/workflow/envelope/EnvelopePropertiesProcessing.jsp">
	<input type="hidden" name="theAction" />
	<input type="hidden" name="transitionID" />
	<input type="hidden" name="fromPage" value="PropertiesDetail">
</form>

<tb:toolbar style="tooltitle" groupTitle="prm.global.tool.workflow.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page level="1" displayToken="@prm.workflow.envelope.propertiesdetail.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/envelope/EnvelopePropertiesDetail.jsp?module=" + net.project.base.Module.WORKFLOW + "&action=1"%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/envelope/EnvelopePropertiesDetailProcessing.jsp">
	<input type="hidden" name="theAction">
	<jsp:include page="include/EnvelopeProperties.jsp" flush="true">
		<jsp:param name="mode" value="detailed" />
	</jsp:include>
</form>
<br />
<%-- History --%>
<jsp:include page="include/HistoryList.jsp" flush="true" />

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>

