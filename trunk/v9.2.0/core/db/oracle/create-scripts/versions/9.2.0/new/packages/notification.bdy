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
CREATE OR REPLACE PACKAGE BODY notification IS
--==================================================================
-- Purpose: Subscription procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Chad        05-Sep-00  Created it.
-- Tim         24-Jul-01  Added is_immediate to create_notification
--                        Modified get_notifications to return only
--                        non-immediate notifications
--==================================================================

----------------------------------------------------------------------



----------------------------------------------------------------------
PROCEDURE  create_type_subscription
(
      i_subscription_name          IN       VARCHAR2,   -- the user created name of the subscription
      i_subscription_description   IN       VARCHAR2,   -- the user created description of the subscription
      --what will be sent in the event of a notification
      i_subscription_type_id       IN       VARCHAR2,   -- the system created type of subscription
      i_target_type                IN       VARCHAR2,   -- the system defined type of object that this subscription was created for, if it was created around an object typee, and not a particular object. Null otherwise.
      i_delivery_interval          IN       VARCHAR2,   -- the interval (weekly daily immediately etc...) that the subscriber has chosen to recieve notifications on
      i_delivery_address_group     IN       VARCHAR2,   -- the user entered delivery address (URI)
      i_created_by_id              IN       VARCHAR2,   -- the entity which created this subscription, possibly the system itself.
      i_space_id                   IN       VARCHAR2,
      i_is_custom_message_null     IN       NUMBER,
      o_custom_message_clob        OUT      CLOB,
      o_subscription_id            OUT      NUMBER
  )
   IS
     v_delivery_interval      pn_subscription.delivery_interval%TYPE
               := TO_NUMBER (i_delivery_interval);
     v_delivery_group_id      pn_delivery_address.address_group_id%TYPE
               := TO_NUMBER (i_delivery_address_group);
      v_subscription_type_id   pn_subscription_type.subscription_type_id%TYPE
               := TO_NUMBER (i_subscription_type_id);
      v_created_by_id          pn_subscription.created_by_id%TYPE
               := TO_NUMBER (i_created_by_id);
     v_space_id   pn_space_has_subscription.space_id%TYPE
               := TO_NUMBER (i_space_id);

      v_datetime               DATE;
      v_subscription_id        pn_object.object_id%TYPE;
      -- Standard error handling
      stored_proc_name         VARCHAR2(100)
               := 'NOTIFICATION.create_subscription';
   BEGIN
      -- Create new entry in pn_object gets the guid and inserts a row into pn_object
      -- base.creat_object == table.sotred procedure.  This effects two ends.
      --One is to create an entry into the pn_object table.
      --The other is to have this methd return that object's GUID.
      v_subscription_id :=
         create_new_object (
            g_subscription_object_type,
            v_created_by_id,
            g_active_record_status
         );

      -- Get current datetime. This has to be done manually, then entered into the table
      SELECT SYSDATE INTO v_datetime FROM dual;

      if (i_is_custom_message_null > 0) then
          -- Create the subscription record with null custom message
          INSERT INTO pn_subscription
              ( subscription_id, name, description, custom_message_clob,
                subscriber_batch_id, delivery_interval, created_by_id, subscription_type_id,
                created_date, modified_date, modified_by, record_status, crc )
          VALUES
              ( v_subscription_id, i_subscription_name, i_subscription_description, null,
                v_delivery_group_id, v_delivery_interval, v_created_by_id, v_subscription_type_id,
                v_datetime, v_datetime, v_created_by_id, g_active_record_status, v_datetime)
          returning
              custom_message_clob into o_custom_message_clob;

      else
          -- Create the subscription record with empty custom message for
          -- subsequent streaming
          INSERT INTO pn_subscription
              ( subscription_id, name, description, custom_message_clob,
                subscriber_batch_id, delivery_interval, created_by_id, subscription_type_id,
                created_date, modified_date, modified_by, record_status, crc )
          VALUES
              ( v_subscription_id, i_subscription_name, i_subscription_description, empty_clob(),
                v_delivery_group_id, v_delivery_interval, v_created_by_id, v_subscription_type_id,
                v_datetime, v_datetime, v_created_by_id, g_active_record_status, v_datetime)
          returning
              custom_message_clob into o_custom_message_clob;

      end if;

      INSERT INTO pn_object_type_subscription (subscription_id, object_type)
           VALUES (v_subscription_id, i_target_type);

      insert into pn_space_has_subscription (subscription_id, space_id)
            values (v_subscription_id, v_space_id);

      -- Set output parameters
      o_subscription_id := v_subscription_id;

   EXCEPTION
      WHEN OTHERS THEN
          base.log_error (stored_proc_name, SQLCODE, SQLERRM);
          raise;
   END;


PROCEDURE create_notification
(
    i_delivery_address  IN  varchar2,
    i_notification_clob_id  IN  number,
    i_delivery_type_id IN number,
    i_delivery_from_address IN varchar2,
    i_customization_user_id IN varchar2,
    i_sender_id IN varchar2,
    i_is_immediate in number,
    o_notification_id OUT number,
    o_return_value OUT number
 )
  IS

    v_notification_id       pn_notification.notification_id%type;
    v_timestamp DATE := SYSDATE;
    stored_proc_name VARCHAR2(100) := 'NOTIFICATION.create_notification';
    v_pnet_admin_id pn_person.person_id%type := 1000;

BEGIN

    select pn_object_sequence.nextval into v_notification_id from dual;

    -- first create a master notification record
    insert into pn_notification
    (
        notification_id,
        notification_clob_id,
        delivery_address,
        delivery_from_address,
        delivery_type_id,
        created_date,
        created_by_id,
        customization_user_id,
        modified_date,
        modified_by_id,
        sender_id,
        record_status,
        crc
    ) values
    (
        v_notification_id,
        i_notification_clob_id,
        i_delivery_address,
        i_delivery_from_address,
        i_delivery_type_id,
        v_timestamp,
        v_pnet_admin_id,
        i_customization_user_id,
        v_timestamp,
        v_pnet_admin_id,
        i_sender_id,
        BASE.ACTIVE_RECORD_STATUS,
        v_timestamp
    );


    -- now create an entry in the queue
    insert into pn_notification_queue
    (
        notification_id,
        posted_date,
        posted_by_id,
        is_immediate,
        record_status
    ) values
    (
        v_notification_id,
        v_timestamp,
        v_pnet_admin_id,
        i_is_immediate,
        BASE.ACTIVE_RECORD_STATUS
    );

      o_notification_id := v_notification_id;
      o_return_value := 0;   -- these are defined in our packages. This is success

      COMMIT;

   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            rollback;
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;


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
  )
   IS
      -- Convert input varchar2() to numbers
      v_delivery_group_id      pn_delivery_address.address_group_id%TYPE
               := TO_NUMBER (i_delivery_address_group);
      v_delivery_interval      pn_subscription.delivery_interval%TYPE
               := TO_NUMBER (i_delivery_interval);

      v_subscription_type_id   pn_subscription_type.subscription_type_id%TYPE
               := TO_NUMBER (i_subscription_type_id);
      v_created_by_id          pn_subscription.created_by_id%TYPE
               := TO_NUMBER (i_created_by_id);
      v_target_id   pn_object_has_subscription.object_id%TYPE
               := TO_NUMBER (i_target_id);
      v_space_id   pn_space_has_subscription.space_id%TYPE
               := TO_NUMBER (i_space_id);

      v_datetime               DATE;
      v_subscription_id        pn_object.object_id%TYPE;
      -- Standard error handling
      stored_proc_name         VARCHAR2(100)
               := 'NOTIFICATION.create_object_subscription';


   BEGIN
      -- Create new entry in pn_object gets the guid and inserts a row into pn_object
      -- base.creat_object == table.sotred procedure.  This effects two ends.
      --One is to create an entry into the pn_object table.
      --The other is to have this methd return that object's GUID.
      v_subscription_id :=
         create_new_object (
            g_subscription_object_type,
            v_created_by_id,
            g_active_record_status
         );
      -- Get current datetime. This has to be done manually, then entered into the table
      SELECT SYSDATE
        INTO v_datetime
        FROM dual;   -- RETURNS DATE AND TIME SELECT SYSDATE INTO THE SAID VAR.

      if (i_is_custom_message_null > 0) then
          -- Create the subscription record with null custom message
          INSERT INTO pn_subscription
              ( subscription_id, name, description, custom_message_clob,
                subscriber_batch_id, delivery_interval, created_by_id, subscription_type_id,
                created_date, modified_date, modified_by, record_status, crc )
          VALUES
              ( v_subscription_id, i_subscription_name, i_subscription_description, null,
                v_delivery_group_id, v_delivery_interval, v_created_by_id, v_subscription_type_id,
                v_datetime, v_datetime, v_created_by_id, g_active_record_status, v_datetime )
          returning
              custom_message_clob into o_custom_message_clob;
      else
          -- Create the subscription record with empty custom message
          INSERT INTO pn_subscription
              ( subscription_id, name, description, custom_message_clob,
                subscriber_batch_id, delivery_interval, created_by_id, subscription_type_id,
                created_date, modified_date, modified_by, record_status, crc )
          VALUES
              ( v_subscription_id, i_subscription_name, i_subscription_description, empty_clob(),
                v_delivery_group_id, v_delivery_interval, v_created_by_id, v_subscription_type_id,
                v_datetime, v_datetime, v_created_by_id, g_active_record_status, v_datetime )
          returning
              custom_message_clob into o_custom_message_clob;

      end if;

      INSERT INTO pn_object_has_subscription (subscription_id, object_id)
           VALUES (v_subscription_id, v_target_id);

      insert into pn_space_has_subscription (subscription_id, space_id)
            values (v_subscription_id, v_space_id);

      -- Set output parameters
      o_subscription_id := v_subscription_id;

   EXCEPTION
      WHEN OTHERS THEN
          base.log_error (stored_proc_name, SQLCODE, SQLERRM);
          raise;
   END;



--==================================================================
-- Purpose: Subscription procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Chad        05-Sep-00  Created it.
--==================================================================

----------------------------------------------------------------------
-- makes an entry into pn_scheduled_subscription
----------------------------------------------------------------------
 PROCEDURE create_scheduled_subscription
(
      i_event_type                  IN       VARCHAR2,
      i_target_id                   IN       NUMBER,
      i_target_type                 IN       VARCHAR2,
      i_event_time                  IN       DATE,
      i_description                 IN       VARCHAR2,
      i_target_object_clob_id       IN       NUMBER,
      i_initiator_id                IN       NUMBER,
      i_space_id                    IN       NUMBER,
      o_scheduled_subscription_id   OUT      NUMBER
   )
   IS
      /* these are my local var definitions */
      v_datetime                    DATE;
      v_delivery_date               DATE;
      v_custom_message_clob         pn_scheduled_subscription.custom_message_clob%type;

      -- Standard error handling
      stored_proc_name              VARCHAR2(100)
               := 'NOTIFICATION.create_scheduled_subscription';
      v_event_type_id               NUMBER
               := get_event_type_for_name(i_event_type);


      /* this is a local var definition which is also a cursor */
      CURSOR c_ready_subscription (
         c_i_target_id   pn_object_has_subscription.object_id%TYPE,
         c_i_event_type_id  pn_event_type.event_type_id%type,
         c_i_space_id pn_space_has_subscription.space_id%type
      )
      IS
         (SELECT n.notification_type_id,
                sb.subscription_id,
                sb.name,
                sb.description,
                sb.subscription_type_id,
                sb.subscriber_batch_id,
                sb.created_date,
                sb.created_by_id,
                sb.delivery_interval

           FROM pn_object_has_subscription ohs,
                pn_sub_has_notify_type n,
                pn_subscription sb,
                pn_event_has_notification ehn
          -- this line qualifies the selection of columns according to that column's actual value
          WHERE ohs.object_id = c_i_target_id
          AND sb.subscription_id = ohs.subscription_id
          and ehn.event_type_id = c_i_event_type_id
          and ehn.notification_type_id = n.notification_type_id
          AND ohs.subscription_id = n.subscription_id
          AND sb.record_status = 'A')
      UNION
          (SELECT n.notification_type_id,
                sb.subscription_id,
                sb.name,
                sb.description,
                sb.subscription_type_id,
                sb.subscriber_batch_id,
                sb.created_date,
                sb.created_by_id,
                sb.delivery_interval

           FROM pn_subscription sb,
                pn_sub_has_notify_type n,
                pn_object_type_subscription ots,
                pn_event_has_notification ehn,
                pn_space_has_subscription shs
          -- this line qualifies the selection of columns according to that column's actual value
          WHERE shs.space_id = c_i_space_id
          and ots.object_type = (select object_type from pn_object where object_id = c_i_target_id)
          AND sb.subscription_id = shs.subscription_id
          and sb.subscription_id = ots.subscription_id
          and ehn.event_type_id = c_i_event_type_id
          and ehn.notification_type_id = n.notification_type_id
          AND sb.subscription_id = n.subscription_id
          AND sb.record_status = 'A');

      /* this line defines two more variables */
      --        v_subscription_ready_rec   c_ready_subscription%ROWTYPE;    -- the cursor has a row type. Here we define a variable whose "type"  is the same as that rowtype
      v_scheduled_subscription_id   pn_scheduled_subscription.scheduled_subscription_id%TYPE;
   BEGIN
      -- STATE now a row exists in the clob table and the clob data has been entered into that row.
      v_datetime := SYSDATE;

      BEGIN
         FOR v_subscription_ready_rec IN c_ready_subscription (i_target_id, v_event_type_id, i_space_id)
         LOOP
            v_delivery_date :=
               compute_delivery_date (
                  v_subscription_ready_rec.delivery_interval,
                  i_event_time
               );
            v_scheduled_subscription_id :=
               create_new_object (
                  g_scheduled_subscription_type,
                  i_initiator_id,
                  g_active_record_status
               );

            -- Grab the CLOB locater for the current record
            -- This cannot be included in the UNION so is not available in the cursor
            select custom_message_clob into v_custom_message_clob
            from pn_subscription
            where subscription_id = v_subscription_ready_rec.subscription_id;

            INSERT INTO pn_scheduled_subscription
                ( scheduled_subscription_id, name, description, notification_type_id, custom_message_clob,
                  subscriber_batch_id, delivery_interval, delivery_date,
                  target_object_id, target_object_type, target_object_clob_id, initiator_id,
                  event_time, event_type, is_queued, batch_id,
                  create_date, created_by_id, modified_date, modified_by_id, record_status, crc,
                  space_id )
            VALUES
                ( v_scheduled_subscription_id, v_subscription_ready_rec.name, v_subscription_ready_rec.description, v_subscription_ready_rec.notification_type_id, v_custom_message_clob,
                  v_subscription_ready_rec.subscriber_batch_id, v_subscription_ready_rec.delivery_interval, v_delivery_date,
                  i_target_id, i_target_type, i_target_object_clob_id, i_initiator_id,
                  v_datetime, v_event_type_id, 0, -1,   -- this will always signify not-batched. This column needs SOMETHING in it so its value can be compared to another number. You can't compare a blank column with a n8umber.
                  v_datetime, i_initiator_id, v_datetime, i_initiator_id, 'A', v_datetime,
                  to_char(i_space_id) );

         END LOOP subscription_loop;

      END;

      -- Set output parameters
      o_scheduled_subscription_id := v_scheduled_subscription_id;   -- JDBC will convert this if I try to catch it with a callableStatement.getString()

   EXCEPTION
      WHEN OTHERS THEN
          base.log_error (stored_proc_name, SQLCODE, SQLERRM);
          raise;
   END;   --create_scheduled_subscription


   FUNCTION get_event_type_for_name (i_event_name VARCHAR2)
      RETURN NUMBER
   IS
      return_value       NUMBER       := -1;
      stored_proc_name   VARCHAR(100)
               := 'NOTIFICATION::get_event_type_for_name';
   /* returns each scheduled subscription and it's associated clob_id and clob_field */

   BEGIN
      SELECT et.event_type_id
        INTO return_value
        FROM pn_event_type et
       WHERE et.name = LOWER (i_event_name);
      RETURN return_value;
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;   -- end get_event_type_for_name

   /* returns a cursor composed of the all the rows from pn_scheduled_subscription with due_date < sysdate and the clob_field from pn_notification_clob*/
   FUNCTION get_ready_subscriptions
      RETURN g_reference_cursor
   IS
      v_return_cur       G_reference_cursor;   -- the cursor will contain the ready subscriptions
      v_batch_id         NUMBER             := mark_ready_subscription;   -- the guid which will mark the rows to be processed in this procedure.
      stored_proc_name   VARCHAR2(100)
               := 'NOTIFICATION.get_ready_subscriptions';   -- used during error logging
   /* returns each scheduled subscription and it's associated clob_id and clob_field */

   BEGIN
      OPEN v_return_cur FOR
         SELECT ss.*, nt.name as nt_name, nt.description as nt_description, nt.default_message as nt_default_message,
                nt.object_type as nt_object_type, nc.object_id AS clob_id,
                s.space_name, s.space_type, ss.created_by_id as sender_id
           FROM pn_scheduled_subscription ss, pn_notification_type nt, pn_notification_clob nc,
                pn_space_view s
          WHERE ss.target_object_clob_id = nc.object_id
            AND batch_id = v_batch_id
            and nt.notification_type_id = ss.notification_type_id
            and s.space_id = ss.space_id
          ORDER BY ss.event_time desc;
      RETURN v_return_cur;
   -- We are waiting here if there's another process which wants to alter this row somehow.
   -- That should never be the case. If it is, there's a potential for deadlock.
   -- Perhaps this this should have a NOWAIT / ERROR handler that returns a null cursor to the caller that the caller then checks for.
   -- without that, and if there's a  collision, some notifications will be orphans, owing to the fact that the batch_id used to select them is lost.
   -- Perhaps we should prepare for that possibility anyway.
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;   -- end mark_and_batch_notifications

   /* returns a GUID or -1 if it tanked.  */
   FUNCTION get_guid (stored_proc_name IN VARCHAR2)
      RETURN NUMBER
   IS
      guid   NUMBER := -1;   -- this signifies failure. The caller can check for this ( x >0)
   BEGIN
      SELECT pn_object_sequence.nextval
        INTO guid
        FROM dual;
      RETURN guid;
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   /* mark the given scheduled subscription with the given  batching id */
   FUNCTION mark_ready_subscription
      RETURN NUMBER
   IS

        pragma autonomous_transaction;


      stored_proc_name   VARCHAR2(100)
               := 'NOTIFICATION.mark_ready_subscription ';
      v_batch_id         NUMBER        := get_guid (stored_proc_name);
      debugCounter NUMBER;


      CURSOR batch_id_cur
      IS
         SELECT batch_id
           FROM pn_scheduled_subscription
          WHERE batch_id = -1   -- signifies that this has not been batched and is available to be delivered.
            AND delivery_date < SYSDATE
            FOR UPDATE OF batch_id NOWAIT;
   BEGIN
      DBMS_OUTPUT.put_line('in mark_ready_subscription');
      FOR batch_id_rec IN batch_id_cur
      LOOP

         UPDATE pn_scheduled_subscription
            SET batch_id = v_batch_id
            where current of batch_id_cur;

      END LOOP;

      COMMIT;
      RETURN v_batch_id;
   EXCEPTION   -- causes a variable to be set and then returns to caller.
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   FUNCTION create_new_object (
      object_type     IN   VARCHAR2,
      created_by_id   IN   VARCHAR2,
      record_status   IN   VARCHAR2
      )
      RETURN NUMBER
   IS
   BEGIN
      RETURN base.create_object (object_type, created_by_id, record_status);
   END;

   FUNCTION create_clob
      RETURN NUMBER
   IS
      stored_proc_name   VARCHAR2(100) := 'NOTIFICATION.create_new_CLOB';
      v_clob_guid        NUMBER        := get_guid (stored_proc_name);   -- get a GUID for this unique CLOB instnace we are now creating
   BEGIN
      INSERT INTO pn_notification_clob (object_id, clob_field)
           VALUES (v_clob_guid, EMPTY_CLOB ());
      RETURN v_clob_guid;
   EXCEPTION   -- causes a variable to be set and then returns to caller.
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   FUNCTION get_clob_for_read (i_object_id IN VARCHAR2)
      RETURN CLOB
   IS
      stored_proc_name   VARCHAR2(100) := 'NOTIFICATION.get_CLOB';
      return_clob        CLOB;
      v_clob_id          NUMBER(20)    := TO_NUMBER (i_object_id);
   BEGIN
      SELECT clob_field
        INTO return_clob
        FROM pn_notification_clob
       WHERE object_id = v_clob_id;
      RETURN return_clob;
   EXCEPTION   -- causes a variable to be set and then returns to caller.
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   FUNCTION get_clob_for_update (i_object_id IN VARCHAR2)
      RETURN CLOB
   IS
      stored_proc_name   VARCHAR2(100) := 'NOTIFICATION.get_CLOB';
      return_clob        CLOB;
      v_clob_id          NUMBER(20)    := TO_NUMBER (i_object_id);
   BEGIN
      SELECT clob_field
        INTO return_clob
        FROM pn_notification_clob
       WHERE object_id = v_clob_id
         FOR UPDATE;
      RETURN return_clob;
   EXCEPTION   -- causes a variable to be set and then returns to caller.
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;




   PROCEDURE erase_clob (i_clob_id IN VARCHAR2)
   IS
      stored_proc_name       VARCHAR2(100) := 'NOTIFICATION.ERASE_CLOB';
      clob_to_erase          CLOB;
      clob_to_erase_length   NUMBER        := 0;
      o_return_status        NUMBER        := 2000;   -- reset return status to represent failure until this succeeds
   BEGIN
      SELECT clob_field
        INTO clob_to_erase
        FROM pn_notification_clob
       WHERE object_id = i_clob_id
         FOR UPDATE;
      clob_to_erase_length := dbms_lob.getlength (clob_to_erase);
      --dbms_output.put_line(clob_to_erase_length);
      dbms_lob.erase (clob_to_erase, clob_to_erase_length, 1);
--    clob_to_erase_length := dbms_lob.getlength(clob_to_erase);
      --   dbms_output.put_line(clob_to_erase_length);
      dbms_lob.TRIM (clob_to_erase, 0);
      -- clob_to_erase_length := dbms_lob.getlength(clob_to_erase);
      -- dbms_output.put_line(clob_to_erase_length);
      COMMIT;
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            o_return_status := 2000;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;

         o_return_status := 0;   -- if I ever decide this needs to be returned
   END;

   PROCEDURE erase_clob (i_clob IN CLOB)
   IS
      stored_proc_name       VARCHAR2(100) := 'NOTIFICATION.ERASE_CLOB';
      clob_to_erase_length   NUMBER        := 0;
      clob_to_erase          CLOB          := i_clob;
      o_return_status        NUMBER        := 2000;   -- reset return status to represent failure until this succeeds
   BEGIN
      clob_to_erase_length := dbms_lob.getlength (clob_to_erase);
      --dbms_output.put_line(clob_to_erase_length);
      dbms_lob.erase (clob_to_erase, clob_to_erase_length, 1);
--    clob_to_erase_length := dbms_lob.getlength(clob_to_erase);
      --   dbms_output.put_line(clob_to_erase_length);
      dbms_lob.TRIM (clob_to_erase, 0);
      -- clob_to_erase_length := dbms_lob.getlength(clob_to_erase);
      -- dbms_output.put_line(clob_to_erase_length);
      COMMIT;
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            o_return_status := 2000;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;

         o_return_status := 0;   -- if I ever decide this needs to be returned
   END;




/* trims the CLOB to the length indicated by i_new_length*/
   PROCEDURE trim_clob (i_clob IN CLOB, i_new_length NUMBER)
   IS
      stored_proc_name   VARCHAR2(100) := 'NOTIFICATION.TRIM_CLOB';
      clob_to_trim       CLOB          := i_clob;
      v_new_length       NUMBER        := TO_NUMBER (i_new_length);
      o_return_status    NUMBER        := 2000;   -- reset return status to represent failure until this succeeds
   BEGIN
      dbms_lob.TRIM (clob_to_trim, v_new_length);
      COMMIT;
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            o_return_status := 2000;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;

         o_return_status := 0;   -- if I ever decide this needs to be returned
   END;

   /* begins a new row in pn_notification_queue. Returns a status number, 0 == success 2000 = failure

   PROCEDURE  begin_new_queue_record(i_ready_subscription_rec IN ready_subscription_rec, i_notification_id IN NUMBER, o_return_status OUT NUMBER)

   IS
      stored_proc_name VARCHAR2(100):= 'NOTIFICATION.begin_new_queue_record';

   BEGIN
   o_return_status := 2000;   -- reset the return status to failure until we succeed
      INSERT
        INTO pn_notification_queue
        (
         notification_id,
         posted_by_id,
         posted_date,
         record_status
         )
     VALUES
        (
         i_notification_id,
         i_ready_subscription_rec.created_by_id, -- created by
         SYSDATE, -- create date
         'A'    -- status
         );

  EXCEPTION
     WHEN OTHERS THEN
       BEGIN
          o_return_status := 2000;
          base.log_error(stored_proc_name, sqlcode, sqlerrm);
       END;

   o_return_status :=0;   -- reset the return status to failure until we succeed

   END; -- end of begin_new_pn_notification_queue

*/


/*

          RECORD_LOCKED           EXCEPTION;     -- AN EXCEPTION DEFINED TO BE ORA-00054, RECORD LOCKED EXCEPTION
          PRAGMA EXCEPTION_INIT( RECORD_LOCKED, -54);  -- THE ASSOCIATION BETWEEN MY EXCEPTION AND ORA-00054 IS MADE

 EXCEPTION
                    WHEN record_locked                           -- our user-defined alias for ORA-00051
                    THEN
                        NULL;
                    WHEN OTHERS THEN             -- if we tanked becasuse of something else, then roll back and return null
                    BEGIN
                        base.log_error(stored_proc_name, sqlcode, sqlerrm);
                        RETURN FALSE;
                    END;

*/

/* mark the given notification_queue row with the given  batching id */
   FUNCTION mark_notifications
      RETURN NUMBER
   IS
      stored_proc_name   VARCHAR2(100) := 'NOTIFICATION.mark_notifications ';
      v_batch_id         NUMBER        := 0;

      CURSOR batch_id_cur
      IS
         SELECT batch_id
           FROM pn_notification_queue
          WHERE batch_id != 1000
            FOR UPDATE OF batch_id NOWAIT;
   BEGIN
      DBMS_OUTPUT.put_line ('v_batch_id = ' || TO_CHAR (v_batch_id));
      DBMS_OUTPUT.put_line ('in Begin');
      v_batch_id := get_guid (stored_proc_name);

--     OPEN batch_id_cur;
      FOR batch_id_rec IN batch_id_cur
      LOOP
         UPDATE pn_notification_queue
            SET batch_id = v_batch_id
          WHERE CURRENT OF batch_id_cur;
      END LOOP;

--     CLOSE batch_id_cur;
      COMMIT;
      RETURN v_batch_id;
   EXCEPTION   -- causes a variable to be set and then returns to caller.
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
            CLOSE batch_id_cur;
         END;
   END;

   /* takes a batch ID and returns a cursor which is composed of a subset of columns in pn_notification, pn_notification_queue and pn_notiifcation_clob*/
   FUNCTION get_notifications
      RETURN g_reference_cursor
   IS
      return_cur         g_reference_cursor;   --define the cursor, open it then return it.
      stored_proc_name   VARCHAR2(100)
               := 'NOTIFICATION.get_notifications';
      v_batch_id         NUMBER             := mark_notifications;
   BEGIN
      OPEN return_cur FOR
         SELECT n.delivery_type_id,
		 		n.notification_id,
                n.delivery_address,
                n.customization_user_id, -- the id of the user whose customization(locale + config language) info would be used to render the notification.
                n.sender_id, -- the id of the user who created or sent the notification.
                nc.object_id as clob_id   -- needs to be selected from the clob table. now the name of this row willl change
           FROM pn_notification n,
                pn_notification_queue nq,
                pn_notification_clob nc   -- clob now this will also reference the clob table
          WHERE n.notification_id = nq.notification_id   -- make a row of these columns if the ids match
            and nq.is_immediate <> 1                     -- exclude immediate notifications
--         AND nq.batch_id = v_batch_id   -- and they are in the batch I am now processing

            AND n.notification_clob_id = nc.object_id
          ORDER BY posted_date asc;
      RETURN return_cur;   -- return reference cursor
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   FUNCTION get_notification_types (i_object_type VARCHAR2)
      RETURN g_reference_cursor
   IS
      return_cur         g_reference_cursor;   --define the cursor, open it then return it.
      stored_proc_name   VARCHAR2(100)
               := 'NOTIFICATION.get_notification_types';

   BEGIN

    IF i_object_type = 'form' THEN

        OPEN return_cur FOR
            SELECT n.notification_type_id,
                n.description
            FROM pn_event_type e,
                pn_notification_type n,
                pn_event_has_notification ehn
            WHERE e.object_type in ( 'form' , 'form_data' )
            AND e.event_type_id = ehn.event_type_id
            AND n.notification_type_id = ehn.notification_type_id;
    ELSE
            OPEN return_cur FOR
            SELECT n.notification_type_id,
                n.description
            FROM pn_event_type e,
                pn_notification_type n,
                pn_event_has_notification ehn
            WHERE e.object_type = i_object_type
            AND e.event_type_id = ehn.event_type_id
            AND n.notification_type_id = ehn.notification_type_id;
    END IF;

      RETURN return_cur;   -- return reference cursor
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   /* takes a event_type_id and returns a cursor which is composed of a subset of columns in notification_type*/
   FUNCTION get_notification_types_file (i_object_type VARCHAR2)
      RETURN g_reference_cursor
   IS
      return_cur         g_reference_cursor;   --define the cursor, open it then return it.
      stored_proc_name   VARCHAR2(100)
               := 'NOTIFICATION.get_notification_types';
   BEGIN
      OPEN return_cur FOR
         SELECT n.notification_type_id,
                n.description
           FROM pn_event_type e,
                pn_notification_type n,
                pn_event_has_notification ehn
          WHERE e.object_type = i_object_type
            AND e.event_type_id = ehn.event_type_id
            AND n.notification_type_id = ehn.notification_type_id
            AND n.name != 'create'
            AND n.name != 'create_container'
            AND n.name != 'remove_container';
      RETURN return_cur;   -- return reference cursor
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   /* DATE FUNCTIONS */

/* returns a Date with the value of 8 a.m. today. *//* NO ERROR CHECKING !!!*/
   FUNCTION get_8_am_on_day (i_date IN DATE)
      RETURN DATE
   IS
   BEGIN
      RETURN TRUNC (i_date) + 1 / 3;
   END;

/* computes the delivery date given the delivery_interval of a subscription *//* NO ERROR CHECKING !!!*/
   FUNCTION compute_delivery_date (
      i_delivery_interval   IN   NUMBER,
      i_event_time               DATE
      )
      RETURN DATE
   IS
      time_of_event     DATE    := i_event_time;
      current_date      DATE    := SYSDATE;   --
      return_date       DATE;
      before_deadline   BOOLEAN;
   BEGIN
      IF i_delivery_interval = 100   -- if delivery is to happen immediately
      THEN
         return_date := current_date;   -- then the date to return to the caller is the current date and we're done
      ELSE   -- otherwise it's not immediate and we need to get the delivery date
         BEGIN
            IF time_of_event < get_8_am_on_day (current_date)   -- if time_of_event is before 8 am today.
            THEN
               before_deadline := TRUE;   -- then set this flag to true
            ELSE
               before_deadline := FALSE;   -- otherwise to false
            END IF;   -- either way, go get the date

            return_date :=
               get_delayed_delivery_date (
                  i_delivery_interval,
                  time_of_event,
                  current_date,
                  before_deadline
               );
         END;
      END IF;

      RETURN return_date;
   END;

   /* processes an INotificationEvent which has occured after the deadline which demarcates between an even happening today adn an event which is considerd to have happened tomorrow*/
   /* LIMITED ERROR CHECKING !!!*/
   FUNCTION get_delayed_delivery_date (
      i_delivery_interval   IN   NUMBER,
      time_of_event         IN   DATE,
      current_date          IN   DATE,
      before_deadline       IN   BOOLEAN
      )
      RETURN DATE
   IS
      return_date                 DATE;
      invalid_delivery_interval   EXCEPTION;
      delivery_day                VARCHAR2(10) := 'monday';
   BEGIN   -- simply fork processing according to received i_delivery_interval.
      --If no match is found, then raise an excpetion
      IF i_delivery_interval = 200   -- daily notification
      THEN
         return_date :=
            get_daily_delivery_date (
               time_of_event,
               current_date,
               before_deadline
            );
      ELSIF i_delivery_interval = 300   -- weekly notification
      THEN
         return_date :=
            get_weekly_delivery_date (
               time_of_event,
               current_date,
               delivery_day,
               before_deadline
            );
      ELSIF i_delivery_interval = 400   -- then it must be monthly ort we can check for an error here ....
      THEN
         return_date :=
            get_monthly_delivery_date (
               time_of_event,
               current_date,
               delivery_day,
               before_deadline
            );
      ELSE   -- define but don't raise the error, simply record it
         INSERT INTO pn_sp_error_log
              VALUES (
                 SYSDATE,
                 'create_scheduled_subscription::get_delayed_delivery_date',
                 100,
                 TO_CHAR (i_delivery_interval)
              );
      END IF;

      RETURN return_date;
   END;

   /* returns the delivery_date for a daily notification*//* NO ERROR CHECKING !!!*/
   FUNCTION get_daily_delivery_date (
      time_of_event     DATE,
      current_date      DATE,
      before_deadline   BOOLEAN
      )
      RETURN DATE
   IS
      return_date   DATE;
   BEGIN
      IF before_deadline   -- if you're in under the deadline
      THEN
         return_date := current_date;   -- then deliver it today
      ELSE   -- otherwise deliver it tomorrow at 8am
         return_date := get_8_am_on_day (current_date + 1);
      END IF;

      RETURN return_date;
   END;

   /* check to see if date passed in is the day of week passed in as a String *//* NO ERROR CHECKING !!!*/
   FUNCTION is_day (today IN DATE, day_to_check IN VARCHAR2)
      RETURN BOOLEAN
   IS
   BEGIN
      RETURN NEXT_DAY (TRUNC (today) - 1, day_to_check) = TRUNC (today);
   END;

   /* check to see if the date passed in is the first of the month in which it falls.*//* NO ERROR CHECKING !!!*/
   FUNCTION is_first_of_month (today IN DATE, day_to_check IN VARCHAR2)
      RETURN BOOLEAN
   IS
   BEGIN
      RETURN TRUNC (today, 'month') = TRUNC (today);
   END;

   /* returns the delivery date for a weekly notification */
   FUNCTION get_weekly_delivery_date (
      time_of_event      DATE,
      current_date       DATE,
      delivery_weekday   VARCHAR2,
      before_deadline    BOOLEAN
      )
      RETURN DATE
   IS
      return_date   DATE;
   BEGIN
      IF before_deadline   -- if you're in under the deadline
      THEN
         BEGIN
            IF is_day (delivery_weekday, 'monday')   -- and today is the day weekly deliveries are made
            THEN
               return_date := current_date;   -- then deliver it today
            ELSE   -- otherwise deliver it at 8 am the next delivery day
               return_date :=
                  get_8_am_on_day (NEXT_DAY (current_date, delivery_weekday));
            END IF;
         END;
      ELSE   -- otherwise deliver it at 8 am the next delivery day
         return_date :=
            get_8_am_on_day (NEXT_DAY (current_date, delivery_weekday));
      END IF;

      RETURN return_date;
   END;

   /* returns the delivery date for a monthly notification. *//* NO ERROR CHECKING !!!*/
   FUNCTION get_monthly_delivery_date (
      time_of_event      DATE,
      current_date       DATE,
      delivery_weekday   VARCHAR2,
      before_deadline    BOOLEAN
      )
      RETURN DATE
   IS
      return_date   DATE;
   BEGIN
      IF before_deadline   -- if you're in under the dealine
      THEN
         IF is_first_of_month (current_date, delivery_weekday)   -- and today is the first of the month
         THEN
            return_date := current_date;   -- then deliver it today
         ELSE
            return_date :=
               get_8_am_on_day (NEXT_DAY (current_date, delivery_weekday));   -- otherwise deliver it the next delivery day at 8am
         END IF;
      ELSE
         return_date :=
            get_8_am_on_day (NEXT_DAY (current_date, delivery_weekday));   -- otherwise deliver it the next delivery day at 8am
      END IF;

      RETURN return_date;
   END;


   FUNCTION create_notifiable_event (
        i_event_name    IN  varchar2,
        i_event_label   IN  varchar2,
        i_event_description IN  varchar2,
        i_object_type   IN  varchar2
        ) RETURN NUMBER

        IS
            v_error_code    NUMBER := -1;
            v_event_id     NUMBER;
            v_notify_type_id    NUMBER;
            v_timestamp     DATE := SYSDATE;
            v_admin_id      NUMBER := 1000;

         BEGIN

            select pn_object_sequence.nextval into v_event_id from dual;
            select pn_object_sequence.nextval into v_notify_type_id from dual;

            insert into pn_event_type
            (
                event_type_id,
                name,
                description,
                object_type,
                record_status,
                crc
            ) values (
                v_event_id,
                i_event_name,
                i_event_description,
                i_object_type,
                'A',
                v_timestamp
            );

            insert into pn_notification_type
            (
                notification_type_id,
                name,
                description,
                default_message,
                object_type,
                create_date,
                created_by_id,
                modified_date,
                modified_by_id,
                record_status,
                crc
             ) values (
                v_notify_type_id,
                i_event_label,
                i_event_description,
                i_event_description,
                i_object_type,
                v_timestamp,
                v_admin_id,
                v_timestamp,
                v_admin_id,
                'A',
                v_timestamp
          );

            insert into pn_event_has_notification
            (
                notification_type_id,
                event_type_id
            ) values (
                v_notify_type_id,
                v_event_id
            );

            v_error_code := BASE.OPERATION_SUCCESSFUL;
            return v_error_code;

    EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            v_error_code := BASE.PLSQL_EXCEPTION;
            return v_error_code;
         END;

     END; -- create_notifiable_event


END;   -- Package Body NOTIFICATION
/

