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
import java.util.TimeZone;

/**
 * Provides a WorkingTime which is a time range.
 * For example 8:00 AM to 12:00 PM, inclusive of the start time but
 * exclusive of the end time.
 *
 * @author Tim Morrow
 * @since Version 7.6.2
 */
public class WorkingTime {

    //
    // Static Members
    //

    /**
     * Specifies the scale we use for all calculations involving minutes,
     * currently <code>3</code>.
     * A scale of 3 ensures that our results are correct to the nearest minute.
     * For example, 40 minutes is 2/3 hour which is 0.667 using our scale.
     * 0.667 hours when multiplied by 60 gives 40.02 minutes, or 40 minutes to
     * the nearest minute.
     */
    static final int MINUTE_ARITHMETIC_SCALE = 10;

    /**
     * Specifies the time zone to use when converting times to dates
     * and vice-versa for storage; currently <code>Etc/UTC</code>.
     * This timezone should never be used when formatting
     * values. It should <b>never</b> be changed; doing so will affect all stored
     * working times and likely prevent any working time calendar from loading.
     */
    static final TimeZone TIME_CONVERSION_TIMEZONE = TimeZone.getTimeZone("Etc/UTC");

    //
    // Instance Members
    //

    /**
     * The start time of this time range.
     */
    private final Time startTime;

    /**
     * The end time of this time range.
     */
    private final Time endTime;

    /**
     * The calculated duration which is the amount of time in hours between
     * the start time and end time.
     */
    private final SimpleTimeQuantity duration;

    /**
     * Creates a new WorkingTime.
     * @param startHour the start hour in the range 0..23
     * @param startMinute the start minutes in the range 0..59
     * @param endHour the end hour in the range 0..23
     * @param endMinute the end minutes in the range 0..59
     * @throws IllegalArgumentException if the start time is after the end time
     */
    WorkingTime(int startHour, int startMinute, int endHour, int endMinute) {
        this(new Time(startHour, startMinute), new Time(endHour, endMinute));
    }

    /**
     * Creates a new WorkingTime.
     * @param startTime the start time
     * @param endTime the end time
     * @throws IllegalArgumentException if the start time is after the end time
     */
    public WorkingTime(Time startTime, Time endTime) {
        this.startTime = startTime;
        this.endTime = endTime;

        if (this.startTime.isAfter(this.endTime)) {
            throw new IllegalArgumentException("start time cannot be after end time");
        }

        this.duration = calculateDuration();
    }

    /**
     * Convenience method to create a working time from two date values.
     * <p>
     * The date portion is ignored; only the hour and minute of the date is
     * considered.  It assumes the date was created using the {@link #TIME_CONVERSION_TIMEZONE}
     * time zone.
     * </p>
     * <p>
     * This is in intended to conveniently construct a WorkingTime from a
     * database date value. Note that special handling ensures that midnight
     * endDates are correctly converted to "24:00" end times.
     * </p>
     * @param startDate the start date containing the time component of the
     * start time
     * @param endDate the date containing the time component of the end time
     * @throws IllegalArgumentException if the start time is after the end time
     */
    WorkingTime(Date startDate, Date endDate) {
        this(toTime(startDate, false), toTime(endDate, true));
    }

    /**
     * Helper method that converts dates to time.
     * <p>
     * The date portion is ignored, only the hour and minutes are considered.
     * It assumes the date was created suing the {@link #TIME_CONVERSION_TIMEZONE}
     * time zone.
     * </p>
     * @param date the date containing the time component to get
     * @param isEndTime true if this is an end time; false otherwise.
     * If this is an end time midnight dates will be returned as 24:00
     * @return the time component converted from the date
     */
    private static Time toTime(Date date, boolean isEndTime) {
        Calendar cal = Calendar.getInstance(TIME_CONVERSION_TIMEZONE);
        cal.setTime(date);

        Time time = new Time(cal);
        if (isEndTime && time.equals(new Time(0, 0))) {
            time = new Time(24, 0);
        }

        return time;
    }

    /**
     * Calculations the duration of this working time in hours given the
     * current start and end times.
     * @return the duration
     */
    private SimpleTimeQuantity calculateDuration() {
        return startTime.calculateDuration(endTime);
    }

    /**
     * Returns the start time.
     * @return the start time
     */
    public Time getStartTime() {
        return this.startTime;
    }

    /**
     * Returns the end time.
     * @return the end time
     */
    public Time getEndTime() {
        return this.endTime;
    }

    /**
     * Returns the total duration of this working time.
     * @return the duration
     */
    SimpleTimeQuantity getDuration() {
        return this.duration;
    }

    /**
     * Indicates whether the specified time is the start time of this
     * working time.
     * @param time the time to compare
     * @return true if the specified time is equal to the start time;
     * false otherwise
     */
    boolean isStartTime(Time time) {
        return this.startTime.equals(time);
    }

    /**
     * Indicates whether this working time contains the specified time.
     * @param time the time to check
     * @return true if this working time contains a time range containing
     * the specified hour and minute; false otherwise
     * Will return false if the end of the working time range is equal
     * to the hour and minute
     */
    boolean containsTime(Time time) {
        return (time.isOnOrAfter(this.startTime) && time.isBefore(this.endTime));
    }

    /**
     * Indicates whether the start time is after the specified time.
     * @param time the time to check
     * @return true if the start time is after the specified time
     */
    boolean isStartAfter(Time time) {
        return this.startTime.isAfter(time);
    }

    /**
     * Indicates whether the end time is on or before the specified time.
     * @param time the time to check
     * @return true if the end time is no or before the specified time
     */
    boolean isEndOnOrBefore(Time time) {
        return this.endTime.isOnOrBefore(time);
    }

    /**
     * Returns the duration between the end of this working time and the
     * specified time.
     * @param time the end time to use to calculate remaining duration
     * @return the duration
     */
    SimpleTimeQuantity getRemainingDuration(Time time) {
        return time.calculateDuration(this.endTime);
    }

    /**
     * Returns the duration between this working time's start time and
     * the specified time.
     * @param time the time to use to calculate duration
     * @return the duration
     */
    SimpleTimeQuantity getEarlierDuration(Time time) {
        return startTime.calculateDuration(time);
    }

    /**
     * Returns the string representation of this working time suitable for
     * debugging.
     * In the form <code><i>start time</i> - <i>end time</i> (<i>duration</i>)</code>
     * @return the string representation of this working time
     */
    public String toString() {
        return "" + this.startTime + " - " + this.endTime + "(" + this.duration + ")";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkingTime)) return false;

        final WorkingTime workingTime = (WorkingTime) o;

        if (!endTime.equals(workingTime.endTime)) return false;
        if (!startTime.equals(workingTime.startTime)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = startTime.hashCode();
        result = 29 * result + endTime.hashCode();
        return result;
    }

    /**
     * Indicates whether this working time's start time is between the
     * start and end time of the specified working time (including on the
     * start or end time).
     * @param workingTime the working time to check with
     * @return true if this working time's start time falls on or between
     * the specified working time's start and end; false otherwise
     */
    boolean isStartBetween(WorkingTime workingTime) {
        return (getStartTime().isOnOrAfter(workingTime.getStartTime()) && getStartTime().isOnOrBefore(workingTime.getEndTime()));
    }

    /**
     * Indicates whether this working time's end time is between the
     * start and end time of the specified working time (including on the
     * start or end time).
     * @param workingTime the working time to check with
     * @return true if this working time's end time falls on or between
     * the specified working time's start and end; false otherwise
     */
    boolean isEndBetween(WorkingTime workingTime) {
        return (getEndTime().isOnOrAfter(workingTime.getStartTime()) && getEndTime().isOnOrBefore(workingTime.getEndTime()));
    }

    /**
     * Indicates whether this working time's start time is before the specified
     * working time's start time.
     * @param workingTime the working time to check
     * @return true if this working time's start time is before;
     * false if it is on or after
     */
    boolean isStartBefore(WorkingTime workingTime) {
        return (getStartTime().isBefore(workingTime.getStartTime()));
    }


    /**
     * Indicates whether this working time's end time is after the specified
     * working time's end time.
     * @param workingTime the working time to check
     * @return true if this working time's end time is after;
     * false if it is on or before
     */
    public boolean isEndAfter(WorkingTime workingTime) {
        return (getEndTime().isAfter(workingTime.getEndTime()));
    }

    /**
     * Returns the end time after working the specified amount starting
     * at this working time's start time.
     * @param workTime the time worked
     * @return the end time
     * @throws IllegalArgumentException if the number of hours is greater
     * than the duration of this working time block
     */
    Time getEndTimeForWork(SimpleTimeQuantity workTime) {

        if (workTime == null) {
            throw new NullPointerException("workTime is required");
        }

        if (workTime.compareTo(getDuration()) > 0) {
            throw new IllegalArgumentException("workTime cannot be more than duration of working time");
        }

        // Calculate the end time given the working time
        return getStartTime().calculateEndTime(workTime);
    }

    /**
     * Returns the number of hours which are the intersection of this
     * working time and the specified working time.
     * @param nextWorkingTime the working time with which to intersect
     * @return the number of working hours that are common to both
     * working times; zero if there is no intersection
     */
    SimpleTimeQuantity getIntersectingHours(WorkingTime nextWorkingTime) {

        SimpleTimeQuantity intersectingHours;

        if (isStartBetween(nextWorkingTime) || isEndBetween(nextWorkingTime) || (isStartBefore(nextWorkingTime) && isEndAfter(nextWorkingTime))) {

            // Our time range overlaps with working time
            // We construct the intersection
            Time latestStartTime = (isStartBefore(nextWorkingTime) ? nextWorkingTime.getStartTime() : getStartTime());
            Time earliestEndTime = (isEndAfter(nextWorkingTime) ? nextWorkingTime.getEndTime() : getEndTime());
            WorkingTime intersection = new WorkingTime(latestStartTime, earliestEndTime);

            intersectingHours = intersection.getDuration();

        } else {
            intersectingHours = new SimpleTimeQuantity(0, 0);
        }

        return intersectingHours;
    }

}

