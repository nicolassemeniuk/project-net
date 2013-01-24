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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Document Copy Utility" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    net.project.security.User,
	net.project.security.SessionManager,
            net.project.security.AuthorizationFailedException"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

 <%
    if (!user.isApplicationAdministrator()) {
        throw new AuthorizationFailedException("Failed security validation: This functionality is only available to application administrators.");
    }
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>


<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />




<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id='content'>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/admin/document/DocumentCopyUtilityProcessing.jsp">
 
  <input type="hidden" name="theAction">

  From Folder ID: <input type="text" name="fromContainerID"><P>
  To Folder ID: <input type="text" name="toContainerID"><P>
  <input type="submit" value="COPY">

  <br clear=all>

</form>
 
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
