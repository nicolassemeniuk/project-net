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
package net.project.schedule.report.resourceallocation;

import java.math.BigDecimal;
import java.util.Date;

import net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkCalculatorHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.security.SessionManager;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a helper for calculating the amount of work a resource
 * will do over a date range.
 *
 * @author Matthew Flower
 * @author Tim Morrow
 * @since Version 7.6.3
 */
class WorkCalculator {

    private final IWorkingTimeCalendarProvider provider;

    public WorkCalculator(IWorkingTimeCalendarProvider provider) {
        this.provider = provider;
    }

    /**
     * Get the amount of hours that a person would be working on a given day if
     * they were working the designated percentage of their day.
     * <p>
     * <b>04/01/2004 - Tim; Currently broken.  Does not take into account the resource's
     * time zone.  The dates are currently converted to calendars using the current user's
     * time zone. As a result, the work value will likely be wrong when the resource's time zone
     * differs from the current user's time zone.
     * </p>
     * @param resourceID the ID of the resource for whom to calculate the work hours
     * @param date1 a <code>Date</code> which is the start of a date range for
     * which we are going to compute the total amount of work.
     * @param date2 a <code>Date</code> which is the end of a date range for
     * which we are going to computer the total amount of work.
     * @param percentAssigned the amount of a person's day they'd actually be doing work
     *  (100 = 100%, 50 = 50%)
     * @return a <code>TimeQuantity</code> which indicates the amount of time
     * worked on a given day.
     */
    public TimeQuantity getHoursWorkedBetweenDates(String resourceID, Date date1, Date date2, int percentAssigned) {

        // Grab the calendar definition for this resource
        WorkingTimeCalendarDefinition resourceCalendarDef = this.provider.getForResourceID(resourceID);
        if (resourceCalendarDef == null) {
            resourceCalendarDef = provider.getDefault();
        }

        // 04/01/2004 - Tim
        // TODO Use resource's time zone instead of current user's time zone
        // TODO this problem previously occurred due to use of PnCalendar to convert
        // TODO dates to times in getWorkingTimeAmountForDateRange() method
        // TODO That is, the problem needs solved, but wasn't introduced by this refactoring
        WorkCalculatorHelper workCalcHelper = new WorkCalculatorHelper(new DefinitionBasedWorkingTimeCalendar(SessionManager.getUser().getTimeZone(), resourceCalendarDef));

        BigDecimal percentAssignedD = new BigDecimal(percentAssigned).divide(new BigDecimal(100), 3, BigDecimal.ROUND_HALF_UP);
        // Calculate work that can be done by resource between dates at their current percentage
        return workCalcHelper.getWork(date1, date2, percentAssignedD).convertTo(TimeQuantityUnit.HOUR, 3, BigDecimal.ROUND_HALF_UP);
    }

}
