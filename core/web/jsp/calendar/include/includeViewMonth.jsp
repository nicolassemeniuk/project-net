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
|   $Revision: 20739 $
|       $Date: 2010-04-22 11:20:52 -0300 (jue, 22 abr 2010) $
|
|   Includable month view
|   
|   Adam Klatzkin    03/00
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Month View" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.calendar.*, net.project.project.*, net.project.security.*, java.util.Calendar,
            net.project.base.property.PropertyProvider, java.util.Date, java.text.SimpleDateFormat"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="calendar" class="net.project.calendar.CalendarBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.CALENDAR%>" /> 
   
<%!
// CONSTANTS
public static final String          HEADER_FORMAT       = "MMMM yyyy";
public static final String          STYLE_SHEET         = "/calendar/xsl/type-list.xsl";

public static final String          ATTRIBUTE_date      = "Date";
public static final String          ATTRIBUTE_width     = "Width";
public static final String          ATTRIBUTE_includer  = "Includer";
%>
      
<%
// Evaluate request attributes
    // ***** Date
    java.util.Date date = (java.util.Date)request.getAttribute(ATTRIBUTE_date);
    // if a date attribute is not set by the caller, we'll use the current time
    if (date == null) 
        date = PnCalendar.currentTime();
    calendar.setTime(date);

    // ***** Width
    String width = (String)request.getAttribute(ATTRIBUTE_width);
    if (width == null) 
        width = "100%";

    // ***** Includer
    String includer = (String)request.getAttribute(ATTRIBUTE_includer);

// Configure the calendar
	calendar.setSpace(user.getCurrentSpace());
    calendar.loadIfNeeded();

    int monthNum = calendar.get(calendar.MONTH);
    int yearNum = calendar.get(calendar.YEAR);
	calendar.setStartDate(calendar.startOfMonth(monthNum, yearNum));
	calendar.setEndDate(calendar.endOfMonth(monthNum, yearNum));
	String[] entryTypes= {"meeting", "event", "milestone", "task"};
	calendar.loadEntries(entryTypes);
	
	// Use simple formatted date as a paramer instead of locale specific format 
	String todaysDate = new SimpleDateFormat("MMddyyyy").format(new Date());

%>
<table  border="0" cellpadding="0" cellspacing="0" width="100%">	
	<tr class="channelHeader">
		<td width=1% class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td width="34%" align="right" class="channelHeader"> <A HREF="<%= includer %><%=calendar.getStateAsQueryString(calendar.getPrevMonth()) %>&module=<%= net.project.base.Module.CALENDAR %>"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/back.gif" width=9 height=15 alt="" border=0></a></td>
		<td width=30% align=center nowrap class="channelHeader"><nobr><%= calendar.formatDateAs(date, HEADER_FORMAT) %></nobr></td>
		<td width=20% align=left class="channelHeader"><A HREF="<%= includer %><%=calendar.getStateAsQueryString(calendar.getNextMonth()) %>&module=<%= net.project.base.Module.CALENDAR %>"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/forward.gif" width=9 height=15 alt="" border=0></a></td>
		<td width=14% align=center nowrap class="channelHeader">&nbsp;&nbsp;<%=PropertyProvider.get("prm.calendar.main.viewmonthgraphical.graphicalview.1.text")%><A HREF="<%= includer %><%=calendar.getStateAsQueryString(todaysDate, null, calendar.TYPE_graphic) %>&module=<%= net.project.base.Module.CALENDAR %>" class="channelNoUnderline" style="color: red;"><%=PropertyProvider.get("prm.calendar.main.viewmonthgraphical.graphicalview.2.link")%>
</A><%=PropertyProvider.get("prm.calendar.main.viewmonthgraphical.graphicalview.3.text")%>
		<td align=right width=1% class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>

    <tr>
    	<td valign="top" colspan="6">
    		<jsp:setProperty name="calendar" property="stylesheet" value="<%= STYLE_SHEET %>" />
    		<jsp:getProperty name="calendar" property="presentation" />
    	</td>
    </tr>
</table>


