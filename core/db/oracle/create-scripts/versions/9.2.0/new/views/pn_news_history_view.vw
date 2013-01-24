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
create or replace view pn_news_history_view as
select
    n.news_id,
    n.topic,
    nh.news_history_id,
    nh.action,
    nh.action_name,
    nh.action_by_id,
    p.display_name as action_by,
    nh.action_date,
    nh.action_comment
from
    pn_news n, pn_news_history nh,
    pn_person p
where
    n.news_id = nh.news_id
and
    n.record_status = 'A'
and
    nh.action_by_id = p.person_id
/

