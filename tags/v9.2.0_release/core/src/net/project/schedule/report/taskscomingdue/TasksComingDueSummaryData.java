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
package net.project.schedule.report.taskscomingdue;

/**
 * Class to contain the data that pertains to the summary section of the Tasks
 * Coming Due Report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class TasksComingDueSummaryData {
    /** Count of all tasks that match the report criteria. */
    private int taskCount;
    /** Count of all completed tasks that match the report criteria. */
    private int completedTaskCount;
    /** Count of all tasks due today. */
    private int dueTodayCount;
    /** Count of all tasks due tomorrow. */
    private int dueTomorrowCount;
    /** Count of all tasks due within the next 7 days. */
    private int dueNext7Count;
    /** Count of all tasks due within the next 28 days. */
    //private int dueNext28Count;
    /** Count of all tasks due within the next month. */
    private int dueNextMonth;
    /** Count of all tasks that are eventually due. */
    private int uncompletedTasks;

    /**
     * Get the count of all tasks that match the report criteria.
     *
     * @return a <code>int</code> object that contain the count of all tasks that
     * match the report criteria.
     */
    public int getTaskCount() {
        return taskCount;
    }

    /**
     * Set the count of all tasks that match the report criteria.
     *
     * @param taskCount an <code>int</code> object that contains the count of
     * all tasks that match the report criteria.
     */
    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    /**
     * Get the count of all tasks that match the report criteria and which have
     * already been completed.
     *
     * @return an <code>int</code> value which contains the count of all tasks
     * that match the report criteria and which are already completed.
     */
    public int getCompletedTaskCount() {
        return completedTaskCount;
    }

    /**
     * Set the count all of tasks which match the report criteria and which have
     * already been completed.
     *
     * @param completedTaskCount an <code>int</code> value which contains the
     * count of all tasks that match the report criteria and which have already
     * been completed.
     */
    public void setCompletedTaskCount(int completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }

    /**
     * Get the count of all tasks that are due today.
     *
     * @return an <code>int</code> value which represents the tasks which match
     * the report criteria and which are due today.
     */
    public int getDueTodayCount() {
        return dueTodayCount;
    }

    /**
     * Set a count of tasks that are due today and which match the report
     * criteria.
     *
     * @param dueTodayCount an <code>int</code> value which represents the
     * tasks that are due today and which match the report criteria.
     */
    public void setDueTodayCount(int dueTodayCount) {
        this.dueTodayCount = dueTodayCount;
    }

    /**
     * Get the count of all tasks that are due tomorrow.
     *
     * @return an <code>int</code> value which represents the tasks which match
     * the report criteria and which are due tomorrow.
     */
    public int getDueTomorrowCount() {
        return dueTomorrowCount;
    }

    /**
     * Set a count of tasks that are due tomorrow and which match the report
     * criteria.
     *
     * @param dueTomorrowCount an <code>int</code> value which represents the
     * tasks that are due tomorrow and which match the report criteria.
     */
    public void setDueTomorrowCount(int dueTomorrowCount) {
        this.dueTomorrowCount = dueTomorrowCount;
    }

    /**
     * Get the count of tasks which are due in the next 7 days.
     *
     * @return an <code>int</code> value containing the number of tasks due in
     * the next 7 days.
     */
    public int getDueNext7Count() {
        return dueNext7Count;
    }

    /**
     * Set the number of tasks due in the next 7 days.
     *
     * @param dueNext7Count an <code>int</code> value containing the number of
     * tasks that are due in the next 7 days.
     */
    public void setDueNext7Count(int dueNext7Count) {
        this.dueNext7Count = dueNext7Count;
    }

    /**
     * Gets the number of tasks or milestones that are due within the next
     * calendar month.  This time period is defined as the period of time from
     * the current day of the current month until the same day in the following
     * month.  For example, if today is January 14, this method would return the
     * number of tasks occurring during or after January 14 but before February
     * 14th.
     *
     * @return an <code>int</code> value indicating the number of tasks which are
     * due in the next month.
     */
    public int getDueNextMonthCount() {
        return this.dueNextMonth;
    }

    /**
     * Set the number of tasks due in the next month.  Strictly, indicate the
     * number of tasks that occur between midnight of the current day and before
     * midnight of this same day of the month one month from now.
     *
     * @param dueNextMonth an <code>int</code> value indicating the number of
     * tasks which are due within the next month.
     */
    public void setDueNextMonthCount(int dueNextMonth) {
        this.dueNextMonth = dueNextMonth;
    }

    /**
     * Sets the total number of tasks that are not yet completed.
     *
     * @param uncompletedTasks a <code>int</code> value containing the total
     * number of unfinished tasks.
     */
    public void setUncompletedTasks(int uncompletedTasks) {
        this.uncompletedTasks = uncompletedTasks;
    }

    /**
     * Get the total number of unfinished tasks.
     *
     * @return an <code>int</code> value containing the total number of
     * unfinished tasks.
     */
    public int getUncompletedTaskCount() {
        return this.uncompletedTasks;
    }

}
