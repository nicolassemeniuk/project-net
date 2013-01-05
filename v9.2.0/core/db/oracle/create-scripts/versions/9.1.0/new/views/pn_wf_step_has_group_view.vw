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
create or replace view pn_wf_step_has_group_view as
select sg.step_id, sg.workflow_id, sg.group_id, g.group_name, g.group_desc,
       g.is_principal, sg.is_participant, sg.created_by_id,
       pcreated.display_name as "CREATED_BY_FULL_NAME", sg.created_datetime,
       sg.modified_by_id, pmodified.display_name as "MODIFIED_BY_FULL_NAME",
       sg.modified_datetime, sg.crc, sg.record_status
  from pn_workflow_step_has_group sg, pn_person pcreated, pn_person pmodified,
       (select g.group_id,
               decode (
                   g.is_principal,
                   1, g.principal_owner_display_name,
                   g.group_name
               ) as group_name,
               g.group_desc, g.is_principal
          from pn_group_view g) g
 where pcreated.person_id(+) = sg.created_by_id
   and pmodified.person_id(+) = sg.modified_by_id
   and g.group_id = sg.group_id
/

