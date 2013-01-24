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
CREATE OR REPLACE PACKAGE BODY workflow_rule IS
--==================================================================
-- Purpose: Workflow procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        26-Sep-00  Created it.
-- TimM        26-Oct-00  Added copy_rules from WORKFLOW package and
--                        added remove_rule
--==================================================================

/*--------------------------------------------------------------------
  PRIVATE PROCEDURE PROTOTYPES
  Required for private procedures so that they can be used
  lexically earlier than their definition.
--------------------------------------------------------------------*/

    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean;

    procedure copy_rule_auth(
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_from_transition_id    in number,
        i_to_transition_id      in number,
        i_from_rule_id          in number,
        i_to_rule_id            in number,
        i_created_by_id         in number,
        i_copy_groups           in number);

/*--------------------------------------------------------------------
  END OF PROCEDURE PROTOTYPES
--------------------------------------------------------------------*/

----------------------------------------------------------------------
-- CREATE_RULE
----------------------------------------------------------------------
    procedure create_rule
      ( i_workflow_id    in varchar2,
        i_transition_id  in varchar2,
        i_rule_name      in varchar2,
        i_rule_description in varchar2,
        i_notes          in varchar2,
        i_rule_status_id in varchar2,
        i_rule_type_id   in varchar2,
        i_created_by_id  in varchar2,
        o_rule_id        out varchar2,
        o_return_value   out number)
    is
        -- Convert input varchar2() to numbers
        v_workflow_id    pn_workflow_rule.workflow_id%type := to_number(i_workflow_id);
        v_transition_id  pn_workflow_rule.transition_id%type := to_number(i_transition_id);
        v_rule_status_id pn_workflow_rule.rule_status_id%type := to_number(i_rule_status_id);
        v_rule_type_id   pn_workflow_rule.rule_type_id%type := to_number(i_rule_type_id);
        v_created_by_id  pn_workflow_rule.created_by_id%type := to_number(i_created_by_id);

        v_datetime      date;
        v_rule_id       pn_workflow_rule.rule_id%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW_RULE.CREATE_RULE';

    begin

        -- Create new entry in pn_object
        v_rule_id := base.create_object(
            G_rule_object_type,
            v_created_by_id,
            base.active_record_status);

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        -- Create the step record
        insert into pn_workflow_rule (
            rule_id,
            workflow_id,
            transition_id,
            rule_name,
            rule_description,
            notes,
            rule_status_id,
            rule_type_id,
            created_by_id,
            created_datetime,
            crc,
            record_status)
        values (
            v_rule_id,
            v_workflow_id,
            v_transition_id,
            i_rule_name,
            i_rule_description,
            i_notes,
            v_rule_status_id,
            v_rule_type_id,
            v_created_by_id,
            v_datetime,
            v_datetime,
            base.active_record_status);

        -- Make it so.
        COMMIT;

        -- Set output parameters
        o_rule_id := to_char(v_rule_id);
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
    end create_rule;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_RULE
-- Update existing rule
----------------------------------------------------------------------
    procedure modify_rule (
        i_rule_id        in varchar2,
        i_rule_name      in varchar2,
        i_rule_description in varchar2,
        i_notes          in varchar2,
        i_rule_status_id in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_rule_id        pn_workflow_rule.rule_id%type := to_number(i_rule_id);
        v_modified_by_id pn_workflow_rule.modified_by_id%type := to_number(i_modified_by_id);
        v_rule_status_id pn_workflow_rule.rule_status_id%type := to_number(i_rule_status_id);

        v_datetime      date;
        v_current_crc    pn_workflow_rule.crc%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW_RULE.MODIFY_RULE';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_rule
            where rule_id = v_rule_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            -- Update record
            update
                pn_workflow_rule wfrule
            set
                wfrule.rule_name = i_rule_name,
                wfrule.rule_description = i_rule_description,
                wfrule.notes = i_notes,
                wfrule.rule_status_id = v_rule_status_id,
                wfrule.modified_by_id = v_modified_by_id,
                wfrule.modified_datetime = v_datetime,
                wfrule.crc = v_datetime
            where
                wfrule.rule_id = v_rule_id;

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
    end modify_rule;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_RULE
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_rule (
        i_rule_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_rule_id        pn_workflow_rule.rule_id%type := to_number(i_rule_id);
        v_modified_by_id pn_workflow_rule.modified_by_id%type := to_number(i_modified_by_id);

        v_datetime      date;
        v_current_crc    pn_workflow_rule.crc%type;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW_RULE.REMOVE_RULE';

    begin
        -- Check rule has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_rule
            where rule_id = v_rule_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then
            select sysdate into v_datetime from dual;
            -- Update record
            update
                pn_workflow_rule wfrule
            set
                wfrule.modified_by_id = v_modified_by_id,
                wfrule.modified_datetime = v_datetime,
                wfrule.crc = sysdate,
                wfrule.record_status = base.deleted_record_status
            where
                wfrule.rule_id = v_rule_id;

        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK
         */
        o_return_value := BASE.OPERATION_SUCCESSFUL;

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
    end remove_rule;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- copy_rules
-- DOES NOT COMMIT
----------------------------------------------------------------------
    procedure copy_rules (
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_from_transition_id    in number,
        i_to_transition_id      in number,
        i_created_by_id    in number,
        i_copy_groups      in number)
    is
        -- Cursor for reading rules
        cursor rule_cur(i_workflow_id in number, i_transition_id in number) is
            select *
            from pn_workflow_rule
            where
                workflow_id = i_workflow_id and
                transition_id = i_transition_id;

        unknown_rule_type   exception;

        v_rule_id           pn_workflow_rule.rule_id%type;
        v_datetime          date;
        v_created_by_id     number  := i_created_by_id;

        v_from_rule_id      pn_workflow_rule.rule_id%type;

    begin
        -- Get current datetime
        select sysdate into v_datetime from dual;

        -- Copy the rules
        for rule_rec in rule_cur(i_from_workflow_id, i_from_transition_id) loop
            v_from_rule_id := rule_rec.rule_id;

            -- Create new entry in pn_object
            v_rule_id := base.create_object(
                G_rule_object_type,
                v_created_by_id,
                base.active_record_status);

            insert into pn_workflow_rule (
                rule_id,
                workflow_id,
                transition_id,
                rule_name,
                rule_description,
                notes,
                rule_status_id,
                rule_type_id,
                created_by_id,
                created_datetime,
                crc,
                record_status)
            values (
                v_rule_id,
                i_to_workflow_id,
                i_to_transition_id,
                rule_rec.rule_name,
                rule_rec.rule_description,
                rule_rec.notes,
                rule_rec.rule_status_id,
                rule_rec.rule_type_id,
                v_created_by_id,
                v_datetime,
                sysdate,
                base.active_record_status);

            /*
                Now copy specific rule
             */
            if rule_rec.rule_type_id = '100' then
                -- Authorization rule
                copy_rule_auth(i_from_workflow_id, i_to_workflow_id,
                               i_from_transition_id, i_to_transition_id,
                               v_from_rule_id, v_rule_id, v_created_by_id,
                               i_copy_groups);

            -- else if rule_rec.rule_type_id = 'xxx' then

            else
                raise unknown_rule_type;
            end if;

         end loop;

    end copy_rules;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE copy_rule_auth
-- DOES NOT COMMIT
----------------------------------------------------------------------
    procedure copy_rule_auth(
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_from_transition_id    in number,
        i_to_transition_id      in number,
        i_from_rule_id          in number,
        i_to_rule_id            in number,
        i_created_by_id         in number,
        i_copy_groups           in number)

    is

         -- Cursor for reading pn_wf_rule_auth record (there should be 1)
         cursor rule_auth_cur(i_from_workflow_id in number,
                              i_from_transition_id in number,
                              i_from_rule_id in number) is
            select *
            from pn_wf_rule_auth
            where
                workflow_id = i_from_workflow_id and
                transition_id = i_from_transition_id and
                rule_id = i_from_rule_id;
        rule_auth_rec   rule_auth_cur%rowtype;

        -- Cursor for reading rule_auth_has_group (multiple)
        cursor rule_auth_group_cur(i_from_workflow_id in number,
                                   i_from_transition_id in number,
                                   i_from_rule_id in number) is
            select *
            from pn_wf_rule_auth_has_group
            where
                workflow_id = i_from_workflow_id and
                transition_id = i_from_transition_id and
                rule_id = i_from_rule_id;

        v_new_step_id   pn_wf_rule_auth_has_group.step_id%type;

    begin

        open rule_auth_cur(i_from_workflow_id, i_from_transition_id, i_from_rule_id);
        fetch rule_auth_cur into rule_auth_rec;
        if rule_auth_cur%found then
            insert into pn_wf_rule_auth (
                rule_id,
                workflow_id,
                transition_id,
                crc,
                record_status
            ) values (
                i_to_rule_id,
                i_to_workflow_id,
                i_to_transition_id,
                sysdate,
                base.active_record_status
            );

            -- Only copy rule groups if flag indicates we are
            -- copying groups
            if i_copy_groups = 1 then

                for rule_auth_group_rec in rule_auth_group_cur(
                        i_from_workflow_id, i_from_transition_id, i_from_rule_id) loop

                    -- First determine new step id
                    select to_step_id into v_new_step_id
                    from
                        pn_workflow_step_copy
                    where
                        workflow_id = rule_auth_group_rec.workflow_id and
                        step_id = rule_auth_group_rec.step_id;

                    insert into pn_wf_rule_auth_has_group (
                        rule_id,
                        workflow_id,
                        transition_id,
                        group_id,
                        step_id
                    ) values (
                        i_to_rule_id,
                        i_to_workflow_id,
                        i_to_transition_id,
                        rule_auth_group_rec.group_id,
                        v_new_step_id
                    );
                end loop;

            end if; -- i_copy_groups = 1

        else
            -- Hmm, no data; thats ok, not an error
            null;
        end if;

    exception
        when others then
        begin
            if rule_auth_cur%isopen then
                close rule_auth_cur;
            end if;

            raise;
        end;

    end copy_rule_auth;
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

END; -- Package Body WORKFLOW_RULE
/

