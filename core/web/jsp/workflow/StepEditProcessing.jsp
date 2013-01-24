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

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="StepEditProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/workflow/WorkflowErrors.jsp"
    import="net.project.base.PnetException,
    		net.project.security.SessionManager" 
%>
<script type="text/javascript">
	 javascript:window.history.forward(-1);	
</script>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="stepBean" class="net.project.workflow.StepBean" scope="session" />
<jsp:useBean id="transitionBean" class="net.project.workflow.TransitionBean" scope="session" />
<jsp:useBean id="listBean" class="net.project.workflow.SelectListBean" scope="session" />
<jsp:useBean id="managerBean" class="net.project.workflow.WorkflowManagerBean" scope="session" /> 
<%
    stepBean.setUser(user);
    stepBean.setSpace(user.getCurrentSpace());

	String theAction = request.getParameter("theAction");
	String groupMode = request.getParameter("groupMode");
    if (theAction.equals("submit") || 
		theAction.equals("stepgroup_modify") ||
		theAction.equals("grouplist_select") ||
		theAction.equals("grouplist_deselect")) {
%><!-- Avinash:---------------------------------------------------------------------->
		<jsp:setProperty name="stepBean" property="stepID" param="step_id" />
<!-- Avinash:---------------------------------------------------------------------->
		<jsp:setProperty name="stepBean" property="name" />
        <jsp:setProperty name="stepBean" property="sequence" />
		<jsp:setProperty name="stepBean" property="description" />
		<jsp:setProperty name="stepBean" property="initialStep" />
		<jsp:setProperty name="stepBean" property="finalStep" />
		<jsp:setProperty name="stepBean" property="notes" />
<%
		if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		    stepBean.setDescription("");
		}
		if ((request.getParameter("notes") == null) || (request.getParameter("notes").equals(""))) {
		    stepBean.setNotes("");
		}
	    stepBean.setEntryStatusID(net.project.util.StringUtils.isEmpty(request.getParameter("entryStatusID")) ? null : request.getParameter("entryStatusID"));
		stepBean.clearErrors();
		stepBean.validate();
		stepBean.validateUnpublished();

		// If the group list is in modify mode, set those values too
		if (groupMode.equals("modify")) {
			// Regardless of action, set the notification and persistence stuff
			String[] notification = request.getParameterValues("notificationList");
			if (notification != null) {
				for (int i = 0; i < notification.length; i++) {
					listBean.setNotification(notification[i]);
				}
			}
			String[] participation = request.getParameterValues("participationList");
			if (participation != null) {
				for (int i = 0; i < participation.length; i++) {
					listBean.setParticipation(participation[i]);
				}
			}
			listBean.clearErrors();
			listBean.validateUnpublished();
		}

		if (stepBean.hasErrors() || (groupMode.equals("modify") && listBean.hasErrors()) ) {
			pageContext.forward("StepEdit.jsp");

		} else {
			// Store step details
	        stepBean.store();

			// Store / select / deselect groups
			if (groupMode.equals("modify")) {
				if (theAction.equals("submit")) {
					listBean.store();
					listBean.clear();

				} else if (theAction.equals("grouplist_select")) {
					// Iterate over sel[] and select those people
					String[] sel = request.getParameterValues("sel");
					if (sel != null) {
						for (int i = 0; i < sel.length; i++) {
							listBean.select(sel[i]);
						}
					}

				} else if (theAction.equals("grouplist_deselect")) {
					// Iterate over desel[]
					String[] desel = request.getParameterValues("desel");
					if (desel != null) {
						for (int i = 0; i < desel.length; i++) {
							listBean.deselect(desel[i]);
						}
					}
				}
			}

			// Go back to StepEdit if they clicked modify or select or deselect
			if (theAction.equals("submit")) {
				response.sendRedirect(SessionManager.getJSPRootURL()+ "/workflow/StepList.jsp?module="+net.project.base.Module.WORKFLOW+"&action="+net.project.security.Action.MODIFY+"&id="+managerBean.getCurrentWorkflowID());
			} else if (theAction.equals("stepgroup_modify")) {
				pageContext.forward("StepEdit.jsp?groupMode=modify&loadGroupList=true");
			} else if (theAction.equals("grouplist_select") ||
					   theAction.equals("grouplist_deselect")) {
				pageContext.forward("StepEdit.jsp?groupMode=modify&loadGroupList=false");
			} else {
			}
		}

    } else if (theAction.equals("transition_create")) {
		// Create new transition
		transitionBean.clear();
		pageContext.forward("TransitionCreate.jsp");

    } else if (theAction.equals("transition_modify")) {
		// Modify transition
		pageContext.forward("TransitionEdit.jsp");
	
    } else if (theAction.equals("transition_remove")) {
		transitionBean.clear();
		pageContext.forward("TransitionRemoveConfirm.jsp");
	
    } else if (theAction.equals("transition_properties")) {
		throw new PnetException("View transition properties functionality not yet implemented.");

	} else if (theAction.equals("stepgroup_modify")) {
		listBean.clear();
		pageContext.forward("StepActorsSelect.jsp");
		
	} else {
		throw new PnetException("Unknown action '" + request.getParameter("theAction") + "' passed to StepEditProcessing.jsp");
	}
%>
<%-- End of action handling --%>
