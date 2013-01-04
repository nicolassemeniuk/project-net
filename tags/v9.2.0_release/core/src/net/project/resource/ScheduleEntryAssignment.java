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

 package net.project.resource;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.DateCalculatorHelper;
import net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar;
import net.project.calendar.workingtime.DurationCalculatorHelper;
import net.project.calendar.workingtime.IDaysWorked;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.PercentageCalculatorHelper;
import net.project.calendar.workingtime.WorkCalculatorHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.hibernate.model.PnAssignment;
import net.project.persistence.PersistenceException;
import net.project.schedule.SchedulePropertiesHelper;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.schedule.calc.IDateCalculator;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

/**
 * Provides an assignment to a task.
 *
 * @author Tim Morrow
 * @since Version 7.6.2
 */
public class ScheduleEntryAssignment extends Assignment {

    //
    // Static members
    //

    /**
     * Parses the entered percent assigned value and returns it as an int
     * (where "100%" = 100).
     * <p>
     * Fractional values are rounded up to the nearest whole number using the
     * "round half up" rounding mode.
     * </p>
     * <p>
     * Note: This uses normal Java parsing mechanisms so the value is truncated
     * at the first non-digit character after the first position.  For example,
     * <code>100X</code> is a valid number (results in <code>100</code>) but <code>X100</code> is not.
     * </p>
     * @param value the value to parse; if null or empty zero is returned
     * @return the parsed value or zero if none was specified
     * @throws ParseException if there is a problem parsing the value, for example
     * it is not a valid number
     */
    public static int parsePercentAssigned(String value) throws ParseException {
        int percentAssigned;

        if (Validator.isBlankOrNull(value)) {
            percentAssigned = 0;

        } else {
            // We are disallowing fractional % assigned (like MSP)
            // But if the user entered a fraction we round-half-up to the nearest whole number
            // Note: This assumes that all sub-classes of Number returned by the parse method
            // override toString to provide a sensible result (this is true of all known sub-classes)
            BigDecimal parsedPercentAssigned = new BigDecimal(NumberFormat.getInstance().parseNumber(value).toString());
            percentAssigned = parsedPercentAssigned.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        }

        return percentAssigned;
    }

    /**
     * Indicates whether the specified percent assigned is valid.
     * <p>
     * A percent assigned is invalid if it does not satisfy 0 <= percentAssigned < 100
     * </p>
     * @param percentAssigned the percent assigned value
     * @return true if it is valid; false otherwise
     */
    public static boolean isValidPercentAssigned(int percentAssigned) {
        return (percentAssigned >= 0 && percentAssigned <= 100);
    }

    //
    // Instance members
    //

    /** Date on which assignment is planned to start. */
    private Date startTime = null;
    /** Date on which the first work on the assignment occurred. */
    private Date actualStart = null;

    /** Date on which assignment completes. */
    private Date endTime = null;
    /** Date on which the final work on this assignment was completed. */
    private Date actualFinish = null;
    /** Date on which we expected the work to complete given the actual start date. */
    private Date estimatedFinish = null;

    /**
     * This is a copy of the current ScheduleEntryAssignment as it appeared when
     * it was loaded from the database.  This assignment should only be stored
     * later on if it differs from this saved state.
     */
    private ScheduleEntryAssignment lastSavedState;
    /** This indicates the amount of work associated with this assignment. */
    private TimeQuantity work;
    /** The total amount of work completed on this assignment. */
    private TimeQuantity workComplete;
    /** The amount of work complete that we are going to store and log during the next assignment storage. */
//    private TimeQuantity workCompleteDeltaToStore = new TimeQuantity(0, TimeQuantityUnit.HOUR);
    /**
     * This field indicates the work complete that was done before being assigned
     * to the task.  This only really applies to assignments that have not yet been
     * committed.
     */
//    private TimeQuantity previousWorkComplete = new TimeQuantity(0, TimeQuantityUnit.HOUR);
    /** This structure will reveal when the work complete in question was completed. */
//    private IDaysWorked daysWorkedToStore = null;
    /** The total amount of work for the entire object this assignment is related to. */
//    private TimeQuantity totalWork;
    /** The total amount of work complete for the entire object. */
//    private TimeQuantity totalWorkComplete;
    /**
     * This is work that the assignee received because there was "unassigned work complete" in the schedule entry that
     * the user received.  It is important to keep this information because this work is treated differently than other
     * work.  Because they didn't really "do" this work specifically like work that has been logged, it should be
     * redistributed if we are click add/remove repeatedly on the schedule assingments page.
     */
//    private TimeQuantity distributedWorkComplete = new TimeQuantity(0, TimeQuantityUnit.HOUR);

    /**
     * The timeZone of the assignee.
     * This is used when calculating working times.
     */
    private TimeZone timeZone;

    /**
     * The percentage complete of the entire task.  We need to replace this with
     * the percentage complete of the assignment.
     * <code>100</code> = 100% complete and <code>50</code> = 50% complete.
     */
//    private int taskPercentComplete;
    /** Percent complete of this assignment. */
    private BigDecimal percentComplete;
    /** Indicates if the work for this assignment has been completed. */
    private boolean isComplete = false;
    
    /**Indicates the task type of assignment */
    private String taskType; 
    
    /** Indicates whether task type is required for the assignments*/
    private boolean includeTaskType = false;

    //not needed as no resource can be assigned to shared tasks 
//    /** is coresponding task from share */
//    private boolean isFromShare = false;
//    
//    /** is coresponding task from shard read only*/
//    private boolean isShareReadOnly = false;

    /**
     * Creates an empty ScheduleEntryAssignment.
     */
    public ScheduleEntryAssignment() {
        super();
    }

    /**
     * Returns the XML representation of a ScheduleEntryAssignment.
     * @return
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append("<assignment>\n");
        xml.append(getXMLElements());
        // Deprecated XML element "due_date"
        xml.append("<due_date>" + XMLUtils.formatISODateTime(getEndTime()) + "</due_date>");
        xml.append("<start_time>" + XMLUtils.formatISODateTime(getStartTime()) + "</start_time>");
        xml.append("<end_time>" + XMLUtils.formatISODateTime(getEndTime()) + "</end_time>");
        xml.append("<actual_start>" + XMLUtils.formatISODateTime(getActualStart()) + "</actual_start>");
        xml.append("<actual_end>" + XMLUtils.formatISODateTime(getActualFinish()) + "</actual_end>");
        xml.append("<estimated_end>" + XMLUtils.formatISODateTime(getEstimatedFinish()) + "</estimated_end>");
        xml.append("<percent_complete>" + (percentComplete != null ? percentComplete.multiply(new BigDecimal(100)) : new BigDecimal(0)) + "</percent_complete>");
//        xml.append("<task_percent_complete>"+taskPercentComplete+"</task_percent_complete>");
        xml.append("<work>" + getWork().toShortString(0,2) + "</work>");
        xml.append("<work_complete>" + getWorkComplete().toShortString(0,2) + "</work_complete>");
        xml.append("<work_remaining>" + getWorkRemaining().toShortString(0,2) + "</work_remaining>");
//        xml.append("<total_work>" + getTotalWork().toShortString(0,2) + "</total_work>");
//        xml.append("<total_work_complete>" + getTotalWorkComplete().toShortString(0,2) + "</total_work_complete>");

        if (getEndTime() != null && new Date().after(getEndTime())) {
            xml.append("<is_late/>");
        }

        PnCalendar cal = new PnCalendar(getTimeZone());
        if (getStartTime() != null && cal.startOfDay(getStartTime()).equals(cal.startOfDay(getEndTime()))) {
            xml.append("<one_day_assignment/>");
        }

        xml.append("</assignment>\n");

        return xml.toString();
    }

    /**
     * Specifies the start time of this assignment.
     * This is an optional value.  It is typically used for task assignments.
     * @param startTime the start time of the assignment.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the current start time of this assignment.
     * @return the start time
     */
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * Specifies the end time of this assignment.
     * @param endTime the end time
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Returns the current end time of this assignment.
     * @return the end time
     */
    public Date getEndTime() {
        return this.endTime;
    }

    /**
     * Get the date on which work started on this assignment.
     *
     * @return a <code>Date</code> indicating when work started on this
     * assignment.
     */
    public Date getActualStart() {
        return actualStart;
    }

    /**
     * Set the date on which work started on this assignment.
     *
     * @param actualStart a <code>Date</code> on which work started on the
     * assignment.
     */
    public void setActualStart(Date actualStart) {
        this.actualStart = actualStart;
    }

    /**
     * Get the date that the assignee finished work on this assignment.
     *
     * @return a <code>Date</code> indicating when work was completed on this
     * assignment.
     */
    public Date getActualFinish() {
        return actualFinish;
    }

    /**
     * Set the date that the assignee finished work on this assignment.  This
     * method has a side effect that it updates the estimated finish to be the
     * same as the actual finish.
     *
     * @param actualFinish a <code>Date</code> indicating when work finished on
     * this assignment.
     */
    public void setActualFinish(Date actualFinish) {
        this.actualFinish = actualFinish;

        if (actualFinish != null && !actualFinish.equals(estimatedFinish)) {
            estimatedFinish = actualFinish;
        }
    }

    /**
     * Get the date on which the assignment is suspected it will be complete.
     * This is based on there being an actual start date, but the assignment
     * being incomplete.
     *
     * @return a <code>Date</code> containing the estimated finish date for
     * the assignment.
     */
    public Date getEstimatedFinish() {
        return estimatedFinish;
    }

    /**
     * Set the date on which it is estimated that the assignment will be
     * completed given the actual start date.
     *
     * @param estimatedFinish a <code>Date</code> which estimates when the
     * assignment will be complete.
     */
    public void setEstimatedFinish(Date estimatedFinish) {
        this.estimatedFinish = estimatedFinish;
    }

    /**
     * Calculates the estimated finish date given the actual start date of an
     * assignment.
     *
     * @param def a <code>WorkingTimeCalendarDefinition</code> which is used
     * in the calculation.  This must the the proper provider for this
     * assignment.
     * @param timeZone a <code>TimeZone</code> which is used
     * in the calculation. Must be the resource timezone or schedules default
     */
    public void calculateEstimatedFinish(WorkingTimeCalendarDefinition def, TimeZone timeZone) {
        DefinitionBasedWorkingTimeCalendar cal = new DefinitionBasedWorkingTimeCalendar(timeZone, def);
        DateCalculatorHelper dateCalculatorHelper = new DateCalculatorHelper(cal);
        estimatedFinish = dateCalculatorHelper.calculateDate(actualStart, getWork(), getPercentAssignedDecimal());
    }

    /**
     * Sets the TimeZone for the assignee.
     * <p>
     * This ensures working times are calculated according to the assignee's
     * time zone.
     * </p>
     * @param timeZone the time zone for the assignee
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Returns the time zone of the assignee of this assignment.
     * @return the time zone or null if the assignee has none
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    /**
     * Returns a working time calendar for this assignment; based on the
     * resource's working times.
     * @param provider the provider used to locate calendars
     * @return a working time calendar
     */
    public DefinitionBasedWorkingTimeCalendar getWorkingTimeCalendar(IWorkingTimeCalendarProvider provider) {
        return new DefinitionBasedWorkingTimeCalendar(determineTimeZone(provider), determineCalendarDefinition(provider));
    }

    /**
     * Populates this assignment with schedule entry specific items.
     * @param result the result set from which to get the data
     * @throws SQLException if there is a problem reading the result set
     */
    protected void populateAssignment(ResultSet result) throws SQLException {
        startTime = DatabaseUtils.getTimestamp(result, AssignmentFinder.START_DATE_COL_ID);
        endTime = DatabaseUtils.getTimestamp(result, AssignmentFinder.END_DATE_COL_ID);
        actualStart = DatabaseUtils.getTimestamp(result, AssignmentFinder.ACTUAL_START_COL_ID);
        actualFinish = DatabaseUtils.getTimestamp(result, AssignmentFinder.ACTUAL_FINISH_COL_ID);
        estimatedFinish = DatabaseUtils.getTimestamp(result, AssignmentFinder.ESTIMATED_FINISH_COL_ID);
        work = DatabaseUtils.getTimeQuantity(result, AssignmentFinder.WORK_COL_ID, AssignmentFinder.WORK_UNITS_COL_ID);
        workComplete = DatabaseUtils.getTimeQuantity(result, AssignmentFinder.WORK_COMPLETE_COL_ID, AssignmentFinder.WORK_COMPLETE_UNITS_COL_ID);
//        totalWork = DatabaseUtils.getTimeQuantity(result, AssignmentFinder.TOTAL_WORK_COL_ID, AssignmentFinder.TOTAL_WORK_UNITS_COL_ID);
//        totalWorkComplete = DatabaseUtils.getTimeQuantity(result, AssignmentFinder.TOTAL_WORK_COMPLETE_COL_ID, AssignmentFinder.TOTAL_WORK_COMPLETE_UNITS_COL_ID);
        percentComplete = result.getBigDecimal(AssignmentFinder.PERCENT_COMPLETE_COL_ID);
//        workCompleteDeltaToStore = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        if(includeTaskType){
        	taskType = result.getString(AssignmentFinder.TASK_TYPE_COLUMN);
        }
        if (percentComplete == null) {
//            TimeQuantity tempWork = totalWork;
//            if (tempWork.isZero()) {
//                tempWork = new TimeQuantity(1, TimeQuantityUnit.HOUR);
//            }
//
//            percentComplete = workComplete.divide(tempWork, 2, BigDecimal.ROUND_HALF_UP); 
        	percentComplete = new BigDecimal("0.00000");
        }

//        taskPercentComplete = result.getInt(AssignmentFinder.TASK_PERCENT_COMPLETE_COL_ID);
        isComplete = result.getBoolean(AssignmentFinder.IS_COMPLETE_COL_ID);

        // Note: timezone_code may be null for as-yet unregistered users
        // We'll leave it null for those users so that later we will use
        // the schedule default
        String timeZoneID = result.getString(AssignmentFinder.TIMEZONE_CODE_COL_ID);
        if (timeZoneID != null) {
            setTimeZone(TimeZone.getTimeZone(timeZoneID));
        } else {
            setTimeZone(null);
        }

    }
    
//    @Override
//    protected void populateAssignment(PnAssignment pnAssignment, String timeZoneId) {
//        startTime = pnAssignment.getStartDate();
//        endTime = pnAssignment.getEndDate();
//        actualStart = pnAssignment.getActualStart();
//        actualFinish = pnAssignment.getActualFinish();
//        estimatedFinish = pnAssignment.getEstimatedFinish();
//        work = new TimeQuantity(pnAssignment.getWork(), TimeQuantityUnit.getForID(pnAssignment.getWorkUnits()));
//        workComplete = new TimeQuantity(pnAssignment.getWorkComplete(), TimeQuantityUnit.getForID(pnAssignment.getWorkCompleteUnits()));
//        percentComplete = new BigDecimal(pnAssignment.getPercentComplete());
//        isComplete = pnAssignment.getIsComplete() == 1 ? true : false;
//        if (timeZoneId != null && "".equals(timeZoneId)) {
//            setTimeZone(TimeZone.getTimeZone(timeZoneId));
//        } else {
//            setTimeZone(null);
//        }
//    }

    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            store(db);
            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) { }
            throw new PersistenceException("Unable to store assignment.", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Stores the assignment.
     * Creates an assignment, or updates an assignment if one already exists
     * for the current id for the same space id, person id and object id.
     *
     * This version differs from the normal {@link net.project.resource.Assignment#store}
     * because this class knows when it has been modified.
     *
     * @throws SQLException if there is a problem storing the assignment
     */
    public void store(DBBean db) throws SQLException {
        //Assertions are only used in development time.
        assert workComplete == null || workComplete.compareTo(work) <= 0 : "Work complete cannot be greater than work";
        assert work == null || work.compareTo(TimeQuantity.O_HOURS) >= 0 : "Work cannot be negative";
        assert workComplete == null || workComplete.compareTo(TimeQuantity.O_HOURS) >= 0 : "Work complete cannot be negative";

        if (!this.equals(lastSavedState)) {
            //If the actual start hasn't been set and the work is now completed,
            //set the actual finish
            if (actualFinish == null && getWork().equals(getWorkComplete())) {
                actualFinish = new Date();
            }
            BigDecimal percentComplete = null;
            if (workComplete != null && work != null) {
                if (work.getAmount().signum() != 0) {
                    percentComplete = ScheduleTimeQuantity.convertToHour(workComplete).divide(ScheduleTimeQuantity.convertToHour(work), 5, BigDecimal.ROUND_HALF_UP);
                } 
            }
            super.storeAssignment(db, startTime, endTime, actualStart, actualFinish, estimatedFinish, work, workComplete, percentComplete);
        }

//        if (workCompleteDeltaToStore != null && !workCompleteDeltaToStore.isZero() && daysWorkedToStore != null) {
//
//            // Iterate over all the date ranges worked, creating an entry for each
//            List entries = new LinkedList();
//            TimeQuantity currentWorkComplete = this.workComplete;
//
//            for (Iterator iterator = daysWorkedToStore.getTimeRangesWorked().iterator(); iterator.hasNext();) {
//                TimeRangeWorked timeRangeWorked = (TimeRangeWorked) iterator.next();
//
//                //Figure out the work completed between the dates
//                TimeQuantity hoursWorked = timeRangeWorked.getWork();
//
//                //Make sure we don't have entries without any work.
//                if (hoursWorked.isZero()) {
//                    continue;
//                }
//
//                //For every iteration, we add the amount of the logged work complete to the
//                //currently stored work complete.  This should allow the log entries
//                //to show the correct amount of time remaining.
//                if (currentWorkComplete != null) {
//                    currentWorkComplete = currentWorkComplete.add(hoursWorked);
//                } else {
//                    currentWorkComplete = hoursWorked;
//                }
//
//                //Create a log entry for this work
//                AssignmentWorkLogEntry log = new AssignmentWorkLogEntry();
//                log.setAssigneeID(this.getPersonID());
//                log.setObjectID(this.getObjectID());
//                log.setDatesWorked(
//                    new DateRange(timeRangeWorked.getStartDateTime(), timeRangeWorked.getEndDateTime())
//                );
//                log.setHoursWorked(hoursWorked);
//                log.setRemainingWork(work.subtract(currentWorkComplete));
//                log.setScheduledWork(work);
//                log.setModifiedByID(SessionManager.getUser().getID());
//
//                BigDecimal percentComplete;
//                if (work.isZero()) {
//                    percentComplete = new BigDecimal("0.00");
//                } else {
//                    percentComplete = currentWorkComplete.divide(work, 2, BigDecimal.ROUND_HALF_UP);
//                }
//                log.setPercentComplete(percentComplete);
//
//                entries.add(log);
//            }
//
//            new AssignmentWorkLogDAO().store(entries, db);
//        }
//
//        //Now that we've stored, the work complete delta becomes the work
//        if (workCompleteDeltaToStore != null && !workCompleteDeltaToStore.isZero()) {
//            if (workComplete == null) {
//                workComplete = workCompleteDeltaToStore;
//            } else {
//                workComplete = workComplete.add(workCompleteDeltaToStore);
//            }
//
//            workCompleteDeltaToStore = TimeQuantity.O_HOURS;
//        }

        // TODO: 814-->trunk merge conflict.  Remove after compile and test.
        // previousWorkComplete = null;
//        previousWorkComplete = TimeQuantity.O_HOURS;
    }

    /**
     * Determine whether this <code>ScheduleEntryAssignment</code> object is
     * equal to another object by checking their types and internal fields.
     *
     * @param o a <code>Object</code> which is to be compared to the current one.
     * @return <code>true</code> if the two objects are identical.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduleEntryAssignment)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final ScheduleEntryAssignment scheduleEntryAssignment = (ScheduleEntryAssignment) o;

        if (isComplete != scheduleEntryAssignment.isComplete) {
            return false;
        }
        if (actualFinish != null ? !actualFinish.equals(scheduleEntryAssignment.actualFinish) : scheduleEntryAssignment.actualFinish != null) {
            return false;
        }
        if (actualStart != null ? !actualStart.equals(scheduleEntryAssignment.actualStart) : scheduleEntryAssignment.actualStart != null) {
            return false;
        }
        if (endTime != null ? !endTime.equals(scheduleEntryAssignment.endTime) : scheduleEntryAssignment.endTime != null) {
            return false;
        }
        if (estimatedFinish != null ? !estimatedFinish.equals(scheduleEntryAssignment.estimatedFinish) : scheduleEntryAssignment.estimatedFinish != null) {
            return false;
        }
        if (percentComplete != null ? !percentComplete.equals(scheduleEntryAssignment.percentComplete) : scheduleEntryAssignment.percentComplete != null) {
            return false;
        }
        if (startTime != null ? !startTime.equals(scheduleEntryAssignment.startTime) : scheduleEntryAssignment.startTime != null) {
            return false;
        }
        if (work != null ? !work.equals(scheduleEntryAssignment.work) : scheduleEntryAssignment.work != null) {
            return false;
        }
        if (workComplete != null ? !workComplete.equals(scheduleEntryAssignment.workComplete) : scheduleEntryAssignment.workComplete != null) {
            return false;
        }
//        if (!workCompleteDeltaToStore.equals(scheduleEntryAssignment.workCompleteDeltaToStore)) {
//            return false;
//        }
//        if (daysWorkedToStore != null ? !daysWorkedToStore.equals(scheduleEntryAssignment.daysWorkedToStore) : scheduleEntryAssignment.daysWorkedToStore != null) {
//            return false;
//        }

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 29 * result + (actualStart != null ? actualStart.hashCode() : 0);
        result = 29 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 29 * result + (actualFinish != null ? actualFinish.hashCode() : 0);
        result = 29 * result + (estimatedFinish != null ? estimatedFinish.hashCode() : 0);
        result = 29 * result + (work != null ? work.hashCode() : 0);
        result = 29 * result + (workComplete != null ? workComplete.hashCode() : 0);
        result = 29 * result + (percentComplete != null ? percentComplete.hashCode() : 0);
        result = 29 * result + (isComplete ? 1 : 0);
        return result;
    }

    public Object clone() {
        ScheduleEntryAssignment clone = new ScheduleEntryAssignment();
        clone.setPersonID(getPersonID());
        clone.setAssignorID(getAssignorID());
        clone.setObjectID(getObjectID());
        if (getPercentAssignedInt() >= 0) {
            clone.setPercentAssignedDecimal(getPercentAssignedDecimal());
        }
        clone.setPersonRole(getPersonRole());
        clone.setStatus(getStatus());
        clone.setStartTime(getStartTime());
        clone.setEndTime(getEndTime());
        clone.setActualStart(getActualStart());
        clone.setActualFinish(getActualFinish());
        clone.setEstimatedFinish(getEstimatedFinish());
        clone.setPrimaryOwner(isPrimaryOwner());
        clone.setSpaceID(getSpaceID());
        clone.setWork(work);
        clone.setSpaceName(getSpaceName());
        clone.setObjectType(getObjectType());
        clone.setPersonName(getPersonName());
        clone.setTimeZone(timeZone);
//        clone.setTaskPercentComplete(getTaskPercentComplete());
//        clone.setPercentComplete(getPercentComplete());
//        clone.setTotalWork(getTotalWork());
//        clone.setTotalWorkComplete(getTotalWorkComplete());
        clone.setWorkComplete(workComplete);
        clone.setPercentComplete(getPercentComplete());
        clone.setComplete(isComplete());
//        clone.setWorkCompleteDeltaToStore(workCompleteDeltaToStore);
//        clone.daysWorkedToStore = daysWorkedToStore;
//        clone.distributedWorkComplete = distributedWorkComplete;
//        clone.previousWorkComplete = previousWorkComplete;

        return clone;
    }

    /**
     * This method indicates that the current state that the object is in is the
     * state that is currently saved in the database for this object.  The
     * object should only be saved if it changes from this state.
     */
    public void setSavedState() {
        lastSavedState = (ScheduleEntryAssignment)clone();
    }

    /**
     * Indicates if this object has been changed from the last time that its
     * state was saved with the {@link #setSavedState} method.
     *
     * @return a <code>boolean</code> indicating if the object has changed since
     * the last time setSavedState was called.
     */
    public boolean isModified() {
        return lastSavedState == null || !this.equals(lastSavedState);
    }

    /**
     * Get the total amount of work that the assignee would need to perform to
     * complete this assignment.
     *
     * @return a <code>TimeQuantity</code> representing the total amount of work
     * that the assignee would have to do to complete this assignment.
     */
    public TimeQuantity getWork() {
        TimeQuantity workToReturn = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        TimeQuantityUnit targetUnits;

        //Determine the proper units to return the work complete in
        if (work != null && !work.isZero()) {
            targetUnits = work.getUnits();
        } else {
            targetUnits = TimeQuantityUnit.HOUR;
        }


        //Now add up the components of work complete we have on hand
        if (work != null && !work.isZero()) {
            workToReturn = workToReturn.add(work);
        }

        return workToReturn.convertTo(targetUnits, 10, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Set the total amount of work that the assignee would need to complete to
     * complete this task.
     *
     * @param work a <code>TimeQuantity</code> representing the total amount of
     * work that the assignee would need to complete to complete this assignment.
     */
    public void setWork(TimeQuantity work) {
        TimeQuantity currentWork = getWork();

        if (work == null || !work.equals(currentWork)) {
            this.work = work;
        }
    }

    /**
     * Get the amount of work that the assignee has completed thus far.
     *
     * @return a <code>TimeQuantity</code> representing the total amount of
     * work that the user has completed or zero hours if work complete has not
     * been set
     * @see #workComplete
     */
    public TimeQuantity getWorkComplete() {
        TimeQuantity workCompleteToReturn = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        TimeQuantityUnit targetUnits;

        //Determine the proper units to return the work complete in
        if (workComplete != null && !workComplete.isZero()) {
            targetUnits = workComplete.getUnits();
        } /*else if (workCompleteDeltaToStore != null && !workCompleteDeltaToStore.isZero()) {
            targetUnits = workCompleteDeltaToStore.getUnits();
        } */else {
            targetUnits = TimeQuantityUnit.HOUR;
        }


        //Now add up the components of work complete we have on hand
        if (workComplete != null && !workComplete.isZero()) {
            workCompleteToReturn = workCompleteToReturn.add(workComplete);
        }
//        if (workCompleteDeltaToStore != null && !workCompleteDeltaToStore.isZero()) {
//            workCompleteToReturn = workCompleteToReturn.add(workCompleteDeltaToStore);
//        }
//        if (previousWorkComplete != null && !previousWorkComplete.isZero()) {
//            workCompleteToReturn = workCompleteToReturn.add(previousWorkComplete);
//        }


        return workCompleteToReturn.convertTo(targetUnits, 10, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * Set the amount of work that the assignee has completed thus far.
     *
     * @param workComplete a <code>TimeQuantity</code> representing the total
     * amount of work that the assignee has completed thus far.
     */
    public void setWorkComplete(TimeQuantity workComplete) {
        TimeQuantity currentWorkComplete = getWorkComplete();

        if (workComplete == null || !workComplete.equals(currentWorkComplete)) {
            this.workComplete = workComplete;
            //Don't count work we've already completed as a delta
//            TimeQuantity tempWorkCompleteDeltaToStore = (this.workComplete == null || this.workComplete.isZero() ? workComplete : workComplete.subtract(this.workComplete));
//            tempWorkCompleteDeltaToStore = (this.previousWorkComplete == null ? tempWorkCompleteDeltaToStore : tempWorkCompleteDeltaToStore.subtract(previousWorkComplete));
//
//            if (tempWorkCompleteDeltaToStore == null || !tempWorkCompleteDeltaToStore.equals(workCompleteDeltaToStore)) {
//                workCompleteDeltaToStore = tempWorkCompleteDeltaToStore;
//                daysWorkedToStore = null;
//            }
        }
    }

//    public TimeQuantity getDistributedWorkComplete() {
//        return distributedWorkComplete;
//    }
//
//    public void setDistributedWorkComplete(TimeQuantity distributedWorkComplete) {
//        this.distributedWorkComplete = distributedWorkComplete;
//    }
//
//    public TimeQuantity getWorkCompleteDeltaToStore() {
//        return workCompleteDeltaToStore;
//    }
//
//    public void setWorkCompleteDeltaToStore(TimeQuantity workCompleteDelta) {
//        this.workCompleteDeltaToStore = workCompleteDelta;
//    }
//
//    public TimeQuantity getPreviousWorkComplete() {
//        return previousWorkComplete;
//    }
//
//    public void setPreviousWorkComplete(TimeQuantity previousWorkComplete) {
//        this.previousWorkComplete = previousWorkComplete;
//    }

//    public IDaysWorked getDeltaWorkLog() {
//        return this.daysWorkedToStore;
//    }

    /**
     * Indicates if the work is complete for this assignment.
     *
     * @return a <code>boolean</code> indicating if the work is complete for this
     * assignment.
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Indicate if work is complete for this assignment.
     *
     * @param complete a <code>boolean</code> indicating if work is complete for
     * this task.
     */
    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    /**
     * Get the amount of work remaining to complete this assignment.
     *
     * @return a <code>TimeQuantity</code> representing the amount of work
     * necessary to complete this assignment.
     */
    public TimeQuantity getWorkRemaining() {
        TimeQuantity workCompleteToCheck = ScheduleTimeQuantity.convertToHour(getWorkComplete());
        TimeQuantity workToCheck = ScheduleTimeQuantity.convertToHour(getWork());
        if (workToCheck.compareTo(workCompleteToCheck) < 0) {
            //More work has been completed than was assigned, there is no more work to do.
            return new TimeQuantity(0, TimeQuantityUnit.HOUR);
        } else {
            return workToCheck.subtract(workCompleteToCheck);
        }
    }

    /**
     * Get the total amount of work needed to complete all work for the object
     * the assignment relates to.  For example, for a task, this would be all
     * the work required to finish the task.
     *
     * @return a <code>TimeQuantity</code> representing the total amount of work
     * required to complete the work for the object or zero hours if total work
     * has not been set
     */
//    public TimeQuantity getTotalWork() {
//        return (totalWork != null ? totalWork : new TimeQuantity(0, TimeQuantityUnit.HOUR));
//    }
//
//    public void setTotalWork(TimeQuantity totalWork) {
//        this.totalWork = totalWork;
//    }
//
//    public TimeQuantity getTotalWorkComplete() {
//        return (totalWorkComplete != null ? totalWorkComplete : new TimeQuantity(0, TimeQuantityUnit.HOUR));
//    }
//
//    public void setTotalWorkComplete(TimeQuantity totalWorkComplete) {
//        this.totalWorkComplete = totalWorkComplete;
//    }

    /**
     * Get the percentage complete of the schedule entry related to this
     * assignment.
     *
     * @return a <code>int</code> indicating the completion of the task related
     * to this assignment.
     */
//    public int getTaskPercentComplete() {
//        return taskPercentComplete;
//    }

    /**
     * Set the percentage complete of the task related to this assignment.  Note
     * that setting this value does not imply that the value will be stored when
     * this assignment is stored.
     *
     * @param taskPercentComplete a <code>int</code> indicating the completion
     * of the task this assignment is associated with.
     */
//    public void setTaskPercentComplete(int taskPercentComplete) {
//        this.taskPercentComplete = taskPercentComplete;
//    }

    /**
     * Returns this assignment's percentage complete as a percent value,
     * currently interpreted as percentage of work complete.

     * @return the percent complete where <code>1.0</code> = 100% and <code>0.5</code> = 50%
     * @see #setPercentComplete(BigDecimal)
     */
    public BigDecimal getPercentComplete() {
        return percentComplete;
    }

    /**
     * Specifies this assignment's percentage of work complete.
     * @param percentComplete the percent complete
     * @see #getPercentComplete()
     */
    public void setPercentComplete(BigDecimal percentComplete) {
        this.percentComplete = percentComplete;
    }

    public IDateCalculator getDateCalculator(IWorkingTimeCalendarProvider provider) {
        return new AssignmentDateCalculator(this, provider);
    }

    /**
     * Calculates duration that it takes this assignment to complete its work beginning at
     * the specified date, taking into account the percentage assigned and working times.
     * @param startDate the date at which to begin calculating duration.
     * @param provider the provider from which to get the assignment resource's working time calendar
     * @return the duration that the assignment works to complete its work at its percentage
     */
    public IDaysWorked getDaysWorked(Date startDate, IWorkingTimeCalendarProvider provider) {
        return getDaysWorked(startDate, getWork(), provider);
    }

    /**
     * Calculates duration that it takes this assignment to complete the specified amount of work beginning at
     * the specified date, taking into account the percentage assigned and working times.
     * @param startDate the date at which to begin calculating duration.
     * @param provider the provider from which to get the assignment resource's working time calendar
     * @return the duration that the assignment works to complete its work at its percentage
     */
    public IDaysWorked getDaysWorked(Date startDate, TimeQuantity work, IWorkingTimeCalendarProvider provider) {
        DurationCalculatorHelper durationCalculatorHelper = new DurationCalculatorHelper(determineTimeZone(provider), determineCalendarDefinition(provider));
        return durationCalculatorHelper.getDaysWorked(startDate, work, getPercentAssignedDecimal());

    }

    /**
     * Calculates the amount of work that can be completed by this assignment between the
     * specified dates given the current percentage.
     * <p>
     * The assignment work is updated.
     * </p>
     * @param provider the working time calendar provider used to get the assignment's resource's working
     * time calendar, or the schedule default calendar if the resource has none
     * @param startDate the start date
     * @param endDate the end date
     * @see #getWork()
     */
    public void calculateWork(IWorkingTimeCalendarProvider provider, Date startDate, Date endDate) {
        WorkCalculatorHelper workCalculatorHelper = new WorkCalculatorHelper(getWorkingTimeCalendar(provider));
        setWork(workCalculatorHelper.getWork(startDate, endDate, getPercentAssignedDecimal()).convertTo(TimeQuantityUnit.HOUR, 3, BigDecimal.ROUND_HALF_UP));

    }

    /**
     * Get the amount of unique working time between two dates.
     *
     * @param provider a working time calendar provider used to get the resource's
     * working time calendar.
     * @param startDate the start date of the duration we want to calculate.
     * @param endDate the end date of the duration we want to calculate
     * @return a <code>TimeQuantity</code> indicating the amount of working time
     * the resource has between these two dates.
     */
    public TimeQuantity getAssignmentDuration(IWorkingTimeCalendarProvider provider, Date startDate, Date endDate) {
        WorkCalculatorHelper workCalculatorHelper = new WorkCalculatorHelper(getWorkingTimeCalendar(provider));
        return workCalculatorHelper.getWork(startDate, endDate, new BigDecimal("1.00")).convertTo(TimeQuantityUnit.HOUR, 3, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calculates the percentage of a day that the assignment resource must work
     * to complete the assigned amount of work within the specified dates, considering
     * the resource's working time calendar.
     * <p>
     * For example, given a 2 day period and 8 hours of assigned work, the percentage
     * of time spent working would be 50%.
     * </p>
     * <p>
     * The percentage assigned is update.
     * </p>
     * @param provider the working time calendar provider used to get the assignment's resource's working
     * time calendar, or the schedule default calendar if the resource has none
     * @param startDate the starting date
     * @param endDate the ending date
     * @see #getPercentAssigned()
     */
    public void calculatePercentage(IWorkingTimeCalendarProvider provider, Date startDate, Date endDate) {
        PercentageCalculatorHelper percentageCalculatorHelper = new PercentageCalculatorHelper(determineTimeZone(provider), determineCalendarDefinition(provider));
        setPercentAssignedDecimal(percentageCalculatorHelper.getPercentage(startDate, endDate, getWork()));
    }

    /**
     * When work complete is performed, that work complete must be translated into
     * times at which the work was done.  This method is called to do those
     * calculations.
     *
     * @param provider a <code>IWorkingTimeCalendarProvider</code> that can be
     * used to find when a user can work.
     * @param startDate a <code>Date</code> when the schedule entry this assignment
     * is part of is going to start.
     */
//    public void calculateWorkCompleteDeltaDates(IWorkingTimeCalendarProvider provider, Date startDate) {
//        AssignmentLogCalculationHelper calc = new AssignmentLogCalculationHelper(
//            determineTimeZone(provider),
//            determineCalendarDefinition(provider)
//        );
//
//        //Always recalculate all the work to store.  Don't allow duplicates.
//        daysWorkedToStore = null;
//        boolean workAlreadyPerformed = false;
//
//        //If work complete delta to store is negative we want to get rid of existing
//        //completions, not create new ones
//        if (workCompleteDeltaToStore.compareTo(new TimeQuantity(0, TimeQuantityUnit.HOUR)) > 0) {
//            DBBean db = new DBBean();
//            try {
//                db.prepareStatement(
//                    "select max(work_end) from pn_assignment_work " +
//                    "where object_id = ? and person_id = ?"
//                );
//                db.pstmt.setString(1, getObjectID());
//                db.pstmt.setString(2, getPersonID());
//                db.executePrepared();
//
//                if (db.result.next()) {
//                    if (db.result.getDate(1) != null) {
//                        startDate = DatabaseUtils.getTimestamp(db.result, 1);
//                        workAlreadyPerformed = true;
//                    }
//                }
//            } catch (SQLException sqle) {
//                throw new PnetRuntimeException("Unable to calculate work complete delta dates");
//            } finally {
//                db.release();
//            }
//
//            //Try to find an appropriate place in time to log the work we've done.
//            if (workAlreadyPerformed) {
//                //Work has been done in the past.  Do a continuation of that work.
//                daysWorkedToStore = calc.getDaysWorked(startDate, workCompleteDeltaToStore, getPercentAssignedDecimal());
//            } else {
//                daysWorkedToStore = calc.getDaysWorkedFromEnd(new Date(), workCompleteDeltaToStore, getPercentAssignedDecimal());
//
//                if (startDate != null && (daysWorkedToStore.getTimeRangesWorked().size() == 0 || daysWorkedToStore.getEarliestWorkingTime().before(startDate))) {
//                    //Whoops, we went too far in the past.  Start at the beginning of
//                    //the task and work into the future.
//                    daysWorkedToStore = calc.getDaysWorked(startDate, workCompleteDeltaToStore, getPercentAssignedDecimal());
//                }
//            }
//        }
//    }

    /**
     * Calculates the work that is actually performed by this assignment between the specified
     * date range based on the assignment's start and end dates and working time calendar.
     * <p>
     * A value of zero may be returned if the assignment doesn't work between those dates (for example,
     * they finish earlier, start later, or the date range is not working time).
     * </p>
     * <p>
     * Does not modify any properties of the assignment.
     * </p>
     * @param startDate the starting date
     * @param endDate the ending date
     * @return the amount of work actually performed by the assignment between the specified dates;
     * unit is MINUTES to maintain precision
     */
    public TimeQuantity calculateWorkPerformed(IWorkingTimeCalendarProvider provider, Date startDate, Date endDate) {

        TimeQuantity workPerformed;

        Date adjustedStartDate;
        Date adjustedEndDate;

        if (!getStartTime().before(endDate) || !getEndTime().after(startDate)) {
            // Assignnment start is equal or after end date or assignment end is equal or before start date
            // This means the assignment doesn't begin work until after the range
            // or completes work before the range
            // No work, no duration
            workPerformed = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        } else {
            // The assignment's working period overlaps with this date range

            if (getStartTime().after(startDate)) {
                // Assignment starts work within the date range
                adjustedStartDate = getStartTime();
            } else {
                // Assignment starts work outside the date range
                adjustedStartDate = startDate;
            }

            if (getEndTime().before(endDate)) {
                // Assignment finished work within the date range
                adjustedEndDate = getEndTime();
            } else {
                // Assignment finishes work outside the date range
                adjustedEndDate = endDate;
            }

            // Then figure out how much work is there between those dates
            // For example, 8:00 AM to 5:00 PM for a 50% assignment might result in a value of 4 hours
            workPerformed = new WorkCalculatorHelper(getWorkingTimeCalendar(provider)).getWork(adjustedStartDate, adjustedEndDate, getPercentAssignedDecimal());

        }

        return workPerformed;
    }

    /**
     * Helper method to determine the assignment's time zone based on the fact
     * that the assignment may not have one yet.
     * @param provider the provider from which to locate the default time zone, if necessary
     * @return this assignment's time zone, or the default timezone if the assignment
     * doesn't have one
     */
    public TimeZone determineTimeZone(IWorkingTimeCalendarProvider provider) {
        TimeZone timeZone;

        // It is possible that the person ID is null (when this is a "Default" assignment,
        // not representing a real person)
        if (getPersonID() == null) {
            // No real assignment
            // Use the default time zone
            timeZone = provider.getDefaultTimeZone();

        } else {
            // A real assignment; use the assignee's time zone
            // Some assignments are for unreigstered users; they don't have a time zone
            // yet, so we'll use the default time zone again
            timeZone = getTimeZone();
            if (timeZone == null) {
                timeZone = provider.getDefaultTimeZone();
            }

        }

        return timeZone;
    }

    /**
     * Helper method to determine the assignment's calendar definition based on the fact
     * that the assignment may not have one yet.
     * @param provider the provider from which to get calendar definitions
     * @return the assignment's calendar definition, or the default calendar definition if the assignment
     * doesn't have one
     */
    private WorkingTimeCalendarDefinition determineCalendarDefinition(IWorkingTimeCalendarProvider provider) {
        WorkingTimeCalendarDefinition workingTimeCal = null;

        // It is possible that the person ID is null (when this is a "Default" assignment,
        // not representing a real person)
        if (getPersonID() == null) {
            // No real assignment
            // Use the default calendar definition
            workingTimeCal = provider.getDefault();

        } else {
            // A real assignment; use the assignee's calendar
            
        	
            //If schedule is configured for schedule resource calender get sechedule resource calender.
        	if(SessionManager.getSchedule() != null && SchedulePropertiesHelper.SCHEDULE_RESOURCE_CALENDAR.equals(SessionManager.getSchedule().getResourceCalendar())){
        		workingTimeCal = provider.getForResourceID(getPersonID());
        	} else {
            //else get personal resource working time calendar.
                try {
                    workingTimeCal = ResourceWorkingTimeCalendarProvider.make(new User(new Person(getPersonID())))
                                    .getForResourceID(getPersonID());
                } catch (PersistenceException pnetEx) {
                    Logger.getLogger(ScheduleEntryAssignment.class).error("Error occured while getting resource's " +
                            "personal working time calender : "+ pnetEx.getMessage());
                }
            }
            
            //If still it is not initialized, get schedule default calender. 
            if (workingTimeCal == null) {
                workingTimeCal = provider.getDefault();
            }
        }

        return workingTimeCal;
    }

    //
    // Nested top-level classes
    //

    /**
     * Provides date calculation for an assignment based on the assignment work,
     * percentage and resource's working time calendar.
     */
    private static class AssignmentDateCalculator implements IDateCalculator {

        private final IWorkingTimeCalendarProvider provider;
        private final ScheduleEntryAssignment assignment;

        /**
         * Creates a new date calculator based on an assignment.
         *
         * @param assignment to use when calculatint the date
         * @param provider the provider from which to get the calendar
         */
        private AssignmentDateCalculator(ScheduleEntryAssignment assignment, IWorkingTimeCalendarProvider provider) {

            if (assignment == null || provider == null) {
                throw new NullPointerException("Assignment and provider are required");
            }

            this.assignment = assignment;
            this.provider = provider;
        }

        /**
         * Calculates when the assignment work could be completed based on the assignment percentage and resource's
         * working time calendar.
         *
         * @param startDate the date from which to calculate the finish date
         * @return the finish date; this will be further in the future than the start date
         */
        public Date calculateFinishDate(Date startDate) {
            return calculateDate(startDate, true);
        }

        /**
         * Calculates when the assignment work could be started based on the assignment percentage and resource's
         * working time calendar.
         *
         * @param finishDate the date from which to calculate the start date
         * @return the start date; this will be further in the past than the start date
         */
        public Date calculateStartDate(Date finishDate) {
            return calculateDate(finishDate, false);
        }

        private Date calculateDate(Date date, boolean isForward) {
            TimeQuantity work = (isForward ? this.assignment.getWork() : this.assignment.getWork().negate());
            DateCalculatorHelper dateCalculatorHelper = new DateCalculatorHelper(this.assignment.getWorkingTimeCalendar(this.provider));
            return dateCalculatorHelper.calculateDate(date, work, this.assignment.getPercentAssignedDecimal());

        }
    }

	/**
	 * Returns the task type of the this assignemnt
	 * currently as Task type value
	 *  
	 * @return the taskType of the task that is now assignment
	 */
	public String getTaskType() {
		return taskType;
	}
	
	/**
	 * Sets the boolean value for including task type column
	 * 
	 * @param includeTaskType the includeTaskType to set
	 */
	public void setIncludeTaskType(boolean includeTaskType) {
		this.includeTaskType = includeTaskType;
	}

//    /**
//     * Is coresponding task from share?
//     * @return
//     */
//	public boolean isFromShare() {
//		return isFromShare;
//	}
//
//	/**
//	 * Set if coresponding task is from share
//	 * @param isFromShare
//	 */
//	public void setFromShare(boolean isFromShare) {
//		this.isFromShare = isFromShare;
//	}
//
//	 /**
//     * Is coresponding task shared read only?
//     * @return
//     */
//	public boolean isShareReadOnly() {
//		return isShareReadOnly;
//	}
//
//	/**
//	 * Set if coresponding task is shared read only
//	 * @param isShareReadOnly
//	 */
//	public void setShareReadOnly(boolean isShareReadOnly) {
//		this.isShareReadOnly = isShareReadOnly;
//	}
//    
//    

}
