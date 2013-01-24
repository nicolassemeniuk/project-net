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
    info="Create Workflow" 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.*,
			net.project.security.*,
			net.project.workflow.*" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" /> 
<jsp:useBean id="domainList" class="net.project.workflow.DomainListBean" scope="page" />
<%
    workflowBean.setUser(user);
    workflowBean.setSpace(user.getCurrentSpace());
	domainList.setSpace(user.getCurrentSpace());
%> 
<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.WORKFLOW%>"
/> 
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {

    theForm = self.document.forms[0];
	isLoaded = true;
	// Set the two dropdown lists to the current workflow values
	selectByValue(theForm.strictnessID, '<jsp:getProperty name="workflowBean" property="strictnessID" />');
	selectByValue(theForm.ownerID, '<jsp:getProperty name="workflowBean" property="ownerID" />');
	setFunctionForOnSubmitEventOfForm();
}

function cancel() {
	self.document.location = JSPRootURL + "/workflow/Main.jsp?module=<%=net.project.base.Module.WORKFLOW%>";
}

function reset() { 
	self.document.location = JSPRootURL + "/workflow/WorkflowCreate.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.CREATE%>";
}

function tabClick(nextPage) {
	if (theForm.nextPage) {
		theForm.nextPage.value = nextPage;
		submit();
	}
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
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_designer&section=create";
	openwin_help(helplocation);
}

</script>
</head>
<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.workflow.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page displayToken="@prm.workflow.create.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/WorkflowCreate.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.WORKFLOW + "&action=" + net.project.security.Action.CREATE%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" id="workflowCreate" action="<session:getJSPRootURL />/workflow/WorkflowCreateProcessing.jsp">
<input type="hidden" name="theAction" />
<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.CREATE%>" />
<input type="hidden" name="id" value='' />
<%-- Page Header --%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr class="channelHeader">
        <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
        <td class="channelHeader"><%=PropertyProvider.get("prm.workflow.edit.designer.pagetitle")%> <%=PropertyProvider.get("prm.global.display.requiredfield")%></td>
        <td class="channelHeader" align="right"><%=PropertyProvider.get("prm.workflow.create.pagetitle")%></td>
    	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
    </tr>
	<tr><td colspan="4">&nbsp;</td></tr>
</table>
<input type="hidden" name="nextPage" value=''/>
<tab:tabStrip>
	<%-- Once we've left this page, there will be a workflow, therefore we always
	     resort to WorkflowEdit for subsequent editing --%>
	<tab:tab labelToken="@prm.workflow.create.definition.tab" href="javascript:tabClick('WorkflowEdit.jsp');" selected="true" />
	<tab:tab labelToken="@prm.workflow.create.steps.tab" href="javascript:tabClick('StepList.jsp');" />
	<tab:tab labelToken="@prm.workflow.create.transitions.tab" clickable="false" />
	<tab:tab labelToken="@prm.workflow.create.publish.tab" clickable="false" />
</tab:tabStrip>
<input type="hidden" name="spaceID" value='<%=user.getCurrentSpace().getID()%>' />
<table border="0" width="100%" cellspacing="0" cellpadding="2">
    <tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<%-- Display any problems --%>
		<td colspan="3"><%=workflowBean.getErrorsTable()%></td>
	</tr>
    <tr> 
      <td  align="left" nowrap class="fieldRequired"><%=workflowBean.getFlagError("name", PropertyProvider.get("prm.workflow.create.name.label"))%></td>
      <td  align="left" nowrap> 
        <input type="text" name="name" maxlength="80" value='<c:out value="${name}"/>' size="30">
      </td>
      <td nowrap width="25%"/> 
    </tr>
    <tr> 
      <td  align="left" nowrap colspan="2" class="fieldNonRequired"><%=workflowBean.getFlagError("description", PropertyProvider.get("prm.workflow.create.description.label"))%><br>
        <textarea name="description" cols="60" rows="3"><c:out value="${description}"/></textarea>
      </td>
      <td nowrap width="25%"/> 
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
      <td nowrap width="25%"/> 
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
      <td nowrap width="25%"/> 
    </tr>
	<tr>
      <td align="left" class="fieldRequired" width="25%"><%=workflowBean.getFlagError("object_types", PropertyProvider.get("prm.workflow.create.objecttype.label"))%></td>
	  <td align="left" nowrap>
        <select name="objectTypeSelect" onChange="window.objectTypeSelect();">
			<jsp:setProperty name="domainList" property="stylesheet" value="/workflow/xsl/object_type_list_option.xsl" />
			<jsp:getProperty name="domainList" property="workflowObjectTypesPresentation" />
        </select>
		<input type="hidden" name="objectTypeName" value=''>
		<input type="hidden" name="subTypeID" value=''>
      </td>
      <td>&nbsp;</td>
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
<%workflowBean.clearErrors();%>