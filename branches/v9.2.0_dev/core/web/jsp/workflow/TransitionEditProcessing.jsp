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
|   $Revision: 18500 $
|       $Date: 2008-12-06 05:26:03 -0200 (sáb, 06 dic 2008) $
|     $Author: umesha $
|
| Provides handling for Transition Edit JSP
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="TransitionEditProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="WorkflowErrors.jsp"
    import="net.project.base.PnetException,
    		net.project.workflow.AuthorizationRule,
    		net.project.workflow.Rule,
    		net.project.workflow.RuleFactory,
    		net.project.workflow.RuleType" 
%>
<script type="text/javascript">
	window.history.forward(-1);
</script>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" /> 
<jsp:useBean id="transitionBean" class="net.project.workflow.TransitionBean" scope="session" />
<jsp:useBean id="ruleWizard" class="net.project.workflow.RuleWizard" scope="session" />
<%
    transitionBean.setUser(user);
    transitionBean.setSpace(user.getCurrentSpace());

	String theAction = request.getParameter("theAction");
	String ruleMode = request.getParameter("ruleMode");

    if (theAction.equals("submit") ||
		theAction.equals("rule_create") ||
		theAction.equals("rule_remove")) {
%><!-- Avinash:------------------------------------------------------------------------------->
		<jsp:setProperty name="transitionBean" property="transitionID" param="transition_id" />
  <!-- Avinash: ------------------------------------------------------------------------------------->		
		<jsp:setProperty name="transitionBean" property="transitionVerb" />
		<jsp:setProperty name="transitionBean" property="description" />
		<jsp:setProperty name="transitionBean" property="beginStepID" />
		<jsp:setProperty name="transitionBean" property="endStepID" />
<%
		if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		    transitionBean.setDescription("");
		}
		transitionBean.clearErrors();
		transitionBean.validate();
		transitionBean.validateUnpublished();
		if (transitionBean.hasErrors()) {
			pageContext.forward("TransitionEdit.jsp");
		} else {
	        transitionBean.store();

			if (theAction.equals("rule_create")) {
				// Create the rule in memory and go back to TransitionEdit
				ruleWizard.clearRuleInfo();
				Rule rule = RuleFactory.createRuleForType(RuleType.AUTHORIZATION_RULE_TYPE);
				rule.setRuleTypeID(RuleType.AUTHORIZATION_RULE_TYPE.getID());
				/* Set important properties */
				rule.setWorkflowID(managerBean.getCurrentWorkflowID());
				rule.setTransitionID(managerBean.getCurrentTransitionID());
				// Set default values
				rule.setName(managerBean.getCurrentTransitionName() + " Rule");
				rule.setDescription("Authorization Rule created for transition " + managerBean.getCurrentTransitionName() + ".");
				/* Give ruleWizard handle on rule */
				ruleWizard.setRule(rule);
				ruleWizard.clearErrors();
				ruleWizard.validateRuleTypeID();
				pageContext.forward("TransitionEdit.jsp?ruleMode=create");

			} else if (theAction.equals("rule_remove")) {
				// Remove it and go back to transition edit
				Rule rule = ruleWizard.getRule();
				rule.setUser(user);
				rule.remove();
				pageContext.forward("TransitionEdit.jsp?ruleMode=");

			} else {
				// Action is submit
				if (ruleMode.equals("noRule")) {
					// Submitting and there is no rule - do nothing
					response.sendRedirect(net.project.security.SessionManager.getJSPRootURL()+ "/workflow/TransitionList.jsp?module="+net.project.base.Module.WORKFLOW+"&action="+net.project.security.Action.MODIFY+"&id="+managerBean.getCurrentWorkflowID());

				} else if (ruleMode.equals("create") || ruleMode.equals("modify")) {
					// Submitting and we were creating or modifying a rule
					// store it
					Rule rule = ruleWizard.getRule();
					rule.setUser(user);
					rule.clearErrors();
					rule.validateUnpublished();
					if (rule.hasErrors()) {
				        pageContext.forward("TransitionEdit.jsp?ruleMode=" + ruleMode);
					} else {
						// Setting group to fix BFD 2775 , 3116 , 3117
						AuthorizationRule ar = (AuthorizationRule) rule;
						ar.setGroups(request.getParameterValues("groups"));
						ar.store();
						response.sendRedirect(net.project.security.SessionManager.getJSPRootURL()+ "/workflow/TransitionList.jsp?module="+net.project.base.Module.WORKFLOW+"&action="+net.project.security.Action.MODIFY+"&id="+managerBean.getCurrentWorkflowID());
					}
				} //end if ruleMode.equals()
				
			} //end if (theAction.equals())
			
		} //end if (transitionBean.hasErrors())

    } else if (theAction.equals("rule_create")) {
		pageContext.forward("RuleWizardStart.jsp");

    } else if (theAction.equals("rule_modify")) {
		pageContext.forward("RuleWizardStart.jsp");
	
    } else if (theAction.equals("rule_remove")) {
		pageContext.forward("RuleRemoveConfirm.jsp");
	
    } else if (theAction.equals("rule_properties")) {
		throw new PnetException("View rule properties functionality not yet implemented.");

    } else {
		// Problem
		throw new PnetException("Unknown action passed to TransitionEditProcessing: " + theAction);
	}
%>
<%-- End of action handling --%>
