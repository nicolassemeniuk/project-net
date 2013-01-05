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
| Search page
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Search" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
			net.project.search.IObjectSearch,
			net.project.search.SearchManager,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager,
            net.project.security.User,
            net.project.base.ObjectType,
            net.project.space.SpaceType,
            net.project.util.Validator,
            net.project.space.Space,
            net.project.space.SpaceTypes"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%	
	if(request.getParameter("module") != null) {
		search.setModuleID(Integer.parseInt(request.getParameter("module")));
	}

	if(request.getParameter("action") != null) {
		search.setActionID(Integer.parseInt(request.getParameter("action")));
	}	

	if(request.getParameter("refLink") != null) {
		search.setRefererLink(request.getParameter("refLink"));
	}

	String objectType = request.getParameter("otype");
	// Default unkown search object type to "all".
	if ((objectType == null) || objectType.equals("")) {
		objectType = ObjectType.ALL;
	}
	search.setAllowAll(true);
	search.setSearchObjectType(objectType);
	
	// Choose Search Form to Display.
	// Do quick search if "all".
	// Try Advanced search, drop to default if not supported		
	if (objectType.equals(ObjectType.ALL)) {
		search.setSearchType(IObjectSearch.QUICK_SEARCH);
	} else if (search.isSearchTypeSupported(IObjectSearch.ADVANCED_SEARCH)) {
		search.setSearchType(IObjectSearch.ADVANCED_SEARCH);
	} else {
		search.setSearchType(search.getDefaultSearchType());
	}
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>

<%  if(request.getParameter("spaceType") != null) {
		if (request.getParameter("spaceType").equals("project")) { %>
			<template:getSpaceCSS space="project"/>	
		<% } else if (request.getParameter("spaceType").equals("business")) { %>
			<template:getSpaceCSS space="business"/>
		<% } else { %>
			<template:getSpaceCSS space="person"/>
		<% } 
 	} else {%>
 		<template:getSpaceCSS/>
<% } %>


<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
	theForm = self.document.ObjSearch;
	isLoaded = true;
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=search_summary";
	openwin_help(helplocation);
}

function cancel() {
   var theLocation = "<%=search.getRefererLink()%>";
   self.document.location = theLocation;
}
    
function reset() { 
	theForm.reset();   
}
</script>
</head>

<%-------------------------------------------------------------------------------------------
  -- History Tags                                                                            
  -----------------------------------------------------------------------------------------%>
<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
<tb:toolbar style="tooltitle" showAll="true" groupTitle="all.global.toolbar.standard.search">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.global.search.module.history")%>' 
							jspPage='<%=SessionManager.getJSPRootURL() + "/search/Search.jsp?module=" + search.getModuleID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br />
<%-- Indicate search errors--%>
<ul class="errorMessage">
	<% 
		java.util.Collection collection = search.getSearchErrors();
		java.util.Iterator iter = collection.iterator();
		while(iter.hasNext())
		{
			Object message=iter.next();
	%>
	<li><%= String.valueOf(message) %></li>
	<% } %>
</ul>
<%-- Indicate spaces being searched --%>
<table border="0" cellpadding="0" cellspacing ="0" width="100%">
	<tr>
		<td class="tableHeader" valign="top"><%=PropertyProvider.get("prm.global.search.searchingin.label")%>&nbsp;</td>
		<td colspan="3" class="tableHeader">
			<table border="0" cellpadding="0" cellspacing ="0" width="100%">
<%
	net.project.search.SearchSpace searchSpace = null;
	java.util.Iterator it = search.getSearchSpaces().iterator();
	while (it.hasNext()) {
		searchSpace = (net.project.search.SearchSpace) it.next();
%>
				<tr><td class="tableContent"><%=searchSpace.getName()%></td></tr>
<%	} %>
			</table>
		</td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr>
		<td class="tableHeader" valign="top"><%=PropertyProvider.get("prm.global.search.searchtype.label")%>&nbsp;</td>
		<td class="tableContent" colspan="3">
		<form method="POST" name="TYPE_PICK" action="<%=SessionManager.getJSPRootURL() + "/search/Search.jsp"%>">
            <%
                SpaceType searchTypeSource;
    		// Avinash: bfd-2982 Colors and options change when searching multiple projects.             
                if (!Validator.isBlankOrNull(request.getParameter("spaceType")) || !"null".equals(request.getParameter("spaceType"))) {
                    searchTypeSource = SpaceTypes.getForID(request.getParameter("spaceType"));
                } else {
                    searchTypeSource = user.getCurrentSpace().getSpaceType();
                }
            %>
			<select name="otype" onChange="submit();">
				<%= search.getSearchTypeOptionList(searchTypeSource) %>
			</select>
		<input type="hidden" name="module" value="<%=search.getModuleID()%>">
		</form>
		</td>
	</tr>
</table>

<form method="POST" name="ObjSearch" action="<%=SessionManager.getJSPRootURL() + "/search/SearchProcessing.jsp"%>">
	<input type="hidden" name="theAction">  
	<%= search.getSearchForm("ObjSearch", request) %> 

	<input type="hidden" name="TYPE" value="<%=search.getSearchType()%>">
	<input type="hidden" name="otype" value="<%=search.getSearchObjectType()%>">
	<input type="hidden" name="SUBMIT" value="submit">
	<input type="hidden" name="module" value="<%=search.getModuleID()%>">
	<input type="hidden" name="action" value="<%=search.getActionID()%>">
</form>

<%-------------------------------------------------------------------------------------------
  -- Action Bar                                                                              
  -----------------------------------------------------------------------------------------%>
<tb:toolbar style="action" showLabels="true" width="500" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="search" label='<%=PropertyProvider.get("prm.global.search.search.button.label")%>' function="javascript:document.ObjSearch.submit();" />
	</tb:band>
</tb:toolbar>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
