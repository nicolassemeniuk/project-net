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

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.notification.NotificationManager;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 *  This class handles the notification events for Task
 *
 * @author deepak
 */
public class TaskEvent extends net.project.notification.Event {

    /**
     * Stores the Task Event in the Database
     *
     * @exception PersistenceException thrown ,in case ,  anything goes wrong
     */
    public void store() throws PersistenceException {

        DBBean db = new DBBean();
        try {
            store(db);
        } catch (SQLException sqle) {
        	Logger.getLogger(TaskEvent.class).debug("TaskEvent.store(): User: " + getInitiatorID() + ", unable to log event: " + sqle);
            throw new PersistenceException("TaskEvent.store(): User: " + getInitiatorID() + ", unable to log event: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    public void store(DBBean db) throws SQLException {
        db.prepareCall("begin  TASK.LOG_EVENT (?,?,?,?,?); end;");
        int index = 0;
        db.cstmt.setString(++index, getTargetObjectID());
        db.cstmt.setString(++index, getInitiatorID());
        db.cstmt.setString(++index, getEventType());
        db.cstmt.setString(++index, this.name);
        db.cstmt.setString(++index, getDescription());
        db.executeCallable();
        NotificationManager.notify(this);
    }
}
