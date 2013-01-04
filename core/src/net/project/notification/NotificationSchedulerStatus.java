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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.notification;

import java.sql.SQLException;
import java.util.Date;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Provides status about the Notification Scheduler.
 * Example usage: <code><pre>
 * NotificationSchedulerStatus status = new NotificationSchedulerStatus();
 * status.setSchedulerID(NotificationSchedulerStatus.DEFAULT);
 * status.load();
 * status.getXML();
 * </pre></code>
 */
public class NotificationSchedulerStatus implements IXMLPersistence {

    /** The default scheduler's id, currently <code>1</code>. */
    public static final String DEFAULT_SCHEDULER_ID = "1";

    /** Query to delete the existing status row for an id. */
    private static final String STATUS_DELETE_QUERY =
            "delete from pn_notification_sched_status where scheduler_id = ? ";

    /** Query to insert the status entry for an id. */
    private static final String STATUS_INSERT_QUERY =
            "insert into pn_notification_sched_status " +
            "(scheduler_id, last_check_datetime) " +
            "values (?, sysdate) ";

    /** Query to load the status for an id. */
    private static final String STATUS_LOAD_QUERY =
            "select scheduler_id, last_check_datetime " +
            "from pn_notification_sched_status " +
            "where scheduler_id = ? ";

    /**
     * Logs a check by the Notification Scheduler for new notifications to make.
     * @param id the id of the scheduler performing the check
     */
    public static void logCheck(String id) {
        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);

            // We first delete the existing row, then insert the new row
            // There is no point in checking for existence, then updating or
            // inserting since that still requires two round-trips

            int index = 0;
            db.prepareStatement(STATUS_DELETE_QUERY);
            db.pstmt.setString(++index, id);
            db.executePrepared();

            index = 0;
            db.prepareStatement(STATUS_INSERT_QUERY);
            db.pstmt.setString(++index, id);
            db.executePrepared();

            db.commit();

        } catch (SQLException sqle) {
        	Logger.getLogger(NotificationSchedulerStatus.class).error("NotificationSchedulerStatus.logCheck() threw an SQLException: " + sqle);
            // We do not re-throw error to avoid situation where this failure
            // actually disables the notification scheduler
            // A real database error will manifest itself elsewhere

        } finally {
            try {
                db.rollback();
            } catch (SQLException sqle) {
                // Simply release
            }

            db.release();
        }
    }


    /** The id of the scheduler to which this status pertains. */
    private String id = null;

    /** The last check datetime of this scheduler. */
    private Date lastCheckDatetime = null;


    /**
     * Creates an empty NotificationSchedulerStatus.
     */
    public NotificationSchedulerStatus() {
        // Nothing to initialze
    }


    /**
     * Sets the id of the scheduler to which this status pertains.
     * @param id notification scheduler id
     * @see #getSchedulerID
     */
    public void setSchedulerID(String id) {
        this.id = id;
    }


    /**
     * Returns the scheduler id to which this status pertains.
     * @return the notification scheduler id
     * @see #setSchedulerID
     */
    private String getSchedulerID() {
        return this.id;
    }


    /**
     * Sets the datetime of the last check by this scheduler.
     * @param date the last check datetime
     * @see #getLastCheckDatetime
     */
    private void setLastCheckDatetime(Date date) {
        this.lastCheckDatetime = date;
    }


    /**
     * Indicates the date and time of the last check by this scheduler.
     * @return the last check datetime
     */
    private Date getLastCheckDatetime() {
        return this.lastCheckDatetime;
    }


    /**
     * Loads the status for the Notification Scheduler with the set id.
     * @precondition Assumes {@link #setSchedulerID} has been called
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {
        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(STATUS_LOAD_QUERY);
            db.pstmt.setString(++index, getSchedulerID());
            db.executePrepared();

            if (db.result.next()) {
                setLastCheckDatetime(new Date(db.result.getTimestamp("last_check_datetime").getTime()));

            } else {
                throw new PersistenceException("Notification Scheduler Status found no data for id: " + getSchedulerID());

            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Notification Scheduler Status load operation failed", sqle);

        } finally {
            db.release();

        }

    }


    /**
     * Returns this object's XML representation, including the XML version tag.
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXMLBody
     * @see net.project.persistence.IXMLPersistence#XML_VERSION
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }


    /**
     * Returns this object's XML representation, without the XML version tag.
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<NotificationSchedulerStatus>");
        xml.append("<SchedulerID>" + XMLUtils.escape(getSchedulerID()) + "</SchedulerID>");
        xml.append("<LastCheckDatetime>" + XMLUtils.formatISODateTime(getLastCheckDatetime()) + "</LastCheckDatetime>");
        xml.append("</NotificationSchedulerStatus>");
        return xml.toString();
    }

}
