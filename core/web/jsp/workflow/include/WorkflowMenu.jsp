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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
| Workflow Menu provides the ability to select a workflow
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Workflow Menu" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.*,
			net.project.workflow.*,
			net.project.security.*"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<%
	// Grab parameters
	String title = request.getParameter("title");
	String objectType = request.getParameter("objectType");
	String subTypeID = request.getParameter("subTypeID");
	String stylesheet = "/workflow/include/xsl/workflow_menu.xsl";
	String selectedWorkflowID = request.getParameter("selectedWorkflowID");
	if (request.getParameter("stylesheet") != null && !request.getParameter("stylesheet").equals("")) {
		stylesheet = request.getParameter("stylesheet");
	}
			
    // Load workflow menu data
	managerBean.setSpace(user.getCurrentSpace());
	managerBean.setUser(user);
%>
<script language="JavaScript">
	var selectedWorkflowID = "<%=selectedWorkflowID%>";
	
<%-- Functions to present the selected workflow row and to handle the selection of a new workflow --%>
function selectedWorkflowPresentation(workflowID) {
	if (workflowID == selectedWorkflowID) {
		return '<img src="<%=SessionManager.getJSPRootURL()%>/images/blk_check.gif" border="0" title="Currently selected workflow" />';
	} else {
		return '&nbsp;&nbsp;&nbsp;&nbsp;';
	}
}

function selectWorkflow(workflowID) {
	theForm.workflowID.value = workflowID;
	theAction("select_workflow");
	theForm.submit();
}
</script>
<input type="hidden" name="workflowID" />
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr class="channelHeader" align="left">
        <th class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=title%></th>
        <th class="channelHeader" align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
	<tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<td>&nbsp;</td>
		<td>
			<%-- Apply stylesheet to format Workflow list --%>
			<jsp:setProperty name="managerBean" property="stylesheet" value="<%=stylesheet%>" />
			<%=managerBean.getWorkflowsForTypePresentation(objectType, subTypeID)%>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<td>&nbsp;</td>
		<td class="tableContent" align="left"><%=PropertyProvider.get("prm.workflow.include.workflowmenu.clearselection.1.text")%><a href="javascript:selectWorkflow('');"><%=PropertyProvider.get("prm.workflow.include.workflowmenu.clearselection.2.link")%></a><%=PropertyProvider.get("prm.workflow.include.workflowmenu.clearselection.3.text")%></td>
		<td>&nbsp;</td>
	</tr>		
</table>

