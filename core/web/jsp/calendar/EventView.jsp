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
|   $Revision: 20795 $
|       $Date: 2010-05-04 13:31:53 -0300 (mar, 04 may 2010) $
|
|   Displays an uneditable event description
|   
|   Request Parameters:
|       id      id of the event to view
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="View Event" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.calendar.CalendarEvent,
			net.project.calendar.CalendarBean,
			net.project.security.User,
			net.project.security.SecurityProvider,
			net.project.security.SessionManager,
			net.project.resource.IFacility,
			net.project.space.Space,
            net.project.util.DateFormat,
            net.project.security.Action,
            net.project.util.HTMLUtils,
            net.project.space.SpaceTypes"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>                                                                                                     
<jsp:useBean id="event" class="net.project.calendar.CalendarEvent" scope="session" />
<jsp:useBean id="calendar" class="net.project.calendar.CalendarBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0"> 
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.CALENDAR%>" /> 


<%
// get the space from the session that was put in on the main calendar page
String id = securityProvider.getCheckedObjectID();
String mySpace=null;
mySpace=user.getCurrentSpace().getType();
// If the user is in their personal space, change context to events project space if
// necessary
if (mySpace == null || mySpace.equals(net.project.space.Space.PERSONAL_SPACE))
    {
    event.reset();
    event.setID(id);
    event.getEventSpaceDetails();
    String space_id = event.getEventSpaceId();
    String event_space_type = event.getEventSpaceType();
    if (!space_id.equals(user.getCurrentSpace().getID()) && !SpaceTypes.PERSONAL_SPACE.equals(event_space_type)) 
        {
   		String redirect = SessionManager.getJSPRootURL() + "/"+event_space_type+"/Main.jsp?id=" + space_id
                              + "&page=" + java.net.URLEncoder.encode(request.getRequestURI() + "?" + request.getQueryString());
        response.sendRedirect(redirect);
        return;
        }
    }
%>
<%
    DateFormat df = new DateFormat(user);

// initialize the calendar
calendar.setSpace(SessionManager.getUser().getCurrentSpace());
calendar.loadIfNeeded();

// reinitialize the event object
event.reset();
event.setCalendar(calendar);
event.setID(id);
event.load();
    
// Navigation through the calendar is complex with many possible paths so we will use 
// a hashtable stored in the session to maintain navigation state
java.util.Hashtable nav = (java.util.Hashtable)request.getSession().getValue("PageNavigator");
if (nav == null) {
    nav = new java.util.Hashtable();
    request.getSession().putValue("PageNavigator", nav);
}

String myReturnTo = (String)nav.get("MeetingEdit_returnto");
if (myReturnTo == null)
    myReturnTo = SessionManager.getJSPRootURL() + "/project/Dashboard";

%>

<script language="javascript">
var theForm;
var isLoaded = false;
	
function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	isLoaded = true;
	theForm = self.document.forms[0];
}
     
function cancel() {
   self.location = "<%= myReturnTo %>";
}
   
function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=calendar&section=event_view";
	openwin_help(helplocation);
}

function modify() {
      theAction("modify");
	  theForm.action.value = '<%=Action.MODIFY%>';
      theForm.submit();
}

function remove() {
	Ext.MessageBox.confirm('<display:get name="prm.global.extconfirm.title"/>', '<display:get name="prm.calendar.event.remove.confirm.message" />', function(btn) { 
				if( btn == 'yes' ){
				    theAction("remove");
				    theForm.action.value = '<%=Action.DELETE%>';
				    theForm.submit();		
				}  
			});
}

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.calendar.mainpage.title">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.calendar.module.history")%>' 
					jspPage='<%=SessionManager.getJSPRootURL() + "/calendar/Main.jsp"%>'
					queryString='<%="module="+net.project.base.Module.CALENDAR%>' />
			<history:page display='<%=PropertyProvider.get("prm.calendar.event.module.history")%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="modify" />
        <tb:button type="remove"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%=SessionManager.getJSPRootURL() + "/calendar/EventViewProcessing.jsp"%>">
	<input type="hidden" name="theAction">               
	<input type="hidden" name="module" value="<%=net.project.base.Module.CALENDAR%>">
	<input type="hidden" name="action" value="<%=net.project.security.Action.VIEW%>">
	<input type="hidden" name="id" value="<%=event.getID()%>">
	
<div align="left">

<table  border="0" cellpadding="0" cellspacing="0" width="100%">
<tbody>
<tr class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" colspan="4"><nobr><%=PropertyProvider.get("prm.calendar.event.channel.event.title")%>&nbsp;&nbsp;</nobr></td>
	<td align=right width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr>
	
	<td>&nbsp;</td>
	<th nowrap align="left" width="30%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.event.name.label")%>&nbsp;&nbsp;</th>
	<td nowrap colspan="3" class="tableContent">
		<%= HTMLUtils.escape(event.getName()) %>
	<td>&nbsp;</td>

</tr>                        
<tr >
<td>&nbsp;</td>
	<th nowrap align="left" width="30%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.event.date.label")%>&nbsp;&nbsp;</th>
	<td nowrap colspan="3" class="tableContent">
		<jsp:getProperty name="event" property="dateString" />
	</td>
	<td>&nbsp;</td>
</tr>
		<tr >
<td>&nbsp;</td>
	<th nowrap align="left" width="30%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.event.time.label")%>&nbsp;&nbsp;</th>
	<td nowrap colspan="3" class="tableContent">
		<%=PropertyProvider.get("prm.calendar.event.timeto.duration", new Object [] {df.formatTime(event.getStartTime()), df.formatTime(event.getEndTime())})%>
	</td>
	<td>&nbsp;</td>
</tr>
<tr >
<td>&nbsp;</td>
	<th nowrap align="left" width="30%" class="tableHeader"><%=PropertyProvider.get("prm.calendar.event.facilitydescription.label")%>&nbsp;&nbsp;</th>
	<td nowrap colspan="3"  class="tableContent">
<%
IFacility ofacility = event.getFacility();
String facDesc = ofacility.getDescription();
%>
<%= facDesc %>
    </td>
    <td>&nbsp;</td>
</tr>                
<tr align="left">
	<td>&nbsp;</td>
	<td valign="top" align="left" colspan="4" class="tableHeader">
		<%=PropertyProvider.get("prm.calendar.event.purpose.label")%>&nbsp;&nbsp;
    </td>
	<td>&nbsp;</td>
</tr>
<tr align="left">
	<td>&nbsp;</td>
	<td valign="top" align="left" colspan="4" class="tableContent">
        <output:text><jsp:getProperty name="event" property="purpose" /></output:text>
	</td>
	<td>&nbsp;</td>
</tr>
<tr align="left">
    <td>&nbsp;</td>
	<td valign="top" align="left" colspan="4" class="tableHeader">
		<%=PropertyProvider.get("prm.calendar.event.description.label")%>&nbsp;&nbsp;
	</td>
	<td>&nbsp;</td>
</tr>
<tr align="left">
    <td>&nbsp;</td>
	<td valign="top" align="left" colspan="4" class="tableContent">
        <output:text><jsp:getProperty name="event" property="description" /></output:text>
	</td>
	<td>&nbsp;</td>
</tr>
</table>
</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
	</tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

