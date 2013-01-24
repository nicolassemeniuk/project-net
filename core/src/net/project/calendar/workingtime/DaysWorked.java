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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.project.schedule.Schedule;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.security.SessionManager;

/**
 * Maintains days worked by resources.
 * <p>
 * If two resource work the same hours on the same day, those hours are counted
 * once.  If two resources work different hours on the same day, all hours are
 * counted.  If two resources work overlapping hours on the same day, the
 * distinct hours are counted. <br>
 * Similarly, if two resources work during the same hour of a day for different
 * number of minutes, the overlapping minutes are counted only once.
 * </p>
 * <p>
 * When times worked for a particular day are added, the time zone is required
 * to ensure that two resources working the same hours in different time zones
 * are counted appropriately.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class DaysWorked implements IDaysWorked {

    /**
     * The time zone used when converting times to a date for storage in the map.
     */
    private static final TimeZone CONVERSION_TIMEZONE = TimeZone.getTimeZone("Etc/UTC");

    /**
     * Provides a lookup of working days to the set of hours worked on that day.
     * Each key is a <code>DayOfYear</code>, each value is an instance of
     * <code>AggregatedWorkingTimes</code>.
     * <p>
     * The DayOfYear and corresponding times are always based on UTC
     * </p>
     */
    private final Map days = new HashMap();

    /**
     * Returns the duration, in days, assuming an 8 hour day.
     * @return the duration based on an 8 hour day with a scale of 10
     */
    public BigDecimal getTotalDays() {
    	Schedule schedule = SessionManager.getSchedule();
    	int hoursPerDay = schedule != null && schedule.getHoursPerDay() != null ? schedule.getHoursPerDay().intValue() : ScheduleTimeQuantity.DEFAULT_HOURS_PER_DAY.intValue() ;
        return getDuration().toDay(hoursPerDay).setScale(10, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Returns the duration, in minutes.
     * @return the duration with a scale of zero
     */
    public BigDecimal getTotalMinutes() {
        return new BigDecimal(getDuration().toMinutes()).setScale(0, BigDecimal.ROUND_UNNECESSARY);
    }

    /**
     * Returns an unmodifiable map where each key is a <code>DayOfYear</code>
     * and each value is an <code>AggregatedWorkingTimes</code>.
     * <p/>
     * <b>Note:</b> Days and times are based on the <code>Etc/UTC</code> timezone.
     * @return the map of days to working times
     */
    public Map getDaysTimesMap() {
        return Collections.unmodifiableMap(days);
    }

    /**
     * Adds the specified days worked to this days worked.
     * <p/>
     * Times are merged on for days that have times in both the given and this
     * daysworked structure.
     * @param daysWorked the days worked to add to this
     */
    public void add(IDaysWorked daysWorked) {
        Map daysMap = daysWorked.getDaysTimesMap();
        for (Iterator it = daysMap.keySet().iterator(); it.hasNext();) {
            DayOfYear nextDayOfYear = (DayOfYear) it.next();
            addDay(nextDayOfYear, (AggregatedWorkingTimes) daysMap.get(nextDayOfYear));
        }
    }

    /**
     * Returns a list of the unique time ranges worked.
     * <p/>
     * This provides start and end dates, including the elapsed time between those dates
     * on which work was completed.
     * @return a list where each element is a {@link TimeRangeWorked}, sorted by start date.
     */
    public List getTimeRangesWorked() {

        List timeRangesWorked = new LinkedList();

        // Iterate over days, then iterate over working times creating a time range
        // for each working time
        for (Iterator iterator = this.days.keySet().iterator(); iterator.hasNext();) {
            DayOfYear nextDayOfYear = (DayOfYear) iterator.next();

            AggregatedWorkingTimes workingTimes = (AggregatedWorkingTimes) this.days.get(nextDayOfYear);
            for (Iterator workingTimesIterator = workingTimes.getTimes().iterator(); workingTimesIterator.hasNext();) {
                WorkingTime workingTime = (WorkingTime) workingTimesIterator.next();

                timeRangesWorked.add(new TimeRangeWorked(nextDayOfYear, workingTime, CONVERSION_TIMEZONE));
            }

        }

        Collections.sort(timeRangesWorked, new TimeRangeStartDateTimeComparator());

        return timeRangesWorked;
    }

    /**
     * Gets the earliest time range in this map.
     */
    public Date getEarliestWorkingTime() {
        Date earliestWorkingTime = null;

        for (Iterator it = days.keySet().iterator(); it.hasNext();) {
            DayOfYear dayOfYear = (DayOfYear) it.next();
            Date workingTimeStart = dayOfYear.toDate(CONVERSION_TIMEZONE);

            if (earliestWorkingTime == null || workingTimeStart.before(earliestWorkingTime)) {
                earliestWorkingTime = workingTimeStart;
            }
        }

        return earliestWorkingTime;
    }

    /**
     * Adds a working day specifying the times worked on that day in the specified time zone.
     * @param dayOfYear the date of the working day
     * @param times the working time blocks worked on that day
     * @param timeZone the time zone to use for converting day + times back to actual dates;
     * this is necessary to figure out the union of actual times worked across multiple time zones
     * @throws NullPointerException if either dayOfYear or times is null
     */
    void addDay(DayOfYear dayOfYear, AggregatedWorkingTimes times, TimeZone timeZone) {

        if (dayOfYear == null || times == null || timeZone == null) {
            throw new NullPointerException("dayOfYear, times and timeZone are required values");
        }

        // Calendars used for conversion to consistent timezone
        Calendar utcStartCal = Calendar.getInstance(CONVERSION_TIMEZONE);
        Calendar utcEndCal = Calendar.getInstance(CONVERSION_TIMEZONE);

        // Calendar based on specified time zone since this is the context
        // for the dayOfYear and times
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        for (Iterator it = times.getTimes().iterator(); it.hasNext();) {
            WorkingTime nextWorkingTime = (WorkingTime) it.next();

            // Convert the day and time to a date then set the UTC calendars
            utcStartCal.setTime(getDate(cal, dayOfYear, nextWorkingTime.getStartTime()));
            utcEndCal.setTime(getDate(cal, dayOfYear, nextWorkingTime.getEndTime()));

            DayOfYear startDayOfYear = new DayOfYear(utcStartCal);
            DayOfYear endDayOfYear = new DayOfYear(utcEndCal);
            if (!startDayOfYear.equals(endDayOfYear)) {
                // When converted to UTC, the working time spans a day
                // We need to produce 2 working times, one for each day

                // First working time is (start time - 24:00)
                // We add that working time to the day determined by the start day
                addTimeWorked(startDayOfYear, new WorkingTime(new Time(utcStartCal), new Time(24, 0)));

                // Second working time is (0:00 - end time)
                // We add that time to the day determined by the end day
                addTimeWorked(endDayOfYear, new WorkingTime(new Time(0, 0), new Time(utcEndCal)));


            } else {
                // Same day; we simply construct a new working time based on start and end times
                // And add it to the day determined by the start/end day (they are the same day)
                addTimeWorked(startDayOfYear, new WorkingTime(new Time(utcStartCal), new Time(utcEndCal)));

            }

        }

    }

    /**
     * Returns the date representing the day of year and time as determined
     * by the specified calendar.
     * @param timeZoneCal the calendar set to the correct timezone
     * @param dayOfYear the day of year
     * @param time the time
     * @return the date representing that day and time in the time zone of the calendar
     */
    private static Date getDate(Calendar timeZoneCal, DayOfYear dayOfYear, Time time) {
        // Update the day, month and year
        dayOfYear.updateCalendar(timeZoneCal);
        // Update the time
        timeZoneCal.set(Calendar.HOUR_OF_DAY, time.getHour());
        timeZoneCal.set(Calendar.MINUTE, time.getMinute());
        return timeZoneCal.getTime();
    }

    /**
     * Adds the specified working time as time worked on the specified day of year.
     * <p>
     * Updates the <code>days</code> map. <br>
     * <b>Note:</b> Both day of year and time must have been created in the {@link #CONVERSION_TIMEZONE}
     * time zone in order for times to be correctly detected as overlapping.
     * </p>
     * @param dayOfYear the day of year
     * @param workingTime the time span worked
     */
    private void addTimeWorked(DayOfYear dayOfYear, WorkingTime workingTime) {
        AggregatedWorkingTimes times = (AggregatedWorkingTimes) this.days.get(dayOfYear);
        if (times == null) {
            times = new AggregatedWorkingTimes();
            this.days.put(dayOfYear, times);
        }

        times.add(workingTime);
    }

    /**
     * Adds the specified times to the specified day of year.
     * <p>
     * Note: The dayOfYear and times must have been constructed from a UTC calendar.
     * To add times that were constructed from anothe time zone, use {@link #addDay(DayOfYear, AggregatedWorkingTimes, TimeZone)}.
     * </p>
     * @param dayOfYear the day of year to which to add the times
     * @param times the times to add; a copy of the times are used
     */
    private void addDay(DayOfYear dayOfYear, AggregatedWorkingTimes times) {
        AggregatedWorkingTimes timesWorked = (AggregatedWorkingTimes) this.days.get(dayOfYear);

        if (timesWorked == null) {
            // Create a new AggregatedWorkingTimes structure
            // We do this to clone the specified times
            timesWorked = new AggregatedWorkingTimes();
            this.days.put(dayOfYear, timesWorked);
        }

        // Update times
        timesWorked.add(times);
    }

    /**
     * Returns the duration.
     * @return the duration
     */
    SimpleTimeQuantity getDuration() {

        SimpleTimeQuantity total = new SimpleTimeQuantity(0, 0);

        // Add the duration of each aggregated working time
        for (Iterator it = this.days.keySet().iterator(); it.hasNext();) {
            DayOfYear nextDayOfYear = (DayOfYear) it.next();
            total = total.add(((AggregatedWorkingTimes) this.days.get(nextDayOfYear)).getDuration());
        }

        return total;
    }

    /**
     * Returns a formatted display suitable for debugging purposes.
     * @return a formatted display
     */
    public String toString() {
        return super.toString() + "[" + getDuration().toHour() + " hours]";
    }

    //
    // Nested top-level classes
    //

    /**
     * Provides a comparator based on start datetime of two time ranges.
     */
    private static class TimeRangeStartDateTimeComparator implements Comparator {

        /**
         * Compares two TimeRangeWorked objects.
         * @param o1 the first object to compare
         * @param o2 the second object to compare
         * @return the result of comparint start datetimes of the objects
         * @see Date#compareTo(java.util.Date)
         */
        public int compare(Object o1, Object o2) {

            TimeRangeWorked range1 = (TimeRangeWorked) o1;
            TimeRangeWorked range2 = (TimeRangeWorked) o2;

            return (range1.getStartDateTime().compareTo(range2.getStartDateTime()));
        }

    }
}
