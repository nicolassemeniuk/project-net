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
CREATE OR REPLACE PACKAGE BODY message IS

PROCEDURE add_message  (i_title IN VARCHAR2,
                        i_message IN VARCHAR2,
                        i_active_ind IN VARCHAR2,
                        o_message_id OUT NUMBER)

IS

stored_proc_name VARCHAR2(100):= 'MESSAGE.ADD_MESSAGE';
v_message_id NUMBER(20);
v_status NUMBER(20);

BEGIN

   INSERT INTO status_messages (message_id, title, message, active_indicator, timestamp)
         VALUES (message_sequence.nextval, i_title, i_message, i_active_ind , sysdate);

   SELECT message_sequence.currval INTO v_message_id FROM dual;

   o_message_id := v_message_id ;

   COMMIT;

EXCEPTION
   WHEN OTHERS THEN
      err_num:=SQLCODE;
      err_msg:=SUBSTR(SQLERRM,1,120);
      ROLLBACK;
      INSERT INTO pn_sp_error_log
         VALUES (sysdate,stored_proc_name,err_num,err_msg);
      COMMIT;
      raise;

END add_message;


/*-------------------------------------------------------------------------------

EDIT_MESSAGE

Modification History
Date       Programmer  Reason
========   ==========  =================================================
12-Jun-00  Robin       Creation.
--------------------------------------------------------------------------------*/

PROCEDURE edit_message  (i_message_id IN VARCHAR2,
                         i_title IN VARCHAR2,
                         i_message IN VARCHAR2,
                         o_status OUT NUMBER)

IS

stored_proc_name VARCHAR2(100):= 'MESSAGE.EDIT_MESSAGE';

BEGIN

   UPDATE status_messages
      SET title = i_title, message = i_message
      WHERE message_id = i_message_id;

   IF SQL%NOTFOUND THEN
      o_status := no_data;
      RETURN;
   END IF;

   o_status := success;
   COMMIT;

EXCEPTION

   WHEN e_null_constraint THEN
      -- message title empty
      o_status := null_field;

   WHEN OTHERS THEN
      o_status := generic_error;
      err_num:=SQLCODE;
      err_msg:=SUBSTR(SQLERRM,1,120);
      ROLLBACK;
      INSERT INTO pn_sp_error_log
         VALUES (sysdate,stored_proc_name,err_num,err_msg);
      COMMIT;
      raise;

END edit_message;


/*-------------------------------------------------------------------------------

DELETE_MESSAGE

Modification History
Date       Programmer  Reason
========   ==========  =================================================
12-Jun-00  Robin       Creation.
--------------------------------------------------------------------------------*/

PROCEDURE delete_message  (i_message_id IN NUMBER,
                           o_status OUT NUMBER)

IS

stored_proc_name VARCHAR2(100):= 'MESSAGE.DELETE_MESSAGE';


BEGIN

        DELETE FROM status_messages
            WHERE message_id = i_message_id;

   o_status := success;

   COMMIT;

EXCEPTION

   WHEN OTHERS THEN
      o_status := generic_error;
      err_num:=SQLCODE;
      err_msg:=SUBSTR(SQLERRM,1,120);
      ROLLBACK;
      INSERT INTO pn_sp_error_log
         VALUES (sysdate,stored_proc_name,err_num,err_msg);
      COMMIT;
      raise;

END delete_message;


/*-------------------------------------------------------------------------------

SET_STATUS

Modification History
Date       Programmer  Reason
========   ==========  =================================================
12-Jun-00  Robin       Creation.
--------------------------------------------------------------------------------*/

PROCEDURE set_status  (i_message_id IN NUMBER,
                       i_active_ind IN VARCHAR2,
                       o_status OUT NUMBER)

IS

stored_proc_name VARCHAR2(100):= 'MESSAGE.SET_STATUS';

BEGIN

   UPDATE status_messages
      SET active_indicator = i_active_ind
      WHERE message_id = i_message_id;

   IF SQL%NOTFOUND THEN
      o_status := no_data;
      RETURN;
   END IF;

   o_status := success;

   commit;

EXCEPTION

   WHEN OTHERS THEN
      o_status := generic_error;
      err_num:=SQLCODE;
      err_msg:=SUBSTR(SQLERRM,1,120);
      ROLLBACK;
      INSERT INTO pn_sp_error_log
         VALUES (sysdate,stored_proc_name,err_num,err_msg);
      COMMIT;
      raise;

END set_status;


END; -- Package Body MESSAGE
/

