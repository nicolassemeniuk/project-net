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
    info="Displayes date and times for a schedule entry"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.schedule.ScheduleEntryDateHelper,
            java.util.Iterator"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" type="net.project.security.User" scope="session" />

<%
    ScheduleEntryDateHelper dateHelper = new ScheduleEntryDateHelper(request, request.getParameter("scheduleEntryID"));
%>

<security:verifyAccess action="view" module="<%=Module.SCHEDULE%>"/>

<html>
<head>
<title><display:get name="prm.schedule.scheduleentrydatetimes.pagetitle"/></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

</script>

</head>

<body class="mainNoBGC popupBody">

<table width="100%" cellpadding="0" cellspacing="0">

    <%-- Header --%>
    <tr class="tableHeader">
        <td>&nbsp;</td>
        <td><display:get name="prm.schedule.scheduleentrydatetimes.starttime.column" /></td>
        <td><display:get name="prm.schedule.scheduleentrydatetimes.endtime.column" /></td>
    </tr>
    <tr><td class="headerSep" colspan="3"></td></tr>

    <%-- First item is the task start and end times --%>
    <tr class="popupBody">
        <td><display:get name="prm.schedule.scheduleentrydatetimes.scheduleentry.label" /></td>
        <td><%=dateHelper.getScheduleEntryDateTimeRange().formatStartDateTime()%></td>
        <td><%=dateHelper.getScheduleEntryDateTimeRange().formatEndDateTime()%></td>
    </tr>
<%
    if (dateHelper.isUserTimeZoneDisplayRequired()) {
%>
    <%-- User's time zone is differnt from the schedule's time zone and they aren't an assignee --%>
    <tr class="popupBody">
        <td>&nbsp;</td>
        <td><%=dateHelper.getScheduleEntryDateTimeRangeForUser().formatStartDateTime()%></td>
        <td><%=dateHelper.getScheduleEntryDateTimeRangeForUser().formatEndDateTime()%></td>
    </tr>

<%
    }
%>

    <%-- Remaining items are resource start and end times --%>
<%
    for (Iterator it = dateHelper.getAssignmentDateTimeRanges().iterator(); it.hasNext();) {
        ScheduleEntryDateHelper.IResourceDateRange nextDateRange = (ScheduleEntryDateHelper.IResourceDateRange) it.next();
%>
    <tr class="popupBody">
        <td><%=nextDateRange.getDisplayName()%>:</td>
        <td><%=nextDateRange.formatStartDateTime()%></td>
        <td><%=nextDateRange.formatEndDateTime()%></td>
    </tr>
<%
    }
%>
</table>
</body>
</html>
