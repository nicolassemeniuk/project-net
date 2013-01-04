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
|   Meeting attendee Processing page
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Agenda Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider, 
net.project.calendar.AttendeeBean,
            net.project.calendar.MeetingBean,
            net.project.persistence.PersistenceException,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager,
            net.project.resource.IFacility" 

%>

<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="attendee" class="net.project.calendar.AttendeeBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 

<%
    // validate the security check 
    int module = securityProvider.getCheckedModuleID();
    String id = securityProvider.getCheckedObjectID();
    int action = securityProvider.getCheckedActionID();

    if ((!id.equals(meeting.getID())) || (action != net.project.security.Action.MODIFY) || (module != net.project.base.Module.CALENDAR)) {
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message"));
    }

    //Get the location of the page that we are going to return to when we are done processing this one
    java.util.Hashtable nav = (java.util.Hashtable)session.getValue("PageNavigator");
    String myReturnTo = (String)nav.get("MeetingItem_returnto");

    //Determine what action this processing page is going to process
	String theaction = request.getParameter("theAction");

	if (theaction.equals("submit")) {
        attendee.setPersonId(request.getParameter("personId"));
        attendee.setStatusId(request.getParameter("statusId"));
        attendee.setComment(request.getParameter("comment"));
        attendee.setHostID(meeting.getHostID());
        attendee.store();
    }

    //Done processing the page, redirect
    if (myReturnTo != null) 
    {
        response.sendRedirect(myReturnTo);
    }
%>

