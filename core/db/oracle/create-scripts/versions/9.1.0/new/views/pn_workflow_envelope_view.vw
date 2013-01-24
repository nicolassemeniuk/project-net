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
CREATE OR REPLACE VIEW PN_WORKFLOW_ENVELOPE_VIEW AS
SELECT
    wfe.envelope_id,
    wfe.workflow_id,
    wf.workflow_name,
    wf.workflow_description,
    wfspace.space_id,
    wfe.strictness_id,
    wfs.strictness_name,
    wfs.strictness_description,
    wfe.current_version_id,
    wfe.envelope_name,
    wfe.envelope_description,
    wfe.created_by_id,
    pcreated.display_name as "CREATED_BY_FULL_NAME",
    wfe.created_datetime,
    wfe.modified_by_id,
    pmodified.display_name as "MODIFIED_BY_FULL_NAME",
    wfe.modified_datetime,
    wfe.crc,
    wfe.record_status,
    workflow.is_active_envelope(wfe.envelope_id) "IS_ACTIVE",
    wfev.step_id "CURRENT_STEP_ID",
    wfev.step_name "CURRENT_STEP_NAME",
    wfev.step_description "CURRENT_STEP_DESCRIPTION",
    wfev.step_notes_clob "CURRENT_STEP_NOTES_CLOB",
    wfev.status_id "CURRENT_STATUS_ID",
    wfev.status_name "CURRENT_STATUS_NAME",
    wfev.status_description "CURRENT_STATUS_DESCRIPTION",
    wfev.transition_id "LAST_TRANSITION_ID",
    wfev.transition_verb "LAST_TRANSITION_VERB",
    wfev.transition_description "LAST_TRANSITION_DESCRIPTION",
    wfev.priority_id "CURRENT_PRIORITY_ID",
    wfev.priority_name "CURRENT_PRIORITY_NAME",
    wfev.priority_description "CURRENT_PRIORITY_DESCRIPTION",
    wfev.comments_clob "CURRENT_COMMENTS_CLOB",
    wfev.created_by_id "CURRENT_CREATED_BY_ID",
    wfev.created_by_full_name "CURRENT_CREATED_BY_FULL_NAME",
    wfev.created_datetime "CURRENT_CREATED_DATETIME",
    wfev.crc "CURRENT_VERSION_CRC",
    wfev.record_status "CURRENT_VERSION_RECORD_STATUS"
FROM
    pn_workflow_envelope wfe,
    pn_workflow wf,
    pn_envelope_version_view wfev,
    pn_workflow_strictness wfs,
    pn_person pcreated, pn_person pmodified,
    pn_space_has_workflow wfspace
WHERE
    wfspace.workflow_id = wfe.workflow_id AND       -- Join space for workflow to be envelope space
    wfe.workflow_id = wf.workflow_id AND
    wfe.current_version_id = wfev.version_id AND
    wfe.envelope_id = wfev.envelope_id AND
    wfe.strictness_id = wfs.strictness_id AND
    wfe.created_by_id = pcreated.person_id(+) AND
    wfe.modified_by_id = pmodified.person_id(+)
/

