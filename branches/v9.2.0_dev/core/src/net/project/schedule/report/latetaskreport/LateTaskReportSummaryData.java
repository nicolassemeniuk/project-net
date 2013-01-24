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
|    $RCSfile$NAME.java,v $
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.schedule.report.latetaskreport;

/**
 * Class to store all of the data that will appear in the summary section of
 * various task reports.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class LateTaskReportSummaryData {
    /** The plan id of the tasks we are reporting on. */
    private int planID = 0;
    /** Total number of tasks in the schedule. */
    private int taskCount = 0;
    /**
     * Total number of completed tasks in the schedule in our reporting period
     * as defined by the parameters that the user supplied.
     */
    private int completedTaskCount = 0;
    /**
     * Total number of overdue tasks in the schedule in our reporting period as
     * defined by the parameters the the user supplied.
     */
    private int overdueTaskCount = 0;
    /**
     * Total number of overdue milestones in the schedule in our reporting
     * period as defined by the parameters the the user supplied.
     */
    private int overdueMilestoneCount = 0;
    /**
     * Total number of tasks completed in the last 7 days.
     */
    private int taskCompletedInLast7DaysCount = 0;
    /**
     * Total number of tasks that are scheduled to be completed in the next 7
     * days.
     */
    private int taskDueInNext7DaysCount = 0;

    /**
     * Get the plan id that this task report data pertains to.
     *
     * @return an <code>int</code> value that indicates which plan this task
     * report information belongs to.  (As a bit of background, every project
     * currently contains a single plan (aka schedule) but the data structures
     * support more than one.)
     * @see #setPlanID
     */
    public int getPlanID() {
        return planID;
    }

    /**
     * Set the plan id that this task report data pertains to.
     *
     * @param planID a <code>int</code> value that indicates which plan this
     * task report information pertains to.
     * @see #getPlanID
     */
    public void setPlanID(int planID) {
        this.planID = planID;
    }

    /**
     * Get the total number of tasks.
     *
     * @return an <code>int</code> value that indicates the total number of tasks.
     * @see #setTaskCount
     */
    public int getTaskCount() {
        return taskCount;
    }

    /**
     * Set the total number of tasks.
     *
     * @param taskCount the new <code>int</code> value that will represent the
     * total number of tasks.
     * @see #getTaskCount
     */
    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    /**
     * Get the total number of completed tasks.
     *
     * @return a <code>int</code> value which indicates the total number of
     * completed tasks.
     * @see #setCompletedTaskCount
     */
    public int getCompletedTaskCount() {
        return completedTaskCount;
    }

    /**
     * Set the total number of completed tasks.
     *
     * @param completedTaskCount a <code>int</code> value which indicates the
     * total number of completed tasks.
     * @see #getCompletedTaskCount
     */
    public void setCompletedTaskCount(int completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }

    /**
     * Get the total number of overdue tasks.
     *
     * @return an <code>int</code> value that indicates the total number of
     * overdue tasks.
     * @see #setOverdueTaskCount
     */
    public int getOverdueTaskCount() {
        return overdueTaskCount;
    }

    /**
     * Set the total number of overdue tasks.
     *
     * @param overdueTaskCount an <code>int</code> value indicating the total
     * number of overdue tasks.
     * @see #getOverdueTaskCount
     */
    public void setOverdueTaskCount(int overdueTaskCount) {
        this.overdueTaskCount = overdueTaskCount;
    }

    /**
     * Get the count of overdue milestones.
     *
     * @return a <code>int</code> value indicating the total number of overdue
     * milestones.
     * @see #setOverdueMilestoneCount
     */
    public int getOverdueMilestoneCount() {
        return overdueMilestoneCount;
    }

    /**
     * Set the total number of overdue milestones.
     *
     * @param overdueMilestoneCount a <code>int</code> value indicating the total
     * number of overdue milestones.
     * @see #getOverdueMilestoneCount
     */
    public void setOverdueMilestoneCount(int overdueMilestoneCount) {
        this.overdueMilestoneCount = overdueMilestoneCount;
    }

    /**
     * Get the number of tasks completed in the last 7 days.
     *
     * @return a <code>int</code> value indicating the number of tasks completed
     * in the last 7 days.
     * @see #setTaskCompletedInLast7DaysCount
     */
    public int getTaskCompletedInLast7DaysCount() {
        return taskCompletedInLast7DaysCount;
    }

    /**
     * Set the number of tasks completed in the last 7 days.
     *
     * @param taskCompletedInLast7DaysCount a <code>int</code> value indicating
     * the number of tasks completed in the last 7 days.
     * @see #getTaskCompletedInLast7DaysCount
     */
    public void setTaskCompletedInLast7DaysCount(int taskCompletedInLast7DaysCount) {
        this.taskCompletedInLast7DaysCount = taskCompletedInLast7DaysCount;
    }

    /**
     * Get the number of tasks due in the next 7 days.
     *
     * @return a <code>int</code> value indicating the number of tasks due in
     * the next 7 days.
     * @see #setTaskDueInNext7DaysCount
     */
    public int getTaskDueInNext7DaysCount() {
        return taskDueInNext7DaysCount;
    }

    /**
     * Set the number of tasks scheduled to be completed in the next 7 days.
     *
     * @param taskDueInNext7DaysCount a <code>int</code> value indicating the
     * number of tasks scheduled to be completed in the next 7 days.
     */
    public void setTaskDueInNext7DaysCount(int taskDueInNext7DaysCount) {
        this.taskDueInNext7DaysCount = taskDueInNext7DaysCount;
    }
}
