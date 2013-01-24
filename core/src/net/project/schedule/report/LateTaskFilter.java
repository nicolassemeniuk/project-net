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

import java.util.Date;

import net.project.base.finder.EmptyFinderFilter;
import net.project.schedule.TaskFinder;
import net.project.util.DateUtils;

/**
 * This filter will only allow tasks to load if their end date is later than
 * today but they are not yet 100% complete.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class LateTaskFilter extends EmptyFinderFilter {
    public LateTaskFilter(String id) {
        super(id);
    }

    /**
     * Get the where clause for an empty finder filter, which should always be
     * empty.
     *
     * @return a <code>String</code> value which should always be empty.
     */
    public String getWhereClause() {
        return "("+TaskFinder.DATE_FINISH_COLUMN.getColumnName()+" < "+
            DateUtils.getDatabaseDateString(new Date())+" and "+
            TaskFinder.WORK_PERCENT_COMPLETE_COLUMN.getColumnName()+" < 100)";
    }
}
