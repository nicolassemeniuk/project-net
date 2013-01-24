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
CREATE OR REPLACE VIEW PN_WORKFLOW_VIEW AS
SELECT
    wfspace.space_id,
    wf.workflow_id,
    wf.workflow_name,
    wf.strictness_id,
    wfs.strictness_name,
    wf.workflow_description,
    wf.notes,
    wf.is_published,
    wf.owner_id,
    powner.display_name as "OWNER_FULL_NAME",
    wf.created_by_id,
    pcreated.display_name as "CREATED_BY_FULL_NAME",
    wf.created_datetime,
    wf.modified_by_id,
    pmodified.display_name as "MODIFIED_BY_FULL_NAME",
    wf.modified_datetime,
    wf.is_generic,
    wf.crc,
    wf.record_status,
    nvl(wfea.active_envelope_count,0) "ACTIVE_ENVELOPE_COUNT"
FROM
    pn_space_has_workflow wfspace,
    pn_workflow wf,
    pn_workflow_strictness wfs,
    pn_person powner, pn_person pcreated, pn_person pmodified,
    pn_envelope_active_view wfea
WHERE
    wf.workflow_id = wfspace.workflow_id AND
    wfs.strictness_id(+) = wf.strictness_id AND
    powner.person_id(+) = wf.owner_id AND
    pcreated.person_id(+) = wf.created_by_id AND
    pmodified.person_id(+) = wf.modified_by_id AND
    wfea.workflow_id(+) = wf.workflow_id
/

