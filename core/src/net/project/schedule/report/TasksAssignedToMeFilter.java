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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.schedule.report;

import net.project.base.finder.EmptyFinderFilter;
import net.project.security.SessionManager;

/**
 * This filter limits the TaskFinder and LateTaskReportSummaryFinder objects to only
 * return data that pertains to a certain user.
 *
 * @since 7.4
 * @author Matthew Flower
 */
public class TasksAssignedToMeFilter extends EmptyFinderFilter {
    private static String TASKS_ASSIGNED_TO_ME_FILTER_NAME = "prm.schedule.report.common.filter.showtasksassignedtome.name";

    /**
     * Standard constructor.
     *
     * @param id a <code>String</code> that uniquely identifies this filter.
     * Generally, these are just an integer value specific to the current query
     * you are constructing, though it can be any unique value.  You can use
     * whatever value you like, as long as it is unique.
     */
    public TasksAssignedToMeFilter(String id) {
        super(id, TASKS_ASSIGNED_TO_ME_FILTER_NAME);
    }

    /**
     * Get the where clause that will help construct a query to limit the results
     * of a task finder to only a certain user.
     *
     * @return a <code>String</code> value which contains a where clause appropriate
     * for the Late Task and Tasks Coming Due reporting objects and the
     * {@link net.project.schedule.TaskFinder} object.
     */
    public String getWhereClause() {
        StringBuffer whereClause = new StringBuffer();

        if (isSelected()) {
            whereClause.append(" t.task_id in (select a.object_id from pn_assignment a ")
                .append("where a.person_id = ").append(SessionManager.getUser().getID())
                .append(" and a.record_status = 'A'").append(")");
        }

        return whereClause.toString();
    }

    public String getFilterDescription() {
        return getName();
    }
}
