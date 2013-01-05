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
|   $Revision: 19724 $
|       $Date: 2009-08-12 09:14:00 -0300 (mié, 12 ago 2009) $
|     $Author: uroslates $
|
| List of transitions belonging to a workflow.
+----------------------------------------------------------------------*/
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Transition List" 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.space.*,
			net.project.security.*,
			net.project.workflow.*,
			net.project.base.PnetException,
			net.project.base.property.PropertyProvider" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" /> 
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" /> 
<jsp:useBean id="transitionBean" class="net.project.workflow.TransitionBean" scope="session" />
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
		throw new PnetException("Missing parameter 'id' in TransitionList.jsp");
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

	window.history.forward(-1);
	
	var theForm;
	var theTransitionForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	var selectWin;		// Popup window
	
function setup() {
	theForm = self.document.forms[0];
	theTransitionForm = self.document.forms[1];
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
	self.document.location = JSPRootURL + "/workflow/TransitionList.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=workflowBean.getID()%>";
}

function tabClick(nextPage) {
	if (theForm.nextPage) {
		theForm.nextPage.value = nextPage;
		submit();
	}
}

// Functions that will be called by the included TransitionList page
function transitionCreate() {
	theTransitionForm.theAction.value = "transition_create";
	theTransitionForm.submit();
}

function transitionModify(id) {
	if (arguments.length != 0) {
		// We got an ID to modify
		aRadio = theTransitionForm.transition_id;
		if (aRadio) {
			for (i = 0; i < aRadio.length; i++) {
				if (aRadio[i].value == id) aRadio[i].checked = true;
			}
		}
	}
	if (isSelected(theTransitionForm.transition_id)) {
		theTransitionForm.theAction.value = "transition_modify";
		theTransitionForm.submit();
	}
}
function transitionRemove() {
	if (isSelected(theTransitionForm.transition_id)) {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>','<%=PropertyProvider.get("prm.workflow.transitionlist.remove.message")%>', function(btn){
			if( btn == 'yes' ){
				theTransitionForm.theAction.value = "transition_remove";
				theTransitionForm.submit();
			} else {
				return false; 
			}
		});
	}
}
function transitionProperties() {
	if (isSelected(theTransitionForm.transition_id)) {
		theTransitionForm.theAction.value = "transition_properties";
		theTransitionForm.submit();
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
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_designer&section=transition_list";
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
			<history:page displayToken="@prm.workflow.transitionlist.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/TransitionList.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.WORKFLOW + "&action=" + net.project.security.Action.MODIFY + "&id=" + workflowBean.getID()%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" function="javascript:transitionCreate();" labelToken="prm.workflow.transition.link.newtransition"/>
		<tb:button type="modify" function="javascript:transitionModify();" labelToken="prm.workflow.transition.link.edittransition"/>
		<tb:button type="remove" function="javascript:transitionRemove();" labelToken="prm.workflow.transition.link.deletetransition"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/TransitionListProcessing.jsp">
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
	<!-- error block -->
	<tr><td colspan="2"><%= transitionBean.getErrorsTable()%></td></tr>
	<tr><td colspan="2">&nbsp;</td></tr>
</table>
<tab:tabStrip>
	<tab:tab labelToken="@prm.workflow.create.definition.tab" href="javascript:tabClick('WorkflowEdit.jsp');" />
	<tab:tab labelToken="@prm.workflow.create.steps.tab" href="javascript:tabClick('StepList.jsp');"/>
	<tab:tab labelToken="@prm.workflow.create.transitions.tab" href="javascript:tabClick('TransitionList.jsp');" selected="true" />
	<tab:tab labelToken="@prm.workflow.create.publish.tab" href="javascript:tabClick('WorkflowPublish.jsp');" />
</tab:tabStrip>
<br />
<form method="post" action="<session:getJSPRootURL />/workflow/TransitionListProcessing.jsp">
	<input type="hidden" name="theAction" />
	<input type="hidden" name="id" value='<jsp:getProperty name="workflowBean" property="ID" />' />
	<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
	<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
	<jsp:include page="include/WorkflowTransitionList.jsp" flush="true" />
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
