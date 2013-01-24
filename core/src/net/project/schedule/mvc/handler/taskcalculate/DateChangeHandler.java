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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.ErrorReporter;
import net.project.util.InvalidDateException;
import net.project.util.Validator;

/**
 * Changes the date on a task by updating the constraint and performs a task endpoint calculation.
 *
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class DateChangeHandler extends AbstractScheduleEntryChangeHandler {

    public DateChangeHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Invokes a task calculation for changing the start date OR end date.
     * Expects ONE of the following request attributes:
     * <ul>
     * <li>startDateString - the start date; in this case the start date is changed
     * <li>endDateString - the end date; in this case the end date is changed
     * </ul>
     * @param request the current request
     * @param schedule the current schedule
     * @param scheduleEntry the current schedule entry
     * @param scheduleEntryID the ID of the object being modified; this may be null if a new schedule entry is being
     * created
     * @param errorReporter the error reporter to update with errors
     * @param user the current user
     * @param clonedSchedule
     * @throws net.project.base.mvc.ControllerException if there is a problem handling the request; if
     * both start date and end date parameters are specified, or if neither is specified
     * @see net.project.schedule.TaskConstraintType
     */
    protected void doHandleRequest(HttpServletRequest request, Schedule schedule, ScheduleEntry scheduleEntry, String scheduleEntryID, ErrorReporter errorReporter, User user, Schedule clonedSchedule) {

        // Grab the parameters from the request
        String startDateString = request.getParameter("startDateString");
        String endDateString = request.getParameter("endDateString");

        // Determine whether the start date or end date was specified
        String dateToParse=  null;
        boolean isStartDateSpecified = false;
        if (startDateString != null && endDateString != null) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.dates.message"));

        } else if (startDateString != null) {
            isStartDateSpecified = true;
            dateToParse = startDateString;

        } else if (endDateString != null) {
            isStartDateSpecified = false;
            dateToParse = endDateString;

        } else {
            errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.datesrequired.message"));
        }
        if(errorReporter.errorsFound())
            return;

        if (Validator.isBlankOrNull(dateToParse)) {
            // User entered an empty date; we can't do anything with that
            String errorPropertyName = (isStartDateSpecified ? "prm.schedule.taskedit.error.startdate.message" : "prm.schedule.taskedit.error.enddate.message");
            errorReporter.addError(PropertyProvider.get(errorPropertyName, dateToParse));

        } else {

            // Now parse and change the date
            try {
                // First parse the date
                Date date = DateFormat.getInstance().parseDateString(dateToParse);

                ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, clonedSchedule.getWorkingTimeCalendarProvider());
                if (isStartDateSpecified) {
                    calc.startDateChanged(clonedSchedule, date, user.getTimeZone());
                } else {
                    calc.endDateChanged(clonedSchedule, date, user.getTimeZone());
                }

            } catch (InvalidDateException e) {
                // Problem parsing the start date or end date
                String errorPropertyName = (isStartDateSpecified ? "prm.schedule.taskedit.error.startdate.message" : "prm.schedule.taskedit.error.enddate.message");
                errorReporter.addError(PropertyProvider.get(errorPropertyName, dateToParse));
            }

        }

    }

}