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
+----------------------------------------------------------------------*/
package net.project.news;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.notification.NotificationManager;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 *  This class handles the notification events for Form.
 *
 * @author deepak
 */
public class NewsEvent extends net.project.notification.Event {

    /**
     * Stores the News Event in the Database
     *
     * @exception PersistenceException     thrown ,in case ,  anything goes wrong
     */
    public void store() throws PersistenceException {
        int index = 0;

        DBBean db = new DBBean();
        try {
            db.prepareCall("begin  NEWS.LOG_EVENT (?,?,?,?,?); end;");
            db.cstmt.setString(++index, getTargetObjectID());
            db.cstmt.setString(++index, getInitiatorID());
            db.cstmt.setString(++index, getEventType());
            db.cstmt.setString(++index, this.name);
            db.cstmt.setString(++index, getDescription());
            db.executeCallable();
            NotificationManager.notify(this);

        } catch (SQLException sqle) {
        	Logger.getLogger(NewsEvent.class).error("NewsEvent.store(): User: " + getInitiatorID() + ", unable to log event: " + sqle);
            throw new PersistenceException("NewsEvent.store(): User: " + getInitiatorID() + ", unable to log event: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

}
