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

 package net.project.schedule.conflict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentList;
import net.project.resource.ResourceAllocationList;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;

/**
 * This class is designed to look for resolutions to problems that a task might
 * have, for example that it has an overallocated resource.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class Rescheduler {
    /**
     * Find ways to resolve a schedule entry that has one or more overallocated
     * resources.
     *
     * @param schedule a <code>Schedule</code> object which contains the
     * schedule entry that we are going to try to fix.
     * @param scheduleEntry a <code>ScheduleEntry</code> object that we are
     * going to try to resolve the overallocations in.
     * @param resourceAllocationLists a <code>Map</code> of person ID to
     * <code>ResourceAllocationList</code> objects.
     * @param overallocatedAssignments a <code>List</code> of assignments that
     * have overallocations.  (We are only going
     * to bother with overallocated ones.)
     * @return a <code>Collection</code> of IOverallocationResolution objects
     * that know how to fix overallocation problems in a ScheduleEntry.
     */
    static List findOverallocationResolutions(Schedule schedule, ScheduleEntry scheduleEntry, Map resourceAllocationLists, List overallocatedAssignments) throws PersistenceException {
        List resolutions = new ArrayList();

        for (Iterator it = overallocatedAssignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment)it.next();

            //Try to resolve the overallocation by changing the allocation percentage
            IOverallocationResolution resolution = new ChangeAllocationPercentage(
                (Schedule)schedule.clone(),
                (ScheduleEntry)scheduleEntry.clone(),
                (ScheduleEntryAssignment)assignment.clone(),
                (ResourceAllocationList)resourceAllocationLists.get(assignment.getPersonID()));
            if (resolution.isApplicable()) {
                resolutions.add(resolution);
            }

            IOverallocationResolution resolution2 = new MoveStartDateBackward(
                (Schedule)schedule.clone(),
                (ScheduleEntry)scheduleEntry.clone(),
                (ScheduleEntryAssignment)assignment.clone(),
                resourceAllocationLists);
            if (resolution2.isApplicable()) {
                resolutions.add(resolution2);
            }

            IOverallocationResolution resolution3 = new MoveStartDateForward(
                (Schedule)schedule.clone(),
                (ScheduleEntry)scheduleEntry.clone(),
                (ScheduleEntryAssignment)assignment.clone(),
                resourceAllocationLists);
            if (resolution3.isApplicable()) {
                resolutions.add(resolution3);
            }

        }

        return resolutions;
    }

    /**
     * Find ways to resolve a schedule entry that has one or more overallocated
     * resources.
     *
     * @param schedule a <code>Schedule</code> object which contains the
     * schedule entry that we are going to try to fix.
     * @param scheduleEntry a <code>ScheduleEntry</code> object that we are
     * going to try to resolve the overallocations in.
     * @return a <code>Collection</code> of IOverallocationResolution objects
     * that know how to fix overallocation problems in a ScheduleEntry.
     */
    public static List findOverallocationResolutions(Schedule schedule, ScheduleEntry scheduleEntry) throws PersistenceException {

        //Find the ScheduleEntryAssignments that are a problem
        Map resourceAllocationLists = findResourceAllocationLists(scheduleEntry);
        List overallocatedAssignments = findOverallocatedAssignments(scheduleEntry, resourceAllocationLists);

        return findOverallocationResolutions(schedule, scheduleEntry, resourceAllocationLists, overallocatedAssignments);
    }

    private static Map findResourceAllocationLists(ScheduleEntry scheduleEntry) throws PersistenceException {
        Map rals = new HashMap();

        for (Iterator it = scheduleEntry.getAssignmentList().iterator(); it.hasNext();) {
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment)it.next();

            ResourceAllocationList ral = new ResourceAllocationList();
            ral.loadResourceAllocationsForPerson(assignment.getPersonID());

            rals.put(assignment.getPersonID(), ral);
        }

        return rals;
    }

    private static List findOverallocatedAssignments(ScheduleEntry scheduleEntry, Map resourceAllocationLists) {
        List overallocatedAssignments = new ArrayList();
        AssignmentList assignmentList = scheduleEntry.getAssignmentList();
        for (Iterator it = assignmentList.iterator(); it.hasNext();) {
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment)it.next();
            ResourceAllocationList ral = (ResourceAllocationList)resourceAllocationLists.get(assignment.getPersonID());
            if (ral.getMaximumAllocation(scheduleEntry.getStartTime(), scheduleEntry.getEndTime()) > 100) {
                overallocatedAssignments.add(assignment);
            }
        }
        return overallocatedAssignments;
    }
}
