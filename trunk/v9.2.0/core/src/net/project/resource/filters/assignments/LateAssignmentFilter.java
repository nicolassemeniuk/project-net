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
package net.project.resource.filters.assignments;

import java.util.Date;

import net.project.base.finder.EmptyFinderFilter;
import net.project.calendar.PnCalendar;
import net.project.security.SessionManager;
import net.project.util.DateUtils;

/**
 * This filter will cause the AssignmentFinder to only finder assignments who
 * have a percent complete field, whose percentage complete field is less than
 * 100%, and whose end date is before now.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class LateAssignmentFilter extends EmptyFinderFilter {
    public LateAssignmentFilter(String id) {
        super(id);
    }

    public String getWhereClause() {
        String sql = "";

        if (isSelected()) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            cal.setTime(new Date());
            String todayString = DateUtils.getDatabaseDateString(cal.endOfDay());
            
            sql = "((a.percent_complete is null) or " +
                "(a.end_date <  "+todayString+" and " +
                "(a.percent_complete < 1))) ";
        }

        return sql;
    }
}
