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
CREATE OR REPLACE PACKAGE process

  IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Robin       06-Apr-00  Creation from Brian's procs.
-- Brian       30-Apr-00  Added a remove procedure
-- Robin       11-May-00  Added error codes to coincide with new table.
-- Carlos      07-Aug-06  Updated GET_PHASE_WORKPLAN_START and GET_PHASE_WORKPLAN_FINISH.

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

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

Procedure STORE_PROCESS
(
    p_person_id IN NUMBER,
    p_space_id IN NUMBER,
    p_process_id IN NUMBER,
    p_process_name IN VARCHAR2,
    p_process_desc IN VARCHAR2,
    p_current_phase_id IN NUMBER,
    p_last_gate_passed_id IN NUMBER,
    p_record_status IN VARCHAR2,
    o_process_id OUT NUMBER,
    o_status OUT NUMBER
);

Procedure COPY_PROCESS
(
    i_from_space_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_return_value OUT NUMBER
);


Procedure REMOVE_PROCESS
(
    p_process_id IN NUMBER,
    o_status OUT NUMBER
);

Procedure STORE_PHASE
(
    p_person_id IN NUMBER,
    p_phase_id  IN NUMBER,
    p_process_id IN NUMBER,
    p_space_id IN NUMBER,
    p_phase_name IN VARCHAR2,
    p_phase_desc IN VARCHAR2,
    p_sequence IN NUMBER,
    p_status_id IN NUMBER,
    p_record_status IN VARCHAR2,
    p_progress_reporting_method IN VARCHAR2,
    p_start_date IN DATE,
    p_end_date IN DATE,
    p_entered_percent_complete IN NUMBER,
    o_phase_id OUT NUMBER,
    o_status OUT NUMBER
);

Procedure COPY_PHASE
(
    i_phase_id  IN NUMBER,
    i_to_process_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_return_value OUT NUMBER
 );


Procedure REMOVE_PHASE
(
    p_phase_id IN NUMBER
);

Procedure STORE_GATE
(
    p_person_id IN NUMBER,
    p_gate_id IN NUMBER,
    p_phase_id  IN NUMBER,
    p_space_id IN NUMBER,
    p_gate_name IN VARCHAR2,
    p_gate_desc IN VARCHAR2,
    p_gate_date IN DATE,
    p_status_id IN NUMBER,
    p_record_status IN VARCHAR2,
    o_gate_id OUT NUMBER,
    o_status OUT NUMBER
);

Procedure COPY_GATE
(
    i_gate_id IN NUMBER,
    i_to_phase_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_status OUT NUMBER
);


Procedure REMOVE_GATE
(
    p_gate_id IN NUMBER,
    o_status OUT NUMBER
);

Procedure STORE_DELIVERABLE
(
    p_person_id IN NUMBER,
    p_phase_id IN NUMBER,
    p_deliverable_id IN NUMBER,
    p_space_id IN NUMBER,
    p_deliverable_name IN VARCHAR2,
    p_deliverable_desc IN VARCHAR2,
    p_status_id IN NUMBER,
    p_methodology_deliverable_id IN NUMBER,
    p_is_optional IN NUMBER,
    p_record_status IN VARCHAR2,
    i_is_deliverable_comments_null in number,
    o_deliverable_comments_clob out clob,
    o_deliverable_id OUT NUMBER
);

Procedure COPY_DELIVERABLE
(
    i_deliverable_id IN NUMBER,
    i_to_phase_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_status OUT NUMBER
);

Procedure REMOVE_DELIVERABLE
(
    p_deliverable_id IN NUMBER,
    o_status OUT NUMBER
);

function GET_PHASE_SCHEDULE_COMPLETE (
    i_phase_id in NUMBER
)  return NUMBER;

function GET_PHASE_WORKPLAN_START (
    i_phase_id in NUMBER
)   return DATE;

    function GET_PHASE_WORKPLAN_FINISH (
    i_phase_id in NUMBER
)    return DATE;

END; -- Package Specification PROCESS
/

