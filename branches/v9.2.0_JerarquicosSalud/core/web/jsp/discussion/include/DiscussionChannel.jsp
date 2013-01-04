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
    info="Discussion Channel" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.discussion.DiscussionManager,
			net.project.security.SessionManager"
    
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="discussions" class="net.project.discussion.DiscussionManager" scope="session" />

<%
	// Configure the discussion manager
	discussions.setSpace(user.getCurrentSpace());
	discussions.setUser(user);
%>

<jsp:include page="/discussion/include/discussionList.jsp" flush="true">
	<jsp:param name="isSelectable" value="0" />
</jsp:include>
