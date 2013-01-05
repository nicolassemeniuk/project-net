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
    info="Calendar Meetings Channel" 
    language="java" 
    errorPage="/errors.jsp"
%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Iterator"%>
<%@page import="net.project.calendar.ICalendarEntry"%>
<%@page import="java.text.SimpleDateFormat"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="myMeetings" class="net.project.calendar.MyMeetings" scope="page" />

<%
	SimpleDateFormat sdf = new SimpleDateFormat("h:ma, MM/dd/yyyy (EEE)");
    myMeetings.setUser(user);
    myMeetings.loadEntries();
    
    Collection meetings =  myMeetings.getMeetingList();
    Iterator i = meetings.iterator();
    while(i.hasNext()) {
    	ICalendarEntry entry = (ICalendarEntry)i.next();
%>
		<a href="/calendar/MeetingManager.jsp?id=<%= entry.getID() %>&module=70&refLink=/calendar/Main.jsp?module=70&action=1"><%= entry.getName() %></a><br />
		<%= sdf.format(entry.getStartTime()) %>
		<br /><p />
<%
		
    }
%>


