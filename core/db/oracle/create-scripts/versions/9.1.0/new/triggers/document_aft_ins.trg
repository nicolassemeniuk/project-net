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
CREATE OR REPLACE TRIGGER document_aft_ins
AFTER INSERT
ON pn_doc_container_has_object
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
begin
  insert into pn_object_space
  (object_id, space_id)
  select
    :new.object_id, space_id
  from
    pn_space_has_doc_space shds,
    pn_doc_space_has_container dshc
  where
    shds.doc_space_id = dshc.doc_space_id
    and dshc.doc_container_id = :new.doc_container_id
    and not exists (select 1 from pn_object_space where object_id = :new.object_id and space_id = shds.space_id);
end;
/

