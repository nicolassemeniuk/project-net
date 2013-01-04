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
create or replace view pn_baseline_task
(baseline_id, baseline_name, baseline_create_date, task_id, task_version_id, task_name, task_desc, task_type, duration, work, work_units, work_complete, date_start, work_complete_units, date_finish, actual_start, actual_finish, priority, percent_complete, date_created, date_modified, modified_by, duration_units, parent_task_id, record_status, critical_path, seq, ignore_times_for_dates, is_milestone, early_start, early_finish, late_start, late_finish, work_percent_complete, legacy_flag, constraint_type, constraint_date, deadline, parent_task_version_id, plan_version_id, calculation_type_id)
as
select
  b.baseline_id,
  b.name,
  b.date_created,
  bt.task_id,
  bt.task_version_id,
  bt.task_name,
  bt.task_desc,
  bt.task_type,
  bt.duration,
  bt.work,
  bt.work_units,
  bt.work_complete,
  bt.date_start,
  bt.work_complete_units,
  bt.date_finish,
  bt.actual_start,
  bt.actual_finish,
  bt.priority,
  bt.percent_complete,
  bt.date_created,
  bt.date_modified,
  bt.modified_by,
  bt.duration_units,
  bt.parent_task_id,
  bt.record_status,
  bt.critical_path,
  bt.seq,
  bt.ignore_times_for_dates,
  bt.is_milestone,
  bt.early_start,
  bt.early_finish,
  bt.late_start,
  bt.late_finish,
  bt.work_percent_complete,
  bt.legacy_flag,
  bt.constraint_type,
  bt.constraint_date,
  bt.deadline,
  bt.parent_task_version_id,
  bt.plan_version_id,
  bt.calculation_type_id
from
  pn_baseline b,
  pn_baseline_has_task bht,
  pn_task_version bt
where
  b.baseline_id = bht.baseline_id
  and bht.task_version_id = bt.task_version_id
/

