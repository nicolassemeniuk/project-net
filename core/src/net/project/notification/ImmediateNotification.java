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
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.notification;

import net.project.security.SessionManager;

/**
 * An ImmediateNotification is a Notification that is delivered
 * as soon as it is posted.
 * <p>
 * Note immediate notifications are always synchronously delivered and initiated by a user.  As such, the "sender"
 * for immediate notifications is always set to the current user in session.
 */
public abstract class ImmediateNotification extends Notification {

    private static final boolean IS_IMMEDIATE = true;

    /**
     * Creates a new, empty ImmediateNotification
     */
    public ImmediateNotification() {
        super(IS_IMMEDIATE);
        setSenderID(SessionManager.getUser().getID());
    }

    /**
     * Posts the notification and delivers it immediately.
     * @throws DeliveryException if there is a problem delivering
     * @throws NotificationException if there is a problem posting
     */
    public void post() throws DeliveryException, NotificationException {
        // Post this notification
        super.post();
        // Deliver this notificaiton immediately
        NotificationManager.deliverImmediately(this);
    }

}
