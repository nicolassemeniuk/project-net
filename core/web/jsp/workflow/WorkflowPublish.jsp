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
| Publish a workflow
+----------------------------------------------------------------------*/
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Publish Workflow" 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.*,
			net.project.security.*,
			net.project.workflow.*,
			net.project.base.PnetException" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" /> 
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" /> 
<%
	workflowBean.clear();
    workflowBean.setUser(user);
    workflowBean.setSpace(user.getCurrentSpace());

	if (request.getParameter("id") != null) {
        workflowBean.setID(request.getParameter("id"));
        workflowBean.load();
		managerBean.setCurrentWorkflowID(workflowBean.getID());
		managerBean.setCurrentWorkflowName(workflowBean.getName());
	} else if (managerBean.getCurrentWorkflowID() != null) {
		workflowBean.setID(managerBean.getCurrentWorkflowID());
        workflowBean.load();
		managerBean.setCurrentWorkflowName(workflowBean.getName());
	} else {
		throw new PnetException("Missing parameter 'id' in WorkflowPublish.jsp");
	}
		
	/* Set up values for button in action bar */
	String labelToken = "";
	String function = "";
	if (workflowBean.isPublished()) {
		labelToken = "@prm.workflow.publish.unpublish.label";
		function = "javascript:unpublish();";
	} else {
		labelToken = "@prm.workflow.publish.publish.label";
		function = "javascript:publish();";
	}
%> 
<security:verifyAccess objectID='<%=workflowBean.getID()%>'
					   action="modify"
					   module="<%=net.project.base.Module.WORKFLOW%>"
/> 
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/standard_prototypes.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
    theForm = self.document.forms[0];
	isLoaded = true;
}

function submit() {
	theAction("submit");
	theForm.submit();
}

function cancel() {
	self.document.location = JSPRootURL + "/workflow/WorkflowDesigner.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>";
}

function reset() { 
	self.document.location = JSPRootURL + "/workflow/WorkflowPublish.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=workflowBean.getID()%>";
}

function tabClick(nextPage) {
	if (theForm.nextPage) {
		theForm.nextPage.value = nextPage;
		submit();
	}
}

// Publish the workflow
function publish() {
	theAction("publish");
	theForm.submit();
}

// Unpublish the workflow
function unpublish() {
	theAction("unpublish");
	theForm.submit();
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_designer&section=publish";
	openwin_help(helplocation);
}

</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.workflow.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page displayToken="@prm.workflow.publish.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/WorkflowPublish.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.WORKFLOW + "&action=" + net.project.security.Action.MODIFY + "&id=" + workflowBean.getID()%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/WorkflowPublishProcessing.jsp">
	<input type="hidden" name="theAction" />
	<input type="hidden" name="id" value='<jsp:getProperty name="workflowBean" property="ID" />' />
	<input type="hidden" name="nextPage" />
	<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
	<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<%-- Page Header --%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td class="pageTitle" align="left"><%=PropertyProvider.get("prm.workflow.edit.designer.pagetitle")%></td>
        <td class="pageTitle" align="right"><%=workflowBean.getName()%></td>
    </tr>
	<tr><td colspan="2">&nbsp;</td></tr>
</table>
<tab:tabStrip>
	<tab:tab labelToken="@prm.workflow.create.definition.tab" href="javascript:tabClick('WorkflowEdit.jsp');" />
	<tab:tab labelToken="@prm.workflow.create.steps.tab" href="javascript:tabClick('StepList.jsp');"/>
	<tab:tab labelToken="@prm.workflow.create.transitions.tab" href="javascript:tabClick('TransitionList.jsp');" />
	<tab:tab labelToken="@prm.workflow.create.publish.tab" href="javascript:tabClick('WorkflowPublish.jsp');" selected="true" />
</tab:tabStrip>
<br />
<table border="0" width="100%" cellspacing="0" cellpadding="2">
    <tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<%-- Display any problems with publishing / unpublishing the workflow --%>
		<td colspan="3"><%=workflowBean.getErrorsTable()%></td>
	</tr>
    <tr> 
	  <td>&nbsp;</td>
	  <td>
	  	<jsp:setProperty name="workflowBean" property="stylesheet" value="/workflow/xsl/workflow_publish.xsl" />
		<jsp:getProperty name="workflowBean" property="propertiesPresentation" />
	  </td>
	  <td>&nbsp;</td>
    </tr>
    <tr><td colspan="3">&nbsp;</td></tr>
</table>

<!-- Action Bar -->
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" labelToken="<%=labelToken%>" function="<%=function%>" />
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
<%workflowBean.clearErrors();%>

