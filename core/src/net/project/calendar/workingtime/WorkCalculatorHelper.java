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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a helper class for calculating available work between two dates.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class WorkCalculatorHelper {

    /**
     * The definition of this working time calendar.
     * This currently contain default working times.
     */
    private final WorkingTimeCalendarDefinition calendarDefinition;

    /**
     * Time zone for converting dates to calendars.
     */
    private final TimeZone timeZone;

    /**
     * Creates a WorkCalculatorHelper using the specified working time calendar to
     * determine the amount of work.
     *
     * @param workingTimeCalendar the calendar
     */
    public WorkCalculatorHelper(DefinitionBasedWorkingTimeCalendar workingTimeCalendar) {
        this.timeZone = workingTimeCalendar.getTimeZone();
        this.calendarDefinition = workingTimeCalendar.getCalendarDefinition();
    }

    /**
     * Calculates the amount of work between the specified dates given the specified
     * percentage assignment.
     * @param startDate the starting date
     * @param endDate the ending date
     * @param percentAssignedDecimal the percentage of time spent working where
     * <code>1</code> = 100% and <code>0.5</code> = 50%
     * @return the work that can be accomplished between two dates; this is the product
     * of the actual work hours and the percentage assignment. The unit is MINUTES to maintain precision.
     */
    public TimeQuantity getWork(Date startDate, Date endDate, BigDecimal percentAssignedDecimal) {

        TimeQuantity work;

        if (percentAssignedDecimal.signum() == 0) {
            // Handle the special case of zero percentage
            // No work will be done, so no need to calculate actual work
            work = new TimeQuantity(0, TimeQuantityUnit.MINUTE);

        } else {
            // Non-zero percentage; continue to calculate the work
            Calendar startDateCal;
            Calendar endDateCal;

            startDateCal = new GregorianCalendar(this.timeZone);
            startDateCal.setTime(startDate);
            endDateCal = new GregorianCalendar(this.timeZone);
            endDateCal.setTime(endDate);

            // Assignment work is actual work multiplied by assignment %
            work = new TimeQuantity(calendarDefinition.getWorkingTimeAmountForDateRange(startDateCal, endDateCal).toMinutes(), TimeQuantityUnit.MINUTE).multiply(percentAssignedDecimal);
        }

        return work;
    }

}