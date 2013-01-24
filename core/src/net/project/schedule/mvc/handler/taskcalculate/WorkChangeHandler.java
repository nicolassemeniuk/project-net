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

import javax.servlet.http.HttpServletRequest;

import net.project.base.mvc.ControllerException;
import net.project.base.property.PropertyProvider;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.security.User;
import net.project.util.ErrorReporter;
import net.project.util.TimeQuantity;

/**
 * Changes the work on a task and recalculates other components
 * as appropriate.
 * <p>
 * This is designed to be a synchronous round-trip called from Javascript.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class WorkChangeHandler extends AbstractScheduleEntryChangeHandler {

    public WorkChangeHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Invokes a task calculation for changing the work.
     * <p>
     * Expects the following request attributes:
     * <ul>
     * <li>workAmount - the new work amount
     * <li>workUnits - the new work units
     * </ul>
     * @param request the current request
     * @param schedule the current schedule
     * @param scheduleEntry the current schedule entry
     * @param scheduleEntryID the ID of the object being modified; this may be null if
     * a new schedule entry is being created
     * @param errorReporter reporter to which to add errors
     * @param user the current user
     * @param clonedSchedule
     * @throws ControllerException if there is a problem handling the request
     */
    protected void doHandleRequest(HttpServletRequest request, Schedule schedule, ScheduleEntry scheduleEntry, String scheduleEntryID, ErrorReporter errorReporter, User user, Schedule clonedSchedule) {

        // Get and parse the work, work complete, and percent complete
        TimeQuantity work = parseWork(request, errorReporter);
        TimeQuantity workComplete = parseWorkComplete(request, errorReporter);
        BigDecimal percentComplete = parseWorkPercentComplete(request, errorReporter);
        
        if (work == null || workComplete == null || percentComplete == null) {
            return;
        }
        
        //Test to make sure the work complete is less or equal to the work
        if (workComplete.compareTo(work) > 0) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.toomuchworkcomplete.message"));
        }

        //If there were errors found while parsing the time quantities, don't go
        //to the trouble to try to calculate.
        if (errorReporter.errorsFound()) {
            return;
        }


        boolean existingWorkIsZero = scheduleEntry.getWorkTQ().isZero();

        try {
            // Recalculate task
            ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
            calc.workChanged(work);
            
            //If work was already zero when we started, we need to set the percent
            //complete to make sure it doesn't get modified on the interface
            if (existingWorkIsZero) {
                calc.workPercentCompleteChanged(percentComplete);
            } else {
                calc.workCompleteChanged(workComplete);
            }
        } catch (RuntimeException e) {
            errorReporter.addError(e.getLocalizedMessage());
        }
    }

}
