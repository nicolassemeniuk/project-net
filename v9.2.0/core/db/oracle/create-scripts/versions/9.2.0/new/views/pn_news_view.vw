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
create or replace view pn_news_view as
select
    nspace.space_id,
    n.news_id,
    n.topic,
    n.message_clob,
    n.priority_id,
    pri_code.code_name as priority_name,
    pri_code.code_desc as priority_description,
    n.notification_id,
    n.posted_by_id,
    n.posted_datetime,
    pposted.display_name as posted_by_full_name,
    n.created_by_id,
    n.created_datetime,
    pcreated.display_name as created_by_full_name,
    n.modified_by_id,
    n.modified_datetime,
    pmodified.display_name as modified_by_full_name,
    n.crc,
    n.record_status
from
    pn_space_has_news nspace,
    pn_news n,
    pn_person pposted,
    pn_person pcreated,
    pn_person pmodified,
    pn_global_domain pri_code
where
    nspace.news_id = n.news_id and
    pposted.person_id(+) = n.posted_by_id and
    pcreated.person_id = n.created_by_id and
    pmodified.person_id(+) = n.modified_by_id and
    pri_code.table_name = 'pn_news' and
    pri_code.column_name = 'priority_id' and
    pri_code.code = n.priority_id
/

