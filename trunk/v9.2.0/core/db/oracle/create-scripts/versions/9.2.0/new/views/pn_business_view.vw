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
create or replace view pn_business_view as
select
     BUSINESS_ID,
     RECORD_STATUS,
     IS_MASTER,
     BUSINESS_CATEGORY_ID,
     BRAND_ID,
     BILLING_ACCOUNT_ID,
     ADDRESS_ID,
     BUSINESS_NAME,
     BUSINESS_DESC,
     BUSINESS_TYPE,
     LOGO_IMAGE_ID,
     IS_LOCAL,
     REMOTE_HOST_ID,
     REMOTE_BUSINESS_ID,
     business.getNumProjects (business_id) as num_projects,
     business.getNumPeople (business_id) as num_people
from pn_business
/

