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
|   $Revision: 17954 $
|       $Date: 2008-08-27 07:50:06 +0530 (Wed, 27 Aug 2008) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Task Blog View" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
    		net.project.security.SessionManager,
			net.project.base.Module,
			net.project.resource.AssignmentManager,
            java.net.URLEncoder,
            net.project.security.Action,
            net.project.xml.XMLFormatter"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="history" class="net.project.schedule.ScheduleEntryHistory" scope="request" />
<jsp:useBean id="refLink" class="java.lang.String" scope="request"/>
<jsp:useBean id="refLinkEncoded" class="java.lang.String" scope="request"/>
<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="request" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
	String baseUrl = SessionManager.getJSPRootURL();
    refLink = (refLink != null && refLink.length() > 0 ? refLink : SessionManager.getJSPRootURL()+ "/workplan/taskview?module=" + net.project.base.Module.SCHEDULE);
    refLinkEncoded = refLinkEncoded != null && refLinkEncoded.length() > 0 ? refLinkEncoded : java.net.URLEncoder.encode(refLink, SessionManager.getCharacterEncoding());
%>

<template:getDoctype/>
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>

<template:import type="css" src="/styles/blog.css" />
<template:import type="css" src="/styles/personal.css" />
<template:getSpaceCSS/>
<template:import type="javascript" src="/src/workCapture.js" />
<script language="javascript">
	var theForm;
	var isLoaded = false;   
	var JSPRootURL = '<%= baseUrl %>';
	var blogItFor = 'taskView';
	var moduleId = <%=Module.SCHEDULE%>;
	var spaceId = <%=user.getCurrentSpace().getID()%>;
	var userId = <%=user.getUserDomainID()%>;
	var taskId = <%=scheduleEntry.getID()%>;
	var blogEntryIds = ''; 
	var fullEnriesView = false;
	var objectType = '<%=scheduleEntry.getTaskType()%>';
	var objectName = '<%=scheduleEntry.getName().replaceAll("'", "&acute;")%>';
	var needToLoadBlogEntries = true;
	var actionsIconEnabled = '<display:get name="prm.global.actions.icon.isenabled" />';
	var showTitlesOnlyLink = '<display:get name="prm.blog.viewblog.showtitlesonly.link" />';
	var showFullEntriesLink = '<display:get name="prm.blog.viewblog.showfullentries.link" />';
	var showTitlesOnlyImageOn = JSPRootURL + '<display:get name="all.global.toolbar.standard.showtitles.image.on" />';
	var showTitlesOnlyImageOver = JSPRootURL + '<display:get name="all.global.toolbar.standard.showtitles.image.over" />';
function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() {
	self.document.location = JSPRootURL +"<%=refLink%>";
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
   self.location = JSPRootURL + src + "&module=<%=Module.SCHEDULE%>&id=<%=scheduleEntry.getID()%>&refLink=<%=refLinkEncoded %>";
}

function loadBlogEntriesForTaskView(){
	showBlogEntriesForTaskOnPage(<%=Module.SCHEDULE%>, <%=scheduleEntry.getID()%>);
}

</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport" onLoad="loadBlogEntriesForTaskView();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="<%=scheduleEntry.getNameMaxLength40()%>"
					jspPage='<%=baseUrl + "/servlet/ScheduleController/TaskView/History"%>'
					queryString='<%="module=" + Module.SCHEDULE + "&action=" + Action.VIEW + "&id=" + scheduleEntry.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.schedule.taskview.modify.link")%>' function="javascript:modify();"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post">
	<input type="hidden" name="theAction">
	<input type="hidden" name="refLink" value="<%=refLink%>" />
<table border="0" align="left" cellpadding="0" cellspacing="0" width="97%">
	<tr class="channelHeader">
		<td width="1%" class="channelHeader"><img src="<%=baseUrl%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td  nowrap class="channelHeader" colspan="4"><nobr><display:get name="prm.schedule.taskview.channel.viewtask.title"/></nobr></td>
		<td align=right width="1%" class="channelHeader"><img src="<%=baseUrl%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>

        <jsp:include page="include/taskProperties.jsp" flush="true" />

	<tr>
		<td colspan="6">
<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.status.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView?action=1');"  />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.resources.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Assignments?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.materials.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Material?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.dependencies.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Dependencies?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.financial.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Financial?action=1');" />	
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.advanced.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Advanced?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.history.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/History?action=1');"/>
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.blogs.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/History?action=1&showBlog=1');" selected="true"/>
</tab:tabStrip>
		</td>
	</tr>
	<tr><td colspan="6">&nbsp;</td></tr>
	<tr><td colspan="6">
		<table border="0" align="left" cellpadding="0" cellspacing="0" width="100%">
			
			<tr>
				<td colspan="6">
	                <div id="TaskBlogDiv" style="position:relative; height: auto;">
	                	<font color="blue"  style="font-weight:bold; font-size: 14px;">Loading blog entries... </font>
						<img src="<%= baseUrl %>/images/default/grid/loading.gif" align="absmiddle"/>
					</div>
			    </td>
			</tr>
		</table>
		</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
	    <tb:button type="cancel" labelToken="prm.schedule.taskview.returntotasklist.label" show="false"/>
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
