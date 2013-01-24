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
    info="Edit Workflow" 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager,
			net.project.base.PnetException,
			net.project.util.HTMLUtils" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" /> 
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" /> 
<jsp:useBean id="domainList" class="net.project.workflow.DomainListBean" scope="page" />
<%
    workflowBean.setUser(user);
    workflowBean.setSpace(user.getCurrentSpace());
	domainList.setSpace(user.getCurrentSpace());

	if (!workflowBean.hasErrors()) {
		workflowBean.clear();
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
			throw new PnetException("Missing parameter 'id' in WorkflowEdit.jsp");
		}
	}
%>
<security:verifyAccess objectID='<%=workflowBean.getID()%>'
					   action="modify"
					   module="<%=net.project.base.Module.WORKFLOW%>"
/> 
<%-- Import CSS --%>
<template:getSpaceCSS />

<c:set var="subTypeID"><jsp:getProperty name="workflowBean" property="subTypeID" /></c:set>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/standard_prototypes.js" />

<script language="javascript" type="text/javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
    theForm = self.document.forms[0];
	isLoaded = true;
	// Set the two dropdown lists to the current workflow values
	selectByValue(theForm.strictnessID, '<jsp:getProperty name="workflowBean" property="strictnessID" />');
	selectByValue(theForm.ownerID, '<jsp:getProperty name="workflowBean" property="ownerID" />');
	// Select the correct object type dropwn value
	selectByValue(theForm.objectTypeSelect,	'<jsp:getProperty name="workflowBean" property="objectTypeName" />:<jsp:getProperty name="workflowBean" property="subTypeID" />');
	// Now populate the two hidden fields
	objectTypeSelect();
}

function cancel() {
	self.document.location = JSPRootURL + "/workflow/WorkflowDesigner.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>";
}

function reset() { 
	self.document.location = JSPRootURL + "/workflow/WorkflowEdit.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=workflowBean.getID()%>";
}

function validate(){
	if(theForm.ownerID.value == ""){
		extAlert('Error', '<%= PropertyProvider.get("prm.workflow.edit.ownerid.error") %>' , Ext.MessageBox.ERROR);
		return false;
	}
	return true;
}

function submit() {
	if(validate()){
		theAction("submit");
		theForm.submit();
	}
}

function tabClick(nextPage) {
	if (theForm.nextPage) {
		theForm.nextPage.value = nextPage;
		submit();
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

// Splits the value of the selected object type and sets the appropriate fields
function objectTypeSelect() {
	var objectType = '', subType = '';
	var opt = theForm.objectTypeSelect.options[theForm.objectTypeSelect.selectedIndex];
	if (opt) {
		var a = opt.value.split(':');
		if (a[0]) objectType = a[0];
		if (a[1]) subType = a[1];
	}
	theForm.objectTypeName.value = objectType;
	theForm.subTypeID.value = subType;			
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_designer&section=edit";
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
			<history:page displayToken="@prm.workflow.edit.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/WorkflowEdit.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.WORKFLOW + "&action=" + net.project.security.Action.MODIFY + "&id=" + workflowBean.getID()%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/WorkflowEditProcessing.jsp">
<input type="hidden" name="theAction" />
<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value='<jsp:getProperty name="workflowBean" property="ID" />' />
<%-- Page Header --%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr class="channelHeader">
        <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
        <td align="left"><%=PropertyProvider.get("prm.workflow.edit.designer.pagetitle")%> <%=PropertyProvider.get("prm.global.display.requiredfield")%></td>
        <td align="right"><jsp:getProperty name="workflowBean" property="name" /></td>
    	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
    </tr>
	<tr><td colspan="4">&nbsp;</td></tr>
</table>
<input type="hidden" name="nextPage" />
<tab:tabStrip>
	<tab:tab labelToken="@prm.workflow.create.definition.tab" href="javascript:tabClick('WorkflowEdit.jsp');" selected="true" />
	<tab:tab labelToken="@prm.workflow.create.steps.tab" href="javascript:tabClick('StepList.jsp');" />
	<tab:tab labelToken="@prm.workflow.create.transitions.tab" href="javascript:tabClick('TransitionList.jsp');" />
	<tab:tab labelToken="@prm.workflow.create.publish.tab" href="javascript:tabClick('WorkflowPublish.jsp');" />
</tab:tabStrip>
<%-- Workflow Definition Form --%> 
<input type="hidden" name="spaceID" value='<jsp:getProperty name="workflowBean" property="spaceID" />' />
<table border="0" width="100%" cellspacing="0" cellpadding="2">
    <tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<%-- Display any problems --%>
		<td colspan="3"><%=workflowBean.getErrorsTable()%></td>
	</tr>
    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=workflowBean.getFlagError("name", PropertyProvider.get("prm.workflow.create.name.label"))%></td>
      <td  align="left" nowrap> 
        <input type="text" name="name" maxlength="80" value='<%=HTMLUtils.escape(workflowBean.getName()).replaceAll("'","&acute;")%>' size="30">
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
      <td  align="left" nowrap colspan="2" class="fieldNonRequired"><%=workflowBean.getFlagError("description", PropertyProvider.get("prm.workflow.create.description.label"))%><br>
        <textarea name="description" cols="60" rows="3"><%=HTMLUtils.escape(workflowBean.getDescription()).replaceAll("'","&acute;")%></textarea>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=workflowBean.getFlagError("owner_id", PropertyProvider.get("prm.workflow.create.owner.label"))%></td>
      <td  align="left" nowrap> 
        <select name="ownerID">
		    <option value=""></option>
			<jsp:setProperty name="domainList" property="stylesheet" value="/workflow/xsl/ownerIDList.xsl" />
			<jsp:getProperty name="domainList" property="ownerIDList" />
        </select>
      </td>
      <td>&nbsp;</td>
	</tr>
    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=workflowBean.getFlagError("strictness_id", PropertyProvider.get("prm.workflow.create.rule.label"))%></td>
      <td  align="left" nowrap> 
        <select name="strictnessID">
		    <option value=""></option>
			<jsp:setProperty name="domainList" property="stylesheet" value="/workflow/xsl/strictnessIDList.xsl" />
			<jsp:getProperty name="domainList" property="strictnessIDList" />
        </select>
      </td>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td align="left" class="fieldRequired" width="25%"><%=workflowBean.getFlagError("object_types", PropertyProvider.get("prm.workflow.create.objecttype.label"))%></td>
	  <td align="left" nowrap>
        <select name="objectTypeSelect" onChange="window.objectTypeSelect();">
			<jsp:setProperty name="domainList" property="stylesheet" value="/workflow/xsl/object_type_list_option.xsl" />
			<jsp:getProperty name="domainList" property="workflowObjectTypesPresentation" />
        </select>
		<input type="hidden" name="objectTypeName">
		<input type="hidden" name="subTypeID">
      </td>
      <td>&nbsp;</td>
    </tr>
</table>

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
<%workflowBean.clearErrors();%>
