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
create or replace view pn_user_view as
select
   u.user_id,
    p.display_name,
    u.username,
    p.user_status,
    u.domain_id,
    u.last_login,
    u.is_login,
    u.last_brand_id,
    case when phl.person_id is null then 0 else 1 end as has_license
from
    pn_user u, pn_person_view p,
    pn_person_has_license phl
where
    u.user_id = p.person_id
and phl.person_id(+) = u.user_id
/

