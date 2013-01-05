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
|   $Revision: 19941 $
|       $Date: 2009-09-10 14:41:29 -0300 (jue, 10 sep 2009) $
|
|   Go to date box for quickly navigating to a date
|   specified by the user
|   
|   Adam Klatzkin    03/00
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Go To Date box" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.calendar.*,
			net.project.project.*,
			net.project.security.*,
			net.project.space.Space,
			java.util.Calendar,
            net.project.util.DateFormat"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="calendar" class="net.project.calendar.CalendarBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
   
<%!
// Constants
public static final String          ATTRIBUTE_width     = "Width";
public static final String          ATTRIBUTE_includer  = "Includer";
public static final String[]        selected            = {"", "SELECTED"};

public String padded (int num) {
    return (num < 10) ? "0" + num : Integer.toString(num);
}
%>

<%
    // Localization issue:
    // This causes Months to be spelled out
    // Days to be padded with leading 0
    // Years to always be 4 digits
    // These may not be appropriate for all Locales
    // The correct solution is likely to format all dates between Jan 1 and
    // Jan 31 with the LONG date format and use an AttributedCharacterIterator
    // to extract out the formatted day, month and year components
    String monthPattern = "MMMM";
    String dayPattern = "dd";
    String yearPattern = "yyyy";

    net.project.util.DateFormat dateFormatter = user.getDateFormatter();
    PnCalendar tempCal = new PnCalendar(user);
    String currentMonth = dateFormatter.formatDate(PnCalendar.currentTime(), monthPattern);
    String currentDay = dateFormatter.formatDate(PnCalendar.currentTime(), dayPattern);
    String currentYear = dateFormatter.formatDate(PnCalendar.currentTime(), yearPattern);

    String[] MONTHS =  new String [12];
    PnCalendar monthCal = new PnCalendar(user);
    monthCal.set(Calendar.MONTH, Calendar.JANUARY);
    for (int i=0; i<12; i++) {
        MONTHS[i] = dateFormatter.formatDate(monthCal.getTime(), monthPattern);
        monthCal.roll(Calendar.MONTH, 1);
    }
    
    String[] DAYS =  new String [31];
    PnCalendar dayCal = new PnCalendar(user);
    dayCal.set(Calendar.MONTH, Calendar.JANUARY);
    dayCal.set(Calendar.DAY_OF_MONTH, 1);

    for (int i=0; i<31; i++) {
        DAYS[i] = dateFormatter.formatDate(dayCal.getTime(), dayPattern);
        dayCal.roll(Calendar.DAY_OF_MONTH, 1);
    }
    
    String[] YEARS =  new String [21];
    PnCalendar yearCal = new PnCalendar(user);
    yearCal.roll(Calendar.YEAR, -10);
    for (int i=0; i<21; i++) {
        YEARS[i] = dateFormatter.formatDate(yearCal.getTime(), yearPattern);
        yearCal.roll(Calendar.YEAR, 1);
    }
%>

<%
// Evaluate request attributes
    // ***** Width
    String width = (String)request.getAttribute(ATTRIBUTE_width);
    if (width == null) 
        width = "100%";

    // ***** Includer
    String includer = (String)request.getAttribute(ATTRIBUTE_includer);%>
<template:import type="javascript" src="/src/checkDate.js" />
<SCRIPT LANGUAGE="javascript" type="text/javascript">

monthDaysLeap = new Array(31,29,31,30,31,30,31,31,30,31,30,31)
monthDaysNonLeap = new Array(31,28,31,30,31,30,31,31,30,31,30,31)
monthDays = new Array(31,28,31,30,31,30,31,31,30,31,30,31)

function populateDaysInYear(yearChosen) {
	SomeYearStr = yearChosen.options[yearChosen.selectedIndex].value
	SomeYear = parseInt( SomeYearStr )
	if ((SomeYear % 400 == 0) || ((SomeYear % 4 == 0) && (SomeYear % 100 != 0))) {
		monthDays=monthDaysLeap
	} else {
		monthDays=monthDaysNonLeap
	}
	//make chages (for month days) active by calling populateDays function
	populateDays ( document.JumpTo.month )
}

function populateDays( monthChosen ) {
	monthStr = monthChosen.options[monthChosen.selectedIndex].value
	if ( monthStr != "" ) {
		theMonth = parseInt( monthStr )
		document.JumpTo.day.options.length = 0
 		for ( i = 0; i < monthDays[theMonth - 1]; i++ ) {
 			document.JumpTo.day.options[i] = new Option(i+1)
 			if( i < 9 ) {
 				document.JumpTo.day.options[i].value = "0" + (i+1)
 			} else {
 				document.JumpTo.day.options[i].value =  (i+1)
 			}  
 		}
	}
}

function jump() {
	monthStr = document.JumpTo.month.options[document.JumpTo.month.options.selectedIndex].value
	monthVal = parseInt( monthStr )
	if (  monthVal < 10 ) {
		document.JumpTo.month.options[document.JumpTo.month.options.selectedIndex].value = "0" + monthStr
	}
	
    this.location = "<%= includer %>?DisplayMode=day&DisplayDate=" + document.JumpTo.month.options[document.JumpTo.month.options.selectedIndex].value + document.JumpTo.day.options[document.JumpTo.day.options.selectedIndex].value + document.JumpTo.year.options[document.JumpTo.year.options.selectedIndex].value
                  + "&module=<%= net.project.base.Module.CALENDAR %>"
                  + "&action=<%= net.project.security.Action.VIEW %>";
}
</SCRIPT>
<table name="monthsTable" cellpadding=0 cellspacing=0 border=0  width="<%= width %>">
<form name="JumpTo" action="javascript:jump();">
<tr>
	<td class="channelHeader" width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td class="channelHeader" width=98% colspan=2 align=center nowrap="nowrap"><%=PropertyProvider.get("prm.calendar.main.gotodate.channel.jumptodate.title")%></td>
	<td class="channelHeader"  align="right" width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr><td colspan=4><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width=1 height=4 alt="" border="0"></td></tr>

<tr class="tableHeader">    
    <td class="tableHeader" colspan="2"><%=PropertyProvider.get("prm.calendar.main.gotodate.year.label")%></td>
    <td class="channelContent" colspan="2">
    <select name="year" onchange="populateDaysInYear(this)">
    	<option>Year</option>
<%
calendar.setTime(PnCalendar.currentTime());
int year = calendar.get(calendar.YEAR);
year = year - 10;
for (int i=0; i<21; i++, year++) {
%>
        <option VALUE="<%= year %>"><%= YEARS[i] %></option>
<%
}
%>
            </select>
    </td>    
</tr>

<tr><td colspan=4><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width=1 height=4 alt="" border="0"></td></tr>

<tr class="tableHeader">
    <td class="tableHeader" colspan="2"><%=PropertyProvider.get("prm.calendar.main.gotodate.month.label")%></td>
    <td class="channelContent" colspan="2">
	    <select name="month" onchange="populateDays(this)">
			<option value="">Month</option>
			<% for (int monthIndex = 0; monthIndex < 12; monthIndex++) { %>
				<option value=<%= monthIndex + 1%>><%= MONTHS[monthIndex]%></option>
			<% } %>
		</select>
    </td>
</tr>

<tr><td colspan=4><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width=1 height=4 alt="" border="0"></td></tr>

<tr class="tableHeader">
    <td class="tableHeader" colspan="2"><%=PropertyProvider.get("prm.calendar.main.gotodate.day.label")%></td> 
    <td class="channelContent" colspan="2">
    	<select name="day">
			<option>Day</option>
		</select>
    </td>
</tr>

<tr>
<%
    java.util.Date today = PnCalendar.currentTime();
    String todayFull = new DateFormat(user).formatDate(today, java.text.DateFormat.FULL);
%>
	<td nowrap colspan=4 align="CENTER" class="tableContent"> 
	<FONT SIZE=-1>
			<%=PropertyProvider.get("prm.calendar.main.gotodate.today.1.text")%>
            <A HREF="<%= includer %><%=calendar.getStateAsQueryString(today, calendar.MODE_day, null) %>&module=<%=net.project.base.Module.CALENDAR%>">
                <%=PropertyProvider.get("prm.calendar.main.gotodate.today.2.link")%>
            </A>
            <%=PropertyProvider.get("prm.calendar.main.gotodate.today.3.text", new Object [] {todayFull})%>
            </FONT>
	</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true">
			<tb:band name="action">
				<tb:button type="jump" />
			</tb:band>
</tb:toolbar>

</form>

