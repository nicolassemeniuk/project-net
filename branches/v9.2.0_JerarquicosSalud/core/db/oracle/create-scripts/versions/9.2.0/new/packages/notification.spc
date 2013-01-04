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
CREATE OR REPLACE PACKAGE notification IS
--==================================================================
-- Purpose: PNET Notification procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Chad        9/20/00  Created it.
--==================================================================

   -- Package constants
   G_subscription_object_type                   pn_object.object_type%type := 'subscription'; -- TABLENAME.COLUMNNAME%KWTYPE  G means global := means default value
   G_scheduled_subscription_type                pn_object.object_type%type :='scheduled_subscription';
   G_notification_object_type                   pn_object.object_type%type :='notification';

   DELIVERY_STATUS_UNDELIVERED number := 100;

   DELIVERY_TYPE_EMAIL number := 100;


   G_active_record_status                       pn_subscription.record_status%type := 'A';  -- := MEANS DEFAULT ASSIGNMENT
   TYPE G_reference_cursor                      IS REF CURSOR;
   TYPE G_scheduled_subscription_cur             IS REF CURSOR RETURN pn_scheduled_subscription%ROWTYPE;


PROCEDURE  create_type_subscription
     (i_subscription_name          IN       VARCHAR2,   -- the user created name of the subscription
      i_subscription_description   IN       VARCHAR2,   -- the user created description of the subscription
      i_subscription_type_id       IN       VARCHAR2,   -- the system created type of subscription
      i_target_type                IN       VARCHAR2,   -- the system defined type of object that this subscription was created for, if it was created around an object typee, and not a particular object. Null otherwise.
      i_delivery_interval          IN       VARCHAR2,   -- the interval (weekly daily immediately etc...) that the subscriber has chosen to recieve notifications on
      i_delivery_address_group     IN       VARCHAR2,   -- the address group id of the address list
      i_created_by_id              IN       VARCHAR2,   -- the entity which created this subscription, possibly the system itself.
      i_space_id                   IN       VARCHAR2,
      i_is_custom_message_null     IN       NUMBER,
      o_custom_message_clob        OUT      CLOB,
      o_subscription_id            OUT      NUMBER
  );

  PROCEDURE  create_object_subscription
 (
      i_subscription_name          IN       VARCHAR2,   -- the user created name of the subscription
      i_subscription_description   IN       VARCHAR2,   -- the user created description of the subscription
      i_subscription_type_id       IN       VARCHAR2,   -- the system created type of subscription
      i_target_id                  IN       VARCHAR2,   -- the system defined type of object that this subscription was created for, if it was created around an object typee, and not a particular object. Null otherwise.
      i_delivery_interval          IN       VARCHAR2,   -- the interval (weekly daily immediately etc...) that the subscriber has chosen to recieve notifications on
      i_delivery_address_group     IN       VARCHAR2,   -- the user entered delivery address (URI)
      i_created_by_id              IN       VARCHAR2,   -- the entity which created this subscription, possibly the system itself.
      i_space_id                   IN       VARCHAR2,
      i_is_custom_message_null     IN       NUMBER,
      o_custom_message_clob        OUT      CLOB,
      o_subscription_id            OUT      NUMBER
  );



----------------------------------------------------------------------
----------------------------------------------------------------------
-- create_subscription  these are the variables coming into or going out of this method.
-- values are returned via out parameters.. these values are passed in then altered by reference.
----------------------------------------------------------------------
    PROCEDURE create_scheduled_subscription   -- who calls me and what did they know?
      (
        i_event_type IN varchar2,
        i_target_id    IN number,
        i_target_type IN varchar2,
        i_event_time    IN Date,
        i_description    IN varchar2,
        i_target_object_clob_id     IN number,
        i_initiator_id  IN number,
        i_space_id      IN number,
        o_scheduled_subscription_id OUT number
        );


    PROCEDURE create_notification
      (
        i_delivery_address  IN  varchar2,
        i_notification_clob_id IN number,
        i_delivery_type_id IN number,
        i_delivery_from_address IN varchar2,
        i_customization_user_id IN varchar2,
        i_sender_id IN varchar2,
        i_is_immediate in number,
        o_notification_id OUT number,
        o_return_value OUT number
      );



      --PROCEDURE begin_new_notification_record(i_ready_subscription_rec IN ready_subscription_rec, o_notification_id OUT NUMBER, o_return_status OUT NUMBER);

      --PROCEDURE  begin_new_queue_record(i_ready_subscription_rec IN ready_subscription_rec, i_notification_id IN NUMBER, o_return_status OUT NUMBER);
----------------------------------------------------------------------
-- create notifications by selecting subscriptions which have come due,
-- compressing their XML and entering rows into both pn_notification_queue
-- and pn_notification.
----------------------------------------------------------------------
FUNCTION create_new_object(object_type IN VARCHAR2, created_by_id IN VARCHAR2, record_status IN VARCHAR2)
RETURN NUMBER;

/* returns a cursor to the rows from pn_scheduled_subscription with due_date < sysdate. Marks those rows with a batch_id */
FUNCTION get_ready_subscriptions
RETURN G_reference_cursor;

/*utitlity function used to brand a certain group of ready_subscriptions so they can be processed as a unit */
FUNCTION mark_ready_subscription
RETURN NUMBER;

 /* Returns a Cursor to a certain number of Notifications  in the notification_queue*/
FUNCTION get_notifications
RETURN G_reference_cursor;

 /* marks rows in pn_notification_queue as "in progress" and assigns them a batch_id number for selection */
FUNCTION mark_notifications
RETURN NUMBER;

FUNCTION get_notification_types (i_object_type VARCHAR2)
RETURN G_reference_cursor;

FUNCTION get_notification_types_file(i_object_type VARCHAR2)
RETURN G_reference_cursor;

/* Returns a GUID without creating an object in the system */
FUNCTION get_GUID(stored_proc_name  IN VARCHAR2 )
RETURN NUMBER;


/* Computes the value for the column delivery_date which is used to indicate when a Subscriptiion is ready to be delviered*/
FUNCTION compute_delivery_date( i_delivery_interval IN NUMBER, i_event_time DATE)    -- THIS HAS TO MATCH THE PARAMETER NAME ALSO!!!
RETURN DATE;

/*utility function which process an INotificationEvent which has occured after the deadline which demarcates between an event happening today and an event which is considerd to have happened tomorrow*/
FUNCTION  get_delayed_delivery_date(i_delivery_interval IN NUMBER, time_of_event IN DATE, current_date IN DATE, before_deadline BOOLEAN)
RETURN DATE;

 /* returns the delviery_date for a daily delivery*/
FUNCTION get_daily_delivery_date(time_of_event IN DATE, current_date IN DATE, before_deadline IN BOOLEAN)
RETURN DATE;

/* returns the delivery date for a weekly notification*/
FUNCTION  get_weekly_delivery_date(time_of_event IN DATE, current_date IN DATE, delivery_weekday IN VARCHAR2 ,before_deadline IN BOOLEAN)
RETURN DATE;

/* returns the delivery date for a monthly notification */
FUNCTION  get_monthly_delivery_date(time_of_event IN DATE, current_date IN DATE, delivery_weekday IN VARCHAR2 ,before_deadline IN BOOLEAN)
RETURN DATE;

/* returns the event_type as a number given the name of the event*/
FUNCTION get_event_type_for_name (i_event_name VARCHAR2)
RETURN NUMBER;

FUNCTION is_first_of_month( today IN DATE , day_to_check IN VARCHAR2)
RETURN BOOLEAN;
 /* returns a Date with the value of 8 a.m. today. */

FUNCTION get_8_am_on_day(i_date IN DATE)
RETURN DATE;

   /* check to see if date passed in is the day of week passed in as a String */

FUNCTION is_day( today IN DATE , day_to_check IN VARCHAR2)
RETURN BOOLEAN;

/* create a new CLOB inthe database in the table pn_notification_clob */
FUNCTION create_CLOB
RETURN NUMBER;

/* erase the clob_field value of a clob without deleting the clob itself.*/
PROCEDURE erase_clob(i_clob_id IN VARCHAR2);

/* erase the clob_field value of a clob without deleting the clob itself.*/
PROCEDURE erase_CLOB(i_clob IN CLOB);

/* trims the CLOB to the length indicated by i_new_length*/
PROCEDURE trim_CLOB(i_clob IN CLOB, i_new_length NUMBER);

/* retrieve from the table pn_notification_clob the CLOB associated with a particular object_id */
FUNCTION get_CLOB_for_read(i_object_id IN VARCHAR2)
RETURN CLOB;

/* retrieve from the table pn_notification_clob the CLOB associated with a particular object_id */
FUNCTION get_CLOB_for_update(i_object_id IN VARCHAR2)
RETURN CLOB;

function create_notifiable_event (
    i_event_name    IN  varchar2,
    i_event_label   IN  varchar2,
    i_event_description IN  varchar2,
    i_object_type   IN  varchar2) return NUMBER;

END; -- Package Specification NOTIFICATION
/

