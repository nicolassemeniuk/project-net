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
import net.project.util.TimeQuantity;
import net.project.util.Validator;

import org.apache.log4j.Logger;

/**
 * Provides a handler invoked when an assignment work value is modified.
 * <p>
 * This is designed to be a synchronous round-trip called from Javascript.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class AssignmentModifyWorkHandler extends AbstractAssignmentChangeHandler {

    private static final Logger LOGGER = Logger.getLogger(AssignmentModifyWorkHandler.class);

    public AssignmentModifyWorkHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Invokes a task calculation for modifying assignment percentage.
     * <p>
     * Expects the following request attributes:
     * <ul>
     * <li>workAmount - the formatted amount of work
     * <li>workUnitsID - the id of the selected work unit
     * </ul>
     * </p>
     */
    protected void doHandleRequest(HttpServletRequest request, Schedule schedule, ScheduleEntry scheduleEntry, String resourceID, String timeZoneId, ErrorReporter errorReporter) throws ControllerException {

        // Read the modified work value
        String workAmountString = request.getParameter("workAmount");
        String workUnits = request.getParameter("workUnitsID");
        if (Validator.isBlankOrNull(workAmountString) || Validator.isBlankOrNull(workUnits)) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.emptywork.message"));
        } else if (Validator.isNegative(workAmountString)) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.negativework.message"));
        }
        
        if(errorReporter.errorsFound())
            return;
        
        TimeQuantity work;
        try {
            work = TimeQuantity.parse(workAmountString, workUnits);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Modifying assignment resource " + resourceID + " changing work to " + work);
            }

            // Recalculate task
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) scheduleEntry.getAssignmentList().getForResourceID(resourceID);
            ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
            calc.assignmentWorkChanged(work, assignment);

        } catch (ParseException e) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskview.resources.error.invalidwork.message"));
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