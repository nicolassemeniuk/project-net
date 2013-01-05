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
|   This page displays a threaded list of posts within a discussion group.
|
|   Beans:
|   discussion    The active discussion group should already be loaded in the session|
|   
|    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Discussion - Post Search Results frame" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.DiscussionGroupBean,
			net.project.discussion.SearchBean,
			net.project.security.User,
			net.project.security.SecurityProvider,
			net.project.space.Space" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="discussion" class="net.project.discussion.DiscussionGroupBean" scope="session" />
<jsp:useBean id="discussionSearch" class="net.project.discussion.SearchBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.DISCUSSION%>" 
					   objectID="<%=discussion.getID()%>"/> 

<% discussionSearch.setDiscussion(discussion); %>
<jsp:setProperty name="discussionSearch" property="*" />

<html>
<head>
<%-- Import CSS --%>
<template:getSpaceCSS />
</head>
<body class="main">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td width="2%">&nbsp;</td>
		<td align="left" class="tableContent">
<% 
    if (request.getParameter("mode") == null)
        {
%>
<%=PropertyProvider.get("prm.discussion.searchresults.tofindpost.instruction")%>
<%
        }
   else
        {
%>
<jsp:getProperty name="discussionSearch" property="HTML" />
<%
        }
%>
		</td>
		<td width="2%">&nbsp;</td>
	</tr>
</table>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>
