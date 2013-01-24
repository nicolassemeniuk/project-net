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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.project.database.Clob;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import oracle.jdbc.OracleTypes;

/**
 * Provides a collection of {@link Notification}s which must be sent.
 *
 *
 * @author Unascribed
 * @since Version 2
 */
class NotificationCollection extends ArrayList implements INotificationDB {

    /* -------------------------------  Constructors  ------------------------------- */

    /**
     * Creates an empty NotificationCollection.
     */
    public NotificationCollection() {
        // do nothing
    }


    /* -------------------------------  Load collection from database  ------------------------------- */

    /**
     * Loads all non-immediate notifications and populates this collection.
     * @throws PersistenceException if there is a problem loading any of the
     * notifications
     */
    public void load() throws PersistenceException {

        DBBean db = new DBBean();

        try {

            db.prepareCall(PREPARED_CALL_GET_NOTIFICATIONS_FROM_QUEUE);

            db.cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            db.executeCallable();

            db.result = (ResultSet) db.cstmt.getObject(1);

            while (db.result.next()) {
                Notification notification = new Notification();

                notification.setNotificationID(db.result.getString(NOTIFICATION_ID));
                notification.setDeliveryAddress(db.result.getString(DELIVERY_ADDRESS));
                notification.setDeliveryTypeID(db.result.getString(DELIVERY_TYPE_ID));
                notification.setNotificationXML(getClobData(db.result.getString(CLOB_ID)));
                notification.setCustomizationUserID(db.result.getString(CUSTOMIZATION_USER_ID));
                notification.setSenderID(db.result.getString(SENDER_ID));

                this.add(notification);
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Error loading notifications: " + sqle, sqle);

        } finally {
            db.release();
        }

    }

    /**
     * Returns the data loaded from the Notification Clob with the specified ID.
     *
     * @param clobID the ID of the clob data to load
     * @return the loaded data
     * @throws PersistenceException if there is a problem loading the data
     */
    private String getClobData(String clobID) throws PersistenceException {
        DBBean db = new DBBean();
        String clobData = null;

        try {
            Clob clob = new Clob(db);
            clob.setTableName(NOTIFICATION_CLOB_TABLE);
            clob.setID(clobID);
            clob.loadReadOnly();
            clobData = clob.getData();

        } finally {
            db.release();

        }

        return clobData;
    }

}