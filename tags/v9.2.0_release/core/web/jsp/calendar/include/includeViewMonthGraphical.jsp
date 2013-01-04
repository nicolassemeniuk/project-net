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
    info="Graphical Month View" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.calendar.*,
			net.project.project.*,
			net.project.security.*,
			java.util.*,
			net.project.schedule.Schedule,
			net.project.base.ObjectType,
            net.project.schedule.TaskType,
            org.apache.log4j.Logger,
            net.project.schedule.ScheduleEntry,
            net.project.base.Module,
            net.project.util.HTMLUtils,
            java.text.SimpleDateFormat"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="calendar" class="net.project.calendar.CalendarBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.CALENDAR%>" /> 
   
<%!
// CONSTANTS
public static final String HEADER_FORMAT = "MMMM yyyy";

public static final String ATTRIBUTE_date = "Date";
public static final String ATTRIBUTE_width = "Width";
public static final String ATTRIBUTE_includer = "Includer";

public static final String ICON_meeting = "red-ball-small.gif";
public static final String ICON_event = "green-ball-small.gif";
public static final String ICON_milestone = "blue-ball-small.gif";
public static final String ICON_task = "yellow-ball-small.gif";

public static final String COLOR_meeting = "darkred";
public static final String COLOR_event = "darkgreen";
public static final String COLOR_milestone = "darkblue";
public static final String COLOR_task = "#888800";

public Hashtable parseEntries(List entries, int month) {
    java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
    Hashtable days = new Hashtable(31);
    for (int i=0;i<entries.size();i++) {
        // get the ith entry
        ICalendarEntry entry = (ICalendarEntry)entries.get(i);
        // determine what day of the month this entry occurs on
        tempCal.setTimeZone(SessionManager.getUser().getTimeZone());
        tempCal.setTime(entry.getStartTime());

        String theDay = Integer.toString(tempCal.get(tempCal.DAY_OF_MONTH));
        // get the day's entry ArrayList from the hashtable (create it if it doesn't exist)
        ArrayList dayArray = (ArrayList)days.get(theDay);
        if (dayArray == null) {
            dayArray = new ArrayList();
            days.put(theDay, dayArray);
        }
        // add the entry to the list
        dayArray.add(entry);
    }
    return days;
}
%>
<%
// Evaluate request attributes
    // ***** Date
    java.util.Date date = (java.util.Date)request.getAttribute(ATTRIBUTE_date);
    if (date == null) 
        date = PnCalendar.currentTime();
    calendar.setTime(date);
    
    // ***** Includer
    String includer = (String)request.getAttribute(ATTRIBUTE_includer);
	
    // Configure the calendar
    // We always set the space to ensure that loadIfNeeded()
    // loads for the correct space
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
		<td width="1%" class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td width="34%" align="right" class="channelHeader"> <A HREF="<%= includer %><%=calendar.getStateAsQueryString(calendar.getPrevMonth()) %>&module=<%= net.project.base.Module.CALENDAR %>"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/back.gif" width=9 height=15 alt="" border=0></a></td>
		<td width="30%" align=center nowrap class="channelHeader"><nobr><%= calendar.formatDateAs(date, HEADER_FORMAT) %></nobr></td>
		<td width="20%" align=left class="channelHeader"><A HREF="<%= includer %><%=calendar.getStateAsQueryString(calendar.getNextMonth()) %>&module=<%= net.project.base.Module.CALENDAR %>"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/forward.gif" width=9 height=15 alt="" border=0></a></td>
		<td width="14%" align=center nowrap class="channelHeader">&nbsp;&nbsp;<%=PropertyProvider.get("prm.calendar.main.viewmonthgraphical.listview.1.text")%><A HREF="<%= includer %><%=calendar.getStateAsQueryString(todaysDate, null, calendar.TYPE_list) %>&module=<%= net.project.base.Module.CALENDAR %>" class="channelNoUnderline" style="color: red;"><%=PropertyProvider.get("prm.calendar.main.viewmonthgraphical.listview.2.link")%>
</A><%=PropertyProvider.get("prm.calendar.main.viewmonthgraphical.listview.3.text")%></td>
		<td align=right width="1%" class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr> 

<tr>
    <td valign=top colspan="7">
        <table bgcolor=cccccc cellpadding=4 cellspacing=1 border=0 width="100%">
        <tr class="tableHeader">
<%
    // Generate locale-specific days of week
    // Starting on appropriate day of week for locale
    PnCalendar weekCal = new PnCalendar(user);
    // Start at the first day of week (month or year is irrelevant)
    weekCal.set(Calendar.DAY_OF_WEEK, weekCal.getFirstDayOfWeek());
    String dayPattern = "EEE";
    // Iterate over 7 days, displaying the name of the day
    for (int i = 0; i < 7; i++) {
%>    
            <TH align=left width="14%" class="tableHeader"><%=user.getDateFormatter().formatDate(weekCal.getTime(), dayPattern)%></TH>
<%
        weekCal.roll(Calendar.DAY_OF_WEEK, 1);
    }
%>
        </tr>
<%
    int iMonth = calendar.get(Calendar.MONTH);
    int iYear = calendar.get(Calendar.YEAR);

    PnCalendar tempCal = new PnCalendar(user);    
    tempCal.setTime(calendar.endOfMonth(iMonth, iYear));
    int lastDayOfMonth = tempCal.get(Calendar.DAY_OF_MONTH);
    tempCal.setTime(calendar.startOfMonth(iMonth, iYear));
    int firstDayOfMonth = tempCal.get(Calendar.DAY_OF_MONTH);
    int firstDayOfWeek = tempCal.getFirstDayOfWeek();
    int day = firstDayOfMonth;

    ArrayList weeks = new ArrayList();
    while (day <= lastDayOfMonth) {

        String[] week = new String[7];

        // Build the week array, assigning the day of month to
        // appropriate index position
        boolean isEndOfWeek = false;
        while (day <= lastDayOfMonth && !isEndOfWeek) {
        
            // Calculate index position in our week array, based on
            // the starting day of week for the user's locale
            // and the day of week of the current day
            // For example:  First day of week for US is Sunday (1)
            //               if the current day is saturday (7)
            //               then index position is ( 7 - 1 == 6)
            //               therefore Saturday is the _last_ index position
            //
            //               First day of week for Austria is Monday (2)
            //               if the current day is sunday (1)
            //               then index position is ( 1 - 2 == -1)
            //               which is less than zero, so becomes ( 7 + -1 == 6)
            //               therefore Sunday is the _last_ index position
            int indexPos = tempCal.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek;
            if (indexPos < 0) {
                indexPos = 7 + indexPos;
            }
            week[indexPos] = Integer.toString(day);

            // Move to next day
            tempCal.roll(Calendar.DAY_OF_MONTH, 1);
            day++;

            // If we populated the last index element
            // Then its time for a new week
            if (indexPos == 6) {
                isEndOfWeek = true;
            }

        }

        // Save the week
        weeks.add(week);
    }

    String sMonthNum = Integer.toString(calendar.get(Calendar.MONTH) + 1);
    if (sMonthNum.length() == 1) {
        sMonthNum = "0" + sMonthNum;
    }
    String sYearNum = Integer.toString(calendar.get(Calendar.YEAR));

    //Separate the calendar entries into the appropriate days.
    Hashtable dailyEntries = parseEntries(calendar.getEntries(), calendar.get(Calendar.MONTH));
    
    int iEntries;
    for (int i=0; i<weeks.size(); i++) {
        String[] week = (String[])weeks.get(i);
%>
            <%-- Draw row containing day of month and link to add new task --%>
			<tr bgcolor=dcdcdc>            
<%
            for (int j=0; j<week.length; j++) {
                String dayNum = week[j];
                if (dayNum != null && dayNum.length() == 1) {
                    dayNum = "0" + dayNum;
                }
                String dateParam = sMonthNum + dayNum + sYearNum;         
%>
            
		            <td align=left nowrap class="calendar">
<%
                    if (week[j] != null && week[j].length() > 0) {
%>
                        <A HREF="<%= includer %><%=calendar.getStateAsQueryString(dateParam, "day", null) %>&module=<%=net.project.base.Module.CALENDAR%>">
                        <%= week[j] %>
                        </A>
                        &nbsp;&nbsp;&nbsp; <%=PropertyProvider.get("prm.calendar.main.viewmonthgraphical.add.1.text")%>
                        <A HREF="javascript: create('<%= dateParam %>')"><%=PropertyProvider.get("prm.calendar.main.viewmonthgraphical.add.2.link")%></A> <%=PropertyProvider.get("prm.calendar.main.viewmonthgraphical.add.3.text")%>
                        &nbsp;&nbsp;&nbsp;
<%                  } %>
                    </td>   
<%          } %>
            </tr>             
            
            <%-- Draw row containing task entries for each day in week --%>
			<tr bgcolor=ffffff>
<%
            for (int j=0; j<week.length; j++) {
                ArrayList entries = null;
                String dateParam = null;
                
                String dayNum = week[j];
                if (dayNum != null) {
                    if (dayNum.length() == 1)
                        dayNum = "0" + dayNum;
                    dateParam = sMonthNum + dayNum + sYearNum;
                    entries = (ArrayList)dailyEntries.get(week[j]);
                }
%>
                <td valign=top bgcolor=ffffff>
<%
                    if (entries != null) {
                        for (iEntries =0; iEntries<entries.size();iEntries++) {
                            ICalendarEntry theEntry = (ICalendarEntry)entries.get(iEntries);
                            String entryType = theEntry.getType();
                            String image=null;
                            String color=null;      
                            String href="";
                            if (entryType.equals(ObjectType.MEETING)) {
                                image = ICON_meeting;
                                color= COLOR_meeting;
                                href = SessionManager.getJSPRootURL() + "/calendar/MeetingManager.jsp?id=" + theEntry.getID() + "&module=" + net.project.base.Module.CALENDAR;
                            } else if (entryType.equals(ObjectType.EVENT)) {
                                image = ICON_event;
                                color= COLOR_event;
                                href = SessionManager.getJSPRootURL() + "/calendar/EventView.jsp?id=" + theEntry.getID() + "&module=" + net.project.base.Module.CALENDAR;
                            } else if ((entryType.equals(TaskType.TASK.getID())) || (entryType.equals(TaskType.SUMMARY.getID()))) {
                                ScheduleEntry entry = (ScheduleEntry)theEntry;
                                if (entry.isMilestone()) {
                                    image = ICON_milestone;
                                    color= COLOR_milestone;
                                } else {
                                    image = ICON_task;
                                    color= COLOR_task;
                                }
                                href = SessionManager.getJSPRootURL() + "/servlet/ScheduleController/TaskView?id=" + theEntry.getID() + "&module=" + net.project.base.Module.SCHEDULE;
                            }
                            
                            final String refLink = "/calendar/Main.jsp?module=" 
                            		+ Module.CALENDAR + "&action=" + Action.VIEW;            
                            
                            href += "&refLink=" + 
                            		java.net.URLEncoder.encode(refLink, SessionManager.getCharacterEncoding());
%>
                            <IMG SRC="<%= SessionManager.getJSPRootURL() %>/images/<%= image %>" align=middle>
                            <A HREF="<%= href %>">
                            <FONT SIZE=1 COLOR="<%= color %>">                            
                                <%= HTMLUtils.escape(theEntry.getName()) %>
                            </FONT>                                
                            </A>                              
                            <BR>
<%
                        }
                    }
%>
                        <BR>
					</td>           
<%
                }
%>
            </tr>
<%
    }
%>
        
        </table>
    </td>
</tr>
<tr> 
    <td>
    &nbsp;
    </td>
</tr>
</table>




