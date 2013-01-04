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
package net.project.schedule.report;

import net.project.base.finder.EmptyFinderFilter;
import net.project.security.SessionManager;

/**
 * This filter will only return tasks which don't have any resources assigned to
 * them.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class UnassignedTasksFilter extends EmptyFinderFilter {
    public UnassignedTasksFilter(String id) {
        super(id);
    }

    /**
     * Get the where clause for an empty finder filter, which should always be
     * empty.
     *
     * @return a <code>String</code> value which should always be empty.
     */
    public String getWhereClause() {
        String sql =
            "(not t.task_id in" +
            "  (" +
            "  select " +
            "    object_id " +
            "  from " +
            "    pn_assignment unassigned_assignment " +
            "  where " +
            "    object_id = t.task_id " +
            "    and unassigned_assignment.record_status = 'A' " +
            "    and space_id = "+SessionManager.getUser().getCurrentSpace().getID()+"))";
        return sql;
    }
}
