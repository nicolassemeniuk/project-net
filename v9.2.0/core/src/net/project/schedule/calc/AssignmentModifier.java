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

import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkCalculatorHelper;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a helper class for modifying an assignment (and changing the schedule entry
 * to which is is assigned).
 * @author Tim Morrow
 * @since Version 7.7
 */
class AssignmentModifier {

    private final ScheduleEntryAssignment assignment;
    private final ScheduleEntry scheduleEntry;
    private final IWorkingTimeCalendarProvider workingTimeCalendarProvider;

    /**
     * Creates a new assignment modifier where the specified assignment has been modified.
     * <p>
     * The assignment must be one of the schedule entry's assignments.
     * </p>
     * @param assignment the assignment that is being modified
     * @param scheduleEntry the schedule entry to which the specified assignment belongs
     * @param workingTimeCalendarProvider the provide of assignment and schedule working time calendars
     */
    public AssignmentModifier(ScheduleEntryAssignment assignment, ScheduleEntry scheduleEntry, IWorkingTimeCalendarProvider workingTimeCalendarProvider) {

        if (scheduleEntry.getAssignments().isEmpty()) {
            throw new IllegalArgumentException("Schedule entry with ID " + scheduleEntry.getID() + " has no assignments; no assignments can be modified.");
        }

        if (!scheduleEntry.getAssignmentList().containsForResource(assignment)) {
            throw new IllegalArgumentException("Schedule entry with ID " + scheduleEntry.getID() + " does not contain the resource assignment to be modified with ID " + assignment.getPersonID());
        }

        this.assignment = assignment;
        this.scheduleEntry = scheduleEntry;
        this.workingTimeCalendarProvider = workingTimeCalendarProvider;
    }

    /**
     * Changes the assignment's percentage to the specified value and recalculates
     * task duration as a result.
     * <p>
     * Assignment work (and therefore task work) do not change (except if percentage is zero, see below).
     * Duration and dates change.
     * </p>
     * <p>
     * <b>When percentage is zero</b> the assignment work is zero, which will affect task work.
     * </p>
     * @param newPercentageDecimal the new percentage for the current assignment
     * where <code>1.00</code> = 100%, <code>0.50</code> = 50%
     * @throws NullPointerException if the specified percentage is null
     */
    public void calculateDuration(BigDecimal newPercentageDecimal) {

        if (newPercentageDecimal == null) {
            throw new NullPointerException("newPercentageDecimal is required");
        }

        if (newPercentageDecimal.signum() == 0) {
            // When percentage is zero, the work _is_ adjusted; and the duration
            // is recalculate below
            calculateWork(newPercentageDecimal);

        } else {
            // Update the assignment's percentage and recalculate the duration
            // which will take that into account
            // Work isn't affected
            this.assignment.setPercentAssignedDecimal(newPercentageDecimal);

        }

        this.scheduleEntry.calculateDuration(this.workingTimeCalendarProvider);
    }

    /**
     * Changes the assignment's work to the specified value and recalculates task duration as a result.
     * <p/>
     * Task work changes but maintains its current unit.
     * Assignment percentage doesn't change unless it was previously 0%, in which case it is updated to 100%.
     * @param newWork the new work for the current assignment
     * @throws NullPointerException if the specified work is null
     */
    public void calculateDuration(TimeQuantity newWork) {

        if (newWork == null) {
            throw new NullPointerException("newWork is required");
        }

        // Update Assignment work; we accept the units of the new work
        this.assignment.setWork(newWork);

        // If assignment was previously 0%, make them 100%
        if (this.assignment.getPercentAssignedDecimal()!= null  && this.assignment.getPercentAssignedDecimal().signum() == 0) {
            this.assignment.setPercentAssignedDecimal(new BigDecimal("1.00"));
        }

        // Recalculate task work
        WorkHelper.updateWorkFromAssignments(this.scheduleEntry);

        scheduleEntry.calculateDuration(this.workingTimeCalendarProvider);
    }

    /**
     * Changes the assignment's percentage to the specifid value and
     * changes assignment work and task work such that the duration remains constant.
     * <p>
     * The new assignment work is (current work * (new percentage / current percentage)).
     * </p>
     * @param newPercentageDecimal the new percentage for the current assignment
     * where <code>1.00</code> = 100%, <code>0.50</code> = 50%
     * @throws NullPointerException if the specified percentage is null
     */
    public void calculateWork(BigDecimal newPercentageDecimal) {

        if (newPercentageDecimal == null) {
            throw new NullPointerException("newPercentageDecimal is required");
        }

        // Compute new assignment work
        TimeQuantity currentWork = WorkHelper.getConvertedWork(assignment);
        TimeQuantity newWork = currentWork.multiply(newPercentageDecimal).divide(assignment.getPercentAssignedDecimal(), 2, BigDecimal.ROUND_HALF_UP);

        // Update assignment work and percentage and task
        assignment.setPercentAssignedDecimal(newPercentageDecimal);
        WorkHelper.setConvertedWork(assignment, newWork);

        // Compute new task work
        WorkHelper.updateWorkFromAssignments(this.scheduleEntry);
    }

    /**
     * Changes the assignment work complete in such a way that all the
     * dependencies upon this field changing are properly handled.  These
     * required changes include:
     *
     * <ul>
     * <li>Change the task's work complete</li>
     * <li>Change the assignment's percentage complete.</li>
     * <li>Change the assignment's isComplete flag, if necessary.</li>
     * </ul>
     *
     * @param newWorkComplete a <code>TimeQuantity</code> indicating the total
     * amount of work that is now completed.
     */
    //sjmittal: as per de coupling assignment's work complete can only be calculated
    //as the actual work done by that assignment
//    public void calculateWorkComplete(TimeQuantity newWorkComplete) {
//        TimeQuantity delta = newWorkComplete.subtract(assignment.getWorkComplete());
//        assignment.setWorkComplete(newWorkComplete);
//        scheduleEntry.setWorkComplete(scheduleEntry.getWorkCompleteTQ().add(delta));
//
//        assignment.setComplete(assignment.getWork().equals(assignment.getWorkComplete()));
//    }

//    public void redistributeUnallocatedWorkComplete() {
//        AssignmentAdder.redistributeWorkComplete(scheduleEntry, workingTimeCalendarProvider);
//    }

    /**
     * Changes the assignment's work to the specified value and computes task work
     * and assignment percentage such that the assignment completes the new amount
     * of work in the same duration.
     * <p/>
     * The new assignment percentage is (new work / old work) * percentage.
     * <p/>
     * If the new work is zero, then the assigment percentage is not change, and the work is set to zero. <br/>
     * If the current assignment work is zero, then the percentage has to be computed based on the duration.
     * <p/>
     * Task work maintains its current unit.
     * @param newWork the new work for the current assignment
     * @throws NullPointerException if the specified work is null
     */
    public void calculatePercentage(TimeQuantity newWork) {

        if (newWork == null) {
            throw new NullPointerException("newWork is required");
        }

        // Save the work unit; we'll want to convert it back later
        TimeQuantityUnit savedUnit = newWork.getUnits();

        // Convert work to common unit for calculation
        newWork = ScheduleTimeQuantity.convertToHour(newWork);

        boolean isCalculateDates = false;

        BigDecimal newPercentageDecimal;
        if (newWork.isZero()) {
            // In the case of zero work, we don't actually change the assignment percentage
            newPercentageDecimal = this.assignment.getPercentAssignedDecimal();

        } else {

            // Only when work is non-zero to we recalculate percentage
            // Compute new assignment percentage
            TimeQuantity currentWork = WorkHelper.getConvertedWork(assignment);
            if (currentWork.isZero()) {
                // We can't calculate the new percentage as a ratio of new work to old work, since
                // old work is zero
                // The percentage has to be computed based on completing the new work within the duration
                // given the working time calendar
                WorkCalculatorHelper workCalc = new WorkCalculatorHelper(this.assignment.getWorkingTimeCalendar(this.workingTimeCalendarProvider));
                TimeQuantity maximumWork = workCalc.getWork(this.scheduleEntry.getStartTime(), this.scheduleEntry.getEndTime(), new BigDecimal("1.00"));

                if (maximumWork.isZero()) {
                    // The assignment cannot currently complete any work within the current dates
                    // So, we simply accept the new amount of work, at the current percentage
                    // And adjust the dates
                    newPercentageDecimal = assignment.getPercentAssignedDecimal();
                    isCalculateDates = true;

                } else {
                    // The assignment can do some work within the duration
                    // Calculate the percentage they need to work at to complete the specified work
                    newPercentageDecimal = newWork.divide(maximumWork, 2, BigDecimal.ROUND_HALF_UP);

                }

            } else {
                //Calculate new percentage effort
                TimeQuantity possibleAssignmentDuration = assignment.
                    getAssignmentDuration(workingTimeCalendarProvider,
                    scheduleEntry.getStartTime(), scheduleEntry.getEndTime());
                if (possibleAssignmentDuration.isZero()) {
                    newPercentageDecimal = new BigDecimal("0.0000000000");
                } else {
                    newPercentageDecimal = newWork.divide(possibleAssignmentDuration, 10, BigDecimal.ROUND_HALF_UP);
                }
            }

        }

        assignment.setPercentAssignedDecimal(newPercentageDecimal);
        assignment.setWork(ScheduleTimeQuantity.convertToUnit(newWork, savedUnit, 3, BigDecimal.ROUND_HALF_UP));

        // Compute new task work
        WorkHelper.updateWorkFromAssignments(this.scheduleEntry);

        if (isCalculateDates) {
            // The special case whereby duration had to change
            this.scheduleEntry.calculateEndDate(this.workingTimeCalendarProvider);
        }

    }

}
