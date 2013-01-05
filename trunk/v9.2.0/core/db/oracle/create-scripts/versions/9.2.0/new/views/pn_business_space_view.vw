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
CREATE OR REPLACE VIEW PN_BUSINESS_SPACE_VIEW AS
SELECT bs.business_space_id, bs.business_id, bs.space_type,
       bs.complete_portfolio_id, bs.record_status,
       b.is_master, b.business_category_id, b.brand_id,
       b.billing_account_id, b.address_id, b.business_name, b.business_desc,
       b.business_type, b.logo_image_id, b.is_local, b.remote_host_id,
       b.remote_business_id, b.num_projects, b.num_people, bs.includes_everyone
  FROM pn_business_space bs, pn_business_view b
 WHERE b.business_id = bs.business_id
/

