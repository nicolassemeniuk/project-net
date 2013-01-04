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
package net.project.security.domain;

import net.project.base.property.PropertyProvider;
import net.project.notification.ImmediateNotification;
import net.project.notification.NotificationException;
import net.project.persistence.IXMLPersistence;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
 * Provides funtionality for sending Domain Migration Based notifications.
 */
public class DomainMigrationNotification extends ImmediateNotification {

    private static final String NOTIFICATION_STYLE_SHEET = "/domain/xsl/DomainMigrationNotification.xsl";

    /**
     * Creates a new DomainMigrationNotification.
     */
    public DomainMigrationNotification() {
        super();
    }

    /**
     * Notifies user of DomainMigration.
     * @param userDomainMigrationManager providing information about the
     * migration to be included in the email
     * @param person the Migrated User; assumed to be a registered user
     */
    public void notifyUser(UserDomainMigrationManager userDomainMigrationManager, User person) {

        // set the sender in this case because we do not have a user in session for notification on registration
        setSenderID(person.getID());
        // in this special case, we want the "From" address of the notification to be the brand default
        // rather than the sender (who in this case is the recipeient).
        setFromAddress(PropertyProvider.get("prm.global.default.email.fromaddress"));


        setDeliveryTypeID(ImmediateNotification.EMAIL_DELIVERABLE);
        setXSLStylesheetPath(NOTIFICATION_STYLE_SHEET);
        setDeliveryAddress(person.getEmail());
        setCustomizationUserID(person.getID());

        StringBuffer xml = new StringBuffer();
        xml.append(IXMLPersistence.XML_VERSION);
        xml.append("<DomainMigrationNotification>");
        xml.append(person.getXMLBody());
        xml.append(userDomainMigrationManager.getXMLBody());
        xml.append("</DomainMigrationNotification>");

        setNotificationXML(xml.toString());
        try {
            this.post();
        } catch (NotificationException ne) {
            // We don't want the Domain Migration to fail just because of notification failure.
        	Logger.getLogger(DomainMigrationNotification.class).debug("DomainMigrationNotification.java : notifyUser() operation failed : Notification exception thrown " + ne.getMessage());
        }

    }

}
