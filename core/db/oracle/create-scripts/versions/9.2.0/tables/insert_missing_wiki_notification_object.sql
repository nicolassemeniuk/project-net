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

-- Script for fixing Notification(-wiki) related issues.
-- Initially wiki wasn't creating pn_object records for each wiki page created and this scirpt is used to correct this for notification issues.
INSERT INTO pn_object po (po.object_id, po.object_type, po.date_created, po.created_by, po.record_status)
       SELECT pw.wiki_page_id, 'wiki', pw.created_date, pw.created_by, pw.record_status
              FROM pn_wiki_page pw
              WHERE pw.wiki_page_id NOT IN (select o.object_id from pn_object o);
