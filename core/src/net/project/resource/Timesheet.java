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

/*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|    $Revision: 15748 $
|    $Date: 2007-03-01 20:20:09 +0530 (Thu, 01 Mar 2007) $
|    $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.resource;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.project.base.Identifiable;
import net.project.base.ObjectType;
import net.project.base.RecordStatus;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.TimeQuantity;

import org.apache.log4j.Logger;

/**
 * Represents a timesheet for a resource.
 *
 * @author Sachin Mittal
 * @since Version 8.2.0
 */
public class Timesheet implements IXMLPersistence, IJDBCPersistence, Identifiable, Cloneable, Serializable {


	private String id;

	private String person_id;

	private String status_id;

	private Date start_date;

	private Date end_date;

	private Date date_submitted;

	private Date date_approve_reject;

	private String approve_reject_by_id;

	private String comments;

	private RecordStatus recordStatus;

    private TimeQuantity work;

	private TimesheetStatus timesheetStatus;

    private List<Assignment> assignmentList;

	private boolean isLoaded;

    /**
     * Creates a new instance of a timesheet.
     */
    public Timesheet() {
    }

    /**
     * Clears out any information stored in this timesheet.
     */
    public void clear() {
        id = null;
        person_id = null;
        status_id = null;
        start_date = null;
        end_date = null;
        date_submitted = null;
        date_approve_reject = null;
        approve_reject_by_id = null;
        comments = null;
        recordStatus = null;
        work = null;
        timesheetStatus = null;
        assignmentList = null;
        isLoaded = false;
    }

    /**
     * Gets the id of this timesheet.
     *
     * @return String the timesheet database id
     */
	public String getID() {
		return id;
	}

	public String getType() {
		return ObjectType.TIMESHEET;
	}

    /**
     * Sets the id of this timesheet.
     *
     * @param id the timesheet database id
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Check if the timesheet is loaded.
     *
     * @return a <code>boolean</code> indicating if the timesheet is loaded.
     */
    public boolean isLoaded() {
        return isLoaded;
    }

	/**
	 * Gets the start date for the timesheet
	 *
	 * @return Returns the start_date.
	 */
	public Date getStartDate() {
		return start_date;
	}

    /**
     * Get a formatted String representation of the start date
     *
     * @return a <code>String</code> value containing a date formatted to the
     * current user's preferences which indicates the start date of the timesheet
     */
    public String getStartDateString() {
        return SessionManager.getUser().getDateFormatter().formatDate(start_date);
    }

	/**
	 * Sests the start date for the timesheet
	 *
	 * @param start_date The start_date to set.
	 */
	public void setStartDate(Date start_date) {
		this.start_date = start_date;
	}

	/**
	 * Gets the end date for this timesheet
	 *
	 * @return Returns the end_date.
	 */
	public Date getEndDate() {
		return end_date;
	}

    /**
     * Get a formatted String representation of the end date
     *
     * @return a <code>String</code> value containing a date formatted to the
     * current user's preferences which indicates the end date of the timesheet
     */
    public String getEndDateString() {
        return SessionManager.getUser().getDateFormatter().formatDate(end_date);
    }


	/**
	 * Sets the end date for this timesheet
	 *
	 * @param end_date The end_date to set.
	 */
	public void setEndDate(Date end_date) {
		this.end_date = end_date;
	}

    /**
     * Gets the date when the timesheet was approved or rejected
     *
     * @return the date_approve_reject
     */
    public Date getDateApproveReject() {
        return date_approve_reject;
    }

    /**
     * Sets the date when the timesheet was approved or rejected
     * ie when the staus was set to <code>TimesheetStatus</code>
     * approved or rejected
     *
     * @param date_approve_reject the date_approve_reject to set
     * @see TimesheetStatus#APPROVED
     * @see TimesheetStatus#REJECTED
     */
    public void setDateApproveReject(Date date_approve_reject) {
        this.date_approve_reject = date_approve_reject;
    }

    /**
     * Gets the date when the timesheet was submitted
     *
     * @return the date_submitted
     */
    public Date getDateSubmitted() {
        return date_submitted;
    }

    /**
     * Sets the date when the timesheet was submitted
     * ie when the status was set to <code>TimesheetStatus</code>
     * submitted.
     *
     * @param date_submitted the date_submitted to set
     * @see TimesheetStatus#SUBMITTED
     */
    public void setDateSubmitted(Date date_submitted) {
        this.date_submitted = date_submitted;
    }

    /**
	 * Gets the person id who is creating the timesheet
	 *
	 * @return Returns the person_id.
	 */
	public String getPersonId() {
		return person_id;
	}

	/**
	 * Sets the person id who is creating the timesheet
	 *
	 * @param person_id The person_id to set.
	 */
	public void setPersonId(String person_id) {
		this.person_id = person_id;
	}

	/**
     * Gets the person id who approved or rejected the timesheet.
     * To know if the person approved or rejected the timesheet check
     * the status of the timesheet.
     *
     * @return the approve_reject_by_id
     * @see #getTimesheetStatus()
     * @see #getStatusId()
     */
    public String getApproveRejectById() {
        return approve_reject_by_id;
    }

    /**
     * Sets the person id who approved or rejected the timesheet.
     * This would be set along with the status of the timesheet.
     *
     * @param approve_reject_by_id the approve_reject_by_id to set
     * @see #setTimesheetStatus(TimesheetStatus)
     */
    public void setApproveRejectById(String approve_reject_by_id) {
        this.approve_reject_by_id = approve_reject_by_id;
    }

	/**
	 * Gets the status of the timesheet
	 *
	 * @return Returns the status_id.
	 */
	public String getStatusId() {
		return status_id;
	}
	/**
	 * Sets the status of the timesheet
	 *
	 * @param status_id The status_id to set.
	 */
	public void setStatusId(String status_id) {
		this.status_id = status_id;
		this.timesheetStatus = TimesheetStatus.getForID(this.status_id);
	}


	/**
     * Returns the total work logged in the timesheet
     *
     * @return the work
     */
    public TimeQuantity getWork() {
        return work;
    }

    /**
     * Sets the total work logged in the timesheet
     *
     * @param work the work to set
     */
    public void setWork(TimeQuantity work) {
        this.work = work;
    }

    /**
	 * Gets the status object for the timesheet
	 *
	 * @return Returns the timesheetStatus.
	 */
	public TimesheetStatus getTimesheetStatus() {
		return timesheetStatus;
	}

	/**
     * Gets the comments for the timesheet
     *
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the comments for the timesheet.
     * These would be set when someone approves or rejects a timesheet.
     *
     * @param comments the comments to set
     * @see TimesheetStatus#APPROVED
     * @see TimesheetStatus#REJECTED
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
	 * Sets the status object for the timesheet
	 *
	 * @param timesheetStatus The timesheetStatus to set.
	 */
	public void setTimesheetStatus(TimesheetStatus timesheetStatus) {
		this.timesheetStatus = timesheetStatus;
		this.status_id = timesheetStatus.getID();
	}


    /**
     * Gets the record status of the timesheet
     *
     * @return the recordStatus
     */
    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    /**
     * Sets the records status of the timesheet.
     *
     * @param recordStatus the recordStatus to set
     */
    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    /**
     * Adds an assignment to the timesheet
     *
     * @param assignment the assignment to add
     */
    public void addAssignment(Assignment assignment)
    {
        if(assignmentList == null)
            assignmentList = new ArrayList<Assignment>();
        assignmentList.add(assignment);
    }

    /**
     * Sets the list of assignments for the timesheet
     *
     * @param assignmentList assignment list to add
     */
    public void setAssignmentList(List assignmentList) {
        this.assignmentList = assignmentList;
    }

    /**
     * Gets the list of assignments for the timesheet
     *
     * @return assignment list to return
     */
    public List getAssignmentList() {
       return assignmentList;
    }

    /***************************************************************************************
     *  Implementing IXMLPersistence
     ***************************************************************************************/

    /**
     * Converts the object to XML representation.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
	public String getXML() throws SQLException {
		 return (IXMLPersistence.XML_VERSION + getXMLBody());
	}

    /**
     * Returns the XML representation of a Timesheet.
     * @return
     */
	public String getXMLBody() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

    /***************************************************************************************
     *  Implementing IJDBCPersistence
     ***************************************************************************************/

    /**
     * Load the timesheet properties. Note: This does not load the timesheet's
     * entries.
     *
     * @throws PersistenceException if there is a problem loading the timesheet
     * properties.
     */
	public void load() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            load(db);
        } finally {
            db.release();
        }

	}

	/**
     * Load the schedule properties. Note: This does not load the schedule's
     * entries.
     *
     * @throws SQLException if there is a problem loading the schedule
     * properties.
     */
    public void load(DBBean db) throws PersistenceException {
        String query;

        this.isLoaded = false;
        // TODO create the object from query via TimesheetFinder
        TimesheetFinder tf = new TimesheetFinder();
        this.isLoaded = tf.findByID(this.id, this);
        if (!isLoaded()) {
            throw new PersistenceException("could not load timesheet");
        }
    }

    /**
     * Stores the timesheet for a resource or updates status,comments if one already exists.
     * @throws PersistenceException if there is a problem storing the assignment
     */
	public void store() throws PersistenceException, SQLException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            store(db);
            if (getID() == null || "".equals(getID()) || "0".equals(getID()) ) {
                throw new SQLException ("error in TIMESHEET.STORE_TIMESHEET");
            }
            updateAssignments(db);
            db.commit();
        } catch (SQLException sqle) {
        	Logger.getLogger(Assignment.class).error("Timesheet.java: Error storing timesheet: " + sqle);
            throw new PersistenceException("Error storing timesheet" + sqle.getLocalizedMessage(), sqle);
        } finally {
            db.release();
        }

	}

    public void store(DBBean db) throws SQLException {

        assert work != null && work.compareTo(TimeQuantity.O_HOURS) >= 0 : "Work cannot be negative or null";

        int index = 0;
        int idIndex;
        db.prepareCall("{call TIMESHEET.STORE_TIMESHEET(?,?,?, ?,?, ?,?, ?,?, ?,?, ?, ?)}");

        DatabaseUtils.setInteger(db.cstmt, ++index, this.id);
        DatabaseUtils.setInteger(db.cstmt, ++index, this.person_id);
        DatabaseUtils.setInteger(db.cstmt, ++index, this.approve_reject_by_id);

        DatabaseUtils.setTimestamp(db.cstmt, ++index, this.start_date);
        DatabaseUtils.setTimestamp(db.cstmt, ++index, this.end_date);

        db.cstmt.setDouble(++index, this.work.getAmount().doubleValue());
        db.cstmt.setInt(++index, this.work.getUnits().getUniqueID());

        if (this.timesheetStatus.equals(TimesheetStatus.SUBMITTED)) {
            this.date_submitted = new Date();
        }
        DatabaseUtils.setTimestamp(db.cstmt, ++index, this.date_submitted);

        if (this.timesheetStatus.equals(TimesheetStatus.APPROVED) || this.timesheetStatus.equals(TimesheetStatus.REJECTED)) {
            this.date_approve_reject = new Date();
        }
        DatabaseUtils.setTimestamp(db.cstmt, ++index, this.date_approve_reject);


        DatabaseUtils.setString(db.cstmt, ++index, this.recordStatus.getID());
        DatabaseUtils.setInteger(db.cstmt, ++index, this.timesheetStatus.getID());

        DatabaseUtils.setString(db.cstmt, ++index, this.comments);

        db.cstmt.registerOutParameter((idIndex = ++index), Types.INTEGER);
        db.executeCallable();

        setID(Integer.toString(db.cstmt.getInt(idIndex)));
    }

    private void updateAssignments(DBBean db) throws SQLException {

        db.prepareCall("{call TIMESHEET.INSERT_ASSIGNMENT(?,?,?)}");

        for (int i = 0; i < assignmentList.size(); i++) {
            int index = 0;
            Assignment assignment = assignmentList.get(i);

            DatabaseUtils.setInteger(db.cstmt, ++index, this.id);
            DatabaseUtils.setInteger(db.cstmt, ++index, assignment.getSpaceID());
            DatabaseUtils.setInteger(db.cstmt, ++index, assignment.getObjectID());

            db.executeCallable();
        }
    }

    /**
     * Soft delete the timesheet from the database (change the record status).
     * Requires object ID to be set.
     * @throws IllegalStateException if object ID is null
     */
	public void remove() throws PersistenceException {

        if (this.id == null) {
            throw new NullPointerException("timesheet id is null");
        }
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            int index = 0;
            db.prepareCall("{call TIMESHEET.REMOVE_TIMESHEET(?,?)}");
            DatabaseUtils.setInteger(db.cstmt, ++index, this.id);
            DatabaseUtils.setString(db.cstmt, ++index, RecordStatus.DELETED.getID());
            db.executeCallable();
            db.commit();
        } catch (SQLException sqle) {
            if (db.connection != null) {
                try {
                    db.connection.rollback();
                } catch (SQLException sqle2) {
                }
            }
            this.isLoaded = false;
            Logger.getLogger(Timesheet.class).error("Timesheet.remove() threw an SQL exception: " + sqle);
            throw new PersistenceException("Timesheet remove failed. " + sqle.getLocalizedMessage(), sqle);
        } finally {
            db.release();
        }
	}


}
