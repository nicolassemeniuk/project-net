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
create or replace view pn_group_view as
select g.group_id, g.group_name, g.group_desc, g.is_principal, g.is_system_group,
g.record_status, g.group_type_id, decode(members.member_count, null, 0, members.member_count) as member_count,
g.principal_owner_id, p.display_name as principal_owner_display_name
  from pn_group g, pn_group_member_count_view members, pn_person p
 where members.group_id(+) = g.group_id
 and p.person_id(+) = g.principal_owner_id
/

