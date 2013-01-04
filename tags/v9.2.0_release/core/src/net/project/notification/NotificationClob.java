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
|
+----------------------------------------------------------------------*/
package net.project.notification;

import java.sql.SQLException;

import net.project.database.Clob;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Combine all locations that were creating an entry into the PN_NOTIFICATION_CLOB
 * table into a single location.
 *
 * @author Matthew Flower (11/16/2001)
 * @since Gecko
 */
public class NotificationClob {
    /**
     * Create the clob that will store the notification XML.
     *
     * @see #createClob(String, DBBean)
     * @param data a <code>String</code> value containing the XML that will
     * format the notification.
     * @return a <code>String</code> value containing the id of the newly created
     * row in PN_NOTIFICATION_CLOB
     * @exception SQLException if an error occurs while trying to create the
     * clob.
     */
    public synchronized static String createClob(String data) throws SQLException {
        DBBean db = new DBBean();

        try {
            //All functionality is implemented in the overloaded method, create a
            //db for that function to use and pass it in.
            return createClob(data, db);
        } finally {
            db.release();
            db = null;
        }
    }

    /**
     * Create the clob that will store the notification XML inside of an existing
     * transaction.
     *
     * @see #createClob(String)
     * @param data a <code>String</code> value containing the XML that will
     * format the notification.
     * @param clobBean a <code>DBBean</code> value
     * @return a <code>String</code> value containing the id of the newly created
     * row in PN_NOTIFICATION_CLOB
     * @exception SQLException if an error occurs while trying to create the
     * clob.
     */
    public synchronized static String createClob(String data, DBBean clobBean) throws SQLException {
        Clob newClob = null;
        String clobID = null;

        try {
            clobBean.setClobTableName("pn_notification_clob");

            newClob = clobBean.createClob(data);

            clobID = newClob.getID();
            newClob.store();
            newClob.commit();
        } catch (PersistenceException pe) {
            throw (new SQLException("Notification.createClob threw a SQL exception: " + pe));
        }

        return clobID;
    }
    
    /**
     * Deletes all orphaned notification clobs that appear in the database.
     *
     * Method to delete notification clob entries that do not have a corresponding
     * entry in the pn_scheduled_subscription or pn_notification table.
     *
     * In reality, we really shouldn't need to have such a method, but for reasons
     * that we haven't been able to unravel quite yet, some operations seem to not
     * delete notification clobs even though we tell them to.
     *
     * These records pile up and eventually take up quite a bit of space.  We are
     * opting to delete them on a scheduled basis to prevent them from piling up
     * until we decide why they aren't being deleted.
     * 
     * @return a <code>int</code> value indicating the number of clobs deleted.
     * @throws PersistenceException if an error has occurred while deleting the
     * clobs.
     */
    public synchronized static int delete() throws PersistenceException {
        int totalClobsDeleted = 0;

        DBBean db = new DBBean();
        try {
            db.prepareStatement(
                    "delete from pn_notification_clob " +
                    "where object_id in " +
                    "( " +
                    "  select object_id from pn_notification_clob nc " +
                    "  where " +
                    "    not exists (select 1 from pn_scheduled_subscription ss where ss.target_object_clob_id = nc.object_id) " +
                    "    and not exists (select 1 from pn_notification n where n.notification_clob_id = nc.object_id) " +
                    ")");
            totalClobsDeleted = db.executePreparedUpdate();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to delete orphaned notification clobs.  SQLException:" + sqle, sqle);
        } finally {
            db.release();
        }
        return totalClobsDeleted;
    }

    /**
     * Delete all clobs associated to a certain notification id.
     *
     * @param notificationID a <code>String</code> value containing the notification id
     * that has outdated notification clobs associated to it.
     * @exception PersistenceException if an error occurs while trying to delete the
     * clobs.
     */
    public synchronized static void deleteClobsForNotification(String notificationID) throws PersistenceException {
        DBBean db = new DBBean();
        String clobIdToDelete = null;
        
        try {
            db.prepareStatement("select notification_clob_id from pn_notification where notification_id = ?");
            db.pstmt.setString(1, notificationID);

            db.executePrepared();

            if (db.result.next()) {
                //Now delete that clob
                clobIdToDelete = db.result.getString("notification_clob_id");
            }
            //sjmittal: we clear the notification queue also here so as to keep things synchronized
            clearNotificationFromDBQueue(notificationID);
        } catch (SQLException sqle) {
        	Logger.getLogger(NotificationClob.class).error("Unable to delete clobs for notification_id " + notificationID + ": " + sqle);
            throw new PersistenceException("Unable to delete clobs for notification_id " + notificationID + ": " + sqle, sqle);
        } finally {
            db.release();
        }
        deleteClob(clobIdToDelete);
    }
    
    /**
     * Deletes the notification with the specified ID from the notification queue.
     * This method silently completes if the notification is not found in the queue.
     *
     * @param notificationID the ID of the notification to delete
     * @throws PersistenceException if there is a problem deleting
     */
    private static void clearNotificationFromDBQueue(String notificationID) throws PersistenceException {
        DBBean db = new DBBean();
        String qstrClearNotificationFromQueue = "delete from pn_notification_queue" + " where notification_id = " + notificationID;

        try {
            db.executeQuery(qstrClearNotificationFromQueue);
        } catch (SQLException sqle) {
            Logger.getLogger(Postman.class).debug("Postman.clearNotificationFromQueue threw an SQLException: " + sqle);
            throw new PersistenceException("Postman.clearNotificationFromQueue threw an SQLException: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * deleteClob deletes the clob related to this notification from the database.
     *
     * @exception PersistenceException if a database error occurs while trying to
     * delete the clob.
     */
    private static void deleteClob(String clobID) throws PersistenceException {
        DBBean db = new DBBean();
        StringBuffer query;

        if ((clobID == null) || (clobID.equals(""))) {
        	Logger.getLogger(NotificationClob.class).error("Tried to delete clob that has a null clob ID");
            return;
        }

        try {
            //Set up a transaction
            db.setAutoCommit(false);
            db.openConnection();

            //Clean out any references to the clob from pn_notification
            query = new StringBuffer();
            query.append("update pn_notification set notification_clob_id = null ");
            query.append("where notification_clob_id = ?");
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, clobID);
            db.executePrepared();

            //Clean out any references to the clob from pn_scheduled_subscription
            query = new StringBuffer();
            query.append("update pn_scheduled_subscription set target_object_clob_id ");
            query.append(" = null where target_object_clob_id = ?");
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, clobID);
            db.executePrepared();

            //Set up a query that will delete a notification clob based on the
            //notification id (as opposed to the clob id)
            query = new StringBuffer();
            query.append("delete from pn_notification_clob nc ");
            query.append("where nc.object_id = ?");
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, clobID);
            db.executePrepared();

            //Everything is complete, commit
            db.commit();
        } catch (SQLException sqle) {
        	Logger.getLogger(NotificationClob.class).error("Unexpected error thrown while trying to delete the " + "notification clob for notification " + clobID + ": " + sqle);
            try {
                db.rollback();
            } catch (SQLException sqle2) {
            	Logger.getLogger(NotificationClob.class).error("Unable to rollback deleteClob transaction.  " + sqle2);
            }
            throw new PersistenceException("An unexpected error occurred while trying to clean up notifications.", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Describe <code>getClobData</code> method here.
     *
     * @param clobID a <code>String</code> value
     * @return a <code>String</code> value
     * @exception PersistenceException if an error occurs
     */
    public synchronized static String getClobData(String clobID) throws PersistenceException {
        DBBean clobDB = new DBBean();
        Clob clob = null;
        String clobData = null;

        try {
            clobDB.setClobTableName("pn_notification_clob");
            clob = clobDB.getClob(clobID);

            clobData = clob.getData();
        } finally {
            clobDB.release();
        }

        return (clobData);
    }
}
