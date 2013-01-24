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
| Workflow Remove Result page
| Input request parameters:
|	mode	- "prepareError" : Error preparing for remove; Workflow cannot be removed
|			  "removeError"  : Error removing workflow
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Remove Workflow" 
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
<%@ include file="/base/taglibInclude.jsp" %>
<%
    workflowBean.setUser(user);
    workflowBean.setSpace(user.getCurrentSpace());

	String mode = null;
	if (request.getParameter("mode") != null && !request.getParameter("mode").equals("")) {
		mode = request.getParameter("mode");
	}
%> 
<security:verifyAccess objectID="<%=workflowBean.getID()%>"
					   action="delete"
					   module="<%=net.project.base.Module.WORKFLOW%>" /> 

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
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

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_designer&section=remove";
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
			<history:page displayToken="@prm.workflow.removeresult.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/WorkflowRemoveResult.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.WORKFLOW + "&action=" + net.project.security.Action.DELETE + "&id=" + workflowBean.getID()%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td class="pageTitle" align="left"><%=PropertyProvider.get("prm.workflow.edit.designer.pagetitle")%></td>
        <td class="pageTitle" align="right"><%=PropertyProvider.get("prm.workflow.removeresult.pagetitle", new Object [] {workflowBean.getName()})%></td>
<!-- Results of removing Workflow "<jsp:getProperty name="workflowBean" property="name" />" -->
    </tr>
</table>
<table border="0" width="100%" cellspacing="0" cellpadding="2">
    <tr><td colspan="4">&nbsp;</td></tr>
	<tr>
		<td>&nbsp;</td>
		<td colspan="2" align="left" class="fieldContent">
		<%
			if (mode.equals("prepareError")) {
		%>
				<jsp:getProperty name="workflowBean" property="prepareRemovePresentation" />
		<%
			} else {
		%>
				<jsp:getProperty name="workflowBean" property="removeResultPresentation" />
		<%
			}
		%>
		</td>
		<td>&nbsp;</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action" >
		<tb:button type="cancel" labelToken="@prm.workflow.removeresult.close.button.label" />
	</tb:band>
</tb:toolbar>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
<%workflowBean.clearErrors();%>
