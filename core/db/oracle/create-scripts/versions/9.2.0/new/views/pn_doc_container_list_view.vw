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
CREATE OR REPLACE VIEW PN_DOC_CONTAINER_LIST_VIEW AS
(SELECT
   doc.doc_id AS object_id,
   doc.doc_name AS name,
   0 as is_hidden,
   doc.object_type,
   doc.doc_container_id,
   doc.format_name AS format,
   doc.app_icon_url,
   '' as url,
   doc.doc_version_num AS version,
   doc.is_checked_out,
   doc.checked_out_by_id,
   doc.checked_out_by,
   doc.doc_status AS status,
   doc.doc_author AS author,
   doc.date_modified,
   doc.file_size,
   doc.short_file_name,
   doc.has_links,
   doc.has_workflows,
   doc.has_discussions,
   doc.doc_description as description,
   doc.doc_comment as comments
FROM
   pn_doc_by_space_view doc
)
UNION
(SELECT
   c.doc_container_id AS object_id,
   c.container_name AS name,
   c.is_hidden,
   'doc_container' as object_type,
   dco.doc_container_id,
   'File Folder' AS format,
   '' as app_icon_url,
   '' as url,
   0 AS version,
   0 AS is_checked_out,
   0 AS checked_out_by_id,
   '' AS checked_out_by,
   '' AS status,
   '' AS author,
   c.date_modified,
   0 AS file_size,
   '' AS short_file_name,
   0 as has_links,
   0 as has_workflows,
   0 as has_discussions,
   c.container_description as description,
   null as comments
from
   pn_doc_container c,
   (SELECT object_id, object_type, doc_container_id
    FROM pn_doc_container_objects_view
    WHERE object_type = 'doc_container') dco
WHERE
   c.doc_container_id = dco.object_id
AND
   c.record_status = 'A')
UNION
(SELECT
   b.bookmark_id AS object_id,
   b.name,
   0 as is_hidden,
   'bookmark' as object_type,
   b.container_id,
   'URL' AS format,
   '' as app_icon_url,
   b.url,
   0 AS version,
   0 AS is_checked_out,
   0 AS checked_out_by_id,
   '' AS checked_out_by,
   b.status,
   b.owner AS author,
   b.modified_date as date_modified,
   0 AS file_size,
   '' AS short_file_name,
   b.has_links as has_links,
   0 as has_workflows,
   0 as has_discussions,
   b.description as description,
   b.comments as comments
from
   pn_bookmark_view b
WHERE
   b.record_status = 'A')
/

