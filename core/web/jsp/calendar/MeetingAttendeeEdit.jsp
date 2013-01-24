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
|   Displays an editable attendee
|   
|   Request Parameters:
|		id				id of meeting
|       attendeeid      id of the attendee to edit
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Edit Meeting Attendee" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.calendar.MeetingBean, 
            net.project.calendar.AttendeeBean,
            net.project.security.SecurityProvider,
            net.project.security.User,
            net.project.security.SessionManager,
            net.project.space.Space" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="attendee" class="net.project.calendar.AttendeeBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>                                                                                                     

<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.CALENDAR%>"
					   objectID="<%=meeting.getID()%>" /> 

<%
    String attendeeid = request.getParameter("attendeeid");

    attendee.clear();
    attendee.setEvent(meeting);
    attendee.setPersonId(attendeeid);
    attendee.load();

    java.util.Hashtable nav = (java.util.Hashtable)request.getSession().getValue("PageNavigator");
    String myReturnTo = (String)nav.get("MeetingItem_returnto");

    // Decided which actionbar buttons to show
    String showCancelButton = (myReturnTo != null ? "true" : "false");
%>
<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0"> 
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
    function setup() {
        load_menu('<%=user.getCurrentSpace().getID()%>');
        isLoaded = true;
        theForm = self.document.forms[0];
    }

    function cancel() {
        var theLocation="<%= myReturnTo %>";
        self.location = theLocation;
    }
    
    function submit () {
        theAction("submit");
        theForm.submit();
    }
    
    function reset() {
        theForm.reset();
    }

    function help() {
        var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=calendar&section=attendee_edit";
        openwin_help(helplocation);
    }
</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.calendar.mainpage.title">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="<%=meeting.getName()%>" 
					jspPage='<%=SessionManager.getJSPRootURL() + "/calendar/MeetingManager.jsp"%>'
					queryString='<%="module="+net.project.base.Module.CALENDAR+"&id="+meeting.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="MeetingAttendeeItemProcessing.jsp">
	<input type="hidden" name="theAction" />
    <input type="hidden" name="module" value="<%= net.project.base.Module.CALENDAR %>">
    <input type="hidden" name="action" value="<%= net.project.security.Action.MODIFY %>">
    <input type="hidden" name="id" value="<jsp:getProperty name="meeting" property="ID" />">
	<input type="hidden" name="personId" value="<%=attendeeid%>">
	

<table border="0" cellpadding="0" cellspacing="0" width="97%">
<tr class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" colspan="4"><%=PropertyProvider.get("prm.calendar.meeting.attendeeedit.channel.update.title")%></td>
	<td align=right width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr>
	<td colspan="6">
        <%-- include the meeting header inside this table cell --%>
        <%	request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));	
        	net.project.security.ServletSecurityProvider.setAndCheckValues(request);
        %>
        <jsp:include page="include/includeMeetingHeader.jsp"  />
	</td>
</tr>
<tr>
	<td colspan="6">&nbsp;</td>
</tr>

<tr class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" colspan="4"><nobr><%=PropertyProvider.get("prm.calendar.meeting.attendeeedit.channel.attendees.title")%></nobr></td>
	<td align=right width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr align="left" bgcolor="#FFFFFF" valign="middle">
	<td nowrap colspan="2" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.meeting.attendeeedit.person.label")%>
	</td>
	<td colspan="4" class="tableContent">
    <%= attendee.getPersonName() %>
	</td>
</tr>
<tr align="left"  valign="middle">
	<td nowrap colspan="2" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.meeting.attendeeedit.status.label")%>
	</td>
	<td colspan="4" class="tableContent">
		<select name="statusId">
            <jsp:getProperty name="attendee" property="statusOptionList" />
		</select>
	</td>
</tr>
<tr align="left" valign="middle">
	<td nowrap colspan="2" class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.meeting.attendeeedit.comment.label")%>
	</td>
	<td colspan="4" class="tableContent">
		<input type="text" name="comment" size="30" maxlength="80" value="<%= net.project.util.HTMLUtils.escape(attendee.getComment()) %>">
	</td>
</tr>

<tr>
    <td colspan="6">&nbsp;</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
	    <tb:button type="cancel" show="<%=showCancelButton%>" />
	    
	    <tb:button type="submit" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
