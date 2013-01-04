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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Search Space"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.search.SearchManager,
            net.project.security.SessionManager,
            net.project.security.User,
            net.project.space.Space,
            net.project.space.SpaceURLFactory,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<%
	// Mostly a redundant page for the time being but can see utility afterwards
	// -- deepak
	
	  String mode = request.getParameter("mode");
    //Go straight to processing if we should perform the search immediately.
    if (mode != null && mode.equals("performsearch")) {
%>
    <jsp:forward page="/search/SearchProcessing.jsp" />
<%	} else { 
%>
		<jsp:forward page="/search/Search.jsp" />
		
<%	} %>
