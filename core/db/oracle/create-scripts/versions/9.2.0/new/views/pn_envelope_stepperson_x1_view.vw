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
create or replace view pn_envelope_stepperson_x1_view as
select
    env.envelope_id,
    wf.workflow_id,
    step.step_id,
    grouppers.person_id "PERSON_ID",
    stepgroup.is_participant
from
    pn_workflow_envelope env,
    pn_workflow wf,
    pn_workflow_step step,
    pn_workflow_step_has_group stepgroup,
    pn_group grp,
    pn_group_has_person grouppers
where
    env.workflow_id = wf.workflow_id and
    wf.record_status = 'A' and
    wf.workflow_id = step.workflow_id and
    step.record_status = 'A' and
    step.step_id = stepgroup.step_id and
    stepgroup.record_status = 'A' and
    stepgroup.group_id = grp.group_id and
    grp.record_status = 'A' and
    grp.group_id = grouppers.group_id
 union
select
    env.envelope_id,
    wf.workflow_id,
    step.step_id,
    env.created_by_id "PERSON_ID",
    stepgroup.is_participant
from
    pn_workflow_envelope env,
    pn_workflow wf,
    pn_workflow_step step,
    pn_workflow_step_has_group stepgroup
where
    env.workflow_id = wf.workflow_id and
    wf.record_status = 'A' and
    wf.workflow_id = step.workflow_id and
    step.record_status = 'A' and
    step.step_id = stepgroup.step_id and
    stepgroup.record_status = 'A' and
    stepgroup.group_id = 100
/

