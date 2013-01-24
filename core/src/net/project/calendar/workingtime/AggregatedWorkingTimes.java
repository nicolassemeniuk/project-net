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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Provides an aggregate of times worked.
 * <p>
 * Maintains unique minutes worked.  If overlapping times are added,
 * each time is counted only once.  For example, (8:30 - 8:45) is added
 * and (8:30 - 8:35) is added then only 15 minutes is worked.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class AggregatedWorkingTimes {

    /**
     * The aggregated working times; none of the entries overlap.
     * Each element is a <code>WorkingTime</code>.
     */
    private final Collection workingTimes = new ArrayList();

    /**
     * Returns the duration of the working times.
     * This is the sum of the unique time worked by all the working times
     * added to this aggregate.
     * @return the duration
     */
    SimpleTimeQuantity getDuration() {

        SimpleTimeQuantity total = new SimpleTimeQuantity(0, 0);

        for (Iterator it = workingTimes.iterator(); it.hasNext();) {
            WorkingTime nextworkingTime = (WorkingTime) it.next();
            total = total.add(nextworkingTime.getDuration());
        }

        return total;
    }

    /**
     * Adds the specified working time to be considered when totalling the
     * number of hours worked.
     * <p>
     * The whole working time is considered to be worked.
     * </p>
     * @param workingTime the working time to add
     */
    void add(WorkingTime workingTime) {

        // This is the working time we are going to add; it might change
        WorkingTime newWorkingTime = workingTime;

        // We check each current working time to see if it overlaps with the
        // new one; if so, we update the new one to be the combination of
        // both and remove the overlapping one
        // We proceed to loop with the new expanded working time
        for (Iterator it = workingTimes.iterator(); it.hasNext();) {
            WorkingTime nextWorkingTime = (WorkingTime) it.next();

            if (workingTime.isStartBetween(nextWorkingTime) ||
                    workingTime.isEndBetween(nextWorkingTime) ||
                    (workingTime.isStartBefore(nextWorkingTime) && workingTime.isEndAfter(nextWorkingTime))) {

                // They overlap
                // We create a new working time as the combination of the two
                Time earliestStartTime = (workingTime.isStartBefore(nextWorkingTime) ? workingTime.getStartTime() : nextWorkingTime.getStartTime());
                Time latestEndTime = (workingTime.isEndAfter(nextWorkingTime) ? workingTime.getEndTime() : nextWorkingTime.getEndTime());
                newWorkingTime = new WorkingTime(earliestStartTime, latestEndTime);

                // Remove the overlapping one since it is superceded by the
                // new one
                it.remove();
            }

        }

        workingTimes.add(newWorkingTime);
    }

    /**
     * Adds the specified times worked to this one, aggregating both.
     * <p>
     * Works be iteratively calling {@link #add} with each working time
     * added to the specified times worked.
     * </p>
     * @param timesWorked the times worked to add to this one
     * @throws NullPointerException if timesWorked is null
     */
    void add(AggregatedWorkingTimes timesWorked) {

        if (timesWorked == null) {
            throw new NullPointerException("timesWorked is required");
        }

        for (Iterator it = timesWorked.workingTimes.iterator(); it.hasNext();) {
            WorkingTime nextWorkingTime = (WorkingTime) it.next();
            add(nextWorkingTime);
        }

    }

    /**
     * Returns an unmodifiable collection of the working times.
     * @return a collection where each element is a <code>WorkingTime</code>
     */
    public Collection getTimes() {
        return Collections.unmodifiableCollection(this.workingTimes);
    }

    public String toString() {
        return getDuration() + " " + this.workingTimes.toString();
    }
}
