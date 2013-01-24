/*
* Updating pn_space_has_module to deactivate resources module from business space
*/
begin    
    for x in (select b.business_id from pn_business_space b) loop
      begin
          update pn_space_has_module
          set is_active = 0
          where space_id =  x.business_id
          and module_id = 360;

          exception
            WHEN OTHERS THEN
              dbms_output.put_line('Error while updating record in pn_space_has_module for business '|| x.business_id);
              raise;
      end;
    end loop;
end;
/