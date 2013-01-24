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
| Allows selection of workflows for a form
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Form Designer -- Workflows" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
            net.project.form.FormDesigner,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.security.User"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<%
	// Load the workflows associated with this form
	formDesigner.loadWorkflows();
	String  refererURL = (String) pageContext.getAttribute("refererURL", pageContext.SESSION_SCOPE);
	String titleVal = PropertyProvider.get("prm.form.designer.workflowselect.channel.select.title");
%>	

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<template:getSpaceCSS/>
<security:verifyAccess objectID='<%=formDesigner.getID()%>'
					   action="modify"
					   module="<%=net.project.base.Module.FORM%>"
/> 
<template:import type="javascript" src="/src/standard_prototypes.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() { 
<%
	if(refererURL != null && !refererURL.trim().equals("")) {
%>
	self.document.location = '<%= refererURL %>';
<% 
	} else {
%>		
	self.document.location= JSPRootURL + "/form/designer/Main.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>"; 
<%	
	}
%>	
}

function reset()  { self.document.location = JSPRootURL + "/form/designer/WorkflowSelect.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=formDesigner.getID()%>"; }

function submit() {
	theAction("submit");
	theForm.submit();
}

function help() {
    var helplocation = "<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=form_workflow_select";
    openwin_help(helplocation);
}

</script>
</head>
<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle"><history:display /></tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="WorkflowSelectProcessing.jsp">
<input type="hidden" name="theAction">

<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value="<jsp:getProperty name="formDesigner" property="ID"/>" />

<%-- Header and Tab Bar --%>	
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<tr align="left">
	<td align="left">
		<span class="pageTitle"><%=PropertyProvider.get("prm.form.designer.main.pagetitle")%></span>
	</td>
	<td align="right" nowrap>
		<span class="pageTitle"><jsp:getProperty name="formDesigner" property="name" /></span>
	</td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>
</table>
<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.definitionedit.definition.tab")%>' href='<%="DefinitionEdit.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.fieldsmanager.fields.tab")%>' href='<%="FieldsManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.preview.preview.tab")%>' href='<%="Preview.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.listsmanager.lists.tab")%>' href='<%="ListsManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
<%-- Disabled for now since they are not implemented yet
	<tab:tab label="Search" href='<%="SearchEdit.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label="Links" href='<%="LinksManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label="Documentation" href='<%="DocumentationManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
--%>
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.workflowselect.workflows.tab")%>' href='<%="WorkflowSelect.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' selected="true" />
    <display:if name="prm.global.form.sharing.isenabled">
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.formsharing.sharing.tab")%>' href='<%="FormSharing.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>'/>
    </display:if>    
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.activateedit.activate.tab")%>' href='<%="ActivateEdit.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
</tab:tabStrip>
<br />
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr> 
		<td valign="top" colspan="3"> 
			<%-- Include the workflow menu --%>
			<jsp:include page="/workflow/include/WorkflowMenu.jsp" flush="true">
				<jsp:param name="title" value= "<%=titleVal%>"/>
				<jsp:param name="objectType" value="<%=net.project.base.ObjectType.FORM_DATA%>" />
				<jsp:param name="subTypeID" value="<%=formDesigner.getID()%>" />
				<jsp:param name="selectedWorkflowID" value="<%=formDesigner.getDefaultWorkflowID()%>" />
			</jsp:include>
		</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>
<p />

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>