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
create or replace view pn_methodology_view as
select
    m.methodology_id,
    s.parent_space_id,
    s.child_space_id,
    m.methodology_name,
    m.methodology_desc,
    m.use_scenario_clob,
    m.status_id,
    m.created_by_id,
    profile.get_display_name(m.created_by_id) as created_by,
    m.created_date,
    m.modified_by_id,
    m.modified_date,
    profile.get_display_name(m.modified_by_id) as modified_by,
    m.record_status,
    m.is_global,
    m.crc
from
    pn_methodology_space m, pn_space_has_space s
where
    s.child_space_id = m.methodology_id
/

