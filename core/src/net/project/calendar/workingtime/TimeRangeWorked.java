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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a time range that was worked.
 * <p/>
 * See {@link DaysWorked} for details on how to produce a structure of these items.
 * 
 * @author Tim Morrow
 * @since Version 7.7.1
 */
public class TimeRangeWorked {

    private final WorkingTime workingTime;
    private final Date startDateTime;
    private final Date endDateTime;

    /**
     * Creates a TimeRangeWorked for the specified DayOfYear and working time.
     * @param dayOfYear the day of year on which the times were worked
     * @param workingTime the times worked
     * @param timeZone the time zone to use to convert times to dates
     */
    TimeRangeWorked(DayOfYear dayOfYear, WorkingTime workingTime, TimeZone timeZone) {
        this.workingTime = workingTime;

        Calendar cal = new GregorianCalendar(timeZone);
        dayOfYear.updateCalendar(cal);

        workingTime.getStartTime().updateCalendar(cal);
        this.startDateTime = cal.getTime();

        workingTime.getEndTime().updateCalendar(cal);
        this.endDateTime = cal.getTime();
    }

    /**
     * Returns the start datetime for this time range.
     * @return the starting date and time
     */
    public Date getStartDateTime() {
        return this.startDateTime;
    }

    /**
     * Returns the end datetime for this time range.
     * @return the ending date and time
     */
    public Date getEndDateTime() {
        return this.endDateTime;
    }

    /**
     * Returns the work between the start and end datetimes.
     * @return the amount of work completed between the start and end datetimes
     */
    public TimeQuantity getWork() {
        return new TimeQuantity(this.workingTime.getDuration().toHour(), TimeQuantityUnit.HOUR);
    }

    /**
     * Returns a string representation of this time range, suitable for debugging.
     * @return the start time, end time and work
     */
    public String toString() {
        return getStartDateTime() + " - " + getEndDateTime() + " : " + getWork();
    }

}