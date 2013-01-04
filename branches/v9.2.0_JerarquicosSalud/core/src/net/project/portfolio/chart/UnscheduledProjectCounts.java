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
package net.project.portfolio.chart;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.project.code.ColorCode;

/**
 * Data storage class which holds and does operations on the number of projects
 * which exist with a certain color code.
 *
 * This class does not exist in isolation.  It does no querying by itself, it is
 * populated through external means.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class UnscheduledProjectCounts {
    /** Count of the number of project correspond to a color code. */
    private Map countMap = new HashMap();

    /**
     * Increment the amount of projects with a certain color code status.
     *
     * @param color a <code>ColorCode</code> which is the status type of the
     * project count we are going to increment.
     */
    void incrementProjectCount(ColorCode color) {
        BigDecimal count = (BigDecimal)countMap.get(color);
        if (count == null) {
            countMap.put(color, new BigDecimal(1));
        } else {
            countMap.put(color, count.add(new BigDecimal(1)));
        }
    }

    /**
     * Indicates whether or not there are any unscheduled projects.
     *
     * @return a <code>boolean</code> value indicating whether there are any
     * unscheduled projects.
     */
    boolean hasOccurrences() {
        return (countMap.size() > 0);
    }

    /**
     * Get the count of projects that have a given color code.  This method
     * relies on this object having already been occupied by the
     * {@link #incrementProjectCount} method externally.
     *
     * @param color
     * @return a <code>double</code> value indicating the number of unscheduled
     * projects which have this color code.
     */
    double getCount(ColorCode color) {
        BigDecimal count = (BigDecimal)countMap.get(color);
        double toReturn = 0;

        if (count != null) {
            toReturn = count.doubleValue();
        }

        return toReturn;
    }
}
