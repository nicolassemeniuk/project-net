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
| Workflow Edit Confirmation page - this ensures the workflow is unpublished
| before editing.
+----------------------------------------------------------------------*/
%>
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
			net.project.space.*,
			net.project.security.*,
			net.project.workflow.*,
			net.project.base.PnetException" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" /> 
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="session" /> 
<%
    workflowBean.setUser(user);
    workflowBean.setSpace(user.getCurrentSpace());

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
		throw new PnetException("Missing parameter 'id' in WorkflowEditConfirm.jsp");
	}
	
	if (!workflowBean.isPublished()) {
%>
		<%-- Security is handled in WorkflowEdit --%>
		<jsp:forward page="WorkflowEdit.jsp" />
<%
	}
%>
<security:verifyAccess objectID='<%=workflowBean.getID()%>'
					   action="modify"
					   module="<%=net.project.base.Module.WORKFLOW%>"
/> 
	<%--
	
		At this point we know the workflow is published.
		We must therefore get confirmation to change it
		to unpublished.
		
	--%>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/standard_prototypes.js" />
<script language="javascript" type="text/javascript">
	var theForm;
	
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
    theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() {
	self.document.location = JSPRootURL + "/workflow/WorkflowDesigner.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>";
}

function reset() { 
	self.document.location = JSPRootURL + "/workflow/WorkflowEditConfirm.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=workflowBean.getID()%>";
}

function changeStatus() {
	theAction("change_status");
	theForm.submit();
}
function viewOnly() {
	theAction("view_only");
	theForm.submit();
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_designer&section=edit_confirm";
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
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/WorkflowEditConfirm.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.WORKFLOW + "&action=" + net.project.security.Action.MODIFY + "&id=" + workflowBean.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/WorkflowEditConfirmProcessing.jsp">
<input type="hidden" name="theAction" />
<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value='<jsp:getProperty name="workflowBean" property="ID" />' />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td class="pageTitle" align="left"><%=PropertyProvider.get("prm.workflow.edit.designer.pagetitle")%></td>
        <td class="pageTitle" align="right"><jsp:getProperty name="workflowBean" property="name" /></td>
    </tr>
</table>
<input type="hidden" name="spaceID" value='<jsp:getProperty name="workflowBean" property="spaceID" />' />
<table border="0" width="100%" cellspacing="0" cellpadding="2">
    <tr><td colspan="4">&nbsp;</td></tr>
	<tr>
		<td>&nbsp;</td>
		<td colspan="2" align="left" class="fieldContent">
			<display:get name="prm.workflow.editconfirm.published.message"/>
		</td>
		<td>&nbsp;</td>
	</tr>
</table>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action" >
		<tb:button type="submit" labelToken="@prm.workflow.editconfirm.unpublish.label" function="javascript:changeStatus();" />
		<tb:button type="cancel" labelToken="@prm.workflow.editconfirm.viewonly.label" function="javascript:viewOnly();" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>