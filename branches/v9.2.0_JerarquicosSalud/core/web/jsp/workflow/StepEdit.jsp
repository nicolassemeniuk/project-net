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
|   $Revision: 20748 $
|       $Date: 2010-04-23 12:43:10 -0300 (vie, 23 abr 2010) $
|     $Author: umesha $
|
| Step Edit - for modifying an existing Step.
+----------------------------------------------------------------------*/
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Edit Step" 
    language="java" 
    errorPage="/workflow/WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager,
			net.project.base.PnetException,
			net.project.util.HTMLUtils" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" /> 
<jsp:useBean id="stepBean" class="net.project.workflow.StepBean" scope="session" />
<jsp:useBean id="domainList" class="net.project.workflow.DomainListBean" scope="page" />
<%
	// Determine edit mode: create or modify (default)
	String mode = "modify";
	if (request.getParameter("mode") != null && !request.getParameter("mode").equals("")) {
		mode = request.getParameter("mode");
	}
	
	// Determine mode for Step groups.  "view" or "modify"
	String groupMode = null;
	if (request.getParameter("groupMode") != null && !request.getParameter("groupMode").equals("")) {
		groupMode = request.getParameter("groupMode");
	} else {
		groupMode = "view";
	}

	domainList.setSpace(user.getCurrentSpace());
	stepBean.setSpace(user.getCurrentSpace());
	stepBean.setUser(user);

	if (!mode.equals("create") && !stepBean.hasErrors()) {
		if (request.getParameter("step_id") != null) {
			stepBean.clear();
			stepBean.setID(request.getParameter("step_id"));
			stepBean.load();
			managerBean.setCurrentStepID(stepBean.getID());
			managerBean.setCurrentStepName(stepBean.getName());
		} else if (managerBean.getCurrentStepID() != null) {
			stepBean.clear();
			stepBean.setID(managerBean.getCurrentStepID());
	        stepBean.load();
			managerBean.setCurrentStepName(stepBean.getName());
		} else {
			throw new PnetException("Missing parameter 'step_id' in StepEdit.jsp.");
		}
	}
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
	var selectWin;		// Popup window
	javascript:window.history.forward(-1);
	
function setup() {
    theForm = self.document.forms[0];
	// Initialize radio group
	stepTypeInit();
	// Set the entry status dropdown list to the current value
	selectByValue(theForm.entryStatusID, '<jsp:getProperty name="stepBean" property="entryStatusID" />');
	isLoaded = true;
}

function submit() {			
        theAction("submit");
	    theForm.submit();
}

function cancel() {
	self.document.location = JSPRootURL + '/workflow/StepList.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&id=<jsp:getProperty name="stepBean" property="workflowID" />';
}

function reset() { 
	self.document.location = JSPRootURL + 
							 "/workflow/StepEdit.jsp" + 
							 '?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&id=<jsp:getProperty name="stepBean" property="workflowID" />';
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

// Functions that will be called by the included StepGroupList page
function stepGroupModify() {
	theAction("stepgroup_modify");
	theForm.submit();
}

// Functions called by the included StepActorsSelect page
function doSelect() {
	theAction("grouplist_select");
	theForm.submit();
}

function doDeselect() {
	theAction("grouplist_deselect");
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
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_designer&section=step_edit";
	openwin_help(helplocation);
}


</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.workflow.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page level="1" displayToken="@prm.workflow.stepedit.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/StepEdit.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.WORKFLOW + "&action=" + net.project.security.Action.MODIFY + "&id=" + stepBean.getID()%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/StepEditProcessing.jsp">
<input type="hidden" name="theAction" value="" />
<input type="hidden" name="step_id" value='<jsp:getProperty name="stepBean" property="ID" />' />
<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value='<jsp:getProperty name="stepBean" property="workflowID" />' />
<%-- Page Header --%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td class="pageTitle" align="left"><%=PropertyProvider.get("prm.workflow.edit.designer.pagetitle")%></td>
        <td class="pageTitle" align="right"><%=PropertyProvider.get("prm.workflow.stepedit.pagetitle", new Object [] {stepBean.getName()})%></td>
    </tr>
</table>
<%-- Step Create Form --%> 
<input type="Hidden" name="initialStep" value='<jsp:getProperty name="stepBean" property="initialStep" />' />
<input type="Hidden" name="finalStep" value='<jsp:getProperty name="stepBean" property="finalStep" />' />
<table border="0" width="100%" cellspacing="0" cellpadding="2">
    <tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<%-- Display any problems --%>
		<td colspan="3"><%=stepBean.getErrorsTable()%></td>
	</tr>
    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=stepBean.getFlagError("name", PropertyProvider.get("prm.workflow.stepedit.name.label"))%></td>
      <td  align="left" nowrap> 
        <input type="text" name="name" maxlength="80" size="30" value='<%=HTMLUtils.escape(stepBean.getName()).replaceAll("'","&acute;")%>' />
      </td>
      <td nowrap width="25%" />
    </tr>
        <tr>
      <td  align="left" nowrap class="fieldRequired"><%=stepBean.getFlagError("sequence", PropertyProvider.get("prm.workflow.stepedit.sequence.label"))%></td>
      <td  align="left" nowrap>
        <input type="text" name="sequence" maxlength="4" size="4" value='<%=HTMLUtils.escape(stepBean.getSequence()).replaceAll("'","&acute;")%>' />
      </td>
      <td nowrap width="25%" />
    </tr>
    <tr>
      <td  align="left" nowrap colspan="2" class="fieldNonRequired"><%=stepBean.getFlagError("description", PropertyProvider.get("prm.workflow.stepedit.description.label"))%><br>
        <textarea name="description" cols="60" rows="3"><%=HTMLUtils.escape(stepBean.getDescription()).replaceAll("'","&acute;")%></textarea>
      </td>
      <td nowrap width="25%" /> 
    </tr>
    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=stepBean.getFlagError("type", PropertyProvider.get("prm.workflow.stepedit.type.label"))%></td>
      <td  align="left" nowrap>
		<input name="pres_stepType" type="Radio" value="0" onClick="javascript:stepTypeChange(this.value)"><%=PropertyProvider.get("prm.workflow.stepedit.type.option.default.name")%></input>
		<input name="pres_stepType" type="Radio" value="1" onClick="javascript:stepTypeChange(this.value)"><%=PropertyProvider.get("prm.workflow.stepedit.type.option.initial.name")%></input>
		<input name="pres_stepType" type="Radio" value="2" onClick="javascript:stepTypeChange(this.value)"><%=PropertyProvider.get("prm.workflow.stepedit.type.option.final.name")%></input>
      </td>
      <td nowrap width="25%" /> 
    </tr>
    <tr> 
      <td  align="left" nowrap class="fieldNonRequired"><%=stepBean.getFlagError("entry_status_id", PropertyProvider.get("prm.workflow.stepedit.status.label"))%></td>
      <td  align="left" nowrap>
        <select name="entryStatusID">
		    <option value=""><%=PropertyProvider.get("prm.workflow.stepedit.status.option.name")%></option>
			<jsp:setProperty name="domainList" property="stylesheet" value="/workflow/xsl/status_list_option.xsl" />
			<jsp:getProperty name="domainList" property="statusListPresentation" />
        </select>
      </td>
      <td nowrap width="25%" /> 
    </tr>
    <tr> 
      <td align="left" nowrap colspan="2" class="fieldNonRequired"><%=stepBean.getFlagError("notes", PropertyProvider.get("prm.workflow.stepedit.instructions.label"))%><br>
          <textarea name="notes" cols="60" rows="3" wrap><%=HTMLUtils.escape(stepBean.getNotes()).replaceAll("'","&acute;")%></textarea>
      </td>
      <td nowrap width="25%" />
    </tr>
    <tr> 
      <td nowrap colspan="3">&nbsp;</td>
    </tr>
</table>
<%------------------------------------------------------------------------
  -- Groups at this step
  ----------------------------------------------------------------------%>
<%
	if (groupMode.equals("view")) {
%>
	<input type="hidden" name="groupMode" value="view" />
	<jsp:include page="include/StepGroupList.jsp" flush="true" />
<%
	} else {
%>
	<input type="hidden" name="groupMode" value="modify" />
	<jsp:include page="include/StepActorsSelect.jsp" flush="true" />
<%
	}
%>
<br />

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