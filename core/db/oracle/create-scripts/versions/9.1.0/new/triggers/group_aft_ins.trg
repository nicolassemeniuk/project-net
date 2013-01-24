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
CREATE OR REPLACE TRIGGER group_aft_ins
AFTER INSERT
ON pnet.pn_space_has_group
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
declare
  v_exists number;
begin
  --We need to check that the object isn't already in this
  --table and space because the group may have been added
  --from a parent business, removed, then added again.
  v_exists := 0;
  select count(*) into v_exists from pn_object_space
  where object_id = :new.group_id
    and space_id = :new.space_id;

  if (v_exists = 0) then
      insert into pn_object_space
      (object_id, space_id)
      values
      (:new.group_id, :new.space_id);
  end if;
end;
/

