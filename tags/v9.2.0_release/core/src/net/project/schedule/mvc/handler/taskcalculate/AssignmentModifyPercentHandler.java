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
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import net.project.base.mvc.ControllerException;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskEndpointCalculation;
import net.project.schedule.TaskList;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

/**
 * Provides a handler invoked when an assignment percent value is modified.
 * <p/>
 * This is designed to be a synchronous round-trip called from Javascript.
 *
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class AssignmentModifyPercentHandler extends AbstractAssignmentChangeHandler {

    public AssignmentModifyPercentHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Invokes a task calculation for modifying assignment percentage.
     * <p/>
     * Expects the following request attributes:
     * <ul>
     * <li>percentValue - the new assignment percentage
     * </ul>
     */
    protected void doHandleRequest(HttpServletRequest request, Schedule schedule, ScheduleEntry scheduleEntry, String resourceID, String timeZoneId, ErrorReporter errorReporter) throws ControllerException {

        // Parse percent value
        String percentValue = request.getParameter("percentValue");
        if (Validator.isBlankOrNull(percentValue)) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.emptypercent.message"));
        } else if (Validator.isNegative(percentValue)) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.negativepercent.message"));
        }
        
        if(errorReporter.errorsFound())
            return;

        try {
            int percentAssigned = ScheduleEntryAssignment.parsePercentAssigned(percentValue);

            //sjmittal: fix for bug 994. Removing for % range check here as greater that 100 is allowed
//            if (ScheduleEntryAssignment.isValidPercentAssigned(percentAssigned)) {
                BigDecimal percentDecimal = new BigDecimal(percentAssigned).movePointLeft(2);

                // Recalculate task
                ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) scheduleEntry.getAssignmentList().getForResourceID(resourceID);
                ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
                calc.assignmentPercentageChanged(percentDecimal, assignment);

//            } else {
//                // Out of range
//                errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.percentoutofrange.message"));
//            }

        } catch (ParseException e) {
            // Invalid number
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.invalidnumber.message", percentValue));
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