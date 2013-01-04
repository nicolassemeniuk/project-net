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
    info="Show possible alternatives to fix overallocated resources"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            java.util.Iterator,
            net.project.schedule.conflict.IOverallocationResolution,
            net.project.util.DateFormat,
            net.project.base.Module,
            net.project.security.Action,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="resolutions" type="java.util.Collection" scope="request" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="request"/>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />


<script language="javascript" type="text/javascript">
var theForm;

function setup() {
    theForm = self.document.forms[0];
}

function cancel() {
    history.back();
}

function submit() {
    theForm.submit();
}
</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display=""
					jspPage='<%=SessionManager.getJSPRootURL() + "" %>'
					queryString='<%="module="+net.project.base.Module.REPORT%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br>

<form action="<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskView/FixOverallocationsProcessing" method="post">
<input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
<input type="hidden" name="action" value="<%=Action.MODIFY%>">
<input type="hidden" name="id" value="<%=scheduleEntry.getID()%>">

<table width="97%">
<tr>
    <td colspan="3">
        <table border="0" cellspacing="0" cellpadding="0" vspace="0" width="100%">
        <tr>
        <td class="channelHeader" width="1%"><img  src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
        <td class="channelHeader" align="left"></td>
        <td width="1%" align=right class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
        <tr>
        </table>
    </td>
</tr>

<% if (!resolutions.isEmpty()) { %>
<tr class="tableHeader">
    <td></td>
    <td><display:get name="prm.schedule.fixoverallocations.description.name"/></td>
    <td><display:get name="prm.schedule.fixoverallocations.newscheduleenddate.name"/></td>
</tr>
<tr><td colspan="3" class="headerSep"></td></tr>
<%
    DateFormat df = user.getDateFormatter();
    int i = 0;
    for (Iterator it = resolutions.iterator(); it.hasNext();) {
        IOverallocationResolution resolution = (IOverallocationResolution)it.next();
%>
<tr class="tableContent">
<td><input type="radio" name="resolutionID" value="<%=i++%>"<%=(i==1 ? " checked" : "")%>></td>
<td><%=resolution.getDescription()%></td>
<td><%=(resolution.doesScheduleEndDateChange() ? df.formatDate(resolution.newScheduleEndDate()) : "")%></td>
</tr>
<tr><td colspan="3" class="rowSep"></td></tr>
<%  } %>
<% } else { %>
<tr>
<td width="1%">&nbsp;</td>
<td class="tableContent"><display:get name="prm.schedule.fixoverallocations.nofixesfound.message"/></td>
</tr>
<% } %>
</table>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
    <% if (resolutions.isEmpty()) { %>
           <tb:button type="cancel" label="@prm.schedule.fixoverallocations.returntoassignmentspage.label"/>
    <% } else { %>
           <tb:button type="submit"/>
           <tb:button type="cancel"/>
    <% } %>
    </tb:band>
</tb:toolbar>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
