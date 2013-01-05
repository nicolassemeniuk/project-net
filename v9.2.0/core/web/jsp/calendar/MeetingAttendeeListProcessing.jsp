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
|   Attendee list processing page
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Agenda Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider, 
net.project.calendar.MeetingBean,
    net.project.calendar.AttendeeBean,
    net.project.security.SecurityProvider,
    net.project.security.SessionManager" 
%>

<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="attendee" class="net.project.calendar.AttendeeBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 
	
<%
// validate the security check 
int module = securityProvider.getCheckedModuleID();
String id = securityProvider.getCheckedObjectID();
int action = securityProvider.getCheckedActionID();

if ((!id.equals(meeting.getID())) || 
    (action != net.project.security.Action.MODIFY) ||
    (module != net.project.base.Module.CALENDAR))
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message")
);
%>
     
<%! public static final String          STATUS_NAME         = "attendee_status_"; %>
<%
    java.util.Hashtable nav = (java.util.Hashtable)request.getSession().getValue("PageNavigator");
    String myReturnTo = (String)nav.get("MeetingList_returnto");

    //bfd-5080
    if (myReturnTo == null){
        myReturnTo = SessionManager.getJSPRootURL() + "/calendar/Main.jsp?module="+module;
    }
    
    boolean isWizard = (nav.get("wizard") != null) ? true : false;

	String theaction = request.getParameter("theAction");
	if (theaction.equals("submit"))
		{
        java.util.Enumeration params = request.getParameterNames();
        attendee.clear();
        attendee.setEvent(meeting);
        while (params.hasMoreElements()) 
            {
            String paramName = (String)params.nextElement();
            if (paramName.startsWith(STATUS_NAME)) 
                {
                attendee.setID(paramName.substring(STATUS_NAME.length()));
                attendee.updateStatus(request.getParameter(paramName));
                }
            }
        }
    else if (theaction.equals("remove"))
	    {
        // remove entry
	    attendee.setEvent(meeting);
        // Temp fix due to bluestone bug
        String temp = request.getParameter("selected");
%>
<jsp:setProperty name="attendee" property="personId" value="<%= temp %>" />
<%
	    attendee.remove();
        response.sendRedirect((String)nav.get("MeetingItem_returnto"));
        return;
        }
%>

<%
    if (request.getAttribute("noForward") == null) 
        {
        if ((isWizard) && (request.getParameter("wizard").equals("next")))
            {

			    // we are no longer notifying attendees until the completion of the wizard.
                //meeting.notifyNewAttendees();
				//meeting.clearNewAttendeesList();
				
            	request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
                
//Avinash:--------------------------------------------------------------------------
    		net.project.security.ServletSecurityProvider.setAndCheckValues(request);
//Avinash:--------------------------------------------------------------------------
%>
<jsp:forward page="MeetingAgendaList.jsp" />
<%

            }
        else
            {
				response.sendRedirect(myReturnTo + "&id=" + id);
            }
        }
		
%>
