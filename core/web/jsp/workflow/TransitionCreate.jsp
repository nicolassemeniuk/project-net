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
|   $Revision: 19630 $
|       $Date: 2009-08-05 10:00:19 -0300 (mié, 05 ago 2009) $
|     $Author: dpatil $
|
| Transition Create - for creating a new Transition.
| Assumes workflowID and beginStepID already set for Transition.
+----------------------------------------------------------------------*/
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Create Transition" 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.*,
			net.project.security.*,
			net.project.workflow.*" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="transitionBean" class="net.project.workflow.TransitionBean" scope="session" />
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" /> 
<%
    transitionBean.setUser(user);
    transitionBean.setSpace(user.getCurrentSpace());
	transitionBean.setWorkflowID(managerBean.getCurrentWorkflowID());
%> 
<security:verifyAccess objectID="<%=transitionBean.getWorkflowID()%>"
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
	selectByValue(theForm.beginStepID, '<jsp:getProperty name="transitionBean" property="beginStepID" />');	
	selectByValue(theForm.endStepID, '<jsp:getProperty name="transitionBean" property="endStepID" />');	
	setFunctionForOnSubmitEventOfForm();
}

function cancel() {
	self.document.location = JSPRootURL + '/workflow/TransitionList.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&id=<jsp:getProperty name="transitionBean" property="workflowID" />';
}

function reset() { 
	self.document.location = JSPRootURL + 
							 "/workflow/TransitionCreate.jsp" + 
							 '?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&id=<jsp:getProperty name="transitionBean" property="workflowID" />';
}

function submit() {
	theAction("submit");
	theForm.submit();
}

/*
  Select a specific entry in a dropdown (select list)
  theSelect - select object =
  theValue - value of the option to select
  e.g.
  	selectByValue(theForm.ownerID, '100');
  of course, the value is most likey going to come from a bean property
 */
function selectByValue(theSelect, theValue) {
	if (theSelect) {
		for (i = 0; i < theSelect.options.length; i++) {
			if (theSelect.options[i].value == theValue) {
				theSelect.options[i].selected = true;
			}
		}
	}
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_designer&section=transition_create";
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
			<history:page level="1" displayToken="@prm.workflow.transitioncreate.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/TransitionCreate.jsp"%>'
						  queryString='module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/TransitionCreateProcessing.jsp">
	<input type="hidden" name="theAction" />
	<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
	<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
	<input type="hidden" name="id" value='<jsp:getProperty name="transitionBean" property="workflowID" />' />
<%-- Page Header --%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td class="pageTitle" align="left"><%=PropertyProvider.get("prm.workflow.edit.designer.pagetitle")%></td>
        <td class="pageTitle" align="right"><%=PropertyProvider.get("prm.workflow.transitioncreate.pagetitle")%></td>
    </tr>
</table>
<%-- Transition Create Form --%> 
<table border="0" width="100%" cellspacing="0" cellpadding="2">
    <tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<%-- Display any problems --%>
		<td colspan="3"><%=transitionBean.getErrorsTable()%></td>
	</tr>
    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=transitionBean.getFlagError("transition_verb", PropertyProvider.get("prm.workflow.transitioncreate.name.label"))%></td>
      <td  align="left" nowrap> 
        <input type="text" name="transitionVerb" maxlength="80" size="30" value='<c:out value="${transitionVerb}"/>' />
      </td>
      <td nowrap width="25%" /> 
    </tr>
    <tr> 
      <td  align="left" nowrap colspan="2" class="fieldNonRequired"><%=transitionBean.getFlagError("description", PropertyProvider.get("prm.workflow.transitioncreate.description.label"))%><br>
        <textarea name="description" cols="60" rows="3"><c:out value="${description}"/></textarea>
      </td>
      <td nowrap width="25%" /> 
    </tr>
<%--    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=transitionBean.getFlagError("begin_step_id", "From Step:")%></td>
      <td  align="left" nowrap>
		<jsp:getProperty name="managerBean" property="currentStepName" />
      </td>
      <td nowrap width="25%" /> 
    </tr> --%>
    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=transitionBean.getFlagError("begin_step_id", PropertyProvider.get("prm.workflow.transitioncreate.fromstep.label"))%></td>
      <td  align="left" nowrap>
        <select name="beginStepID">
		    <option value=""></option>
			<jsp:setProperty name="managerBean" property="stylesheet" value="/workflow/xsl/step_list_option.xsl" />
			<%=managerBean.getStepsPresentation(transitionBean.getWorkflowID())%>
        </select>
      </td>
      <td nowrap width="25%" /> 
    </tr>
    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=transitionBean.getFlagError("end_step_id", PropertyProvider.get("prm.workflow.transitioncreate.tostep.label"))%></td>
      <td  align="left" nowrap>
	  	<!-- get to step list -->
        <select name="endStepID">
		    <option value=""></option>
			<jsp:setProperty name="managerBean" property="stylesheet" value="/workflow/xsl/step_list_option.xsl" />
			<%=managerBean.getStepsPresentation(transitionBean.getWorkflowID())%>
        </select>
      </td>
      <td nowrap width="25%" /> 
    </tr>
    <tr> 
      <td nowrap colspan="3">&nbsp;</td>
    </tr>
</table>

<!-- Action Bar -->
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" />
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
<%transitionBean.clearErrors();%>
