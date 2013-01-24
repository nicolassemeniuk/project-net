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

 package net.project.schedule.calc;

import java.math.BigDecimal;
import java.util.Iterator;

import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Helper class for getting and setting work for calculation purposes.
 * <p/>
 * This class is necessary because work must be in a unit of <code>hours</code>
 * for calculation (since days or higher have an ambiguous number of hours in them).
 * Then the schedule entry's work value must be updated and converted to its original unit value.
 * @author Tim Morrow
 * @since Version 7.7.0
 */
class WorkHelper {

    /**
     * Returns the schedule entry work, in hours, converted using the appropriate
     * conversion ratio.
     * @param scheduleEntry the schedule entry whose work to get
     * @return the schedule entry work, in hours
     */
    static TimeQuantity getConvertedWork(ScheduleEntry scheduleEntry) {
        return ScheduleTimeQuantity.convertToHour(scheduleEntry.getWorkTQ());
    }

    /**
     * Sets the schedule entry work, converting to the original work unit using
     * an appropriate conversion ratio.
     * @param scheduleEntry the schedule entry on which to set work
     * @param work the work to set
     */
    static void setConvertedWork(ScheduleEntry scheduleEntry, TimeQuantity work) {
        scheduleEntry.setWork(ScheduleTimeQuantity.convertToUnit(work, scheduleEntry.getWorkTQ().getUnits(), 3, BigDecimal.ROUND_HALF_UP));
    }

    /**
     * Returns the assignment work, in hours, converted using the appropriate
     * conversionr ratio.
     * @param assignment the assignment whose work to get
     * @return the assignment work, in hours
     */
    static TimeQuantity getConvertedWork(ScheduleEntryAssignment assignment) {
        return ScheduleTimeQuantity.convertToHour(assignment.getWork());
    }

    /**
     * Sets the assignment wor, converting to the original work unit using
     * an appropriate conversion ratio.
     * @param assignment the assignment on which to set work
     * @param work the work to set
     */
    static void setConvertedWork(ScheduleEntryAssignment assignment, TimeQuantity work) {
        assignment.setWork(ScheduleTimeQuantity.convertToUnit(work, assignment.getWork().getUnits(), 3, BigDecimal.ROUND_HALF_UP));
    }

    /**
     * Updates the specified schedule entry's work as the sum of its assignments' work,
     * ensuring schedule entry's work unit is the specified unit.
     * @param scheduleEntry the schedule entry to update
     * @throws NullPointerException if scheduleEntry or desiredUnit are null
     */
    static void updateWorkFromAssignments(ScheduleEntry scheduleEntry) {

        if (scheduleEntry == null) {
            throw new NullPointerException("scheduleEntry is required");
        }

        //sjmittal:
        // Recalculate task work based on all assignments
        TimeQuantity oldWork = scheduleEntry.getWorkTQ();
        TimeQuantity taskWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        for (Iterator iterator = scheduleEntry.getAssignments().iterator(); iterator.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
            taskWork = taskWork.add(WorkHelper.getConvertedWork(nextAssignment));
        }
        //subtract and un associated work from the old work which was removed 
        //as a result of un assigning a resource
        oldWork = oldWork.subtract(scheduleEntry.getUnassociatedWorkComplete());
        TimeQuantity delta = oldWork.subtract(taskWork);
        if(delta.compareTo(TimeQuantity.O_HOURS) > 0) {
            //sjmittal: this means new work is less than the old work
            //we might have to reduce the un allocated work and work complete
            //if any for the task by delta
            TimeQuantity workComplete = scheduleEntry.getWorkCompleteTQ();
            TimeQuantity unallocateWorkComplete = scheduleEntry.getUnallocatedWorkComplete();
            if(unallocateWorkComplete.compareTo(delta) > 0) {
                scheduleEntry.setWorkComplete(workComplete.subtract(delta));
                scheduleEntry.setUnallocatedWorkComplete(unallocateWorkComplete.subtract(delta));
            }
        }

        //taskWork = taskWork.add(scheduleEntry.getUnassociatedWork());
        WorkHelper.setConvertedWork(scheduleEntry, taskWork);
    }

}