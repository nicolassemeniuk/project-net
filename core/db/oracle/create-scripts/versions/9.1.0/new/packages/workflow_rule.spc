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
CREATE OR REPLACE PACKAGE workflow_rule IS
--==================================================================
-- Purpose: PNET Workflow Rule procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        26-Sep-00  Created it.
--==================================================================

   -- Package constants
   G_workflow_object_type       pn_object.object_type%type := 'workflow';
   G_step_object_type           pn_object.object_type%type := 'workflow_step';
   G_transition_object_type     pn_object.object_type%type := 'workflow_transition';
   G_rule_object_type           pn_object.object_type%type := 'workflow_rule';

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
        o_return_value   out number);
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
        o_return_value   out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_RULE
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_rule (
        i_rule_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- COPY_RULES
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure copy_rules (
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_from_transition_id    in number,
        i_to_transition_id      in number,
        i_created_by_id     in number,
        i_copy_groups       in number);
----------------------------------------------------------------------

END; -- Package Specification WORKFLOW_RULE
/

