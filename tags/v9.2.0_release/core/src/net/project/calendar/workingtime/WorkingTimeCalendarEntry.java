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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.util.DateUtils;

/**
 * Provides an entry in a working time calendar.
 * 
 * @author Tim Morrow
 * @since Version 7.6.0
 */
public abstract class WorkingTimeCalendarEntry {

	//
	// Static members
	//

	/**
	 * Creates a non-working day of week.
     * @param dayNumber the day of the week for which to create the entry
	 * @return the day of week entry
	 */
	public static DayOfWeekEntry makeNonWorkingDayOfWeek(int dayNumber) {
		return new DayOfWeekEntry(dayNumber, false, true);
	}

	/**
	 * Creates a working day of week with default working times.
     * @param dayNumber the day of the week for which to create the entry
	 * @return the day of week entry
	 */
	public static DayOfWeekEntry makeDefaultWorkingDayOfWeek(int dayNumber) {
		return new DayOfWeekEntry(dayNumber, true, true);
	}

	/**
	 * Creates a working day of week with no working times.
     * @param dayNumber the day of the week for which to create the entry
	 * @return the day of week entry
	 */
	public static DayOfWeekEntry makeWorkingDayOfWeek(int dayNumber) {
		return new DayOfWeekEntry(dayNumber, true, false);
	}

	/**
	 * Creates a non-working date.
     * @param dayOfYear the date which is not a working day
	 * @return the date entry
	 */
	public static DateEntry makeNonWorkingDate(DayOfYear dayOfYear) {
		return new DateEntry(dayOfYear, false, true);
	}

	/**
	 * Creates a non-working date for a range of dates.
     * @param startDayOfYear the first date that is not a working day
     * @param endDayOfYear the last date that is not a working day
	 * @return the date entry
	 */
	public static DateEntry makeNonWorkingDate(DayOfYear startDayOfYear, DayOfYear endDayOfYear) {
		return new DateEntry(startDayOfYear, endDayOfYear, false, true);
	}

	/**
	 * Creates a working date with no working times.
     * @param dayOfYear the date which is a working day
	 * @return the date entry
	 */
	public static DateEntry makeWorkingDate(DayOfYear dayOfYear) {
		return new DateEntry(dayOfYear, true, false);
	}

	/**
	 * Creates a working date for a range of dates no working times.
     * @param startDayOfYear the first date that is a working day
     * @param endDayOfYear the last date that is a working day
	 * @return the date entry
	 */
	public static DateEntry makeWorkingDate(DayOfYear startDayOfYear, DayOfYear endDayOfYear) {
		return new DateEntry(startDayOfYear, endDayOfYear, true, false);
	}

	//
	// Instance Members
	//

	/** The ID of the working time calendar to which this entry belongs. */
	private String calendarID = null;

	/**
     * The ID of this entry.
     * Populated after loading; it is a simple sequential number.
	 */
	private String entryID = null;

	/**
     * Indicates whether this is a working day.
     * A non-working day (false) will have empty working times.
	 */
	private boolean isWorkingDay;

	/**
	 * The working times for a working day.
	 */
	private final List workingTimes = new ArrayList();

	/**
	 * The total number of working hours in this day.
	 */
	private SimpleTimeQuantity workingHourTotal = new SimpleTimeQuantity(0, 0);
	
	/**
	 * The date descripton, optional field. 
	 */
	private String dateDescription = null;

	/**
	 * Creates a new calendar entry.
     * @param isWorkingDay true if this is a working day; false otherwise
     * @param isAddDefaultTimes true if we are to add default working times
     * (but only if this is a working day)
	 */
	protected WorkingTimeCalendarEntry(boolean isWorkingDay, boolean isAddDefaultTimes) {
		this.isWorkingDay = isWorkingDay;

		if (isAddDefaultTimes && this.isWorkingDay) {
            setWorkingTimes(Arrays.asList(new WorkingTime[]{
                new WorkingTime(8, 0, 12, 0),
                new WorkingTime(13, 0, 17, 0)
            }));
		}
	}

	/**
	 * For serialization only.
	 */
	protected WorkingTimeCalendarEntry() {
	}

	/**
     * Adds all the working times in the specified collection to this
     * working time calendar entry.
	 * <p>
	 * Clears the current working times first.
	 * </p>
     * @param workingTimes a collection where each element is a <code>WorkingTime</code>
     * @throws NullPointerException if workingTimes is empty
     * @throws IllegalArgumentException if working times is empty;
     * or one of the working times represents a zero minute span;
     * or the working times are not in earliest .. latest order
     * The state of this entry is preserved when an exception is thrown.
	 */
	public void setWorkingTimes(Collection workingTimes) {
		checkValidWorkingTimes(workingTimes);

		this.workingTimes.clear();
		this.workingHourTotal = new SimpleTimeQuantity(0, 0);
		for (Iterator it = workingTimes.iterator(); it.hasNext();) {
			WorkingTime nextWorkingTime = (WorkingTime) it.next();
			// Add and increment total hours
			this.workingTimes.add(nextWorkingTime);
			workingHourTotal = workingHourTotal.add(nextWorkingTime.getDuration());
		}
	}

	/**
	 * Checks to make sure the working times are valid.
     * @param workingTimes the working times to check
     * @throws NullPointerException if workingTimes is empty
     * @throws IllegalArgumentException if working times is empty;
     * or one of the working times represents a zero minute span;
     * or the working times are not in earliest .. latest order
	 */
	private void checkValidWorkingTimes(Collection workingTimes) {

		if (workingTimes == null) {
			throw new NullPointerException("workingTimes is required");
		}

		if (workingTimes.isEmpty()) {
			throw new IllegalArgumentException("Empty workingTimes");
		}

		WorkingTime previousWorkingTime = null;
		for (Iterator it = workingTimes.iterator(); it.hasNext();) {
			WorkingTime nextWorkingTime = (WorkingTime) it.next();

			// Check to make sure the duration is non zero
			if (nextWorkingTime.getDuration().isZero()) {
                throw new IllegalArgumentException("A working time must have a duration greater than zero");
			}

            // Check to make sure the next start time is after the previous end time
			if (previousWorkingTime != null && !nextWorkingTime.isStartAfter(previousWorkingTime.getEndTime())) {
                throw new IllegalArgumentException("The start of a working time must be later than the end of the previous working time");
			}

			previousWorkingTime = nextWorkingTime;
		}

	}

	/**
	 * Specifies the ID of the calendar to which this entry belongs.
     * @param calendarID the ID of the calendar
	 */
	void setCalendarID(String calendarID) {
		this.calendarID = calendarID;
	}

	/**
	 * Returns the ID of the calendar to which this entry belongs.
	 * @return the calendar ID
	 */
	String getCalendarID() {
		return this.calendarID;
	}

	/**
	 * Sets the entry ID of this entry.
     * @param entryID the ID of this entry
	 */
	void setEntryID(String entryID) {
		this.entryID = entryID;
	}

	/**
     * Returns the ID of this entry.
     * Only entries loaded from the database have an ID.
	 */
	String getEntryID() {
		return this.entryID;
	}
	
	/**
	 * @return the dateDescription
	 */
	public String getDateDescription() {
		return dateDescription;
	}

	/**
	 * @param dateDescription the dateDescription to set
	 */
	public void setDateDescription(String dateDescription) {
		this.dateDescription = dateDescription;
	}

	/**
	 * Returns an unmodifiable list of working times in this entry.
     * @return a list where each element is a <code>WorkingTime</code>;
     * may be empty if this is not a working day
	 */
	List getWorkingTimes() {
		return Collections.unmodifiableList(this.workingTimes);
	}

	/**
	 * Changed method access from default to public - for Export project functionality
     * Indicates whether this calendar entry is a working day or non-working day.
	 * @return true if it is a working day; false if it is a non-working day
	 */
	public boolean isWorkingDay() {
		return this.isWorkingDay;
	}

	/**
	 * Indicates whether the specified time is a working time.
     * @param time the time
	 * @return true if this is a working day and has a working time that
	 *         includes the specified time
	 */
	boolean isWorkingTime(Time time) {

		boolean isWorkingTime = false;

		if (isWorkingDay()) {

			// Loop over working times looking for one that contains
			// the specified hour and minute
			for (Iterator it = getWorkingTimes().iterator(); it.hasNext();) {
				WorkingTime nextWorkingTime = (WorkingTime) it.next();

				if (nextWorkingTime.containsTime(time)) {
					// We found a working time in which the specified time
					// occurs
					isWorkingTime = true;
					break;
				}
			}

		}

		return isWorkingTime;
	}

	/**
     * Indicates whether the specified time is a working time however if
     * the time represents the start of a working time block it is not
     * considered working time.
	 * <p>
	 * This is most useful when considering whether an end time is a working
     * time.  For example, if an end time is 8:00 AM, this is not considered
     * to be in working time since no time has elapsed in the working time
     * block.
	 * </p>
     * @param time the time to check
	 * @return true if this is a working day and has a working time that
	 *         includes the specified time; false otherwise
     * @throws NullPointerException if time is null
	 */
	boolean isWorkingTimeForEnd(Time time) {

		if (time == null) {
			throw new NullPointerException("time is required");
		}

		boolean isWorkingTime = false;

		if (isWorkingDay()) {

			// Loop over working times looking for one that contains
			// the specified hour and minute
			for (Iterator it = getWorkingTimes().iterator(); it.hasNext();) {
				WorkingTime nextWorkingTime = (WorkingTime) it.next();

				if (nextWorkingTime.isStartTime(time)) {
					// Time is the start of working time
					// Not considered working time for an end time
					isWorkingTime = false;
					break;

				} else if (nextWorkingTime.containsTime(time)) {
					// We found a working time in which the specified time
					// occurs
					isWorkingTime = true;
					break;
				}
			}

		}

		return isWorkingTime;
	}

	/**
	 * Returns the number of working hours in this day.
	 * @return the number of hours in the range 0..24.
	 */
	SimpleTimeQuantity getWorkingHours() {
		return this.workingHourTotal;
	}

	/**
     * Indicates whether this entry has working time that starts on or after
     * the specified time.
     * @param time the time
	 * @return true if there is a working time that starts on or after the
	 *         specified; false otherwise
	 */
	boolean hasNextWorkingTime(Time time) {
		return (getNextWorkingTime(time) != null);

	}

	/**
     * Indicates whether this entry has a working time that ends on or before the
     * specified time.
	 * <p>
	 * The previous working time is defined as the end of the working time that
     * precedes the specified time OR the working time that the time ends on,
     * if the time is exactly at the end of working time. <br>
     * For example, if the specified time is 17:30, the previous working time end
     * might be 17:00.  Similarly, if the specified time is 17:00, the previous
     * working time end would be 17:00.
	 * </p>
     * @param time the time
     * @return true if there is a working time that ends on or before the specified
     * time; false otherwise
	 */
	boolean hasPreviousWorkingTime(Time time) {
		return (getPreviousWorkingTime(time) != null);

	}

	/**
	 * Updates the specified calendar with the start time of the next working
	 * time that follows the specified time.
	 * <p>
	 * For example, if the specified time is 12:30, the next working time start
     * might be 13:00.  If the time is the start of working time then that
     * is used.
	 * </p>
	 * <p>
     * This method should only be called if {@link #hasNextWorkingTime}
     * returns true.
	 * </p>
     * @param cal the calendar who's time components to update; the calendar
	 *            hour and minute is updated; the second and millisecond values
	 *            are set to zero.
     * @param time the time on or after which to find the next working time
     * @throws IllegalStateException if there is no next working time
	 */
	void updateWithStartOfNextWorkingTime(Calendar cal, Time time) {

		WorkingTime workingTime = getNextWorkingTime(time);

		if (workingTime == null) {
            throw new IllegalStateException("No next working time found after " + time.getHour() + ":" + time.getMinute());
		}

		cal.set(Calendar.HOUR_OF_DAY, workingTime.getStartTime().getHour());
		cal.set(Calendar.MINUTE, workingTime.getStartTime().getMinute());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

	}

	/**
     * Updates the specified calendar with the time of the previous working time end.
	 * <p>
	 * The previous working time is defined as the end of the working time that
     * precedes the specified time OR the working time that the time ends on,
     * if the time is exactly at the end of working time. <br>
     * For example, if the specified time is 17:30, the previous working time end
     * might be 17:00.  Similarly, if the specified time is 17:00, the previous
     * working time end would be 17:00.
	 * </p>
	 * <p>
	 * This method should only be called if {@link #hasPreviousWorkingTime}
	 * returns true.
	 * </p>
     * @param cal the calendar who's time components to update; the calendar
	 *            hour and minute is updated; the second and millisecond values
	 *            are set to zero.
     * @param time the time before which to find the previous working time
     * @throws IllegalStateException if there is no previous working time
	 */
	void updateWithEndOfPreviousWorkingTime(Calendar cal, Time time) {

		WorkingTime workingTime = getPreviousWorkingTime(time);

		if (workingTime == null) {
            throw new IllegalStateException("No previous working time found after " + time.getHour() + ":" + time.getMinute());
		}

		cal.set(Calendar.HOUR_OF_DAY, workingTime.getEndTime().getHour());
		cal.set(Calendar.MINUTE, workingTime.getEndTime().getMinute());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

	}

	/**
	 * Updates the specified calendar with the time of the first working time.
     * @param cal the calendar who's time components to update; the calendar
     * hour and minute is updated; the second and millisecond values are set
     * to zero
     * @throws IllegalStateException if there is no first working time
	 */
	void updateWithStartOfFirstWorkingTime(Calendar cal) {

		if (getWorkingTimes().isEmpty()) {
			throw new IllegalStateException("No first working time");
		}

		WorkingTime workingTime = (WorkingTime) getWorkingTimes().get(0);

		cal.set(Calendar.HOUR_OF_DAY, workingTime.getStartTime().getHour());
		cal.set(Calendar.MINUTE, workingTime.getStartTime().getMinute());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	/**
     * Updates the specified calendar with the end time of the last working
     * time in this calendar entry.
	 * <p>
     * <b>Note:</b> The calendar may actually roll to the next day if the
     * last working time block in this entry ends at midnight.
     * In that case the calendar will be set to midnight on the next day.
	 * </p>
     * @param cal the calendar who's time components to update; the calendar
     * hour and minute is updated (and possibly day/month/year if necessary
     * to facilitate a midnight time);
     * The second and millisecond values are set to zero
     * @throws IllegalStateException if there is no last working time
	 */
	void updateWithEndOfLastWorkingTime(Calendar cal) {

		if (getWorkingTimes().isEmpty()) {
			throw new IllegalStateException("No last working time");
		}

		// Grab the last working time
		WorkingTime workingTime = (WorkingTime) getWorkingTimes().get(getWorkingTimes().size() - 1);

		// We add the hours and minutes to the current calendar's midnight
		// time to cause the calendar to
		DateUtils.zeroTime(cal);
		cal.add(Calendar.HOUR_OF_DAY, workingTime.getEndTime().getHour());
		cal.add(Calendar.MINUTE, workingTime.getEndTime().getMinute());
	}

	/**
     * Updates the specified calendar by adding the specified number of
     * working hours based on the working time of this entry.
	 * <p>
     * <b>Note:</b> the calendar may be set to midnight on the next day
     * if the number of hours to advance is exactly equal to the remaining
     * hours on a day with working time that ends at midnight (e.g. a 24-hour
     * or nightshift working time)
     * </p>
     * @param cal the calendar who's time components to update
     * @param workingHours the hours to add
     * @throws IllegalArgumentException if the specified workingHours is
     * greater than the number of working hours in this day or greater than
     * the number of hours remaining after the calendar's time
	 */
	void advanceHours(Calendar cal, SimpleTimeQuantity workingHours) {

        if (workingHours.compareTo(getWorkingHours()) > 0) {
            throw new IllegalArgumentException("Cannot advance more hours than available hours in day");
        }

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		boolean isAllTimeAssigned = false;

		for (Iterator it = getWorkingTimes().iterator(); it.hasNext() && !isAllTimeAssigned;) {
			WorkingTime nextWorkingTime = (WorkingTime) it.next();

			if (nextWorkingTime.isStartAfter(new Time(hour, minute))) {
				// The time is before the current working time
				// We can consider the entire working time block

				if (nextWorkingTime.getDuration().compareTo(workingHours) < 0) {
					// Duration of working time block not enough to satisfy
					// all working hours
					// We'll deduct the duration and continue with the
					// next working time
					workingHours = workingHours.subtract(nextWorkingTime.getDuration());

				} else {
					// Duration of working time block is more than enough to
					// satisfay working hours
					cal.set(Calendar.HOUR_OF_DAY, nextWorkingTime.getStartTime().getHour());
					cal.set(Calendar.MINUTE, nextWorkingTime.getStartTime().getMinute());
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					addDuration(cal, workingHours, true);
					isAllTimeAssigned = true;

				}

			} else if (nextWorkingTime.containsTime(new Time(hour, minute))) {
				// The time is inside the current working time block
				// We consider only the remaining time in the current block

				if (nextWorkingTime.getRemainingDuration(new Time(hour, minute)).compareTo(workingHours) < 0) {
					// Remaining duration of working time block is not enough
					// to satisfy all working hours
					// Deduct the remaining duration and continue with the
					// next working time
					workingHours = workingHours.subtract(nextWorkingTime.getRemainingDuration(new Time(hour, minute)));

				} else {
					// Remaining duration is sufficient
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					addDuration(cal, workingHours, true);
					isAllTimeAssigned = true;
				}

			}

		}

        if (!isAllTimeAssigned) {
            // We still have time left
            throw new IllegalArgumentException("Cannot advance more hours than remaining hours in day");
        }

	}

	/**
	 * Updates the specified calendar by subtracting the specified number of
	 * working hours based on the working time of this entry.
	 * <p>
     * If the calendar is set to midnight, then it is assumed to represent a time
     * of 24:00 on the previous day (that is, the end of the previous day).
     * Thus retarding the hours of that calendar by any amount will result on
     * a calendar set to the previous day.
     * </p>
     * @param cal the calendar who's time components to update; the date component is
     * updated if the calendar was set to midnight and workingHours is greater
     * than zero.
     * @param workingHours the hours to subtract
     * @throws IllegalArgumentException if the specified workingHours is
     * greater than the number of working hours in this day or greater than
     * the number of hours earlier than the calendar's time; the calendar
     * is not modified in this case
	 */
	void retardHours(Calendar cal, SimpleTimeQuantity workingHours) {

		if (workingHours.compareTo(getWorkingHours()) > 0) {
            throw new IllegalArgumentException("Cannot retard more hours than available hours in day");
		}

		// Use a copy of the calendar to avoid modifying the
		// original until we're done
		Calendar updateCalendar = (Calendar) cal.clone();
		Time time = new Time(updateCalendar);

		if (time.equals(new Time(0, 0))) {
			// Special case; when subtracting hours from a date that is
			// at midnight, we roll back to previous day
			updateCalendar.add(Calendar.DAY_OF_MONTH, -1);
			time = new Time(24, 0);
		}

		boolean isAllTimeAssigned = false;

		for (int i = getWorkingTimes().size() - 1; i >= 0 && !isAllTimeAssigned; i--) {
			WorkingTime nextWorkingTime = (WorkingTime) getWorkingTimes().get(i);

			if (nextWorkingTime.isEndOnOrBefore(time)) {
				// The time is after the current working time
				// We can consider the entire working time block

				if (nextWorkingTime.getDuration().compareTo(workingHours) < 0) {
					// Duration of working time block not enough to satisfy
					// all working hours
					// We'll deduct the duration and continue with the
					// next working time
					workingHours = workingHours.subtract(nextWorkingTime.getDuration());

				} else {
					// Duration of working time block is more than enough to
					// satisfay working hours
					updateCalendar.set(Calendar.HOUR_OF_DAY, nextWorkingTime.getEndTime().getHour());
					updateCalendar.set(Calendar.MINUTE, nextWorkingTime.getEndTime().getMinute());
					updateCalendar.set(Calendar.SECOND, 0);
					updateCalendar.set(Calendar.MILLISECOND, 0);
					addDuration(updateCalendar, workingHours, false);
					isAllTimeAssigned = true;

				}

			} else if (nextWorkingTime.containsTime(time)) {
				// The time is inside the current working time block
				// We consider only the remaining time in the current block

				if (nextWorkingTime.getEarlierDuration(time).compareTo(workingHours) < 0) {
					// Remaining duration of working time block is not enough
					// to satisfy all working hours
					// Deduct the remaining duration and continue with the
					// next working time
					workingHours = workingHours.subtract(nextWorkingTime.getEarlierDuration(time));

				} else {
					// Remaining duration is sufficient
					updateCalendar.set(Calendar.SECOND, 0);
					updateCalendar.set(Calendar.MILLISECOND, 0);
					addDuration(updateCalendar, workingHours, false);
					isAllTimeAssigned = true;
				}

			}

		}

		if (!isAllTimeAssigned) {
			// We still have time left
            throw new IllegalArgumentException("Cannot retard more hours than remaining hours in day");
		}

		// After success, update calendar
		cal.setTime(updateCalendar.getTime());
	}

	/**
     * Adds time in hours and fraction of hours to specified calendar.
     * Only adds hours and minutes; any fractional part shorter than a minute
     * are discared
     * @param cal the calendar to which to add the duration
     * @param duration the hours and fraction of hours to add
	 */
	private static void addDuration(Calendar cal, SimpleTimeQuantity duration, boolean isPositive) {

		BigDecimal durationHours = duration.toHour();

		// Hour part is the integer part
		int addHour = durationHours.intValue();

		// Minute part is the fractional part multipled by 60
		// We round the minutes in case our division didn't result
		// in an exact number of minutes
		int addMinute = durationHours.subtract(
				new BigDecimal(String.valueOf(addHour))).multiply(new BigDecimal("60")).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();

		cal.add(Calendar.HOUR_OF_DAY, (isPositive ? addHour : addHour * -1));
		cal.add(Calendar.MINUTE, (isPositive ? addMinute : addMinute * -1));
	}

	/**
	 * Gets the next working time that starts on after the specified time.
	 * <p>
	 * If the time is exactly equal to the start of working time, then that
	 * working time is returned, otherwise the working time that starts after
	 * the time is returned.
	 * </p>
     * @param time the time for which to get the next working time
	 * @return the next working time that starts on or after the specified time;
	 *         null if no working time is found that starts after
	 */
	private WorkingTime getNextWorkingTime(Time time) {

		WorkingTime foundWorkingTime = null;

		// Iterate over the working times in this entr
		// If we find a working time that starts after the specified time
		// then it is the next working time
		for (Iterator it = getWorkingTimes().iterator(); it.hasNext() && (foundWorkingTime == null);) {
			WorkingTime nextWorkingTime = (WorkingTime) it.next();
			if (nextWorkingTime.getStartTime().equals(time) || nextWorkingTime.isStartAfter(time)) {
				foundWorkingTime = nextWorkingTime;
			}
		}

		return foundWorkingTime;
	}

	/**
	 * Gets the previous working time that ends on orbefore the specified time.
     * If the time is on an end boundary, the working time is the one that
     * ends at that time.
     * @param time the time
     * @return the previous working time that ends on or before the specified time or
     * null if no working time is found that ends before
	 */
	private WorkingTime getPreviousWorkingTime(Time time) {

		WorkingTime foundWorkingTime = null;

		// Iterate over the working times in this entry in reverse order
		// If we find a working time that ends before the specified time
		// then it is the previous working time
		for (int i = getWorkingTimes().size() - 1; i >= 0 && (foundWorkingTime == null); i--) {
			WorkingTime previousWorkingTime = (WorkingTime) getWorkingTimes().get(i);
			if (previousWorkingTime.isEndOnOrBefore(time)) {
				foundWorkingTime = previousWorkingTime;
			}
		}

		return foundWorkingTime;
	}

	/**
	 * Returns the number of hours remaining in the day after the specified
	 * time.
	 * <p>
	 * <code>time</code> may be outside of working time or in working time.
     * When at the start of a working time block the entire block is counted (and
     * the subsequent blocks in the entry).
     * When in the middle of a working time block, the remainder of the block
     * is counted (and the subsequent blocks in the entry).
     * When at the end of a working time block that block is not counted.
     * When outside of working time, the following working time
	 * blocks are counted.
	 * </p>
     * @param time the time from which to get the remaining working hours
     * @return the remaining working hours in the day in the range
     * zero to <code>getWorkingHours</code>
	 */
	SimpleTimeQuantity getRemainingWorkingHours(Time time) {

		SimpleTimeQuantity remaining = new SimpleTimeQuantity(0, 0);

		if (isWorkingDay) {
			// This is a working day
			// First, we'll look to see if the specified hour and minute
			// is before the start of the day or after the end

			WorkingTime firstWorkingTime = (WorkingTime) getWorkingTimes().get(0);
			WorkingTime lastWorkingTime = (WorkingTime) getWorkingTimes().get(getWorkingTimes().size() - 1);

			if (firstWorkingTime.isStartAfter(time)) {
				// hour and minute precedes first working time
				// Remaining is sum of all working times in day
				remaining = remaining.add(this.getWorkingHours());

			} else if (lastWorkingTime.isEndOnOrBefore(time)) {
				// hour and minute is at end of last working time
				// Remaining stays zero

			} else {
				// hour and minute somewhere in middle of day
				// We must examine each working time to find out which ones
				// to add

				boolean isAddHours = false;

				for (Iterator it = getWorkingTimes().iterator(); it.hasNext();) {
					WorkingTime nextWorkingTime = (WorkingTime) it.next();

					if (isAddHours) {
						// We're simply adding the remaining hours
						remaining = remaining.add(nextWorkingTime.getDuration());

					} else {
						// We're still looking for the first working time

						if (nextWorkingTime.isStartAfter(time)) {
							// hour and minute was in non-working time
							// We've found the next working time
							remaining = remaining.add(nextWorkingTime.getDuration());
							isAddHours = true;

						} else if (nextWorkingTime.containsTime(time)) {
							// hour and minute is in this working time
							remaining = remaining.add(nextWorkingTime.getRemainingDuration(time));
							isAddHours = true;

						}

					}

				}

			}

		}

		return remaining;
	}

	/**
     * Returns the number of hours earlier in the day before the specified
     * time.
     * @param time the time before which to find the earlier working hours
     * @return the earlier working hours in the day in the range
     * zero to <code>getWorkingHours</code>; zero will be
     * returned if this is a non working day
	 */
	SimpleTimeQuantity getEarlierWorkingHours(Time time) {

		// Set the scale for enough precision when calculating fractional hours
		SimpleTimeQuantity remaining = new SimpleTimeQuantity(0, 0);

		if (isWorkingDay()) {
			// This is a working day
			// First, we'll look to see if the specified hour and minute
			// is before the start of the day or after the end

			WorkingTime firstWorkingTime = (WorkingTime) getWorkingTimes().get(0);
			WorkingTime lastWorkingTime = (WorkingTime) getWorkingTimes().get(getWorkingTimes().size() - 1);

			if (firstWorkingTime.isStartAfter(time)) {
				// hour and minute precedes first working time
				// Remaining stays zero

			} else if (lastWorkingTime.isEndOnOrBefore(time)) {
				// hour and minute is at end of last working time
				// Remaining is sum of all working times in day
				remaining = remaining.add(this.getWorkingHours());

			} else {
				// hour and minute somewhere in middle of day
				// We must examine each working time to find out which ones
				// to add

				boolean isAddHours = false;

				// Loop in reverse order
				for (int i = getWorkingTimes().size() - 1; i >= 0; i--) {
					WorkingTime nextWorkingTime = (WorkingTime) getWorkingTimes().get(i);

					if (isAddHours) {
						// We're simply adding the remaining hours
						remaining = remaining.add(nextWorkingTime.getDuration());

					} else {
						// We're still looking for the first working time

						if (nextWorkingTime.isEndOnOrBefore(time)) {
							// hour and minute in non working time or
							// at end boundary of working time
							remaining = remaining.add(nextWorkingTime.getDuration());
							isAddHours = true;

						} else if (nextWorkingTime.containsTime(time)) {
							// hour and minute is in this working time
							remaining = remaining.add(nextWorkingTime.getEarlierDuration(time));
							isAddHours = true;

						}

					}

				}

			}

		}

		return remaining;
	}

	/**
	 * Returns the number of hours in the day between the specified times.
     * @param startTime the time after which to find working hours
     * @param endTime the time before which to find working hours
     * @return the working hours in the day between the times in the range
     * zero to <code>getWorkingHours</code>; zero will be
     * returned if this is a non working day
	 */
	SimpleTimeQuantity getWorkingHoursBetween(Time startTime, Time endTime) {

		SimpleTimeQuantity remaining = new SimpleTimeQuantity(0, 0);

		if (isWorkingDay) {

			// We construct a working time representing the range
			// add up all the intersecting work hours
			WorkingTime range = new WorkingTime(startTime, endTime);
			for (Iterator it = workingTimes.iterator(); it.hasNext();) {
				WorkingTime nextWorkingTime = (WorkingTime) it.next();
				remaining = remaining.add(nextWorkingTime.getIntersectingHours(range));
			}

		}

		return remaining;
	}

	/**
	 * Returns the string representation of this WorkingTimeCalendarEntry,
	 * suitable for debugging only.
	 * @return the string representation
	 */
	public String toString() {
		return "[isWorkingDay=" + isWorkingDay + ", workingTimes=" + workingTimes + "]";
	}

	/**
     * Two working time calendar entries are equal if they are both
     * working days or non working days and if working days have the same
     * working times.
     * @param o the working time calendar entry to compare
     * @return true if the specified working time calendar entry is equal
     * to this one
	 */
	public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkingTimeCalendarEntry)) return false;

		final WorkingTimeCalendarEntry workingTimeCalendarEntry = (WorkingTimeCalendarEntry) o;

        if (isWorkingDay != workingTimeCalendarEntry.isWorkingDay) return false;
        if (!workingTimes.equals(workingTimeCalendarEntry.workingTimes)) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (isWorkingDay ? 1 : 0);
		result = 29 * result + workingTimes.hashCode();
		return result;
	}

	/**
	 * Returns the times that are worked in this entry to complete the specified
	 * amount of work assuming work starts at the specified time.
	 * <p>
     * If the specified amount of work is more than can be completed after
     * the specified time then the return value will simply reflect a smaller
     * amount of time; it will actually be the remainder of the day. <br>
	 * If the specified amount of work is less than the available working hours
	 * after the start time, then the return value will reflect the same number
	 * of hours as the work.
	 * </p>
     * @param time the time at which work starts; may be working or non working time
     * @param work the maximum amount of work to do
     * @return the times worked from the specified time to the earlier of
     * end of day or point at which work is complete
	 */
	AggregatedWorkingTimes getTimesWorkedAfter(Time time, SimpleTimeQuantity work) {

		SimpleTimeQuantity remainingWork = work;
		AggregatedWorkingTimes timesWorked = new AggregatedWorkingTimes();

		// Check each working time to see if it is relevant given the start
		// time, ending only when the remaining work is complete
		for (Iterator it = getWorkingTimes().iterator(); it.hasNext() && !remainingWork.isZero();) {
			WorkingTime nextWorkingTime = (WorkingTime) it.next();

			if (!nextWorkingTime.isEndOnOrBefore(time)) {
				// Working time does not end before specified start time
				// Some work is done during it

				if (nextWorkingTime.containsTime(time)) {
					// Only part of the working time is to be worked
					// We tweak the working time to reflect that part
					nextWorkingTime = new WorkingTime(time, nextWorkingTime.getEndTime());
				}

				if (nextWorkingTime.getDuration().compareTo(remainingWork) <= 0) {
					// The working time is shorter or equal to remaining work
					// The whole working time is worked
					timesWorked.add(nextWorkingTime);
					remainingWork = remainingWork.subtract(nextWorkingTime.getDuration());

				} else {
					// The working time is longer than the remaining work
					// Only some of the working time is worked; all the work
					// is done
					timesWorked.add(new WorkingTime(nextWorkingTime.getStartTime(), nextWorkingTime.getEndTimeForWork(remainingWork)));
					remainingWork = new SimpleTimeQuantity(0, 0);

				}

			}

		}

		return timesWorked;
	}

}
