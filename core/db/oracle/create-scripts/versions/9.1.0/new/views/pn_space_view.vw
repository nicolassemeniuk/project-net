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
create or replace view pn_space_view as
select person_id space_id, display_name space_name , null space_desc , 'person' space_type , record_status, null parent_space_name from pn_person
union
select business_id space_id, business_name space_name, business_desc space_desc , 'business' space_type, record_status, null parent_space_name from pn_business
union
select project_id space_id, project_name space_name, project_desc space_desc ,'project' space_type, record_status, null parent_space_name from pn_project_space
union
select m.methodology_id space_id, m.methodology_name space_name, m.methodology_desc space_desc , 'methodology' space_type, m.record_status, decode(b.business_id,null,'Personal',b.business_name) parent_space_name
from pn_methodology_space m, pn_space_has_space s, pn_business b
where m.methodology_id = s.child_space_id and
      s.parent_space_id = b.business_id(+)
union
select application_id space_id, application_name space_name, application_desc space_desc ,'application' space_type, record_status, null parent_space_name from pn_application_space
union
select configuration_id space_id, configuration_name space_name, configuration_desc space_desc ,'configuration' space_type, record_status, null parent_space_name from pn_configuration_space
union
select 5, '@prm.enterprise.space.type.enterprise.name', '', 'enterprise', 'A', '' from dual
union
select 10, '@prm.resource.space.type.resource.name', '', 'resources', 'A', '' from dual
/

