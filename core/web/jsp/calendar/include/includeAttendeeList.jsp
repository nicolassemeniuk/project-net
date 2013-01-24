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
|   use this to include a listing of attendees for a meeting
|   set the request attribute
|       AttendeeSelectEnabled="true"
|   to obtain a list that includes selectable radio controls
|   
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
       info="Attendee list"
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.base.property.PropertyProvider,
	   		   net.project.calendar.*,
			   net.project.security.*" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<%--<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> --%>

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.CALENDAR%>"
					   objectID="<%=meeting.getID()%>" /> 
<%
    boolean enableSelect = false;
    if (request.getAttribute("AttendeeSelectEnabled") != null)
        enableSelect = true;

    boolean editable = false;
    if (request.getAttribute("editable") != null) 
        editable = true;
%>

<input type="hidden" name="IncludeJSP" value="MeetingAttendeeListProcessing.jsp">   
<table cellpadding="0" cellspacing="0"  border="0" width="100%">
    <tbody>
        <tr class="tableHeader">
<%
    if (enableSelect)
        {
%>
            <td>
            <input type="hidden" name="AttendeeSelectEnabled" value="true">                    
            </td>
<%
        }
%>

            <td width="26%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.attendeelist.name.column")%></td>
            <td width="24%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.attendeelist.status.column")%></td>
            <td colspan="2" width="34%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.attendeelist.comment.column")%></td>
        </tr>
<tr class="tableLine">
	<td  colspan="5" class="tableLine"><img src="/images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
</tr>

<%  java.util.ArrayList attendees = meeting.getAttendees();
    if (attendees != null) 
        {
        net.project.code.TableCodeDomain domain = new net.project.code.TableCodeDomain();
        domain.setTableName("pn_cal_event_has_attendee");
        domain.setColumnName("status_id");
        domain.load();
        
        for (int i=0;i<attendees.size();i++) 
            {
                AttendeeBean theAttendee = (AttendeeBean)attendees.get(i);
			
%>
        <tr align="left" valign="top">                                    
<%
    if (enableSelect)
        {
%>
        <td align="center" class="tableContent">
        <input type="radio" name="selected" value="<%= theAttendee.getID() %>"/>
        </td>
<%
        }
%>                    
        <td align="left" class="tableContent">
<% 
if (editable)
    {
%>
            <A href="../calendar/MeetingAttendeeEdit.jsp?attendeeid=<%= theAttendee.getID() %>&module=<%= net.project.base.Module.CALENDAR %>&action=<%= net.project.security.Action.MODIFY %>&id=<%= meeting.getID() %>"><%= theAttendee.getPersonName() %></A><%
    }
else
    {
%>
            <%= theAttendee.getPersonName() %>
<%
    }
%>
        

        </td>
        <td align="left" class="tableContent">
<% 
if (editable)
    {
%>
            <select name="attendee_status_<%= theAttendee.getID() %>">
            <%= domain.getOptionList(theAttendee.getStatusID()) %>
            </select>
<%
    }
else
    {
%>
            <%= theAttendee.getStatus() %>
<%
    }
%>
        </td>
        <td colspan="2" class="tableContent">
            <%=  net.project.util.HTMLUtils.escape(theAttendee.getComment()) %>                                       
        </td>
        </tr>
     <%
                }
            }
     %>                               
    </tbody>
</table>                                    
