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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Event View processing.. omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
			net.project.base.Module,
			net.project.security.Action,
		    net.project.security.User,
            java.util.Hashtable"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="event" class="net.project.calendar.CalendarEvent" scope="session" />

<%
	String id = request.getParameter("id");
	String theAction = request.getParameter("theAction");

	if (theAction != null && theAction.equals("modify")) {
		// Modify Event
		response.sendRedirect(SessionManager.getJSPRootURL() + "/calendar/EventEdit.jsp?module=" + Module.CALENDAR + "&action=" + Action.MODIFY + "&id=" + id);
    } else if (theAction != null && theAction.equals("remove")) {
        event.remove();

        Hashtable nav = (Hashtable)request.getSession().getAttribute("PageNavigator");
        if (nav == null) {
            nav = new java.util.Hashtable();
            request.getSession().putValue("PageNavigator", nav);
        }

        String myReturnTo = (String)nav.get("MeetingEdit_returnto");
        if (myReturnTo == null)
            myReturnTo = SessionManager.getJSPRootURL() + "/project/Dashboard?module="+Module.PROJECT_SPACE;

        response.sendRedirect(myReturnTo);
	} else {
		// Unknown Action
		throw new net.project.base.PnetException("Unknown or missing action '" + theAction + "' in EventViewProcessing.jsp");

	}
%>
