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
| Search Processing
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Search Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.ObjectType,
			net.project.search.IObjectSearch,
			net.project.search.SearchManager,
            net.project.security.SessionManager,
            net.project.security.User" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
	String objectType = request.getParameter("otype");
		
	search.clearResults();
			
	if (objectType.equals(ObjectType.ALL)) {
		search.setSearchType(IObjectSearch.QUICK_SEARCH);
		
	} else if ( request.getParameter("type") != null && !request.getParameter("type").equals("")) {
		search.setSearchType(Integer.parseInt(request.getParameter("type")));	
	} else if (search.isSearchTypeSupported(IObjectSearch.ADVANCED_SEARCH)) {
		search.setSearchType(IObjectSearch.ADVANCED_SEARCH);
	} else {
		search.setSearchType(search.getDefaultSearchType());
	}
	
	search.setSearchObjectType(objectType);
	search.setMaxDisplayNum(10);
	search.doSearch(request);
	
	if(search.getSearchErrors().size()>0)
	{
		pageContext.forward("/search/Search.jsp");
	}
	
	// For searhing "all" object types use special page for presentation
	// QuickSearching Forms searches all forms and also uses this page for presentatation.
	// Multiple space search also uses that form
	if (objectType != null && 
		(objectType.equals("all") || (objectType.equals(ObjectType.FORM_DATA) && (search.getSearchType() == IObjectSearch.QUICK_SEARCH)) )) {
		pageContext.forward("QuickSearchResults.jsp");

	} else if (search.getSearchSpaces().size() > 1) {
		pageContext.forward("QuickSearchResults.jsp");

	} else {
		pageContext.forward("/search/SearchResults.jsp");
	}
%>
