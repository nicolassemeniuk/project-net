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

/*
* Updating sequence numbers in pn_class_instance for already existing form records
*/

declare 
	 rc SYS_REFCURSOR;
	 v_object_id NUMBER;
	 v_seq_num NUMBER;  
begin    
    for x in (select 'pnet_user.' || master_table_name table_name from pn_class where master_table_name is not null) loop
      begin
               
         open rc for 'select object_id, seq_num from ' || x.table_name;
             loop
                fetch rc into v_object_id, v_seq_num;
                
                update pn_class_instance
                set seq_num = v_seq_num
                where class_instance_id = v_object_id; 
                
                EXIT WHEN rc%NOTFOUND;
             end loop;

          exception
            WHEN OTHERS THEN
              dbms_output.put_line('Error while updating record');
              raise;
              
         commit;
      end;
    end loop;
end;
/