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
CREATE OR REPLACE PACKAGE TIMESHEET IS

-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Sachin      3/12/07 created


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
   );

   PROCEDURE INSERT_ASSIGNMENT
   (
        i_timesheet_id IN NUMBER,
        i_space_id IN NUMBER,
        i_activity_id NUMBER
   );

   PROCEDURE REMOVE_TIMESHEET
   (
        i_timesheet_id IN NUMBER,
        i_record_status IN CHAR
   );

   PROCEDURE STORE_ACTIVITY
   (
        i_object_id IN NUMBER,
        i_person_id IN NUMBER,
        i_name IN VARCHAR,
        o_object_id OUT NUMBER
   );

END;
/

