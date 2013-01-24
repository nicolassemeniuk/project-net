/* Updating security permission actions for the blog module. This will add create permission for all users in space.  */
begin
   begin
      update pn_module set default_permission_actions = 207 
      where module_id = 380;
      exception
      WHEN OTHERS THEN
           dbms_output.put_line('Error while updating default permission actions.');  
    end; 
    for x in (select p.project_id from pn_project_space p) loop 
        for xx in (select distinct mp.group_id from pn_module_permission mp) loop             
            begin              
                update pn_module_permission set actions = 207
                where space_id = x.project_id
                and group_id = xx.group_id
                and actions = 1
                and module_id = 380;                
                exception
                WHEN OTHERS THEN
                  dbms_output.put_line('Error while updating module permissions.');               
            end;
        end loop;
    end loop;
end;
/