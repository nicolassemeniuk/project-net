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
begin
    for rec in 
	(select
      		pmp.space_id space_id,
      		pmp.group_id group_id
    	from
      		pn_module_permission pmp,
      		pn_person p
    	where
      		pmp.space_id = p.person_id
      		and p.user_status = 'Active'
    		group by pmp.space_id, pmp.group_id)
 loop
      begin
        --insert module for space
        insert into pn_space_has_module
          (space_id, module_id, is_active)
        values
          (rec.space_id,360,1);
      exception
          when others then
           null;
      end;
      begin
        --insert new entry in module_permission for this row
        insert into pn_module_permission
          (space_id, group_id, module_id, actions)
        values
          (rec.space_id, rec.group_id, 360, 65535);
        exception
          when others then
           null;
      end;
    end loop;
    --Commit any remaining insertions
    commit;
exception
    when others then
        begin
            dbms_output.put_line('Error occurred while updating module permissions for resource space.');
        end;
end;
/
