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
CREATE OR REPLACE VIEW PN_DOC_BY_SPACE_VIEW AS
SELECT d.doc_id,
       d.doc_name,
       d.crc,
       'document' AS object_type,
       d.doc_status_id,
       gd.code_name AS doc_status,
       d.doc_description,
       d.current_version_id,
       d.record_status,
       ds.doc_space_id,
       ds.doc_space_name,
       dc.doc_container_id,
       dc.container_name,
       dc.is_hidden,
       dv.doc_author_id,
       PROFILE.get_display_name (dv.doc_author_id) AS doc_author,
       dv.doc_version_id,
       dv.doc_version_num,
       dv.doc_version_label,
       dv.doc_version_name,
       dv.source_file_name,
       dv.short_file_name,
       dv.doc_description AS version_description,
       dv.date_modified,
       dv.modified_by_id,
       PROFILE.get_display_name (dv.modified_by_id) AS modified_by,
       dv.is_checked_out,
       dv.checked_out_by_id,
       PROFILE.get_display_name (dv.checked_out_by_id) AS checked_out_by,
       dv.date_checked_out,
       dv.checkout_due,
       dv.doc_comment,
       dce.doc_content_id,
       dce.doc_format_id,
       dce.file_size,
       dce.file_handle,
       dce.repository_id,
       drb.repository_path,
       dft.format_name,
       dft.mime_type,
       dft.app_icon_url,
       document.has_links (d.doc_id) AS has_links,
       workflow.is_object_in_active_envelope (d.doc_id) AS has_workflows,
       discussion.object_has_discussion (d.doc_id) AS has_discussions,
       obj.date_created as date_created
  FROM pn_doc_space ds,
       pn_doc_space_has_container dshc,
       pn_doc_container dc,
       pn_doc_container_has_object dcho,
       pn_global_domain gd,
       pn_doc_format dft,
       pn_doc_repository_base drb,
       pn_doc_content_element dce,
       pn_doc_version dv,
       pn_doc_version_has_content dvhc,
       pn_document d,
       pn_space_has_doc_space shds,
       pn_object obj
 WHERE d.current_version_id = dv.doc_version_id
   AND d.current_version_id = dvhc.doc_version_id
   AND dvhc.doc_content_id = dce.doc_content_id
   AND dft.doc_format_id = dce.doc_format_id
   AND dce.repository_id = drb.repository_id
   AND d.doc_status_id = gd.code
   AND d.record_status = 'A'
   AND gd.table_name = 'pn_document'
   AND gd.column_name = 'doc_status_id'
   AND dcho.object_id = d.doc_id
   AND dcho.doc_container_id = dc.doc_container_id
   AND dcho.doc_container_id = dshc.doc_container_id
   AND dshc.doc_space_id = ds.doc_space_id
   AND ds.doc_space_id = shds.doc_space_id
   AND d.doc_id = obj.object_id
/

