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
| Special Rule Edit page - allows direct editing of Authorization rule
| Input request parameters:
|	ruleMode	(none) : Checks to see if a rule has already been defined
|						 and sets ruleMode to "noRule" or "modify"
|				create : We are creating a new rule.  Assumes new rule has
|						 been created.
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Rule Edit Special"
    language="java" 
    errorPage="/workflow/WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.*,
			net.project.workflow.*, 
			net.project.security.*,
			net.project.persistence.*, 
			net.project.base.PnetException" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="transitionBean" class="net.project.workflow.TransitionBean" scope="session" />
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" />
<jsp:useBean id="ruleWizard" class="net.project.workflow.RuleWizard" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<%
	ruleWizard.setSpace(user.getCurrentSpace());

	// Set current user and space
	managerBean.setUser(user);
    managerBean.setSpace(user.getCurrentSpace());
	
	String ruleMode = null;
	if (request.getParameter("ruleMode") != null && !request.getParameter("ruleMode").equals("")) {
		ruleMode = request.getParameter("ruleMode");
	}

	// Mode is not create - check to see if a rule has been defined.
	// This also loads the rule into the ruleWizard if there is one.
	if (ruleMode == null) {
		if (ruleWizard.isRuleDefined(transitionBean, RuleType.AUTHORIZATION_RULE_TYPE)) {
			ruleMode = "modify";
		} else {
			ruleMode = "noRule";
		}
	}
	
	// Now fetch loaded rule (or newly created rule) out of ruleWizard
	Rule rule = null;
	if (ruleMode.equals("create") || ruleMode.equals("modify")) {
		rule = ruleWizard.getRule();
	}

%>
<security:verifyAccess objectID="<%=transitionBean.getWorkflowID()%>"
					   action="modify"
					   module="<%=net.project.base.Module.WORKFLOW%>"
/> 

<input type="hidden" name="ruleMode" value="<%=ruleMode%>" />
<%
	if (ruleMode.equals("noRule")) {
%>
<%-- No rule has been created.  Inform user of this, provide button to create rule --%>
<table border="0" cellspacing="0" cellpadding="0" width="50%">
	<tr class="channelHeader" align="left">
    	<td class="channelHeader" width="1%"><img src="<session:getJSPRootURL />/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
		<td class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.workflow.include.rulelist.channel.rules.title")%></td>
		<td class="channelHeader" align="right">
			<tb:toolbar style="channel" showLabels="true">
				<tb:band name="channel">
					<tb:button type="create" function="javascript:ruleCreate();" />
				</tb:band>
			</tb:toolbar>
		</td>
        <td class="channelHeader" align="right" width="1%"><img src="<session:getJSPRootURL />/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
    <tr>
		<td>&nbsp;</td>
        <td colspan="2" class="tableContent" align="left">
			<%=PropertyProvider.get("prm.workflow.include.ruleeditspecial.message")%>
        </td>
		<td>&nbsp;</td>
    </tr>
</table>
<%
	} else {
%>
<%-- Either there is an existing rule or a rule is being created --%>
<table border="0" cellspacing="0" cellpadding="0" width="50%">
	<tr class="channelHeader" align="left">
    	<th class="channelHeader" width="1%"><img src="<session:getJSPRootURL />/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.workflow.include.rulelist.channel.rules.title")%></th>
		<th class="channelHeader" align="right">
			<tb:toolbar style="channel" showLabels="true">
				<tb:band name="channel">
					<tb:button type="remove" function="javascript:ruleRemove();" />
				</tb:band>
			</tb:toolbar>
		</th>
        <th class="channelHeader" align="right" width="1%"><img src="<session:getJSPRootURL />/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
    <tr>
		<td>&nbsp;</td>
        <td colspan="2" class="tableHeader" align="left">
	  		<%-- Insert instructions for this rule type --%>
			<%=ruleWizard.getRuleTypeNotes()%>
        </td>
		<td>&nbsp;</td>
    </tr>
	<tr>
		<%-- Display any problems --%>
		<td>&nbsp;</td>
		<td colspan="2"><%=rule.getErrorsTable()%></td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td colspan="2">
		    <%-- Custom Properties Edit Form --%> 
			<jsp:setProperty name="ruleWizard" property="stylesheet" value="/workflow/include/xsl/rule_properties_edit.xsl" />
			<jsp:getProperty name="ruleWizard" property="rulePropertiesEditPresentation" />
		</td>
	    <td>&nbsp;</td>
	</tr>
</table>
<%
	}
%>
