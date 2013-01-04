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
|     $Author: avinash $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Schedule Working Times"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            java.util.Date,
            java.util.List,
            net.project.calendar.PnCalendar,
            java.util.Iterator,
            net.project.schedule.IScheduleEntry,
            net.project.xml.XMLFormatter,
            net.project.base.Module,
            net.project.security.Action,
            net.project.calendar.workingtime.WorkingTimeCalendarHelper,
            net.project.gui.html.HTMLOptionList"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="calendarHelper" type="net.project.calendar.workingtime.WorkingTimeCalendarHelper" scope="request" />

<security:verifyAccess action="view" module="<%=Module.SCHEDULE%>" />


<!DOCTYPE html public "-//W3C//DTD html 4.0 Transitional//EN">
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript" type="text/javascript">
    var theForm;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';

function setup() {
    theForm = self.document.forms[0];
}

function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=schedule_workingtimeview";
	openwin_help(helplocation);
}

function cancel() {
    self.document.location = JSPRootURL + '/servlet/ScheduleController/WorkingTime/List?module=<%=Module.SCHEDULE%>';
}

function reset() {
    theAction("refresh");
    theForm.submit();
}

function submit() {
    theForm.submit();
}

function changeCalendarID() {
    reset();
}
</script>

</head>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page level="2" display='<%=PropertyProvider.get("prm.schedule.workingtime.view.pagetitle")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/WorkingTime/View" %>'
					queryString='<%="module="+Module.SCHEDULE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/WorkingTime/View">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.VIEW%>">
    <input type="hidden" name="theAction">

    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
            <td nowrap class="channelHeader" align=left colspan="2"><display:get name="prm.schedule.workingtime.view.pagetitle" /></td>
            <td width="1%" align="right" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
        </tr>
        <tr>
            <td>&nbsp;</td>

            <table width="100%">

                <tr>
                    <td class="tableHeader"><display:get name="prm.schedule.workingtime.view.calendar.label" /></td>
                    <td class="tableContent">
                        <select name="calendarID" onchange="changeCalendarID();">
                        <%=HTMLOptionList.makeHtmlOptionList(calendarHelper.getWorkingTimeCalendarOptions(), calendarHelper.getID())%>
                        </select>
                    </td>
                </tr>

                <tr class="tableContent">
                    <td>&nbsp;</td>
                    <td>
                        <%  if (!calendarHelper.isBaseCalendar()) { %>
                            <display:get name="prm.schedule.workingtime.view.basedon">
                                <display:param value="<%=calendarHelper.getParentCalendarDisplayName()%>" />
                            </display:get>
                        <% } else { %>
                            &nbsp;
                        <% } %>
                    </td>
                </tr>

                <tr class="tableHeader">
                    <td colspan="2">
                        <display:get name="prm.schedule.workingtime.view.dayofweek.title" />
                    </td>
                </tr>

<%
            for (Iterator it = calendarHelper.getDayOfWeekIterator(); it.hasNext();) {
                WorkingTimeCalendarHelper.DayOfWeekHelper nextEntry = (WorkingTimeCalendarHelper.DayOfWeekHelper) it.next();
%>
                <tr class="tableContent">
                    <td><%=nextEntry.getDayOfWeekFormatted()%></td>
                    <td>
                        <%=PropertyProvider.get((nextEntry.isWorkingDay() ? "prm.schedule.workingtime.view.workingtime" : "prm.schedule.workingtime.view.nonworkingtime"))%>
                    </td>
                </tr>
<%          } %>
                <tr><td colspan="2">&nbsp;</td></tr>

                <tr class="tableHeader">
                    <td colspan="2">
                        <display:get name="prm.schedule.workingtime.view.date.title" />
                    </td>
                </tr>

<%
            for (Iterator it = calendarHelper.getDateIterator(); it.hasNext();) {
                WorkingTimeCalendarHelper.DateHelper nextEntry = (WorkingTimeCalendarHelper.DateHelper) it.next();
%>
                <tr class="tableContent">
                    <td><%=nextEntry.getDateDisplay()%></td>
                    <td>
                        <%=PropertyProvider.get((nextEntry.isWorkingDay() ? "prm.schedule.workingtime.view.workingtime" : "prm.schedule.workingtime.view.nonworkingtime"))%>
                    </td>
                </tr>
<%          } %>


            </table>

            <td>&nbsp;</td>
        </tr>
    </table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action">
    	<tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
