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

 /*----------------------------------------------------------------------+
|
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.util.Calendar;

/**
 * Provides a simple time class maintaining hours and minutes.
 * <p>
 * Times are in the range 0:00 to 23:59.  A special time of 24:00 is
 * allowed to indicate the very end of a day.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.2
 */
public class Time {

    /**
     * The hour of this time.
     */
    private final int hour;

    /**
     * The minute of this time.
     */
    private final int minute;

    /**
     * Creates a new time with the specified hour and minute.
     * @param hour the hour in the range 0..23 in combination with a minute
     * value; OR the hour of 24 to indicate the end of the day.  In that case
     * minute must be zero
     * @param minute the minute in the range 0..59; must be zero if hour is 24
     * @throws IllegalArgumentException if there hour and minute are not in range
     * specified above
     */
    public Time(int hour, int minute) {

        if (hour == 24 && minute != 0) {
            throw new IllegalArgumentException("When hour is 24, minute must be 0");

        } else if ((hour != 24) && (hour < 0 || hour > 23) || (minute < 0 || minute > 59)) {
            throw new IllegalArgumentException("Working time hour must be in the range 0..23 and minute must be in the range 0..59");
        }

        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Creates a new time from the hour and minute of the specified calendar.
     * @param calendar the calendar from which to create the time
     */
    public Time(Calendar calendar) {
        this(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    /**
     * Creates a new time given the specified number of minutes since midnight.
     * @param minutesSinceMidnight the number of minutes since midnight
     */
    private Time(int minutesSinceMidnight) {
        // Integer division truncates the result
        this.hour = (minutesSinceMidnight / 60);
        this.minute = (minutesSinceMidnight - (this.hour * 60));
    }

    /**
     * Returns the hour value in the range 0..24.
     * @return  the hour value
     */
    public int getHour() {
        return this.hour;
    }

    /**
     * Returns the minute value in the range 0..59.
     * @return the minute value
     */
    public int getMinute() {
        return this.minute;
    }

    /**
     * Calculates the duration between this time and the specified time.
     * <p>
     * For example, 08:45 to 12:15 is a duration of 3.500 hours. <br>
     * </p>
     * @param endTime the end time
     * @return the duration
     * @throws ArithmeticException if the specified endTime is before this time
     */
    SimpleTimeQuantity calculateDuration(Time endTime) {
        return new SimpleTimeQuantity((endTime.toMinutesSinceMidnight() - this.toMinutesSinceMidnight()));
    }

    /**
     * Calculates the end time given the specified duration and this time.
     * @param duration the duration to add to get the end time
     * @return the end time as this time plus the duration
     */
    Time calculateEndTime(SimpleTimeQuantity duration) {
        SimpleTimeQuantity durationSinceMidnight = new SimpleTimeQuantity(toMinutesSinceMidnight()).add(duration);
        return new Time((int) durationSinceMidnight.toMinutes());
    }

    /**
     * Returns this time as minutes since midnight.
     * @return the hours multipled by 60 plus the minutes
     */
    private int toMinutesSinceMidnight() {
        return ((getHour() * 60) + getMinute());
    }

    /**
     * Indicates whether this time is on or after the specified time.
     * @param t the time to compare
     * @return true if this time is equal to or later than the specified time;
     * false otherwise
     */
    boolean isOnOrAfter(Time t) {
        return ((this.hour > t.hour) || (this.hour == t.hour && this.minute >= t.minute));
    }

    /**
     * Indicates whether this time is after the specified time.
     * @param t the time to compare
     * @return true if this time is later than the specified time;
     * false otherwise
     */
    boolean isAfter(Time t) {
        return ((this.hour > t.hour) || (this.hour == t.hour && this.minute > t.minute));
    }

    /**
     * Indicates whether this time is on or before the specified time.
     * @param t the time to compare
     * @return true if this time is equal to or earlier than the specified time;
     * false otherwise
     */
    boolean isOnOrBefore(Time t) {
        return ((this.hour < t.hour) || (this.hour == t.hour && this.minute <= t.minute));
    }

    /**
     * Indicates whether this time is before the specified time.
     * @param t the time to compare
     * @return true if this time is earlier than the specified time
     */
    boolean isBefore(Time t) {
        return ((this.hour < t.hour) || (this.hour == t.hour && this.minute < t.minute));
    }

    /**
     * Updates the specified calendar with this time.
     * @param cal the calendar to update; seconds and milliseconds are set to zero
     */
    void updateCalendar(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, this.hour);
        cal.set(Calendar.MINUTE, this.minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Time)) return false;

        final Time time = (Time) o;

        if (hour != time.hour) return false;
        if (minute != time.minute) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = hour;
        result = 29 * result + minute;
        return result;
    }

    /**
     * Returns a string representation suitable for debugging.
     * In the form <code><i>hour:minute</i></code>.
     * @return the string representation
     */
    public String toString() {
        return hour + ":" + (minute < 10 ? "0" + minute : "" + minute);
    }

}
