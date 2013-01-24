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
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Workflow" 
    language="java" 
    errorPage="/workflow/WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*,
			net.project.space.Space,
			net.project.workflow.*" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<% 
	managerBean.setSpace(user.getCurrentSpace());
	managerBean.setUser(user);
%>
<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.WORKFLOW%>"
/> 
<template:getDoctype />
<template:insert> 
	<template:put name="title" content='<%=PropertyProvider.get("prm.global.application.title")%>' direct="true" /> 

<%-- Additional HEAD stuff --%>
<template:put name="head">

<%-- Import Javascript --%>
<template:getSpaceCSS />
<template:getSpaceJS />
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function reset() { 
	self.document.location = JSPRootURL + '/workflow/Main.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=1'; 
}

function create() { 
	theAction("create");
	theForm.action.value = "<%=net.project.security.Action.CREATE%>";
	theForm.id.value = '';
	theForm.submit();
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=workflow_main";
	openwin_help(helplocation);
}

</script>
	
</template:put>
<%-- End of HEAD --%>

<%-- Begin Content --%>		
<template:put name="content">

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.workflow.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.workflow.main.module.history")%>' 
							jspPage='<%=SessionManager.getJSPRootURL() + "/workflow/Main.jsp?module=" + net.project.base.Module.WORKFLOW + "&action=1"%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" label='<%=PropertyProvider.get("prm.workflow.create.button.tooltip")%>'  />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/workflow/MainProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=net.project.base.Module.WORKFLOW%>" />
	<input type="hidden" name="id" />
	<input type="hidden" name="action" />
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td />
		<td align="right"><!--<a href="WorkflowDesigner.jsp?module=<%=net.project.base.Module.WORKFLOW%>&action=2"><%=PropertyProvider.get("prm.workflow.main.designer.link")%></a>--></td>
	</tr>
</table>
<br />
<%-- Insert Workflow List --%>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr class="channelHeader" align="left">
    <th class="channelHeader" width="1%"><img src="<session:getJSPRootURL />/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
	<th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.workflow.main.channel.defined.title")%></th>
    <th class="channelHeader" align="right" width="1%"><img src="<session:getJSPRootURL />/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
  </tr>
  <tr>
    <td>&nbsp;</td>
	<td>
		<%-- Apply stylesheet to format Workflow list --%>
		<jsp:setProperty name="managerBean" property="stylesheet" value="/workflow/xsl/workflow_list.xsl" />
		<!-- Avinash: Do not show unpublished workflow on main page -->
		<%=managerBean.getAvailableWorkflowsPresentation(false)%>
    </td>
	<td>&nbsp;</td>
  </tr>
</table>
</form>

</template:put>
<%-- End Content --%>
</template:insert>

<template:getSpaceMainMenu />
<template:getSpaceNavBar />