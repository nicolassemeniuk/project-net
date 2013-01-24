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

<%@ page contentType="text/html; charset=UTF-8"
	info="Edit Working Time Calendar" language="java"
	errorPage="/errors.jsp"
	import="net.project.security.SessionManager,net.project.base.property.PropertyProvider,net.project.base.Module,net.project.security.Action,java.util.Iterator,net.project.calendar.workingtime.WorkingTimeCalendarHelper"%>
<jsp:useBean id="editHelper"
	type="net.project.calendar.workingtime.WorkingTimeCalendarHelper"
	scope="request" />
<%@ include file="/base/taglibInclude.jsp"%>

<security:verifyAccess action="modify" module="<%=Module.SCHEDULE%>" />

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
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=schedule_workingtimeedit";
	openwin_help(helplocation);
}

function cancel() {
    self.document.location = JSPRootURL + '/servlet/ScheduleController/WorkingTime/List?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&calendarID=<%=editHelper.getID()%>';
}

function reset() {
    theForm.reset();
}

function submit() {
	theAction("submit");
    theForm.handlerName.value = "/WorkingTime/Edit";
    theForm.submit();
}

function createDate() {
    theAction("createDate");
    theForm.handlerName.value = "/WorkingTime/EditDate";
    theForm.submit();
}

function modifyDate() {
    if (verifySelection(theForm, 'single', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        clickDate(getSelection(theForm));
    }
}

function clickDate(value) {
    theAction("modifyDate");
    theForm.handlerName.value = "/WorkingTime/EditDate";
    theForm.entryID.value = value;
    theForm.submit();
}

function removeDate() {
    if (verifySelection(theForm, 'single', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.schedule.workingtime.edit.removedateconfirm.message")%>', function(btn) { 
			if(btn == 'yes') { 
				theAction("removeDate");
           	 	theForm.handlerName.value = "/WorkingTime/EditDate";
            	theForm.entryID.value = getSelection(theForm);
            	theForm.submit();
			} else {
			 	return false;
			}
		});
    }
}

function setStandard() {
    Ext.MessageBox.confirm('<display:get name="prm.global.extconfirm.title" />', '<display:get name="prm.schedule.workingtime.edit.preset.message" />', function(btn) { 
     if(btn == 'yes'){
	        theAction("preset");
	        theForm.presetType.value = "standard";
	        theForm.handlerName.value = "/WorkingTime/Edit";
	        theForm.submit();
    	}
    });
}

function setNightshift() {
    Ext.MessageBox.confirm('<display:get name="prm.global.extconfirm.title" />', '<display:get name="prm.schedule.workingtime.edit.preset.message" />', function(btn) { 
     if(btn == 'yes'){
	        theAction("preset");
	        theForm.presetType.value = "nightshift";
	        theForm.handlerName.value = "/WorkingTime/Edit";
	        theForm.submit();
    	}
    });
}

function set24Hour() {
     Ext.MessageBox.confirm('<display:get name="prm.global.extconfirm.title" />', '<display:get name="prm.schedule.workingtime.edit.preset.message" />', function(btn) { 
     if(btn == 'yes'){
	        theAction("preset");
	        theForm.presetType.value = "24hour";
	        theForm.handlerName.value = "/WorkingTime/Edit";
	        theForm.submit();
    	}
    });
}

function updateTimeFields(dayNumber, isWorkingTime) {
    var isEnabled;

    <%-- Determine the coloring --%>
    var dayTable = document.getElementById("day_" + dayNumber);
    if (isWorkingTime) {
        dayTable.className = "workingDay";
    } else {
        dayTable.className = "nonWorkingDay";
    }

    <%-- Determine whether to enable or disable the times --%>
    var dayValue = getSelectedValue(theForm.elements["dayValue_" + dayNumber]);
    if (dayValue == "workingtime") {
        isEnabled = true;
    } else {
        isEnabled = false;
    }

    updateTimeFieldsForPrefix("dayTimeStart_" + dayNumber, isEnabled);
    updateTimeFieldsForPrefix("dayTimeEnd_" + dayNumber, isEnabled);
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

//To handle form submission on hitting enter key
function onsubmitForm(){
	if( theForm.name.value.trim() != ''){
		submit();
	} else {
		extAlert(errorTitle,'<display:get name="prm.schedule.workingtime.edit.name.isrequired.message" />',Ext.MessageBox.ERROR);
		return false;
	}
}
</script>

<style type="text/css">

.nonWorkingDay {
    background-color: #CCCCCC;
}


.workingDay {
}

</style>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page level="2"
				display='<%=PropertyProvider.get("prm.schedule.workingtime.edit.pagetitle")%>'
				jspPage='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/WorkingTime/Edit" %>'
				queryString='<%="module="+Module.SCHEDULE+"&action="+Action.MODIFY+"&calendarID=" + editHelper.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'><br>

<form name="main" method="post"
	action="<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController"
	onSubmit="return onsubmitForm();"><input type="hidden"
	name="module" value="<%=Module.SCHEDULE%>"> <input
	type="hidden" name="action" value="<%=Action.MODIFY%>"> <input
	type="hidden" name="theAction" value="/WorkingTime/Edit"> <input
	type="hidden" name="handlerName"> <input type="hidden"
	name="calendarID" value="<%=editHelper.getID()%>"> <input
	type="hidden" name="entryID"> <input type="hidden"
	name="presetType"> <errors:show scope="request" />

<table border="0" cellspacing="0" cellpadding="0" width="100%">

	<%-- Name of calendar --%>
	<tr>
		<td colspan="4">
		<table width="100%">
			<tr>
				<td class="fieldRequired"><label for="name"><display:get
					name="prm.schedule.workingtime.edit.name.label" /></label></td>
				<td class="tableContent">
				<%
				if (editHelper.isNameUpdateable()) {
				%> <input type="text" name="name" id="name" size="40"
					maxLength="255" value="<%=editHelper.getName()%>"> <%
 } else {
 %> <%=editHelper.getName()%> <%
 }
 %>
				</td>
			</tr>
			<%
			if (!editHelper.isBaseCalendar()) {
			%>
			<tr>
				<td>&nbsp;</td>
				<td class="tableContent"><display:get
					name="prm.schedule.workingtime.edit.basedon">
					<display:param
						value="<%=editHelper.getParentCalendarDisplayName()%>" />
				</display:get></td>
			</tr>
			<%
			}
			%>
		</table>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>

	<%-- Day Of Week --%>
	<tr>
		<td class="channelHeader" width="1%"><img
			src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif"
			width=8 height=15></td>
		<td nowrap class="channelHeader" align=left colspan="2"><display:get
			name="prm.schedule.workingtime.edit.dayofweek.title" /></td>
		<td width="1%" align="right" class="channelHeader"><img
			src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif"
			width=8 height=15></td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>

	<%-- Preset Calendar type links --%>
	<tr>
		<td>&nbsp;</td>
		<td colspan="2">
		<table>
			<tr class="tableContent">
				<td class="tableHeader"><display:get
					name="prm.schedule.workingtime.edit.template.label" /></td>
				<td><a href="javascript:setStandard();"><display:get
					name="prm.schedule.workingtime.edit.template.standard.label" /></a></td>
				<td><a href="javascript:setNightshift();"><display:get
					name="prm.schedule.workingtime.edit.template.nightshift.label" /></a></td>
				<td><a href="javascript:set24Hour();"><display:get
					name="prm.schedule.workingtime.edit.template.24hour.label" /></a></td>
			</tr>
		</table>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>

	<tr>
		<td>&nbsp;</td>
		<td colspan="2">

		<table width="100%" cellspacing="0" border="1">
			<%
			            // Number of days to show on each row (to avoid extra wide html page)
			            int daysPerRow = 2;
			%>
			<colgroup span="<%=daysPerRow%>"
				style="border: solid 1px; border-collapse: collapse" />
				<%-- Repeat for each day of week --%>
				<%
				            int count = 0;
				            for (Iterator it = editHelper.getDayOfWeekIterator(); it.hasNext(); count++) {
				                WorkingTimeCalendarHelper.DayOfWeekHelper nextEntry = (WorkingTimeCalendarHelper.DayOfWeekHelper) it
				                                .next();
				                String radioName = "dayValue_" + nextEntry.getDayNumber();
				                String useDefaultChecked = (nextEntry.isUseDefaultSelected() ? "checked" : "");
				                String workingTimeChecked = (nextEntry.isWorkingTimeSelected() ? "checked" : "");
				                String nonWorkingTimeChecked = (nextEntry.isNonWorkingTimeSelected() ? "checked" : "");
				                String htmlClass = (nextEntry.isWorkingDay() ? "workingDay" : "nonWorkingDay");
				%>
				<%
				                    if ((count % daysPerRow) == 0) {
				                    if (count > 0) {
				%>
			
			</tr>
			<%
			}
			%>
			<tr>
				<%
				}
				%>
				<td style="border: solid 1px">
				<table width="100%" cellspacing="0" class="<%=htmlClass%>"
					id="day_<%=nextEntry.getDayNumber()%>">
					<colgroup span="2">
						<col width="0*" />
					</colgroup>
					<tr class="tableHeaderNoBGC">
						<td align="center" colspan="2" style="border-bottom: solid 1px"><%=nextEntry.getDayOfWeekFormatted()%></td>
					</tr>
					<%
					                    if (nextEntry.hasParent()) {
					                    String defaultDisplay = PropertyProvider
					                                    .get((nextEntry.isParentEntryWorkingDay() ? "prm.schedule.workingtime.edit.workingtime"
					                                                    : "prm.schedule.workingtime.edit.nonworkingtime"));
					%>
					<tr class="tableContentNoBGC">
						<td><input type="radio"
							id="dayValueDefault_<%=nextEntry.getDayNumber()%>"
							name="<%=radioName%>" value="usedefault" <%=useDefaultChecked%>
							onClick="updateTimeFields(<%=nextEntry.getDayNumber()%>, <%=nextEntry.isParentEntryWorkingDay()%>)"></td>
						<td><label
							for="dayValueDefault_<%=nextEntry.getDayNumber()%>"> <display:get
							name="prm.schedule.workingtime.edit.usedefault">
							<display:param value="<%=defaultDisplay%>" />
						</display:get> </label></td>
					</tr>
					<%
					}
					%>
					<tr class="tableContentNoBGC">
						<td><input type="radio"
							id="dayValueNonWorkingTime_<%=nextEntry.getDayNumber()%>"
							name="<%=radioName%>" value="nonworkingtime"
							<%=nonWorkingTimeChecked%>
							onClick="updateTimeFields(<%=nextEntry.getDayNumber()%>, false)"></td>
						<td><label
							for="dayValueNonWorkingTime_<%=nextEntry.getDayNumber()%>">
						<display:get name="prm.schedule.workingtime.edit.nonworkingtime" />
						</label></td>
					</tr>
					<tr class="tableContentNoBGC">
						<td><input type="radio"
							id="dayValueWorkingTime_<%=nextEntry.getDayNumber()%>"
							name="<%=radioName%>" value="workingtime" <%=workingTimeChecked%>
							onClick="updateTimeFields(<%=nextEntry.getDayNumber()%>, true)"></td>
						<td><label
							for="dayValueWorkingTime_<%=nextEntry.getDayNumber()%>">
						<display:get name="prm.schedule.workingtime.edit.workingtime" />
						</label></td>
					</tr>

					<!-- Draw the time boxes -->
					<tr class="tableContentNoBGC">
						<td>&nbsp;</td>
						<td>
						<table width="100%">
							<colgroup span="2" />
							<tr>
								<td class="tableHeaderNoBGC"><display:get
									name="prm.schedule.workingtime.edit.workingtime.from.label" /></td>
								<td class="tableHeaderNoBGC"><display:get
									name="prm.schedule.workingtime.edit.workingtime.to.label" /></td>
							</tr>
							<%
							                // Draw 5 time boxes
							                for (int i = 0; i < 5; i++) {
							                    String controlName = "dayTimeControl_" + nextEntry.getDayNumber() + "_" + i;
							                    String startName = "dayTimeStart_" + nextEntry.getDayNumber() + "_" + i;
							                    String endName = "dayTimeEnd_" + nextEntry.getDayNumber() + "_" + i;
							%>
							<tr>
								<td><%-- Control field ensures an easily parseable field name --%>
								<input type="hidden" name="<%=controlName%>" value="1">
								<input:time
									time="<%=nextEntry.getWorkingTimeEditHelper().getStartTime(i)%>"
									name="<%=startName%>" isOptional="true"
									disabled="<%=Boolean.valueOf(!nextEntry.isWorkingTimeSelected())%>" />
								</td>
								<td><input:time
									time="<%=nextEntry.getWorkingTimeEditHelper().getEndTime(i)%>"
									name="<%=endName%>" isOptional="true"
									disabled="<%=Boolean.valueOf(!nextEntry.isWorkingTimeSelected())%>" /></td>
							</tr>
							<%
							}
							%>
						</table>
						</td>
					</tr>
					<!-- End of time boxes -->

				</table>
				</td>
				<%
				}
				%>
			</tr>
		</table>

		</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>

	<%-- Date Entries --%>
	<tr>
		<td class="channelHeader" width="1%"><img
			src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif"
			width=8 height=15></td>
		<td nowrap class="channelHeader" align=left><%=PropertyProvider.get("prm.schedule.workingtime.edit.dates.title")%></td>
		<td class="channelHeader" align="right"><tb:toolbar
			style="channel">
			<tb:band name="channel" showLabels="true">
				<tb:button type="create" function="javascript:createDate();" />
				<tb:button type="modify" function="javascript:modifyDate();" />
				<tb:button type="remove" function="javascript:removeDate();" />
			</tb:band>
		</tb:toolbar></td>
		<td width="1%" align="right" class="channelHeader"><img
			src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif"
			width=8 height=15></td>
	</tr>

	<tr>
		<td>&nbsp;</td>
		<td colspan="2">

		<table width="100%" border="0">
			<colgroup span="3">
				<col width="0*" />
			</colgroup>
			<%-- Repeat for each date --%>
			<%
			                for (Iterator it = editHelper.getDateIterator(); it.hasNext();) {
			                WorkingTimeCalendarHelper.DateHelper nextEntry = (WorkingTimeCalendarHelper.DateHelper) it.next();
			%>
			<tr class="tableContent">
				<td><input type="radio" name="selected"
					value="<%=nextEntry.getEntryID()%>" /></td>
				<td><a
					href="javascript:clickDate(<%=nextEntry.getEntryID()%>);"> <%=nextEntry.getDateDisplay()%>
				</a></td>
				<td><%=PropertyProvider
                                                                .get((nextEntry.isWorkingDay() ? "prm.schedule.workingtime.view.workingtime"
                                                                                : "prm.schedule.workingtime.view.nonworkingtime"))%></td>
			</tr>
			<%
			}
			%>
		</table>
		</td>
		<td>&nbsp;</td>
	</tr>
</table>

</form>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" />
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar> <%@ include file="/help/include_outside/footer.jsp"%>

<template:getSpaceJS />
</body>
</html>
