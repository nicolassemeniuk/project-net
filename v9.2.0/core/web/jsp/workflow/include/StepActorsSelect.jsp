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
| Provides an include file for selecting and deselecting Step Actors
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Step Actors Select" 
    language="java" 
    errorPage="/workflow/WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.*,
			net.project.workflow.*,
			net.project.security.*" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<jsp:useBean id="listBean" class="net.project.workflow.SelectListBean" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<%
	boolean doLoad = true;
	if (request.getParameter("loadGroupList") != null && request.getParameter("loadGroupList").equals("false")) {
		doLoad = false;
	}

	listBean.setSpace(user.getCurrentSpace());
	listBean.setUser(user);
	listBean.setCurrentWorkflowID(managerBean.getCurrentWorkflowID());
	listBean.setCurrentStepID(managerBean.getCurrentStepID());
	if (doLoad) {
		listBean.load();
	}
%>
<security:verifyAccess objectID="<%=managerBean.getCurrentWorkflowID()%>"
					   action="modify"
					   module="<%=net.project.base.Module.WORKFLOW%>"
/> 

  <!-- Select List table -->
  <table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr class="channelHeader" align="left"> 
      <th class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
      <th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.workflow.include.stepactorsselect.channel.select.title")%></th>
      <th class="channelHeader" align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
    </tr>
	<tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<td>&nbsp;</td>
		<td class="tableHeader" align="left">
			<%=PropertyProvider.get("prm.workflow.include.stepactorsselect.instruction")%>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<%-- Display any problems --%>
		<td>&nbsp;</td>
		<td><%=listBean.getErrorsTable()%></td>
		<td>&nbsp;</td>
	</tr>
    <tr> 
      <td>&nbsp;</td>
      <td> 
        <table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
          <tr> 
            <th class="tableHeader" align="left"><%=PropertyProvider.get("prm.workflow.include.stepactorsselect.select.label")%></th>
            <th class="tableHeader" align="left">&nbsp;</th>
            <th class="tableHeader" align="left"><%=PropertyProvider.get("prm.workflow.include.stepactorsselect.selected.label")%></th>
          </tr>
          <tr valign="top"> 
            <td width="50%"> <%-- Insert deselect list --%> 
              <jsp:setProperty name="listBean" property="stylesheet" value="/workflow/include/xsl/actors_deselect_list.xsl" /> 
              <%=listBean.getEntryListPresentation()%> 
            </td>
            <td class="tableContent" valign="center"> 
              <table border="0" cellspacing="0" cellpadding="0" width="100%">
			    <tr><td>&nbsp;</td></tr>
                <tr align="center"> 
                  <td><a href="#" onClick="javascript:doSelect();"><img  width="27" height="27" border="0" src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-nextpost_off.gif"></a></td>
                </tr>
			    <tr><td>&nbsp;</td></tr>
                <tr align="center"> 
                  <td><a href="#" onClick="javascript:doDeselect();"><img  width="27" height="27" border="0" src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-previouspost_off.gif"></a></td>
                </tr>
			    <tr><td>&nbsp;</td></tr>
              </table>
            </td>
            <td width="50%"> <%-- Insert select list --%> 
              <jsp:setProperty name="listBean" property="stylesheet" value="/workflow/include/xsl/actors_select_list.xsl" /> 
              <%=listBean.getEntryListPresentation()%> 
            </td>
          </tr>
        </table>
      </td>
      <td>&nbsp;</td>
    </tr>
  </table>
<%listBean.clearErrors();%>

