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
CREATE OR REPLACE PACKAGE BODY workflow IS
--==================================================================
-- Purpose: Workflow procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        05-Sep-00  Created it.
-- TimM        12-Sep-00  Added i_space_id and insert of
--      pn_space_has_workflow records.
-- TimM        20-Oct-00  Added copy procedures
-- TimM        27-Oct-00  Modify CREATE_STEP, MODIFY_STEP, COPY_STEPS
--                        to deal with entry_status_id
-- TimM        01-Nov-00  Changed signature of ADD_ENVELOPE_OBJECT to
--                        change i_object_properties to i_object_properties_id
-- TimM        12-15-00   Change CREATE_HISTORY to use history_message_id for CLOB storage
-- Umesh       12-18-08   Change CREATE_ENVELOPE: overwite initial status only if step has it specified 
--==================================================================

/*--------------------------------------------------------------------
  PROCEDURE PROTOTYPES
  Required for private procedures so that they can be used
  lexically earlier than their definition.
--------------------------------------------------------------------*/

    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean;

    function getSpaceID(i_workflow_id in number)
        return number;

    procedure copy_workflow(
        i_to_space_id in number,
        i_workflow_id in number,
        i_created_by_id in number,
        i_copy_groups   in number);

    procedure copy_steps(
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_created_by_id     in number,
        i_copy_groups       in number);

    procedure copy_transitions (
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_created_by_id     in number,
        i_copy_groups       in number);

/*--------------------------------------------------------------------
  END OF PROCEDURE PROTOTYPES
--------------------------------------------------------------------*/


----------------------------------------------------------------------
-- CREATE_WORKFLOW
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    PROCEDURE create_workflow
      ( i_space_id       IN varchar2,
        i_workflow_name  IN varchar2,
        i_workflow_description IN varchar2,
        i_notes          IN varchar2,
        i_owner_id       IN varchar2,
        i_is_published   IN number,
        i_strictness_id  IN varchar2,
        i_is_generic     in number,
        i_created_by_id  IN varchar2,
        o_workflow_id    OUT varchar2,
        o_return_value   OUT number)
    IS
        -- Convert input varchar2() to numbers
        v_space_id      pn_space_has_workflow.space_id%type := to_number(i_space_id);
        v_owner_id      pn_workflow.owner_id%type := to_number(i_owner_id);
        v_strictness_id pn_workflow.strictness_id%type := to_number(i_strictness_id);
        v_created_by_id pn_workflow.created_by_id%type := to_number(i_created_by_id);

        v_datetime      date;
        v_workflow_id    pn_workflow.workflow_id%type;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_WORKFLOW';

    BEGIN

        -- Create new entry in pn_object
        v_workflow_id := base.create_object(
            G_workflow_object_type,
            v_created_by_id,
            base.active_record_status);

        SECURITY.CREATE_SECURITY_PERMISSIONS(v_workflow_id, G_workflow_object_type, v_space_id, v_created_by_id);

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        -- Create the workflow record
        INSERT INTO pn_workflow (
            workflow_id,
            workflow_name,
            workflow_description,
            notes,
            owner_id,
            is_published,
            strictness_id,
            created_by_id,
            created_datetime,
            is_generic,
            crc,
            record_status)
        VALUES (
            v_workflow_id,
            i_workflow_name,
            i_workflow_description,
            i_notes,
            v_owner_id,
            i_is_published,
            v_strictness_id,
            v_created_by_id,
            v_datetime,
            i_is_generic,
            v_datetime,
            base.active_record_status);

        -- Associate workflow with space
        INSERT INTO pn_space_has_workflow (
            space_id,
            workflow_id)
        VALUES (
            v_space_id,
            v_workflow_id);

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

        -- Set output parameters
        o_workflow_id := to_char(v_workflow_id);
        o_return_value := BASE.OPERATION_SUCCESSFUL;

      EXCEPTION
      WHEN OTHERS THEN
        BEGIN
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        END;

      END;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_WORKFLOW
-- Update an existing workflow
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure modify_workflow (
        i_workflow_id    in varchar2,
        i_workflow_name  in varchar2,
        i_workflow_description in varchar2,
        i_notes          in varchar2,
        i_owner_id       in varchar2,
        i_is_published   in number,
        i_strictness_id  in varchar2,
        i_is_generic     in number,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_workflow_id    pn_workflow.workflow_id%type := to_number(i_workflow_id);
        v_owner_id       pn_workflow.owner_id%type := to_number(i_owner_id);
        v_strictness_id  pn_workflow.strictness_id%type := to_number(i_strictness_id);
        v_modified_by_id pn_workflow.created_by_id%type := to_number(i_modified_by_id);

        v_datetime       date;
        v_current_crc    pn_workflow.crc%type;

        stored_proc_name varchar2(100):= 'WORKFLOW.MODIFY_WORKFLOW';
    begin
        -- Check workflow has not changed
        select crc into v_current_crc
            from pn_workflow
            where workflow_id = v_workflow_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            update
                pn_workflow wf
            set
                wf.workflow_name = i_workflow_name,
                wf.workflow_description = i_workflow_description,
                wf.notes = i_notes,
                wf.owner_id = v_owner_id,
                wf.modified_by_id = v_modified_by_id,
                wf.modified_datetime = v_datetime,
                wf.is_published = i_is_published,
                wf.strictness_id = v_strictness_id,
                wf.is_generic = i_is_generic,
                wf.crc = v_datetime,
                wf.record_status = base.active_record_status
            where
                wf.workflow_id = v_workflow_id;

            o_return_value := BASE.OPERATION_SUCCESSFUL;
        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

    exception
        when record_locked then
        begin
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;
    end modify_workflow;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_WORKFLOW
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_workflow (
        i_workflow_id    in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_workflow_id    pn_workflow.workflow_id%type := to_number(i_workflow_id);
        v_modified_by_id pn_workflow.created_by_id%type := to_number(i_modified_by_id);

        v_datetime       date;
        v_current_crc    pn_workflow.crc%type;

        stored_proc_name varchar2(100):= 'WORKFLOW.REMOVE_WORKFLOW';
    begin
        -- Check workflow has not changed, may raise record_locked exception
        select crc into v_current_crc
            from pn_workflow
            where workflow_id = v_workflow_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then
            select sysdate into v_datetime from dual;
            update
                pn_workflow wf
            set
                wf.modified_by_id = v_modified_by_id,
                wf.modified_datetime = v_datetime,
                wf.crc = sysdate,
                wf.record_status = base.deleted_record_status
            where
                wf.workflow_id = v_workflow_id;

            o_return_value := BASE.OPERATION_SUCCESSFUL;
        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

    exception
        when record_locked then
        begin
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;
    end remove_workflow;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_STEP
----------------------------------------------------------------------
    procedure create_step (
        i_workflow_id       IN varchar2,
        i_step_name         IN varchar2,
        i_step_sequence      in varchar2,
        i_step_description  IN varchar2,
        i_is_initial_step   IN number,
        i_is_final_step     IN number,
        i_entry_status_id   in number,
        i_subscription_id   in number,
        i_created_by_id     IN varchar2,
        i_is_notes_null     in number,
        o_notes_clob        out clob,
        o_step_id           OUT varchar2)
    is
        -- Convert input varchar2() to numbers
        v_workflow_id    pn_workflow_step.workflow_id%type := to_number(i_workflow_id);
          v_step_sequence pn_workflow_step.step_sequence%type := to_number (i_step_sequence);
        v_entry_status_id pn_workflow_step.entry_status_id%type := to_number(i_entry_status_id);
        v_subscription_id pn_workflow_step.subscription_id%type := to_number(i_subscription_id);
        v_created_by_id  pn_workflow_step.created_by_id%type := to_number(i_created_by_id);
        v_datetime      date;
        v_step_id       pn_workflow_step.step_id%type;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_STEP';

    begin

        -- If this is an initial step, make sure there are no other initial steps
        if (i_is_initial_step = 1) then
            update pn_workflow_step
            set
                is_initial_step = 0
            where
                workflow_id = v_workflow_id;
        end if;

        -- Create new entry in pn_object
        v_step_id := base.create_object(
            G_step_object_type,
            v_created_by_id,
            base.active_record_status);

        SECURITY.CREATE_SECURITY_PERMISSIONS(v_step_id, G_step_object_type, getSpaceID(v_workflow_id), v_created_by_id);

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        if (i_is_notes_null = 1) then
            -- Create the step record with null notes
            insert into pn_workflow_step (
                step_id, workflow_id, step_name, step_description, notes_clob,
                is_initial_step, is_final_step, entry_status_id, subscription_id,
                created_by_id, created_datetime, crc, record_status, step_sequence)
            values (
                v_step_id, v_workflow_id, i_step_name, i_step_description, null,
                i_is_initial_step, i_is_final_step, v_entry_status_id, v_subscription_id,
                v_created_by_id, v_datetime, v_datetime, base.active_record_status, v_step_sequence)
            returning
                notes_clob into o_notes_clob;
        else
            -- Create the step record with empty_clob() notes
            insert into pn_workflow_step (
                step_id, workflow_id, step_name, step_description, notes_clob,
                is_initial_step, is_final_step, entry_status_id, subscription_id,
                created_by_id, created_datetime, crc, record_status, step_sequence)
            values (
                v_step_id, v_workflow_id, i_step_name, i_step_description, empty_clob(),
                i_is_initial_step, i_is_final_step, v_entry_status_id, v_subscription_id,
                v_created_by_id, v_datetime, v_datetime, base.active_record_status, v_step_sequence)
            returning
                notes_clob into o_notes_clob;

        end if;

        -- Set output parameters
        o_step_id := to_char(v_step_id);

      exception
      when others then
          base.log_error(stored_proc_name, sqlcode, sqlerrm);
          raise;
  end create_step;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_STEP
-- Update an existing step
----------------------------------------------------------------------
    procedure modify_step (
        i_step_id        in varchar2,
        i_step_name      in varchar2,
        i_step_sequence      in varchar2,
        i_step_description in varchar2,
        i_is_initial_step in number,
        i_is_final_step  in number,
        i_entry_status_id   in number,
        i_subscription_id   in number,
        i_modified_by_id in varchar2,
        i_crc            in date,
        i_is_notes_null     in number,
        o_notes_clob        out clob)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_step_id       pn_workflow_step.step_id%type := to_number(i_step_id);
        v_step_sequence pn_workflow_step.step_sequence%type := to_number (i_step_sequence);
        v_entry_status_id pn_workflow_step.entry_status_id%type := to_number(i_entry_status_id);
        v_subscription_id pn_workflow_step.subscription_id%type := to_number(i_subscription_id);
        v_modified_by_id pn_workflow_step.modified_by_id%type := to_number(i_modified_by_id);

        v_datetime      date;
        v_current_crc    pn_workflow_step.crc%type;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW.MODIFY_STEP';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_step
            where step_id = v_step_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            if (i_is_notes_null = 1) then
                -- Update record with null notes
                update
                    pn_workflow_step wfstep
                set
                    wfstep.step_name = i_step_name,
                    wfstep.step_sequence = v_step_sequence,
                    wfstep.step_description = i_step_description,
                    wfstep.notes_clob = null,
                    wfstep.is_final_step = i_is_final_step,
                    wfstep.is_initial_step = i_is_initial_step,
                    wfstep.entry_status_id = v_entry_status_id,
                    wfstep.subscription_id = v_subscription_id,
                    wfstep.modified_by_id = v_modified_by_id,
                    wfstep.modified_datetime = v_datetime,
                    wfstep.crc = v_datetime
                where
                    wfstep.step_id = v_step_id
                returning
                    notes_clob into o_notes_clob;
            else
                -- Update record with empty_clob() notes for subsequent writing
                update
                    pn_workflow_step wfstep
                set
                    wfstep.step_name = i_step_name,
                    wfstep.step_sequence = v_step_sequence,
                    wfstep.step_description = i_step_description,
                    wfstep.notes_clob = empty_clob(),
                    wfstep.is_final_step = i_is_final_step,
                    wfstep.is_initial_step = i_is_initial_step,
                    wfstep.entry_status_id = v_entry_status_id,
                    wfstep.subscription_id = v_subscription_id,
                    wfstep.modified_by_id = v_modified_by_id,
                    wfstep.modified_datetime = v_datetime,
                    wfstep.crc = v_datetime
                where
                    wfstep.step_id = v_step_id
                returning
                    notes_clob into o_notes_clob;
            end if;

        else
            -- crc different
            raise record_modified;
        end if;

    exception
        when others then
        begin
           base.log_error(stored_proc_name, sqlcode, sqlerrm);
           raise;
        end;
    end modify_step;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_STEP
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_step (
        i_step_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_step_id       pn_workflow_step.step_id%type := to_number(i_step_id);
        v_modified_by_id pn_workflow_step.modified_by_id%type := to_number(i_modified_by_id);

        v_datetime      date;
        v_current_crc    pn_workflow_step.crc%type;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW.REMOVE_STEP';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_step
            where step_id = v_step_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then
            select sysdate into v_datetime from dual;
            -- Update and set to DELETED
            update
                pn_workflow_step wfstep
            set
                wfstep.modified_by_id = v_modified_by_id,
                wfstep.modified_datetime = v_datetime,
                wfstep.crc = sysdate,
                wfstep.record_status = base.deleted_record_status
            where
                wfstep.step_id = v_step_id;

        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when record_locked then
        begin
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;
    end remove_step;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_TRANSITION
----------------------------------------------------------------------
    procedure create_transition
      ( i_workflow_id            in varchar2,
        i_transition_verb        in varchar2,
        i_transition_description in varchar2,
        i_begin_step_id          in varchar2,
        i_end_step_id            in varchar2,
        i_created_by_id          in varchar2,
        o_transition_id          out varchar2,
        o_return_value           out number)
    is
        -- Convert input varchar2() to numbers
        v_workflow_id    pn_workflow_transition.workflow_id%type := to_number(i_workflow_id);
        v_created_by_id  pn_workflow_transition.created_by_id%type := to_number(i_created_by_id);
        v_begin_step_id  pn_workflow_transition.begin_step_id%type := to_number(i_begin_step_id);
        v_end_step_id    pn_workflow_transition.end_step_id%type := to_number(i_end_step_id);

        v_datetime      date;
        v_transition_id pn_workflow_transition.transition_id%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_TRANSITION';

    begin

        -- Create new entry in pn_object
        v_transition_id := base.create_object(
            G_transition_object_type,
            v_created_by_id,
            base.active_record_status);

        SECURITY.CREATE_SECURITY_PERMISSIONS(v_transition_id, G_transition_object_type, getSpaceID(v_workflow_id), v_created_by_id);

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        -- Create the transition record
        insert into pn_workflow_transition (
            workflow_id,
            transition_id,
            transition_verb,
            transition_description,
            begin_step_id,
            end_step_id,
            created_by_id,
            created_datetime,
            crc,
            record_status)
        values (
            v_workflow_id,
            v_transition_id,
            i_transition_verb,
            i_transition_description,
            v_begin_step_id,
            v_end_step_id,
            v_created_by_id,
            v_datetime,
            v_datetime,
            base.active_record_status);

        -- Make it so.
        COMMIT;

        -- Set output parameters
        o_transition_id := to_char(v_transition_id);
        o_return_value := BASE.OPERATION_SUCCESSFUL;

      exception
      when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log values (
                sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;

      end create_transition;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_TRANSITION
-- Update an existing transition
----------------------------------------------------------------------
    procedure modify_transition (
        i_transition_id          in varchar2,
        i_transition_verb        in varchar2,
        i_transition_description in varchar2,
        i_begin_step_id          in varchar2,
        i_end_step_id            in varchar2,
        i_modified_by_id         in varchar2,
        i_crc                    in date,
        o_return_value           out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_transition_id  pn_workflow_transition.transition_id%type := to_number(i_transition_id);
        v_modified_by_id pn_workflow_transition.modified_by_id%type := to_number(i_modified_by_id);
        v_begin_step_id  pn_workflow_transition.begin_step_id%type := to_number(i_begin_step_id);
        v_end_step_id    pn_workflow_transition.end_step_id%type := to_number(i_end_step_id);

        v_datetime      date;
        v_current_crc    pn_workflow_transition.crc%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.MODIFY_TRANSITION';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_transition
            where transition_id = v_transition_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            -- Update record
            update
                pn_workflow_transition tran
            set
               tran.transition_verb = i_transition_verb,
               tran.transition_description = i_transition_description,
               tran.begin_step_id = v_begin_step_id,
               tran.end_step_id = v_end_step_id,
               tran.modified_by_id = v_modified_by_id,
               tran.modified_datetime = v_datetime,
               tran.crc = v_datetime
            where
                tran.transition_id = v_transition_id;

        else
            -- crc different
            raise record_modified;
        end if;

        commit;
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when record_locked then
        begin
            rollback;
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            rollback;
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log
               values (sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;
    end modify_transition;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_TRANSITION
-- DOES NOT COMMIT OR ROLLBACK
-- NO OUTPUT PARMS TO FACILITATE BATCHING
-- Raises workflow.unspecified_error when there is a problem
----------------------------------------------------------------------
    procedure remove_transition(
        i_transition_id  in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date)
    is
        v_return_value      number;
    begin
        -- Call remove transition and suck up return value
        remove_transition(i_transition_id, i_modified_by_id, i_crc, v_return_value);
        if v_return_value != base.operation_successful then
            raise workflow.unspecified_error;
        end if;
    end remove_transition;

----------------------------------------------------------------------
-- REMOVE_TRANSITION
-- DOES NOT COMMIT OR ROLLBACK
-- Raises workflow.unspecified_error when there is a problem
----------------------------------------------------------------------
    procedure remove_transition (
        i_transition_id  in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        v_transition_id  pn_workflow_transition.transition_id%type := to_number(i_transition_id);
        v_modified_by_id pn_workflow_transition.modified_by_id%type := to_number(i_modified_by_id);
        v_datetime      date;
        v_current_crc    pn_workflow_transition.crc%type;

        -- Standard error handling
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.REMOVE_TRANSITION';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_transition
            where transition_id = v_transition_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then
            select sysdate into v_datetime from dual;
            update
                pn_workflow_transition tran
            set
               tran.modified_by_id = v_modified_by_id,
               tran.modified_datetime = v_datetime,
               tran.crc = sysdate,
               tran.record_status = base.deleted_record_status
            where
                tran.transition_id = v_transition_id;

        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK
         */
        o_return_value := base.operation_successful;

    exception
        when record_locked then
        begin
            o_return_value := base.update_record_locked;
        end;
        when record_modified then
        begin
            o_return_value := base.update_record_out_of_sync;
        end;
        when others then
        begin
            o_return_value := base.plsql_exception;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;
    end remove_transition;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_STEP_GROUP
----------------------------------------------------------------------
    procedure create_step_group (
        i_step_id                in varchar2,
        i_workflow_id            in varchar2,
        i_group_id               in varchar2,
        i_is_participant         in number,
        i_created_by_id          in varchar2,
        o_return_value           out number)
    is
        -- Convert input varchar2() to numbers
        v_step_id        pn_workflow_step_has_group.step_id%type := to_number(i_step_id);
        v_workflow_id    pn_workflow_step_has_group.workflow_id%type := to_number(i_workflow_id);
        v_group_id       pn_workflow_step_has_group.group_id%type := to_number(i_group_id);
        v_created_by_id  pn_workflow_step_has_group.created_by_id%type := to_number(i_created_by_id);

        v_datetime      date;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_STEP_GROUP';

    begin

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        -- Check to see if row exists in deleted form (since we don't have generated PK)
        declare
            v_dummy number;
        begin
            select
                1 into v_dummy
            from
                pn_workflow_step_has_group
            where
                step_id = v_step_id and
                workflow_id = v_workflow_id and
                group_id = v_group_id
            for update nowait;

            -- If it exists, set status to ACTIVE
            update
                pn_workflow_step_has_group sg
            set
                sg.is_participant = i_is_participant,
                sg.modified_by_id = v_created_by_id,
                sg.modified_datetime = v_datetime,
                sg.crc = v_datetime,
                sg.record_status = base.active_record_status
            where
                sg.step_id = v_step_id and
                sg.workflow_id = v_workflow_id and
                sg.group_id = v_group_id;

        exception
            when NO_DATA_FOUND then
            begin
                -- Create the step_has_group record
                insert into pn_workflow_step_has_group (
                    step_id,
                    workflow_id,
                    group_id,
                    is_participant,
                    created_by_id,
                    created_datetime,
                    crc,
                    record_status)
                values (
                    v_step_id,
                    v_workflow_id,
                    v_group_id,
                    i_is_participant,
                    v_created_by_id,
                    v_datetime,
                    v_datetime,
                    base.active_record_status);
            end;
        end;

        -- Make it so.
        COMMIT;

        -- Set output parameters
        o_return_value := BASE.OPERATION_SUCCESSFUL;

      exception
      when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log values (
                sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;

    end create_step_group;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_STEP_GROUP
-- Remove a step group
----------------------------------------------------------------------
    procedure remove_step_group (
        i_step_id                in varchar2,
        i_workflow_id            in varchar2,
        i_group_id               in varchar2,
        i_modified_by_id         in varchar2,
        i_crc                    in date,
        o_return_value           out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_step_id        pn_workflow_step_has_group.step_id%type := to_number(i_step_id);
        v_workflow_id    pn_workflow_step_has_group.workflow_id%type := to_number(i_workflow_id);
        v_group_id       pn_workflow_step_has_group.group_id%type := to_number(i_group_id);
        v_modified_by_id pn_workflow_step_has_group.modified_by_id%type := to_number(i_modified_by_id);

        v_datetime      date;
        v_current_crc    pn_workflow_transition.crc%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.REMOVE_STEP_GROUP';

    begin
        -- Check record has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_step_has_group
            where
                step_id = v_step_id and
                workflow_id = v_workflow_id and
                group_id = v_group_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            -- Set status to DELETED
            update
                pn_workflow_step_has_group sg
            set
                sg.modified_by_id = v_modified_by_id,
                sg.modified_datetime = v_datetime,
                sg.crc = v_datetime,
                sg.record_status = base.deleted_record_status
            where
                sg.step_id = v_step_id and
                sg.workflow_id = v_workflow_id and
                sg.group_id = v_group_id;

        else
            -- crc different
            raise record_modified;
        end if;

        commit;
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when record_locked then
        begin
            rollback;
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            rollback;
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log
               values (sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;


     end remove_step_group;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_STEP_GROUP
-- Update an existing step group
----------------------------------------------------------------------
    procedure modify_step_group (
        i_step_id                in varchar2,
        i_workflow_id            in varchar2,
        i_group_id               in varchar2,
        i_is_participant         in number,
        i_modified_by_id         in varchar2,
        i_crc                    in date,
        o_return_value           out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_step_id        pn_workflow_step_has_group.step_id%type := to_number(i_step_id);
        v_workflow_id    pn_workflow_step_has_group.workflow_id%type := to_number(i_workflow_id);
        v_group_id       pn_workflow_step_has_group.group_id%type := to_number(i_group_id);
        v_modified_by_id pn_workflow_step_has_group.modified_by_id%type := to_number(i_modified_by_id);

        v_datetime      date;
        v_current_crc    pn_workflow_transition.crc%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.MODIFY_STEP_GROUP';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_step_has_group
            where
                step_id = v_step_id and
                workflow_id = v_workflow_id and
                group_id = v_group_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            -- Update record
            update
                pn_workflow_step_has_group sg
            set
                sg.is_participant = i_is_participant,
                sg.modified_by_id = v_modified_by_id,
                sg.modified_datetime = v_datetime,
                sg.crc = v_datetime
            where
                sg.step_id = v_step_id and
                sg.workflow_id = v_workflow_id and
                sg.group_id = v_group_id;

        else
            -- crc different
            raise record_modified;
        end if;

        commit;
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when record_locked then
        begin
            rollback;
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            rollback;
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log
               values (sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;

    end modify_step_group;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_ENVELOPE
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure create_envelope
      ( i_workflow_id    in varchar2,
        i_strictness_id  in varchar2,
        i_envelope_name  in varchar2,
        i_envelope_description in varchar2,
        i_created_by_id  in varchar2,
        i_status_id      in varchar2,
        i_priority_id    in varchar2,
        i_is_comments_null in number,
        o_comments_clob  out clob,
        o_envelope_id    out varchar2)
    is
        -- User defined exceptions
        initial_step_not_found exception;  -- Could not find initial step for workflow

        -- Convert input varchar2() to numbers
        v_workflow_id   pn_workflow_envelope.workflow_id%type   := to_number(i_workflow_id);
        v_strictness_id pn_workflow_envelope.strictness_id%type := to_number(i_strictness_id);
        v_created_by_id pn_workflow_envelope.created_by_id%type := to_number(i_created_by_id);
        v_status_id     pn_envelope_version.status_id%type      := to_number(i_status_id);
        v_priority_id   pn_envelope_version.priority_id%type    := to_number(i_priority_id);

        v_datetime      date;
        v_envelope_id   pn_workflow_envelope.envelope_id%type;
        v_envelope_version_id pn_envelope_version.version_id%type;
        v_step_id       pn_envelope_version.step_id%type;
        v_entry_status_id pn_workflow_step.entry_status_id%type := null;

        -- Standard error handling
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_ENVELOPE';

    begin
        /*
            Locate initial step for the workflow
            to which this envelope is attached

            04/09/01 - Tim - Handles erroneous scenario where there may be more
            than one initial step: Accounts for older "create_step" code that
            did not prohibit multiple initial steps.
         */
        begin
            select
               step_id, entry_status_id into v_step_id, v_entry_status_id
            from
               pn_workflow_step
            where
               rownum = 1 and
               workflow_id = v_workflow_id and
               is_initial_step = 1 and
               record_status = base.active_record_status;
        exception
            when NO_DATA_FOUND then
            begin
                raise initial_step_not_found;
            end;
        end;

        /** If the initial step doesn't have a new status, we should keep the 
            initial status to the status that is specified while creating envelope by default. */
        -- if (v_entry_status_id = '') or (v_entry_status_id is null) then
        --    v_status_id := '100';
        -- end if;
        -- If initial status not specified, mark it "New"
        if v_status_id is null then
			v_status_id := '100';
		end if;
		
        /* Override passed in status id if the step had an entry status id */
        if v_entry_status_id is not null then
            v_status_id := v_entry_status_id;
        end if;

        -- Create new entry in pn_object
        v_envelope_id := base.create_object(
            G_envelope_object_type,
            v_created_by_id,
            base.active_record_status);

        -- **** TBC **** -> Should SPACE be inferred from workflow or passed in and stored with envelope ?????
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_envelope_id, G_envelope_object_type, getSpaceID(v_workflow_id), v_created_by_id);

        -- Create envelope version object entry
        v_envelope_version_id := base.create_object(
            G_envelope_version_object_type,
            v_created_by_id,
            base.active_record_status);

        -- **** TBC **** -> Should SPACE be inferred from workflow or passed in and stored with envelope ?????
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_envelope_version_id, G_envelope_version_object_type, getSpaceID(v_workflow_id), v_created_by_id);

        -- Get current datetime
        select sysdate into v_datetime from dual;

        -- Insert envelope
        insert into pn_workflow_envelope (
            envelope_id,
            workflow_id,
            strictness_id,
            current_version_id,
            envelope_name,
            envelope_description,
            created_by_id,
            created_datetime,
            crc,
            record_status)
        values (
            v_envelope_id,
            v_workflow_id,
            v_strictness_id,
            v_envelope_version_id,
            i_envelope_name,
            i_envelope_description,
            v_created_by_id,
            v_datetime,
            v_datetime,
            base.active_record_status);

        if (i_is_comments_null > 0) then
            -- Insert envelope version with null comments
            insert into  pn_envelope_version
                ( version_id, envelope_id, status_id, step_id, workflow_id,
                  transition_id, priority_id, comments_clob,
                  created_by_id, created_datetime, crc, record_status)
            values
                ( v_envelope_version_id, v_envelope_id, v_status_id, v_step_id, v_workflow_id,
                  null, v_priority_id, null,
                  v_created_by_id, v_datetime, v_datetime, base.active_record_status)
            returning
                comments_clob into o_comments_clob;

        else
            -- Insert envelope version with empty comments for subsequent streaming
            insert into  pn_envelope_version
                ( version_id, envelope_id, status_id, step_id, workflow_id,
                  transition_id, priority_id, comments_clob,
                  created_by_id, created_datetime, crc, record_status)
            values
                ( v_envelope_version_id, v_envelope_id, v_status_id, v_step_id, v_workflow_id,
                  null, v_priority_id, empty_clob(),
                  v_created_by_id, v_datetime, v_datetime, base.active_record_status)
            returning
                comments_clob into o_comments_clob;

        end if;

        -- Update envelope with current version id
        update
            pn_workflow_envelope
        set
            current_version_id = v_envelope_version_id
        where
            envelope_id = v_envelope_id;


        /*
            IMPORTANT - THIS PROCEDURE DOES NOT COMMIT OR ROLLBACK
            SINCE IT MUST BE POSSIBLE TO ADD OJBECTS TO THE ENVELOPE
            ONCE CREATED
         */

        -- Set output parameters
        o_envelope_id := to_char(v_envelope_id);

    exception
        when initial_step_not_found then
        begin
            base.log_error(stored_proc_name, sqlcode, 'No initial step found for workflow '||v_workflow_id);
            raise;
        end;
        when others then
        begin
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        end;
    end create_envelope;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- ADD_ENVELOPE_OBJECT
-- DOES NOT COMMIT OR ROLLBACK
-- This routine will Raise an exception if there is a problem
-- There are no output parameters to facilitate batching
----------------------------------------------------------------------
    procedure add_envelope_object
      ( i_envelope_id    in varchar2,
        i_object_id       in varchar2,
        i_object_type     in varchar2,
        i_object_version_id in varchar2,
        i_object_properties_id in varchar2,
        i_created_by_id  in varchar2)
    is
        envelope_not_found exception;   -- Could not find envelope/current version

        v_envelope_id   pn_workflow_envelope.envelope_id%type   := to_number(i_envelope_id);
        v_object_id     pn_object.object_id%type                := to_number(i_object_id);
        v_object_version_id pn_envelope_version_has_object.object_version_id%type := to_number(i_object_version_id);
        v_object_type pn_envelope_version_has_object.object_type%type := i_object_type;
	v_object_properties_id pn_envelope_version_has_object.object_properties_id%type := to_number(i_object_properties_id);
        -- i_created_by_id not currently used

        v_envelope_version_id   pn_envelope_version.version_id%type;
        v_datetime  date;
        v_dummy     number;

        -- Standard error handling
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.ADD_ENVELOPE_OBJECT';

    begin
        /* Make sure we got a version id (it is optional) */
        if i_object_version_id is null or i_object_version_id = '' then
            v_object_version_id := 0;
        end if;

	/* bfd - 2107 make object type unlinked when the document attached to envolop is deleted...*/
	if i_object_type is null or i_object_type = '' then
            v_object_type := 'unlinked';
        end if;

        /* Determine current version id of envelope */
        begin
            select
                current_version_id into v_envelope_version_id
            from
                pn_workflow_envelope
            where
                envelope_id = v_envelope_id;
        exception
            when NO_DATA_FOUND then
            begin
                raise envelope_not_found;
            end;
        end;

        /* Get current date and time */
        select sysdate into v_datetime from dual;

        /*
            Try to locate existing Envelope Has Object record
            If none exists, create one
         */
        begin
            select
                1 into v_dummy
            from
                pn_envelope_has_object
            where
                object_id = v_object_id and
                envelope_id = v_envelope_id;
        exception
            when NO_DATA_FOUND then
            begin
                /* Create envelope_has_object record */
                insert into pn_envelope_has_object (
                    envelope_id,
                    object_id,
                    crc,
                    record_status)
                values (
                    v_envelope_id,
                    v_object_id,
                    v_datetime,
                    base.active_record_status);
            end;
        end;

        /*
            Create envelope_version_has_object record
         */
        insert into pn_envelope_version_has_object (
            envelope_id,
            version_id,
            object_id,
            object_type,
            object_version_id,
            object_properties_id,
            crc,
            record_status)
        values (
            v_envelope_id,
            v_envelope_version_id,
            v_object_id,
            v_object_type,
            v_object_version_id,
            v_object_properties_id,
            v_datetime,
            base.active_record_status);

            /* NO COMMIT -- LEFT UP TO CALLING ENVIRONMENT TO COMMIT */
    exception
        when envelope_not_found then
        begin
            raise workflow.unspecified_error;
        end;
        when others then
        begin
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        end;

    end add_envelope_object;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_ENVELOPE
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure modify_envelope
      ( i_envelope_id    in varchar2,
        i_strictness_id  in varchar2,
        i_envelope_name  in varchar2,
        i_envelope_description in varchar2,
        i_modified_by_id  in varchar2,
        i_step_id        in varchar2,
        i_transition_id  in varchar2,
        i_status_id      in varchar2,
        i_priority_id    in varchar2,
        i_crc            in date,
        i_is_comments_null in number,
        o_comments_clob  out clob)
    is
        -- User defined exceptions
        step_not_found exception;   -- Could not find step for specified i_step_id
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        v_envelope_id       pn_workflow_envelope.envelope_id%type   := to_number(i_envelope_id);
        v_strictness_id     pn_workflow_envelope.strictness_id%type := to_number(i_strictness_id);
        v_modified_by_id    pn_workflow_envelope.modified_by_id%type:= to_number(i_modified_by_id);
        v_step_id           pn_envelope_version.step_id%type        := to_number(i_step_id);
        v_transition_id     pn_envelope_version.transition_id%type  := to_number(i_transition_id);
        v_status_id         pn_envelope_version.status_id%type      := to_number(i_status_id);
        v_priority_id       pn_envelope_version.priority_id%type    := to_number(i_priority_id);

        v_datetime      date;
        v_current_crc    pn_workflow_transition.crc%type;
        v_envelope_version_id   pn_envelope_version.version_id%type;
        v_workflow_id           pn_workflow_envelope.workflow_id%type;
        v_entry_status_id pn_workflow_step.entry_status_id%type := null;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW.MODIFY_ENVELOPE';
    begin

        -- Check envelope has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_envelope
            where
                envelope_id = v_envelope_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            /* Grab the entry status id from the step */
            begin
                select
                   entry_status_id into v_entry_status_id
                from
                    pn_workflow_step
                where
                    step_id = v_step_id;
            exception
                when NO_DATA_FOUND then
                begin
                    raise step_not_found;
                end;
            end;

            /* Override passed in status id if the step had an entry status id */
            if v_entry_status_id is not null then
                v_status_id := v_entry_status_id;
            end if;

            -- Create envelope version object entry
            v_envelope_version_id := base.create_object(
                G_envelope_version_object_type,
                v_modified_by_id,
                base.active_record_status);

            select sysdate into v_datetime from dual;

            -- Update Envelope record
            update
                pn_workflow_envelope wfe
            set
               wfe.current_version_id = v_envelope_version_id,
               wfe.strictness_id = v_strictness_id,
               wfe.envelope_name = i_envelope_name,
               wfe.envelope_description = i_envelope_description,
               wfe.modified_by_id = v_modified_by_id,
               wfe.modified_datetime = v_datetime,
               wfe.crc = sysdate
            where
                 wfe.envelope_id = v_envelope_id
            returning
                wfe.workflow_id into v_workflow_id;

            -- **** TBC **** should space be inferred from workflow, or from envelope (if I stored it on the envelope)
            SECURITY.CREATE_SECURITY_PERMISSIONS(v_envelope_version_id, G_envelope_version_object_type, getSpaceID(v_workflow_id), v_modified_by_id);

            if (i_is_comments_null > 0) then
                -- Insert envelope version record with null comments
                insert into pn_envelope_version
                    ( version_id, envelope_id, status_id, step_id, workflow_id,
                      transition_id, priority_id, comments_clob,
                      created_by_id, created_datetime, crc, record_status)
                values
                    ( v_envelope_version_id, v_envelope_id, v_status_id, v_step_id, v_workflow_id,
                      v_transition_id, v_priority_id, null,
                      v_modified_by_id, v_datetime, v_datetime, base.active_record_status)
                returning
                    comments_clob into o_comments_clob;

            else
                -- Insert envelope version record with empty comments
                insert into pn_envelope_version
                    ( version_id, envelope_id, status_id, step_id, workflow_id,
                      transition_id, priority_id, comments_clob,
                      created_by_id, created_datetime, crc, record_status)
                values
                    ( v_envelope_version_id, v_envelope_id, v_status_id, v_step_id, v_workflow_id,
                      v_transition_id, v_priority_id, empty_clob(),
                      v_modified_by_id, v_datetime, v_datetime, base.active_record_status)
                returning
                    comments_clob into o_comments_clob;

            end if;

        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK
         */

    exception
        when others then
        begin
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        end;

    end modify_envelope;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_HISTORY
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    PROCEDURE create_history
      ( i_envelope_id       IN varchar2,
        i_history_action_id IN varchar2,
        i_action_by_id      IN varchar2,
        i_action_datetime   IN date,
        i_history_message_id IN varchar2,
        o_history_id        OUT varchar2,
        o_return_value      OUT number)
    is

        v_envelope_id   pn_envelope_history.envelope_id%type := to_number(i_envelope_id);
        v_history_action_id pn_envelope_history_action.history_action_id%type := to_number(i_history_action_id);
        v_history_message_id pn_envelope_history.history_message_id%type := to_number(i_history_message_id);
        v_action_by_id  pn_envelope_history.action_by_id%type := to_number(i_action_by_id);

        v_history_id    pn_envelope_history.history_id%type;
        v_datetime      date;

        -- Standard error handling
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_HISTORY';

    begin

        -- Create new entry in pn_object
        v_history_id := base.create_object(
            G_history_object_type,
            v_action_by_id,
            base.active_record_status);

        select sysdate into v_datetime from dual;

        -- DO I NEED SECURITY FOR HISTORY???? IF YES, I NEED SPACE_ID on ENVELOPE
        --SECURITY.CREATE_SECURITY_PERMISSIONS(v_history_version_id, G_history_object_type, getSpaceID(v_workflow_id), v_modified_by_id);

        -- Insert history record
        insert into pn_envelope_history (
            history_id,
            envelope_id,
            action_by_id,
            history_action_id,
            action_datetime,
            history_message_id,
            crc,
            record_status)
        values (
            v_history_id,
            v_envelope_id,
            v_action_by_id,
            v_history_action_id,
            i_action_datetime,
            v_history_message_id,
            v_datetime,
            base.active_record_status);

        /*
            NO COMMIT OR ROLLBACK
         */
        o_history_id := v_history_id;
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;

    end create_history;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- IS_OBJECT_IN_ACTIVE_ENVELOPE
-- Indicates whether an object is in an active envelope
-- Input Parameters
--      i_object_id     the object id to check for as a number
-- Returns
--      0   - object is not in an active envelope
--      1   - object is in an active envelope
----------------------------------------------------------------------
    function is_object_in_active_envelope
        ( i_object_id   in varchar2)
        return number
    is
    begin
        return is_object_in_active_envelope(to_number(i_object_id));
    end;



    function is_object_in_active_envelope
        ( i_object_id   in number)
        return number
    is
        v_object_id     pn_envelope_has_object_view.object_id%type := to_number(i_object_id);
        v_dummy         number;
        v_result        number := 0;

         stored_proc_name VARCHAR2(100):= 'WORKFLOW.IS_OBJECT_IN_ACTIVE_ENVELOPE';

    begin
        select 1 into v_dummy
        from
            pn_envelope_has_object_view eo
        where
            record_status = 'A' and
            object_id = v_object_id and
            is_active = 1 and
            rownum = 1;

        -- If we got here, then there was one row
        v_result := 1;

        return v_result;

    exception
        when no_data_found then
        begin
            v_result := 0;
            return v_result;
        end;

        when others then
        begin
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        end;
    end is_object_in_active_envelope;




----------------------------------------------------------------------
-- IS_ACTIVE_ENVELOPE
----------------------------------------------------------------------
    FUNCTION IS_ACTIVE_ENVELOPE
      ( i_envelope_id   IN varchar2)
      RETURN number
    IS
        v_envelope_id   pn_workflow_envelope.envelope_id%type := to_number(i_envelope_id);
        v_dummy         number;
        v_result        number := 1;

    BEGIN
        -- Check the envelope's current version has a status which is not Inactive
        BEGIN
            SELECT 1 INTO v_dummy
            FROM
                pn_workflow_envelope wfe
            WHERE
                wfe.envelope_id = v_envelope_id AND exists (
                    SELECT 1
                    FROM
                        pn_envelope_version wfev,
                        pn_workflow_status wfs
                    WHERE
                        wfev.envelope_id = wfe.envelope_id AND
                        wfev.version_id = wfe.current_version_id AND
                        wfev.workflow_id = wfe.workflow_id AND
                        wfs.status_id = wfev.status_id AND
                        wfs.is_inactive = 0                     -- Not Inactive (= Active!)
                    );
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_result := 0;
        END;

        RETURN v_result;
    END IS_ACTIVE_ENVELOPE;
----------------------------------------------------------------------

/*--------------------------------------------------------------------
  WORKFLOW COPY PROCEDURES
--------------------------------------------------------------------*/

----------------------------------------------------------------------
-- COPY_ALL
-- Copies all workflows for a space to another space, not including groups
----------------------------------------------------------------------
    PROCEDURE copy_all
      ( i_from_space_id     IN varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        o_return_value      OUT number)
    is
    begin
        workflow.copy_all(i_from_space_id, i_to_space_id, i_created_by_id, 0, o_return_value);
    end copy_all;

----------------------------------------------------------------------
-- COPY_ALL
-- Copies all workflows for a space to another space
-- AUTONOMOUS TRANSACTION
----------------------------------------------------------------------
    PROCEDURE copy_all
      ( i_from_space_id     IN varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        i_copy_groups       in number,
        o_return_value      OUT number)
    is
        pragma autonomous_transaction;

        -- Cursor to read all workflows for a given space (where workflow not
        -- deleted).
        cursor workflow_cur (i_space_id in number)
        is
            select spwf.workflow_id
            from
                pn_space_has_workflow spwf,
                pn_workflow wf
            where
                space_id = i_space_id and
                spwf.workflow_id = wf.workflow_id and
                wf.record_status <> base.deleted_record_status;

        v_from_space_id pn_space_has_workflow.space_id%type := to_number(i_from_space_id);
        v_to_space_id pn_space_has_workflow.space_id%type := to_number(i_to_space_id);

        v_created_by_id     number := to_number(i_created_by_id);

    begin
        for workflow_rec in workflow_cur(i_from_space_id) loop
            copy_workflow(v_to_space_id, workflow_rec.workflow_id, v_created_by_id, i_copy_groups);
        end loop;

        commit;
        o_return_value := base.operation_successful;

    exception
        when others then
        begin
            rollback;
            base.log_error('WORKFLOW.COPY_ALL', sqlcode, sqlerrm);
            o_return_value := base.plsql_exception;
        end;
    end copy_all;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE copy_workflow
-- Copy specified workflow to specified space_id
-- AUTONOMOUS_TRANSACTION
----------------------------------------------------------------------
    procedure copy_workflow(
        i_to_space_id in number,
        i_workflow_id in number,
        i_created_by_id in number,
        i_copy_groups in number)
    is
        PRAGMA AUTONOMOUS_TRANSACTION;

        -- Exceptions
        workflow_not_found exception;

        -- Cursor for reading workflow
        cursor workflow_cur(i_workflow_id in number) is
            select *
            from pn_workflow
            where workflow_id = i_workflow_id;
        workflow_rec workflow_cur%rowtype;

        -- Cursor for reading object types
        cursor object_type_cur(i_workflow_id in number) is
            select *
            from pn_workflow_has_object_type
            where workflow_id = i_workflow_id;

        v_workflow_id       number;
        v_datetime          date;
        v_created_by_id     number := i_created_by_id;

    begin
        -- Get current datetime
        select sysdate into v_datetime from dual;

        -- Copy the workflow record
        open workflow_cur(i_workflow_id);
        fetch workflow_cur into workflow_rec;
        if workflow_cur%found then
            -- Create new entry in pn_object
            v_workflow_id := base.create_object(
                G_workflow_object_type,
                v_created_by_id,
                base.active_record_status);

            insert into pn_workflow (
                workflow_id,
                workflow_name,
                workflow_description,
                notes,
                owner_id,
                is_published,
                strictness_id,
                created_by_id,
                created_datetime,
                is_generic,
                crc,
                record_status)
            values (
                v_workflow_id,
                workflow_rec.workflow_name,
                workflow_rec.workflow_description,
                workflow_rec.notes,
                workflow_rec.owner_id,
                workflow_rec.is_published,
                workflow_rec.strictness_id,
                v_created_by_id,
                v_datetime,
                workflow_rec.is_generic,
                sysdate,
                base.active_record_status);

            -- Create the space has workflow record
            insert into pn_space_has_workflow
                (space_id, workflow_id)
            values
                (i_to_space_id, v_workflow_id);

            SECURITY.CREATE_SECURITY_PERMISSIONS(v_workflow_id, G_workflow_object_type, i_to_space_id, v_created_by_id);

        else
            raise workflow_not_found;
        end if;
        close workflow_cur;

        -- Copy the object types
        for object_type_rec in object_type_cur(i_workflow_id) loop
            insert into pn_workflow_has_object_type (
                workflow_id,
                object_type,
                sub_type_id
            ) values (
                v_workflow_id,
                object_type_rec.object_type,
                object_type_rec.sub_type_id
            );
        end loop;

        -- Copy steps
        copy_steps(i_workflow_id, v_workflow_id, v_created_by_id, i_copy_groups);

        -- Copy transitions
        copy_transitions(i_workflow_id, v_workflow_id, v_created_by_id, i_copy_groups);

        -- Now clean up intermediate data created by copy_steps
        delete from pn_workflow_step_copy
        where
            workflow_id = i_workflow_id and
            to_workflow_id = v_workflow_id;

        /*
            SUCCESSFULLY COPIED WORKFLOW
         */
        commit;

    exception
        when others then
        begin
            rollback;

            if workflow_cur%isopen then
                close workflow_cur;
            end if;

            raise;
        end;
    end copy_workflow;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE copy_steps
-- copies steps from specified workflow id to specified workflow id
-- sets created by id to specified value
-- DOES NOT COMMIT
----------------------------------------------------------------------
    procedure copy_steps(
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_created_by_id     in number,
        i_copy_groups       in number)
    is
        -- Cursor for reading steps
        cursor step_cur(i_workflow_id in number) is
            select *
            from pn_workflow_step
            where workflow_id = i_workflow_id;

        -- Cursor for step has group records
        cursor step_has_group_cur(i_workflow_id in number, i_step_id in number) is
            select *
            from pn_workflow_step_has_group
            where
                workflow_id = i_workflow_id and
                step_id = i_step_id;

        v_step_id       pn_workflow_step.step_id%type;
        v_from_step_id  pn_workflow_step.step_id%type;
        v_datetime date;
        v_created_by_id number  := i_created_by_id;

    begin
        -- Get current datetime
        select sysdate into v_datetime from dual;

        -- Copy the steps
        for step_rec in step_cur(i_from_workflow_id) loop
            v_from_step_id := step_rec.step_id;

             -- Create new entry in pn_object
            v_step_id := base.create_object(
                G_step_object_type,
                v_created_by_id,
                base.active_record_status);

            SECURITY.CREATE_SECURITY_PERMISSIONS(v_step_id, G_step_object_type, getSpaceID(i_to_workflow_id), v_created_by_id);

            insert into pn_workflow_step (
                step_id,
                workflow_id,
                step_name,
                step_description,
                notes_clob,
                is_initial_step,
                is_final_step,
                entry_status_id,
                subscription_id,
                created_by_id,
                created_datetime,
                crc,
                record_status,
                step_sequence)
            values (
                v_step_id,
                i_to_workflow_id,
                step_rec.step_name,
                step_rec.step_description,
                step_rec.notes_clob,
                step_rec.is_initial_step,
                step_rec.is_final_step,
                step_rec.entry_status_id,
                step_rec.subscription_id,
                v_created_by_id,
                v_datetime,
                sysdate,
                base.active_record_status,
                step_rec.step_sequence);

            -- Now store from and to values of step id so that other copy routines
            -- may correctly update their step_id columns
            insert into pn_workflow_step_copy (
                workflow_id,
                step_id,
                to_workflow_id,
                to_step_id)
            values (
                step_rec.workflow_id,
                v_from_step_id,
                i_to_workflow_id,
                v_step_id);

            -- Only copy groups if specified; group copy makes no sense
            -- when copying between spaces (since we have no idea what
            -- the corresponding groups might be in the target space)
            if i_copy_groups = 1 then

                -- Copy the step groups for the current step
                for step_has_group_rec in step_has_group_cur(i_from_workflow_id, v_from_step_id) loop
                    insert into pn_workflow_step_has_group (
                        step_id,
                        workflow_id,
                        group_id,
                        is_participant,
                        created_by_id,
                        created_datetime,
                        crc,
                        record_status)
                    values (
                        v_step_id,
                        i_to_workflow_id,
                        step_has_group_rec.group_id,
                        step_has_group_rec.is_participant,
                        v_created_by_id,
                        v_datetime,
                        sysdate,
                        base.active_record_status);
                end loop;

            end if;

        end loop;

    end copy_steps;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE copy_transitions
-- copies transitions from specified workflow id to specified workflow id
-- sets created by id to specified value
-- DOES NOT COMMIT
----------------------------------------------------------------------
    procedure copy_transitions (
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_created_by_id     in number,
        i_copy_groups       in number)
    is
        -- Cursor for reading transitions
        cursor transition_cur(i_workflow_id in number) is
            select *
            from pn_workflow_transition
            where workflow_id = i_workflow_id;

        v_transition_id     pn_workflow_transition.transition_id%type;
        v_from_transition_id pn_workflow_transition.transition_id%type;
        v_datetime          date;
        v_created_by_id     number  := i_created_by_id;
        v_new_begin_step_id pn_workflow_transition.begin_step_id%type;
        v_new_end_step_id   pn_workflow_transition.end_step_id%type;

    begin
        -- Get current datetime
        select sysdate into v_datetime from dual;

        -- Copy the transitions
        for transition_rec in transition_cur(i_from_workflow_id) loop
            v_from_transition_id := transition_rec.transition_id;

            -- Lookup new step id values for this transition
            select to_step_id into v_new_begin_step_id
            from
                pn_workflow_step_copy
            where
                workflow_id = transition_rec.workflow_id and
                step_id = transition_rec.begin_step_id;

            select to_step_id into v_new_end_step_id
            from
                pn_workflow_step_copy
            where
                workflow_id = transition_rec.workflow_id and
                step_id = transition_rec.end_step_id;

            -- Create new entry in pn_object
            v_transition_id := base.create_object(
                G_transition_object_type,
                v_created_by_id,
                base.active_record_status);

            SECURITY.CREATE_SECURITY_PERMISSIONS(v_transition_id, G_transition_object_type, getSpaceID(i_to_workflow_id), v_created_by_id);

            insert into pn_workflow_transition (
                workflow_id,
                transition_id,
                transition_verb,
                transition_description,
                begin_step_id,
                end_step_id,
                created_by_id,
                created_datetime,
                crc,
                record_status)
            values (
                i_to_workflow_id,
                v_transition_id,
                transition_rec.transition_verb,
                transition_rec.transition_description,
                v_new_begin_step_id,
                v_new_end_step_id,
                v_created_by_id,
                v_datetime,
                sysdate,
                base.active_record_status);

            workflow_rule.copy_rules(i_from_workflow_id, i_to_workflow_id,
                                     v_from_transition_id, v_transition_id, v_created_by_id,
                                     i_copy_groups);

        end loop;

    end copy_transitions;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE crc_matches
-- Compare two crc values
-- i_original_crc - first crc value
-- i_new_crc - second cec value
-- returns true if both values are identical, false otherwise
----------------------------------------------------------------------
    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean
    is
        v_crc_match     boolean := false;

    begin
        if (i_original_crc = i_new_crc) then
            v_crc_match := true;
        end if;
        return v_crc_match;
    end crc_matches;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE getSpaceID
-- get the space id of a workflow
-- i_workflow_id - the workflow id to get space id from
-- returns space_id
-- throws exception if there is a problem
----------------------------------------------------------------------
    function getSpaceID(i_workflow_id in number)
        return number
    is
        v_space_id  pn_space_has_workflow.space_id%type := null;

    begin
        select
            sp.space_id into v_space_id
        from
            pn_space_has_workflow sp
        where
            sp.workflow_id = i_workflow_id;

        return v_space_id;
    end getSpaceID;
----------------------------------------------------------------------

END; -- Package Body WORKFLOW
/

