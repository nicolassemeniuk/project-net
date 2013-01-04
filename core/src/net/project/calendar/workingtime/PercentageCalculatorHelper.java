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
 * Provides a helper class for calculating percentage based on work and a date range.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class PercentageCalculatorHelper {

    private final TimeZone timeZone;
    private final WorkingTimeCalendarDefinition calendarDefinition;

    /**
     * Creates a PercentageCalculatorHelper using the specified working time calendar definition.
     * @param timeZone the time zone used to convert dates to calendars for performing date arithmetic
     * @param workingTimeCalendarDef the calendar definition used when calculating percentage
     */
    public PercentageCalculatorHelper(TimeZone timeZone,
            WorkingTimeCalendarDefinition workingTimeCalendarDef) {
        this.timeZone = timeZone;
        this.calendarDefinition = workingTimeCalendarDef;
    }

    /**
     * Returns the percentage of time that must be spent working to complete the
     * specified amount of work within the specified dates based on the current
     * working time calendar definition.
     * @param startDate the start date
     * @param endDate the end date
     * @param work the amount of work to be performed; greater or equal to zero.
     * When zero, the percentage will be zero
     * @return the percentage of time spent working; a value of <code>1.00</code> = 100%;
     * has a scale of 2
     * @throws IllegalArgumentException if work is less than zero
     */
    public BigDecimal getPercentage(Date startDate, Date endDate, TimeQuantity work) {

        if (work.getAmount().signum() < 0) {
            // Work is negative
            throw new IllegalArgumentException("Work amount must be greater or equal to zero");
        }

        BigDecimal percentage;

        if (work.getAmount().signum() == 0) {
            // Handle the special case of zero work
            // Results in zero percentage
            percentage = new BigDecimal("0.00");

        } else {
            // Non-zero work; continue to calculate the percentage
            Calendar startDateCal;
            Calendar endDateCal;

            startDateCal = new GregorianCalendar(this.timeZone);
            startDateCal.setTime(startDate);
            endDateCal = new GregorianCalendar(this.timeZone);
            endDateCal.setTime(endDate);

            // Percentage is work to do divided by actual hours
            // E.g. 8 hours of work, 16 actual hours:  Work 50%
            TimeQuantity actualHours = new TimeQuantity(calendarDefinition.getWorkingTimeAmountForDateRange(startDateCal, endDateCal).toMinutes(), TimeQuantityUnit.MINUTE);
            if (actualHours.isZero()) {
                percentage = new BigDecimal("0.00");
            } else {
                percentage = work.divide(actualHours, 2, BigDecimal.ROUND_HALF_UP);
            }
        }

        return percentage;
    }

}