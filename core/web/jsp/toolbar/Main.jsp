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
    info="Top Banner"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.*,
            net.project.base.property.PropertyProvider,
            net.project.security.*"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<html>
<head>
<title>toolbar main</title>

<%-- Import CSS --%>
<template:import type="js" src="/src/util.js" />
<%-- ExtJs Component --%>
<template:import type="javascript" src="/src/extjs/ext-all.js" />
<template:import type="javascript" src="/src/ext-components.js" />

<template:getSpaceCSS />
<%--
<link rel="stylesheet" rev="stylesheet" type="text/css" href='<display:get name="prm.global.css.project" />'>
--%>
</head>

<body class="banner" leftmargin="0" topmargin="0" background="<%=PropertyProvider.get("prm.global.header.banner.image")%>">

<%-- Main functionality now in include file for re-use by template system --%>
<jsp:include page="include/Main.jsp" flush="true" />

</body>
</html>

