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
|   Event edit processing page
|
|
|   Author: Adam Klatzkin
|--------------------------------------------------------------------%>

<%@ page
    contentType="text/html; charset=UTF-8"
    info="Event edit processing.. omits no output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
		    net.project.resource.IFacility,
    		net.project.resource.FacilityFactory,
            net.project.resource.UnknownFacility,
            net.project.util.ErrorDescription,
            net.project.calendar.TimeBean"
%>

<jsp:useBean id="event" class="net.project.calendar.CalendarEvent" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>

<%
    java.util.Hashtable nav = (java.util.Hashtable)request.getSession().getValue("PageNavigator");
    String myReturnTo = (String)nav.get("MeetingEdit_returnto");
 %>
<%-- Check if the date entered by the user is legal--%>
<%
    String dateString = request.getParameter("dateString");
    if (!user.getDateFormatter().isLegalDate(dateString)) {
        errorReporter.addError(new ErrorDescription(
            "dateString",
            PropertyProvider.get("prm.calendar.eventedit.eventdate.error.message", new Object[] {dateString})
        ));
    } else {
        event.setDate(dateString);
    }
%>

<%-- Get the form fields --%>
<jsp:setProperty name="event" property="*" />

<% 
	// bfd - 2994 issue
	if ((request.getParameter("purpose") == null) || (request.getParameter("purpose").equals(""))) {
		event.setPurpose("");
	}
	if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		event.setDescription("");
	}
%>

<%
    // validate the security check
    int module = securityProvider.getCheckedModuleID();
    String id = securityProvider.getCheckedObjectID();
    int action = securityProvider.getCheckedActionID();
    // If id is null then this page will create a new event
    if ((id.length() == 0) &&
        ((event.getID() != null) ||
         (action != net.project.security.Action.CREATE) ||
         (module != net.project.base.Module.CALENDAR)
        )
       )
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message")
    );
    // If id is not null the user must have modify permission on the id,
    else if ((id.length() > 0) &&
             ((!id.equals(event.getID())) ||
              (action != net.project.security.Action.MODIFY) ||
              (module != net.project.base.Module.CALENDAR)
             )
            )
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message")
    );
%>

<%
    // set the meetings start and end times from the form fields
    // This assumes that the meeting date has already been set
    event.setStartTime(TimeBean.parseTime(request, "start", event.getStartTime()));
    event.setEndTime(TimeBean.parseTime(request, "end", event.getEndTime()));
    
	//lets check is start time after end time
    if(event.getStartTime().after(event.getEndTime())){
    	errorReporter.addError(PropertyProvider.get("prm.calendar.eventedit.eventtime.error.starttimecannotafterendtime.message"));
    }

    // remove the events current facility object if one exists
    IFacility facility = null;
    facility = event.getFacility();
    if ((facility != null) && (!(facility instanceof UnknownFacility)))
        facility.remove();

    // Create the new facility object
    String facilityTypeID = request.getParameter("FacilityType");
    facility = FacilityFactory.createNew(facilityTypeID);
    facility.setOwnerId(user.getID());
    facility.setDescription(request.getParameter("FacilityDescription"));
    event.setFacility(facility);

    //If there are errors, deal with them now
    if (errorReporter.errorsFound()) {
        %>
        <jsp:forward page="EventEdit.jsp">
            <jsp:param name="isShowingError" value="true" />
        </jsp:forward>
        <%
    } else {
        // update the event object and store
        event.store();

        response.sendRedirect(myReturnTo);
    }
%>