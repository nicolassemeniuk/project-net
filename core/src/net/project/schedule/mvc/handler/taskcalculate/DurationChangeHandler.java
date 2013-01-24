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

import javax.servlet.http.HttpServletRequest;

import net.project.base.mvc.ControllerException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskEndpointCalculation;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.security.User;
import net.project.util.ErrorReporter;
import net.project.util.TimeQuantity;

/**
 * Changes the duration on a task and recalculates other components
 * as appropriate.
 * <p>
 * This is designed to be a synchronous round-trip called from Javascript.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class DurationChangeHandler extends AbstractScheduleEntryChangeHandler {

    public DurationChangeHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Invokes a task calculation for changing the duration.
     * Expects the following request attributes:
     * <ul>
     * <li>durationAmount - the new duration amount
     * <li>durationUnits - the new duration units
     * </ul>
     * @param request the current request
     * @param schedule the current schedule
     * @param scheduleEntry the current schedule entry
     * @param scheduleEntryID the ID of the object being modified; this may be null if
     * a new schedule entry is being created
     * @param errorReporter
     * @param user the current user
     * @param clonedSchedule
     * @throws ControllerException if there is a problem handling the request
     */
    protected void doHandleRequest(HttpServletRequest request, Schedule schedule, ScheduleEntry scheduleEntry, String scheduleEntryID, ErrorReporter errorReporter, User user, Schedule clonedSchedule) {

        TimeQuantity duration = parseDuration(request,errorReporter);
        
        //If there were errors found while parsing the time quantities, don't go
        //to the trouble to try to calculate.
        if(duration == null || errorReporter.errorsFound())
            return;
        
        // Recalculate task
        ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
        try {
            calc.durationChanged(duration);
            new TaskEndpointCalculation().recalculateTaskTimesNoLoad(clonedSchedule);
        } catch (Exception e) {
            errorReporter.addError(e.getLocalizedMessage());
        } 

    }

}
