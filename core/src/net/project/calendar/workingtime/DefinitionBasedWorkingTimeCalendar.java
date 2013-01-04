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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Provides an IWorkingTimeCalendar implementation based on a single
 * working time calendar definition (which itself may be hierarchical).
 * <p>
 * Shows which days and times are working times for a given resource.
 * </p>
 * @author Matthew Flower
 * @author Tim Morrow
 * @since Version 7.4
 */
public class DefinitionBasedWorkingTimeCalendar implements IWorkingTimeCalendar {

    /**
     * The definition of this working time calendar.
     * This currently contain default working times.
     */
    private final WorkingTimeCalendarDefinition calendarDefinition;

    /**
     * Time zone for which the methods in this DefinitionBasedWorkingTimeCalendar apply.
     */
    private final TimeZone timeZone;

    /**
     * Creates a new DefinitionBasedWorkingTimeCalendar for the specified time zone.
     * @param timeZone the time zone used for date manipulation
     * @param calendarDef the definition that provides the settings (day of week, date etc.)
     * that affects how this calendar behaves
     */
    public DefinitionBasedWorkingTimeCalendar(TimeZone timeZone, WorkingTimeCalendarDefinition calendarDef) {
        this.timeZone = timeZone;
        this.calendarDefinition = calendarDef;
    }

    /**
     * Returns the time zone that this calendar is using for date conversions.
     * @return the time zone
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    /**
     * Returns the calendar definition that this calendar is using.
     * @return the working time calendar
     */
    public WorkingTimeCalendarDefinition getCalendarDefinition() {
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

//    /**
//     * Indicates whether the specified date is considered a working time
//     * when for the purposes of locating the end of working time.
//     * The start boundary of a working time block (e.g. 8:00 AM and 1:00 PM) are <b>not</b>
//     * considered to be working time since not time would have elapsed between
//     * the start of working time and the end date.
//     * @param date the date to check
//     * @return true if the specified date is a working time; false otherwise
//     */
//    private boolean isWorkingTimeForEnd(Date date) {
//        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
//        cal.setTime(date);
//
//        return this.calendarDefinition.isWorkingTimeForEnd(cal);
//    }

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
     * @see #getEndOfWorkingDay
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

        if (!this.calendarDefinition.isWorkingTimeForEnd(cal)) {
            // Specified date is not a working time for the purposes of
            // locating an end boundary; find the end of the
            // previous working time
            this.calendarDefinition.updateWithEndOfPreviousWorkingTime(cal);
        }

        return cal.getTime();
    }


}
