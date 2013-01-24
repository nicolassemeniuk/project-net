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
    import="net.project.security.User,
		    net.project.news.NewsManagerBean" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="newsManager" class="net.project.news.NewsManagerBean" scope="session" />
<% 
	newsManager.setSpace(user.getCurrentSpace());
	newsManager.setUser(user);
	newsManager.setTruncatedPresentableMessageLength(390);
    newsManager.setTruncatedPresentableMessageMaxParagraphs(5);
%>
<%
	// Check attribute then parameter (attribute may be set by a channel page)
	String filter_viewRange = (String) request.getAttribute("filter_viewRange");
	if (filter_viewRange == null || filter_viewRange.equals("")) {
		filter_viewRange = request.getParameter("filter_viewRange");
	}
	if (filter_viewRange != null) {
		newsManager.setViewRange(Integer.parseInt(filter_viewRange));
	}
%>

<jsp:setProperty name="newsManager" property="stylesheet" value="/news/xsl/news-channel-dashboard.xsl" />
<jsp:getProperty name="newsManager" property="newsItemsPresentation" />
