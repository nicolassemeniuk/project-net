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
    import="net.project.security.SessionManager,
            net.project.resource.ResourceAllocationList,
            java.util.Date,
            net.project.resource.ResourceAllocationCalendar,
            net.project.calendar.PnCalendar,
            net.project.xml.XMLFormatter,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.resource.Person,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:verifyAccess action="view" module="<%=Module.DIRECTORY%>"/>

<%
    //Figure out the first and last month we'll be looking at
    PnCalendar cal = new PnCalendar(user);

    Date startMonth;
    if (request.getParameter("startDate") != null) {
        cal.setTime(new Date(Long.parseLong(request.getParameter("startDate"))));
        startMonth = cal.getTime();
    } else {
        cal.setTime(new Date());
        startMonth = cal.startOfMonth(cal.getTime());
    }

    cal.add(PnCalendar.MONTH, 2);
    Date endMonth = cal.endOfMonth(cal.getTime());
    endMonth.setHours(23);
    cal.add(PnCalendar.MONTH, 1);
    Date nextMonth = cal.startOfMonth(cal.getTime());
    cal.add(PnCalendar.MONTH, -6);
    Date prevMonth = cal.startOfMonth(cal.getTime());

    request.setAttribute("startMonth", startMonth);
    request.setAttribute("endMonth", endMonth);
    request.setAttribute("nextMonth", nextMonth);
    request.setAttribute("prevMonth", prevMonth);


    //Load the person for which we are listing allocations
    Person person = new Person();
    person.setID(request.getParameter("personID"));
    person.load();
%>

<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:import type="css" src="/styles/resource.css" />
<template:getSpaceCSS />


<script language="javascript" type="text/javascript">
function cancel() {
    window.close();
}
</script>

</head>

<body class="main">

<channel:channel name="ResourceCalendars" customizable="false" >
    <channel:insert name="ResourceCalendar" title='<%=PropertyProvider.get("prm.resource.allocation.resourcecalendarsfor.label", person.getDisplayName())%>'
        minimizable="false" closeable="false" row="1" column="1"
        include="/resource/include/ResourceAllocationCalendar.jsp"/>
    <channel:insert name="ResourceAllocationList" title='<%=PropertyProvider.get("prm.resource.allocation.resourcelistfor.label", person.getDisplayName())%>'
        minimizable="false" closeable="false" row="2" column="1"
        include="/resource/include/ResourceAllocationList.jsp"/>
</channel:channel>

<tb:toolbar style="action" showLabels="true" width="97%">
    <tb:band name="action">
        <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
