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
package net.project.calendar.workingtime;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import net.project.schedule.ScheduleTimeQuantity;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;

/**
 * Try to determine (somewhat arbitrarily) when a person worked on an assignment.
 * This is mostly important for those times when someone updates the work %
 * complete for a schedule entry instead of having everyone enter their work.
 * We don't know the exact times that the person worked, so we try to guess.
 *
 * This algorithm will attempt to determine when else a person has worked during
 * the time range and it will try not to step on existing work.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class AssignmentLogCalculationHelper {
    private TimeZone timeZone;
    private WorkingTimeCalendarDefinition calendarDefinition;

    public AssignmentLogCalculationHelper(TimeZone timeZone, WorkingTimeCalendarDefinition calendarDefinition) {
        this.timeZone = timeZone;
        this.calendarDefinition = calendarDefinition;
    }

    public IDaysWorked getDaysWorked(Date startDate, TimeQuantity work, BigDecimal percentAssignedDecimal) {
        if (work.getAmount().signum() < 0) {
            // Work is negative
            throw new IllegalArgumentException("Work amount must be greater or equal to zero");
        }

        work = ScheduleTimeQuantity.convertToHour(work);
        DaysWorked daysWorked;

        if (work.isZero()) {
            //DaysWorked will be empty by default, we don't need to fill it.
            daysWorked = new DaysWorked();
        } else {
            // Positive amount of work

            // Handle special case of zero work factor
            // If assignment is assigned zero work, then their days worked is zero
            // Note: What happens when many assignments have zero work?
            // That depends on the task calculation type; however, assignment duration remains zero
            if (percentAssignedDecimal.signum() == 0) {
                daysWorked = new DaysWorked();

            } else {

                // Allocation structure provides remaining number of hours to
                // allocate to dates
                Allocation remainingWork = new Allocation(new BigDecimal("1.0"), work.getAmount());

                // Now figure out days worked
                Calendar cal = new GregorianCalendar(this.timeZone);
                cal.setTime(startDate);

                daysWorked = new DaysWorked();

                // Loop starting at the calendar's current date, allocating work
                // based on the number of hours available in the day
                // Recording the actual hours in the day worked (not just the count)
                // We abort if the working time calendar definition does not
                // have any working time available (at any point into the future) for
                // the current calendar date
                do {
                    if (calendarDefinition.isWorkingDay(cal)) {
                        //We have to make sure we don't allocate more hours than
                        //the user would work in a day for the given percentage.
                        //
                        //In the duration calculation, if you work at 25% you'll
                        //still get a IDaysWorked containing 8 hours.  We need it
                        //to be 2 hours, so we do the appropriate thing.
                        SimpleTimeQuantity workingHours = calendarDefinition.getWorkingHoursInDay(cal);
                        BigDecimal workForToday = workingHours.toHour().multiply(percentAssignedDecimal);
                        workForToday = workForToday.min(remainingWork.getActualTimeRemaining().toHour());
                        Allocation todaysAllocation = new Allocation(new BigDecimal("1.0"), workForToday);

                        // Now update the days worked for today, deducting from
                        // remaining work
                        calendarDefinition.updateDaysWorked(daysWorked, cal, todaysAllocation);
                        BigDecimal workAllocatedToday = workForToday.subtract(todaysAllocation.getActualTimeRemaining().toHour());
                        remainingWork.subtractActualHoursRemaining(workAllocatedToday);
                    }

                    // Increment date if we still have work to allocate
                    if (remainingWork.isWorkRemaining()) {
                        cal.add(Calendar.DATE, 1);

                        // We reset the calendar's time to the start of the day
                        // to be sure we count the whole day
                        DateUtils.zeroTime(cal);
                    }

                } while (remainingWork.isWorkRemaining() && calendarDefinition.isWorkingTimeAvailable(cal));
            }
        }

        return daysWorked;
    }

    public IDaysWorked getDaysWorkedFromEnd(Date finishDate, TimeQuantity work, BigDecimal percentAssignedDecimal) {
        if (work.getAmount().signum() < 0) {
            // Work is negative
            throw new IllegalArgumentException("Work amount must be greater or equal to zero");
        }

        work = ScheduleTimeQuantity.convertToHour(work);
        DaysWorked daysWorked;

        if (work.isZero()) {
            //DaysWorked will be empty by default, we don't need to fill it.
            daysWorked = new DaysWorked();
        } else {
            // Positive amount of work

            // Handle special case of zero work factor
            // If assignment is assigned zero work, then their days worked is zero
            // Note: What happens when many assignments have zero work?
            // That depends on the task calculation type; however, assignment duration remains zero
            if (percentAssignedDecimal.signum() == 0) {
                daysWorked = new DaysWorked();

            } else {

                // Allocation structure provides remaining number of hours to
                // allocate to dates
                Allocation remainingWork = new Allocation(new BigDecimal("1.0"), work.getAmount());

                // Now figure out days worked
                Calendar cal = new GregorianCalendar(this.timeZone);
                cal.setTime(finishDate);

                //Start at the beginning of today -- it isn't worth it to think
                //that it is done this very instant.
                DateUtils.zeroTime(cal);

                daysWorked = new DaysWorked();

                // Loop starting at the calendar's current date, allocating work
                // based on the number of hours available in the day
                // Recording the actual hours in the day worked (not just the count)
                // We abort if the working time calendar definition does not
                // have any working time available (at any point into the future) for
                // the current calendar date
                do {
                    if (calendarDefinition.isWorkingDay(cal)) {
                        //We have to make sure we don't allocate more hours than
                        //the user would work in a day for the given percentage.
                        //
                        //In the duration calculation, if you work at 25% you'll
                        //still get a IDaysWorked containing 8 hours.  We need it
                        //to be 2 hours, so we do the appropriate thing.
                        SimpleTimeQuantity workingHours = calendarDefinition.getWorkingHoursInDay(cal);
                        BigDecimal workForToday = workingHours.toHour().multiply(percentAssignedDecimal);
                        workForToday = workForToday.min(remainingWork.getActualTimeRemaining().toHour());
                        Allocation todaysAllocation = new Allocation(new BigDecimal("1.0"), workForToday);

                        // Now update the days worked for today, deducting from
                        // remaining work
                        calendarDefinition.updateDaysWorked(daysWorked, cal, todaysAllocation);
                        BigDecimal workAllocatedToday = workForToday.subtract(todaysAllocation.getActualTimeRemaining().toHour());
                        remainingWork.subtractActualHoursRemaining(workAllocatedToday);
                    }

                    // Increment date if we still have work to allocate
                    if (remainingWork.isWorkRemaining()) {
                        cal.add(Calendar.DATE, -1);

                        // We reset the calendar's time to the start of the day
                        // to be sure we count the whole day
                        DateUtils.zeroTime(cal);
                    }

                } while (remainingWork.isWorkRemaining() && calendarDefinition.isWorkingTimeAvailable(cal));
            }
        }

        return daysWorked;
    }
}
