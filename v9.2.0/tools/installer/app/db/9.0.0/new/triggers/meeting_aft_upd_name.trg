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
CREATE OR REPLACE TRIGGER meeting_aft_upd_name
AFTER INSERT  OR UPDATE
ON pnet.pn_meeting
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
when ((new.calendar_event_id != old.calendar_event_id) or old.calendar_event_id is null)
begin
  if INSERTING then
    insert into pn_object_name
      (object_id, name)
    select
      :new.meeting_id, ce.event_name
    from
      pn_calendar_event ce
    where
      ce.calendar_event_id = :new.calendar_event_id;
  else
    update pn_object_name
    set name =
      (select event_name from pn_calendar_event ce where ce.calendar_event_id = :new.calendar_event_id)
    where
      object_id = :new.meeting_id;
  end if;
end;
/

