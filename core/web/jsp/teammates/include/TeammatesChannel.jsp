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
    import="net.project.security.User,
    		net.project.security.SessionManager,
		    net.project.news.NewsManagerBean" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<%@page import="net.project.hibernate.service.ServiceFactory"%>
<%@page import="net.project.hibernate.service.IPnPersonService"%>
<%@page import="net.project.hibernate.model.PnPerson"%>
<%@page import="java.util.List"%>
<%@page import="net.project.hibernate.service.IPnAssignmentService"%>
<%@page import="net.project.hibernate.model.project_space.Teammate"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
	Calendar c = Calendar.getInstance();
	Date start = c.getTime();
	c.add(Calendar.MONTH, 3);
	Date finish = c.getTime();
	
%>
	<table cellspacing="0" cellpadding="0" width="100%">
		<tr class="tableHeader">
			<td class="tableHeader">
				Name
			</td>
			<td colspan="2" class="tableHeader">
				<center>
					Assignments
				</center>
			</td>
		</tr>
<%
	
	IPnAssignmentService service = ServiceFactory.getInstance().getPnAssignmentService();
	List<Teammate> teammates = service.getAssignmentsByPersonForProject(Integer.parseInt(projectSpace.getID()), start, finish);
	for(int i=0; i<teammates.size(); i++) {
		Teammate teammate = teammates.get(i);
		
%>
		<tr class="tableLine">
			<td colspan="3" class="tableLine"><img src="<%=SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="2" border="0"/></td>
		</tr>
		<tr>
			<td class="tableContent">
				<%
				
					String name = "";
					if(teammate.getFirstName() != null) {
						name = teammate.getFirstName().substring(0, 1) + ". ";
					}
					
					name += teammate.getLastName();
				
					if(teammate.isOnline()) {
%>
						<img src='<%=SessionManager.getJSPRootURL() %>/images/project/teammate_online.gif' />
<%
					} else {
%>
						<img src='<%=SessionManager.getJSPRootURL() %>/images/project/teammate_offline.gif' />
<%
					}
				
				%>
				 <%= name %>
			</td>
			<td class="tableContent" width="15">
<%
			if(teammate.isOverassigned()) {
%>
				<a href="javascript:showResourceAllocation(<%= teammate.getPersonId() %>)"><img src="<%=SessionManager.getJSPRootURL() %>/images/schedule/overassignment.GIF" /></a>
<%
			} else {
%>
				<a href="javascript:showResourceAllocation(<%= teammate.getPersonId() %>)"><img src="<%=SessionManager.getJSPRootURL() %>/images/schedule/constraint.gif" /></a>
<%
			}
%>
			</td>
			<td class="tableContent">
				<center>
					<a href="javascript:showTeammateTasksReport('<%= teammate.getPersonId() %>')"><%= teammate.getAssignments().size() %></a>
				</center>
			</td>
		</tr>
<%
	}
%>
	</table>
