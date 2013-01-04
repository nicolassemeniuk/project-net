/*  Add time summary report module description   */
insert into PN_PROPERTY (CONTEXT_ID, LANGUAGE, PROPERTY_TYPE, PROPERTY, PROPERTY_VALUE, RECORD_STATUS, IS_SYSTEM_PROPERTY, IS_TRANSLATABLE_PROPERTY, PROPERTY_VALUE_CLOB)
values (2000, 'en', 'text', 'prm.timesummaryreport.module.description', 'Time Summary Report', 'A', 0, 1, null);

/*    
* To add time summary report module in exisiting and new business spaces
*/
begin
    begin
      insert into pn_module(module_id, name, description, default_permission_actions)
      values(390, 'time_summary_report', '@prm.timesummaryreport.module.description', 0);
    end;
    for x in (select b.business_id from pn_business_space b) loop
        begin
          insert into pn_space_has_module(space_id, module_id, is_active)
          values(x.business_id, 390, 1);
          exception
          WHEN OTHERS THEN
               dbms_output.put_line('Record exists in pn_space_has_module.');
        end;
	-- insert module permissions for team member group
        for xx in ( select g.group_id from pn_space_has_group shg, pn_group g 
                    where shg.group_id = g.group_id 
                    and shg.space_id = x.business_id
                    and g.group_name = '@prm.security.group.type.teammember.name'
                    and g.is_system_group = 1) loop
            begin
               insert into pn_module_permission(space_id, group_id, module_id, actions) 
               values (x.business_id, xx.group_id, 390, 0);
               exception
               WHEN OTHERS THEN
                   dbms_output.put_line('Record exists in pn_space_has_group.');                   
            end;
        end loop;
	-- insert module permissions for space administrator group
        for xx in ( select g.group_id from pn_space_has_group shg, pn_group g 
                    where shg.group_id = g.group_id 
                    and shg.space_id = x.business_id
                    and g.group_name = '@prm.security.group.type.spaceadministrator.name'
                    and g.is_system_group = 1) loop
            begin
               insert into pn_module_permission(space_id, group_id, module_id, actions) 
               values (x.business_id, xx.group_id, 390, 65535);
               exception
               WHEN OTHERS THEN
                   dbms_output.put_line('Record exists in pn_space_has_group.');                   
            end;
        end loop;
	-- insert module permissions for power user group
        for xx in ( select g.group_id from pn_space_has_group shg, pn_group g 
                    where shg.group_id = g.group_id 
                    and shg.space_id = x.business_id
                    and g.group_name = '@prm.security.group.type.poweruser.name'
                    and g.is_system_group = 1) loop
            begin
               insert into pn_module_permission(space_id, group_id, module_id, actions) 
               values (x.business_id, xx.group_id, 390, 135);
               exception
               WHEN OTHERS THEN
                   dbms_output.put_line('Record exists in pn_space_has_group.');
            end;
        end loop;        
    end loop;
end;
/