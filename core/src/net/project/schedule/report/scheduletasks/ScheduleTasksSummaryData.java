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
package net.project.schedule.report.scheduletasks;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.calendar.PnCalendar;
import net.project.schedule.ScheduleEntry;
import net.project.security.SessionManager;

/**
 * This class constructs and serves as a container for aggregate counts of data
 * contained in a list of tasks.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ScheduleTasksSummaryData {
    /** Total number of tasks on the schedule. */
    private int totalNumberOfTasks = 0;
    /** Total number of tasks already completed. */
    private int totalNumberOfCompletedTasks = 0;
    /** Total number of currently overdue tasks. */
    private int totalNumberOfOverdueTasks = 0;
    /** Total number of tasks completed in the last 7 days. */
    private int totalCompletedInLast7Days = 0;
    /** Total number of tasks due in the next 7 days. */
    private int totalDueInNext7Days = 0;

    /**
     * Public constructor that initializes the data.
     *
     * @param tasksToIterate a <code>java.util.List</code> variable which
     * contains zero or more {@link net.project.schedule.Task} objects.
     * @see #populate
     */
    public ScheduleTasksSummaryData(List tasksToIterate) {
        populate(tasksToIterate);
    }

    /**
     * Fill all of the values in this object with data by analyzing a list of
     * tasks.
     *
     * @param tasksToIterate a <code>java.util.List</code> variable which
     * contains zero or more {@link net.project.schedule.Task} objects.
     */
    public void populate(List tasksToIterate) {
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        Date now = new Date();
        cal.setTime(now);
        cal.add(PnCalendar.DATE, -7);
        Date sevenDaysAgo = cal.getTime();
        cal.setTime(now);
        cal.add(PnCalendar.DATE, 7);
        Date sevenDaysAhead = cal.getTime();

        for (Iterator it = tasksToIterate.iterator(); it.hasNext();) {
            ScheduleEntry currentTask = (ScheduleEntry)it.next();

            totalNumberOfTasks++;
            if ((!currentTask.isMilestone()) && (currentTask.isComplete())) {
                totalNumberOfCompletedTasks++;
            }

            if ((currentTask.getEndTime().getTime() < now.getTime()) &&
                (!currentTask.isComplete())) {
                totalNumberOfOverdueTasks++;
            }

            if ((currentTask.getEndTime().getTime() >= sevenDaysAgo.getTime()) &&
                (currentTask.isComplete())) {
                totalCompletedInLast7Days++;
            }

            if ((currentTask.getEndTime().getTime() <= sevenDaysAhead.getTime()) &&
                (!currentTask.isComplete())) {
                totalDueInNext7Days++;
            }
        }
    }

    /**
     * Returns the total number of tasks in the list of tasks.
     *
     * @return a <code>int</code> value containing the total number of tasks.
     */
    public int getTotalNumberOfTasks() {
        return totalNumberOfTasks;
    }

    /**
     * Return the total number of completed tasks in the list of tasks.
     *
     * @return a <code>int</code> value containing the total number of completed
     * tasks.
     */
    public int getTotalNumberOfCompletedTasks() {
        return totalNumberOfCompletedTasks;
    }

    /**
     * Return the total number of overdue tasks in the list of tasks.
     *
     * @return a <code>int</code> value containing the total number of overdue
     * tasks.
     */
    public int getTotalNumberOfOverdueTasks() {
        return totalNumberOfOverdueTasks;
    }

    /**
     * Return the total number of tasks completed in the last 7 days that appears
     * in the list of tasks fed to this object throught the {@link #populate}
     * method.
     *
     * @return a <code>int</code> value containing the number of tasks completed
     * in the last 7 days.
     */
    public int getTotalCompletedInLast7Days() {
        return totalCompletedInLast7Days;
    }

    /**
     * Get the total number of tasks due in the next 7 days that appear in the
     * task list.
     *
     * @return a <code>int</code> value which indicates the number of tasks due
     * in the next 7 days.
     */
    public int getTotalDueInNext7Days() {
        return totalDueInNext7Days;
    }
}
