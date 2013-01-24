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

package net.project.schedule.report.resourceallocation;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.project.util.TimeQuantityUnit;

/**
 * This class is designed to hold the data that is shown in the summary section
 * of a Resource Allocation Report.  It contains rollup numbers of task
 * assignments.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ResourceAllocationSummaryData {
    /** Total number of resources assigned to tasks. */
    private int resourceCount;
    /** Total number of tasks in the schedule. */
    private int taskCount;
    /** Total number of working hours in the schedule. */
    private int assignedHourCount;
    /** Total number of unassigned hours in the schedule. */
    private int unassignedHourCount;

    /**
     * This method populates the resource allocation summary data object with
     * values by examining the data already fetched for the detailed section.
     *
     * @param listOfTaskResourceAllocations a {@link java.util.List} object
     * containing zero or more
     * {@link net.project.schedule.report.resourceallocation.TaskResourceAllocation}
     * objects.
     */
    public void populate(List listOfTaskResourceAllocations) {
        HashSet allocatedResources = new HashSet();
        HashSet tasks = new HashSet();
        BigDecimal assignedHourCount = new BigDecimal(0);
        BigDecimal totalHours = new BigDecimal(0);

        for (Iterator it = listOfTaskResourceAllocations.iterator(); it.hasNext();) {
            TaskResourceAllocation current = (TaskResourceAllocation)it.next();

            allocatedResources.add(current.getResourceID());
            tasks.add(current.getTaskID());
            assignedHourCount = assignedHourCount.add(current.getAllocatedHours());
            totalHours = totalHours.add(current.getTotalTaskWork().convertTo(TimeQuantityUnit.HOUR, 2).getAmount());
        }

        this.resourceCount = allocatedResources.size();
        this.taskCount = tasks.size();
        this.assignedHourCount = assignedHourCount.intValue();
        //this.unassignedHourCount = totalHours.subtract(assignedHourCount).intValue();
    }

    /**
     * Get the total number of resources assigned to tasks in this schedule.
     *
     * @return a <code>int</code> value which indicates how many resources are
     * are assigned to tasks.
     */
    public int getResourceCount() {
        return resourceCount;
    }

    /**
     * Get the number of tasks currently being worked on.
     *
     * @return a <code>int</code> value indicating the number of tasks being
     * worked on.
     */
    public int getTaskCount() {
        return taskCount;
    }

    /**
     * Get the total number of assigned hours.
     *
     * @return an <code>int</code> value indicating the number of working hours
     * in this project.
     */
    public int getAssignedHourCount() {
        return assignedHourCount;
    }

    /**
     * For the tasks currently in progress this method returns the total number
     * of working hours which are still unassigned in the current schedule.
     *
     * @return a <code>int</code> value indicating the number of working hours
     * which are currently unassigned.
     */
    public int getUnassignedHourCount() {
        return unassignedHourCount;
    }

}
