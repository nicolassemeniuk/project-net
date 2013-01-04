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
CREATE OR REPLACE TRIGGER event_aft_upd_name
AFTER INSERT  OR UPDATE OF
  event_name
ON pnet.pn_calendar_event
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
when ((new.event_name != old.event_name) or old.event_name is null)
begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    values
      (:new.calendar_event_id, :new.event_name);
  else
    update pn_object_name
    set name = :new.event_name
    where object_id = :new.calendar_event_id;

    --We have to update meeting's here too because the meeting trigger wouldn't
    --know about the change
    update pn_object_name
    set name = :new.event_name
    where object_id = (select meeting_id from pn_meeting where calendar_event_id = :new.calendar_event_id);
  end if;
end;
/

