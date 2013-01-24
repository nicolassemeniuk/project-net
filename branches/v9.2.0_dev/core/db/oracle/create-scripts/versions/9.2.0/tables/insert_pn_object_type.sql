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

-- Inserting new object type as "assignment_work" 

insert into pn_object_type(object_type, master_table_name, object_type_desc, parent_object_type, default_permission_actions, is_securable, is_workflowable)
values ('assignment_work','pn_assignemnt_work','@prm.assignment.work.objecttype.description',null,1,0,0)
/