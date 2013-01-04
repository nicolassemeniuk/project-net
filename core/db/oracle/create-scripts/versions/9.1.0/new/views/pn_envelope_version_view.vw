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
CREATE OR REPLACE VIEW PN_ENVELOPE_VERSION_VIEW AS
SELECT
    wfev.envelope_id,
    wfev.version_id,
    wfev.workflow_id,
    wfev.step_id,
    wfstep.step_name,
    wfstep.step_description,
    wfstep.notes_clob "STEP_NOTES_CLOB",
    wfev.status_id,
    wfstatus.status_name,
    wfstatus.status_description,
    wfev.transition_id,
    wft.transition_verb,
    wft.transition_description,
    wfev.priority_id,
    pri_code.code_name "PRIORITY_NAME",
    pri_code.code_desc "PRIORITY_DESCRIPTION",
    wfev.comments_clob,
    wfev.created_by_id,
    pcreated.display_name as "CREATED_BY_FULL_NAME",
    wfev.created_datetime,
    wfev.crc,
    wfev.record_status
FROM
    pn_envelope_version wfev,
    pn_person pcreated,
    pn_workflow_step wfstep,
    pn_workflow_status wfstatus,
    pn_workflow_transition wft,
    pn_global_domain pri_code
WHERE
    pcreated.person_id(+) = wfev.created_by_id AND
    wfstep.step_id(+) = wfev.step_id AND
    wfstatus.status_id(+) = wfev.status_id AND
    wft.transition_id(+) = wfev.transition_id AND
    pri_code.table_name(+) = 'pn_envelope_version' and
    pri_code.column_name(+) = 'priority_id' and
    pri_code.code(+) = wfev.priority_id
/

