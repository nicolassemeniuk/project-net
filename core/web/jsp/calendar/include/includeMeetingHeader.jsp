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
|   use this to include a meeting header block
|   defaults to a short display.  To obtain a long display set
|   the request attribute:
|        display="long"
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
       info="Header table containing info about meetingr"
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.base.property.PropertyProvider,
	   		   net.project.calendar.*,
			   net.project.resource.*,
			   net.project.security.*,
               net.project.util.DateFormat,
               net.project.util.HTMLUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.CALENDAR%>"
					   objectID="<%=meeting.getID()%>" /> 
<%!
public static final String PARAM_display = "display";
public static final String DISPLAY_long = "long";
public static final String START_MEETING = "Start Meeting";
public static final String JOIN_MEETING = "Join Meeting";
%>
<%
    DateFormat df = new DateFormat(user);

    boolean displayLong = false;
    String display = (String)request.getAttribute(PARAM_display);
    if ((display != null) && (display.equals(DISPLAY_long)))
        displayLong = true;
%>
<table cellpadding="2" cellspacing="1"  width="100%" border="0">
<% if (!displayLong) { %>
<tr align="left" valign="top">
	<th nowrap width="11%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.header.meeting.label")%>
	</th>
	<td nowrap colspan="3" width="89%" class="tableContent"><%=HTMLUtils.escape(meeting.getName())%>
	</td>
</tr>
<% } %>
<tbody>
<tr align="left" valign="top">
	<th nowrap width="11%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.header.date.label")%>
	</th>
	<td nowrap colspan="3" width="89%" class="tableContent"><jsp:getProperty name="meeting" property="dateString" />
	</td>
</tr>
<tr align="left" valign="top">
	<th nowrap width="11%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.header.time.label")%>
	</th>
	<td colspan="3" width="89%" class="tableContent">
		<%=PropertyProvider.get("prm.calendar.meeting.header.timeto.duration", new Object [] {df.formatTime(meeting.getStartTime()), df.formatTime(meeting.getEndTime())})%>
	</td>
</tr>
<%
IFacility facility = meeting.getFacility();
%>
<tr align="left" valign="top">
	<th nowrap width="11%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.header.facilitytype.label")%>
	</th>
	<td colspan="3" width="89%" class="tableContent"><%= facility.getName() %>
<%
if (displayLong) {
    if (facility.getType().equals(FacilityType.WEBEX)) {
        WebExFacility webex = (WebExFacility)facility;
        String href=null;
        String displayText = null;
        if (user.getID().equals(meeting.getHostID())) {
            href =  "StartWebExMeetingProcessing.jsp?CM=HM"
                    + "&module=" + net.project.base.Module.CALENDAR
                    + "&action=" + net.project.security.Action.VIEW
                    + "&id=" + meeting.getID();
            displayText = START_MEETING;
        } else {
            href = "StartWebExMeetingProcessing.jsp?CM=JM"
                    + "&module=" + net.project.base.Module.CALENDAR
                    + "&action=" + net.project.security.Action.VIEW
                    + "&id=" + meeting.getID();

            displayText = JOIN_MEETING;
        }
%>	
        <SCRIPT LANGUAGE = "JAVASCRIPT">
            function startMeeting() {
                if((theForm.securebox) && (theForm.securebox.checked == true)) {
                    window.open("<%= href %>&secure=true", "meeting_window", "");
                } else {
                    window.open("<%= href %>&secure=false", "meeting_window", "");
                }
            }

            function disableSecureBox() {
                if (theForm.securebox) {
                    theForm.securebox.disabled=true;
                }
            }
        </SCRIPT>
        
        &nbsp;&nbsp;<A HREF="javascript:startMeeting(); "><%= displayText %></A>
        &nbsp;&nbsp;
    <%
        webex.load();
    %>
        <display:if name="prm.global.calendar.meeting.facility.type.webex.sslenabled">
    <%
        if (webex.isSecure()) {
            out.println( " <input TYPE = \"CHECKBOX\"  name=\"securebox\"  value=\"securebox\" CHECKED DISABLED>Secure?");
        } else { 
            out.println( " <input TYPE = \"CHECKBOX\"  name=\"securebox\"  DISABLED value=\"securebox\">Secure?");
        }
    %>
        </display:if>
    <%
    }
}
%>
    </td>                                       
</tr>

<tr align="left" valign="top">
	<th nowrap width="11%" class="tableHeader" ><%=PropertyProvider.get("prm.calendar.meeting.header.facilitydescription.label")%>
	</th>
	<td colspan="3" width="89%" class="tableContent"><%= net.project.util.HTMLUtils.escape(facility.getDescription()) %>
    </td>
</tr>

<tr align="left" valign="top">
	<th nowrap width="11%" class="tableHeader" ><%=PropertyProvider.get("prm.calendar.meeting.header.host.label")%>
	</th>
	<td colspan="3" width="89%" class="tableContent"><%= net.project.util.HTMLUtils.escape(meeting.getHost().getDisplayName()) %>
    </td>
</tr>

<% if (displayLong)
    {
%>
<tr align="left" valign="top">
	<th nowrap width="11%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.header.purpose.label")%></th>
	<td colspan="3" width="89%" class="tableContent">&nbsp;</td>
</tr>
<tr align="left" valign="top">
	<td colspan="4" width="100%" class="tableContent">
        <output:text><jsp:getProperty name="meeting" property="purpose" /></output:text>
	</td>
</tr>
<tr align="left" valign="top">
	<th nowrap width="11%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.header.description.label")%></th>
	<td colspan="3" width="89%" class="tableContent">&nbsp;</td>
</tr>
<tr align="left" valign="top">
	<td colspan="4" width="100%" class="tableContent">
        <output:text><jsp:getProperty name="meeting" property="description" /></output:text>
	</td>
</tr>
<%
    }
%>
</tbody>
</table>