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
--%><%@ page 
    contentType="text/x-vCalendar; charset=UTF-8"
    info="ICalendar View"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.calendar.ical.ICalendar, 
    		java.util.Calendar" 
%><%!
	// CONSTANTS
	public static final String KEY = "key";
%><%
	final ICalendar iCalendar = new ICalendar();
	String iCalData = null;
	
	// set file headers
	//if (request.getParameter("export") != null)
	//{
		response.setHeader("Content-Disposition", 
				"attachment; filename=" + "Project.net-iCalender"+ Calendar.getInstance().get(Calendar.YEAR) +".ics");
	//}
	iCalData = iCalendar.getOutput(request.getParameter(KEY));
	
	out.print(iCalData);
%>
