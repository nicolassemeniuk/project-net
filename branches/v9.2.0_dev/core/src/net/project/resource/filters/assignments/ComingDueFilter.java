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
 * This filter shows assignments that are coming due in the next 7 days.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class ComingDueFilter extends EmptyFinderFilter {
    public ComingDueFilter(String id) {
        super(id);
    }

    public String getWhereClause() {
        String sql = "";

        if (isSelected()) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            cal.setTime(new Date());
            String todayString = DateUtils.getDatabaseDateString(cal.startOfDay());

            sql = "((a.end_date <= "+todayString+"+7) and (a.end_date >= "+todayString+"))";
        }

        return sql;
    }
}
