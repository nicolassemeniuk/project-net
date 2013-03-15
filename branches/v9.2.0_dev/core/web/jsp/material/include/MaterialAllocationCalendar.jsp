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
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.calendar.PnCalendar,
            net.project.material.MaterialResourceAllocationCalendar,
            java.util.Date,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="endMonth" class="java.util.Date" scope="request"/>
<jsp:useBean id="startMonth" class="java.util.Date" scope="request"/>
<jsp:useBean id="prevMonth" class="java.util.Date" scope="request"/>
<jsp:useBean id="nextMonth" class="java.util.Date" scope="request"/>
<jsp:useBean id="rc" class="net.project.material.MaterialResourceAllocationCalendar" scope="page"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:verifyAccess action="view" module="<%=Module.MATERIAL%>"/>

<%
    rc.setCalendar(new PnCalendar(user));
    rc.setCalendarStart(startMonth);
    rc.setCalendarEnd(endMonth);
    rc.load(request.getParameter("materialID"));
%>
<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:getSpaceCSS />


</head>
<body>

<pnet-xml:transform name="rc" scope="page" stylesheet="/material/xsl/material-allocation-calendar.xsl">
    <pnet-xml:property name="nextMonth" value="<%=String.valueOf(nextMonth.getTime())%>"/>
    <pnet-xml:property name="prevMonth" value="<%=String.valueOf(prevMonth.getTime())%>"/>
    <pnet-xml:property name="materialID" value='<%=request.getParameter("materialID")%>'/>
</pnet-xml:transform>

<template:getSpaceJS />
</body>
</html>
