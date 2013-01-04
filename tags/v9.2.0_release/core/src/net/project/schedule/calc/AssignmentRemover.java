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

import net.project.calendar.workingtime.IDaysWorked;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a helper class for removing an assignment from a schedule entry.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
class AssignmentRemover {

    /**
     * The assignment to remove.
     */
    private final ScheduleEntryAssignment assignment;

    /**
     * The schedule entry from which to remove the assignment.
     */
    private final ScheduleEntry scheduleEntry;

    /**
     * The provider of working time calendars.
     */
    private final IWorkingTimeCalendarProvider workingTimeCalendarProvider;

    /**
     * Creates a new assignment remover indicating that the specified assignment is being removed from the
     * specified schedule entry.
     * <p>
     * The schedule entry duration / work and assignment work / percentage will be recalculated and
     * the assignment removed from the schedule entry.
     * </p>
     * @param assignment the assignment to remove
     * @param scheduleEntry the schedule entry from which to remove the assignment
     * @param provider the provider of resource and schedule working time calendars
     * @throws IllegalArgumentException if the specified assignment is not assigned to the schedule entry
     * (specifically, the resource for the assignment is not assigned to the schedule entry).
     */
    public AssignmentRemover(ScheduleEntryAssignment assignment, ScheduleEntry scheduleEntry, IWorkingTimeCalendarProvider provider) {

        // Check the assignment is in the scheduler entry
        if (!scheduleEntry.getAssignmentList().containsForResource(assignment)) {
            throw new IllegalArgumentException("Schedule entry does not contain the specified assignment.");
        }

        // We grab the actual schedule entry for the specified assignment's resource person ID
        // This ensures the work that we remove from the task is definitely from the assignment
        // we're removing
        this.assignment = (ScheduleEntryAssignment) scheduleEntry.getAssignmentList().getForResourceID(assignment.getPersonID());
        this.scheduleEntry = scheduleEntry;
        this.workingTimeCalendarProvider = provider;
    }

    public void removeAssignment(TaskCalculationType calcType) {
        if (calcType.isEffortDriven()) {
            //If the task type is effort driven we ALWAYS have to share work.
            //Not exactly sure what case Tim found that was different, but I need
            //to be shown this case.  (It is probably an MS bug)
            shareWork(TaskCalculationType.FIXED_DURATION_TYPES.contains(calcType));
        } else {
            //Any case where we are not effort driven should be pretty easy.  Just
            //remove the assignment and everything else stays the same.
            removeWork(!TaskCalculationType.FIXED_DURATION_TYPES.contains(calcType));
        }
    }

    /**
     * Removes the assignment and subtracts their work from task work.
     * @param isCalculateDuration true if duration should be recalculated; false otherwise.
     * Note that duration is _never_ recalculated if the last assignment is removed
     */
    private void removeWork(boolean isCalculateDuration) {
        // Remove the assignment
        this.scheduleEntry.removeAssignment(this.assignment);
        scheduleEntry.setUnassociatedWorkComplete(scheduleEntry.getUnassociatedWorkComplete().add(assignment.getWorkComplete()));
        TimeQuantity newWorkComplete = scheduleEntry.getWorkCompleteTQ().subtract(ScheduleTimeQuantity.convertToUnit(assignment.getWorkComplete(), scheduleEntry.getWorkCompleteTQ().getUnits(), 5, BigDecimal.ROUND_HALF_UP));
        scheduleEntry.setWorkComplete(newWorkComplete.getAmount().signum() > 0 ? newWorkComplete : TimeQuantity.O_HOURS);

        if (this.scheduleEntry.getAssignments().isEmpty()) {
            // sjmittal:
            // remove the work complete for the task total work as this has now gone to the un associated work
            scheduleEntry.setWork(scheduleEntry.getWorkTQ().subtract(ScheduleTimeQuantity.convertToUnit(assignment.getWorkComplete(), scheduleEntry.getWorkTQ().getUnits(), 5, BigDecimal.ROUND_HALF_UP)));
            
            if (isCalculateDuration && !this.scheduleEntry.getWorkTQ().isZero()) {
                
                computeDuration();
                this.scheduleEntry.calculateEndDate(this.workingTimeCalendarProvider);
            }


        } else {
            // Recalculate task work
            WorkHelper.updateWorkFromAssignments(this.scheduleEntry);

            if (isCalculateDuration && !this.scheduleEntry.getWorkTQ().isZero()) {
                this.scheduleEntry.calculateDuration(this.workingTimeCalendarProvider);
            }
        }

    }

    /**
     * If the user had just been assigned work complete, that work complete will
     * be reassigned to the schedule entry.  Otherwise, the assignment is removed
     * and the assignment keeps the work that it completed.
     */
//    public void calculateWorkComplete() {
//        //See if this assignment was just added
//        if (assignment.getDistributedWorkComplete() != null && !assignment.getDistributedWorkComplete().isZero()) {
//            TimeQuantity undistributedWorkComplete = scheduleEntry.getUnallocatedWorkComplete();
//            //undistributedWorkComplete = undistributedWorkComplete.add(assignment.getDistributedWorkComplete());
//            undistributedWorkComplete = undistributedWorkComplete.add(assignment.getWorkComplete());
//            scheduleEntry.setUnallocatedWorkComplete(undistributedWorkComplete);
//            AssignmentAdder.redistributeWorkComplete(scheduleEntry, this.workingTimeCalendarProvider);
//        }
//
//        if (assignment.getDistributedWorkComplete().compareTo(assignment.getWorkComplete()) < 0) {
//            TimeQuantity unassociatedWork = assignment.getWorkComplete().subtract(assignment.getDistributedWorkComplete());
//            assignment.setDistributedWorkComplete(TimeQuantity.O_HOURS);
//            scheduleEntry.setUnassociatedWork(unassociatedWork);
//            AssignmentAdder.redistributeWorkComplete(scheduleEntry, this.workingTimeCalendarProvider);
//        }
//    }

    /**
     * Shares the removed assignment's work with other assignments while maintaining
     * the relative proportion of work that each assignment has.
     * <p/>
     * If some remaining assignments currently have zero hours of work, they receive no
     * additional work, unless all remaining assignments currently have zero hours of work,
     * in which case work is divided amongst them and percentage is never recalculated. <br/>
     * Additionally, duration is never recalculated if task work is zero.
     * @param isCalculatePercentage true if assignment percentage should be recalculated
     * to ensure they complete the work in the same time; false to not recalculate percentage.
     * When false, duration is recalculated (as long as there is at least one remaining assignment and
     * some task work)
     */
    private void shareWork(boolean isCalculatePercentage) {

        // Remove the assignment
        scheduleEntry.removeAssignment(assignment);
        scheduleEntry.setUnassociatedWorkComplete(scheduleEntry.getUnassociatedWorkComplete().add(assignment.getWorkComplete()));
        TimeQuantity newWorkComplete = scheduleEntry.getWorkCompleteTQ().subtract(ScheduleTimeQuantity.convertToUnit(assignment.getWorkComplete(), scheduleEntry.getWorkCompleteTQ().getUnits(), 5, BigDecimal.ROUND_HALF_UP));
        scheduleEntry.setWorkComplete(newWorkComplete.getAmount().signum() > 0 ? newWorkComplete : TimeQuantity.O_HOURS);

        if (this.scheduleEntry.getAssignments().isEmpty()) {
            // sjmittal:
            // remove the work complete for the task total work as this has now gone to the un associated work
            scheduleEntry.setWork(scheduleEntry.getWorkTQ().subtract(ScheduleTimeQuantity.convertToUnit(assignment.getWorkComplete(), scheduleEntry.getWorkTQ().getUnits(), 5, BigDecimal.ROUND_HALF_UP)));

            if (!isCalculatePercentage && !this.scheduleEntry.getWorkTQ().isZero()) {

                computeDuration();
                this.scheduleEntry.calculateEndDate(this.workingTimeCalendarProvider);
            }

        } else {
            //sjmittal: share only the remaining task work as work complete is removed
            TimeQuantity removedTaskWork = assignment.getWorkRemaining();
            //            removedTaskWork = removedTaskWork.add(assignment.getWorkComplete());
            TimeQuantity allRemainingTaskWork = TimeQuantity.O_HOURS;
            for (Iterator it = scheduleEntry.getAssignmentList().iterator(); it.hasNext();) {
                ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) it.next();
                allRemainingTaskWork = allRemainingTaskWork.add(assignment.getWorkRemaining());
            }

            // Update remaining assignment work and percentage based to maintain constant task work
            for (Iterator iterator = this.scheduleEntry.getAssignments().iterator(); iterator.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();

                // First calculate new work
                TimeQuantity newAssignmentWork;
                if (allRemainingTaskWork.isZero()) {
                    //divide the work equally
                    newAssignmentWork = removedTaskWork.divide(new BigDecimal(this.scheduleEntry.getAssignmentList().size()), 10, BigDecimal.ROUND_HALF_UP);
                } else {
                    //divide the work in the ratio of work remaining for each
                    newAssignmentWork = removedTaskWork.multiply(nextAssignment.getWorkRemaining().divide(allRemainingTaskWork, 10, BigDecimal.ROUND_HALF_UP));
                }
                //                newAssignmentWork = newAssignmentWork.add(nextAssignment.getWork());
                nextAssignment.setWork(nextAssignment.getWork().add(newAssignmentWork));

                if (isCalculatePercentage) {
                    TimeQuantity possibleAssignmentDuration = nextAssignment.getAssignmentDuration(workingTimeCalendarProvider, scheduleEntry.getStartTime(), scheduleEntry.getEndTime());
                    BigDecimal newAssignmentPercentage;
                    if (possibleAssignmentDuration.isZero()) {
                        newAssignmentPercentage = new BigDecimal("0.0000000000");
                    } else {
                        newAssignmentPercentage = nextAssignment.getWork().divide(possibleAssignmentDuration, 10, BigDecimal.ROUND_HALF_UP);
                    }
                    nextAssignment.setPercentAssignedDecimal(newAssignmentPercentage);
                }
            }

            // Recalculate task work
            WorkHelper.updateWorkFromAssignments(this.scheduleEntry);

            if (!isCalculatePercentage && !this.scheduleEntry.getWorkTQ().isZero()) {
                // Recalculate duration if we didn't calculate percentage
                // We never reaclculate duration if task work is zero
                this.scheduleEntry.calculateDuration(this.workingTimeCalendarProvider);
            }

        }
    }

    private void computeDuration() {

        // Create a 100% assignment with all the work
        ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
        assignment.setPercentAssigned(100);
        assignment.setWork(this.scheduleEntry.getWorkTQ());
        assignment.setStartTime(this.scheduleEntry.getStartTime());
        assignment.setEndTime(this.scheduleEntry.getEndTime());
        IDaysWorked assignmentDaysWorked = assignment.getDaysWorked(this.scheduleEntry.getStartTime(), this.workingTimeCalendarProvider);
        TimeQuantity timeQuantityInDays = new TimeQuantity(assignmentDaysWorked.getTotalDays(), TimeQuantityUnit.DAY);
        TimeQuantityUnit originalUnit = this.scheduleEntry.getDurationTQ().getUnits();
        TimeQuantity timeQuantityInUnit = ScheduleTimeQuantity.convertToUnit(timeQuantityInDays, originalUnit, 3, BigDecimal.ROUND_HALF_UP);
        this.scheduleEntry.setDuration(timeQuantityInUnit);
    }

}