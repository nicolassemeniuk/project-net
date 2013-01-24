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
CREATE OR REPLACE Package Body help
IS

/*-------------------------------------------------------------------------------

ADD_FEEDBACK

Modification History
Date       Programmer  Reason
========   ==========  =================================================
21-Jun-00  Robin       Creation.
--------------------------------------------------------------------------------*/

PROCEDURE add_feedback  (i_person_id IN NUMBER,
                         i_project_id IN NUMBER,
                         i_subject IN VARCHAR2,
                         i_key_id IN NUMBER,
                         i_comments IN VARCHAR2,
                         o_status OUT NUMBER)


IS

stored_proc_name VARCHAR2(100):= 'HELP.ADD_FEEDBACK';

BEGIN

    INSERT INTO help_feedback (person_id, timestamp, project_id, subject, key_id, comments)
         VALUES (i_person_id, sysdate, i_project_id, i_subject, i_key_id, i_comments);

   o_status := success;
   COMMIT;

EXCEPTION
   WHEN e_null_constraint THEN
      -- feedback subject/comments empty
      o_status := null_field;

   WHEN e_unique_constraint THEN
      -- feedback already exists
      o_status := dupe_key;

   WHEN e_no_parent_key THEN
      -- person, key, project doesn't exist
      o_status := no_parent_key;

   WHEN e_value_too_large THEN
      -- value too large for column
      o_status := value_too_large;

   WHEN OTHERS THEN
      o_status := generic_error;
      err_num:=SQLCODE;
      err_msg:=SUBSTR(SQLERRM,1,120);
      ROLLBACK;
      INSERT INTO pn_sp_error_log
         VALUES (sysdate,stored_proc_name,err_num,err_msg);
      COMMIT;

END add_feedback;

END; -- Package Body HELP
/

