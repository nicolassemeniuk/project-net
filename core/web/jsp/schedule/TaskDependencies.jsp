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
    import="java.util.Iterator,
            net.project.base.property.PropertyProvider,
            net.project.link.LinkManagerBean,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.schedule.TaskDependency,
            java.net.URLEncoder,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="refLink" class="java.lang.String" scope="request"/>
<jsp:useBean id="refLinkEncoded" class="java.lang.String" scope="request"/>
<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="request" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
    String baseUrl = SessionManager.getJSPRootURL();
    refLink = (refLink != null && refLink.length() > 0 ? refLink : SessionManager.getJSPRootURL()+ "/workplan/taskview?module=" + net.project.base.Module.SCHEDULE);
    refLinkEncoded = refLinkEncoded != null && refLinkEncoded.length() > 0 ? refLinkEncoded : java.net.URLEncoder.encode(refLink, SessionManager.getCharacterEncoding());
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
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
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() {
	self.document.location = JSPRootURL + "/workplan/taskview?module=" + "<%=net.project.base.Module.SCHEDULE%>";
}

function modify() {
   // Task Edit security handled within TaskEdit page
   var theLocation="<%=baseUrl%>/servlet/ScheduleController/TaskEdit?action=<%= net.project.security.Action.VIEW %>"
                  +"&module=<%= net.project.base.Module.SCHEDULE %>"
                  +"&id=<%=scheduleEntry.getID()%>"
				  + '<%="&refLink=" + java.net.URLEncoder.encode("/servlet/ScheduleController/TaskView?module=" + net.project.base.Module.SCHEDULE + "&action=" + net.project.security.Action.VIEW + "&id=" + scheduleEntry.getID() + "&refLink=" + refLinkEncoded, SessionManager.getCharacterEncoding())%>';
   self.location = theLocation;
}

function reset() {
	var theLocation='<%=baseUrl + "/servlet/ScheduleController/TaskView?module=" + net.project.base.Module.SCHEDULE + "&action=" + net.project.security.Action.VIEW + "&id=" + scheduleEntry.getID()%>&refLink=<%=refLinkEncoded%>';
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

function submit () {
	theAction("submit");
	theForm.submit();
}

function modifyLinks() {
	theAction("modifyLinks");
	theForm.submit();
}
</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="<%=scheduleEntry.getNameMaxLength40()%>"
					jspPage='<%=baseUrl + "/servlet/ScheduleController/TaskView/Advanced"%>'
					queryString='<%="module=" + net.project.base.Module.SCHEDULE + "&action=" + net.project.security.Action.MODIFY + "&id=" + scheduleEntry.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.schedule.taskview.modify.link")%>' function="javascript:modify();"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%= SessionManager.getJSPRootURL() %>/schedule/TaskDependencyProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="id" value="<jsp:getProperty name="scheduleEntry" property="ID" />">
    <input type="hidden" name="action" value="<%= request.getParameter("action") %>">
    <input type="hidden" name="module" value="<%= Module.SCHEDULE %>">
	<input type="hidden" name="refLink" value="<%=refLink%>" />
	<input type="hidden" name="nextPage" value=''>
<div align="center">
<table border="0" align="left" cellpadding="0" cellspacing="0" width="97%">
	<tr class="channelHeader">
		<td width="1%" class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td  nowrap class="channelHeader" colspan="4"><nobr><%=PropertyProvider.get("prm.schedule.taskview.channel.viewtask.title")%></nobr></td>
		<td align=right width="1%" class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>

    <jsp:include page="include/taskProperties.jsp" flush="true" />

	<tr>
		<td colspan="6">
<tab:tabStrip>
<!-- Avinash: contextpath gets changed. so removing '/' from href -->
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.status.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView?action=1');"  />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.resources.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Assignments?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.materials.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Material?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.dependencies.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Dependencies?action=1');" selected="true" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.financial.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Financial?action=1');" />	
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.advanced.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Advanced?action=1');"/>
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.history.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/History?action=1');" />
	<display:if name="@prm.blog.isenabled">
		<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.blogs.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/History?action=1&showBlog=1');" />
	</display:if>
<!-- Avinash----------------------------------->	
</tab:tabStrip>
		</td>
	</tr>

    <%-- Header for task dependencies --%>
    <tr><td colspan="6">
    <table border="0" align="left" cellpadding="0" cellspacing="0" width="100%">
	<tr><td colspan="6">&nbsp;</td></tr>
	<tr class="channelHeader">
		<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td nowrap class="channelHeader" colspan="4"><nobr><%=PropertyProvider.get("prm.schedule.taskview.dependencies.channel.dependson.title", new Object[] {scheduleEntry.getNameMaxLength40()})%></nobr></td>
		<td align=right width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>

    <tr align="left" valign="top">
        <td>&nbsp;</td>
        <td class="tableHeader" width="40%"><display:get name="prm.schedule.taskview.dependencies.name.column"/></td>
        <td class="tableHeader" width="15%"><display:get name="prm.schedule.taskview.dependencies.type.column"/></td>
        <td class="tableHeader" width="15%"><display:get name="prm.schedule.taskview.dependencies.lagtime.column"/></td>
        <td class="tableHeader" width="15%"><display:get name="prm.schedule.taskview.dependencies.startdate.column"/></td>
        <td class="tableHeader" width="15%"><display:get name="prm.schedule.taskview.dependencies.enddate.column"/></td>
    </tr>
    <tr align="left" valign="middle">
		<td></td>
        <td colspan="5" class="tableLine" valign="middle"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="2" border="0"/></td>
    </tr>
    <%-- Iterate through existing task dependencies --%>
<%
Iterator it = scheduleEntry.getPredecessors().iterator();

while (it.hasNext()) {
    TaskDependency currentDependency = (TaskDependency)it.next();
%>
	<tr align="left">
		<td>&nbsp;</td>
		<td nowrap class="tableContent"><%=currentDependency.getTaskNameMaxLength40()%></td>
		<td nowrap class="tableContent"><%=currentDependency.getDependencyType().getName()%></td>
		<td nowrap class="tableContent"><%=currentDependency.getLag()%></td>
        <td nowrap class="tableContent"><%=currentDependency.getStartDateString()%></td>
        <td nowrap class="tableContent"><%=currentDependency.getFinishDateString()%></td>
	</tr>
    <tr align="left" valign="middle">
		<td></td>
        <td colspan="5" class="tableLine" valign="middle"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="1" border="0"/></td>
    </tr>
<% } %>
    <%-- Header for task dependencies --%>
	<tr><td colspan="6">&nbsp;</td></tr>
	<tr class="channelHeader">
		<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>

		<td nowrap class="channelHeader" colspan="4"><nobr><%=PropertyProvider.get("prm.schedule.taskview.dependencies.channel.dependants.title", new Object[] {scheduleEntry.getNameMaxLength40()})%></nobr></td>
		<td align=right width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>

    <tr align="left" valign="top">
        <td>&nbsp;</td>
        <td class="tableHeader"><display:get name="prm.schedule.taskview.dependencies.name.column"/></td>
        <td class="tableHeader"><display:get name="prm.schedule.taskview.dependencies.type.column"/></td>
        <td class="tableHeader"><display:get name="prm.schedule.taskview.dependencies.lagtime.column"/></td>
        <td class="tableHeader"><display:get name="prm.schedule.taskview.dependencies.startdate.column"/></td>
        <td class="tableHeader"><display:get name="prm.schedule.taskview.dependencies.enddate.column"/></td>
    </tr>
    <tr align="left" valign="middle">
		<td></td>
        <td colspan="5" class="tableLine" valign="middle"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="2" border="0"/></td>
    </tr>
    <%-- Iterate through the tasks that depend on this task --%>
<%
it = scheduleEntry.getSuccessors().iterator();

while (it.hasNext()) {
    TaskDependency currentDependency = (TaskDependency)it.next();
%>
	<tr align="left">
		<td>&nbsp;</td>
		<td nowrap class="tableContent"><%=currentDependency.getTaskNameMaxLength40()%></td>
		<td nowrap class="tableContent"><%=currentDependency.getDependencyType().getName()%></td>
		<td nowrap class="tableContent"><%=currentDependency.getLag()%></td>
        <td nowrap class="tableContent"><%=currentDependency.getStartDateString()%></td>
        <td nowrap class="tableContent"><%=currentDependency.getFinishDateString()%></td>
	</tr>
    <tr align="left" valign="middle">
		<td></td>
        <td colspan="5" class="tableLine" valign="middle"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="1" border="0"/></td>
    </tr>
<%
}
%>
    </table>
    </td></tr>
</table>
</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" labelToken="prm.schedule.taskview.returntotasklist.label" show="false"/>
		
		<tb:button type="submit" />
	</tb:band>
</tb:toolbar>

</form>

</div>

<template:getSpaceJS />
<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/workCapture.js" />
</body>
</html>
