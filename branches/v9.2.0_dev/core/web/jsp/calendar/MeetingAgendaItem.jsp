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
|   Displays an editable MeetingAgendaItem
|   
|   Request Parameters:
|		id			meeting id
|       agendaid	id of the meeting agenda item to edit
|					if not present, display an empty meeting agenda item
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Create/Edit Meeting Agenda Item" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.calendar.AgendaBean,
		    net.project.calendar.MeetingBean,
		    net.project.security.SecurityProvider,
		    net.project.security.User,
		    net.project.security.SessionManager,
		    net.project.space.Space" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="agenda" class="net.project.calendar.AgendaBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>                                                                                                     

<%
	String agendaid = request.getParameter("agendaid");
	if (agendaid == null) {
%>
		<security:verifyAccess action="modify"
							   module="<%=net.project.base.Module.CALENDAR%>" 
							   objectID="<%=meeting.getID()%>" /> 
<% } else { %>
		<security:verifyAccess action="view"
							   module="<%=net.project.base.Module.CALENDAR%>" 
							   objectID="<%=meeting.getID()%>" /> 
<% } %>
 
<%
agenda.reset();
agenda.setMeeting(meeting);

if (request.getParameter("agendaid") != null)
    {
    agenda.setId(request.getParameter("agendaid"));
    agenda.load();
    }

java.util.Hashtable nav = (java.util.Hashtable)request.getSession().getValue("PageNavigator");
String myReturnTo = (String)nav.get("MeetingItem_returnto");
boolean editable = false;
String sEditable = (String)nav.get("editable");
if ((sEditable != null) && (sEditable.equals(meeting.getID()))) 
    editable = true;
	
// Decide which action bar buttons to display
String showCancelButton = "false";
String showSubmitButton = "false";
String showBackButton = "false";
if (myReturnTo != null)
	showBackButton = "true";
	
if (editable)
	showSubmitButton = "true";
	
	String refLink= (String)nav.get("Meeting_cancel_to");	
		
if (refLink != null)
	showCancelButton = "true";
		
%>
<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0"> 
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/checkLength.js" />
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
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=calendar&section=agenda_edit";
	openwin_help(helplocation);
}


    function back() {
        var theLocation= "<%= myReturnTo %>";
        self.location = theLocation;
    }
    
	
    function cancel() {
    	self.location = "<%= refLink%>";
    }

<%
    if (editable)
      {
%>
function submit () 
    {
    theAction("submit");
    if (!checkTextbox(theForm.name,'<%=PropertyProvider.get("prm.calendar.meeting.agendaitem.required.message")%>')) return;
    if (!checkMaxLength(theForm.description,500,'<%=PropertyProvider.get("prm.calendar.meeting.itemdescription.maxlength")%>')) return;
  		 theForm.submit();
    }


<%
      }
%>       
    
function reset() 
    {
    theForm.reset();
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

<form method="post" action="MeetingAgendaItemProcessing.jsp">
    <input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%= net.project.base.Module.CALENDAR %>">
    <input type="hidden" name="action" value="<%= net.project.security.Action.MODIFY %>">
    <input type="hidden" name="id" value="<jsp:getProperty name="meeting" property="ID" />">

<table border="0" cellpadding="0" cellspacing="0" width="97%">
<tr class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" colspan="4"><nobr><%=PropertyProvider.get("prm.calendar.meeting.agendaitem.channel.agenda.title")%><jsp:getProperty name="meeting" property="name" /></nobr></td>
	<td align=right width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>


<tr align="left" valign="top">
	<td nowrap colspan="2" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.meeting.agendaitem.agenda.label")%>
	</td>
	<td colspan="4" class="tableContent">
	<% if (editable)
	{
	%>
	<input type="text" name="name" value='<c:out value="${agenda.name}" />' size="30" maxlength="80">
	<%
	}
	else
	{
	%>
	<c:out value="$(agenda.name}" />
	<%
	}
	%>

	</td>
</tr>
<tr align="left" valign="top">
	<td nowrap colspan="2" class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.meeting.agendaitem.time.label")%>
	</td>
	<td colspan="4" class="tableContent">
	<% if (editable)
	{
	%>
	<input type="text" name="allotedTime" value='<c:out value="${agenda.allotedTime}" />' size="8" maxlength="20">
	<%
	}
	else
	{
	%>
	<c:out value="${agenda.allotedTime}" />
	<%
	}
	%>                                                													
	</td>
</tr>
<tr align="left" valign="top">
	<td nowrap colspan="2" class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.meeting.agendaitem.owner.label")%>
	</td>
	<td colspan="4" class="tableContent">
	<% if (editable)
	{
	%>
	<select name="ownerId"><jsp:getProperty name="agenda" property="ownerOptionList" />
	</select>
	<%
	}
	else
	{
	%>
	<jsp:getProperty name="agenda" property="owner" />
	<%
	}
	%>                                                  
	</td>
</tr>
<tr align="left"  valign="top">
	<td nowrap colspan="2" class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.meeting.agendaitem.status.label")%></td>
	<td colspan="4" class="tableContent">
	<% if (editable)
	{
	%>
	<select name="statusId"><jsp:getProperty name="agenda" property="agendaStatusOptionList" />
	</select>
	<%
	}
	else
	{
	%> 
    <jsp:getProperty name="agenda" property="status" />
	<%
	}
	%>                                                  
	</td>
</tr>
    <tr align="left"  valign="top">
    <td nowrap colspan="2" class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.meeting.agendaitem.number.label")%></td>	
    <td colspan="4" class="tableContent">
	<% if (editable)
	{
	%>
    <select name="itemSequence"><jsp:getProperty name="agenda" property="sequenceNumberOptionList" />
	</select>
	<%
	}
	else
	{
	%>
	<jsp:getProperty name="agenda" property="itemSequence" />
	<%
	}
	%>       
    </td>                                                  
</tr>
<tr align="left" valign="top">
	<td nowrap colspan="6" class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.meeting.agendaitem.description.label")%><br>
	<% if (editable)
	{
	%>
		<textarea name="description" cols="70" rows="4" wrap="VIRTUAL"><c:out value="${agenda.description}" /></textarea>
	<%
	}
	else
	{
	%>
	<table width="75%" border="1" cellspacing="1" cellpadding="4">
	<tr>
	<td align="left"  valign="top" colspan="4" class="tableContent">
	<c:out value="${agenda.description}" />&nbsp; 
	</td>
	</tr>
	</table>   
	<%
	}
	%>   


	</td>
</tr>
<tr align="left" valign="top">
	<td nowrap colspan="6" class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.meeting.agendaitem.minutes.label")%><br>
	<% if (editable)
	{
	%>
	<textarea name="meetingNotes" cols="70" rows="10" wrap="VIRTUAL"><c:out value="${agenda.meetingNotes}" /></textarea>
	<%
	}
	else
	{
	%>
	<table width="75%" border="1" cellspacing="1" cellpadding="4">
	<tr>
	<td align="left"  valign="top" colspan="4" class="tableContent">
	<c:out value="${agenda.meetingNotes}" />&nbsp; 
	</td>
	</tr>
	</table>   
	<%
	}
	%>  

	</td>
</tr>
<tr>
    <td colspan="6">&nbsp;</td>
</tr>

</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		
		<tb:button type="cancel" show="<%=showCancelButton%>" />
		<tb:button type="back" show="<%=showBackButton%>" />
		<tb:button type="submit" show="<%=showSubmitButton%>" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
