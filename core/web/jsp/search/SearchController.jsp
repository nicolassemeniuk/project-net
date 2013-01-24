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
|   $Revision: 20727 $
|       $Date: 2010-04-20 11:28:19 -0300 (mar, 20 abr 2010) $
|     $Author: avinash $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Space Controller"
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
    String id = user.getCurrentSpace().getID();
	
    //Pass on referring link, or if not, construct the main page of the current
    //space as a default place to return.
	String refLink, refLinkEncoded = null;	
	boolean check = false;
	int module=0;
	int action=net.project.security.Action.VIEW;
	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");

	if (request.getParameter("module") != null) {
		module = Integer.parseInt(request.getParameter("module"));
	}
	if (request.getParameter("action") != null) {
		action = Integer.parseInt(request.getParameter("action"));
	}
	
	if (refLink == null) {
        refLink = SpaceURLFactory.constructSpaceURLForMainPage(user.getCurrentSpace(),module,action);
		check = true;
    }
	
	search.clear();
	search.setRefererLink(refLink);
	search.setModuleID(module);
	search.setActionID(action);
	 
    Space currentSpace = user.getCurrentSpace();
	
    // If portfolio page then search in all the projects under this portfolio
    if(session.getAttribute("isFromProjectPortfolio") != null){
	    search.setPortofolioSpaces(user.getID());
    }
    search.addSearchSpace(currentSpace.getID(), currentSpace.getName(), currentSpace.getDescription());

%>

<%
    String mode = request.getParameter("mode");
	
    //Go straight to processing if we should perform the search immediately.
    if (check) {
%>
   <jsp:forward page="/search/SearchSpace.jsp"/>
		
<%	} else if (mode != null && mode.equals("performsearch")) { 
%>
		<jsp:forward page="/search/SearchProcessing.jsp" />
		
<%	} else { 
%>
		<jsp:forward page="/search/Search.jsp" />
		
<%	} %>

