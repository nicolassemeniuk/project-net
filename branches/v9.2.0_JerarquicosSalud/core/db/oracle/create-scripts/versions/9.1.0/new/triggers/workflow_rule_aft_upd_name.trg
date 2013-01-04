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
create or replace trigger workflow_rule_aft_upd_name
after insert or update of rule_name on PNET.pn_workflow_rule
for each row
when ((new.rule_name != old.rule_name) or old.rule_name is null)
begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.rule_id, :new.rule_name);
  else
    update pn_object_name
    set name = :new.rule_name
    where object_id = :new.rule_id;
  end if;
end;
/

