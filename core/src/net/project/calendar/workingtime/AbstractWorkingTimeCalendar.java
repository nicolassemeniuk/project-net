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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
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
 * Shows which days and times are working times for a given resource.
 * <p>
 * Currently uses the default calendar; Saturday and Sunday are non-working
 * days; Monday thru Friday are working days with default working times.
 * The default working times are 8:00 AM to 12:00 PM and 1:00 PM to 5:00 PM.
 * </p>
 * @author Matthew Flower
 * @author Tim Morrow
 * @since Version 7.4
 */
public abstract class AbstractWorkingTimeCalendar implements IWorkingTimeCalendar {

    /**
     * The definition of this working time calendar.
     * This currently contain default working times.
     */
    private final WorkingTimeCalendarDefinition calendarDefinition;

    /**
     * Time zone for which the methods in this AbstractWorkingTimeCalendar apply.
     */
    protected final TimeZone timeZone;

    /**
     * Creates a new AbstractWorkingTimeCalendar for the specified time zone.
     * @param timeZone the time zone used for date manipulation
     * @param calendarDef the definition that provides the settings (day of week, date etc.)
     * that affects how this calendar behaves
     */
    public AbstractWorkingTimeCalendar(TimeZone timeZone, WorkingTimeCalendarDefinition calendarDef) {
        this.timeZone = timeZone;
        this.calendarDefinition = calendarDef;
    }

    /**
     * Returns the current working time calendar definition on which this calendar
     * bases its calculations.
     * <p>
     * This can be used to construct other kinds of working time calendar based
     * on similar calendar entries.
     * </p>
     * @return the working time calendar definition
     */
    public WorkingTimeCalendarDefinition getWorkingTimeCalendarDefinition() {
        return this.calendarDefinition;
    }

    /**
     * Indicates whether the specified date is a working day.
     * Currently, a date is a working day of it falls on a Monday, Tuesday,
     * Wednesday, Thursday or Friday.
     * @param date the date to check
     * @return true if the specified date falls on a working day;
     * false otherwise
     */
    public boolean isWorkingDay(Date date) {
        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
        cal.setTime(date);

        return this.calendarDefinition.isWorkingDay(cal);
    }

    /**
     * This method indicates whether or not the given calendar day of week is a
     * working day normally.  This method does not take into account if there
     * are any exceptions for that day.
     *
     * @param dayOfWeek a <code>int</code> code for day of week derived from
     * {@link java.util.Calendar.DAY_OF_WEEK}.
     * @return a <code>boolean</code> indicating if this particular day of week
     * is normally a working day.
     */
    public boolean isStandardWorkingDay(int dayOfWeek) {
        return this.calendarDefinition.isStandardWorkingDay(dayOfWeek);
    }

    /**
     * Indicates whether the specified date is considered a working time
     * when for the purposes of locating the end of working time.
     * The start boundary of a working time block (e.g. 8:00 AM and 1:00 PM) are <b>not</b>
     * considered to be working time since not time would have elapsed between
     * the start of working time and the end date.
     * @param date the date to check
     * @return true if the specified date is a working time; false otherwise
     */
    private boolean isWorkingTimeForEnd(Date date) {
        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
        cal.setTime(date);

        return this.calendarDefinition.isWorkingTimeForEnd(cal);
    }

    /**
     * Returns a date that is set to the end of working time on the day
     * specified by the date.
     * <p>
     * <b>Note:</b> The date may become midnight on the next day if its current
     * day's working time ends at midnight. <br>
     * For example, if the working times are 8:00 AM - 12:00 PM, 1:00 PM - 5:00 PM
     * then the date will be set to 5:00 PM on the same day. <br>
     * If the working times are 12:00 AM - 12:00 AM (24 hours) then the
     * date will be set to 12:00 AM on the next day.
     * </p>
     * @param date the date from which to determine the end-of-day time
     * @return a date updated to the end of working time on the current day
     */
    public Date getEndOfWorkingDay(Date date) {
        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
        cal.setTime(date);
        this.calendarDefinition.updateWithEndOfLastWorkingTimeForDay(cal);
        return cal.getTime();
    }

    /**
     * Returns a date that is on the same day as the specified date,
     * with the time set to the start boundary of the first working time
     * block on the day of the date.
     * <p>
     * For example, 8:00 AM on the specified date.
     * </p>
     * @param date the date from which to determine the start-of-day time
     * @return a date with unchanged day but time set to the start of working
     * time on that day
     */
    public Date getStartOfWorkingDay(Date date) {
        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
        cal.setTime(date);
        this.calendarDefinition.updateWithStartOfFirstWorkingTimeForDay(cal);
        return cal.getTime();
    }

    /**
     * Get the start of the next working time block after this working time
     * block.
     *
     * @param date a <code>Date</code> object from which to start looking for
     * the next working time block.
     * @return a <code>Date</code> which is the start of the next working time
     * block.
     * @throws NoWorkingTimeException if there isn't a "next working time"
     * block.
     */
    public Date getStartOfNextWorkingTime(Date date) throws NoWorkingTimeException {
        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
        cal.setTime(date);
        this.calendarDefinition.updateWithStartOfNextWorkingTime(cal);
        return cal.getTime();
    }

    /**
     * Get the end of the previous block of working time.
     *
     * @param date a <code>Date</code> object which will be the start of our
     * search for the end of the previous working time block.
     * @return a <code>Date</code> indicating the end of the previous block of
     * working time.
     * @throws NoWorkingTimeException if there is a problem locating
     * a previous working time; for example the calendar defines no working
     * days
     */
    public Date getEndOfPreviousWorkingTime(Date date) throws NoWorkingTimeException {
        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
        cal.setTime(date);
        this.calendarDefinition.updateWithEndOfPreviousWorkingTime(cal);
        return cal.getTime();
    }

    /**
     * Returns a date that is at the start time on the next working day
     * following the specified date.
     * <p>
     * If no working time is available (there is a problem with the working
     * time calendar definition and no working time can be found), the same
     * date is returned.
     * </p>
     * @param date the date from which to determine the start date and time
     * of the next working day
     * @return a date at 8 AM on the next available working day or the same
     * date if no working time could be found in the calendar
     * @see #isWorkingDay
     * @see #getStartOfWorkingDay
     */
    public Date getStartOfNextWorkingDay(Date date) {
        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
        cal.setTime(date);

        boolean isWorkingTimeAvailable = true;

        // We only try to find next working day if there is some working time
        // Loop until we find a working day and while there is working time available
        do {
            cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
        } while (!isWorkingDay(cal.getTime()) && (isWorkingTimeAvailable = calendarDefinition.isWorkingTimeAvailable(cal)));


        Date resultDate;
        if (!isWorkingTimeAvailable) {
            // We aborted due to lack of working time
            resultDate = date;

        } else {
            // We aborted due to found working time
            resultDate = getStartOfWorkingDay(cal.getTime());
        }

        return resultDate;
    }

    /**
     * Returns a date that is at the end time on the previous working day
     * following the specified date.
     * <p>
     * If no working time is available (there is a problem with the working
     * time calendar definition and no working time can be found), the same
     * date is returned.
     * </p>
     * @param date the date from which to determine the end date and time
     * of the previous working day
     * @return a date at 5 PM on the previous available working day or the same
     * date if no working time could be found in the calendar
     * @see #isWorkingDay
     * @see #getStartOfWorkingDay
     */
    public Date getEndOfPreviousWorkingDay(Date date) {
        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
        cal.setTime(date);

        boolean isWorkingTimeAvailable = true;

        // We only try to find next working day if there is some working time
        // Loop until we find a working day and while there is working time available
        do {
            cal.add(GregorianCalendar.DAY_OF_YEAR, -1);
        } while (!isWorkingDay(cal.getTime()) && (isWorkingTimeAvailable = calendarDefinition.isWorkingTimeAvailable(cal)));


        Date resultDate;
        if (!isWorkingTimeAvailable) {
            // We aborted due to lack of working time
            resultDate = date;

        } else {
            // We aborted due to found working time
            resultDate = getEndOfWorkingDay(cal.getTime());
        }

        return resultDate;
    }
    
    public BigDecimal getDuration(Date startDate, TimeQuantity work) {
        return getDaysWorked(startDate, work).getTotalDays();
    }

    public Date ensureWorkingTimeStart(Date date) throws NoWorkingTimeException {

        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
        cal.setTime(date);

        if (!this.calendarDefinition.isWorkingTime(cal)) {
            // Specified date is not a working time; find the start of the
            // next working time
            this.calendarDefinition.updateWithStartOfNextWorkingTime(cal);
        }

        return cal.getTime();
    }

    public Date ensureWorkingTimeEnd(Date date) throws NoWorkingTimeException {

        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
        cal.setTime(date);

        if (!isWorkingTimeForEnd(cal.getTime())) {
            // Specified date is not a working time for the purposes of
            // locating an end boundary; find the end of the
            // previous working time
            this.calendarDefinition.updateWithEndOfPreviousWorkingTime(cal);

        }

        return cal.getTime();
    }

    /**
     * Adds the specified work to the start date.
     * Assumes the assignee is the only assignee to the task.
     * @param startDate the date to which to add the work
     * @param work the work; this may be positive or negative
     * @return the resulting date; or the original date if a problem occurred
     */
    public Date addWork(Date startDate, TimeQuantity work) {
        return addDuration(startDate, work, calculateAllocatedResourceFactor());
    }

    /**
     * Adds the specified work to the start date using the specified value
     * as the percentage of work performed by this assignee.
     * @param startDate the date to which to add the work
     * @param work the work; this may be positive or negative
     * @param allocatedResourceFactor a decimal indicating the percentage of
     * work performed by the assignee; a value of 1 means 100%
     * @return the resulting date; or the original date if a problem occurred
     */
    private Date addDuration(Date startDate, TimeQuantity work, BigDecimal allocatedResourceFactor) {

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

            // Handle special case of zero work factor
            if (allocatedResourceFactor.signum() == 0) {
                // Assigned resources with a total of 0% allocation;
                // behave as if there were no assigned resources
                allocatedResourceFactor = new BigDecimal("1");

            }

            // Allocation structure provides remaining number of hours to
            // allocate to dates
            Allocation remainingWork = new Allocation(allocatedResourceFactor, work.getAmount());

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
                    endDateCal.setTime(ensureWorkingTimeEnd(endDateCal.getTime()));

                } else {
                    // Ensure the end date is at the start of working time
                    // It might be at 5:00 PM on the previous day due to the calculation
                    endDateCal.setTime(ensureWorkingTimeStart(endDateCal.getTime()));
                }

            } catch (NoWorkingTimeException e) {
                // There was an error figuring out the working time start
                // or end; we'll use the previous calculated date
                // End date will remain same

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
                cal.setTime(ensureWorkingTimeStart(cal.getTime()));
            } else {
                cal.setTime(ensureWorkingTimeEnd(cal.getTime()));
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

    /**
     * Returns the number of units to allocated when calculating dates.
     * @return the number of units with a scale of 2; a value of <code>1</code> means 1 hour
     * of work will be completed in 1 hour.  A value of <code>0.5</code> means
     * 1 hour of work requires 2 hours to be completed.
     */
    protected abstract BigDecimal calculateAllocatedResourceFactor();

    /**
     * Returns the days worked starting from the start date for the amount
     * of work specified.
     * @param startDate the start date
     * @param work the amount of work
     * @return the days worked
     */
    public DaysWorked getDaysWorked(Date startDate, TimeQuantity work) {
        return getDaysWorked(startDate, work, calculateAllocatedResourceFactor());
    }

    /**
     * Returns the days worked starting from the start date for the amount
     * of work specified and the allocated resource factor.
     * <p>
     * Note: This handles the case where there is not enough working time definied
     * in the calendar to satisfy all the days worked.  In that case, days worked
     * is returned as the total days worked until working time was exhausted.
     * </p>
     * @param startDate the start date
     * @param work the amount of work
     * @param allocatedResourceFactor the allocation of a resource, out of 1.
     * @return the days worked
     */
    private DaysWorked getDaysWorked(Date startDate, TimeQuantity work, BigDecimal allocatedResourceFactor) {

        // Can't shortcut like work; must traverse every day
        // But this is calculated much less often

        work = ScheduleTimeQuantity.convertToHour(work);

        final DaysWorked daysWorked;

        if (work.getAmount().signum() < 0) {
            // Work is negative
            throw new IllegalArgumentException("Work amount must be greater or equal to zero");

        } else if (work.getAmount().signum() == 0) {
            // Handle the special case of zero duration
            // daysWorked remains empty meaning zero hours worked
            daysWorked = new DaysWorked();

        } else {
            // Positive amount of work

            // Handle special case of zero work factor
            if (allocatedResourceFactor.signum() == 0) {
                // Assigned resources with a total of 0% allocation;
                // behave as if there were no assigned resources
                allocatedResourceFactor = new BigDecimal("1");

            }

            // Allocation structure provides remaining number of hours to
            // allocate to dates
            Allocation remainingWork = new Allocation(allocatedResourceFactor, work.getAmount());

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

        return daysWorked;
    }

}
