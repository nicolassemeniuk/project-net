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
| Displays envelopes for workflow
+----------------------------------------------------------------------*/
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Workflow Envelope List" 
    language="java" 
    errorPage="/workflow/WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*,
			net.project.space.Space,
			net.project.workflow.*" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="page" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<jsp:useBean id="domainList" class="net.project.workflow.DomainListBean" scope="page" />
<% 
    // Load the workflow
	workflowBean.setUser(user);
	workflowBean.setSpace(user.getCurrentSpace());
		
	if (request.getParameter("id") != null) {
		workflowBean.setID(request.getParameter("id"));
		workflowBean.load();
	}
%>
<security:verifyAccess objectID="<%=workflowBean.getID()%>"
					   action="view"
					   module="<%=net.project.base.Module.WORKFLOW%>"
/> 
<%
	// Handle filter parameters
	boolean isInactiveIncluded = false;
	String priorityID = "";
	if (request.getParameter("isInactiveIncluded") != null && !request.getParameter("isInactiveIncluded").equals("")) {
		isInactiveIncluded = (request.getParameter("isInactiveIncluded").equals("1") ? true : false);
	}
	if (request.getParameter("priorityID") != null && !request.getParameter("priorityID").equals("")) {
		priorityID = request.getParameter("priorityID");
	}

	// Set up filter
	envelopeManagerBean.setUser(user);
	envelopeManagerBean.setSpace(user.getCurrentSpace());
	envelopeManagerBean.clearFilter();
	envelopeManagerBean.setWorkflowFilter(workflowBean.getID());
	envelopeManagerBean.setInactiveIncluded(isInactiveIncluded);
	envelopeManagerBean.setPriorityID(priorityID);
	envelopeManagerBean.setStylesheet("/workflow/envelope/include/xsl/workflow_envelope_list.xsl");
%>
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	var workflowId = '<jsp:getProperty name="workflowBean" property="ID" />';
		
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
	selectByValue(theForm.isInactiveIncluded, '<%=(isInactiveIncluded ? "1" : "0")%>');
	selectByValue(theForm.priorityID, '<%=priorityID%>');
}

function cancel() { 
	self.document.location = JSPRootURL + "/workflow/Main.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.VIEW%>";
}

function reset() { 
	self.document.location = JSPRootURL + '/workflow/WorkflowEnvelopeList.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=<%=net.project.security.Action.VIEW%>&id=<%=workflowBean.getID()%>'; 
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
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_envelopes";
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
			<history:page displayToken="@prm.workflow.envelopelist.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/WorkflowEnvelopeList.jsp?module=" + net.project.base.Module.WORKFLOW + "&action=" + net.project.security.Action.VIEW + "&id=" + workflowBean.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/WorkflowEnvelopeListProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
	<input type="hidden" name="action" value="<%=net.project.security.Action.VIEW%>"/>
	<input type="hidden" name="id" value='<jsp:getProperty name="workflowBean" property="ID" />'/>
	<input type="hidden" name="nextPage" />
<tab:tabStrip>
	<tab:tab labelToken="@prm.workflow.properties.properties.tab" href="javascript:tabClick('Properties.jsp');" />
	<tab:tab labelToken="@prm.workflow.properties.envelopes.tab" href="javascript:tabClick('WorkflowEnvelopeList.jsp');" selected="true" />
</tab:tabStrip>
<br />
<table width="100%" cellpadding="0" cellspacing="0">
	<tr>
		<td align="left" class="tableHeader"><%=PropertyProvider.get("prm.workflow.envelopelist.status.label")%></td>
		<td align="left" class="tableHeader">
			<select name="isInactiveIncluded" onchange="window.submit();">
				<option value="0"><%=PropertyProvider.get("prm.workflow.envelopelist.status.option.active.name")%></option>
				<option value="1"><%=PropertyProvider.get("prm.workflow.envelopelist.status.option.activeandinactive.name")%></option>
			</select>
		</td>
		<td align="left" class="tableHeader"><%=PropertyProvider.get("prm.workflow.envelopelist.priority.label")%></td>
		<td align="left" class="tableHeader">
			<select name="priorityID" onchange="window.submit();">
				<option value=''><%=PropertyProvider.get("prm.workflow.envelopelist.priority.option.any.name")%></option>
				<jsp:getProperty name="domainList" property="priorityListPresentation" />
			</select>
		</td>
		<td width="25%">&nbsp;</td>
	</tr>
</table>				
</form>
<%-- Insert envelope list here based on filter --%>
<jsp:include page="envelope/include/EnvelopeList.jsp" flush="true" />

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>