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
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            java.util.Date,
            java.util.List,
            net.project.calendar.PnCalendar,
            java.util.Iterator,
            net.project.schedule.IScheduleEntry,
            net.project.xml.XMLFormatter,
            net.project.base.Module,
            net.project.security.Action,
            net.project.gui.html.HTMLOptionList,
            net.project.schedule.calc.TaskCalculationType,
            net.project.schedule.TaskDependency,
            net.project.schedule.TaskConstraintType,
            net.project.schedule.SchedulePropertiesHelper"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>
<jsp:useBean id="schedulePropertiesHelper" class="net.project.schedule.SchedulePropertiesHelper" scope="page" />

<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.SCHEDULE%>" />

<%
    schedulePropertiesHelper.init(request);
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />

<script language="javascript" type="text/javascript">
    var theForm;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';

function setup() {
    theForm = self.document.forms[0];
}

function cancel() {
    self.document.location = JSPRootURL + '/workplan/taskview?action=<%=Action.VIEW%>&module=<%=Module.SCHEDULE%>';
}

function reset() {
    theForm.onlyrecalculate.value = "true";
    submit();
}

function shiftSchedule() {
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.schedule.properties.shiftschedule.message")%>', function(btn) {
		if(btn == 'yes') {
			theForm.onlyshiftworkplan.value = "true";
			submit();
		} 
	});
}

function submit() {
    if(validateForm(theForm)) {
        theForm.submit();
    }
}
function validateForm(theForm) {
    if (!checkTextbox(theForm.scheduleName,'<%=PropertyProvider.get("prm.schedule.properties.name.required.message")%>')) return false;
    return true;
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=schedule_properties";
	openwin_help(helplocation);
}

function changeFixedElement() {
    var selectField = theForm.elements["defaultTaskCalculationTypeFixedElementID"];
    var newTaskEffortDrivenCheckbox = theForm.elements["newTaskEffortDriven"];
    if (selectField.options[selectField.selectedIndex].value ==
            "<%=TaskCalculationType.FixedElement.WORK.getID()%>") {
        // Fixed Work
        newTaskEffortDrivenCheckbox.checked = true;
        newTaskEffortDrivenCheckbox.disabled = true;
    } else {
        // Everything else
        newTaskEffortDrivenCheckbox.disabled = false;
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
			<history:module display='<%=PropertyProvider.get("prm.schedule.properties.pagetitle")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/schedule/properties/ScheduleProperties.jsp" %>'
					queryString='<%="module="+net.project.base.Module.SCHEDULE+"&action="+Action.MODIFY%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>

<tab:tabStrip width="97%">
    <tab:tab labelToken='prm.schedule.properties.pagetitle' href='<%=SessionManager.getJSPRootURL() + "/schedule/properties/ScheduleProperties.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY %>' selected="true"/>
    <tab:tab labelToken='prm.schedule.properties.changeworkingtimes.link' href='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/WorkingTime/List?module="+Module.SCHEDULE+"&action="+Action.VIEW%>'/>
    <tab:tab labelToken='prm.schedule.properties.history.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/ScheduleHistory.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.properties.baseline' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/Baseline/List?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.tasklistdecorating.pagetitle' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/TaskListDecorating.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <display:if name="@prm.crossspace.isenabled">
        <tab:tab labelToken='prm.schedule.properties.sharing' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/ScheduleProperties/Sharing?module="+Module.SCHEDULE+"&action="+Action.SHARE%>'/>
    </display:if>
    <tab:tab labelToken='prm.schedule.properties.tools.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/Tools.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
</tab:tabStrip>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/schedule/properties/SchedulePropertiesProcessing.jsp">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.MODIFY%>">
    <input type="hidden" name="onlyrecalculate" value="false">
    <input type="hidden" name="onlyshiftworkplan" value="false">

<table border="0" width="100%">
<% if (errorReporter.errorsFound()) { %>
<tr><td colspan="2">
    <errors:show />
</td></tr>
<% } %>
<%-- Schedule name --%>
<tr>
    <td class="tableHeader" width="20%"><display:get name="prm.schedule.properties.schedulename.label"/></td>
    <td class="tableContent">
        <input type="text" name="scheduleName" size="20" maxlength="80" value='<jsp:getProperty name="schedule" property="name"/>'>
    </td>

<tr><td colspan="2">&nbsp;</td></tr>

<%-- Start Datetime --%>
<tr>
    <td class="tableHeader" width="20%"><display:get name="prm.schedule.properties.schedulestartdate.label"/></td>
    <td class="tableContent">
        <%--  TODO:  schedule start date being set from a shared task or schedule is not working yet.   Hard coding TaskConstraintType = MUST_START_ON for now. --%>
        <input type="hidden" name="scheduleStartConstraint" value="50">
    <%--
        <display:if name="@prm.crossspace.isenabled">
        <input type="radio" name="scheduleStartConstraint" value="50"<%=schedule.getStartConstraint().equals(TaskConstraintType.MUST_START_ON) ? " checked" : ""%>>Schedule must start on
        </display:if>
    --%>
        <input type="text" name="scheduleStart" size="10" maxlength="10" value='<jsp:getProperty name="schedulePropertiesHelper" property="startDateFormatted" />'>
        <input:time name="scheduleStart" time="<%=schedulePropertiesHelper.getStartDate()%>" isIncludeTimeZone="true" timeZone="<%=schedulePropertiesHelper.getDefaultTimeZone()%>" />
        <util:insertCalendarPopup fieldName="scheduleStart" />&nbsp;
        <a href="javascript:shiftSchedule();"><display:get name="prm.schedule.properties.shiftschedule.label" /></a>
    </td>
</tr>

<%--  TODO:  schedule start date being set from a shared task or schedule is not working yet. --%>
<%--
<display:if name="@prm.crossspace.isenabled">
<tr>
    <td></td>
    <td class="tableContent">
        <input type="radio" name="scheduleStartConstraint" value="10"<%=schedule.getStartConstraint().equals(TaskConstraintType.AS_SOON_AS_POSSIBLE) ? " checked" : ""%>>Schedule starts as soon as these tasks or schedules are complete:
    </td>
</tr>
<tr>
    <td></td>
    <td class="tableContent" style="padding-left:20px">
        <table style="border: thin solid #CCCCFF" width="50%">
            <tbody id="dependencies">
            <tr class="tableHeader">
                <td>Object Name</td>
                <td>Type</td>
                <td>Space Name</td>
                <!--<td width="1%"><a href="javascript:findDependencies()"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-create_on.gif" border="0"></a></td>-->
            </tr>
            <tr>
                <td colspan="3" class="headerSep"></td>
            </tr>
            <%
                for(Iterator it = schedule.getPredecessorList().iterator(); it.hasNext(); ) {
                    TaskDependency td =(TaskDependency) it.next();
                %>
            <tr>
                <td><%=td.getTaskName()%></td>
                <td>Task</td>
                <td></td>
            </tr>
                <%
                }
            %>
            </tbody>
        </table>
    </td>
</tr>
</display:if>
--%>

<%-- End Date Time --%>
<tr>
    <td class="tableHeader"><display:get name="prm.schedule.properties.scheduleenddate.label"/></td>
    <td class="tableContent"><jsp:getProperty name="schedulePropertiesHelper" property="endDateFormatted" /></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<%-- Timezone --%>
<tr>
    <td class="tableHeader"><display:get name="prm.schedule.properties.scheduletimezone.label"/></td>
    <td class="tableContent">
        <select name="scheduleTimeZoneID">
            <jsp:getProperty name="schedulePropertiesHelper" property="timeZoneOptions" />
        </select>
    </td>
</tr>
<tr class="tableContent">
    <td>&nbsp;</td>
    <td>
        <display:get name="prm.schedule.properties.scheduletimezone.instructions"/>
    </td>
</tr>
<%-- Default Task Calculation Type "Fixed" element, New Tasks Effort Driven --%>
<tr>
    <td class="tableHeader">
        <display:get name="prm.schedule.properties.defaulttaskcalculationtype.label"/>
    </td>
    <td class="tableContent">
        <input:select options="<%=TaskCalculationType.getFixedElementHTMLOptions()%>" defaultSelected="<%=schedulePropertiesHelper.getDefaultTaskCalculationTypeFixedElement()%>"
                name="defaultTaskCalculationTypeFixedElementID" onChange="changeFixedElement();"/>
        &nbsp;
        <label>
            <input type="checkbox" name="newTaskEffortDriven" value="true" <jsp:getProperty name="schedulePropertiesHelper" property="checkedNewTasksEffortDriven" /> <jsp:getProperty name="schedulePropertiesHelper" property="disabledNewTasksEffortDriven" /> >
            <display:get name="prm.schedule.properties.newtaskeffortdriven.label"/>
        </label>
    </td>
</tr>
<%-- Resource working ime calendar --%>
<tr>
    <td class="tableHeader">
        <display:get name="prm.schedule.properties.resourcecalendar.label"/>
    </td>
    <td class="tableContent">
    	<input type="radio" name="resourceCalendar" value="<%=SchedulePropertiesHelper.PERSONAL_RESOURCE_CALENDAR %>" <jsp:getProperty name="schedulePropertiesHelper" property="checkedPersonalResourceCalendar" />>&nbsp; 
    	<display:get name="prm.schedule.properties.resourcecalendar.personaloption.label"/>
    	&nbsp;&nbsp;
    	<input type="radio" name="resourceCalendar" value="<%=SchedulePropertiesHelper.SCHEDULE_RESOURCE_CALENDAR %>" <jsp:getProperty name="schedulePropertiesHelper" property="checkedScheduleResourceCalendar" />>&nbsp; 
        <display:get name="prm.schedule.properties.resourcecalendar.scheduleoption.label"/>
    </td>
</tr>

<%-- Auto Calculate checkbox --%>
<tr>
    <td colspan="2" class="tableContent">
        <label>
        <input type="checkbox" name="autocalculation" value="true" <jsp:getProperty name="schedulePropertiesHelper" property="checkedAutocalculateTaskEndpoints" />>
        <display:get name="prm.schedule.properties.autocalculate.label"/>
        </label>
    </td>
</tr>
<%-- enable inline editing warning message checkbox --%>
<tr>
    <td colspan="2" class="tableContent">
        <label>
        <input type="checkbox" name="editingWarning" value="true" <jsp:getProperty name="schedulePropertiesHelper" property="checkedEditingWarning" />>
        <display:get name="prm.schedule.properties.inlineeditwarning.label"/>
        </label>
    </td>
</tr>
<%-- Enable Un-assigned task work capture --%>
<tr>
    <td colspan="2" class="tableContent">
        <label>
        <input type="checkbox" name="unAssignedWorkcapture" value="true" <jsp:getProperty name="schedulePropertiesHelper" property="checkedUnAssignedWorkcapture" />>
        <display:get name="prm.schedule.properties.unassignedworkcapture.label"/>
        </label>
    </td>
</tr>


</table>

<%-- Toolbar --%>
<tb:toolbar style="action" showLabels="true" width="100%" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="reset" labelToken="prm.schedule.properties.recalculate.label" />
		<tb:button type="submit"/>
		<tb:button type="cancel"/>
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
