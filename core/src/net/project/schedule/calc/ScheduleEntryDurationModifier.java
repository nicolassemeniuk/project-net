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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.project.calendar.workingtime.DaysWorked;
import net.project.calendar.workingtime.IDaysWorked;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.calendar.workingtime.WorkCalculatorHelper;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a helper class that performs changes when modifying
 * task duration.
 * <p>
 * This class performs two functions:
 * <ul>
 * <li>It modifies assignment work (and therefore task work) given a desired duration.
 * An increase in duration requires additional assignment work; a decrease in duration
 * requires less assignment work. Assignment percentages remain constant.<br>
 * Not all assignments may be affected.  The assignments that are modified for increased duration are carefully
 * chosen such that in general a small amount of work is added, but ensuring that any set of
 * assignments whose next availability for additional work coincides, those assignments receive
 * equal quantities of additional work.<br>
 * When decreasing duration, work is first removed from the assignments most affecting the end date of
 * the task.  When more than one assignment affects the end date of the task equally, work is removed
 * equally from all those assignments.<br>
 * For example, a user modifies a task to increase duration from 3 to 4 days.  The
 * assignment work must be updated in such a way that the resulting duration is 4 days.
 * Working time calendars and percentage assigned must be considered.  The goal is to
 * add work to the assignments in exactly the same way as other products.
 * <li>It modifies assignment percentages given a desired duration.  An increase in duration
 * requires less percentage assignment; a decrease in duration requires a greater percentage
 * assignment.  Assignment work (and therefore task work) remains constant.<br>
 * All assignment percentages are modified.
 * </ul>
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
class ScheduleEntryDurationModifier {

    /**
     * The schedule entry being modified.
     */
    private final ScheduleEntry scheduleEntry;

    /**
     * The provider of working time calendars for each assignment resource.
     */
    private final IWorkingTimeCalendarProvider workingTimeCalendarProvider;

    /**
     * Creates a new ScheduleEntryDurationModifier for modifying the duration (and assignments)
     * of the specified schedule entry
     * @param scheduleEntry the schedule entry whose duration and assignments to modify
     * @param workingTimeCalendarProvider the provider of working time calendars for assignment resources
     */
    public ScheduleEntryDurationModifier(ScheduleEntry scheduleEntry, IWorkingTimeCalendarProvider workingTimeCalendarProvider) {
        this.scheduleEntry = scheduleEntry;
        this.workingTimeCalendarProvider = workingTimeCalendarProvider;
    }

    /**
     * Modifies the schedule entry's work and the work of each of its assignments
     * such that the calculated duration matches the specified duration.
     * <p>
     * Work for one or more assignments is updated such that the task duration
     * is equal to the new value. Task Work is updated and task duration and any required
     * changes in dates are updated. <br/>
     * When there are no assignments, work does not change (unless duration is set to zero,
     * in which case work is set to zero).
     * </p>
     * <p>
     * There are multiple solutions to this problem; any number of assignments can be modified
     * and still result in the same duration.  A rough description of the process is:
     * <ul>
     * <li>We attempt to minimize the end time of the task
     * <li>We attempt to minimize the work added, although when we are adding work to assignments,
     * we distribute it evenly to assignments who are available to work on the same day.  In theory
     * you could add work to one assignment to achieve the duration, but that would result in a large
     * discrepency in the assignment work
     * <li>The combination of the above statements means we don't add work to an assignment to "catch up"
     * their end time if other assignments are already working on the days needed to catch up.  In other words,
     * we don't add work to an assignment if it doesn't affect the duration
     * </ul>
     * </p>
     * @param newDuration the new task duration
     * @throws NullPointerException if the specified duration is null
     */
    public void calculateWork(final TimeQuantity newDuration) {

        if (newDuration == null) {
            throw new NullPointerException("newDuration is null");
        }
        
        // Fix of bug-3302
        if(this.scheduleEntry.getWorkTQ().isZero()){
            this.scheduleEntry.setDuration(ScheduleTimeQuantity.convertToUnit(newDuration, newDuration.getUnits(), 3, BigDecimal.ROUND_HALF_UP));
            return;
        }

        if (scheduleEntry.getAssignments().isEmpty()) {
            // Schedule entry as no assignments; special handling

            // We accept the duration as-is
            // If the new duration is zero, we set work to zero (keeping same units)
            if (newDuration.isZero()) {
            	scheduleEntry.setDuration(newDuration);
                scheduleEntry.setWork(new TimeQuantity(0, scheduleEntry.getWorkTQ().getUnits()));
                scheduleEntry.setWorkComplete(new TimeQuantity(0, scheduleEntry.getWorkCompleteTQ().getUnits()));
                scheduleEntry.calculateEndDate(this.workingTimeCalendarProvider);
            }else{
	            // To calculate work when task has no assignments.
	            TimeQuantity convertedDuration = ScheduleTimeQuantity.convertToHour(newDuration);
	            TimeQuantity durationDelta = convertedDuration.subtract(ScheduleTimeQuantity.convertToHour(this.scheduleEntry.getDurationTQ()));
	            ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
	            assignment.setPercentAssigned(100);
	            assignment.setWork(this.scheduleEntry.getWorkTQ());
	            assignment.setStartTime(this.scheduleEntry.getStartTime());
	            assignment.setEndTime(this.scheduleEntry.getEndTime());
	            scheduleEntry.setDuration(newDuration);
	            scheduleEntry.calculateEndDate(this.workingTimeCalendarProvider);
	            this.scheduleEntry.setWork(setWork(assignment,durationDelta));
            }
        } else {

            // Converted the duration to a manageable unit; this takes care of converting
            // month / week / day to something discrete (like hours)
            TimeQuantity convertedDuration = ScheduleTimeQuantity.convertToHour(newDuration);

            // Calculate difference between new duration and current duration
            // This delta is the amount by which duration must change
            TimeQuantity durationDelta = convertedDuration.subtract(ScheduleTimeQuantity.convertToHour(this.scheduleEntry.getDurationTQ()));

            if (durationDelta.getAmount().signum() < 0) {
                // New duration is less than current duration; we must decrease work
                TimeQuantity requiredDecrease = durationDelta.negate();

                // No other way than to traverse the assignments eliminating work
                // as efficiently as possible
                adjustDuration(requiredDecrease, scheduleEntry.getAssignments());

            } else {
                // New duration is greater than current duration; we must increase work

                // Locate assignments where no-one else performs any work later than
                // their next working time.  Typically these assignments all have
                // the same next working time (the latest of all assignments) but it may also
                // include assignments with earlier next working times due to differences
                // in working time.
                // The key is that no-one currently performs any work at any time
                // on or after the earliest next working time
                Collection assignmentsToModify = getAssignmentsToIncreaseWork();

                // First we calculate the "most efficient" duration, that is the duration that means
                // the required increase in duration is completed in the shortest amount of time
                // possible.  This duration is only accurate if all assignment working time calendars
                // over the required period are identical.
                // If it turns out this duration isn't accurate, we decrease it until we hit the right amount
                IDaysWorked preliminaryDuration = calculatePreliminaryIncreasedDuration(assignmentsToModify, durationDelta);

                if (!new TimeQuantity(preliminaryDuration.getTotalDays(), TimeQuantityUnit.DAY).equals(ScheduleTimeQuantity.convertToUnit(convertedDuration, TimeQuantityUnit.DAY, 3, BigDecimal.ROUND_HALF_UP))) {
                    // Our duration is off due to working time
                    // Adjust the duration downards
                    adjustDuration(convertedDuration, preliminaryDuration, assignmentsToModify);
                }

            }

            // Now convert calculated duration to the unit specified by the new duration
            this.scheduleEntry.setDuration(ScheduleTimeQuantity.convertToUnit(this.scheduleEntry.getDurationTQ(), newDuration.getUnits(), 3, BigDecimal.ROUND_HALF_UP));
        }

    }

    /**
     * Modifies each of the schedule entry's assignment percentages
     * such that the calculate duration matches the specified duration.
     * <p>
     * Schedule Entry work is not modified, assignment work is not modified.
     * This method is actually very similar to calculating work; the same assignments
     * are modified.  However, rather than modifying work, percentage is calculated as follows:
     * <code>
     * ((current assignment work * current assignment percentage) / work required to satisfy duration)
     * </code>
     * That is, the ratio of current work amount to potential work amount.
     * </p>
     * <p>
     * <b>Special Cases</b>
     * <ul>
     * <li>Changing duration to zero:  Work is set to zero, rather
     * than percentage.  It doesn't make sense to say someone works 0% on an 8 hour task; if the task
     * is to take 0 days, then it must have 0 work.
     * <li>When task work is zero, changing duration will calculate task work based on current
     * assignment percentages.
     * </ul>
     * </p>
     *
     * @param newDuration the new task duration
     * @throws NullPointerException if the specified duration is null
     */
    public void calculatePercentage(final TimeQuantity newDuration) {

        if (newDuration == null) {
            throw new NullPointerException("newDuration is null");
        }

        if (this.scheduleEntry.getAssignments().isEmpty()) {
            // No assignments; special handling

            // We accept the duration as-is
            this.scheduleEntry.setDuration(newDuration);
            this.scheduleEntry.calculateEndDate(this.workingTimeCalendarProvider);

            // If the new duration is zero, we set work to zero (keeping same units)
            if (newDuration.isZero()) {
                scheduleEntry.setWork(new TimeQuantity(0, scheduleEntry.getWorkTQ().getUnits()));
                scheduleEntry.setWorkComplete(new TimeQuantity(0, scheduleEntry.getWorkCompleteTQ().getUnits()));
            }

        } else if (this.scheduleEntry.getWorkTQ().isZero()) {
            // Special case where there is zero work
            // In this case, we don't calculate assignment percentage, rather we calculate
            // work based on the new duration
            calculateWork(newDuration);

        } else {
            // We calculate percentage by first calculating work capability then
            // ratio of current work to capability

            Collection assignments = this.scheduleEntry.getAssignments();

            // Save current work because it will get updated below
            List currentAssignmentWork = new ArrayList(assignments.size());
            for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
                currentAssignmentWork.add(nextAssignment.getWork());
            }

            // Save task work for same reason
            TimeQuantity taskWork = this.scheduleEntry.getWorkTQ();

            // Calculate new assignment work;
            // this modifies actual assignment work and task work, that is why we saved those values above
            // This will also calculate duration
            // After doing so, the assignment end times will be perfectly set, regardless of any loss of
            // precision when calculating percentage
            calculateWork(newDuration);

            // Special situation:  When duration is set to zero, work is _always_ changed to zero
            // Percentage is NOT changed to 0%
            if (newDuration.isZero()) {
                // We're done
                // The work calculation already changed work to zero

            } else {

                // Calculate percentage and reset work back to the original value
                // Percentage is rounded up to 3 d.p.
                int index = 0;
                for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
                    ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
                    TimeQuantity oldWork = ((TimeQuantity) currentAssignmentWork.get(index++));
                    TimeQuantity newWork = nextAssignment.getWork();
                    BigDecimal newPercentAssignedDecimal;
                    if (newWork.isZero()) {
                        newPercentAssignedDecimal = new BigDecimal("0.00");
                    } else {
                        newPercentAssignedDecimal = oldWork.multiply(nextAssignment.getPercentAssignedDecimal()).divide(newWork, 3, BigDecimal.ROUND_HALF_UP);
                    }
                    WorkHelper.setConvertedWork(nextAssignment, oldWork);
                    nextAssignment.setPercentAssignedDecimal(newPercentAssignedDecimal);
                }
                this.scheduleEntry.setWork(taskWork);

                // Note: At this point the percentage is now set.  However, it is rounded to two d.p.
                // so it may be something like 0.67 (or 67%).  If this percentage were subsequently applied
                // to the assignment work to calculate duration, then the duration and/or end times might change
                // because that percentage is fairly imprecise.
                // However, this is exactly what MSP does:  percentage is calculated and all dates remain "exact".
                // Only when the user modifies the percentage does the duration get recalculated to its "imprecise" value

                // In other words, DON'T recalculate duration here (it doesn't need it anyway)

            }

        }

    }

    /**
     * Determines the assignments whose work to increase when increasing duration.
     * <p>
     * These are defined as the assignments that satisfy the requirement that
     * no other assignments currently perform any work on or after their next working time.
     * </p>
     * <p>
     * For example, consider two assignments who perform the following amounts of
     * work on the specified days (where X means non working day):
     * <pre>
     *     Mon Tue Wed Thu Fri
     * A1   8   X   8
     * A2   8   8
     * </pre>
     * In this case, only A1 is selected. No-one works after A1's next working time (Thursday @ 8:00 AM)
     * However, someone (A1) works on or after A2's next working time (Wednesday @ 8:00 AM).
     * </p>
     * <p>
     * Example 2:
     * <pre>
     *     Mon Tue Wed Thu Fri
     * A1   8   X   8
     * A2   8   8   X
     * </pre>
     * In this case, both assignments are selected because no-one works after A1's
     * next working time (Thursday @ 8:00 AM) and no-one works after A2's next working time (Thursday @ 8:00 AM).
     * </p>
     * <p>
     * Example 3:
     * <pre>
     *     Mon Tue Wed Thu Fri
     * A1   8   X   4   X
     * A2   8   X   4   X
     * A3   8   4   X
     * </pre>
     * A1 is selected because no-one works after A1's next working time (Wednesday @ 1:00 PM). <br>
     * A2 is selected because no-one works after A2's next working time (Wednesday @ 1:00 PM). <br>
     * A3 is not selected because someone (A1 and A2) works after A3's next working time (Tuesday @ 1:00 PM). <br>
     * </p>
     * <p>
     * If the ScheduleEntry has no assignments, a single 100% assignment is included.
     * </p>
     * @return the assignments to modify based on the described algorithm
     */
    private Collection getAssignmentsToIncreaseWork() {
        Collection assignmentsToModifyWork = new LinkedList();

        for (Iterator iterator = this.scheduleEntry.getAssignments().iterator(); iterator.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();

            try {
                // Locate the date that the assignment would work following
                // their end date
                Date nextWorkingTimeDate = nextAssignment.getWorkingTimeCalendar(this.workingTimeCalendarProvider).getStartOfNextWorkingTime(nextAssignment.getEndTime());

                if (!isLaterWorkPerformed(nextWorkingTimeDate, this.scheduleEntry.getAssignments())) {
                    assignmentsToModifyWork.add(nextAssignment);
                } else {
                    // Silently ignore assignment; their work won't be modified
                }

            } catch (NoWorkingTimeException e) {
                // Ignore this assignment; they have an invalid working time calendar
            }

        }

        return assignmentsToModifyWork;
    }

    /**
     * Indicates whether any work is performed by any assignments on or after the
     * specified date.
     * @param date the date to check for work performed on or after
     * @param assignments the assignments who are doing the work
     * @return true if at least one assignments performs some work (1 second or more)
     * on or after the specified date; false if no assignments perform any work on or after the date
     */
    private boolean isLaterWorkPerformed(Date date, Collection assignments) {

        boolean isLaterWorkedPerformed = false;

        for (Iterator iterator = assignments.iterator(); iterator.hasNext() && !isLaterWorkedPerformed;) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();

            TimeQuantity workPerformedAfterDate = nextAssignment.calculateWorkPerformed(this.workingTimeCalendarProvider, date, nextAssignment.getEndTime());
            if (!workPerformedAfterDate.isZero()) {
                // Assignment performs some work
                // We're done
                isLaterWorkedPerformed = true;
            }
        }

        return isLaterWorkedPerformed;
    }

    /**
     * Updates the schedule entry duration by adding the specified amount of work to each
     * assignment and calculating the duration from that.
     * <p>
     * Updates schedule entry work, start date, end date, duration and assignment work,
     * start date and end date.
     * </p>
     * <p>
     * This can be regarded as the "most efficient" duration; that is, the shortest possible duration,
     * which can only occur when all assignments have identical working time calendars (at least identical
     * considering the time period following their current completion). <br>
     * If calendars are not identical, then this calculated duration will be to great; that is, we added
     * too much work to one or more assignemnts.
     * </p>
     * @param assignments the assignments whose work to modify
     * @param work the work to add to each assignment
     * @return the preliminary duration
     */
    private IDaysWorked calculatePreliminaryIncreasedDuration(Collection assignments, TimeQuantity work) {

        // Iterate over those assignments, allocating them an additional amount of work
        for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();

            // Multiply work by % assigned to get the actual amount of work
            TimeQuantity additionalWork = work.multiply(nextAssignment.getPercentAssignedDecimal());

            // Add that work to the current work
            nextAssignment.setWork(nextAssignment.getWork().add(additionalWork));
        }

        recalculateTaskWorkAndDuration();
        return this.scheduleEntry.getDaysWorked(this.workingTimeCalendarProvider);
    }
    
    /**
     * To calculate work when entry has no assignments.
     * @param assignments
     * @param work
     * @return
     */
    public TimeQuantity setWork(ScheduleEntryAssignment assignments, TimeQuantity work){
    	TimeQuantity additionalWork = work.multiply(assignments.getPercentAssignedDecimal());
        // Add that work to the current work
    	return assignments.getWork().add(additionalWork);
    }

    /**
     * Adjusts the task's duration until it is computed correctly based on the assignment
     * work.
     * @param requiredDuration the new duration that is desired
     * @param currentDaysWorked the calculated days work that need to be adjusted
     * @param assignments the assignments requiring adjustment to get the duration to match the new duration
     * @throws IllegalArgumentException if the specified current duration is less then the required duration;
     * this method can only adjust duration downwards
     */
    private void adjustDuration(final TimeQuantity requiredDuration, final IDaysWorked currentDaysWorked, Collection assignments) {

        TimeQuantity requiredDurationWorkingHours = ScheduleTimeQuantity.convertToHour(requiredDuration);
        if (new TimeQuantity(currentDaysWorked.getTotalDays(), TimeQuantityUnit.DAY).compareTo(requiredDurationWorkingHours) < 0) {
            throw new IllegalArgumentException("Duration to adjust (" + currentDaysWorked.getTotalDays() + " days) must be greater than required duration (" + requiredDuration + ")");
        }

        // Adjust by the specified amount
        TimeQuantity remainingAdjustment = new TimeQuantity(currentDaysWorked.getTotalMinutes(), TimeQuantityUnit.MINUTE).subtract(requiredDurationWorkingHours);
        adjustDuration(remainingAdjustment, assignments);
    }

    /**
     * Adjusts the duration of the schedule entry by modifying the work of the specified assignments
     * until the duration has been reduced by the specified amount.
     * <p>
     * The schedule entry is recalculated such that its duration and work match the assignments work
     * </p>
     * @param requiredDurationDecrease the decrease in duration required
     * @param assignments the assignments whose work to modify
     */
    private void adjustDuration(TimeQuantity requiredDurationDecrease, Collection assignments) {

        // Figure the time at which to start dropping work
        // This is initially the end date of the schedule entry
        // We actually set the date to midnight of the next day
        // meaning its really right at the end of the current day
        Calendar current = new GregorianCalendar(this.workingTimeCalendarProvider.getDefaultTimeZone());
        current.setTime(this.scheduleEntry.getEndTime());
        current.add(Calendar.DAY_OF_YEAR, 1);
        DateUtils.zeroTime(current);

        // Start by adjusting duration day by day across assignments
        requiredDurationDecrease = adjustByDays(requiredDurationDecrease, current, assignments);

        if (requiredDurationDecrease.getAmount().signum() > 0) {
            // Adjust duration hour by hour across assignments
            requiredDurationDecrease = adjustByHours(requiredDurationDecrease, current, assignments);

            if (requiredDurationDecrease.getAmount().signum() > 0) {
                // Adjust duration minute by minute across assignments
                adjustByMinutes(requiredDurationDecrease, current, assignments);
            }
        }

        recalculateTaskWorkAndDuration();
    }

    /**
     * Adjusts the schedule entry's duration by subtracting work from assignments until the duration has been
     * shorteneed by the specified amount.
     * @param adjustment the amount to shorten the duration by
     * @param current the end time at which to start eliminating work; this is updated and is set to
     * the next time to start eliminating work at; assumed to be set to midnight on the following day
     * @param assignments the assignments to eliminate work from
     * @return the remaining adjustment; this will be zero if the amount of adjustment to be done
     * could be accomplished by subtracting whole days of work.  It will be greater than zero if
     * the day (who ends at "current") accounts for more duration than the remaining adjustment.
     * That is, only a portion of the day's work must be eliminated.
     */
    private TimeQuantity adjustByDays(TimeQuantity adjustment, Calendar current, Collection assignments) {

        while (adjustment.getAmount().signum() > 0) {
            // Figure out the actual date that we're interested in;
            // The current calendar is always set to midnight on the next day
            // So we simply subtact a day; we're not interested in the time
            Calendar actualDay = (Calendar) current.clone();
            actualDay.add(Calendar.DAY_OF_YEAR, -1);
            Date actualDate = actualDay.getTime();

            IDaysWorked daysWorked = new DaysWorked();

            // Collect the work added to the assignments so we can
            // potentially delete it later
            ArrayList assignmentWorkPerformed = new ArrayList();
            boolean isAssignmentWorkRemaining = false;

            // Calculate the duration across all assignments on the current date
            for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
                IWorkingTimeCalendar assignmentCalendar = nextAssignment.getWorkingTimeCalendar(this.workingTimeCalendarProvider);

                // An infinite loop can occur if for some reason the current time is decremented past the start
                // date of all assignments (which would only occur due to erroneous assignment dates)
                // In that case, we will error out
                isAssignmentWorkRemaining = isAssignmentWorkRemaining | nextAssignment.getStartTime().before(current.getTime());

                // Get start of working day on current date
                // Only if the assignment works on that date
                if (assignmentCalendar.isWorkingDay(actualDate)) {
                    Date startDate = assignmentCalendar.getStartOfWorkingDay(actualDate);
                    // Determine duration that the assignment works on day
                    // It might be zero if they don't work on that day
                    // And add it to our running total
                    TimeQuantity workPerformed = nextAssignment.calculateWorkPerformed(this.workingTimeCalendarProvider, startDate, current.getTime());
                    assignmentWorkPerformed.add(workPerformed);
                    daysWorked.add(nextAssignment.getDaysWorked(startDate, workPerformed, this.workingTimeCalendarProvider));

                } else {
                    // Assignment doesn't work that day
                    assignmentWorkPerformed.add(new TimeQuantity(0, TimeQuantityUnit.HOUR));
                }

            }

            if (!isAssignmentWorkRemaining) {
                // We've identified the condition whereby no assignment performs any work prior to the current
                // date; there is no way to decrement assignment work
                // We're done
                adjustment = new TimeQuantity(0, TimeQuantityUnit.HOUR);

            } else {

                // If the duration on that day is less than the amount we need to adjust by,
                // we can simply eliminate the work that each assignment did on the day
                TimeQuantity durationMinutes = new TimeQuantity(daysWorked.getTotalMinutes(), TimeQuantityUnit.MINUTE);
                if (durationMinutes.compareTo(adjustment) <= 0) {
                    adjustment = adjustment.subtract(durationMinutes);

                    // Now we must remove the work that was performed on this day from the assignments
                    subtractWork(assignments, assignmentWorkPerformed);

                    // We'll begin the next iteration on the previous day
                    current.add(Calendar.DAY_OF_MONTH, -1);

                } else {
                    // More minutes in day; we need to adjust hour-by-hour
                    break;
                }

            }

        }

        return adjustment;
    }

    /**
     * Adjusts the schedule entry's duration by subtracting work from assignments until the duration has been
     * shorteneed by the specified amount.
     * @param adjustment the amount to shorten the duration by
     * @param current the end time at which to start eliminating work; this is updated and is set to
     * the next time to start eliminating work at; assumed to be set to the ending hour of the first hour
     * to be considered.
     * @param assignments the assignments to eliminate work from
     * @return the remaining adjustment; this will be zero if the amount of adjustment to be done
     * could be accomplished by subtracting whole hours of work.  It will be greater than zero if
     * the hour (who ends at "current") accounts for more duration than the remaining adjustment.
     * That is, only a portion of the hour's work must be eliminated.
     */
    private TimeQuantity adjustByHours(TimeQuantity adjustment, Calendar current, Collection assignments) {

        while (adjustment.getAmount().signum() > 0) {

            IDaysWorked daysWorked = new DaysWorked();

            // Collect the work added to the assignments so we can
            // potentially delete it later
            List assignmentWorkPerformed = new ArrayList();
            boolean isAssignmentWorkRemaining = false;

            // Calculate the duration across all assignments on the current hour
            for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
                IWorkingTimeCalendar assignmentCalendar = nextAssignment.getWorkingTimeCalendar(this.workingTimeCalendarProvider);

                // An infinite loop can occur if for some reason the current time is decremented past the start
                // date of all assignments (which would only occur due to erroneous assignment dates)
                // In that case, we will error out
                isAssignmentWorkRemaining = isAssignmentWorkRemaining | nextAssignment.getStartTime().before(current.getTime());

                if (assignmentCalendar.isWorkingDay(current.getTime())) {
                    Calendar startCal = (Calendar) current.clone();
                    startCal.add(Calendar.HOUR_OF_DAY, -1);

                    TimeQuantity workPerformed = nextAssignment.calculateWorkPerformed(this.workingTimeCalendarProvider, startCal.getTime(), current.getTime());
                    assignmentWorkPerformed.add(workPerformed);

                    daysWorked.add(nextAssignment.getDaysWorked(startCal.getTime(), workPerformed, this.workingTimeCalendarProvider));

                } else {
                    // Assignment doesn't work that day
                    assignmentWorkPerformed.add(new TimeQuantity(0, TimeQuantityUnit.HOUR));
                }

            }

            if (!isAssignmentWorkRemaining) {
                // We've identified the condition whereby no assignment performs any work prior to the current
                // date; there is no way to decrement assignment work
                adjustment = new TimeQuantity(0, TimeQuantityUnit.HOUR);

            } else {

                TimeQuantity durationMinutes = new TimeQuantity(daysWorked.getTotalMinutes(), TimeQuantityUnit.MINUTE);
                if (durationMinutes.compareTo(adjustment) <= 0) {
                    // The duration on this hour is less or equal to the adjustment we need
                    // Subtract it from the remaining adjustment
                    adjustment = adjustment.subtract(durationMinutes);

                    // Now we must remove the work that was performed on this hour from the assignments
                    subtractWork(assignments, assignmentWorkPerformed);

                    // Subtract 1 hour
                    current.add(Calendar.HOUR_OF_DAY, -1);

                } else {
                    // Duration on hour more than we need to subtract
                    break;
                }

            }

        }

        return adjustment;
    }

    /**
     * Adjusts the schedule entry's duration by subtracting work from assignments until the duration has been
     * shortened by the specified amount.
     * @param adjustment the amount to shorten the duration by
     * @param current the end time at which to start eliminating work; this is updated and is set to
     * the next time to start eliminating work at; assumed to be set to the ending minute of the first minute
     * to be considered.
     * @param assignments the assignments to eliminate work from
     */
    private void adjustByMinutes(TimeQuantity adjustment, Calendar current, Collection assignments) {

        while (adjustment.getAmount().signum() > 0) {

            IDaysWorked daysWorked = new DaysWorked();

            // Collect the work added to the assignments so we can
            // potentially delete it later
            List assignmentWorkPerformed = new ArrayList();
            boolean isAssignmentWorkRemaining = false;

            // Calculate the duration across all assignments on the current minute
            for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
                IWorkingTimeCalendar assignmentCalendar = nextAssignment.getWorkingTimeCalendar(this.workingTimeCalendarProvider);

                // An infinite loop can occur if for some reason the current time is decremented past the start
                // date of all assignments (which would only occur due to erroneous assignment dates)
                // In that case, we will error out
                isAssignmentWorkRemaining = isAssignmentWorkRemaining | nextAssignment.getStartTime().before(current.getTime());

                if (assignmentCalendar.isWorkingDay(current.getTime())) {
                    Calendar startCal = (Calendar) current.clone();
                    startCal.add(Calendar.MINUTE, -1);

                    // Calculate how much work (if any) the assignment did during that minute
                    TimeQuantity workPerformed = nextAssignment.calculateWorkPerformed(this.workingTimeCalendarProvider, startCal.getTime(), current.getTime());
                    assignmentWorkPerformed.add(workPerformed);

                    // Update the days worked (duration) for the work done beginning at the start of the minute
                    daysWorked.add(nextAssignment.getDaysWorked(startCal.getTime(), workPerformed, this.workingTimeCalendarProvider));

                } else {
                    // Assignment doesn't work that day
                    assignmentWorkPerformed.add(new TimeQuantity(0, TimeQuantityUnit.HOUR));
                }

            }

            if (!isAssignmentWorkRemaining) {
                // We've identified the condition whereby no assignment performs any work prior to the current
                // date; there is no way to decrement assignment work
                adjustment = new TimeQuantity(0, TimeQuantityUnit.HOUR);

            } else {

                // Subtract the duration minutes from the adjustment
                TimeQuantity durationMinutes = new TimeQuantity(daysWorked.getTotalMinutes(), TimeQuantityUnit.MINUTE);
                adjustment = adjustment.subtract(durationMinutes);

                // Now we must remove the work that was performed on this minute from the assignments
                subtractWork(assignments, assignmentWorkPerformed);

                // Subtract 1 minute
                current.add(Calendar.MINUTE, -1);

            }

        }

    }

    /**
     * Subtracts work from assignment work.
     * @param assignments the assignments whose work to modify; each assignments is modified
     * @param workToSubtract the work to subtract; each element in this list
     * corresponds to an element in the assignment (in the same order)
     * @throws IllegalArgumentException if the sizes of the two collections are not equal
     */
    private static void subtractWork(final Collection assignments, final List workToSubtract) {

        if (assignments.size() != workToSubtract.size()) {
            throw new IllegalArgumentException("The number of assignments (" + assignments.size() + ") must equal the number of elements of work (" + workToSubtract + ").");
        }

        // Iterate over each assignment, subtracting from its work the amount of work
        // in the corresponding index position of the workToSubtract list
        int index = 0;
        for (Iterator iterator = assignments.iterator(); iterator.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
            TimeQuantity workPerformed = (TimeQuantity) workToSubtract.get(index++);
            nextAssignment.setWork(nextAssignment.getWork().subtract(workPerformed));
        }

    }

    /**
     * Recalculates schedule entry work and duration based on current assignment work.
     * <p/>
     * Modifies the schedule entry duration, work and end date also ensures that assignment work
     * is at the correct precision.
     */
    private void recalculateTaskWorkAndDuration() {

        if (this.scheduleEntry.getAssignments().isEmpty()) {
            throw new IllegalStateException("No duration calculation permitted without assignments");
        } else {
            // Recalculate task work based on all assignments
            TimeQuantity taskWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
            TimeQuantityUnit originalUnit = this.scheduleEntry.getWorkTQ().getUnits();
            for (Iterator iterator = this.scheduleEntry.getAssignments().iterator(); iterator.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) iterator.next();
                // First ensure the assignment work is scaled properly
                WorkHelper.setConvertedWork(nextAssignment, nextAssignment.getWork());
                taskWork = taskWork.add(nextAssignment.getWork());
            }
            this.scheduleEntry.setWork(ScheduleTimeQuantity.convertToUnit(taskWork, originalUnit, 3, BigDecimal.ROUND_HALF_UP));

            // Reclaculate assignment dates
            this.scheduleEntry.calculateEndDate(this.workingTimeCalendarProvider);

            // Recalculate task duration
            this.scheduleEntry.calculateDuration(this.workingTimeCalendarProvider);
        }

    }

}