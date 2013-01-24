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
create or replace view pn_doc_content_elements_view
(doc_content_id, document_id, version_id, doc_format_id, doc_format, file_size, file_handle, record_status, mime_type, app_icon_url, repository_id, repository_path)
as
select
    dce.doc_content_id,
    dv.doc_id,
    dv.doc_version_id,
    dce.doc_format_id,
    dft.format_name as doc_format,
    dce.file_size,
    dce.file_handle,
    dce.record_status,
    dft.mime_type,
    dft.app_icon_url,
	dce.repository_id,
	drb.repository_path
from
    pn_doc_content_element dce,
    pn_doc_version_has_content dvhc,
	pn_doc_version dv,
    pn_doc_format dft, pn_doc_repository_base drb
where
    dce.doc_content_id = dvhc.doc_content_id
and
    dce.doc_format_id = dft.doc_format_id
and
    dce.repository_id = drb.repository_id
and
    dvhc.doc_version_id = dv.doc_version_id
/

