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

import net.project.base.property.PropertyProvider;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.security.User;
import net.project.util.ErrorReporter;
import net.project.util.TimeQuantity;

/**
 * Calculation class to update work when work percent complete changes.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class WorkPercentCompleteChangeHandler extends AbstractScheduleEntryChangeHandler {
    public WorkPercentCompleteChangeHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Implementing classes should modify the schedule entry as appropriate.
     *
     * @param request the current request
     * @param schedule the current schedule
     * @param scheduleEntry the current schedule entry
     * @param scheduleEntryID the ID of the object being modified; this may be
     * null if a new schedule entry is being created
     * @param errorReporter an error reporter to which to add any errors
     * @param user the current user
     * @param clonedSchedule a clone of the schedule, also containing the
     * session scheduleEntry object such that updating the session scheduleEntry
     * object will update the schedule clone; this clone is a "sandbox" which
     * can be recalculated without affecting the session schedule. Once the
     * session scheduleEntry is stored, the session schedule should be
     * recalculated
     * @throws net.project.base.mvc.ControllerException if there is a problem
     * handling the request
     */
    protected void doHandleRequest(HttpServletRequest request, Schedule schedule,ScheduleEntry scheduleEntry, String scheduleEntryID, ErrorReporter errorReporter,User user, Schedule clonedSchedule) {
        TimeQuantity work;
        TimeQuantity workComplete;

        // Get and parse the work, work complete, and percent complete
        work = parseWork(request, errorReporter);
        workComplete = parseWorkComplete(request, errorReporter);
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

        //If work is zero, then work complete and percent complete aren't related
        //sjmittal: for fix of bfd 4133, I realy don't know why are we adding an error here
        //because for milestones we can have work = 0
//        if (scheduleEntry.getWorkTQ().isZero()) {
//            //Make sure we reflect the change to work complete in the schedule
//            //entry, otherwise it will be overridden when we are done with our
//            //round trip.
//            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.toomuchworkcomplete.message"));
//            scheduleEntry.setWorkPercentComplete(percentComplete);
//            return;
//        }

        //sjmittal: don't understand as why we need to re set this as only % complete is changed here
        // Recalculate task
//        scheduleEntry.setWork(work);

        ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
        try {
            calc.workPercentCompleteChanged(percentComplete);
        } catch (RuntimeException e) {
            errorReporter.addError(e.getLocalizedMessage());
        }
    }
}
