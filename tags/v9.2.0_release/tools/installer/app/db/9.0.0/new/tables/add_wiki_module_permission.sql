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
declare
  uncommitted_count number(20) := 0;
  
  cursor source_cursor is
    select 
      ps.project_id,
      shg.group_id
    from
      pn_project_space ps,
      pn_space_has_group shg
    where
      ps.project_id = shg.space_id(+);   
begin
    for rec in source_cursor loop
        --insert new entry in module_permission for this row
        insert into pn_module_permission
          (space_id, group_id, module_id, actions)
        values
          (rec.project_id, rec.group_id, 340, 207);
          
        uncommitted_count := uncommitted_count + 1;
        
        --Commit every 50th insert to make sure we don't overflow
        --rollback log.
        if (uncommitted_count >=50) then
            commit;
            uncommitted_count := 0;
        end if;
    end loop;
    
    --Commit any remaining insertions
    commit;
exception
    when others then
        begin
            dbms_output.put_line('Error occurred while updating module permissions for wiki.');
            raise;
        end;
end;
/