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
|   MiniMonthPopup used by the javascript function auto_date
|   provided in util.js
|   Provides a mini month in a popup window to use for date
|   selection
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Mini Month Popup" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.calendar.CalendarBean,
		    net.project.calendar.MiniMonth,
		    net.project.security.User,
		    net.project.space.Space" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<html>
<head>
<META http-equiv="expires" content="0">
<title><%=PropertyProvider.get("prm.calendar.minimonthpopuppage.title")%></title>

<jsp:useBean id="popupCal" class="net.project.calendar.CalendarBean" scope="request" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%-- Import CSS --%>
<template:getSpaceCSS />

<%
    // configure the calendar
    popupCal.setState(request.getParameter(popupCal.PARAM_date), 
                      popupCal.MODE_day, 
                      popupCal.TYPE_list);
    java.util.Date date = popupCal.getDisplayDateAsObject();
%>
</head>
<body class="main">
	
<table cellpadding=3 border=0 cellspacing=0 width="100%">    
    <tr>
        <td>
            <%
                MiniMonth miniMonth = new MiniMonth();
                miniMonth.setDate(date);
                miniMonth.setMode(popupCal.MODE_day);
                miniMonth.setIncluder("MiniMonthPopup.jsp");
                miniMonth.setOnDayClick("MiniMonthPopupProcessing.jsp");
                miniMonth.setTabs(false);
                miniMonth.setHighlightActiveDay(true);
                miniMonth.setWidth("170");
                miniMonth.renderToStream(out);
            %>               
        </td>
    </tr>
</table>

</body>
</html>

