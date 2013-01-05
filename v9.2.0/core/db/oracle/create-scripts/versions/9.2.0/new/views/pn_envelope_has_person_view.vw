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
create or replace view pn_envelope_has_person_view as
select
    env_person.person_id,
    env.envelope_id,
    env.workflow_id,
    env.space_id,
    env.strictness_id,
    env.strictness_name,
    env.strictness_description,
    env.current_version_id,
    env.envelope_name,
    env.envelope_description,
    env.created_by_id,
    env.created_by_full_name,
    env.created_datetime,
    env.modified_by_id,
    env.modified_by_full_name,
    env.modified_datetime,
    env.crc,
    env.record_status,
    env.is_active,
    env.current_step_id,
    env.current_step_name,
    env.current_step_description,
    env.current_step_notes_clob,
    env.current_status_id,
    env.current_status_name,
    env.current_status_description,
    env.last_transition_id,
    env.last_transition_verb,
    env.last_transition_description,
    env.current_priority_id,
    env.current_priority_name,
    env.current_priority_description,
    env.current_comments_clob,
    env.current_created_by_id,
    env.current_created_by_full_name,
    env.current_created_datetime,
    env.current_version_crc,
    env.current_version_record_status
from
    pn_workflow_envelope_view env,
    pn_envelope_step_person_view env_person
where
    env.envelope_id = env_person.envelope_id
and env.workflow_id = env_person.workflow_id
and env.current_step_id = env_person.step_id
and env_person.is_participant = 1
/

