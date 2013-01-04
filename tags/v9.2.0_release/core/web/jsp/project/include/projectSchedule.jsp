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
<%@page import="net.project.hibernate.service.IPnProjectSpaceService"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="net.project.hibernate.model.project_space.ProjectSchedule"%>
<%@page import="net.project.base.Module"%>
<%@page import="net.project.security.SessionManager"%>
<%@page import="net.project.util.DateFormat"%>
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%

	SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy");
	DateFormat userDateFormatter = SessionManager.getUser().getDateFormatter();
	IPnProjectSpaceService service = ServiceFactory.getInstance().getPnProjectSpaceService();
	ProjectSchedule projectSchedule = service.getProjectSchedule(Integer.parseInt(projectSpace.getID()));
	String startDate = "";
	String startActualDate = "";
	String finishDate = "";
	String finishActualDate = "";
	
	if(projectSchedule.getPlannedStart() != null) {
		//startDate = sdf.format(projectSchedule.getPlannedStart());
		startDate = userDateFormatter.formatDate(projectSchedule.getPlannedStart(), "dd/MM/yyyy");
	}
	
	if(projectSchedule.getActualStart() != null) {
		//startActualDate = sdf.format(projectSchedule.getActualStart());
		startActualDate = userDateFormatter.formatDate(projectSchedule.getActualStart(), "dd/MM/yyyy");
	}
	
	if(projectSchedule.getPlannedFinish() != null) {
		//finishDate = sdf.format(projectSchedule.getPlannedFinish());
		finishDate = userDateFormatter.formatDate(projectSchedule.getPlannedFinish(), "dd/MM/yyyy");
	}
	
	if(projectSchedule.getActualFinish() != null) {
		//finishActualDate = sdf.format(projectSchedule.getActualFinish());
		finishActualDate = userDateFormatter.formatDate(projectSchedule.getActualFinish(), "dd/MM/yyyy");
	}

%>
	<div id="project_schedule">
		<div>
			<span id="project_schedule_title">Schedule</span><br />
			<div id="project_schedule">
				<table cellspacing="5" cellpadding="0" style="font-size: small;">
					<tr>
						<td></td>
						<td><b>Start</b></td>
						<td><b>Finish</b></td>
					</tr>
					<tr>
						<td><b>Planned:</b></td>
						<td><%= startDate %></td>
						<td><%= finishDate %></td>
					</tr>
					<tr>
						<td><b>Actual:</b></td>
						<td><%= startActualDate %></td>
						<td><%= finishActualDate %></td>
					</tr>
				</table>
				<br /><p />
				<a href="javascript:showReport('ltr')"><%= projectSchedule.getNumberOfLateTasks() %> late tasks</a><br />
				<a href="javascript:showReport('tcdr')"><%= projectSchedule.getNumberOfTaskComingDue() %> tasks coming due this week</a><br />
				<a href="javascript:showReport('unassigned')"><%= projectSchedule.getNumberOfUnassignedTasks() %> unassigned tasks</a><br />
				<a href="javascript:showReport('wcr')"><%= projectSchedule.getNumberOfCompletedTasks() %> tasks completed</a><br />
			</div>
		</div>
		<div>
			<img src="<%=SessionManager.getJSPRootURL() %>/servlet/PieChartServlet?project=<%= projectSpace.getID() %>&module=<%= Module.PROJECT_SPACE %>" />
		</div>
	</div>