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
<%@page import="java.util.List"%>
<%@page import="net.project.hibernate.service.IPnProjectSpaceService"%>
<%@page import="net.project.hibernate.model.project_space.ProjectChanges"%>
<%@page import="net.project.hibernate.model.project_space.ObjectChanged"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="net.project.security.SessionManager"%>
<%@page import="net.project.base.Module"%>
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%

	SimpleDateFormat sdf = new SimpleDateFormat("h:ma, MM/dd/yyyy");
	IPnProjectSpaceService service = ServiceFactory.getInstance().getPnProjectSpaceService();
	ProjectChanges changes = service.getProjectChanges(Integer.parseInt(projectSpace.getID()), 7);

%>
	<br /><p />
	<b>Documents</b><br />
<%
	List<ObjectChanged> documents = changes.getDocuments();
	if(documents.size() > 0) {
		for(int i=0; i<documents.size(); i++) {
			ObjectChanged document = documents.get(i);
			String date = "";
			if(document.getDateOfChange() != null) {
				date = sdf.format(document.getDateOfChange());
			}
%>
	<a href="<%= SessionManager.getJSPRootURL() %>/document/GoToDocument.jsp?id=<%= document.getObjectId() %>&module=<%= Module.DOCUMENT %>"><%= document.getObjectName() %></a><%= (!date.equals(""))?", "+date:"" %><br />
<%
		}
	} else {
%>
		No changes
<%
	}

%>
	<br /><p />
	<br /><p />
	<b>Forms</b><br />
<%
	List<ObjectChanged> forms = changes.getForms();
	if(forms.size() > 0) {
		for(int i=0; i<forms.size(); i++) {
			ObjectChanged form = forms.get(i);
			String date = "";
			if(form.getDateOfChange() != null) {
				date = sdf.format(form.getDateOfChange());
			}
%>
	<a href="<%= SessionManager.getJSPRootURL() %>/form/FormList.jsp?module=<%= Module.FORM %>&action=1&id=<%= form.getObjectId() %>"><%= form.getObjectName() %></a><%= (!date.equals(""))?", "+date:"" %><br />
<%
		}
	} else {
%>
		No changes
<%
	}

%>
	<br /><p />
	<br /><p />
	<b>Discussions</b><br />
<%
	List<ObjectChanged> discussions = changes.getDiscussions();
	if(discussions.size() > 0) {
		for(int i=0; i<discussions.size(); i++) {
			ObjectChanged discussion = discussions.get(i);
			String date = "";
			if(discussion.getDateOfChange() != null) {
				date = sdf.format(discussion.getDateOfChange());
			}
%>
	<a href="<%= SessionManager.getJSPRootURL() %>/discussion/GroupView.jsp?module=<%= Module.DISCUSSION %>&action=1&id=<%= discussion.getObjectId() %>"><%= discussion.getObjectName() %></a><%= (!date.equals(""))?", "+date:"" %>
<%
		}
	} else {
%>
		No changes
<%
	}
%>
