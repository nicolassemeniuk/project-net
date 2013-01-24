begin   
    begin
      insert into pn_module(module_id, name, description, default_permission_actions)
      values(380, 'blog', '@prm.blog.module.description', 207);
      exception
      WHEN OTHERS THEN
           dbms_output.put_line('Record exists.');  
    end; 
    for x in (select p.project_id from pn_project_space p) loop
        begin
          insert into pn_space_has_module(space_id, module_id, is_active)
          values(x.project_id, 340, 1);
          exception
          WHEN OTHERS THEN
               dbms_output.put_line('Record exists.');  
        end;
        begin
          insert into pn_space_has_module(space_id, module_id, is_active)
          values(x.project_id, 380, 1); 
          exception
          WHEN OTHERS THEN
               dbms_output.put_line('Record exists.');  
        end;                 
        for xx in (select distinct mp.group_id from pn_module_permission mp where mp.space_id = x.project_id) loop
            begin
               insert into pn_module_permission(space_id, group_id, module_id, actions) 
               values (x.project_id, xx.group_id, 340, 207);   
               exception
               WHEN OTHERS THEN
                   dbms_output.put_line('Record exists.');  
            end; 
            begin  
              insert into pn_module_permission(space_id, group_id, module_id, actions) 
              values (x.project_id, xx.group_id, 380, 207);    
              exception
              WHEN OTHERS THEN
                   dbms_output.put_line('Record exists.');                              
            end;                                                
        end loop; 
    end loop;
end;
/

begin
    for x in (select b.business_id from pn_business_space b) loop
        begin
          insert into pn_space_has_module(space_id, module_id, is_active)
          values(x.business_id, 340, 1);
          exception
          WHEN OTHERS THEN
               dbms_output.put_line('Record exists.');  
        end;
        for xx in (select distinct mp.group_id from pn_module_permission mp where mp.space_id = x.business_id) loop
            begin
               insert into pn_module_permission(space_id, group_id, module_id, actions) 
               values (x.business_id, xx.group_id, 340, 207);   
               exception
               WHEN OTHERS THEN
                   dbms_output.put_line('Record exists.');  
            end;
        end loop; 
    end loop;
end;
/