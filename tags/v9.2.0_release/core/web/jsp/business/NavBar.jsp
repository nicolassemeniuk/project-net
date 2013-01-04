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
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Business Navigation" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
    			net.project.security.User,
				net.project.base.Module" 
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
<title><display:get name="prm.business.navbar.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="business" />

<script language="javascript">
var space = "business";
var displayedForSpaceID = '<%=displayedForSpaceID%>';
</script>

<META http-equiv="expires" content="0">

</head>
<body  bgcolor="#FFFFFF" class="leftnav" leftmargin="0" topmargin="2" marginheight="2">

<%-- All Navbar functions now in include page for re-use by template stuff --%>
<jsp:include page="include/NavBar.jsp" flush="true" />

</body>
</html>
