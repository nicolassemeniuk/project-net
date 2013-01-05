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
    info="Discussion" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.discussion.DiscussionManager,
			net.project.security.SessionManager"
    
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="discussions" class="net.project.discussion.DiscussionManager" scope="session" />

<%
	// Determine whether the discussion groups should have a
	// selection html input field (e.g. a radio group)
	// The dicussions channel 
	String isSelectable = request.getParameter("isSelectable");
	if (isSelectable == null || isSelectable.length() == 0) {
		isSelectable = "1";
	}
%>

<%-- Apply stylesheet to format discussion group listing rows --%>
<pnet-xml:transform name="discussions" scope="session" stylesheet="/discussion/xsl/discussions.xsl">
	<pnet-xml:property name="JSPRootURL" value="<%=SessionManager.getJSPRootURL()%>" />
	<pnet-xml:property name="isSelectable" value="<%=isSelectable%>" />
</pnet-xml:transform>
