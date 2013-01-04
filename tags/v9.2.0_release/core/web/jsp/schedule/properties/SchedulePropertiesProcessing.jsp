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
    import="net.project.util.Validator,
            java.util.Date,
            java.util.List,
            net.project.util.ErrorDescription,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.security.Action,
            net.project.schedule.TaskEndpointCalculation,
            net.project.schedule.ScheduleEntry,
            net.project.calendar.TimeBean,
            java.util.TimeZone,
            net.project.schedule.calc.TaskCalculationType,
            net.project.schedule.TaskConstraintType"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>

<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.SCHEDULE%>" />

<%
    String scheduleName = request.getParameter("scheduleName");
    if (Validator.isBlankOrNull(scheduleName)) {
        errorReporter.addError(new ErrorDescription("scheduleName", PropertyProvider.get("prm.schedule.properties.name.required.message")));
    }

    String scheduleStartParameter = request.getParameter("scheduleStart");
    Date scheduleStart = null;
    if (Validator.isBlankOrNull(scheduleStartParameter)) {
        errorReporter.addError(new ErrorDescription("scheduleStart", PropertyProvider.get("prm.schedule.properties.startdaterequired.message")));
    } else {
        try {
            scheduleStart = TimeBean.parseDateTime(request, "scheduleStart", "scheduleStart");

        } catch (net.project.util.InvalidDateException e) {
            errorReporter.addError(new ErrorDescription("scheduleStart", PropertyProvider.get("prm.schedule.properties.invalidstartdate")));
        }
    }

    String scheduleTimeZoneID = request.getParameter("scheduleTimeZoneID");
    TimeZone scheduleTimeZone = null;
    if (Validator.isBlankOrNull(scheduleTimeZoneID)) {
        throw new IllegalStateException("Missing request parameter 'scheduleTimeZoneID'");
    } else {
        scheduleTimeZone = TimeZone.getTimeZone(scheduleTimeZoneID);
        if (!scheduleTimeZone.getID().equals(scheduleTimeZoneID)) {
            // If time zone for ID cannot be found, Java returns the default
            // This isn't acceptable; it is an error to select a time zone that doesn't exist
            throw new IllegalStateException("Could not find time zone for ID " + scheduleTimeZoneID + "; found " + scheduleTimeZone.getID() + " instead");
        }
    }

    String autoCalculate = request.getParameter("autocalculation");
    boolean autoCalculateBoolean = autoCalculate != null && autoCalculate.equals("true");

    String onlyshiftworkplan = request.getParameter("onlyshiftworkplan");
    //sjmittal: fix for bug 80
    // not allow shifting in there is a shared task.
    if (!Validator.isBlankOrNull(onlyshiftworkplan) && onlyshiftworkplan.equals("true")) {
        if(!schedule.isLoaded()) {
            schedule.loadAll();
        } else if(schedule.getTaskList().size() == 0) {
            schedule.loadEntries();
        }
        List tasks = schedule.getEntries();
        for(int i = 0; i < tasks.size(); i++) {
            ScheduleEntry task = (ScheduleEntry) tasks.get(i);
            if(task.isFromShare()) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.properties.sharedtask.message"));
                break;
            }
        }
    }

    /*if (autoCalculateBoolean && scheduleStart == null) {
        errorReporter.addError(PropertyProvider.get("prm.schedule.properties.startdaterequired.message"));
    }*/

    // Schedule default task calculation type
    String defaultTaskCalculationTypeFixedElementID = request.getParameter("defaultTaskCalculationTypeFixedElementID");
    String newTaskEffortDriven = request.getParameter("newTaskEffortDriven");
    boolean isNewTaskEffortDriven = (newTaskEffortDriven != null && newTaskEffortDriven.equals("true"));
    // Construct the calculation type from the fixed element and boolean
    TaskCalculationType taskCalculationType = TaskCalculationType.makeFromComponents(TaskCalculationType.FixedElement.forID(defaultTaskCalculationTypeFixedElementID), isNewTaskEffortDriven);
    TaskConstraintType startConstraintType = TaskConstraintType.getForID(request.getParameter("scheduleStartConstraint"));
	boolean inlineEditingWarning = "true".equals(request.getParameter("editingWarning"));
	boolean unAssignedWorkcapture = "true".equals(request.getParameter("unAssignedWorkcapture"));
	String resourceCalendar = request.getParameter("resourceCalendar");
    if (!errorReporter.errorsFound()) {
        if (!Validator.isBlankOrNull(onlyshiftworkplan) && onlyshiftworkplan.equals("true")) {
            schedule.shiftWorkplan(scheduleStart, autoCalculate != null && autoCalculate.equals("true"));
          
            response.sendRedirect(SessionManager.getJSPRootURL()+"/schedule/properties/ScheduleProperties.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY);
        } else {
            schedule.setName(scheduleName);
            schedule.setScheduleStartDate(scheduleStart);
            schedule.setTimeZone(scheduleTimeZone);
            schedule.setAutocalculateTaskEndpoints(autoCalculate != null && autoCalculate.equals("true"));
            schedule.setDefaultTaskCalculationType(taskCalculationType);
            schedule.setStartConstraint(startConstraintType);
            schedule.setEditingWarning(inlineEditingWarning);
            schedule.setUnAssignedWorkcapture(unAssignedWorkcapture);
            schedule.setResourceCalendar(resourceCalendar);
            
            schedule.store();

            if (schedule.isAutocalculateTaskEndpoints()) {
                schedule.recalculateTaskTimes();
            }

            String onlyRecalculate = request.getParameter("onlyrecalculate");
            if (!Validator.isBlankOrNull(onlyRecalculate) && onlyRecalculate.equals("true")) {
                response.sendRedirect(SessionManager.getJSPRootURL()+"/schedule/properties/ScheduleProperties.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY);
            } else {
                response.sendRedirect(SessionManager.getJSPRootURL()+"/workplan/taskview?module="+Module.SCHEDULE);
            }
        }
    } else {
        response.sendRedirect(SessionManager.getJSPRootURL()+"/schedule/properties/ScheduleProperties.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY);
    }

%>
