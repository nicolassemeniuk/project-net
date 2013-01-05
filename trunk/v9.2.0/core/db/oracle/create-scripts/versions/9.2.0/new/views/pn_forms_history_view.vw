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
create or replace view pn_forms_history_view as
select
    f.object_id,
    fh.forms_history_id,
    fh.action,
    fh.action_name,
    fh.action_by_id,
    p.display_name as action_by,
    fh.action_date,
    fh.action_comment
from
    pn_object f, pn_forms_history fh,
    pn_person p
where
    f.object_id = fh.object_id
and
    f.record_status = 'A'
and
    fh.action_by_id = p.person_id
/

