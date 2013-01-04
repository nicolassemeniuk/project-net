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
    info="Meeting Form processing.. omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider, 
			net.project.calendar.MeetingBean,
		    net.project.calendar.AttendeeBean,
		    net.project.security.SecurityProvider, 
		    net.project.security.SessionManager,
		    net.project.resource.IFacility,
		    net.project.resource.FacilityFactory,
			net.project.resource.FacilityType,
            java.util.Hashtable,
            net.project.util.ErrorDescription,
            net.project.resource.UnknownFacility,
            net.project.util.Validator,
            net.project.calendar.TimeBean,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.AuthorizationFailedException"
%>
<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>

<% 
    // validate the security check
    int module = securityProvider.getCheckedModuleID();
    String id = securityProvider.getCheckedObjectID();
    int action = securityProvider.getCheckedActionID();
    if(!Validator.isBlankOrNull(meeting.getMeetingID())){
    	id = meeting.getMeetingID();
    	action = Action.MODIFY;
    }	
    // If id is empty then this page will create a new meeting
    if ((id==null || id.length() == 0 ) && (!Validator.isBlankOrNull(meeting.getMeetingID())||
        (action != Action.CREATE) ||
        (module != Module.CALENDAR))) {
        throw new AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message") );
    }

    // If id is not empty the user must have modify permission on the id,
    else if ((id.length() > 0 && (id.equals("null")==false)) &&
             ((!id.equals(meeting.getMeetingID())) ||
              (action != Action.MODIFY) ||
              (module != Module.CALENDAR)
             )
            )
        throw new AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message")
    );

    Hashtable nav = (Hashtable)session.getAttribute("PageNavigator");
    String myReturnTo = (String)nav.get("MeetingEdit_returnto");
    boolean isWizard = (nav.get("wizard") != null) ? true : false;

    // Check if the date entered by the user is legal
    String dateString = request.getParameter("dateString");
    if (!SessionManager.getUser().getDateFormatter().isLegalDate(dateString)) {
        errorReporter.addError(new ErrorDescription(
            "date",
            PropertyProvider.get("prm.meetings.edit.datestring.error.cannotparse.message", new Object[] { dateString } )
        ));
    } else {
        meeting.setDate(dateString);
    }
%>
<%-- Get the form fields --%>
<jsp:setProperty name="meeting" property="*" />

<%
	// bfd - 2994 issue
	if ((request.getParameter("purpose") == null) || (request.getParameter("purpose").equals(""))) {
		meeting.setPurpose("");
	}
	if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		meeting.setDescription("");
	}
%>
	
<%
    // set the meetings start and end times from the form fields
    // This assumes that the meeting date has already been set
    meeting.setStartTime(TimeBean.parseTime(request, "start", meeting.getStartTime()));
    meeting.setEndTime(TimeBean.parseTime(request, "end", meeting.getEndTime()));
    
    //lets check is start time after end time
    if(meeting.getStartTime().after(meeting.getEndTime())){
    	errorReporter.addError(PropertyProvider.get("prm.meetings.edit.datestring.error.starttimecannotafterendtime.message"));
    }

    // remove the meetings current facility object if one exists
    IFacility facility = null;
    facility = meeting.getFacility();
    if ((facility != null) && (!(facility instanceof UnknownFacility)))
        facility.remove();

    // Create the new facility object
    String facilityTypeID = request.getParameter("FacilityType");
    FacilityType facilityType = FacilityType.forID(facilityTypeID);
    facility = FacilityFactory.createNew(facilityType);
    facility.setOwnerId(meeting.getHostID());
    facility.setDescription(request.getParameter("FacilityDescription"));
    meeting.setFacility(facility);

    if (errorReporter.errorsFound()) {
%>
        <jsp:forward page="MeetingEdit.jsp">
            <jsp:param name="isShowingError" value="true"/>
        </jsp:forward>
<%
    } else {
        // update the meeting object and store
        meeting.store();

        // Is the meeting host an attendee?? If not make him one.
        AttendeeBean host = new AttendeeBean();
        host.setID(facility.getOwnerId());
        host.setHostID(facility.getOwnerId());
        host.setEvent(meeting);
        host.setMStartDate(meeting.getStartTime());
        host.setMEndDate(meeting.getEndTime());
        host.load();
        // did the host load successfully?
        if (!host.isLoaded()) {
            host.setStatusID("20");
            host.store();
        }

        // Finish Processing
        if (facilityType.equals(FacilityType.WEBEX)) {
            //System.out.println("SSSSSS SECURE_TYPE = " + request.getParameter("SecurityType"));
                //String theUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getRequestURI();
            //theUrl = theUrl.substring(0, theUrl.lastIndexOf("/")) + "/WebExProcessing.jsp";
            String theUrl = SessionManager.getJSPRootURL() + "/calendar/WebExProcessing.jsp?"
                            + "module=" + net.project.base.Module.CALENDAR
                            + "&action=" + net.project.security.Action.MODIFY
                            + "&id=" + meeting.getID()
                            + "&SecurityType=" + request.getParameter("SecurityType");
            //Avinash:-----------------------------------------------
            	request.getRequestDispatcher(theUrl).forward(request,response);
            //Avinash:-----------------------------------------------
            //response.sendRedirect(theUrl);
        } else if (isWizard) {
            request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
            request.setAttribute("id", meeting.getID());
            
	//Avinash:-----------------------------------------------
			net.project.security.ServletSecurityProvider.setAndCheckValues(request);
	//Avinash:-----------------------------------------------
%>
        <jsp:forward page="MeetingAttendeeList.jsp" />
<%
        } else {
            response.sendRedirect(myReturnTo);
        }
    }
%>
