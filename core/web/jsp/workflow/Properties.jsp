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
| Displays workflow properties
+----------------------------------------------------------------------*/
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Workflow Properties" 
    language="java" 
    errorPage="/workflow/WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="workflowBean" class="net.project.workflow.WorkflowBean" scope="page" />
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
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() { 
	self.document.location = JSPRootURL + "/workflow/Main.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=1";
}

function reset() { 
	self.document.location = JSPRootURL + '/workflow/Properties.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=1&id=<%=workflowBean.getID()%>'; 
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

function modify() {
	theAction("modify");
	theForm.action.value = "<%=net.project.security.Action.MODIFY%>";
	theForm.submit();
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_properties";
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
			<history:page display='<%=PropertyProvider.get("prm.workflow.properties.module.history")%>' 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/Properties.jsp?module=" + net.project.base.Module.WORKFLOW + "&action=1&id=" + workflowBean.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="modify" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/PropertiesProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
	<input type="hidden" name="action" value="<%=net.project.security.Action.VIEW%>"/>
	<input type="hidden" name="id" value='<jsp:getProperty name="workflowBean" property="ID" />'/>
<input type="hidden" name="nextPage" />
<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.workflow.properties.properties.tab")%>' href="javascript:tabClick('Properties.jsp');" selected="true" />
	<tab:tab label='<%=PropertyProvider.get("prm.workflow.properties.envelopes.tab")%>' href="javascript:tabClick('WorkflowEnvelopeList.jsp');" />
</tab:tabStrip>
<br />
<jsp:setProperty name="workflowBean" property="stylesheet" value="/workflow/xsl/workflow_properties.xsl" />
<jsp:getProperty name="workflowBean" property="propertiesPresentation" />
</form>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
		<tb:band name="action" enableAll="true">
      </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
	