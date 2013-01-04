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
    info="Edit Form" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.space.SpaceURLFactory,
			net.project.security.*, 
            net.project.util.JSPUtils" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title"/></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

</head>
<body class="main"  id="bodyWithFixedAreasSupport">
<div id='content'>
<table>
	<tr>
	<td>	
  		<%=PropertyProvider.get("prm.form.errorpage.formnotavailable.message")%>
    </td>
</tr>
</table>
</div>

<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
