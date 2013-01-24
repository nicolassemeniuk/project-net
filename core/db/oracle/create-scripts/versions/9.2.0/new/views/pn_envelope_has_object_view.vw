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
CREATE OR REPLACE VIEW PN_ENVELOPE_HAS_OBJECT_VIEW AS
SELECT wfe.envelope_id, wfe.workflow_id, wfe.workflow_name,
       wfe.workflow_description, wfe.space_id, wfe.strictness_id,
       wfe.strictness_name, wfe.strictness_description, wfe.current_version_id,
       wfe.envelope_name, wfe.envelope_description, wfe.created_by_id,
       wfe.created_by_full_name, wfe.created_datetime, wfe.modified_by_id,
       wfe.modified_by_full_name, wfe.modified_datetime, wfe.crc,
       wfe.record_status, wfe.is_active, wfe.current_step_id,
       wfe.current_step_name, wfe.current_step_description,
       wfe.current_step_notes_clob, wfe.current_status_id,
       wfe.current_status_name, wfe.current_status_description,
       wfe.last_transition_id, wfe.last_transition_verb,
       wfe.last_transition_description, wfe.current_priority_id,
       wfe.current_priority_name, wfe.current_priority_description,
       wfe.current_comments_clob, wfe.current_created_by_id,
       wfe.current_created_by_full_name, wfe.current_created_datetime,
       wfe.current_version_crc, wfe.current_version_record_status, eo.object_id
  FROM pn_workflow_envelope_view wfe, pn_envelope_has_object eo
 WHERE eo.envelope_id = wfe.envelope_id
/

