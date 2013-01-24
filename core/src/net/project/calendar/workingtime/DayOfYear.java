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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

/**
 * Provides an exact day of the year as an alternative to using a date
 * with no time component.
 * <p>
 * Typically storing, retrieving and displaying a specific date with
 * no time component is error prone.  When stored as a date with "midnight"
 * time, the date can change on display when formatted for users with
 * different time zones if it is not stored and retrieved using a
 * consistent time zone. <br>
 * This class enforces the use of a time zone when constructing a day of year
 * from a date.
 * </p>
 * <p>
 * To store and retrieve a DayOfYear value from the database use
 * {@link #getDateForStore(DayOfYear)} and {@link #makeFromStoredDate(Date)}.<br>
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class DayOfYear {

    //
    // Static members
    //

    /**
     * The time zone to use to convert to/from a date for storage.
     */
    private static final TimeZone STORAGE_CONVERSION_TIMEZONE = TimeZone.getTimeZone("Etc/UTC");

    /**
     * Creates a DayOfYear from the specified date that was loaded from persistent store.
     * @param storedDate the date from which to create the DayOfYear; this
     * date <b>must</b> have been created by {@link #getDateForStore(DayOfYear)} otherwise
     * the day of year may reflect a different year, month and day.
     * @return the DayOfYear for the specified stored date
     * @throws NullPointerException if parameter is null
     */
    static DayOfYear makeFromStoredDate(Date storedDate) {
        if (storedDate == null) {
            throw new NullPointerException("storedDate is required");
        }
        return new DayOfYear(storedDate, STORAGE_CONVERSION_TIMEZONE);
    }

    /**
     * Creates a Date from the specified DayOfYear suitable for storage such that
     * later calls to {@link #makeFromStoredDate(Date)} will return an equal
     * DayOfYear.
     * @param dayOfYear the day of year to store
     * @return the date for storage
     * @throws NullPointerException if parameter is null
     */
    static Date getDateForStore(DayOfYear dayOfYear) {
        if (dayOfYear == null) {
            throw new NullPointerException("dayOfYear is required");
        }
        return dayOfYear.toDate(STORAGE_CONVERSION_TIMEZONE);
    }

    //
    // Instance Members
    //

    /**
     * The year.
     */
    private final int year;

    /**
     * The month.
     */
    private final int month;

    /**
     * The day.
     */
    private final int day;

    /**
     * Makes a DayOfYear from the specified date that was created using
     * the specified time zone.
     * @param date the date containing the year, month and day; time
     * is ignored
     * @param timeZone the time zone to use to convert to components
     * @throws NullPointerException if date or timeZone is null
     */
    public DayOfYear(Date date, TimeZone timeZone) {
        if (date == null) {
            throw new NullPointerException("date is required");
        }
        if (timeZone == null) {
            throw new NullPointerException("timeZone is required");
        }

        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(date);
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH);
        this.day = cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Creates a DayOfYear for the specified date components.
     * @param year the year; must be >= 0
     * @param month the month in the range 0.11; Use {@link Calendar#JANUARY} .. {@link Calendar#DECEMBER}
     * @param day the day of the month; must be valid for the month and year
     * @throws IllegalArgumentException if the year, month or day is invalid
     */
    DayOfYear(int year, int month, int day) {
        validate(year, month, day);
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * Creates a DayOfYear from the year month and day components of
     * the specified calendar.
     * @param calendar the calendar from which to create the DayOfYear
     */
    public DayOfYear(Calendar calendar) {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Validates the specified components are within appropriate ranges
     * and are valid (for example, September 31 is invalid).
     * @param year the year
     * @param month the month
     * @param day the day
     * @throws IllegalArgumentException if the year, month or day is invalid
     */
    private static void validate(int year, int month, int day) {

        if (year < 0) {
            throw new IllegalArgumentException("invalid year");
        }

        if (month < Calendar.JANUARY || month > Calendar.DECEMBER) {
            throw new IllegalArgumentException("month " + month + " is not in the range " + Calendar.JANUARY + " .. " + Calendar.DECEMBER);
        }

        // Now check that the year month and day are all valid
        // with respect to each other
        // For example, we don't want January 32 to become February 1
        // Time zone is irrelevant for this check
        Calendar cal = Calendar.getInstance();
        updateCalendar(cal, year, month, day);
        if ((cal.get(Calendar.YEAR) != year) || (cal.get(Calendar.MONTH) != month) || (cal.get(Calendar.DAY_OF_MONTH)) != day) {
            throw new IllegalArgumentException("invalid year, month or day");
        }

    }

    /**
     * Updates the specified calendar with the year, month and day
     * ensuring that time components are set to zero.
     * @param cal the calendar to update
     * @param year the year
     * @param month the month
     * @param day the day
     */
    private static void updateCalendar(Calendar cal, int year, int month, int day) {
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(year, month, day, 0, 0, 0);
    }

    /**
     * Converts the DayOfYear to a midnight date using the specified time zone.
     * @param timeZone the time zone to use to convert to a date
     * @return the date at midnight on current day of the year
     * @throws NullPointerException if timeZone is null
     */
    public Date toDate(TimeZone timeZone) {
        if (timeZone == null) {
            throw new NullPointerException("timeZone is required");
        }
        Calendar cal = Calendar.getInstance(timeZone);
        updateCalendar(cal);
        return cal.getTime();
    }

    /**
     * Updates the specified calendar with this day of year's
     * year month and day, reseting hours, minutes, seconds and
     * milliseconds to zero.
     * @param cal the calnedar to update
     */
    public void updateCalendar(Calendar cal) {
        updateCalendar(cal, this.year, this.month, this.day);
    }

    /**
     * Indicates whether this day of year is before the specified
     * day of year.
     * @param dayOfYear the day of year to check
     * @return true if this is an earlier day than the specified day of year
     * @throws NullPointerException if dayOfYear is null
     */
    public boolean isBefore(DayOfYear dayOfYear) {

        if (dayOfYear == null) {
            throw new NullPointerException("dayOfYear is required");
        }

        boolean isBefore = false;

        if (this.year < dayOfYear.year) {
            isBefore = true;

        } else if (this.year == dayOfYear.year) {

            if (this.month < dayOfYear.month) {
                isBefore = true;
            } else if (this.month == dayOfYear.month) {
                isBefore = (this.day < dayOfYear.day);
            }

        }

        return isBefore;
    }

    /**
     * Indicates whether this day of year is after the specified
     * day of year.
     * @param dayOfYear the day of year to check
     * @return true if this is a later day than the specified day of year
     * @throws NullPointerException if dayOfYear is null
     */
    public boolean isAfter(DayOfYear dayOfYear) {

        if (dayOfYear == null) {
            throw new NullPointerException("dayOfYear is required");
        }

        boolean isAfter = false;

        if (this.year > dayOfYear.year) {
            isAfter = true;

        } else if (this.year == dayOfYear.year) {

            if (this.month > dayOfYear.month) {
                isAfter = true;
            } else if (this.month == dayOfYear.month) {
                isAfter = (this.day > dayOfYear.day);
            }

        }

        return isAfter;
    }

    /**
     * Returns a DayOfYear for each day between this day of year
     * and the specified day of year, inclusive of both.
     * @param endDayOfYear the end day of year
     * @return an unmodifiable collection where each element is a <code>DayOfYear</code>
     * that falls between this and the specified day of year
     */
    public Collection getAllBetween(DayOfYear endDayOfYear) {

        if (endDayOfYear == null) {
            throw new NullPointerException("endDayOfYear is required");
        }

        Calendar currentCal;
        Calendar endCal;

        // Time Zone is irrelevant; we use the default
        currentCal = Calendar.getInstance();
        updateCalendar(currentCal);
        endCal = Calendar.getInstance();
        endDayOfYear.updateCalendar(endCal);

        // Loop from start while on or before (same as "not after") end date
        Collection results = new ArrayList();
        do {
            // Add the current date to the list and increment it for the
            // next iteration
            results.add(new DayOfYear(currentCal));
            currentCal.add(Calendar.DATE, 1);
        } while (!currentCal.getTime().after(endCal.getTime()));

        return results;
    }

    /**
     * Indicates whether this day of year falls between, or is on, the
     * specified start and end days of year.
     * @param startDayOfYear the start
     * @param endDayOfYear the end
     * @return true if this day of year is on the start, on the end or
     * somewhere in between; false if it is before the start or after the end
     */
    public boolean isOnOrBetween(DayOfYear startDayOfYear, DayOfYear endDayOfYear) {
        return !(this.isBefore(startDayOfYear) || this.isAfter(endDayOfYear));
    }

    /**
     * Returns the DayOfYear one day previous to this.
     * @return the previous day
     */
    public DayOfYear previousDay() {
        // Time zone is irrelvant; subtracting a day is not affected by time zone
        Calendar currentCal = Calendar.getInstance();
        updateCalendar(currentCal);
        currentCal.add(Calendar.DAY_OF_MONTH, -1);
        return new DayOfYear(currentCal);
    }

    /**
     * Returns the DayOfYear one day after this.
     * @return the next day
     */
    public DayOfYear nextDay() {
        // Time zone is irrelvant; adding a day is not affected by time zone
        Calendar currentCal = Calendar.getInstance();
        updateCalendar(currentCal);
        currentCal.add(Calendar.DAY_OF_MONTH, 1);
        return new DayOfYear(currentCal);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DayOfYear)) {
            return false;
        }

        final DayOfYear dayOfYear = (DayOfYear) o;

        if (day != dayOfYear.day) {
            return false;
        }
        if (month != dayOfYear.month) {
            return false;
        }
        if (year != dayOfYear.year) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = year;
        result = 29 * result + month;
        result = 29 * result + day;
        return result;
    }

    /**
     * Returns the string representation of this day of year suitable
     * for debugging.
     * @return the string representation; month values are converted to range of 1..12
     */
    public String toString() {
        return year + "-" + (month + 1) + "-" + day;
    }

}
