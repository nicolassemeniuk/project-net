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
    info="Task View" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.gui.html.HTMLOptionList,
            net.project.security.Action,
            net.project.util.DateFormat"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="refLink" class="java.lang.String" scope="request"/>
<jsp:useBean id="refLinkEncoded" class="java.lang.String" scope="request"/>
<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="request" />
<jsp:useBean id="user" type="net.project.security.User" scope="session"/>
<jsp:useBean id="schedule" type="net.project.schedule.Schedule" scope="session"/>
<%
    DateFormat dateFormat = user.getDateFormatter();
    String baseURL = SessionManager.getJSPRootURL();
    refLink = (refLink != null && refLink.length() > 0 ? refLink : "/workplan/taskview?module=" + net.project.base.Module.SCHEDULE);
    refLinkEncoded = refLinkEncoded != null && refLinkEncoded.length() > 0 ? refLinkEncoded : java.net.URLEncoder.encode(refLink, SessionManager.getCharacterEncoding());
	
    // for redirection to time summary page through business space when this page called from time summary report 
	if(refLink != null && refLink.contains("report/TimeSummary")){ 
		refLink = "/business/Main.jsp?id="+request.getParameter("businessId")+ 
				"&page="+  java.net.URLEncoder.encode(SessionManager.getJSPRootURL() +"/business/report/TimeSummary?module=" 
				+ Module.TIME_SUMMARY_REPORT, SessionManager.getCharacterEncoding());
	}
%>

<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:import type="css" src="/styles/schedule.css" />
<template:getSpaceCSS />
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';   
	var moduleId = <%=Module.FORM%>;
	var spaceId='<%=user.getCurrentSpace().getID()%>';
	var blogItFor = 'taskView';
	var objectType = '<%=scheduleEntry.getTaskType()%>';
	var taskId = '<%=scheduleEntry.getID()%>';
	var objectName = '<%=scheduleEntry.getName().replaceAll("'", "&acute;")%>';
	var needToLoadBlogEntries = true;
	
function setup() {
	load_menu('<%=SessionManager.getUser().getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() {
	self.document.location = JSPRootURL +"<%=refLink%>";
}

function back() {
	self.document.location = JSPRootURL +"<%=refLink%>";
}

function modify() {
   // Task Edit security handled within TaskEdit page
   var theLocation="<%=baseURL%>/servlet/ScheduleController/TaskEdit?action=<%= net.project.security.Action.VIEW %>"
                  +"&module=<%= net.project.base.Module.SCHEDULE %>"
                  +"&id=<%=scheduleEntry.getID()%>"
				  + '<%="&refLink=" + java.net.URLEncoder.encode("/servlet/ScheduleController/TaskView?module=" + net.project.base.Module.SCHEDULE + "&action=" + net.project.security.Action.VIEW + "&id=" + scheduleEntry.getID() + "&refLink=" + refLinkEncoded, SessionManager.getCharacterEncoding())%>';
   self.location = theLocation;
}

function reset() {
	var theLocation='<%=baseURL + "/servlet/ScheduleController/TaskView?module=" + net.project.base.Module.SCHEDULE + "&action=" + net.project.security.Action.VIEW + "&id=" + scheduleEntry.getID()%>&refLink=<%=refLinkEncoded%>';
	self.location = theLocation;
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=schedule_main&section=task_view";
	openwin_help(helplocation);
}

function tabClick(src) {
   theForm.nextPage.value= src + "&module=<%=Module.SCHEDULE%>&id=<%=scheduleEntry.getID()%>&refLink=<%=refLinkEncoded %>";
   submit();
}

function submit() {
   theAction("submit");
   theForm.submit();
}

</script>
</head>
<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />

<template:getSpaceNavBar space="project"/>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.schedule.main.module.history")%>'
					jspPage='<%=baseURL + "/workplan/taskview"%>'
					queryString='<%="module="+net.project.base.Module.SCHEDULE%>' />
			<history:page display="<%=scheduleEntry.getNameMaxLength40()%>"
					jspPage='<%=baseURL + "/servlet/ScheduleController/TaskView"%>'
					queryString='<%="module=" + net.project.base.Module.SCHEDULE + "&action=" + net.project.security.Action.VIEW + "&id=" + scheduleEntry.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.schedule.taskview.modify.link")%>' function="javascript:modify();"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action='<%=SessionManager.getJSPRootURL() + "/schedule/TaskViewProcessing.jsp"%>'>
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=Module.SCHEDULE%>"/>
<input type="hidden" name="action" value="<%=Action.MODIFY%>"/>
<input type="hidden" name="id" value="<%=scheduleEntry.getID()%>">
<input type="hidden" name="refLink" value="<%=refLink%>" />
<!--
<input type="hidden" name="nextPage" value="/servlet/ScheduleController/TaskView?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>&refLink=<%=refLinkEncoded %>"/>
-->
<input type="hidden" name="nextPage" value="<%=refLink %>"/>

<div align="center">
<table border="0" align="left" cellpadding="0" cellspacing="0" width="97%">
	<tr class="channelHeader">
		<td width="1%" class="channelHeader"><img src="<%=baseURL%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td class="channelHeader" colspan="4"><nobr><%=PropertyProvider.get("prm.schedule.taskview.channel.viewtask.title")%></nobr></td>
		<td align=right width="1%" class="channelHeader"><img src="<%=baseURL%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>

    <jsp:include page="include/taskProperties.jsp" flush="true" />

	<tr>
		<td colspan="6">
<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.status.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView?action=1');" selected="true" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.resources.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Assignments?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.materials.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Material?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.dependencies.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Dependencies?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.advanced.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Advanced?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.history.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/History?action=1');" />
	<display:if name="@prm.blog.isenabled">
		<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.blogs.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/History?action=1&showBlog=1');" />
	</display:if>
</tab:tabStrip>
		</td>
	</tr>

    <tr><td colspan="6">&nbsp;</td></tr>

    <tr align="left" valign="top">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.phase.label")%>&nbsp;</td>
		<td colspan="3" class="tableContent">
			<c:out value="${scheduleEntry.phaseName}"/>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="top">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.subtaskof.label")%>&nbsp;</td>
		<td nowrap colspan="3" class="tableContent">
			<c:out value="${scheduleEntry.parentTaskNameMaxLength40}"/>
		</td>
		<td>&nbsp;</td>
	</tr>
    <tr>
        <td colspan="6">&nbsp;</td>
    </tr>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.baselinestartdate.label")%>&nbsp;</td>
		<td class="tableContent">
			<%=dateFormat.formatDateTime(scheduleEntry.getBaselineStart())%>&nbsp;
		</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.baselinestartvariance.label")%>&nbsp;</td>

        <td class="tableContent">
			<%=scheduleEntry.getStartDateVariance(schedule.getWorkingTimeCalendarProvider())%>
			&nbsp;
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.baselinefinishdate.label")%>&nbsp;</td>
		<td nowrap class="tableContent">
			<%=dateFormat.formatDateTime(scheduleEntry.getBaselineEnd())%>
			&nbsp;
		</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.baselinefinishvariance.label")%>&nbsp;</td>
		<td nowrap class="tableContent">
			<%=scheduleEntry.getEndDateVariance(schedule.getWorkingTimeCalendarProvider())%>
			&nbsp;
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.baselinework.label")%>
		</td>
		<td class="tableContent">
            <%=scheduleEntry.getBaselineWork() != null ? scheduleEntry.getBaselineWork().toShortString(0,2) : ""%>
			&nbsp;
		</td>
        <td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.workvariance.label")%>&nbsp;</td>
        <td class="tableContent">
            <%=scheduleEntry.getWorkVariance() != null ? scheduleEntry.getWorkVariance().toShortString(0,2) : ""%>
            &nbsp;
        </td>
		<td>&nbsp;</td>
	</tr>
    <tr align="left" valign="middle">
        <td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.baselineduration.label")%> </td>
		<td class="tableContent">
            <%=scheduleEntry.getBaselineDuration() != null ? scheduleEntry.getBaselineDuration().toShortString(0,2) : ""%>
			&nbsp;
		</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.durationvariance.label")%>&nbsp;</td>
		<td nowrap class="tableContent">
            <%=scheduleEntry.getDurationVariance().toShortString(0,2)%>
			&nbsp;
		</td>
        <td>&nbsp;</td>
    </tr>
</table>

</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="back"/>
		<tb:button type="submit" enable="false" show="false"/>
	</tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceJS space="project" />
<template:import type="javascript" src="/src/workCapture.js" />
</body>
</html>
