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
create or replace view pn_doc_version_view
(version_id, document_id, document_name, doc_version_num, doc_version_label, date_modified, modified_by, doc_comment, source_file_name, short_file_name, doc_author_id, author, is_checked_out, checked_out_by_id, cko_by, date_checked_out, cko_due, record_status, repository_id, repository_path, file_handle)
as
select
    dv.doc_version_id,
    d.doc_id,
    d.doc_name,
    dv.doc_version_num,
    dv.doc_version_label,
    dv.date_modified,
    modified.display_name as modified_by,
    dv.doc_comment,
    dv.source_file_name,
    dv.short_file_name,
    dv.doc_author_id,
    author.display_name as author,
    dv.is_checked_out,
    dv.checked_out_by_id,
    cko_by.display_name as cko_by,
    dv.date_checked_out,
    dv.checkout_due,
    dv.record_status,
    dce.repository_id,
    drb.repository_path,
    dce.file_handle
from
    pn_doc_content_element dce, pn_doc_version_has_content dvhc,
	pn_doc_version dv, pn_document d,
    pn_person modified, pn_person author, pn_person cko_by,
	pn_doc_repository_base drb
where
	d.doc_id = dv.doc_id
and
    dv.doc_version_id = dvhc.doc_version_id
and
    dvhc.doc_content_id = dce.doc_content_id
and
    dce.repository_id = drb.repository_id
and
    dv.modified_by_id = modified.person_id
and
    dv.doc_author_id = author.person_id
and
    dv.checked_out_by_id = cko_by.person_id (+)
and
	d.record_status = 'A'
/

