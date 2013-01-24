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
create or replace view pn_workflow_rule_view as
select
    r.rule_id,
    r.transition_id,
    r.workflow_id,
    r.rule_type_id,
    rt.rule_type_name "RULE_TYPE_NAME",
    r.rule_status_id,
    rs.status_name "RULE_STATUS_NAME",
    r.rule_name,
    r.rule_description,
    r.notes,
    r.created_by_id,
    pcreated.display_name as "CREATED_BY_FULL_NAME",
    r.created_datetime,
    r.modified_by_id,
    pmodified.display_name as "MODIFIED_BY_FULL_NAME",
    r.modified_datetime,
    r.crc,
    r.record_status
from
    pn_workflow_rule r,
    pn_workflow_rule_type rt,
    pn_workflow_rule_status rs,
    pn_person pcreated, pn_person pmodified
where
    rt.rule_type_id = r.rule_type_id and
    rs.rule_status_id = r.rule_status_id and
    pcreated.person_id(+) = r.created_by_id and
    pmodified.person_id(+) = r.modified_by_id
/

