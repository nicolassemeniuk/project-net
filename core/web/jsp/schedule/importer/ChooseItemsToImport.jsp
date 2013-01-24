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
<jsp:useBean id="importStartAndEnd" type="java.lang.Boolean" scope="request"/>

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
			<history:page displayToken='prm.schedule.import.xml.chooseitemstoimport.pagetitle'
					jspPage='<%=SessionManager.getJSPRootURL() + "/schedule/importer/ChooseItemsToImport.jsp" %>'
					queryString='<%="module="+Module.SCHEDULE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<form name="main" method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/ImportController/MapResources">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.MODIFY%>">
    <input type="hidden" name="theAction">

<table border="0" width="100%" cellspacing="0" cellpadding="0">

<tr class="channelHeader">
    <td width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td align="left" class="channelHeader" nowrap>&nbsp;<display:get name="prm.schedule.import.xml.chooseitemstoimport.pagetitle" /></td>
    <td width="1%" align=right class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td>
    <table>

    <%-- Select project from available projects in MPD file --%>
    <tr>
        <td class="tableHeader">
            <label for="MSProjectID"><display:get name="prm.schedule.import.xml.chooseitemstoimport.pleaseselectaprojectname.message" /></label>
        </td>
        <td class="tableContent">
            <select name="MSProjectID" id="MSProjectID">
                <%= scheduleImporter.getProjectNameHTMLOptionList() %>
            </select>
        </td>
    </tr>

    <%-- Checkboxes for selecting items to import --%>
    <tr>
        <td colspan="2">
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <colgroup>
                <col width="1%">
                <col width="1%">
                <col>
            </colgroup>
        <tr class="tableContent">
            <td><input type="checkbox" name="importTasks" id="importTasks" value="true" checked onClick="clickImportTasks(this.value)"></td>
            <td colspan="2" align="left"><label for="importTasks"><display:get name="prm.schedule.import.xml.chooseitemstoimport.tasks.name" /></label></td>
        </tr>
        <tr class="tableContent">
            <td>&nbsp;</td>
            <td><input type="checkbox" name="importAssignments" id="importAssignments" value="true" checked></td>
            <td align="left"><label for="importAssignments"><display:get name="prm.schedule.import.xml.chooseitemstoimport.assignments.name" /></label></td>
        </tr>
        <tr class="tableContent">
            <td><input type="checkbox" name="importStartAndEndDates" id="importStartAndEndDates" value="true" <%=importStartAndEnd.booleanValue() ? "CHECKED" : ""%>></td>
            <td colspan="2" align="left"><label for="importStartAndEndDates"><display:get name="prm.schedule.import.xml.chooseitemstoimport.projstartandend.name"/></label></td>
        </tr>
        <tr class="tableContent">
            <td><input type="checkbox" name="importWorkingTimeCalendars" id="importWorkingTimeCalendars" value="true" checked onClick="clickImportWorkingTimeCalendars(this.value)"></td>
            <td colspan="2" align="left"><label for="importWorkingTimeCalendars"><display:get name="prm.schedule.import.xml.chooseitemstoimport.workingtimecalendars.name" /></label></td>
        </tr>
        <tr class="tableContent">
            <td>&nbsp;</td>
            <td colspan="2">
            <table width="100%">
                <colgroup>
                    <col width="1%">
                    <col>
                </colgroup>
                <tr class="tableContent">
                    <td><input type="radio" name="defaultWorkingTimeCalendar" id="defaultWorkingTimeCalendarUpdate" value="update" checked></td>
                    <td align="left"><label for="defaultWorkingTimeCalendarUpdate"><display:get name="prm.schedule.import.xml.chooseitemstoimport.defaultworkingtimecalendar.update.label" /></label></td>
                </tr>
                <tr class="tableContent">
                    <td><input type="radio" name="defaultWorkingTimeCalendar" id="defaultWorkingTimeCalendarAdd" value="add"></td>
                    <td align="left"><label for="defaultWorkingTimeCalendarAdd"><display:get name="prm.schedule.import.xml.chooseitemstoimport.defaultworkingtimecalendar.add.label" /></label></td>
                </tr>
                <tr class="tableContent">
                    <td><input type="checkbox" name="importResourceWorkingTimeCalendars" id="importResourceWorkingTimeCalendars" value="true" checked></td>
                    <td align="left"><label for="importResourceWorkingTimeCalendars"><display:get name="prm.schedule.import.xml.chooseitemstoimport.resourceworkingtimecalendars.label" /></label></td>
                </tr>
            </table>
            </td>
        </tr>
        <tr class="tableContent">
            <td><input type="checkbox" name="importStartAndEndDates" id="importStartAndEndDates" value="true" checked></td>
            <td colspan="2" align="left"><label for="importStartAndEndDates"><display:get name="prm.schedule.import.xml.chooseitemstoimport.importstartandenddates.name"/></label></td>
        </tr>
        </table>
        </td>
    </tr>
    </table>
    </td>
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
