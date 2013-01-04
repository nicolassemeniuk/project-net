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
CREATE OR REPLACE
PACKAGE SCHEDULE IS

-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Phil        1/15/01 created

    -- Default Task Calculation Type ID
    -- 10 = Fixed Unit, Effort Driven
    DEFAULT_TASK_CALC_TYPE_ID   constant number := 10;

   PROCEDURE store_assignment
     (
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_object_id IN NUMBER,
    i_percent IN NUMBER,
    i_role IN VARCHAR2,
    i_primary_owner IN NUMBER,
    i_start_date in date,
    i_end_date in date,
    i_actual_start IN DATE,
    i_actual_finish IN DATE,
    i_estimated_finish in DATE,
    i_work in NUMBER,
    i_work_units in NUMBER,
    i_work_complete in NUMBER,
    i_work_complete_units in NUMBER,
    i_is_complete in NUMBER,
    i_percent_complete in NUMBER,
    i_modified_by in NUMBER,
    i_record_status IN VARCHAR2,
    i_assignor_id IN NUMBER,
    o_status OUT NUMBER
);

  procedure store_task
     (
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_plan_id IN NUMBER,
    i_task_id IN NUMBER,
    i_task_name IN VARCHAR2,
    i_description IN vARCHAR2,
    i_task_type IN VARCHAR2,
    i_priority IN NUMBER,
    i_duration IN NUMBER,
    i_duration_units IN NUMBER,
    i_work IN NUMBER,
    i_work_units IN NUMBER,
    i_work_complete IN NUMBER,
    i_work_complete_units IN NUMBER,
    i_work_ms IN NUMBER,
    i_work_complete_ms IN NUMBER,
    i_work_percent_complete IN NUMBER,
    i_percent_complete IN NUMBER,
    i_date_finish IN DATE,
    i_date_start IN DATE,
    i_actual_start IN DATE,
    i_actual_finish IN DATE,
    i_critical_path IN NUMBER,
    i_record_status IN CHAR,
    i_parent_task_id IN NUMBER,
    i_ignore_times IN NUMBER,
    i_early_start IN DATE,
    i_early_finish IN DATE,
    i_late_start IN DATE,
    i_late_finish IN DATE,
    i_milestone IN NUMBER,
    i_constraint_type IN NUMBER,
    i_constraint_date IN DATE,
    i_deadline IN DATE,
    i_phase_id IN NUMBER,
    i_unallocated_wk_complete IN NUMBER,
    i_unallocated_wk_complete_unit IN NUMBER,
    i_unassigned_work IN NUMBER,
    i_unassigned_work_units IN NUMBER,
    i_calculation_type_id in NUMBER,
    i_wbs IN VARCHAR2,
    i_wbs_level IN VARCHAR2,
    i_unindent_status IN NUMBER,
    o_sequence_number IN OUT NUMBER,
    o_task_id OUT NUMBER
);

procedure add_comment (
    i_task_id   in  number,
    i_created_by_id in number,
    i_created_datetime  in date,
    i_is_comment_null in number,
    o_comment_clob   out clob
);

procedure MOVE_TASK_DOWN (
    i_task_id in number,
    i_plan_id in number
);

procedure MOVE_TASK_UP (
    i_task_id in number,
    i_plan_id in number
);

procedure RECALCULATE_SEQUENCE_NUMBERS (
    i_plan_id in number
);

procedure MOVE_TASK_TO_SEQUENCE_NUMBER (
    i_plan_id number,
    i_task_id number,
    i_new_seq number
);

procedure FIX_SUMMARY_TASK_TYPES (
    i_plan_id in number
);

function FIND_NEW_TASK_PARENT(
    i_task_id in number,
    i_task_above_id in number
) return NUMBER;

function GET_SCHEDULE_PERCENT_COMPLETE(
    i_plan_id in number
) return NUMBER;

procedure RESEQUENCE_SCHEDULE(
    i_plan_id in number
);

procedure RESEQUENCE_TASK_CHILDREN(
    i_plan_id in number,
    i_task_id in number,
    i_current_seq_number in out number
);

procedure STORE_BASELINE (
    i_object_id in NUMBER,
    i_baseline_name in VARCHAR2,
    i_baseline_description in VARCHAR2,
    i_creator_id in NUMBER,
    i_is_default_for_object in NUMBER,
    i_record_status in VARCHAR2,
    o_baseline_id in out NUMBER
);

procedure STORE_TASK_BASELINE (
    i_task_id in number,
    i_baseline_id in number
);

procedure STORE_TASK_VERSION (
    i_task_id in number,
    o_task_version_id out number
);

procedure STORE_PLAN_BASELINE (
    i_plan_id in number,
    i_baseline_id in number
);

procedure STORE_PLAN_VERSION (
    i_plan_id in number,
    o_plan_version_id out number
);
procedure STORE_TASK_DEPENDENCY (
    i_task_id IN NUMBER,
    i_dependency_type_id IN NUMBER,
    i_lag IN NUMBER,
    i_lag_units IN NUMBER,
    i_dependency_ID NUMBER,
    i_update_if_exists NUMBER
);
procedure STORE_TASK_DEPENDENCY_VERSION (
    i_task_id IN NUMBER,
    i_task_version_id in number
);
procedure STORE_PLAN (
    i_name IN VARCHAR2,
    i_description IN VARCHAR2,
    i_start_date IN DATE,
    i_end_date IN DATE,
    i_autocalculate_task_endpoints IN NUMBER,
    i_default_calendar_id IN NUMBER,
    i_timezone_id IN VARCHAR2,
    i_modified_by IN NUMBER,
    i_space_id IN NUMBER,
    i_default_task_calc_type_id IN NUMBER,
    i_earliest_start_date IN DATE,
    i_earliest_finish_date IN DATE,
    i_latest_start_date IN DATE,
    i_latest_finish_date IN DATE,
    i_start_constraint_type VARCHAR2,
    i_start_constraint_date DATE,
    io_plan_id IN OUT NUMBER
);

procedure REMOVE_BASELINE (
    i_baseline_id in number
);

Function IS_UP_TO_DATE
(
    i_plan_id IN NUMBER,
    i_last_loaded IN DATE
)
return NUMBER;

function TEST_CYCLIC (
    i_task_id NUMBER,
    i_new_dependency_id NUMBER
)
return NUMBER;

function GET_USERNAME (
	i_id in NUMBER
) return VARCHAR2;

END;
/
