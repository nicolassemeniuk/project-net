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
    info="Configuration Space Navigation Bar" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<html>
<head>
<META http-equiv="expires" content="0"> 

<title>Configuration Space</title>

<%-- Import CSS --%>
<template:getSpaceCSS />

</head>
<script language="javascript" >
    var displayedForSpaceID = '<%=user.getCurrentSpace().getID()%>';
</script>
<body  bgcolor="#FFFFFF"  class="leftnav" leftmargin="0" topmargin="2" marginheight="2">
<%-- Include content --%>
<jsp:include page="include/NavBar.jsp" flush="true" />
<template:getSpaceJS />
</body>

</html>
