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
|
| Project navigation bar : includes all content from an include page
| for re-use with template system
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Project Navigation" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.project.*, 
			net.project.space.Space, 
			net.project.security.*, 
			net.project.base.*,
			net.project.search.IObjectSearch" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
    String displayedForSpaceID = null;

    // Figure out if we're reloading because we are seeing the portfolio
    boolean isPortfolio = (request.getParameter("portfolio") != null && request.getParameter("portfolio").equals("true"));
    if (isPortfolio) {
        // Indicate that we are displaying the portfolio
        displayedForSpaceID = "portfolio";
    } else {
        // Indicate that we are displaying the navbar for the current space
        displayedForSpaceID = user.getCurrentSpace().getID();
    }
%>
<html>
<head>

<%-- This javascript variable lets other spaces know that they have to reload the
     left nav bar menu if they came to that space without going through portfolio
     page.  --%>
<script language="javascript">
var space="project";
var displayedForSpaceID = '<%=displayedForSpaceID%>';
</script>

<META http-equiv="expires" content="0"> 

<title><display:get name="prm.project.navbar.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS space="project" />

</head>

<body  bgcolor="#FFFFFF"  class="leftnav" leftmargin="0" topmargin="2" marginheight="2">
<%-- Include content --%>
<jsp:include page="include/NavBar.jsp" flush="true" />
<template:getSpaceJS space="project" />
</body>

</html>
