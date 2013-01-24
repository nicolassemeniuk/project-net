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

<%---------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 20058 $
|       $Date: 2009-10-05 12:40:42 -0300 (lun, 05 oct 2009) $
|     $Author: ritesh $
|
|
|----------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Add Meeting Attendees"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.calendar.MeetingBean,
            net.project.calendar.UninvitedTeamMemberList,
            net.project.calendar.AttendeeBean,
            net.project.security.SecurityProvider,
            net.project.security.User,
            net.project.security.SessionManager,
            net.project.space.ISpaceTypes"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />

<jsp:useBean id="attendee" class="net.project.calendar.AttendeeBean" scope="session"/>
<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session"/>
<jsp:useBean id="uninvitedTeamMembers" class="net.project.calendar.UninvitedTeamMemberList" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>                                                                                                     

<%
    //We need the attendee object to get the list of status codes, we can't have
    //any preselected though
    attendee.clear();

    //Load list of uninvited team members
    uninvitedTeamMembers.clear();
    uninvitedTeamMembers.setEvent(meeting);
    uninvitedTeamMembers.load();

    //Get the height of the team member listbox
    int listBoxHeight = uninvitedTeamMembers.size() + 1;
    if (listBoxHeight < 3) {
        listBoxHeight = 3;
    } else if (listBoxHeight > 10) {
        listBoxHeight = 10;
    }

    java.util.Hashtable nav = (java.util.Hashtable)request.getSession().getValue("PageNavigator");
    String myReturnTo = (String)nav.get("MeetingItem_returnto");

    // Decided which actionbar buttons to show
	
	String showBackButton=(myReturnTo != null ? "true" : "false");
	
    String refLink= (String)nav.get("Meeting_cancel_to");	

	String showCancelButton=(refLink != null ? "true" : "false");
	
%>

<security:verifyAccess action="modify"
                       module="<%=net.project.base.Module.CALENDAR%>"
                       objectID="<%=meeting.getID()%>"/>

<html>
<head>
<META http-equiv="expires" content="0">
<title><%=PropertyProvider.get("prm.calendar.meeting.addattendeespage.title")%></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
    var theForm;
    
    // Variable declarations  for blog-it 
	var blogItFor ='meetingFor'+'<%=user.getCurrentSpace().getSpaceType().getName()%>';
	var spaceId = <%= user.getCurrentSpace().getID()%>;
	var moduleId = <%= net.project.base.Module.CALENDAR%>;
	var objectTypeNameFor = 'Meeting';
	var objectId = '<%=meeting.getID()%>';
    var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";
    
    function setup() {
        theForm = self.document.forms[0];
    }

    function submit() {
	    if (theForm.personID.selectedIndex == -1 ||(theForm.personID.selectedIndex == 0 && theForm.personID.options[0].value == '')){
			extAlert('<display:get name="prm.resource.global.exterroralert.title"/>', '<display:get name="prm.calendar.meeting.addattendees.selectoneattendee.message" />', Ext.MessageBox.ERROR);
		} else {
	        theAction("submit");
	        theForm.submit();
	    }
    }

    function back() {
        var theLocation= "<%= myReturnTo %>";
        self.location = theLocation;
    }
    
	
    function cancel() {
    	self.location = "<%= refLink %>";
    }

    
    function reset() {
        theForm.reset();
    }

    function help() {
        var helplocation = "<%=SessionManager.getJSPRootURL() %>/help/Help.jsp?page=calendar&section=attendee_edit";
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
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/calendar/MeetingAddAttendeesProcessing.jsp">
<input type="hidden" name="theAction" />
<input type="hidden" name="module" value="<%= net.project.base.Module.CALENDAR %>">
<input type="hidden" name="action" value="<%= net.project.security.Action.MODIFY %>">
<input type="hidden" name="id" value="<jsp:getProperty name="meeting" property="ID" />">
<input type="hidden" name="refLink" value="<%= refLink %>" >

<table border="0" cellpadding="0" cellspacing="0" width="97%">
    <%-- Meeting Information Channel Header --%>
    <tr class="channelHeader">
        <td width="15"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
        <td colspan="2" class="channelHeader"><%=PropertyProvider.get("prm.calendar.meeting.addattendees.channel.new.title")%></td>
        <td width="15"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
    </tr>
</table>

<% if (uninvitedTeamMembers.size() > 0) { %>
<table border="0" cellpadding="3" cellspacing="0" width="97%">
    <tr>
        <td valign="top" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.meeting.addattendees.new.label")%></td>
        <td class="tableContent">
            <select name="personID" size="<%=listBoxHeight%>" multiple>
                <%-- Render the select list of participants --%>
                <pnet-xml:transform name='uninvitedTeamMembers' scope='session' stylesheet='/base/xsl/option-list.xsl'/>
            </select>
        </td>
    </tr>
    <tr>
        <td class="fieldRequired"><%=PropertyProvider.get("prm.calendar.meeting.addattendees.status.label")%></td>
        <td class="tableContent">
            <select name="statusID">
                <jsp:getProperty name="attendee" property="statusOptionList"/>
            </select>
        </td>
    </tr>
    <tr>
        <td class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.meeting.addattendees.comment.label")%></td>
        <td class="tableContent">
            <input type="text" name="comment" size="30" maxlength="80"/>
        </td>
    </tr>
</table>
<tb:toolbar style="action" showLabels="true">
                <tb:band name="action">
                    <tb:button type="cancel" show="<%=showCancelButton%>" />
					<tb:button type="back" show="<%=showBackButton%>" />
                    
                    <tb:button type="submit" />
                </tb:band>
</tb:toolbar>
<% } else { %>
<table border="0" cellpadding="3" cellspacing="0" width="97%">
    <tr>
        <td>
        	<%
        		if (meeting.getSpaceType().equals(ISpaceTypes.BUSINESS_SPACE)) {
        	%>
        			<%=PropertyProvider.get("prm.calendar.meeting.addattendees.nomembers.business.message")%>
        	<%
        		} else if (meeting.getSpaceType().equals(ISpaceTypes.PROJECT_SPACE)) {
        	%>
        			<%=PropertyProvider.get("prm.calendar.meeting.addattendees.nomembers.message")%>
        	<% 
        		} 
        	%>
        </td>
    </tr>
</table>
<tb:toolbar style="action" showLabels="true">
                <tb:band name="action">
                    <tb:button type="cancel" show="<%=showCancelButton%>" />
					<tb:button type="back" show="<%=showBackButton%>" />
                </tb:band>
</tb:toolbar>
<% } %>
</form>
<%@ include file="/help/include_outside/footer.jsp"%>
<template:getSpaceJS />
</body>
</html>



    