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
create or replace view pn_doc_history_view
(document_id, document_name, doc_history_id, action, action_name, action_by_id, action_by, action_date, action_comment)
as
select
    d.doc_id,
    d.doc_name,
    dh.doc_history_id,
    dh.action,
    dh.action_name,
    dh.action_by_id,
    p.display_name as action_by,
    dh.action_date,
    dh.action_comment
from
    pn_document d, pn_doc_history dh,
    pn_person p
where
    d.doc_id = dh.doc_id
and
    d.record_status = 'A'
and
    dh.action_by_id = p.person_id
/

