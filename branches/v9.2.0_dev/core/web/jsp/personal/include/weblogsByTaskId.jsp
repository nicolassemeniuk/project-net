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
<%@ page language="java" contentType="text/xml; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="net.project.hibernate.service.ServiceFactory"%>
<%@page import="net.project.hibernate.service.IBlogProvider"%>
<%@page import="java.util.List"%>
<%@page import="net.project.hibernate.model.PnWeblogEntry"%>
<%@page import="java.util.Date"%>
<%@page import="net.project.hibernate.constants.WeblogConstants"%>
<%@page import="net.project.base.property.PropertyProvider"%>

<%

	if((request.getParameter("task") != null) && !request.getParameter("task").equals("")) {
		int task = new Integer(request.getParameter("task"));
		%>
		
<b><%=PropertyProvider.get("prm.project.personal.include.blog.entries.label")%><%= task %></b>
		<br /><p />
		<%
		ServiceFactory factory = ServiceFactory.getInstance();
		IBlogProvider blogProvider = factory.getBlogProvider();
		List<PnWeblogEntry> weblog = blogProvider.getWeblogEntriesByTaskId(task);
		
		if((weblog != null) && (weblog.size() > 0)) {
			for(int i=0; i<weblog.size(); i++) {
				PnWeblogEntry entry = weblog.get(i);
	%>
				<%= entry.getPubTimeString() %><br />
				<br /><p />
				<%= entry.getTitle() %><br />
				<br /><p />
				<br /><p />
				<%= entry.getText() %><br />
				<%= entry.getPnWeblogComment().size() %> <%=PropertyProvider.get("prm.project.personal.include.blog.comments.label")%> <a href="#">
				<%=PropertyProvider.get("prm.project.personal.include.blog.add.comment.label")%></a> | <a href="#"><%=PropertyProvider.get("prm.project.personal.include.blog.edit.label")%></a><br />
				<hr />
	<%

			}
		}
	}

%>