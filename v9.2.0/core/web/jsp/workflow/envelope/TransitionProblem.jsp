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
    info="TransitionProblem.jsp  Omits no output." 
    language="java" 
    errorPage="../WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.workflow.*, 
			net.project.project.*, 
			net.project.security.*,
			net.project.base.PnetException,
			net.project.space.Space" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<%
	String transitionID = request.getParameter("transitionID");
%>
<html>
<head>
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var page = false;
    var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';  

function setup() {
	page=true;
	theForm = self.document.forms[0];
	isLoaded = true;
}

function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=workflow_envelope&section=transition_errors";
	openwin_help(helplocation);
}

// These functions called from buttons in the output generated by the stylesheet
function cancelTransition() {
	theAction("cancel");
	theForm.submit();
}

function continueTransition() {
	theAction("continue");
	theForm.submit();
}
</script>
</head>
<body class="main" onLoad="setup();">
<form method="post" action="<session:getJSPRootURL />/workflow/envelope/TransitionProblemProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="transitionID" value="<%=transitionID%>">
<br />
<div align="center">
<table border="0" cellspacing="0" cellpadding="0" width="75%">
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<session:getJSPRootURL />/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.workflow.envelope.transitionproblem.channel.title")%></th>
		<th class="channelHeader" align="right">&nbsp;</th>
        <th class="channelHeader" align="right" width="1%"><img src="<session:getJSPRootURL />/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
	<tr><td>&nbsp;</td></tr>
    <tr>
        <td colspan="4">
			<jsp:setProperty name="envelopeManagerBean" property="stylesheet" value="/workflow/envelope/xsl/rule_problem_list.xsl" />
			<jsp:getProperty name="envelopeManagerBean" property="ruleProblemsPresentation" />
        </td>
    </tr>
</table>
</div>
</form>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>                                                                                      
