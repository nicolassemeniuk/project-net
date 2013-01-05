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
create or replace view pn_class_inst_active_cnt_view as
select c.class_id, decode(active_count.active_count, null, 0, active_count.active_count) as active_count
from
    pn_class c,
(select ci.class_id, count(*) as active_count
from
    pn_class_instance ci
where
    ci.record_status = 'A'
group by
    ci.class_id) active_count
where
    active_count.class_id(+) = c.class_id
/

