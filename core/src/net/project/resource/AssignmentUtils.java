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

import net.project.schedule.ScheduleTimeQuantity;
import net.project.util.TimeQuantity;

public class AssignmentUtils {
    /**
     * Calculates the amount of work allocated.
     * Defined as (totalWork * percentage) / totalPercentage)).
     * <b>Note:</b> Despite the fact that MS Project displayed allocated
     * work with a scale of 2, they perform the calculation with at least a
     * scale of 3.
     * @return the appropriate amount of work performed based on the assigned
     * percentage with a scale of 3.
     */
    public static TimeQuantity calculateAllocatedWork(TimeQuantity totalWork, BigDecimal percentage, BigDecimal totalPercentage) {
        TimeQuantity allocatedWork;

        // Convert the work to hours to improve precision of calculations
        totalWork = ScheduleTimeQuantity.convertToHour(totalWork);
        
        // Handle case where total percentage is zero
        // This may happen if one or more people are assigned with zero
        // percentage
        if (totalPercentage.signum() == 0) {
            // Zero percentage means the work is 100% allocated
            allocatedWork = totalWork;

        } else {
            // Non-zero percentage.  Now calculate allocated work

            // Calculate the amount of work done based on the total work
            // and the percentage allocated out of the total percentage
            // Calculation performed as (totalWork * percentage) / totalPercentage)
            // to improve rounding (divide performed last)
            BigDecimal workAmount = totalWork.getAmount().multiply(percentage).divide(totalPercentage, 3, BigDecimal.ROUND_HALF_EVEN);

            // Convert back to a TimeQuantity based on same units (hours)
            allocatedWork = new TimeQuantity(workAmount, totalWork.getUnits());
        }

        return allocatedWork;
    }
}
