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
|   Processing page for the mini month popup calendar
|   
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Mini Month Processing page" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.calendar.CalendarBean,
	net.project.util.DateFormat,
	net.project.security.User,
	java.util.Date" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
   // special case to parse the popup dates
	String tempParsingPattern = "MMddyyyy";
	
	DateFormat dateFormat = user.getDateFormatter();
	Date theDate = dateFormat.parseDateString(request.getParameter(CalendarBean.PARAM_date), tempParsingPattern);

	String displayDate = dateFormat.formatDate (theDate);

%>
<SCRIPT LANGUAGE="JAVASCRIPT">
opener.dateField.value= "<%= displayDate %>";
opener.dateField.focus();
if (opener.dateField.onchange) {
    // Simulate onChange event; required to invoke any special handling
    opener.dateField.onchange();
}
window.close();
</SCRIPT>
