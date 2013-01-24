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
    info="Workflow Designer" 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*,
			net.project.space.*,
			net.project.workflow.*" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<%
    // Load workflow menu data
	managerBean.setSpace(user.getCurrentSpace());
	managerBean.setUser(user);
%>
<security:verifyAccess action="modify"
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
}

function create() { 
	theAction("create");
    theForm.target = "_self";
	theForm.action.value = "<%=net.project.security.Action.CREATE%>";
	theForm.id.value = '';
	theForm.submit();
}

function modify(id) {
	// id radio group has been set to an id
	if (arguments.length != 0) {
		// We got an ID to modify
		aRadio = theForm.workflow_id;
		if (aRadio) {
			if (aRadio.length) {
				for (i = 0; i < aRadio.length; i++) {
					if (aRadio[i].value == id) aRadio[i].checked = true;
				}
			} else {
				if (aRadio.value == id) aRadio.checked = true;
			}
			
		}
	}
	if (isSelected(theForm.workflow_id)) {
		theAction("modify");
        theForm.target = "_self";
		theForm.action.value = "<%=net.project.security.Action.MODIFY%>";
		theForm.id.value = getSelected(theForm.workflow_id);
		theForm.submit();
	} 
	else if (arguments.length == 0) {
		extAlert(errorTitle, "<display:get name='prm.global.javascript.verifyselection.noselection.error.message'/>" , Ext.MessageBox.ERROR);
	}

}

function remove() {
	if (isSelected(theForm.workflow_id)) {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.workflow.designer.remove.message")%>', function(btn){
			if( btn=='yes' ){
				theAction("remove");
		        theForm.target = "_self";
				theForm.action.value = "<%=net.project.security.Action.DELETE%>";
				theForm.id.value = getSelected(theForm.workflow_id);
				theForm.submit();
			} else {
				return false;
			}
		});
	}
	else {
		extAlert(errorTitle, "<display:get name='prm.global.javascript.verifyselection.noselection.error.message'/>" , Ext.MessageBox.ERROR);
	}
}

function reset() { 
	self.document.location = JSPRootURL + '/workflow/WorkflowDesigner.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=2'; 
}

function properties() {
	if (isSelected(theForm.workflow_id)) {
		theAction("properties");
        theForm.target = "_self";
		theForm.action.value = "<%=net.project.security.Action.VIEW%>";
		theForm.id.value = getSelected(theForm.workflow_id);
		theForm.submit();
	}
	else {
		extAlert(errorTitle, "<display:get name='prm.global.javascript.verifyselection.noselection.error.message'/>" , Ext.MessageBox.ERROR);
	}
}

function security() {
	var security;
	if (isSelected(theForm.workflow_id)) {
	    if (!security || security.closed == true) {
			security = openwin_security("security");
		}
        if (security && !security.closed) {
    	    theAction("security");
            theForm.target = "security";
			theForm.action.value = "<%=net.project.security.Action.MODIFY_PERMISSIONS%>";
			theForm.id.value = getSelected(theForm.workflow_id);
            theForm.submit();
            security.focus();
        }
    } else {
    	extAlert("Error","<display:get name='prm.global.javascript.verifyselection.noselection.error.message' />",Ext.MessageBox.ERROR);
    }
}

function isSelected(aList) {
	if (aList) {
		if (aList.checked) return true;
		for (i = 0; i < aList.length; i++) {
			if (aList[i].checked) return true;
		}
	}
	return false;
}

function getSelected(aList) {
	if (aList) {
		if (aList.checked) return aList.value;
		for (i = 0; i < aList.length; i++) {
			if (aList[i].checked) return aList[i].value;
		}
	}
	return null;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_designer";
	openwin_help(helplocation);
}

</script>

</head>

<body  onLoad="setup();" id="bodyWithFixedAreasSupport" class="main">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.workflow.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module displayToken="@prm.workflow.designer.module.history" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/WorkflowDesigner.jsp?module=" + net.project.base.Module.WORKFLOW + "&action=2"%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" label='<%=PropertyProvider.get("prm.workflow.create.button.tooltip")%>' />
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.workflow.edit.button.tooltip")%>' />
		<tb:button type="remove" label='<%=PropertyProvider.get("prm.workflow.delete.button.tooltip")%>' />
		<tb:button type="properties" label='<%=PropertyProvider.get("prm.workflow.view.button.tooltip")%>' />
		<tb:button type="security" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/WorkflowDesignerProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
<input type="hidden" name="action" />
<input type="hidden" name="id" />
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr class="channelHeader" align="left">
        <th class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.workflow.main.channel.defined.title")%></th>
        <th class="channelHeader" align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
	<tr>
	  <td>&nbsp;</td>
	<td>
		<%-- Apply stylesheet to format Workflow list --%>
		<jsp:setProperty name="managerBean" property="stylesheet" value="/workflow/xsl/workflow_designer.xsl" />
		<%=managerBean.getAvailableWorkflowsPresentation(true)%>
    </td>
	  <td>&nbsp;</td>
	</tr>
</table>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
