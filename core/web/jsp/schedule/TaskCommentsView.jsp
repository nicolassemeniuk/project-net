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

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
| TaskCommentsView provides the display of all task comments in
| a read-only format.
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Task Comments View" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User,
			net.project.security.SessionManager,
			net.project.schedule.TaskComments"
%>

<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />                                    
<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="session" />

<jsp:useBean id="taskComments" class="net.project.schedule.TaskComments" scope="page" />

<%
    //sjmittal: removing the security access as the task would have been checked for security in the parent page
	// Load the comments for the current task
	taskComments = scheduleEntry.getComments();
	pageContext.setAttribute("taskComments", taskComments, PageContext.PAGE_SCOPE);
%>

<template:insert template="/template/IFrame.jsp"> 
	<template:put name="title" content='<%=PropertyProvider.get("prm.schedule.taskview.advanced.commentspage.title")%>' direct="true" /> 
	<template:put name="content">
		<%-- Draw the Task Comments --%>
		<pnet-xml:transform name="taskComments" stylesheet="/schedule/xsl/task-comments-view.xsl" />
	</template:put>
</template:insert>
	