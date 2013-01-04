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
| A list of all transitions for a particular workflow
| This is designed to be included in other files.
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Workflow Transition List"
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.*,
			net.project.workflow.*,
			net.project.security.*,
			net.project.base.PnetException" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<%
    String workflowID;
	if (request.getParameter("id") != null) {
		workflowID = request.getParameter("id");
	} else if (managerBean.getCurrentWorkflowID() != null) {
		workflowID = managerBean.getCurrentWorkflowID();
	} else {
		throw new PnetException("Missing parameter 'id' in WorkflowTransitionList.jsp.");
	}
		
	// Set current user and space
	managerBean.setUser(user);
    managerBean.setSpace(user.getCurrentSpace());
%>
<security:verifyAccess objectID="<%=workflowID%>"
					   action="modify"
					   module="<%=net.project.base.Module.WORKFLOW%>"
/> 
<%-- Draw transition list --%>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.workflow.include.workflowtransitionlist.channel.transitions.title")%></th>
		<th class="channelHeader" align="right">&nbsp;</th>
        <th class="channelHeader" align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
    <tr>
		<td>&nbsp;</td>
        <td colspan="2">
        	<%-- Display transition List --%>
	        <jsp:setProperty name="managerBean" property="stylesheet" value="/workflow/include/xsl/workflow_transition_list.xsl" />
            <%=managerBean.getWorkflowTransitionsPresentation(workflowID)%>
        </td>
		<td>&nbsp;</td>
    </tr>
</table>
