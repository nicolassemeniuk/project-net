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
CREATE OR REPLACE VIEW PN_USER_DOMAIN_VIEW AS
SELECT d.domain_id, d.NAME, d.description, d.directory_provider_type_id,
       dpt.NAME AS directory_provider_type_name, d.record_status,
       user_domain.getcountusersfordomain (d.domain_id) AS user_count,
       d.registration_instructions_clob, d.is_verification_required,
       d.supports_credit_card_purchases
  FROM pn_user_domain d, pn_directory_provider_type dpt
 WHERE dpt.provider_type_id = d.directory_provider_type_id
/

