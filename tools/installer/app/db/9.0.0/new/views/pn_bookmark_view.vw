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
create or replace view pn_bookmark_view
(bookmark_id, name, description, url, status_id, status, owner_id, owner, comments, modified_date, modified_by_id, modified_by, container_id, owner_space_id, record_status, crc, has_links)
as
select
    b.bookmark_id,
    b.name,
    b.description,
    b.url,
    b.status_id,
    gd.code_name as status,
    b.owner_id,
    profile.get_display_name (b.owner_id) as owner,
    b.comments,
    b.modified_date,
    b.modified_by_id,
    profile.get_display_name (b.modified_by_id) as modified_by,
    --document.get_container_for_object_id (b.bookmark_id) as container_id,
    dcho.doc_container_id,
    document.get_space_for_id (b.bookmark_id) as owner_space_id,
    b.record_status,
    b.crc,
    document.has_links (b.bookmark_id) as has_links
from
    pn_bookmark b, pn_global_domain gd
    , pn_doc_container_has_object dcho
where
    b.status_id = gd.code
and
    gd.table_name = 'pn_document'
and
    gd.column_name = 'doc_status_id'
and
    b.record_status = 'A'
and dcho.object_id = b.bookmark_id
/

