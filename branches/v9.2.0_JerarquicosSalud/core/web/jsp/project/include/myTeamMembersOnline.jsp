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
    info="My Team Members Online Channel"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.project.ProjectSpaceBean,
            net.project.security.BuddyListBean,
            net.project.security.User" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="buddyList" class="net.project.security.BuddyListBean" scope="page" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />
<% 
	buddyList.setUser(user);
    buddyList.setSpace(projectSpace);
    buddyList.load();

    pageContextManager.setProperty("directory.url.complete", net.project.security.SessionManager.getJSPRootURL() + "/project/Main.jsp");
%>

<%-- Apply stylesheet to format myTeamMembersOnline channel --%>
<pnet-xml:transform name="buddyList" scope="page" stylesheet="/project/include/buddy-list.xsl">
    <pnet-xml:property name="JSPRootURL" value="<%=net.project.security.SessionManager.getJSPRootURL()%>" />
</pnet-xml:transform>
