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
package net.project.resource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.TimeZone;

import net.project.calendar.workingtime.AbstractWorkingTimeCalendar;
import net.project.calendar.workingtime.SimpleTimeQuantity;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * A working time calendar which indicates working time for an individual
 * resource without respect to how they are assigned to a given task.  This is
 * good for determining the amount of working time a person is assigned in a
 * given day.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class ResourceWorkingTimeCalendar extends AbstractWorkingTimeCalendar {
    public ResourceWorkingTimeCalendar(TimeZone timeZone, WorkingTimeCalendarDefinition calendarDef) {
        super(timeZone, calendarDef);
    }

    /**
     * Returns the number of units to allocated when calculating dates.
     * @return the number of units with a scale of 2; a value of <code>1</code> means 1 hour
     * of work will be completed in 1 hour.  A value of <code>0.5</code> means
     * 1 hour of work requires 2 hours to be completed.
     */
    protected BigDecimal calculateAllocatedResourceFactor() {
        return new BigDecimal("1.0");
    }

    /**
     * Get the amount of time that was worked on a given day.
     *
     * @param date a <code>Date</code> for which we want to look up the amount
     * of working time.
     * @return a <code>TimeQuantity</code> which indicates the amount of time
     * worked on a given day.
     */
    public TimeQuantity getHoursWorkedOnDate(Date date) {
        return getHoursWorkedOnDate(date, 100);
    }

    /**
     * Get the amount of hours that a person would be working on a given day if
     * they were working the designated percentage of their day.
     *
     * @param date a <code>Date</code> for which we want to look up the amount
     * of working time.
     * @param percentAssigned a <code>int</code> which indicates the amount of
     * a person's day they'd actually be doing work.  (e.g. 100=100%, 50=50%).
     * @return a <code>TimeQuantity</code> which indicates the amount of time
     * worked on a given day.
     */
    public TimeQuantity getHoursWorkedOnDate(Date date, int percentAssigned) {
        TimeQuantity totalWorkingTime = getWorkingTimeCalendarDefinition().getWorkingTimeAmountForDate(date, timeZone);

        return totalWorkingTime.multiply(new BigDecimal(percentAssigned).divide(new BigDecimal(100), 3, BigDecimal.ROUND_HALF_UP));
    }

    /**
     * Get the amount of hours that a person would be working on a given day if
     * they were working the designated percentage of their day.
     *
     * @param date1 a <code>Date</code> which is the start of a date range for
     * which we are going to compute the total amount of work.
     * @param date2 a <code>Date</code> which is the end of a date range for
     * which we are going to computer the total amount of work.
     * @param percentAssigned a <code>int</code> which indicates the amount of
     * a person's day they'd actually be doing work.  (e.g. 100=100%, 50=50%).
     * @return a <code>TimeQuantity</code> which indicates the amount of time
     * worked on a given day.
     */
    public TimeQuantity getHoursWorkedBetweenDates(Date date1, Date date2, int percentAssigned) {

        SimpleTimeQuantity stq = getWorkingTimeCalendarDefinition().getWorkingTimeAmountForDateRange(date1, date2, timeZone);
        TimeQuantity totalWorkingTime = new TimeQuantity(stq.toMinutes(), TimeQuantityUnit.MINUTE).convertTo(TimeQuantityUnit.HOUR, 3);
        return totalWorkingTime.multiply(new BigDecimal(percentAssigned).divide(new BigDecimal(100), 3, BigDecimal.ROUND_HALF_UP));
    }
}
