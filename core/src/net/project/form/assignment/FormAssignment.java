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
package net.project.form.assignment;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import net.project.base.ObjectType;
import net.project.calendar.PnCalendar;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.hibernate.model.PnAssignment;
import net.project.persistence.PersistenceException;
import net.project.resource.Assignment;
import net.project.resource.AssignmentFinder;
import net.project.resource.AssignmentManager;
import net.project.resource.AssignmentRoster;
import net.project.schedule.ScheduleTimeQuantity;
import net.project.security.SessionManager;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.xml.XMLUtils;

public class FormAssignment extends Assignment {

    /**
     * The timeZone of the assignee.
     * This is used when calculating working times.
     */
    private TimeZone timeZone;
    
    /** This indicates the amount of work associated with this assignment. */
    private TimeQuantity work;
    /** The total amount of work completed on this assignment. */
    private TimeQuantity workComplete;
    
    /** Date on which assignment is to start. */
    private Date startTime = null;
    /** Date on which assignment completes. */
    private Date endTime = null;
    /** Date on which we expected the work to complete. */
    private Date estimatedFinish = null;
    
    /** a flag to indicate if this form assignment was loaded from db or created
     * in memory. This would be used at the time of creating the form assignment 
     * on the fly and storing it along with updating the form and timesheet object.
     */
    private boolean loaded = false;

    /**
     * Checks if the object is created in the db
     * @return if the object is created in db
     */
    public boolean isLoaded() {
        return loaded;
    }
    
    public static FormAssignment makeAssignmentFromRoster(String formDataId, String formDataName, String spaceId, AssignmentRoster.Person person) {
        FormAssignment formAssignment = new FormAssignment();
        formAssignment.setObjectID(formDataId);
        formAssignment.setObjectName(formDataName);
        formAssignment.setPersonID(person.getID());
        formAssignment.setPersonName(person.getDisplayName());
        formAssignment.setSpaceID(spaceId);
        //load data from any previously added assignment work entry
        if(person.getWorkComplete() != null) {
            formAssignment.setWork(person.getWorkComplete());//currently make the work default to work complete
            formAssignment.setWorkComplete(person.getWorkComplete());
            formAssignment.startTime = person.getDateStart();
            formAssignment.endTime = person.getDateEnd();
        }
        return formAssignment;
    }
    
    //form name cannot be null
    public FormAssignment() {
        super();
        setObjectType(ObjectType.FORM_DATA);
        work = TimeQuantity.O_HOURS;
        workComplete = TimeQuantity.O_HOURS;
    }
    /**
     * Returns the XML representation of a ScheduleEntryAssignment.
     * @return
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append("<assignment>\n");
        xml.append(getXMLElements());
        // not xml formatted to 0, 1 so if later changed to xml format the UI might
        xml.append("<loaded>" + loaded + "</loaded>");
        xml.append("<start_time>" + XMLUtils.formatISODateTime(getStartTime()) + "</start_time>");
        xml.append("<end_time>" + XMLUtils.formatISODateTime(getEndTime()) + "</end_time>");
        xml.append("<due_date>" + XMLUtils.formatISODateTime(getEstimatedFinish()) + "</due_date>");
        xml.append("<percent_complete>" + getPercentComplete().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "</percent_complete>");
        xml.append("<work>" + getWork().toShortString(0,2) + "</work>");
        xml.append("<work_complete>" + getWorkComplete().toShortString(0,2) + "</work_complete>");
        xml.append("<work_remaining>" + getWorkRemaining().toShortString(0,2) + "</work_remaining>");

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
     * Get a formatted String representation of the start time
     *
     * @return a <code>String</code> value containing a date formatted to the
     * current user's preferences which indicates the planned start time of the
     * task.
     */
    public String getStartTimeString() {
        return SessionManager.getUser().getDateFormatter().formatDate(startTime);
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
     * Get a formatted String representation of the end time
     *
     * @return a <code>String</code> value containing the planned end time of
     * this task formatted for the user's time preferences.
     */
    public String getEndTimeString() {
        return ((SessionManager.getUser()).getDateFormatter()).formatDate(endTime);
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
     * completed.
     *
     * @param estimatedFinish a <code>Date</code> which estimates when the
     * assignment will be complete.
     */
    public void setEstimatedFinish(Date estimatedFinish) {
        this.estimatedFinish = estimatedFinish;
    }
    
    /**
     * Get the total amount of work that the resource has performed for this form data.
     *
     * @return a <code>TimeQuantity</code> representing the total amount of work
     * that has been done.
     */
    public TimeQuantity getWork() {
        return work;
    }

    /**
     * Set the total amount of work that the resource has performed for this for data.
     *
     * @param work a <code>TimeQuantity</code> representing the total amount of
     * work that has been done.
     */
    public void setWork(TimeQuantity work) {
        this.work = work;
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
        return this.workComplete;
    }
    
    /**
     * Set the amount of work that the assignee has completed thus far.
     *
     * @param workComplete a <code>TimeQuantity</code> representing the total
     * amount of work that the assignee has completed thus far.
     */
    public void setWorkComplete(TimeQuantity workComplete) {
        this.workComplete = workComplete;
    }

    /**
     * Indicates if the work is complete for this assignment.
     *
     * @return a <code>boolean</code> indicating if the work is complete for this
     * assignment.
     */
    public boolean isComplete() {
        return work.equals(workComplete);
    }

    /**
     * Get the amount of work remaining to complete this assignment.
     *
     * @return a <code>TimeQuantity</code> representing the amount of work
     * necessary to complete this assignment.
     */
    public TimeQuantity getWorkRemaining() {
        TimeQuantity workCompleteToCheck = getWorkComplete();
        TimeQuantity workToCheck = getWork();
        if (workToCheck.compareTo(workCompleteToCheck) < 0) {
            //More work has been completed than was assigned, there is no more work to do.
            return new TimeQuantity(0, TimeQuantityUnit.HOUR);
        } else {
            return workToCheck.subtract(workCompleteToCheck);
        }
    }
    
    /**
     * Returns this assignment's percentage complete as a percent value,
     * currently interpreted as percentage of work complete.

     * @return the percent complete where <code>1.0</code> = 100% and <code>0.5</code> = 50%
     */
    public BigDecimal getPercentComplete() {
        if(work.getAmount().signum() != 0)
            return workComplete.divide(work, 5, BigDecimal.ROUND_HALF_UP);
        else 
            return new BigDecimal(0.00000);
    }
    
    /**
     * Returns this assignment's percentage complete as the formatted percent,
     * currently interpreted as percentage of work complete.

     * @return a <code>String</code> value containing the percent complete
     * including percent sign (e.g.: "95%") formatted for the user's locale
     */
    public String getPercentCompleteString() {
        return NumberFormat.getInstance().formatPercent(getPercentComplete().doubleValue(), 0, 2);
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

    protected void populateAssignment(ResultSet result) throws SQLException {
        startTime = DatabaseUtils.getTimestamp(result, AssignmentFinder.START_DATE_COL_ID);
        endTime = DatabaseUtils.getTimestamp(result, AssignmentFinder.END_DATE_COL_ID);
        estimatedFinish = DatabaseUtils.getTimestamp(result, AssignmentFinder.ESTIMATED_FINISH_COL_ID);
        work = DatabaseUtils.getTimeQuantity(result, AssignmentFinder.WORK_COL_ID, AssignmentFinder.WORK_UNITS_COL_ID);
        workComplete = DatabaseUtils.getTimeQuantity(result, AssignmentFinder.WORK_COMPLETE_COL_ID, AssignmentFinder.WORK_COMPLETE_UNITS_COL_ID);
        loaded = true;
    }
    
//    @Override
//    protected void populateAssignment(PnAssignment pnAssignment, String timeZoneId) {
//        startTime = pnAssignment.getStartDate();
//        endTime = pnAssignment.getEndDate();
//        estimatedFinish = pnAssignment.getEstimatedFinish();
//        work = new TimeQuantity(pnAssignment.getWork(), TimeQuantityUnit.getForID(pnAssignment.getWorkUnits()));
//        workComplete = new TimeQuantity(pnAssignment.getWorkComplete(), TimeQuantityUnit.getForID(pnAssignment.getWorkCompleteUnits()));
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
            throw new PersistenceException("Unable to store form assignment.", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Stores the form assignment.
     * Creates an form assignment, or updates an form assignment if one already exists
     * for the current id for the same space id, person id and object id.
     *
     * This version differs from the normal {@link net.project.resource.Assignment#store}
     * because this class knows when it has been modified.
     *
     * @throws SQLException if there is a problem storing the assignment
     */
    public void store(DBBean db) throws SQLException {
        
        assert work != null && work.compareTo(TimeQuantity.O_HOURS) >= 0 : "Work cannot be negative or null";
        assert workComplete != null && workComplete.compareTo(TimeQuantity.O_HOURS) >= 0 : "Work Complete cannot be negative or null";
        assert work.compareTo(workComplete) >= 0 : "Work cannot be less that Work Complete";
        assert endTime.compareTo(startTime) >= 0 : "End Time cannot be less that Start Time";
        BigDecimal percentComplete = null;
        if (workComplete != null && work != null) {
            if (work.getAmount().signum() != 0) {
                percentComplete = workComplete.divide(work, 5, BigDecimal.ROUND_HALF_UP);
            } 
        }
        super.storeAssignment(db, startTime, endTime, startTime, endTime, estimatedFinish, work, workComplete, percentComplete);
        loaded = true;
    }
    
    /**
     * Soft delete the form assignment from the database (change the record status) and marks loaded = false.
     * Requires person ID and object ID to be set.
     * @throws IllegalStateException if person ID or object ID is null
     */
    public void remove() throws PersistenceException {
        super.remove();
        loaded = false;
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
        if (!(o instanceof FormAssignment)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final FormAssignment formAssignment = (FormAssignment) o;

        if (endTime != null ? !endTime.equals(formAssignment.endTime) : formAssignment.endTime != null) {
            return false;
        }
        if (estimatedFinish != null ? !estimatedFinish.equals(formAssignment.estimatedFinish) : formAssignment.estimatedFinish != null) {
            return false;
        }
        if (startTime != null ? !startTime.equals(formAssignment.startTime) : formAssignment.startTime != null) {
            return false;
        }
        if (work != null ? !work.equals(formAssignment.work) : formAssignment.work != null) {
            return false;
        }
        if (workComplete != null ? !workComplete.equals(formAssignment.workComplete) : formAssignment.workComplete != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 29 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 29 * result + (estimatedFinish != null ? estimatedFinish.hashCode() : 0);
        result = 29 * result + (work != null ? work.hashCode() : 0);
        result = 29 * result + (workComplete != null ? workComplete.hashCode() : 0);
        return result;
    }

    public Object clone() {
        FormAssignment clone = new FormAssignment();
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
        clone.setEstimatedFinish(getEstimatedFinish());
        clone.setPrimaryOwner(isPrimaryOwner());
        clone.setSpaceID(getSpaceID());
        clone.setWork(work);
        clone.setSpaceName(getSpaceName());
        clone.setObjectType(getObjectType());
        clone.setPersonName(getPersonName());
        clone.setTimeZone(timeZone);
        clone.setWorkComplete(workComplete);

        return clone;
    }


}
