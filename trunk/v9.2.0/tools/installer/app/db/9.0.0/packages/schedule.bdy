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
PACKAGE BODY SCHEDULE IS
-- Purpose: Provides procedures for creating and modifying tasks and
--          assignments
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Tim         07/22/01 Added "add_comment" procedure

Procedure STORE_TASK
(
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_plan_id IN NUMBER,
    i_task_id IN NUMBER,
    i_task_name IN VARCHAR2,
    i_description IN VARCHAR2,
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
    o_sequence_number IN OUT NUMBER,
    o_task_id OUT NUMBER
)

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Adam        01-Mar-00  Creation.
-- Robin       06-Apr-00  Added space_id and changed order of proc.
-- Robin       11-May-00  Changed error codes to coincide with new table.

IS

v_task_id NUMBER(20);
v_task_version_id NUMBER(20);

v_date_start DATE;
v_date_finish DATE;
v_critical_path PN_TASK.CRITICAL_PATH%TYPE;
v_work PN_TASK.WORK%TYPE;
v_work_units PN_TASK.WORK_UNITS%TYPE;
v_work_complete PN_TASK.WORK_COMPLETE%TYPE;
v_work_complete_units PN_TASK.WORK_COMPLETE_UNITS%TYPE;
v_duration PN_TASK.DURATION%TYPE;
v_duration_units PN_TASK.DURATION_UNITS%TYPE;
v_priority PN_TASK.PRIORITY%TYPE;
v_parent_task_id PN_TASK.PARENT_TASK_ID%TYPE;
v_prev_seq_number PN_TASK.SEQ%TYPE;
v_prev_parent_id PN_TASK.PARENT_TASK_ID%TYPE;
v_sibling_count NUMBER;
v_autocalculate_schedule NUMBER;
v_schedule_start DATE;
v_schedule_end DATE;
v_move_to_sequence_number NUMBER;
v_tasks_in_tree NUMBER;

v_actual_start DATE;
v_actual_finish DATE;

v_propagate NUMBER;

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100):= 'STORE_TASK';

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


BEGIN
    -- Adjust actual start and actual finish as necessary
    if ((i_actual_start is null) and (i_work_percent_complete > 0)) then
        v_actual_start := sysdate;
    else
        v_actual_start := i_actual_start;
    end if;

    if ((i_actual_finish is null) and (i_work_percent_complete >= 100)) then
        v_actual_finish := sysdate;
    else
        v_actual_finish := i_actual_finish;
    end if;

    -- NEW TASK, INSERT
    IF ((i_task_id IS NULL) OR (i_task_id = ''))    THEN
        v_task_id := BASE.CREATE_OBJECT('task', i_person_id, i_record_status);
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_task_id, 'task', i_space_id, i_person_id);

        v_move_to_sequence_number := o_sequence_number;
        SELECT nvl(max(seq),0)+1 into o_sequence_number
        from
          pn_task t,
          pn_plan_has_task pht
        where
          t.task_id = pht.task_id
          and pht.plan_id = i_plan_id
          and t.record_status = 'A';

        INSERT INTO pn_task (
             task_id, task_name, task_desc, task_type, duration, duration_units, work, work_units, work_complete, date_start,
             work_complete_units, work_ms, work_complete_ms, date_finish, actual_start, actual_finish, priority, work_percent_complete, percent_complete, critical_path,
             date_modified, modified_by, record_status, parent_task_id, seq, ignore_times_for_dates, early_start,
             early_finish, late_start, late_finish, is_milestone, constraint_type, constraint_date, deadline,
             calculation_type_id, unallocated_work_complete, unallocated_work_complete_unit, unassigned_work, unassigned_work_units
        ) VALUES (
             v_task_id, i_task_name, i_description, i_task_type, i_duration, i_duration_units, i_work, i_work_units, i_work_complete,
             i_date_start, i_work_complete_units, i_work_ms, i_work_complete_ms, i_date_finish, v_actual_start, v_actual_finish, i_priority,
             i_work_percent_complete, i_percent_complete, i_critical_path, SYSDATE, i_person_id, i_record_status, i_parent_task_id,
             o_sequence_number, i_ignore_times, i_early_start, i_early_finish, i_late_start, i_late_finish, i_milestone,
             i_constraint_type, i_constraint_date, i_deadline, i_calculation_type_id, i_unallocated_wk_complete, i_unallocated_wk_complete_unit,
             i_unassigned_work, i_unassigned_work_units
        );

        -- add the task to the plan
        INSERT INTO pn_plan_has_task
            (plan_id, task_id)
            VALUES
            (i_plan_id, v_task_id);

        o_task_id := v_task_id;
        
        -- By default, the task is not shareable.
        v_propagate := SHARING.GET_PROPAGATE_TO_CHILDREN(i_plan_id);
        SHARING.STORE_SHARE(o_task_id, 0, i_plan_id, i_space_id, v_propagate, 1);

        -- If this is a task whose plan says to always share a task, make sure that happens
        -- SHARING.INHERIT_FROM_PARENT(i_plan_id, o_task_id);

        -- Store this version in the history of the task
        STORE_TASK_VERSION(o_task_id, v_task_version_id);

        if (v_move_to_sequence_number is not null and v_move_to_sequence_number <> 0) then
            MOVE_TASK_TO_SEQUENCE_NUMBER(i_plan_id, v_task_id, v_move_to_sequence_number);

            --Assign variable to out variable so caller can learn new sequence #
            o_sequence_number := v_move_to_sequence_number;
        elsif (i_parent_task_id is not null) then
            select     max(seq)+1 into o_sequence_number
            from       pn_task t
            where      t.record_status = 'A'
              and      t.task_id <> v_task_id
            start with t.task_id = i_parent_task_id
            connect by t.parent_task_id = prior t.task_id
            order by   t.date_start, t.rowid;

            MOVE_TASK_TO_SEQUENCE_NUMBER(i_plan_id, v_task_id, o_sequence_number);

            --Now makes sure the parent task knows that it is a summary task
            update pn_task t
            set task_type = 'summary'
            where t.task_id = i_parent_task_id;
        end if;


    -- EXISTING TASK, UPDATE
    ELSE
        o_task_id := i_task_id;

        -- Collect old sequence number and parent task id info just in case we
        -- need to change the sequence number
        select parent_task_id, seq into v_prev_parent_id, v_prev_seq_number
        from pn_task
        where task_id = i_task_id;

        --Figure out how many tasks (including the task itself) are in the
        --tree of tasks that has the task we are saving as its head
        select count(*) into v_tasks_in_tree
        from pn_task t
        where t.record_status = 'A'
        connect by t.parent_task_id = prior t.task_id
        start with t.task_id = i_task_id;

        --If the parent has changed, the sequence number must too
        if (((v_prev_parent_id is null) and (i_parent_task_id is not null)) or
            ((v_prev_parent_id is not null) and (i_parent_task_id is not null) and (v_prev_parent_id <> i_parent_task_id))) then

            --The task didn't used to have a parent, but now it does.
            select     nvl(max(seq),0)+1 into o_sequence_number
            from       pn_task t
            where      t.record_status = 'A'
              and      t.task_id <> i_task_id
            start with t.task_id = i_parent_task_id
            connect by t.parent_task_id = prior t.task_id
            order by   t.date_start, t.rowid;

            -- If the old sequence number was in the middle of this tree, we need
            -- to account for its placement.
            if (o_sequence_number > v_prev_seq_number) then
                o_sequence_number := o_sequence_number - v_tasks_in_tree;
            end if;

            MOVE_TASK_TO_SEQUENCE_NUMBER(i_plan_id, i_task_id, o_sequence_number);

        elsif ((v_prev_parent_id is not null) and (i_parent_task_id is null)) then
            --The task used to have a parent, now it doesn't.
            select     max(seq)+1 into o_sequence_number
            from       pn_task t
            where      t.record_status = 'A'
              and      t.task_id <> i_task_id
            start with t.task_id = v_prev_parent_id
            connect by t.parent_task_id = prior t.task_id
            order by   t.date_start, t.rowid;

            -- If the old sequence number was in the middle of this tree, we need
            -- to account for its placement.
            if (o_sequence_number > v_prev_seq_number) then
                o_sequence_number := o_sequence_number - v_tasks_in_tree;
            end if;

            MOVE_TASK_TO_SEQUENCE_NUMBER(i_plan_id, i_task_id, o_sequence_number);
        else
            o_sequence_number := v_prev_seq_number;
        end if;

        -- get the task date info. Determine there is baseline change.
        SELECT
            date_start, date_finish, priority, parent_task_id, critical_path,
            work, work_units, work_complete, work_complete_units, duration, duration_units
        INTO
            v_date_start, v_date_finish, v_priority, v_parent_task_id,
            v_critical_path, v_work, v_work_units, v_work_complete, v_work_complete_units, 
            v_duration, v_duration_units
        FROM
            pn_task
        WHERE
        	task_id = i_task_id;

        -- Perform the actual task update
        UPDATE
    	    pn_task
        SET
        	task_id = i_task_id,
            task_name = i_task_name,
            task_desc = i_description,
            task_type = i_task_type,
            duration = i_duration,
            duration_units = i_duration_units,
            work_ms = i_work_ms,
            work_complete_ms = i_work_complete_ms,
            work = i_work,
            work_units = i_work_units,
            work_complete = i_work_complete,
            date_start = i_date_start,
            work_complete_units = i_work_complete_units,
            date_finish = i_date_finish,
            actual_start = v_actual_start,
            actual_finish = v_actual_finish,
            priority = i_priority,
            percent_complete = i_percent_complete,
            work_percent_complete = i_work_percent_complete,
            date_modified = SYSDATE,
            modified_by = i_person_id,
            record_status = i_record_status,
            parent_task_id = i_parent_task_id,
            critical_path = i_critical_path,
            ignore_times_for_dates = i_ignore_times,
            early_start = i_early_start,
            early_finish = i_early_finish,
            late_start = i_late_start,
            late_finish = i_late_finish,
            is_milestone = i_milestone,
            constraint_type = i_constraint_type,
            constraint_date = i_constraint_date,
            deadline = i_deadline,
            calculation_type_id = i_calculation_type_id,
            unallocated_work_complete = i_unallocated_wk_complete,
            unallocated_work_complete_unit = i_unallocated_wk_complete_unit,
            unassigned_work = i_unassigned_work,
            unassigned_work_units = i_unassigned_work_units
        WHERE
        	task_id = i_task_id;

        IF ((NOT UTIL.COMPARE_DATES(i_date_start, v_date_start)) OR
            (NOT UTIL.COMPARE_DATES(i_date_finish, v_date_finish)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_critical_path, v_critical_path)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_work, v_work)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_work_units, v_work_units)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_work_complete, v_work_complete)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_work_complete_units, v_work_complete_units)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_duration, v_duration)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_duration_units, v_duration_units)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_parent_task_id, v_parent_task_id))
            )
        THEN
            -- Store this version in the history of the task
            STORE_TASK_VERSION(i_task_id, v_task_version_id);
        END IF;

        --If there was an old parent_task id that is no longer the parent, make
        --sure its task type is correct.
        if ((i_parent_task_id is null and v_parent_task_id is not null) or
            (i_parent_task_id <> v_parent_task_id))then

            select
                count(*) into v_sibling_count
            from
                pn_task t
            where
                t.record_status = 'A'
                and t.parent_task_id = v_parent_task_id;

            if (v_sibling_count = 0) then
                update pn_task
                set task_type = 'task', work = 1, work_units = 8
                where task_id = v_parent_task_id;
            end if;
        end if;

        if (i_parent_task_id is not null) then
            update pn_task
            set task_type = 'summary'
            where task_id = i_parent_task_id;
        end if;
    END IF; -- insert/update

    -- If there is a phase specified, store it
    delete from pn_phase_has_task
    where
      task_id = o_task_id;

    if (i_phase_id is not null) then
        insert into pn_phase_has_task
          (phase_id, task_id)
        values
          (i_phase_id, o_task_id);
    end if;

    --If the schedule for this task does not use autocalculation, update the
    --start and end date based on the tasks that are already there
    select date_start, date_end, autocalculate_task_endpoints into v_schedule_start,
           v_schedule_end, v_autocalculate_schedule
    from pn_plan
    where plan_id = i_plan_id;

    if (v_autocalculate_schedule = 0) then
        if ((i_date_start < v_schedule_start) or (v_schedule_start is null)) then
            update pn_plan
            set date_start = i_date_start
            where plan_id = i_plan_id;
        end if;

        if ((i_date_finish > v_schedule_end) or (v_schedule_end is null)) then
            update pn_plan
            set date_end = i_date_finish
            where plan_id = i_plan_id;
        end if;
    end if;
END store_task;

procedure STORE_PLAN
(
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
)
is
    v_plan_version_id NUMBER;
    v_baseline_exists NUMBER;

    v_current_baseline_id NUMBER;
    v_baseline_start DATE;
    v_baseline_end DATE;
    v_default_task_calc_type_id NUMBER := i_default_task_calc_type_id;

    --date value to replace date
    v_compare BOOLEAN;
    v_old_date_start DATE;
    v_end_date_changed BOOLEAN;
    v_old_date_end DATE;
    v_old_autocalc_task_endpoints NUMBER;
    v_old_timezone_id VARCHAR(2000);
    v_old_baseline_start DATE;
    v_old_baseline_end DATE;
    v_old_def_task_calc_type_id NUMBER;
begin
    -- If no task calculation type was specified (expected when creating plans)
    -- Use the default
    if (v_default_task_calc_type_id is null) then
        v_default_task_calc_type_id := DEFAULT_TASK_CALC_TYPE_ID;
    end if;

    if (io_plan_id is null) then
        --Plan is new, save it
        io_plan_id := BASE.CREATE_OBJECT('plan', 1, 'A');

        insert into pn_plan
          (plan_id, plan_name, plan_desc, date_start, date_end,
           autocalculate_task_endpoints, default_calendar_id, timezone_id,
           baseline_start, baseline_end, modified_by, modified_date, default_task_calc_type_id,
           earliest_start_date, earliest_finish_date, latest_start_date, latest_finish_date,
           constraint_type_id, constraint_date)
        values
          (io_plan_id, i_name, i_description, i_start_date, i_end_date,
           i_autocalculate_task_endpoints, i_default_calendar_id, i_timezone_id,
           null, null, i_modified_by, SYSDATE, v_default_task_calc_type_id,
           i_earliest_start_date, i_earliest_finish_date, i_latest_start_date,
           i_latest_finish_date, i_start_constraint_type, i_start_constraint_date);

        insert into pn_space_has_plan
          (space_id, plan_id)
        values
          (i_space_id, io_plan_id);

        --We always store the initial version
        STORE_PLAN_VERSION(io_plan_id, v_plan_version_id);
    else
        --Get the current default baseline
        begin
            select b.baseline_id, pv.date_start, pv.date_end
            into v_current_baseline_id, v_baseline_start, v_baseline_end
            from pn_baseline b, pn_baseline_has_plan bhp, pn_plan_version pv
            where
              b.object_id = io_plan_id
              and b.is_default_for_object = 1
              and b.baseline_id = bhp.baseline_id
              and bhp.plan_version_id = pv.plan_version_id;
        exception
            when no_data_found then
                v_current_baseline_id := null;
        end;

        --Grab information about the schedule before the update
        select
          date_start, date_end, autocalculate_task_endpoints, timezone_id,
          baseline_start, baseline_end, default_task_calc_type_id
        into
          v_old_date_start, v_old_date_end, v_old_autocalc_task_endpoints,
          v_old_timezone_id, v_old_baseline_start, v_old_baseline_end, v_old_def_task_calc_type_id
        from
          pn_plan
        where
          plan_id = io_plan_id;

        --Do the actual update of the plan
        update
          pn_plan
        set
          plan_name = i_name,
          plan_desc = i_description,
          date_start = i_start_date,
          date_end = i_end_date,
          autocalculate_task_endpoints = i_autocalculate_task_endpoints,
          default_calendar_id = i_default_calendar_id,
          timezone_id = i_timezone_id,
          baseline_start = v_baseline_start,
          baseline_end = v_baseline_end,
          modified_by = i_modified_by,
          modified_date = SYSDATE,
          baseline_id = v_current_baseline_id,
          default_task_calc_type_id = v_default_task_calc_type_id,
          earliest_start_date = i_earliest_start_date,
          earliest_finish_date = i_earliest_finish_date,
          latest_start_date = i_latest_start_date,
          latest_finish_date = i_latest_finish_date,
          constraint_type_id = i_start_constraint_type,
          constraint_date = i_start_constraint_date
        where
          plan_id = io_plan_id;

        --Store a plan version if dates or other properties changed
        if ((NOT UTIL.COMPARE_DATES(v_old_date_start, i_start_date)) OR
            (NOT UTIL.COMPARE_DATES(v_old_date_end, i_end_date)) OR
            (v_old_autocalc_task_endpoints <> i_autocalculate_task_endpoints) OR
            (NOT UTIL.COMPARE_STRINGS(v_old_timezone_id, i_timezone_id)) OR
            (NOT UTIL.COMPARE_DATES(v_old_baseline_start, v_baseline_start)) OR
            (NOT UTIL.COMPARE_DATES(v_old_baseline_end, v_baseline_end)) OR
            (v_old_def_task_calc_type_id <> v_default_task_calc_type_id)) then
            STORE_PLAN_VERSION(io_plan_id, v_plan_version_id);
        end if;
    end if;
end STORE_PLAN;

Procedure STORE_ASSIGNMENT
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_object_id IN NUMBER,
    i_percent IN NUMBER,
    i_role IN VARCHAR2,
    i_primary_owner IN NUMBER,
    i_start_date in date,
    i_end_date in date,
    i_actual_start in date,
    i_actual_finish in date,
    i_estimated_finish in date,
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
)
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Adam        01-Mar-00  Creation.
-- Robin       06-Apr-00  Added space_id and changed order of proc.
-- Robin       11-May-00  Changed error codes to coincide with new table.
-- Tim         10-Jul-01  Added due_datetime
-- Sachin      07-Jul-08  Added assignor concept
IS

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100) := 'STORE_ASSIGNMENT';

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

BEGIN

    -- if this assignment is set as a primary owner then update an existing primary owner
    IF (i_primary_owner = 1) THEN
        UPDATE pn_assignment
        SET is_primary_owner = 0
        WHERE space_id = i_space_id and
            person_id = i_person_id and
            object_id = i_object_id and
            i_primary_owner != 0;
    END IF;

    BEGIN

        INSERT INTO pn_assignment
            (space_id, person_id, assignor_id, object_id, status_id, percent_allocated, role,
             is_primary_owner, start_date, end_date, actual_start, actual_finish,
             estimated_finish, work, work_units, work_complete,
             work_complete_units, is_complete, percent_complete, modified_by,
             modified_date, record_status, date_created)
          VALUES
            (i_space_id, i_person_id, i_assignor_id, i_object_id, 20, i_percent, i_role,
            i_primary_owner, i_start_date, i_end_date, i_actual_start,
            i_actual_finish, i_estimated_finish, i_work, i_work_units,
            i_work_complete, i_work_complete_units, i_is_complete, i_percent_complete,
            i_modified_by, SYSDATE, i_record_status, SYSDATE);


    EXCEPTION
    -- assignment already exists
        WHEN e_unique_constraint THEN

            UPDATE pn_assignment
              SET status_id = 20,
                  percent_allocated = i_percent,
                  role = i_role,
                  is_primary_owner = i_primary_owner,
                  start_date = i_start_date,
                  end_date = i_end_date,
                  actual_start = i_actual_start,
                  actual_finish = i_actual_finish,
                  estimated_finish = i_estimated_finish,
                  work = i_work,
                  work_units = i_work_units,
                  work_complete = i_work_complete,
                  work_complete_units = i_work_complete_units,
                  is_complete = i_is_complete,
                  percent_complete = i_percent_complete,
                  modified_by = i_modified_by,
                  modified_date = SYSDATE,
                  record_status = i_record_status,
                  assignor_id = i_assignor_id
              WHERE space_id = i_space_id
                AND person_id = i_person_id
                AND object_id = i_object_id;

    END;

    COMMIT;
    o_status := success;
END;


/**
 * Adds a comment to a task
 */
procedure add_comment (
    i_task_id   in  number,
    i_created_by_id in number,
    i_created_datetime  in date,
    i_is_comment_null in number,
    o_comment_clob   out clob
)
is

    v_seq               number;
    v_task_baseline_id  number;

begin
    -- Get unique seq number for this comment
    SELECT pn_object_sequence.nextval into v_seq FROM dual;

    -- Fetch the latest task baseline id
    select
        max(tv.task_version_id) into v_task_baseline_id
    from
        pn_task_version tv
    where
        tv.task_id = i_task_id;

    -- Add comment to task and basline
    if (i_is_comment_null = 1) then
        insert into pn_task_comment
            (task_id, baseline_id, seq, created_by_id, created_datetime, text_clob)
        values
            (i_task_id, v_task_baseline_id, v_seq, i_created_by_id, i_created_datetime, null)
        returning
            text_clob into o_comment_clob;
    else
        insert into pn_task_comment
            (task_id, baseline_id, seq, created_by_id, created_datetime, text_clob)
        values
            (i_task_id, v_task_baseline_id, v_seq, i_created_by_id, i_created_datetime, empty_clob())
        returning
            text_clob into o_comment_clob;
    end if;

end add_comment;

procedure MOVE_TASK_DOWN (
    i_task_id in number,
    i_plan_id in number
)
is
    current_seq number;
    parent_id number;
    highest_seq_in_group number;
    exchange_task_id number;

    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.DEMOTE_TASK';
begin
    --Get some local variables
    select seq, parent_task_id into current_seq, parent_id
    from pn_task t
    where t.task_id = i_task_id;

    --Make sure that the task isn't at the bottom of the list.  If it is, it
    --cannot be demoted.
    if (parent_id is not null) then
        select max(t.seq) into highest_seq_in_group
        from pn_task t, pn_plan_has_task pht
        where t.task_id = pht.task_id
        and t.parent_task_id = parent_id
        and pht.plan_id = i_plan_id
        and t.record_status = 'A';
    else
        select max(t.seq) into highest_seq_in_group
        from pn_task t, pn_plan_has_task pht
        where t.task_id = pht.task_id
        and t.parent_task_id is null
        and pht.plan_id = i_plan_id
        and t.record_status = 'A';
    end if;

    if (highest_seq_in_group = current_seq) then
        return;
    end if;

    --Figure out which sequence number we are exchanging with
    if (parent_id is not null) then
        select task_id into exchange_task_id
        from pn_task t
        where t.parent_task_id = parent_id
        and t.record_status = 'A'
        and seq =
            (select min(t2.seq)
             from pn_task t2
             where t2.parent_task_id = parent_id
               and t2.record_status = 'A'
               and t2.seq > current_seq);
    else
        select t.task_id into exchange_task_id
        from pn_task t, pn_plan_has_task pht
        where t.parent_task_id is null
        and t.task_id = pht.task_id
        and pht.plan_id = i_plan_id
        and t.record_status = 'A'
        and seq =
            (select min(t2.seq)
             from pn_task t2, pn_plan_has_task pht
             where t2.parent_task_id is null
               and t2.task_id = pht.task_id
               and pht.plan_id = i_plan_id
               and t2.record_status = 'A'
               and t2.seq > current_seq);
    end if;

    -- We just use "MOVE_TASK_UP" to move up the task beneath us
    MOVE_TASK_UP(exchange_task_id, i_plan_id);
exception
    when no_data_found then
        return;
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end MOVE_TASK_DOWN;

procedure MOVE_TASK_UP (
    i_task_id in number,
    i_plan_id in number
)
is
    lowest_seq_in_group number;
    current_seq number;
    new_seq number;
    parent_id number;
    exchange_task_id number;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.MOVE_TASK_UP';

    --The tree of tasks starting with the task we are moving
    cursor tasks_to_move_up(i_head_id number) is
        select task_id
        from pn_task t
        where t.record_status = 'A'
        connect by t.parent_task_id = prior t.task_id
        start with t.task_id = i_head_id;
    move_up_row tasks_to_move_up%rowtype;

    --The tree of tasks we are exchanging our task with
    cursor tasks_to_move_down(i_head_id number) is
        select task_id
        from pn_task t
        where t.record_status = 'A'
        connect by t.parent_task_id = prior t.task_id
        start with t.task_id = i_head_id;
    move_down_row tasks_to_move_down%rowtype;
begin
    -- Get local variables we are going to need
    select t.seq, t.parent_task_id into current_seq, parent_id
    from pn_task t, pn_plan_has_task pht
    where pht.task_id = t.task_id and t.task_id = i_task_id;

    --If the task is at the top of the list, we cannot promote it
    if (parent_id is not null) then
        select min(t.seq) into lowest_seq_in_group
        from pn_task t, pn_plan_has_task pht
        where t.task_id = pht.task_id
        and pht.plan_id = i_plan_id
        and t.parent_task_id = parent_id
        and t.record_status = 'A';
    else
        select min(t.seq) into lowest_seq_in_group
        from pn_task t, pn_plan_has_task pht
        where t.task_id = pht.task_id
        and pht.plan_id = i_plan_id
        and t.parent_task_id is null
        and t.record_status = 'A';
    end if;

    if (current_seq = lowest_seq_in_group) then
        return;
    end if;

    --Figure out which sequence number we are exchanging with
    if (parent_id is not null) then
        select seq, task_id into new_seq, exchange_task_id
        from pn_task t
        where t.parent_task_id = parent_id
        and t.record_status = 'A'
        and seq =
            (select max(t2.seq)
             from pn_task t2
             where t2.parent_task_id = parent_id
               and t2.record_status = 'A'
               and t2.seq < current_seq);
    else
        select seq, t.task_id into new_seq, exchange_task_id
        from pn_task t, pn_plan_has_task pht
        where t.parent_task_id is null
        and t.task_id = pht.task_id
        and pht.plan_id = i_plan_id
        and t.record_status = 'A'
        and seq =
            (select max(t2.seq)
             from pn_task t2, pn_plan_has_task pht
             where t2.parent_task_id is null
               and t2.task_id = pht.task_id
               and pht.plan_id = i_plan_id
               and t2.record_status = 'A'
               and t2.seq < current_seq);
    end if;

    --It is important to note that "moving up" a task doesn't always imply that
    --we are only exchanging two tasks.  If the one we are moving up has
    --children, we need to move them too.

    --Get the tasks we are moving up
    open tasks_to_move_up(i_task_id);

    --Get the tasks we are demoting
    open tasks_to_move_down(exchange_task_id);

    --Move up the tasks
    loop
        fetch tasks_to_move_up into move_up_row;
        exit when tasks_to_move_up%NOTFOUND;

        update pn_task set seq = new_seq where task_id = move_up_row.task_id;
        new_seq := new_seq +1;
    end loop;
    close tasks_to_move_up;

    --Move down the tasks we are exchanging with
    loop
        fetch tasks_to_move_down into move_down_row;
        exit when tasks_to_move_down%NOTFOUND;

        update pn_task set seq = new_seq where task_id = move_down_row.task_id;
        new_seq := new_seq +1;
    end loop;
    close tasks_to_move_down;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end MOVE_TASK_UP;

procedure RECALCULATE_SEQUENCE_NUMBERS (
    i_plan_id in number
)
is
    new_seq number := 1;
    lowest_seq_in_group number;
    cursor task_rows is
        select t.task_id
        from pn_task t, pn_plan_has_task pht
        where t.task_id = pht.task_id
          and pht.plan_id = i_plan_id
          and t.record_status = 'A'
        order by
          t.seq;
    task_row task_rows%ROWTYPE;

    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.RECALCULATE_SEQUENCE_NUMBERS';
begin
    for task_row in task_rows loop
        update pn_task
        set seq = new_seq
        where task_id = task_row.task_id;

        new_seq := new_seq + 1;
    end loop;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
END RECALCULATE_SEQUENCE_NUMBERS;

--Warning, this task can currently only move a single task.  It is not yet
--appropriate for a summary task.
procedure MOVE_TASK_TO_SEQUENCE_NUMBER (
    i_plan_id number,
    i_task_id number,
    i_new_seq number
)
is
    current_seq number;
    new_seq number;

    -- The number of tasks we are going to move
    num_tasks_to_move number;

    -- The actual tasks we are going to move
    cursor tasks_to_move(i_head_id number) is
    select task_id
    from pn_task t
    where t.record_status = 'A'
    connect by t.parent_task_id = prior t.task_id
    start with t.task_id = i_head_id;
    move_row tasks_to_move%rowtype;

    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.MOVE_TASK_TO_SEQUENCE_NUMBER';
begin
    --Determine what the current sequence number is so we can know if we are
    --moving up or down.
    select seq into current_seq from pn_task where task_id = i_task_id;

    if (current_seq = i_new_seq) then
        return;
    end if;

    --Get the size of the current "task tree"
    select count(*) into num_tasks_to_move
    from pn_task t
    where t.record_status = 'A'
    connect by t.parent_task_id = prior t.task_id
    start with t.task_id = i_task_id;

    if (i_new_seq < current_seq) then
        --We are moving the task upwards
        update pn_task
        set seq = seq + num_tasks_to_move
        where task_id in (
            select t.task_id
            from pn_task t, pn_plan_has_task pht
            where
                t.task_id = pht.task_id
                and pht.plan_id = i_plan_id
                and t.seq >= i_new_seq
                and t.seq < current_seq
                and t.record_status = 'A'
        );

        --Let the code that is going to move the tasks where the tasks are going
        new_seq := i_new_seq;
    else
        --We are moving the task downward in the list
        update pn_task
        set seq = seq - num_tasks_to_move
        where task_id in (
            select t.task_id
            from pn_task t, pn_plan_has_task pht
            where
                t.task_id = pht.task_id
                and pht.plan_id = i_plan_id
                and seq > current_seq + num_tasks_to_move -1
                and seq <= i_new_seq + num_tasks_to_move -1
                and t.record_status = 'A'
        );

        --We have to adjust the target sequence number because we just moved
        --them all.
        new_seq := i_new_seq;
    end if;

    --Get the tasks we are demoting
    open tasks_to_move(i_task_id);

    --Move down the tasks we are exchanging with
    loop
        fetch tasks_to_move into move_row;
        exit when tasks_to_move%NOTFOUND;

        update pn_task set seq = new_seq where task_id = move_row.task_id;
        new_seq := new_seq +1;
    end loop;
    close tasks_to_move;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end MOVE_TASK_TO_SEQUENCE_NUMBER;

-- sjmittal: calculation_type_id is handled in java so removing it from here
procedure FIX_SUMMARY_TASK_TYPES(
    i_plan_id in number
) is
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.FIX_SUMMARY_TASK_TYPES';
begin
    update pn_task t2
    set task_type = 'task'
    where exists
      (select 1
         from pn_plan_has_task pht, pn_task t
        where pht.task_id = t.task_id
          and pht.plan_id = i_plan_id
          and t.task_type='summary'
          and t2.task_id = t.task_id
          and t.record_status = 'A');

    update pn_task t
    set t.task_type = 'summary'
    where exists
      (select 1
         from pn_plan_has_task pht, pn_task t2
        where pht.plan_id = i_plan_id
          and pht.task_id = t2.task_id
          and t2.record_status = 'A'
          and t2.parent_task_id = t.task_id);
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end FIX_SUMMARY_TASK_TYPES;

--This method finds the new parent task id if the task is intended underneath
--the i_task_above_id variable.  It will return that parent_task_id.
function FIND_NEW_TASK_PARENT(
    i_task_id in number,
    i_task_above_id in number
)
    return NUMBER
is
    -- Mechanism for finding the proper parent
    cursor parent_finder (
        task_above_id in number,
        existing_parent in number
    ) is
        select task_id
        from pn_task t
        start with t.task_id = task_above_id
        connect by t.task_id = prior t.parent_task_id
          and t.task_id != existing_parent
        order by level;

    parent_id_rec parent_finder%ROWTYPE;
    v_current_parent_id number;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.FIND_NEW_TASK_PARENT';
begin
    --Figure out what the current parent id of this task is.
    --If the parent_task_id is null, the t.task_id != existing_parent did not
    --work in the parent_finder.  It would have worked if we used
    --t.task_id is null, but null doesn't work too well in comparisons.  Instead,
    --we are just making the parent_task_id zero if a task doesn't have one.
    select nvl(parent_task_id,0) into v_current_parent_id from pn_task where task_id = i_task_id;

    --Open up the parent_finder cursor, which we will use to traverse up
    --the tree
    open parent_finder(i_task_above_id, v_current_parent_id);

    --This is a bit clunky.  We need the last task in the list, so we just
    --cycle through all of them until we find it.
    loop
        fetch parent_finder into parent_id_rec;
        exit when parent_finder%NOTFOUND;
    end loop;

    close parent_finder;

    return(parent_id_rec.task_id);
end FIND_NEW_TASK_PARENT;

--This method finds the "natural sequence order" for a schedule and changes all
--the sequence numbers in the schedule to match that order.  This is useful if
--the sequence numbers are wrong due to some Project.net bug.
procedure RESEQUENCE_SCHEDULE(
    i_plan_id in number
)
is
    current_seq_number number := 1;

    cursor task_rows (i_plan_id in number) is
          select  t.task_id, pht.plan_id, seq, t.date_start, decode(t2.parent_task_id,null,0,1) as has_children
            from  pn_plan_has_task pht, pn_task t,
                  (select parent_task_id from pn_task t2 where t2.parent_task_id is not null group by parent_task_id) t2
           where  pht.task_id = t.task_id
                  and t.record_status = 'A'
                  and t2.parent_task_id(+) = t.task_id
                  and t.parent_task_id is null
                  and pht.plan_id = i_plan_id
        order by  pht.plan_id, t.date_start, t.rowid;

    task_row task_rows%ROWTYPE;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.RESEQUENCE_SCHEDULE';
begin
    --Iterate through all of the tasks
    for task_row in task_rows(i_plan_id) loop
        update pn_task t
        set seq = current_seq_number
        where t.task_id = task_row.task_id;

        --Increment the sequence number for the next pass
        current_seq_number := current_seq_number + 1;

        if (task_row.has_children = 1) then
            --Fix the sequence number for subtasks
            resequence_task_children(i_plan_id, task_row.task_id, current_seq_number);
        end if;
    end loop;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end RESEQUENCE_SCHEDULE;

procedure RESEQUENCE_TASK_CHILDREN(
    i_plan_id in number,
    i_task_id in number,
    i_current_seq_number in out number
)
is
    cursor subtask_rows (i_task_id in number, i_plan_id in number) is
          select  t.task_id, pht.plan_id, seq, t.date_start, decode(t2.parent_task_id,null,0,1) as has_children
            from  pn_plan_has_task pht, pn_task t,
                  (select parent_task_id from pn_task t2 where t2.parent_task_id is not null group by parent_task_id) t2
           where  pht.task_id = t.task_id
                  and t.record_status = 'A'
                  and t2.parent_task_id(+) = t.task_id
                  and t.parent_task_id = i_task_id
                  and pht.plan_id = i_plan_id
        order by  pht.plan_id, t.date_start, t.rowid;
    subtask_row subtask_rows%ROWTYPE;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.RESEQUENCE_TASK_CHILDREN';
begin
    for subtask_row in subtask_rows(i_task_id, i_plan_id) loop
        update pn_task t
        set seq = i_current_seq_number
        where t.task_id = subtask_row.task_id;

        --Increment the sequence number for the next pass
        i_current_seq_number := i_current_seq_number + 1;

        if (subtask_row.has_children = 1) then
            --Fix the sequence number for subtasks
            resequence_task_children(i_plan_id, subtask_row.task_id, i_current_seq_number);
        end if;
    end loop;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end RESEQUENCE_TASK_CHILDREN;

procedure STORE_BASELINE (
    i_object_id in NUMBER,
    i_baseline_name in VARCHAR2,
    i_baseline_description in VARCHAR2,
    i_creator_id in NUMBER,
    i_is_default_for_object in NUMBER,
    i_record_status in VARCHAR2,
    o_baseline_id in out NUMBER
)
is
    v_baseline_exists NUMBER := 0;
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.STORE_BASELINE';
begin
    if (o_baseline_id is not null) then
        select count(*) into v_baseline_exists
        from pn_baseline
        where baseline_id = o_baseline_id;
    end if;

    --If the new object is now the default object, clear out the default
    --flag from any other baseline
    if (i_is_default_for_object = 1) then
        update pn_baseline
        set is_default_for_object = 0
        where object_id = i_object_id;
    end if;

    if (v_baseline_exists > 0) then
        --Baseline already exists, update it
        update pn_baseline
        set object_id = i_object_id,
            name = i_baseline_name,
            description = i_baseline_description,
            date_modified = sysdate,
            record_status = i_record_status,
            is_default_for_object = i_is_default_for_object
        where
            baseline_id = o_baseline_id;
    else
        --This is a new baseline, create it

        --Get an id for this baseline
        o_baseline_id := BASE.CREATE_OBJECT('baseline', i_creator_id, 'A');

        --Insert baseline record
        insert into pn_baseline
          (baseline_id, object_id, name, description, date_created, date_modified, record_status, is_default_for_object)
        values
          (o_baseline_id, i_object_id, i_baseline_name, i_baseline_description, SYSDATE, SYSDATE, i_record_status, i_is_default_for_object);
    end if;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end STORE_BASELINE;

procedure REMOVE_BASELINE (
    i_baseline_id in number
)
is
begin
    delete from pn_baseline_has_task
    where baseline_id = i_baseline_id;

    delete from pn_baseline_has_plan
    where baseline_id = i_baseline_id;

    delete from pn_baseline b
    where baseline_id = i_baseline_id;
end REMOVE_BASELINE;

procedure STORE_PLAN_BASELINE (
    i_plan_id in number,
    i_baseline_id in number
)
is
    cursor plan_tasks (i_plan_id number) is
        select
          t.task_id
        from
          pn_task t, pn_plan_has_task pht
        where
          t.task_id = pht.task_id
          and t.record_status = 'A'
          and pht.plan_id = i_plan_id;
    plan_task_row plan_tasks%ROWTYPE;

    v_plan_version_id NUMBER;
    v_baseline_is_default NUMBER;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.STORE_PLAN_BASELINE';
begin
    --Now, iterate through all of the schedule entries and create a baseline entry
    for plan_task_row in plan_tasks(i_plan_id) loop
        STORE_TASK_BASELINE(plan_task_row.task_id, i_baseline_id);
    end loop;

    --Update the denormalized baseline start and end dates
    select is_default_for_object into v_baseline_is_default
    from pn_baseline
    where baseline_id = i_baseline_id;

    if (v_baseline_is_default = 1) then
        --Update the baseline columns to equal the new baseline.
        update pn_plan p
        set (baseline_start, baseline_end, baseline_id) =
          (select date_start, date_end, i_baseline_id
             from pn_plan p2
            where p2.plan_id = i_plan_id)
        where
          p.plan_id = i_plan_id;
    end if;

    --Store the current plan as a version.  This may be a bit redundant (there
    --is already one that is nearly identical), but it is easy to do because we
    --don't easily know what that last version is.  Perhaps we ought to start
    --storing what that last version is.
    STORE_PLAN_VERSION(i_plan_id, v_plan_version_id);

    --Remove any entry in pn_baseline_has_plan for this specific plan_id and
    --baseline id
    delete from pn_baseline_has_plan
    where
      baseline_id = i_baseline_id
      and plan_id = i_plan_id;

    insert into pn_baseline_has_plan
      (baseline_id, plan_id, plan_version_id)
    values
      (i_baseline_id, i_plan_id, v_plan_version_id);
end STORE_PLAN_BASELINE;

procedure STORE_PLAN_VERSION (
    i_plan_id in number,
    o_plan_version_id out number
)
is
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.STORE_PLAN_VERSION';
begin
    SELECT pn_object_sequence.nextval into o_plan_version_id FROM dual;

    insert into pn_plan_version (
      plan_id,plan_version_id,plan_name,plan_desc,date_start,date_end,autocalculate_task_endpoints,
      overallocation_warning,default_calendar_id,timezone_id,baseline_start,baseline_end,modified_date,
      modified_by,baseline_id, default_task_calc_type_id
    )
    select
      i_plan_id,o_plan_version_id,plan_name,plan_desc,date_start,date_end,autocalculate_task_endpoints,
      overallocation_warning,default_calendar_id,timezone_id,baseline_start,baseline_end,modified_date,
      modified_by,baseline_id, default_task_calc_type_id
    from pn_plan p
    where p.plan_id = i_plan_id;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end STORE_PLAN_VERSION;

procedure STORE_TASK_BASELINE (
    i_task_id in number,
    i_baseline_id in number
)
is
    v_task_version_id NUMBER;
    v_task_in_baseline NUMBER;
    v_baseline_is_default NUMBER;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.ARCHIVE_TASK_VERSION';
begin
    --Create a new version of the task
    STORE_TASK_VERSION(i_task_id, v_task_version_id);

    --Update the baseline_has_task table
    select count(*) into v_task_in_baseline
    from pn_baseline_has_task
    where baseline_id = i_baseline_id
      and task_id = i_task_id;

    if (v_task_in_baseline > 0) then
        --There is already a record in the baseline table for this baseline id, update it
        update pn_baseline_has_task
        set task_version_id = v_task_version_id
        where baseline_id = i_baseline_id
          and task_id = i_task_id;
    else
        insert into pn_baseline_has_task
          (baseline_id, task_id, task_version_id)
        values
          (i_baseline_id, i_task_id, v_task_version_id);
    end if;

    --Update the denormalized baseline start and end dates
    select is_default_for_object into v_baseline_is_default
    from pn_baseline
    where baseline_id = i_baseline_id;

/*    if (v_baseline_is_default = 1) then
        update pn_task
        set (baseline_start, baseline_end) =
          (select date_start, date_finish
             from pn_task_version
            where task_version_id = v_task_version_id)
        where
          task_id = i_task_id;
    end if;
*/exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end STORE_TASK_BASELINE;

procedure STORE_TASK_VERSION (
    i_task_id in number,
    o_task_version_id out number
)
is
    v_baseline_id NUMBER;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.STORE_TASK_VERSION';
begin
    --Get a new ID for this baselined task id
    SELECT pn_object_sequence.nextval into o_task_version_id FROM dual;

    --Get the current baseline id
    begin
        select b.baseline_id into v_baseline_id
        from pn_baseline b, pn_baseline_has_task bht
        where b.baseline_id = bht.baseline_id
          and b.is_default_for_object = 1
          and bht.task_id = i_task_id;
    exception
        when no_data_found then
            v_baseline_id := null;
    end;

    --Create the copy of the task
    insert into pn_task_version (
      task_id,task_version_id,task_name,task_desc,task_type,duration,work,work_units,work_complete,
      date_start,work_complete_units,work_ms,work_complete_ms,date_finish,actual_start,actual_finish,priority,
      percent_complete,date_created,date_modified,modified_by,duration_units,parent_task_id,
      record_status,critical_path,seq,ignore_times_for_dates,is_milestone,early_start,
      early_finish,late_start,late_finish,work_percent_complete,baseline_id, calculation_type_id,
      unallocated_work_complete, unallocated_work_complete_unit
    )
    select
      i_task_id,o_task_version_id,t.task_name,t.task_desc,t.task_type,t.duration,t.work,t.work_units,t.work_complete,
      t.date_start,t.work_complete_units,t.work_ms,t.work_complete_ms,t.date_finish,t.actual_start,t.actual_finish,t.priority,
      t.percent_complete,t.date_created,t.date_modified,t.modified_by,t.duration_units,t.parent_task_id,
      t.record_status,t.critical_path,t.seq,t.ignore_times_for_dates,t.is_milestone,t.early_start,
      t.early_finish,t.late_start,t.late_finish,t.work_percent_complete, v_baseline_id, t.calculation_type_id,
      t.unallocated_work_complete, t.unallocated_work_complete_unit
    from
      pn_task t
    where
      t.task_id = i_task_id;

    STORE_TASK_DEPENDENCY_VERSION(i_task_id, o_task_version_id);
end STORE_TASK_VERSION;

procedure STORE_TASK_DEPENDENCY (
    i_task_id IN NUMBER,
    i_dependency_type_id IN NUMBER,
    i_lag IN NUMBER,
    i_lag_units IN NUMBER,
    i_dependency_ID NUMBER,
    i_update_if_exists NUMBER
)
is
    v_exists number;
begin
    select count(*) into v_exists from pn_task_dependency
    where
      task_id = i_task_id
      and dependency_id = i_dependency_id
      and dependency_type_id = i_dependency_type_id;

    if (v_exists = 0) then
        insert into pn_task_dependency
          (task_id, dependency_type_id, lag, lag_units, dependency_id)
        values
          (
            i_task_id,
            i_dependency_type_id,
            i_lag,
            i_lag_units,
            i_dependency_id
          );
    else
        if (i_update_if_exists > 0) then
            update pn_task_dependency
            set
              lag = i_lag,
              lag_units = i_lag_units
            where
              task_id = i_task_id
              and dependency_id = i_dependency_id
              and dependency_type_id = i_dependency_type_id;
        end if;
    end if;
end STORE_TASK_DEPENDENCY;

procedure STORE_TASK_DEPENDENCY_VERSION (
    i_task_id IN NUMBER,
    i_task_version_id in number
)
is
begin
    insert into pn_task_dependency_version
      (task_id, task_version_id, dependency_id, dependency_type_id, lag, lag_units)
    select
      task_id, i_task_version_id, dependency_id, dependency_type_id, lag, lag_units
    from
      pn_task_dependency
    where
      task_id = i_task_id;
end STORE_TASK_DEPENDENCY_VERSION;

Function IS_UP_TO_DATE
(
    i_plan_id IN NUMBER,
    i_last_loaded IN DATE
)
return NUMBER
is
  last_modified_date date;
begin
  select
    max(modified_date) into last_modified_date
  from
    (
      (select modified_date
         from pn_plan
        where plan_id = i_plan_id)
      UNION ALL
      (select t.date_modified
         from pn_plan_has_task pht,
              pn_task t
        where pht.task_id = t.task_id
          and pht.plan_id = i_plan_id
          and t.record_status = 'A')
      UNION ALL
      (select max(modified_date)
         from pn_plan_has_task pht,
              pn_assignment a
        where pht.task_id = a.object_id
          and pht.plan_id = i_plan_id
          and a.record_status = 'A')
    ) last_modified;

    if (i_last_loaded <= last_modified_date) then
        return 1;
    else
        return 0;
    end if;
end IS_UP_TO_DATE;


-- This function returns the percent complete (as a decimal)
-- calculated based on the work_ms and work_complete_ms of each schedule entry.
-- Note, summary tasks are not included in this calculation.
function GET_SCHEDULE_PERCENT_COMPLETE(
    i_plan_id in NUMBER
)
    return NUMBER
is
    v_work_complete NUMBER;
    v_work NUMBER;
    o_percent_complete NUMBER;
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.GET_WORK_PERCENT_COMPLETE';
begin

    -- first get the work for all active (non-summary) tasks in the plan
    select sum(t.work_ms) into v_work
    from pn_task t, pn_space_has_plan sp, pn_plan_has_task pt
    where sp.plan_id = i_plan_id
    and sp.plan_id = pt.plan_id
    and t.task_id = pt.task_id
    and t.record_status = 'A'
    and t.task_type != 'summary';

    -- next get the work complete for all active (non-summary) tasks in the plan
    select sum(t.work_complete_ms) into v_work_complete
    from pn_task t, pn_space_has_plan sp, pn_plan_has_task pt
    where sp.plan_id = i_plan_id
    and sp.plan_id = pt.plan_id
    and t.task_id = pt.task_id
    and t.record_status = 'A'
    and t.task_type != 'summary';

    if (v_work = 0) then
        o_percent_complete := 0;
    else
        o_percent_complete := (v_work_complete / v_work) * 100;
    end if;

    return o_percent_complete;

end GET_SCHEDULE_PERCENT_COMPLETE;

function TEST_CYCLIC (
    i_task_id NUMBER,
    i_new_dependency_id NUMBER
)
    return NUMBER
is
    is_summary_task number;
    dependency_count number;
    cycle_found number;
begin
    --
    --Set up the initial run.   This is a little bit different from subsequent
    --runs because we have a "hypothetical" dependency here too.
    --
    cycle_found := 0;

    --Insert task dependencies
    insert into PN_TASK_CYCLE_DEPENDENCIES
    select dependency_id from pn_task_dependency where task_id = i_task_id;

    --Insert summary children
    insert into PN_TASK_CYCLE_DEPENDENCIES
    select task_id from pn_task where parent_task_id = i_task_id;

    --Insert shared tasks
    insert into PN_TASK_CYCLE_DEPENDENCIES
    select exported_object_id from pn_shared where imported_object_id = i_task_id;

    --Insert our test dependency id
    insert into PN_TASK_CYCLE_DEPENDENCIES values (i_new_dependency_id);

    select count(*) into dependency_count from PN_TASK_CYCLE_DEPENDENCIES;
    while (dependency_count > 0 AND cycle_found = 0) LOOP
        --Test to see if we have a cycle
        select count(*) into cycle_found from PN_TASK_CYCLE_DEPENDENCIES
        where id = i_task_id;

        if (cycle_found > 0) then
            return 1;
        end if;

        --Move all rows from PN_TASK_CYCLE_DEPENDENCIES to PN_TASK_CYCLE_WORK
        delete from PN_TASK_CYCLE_WORK;
        insert into PN_TASK_CYCLE_WORK select id from PN_TASK_CYCLE_DEPENDENCIES;
        delete from PN_TASK_CYCLE_DEPENDENCIES;

        --
        -- Find all dependencies and put them in PN_TASK_CYCLE_DEPENDENCIES
        --

        --Direct task dependencies
        insert into PN_TASK_CYCLE_DEPENDENCIES
        select dependency_id from PN_TASK_DEPENDENCY
        where task_id in (select id from PN_TASK_CYCLE_WORK);

        --Summary children
        insert into PN_TASK_CYCLE_DEPENDENCIES
        select task_id from pn_task
        where parent_task_id in (select id from PN_TASK_CYCLE_WORK);

        --Shared tasks
        insert into PN_TASK_CYCLE_DEPENDENCIES
        select exported_object_id from pn_shared 
        where imported_object_id in (select id from PN_TASK_CYCLE_WORK);

        --
        --Get ready for the next loop iteration
        --
        select count(*) into dependency_count from PN_TASK_CYCLE_DEPENDENCIES;
    END LOOP;

    return 0;
end TEST_CYCLIC;

function GET_USERNAME (
	i_id in NUMBER
) return VARCHAR2
is
	i_user VARCHAR2(100) := '';
begin
	select username into i_user from pn_user where user_id = i_id and record_status = 'A';
	return i_user;
end GET_USERNAME;

end;
/
