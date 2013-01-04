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
| Step Create - for creating a new Step.
| Assumes workflowID already set for step
+----------------------------------------------------------------------*/
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Create Step" 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*,
			net.project.space.*,
			net.project.workflow.*"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" /> 
<jsp:useBean id="stepBean" class="net.project.workflow.StepBean" scope="session" />
<jsp:useBean id="domainList" class="net.project.workflow.DomainListBean" scope="page" />
<%
	domainList.setSpace(user.getCurrentSpace());
    stepBean.setUser(user);
    stepBean.setSpace(user.getCurrentSpace());
	stepBean.setWorkflowID(managerBean.getCurrentWorkflowID());
%>
<security:verifyAccess objectID="<%=stepBean.getWorkflowID()%>"
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
	stepTypeInit();
	// Set the entry status dropdown list to the current value
	selectByValue(theForm.entryStatusID, '<jsp:getProperty name="stepBean" property="entryStatusID" />');
}

function cancel() {
	self.document.location = JSPRootURL + '/workflow/StepList.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&id=<jsp:getProperty name="stepBean" property="workflowID" />';
}

function reset() { 
	self.document.location = JSPRootURL + 
							 "/workflow/StepCreate.jsp" + 
							 '?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&id=<jsp:getProperty name="stepBean" property="workflowID" />';
}

function submit() {
	theAction("submit");
	theForm.submit();
}

// Initialize the stepType radio group based on the two flags
function stepTypeInit() {
	var radio = theForm.elements["pres_stepType"];
	var initialStep = theForm.elements["initialStep"];
	var finalStep = theForm.elements["finalStep"];
	
	radio[0].checked = true;
	if (initialStep.value == "true") {
		radio[1].checked = true;
	} else if (finalStep.value == "true") {
		radio[2].checked = true;
	}
}

function stepTypeChange(theValue) {
	var initialStep = theForm.elements["initialStep"];
	var finalStep = theForm.elements["finalStep"];
	
	if (theValue == "0") {
		initialStep.value = "false";
		finalStep.value = "false";
	} else if (theValue == "1") {
		initialStep.value = "true";
		finalStep.value = "false";
	} else if (theValue =="2") {
		initialStep.value = "false";
		finalStep.value = "true";
	}
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
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_designer&section=step_create";
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
			<history:page level="1" displayToken="@prm.workflow.stepcreate.module.history"
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/StepCreate.jsp"%>'
						  queryString='module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/StepCreateProcessing.jsp">
<input type="hidden" name="theAction" />
<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value='<jsp:getProperty name="stepBean" property="workflowID" />' />
<%-- Page Header --%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td class="pageTitle" align="left"><%=PropertyProvider.get("prm.workflow.edit.designer.pagetitle")%></td>
        <td class="pageTitle" align="right"><%=PropertyProvider.get("prm.workflow.stepcreate.pagetitle")%></td>
    </tr>
</table>
<%-- Step Create Form --%> 
<input type="hidden" name="spaceID" value='<%=user.getCurrentSpace().getID()%>' />
<%--bfd:2733 AVINASH: initialising the values from bean -Start- --%>
<input type="Hidden" name="initialStep" value="<jsp:getProperty name="stepBean" property="initialStep" />" />
<input type="Hidden" name="finalStep" value="<jsp:getProperty name="stepBean" property="finalStep" />" />
<%--bfd:2733 AVINASH: initialising the values from bean -End- --%>
<table border="0" width="100%" cellspacing="0" cellpadding="2">
    <tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<%-- Display any problems --%>
		<td colspan="3"><%=stepBean.getErrorsTable()%></td>
	</tr>
    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=stepBean.getFlagError("name", PropertyProvider.get("prm.workflow.stepcreate.name.label"))%></td>
      <td  align="left" nowrap> 
        <input type="text" name="name" maxlength="80" size="30" value='<c:out value="${name}"/>' />
      </td>
      <td nowrap width="25%" />
    </tr>
        <tr>
      <td  align="left" nowrap class="fieldRequired"><%=stepBean.getFlagError("sequence", PropertyProvider.get("prm.workflow.stepcreate.sequence.label"))%></td>
      <td  align="left" nowrap>
        <input type="text" name="sequence" maxlength="4" size="4" />
      </td>
      <td nowrap width="25%" />
    </tr>
    <tr>
      <td  align="left" nowrap colspan="2" class="fieldNonRequired"><%=stepBean.getFlagError("description", PropertyProvider.get("prm.workflow.stepcreate.description.label"))%><br>
        <textarea name="description" cols="60" rows="3"><c:out value="${description}"/></textarea>
      </td>
      <td nowrap width="25%" /> 
    </tr>
    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=stepBean.getFlagError("type", PropertyProvider.get("prm.workflow.stepcreate.type.label"))%></td>
      <td  align="left" nowrap>
		<input name="pres_stepType" id="stepTypeDefault" type="radio" value="0" checked onClick="javascript:stepTypeChange(this.value)"><%=PropertyProvider.get("prm.workflow.stepcreate.type.option.default.name")%></input>
		<input name="pres_stepType" id="stepTypeInitialStep" type="radio" value="1" onClick="javascript:stepTypeChange(this.value)"><%=PropertyProvider.get("prm.workflow.stepcreate.type.option.initial.name")%></input>
		<input name="pres_stepType" id="stepTypeFinal" type="radio" value="2" onClick="javascript:stepTypeChange(this.value)"><%=PropertyProvider.get("prm.workflow.stepcreate.type.option.final.name")%></input>
      </td>
      <td nowrap width="25%" /> 
    </tr>
    <tr> 
      <td  align="left" nowrap class="fieldNonRequired"><%=stepBean.getFlagError("entry_status_id", PropertyProvider.get("prm.workflow.stepcreate.status.label"))%></td>
      <td  align="left" nowrap>
        <select name="entryStatusID">
		    <option value=""><%=PropertyProvider.get("prm.workflow.stepcreate.status.option.name")%></option>
			<jsp:setProperty name="domainList" property="stylesheet" value="/workflow/xsl/status_list_option.xsl" />
			<jsp:getProperty name="domainList" property="statusListPresentation" />
        </select>
      </td>
      <td nowrap width="25%" /> 
    </tr>
    <tr> 
      <td align="left" nowrap colspan="2" class="fieldNonRequired"><%=stepBean.getFlagError("notes", PropertyProvider.get("prm.workflow.stepcreate.instructions.label"))%><br>
          <textarea name="notes" cols="60" rows="3" wrap><c:out value="${notes}"/></textarea>
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
<%stepBean.clearErrors();%>