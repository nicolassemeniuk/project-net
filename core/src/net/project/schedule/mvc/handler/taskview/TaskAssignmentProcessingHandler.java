/* 
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
*/

 package net.project.schedule.mvc.handler.taskview;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.notification.DeliveryException;
import net.project.notification.NotificationException;
import net.project.resource.RosterBean;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskAssignmentNotification;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.ErrorReporter;

/**
 * Provides a handler for processing task assignments.
 *
 * @author Tim Morrow
 * @since Version 7.7.1
 */
public class TaskAssignmentProcessingHandler extends AbstractTaskAssignmentHandler {

    public TaskAssignmentProcessingHandler(HttpServletRequest request) {
        super(request);
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // We specifically do not call the super.handleRequest() method since it reloads the
        // schedule entry
        Map model = new HashMap();
        ErrorReporter errorReporter = new ErrorReporter();
        model.put("errorReporter", errorReporter);

        ScheduleEntry scheduleEntry = (ScheduleEntry) getSessionVar("scheduleEntry");
        User user = (User) getSessionVar("user");
        RosterBean roster = (RosterBean) getSessionVar("roster");
        if (roster == null) {
            roster = new RosterBean();
            request.getSession().setAttribute("roster", roster);
        }
        Schedule schedule = (Schedule) getSessionVar("schedule");

        String theAction = request.getParameter("theAction");
        if (theAction.equals("submit") || theAction.equals("update") || theAction.equals("overallocation")) {
            // Grab the assignment list; note that it already includes the assignmentMap
            // with correct % allocation and work
            // Only Owner, Status and Role are needed to be updated
            Map assignmentMap = scheduleEntry.getAssignmentList().getAssignmentMap();

            String[] resourceIDs = request.getParameterValues("resource");
            String assignorId = request.getParameter("assignorUser");
            if(assignorId == null)
                assignorId = user.getID();

            // First Update all the assignments for the specified resourceIDs
            if (resourceIDs != null) {
                String primaryOwnerID = request.getParameter("primary_owner");

                for (Iterator iterator = Arrays.asList(resourceIDs).iterator(); iterator.hasNext();) {
                    String resourceID = (String) iterator.next();
                    ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) assignmentMap.get(resourceID);
                    if (assignment == null) {
                        // Hmm... A checked resource is not listed in the assignments
                        // This means there was a problem with the round-trip error handling
                        errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.invalidstate.message"));
                        break;
                    } else {
                        assignment.setSpaceID(user.getCurrentSpace().getID());
                        assignment.setPersonRole(request.getParameter("role_" + resourceID));
                        assignment.setStatusID(request.getParameter("status_" + resourceID));
                        if (assignment.getPersonID().equals(primaryOwnerID)) {
                            assignment.setPrimaryOwner(true);
                        }
                        assignment.setAssignorID(assignorId);
                        assignment.setTimeZone(roster.getAnyPerson(assignment.getPersonID()).getTimeZone());
                    }
                }
            }

            if (!errorReporter.errorsFound()) {
                // We have to store the schedule entry, since work, dates and duration
                // May have changed due to modified assignments
                // This will store the assignments too.
                scheduleEntry.store(true, schedule);

                // Now send the notifications
                if (PropertyProvider.getBoolean("prm.schedule.taskassignments.notification.enabled", true)) {
                    try {
                        sendNotifications(scheduleEntry, user, roster, errorReporter);
                    } catch (Exception e) {
                        //sjmittal catch it as we still have to calculate end points
                    }
                }

                //Recalculating will force the start date and end date of the
                //assignmentMap to be shown.  If this wasn't done, the start date
                //and end dates of assignmentMap probably wouldn't be correct.
                //sjmittal: not needed as already done in scheduleEntry.store(true, schedule); above 
//              if (schedule.isAutocalculateTaskEndpoints()) {
//                  schedule.recalculateTaskTimes();
//              }

                //Reload the schedule entry to make sure it gets the latest stuff
                String id = scheduleEntry.getID();
                scheduleEntry.clear();
                scheduleEntry.setID(id);
                scheduleEntry.load();

            }

        }

        // Now decide where to go
        String refLink = request.getParameter("refLink");
        String viewAssignmentsUrl = "/servlet/ScheduleController/TaskView/Assignments?module" + Module.SCHEDULE + "&action=" + Action.VIEW;

        if (errorReporter.errorsFound()) {
            setViewName(viewAssignmentsUrl);

        } else if (theAction != null && theAction.equals("overallocation")) {
            setViewName("/servlet/ScheduleController/TaskView/FixOverallocations");

        } else if (theAction != null && (theAction.equals("update") || theAction.equals("deleteAssignment"))) {
            // In the case of an "update" go back to assignments
            setViewName(viewAssignmentsUrl);

        } else if (refLink != null && ! refLink.equals("")) {
            String decodedRefererLink = java.net.URLDecoder.decode(refLink, SessionManager.getCharacterEncoding());
            setViewName(decodedRefererLink);
            setRedirect(true);

        } else {
            // In the case of all other actions, and without a refLink, we go back to workplan main
            setViewName("/workplan/taskview?module=" + Module.SCHEDULE + "&action=" + Action.VIEW);
        }

        return model;
    }

    /**
     * Sends notifications to newly added assignments.
     * <p/>
     * When delivery errors occur (for example, sending email), error reporter is update.
     * @param scheduleEntry the schedule entry about which assignments will be notified of
     * their assignment to
     * @param user the current user performing the assignments
     * @param roster the roster used for locating resource names in the event of errors
     * @param errorReporter the error report to which to add errors
     * @throws NotificationException if a non-delivery notification exception occurred
     */
    private void sendNotifications(ScheduleEntry scheduleEntry, User user, RosterBean roster, ErrorReporter errorReporter) throws NotificationException {

        DeliveryException firstDeliveryException = null;
        List failedResourceNames = new LinkedList();

        for (Iterator it = scheduleEntry.getAssignmentList().getAssignments().iterator(); it.hasNext();) {
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment)it.next();

            // Only send notification if person not already assigned
            if (!assignmentManager.isUserInAssignmentList(assignment.getPersonID())) {
                TaskAssignmentNotification notification = new TaskAssignmentNotification();
                notification.initialize (scheduleEntry, assignment, user);
                //Author: Avinash Bhamare,  Date: 31st Mar 2006
                //bfd-2997 : passing parameter assignment to new method getVCalendarForUser
                notification.attach(new net.project.calendar.vcal.VCalAttachment(scheduleEntry.getVCalendarForUser(assignment)));
                try {
                    notification.post();
                } catch (DeliveryException e) {
                    // Problem delivering email, possibly due to bad email configuration
                    // We have to look up roster by any ID since resourceIDs may yet to have registered
                    if (firstDeliveryException == null) {
                        firstDeliveryException = e;
                    }
                    failedResourceNames.add(roster.getAnyPerson(assignment.getPersonID()).getDisplayName());
                }
            }
        }

        if (firstDeliveryException != null) {
            // There was an error
            // Format the names of the failed resources
            StringBuffer failedResourceNamesFormatted = new StringBuffer();
            for (Iterator iterator = failedResourceNames.iterator(); iterator.hasNext();) {
                String name = (String) iterator.next();
                if (failedResourceNamesFormatted.length() > 0) {
                    failedResourceNamesFormatted.append(", ");
                }
                failedResourceNamesFormatted.append(name);
            }

            // Document the error, using only the first exception, but all the names
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.notification.message", failedResourceNamesFormatted.toString(), firstDeliveryException.formatOriginalCauseMessage()));
        }
    }
}