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
CREATE OR REPLACE PACKAGE BODY profile IS

/**
 Does not commit or rollback
 Does not RAISE exceptions
 o_status contains status code
*/
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
)

IS

    stored_proc_name VARCHAR2(100):= 'PROFILE.CREATE_PERSON_PROFILE';

    v_address_id pn_address.address_id%TYPE;
    v_portfolio_id pn_portfolio.portfolio_id%TYPE;
    v_group_id pn_group.group_id%TYPE;
    v_doc_space_id pn_doc_space.doc_space_id%TYPE;
    v_doc_container_id pn_doc_container.doc_container_id%TYPE;
    v_calendar_id pn_calendar.calendar_id%TYPE;
    v_module_id pn_module.module_id%TYPE;
    v_space_admin_group_id NUMBER(10);
    v_sysdate DATE;
    v_status NUMBER;

--------------------------------------------
-- added for security
CURSOR  c_module IS
    SELECT module_id FROM pn_module;

BEGIN


    SELECT sysdate INTO v_sysdate FROM dual;

    -- create user's address object
    v_address_id := BASE.CREATE_OBJECT('address', 1, 'A');

    INSERT INTO pn_address
        (address_id, address1, address2, address3, address4, address5, address6, address7, city, city_district, region, state_provence,
         country_code, zipcode, office_phone, fax_phone, record_status)
    VALUES
        (v_address_id, i_address1, i_address2, i_address3, i_address4, i_address5, i_address6, i_address7, i_city, i_city_district,
        i_region, i_state_provence, i_country_code, i_zipcode, i_office_phone, i_fax_phone, 'A');



    -- next create the user's profile
    /*INSERT INTO pn_person_profile
        (person_id, prefix_name, middle_name,
         second_last_name, suffix_name, language_code, date_format_id, timezone_code,
         personal_space_name, verification_code, address_id, alternate_email_1, alternate_email_2, alternate_email_3)
    */
    INSERT INTO pn_person_profile
        (person_id, prefix_name, middle_name,
         second_last_name, suffix_name, locale_code, language_code, timezone_code,
         personal_space_name, verification_code, address_id, alternate_email_1, alternate_email_2, alternate_email_3)
    VALUES
        (i_person_id, i_prefix_name,
         i_middle_name, i_second_last_name, i_suffix_name, i_locale_code,
         i_language_code, i_timezone_code, i_display_name, i_verification_code, v_address_id, i_alternate_email_1, i_alternate_email_2, i_alternate_email_3);


    -- finally update the user master record with the first, last and display name
    UPDATE pn_person
    SET
        first_name = i_first_name,
        last_name = i_last_name,
        display_name = i_display_name
    WHERE
        person_id = i_person_id;


   -- add default directory
    INSERT INTO pn_directory_has_person
        (directory_id, person_id, is_default)
    SELECT directory_id, i_person_id, 1
        FROM pn_directory
        WHERE is_default = 1;
    IF SQL%NOTFOUND THEN
        o_status := no_parent_key;
        RETURN;
    END IF;



    -- create default notification preferences
    -- default to email delivery at their primary email address
    INSERT into pn_person_notification_address
        (person_id, delivery_type_id, delivery_address, is_default)
    values
        (i_person_id, NOTIFICATION.DELIVERY_TYPE_EMAIL, i_email, 1);
-------------------------------------------------------------------------------------------------------
     -- create security defaults

    OPEN c_module;
	<<module_loop>>
	LOOP

		FETCH c_module INTO v_module_id;
		EXIT WHEN c_module%NOTFOUND;

		INSERT INTO pn_space_has_module
        (
			space_id,
		    module_id,
            is_active
        )
		VALUES
        (
			i_person_id,
			v_module_id,
            1
        );

	END LOOP module_loop;
	CLOSE c_module;


    -- SPACE ADMINISTRATOR GROUP
    -- the new person is the space administrator of the new person
    v_space_admin_group_id := SECURITY.F_CREATE_SPACE_ADMIN_GROUP(i_person_id, i_person_id, 'Personal Space Admin');


    -- now create a default doc space for this personal space
    document.create_doc_space (i_person_id, i_person_id,  v_doc_space_id, o_status);

    -- create doc_space object
    v_doc_container_id := BASE.CREATE_OBJECT('doc_container', 1, 'A');

    INSERT INTO pn_doc_container
        (doc_container_id, container_name, container_description, date_modified,
         modified_by_id, is_hidden, crc, record_status)
      VALUES
        (v_doc_container_id, '@prm.document.container.topfolder.name', 'Top level document folder', v_sysdate, 1, 0, v_sysdate, 'A');

    -- create security permissions for new doc container
    SECURITY.CREATE_SECURITY_PERMISSIONS (v_doc_container_id, 'doc_container', i_person_id, 1);

    -- link new doc container to doc space
    INSERT INTO pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
      VALUES
        (v_doc_space_id, v_doc_container_id, 1);



 /*
     -- create doc_space object
    v_doc_space_id := BASE.CREATE_OBJECT('doc_space', 1, 'A');

    INSERT INTO pn_doc_space
        (doc_space_id, doc_space_name, crc, record_status)
      VALUES
        (v_doc_space_id, 'default', v_sysdate, 'A');

    INSERT INTO pn_space_has_doc_space
        (space_id, doc_space_id, is_owner)
      VALUES
        (o_person_id, v_doc_space_id, 1);

    -- link new doc_space to doc provider
    INSERT INTO pn_doc_provider_has_doc_space
        (doc_provider_id, doc_space_id)

      SELECT doc_provider_id, v_doc_space_id
        FROM pn_doc_provider
        WHERE is_default = 1;

    IF SQL%NOTFOUND THEN
        o_status := no_data;
        RETURN;
    END IF;
*/




    -- create calendar defaults
    v_calendar_id := BASE.CREATE_OBJECT('calendar', 1, 'A');

    insert into pn_calendar
        (calendar_id, is_base_calendar, calendar_name, calendar_description, record_status)
    values (v_calendar_id, 1, 'Personal Calendar', 'Main Personal Calendar', 'A');

    SECURITY.CREATE_SECURITY_PERMISSIONS(v_calendar_id, 'calendar', i_person_id, 1);

    -- Link calendar to project space
    insert into pn_space_has_calendar
        (space_id, calendar_id)
    values (i_person_id, v_calendar_id);

    o_status := success;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        o_status := no_data;
        BASE.LOG_ERROR (stored_proc_name, SQLCODE, SQLERRM);

    WHEN e_null_constraint THEN
        o_status := null_field;
        BASE.LOG_ERROR (stored_proc_name, SQLCODE, SQLERRM);

    WHEN e_check_constraint THEN
        o_status := check_violated;
        BASE.LOG_ERROR (stored_proc_name, SQLCODE, SQLERRM);

    WHEN e_unique_constraint THEN
        o_status := dupe_key;
        BASE.LOG_ERROR (stored_proc_name, SQLCODE, SQLERRM);

    WHEN e_no_parent_key THEN
        o_status := no_parent_key;
        BASE.LOG_ERROR (stored_proc_name, SQLCODE, SQLERRM);

    WHEN OTHERS THEN
        o_status := generic_error;
        BASE.LOG_ERROR (stored_proc_name, SQLCODE, SQLERRM);

END create_person_profile;




-------------------------------------------------------------------------
-- Create user stub
-------------------------------------------------------------------------
-- Purpose: creates the pn_person part of a user
--
-- MODIFICATION HISTORY
--  Person      Date       Comments
---------------------------------------------------------------
--  Phil        6/8/01     Creation.

procedure create_person_stub
(
    i_email IN VARCHAR2,
    i_first_name IN VARCHAR2,
    i_last_name IN VARCHAR2,
    i_display_name IN VARCHAR2,
    i_user_status IN VARCHAR2,
    o_person_id OUT NUMBER
) as

    v_timestamp DATE := SYSDATE;
    v_portfolio_id pn_person.membership_portfolio_id%type;

BEGIN

    -- first, create person object
    o_person_id := BASE.CREATE_OBJECT('person', 1, 'A');

    -- now create a portfolio for the user
    v_portfolio_id := BASE.CREATE_OBJECT('portfolio', 1, 'A');

    -- next create person entry
    INSERT INTO pn_person
      (person_id, email, first_name,last_name, display_name,
       user_status, membership_portfolio_id, created_date, record_status)
    VALUES
      (o_person_id, i_email, i_first_name, i_last_name, i_display_name,
       i_user_status, v_portfolio_id, v_timestamp, 'A');

    -- add portfolio stuff
    INSERT INTO pn_portfolio
        (portfolio_id, portfolio_name, portfolio_desc)
      VALUES (v_portfolio_id, 'Personal Portfolio', 'Personal Portfolio');

    -- add the user's personal space to their portfolio
    INSERT INTO pn_space_has_portfolio
        (space_id, portfolio_id, is_default)
      VALUES (o_person_id, v_portfolio_id, 1);

    -- add the user as a member of their own space
    INSERT INTO pn_space_has_person
        (space_id, person_id, relationship_person_to_space, record_status)
      VALUES (o_person_id, o_person_id, 'Person''s Personal Space', 'A');

END;





-------------------------------------------------------------------------
-- Create person
-------------------------------------------------------------------------
-- Purpose: creates the full profile of a user
--
-- MODIFICATION HISTORY
--  Person      Date       Comments
---------------------------------------------------------------
--  Phil        6/8/01     Creation.
--  Tim         1/31/02    Removed all COMMIT and ROLLBACK statements since
--  this is part of a much larger transaction
--  Tim         4/16/02    Removed password, jog question and answer since they are
--                         irrelevant for a person (now stored by directory provider)
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
)

IS

    stored_proc_name VARCHAR2(100):= 'PROFILE.CREATE_PERSON';
    create_person_error exception;

BEGIN

    -- first create person stub entry
    create_person_stub (i_email, i_first_name, i_last_name, i_display_name,
        i_user_status, o_person_id);

    -- next create person profile entry
    create_person_profile (o_person_id, i_first_name, i_last_name, i_display_name, i_username,
        i_email, i_alternate_email_1, i_alternate_email_2, i_alternate_email_3, i_prefix_name, i_middle_name, i_second_last_name,
        i_suffix_name, i_locale_code, i_language_code, i_timezone_code,
        i_verification_code, i_address1, i_address2, i_address3,
        i_address4, i_address5, i_address6, i_address7,i_city,
        i_city_district, i_region, i_state_provence, i_country_code, i_zipcode,
        i_office_phone, i_fax_phone, o_status);

    if o_status <> BASE.OPERATION_SUCCESSFUL then
        raise create_person_error;
    end if;

    -- Success
    o_status := BASE.OPERATION_SUCCESSFUL;

EXCEPTION
    WHEN OTHERS THEN
        o_status := generic_error;
        BASE.LOG_ERROR (stored_proc_name, SQLCODE, SQLERRM);

END create_person;


-------------------------------------------------------------------------
-- CHECK_USER
-------------------------------------------------------------------------
-- Purpose: This function checks if the username and email are being used
--  0: Email and Username are OK
--  0<: Variable already exists in DB
--
--
-- MODIFICATION HISTORY
--  Person      Date       Comments
---------------------------------------------------------------
--  Dan Kelley  03-Mar-00  Creation.
--  Robin       25-Apr-00  Added check_user to package.



PROCEDURE check_user
(
    i_username IN VARCHAR2,
    i_email IN VARCHAR2,
    o_username_status OUT NUMBER,
    o_email_status OUT NUMBER
)

IS

    email_cnt NUMBER(1);
    v_email VARCHAR2(120);
    v_username VARCHAR(120);
    username_cnt NUMBER(1);
    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'PROFILE.CHECK_USER';

BEGIN

    v_email := LOWER(i_email);
    v_username := LOWER(i_username);

    SELECT count(email) INTO email_cnt FROM pn_person WHERE lower(email) = v_email;
    SELECT count(username) INTO username_cnt FROM pn_user_view WHERE lower(username) = v_username;
    o_EMAIL_STATUS := email_cnt;
    o_USERNAME_STATUS := username_cnt;

EXCEPTION
    WHEN OTHERS THEN
        o_USERNAME_STATUS := -1;
        o_EMAIL_STATUS := -1;
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END; -- Procedure CHECK_USER

----------------------------------------------------------------------
-- LOG HISTORY
----------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  --------------------------------------------
-- Robin       10-Apr-00  Creation.

PROCEDURE log_history
(
    i_person_id IN NUMBER,
    i_username IN VARCHAR2,
    o_status OUT NUMBER
)

IS

v_sysdate DATE;
stored_proc_name VARCHAR2(100):= 'PROFILE.LOG_HISTORY';

BEGIN

    SELECT sysdate INTO v_sysdate FROM dual;

    --This might seem strange, but this is being done because we are trying to
    --prevent duplicate login history entries, which would trigger an error
    --from the login history PK.
    INSERT INTO pn_login_history
        (person_id, login_date, login_name_used)
    select
        i_person_id, v_sysdate, i_username
    from DUAL
    where
        NOT EXISTS (select 1
                    from pn_login_history
                    where login_date = v_sysdate and person_id = i_person_id);

    o_status := success;
EXCEPTION

    WHEN e_null_constraint THEN
        o_status := null_field;

    WHEN e_unique_constraint THEN
        --Ignore duplicates.  This just means that the user logged in twice in
        --the same second.  This probably is due to a double-submit on a web
        --browser or something similar.
        o_status := success;

    WHEN e_no_parent_key THEN
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        raise;
END log_history;


----------------------------------------------------------------------
-- STORE_JOB
----------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     --------------------------------------------
-- Robin       10-Apr-00  Creation.


PROCEDURE store_job
(
    i_person_id IN NUMBER,
    i_job_description_code IN NUMBER,
    i_other_job IN VARCHAR2,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'PROFILE.STORE_JOB';
other_indicator CONSTANT NUMBER:= 999;

BEGIN

    UPDATE pn_person_profile
        SET job_description_code = i_job_description_code
        WHERE person_id = i_person_id;

    IF SQL%NOTFOUND THEN
        o_status := no_parent_key;
        ROLLBACK;
        RETURN;
    END IF;

    -- if job description is "other", populate other table
    IF (i_job_description_code = other_indicator) THEN
        INSERT INTO pn_job_description_feedback (other_job_description)
          VALUES (i_other_job);

    END IF;

    COMMIT;
    o_status := success;

EXCEPTION

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END store_job;


----------------------------------------------------------------------
-- STORE_DISCIPLINE
----------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     --------------------------------------------
-- Robin       10-Apr-00  Creation.

PROCEDURE store_discipline
(
    i_person_id IN NUMBER,
    i_discipline_code IN NUMBER,
    i_other_discipline IN VARCHAR2,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'PROFILE.STORE_DISCIPLINE';

BEGIN

         INSERT INTO pn_person_has_discipline
            (person_id, discipline_code, other_discipline)
          VALUES
            (i_person_id, i_discipline_code, i_other_discipline);

EXCEPTION

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_status := dupe_key;

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END store_discipline;

----------------------------------------------------------------------
-- STORE_PROF_CERT
----------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     --------------------------------------------
-- Robin       10-Apr-00  Creation.

PROCEDURE store_prof_cert
(
    i_person_id IN NUMBER,
    i_prof_cert_code IN NUMBER,
    i_other_prof_cert IN VARCHAR2,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'PROFILE.STORE_PROF_CERT';

BEGIN

    INSERT INTO pn_person_has_prof_cert
        (person_id, prof_cert_code, other_prof_cert)
      VALUES
        (i_person_id, i_prof_cert_code, i_other_prof_cert);


    COMMIT;
    o_status := success;

EXCEPTION

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_status := dupe_key;

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END store_prof_cert;

----------------------------------------------------------------------
-- STORE_PERSON_STATE_REG
----------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     --------------------------------------------
-- Robin       10-Apr-00  Creation.


PROCEDURE store_person_state_reg
(
    i_person_id IN NUMBER,
    i_state_code IN NUMBER,
    i_other_state IN VARCHAR2,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'PROFILE.STORE_PERSON_STATE_REG';

BEGIN

    INSERT INTO pn_person_has_state_reg
        (person_id, state_code, other_reg_state)
      VALUES
        (i_person_id, i_state_code, i_other_state);

    COMMIT;
    o_status := success;

EXCEPTION

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_status := dupe_key;

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END store_person_state_reg;

----------------------------------------------------------------------
-- STORE_SURVEY
----------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     --------------------------------------------
-- Robin       10-Apr-00  Creation.
-- Robin       29-May-00  Added referral_page.

PROCEDURE store_survey
(
    i_person_id IN NUMBER,
    i_spam_allowed IN VARCHAR2,
    i_spam_method IN VARCHAR2,
    i_source IN VARCHAR2,
    i_bentley_exp IN VARCHAR2,
    i_referral_page IN VARCHAR2,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'PROFILE.STORE_SURVEY';

BEGIN

    -- do an insert first if that fails, do an update
    INSERT INTO pn_person_survey (person_id, spam_allowed, spam_method, modelvista_source, previous_bentley_exp, referral_page)
      VALUES (i_person_id, i_spam_allowed, i_spam_method, i_source, i_bentley_exp, i_referral_page);

    COMMIT;
    o_status := success;

EXCEPTION

    WHEN e_unique_constraint THEN
        UPDATE pn_person_survey
           SET spam_allowed=i_spam_allowed, spam_method=i_spam_method, modelvista_source=i_source,
               previous_bentley_exp=i_bentley_exp, referral_page=i_referral_page
           WHERE person_id=i_person_id;

        o_status := success;

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_status := no_parent_key;

    WHEN e_null_constraint THEN
        ROLLBACK;
        o_status := null_field;

    WHEN e_check_constraint THEN
        ROLLBACK;
        o_status := check_violated;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END store_survey;

----------------------------------------------------------------------
-- STORE_SPAM
----------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     --------------------------------------------
-- Robin       10-Apr-00  Creation.


PROCEDURE store_spam
(
    i_person_id IN NUMBER,
    i_spam_type_code IN NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'PROFILE.STORE_SPAM';

BEGIN

    INSERT INTO pn_person_picks_spam
        (person_id, spam_type_code)
      VALUES
        (i_person_id, i_spam_type_code);


    COMMIT;
    o_status := success;

EXCEPTION

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_status := dupe_key;

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END store_spam;


----------------------------------------------------------------------
-- CONFIRM_PERSON
----------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     --------------------------------------------
-- Robin       12-Apr-00  Creation.


PROCEDURE confirm_person
(
    i_email IN VARCHAR2,
    i_verification_code IN VARCHAR2,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'PROFILE.CONFIRM_PERSON';
v_person_id pn_person.person_id%type;

BEGIN

    select person_id into v_person_id from pn_person where email = i_email;

    UPDATE pn_person
      SET user_status = 'Active'
      WHERE email = i_email
      AND i_verification_code =
        (select verification_code from pn_person_profile where person_id = v_person_id);

    IF SQL%NOTFOUND THEN
        o_status := no_data;
        ROLLBACK;
        RETURN;
    END IF;

    COMMIT;
    o_status := success;

EXCEPTION

    WHEN e_check_constraint THEN
        ROLLBACK;
        o_status := check_violated;

    WHEN e_null_constraint THEN
        ROLLBACK;
        o_status := null_field;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END confirm_person;

----------------------------------------------------------------------
-- CREATE_DOC_DEFAULT
----------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     --------------------------------------------
-- Robin       12-Apr-00  Creation.


PROCEDURE create_doc_default
(
    i_person_id IN NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'PROFILE.CREATE_DOC_DEFAULT';
v_sysdate DATE;
v_doc_space_id pn_doc_space.doc_space_id%TYPE;
v_doc_container_id pn_doc_container.doc_container_id%TYPE;

BEGIN

    SELECT sysdate INTO v_sysdate FROM dual;

    -- create doc_space object
    v_doc_space_id := BASE.CREATE_OBJECT('doc_space', 1, 'A');

    INSERT INTO pn_doc_space
        (doc_space_id, doc_space_name, crc, record_status)
      VALUES
        (v_doc_space_id, 'default', v_sysdate, 'A');

    INSERT INTO pn_space_has_doc_space
        (space_id, doc_space_id, is_owner)
      VALUES
        (i_person_id, v_doc_space_id, 1);

    -- link new doc_space to doc provider
    INSERT INTO pn_doc_provider_has_doc_space
        (doc_provider_id, doc_space_id)

      SELECT doc_provider_id, v_doc_space_id
        FROM pn_doc_provider
        WHERE is_default = 1;

    IF SQL%NOTFOUND THEN
        o_status := no_data;
        ROLLBACK;
        RETURN;
    END IF;

    -- create doc_space object
    v_doc_container_id := BASE.CREATE_OBJECT('doc_container', 1, 'A');

    INSERT INTO pn_doc_container
        (doc_container_id, container_name, container_description, date_modified,
         modified_by_id, is_hidden, crc, record_status)
      VALUES
        (v_doc_container_id, '@prm.document.container.topfolder.name', 'Top level document folder', v_sysdate, 1, 0, v_sysdate, 'A');

    -- create security permissions for new doc container
    SECURITY.CREATE_SECURITY_PERMISSIONS (v_doc_container_id, 'doc_container', i_person_id, 1);

    -- link new doc container to doc space
    INSERT INTO pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
      VALUES
        (v_doc_space_id, v_doc_container_id, 1);


    COMMIT;
    o_status := success;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        o_status := no_data;

    WHEN e_null_constraint THEN
        ROLLBACK;
        o_status := null_field;

    WHEN e_check_constraint THEN
        ROLLBACK;
        o_status := check_violated;

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_status := dupe_key;

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END create_doc_default;


function getLastLoginDate
( i_username in pn_user.username%type)
return date
as
    v_last_login_date DATE;

begin

    select login_date into v_last_login_date from pn_login_history
        where login_name_used = i_username
        and login_date =
            (select max(login_date) from pn_login_history where login_name_used = i_username);

    return v_last_login_date;

exception
    WHEN OTHERS THEN
        v_last_login_date := null;
        return v_last_login_date;
end;

----------------------------------------------------------------------
-- GET_DISPLAY_NAME
----------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     --------------------------------------------


FUNCTION get_display_name
( i_person_id IN pn_person.person_id%type )
RETURN pn_person.display_name%type
AS

    v_display_name pn_person.display_name%type;

BEGIN

    select display_name into v_display_name from pn_person where person_id = i_person_id;

    return v_display_name;
END;

----------------------------------------------------------------------
-- REMOVE_PERSON_FROM_SPACE
----------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     --------------------------------------------
-- Robin       04-May-00  Creation.
-- Robin       11-May-00  Changed project_id to space_id in invited_users.
-- Robin       25-May-00  Moved assignment delete to top b/c of integrity constraint.

PROCEDURE remove_person_from_space
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'PROFILE.REMOVE_PERSON_FROM_SPACE';
v_principal_group pn_group.group_id%TYPE;
v_status NUMBER;

BEGIN

    LOCK TABLE pn_space_has_group IN SHARE MODE;
    LOCK TABLE pn_group_has_person IN SHARE MODE;
    LOCK TABLE pn_person IN SHARE MODE;

    -- delete any assigments for person/space
    -- 10/10/01 - Tim - Note that in the assignments table, the space to
    -- which the assignment pertains is actually in the OBJECT_ID column.
    -- 11/14/01 - Tim - Update to previous comment.

    -- The situatation is this:
    -- For the assignment that is the invitation to the space, the assignments
    -- table looks like this:
    --     space_id             object_id   person_id
    --     <personal space id>  <space id>  <person id>

    -- For all other assignments in the space, the table looks like this
    --     space_id    object_id    person_id
    --     <space id>  <object id>  <person id>

    -- Therefore, we must delete all assignments where the object_id is the
    -- space id OR the space_id is the space id
    DELETE FROM pn_assignment
        WHERE
            person_id = i_person_id
        and (object_id = i_space_id or space_id = i_space_id);

    -- delete person from space
    DELETE FROM pn_space_has_person
        WHERE space_id = i_space_id
          AND person_id = i_person_id;

    -- delete the space from person's Team Member portfolio
    DELETE FROM pn_portfolio_has_space
        WHERE space_id = i_space_id
          AND portfolio_id = (SELECT membership_portfolio_id FROM pn_person
                              WHERE person_id = i_person_id);


    -- get person's principal group for space
    -- they may not yet have a principal group, if there is an unaccpeted
    -- invitation for them
    begin
        select g.group_id INTO v_principal_group
          from pn_space_has_group sg, pn_group g
         where g.is_principal = 1
           and g.principal_owner_id = i_person_id
           and g.group_id = sg.group_id
           and sg.space_id = i_space_id;
    exception
        when NO_DATA_FOUND then
            -- If no data, then do nothing
            null;
    end;

    -- delete person from all groups within the space where those groups
    -- are OWNED by the space
    -- VERY important to only delete from owned groups; otherwise we could
    -- end up removing them from groups inherited from other spaces
    DELETE FROM pn_group_has_person
        WHERE person_id = i_person_id
          AND group_id IN (SELECT gp.group_id FROM pn_group_has_person gp, pn_space_has_group sg
                            WHERE
                                  sg.is_owner = 1
                              and gp.group_id = sg.group_id
                              AND sg.space_id = i_space_id
                              AND gp.person_id = i_person_id);

    if (v_principal_group is not null) then
        -- delete all permissions for this group
        SECURITY.REMOVE_GROUP_PERMISSION (v_principal_group, v_status);

        -- delete the principal group for the space/person
        -- This has been changed to a soft delete to avoid blowing up notificaiton
        -- and workflows (PCD: 8/20/2001)
        /*
        DELETE FROM pn_group
            WHERE group_id = v_principal_group;
        */
        update pn_group set record_status = 'D' where group_id = v_principal_group;
    end if;

    -- update invited users status to deleted
    UPDATE pn_invited_users
       SET invited_status = 'Deleted'
       WHERE space_id  = i_space_id and person_id = i_person_id ;
        -- 10/09/01 - Tim - Removed condition;  this will cause invitations
        -- still in "invited" state to be deleted also.  In fact, any invitation
        -- becomes deleted.  This is as expected:  If a person is removed from
        -- a space, we are essentially revoking their invitation
        --                          AND i.invited_status = 'Accepted';


    COMMIT;
    o_status := success;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        o_status := no_data;

    WHEN e_null_constraint THEN
        ROLLBACK;
        o_status := null_field;

    WHEN e_check_constraint THEN
        ROLLBACK;
        o_status := check_violated;

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_status := dupe_key;

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

 END;


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
    o_address_id OUT NUMBER)

AS
    v_creator_id    pn_person.person_id%type := to_number(p_creator);
      err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'CREATE_ADDRESS';

BEGIN
 -- Get new object_id (address_id) from the Object Sequence Generator
 SELECT pn_object_sequence.nextval into o_address_id FROM dual;

 -- Create the object
 INSERT INTO pn_object
        (object_id, object_type, date_created, created_by, record_status)
        VALUES
        (o_address_id, 'address', SYSDATE, v_creator_id, 'A');

-- Create the Address
 INSERT INTO pn_address
        (address_id, address1, address2, address3, address4, address5, address6, address7, city, state_provence, zipcode, country_code,
         office_phone, home_phone, mobile_phone, fax_phone, pager_phone, pager_email, website_url, record_status)
        VALUES
        (o_address_id, p_address1, p_address2, p_address3, p_address4, p_address5, p_address6, p_address7, p_city, p_state_provence, p_zipcode, p_country_code,
         p_office_phone, p_home_phone, p_mobile_phone, p_fax_phone, p_pager_phone, p_pager_email, p_website_url, 'A');

         EXCEPTION

WHEN OTHERS THEN
      ROLLBACK;
      err_num:=SQLCODE;
      err_msg:=SUBSTR(SQLERRM,1,120);
      INSERT INTO pn_sp_error_log
         VALUES (sysdate,stored_proc_name,err_num,err_msg);
      COMMIT;

END;

----------------------------------------------------------------------

----------------------------------------------------------------------
-- LOGS_EVENT
----------------------------------------------------------------------
 PROCEDURE log_event
    (
        group_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    )

IS
    v_group_id     pn_group.group_id%type := TO_NUMBER(group_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_group_history.group_history_id%type;
    v_action          pn_group_history.action%type := action;
    v_action_name     pn_group_history.action_name%type := action_name;
    v_action_comment  pn_group_history.action_comment%type := notes;

BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- insert a history record for this document

    insert into pn_group_history (
        group_id,
        group_history_id,
        action,
        action_name,
        action_by_id,
        action_date,
        action_comment)
    values (
        v_group_id,
        v_history_id,
        v_action,
        v_action_name,
        v_whoami,
        SYSDATE,
        v_action_comment
    );

COMMIT;
EXCEPTION
    WHEN OTHERS THEN
    BEGIN
         base.log_error('PROFILE.LOG_EVENT', sqlcode, sqlerrm);
        raise;
    END;
END log_event;

-- Procedure LOG_EVENT
----------------------------------------------------------------------
END; -- Package Body PROFILE
/

