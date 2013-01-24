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
CREATE OR REPLACE PACKAGE BODY TIMESHEET IS
-- Purpose: Provides procedures for creating and modifying timesheets

   PROCEDURE STORE_TIMESHEET
   (
        i_timesheet_id IN NUMBER,
        i_person_id IN NUMBER,
        i_approve_reject_by_id IN NUMBER,
        i_start_date IN DATE,
        i_end_date IN DATE,
        i_work IN NUMBER,
        i_work_units IN NUMBER,
        i_date_submitted IN DATE,
        i_date_approve_reject IN DATE,
        i_record_status IN CHAR,
        i_status_id IN NUMBER,
        i_comments IN VARCHAR2,
        o_timesheet_id OUT NUMBER
   )

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Sachin      12-Mar-07  Creation.


IS

v_timesheet_id NUMBER(20);

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100) := 'STORE_TIMESHEET';

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);


BEGIN
-- NEW TIMESHEET, INSERT
    IF ((i_timesheet_id IS NULL) OR (i_timesheet_id = ''))    THEN

        v_timesheet_id := BASE.CREATE_OBJECT('timesheet', i_person_id, i_record_status);
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_timesheet_id, 'timesheet', i_person_id, i_person_id);

        INSERT INTO pn_timesheet (
             object_id, person_id, status_id, approve_reject_by_id,
             start_date, end_date, work, work_units, date_submitted, date_approve_reject, record_status, comments
        ) VALUES (
             v_timesheet_id, i_person_id, i_status_id, i_approve_reject_by_id,
             i_start_date, i_end_date, i_work, i_work_units, i_date_submitted, i_date_approve_reject, i_record_status, i_comments
        );
        o_timesheet_id := v_timesheet_id;

-- EXISTING TIMESHEET, UPDATE
    ELSE

        o_timesheet_id := i_timesheet_id;

        -- Perform the actual timesheet update
        UPDATE
    	    pn_timesheet
        SET
        	object_id = i_timesheet_id,
            person_id = i_person_id,
            status_id = i_status_id,
            approve_reject_by_id = i_approve_reject_by_id,
            start_date = i_start_date,
            end_date = i_end_date,
            work = i_work,
            work_units = i_work_units,
            date_submitted = i_date_submitted,
            date_approve_reject = i_date_approve_reject,
            record_status = i_record_status,
            comments = i_comments
        WHERE
        	object_id = i_timesheet_id;

    END IF; -- insert/update

    EXCEPTION
    -- handle the exceptions and as of now set o_timesheet id = ''
        WHEN e_unique_constraint THEN
            o_timesheet_id := '';

        WHEN e_null_constraint THEN
            o_timesheet_id := '';

        WHEN e_no_parent_key THEN
            o_timesheet_id := '';

        WHEN e_check_constraint THEN
            o_timesheet_id := '';

        WHEN OTHERS THEN
            err_num:=SQLCODE;
            err_msg:=SUBSTR(SQLERRM,1,120);
            o_timesheet_id := '';
            ROLLBACK;
            INSERT INTO pn_sp_error_log
                VALUES (sysdate,stored_proc_name,err_num,err_msg);
            COMMIT;

END STORE_TIMESHEET;


   PROCEDURE INSERT_ASSIGNMENT
   (
        i_timesheet_id IN NUMBER,
        i_space_id IN NUMBER,
        i_activity_id NUMBER
   )

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Sachin      14-Mar-07  Creation.

IS

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100) := 'INSERT_ASSIGNMENT';

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);

BEGIN

    INSERT INTO pn_assignment_timesheet (
             timesheet_id, space_id, object_id
        ) VALUES (
             i_timesheet_id, i_space_id, i_activity_id
        );

    EXCEPTION
    -- handle the exceptions as throwing them is not needed as of now
        WHEN e_unique_constraint THEN
    -- entry already exists so do nothing
        null;

        WHEN e_null_constraint THEN
    -- nothing gets entered
        null;

        WHEN e_no_parent_key THEN
        null;

    -- as of now we just handle this
        WHEN OTHERS THEN
            err_num:=SQLCODE;
            err_msg:=SUBSTR(SQLERRM,1,120);
            ROLLBACK;
            INSERT INTO pn_sp_error_log
                VALUES (sysdate,stored_proc_name,err_num,err_msg);
            COMMIT;

END INSERT_ASSIGNMENT;


   PROCEDURE REMOVE_TIMESHEET
   (
        i_timesheet_id IN NUMBER,
        i_record_status IN CHAR
   )

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Sachin      14-Mar-07  Creation.

IS

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100) := 'REMOVE_TIMESHEET';

e_null_constraint EXCEPTION;

PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);

BEGIN

    UPDATE pn_timesheet
    SET
        record_status = i_record_status
    WHERE
        object_id = i_timesheet_id;

 EXCEPTION
    -- handle the exceptions as throwing them is not needed as of now
        WHEN e_null_constraint THEN
    -- nothing gets updated
        null;

        WHEN OTHERS THEN
            err_num:=SQLCODE;
            err_msg:=SUBSTR(SQLERRM,1,120);
            ROLLBACK;
            INSERT INTO pn_sp_error_log
                VALUES (sysdate,stored_proc_name,err_num,err_msg);
            COMMIT;

END REMOVE_TIMESHEET;

   PROCEDURE STORE_ACTIVITY
   (
        i_object_id IN NUMBER,
        i_person_id IN NUMBER,
        i_name IN VARCHAR,
        o_object_id OUT NUMBER
   )

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Sachin      20-Apr-07  Creation.

IS

v_object_id NUMBER(20);

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100) := 'STORE_ACTIVITY';

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);

BEGIN

-- NEW ACTIVITY, INSERT
    IF ((i_object_id IS NULL) OR (i_object_id = ''))    THEN

        v_object_id := BASE.CREATE_OBJECT('activity', i_person_id, 'A');
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_object_id, 'activity', i_person_id, i_person_id);

        INSERT INTO pn_activity (
                object_id, space_id, name
            ) VALUES (
                 v_object_id, i_person_id, i_name
            );
        o_object_id := v_object_id;

-- EXISTING ACTIVITY, UPDATE
    ELSE

        o_object_id := i_object_id;

        -- Perform the actual timesheet update
        UPDATE
    	    pn_activity
        SET
        	name = i_name
        WHERE
        	object_id = i_object_id;

    END IF; -- insert/update

    EXCEPTION
    -- handle the exceptions as throwing them is not needed as of now
        WHEN e_unique_constraint THEN
    -- entry already exists so do nothing
        null;

        WHEN e_null_constraint THEN
    -- nothing gets entered
        null;

        WHEN e_no_parent_key THEN
        null;

    -- as of now we just handle this
        WHEN OTHERS THEN
            err_num:=SQLCODE;
            err_msg:=SUBSTR(SQLERRM,1,120);
            ROLLBACK;
            INSERT INTO pn_sp_error_log
                VALUES (sysdate,stored_proc_name,err_num,err_msg);
            COMMIT;

END STORE_ACTIVITY;

END;
/

