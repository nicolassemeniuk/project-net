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

<<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 19815 $
|       $Date: 2009-08-21 09:47:33 -0300 (vie, 21 ago 2009) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Processing for addition of new team member.  Emits no output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider, 
net.project.calendar.AttendeeBean,
            net.project.calendar.MeetingBean,
            net.project.persistence.PersistenceException,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager"
%>

<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session"/>
<jsp:useBean id="attendee" class="net.project.calendar.AttendeeBean" scope="session"/>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 

<%
    // validate the security check 
    int module = securityProvider.getCheckedModuleID();
    String id = securityProvider.getCheckedObjectID();
    int action = securityProvider.getCheckedActionID();

    if ((!id.equals(meeting.getID())) || (action != net.project.security.Action.MODIFY) || (module != net.project.base.Module.CALENDAR)) {
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message"));
    }

    //Get the location that we are going to return to when we are done processing this page
    java.util.Hashtable nav = (java.util.Hashtable)session.getValue("PageNavigator");
    String myReturnTo = (String)nav.get("MeetingItem_returnto");

    //Determine what action this processing page is going to process
	String theAction = request.getParameter("theAction");

	if (theAction.equals("submit")) {
        //Get parameters that we are going to need to save attendees
        String statusID = request.getParameter("statusID");
        String comment = request.getParameter("comment");

        // Iterate through the list of attendees, storing each
        String[] personID = request.getParameterValues("personID");

        for (int i = 0; ((personID != null) && (i < personID.length)); i++) {
            
			attendee = new AttendeeBean();		
            attendee.setEvent(meeting);
            attendee.setStatusID(statusID);
            attendee.setComment(comment);
            attendee.setID(personID[i]);
            attendee.setHostID(meeting.getHostID());
            attendee.setMStartDate(meeting.getStartTime());
            attendee.setMEndDate(meeting.getEndTime());
            
            //Try to store this attendee
            attendee.store();
			
			meeting.addNewAttendees(attendee);
			
        } //For loop

		// removed notification actions from here and added to end of new meeting wizard and modify
        //meeting.notifyNewAttendees();
		//meeting.clearNewAttendeesList();
    }

	String refLink, refLinkEncoded,refLinkDecoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");
	refLinkDecoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLDecoder.decode(refLink) : "");

    //Done processing the page, redirect
    if (myReturnTo != null) {
        response.sendRedirect(myReturnTo+"&refLink="+refLinkEncoded);
    }
%>        