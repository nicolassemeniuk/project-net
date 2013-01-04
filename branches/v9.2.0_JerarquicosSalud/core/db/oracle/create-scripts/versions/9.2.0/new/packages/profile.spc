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
CREATE OR REPLACE PACKAGE profile IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Robin       06-Apr-00  Creation from Brian's procs.
-- Brian       30-Apr-00  Added a remove procedure
-- Robin       11-May-00  Changed error codes to coincide with new table.
-- Tim         06-Mar-01  Migrated Phil's changes from salesmode for new address stuff
-- Vish        10-Sep-02  Tokenized "Top folder" string
--Vish         31-Oct-02  Modified create_person procedure

-- global variables and exceptions

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

err_num NUMBER;
err_msg VARCHAR2(120);

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

PROCEDURE create_person
(
    i_username IN VARCHAR2,
    i_email IN VARCHAR2,
    i_alternate_email_1 IN VARCHAR2,
    i_alternate_email_2 IN VARCHAR2,
    i_alternate_email_3 IN VARCHAR2,
    i_prefix_name IN VARCHAR2,
    i_first_name IN VARCHAR2,
    i_middle_name IN VARCHAR2,
    i_last_name IN VARCHAR2,
    i_second_last_name IN VARCHAR2,
    i_suffix_name IN VARCHAR2,
    i_display_name IN VARCHAR2,
    i_locale_code IN VARCHAR2,
    i_language_code IN VARCHAR2,
    i_timezone_code IN VARCHAR2,
   -- i_date_format_id IN NUMBER,
    i_verification_code IN VARCHAR2,
    i_address1 IN VARCHAR2,
    i_address2 IN VARCHAR2,
    i_address3 IN VARCHAR2,
    i_address4 IN VARCHAR2,
    i_address5 IN VARCHAR2,
    i_address6 IN VARCHAR2,
    i_address7 IN VARCHAR2,
    i_city IN VARCHAR2,
    i_city_district IN VARCHAR2,
    i_region IN VARCHAR2,
    i_state_provence IN VARCHAR2,
    i_country_code IN VARCHAR2,
    i_zipcode IN VARCHAR2,
    i_office_phone IN VARCHAR2,
    i_fax_phone IN VARCHAR2,
    i_user_status in varchar2,
    o_person_id OUT NUMBER,
    o_status OUT NUMBER
);

procedure create_person_stub
(
    i_email IN VARCHAR2,
    i_first_name IN VARCHAR2,
    i_last_name IN VARCHAR2,
    i_display_name IN VARCHAR2,
    i_user_status IN VARCHAR2,
    o_person_id OUT NUMBER
);


PROCEDURE create_person_profile
(
    i_person_id IN NUMBER,
    i_first_name IN VARCHAR2,
    i_last_name IN VARCHAR2,
    i_display_name IN VARCHAR2,
    i_username IN VARCHAR2,
    i_email in varchar2,
    i_alternate_email_1 in varchar2,
    i_alternate_email_2 in varchar2,
    i_alternate_email_3 in varchar2,
    i_prefix_name IN VARCHAR2,
    i_middle_name IN VARCHAR2,
    i_second_last_name IN VARCHAR2,
    i_suffix_name IN VARCHAR2,
    i_locale_code IN VARCHAR2,
    i_language_code IN VARCHAR2,
    i_timezone_code IN VARCHAR2,
--    i_date_format_id IN NUMBER,
    i_verification_code IN VARCHAR2,
    i_address1 IN VARCHAR2,
    i_address2 IN VARCHAR2,
    i_address3 IN VARCHAR2,
    i_address4 IN VARCHAR2,
    i_address5 IN VARCHAR2,
    i_address6 IN VARCHAR2,
    i_address7 IN VARCHAR2,
    i_city IN VARCHAR2,
    i_city_district IN VARCHAR2,
    i_region IN VARCHAR2,
    i_state_provence IN VARCHAR2,
    i_country_code IN VARCHAR2,
    i_zipcode IN VARCHAR2,
    i_office_phone IN VARCHAR2,
    i_fax_phone IN VARCHAR2,
    o_status OUT NUMBER
);

-- check_user

PROCEDURE check_user
(
    i_username IN VARCHAR2,
    i_email IN VARCHAR2,
    o_username_status OUT NUMBER,
    o_email_status OUT NUMBER
);

-- log_history

PROCEDURE log_history
(
    i_person_id IN NUMBER,
    i_username IN VARCHAR2,
    o_status OUT NUMBER
);


-- create profile

PROCEDURE store_job
(
    i_person_id IN NUMBER,
    i_job_description_code IN NUMBER,
    i_other_job IN VARCHAR2,
    o_status OUT NUMBER
);

PROCEDURE store_discipline
(
    i_person_id IN NUMBER,
    i_discipline_code IN NUMBER,
    i_other_discipline IN VARCHAR2,
    o_status OUT NUMBER
);

PROCEDURE store_prof_cert
(
    i_person_id IN NUMBER,
    i_prof_cert_code IN NUMBER,
    i_other_prof_cert IN VARCHAR2,
    o_status OUT NUMBER
);

PROCEDURE store_person_state_reg
(
    i_person_id IN NUMBER,
    i_state_code IN NUMBER,
    i_other_state IN VARCHAR2,
    o_status OUT NUMBER
);

PROCEDURE store_survey
(
    i_person_id IN NUMBER,
    i_spam_allowed IN VARCHAR2,
    i_spam_method IN VARCHAR2,
    i_source IN VARCHAR2,
    i_bentley_exp IN VARCHAR2,
    i_referral_page IN VARCHAR2,
    o_status OUT NUMBER
);

PROCEDURE store_spam
(
    i_person_id IN NUMBER,
    i_spam_type_code IN NUMBER,
    o_status OUT NUMBER
);

PROCEDURE confirm_person
(
    i_email IN VARCHAR2,
    i_verification_code IN VARCHAR2,
    o_status OUT NUMBER
);

FUNCTION get_display_name
(
    i_person_id IN pn_person.person_id%type
)
    RETURN pn_person.display_name%type;


function getLastLoginDate
(
    i_username in pn_user.username%type
) return date;

PROCEDURE remove_person_from_space
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    o_status OUT NUMBER
);

PROCEDURE      create_address (
    p_creator IN VARCHAR2,
    p_address1 IN VARCHAR2,
    p_address2 IN VARCHAR2,
    p_address3 IN VARCHAR2,
    p_city IN VARCHAR2,
    p_state_provence IN VARCHAR2,
    p_zipcode IN VARCHAR2,
    p_country_code IN VARCHAR2,
    p_office_phone IN VARCHAR2,
    p_home_phone IN VARCHAR2,
    p_mobile_phone IN VARCHAR2,
    p_fax_phone IN VARCHAR2,
    p_pager_phone IN VARCHAR2,
    p_pager_email IN VARCHAR2,
    p_website_url IN VARCHAR2,
    p_address4 IN VARCHAR2,
    p_address5 IN VARCHAR2,
    p_address6 IN VARCHAR2,
    p_address7 IN VARCHAR2,
    o_address_id OUT NUMBER);

PROCEDURE log_event
    (
        group_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    );

END; -- Package Specification PROFILE
/

