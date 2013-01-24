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
create or replace view pn_doc_container_objects_view as
select
   dc.doc_container_id,
   dc.container_name AS doc_container_name,
   o.object_id,
   o.object_type,
   o.date_created,
   o.created_by
from
    pn_doc_container_has_object dcho, pn_object o, pn_doc_container dc
WHERE
   dc.doc_container_id = dcho.doc_container_id
AND
   dcho.object_id = o.object_id
/

