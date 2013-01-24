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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
| Workflow Step List JSP
+----------------------------------------------------------------------*/
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Step List" 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.PnetException,
			net.project.base.property.PropertyProvider,
			net.project.security.*,
			net.project.space.*,
			net.project.workflow.*"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" /> 
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" /> 
<%
	workflowBean.clear();
    workflowBean.setUser(user);
    workflowBean.setSpace(user.getCurrentSpace());

	if (request.getParameter("id") != null && !request.getParameter("id").equals("")) {
        workflowBean.setID(request.getParameter("id"));
        workflowBean.load();
		managerBean.setCurrentWorkflowID(workflowBean.getID());
		managerBean.setCurrentWorkflowName(workflowBean.getName());
	} else if (managerBean.getCurrentWorkflowID() != null) {
		workflowBean.setID(managerBean.getCurrentWorkflowID());
        workflowBean.load();
		managerBean.setCurrentWorkflowName(workflowBean.getName());
	} else {
		throw new PnetException("Missing parameter 'id' in StepList.jsp");
	}
%>
<security:verifyAccess objectID="<%=workflowBean.getID()%>"
					   action="modify"
					   module="<%=net.project.base.Module.WORKFLOW%>"
/> 
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/standard_prototypes.js" />

<script language="javascript" type="text/javascript">

	window.history.forward(-1);
	var theForm;
	var theStepForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms[0];
	theStepForm = self.document.forms[1];
	isLoaded = true;
}

function submit() {
	theForm.theAction.value = "submit";
	theForm.submit();
}

function cancel() {
	self.document.location = JSPRootURL + "/workflow/WorkflowDesigner.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>";
}

function reset() { 
	self.document.location = JSPRootURL + "/workflow/StepList.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=workflowBean.getID()%>";
}

function tabClick(nextPage) {
	if (theForm.nextPage) {
		theForm.nextPage.value = nextPage;
		submit();
	}
}

// Functions that will be called by the Included StepListInclude page.
function stepCreate() {
	theStepForm.theAction.value = "step_create";
	theStepForm.submit();
}
function stepModify(id) {
	if (arguments.length != 0) {
		// We got an ID to modify
		aRadio = theStepForm.step_id;
		if (aRadio) {
			for (i = 0; i < aRadio.length; i++) {
				if (aRadio[i].value == id) aRadio[i].checked = true;
			}
		}
	}
	if (isSelected(theStepForm.step_id)) {
		theStepForm.theAction.value = "step_modify";
		theStepForm.submit();
	}
}
function stepRemove() {
	if (isSelected(theStepForm.step_id)) {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.workflow.steplist.remove.message")%>', function(btn) { 
			if(btn == 'yes'){ 
		 		theStepForm.theAction.value = "step_remove";
				theStepForm.submit();
			}else{
				return false;
			}
	});
  }
}
function stepProperties() {
	if (isSelected(theStepForm.step_id)) {
		theStepForm.theAction.value = "step_properties";
		theStepForm.submit();
	}
}

function isSelected(aList) {
	if (aList) {
		if (aList.checked) return true;
		for (i = 0; i < aList.length; i++) {
			if (aList[i].checked) return true;
		}
	}
	return false;
}

function getSelected(aList) {
	if (aList) {
		if (aList.checked) return aList.value;
		for (i = 0; i < aList.length; i++) {
			if (aList[i].checked) return aList[i].value;
		}
	}
	return null;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_designer&section=steplist";
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
			<history:page displayToken="@prm.workflow.steplist.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/StepList.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.WORKFLOW + "&action=" + net.project.security.Action.MODIFY + "&id=" + workflowBean.getID()%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" function="javascript:stepCreate();" labelToken="prm.workflow.step.link.newstep"/>
		<tb:button type="modify" function="javascript:stepModify();" labelToken="prm.workflow.step.link.editstep"/>
		<tb:button type="remove" function="javascript:stepRemove();" labelToken="prm.workflow.step.link.deletestep"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/StepListProcessing.jsp">
	<input type="hidden" name="theAction" />
	<input type="hidden" name="nextPage" />
	<input type="hidden" name="id" value='<jsp:getProperty name="workflowBean" property="ID" />' />
	<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
	<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
</form>
<%-- Page Header --%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td class="pageTitle" align="left"><%=PropertyProvider.get("prm.workflow.edit.designer.pagetitle")%></td>
        <td class="pageTitle" align="right"><jsp:getProperty name="workflowBean" property="name" /></td>
    </tr>
	<tr><td colspan="2">&nbsp;</td></tr>
</table>
<tab:tabStrip>
	<tab:tab labelToken="@prm.workflow.create.definition.tab" href="javascript:tabClick('WorkflowEdit.jsp');" />
	<tab:tab labelToken="@prm.workflow.create.steps.tab" href="javascript:tabClick('StepList.jsp');" selected="true" />
	<tab:tab labelToken="@prm.workflow.create.transitions.tab" href="javascript:tabClick('TransitionList.jsp');" />
	<tab:tab labelToken="@prm.workflow.create.publish.tab" href="javascript:tabClick('WorkflowPublish.jsp');" />
</tab:tabStrip>
<br />
<form method="post" action="<session:getJSPRootURL />/workflow/StepListProcessing.jsp">
	<input type="hidden" name="theAction" />
	<input type="hidden" name="id" value='<jsp:getProperty name="workflowBean" property="ID" />' />
	<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
	<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
	<jsp:include page="include/StepList.jsp" flush="true" />
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
