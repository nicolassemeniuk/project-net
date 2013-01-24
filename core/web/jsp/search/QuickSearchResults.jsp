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
    info="page for adding a link to an object" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
			net.project.search.IObjectSearch,
			net.project.search.SearchManager,
            net.project.security.SessionManager,
            net.project.security.User" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<%
	boolean isProjectList = false;
	if(session.getAttribute("isFromProjectPortfolio") != null ) {%>
		<template:getSpaceCSS space="project" />
	<%
		isProjectList = true;
	} else {%>
		<template:getSpaceCSS />
<%	}	%>

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
	theForm = self.document.actForm;
	isLoaded = true;
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=search&section=quick_results";
	openwin_help(helplocation);
}


function cancel() {
   var theLocation = "<%=search.getRefererLink()%>";
   self.document.location = theLocation;
}

function search() {
	theForm.submit();
}

</script>
</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<% if(session.getAttribute("isFromProjectPortfolio") == null) {%>
	<template:getSpaceNavBar />
<%}
	session.removeAttribute("isFromProjectPortfolio");
%>

<%-------------------------------------------------------------------------------------------
  -- Toolbar                                                                                 
  -----------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="all.global.toolbar.standard.search" projectListPage="<%=isProjectList%>">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.global.search.module.history")%>' 
							jspPage='<%=SessionManager.getJSPRootURL() + "/search/Search.jsp?module=" +search.getModuleID()%>' />
			<history:page display='<%=PropertyProvider.get("prm.global.search.results.module.history")%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" >
	</tb:band>	
</tb:toolbar>

<div id='content'>

<form method="POST" name="actForm" action="<%=SessionManager.getJSPRootURL()%>/search/Search.jsp" >
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=search.getModuleID()%>"> 
</form>
<jsp:setProperty name="search" property="stylesheet" value="/search/xsl/quicksearch-results.xsl" />
<jsp:getProperty name="search" property="presentation" />

<%-----------------------------------------------------------------------------
  --  Action Bar                                                               
  ---------------------------------------------------------------------------%>
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="search" label='<%=PropertyProvider.get("prm.global.search.results.newsearch.button.label")%>' />
	</tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
