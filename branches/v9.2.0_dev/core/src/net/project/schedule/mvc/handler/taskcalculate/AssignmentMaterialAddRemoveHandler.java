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

 package net.project.schedule.mvc.handler.taskcalculate;

import java.math.BigDecimal;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import net.project.base.ObjectType;
import net.project.base.mvc.ControllerException;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.persistence.PersistenceException;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskEndpointCalculation;
import net.project.schedule.TaskList;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.User;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

/**
 * Provides a handler invoked when an assignment is added or removed from
 * a schedule entry, providing a Javascript view of the model.
 * <p>
 * This is designed to be a synchronous round-trip called from Javascript.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class AssignmentMaterialAddRemoveHandler extends AbstractAssignmentChangeHandler {

    public AssignmentMaterialAddRemoveHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Invokes a task calculation for adding or removing an assignment.
     * <p>
     * Expects the following request attributes:
     * <ul>
     * <li>mode - one of <code>add</code> or <code>remove</code>
     * <li>resourceID - the id of the resource being added or removed
     * </ul>
     */
    protected void doHandleRequest(HttpServletRequest request, Schedule schedule, ScheduleEntry scheduleEntry, String resourceID, String timeZoneId, ErrorReporter errorReporter) throws ControllerException {

        // Determine whether being added or removed
        User user = (User)getSessionVar("user");
        String mode = request.getParameter("mode");
        if (Validator.isBlankOrNull(mode)) {
            throw new ControllerException("Missing request parameter mode");
        }
        boolean isAddAssignment = mode.equals("add");

        // Recalculate task
        ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());

        if (isAddAssignment) {
            // When adding, create a new assignment
            ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
            assignment.setPersonID(resourceID);
            //Sachin: set assignor to the one selected
            //done in the TaskAssignmentProcessingHandler
//            assignment.setAssignorID(user.getID());
            assignment.setTimeZone(TimeZone.getTimeZone(timeZoneId));
            assignment.setObjectID(scheduleEntry.getID());
            assignment.setObjectType(ObjectType.TASK);

            // Fixed duration effort driven tasks cannot specify a percentage
            // For other types, we're defaulting to adding at 100% although in theory the algorithm
            // can handle adding at a specific percentage.  It is our interface that makes this hard.
            // Anyway, the user can change the percentage later
            BigDecimal addPercentage;
            if (scheduleEntry.getTaskCalculationType().equals(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN)) {
                addPercentage = null;
            } else {
                addPercentage = new BigDecimal("1.00");
            }
            try {
                calc.assignmentAdded(addPercentage, assignment);
            } catch (NoWorkingTimeException e) {
                errorReporter.addError(e.getMessage());
            }

        } else {                                                
            // When removing, the assignment must be in the list of assignments
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) scheduleEntry.getAssignmentList().getForResourceID(resourceID);
            calc.assignmentRemoved(assignment);
        }

        try {
            //Make a copy of the schedule and replace the build in schedule entry
            //with the one we're modifying.  Then we can do a recalculation to
            //get dates
            Schedule newSchedule = (Schedule)schedule.clone();
            TaskList tl = newSchedule.getTaskList();
            tl.remove(scheduleEntry.getID());
            tl.add(scheduleEntry);

            new TaskEndpointCalculation().recalculateTaskTimesNoLoad(newSchedule);
        } catch (PersistenceException e) {
            // No loading should ever be occurring; the problem is with methods
            // That optionally load, they optionally throw a PersistenceException
            throw (IllegalStateException) new IllegalStateException("PersistenceException occurred when no persistence operations were expected").initCause(e);
        }
    }

}
