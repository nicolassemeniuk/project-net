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
|   This page provides a frame set for the post search form and results
|
|   Beans:
|   discussion    The active discussion group should already be loaded in the session|
|   
|    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Discussion - Search Frame set" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.DiscussionGroupBean,
			net.project.security.SecurityProvider" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.DISCUSSION%>" 
					   objectID="<%=discussion.getID()%>"/> 
<html>
<head>
<META http-equiv="expires" content="0"> 
<title><%=PropertyProvider.get("prm.discussion.searchframesetpage.title")%></title>
</head>
<frameset rows="200,*" border=0 frameborder=no >
	<FRAME SRC="SearchForm.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>&id=<%= discussion.getID() %>" name="searchForm_frame" marginwidth=20 frameborder=0 >
	<FRAME SRC="SearchResults.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>&id=<%= discussion.getID() %>" name="searchForm_results" marginwidth=8 frameborder=0 >
</frameset>
</html>