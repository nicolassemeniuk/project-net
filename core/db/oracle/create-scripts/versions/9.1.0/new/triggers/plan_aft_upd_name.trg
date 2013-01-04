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
create or replace trigger plan_aft_upd_name
after insert or update of plan_name on PNET.pn_plan
for each row
when ((new.plan_name != old.plan_name) or old.plan_name is null)
begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.plan_id, :new.plan_name);
  else
    update pn_object_name
    set name = :new.plan_name
    where object_id = :new.plan_id;
  end if;
end;
/

