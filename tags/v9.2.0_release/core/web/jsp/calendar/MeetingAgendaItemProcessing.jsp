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
|   Processing page for a meeting agenda item
|   
|   Request Parameters:
|       theAction   the action to process
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
    net.project.calendar.AgendaBean,
    net.project.security.SecurityProvider" 
%>

<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="agenda" class="net.project.calendar.AgendaBean" scope="session" />
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

<%
    java.util.Hashtable nav = (java.util.Hashtable)request.getSession().getValue("PageNavigator");
    String myReturnTo = (String)nav.get("MeetingItem_returnto");
	
	String refLink, refLinkEncoded,refLinkDecoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");
	refLinkDecoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLDecoder.decode(refLink) : "");

	String theaction = request.getParameter("theAction");
	if (theaction.equals("submit"))
		{
        // hack, since we are not securing agenda items but are securing the meeting
        // they belong to, the id in the request parameter will be the meeting id.
        // We are about to set the agenda bean from the request parameters which
        // will set an incorrect agenda id.  Therefore store the current one and
        // then reset.
        String agendaId = agenda.getId();
%>
<jsp:setProperty name="agenda" property="*" />

<%
	// bfd - 2994 issue
	if ((request.getParameter("allotedTime") == null) || (request.getParameter("allotedTime").equals(""))) {
		agenda.setAllotedTime("");
	}
	if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		agenda.setDescription("");
	}
	if ((request.getParameter("meetingNotes")== null) || (request.getParameter("meetingNotes").equals(""))) {
		agenda.setMeetingNotes("");
	}
%>

<%
		agenda.setId(agendaId);
        agenda.store();
		}
%>

<%
    if (myReturnTo != null) 
        {
        response.sendRedirect(myReturnTo+"&refLink="+refLinkEncoded);
        }
%>

