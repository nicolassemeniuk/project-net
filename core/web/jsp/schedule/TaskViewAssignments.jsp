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
    import="java.net.URLDecoder,
            java.net.URLEncoder,
            net.project.base.property.PropertyProvider,
			net.project.base.Module,
            net.project.code.TableCodeDomain,
            net.project.gui.html.HTMLOptionList,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.space.SpaceTypes,
            net.project.util.Validator,
            net.project.resource.AssignmentStatus,
            net.project.resource.Roster,
            net.project.resource.ScheduleEntryAssignment,
            net.project.util.NumberFormat,
            java.util.Iterator,
            java.util.Date,
            net.project.resource.Assignment,
            net.project.schedule.AssignmentsHelper,
            java.util.Collection,
            net.project.util.DateFormat,
            net.project.schedule.IAssignmentHelper,
            net.project.chargecode.ChargeCodeManager,
            net.project.hibernate.service.ServiceFactory,
			net.project.hibernate.model.PnChargeCode,
			org.apache.commons.collections.CollectionUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" type="net.project.security.User" scope="session" />
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="refLink" class="java.lang.String" scope="request"/>
<jsp:useBean id="refLinkEncoded" class="java.lang.String" scope="request"/>
<jsp:useBean id="assignments" type="java.util.List" scope="request"/>
<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="request" />
<jsp:useBean id="assignmentMap" type="java.util.Map" scope="request"/>
<jsp:useBean id="assignmentRoster" type="net.project.resource.AssignmentRoster" scope="request"/>
<jsp:useBean id="overallocatedResourcesExist" type="java.lang.Boolean" scope="request"/>
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request"/>
<jsp:useBean id="assignmentsHelper" class="net.project.schedule.AssignmentsHelper" scope="page"/>

<%
    DateFormat dateFormat = user.getDateFormatter();

    String baseUrl = SessionManager.getJSPRootURL();
    String id = scheduleEntry.getID();
    refLink = (refLink != null && refLink.length() > 0 ? refLink : "/workplan/taskview?module=" + net.project.base.Module.SCHEDULE);
    refLinkEncoded = refLinkEncoded != null && refLinkEncoded.length() > 0 ? refLinkEncoded : java.net.URLEncoder.encode(refLink, SessionManager.getCharacterEncoding());

    //Determine if we need to show the information icon box by default
    boolean showInfoBox = scheduleEntry.isCriticalPath() || overallocatedResourcesExist.booleanValue();

    assignmentsHelper.init(request, assignmentRoster, assignmentMap);
	//load the assignies for the space
	roster.setSpace(user.getCurrentSpace());
	roster.load();
 	ChargeCodeManager chargeCodeManager = new ChargeCodeManager();	
	Integer spaceId = Integer.valueOf(user.getCurrentSpace().getID());
	chargeCodeManager.setChargeCodeList(ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeByProjectId(spaceId));	
%>

<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:import type="css" src="/styles/schedule.css" />
<template:getSpaceCSS />
<style>
.invalidNumber {
    color: red;
}
.validNumber {
    color: black;
}
</style>

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

    var ignoreOnChangeEvents = false;
    var modified = false;
    var goSubmit = true;
    
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

    //Make sure the icon is right if the back button has been used.
    if (theForm.overallocated.value == 'true') {
        overallocationExist(true)
    }
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

function submit() {
    if(goSubmit) {
        theAction("submit");
        theForm.submit();
    }
}

function update() {
    if(goSubmit) {
        theAction("update");
        theForm.submit();
    }
}

function remove(){
    if(!verifySelection(theForm, "single", '<%=PropertyProvider.get("prm.schedule.taskview.resources.assignment.remove.noselection.error.message")%>')) return;
    theAction("deleteAssignment");
    theForm.submit();
}

function turnOnModifiedIcon() {
    var infoTable = document.getElementById("infoTable");
    infoTable.className = "informationTable";

    var modifiedIcon = document.getElementById("modifiedIcon");
    modifiedIcon.className = "tableContent";

    modified = true;
}

// Get the value in hours
function getWorkHours(workValue, workUnits) {
	if (workUnits == 4) {
		return workValue;
	}else if (workUnits == 8) {
		return workValue * 8;
	}else if (workUnits == 16) {
		return workValue * 40;
	}else { return workValue}
}

function assignPercentChanged(resourceID, timeZoneId) {
    if (ignoreOnChangeEvents) return;

    // Make sure the user knows that they've modified the page
    turnOnModifiedIcon();

    var url = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskView/AssignmentModifyPercent?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>";

    var percentValue = document.getElementById("percent_" + resourceID).value.replace(/%/g,"");
    invoke(url + "&resourceID=" + resourceID + "&timeZoneId=" + timeZoneId + "&percentValue=" + percentValue + constructMaxAllocParameters());
}

function assignmentWorkChanged(resourceID, timeZoneId) {
    if (ignoreOnChangeEvents) return;

    var url = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskView/AssignmentModifyWork?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>";

    var workAmount = document.getElementById("assignment_work_" + resourceID).value;
    var workUnitsID = getSelectedValue(document.getElementById("assignment_work_units_" + resourceID));
	var workValue = getWorkHours(parseFloat(workAmount), parseInt(workUnitsID));
	var workCompleteValue = document.getElementById("assignment_work_completehidden_" + resourceID).value;
	if (workCompleteValue > workValue) {				
		errorHandler( document.getElementById("assignment_work_" + resourceID), '<%=PropertyProvider.get("prm.schedule.taskedit.error.moreworkcomplete.message")%>');
		document.getElementById("assignment_work_" + resourceID).value = document.getElementById("assignment_workhidden_" + resourceID).value;
		setSelectedValue(document.getElementById("assignment_work_units_" + resourceID), document.getElementById("assignment_work_unitshidden_" + resourceID).value);
	} else {
		// Make sure the user knows that they've modified the page
		turnOnModifiedIcon();
		invoke(url + "&resourceID=" + resourceID + "&timeZoneId=" + timeZoneId + "&workAmount=" + workAmount + "&workUnitsID=" + workUnitsID + constructMaxAllocParameters());
	}
}

function fixOverallocations() {
    if (modified) {
    	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.schedule.taskview.resources.savebeforefixes.message")%>', function(btn) { 
			if(btn == 'yes'){ 
				theAction("overallocation");
            	theForm.submit();
			}else{
		 		return false;
			}
		});
    } else { 
        //Attempt to look for other ways to complete this task that don't cause
        //overallocation.
        self.location = '<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskView/FixOverallocations?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>';
    }
}

function showResourceAllocation(personID, startDate) {
    var url = '<%=SessionManager.getJSPRootURL()+"/resource/ResourceAllocations.jsp?module=140&personID="%>'+
        personID + '&startDate=' + startDate;

    openwin_large('resource_allocation', url);
}

function toggleExtraInfo(personID, isShow) {
    var iconClass;
    var rowClass;

    if (isShow) {
        iconClass = 'unexpandIcon';
        rowClass = 'visible';
    } else {
        iconClass = 'expandIcon';
        rowClass = 'hidden';
    }

    document.getElementById('a_' + personID).setAttribute('href', 'javascript:toggleExtraInfo(' + personID + ', ' + !isShow + ');');
    document.getElementById('icon_' + personID).className = iconClass;
    document.getElementById('extraRow_' + personID).className = 'tableContent ' + rowClass;
}

function assignmentCheckboxClicked(resourceID, timeZoneId) {
    // Make sure the user knows that they've modified the page
    turnOnModifiedIcon();

    var mode;
    var checkboxElement = document.getElementById('resource_' + resourceID);
    if (checkboxElement.checked) {
        mode = "add";
        setAvailability(resourceID, true);
    } else {
        mode = "remove";
        setAvailability(resourceID, false);
    }

    var url = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskCalculate/AssignmentAddRemove?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>";

    var responseText = invoke(url + "&resourceID=" + resourceID + "&timeZoneId=" + timeZoneId + "&mode=" + mode + constructMaxAllocParameters(resourceID));
    if(responseText.indexOf("flagError") > -1) { //if there is an error we restore back!!
        if (checkboxElement.checked) {
            checkboxElement.checked = false;
            setAvailability(resourceID, false);
        } else {
            checkboxElement.checked = true;
            setAvailability(resourceID, true);
        }
    }
}

function invoke(url) {
    // Always clear errors before invoking round-trip
    document.getElementById("errorLocationID").innerHTML = "";
    goSubmit = true;
   	var xmlRequest = new XMLRemoteRequest();
    var responseText = xmlRequest.getRemoteDocumentString(url);
    try {
        // Since we're fired by onChange events, we must ignore them while evaluating the results
        // to avoid refiring them in an infinite loop
        ignoreOnChangeEvents = true;
        eval(responseText);
    } catch (e) {
        if (responseText.match(/toggleStackTrace/) != null) {
            var errorWindow = openwin_dialog("error_window");
            errorWindow.document.write(responseText);
        } else {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.general.schedule.taskedit.error.message")%>';
			extAlert(errorTitle, errorMessage + e , Ext.MessageBox.ERROR);
        }
    }
    ignoreOnChangeEvents = false;
    return responseText;
}

<%--
   Constructs parameter list of current max allocation for all assigned resources and
   the specified resource (it may have just been unchecked)
--%>
function constructMaxAllocParameters(resourceID) {
    var parameters = "";

    if (!theForm.resource.length) {
        // Only one resource
        var maxAllocValue = document.getElementById("max_alloc_value_" + theForm.resource.value).value;
        parameters += "&max_alloc_value_" + theForm.resource.value + "=" + maxAllocValue;
    } else {
        for (var i = 0; i < theForm.resource.length; i++) {
            if (theForm.resource[i].checked || theForm.resource[i].value == resourceID) {
                // Currently assigned, or being assigned / unassigned
                var maxAllocValue = document.getElementById("max_alloc_value_" + theForm.resource[i].value).value;
                parameters += "&max_alloc_value_" + theForm.resource[i].value + "=" + maxAllocValue;
            }
        }
    }

    return parameters;
}

<%-- Invoked by eval from server-side calculations --%>
function flagError(errorText) {
    document.getElementById("errorLocationID").innerHTML += (errorText + "<br/>");
    goSubmit = false;
}
function setTaskDuration(displayValue) {
    document.getElementById("durationFormatted").innerHTML = displayValue;
}
function setTaskWork(displayValue) {
    document.getElementById("workFormatted").innerHTML = displayValue;
}
function setTaskStartDate(displayDate) {
    document.getElementById("startTimeString").innerHTML = displayDate;
}
function setTaskEndDate(displayDate) {
    document.getElementById("endTimeString").innerHTML = displayDate;
}
function setTaskWorkComplete(displayValue) {
    document.getElementById("workCompleteFormatted").innerHTML = displayValue;
}
function setAssignmentValues(resourceID, percentageAssigned, workAmount, workUnitsID, workComplete, maxPercentString, maxPercentValue, isOverallocated) {
    document.getElementById("percent_" + resourceID).value = percentageAssigned;
    setTimeQuantity(resourceID, workAmount, workUnitsID);
    document.getElementById("work_complete_" + resourceID).innerHTML = workComplete;

    if (maxPercentString != null) {
        // Change in max percent
        var maxPercentField = document.getElementById("max_allocation_" + resourceID);
        maxPercentField.innerHTML = maxPercentString;
        maxPercentField.color = (isOverallocated ? "red" : "black");
        document.getElementById("max_alloc_value_" + resourceID).value = maxPercentValue;
    }

}
function overallocationExist(exist) {
    if (exist) {
        document.getElementById('overallocatedResourcesIcon').className = 'tableContent';
        theForm.overallocated.value = 'true';
    } else {
        document.getElementById('overallocatedResourcesIcon').className = 'hidden';
        theForm.overallocated.value = 'false';
    }
}
<%-- End of eval functions --%>

function setTimeQuantity(resourceID, amount, unitsID) {
	document.getElementById("assignment_workhidden_" + resourceID).value = amount;
	document.getElementById("assignment_work_unitshidden_" + resourceID).value = unitsID;
    document.getElementById("assignment_work_" + resourceID).value = amount;
    setSelectedValue(document.getElementById("assignment_work_units_" + resourceID), unitsID);
}

<%-- Enable / disable editable items for specified resource --%>
function setAvailability(resourceID, isEnabled) {
    var isDisabled = !isEnabled;
    document.getElementById("percent_" + resourceID).disabled = isDisabled;
    document.getElementById("assignment_work_" + resourceID).disabled = isDisabled;
    document.getElementById("assignment_work_units_" + resourceID).disabled = isDisabled;
    document.getElementById("primary_owner_" + resourceID).disabled = isDisabled;
    //document.getElementById("status_" + resourceID).disabled = isDisabled;
    document.getElementById("role_" + resourceID).disabled = isDisabled;
    if(document.getElementById("chargecode_" + resourceID)){
	    document.getElementById("chargecode_" + resourceID).disabled = isDisabled;
    }
}

</script>

<style type="text/css">
    .expandIcon {
        background: url("<%= SessionManager.getJSPRootURL() %>/images/expand.gif") no-repeat;
        width: 11px;
        height: 11px;

    }

    .unexpandIcon {
        background: url("<%= SessionManager.getJSPRootURL() %>/images/unexpand.gif") no-repeat;
        width: 11px;
        height: 11px;
    }

</style>

</head>
<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="<%=scheduleEntry.getNameMaxLength40()%>"
					jspPage='<%=baseUrl + "/servlet/ScheduleController/TaskView/Assignments"%>'
					queryString='<%="module=" + net.project.base.Module.SCHEDULE + "&action=" + net.project.security.Action.MODIFY + "&id=" + id%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.schedule.taskview.modify.link")%>' function="javascript:modify();"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<% if (SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.METHODOLOGY)) { %>
<span class="errorMessage"><display:get name="prm.schedule.taskview.resources.cantassignmentresource.message"/></span>
<% } %>
<form method="post" action="<%=baseUrl%>/servlet/ScheduleController/TaskView/AssignmentsProcessing" >
	<input type="hidden" name="id" value="<%=id%>">
	<input type="hidden" name="action" value="<%= Action.VIEW%>">
	<input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
	<input type="hidden" name="theAction">
	<input type="hidden" name="refLink" value="<%=refLink%>" />
    <input type="hidden" name="overallocated" value="false"/>

<table border="0" cellpadding="0" cellspacing="0" width="97%">
	<tr class="channelHeader">
		<td width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td nowrap class="channelHeader" colspan="5"><nobr><display:get name="prm.schedule.taskview.channel.viewtask.title"/></nobr></td>
		<td align=right width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>

    <tr><td colspan="6">&nbsp;</td></tr>

	<tr align="left" valign="top">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.name.label")%>&nbsp;</td>
		<td class="tableContent">
           <jsp:getProperty name="scheduleEntry" property="name" />
		</td>
        <td></td>

        <td colspan="3" rowspan="4" valign="middle">
            <!-- Information Icons Table -->
            <table id="infoTable" border="0" class='<%=showInfoBox ? "informationTable" : "hidden"%>'>
                <tr class="hidden" id="modifiedIcon">
                    <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/schedule/after_deadline.gif" border="0"></td>
                    <td><display:get name="prm.schedule.taskview.resources.unsavedchanges.message"/></td>
                </tr>
                <tr id="criticalPathIcon" class="<%=(scheduleEntry.isCriticalPath() ? "tableContent" : "hidden")%>">
                    <td><img src="<%=SessionManager.getJSPRootURL()%>/images/schedule/critical_path.gif" border="0"></td>
                    <td><display:get name="prm.schedule.taskview.resources.oncriticalpath.message"/></td>
                </tr>
                <tr id="overallocatedResourcesIcon" class="<%=(overallocatedResourcesExist.booleanValue() ? "tableContent" : "hidden")%>">
                    <td><img src="<%=SessionManager.getJSPRootURL()%>/images/check_red.gif" border="0"></td>
                    <td><display:get name="prm.schedule.taskview.resources.overallocatedresources.message"/>&nbsp;<a href="javascript:fixOverallocations();"><display:get name="prm.schedule.taskview.resources.lookforfixes.message"/></a></td>
                </tr>
            </table>
        </td>
	</tr>
    <tr align="left" valign="top">
        <td>&nbsp;</td>
        <td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.description.label")%>&nbsp;</td>
        <td class="tableContent">
           <%= net.project.util.HTMLUtils.escape(scheduleEntry.getDescription()) %>
        </td>
        <td></td>
    </tr>
    <tr align="left" valign="top">
        <td>&nbsp;</td>
        <td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.priority.label")%>&nbsp;</td>
        <td class="tableContent"><jsp:getProperty name="scheduleEntry" property="priorityString" /></td>
        <td></td>
    </tr>
    <tr align="left" valign="top">
        <td>&nbsp;</td>
		<td class="tableHeader"><display:get name="prm.schedule.taskedit.taskcalculationtype.label" />&nbsp;</td>
		<td class="tableContent"><%=scheduleEntry.getTaskCalculationType().formatDisplay()%></td>
        <td></td>
	</tr>

    <tr><td colspan="7">&nbsp;</td></tr>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.startdate.label")%>&nbsp;</td>
		<td class="tableContent">
            <div id="startTimeString"><%=dateFormat.formatDateTime(scheduleEntry.getStartTime())%></div>
		</td>
        <td></td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.finishdate.label")%>&nbsp;</td>
		<td nowrap class="tableContent">
            <div id="endTimeString"><%=dateFormat.formatDateTime(scheduleEntry.getEndTime())%></div>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.actualstartdate.label")%>&nbsp;</td>
		<td class="tableContent">
            <%=dateFormat.formatDateTime(scheduleEntry.getActualStartTime())%>
			&nbsp;
		</td>
        <td></td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.actualfinishdate.label")%>&nbsp;</td>
		<td nowrap class="tableContent">
            <%=dateFormat.formatDateTime(scheduleEntry.getActualEndTime())%>
			&nbsp;
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.work.label")%>
		</td>
		<td class="tableContent">
            <div id="workFormatted"><%=scheduleEntry.getWorkTQ().toShortString(0,2)%></div>
		</td>
        <td></td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.workcomplete.label")%>&nbsp;</td>
		<td nowrap class="tableContent">
            <div id="workCompleteFormatted"><%=scheduleEntry.getWorkCompleteTQ().toShortString(0,2)%></div>
			&nbsp;
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.duration.label")%>
		</td>
		<td class="tableContent">
            <div id="durationFormatted"><%=scheduleEntry.getDurationTQ().toShortString(0,2)%></div>
		</td>
        <td></td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.workpercentcomplete.label")%>&nbsp;</td>
		<td nowrap class="tableContent">
            <%=scheduleEntry.getWorkPercentComplete()%>
			&nbsp;
		</td>
	</tr>

	<tr><td colspan="7">&nbsp;</td></tr>

	<tr>
		<td colspan="7">
<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.status.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView?action=1');"  />
	<tab:tab label='<%=PropertyProvider.get("prm.schedule.taskview.resources.tab")%>' href="javascript:tabClick('/servlet/ScheduleController/TaskView/Assignments?action=1');" selected="true" />
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

	<tr>
        <td colspan="7">
            <%
                if (scheduleEntry.isFromShare()) {
                    errorReporter.addWarning(PropertyProvider.get("prm.schedule.taskedit.resources.cannotassignonshared.message"));
                }
            %>
            <pnet-xml:transform xml="<%=errorReporter.getXML()%>" stylesheet="/base/xsl/error-report.xsl" />
            <%-- Provide a div for server round-trip error messaging --%>
            <div id="errorLocationID" class="errorMessage"></div>

        </td>
    </tr>
	<%if(!SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.METHODOLOGY)){ %>
    <tr><td colspan="7">
        <table width="100%" class="detailTable" border="0">
            <tr>
                <td colspan="10">
                <table cellpadding="0" cellspacing="0" width="100%">
                    <tr class="channelHeader">
                        <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
                        <td nowrap class="channelHeader"><nobr>
						<%=PropertyProvider.get("prm.resources.assignor.label")%>&nbsp;&nbsp;
						<select id="assignorId" name="assignorUser" class="assignuser-name"><%=roster.getSelectionList(user.getID())%></select>&nbsp;&nbsp;
						<%=PropertyProvider.get("prm.schedule.taskview.resources.channel.assign.title")%></nobr></td>
                        <td align=right width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
                    </tr>
                </table>
                </td>
            </tr>
            <tr class="tableHeader">
                <td colspan="2"></td>
                <td><display:get name="prm.schedule.taskview.resources.assign.person.column"/></td>
                <td align="center"><display:get name="prm.schedule.taskview.resources.assign.assigned.column"/></td>
                <td align="center"><display:get name="prm.schedule.taskview.resources.assign.work.column"/></td>
                <td align="center"><display:get name="prm.schedule.taskview.resources.assign.workcomplete.column"/></td>
                <td align="center"><display:get name="prm.schedule.taskview.resources.assign.maxallocation.column"/></td>
                <td align="center"><display:get name="prm.schedule.taskview.resources.assign.workingcalendar.column"/></td>
                <td align="center"><display:get name="prm.schedule.taskview.resources.assign.owner.column"/></td>
                <td><display:get name="prm.schedule.taskview.resources.assign.role.column"/></td>
            </tr>

            <tr><td colspan="10" class="headerSep"></td></tr>

<%
    for (Iterator it = assignmentsHelper.getAssignments().iterator(); it.hasNext();) {
        IAssignmentHelper assignment = (IAssignmentHelper)it.next();
        String personID = assignment.getPersonID();
        String disabledString = (assignment.isAssigned() ? "" : "disabled");
        String timeZoneId = assignment.getTimeZone().getID();
%>
            <tr class="tableContent">
                <input type="hidden" id="max_alloc_value_<%=personID%>" value='<%=assignment.getMaximumAllocationDecimal()%>'>

                <td>
<%       if (assignment.isAssigned()) { %>
                   <%-- The expand/unexpand button; initially in collapsed mode --%>
                    <a id="a_<%=personID%>" href="javascript:toggleExtraInfo(<%=personID%>, true);">
                        <div class="expandIcon" id="icon_<%=personID%>"></div>
                    </a>
<%       } %>
                </td>
                <td align="center">
                    <input name="resource" id="resource_<%=personID%>" value="<%=personID%>" type="checkbox" <%=assignment.getIsAssignedCheckedAttribute()%> onClick="assignmentCheckboxClicked('<%=personID%>', '<%=timeZoneId%>')" <%=(scheduleEntry.isFromShare() ? " readonly disabled=\"true\"" : "")%>>
                </td>
                <td class="assignuser-name">
                    <label for="resource_<%=personID%>"><%=assignment.getDisplayName()%></label>
                </td>
                <td align="center">
                    <input name="percent_<%=personID%>" id="percent_<%=personID%>" value='<%=assignment.getPercentAssigned()%>' type="text" maxlength="4" style="width:50px" onChange="assignPercentChanged('<%=personID%>', '<%=timeZoneId%>');" <%=disabledString%>>
                </td>
                <td align="center">
				    <input type="hidden" id="assignment_workhidden_<%=personID%>" name="assignment_workhidden_<%=personID%>" value='<%=assignment.getWorkAmountFormatted()%>'/>
    			    <input type="hidden"  id="assignment_work_unitshidden_<%=personID%>" name="assignment_work_unitshidden_<%=personID%>" value='  <%=assignment.getSelectedWorkUnit().getUniqueID()%>' />

    				<input type="text" id="assignment_work_<%=personID%>" name="assignment_work_<%=personID%>" style="width:50px" maxlength="10" onChange="assignmentWorkChanged('<%=personID%>', '<%=timeZoneId%>');" value='<%=assignment.getWorkAmountFormatted()%>' <%=disabledString%>>
    				<select id="assignment_work_units_<%=personID%>" name="assignment_work_units_<%=personID%>" onChange="assignmentWorkChanged('<%=personID%>', '<%=timeZoneId%>');" <%=disabledString%>> 
					<%=assignment.getWorkUnitOptions()%>
    				</select>
                </td>
                <td align="center">
                    <div id="work_complete_<%=personID%>"><%=assignment.getWorkComplete()%></div>
					<input type="hidden" id="assignment_work_completehidden_<%=personID%>" name="assignment_workcompletehidden_<%=personID%>" value='<%=assignment.getWorkCompleteInHours()%>'/>
                </td>
                <td align="center">
                    <div id="max_allocation_<%=personID%>"><%=assignment.getMaximumAllocation()%></div>
                </td>
                <td align="center">
                    <a href='javascript:showResourceAllocation(<%=personID%>, <%=assignment != null && !Validator.isBlankOrNull(assignment.getResourceAllocationTime()) ? assignment.getResourceAllocationTime() : String.valueOf((new Date()).getTime())%>)'>
                        <img src="<%=SessionManager.getJSPRootURL()%>/images/schedule/constraint.gif" border="0">
                    </a>
                </td>
                <td align="center">
                    <input type="radio" id="primary_owner_<%=personID%>" name="primary_owner" value="<%=personID%>" <%=assignment.getIsOwnerCheckedAttribute()%> <%=disabledString%>>
                </td>
                <td>
                    <input type="text" id="role_<%=personID%>" name="role_<%=personID%>" style="width:100px" value='<%=assignment.getRole()%>' <%=disabledString%>>
                </td>
                <% if (PropertyProvider.getBoolean("prm.global.business.managechargecode.isenabled")) {
                	String chargeCodeId = null;
	            	PnChargeCode personHasChargeCode = ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeAppliedOnPersonInSpace(Integer.valueOf(personID), spaceId);
	            	if(personHasChargeCode != null)
	            		chargeCodeId = personHasChargeCode.getCodeId().toString();
					if(chargeCodeManager.isChargeCodeAvailable()){
				 %>
                <td>
	                <select id="chargecode_<%=personID%>" name="chargecode_<%=personID%>" <%=disabledString%>>
	                    <%=chargeCodeManager.getChargeCodeHtml(chargeCodeId)%>
	                </select>
                </td>
            </tr>
            	<%}} %>
<%       if (assignment.isAssigned()) { %>
            <%-- Extra row of information; initially hidden --%>
            <tr class="tableContent hidden" id="extraRow_<%=personID%>">
                <td colspan="2"></td>
                <td colspan="8">
                    <table width="100%" cellspacing="0" cellpadding="0">
                        <tr class="tableHeader">
                            <td><display:get name="prm.schedule.taskview.resources.assign.startdate.column" /></td>
                            <td><display:get name="prm.schedule.taskview.resources.assign.enddate.column" /></td>
                        </tr>
                        <tr class="tableContent">
                            <td><%=assignment.getStartDateTime()%></td>
                            <td><%=assignment.getEndDateTime()%></td>
                        </tr>
<%
            if (assignment.isUserTimeZoneDisplayRequired()) {
%>
                        <tr class="tableContent">
                            <td><%=assignment.getStartDateTimeForUser()%></td>
                            <td><%=assignment.getEndDateTimeForUser()%></td>
                        </tr>
<%
            }
%>
                    </table>
                </td>
            </tr>
<%      } %>
            <tr><td colspan="10" class="rowSep"></td></tr>
<%
    	}
	}
%>
        </table>
    </td></tr>
  </table>
</form>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" labelToken="prm.schedule.taskview.returntotasklist.label" show="false"/>
	    <tb:button type="update" />
	    <tb:button type="submit" />
	</tb:band>
</tb:toolbar>
</div>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />
<template:import type="javascript" src="/src/dhtml/xmlrequest.js" />
<template:import type="javascript" src="/src/workCapture.js" />
</body>
</html>
<%errorReporter.clear();%>
