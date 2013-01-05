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
|   Displays a form for editing a meeting
|
|   Request Parameters:
|       id      id of the meeting  to edit
|               if not present, display an empty meeting form
|
|   Author: Adam Klatzkin
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Edit Meeting"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.calendar.MeetingBean,
            net.project.calendar.CalendarBean,
            net.project.resource.IFacility,
            net.project.resource.Person,
            net.project.security.SecurityProvider,
            net.project.security.User,
            net.project.security.SessionManager,
            net.project.space.Space,
            net.project.xml.XMLFormatter,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="calendar" class="net.project.calendar.CalendarBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>

<%
	String id = securityProvider.getCheckedObjectID();
	int action = securityProvider.getCheckedActionID();
	if (id.length() == 0) {
%>
		<security:verifyAccess action="create"
							   module="<%=net.project.base.Module.CALENDAR%>" />
<%	} else { %>
		<security:verifyAccess action="modify"
							   module="<%=net.project.base.Module.CALENDAR%>"
							   objectID="<%=id%>" />
<%	} %>
<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />
<%
    String pageName = null;
    String rDate = null;

    if (Validator.isBlankOrNull(request.getParameter("isShowingError"))) {
        // reinitialize the meeting object
        meeting.reset();
        meeting.setCalendar(calendar);

        // a 'DisplayDate' parameter is used to set a default start date
        // for the meeting
        rDate = request.getParameter(CalendarBean.PARAM_date);
        if (rDate != null) {

            java.util.Date date = new java.util.Date();
            net.project.util.DateFormat formatter = user.getDateFormatter();

            date = formatter.parseDateString(rDate, "MMddyyyy");
            meeting.setDate(formatter.formatDate (date));
        }

        // If 'id' parameter is set we edit an existing meeting.
        if (id.length() > 0) {
            meeting.setID(id);
            meeting.load();
            pageName = meeting.getName();
            }
        else
            pageName = "Meeting";
    }

    // Navigation through the calendar is complex with many possible paths so we will use
    // a hashtable stored in the session to maintain navigation state
    String myPath =  SessionManager.getJSPRootURL() + "/calendar/MeetingEdit.jsp" + "?action=" + net.project.security.Action.MODIFY
                                            + "&module=" + net.project.base.Module.CALENDAR;
    java.util.Hashtable nav = (java.util.Hashtable)session.getAttribute("PageNavigator");
    String myReturnTo = (String)nav.get("MeetingEdit_returnto");
    // Decide whether to show cancel button on Action Bar
    String showCancelButton = (myReturnTo != null ? "true" : "false");

    // There are two modes for data entry, wizard and regular.
    // Wizard is used for creation of new meetings.
    // If the 'wizard' parameter is set, register this by adding it to the navigation object
    boolean isWizard = (request.getParameter("wizard") != null) ? true : false;
    if (isWizard) {
        nav.put("wizard", "true");
        myPath += "&wizard=true";
    }
    else
        nav.remove("wizard");

    String refLink = myReturnTo;

    nav.put("Meeting_cancel_to",refLink);
    nav.put("MeetingList_returnto", myPath);
%>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/trim.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	
	//variables for meeting blog-it
	var blogItFor ='meetingFor'+'<%=user.getCurrentSpace().getSpaceType().getName()%>';
	var spaceId = <%= user.getCurrentSpace().getID()%>;
	var moduleId = <%= net.project.base.Module.CALENDAR%>;
	var objectTypeNameFor = 'Meeting';
	var objectId =  '<%=id%>';
    var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

function setup() {
     load_menu('<%=user.getCurrentSpace().getID()%>');
     isLoaded = true;
     theForm = self.document.forms[0];
}

function help()
{
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=calendar&section=meeting_edit";
	openwin_help(helplocation);
}


function cancel() {
	self.location = "<%= myReturnTo %>";
}

function submit () {
	theAction("submit");
	if(validateForm(theForm))
	    theForm.submit();
}

function reset() {
	theForm.reset();
}

function validateForm(frm) {
    if (!checkTextbox(frm.name,'<%=PropertyProvider.get("prm.calendar.meetingedit.namerequired.message")%>')) return false;
    if(theForm.FacilityDescription.value.length > 500){
    	extAlert("Error", '<display:get name="prm.calendar.meetingedit.descriptionlength.message" />' , Ext.MessageBox.ERROR);
    	return false;
    }
    return true;
}

</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.calendar.mainpage.title">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.calendar.module.history")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/calendar/Main.jsp"%>'
					queryString='<%="module="+net.project.base.Module.CALENDAR%>' />
			<history:page display="<%=pageName%>" />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="MeetingEditProcessing.jsp">
<%
if (rDate != null) {
%>
    <%= calendar.getStateAsFormBody() %>
<%
}
%>

<input type="hidden" name="theAction">

<%-- Avinash: 																				--%>
<input type="hidden" name="id" value=<jsp:getProperty name="meeting" property="meetingID" /> >
<%-- Avinash: 																				--%>	

<input type="hidden" name="action" value="<%= action %>" >
<input type="hidden" name="module" value="<%= net.project.base.Module.CALENDAR %>" >
<% if (isWizard) { %>
<input type="hidden" name="wizard" value="true">
<% } %>

<div align="left">
<table border="0" cellpadding="0" cellspacing="0"  width="97%">
	<tbody>
<tr class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" colspan="4"><nobr><%=PropertyProvider.get("prm.calendar.meetingedit.channel.meeting.title")%>&nbsp;&nbsp; <%=PropertyProvider.get("prm.calendar.meetingedit.channel.required.message")%></nobr></td>
	<td align=right width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr align="left" valign="top">
	<td>&nbsp;</td>
    <td colspan="4" class="warnText">
    <%
        if (errorReporter.errorsFound()) {
            XMLFormatter formatter = new XMLFormatter();
            formatter.setXML(errorReporter.getXML());
            formatter.setStylesheet("/base/xsl/error-report.xsl");
            out.println(formatter.getPresentation());
            //Clear out the error reporter now that we are done with it
            errorReporter.clear();
        }
    %>
    </td>
    <td>&nbsp;</td>
</tr>
<tr>
	<td nowrap align="left" colspan="2" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.meetingedit.name.label")%></td>
	<td nowrap colspan="4">
	<input type="text" name="name" value='<c:out value="${meeting.name}" />' size="40" maxlength="80">
</td>
</tr>

<tr>
	<td nowrap align="left" colspan="2" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.meetingedit.host.label")%></th>
	<td nowrap colspan="4">
	<%
	Person oHost = meeting.getHost();
	%>
	<select name="hostID"><%= user.getCurrentSpace().getRoster().getSelectionList(oHost != null ? oHost.getID() : user.getID()) %></select>
	</td>
</tr>
<tr>
	<td nowrap align="left" colspan="2" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.meetingedit.date.label")%></td>
	<td nowrap colspan="4">
		<input type="text" name="dateString" size="10" maxlength="10" value='<c:out value="${meeting.dateString}" />'>
		<util:insertCalendarPopup fieldName="dateString"/>
	</td>
</tr>
<tr>
	<td nowrap align="left" colspan="2" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.meetingedit.starttime.label")%></td>
	<td nowrap colspan="4">
        <input:time name="start" time="<%=meeting.getStartTime()%>" minuteStyle="calendar" />
    </td>
</tr>

<tr>
	<td nowrap align="left" colspan="2" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.meetingedit.endtime.label")%></td>
	<td nowrap colspan="4">
        <input:time name="end" time="<%=meeting.getEndTime()%>" minuteStyle="calendar" />
	</td>
</tr>

<tr>
	<td nowrap align="left" colspan="2" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.meetingedit.facilitytype.label")%></td>
	<td nowrap colspan="4">
		<select  name="FacilityType" ><jsp:getProperty name="meeting" property="facilityList"/></select>
		<display:if name="prm.global.calendar.meeting.facility.type.webex.isenabled">
     <% if (PropertyProvider.getBoolean("prm.global.calendar.meeting.facility.type.webex.sslenabled")) {%>
	        <select name="SecurityType">
	            <option value="1"><%=PropertyProvider.get("prm.calendar.meetingedit.option.secure.name")%></option>
	            <option value="0" SELECTED><%=PropertyProvider.get("prm.calendar.meetingedit.option.unsecure.name")%></option>
	        </select>
	        <%=PropertyProvider.get("prm.calendar.meetingedit.secure.instruction")%>
     <% } else { %>
            <input type="hidden" name="SecurityType" value="0"/>
     <% } %>
		</display:if>
	</td>
</tr>

<tr bgcolor="#FFFFFF">
	<td nowrap align="left" colspan="2" class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.meetingedit.facilitydescription.label")%></td>
	<td nowrap colspan="4">
	<%
	IFacility ofacility = meeting.getFacility();
	String facDesc = ofacility.getDescription();
	%>
		<textarea wrap="VIRTUAL" cols="50" rows="2" name="FacilityDescription"><%=facDesc != null ? facDesc : ""%></textarea>
	</td>
</tr>
<input TYPE=hidden name="frequencyTypeId" value="10">
<%--
			<tr align="left" bgcolor="#FFFFFF">
				<th nowrap valign="top" align="left" width="17%">Frequency:</th>
				<td colspan="3" width="83%">
					<select name="FrequencyTypeId"><jsp:getProperty name="meeting" property="frequencyList" /></select>
				</td>
			</tr>
--%>
<tr>
	<td nowrap align="left" colspan="6" class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.meetingedit.purpose.label")%><br>
		<textarea name="purpose" wrap="VIRTUAL" cols="60" rows="3"><c:out value="${meeting.purpose}" /></textarea>
	</td>
</tr>
<tr>
	<td nowrap align="left" colspan="6" class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.meetingedit.description.label")%><br>
		<textarea name="description" wrap="VIRTUAL" cols="60" rows="3"><c:out value="${meeting.description}" /></textarea>
	</td>
</tr>
</tbody>
</table>

</div>

<% if (!isWizard)
{
%>
	<table class="fixed" border="0" cellpadding="0" cellspacing="0"  width="100%">
		<tr class="actionBar" >
			<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
			<td class="actionBar" align=right>
			&nbsp;&nbsp;&nbsp;<a href="javascript:reset();" class="channelNoUnderline" ><%=PropertyProvider.get("prm.calendar.meetingedit.reset.button.label")%>&nbsp;<img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-reset_off.gif" width=27 height=27 alt="" border=0 align="absmiddle"></a>
			&nbsp;&nbsp;&nbsp;<a href="MeetingAgendaList.jsp?module=<%= net.project.base.Module.CALENDAR %>&id=<jsp:getProperty name="meeting" property="ID" />" class="channelNoUnderline" ><%=PropertyProvider.get("prm.calendar.meetingedit.agenda.button.label")%>&nbsp;<img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-jump_off.gif" width=27 height=27 alt="" border=0 align="absmiddle"></a>
			&nbsp;&nbsp;&nbsp;<a href="MeetingAttendeeList.jsp?module=<%= net.project.base.Module.CALENDAR %>&id=<jsp:getProperty name="meeting" property="ID" />" class="channelNoUnderline"><%=PropertyProvider.get("prm.calendar.meetingedit.attendees.button.label")%>&nbsp;<img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-jump_off.gif" width=27 height=27 alt="" border=0 align="absmiddle"></a>
			&nbsp;&nbsp;&nbsp;<a href="javascript:submit();" class="channelNoUnderline" ><%=PropertyProvider.get("prm.calendar.meetingedit.submit.button.label")%>&nbsp;<img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-submit_off.gif" width=27 height=27 alt="" border=0 align="absmiddle"></a></nobr></td>
			<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
		</tr>
	</table>
</div>
	
<% }

if (isWizard)
{
%>
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" show="<%=showCancelButton%>" />
		
		<tb:button type="next" function="javascript:submit();" />
	</tb:band>
</tb:toolbar>
<%
}
%>

</div>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
