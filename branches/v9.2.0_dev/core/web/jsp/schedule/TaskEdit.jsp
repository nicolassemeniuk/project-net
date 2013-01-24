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
    info="Task Edit"
    language="java"
    errorPage="/errors.jsp"
    import="java.net.URLDecoder,
            java.net.URLEncoder,
    		net.project.base.Module,
    		net.project.base.property.PropertyProvider,
    		net.project.calendar.CalendarBean,
            net.project.gui.html.HTMLOption,
            net.project.gui.html.HTMLOptionList,
            net.project.gui.html.IHTMLOption,
            net.project.schedule.SummaryTask,
			net.project.schedule.TaskConstraintType,
            net.project.schedule.TaskDependency,
            net.project.schedule.TaskDependencyType,
            net.project.schedule.calc.TaskCalculationType,
            net.project.security.Action,            
            net.project.security.SessionManager,
            net.project.schedule.TaskType,
            net.project.util.DateFormat,
            net.project.util.HTMLUtils,
            net.project.util.NumberFormat,
            net.project.util.TimeQuantityUnit,
            net.project.util.Validator,
            net.project.xml.XMLFormatter,
            net.project.xml.XMLUtils,
            java.util.Arrays,
            java.util.ArrayList,
            java.util.Collection,
            java.util.Date,
            java.util.Iterator,
            java.util.List,            
            org.apache.log4j.Logger,
            net.project.security.SecurityProvider,
            net.project.project.ProjectSpaceBean,
            net.project.space.Space,
            net.project.chargecode.ChargeCodeManager,
            net.project.hibernate.service.ServiceFactory,
            net.project.hibernate.model.PnChargeCode,
            org.apache.commons.collections.CollectionUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request"/>
<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="request"/>
<jsp:useBean id="action" type="java.lang.String" scope="request"/>
<jsp:useBean id="id" type="java.lang.String" scope="request"/>
<jsp:useBean id="spaceID" type="java.lang.String" scope="request"/>
<jsp:useBean id="refLink" class="java.lang.String" scope="request"/>
<jsp:useBean id="showStartEndDate" type="java.lang.Boolean" scope="request"/>
<jsp:useBean id="schedule" type="net.project.schedule.Schedule" scope="request"/>
<jsp:useBean id="isReadOnly" type="java.lang.Boolean" scope="request"/>
<jsp:useBean id="filter" type="net.project.schedule.mvc.handler.taskedit.ReadOnlyState" scope="request"/>
<jsp:useBean id="baselineID" type="java.lang.String" scope="request"/>
<jsp:useBean id="showStartEndDateWarnings" type="java.lang.Boolean" scope="request"/>
<jsp:useBean id="priorityOptions" type="java.util.Collection" scope="request"/>
<jsp:useBean id="phaseOptions" type="java.util.Collection" scope="request"/>
<jsp:useBean id="durationUnitOptions" type="java.util.Collection" scope="request" />
<jsp:useBean id="workUnitOptions" type="java.util.Collection" scope="request" />
<jsp:useBean id="workCompleteUnitOptions" type="java.util.Collection" scope="request" />

<%
    String baseUrl = SessionManager.getJSPRootURL();

    // Whether or not various fields are read only for summary tasks
    // We use readonly for input text fields, disabled for all other fields types
    // (since they don't support "readonly")
    // We could just use disabled, but "readonly" allows copy-and-paste operations
    String summaryTaskReadOnly = "";
    if (isReadOnly.booleanValue()) {
        String titleAttribute = "title=\"" + PropertyProvider.get("prm.schedule.taskedit.summarytask.readonly.message")+"\"";
        summaryTaskReadOnly = "readonly " + titleAttribute;
    }

    NumberFormat nf = NumberFormat.getInstance();
    
    boolean showPercentComplete = false;
    if (scheduleEntry.isMilestone() && scheduleEntry.getWorkTQ().getAmount().signum() == 0) {
        showPercentComplete = true;
    }

    // When creating a task, the calculation type is default from the schedule's value
    TaskCalculationType taskCalculationType = (scheduleEntry.getTaskCalculationType() == null ? schedule.getDefaultTaskCalculationType() : scheduleEntry.getTaskCalculationType());
%>

<template:getDoctype />
<html>
<head>
<% if (action.equals(String.valueOf(Action.CREATE))) { %>
		<history:history displayHere="false">
			<history:module display='<%=PropertyProvider.get("prm.schedule.main.module.history")%>'
					jspPage='<%=baseUrl + "/workplan/taskview"%>'
					queryString='<%="module="+net.project.base.Module.SCHEDULE%>' />
			<history:page display='<%=PropertyProvider.get("prm.schedule.taskcreate.new.module.history")%>' />
		</history:history>
<%	} else { %>
		<history:history displayHere="false">
			<history:module display='<%=PropertyProvider.get("prm.schedule.main.module.history")%>'
					jspPage='<%=baseUrl + "/workplan/taskview"%>'
					queryString='<%="module="+net.project.base.Module.SCHEDULE%>' />
			<history:page display="<%=scheduleEntry.getName()%>"
					jspPage='<%=baseUrl  + "/servlet/ScheduleController/TaskEdit"%>'
					queryString='<%="module=" + net.project.base.Module.SCHEDULE + "&action="+Action.VIEW + "&id=" + id%>' />
		</history:history>
<%	} %>

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:import type="css" src="/styles/schedule.css" />
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/dhtml/findDOM.js" />
<template:import type="javascript" src="/src/dhtml/xmlrequest.js" />
<template:import type="javascript" src="/src/checkRange.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
    var decimalSeparator = '<%=nf.getDecimalSeparator()%>';

    var ignoreOnChangeEvents = false;
    var startDateElementID = '<%=(showStartEndDate.booleanValue() ? "startTimeString" : "startTime_NotSubmitted")%>';
    var endDateElementID = '<%=(showStartEndDate.booleanValue() ? "endTimeString" : "endTime_NotSubmitted")%>';
    
    var goSubmit = true;

<% if (action.equals(String.valueOf(Action.CREATE))) { %>    
	// this is to avoid error when the task is beaing created and again user tries to modify it using back button
	window.history.forward(1);
<%	} %>
	
function setup() {
	load_menu('<%=spaceID%>');
	theForm = self.document.forms[0];
	
/*When we edit task, we do not allow setting a start date of a task form calender
<%--
if(request.getParameter(CalendarBean.PARAM_date) != null) {
	String rDate = request.getParameter(CalendarBean.PARAM_date);
	DateFormat formatter = user.getDateFormatter();
    Date date = formatter.parseDateString(rDate, "MMddyyyy");
	rDate = formatter.formatDate(date);
--%>
	var startTimeElement = document.getElementById(startDateElementID);
	startTimeElement.value = '<%--=rDate--%>';
	startDateEntered();
<%-- } --%>
*/
    //sjmittal invoke this method to enable disable effort drven checkbox
    changeFixedElement();
	isLoaded = true;
}

function cancel() {
	self.document.location = JSPRootURL + "<%= (refLink != null && refLink.length() > 0 ? URLDecoder.decode(refLink, SessionManager.getCharacterEncoding()) : "/workplan/taskview?module=" + net.project.base.Module.SCHEDULE)%>";
}

function checkValidData() {
    var workValue = theForm.work.value;
    var workIsZero = !isNaN(workValue) && workValue == 0;
    if (!checkMaxLength(theForm.description,1000,'<%=PropertyProvider.get("prm.schedule.taskedit.descriptionsize.message")%>')) return false;
    if (theForm.milestoneCheckbox.checked && workIsZero && !checkRangeInt(theForm.percentComplete,0,100,'<%=PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message")%>', '<%=PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message")%>',decimalSeparator)) return false;
    if (!verifyNoHtmlContent(theForm.name.value, '<%=PropertyProvider.get("prm.schedule.main.verifyname.nohtmlcontent.message")%>')) return false;
<% if (!scheduleEntry.isFromShare() && scheduleEntry.isLoaded()) { %>
    //Check to make sure the lag times are valid numbers
    var i = 1;
    var lagField;
    eval("lagField = theForm.dependency_"+i+++"_lag");
    while (lagField) {
        eval("lagField = theForm.dependency_"+i+++"_lag");
    }
<% } %>
    return true;
}

function checkRequiredData() {
    var constraintsRequiringDates = "<%=request.getAttribute("constraintsRequiringDates")%>";
     <% if (!scheduleEntry.isFromShare()) { %>
    if (constraintsRequiringDates.indexOf(theForm.constraintTypeID.value) > -1) {
    	if ( !verifyNonBlankField_withAlert(theForm.constraintDateString, '<display:get name="prm.schedule.taskedit.constraintdate.required.message"/>') ) return false;
    }
     <% } %>
	if (!checkTextbox(theForm.name,'<display:get name="prm.schedule.taskedit.name.required.message"/>')) return false;
    <% if (showStartEndDate.booleanValue()) { %>
    if ( !verifyNonBlankField_withAlert(theForm.startTimeString, '<display:get name="prm.schedule.taskedit.startdate.required.message"/>') ) return false;
    if ( !verifyNonBlankField_withAlert(theForm.endTimeString, '<display:get name="prm.schedule.taskedit.finishdate.required.message"/>') ) return false;
    <% } %>

    return true;
}

function validate() {
    if (!checkRequiredData()) return false;
    if (!checkValidData()) return false;
    return true;
}

function submit () {
	if (goSubmit){
		theAction("submit");
	
	    if (validate()){
		    theForm.submit();
		}
	}
}

function showMoreDependencies(count) {
    theForm.attributes["action"].value = '<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskEditProcessing/MoreDependencies';
    theAction("submit");
    theForm.moreDependencies.value = count;

    if (checkValidData())
        theForm.submit();
}

function recalculate() {
    theForm.attributes["action"].value = '<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskEditProcessing/Recalculate';
	theAction("submit");
	submit();
}

function replaceBaseline() {
    theForm.attributes["action"].value = '<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskEditProcessing/RefreshBaselineTask';
    theAction("submit");
    submit();
}

function addToBaseline() {
    theForm.attributes["action"].value = '<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskEditProcessing/AddTaskToBaseline';
    theForm.baselineID.value = "<%=baselineID%>";
    theAction("submit");
    submit();
}

function reset() {
<% if (action.equals(String.valueOf(Action.CREATE))) { %>
    var theLocation='<%=baseUrl + "/servlet/ScheduleController/TaskCreate?module=" + net.project.base.Module.SCHEDULE + "&action=" + net.project.security.Action.CREATE%>&refLink=<%=(refLink != null && refLink.length() > 0) ? refLink : ""%>';
	self.location = theLocation;
<% } else if(action.equals(String.valueOf(Action.VIEW))) { %> 
    var theLocation='<%=baseUrl + "/servlet/ScheduleController/TaskEdit?module=" + net.project.base.Module.SCHEDULE + "&action=" + net.project.security.Action.VIEW + "&id=" + scheduleEntry.getID()%>&refLink=<%=(refLink != null && refLink.length() > 0) ? refLink : ""%>';
	self.location = theLocation;
<%}%>
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=schedule_main&section=task_edit";
	openwin_help(helplocation);
}

function popupHelp(page) {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page="+page;
	openwin_help(helplocation);
}

function constraintTypeChange() {
    goSubmit = true;
    // Remove the warning indicators since the user specifically edited the constraint
    toggleTimeWarningHelp(true);

    var constraintsRequiringDates = "30 40 50 60 70 80 ";
    var isConstraintDateRequired = (constraintsRequiringDates.indexOf(theForm.constraintTypeID.value) > -1);
	var dependencyCount = <%= scheduleEntry != null ? scheduleEntry.getPredecessors().size() : 0 %>;
    var dom = findDOM('constraintDateCell', 0);

	if(dependencyCount > 0){ 
		dom.className = 'fieldNonRequired';
		document.getElementById("constraintDateTime_hour").disabled = true;
	    document.getElementById("constraintDateTime_minute").disabled = true;
	    document.getElementById("constraintDateTime_ampm").disabled = true;
	} else if (isConstraintDateRequired) {
        dom.className = 'fieldRequired';
        disableConstraintDateTime(false);
    } else {
        dom.className = 'fieldNonRequired';
        disableConstraintDateTime(true);

        // Now do a round-trip since no date is required
        if (!ignoreOnChangeEvents) {
            recalculateForConstraintChange();
        }
    }
}

function disableConstraintDateTime(isDisabled) {
    document.getElementById("constraintDateString").disabled = isDisabled;
    document.getElementById("constraintDateTime_hour").disabled = isDisabled;
    document.getElementById("constraintDateTime_minute").disabled = isDisabled;
    if(!isDisabled && (getSelectedValue(document.getElementById("constraintTypeID")) != document.getElementById("constraintTypeIDHidden").value)) {
        document.getElementById("constraintDateString").value = "";
        document.getElementById("constraintDateTime_hour").value = "";
        document.getElementById("constraintDateTime_minute").value = "";
    }
    var amPmElement = document.getElementById("constraintDateTime_ampm");
    if (amPmElement) {
        amPmElement.disabled = isDisabled;
        if(!isDisabled && (getSelectedValue(document.getElementById("constraintTypeID")) != document.getElementById("constraintTypeIDHidden").value)) {
            amPmElement.value = "";
        }
    }
}

function toggleTimeWarningHelp(isHidden) {
    <% if (showStartEndDateWarnings.booleanValue()) { %>
    var className = (isHidden ? "hidden" : "");
    var element1 = document.getElementById("timeWarningHelp1");
    var element2 = document.getElementById("timeWarningHelp2");
    if (element1) {
        element1.className = className;
    }
    if (element2) {
        element2.className = className;
    }
    <% } %>
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

function constraintDateChanged() {
    goSubmit = true;
    // Remove the warning indicators since the user specifically edited the constraint
    toggleTimeWarningHelp(true);

    if (!ignoreOnChangeEvents) {
        recalculateForConstraintChange();
    }
}

function workChanged() {
    goSubmit = true;
    var percentCompleteValue = document.getElementById("percentCompleteValue");
    var percentCompleteLabelCell = document.getElementById("percentCompleteLabelCell");
    var percentCompleteValueCell = document.getElementById("percentCompleteValueCell");

	var workCompleteValue = getWorkHours(parseFloat(removeSeparator(theForm.work_complete.value)),parseInt(getSelectedValue(document.getElementById("work_complete_units"))));
	var workValue = getWorkHours(parseFloat(removeSeparator(theForm.work.value)), parseInt(getSelectedValue(document.getElementById("work_units"))));
	if (workCompleteValue > workValue) {				
		errorHandler(theForm.work, '<%=PropertyProvider.get("prm.schedule.taskedit.error.moreworkcomplete.message")%>');
        theForm.work.value = document.getElementById("workHidden").value;
        theForm.work.focus();
	} else if (!ignoreOnChangeEvents) {
        recalculateForWorkChange();
    }
}

function workUnitsChanged() {
    goSubmit = true;
    // Only if user changed the value
    if (!ignoreOnChangeEvents) {
		var workCompleteValue = getWorkHours(parseFloat(removeSeparator(theForm.work_complete.value)),parseInt(getSelectedValue(document.getElementById("work_complete_units"))));
		var workValue = getWorkHours(parseFloat(removeSeparator(theForm.work.value)), parseInt(getSelectedValue(document.getElementById("work_units"))));
		if (workCompleteValue > workValue) {	
			errorHandler(theForm.work, '<%=PropertyProvider.get("prm.schedule.taskedit.error.moreworkcomplete.message")%>');
            setSelectedValue(document.getElementById("work_units"),document.getElementById("work_unitsHidden").value) ;
		} else {
			recalculateForWorkChange();
		}
    }
}

function workCompleteChanged() {
    goSubmit = true;
	var workCompleteValue = getWorkHours(parseFloat(removeSeparator(theForm.work_complete.value)),parseInt(getSelectedValue(document.getElementById("work_complete_units"))));
	var workValue = getWorkHours(parseFloat(removeSeparator(theForm.work.value)), parseInt(getSelectedValue(document.getElementById("work_units"))));
	if (workCompleteValue > workValue) {				
		errorHandler(theForm.work_complete, '<%=PropertyProvider.get("prm.schedule.taskedit.error.moreworkcomplete.message")%>');
        theForm.work_complete.value = document.getElementById("work_completeHidden").value;
        theForm.work_complete.focus();
	} else if (!ignoreOnChangeEvents) {
       	recalculateForWorkCompleteChange();
	}

    //Deal with actual start and end dates
    if (theForm.work_complete.value == '100') {
        if (theForm.actualEndDate.value == '') {
            theForm.actualEndDate.value = '<%=scheduleEntry.getActualEndDateString()%>';
        }
    } else if (theForm.work_complete.value != '0') {
        if (theForm.actualStartDate.value == "") {
            theForm.actualStartDate.value = '<%=scheduleEntry.getActualStartDateString()%>';
        }
    }
}

function workCompleteUnitsChanged() {
    goSubmit = true;
    if (!ignoreOnChangeEvents) {
		var workCompleteValue = getWorkHours(parseFloat(removeSeparator(theForm.work_complete.value)),parseInt(getSelectedValue(document.getElementById("work_complete_units"))));
		var workValue = getWorkHours(parseFloat(removeSeparator(theForm.work.value)), parseInt(getSelectedValue(document.getElementById("work_units"))));
		if (workCompleteValue > workValue) {				
			errorHandler(theForm.work_complete, '<%=PropertyProvider.get("prm.schedule.taskedit.error.moreworkcomplete.message")%>');
            setSelectedValue(document.getElementById("work_complete_units"),document.getElementById("work_complete_unitsHidden").value) ;
		} else {
			recalculateForWorkCompleteChange();
		}
    }
}

function percentCompleteChanged() {
    if(!checkRangeInt(theForm.percentComplete,0,100,'<%=PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message")%>', '<%=PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message")%>',decimalSeparator)) {
        theForm.percentComplete.focus();
        theForm.percentComplete.select();
    }
}

function workPercentCompleteChanged() {
	goSubmit = true;
    if(!checkRangeInt(theForm.work_percent_complete,0,100,'<%=PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message")%>', '<%=PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message")%>',decimalSeparator)) {
        theForm.work_percent_complete.focus();
        theForm.work_percent_complete.value = document.getElementById("work_percent_completeHidden").value;
        theForm.work_percent_complete.select();
    } else if (!ignoreOnChangeEvents) {
   	 	recalculateForWorkPercentCompleteChange();
	}
}

function doNothing() {
	event.srcElement.onchange();
	return true;
}

function milestoneChecked() {
    var percentCompleteValue = document.getElementById("percentCompleteValue");
    var percentCompleteLabelCell = document.getElementById("percentCompleteLabelCell");
    var percentCompleteValueCell = document.getElementById("percentCompleteValueCell");

    var workValue = theForm.work.value;
    var workIsZero = !isNaN(workValue) && workValue == 0;

    if (theForm.milestoneCheckbox.checked && workIsZero) {
        percentCompleteValue.disabled = false;
        percentCompleteValue.title = "";
        percentCompleteLabelCell.className = "fieldNonRequired";
        percentCompleteValueCell.className = "tableContent";
    } else {
        percentCompleteValue.value = "";
        percentCompleteValue.disabled = true;
        percentCompleteValue.title = "<%=PropertyProvider.get("prm.schedule.taskedit.percentcompletewarning.message")%>";
        percentCompleteLabelCell.className = "hidden";
        percentCompleteValueCell.className = "hidden";
    }
}

function blinkConstraintFields() {
    blinkConstraintFieldsOn();
    setTimeout("blinkConstraintFieldsOff()", 500);
    setTimeout("blinkConstraintFieldsOn()", 750);
    setTimeout("blinkConstraintFieldsOff()", 1750);
}

function blinkConstraintFieldsOn() {
    document.getElementById("constraintTypeID").className = 'tableContentHighlight';
    document.getElementById("constraintDateString").className = 'tableContentHighlight';
}

function blinkConstraintFieldsOff() {
    document.getElementById("constraintTypeID").className = '';
    document.getElementById("constraintDateString").className = '';
}

function startDateEntered() {
    goSubmit = true;
    var startTimeElement = document.getElementById(startDateElementID);
    var startTimeHiddenElement = document.getElementById("startTimeHidden");

    if (!ignoreOnChangeEvents) {
        if (!verifyConstraintChange(true)) {
            startTimeElement.value = startTimeHiddenElement.value;
            startTimeElement.focus();
            ignoreOnChangeEvents = false;
            return false;
        }

        var url = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskCalculate/DateChange?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>";
        var params = "&startDateString=" + startTimeElement.value;
        invoke(url + params + getTaskCalculationTypeParameters());

<% if (showStartEndDateWarnings.booleanValue()) { %>
        var timeWarningHelp1 = document.getElementById("timeWarningHelp1");
        if (timeWarningHelp1) {
            timeWarningHelp1.className = "";
            blinkConstraintFields();
        }
<% } %>
    }

}

function finishDateEntered() {
    goSubmit = true;
    var endTimeElement = document.getElementById(endDateElementID);
    var endTimeHiddenElement = document.getElementById("endTimeHidden");

    if (!ignoreOnChangeEvents) {
        if (!verifyConstraintChange(false)) {
            endTimeElement.value = endTimeHiddenElement.value;
            endTimeElement.focus();
            ignoreOnChangeEvents = false;
            return false;
        }

        var url = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskCalculate/DateChange?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>";
        var params = "&endDateString=" + endTimeElement.value;
        invoke(url + params + getTaskCalculationTypeParameters());
<% if (showStartEndDateWarnings.booleanValue()) { %>
        var timeWarningHelp2 = document.getElementById("timeWarningHelp2");
		if (timeWarningHelp2) {
            timeWarningHelp2.className = "";
            blinkConstraintFields();
        }
<% } %>
    }

}

<%-- Confirms a constraint change when entering a start or end date
     Confirmation only when not one of the 'default' constraints and not one of the constraints
     we would set it to anyway --%>
function verifyConstraintChange(isStartDateChanged) {
    var currentConstraintTypeID = getSelectedValue(document.getElementById("constraintTypeID"));
    if (currentConstraintTypeID != '<%=TaskConstraintType.AS_SOON_AS_POSSIBLE.getID()%>' &&
        currentConstraintTypeID != '<%=TaskConstraintType.AS_LATE_AS_POSSIBLE.getID()%>' &&
        currentConstraintTypeID != '<%=TaskConstraintType.START_NO_EARLIER_THAN.getID()%>' &&
        currentConstraintTypeID != '<%=TaskConstraintType.FINISH_NO_EARLIER_THAN.getID()%>') {
        if (isStartDateChanged) {
        	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<display:get name="prm.schedule.taskedit.constraintchange.startdate.message" />', function(btn) {         		
				return (btn == 'yes');
			 });
        } else {
        	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<display:get name="prm.schedule.taskedit.constraintchange.finishdate.message" />', function(btn) {
        		return (btn == 'yes');
        	 });
        }
    }
    return true;
}

function changeFixedElement() {
    var selectField = theForm.elements["taskCalculationTypeFixedElementID"];
    if(selectField.disabled)
        return;
    var effortDrivenCheckbox = theForm.elements["effortDriven"];
    if (selectField.options[selectField.selectedIndex].value == "<%=TaskCalculationType.FixedElement.WORK.getID()%>") {
        // Fixed Work
        effortDrivenCheckbox.checked = true;
        effortDrivenCheckbox.disabled = true;
    } else {
        // Everything else
        effortDrivenCheckbox.disabled = false;
    }
}

function durationChanged() {
    goSubmit = true;
    if (!ignoreOnChangeEvents) {
    	var workCompleteValue = getWorkHours(parseFloat(removeSeparator(theForm.work_complete.value)),parseInt(getSelectedValue(document.getElementById("work_complete_units"))));
		var workValue = getWorkHours(parseFloat(removeSeparator(theForm.duration.value)), parseInt(getSelectedValue(document.getElementById("duration_units"))));
		if (workCompleteValue > workValue) {				
			errorHandler(theForm.duration, '<%=PropertyProvider.get("prm.schedule.taskedit.error.moreworkcomplete.message")%>');
	        theForm.duration.value = document.getElementById("durationHidden").value;
	        theForm.duration.focus();
		}else{
        	recalculateForDurationChange();
        }
    }
}

function durationUnitsChanged() {
    goSubmit = true;
    if (!ignoreOnChangeEvents) {
        recalculateForDurationChange();
    }
}

function recalculateForDurationChange() {
    var url = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskCalculate/DurationChange?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>";
    var durationAmount = document.getElementById("duration").value;
    var durationUnitsID = getSelectedValue(document.getElementById("duration_units"));
    var durationParams = "&durationAmount=" + durationAmount + "&durationUnitsID=" + durationUnitsID;

    invoke(url + durationParams + getTaskCalculationTypeParameters());
    
    if((document.getElementById("duration").value == "0") && (document.getElementById("work").value == "0")) {
    	theForm.milestoneCheckbox.checked = true;
    } 
    setBoolean(theForm.milestoneCheckbox, theForm.milestone);
    milestoneChecked();
}

function recalculateForWorkChange() {
    var url = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskCalculate/WorkChange?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>";
    var workAmount = document.getElementById("work").value;
    var workUnitsID = getSelectedValue(document.getElementById("work_units"));
    var workParams = "&workAmount=" + workAmount + "&workUnitsID=" + workUnitsID;
    var workCompleteAmount = document.getElementById("work_complete").value;
    var workCompleteUnitsID = getSelectedValue(document.getElementById("work_complete_units"));
    var workCompleteParams = "&workCompleteAmount="+workCompleteAmount+"&workCompleteUnitsID="+workCompleteUnitsID;
    var workPercentCompleteParam = "&workPercentComplete="+document.getElementById("work_percent_complete").value.replace(/%/g,"");

    invoke(url + workParams + workCompleteParams + workPercentCompleteParam + getTaskCalculationTypeParameters());

    if((document.getElementById("duration").value == "0") && (document.getElementById("work").value == "0")) {
    	theForm.milestoneCheckbox.checked = true;
    } 
    setBoolean(theForm.milestoneCheckbox, theForm.milestone);
    milestoneChecked();
}

function recalculateForConstraintChange() {
    var url = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskCalculate/ConstraintChange?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>";
    var constraintDate = document.getElementById("constraintDateString").value;
    var constraintTypeID = getSelectedValue(document.getElementById("constraintTypeID"));
    //sjmittal: commented to fix bug-5470 as in the handler class we anyway set the time part.
    var params = "&constraintDateString=" + constraintDate +
                 "&constraintTypeID=" + constraintTypeID +
                 /*"&constraintDateTime_hour=" + getSelectedValue(document.getElementById("constraintDateTime_hour")) +
                 "&constraintDateTime_minute=" + getSelectedValue(document.getElementById("constraintDateTime_minute")) +
                 "&constraintDateTime_ampm=" + getSelectedValue(document.getElementById("constraintDateTime_ampm")) +*/
                 "&constraintDateTime_timeZoneID=" + document.getElementById("constraintDateTime_timeZoneID").value;
    invoke(url + params + getTaskCalculationTypeParameters());
}

function recalculateForWorkCompleteChange() {
    var url = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskCalculate/WorkCompleteChange?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>";
    var params = "&workCompleteAmount="+document.getElementById("work_complete").value+
        "&workCompleteUnitsID="+getSelectedValue(document.getElementById("work_complete_units"))+
        "&workAmount="+document.getElementById("work").value+
        "&workUnitsID="+getSelectedValue(document.getElementById("work_units"));
    invoke(url + params + getTaskCalculationTypeParameters());
}

function recalculateForWorkPercentCompleteChange() {
    var url = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/TaskCalculate/WorkPercentCompleteChange?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&id=<%=scheduleEntry.getID()%>";
    var params =
        "&workAmount=" + document.getElementById("work").value +
        "&workUnitsID=" + getSelectedValue(document.getElementById("work_units")) +
        "&workCompleteAmount="+document.getElementById("work_complete").value+
        "&workCompleteUnitsID="+getSelectedValue(document.getElementById("work_complete_units"))+
        "&workPercentComplete="+document.getElementById("work_percent_complete").value.replace(/%/g,"");
    invoke(url + params + getTaskCalculationTypeParameters());
}

function getTaskCalculationTypeParameters() {
    var taskCalculationTypeID = getSelectedValue(theForm.elements["taskCalculationTypeFixedElementID"]);
    var effortDriven = (theForm.elements["effortDriven"].checked ? "true" : "false");
    return "&taskCalculationTypeID=" + taskCalculationTypeID + "&effortDriven=" + effortDriven;
}


function invoke(url) {
    // Always clear errors before invoking round-trip
    document.getElementById("errorLocationID").innerHTML = "";
  	var xmlRequest = new XMLRemoteRequest();
    var responseText = xmlRequest.getRemoteDocumentString(url);
    try {
        // Since we're fired by onChange events, we must ignore them while evaling the results
        // to avoid refiring them in an infinite loop
        ignoreOnChangeEvents = true;
        eval(responseText);
    } catch (e) {
		var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.general.schedule.taskedit.error.message")%>';
		extAlert(errorTitle, errorMessage + e , Ext.MessageBox.ERROR);
    }
    ignoreOnChangeEvents = false;
}

<%-- Invoked by eval from server-side calculations --%>
function flagError(errorText) {
    document.getElementById("errorLocationID").innerHTML += (errorText + "<br/>");
    goSubmit = false;
}
function setDuration(amount, unitsID) {
    setTimeQuantity(document.getElementById("duration"), document.getElementById("duration_units"), amount, unitsID);
    // Now save the new values
    document.getElementById("durationHidden").value = document.getElementById("duration").value;
    document.getElementById("duration_unitsHidden").value = getSelectedValue(document.getElementById("duration_units"));
}
function setWork(amount, unitsID) {
    setTimeQuantity(document.getElementById("work"), document.getElementById("work_units"), amount, unitsID);
    document.getElementById("workHidden").value = document.getElementById("work").value;
    document.getElementById("work_unitsHidden").value = getSelectedValue(document.getElementById("work_units"));
}
function setWorkComplete(amount, unitsID) {
    setTimeQuantity(document.getElementById("work_complete"), document.getElementById("work_complete_units"), amount, unitsID);
    document.getElementById("work_completeHidden").value = document.getElementById("work_complete").value;
    document.getElementById("work_complete_unitsHidden").value = getSelectedValue(document.getElementById("work_complete_units"));
}
function setStartDate(date) {
    document.getElementById(startDateElementID).value = date;
    document.getElementById("startTimeHidden").value = document.getElementById(startDateElementID).value;
}
function setEndDate(date) {
    document.getElementById(endDateElementID).value = date;
    document.getElementById("endTimeHidden").value = document.getElementById(endDateElementID).value;
}
function setConstraint(constraintTypeID, constraintDate, constraintTimeHourValue, constraintTimeMinuteValue, constraintTimeAmPmValue) {
    setSelectedValue(document.getElementById("constraintTypeID"), constraintTypeID);
    document.getElementById("constraintDateString").value = constraintDate;

    setSelectedValue(document.getElementById("constraintDateTime_hour"), constraintTimeHourValue);
    setSelectedValue(document.getElementById("constraintDateTime_minute"), constraintTimeMinuteValue);
    var amPmElement = document.getElementById("constraintDateTime_ampm");
    if (amPmElement) {
        setSelectedValue(amPmElement, constraintTimeAmPmValue);
    }

    document.getElementById("constraintTypeIDHidden").value = getSelectedValue(document.getElementById("constraintTypeID"));
    document.getElementById("constraintDateStringHidden").value = document.getElementById("constraintDateString").value;

    // Ensure Date field is enabled / disabled
    constraintTypeChange()
}
function setWorkPercentComplete(percentComplete) {
    document.getElementById("work_percent_complete").value = percentComplete;
    document.getElementById("work_percent_completeHidden").value = document.getElementById("work_percent_complete").value;
}
<%-- End server-side invocations --%>

function setTimeQuantity(amountField, unitsField, amount, unitsID) {
    amountField.value = amount;
    setSelectedValue(unitsField, unitsID);
}

function removeSeparator(str){
	return str.replace(decimalSeparator, '.');
}

</script>

<style type="text/css">
    .nowrap {
        white-space: nowrap;
    }
</style>
</head>

<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history />
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="taskEdit" method="post" action="<%= SessionManager.getJSPRootURL() + request.getAttribute("handler")%>">
	<input type="hidden" name="theAction">
	<input type="hidden" name="id" value="<jsp:getProperty name="scheduleEntry" property="ID" />">
    <input type="hidden" name="action" value="<%= action %>">
    <input type="hidden" name="module" value="<%= net.project.base.Module.SCHEDULE %>">
	<input type="hidden" name="refLink" value="<%= (refLink != null && refLink.length() > 0 ? URLDecoder.decode(refLink, SessionManager.getCharacterEncoding()) : "/workplan/taskview?module=" + net.project.base.Module.SCHEDULE)%>" />
    <input type="hidden" name="moreDependencies" value=""/>
    <%-- This is here just in case we are modifying and the user decides to set a baseline.
    It is used in the JavaScript "addToBaseline()" function.--%>
    <input type="hidden" name="baselineID"/>

<%
String mode = PropertyProvider.get("prm.schedule.taskedit.channel.createtask.title");
if (action.equals(String.valueOf(net.project.security.Action.VIEW))) {
    mode = PropertyProvider.get("prm.schedule.taskedit.channel.edittask.title");
}
%>
<br>
<% if (scheduleEntry.isFromShare()) { %>
<table width="80%">
    <tr>
        <td><img src="<%= SessionManager.getJSPRootURL() %>/images/big_warning.gif"></td>
        <td class="tableContent">
        <display:get name="prm.schedule.taskedit.share.warning"/>
        </td>
    </tr>
</table>
<br>
<% } %>
<div align="center">
<channel:channel name="taskedit" customizable="false">
    <channel:insert name="taskedit_basic" title='<%=mode+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+PropertyProvider.get("prm.schedule.taskedit.channel.fieldsrequired.title")%>' width="97%" row="1" column="1" minimizable="false">
        <table width="100%" border="0" align="left" cellpadding="0" cellspacing="0" class="task-create-margin-top">
		<tr align="left" valign="top">
		    <td colspan="4">
            <errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="session"/>
            <%-- Provide a div for server round-trip error messaging --%>
            <div id="errorLocationID" class="errorMessage"></div>
		    </td>
		</tr>
        <tr align="left" valign="top" >
			<td nowrap class="fieldRequired"><display:get name="prm.schedule.taskedit.name.label"/>&nbsp;</td>
			<td colspan="3">
            <input:text name="name" size="40" maxLength="255" value="<%=HTMLUtils.escape(scheduleEntry.getName())%>" filter="<%=filter%>"/>
			</td>
		</tr>
		<tr align="left" valign="top">
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.description.label"/>&nbsp;</td>
			<td nowrap colspan="3">
            <input:textarea wrap="virtual" cols="50" rows="3" name="description" filter="<%=filter%>"><c:out value="${scheduleEntry.description}"/></input:textarea>
			</td>
		</tr>
		<tr align="left" valign="top">
			<td nowrap class="fieldRequired"><display:get name="prm.schedule.taskedit.priority.label"/>&nbsp;</td>
			<td nowrap>
                <input:select name="priority" options="<%=priorityOptions%>" defaultSelected="<%=scheduleEntry.getPriority()%>" filter="<%=filter%>"/>
			</td>
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.phase.label"/>&nbsp;</td>
			<td nowrap>
                <%
                    List options2 = new ArrayList(phaseOptions);
                    options2.add(0, new HTMLOption("0", PropertyProvider.get("prm.schedule.taskedit.phase.option.none.name")));
                %>
                <%-- bfd-2956 issue; name="phaseSelection" statment inserted --%>
                <input:select name="phaseSelection" options="<%=options2%>" defaultSelectedMulti="<%=scheduleEntry.getSelectedPhases()%>" filter="<%=filter%>"/>
			</td>
		</tr>
        <%-- Task Calculation Type selections --%>
		<tr align="left" valign="middle">
			<td class="fieldNonRequired">
                <display:get name="prm.schedule.taskedit.taskcalculationtype.label" />
            </td>
            <td>
                <input:select options="<%=TaskCalculationType.getFixedElementHTMLOptions()%>" defaultSelected="<%=taskCalculationType.getFixedElementHTMLOption()%>"
                        name="taskCalculationTypeFixedElementID" onChange="changeFixedElement();" disabled="<%=isReadOnly.booleanValue()%>" filter="<%=filter%>"/>
            </td>
            <td colspan="2" class="fieldNonRequired">
                <label>
                    <input:checkbox name="effortDriven" value="true" checked="<%=taskCalculationType.isEffortDriven()%>" filter="<%=filter%>"/>
                    <display:get name="prm.schedule.taskedit.effortdriven.label" />
                </label>
            </td>
		</tr>
        </table>
    </channel:insert>
    <channel:insert name="taskedit_status" titleToken="prm.schedule.taskedit.channel.status.title" width="97%" row="2" column="1" minimizable="false">
        <input type="hidden" id="startTimeHidden" value="<jsp:getProperty name="scheduleEntry" property="startTimeString" />" />
        <input type="hidden" id="endTimeHidden" value="<jsp:getProperty name="scheduleEntry" property="endTimeString" />" />
        <table width="100%" border="0" align="left" cellpadding="0" cellspacing="0" class="task-create-margin-top">
        <% if (showStartEndDate.booleanValue()) { %>
		<tr align="left" valign="middle">
			<td nowrap class="fieldRequired" width="24%"><display:get name="prm.schedule.taskedit.status.startdate.label"/>&nbsp;</td>
			<td nowrap width="25%">
                <input:text elementID="startTimeString" name="startTimeString" size="10" maxLength="10" onChange="startDateEntered();" value="<%=scheduleEntry.getStartTimeString()%>" filter="<%=filter%>"/><util:insertCalendarPopup fieldName="startTimeString" rootURL="<%=baseUrl%>"/>
			</td>
			<td nowrap class="fieldRequired" width="25%"><display:get name="prm.schedule.taskedit.status.finishdate.label"/>&nbsp;</td>
			<td nowrap width="24%">
                <input:text elementID="endTimeString" name="endTimeString" size="10" maxLength="10" onChange="finishDateEntered();" value="<%=scheduleEntry.getEndTimeString()%>" filter="<%=filter%>"/><util:insertCalendarPopup fieldName="endTimeString" rootURL="<%=baseUrl%>"/>
			</td>
		</tr>
        <% } else { %>
        <tr align="left" valign="middle">
            <td nowrap width="24%" class="fieldNonRequired"><display:get name="prm.schedule.taskedit.status.startdate.label"/>&nbsp;</td>
            <td nowrap width="25%" class="fieldNonRequired">
                <input:text elementID="startTime_NotSubmitted" name="startTime_NotSubmitted" size="10" maxLength="10" onChange="startDateEntered();" value="<%=scheduleEntry.getStartTimeString()%>" filter="<%=filter%>"/><util:insertCalendarPopup fieldName="startTime_NotSubmitted" rootURL="<%=baseUrl%>"/>
                <span nowrap id="timeWarningHelp1" class="hidden"><a href='javascript:popupHelp("task_start_end_dates");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popupwarning.gif" border="0" width="17" height="15" title='<display:get name="prm.schedule.taskedit.startendtimetooltip.message"/>'></a></span>
            </td>
            <td nowrap width="25%" class="fieldNonRequired"><display:get name="prm.schedule.taskedit.status.finishdate.label"/>&nbsp;</td>
            <td nowrap width="24%" class="fieldNonRequired">
                <input:text elementID="endTime_NotSubmitted" name="endTime_NotSubmitted" size="10" maxLength="10" onChange="finishDateEntered();" value="<%=scheduleEntry.getEndTimeString()%>" filter="<%=filter%>"/><util:insertCalendarPopup fieldName="endTime_NotSubmitted" rootURL="<%=baseUrl%>"/>
                <span nowrap id="timeWarningHelp2" class="hidden"><a href='javascript:popupHelp("task_start_end_dates");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popupwarning.gif" border="0" width="17" height="15" title='<display:get name="prm.schedule.taskedit.startendtimetooltip.message"/>'></a></span>
            </td>
        </tr>
        <% } %>
        <tr align="left" valign="middle">
            <td nowrap class="fieldNonRequired" width="24%"><display:get name="prm.schedule.taskedit.status.actualstart.label"/></td>
            <td nowrap>
                <input:text name="actualStartDate" size="10" maxLength="10" value="<%=scheduleEntry.getActualStartDateString()%>" filter="<%=filter%>"/><util:insertCalendarPopup fieldName="actualStartDate" rootURL="<%=baseUrl%>"/>
                <a href='javascript:popupHelp("task_actual_dates");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15" title='<display:get name="prm.schedule.taskedit.actualstartendtooltip.message"/>'></a>
            </td>
            <td nowrap class="fieldNonRequired" width="25%"><display:get name="prm.schedule.taskedit.status.actualend.label"/>&nbsp;</td>
            <td nowrap>
                <input:text name="actualEndDate" size="10" maxLength="10" value="<%=scheduleEntry.getActualEndDateString()%>" filter="<%=filter%>"/><util:insertCalendarPopup fieldName="actualEndDate" rootURL="<%=baseUrl%>"/>
                <a href='javascript:popupHelp("task_actual_dates");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15" title='<display:get name="prm.schedule.taskedit.actualstartendtooltip.message"/>'></a>
            </td>
        </tr>
        <%-- Duration --%>
		<tr align="left" valign="middle">
            <td class="fieldNonRequired"><display:get name="prm.schedule.taskedit.status.duration.label"/></td>
            <td>
                <%-- Hidden fields track value so we know if it really changed --%>
                <input type="hidden" id="durationHidden" value="<%=scheduleEntry.getDurationTQ().formatAmount()%>" />
                <input type="hidden" id="duration_unitsHidden" value="<%=scheduleEntry.getDurationTQ().getUnits().getUniqueID()%>" />
                <input:text elementID="duration" name="duration" size="5" maxLength="10" onChange="durationChanged();" value="<%=scheduleEntry.getDurationTQ().formatAmount()%>" filter="<%=filter%>"/>
                <input:select elementID="duration_units" name="duration_units" onChange="durationUnitsChanged();" options="<%=durationUnitOptions%>" defaultSelected="<%=scheduleEntry.getDurationTQ().getUnits()%>" filter="<%=filter%>"/>
            </td>
            <td nowrap id="percentCompleteLabelCell" class="<%=(showPercentComplete ? "fieldNonRequired" : "hidden")%>">
                <display:get name="prm.schedule.taskedit.percentcomplete.label"/>
            </td>
            <td nowrap id="percentCompleteValueCell" class="<%=(showPercentComplete ? "fieldNonRequired" : "hidden")%>">
                <input:text elementID="percentCompleteValue" name="percentComplete" onChange="percentCompleteChanged();" value="<%=String.valueOf(scheduleEntry.getPercentComplete())%>" size="4" maxLength="7" disabled="<%=showPercentComplete%>" filter="<%=filter%>"/>
            </td>
        </tr>

        <%-- Work, Work Complete --%>
		<tr align="left" valign="middle">
			<td nowrap class="fieldNonRequired" width="24%"><display:get name="prm.schedule.taskedit.status.work.label"/></td>
			<td nowrap>
                <%-- Hidden fields track value so we know if it really changed --%>
                <input type="hidden" id="workHidden" value="<%=scheduleEntry.getWorkTQ().formatAmount()%>" />
                <input type="hidden" id="work_unitsHidden" value="<%=scheduleEntry.getWorkTQ().getUnits().getUniqueID()%>" />
                <input:text elementID="work" name="work" size="5" maxLength="10" onChange="workChanged();" value="<%=scheduleEntry.getWorkTQ().formatAmount()%>" filter="<%=filter%>"/>
                <input:select elementID="work_units" name="work_units" onChange="workUnitsChanged();" options="<%=workUnitOptions%>" defaultSelected="<%=scheduleEntry.getWorkTQ().getUnits()%>" filter="<%=filter%>"/>
				<a href='javascript:popupHelp("schedule_task_times");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15" title='<display:get name="prm.schedule.taskedit.worktimetooltip.message"/>'></a>
			</td>
			<td nowrap class="fieldNonRequired" width="25%"><display:get name="prm.schedule.taskedit.status.workcomplete.label"/>&nbsp;</td>
			<td nowrap>
                <input type="hidden" id="work_completeHidden" value="<%=scheduleEntry.getWorkCompleteTQ().formatAmount()%>" />
                <input type="hidden" id="work_complete_unitsHidden" value="<%=scheduleEntry.getWorkCompleteTQ().getUnits().getUniqueID()%>" />
                <input:text elementID="work_complete" name="work_complete" size="5" maxLength="10" onBlur="workCompleteChanged();" onChange="workCompleteChanged();" value="<%=scheduleEntry.getWorkComplete()%>" filter="<%=filter%>"/>
                <input:select elementID="work_complete_units" name="work_complete_units" onChange="workCompleteUnitsChanged();" options="<%=workCompleteUnitOptions%>" defaultSelected="<%=scheduleEntry.getWorkCompleteTQ().getUnits()%>" filter="<%=filter%>"/>
				<a href='javascript:popupHelp("schedule_task_times");' border="0"><img src="<%= SessionManager.getJSPRootURL() %>/images/help/popuphelp.gif" border="0" width="17" height="15" title='<display:get name="prm.schedule.taskedit.worktimetooltip.message"/>'></a>
			</td>
        </tr>

        <%-- Milestone, % Work Complete --%>
		<tr align="left" valign="middle">
			<td nowrap class="fieldNonRequired">
                <display:get name="prm.schedule.taskedit.status.milestone.label"/>&nbsp;
            </td>
			<td nowrap>
                <input:checkbox name="milestoneCheckbox" checked="<%=scheduleEntry.isMilestone()%>" onClick="setBoolean(milestoneCheckbox, milestone); milestoneChecked();" filter="<%=filter%>"/>
				<input type="hidden" name="milestone" value="<%=scheduleEntry.isMilestone()%>" />
			</td>

            <td nowrap class="fieldNonRequired">
                <display:get name="prm.schedule.taskedit.status.workpercentcomplete.label"/>&nbsp;
            </td>
            <td nowrap>
                <input type="hidden" id="work_percent_completeHidden" value="<%=scheduleEntry.getWorkPercentComplete()%>"/>
                <input:text elementID="work_percent_complete" name="work_percent_complete" onChange="workPercentCompleteChanged();" value="<%=scheduleEntry.getWorkPercentComplete()%>" size="4" maxLength="7" filter="<%=filter%>"/>
            </td>
			<td>&nbsp;</td>
		</tr>

        <%-- Charge Codes --%>
	    <% if (PropertyProvider.getBoolean("prm.global.business.managechargecode.isenabled")) { 
	    	ChargeCodeManager chargeCodeManager = new ChargeCodeManager();
	    	Integer spaceId = Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID());
	    	chargeCodeManager.setChargeCodeList(ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeByProjectId(spaceId));
	  		if(CollectionUtils.isNotEmpty(chargeCodeManager.getChargeCodeList())){
	  			PnChargeCode chargeCode = null;
	  			if(!action.equals(String.valueOf(Action.CREATE)))
	  				chargeCode = ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeApliedOnTask(Integer.valueOf(scheduleEntry.getID()),spaceId);  
  				String chargeCodeId = "";
  				if(chargeCode != null)
	  				chargeCodeId = chargeCode.getCodeId().toString();
	  	%>
			<tr align="left" valign="middle">
				<td nowrap class="fieldNonRequired"><display:get name="prm.business.chargecode.label" />&nbsp;</td>
				<td nowrap>
	                <select id="chargecode" name="chargecode">
	                    <%=chargeCodeManager.getChargeCodeHtml(chargeCodeId)%>
	                </select>
				</td>
	            <td nowrap></td>
	            <td nowrap></td>
				<td>&nbsp;</td>
			</tr>
        <% }} %>
		<tr><td colspan="6">&nbsp;</td></tr>

        </table>
    </channel:insert>
    <% if (scheduleEntry.isFromShare()) { %>
    <channel:insert name="taskedit_crossspace" titleToken="prm.schedule.taskedit.tasksharinginfo.title" width="97%" row="3" column="1" minimizable="false">
        <table width="100%" border="0" align="left" cellpadding="0" cellspacing="0" class="task-create-margin-top">
        <tr class="fieldNonRequired">
            <td>&nbsp;</td>
            <td width="24%"><display:get name="prm.schedule.taskedit.tasksharedfrom.label"/></td>
            <td><% 
            SecurityProvider securityProvider = new SecurityProvider();
            Space testSpace = new ProjectSpaceBean();
            securityProvider.setUser(user);
        	testSpace.setID(scheduleEntry.getSharingSpaceID());
            securityProvider.setSpace(testSpace);
        	if(securityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.PROJECT_SPACE), net.project.security.Action.VIEW)){%>
                <a href="<%=SessionManager.getJSPRootURL()+"/project/Main.jsp?module="+Module.PROJECT_SPACE+"&id="+scheduleEntry.getSharingSpaceID()+"&page="+URLEncoder.encode(SessionManager.getJSPRootURL()+"/workplan/taskview?module="+Module.SCHEDULE, SessionManager.getCharacterEncoding())%>"><%=scheduleEntry.getSharingSpaceName()%></a>
            <% }else{%>
            	<%=scheduleEntry.getSharingSpaceName()%>
            <% } %>
            </td>
            <td>&nbsp;</td>
        </tr>
        </table>
    </channel:insert>
    <% } %>
    <% if (!scheduleEntry.isFromShare() && scheduleEntry.isLoaded()) { %>
    <channel:insert name="taskedit_dependencies" titleToken="prm.schedule.taskedit.channel.dependencies.title" width="97%" row="4" column="1" minimizable="false">
        <table width="100%" border="0" align="left" cellpadding="0" cellspacing="0" class="task-create-margin-top">
		<tr align="left" valign="middle">
            <td class="tableHeader"><display:get name="prm.schedule.taskedit.dependencies.taskname.column"/></td>
            <td class="tableHeader"><display:get name="prm.schedule.taskedit.dependencies.type.column"/></td>
            <td class="tableHeader"><display:get name="prm.schedule.taskedit.dependencies.lagtime.column"/></td>
        </tr>
        <tr>
			<td colspan="3"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="2" border="0"/></td>
        </tr>
<%
//First, emit all of the dependencies that the object has stored
Collection availableUnits = Arrays.asList(new TimeQuantityUnit[] { TimeQuantityUnit.HOUR, TimeQuantityUnit.DAY, TimeQuantityUnit.WEEK });
Iterator it = scheduleEntry.getPredecessors().iterator();
List dependencyExclusions = schedule.getTaskList().getHierarchyTree(scheduleEntry.getID());
List taskNameOptions = schedule.getTaskNameOptionList(dependencyExclusions, true);
List dependencyTypeOptions = TaskDependencyType.getDependencyTypeOptionList();
int i = 0;
while (it.hasNext()) {
    TaskDependency td = (TaskDependency)it.next();
	String dependTask = "dependency_"+(++i)+"_task_id";
	String dependType = "dependency_"+i+"_type";
	String dependLag = "dependency_"+i+"_lag";
	String dependLagUnit = "dependency_"+i+"_lag_units";
%>
        <tr align="left" valign="middle">
            <td class="tableHeader"><input:select name="<%=dependTask%>" options="<%=taskNameOptions%>" defaultSelected="<%=td.getDependencyID()%>" filter="<%=filter%>"/></td>
            <td class="tableHeader"><input:select name="<%=dependType%>" options="<%=dependencyTypeOptions%>" defaultSelected="<%=td.getDependencyType()%>" filter="<%=filter%>"/></td>
            <td class="tableHeader">
                <input:text name="<%=dependLag%>" size="3" maxLength="5" value="<%=nf.formatNumber(td.getLag().getAmount().doubleValue())%>" filter="<%=filter%>"/>
                <input:select options="<%=availableUnits%>" defaultSelected="<%=td.getLag().getUnits()%>" name="<%=dependLagUnit%>" filter="<%=filter%>"/>
            </td>
        </tr>
<%

}

//Create as many dependency objects as needed to display all the dependencies the
//scheduleEntry has as well as 3 additional ones to allow users to add additional dependencies.
int extraDependencyCount = 0;
if (request.getAttribute("dependencies") != null) {
    extraDependencyCount = Integer.parseInt((String)request.getAttribute("dependencies"));
} else {
    extraDependencyCount = 3;
}
for (int j = i+1; j < extraDependencyCount+i+1; j++) {
	String dependTask = "dependency_"+j+"_task_id";
	String dependType = "dependency_"+j+"_type";
	String dependLag = "dependency_"+j+"_lag";
	String dependLagUnit = "dependency_"+j+"_lag_units";
%>
        <tr align="left" valign="middle">
            <td class="tableHeader"><input:select name="<%=dependTask%>" options="<%=taskNameOptions%>" filter="<%=filter%>"/></td>
            <td class="tableHeader"><input:select name="<%=dependType%>" options="<%=dependencyTypeOptions%>" filter="<%=filter%>"/></td>
            <td class="tableHeader">
            <!-- Avinash: Check case of letter 'L' in Attribute MAXLENGTH   -->   
           	  <input:text name="<%=dependLag%>" size="3" maxLength="5" filter="<%=filter%>"/>	
              <input:select name="<%=dependLagUnit%>" options="<%=availableUnits%>" filter="<%=filter%>" defaultSelected="<%=TimeQuantityUnit.DAY%>"/>
            </td>
        </tr>
<%
} //for loop
%>
        <tr align="left" valign="middle">
			<td colspan="3"><a href="javascript:showMoreDependencies(<%=extraDependencyCount+3%>);"><font size="-1"><display:get name="prm.schedule.taskedit.dependencies.showmore.link"/></font></a></td>
        </tr>
        </table>
    </channel:insert>
    <%
    } //not fromShare and loaded condition
    %>
    <% if (!scheduleEntry.isFromShare()) { %>
    <channel:insert name="taskedit_constraints" titleToken="prm.schedule.taskedit.channel.constraints.title" width="97%" row="5" column="1" minimizable="false" >
        <table width="100%" border="0" align="left" cellpadding="0" cellspacing="0" class="task-create-margin-top" >
		<tr align="left" valign="middle">
            <td class="fieldRequired"><display:get name="prm.schedule.taskedit.constraints.type.label"/></td>
            <td class="tableContent">
                <input type="hidden" id="constraintTypeIDHidden" value="<%=scheduleEntry.getConstraintType().getID()%>" />
                <% if(scheduleEntry.getPredecessors().size() == 0) { %>
	                <select id="constraintTypeID" name="constraintTypeID" onChange="constraintTypeChange()">
	                    <%=TaskConstraintType.getHTMLOptionList(scheduleEntry.getConstraintType(), (scheduleEntry instanceof SummaryTask))%>
	                </select>
                <%} else { %>
                	<select id="constraintTypeID" name="constraintTypeID" onChange="constraintTypeChange()" disabled="disabled">
	                    <%=TaskConstraintType.getHTMLOptionList(scheduleEntry.getConstraintType(), (scheduleEntry instanceof SummaryTask))%>
	                </select>
                <%}%>
            </td>
        </tr>
<%
    boolean isConstraintDateDisabled = !scheduleEntry.getConstraintType().isDateConstrained();
%>
		<tr align="left" valign="middle">
            <td id="constraintDateCell" class="fieldNonRequired"><display:get name="prm.schedule.taskedit.constraints.date.label"/></td>
            <td class="tableContent" colspan="3">
                <input type="hidden" id="constraintDateStringHidden" value="<%=scheduleEntry.getConstraintDateString()%>" />
                 <% if(scheduleEntry.getPredecessors().size() == 0) { %>
 	        		<input type="text" name="constraintDateString" id="constraintDateString" size="10" maxlength="20" value="<%=scheduleEntry.getConstraintDateString()%>" onChange="constraintDateChanged()" <%=isConstraintDateDisabled ? "disabled" : ""%> />
                 	<util:insertCalendarPopup fieldName="constraintDateString" rootURL="<%=baseUrl%>"/>
                	<input:time name="constraintDateTime" elementID="constraintDateTime" time="<%=scheduleEntry.getConstraintDate()%>" isIncludeTimeZone="true" timeZone="<%=schedule.getTimeZone()%>" isOptional="true" disabled="<%=isConstraintDateDisabled%>" />
           		<%} else { %>	
           				<input type="text" name="constraintDateString" id="constraintDateString" size="10" maxlength="20" value="<%=scheduleEntry.getConstraintDateString()%>" onChange="constraintDateChanged()" readonly="readonly"/>
	                	<img src="<%= SessionManager.getJSPRootURL()%>/images/calendar.gif" width="16" align="absmiddle" height="16" border="0">
	                	<span class="calendarPopup"><%= SessionManager.getUser().getDateFormatter().getDateFormatExample() %></span>
	                	<input:time name="constraintDateTime" elementID="constraintDateTime" time="<%=scheduleEntry.getConstraintDate()%>" isIncludeTimeZone="true" timeZone="<%=schedule.getTimeZone()%>" isOptional="true" disabled="true" />
           		<%}%>
			</td>
        </tr>
		<tr align="left" valign="middle">
			<td class="fieldNonRequired"><display:get name="prm.schedule.taskedit.constraints.deadline.label"/></td>
			<td class="tableContent" colspan="3">
				<% if(scheduleEntry.getPredecessors().size() == 0) { %>
		                <input name="deadlineString" size="10" maxlength="20" value="<%=scheduleEntry.getDeadlineString()%>">
		                <util:insertCalendarPopup fieldName="deadlineString" rootURL="<%=baseUrl%>"/>
		                <input:time name="deadlineTime" time="<%=scheduleEntry.getDeadline()%>" isIncludeTimeZone="true" timeZone="<%=schedule.getTimeZone()%>" isOptional="true" />
                <%} else { %>
		                <input name="deadlineString" size="10" maxlength="20" value="<%=scheduleEntry.getDeadlineString()%>" readonly="readonly">
		                <img src="<%= SessionManager.getJSPRootURL()%>/images/calendar.gif" width="16" align="absmiddle" height="16" border="0">
		                <span class="calendarPopup"><%= SessionManager.getUser().getDateFormatter().getDateFormatExample() %></span>
		                <input:time name="deadlineTime" time="<%=scheduleEntry.getDeadline()%>" isIncludeTimeZone="true" timeZone="<%=schedule.getTimeZone()%>" isOptional="true" disabled="true"/>	
                <%}%>
            </td>
		</tr>
        </table>
    </channel:insert>
    <%
    } //not fromShare condition
    %>
    <channel:insert name="taskedit_calculated" titleToken="prm.schedule.taskedit.channel.calculated.title" width="97%" row="6" column="1" minimizable="false">
        <% if (scheduleEntry.getBaselineID() != null) { %>
        <channel:button type="recalculate" labelToken="prm.schedule.taskedit.replacebaselinewithcurrent.label" href="javascript:replaceBaseline();" />
        <% } else if (scheduleEntry.isLoaded() && baselineID != null) { %>
        <channel:button type="recalculate" labelToken="prm.schedule.taskedit.addtocurrentbaseline.label" href="javascript:addToBaseline();"/>
        <% } %>

        <table width="100%" border="0" align="left" cellpadding="0" cellspacing="0" class="task-create-margin-top">
		<tr align="left" valign="middle">
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.baselinestartdate.label"/>&nbsp;</td>
			<td nowrap class="fieldNonRequired"><%=DateFormat.getInstance().formatDateTime(scheduleEntry.getBaselineStart())%></td>
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.startvariance.label"/>&nbsp;</td>
			<td nowrap class="fieldNonRequired"><%=scheduleEntry.getStartDateVariance(schedule.getWorkingTimeCalendarProvider())%></td>
		</tr>
		<tr align="left" valign="middle">
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.baselineenddate.label"/>&nbsp;</td>
			<td nowrap class="fieldNonRequired"><%=DateFormat.getInstance().formatDateTime(scheduleEntry.getBaselineEnd())%></td>
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.endvariance.label"/>&nbsp;</td>
			<td nowrap class="fieldNonRequired"><%=scheduleEntry.getEndDateVariance(schedule.getWorkingTimeCalendarProvider())%></td>
		</tr>
		<tr align="left" valign="middle">
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.baselinework.label"/>&nbsp;</td>
			<td nowrap class="fieldNonRequired"><c:out value="${scheduleEntry.formattedBaselineWork}"/></td>
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.workvariance.label"/>&nbsp;</td>
			<td nowrap class="fieldNonRequired"><jsp:getProperty name="scheduleEntry" property="workVarianceString" /></td>
		</tr>
		<tr align="left" valign="middle">
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.baselineduration.label"/>&nbsp;</td>
			<td nowrap class="fieldNonRequired"><c:out value="${scheduleEntry.baselineDurationString}"/></td>
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.durationvariance.label"/>&nbsp;</td>
			<td nowrap class="fieldNonRequired"><jsp:getProperty name="scheduleEntry" property="durationVarianceString"/></td>
		</tr>
       <tr><td colspan="6">&nbsp;</td></tr>
		<tr align="left" valign="middle">
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.percentcomplete.label"/>&nbsp;</td>
			<td nowrap class="fieldNonRequired"><c:out value="${scheduleEntry.workPercentComplete}"/></td>
			<td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.fromcomplete.message"/></td>
		</tr>
		<tr align="left" valign="middle">
			<td nowrap class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.duration.label"/>&nbsp;</td>
			<td nowrap class="fieldNonRequired"><c:out value="${scheduleEntry.durationFormatted}"/></td>
			<td nowrap colspan="2" class="fieldNonRequired"><display:get name="prm.schedule.taskedit.calculated.fromdates.message"/></td>
		</tr>
        </table>
    </channel:insert>
</channel:channel>
</div>

<% if (scheduleEntry.isLoaded()) { %>
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action">
        <%-- recalculate on this page was required when we had old page which was not doing any ajax calls on change of values. Now recalculate is not required. 
        <tb:button type="update" label='<%=PropertyProvider.get("prm.schedule.taskedit.recalculate.button.label")%>' function="javascript:recalculate();" />  --%>
        <tb:button type="submit" />
        <tb:button type="cancel" />
    </tb:band>
</tb:toolbar>
<% } else { %>
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action">
       <tb:button type="submit" />
    </tb:band>
</tb:toolbar>
<% } %>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

<% if (!Validator.isBlankOrNull("id")) { %>
<script language="javascript">
self.document.forms[0].name.focus();
</script>
<% } %>


