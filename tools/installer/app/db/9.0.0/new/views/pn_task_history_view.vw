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
create or replace view pn_task_history_view as
select
    t.task_id,
    t.task_name,
    th.task_history_id,
    th.action,
    th.action_name,
    th.action_by_id,
    p.display_name as action_by,
    th.action_date,
    th.action_comment
from
    pn_task t, pn_task_history th,
    pn_person p
where
    t.task_id = th.task_id
and
    t.record_status = 'A'
and
    th.action_by_id = p.person_id
/

