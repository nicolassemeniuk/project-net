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

import java.util.Date;

/**
 * Provides common WorkingTimeCalendar features.
 *
 * @author Matthew Flower
 * @author Tim Morrow
 * @since Version 7.6.0
 */
public interface IWorkingTimeCalendar {

    /**
     * Indicates whether the specified date is a working day.
     * @param date the date to check
     * @return true if the specified date falls on a working day;
     * false otherwise
     */
    boolean isWorkingDay(Date date);

    /**
     * This method indicates whether or not the given calendar day of week is a
     * working day normally.  This method does not take into account if there
     * are any exceptions for that day.
     *
     * @param dayOfWeek a <code>int</code> code for day of week derived from
     * {@link java.util.Calendar#DAY_OF_WEEK}.
     * @return a <code>boolean</code> indicating if this particular day of week
     * is normally a working day.
     */
    boolean isStandardWorkingDay(int dayOfWeek);

    /**
     * Returns a date that is on the same day as the specified date,
     * with the time set to just after the end of a working day.
     * @param date the date from which to determine the end-of-day time
     * @return a date just after the end time of a working day on the same day
     * as the specified date
     */
    Date getEndOfWorkingDay(Date date);

    /**
     * Returns a date that is on the same day as the specified date, with the
     * time set to the beginning of a working day.
     * @param date the date from which to determine the start-of-day time
     * @return a date at the start time of a working day on the same day
     * as the specified date
     */
    Date getStartOfWorkingDay(Date date);

    /**
     * Returns a date that is at the start time on the next working day
     * following the specified date.
     * @param date the date from which to determine the start date and time
     * of the next working day
     * @return a date at the start time on the next available working day
     * @see #isWorkingDay
     * @see #getStartOfWorkingDay
     */
    Date getStartOfNextWorkingDay(Date date);
    
    /**
     * Returns a date that is at the end time on the previous working day
     * following the specified date.
     * @param date the date from which to determine the end date and time
     * of the previous working day
     * @return a date at the start time on the previous available working day
     * @see #isWorkingDay
     * @see #getEndOfWorkingDay
     */
    Date getEndOfPreviousWorkingDay(Date date);

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
    Date getStartOfNextWorkingTime(Date date) throws NoWorkingTimeException;

    /**
     * Get the end of the previous block of working time.
     *
     * @param date a <code>Date</code> object which will be the start of our
     * search for the end of the previous working time block.
     * @return a <code>Date</code> indicating the end of the previous block of
     * working time.
     * @throws NoWorkingTimeException if there is a problem locating
     * a previous working time end; for example the calendar defines no working
     * days
     */
    Date getEndOfPreviousWorkingTime(Date date) throws NoWorkingTimeException;

    /**
     * Returns a new date that is within a working time by rolling to the
     * next working time start.
     * @param date the date to check
     * @return the same date if the date is already in working time;
     * otherwise the next available working time start, which may be the next day
     * @throws NoWorkingTimeException if there is a problem locating
     * a working time start; for example the calendar defines no working
     * days
     */
    Date ensureWorkingTimeStart(Date date) throws NoWorkingTimeException;

    /**
     * Ensures a date is within working time or at the end of a working time
     * block.
     * <p>
     * This method is most useful for fixing an end time to be sensible; it
     * must be inside or at the end of a working time block, but never at
     * the beginning.
     * </p>
     * <p>
     * If the date is within working time, the same date is returned.
     * If the date is at the end of a working time block, the same date is
     * returned.
     * If the date is at the start of a working time block, it is rolled back
     * to the end of the previous working time block.
     * </p>
     * @param date the date to check
     * @return the same date if it is already in or at the end of working time;
     * otherwise the previous working time end, which may be the previous
     * day
     * @throws NoWorkingTimeException if there is a problem locating
     * a working time end; for example the calendar defines no working
     * days
     */
    Date ensureWorkingTimeEnd(Date date) throws NoWorkingTimeException;

}
