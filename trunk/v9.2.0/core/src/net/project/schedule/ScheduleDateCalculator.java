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

 package net.project.schedule;

import java.math.BigDecimal;
import java.util.Date;

import net.project.calendar.workingtime.DateCalculatorHelper;
import net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.schedule.calc.IDateCalculator;
import net.project.util.TimeQuantity;

/**
 * Provides a date calculator based on a schedule workingtime calendar.
 * <p>
 * No tasks or resources are considered. It allows an arbitrary amount of work to be specified;
 * this is most useful when calculating lag dates.
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
class ScheduleDateCalculator implements IDateCalculator {

    private final IWorkingTimeCalendarProvider provider;
    private final TimeQuantity work;

    /**
     * Creates a new date calculator based on a schedule default workingtime calendar
     * for the specified amount of work.
     * @param work the amount of work to be added to the date; when positive, the
     * date is calculated as expected.  When negative, the date is calculated in reverse.
     * @param provider the provider from which to get the calendar
     * @throws NullPointerException if work and provider are null
     */
    public ScheduleDateCalculator(TimeQuantity work, IWorkingTimeCalendarProvider provider) {

        if (provider == null || work == null) {
            throw new NullPointerException("provider and work are required");
        }

        this.provider = provider;
        this.work = work;
    }

    /**
     * Calculates when the current work could be completed based on the schedule
     * default working time calendar.
     * <p>
     * Assumes a single resource at 100% will complete the work.
     * <p>
     * @param startDate the date from which to calculate the finish date
     * @return the finish date; this will be further in the future than the start date if
     * work was positive.  Otherwise, it will be in the past.
     */
    public Date calculateFinishDate(Date startDate) {
        return calculateDate(startDate, true);
    }

    /**
     * Calculates when the current work could be started based on the schedule
     * default working time calendar.
     * <p>
     * Assumes a single resource at 100% will complete the work.
     * <p>
     * @param finishDate the date from which to calculate the start date
     * @return the start date; this will be further in the past than the finish date if
     * work was positive.  Otherwise, it will be in the future.
     */
    public Date calculateStartDate(Date finishDate) {
        return calculateDate(finishDate, false);
    }

    private Date calculateDate(Date date, boolean isForward) {
        // We decide whether to negate the work
        // When the work is negatve, the direction will actually be reversed
        // Here is an explanation:
        //   isForward   Work Sign   Converted Work Sign  Actual direction
        //     true       +ve          +ve                 forward
        //     true       -ve          -ve                 backward  (reversed)
        //     false      +ve          -ve                 backward
        //     false      -ve          +ve                 forward   (reversed)
        // When isForward = true and Work is -ve, the direction is reversed
        // When isForward = false and work is -ve, the direction is reversed
        TimeQuantity work = (isForward ? this.work : this.work.negate());
        DefinitionBasedWorkingTimeCalendar workingTimeCalendar = new DefinitionBasedWorkingTimeCalendar(provider.getDefaultTimeZone(), provider.getDefault());
        return new DateCalculatorHelper(workingTimeCalendar).calculateDate(date, work, new BigDecimal("1.00"));

    }

}