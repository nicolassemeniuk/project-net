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
|   Displays a form for editing an event
|   
|   Request Parameters:
|       id      id of the event  to edit
|               if not present, display an empty event form
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Edit Event" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.calendar.CalendarBean,
		    net.project.calendar.CalendarEvent,
			net.project.security.User,
		    net.project.security.SecurityProvider,
		    net.project.security.SessionManager,
		    net.project.resource.IFacility,
		    net.project.space.Space,
            net.project.util.Validator,
            net.project.xml.XMLFormatter"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0"> 
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<jsp:useBean id="user" class="net.project.security.User" scope="session"/>                                                                                                     
<jsp:useBean id="event" class="net.project.calendar.CalendarEvent" scope="session" />
<jsp:useBean id="calendar" class="net.project.calendar.CalendarBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>

<%
	String id = securityProvider.getCheckedObjectID(); 

	if (!Validator.isBlankOrNull(id)) {
%>

<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.CALENDAR%>"
					   objectID="<%=id%>" /> 

<%	} else { %>

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.CALENDAR%>" /> 

<%	} %>

<%
    String rDate = null;
    if (Validator.isBlankOrNull(request.getParameter("isShowingError"))) {
        // reinitialize the event object
        event.reset();
        event.setCalendar(calendar);

        // a 'DisplayDate' parameter is used to set a default start date
        // for the event
        rDate = request.getParameter(CalendarBean.PARAM_date);
        if (rDate != null) {
            java.util.Date date = new java.util.Date();
            net.project.util.DateFormat formatter = user.getDateFormatter();

            date = formatter.parseDateString(rDate, "MMddyyyy");
            event.setDate(formatter.formatDate (date));
        }


        // If 'id' parameter is set we edit an existing event.
        if (id.length() > 0) {
            event.setID(id);
            event.load();
        }
    }

    // Navigation through the calendar is complex with many possible paths so we will use
    // a hashtable stored in the session to maintain navigation state
    java.util.Hashtable nav = (java.util.Hashtable)session.getAttribute("PageNavigator");
    String myReturnTo = (String)nav.get("MeetingEdit_returnto");

%>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/checkLength.js" />

<script language="javascript">
var theForm;
var isLoaded = false;
window.history.forward(-1);	
function setup() {

    load_menu('<%=user.getCurrentSpace().getID()%>');
	isLoaded = true;
	theForm = self.document.forms[0];
   }

function cancel() 
   {
   self.location = "<%= myReturnTo %>";
   }

function submit () 
   {
   theAction("submit");
   if(validateForm(theForm))
       theForm.submit();
   }

function validateForm(frm) {

    if (!checkTextbox(frm.name,'<%=PropertyProvider.get("prm.calendar.eventedit.eventrequired.message")%>')) return false;
    if (!checkTextbox(frm.FacilityDescription,'<%=PropertyProvider.get("prm.calendar.eventedit.facilityrequired.message")%>')) return false;
	if (!checkMaxLength(frm.FacilityDescription,500,'<%=PropertyProvider.get("prm.calendar.eventedit.facilityrequired.maxlength")%>')) return false;
    return true;
}

function help()
{
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=calendar&section=event_edit";
	openwin_help(helplocation);
}


function reset() {
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
			<history:module display='<%=PropertyProvider.get("prm.calendar.module.history")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/calendar/Main.jsp"%>'
					queryString='<%="module="+net.project.base.Module.CALENDAR%>' />
			<history:page display='<%=PropertyProvider.get("prm.calendar.eventedit.module.history")%>'/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="EventEditProcessing.jsp">
<% if (rDate != null) { %>
    <%= calendar.getStateAsFormBody() %>
<% } %>
<input type="hidden" name="theAction">
<% if (id.length() > 0) { %>
<input type="hidden" name="id" value="<jsp:getProperty name="event" property="ID" />">
<input type="hidden" name="action" value="<%= net.project.security.Action.MODIFY %>" >
<% } else { %>
<input type="hidden" name="action" value="<%= net.project.security.Action.CREATE %>" >
<% } %>
<input type="hidden" name="module" value="<%= net.project.base.Module.CALENDAR %>" >
	
<div align="left">
<table border="0" cellpadding="0" cellspacing="0"  width="97%">
	<tbody>
<tr class="channelHeader">
	<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" colspan="4"><nobr><%=PropertyProvider.get("prm.calendar.eventedit.module.history")%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <%=PropertyProvider.get("prm.calendar.eventedit.channel.required.message")%></nobr></td>
	<td align=right width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
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
	<td>&nbsp;</td>
	<td nowrap align="left" width="17%" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.event.name.label")%></td>
	<td nowrap colspan="3" width="83%">
		<input type="text" name="name" value="<c:out value="${event.name}"/>" size="40" maxlength="80">
	</td>
	<td>&nbsp;</td>
</tr>                        
<tr>
	<td>&nbsp;</td>
	<td nowrap align="left" width="17%" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.event.date.label")%></td>
	<td nowrap colspan="3" width="83%">
		<input type="text" name="dateString" size="10" maxlength="10" value="<jsp:getProperty name="event" property="dateString" />">
		<util:insertCalendarPopup fieldName="dateString"/>
	</td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td nowrap align="left" width="17%" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.eventedit.starttime.label")%></td>
	<td nowrap colspan="3" width="83%">
        <input:time name="start" time="<%=event.getStartTime()%>" minuteStyle="calendar" />
	<td>&nbsp;</td>
</tr>
<tr >
	<td>&nbsp;</td>
	<th nowrap align="left" width="17%" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.eventedit.endtime.label")%></th>
	<td nowrap colspan="3" width="83%">
        <input:time name="end" time="<%=event.getEndTime()%>" minuteStyle="calendar" />
	</td>
	<td>&nbsp;</td>
</tr>                      
<input type="hidden" name="FacilityType" value="10">
<tr>
	<td>&nbsp;</td>
	<th nowrap align="left" width="17%" class="fieldRequired"><%=PropertyProvider.get("prm.calendar.event.facilitydescription.label")%></th>
	<td nowrap colspan="3" width="83%">
	<%
	IFacility ofacility = event.getFacility();
	String facDesc = ofacility.getDescription();
	%>
	<textarea wrap="VIRTUAL" cols="50" rows="2" name="FacilityDescription"><%= facDesc %></TEXTAREA>
	</td>
	<td>&nbsp;</td>
</tr>                
<input TYPE=hidden name="frequencyTypeId" value="10">

<tr align="left">
	<td>&nbsp;</td>
	<td valign="top" align="left" colspan="4" class="fieldNonRequired">
		<%=PropertyProvider.get("prm.calendar.event.purpose.label")%>
		<br>
		<textarea name="purpose" wrap="VIRTUAL" cols="50" rows="3"><c:out value="${event.purpose}"/></textarea>
	</td>
	<td>&nbsp;</td>
</tr>
<tr align="left" >
	<td>&nbsp;</td>
	<td valign="top" align="left" colspan="4" class="fieldNonRequired"><%=PropertyProvider.get("prm.calendar.event.description.label")%>
		<br>
		<textarea name="description" wrap="VIRTUAL" cols="50" rows="3"><c:out value="${event.description}"/></textarea>
	</td>
	<td>&nbsp;</td>
</tr>
</table>
</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" />
	</tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

