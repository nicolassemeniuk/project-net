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
package net.project.schedule.report.overallocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.project.resource.ResourceAssignment;

/**
 * This class contains and loads summary data regarding overallocated resources.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class OverallocatedResourcesSummaryData {
    /**
     * Number of overallocated days in the reporting period which was specified
     * by the user.
     */
    private int overAllocatedDays = 0;
    /**
     * Number of overallocated resources in the reporting period which was
     * specified by the user.
     */
    private int overAllocatedResources = 0;
    /**
     * Highest Percentage that a resource was allocated in the reporting period
     * which was specified by the user.
     */
    private int highestPercentOfAllocation = 0;
    /**
     * Highest number of allocated hours in the reporting period which was
     * specified by the user.
     */
    private int highestNumberOfAllocatedHours = 0;
    /**
     * Resource that was allocated the most in the reporting period which was
     * specified by the user.
     */
    private String mostOverallocatedResource = "";

    /**
     * Standard constructor to create the summary data from a list of data
     * already queried from the database.
     *
     * @param overallocatedResources a <code>List</code> value containing zero
     * or more {@link net.project.resource.ResourceAssignment} objects.
     */
    public OverallocatedResourcesSummaryData(List overallocatedResources) {
        populate(overallocatedResources);
    }

    /**
     * Populate this object with data from a <code>List</code> of
     * {@link net.project.resource.ResourceAssignment} objects.
     *
     * @param resourceAllocations a <code>List</code> of zero or more
     * {@link net.project.resource.ResourceAssignment} objects.
     */
    private void populate(List resourceAllocations) {
        clear();

        Set overAllocatedResourceList = new HashSet();
        HashMap resourceUseMap = new HashMap();

        for (Iterator it = resourceAllocations.iterator(); it.hasNext();) {
            ResourceAssignment alloc = (ResourceAssignment)it.next();

            //Any new entry should indicate a new day
            overAllocatedDays++;

            //Add the overallocated resource to a set so afterwards we can
            //find the total number of resources
            overAllocatedResourceList.add(new Integer(alloc.getResourceID()));

            //Update the highestPercentOfAllocation field if the value for this
            //allocation is higher.
            highestPercentOfAllocation = Math.max(highestPercentOfAllocation,
                alloc.getPercentAssigned());

            //Update the highestNumberOfAllocatedHours field if the value for
            //this allocation is higher.
            highestNumberOfAllocatedHours = Math.max(highestNumberOfAllocatedHours,
                alloc.getHoursAssigned());

            //Figure out how many times the current resource has been used and
            //store that value.
            Integer resourceIDInteger = new Integer(alloc.getResourceID());
            Integer resourceUsagesCount = (Integer)resourceUseMap.get(resourceIDInteger);
            Integer highUsageCount = new Integer(0);
            if (resourceUsagesCount == null) {
                resourceUsagesCount = new Integer(1);
            } else {
                resourceUsagesCount = new Integer(resourceUsagesCount.intValue() + 1);
            }
            resourceUseMap.put(resourceIDInteger, resourceUsagesCount);

            //If this resource has been used more times than all of the others,
            //then store it.
            if (resourceUsagesCount.intValue() > highUsageCount.intValue()) {
                highUsageCount = resourceUsagesCount;
                mostOverallocatedResource = alloc.getResourceName();
            }
        }
        overAllocatedResources = overAllocatedResourceList.size();
    }

    /**
     * Clear the internal list of values.
     */
    private void clear() {
        overAllocatedDays = 0;
        overAllocatedResources = 0;
        highestPercentOfAllocation = 0;
        highestNumberOfAllocatedHours = 0;
        mostOverallocatedResource = "";
    }

    /**
     * Get the number of days that were overallocated.  This is bounded by
     * parameters in the report that the user has applied.  (Filters, for
     * example.)
     *
     * @return an <code>int</code> value indicating the number of overallocated
     * days.
     * @see #setOverallocatedDays
     */
    public int getOverallocatedDays() {
        return overAllocatedDays;
    }

    /**
     * Set the total number of days in which a resource was overallocated.
     * (That is, the total number of days when one or more resources were
     * required to work more than 100% of a work day.)
     *
     * @param overAllocatedDays a <code>int</code> value indicating the total
     * number of days that resources were allocated to work more than 100% of
     * their days on tasks.
     * @see #getOverallocatedDays
     */
    public void setOverallocatedDays(int overAllocatedDays) {
        this.overAllocatedDays = overAllocatedDays;
    }

    /**
     * Get the total number of resources that were overallocated.
     *
     * @return a <code>int</code> value indicating the total number of resources
     * which were overallocated.
     * @see #setOverallocatedResources
     */
    public int getOverallocatedResources() {
        return overAllocatedResources;
    }

    /**
     * Set the total number of resources that were overallocated for this
     * report.
     *
     * @param overAllocatedResources an <code>int</code> value indicating the
     * total number of resources that were overallocated.
     * @see #getOverallocatedResources
     */
    public void setOverallocatedResources(int overAllocatedResources) {
        this.overAllocatedResources = overAllocatedResources;
    }

    /**
     * Get the percentage of allocation that most overallocated resource was
     * overallocated.  For example if resources were allocated as:
     *
     * <pre>
     * Tom      10hr        125%
     * Dick     12hr        150%
     * Harry    16hr        200%
     * </pre>
     *
     * Then this method would return 200.
     *
     * @return a <code>int</code> value which displays the percentage of
     * allocation for the highest allocated resource.
     * @see #setHighestPercentOfAllocation
     */
    public int getHighestPercentOfAllocation() {
        return highestPercentOfAllocation;
    }

    /**
     * Set the percentage of allocation of the resource that was the most
     * highly allocated resource on a single day.
     *
     * @param highestPercentOfAllocation a <code>int</code> value which
     * indicates the percentage of allocation for the highest allocated
     * resource.
     * @see #getHighestPercentOfAllocation
     */
    public void setHighestPercentOfAllocation(int highestPercentOfAllocation) {
        this.highestPercentOfAllocation = highestPercentOfAllocation;
    }

    /**
     * Get the highest number of allocated hours that were allocated to any
     * resource identified by this report.
     *
     * @return a <code>int</code> value indicating the highest number of
     * allocated hours allocated to a resource in this report.
     */
    public int getHighestNumberOfAllocatedHours() {
        return highestNumberOfAllocatedHours;
    }

    /**
     * Set the highest number of hours that any resource in this report was
     * allocated on a single day.
     *
     * @param highestNumberOfAllocatedHours a <code>int</code> value identifying
     * the highest number of hours allocated to any resource on any single day
     * in the reporting period.
     * @see #getHighestNumberOfAllocatedHours
     */
    public void setHighestNumberOfAllocatedHours(int highestNumberOfAllocatedHours) {
        this.highestNumberOfAllocatedHours = highestNumberOfAllocatedHours;
    }

    /**
     * Get the name of the resource which was overallocated the highest number
     * of times in this report.
     *
     * @return a <code>String</code> value containing the name of the resource
     * which was overallocated the highest number of times.
     * @see #setMostOverallocatedResource
     */
    public String getMostOverallocatedResource() {
        return mostOverallocatedResource;
    }

    /**
     * Set the name of the resource which was overallocated the highest number
     * of times in this report.
     *
     * @param mostOverallocatedResource a <code>String</code> value containing
     * the name of the resource which was overallocated the highest number of
     * times.
     * @see #getMostOverallocatedResource
     */
    public void setMostOverallocatedResource(String mostOverallocatedResource) {
        this.mostOverallocatedResource = mostOverallocatedResource;
    }
}