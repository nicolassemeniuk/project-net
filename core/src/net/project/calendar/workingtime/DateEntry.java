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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.util.Calendar;
import java.util.Collection;

/**
 * Provides a WorkingTimeCalendarEntry that represents a specific date or
 * range of dates.
 * <p>
 * Maintains no time comonent.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.0
 */
class DateEntry extends WorkingTimeCalendarEntry {

    /**
     * The start date of this entry.
     */
    private final DayOfYear startDayOfYear;

    /**
     * The end date of this entry.
     */
    private final DayOfYear endDayOfYear;

    /**
     * Create a new DateEntry for the specified date.
     * <p>
     * The end date is set to the start date, meaning this entry applies to
     * a single day only.
     * </p>
     * @param startDayOfYear the start date of this entry
     * @param isWorkingDay true if this is a working day; false if it is
     * a non-working day
     * @param isAddDefaultTimes true if we are to add default working times;
     * false if now working times are to be added
     * @throws NullPointerException if the startDate is null
     */
    DateEntry(DayOfYear startDayOfYear, boolean isWorkingDay, boolean isAddDefaultTimes) {
        this(startDayOfYear, startDayOfYear, isWorkingDay, isAddDefaultTimes);
    }

    /**
     * Create a new DateEntry for the specified date.
     * <p>
     * If the end date is on a different day than (and is after) the start date
     * then this entry spans all the days between the start date and end date
     * inclusive.  That is, each day is considered to be set to the same value.
     * </p>
     * @param startDayOfYear the start date of this entry
     * @param endDayOfYear the end date of this entry
     * @param isWorkingDay true if this is a working day; false if it is
     * a non-working day
     * @param isAddDefaultTimes true if we are to add default working times;
     * false if no working times are to be added
     * @throws NullPointerException if the startDate is null
     * @throws IllegalArgumentException if the endDate is before the start date
     */
    DateEntry(DayOfYear startDayOfYear, DayOfYear endDayOfYear, boolean isWorkingDay, boolean isAddDefaultTimes) {
        super(isWorkingDay, isAddDefaultTimes);

        if (startDayOfYear == null) {
            throw new NullPointerException("startDayOfYear is required");
        }

        if (endDayOfYear == null) {
            throw new NullPointerException("endDayOfYear is required");
        }

        if (endDayOfYear.isBefore(startDayOfYear)) {
            throw new IllegalArgumentException("endDayOfYear cannot be before startDayOfYear");
        }

        this.startDayOfYear = startDayOfYear;
        this.endDayOfYear = endDayOfYear;
    }

    /**
     * Returns the start date for this date entry.
     * @return the start date; this is the exact date passed to this
     * DateEntry during construction
     */
    public DayOfYear getStartDayOfYear() {
        return this.startDayOfYear;
    }

    /**
     * Returns the end date for this date entry.
     * @return the end date; this is the exact date passed to this
     * DateEntry during construction
     */
    public DayOfYear getEndDayOfYear() {
        return this.endDayOfYear;
    }

    /**
     * Returns the days that this entry spans.
     * <p>
     * If this entry is for a single date (start date equals end date) then
     * one date (the start date) is returned.  If the end date is after the start date,
     * a number of dates are returned.  There will be one date for each day
     * between the start date and end date, inclusive. <br>
     * For example, consider a start date of "7/1/03" and an end date of "7/3/03".
     * This method would return three values: "7/1/03", "7/2/03", "7/3/03".
     * </p>
     * @return a collection where each element is a <code>DayOfYear</code>
     */
    Collection getSpanDayOfYear() {
        return this.startDayOfYear.getAllBetween(this.endDayOfYear);
    }

    /**
     * Indicates whether this date entry represents a single date or a range of dates.
     * @return true if it is a single date; false if it is a range of date
     * (even if only a 2 day range)
     */
    public boolean isSingleDate() {
        // Compare times to avoid potential equality issues
        // with dates constructed from java.sql.Timestamps
        return (getStartDayOfYear().equals(getEndDayOfYear()));
    }

    /**
     * Indicates whether this date entry has working time on, or after,
     * the specified calendar's date.
     * @param cal the calendar containing the date and time to check
     * @return true if this date entry occurs on the calendar's date (and
     * working time is available); false if working time for this date entry
     * occurs before the specified calendar's date and time
     */
    boolean isWorkingTimeOnOrAfter(Calendar cal) {

        boolean isOnOrAfter;

        DayOfYear dayOfYear = new DayOfYear(cal);

        if (!isWorkingDay()) {
            // This isn't a working day, so no working time
            isOnOrAfter = false;

        } else if (this.startDayOfYear.isAfter(dayOfYear)) {
            // Starts after date
            isOnOrAfter = true;

        } else if (this.endDayOfYear.isBefore(dayOfYear)) {
            // Ends before date
            isOnOrAfter = false;

        } else {
            // Calendar date falls somewhere between this start and end date
            // We check to ensure there is working time
            // We must be careful to check edge conditions:  date falls on start date or end date
            if (this.startDayOfYear.equals(dayOfYear)) {
                // Start date and date are on the same day
                // Check working time on start date, or end date is on another day

                if (this.startDayOfYear.equals(this.endDayOfYear)) {
                    // End date on same day as start date
                    // We must check working time

                    if (!this.getRemainingWorkingHours(new Time(cal)).isZero()) {
                        // There is some work hours remining after time
                        // We're good
                        isOnOrAfter = true;

                    } else {
                        // No more time on date
                        isOnOrAfter = false;
                    }

                } else {
                    // End date is on a different day as start date
                    // There will be some working time available
                    isOnOrAfter = true;
                }

            } else if (this.endDayOfYear.equals(dayOfYear)) {
                // End date and date are on same day
                // If we have working time on end date, then we're ok

                if (!this.getRemainingWorkingHours(new Time(cal)).isZero()) {
                    // There is some work hours remining after time
                    // We're good
                    isOnOrAfter = true;

                } else {
                    // No more time on date
                    isOnOrAfter = false;
                }


            } else {
                // Falls on a day somewhere between start and end date;
                // We have working time
                isOnOrAfter = true;
            }

        }

        return isOnOrAfter;
    }

    /**
     * Indicates whether this entry is overlapped by the current entry.
     * @param entry the entry to check with this one
     * @return true if the specified entry's date or any date in its date range
     * fall on the same date as this entry, or any date in this entry's date range
     */
    public boolean isOverlappedBy(DateEntry entry) {

        // overlap = entry start date between this start and end date  (starts between)
        //         or entry end date between this start and end date   (ends between)
        //         or entry start date before this start and end after (spans across)
        return ((entry.getStartDayOfYear().isOnOrBetween(getStartDayOfYear(), getEndDayOfYear())) ||
                (entry.getEndDayOfYear().isOnOrBetween(getStartDayOfYear(), getEndDayOfYear())) ||
                (entry.getStartDayOfYear().isBefore(getStartDayOfYear()) && entry.getEndDayOfYear().isAfter(getEndDayOfYear())));
    }

    public String toString() {
        String result;

        if (isSingleDate()) {
            result = "[" + this.startDayOfYear + "]";
        } else {
            result = "[" + this.startDayOfYear + " - " + this.endDayOfYear + "]";
        }

        result += super.toString();

        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateEntry)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final DateEntry dateEntry = (DateEntry) o;

        if (!endDayOfYear.equals(dateEntry.endDayOfYear)) {
            return false;
        }
        if (!startDayOfYear.equals(dateEntry.startDayOfYear)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + startDayOfYear.hashCode();
        result = 29 * result + endDayOfYear.hashCode();
        return result;
    }

    /**
     * Copies this DateEntry, creating a new DateEntry based on
     * it but with the specified start date and end date.
     * <p>
     * All other properties of the entry will be the same.
     * </p>
     * @param newStartDayOfYear the start date of the copy
     * @param newEndDayOfYear the end date of the copy
     * @return the copy
     */
    public DateEntry copy(DayOfYear newStartDayOfYear, DayOfYear newEndDayOfYear) {
        // The new date entry has no default entries since we'll add them below
        DateEntry newEntry = new DateEntry(newStartDayOfYear, newEndDayOfYear, isWorkingDay(), false);
        newEntry.setCalendarID(getCalendarID());
        newEntry.setEntryID(getEntryID());
        if (!getWorkingTimes().isEmpty()) {
            newEntry.setWorkingTimes(getWorkingTimes());
        }
        return newEntry;
    }

}
