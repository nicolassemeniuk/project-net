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
|   $Revision: 19958 $
|       $Date: 2009-09-12 13:00:28 -0300 (sáb, 12 sep 2009) $
|   $author: |
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Task Advanced View"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.schedule.ScheduleEntry,
            net.project.link.LinkManagerBean,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.base.Module,
            java.net.URLEncoder"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="display_linkMgr" class="net.project.link.LinkManagerBean" scope="session" />
<jsp:useBean id="refLink" class="java.lang.String" scope="request"/>
<jsp:useBean id="refLinkEncoded" class="java.lang.String" scope="request"/>
<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />
<%
    String baseUrl = SessionManager.getJSPRootURL();
    refLink = (refLink != null && refLink.length() > 0 ? refLink : "/workplan/taskview?module=" + net.project.base.Module.SCHEDULE);
    refLinkEncoded = refLinkEncoded != null && refLinkEncoded.length() > 0 ? refLinkEncoded : java.net.URLEncoder.encode(refLink, SessionManager.getCharacterEncoding());
%>

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= baseUrl %>';
	
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
   theForm.nextPage.value= src + "&module=<%=Module.SCHEDULE%>&id=<%=scheduleEntry.getID()%>&refLink=<%=refLinkEncoded %>";
   submit();
}

function submit () {
	theAction("submit");
	theForm.submit();
}

//open in popup
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
					queryString='<%="module=" + Module.SCHEDULE + "&action=" + Action.MODIFY + "&id=" + scheduleEntry.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.schedule.taskview.modify.link")%>' function="javascript:modify();"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%=baseUrl%>/schedule/TaskAdvancedProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="id" value="<jsp:getProperty name="scheduleEntry" property="ID" />">
    <input type="hidden" name="action" value="<%= request.getParameter("action") %>">
    <input type="hidden" name="module" value="<%= Module.SCHEDULE %>">
	<input type="hidden" name="refLink" value="<%=refLink%>" />
	<input type="hidden" name="nextPage" value=''>
<div align="center">
<table border="0" align="left" cellpadding="0" cellspacing="0" width="97%">
	<tr class="channelHeader">
		<td width="1%" class="channelHeader"><img src="<%=baseUrl%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td  nowrap class="channelHeader" colspan="4"><nobr><display:get name="prm.schedule.taskview.channel.viewtask.title"/></nobr></td>
		<td align=right width="1%" class="channelHeader"><img src="<%=baseUrl%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
    <% if (request.getAttribute("errorMessage") != null) { %>
    <tr align="left" valign="top">
			<td>&nbsp;</td>
		    <td colspan="4" class="warnText">
		    <%=(String)request.getAttribute("errorMessage")%>
		    </td>
		    <td>&nbsp;</td>
	</tr>
    <% } %>

        <jsp:include page="include/taskProperties.jsp" flush="true" />

	<tr>
		<td colspan="6">
<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.status.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView?action=1');"  />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.resources.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Assignments?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.materials.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Material?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.dependencies.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Dependencies?action=1');" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.advanced.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Advanced?action=1');" selected="true" />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.history.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/History?action=1');" />
	<display:if name="@prm.blog.isenabled">
		<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.blogs.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/History?action=1&showBlog=1');" />
    </display:if>
</tab:tabStrip>
		</td>
	</tr>
	<tr><td colspan="6">&nbsp;</td></tr>

    <tr align="left" valign="middle">
        <td>&nbsp;</td>
        <td nowrap class="fieldRequired"><display:get name="prm.schedule.taskedit.constraints.type.label"/></td>
        <td class="tableContent"><%=scheduleEntry.getConstraintType().getName()%></td>
        <td nowrap class="fieldRequired"><display:get name="prm.schedule.taskedit.constraints.date.label"/></td>
        <td class="tableContent"><%=scheduleEntry.getConstraintDateString()%></td>
    </tr>
    <tr align="left" valign="middle">
        <td>&nbsp;</td>
        <td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.constraints.deadline.label"/></td>
        <td nowrap class="tableContent"><%=scheduleEntry.getDeadlineString()%></td>
        <td colspan="3">&nbsp;</td>
    </tr>

	<tr><td colspan="6">&nbsp;</td></tr>

	<%-- Insert Task Comments Section --%>
	<tr class="channelHeader">
		<td width="1%" class="channelHeader"><img src="<%=baseUrl%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td nowrap class="channelHeader" colspan="4"><nobr><display:get name="prm.schedule.taskview.advanced.channel.comments.title"/></nobr></td>
		<td align=right width="1%" class="channelHeader"><img src="<%=baseUrl%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td colspan="4">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr><td class="tableHeader" align="left"><display:get name="prm.schedule.taskview.advanced.comments.previous.label"/></td></tr>
			<tr><td class="tableContent">
				<%	if (!scheduleEntry.getComments().isEmpty()) { %>
					<iframe frameborder="0" width="100%" height="100" scrolling="auto"
							src="<%=SessionManager.getJSPRootURL() + "/schedule/TaskCommentsView.jsp?module=" + net.project.base.Module.SCHEDULE + "&id=" + scheduleEntry.getID()%>">
						<%-- Browser does not support iframes. --%>
					</iframe>
				<%	} else { %>
					<display:get name="prm.schedule.taskview.advanced.comments.none.message"/>
                <%	} %>
			</td></tr>
			<!--<tr><td colspan="5">&nbsp;</td></tr>-->
			<tr><td class="fieldNonRequired" align="left"><display:get name="prm.schedule.taskview.advanced.comments.add.label"/><br/>
				<textarea name="comment" cols="60" rows="5" ></textarea>
			</td></tr>
		</table>
		</td>
		<td>&nbsp;</td>
	</tr>

	<%-- End of Task Comments Section --%>
	<tr><td colspan="6">&nbsp;</td></tr>

	<%-- Insert Links Channel --%>
    <tr><td colspan="6">
		<table width="400" border="0" cellspacing="0" cellpadding="0">
	        <tr class="channelHeader">
	    	    <td class="channelHeader" width="1%"><img src="<%=baseUrl%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		        <td nowrap class="channelHeader" width="85%"><display:get name="prm.schedule.taskview.advanced.channel.links.title"/></td>
				<td nowrap class="channelHeader">
					<tb:toolbar style="channel" showLabels="true">
						<tb:band name="channel">
							<tb:button type="modify" function='javascript:modifyLinks();' />
						</tb:band>
					</tb:toolbar>
				</td>
		       	<td align=right class="channelHeader" width="1%"><img src="<%=baseUrl%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	        </tr>
	        <tr valign="top">
		        <td class="channelContent">&nbsp;</td>
		        <td colspan=2 class="channelContent">
		        <%
                    display_linkMgr.setView(LinkManagerBean.VIEW_ALL);
                    display_linkMgr.setContext(net.project.link.ILinkableObject.GENERAL);
                    display_linkMgr.setRootObject(scheduleEntry);

                    request.setAttribute("action", "" + net.project.security.Action.VIEW);
		        %>
		        <jsp:include page="/link/displayLinks.jsp" flush="true" />
		        </td>
		        <td class="channelContent">&nbsp;</td>
	        </tr>
        </table>
		</td>
	</tr>
	<%-- End of Links Channel --%>
</table>
</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" labelToken="prm.schedule.taskview.returntotasklist.label" show="false"/>
		
		<tb:button type="submit" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkDate.js"/>
<template:import type="javascript" src="/src/workCapture.js" />
</body>
</html>
