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
CREATE OR REPLACE PACKAGE BODY task IS
-- Purpose: Repository for TASK Related procedures
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
   -- Deepak   11-Oct-01 Added Log event procedure , more later

----------------------------------------------------------------------

----------------------------------------------------------------------
-- LOGS_EVENT
----------------------------------------------------------------------
PROCEDURE log_event
(
    task_id IN varchar2,
    whoami IN varchar2,
    action IN varchar2,
    action_name IN varchar2,
    notes IN varchar2
)
IS
    v_task_id     pn_task.task_id%type := TO_NUMBER(task_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_task_history.task_history_id%type;
    v_action          pn_task_history.action%type := action;
    v_action_name     pn_task_history.action_name%type := action_name;
    v_action_comment  pn_task_history.action_comment%type := notes;

BEGIN
    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- insert a history record for this document

    insert into pn_task_history (
        task_id,
        task_history_id,
        action,
        action_name,
        action_by_id,
        action_date,
        action_comment)
    values (
        v_task_id,
        v_history_id,
        v_action,
        v_action_name,
        v_whoami,
        SYSDATE,
        v_action_comment
    );
END;  -- Procedure LOG_EVENT
----------------------------------------------------------------------

----------------------------------------------------------------------


   -- Enter further code below as specified in the Package spec.
END;
/

