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

import java.util.Date;
import java.util.TimeZone;

import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskConstraintType;
import net.project.schedule.TaskEndpointCalculation;

/**
 * Provides a helper class for updating a schedule and schedule entries
 * when a constrinat is modified, either by directly setting the constraint
 * or indirectly by changing start or end date.
 * <p/>
 * When a start date or end date is changed, a constraint is set where the
 * exact constraint type depends on whether a start or end date was changed;
 * a task end point calculation occurs to recalculate the schedule and to calculate
 * the modified schedule entry's the start and end date.
 * <p/>
 * When a constraint is specified, the constraint and constraint date are
 * set as specified;
 * a task end point calculation occurs to recalculate the schedule and to calculate
 * the modified schedule entry's the start and end date.
 * <p/>
 * <b>Note1:</b>No storage of the schedule or its tasks occurs. <br/>
 * <b>Note2:</b> Any existing constraint is always replaced
 * @author Tim Morrow
 * @since Version 7.7.0
 */
class ScheduleEntryConstraintModifier {

    private final Schedule schedule;
    private final ScheduleEntry scheduleEntry;
    private final TaskEndpointCalculation tec;
    
    /**
     * Creates a new Constraint Modifier Helper for modifying the constraint of the specified scheduleEntry
     * which already belongs to the specified schedule.
     * @param schedule the loaded schedule owning the scheduleEntry which will be recalculated
     * @param scheduleEntry the schedule entry whose constraint is being modified; this class
     * will update the schedule entry so the specified one should not yet have been modified
     */
    public ScheduleEntryConstraintModifier(Schedule schedule, ScheduleEntry scheduleEntry) {

        if (schedule == null || scheduleEntry == null) {
            throw new NullPointerException("Missing required parameter");
        }

        this.schedule = schedule;
        this.scheduleEntry = scheduleEntry;
        this.tec = new TaskEndpointCalculation();
    }

    /**
     * Updates the schedule entry based on the start date changing and recalculates the schedule.
     * <p/>
     * A <i>Start No Earlier Than</i> constraint is added with the constraint date set to the
     * specified start date. Any existing constraint is overwritten. <br/>
     * <b>Note:</b> The time is always updated to be <code>8:00 AM</code> on the specified date.
     * <p/>
     * The schedule is recalculated.  However, the resulting start date may
     * not actually be the same as the one specified.  For example, a task dependency
     * may be causing the start date to be later than the entered start date.
     * In this case, while the constraint and constraint date are set, the task start date
     * won't have changed.
     * @param startDate the changed start date for the current schedule entry
     * @param timeZone the current user's time zone, used to update the date with a specific time of day
     * @throws NullPointerException if startDate or timeZone is null
     */
    public void startDateChanged(Date startDate, TimeZone timeZone) {

        if (startDate == null || timeZone == null) {
            throw new NullPointerException("Missing required parameter startDate or timeZone");
        }
        //get the next working time to update to constraint date fix for bfd-2700 (sachin)
        IWorkingTimeCalendar cal = scheduleEntry.getResourceCalendar(schedule.getWorkingTimeCalendarProvider());
        try {
            startDate = cal.ensureWorkingTimeStart(startDate);
        } catch (NoWorkingTimeException e) {
        }
        this.scheduleEntry.setConstraintType(TaskConstraintType.START_NO_EARLIER_THAN);
        this.scheduleEntry.setConstraintDate(ScheduleEntry.updateConstraintTime(this.scheduleEntry.getConstraintType(), startDate, timeZone));

        recalculateSchedule();
    }

    /**
     * Updates the schedule entry based on the end date changing and recalculates the schedule.
     * <p/>
     * A <i>Finish No Earlier Than</i> constraint is added with the constraint date set to the
     * specified end date. Any existing constraint is overwritten. <br/>
     * <b>Note:</b> The time is always updated to be <code>5:00 PM</code> on the specified date.
     * <p/>
     * The schedule is recalculated.  However, the resulting end date may
     * not actually be the same as the one specified.  For example, a task dependency
     * may be causing the end date to be earlier than the entered end date.
     * In this case, while the constraint and constaint date are set, the task end date
     * won't have changed.
     * @param endDate the new end date for the current schedule entry
     * @param timeZone the current user's time zone, used to update the date with a specific time of day
     * @throws NullPointerException if endDate or timeZone is null
     */
    public void endDateChanged(Date endDate, TimeZone timeZone) {

        if (endDate == null || timeZone == null) {
            throw new NullPointerException("Missing required parameter endDate or timeZone");
        }
        //get the next working time to update to constraint date fix for bfd-2700 (sachin)
        IWorkingTimeCalendar cal = scheduleEntry.getResourceCalendar(schedule.getWorkingTimeCalendarProvider());
        try {
            endDate = cal.ensureWorkingTimeStart(endDate);
        } catch (NoWorkingTimeException e) {
        }
        
        this.scheduleEntry.setConstraintType(TaskConstraintType.FINISH_NO_EARLIER_THAN);
        this.scheduleEntry.setConstraintDate(ScheduleEntry.updateConstraintTime(this.scheduleEntry.getConstraintType(), endDate, timeZone));

        recalculateSchedule();
    }

    /**
     * Updates the schedule entry based on the specified constraint and recalculates the schedule.
     * <p/>
     * Any existing constraint is overwritten. Constraint Types <code>As Soon As Possible</code> and
     * <code>As Late As Possible</code> cannot specify a constraint date; <code>null</code> should be
     * specified instead.
     * <p/>
     * The schedule and scheduleEntry are recalculated.
     * @param constraintType the constraint to set
     * @param constraintDate the constraint date if required by the constraint type or null if not
     * @throws NullPointerException if constraintType or constraintDate are null
     * @throws IllegalArgumentException if a constraint date is specified when the constraint
     * type is {@link TaskConstraintType#AS_SOON_AS_POSSIBLE} or {@link TaskConstraintType#AS_LATE_AS_POSSIBLE}
     */
    public void constraintChanged(TaskConstraintType constraintType, Date constraintDate, TimeZone timeZone) {

        if (constraintType == null) {
            throw new NullPointerException("Missing required parameter constraintType");
        }

        if (constraintType.equals(TaskConstraintType.AS_SOON_AS_POSSIBLE) 
                || constraintType.equals(TaskConstraintType.AS_LATE_AS_POSSIBLE) 
                || constraintType.equals(TaskConstraintType.START_AND_END_DATES_FIXED)) {

            if (constraintDate != null) {
                throw new IllegalArgumentException("Constraint types As Soon As Possible, As Late As Possible and Start And End Dates Fixed cannot specify a constraint date");
            }

        } else if (constraintDate == null) {
            // Other constraint types need constraint dates
            throw new NullPointerException("Missing required parameter constraintDate");

        }
        
        if (constraintDate != null) {
	        //get the next working time to update to constraint date fix for bfd-2700 (sachin)
	        IWorkingTimeCalendar cal = scheduleEntry.getResourceCalendar(schedule.getWorkingTimeCalendarProvider());
	        try {
	            constraintDate = cal.ensureWorkingTimeStart(constraintDate);
	        } catch (NoWorkingTimeException e) {
	        }
        
	        this.scheduleEntry.setConstraintType(constraintType);
	        this.scheduleEntry.setConstraintDate(ScheduleEntry.updateConstraintTime(constraintType, constraintDate, timeZone));
        } else {
            this.scheduleEntry.setConstraintType(constraintType);
	        this.scheduleEntry.setConstraintDate(null);
        }
        
        recalculateSchedule();
    }

    /**
     * Recalculates the schedule without performing any persistence operations.
     * @throws IllegalStateException if a persistence exception occurs, because
     * no persistence operations were expected to be performed
     */
    private void recalculateSchedule() {
        try {
            tec.recalculateTaskTimesNoLoad(this.schedule);
        } catch (PersistenceException e) {
            // No loading should ever be occurring; the problem is with methods
            // That optionally load, they optionally throw a PersistenceException
            throw (IllegalStateException) new IllegalStateException("PersistenceException occurred when no persistence operations were expected").initCause(e);
        }
    }


}