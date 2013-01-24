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
create or replace view pn_current_task_version
(baseline_id, plan_id, task_id, task_version_id, work, work_units, duration, duration_units, date_start, date_finish)
as
select
  b.baseline_id,
  b.object_id,
  tv.task_id,
  tv.task_version_id,
  tv.work,
  tv.work_units,
  tv.duration,
  tv.duration_units,
  tv.date_start,
  tv.date_finish
from
  pn_baseline b,
  pn_baseline_has_task bht,
  pn_task_version tv
where
  b.baseline_id = bht.baseline_id
  and bht.task_version_id = tv.task_version_id
  and b.is_default_for_object = 1
/

