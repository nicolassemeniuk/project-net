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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.notification;

import net.project.base.DefaultDirectory;
import net.project.security.SessionManager;
import net.project.security.User;

/**
 * This class is designed to eliminate the need to subclass
 * ImmediateNotification in order to send a notification.  Now, you should be
 * able to send notifications by just calling the {@see #send} method.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public class NotificationUtils {
    /**
     * Subclass of ImmediateNotification which is used to send immediate
     * notifications.
     */
    private static class ImmediateEmailNotification extends ImmediateNotification {
        /**
         * Send an immediate notification to the indicated user.
         *
         * @param user a <code>User</code> object to which we are going to send a
         * notification.
         * @param xml a <code>String</code> which contains the xml which will be
         * used to build the body of the email.  There is no set type for this
         * body, it will be transformed by the stylesheet in the
         * {@see #xslStylesheet} parameter.
         * @param xslStylesheet a <code>String</code> containing the location of a
         * stylesheet which will be used to build the presentation of this email
         * notification.
         * @throws NotificationException if any error occurs while build the email
         * notification.
         */
        public void send(User user, String xml, String xslStylesheet) throws NotificationException {
            setDeliveryTypeID(ImmediateNotification.EMAIL_DELIVERABLE);
            setXSLStylesheetPath(xslStylesheet);
            setDeliveryAddress(user.getEmail());
            if (DefaultDirectory.isUserRegisteredByID(user.getID())) {
                setCustomizationUserID(user.getID());
            } else {
                setCustomizationUserID(SessionManager.getUser().getID());
            }
            setNotificationXML(xml);
            this.post();
        }
    }


    /**
     * Send an immediate notification to the indicated user.
     *
     * @param user a <code>User</code> object to which we are going to send a
     * notification.
     * @param xml a <code>String</code> which contains the xml which will be
     * used to build the body of the email.  There is no set type for this
     * body, it will be transformed by the stylesheet in the
     * {@see #xslStylesheet} parameter.
     * @param xslStylesheet a <code>String</code> containing the location of a
     * stylesheet which will be used to build the presentation of this email
     * notification.
     * @throws NotificationException if any error occurs while build the email
     * notification.
     */
    public static void sendImmediateEmailNotification(User user, String xml,
        String xslStylesheet) throws NotificationException {
        new ImmediateEmailNotification().send(user, xml, xslStylesheet);
    }
}

