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
create or replace view pn_envelope_step_person_view as
select
/*
    This view returns the participation of persons at a step
    for a workflow (against envelope).  Note - If the person belongs
    to a group where is_participant = 1 AND to a group where is_participant = 0
    the person is returned only ONCE as the row where is_participant = 1
    I.E. duplicates are removed and is_participant = 1 overrides is_participant = 0
*/
    envpers.envelope_id,
    envpers.workflow_id,
    envpers.step_id,
    envpers.person_id,
    envpers.is_participant
from
    pn_envelope_stepperson_x1_view envpers
where
    envpers.is_participant = 1
    or
    (envpers.is_participant = 0 and
     not exists (
        select 1 from pn_envelope_stepperson_x1_view envpers2
        where
            envpers2.envelope_id = envpers.envelope_id and
            envpers2.workflow_id = envpers.workflow_id and
            envpers2.step_id = envpers.step_id and
            envpers2.person_id = envpers.person_id and
            envpers2.is_participant = 1
        )
    )
/

