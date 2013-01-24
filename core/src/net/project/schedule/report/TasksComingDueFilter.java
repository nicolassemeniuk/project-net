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
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.security.SessionManager;
import net.project.util.DateUtils;

/**
 * This filter shows tasks that are starting in the next n days.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class TasksComingDueFilter extends EmptyFinderFilter {
    private int daysInAdvance = PropertyProvider.getInt("prm.schedule.filters.taskscomingdue.numberofdays.value");

    public TasksComingDueFilter(String id) {
        super(id);
    }

    /**
     * Get the where clause for an empty finder filter, which should always be
     * empty.
     *
     * @return a <code>String</code> value which should always be empty.
     */
    public String getWhereClause() {
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        cal.setTime(new Date());
        String todayString = DateUtils.getDatabaseDateString(cal.startOfDay());

        String sql = "((" +
            "(t.date_finish >= " + todayString + ") and " +
            "(t.date_finish-"+daysInAdvance+" < "+ todayString + ")" +
            ") and (t.work_percent_complete != 100))";

        return sql;
    }

    /**
     * Get the number of days in advance that this filter considers "coming".
     *
     * @return a <code>int</code> which indicates who fews days away a task
     * has to be away to be considered "coming due".
     */
    public int getDaysInAdvance() {
        return daysInAdvance;
    }

    /**
     * Set the number of days in advance that a task needs to be to be
     * considered "coming due".
     *
     * @param daysInAdvance an <code>int</code> indicating the number of days in
     * advance that a task needs to be to be considered "coming due".
     */
    public void setDaysInAdvance(int daysInAdvance) {
        this.daysInAdvance = daysInAdvance;
    }
}
