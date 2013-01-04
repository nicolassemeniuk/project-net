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
|   WebEx Processing page
|   The purpose of this page is to schedule a webex meeting.
|   This process requires communication with the webex server through
|   their API.
|   Implemented as a 3 state machine that redirects the user to
|   login to the webex server, redirects the user to schedule the meeting
|   and finally stores the webex information in our database.    
|   
|   Author: Adam Klatzkin
|--------------------------------------------------------------------
|   02/28/2001 MAF
|   Ug!  Another state machine.  I've tried to clean it up a bit by
|   changing magic numbers to CONSTANTS and clearing out the tokens.
|   This JSP needs to be rewritten as a class and cleaned up.
|   (Preferably swept under a rug.)
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Meeting Form processing.. omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="org.apache.log4j.Logger,
            java.net.URLEncoder,
            java.util.Date,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.calendar.MeetingBean, 
            net.project.calendar.PnCalendar, 
            net.project.resource.Person,
            net.project.resource.WebExFacility,
            net.project.security.Action,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager,
            net.project.security.User"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 

<%!
    //These CONSTANTS are defined to try to help make the state machine a bit more understandable
    final int LOGIN_INTO_WEBEX = 0;
    final int CREATE_MEETING = 1;
    final int PROCESS_CREATE_MEETING_RESULTS = 2;
%>

<%
// validate the security check 
int module = securityProvider.getCheckedModuleID();
String id = securityProvider.getCheckedObjectID();
int action = securityProvider.getCheckedActionID();
%>

<security:verifyAccess module="<%=Module.CALENDAR%>"
                       action="modify"
                       objectID="<%=meeting.getID()%>"/>

           
<%
// get the meetings webex facility object
WebExFacility webex = (WebExFacility)meeting.getFacility();

// Did the creator select secure or not secure, go to site accordingly
String webex_site = PropertyProvider.get("prm.global.calendar.meeting.facility.type.webex.siteurl");
int secure = Integer.parseInt (request.getParameter("SecurityType"));

String protocol;
if (secure == 1) {
    protocol = "https://";
    webex.setRoomName("secure"); 
    webex_site = PropertyProvider.get("prm.global.calendar.meeting.facility.type.webex.sslsiteurl");
} else {
    protocol = "http://";
    webex.setRoomName("unsecure");
}

// navigation
java.util.Hashtable nav = (java.util.Hashtable)request.getSession().getValue("PageNavigator");
String myReturnTo = (String)nav.get("MeetingEdit_returnto");   
boolean isWizard = (nav.get("wizard") != null) ? true : false;

// WebEx Meeting creation is performed by a state machine.
// Get the current state from the request parameter
String state = request.getParameter("state");
int iState = 0;
if (state != null) {
    iState = Integer.parseInt(state);
}

// Begin the processing
String theUrl = null;
switch (iState) {
    case LOGIN_INTO_WEBEX:
        // redirect the client to login
        Person theHost = meeting.getHost();

        // Load the meeting host as a user so I can access the info (Verification code)
        String hostid = theHost.getID();
        User hostAsUser = new User();
        hostAsUser.setID(hostid);
        try {
            hostAsUser.load();
        } catch (Exception e) {
        	Logger.getLogger(this.getClass()).error("Failed to load webex host " + e);
        }
    
        theUrl = protocol + webex_site + "/webex/s.asp?AT=PL&PID=00"
            + "&FN=" + URLEncoder.encode(theHost.getFirstName())
            + "&LN=" + URLEncoder.encode(theHost.getLastName())
            + "&WID=" + URLEncoder.encode(theHost.getUserName())
            + "&PW=" + URLEncoder.encode("webex_" +hostAsUser.getVerificationCode())
            + "&EM=" + URLEncoder.encode("webex." +theHost.getID()+"@" + PropertyProvider.get("prm.global.calendar.meeting.facility.type.webex.prm.global.calendar.meeting.facility.type.webex.userdomainsuffix"))
//            + "&EM=" + URLEncoder.encode(theHost.getEmail())
            + "&MU=GoBack"
            + "&BU=" + URLEncoder.encode(SessionManager.getAppURL()
            + "/calendar/WebExProcessing.jsp?state=" + (iState+1) 
            +"&module=" +module +"&id=" +id + "&CM=SM" + "&action=" + action
            +"&SecurityType=" + request.getParameter("SecurityType"));
        Logger.getLogger(this.getClass()).debug("The Webex Login: \n" + theUrl);

        response.sendRedirect(theUrl);  
        break;
    case CREATE_MEETING:
        //   redirect the client to schedule the meeting and set state to PROCESS_CREATE_MEETING_RESULTS
        Date start = meeting.getStartTime();
        Date end = meeting.getEndTime();
        PnCalendar cal = new PnCalendar();
        cal.setTime(start);
        webex.setPassword("12345"); // XXX
        theUrl = protocol + webex_site + "/webex/m.asp?AT=SM"
                    + "&MN=" + URLEncoder.encode(meeting.getName())
                    + "&PW=" + URLEncoder.encode(webex.getPassword())
                    + "&DU=" + (((end.getTime() - start.getTime()) / 1000) / 60)
                    + "&YE=" + cal.get(cal.YEAR)
                    + "&MO=" + (cal.get(cal.MONTH) + 1)
                    + "&DA=" + cal.get(cal.DAY_OF_MONTH)
                    + "&HO=" + cal.get(cal.HOUR)
                    + "&MI=" + cal.get(cal.MINUTE)           
                    + "&BU=" +
                    URLEncoder.encode(SessionManager.getAppURL()
                    + "/calendar/WebExProcessing.jsp?state=" + (iState+1) 
                    +"&module="+ module +"&id=" +id + "&CM=SM" + "&action=" + action
                    +"&SecurityType=" + request.getParameter("SecurityType"));
               
        Logger.getLogger(this.getClass()).debug("Schedule Meeting Command: \n" + theUrl);
        response.sendRedirect(theUrl);  
        break;
    case PROCESS_CREATE_MEETING_RESULTS: 
        // Handle the errors that come back from the webex site (schedule a meeting).

        String webex_status = request.getParameter("ST");

        // URL for webex errors
        String webex_error_url= SessionManager.getAppURL()+ "/WebExError.jsp";

        if (webex_status != null){
            if (!webex_status.equals("SUCCESS")) {
                String webex_reason = request.getParameter("RS");
                if (webex_reason.equals("AlreadyLogon")) {
                } else {
                    // remove the persistent meeting
                    meeting.remove();
                    // we need to log this error
                    Logger.getLogger(this.getClass()).error("Webex Action Failed: \"SM\":" +webex_reason);

                    //send to the WebExError page
                    response.sendRedirect(webex_error_url + "?module=" +module +"&id=" +id + "&CM=SM"
                        + "&action=" + action + "&RS=" + webex_reason);
                }
            }
        }
    
        // store the returned meeting key and finish     
        webex.setRoomNumber(request.getParameter("MK"));
        webex.store(); 
        if (isWizard) {
            request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
            pageContext.forward("MeetingAttendeeList.jsp");
        } else {
           // need to redirect because we have lost the request paramters and there is
           // no way to populate them before a forward
           response.sendRedirect(myReturnTo);
        }
        break;
    }
%>
