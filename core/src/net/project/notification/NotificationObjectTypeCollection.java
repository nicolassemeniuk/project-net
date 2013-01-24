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

 /*------------------------------------------------------------------------------------------+
|
|    $RCSfile$
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|
|
+------------------------------------------------------------------------------------------*/
package net.project.notification;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Provides Collection of Notification Object Types .
 * @author deepak
 */

public class NotificationObjectTypeCollection
        extends ArrayList implements IXMLPersistence {
    // Checks whether to load only the subscribable notification types
    private boolean subscribable = true;

    /**
     Converts the object to XML representation of the Space Collection.
     This method returns the Space as XML text.
     @return XML representation of the Space
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + this.getXMLBody());
    }

    /**
     Converts the Space Types to XML representation without the XML version tag.
     This method returns the Space Types as XML text.
     @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer(300);

        xml.append("<NotificationObjectTypeCollection>\n");
        Iterator itr = this.iterator();

        while (itr.hasNext()) {
            NotificationObjectType notificationObjectType = (NotificationObjectType) itr.next();
            xml.append(notificationObjectType.getXMLBody());
        }
        xml.append("</NotificationObjectTypeCollection>\n");
        return xml.toString();
    }

    /**
     * Loads the Notification types depending on the suscribable flag
     *
     * @exception PersistenceException
     *           thrown in case of any trouble in loading the Notification Object Types
     */
    private void load() throws PersistenceException {
        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();

        // Query is as follows:
        // Select all the columns from PN_NOTIFICATION_OBJECT_TYPE
        query.append("select p.object_type, p.display_name, p.is_subscribable ");
        query.append(" from pn_notification_object_type p  ");

        // Check whether to load only the subscribable notification types
        if (subscribable) {
            query.append("where p.is_subscribable = '1' ");
        }

        try {
            db.prepareStatement(query.toString());
            db.executePrepared();

            // Create the Notification Object Type objects
            while (db.result.next()) {
                String displayName = null;
                String objectType = db.result.getString("object_type");
                String displayNameToken = db.result.getString("display_name");

                if (PropertyProvider.isToken(displayNameToken)) {
                    displayName = PropertyProvider.get(displayNameToken);
                } else {
                    displayName = displayNameToken;
                }
                NotificationObjectType notificationObjectType = new NotificationObjectType(objectType, displayName);
                this.add(notificationObjectType);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(NotificationObjectTypeCollection.class).error("NotificationObjectTypeCollection.load(): threw and SQLE: " + sqle);
            throw new PersistenceException(" Error loading the Notification Object Type Data ", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Loads all Subscribable  Notification types.
     *
     * @exception PersistenceException
     *           thrown in case of any trouble in loading the Notification Object Types
     */
    public void loadSubscribable() throws PersistenceException {
        this.subscribable = true;
        load();
    }

    /**
     * Returns the String representation of Option List for the Subscribable Notification
     * types
     *
     * @return String
     */
    public String getOptionList(String selectedObjectType) {
        StringBuffer types = new StringBuffer(200);
        Iterator itr = this.iterator();
        while (itr.hasNext()) {
            NotificationObjectType notificationObjectType = (NotificationObjectType) itr.next();
            if (selectedObjectType != null && selectedObjectType.equals(notificationObjectType.getObjectType())) {
                types.append("<option SELECTED value=\"" + notificationObjectType.getObjectType() + "\">"
                        + notificationObjectType.getName() + "</option> \n");
            } else {
                types.append("<option value=\"" + notificationObjectType.getObjectType() + "\">"
                        + notificationObjectType.getName() + "</option> \n");
            }
        }
        return types.toString();
    }
}
