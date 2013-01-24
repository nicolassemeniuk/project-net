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
import net.project.util.TimeQuantity;

/**
 * Provides a helper class for calculating dates based on a working time calendar.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class DateCalculatorHelper {

    /**
     * A working time calendar used for determining various working times.
     */
    private final IWorkingTimeCalendar workingTimeCalendar;

    /**
     * The definition of this working time calendar.
     * This currently contain default working times.
     */
    private final WorkingTimeCalendarDefinition calendarDefinition;

    /**
     * Time zone for which the methods in this DateCalculatorHelper apply.
     */
    private final TimeZone timeZone;

    /**
     * Creates a helper for the specified definition-based working time calendar.
     * @param workingTimeCalendar the working time calendar to use for determining working times

     */
    public DateCalculatorHelper(DefinitionBasedWorkingTimeCalendar workingTimeCalendar) {
        this.workingTimeCalendar = workingTimeCalendar;
        this.timeZone = workingTimeCalendar.getTimeZone();
        this.calendarDefinition = workingTimeCalendar.getCalendarDefinition();
    }

    /**
     * Calculates a date such that the amount of working time between the
     * calculated date and the specified start date is equal to the
     * specified time quantity.  The difference between the end date and
     * the start date can be no less than the specified time quantity.
     * <p>
     * For example, given startDate of Monday 5th May 8:00 AM and a time quantity
     * of 1 day, the end date will be Monday 5th May 5:00 PM.
     * Given a startDate of Tuesday 6th May 5:00 PM and a time quantity of
     * -1 day, the end date will be Monday 5th May 8:00 AM.
     * This example assumes 100% assignment and a default working time
     * calendar.
     * </p>
     * <p>
     * The working time calendar is considered when calculating so that
     * the specified work is exactly completed between the start date and
     * the calculated date given the specified percentage.
     * </p>
     * @param startDate the date from which to calculate the end date
     * @param work the work performed beginning on the start date;
     * this may be positive or negative.  A negative value means the calculated
     * date should be earlier than the specified date; a positive value means the calculated
     * date should be later than the specified date.
     * @param percentAssignedDecimal a decimal indicating the percentage of
     * time per day spent completing the work; a value of 1 = 100%, 0.5 = 50%
     * @return the calculated end date; the date on which work would be completed
     * (given a positive work amount) or the date on which work could start (given
     * a negative work amount)
     */
    public Date calculateDate(Date startDate, TimeQuantity work, BigDecimal percentAssignedDecimal) {

        work = ScheduleTimeQuantity.convertToHour(work);

        // This is the result date; initially set to the start date
        Calendar endDateCal = new GregorianCalendar(this.timeZone);
        endDateCal.setTime(startDate);

        if (work.getAmount().signum() == 0) {
            // Handle the special case of zero duration; the end date is
            // equal to the start date
            // Nothing to change

        } else {
            // Non-zero time; continue to calculate the end date

            if (percentAssignedDecimal != null && percentAssignedDecimal.signum() == 0) {
                // When no percentatge assignment, no work is done
                // and thus the date does not change
                // Nothing to change

            } else if (percentAssignedDecimal != null ){

                // Allocation structure provides remaining number of hours to
                // allocate to dates
                Allocation remainingWork = new Allocation(percentAssignedDecimal, work.getAmount());

                // Modify the end date
                addWork(endDateCal, remainingWork);

                // Now move the end date to the appropriate boundary
                // If we're adding time then it should be at the end of previous
                // working time
                // If we're subtracting time then it should be at the start of working
                // time block
                try {
                    if (remainingWork.isEndTimeRequired()) {
                        // ensure the end date is at the end of working time
                        // It might be at 8:00 AM on the following day due to the calculation
                        // we use to calculate weeks
                        endDateCal.setTime(this.workingTimeCalendar.ensureWorkingTimeEnd(endDateCal.getTime()));

                    } else {
                        // Ensure the end date is at the start of working time
                        // It might be at 5:00 PM on the previous day due to the calculation
                        endDateCal.setTime(this.workingTimeCalendar.ensureWorkingTimeStart(endDateCal.getTime()));
                    }

                } catch (NoWorkingTimeException e) {
                    // There was an error figuring out the working time start
                    // or end; we'll use the previous calculated date
                    // End date will remain same

                }

            }

        }

        return endDateCal.getTime();
    }

    /**
     * Adds the specified amount of work to the specified calendar.
     * The calendar is updated.  The calendar may not be set to an appropriate
     * working time boundary.
     * @param cal the calendar containing the start date
     * @param workToAllocate the work to add; this is updated and will be
     * zero
     */
    private void addWork(Calendar cal, Allocation workToAllocate) {

        try {

            // Move to start/end of working time
            // This moves the date out of non-working time to a boundary
            // of working time
            if (workToAllocate.isEndTimeRequired()) {
                cal.setTime(this.workingTimeCalendar.ensureWorkingTimeStart(cal.getTime()));
            } else {
                cal.setTime(this.workingTimeCalendar.ensureWorkingTimeEnd(cal.getTime()));
            }

            // Save the actual start date for purposes of locating exceptional
            // date entries between start and calculated end
            Date startDate;
            startDate = cal.getTime();

            // Loop until we've allocated all the work
            // cal will be updated with appropriate date (although the date
            // may not be on the appropriate working time boundary)
            while (workToAllocate.isWorkRemaining()) {

                // Calculate the number of whole weeks of work based on the number
                // of working hours in week based on day of week entries.
                // This gives us an end date that, assuming there are few date exceptions,
                // is within 1 week of the real end date
                BigDecimal wholeWeeks = workToAllocate.subtractActualWeeksRemaining(calendarDefinition.getWorkingHoursInWeek().toHour());
                if (wholeWeeks.signum() != 0) {

                    // We'll add or subtract those weeks directly
                    BigDecimal multiplier = new BigDecimal((workToAllocate.isPositive() ? 7 : -7));
                    cal.add(Calendar.DAY_OF_MONTH, wholeWeeks.multiply(multiplier).intValue());

                    // Calculate exception hours between start date and current end date
                    // Exception hours are positive if there are additional hours
                    // available; negative if there are fewer hours available
                    SimpleTimeQuantity exceptionHours = calendarDefinition.calculateExceptionHours(startDate, cal.getTime(), this.timeZone);

                    // Subtract the exception hours
                    // This means, if we found there were additional hours for
                    // work due to exceptions, (say someone worked a weekend)
                    // we'll go the opposite direction
                    // (back in time for positive durations, forward in time
                    // for negative duration) since we need a closer date for
                    // completion
                    // If we found there were fewer hours for work due to
                    // exceptions (say someone on vacation), we'll go the same
                    // direction (forward in time for positive durations, back in time
                    // for negative duration) since we need a farther date
                    // for completion
                    workToAllocate.subtractActualHoursRemaining(exceptionHours.toHour());

                    startDate = cal.getTime();

                } else {
                    // No whole weeks remaining; we'll loop day by day
                    // There is less than one week remaining
                    // While we have additional hours, allocate those hours to each
                    // day
                    while (workToAllocate.isWorkRemaining()) {
                        if (workToAllocate.isPositive()) {
                            calendarDefinition.allocateLaterHoursInDay(cal, workToAllocate);
                        } else {
                            calendarDefinition.allocateEarlierHoursInDay(cal, workToAllocate);
                        }
                    }

                }

            }

        } catch (NoWorkingTimeException e) {
            // Couldn't find a working time start
            // Don't change the start date

        }

    }

}
