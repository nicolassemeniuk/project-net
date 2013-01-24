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

 package net.project.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.resource.ScheduleEntryAssignment;

/**
 * Provides a ScheduleEntryWorkingTimeCalendar which calculates values based on
 * zero, one or more resource assignments and working time calendars.
 * <p>
 * This is an aggregate of working time calendars, one for each resource.
 * All dates are calculated by determining the calculation for each resource
 * then choosing the earliest (when finding next dates or start times)
 * or latest (when finding "previous" dates or end times).
 * </p>
 * <p>
 * Duration is calculated by allocating the total work based on assignment
 * percentage then calculating the result duration for each resource and
 * choosing the latest value (when duration is positive) or earliest value
 * (when duration is negative).
 * </p>
 * <p>
 * When there are no assignments, the a single assignment of 100% is assumed.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.2
 */
public class ScheduleEntryWorkingTimeCalendar implements IWorkingTimeCalendar {

    //
    // Static Members
    //

    /**
     * Returns the later value of the specified dates.
     * If either date is null, the other is returned.
     * @param date1 the first date to compare
     * @param date2 the second date to compare
     * @return the later date or null if both dates are null
     */
    private static Date laterOf(Date date1, Date date2) {

        Date latestDate;

        if (date1 == null && date2 == null) {
            latestDate = null;

        } else if (date1 == null) {
            latestDate = date2;

        } else if (date2 == null) {
            latestDate = date1;

        } else {
            latestDate = (date1.after(date2) ? date1 : date2);
        }

        return latestDate;
    }

    /**
     * Returns the earlier value of the specified dates.
     * If either date is null, the other is returned.
     * @param date1 the first date to compare
     * @param date2 the second date to compare
     * @return the earlier date or null if both dates are null
     */
    private static Date earlierOf(Date date1, Date date2) {

        Date earliestDate;

        if (date1 == null && date2 == null) {
            earliestDate = null;

        } else if (date1 == null) {
            earliestDate = date2;

        } else if (date2 == null) {
            earliestDate = date1;

        } else {
            earliestDate = (date1.before(date2) ? date1 : date2);
        }

        return earliestDate;
    }


    //
    // Instance Members
    //

    /**
     * The assignments operated on by this calendar.
     */
    private final List assignments;

    /**
     * The provider of working time calendars.
     */
    private IWorkingTimeCalendarProvider provider = null;
    
    /**
     * Creates a ScheduleEntryWorkingTimeCalendar for the specified assignments
     * using the assignment's working time calendar definition or the
     * specified working time calendar definition if none found for an
     * assignment.
     * @param assignments the assignments indicating percentage allocated
     * @param provider the working time calendar provider used to fetch calendars
     * for this schedule entry's resources
     */
    public ScheduleEntryWorkingTimeCalendar(Collection assignments, IWorkingTimeCalendarProvider provider) {

        this.provider = provider;

        // Handle special case of zero assignments
        if (assignments.isEmpty()) {
            ScheduleEntryAssignment assigment = new ScheduleEntryAssignment();
            assigment.setPercentAssigned(100);
            this.assignments = new ArrayList();
            this.assignments.add(assigment);

        } else {
            this.assignments = new ArrayList(assignments);

        }

    }

    /**
     * Indicates whether the specified date is a working day for any assigned
     * resource.
     * @param date the date to check
     * @return true if the specified date is a working day for any assigned
     * resource; false if the date is not a working day for any resource
     */
    public boolean isWorkingDay(Date date) {
        boolean isWorkingDay = false;
        for (Iterator it = assignments.iterator(); it.hasNext() && !isWorkingDay;) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            IWorkingTimeCalendar nextWorkingTimeCalendar = getWorkingTimeCalendar(nextAssignment);
            isWorkingDay |= nextWorkingTimeCalendar.isWorkingDay(date);
        }
        return isWorkingDay;
    }

    /**
     * This method indicates whether or not the given calendar day of week is a
     * working day normally.  This method does not take into account if there
     * are any exceptions for that day.
     *
     * @param dayOfWeek a <code>int</code> code for day of week derived from
     * {@link java.util.Calendar.DAY_OF_WEEK}.
     * @return a <code>boolean</code> indicating if this particular day of week
     *         is normally a working day.
     */
    public boolean isStandardWorkingDay(int dayOfWeek) {
        boolean isStandardWorkingDay = false;
        for (Iterator it = assignments.iterator(); it.hasNext() && !isStandardWorkingDay;) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            IWorkingTimeCalendar nextWorkingTimeCalendar = getWorkingTimeCalendar(nextAssignment);
            isStandardWorkingDay |= nextWorkingTimeCalendar.isStandardWorkingDay(dayOfWeek);
        }
        return isStandardWorkingDay;
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
        Date latestDate = null;

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            IWorkingTimeCalendar nextWorkingTimeCalendar = getWorkingTimeCalendar(nextAssignment);

            if (nextWorkingTimeCalendar.isWorkingDay(date)) {
                latestDate = laterOf(latestDate, nextWorkingTimeCalendar.getEndOfWorkingDay(date));
            } else { //sachin: if it is not a working day the we should fecth the end of previous working day
                latestDate = laterOf(latestDate, nextWorkingTimeCalendar.getEndOfPreviousWorkingDay(date));
            }

        }
        

        return latestDate;
    }

    /**
     * Returns a date time which is the earliest start of the working day for
     * the day of the specified date.
     * @param date the date for which to find the time which is the start of
     * the working day
     * @return a date and time value; the date portion will be the same as
     * the date portion of the specified date.  The time portion will be set
     * to the earliest start of working time on the date for the assigned resources
     */
    public Date getStartOfWorkingDay(Date date) {

        Date earliestDate = null;

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            IWorkingTimeCalendar nextWorkingTimeCalendar = getWorkingTimeCalendar(nextAssignment);

            if (nextWorkingTimeCalendar.isWorkingDay(date)) {
                earliestDate = earlierOf(earliestDate, nextWorkingTimeCalendar.getStartOfWorkingDay(date));
            } else { //sachin: if it is not a working day the we should fecth the start of next working day
                earliestDate = earlierOf(earliestDate, nextWorkingTimeCalendar.getStartOfNextWorkingDay(date));
            }

        }


        return earliestDate;
    }

    /**
     * Returns a date and time which is set to the earliest start of the next
     * working day.
     * @param date the date from which to find the next working day
     * @return a date and time value which is set to the earliest start time
     * of the next working day
     */
    public Date getStartOfNextWorkingDay(Date date) {

        Date earliestDate = null;

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            IWorkingTimeCalendar nextWorkingTimeCalendar = getWorkingTimeCalendar(nextAssignment);
            earliestDate = earlierOf(earliestDate, nextWorkingTimeCalendar.getStartOfNextWorkingDay(date));
        }

        return earliestDate;
    }
    
    /**
     * Returns a date and time which is set to the latest end of the previous
     * working day.
     * @param date the date from which to find the previous working day
     * @return a date and time value which is set to the latest end time
     * of the previous working day
     */
    public Date getEndOfPreviousWorkingDay(Date date) {
        Date latestDate = null;

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            IWorkingTimeCalendar nextWorkingTimeCalendar = getWorkingTimeCalendar(nextAssignment);
            latestDate = laterOf(latestDate, nextWorkingTimeCalendar.getEndOfPreviousWorkingDay(date));
        }

        return latestDate;
    }

    /**
     * Returns the start of the earliest next working time block on the specified date.
     * @param date the date to get next working time for
     * @return the date and time of the next working time on the specified date
     * @throws NoWorkingTimeException if there isn't a next working time
     * on that date
     */
    public Date getStartOfNextWorkingTime(Date date) throws NoWorkingTimeException {

        Date earliestTime = null;

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            IWorkingTimeCalendar nextWorkingTimeCalendar = getWorkingTimeCalendar(nextAssignment);
            earliestTime = earlierOf(earliestTime, nextWorkingTimeCalendar.getStartOfNextWorkingTime(date));
        }

        return earliestTime;
    }

    /**
     * Returns the end of the latest previous working time block on the specified date.
     * @param date the date to get next working time for
     * @return the date and time of the previous working time on the specified date
     * @throws NoWorkingTimeException if there isn't a previous working time
     * on that date
     */
    public Date getEndOfPreviousWorkingTime(Date date) throws NoWorkingTimeException {

        Date latestTime = null;

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            IWorkingTimeCalendar nextWorkingTimeCalendar = getWorkingTimeCalendar(nextAssignment);
            latestTime = laterOf(latestTime, nextWorkingTimeCalendar.getEndOfPreviousWorkingTime(date));
        }

        return latestTime;
    }

    /**
     * Ensures the specified date is a working time or finds the next working
     * time if not. Finds the earliest working time start.
     * @param date the date time to check
     * @return the same date time if it is already a working time, otherwise
     * a date time that is at the start of the next working time
     * @throws NoWorkingTimeException if there is no next working time
     */
    public Date ensureWorkingTimeStart(Date date) throws NoWorkingTimeException {

        Date earliestTime = null;

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            IWorkingTimeCalendar nextWorkingTimeCalendar = getWorkingTimeCalendar(nextAssignment);
            earliestTime = earlierOf(earliestTime, nextWorkingTimeCalendar.ensureWorkingTimeStart(date));
        }

        return earliestTime;
    }

    /**
     * Ensures the specified date is a working time or finds the previous working
     * time if not. Finds the latest working time end.
     * @param date the date time to check
     * @return the same date time if it is already a working time, otherwise
     * a date time that is at the start of the next working time
     * @throws NoWorkingTimeException if there is no working time
     */
    public Date ensureWorkingTimeEnd(Date date) throws NoWorkingTimeException {

        Date latestTime = null;

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            IWorkingTimeCalendar nextWorkingTimeCalendar = getWorkingTimeCalendar(nextAssignment);
            latestTime = laterOf(latestTime, nextWorkingTimeCalendar.ensureWorkingTimeEnd(date));
        }

        return latestTime;
    }

    /**
     * Gets the working time calendar from the specified assignment.
     * @param assignment the assignment for which to get the working
     * time calendar
     * @return the working time calendar for the assignment; if the assignment
     * had none, a calendar based of the current default definitions is returned
     */
    public IWorkingTimeCalendar getWorkingTimeCalendar(ScheduleEntryAssignment assignment) {
        return assignment.getWorkingTimeCalendar(this.provider);
    }


}
