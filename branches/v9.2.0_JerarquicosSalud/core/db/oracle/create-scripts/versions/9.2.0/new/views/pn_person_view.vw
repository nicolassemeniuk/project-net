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
create or replace view pn_person_view
(person_id, email, image_id, alternate_email_1, alternate_email_2, alternate_email_3, skype, skills_bio, first_name, last_name, display_name, prefix_name, middle_name, second_last_name, suffix_name, user_status, record_status, address_id, company_name, company_division, job_description_code, locale_code, language_code, timezone_code, membership_portfolio_id, personal_space_name, verification_code, has_license)
as
select
    p.person_id,
    p.email,
    p.image_id,
    pp.alternate_email_1,
    pp.alternate_email_2,
    pp.alternate_email_3,
    pp.skype,
    pp.skills_bio,
    p.first_name,
    p.last_name,
    p.display_name,
    pp.prefix_name,
    pp.middle_name,
    pp.second_last_name,
    pp.suffix_name,
    p.user_status,
    p.record_status,
    pp.address_id,
    pp.company_name,
    pp.company_division,
    pp.job_description_code,
    pp.locale_code,
    pp.language_code,
    pp.timezone_code,
    p.membership_portfolio_id,
    decode(pp.personal_space_name,null,p.display_name,pp.personal_space_name) ,
    pp.verification_code,
    case when phl.person_id is null then 0 else 1 end as has_license
from
    pn_person p, pn_person_profile pp, pn_person_has_license phl
where
    p.person_id = pp.person_id (+)
and phl.person_id(+) = p.person_id
/

