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
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.project.calendar.workingtime.IDaysWorked;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a helper class to change a task and assignments when work is modified.
 * <p>
 * Performs two functions:
 * <ul>
 * <li>Divides work amongst assignments given their current ratios of assignment work to task work
 * then recalculates task duration based on the new work; assignment percentages remain constant
 * <li>Divides work amongst assignments then recalculates assignment percentage; duration remains constant
 * </ul>
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
class ScheduleEntryWorkModifier {

    /**
     * The schedule entry being modified.
     */
    private final ScheduleEntry scheduleEntry;

    /**
     * The assignments we are working with.
     */
    private final Collection assignments;

    /**
     * The provider of working time calendars for each assignment resource.
     */
    private final IWorkingTimeCalendarProvider workingTimeCalendarProvider;

    /**
     * Creates a new ScheduleEntryWorkModifier for modifying the work (and assignments) of the specified schedule entry.
     *
     * @param scheduleEntry the schedule entry whose work and assignments to modify
     * @param workingTimeCalendarProvider the provider of working time calendars for assignment resources
     */
    public ScheduleEntryWorkModifier(ScheduleEntry scheduleEntry, IWorkingTimeCalendarProvider workingTimeCalendarProvider) {
        this.scheduleEntry = scheduleEntry;
        this.assignments = scheduleEntry.getAssignments();
        this.workingTimeCalendarProvider = workingTimeCalendarProvider;
    }

    /**
     * Modifies the task and assignment work and recalculates the task duration.
     * <p>
     * <ul>
     * <li>When there are no assignments, duration is not recalculated; work is simply
     * set.
     * <li>When there are assignments, task work and duration is changed.  Assignment work is changed.
     * </ul>
     * </p>
     * @param newWork the new task work value
     * @throws NullPointerException if newWork is null
     */
    public void calculateDuration(TimeQuantity newWork) {

        if (newWork == null) {
            throw new NullPointerException("newWork must be specified");
        }

//        boolean isCurrentDurationZero = (this.scheduleEntry.getDurationTQ().isZero());

        allocateWorkToAssignments(this.assignments, this.scheduleEntry.getWorkTQ(), newWork, false);

        //sjmittal: a genral fix: always compute duration
        // Now recalculate duration
        // We only do this if duration was previously zero, or there were are assignments
//        if (isCurrentDurationZero || !this.assignments.isEmpty()) {

            computeDuration();

            if (this.scheduleEntry.getStartTime() != null) {
                this.scheduleEntry.calculateEndDate(this.workingTimeCalendarProvider);
            }

//        }
    }

    /**
     * Modifies the task work and assignment work and recalculates assignment percentages.
     * <p>
     * Task work is changed.  Assignment work and percentage is changed.
     * Dutation is not changed unless duration is currently zero, in which case duration is recalculated
     * based on the new work.
     * </p>
     * @param newWork the new task work value
     * @throws NullPointerException if newWork is null
     */
    public void calculatePercentage(TimeQuantity newWork) {

        if (newWork == null) {
            throw new NullPointerException("newWork must be specified");
        }

        boolean isCurrentDurationZero = (this.scheduleEntry.getDurationTQ().isZero());

        allocateWorkToAssignments(this.assignments, this.scheduleEntry.getWorkTQ(), newWork, true);

        if (isCurrentDurationZero) {
            // If the duration was zero, then we recalculate since we just set work to some
            // value, thus allowing a duration to be calculated
            computeDuration();
        }
    }

    /**
     * Allocates a portion of the specified new task work to each assignment given the specified current task work.
     * <p>
     * If there are no assignments, then the task work is simply updated to the new task work.
     * </p>
     * <p>
     * New assignment work calculated as: <br>
     * <code>new task work * (ratio of assignment work to old task work)</code>
     * Thus, all the new task work is distributed to all assignments and the ratio of each assignment's work to every
     * other assignment's work remains constant.
     * </p>
     * <p>
     * If current task work is zero which implies existing assignment work is zero,
     * then each assignment receives an equal share of new task work regardless of
     * percentage assigned. Percentage is not recalculated.
     * </p>
     * <p>
     * If the calculatePercentage flag is set to true, new percentage is calculated as: <br>
     * <code>current percentage * (new assignment work / old assignment work)</code>
     * </p>
     * @param assignments the assignments whose work to modify
     * @param currentTaskWork the current task work
     * @param newTaskWork the new task work
     * @param isCalculatePercentage true to recalculate assignment percentage such that new amount
     * of work is completed in same time as old amount; false for percentage to remain the same
     */
    private void allocateWorkToAssignments(Collection assignments, TimeQuantity currentTaskWork, TimeQuantity newTaskWork, boolean isCalculatePercentage) {


        if (assignments.isEmpty()) {
            // No assignments to allocate to
            // Instead, task work is modified to new value
            this.scheduleEntry.setWork(newTaskWork);

        } else {
            // Convert work using appropriate conversions
            TimeQuantity currentTaskWorkHours = ScheduleTimeQuantity.convertToHour(currentTaskWork);
            TimeQuantity newTaskWorkHours = ScheduleTimeQuantity.convertToHour(newTaskWork);
            TimeQuantity taskWorkDelta = newTaskWorkHours.subtract(currentTaskWorkHours);

            TimeQuantity remainingAssignmentWork = TimeQuantity.O_HOURS;
            for (Iterator it = scheduleEntry.getAssignments().iterator(); it.hasNext();) {
                ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();
                remainingAssignmentWork = remainingAssignmentWork.add(ScheduleTimeQuantity.convertToHour(assn.getWorkRemaining()));
            }

            // Check to see if current task work is zero; that means all current assignment work is zero
            // In this case, each assignment receives an equal share of work,
            // regardless of percentage assigned
            TimeQuantity evenShareWorkHours = newTaskWorkHours.divide(new BigDecimal(assignments.size()), 3, BigDecimal.ROUND_HALF_UP);

            // We always calculate task work as the sum of assignment work to ensure
            // That in the event of some "invalid state" (where assignment work isn't
            // calculated accurately etc.) the task work always sums to assignment work
            TimeQuantity computedTaskWorkHours = new TimeQuantity(0, TimeQuantityUnit.HOUR);

            for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
                TimeQuantityUnit originalUnit = nextAssignment.getWork().getUnits();
                TimeQuantity currentAssignmentWorkHours = ScheduleTimeQuantity.convertToHour(nextAssignment.getWork());
                TimeQuantity newAssignmentWorkHours;

                if (currentTaskWorkHours.isZero()) {
                    // Each assignment gets the same share and their percentage does not change
                    newAssignmentWorkHours = evenShareWorkHours;

                } else {
                    if(remainingAssignmentWork.isZero()) {
                        // assignments had completed all their work and now new work is added
                        newAssignmentWorkHours = taskWorkDelta.divide(new BigDecimal(assignments.size()), 3, BigDecimal.ROUND_HALF_UP);
                        newAssignmentWorkHours = newAssignmentWorkHours.add(nextAssignment.getWork());
                    } else {
                        // Calculate assignment's new work based as a portion of new task work
                        newAssignmentWorkHours = taskWorkDelta.multiply(nextAssignment.getWorkRemaining().divide(remainingAssignmentWork, 10, BigDecimal.ROUND_HALF_UP));
                        newAssignmentWorkHours = newAssignmentWorkHours.add(nextAssignment.getWork());
                    }

                    if (isCalculatePercentage) {
                        BigDecimal percentAssignedDecimal = nextAssignment.getPercentAssignedDecimal();
                        // Now calculate percentage based on the ratio of their new work to old work
                        BigDecimal newPercentAssigned = percentAssignedDecimal.multiply(newAssignmentWorkHours.divide(currentAssignmentWorkHours, 3, BigDecimal.ROUND_HALF_UP));

                        nextAssignment.setPercentAssignedDecimal(newPercentAssigned);
                    }

                }

                // Add to task work, maintain precision
                computedTaskWorkHours = computedTaskWorkHours.add(newAssignmentWorkHours);

                // Update assignment work, with fewer decimal places
                nextAssignment.setWork(ScheduleTimeQuantity.convertToUnit(newAssignmentWorkHours, originalUnit, 3, BigDecimal.ROUND_HALF_UP));

            }

            // Make sure to preserve the units that the user selected for the new work
            this.scheduleEntry.setWork(ScheduleTimeQuantity.convertToUnit(computedTaskWorkHours, newTaskWork.getUnits(), 3, BigDecimal.ROUND_HALF_UP));
        }

    }

    /**
     * Compute new task duration, taking into consideration schedule entries
     * with null dates.
     * <p/>
     * When assignments are empty, duration is recomputed based on a 100% assignment
     * performing all the task work.
     */
    private void computeDuration() {

        // When creating new schedule entries, no dates have yet been specified
        // In that case, we arbitrarily set the start date for the purposes of
        // calculating duration, then reset the dates back to null afterwards
        boolean isNullDates = false;
        if (this.scheduleEntry.getStartTime() == null) {
            isNullDates = true;
            this.scheduleEntry.setStartTimeD(new Date());
        }
        if (this.scheduleEntry.getAssignments().isEmpty()) {
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
        } else {
            // Recalculate duration on existing assignments
            this.scheduleEntry.calculateDuration(this.workingTimeCalendarProvider);
        }


        if (isNullDates) {
            this.scheduleEntry.setStartTimeD(null);
            this.scheduleEntry.setEndTimeD(null);
        }
    }

}