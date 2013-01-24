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
|   $Revision: 15815 $
|       $Date: 2007-04-10 10:04:20 +0530 (Tue, 10 Apr 2007) $
|     $Author: avinash $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
            net.project.security.Action,
            java.util.Enumeration,
            net.project.base.property.PropertyProvider,
            java.net.URLEncoder"
%>
<%@ include file="/base/taglibInclude.jsp"%> 
<jsp:useBean id="scheduleImporter" type="net.project.schedule.importer.XMLImporter" scope="session"/>
<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript" type="text/javascript">
function cancel() {
	theLocation="<%=SessionManager.getJSPRootURL()%>/workplan/taskview?module=<%= net.project.base.Module.SCHEDULE %>";
	self.location = theLocation;
}
function finish() {
    <% String ce = SessionManager.getCharacterEncoding(); %>
    self.document.location = "<%=SessionManager.getJSPRootURL()%>/base/Busy.jsp?" +
        "module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>&processingURL="+
        "<%=URLEncoder.encode(SessionManager.getJSPRootURL()+"/servlet/ImportController/Import", ce)%>"+
        "&processingParams=<%=URLEncoder.encode("&module="+Module.SCHEDULE+"&action="+Action.MODIFY, ce)%>"+
        "&title=<%=URLEncoder.encode(PropertyProvider.get("prm.schedule.import.xml.pleasewait.title"), ce)%>"+
        "&message=<%=URLEncoder.encode(PropertyProvider.get("prm.schedule.import.xml.pleasewait.message"), ce)%>";
}
</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page displayToken='prm.schedule.import.xml.importsummary.pagetitle'
					jspPage='<%=SessionManager.getJSPRootURL() + "/schedule/importer/ImportSummary.jsp" %>'
					queryString='<%="module="+net.project.base.Module.SCHEDULE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
	</tb:band>
</tb:toolbar>
<div id='content'>
<br>

<table border="0" width="97%" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
    <td width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td align="left" class="channelHeader" nowrap><display:get name="prm.schedule.import.xml.importsummary.pagetitle" /></td>
    <td width="1%" align=right class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>

<tr>
    <td>&nbsp;</td>
    <td>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td class="tableHeader"><display:get name="prm.schedule.import.xml.importsummary.projecttoimport.name" /></td>
        <td class="tableContent"><jsp:getProperty name="scheduleImporter" property="projectName" /></td>
    </tr>
    <tr>
        <td class="tableHeader"><display:get name="prm.schedule.import.xml.importsummary.taskstoimport.name" /></td>
        <td class="tableContent"><%=(scheduleImporter.isImportTasks() ? scheduleImporter.getTaskCount() : 0)%></td>
    </tr>
    <tr>
        <td class="tableHeader"><display:get name="prm.schedule.import.xml.importsummary.resourcestoimport.name" /></td>
        <td class="tableContent"><%=(scheduleImporter.isImportAssignments() ? scheduleImporter.getResourceCount() : 0)%></td>
    </tr>
    <tr>
        <td class="tableHeader"><display:get name="prm.schedule.import.xml.importsummary.workingtimecalendarstoimport.label" /></td>
        <td class="tableContent"><%=(scheduleImporter.isImportWorkingTimeCalendars() ? scheduleImporter.getWorkingTimeCalendarImportCount() : 0)%></td>
    </tr>
    </table>
    </td>
    <td>&nbsp;</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true" width="97%">
    <tb:band name="action">
            <tb:button type="cancel"/>
            <tb:button type="finish" label="@prm.schedule.import.xml.import.button.label"/>
    </tb:band>
</tb:toolbar>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
