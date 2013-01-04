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
       import="net.project.security.*" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="buddyList" class="net.project.security.BuddyListBean" scope="page" />

<% 
	buddyList.setUser(user);
%>

<%-- Apply stylesheet to format myTeamMembersOnline channel --%>
<jsp:setProperty name="buddyList" property="stylesheet" value="/xsl/buddy-list.xsl" />
<jsp:getProperty name="buddyList" property="presentation" />

