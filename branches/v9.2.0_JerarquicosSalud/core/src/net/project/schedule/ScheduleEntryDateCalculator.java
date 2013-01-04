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

 package net.project.schedule;

import java.util.Date;
import java.util.Iterator;

import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.calc.IDateCalculator;
import net.project.util.TimeQuantity;

/**
 * Provides a date calculator based on a schedule entry (task).
 * <p>
 * The resultant dates are based on the task assignments and the resource working time
 * calendars.
 * If the task has no assignments, then a single 100% assignment performing all the task
 * work is considered using the schedule default working time calendar.
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
public class ScheduleEntryDateCalculator implements IDateCalculator {

    //
    // Static Members
    //

    /**
     * Returns the later value of the specified dates.
     * If either date is null, the other is returned.
     * @param date1 the first date to compare
     * @param date2 the second date to compare
     * @return the later date or null if both dates are null
     */
    private static Date laterOf(Date date1, Date date2) {

        Date latestDate;

        if (date1 == null && date2 == null) {
            latestDate = null;

        } else if (date1 == null) {
            latestDate = date2;

        } else if (date2 == null) {
            latestDate = date1;

        } else {
            latestDate = (date1.after(date2) ? date1 : date2);
        }

        return latestDate;
    }

    /**
     * Returns the earlier value of the specified dates.
     * If either date is null, the other is returned.
     * @param date1 the first date to compare
     * @param date2 the second date to compare
     * @return the earlier date or null if both dates are null
     */
    private static Date earlierOf(Date date1, Date date2) {

        Date earliestDate;

        if (date1 == null && date2 == null) {
            earliestDate = null;

        } else if (date1 == null) {
            earliestDate = date2;

        } else if (date2 == null) {
            earliestDate = date1;

        } else {
            earliestDate = (date1.before(date2) ? date1 : date2);
        }

        return earliestDate;
    }

    /**
     * Indicates whether two dates are different, handling the case where
     * either or both dates are null.
     * Two dates are different if one date is null and the other is non-null
     * or both dates are non-null and they are not equal.  If both dates are
     * null, they are considered equal.
     * @param currentDate the current date to compare
     * @param newDate the new date to compare
     * @return true if the dates are different; false otherwise
     */
    private static boolean isDateDifferent(Date currentDate, Date newDate) {
        final boolean isDifferent;

        if (currentDate == null && newDate == null) {
            // Null dates are not different
            isDifferent = false;

        } else if (currentDate == null || newDate == null) {
            // Either null date means they are different
            isDifferent = true;

        } else if (currentDate.equals(newDate)) {
            // Equal dates are not different
            isDifferent = false;

        } else {
            // Unequal dates are different
            isDifferent = true;
        }

        return isDifferent;
    }

    //
    // Instance Members
    //

    private final ScheduleEntry scheduleEntry;
    private final IWorkingTimeCalendarProvider provider;

    private boolean isAssignmentModified = false;

    /**
     * Creates a date calculator based on the specified task, getting workingtime calendars
     * from the specified provider.
     * @param scheduleEntry the task who's assignments or task work to use to calculate dates
     * @param provider the provider of calendars
     */
    public ScheduleEntryDateCalculator(ScheduleEntry scheduleEntry, IWorkingTimeCalendarProvider provider) {
        this.scheduleEntry = scheduleEntry;
        this.provider = provider;
    }

    /**
     * Calculates the date on which the task could be completed.
     * @param startDate the date from which to calculate the finish date
     * @return the date on which work could be completed; further in the future than start date
     */
    public Date calculateFinishDate(Date startDate) {
        return calculateDate(startDate, true);
    }

    /**
     * Calculates the date on which the task could be started.
     * @param finishDate the date from which to calculate the start date
     * @return the date on which work could be started; further in the past than finish date
     */
    public Date calculateStartDate(Date finishDate) {
        return calculateDate(finishDate, false);
    }

    /**
     * Calculates the resultant date in the past/future by
     * calculating the farthest date that any assignment can be completed on.
     * @param date the date from which to base the result
     * @param isForward true if the work should be added; false otherwise
     * @return the resultant date
     */
    private Date calculateDate(Date date, boolean isForward) {

        Date farthestDate = null;

        if (this.scheduleEntry.getAssignments().isEmpty()) {
            // Task has no assignments; date is based on duration
            TimeQuantity assumedWork = ScheduleTimeQuantity.convertToHour(this.scheduleEntry.getDurationTQ());

            // We create a dummy assignment based on an amount of work equal to the duration
            ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
            assignment.setPercentAssigned(100);
            assignment.setWork(assumedWork);
            assignment.setStartTime(this.scheduleEntry.getStartTime());
            assignment.setEndTime(this.scheduleEntry.getEndTime());

            IDateCalculator dateCalc = assignment.getDateCalculator(this.provider);
            if (isForward) {
                farthestDate = dateCalc.calculateFinishDate(date);
            } else {
                farthestDate = dateCalc.calculateStartDate(date);
            }

        } else {
            // Task has assignments; date is latest / earliest of assignment dates
            for (Iterator it = this.scheduleEntry.getAssignments().iterator(); it.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();

                IDateCalculator dateCalc = nextAssignment.getDateCalculator(this.provider);

                if (isForward) {
                    farthestDate = laterOf(farthestDate, dateCalc.calculateFinishDate(date));
                } else {
                    farthestDate = earlierOf(farthestDate, dateCalc.calculateStartDate(date));
                }
            }

        }

        return farthestDate;
    }

    /**
     * Updates the assignments that this working time calendar is based on
     * based on the specified start date and work.
     * Each assignment's start and end date may be different depending on
     * their working time calendars.
     * @param startDate the start date from which to begin scheduling work
     * @return the resulting date
     * @throws IllegalArgumentException if work amount is negative
     */
    public Date addWorkAndupdateAssignmentDates(final Date startDate) {

        this.isAssignmentModified = false;
        Date farthestDate = null;

        if (this.scheduleEntry.getAssignments().isEmpty()) {
            // No dates to update, except we must still return a date
            farthestDate = calculateFinishDate(startDate);

        } else {
            // Calculate farthest date while updating assignments
            for (Iterator it = this.scheduleEntry.getAssignments().iterator(); it.hasNext();) {
                ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
                IWorkingTimeCalendar nextWorkingTimeCalendar = nextAssignment.getWorkingTimeCalendar(this.provider);
                IDateCalculator dateCalc = nextAssignment.getDateCalculator(this.provider);

                // Calculate when the assignment is complete
                Date assignmentStartDate = (nextAssignment.getActualStart() == null ? startDate : nextAssignment.getActualStart());
                Date assignmentEndDate = dateCalc.calculateFinishDate(assignmentStartDate);

                // Keep the farthest date so far
                farthestDate = laterOf(farthestDate, assignmentEndDate);

                try {
                    // Ensure the start date is at the start of working time
                    // We already know that the end date is
                    assignmentStartDate = nextWorkingTimeCalendar.ensureWorkingTimeStart(assignmentStartDate);

                } catch (NoWorkingTimeException e) {
                    // There was no next working time start
                    // We'll leave startDate alone
                }

                // Update the assignment only if either start or end date
                // is different.  The default assignment isn't really stored, so
                // it shouldn't trigger the modification flag.
                if (!(nextAssignment.getPersonID() == null)) {
                    if (isDateDifferent(nextAssignment.getStartTime(), assignmentStartDate) || isDateDifferent(nextAssignment.getEndTime(), assignmentEndDate)) {
                        nextAssignment.setStartTime(assignmentStartDate);
                        nextAssignment.setEndTime(assignmentEndDate);
                        //No need to find earliar and later time, after task time recalculation it will be recalculate.
						scheduleEntry.setStartTimeD(assignmentStartDate);
                        scheduleEntry.setEndTimeD(assignmentEndDate);
                        this.isAssignmentModified = true;
                    }
                }
            }

        }


        return farthestDate;
    }

    public boolean isAssignmentModified() {
        return this.isAssignmentModified;
    }

}