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
import java.util.Date;
import java.util.TimeZone;

import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.persistence.PersistenceException;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.schedule.TaskConstraintType;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a helper class that updates a schedule entry based on its
 * calculation type.
 * <p>
 * The schedule entry's duration, work or assignments are modified.
 * </p>
 * 
 * @author Tim Morrow
 * @since Version 7.7
 */
public class ScheduleEntryCalculator {

	/**
	 * The schedule entry being modified.
	 */
	private final ScheduleEntry scheduleEntry;
    
	/**
	 * Provides working time calendars for the schedule, resources.
	 */
	private final IWorkingTimeCalendarProvider provider;

	/**
	 * Creates a schedule entry calculator for modifying the specified schedule
	 * entry.
	 * 
	 * @param scheduleEntry the schedule entry being modified
	 * @param provider the provider of schedule and resource working time calendars
	 */
    public ScheduleEntryCalculator(ScheduleEntry scheduleEntry, IWorkingTimeCalendarProvider provider) {
        this.scheduleEntry = scheduleEntry;
        this.provider = provider;
    }

	/**
	 * Indicates that the duration changed to the specified value.
	 * <p>
	 * <ul>
	 * <li>Fixed Unit, Fixed Duration - Recalculate assignment work based on
	 * new duration
	 * <li>Fixed Work - Recalculate assignment percentages based on new
	 * duration
	 * </ul>
	 * </p>
	 * 
	 * @param newDuration the schedule entry's new duration
	 * @throws PersistenceException
	 */
	public void durationChanged(final TimeQuantity newDuration) {
		TaskCalculationType calcType = scheduleEntry.getTaskCalculationType();

		ScheduleEntryDurationModifier durationModifier = new ScheduleEntryDurationModifier(this.scheduleEntry, this.provider);

		if (calcType.isFixedUnit() || calcType.isFixedDuration()) {
			durationModifier.calculateWork(newDuration);
		} else {
			durationModifier.calculatePercentage(newDuration);
		}
        
		//  sjmittal: moved this piece of code to the caller method
//		if (this.scheduleEntry.getConstraintType().isDateConstrained()) {
//			TaskEndpointCalculation tec = new TaskEndpointCalculation();
//			tec.getCache().put(this.scheduleEntry.getID(),this.scheduleEntry);
//			tec.recalculateTaskTimesSpecial(this.scheduleEntry);
//		}

	}

	/**
	 * Indicates that work changed to the specified value.
	 * <p>
	 * <ul>
	 * <li>Fixed Unit, Fixed Work - Add a portion of the new work to each
	 * assignment then recalculate task duration. Task work changes, assignment
	 * percentages remain constant.
	 * <li>Fixed Duration - Add a portion of the new work to each assignment
	 * then recalculate percentage assignment. Task work changes, task duration
	 * remains constant.
	 * </ul>
	 * </p>
	 * 
	 * @param newWork the schedule entry's new work
	 */
	public void workChanged(final TimeQuantity newWork) {
		TaskCalculationType calcType = scheduleEntry.getTaskCalculationType();

		ScheduleEntryWorkModifier workModifier = new ScheduleEntryWorkModifier(this.scheduleEntry, this.provider);

		if (calcType.isFixedUnit() || calcType.isFixedWork()) {
			workModifier.calculateDuration(newWork);

		} else {
			workModifier.calculatePercentage(newWork);
		}

	}

	/**
	 * Indicates that work has changed to the indicated value. Updates the
	 * schedule entry and assignments accordingly.
	 * 
	 * @param workComplete
	 *            a <code>TimeQuantity</code> containing the new amount of
	 *            work completed for this task. We deal with either an increase
	 *            or a decrease in work, though a decrease in work complete is
	 *            decidedly more messy.
	 */
	public void workCompleteChanged(TimeQuantity workComplete) {
		ScheduleEntryWorkCompleteModifier.computeWorkComplete(this.provider, scheduleEntry, workComplete);

		if ((!workComplete.isZero()) && scheduleEntry.getActualStartTime() == null) {

			// if (scheduleEntry.getAssignmentList().size() > 0) {
			// scheduleEntry.setActualStartTimeD(scheduleEntry.getAssignmentList().findEarliestWorkLogTime());
			// } else {
			Date startDate = scheduleEntry.getResourceCalendar(provider).getStartOfWorkingDay(new Date());
			startDate = DateUtils.max(startDate, scheduleEntry.getStartTime());
			scheduleEntry.setActualStartTimeD(startDate);
			// }
		}
		if (scheduleEntry.getWorkPercentComplete().toString().compareTo("100%") == 0) {
			Date endDate = scheduleEntry.getResourceCalendar(provider).getEndOfWorkingDay(new Date());
			endDate = DateUtils.max(endDate, scheduleEntry.getEndTime());
			scheduleEntry.setActualEndTimeD(endDate);
		}
	}

	/**
	 * Indicates that work percent complete has changed to the indicated value.
	 * Updates the schedule entry and assignments accordingly.
	 */
	public void workPercentCompleteChanged(BigDecimal workPercentComplete) {
		// If the work on the schedule entry is zero, we don't touch the work
		// complete.
		if (!scheduleEntry.getWorkTQ().isZero()) {
			// We need to calculate a new work percent complete if we haven't
			// already
			TimeQuantity work = ScheduleTimeQuantity.convertToHour(scheduleEntry.getWorkTQ());
			TimeQuantity workComplete = ScheduleTimeQuantity.convertToHour(scheduleEntry.getWorkCompleteTQ());

			BigDecimal existingWorkPercentComplete;
			if (work.isZero()) {
				existingWorkPercentComplete = new BigDecimal("0.00000");
			} else {
				existingWorkPercentComplete = workComplete.divide(work, 5, BigDecimal.ROUND_HALF_UP);
			}

			// Check to see if the existing work percent complete and this work
			// complete
			// match. If so, we don't have to calculate work completed.
			if (!workPercentComplete.equals(existingWorkPercentComplete)) {
				TimeQuantityUnit originalUnit = scheduleEntry.getWorkCompleteTQ().getUnits();
				workComplete = work.multiply(workPercentComplete);

				workCompleteChanged(ScheduleTimeQuantity.convertToUnit(workComplete, originalUnit, 3, BigDecimal.ROUND_HALF_UP));
			}
		}
		scheduleEntry.setWorkPercentComplete(workPercentComplete);
	}

	/**
	 * Indicates that an assignment percentage value changed.
	 * <p>
	 * <ul>
	 * <li>Fixed Unit, Fixed Work - Duration recalculated based on new
	 * percentage value
	 * <li>Fixed Duration - Assignment work and task work are modified so that
	 * the assignment completes their work within the same time period (thus
	 * duration remains constant)
	 * 
	 * @param newPercentageDecimal the assignment's new percentage value
	 * @param assignment the assignment that changed
	 */
	public void assignmentPercentageChanged(final BigDecimal newPercentageDecimal, ScheduleEntryAssignment assignment) {
		TaskCalculationType calcType = scheduleEntry.getTaskCalculationType();

		AssignmentModifier assignmentModifier = new AssignmentModifier(assignment, this.scheduleEntry, this.provider);

		if (calcType.isFixedUnit() || calcType.isFixedWork()) {
			// change duration
			assignmentModifier.calculateDuration(newPercentageDecimal);

		} else {
			// change work
			assignmentModifier.calculateWork(newPercentageDecimal);

		}

//		assignmentModifier.redistributeUnallocatedWorkComplete();
	}

	/**
	 * Indicates that an assignment work value changed.
	 * <p>
	 * <ul>
	 * <li>Fixed Unit, Fixed Work - Duration recalculated based on new task
	 * work
	 * <li>Fixed Duration - Assignment work and task work are modified,
	 * assignment percentage modified to complete new work in current duration
	 * 
	 * @param newWork the assignment's new percentage value
	 * @param assignment the assignment that changed
	 */
	public void assignmentWorkChanged(final TimeQuantity newWork, ScheduleEntryAssignment assignment) {
		TaskCalculationType calcType = scheduleEntry.getTaskCalculationType();

		AssignmentModifier assignmentModifier = new AssignmentModifier(assignment, this.scheduleEntry, this.provider);

		if (calcType.isFixedUnit() || calcType.isFixedWork()) {
			// change duration
			assignmentModifier.calculateDuration(newWork);

		} else {
			// change percentage
			assignmentModifier.calculatePercentage(newWork);

		}

//		assignmentModifier.redistributeUnallocatedWorkComplete();
	}

    //sjmittal: as the result of decoupling now no one can change assignments work complete directly.
    //assignmnets work complete is now always derived from actual work done by that resource for that task
//	public void assignmentWorkCompleteChanged(TimeQuantity newWorkComplete, ScheduleEntryAssignment assignment) {
//		AssignmentModifier assignmentModifier = new AssignmentModifier(assignment, this.scheduleEntry, this.provider);
//		assignmentModifier.calculateWorkComplete(newWorkComplete);
//	}

	/**
	 * Indicates that the assignment was added to the current schedule entry.
	 * <p>
	 * Assignment work and percentage are computed.
	 * <ul>
	 * <li>Effort Driven - Task work does not change. New assignment's work is
	 * computed as the amount they can do within the current duration (with
	 * variations). A portion of each existing assignment's work is taken away
	 * while keeping the relative amounts of assignment work constant.
	 * <ul>
	 * <li>Fixed Unit, Fixed Work - Assignment work computed using percentage
	 * _ignoring_ their working time calendar. Duration is recalculated. Other
	 * assignment percentages remain constant.
	 * <li>Fixed Duration - Assignment work computed as the amount they can do
	 * at a particular percentage considering their working time calendar. It is
	 * therefore possible that the assignment is assigned zero hours if they
	 * can't work over the current dates. The assignment is assumed to work at
	 * the same percentage as the existing assignment with the largest
	 * assignment percentage, or 100% if there are no assignments or the largest
	 * percentage is over 100%). All assignment percentages calculated (when
	 * zero work performed, percentage value is 100%). Duration remains
	 * constant.
	 * </ul>
	 * <li>Non-Effort Driven - Task Work increases. New assignment's work is
	 * computed and based on the amount they can do within the duration (but
	 * differs as described below). Assignment added as 100%.
	 * <ul>
	 * <li>Fixed Unit - The new assignment's work is calculated as the most
	 * they can do given their percentage within the current duration _ignoring
	 * their working time calendar_. Duration is recalculated.
	 * <li>Fixed Duration - The new assignment's work is calculated as the most
	 * they can do given their percentage within the current duration,
	 * considering their working time calendar. Duration remains constant.
	 * </ul>
	 * </ul>
	 * </p>
	 * 
	 * @param newPercentageDecimal
	 *            the percentage of the new assignments; must be null on a Fixed
	 *            Duration, Effort Driven task (since percentage is computed)
	 * @param assignment
	 *            the assignment added
	 * @throws IllegalArgumentException
	 *             if the percentage value is specified when the schedule entry
	 *             is Fixed Duration, Effort Driven; in this case the percentage
	 *             will be calculated
     * @throws NoWorkingTimeException
     *             if there is no working time found between the tasks start and end date
     *             for this assignment and task has some work
     *             
	 */
	public void assignmentAdded(final BigDecimal newPercentageDecimal, ScheduleEntryAssignment assignment) throws NoWorkingTimeException {
		AssignmentAdder assignmentAdder = new AssignmentAdder(assignment, this.scheduleEntry, this.provider);
		assignmentAdder.addAssignment(newPercentageDecimal);
//		assignmentAdder.calculateWorkComplete();
	}

	/**
	 * Indicates that the assignment was removed from the current schedule
	 * entry.
	 * <p>
	 * Assignment work and percentages are computed.
	 * <ul>
	 * <li>Effort Driven - Task work does not change. The removed assignment's
	 * work is divided amongst the remaining assignments (increasing their work)
	 * while keeping the relative amount of work for each assignment constant.
	 * <ul>
	 * <li>Fixed Unit, Fixed Work - Duration is recalculated. Assignent
	 * percentages remain constant.
	 * <li>Fixed Duration - Remaining assignment percentages are recalculated.
	 * Duration remains constant.
	 * </ul>
	 * <li>Non-Effort Driven - Task work changes by the amount of work that the
	 * assignment performed.
	 * <ul>
	 * <li>Fixed Unit - Duration is recalculated.
	 * <li>Fixed Duration - Assignment percentages are recalculated.
	 * </ul>
	 * </ul>
	 * </p>
	 * 
	 * @param assignment
	 *            assignment removed
	 */
	public void assignmentRemoved(ScheduleEntryAssignment assignment) {
		assignmentRemoved(assignment, scheduleEntry.getTaskCalculationType());
	}

	/**
	 * Indicates that the assignment was removed from the current schedule
	 * entry.
	 * <p>
	 * Assignment work and percentages are computed.
	 * <ul>
	 * <li>Effort Driven - Task work does not change. The removed assignment's
	 * work is divided amongst the remaining assignments (increasing their work)
	 * while keeping the relative amount of work for each assignment constant.
	 * <ul>
	 * <li>Fixed Unit, Fixed Work - Duration is recalculated. Assignent
	 * percentages remain constant.
	 * <li>Fixed Duration - Remaining assignment percentages are recalculated.
	 * Duration remains constant.
	 * </ul>
	 * <li>Non-Effort Driven - Task work changes by the amount of work that the
	 * assignment performed.
	 * <ul>
	 * <li>Fixed Unit - Duration is recalculated.
	 * <li>Fixed Duration - Assignment percentages are recalculated.
	 * </ul>
	 * </ul>
	 * </p>
	 * 
	 * @see #assignmentRemoved(net.project.resource.ScheduleEntryAssignment)
	 * @param calcType
	 *            a <code>TaskCalculation</code> that can be specified if you
	 *            need to specifically override the calculation type of the
	 *            assignment.
	 * @param assignment assignment removed
	 */
	public void assignmentRemoved(ScheduleEntryAssignment assignment, TaskCalculationType calcType) {
		AssignmentRemover assignmentRemover = new AssignmentRemover(assignment, this.scheduleEntry, this.provider);
		assignmentRemover.removeAssignment(calcType);
//		assignmentRemover.calculateWorkComplete();
	}

	public void constraintChanged(Schedule schedule, TaskConstraintType constraintType, Date constraintDate, TimeZone timeZone) {
		ScheduleEntryConstraintModifier modifier = new ScheduleEntryConstraintModifier(schedule, scheduleEntry);
        modifier.constraintChanged(constraintType, constraintDate, timeZone);
	}

	public void startDateChanged(Schedule schedule, Date date, TimeZone timeZone) {
		ScheduleEntryConstraintModifier modifier = new ScheduleEntryConstraintModifier(schedule, scheduleEntry);
		modifier.startDateChanged(date, timeZone);
	}

	public void endDateChanged(Schedule schedule, Date date, TimeZone timeZone) {
		ScheduleEntryConstraintModifier modifier = new ScheduleEntryConstraintModifier(schedule, scheduleEntry);
		modifier.endDateChanged(date, timeZone);
	}
}