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
|   $Revision: 18465 $
|       $Date: 2008-12-02 17:02:12 -0200 (mar, 02 dic 2008) $
|     $Author: nilesh $
|
| Project Search
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Project Search" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.search.SearchManager,
            net.project.security.SessionManager,
            net.project.security.User,
			net.project.space.Space,
			net.project.base.Module" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
	// Pass on referring link, or use project Main as default place to return
	// to when search is done.
	String refLink, refLinkEncoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");
	if (refLink == null) {
		refLink = SessionManager.getJSPRootURL()+ "/project/Dashboard?module=" + Module.PROJECT_SPACE;
		refLinkEncoded = java.net.URLEncoder.encode(refLink);
	}
%>

<%
	String mode = request.getParameter("mode");

	// Add this space to be searched
	search.clear();
	Space currentSpace = user.getCurrentSpace();
	if(request.getParameter("type") != null && !request.getParameter("type").equals("")) {
		search.setSearchType(Integer.parseInt(request.getParameter("type")));
	}
	search.addSearchSpace(currentSpace.getID(), currentSpace.getName(), currentSpace.getDescription());

	// Go straight to processing if we should perform search immediately
	if (mode != null && mode.equals("performsearch")) {
%>
		<jsp:forward page="/search/SearchProcessing.jsp">
			<jsp:param name="refLink" value="<%=refLink%>" />
		</jsp:forward>

<%	} else { %>

		<jsp:forward page="/search/Search.jsp">
			<jsp:param name="refLink" value="<%=refLink%>" />
		</jsp:forward>

<%	} %>
