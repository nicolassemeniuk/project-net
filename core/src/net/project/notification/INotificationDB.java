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

/**
 * Provides constants used throughout notification.
 *
 * @author Unascrbied
 * @since Version 2
 */
public interface INotificationDB {

    public static final String CREATED_BY_ID = "CREATED_BY_ID";
    public static final String CREATED_DATE = "CREATED_DATE";
    public static final String CUSTOM_MESSAGE = "CUSTOM_MESSAGE";
    public static final String CLOB_ID = "CLOB_ID";

    public static final String DELIVERY_TYPE_ID = "DELIVERY_TYPE_ID";
    public static final String DELIVERY_ADDRESS = "DELIVERY_ADDRESS";
    public static final String SUBSCRIBER_BATCH_ID = "SUBSCRIBER_BATCH_ID";
    public static final String DESCRIPTION = "DESCRIPTION";


    public static final String EVENT_TIME = "EVENT_TIME";
    public static final String EVENT_TYPE = "EVENT_TYPE";

    /**
     * Delivery type ID for Email, currentyly <code>100</code>.
     */
    public static final String EMAIL_DELIVERABLE = "100";

    public static final String FILE = "file";

    public static final String INITIATOR_ID = "INITIATOR_ID";

    public static final String NAME = "NAME";
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String NOTIFICATION_TYPE_ID = "NOTIFICATION_TYPE_ID";
    public static final String NOTIFICATION_CLOB_TABLE = "pn_notification_clob";
    public static final String CUSTOMIZATION_USER_ID = "CUSTOMIZATION_USER_ID";
    public static final String SENDER_ID = "SENDER_ID";

    /* returns a cursor composed of the all the rows from pn_scheduled_subscription with due_date < sysdate and the clob_field from pn_notification_clob*/
    public static final String PREPARED_CALL_GET_READY_SUBSCRIPTIONS = "{ ? = call NOTIFICATION.GET_READY_SUBSCRIPTIONS()  }";


    /** takes a batch ID and returns a cursor which is composed of a subset of columns in pn_notification, pn_notification_queue and pn_notiifcation_clob*/
    public static final String PREPARED_CALL_GET_NOTIFICATIONS_FROM_QUEUE = "{ ? = call NOTIFICATION.GET_NOTIFICATIONS()  }";


    /* takes a event_type_id and returns a cursor which is composed of a subset of columns in pn_notification_type description and notification_type_id*/
    public static final String PREPARED_CALL_GET_NOTIFICATION_TYPES_FOR_FILE = "{ ? = call NOTIFICATION.GET_NOTIFICATION_TYPES_FILE(?)  }";

    /* takes a event_type_id and returns a cursor which is composed of a subset of columns in pn_notification_type description and notification_type_id*/
    public static final String PREPARED_CALL_GET_NOTIFICATION_TYPES = "{ ? = call NOTIFICATION.GET_NOTIFICATION_TYPES(?)  }";


    /** causes an entry to be made into pn_scheduled_subscription */
    public static final String PREPARED_CALL_CREATE_SCHEDULED_SUBSCRIPTION = "{call  NOTIFICATION.CREATE_SCHEDULED_SUBSCRIPTION (?,?,?,?,?,?,?,?,?) }";


    public static final String SUBSCRIPTION_ID = "SUBSCRIPTION_ID";
    public static final String SCHEDULED_SUBSCRIPTION_ID = "SCHEDULED_SUBSCRIPTION_ID";

    public static final String TARGET_OBJECT_ID = "TARGET_OBJECT_ID";
    public static final String TARGET_OBJECT_TYPE = "TARGET_OBJECT_TYPE";


}
