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
    info="Meeting Manager"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.calendar.CalendarBean,
		    net.project.calendar.MeetingBean,
			net.project.channel.Channel,
			net.project.channel.ChannelManager,
		    net.project.link.LinkManagerBean,
		    net.project.security.SecurityProvider,
		    net.project.security.User,
		    net.project.security.SessionManager, net.project.space.SpaceFactory,
		    net.project.space.Space,
            net.project.base.Module,
            net.project.security.Action,
            net.project.util.HTMLUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="display_linkMgr" class="net.project.link.LinkManagerBean" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session"/>
<jsp:useBean id="calendar" class="net.project.calendar.CalendarBean" scope="session"/>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session"/>

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.CALENDAR%>" />

<%
	int module = net.project.base.Module.CALENDAR;
	String id = request.getParameter("id");
	int action = net.project.security.Action.VIEW;
%>

<%
// This page will be accessed via external notifications.  We need to make sure that
// if the user is already logged in they are sent through the notification frameset
if ((request.getParameter("external") != null) && (request.getParameter("inframe") == null))
    {
    meeting.reset();
    meeting.setID(id);
    String project_id = meeting.getSpaceID();
    session.setAttribute("requestedPage",
                     SessionManager.getJSPRootURL() + "/project/Main.jsp?id=" + project_id
                     + "&page=" +
                        java.net.URLEncoder.encode(
                                request.getRequestURI() + "?id=" + id + "&module=" + module + "&action" + action,
                                SessionManager.getCharacterEncoding()
                        )
    );
    response.sendRedirect(SessionManager.getJSPRootURL() + "/NavigationFrameset.jsp");
    return;
    }
%>
<%
String mySpace=user.getCurrentSpace().getType();
// If the user is in their personal space, change context to meetings project space
if (mySpace == null || mySpace.equals(Space.PERSONAL_SPACE))
    {
    meeting.reset();
    meeting.setID(id);
    String project_id = meeting.getSpaceID();
	Space meetingSpace = SpaceFactory.constructSpaceFromID(meeting.getSpaceID());
	meetingSpace.load();
	if(meetingSpace.isTypeOf(Space.PROJECT_SPACE)){
		String redirect = SessionManager.getJSPRootURL() + "/project/Main.jsp?id=" + project_id
							    + "&page=" + java.net.URLEncoder.encode(request.getRequestURI() + "?" + request.getQueryString(), SessionManager.getCharacterEncoding());
		response.sendRedirect(redirect);
	    return;
	}
    }
%>
<%
// initialize the calendar
calendar.setSpaceId(SessionManager.getUser().getCurrentSpace().getID());
calendar.loadIfNeeded();

// initialize the meeting
meeting.reset();
meeting.setCalendar(calendar);
meeting.setID(id);
meeting.load();

// configure session navigation object
String myPath = SessionManager.getJSPRootURL() + "/calendar/MeetingManager.jsp?module=" + module +
                        "&action=" + action +
                        "&id=" + id;
java.util.Hashtable nav = (java.util.Hashtable)request.getSession().getAttribute("PageNavigator");
if (nav == null)
    {
    nav = new java.util.Hashtable();
    request.getSession().setAttribute("PageNavigator", nav);
    }
String myReturnTo = (String)nav.get("MeetingManager_returnto");
if (myReturnTo == null)
    myReturnTo = SessionManager.getJSPRootURL() + "/project/Dashboard";
nav.put("MeetingList_returnto", myPath);
nav.put("MeetingItem_returnto", myPath);
nav.put("MeetingEdit_returnto", myPath);

// Does the user have permission to edit this meeting?
boolean editable = securityProvider.isActionAllowed(id,
                                                    Integer.toString(net.project.base.Module.CALENDAR),
                                                    net.project.security.Action.MODIFY);
// if the user does have edit permission, cache it in the navigation object
if (editable)
    nav.put("editable", id);
else
    nav.remove("editable");

// decied which buttons to show
String enableModifyButton = "false";
String showSubmitButton = "false";
String showCancelButton = "false";
if (editable) {
	enableModifyButton="true";
	showSubmitButton="true";
	showCancelButton="true";
}
%>
<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />
<%-- Import Javascript --%>
<template:import type="javascript" src="/src/standard_prototypes.js" />
<script language="javascript">
	var theForm;
	var isLoaded = false;
	
	// Variable declarations  for blog-it 
	var blogItFor ='meetingFor'+'<%=user.getCurrentSpace().getSpaceType().getName()%>';
	var spaceId = <%= user.getCurrentSpace().getID()%>;
	var moduleId = <%= net.project.base.Module.CALENDAR%>;
	var objectTypeNameFor = 'Meeting';
	var objectId = '<%=meeting.getID()%>';
    var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";
    
function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    isLoaded = true;
	theForm = self.document.forms[0];
}

function help()
{
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=calendar&section=meeting_view";
	openwin_help(helplocation);
}


function cancel() {
    var theLocation="<%= myReturnTo %>";
    self.location = theLocation;
}

<% if (editable)
    {
%>
function submit () {
    theAction("submit");
    theForm.submit();
}

function modify() {
    var theLocation="<%= SessionManager.getJSPRootURL() %>/calendar/MeetingEdit.jsp?id=<%= meeting.getID() %>&module=<%= module %>&action=<%= net.project.security.Action.MODIFY %>";
    self.location = theLocation;
}

function remove() {
	// if object is linked you can delete it.
	//if (<%= display_linkMgr.getLinksTo(meeting, net.project.link.ILinkableObject.GENERAL).isLinked() %>) {
	//	var errorMessage = 'The object is linked and cannot be deleted';
	//	extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
	//	return;
	//}
	Ext.MessageBox.confirm('<display:get name="prm.global.extconfirm.title" />', '<display:get name="prm.project.calendar.meeting.deletemeeting.confirm" />', function(btn) { 
		if(btn == 'yes'){ 
			theAction("remove");
			theForm.action.value = '<%=Action.DELETE%>';
			theForm.submit();
		}
	});
}

<%
    }
%>

function reset() {
    theLocation="<%= myPath %>";
    self.location = theLocation;
}
</script>
</head>

<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.calendar.mainpage.title">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.calendar.module.history")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/calendar/Main.jsp"%>'
					queryString='<%="module="+net.project.base.Module.CALENDAR%>' />
			<history:page display="<%=meeting.getName()%>" />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
        <tb:button type="remove" enable="<%=enableModifyButton%>" />
		<tb:button type="modify" enable="<%=enableModifyButton%>" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%= SessionManager.getJSPRootURL() %>/calendar/MeetingManagerProcessing.jsp">
    <input type="hidden" name="theAction">
    <input type="hidden" name="id" value="<jsp:getProperty name="meeting" property="ID" />">
    <input type="hidden" name="action" value="<%=Action.MODIFY %>" >
    <input type="hidden" name="module" value="<%=Module.CALENDAR %>" >


    <div align="left">
 <table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr class="channelHeader">
 	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
 	<td nowrap class="channelHeader" colspan="4"><nobr><%=HTMLUtils.escape(meeting.getName())%></nobr></td>
 	<td align=right width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>

<tr>
    <td colspan="6">
<%
    request.setAttribute("display", "long");
%>
    <jsp:include page="include/includeMeetingHeader.jsp" flush="true" />
    </td>
</tr>
<tr>
	<td colspan="6">&nbsp;</td>
</tr>
</table>
<%
if (editable)
    request.setAttribute("editable", "1");

Channel agendaChannel = new Channel("MeetingManager_Agenda_" + meeting.getName());
agendaChannel.setTitle(PropertyProvider.get("prm.calendar.meetingmanager.channel.agenda.title"));
agendaChannel.setWidth("100%");
agendaChannel.setMinimizable(false);
agendaChannel.setCloseable(false);
agendaChannel.setInclude("/calendar/include/includeAgendaList.jsp");
if (editable) {
    String buttonAction = SessionManager.getJSPRootURL() + "/calendar/MeetingAgendaList.jsp?module=" + module + "&action=" + action + "&id=" + id;
    agendaChannel.addActionButtonTokens("modify", null, buttonAction);
}
Channel attendeeChannel = new Channel("MeetingManager_Attendee_" + meeting.getName());
attendeeChannel.setTitle(PropertyProvider.get("prm.calendar.meetingmanager.channel.attendees.title"));
attendeeChannel.setWidth("100%");
attendeeChannel.setMinimizable(false);
attendeeChannel.setCloseable(false);
attendeeChannel.setInclude("/calendar/include/includeAttendeeList.jsp");
if (editable) {
    String buttonAction = SessionManager.getJSPRootURL() + "/calendar/MeetingAttendeeList.jsp?module=" + module + "&action=" + action + "&id=" + id;
    attendeeChannel.addActionButtonTokens("modify", null, buttonAction);
}
display_linkMgr.setView(display_linkMgr.VIEW_ALL);
display_linkMgr.setContext(net.project.link.ILinkableObject.GENERAL);
display_linkMgr.setRootObject(meeting);
Channel linkChannel = new Channel("MeetingManager_Links_" + meeting.getName());
linkChannel.setTitle(PropertyProvider.get("prm.calendar.meetingmanager.channel.links.title"));
linkChannel.setWidth("400");
linkChannel.setMinimizable(false);
linkChannel.setCloseable(false);
linkChannel.setInclude("/link/displayLinks.jsp");
if (editable) {
    String buttonAction = SessionManager.getJSPRootURL() + "/calendar/MeetingAddLink.jsp?module=" + module + "&action=" + net.project.security.Action.MODIFY + "&id=" + id + "&context=" + net.project.link.ILinkableObject.GENERAL + "&view=" + display_linkMgr.VIEW_FROM;
    linkChannel.addActionButtonTokens("modify", null, buttonAction);
}

    ChannelManager manager = new ChannelManager(pageContext);
    manager.setCustomizable(false);
    manager.addChannel(agendaChannel, 0, 0);
    manager.addChannel(attendeeChannel, 1, 0);
    manager.addChannel(linkChannel, 2, 0);
    manager.display();
%>
    </div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" show="<%=showSubmitButton%>" />
	</tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
