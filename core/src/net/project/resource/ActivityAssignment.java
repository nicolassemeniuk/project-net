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

 /*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 15404 $
|        $Date: 2007-04-06 20:20:09 +0530 (Fri, 06 Apr 2007) $
|      $Author: sjmittal $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.resource;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import net.project.calendar.PnCalendar;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.hibernate.model.PnAssignment;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.xml.XMLUtils;

/**
 * Provides an assignment to an activity, created for an unplanned work
 * the resource had performed.
 *
 * @author Sachin Mittal
 * @since Version 8.2.0
 */
public class ActivityAssignment extends Assignment {

    /** Name to describe this activity */
    private String name = null;
    
    /** Date on which activity starts. */
    private Date startTime = null;
    
    /** Date on which the activity ends. */
    private Date endTime = null;
    
    /** This indicates the amount of work associated with this activity.   
     * The total amount of work completed on this activity would always be 
     * equal to work since its an adhoc/unplanned activity
     * we would not know how much is do be done before hand.*/
    private TimeQuantity work;

    /** a flag to indicate if this activity was loaded from db or created
     * in memory. This would be used at the time of creating the activity 
     * on the fly and storing it along with updating the timesheet object.
     */
    private boolean loaded = false;
    
    /**
     * Creates an empty ActivityAssignment.
     */
    public ActivityAssignment() {
        super();
    }
    
    /**
     * Checks if the object is created in the db
     * @return if the object is created in db
     */
    public boolean isLoaded() {
        return loaded;
    }
    
    /**
     * Gets the name for this activity 
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for this activity
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Specifies the start time of this activity.
     * @param startTime the start time of the assignment.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the current start time of this activity.
     * @return the start time
     */
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * Specifies the end time of this activity.
     * @param endTime the end time
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Returns the current end time of this activity.
     * @return the end time
     */
    public Date getEndTime() {
        return this.endTime;
    }
    
    /**
     * Get the total amount of work that the resource has performed for this activity
     *
     * @return a <code>TimeQuantity</code> representing the total amount of work
     * that has been done.
     */
    public TimeQuantity getWork() {
        return work;
    }

    /**
     * Set the total amount of work that the resource has performed for this activity
     *
     * @param work a <code>TimeQuantity</code> representing the total amount of
     * work that has been done.
     */
    public void setWork(TimeQuantity work) {
        this.work = work;
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
        xml.append("<actual_start>" + XMLUtils.formatISODateTime(getStartTime()) + "</actual_start>");
        xml.append("<actual_end>" + XMLUtils.formatISODateTime(getEndTime()) + "</actual_end>");
        xml.append("<estimated_end>" + XMLUtils.formatISODateTime(getEndTime()) + "</estimated_end>");
        xml.append("<percent_complete>"+ 100 +"</percent_complete>");
//        xml.append("<task_percent_complete>"+taskPercentComplete+"</task_percent_complete>");
        xml.append("<work>" + work.toShortString(0,2) + "</work>");
        xml.append("<work_complete>" + work.toShortString(0,2) + "</work_complete>");
        xml.append("<work_remaining>" + TimeQuantity.O_HOURS.toShortString(0,2) + "</work_remaining>");


        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        if (getStartTime() != null && cal.startOfDay(getStartTime()).equals(cal.startOfDay(getEndTime()))) {
            xml.append("<one_day_assignment/>");
        }

        xml.append("</assignment>\n");

        return xml.toString();
    }
    
    protected void populateAssignment(ResultSet result) throws SQLException {
        startTime = DatabaseUtils.getTimestamp(result, AssignmentFinder.START_DATE_COL_ID);
        endTime = DatabaseUtils.getTimestamp(result, AssignmentFinder.END_DATE_COL_ID);
        work = DatabaseUtils.getTimeQuantity(result, AssignmentFinder.WORK_COL_ID, AssignmentFinder.WORK_UNITS_COL_ID);
        loaded = true;
    }
    
//    @Override
//    protected void populateAssignment(PnAssignment pnAssignment, String timeZoneId) {
//        startTime = pnAssignment.getStartDate();
//        endTime = pnAssignment.getEndDate();
//        work = new TimeQuantity(pnAssignment.getWork(), TimeQuantityUnit.getForID(pnAssignment.getWorkUnits()));
//    }
    
    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            store(db);
            if (getObjectID() == null || "".equals(getObjectID()) || "0".equals(getObjectID()) ) {
                loaded = false;
                throw new SQLException ("error in TIMESHEET.STORE_ACTIVITY");
            }
            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) { }
            throw new PersistenceException("Unable to store activity.", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Stores the activity.
     * Creates an activity, or updates an activity if one already exists
     * for the current id for the same space id, person id and object id.
     *
     * This version differs from the normal {@link net.project.resource.Assignment#store}
     * because this class knows when it has been modified.
     *
     * @throws SQLException if there is a problem storing the assignment
     */
    public void store(DBBean db) throws SQLException {
        
        if(!loaded) {
            assert name != null  && !"".equals(name.trim()): "Name cannot be blank or null";
            assert getPersonID() != null  && !"".equals(getPersonID()): "Person id cannot be blank or null";

            int index = 0;
            int idIndex;
            db.prepareCall("{call TIMESHEET.STORE_ACTIVITY(?,?,?, ?)}");

            DatabaseUtils.setInteger(db.cstmt, ++index, this.getObjectID());
            DatabaseUtils.setInteger(db.cstmt, ++index, this.getPersonID());
            DatabaseUtils.setString(db.cstmt, ++index, this.name);

            db.cstmt.registerOutParameter((idIndex = ++index), Types.INTEGER);
            db.executeCallable();

            this.setObjectID(Integer.toString(db.cstmt.getInt(idIndex)));
            loaded = true;
        } else {
            assert work != null && work.compareTo(TimeQuantity.O_HOURS) >= 0 : "Work cannot be negative or null";
            //owner is self
            if(getAssignorID() == null)
                setAssignorID(getPersonID());
            super.storeAssignment(db, startTime, endTime, startTime, endTime, null, work, work, new BigDecimal(1.00000));
        }
    }

    /**
     * Determine whether this <code>ActivityAssignment</code> object is
     * equal to another object by checking their types and internal fields.
     *
     * @param o a <code>Object</code> which is to be compared to the current one.
     * @return <code>true</code> if the two objects are identical.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActivityAssignment)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final ActivityAssignment activityAssignment = (ActivityAssignment) o;

        if (endTime != null ? !endTime.equals(activityAssignment.endTime) : activityAssignment.endTime != null) {
            return false;
        }
        if (startTime != null ? !startTime.equals(activityAssignment.startTime) : activityAssignment.startTime != null) {
            return false;
        }
        if (work != null ? !work.equals(activityAssignment.work) : activityAssignment.work != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 29 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 29 * result + (work != null ? work.hashCode() : 0);
        return result;
    }

    public Object clone() {
        ActivityAssignment clone = new ActivityAssignment();
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
        clone.setPrimaryOwner(isPrimaryOwner());
        clone.setSpaceID(getSpaceID());
        clone.setWork(work);
        clone.setSpaceName(getSpaceName());
        clone.setObjectType(getObjectType());
        clone.setPersonName(getPersonName());

        return clone;
    }


}
