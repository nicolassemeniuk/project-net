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
CREATE OR REPLACE TRIGGER deliverable_aft_ins
AFTER INSERT
ON pn_phase_has_deliverable
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  select
    :new.deliverable_id, shp.space_id
  from
    pn_space_has_process shp,
    pn_phase ph
  where
    shp.process_id = ph.process_id
    and ph.phase_id = :new.phase_id;
end;
/

