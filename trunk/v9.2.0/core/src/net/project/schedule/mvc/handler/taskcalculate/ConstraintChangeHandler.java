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

import net.project.base.mvc.ControllerException;
import net.project.base.property.PropertyProvider;
import net.project.calendar.TimeBean;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskConstraintType;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.security.User;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.InvalidDateException;
import net.project.util.Validator;

/**
 * Changes the constraint on a task and performs a task endpoint calculation.
 *
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class ConstraintChangeHandler extends AbstractScheduleEntryChangeHandler {

    public ConstraintChangeHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Invokes a task calculation for changing the constraint.
     * Expects the following request attributes:
     * <ul>
     * <li>constraintTypeID - the ID of the new constraint type
     * <li>constraintDateString - the new constraint date, formatted for the user's locale
     * <li>constraintDateTime_hour, constraintDateTime_minute, {constraintDateTime_ampm}, constraintDateTime_timeZoneID -
     * the time components, suitable for parsing by {@link TimeBean#parseTime(javax.servlet.http.HttpServletRequest, java.lang.String, java.util.Date)}
     * </ul>
     * @param request the current request
     * @param schedule the current schedule
     * @param scheduleEntry the current schedule entry
     * @param scheduleEntryID the ID of the object being modified; this may be null if a new schedule entry is being
     * created
     * @param errorReporter the error reporter to update with errors
     * @param user the current user
     * @param clonedSchedule
     * @throws ControllerException if there is a problem handling the request
     * @see net.project.schedule.TaskConstraintType
     */
    protected void doHandleRequest(HttpServletRequest request, Schedule schedule, ScheduleEntry scheduleEntry, String scheduleEntryID, ErrorReporter errorReporter, User user, Schedule clonedSchedule) {

        // Grab the parameters from the request
        String constraintTypeID = request.getParameter("constraintTypeID");
        String constraintDateFormatted = request.getParameter("constraintDateString");

        try {
            TaskConstraintType constraintType;
            Date constraintDate;

            // Constraint Type ID is always required
            if (Validator.isBlankOrNull(constraintTypeID)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.requiredConstraintId.message"));
            }
            if(errorReporter.errorsFound())
                return;

            constraintType = TaskConstraintType.getForID(constraintTypeID);
            if (constraintType == null) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.unkownConstraintType.message",constraintTypeID));
            }
            if(errorReporter.errorsFound())
                return;

            if (!constraintType.isDateConstrained()) {
                // Constraint Date is sometimes not allowed
                constraintDate = null;

            } else {
                // For other constraints, a date is required
                if (Validator.isBlankOrNull(constraintDateFormatted)) {
                    errorReporter.addError(PropertyProvider.get("prm.schedule.taskedit.error.requiredConstraintDate.message"));
                }
                if(errorReporter.errorsFound())
                    return;

                // Parse the date and time, where the time components are optional, since we can
                // determine them based on the constraint type
                constraintDate = TimeBean.parseDateTime(request, "constraintDateString", "constraintDateTime", true);
    	        //sjmittal: please dont update the constraint time here as this is would be done in ScheduleEntryConstraintModifier
    	        //fix for bfd 3048
                /*if (!isTimeSpecified(request, "constraintDateTime")) {
                    // If the user didn't specify a time, we want to set the time based on the constraint type
                    constraintDate = ScheduleEntry.updateConstraintTime(constraintType, constraintDate, TimeBean.getTimeZone(request, "constraintDateTime"));
                }*/

            }
            
            // Now update the scheduleEntry and the schedule (the cloned one to aovid upsetting the main schedule)
            ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, clonedSchedule.getWorkingTimeCalendarProvider());
            calc.constraintChanged(clonedSchedule, constraintType, constraintDate, user.getTimeZone());

        } catch (InvalidDateException e) {
            // Problem parsing the date and time
            ErrorDescription ed = new ErrorDescription("constraintDateString", PropertyProvider.get("prm.schedule.taskedit.error.constraint.message", new Object[]{constraintDateFormatted}));
            errorReporter.addError(ed);
        }

    }

    /**
     * Indicates whether a time component was specified in the request.
     *
     * @param request the request
     * @param timeFieldName the prefix for the name of the time components
     * @return true if no time components were found
     */
    private boolean isTimeSpecified(HttpServletRequest request, String timeFieldName) {
        return (TimeBean.parseTime(request, timeFieldName, new Date(), true) != null);
    }
}