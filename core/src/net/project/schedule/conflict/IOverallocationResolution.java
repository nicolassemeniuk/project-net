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
package net.project.schedule.conflict;

import java.util.Date;

import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;

public interface IOverallocationResolution {
    /**
     * This method indicates if applying this overallocation fix will cause the
     * end date of the schedule to change.
     *
     * @return a <code>boolean</code> indicating if applying this overallocation
     * fix will cause the end date of the schedule to change.
     */
    public boolean doesScheduleEndDateChange();
    /**
     * This method will return the new end date of the schedule if this fix is
     * applied.
     *
     * @return a <code>Date</code> object which will be the new completion date
     * of this schedule if this overallocation fix is applied.
     */
    public Date newScheduleEndDate();

    /**
     * Do whatever is necessary to resolve the conflict using the current
     * conflict resolution method.
     *
     * @param schedule
     * @param entry
     */
    void resolveConflict(Schedule schedule, ScheduleEntry entry) throws PersistenceException;

    /**
     * Get a description of what this conflict resolution method would do to
     * solve the conflict.
     *
     * @return a <code>String</code> containing an human-readable description
     * of what this IConflictResolution object will do to resolve the
     * overallocation.
     */
    String getDescription();

    /**
     * Indicates if this conflict resolution method can be applied in order to
     * solve a problem.
     *
     * @return a <code>boolean</code> indicating if this resolution method would
     * be at all helpful.
     */
    boolean isApplicable() throws PersistenceException;
}
