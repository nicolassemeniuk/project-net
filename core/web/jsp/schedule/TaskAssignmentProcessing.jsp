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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $                               1
|
|   task assignment processing page
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Task assignment processing.. omits no output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.schedule.TaskAssignmentNotification,
			net.project.security.SecurityProvider,
            net.project.security.User,
			net.project.security.Action,
            net.project.resource.AssignmentList,
            net.project.resource.AssignmentManager,
            net.project.security.SessionManager,
            net.project.util.HttpUtils,
            net.project.base.Module,
            net.project.resource.ScheduleEntryAssignment,
            java.util.Iterator,
            net.project.resource.Assignment,
            java.text.ParseException,
            net.project.util.ErrorReporter,
            net.project.base.property.PropertyProvider"
%>

<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="assignmentManager" class="net.project.resource.AssignmentManager" scope="session" />
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session"/>
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session"/>

<%
    String theAction = request.getParameter("theAction");

    String refLink = null;
    refLink = request.getParameter("refLink");

    // Check security
    if (!assignmentManager.isUserInAssignmentList(user.getID())) {
        // Check modify permission if user is not assigned scheduleEntry
        // An exception is thrown if they have no permission
        securityProvider.securityCheck(scheduleEntry.getID(), Integer.toString(net.project.base.Module.SCHEDULE), Action.MODIFY);
    }

    ErrorReporter errorReporter = new ErrorReporter();

    String[] assignees = request.getParameterValues("resource");
    int primary_owner = -1;
    if (request.getParameter("primary_owner") != null) {
        primary_owner = Integer.parseInt(request.getParameter("primary_owner"));
    }
	String assignorId = request.getParameter("assignorUser");
	if(assignorId == null)
		assignorId = user.getID();

    if (theAction.equals("submit") || theAction.equals("update")) {
        AssignmentList assignmentList = scheduleEntry.getAssignmentList();
        assignmentList.clear();

        if (assignees != null) {
            for (int i=0;i<assignees.length;i++) {
                ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
                assignment.setSpaceID(user.getCurrentSpace().getID());
                assignment.setPersonID(assignees[i]);
                assignment.setObjectID(scheduleEntry.getID());

                // Grab the % assigned value and parse it
                String percentValue = request.getParameter("percent_" + assignees[i]);
                try {
                    int percentAssigned = ScheduleEntryAssignment.parsePercentAssigned(percentValue);
                    if (ScheduleEntryAssignment.isValidPercentAssigned(percentAssigned)) {
                        assignment.setPercentAssigned(percentAssigned);
                    } else {
                        // Out of range
                        errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.percentoutofrange.message"));
                    }
                } catch (ParseException e) {
                    // Invalid number
                    errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.invalidnumber.message", percentValue));
                }
				
				assignment.setAssignorID(assignorId);
                assignment.setPersonRole(request.getParameter("role_" + assignees[i]));
                assignment.setStatusID(request.getParameter("status_" + assignees[i]));
                if (primary_owner == i) {
                    assignment.setPrimaryOwnerString("1");
                }
                assignment.setStartTime(scheduleEntry.getStartTime());
                assignment.setEndTime(scheduleEntry.getEndTime());
                assignment.setTimeZone(roster.getAnyPerson(assignment.getPersonID()).getTimeZone());

                assignmentList.addAssignment(assignment);
            }
        }

        assignmentList.recalculateAssignmentWork(scheduleEntry.getWorkTQ());
        assignmentList.store();


        //Iterate through all of the assignments to send notifications
        if (PropertyProvider.getBoolean("prm.schedule.taskassignments.notification.enabled", true)) {
            for (Iterator it = assignmentList.getAssignments().iterator(); it.hasNext();) {
                ScheduleEntryAssignment assignment = (ScheduleEntryAssignment)it.next();
                // Only send notification if user is not already in the assignment list
                if (!assignmentManager.isUserInAssignmentList(user.getID())) {
                    TaskAssignmentNotification notification = new TaskAssignmentNotification();

                    notification.initialize (scheduleEntry, assignment, user);
                    notification.attach(new net.project.calendar.vcal.VCalAttachment(scheduleEntry.getVCalendar()));
                    notification.post();
                }
            }
        }

        //Recalculating will force the start date and end date of the
        //assignments to be shown.  If this wasn't done, the start date
        //and end dates of assignments probably wouldn't be correct.
        if (schedule.isAutocalculateTaskEndpoints()) {
            schedule.recalculateTaskTimes();
        }

        //Reload the schedule entry to make sure it gets the latest stuff
        String id = scheduleEntry.getID();
        scheduleEntry.clear();
        scheduleEntry.setID(id);
        scheduleEntry.load();
    }

    String parameterString = HttpUtils.getRedirectParameterString(request);
    String rootURL = SessionManager.getJSPRootURL();

    // Decide where to go to next
    if (errorReporter.errorsFound()) {
        session.setAttribute("errorReporter", errorReporter);
        response.sendRedirect(rootURL + "/servlet/ScheduleController/TaskView/Assignments?" + parameterString);
    } else if (theAction != null && theAction.equals("update") || theAction.equals("deleteAssignment")) {
        // In the case of an "update" go back to assignments
        response.sendRedirect(rootURL + "/servlet/ScheduleController/TaskView/Assignments?" + parameterString);
    } else if (refLink != null && ! refLink.equals("")) {
        String decodedRefererLink = java.net.URLDecoder.decode(refLink, SessionManager.getCharacterEncoding());
        decodedRefererLink = rootURL + decodedRefererLink;
        response.sendRedirect(decodedRefererLink);
    } else {
        // In the case of an "update" go back to assignments
        response.sendRedirect(rootURL + "/servlet/ScheduleController/TaskView/Assignments?" + parameterString);
    }
%>
