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
create or replace view pn_workflow_transition_view as
select
    tran.transition_id,
    tran.workflow_id,
    wf.workflow_name,
    tran.transition_verb,
    tran.transition_description,
    tran.begin_step_id,
    beginstep.step_name "BEGIN_STEP_NAME",
    tran.end_step_id,
    endstep.step_name "END_STEP_NAME",
    tran.created_by_id,
    pcreated.display_name as "CREATED_BY_FULL_NAME",
    tran.created_datetime,
    tran.modified_by_id,
    pmodified.display_name as "MODIFIED_BY_FULL_NAME",
    tran.modified_datetime,
    tran.crc,
    tran.record_status
from
    pn_workflow_transition tran,
    pn_workflow wf,
    pn_workflow_step beginstep, pn_workflow_step endstep,
    pn_person pcreated, pn_person pmodified
where
    wf.workflow_id = tran.workflow_id AND
    beginstep.step_id = tran.begin_step_id AND
    endstep.step_id = tran.end_step_id AND
    pcreated.person_id(+) = tran.created_by_id AND
    pmodified.person_id(+) = tran.modified_by_id
/

