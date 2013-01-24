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
    info="Edit Working Time Calendar"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action"
%>
<jsp:useBean id="calendarID" type="java.lang.String" scope="request" />
<jsp:useBean id="dateHelper" type="net.project.calendar.workingtime.WorkingTimeCalendarDateEntryHelper" scope="request" />
<%@ include file="/base/taglibInclude.jsp"%>

<security:verifyAccess action="modify" module="<%=Module.SCHEDULE%>" />

<%
    String dateTypeSingleChecked = dateHelper.isSingleDate() ? "checked" : "";
    String dateTypeRangeChecked = dateHelper.isSingleDate() ? "" : "checked";
    String dateValueWTChecked = dateHelper.isWorkingTimeSelected() ? "checked" : "";
    String dateValueNWTChecked = dateHelper.isNonWorkingTimeSelected() ? "checked" : "";
%>

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
    theForm = self.document.forms["main"];
}

function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=schedule_workingtimedateedit";
	openwin_help(helplocation);
}

function cancel() {
    self.document.location = JSPRootURL + '/servlet/ScheduleController/WorkingTime/Edit?module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>&calendarID=<%=calendarID%>';
}

function reset() {
    theForm.reset();
}

function submit() {
    theAction("submit");
    theForm.submit();
}

function add() {
    theAction("add");
    theForm.submit();
}

function setStandard() {
    theAction("preset");
    theForm.presetType.value = "standard";
    theForm.submit();
}

function setNightshift() {
    theAction("preset");
    theForm.presetType.value = "nightshift";
    theForm.submit();
}

function set24Hour() {
    theAction("preset");
    theForm.presetType.value = "24hour";
    theForm.submit();
}

function updateTimeFields(selectedOption) {
    var isEnabled;

    if (selectedOption.value == "workingtime") {
        isEnabled = true;
    } else {
        isEnabled = false;
    }

    updateTimeFieldsForPrefix("timeStart", isEnabled);
    updateTimeFieldsForPrefix("timeEnd", isEnabled);
}

function updateTimeFieldsForPrefix(prefix, isEnabled) {
    <%-- Overly cautious about presence of elements due to i18n; some elements
         (namely _ampm) may not be present --%>
    for (var i = 0; i < 5; i++) {
        var namePrefix = prefix + "_" + i + "_";
        var nextElement;

        nextElement = theForm.elements[namePrefix + "hour"]
        if (nextElement) {
            nextElement.disabled = !isEnabled;
        }
        nextElement = theForm.elements[namePrefix + "minute"];
        if (nextElement) {
            nextElement.disabled = !isEnabled;
        }
        nextElement = theForm.elements[namePrefix + "ampm"];
        if (nextElement) {
            nextElement.disabled = !isEnabled;
        }
    }
}

</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page level="3" display='<%=PropertyProvider.get("prm.schedule.workingtime.editdate.pagetitle")%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>

<form name="main" method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/WorkingTime/EditDate">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.MODIFY%>">
    <input type="hidden" name="theAction">
    <input type="hidden" name="calendarID" value="<%=calendarID%>">
    <input type="hidden" name="entryID" value="<jsp:getProperty name="dateHelper" property="entryID" />">
    <input type="hidden" name="presetType">

    <errors:show scope="request" />

    <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr>
            <td class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
            <td nowrap class="channelHeader" align=left colspan="2"><display:get name="prm.schedule.workingtime.editdate.pagetitle" /></td>
            <td width="1%" align="right" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
        </tr>

        <tr>
            <td>&nbsp;</td>
            <td colspan="2">

    <table width="100%">
        <colgroup>
            <col width="1%">
        </colgroup>
        <tr class="tableContent">
            <td><input type="radio" name="dateType" id="dateTypeSingle" value="single" <%=dateTypeSingleChecked%> /></td>
            <td class="fieldRequired"><label for="dateTypeSingle"><display:get name="prm.schedule.workingtime.editdate.singledate.label" /></label></td>
            <td colspan="2">
                <input type="text" size="20" maxLength="20" name="singleDateValue" id="singleDateValue" value="<jsp:getProperty name="dateHelper" property="singleDateFormatted" />"/>
                <util:insertCalendarPopup fieldName="singleDateValue" rootURL="<%=SessionManager.getJSPRootURL()%>" />
            </td>
        <tr>
        <tr class="tableContent">
            <td><input type="radio" name="dateType" id="dateTypeRange" value="range" <%=dateTypeRangeChecked%> /></td>
            <td class="fieldRequired"><label for="dateTypeRange"><display:get name="prm.schedule.workingtime.editdate.rangedate.label" /></label></td>
            <td class="tableContent">
                <input type="text" size="20" maxLength="20" name="rangeDateStartValue" id="rangeDateStartValue" value="<jsp:getProperty name="dateHelper" property="rangeDateStartFormatted" />"/>
                <util:insertCalendarPopup fieldName="rangeDateStartValue" rootURL="<%=SessionManager.getJSPRootURL()%>" />
            </td>
            <td class="tableContent">
                <input type="text" size="20" maxLength="20" name="rangeDateEndValue" id="rangeDateEndValue" value="<jsp:getProperty name="dateHelper" property="rangeDateEndFormatted" />"/>
                <util:insertCalendarPopup fieldName="rangeDateEndValue" rootURL="<%=SessionManager.getJSPRootURL()%>" />
            </td>
        <tr>

        <tr><td colspan="4">&nbsp;</td></tr>

        <%-- Working time / Non working time selection --%>
        <tr>
            <td>&nbsp;</td>
            <td colspan="3">
            <table width="100%">
                <colgroup>
                    <col width="1%">
                    <col span="2">
                </colgroup>
                <tr class="tableContent">
                    <td><input type="radio" name="dateValue" id="dateValueNWT" value="nonworkingtime" <%=dateValueNWTChecked%> onClick="updateTimeFields(this)" /></td>
                    <td><label for="dateValueNWT"><display:get name="prm.schedule.workingtime.editdate.nonworkingtime.label" /></label></td>
                    <td>&nbsp;</td>
                </tr>
                <tr class="tableContent">
                    <td><input type="radio" name="dateValue" id="dateValueWT" value="workingtime" <%=dateValueWTChecked%> onClick="updateTimeFields(this)" /></td>
                    <td><label for="dateValueWT"><display:get name="prm.schedule.workingtime.editdate.workingtime.label" /></label></td>
                    <td>
                    <%-- Preset Calendar type links --%>
                    <table>
                        <tr class="tableContent">
                            <td class="tableHeader"><display:get name="prm.schedule.workingtime.editdate.template.label" /></td>
                            <td><a href="javascript:setStandard();"><display:get name="prm.schedule.workingtime.editdate.template.standard.label" /></a></td>
                            <td><a href="javascript:setNightshift();"><display:get name="prm.schedule.workingtime.editdate.template.nightshift.label" /></a></td>
                            <td><a href="javascript:set24Hour();"><display:get name="prm.schedule.workingtime.editdate.template.24hour.label" /></a></td>
                        </tr>
                    </table>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td colspan="2">
                    <table width="100%">
                        <colgroup span="2" />
                        <tr class="tableHeaderNoBGC">
                            <td><display:get name="prm.schedule.workingtime.editdate.workingtime.from.label" /></td>
                            <td><display:get name="prm.schedule.workingtime.editdate.workingtime.to.label" /></td>
                        </tr>
<%
    // Draw 5 time boxes
    for (int i = 0; i < 5; i++) {
        String controlName = "timeControl_" + i;
        String startName = "timeStart_" + i;
        String endName = "timeEnd_" + i;
%>
                        <tr>
                            <td>
                                <input type="hidden" name="<%=controlName%>" value="1">
                                <input:time time="<%=dateHelper.getWorkingTimeEditHelper().getStartTime(i)%>" name="<%=startName%>" isOptional="true" disabled="<%=Boolean.valueOf(!dateHelper.isWorkingTimeSelected())%>" />
                            </td>
                            <td><input:time time="<%=dateHelper.getWorkingTimeEditHelper().getEndTime(i)%>" name="<%=endName%>" isOptional="true" disabled="<%=Boolean.valueOf(!dateHelper.isWorkingTimeSelected())%>" /></td>
                        </tr>
<%      } %>
                    </table>
                    </td>
                </tr>
            </table>
            </td>
        </tr>

    </table>

            </td>
            <td>&nbsp;</td>
        </tr>
    </table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action">
        <tb:button type="submit"/>
        <tb:button type="cancel"/>
        <tb:button type="add" label="@prm.schedule.workingtime.editdate.button.addanother.label" />
    </tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
