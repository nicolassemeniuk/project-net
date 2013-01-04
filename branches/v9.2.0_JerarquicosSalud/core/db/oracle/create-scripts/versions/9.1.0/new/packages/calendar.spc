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
CREATE OR REPLACE PACKAGE calendar
  IS

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;
e_no_data EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

Procedure store_meeting
(
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_calendar_id IN NUMBER,
    i_event_id IN NUMBER,
    i_meeting_id IN NUMBER,
    i_host_id IN NUMBER,
    i_event_name IN VARCHAR2,
    i_frequency_type_id IN NUMBER,
    i_facility_id IN NUMBER,
    i_start_date IN DATE,
    i_end_date IN DATE,
    o_event_desc_clob OUT CLOB,
    o_event_purpose_clob OUT CLOB,
    o_meeting_id OUT NUMBER,
    o_event_id OUT NUMBER
);

Procedure store_event
(
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_calendar_id IN NUMBER,
    i_event_id IN NUMBER,
    i_event_name IN VARCHAR2,
    i_frequency_type_id IN NUMBER,
    i_facility_id IN NUMBER,
    i_start_date IN DATE,
    i_end_date IN DATE,
    o_event_desc_clob OUT CLOB,
    o_event_purpose_clob OUT CLOB,
    o_event_id OUT NUMBER
);

Procedure store_agenda_item
(
    i_person_id IN NUMBER,
    i_meeting_id IN NUMBER,
    i_agenda_item_id IN NUMBER,
    i_item_name IN VARCHAR2,
    i_item_desc IN VARCHAR2,
    i_time_alloted IN VARCHAR2,
    i_status_id IN NUMBER,
    i_owner_id IN NUMBER,
    i_item_sequence IN NUMBER,
    i_is_meeting_notes_null IN NUMBER,
    o_meeting_notes_clob OUT CLOB
);

Procedure remove_agenda_item
(
    i_agenda_item_id IN NUMBER,
    i_meeting_id IN NUMBER,
    o_status OUT NUMBER
);



END; -- Package Specification CALENDAR
/

