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
CREATE OR REPLACE VIEW PN_WORKFLOW_STEP_VIEW AS
SELECT
    step.step_id,
    step.workflow_id,
    wf.workflow_name,
    step.step_name,
    step.step_sequence,
    step.step_description,
    step.notes_clob,
    step.is_initial_step,
    step.is_final_step,
    step.entry_status_id,
    wfstatus.status_name "ENTRY_STATUS_NAME",
    wfstatus.status_description "ENTRY_STATUS_DESCRIPTION",
    step.subscription_id,
    step.created_by_id,
    step.created_datetime,
    pcreated.display_name as "CREATED_BY_FULL_NAME",
    step.modified_by_id,
    step.modified_datetime,
    pmodified.display_name as "MODIFIED_BY_FULL_NAME",
    step.crc,
    step.record_status,
    nvl((SELECT count(tran.begin_step_id)
         FROM pn_workflow_transition tran
         WHERE
            tran.workflow_id = step.workflow_id AND
            tran.begin_step_id = step.step_id and
            tran.record_status = 'A'),0) AS "BEGIN_TRANSITION_COUNT",
    nvl((SELECT count(tran.end_step_id)
         FROM pn_workflow_transition tran
         WHERE
            tran.workflow_id = step.workflow_id AND
            tran.end_step_id = step.step_id and
            tran.record_status = 'A'),0) AS "END_TRANSITION_COUNT"
FROM
    pn_workflow_step step,
    pn_workflow_view wf,
    pn_workflow_status wfstatus,
    pn_person pcreated, pn_person pmodified
WHERE
    wf.workflow_id = step.workflow_id AND
    wfstatus.status_id(+) = step.entry_status_id AND
    pcreated.person_id(+) = step.created_by_id AND
    pmodified.person_id(+) = step.modified_by_id
/

