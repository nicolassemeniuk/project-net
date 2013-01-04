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

 package net.project.calendar.workingtime;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import net.project.schedule.ScheduleTimeQuantity;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;

/**
 * Provides a helper class for calculating durations.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class DurationCalculatorHelper {

    private final TimeZone timeZone;
    private final WorkingTimeCalendarDefinition calendarDefinition;

    /**
     * Creates a DurationCalculatorHelper using the specified working time calendar definition.
     * @param timeZone the time zone used to convert dates to calendars for performing date arithmetic
     * @param workingTimeCalendarDef the calendar definition used when calculating duration
     */
    public DurationCalculatorHelper(TimeZone timeZone, WorkingTimeCalendarDefinition workingTimeCalendarDef) {
        this.timeZone = timeZone;
        this.calendarDefinition = workingTimeCalendarDef;
    }

    /**
     * Returns the days worked starting from the start date for the amount
     * of work specified and the allocated resource factor.
     * <p>
     * Note: This handles the case where there is not enough working time defined
     * in the calendar to satisfy all the days worked.  In that case, days worked
     * is returned as the total days worked until working time was exhausted.
     * </p>
     * @param startDate the start date
     * @param work the amount of work performed; greater or equal to zero
     * @param percentAssignedDecimal the percentage of a resource's time; a value of <code>1</code>
     * means 100% assigned.  A value of <code>0.5</code> means 50% assigned.
     * When zero, days worked is zero
     * @return the days worked
     * @throws IllegalArgumentException if work is less than zero
     */
    public IDaysWorked getDaysWorked(Date startDate, TimeQuantity work, BigDecimal percentAssignedDecimal) {

        if (work.getAmount().signum() < 0) {
            // Work is negative
            throw new IllegalArgumentException("Work amount must be greater or equal to zero");
        }

        // Can't shortcut like calculating an end date
        // We must figure every minute of every day on which work is performed
        // since this is ultimately aggregated to produce total duration
        work = ScheduleTimeQuantity.convertToHour(work);

        final DaysWorked daysWorked;

        if (work.getAmount().signum() == 0) {
            // Handle the special case of zero duration
            // daysWorked remains empty meaning zero hours worked
            daysWorked = new DaysWorked();

        } else {
            // Positive amount of work

            // Handle special case of zero work factor
            // If assignment is assigned zero work, then their days worked is zero
            // Note: What happens when many assignments have zero work?
            // That depends on the task calculation type; however, assignment duration remains zero
            if (percentAssignedDecimal.signum() == 0) {
                daysWorked = new DaysWorked();

            } else {

                // Allocation structure provides remaining number of hours to
                // allocate to dates
                Allocation remainingWork = new Allocation(percentAssignedDecimal, work.getAmount());

                // Now figure out days worked
                Calendar cal = new GregorianCalendar(this.timeZone);
                cal.setTime(startDate);

                daysWorked = new DaysWorked();

                // Loop starting at the calendar's current date, allocating work
                // based on the number of hours available in the day
                // Recording the actual hours in the day worked (not just the count)
                // We abort if the working time calendar definition does not
                // have any working time available (at any point into the future) for
                // the current calendar date
                do {

                    if (calendarDefinition.isWorkingDay(cal)) {
                        // This is a working day

                        // Now update the days worked for today, deducting from
                        // remaining work
                        calendarDefinition.updateDaysWorked(daysWorked, cal, remainingWork);

                    }

                    // Increment date if we still have work to allocate
                    if (remainingWork.isWorkRemaining()) {
                        cal.add(Calendar.DATE, 1);

                        // We reset the calendar's time to the start of the day
                        // to be sure we count the whole day
                        DateUtils.zeroTime(cal);

                    }

                } while (remainingWork.isWorkRemaining() && calendarDefinition.isWorkingTimeAvailable(cal));

            }

        }

        return daysWorked;
    }

}