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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.util;

import java.util.Calendar;
import java.util.Date;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DateFilter;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.FilterOperator;
import net.project.base.finder.FinderFilter;
import net.project.base.finder.FinderFilterList;
import net.project.calendar.PnCalendar;
import net.project.security.SessionManager;

/**
 * This class represents a date range.
 *
 * @author Matthew Flower
 * @since Version 7.6.2
 */
public class DateRange {

    //
    // Static members
    //

    /**
     * Returns a Date Range where the start date is a certain amount before
     * "now" and the end date is a certain amount after "now".
     * @param startOffsetAmount the amount before "now" to start
     * @param startUnit the start amount units (e.g. <code>Calendar.DAY</code>); a quantity of <code>startOffsetAmount</code>
     * of this unit will be subtracted from "now" to produce the start date
     * @param endOffsetAmount the amount after "now" to end
     * @param endUnit the end amount units; a quantity of <code>endOffsetAmount</code>
     * of this unit will be added to "now" to produce the end date
     * @return the DateRange representing the specified offsets from "now"
     */
    public static DateRange makeDateRange(int startOffsetAmount, int startUnit, int endOffsetAmount, int endUnit) {

        Calendar cal = new PnCalendar(SessionManager.getUser());
        Date now = new Date();

        // Subtract the start amount to get the start date
        cal.setTime(now);
        cal.add(startUnit, (startOffsetAmount * -1));
        Date startDate = cal.getTime();

        // Add the end amount to get the end date
        cal.setTime(now);
        cal.add(endUnit, endOffsetAmount);
        Date endDate = cal.getTime();

        return new DateRange(startDate, endDate);
    }

    //
    // Instance Members
    //

    /** The start of the date range. */
    private final Date rangeStart;

    /** The end of the date range. */
    private final Date rangeEnd;

    /**
     * A constructor for the date range object which preinitialized the start
     * and end of the date range.
     *
     * @param rangeStart a <code>Date</code> which indicates the start of the
     * date range.
     * @param rangeEnd a <code>Date</code> which indicates the end of the date
     * range.
     */
    public DateRange(Date rangeStart, Date rangeEnd) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }

    /**
     * Indicates if a start date has been specified for this date range.
     *
     * @return a <code>boolean</code> value indicating if a start date has been
     * identified for this date range.
     */
    public boolean isStartBounded() {
        return rangeStart != null;
    }

    /**
     * Indicates if an end date has been specified for this date range.
     *
     * @return a <code>boolean</code> indicating if this date range has an end
     * date set.
     */
    public boolean isEndBounded() {
        return rangeEnd != null;
    }

    /**
     * Get the start of this date range.
     *
     * @return a <code>Date</code> indicating the start of this date range.  This
     * will be set to zero if date range start hasn't been set yet.
     */
    public Date getRangeStart() {
        return rangeStart;
    }

    /**
     * Get the end of this date range.
     *
     * @return a <code>Date</code> object which indicates the end of the date
     * range.  This will be equal to null if a date range hasn't been set yet.
     */
    public Date getRangeEnd() {
        return rangeEnd;
    }

    /**
     * Get a time quantity corresponding to the difference in time between two
     * days.
     *
     * @param unit a <code>TimeQuantityUnit</code> describing the unit that we
     * want to result to be in.
     * @param scale an <code>int</code> describing the maximum number of decimal
     * places that will be accurate for this resulting amount of time.
     * @return a <code>TimeQuantity</code> describing the amount of time that
     * transpires between two dates.
     */
    public TimeQuantity getTimeQuantity(TimeQuantityUnit unit, int scale) {
        long milliDifference = java.lang.Math.abs(rangeEnd.getTime() - rangeStart.getTime());
        TimeQuantity quantityInMillis = new TimeQuantity(milliDifference, TimeQuantityUnit.MILLISECOND);

        return quantityInMillis.convertTo(unit, scale);
    }

    /**
     * Create a {@link net.project.base.finder.DateFilter} object which
     * corresponds to the dates in this object.
     *
     * @param def a <code>ColumnDefinition</code> that we want to filter on.
     * @param includeNulls a <code>boolean</code> which indicates if we should
     * allow database rows where the column is equal to null to be included in
     * the result set.
     * @return a <code>DateFilter</code> which is initialized to look for dates
     * that are in the date range indicated by this object.
     */
    public DateFilter getDateFilter(String filterID, ColumnDefinition def, boolean includeNulls) {
        DateFilter filterToReturn = new DateFilter(filterID, def, includeNulls);
        filterToReturn.setDateRangeStart(rangeStart);
        filterToReturn.setDateRangeFinish(rangeEnd);
        filterToReturn.setSelected(true);

        return filterToReturn;
    }

    /**
     * Get a filter that will catch any time that has elapsed.  Why you'd need
     * this is best illustrated with an example.  Let's say you had a task that
     * begin on Jan 1 and ends on Mar 1.  Someone wants to find what tasks they
     * have going on between Jan 15 and Feb 15.  They can't just make two filters
     * (start and end date) because that would result in:
     *
     * <pre>
     *   and ((t.start_date > Jan 15 and t.start_date < Feb 15) or
     *        (t.end_date > Jan 15 and t.end_date < Feb 15)
     * </pre>
     *
     * This wouldn't return the task.  Instead we need:
     *
     * <pre>
     *  and (t.start_date < Feb 15 and t.end_date > Jan 15)
     * </pre>
     *
     * That is exactly the where clause this filter would produce.
     *
     * @param startColumn a <code>ColumnDefinition</code> which represents the
     * column in the database for the start of the date range.
     * @param endColumn a <code>ColumnDefinition</code> which represents the
     * column in the database for the start of the date range.
     * @return a <code>FinderFilter</code> for the behavior described above.
     */
    public FinderFilter getElapsedTimeFilter(String id, ColumnDefinition startColumn, ColumnDefinition endColumn) {
        FinderFilterList list =  new FinderFilterList(id, FilterOperator.AND);
        list.setSelected(true);

        try {
            if (isEndBounded()) {
                DateFilter startDateFilter = new DateFilter("startDate", startColumn, false);
                startDateFilter.setSelected(true);
                startDateFilter.setDateRangeFinish(rangeEnd);
                list.add(startDateFilter);
            }

            if (isStartBounded()) {
                DateFilter endDateFilter = new DateFilter("endDate", endColumn, false);
                endDateFilter.setSelected(true);
                endDateFilter.setDateRangeStart(rangeStart);
                list.add(endDateFilter);
            }
        } catch (DuplicateFilterIDException e) {
            throw new RuntimeException("Duplicate ids in getElapsedTimeFilter().  Internal error.");
        }

        return list;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateRange)) {
            return false;
        }

        final DateRange dateRange = (DateRange) o;

        if (!rangeEnd.equals(dateRange.rangeEnd)) {
            return false;
        }
        if (!rangeStart.equals(dateRange.rangeStart)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = rangeStart.hashCode();
        result = 29 * result + rangeEnd.hashCode();
        return result;
    }

    /**
     * Returns a string representation of this date range suitable for debugging.
     * @return the date range as a string
     */
    public String toString() {
        return this.rangeStart + " - " + this.rangeEnd;
    }
}
