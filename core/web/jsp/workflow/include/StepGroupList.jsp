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
| The list of Groups (and People) defined at this step
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Step Group List"
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.*,
			net.project.workflow.*,
			net.project.security.*, 
			net.project.persistence.*,
			net.project.base.PnetException" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<jsp:useBean id="stepBean" class="net.project.workflow.StepBean" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<%
/*
    // Get stepID input parameter
    String stepID;
	if (request.getParameter("step_id") != null) {
		stepID = request.getParameter("step_id");
	} else if (managerBean.getCurrentStepID() != null) {
		stepID = managerBean.getCurrentStepID();
	} else {
		throw new PnetException("Missing parameter 'step_id' in StepTransitionList.jsp.");
	}
*/
	// Set current user and space
	managerBean.setUser(user);
    managerBean.setSpace(user.getCurrentSpace());
%>
<security:verifyAccess objectID="<%=stepBean.getWorkflowID()%>"
					   action="modify"
					   module="<%=net.project.base.Module.WORKFLOW%>"
/> 
<%------------------------------------------------------------------------
  -- Draw StepGroupList Table
  ----------------------------------------------------------------------%>
<table border="0" cellspacing="0" cellpadding="0" width="50%">
  <tr class="channelHeader" align="left">
    <td class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
    <td class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.workflow.include.stepgrouplist.channel.roles.title")%></td>
	<td class="channelHeader" align="right">
		<tb:toolbar style="channel" showLabels="true">
			<tb:band name="channel">
				<tb:button type="modify" function="javascript:stepGroupModify();" />
			</tb:band>
		</tb:toolbar>
	</td>
    <td class="channelHeader" align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
  </tr>
  <tr>
    <td colspan="4">
	  <jsp:setProperty name="managerBean" property="stylesheet" value="/workflow/include/xsl/step_group_list.xsl" />
      <%=managerBean.getStepGroupsPresentation(stepBean.getID())%>
    </td>
  </tr>
</table>
