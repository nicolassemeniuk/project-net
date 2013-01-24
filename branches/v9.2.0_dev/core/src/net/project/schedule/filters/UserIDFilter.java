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
package net.project.schedule.filters;

import net.project.base.finder.EmptyFinderFilter;

/**
 * Filter which matches task ids that have an assignment to a particular user.
 *
 * @author Matthew Flower
 * @since Version 7.6.4
 */
public class UserIDFilter extends EmptyFinderFilter {
    /** User ID assigned to some tasks. */
    private String userID;

    public UserIDFilter(String id, boolean selected, String userID) {
        super(id, selected);
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Get the where clause for an empty finder filter, which should always be
     * empty.
     *
     * @return a <code>String</code> value which should always be empty.
     */
    public String getWhereClause() {
        String sql;

        sql =
            "(t.task_id in " +
            "(select a.object_id from pn_assignment a where person_id = "+userID+" and record_status = 'A'))";

        return sql;
    }
}
