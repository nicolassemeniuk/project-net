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
create or replace view pn_phase_has_task_view as
select
    pt.phase_id,
    pt.task_id,
    phase.record_status as phase_record_status,
    task.record_status as task_record_status
from
    pn_phase_has_task pt,
    pn_phase phase,
    pn_task task
where
    phase.phase_id = pt.phase_id
and task.task_id = pt.task_id
/

