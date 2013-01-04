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
CREATE OR REPLACE PACKAGE workflow IS
--==================================================================
-- Purpose: PNET Workflow procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        05-Sep-00  Created it.
-- TimM        12-Sep-00  Added i_space_id to CREATE_WORKFLOW
-- TimM        18-Sep-00  Added CREATE_STEP, MODIFY_STEP
-- TimM        19-Sep-00  Added CREATE_TRANSITION, MODIFY_TRANSITION
-- TimM        21-Sep-00  Added CREATE/MODIFY/REMOVE STEP_HAS_GROUP
-- TimM        02-Oct-00  Added CREATE_ENVELOPE
-- TimM        11-Oct-00  Added MODIFY_ENVELOPE
-- TimM        27-Oct-00  Changed signature of STEP_MODIFY, STEP_CREATE
--                        for entry_status_id
-- TimM        01-Nov-00  Changed signature of ADD_ENVELOPE_OBJECT to
--                        change i_object_properties to i_object_properties_id
--==================================================================

   -- Package constants
   G_workflow_object_type       constant pn_object.object_type%type := 'workflow';
   G_step_object_type           constant pn_object.object_type%type := 'workflow_step';
   G_transition_object_type     constant pn_object.object_type%type := 'workflow_transition';
   G_rule_object_type           constant pn_object.object_type%type := 'workflow_rule';
   G_envelope_object_type       constant pn_object.object_type%type := 'workflow_envelope';
   G_envelope_version_object_type constant pn_object.object_type%type := 'workflow_envelope_version';
   G_history_object_type        constant pn_object.object_type%type := 'workflow_envelope_history';

   -- Raise-able errors
   unspecified_error  exception;
   pragma exception_init(unspecified_error, -20000);
   space_not_found    exception;

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
        o_return_value   OUT number);
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
        o_return_value   out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_WORKFLOW
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_workflow (
        i_workflow_id    in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_STEP
----------------------------------------------------------------------
    procedure create_step
      ( i_workflow_id       in varchar2,
        i_step_name         in varchar2,
        i_step_sequence     in varchar2,
        i_step_description  in varchar2,
        i_is_initial_step   in number,
        i_is_final_step     in number,
        i_entry_status_id   in number,
        i_subscription_id   in number,
        i_created_by_id     in varchar2,
        i_is_notes_null     in number,
        o_notes_clob        out clob,
        o_step_id           out varchar2);
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
        o_notes_clob        out clob);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_STEP
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_step (
        i_step_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
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
        o_return_value           out number);
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
        o_return_value           out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_TRANSITION
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    -- no output parms to facilitate batching
    procedure remove_transition (
        i_transition_id  in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date);

    procedure remove_transition (
        i_transition_id  in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
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
        o_return_value           out number);
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
        o_return_value           out number);
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
        o_return_value           out number);
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
        o_envelope_id    out varchar2);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- ADD_ENVELOPE_OBJECT
-- DOES NOT COMMIT OR ROLLBACK
-- Adds an object to the specified envelope by creating
-- pn_envelope_has object and pn_envelope_version_has_object
-- records (for the current version)
----------------------------------------------------------------------
    procedure add_envelope_object
      ( i_envelope_id    in varchar2,
        i_object_id       in varchar2,
        i_object_type     in varchar2,
        i_object_version_id in varchar2,
        i_object_properties_id in varchar2,
        i_created_by_id  in varchar2);
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
        o_comments_clob  out clob);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_HISTORY
----------------------------------------------------------------------
    PROCEDURE create_history
      ( i_envelope_id       IN varchar2,
        i_history_action_id IN varchar2,
        i_action_by_id      IN varchar2,
        i_action_datetime   IN date,
        i_history_message_id IN varchar2,
        o_history_id        OUT varchar2,
        o_return_value      OUT number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- IS_ACTIVE_ENVELOPE
-- Returns:
--      1 - Envelope is active
--      0 - Envelope is not active
----------------------------------------------------------------------
    FUNCTION IS_ACTIVE_ENVELOPE
      ( i_envelope_id   IN varchar2)
      RETURN number;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- IS_OBJECT_IN_ACTIVE_ENVELOPE
-- Indicates whether an object is in an active envelope
-- Input Parameters
--      i_object_id     the object id to check for
-- Returns
--      0   - object is not in an active envelope
--      1   - object is in an active envelope
----------------------------------------------------------------------
    function is_object_in_active_envelope
        ( i_object_id   in number)
        return number;

    function is_object_in_active_envelope
        ( i_object_id   in varchar2)
        return number;

/**
 * COPY_ALL
 * See below for details
 */
    PROCEDURE copy_all
      ( i_from_space_id     IN varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        o_return_value      OUT number);

/**
 * COPY_ALL
 * Copies all workflows for a space to another space
 * @param i_from_space_id space id of space from which to copy
 * @param i_to_space_id space id of space to which to copy
 * @param i_created_by_id user id of person performing copy
 * @param i_copy_groups 1 = copy groups associated with workflows;
 *                      0 = do not copy groups associated with workflows
 *   Specify "1" when copying a workflow within the same space
 *   Otherwise "0" should be specified, since the groups will be different
 *   between spaces.
 * @param o_return_value Returns a status code indicating the success
 */
    PROCEDURE copy_all
      ( i_from_space_id     IN varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        i_copy_groups       in number,
        o_return_value      OUT number);
----------------------------------------------------------------------

END; -- Package Specification WORKFLOW
/

