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
    info="News" 
    language="java" 
    errorPage="/errors.jsp"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<%@page import="net.project.hibernate.service.ServiceFactory"%>
<%@page import="net.project.hibernate.model.PnPerson"%>
<%@page import="java.util.List"%>
<%@page import="net.project.hibernate.service.IPnProjectSpaceService"%>
<%@page import="net.project.hibernate.model.project_space.ProjectPhase"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="net.project.hibernate.model.PnTask"%>
<%@page import="net.project.security.SessionManager"%>
<%@page import="net.project.base.property.PropertyProvider"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="net.project.util.DateFormat"%>
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<table cellspacing="0" cellpadding="0" width="100%">
	<tr class="tableHeader">
		<td class="tableHeader">Name</td>
		<td class="tableHeader">End Date</td>
		<td class="tableHeader">Status</td>
		<td class="tableHeader">Progress</td>
	</tr>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	DateFormat userDateFormatter = SessionManager.getUser().getDateFormatter();
	IPnProjectSpaceService service = ServiceFactory.getInstance().getPnProjectSpaceService();
	List<ProjectPhase> phases = service.getProjectPhasesAndMilestones(Integer.parseInt(projectSpace.getID()));
	for(int i=0; i<phases.size(); i++) {
		ProjectPhase phase = phases.get(i);
		String endDate = "";
		if(phase.getEndDate() != null) {
			endDate = userDateFormatter.formatDate(phase.getEndDate(), "dd/MM/yyyy");
		}
%>
	<tr class="tableLine">
		<td colspan="4" class="tableLine"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="2" border="0"/></td>
	</tr>
	<tr>
		<td class="tableContent"><a href="<%= SessionManager.getJSPRootURL() %>/process/ViewPhase.jsp?id=<%= phase.getPhaseId() %>&module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.VIEW%>"><%= phase.getPhaseName() %></a>&nbsp;</td>
		<td class="tableContent">&nbsp;<%= endDate %></td>
		<td class="tableContent"><%= PropertyProvider.get(phase.getStatusCode()) %>&nbsp;</td>
		<td class="tableContent">
			<table>
				<tr>
					<td>
						<div style="width: 100px;">
							<table border="1" width="100" height="10" cellspacing="0" cellpadding="0">
								<tr>
									<td bgcolor="#FFFFFF" title="<%= phase.getPercentComplete() %>%">
										<img src="<%= SessionManager.getJSPRootURL() %>/images/lgreen.gif" width="<%= phase.getPercentComplete() %>" height="10"/>
									</td>
								</tr>
							</table>
						</div>
					</td>
					<td style="font-size: small;">
						<%= phase.getPercentComplete() %>%
					</td>
				</tr>
			</table>
		</td>
	</tr>
<%		
		
		List<PnTask> milestones = phase.getMilestones();
		if((milestones != null) && (milestones.size() > 0)) {
			for(int j=0; j<milestones.size(); j++) {
				PnTask milestone = milestones.get(j);
				String taskDate = "";
				if(milestone.getDateFinish() != null) {
					taskDate = userDateFormatter.formatDate(milestone.getDateFinish(), "dd/MM/yyyy");
				}
				
%>
	<tr class="tableLine">
		<td colspan="4" class="tableLine"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="2" border="0"/></td>
	</tr>
	<tr>
		<td class="tableContent">&nbsp;&nbsp;<img src="<%=SessionManager.getJSPRootURL() %>/images/milestone.gif" /> <a href="<%= SessionManager.getJSPRootURL() %>/servlet/ScheduleController/TaskView?module=60&action=1&id=<%= milestone.getTaskId() %>"><%= milestone.getTaskName() %></a>&nbsp;</td>
		<td class="tableContent" colspan="2">&nbsp;<%= taskDate %></td>
		<td class="tableContent"><%= (milestone.getPercentComplete() != null)?milestone.getPercentComplete():0 %>%&nbsp;</td>
	</tr>
<%
			}
		}
	}
	
	List<PnTask> milestones = ServiceFactory.getInstance().getPnTaskService().getProjectMilestones(Integer.parseInt(projectSpace.getID()), true);
	//DateFormat userDateFormatter = SessionManager.getUser().getDateFormatter(); 
	if((milestones != null) && (milestones.size() > 0)) {
		for(int j=0; j<milestones.size(); j++) {
			PnTask milestone = milestones.get(j);
			String taskDate = "";
			if(milestone.getDateFinish() != null) {
				taskDate = userDateFormatter.formatDate(milestone.getDateFinish(), "dd/MM/yyyy");
			}
%>
			<tr class="tableLine">
				<td colspan="4" class="tableLine"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="2" border="0"/></td>
			</tr>
			<tr>
				<td class="tableContent"><img src="<%=SessionManager.getJSPRootURL() %>/images/milestone.gif" /> <a href="<%= SessionManager.getJSPRootURL() %>/servlet/ScheduleController/TaskView?module=60&action=1&id=<%= milestone.getTaskId() %>"><%= milestone.getTaskName() %></a>&nbsp;</td>
				<td class="tableContent" colspan="2">&nbsp;<%= taskDate %></td>
				<td class="tableContent"><%= (milestone.getPercentComplete() != null)?milestone.getPercentComplete():0 %>%&nbsp;</td>
			</tr>
<%
		}
	}
%>
</table>
