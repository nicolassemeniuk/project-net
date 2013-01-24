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
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="scheduleImporter" class="net.project.schedule.importer.XMLImporter" type="net.project.schedule.importer.IScheduleImporter" scope="session"/>

<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript" type="text/javascript">

    var theForm;

    function setup() {
    	theForm = self.document.forms["main"];
    }
    function next() {
        theAction("next");
        theForm.submit();
    }

    function cancel() {
    	theLocation="<%=SessionManager.getJSPRootURL()%>/workplan/taskview?module=<%= net.project.base.Module.SCHEDULE %>";
    	self.location = theLocation;
    }

    function clickImportTasks() {
        theForm.importAssignments.disabled = !theForm.importTasks.checked;
    }

    function clickImportWorkingTimeCalendars() {
        var disabledString = (theForm.importWorkingTimeCalendars.checked ? "false" : "true");
        document.getElementById("defaultWorkingTimeCalendarUpdate").disabled = !theForm.importWorkingTimeCalendars.checked;
        document.getElementById("defaultWorkingTimeCalendarAdd").disabled = !theForm.importWorkingTimeCalendars.checked;
        theForm.importResourceWorkingTimeCalendars.disabled = !theForm.importWorkingTimeCalendars.checked;
    }

</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page displayToken='prm.schedule.import.xml.splitedtaskwarning.pagetitle'
					jspPage='<%=SessionManager.getJSPRootURL() + "/schedule/importer/SplitedTaskWarning.jsp" %>'
					queryString='<%="splited=true"%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>
<br>
<div id='content'>
<form name="main" method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/ImportController/MapResources">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.MODIFY%>">    
    <input type="hidden" name="MSProjectID" value="<%=request.getParameter("MSProjectID")%>">    
	<input type="hidden" name="importTasks" value="<%=request.getParameter("importTasks")%>">	
	<input type="hidden" name="importAssignments" value="<%=request.getParameter("importAssignments")%>">	
	<input type="hidden" name="importStartAndEndDates" value="<%=request.getParameter("importStartAndEndDates")%>">	
	<input type="hidden" name="importWorkingTimeCalendars" value="<%=request.getParameter("importWorkingTimeCalendars")%>">
	<input type="hidden" name="importResourceWorkingTimeCalendars" value="<%=request.getParameter("importResourceWorkingTimeCalendars")%>">
	<input type="hidden" name="importStartAndEndDates" value="<%=request.getParameter("importStartAndEndDates")%>">
	<input type="hidden" name="defaultWorkingTimeCalendar" value="<%=request.getParameter("defaultWorkingTimeCalendar")%>">
	<input type="hidden" name="splited" value="true">
    <input type="hidden" name="theAction">

<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr class="actionBar">
    <td width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td align="left" class="channelHeader" nowrap>&nbsp;<display:get name="prm.schedule.import.xml.splitedtaskwarning.pagetitle" /></td>
    <td width="1%" align=right class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr>
	<td>&nbsp;</td>
    <td class="instructions"><br><display:get name="prm.schedule.import.xml.splitedtaskwarning.warningtext" /></td>
    <td>&nbsp;</td>
</tr>
</table>

</form>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="next" />
	</tb:band>
</tb:toolbar>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
