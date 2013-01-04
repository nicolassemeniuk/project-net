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
CREATE OR REPLACE PACKAGE BODY application IS

--------------------------------------------------------------------
-- INVITE_PERSON_TO_APPLICATION
--------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Deepak      14-June-02 Removed the redundant procedure



---------------------------------------------------------------------
-- CREATE_APPLICATION
-- NO COMMIT OR ROLLBACK IS PERFORMED
---------------------------------------------------------------------
PROCEDURE create_application (
    i_application_id    in number,
    i_application_name  IN VARCHAR2,
    i_application_desc  IN VARCHAR2,
    i_creator_id        IN NUMBER
) is
    v_status NUMBER := 0;
    v_group_id NUMBER(20) :=0;
    v_portfolio_id NUMBER(20) :=0;
    v_directory_id NUMBER(20) :=0;
    v_power_user_group_id NUMBER(20) :=0;

/* These not needed due to commented out code below.
    v_methodology_id NUMBER(20) :=0;
    v_doc_provider_id NUMBER(20) := 0;
    v_doc_space_id NUMBER(20) := 0;
    v_doc_container_id NUMBER(20) := 0;
    v_system_container_id NUMBER(20) := 0;
    v_calendar_id NUMBER(20) :=0;
    v_global_domain  pn_global_domain%rowtype;
*/

BEGIN
    -- We assume that the PN_OBJECT record is created for specified
    -- i_application_id

    -- Create the new application space record
    insert into pn_application_space
	    (application_id, application_name, application_desc, created_by_id,
        created_datetime, crc, record_status)
    values
	    (i_application_id, i_application_name, i_application_desc, i_creator_id,
        sysdate, sysdate, BASE.ACTIVE_RECORD_STATUS);



    /*
        Now configure security
     */

    -- Specify the modules to which the new space has access
    insert into pn_space_has_module
        (space_id, module_id, is_active)
        select
            i_application_id, module_id, '1'
        from
            pn_module
        where
            module_id in (30, 140, 240, 250);

    -- SPACE ADMINISTRATOR GROUP
    -- The space creator is the initial space administrator
    v_status := security.F_CREATE_SPACE_ADMIN_GROUP(i_creator_id, i_application_id, APP_SPACE_ADMIN_GROUP_DESC);

    -- PRINCIPAL GROUP
    -- The space creator (person) must be put into a principal group for this space.
    v_group_id := NULL;
    SECURITY.CREATE_PRINCIPAL_GROUP(i_creator_id, i_application_id, v_group_id);

    -- add space creator (person) to their principal group
    v_status := NULL;
    SECURITY.ADD_PERSON_TO_GROUP(v_group_id, i_creator_id, v_status);

    -- TEAM MEMBER GROUP
    -- The creator is the only initial team member
    v_group_id := NULL;
    SECURITY.CREATE_TEAMMEMBER_GROUP(i_creator_id, i_application_id, v_group_id);

    v_status := NULL;
    SECURITY.ADD_PERSON_TO_GROUP(v_group_id, i_creator_id, v_status);


    -- POWER USER GROUP
    --  CREATE POWER USER GROUP
    v_power_user_group_id := security.F_CREATE_POWER_USER_GROUP(i_creator_id, i_application_id, '@prm.security.group.type.poweruser.description');

    /*
        End of space security
     */


     /*
        Application space portfolios
      */

    -- Create a default "owner_portfolio" for this application space
    -- This portfolio is maintained by the system and contains all the configuration spaces
    -- OWNED DIRECTLY by this Application Space.

    -- Create portfolio object_id
    v_portfolio_id := BASE.CREATE_OBJECT('portfolio', i_creator_id, 'A');

    INSERT INTO pn_portfolio
        (portfolio_id, portfolio_name, portfolio_desc)
        VALUES
        (v_portfolio_id, 'owner', 'Configurations owned by this application space');

    -- Now indicate that this portfolio is the default for the space
    insert into pn_space_has_portfolio
    	(space_id, portfolio_id, is_default)
    values
	    (i_application_id, v_portfolio_id, 1);

    /*
        03/23/01 - Tim
        This stuff was copied from business.
        I don't think the Application Space needs both an "owner" and "visible"
        portfolio.

    -- Create a default "complete_portfolio" for this application space
    -- This portfolio is maintained by the system and contains all configuration spaces.
    -- Create portfolio object_id
    v_portfolio_id := BASE.CREATE_OBJECT('portfolio', i_creator_id, 'A');

    INSERT INTO pn_portfolio
        (portfolio_id, portfolio_name, portfolio_desc)
        VALUES
        (v_portfolio_id, 'visible', 'All Configurations visible by this application space');

    INSERT INTO pn_space_has_portfolio
        (space_id, portfolio_id, is_default)
        VALUES
        (i_application_id, v_portfolio_id, 1);     -- Set this to be the Default portfolio.

    UPDATE pn_business_space SET complete_portfolio_id = v_portfolio_id where business_id = i_application_id;
    */

    -- Add the creator as the first member of the business directory
    insert into pn_space_has_person
        (space_id, person_id, relationship_person_to_space, record_status)
        values
        (i_application_id, i_creator_id, 'member', 'A');

    -- Every application space gets the default directory
    select directory_id into v_directory_id from pn_directory where is_default = 1;

    insert into pn_space_has_directory
        (directory_id, space_id)
    values
        (v_directory_id, i_application_id);


    /*
        03/23/01 - Tim

        Copied from Business.  We're not supporting document spaces
        in application space yet due to time constraints.

    /* CREATE DOCUMENT SPACES FOR BUSINESS
       Add the default document provider to this business space.  The user can add more
       document providers later.  All projects created under this business space will
       inherite the busines space's doc providers

       Each business space get it's own doc space created for the defualt doc provider
       TODO:  Set top-level container for the project's doc space.
    * /

    -- Get the doc_providers available to this project (defined by business space)
    -- TODO:  loop to get all doc_providers.  Just getting the default one now.
    -- Get a new project_id
    SELECT
        doc_provider_id into v_doc_provider_id
    FROM
        pn_doc_provider
    WHERE
        doc_provider_type_id = 100;  -- 'Internal Database Vault'

    -- Add this doc_provider to the business so that it's projects will inherit
    insert into pn_space_has_doc_provider
        (space_id, doc_provider_id, is_default)
    values
        (o_business_id, v_doc_provider_id, 1);


    -- new doc space for this business
    v_doc_space_id := BASE.CREATE_OBJECT('doc_space', i_creator_id, 'A');

    insert into pn_doc_space
        (doc_space_id, doc_space_name, record_status, crc)
    values
        (v_doc_space_id, 'default', 'A', SYSDATE);


    -- link new doc_space back to it's doc_provider
    insert into pn_doc_provider_has_doc_space
        (doc_provider_id, doc_space_id)
    values
        (v_doc_provider_id, v_doc_space_id);


    -- this business owns the doc space
    insert into pn_space_has_doc_space
      (space_id, doc_space_id)
    values
      (o_business_id, v_doc_space_id);

    v_doc_container_id := BASE.CREATE_OBJECT('doc_container', i_creator_id, 'A');

    insert into pn_doc_container
        (doc_container_id, container_name, container_description, date_modified, modified_by_id, record_status, crc)
    values
        (v_doc_container_id, 'Top folder', 'Top level document folder', SYSDATE, i_creator_id, 'A', SYSDATE);

    SECURITY.CREATE_SECURITY_PERMISSIONS(v_doc_container_id, 'doc_container', o_business_id, i_creator_id);

    -- Link container (top folder) to doc space
    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_doc_container_id, 1);


    v_system_container_id := BASE.CREATE_OBJECT('doc_container', i_creator_id, 'A');

    -- SYSTEM container
    insert into pn_doc_container
        (doc_container_id, container_name, container_description, modified_by_id, date_modified,record_status, is_hidden, crc)
    values
        (v_system_container_id, o_business_id, 'System container for this space',i_creator_id, SYSDATE,'A', 1, SYSDATE);

    SECURITY.CREATE_SECURITY_PERMISSIONS(v_system_container_id, 'doc_container', o_business_id, i_creator_id);

    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_system_container_id, 0);

   insert into pn_doc_container_has_object
        (doc_container_id, object_id)
    values
        (v_doc_container_id, v_system_container_id);
    */


    /*
        03/23/01 - Tim

        Copied from Business.  We're not supporting calendar
        in application space yet due to time constraints.

     /*
        BUSINESS CALENDAR
      * /
    v_calendar_id := BASE.CREATE_OBJECT('calendar', i_creator_id, 'A');

    insert into pn_calendar
        (calendar_id, calendar_name, calendar_description, record_status)
    values (v_calendar_id, 'Business Calendar', 'Main Business Calendar', 'A');

    -- Link calendar to project space
    insert into pn_space_has_calendar
        (space_id, calendar_id)
    values (o_business_id, v_calendar_id);
    */



    /*
        03/23/01 - Tim

        Copied from Business.  Don't think this stuff applies.

    /* COPY SYSTEM DOMAINS TO BUSINESS' CUSTOM DOMAINS
      The business can modify and add to these domains.
      The business' projects will inheriate these domains.
    * /

    -- pn_person_has_skill
    -- proficiency_code
    insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_person_has_skill' and
        column_name = 'proficiency_code';

    -- pn_post
    -- urgency_id
    insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_post' and
        column_name = 'urgency_id';

    -- pn_deliverable
    -- deliverable_type_id
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_deliverable' and
        column_name = 'deliverable_type_id';


    -- pn_process_phase
    -- status_id
    insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_process_phase' and
        column_name = 'status_id';

    -- pn_process_gate
    -- status_id
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_process_gate' and
        column_name = 'status_id';

    -- pn_process_step
    -- status_id
  insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_process_step' and
        column_name = 'status_id';

    -- pn_deliverable
    -- status_id
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_deliverable' and
        column_name = 'status_id';

    -- pn_task
    -- status_id
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_task' and
        column_name = 'status_id';

    -- pn_task
    -- priority
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_task' and
        column_name = 'priority';

    -- pn_resource_assignment
    -- status_id
    insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_resource_assignment' and
        column_name = 'status_id';

    -- pn_calendar_event
    -- frequency_type_id
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_calendar_event' and
        column_name = 'frequency_type_id';

    -- pn_calendar_event
    -- event_type_id
  insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_calendar_event' and
        column_name = 'event_type_id';

    -- pn_agenda_item
    -- status_id
  insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_agenda_item' and
        column_name = 'status_id';
    */

END CREATE_APPLICATION;


--------------------------------------------------------------------
-- RESPOND_TO_APPLICATION_INVITE
-- NO COMMIT OR ROLLBACK IS PERFORMED
--------------------------------------------------------------------
procedure respond_to_application_invite (
    i_invitation_code in number,
    i_application_id in number,
    i_person_id in number,
    i_response in varchar2,
    o_status out number
) is

    stored_proc_name VARCHAR2(100):= 'APPLICATION.RESPOND_TO_APPLICATION_INVITE';
    v_sysdate DATE;
    v_status NUMBER;
    v_group_id pn_group.group_id%TYPE;

begin

    select sysdate into v_sysdate from dual;

    IF (i_response = 'Accepted') THEN

        -- Add person to space
        insert into pn_space_has_person
            (space_id, person_id, relationship_person_to_space, relationship_space_to_person,
             responsibilities, record_status)
            select
               i_application_id, i_person_id, 'member', 'has',
               invitee_responsibilities, BASE.ACTIVE_RECORD_STATUS
            from
               pn_invited_users
            where
               invitation_code = i_invitation_code;

        -- Add person to team member group
        insert into pn_group_has_person
            (group_id, person_id)
            select
               g.group_id, i_person_id
            from
               pn_group g,
               pn_space_has_group sg
            where
               space_id = i_application_id
            and g.group_type_id = SECURITY.GROUP_TYPE_TEAMMEMBER
            and g.group_id = sg.group_id;

        -- Create principal group
        v_group_id := NULL;
        security.create_principal_group(i_person_id, i_application_id, v_group_id);

        -- Add person to principal group
        v_status := NULL;
        security.add_person_to_group(v_group_id, i_person_id, v_status);

    end if;

   -- Now update invitation, setting to Accepted or Rejected
   update pn_invited_users
   set
      date_responded = v_sysdate,
      invited_status = i_response
   where
      invitation_code = i_invitation_code
   and space_id = i_application_id and person_id = i_person_id ;

   o_status := BASE.OPERATION_SUCCESSFUL;

EXCEPTION
    when others then
    BEGIN
        o_status := BASE.PLSQL_EXCEPTION;
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
    END;
END respond_to_application_invite;



FUNCTION getNewUserTrendChangeMetric
RETURN NUMBER

AS
    v_trend_change NUMBER := 0;
    v_month_three_average NUMBER := 0;
    v_month_two_average NUMBER := 0;
    v_month_one_average NUMBER := 0;

BEGIN

    select count (person_id) into v_month_three_average from pn_person
    where created_date >= add_months (trunc(sysdate, 'MM'), -3)
    and created_date < add_months (trunc(sysdate,'MM'), -2);

    select count (person_id) into v_month_two_average from pn_person
    where created_date >= add_months (trunc(sysdate, 'MM'), -2)
    and created_date < add_months (trunc(sysdate,'MM'), -1);

    select count (person_id) into v_month_one_average from pn_person
    where created_date >= add_months (trunc(sysdate, 'MM'), -1)
    and created_date < trunc(sysdate,'MM');

    v_trend_change := ((v_month_two_average - v_month_three_average) +
        (v_month_one_average - v_month_two_average)) / 2;


    return v_trend_change;

END;


FUNCTION getAverageUserLoginsPerDay
RETURN NUMBER

AS

    v_day_counter NUMBER := 0;

    -- This inits to the date approx. 90 days before today
    v_temp_date DATE := add_months (sysdate, -3);

    v_login_today NUMBER := 0;
    v_temp_total NUMBER := 0;
    v_average_logins NUMBER := 0;

BEGIN

    -- we don't want to include today's count as the day may not be complete
    -- this will loop until we get through all the days.
    while (v_temp_date < sysdate) LOOP

        select count(distinct(person_id)) into v_login_today
        from pn_login_history
        where trunc (login_date, 'DDD') = trunc (v_temp_date, 'DDD');

        -- add today's count to the total
        v_temp_total := v_temp_total + v_login_today;
        -- increment the date
        v_temp_date := v_temp_date + 1;
        -- increment the day counter
        v_day_counter := v_day_counter + 1;

    END LOOP;

    v_average_logins := v_temp_total / v_day_counter;

    return v_average_logins;

END;




/*
---------------------------------------------------------------------
-- UPDATE_BUSINESS
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Robin       11-May-00  Created from existing procedure.
-- Robin       08-Jun-00  Added business_type.
PROCEDURE UPDATE_BUSINESS
    (
        i_business_space_id IN NUMBER,
        i_business_name IN VARCHAR2,
        i_business_desc IN VARCHAR2,
        i_business_type IN VARCHAR2,
        o_status OUT NUMBER
    )
    IS
        v_business_id NUMBER(20) := 0;

    BEGIN
        select  business_id
            into    v_business_id
            from    pn_business_space
            where   business_space_id = i_business_space_id;

        update  pn_business
            set     business_name = i_business_name,
                    business_desc = i_business_desc,
                    business_type = i_business_type
            where   business_id = v_business_id;


    END;

/*
procedure add_logo
(
    business_id IN number,
    logo_id IN number
)

as

BEGIN

    update pn_business
        set logo_image_id = logo_id
    where business_id = business_id;

COMMIT;

END;


*/

END; -- Package Body APPLICATION
/

CREATE OR REPLACE PACKAGE application IS
/*
    Application package provides functions and procedures
    for managing application spaces.
 */
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Tim         22-Mar-01  Cloned from business package.

-- Description of application space administrator group
-- This is the entire application's administrator group.
APP_SPACE_ADMIN_GROUP_DESC constant varchar(80) := '@prm.application.security.group.type.spaceadmin.description';

--------------------------------------------------------------------
-- INVITE_PERSON_TO_APPLICATION
--------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Deepak      14-June-02 Removed the redundant procedure

PROCEDURE create_application (
    i_application_id    in number,
    i_application_name  IN VARCHAR2,
    i_application_desc  IN VARCHAR2,
    i_creator_id        IN NUMBER
);

procedure respond_to_application_invite (
    i_invitation_code in number,
    i_application_id in number,
    i_person_id in number,
    i_response in varchar2,
    o_status out number
);

FUNCTION getNewUserTrendChangeMetric
RETURN NUMBER;

FUNCTION getAverageUserLoginsPerDay
RETURN NUMBER;

/*
procedure add_logo
(
    business_id IN number,
    logo_id IN number
);

PROCEDURE UPDATE_BUSINESS
(
    i_business_space_id IN NUMBER,
    i_business_name IN VARCHAR2,
    i_business_desc IN VARCHAR2,
    i_business_type IN VARCHAR2,
    o_status OUT NUMBER
);
*/


END; -- Package Specification APPLICATION
/

CREATE OR REPLACE PACKAGE BODY BASE
IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Phil        08-May-00  Creation.


--------------------------------------------------------------------
-- GET_OBJECT_TYPE
--------------------------------------------------------------------

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Phil        08-May-00  Creation.

FUNCTION GET_OBJECT_TYPE
( IN_OBJECT_ID PN_OBJECT.OBJECT_ID%TYPE)
RETURN PN_OBJECT.OBJECT_TYPE%TYPE
AS
    V_OBJECT_TYPE PN_OBJECT.OBJECT_TYPE%TYPE;
BEGIN
    SELECT OBJECT_TYPE INTO V_OBJECT_TYPE FROM PN_OBJECT WHERE OBJECT_ID = IN_OBJECT_ID;

    RETURN V_OBJECT_TYPE;
END;

--------------------------------------------------------------------
-- GET_DOC_CONTAINER_FOR_OBJECT
--------------------------------------------------------------------

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Phil        08-May-00  Creation.

FUNCTION GET_DOC_CONTAINER_FOR_OBJECT
    (IN_OBJECT_ID PN_OBJECT.OBJECT_ID%TYPE)
RETURN PN_OBJECT.OBJECT_ID%TYPE
AS
    V_CONTAINER_ID PN_DOC_CONTAINER.DOC_CONTAINER_ID%TYPE;
BEGIN
    SELECT DOC_CONTAINER_ID INTO V_CONTAINER_ID FROM PN_DOC_CONTAINER_HAS_OBJECT
    WHERE OBJECT_ID = IN_OBJECT_ID;

    RETURN V_CONTAINER_ID;
END;

/**
 * GENERATES A DATABASE-UNIQUE OBJECT ID AND INSERTS A ROW IN PN_OBJECT.
 * DOES NOT COMMIT OR ROLLBACK.
 * I_OBJECT_TYPE THE TYPE OF OBJECT TO CREATE
 * I_CREATOR_PERSON_ID THE ID OF THE PERSON CREATING THE OBJECT
 * I_RECORD_STATUS THE RECORD STATUS OF THE OBJECT TO CREATE
 * RETURNS THE UNIQUE OBJECT ID
 * RAISES ANY EXCEPTIONS
 */
FUNCTION      CREATE_OBJECT
(
    I_OBJECT_TYPE  IN VARCHAR2,
    I_CREATOR_PERSON_ID IN NUMBER,
    I_RECORD_STATUS IN VARCHAR2
)
RETURN PN_OBJECT.OBJECT_ID%TYPE
IS

    V_OBJECT_ID PN_OBJECT.OBJECT_ID%TYPE;

    ERR_NUM NUMBER;
    ERR_MSG VARCHAR2(120);
    STORED_PROC_NAME VARCHAR2(100):= 'CREATE_OBJECT';

BEGIN
    -- get new object_id for the sequence generator
    SELECT PN_OBJECT_SEQUENCE.NEXTVAL INTO V_OBJECT_ID FROM DUAL;

    -- Create new pn_object
    INSERT INTO PN_OBJECT
        (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
    VALUES
        (V_OBJECT_ID, I_OBJECT_TYPE, SYSDATE, I_CREATOR_PERSON_ID, I_RECORD_STATUS);

    RETURN V_OBJECT_ID;


    -- Log exceptions
    EXCEPTION
    WHEN OTHERS THEN
        BASE.LOG_ERROR(STORED_PROC_NAME, SQLCODE, SQLERRM);
        RAISE;
END;

--------------------------------------------------------------------
-- LOG_ERROR (autonomous transaction)
-- Writes to the error log.  Truncates any varchar parameters
-- to appropriate length before inserting row.
--------------------------------------------------------------------
    PROCEDURE LOG_ERROR
        ( I_STORED_PROC_NAME    IN VARCHAR2,
          I_ERROR_NUMBER        IN NUMBER,
          I_ERROR_MESSAGE       IN VARCHAR2)
    IS
        PRAGMA AUTONOMOUS_TRANSACTION;
        V_STORED_PROC_NAME  PN_SP_ERROR_LOG.STORED_PROC_NAME%TYPE := SUBSTR(I_STORED_PROC_NAME, 1, 60);
        V_ERROR_MESSAGE     PN_SP_ERROR_LOG.ERROR_MSG%TYPE        := SUBSTR(I_ERROR_MESSAGE, 1, 240);
    BEGIN
        INSERT INTO PN_SP_ERROR_LOG
            (TIMESTAMP, STORED_PROC_NAME, ERROR_CODE, ERROR_MSG)
        VALUES
            (SYSDATE, V_STORED_PROC_NAME, I_ERROR_NUMBER, V_ERROR_MESSAGE);
        COMMIT;
    END LOG_ERROR;

   FUNCTION CLOB_LIKE
       (I_CLOB IN CLOB,
        I_SEARCH IN VARCHAR2,
        I_IS_CASE_SENSITIVE IN NUMBER)
      RETURN NUMBER
   IS

      -- PL/SQL (as opposed to SQL) can cope with varchar2s up to 32K (on 8i).
      -- This may not be the case on older versions, in which case set the
      -- following constant to 4000
      C_MAXVARCHAR2   CONSTANT NUMBER := 32767;
      L_OFFSET                 NUMBER := 1;
      L_LENGTH                 NUMBER;
      L_NEXTBITE               NUMBER;
      O_ANSWER                 NUMBER := 0;

   BEGIN

      -- Determine the length of the CLOB
      L_LENGTH := DBMS_LOB.GETLENGTH (I_CLOB);

      -- The offset between one bite and the next is less than the size of each
      -- bite. Bites need to overlap by (length of search string - 1) to allow
      -- for an occurrence of the string spanning the join.
      L_NEXTBITE := C_MAXVARCHAR2 + 1 - LENGTH (I_SEARCH);

      -- For a case insensitive search we must loop through the CLOB
      -- Look through the CLOB as a series of VARCHAR2 bites
      WHILE L_OFFSET <= L_LENGTH LOOP

          IF (I_IS_CASE_SENSITIVE > 0) THEN

              -- Perform case sensitive like
              IF DBMS_LOB.SUBSTR(I_CLOB, C_MAXVARCHAR2, L_OFFSET) LIKE I_SEARCH THEN
                  O_ANSWER := 1;
                  EXIT; -- A match has been found, so bang out of the loop
              END IF;

          ELSE

              -- Perform case insensitive like
              IF UPPER(DBMS_LOB.SUBSTR(I_CLOB, C_MAXVARCHAR2, L_OFFSET)) LIKE UPPER(I_SEARCH) THEN
                  O_ANSWER := 1;
                  EXIT; -- A match has been found, so bang out of the loop
              END IF;

          END IF;

          -- Find the start of the next bite.
          L_OFFSET := L_OFFSET + L_NEXTBITE;
      END LOOP;

      RETURN O_ANSWER;

   END;

END; -- Package Body BASE
/

CREATE OR REPLACE PACKAGE BASE IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Phil        08-May-00  Creation.
-- Tim         18-Sep-00  Added Database Error Codes (mirror Java ones)
-- Tim         02-Oct-00  Added LOG_ERROR as an autonomous transaction
-- THIS is the base package which contains generally accessible functionality
--

    -- Error codes
    --3456789012345678901234567890
    OPERATION_SUCCESSFUL              CONSTANT NUMBER  := 0;
    UPDATE_RECORD_OUT_OF_SYNC         CONSTANT NUMBER  := 1000;
    UPDATE_RECORD_LOCKED              CONSTANT NUMBER  := 1010;
    PLSQL_EXCEPTION                   CONSTANT NUMBER  := 2000;
    DOC_UNIQUE_NAME_CONSTRAINT        CONSTANT NUMBER  := 5001;
    UNABLE_TO_EXTEND_INDEX            CONSTANT NUMBER  := -1654;
    -- End of error codes

    -- Record Status values
    ACTIVE_RECORD_STATUS            CONSTANT CHAR := 'A';
    DELETED_RECORD_STATUS           CONSTANT CHAR := 'D';
    PENDING_RECORD_STATUS           CONSTANT CHAR := 'P';

  FUNCTION GET_OBJECT_TYPE
     ( IN_OBJECT_ID PN_OBJECT.OBJECT_ID%TYPE)
     RETURN PN_OBJECT.OBJECT_TYPE%TYPE;

  FUNCTION GET_DOC_CONTAINER_FOR_OBJECT
    ( IN_OBJECT_ID PN_OBJECT.OBJECT_ID%TYPE)
    RETURN PN_OBJECT.OBJECT_ID%TYPE;

  FUNCTION  CREATE_OBJECT
(
    I_OBJECT_TYPE  IN VARCHAR2,
    I_CREATOR_PERSON_ID IN NUMBER,
    I_RECORD_STATUS IN VARCHAR2
) RETURN PN_OBJECT.OBJECT_ID%TYPE;

    /*
        THIS PROCEDURE WRITES AN ERORR TO PN_SP_ERROR_LOG
        IT IS AN AUTONOMOUS TRANSACTION WHICH MEANS IT CAN COMMIT
        AND ROLLBACK WITHOUT AFFECTING THE CALLING PROCEDURE AT ALL.
     */
    PROCEDURE LOG_ERROR
        ( I_STORED_PROC_NAME    IN VARCHAR2,
          I_ERROR_NUMBER        IN NUMBER,
          I_ERROR_MESSAGE       IN VARCHAR2);

   /*
    * INDICATES WHETHER THE SPECIFIED CLOB CONTAINS THE SPECIFIED SEARCH
    * STRING.
    * THIS WORKS ON ORACLE 8I BY PARSING THE CLOB IN CHUNKS.
    * @PARAM I_CLOB THE CLOB LOCATER TO SEARCH IN
    * @PARAM I_SEARCH THE STRING TO SEARCH FOR
    * @PARAM I_IS_CASE_SENSITIVE (OPTIONAL) 0 MEANS PERFORM A CASE INSENSITIVE
    * SEARCH.  THIS IS THE DEFAULT.  1 MEANS PERFORM A CASE SENSITIVE SEARCH
    * @RETURN 1 IF THE SEARCH STRING WAS FOUND; 0 OTHERWISE
    */
   FUNCTION CLOB_LIKE
       (I_CLOB IN CLOB,
        I_SEARCH IN VARCHAR2,
        I_IS_CASE_SENSITIVE IN NUMBER)
      RETURN NUMBER;

END; -- Package Specification BASE
/

CREATE OR REPLACE PACKAGE BODY business IS


--------------------------------------------------------------------
-- INVITE_PERSON_TO_BUSINESS
--------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Robin       27-Apr-00  Creation.
-- Robin       11-May-00  Changed project_id to space_id in invited_users.
-- Deepak      20-Aug-01  Added new paramters to Create_business
-- Vish        10-Sep-02  Replaced "Top folder" string with a token.

Procedure invite_person_to_business
(
    i_business_id IN NUMBER,
    i_person_id IN NUMBER,
    i_email IN VARCHAR2,
    i_firstname IN VARCHAR2,
    i_lastname IN VARCHAR2,
    i_responsibilities IN VARCHAR2,
    i_invitor_id IN NUMBER,
    o_invitation_code OUT NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'BUSINESS.INVITE_PERSON_TO_BUSINESS';
v_sysdate DATE;
v_invited_status CONSTANT VARCHAR2(20) := 'Invited';

BEGIN

    SELECT sysdate INTO v_sysdate FROM dual;

    INSERT INTO pn_invited_users
        (invitation_code, space_id, person_id , invitee_email, invitee_firstname, invitee_lastname,
         invitee_responsibilities, invitor_id, date_invited, date_responded, invited_status)
      VALUES
        (pn_invite_sequence.nextval, i_business_id, i_person_id , i_email, i_firstname, i_lastname,
         i_responsibilities, i_invitor_id, v_sysdate, null, v_invited_status);

    SELECT pn_invite_sequence.currval INTO o_invitation_code FROM dual;


    COMMIT;
    o_status := success;

EXCEPTION

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
            VALUES (v_sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END invite_person_to_business;


procedure add_logo
(
    i_business_id IN number,
    i_logo_id IN number
)

as

stored_proc_name VARCHAR2(100):= 'BUSINESS.ADD_LOGO';

BEGIN

    update pn_business
        set logo_image_id = i_logo_id
    where business_id = i_business_id;

COMMIT;

EXCEPTION

    WHEN OTHERS THEN
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (SYSDATE,stored_proc_name,err_num,err_msg);
        COMMIT;

END;

--------------------------------------------------------------------
-- RESPOND_TO_BUSINESS_INVITE
--------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Robin       27-Apr-00  Creation.
-- Robin       11-May-00  Changed project_id to space_id in invited_users.


Procedure respond_to_business_invite
(
    i_invitation_code IN NUMBER,
    i_business_id IN NUMBER,
    i_person_id IN NUMBER,
    i_response IN VARCHAR2,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'BUSINESS.RESPOND_TO_BUSINESS_INVITE';
v_sysdate DATE;
v_status NUMBER;
v_group_id pn_group.group_id%TYPE;

BEGIN

    SELECT sysdate INTO v_sysdate FROM dual;

    IF (i_response = 'Accepted') THEN

        INSERT INTO pn_space_has_person
            (space_id, person_id, relationship_person_to_space, relationship_space_to_person,
             responsibilities, record_status)
          SELECT i_business_id, i_person_id, 'member', 'has', invitee_responsibilities, 'A'
             FROM pn_invited_users
             WHERE invitation_code = i_invitation_code;

        IF SQL%NOTFOUND THEN
            o_status := no_data;
            ROLLBACK;
            RETURN;
        END IF;

        INSERT INTO pn_group_has_person
            (group_id, person_id)
          SELECT g.group_id, i_person_id
             FROM pn_group g, pn_space_has_group sg
             WHERE space_id = i_business_id
               AND g.group_type_id = SECURITY.GROUP_TYPE_TEAMMEMBER
               AND g.group_id = sg.group_id;

        IF SQL%NOTFOUND THEN
            o_status := no_data;
            ROLLBACK;
            RETURN;
        END IF;

        -- principal group
        v_group_id := NULL;
        security.create_principal_group(i_person_id, i_business_id, v_group_id);

        v_status := NULL;
        security.add_person_to_group(v_group_id, i_person_id, v_status);

    END IF;

    UPDATE pn_invited_users
       SET date_responded = v_sysdate, invited_status = i_response
       WHERE invitation_code = i_invitation_code
         AND space_id = i_business_id and person_id = i_person_id;

    IF SQL%NOTFOUND THEN
        o_status := no_data;
        ROLLBACK;
        RETURN;
    END IF;

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
            VALUES (v_sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END respond_to_business_invite;



---------------------------------------------------------------------
-- CREATE_BUSINESS
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Robin       11-May-00  Created from existing procedure.

PROCEDURE CREATE_BUSINESS
 (
    i_creator_id IN VARCHAR2,
    i_business_name IN VARCHAR2,
    i_business_desc IN VARCHAR2,
    i_business_type IN VARCHAR2,
    i_business_logo_id IN VARCHAR2,
    i_address_id IN VARCHAR2,
    i_is_master IN VARCHAR2,
    i_business_category_id IN VARCHAR2,
    i_brand_id IN VARCHAR2,
    i_billing_account_id IN VARCHAR2,
    o_business_id OUT VARCHAR2
)
IS
    v_portfolio_id NUMBER(20) :=0;
    v_methodology_id NUMBER(20) :=0;
    v_doc_provider_id NUMBER(20) := 0;
    v_doc_space_id NUMBER(20) := 0;
    v_doc_container_id NUMBER(20) := 0;
    v_system_container_id NUMBER(20) := 0;
    v_directory_id NUMBER(20) :=0;
    v_calendar_id NUMBER(20) :=0;
    v_group_id NUMBER(20) :=0;
    v_status NUMBER := 0;
    v_power_user NUMBER := 0;

    v_global_domain  pn_global_domain%rowtype;


   	CURSOR  c_object_type IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;

    CURSOR  c_default_permissions (business_id pn_business_space.business_id%type) IS
        select group_id, actions
        from pn_default_object_permission
        where space_id = business_id and object_type = 'business';


BEGIN
-- Create business object_id
    o_business_id := BASE.CREATE_OBJECT('business', i_creator_id, 'A');

-- Create the new business record
-- TODO:  log_image_id null for now
    INSERT INTO pn_business
        (business_id, address_id, business_name, business_desc, business_type, logo_image_id,
         is_local, remote_host_id, remote_business_id, record_status,is_master,business_category_id,brand_id,billing_account_id)
        VALUES
        (o_business_id, i_address_id, i_business_name, i_business_desc, i_business_type, i_business_logo_id,
         '1', null, null, 'A',i_is_master,i_business_category_id,i_brand_id,i_billing_account_id);

-- Create a business_space for this business
-- TODO: space_type null for now (do business_spaces have space_type???)
    INSERT INTO pn_business_space
        (business_id, business_space_id, space_type, record_status)
        VALUES
        (o_business_id, o_business_id, null, 'A');


/****************************************************************************************************
*  BUSINESS SECURITY
****************************************************************************************************/

    -- SPACE_HAS_MODULES
    -- The new business space initially has access to all modules.
    -- Copy all pn_module entries to pn_space_has_modules for this project

    INSERT INTO pn_space_has_module (
        space_id,
        module_id,
        is_active)
    SELECT o_business_id, module_id, '1'
    FROM
        pn_module
    WHERE
        module_id IN (10, 20, 30, 70, 110, 140, 170, 180, 190, 200, 210, 310, 330);

    -- Disable below mentioned modules for the time-being

    UPDATE pn_space_has_module SET is_active = '0' WHERE  space_id = o_business_id AND module_id IN (90 , 100 , 120 , 210 ) ;

    -- SPACE ADMINISTRATOR GROUP
    -- The business creator is the inital space administrator of this new project
    v_status := security.F_CREATE_SPACE_ADMIN_GROUP(i_creator_id, o_business_id, '@prm.business.security.group.type.spaceadmin.description');

    -- SPACE ADMINISTRATOR GROUP
    -- The business creator is the inital space administrator of this new project
    v_power_user := security.F_CREATE_POWER_USER_GROUP(i_creator_id, o_business_id, '@prm.security.group.type.poweruser.description');



    -- PRINCIPAL GROUP
    -- The business creator (person) must be put into a principal group for this space.
    v_group_id := NULL;
    SECURITY.CREATE_PRINCIPAL_GROUP(i_creator_id, o_business_id, v_group_id);

    -- add bsuiness creator (person) to their principal group
    v_status := NULL;
    SECURITY.ADD_PERSON_TO_GROUP(v_group_id, i_creator_id, v_status);

    -- TEAM MEMBER GROUP
    -- The creator is the only initial team member
    v_group_id := NULL;
    SECURITY.CREATE_TEAMMEMBER_GROUP(i_creator_id, o_business_id, v_group_id);

    v_status := NULL;
    SECURITY.ADD_PERSON_TO_GROUP(v_group_id, i_creator_id, v_status);


/****************************************************************************************************
*  END BUSINESS SECURITY
****************************************************************************************************/










/*********************************************************************
   BUSINESS PORTFOLIOS
**********************************************************************/
-- Create a default "owner_portfolio" for this business
-- This portfolio is maintained by the system and contains all the projects
-- OWNED DIRECTLY by this Business Space.

    -- Create portfolio object_id
    v_portfolio_id := BASE.CREATE_OBJECT('portfolio', i_creator_id, 'A');

    INSERT INTO pn_portfolio
        (portfolio_id, portfolio_name, portfolio_desc, portfolio_type, content_type, record_status)
        VALUES
        (v_portfolio_id, 'owner', 'Projects owned by this Business Space', 'static', 'project', 'A');

    INSERT INTO pn_space_has_portfolio
        (space_id, portfolio_id, is_default)
        VALUES
        (o_business_id, v_portfolio_id, 0);


-- Create a default "complete_portfolio" for this business
-- This portfolio is maintained by the system and contains all the businesses' projects.

    -- Create portfolio object_id
    v_portfolio_id := BASE.CREATE_OBJECT('portfolio', i_creator_id, 'A');

    INSERT INTO pn_portfolio
        (portfolio_id, portfolio_name, portfolio_desc)
        VALUES
        (v_portfolio_id, 'visible', 'All projects visible by this business space');

    INSERT INTO pn_space_has_portfolio
        (space_id, portfolio_id, is_default)
        VALUES
        (o_business_id, v_portfolio_id, 1);     -- Set this to be the Default portfolio.

    UPDATE pn_business_space SET complete_portfolio_id = v_portfolio_id where business_id = o_business_id;


-- Add the creator as the first member of the business directory
    INSERT INTO pn_space_has_person
        (space_id, person_id, relationship_person_to_space, record_status)
        VALUES
        (o_business_id, i_creator_id, 'employee', 'A');

-- Every business space gets the default directory
 Select directory_id into v_directory_id from pn_directory where is_default = 1;

 INSERT INTO pn_space_has_directory
        (directory_id, space_id)
        VALUES
        (v_directory_id, o_business_id);


    /* CREATE DOCUMENT SPACES FOR BUSINESS
       Add the default document provider to this business space.  The user can add more
       document providers later.  All projects created under this business space will
       inherite the busines space's doc providers

       Each business space get it's own doc space created for the defualt doc provider
       TODO:  Set top-level container for the project's doc space.
    */

    -- Get the doc_providers available to this project (defined by business space)
    -- TODO:  loop to get all doc_providers.  Just getting the default one now.
    -- Get a new project_id
    SELECT
        doc_provider_id into v_doc_provider_id
    FROM
        pn_doc_provider
    WHERE
        doc_provider_type_id = 100;  -- 'Internal Database Vault'

    -- Add this doc_provider to the business so that it's projects will inherit
    insert into pn_space_has_doc_provider
        (space_id, doc_provider_id, is_default)
    values
        (o_business_id, v_doc_provider_id, 1);


    -- new doc space for this business
    v_doc_space_id := BASE.CREATE_OBJECT('doc_space', i_creator_id, 'A');

    insert into pn_doc_space
        (doc_space_id, doc_space_name, record_status, crc)
    values
        (v_doc_space_id, 'default', 'A', SYSDATE);


    -- link new doc_space back to it's doc_provider
    insert into pn_doc_provider_has_doc_space
        (doc_provider_id, doc_space_id)
    values
        (v_doc_provider_id, v_doc_space_id);


    -- this business owns the doc space
    insert into pn_space_has_doc_space
      (space_id, doc_space_id)
    values
      (o_business_id, v_doc_space_id);

    v_doc_container_id := BASE.CREATE_OBJECT('doc_container', i_creator_id, 'A');

    insert into pn_doc_container
        (doc_container_id, container_name, container_description, date_modified, modified_by_id, record_status, crc)
    values
        (v_doc_container_id, '@prm.document.container.topfolder.name', 'Top level document folder', SYSDATE, i_creator_id, 'A', SYSDATE);

    SECURITY.CREATE_SECURITY_PERMISSIONS(v_doc_container_id, 'doc_container', o_business_id, i_creator_id);

    -- Link container (Top folder) to doc space
    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_doc_container_id, 1);


    v_system_container_id := BASE.CREATE_OBJECT('doc_container', i_creator_id, 'A');

    -- SYSTEM container
    insert into pn_doc_container
        (doc_container_id, container_name, container_description, modified_by_id, date_modified,record_status, is_hidden, crc)
    values
        (v_system_container_id, o_business_id, 'System container for this space',i_creator_id, SYSDATE,'A', 1, SYSDATE);

    SECURITY.CREATE_SECURITY_PERMISSIONS(v_system_container_id, 'doc_container', o_business_id, i_creator_id);

    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_system_container_id, 0);

   insert into pn_doc_container_has_object
        (doc_container_id, object_id)
    values
        (v_doc_container_id, v_system_container_id);


     /*
        BUSINESS CALENDAR
    */
    v_calendar_id := BASE.CREATE_OBJECT('calendar', i_creator_id, 'A');

    insert into pn_calendar
        (calendar_id, calendar_name, calendar_description, record_status)
    values (v_calendar_id, 'Business Calendar', 'Main Business Calendar', 'A');

    -- Link calendar to project space
    insert into pn_space_has_calendar
        (space_id, calendar_id)
    values (o_business_id, v_calendar_id);



    /* COPY SYSTEM DOMAINS TO BUSINESS' CUSTOM DOMAINS
      The business can modify and add to these domains.
      The business' projects will inheriate these domains.
    */

    -- pn_person_has_skill
    -- proficiency_code
    insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_person_has_skill' and
        column_name = 'proficiency_code';


    -- pn_post
    -- urgency_id
    insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_post' and
        column_name = 'urgency_id';


    -- pn_deliverable
    -- deliverable_type_id
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_deliverable' and
        column_name = 'deliverable_type_id';


    -- pn_process_phase
    -- status_id
    insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_process_phase' and
        column_name = 'status_id';


    -- pn_process_gate
    -- status_id
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_process_gate' and
        column_name = 'status_id';


    -- pn_process_step
    -- status_id
  insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_process_step' and
        column_name = 'status_id';


    -- pn_deliverable
    -- status_id
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_deliverable' and
        column_name = 'status_id';


    -- pn_task
    -- status_id
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_task' and
        column_name = 'status_id';

    -- pn_task
    -- priority
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_task' and
        column_name = 'priority';


    -- pn_resource_assignment
    -- status_id
    insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_resource_assignment' and
        column_name = 'status_id';


    -- pn_calendar_event
    -- frequency_type_id
   insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_calendar_event' and
        column_name = 'frequency_type_id';

    -- pn_calendar_event
    -- event_type_id
  insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_calendar_event' and
        column_name = 'event_type_id';

    -- pn_agenda_item
    -- status_id
  insert into pn_custom_domain
        (object_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status)
    select
        o_business_id, table_name, column_name, code_name, code, code_desc, code_url, presentation_sequence, is_default, record_status
    from pn_global_domain where
        table_name = 'pn_agenda_item' and
        column_name = 'status_id';

END;


---------------------------------------------------------------------
-- UPDATE_BUSINESS
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Robin       11-May-00  Created from existing procedure.
-- Robin       08-Jun-00  Added business_type.
-- Roger       28-Aug-01  Added category, brand, billing_account

PROCEDURE UPDATE_BUSINESS
    (
        i_business_space_id IN VARCHAR2,
        i_business_name IN VARCHAR2,
        i_business_desc IN VARCHAR2,
        i_business_type IN VARCHAR2,
        i_business_category_id IN VARCHAR2,
        i_brand_id  IN VARCHAR2,
        i_billing_account_id IN VARCHAR2,
        o_status OUT NUMBER
    )
    IS

    BEGIN
        update  pn_business
            set     business_name = i_business_name,
                    business_desc = i_business_desc,
                    business_type = i_business_type,
                    business_category_id = i_business_category_id,
                    brand_id = i_brand_id,
                    billing_account_id = i_billing_account_id
            where   business_id = i_business_space_id;
            commit;
    END;


function getNumProjects
(
    i_business_id in number
) return number
is

    v_num_projects number := -1;
    v_portfolio_id pn_space_has_portfolio.portfolio_id%type;

BEGIN

        select
            p.portfolio_id into v_portfolio_id
        from
            pn_space_has_portfolio shp, pn_portfolio p
        where
            shp.space_id = i_business_id and
            shp.portfolio_id=p.portfolio_id and
            portfolio_name='owner';


    select count (space_id) into v_num_projects
        from pn_portfolio_has_space
        where portfolio_id = v_portfolio_id;

    return v_num_projects;

exception
    when others then
        return -1;
end;



function getNumPeople
(
    i_business_id in number
) return number
is

    v_num_people number := -1;

BEGIN

    select count(person_id) into v_num_people from pn_space_has_person
    where space_id = i_business_id;

    return v_num_people;

exception
    when others then
        return 0;
end;
END; -- Package Body BUSINESS
/

CREATE OR REPLACE PACKAGE business IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Robin       11-Apr-00  Moved existing procedures to package.
-- Robin       08-Jun-00  Added business_type to update_business.
-- Deepak      20-Aug-01  Added new paramters to Create_business
-- Vish        10-Sep-02  Replaced "Top folder" string with a token.

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

Procedure invite_person_to_business
(
    i_business_id IN NUMBER,
    i_person_id IN NUMBER,
    i_email IN VARCHAR2,
    i_firstname IN VARCHAR2,
    i_lastname IN VARCHAR2,
    i_responsibilities IN VARCHAR2,
    i_invitor_id IN NUMBER,
    o_invitation_code OUT NUMBER,
    o_status OUT NUMBER
);

Procedure respond_to_business_invite
(
    i_invitation_code IN NUMBER,
    i_business_id IN NUMBER,
    i_person_id IN NUMBER,
    i_response IN VARCHAR2,
    o_status OUT NUMBER
);

procedure add_logo
(
    i_business_id IN number,
    i_logo_id IN number
);


PROCEDURE CREATE_BUSINESS
(
    i_creator_id IN VARCHAR2,
    i_business_name IN VARCHAR2,
    i_business_desc IN VARCHAR2,
    i_business_type IN VARCHAR2,
    i_business_logo_id IN VARCHAR2,
    i_address_id IN VARCHAR2,
    i_is_master IN VARCHAR2,
    i_business_category_id IN VARCHAR2,
    i_brand_id IN VARCHAR2,
    i_billing_account_id IN VARCHAR2,
    o_business_id OUT VARCHAR2
);


PROCEDURE UPDATE_BUSINESS
(
        i_business_space_id IN VARCHAR2,
        i_business_name IN VARCHAR2,
        i_business_desc IN VARCHAR2,
        i_business_type IN VARCHAR2,
        i_business_category_id IN VARCHAR2,
        i_brand_id  IN VARCHAR2,
        i_billing_account_id IN VARCHAR2,
        o_status OUT NUMBER
);

function getNumProjects
(
    i_business_id in number
) return number;

function getNumPeople
(
    i_business_id in number
) return number;


END; -- Package Specification BUSINESS
/

CREATE OR REPLACE PACKAGE BODY calendar IS

---------------------------------------------------------------------
-- STORE_MEETING
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Robin       25-Apr-00  Created from Adam's procedures.

Procedure store_meeting
(
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_calendar_id IN NUMBER,
    i_event_id IN NUMBER,
    i_meeting_id IN NUMBER,
    i_host_id IN NUMBER,
    i_event_name IN VARCHAR2,
    i_frequency_type_id IN NUMBER,
    i_facility_id IN NUMBER,
    i_start_date IN DATE,
    i_end_date IN DATE,
    o_event_desc_clob OUT CLOB,
    o_event_purpose_clob OUT CLOB,
    o_meeting_id OUT NUMBER,
    o_event_id OUT NUMBER
)
IS
BEGIN

    -- NEW MEETING, INSERT
    IF ((i_meeting_id IS NULL) OR (i_meeting_id = '')) THEN

        -- Create the object
        o_event_id := BASE.CREATE_OBJECT('event', i_person_id, 'A');
        o_meeting_id := BASE.CREATE_OBJECT('meeting', i_person_id, 'A');

        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(o_event_id, 'event', i_space_id, i_person_id);
        SECURITY.CREATE_SECURITY_PERMISSIONS(o_meeting_id, 'meeting', i_space_id, i_person_id);

        -- create the pn_calendar_event
        INSERT INTO pn_calendar_event
            (calendar_event_id, event_name, event_desc_clob, frequency_type_id,
             event_type_id, facility_id, event_purpose_clob, start_date, end_date, record_status)
        VALUES
            (o_event_id, i_event_name, empty_clob(), i_frequency_type_id,
             100, i_facility_id, empty_clob(), i_start_date, i_end_date, 'A')
        returning
                event_desc_clob, event_purpose_clob into o_event_desc_clob, o_event_purpose_clob;

       -- create the pn_meeting
        INSERT INTO pn_meeting
            (meeting_id, calendar_event_id, next_agenda_item_seq, host_id)
        VALUES
            (o_meeting_id, o_event_id, 1, i_host_id);

        -- link event to space's calendar
        INSERT INTO pn_calendar_has_event
            (calendar_id, calendar_event_id)
        VALUES
            (i_calendar_id, o_event_id);



     -- UPDATE EXISTING MEETING
     ELSE
        -- maintain current event and meeting id's
        o_event_id := i_event_id;
        o_meeting_id := i_meeting_id;

        -- update the event
        UPDATE pn_calendar_event
            SET
                facility_id = i_facility_id,
                event_name = i_event_name,
                event_purpose_clob = empty_clob(),
                event_desc_clob = empty_clob(),
                frequency_type_id = i_frequency_type_id,
                start_date = i_start_date,
                end_date = i_end_date
            WHERE
            	calendar_event_id = i_event_id
            returning
                event_desc_clob, event_purpose_clob into o_event_desc_clob, o_event_purpose_clob;

        IF SQL%NOTFOUND THEN
            raise e_no_data;
        END IF;

        -- update the meeting
        UPDATE pn_meeting
            SET host_id = i_host_id
            WHERE meeting_id = o_meeting_id;


        IF SQL%NOTFOUND THEN
            raise e_no_data;
        END IF;

    END IF;
END store_meeting;


---------------------------------------------------------------------
-- STORE_EVENT
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  -------------------------------------------
-- Robin       25-Apr-00  Created from Adam's procedures.

Procedure STORE_EVENT
(
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_calendar_id IN NUMBER,
    i_event_id IN NUMBER,
    i_event_name IN VARCHAR2,
    i_frequency_type_id IN NUMBER,
    i_facility_id IN NUMBER,
    i_start_date IN DATE,
    i_end_date IN DATE,
    o_event_desc_clob OUT CLOB,
    o_event_purpose_clob OUT CLOB,
    o_event_id OUT NUMBER
)
IS
BEGIN

     -- NEW EVENT, INSERT
    IF ((i_event_id IS NULL) OR (i_event_id = '')) THEN

        -- Create the object
        o_event_id := BASE.CREATE_OBJECT('event', i_person_id, 'A');

         -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(o_event_id, 'event', i_space_id, i_person_id);

        -- create the pn_calendar_event
        INSERT INTO pn_calendar_event
            (calendar_event_id, event_name, event_desc_clob, frequency_type_id,
            event_type_id, facility_id, event_purpose_clob, start_date, end_date, record_status)
        VALUES
            (o_event_id, i_event_name, empty_clob(), i_frequency_type_id,
             200, i_facility_id, empty_clob(), i_start_date, i_end_date, 'A')
        returning
            event_desc_clob, event_purpose_clob into o_event_desc_clob, o_event_purpose_clob;

        -- link event to space's calendar
        INSERT INTO pn_calendar_has_event
            (calendar_id, calendar_event_id)
        VALUES
            (i_calendar_id, o_event_id);

    -- UPDATE EXISTING MEETING
    ELSE
        -- maintain current event and meeting id's
        o_event_id := i_event_id;

        -- update the event
        UPDATE pn_calendar_event
            SET
                facility_id = i_facility_id,
                event_name = i_event_name,
                event_purpose_clob = empty_clob(),
                event_desc_clob = empty_clob(),
                frequency_type_id = i_frequency_type_id,
                start_date = i_start_date,
                end_date = i_end_date
            WHERE
            	calendar_event_id = i_event_id
            returning
                event_desc_clob, event_purpose_clob into o_event_desc_clob, o_event_purpose_clob;

        IF SQL%NOTFOUND THEN
            raise e_no_data;
        END IF;

    END IF;
END store_event;


---------------------------------------------------------------------
-- STORE_AGENDA_ITEM
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  -------------------------------------------
-- Robin       25-Apr-00  Created from Adam's procedures.

Procedure STORE_AGENDA_ITEM
(
    i_person_id IN NUMBER,
    i_meeting_id IN NUMBER,
    i_agenda_item_id IN NUMBER,
    i_item_name IN VARCHAR2,
    i_item_desc IN VARCHAR2,
    i_time_alloted IN VARCHAR2,
    i_status_id IN NUMBER,
    i_owner_id IN NUMBER,
    i_item_sequence IN NUMBER,
    i_is_meeting_notes_null IN NUMBER,
    o_meeting_notes_clob OUT CLOB
)

IS
    v_agenda_item_id    pn_agenda_item.agenda_item_id%TYPE;
    v_old_seq           pn_agenda_item.item_sequence%TYPE;
    v_agenda_item_seq   pn_agenda_item.item_sequence%TYPE;

BEGIN

     -- NEW AGENDA ITEM, INSERT
    IF ((i_agenda_item_id IS NULL) OR (i_agenda_item_id = '')) THEN

        -- register new agenda_item object
        v_agenda_item_id := BASE.CREATE_OBJECT('agenda_item', i_person_id, 'A');

        -- increment the meeting's agenda seq number
        UPDATE pn_meeting
            SET next_agenda_item_seq = next_agenda_item_seq + 1
            WHERE meeting_id = i_meeting_id
            RETURNING  next_agenda_item_seq INTO v_agenda_item_seq;

        IF SQL%NOTFOUND THEN
            raise e_no_data;
        END IF;

        -- must increment the meeting's agenda seq number and figure out the next sequence number
        IF ((i_item_sequence IS NULL) OR (i_item_sequence = '')) THEN

            -- user didn't specify a insert position, add to end of list.
            v_agenda_item_seq := (v_agenda_item_seq - 1);

        ELSE

             v_agenda_item_seq := i_item_sequence;

        END IF;

        -- Renumber the agenda items below the one inserted if needed.
        UPDATE pn_agenda_item
           SET item_sequence = item_sequence + 1
           WHERE meeting_id = i_meeting_id
             AND item_sequence >= v_agenda_item_seq;

        if (i_is_meeting_notes_null > 0) then
            -- Create the agenda item with null meeting notes
            INSERT INTO pn_agenda_item
                (meeting_id, agenda_item_id, item_name, item_desc, time_alloted,
                 status_id, owner_id, item_sequence, meeting_notes_clob, record_status)
            VALUES
                (i_meeting_id, v_agenda_item_id, i_item_name, i_item_desc, i_time_alloted,
                 i_status_id, i_owner_id, v_agenda_item_seq, null, 'A')
            returning
                meeting_notes_clob into o_meeting_notes_clob;
        else
            -- Create the agenda item with empty meeting notes for subsequent
            -- streaming
            INSERT INTO pn_agenda_item
                (meeting_id, agenda_item_id, item_name, item_desc, time_alloted,
                 status_id, owner_id, item_sequence, meeting_notes_clob, record_status)
            VALUES
                (i_meeting_id, v_agenda_item_id, i_item_name, i_item_desc, i_time_alloted,
                 i_status_id, i_owner_id, v_agenda_item_seq, empty_clob(), 'A')
            returning
                meeting_notes_clob into o_meeting_notes_clob;
        end if;


    -- UDATE EXISTING ITEM
    ELSE

        -- Figure out the item's old sequence number
        SELECT item_sequence into v_old_seq
           FROM pn_agenda_item
           WHERE meeting_id = i_meeting_id
             AND agenda_item_id = i_agenda_item_id;

        -- Renumber the other agenda items if this item's seq number changed.
        IF (i_item_sequence <> v_old_seq) THEN
            IF (i_item_sequence < v_old_seq) THEN
                UPDATE pn_agenda_item
                  SET item_sequence = item_sequence + 1
                  WHERE meeting_id = i_meeting_id
                    AND ((item_sequence >= i_item_sequence) AND (item_sequence < v_old_seq));
            ELSE
                UPDATE pn_agenda_item
                  SET item_sequence = item_sequence - 1
                  WHERE meeting_id = i_meeting_id
                    AND ((item_sequence > v_old_seq) AND (item_sequence <= i_item_sequence));

            END IF;
            IF SQL%NOTFOUND THEN
                raise e_no_data;
            END IF;
        END IF;

        if (i_is_meeting_notes_null > 0) then
            -- update the event with null meeting notes
            UPDATE pn_agenda_item
               SET item_name = i_item_name,
                   item_desc = i_item_desc,
                   time_alloted = i_time_alloted,
                   status_id = i_status_id,
                   owner_id = i_owner_id,
                   item_sequence = i_item_sequence,
                   meeting_notes_clob = null
               WHERE meeting_id = i_meeting_id
                 AND agenda_item_id = i_agenda_item_id
               returning
                   meeting_notes_clob into o_meeting_notes_clob;
         else
            -- update the event with empty meeting notes for subsequent streaming
            UPDATE pn_agenda_item
               SET item_name = i_item_name,
                   item_desc = i_item_desc,
                   time_alloted = i_time_alloted,
                   status_id = i_status_id,
                   owner_id = i_owner_id,
                   item_sequence = i_item_sequence,
                   meeting_notes_clob = empty_clob()
               WHERE meeting_id = i_meeting_id
                 AND agenda_item_id = i_agenda_item_id
               returning
                   meeting_notes_clob into o_meeting_notes_clob;

         end if;

         IF SQL%NOTFOUND THEN
             raise e_no_data;
         END IF;

    END IF;
END store_agenda_item;


---------------------------------------------------------------------
-- REMOVE_AGENDA_ITEM
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  -------------------------------------------
-- Robin       25-Apr-00  Created from Adam's procedures.

Procedure REMOVE_AGENDA_ITEM
(
    i_agenda_item_id IN NUMBER,
    i_meeting_id IN NUMBER,
    o_status OUT NUMBER
)

IS

    stored_proc_name VARCHAR2(100):= 'CALENDAR.REMOVE_AGENDA_MEETING';
    v_old_seq pn_agenda_item.item_sequence%TYPE;

BEGIN
    -- Figure out the item's current sequence number
    SELECT item_sequence into v_old_seq
        FROM pn_agenda_item
        WHERE meeting_id = i_meeting_id
          AND agenda_item_id = i_agenda_item_id;

    -- Renumber the other agenda items
    UPDATE pn_agenda_item
      SET item_sequence = item_sequence - 1
      WHERE meeting_id = i_meeting_id
        AND item_sequence > v_old_seq;

    -- set the agenda items status as deleted
    UPDATE pn_agenda_item
      SET record_status = 'D'
      WHERE meeting_id = i_meeting_id
        AND agenda_item_id = i_agenda_item_id;

    -- adjust the next agenda item sequence
    UPDATE pn_meeting
      SET next_agenda_item_seq = next_agenda_item_seq - 1
      WHERE meeting_id = i_meeting_id
        AND next_agenda_item_seq > 1;

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


END remove_agenda_item;

END; -- Package Body CALENDAR
/

CREATE OR REPLACE PACKAGE calendar
  IS

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;
e_no_data EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

Procedure store_meeting
(
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_calendar_id IN NUMBER,
    i_event_id IN NUMBER,
    i_meeting_id IN NUMBER,
    i_host_id IN NUMBER,
    i_event_name IN VARCHAR2,
    i_frequency_type_id IN NUMBER,
    i_facility_id IN NUMBER,
    i_start_date IN DATE,
    i_end_date IN DATE,
    o_event_desc_clob OUT CLOB,
    o_event_purpose_clob OUT CLOB,
    o_meeting_id OUT NUMBER,
    o_event_id OUT NUMBER
);

Procedure store_event
(
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_calendar_id IN NUMBER,
    i_event_id IN NUMBER,
    i_event_name IN VARCHAR2,
    i_frequency_type_id IN NUMBER,
    i_facility_id IN NUMBER,
    i_start_date IN DATE,
    i_end_date IN DATE,
    o_event_desc_clob OUT CLOB,
    o_event_purpose_clob OUT CLOB,
    o_event_id OUT NUMBER
);

Procedure store_agenda_item
(
    i_person_id IN NUMBER,
    i_meeting_id IN NUMBER,
    i_agenda_item_id IN NUMBER,
    i_item_name IN VARCHAR2,
    i_item_desc IN VARCHAR2,
    i_time_alloted IN VARCHAR2,
    i_status_id IN NUMBER,
    i_owner_id IN NUMBER,
    i_item_sequence IN NUMBER,
    i_is_meeting_notes_null IN NUMBER,
    o_meeting_notes_clob OUT CLOB
);

Procedure remove_agenda_item
(
    i_agenda_item_id IN NUMBER,
    i_meeting_id IN NUMBER,
    o_status OUT NUMBER
);



END; -- Package Specification CALENDAR
/

CREATE OR REPLACE PACKAGE BODY configuration IS

----------------------------------------------------------------------
-- PRIVATE crc_matches
-- Compare two crc values
-- i_original_crc - first crc value
-- i_new_crc - second cec value
-- returns true if both values are identical, false otherwise
----------------------------------------------------------------------
function crc_matches (i_original_crc in date, i_new_crc in date)
    return boolean
is
    v_crc_match     boolean := false;

begin
    if (i_original_crc = i_new_crc) then
        v_crc_match := true;
    end if;
    return v_crc_match;
end crc_matches;
----------------------------------------------------------------------

---------------------------------------------------------------------
-- CREATE_CONFIGURATION
-- NO COMMIT OR ROLLBACK IS PERFORMED
---------------------------------------------------------------------
PROCEDURE create_configuration (
    i_configuration_name  IN VARCHAR2,
    i_configuration_desc  IN VARCHAR2,
    i_brand_id            in varchar2,
    i_creator_id          IN NUMBER,
    o_configuration_id    out number,
    o_status              out number
) is
    v_status            NUMBER := 0;
    v_group_id          NUMBER(20) := 0;
    v_portfolio_id      NUMBER(20) := 0;
    v_directory_id      NUMBER(20) := 0;
    v_configuration_id  NUMBER(20) := 0;
    v_app_space_id      NUMBER(20);
    v_power_user_group_id NUMBER(20) := 0;

BEGIN
    -- Create the configuration space
    v_configuration_id := BASE.CREATE_OBJECT(CONFIGURATION_OBJECT_TYPE, i_creator_id, BASE.ACTIVE_RECORD_STATUS);

	-- Create security permissions for configuration space object
	-- object id is configuration id, space id is also configuration id
	SECURITY.CREATE_SECURITY_PERMISSIONS(v_configuration_id, CONFIGURATION_OBJECT_TYPE,
	                                     v_configuration_id, i_creator_id);

    -- Create the new configuration space record
    insert into pn_configuration_space
	    (configuration_id, configuration_name, configuration_desc, brand_id, created_by_id,
        created_datetime, crc, record_status)
    values
	    (v_configuration_id, i_configuration_name, i_configuration_desc, i_brand_id, i_creator_id,
        sysdate, sysdate, BASE.ACTIVE_RECORD_STATUS);


    /*
        Now configure security
     */

    -- Specify the modules to which the new space has access
    insert into pn_space_has_module
        (space_id, module_id, is_active)
        select
            v_configuration_id, module_id, '1'
        from
            pn_module
        where
            module_id in (30, 140, 250);

    -- SPACE ADMINISTRATOR GROUP
    -- The space creator is the initial space administrator
    v_status := security.F_CREATE_SPACE_ADMIN_GROUP(i_creator_id, v_configuration_id, CONFIG_SPACE_ADMIN_GROUP_DESC);

    -- PRINCIPAL GROUP
    -- The space creator (person) must be put into a principal group for this space.
    v_group_id := NULL;
    SECURITY.CREATE_PRINCIPAL_GROUP(i_creator_id, v_configuration_id, v_group_id);

    -- add space creator (person) to their principal group
    v_status := NULL;
    SECURITY.ADD_PERSON_TO_GROUP(v_group_id, i_creator_id, v_status);

    -- TEAM MEMBER GROUP
    -- The creator is the only initial team member
    v_group_id := NULL;
    SECURITY.CREATE_TEAMMEMBER_GROUP(i_creator_id, v_configuration_id, v_group_id);

    v_status := NULL;
    SECURITY.ADD_PERSON_TO_GROUP(v_group_id, i_creator_id, v_status);


    -- POWER USER GROUP
    --  CREATE POWER USER GROUP
    v_power_user_group_id := security.F_CREATE_POWER_USER_GROUP(i_creator_id, v_configuration_id, '@prm.security.group.type.poweruser.description');

    /*
        Directory
     */
    -- Add the creator as the first member of the configuration directory
    insert into pn_space_has_person
        (space_id, person_id, relationship_person_to_space, record_status)
    values
        (v_configuration_id, i_creator_id, 'member', 'A');

    -- Every configuration space gets the default directory
    select directory_id into v_directory_id from pn_directory where is_default = 1;

    insert into pn_space_has_directory
        (directory_id, space_id)
    values
        (v_directory_id, v_configuration_id);


    /*
        Portfolios
     */
    -- Insert configuration into creator personal portfolio
    insert into pn_portfolio_has_space (portfolio_id, space_id)
    select
        membership_portfolio_id,
        v_configuration_id
    from
        pn_person
    where
        person_id = i_creator_id;


    o_status := BASE.OPERATION_SUCCESSFUL;
    o_configuration_id := v_configuration_id;
/*
    -- there will only be one app space
    select application_id into v_app_space_id
    from pn_application_space;

    FORMS.COPY_ALL(v_app_space_id, v_configuration_id, i_creator_id, o_status);
*/
exception
    when others then
    begin
        o_status := BASE.PLSQL_EXCEPTION;
    end;
end create_configuration;


--------------------------------------------------------------------
-- CREATE_CONFIG_FOR_SPACE
-- NO COMMIT OR ROLLBACK IS PERFORMED
--------------------------------------------------------------------
PROCEDURE create_config_for_space (
    i_configuration_name  IN VARCHAR2,
    i_configuration_desc  IN VARCHAR2,
    i_brand_id            in varchar2,
    i_creator_id        IN NUMBER,
    i_space_id            in number)
is
    v_configuration_id  number;
    v_status            number;
    create_config_error exception;

begin
    -- First, create configuration
    create_configuration(i_configuration_name, i_configuration_desc, i_brand_id,
                         i_creator_id, v_configuration_id, v_status);

    if v_status <> BASE.OPERATION_SUCCESSFUL then
        raise create_config_error;
    end if;

    -- Now insert into default portfolio for specified space
    insert into pn_portfolio_has_space (portfolio_id, space_id)
    select
        portfolio_id,
        v_configuration_id
    from
        pn_space_has_portfolio
    where
        space_id = i_space_id
        and is_default = 1;

end create_config_for_space;

--------------------------------------------------------------------
-- RESPOND_TO_CONFIGURATION_INVITE
-- NO COMMIT OR ROLLBACK IS PERFORMED
--------------------------------------------------------------------
procedure respond_to_config_invite (
    i_invitation_code in number,
    i_configuration_id in number,
    i_person_id in number,
    i_response in varchar2,
    o_status out number
) is

    stored_proc_name VARCHAR2(100):= 'CONFIGURATION.RESPOND_TO_CONFIGURATION_INVITE';
    v_sysdate DATE;
    v_status NUMBER;
    v_group_id pn_group.group_id%TYPE;

begin

    select sysdate into v_sysdate from dual;

    IF (i_response = 'Accepted') THEN

        -- Add person to space
        insert into pn_space_has_person
            (space_id, person_id, relationship_person_to_space, relationship_space_to_person,
             responsibilities, record_status)
            select
               i_configuration_id, i_person_id, 'member', 'has',
               invitee_responsibilities, BASE.ACTIVE_RECORD_STATUS
            from
               pn_invited_users
            where
               invitation_code = i_invitation_code;

        -- Add person to team member group
        insert into pn_group_has_person
            (group_id, person_id)
            select
               g.group_id, i_person_id
            from
               pn_group g,
               pn_space_has_group sg
            where
               space_id = i_configuration_id
            and g.group_type_id = SECURITY.GROUP_TYPE_TEAMMEMBER
            and g.group_id = sg.group_id;

        -- Add configuration id to personal portfolio
        INSERT INTO pn_portfolio_has_space
            (portfolio_id, space_id)
          SELECT membership_portfolio_id, i_configuration_id
             FROM pn_person
             WHERE person_id = i_person_id;

        -- Create principal group
        v_group_id := NULL;
        security.create_principal_group(i_person_id, i_configuration_id, v_group_id);

        -- Add person to principal group
        v_status := NULL;
        security.add_person_to_group(v_group_id, i_person_id, v_status);

    end if;

   -- Now update invitation, setting to Accepted or Rejected
   update pn_invited_users
   set
      date_responded = v_sysdate,
      invited_status = i_response
   where
      invitation_code = i_invitation_code
   and space_id = i_configuration_id and person_id = i_person_id ;

   o_status := BASE.OPERATION_SUCCESSFUL;

EXCEPTION
    when others then
    BEGIN
        o_status := BASE.PLSQL_EXCEPTION;
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
    END;
END respond_to_config_invite;

---------------------------------------------------------------------
-- MODIFY_CONFIGURATION
-- Updates a configuration
-- NO COMMIT OR ROLLBACK
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Tim         23-Apr-01  Created for modifying configurations
PROCEDURE modify_configuration (
	i_configuration_id	  in number,
    i_configuration_name  IN VARCHAR2,
    i_configuration_desc  IN VARCHAR2,
    i_brand_id            in varchar2,
    i_modified_by_id      IN NUMBER,
	i_crc				  in date,
    o_return_value        out number
)
is
	-- User defined exceptions
    record_modified exception;  -- CRC differs
    record_locked   exception;  -- Record being updated by another user
    pragma exception_init (record_locked, -00054);

    v_datetime       pn_configuration_space.modified_datetime%type;
    v_current_crc    pn_configuration_space.crc%type;
    stored_proc_name varchar2(100):= 'CONFIGURATION.MODIFY_CONFIGURATION';

begin

    -- Check configuration space has not changed and lock record
    select crc into v_current_crc
        from pn_configuration_space
        where configuration_id = i_configuration_id
        for update nowait;

    if (crc_matches(i_crc, v_current_crc)) then

		-- Get current date and time for CRC and modified datetime
        select sysdate into v_datetime from dual;

		-- Update the configuration space record
        update
            pn_configuration_space c
        set
			c.configuration_name = i_configuration_name,
			c.configuration_desc = i_configuration_desc,
			c.brand_id = i_brand_id,
			c.modified_by_id = i_modified_by_id,
			c.modified_datetime = v_datetime,
			c.crc = v_datetime
		where
			c.configuration_id = i_configuration_id;

        o_return_value := BASE.OPERATION_SUCCESSFUL;

    else
        -- crc different
        raise record_modified;

    end if;

    /*
        NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
    */

exception

    when record_locked then
    begin
        o_return_value := BASE.UPDATE_RECORD_LOCKED;
    end;

    when record_modified then
    begin
        -- Record out of sync
        o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
    end;

	when others then
    begin
        o_return_value := BASE.PLSQL_EXCEPTION;
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
    end;

end modify_configuration;

---------------------------------------------------------------------
-- REMOVE_CONFIGURATION
-- Removes a configuration
-- NO COMMIT OR ROLLBACK
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Tim         23-Apr-01  Created for removing configurations
PROCEDURE remove_configuration (
	i_configuration_id	  in number,
    i_modified_by_id      IN NUMBER,
	i_crc				  in date,
    o_return_value        out number
)
is
	-- User defined exceptions
    record_modified exception;  -- CRC differs
    record_locked   exception;  -- Record being updated by another user
    pragma exception_init (record_locked, -00054);

    v_brand_id      pn_configuration_space.brand_id%type;

    v_datetime       pn_configuration_space.modified_datetime%type;
    v_current_crc    pn_configuration_space.crc%type;
    stored_proc_name varchar2(100):= 'CONFIGURATION.REMOVE_CONFIGURATION';

begin

    -- Check configuration space has not changed and lock record
    select crc into v_current_crc
        from pn_configuration_space
        where configuration_id = i_configuration_id
        for update nowait;

    if (crc_matches(i_crc, v_current_crc)) then

		-- Get current date and time for CRC and modified datetime
        select sysdate into v_datetime from dual;

        -- get this configuration's brand id
        select brand_id into v_brand_id from pn_configuration_space
        where configuration_id = i_configuration_id;

		-- Update the configuration space record to Removed
        update
            pn_configuration_space c
        set
			c.modified_by_id = i_modified_by_id,
			c.modified_datetime = v_datetime,
			c.crc = v_datetime,
			c.record_status = BASE.DELETED_RECORD_STATUS
		where
			c.configuration_id = i_configuration_id;

        -- remove the brand_has_host entries for this configuration
        delete from pn_brand_has_host where brand_id = v_brand_id;

        o_return_value := BASE.OPERATION_SUCCESSFUL;

    else
        -- crc different
        raise record_modified;

    end if;

    /*
        NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
    */

exception

    when record_locked then
    begin
        o_return_value := BASE.UPDATE_RECORD_LOCKED;
    end;

    when record_modified then
    begin
        -- Record out of sync
        o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
    end;

	when others then
    begin
        o_return_value := BASE.PLSQL_EXCEPTION;
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
    end;

end remove_configuration;


END; -- Package Body CONFIGURATION
/

CREATE OR REPLACE PACKAGE configuration IS
/*
    Configuration package provides functions and procedures
    for managing configuration spaces.
 */
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Tim         25-Mar-01  Cloned from application package.
-- Tim         20-Apr-01  Added creation of security permissions when creating
--                        configuration space
-- Tim         23-Apr-01  Added modify configuration capability

-- Object type for configuration space
CONFIGURATION_OBJECT_TYPE   constant varchar2(80) := 'configuration';

-- Description of configuration space administrator group
CONFIG_SPACE_ADMIN_GROUP_DESC constant varchar2(80) := '@prm.configuration.security.group.type.spaceadmin.description';

--------------------------------------------------------------------
-- INVITE_PERSON_TO_CONFIGURATION
--------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Deepak      14-June-02 Removed the redundant procedure


/**
  * Creates a configuration space, adds to creators portfolio.
  */
PROCEDURE create_configuration (
    i_configuration_name  IN VARCHAR2,
    i_configuration_desc  IN VARCHAR2,
    i_brand_id            in varchar2,
    i_creator_id        IN NUMBER,
    o_configuration_id    out number,
    o_status              out number
);

/*
 * Creates a configuration, adding to specified space default portfolio.
 * Used when inserting system accounts.
 */
PROCEDURE create_config_for_space (
    i_configuration_name  IN VARCHAR2,
    i_configuration_desc  IN VARCHAR2,
    i_brand_id            in varchar2,
    i_creator_id        IN NUMBER,
    i_space_id            in number
);

procedure respond_to_config_invite (
    i_invitation_code in number,
    i_configuration_id in number,
    i_person_id in number,
    i_response in varchar2,
    o_status out number
);

/**
  * Updates a configuration space.
  */
PROCEDURE modify_configuration (
	i_configuration_id	  in number,
    i_configuration_name  IN VARCHAR2,
    i_configuration_desc  IN VARCHAR2,
    i_brand_id            in varchar2,
    i_modified_by_id      IN NUMBER,
	i_crc				  in date,
    o_return_value        out number
);

/**
  * Removes a configuration space.
  */
PROCEDURE remove_configuration (
	i_configuration_id	  in number,
    i_modified_by_id      IN NUMBER,
	i_crc				  in date,
    o_return_value        out number
);

END; -- Package Specification CONFIGURATION
/

CREATE OR REPLACE PACKAGE BODY discussion IS

---------------------------------------------------------------------
-- CREATE_DISCUSSION_GROUP
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Robin       04-May-00  Created from existing procedure, added error handling
-- Adam        22-May-00  added ability to update discussion group properties
-- Roger       07-Jan-01  Added initial post to new group. Added group copy.
-- Tim         17-Dec-01  Removed COMMIT and ROLLBACK to enable this to be
--                        part of a greater transaction (e.g. DOCUMENT.COPY_DOCUMENT)
-- Tim         28-Feb-03  Made DISCUSSION_GROUP_CHARTER into a CLOB
/**
 * Creates OR updates a disucssion group.
 * During an update, the discussion group charter CLOB is set to empty_clob()
 * After calling this method, the charter must be streamed to the CLOB
 */
Procedure CREATE_DISCUSSION_GROUP
(
      i_discussion_group_id IN NUMBER,
      i_space_id IN VARCHAR2,
      i_object_id IN VARCHAR2,
      i_discussion_group_name IN VARCHAR2,
      i_person_id IN NUMBER,
      i_discussion_group_description IN VARCHAR2,
      i_create_welcome_post IN NUMBER,
      i_welcome_message_subject IN VARCHAR2,
      o_discussion_group_id OUT NUMBER,
      o_welcome_message_post_clob OUT CLOB,
      o_discussion_grp_charter_clob OUT CLOB
)

IS

    stored_proc_name VARCHAR2(100):= 'DISCUSSION.CREATE_DISCUSSION_GROUP';
    v_post_id   pn_post.post_id%type;
    v_status    number;
    v_message   varchar(2000);
    v_post_date date;
    v_post_body_id pn_post.post_body_id%type;

BEGIN
    IF (i_discussion_group_id IS NULL) THEN
        -- Create the object
        o_discussion_group_id := BASE.CREATE_OBJECT('discussion_group', i_person_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(o_discussion_group_id, 'discussion_group', i_space_id, i_person_id);

        -- Create the discussion_group
        -- We return the empty_clob into the clob locater output parameter
        INSERT INTO pn_discussion_group
            (discussion_group_name, discussion_group_id, discussion_group_description, discussion_group_charter_clob, record_status)
        VALUES
            (i_discussion_group_name, o_discussion_group_id, i_discussion_group_description, empty_clob(), 'A')
        RETURNING discussion_group_charter_clob into o_discussion_grp_charter_clob;

        -- This space is creating the discussion group, so it is the owner.
        INSERT INTO pn_object_has_discussion
            (object_id, discussion_group_id)
        VALUES
            (i_object_id, o_discussion_group_id);


        if (i_create_welcome_post > 0) THEN

            v_post_body_id := BASE.CREATE_OBJECT('post', i_person_id, 'A');

            insert into pn_post_body_clob
                (object_id , clob_field)
            values
                (v_post_body_id, empty_clob())
            returning clob_field into o_welcome_message_post_clob;

            CREATE_POST(
                i_space_id,
                o_discussion_group_id,
                null,
                i_person_id,
                i_welcome_message_subject,
                100,
                v_post_body_id,
                v_post_date,
                v_post_id);
        END IF;

    ELSE
        -- A. Klatzkin 5/22/00
        -- discussion group already exists so just update
        o_discussion_group_id := i_discussion_group_id;

        -- Return the CLOB locater into the OUT variable so it can
        -- be updated outside of this procedure
        -- Clears out the charter so that a subsequent write will overwrite
        -- all data
        UPDATE pn_discussion_group
        SET
           discussion_group_name = i_discussion_group_name,
           discussion_group_description = i_discussion_group_description,
           discussion_group_charter_clob = empty_clob()
        WHERE
           discussion_group_id = i_discussion_group_id
        RETURNING discussion_group_charter_clob into o_discussion_grp_charter_clob;

    END IF;

EXCEPTION
    WHEN OTHERS THEN
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;

END create_discussion_group;

----------------------------------------------------------------------
-- LOGS_EVENT
----------------------------------------------------------------------
 PROCEDURE log_event
    (
        discussion_group_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    )

IS
    v_discussion_group_id     pn_discussion_group.discussion_group_id%type := TO_NUMBER(discussion_group_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_discussion_history.discussion_group_history_id%type;
    v_action          pn_discussion_history.action%type := action;
    v_action_name     pn_discussion_history.action_name%type := action_name;
    v_action_comment  pn_discussion_history.action_comment%type := notes;

BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- insert a history record for this document

    insert into pn_discussion_history (
        discussion_group_id,
        discussion_group_history_id,
        action,
        action_name,
        action_by_id,
        action_date,
        action_comment)
    values (
        v_discussion_group_id,
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
        DBMS_OUTPUT.put_line('exception');
        rollback ;
    END;
END;  -- Procedure LOG_EVENT
----------------------------------------------------------------------

---------------------------------------------------------------------
-- CREATE_POST
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Robin       04-May-00  Created from existing procedure, added
--                        error handling

Procedure CREATE_POST
(
    i_space_id IN NUMBER,
    i_discussion_group_id IN NUMBER,
    i_parent_id IN NUMBER,
    i_person_id  IN NUMBER,
    i_subject IN VARCHAR2,
    i_urgency_id IN NUMBER,
    i_post_body_id IN NUMBER,
    o_post_date OUT DATE,
    o_post_id OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'DISCUSSION.CREATE_POST';
v_current_date date := SYSDATE;

BEGIN
    -- Create the object
    o_post_id := BASE.CREATE_OBJECT('post', i_person_id, 'A');

    -- Apply default security permissions
    SECURITY.CREATE_SECURITY_PERMISSIONS(o_post_id, 'post', i_space_id, i_person_id);

    --Create the post
    INSERT INTO pn_post
        (person_id, parent_id, discussion_group_id, post_id, subject, urgency_id, post_body_id, date_posted, record_status)
    VALUES
        (i_person_id, i_parent_id, i_discussion_group_id, o_post_id, i_subject, i_urgency_id, i_post_body_id, v_current_date, 'A');


    -- We say that the originator of the post has also read the post
    INSERT INTO pn_post_reader
        (person_id, discussion_group_id, post_id, date_read)
    VALUES
        (i_person_id, i_discussion_group_id, o_post_id, v_current_date);

    o_post_date := v_current_date;

EXCEPTION
    WHEN OTHERS THEN
        BASE.LOG_ERROR(stored_proc_name, SQLCODE, SQLERRM);
        raise;

END create_post;    -- A new post with a unique post_id number has been inserted into the data base



---------------------------------------------------------------------
-- UPDATE_POST_READER
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Robin       04-May-00  Created from existing procedure, added
--                        error handling

Procedure UPDATE_POST_READER
(
    i_person_id IN NUMBER,
    i_discussion_group_id IN NUMBER,
    i_post_id IN NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'DISCUSSION.UPDATE_POST_READER';
reader_count NUMBER(6);

BEGIN
    -- See if the person has already read this post
    select count(pr.person_id) into reader_count
    from pn_post_reader pr
    where
        pr.person_id = i_person_id and
        pr.discussion_group_id = i_discussion_group_id and
        pr.post_id = i_post_id;


    -- If the person has not read this post, mark it read.
    -- Otherwise, update date_read
    --IF (reader_count > 0) THEN
    --    update pn_post_reader pr
    --    set date_read = SYSDATE
    --    where
    --        pr.person_id = i_person_id and
    --        pr.discussion_group_id = i_discussion_group_id and
    --        pr.post_id = i_post_id;

    IF (reader_count <= 0) THEN
        INSERT INTO	pn_post_reader
            (person_id, discussion_group_id, post_id, date_read)
          VALUES
            (i_person_id, i_discussion_group_id, i_post_id, SYSDATE);
    END IF;

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

END update_post_reader;


----------------------------------------------------------------------
-- COPY_ALL
-- Copies all discussion groups for a space to another space
-- AUTONOMOUS TRANSACTION
----------------------------------------------------------------------
    PROCEDURE copy_all
      ( i_from_space_id     in varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        o_return_value      OUT number)
    is
        pragma autonomous_transaction;

        -- Cursor to read all discussion groups for a given space
        cursor group_cur (i_space_id in number)
        is
            select discussion_group_id
            from pn_object_has_discussion
            where object_id = i_space_id;

        group_rec           group_cur%rowtype;
        v_from_space_id     pn_space_has_class.space_id%type := to_number(i_from_space_id);
        v_to_space_id       pn_space_has_class.space_id%type := to_number(i_to_space_id);
        v_created_by_id     number := to_number(i_created_by_id);

    begin

        for group_rec in group_cur(i_from_space_id)
        loop
            copy_discussion_group(v_from_space_id, v_to_space_id, group_rec.discussion_group_id, v_created_by_id);
        end loop;

        if group_cur%isopen then
            close group_cur;
        end if;
        commit;
        o_return_value := base.operation_successful;

    exception
        when others then
        begin
           --dbms_output.put_line('copy_all Error '||TO_CHAR(SQLCODE)||': '||SQLERRM);
           if group_cur%isopen then
                close group_cur;
           end if;
           rollback;
           o_return_value := base.plsql_exception;
        end;

    end copy_all;





----------------------------------------------------------------------
-- COPY_DISCUSSION_GROUP
-- Copies a single discussion group from one space to another space.
-- New objects ids are assigned so that the two discussion groups
-- are completely independant copies of each other.
-- AUTONOMOUS_TRANSACTION
----------------------------------------------------------------------
    procedure copy_discussion_group (
        i_from_space_id in number,
        i_to_space_id in number,
        i_discussion_group_id in number,
        i_created_by_id in number)
    is
        PRAGMA AUTONOMOUS_TRANSACTION;

        -- Exceptions
        group_not_found exception;

        group_rec               pn_discussion_group%rowtype;
        v_discussion_group_id   pn_discussion_group.discussion_group_id%type;
        v_space_id              pn_object_has_discussion.object_id%type;
        v_welcome_message_post_clob pn_post_body_clob.clob_field%type;
        v_discussion_grp_charter_clob pn_discussion_group.discussion_group_charter_clob%type;

    begin

        select * into group_rec
        from pn_discussion_group
        where discussion_group_id = i_discussion_group_id;

        v_space_id := i_to_space_id;

        CREATE_DISCUSSION_GROUP
        (
            null,
            v_space_id,          -- varchar2
            v_space_id,
            group_rec.discussion_group_name,
            i_created_by_id,
            group_rec.discussion_group_description,
            0, -- Do not create-welcome-message
            NULL,
            v_discussion_group_id,  -- out
            v_welcome_message_post_clob, -- out
            v_discussion_grp_charter_clob -- out
        );

        -- Now stream the current clob data to the new clob locater
        if (group_rec.discussion_group_charter_clob is not null) then
            dbms_lob.append(v_discussion_grp_charter_clob,group_rec.discussion_group_charter_clob);
        end if;

        commit;

    exception
        when others then
        begin
            rollback;
            BASE.LOG_ERROR('DISCUSSION.COPY_DISCUSSION_GROUP', SQLCODE, SQLERRM);
            raise;
        end;
    end copy_discussion_group;


FUNCTION object_has_discussion
(
    i_object_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_has_disc             NUMBER  := 0;
    v_group_id              pn_post.discussion_group_id%type;
    v_count                 NUMBER  := 0;

BEGIN

    select distinct discussion_group_id into v_group_id from pn_object_has_discussion
    where object_id = i_object_id;

    select count(*) into v_count from pn_post
    where discussion_group_id = v_group_id
    and record_status = 'A';

    if (v_count > 0) then
        v_has_disc := 1;
    end if;

    return v_has_disc;


EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        v_has_disc := 0;
        return v_has_disc;
      END;


END; -- has_links

END; -- Package Body DISCUSSION
/

CREATE OR REPLACE PACKAGE discussion IS
------------------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Robin       04-May-00  Moved existing procedures to package.
-- Robin       11-May-00  Changed error codes to coincide with new table.
-- Adam        22-May-00  added new parameter to create_discussion_group
-- Roger       07-Jan-01  Added initial post to new group. Added group copy.
-----------------------------------------------------------------------------

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

/**
 * Does NOT commit or rollback
 */
Procedure CREATE_DISCUSSION_GROUP
(
      i_discussion_group_id IN NUMBER,
      i_space_id IN VARCHAR2,
      i_object_id IN VARCHAR2,
      i_discussion_group_name IN VARCHAR2,
      i_person_id IN NUMBER,
      i_discussion_group_description IN VARCHAR2,
      i_create_welcome_post IN NUMBER,
      i_welcome_message_subject IN VARCHAR2,
      o_discussion_group_id OUT NUMBER,
      o_welcome_message_post_clob OUT CLOB,
      o_discussion_grp_charter_clob OUT CLOB
);

Procedure CREATE_POST
(
    i_space_id IN NUMBER,
    i_discussion_group_id IN NUMBER,
    i_parent_id IN NUMBER,
    i_person_id  IN NUMBER,
    i_subject IN VARCHAR2,
    i_urgency_id IN NUMBER,
    i_post_body_id IN NUMBER,
    o_post_date OUT DATE,
    o_post_id OUT NUMBER
);

Procedure UPDATE_POST_READER
(
    i_person_id IN NUMBER,
    i_discussion_group_id IN NUMBER,
    i_post_id IN NUMBER,
    o_status OUT NUMBER
);


----------------------------------------------------------------------
-- LOG_EVENT
-- Logs the event that happens to the NEWS item
----------------------------------------------------------------------
   PROCEDURE log_event
    (
        discussion_group_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    );
----------------------------------------------------------------------

----------------------------------------------------------------------

----------------------------------------------------------------------
-- COPY_ALL
-- Copies all discussion groups for a space to another space
----------------------------------------------------------------------
   procedure copy_all (
        i_from_space_id     in varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        o_return_value      out number);


----------------------------------------------------------------------
-- COPY_FORM
-- Copies a single discussion group from one space to another space.
-- New objects ids are assigned so that the two discussion groups
-- are completely independant copies of each other.
----------------------------------------------------------------------
    procedure copy_discussion_group (
        i_from_space_id in number,
        i_to_space_id in number,
        i_discussion_group_id in number,
        i_created_by_id in number);

    function object_has_discussion(
        i_object_id in  pn_object.object_id%type) return number;


END; -- Package Specification DISCUSSION
/

CREATE OR REPLACE PACKAGE BODY document IS
-----------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- Robin       032900  Created package body for Phil's procedures
-- Adam        052200  Parameters to CREATE_DISCUSSION_GROUP changed
-- Tim         091201  Overhauled document copy procedures
--                     due to Java-ization of document copy
-- Matt        080702  Integrated get_path_for_object and
--                     get_container_list provided by VerticalBuilder
--                     to eliminate use of pn_tmp_doc_path_info
-----------------------------------------------------------------


-----------------------------------------------------------------
-- CREATE_BOOKMARK
-----------------------------------------------------------------
PROCEDURE create_bookmark
(
        i_name in varchar2,
        i_description in varchar2,
        i_url in varchar2,
        i_container_id in number,
        i_status_id in number,
        i_owner_id in number,
        i_comments in varchar2,
        i_created_by_id in number,
        o_status out number,
        o_object_id out number
)   IS

    -- general variables

    v_sysdate DATE := SYSDATE;
    v_space_id number(20);


    -- debugging / error logging variables

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CREATE_BOOKMARK';

BEGIN

    o_object_id := base.create_object (G_bookmark_object_type, i_created_by_id, 'A');

    insert into pn_bookmark
    (
        BOOKMARK_ID,
        NAME,
        DESCRIPTION,
        URL,
        STATUS_ID,
        OWNER_ID,
        COMMENTS,
        MODIFIED_DATE,
        MODIFIED_BY_ID,
        RECORD_STATUS,
        CRC
    ) values (
        o_object_id,
        i_name,
        i_description,
        i_url,
        i_status_id,
        i_owner_id,
        i_comments,
        v_sysdate,
        i_created_by_id,
        G_active_record_status,
        v_sysdate
    );

    -- apply default object permissions for the document
    v_space_id := get_space_for_container_id (i_container_id);

    SECURITY.APPLY_DOCUMENT_PERMISSIONS (o_object_id, i_container_id, G_bookmark_object_type, v_space_id, i_created_by_id);

    -- finally, add this bookmark to it's parent container
    add_object_to_container (o_object_id, i_container_id);
    o_status := 0;

EXCEPTION

    WHEN OTHERS THEN

        o_status := SQLCODE;
        o_object_id := -1;

        err_num := o_status;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

END;



-----------------------------------------------------------------
-- MODIFY_BOOKMARK
-----------------------------------------------------------------
PROCEDURE modify_bookmark
(
        i_id in number,
        i_name in varchar2,
        i_description in varchar2,
        i_url in varchar2,
        i_container_id in number,
        i_status_id in number,
        i_owner_id in number,
        i_comments in varchar2,
        i_created_by_id in number,
        o_status out number
)   IS

    -- general variables

    v_sysdate DATE := SYSDATE;


    -- debugging / error logging variables

    stored_proc_name VARCHAR2(100):= 'DOCUMENT.MODIFY_BOOKMARK';

BEGIN

    update pn_bookmark
    set
        name = i_name,
        description = i_description,
        url = i_url,
        status_id = i_status_id,
        owner_id = i_owner_id,
        comments = i_comments,
        modified_date = v_sysdate,
        modified_by_id = i_created_by_id,
        record_status = 'A',
        crc = v_sysdate
    where
        bookmark_id = i_id;

    o_status := 0;

EXCEPTION

    WHEN OTHERS THEN

        o_status := SQLCODE;
        BASE.LOG_ERROR (stored_proc_name, o_status, SQLERRM);

END;



procedure create_doc_space
(
    i_in_space_id in pn_doc_space.doc_space_id%type,
    i_creator_id in pn_person.person_id%type,
    o_doc_space_id out number,
    o_return_value out number
) as

    v_doc_space_id pn_doc_space.doc_space_id%type;

BEGIN

     -- create doc_space object
    v_doc_space_id := BASE.CREATE_OBJECT('doc_space', 1, 'A');

    INSERT INTO pn_doc_space
        (doc_space_id, doc_space_name, crc, record_status)
      VALUES
        (v_doc_space_id, 'default', SYSDATE, 'A');

    INSERT INTO pn_space_has_doc_space
        (space_id, doc_space_id, is_owner)
      VALUES
        (i_in_space_id, v_doc_space_id, 1);

    -- link new doc_space to doc provider
    INSERT INTO pn_doc_provider_has_doc_space
        (doc_provider_id, doc_space_id)

      SELECT doc_provider_id, v_doc_space_id
        FROM pn_doc_provider
        WHERE is_default = 1;

    o_doc_space_id := v_doc_space_id;
    o_return_value := BASE.OPERATION_SUCCESSFUL;

EXCEPTION
    when others then
        o_return_value := BASE.PLSQL_EXCEPTION;


END;



-----------------------------------------------------------------
-- CREATE_DOC
-----------------------------------------------------------------

PROCEDURE create_doc
    (
        tmp_doc IN varchar2,
        container_id IN varchar2,
        whoami IN varchar2,
        space_id IN varchar2,
        ignore_name_constraint IN NUMBER,
        i_discussion_group_description IN VARCHAR2,
        v_return_value OUT NUMBER
    )

IS
    -- variable declaration

    v_tmp_doc_id      pn_tmp_document.tmp_doc_id%type := TO_NUMBER(tmp_doc);
    v_container_id    pn_doc_container.doc_container_id%type := TO_NUMBER(container_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_space_id        pn_project_space.project_id%type := TO_NUMBER(space_id);
    v_document_id     pn_document.doc_id%type;
    v_version_id      pn_doc_version.doc_version_id%type;
    v_history_id      pn_doc_history.doc_history_id%type;
    v_content_id      pn_doc_content_element.doc_content_id%type;
    v_tmp_doc_rec     pn_tmp_document%rowtype;
    v_discussion_group_id   pn_discussion_group.discussion_group_id%type;
    --v_status          NUMBER;
    v_welcome_message_post_clob   pn_post_body_clob.clob_field%type;
    v_discussion_grp_charter_clob pn_discussion_group.discussion_group_charter_clob%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CREATE';

-- global (static) variable declaration

    G_initial_version number := 1;
    G_initial_label   pn_tmp_document.doc_version_label%type := 'Initial Version';
    G_action          pn_doc_history.action%type := 'create';
    G_action_comment  pn_doc_history.action_comment%type := 'Created new document: ';

BEGIN

    SET TRANSACTION READ WRITE;

    -- document_id is set to the value of the tmp_doc_id.
    -- Needed because JDBC can't deal with returns.  -Roger 9/8/99
    v_document_id := v_tmp_doc_id;
    -- doc_id := v_tmp_doc_id;

    -- SELECT pn_object_sequence.nextval INTO v_document_id FROM dual;
    SELECT pn_object_sequence.nextval INTO v_version_id FROM dual;
    SELECT pn_object_sequence.nextval INTO v_content_id FROM dual;
    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- get a tmp_document record
    select * INTO v_tmp_doc_rec from pn_tmp_document where tmp_doc_id = v_tmp_doc_id;


    IF ((ignore_name_constraint = 1) OR
        (document.f_verify_unique_name (v_tmp_doc_rec.doc_name, v_container_id, G_document_object_type)))
        THEN

      -- "register" the document object
      insert into pn_object (
          object_id,
          date_created,
          object_type,
          created_by,
          record_status )
      values (
          v_document_id,
          SYSDATE,
          G_document_object_type,
          v_whoami,
          G_active_record_status);


      -- insert the document object
      insert into pn_document
      (
          doc_id,
          doc_name,
          doc_description,
          doc_type_id,
          doc_status_id,
          current_version_id,
          record_status,
          crc
      ) values
      (
          v_document_id,
          v_tmp_doc_rec.doc_name,
          v_tmp_doc_rec.doc_description,
          v_tmp_doc_rec.doc_type_id,
          v_tmp_doc_rec.doc_status_id,
          v_version_id,
          G_active_record_status,
          SYSDATE
      );

      -- apply default object permissions for the document
      SECURITY.APPLY_DOCUMENT_PERMISSIONS (v_document_id, v_container_id, G_document_object_type, v_space_id, v_whoami);


      -- register the "document_version" object
      insert into pn_object (
          object_id,
          date_created,
          object_type,
          created_by,
          record_status )
      values (
          v_version_id,
          SYSDATE,
          G_document_version_object_type,
          v_whoami,
          G_active_record_status);

      -- create a document version
      insert into pn_doc_version (
          doc_id,
          doc_version_id,
          doc_version_num,
          doc_version_label,
          doc_version_name,
          source_file_name,
          short_file_name,
          doc_author_id,
          doc_description,
          date_modified,
          modified_by_id,
          doc_comment,
          crc,
          record_status)
      values (
          v_document_id,
          v_version_id,
          G_initial_version,
          G_initial_label,
          v_tmp_doc_rec.doc_name,
          v_tmp_doc_rec.source_file_name,
          v_tmp_doc_rec.short_file_name,
          v_tmp_doc_rec.doc_author_id,
          v_tmp_doc_rec.doc_description,
          SYSDATE,
          v_whoami,
          v_tmp_doc_rec.doc_comment,
          SYSDATE,
          G_active_record_status
      );

      -- no need to apply security permissions to the the version

      -- create the content element(s) for the document version
      insert into pn_doc_content_element (
          doc_content_id,
          doc_format_id,
          record_status,
          file_size,
          file_handle,
          repository_id)
      values (
          v_content_id,
          v_tmp_doc_rec.doc_format_id,
          G_active_record_status,
          v_tmp_doc_rec.file_size,
          v_tmp_doc_rec.file_handle,
          v_tmp_doc_rec.repository_id
      );

      -- link the content to the doc_version
      insert into pn_doc_version_has_content (
          doc_id,
          doc_version_id,
          doc_content_id)
      values (
          v_document_id,
          v_version_id,
          v_content_id
      );

      -- insert the doc into the correct container
      insert into pn_doc_container_has_object (
          doc_container_id,
          object_id)
      values (
          v_container_id,
          v_document_id
      );

      -- create the docs discussion group
      -- We don't stream any data to the charter CLOB nor the welcome message
      DISCUSSION.CREATE_DISCUSSION_GROUP(
        NULL,
        v_space_id,
        v_document_id,
        v_tmp_doc_rec.doc_name,
        v_whoami,
        i_discussion_group_description ,
        0, -- do not create a welcome message
        NULL,
        v_discussion_group_id,
        v_welcome_message_post_clob,
        v_discussion_grp_charter_clob);

      -- apply the documents permissions to the discussion group
      SECURITY.APPLY_DOCUMENT_PERMISSIONS(
        v_discussion_group_id,
        v_document_id,
        'discussion_group',
        v_space_id,
        v_whoami);

      v_return_value := 0;
      COMMIT;
    ELSE
      v_return_value := 5001;
      ROLLBACK;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        v_return_value := 2000;
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;


END;  -- Procedure CREATE_DOC


-----------------------------------------------------------------
-- IS_PARENT_CONTAINER
-----------------------------------------------------------------


FUNCTION IS_PARENT_CONTAINER
( parent_container_id IN varchar2,
  child_container_id IN varchar2
)
    RETURN NUMBER

   IS

        v_parent_id     pn_doc_container.doc_container_id%type := to_number(parent_container_id);
        v_child_id      pn_doc_container.doc_container_id%type := to_number(child_container_id);

        v_return_value  NUMBER := 0;

        v_tmp_id        pn_doc_container.doc_container_id%type;

        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'DOCUMENT.IS_PARENT_CONTAINER';

BEGIN

    select doc_container_id into v_tmp_id from pn_doc_container_has_object
        where object_id = v_child_id;

    if (v_parent_id = v_tmp_id) then
       v_return_value := 1;
    end if;

    while (v_return_value = 0 AND v_tmp_id IS NOT NULL)
    loop

        select doc_container_id into v_tmp_id from pn_doc_container_has_object
            where object_id = v_tmp_id;

        if (v_parent_id = v_tmp_id) then
            v_return_value := 1;
        end if;

    end loop; -- end loop

    return v_return_value;

 EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
  END;

END; -- is_parent_container

-----------------------------------------------------------------
-- GET_PATH_FOR_OBJECT
-----------------------------------------------------------------
FUNCTION GET_PATH_FOR_OBJECT ( object_id IN VARCHAR2, root_object_id IN varchar2 )
  RETURN ReferenceCursor IS
    v_object_id pn_object.object_id%type := to_number ( object_id );
    v_object_type pn_object.object_type%type;
    v_object_name pn_document.doc_name%type;
    v_root_container_id pn_object.object_id%type := to_number(root_object_id);
    v_collection_id NUMBER;
    v_reached_top_level BOOLEAN := FALSE;
    err_num NUMBER;
    err_msg VARCHAR2 ( 120 );
    stored_proc_name VARCHAR2 ( 100 ) := 'DOCUMENT.GET_PATH_FOR_OBJECT';
    pathInfo ReferenceCursor;
BEGIN
    SELECT pn_object_sequence.NEXTVAL
      INTO v_collection_id
      FROM dual;

    -- now that we have collected the path for this document...
    OPEN pathInfo FOR
    select path.parent_id container_id,
           o.object_id,
           decode(o.object_type,
                  G_document_object_type,d.doc_name,
                  G_container_object_type,c.container_name,
                  null) object_name,
           v_collection_id,
           path.depth
    from pn_doc_container c,
         pn_document d,
         pn_object o,
         (select doc_container_id as parent_id,
                 object_id,
                 level depth
          from pn_doc_container_has_object
          start with object_id = v_object_id
          connect by prior doc_container_id = object_id) path
    where
        c.doc_container_id(+) = o.object_id
        and d.doc_id(+) = o.object_id
        and o.object_id = path.object_id
        and c.is_hidden = 0
    union
    select to_number(null),
           c.doc_container_id,
           c.container_name,
           v_collection_id,
           to_number(null)
    from pn_doc_container c
    where
        c.doc_container_id = v_root_container_id
    order by 5;

    RETURN pathInfo;
EXCEPTION
    WHEN OTHERS THEN
    BEGIN
        ROLLBACK;
        err_num := SQLCODE;
        err_msg := SUBSTR ( SQLERRM,
                    1,
                    120 );
        INSERT INTO pn_sp_error_log
        VALUES ( SYSDATE,
                 stored_proc_name,
                 err_num,
                 err_msg );
        COMMIT;
    END;
END;

-----------------------------------------------------------------
-- GET_PARENT_CONTAINER_INFO
-----------------------------------------------------------------

FUNCTION GET_PARENT_CONTAINER_INFO
( object_id IN varchar2)
    RETURN ReferenceCursor

   IS

    v_object_id         pn_object.object_id%type := to_number(object_id);
    v_container_id      pn_doc_container.doc_container_id%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.GET_PARENT_CONTAINER_INFO';

    containerInfo            ReferenceCursor;

BEGIN

    BEGIN
        select doc_container_id into v_container_id from pn_doc_container_has_object
            where object_id = v_object_id;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_container_id := null;

    END;

    open containerInfo for
       select doc_container_id, container_name, container_description, date_modified,
           modified_by_id, is_hidden, crc, record_status
           from pn_doc_container where doc_container_id = v_container_id;


    RETURN containerInfo;

 EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;


END; -- Procedure GET_PARENT_CONTAINER_INFO


-----------------------------------------------------------------
-- get_parent_space_for_object
-----------------------------------------------------------------

FUNCTION get_parent_space_for_object
( object_id IN varchar2)
 RETURN ReferenceCursor

   IS

    v_object_id         pn_object.object_id%type := to_number(object_id);
    v_container_id     pn_object.object_id%type;
    v_doc_space_id     pn_object.object_id%type;
    v_space_id         pn_object.object_id%type;
    v_object_type      pn_object.object_type%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.GET_PARENT_SPACE_FOR_OBJECT';

    spaceInfo            ReferenceCursor;

    G_project_space_type    pn_object.object_type%type := 'project';
    G_personal_space_type    pn_object.object_type%type := 'person';
    G_business_space_type    pn_object.object_type%type := 'business';



BEGIN

    -- first get the ID of this object's container
    select doc_container_id into v_container_id from pn_doc_container_has_object
        where object_id = v_object_id;

    -- then get the ID of the doc_space_which contains the "container"
    select doc_space_id into v_doc_space_id from pn_doc_space_has_container
        where doc_container_id = v_container_id;

    -- now get the space which 'owns' the doc space
    select space_id into v_space_id from pn_space_has_doc_space
        where doc_space_id = v_doc_space_id;

    -- now get the object type of the space
    select object_type into v_object_type from pn_object
        where object_id = v_space_id;


    if (v_object_type = G_project_space_type) then
        open spaceInfo for
            select project_id as object_id, project_name as name, 'Project' as object_type from pn_project_space
            where project_id = v_space_id;

    elsif (v_object_type = G_business_space_type) then
        open spaceInfo for
            select business_id as object_id, business_name as name, 'Business' as object_type from pn_business
            where business_id = v_space_id;

    elsif (v_object_type = G_personal_space_type) then
        open spaceInfo for
            select person_id as object_id, 'Personal Space' as name, 'Personal' as object_type from pn_person
            where person_id = v_space_id;

    end if;

    RETURN spaceInfo;

 EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;


END; -- Procedure get_parent_space_for_object





-----------------------------------------------------------------
-- GET_CONTAINER_LIST
-----------------------------------------------------------------

FUNCTION GET_CONTAINER_LIST ( doc_space_id IN pn_doc_space.doc_space_id%type )
  RETURN ReferenceCursor IS
    v_doc_space_id pn_doc_space.doc_space_id%type := to_number ( doc_space_id );
    v_object_id pn_object.object_id%type;
    v_object_name pn_doc_container.container_name%type;
    v_parent_id pn_doc_container.doc_container_id%type;
    v_parent_name pn_doc_container.container_name%type;
    v_root_container_id pn_doc_container.doc_container_id%type;
    v_is_root NUMBER := 0;
    v_is_hidden NUMBER := 0;
    v_collection_id pn_object.object_id%type;
    err_num NUMBER;
    err_msg VARCHAR2 ( 120 );
    stored_proc_name VARCHAR2 ( 100 ) := 'DOCUMENT.GET_CONTAINER_LIST';
    containerList ReferenceCursor;
BEGIN
    SELECT pn_object_sequence.NEXTVAL
      INTO v_collection_id
      FROM dual;
    SELECT doc_container_id
      INTO v_root_container_id
      FROM pn_doc_space_has_container
     WHERE doc_space_id = v_doc_space_id
       AND is_root = 1;
     OPEN containerList FOR
         select o.doc_container_id as object_id,
                o.container_name as object_name,
                p.doc_container_id as parent_id,
                p.container_name as parent_name,
                0 as is_root
         from pn_doc_container p, pn_doc_container o,
              (select doc_container_id as parent_id,
                      object_id
               from pn_doc_container_has_object tree
               where exists (select c.doc_container_id
                             from pn_doc_space_has_container dshc,
                                  pn_doc_container c
                             where dshc.doc_space_id = v_doc_space_id
                             and dshc.doc_container_id = c.doc_container_id
                             and c.doc_container_id = tree.object_id
                             and  c.record_status = 'A'
                            )
               start with doc_container_id = v_root_container_id
               connect by prior object_id = doc_container_id) path
         where p.doc_container_id = path.parent_id
         and o.record_status = 'A'
         and o.is_hidden <> 1
         and o.doc_container_id = path.object_id
         union
         select c.doc_container_id,
                c.container_name,
                to_number(null),
                null,
                1
         from pn_doc_container c
         where c.doc_container_id = v_root_container_id;
    RETURN containerList;
EXCEPTION
    WHEN OTHERS THEN
    BEGIN
        ROLLBACK;
        err_num := SQLCODE;
        err_msg := SUBSTR ( SQLERRM,
                    1,
                    120 );
        INSERT INTO pn_sp_error_log
        VALUES ( SYSDATE,
                 stored_proc_name,
                 err_num,
                 err_msg );
        COMMIT;
    END;
END;

-----------------------------------------------------------------
-- CREATE_CONTAINER
-----------------------------------------------------------------

PROCEDURE create_container
    (
        in_container_id IN varchar2,
        whoami IN varchar2,
        folder_name IN varchar2,
        description IN varchar2,
        space_id    IN  varchar2,
        is_hidden IN varchar2,
        object_id OUT number,
        v_return_value OUT number
    )

IS

-- variable declaration

    v_in_container    pn_doc_container.doc_container_id%type := TO_NUMBER(in_container_id);
    v_doc_space_id    pn_doc_space.doc_space_id%type;
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_space_id        pn_project_space.project_id%type := TO_NUMBER(space_id);
    v_folder_name     pn_doc_container.container_name%type := folder_name;
    v_description     pn_doc_container.container_description%type := description;
    v_is_hidden       pn_doc_container.is_hidden%type := is_hidden;
    v_new_id          pn_doc_container.doc_container_id%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CREATE_CONTAINER';



BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_new_id FROM dual;

    SELECT distinct doc_space_id into v_doc_space_id
        from pn_doc_space_has_container where doc_container_id = v_in_container;


    IF (document.f_verify_unique_name (v_folder_name, v_in_container, G_container_object_type)) THEN

      -- register new container in pn_object

      insert into pn_object (
          object_id,
          date_created,
          object_type,
          created_by,
          record_status )
      values (
          v_new_id,
          SYSDATE,
          G_container_object_type,
          v_whoami,
          G_active_record_status);

      -- create new document container

      insert into pn_doc_container (
          doc_container_id,
          container_name,
          container_description,
          record_status,
          is_hidden,
          modified_by_id,
          date_modified,
          crc)
      values (
          v_new_id,
          v_folder_name,
          v_description,
          G_active_record_status,
          v_is_hidden,
          v_whoami,
          SYSDATE,
          SYSDATE);

      -- apply default object permissions for the object
      SECURITY.APPLY_DOCUMENT_PERMISSIONS (v_new_id, v_in_container, G_document_version_object_type, v_space_id, v_whoami);

     -- register in doc_space_has_container

     insert into pn_doc_space_has_container (
          doc_space_id,
          doc_container_id,
          is_root)
      values (
          v_doc_space_id,
          v_new_id,
          0);


     -- make the new folder an object of the exiting folder

      insert into pn_doc_container_has_object (
          doc_container_id,
          object_id)
      values (
          v_in_container,
          v_new_id);

      object_id := v_new_id;

      v_return_value := 0;
      COMMIT;

   ELSE
      object_id := -1;
      v_return_value := 5001;
      rollback;
   END IF;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        v_return_value := 2000;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END;  -- Procedure CREATE_CONTAINER


-----------------------------------------------------------------
-- MODIFY_CONTAINER
-----------------------------------------------------------------

PROCEDURE modify_container
    (
        parent_id IN varchar2,
        my_container_id IN varchar2,
        folder_name IN varchar2,
        description in varchar2,
        whoami IN varchar2,
        is_hidden IN varchar2,
        orig_crc IN DATE,
        v_return_value OUT number
    )
IS

    -- variable declaration
    v_parent_id         pn_doc_container.doc_container_id%type := TO_NUMBER(parent_id);
    v_container_id      pn_doc_container.doc_container_id%type := TO_NUMBER(my_container_id);
    v_whoami            pn_person.person_id%type := TO_NUMBER(whoami);
    v_current_crc       pn_doc_container.crc%type;
    v_current_name      pn_doc_container.container_name%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.MODIFY_CONTAINER';


BEGIN
    SET TRANSACTION READ WRITE;

    select crc into v_current_crc from pn_doc_container where doc_container_id = v_container_id;

    IF (document.f_verify_crc (orig_crc, v_current_crc)) THEN
        select container_name into v_current_name from pn_doc_container
            where doc_container_id = v_container_id;

        IF ((folder_name = v_current_name) OR
            document.f_verify_unique_name (folder_name, v_parent_id, G_container_object_type)) THEN

            UPDATE pn_doc_container
            SET
                container_name = folder_name,
                container_description = description,
                is_hidden = is_hidden,
                modified_by_id = v_whoami,
                date_modified = SYSDATE
            WHERE
                doc_container_id = v_container_id;

            v_return_value := 0;
         ELSE
		IF ((folder_name!=v_current_name)AND(lower(folder_name) = lower(v_current_name))) THEN
			UPDATE pn_doc_container
			SET
    				container_name = folder_name,
    				container_description = description,
    				is_hidden = is_hidden,
    				modified_by_id = v_whoami,
    				date_modified = SYSDATE
			WHERE
    				doc_container_id = v_container_id;
    			v_return_value := 0;
		ELSE
  			v_return_value := 5001;
		END IF;
          END IF;
     ELSE
        v_return_value := 1000;
     END IF;

    if (v_return_value = 0) THEN
        COMMIT;
    else
        ROLLBACK;
    END IF;

    EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        v_return_value := 2000;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;



END; -- Procedure MODIFY_CONTAINER

-----------------------------------------------------------------
-- ADD_OBJECT_TO_CONTAINER
-----------------------------------------------------------------

PROCEDURE add_object_to_container
    (
        in_object_id IN varchar2,
        in_container_id IN varchar2
    )

IS

    -- variable declaration

    v_in_container    pn_doc_container.doc_container_id%type := TO_NUMBER(in_container_id);
    v_object_id       pn_doc_container.doc_container_id%type := TO_NUMBER(in_object_id);

BEGIN

    insert into pn_doc_container_has_object (
        doc_container_id,
        object_id)
    values (
        v_in_container,
        v_object_id);

EXCEPTION
    WHEN OTHERS THEN
        BEGIN
            RAISE;
        END;

END;  -- Procedure ADD_OBJECT_TO_CONTAINER


-----------------------------------------------------------------
-- CHECK_IN
-----------------------------------------------------------------

PROCEDURE check_in
    (
        tmp_doc IN varchar2,
        whoami  IN varchar2,
        space_id IN varchar2,
        orig_crc IN DATE,
        v_return_value OUT number
    )

IS

-- variable declaration

    v_tmp_doc_id      pn_tmp_document.tmp_doc_id%type := TO_NUMBER(tmp_doc);
    v_doc_id          pn_document.doc_id%type;
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_space_id        pn_project_space.project_id%type := TO_NUMBER(space_id);
    v_version_num     pn_doc_version.doc_version_num%type;
    v_version_id      pn_doc_version.doc_version_id%type;
    v_content_id      pn_doc_content_element.doc_content_id%type;
    v_tmp_doc_rec     pn_tmp_document%rowtype;
    v_doc_properties  pn_document%rowtype;
    v_current_version_rec   pn_doc_version%rowtype;

    v_current_crc   DATE;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CHECK_IN';


-- global (static) variable declaration

    G_active_record_status   pn_document.record_status%type := 'A';

BEGIN

    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_version_id FROM dual;
    SELECT pn_object_sequence.nextval INTO v_content_id FROM dual;

      -- get a tmp_document record
      select * INTO v_tmp_doc_rec from pn_tmp_document where tmp_doc_id = v_tmp_doc_id;
      v_doc_id := v_tmp_doc_rec.doc_id;

--     dbms_output.put_line('GOT TMP_DOC_REC');

     select crc into v_current_crc from pn_document where doc_id = v_tmp_doc_rec.doc_id;

    -- dbms_output.put_line('GOT TMP_DOC_REC');

    IF (document.f_verify_crc (orig_crc, v_current_crc)) THEN




      -- get the properties for the document
      select * INTO v_doc_properties from pn_document where doc_id = v_doc_id;

      -- get the current version info for the document
      select * INTO v_current_version_rec from pn_doc_version
          where doc_id = v_doc_id and doc_version_id = v_doc_properties.current_version_id;

       -- increment the current version id
       v_version_num := v_current_version_rec.doc_version_num + 1;

      -- register the document version
      insert into pn_object (
          object_id,
          date_created,
          object_type,
          created_by,
          record_status )
      values (
          v_version_id,
          SYSDATE,
          G_document_version_object_type,
          v_whoami,
          G_active_record_status);


      -- create a document version
      insert into pn_doc_version (
          doc_id,
          doc_version_id,
          doc_version_num,
          doc_version_label,
          doc_version_name,
          source_file_name,
          short_file_name,
          doc_author_id,
          doc_description,
          date_modified,
          modified_by_id,
          doc_comment,
          crc,
          record_status)
      values (
          v_doc_id,
          v_version_id,
          v_version_num,
          NULL,
          v_doc_properties.doc_name,
          v_tmp_doc_rec.source_file_name,
          v_tmp_doc_rec.short_file_name,
          v_current_version_rec.doc_author_id,
          v_current_version_rec.doc_description,
          SYSDATE,
          v_whoami,
          v_tmp_doc_rec.doc_comment,
          SYSDATE,
          G_active_record_status
      );

      -- no need to apply security permissions to the version

      -- update the document current_version_id
      update pn_document set
          current_version_id = v_version_id,
          doc_status_id = v_tmp_doc_rec.doc_status_id
      where doc_id = v_doc_id;


      -- create the content element(s) for the document version
      insert into pn_doc_content_element (
          doc_content_id,
          doc_format_id,
          record_status,
          file_size,
          file_handle,
          repository_id)
      values (
          v_content_id,
          v_tmp_doc_rec.doc_format_id,
          G_active_record_status,
          v_tmp_doc_rec.file_size,
          v_tmp_doc_rec.file_handle,
          v_tmp_doc_rec.repository_id
      );

      -- link the content to the doc_version
      insert into pn_doc_version_has_content (
          doc_id,
          doc_version_id,
          doc_content_id)
      values (
          v_doc_id,
          v_version_id,
          v_content_id
      );

      v_return_value := 0;
      COMMIT;
    ELSE
      v_return_value := 1000;
      ROLLBACK;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        v_return_value := 2000;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;


END;  -- Procedure check_in


-----------------------------------------------------------------
-- CHECK_OUT
-----------------------------------------------------------------

PROCEDURE check_out
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        cko_due IN date,
        notes IN varchar2,
        orig_crc IN DATE,
        v_return_value OUT number

    )

IS
    v_document_id     pn_document.doc_id%type := TO_NUMBER(doc_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_cko_due         pn_doc_version.checkout_due%type := cko_due;
    v_notes           pn_doc_version.doc_comment%type := notes;
    v_history_id      pn_doc_history.doc_history_id%type;

    v_current_crc     DATE;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CHECK_OUT';



BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    select crc into v_current_crc from pn_document where doc_id = v_document_id;

    IF (document.f_verify_crc (orig_crc, v_current_crc)) THEN

        -- update the current version record to reflect the check out
        UPDATE
            pn_doc_version
        SET
            is_checked_out = 1,
            checked_out_by_id = v_whoami,
    	    checkout_due = v_cko_due,
            doc_comment = v_notes,
            date_checked_out = SYSDATE,
            crc = SYSDATE
	   WHERE
            doc_id = v_document_id
    	AND
            doc_version_id = (select current_version_id from pn_document where doc_id = v_document_id);

        v_return_value := 0;
        COMMIT;
    ELSE
        v_return_value := 1000;
        ROLLBACK;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        v_return_value := 2000;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END;  -- Procedure CHECK_OUT

-----------------------------------------------------------------
-- REMOVE_DOC
-----------------------------------------------------------------

PROCEDURE remove_doc
    (
        doc_id IN varchar2,
        last_modified IN date,
        record_status IN varchar2,
        whoami IN varchar2
    )

IS
    v_doc_id          pn_document.doc_id%type := TO_NUMBER(doc_id);
    v_last_modified   pn_doc_version.date_modified%type := last_modified;
    v_record_status   pn_document.record_status%type := record_status;
    v_whoami          pn_doc_version.modified_by_id%type := TO_NUMBER(whoami);

    v_current_version_id    pn_document.current_version_id%type;

BEGIN
    SET TRANSACTION READ WRITE;

    select current_version_id INTO v_current_version_id from pn_document where doc_id = v_doc_id;

    update
        pn_document
    set
        record_status = v_record_status
    where
        doc_id = v_doc_id;

    update
        pn_doc_version
    set
        date_modified = v_last_modified,
        modified_by_id = v_whoami
    where
        doc_version_id = v_current_version_id;

COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        rollback ;

END;  -- Procedure REMOVE_DOC

-----------------------------------------------------------------
-- REMOVE_CONTAINER
-----------------------------------------------------------------

PROCEDURE remove_container
    (
        container_id IN varchar2,
        record_status IN varchar2,
        whoami IN varchar2
    )

IS
    v_container_id          pn_doc_container.doc_container_id%type := TO_NUMBER(container_id);
    v_record_status         pn_document.record_status%type := record_status;
    v_whoami                pn_person.person_id%type := TO_NUMBER(whoami);

 BEGIN
    SET TRANSACTION READ WRITE;

    update
        pn_doc_container
    set
        record_status = v_record_status,
        modified_by_id = v_whoami,
        date_modified = SYSDATE
    where
        doc_container_id = v_container_id;


COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        rollback ;

END;  -- Procedure REMOVE_CONTAINER

-----------------------------------------------------------------
-- LOG_EVENT
-----------------------------------------------------------------

PROCEDURE log_event
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    )

IS
    v_document_id     pn_document.doc_id%type := TO_NUMBER(doc_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_doc_history.doc_history_id%type;
    v_action          pn_doc_history.action%type := action;
    v_action_name     pn_doc_history.action_name%type := action_name;
    v_action_comment  pn_doc_history.action_comment%type := notes;

BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- insert a history record for this document

    insert into pn_doc_history (
        doc_id,
        doc_history_id,
        action,
        action_name,
        action_by_id,
        action_date,
        action_comment)
    values (
        v_document_id,
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
        DBMS_OUTPUT.put_line('exception');
        rollback ;
    END;
END;  -- Procedure LOG_EVENT

-----------------------------------------------------------------
-- MOVE_OBJECT
-----------------------------------------------------------------

PROCEDURE move_object
    (
        in_object_id IN varchar2,
        in_container_id IN varchar2
    )

IS

    -- variable declaration

    v_new_container_id    pn_doc_container.doc_container_id%type := TO_NUMBER(in_container_id);
    v_object_id       pn_doc_container.doc_container_id%type := TO_NUMBER(in_object_id);
    v_current_container_id  pn_doc_container.doc_container_id%type;
BEGIN
    SET TRANSACTION READ WRITE;

    select doc_container_id into v_current_container_id from pn_doc_container_has_object
    where object_id = v_object_id;

    delete from pn_doc_container_has_object
        where
            doc_container_id = v_current_container_id
        and
            object_id = v_object_id;

     insert into pn_doc_container_has_object (doc_container_id, object_id)
        values (v_new_container_id, v_object_id);


   COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        rollback ;

END;  -- Procedure MOVE_OBJECT

-----------------------------------------------------------------
-- UNDO_CHECK_OUT
-----------------------------------------------------------------

PROCEDURE undo_check_out
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        orig_crc IN date,
        v_return_value OUT number

    )

IS
    v_doc_id          pn_document.doc_id%type := TO_NUMBER(doc_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);

    v_current_crc     DATE;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CHECK_OUT';


BEGIN
    SET TRANSACTION READ WRITE;

    select crc into v_current_crc from pn_document where doc_id = v_doc_id;

    IF (document.f_verify_crc (orig_crc, v_current_crc)) THEN

      update
         pn_doc_version
      set
         is_checked_out = 0,
  	   checked_out_by_id = NULL,
  	   checkout_due = NULL,
  	   date_checked_out = NULL,
  	   crc = SYSDATE
      where
  	   doc_id = v_doc_id
      and
  	   doc_version_id = (select current_version_id from pn_document
  				where doc_id = v_doc_id);

      v_return_value := 0;
      COMMIT;
    ELSE
      v_return_value := 1000;
      ROLLBACK;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        v_return_value := 2000;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;
END;  -- Procedure UNDO_CHECK_OUT

-----------------------------------------------------------------
-- MODIFY_PROPERTIES
-----------------------------------------------------------------

PROCEDURE modify_properties
    (
        tmp_doc IN varchar2,
        whoami  IN varchar2,
        orig_crc IN date,
        return_value OUT NUMBER
    )

IS

-- variable declaration

    v_tmp_doc_id      pn_tmp_document.tmp_doc_id%type := TO_NUMBER(tmp_doc);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_doc_history.doc_history_id%type;
    v_tmp_doc_rec     pn_tmp_document%rowtype;
    v_current_crc     DATE;
    v_container_id    pn_doc_container.doc_container_id%type;

    v_name          pn_document.doc_name%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.MODIFY_PROPERTIES';

    -- global (static) variable declaration

    G_active_record_status   pn_document.record_status%type := 'A';
    G_action          pn_doc_history.action%type := 'modify_properties';
    G_action_comment  pn_doc_history.action_comment%type := 'Modified Document Properties: ';

BEGIN

    SET TRANSACTION READ WRITE;

    -- get an ID for the history record
    select pn_object_sequence.nextval into v_history_id from dual;

    -- get a tmp_document record
    select * INTO v_tmp_doc_rec from pn_tmp_document where tmp_doc_id = v_tmp_doc_id;
    select crc into v_current_crc from pn_document where doc_id = v_tmp_doc_rec.doc_id;
    select doc_name into v_name from pn_document where doc_id = v_tmp_doc_rec.doc_id;

    IF (document.f_verify_crc (orig_crc, v_current_crc)) THEN

        select distinct doc_container_id into v_container_id from pn_doc_container_has_object where object_id = v_tmp_doc_rec.doc_id;

        IF ((LOWER(v_tmp_doc_rec.doc_name) = LOWER(v_name)) OR
            document.f_verify_unique_name (v_tmp_doc_rec.doc_name, v_container_id, G_document_object_type)) THEN


        -- update the document object
        update pn_document
        SET
            doc_name = v_tmp_doc_rec.doc_name,
            doc_description = v_tmp_doc_rec.doc_description,
            doc_type_id = v_tmp_doc_rec.doc_type_id,
            doc_status_id = v_tmp_doc_rec.doc_status_id,
            record_status = G_active_record_status,
            crc = SYSDATE
        WHERE doc_id = v_tmp_doc_rec.doc_id;

        -- update the document version
        update pn_doc_version
        SET
            doc_author_id = v_tmp_doc_rec.doc_author_id,
            doc_description = v_tmp_doc_rec.doc_description,
            date_modified = SYSDATE,
            modified_by_id = v_whoami,
            doc_comment = v_tmp_doc_rec.doc_comment,
            crc = SYSDATE,
            record_status = G_active_record_status
        WHERE
            doc_version_id = v_tmp_doc_rec.doc_version_id
        and
            doc_id = v_tmp_doc_rec.doc_id;

        return_value := 0;
        COMMIT;
        ELSE
            return_value := 5001;
            ROLLBACK;
        END IF; -- verify unique name

    ELSE
        return_value := 1000;
        ROLLBACK;

    END IF;


EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END;  -- Procedure modify_properties

Procedure REMOVE_OBJECT_FROM_CONTAINER
(
    in_object_id IN varchar2,
    in_container_id IN varchar2
)
IS

    -- variable declaration

    v_container_id    pn_doc_container.doc_container_id%type := TO_NUMBER(in_container_id);
    v_object_id       pn_doc_container.doc_container_id%type := TO_NUMBER(in_object_id);

BEGIN
    SET TRANSACTION READ WRITE;

    delete from pn_doc_container_has_object
    where
        doc_container_id = v_container_id
    and
        object_id = v_object_id;

   COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        rollback ;

END;  -- Procedure REMOVE_OBJECT_FROM_CONTAINER

------------------------------------------------------------------
-- F_VERIFY_UNIQUE_NAME
------------------------------------------------------------------
--
-- Purpose: Briefly explain the functionality of the function
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  -------------------------------------------
-- Phil Dixon  4/1/00

Function F_VERIFY_UNIQUE_NAME
(
    new_name IN varchar2,
    containerID IN pn_doc_container.doc_container_id%type,
    object_type IN pn_object.object_type%type
)

  RETURN BOOLEAN

  IS


    -- variables
    v_container_id      pn_doc_container.doc_container_id%type  := TO_NUMBER(containerID);
    v_object_id         pn_object.object_id%type;
    v_name              pn_document.doc_name%type;

    err_msg VARCHAR2(120);
    v_foo   NUMBER := -1;


    v_is_unique_name       BOOLEAN := true;

    -- Static variable definition
    G_document_object_type  pn_object.object_type%type  := 'document';
    G_container_object_type pn_object.object_type%type  := 'doc_container';

   --
   CURSOR  c_container_objects (container_id pn_doc_container.doc_container_id%type, object_type pn_object.object_type%type) IS
        select dcho.object_id
        from pn_doc_container_has_object dcho, pn_object o
        where doc_container_id = container_id
        and o.object_type = object_type
        and o.object_id = dcho.object_id;


BEGIN

    OPEN c_container_objects(v_container_id, object_type);
	<<object_loop>>
	LOOP
        -- OBJECTS CONTAINED BY v_container_id
		FETCH c_container_objects INTO v_object_id;
		EXIT WHEN c_container_objects%NOTFOUND;

        if (object_type = G_document_object_type) then

            BEGIN
                select doc_name into v_name from pn_document
                where doc_id = v_object_id and record_status = 'A';
            EXCEPTION
                when NO_DATA_FOUND then
                begin
                    v_foo := 1;
                end;
            END;

             if (lower(v_name) = lower(new_name)) then
                v_is_unique_name := false;
            end if;

        elsif (object_type = G_container_object_type) then

            BEGIN
                select container_name into v_name from pn_doc_container
                where doc_container_id = v_object_id and record_status = 'A';
            EXCEPTION
                when NO_DATA_FOUND then
                begin
                    v_foo := 1;
                end;
            END;

            if (lower(v_name) = lower(new_name)) then
                v_is_unique_name := false;
            end if;

       end if;


	END LOOP object_loop;
	CLOSE c_container_objects;

    RETURN v_is_unique_name;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_msg := SUBSTR(SQLERRM, 1, 120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,'document.f_verify_unique_name',-019284,err_msg);
        COMMIT;
      END;



END; -- Function F_VERIFY_UNIQUE_NAME


------------------------------------------------------------------
-- F_VERIFY_CRC
------------------------------------------------------------------
Function F_VERIFY_CRC
(
    v_orig_crc IN DATE,
    v_current_crc IN DATE
)
  RETURN BOOLEAN

IS

  v_crc_match  BOOLEAN := false;

BEGIN
    if (v_orig_crc = v_current_crc) THEN
        v_crc_match := true;
    else
        v_crc_match := false;
    END IF;

    RETURN (v_crc_match);

END; -- Function F_VERIFY_CRC


------------------------------------------------------------------
-- GET_COUNT_DOCS_BY_SPACE
------------------------------------------------------------------

FUNCTION GET_COUNT
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_count             NUMBER(9);

BEGIN

    select count(doc_id) into v_count from pn_doc_by_space_view
    where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                            where space_id = in_space_id);

    return v_count;

END; -- get_count_docs_by_space


------------------------------------------------------------------
-- GET_COUNT_DOCS_OPEN_BY_SPACE
------------------------------------------------------------------

FUNCTION GET_COUNT_OPEN
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_count             NUMBER(9);

BEGIN

    select count(doc_id) into v_count from pn_doc_by_space_view
    where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                            where space_id = in_space_id)
    and doc_status_id not in (400);

    return v_count;

END; -- get_count_docs_by_space


------------------------------------------------------------------
-- GET_COUNT_DOCS_CLOSED_BY_SPACE
------------------------------------------------------------------

FUNCTION GET_COUNT_CLOSED
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_count             NUMBER(9);

BEGIN

    select count(doc_id) into v_count from pn_doc_by_space_view
    where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                            where space_id = in_space_id)
    and doc_status_id = 400;

    return v_count;

END; -- get_count_docs_by_space


------------------------------------------------------------------
-- GET_COUNT_DOCS_CLOSED_BY_SPACE
------------------------------------------------------------------

FUNCTION GET_COUNT_CLOSED_LAST_WEEK
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_count             NUMBER(9);

BEGIN

    select count(doc_id) into v_count from pn_doc_by_space_view
    where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                            where space_id = in_space_id)
    and doc_status_id = 400
    and date_modified > (SYSDATE - 7);

    return v_count;

END; -- get_count_docs_by_space


------------------------------------------------------------------
-- GET_NEXT_REPOSITORY_BASE_ID
------------------------------------------------------------------

FUNCTION GET_NEXT_REPOSITORY_BASE_ID
RETURN NUMBER

AS
    v_next_repository_id             pn_doc_repository_base.repository_id%type := 0;
    v_current_repository_id     pn_doc_repository_base.repository_id%type := 0;

BEGIN

    select repository_sequence into v_current_repository_id
        from pn_next_doc_repository;

    select min(repository_id) into v_next_repository_id from pn_doc_repository_base
        where repository_id > v_current_repository_id
        and is_active = 1;

      if (v_next_repository_id IS NULL) then

        select min(repository_id) into v_next_repository_id from pn_doc_repository_base
            where is_active = 1;

    end if;

    if (v_next_repository_id IS NOT NULL) then

        update pn_next_doc_repository
            set repository_sequence = v_next_repository_id;

    end if;

    return v_current_repository_id;

END;


FUNCTION GET_CONTAINER_FOR_DOC_ID
(    i_doc_id        pn_document.doc_id%type    )
RETURN NUMBER

AS

BEGIN

   return get_container_for_object_id (i_doc_id);

END;


FUNCTION GET_CONTAINER_FOR_object_ID
(    i_object_id        pn_object.object_id%type    )
RETURN NUMBER

AS

    v_container_id  pn_doc_container.doc_container_id%type;

BEGIN

    select distinct doc_container_id into v_container_id
    from pn_doc_container_has_object where object_id = i_object_id;

    return v_container_id;

END;


function get_doc_space_for_id
(
    i_id pn_object.object_id%type
) return number

as

    v_container_id pn_doc_container.doc_container_id%type;
    v_doc_space_id pn_doc_space.doc_space_id%type;

begin

    v_container_id := get_container_for_object_id (i_id);

    select doc_space_id into v_doc_space_id
    from pn_doc_space_has_container where doc_container_id = v_container_id;

    return v_doc_space_id;

end;




FUNCTION GET_SPACE_FOR_ID
(    i_id        pn_object.object_id%type    )
RETURN NUMBER

AS

    v_container_id  pn_doc_container.doc_container_id%type;
    v_doc_space_id  pn_doc_space.doc_space_id%type;
    v_owner_id      pn_object.object_id%type;

BEGIN

    v_container_id := get_container_for_doc_id ( i_id );

    select doc_space_id into v_doc_space_id
    from pn_doc_space_has_container where doc_container_id = v_container_id;

    select space_id into v_owner_id
    from pn_space_has_doc_space where doc_space_id = v_doc_space_id;

    return v_owner_id;

END;



FUNCTION GET_SPACE_FOR_CONTAINER_ID
(    i_container_id        pn_doc_container.doc_container_id%type    )
RETURN NUMBER

AS

    v_doc_space_id  pn_doc_space.doc_space_id%type;
    v_owner_id      pn_object.object_id%type;

BEGIN

    select doc_space_id into v_doc_space_id
    from pn_doc_space_has_container where doc_container_id = i_container_id;

    select space_id into v_owner_id
    from pn_space_has_doc_space where doc_space_id = v_doc_space_id;

    return v_owner_id;

END;

-----------------------------------------------------------------
-- PROCEDURE:  COPY_CONTAINER
-- DOES NOT COMMIT OR ROLLBACK
-- Copies a container, but NONE of its contents
-----------------------------------------------------------------
procedure copy_container (
    i_from_container_id         in      pn_doc_container.doc_container_id%type,
    i_new_parent_container_id   in      pn_doc_container.doc_container_id%type,
    i_creator_id                in      pn_person.person_id%type,
    o_new_container_id          out     pn_doc_container.doc_container_id%type,
    o_return_value              out     number
)
is
    -- variable declaration
    v_new_container_id pn_doc_container.doc_container_id%type;
    v_container_rec pn_doc_container%rowtype;
    v_dshc_rec pn_doc_space_has_container%rowtype;
    v_timestamp date := sysdate;
begin
    -- Get current container record
    select *
      into v_container_rec
      from pn_doc_container
     where doc_container_id = i_from_container_id
       and lower (record_status) = 'a';

    if (v_container_rec.is_hidden = 0) then
        if (f_verify_unique_name (
                v_container_rec.container_name,
                i_new_parent_container_id,
                g_container_object_type
            )
           ) then
            -- register new container in pn_object
            v_new_container_id := base.create_object (
                                      g_container_object_type,
                                      i_creator_id,
                                      g_active_record_status
                                  );

            -- create new document container
            insert into pn_doc_container
                        (doc_container_id, container_name, container_description,
                         record_status, modified_by_id, date_modified, crc)
                 values (v_new_container_id, v_container_rec.container_name,
                         v_container_rec.container_description,
                         v_container_rec.record_status, i_creator_id,
                         v_timestamp, v_timestamp);

            -- apply default object permissions for the object
            security.apply_document_permissions (
                v_new_container_id,
                i_new_parent_container_id,
                g_container_object_type,
                get_space_for_container_id (v_container_rec.doc_container_id),
                i_creator_id
            );
            -- Get the doc_space for the parent container
            -- and insert the new container into that doc space
            select *
              into v_dshc_rec
              from pn_doc_space_has_container
             where doc_container_id = i_new_parent_container_id;

            insert into pn_doc_space_has_container
                        (doc_space_id, doc_container_id, is_root)
                 values (v_dshc_rec.doc_space_id, v_new_container_id, 0);

            -- make the new container an object of the parent container
            insert into pn_doc_container_has_object
                        (doc_container_id, object_id)
                 values (i_new_parent_container_id, v_new_container_id);

            -- SUCCESS --
            o_new_container_id := v_new_container_id;
            o_return_value := BASE.OPERATION_SUCCESSFUL;
        else
            -- NAME VIOLATION
            o_return_value := BASE.DOC_UNIQUE_NAME_CONSTRAINT;
        end if;
    end if;
exception
    when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error ('DOCUMENT.COPY_CONTAINER', sqlcode, sqlerrm);
        end;
end; -- PROCEDURE COPY_CONTAINER


-----------------------------------------------------------------
-- COPY_DOCUMENT
-- DOES NOT COMMIT OR ROLLBACK
-- Copies a document
-----------------------------------------------------------------
procedure copy_document (
    i_from_document_id   in     pn_document.doc_id%type,
    i_to_container_id    in     pn_doc_container.doc_container_id%type,
    i_creator_id         in     pn_person.person_id%type,
    i_discussion_group_description IN VARCHAR2,
    o_new_document_id    out    pn_document.doc_id%type,
    o_return_value       out    number
)
is
    -- variable declaration
    v_new_doc_id pn_document.doc_id%type;
    v_new_version_id pn_doc_version.doc_version_id%type;
    v_new_content_id pn_doc_content_element.doc_content_id%type;
    v_current_version_id pn_doc_version.doc_version_id%type;
    v_discussion_group_id pn_discussion_group.discussion_group_id%type;
    v_welcome_message_post_clob pn_post_body_clob.clob_field%type;
    v_discussion_grp_charter_clob pn_discussion_group.discussion_group_charter_clob%type;
    v_doc_record pn_document%rowtype;
    v_version_rec pn_doc_version%rowtype;
    v_content_rec pn_doc_content_element%rowtype;
    v_timestamp date := sysdate;
    --v_status number;

begin
    select *
      into v_doc_record
      from pn_document
     where doc_id = i_from_document_id;

    if (document.f_verify_unique_name (
            v_doc_record.doc_name,
            i_to_container_id,
            g_document_object_type
        )
       ) then
        v_new_doc_id := base.create_object (
                            g_document_object_type,
                            i_creator_id,
                            g_active_record_status
                        );
        v_new_version_id := base.create_object (
                                g_document_version_object_type,
                                i_creator_id,
                                g_active_record_status
                            );
        select pn_object_sequence.nextval
          into v_new_content_id
          from dual;

        insert into pn_document
                    (doc_id, doc_name, doc_type_id, doc_description,
                     current_version_id, doc_status_id, crc, record_status)
             values (v_new_doc_id, v_doc_record.doc_name,
                     v_doc_record.doc_type_id, v_doc_record.doc_description,
                     v_new_version_id, v_doc_record.doc_status_id, v_timestamp,
                     g_active_record_status);

        -- apply default object permissions for the document
        security.apply_document_permissions (
            v_new_doc_id,
            i_to_container_id,
            g_document_object_type,
            get_space_for_id (v_doc_record.doc_id),
            i_creator_id
        );

        select *
          into v_version_rec
          from pn_doc_version
         where doc_version_id = v_doc_record.current_version_id;

        insert into pn_doc_version
                    (doc_version_id, doc_id, doc_version_name, source_file_name,
                     doc_description, date_modified, modified_by_id,
                     is_checked_out, checked_out_by_id, date_checked_out,
                     doc_comment, doc_version_num, doc_version_label,
                     checkout_due, doc_author_id, short_file_name, crc,
                     record_status)
             values (v_new_version_id, v_new_doc_id,
                     v_version_rec.doc_version_name,
                     v_version_rec.source_file_name,
                     v_version_rec.doc_description, v_version_rec.date_modified,
                     v_version_rec.modified_by_id, 0, -- Will not carry checked out documents
                                                     null, -- see above
                                                          null,
                     'Copied Document', 1, -- will renumber as first version for new document
                                          null, null, i_creator_id,
                     v_version_rec.short_file_name, v_timestamp,
                     g_active_record_status);

        select *
          into v_content_rec
          from pn_doc_content_element
         where doc_content_id = (select doc_content_id
                                   from pn_doc_version_has_content
                                  where doc_version_id =
                                                v_doc_record.current_version_id
                                    and doc_id = v_doc_record.doc_id);

        -- create the content element(s) for the document version
        insert into pn_doc_content_element
                    (doc_content_id, doc_format_id, record_status, file_size,
                     file_handle, repository_id)
             values (v_new_content_id, v_content_rec.doc_format_id,
                     g_active_record_status, v_content_rec.file_size,
                     v_content_rec.file_handle,
                     DOCUMENT.GET_NEXT_REPOSITORY_BASE_ID);

        -- link the content to the doc_version
        insert into pn_doc_version_has_content
                    (doc_id, doc_version_id, doc_content_id)
             values (v_new_doc_id, v_new_version_id, v_new_content_id);

        -- insert the doc into the correct container
        insert into pn_doc_container_has_object
                    (doc_container_id, object_id)
             values (i_to_container_id, v_new_doc_id);

        -- create the docs discussion group
        -- We leave the charter CLOB empty and the welcome message
        discussion.create_discussion_group (
            null,
            get_space_for_id (v_doc_record.doc_id),
            v_new_doc_id,
            v_doc_record.doc_name,
            i_creator_id,
            i_discussion_group_description ,
            0, -- do not creaet a welcome message
            NULL,
            v_discussion_group_id,
            v_welcome_message_post_clob,
            v_discussion_grp_charter_clob
        );
        -- apply the documents permissions to the discussion group
        security.apply_document_permissions (
            v_discussion_group_id,
            v_new_doc_id,
            'discussion_group',
            get_space_for_id (v_doc_record.doc_id),
            i_creator_id
        );
        -- SUCCESS --
        o_new_document_id := v_new_doc_id;
        o_return_value := BASE.OPERATION_SUCCESSFUL;
    else
        o_return_value := BASE.DOC_UNIQUE_NAME_CONSTRAINT;
    end if;
exception
    when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error ('DOCUMENT.COPY_DOCUMENT', sqlcode, sqlerrm);
        end;
end; -- PROCEDURE copy_document

-----------------------------------------------------------------
-- COPY_BOOKMARK
-- DOES NOT COMMIT OR ROLLBACK
-- Copies a bookmark
-----------------------------------------------------------------
procedure copy_bookmark (
    i_from_bookmark_id  in      pn_bookmark.bookmark_id%type,
    i_to_container_id   in      pn_doc_container.doc_container_id%type,
    i_creator_id        in      pn_person.person_id%type,
    o_new_bookmark_id   out     pn_bookmark.bookmark_id%type,
    o_return_value      out     number
)
is
    -- variable declaration
    v_new_bookmark_id pn_document.doc_id%type;
    v_bookmark_rec pn_bookmark%rowtype;
    v_sysdate date;
    v_space_id number;
begin
    select sysdate
      into v_sysdate
      from dual;

    -- Get bookmark to copy from
    select *
      into v_bookmark_rec
      from pn_bookmark
     where bookmark_id = i_from_bookmark_id;

    -- Create new bookmark id
    v_new_bookmark_id :=
                  base.create_object (g_bookmark_object_type, i_creator_id, 'A');

    -- Copy bookmark
    insert into pn_bookmark
                (bookmark_id, name, description, url, status_id, owner_id,
                 comments, modified_date, modified_by_id, record_status, crc)
         values (v_new_bookmark_id, v_bookmark_rec.name,
                 v_bookmark_rec.description, v_bookmark_rec.url,
                 v_bookmark_rec.status_id, i_creator_id, v_bookmark_rec.comments,
                 v_sysdate, i_creator_id, g_active_record_status, v_sysdate);

    -- Find the space in which owning container belongs
    v_space_id := get_space_for_container_id (i_to_container_id);
    -- apply default object permissions for the document
    security.apply_document_permissions (
        v_new_bookmark_id,
        i_to_container_id,
        g_bookmark_object_type,
        v_space_id,
        i_creator_id
    );
    -- finally, add this bookmark to it's parent container
    add_object_to_container (v_new_bookmark_id, i_to_container_id);

    -- SUCCESS --
    o_new_bookmark_id := v_new_bookmark_id;
    o_return_value := BASE.OPERATION_SUCCESSFUL;

exception
    when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error ('DOCUMENT.COPY_BOOKMARK', sqlcode, sqlerrm);
        end;
end copy_bookmark;




FUNCTION has_links
(
    i_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_has_links             NUMBER  := 0;
    v_count                 NUMBER  := 0;

BEGIN

    select
      count(*) into v_count
    from
      pn_object_link ol,
      pn_object o1,
      pn_object o2
    where
      o1.object_id = ol.to_object_id
      and o1.record_status = 'A'
      and o2.object_id = ol.from_object_id
      and o2.record_status = 'A'
      and (ol.from_object_id = i_id or ol.to_object_id = i_id);

    if (v_count > 0) then
        v_has_links := 1;
    end if;

    return v_has_links;


EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        v_has_links := 0;
        return v_has_links;
      END;


END; -- has_links

END; -- Package Body DOCUMENT
/

CREATE OR REPLACE PACKAGE document IS
-----------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Robin       29-Mar-00  Created package for Phil's procedures.
-- Robin       25-Apr-00  Added remove_object_from_container.

-----------------------------------------------------------------

  -- Package Variables

    -- to be used to contain the return value for apply permissions
    v_applied_security                  BOOLEAN := false;


  -- Package Global Variables (be careful...)

    G_document_object_type              pn_object.object_type%type := 'document';
    G_document_version_object_type      pn_object.object_type%type := 'document_version';
    G_container_object_type             pn_object.object_type%type := 'doc_container';
    G_bookmark_object_type              pn_object.object_type%type := 'bookmark';
    G_active_record_status              pn_document.record_status%type := 'A';


      TYPE ReferenceCursor            IS REF CURSOR;

  PROCEDURE copy_container
  (
    i_from_container_id         in      pn_doc_container.doc_container_id%type,
    i_new_parent_container_id   in      pn_doc_container.doc_container_id%type,
    i_creator_id                in      pn_person.person_id%type,
    o_new_container_id          out     pn_doc_container.doc_container_id%type,
    o_return_value              out     number
  );


  PROCEDURE copy_document
  (
    i_from_document_id   in     pn_document.doc_id%type,
    i_to_container_id    in     pn_doc_container.doc_container_id%type,
    i_creator_id         in     pn_person.person_id%type,
    i_discussion_group_description IN VARCHAR2,
    o_new_document_id    out    pn_document.doc_id%type,
    o_return_value       out    number
  );

  PROCEDURE copy_bookmark
  (
    i_from_bookmark_id  in      pn_bookmark.bookmark_id%type,
    i_to_container_id   in      pn_doc_container.doc_container_id%type,
    i_creator_id        in      pn_person.person_id%type,
    o_new_bookmark_id   out     pn_bookmark.bookmark_id%type,
    o_return_value      out     number
  );


  procedure create_doc_space
  (
    i_in_space_id in pn_doc_space.doc_space_id%type,
    i_creator_id in pn_person.person_id%type,
    o_doc_space_id out number,
    o_return_value out number
  );


  PROCEDURE create_doc
    (
        tmp_doc IN varchar2,
        container_id IN varchar2,
        whoami IN varchar2,
        space_id IN varchar2,
        ignore_name_constraint IN NUMBER,
        i_discussion_group_description IN VARCHAR2,
        v_return_value OUT NUMBER
    );

PROCEDURE create_bookmark
(
        i_name in varchar2,
        i_description in varchar2,
        i_url in varchar2,
        i_container_id in number,
        i_status_id in number,
        i_owner_id in number,
        i_comments in varchar2,
        i_created_by_id in number,
        o_status out number,
        o_object_id out number
);


PROCEDURE modify_bookmark
(
        i_id in number,
        i_name in varchar2,
        i_description in varchar2,
        i_url in varchar2,
        i_container_id in number,
        i_status_id in number,
        i_owner_id in number,
        i_comments in varchar2,
        i_created_by_id in number,
        o_status out number
);

  FUNCTION IS_PARENT_CONTAINER
    (
        parent_container_id IN varchar2,
        child_container_id IN varchar2
    ) RETURN NUMBER;

  FUNCTION GET_PATH_FOR_OBJECT
    (
        object_id IN varchar2,
        root_object_id IN varchar2
    ) RETURN ReferenceCursor;

  FUNCTION GET_PARENT_CONTAINER_INFO
    (
        object_id IN varchar2
    ) RETURN ReferenceCursor;

  FUNCTION get_parent_space_for_object
    (
        object_id IN varchar2
    ) RETURN ReferenceCursor;

  FUNCTION GET_CONTAINER_LIST
     (
        doc_space_id IN pn_doc_space.doc_space_id%type
      ) RETURN ReferenceCursor;

  PROCEDURE create_container
    (
        in_container_id IN varchar2,
        whoami IN varchar2,
        folder_name IN varchar2,
        description IN varchar2,
        space_id    IN varchar2,
        is_hidden   IN varchar2,
        object_id OUT number,
        v_return_value OUT number
    );

  PROCEDURE modify_container
    (
        parent_id IN varchar2,
        my_container_id IN varchar2,
        folder_name IN varchar2,
        description in varchar2,
        whoami IN varchar2,
        is_hidden IN varchar2,
        orig_crc IN date,
        v_return_value OUT number
    );

  PROCEDURE add_object_to_container
    (
        in_object_id IN varchar2,
        in_container_id IN varchar2
    );

  PROCEDURE check_in
    (
        tmp_doc IN varchar2,
        whoami  IN varchar2,
        space_id IN varchar2,
        orig_crc IN date,
        v_return_value OUT number
    );

  PROCEDURE check_out
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        cko_due IN date,
        notes IN varchar2,
        orig_crc IN date,
        v_return_value OUT number
    );

  PROCEDURE remove_doc
    (
        doc_id IN varchar2,
        last_modified IN date,
        record_status IN varchar2,
        whoami IN varchar2
    );

  PROCEDURE remove_container
    (
        container_id IN varchar2,
        record_status IN varchar2,
        whoami IN varchar2
    );

  PROCEDURE log_event
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    );

  PROCEDURE move_object
    (
        in_object_id IN varchar2,
        in_container_id IN varchar2
    );

  PROCEDURE undo_check_out
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        orig_crc IN date,
        v_return_value OUT number
    );

  PROCEDURE modify_properties
    (
        tmp_doc IN varchar2,
        whoami  IN varchar2,
        orig_crc IN date,
        return_value OUT NUMBER
    );

 Procedure REMOVE_OBJECT_FROM_CONTAINER
    (
        in_object_id IN varchar2,
        in_container_id IN varchar2
    );

Function F_VERIFY_UNIQUE_NAME
(
    new_name IN varchar2,
    containerID IN pn_doc_container.doc_container_id%type,
    object_type IN pn_object.object_type%type
)
RETURN BOOLEAN;

Function F_VERIFY_CRC
(
    v_orig_crc IN DATE,
    v_current_crc IN DATE
)
RETURN BOOLEAN;

FUNCTION GET_COUNT
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER;

FUNCTION GET_COUNT_CLOSED_LAST_WEEK
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER;

FUNCTION GET_COUNT_CLOSED
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER;

FUNCTION GET_COUNT_OPEN
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER;

FUNCTION GET_NEXT_REPOSITORY_BASE_ID
RETURN NUMBER;

FUNCTION GET_CONTAINER_FOR_DOC_ID
(    i_doc_id        pn_document.doc_id%type    )
RETURN NUMBER;

FUNCTION GET_CONTAINER_FOR_object_ID
(    i_object_id        pn_object.object_id%type    )
RETURN NUMBER;


FUNCTION GET_SPACE_FOR_ID
(
    i_id IN pn_object.object_id%type
) RETURN NUMBER;

FUNCTION GET_SPACE_FOR_CONTAINER_ID
(    i_container_id        pn_doc_container.doc_container_id%type    )
RETURN NUMBER;

function get_doc_space_for_id
(
    i_id pn_object.object_id%type
) return number;

function has_links
(       i_id IN pn_object.object_id%type )
RETURN NUMBER;

/*
FUNCTION GET_NEXT_REPOSITORY_BASE_PATH
(
    in_repository_id pn_doc_repository_base.repository_id%type
) RETURN pn_doc_repository_base.repository_path%type;
*/


END; -- Package Specification DOCUMENT
/

CREATE OR REPLACE PACKAGE BODY forms IS
--==================================================================
-- FORMS MODULE procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Roger       03-01-00  Created it.
-- Tim         07-27-01  Updated to preserve IS_OWNER column on copy
-- Tim         09-11-01  Added and reworked copy_form procedures
--                       to facilitate copying forms from application
-- Carlos      06-07-06  Updated the Copy_Form procedure in order to
--                       be able to copy checkboxes information in
--                       form definition page.
--==================================================================

   procedure copy_fields(
        i_from_space_id     in number,
        i_to_space_id       in number,
        i_from_class_id     in number,
        i_to_class_id       in number,
        i_created_by_id     in number);

    procedure copy_lists (
        i_from_space_id     in number,
        i_to_space_id       in number,
        i_from_class_id     in number,
        i_to_class_id       in number,
        i_created_by_id     in number);

    procedure copy_field (
        i_from_space_id     in number,
        i_to_space_id       in number,
        i_from_class_id     in number,
        i_to_class_id       in number,
        i_from_field_id     in number,
        i_created_by_id     in number,
        o_new_field_id      out number);


---------------------------------------------------------------------
-- GET_NEW_FIELD_ID
-- get the new field_id that was created for the passed source field_id.
---------------------------------------------------------------------
Function get_new_field_id (
        i_class_id in number,
        i_source_field_id in number
)
RETURN  NUMBER IS

    v_field_id      pn_class_field.field_id%type;
    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'FORM.get_new_field_id';

BEGIN
    select field_id into v_field_id
    from pn_class_field
    where class_id = i_class_id
    and source_field_id = i_source_field_id;

    RETURN v_field_id;

 EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;
END;




---------------------------------------------------------------------
-- GET_NEW_FILTER_VALUE
-- get the new filter domain value that corresponds to the source
-- filter's domain value.
---------------------------------------------------------------------
Function get_new_filter_value (
        i_to_class_id in number,
        i_list_id in number,
        i_field_id in number,
        i_filter_value in number
)
RETURN NUMBER IS

    o_domain_value_id pn_class_domain_values.domain_value_id%type;
    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'FORM.get_new_filter_value';

BEGIN

    IF i_filter_value IS NULL
    THEN
        RETURN NULL;
    ELSE
        select cdv.domain_value_id into o_domain_value_id
        from pn_class_list_field clf, pn_class_field cf, pn_class_domain_values cdv
        where clf.class_id = i_to_class_id /* dest */
        and clf.list_id = i_list_id /* dest */
        and clf.field_id = i_field_id /* dest */
        and cf.class_id = clf.class_id
        and cf.field_id = clf.field_id
        and cdv.domain_id = cf.domain_id
        and cdv.source_value_id = i_filter_value; /* source */

        RETURN o_domain_value_id;
    END IF;

 EXCEPTION

    When NO_DATA_FOUND THEN
    Begin
        return NULL;
    end;

    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;
END;


----------------------------------------------------------------------
-- COPY_FIELD
-- Copies a single field into the specified form
-- Raises exception if a problem occurs
-- NO COMMIT OR ROLLBACK performed
----------------------------------------------------------------------
procedure copy_field (
    i_from_field_id     in number,
    i_to_class_id       in number,
    i_created_by_id     in number,
    o_new_field_id      out number,
    o_return_status     out number
) is

    v_from_space_id pn_space_has_class.class_id%type;
    v_to_space_id pn_space_has_class.class_id%type;
    v_from_class_id pn_class.class_id%type;
    v_new_field_id pn_class_field.field_id%type;

begin

    select space_id, class_id into v_from_space_id, v_from_class_id
    from pn_class_field
    where field_id = i_from_field_id;

    select space_id into v_to_space_id
    from pn_space_has_class
    where class_id = i_to_class_id;

    copy_field(v_from_space_id, v_to_space_id,
               v_from_class_id, i_to_class_id,
               i_from_field_id, i_created_by_id, o_new_field_id);

    o_return_status := BASE.OPERATION_SUCCESSFUL;

exception
    when others then
    begin
        o_return_status := BASE.PLSQL_EXCEPTION;
        base.log_error('FORMS.COPY_FIELD', SQLCODE, SQLERRM);
        raise;
    end;
end copy_field;


----------------------------------------------------------------------
-- COPY_ALL
-- Copies all forms from a space to another space
-- AUTONOMOUS TRANSACTION
----------------------------------------------------------------------
    PROCEDURE copy_all
      ( i_from_space_id     in varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        o_return_value      OUT number)
    is
        pragma autonomous_transaction;

        -- Cursor to read all forms for a given space
        cursor form_cur (i_space_id in number)
        is
            select shc.class_id, shc.is_owner, c.record_status
            from pn_space_has_class shc, pn_class c
            where space_id = i_space_id
            and c.class_id= shc.class_id
            and c.record_status <> 'D';

        v_from_space_id     pn_space_has_class.space_id%type := to_number(i_from_space_id);
        v_to_space_id       pn_space_has_class.space_id%type := to_number(i_to_space_id);
        v_created_by_id     number := to_number(i_created_by_id);
        v_new_form_id       pn_class.class_id%type;

    begin
        for form_rec in form_cur(i_from_space_id)
        loop
            copy_form(v_from_space_id, v_to_space_id, form_rec.class_id, form_rec.is_owner, v_created_by_id, v_new_form_id);
        end loop;

        commit;
        o_return_value := base.operation_successful;


    exception
        when others then
        begin

           rollback;
           o_return_value := base.plsql_exception;
        end;

    end copy_all;

----------------------------------------------------------------------
-- copy_form_within_space
-- Copy specified form within its current space
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
procedure copy_form_within_space (
    i_from_space_id in number,
    i_from_form_id in number,
    i_created_by_id in number,
    o_new_form_id out number,
    o_return_status out number
) is

    v_is_from_space_owner pn_space_has_class.is_owner%type;
    v_new_form_id pn_class.class_id%type;

begin

    -- Fetch the owner information for form
    select is_owner into v_is_from_space_owner
    from pn_space_has_class
    where space_id = i_from_space_id and class_id = i_from_form_id;

    -- copy the form within the from space, preserving the owner flag
    copy_form(i_from_space_id, i_from_space_id, i_from_form_id, v_is_from_space_owner,
              i_created_by_id, v_new_form_id);

    o_new_form_id := v_new_form_id;
    o_return_status := BASE.OPERATION_SUCCESSFUL;

exception
    when others then
    begin
        o_return_status := BASE.PLSQL_EXCEPTION;
        base.log_error('FORMS.COPY_FORM_WITHIN_SPACE', SQLCODE, SQLERRM);
        raise;
    end;
end copy_form_within_space;

----------------------------------------------------------------------
-- copy_form_to_space
-- Copy specified form within its current space
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
procedure copy_form_to_space (
    i_from_space_id in number,
    i_to_space_id in number,
    i_from_form_id in number,
    i_created_by_id in number,
    o_new_form_id out number,
    o_return_status out number
) is

    v_is_from_space_owner pn_space_has_class.is_owner%type;
    v_new_form_id pn_class.class_id%type;

begin

    -- Fetch the owner information for form
    select is_owner into v_is_from_space_owner
    from pn_space_has_class
    where space_id = i_from_space_id and class_id = i_from_form_id;

    -- copy the form within the from space, preserving the owner flag
    copy_form(i_from_space_id, i_to_space_id, i_from_form_id, v_is_from_space_owner,
              i_created_by_id, v_new_form_id);

    o_new_form_id := v_new_form_id;
    o_return_status := BASE.OPERATION_SUCCESSFUL;

exception
    when others then
    begin
        o_return_status := BASE.PLSQL_EXCEPTION;
        base.log_error('FORMS.COPY_FORM_TO_SPACE', SQLCODE, SQLERRM);
        raise;
    end;
end copy_form_to_space;

----------------------------------------------------------------------
-- copy_form
-- Copy specified form to specified space_id
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure copy_form (
        i_from_space_id in number,
        i_to_space_id in number,
        i_class_id in number,
        i_is_from_space_owner in number,
        i_created_by_id in number,
        o_new_form_id out number)
    is

        -- Exceptions
        form_not_found exception;

        -- Cursor for reading form
        cursor form_cur(i_class_id in number) is
            select *
            from pn_class
            where class_id = i_class_id;

        -- Cursor for reading if original form is displayed in Tools Menu
        cursor form_featured_menuitem(i_from_space_id in number) is
               select *
               from pn_space_has_featured_menuitem
               where space_id = i_from_space_id;

        form_rec                form_cur%rowtype;
        form_feature_rec        form_featured_menuitem%rowtype;
        v_to_class_id           number;
        v_display_in_tools_menu boolean;

    begin

        -- Copy the master form record
        open form_cur(i_class_id);
        fetch form_cur into form_rec;
        if form_cur%found
        then

            v_to_class_id := BASE.CREATE_OBJECT(G_form_object_type, i_created_by_id, base.active_record_status);

            insert into pn_class (
                CLASS_ID,
                CLASS_NAME,
                CLASS_DESC,
                CLASS_ABBREVIATION,
                CLASS_TYPE_ID,
                OWNER_SPACE_ID,
                METHODOLOGY_ID,
                MAX_ROW,
                MAX_COLUMN,
                NEXT_DATA_SEQ,
                DATA_TABLE_SEQ,
                MASTER_TABLE_NAME,
                DATA_TABLE_KEY,
                IS_SEQUENCED,
                IS_SYSTEM_CLASS,
                CRC,
                RECORD_STATUS,
                SUPPORTS_DISCUSSION_GROUP,
                SUPPORTS_DOCUMENT_VAULT)
            values (
                v_to_class_id,
                form_rec.CLASS_NAME,
                form_rec.CLASS_DESC,
                form_rec.CLASS_ABBREVIATION,
                form_rec.CLASS_TYPE_ID,
                i_to_space_id,
                form_rec.METHODOLOGY_ID,
                form_rec.MAX_ROW,
                form_rec.MAX_COLUMN,
                1,                          -- reset data_seq
                1,                          -- reset data_table_seq
                null,                       -- data table does not exist yet.
                form_rec.DATA_TABLE_KEY,
                form_rec.IS_SEQUENCED,
                form_rec.IS_SYSTEM_CLASS,
                sysdate,
                base.PENDING_RECORD_STATUS,
                form_rec.SUPPORTS_DISCUSSION_GROUP,
                form_rec.SUPPORTS_DOCUMENT_VAULT);

            -- Add the form to the destination space
            insert into pn_space_has_class
                (space_id, class_id, is_owner)
            values
                (i_to_space_id, v_to_class_id, i_is_from_space_owner);

            -- Add the "Display in Tools Menu" option if applicable
            open form_featured_menuitem(i_from_space_id);
            fetch form_featured_menuitem into form_feature_rec;
            if form_cur%found
            then
                insert into pn_space_has_featured_menuitem
                       (SPACE_ID,
                        OBJECT_ID)
                values (i_to_space_id,
                        v_to_class_id);
            end if;

            SECURITY.CREATE_SECURITY_PERMISSIONS(v_to_class_id, G_form_object_type, i_to_space_id, i_created_by_id);

        else
            raise form_not_found;
        end if;
        close form_cur;

        -- Copy the other items for this form.
        copy_fields(i_from_space_id, i_to_space_id, i_class_id, v_to_class_id, i_created_by_id);
        copy_lists(i_from_space_id, i_to_space_id, i_class_id, v_to_class_id, i_created_by_id);

        -- SUCCESS --
        o_new_form_id := v_to_class_id;

    exception
        when others then
        begin
            if form_cur%isopen then
                close form_cur;
            end if;

            base.log_error('FORMS.COPY_FORM', SQLCODE, SQLERRM);

            raise;
        end;
    end copy_form;


----------------------------------------------------------------------
-- copy_fields
-- copies all the fields from one form to another
-- DOES NOT COMMIT
----------------------------------------------------------------------
procedure copy_fields(
    i_from_space_id     in number,
    i_to_space_id       in number,
    i_from_class_id  in number,
    i_to_class_id    in number,
    i_created_by_id  in number)
is

    -- Cursor for reading fields
    -- This must read all fields (including deleted ones)
    -- to avoid breaking any relationships with form lists etc.
    cursor field_cur(i_class_id in number) is
        select field_id
        from pn_class_field
        where class_id = i_class_id;

    field_rec field_cur%rowtype;
    v_new_field_id pn_class_field.field_id%type;

begin

    -- Copy the form fields
    for field_rec in field_cur(i_from_class_id)
    loop
        copy_field(i_from_space_id, i_to_space_id, i_from_class_id,
                   i_to_class_id, field_rec.field_id, i_created_by_id, v_new_field_id);

    end loop;

    -- set active field back to pending, no data table yet.
    update pn_class_field
    set record_status='P'
    where record_status='A'
    and class_id = i_to_class_id;

end copy_fields;


----------------------------------------------------------------------
-- copy_field
-- copies a single field
-- DOES NOT COMMIT
----------------------------------------------------------------------
procedure copy_field (
    i_from_space_id     in number,
    i_to_space_id       in number,
    i_from_class_id     in number,
    i_to_class_id       in number,
    i_from_field_id     in number,
    i_created_by_id     in number,
    o_new_field_id      out number
) is
    -- Cursor for field properties
    cursor field_prop_cur(i_class_id in number, i_field_id in number) is
        select *
        from pn_class_field_property
        where
            class_id = i_class_id and
            field_id = i_field_id;

    -- Cursor for reading domain values
    cursor domain_value_cur(i_domain_id in number) is
        select *
        from pn_class_domain_values
        where domain_id = i_domain_id;

    v_field_id          pn_class_field.field_id%type;
    v_source_field_id   pn_class_field.field_id%type;
    v_from_domain_id    pn_class_domain.domain_id%type;
    v_domain_id         pn_class_domain.domain_id%type;
    v_domain_value_id   pn_class_domain_values.domain_value_id%type;
    field_rec           pn_class_field%rowtype;
    field_prop_rec      field_prop_cur%rowtype;
    domain_value_rec    domain_value_cur%rowtype;
    domain_rec          pn_class_domain%rowtype;
    v_new_record_status pn_class_field.record_status%type;

begin

    -- Fetch the field
    select * into field_rec
    from pn_class_field
    where class_id = i_from_class_id and field_id = i_from_field_id;

    v_source_field_id := i_from_field_id;
    v_from_domain_id := field_rec.domain_id;

    -- Determine the record status for the copied field;
    -- Active fields become pending, all others (pending, deleted)
    -- remain the same
    if (field_rec.record_status = BASE.ACTIVE_RECORD_STATUS) then
        v_new_record_status := BASE.PENDING_RECORD_STATUS;
    else
        v_new_record_status := field_rec.record_status;
    end if;

    -- create new domain_id
    if (v_from_domain_id IS NOT NULL) then
    begin
        -- Create is for the domain_id
        v_domain_id := base.create_object(
            G_domain_object_type,
            i_created_by_id,
            base.active_record_status);

        select * into domain_rec
        from pn_class_domain
        where domain_id = v_from_domain_id;

        insert into pn_class_domain (
            domain_id,
            domain_name,
            domain_type,
            domain_desc,
            record_status
        ) values (
            v_domain_id,
            domain_rec.domain_name,
            domain_rec.domain_type,
            domain_rec.domain_desc,
            domain_rec.record_status
        );

    end;
    else
        v_domain_id:= NULL;
    end if;


    --
    -- Create the new field
    --
    v_field_id := base.create_object(
        G_field_object_type,
        i_created_by_id,
        base.active_record_status);


    insert into pn_class_field (
        CLASS_ID,
        FIELD_ID,
        SOURCE_FIELD_ID,
        ELEMENT_ID,
        SPACE_ID,
        ROW_NUM,
        ROW_SPAN,
        COLUMN_NUM,
        COLUMN_SPAN,
        FIELD_GROUP,
        DATA_TABLE_NAME,
        DATA_COLUMN_SIZE,
        data_column_scale,
        DATA_COLUMN_EXISTS,
        HAS_DOMAIN,
        MAX_VALUE,
        MIN_VALUE,
        DEFAULT_VALUE,
        INSTRUCTIONS_CLOB,
        IS_MULTI_SELECT,
        DOMAIN_ID,
        FIELD_LABEL,
        USE_DEFAULT,
        COLUMN_ID,
        DATA_COLUMN_NAME,
        CRC,
        RECORD_STATUS,
        IS_VALUE_REQUIRED)
    values (
        i_to_class_id,
        v_field_id,
        v_source_field_id,              -- the field_id that this field was copied from
        field_rec.ELEMENT_ID,
        i_to_space_id,
        field_rec.ROW_NUM,
        field_rec.ROW_SPAN,
        field_rec.COLUMN_NUM,
        field_rec.COLUMN_SPAN,
        field_rec.FIELD_GROUP,
        null,                           -- data table name is set later
        field_rec.DATA_COLUMN_SIZE,
        field_rec.data_column_scale,
        0,                              -- column does not exist yet. no datatable.
        field_rec.HAS_DOMAIN,
        field_rec.MAX_VALUE,
        field_rec.MIN_VALUE,
        field_rec.DEFAULT_VALUE,
        field_rec.INSTRUCTIONS_CLOB,
        field_rec.IS_MULTI_SELECT,
        v_domain_id,
        field_rec.FIELD_LABEL,
        field_rec.USE_DEFAULT,
        field_rec.COLUMN_ID,
        null,                           -- data table created later.
        sysdate,
        v_new_record_status,
        field_rec.IS_VALUE_REQUIRED
    );

    -- Copy all the properties for this field
    for field_prop_rec in field_prop_cur(i_from_class_id, v_source_field_id)
    loop
        insert into pn_class_field_property (
            CLASS_ID,
            FIELD_ID,
            CLIENT_TYPE_ID,
            PROPERTY_TYPE,
            PROPERTY,
            VALUE)
        values (
             i_to_class_id,
             v_field_id,
             field_prop_rec.CLIENT_TYPE_ID,
             field_prop_rec.PROPERTY_TYPE,
             field_prop_rec.PROPERTY,
             field_prop_rec.VALUE);
    end loop; -- field properties

    -- Copy the domain values if the domain is not null.
    if (v_from_domain_id IS NOT NULL) then
        -- Copy the domain values for the field
        for domain_value_rec in domain_value_cur(v_from_domain_id)
        loop
            -- Create new domain_value_id
            v_domain_value_id := base.create_object(
                G_domain_value_object_type,
                i_created_by_id,
                base.active_record_status);
            insert into pn_class_domain_values (
                DOMAIN_ID,
                DOMAIN_VALUE_ID,
                DOMAIN_VALUE_NAME,
                DOMAIN_VALUE_DESC,
                IS_DEFAULT,
                DOMAIN_VALUE_SEQ,
                source_value_id,
                RECORD_STATUS)
            values (
                v_domain_id,
                v_domain_value_id,
                domain_value_rec.DOMAIN_VALUE_NAME,
                domain_value_rec.DOMAIN_VALUE_DESC,
                domain_value_rec.IS_DEFAULT,
                domain_value_rec.DOMAIN_VALUE_SEQ,
                domain_value_rec.DOMAIN_VALUE_ID,
                domain_value_rec.RECORD_STATUS);
        end loop; -- domain values
    end if;

    o_new_field_id := v_field_id;

end copy_field;

----------------------------------------------------------------------
-- copy_lists
-- copies form lists from one form to another.
-- DOES NOT COMMIT
----------------------------------------------------------------------
    procedure copy_lists (
        i_from_space_id     in number,
        i_to_space_id       in number,
        i_from_class_id     in number,
        i_to_class_id       in number,
        i_created_by_id     in number)
    is
        -- Cursor for reading form lists
        cursor list_cur(i_class_id in number) is
            select *
            from pn_class_list
            where class_id = i_class_id
            and record_status != 'D';

        -- Cursor for form list fields
        cursor list_field_cur(i_class_id in number, i_list_id in number) is
            select *
            from pn_class_list_field
            where class_id = i_class_id
            and list_id = i_list_id;

        -- Cursor for form list filters
        cursor list_filter_cur(i_class_id in number, i_list_id in number) is
            select clf.*, cf.element_id
            from pn_class_list_filter clf, pn_class_field cf
            where clf.class_id = i_class_id
            and clf.list_id = i_list_id
            and cf.field_id = clf.field_id;

        v_list_id           pn_class_list.list_id%type;
        v_from_list_id      pn_class_list.list_id%type;
        v_field_id          pn_class_field.field_id%type;
        v_filter_value_id   pn_class_list_filter.value_id%type;
        v_filter_value      pn_class_domain_values.domain_value_id%type;
        v_is_default        pn_space_has_class_list.is_default%type;
        list_rec            list_cur%rowtype;
        list_field_rec      list_field_cur%rowtype;
        list_filter_rec     list_filter_cur%rowtype;

    begin

        -- For each form list
        for list_rec in list_cur(i_from_class_id)
        loop
           v_from_list_id := list_rec.list_id;
           -- create new list_id
           v_list_id := BASE.CREATE_OBJECT(
                G_list_object_type,
                i_created_by_id,
                base.active_record_status);

            insert into pn_class_list (
                CLASS_ID,
                LIST_ID,
                LIST_NAME,
                FIELD_CNT,
                LIST_DESC,
                OWNER_SPACE_ID,
                CRC,
                RECORD_STATUS)
            values (
                i_to_class_id,
                v_list_id,
                list_rec.LIST_NAME,
                list_rec.FIELD_CNT,
                list_rec.LIST_DESC,
                i_to_space_id,
                sysdate,
                base.active_record_status);

            SECURITY.CREATE_SECURITY_PERMISSIONS(v_list_id, G_list_object_type, i_to_space_id, i_created_by_id);

            -- For each form list field
            for list_field_rec in list_field_cur(i_from_class_id, v_from_list_id)
            loop
                v_field_id := get_new_field_id(i_to_class_id, list_field_rec.field_id);
                -- create new class_list_field
                insert into pn_class_list_field (
                    CLASS_ID,
                    LIST_ID,
                    FIELD_ID,
                    FIELD_ORDER,
                    SORT_ORDER,
                    WRAP_MODE,
                    IS_SUBFIELD,
                    IS_SORT_FIELD,
                    FIELD_WIDTH,
                    SORT_ASCENDING,
                    IS_LIST_FIELD)
                values (
                    i_to_class_id,
                    v_list_id,
                    v_field_id,
                    list_field_rec.FIELD_ORDER,
                    list_field_rec.SORT_ORDER,
                    list_field_rec.WRAP_MODE,
                    list_field_rec.IS_SUBFIELD,
                    list_field_rec.IS_SORT_FIELD,
                    list_field_rec.FIELD_WIDTH,
                    list_field_rec.SORT_ASCENDING,
                    list_field_rec.IS_LIST_FIELD);
            end loop; -- class_list_field

                -- create new class_list_filter
                for list_filter_rec in list_filter_cur(i_from_class_id, v_from_list_id)
                loop
                    -- look up the new destination field_id that
                    -- corresponds the old source field_id.
                    v_field_id := get_new_field_id(
                        i_to_class_id,
                        list_filter_rec.field_id);

                    -- If the field has a domain (ie. menu field), look up the new destination
                    -- filter_value_id that corresponds the old source filter_value_id.
                    -- Otherwise, just copy the exact filter value.
                    if list_filter_rec.element_id = G_selection_menu_id
                    then
                        v_filter_value := get_new_filter_value(
                            i_to_class_id,
                            v_list_id,
                            v_field_id,
                            list_filter_rec.filter_value);
                    else
                        v_filter_value := list_filter_rec.filter_value;
                    end if;


                    if (v_filter_value IS NOT NULL)
                    then
                        v_filter_value_id := BASE.CREATE_OBJECT(
                            G_filter_value_object_type,
                            i_created_by_id,
                            base.active_record_status);

                        insert into pn_class_list_filter (
                            CLASS_ID,
                            LIST_ID,
                            FIELD_ID,
                            VALUE_ID,
                            FILTER_VALUE,
                            OPERATOR)
                        values (
                            i_to_class_id,
                            v_list_id,
                            v_field_id,
                            v_filter_value_id,
                            v_filter_value,
                            list_filter_rec.OPERATOR);
                   end if;

                end loop; -- class_list_filter

                -- Get the defualt setting for the list
                select is_default into v_is_default
                from pn_space_has_class_list
                where space_id = i_from_space_id
                and class_id = i_from_class_id
                and list_id = v_from_list_id;

                insert into pn_space_has_class_list (
                    SPACE_ID,
                    CLASS_ID,
                    LIST_ID,
                    IS_DEFAULT)
                values (
                    i_to_space_id,
                    i_to_class_id,
                    v_list_id,
                    v_is_default);

        end loop; -- class_list

        -- For DEBUG
        EXCEPTION
        WHEN OTHERS THEN
          RAISE;

    end copy_lists;


PROCEDURE log_event
    (
        object_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    )

IS
     pragma autonomous_transaction;

    v_object_id     pn_object.object_id%type := TO_NUMBER(object_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_forms_history.forms_history_id%type;
    v_action          pn_forms_history.action%type := action;
    v_action_name     pn_forms_history.action_name%type := action_name;
    v_action_comment  pn_forms_history.action_comment%type := notes;

BEGIN

    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- insert a history record for this document

    insert into pn_forms_history (
        object_id,
        forms_history_id,
        action,
        action_name,
        action_by_id,
        action_date,
        action_comment)
    values (
        v_object_id,
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
         base.log_error('FORMS.LOG_EVENT', sqlcode, sqlerrm);
        raise;
    END;
END;  -- Procedure LOG_EVENT
----------------------------------------------------------------------


END; -- Package Body FORM
/

CREATE OR REPLACE PACKAGE forms IS

   -- Package constants
   G_form_object_type           constant pn_object.object_type%type := 'form';
   G_field_object_type          constant pn_object.object_type%type := 'form_field';
   G_list_object_type           constant pn_object.object_type%type := 'form_list';
   G_domain_object_type         constant pn_object.object_type%type := 'form_domain';
   G_domain_value_object_type   constant pn_object.object_type%type := 'form_domain_value';
   G_filter_value_object_type   constant pn_object.object_type%type := 'form_filter_value';
   G_selection_menu_id          constant number := 3;
   G_person_selection_id        constant number := 9;

   -- Raise-able errors
   unspecified_error  exception;
   pragma exception_init(unspecified_error, -20000);
   space_not_found    exception;



----------------------------------------------------------------------
-- COPY_ALL
-- Copies all forms for a space to another space
----------------------------------------------------------------------
   procedure copy_all (
        i_from_space_id     in varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        o_return_value      out number);


----------------------------------------------------------------------
-- COPY_FORM
-- Copies a single form from one space to another space.
-- New objects ids are assigned so that the two forms are completely
-- independant copies of each other.
----------------------------------------------------------------------
  procedure copy_form (
        i_from_space_id in number,
        i_to_space_id in number,
        i_class_id in number,
        i_is_from_space_owner in number,
        i_created_by_id in number,
        o_new_form_id out number);
--
-- Simplied COPY_FORM that copies within the from space
--
procedure copy_form_within_space (
    i_from_space_id in number,
    i_from_form_id in number,
    i_created_by_id in number,
    o_new_form_id out number,
    o_return_status out number);

----------------------------------------------------------------------
-- copy_form_to_space
-- Copies a single form from one space to another space
-- Determines whether the from space owns the form or not
-- then calls COPY_FORM
----------------------------------------------------------------------
procedure copy_form_to_space (
    i_from_space_id in number,
    i_to_space_id in number,
    i_from_form_id in number,
    i_created_by_id in number,
    o_new_form_id out number,
    o_return_status out number);

----------------------------------------------------------------------
-- COPY_FIELD
-- Copies a single field into the specified form
----------------------------------------------------------------------
    procedure copy_field (
        i_from_field_id     in number,
        i_to_class_id       in number,
        i_created_by_id     in number,
        o_new_field_id      out number,
        o_return_status     out number);

----------------------------------------------------------------------
-- LOG_EVENT
-- Logs the event that happens to the NEWS item
----------------------------------------------------------------------
   PROCEDURE log_event
    (
        object_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    );
----------------------------------------------------------------------


END; -- Package Specification FORM
/

CREATE OR REPLACE Package Body help
IS

/*-------------------------------------------------------------------------------

ADD_FEEDBACK

Modification History
Date       Programmer  Reason
========   ==========  =================================================
21-Jun-00  Robin       Creation.
--------------------------------------------------------------------------------*/

PROCEDURE add_feedback  (i_person_id IN NUMBER,
                         i_project_id IN NUMBER,
                         i_subject IN VARCHAR2,
                         i_key_id IN NUMBER,
                         i_comments IN VARCHAR2,
                         o_status OUT NUMBER)


IS

stored_proc_name VARCHAR2(100):= 'HELP.ADD_FEEDBACK';

BEGIN

    INSERT INTO help_feedback (person_id, timestamp, project_id, subject, key_id, comments)
         VALUES (i_person_id, sysdate, i_project_id, i_subject, i_key_id, i_comments);

   o_status := success;
   COMMIT;

EXCEPTION
   WHEN e_null_constraint THEN
      -- feedback subject/comments empty
      o_status := null_field;

   WHEN e_unique_constraint THEN
      -- feedback already exists
      o_status := dupe_key;

   WHEN e_no_parent_key THEN
      -- person, key, project doesn't exist
      o_status := no_parent_key;

   WHEN e_value_too_large THEN
      -- value too large for column
      o_status := value_too_large;

   WHEN OTHERS THEN
      o_status := generic_error;
      err_num:=SQLCODE;
      err_msg:=SUBSTR(SQLERRM,1,120);
      ROLLBACK;
      INSERT INTO pn_sp_error_log
         VALUES (sysdate,stored_proc_name,err_num,err_msg);
      COMMIT;

END add_feedback;

END; -- Package Body HELP
/

CREATE OR REPLACE Package help
IS

/*-------------------------------------------------------------------------------
Description: Package contains procedures for inserting and deleting
             HELP_FEEDBACK table.

Modification History
Date      Programmer  Code  Reason
========  ==========  ====  =================================================
06/21/00  RStutman          Creation.
--------------------------------------------------------------------------------*/

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;
value_too_large CONSTANT NUMBER:=107;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;
e_value_too_large EXCEPTION;

PRAGMA EXCEPTION_INIT (e_value_too_large, -01401);
PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);


PROCEDURE add_feedback  (i_person_id IN NUMBER,
                         i_project_id IN NUMBER,
                         i_subject IN VARCHAR2,
                         i_key_id IN NUMBER,
                         i_comments IN VARCHAR2,
                         o_status OUT NUMBER);


END; -- Package Specification HELP
/

CREATE OR REPLACE PACKAGE BODY message IS

PROCEDURE add_message  (i_title IN VARCHAR2,
                        i_message IN VARCHAR2,
                        i_active_ind IN VARCHAR2,
                        o_message_id OUT NUMBER)

IS

stored_proc_name VARCHAR2(100):= 'MESSAGE.ADD_MESSAGE';
v_message_id NUMBER(20);
v_status NUMBER(20);

BEGIN

   INSERT INTO status_messages (message_id, title, message, active_indicator, timestamp)
         VALUES (message_sequence.nextval, i_title, i_message, i_active_ind , sysdate);

   SELECT message_sequence.currval INTO v_message_id FROM dual;

   o_message_id := v_message_id ;

   COMMIT;

EXCEPTION
   WHEN OTHERS THEN
      err_num:=SQLCODE;
      err_msg:=SUBSTR(SQLERRM,1,120);
      ROLLBACK;
      INSERT INTO pn_sp_error_log
         VALUES (sysdate,stored_proc_name,err_num,err_msg);
      COMMIT;
      raise;

END add_message;


/*-------------------------------------------------------------------------------

EDIT_MESSAGE

Modification History
Date       Programmer  Reason
========   ==========  =================================================
12-Jun-00  Robin       Creation.
--------------------------------------------------------------------------------*/

PROCEDURE edit_message  (i_message_id IN VARCHAR2,
                         i_title IN VARCHAR2,
                         i_message IN VARCHAR2,
                         o_status OUT NUMBER)

IS

stored_proc_name VARCHAR2(100):= 'MESSAGE.EDIT_MESSAGE';

BEGIN

   UPDATE status_messages
      SET title = i_title, message = i_message
      WHERE message_id = i_message_id;

   IF SQL%NOTFOUND THEN
      o_status := no_data;
      RETURN;
   END IF;

   o_status := success;
   COMMIT;

EXCEPTION

   WHEN e_null_constraint THEN
      -- message title empty
      o_status := null_field;

   WHEN OTHERS THEN
      o_status := generic_error;
      err_num:=SQLCODE;
      err_msg:=SUBSTR(SQLERRM,1,120);
      ROLLBACK;
      INSERT INTO pn_sp_error_log
         VALUES (sysdate,stored_proc_name,err_num,err_msg);
      COMMIT;
      raise;

END edit_message;


/*-------------------------------------------------------------------------------

DELETE_MESSAGE

Modification History
Date       Programmer  Reason
========   ==========  =================================================
12-Jun-00  Robin       Creation.
--------------------------------------------------------------------------------*/

PROCEDURE delete_message  (i_message_id IN NUMBER,
                           o_status OUT NUMBER)

IS

stored_proc_name VARCHAR2(100):= 'MESSAGE.DELETE_MESSAGE';


BEGIN

        DELETE FROM status_messages
            WHERE message_id = i_message_id;

   o_status := success;

   COMMIT;

EXCEPTION

   WHEN OTHERS THEN
      o_status := generic_error;
      err_num:=SQLCODE;
      err_msg:=SUBSTR(SQLERRM,1,120);
      ROLLBACK;
      INSERT INTO pn_sp_error_log
         VALUES (sysdate,stored_proc_name,err_num,err_msg);
      COMMIT;
      raise;

END delete_message;


/*-------------------------------------------------------------------------------

SET_STATUS

Modification History
Date       Programmer  Reason
========   ==========  =================================================
12-Jun-00  Robin       Creation.
--------------------------------------------------------------------------------*/

PROCEDURE set_status  (i_message_id IN NUMBER,
                       i_active_ind IN VARCHAR2,
                       o_status OUT NUMBER)

IS

stored_proc_name VARCHAR2(100):= 'MESSAGE.SET_STATUS';

BEGIN

   UPDATE status_messages
      SET active_indicator = i_active_ind
      WHERE message_id = i_message_id;

   IF SQL%NOTFOUND THEN
      o_status := no_data;
      RETURN;
   END IF;

   o_status := success;

   commit;

EXCEPTION

   WHEN OTHERS THEN
      o_status := generic_error;
      err_num:=SQLCODE;
      err_msg:=SUBSTR(SQLERRM,1,120);
      ROLLBACK;
      INSERT INTO pn_sp_error_log
         VALUES (sysdate,stored_proc_name,err_num,err_msg);
      COMMIT;
      raise;

END set_status;


END; -- Package Body MESSAGE
/

CREATE OR REPLACE PACKAGE message IS

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;
is_active CONSTANT NUMBER:=120;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);


PROCEDURE add_message  (i_title IN VARCHAR2,
                        i_message IN VARCHAR2,
                        i_active_ind IN VARCHAR2,
                        o_message_id OUT NUMBER);


PROCEDURE edit_message  (i_message_id IN VARCHAR2,
                         i_title IN VARCHAR2,
                         i_message IN VARCHAR2,
                         o_status OUT NUMBER);


PROCEDURE delete_message  (i_message_id IN NUMBER,
                           o_status OUT NUMBER);


PROCEDURE set_status  (i_message_id IN NUMBER,
                       i_active_ind IN VARCHAR2,
                       o_status OUT NUMBER);



END; -- Package Specification MESSAGE
/

CREATE OR REPLACE PACKAGE BODY methodology IS

/*--------------------------------------------------------------------
  PROCEDURE PROTOTYPES
  Required for private procedures so that they can be used
  lexically earlier than their definition.
--------------------------------------------------------------------*/

    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean;


----------------------------------------------------------------------
-- create_methodology
-- Create a new methodology template from an existing space.
----------------------------------------------------------------------
PROCEDURE create_methodology
    (
        i_name IN pn_methodology_space.methodology_name%type,
        i_description IN pn_methodology_space.methodology_desc%type,
        i_parent_space_id IN pn_methodology_space.methodology_id%type,
        i_creator_id IN pn_person.person_id%type,
        i_industry_id IN pn_industry_classification.industry_id%type,
        i_category_id IN pn_category.category_id%type,
        i_is_global IN pn_methodology_space.is_global%type,
        i_is_use_scenario_null IN NUMBER,
        o_use_scenario_clob OUT pn_methodology_space.use_scenario_clob%type,
        o_object_id OUT number
    )

AS

    -- variable declarations

    v_object_id             pn_object.object_id%type;
    v_timestamp             DATE;


    v_group_id               pn_group.group_id%type;
    v_space_admin_group_id   pn_group.group_id%type;
    v_power_user_group_id   pn_group.group_id%type;
    v_space_id               pn_project_space.project_id%type;
    v_doc_container_id       pn_doc_container.doc_container_id%type := 0;
    v_system_container_id    pn_doc_container.doc_container_id%type :=0;
    v_doc_space_id           pn_doc_space.doc_space_id%type := 0;
    v_doc_provider_id        pn_doc_provider.doc_provider_id%type := 0;
    v_directory_id           pn_directory.directory_id%type := 0;
    v_calendar_id            pn_calendar.calendar_id%type := 0;
    v_portfolio_id           pn_portfolio.portfolio_id%type := 0;
    v_membership_portfolio_id   pn_portfolio.portfolio_id%type := 0;
    is_subproject            NUMBER(1) := 0;
    v_is_private             NUMBER(1) := 0;
    v_property_sheet_id      pn_property_sheet.property_sheet_id%type;
    v_property_group_id      pn_object.object_id%type;
    v_property_sheet_type    pn_property_sheet_type.property_sheet_type%type;

    v_custom_domain          pn_custom_domain%rowtype;
    v_object_type            pn_object_type.object_type%type;
    v_actions                pn_object_type.default_permission_actions%type;
    v_object_type_actions    pn_object_type.default_permission_actions%type;
    v_module_id              pn_module.module_id%type;
    v_plan_id                pn_plan.plan_id%TYPE;

    v_status                 number;
    stored_proc_name         VARCHAR2(100):= 'CREATE_METHODOLOGY';

   -- global (static) variable declaration
   G_SECURITY_MODULE pn_object_type.object_type%type := 'group';


BEGIN

    set transaction read write;

    select sysdate into v_timestamp from dual;

    -- Create the methodology space
    v_object_id := BASE.CREATE_OBJECT(G_methodology_object_type, i_creator_id, G_active_record_status);

    if (i_is_use_scenario_null > 0) then
        -- Insert NULL use scenario
        INSERT INTO pn_methodology_space
            (methodology_id, methodology_name, methodology_desc, use_scenario_clob,
             is_global, status_id, created_by_id, modified_by_id, created_date,
             modified_date, record_status, crc)
         VALUES
            (v_object_id, i_name, i_description, null, i_is_global,
             NULL, i_creator_id, i_creator_id, v_timestamp,
             v_timestamp, g_active_record_status, v_timestamp)
         returning
             use_scenario_clob into o_use_scenario_clob;

    else
        -- Insert empty_clob() use scenario for subsequent streaming
        INSERT INTO pn_methodology_space
            (methodology_id, methodology_name, methodology_desc, use_scenario_clob,
             is_global, status_id, created_by_id, modified_by_id, created_date,
             modified_date, record_status, crc)
         VALUES
            (v_object_id, i_name, i_description, empty_clob(), i_is_global,
             NULL, i_creator_id, i_creator_id, v_timestamp,
             v_timestamp, g_active_record_status, v_timestamp)
         returning
             use_scenario_clob into o_use_scenario_clob;

    end if;

    if (i_industry_id is not null) then
        INSERT INTO pn_methodology_in_industry
            (industry_id, methodology_id)
        values (i_industry_id, v_object_id);
    end if;

    if (i_category_id is not null) then
        insert into pn_object_in_category
            (category_id, object_id)
        values (i_category_id, v_object_id);
    end if;

    /* -------------------------------  Project Security  ------------------------------- */

    -- SPACE_HAS_MODULES
    -- The new methodology space initially has access to all modules.
    -- Copy all pn_module entries to pn_space_has_modules for this space

    INSERT INTO pn_space_has_module (
        space_id,
        module_id,
        is_active)
    SELECT
        v_object_id, module_id, '1'
    FROM
        pn_module
    WHERE
        module_id IN (10, 20, 30, 40, 60, 70, 90, 100, 110, 120, 140, 150, 180, 190, 200, 210, 310);

    -- SPACE ADMINISTRATOR GROUP
    -- The space creator is the inital space administrator
    v_space_admin_group_id := security.F_CREATE_SPACE_ADMIN_GROUP(i_creator_id, v_object_id, '@prm.methodology.security.group.type.spaceadmin.description');


     -- POWER USER GROUP
    --  CREATE POWER USER GROUP
    v_power_user_group_id := security.F_CREATE_POWER_USER_GROUP(i_creator_id, v_object_id, '@prm.security.group.type.poweruser.description');


    -- PRINCIPAL GROUP
    -- The space creator (person) must be put into a principal group for this space.
    v_group_id := NULL;
    SECURITY.CREATE_PRINCIPAL_GROUP(i_creator_id, v_object_id, v_group_id);

    -- add project creator (person) to their principal group
    v_status := NULL;
    SECURITY.ADD_PERSON_TO_GROUP(v_group_id, i_creator_id, v_status);


    -- TEAM MEMBER GROUP
    -- The creator is the only initial team member
    v_group_id := NULL;
    SECURITY.CREATE_TEAMMEMBER_GROUP(i_creator_id, v_object_id, v_group_id);

    v_status := NULL;
    SECURITY.ADD_PERSON_TO_GROUP(v_group_id, i_creator_id, v_status);



    /* -------------------------------  Directory Configuration  ------------------------------- */

     --  Add the creator to the roster
    INSERT INTO pn_space_has_person
        (space_id, person_id, relationship_person_to_space, member_type_id, record_status)
    VALUES
        (v_object_id, i_creator_id, 'member', 200, G_active_record_status);


    -- Every space gets the default directory
    Select directory_id into v_directory_id from pn_directory where is_default = 1;

    INSERT INTO pn_space_has_directory
        (directory_id, space_id)
    VALUES
        (v_directory_id, v_object_id);



    /* -------------------------------  Document Space Configuration  ------------------------------- */

    -- use only default doc provider initially
    select doc_provider_id into v_doc_provider_id
    from pn_doc_provider
    where is_default = 1;  -- default provider

    -- new doc space for this space
    v_doc_space_id := BASE.CREATE_OBJECT('doc_space', i_creator_id, G_active_record_status);

    insert into pn_doc_space
        (doc_space_id, doc_space_name, record_status, crc)
    values
        (v_doc_space_id, 'default', G_active_record_status, v_timestamp);

    -- this space owns the doc space
    insert into pn_space_has_doc_space
      (space_id, doc_space_id)
    values
      (v_object_id, v_doc_space_id);

    -- link new doc_space back to it's doc_provider
    insert into pn_doc_provider_has_doc_space
        (doc_provider_id, doc_space_id)
    values
        (v_doc_provider_id, v_doc_space_id);


    -- Create new doc container for the Top-level folder for this project.
    v_doc_container_id := BASE.CREATE_OBJECT('doc_container', i_creator_id, G_active_record_status);

    insert into pn_doc_container
        (doc_container_id, container_name, container_description, modified_by_id, date_modified,record_status, crc)
    values
        (v_doc_container_id, '@prm.document.container.topfolder.name', 'Top level document folder', i_creator_id, v_timestamp, G_active_record_status, v_timestamp);

    SECURITY.APPLY_DOCUMENT_PERMISSIONS(v_doc_container_id, null, 'doc_container', v_object_id, i_creator_id);

    -- Link container (top folder) to doc space
    -- PHIL added the "is_root:1" to pn_doc_space_has_container
    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_doc_container_id, 1);


    -- SYSTEM Container for this space
    -- this container contains (*grin*) all system related objects
    v_system_container_id := BASE.CREATE_OBJECT('doc_container', i_creator_id, G_active_record_status);

    insert into pn_doc_container
        (doc_container_id, container_name, container_description, modified_by_id, date_modified,record_status, is_hidden, crc)
    values
        (v_system_container_id, v_object_id, 'System container for this space',i_creator_id, v_timestamp,G_active_record_status, 1, v_timestamp);

    SECURITY.APPLY_DOCUMENT_PERMISSIONS(v_system_container_id, v_doc_container_id, 'doc_container', v_object_id, i_creator_id);

    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_system_container_id, 0);

    insert into pn_doc_container_has_object
        (doc_container_id, object_id)
    values
        (v_doc_container_id, v_system_container_id);


    -- Metrics container (contained by system container)
    v_doc_container_id := BASE.CREATE_OBJECT('doc_container', i_creator_id, G_active_record_status);

    insert into pn_doc_container
        (doc_container_id, container_name, container_description, modified_by_id, date_modified,record_status, is_hidden, crc)
    values
        (v_doc_container_id, v_object_id||'::Metrics', 'System container for this space', i_creator_id, v_timestamp, G_active_record_status, 1, v_timestamp);

    SECURITY.APPLY_DOCUMENT_PERMISSIONS(v_doc_container_id, v_system_container_id,'doc_container', v_object_id, i_creator_id);

    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_doc_container_id, 0);

    insert into pn_doc_container_has_object
        (doc_container_id, object_id)
    values
        (v_system_container_id, v_doc_container_id);


    -- Budget Container
    v_doc_container_id := BASE.CREATE_OBJECT('doc_container', i_creator_id, G_active_record_status);

    insert into pn_doc_container
        (doc_container_id, container_name, container_description, modified_by_id, date_modified,record_status, is_hidden, crc)
    values
        (v_doc_container_id, v_object_id||'::Budget', 'System container for this space', i_creator_id, v_timestamp,G_active_record_status, 1, v_timestamp);

    SECURITY.APPLY_DOCUMENT_PERMISSIONS(v_doc_container_id, v_system_container_id, 'doc_container', v_object_id, i_creator_id);

    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_doc_container_id, 0);

    insert into pn_doc_container_has_object
        (doc_container_id, object_id)
    values
        (v_system_container_id, v_doc_container_id);


    -- Org Chart container
    v_doc_container_id := BASE.CREATE_OBJECT('doc_container', i_creator_id, G_active_record_status);

    insert into pn_doc_container
        (doc_container_id, container_name, container_description, modified_by_id, date_modified,record_status, is_hidden, crc)
    values
        (v_doc_container_id, v_object_id||'::Organization Chart', 'System container for this space', i_creator_id, v_timestamp,G_active_record_status, 1, v_timestamp);

    SECURITY.APPLY_DOCUMENT_PERMISSIONS(v_doc_container_id, v_system_container_id, 'doc_container', v_object_id, i_creator_id);

    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_doc_container_id, 0);

    insert into pn_doc_container_has_object
        (doc_container_id, object_id)
    values
        (v_system_container_id, v_doc_container_id);


    /* -------------------------------  Space Portfolio Configuration  -------------------------------

    -- Add this project to the creator's "membership_portfolio".
    -- set is_private flag is not visibility="personal".

    IF (i_creator_id <> 0)
    THEN
        SELECT
             membership_portfolio_id INTO v_membership_portfolio_id
        FROM
             pn_person
        WHERE
            person_id = i_creator_id;
    END IF;

    -- the creator always gets the space added to their membership portfolio
    INSERT INTO pn_portfolio_has_space
        (portfolio_id, space_id)
    VALUES
        (v_membership_portfolio_id, v_object_id);


    -- OWNERSHIP setup

        -- pn_space_has_space determines true ownership (security, etc.)
        INSERT INTO pn_space_has_space
            (parent_space_id, child_space_id, relationship_parent_to_child ,created_by, date_created, record_status)
        VALUES
            (i_parent_space_id, v_object_id, 'owns', i_creator_id, v_timestamp, G_active_record_status);

        -- "owner" portfolio is used for grouping projects in the portfolio management pages.
        -- Get the business' project "owner" portfolio
        select
            p.portfolio_id into v_portfolio_id
        from
            pn_space_has_portfolio shp, pn_portfolio p
        where
            shp.space_id = p_business_space_id and
            shp.portfolio_id=p.portfolio_id and
            portfolio_name='owner';

        -- Add this project to the Business Space's Owner portfolio
        insert into pn_portfolio_has_space
            (portfolio_id, project_id, is_private)
        values
            (v_portfolio_id, v_object_id, v_is_private);
    */


    /* -------------------------------  Space Ownership Configuration  ------------------------------- */


    INSERT INTO pn_space_has_space
        (parent_space_id, child_space_id, relationship_parent_to_child ,created_by, date_created, record_status)
    VALUES
        (i_parent_space_id, v_object_id, 'owns', i_creator_id, v_timestamp, G_active_record_status);

    /* ------------------ Schedule ----------------------- */
    SCHEDULE.STORE_PLAN('Project Plan', 'Main Project Plan', null, null, 1, null, null, i_creator_id, v_object_id, null, null, null, null, null, '50', null, v_plan_id);


 o_object_id := v_object_id;

 EXCEPTION

    WHEN OTHERS THEN
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;

END;



----------------------------------------------------------------------
-- MODIFY_METHODOLOGY
-- Update an existing methodology
----------------------------------------------------------------------
procedure modify_methodology (
    i_methodology_id    in number,
    i_name              in pn_methodology_space.methodology_name%type,
    i_description       in pn_methodology_space.methodology_desc%type,
    i_parent_space_id   in pn_methodology_space.methodology_id%type,
    i_modified_by_id    in pn_person.person_id%type,
    i_industry_id       in pn_industry_classification.industry_id%type,
    i_category_id       in pn_category.category_id%type,
    i_is_global 	in pn_methodology_space.is_global%type,
    i_crc               in date,
    i_is_use_scenario_null in number,
    o_use_scenario_clob out pn_methodology_space.use_scenario_clob%type
)
is
    -- User defined exceptions
    record_modified exception;  -- CRC differs
    record_locked   exception;  -- Record being updated by another user
    pragma exception_init (record_locked, -00054);

    v_datetime          date;
    v_current_crc       pn_methodology_space.crc%type;

    stored_proc_name varchar2(100):= 'METHODOLOGY.MODIFY_METHODOLOGY';

begin
    -- Check methodology has not changed
    select
        crc into v_current_crc
    from
        pn_methodology_space
    where
        methodology_id = i_methodology_id
    for update nowait;

    if (crc_matches(i_crc, v_current_crc)) then

        select sysdate into v_datetime from dual;

        if (i_is_use_scenario_null > 0) then
            -- Update methodology setting use_scenario to null
            UPDATE pn_methodology_space m
               SET m.methodology_name = i_name,
                   m.methodology_desc = i_description,
                   m.is_global = i_is_global,
                   m.use_scenario_clob = null,
                   m.modified_by_id = i_modified_by_id,
                   m.modified_date = v_datetime
             WHERE m.methodology_id = i_methodology_id
             returning use_scenario_clob into o_use_scenario_clob;

        else
            -- Update methodology setting use_scenario to empty_clob()
            UPDATE pn_methodology_space m
               SET m.methodology_name = i_name,
                   m.methodology_desc = i_description,
                   m.is_global = i_is_global,
                   m.use_scenario_clob = empty_clob(),
                   m.modified_by_id = i_modified_by_id,
                   m.modified_date = v_datetime
             WHERE m.methodology_id = i_methodology_id
             returning use_scenario_clob into o_use_scenario_clob;

        end if;


        -- Update parent space
        delete from
            pn_space_has_space
        where
            child_space_id = i_methodology_id;

        if (i_parent_space_id is not null) then
            INSERT INTO pn_space_has_space
                (parent_space_id, child_space_id, relationship_parent_to_child ,created_by, date_created, record_status)
            VALUES
                (i_parent_space_id, i_methodology_id, 'owns', i_modified_by_id, v_datetime, G_active_record_status);
        end if;

        -- Update industry
        delete from
            pn_methodology_in_industry
        where
            methodology_id = i_methodology_id;

        if (i_industry_id is not null) then
            INSERT INTO pn_methodology_in_industry
                (industry_id, methodology_id)
            values (i_industry_id, i_methodology_id);
        end if;

        -- Update category
        delete from
            pn_object_in_category
        where
            object_id = i_methodology_id;

        if (i_category_id is not null) then
            insert into pn_object_in_category
                (category_id, object_id)
            values (i_category_id, i_methodology_id);
        end if;

    else
        -- crc different
        raise record_modified;
    end if;

exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end modify_methodology;

----------------------------------------------------------------------
-- GET_METHODOLOGY_FOR_USER
-- Retrieves a list of methodologies a user is able to use when creating
-- projects.
----------------------------------------------------------------------
FUNCTION get_methodology_for_user (
         i_user_id IN pn_person.person_id%TYPE)
  RETURN ReferenceCursor IS
    err_num NUMBER;
    err_msg VARCHAR2 ( 120 );
    stored_proc_name VARCHAR2 ( 100 ) := 'METHODOLOGY.GET_METHODOLOGY_FOR_USER';
    methodology_list ReferenceCursor;
BEGIN
  OPEN methodology_list FOR
    select m.methodology_id, m.methodology_name,shs.parent_space_id, s.parent_space_name
    from pn_methodology_by_user_view m, pn_space_has_space shs, pn_space_view s
    where (m.parent_space_id IN (
      select b.business_space_id
      from pn_person p, pn_space_has_person sp, pn_business_space b
      where p.person_id = i_user_id and
            p.person_id = sp.person_id and
            sp.space_id = b.business_space_id
      ) or m.person_id = i_user_id or m.is_global = 1)
      and shs.child_space_id = m.methodology_id
      and s.space_id = shs.child_space_id and s.record_status = 'A'
      and m.record_status='A' order by s.space_name, m.methodology_name asc;
  RETURN methodology_list;
EXCEPTION
    WHEN OTHERS THEN
    BEGIN
        ROLLBACK;
        err_num := SQLCODE;
        err_msg := SUBSTR ( SQLERRM,
                    1,
                    120 );
        INSERT INTO pn_sp_error_log
        VALUES ( SYSDATE,
                 stored_proc_name,
                 err_num,
                 err_msg );
        COMMIT;
    END;
END;



----------------------------------------------------------------------
-- PRIVATE crc_matches
-- Compare two crc values
-- i_original_crc - first crc value
-- i_new_crc - second cec value
-- returns true if both values are identical, false otherwise
----------------------------------------------------------------------
    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean
    is
        v_crc_match     boolean := false;

    begin
        if (i_original_crc = i_new_crc) then
            v_crc_match := true;
        end if;
        return v_crc_match;
    end crc_matches;
----------------------------------------------------------------------

END; -- Package Body METHODOLOGY
/

CREATE OR REPLACE PACKAGE methodology IS

    G_methodology_object_type              pn_object.object_type%type := 'methodology';
    G_active_record_status                 pn_methodology_space.record_status%type := 'A';
    TYPE ReferenceCursor            IS REF CURSOR;

----------------------------------------------------------------------
-- CREATE_METHODOLOGY
----------------------------------------------------------------------
    PROCEDURE create_methodology
      (
        i_name IN pn_methodology_space.methodology_name%type,
        i_description IN pn_methodology_space.methodology_desc%type,
        i_parent_space_id IN pn_methodology_space.methodology_id%type,
        i_creator_id IN pn_person.person_id%type,
        i_industry_id IN pn_industry_classification.industry_id%type,
        i_category_id IN pn_category.category_id%type,
        i_is_global IN pn_methodology_space.is_global%type,
        i_is_use_scenario_null IN NUMBER,
        o_use_scenario_clob OUT pn_methodology_space.use_scenario_clob%type,
        o_object_id OUT number
      );

----------------------------------------------------------------------
-- MODIFY_METHODOLOGY
-- Update an existing methodology
----------------------------------------------------------------------
procedure modify_methodology (
    i_methodology_id    in number,
    i_name              in pn_methodology_space.methodology_name%type,
    i_description       in pn_methodology_space.methodology_desc%type,
    i_parent_space_id   in pn_methodology_space.methodology_id%type,
    i_modified_by_id    in pn_person.person_id%type,
    i_industry_id       in pn_industry_classification.industry_id%type,
    i_category_id       in pn_category.category_id%type,
    i_is_global 		in pn_methodology_space.is_global%type,
    i_crc               in date,
    i_is_use_scenario_null in number,
    o_use_scenario_clob out pn_methodology_space.use_scenario_clob%type
);

----------------------------------------------------------------------
-- GET_METHODOLOGY_FOR_USER
-- Retrieves a list of methodologies a user is able to use when creating
-- projects.
----------------------------------------------------------------------
FUNCTION get_methodology_for_user (
         i_user_id IN pn_person.person_id%TYPE
) RETURN ReferenceCursor;
END; -- Package Specification METHODOLOGY
/

CREATE OR REPLACE PACKAGE BODY news IS
--==================================================================
-- Purpose: News procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        27-Jan-01  Created it.
-- Deepak      10-Oct-01  Modified it to log events
--==================================================================

/*--------------------------------------------------------------------
  PROCEDURE PROTOTYPES
  Required for private procedures so that they can be used
  lexically earlier than their definition.
--------------------------------------------------------------------*/

    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean;

    function getSpaceID(i_news_id in number)
        return number;
/*--------------------------------------------------------------------
  END OF PROCEDURE PROTOTYPES
--------------------------------------------------------------------*/


----------------------------------------------------------------------
-- CREATE_NEWS
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    PROCEDURE create_news
      ( i_space_id       IN varchar2,
        i_topic          IN varchar2,
        i_priority_id    in varchar2,
        i_notification_id    in varchar2,
        i_posted_by_id   in varchar2,
        i_posted_datetime in date,
        i_created_by_id  IN varchar2,
        i_is_message_null in number,
        o_message_clob   OUT clob,
        o_news_id        OUT varchar2)
    IS
        v_space_id      pn_space_has_news.space_id%type := to_number(i_space_id);
        v_priority_id   pn_news.priority_id%type := to_number(i_priority_id);
        v_notification_id   pn_news.notification_id%type := to_number(i_notification_id);
        v_posted_by_id  pn_news.posted_by_id%type := to_number(i_posted_by_id);
        v_created_by_id pn_news.created_by_id%type := to_number(i_created_by_id);

        v_datetime      date;
        v_news_id       pn_news.news_id%type;

        stored_proc_name VARCHAR2(100):= 'NEWS.CREATE_NEWS';

    BEGIN

        -- Create new entry in pn_object
        v_news_id := base.create_object(
            G_news_object_type,
            v_created_by_id,
            base.active_record_status);

        SECURITY.CREATE_SECURITY_PERMISSIONS(v_news_id, G_news_object_type, v_space_id, v_created_by_id);

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        if (i_is_message_null = 1) then
            -- Create the news record with a null CLOB
            INSERT INTO pn_news (news_id, topic, message_clob, priority_id, notification_id,
                posted_by_id, posted_datetime, created_by_id, created_datetime,
                crc, record_status)
            VALUES (v_news_id, i_topic, null, v_priority_id, v_notification_id,
                v_posted_by_id, i_posted_datetime, v_created_by_id, v_datetime,
                sysdate, base.active_record_status)
            RETURNING message_clob into o_message_clob;
        else
            -- Create the news record with an empty CLOB
            INSERT INTO pn_news (news_id, topic, message_clob, priority_id, notification_id,
                posted_by_id, posted_datetime, created_by_id, created_datetime,
                crc, record_status)
            VALUES (v_news_id, i_topic, empty_clob, v_priority_id, v_notification_id,
                v_posted_by_id, i_posted_datetime, v_created_by_id, v_datetime,
                sysdate, base.active_record_status)
            RETURNING message_clob into o_message_clob;
        end if;

        -- Associate news with space
        INSERT INTO pn_space_has_news (
            space_id,
            news_id)
        VALUES (
            v_space_id,
            v_news_id);

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

        -- Set output parameters
        o_news_id := to_char(v_news_id);

      EXCEPTION
      WHEN OTHERS THEN
        BEGIN
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        END;

      END;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_NEWS
-- Update an existing news
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure modify_news (
        i_news_id        in varchar2,
        i_topic          IN varchar2,
        i_priority_id    in varchar2,
        i_notification_id    in varchar2,
        i_posted_by_id   in varchar2,
        i_posted_datetime in date,
        i_modified_by_id in varchar2,
        i_crc            in date,
        i_is_message_null in number,
        o_message_clob   OUT clob)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_news_id        pn_news.news_id%type := to_number(i_news_id);
        v_priority_id    pn_news.priority_id%type := to_number(i_priority_id);
        v_notification_id   pn_news.notification_id%type := to_number(i_notification_id);
        v_posted_by_id   pn_news.posted_by_id%type := to_number(i_posted_by_id);
        v_modified_by_id pn_news.created_by_id%type := to_number(i_modified_by_id);

        v_datetime       date;
        v_current_crc    pn_news.crc%type;

        stored_proc_name varchar2(100):= 'NEWS.MODIFY_NEWS';
    begin
        -- Check news has not changed
        select crc into v_current_crc
            from pn_news
            where news_id = v_news_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            if (i_is_message_null = 1) then
                -- Set the message clob to null
                update
                    pn_news n
                set
                    n.topic = i_topic,
                    n.message_clob = null,
                    n.priority_id = v_priority_id,
                    n.notification_id = v_notification_id,
                    n.posted_by_id = v_posted_by_id,
                    n.posted_datetime = i_posted_datetime,
                    n.modified_by_id = v_modified_by_id,
                    n.modified_datetime = v_datetime,
                    n.crc = sysdate,
                    n.record_status = base.active_record_status
                where
                    n.news_id = v_news_id
                returning
                    n.message_clob into o_message_clob;
            else
                -- Set the message clob to empty_clob() so that it can
                -- be replaced with new data
                update
                    pn_news n
                set
                    n.topic = i_topic,
                    n.message_clob = empty_clob,
                    n.priority_id = v_priority_id,
                    n.notification_id = v_notification_id,
                    n.posted_by_id = v_posted_by_id,
                    n.posted_datetime = i_posted_datetime,
                    n.modified_by_id = v_modified_by_id,
                    n.modified_datetime = v_datetime,
                    n.crc = sysdate,
                    n.record_status = base.active_record_status
                where
                    n.news_id = v_news_id
                returning
                    n.message_clob into o_message_clob;
            end if;

        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

    exception
        when others then
        begin
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        end;
    end modify_news;

----------------------------------------------------------------------


----------------------------------------------------------------------
-- REMOVE_NEWS
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_news (
        i_news_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_news_id        pn_news.news_id%type := to_number(i_news_id);
        v_modified_by_id pn_news.created_by_id%type := to_number(i_modified_by_id);

        v_datetime       date;
        v_current_crc    pn_news.crc%type;

        stored_proc_name varchar2(100):= 'NEWS.REMOVE_NEWS';
    begin
        -- Check news has not changed, may raise record_locked exception
        select crc into v_current_crc
            from pn_news
            where news_id = v_news_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then
            select sysdate into v_datetime from dual;
            update
                pn_news n
            set
                n.modified_by_id = v_modified_by_id,
                n.modified_datetime = v_datetime,
                n.crc = sysdate,
                n.record_status = base.deleted_record_status
            where
                n.news_id = v_news_id;

            o_return_value := BASE.OPERATION_SUCCESSFUL;
        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

    exception
        when record_locked then
        begin
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;
    end remove_news;
----------------------------------------------------------------------


----------------------------------------------------------------------
-- PRIVATE crc_matches
-- Compare two crc values
-- i_original_crc - first crc value
-- i_new_crc - second cec value
-- returns true if both values are identical, false otherwise
----------------------------------------------------------------------
    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean
    is
        v_crc_match     boolean := false;

    begin
        if (i_original_crc = i_new_crc) then
            v_crc_match := true;
        end if;
        return v_crc_match;
    end crc_matches;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE getSpaceID
-- get the space id of a news
-- i_news_id - the news id to get space id from
-- returns space_id
-- throws exception if there is a problem
----------------------------------------------------------------------
    function getSpaceID(i_news_id in number)
        return number
    is
        v_space_id  pn_space_has_news.space_id%type := null;

    begin
        select
            sp.space_id into v_space_id
        from
            pn_space_has_news sp
        where
            sp.news_id = i_news_id;

        return v_space_id;

    exception
        when NO_DATA_FOUND then
        begin
            raise space_not_found;
        end;
    end getSpaceID;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- LOGS_EVENT
----------------------------------------------------------------------
 PROCEDURE log_event
    (
        news_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    )

IS
    v_news_id     pn_news.news_id%type := TO_NUMBER(news_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_news_history.news_history_id%type;
    v_action          pn_news_history.action%type := action;
    v_action_name     pn_news_history.action_name%type := action_name;
    v_action_comment  pn_news_history.action_comment%type := notes;

BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- insert a history record for this document

    insert into pn_news_history (
        news_id,
        news_history_id,
        action,
        action_name,
        action_by_id,
        action_date,
        action_comment)
    values (
        v_news_id,
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
         base.log_error('NEWS.LOG_EVENT', sqlcode, sqlerrm);
        raise;
    END;
END log_event;

-- Procedure LOG_EVENT
----------------------------------------------------------------------


END; -- Package Body NEWS
/

CREATE OR REPLACE PACKAGE news IS
--==================================================================
-- Purpose: PNET News procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        27-Jan-01  Created it.
-- Deepak      10-Oct-01  Modified it to log events
--==================================================================

   -- Package constants
   G_news_object_type           constant pn_object.object_type%type := 'news';

   -- Raise-able errors
   unspecified_error  exception;
   pragma exception_init(unspecified_error, -20000);
   space_not_found    exception;

----------------------------------------------------------------------
-- CREATE_NEWS
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    PROCEDURE create_news
      ( i_space_id       IN varchar2,
        i_topic          IN varchar2,
        i_priority_id    in varchar2,
        i_notification_id    in varchar2,
        i_posted_by_id   in varchar2,
        i_posted_datetime in date,
        i_created_by_id  IN varchar2,
        i_is_message_null in number,
        o_message_clob   OUT clob,
        o_news_id        OUT varchar2);

----------------------------------------------------------------------
-- MODIFY_NEWS
-- Update an existing news
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure modify_news (
        i_news_id        in varchar2,
        i_topic          IN varchar2,
        i_priority_id    in varchar2,
        i_notification_id    in varchar2,
        i_posted_by_id   in varchar2,
        i_posted_datetime in date,
        i_modified_by_id in varchar2,
        i_crc            in date,
        i_is_message_null in number,
        o_message_clob   OUT clob);

----------------------------------------------------------------------
-- REMOVE_NEWS
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_news (
        i_news_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
----------------------------------------------------------------------



----------------------------------------------------------------------
-- LOG_EVENT
-- Logs the event that happens to the NEWS item
----------------------------------------------------------------------
   PROCEDURE log_event
    (
        news_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    );
----------------------------------------------------------------------


END; -- Package Specification NEWS
/

CREATE OR REPLACE PACKAGE BODY notification IS
--==================================================================
-- Purpose: Subscription procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Chad        05-Sep-00  Created it.
-- Tim         24-Jul-01  Added is_immediate to create_notification
--                        Modified get_notifications to return only
--                        non-immediate notifications
--==================================================================

----------------------------------------------------------------------



----------------------------------------------------------------------
PROCEDURE  create_type_subscription
(
      i_subscription_name          IN       VARCHAR2,   -- the user created name of the subscription
      i_subscription_description   IN       VARCHAR2,   -- the user created description of the subscription
      --what will be sent in the event of a notification
      i_subscription_type_id       IN       VARCHAR2,   -- the system created type of subscription
      i_target_type                IN       VARCHAR2,   -- the system defined type of object that this subscription was created for, if it was created around an object typee, and not a particular object. Null otherwise.
      i_delivery_interval          IN       VARCHAR2,   -- the interval (weekly daily immediately etc...) that the subscriber has chosen to recieve notifications on
      i_delivery_address_group     IN       VARCHAR2,   -- the user entered delivery address (URI)
      i_created_by_id              IN       VARCHAR2,   -- the entity which created this subscription, possibly the system itself.
      i_space_id                   IN       VARCHAR2,
      i_is_custom_message_null     IN       NUMBER,
      o_custom_message_clob        OUT      CLOB,
      o_subscription_id            OUT      NUMBER
  )
   IS
     v_delivery_interval      pn_subscription.delivery_interval%TYPE
               := TO_NUMBER (i_delivery_interval);
     v_delivery_group_id      pn_delivery_address.address_group_id%TYPE
               := TO_NUMBER (i_delivery_address_group);
      v_subscription_type_id   pn_subscription_type.subscription_type_id%TYPE
               := TO_NUMBER (i_subscription_type_id);
      v_created_by_id          pn_subscription.created_by_id%TYPE
               := TO_NUMBER (i_created_by_id);
     v_space_id   pn_space_has_subscription.space_id%TYPE
               := TO_NUMBER (i_space_id);

      v_datetime               DATE;
      v_subscription_id        pn_object.object_id%TYPE;
      -- Standard error handling
      stored_proc_name         VARCHAR2(100)
               := 'NOTIFICATION.create_subscription';
   BEGIN
      -- Create new entry in pn_object gets the guid and inserts a row into pn_object
      -- base.creat_object == table.sotred procedure.  This effects two ends.
      --One is to create an entry into the pn_object table.
      --The other is to have this methd return that object's GUID.
      v_subscription_id :=
         create_new_object (
            g_subscription_object_type,
            v_created_by_id,
            g_active_record_status
         );

      -- Get current datetime. This has to be done manually, then entered into the table
      SELECT SYSDATE INTO v_datetime FROM dual;

      if (i_is_custom_message_null > 0) then
          -- Create the subscription record with null custom message
          INSERT INTO pn_subscription
              ( subscription_id, name, description, custom_message_clob,
                subscriber_batch_id, delivery_interval, created_by_id, subscription_type_id,
                created_date, modified_date, modified_by, record_status, crc )
          VALUES
              ( v_subscription_id, i_subscription_name, i_subscription_description, null,
                v_delivery_group_id, v_delivery_interval, v_created_by_id, v_subscription_type_id,
                v_datetime, v_datetime, v_created_by_id, g_active_record_status, v_datetime)
          returning
              custom_message_clob into o_custom_message_clob;

      else
          -- Create the subscription record with empty custom message for
          -- subsequent streaming
          INSERT INTO pn_subscription
              ( subscription_id, name, description, custom_message_clob,
                subscriber_batch_id, delivery_interval, created_by_id, subscription_type_id,
                created_date, modified_date, modified_by, record_status, crc )
          VALUES
              ( v_subscription_id, i_subscription_name, i_subscription_description, empty_clob(),
                v_delivery_group_id, v_delivery_interval, v_created_by_id, v_subscription_type_id,
                v_datetime, v_datetime, v_created_by_id, g_active_record_status, v_datetime)
          returning
              custom_message_clob into o_custom_message_clob;

      end if;

      INSERT INTO pn_object_type_subscription (subscription_id, object_type)
           VALUES (v_subscription_id, i_target_type);

      insert into pn_space_has_subscription (subscription_id, space_id)
            values (v_subscription_id, v_space_id);

      -- Set output parameters
      o_subscription_id := v_subscription_id;

   EXCEPTION
      WHEN OTHERS THEN
          base.log_error (stored_proc_name, SQLCODE, SQLERRM);
          raise;
   END;


PROCEDURE create_notification
(
    i_delivery_address  IN  varchar2,
    i_notification_clob_id  IN  number,
    i_delivery_type_id IN number,
    i_delivery_from_address IN varchar2,
    i_customization_user_id IN varchar2,
    i_sender_id IN varchar2,
    i_is_immediate in number,
    o_notification_id OUT number,
    o_return_value OUT number
 )
  IS

    v_notification_id       pn_notification.notification_id%type;
    v_timestamp DATE := SYSDATE;
    stored_proc_name VARCHAR2(100) := 'NOTIFICATION.create_notification';
    v_pnet_admin_id pn_person.person_id%type := 1000;

BEGIN

    select pn_object_sequence.nextval into v_notification_id from dual;

    -- first create a master notification record
    insert into pn_notification
    (
        notification_id,
        notification_clob_id,
        delivery_address,
        delivery_from_address,
        delivery_type_id,
        created_date,
        created_by_id,
        customization_user_id,
        modified_date,
        modified_by_id,
        sender_id,
        record_status,
        crc
    ) values
    (
        v_notification_id,
        i_notification_clob_id,
        i_delivery_address,
        i_delivery_from_address,
        i_delivery_type_id,
        v_timestamp,
        v_pnet_admin_id,
        i_customization_user_id,
        v_timestamp,
        v_pnet_admin_id,
        i_sender_id,
        BASE.ACTIVE_RECORD_STATUS,
        v_timestamp
    );


    -- now create an entry in the queue
    insert into pn_notification_queue
    (
        notification_id,
        posted_date,
        posted_by_id,
        is_immediate,
        record_status
    ) values
    (
        v_notification_id,
        v_timestamp,
        v_pnet_admin_id,
        i_is_immediate,
        BASE.ACTIVE_RECORD_STATUS
    );

      o_notification_id := v_notification_id;
      o_return_value := 0;   -- these are defined in our packages. This is success

      COMMIT;

   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            rollback;
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;


PROCEDURE  create_object_subscription
(
      i_subscription_name          IN       VARCHAR2,   -- the user created name of the subscription
      i_subscription_description   IN       VARCHAR2,   -- the user created description of the subscription
      i_subscription_type_id       IN       VARCHAR2,   -- the system created type of subscription
      i_target_id                  IN       VARCHAR2,   -- the system defined type of object that this subscription was created for, if it was created around an object typee, and not a particular object. Null otherwise.
      i_delivery_interval          IN       VARCHAR2,   -- the interval (weekly daily immediately etc...) that the subscriber has chosen to recieve notifications on
      i_delivery_address_group     IN       VARCHAR2,   -- the user entered delivery address (URI)
      i_created_by_id              IN       VARCHAR2,   -- the entity which created this subscription, possibly the system itself.
      i_space_id                   IN       VARCHAR2,
      i_is_custom_message_null     IN       NUMBER,
      o_custom_message_clob        OUT      CLOB,
      o_subscription_id            OUT      NUMBER
  )
   IS
      -- Convert input varchar2() to numbers
      v_delivery_group_id      pn_delivery_address.address_group_id%TYPE
               := TO_NUMBER (i_delivery_address_group);
      v_delivery_interval      pn_subscription.delivery_interval%TYPE
               := TO_NUMBER (i_delivery_interval);

      v_subscription_type_id   pn_subscription_type.subscription_type_id%TYPE
               := TO_NUMBER (i_subscription_type_id);
      v_created_by_id          pn_subscription.created_by_id%TYPE
               := TO_NUMBER (i_created_by_id);
      v_target_id   pn_object_has_subscription.object_id%TYPE
               := TO_NUMBER (i_target_id);
      v_space_id   pn_space_has_subscription.space_id%TYPE
               := TO_NUMBER (i_space_id);

      v_datetime               DATE;
      v_subscription_id        pn_object.object_id%TYPE;
      -- Standard error handling
      stored_proc_name         VARCHAR2(100)
               := 'NOTIFICATION.create_object_subscription';


   BEGIN
      -- Create new entry in pn_object gets the guid and inserts a row into pn_object
      -- base.creat_object == table.sotred procedure.  This effects two ends.
      --One is to create an entry into the pn_object table.
      --The other is to have this methd return that object's GUID.
      v_subscription_id :=
         create_new_object (
            g_subscription_object_type,
            v_created_by_id,
            g_active_record_status
         );
      -- Get current datetime. This has to be done manually, then entered into the table
      SELECT SYSDATE
        INTO v_datetime
        FROM dual;   -- RETURNS DATE AND TIME SELECT SYSDATE INTO THE SAID VAR.

      if (i_is_custom_message_null > 0) then
          -- Create the subscription record with null custom message
          INSERT INTO pn_subscription
              ( subscription_id, name, description, custom_message_clob,
                subscriber_batch_id, delivery_interval, created_by_id, subscription_type_id,
                created_date, modified_date, modified_by, record_status, crc )
          VALUES
              ( v_subscription_id, i_subscription_name, i_subscription_description, null,
                v_delivery_group_id, v_delivery_interval, v_created_by_id, v_subscription_type_id,
                v_datetime, v_datetime, v_created_by_id, g_active_record_status, v_datetime )
          returning
              custom_message_clob into o_custom_message_clob;
      else
          -- Create the subscription record with empty custom message
          INSERT INTO pn_subscription
              ( subscription_id, name, description, custom_message_clob,
                subscriber_batch_id, delivery_interval, created_by_id, subscription_type_id,
                created_date, modified_date, modified_by, record_status, crc )
          VALUES
              ( v_subscription_id, i_subscription_name, i_subscription_description, empty_clob(),
                v_delivery_group_id, v_delivery_interval, v_created_by_id, v_subscription_type_id,
                v_datetime, v_datetime, v_created_by_id, g_active_record_status, v_datetime )
          returning
              custom_message_clob into o_custom_message_clob;

      end if;

      INSERT INTO pn_object_has_subscription (subscription_id, object_id)
           VALUES (v_subscription_id, v_target_id);

      insert into pn_space_has_subscription (subscription_id, space_id)
            values (v_subscription_id, v_space_id);

      -- Set output parameters
      o_subscription_id := v_subscription_id;

   EXCEPTION
      WHEN OTHERS THEN
          base.log_error (stored_proc_name, SQLCODE, SQLERRM);
          raise;
   END;



--==================================================================
-- Purpose: Subscription procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Chad        05-Sep-00  Created it.
--==================================================================

----------------------------------------------------------------------
-- makes an entry into pn_scheduled_subscription
----------------------------------------------------------------------
 PROCEDURE create_scheduled_subscription
(
      i_event_type                  IN       VARCHAR2,
      i_target_id                   IN       NUMBER,
      i_target_type                 IN       VARCHAR2,
      i_event_time                  IN       DATE,
      i_description                 IN       VARCHAR2,
      i_target_object_clob_id       IN       NUMBER,
      i_initiator_id                IN       NUMBER,
      i_space_id                    IN       NUMBER,
      o_scheduled_subscription_id   OUT      NUMBER
   )
   IS
      /* these are my local var definitions */
      v_datetime                    DATE;
      v_delivery_date               DATE;
      v_custom_message_clob         pn_scheduled_subscription.custom_message_clob%type;

      -- Standard error handling
      stored_proc_name              VARCHAR2(100)
               := 'NOTIFICATION.create_scheduled_subscription';
      v_event_type_id               NUMBER
               := get_event_type_for_name(i_event_type);


      /* this is a local var definition which is also a cursor */
      CURSOR c_ready_subscription (
         c_i_target_id   pn_object_has_subscription.object_id%TYPE,
         c_i_event_type_id  pn_event_type.event_type_id%type,
         c_i_space_id pn_space_has_subscription.space_id%type
      )
      IS
         (SELECT n.notification_type_id,
                sb.subscription_id,
                sb.name,
                sb.description,
                sb.subscription_type_id,
                sb.subscriber_batch_id,
                sb.created_date,
                sb.created_by_id,
                sb.delivery_interval

           FROM pn_object_has_subscription ohs,
                pn_sub_has_notify_type n,
                pn_subscription sb,
                pn_event_has_notification ehn
          -- this line qualifies the selection of columns according to that column's actual value
          WHERE ohs.object_id = c_i_target_id
          AND sb.subscription_id = ohs.subscription_id
          and ehn.event_type_id = c_i_event_type_id
          and ehn.notification_type_id = n.notification_type_id
          AND ohs.subscription_id = n.subscription_id
          AND sb.record_status = 'A')
      UNION
          (SELECT n.notification_type_id,
                sb.subscription_id,
                sb.name,
                sb.description,
                sb.subscription_type_id,
                sb.subscriber_batch_id,
                sb.created_date,
                sb.created_by_id,
                sb.delivery_interval

           FROM pn_subscription sb,
                pn_sub_has_notify_type n,
                pn_object_type_subscription ots,
                pn_event_has_notification ehn,
                pn_space_has_subscription shs
          -- this line qualifies the selection of columns according to that column's actual value
          WHERE shs.space_id = c_i_space_id
          and ots.object_type = (select object_type from pn_object where object_id = c_i_target_id)
          AND sb.subscription_id = shs.subscription_id
          and sb.subscription_id = ots.subscription_id
          and ehn.event_type_id = c_i_event_type_id
          and ehn.notification_type_id = n.notification_type_id
          AND sb.subscription_id = n.subscription_id
          AND sb.record_status = 'A');

      /* this line defines two more variables */
      --        v_subscription_ready_rec   c_ready_subscription%ROWTYPE;    -- the cursor has a row type. Here we define a variable whose "type"  is the same as that rowtype
      v_scheduled_subscription_id   pn_scheduled_subscription.scheduled_subscription_id%TYPE;
   BEGIN
      -- STATE now a row exists in the clob table and the clob data has been entered into that row.
      v_datetime := SYSDATE;

      BEGIN
         FOR v_subscription_ready_rec IN c_ready_subscription (i_target_id, v_event_type_id, i_space_id)
         LOOP
            v_delivery_date :=
               compute_delivery_date (
                  v_subscription_ready_rec.delivery_interval,
                  i_event_time
               );
            v_scheduled_subscription_id :=
               create_new_object (
                  g_scheduled_subscription_type,
                  i_initiator_id,
                  g_active_record_status
               );

            -- Grab the CLOB locater for the current record
            -- This cannot be included in the UNION so is not available in the cursor
            select custom_message_clob into v_custom_message_clob
            from pn_subscription
            where subscription_id = v_subscription_ready_rec.subscription_id;

            INSERT INTO pn_scheduled_subscription
                ( scheduled_subscription_id, name, description, notification_type_id, custom_message_clob,
                  subscriber_batch_id, delivery_interval, delivery_date,
                  target_object_id, target_object_type, target_object_clob_id, initiator_id,
                  event_time, event_type, is_queued, batch_id,
                  create_date, created_by_id, modified_date, modified_by_id, record_status, crc,
                  space_id )
            VALUES
                ( v_scheduled_subscription_id, v_subscription_ready_rec.name, v_subscription_ready_rec.description, v_subscription_ready_rec.notification_type_id, v_custom_message_clob,
                  v_subscription_ready_rec.subscriber_batch_id, v_subscription_ready_rec.delivery_interval, v_delivery_date,
                  i_target_id, i_target_type, i_target_object_clob_id, i_initiator_id,
                  v_datetime, v_event_type_id, 0, -1,   -- this will always signify not-batched. This column needs SOMETHING in it so its value can be compared to another number. You can't compare a blank column with a n8umber.
                  v_datetime, i_initiator_id, v_datetime, i_initiator_id, 'A', v_datetime,
                  to_char(i_space_id) );

         END LOOP subscription_loop;

      END;

      -- Set output parameters
      o_scheduled_subscription_id := v_scheduled_subscription_id;   -- JDBC will convert this if I try to catch it with a callableStatement.getString()

   EXCEPTION
      WHEN OTHERS THEN
          base.log_error (stored_proc_name, SQLCODE, SQLERRM);
          raise;
   END;   --create_scheduled_subscription


   FUNCTION get_event_type_for_name (i_event_name VARCHAR2)
      RETURN NUMBER
   IS
      return_value       NUMBER       := -1;
      stored_proc_name   VARCHAR(100)
               := 'NOTIFICATION::get_event_type_for_name';
   /* returns each scheduled subscription and it's associated clob_id and clob_field */

   BEGIN
      SELECT et.event_type_id
        INTO return_value
        FROM pn_event_type et
       WHERE et.name = LOWER (i_event_name);
      RETURN return_value;
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;   -- end get_event_type_for_name

   /* returns a cursor composed of the all the rows from pn_scheduled_subscription with due_date < sysdate and the clob_field from pn_notification_clob*/
   FUNCTION get_ready_subscriptions
      RETURN g_reference_cursor
   IS
      v_return_cur       G_reference_cursor;   -- the cursor will contain the ready subscriptions
      v_batch_id         NUMBER             := mark_ready_subscription;   -- the guid which will mark the rows to be processed in this procedure.
      stored_proc_name   VARCHAR2(100)
               := 'NOTIFICATION.get_ready_subscriptions';   -- used during error logging
   /* returns each scheduled subscription and it's associated clob_id and clob_field */

   BEGIN
      OPEN v_return_cur FOR
         SELECT ss.*, nt.name as nt_name, nt.description as nt_description, nt.default_message as nt_default_message,
                nt.object_type as nt_object_type, nc.object_id AS clob_id,
                s.space_name, s.space_type, ss.created_by_id as sender_id
           FROM pn_scheduled_subscription ss, pn_notification_type nt, pn_notification_clob nc,
                pn_space_view s
          WHERE ss.target_object_clob_id = nc.object_id
            AND batch_id = v_batch_id
            and nt.notification_type_id = ss.notification_type_id
            and s.space_id = ss.space_id
          ORDER BY ss.event_time desc;
      RETURN v_return_cur;
   -- We are waiting here if there's another process which wants to alter this row somehow.
   -- That should never be the case. If it is, there's a potential for deadlock.
   -- Perhaps this this should have a NOWAIT / ERROR handler that returns a null cursor to the caller that the caller then checks for.
   -- without that, and if there's a  collision, some notifications will be orphans, owing to the fact that the batch_id used to select them is lost.
   -- Perhaps we should prepare for that possibility anyway.
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;   -- end mark_and_batch_notifications

   /* returns a GUID or -1 if it tanked.  */
   FUNCTION get_guid (stored_proc_name IN VARCHAR2)
      RETURN NUMBER
   IS
      guid   NUMBER := -1;   -- this signifies failure. The caller can check for this ( x >0)
   BEGIN
      SELECT pn_object_sequence.nextval
        INTO guid
        FROM dual;
      RETURN guid;
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   /* mark the given scheduled subscription with the given  batching id */
   FUNCTION mark_ready_subscription
      RETURN NUMBER
   IS

        pragma autonomous_transaction;


      stored_proc_name   VARCHAR2(100)
               := 'NOTIFICATION.mark_ready_subscription ';
      v_batch_id         NUMBER        := get_guid (stored_proc_name);
      debugCounter NUMBER;


      CURSOR batch_id_cur
      IS
         SELECT batch_id
           FROM pn_scheduled_subscription
          WHERE batch_id = -1   -- signifies that this has not been batched and is available to be delivered.
            AND delivery_date < SYSDATE
            FOR UPDATE OF batch_id NOWAIT;
   BEGIN
      DBMS_OUTPUT.put_line('in mark_ready_subscription');
      FOR batch_id_rec IN batch_id_cur
      LOOP

         UPDATE pn_scheduled_subscription
            SET batch_id = v_batch_id
            where current of batch_id_cur;

      END LOOP;

      COMMIT;
      RETURN v_batch_id;
   EXCEPTION   -- causes a variable to be set and then returns to caller.
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   FUNCTION create_new_object (
      object_type     IN   VARCHAR2,
      created_by_id   IN   VARCHAR2,
      record_status   IN   VARCHAR2
      )
      RETURN NUMBER
   IS
   BEGIN
      RETURN base.create_object (object_type, created_by_id, record_status);
   END;

   FUNCTION create_clob
      RETURN NUMBER
   IS
      stored_proc_name   VARCHAR2(100) := 'NOTIFICATION.create_new_CLOB';
      v_clob_guid        NUMBER        := get_guid (stored_proc_name);   -- get a GUID for this unique CLOB instnace we are now creating
   BEGIN
      INSERT INTO pn_notification_clob (object_id, clob_field)
           VALUES (v_clob_guid, EMPTY_CLOB ());
      RETURN v_clob_guid;
   EXCEPTION   -- causes a variable to be set and then returns to caller.
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   FUNCTION get_clob_for_read (i_object_id IN VARCHAR2)
      RETURN CLOB
   IS
      stored_proc_name   VARCHAR2(100) := 'NOTIFICATION.get_CLOB';
      return_clob        CLOB;
      v_clob_id          NUMBER(20)    := TO_NUMBER (i_object_id);
   BEGIN
      SELECT clob_field
        INTO return_clob
        FROM pn_notification_clob
       WHERE object_id = v_clob_id;
      RETURN return_clob;
   EXCEPTION   -- causes a variable to be set and then returns to caller.
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   FUNCTION get_clob_for_update (i_object_id IN VARCHAR2)
      RETURN CLOB
   IS
      stored_proc_name   VARCHAR2(100) := 'NOTIFICATION.get_CLOB';
      return_clob        CLOB;
      v_clob_id          NUMBER(20)    := TO_NUMBER (i_object_id);
   BEGIN
      SELECT clob_field
        INTO return_clob
        FROM pn_notification_clob
       WHERE object_id = v_clob_id
         FOR UPDATE;
      RETURN return_clob;
   EXCEPTION   -- causes a variable to be set and then returns to caller.
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;




   PROCEDURE erase_clob (i_clob_id IN VARCHAR2)
   IS
      stored_proc_name       VARCHAR2(100) := 'NOTIFICATION.ERASE_CLOB';
      clob_to_erase          CLOB;
      clob_to_erase_length   NUMBER        := 0;
      o_return_status        NUMBER        := 2000;   -- reset return status to represent failure until this succeeds
   BEGIN
      SELECT clob_field
        INTO clob_to_erase
        FROM pn_notification_clob
       WHERE object_id = i_clob_id
         FOR UPDATE;
      clob_to_erase_length := dbms_lob.getlength (clob_to_erase);
      --dbms_output.put_line(clob_to_erase_length);
      dbms_lob.erase (clob_to_erase, clob_to_erase_length, 1);
--    clob_to_erase_length := dbms_lob.getlength(clob_to_erase);
      --   dbms_output.put_line(clob_to_erase_length);
      dbms_lob.TRIM (clob_to_erase, 0);
      -- clob_to_erase_length := dbms_lob.getlength(clob_to_erase);
      -- dbms_output.put_line(clob_to_erase_length);
      COMMIT;
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            o_return_status := 2000;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;

         o_return_status := 0;   -- if I ever decide this needs to be returned
   END;

   PROCEDURE erase_clob (i_clob IN CLOB)
   IS
      stored_proc_name       VARCHAR2(100) := 'NOTIFICATION.ERASE_CLOB';
      clob_to_erase_length   NUMBER        := 0;
      clob_to_erase          CLOB          := i_clob;
      o_return_status        NUMBER        := 2000;   -- reset return status to represent failure until this succeeds
   BEGIN
      clob_to_erase_length := dbms_lob.getlength (clob_to_erase);
      --dbms_output.put_line(clob_to_erase_length);
      dbms_lob.erase (clob_to_erase, clob_to_erase_length, 1);
--    clob_to_erase_length := dbms_lob.getlength(clob_to_erase);
      --   dbms_output.put_line(clob_to_erase_length);
      dbms_lob.TRIM (clob_to_erase, 0);
      -- clob_to_erase_length := dbms_lob.getlength(clob_to_erase);
      -- dbms_output.put_line(clob_to_erase_length);
      COMMIT;
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            o_return_status := 2000;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;

         o_return_status := 0;   -- if I ever decide this needs to be returned
   END;




/* trims the CLOB to the length indicated by i_new_length*/
   PROCEDURE trim_clob (i_clob IN CLOB, i_new_length NUMBER)
   IS
      stored_proc_name   VARCHAR2(100) := 'NOTIFICATION.TRIM_CLOB';
      clob_to_trim       CLOB          := i_clob;
      v_new_length       NUMBER        := TO_NUMBER (i_new_length);
      o_return_status    NUMBER        := 2000;   -- reset return status to represent failure until this succeeds
   BEGIN
      dbms_lob.TRIM (clob_to_trim, v_new_length);
      COMMIT;
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            o_return_status := 2000;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;

         o_return_status := 0;   -- if I ever decide this needs to be returned
   END;

   /* begins a new row in pn_notification_queue. Returns a status number, 0 == success 2000 = failure

   PROCEDURE  begin_new_queue_record(i_ready_subscription_rec IN ready_subscription_rec, i_notification_id IN NUMBER, o_return_status OUT NUMBER)

   IS
      stored_proc_name VARCHAR2(100):= 'NOTIFICATION.begin_new_queue_record';

   BEGIN
   o_return_status := 2000;   -- reset the return status to failure until we succeed
      INSERT
        INTO pn_notification_queue
        (
         notification_id,
         posted_by_id,
         posted_date,
         record_status
         )
     VALUES
        (
         i_notification_id,
         i_ready_subscription_rec.created_by_id, -- created by
         SYSDATE, -- create date
         'A'    -- status
         );

  EXCEPTION
     WHEN OTHERS THEN
       BEGIN
          o_return_status := 2000;
          base.log_error(stored_proc_name, sqlcode, sqlerrm);
       END;

   o_return_status :=0;   -- reset the return status to failure until we succeed

   END; -- end of begin_new_pn_notification_queue

*/


/*

          RECORD_LOCKED           EXCEPTION;     -- AN EXCEPTION DEFINED TO BE ORA-00054, RECORD LOCKED EXCEPTION
          PRAGMA EXCEPTION_INIT( RECORD_LOCKED, -54);  -- THE ASSOCIATION BETWEEN MY EXCEPTION AND ORA-00054 IS MADE

 EXCEPTION
                    WHEN record_locked                           -- our user-defined alias for ORA-00051
                    THEN
                        NULL;
                    WHEN OTHERS THEN             -- if we tanked becasuse of something else, then roll back and return null
                    BEGIN
                        base.log_error(stored_proc_name, sqlcode, sqlerrm);
                        RETURN FALSE;
                    END;

*/

/* mark the given notification_queue row with the given  batching id */
   FUNCTION mark_notifications
      RETURN NUMBER
   IS
      stored_proc_name   VARCHAR2(100) := 'NOTIFICATION.mark_notifications ';
      v_batch_id         NUMBER        := 0;

      CURSOR batch_id_cur
      IS
         SELECT batch_id
           FROM pn_notification_queue
          WHERE batch_id != 1000
            FOR UPDATE OF batch_id NOWAIT;
   BEGIN
      DBMS_OUTPUT.put_line ('v_batch_id = ' || TO_CHAR (v_batch_id));
      DBMS_OUTPUT.put_line ('in Begin');
      v_batch_id := get_guid (stored_proc_name);

--     OPEN batch_id_cur;
      FOR batch_id_rec IN batch_id_cur
      LOOP
         UPDATE pn_notification_queue
            SET batch_id = v_batch_id
          WHERE CURRENT OF batch_id_cur;
      END LOOP;

--     CLOSE batch_id_cur;
      COMMIT;
      RETURN v_batch_id;
   EXCEPTION   -- causes a variable to be set and then returns to caller.
      WHEN OTHERS
      THEN
         BEGIN
            ROLLBACK;
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
            CLOSE batch_id_cur;
         END;
   END;

   /* takes a batch ID and returns a cursor which is composed of a subset of columns in pn_notification, pn_notification_queue and pn_notiifcation_clob*/
   FUNCTION get_notifications
      RETURN g_reference_cursor
   IS
      return_cur         g_reference_cursor;   --define the cursor, open it then return it.
      stored_proc_name   VARCHAR2(100)
               := 'NOTIFICATION.get_notifications';
      v_batch_id         NUMBER             := mark_notifications;
   BEGIN
      OPEN return_cur FOR
         SELECT n.delivery_type_id,
		 		n.notification_id,
                n.delivery_address,
                n.customization_user_id, -- the id of the user whose customization(locale + config language) info would be used to render the notification.
                n.sender_id, -- the id of the user who created or sent the notification.
                nc.object_id as clob_id   -- needs to be selected from the clob table. now the name of this row willl change
           FROM pn_notification n,
                pn_notification_queue nq,
                pn_notification_clob nc   -- clob now this will also reference the clob table
          WHERE n.notification_id = nq.notification_id   -- make a row of these columns if the ids match
            and nq.is_immediate <> 1                     -- exclude immediate notifications
--         AND nq.batch_id = v_batch_id   -- and they are in the batch I am now processing

            AND n.notification_clob_id = nc.object_id
          ORDER BY posted_date asc;
      RETURN return_cur;   -- return reference cursor
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   FUNCTION get_notification_types (i_object_type VARCHAR2)
      RETURN g_reference_cursor
   IS
      return_cur         g_reference_cursor;   --define the cursor, open it then return it.
      stored_proc_name   VARCHAR2(100)
               := 'NOTIFICATION.get_notification_types';

   BEGIN

    IF i_object_type = 'form' THEN

        OPEN return_cur FOR
            SELECT n.notification_type_id,
                n.description
            FROM pn_event_type e,
                pn_notification_type n,
                pn_event_has_notification ehn
            WHERE e.object_type in ( 'form' , 'form_data' )
            AND e.event_type_id = ehn.event_type_id
            AND n.notification_type_id = ehn.notification_type_id;
    ELSE
            OPEN return_cur FOR
            SELECT n.notification_type_id,
                n.description
            FROM pn_event_type e,
                pn_notification_type n,
                pn_event_has_notification ehn
            WHERE e.object_type = i_object_type
            AND e.event_type_id = ehn.event_type_id
            AND n.notification_type_id = ehn.notification_type_id;
    END IF;

      RETURN return_cur;   -- return reference cursor
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   /* takes a event_type_id and returns a cursor which is composed of a subset of columns in notification_type*/
   FUNCTION get_notification_types_file (i_object_type VARCHAR2)
      RETURN g_reference_cursor
   IS
      return_cur         g_reference_cursor;   --define the cursor, open it then return it.
      stored_proc_name   VARCHAR2(100)
               := 'NOTIFICATION.get_notification_types';
   BEGIN
      OPEN return_cur FOR
         SELECT n.notification_type_id,
                n.description
           FROM pn_event_type e,
                pn_notification_type n,
                pn_event_has_notification ehn
          WHERE e.object_type = i_object_type
            AND e.event_type_id = ehn.event_type_id
            AND n.notification_type_id = ehn.notification_type_id
            AND n.name != 'create'
            AND n.name != 'create_container'
            AND n.name != 'remove_container';
      RETURN return_cur;   -- return reference cursor
   EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            base.log_error (stored_proc_name, SQLCODE, SQLERRM);
         END;
   END;

   /* DATE FUNCTIONS */

/* returns a Date with the value of 8 a.m. today. *//* NO ERROR CHECKING !!!*/
   FUNCTION get_8_am_on_day (i_date IN DATE)
      RETURN DATE
   IS
   BEGIN
      RETURN TRUNC (i_date) + 1 / 3;
   END;

/* computes the delivery date given the delivery_interval of a subscription *//* NO ERROR CHECKING !!!*/
   FUNCTION compute_delivery_date (
      i_delivery_interval   IN   NUMBER,
      i_event_time               DATE
      )
      RETURN DATE
   IS
      time_of_event     DATE    := i_event_time;
      current_date      DATE    := SYSDATE;   --
      return_date       DATE;
      before_deadline   BOOLEAN;
   BEGIN
      IF i_delivery_interval = 100   -- if delivery is to happen immediately
      THEN
         return_date := current_date;   -- then the date to return to the caller is the current date and we're done
      ELSE   -- otherwise it's not immediate and we need to get the delivery date
         BEGIN
            IF time_of_event < get_8_am_on_day (current_date)   -- if time_of_event is before 8 am today.
            THEN
               before_deadline := TRUE;   -- then set this flag to true
            ELSE
               before_deadline := FALSE;   -- otherwise to false
            END IF;   -- either way, go get the date

            return_date :=
               get_delayed_delivery_date (
                  i_delivery_interval,
                  time_of_event,
                  current_date,
                  before_deadline
               );
         END;
      END IF;

      RETURN return_date;
   END;

   /* processes an INotificationEvent which has occured after the deadline which demarcates between an even happening today adn an event which is considerd to have happened tomorrow*/
   /* LIMITED ERROR CHECKING !!!*/
   FUNCTION get_delayed_delivery_date (
      i_delivery_interval   IN   NUMBER,
      time_of_event         IN   DATE,
      current_date          IN   DATE,
      before_deadline       IN   BOOLEAN
      )
      RETURN DATE
   IS
      return_date                 DATE;
      invalid_delivery_interval   EXCEPTION;
      delivery_day                VARCHAR2(10) := 'monday';
   BEGIN   -- simply fork processing according to received i_delivery_interval.
      --If no match is found, then raise an excpetion
      IF i_delivery_interval = 200   -- daily notification
      THEN
         return_date :=
            get_daily_delivery_date (
               time_of_event,
               current_date,
               before_deadline
            );
      ELSIF i_delivery_interval = 300   -- weekly notification
      THEN
         return_date :=
            get_weekly_delivery_date (
               time_of_event,
               current_date,
               delivery_day,
               before_deadline
            );
      ELSIF i_delivery_interval = 400   -- then it must be monthly ort we can check for an error here ....
      THEN
         return_date :=
            get_monthly_delivery_date (
               time_of_event,
               current_date,
               delivery_day,
               before_deadline
            );
      ELSE   -- define but don't raise the error, simply record it
         INSERT INTO pn_sp_error_log
              VALUES (
                 SYSDATE,
                 'create_scheduled_subscription::get_delayed_delivery_date',
                 100,
                 TO_CHAR (i_delivery_interval)
              );
      END IF;

      RETURN return_date;
   END;

   /* returns the delivery_date for a daily notification*//* NO ERROR CHECKING !!!*/
   FUNCTION get_daily_delivery_date (
      time_of_event     DATE,
      current_date      DATE,
      before_deadline   BOOLEAN
      )
      RETURN DATE
   IS
      return_date   DATE;
   BEGIN
      IF before_deadline   -- if you're in under the deadline
      THEN
         return_date := current_date;   -- then deliver it today
      ELSE   -- otherwise deliver it tomorrow at 8am
         return_date := get_8_am_on_day (current_date + 1);
      END IF;

      RETURN return_date;
   END;

   /* check to see if date passed in is the day of week passed in as a String *//* NO ERROR CHECKING !!!*/
   FUNCTION is_day (today IN DATE, day_to_check IN VARCHAR2)
      RETURN BOOLEAN
   IS
   BEGIN
      RETURN NEXT_DAY (TRUNC (today) - 1, day_to_check) = TRUNC (today);
   END;

   /* check to see if the date passed in is the first of the month in which it falls.*//* NO ERROR CHECKING !!!*/
   FUNCTION is_first_of_month (today IN DATE, day_to_check IN VARCHAR2)
      RETURN BOOLEAN
   IS
   BEGIN
      RETURN TRUNC (today, 'month') = TRUNC (today);
   END;

   /* returns the delivery date for a weekly notification */
   FUNCTION get_weekly_delivery_date (
      time_of_event      DATE,
      current_date       DATE,
      delivery_weekday   VARCHAR2,
      before_deadline    BOOLEAN
      )
      RETURN DATE
   IS
      return_date   DATE;
   BEGIN
      IF before_deadline   -- if you're in under the deadline
      THEN
         BEGIN
            IF is_day (delivery_weekday, 'monday')   -- and today is the day weekly deliveries are made
            THEN
               return_date := current_date;   -- then deliver it today
            ELSE   -- otherwise deliver it at 8 am the next delivery day
               return_date :=
                  get_8_am_on_day (NEXT_DAY (current_date, delivery_weekday));
            END IF;
         END;
      ELSE   -- otherwise deliver it at 8 am the next delivery day
         return_date :=
            get_8_am_on_day (NEXT_DAY (current_date, delivery_weekday));
      END IF;

      RETURN return_date;
   END;

   /* returns the delivery date for a monthly notification. *//* NO ERROR CHECKING !!!*/
   FUNCTION get_monthly_delivery_date (
      time_of_event      DATE,
      current_date       DATE,
      delivery_weekday   VARCHAR2,
      before_deadline    BOOLEAN
      )
      RETURN DATE
   IS
      return_date   DATE;
   BEGIN
      IF before_deadline   -- if you're in under the dealine
      THEN
         IF is_first_of_month (current_date, delivery_weekday)   -- and today is the first of the month
         THEN
            return_date := current_date;   -- then deliver it today
         ELSE
            return_date :=
               get_8_am_on_day (NEXT_DAY (current_date, delivery_weekday));   -- otherwise deliver it the next delivery day at 8am
         END IF;
      ELSE
         return_date :=
            get_8_am_on_day (NEXT_DAY (current_date, delivery_weekday));   -- otherwise deliver it the next delivery day at 8am
      END IF;

      RETURN return_date;
   END;


   FUNCTION create_notifiable_event (
        i_event_name    IN  varchar2,
        i_event_label   IN  varchar2,
        i_event_description IN  varchar2,
        i_object_type   IN  varchar2
        ) RETURN NUMBER

        IS
            v_error_code    NUMBER := -1;
            v_event_id     NUMBER;
            v_notify_type_id    NUMBER;
            v_timestamp     DATE := SYSDATE;
            v_admin_id      NUMBER := 1000;

         BEGIN

            select pn_object_sequence.nextval into v_event_id from dual;
            select pn_object_sequence.nextval into v_notify_type_id from dual;

            insert into pn_event_type
            (
                event_type_id,
                name,
                description,
                object_type,
                record_status,
                crc
            ) values (
                v_event_id,
                i_event_name,
                i_event_description,
                i_object_type,
                'A',
                v_timestamp
            );

            insert into pn_notification_type
            (
                notification_type_id,
                name,
                description,
                default_message,
                object_type,
                create_date,
                created_by_id,
                modified_date,
                modified_by_id,
                record_status,
                crc
             ) values (
                v_notify_type_id,
                i_event_label,
                i_event_description,
                i_event_description,
                i_object_type,
                v_timestamp,
                v_admin_id,
                v_timestamp,
                v_admin_id,
                'A',
                v_timestamp
          );

            insert into pn_event_has_notification
            (
                notification_type_id,
                event_type_id
            ) values (
                v_notify_type_id,
                v_event_id
            );

            v_error_code := BASE.OPERATION_SUCCESSFUL;
            return v_error_code;

    EXCEPTION
      WHEN OTHERS
      THEN
         BEGIN
            v_error_code := BASE.PLSQL_EXCEPTION;
            return v_error_code;
         END;

     END; -- create_notifiable_event


END;   -- Package Body NOTIFICATION
/

CREATE OR REPLACE PACKAGE notification IS
--==================================================================
-- Purpose: PNET Notification procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Chad        9/20/00  Created it.
--==================================================================

   -- Package constants
   G_subscription_object_type                   pn_object.object_type%type := 'subscription'; -- TABLENAME.COLUMNNAME%KWTYPE  G means global := means default value
   G_scheduled_subscription_type                pn_object.object_type%type :='scheduled_subscription';
   G_notification_object_type                   pn_object.object_type%type :='notification';

   DELIVERY_STATUS_UNDELIVERED number := 100;

   DELIVERY_TYPE_EMAIL number := 100;


   G_active_record_status                       pn_subscription.record_status%type := 'A';  -- := MEANS DEFAULT ASSIGNMENT
   TYPE G_reference_cursor                      IS REF CURSOR;
   TYPE G_scheduled_subscription_cur             IS REF CURSOR RETURN pn_scheduled_subscription%ROWTYPE;


PROCEDURE  create_type_subscription
     (i_subscription_name          IN       VARCHAR2,   -- the user created name of the subscription
      i_subscription_description   IN       VARCHAR2,   -- the user created description of the subscription
      i_subscription_type_id       IN       VARCHAR2,   -- the system created type of subscription
      i_target_type                IN       VARCHAR2,   -- the system defined type of object that this subscription was created for, if it was created around an object typee, and not a particular object. Null otherwise.
      i_delivery_interval          IN       VARCHAR2,   -- the interval (weekly daily immediately etc...) that the subscriber has chosen to recieve notifications on
      i_delivery_address_group     IN       VARCHAR2,   -- the address group id of the address list
      i_created_by_id              IN       VARCHAR2,   -- the entity which created this subscription, possibly the system itself.
      i_space_id                   IN       VARCHAR2,
      i_is_custom_message_null     IN       NUMBER,
      o_custom_message_clob        OUT      CLOB,
      o_subscription_id            OUT      NUMBER
  );

  PROCEDURE  create_object_subscription
 (
      i_subscription_name          IN       VARCHAR2,   -- the user created name of the subscription
      i_subscription_description   IN       VARCHAR2,   -- the user created description of the subscription
      i_subscription_type_id       IN       VARCHAR2,   -- the system created type of subscription
      i_target_id                  IN       VARCHAR2,   -- the system defined type of object that this subscription was created for, if it was created around an object typee, and not a particular object. Null otherwise.
      i_delivery_interval          IN       VARCHAR2,   -- the interval (weekly daily immediately etc...) that the subscriber has chosen to recieve notifications on
      i_delivery_address_group     IN       VARCHAR2,   -- the user entered delivery address (URI)
      i_created_by_id              IN       VARCHAR2,   -- the entity which created this subscription, possibly the system itself.
      i_space_id                   IN       VARCHAR2,
      i_is_custom_message_null     IN       NUMBER,
      o_custom_message_clob        OUT      CLOB,
      o_subscription_id            OUT      NUMBER
  );



----------------------------------------------------------------------
----------------------------------------------------------------------
-- create_subscription  these are the variables coming into or going out of this method.
-- values are returned via out parameters.. these values are passed in then altered by reference.
----------------------------------------------------------------------
    PROCEDURE create_scheduled_subscription   -- who calls me and what did they know?
      (
        i_event_type IN varchar2,
        i_target_id    IN number,
        i_target_type IN varchar2,
        i_event_time    IN Date,
        i_description    IN varchar2,
        i_target_object_clob_id     IN number,
        i_initiator_id  IN number,
        i_space_id      IN number,
        o_scheduled_subscription_id OUT number
        );


    PROCEDURE create_notification
      (
        i_delivery_address  IN  varchar2,
        i_notification_clob_id IN number,
        i_delivery_type_id IN number,
        i_delivery_from_address IN varchar2,
        i_customization_user_id IN varchar2,
        i_sender_id IN varchar2,
        i_is_immediate in number,
        o_notification_id OUT number,
        o_return_value OUT number
      );



      --PROCEDURE begin_new_notification_record(i_ready_subscription_rec IN ready_subscription_rec, o_notification_id OUT NUMBER, o_return_status OUT NUMBER);

      --PROCEDURE  begin_new_queue_record(i_ready_subscription_rec IN ready_subscription_rec, i_notification_id IN NUMBER, o_return_status OUT NUMBER);
----------------------------------------------------------------------
-- create notifications by selecting subscriptions which have come due,
-- compressing their XML and entering rows into both pn_notification_queue
-- and pn_notification.
----------------------------------------------------------------------
FUNCTION create_new_object(object_type IN VARCHAR2, created_by_id IN VARCHAR2, record_status IN VARCHAR2)
RETURN NUMBER;

/* returns a cursor to the rows from pn_scheduled_subscription with due_date < sysdate. Marks those rows with a batch_id */
FUNCTION get_ready_subscriptions
RETURN G_reference_cursor;

/*utitlity function used to brand a certain group of ready_subscriptions so they can be processed as a unit */
FUNCTION mark_ready_subscription
RETURN NUMBER;

 /* Returns a Cursor to a certain number of Notifications  in the notification_queue*/
FUNCTION get_notifications
RETURN G_reference_cursor;

 /* marks rows in pn_notification_queue as "in progress" and assigns them a batch_id number for selection */
FUNCTION mark_notifications
RETURN NUMBER;

FUNCTION get_notification_types (i_object_type VARCHAR2)
RETURN G_reference_cursor;

FUNCTION get_notification_types_file(i_object_type VARCHAR2)
RETURN G_reference_cursor;

/* Returns a GUID without creating an object in the system */
FUNCTION get_GUID(stored_proc_name  IN VARCHAR2 )
RETURN NUMBER;


/* Computes the value for the column delivery_date which is used to indicate when a Subscriptiion is ready to be delviered*/
FUNCTION compute_delivery_date( i_delivery_interval IN NUMBER, i_event_time DATE)    -- THIS HAS TO MATCH THE PARAMETER NAME ALSO!!!
RETURN DATE;

/*utility function which process an INotificationEvent which has occured after the deadline which demarcates between an event happening today and an event which is considerd to have happened tomorrow*/
FUNCTION  get_delayed_delivery_date(i_delivery_interval IN NUMBER, time_of_event IN DATE, current_date IN DATE, before_deadline BOOLEAN)
RETURN DATE;

 /* returns the delviery_date for a daily delivery*/
FUNCTION get_daily_delivery_date(time_of_event IN DATE, current_date IN DATE, before_deadline IN BOOLEAN)
RETURN DATE;

/* returns the delivery date for a weekly notification*/
FUNCTION  get_weekly_delivery_date(time_of_event IN DATE, current_date IN DATE, delivery_weekday IN VARCHAR2 ,before_deadline IN BOOLEAN)
RETURN DATE;

/* returns the delivery date for a monthly notification */
FUNCTION  get_monthly_delivery_date(time_of_event IN DATE, current_date IN DATE, delivery_weekday IN VARCHAR2 ,before_deadline IN BOOLEAN)
RETURN DATE;

/* returns the event_type as a number given the name of the event*/
FUNCTION get_event_type_for_name (i_event_name VARCHAR2)
RETURN NUMBER;

FUNCTION is_first_of_month( today IN DATE , day_to_check IN VARCHAR2)
RETURN BOOLEAN;
 /* returns a Date with the value of 8 a.m. today. */

FUNCTION get_8_am_on_day(i_date IN DATE)
RETURN DATE;

   /* check to see if date passed in is the day of week passed in as a String */

FUNCTION is_day( today IN DATE , day_to_check IN VARCHAR2)
RETURN BOOLEAN;

/* create a new CLOB inthe database in the table pn_notification_clob */
FUNCTION create_CLOB
RETURN NUMBER;

/* erase the clob_field value of a clob without deleting the clob itself.*/
PROCEDURE erase_clob(i_clob_id IN VARCHAR2);

/* erase the clob_field value of a clob without deleting the clob itself.*/
PROCEDURE erase_CLOB(i_clob IN CLOB);

/* trims the CLOB to the length indicated by i_new_length*/
PROCEDURE trim_CLOB(i_clob IN CLOB, i_new_length NUMBER);

/* retrieve from the table pn_notification_clob the CLOB associated with a particular object_id */
FUNCTION get_CLOB_for_read(i_object_id IN VARCHAR2)
RETURN CLOB;

/* retrieve from the table pn_notification_clob the CLOB associated with a particular object_id */
FUNCTION get_CLOB_for_update(i_object_id IN VARCHAR2)
RETURN CLOB;

function create_notifiable_event (
    i_event_name    IN  varchar2,
    i_event_label   IN  varchar2,
    i_event_description IN  varchar2,
    i_object_type   IN  varchar2) return NUMBER;

END; -- Package Specification NOTIFICATION
/

CREATE OR REPLACE PACKAGE BODY process IS

---------------------------------------------------------------------
-- STORE_PROCESS
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation from Brian's procs.
-- Robin       11-May-00   Added error handling.

Procedure STORE_PROCESS
(
    p_person_id IN NUMBER,
    p_space_id IN NUMBER,
    p_process_id IN NUMBER,
    p_process_name IN VARCHAR2,
    p_process_desc IN VARCHAR2,
    p_current_phase_id IN NUMBER,
    p_last_gate_passed_id IN NUMBER,
    p_record_status IN VARCHAR2,
    o_process_id OUT NUMBER,
    o_status OUT NUMBER
)
IS
    v_process_id NUMBER(20);
    stored_proc_name VARCHAR2(100) := 'STORE_PROCESS';

BEGIN
    -- NEW PROCESS, INSERT
    IF ((p_process_id IS NULL) OR (p_process_id = ''))
    THEN

        -- Create the object
        v_process_id := BASE.CREATE_OBJECT('process', p_person_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_process_id, 'process', p_space_id, p_person_id);

        INSERT INTO
            pn_process
        (
            process_id,
            process_name,
            process_desc,
            current_phase_id,
            last_gate_passed_id,
            record_status
        )
        VALUES
        (
            v_process_id,
            p_process_name,
            p_process_desc,
            p_current_phase_id,
            p_last_gate_passed_id,
            p_record_status
        );

        -- Create new pn_project_has_process record

        INSERT INTO
            pn_space_has_process
        (
            space_id,
            process_id
        )
        VALUES
        (
            p_space_id,
            v_process_id
        );

        o_process_id := v_process_id;

    -- EXISTING PROCESS, UPDATE
    ELSE

        UPDATE
                pn_process
        SET
                process_id = p_process_id,
                process_name = p_process_name,
                process_desc = p_process_desc,
                current_phase_id = p_current_phase_id,
                last_gate_passed_id = p_last_gate_passed_id,
                record_status = p_record_status
        WHERE
                process_id = p_process_id;

        o_process_id := p_process_id;

        IF SQL%NOTFOUND THEN
            o_status := no_data;
            ROLLBACK;
            RETURN;
        END IF;

    END IF;

    o_status := success;
    COMMIT;

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


---------------------------------------------------------------------
-- COPY_PROCESS
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        18-Sep-00   Created

Procedure COPY_PROCESS
(
    i_from_space_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_return_value OUT NUMBER
)
IS
    -- variable declaration

    v_old_process_id            pn_process.process_id%type;
    v_new_process_id            pn_process.process_id%type;
    v_phase_id                  pn_phase.phase_id%type;
    v_process_rec               pn_process%rowtype;
    stored_proc_name VARCHAR2(100) := 'STORE_PROCESS';

    v_cmd_status                NUMBER := 0;

    -- cursor definition

    CURSOR c_processes (i_space_id pn_object.object_id%type) IS
        select process_id from pn_space_has_process
        where space_id = i_space_id;


    CURSOR c_phases (i_process_id pn_process.process_id%type) IS
        select phase_id from pn_phase where process_id = i_process_id
        and record_status = 'A';

BEGIN


        /* -------------------------------  COPY processes  ------------------------------- */

        -- FOR EACH OF THE PROCESSES IN THIS SPACE
        OPEN c_processes (i_from_space_id);
        <<process_loop>>
    	LOOP

    		FETCH c_processes INTO v_old_process_id;
    		EXIT WHEN ((c_processes%NOTFOUND));

            -- Create the object
            v_new_process_id := BASE.CREATE_OBJECT('process', i_actor_id, 'A');
            -- Apply default security permissions
            SECURITY.CREATE_SECURITY_PERMISSIONS(v_new_process_id, 'process', i_to_space_id, i_actor_id);

            -- get the data from the existing process
            select * into v_process_rec from pn_process where process_id = v_old_process_id;

            INSERT INTO pn_process
            (
                process_id,
                process_name,
                process_desc,
                current_phase_id,
                last_gate_passed_id,
                record_status
            )
            VALUES
            (
                v_new_process_id,
                v_process_rec.process_name,
                v_process_rec.process_desc,
                v_process_rec.current_phase_id,
                v_process_rec.last_gate_passed_id,
                'A'
            );

            -- Create new pn_project_has_process record

            INSERT INTO
                pn_space_has_process
            (
                space_id,
                process_id
            )
            VALUES
            (
                i_to_space_id,
                v_new_process_id
            );


            /* -------------------------------  COPY phases  ------------------------------- */

            -- FOR EACH OF THE PHASES IN THIS PROCESS
            OPEN c_phases (v_old_process_id);
            <<phase_loop>>
        	LOOP

        		FETCH c_phases INTO v_phase_id;
        		EXIT WHEN ((c_phases%NOTFOUND) OR (v_cmd_status <> 0));

                    copy_phase (v_phase_id, v_new_process_id, i_to_space_id, i_actor_id, v_cmd_status);

            END LOOP phase_loop;
    	    CLOSE c_phases;


    -- NOW END PROCESS LOOP
    END LOOP process_loop;
	CLOSE c_processes;

    o_return_value := v_cmd_status;
    COMMIT;



EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        o_return_value := no_data;

    WHEN e_null_constraint THEN
        ROLLBACK;
        o_return_value := null_field;

    WHEN e_check_constraint THEN
        ROLLBACK;
        o_return_value := check_violated;

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_return_value := dupe_key;

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_return_value := no_parent_key;

    WHEN OTHERS THEN
        o_return_value := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;


END;


---------------------------------------------------------------------
-- REMOVE_PROCESS
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Brian       30-Apr-00   Created for Process Module
-- Robin       11-May-00   Added error handling.

Procedure REMOVE_PROCESS
(
    p_process_id IN NUMBER,
    o_status OUT NUMBER
)

IS

    stored_proc_name VARCHAR2(100) := 'REMOVE_PROCESS';

BEGIN

    -- lock tables temporarily
    LOCK TABLE pn_phase IN SHARE MODE;
    LOCK TABLE pn_gate IN SHARE MODE;
    LOCK TABLE pn_phase_has_deliverable IN SHARE MODE;

    UPDATE pn_process SET record_status = 'D'
        WHERE process_id = p_process_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id = p_process_id;

    UPDATE pn_phase SET record_status = 'D'
        WHERE process_id = p_process_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id =
            ( SELECT phase_id
                FROM pn_phase
                WHERE process_id = p_process_id );

    UPDATE pn_gate SET record_status = 'D'
        WHERE phase_id =
            ( SELECT phase_id
                FROM pn_phase
                WHERE process_id = p_process_id );

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id =
            ( SELECT gate_id
                FROM pn_gate
                WHERE phase_id =
                    ( SELECT phase_id
                        FROM pn_phase
                        WHERE process_id = p_process_id ));

    UPDATE pn_deliverable
        SET record_status = 'D'
        WHERE deliverable_id =
            ( SELECT deliverable_id
                FROM pn_phase_has_deliverable
                WHERE phase_id =
                    ( SELECT phase_id
                        FROM pn_phase
                        WHERE process_id = p_process_id ));

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id =
            ( SELECT deliverable_id
                FROM pn_phase_has_deliverable
                WHERE phase_id =
                    ( SELECT phase_id
                        FROM pn_phase
                        WHERE process_id = p_process_id ));



    o_status := success;
    COMMIT;

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

---------------------------------------------------------------------
-- STORE_PHASE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation from Brian's procs.
-- Robin       11-May-00   Added error handling.

Procedure STORE_PHASE
(
    p_person_id IN NUMBER,
    p_phase_id  IN NUMBER,
    p_process_id IN NUMBER,
    p_space_id IN NUMBER,
    p_phase_name IN VARCHAR2,
    p_phase_desc IN VARCHAR2,
    p_sequence IN NUMBER,
    p_status_id IN NUMBER,
    p_record_status IN VARCHAR2,
    p_progress_reporting_method IN VARCHAR2,
    p_start_date IN DATE,
    p_end_date IN DATE,
    p_entered_percent_complete IN NUMBER,
    o_phase_id OUT NUMBER,
    o_status OUT NUMBER
)

IS
    v_phase_id NUMBER(20);
    stored_proc_name VARCHAR2(100) := 'STORE_PHASE';

BEGIN
    -- NEW PHASE, INSERT
    IF ((p_phase_id IS NULL) OR (p_phase_id = ''))
    THEN

        -- Create the object
        v_phase_id := BASE.CREATE_OBJECT('phase', p_person_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_phase_id, 'phase', p_space_id, p_person_id);

        INSERT INTO
            pn_phase
        (
            phase_id,
            process_id,
            phase_name,
            phase_desc,
            start_date,
            end_date,
            sequence,
            status_id,
            entered_percent_complete,
            progress_reporting_method,
            record_status
        )
        VALUES
        (

            v_phase_id,
            p_process_id,
            p_phase_name,
            p_phase_desc,
            p_start_date,
            p_end_date,
            p_sequence,
            p_status_id,
            p_entered_percent_complete,
            p_progress_reporting_method,
            p_record_status
        );

        o_phase_id := v_phase_id;

    -- EXISTING PHASE, UPDATE
    ELSE

        UPDATE
                pn_phase
        SET
                phase_id = p_phase_id,
                process_id = p_process_id,
                phase_name = p_phase_name,
                phase_desc = p_phase_desc,
                start_date = p_start_date,
                end_date = p_end_date,
                sequence = p_sequence,
                status_id = p_status_id,
                entered_percent_complete = p_entered_percent_complete,
                progress_reporting_method = p_progress_reporting_method,
                record_status = p_record_status
        WHERE
                phase_id = p_phase_id;

        o_phase_id := p_phase_id;


        IF SQL%NOTFOUND THEN
            o_status := no_data;
            ROLLBACK;
            RETURN;
        END IF;

    END IF;

    o_status := success;
    COMMIT;

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


---------------------------------------------------------------------
-- COPY_PHASE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- PHIL        18-Sep-00   Created

Procedure COPY_PHASE
(
    i_phase_id  IN NUMBER,
    i_to_process_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_return_value OUT NUMBER
 )

IS

    -- variable declaration
    v_new_phase_id NUMBER(20);
    v_phase_rec     pn_phase%rowtype;
    v_gate_id       pn_gate.gate_id%type;
    v_deliverable_id pn_deliverable.deliverable_id%type;

    stored_proc_name VARCHAR2(100) := 'COPY_PHASE';
    v_cmd_status        NUMBER := 0;

    -- cursor definition

    CURSOR c_gates (i_phase_id pn_phase.phase_id%type) IS
        select gate_id from pn_gate
        where phase_id = i_phase_id and record_status = 'A';

    CURSOR c_deliverables (i_phase_id pn_phase.phase_id%type) IS
        select
          pd.deliverable_id
        from
          pn_phase_has_deliverable phd,
          pn_deliverable pd
        where
          pd.deliverable_id = phd.deliverable_id
          and phase_id = i_phase_id
          and pd.record_status = 'A';


BEGIN

        /* -------------------------------  COPY phase  ------------------------------- */


        -- Create the object
        v_new_phase_id := BASE.CREATE_OBJECT('phase', i_actor_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_new_phase_id, 'phase', i_to_space_id, i_actor_id);

        select * into v_phase_rec from pn_phase where phase_id = i_phase_id;

        INSERT INTO
            pn_phase
        (
            phase_id,
            process_id,
            phase_name,
            phase_desc,
            start_date,
            end_date,
            sequence,
            status_id,
            entered_percent_complete,
            record_status
        )
        VALUES
        (

            v_new_phase_id,
            i_to_process_id,
            v_phase_rec.phase_name,
            v_phase_rec.phase_desc,
            NULL,  -- no start and end dates
            NULL,
            v_phase_rec.sequence,
            10, -- NOT STARTED
            0, -- nothing done, new project
            'A'
        );


        /* -------------------------------  COPY gates  ------------------------------- */

        -- FOR EACH OF THE PHASES IN THIS PROCESS
        OPEN c_gates (i_phase_id);
        <<gate_loop>>
    	LOOP

    		FETCH c_gates INTO v_gate_id;
    		EXIT WHEN ((c_gates%NOTFOUND) OR (v_cmd_status <> 0));

                copy_gate (v_gate_id, v_new_phase_id, i_to_space_id, i_actor_id, v_cmd_status);

        END LOOP gate_loop;
	    CLOSE c_gates;


        /* -------------------------------  COPY deliverables  ------------------------------- */

        -- FOR EACH OF THE PHASES IN THIS PROCESS
        OPEN c_deliverables (i_phase_id);
        <<deliverable_loop>>
    	LOOP

    		FETCH c_deliverables INTO v_deliverable_id;
    		EXIT WHEN ((c_deliverables%NOTFOUND) OR (v_cmd_status <> 0));

                copy_deliverable (v_deliverable_id, v_new_phase_id, i_to_space_id, i_actor_id, v_cmd_status);

        END LOOP deliverable_loop;
	    CLOSE c_deliverables;


    o_return_value := success;

 EXCEPTION
    WHEN NO_DATA_FOUND THEN
        o_return_value := no_data;

    WHEN e_null_constraint THEN
        o_return_value := null_field;

    WHEN e_check_constraint THEN
        o_return_value := check_violated;

    WHEN e_unique_constraint THEN
        o_return_value := dupe_key;

    WHEN e_no_parent_key THEN
        o_return_value := no_parent_key;

    WHEN OTHERS THEN
        o_return_value := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
END;








---------------------------------------------------------------------
-- REMOVE_PHASE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Brian       30-Apr-00   Created for Process Module
-- Robin       11-May-00   Added error handling.
-- Matt        28-Feb-05   Removed transaction handling and error handling
--                         from procedure -- we always do this in Java code now

Procedure REMOVE_PHASE
(
    p_phase_id IN NUMBER
)
IS
BEGIN
    UPDATE pn_phase SET record_status = 'D'
        WHERE phase_id = p_phase_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id = p_phase_id;

    UPDATE pn_gate SET record_status = 'D'
        WHERE phase_id = p_phase_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id in
            ( SELECT gate_id
                FROM pn_gate
                WHERE phase_id = p_phase_id );

    UPDATE pn_deliverable SET record_status = 'D'
        WHERE deliverable_id in
            ( SELECT deliverable_id
                FROM pn_phase_has_deliverable
                WHERE phase_id = p_phase_id );

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id in
            ( SELECT deliverable_id
                FROM pn_phase_has_deliverable
                WHERE phase_id = p_phase_id );
END;

---------------------------------------------------------------------
-- STORE_GATE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation from Brian's procs.
-- Robin       11-May-00   Added error handling.


Procedure STORE_GATE
(
    p_person_id IN NUMBER,
    p_gate_id IN NUMBER,
    p_phase_id  IN NUMBER,
    p_space_id IN NUMBER,
    p_gate_name IN VARCHAR2,
    p_gate_desc IN VARCHAR2,
    p_gate_date IN DATE,
    p_status_id IN NUMBER,
    p_record_status IN VARCHAR2,
    o_gate_id OUT NUMBER,
    o_status OUT NUMBER
)

IS
    v_gate_id NUMBER(20);
    stored_proc_name VARCHAR2(60):='STORE_GATE';

BEGIN
    -- NEW GATE, INSERT
    IF ((p_gate_id IS NULL) OR (p_gate_id = '')) THEN

        -- Create the object
        v_gate_id := BASE.CREATE_OBJECT('gate', p_person_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_gate_id, 'gate', p_space_id, p_person_id);

        INSERT INTO
            pn_gate
        (
            gate_id,
            phase_id,
            gate_name,
            gate_desc,
            gate_date,
            status_id,
            record_status
        )
        VALUES
        (
            v_gate_id,
            p_phase_id,
            p_gate_name,
            p_gate_desc,
            p_gate_date,
            p_status_id,
            p_record_status
        );

        o_gate_id := v_gate_id;


        -- EXISTING GATE, UPDATE
    ELSE


        UPDATE
            pn_gate
        SET
            gate_id = p_gate_id,
            phase_id = p_phase_id,
            gate_name = p_gate_name,
            gate_desc = p_gate_desc,
            gate_date = p_gate_date,
            status_id = p_status_id,
            record_status = p_record_status
        WHERE
            gate_id = p_gate_id;

        IF SQL%NOTFOUND THEN
            o_status := no_data;
            ROLLBACK;
            RETURN;
        END IF;

    END IF;

    o_status := success;
    COMMIT;

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


---------------------------------------------------------------------
-- COPY_GATE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        18-Sep-00   Created

Procedure COPY_GATE
(
    i_gate_id IN NUMBER,
    i_to_phase_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_status OUT NUMBER
)

IS
    -- variable declaration
    v_new_gate_id NUMBER(20);
    v_gate_rec      pn_gate%rowtype;

    stored_proc_name VARCHAR2(60):='STORE_GATE';

BEGIN
        -- Create the object
        v_new_gate_id := BASE.CREATE_OBJECT('gate', i_actor_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_new_gate_id, 'gate', i_to_space_id, i_actor_id);

        -- get old gate information
        select * into v_gate_rec from pn_gate where gate_id = i_gate_id;

        INSERT INTO pn_gate
        (
            gate_id,
            phase_id,
            gate_name,
            gate_desc,
            gate_date,
            status_id,
            record_status
        )
        VALUES
        (
            v_new_gate_id,
            i_to_phase_id,
            v_gate_rec.gate_name,
            v_gate_rec.gate_desc,
            NULL, -- no gate date
            10, -- NOT SCHEDULED
            'A'
        );


    o_status := success;

 EXCEPTION
    WHEN NO_DATA_FOUND THEN
        o_status := no_data;

    WHEN e_null_constraint THEN
        o_status := null_field;

    WHEN e_check_constraint THEN
        o_status := check_violated;

    WHEN e_unique_constraint THEN
        o_status := dupe_key;

    WHEN e_no_parent_key THEN
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
END;



---------------------------------------------------------------------
-- REMOVE_GATE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Brian       30-Apr-00   Created for Process Module
-- Robin       11-May-00   Added error handling.

Procedure REMOVE_GATE
(
    p_gate_id IN NUMBER,
    o_status OUT NUMBER
)

IS
    stored_proc_name VARCHAR2(100) := 'REMOVE_GATE';

BEGIN
    UPDATE pn_gate SET record_status = 'D'
        WHERE gate_id = p_gate_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id = p_gate_id;


    o_status := success;
    COMMIT;

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


---------------------------------------------------------------------
-- STORE_DELIVERABLE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation from Brian's procs.
-- Robin       11-May-00   Added error handling.

Procedure STORE_DELIVERABLE
(
    p_person_id IN NUMBER,
    p_phase_id IN NUMBER,
    p_deliverable_id IN NUMBER,
    p_space_id IN NUMBER,
    p_deliverable_name IN VARCHAR2,
    p_deliverable_desc IN VARCHAR2,
    p_status_id IN NUMBER,
    p_methodology_deliverable_id IN NUMBER,
    p_is_optional IN NUMBER,
    p_record_status IN VARCHAR2,
    i_is_deliverable_comments_null in number,
    o_deliverable_comments_clob out clob,
    o_deliverable_id OUT NUMBER
)
IS
    v_deliverable_id NUMBER(20);
    stored_proc_name VARCHAR2(100) := 'STORE_DELIVERABLE';

BEGIN
    -- NEW deliverable, INSERT
    IF ((p_deliverable_id IS NULL) OR (p_deliverable_id = ''))
    THEN
        -- Create the object
        v_deliverable_id := BASE.CREATE_OBJECT('deliverable', p_person_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_deliverable_id, 'deliverable', p_space_id, p_person_id);

        if (i_is_deliverable_comments_null > 0) then
            -- Insert with null comments
            INSERT INTO pn_deliverable
                (deliverable_id, deliverable_name, deliverable_desc, deliverable_comments_clob,
                 status_id, methodology_deliverable_id, is_optional, record_status)
            VALUES
                (v_deliverable_id, p_deliverable_name, p_deliverable_desc, null,
                 p_status_id, p_methodology_deliverable_id, p_is_optional, p_record_status)
            returning
                deliverable_comments_clob into o_deliverable_comments_clob;
        else
            -- Insert with empty clob
            INSERT INTO pn_deliverable
                (deliverable_id, deliverable_name, deliverable_desc, deliverable_comments_clob,
                 status_id, methodology_deliverable_id, is_optional, record_status)
            VALUES
                (v_deliverable_id, p_deliverable_name, p_deliverable_desc, empty_clob(),
                 p_status_id, p_methodology_deliverable_id, p_is_optional, p_record_status)
            returning
                deliverable_comments_clob into o_deliverable_comments_clob;
        end if;

        -- Create new pn_phase_has_deliverable record

        INSERT INTO
            pn_phase_has_deliverable
        (
            phase_id,
            deliverable_id
        )
        VALUES
        (
            p_phase_id,
            v_deliverable_id
        );

        o_deliverable_id := v_deliverable_id;

    -- EXISTING DELIVERABLE, UPDATE
    ELSE

        if (i_is_deliverable_comments_null > 0) then
            -- Insert null into clob
            UPDATE
                pn_deliverable
            SET
                deliverable_id = p_deliverable_id,
                deliverable_name = p_deliverable_name,
                deliverable_desc = p_deliverable_desc,
                deliverable_comments_clob = null,
                status_id = p_status_id,
                methodology_deliverable_id = p_methodology_deliverable_id,
                is_optional = p_is_optional,
                record_status = p_record_status
            WHERE
                deliverable_id = p_deliverable_id
            returning
                deliverable_comments_clob into o_deliverable_comments_clob;
        else
            -- clear out clob for subsequent update
            UPDATE
                pn_deliverable
            SET
                deliverable_id = p_deliverable_id,
                deliverable_name = p_deliverable_name,
                deliverable_desc = p_deliverable_desc,
                deliverable_comments_clob = empty_clob(),
                status_id = p_status_id,
                methodology_deliverable_id = p_methodology_deliverable_id,
                is_optional = p_is_optional,
                record_status = p_record_status
            WHERE
                deliverable_id = p_deliverable_id
            returning
                deliverable_comments_clob into o_deliverable_comments_clob;
        end if;

        o_deliverable_id := p_deliverable_id;

    END IF;

 EXCEPTION
    WHEN OTHERS THEN
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
END;



---------------------------------------------------------------------
-- COPY_DELIVERABLE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        18-Sep-00   Created

Procedure COPY_DELIVERABLE
(
    i_deliverable_id IN NUMBER,
    i_to_phase_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_status OUT NUMBER
)
IS
    v_new_deliverable_id NUMBER(20);
    v_deliverable_rec pn_deliverable%rowtype;

    stored_proc_name VARCHAR2(100) := 'STORE_DELIVERABLE';

BEGIN
        -- Create the object
        v_new_deliverable_id := BASE.CREATE_OBJECT('deliverable', i_actor_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_new_deliverable_id, 'deliverable', i_to_space_id, i_actor_id);


        -- get the old deliverable info
        select * into v_deliverable_rec from pn_deliverable
        where deliverable_id = i_deliverable_id;

        INSERT INTO pn_deliverable
        (
            deliverable_id,
            deliverable_name,
            deliverable_desc,
            deliverable_comments_clob,
            status_id,
            methodology_deliverable_id,
            is_optional,
            record_status
        )
        VALUES
        (
            v_new_deliverable_id,
            v_deliverable_rec.deliverable_name,
            v_deliverable_rec.deliverable_desc,
            v_deliverable_rec.deliverable_comments_clob,
            10, -- NOT STARTED
            v_deliverable_rec.methodology_deliverable_id,
            v_deliverable_rec.is_optional,
            'A'
        );

        -- Create new pn_phase_has_deliverable record

        INSERT INTO
            pn_phase_has_deliverable
        (
            phase_id,
            deliverable_id
        )
        VALUES
        (
            i_to_phase_id,
            v_new_deliverable_id
        );

    o_status := success;

 EXCEPTION
    WHEN OTHERS THEN
        o_status := generic_error;
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
END;

---------------------------------------------------------------------
-- REMOVE_DELIVERABLE
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Brian       30-Apr-00   Created for Process Module
-- Robin       11-May-00   Added error handling.

Procedure REMOVE_DELIVERABLE
(
    p_deliverable_id IN NUMBER,
    o_status OUT NUMBER
)
IS
    stored_proc_name VARCHAR2(100) := 'REMOVE_DELIVERABLE';

BEGIN

    UPDATE pn_deliverable SET record_status = 'D'
        WHERE deliverable_id = p_deliverable_id;

    UPDATE pn_object SET record_status = 'D'
        WHERE object_id = p_deliverable_id;

    o_status := success;
    COMMIT;

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

-- This function returns the percent complete (as a decimal)
-- calculated based on the sum of work_ms and work_complete_ms of each
-- schedule entry associated with the specified phase.
-- Note, summary tasks are (necessarily) not included in this calculation.
function GET_PHASE_SCHEDULE_COMPLETE (
    i_phase_id in NUMBER
)
    return NUMBER
is
    v_work_complete NUMBER;
    v_work NUMBER;
    o_percent_complete NUMBER;
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.GET_PHASE_ENTRIES_PERCENT_COMPLETE';
begin

    -- first get the work for all active (non-summary) tasks in the plan
    select sum(t.work_ms) into v_work
    from pn_task t, pn_phase_has_task pht
    where pht.phase_id = i_phase_id
    and t.task_id = pht.task_id
    and t.record_status = 'A'
    and t.task_type != 'summary';

    -- next get the work complete for all active (non-summary) tasks in the plan
    select sum(t.work_complete_ms) into v_work_complete
    from pn_task t, pn_phase_has_task pht
    where pht.phase_id = i_phase_id
    and t.task_id = pht.task_id
    and t.record_status = 'A'
    and t.task_type != 'summary';

     if (v_work = 0) then
        o_percent_complete := 0;
    else
        o_percent_complete := (v_work_complete / v_work) * 100;
    end if;

    return o_percent_complete;

end GET_PHASE_SCHEDULE_COMPLETE;

-- returns the earliest start date of the group of tasks
-- "owned" by this phase
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Carlos      07-Aug-06  Removed summary task restrictions in select
--                        in order to be able to retrieve the start date
--                        when a phase in linked to a summary task.
function GET_PHASE_WORKPLAN_START (
    i_phase_id in NUMBER
)
    return DATE
 is
    startDate DATE;
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.GET_PHASE_WORKPLAN_START';
begin

    select min(t.date_start) into startDate
    from pn_task t, pn_phase_has_task pht
    where pht.phase_id = i_phase_id
    and t.task_id = pht.task_id
    and t.record_status = 'A';

    return startDate;

end GET_PHASE_WORKPLAN_START;

-- returns the latest finish date of the group of tasks
-- "owned" by this phase
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Carlos      07-Aug-06  Removed summary task restrictions in select
--                        in order to be able to retrieve finish date
--                        when a phase in linked to a summary task.
function GET_PHASE_WORKPLAN_FINISH (
    i_phase_id in NUMBER
)
    return DATE
 is
    finishDate DATE;
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.GET_PHASE_WORKPLAN_FINISH';
begin

    select max(t.date_finish) into finishDate
    from pn_task t, pn_phase_has_task pht
    where pht.phase_id = i_phase_id
    and t.task_id = pht.task_id
    and t.record_status = 'A';

    return finishDate;

end GET_PHASE_WORKPLAN_FINISH;

END; -- Package Body PROCESS
/

CREATE OR REPLACE PACKAGE process

  IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Robin       06-Apr-00  Creation from Brian's procs.
-- Brian       30-Apr-00  Added a remove procedure
-- Robin       11-May-00  Added error codes to coincide with new table.
-- Carlos      07-Aug-06  Updated GET_PHASE_WORKPLAN_START and GET_PHASE_WORKPLAN_FINISH.

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

Procedure STORE_PROCESS
(
    p_person_id IN NUMBER,
    p_space_id IN NUMBER,
    p_process_id IN NUMBER,
    p_process_name IN VARCHAR2,
    p_process_desc IN VARCHAR2,
    p_current_phase_id IN NUMBER,
    p_last_gate_passed_id IN NUMBER,
    p_record_status IN VARCHAR2,
    o_process_id OUT NUMBER,
    o_status OUT NUMBER
);

Procedure COPY_PROCESS
(
    i_from_space_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_return_value OUT NUMBER
);


Procedure REMOVE_PROCESS
(
    p_process_id IN NUMBER,
    o_status OUT NUMBER
);

Procedure STORE_PHASE
(
    p_person_id IN NUMBER,
    p_phase_id  IN NUMBER,
    p_process_id IN NUMBER,
    p_space_id IN NUMBER,
    p_phase_name IN VARCHAR2,
    p_phase_desc IN VARCHAR2,
    p_sequence IN NUMBER,
    p_status_id IN NUMBER,
    p_record_status IN VARCHAR2,
    p_progress_reporting_method IN VARCHAR2,
    p_start_date IN DATE,
    p_end_date IN DATE,
    p_entered_percent_complete IN NUMBER,
    o_phase_id OUT NUMBER,
    o_status OUT NUMBER
);

Procedure COPY_PHASE
(
    i_phase_id  IN NUMBER,
    i_to_process_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_return_value OUT NUMBER
 );


Procedure REMOVE_PHASE
(
    p_phase_id IN NUMBER
);

Procedure STORE_GATE
(
    p_person_id IN NUMBER,
    p_gate_id IN NUMBER,
    p_phase_id  IN NUMBER,
    p_space_id IN NUMBER,
    p_gate_name IN VARCHAR2,
    p_gate_desc IN VARCHAR2,
    p_gate_date IN DATE,
    p_status_id IN NUMBER,
    p_record_status IN VARCHAR2,
    o_gate_id OUT NUMBER,
    o_status OUT NUMBER
);

Procedure COPY_GATE
(
    i_gate_id IN NUMBER,
    i_to_phase_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_status OUT NUMBER
);


Procedure REMOVE_GATE
(
    p_gate_id IN NUMBER,
    o_status OUT NUMBER
);

Procedure STORE_DELIVERABLE
(
    p_person_id IN NUMBER,
    p_phase_id IN NUMBER,
    p_deliverable_id IN NUMBER,
    p_space_id IN NUMBER,
    p_deliverable_name IN VARCHAR2,
    p_deliverable_desc IN VARCHAR2,
    p_status_id IN NUMBER,
    p_methodology_deliverable_id IN NUMBER,
    p_is_optional IN NUMBER,
    p_record_status IN VARCHAR2,
    i_is_deliverable_comments_null in number,
    o_deliverable_comments_clob out clob,
    o_deliverable_id OUT NUMBER
);

Procedure COPY_DELIVERABLE
(
    i_deliverable_id IN NUMBER,
    i_to_phase_id IN NUMBER,
    i_to_space_id IN NUMBER,
    i_actor_id IN NUMBER,
    o_status OUT NUMBER
);

Procedure REMOVE_DELIVERABLE
(
    p_deliverable_id IN NUMBER,
    o_status OUT NUMBER
);

function GET_PHASE_SCHEDULE_COMPLETE (
    i_phase_id in NUMBER
)  return NUMBER;

function GET_PHASE_WORKPLAN_START (
    i_phase_id in NUMBER
)   return DATE;

    function GET_PHASE_WORKPLAN_FINISH (
    i_phase_id in NUMBER
)    return DATE;

END; -- Package Specification PROCESS
/

CREATE OR REPLACE PACKAGE BODY product IS
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Phil        1/15/01 created

Procedure GET_VERSION
    (i_product IN VARCHAR2,
     o_current_version OUT VARCHAR2)

IS

BEGIN

    -- select string of major.minor.sub-minor.build
    SELECT lpad(major_version,2,0)||'.'||lpad(minor_version,2,0)||'.'||lpad(sub_minor_version,2,0)||'.'||lpad(build_version,2,0) INTO o_current_version
      FROM product_version
      WHERE product = i_product
      AND timestamp = (select max(timestamp) from product_version where product = i_product);

EXCEPTION
    WHEN OTHERS THEN
        o_current_version := NULL;

END; -- Procedure GET_VERSION

/*
 Returns the database version
 */
Procedure GET_DATABASE_VERSION (
    o_current_version OUT VARCHAR2,
    o_client_version OUT VARCHAR2,
    o_client_name OUT VARCHAR2)
IS

BEGIN
    -- select string of major.minor.sub-minor.x
    BEGIN
        select
  major_version||'.'||minor_version||'.'||sub_minor_version||'.x' into o_current_version
from
  database_version dv,
  (select max(major_version*10000 + minor_version*100 + sub_minor_version) as version_id
     from database_version) maxdv
where
  (major_version*10000 + minor_version*100 + sub_minor_version) = maxdv.version_id;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            o_current_version := null;
    END;

        BEGIN
        select
  major_version||'.'||minor_version||'.'||sub_minor_version||'.x' into o_client_version
from
  client_database_version dv,
  (select max(major_version*10000 + minor_version*100 + sub_minor_version) as version_id
     from client_database_version) maxdv
where
  (major_version*10000 + minor_version*100 + sub_minor_version) = maxdv.version_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            o_client_version := null;
    END;

    BEGIN
        SELECT client_name INTO o_client_name
        FROM client_database_version
        WHERE timestamp = (select max(timestamp) from client_database_version);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            o_client_name := null;
    END;
END; -- Procedure GET_DATABASE_VERSION

END;
/

CREATE OR REPLACE PACKAGE product IS
--
-- General product support (version #'s, etc)

--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Phil        1/15/01 created
-- Tim         04/09/01 Added GET_DATABASE_VERSION since we use a different
--                      table for database version
-- Matt        09/24/01 Add o_client_version and o_client_name parameters to
--                      getDatabaseVersion().

PROCEDURE get_version
 ( i_product IN VARCHAR2,
   o_current_version OUT varchar2);

/*
 Returns the database version
 */
procedure GET_DATABASE_VERSION
    (o_current_version OUT VARCHAR2,
     o_client_version OUT VARCHAR2,
     o_client_name OUT VARCHAR2);

END; -- Package spec
/

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

CREATE OR REPLACE PACKAGE BODY project IS

--------------------------------------------------------------------
-- INVITE_PERSON_TO_PERSON
--------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Robin       27-Apr-00  Creation.
-- Robin       11-May-00  Changed project_id to space_id in invited_users.
-- Adam        20-May-00  User is granted access to their personal document module when they create a project
-- Tim         27-Jan-01  Added NEWS module
-- Roger        5-Jul-01  Improved space relationship inserts; removed space_has_space, now done by SpaceManager.java

Procedure invite_person_to_project
(
    i_project_id IN NUMBER,
    i_email IN VARCHAR2,
    i_firstname IN VARCHAR2,
    i_lastname IN VARCHAR2,
    i_responsibilities IN VARCHAR2,
    i_invitor_id IN NUMBER,
    o_invitation_code OUT NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'PROJECT.INVITE_PERSON_TO_PROJECT';
v_sysdate DATE;
v_invited_status CONSTANT VARCHAR2(20) := 'Invited';

BEGIN

    SELECT sysdate INTO v_sysdate FROM dual;

    INSERT INTO pn_invited_users
        (invitation_code, space_id, invitee_email, invitee_firstname, invitee_lastname,
         invitee_responsibilities, invitor_id, date_invited, date_responded, invited_status)
      VALUES
        (pn_invite_sequence.nextval, i_project_id, i_email, i_firstname, i_lastname,
         i_responsibilities, i_invitor_id, v_sysdate, null, v_invited_status);

    SELECT pn_invite_sequence.currval INTO o_invitation_code FROM dual;


    COMMIT;
    o_status := success;

EXCEPTION

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
            VALUES (v_sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END invite_person_to_project;


--------------------------------------------------------------------
-- RESPOND_TO_PROJECT_INVITE
--------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Robin       27-Apr-00  Creation.
-- Robin       11-May-00  Changed project_id to space_id in invited_users.


Procedure respond_to_project_invite
(
    i_invitation_code IN NUMBER,
    i_project_id IN NUMBER,
    i_person_id IN NUMBER,
    i_response IN VARCHAR2,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'PROJECT.RESPOND_TO_PROJECT_INVITE';
v_sysdate DATE;
v_status NUMBER;
v_group_id pn_group.group_id%TYPE;

BEGIN

    SELECT sysdate INTO v_sysdate FROM dual;

    IF (i_response = 'Accepted') THEN

        INSERT INTO pn_space_has_person
            (space_id, person_id, relationship_person_to_space, relationship_space_to_person,
             responsibilities, record_status)
          SELECT i_project_id, i_person_id, 'member', 'has', invitee_responsibilities, 'A'
             FROM pn_invited_users
             WHERE invitation_code = i_invitation_code;

        IF SQL%NOTFOUND THEN
            o_status := no_data;
            ROLLBACK;
            RETURN;
        END IF;

        INSERT INTO pn_group_has_person
            (group_id, person_id)
          SELECT g.group_id, i_person_id
             FROM pn_group g, pn_space_has_group sg
             WHERE space_id = i_project_id
               AND g.group_type_id = SECURITY.GROUP_TYPE_TEAMMEMBER
               AND g.group_id = sg.group_id;

        IF SQL%NOTFOUND THEN
            o_status := no_data;
            ROLLBACK;
            RETURN;
        END IF;

        -- portfolio
        INSERT INTO pn_portfolio_has_space
            (portfolio_id, space_id)
          SELECT membership_portfolio_id, i_project_id
             FROM pn_person_view
             WHERE person_id = i_person_id;

        IF SQL%NOTFOUND THEN
            o_status := no_data;
            ROLLBACK;
            RETURN;
        END IF;

        -- principal group
        v_group_id := NULL;
        security.create_principal_group(i_person_id, i_project_id, v_group_id);

        v_status := NULL;
        security.add_person_to_group(v_group_id, i_person_id, v_status);

    END IF;

    UPDATE pn_invited_users
       SET date_responded = v_sysdate, invited_status = i_response
       WHERE invitation_code = i_invitation_code
         AND space_id = i_project_id;

    IF SQL%NOTFOUND THEN
        o_status := no_data;
        ROLLBACK;
        RETURN;
    END IF;

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
            VALUES (v_sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END respond_to_project_invite;

--------------------------------------------------------------------
-- GET_PROPERTY_GROUP_INFO
--------------------------------------------------------------------

procedure get_property_group_info
(
    project_id IN varchar2,
    property_group_id OUT NUMBER,
    property_sheet_type OUT varchar2,
    property_table_name OUT varchar2
)
AS

    v_project_id            pn_project_space.project_id%type := to_number(project_id);
    v_property_sheet_id     pn_property_sheet.property_sheet_id%type;
    v_property_sheet_type   pn_property_sheet_type.property_sheet_type%type;
    v_property_group_id     pn_property_sheet.property_group_id%type;
    v_property_table_name   pn_property_sheet_type.properties_table_name%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'PROJECT.GET_PROPERTY_GROUP_INFO';


BEGIN

    select property_sheet_id into v_property_sheet_id from pn_space_has_property_sheet
        where space_id = v_project_id;

    select property_sheet_type, property_group_id
        into v_property_sheet_type, v_property_group_id
        from pn_property_sheet where property_sheet_id = v_property_sheet_id;

    select properties_table_name into v_property_table_name from pn_property_sheet_type
        where property_sheet_type = v_property_sheet_type;


    property_group_id := v_property_group_id;
    property_sheet_type := v_property_sheet_type;
    property_table_name := v_property_table_name;

    EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;


END;

--------------------------------------------------------------------
-- add_logo_to_project
--------------------------------------------------------------------
procedure add_logo_to_project
(
    project_id IN varchar2,
    logo_id IN varchar2
)

as

   v_project_id     pn_project_space.project_id%type := to_number(project_id);
   v_logo_id        pn_document.doc_id%type := to_number(logo_id);

BEGIN

    update pn_project_space
        set project_logo_id = v_logo_id
    where project_id = v_project_id;

COMMIT;

END;


--------------------------------------------------------------------
-- CREATE_PROJECT
--------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Phil        27-Apr-00  Creation.
-- Robin       23-Jun-00  Removed 110 from project security.
-- Phil        21-Nov-00  Added biz space admin group to project.
-- Matt        12-Dec-02  Refactor creation of plan into SCHEDULE package


PROCEDURE create_project
(
    p_proj_creator IN VARCHAR2,
    p_subproject_of IN VARCHAR2,
    p_business_space_id IN VARCHAR2,
    p_project_visibility IN VARCHAR2,
    p_proj_name IN VARCHAR2,
    p_proj_desc IN VARCHAR2,
    p_proj_status IN VARCHAR2,
    p_proj_color_code IN VARCHAR2,
    p_calculation_method in varchar2,
    p_percent_complete IN varchar2,
    p_start_date IN date,
    p_end_date IN date,
    p_serial IN varchar2,
    p_project_logo_id IN varchar2,
    p_default_currency_code in varchar2,
    p_sponsor in varchar2,
    p_improvement_code_id in number,
    p_current_status_description in varchar2,
    p_financial_stat_color_code_id in number,
    p_financial_stat_imp_code_id in number,
    p_budgeted_total_cost_value in number,
    p_budgeted_total_cost_cc in varchar2,
    p_current_est_total_cost_value in number,
    p_current_est_total_cost_cc in varchar2,
    p_actual_to_date_cost_value in number,
    p_actual_to_date_cost_cc in varchar2,
    p_estimated_roi_cost_value in number,
    p_estimated_roi_cost_cc in varchar2,
    p_cost_center in varchar2,
    p_schedule_stat_color_code_id in number,
    p_schedule_stat_imp_code_id in number,
    p_resource_stat_color_code_id in number,
    p_resource_stat_imp_code_id in number,
    p_priority_code_id in number,
    p_risk_rating_code_id in number,
    p_visibility_id in number,
    p_autocalc_schedule in number,
    p_plan_name in varchar2,
    p_create_share in number,
    o_project_id OUT NUMBER,
    o_workplan_id OUT NUMBER,
    o_space_admin_role_id OUT NUMBER

)
AS

    -- local variable declarations
    v_project_id             pn_project_space.project_id%type;
    v_creator                pn_object.created_by%type := TO_NUMBER(p_proj_creator);
    v_subproject_of          pn_space_has_space.parent_space_id%type := to_number(p_subproject_of);
    v_business_space_id      pn_business_space.business_id%type := to_number(p_business_space_id);
    v_status_id              pn_project_space.status_code_id%type := to_number(p_proj_status);
    v_color_code             pn_project_space.color_code_id%type := to_number(p_proj_color_code);
    v_logo_id                pn_project_space.project_logo_id%type := to_number(p_project_logo_id);
    v_percent_complete       pn_project_space.percent_complete%type := to_number(p_percent_complete);

    v_group_id               pn_group.group_id%type;
    v_space_admin_group_id   pn_group.group_id%type;
    v_power_user_group_id    pn_group.group_id%type;
    v_parent_admin_group_id  pn_group.group_id%type;
    v_space_id               pn_project_space.project_id%type;
    v_doc_container_id       pn_doc_container.doc_container_id%type := 0;
    v_system_container_id    pn_doc_container.doc_container_id%type :=0;
    v_doc_space_id           pn_doc_space.doc_space_id%type := 0;
    v_doc_provider_id        pn_doc_provider.doc_provider_id%type := 0;
    v_directory_id           pn_directory.directory_id%type := 0;
    v_calendar_id            pn_calendar.calendar_id%type := 0;
    v_portfolio_id           pn_portfolio.portfolio_id%type := 0;
    v_membership_portfolio_id   pn_portfolio.portfolio_id%type := 0;
    is_subproject            NUMBER(1) := 0;
    v_is_private             NUMBER(1) := 0;
    v_property_sheet_id      pn_property_sheet.property_sheet_id%type;
    v_property_group_id      pn_object.object_id%type;
    v_property_sheet_type    pn_property_sheet_type.property_sheet_type%type;

    v_custom_domain          pn_custom_domain%rowtype;
    v_object_type            pn_object_type.object_type%type;
    v_actions                pn_object_type.default_permission_actions%type;
    v_object_type_actions    pn_object_type.default_permission_actions%type;
    v_module_id              pn_module.module_id%type;
    v_plan_id                pn_plan.plan_id%TYPE;

    v_system_space_id       pn_system_config.system_space_id%type;
    v_contact_class_id      pn_class.class_id%type;

    v_sysdate                DATE;
    v_status                 NUMBER;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'CREATE_PROJECT';

    -- global (static) variable declaration
   G_SECURITY_MODULE pn_object_type.object_type%type := 'group';

   G_AEC_PROPERTY_SHEET_TYPE pn_property_sheet_type.property_sheet_type%type := 100;

    -- cursor definitions
   	CURSOR  c_object_type IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;

    CURSOR  c_default_permissions (project_id pn_project_space.project_id%type) IS
        select group_id, actions
        from pn_default_object_permission
        where space_id = project_id and object_type = 'project';


BEGIN

    SET TRANSACTION READ WRITE;

    SELECT sysdate INTO v_sysdate FROM DUAL;

    -- PROJECT SPACE OWNER
    -- business_space owns the project.  If business_space is null, person owns project
    IF (v_business_space_id IS NULL)
    THEN
        v_space_id := v_business_space_id;
    ELSE
        v_space_id := v_creator;
    END IF;


    -- SET SUBPROJECT
    IF (v_subproject_of <> 0) THEN
        is_subproject := 1;
    ELSE
        is_subproject := 0;
    END IF;

    -- Create the project space
    v_project_id := BASE.CREATE_OBJECT('project', v_creator, 'A');

    INSERT INTO pn_project_space
        (project_id, project_name, project_desc, date_modified, modified_by_id,
         status_code_id, project_logo_id, start_date, end_date, color_code_id,
         is_subproject, percent_calculation_method, percent_complete, record_status, CRC,
         default_currency_code, SPONSOR_DESC, IMPROVEMENT_CODE_ID, CURRENT_STATUS_DESCRIPTION,
         financial_status_color_code_id, financial_status_imp_code_id,
         budgeted_total_cost_value, budgeted_total_cost_cc,
         current_est_total_cost_value, current_est_total_cost_cc,
         actual_to_date_cost_value, actual_to_date_cost_cc,
         estimated_roi_cost_value, estimated_roi_cost_cc,
         cost_center,
         schedule_status_color_code_id, schedule_status_imp_code_id,
         resource_status_color_code_id, resource_status_imp_code_id,
         priority_code_id, risk_rating_code_id, visibility_id
         )
    VALUES
        (v_project_id, p_proj_name, p_proj_desc, v_sysdate, v_creator,
         v_status_id, v_logo_id, p_start_date, p_end_date, v_color_code,
         is_subproject, p_calculation_method, v_percent_complete, 'A', v_sysdate,
         p_default_currency_code, p_sponsor, p_improvement_code_id, p_current_status_description,
         p_financial_stat_color_code_id, p_financial_stat_imp_code_id,
         p_budgeted_total_cost_value, p_budgeted_total_cost_cc,
         p_current_est_total_cost_value, p_current_est_total_cost_cc,
         p_actual_to_date_cost_value, p_actual_to_date_cost_cc,
         p_estimated_roi_cost_value, p_estimated_roi_cost_cc,
         p_cost_center,
         p_schedule_stat_color_code_id, p_schedule_stat_imp_code_id,
         p_resource_stat_color_code_id, p_resource_stat_imp_code_id,
         p_priority_code_id, p_risk_rating_code_id, p_visibility_id
         );

    /****
        Setup Project Property Sheet
        NOTE: THIS IS HARDWIRED FOR NOW.  *MUST* BE CHANGED
        WHEN WE SUPPORT MORE THAN ONE PROPERTY SHEET
    ****/

     v_property_sheet_type := G_AEC_PROPERTY_SHEET_TYPE;

     select pn_object_sequence.nextval into v_property_sheet_id from dual;
     select pn_object_sequence.nextval into v_property_group_id from dual;


     insert into pn_property_sheet
     (
        property_sheet_id,
        property_sheet_type,
        property_group_id
     )
     values
     (
        v_property_sheet_id,
        v_property_sheet_type,
        v_property_group_id
     );


     insert into pn_space_has_property_sheet
     (
        space_id,
        property_sheet_id
     )
     values
     (
        v_project_id,
        v_property_sheet_id
     );


    /****************************************************************************************************
     *  PROJECT SECURITY
     ****************************************************************************************************/

    -- SPACE_HAS_MODULES
    -- The new project space initially has access to all modules.
    -- Copy all pn_module entries to pn_space_has_modules for this project

    INSERT INTO pn_space_has_module (
        space_id,
        module_id,
        is_active)
    SELECT v_project_id, module_id, '1'
    FROM
        pn_module
    WHERE
        module_id IN (10, 20, 30, 40, 60, 70, 90, 100, 110, 120, 140, 150, 180, 190, 200, 210, 260, 310, 330);

    -- Disable below mentioned modules for the time-being

    UPDATE pn_space_has_module SET is_active = '0' WHERE  space_id = v_project_id AND module_id IN (90 , 100 , 120 , 210 ) ;


    -- SPACE ADMINISTRATOR GROUP
    -- The project creator is the inital space administrator of this new project
    v_space_admin_group_id := security.F_CREATE_SPACE_ADMIN_GROUP(v_creator, v_project_id, '@prm.project.security.group.type.spaceadmin.description');

    -- POWER USER GROUP
    --  CREATE POWER USER GROUP
    v_power_user_group_id := security.F_CREATE_POWER_USER_GROUP(v_creator, v_project_id, '@prm.security.group.type.poweruser.description');

    -- BUSINESS ADMINISTRATOR GROUP
    -- If this project is owned by a business, add the business' space administrator
    -- group to this space and initially afford it the permissions of the space admin group
    if ((p_business_space_id IS NOT NULL) AND (p_business_space_id <> 0))
    THEN
        v_parent_admin_group_id := security.create_parent_admin_role(v_project_id, p_business_space_id);
    END IF;

    -- PRINCIPAL GROUP
    -- The project creator (person) must be put into a principal group for this space.
    v_group_id := NULL;
    SECURITY.CREATE_PRINCIPAL_GROUP(v_creator, v_project_id, v_group_id);

    -- add project creator (person) to their principal group
    v_status := NULL;
    SECURITY.ADD_PERSON_TO_GROUP(v_group_id, v_creator, v_status);



    -- TEAM MEMBER GROUP
    -- The creator is the only initial team member
    v_group_id := NULL;
    SECURITY.CREATE_TEAMMEMBER_GROUP(v_creator, v_project_id, v_group_id);

    v_status := NULL;
    SECURITY.ADD_PERSON_TO_GROUP(v_group_id, v_creator, v_status);

    -- A. Klatzkin 5/19/00
    -- per T. Kurke user will not have access to their personal document module until they
    -- have created at least one project.  Therefore we will enable access to it at this point.

    UPDATE pn_module_permission
      SET actions = 65535
      WHERE space_id=v_creator AND
            module_id=10;

    /****************************************************************************************************
    *  END PROJECT SECURITY
    ****************************************************************************************************/

     --  Add the creator to the project's team roster
    INSERT INTO pn_space_has_person
        (space_id, person_id, relationship_person_to_space, member_type_id, record_status)
    VALUES
        (v_project_id, v_creator, 'member', 200, 'A');


    -- Every project space gets the default directory
    Select directory_id into v_directory_id from pn_directory where is_default = 1;

    INSERT INTO pn_space_has_directory
        (directory_id, space_id)
    VALUES
        (v_directory_id, v_project_id);



    /***********************************************************************************************
       CREATE DOCUMENT SPACES FOR PROJECT
       The Business Space this project is being created within has a table of available doc providers.
       Each project space get it's own doc space created for each doc provider available to the business.
       TODO:  Set top-level container for the project's doc space.
    *************************************************************************************************/

    -- Get the doc_providers available to this project (defined by business space)
    -- Get a new project_id
    --
    -- TODO:  handle PLSQL exception for "no data found" when business space has no doc providers
    IF (v_business_space_id <> 0)
    THEN
        SELECT
            doc_provider_id into v_doc_provider_id
        FROM
            pn_space_has_doc_provider
        WHERE
            space_id = v_business_space_id
        AND
            is_default = 1;
    ELSE
        -- use only default doc provider initially
        select doc_provider_id into v_doc_provider_id
        from pn_doc_provider
        where is_default = 1;  -- default provider
    END IF;

     -- new doc space for this project
    v_doc_space_id := BASE.CREATE_OBJECT('doc_space', v_creator, 'A');

    insert into pn_doc_space
        (doc_space_id, doc_space_name, record_status, crc)
    values
        (v_doc_space_id, 'default', 'A', v_sysdate);

    -- this project owns the doc space
    insert into pn_space_has_doc_space
      (space_id, doc_space_id)
    values (v_project_id, v_doc_space_id);

    -- link new doc_space back to it's doc_provider
    insert into pn_doc_provider_has_doc_space
        (doc_provider_id, doc_space_id)
    values
        (v_doc_provider_id, v_doc_space_id);


    -- Create new doc container for the Top-level folder for this project.
    v_doc_container_id := BASE.CREATE_OBJECT('doc_container', v_creator, 'A');

    insert into pn_doc_container
        (doc_container_id, container_name, container_description, modified_by_id, date_modified,record_status, crc)
    values
        (v_doc_container_id, '@prm.document.container.topfolder.name', 'Top level document folder', v_creator, v_sysdate, 'A', v_sysdate);

    SECURITY.APPLY_DOCUMENT_PERMISSIONS(v_doc_container_id, null, 'doc_container', v_project_id, v_creator);

    -- Link container (top folder) to doc space
    -- PHIL added the "is_root:1" to pn_doc_space_has_container
    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_doc_container_id, 1);


    -- SYSTEM Container for this space
    -- this container contains (*grin*) all system related objects
    v_system_container_id := BASE.CREATE_OBJECT('doc_container', v_creator, 'A');

    insert into pn_doc_container
        (doc_container_id, container_name, container_description, modified_by_id, date_modified,record_status, is_hidden, crc)
    values
        (v_system_container_id, v_project_id, 'System container for this space',v_creator, v_sysdate,'A', 1, v_sysdate);

    SECURITY.APPLY_DOCUMENT_PERMISSIONS(v_system_container_id, v_doc_container_id, 'doc_container', v_project_id, v_creator);

    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_system_container_id, 0);

   insert into pn_doc_container_has_object
        (doc_container_id, object_id)
    values
        (v_doc_container_id, v_system_container_id);


    -- Metrics container (contained by system container)
    v_doc_container_id := BASE.CREATE_OBJECT('doc_container', v_creator, 'A');

    insert into pn_doc_container
        (doc_container_id, container_name, container_description, modified_by_id, date_modified,record_status, is_hidden, crc)
    values
        (v_doc_container_id, v_project_id||'::Metrics', 'System container for this space', v_creator, v_sysdate, 'A', 1, v_sysdate);

    SECURITY.APPLY_DOCUMENT_PERMISSIONS(v_doc_container_id, v_system_container_id,'doc_container', v_project_id, v_creator);

    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_doc_container_id, 0);

    insert into pn_doc_container_has_object
        (doc_container_id, object_id)
    values
        (v_system_container_id, v_doc_container_id);


    -- Budget Container
    v_doc_container_id := BASE.CREATE_OBJECT('doc_container', v_creator, 'A');

    insert into pn_doc_container
        (doc_container_id, container_name, container_description, modified_by_id, date_modified,record_status, is_hidden, crc)
    values
        (v_doc_container_id, v_project_id||'::Budget', 'System container for this space', v_creator, v_sysdate,'A', 1, v_sysdate);

    SECURITY.APPLY_DOCUMENT_PERMISSIONS(v_doc_container_id, v_system_container_id, 'doc_container', v_project_id, v_creator);

    insert into pn_doc_space_has_container
        (doc_space_id, doc_container_id, is_root)
    values
        (v_doc_space_id, v_doc_container_id, 0);

    insert into pn_doc_container_has_object
        (doc_container_id, object_id)
    values
        (v_system_container_id, v_doc_container_id);


    /****************************************************************
        PROJECT CALENDAR
    *****************************************************************/

    -- Create new doc container for the Top-level folder for this project.
    v_calendar_id := BASE.CREATE_OBJECT('calendar', v_creator, 'A');

    insert into pn_calendar
        (calendar_id, is_base_calendar, calendar_name, calendar_description, record_status)
    values
        (v_calendar_id, 1, 'Project Calendar', 'Main Project Calendar', 'A');

    SECURITY.CREATE_SECURITY_PERMISSIONS(v_calendar_id, 'calendar', v_project_id, v_creator);

    -- Link calendar to project space
    insert into pn_space_has_calendar
        (space_id, calendar_id)
    values
        (v_project_id, v_calendar_id);


    -- added by Robin 18-Apr-00
    -- add plan default
    SCHEDULE.STORE_PLAN(p_plan_name, 'Main Project Plan', p_start_date, p_end_date, p_autocalc_schedule, null, null, v_creator, v_project_id, null, null, null, null, null, 50, p_start_date, v_plan_id);

    if (p_create_share > 0) then
        --First, create the share for the project.
        sharing.store_share(v_project_id, -1, NULL, v_project_id, sharing.DEFAULT_PROPAGATION, sharing.DEFAULT_ALLOWABLE_ACTIONS);
        --Now, create the share for the plan.
        sharing.store_share(v_plan_id, 1, v_project_id, v_project_id, sharing.DEFAULT_PROPAGATION, sharing.DEFAULT_ALLOWABLE_ACTIONS);
    end if;

    /********************************************************************
      PROJECT PORTFOLIOS
      If the creator chose business visibility, add the project to the
      businesses' "complete portfolio".  Otherwise, we will do this when
      the user "moves" the project from his personal space to the business space.
     *********************************************************************/

    -- If project is being added to personal space scope,
    -- it is private (still being setup by the user).
    IF (p_project_visibility = 'Personal Space') THEN
        v_is_private := 1;
    ELSE
        v_is_private := 0;
    END IF;


    -- Add this project to the creator's "membership_portfolio".
    -- set is_private flag is not visibility="personal".
    IF (v_creator <> 0)
    THEN
        SELECT
             membership_portfolio_id INTO v_membership_portfolio_id
        FROM
             pn_person
        WHERE
            person_id = v_creator;
    END IF;


    -- the creator always gets the project added to their membership portfolio
    INSERT INTO pn_portfolio_has_space
        (portfolio_id, space_id, is_private)
    VALUES
        (v_membership_portfolio_id, v_project_id, v_is_private);


    -- BUSINESS SPACE OWNED
    -- Add this project to the owning business space portfolio.
    IF (v_business_space_id <> 0) THEN

        -- "owner" portfolio is used for grouping projects in the portfolio management pages.
        -- Get the business' project "owner" portfolio
        select
            p.portfolio_id into v_portfolio_id
        from
            pn_space_has_portfolio shp, pn_portfolio p
        where
            shp.space_id = p_business_space_id and
            shp.portfolio_id=p.portfolio_id and
            portfolio_name='owner';

        -- Add this project to the Business Space's Owner portfolio
        insert into pn_portfolio_has_space
            (portfolio_id, space_id, is_private)
        values
            (v_portfolio_id, v_project_id, v_is_private);

    END IF; -- END IF BUSINESS IS NOT NULL


 /*****
    COMMIT WORK SO FAR
  */

 COMMIT;



    /** Copy contacts moved to AFTER commit since COPY_FORM is an
        autonomous transaction, but requires access to rows created
        earlier in this procedure */

   /********************************************************************
      COPY CONTACTS FORM

      Copy the contacts form from the system space.
    *********************************************************************
    select system_space_id into v_system_space_id
    from pn_system_config;

    select c.class_id into v_contact_class_id
    from pn_space_has_class shc, pn_class c
    where shc.space_id = v_system_space_id
    and c.class_id = shc.class_id
    and c.class_name = 'Contacts';

    base.log_error('DEBUG [PROJECT.CREATE_PROJECT', 0, 'Copying form from space '||v_system_space_id||', to space '||p_project_id||', from form '||v_contact_class_id||', creator '||v_creator);
    FORMS.COPY_FORM(v_system_space_id, p_project_id, v_contact_class_id, v_creator);
    base.log_error('DEBUG [PROJECT.CREATE_PROJECT', 0, 'Done copying form');
    */

    o_project_id := v_project_id;

    -- due to an API requirement, we are now returning the workplan id as part of the project create
    o_workplan_id := v_plan_id;

    -- also return the role_id for the space administrator role
    o_space_admin_role_id := v_space_admin_group_id;

 EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        BASE.LOG_ERROR(stored_proc_name, sqlcode, sqlerrm);
        raise;
      END;

END;

PROCEDURE remove
(
    project_id IN VARCHAR2,
    whoami IN VARCHAR2
)

IS

    v_project_id        pn_project_space.project_id%type := to_number(project_id);
    v_whoami            pn_person.person_id%type := to_number(whoami);

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'PROJECT.REMOVE';


BEGIN



  UPDATE pn_object
    SET record_status = 'D'
    WHERE object_id = v_project_id;


  -- Soft delete the project_space
  UPDATE pn_project_space
    SET
        record_status = 'D',
        date_modified = SYSDATE,
        modified_by_id = v_whoami
    WHERE
        project_id = v_project_id;


     COMMIT;

   EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (SYSDATE,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;


END;

--------------------------------------------------------------------
-- UPDATE_PROJECT_PROPERTIES
--------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Phil        27-Apr-00  Creation.
-- Robin       13-Jun-00  Added changing business space owner

PROCEDURE update_properties
(
    p_project_id IN VARCHAR2,
    whoami IN varchar2,
    p_business_space_id IN VARCHAR2,
    p_project_visibility IN VARCHAR2,
    p_proj_name IN VARCHAR2,
    p_proj_desc IN VARCHAR2,
    p_proj_status IN VARCHAR2,
    p_proj_color_code IN VARCHAR2,
    p_calculation_method in varchar2,
    p_percent_complete IN varchar2,
    p_start_date IN date,
    p_end_date IN date,
    p_project_logo_id IN varchar2,
    p_default_currency_code in varchar2,
    p_sponsor in varchar2,
    p_improvement_code_id in number,
    p_current_status_description in varchar2,
    p_financial_stat_color_code_id in number,
    p_financial_stat_imp_code_id in number,
    p_budgeted_total_cost_value in number,
    p_budgeted_total_cost_cc in varchar2,
    p_current_est_total_cost_value in number,
    p_current_est_total_cost_cc in varchar2,
    p_actual_to_date_cost_value in number,
    p_actual_to_date_cost_cc in varchar2,
    p_estimated_roi_cost_value in number,
    p_estimated_roi_cost_cc in varchar2,
    p_cost_center in varchar2,
    p_schedule_stat_color_code_id in number,
    p_schedule_stat_imp_code_id in number,
    p_resource_stat_color_code_id in number,
    p_resource_stat_imp_code_id in number,
    p_priority_code_id in number,
    p_risk_rating_code_id in number,
    p_visibility_id in number,
    i_serial_number IN varchar2
)
AS

    v_project_id            pn_project_space.project_id%type := to_number(p_project_id);
    v_whoami                pn_person.person_id%type := to_number(whoami);
    v_business_space_id     pn_business_space.business_id%type := to_number(p_business_space_id);
    v_status_id             pn_project_space.status_code_id%type := to_number(p_proj_status);
    v_color_code            pn_project_space.color_code_id%type := to_number(p_proj_color_code);
    v_project_logo_id       pn_project_space.project_logo_id%type := to_number(p_project_logo_id);
    v_percent_complete      pn_project_space.percent_complete%type := to_number(p_percent_complete);


    v_property_group_id     pn_property_sheet.property_group_id%type;
    v_property_sheet_type   pn_property_sheet.property_sheet_type%type;
    v_property_table        pn_property_sheet_type.properties_table_name%type;

    v_current_business NUMBER;
    no_current_business NUMBER := -1;

    /*  BFD 1064 */
    v_group_id NUMBER;
    v_remove_status NUMBER;
    v_parent_admin_group_id NUMBER;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'PROJECT.UPDATE_PROPERTIES';

BEGIN
    ----------------------------------------------------------------------------
    --- BEGIN Updating Business Owner
    ----------------------------------------------------------------------------

    BEGIN
        -- Get the business that ownes this project currently.
        SELECT b.business_space_id INTO v_current_business
            FROM pn_business_space b, pn_space_has_space ss
            WHERE ss.child_space_id = v_project_id
            AND b.business_space_id = ss.parent_space_id;

	/*  BFD 1064 */
	SELECT  max(g.group_id) INTO  v_group_id
	    FROM pn_group_view g, pn_space_has_group shg
	    WHERE g.group_id = shg.group_id
	    AND is_owner = 0
	    AND space_id = v_project_id
	    AND g.record_status = 'A' ;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_current_business := no_current_business;

    END;

    -- delete the old business owner
    -- NOTE: this query needs to be more specific now that we have more
    --       types of portfolios.  -Roger 7/7/01
    -- NOTE: ACTUALLY, Portfolios should no longer be used for OWNERSHIP, only
    --       space_has_space. -Roger 7/7/01
    IF (v_current_business > 0) THEN
        DELETE FROM pn_portfolio_has_space
            WHERE space_id = v_project_id
            AND portfolio_id = (SELECT sp.portfolio_id FROM pn_space_has_portfolio sp, pn_portfolio pp
                                WHERE sp.space_id = v_current_business
				AND sp.portfolio_id = pp.portfolio_id
                                AND sp.is_default = 0
				AND pp.portfolio_name = 'owner');


    END IF;

    -- NOTE: ACTUALLY, Portfolios should no longer be used for OWNERSHIP, only
    --       space_has_space.  -Roger 7/7/01

    IF (v_business_space_id > 0) THEN
    -- clean up old portfolio list
        DELETE FROM  pn_portfolio_has_space
            WHERE space_id = v_project_id
            AND portfolio_id = (SELECT sp.portfolio_id FROM pn_space_has_portfolio sp, pn_portfolio pp
            			WHERE sp.space_id = v_business_space_id
				AND sp.portfolio_id = pp.portfolio_id
            			AND sp.is_default = 0
				AND pp.portfolio_name = 'owner');


    -- new value for business passed in
        INSERT INTO pn_portfolio_has_space
            (portfolio_id, space_id)
            SELECT sp.portfolio_id, v_project_id FROM pn_space_has_portfolio sp, pn_portfolio pp
            WHERE sp.space_id = v_business_space_id
	    AND sp.portfolio_id = pp.portfolio_id
            AND sp.is_default = 0
	    AND pp.portfolio_name = 'owner';

        -- error
        IF SQL%NOTFOUND THEN
            base.log_error(stored_proc_name,SQLCODE,SQLERRM);
            raise e_no_data;
        END IF;

    END IF;

    ----------------------------------------------------------------------------
    --- END Updating Business Owner
    ----------------------------------------------------------------------------

    project.get_property_group_info (v_project_id, v_property_group_id, v_property_sheet_type, v_property_table);

    update pn_project_space
    set
        project_desc = p_proj_desc,
        project_name = p_proj_name,
        status_code_id = v_status_id,
        color_code_id = v_color_code,
        percent_calculation_method = p_calculation_method,
        percent_complete = v_percent_complete,
        start_date = p_start_date,
        end_date = p_end_date,
        project_logo_id = v_project_logo_id,
        crc = SYSDATE,
        date_modified = SYSDATE,
        modified_by_id = v_whoami,
        -- Vishwajeet 01/03/03: We must preserve the record status as it is.
        --record_status = 'A',
        default_currency_code = p_default_currency_code,
        SPONSOR_DESC = p_sponsor,
        IMPROVEMENT_CODE_ID = p_improvement_code_id,
        CURRENT_STATUS_DESCRIPTION = p_current_status_description,
        financial_status_color_code_id = p_financial_stat_color_code_id,
        financial_status_imp_code_id = p_financial_stat_imp_code_id,
        budgeted_total_cost_value = p_budgeted_total_cost_value,
        budgeted_total_cost_cc = p_budgeted_total_cost_cc,
        current_est_total_cost_value = p_current_est_total_cost_value,
        current_est_total_cost_cc = p_current_est_total_cost_cc,
        actual_to_date_cost_value = p_actual_to_date_cost_value,
        actual_to_date_cost_cc = p_actual_to_date_cost_cc,
        estimated_roi_cost_value = p_estimated_roi_cost_value,
        estimated_roi_cost_cc = p_estimated_roi_cost_cc,
        cost_center = p_cost_center,
        schedule_status_color_code_id = p_schedule_stat_color_code_id,
        schedule_status_imp_code_id = p_schedule_stat_imp_code_id,
        resource_status_color_code_id = p_resource_stat_color_code_id,
        resource_status_imp_code_id = p_resource_stat_imp_code_id,
        priority_code_id = p_priority_code_id,
        risk_rating_code_id = p_risk_rating_code_id,
        visibility_id = p_visibility_id
    where
        project_id = v_project_id;

	/*  BFD 1064 */
	-- check whether the existing parent business is being removed,
	IF (v_current_business > 0 AND v_business_space_id is null ) THEN
	   -- remove the inherited business group by project space
	   SECURITY.remove_inherited_group(v_project_id, v_group_id);
	END IF;

   EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        base.log_error(stored_proc_name,SQLCODE,SQLERRM);
        raise;
      END;

END;


FUNCTION get_heartbeat_metrics
( project_id IN pn_project_space.project_id%type )
RETURN ReferenceCursor
AS

    -- Supported modules for heartbeats
    /*
        Documents
        Deliverables
        Milestones
        Team Roster
   */

    v_collection_id     pn_object.object_id%type;
    c_heartbeats ReferenceCursor;

BEGIN

    -- get a collection id
    select pn_object_sequence.nextval into v_collection_id from dual;

    -- get document heartbeats
    insert into pn_tmp_heartbeat_metrics
    (
        collection_id,
        module_name,
        total,
        total_open,
        total_closed,
        total_closed_last_week
     )
     values
     (
        v_collection_id,
        'document',
        DOCUMENT.GET_COUNT(project_id),
        DOCUMENT.GET_COUNT_OPEN(project_id),
        DOCUMENT.GET_COUNT_CLOSED(project_id),
        DOCUMENT.GET_COUNT_CLOSED_LAST_WEEK(project_id)
     );


    open c_heartbeats for
        select module_name, total, total_open, total_closed, total_closed_last_week
        from pn_tmp_heartbeat_metrics where collection_id = v_collection_id;

    return c_heartbeats;

END;

--------------------------------------------------------------------
-- ENCODE_SERIAL_NUMBER
--------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Robin       25-May-00  Creation.

FUNCTION ENCODE_SERIAL_NUMBER (i_serial_number in varchar2) return varchar2
AS
   prod_id number(6) := to_number(substr(i_serial_number, -6));
   out_id varchar2(14);
BEGIN
   out_id := to_char(prod_id + 5 );
   out_id := substr(i_serial_number, 1, 6) || substr(i_serial_number, 8, 1) || substr(i_serial_number, 7, 1) || substr('000000', 1, 5-length(out_id)) ||out_id;
   return out_id;
END;

--- Activate
PROCEDURE activate
(
    project_id IN VARCHAR2,
    whoami IN VARCHAR2
)

IS

    v_project_id        pn_project_space.project_id%type := to_number(project_id);
    v_whoami            pn_person.person_id%type := to_number(whoami);

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'PROJECT.ACTIVATE';


BEGIN



  UPDATE pn_object
    SET record_status = 'A'
    WHERE object_id = v_project_id;


  -- Activate the project_space
  UPDATE pn_project_space
    SET
        record_status = 'A',
        date_modified = SYSDATE,
        modified_by_id = v_whoami
    WHERE
        project_id = v_project_id;


     COMMIT;

   EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (SYSDATE,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;


END;



END; -- Package Body PROJECT
/

CREATE OR REPLACE PACKAGE project IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Robin       27-Apr-00  Creation.
-- Robin       11-May-00  Changed error codes to coincide with new table.
-- Brian       12-Jun-00  Added business space logic.

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;
e_no_data EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

TYPE ReferenceCursor            IS REF CURSOR;


Procedure invite_person_to_project
(
    i_project_id IN NUMBER,
    i_email IN VARCHAR2,
    i_firstname IN VARCHAR2,
    i_lastname IN VARCHAR2,
    i_responsibilities IN VARCHAR2,
    i_invitor_id IN NUMBER,
    o_invitation_code OUT NUMBER,
    o_status OUT NUMBER
);


Procedure respond_to_project_invite
(
    i_invitation_code IN NUMBER,
    i_project_id IN NUMBER,
    i_person_id IN NUMBER,
    i_response IN VARCHAR2,
    o_status OUT NUMBER
);

PROCEDURE get_property_group_info
(
    project_id IN varchar2,
    property_group_id OUT NUMBER,
    property_sheet_type OUT varchar2,
    property_table_name OUT varchar2
);

procedure add_logo_to_project
(
    project_id IN varchar2,
    logo_id IN varchar2
);

PROCEDURE create_project
(
    p_proj_creator IN VARCHAR2,
    p_subproject_of IN VARCHAR2,
    p_business_space_id IN VARCHAR2,
    p_project_visibility IN VARCHAR2,
    p_proj_name IN VARCHAR2,
    p_proj_desc IN VARCHAR2,
    p_proj_status IN VARCHAR2,
    p_proj_color_code IN VARCHAR2,
    p_calculation_method in varchar2,
    p_percent_complete IN varchar2,
    p_start_date IN date,
    p_end_date IN date,
    p_serial IN varchar2,
    p_project_logo_id IN varchar2,
    p_default_currency_code in varchar2,
    p_sponsor in varchar2,
    p_improvement_code_id in number,
    p_current_status_description in varchar2,
    p_financial_stat_color_code_id in number,
    p_financial_stat_imp_code_id in number,
    p_budgeted_total_cost_value in number,
    p_budgeted_total_cost_cc in varchar2,
    p_current_est_total_cost_value in number,
    p_current_est_total_cost_cc in varchar2,
    p_actual_to_date_cost_value in number,
    p_actual_to_date_cost_cc in varchar2,
    p_estimated_roi_cost_value in number,
    p_estimated_roi_cost_cc in varchar2,
    p_cost_center in varchar2,
    p_schedule_stat_color_code_id in number,
    p_schedule_stat_imp_code_id in number,
    p_resource_stat_color_code_id in number,
    p_resource_stat_imp_code_id in number,
    p_priority_code_id in number,
    p_risk_rating_code_id in number,
    p_visibility_id in number,
    p_autocalc_schedule in number,
    p_plan_name in varchar2,
    p_create_share in number,
    o_project_id OUT NUMBER,
    o_workplan_id OUT NUMBER,
    o_space_admin_role_id OUT NUMBER
);

PROCEDURE update_properties
(
    p_project_id IN VARCHAR2,
    whoami IN varchar2,
    p_business_space_id IN VARCHAR2,
    p_project_visibility IN VARCHAR2,
    p_proj_name IN VARCHAR2,
    p_proj_desc IN VARCHAR2,
    p_proj_status IN VARCHAR2,
    p_proj_color_code IN VARCHAR2,
     p_calculation_method in varchar2,
    p_percent_complete IN varchar2,
    p_start_date IN date,
    p_end_date IN date,
    p_project_logo_id IN varchar2,
    p_default_currency_code in varchar2,
    p_sponsor in varchar2,
    p_improvement_code_id in number,
    p_current_status_description in varchar2,
    p_financial_stat_color_code_id in number,
    p_financial_stat_imp_code_id in number,
    p_budgeted_total_cost_value in number,
    p_budgeted_total_cost_cc in varchar2,
    p_current_est_total_cost_value in number,
    p_current_est_total_cost_cc in varchar2,
    p_actual_to_date_cost_value in number,
    p_actual_to_date_cost_cc in varchar2,
    p_estimated_roi_cost_value in number,
    p_estimated_roi_cost_cc in varchar2,
    p_cost_center in varchar2,
    p_schedule_stat_color_code_id in number,
    p_schedule_stat_imp_code_id in number,
    p_resource_stat_color_code_id in number,
    p_resource_stat_imp_code_id in number,
    p_priority_code_id in number,
    p_risk_rating_code_id in number,
    p_visibility_id in number,
    i_serial_number IN varchar2
)
;

PROCEDURE remove
(
    project_id IN VARCHAR2,
    whoami IN VARCHAR2
);

FUNCTION get_heartbeat_metrics
(
    project_id in pn_project_space.project_id%type
) RETURN ReferenceCursor;

FUNCTION ENCODE_SERIAL_NUMBER
(
    i_serial_number in varchar2
) return varchar2;

PROCEDURE activate
(
    project_id IN VARCHAR2,
    whoami IN VARCHAR2
);


END; -- Package Specification PROJECT
/

CREATE OR REPLACE PACKAGE BODY SCHEDULE IS
-- Purpose: Provides procedures for creating and modifying tasks and
--          assignments
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Tim         07/22/01 Added "add_comment" procedure

Procedure STORE_TASK
(
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_plan_id IN NUMBER,
    i_task_id IN NUMBER,
    i_task_name IN VARCHAR2,
    i_description IN VARCHAR2,
    i_task_type IN VARCHAR2,
    i_priority IN NUMBER,
    i_duration IN NUMBER,
    i_duration_units IN NUMBER,
    i_work IN NUMBER,
    i_work_units IN NUMBER,
    i_work_complete IN NUMBER,
    i_work_complete_units IN NUMBER,
    i_work_ms IN NUMBER,
    i_work_complete_ms IN NUMBER,
    i_work_percent_complete IN NUMBER,
    i_percent_complete IN NUMBER,
    i_date_finish IN DATE,
    i_date_start IN DATE,
    i_actual_start IN DATE,
    i_actual_finish IN DATE,
    i_critical_path IN NUMBER,
    i_record_status IN CHAR,
    i_parent_task_id IN NUMBER,
    i_ignore_times IN NUMBER,
    i_early_start IN DATE,
    i_early_finish IN DATE,
    i_late_start IN DATE,
    i_late_finish IN DATE,
    i_milestone IN NUMBER,
    i_constraint_type IN NUMBER,
    i_constraint_date IN DATE,
    i_deadline IN DATE,
    i_phase_id IN NUMBER,
    i_unallocated_wk_complete IN NUMBER,
    i_unallocated_wk_complete_unit IN NUMBER,
    i_unassigned_work IN NUMBER,
    i_unassigned_work_units IN NUMBER,
    i_calculation_type_id in NUMBER,
    o_sequence_number IN OUT NUMBER,
    o_task_id OUT NUMBER
)

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Adam        01-Mar-00  Creation.
-- Robin       06-Apr-00  Added space_id and changed order of proc.
-- Robin       11-May-00  Changed error codes to coincide with new table.

IS

v_task_id NUMBER(20);
v_task_version_id NUMBER(20);

v_date_start DATE;
v_date_finish DATE;
v_critical_path PN_TASK.CRITICAL_PATH%TYPE;
v_work PN_TASK.WORK%TYPE;
v_work_units PN_TASK.WORK_UNITS%TYPE;
v_work_complete PN_TASK.WORK_COMPLETE%TYPE;
v_work_complete_units PN_TASK.WORK_COMPLETE_UNITS%TYPE;
v_duration PN_TASK.DURATION%TYPE;
v_duration_units PN_TASK.DURATION_UNITS%TYPE;
v_priority PN_TASK.PRIORITY%TYPE;
v_parent_task_id PN_TASK.PARENT_TASK_ID%TYPE;
v_prev_seq_number PN_TASK.SEQ%TYPE;
v_prev_parent_id PN_TASK.PARENT_TASK_ID%TYPE;
v_sibling_count NUMBER;
v_autocalculate_schedule NUMBER;
v_schedule_start DATE;
v_schedule_end DATE;
v_move_to_sequence_number NUMBER;
v_tasks_in_tree NUMBER;

v_actual_start DATE;
v_actual_finish DATE;

v_propagate NUMBER;

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100):= 'STORE_TASK';

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);


BEGIN
    -- Adjust actual start and actual finish as necessary
    if ((i_actual_start is null) and (i_work_percent_complete > 0)) then
        v_actual_start := sysdate;
    else
        v_actual_start := i_actual_start;
    end if;

    if ((i_actual_finish is null) and (i_work_percent_complete >= 100)) then
        v_actual_finish := sysdate;
    else
        v_actual_finish := i_actual_finish;
    end if;

    -- NEW TASK, INSERT
    IF ((i_task_id IS NULL) OR (i_task_id = ''))    THEN
        v_task_id := BASE.CREATE_OBJECT('task', i_person_id, i_record_status);
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_task_id, 'task', i_space_id, i_person_id);

        v_move_to_sequence_number := o_sequence_number;
        SELECT nvl(max(seq),0)+1 into o_sequence_number
        from
          pn_task t,
          pn_plan_has_task pht
        where
          t.task_id = pht.task_id
          and pht.plan_id = i_plan_id
          and t.record_status = 'A';

        INSERT INTO pn_task (
             task_id, task_name, task_desc, task_type, duration, duration_units, work, work_units, work_complete, date_start,
             work_complete_units, work_ms, work_complete_ms, date_finish, actual_start, actual_finish, priority, work_percent_complete, percent_complete, critical_path,
             date_modified, modified_by, record_status, parent_task_id, seq, ignore_times_for_dates, early_start,
             early_finish, late_start, late_finish, is_milestone, constraint_type, constraint_date, deadline,
             calculation_type_id, unallocated_work_complete, unallocated_work_complete_unit, unassigned_work, unassigned_work_units
        ) VALUES (
             v_task_id, i_task_name, i_description, i_task_type, i_duration, i_duration_units, i_work, i_work_units, i_work_complete,
             i_date_start, i_work_complete_units, i_work_ms, i_work_complete_ms, i_date_finish, v_actual_start, v_actual_finish, i_priority,
             i_work_percent_complete, i_percent_complete, i_critical_path, SYSDATE, i_person_id, i_record_status, i_parent_task_id,
             o_sequence_number, i_ignore_times, i_early_start, i_early_finish, i_late_start, i_late_finish, i_milestone,
             i_constraint_type, i_constraint_date, i_deadline, i_calculation_type_id, i_unallocated_wk_complete, i_unallocated_wk_complete_unit,
             i_unassigned_work, i_unassigned_work_units
        );

        -- add the task to the plan
        INSERT INTO pn_plan_has_task
            (plan_id, task_id)
            VALUES
            (i_plan_id, v_task_id);

        o_task_id := v_task_id;

        -- By default, the task is not shareable.
        v_propagate := SHARING.GET_PROPAGATE_TO_CHILDREN(i_plan_id);
        SHARING.STORE_SHARE(o_task_id, 0, i_plan_id, i_space_id, v_propagate, 1);

        -- If this is a task whose plan says to always share a task, make sure that happens
        -- SHARING.INHERIT_FROM_PARENT(i_plan_id, o_task_id);

        -- Store this version in the history of the task
        STORE_TASK_VERSION(o_task_id, v_task_version_id);

        if (v_move_to_sequence_number is not null and v_move_to_sequence_number <> 0) then
            MOVE_TASK_TO_SEQUENCE_NUMBER(i_plan_id, v_task_id, v_move_to_sequence_number);

            --Assign variable to out variable so caller can learn new sequence #
            o_sequence_number := v_move_to_sequence_number;
        elsif (i_parent_task_id is not null) then
            select     max(seq)+1 into o_sequence_number
            from       pn_task t
            where      t.record_status = 'A'
              and      t.task_id <> v_task_id
            start with t.task_id = i_parent_task_id
            connect by t.parent_task_id = prior t.task_id
            order by   t.date_start, t.rowid;

            MOVE_TASK_TO_SEQUENCE_NUMBER(i_plan_id, v_task_id, o_sequence_number);

            --Now makes sure the parent task knows that it is a summary task
            update pn_task t
            set task_type = 'summary'
            where t.task_id = i_parent_task_id;
        end if;


    -- EXISTING TASK, UPDATE
    ELSE
        o_task_id := i_task_id;

        -- Collect old sequence number and parent task id info just in case we
        -- need to change the sequence number
        select parent_task_id, seq into v_prev_parent_id, v_prev_seq_number
        from pn_task
        where task_id = i_task_id;

        --Figure out how many tasks (including the task itself) are in the
        --tree of tasks that has the task we are saving as its head
        select count(*) into v_tasks_in_tree
        from pn_task t
        where t.record_status = 'A'
        connect by t.parent_task_id = prior t.task_id
        start with t.task_id = i_task_id;

        --If the parent has changed, the sequence number must too
        if (((v_prev_parent_id is null) and (i_parent_task_id is not null)) or
            ((v_prev_parent_id is not null) and (i_parent_task_id is not null) and (v_prev_parent_id <> i_parent_task_id))) then

            --The task didn't used to have a parent, but now it does.
            select     nvl(max(seq),0)+1 into o_sequence_number
            from       pn_task t
            where      t.record_status = 'A'
              and      t.task_id <> i_task_id
            start with t.task_id = i_parent_task_id
            connect by t.parent_task_id = prior t.task_id
            order by   t.date_start, t.rowid;

            -- If the old sequence number was in the middle of this tree, we need
            -- to account for its placement.
            if (o_sequence_number > v_prev_seq_number) then
                o_sequence_number := o_sequence_number - v_tasks_in_tree;
            end if;

            MOVE_TASK_TO_SEQUENCE_NUMBER(i_plan_id, i_task_id, o_sequence_number);

        elsif ((v_prev_parent_id is not null) and (i_parent_task_id is null)) then
            --The task used to have a parent, now it doesn't.
            select     max(seq)+1 into o_sequence_number
            from       pn_task t
            where      t.record_status = 'A'
              and      t.task_id <> i_task_id
            start with t.task_id = v_prev_parent_id
            connect by t.parent_task_id = prior t.task_id
            order by   t.date_start, t.rowid;

            -- If the old sequence number was in the middle of this tree, we need
            -- to account for its placement.
            if (o_sequence_number > v_prev_seq_number) then
                o_sequence_number := o_sequence_number - v_tasks_in_tree;
            end if;

            MOVE_TASK_TO_SEQUENCE_NUMBER(i_plan_id, i_task_id, o_sequence_number);
        else
            o_sequence_number := v_prev_seq_number;
        end if;

        -- get the task date info. Determine there is baseline change.
        SELECT
            date_start, date_finish, priority, parent_task_id, critical_path,
            work, work_units, work_complete, work_complete_units, duration, duration_units
        INTO
            v_date_start, v_date_finish, v_priority, v_parent_task_id,
            v_critical_path, v_work, v_work_units, v_work_complete, v_work_complete_units,
            v_duration, v_duration_units
        FROM
            pn_task
        WHERE
        	task_id = i_task_id;

        -- Perform the actual task update
        UPDATE
    	    pn_task
        SET
        	task_id = i_task_id,
            task_name = i_task_name,
            task_desc = i_description,
            task_type = i_task_type,
            duration = i_duration,
            duration_units = i_duration_units,
            work_ms = i_work_ms,
            work_complete_ms = i_work_complete_ms,
            work = i_work,
            work_units = i_work_units,
            work_complete = i_work_complete,
            date_start = i_date_start,
            work_complete_units = i_work_complete_units,
            date_finish = i_date_finish,
            actual_start = v_actual_start,
            actual_finish = v_actual_finish,
            priority = i_priority,
            percent_complete = i_percent_complete,
            work_percent_complete = i_work_percent_complete,
            date_modified = SYSDATE,
            modified_by = i_person_id,
            record_status = i_record_status,
            parent_task_id = i_parent_task_id,
            critical_path = i_critical_path,
            ignore_times_for_dates = i_ignore_times,
            early_start = i_early_start,
            early_finish = i_early_finish,
            late_start = i_late_start,
            late_finish = i_late_finish,
            is_milestone = i_milestone,
            constraint_type = i_constraint_type,
            constraint_date = i_constraint_date,
            deadline = i_deadline,
            calculation_type_id = i_calculation_type_id,
            unallocated_work_complete = i_unallocated_wk_complete,
            unallocated_work_complete_unit = i_unallocated_wk_complete_unit,
            unassigned_work = i_unassigned_work,
            unassigned_work_units = i_unassigned_work_units
        WHERE
        	task_id = i_task_id;

        IF ((NOT UTIL.COMPARE_DATES(i_date_start, v_date_start)) OR
            (NOT UTIL.COMPARE_DATES(i_date_finish, v_date_finish)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_critical_path, v_critical_path)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_work, v_work)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_work_units, v_work_units)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_work_complete, v_work_complete)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_work_complete_units, v_work_complete_units)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_duration, v_duration)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_duration_units, v_duration_units)) OR
            (NOT UTIL.COMPARE_NUMBERS(i_parent_task_id, v_parent_task_id))
            )
        THEN
            -- Store this version in the history of the task
            STORE_TASK_VERSION(i_task_id, v_task_version_id);
        END IF;

        --If there was an old parent_task id that is no longer the parent, make
        --sure its task type is correct.
        if ((i_parent_task_id is null and v_parent_task_id is not null) or
            (i_parent_task_id <> v_parent_task_id))then

            select
                count(*) into v_sibling_count
            from
                pn_task t
            where
                t.record_status = 'A'
                and t.parent_task_id = v_parent_task_id;

            if (v_sibling_count = 0) then
                update pn_task
                set task_type = 'task', work = 1, work_units = 8
                where task_id = v_parent_task_id;
            end if;
        end if;

        if (i_parent_task_id is not null) then
            update pn_task
            set task_type = 'summary'
            where task_id = i_parent_task_id;
        end if;
    END IF; -- insert/update

    -- If there is a phase specified, store it
    delete from pn_phase_has_task
    where
      task_id = o_task_id;

    if (i_phase_id is not null) then
        insert into pn_phase_has_task
          (phase_id, task_id)
        values
          (i_phase_id, o_task_id);
    end if;

    --If the schedule for this task does not use autocalculation, update the
    --start and end date based on the tasks that are already there
    select date_start, date_end, autocalculate_task_endpoints into v_schedule_start,
           v_schedule_end, v_autocalculate_schedule
    from pn_plan
    where plan_id = i_plan_id;

    if (v_autocalculate_schedule = 0) then
        if ((i_date_start < v_schedule_start) or (v_schedule_start is null)) then
            update pn_plan
            set date_start = i_date_start
            where plan_id = i_plan_id;
        end if;

        if ((i_date_finish > v_schedule_end) or (v_schedule_end is null)) then
            update pn_plan
            set date_end = i_date_finish
            where plan_id = i_plan_id;
        end if;
    end if;
END store_task;

procedure STORE_PLAN
(
    i_name IN VARCHAR2,
    i_description IN VARCHAR2,
    i_start_date IN DATE,
    i_end_date IN DATE,
    i_autocalculate_task_endpoints IN NUMBER,
    i_default_calendar_id IN NUMBER,
    i_timezone_id IN VARCHAR2,
    i_modified_by IN NUMBER,
    i_space_id IN NUMBER,
    i_default_task_calc_type_id IN NUMBER,
    i_earliest_start_date IN DATE,
    i_earliest_finish_date IN DATE,
    i_latest_start_date IN DATE,
    i_latest_finish_date IN DATE,
    i_start_constraint_type VARCHAR2,
    i_start_constraint_date DATE,
    io_plan_id IN OUT NUMBER
)
is
    v_plan_version_id NUMBER;
    v_baseline_exists NUMBER;

    v_current_baseline_id NUMBER;
    v_baseline_start DATE;
    v_baseline_end DATE;
    v_default_task_calc_type_id NUMBER := i_default_task_calc_type_id;

    --date value to replace date
    v_compare BOOLEAN;
    v_old_date_start DATE;
    v_end_date_changed BOOLEAN;
    v_old_date_end DATE;
    v_old_autocalc_task_endpoints NUMBER;
    v_old_timezone_id VARCHAR(2000);
    v_old_baseline_start DATE;
    v_old_baseline_end DATE;
    v_old_def_task_calc_type_id NUMBER;
begin
    -- If no task calculation type was specified (expected when creating plans)
    -- Use the default
    if (v_default_task_calc_type_id is null) then
        v_default_task_calc_type_id := DEFAULT_TASK_CALC_TYPE_ID;
    end if;

    if (io_plan_id is null) then
        --Plan is new, save it
        io_plan_id := BASE.CREATE_OBJECT('plan', 1, 'A');

        insert into pn_plan
          (plan_id, plan_name, plan_desc, date_start, date_end,
           autocalculate_task_endpoints, default_calendar_id, timezone_id,
           baseline_start, baseline_end, modified_by, modified_date, default_task_calc_type_id,
           earliest_start_date, earliest_finish_date, latest_start_date, latest_finish_date,
           constraint_type_id, constraint_date)
        values
          (io_plan_id, i_name, i_description, i_start_date, i_end_date,
           i_autocalculate_task_endpoints, i_default_calendar_id, i_timezone_id,
           null, null, i_modified_by, SYSDATE, v_default_task_calc_type_id,
           i_earliest_start_date, i_earliest_finish_date, i_latest_start_date,
           i_latest_finish_date, i_start_constraint_type, i_start_constraint_date);

        insert into pn_space_has_plan
          (space_id, plan_id)
        values
          (i_space_id, io_plan_id);

        --We always store the initial version
        STORE_PLAN_VERSION(io_plan_id, v_plan_version_id);
    else
        --Get the current default baseline
        begin
            select b.baseline_id, pv.date_start, pv.date_end
            into v_current_baseline_id, v_baseline_start, v_baseline_end
            from pn_baseline b, pn_baseline_has_plan bhp, pn_plan_version pv
            where
              b.object_id = io_plan_id
              and b.is_default_for_object = 1
              and b.baseline_id = bhp.baseline_id
              and bhp.plan_version_id = pv.plan_version_id;
        exception
            when no_data_found then
                v_current_baseline_id := null;
        end;

        --Grab information about the schedule before the update
        select
          date_start, date_end, autocalculate_task_endpoints, timezone_id,
          baseline_start, baseline_end, default_task_calc_type_id
        into
          v_old_date_start, v_old_date_end, v_old_autocalc_task_endpoints,
          v_old_timezone_id, v_old_baseline_start, v_old_baseline_end, v_old_def_task_calc_type_id
        from
          pn_plan
        where
          plan_id = io_plan_id;

        --Do the actual update of the plan
        update
          pn_plan
        set
          plan_name = i_name,
          plan_desc = i_description,
          date_start = i_start_date,
          date_end = i_end_date,
          autocalculate_task_endpoints = i_autocalculate_task_endpoints,
          default_calendar_id = i_default_calendar_id,
          timezone_id = i_timezone_id,
          baseline_start = v_baseline_start,
          baseline_end = v_baseline_end,
          modified_by = i_modified_by,
          modified_date = SYSDATE,
          baseline_id = v_current_baseline_id,
          default_task_calc_type_id = v_default_task_calc_type_id,
          earliest_start_date = i_earliest_start_date,
          earliest_finish_date = i_earliest_finish_date,
          latest_start_date = i_latest_start_date,
          latest_finish_date = i_latest_finish_date,
          constraint_type_id = i_start_constraint_type,
          constraint_date = i_start_constraint_date
        where
          plan_id = io_plan_id;

        --Store a plan version if dates or other properties changed
        if ((NOT UTIL.COMPARE_DATES(v_old_date_start, i_start_date)) OR
            (NOT UTIL.COMPARE_DATES(v_old_date_end, i_end_date)) OR
            (v_old_autocalc_task_endpoints <> i_autocalculate_task_endpoints) OR
            (NOT UTIL.COMPARE_STRINGS(v_old_timezone_id, i_timezone_id)) OR
            (NOT UTIL.COMPARE_DATES(v_old_baseline_start, v_baseline_start)) OR
            (NOT UTIL.COMPARE_DATES(v_old_baseline_end, v_baseline_end)) OR
            (v_old_def_task_calc_type_id <> v_default_task_calc_type_id)) then
            STORE_PLAN_VERSION(io_plan_id, v_plan_version_id);
        end if;
    end if;
end STORE_PLAN;

Procedure STORE_ASSIGNMENT
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_object_id IN NUMBER,
    i_percent IN NUMBER,
    i_role IN VARCHAR2,
    i_status_id IN NUMBER,
    i_primary_owner IN NUMBER,
    i_start_date in date,
    i_end_date in date,
    i_actual_start in date,
    i_actual_finish in date,
    i_estimated_finish in date,
    i_work in NUMBER,
    i_work_units in NUMBER,
    i_work_complete in NUMBER,
    i_work_complete_units in NUMBER,
    i_is_complete in NUMBER,
    i_percent_complete in NUMBER,
    i_modified_by in NUMBER,
    i_record_status IN VARCHAR2,
    o_status OUT NUMBER
)
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Adam        01-Mar-00  Creation.
-- Robin       06-Apr-00  Added space_id and changed order of proc.
-- Robin       11-May-00  Changed error codes to coincide with new table.
-- Tim         10-Jul-01  Added due_datetime
IS

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100) := 'STORE_ASSIGNMENT';

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

BEGIN

    -- if this assignment is set as a primary owner then update an existing primary owner
    IF (i_primary_owner = 1) THEN
        UPDATE pn_assignment
        SET is_primary_owner = 0
        WHERE space_id = i_space_id and
            person_id = i_person_id and
            object_id = i_object_id and
            i_primary_owner != 0;
    END IF;

    BEGIN

        INSERT INTO pn_assignment
            (space_id, person_id, object_id, status_id, percent_allocated, role,
             is_primary_owner, start_date, end_date, actual_start, actual_finish,
             estimated_finish, work, work_units, work_complete,
             work_complete_units, is_complete, percent_complete, modified_by,
             modified_date, record_status, date_created)
          VALUES
            (i_space_id, i_person_id, i_object_id, i_status_id, i_percent, i_role,
            i_primary_owner, i_start_date, i_end_date, i_actual_start,
            i_actual_finish, i_estimated_finish, i_work, i_work_units,
            i_work_complete, i_work_complete_units, i_is_complete, i_percent_complete,
            i_modified_by, SYSDATE, i_record_status, SYSDATE);


    EXCEPTION
    -- assignment already exists
        WHEN e_unique_constraint THEN

            UPDATE pn_assignment
              SET status_id = i_status_id,
                  percent_allocated = i_percent,
                  role = i_role,
                  is_primary_owner = i_primary_owner,
                  start_date = i_start_date,
                  end_date = i_end_date,
                  actual_start = i_actual_start,
                  actual_finish = i_actual_finish,
                  estimated_finish = i_estimated_finish,
                  work = i_work,
                  work_units = i_work_units,
                  work_complete = i_work_complete,
                  work_complete_units = i_work_complete_units,
                  is_complete = i_is_complete,
                  percent_complete = i_percent_complete,
                  modified_by = i_modified_by,
                  modified_date = SYSDATE,
                  record_status = i_record_status
              WHERE space_id = i_space_id
                AND person_id = i_person_id
                AND object_id = i_object_id;

    END;

    COMMIT;
    o_status := success;
END;

/**
 * Adds a comment to a task
 */
procedure add_comment (
    i_task_id   in  number,
    i_created_by_id in number,
    i_created_datetime  in date,
    i_is_comment_null in number,
    o_comment_clob   out clob
)
is

    v_seq               number;
    v_task_baseline_id  number;

begin
    -- Get unique seq number for this comment
    SELECT pn_object_sequence.nextval into v_seq FROM dual;

    -- Fetch the latest task baseline id
    select
        max(tv.task_version_id) into v_task_baseline_id
    from
        pn_task_version tv
    where
        tv.task_id = i_task_id;

    -- Add comment to task and basline
    if (i_is_comment_null = 1) then
        insert into pn_task_comment
            (task_id, baseline_id, seq, created_by_id, created_datetime, text_clob)
        values
            (i_task_id, v_task_baseline_id, v_seq, i_created_by_id, i_created_datetime, null)
        returning
            text_clob into o_comment_clob;
    else
        insert into pn_task_comment
            (task_id, baseline_id, seq, created_by_id, created_datetime, text_clob)
        values
            (i_task_id, v_task_baseline_id, v_seq, i_created_by_id, i_created_datetime, empty_clob())
        returning
            text_clob into o_comment_clob;
    end if;

end add_comment;

procedure MOVE_TASK_DOWN (
    i_task_id in number,
    i_plan_id in number
)
is
    current_seq number;
    parent_id number;
    highest_seq_in_group number;
    exchange_task_id number;

    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.DEMOTE_TASK';
begin
    --Get some local variables
    select seq, parent_task_id into current_seq, parent_id
    from pn_task t
    where t.task_id = i_task_id;

    --Make sure that the task isn't at the bottom of the list.  If it is, it
    --cannot be demoted.
    if (parent_id is not null) then
        select max(t.seq) into highest_seq_in_group
        from pn_task t, pn_plan_has_task pht
        where t.task_id = pht.task_id
        and t.parent_task_id = parent_id
        and pht.plan_id = i_plan_id
        and t.record_status = 'A';
    else
        select max(t.seq) into highest_seq_in_group
        from pn_task t, pn_plan_has_task pht
        where t.task_id = pht.task_id
        and t.parent_task_id is null
        and pht.plan_id = i_plan_id
        and t.record_status = 'A';
    end if;

    if (highest_seq_in_group = current_seq) then
        return;
    end if;

    --Figure out which sequence number we are exchanging with
    if (parent_id is not null) then
        select task_id into exchange_task_id
        from pn_task t
        where t.parent_task_id = parent_id
        and t.record_status = 'A'
        and seq =
            (select min(t2.seq)
             from pn_task t2
             where t2.parent_task_id = parent_id
               and t2.record_status = 'A'
               and t2.seq > current_seq);
    else
        select t.task_id into exchange_task_id
        from pn_task t, pn_plan_has_task pht
        where t.parent_task_id is null
        and t.task_id = pht.task_id
        and pht.plan_id = i_plan_id
        and t.record_status = 'A'
        and seq =
            (select min(t2.seq)
             from pn_task t2, pn_plan_has_task pht
             where t2.parent_task_id is null
               and t2.task_id = pht.task_id
               and pht.plan_id = i_plan_id
               and t2.record_status = 'A'
               and t2.seq > current_seq);
    end if;

    -- We just use "MOVE_TASK_UP" to move up the task beneath us
    MOVE_TASK_UP(exchange_task_id, i_plan_id);
exception
    when no_data_found then
        return;
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end MOVE_TASK_DOWN;

procedure MOVE_TASK_UP (
    i_task_id in number,
    i_plan_id in number
)
is
    lowest_seq_in_group number;
    current_seq number;
    new_seq number;
    parent_id number;
    exchange_task_id number;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.MOVE_TASK_UP';

    --The tree of tasks starting with the task we are moving
    cursor tasks_to_move_up(i_head_id number) is
        select task_id
        from pn_task t
        where t.record_status = 'A'
        connect by t.parent_task_id = prior t.task_id
        start with t.task_id = i_head_id;
    move_up_row tasks_to_move_up%rowtype;

    --The tree of tasks we are exchanging our task with
    cursor tasks_to_move_down(i_head_id number) is
        select task_id
        from pn_task t
        where t.record_status = 'A'
        connect by t.parent_task_id = prior t.task_id
        start with t.task_id = i_head_id;
    move_down_row tasks_to_move_down%rowtype;
begin
    -- Get local variables we are going to need
    select t.seq, t.parent_task_id into current_seq, parent_id
    from pn_task t, pn_plan_has_task pht
    where pht.task_id = t.task_id and t.task_id = i_task_id;

    --If the task is at the top of the list, we cannot promote it
    if (parent_id is not null) then
        select min(t.seq) into lowest_seq_in_group
        from pn_task t, pn_plan_has_task pht
        where t.task_id = pht.task_id
        and pht.plan_id = i_plan_id
        and t.parent_task_id = parent_id
        and t.record_status = 'A';
    else
        select min(t.seq) into lowest_seq_in_group
        from pn_task t, pn_plan_has_task pht
        where t.task_id = pht.task_id
        and pht.plan_id = i_plan_id
        and t.parent_task_id is null
        and t.record_status = 'A';
    end if;

    if (current_seq = lowest_seq_in_group) then
        return;
    end if;

    --Figure out which sequence number we are exchanging with
    if (parent_id is not null) then
        select seq, task_id into new_seq, exchange_task_id
        from pn_task t
        where t.parent_task_id = parent_id
        and t.record_status = 'A'
        and seq =
            (select max(t2.seq)
             from pn_task t2
             where t2.parent_task_id = parent_id
               and t2.record_status = 'A'
               and t2.seq < current_seq);
    else
        select seq, t.task_id into new_seq, exchange_task_id
        from pn_task t, pn_plan_has_task pht
        where t.parent_task_id is null
        and t.task_id = pht.task_id
        and pht.plan_id = i_plan_id
        and t.record_status = 'A'
        and seq =
            (select max(t2.seq)
             from pn_task t2, pn_plan_has_task pht
             where t2.parent_task_id is null
               and t2.task_id = pht.task_id
               and pht.plan_id = i_plan_id
               and t2.record_status = 'A'
               and t2.seq < current_seq);
    end if;

    --It is important to note that "moving up" a task doesn't always imply that
    --we are only exchanging two tasks.  If the one we are moving up has
    --children, we need to move them too.

    --Get the tasks we are moving up
    open tasks_to_move_up(i_task_id);

    --Get the tasks we are demoting
    open tasks_to_move_down(exchange_task_id);

    --Move up the tasks
    loop
        fetch tasks_to_move_up into move_up_row;
        exit when tasks_to_move_up%NOTFOUND;

        update pn_task set seq = new_seq where task_id = move_up_row.task_id;
        new_seq := new_seq +1;
    end loop;
    close tasks_to_move_up;

    --Move down the tasks we are exchanging with
    loop
        fetch tasks_to_move_down into move_down_row;
        exit when tasks_to_move_down%NOTFOUND;

        update pn_task set seq = new_seq where task_id = move_down_row.task_id;
        new_seq := new_seq +1;
    end loop;
    close tasks_to_move_down;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end MOVE_TASK_UP;

procedure RECALCULATE_SEQUENCE_NUMBERS (
    i_plan_id in number
)
is
    new_seq number := 1;
    lowest_seq_in_group number;
    cursor task_rows is
        select t.task_id
        from pn_task t, pn_plan_has_task pht
        where t.task_id = pht.task_id
          and pht.plan_id = i_plan_id
          and t.record_status = 'A'
        order by
          t.seq;
    task_row task_rows%ROWTYPE;

    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.RECALCULATE_SEQUENCE_NUMBERS';
begin
    for task_row in task_rows loop
        update pn_task
        set seq = new_seq
        where task_id = task_row.task_id;

        new_seq := new_seq + 1;
    end loop;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
END RECALCULATE_SEQUENCE_NUMBERS;

--Warning, this task can currently only move a single task.  It is not yet
--appropriate for a summary task.
procedure MOVE_TASK_TO_SEQUENCE_NUMBER (
    i_plan_id number,
    i_task_id number,
    i_new_seq number
)
is
    current_seq number;
    new_seq number;

    -- The number of tasks we are going to move
    num_tasks_to_move number;

    -- The actual tasks we are going to move
    cursor tasks_to_move(i_head_id number) is
    select task_id
    from pn_task t
    where t.record_status = 'A'
    connect by t.parent_task_id = prior t.task_id
    start with t.task_id = i_head_id;
    move_row tasks_to_move%rowtype;

    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.MOVE_TASK_TO_SEQUENCE_NUMBER';
begin
    --Determine what the current sequence number is so we can know if we are
    --moving up or down.
    select seq into current_seq from pn_task where task_id = i_task_id;

    if (current_seq = i_new_seq) then
        return;
    end if;

    --Get the size of the current "task tree"
    select count(*) into num_tasks_to_move
    from pn_task t
    where t.record_status = 'A'
    connect by t.parent_task_id = prior t.task_id
    start with t.task_id = i_task_id;

    if (i_new_seq < current_seq) then
        --We are moving the task upwards
        update pn_task
        set seq = seq + num_tasks_to_move
        where task_id in (
            select t.task_id
            from pn_task t, pn_plan_has_task pht
            where
                t.task_id = pht.task_id
                and pht.plan_id = i_plan_id
                and t.seq >= i_new_seq
                and t.seq < current_seq
                and t.record_status = 'A'
        );

        --Let the code that is going to move the tasks where the tasks are going
        new_seq := i_new_seq;
    else
        --We are moving the task downward in the list
        update pn_task
        set seq = seq - num_tasks_to_move
        where task_id in (
            select t.task_id
            from pn_task t, pn_plan_has_task pht
            where
                t.task_id = pht.task_id
                and pht.plan_id = i_plan_id
                and seq > current_seq + num_tasks_to_move -1
                and seq <= i_new_seq + num_tasks_to_move -1
                and t.record_status = 'A'
        );

        --We have to adjust the target sequence number because we just moved
        --them all.
        new_seq := i_new_seq;
    end if;

    --Get the tasks we are demoting
    open tasks_to_move(i_task_id);

    --Move down the tasks we are exchanging with
    loop
        fetch tasks_to_move into move_row;
        exit when tasks_to_move%NOTFOUND;

        update pn_task set seq = new_seq where task_id = move_row.task_id;
        new_seq := new_seq +1;
    end loop;
    close tasks_to_move;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end MOVE_TASK_TO_SEQUENCE_NUMBER;

-- sjmittal: calculation_type_id is handled in java so removing it from here
procedure FIX_SUMMARY_TASK_TYPES(
    i_plan_id in number
) is
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.FIX_SUMMARY_TASK_TYPES';
begin
    update pn_task t2
    set task_type = 'task'
    where exists
      (select 1
         from pn_plan_has_task pht, pn_task t
        where pht.task_id = t.task_id
          and pht.plan_id = i_plan_id
          and t.task_type='summary'
          and t2.task_id = t.task_id
          and t.record_status = 'A');

    update pn_task t
    set t.task_type = 'summary'
    where exists
      (select 1
         from pn_plan_has_task pht, pn_task t2
        where pht.plan_id = i_plan_id
          and pht.task_id = t2.task_id
          and t2.record_status = 'A'
          and t2.parent_task_id = t.task_id);
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end FIX_SUMMARY_TASK_TYPES;

--This method finds the new parent task id if the task is intended underneath
--the i_task_above_id variable.  It will return that parent_task_id.
function FIND_NEW_TASK_PARENT(
    i_task_id in number,
    i_task_above_id in number
)
    return NUMBER
is
    -- Mechanism for finding the proper parent
    cursor parent_finder (
        task_above_id in number,
        existing_parent in number
    ) is
        select task_id
        from pn_task t
        start with t.task_id = task_above_id
        connect by t.task_id = prior t.parent_task_id
          and t.task_id != existing_parent
        order by level;

    parent_id_rec parent_finder%ROWTYPE;
    v_current_parent_id number;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.FIND_NEW_TASK_PARENT';
begin
    --Figure out what the current parent id of this task is.
    --If the parent_task_id is null, the t.task_id != existing_parent did not
    --work in the parent_finder.  It would have worked if we used
    --t.task_id is null, but null doesn't work too well in comparisons.  Instead,
    --we are just making the parent_task_id zero if a task doesn't have one.
    select nvl(parent_task_id,0) into v_current_parent_id from pn_task where task_id = i_task_id;

    --Open up the parent_finder cursor, which we will use to traverse up
    --the tree
    open parent_finder(i_task_above_id, v_current_parent_id);

    --This is a bit clunky.  We need the last task in the list, so we just
    --cycle through all of them until we find it.
    loop
        fetch parent_finder into parent_id_rec;
        exit when parent_finder%NOTFOUND;
    end loop;

    close parent_finder;

    return(parent_id_rec.task_id);
end FIND_NEW_TASK_PARENT;

--This method finds the "natural sequence order" for a schedule and changes all
--the sequence numbers in the schedule to match that order.  This is useful if
--the sequence numbers are wrong due to some Project.net bug.
procedure RESEQUENCE_SCHEDULE(
    i_plan_id in number
)
is
    current_seq_number number := 1;

    cursor task_rows (i_plan_id in number) is
          select  t.task_id, pht.plan_id, seq, t.date_start, decode(t2.parent_task_id,null,0,1) as has_children
            from  pn_plan_has_task pht, pn_task t,
                  (select parent_task_id from pn_task t2 where t2.parent_task_id is not null group by parent_task_id) t2
           where  pht.task_id = t.task_id
                  and t.record_status = 'A'
                  and t2.parent_task_id(+) = t.task_id
                  and t.parent_task_id is null
                  and pht.plan_id = i_plan_id
        order by  pht.plan_id, t.date_start, t.rowid;

    task_row task_rows%ROWTYPE;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.RESEQUENCE_SCHEDULE';
begin
    --Iterate through all of the tasks
    for task_row in task_rows(i_plan_id) loop
        update pn_task t
        set seq = current_seq_number
        where t.task_id = task_row.task_id;

        --Increment the sequence number for the next pass
        current_seq_number := current_seq_number + 1;

        if (task_row.has_children = 1) then
            --Fix the sequence number for subtasks
            resequence_task_children(i_plan_id, task_row.task_id, current_seq_number);
        end if;
    end loop;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end RESEQUENCE_SCHEDULE;

procedure RESEQUENCE_TASK_CHILDREN(
    i_plan_id in number,
    i_task_id in number,
    i_current_seq_number in out number
)
is
    cursor subtask_rows (i_task_id in number, i_plan_id in number) is
          select  t.task_id, pht.plan_id, seq, t.date_start, decode(t2.parent_task_id,null,0,1) as has_children
            from  pn_plan_has_task pht, pn_task t,
                  (select parent_task_id from pn_task t2 where t2.parent_task_id is not null group by parent_task_id) t2
           where  pht.task_id = t.task_id
                  and t.record_status = 'A'
                  and t2.parent_task_id(+) = t.task_id
                  and t.parent_task_id = i_task_id
                  and pht.plan_id = i_plan_id
        order by  pht.plan_id, t.date_start, t.rowid;
    subtask_row subtask_rows%ROWTYPE;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.RESEQUENCE_TASK_CHILDREN';
begin
    for subtask_row in subtask_rows(i_task_id, i_plan_id) loop
        update pn_task t
        set seq = i_current_seq_number
        where t.task_id = subtask_row.task_id;

        --Increment the sequence number for the next pass
        i_current_seq_number := i_current_seq_number + 1;

        if (subtask_row.has_children = 1) then
            --Fix the sequence number for subtasks
            resequence_task_children(i_plan_id, subtask_row.task_id, i_current_seq_number);
        end if;
    end loop;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end RESEQUENCE_TASK_CHILDREN;

procedure STORE_BASELINE (
    i_object_id in NUMBER,
    i_baseline_name in VARCHAR2,
    i_baseline_description in VARCHAR2,
    i_creator_id in NUMBER,
    i_is_default_for_object in NUMBER,
    i_record_status in VARCHAR2,
    o_baseline_id in out NUMBER
)
is
    v_baseline_exists NUMBER := 0;
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.STORE_BASELINE';
begin
    if (o_baseline_id is not null) then
        select count(*) into v_baseline_exists
        from pn_baseline
        where baseline_id = o_baseline_id;
    end if;

    --If the new object is now the default object, clear out the default
    --flag from any other baseline
    if (i_is_default_for_object = 1) then
        update pn_baseline
        set is_default_for_object = 0
        where object_id = i_object_id;
    end if;

    if (v_baseline_exists > 0) then
        --Baseline already exists, update it
        update pn_baseline
        set object_id = i_object_id,
            name = i_baseline_name,
            description = i_baseline_description,
            date_modified = sysdate,
            record_status = i_record_status,
            is_default_for_object = i_is_default_for_object
        where
            baseline_id = o_baseline_id;
    else
        --This is a new baseline, create it

        --Get an id for this baseline
        o_baseline_id := BASE.CREATE_OBJECT('baseline', i_creator_id, 'A');

        --Insert baseline record
        insert into pn_baseline
          (baseline_id, object_id, name, description, date_created, date_modified, record_status, is_default_for_object)
        values
          (o_baseline_id, i_object_id, i_baseline_name, i_baseline_description, SYSDATE, SYSDATE, i_record_status, i_is_default_for_object);
    end if;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end STORE_BASELINE;

procedure REMOVE_BASELINE (
    i_baseline_id in number
)
is
begin
    delete from pn_baseline_has_task
    where baseline_id = i_baseline_id;

    delete from pn_baseline_has_plan
    where baseline_id = i_baseline_id;

    delete from pn_baseline b
    where baseline_id = i_baseline_id;
end REMOVE_BASELINE;

procedure STORE_PLAN_BASELINE (
    i_plan_id in number,
    i_baseline_id in number
)
is
    cursor plan_tasks (i_plan_id number) is
        select
          t.task_id
        from
          pn_task t, pn_plan_has_task pht
        where
          t.task_id = pht.task_id
          and t.record_status = 'A'
          and pht.plan_id = i_plan_id;
    plan_task_row plan_tasks%ROWTYPE;

    v_plan_version_id NUMBER;
    v_baseline_is_default NUMBER;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.STORE_PLAN_BASELINE';
begin
    --Now, iterate through all of the schedule entries and create a baseline entry
    for plan_task_row in plan_tasks(i_plan_id) loop
        STORE_TASK_BASELINE(plan_task_row.task_id, i_baseline_id);
    end loop;

    --Update the denormalized baseline start and end dates
    select is_default_for_object into v_baseline_is_default
    from pn_baseline
    where baseline_id = i_baseline_id;

    if (v_baseline_is_default = 1) then
        --Update the baseline columns to equal the new baseline.
        update pn_plan p
        set (baseline_start, baseline_end, baseline_id) =
          (select date_start, date_end, i_baseline_id
             from pn_plan p2
            where p2.plan_id = i_plan_id)
        where
          p.plan_id = i_plan_id;
    end if;

    --Store the current plan as a version.  This may be a bit redundant (there
    --is already one that is nearly identical), but it is easy to do because we
    --don't easily know what that last version is.  Perhaps we ought to start
    --storing what that last version is.
    STORE_PLAN_VERSION(i_plan_id, v_plan_version_id);

    --Remove any entry in pn_baseline_has_plan for this specific plan_id and
    --baseline id
    delete from pn_baseline_has_plan
    where
      baseline_id = i_baseline_id
      and plan_id = i_plan_id;

    insert into pn_baseline_has_plan
      (baseline_id, plan_id, plan_version_id)
    values
      (i_baseline_id, i_plan_id, v_plan_version_id);
end STORE_PLAN_BASELINE;

procedure STORE_PLAN_VERSION (
    i_plan_id in number,
    o_plan_version_id out number
)
is
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.STORE_PLAN_VERSION';
begin
    SELECT pn_object_sequence.nextval into o_plan_version_id FROM dual;

    insert into pn_plan_version (
      plan_id,plan_version_id,plan_name,plan_desc,date_start,date_end,autocalculate_task_endpoints,
      overallocation_warning,default_calendar_id,timezone_id,baseline_start,baseline_end,modified_date,
      modified_by,baseline_id, default_task_calc_type_id
    )
    select
      i_plan_id,o_plan_version_id,plan_name,plan_desc,date_start,date_end,autocalculate_task_endpoints,
      overallocation_warning,default_calendar_id,timezone_id,baseline_start,baseline_end,modified_date,
      modified_by,baseline_id, default_task_calc_type_id
    from pn_plan p
    where p.plan_id = i_plan_id;
exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end STORE_PLAN_VERSION;

procedure STORE_TASK_BASELINE (
    i_task_id in number,
    i_baseline_id in number
)
is
    v_task_version_id NUMBER;
    v_task_in_baseline NUMBER;
    v_baseline_is_default NUMBER;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.ARCHIVE_TASK_VERSION';
begin
    --Create a new version of the task
    STORE_TASK_VERSION(i_task_id, v_task_version_id);

    --Update the baseline_has_task table
    select count(*) into v_task_in_baseline
    from pn_baseline_has_task
    where baseline_id = i_baseline_id
      and task_id = i_task_id;

    if (v_task_in_baseline > 0) then
        --There is already a record in the baseline table for this baseline id, update it
        update pn_baseline_has_task
        set task_version_id = v_task_version_id
        where baseline_id = i_baseline_id
          and task_id = i_task_id;
    else
        insert into pn_baseline_has_task
          (baseline_id, task_id, task_version_id)
        values
          (i_baseline_id, i_task_id, v_task_version_id);
    end if;

    --Update the denormalized baseline start and end dates
    select is_default_for_object into v_baseline_is_default
    from pn_baseline
    where baseline_id = i_baseline_id;

/*    if (v_baseline_is_default = 1) then
        update pn_task
        set (baseline_start, baseline_end) =
          (select date_start, date_finish
             from pn_task_version
            where task_version_id = v_task_version_id)
        where
          task_id = i_task_id;
    end if;
*/exception
    when others then
    begin
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
    end;
end STORE_TASK_BASELINE;

procedure STORE_TASK_VERSION (
    i_task_id in number,
    o_task_version_id out number
)
is
    v_baseline_id NUMBER;
    -- Standard error handling
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.STORE_TASK_VERSION';
begin
    --Get a new ID for this baselined task id
    SELECT pn_object_sequence.nextval into o_task_version_id FROM dual;

    --Get the current baseline id
    begin
        select b.baseline_id into v_baseline_id
        from pn_baseline b, pn_baseline_has_task bht
        where b.baseline_id = bht.baseline_id
          and b.is_default_for_object = 1
          and bht.task_id = i_task_id;
    exception
        when no_data_found then
            v_baseline_id := null;
    end;

    --Create the copy of the task
    insert into pn_task_version (
      task_id,task_version_id,task_name,task_desc,task_type,duration,work,work_units,work_complete,
      date_start,work_complete_units,work_ms,work_complete_ms,date_finish,actual_start,actual_finish,priority,
      percent_complete,date_created,date_modified,modified_by,duration_units,parent_task_id,
      record_status,critical_path,seq,ignore_times_for_dates,is_milestone,early_start,
      early_finish,late_start,late_finish,work_percent_complete,baseline_id, calculation_type_id,
      unallocated_work_complete, unallocated_work_complete_unit
    )
    select
      i_task_id,o_task_version_id,t.task_name,t.task_desc,t.task_type,t.duration,t.work,t.work_units,t.work_complete,
      t.date_start,t.work_complete_units,t.work_ms,t.work_complete_ms,t.date_finish,t.actual_start,t.actual_finish,t.priority,
      t.percent_complete,t.date_created,t.date_modified,t.modified_by,t.duration_units,t.parent_task_id,
      t.record_status,t.critical_path,t.seq,t.ignore_times_for_dates,t.is_milestone,t.early_start,
      t.early_finish,t.late_start,t.late_finish,t.work_percent_complete, v_baseline_id, t.calculation_type_id,
      t.unallocated_work_complete, t.unallocated_work_complete_unit
    from
      pn_task t
    where
      t.task_id = i_task_id;

    STORE_TASK_DEPENDENCY_VERSION(i_task_id, o_task_version_id);
end STORE_TASK_VERSION;

procedure STORE_TASK_DEPENDENCY (
    i_task_id IN NUMBER,
    i_dependency_type_id IN NUMBER,
    i_lag IN NUMBER,
    i_lag_units IN NUMBER,
    i_dependency_ID NUMBER,
    i_update_if_exists NUMBER
)
is
    v_exists number;
begin
    select count(*) into v_exists from pn_task_dependency
    where
      task_id = i_task_id
      and dependency_id = i_dependency_id
      and dependency_type_id = i_dependency_type_id;

    if (v_exists = 0) then
        insert into pn_task_dependency
          (task_id, dependency_type_id, lag, lag_units, dependency_id)
        values
          (
            i_task_id,
            i_dependency_type_id,
            i_lag,
            i_lag_units,
            i_dependency_id
          );
    else
        if (i_update_if_exists > 0) then
            update pn_task_dependency
            set
              lag = i_lag,
              lag_units = i_lag_units
            where
              task_id = i_task_id
              and dependency_id = i_dependency_id
              and dependency_type_id = i_dependency_type_id;
        end if;
    end if;
end STORE_TASK_DEPENDENCY;

procedure STORE_TASK_DEPENDENCY_VERSION (
    i_task_id IN NUMBER,
    i_task_version_id in number
)
is
begin
    insert into pn_task_dependency_version
      (task_id, task_version_id, dependency_id, dependency_type_id, lag, lag_units)
    select
      task_id, i_task_version_id, dependency_id, dependency_type_id, lag, lag_units
    from
      pn_task_dependency
    where
      task_id = i_task_id;
end STORE_TASK_DEPENDENCY_VERSION;

Function IS_UP_TO_DATE
(
    i_plan_id IN NUMBER,
    i_last_loaded IN DATE
)
return NUMBER
is
  last_modified_date date;
begin
  select
    max(modified_date) into last_modified_date
  from
    (
      (select modified_date
         from pn_plan
        where plan_id = i_plan_id)
      UNION ALL
      (select t.date_modified
         from pn_plan_has_task pht,
              pn_task t
        where pht.task_id = t.task_id
          and pht.plan_id = i_plan_id
          and t.record_status = 'A')
      UNION ALL
      (select max(modified_date)
         from pn_plan_has_task pht,
              pn_assignment a
        where pht.task_id = a.object_id
          and pht.plan_id = i_plan_id
          and a.record_status = 'A')
    ) last_modified;

    if (i_last_loaded <= last_modified_date) then
        return 1;
    else
        return 0;
    end if;
end IS_UP_TO_DATE;


-- This function returns the percent complete (as a decimal)
-- calculated based on the work_ms and work_complete_ms of each schedule entry.
-- Note, summary tasks are not included in this calculation.
function GET_SCHEDULE_PERCENT_COMPLETE(
    i_plan_id in NUMBER
)
    return NUMBER
is
    v_work_complete NUMBER;
    v_work NUMBER;
    o_percent_complete NUMBER;
    stored_proc_name VARCHAR2(100):= 'SCHEDULE.GET_WORK_PERCENT_COMPLETE';
begin

    -- first get the work for all active (non-summary) tasks in the plan
    select sum(t.work_ms) into v_work
    from pn_task t, pn_space_has_plan sp, pn_plan_has_task pt
    where sp.plan_id = i_plan_id
    and sp.plan_id = pt.plan_id
    and t.task_id = pt.task_id
    and t.record_status = 'A'
    and t.task_type != 'summary';

    -- next get the work complete for all active (non-summary) tasks in the plan
    select sum(t.work_complete_ms) into v_work_complete
    from pn_task t, pn_space_has_plan sp, pn_plan_has_task pt
    where sp.plan_id = i_plan_id
    and sp.plan_id = pt.plan_id
    and t.task_id = pt.task_id
    and t.record_status = 'A'
    and t.task_type != 'summary';

    if (v_work = 0) then
        o_percent_complete := 0;
    else
        o_percent_complete := (v_work_complete / v_work) * 100;
    end if;

    return o_percent_complete;

end GET_SCHEDULE_PERCENT_COMPLETE;

function TEST_CYCLIC (
    i_task_id NUMBER,
    i_new_dependency_id NUMBER
)
    return NUMBER
is
    is_summary_task number;
    dependency_count number;
    cycle_found number;
begin
    --
    --Set up the initial run.   This is a little bit different from subsequent
    --runs because we have a "hypothetical" dependency here too.
    --
    cycle_found := 0;

    --Insert task dependencies
    insert into PN_TASK_CYCLE_DEPENDENCIES
    select dependency_id from pn_task_dependency where task_id = i_task_id;

    --Insert summary children
    insert into PN_TASK_CYCLE_DEPENDENCIES
    select task_id from pn_task where parent_task_id = i_task_id;

    --Insert shared tasks
    insert into PN_TASK_CYCLE_DEPENDENCIES
    select exported_object_id from pn_shared where imported_object_id = i_task_id;

    --Insert our test dependency id
    insert into PN_TASK_CYCLE_DEPENDENCIES values (i_new_dependency_id);

    select count(*) into dependency_count from PN_TASK_CYCLE_DEPENDENCIES;
    while (dependency_count > 0 AND cycle_found = 0) LOOP
        --Test to see if we have a cycle
        select count(*) into cycle_found from PN_TASK_CYCLE_DEPENDENCIES
        where id = i_task_id;

        if (cycle_found > 0) then
            return 1;
        end if;

        --Move all rows from PN_TASK_CYCLE_DEPENDENCIES to PN_TASK_CYCLE_WORK
        delete from PN_TASK_CYCLE_WORK;
        insert into PN_TASK_CYCLE_WORK select id from PN_TASK_CYCLE_DEPENDENCIES;
        delete from PN_TASK_CYCLE_DEPENDENCIES;

        --
        -- Find all dependencies and put them in PN_TASK_CYCLE_DEPENDENCIES
        --

        --Direct task dependencies
        insert into PN_TASK_CYCLE_DEPENDENCIES
        select dependency_id from PN_TASK_DEPENDENCY
        where task_id in (select id from PN_TASK_CYCLE_WORK);

        --Summary children
        insert into PN_TASK_CYCLE_DEPENDENCIES
        select task_id from pn_task
        where parent_task_id in (select id from PN_TASK_CYCLE_WORK);

        --Shared tasks
        insert into PN_TASK_CYCLE_DEPENDENCIES
        select exported_object_id from pn_shared
        where imported_object_id in (select id from PN_TASK_CYCLE_WORK);

        --
        --Get ready for the next loop iteration
        --
        select count(*) into dependency_count from PN_TASK_CYCLE_DEPENDENCIES;
    END LOOP;

    return 0;
end TEST_CYCLIC;

function GET_USERNAME (
	i_id in NUMBER
) return VARCHAR2
is
	i_user VARCHAR2(100) := '';
begin
	select username into i_user from pn_user where user_id = i_id and record_status = 'A';
	return i_user;
end GET_USERNAME;

end;
/

CREATE OR REPLACE PACKAGE SCHEDULE IS

-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Phil        1/15/01 created

    -- Default Task Calculation Type ID
    -- 10 = Fixed Unit, Effort Driven
    DEFAULT_TASK_CALC_TYPE_ID   constant number := 10;

   PROCEDURE store_assignment
     (
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_object_id IN NUMBER,
    i_percent IN NUMBER,
    i_role IN VARCHAR2,
    i_status_id IN NUMBER,
    i_primary_owner IN NUMBER,
    i_start_date in date,
    i_end_date in date,
    i_actual_start IN DATE,
    i_actual_finish IN DATE,
    i_estimated_finish in DATE,
    i_work in NUMBER,
    i_work_units in NUMBER,
    i_work_complete in NUMBER,
    i_work_complete_units in NUMBER,
    i_is_complete in NUMBER,
    i_percent_complete in NUMBER,
    i_modified_by in NUMBER,
    i_record_status IN VARCHAR2,
    o_status OUT NUMBER
);

  procedure store_task
     (
    i_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_plan_id IN NUMBER,
    i_task_id IN NUMBER,
    i_task_name IN VARCHAR2,
    i_description IN vARCHAR2,
    i_task_type IN VARCHAR2,
    i_priority IN NUMBER,
    i_duration IN NUMBER,
    i_duration_units IN NUMBER,
    i_work IN NUMBER,
    i_work_units IN NUMBER,
    i_work_complete IN NUMBER,
    i_work_complete_units IN NUMBER,
    i_work_ms IN NUMBER,
    i_work_complete_ms IN NUMBER,
    i_work_percent_complete IN NUMBER,
    i_percent_complete IN NUMBER,
    i_date_finish IN DATE,
    i_date_start IN DATE,
    i_actual_start IN DATE,
    i_actual_finish IN DATE,
    i_critical_path IN NUMBER,
    i_record_status IN CHAR,
    i_parent_task_id IN NUMBER,
    i_ignore_times IN NUMBER,
    i_early_start IN DATE,
    i_early_finish IN DATE,
    i_late_start IN DATE,
    i_late_finish IN DATE,
    i_milestone IN NUMBER,
    i_constraint_type IN NUMBER,
    i_constraint_date IN DATE,
    i_deadline IN DATE,
    i_phase_id IN NUMBER,
    i_unallocated_wk_complete IN NUMBER,
    i_unallocated_wk_complete_unit IN NUMBER,
    i_unassigned_work IN NUMBER,
    i_unassigned_work_units IN NUMBER,
    i_calculation_type_id in NUMBER,
    o_sequence_number IN OUT NUMBER,
    o_task_id OUT NUMBER
);

procedure add_comment (
    i_task_id   in  number,
    i_created_by_id in number,
    i_created_datetime  in date,
    i_is_comment_null in number,
    o_comment_clob   out clob
);

procedure MOVE_TASK_DOWN (
    i_task_id in number,
    i_plan_id in number
);

procedure MOVE_TASK_UP (
    i_task_id in number,
    i_plan_id in number
);

procedure RECALCULATE_SEQUENCE_NUMBERS (
    i_plan_id in number
);

procedure MOVE_TASK_TO_SEQUENCE_NUMBER (
    i_plan_id number,
    i_task_id number,
    i_new_seq number
);

procedure FIX_SUMMARY_TASK_TYPES (
    i_plan_id in number
);

function FIND_NEW_TASK_PARENT(
    i_task_id in number,
    i_task_above_id in number
) return NUMBER;

function GET_SCHEDULE_PERCENT_COMPLETE(
    i_plan_id in number
) return NUMBER;

procedure RESEQUENCE_SCHEDULE(
    i_plan_id in number
);

procedure RESEQUENCE_TASK_CHILDREN(
    i_plan_id in number,
    i_task_id in number,
    i_current_seq_number in out number
);

procedure STORE_BASELINE (
    i_object_id in NUMBER,
    i_baseline_name in VARCHAR2,
    i_baseline_description in VARCHAR2,
    i_creator_id in NUMBER,
    i_is_default_for_object in NUMBER,
    i_record_status in VARCHAR2,
    o_baseline_id in out NUMBER
);

procedure STORE_TASK_BASELINE (
    i_task_id in number,
    i_baseline_id in number
);

procedure STORE_TASK_VERSION (
    i_task_id in number,
    o_task_version_id out number
);

procedure STORE_PLAN_BASELINE (
    i_plan_id in number,
    i_baseline_id in number
);

procedure STORE_PLAN_VERSION (
    i_plan_id in number,
    o_plan_version_id out number
);
procedure STORE_TASK_DEPENDENCY (
    i_task_id IN NUMBER,
    i_dependency_type_id IN NUMBER,
    i_lag IN NUMBER,
    i_lag_units IN NUMBER,
    i_dependency_ID NUMBER,
    i_update_if_exists NUMBER
);
procedure STORE_TASK_DEPENDENCY_VERSION (
    i_task_id IN NUMBER,
    i_task_version_id in number
);
procedure STORE_PLAN (
    i_name IN VARCHAR2,
    i_description IN VARCHAR2,
    i_start_date IN DATE,
    i_end_date IN DATE,
    i_autocalculate_task_endpoints IN NUMBER,
    i_default_calendar_id IN NUMBER,
    i_timezone_id IN VARCHAR2,
    i_modified_by IN NUMBER,
    i_space_id IN NUMBER,
    i_default_task_calc_type_id IN NUMBER,
    i_earliest_start_date IN DATE,
    i_earliest_finish_date IN DATE,
    i_latest_start_date IN DATE,
    i_latest_finish_date IN DATE,
    i_start_constraint_type VARCHAR2,
    i_start_constraint_date DATE,
    io_plan_id IN OUT NUMBER
);

procedure REMOVE_BASELINE (
    i_baseline_id in number
);

Function IS_UP_TO_DATE
(
    i_plan_id IN NUMBER,
    i_last_loaded IN DATE
)
return NUMBER;

function TEST_CYCLIC (
    i_task_id NUMBER,
    i_new_dependency_id NUMBER
)
return NUMBER;

function GET_USERNAME (
	i_id in NUMBER
) return VARCHAR2;

END;
/

CREATE OR REPLACE PACKAGE BODY security IS

---------------------------------------------------------------------
-- STORE_MODULE_PERMISSION
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  -------------------------------------------
-- Phil Dixon   3/30/00 initial create
-- Roger Bly    4/6/00  added person_id (object creator), give all object permissions to creator.
-- Roger Bly    4/6/00  moved to SECURITY PACKAGE
-- Roger Bly    4/10/00 Fixed bug in create_security_permissions for creator's principal group.
-- Roger Bly    1/7/01  Added procedures for copying groups and permissions to another space.
-- Tim Morrow  10/23/01 Fixed for new column GROUP_TYPE_ID in PN_GROUP
-- Tim Morrow  11/12/01 Added inherit_group procedure
-- Tim Morrow  28/02/03 Fixed CREATE_SECURITY_PERMISSIONS to not rollback and to
--                      raise exceptions that occur

PROCEDURE CREATE_SECURITY_PERMISSIONS
(
    p_object_id IN pn_object.object_id%type,
    p_object_type IN pn_object_type.object_type%type,
    p_space_id IN pn_project_space.project_id%type,
    p_person_id IN pn_person.person_id%type
)
IS

    v_group_id                      pn_group.group_id%type;
    v_space_group                   pn_group.group_id%type;
    v_actions                       pn_object_type.default_permission_actions%type;
    v_creator_principal_group_id    pn_group.group_id%type;
    v_module_id                     pn_module.module_id%type;

    -- All action bits set = (2^16)-1
    G_ALL_ACTIONS NUMBER := 65535;

    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.CREATE_SECURITY_PERMISSIONS';

    -- All default object permissions for the space and object type
    CURSOR  c_default_permissions (the_space_id pn_project_space.project_id%type, the_object_type pn_object_type.object_type%type) IS
        select dop.group_id, dop.actions
        from pn_default_object_permission dop, pn_group g
        where dop.space_id = the_space_id and
        dop.object_type = the_object_type and
        g.group_id = dop.group_id and
        g.is_principal <> 1;

    -- All groups currently defined for the space.
    CURSOR  c_groups (the_space_id pn_project_space.project_id%type) IS
		SELECT g.group_id, g.is_principal
		FROM pn_space_has_group shg, pn_group g
        WHERE shg.space_id = the_space_id and g.group_id = shg.group_id and g.record_status = 'A';

BEGIN

    -- Get the object creator's (person) principal group_id for this space.
    -- The object creator gets all permissions on the object.
    BEGIN
        select
            g.group_id into v_creator_principal_group_id
        from
            pn_space_has_group shg, pn_group g, pn_group_has_person ghp
        where
            shg.space_id = p_space_id and
            ghp.group_id = shg.group_id and
            ghp.person_id = p_person_id and
            g.group_id = shg.group_id and
            g.is_principal = 1;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_creator_principal_group_id := NULL;
    END;

    -- For this group is the creator's principal group,
    -- give them all permissions on the object they are creating.
    IF (v_creator_principal_group_id IS NOT NULL)
    THEN
        INSERT INTO pn_object_permission
          (object_id, group_id, actions)
        values
          (p_object_id, v_creator_principal_group_id, G_ALL_ACTIONS);
    END IF;


    -- COPIES ALL OBJECT PERMISSIONS AND ALL OF THE GROUPS (except principal groups)
    -- FOR THIS PROJECT TO THE OBJECT_PERMISSION TABLE

    OPEN c_default_permissions(p_space_id, p_object_type);
    <<permission_loop>>
	LOOP

		FETCH c_default_permissions INTO v_group_id, v_actions;
		EXIT WHEN c_default_permissions%NOTFOUND;

            IF (v_group_id IS NOT NULL)
            THEN
                INSERT INTO pn_object_permission
                    (object_id, group_id, actions)
                values
                    (p_object_id, v_group_id, v_actions);
            END IF;

    END LOOP permission_loop;
	CLOSE c_default_permissions;

    -- Log exceptions
    EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        BASE.LOG_ERROR(stored_proc_name, SQLCODE, SQLERRM);
        raise;
      END;

END; -- CREATE_SECURITY_PERMISSIONS

---------------------------------------------------------------------
-- APPLY_DOCUMENT_PERMISSIONS
---------------------------------------------------------------------
PROCEDURE APPLY_DOCUMENT_PERMISSIONS
(
    p_object_id IN pn_object.object_id%type,
    p_parent_id IN pn_object.object_id%type,
    p_object_type IN pn_object_type.object_type%type,
    p_space_id IN pn_project_space.project_id%type,
    p_person_id IN pn_person.person_id%type
)
IS

    v_group_id                      pn_group.group_id%type;
    v_actions                       pn_object_type.default_permission_actions%type;
    v_creator_principal_group_id    pn_group.group_id%type;

    -- All action bits set = (2^16)-1
    G_ALL_ACTIONS NUMBER := 65535;

    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.APPLY_DOCUMENT_PERMISSIONS';

    -- All default object permissions for the space system groups
    CURSOR  c_default_permissions (the_space_id pn_project_space.project_id%type, the_object_type pn_object_type.object_type%type) IS
        select dop.group_id, dop.actions
        from pn_default_object_permission dop, pn_group g
        where dop.space_id = the_space_id and
        dop.object_type = the_object_type and
        g.group_id = dop.group_id and
        g.is_system_group = 1 and g.is_principal <> 1;

    -- All default object permissions for the space system groups
    CURSOR  c_parent_permissions (the_parent pn_object_permission.object_id%type) IS
        select group_id, actions
        from pn_object_permission
        where object_id = the_parent;

BEGIN
    -- remove the objects current security settings
    DELETE FROM pn_object_permission
    WHERE object_id = p_object_id;

    IF (p_parent_id IS NOT NULL)
    THEN
        -- the object has a parent, therefore inherit the parents security settings
        OPEN c_parent_permissions(p_parent_id);
        <<permission_loop>>
	    LOOP
		  FETCH c_parent_permissions INTO v_group_id, v_actions;
		  EXIT WHEN c_parent_permissions%NOTFOUND;

            IF (v_group_id IS NOT NULL)
            THEN
                INSERT INTO pn_object_permission
                    (object_id, group_id, actions)
                values
                    (p_object_id, v_group_id, v_actions);
            END IF;

        END LOOP permission_loop;
	    CLOSE c_parent_permissions;


        IF (p_person_id IS NOT NULL)
        THEN
            -- Get the object creator's (person) principal group_id for this space.
            -- The object creator gets all permissions on the object.
            BEGIN
                select
                    g.group_id into v_creator_principal_group_id
                from
                    pn_space_has_group shg, pn_group g, pn_group_has_person ghp
                where
                    shg.space_id = p_space_id and
                    ghp.group_id = shg.group_id and
                    ghp.person_id = p_person_id and
                    g.group_id = shg.group_id and
                    g.is_principal = 1;
            EXCEPTION
            WHEN NO_DATA_FOUND THEN
                v_creator_principal_group_id := NULL;
            END;

            -- For this group is the creator's principal group,
            -- give them all permissions on the object they are creating.
            IF (v_creator_principal_group_id IS NOT NULL)
            THEN
                DELETE FROM pn_object_permission
                WHERE object_id = p_object_id and
                    group_id = v_creator_principal_group_id;

                INSERT INTO pn_object_permission
                    (object_id, group_id, actions)
                values
                    (p_object_id, v_creator_principal_group_id, G_ALL_ACTIONS);
            END IF; -- (v_creator_principal_group_id IS NOT NULL)

        END IF; -- (p_pesron_id IS NOT NULL)
    ELSE
    -- the object does not have a parent therefore system groups get full permissions

    OPEN c_default_permissions(p_space_id, p_object_type);
    <<permission_loop>>
	LOOP

		FETCH c_default_permissions INTO v_group_id, v_actions;
		EXIT WHEN c_default_permissions%NOTFOUND;

            IF (v_group_id IS NOT NULL)
            THEN
                INSERT INTO pn_object_permission
                    (object_id, group_id, actions)
                values
                    (p_object_id, v_group_id, G_ALL_ACTIONS);
            END IF;

    END LOOP permission_loop;
	CLOSE c_default_permissions;

    END IF;

    -- Log exceptions
    EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        BASE.LOG_ERROR(stored_proc_name, sqlcode, sqlerrm);
        raise;
      END;

END; -- APPLY_DOCUMENT_PERMISSIONS

---------------------------------------------------------------------
-- ADD Person To Group
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        27-Apr-00   Creation.

PROCEDURE add_person_to_group
(
    group_id IN pn_group.group_id%type,
    person_id IN pn_person.person_id%type,
    o_status OUT NUMBER
)
IS

stored_proc_name VARCHAR2(100):= 'SECURITY.ADD_PERSON_TO_GROUP';

BEGIN

    insert into pn_group_has_person
        (group_id, person_id)
    values
        (group_id, person_id);

    o_status := success;

EXCEPTION
    WHEN OTHERS THEN
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
END;


---------------------------------------------------------------------
-- STORE_MODULE_PERMISSION
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation.

PROCEDURE store_module_permission
(
    i_space_id IN NUMBER,
    i_group_id IN NUMBER,
    i_module_id IN NUMBER,
    i_actions IN NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'SECURITY.STORE_MODULE_PERMISSION';

BEGIN

    INSERT INTO PN_MODULE_PERMISSION (space_id, group_id, module_id, actions)
        VALUES (i_space_id, i_group_id, i_module_id, i_actions);

    COMMIT;
    o_status := success;

EXCEPTION
    WHEN e_unique_constraint THEN
        UPDATE PN_MODULE_PERMISSION
            SET actions = i_actions
            WHERE space_id = i_space_id
            AND group_id = i_group_id
            AND module_id = i_module_id;

        IF SQL%NOTFOUND THEN
            o_status := no_parent_key;
            ROLLBACK;
            RETURN;
        END IF;

        COMMIT;
        o_status := success;

    WHEN e_null_constraint THEN
        o_status := null_field;

    WHEN e_no_parent_key THEN
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END store_module_permission;


---------------------------------------------------------------------
-- STORE_OBJECT_PERMISSION
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation.

PROCEDURE store_object_permission
(
    i_object_id IN NUMBER,
    i_group_id IN NUMBER,
    i_actions IN NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'SECURITY.STORE_OBJECT_PERMISSION';

BEGIN

    INSERT INTO PN_OBJECT_PERMISSION (object_id, group_id, actions)
        VALUES (i_object_id, i_group_id, i_actions);

    COMMIT;
    o_status := success;

EXCEPTION
    WHEN e_unique_constraint THEN
        UPDATE PN_OBJECT_PERMISSION
            SET actions = i_actions
            WHERE object_id = i_object_id
            AND group_id = i_group_id;

        IF SQL%NOTFOUND THEN
            o_status := no_parent_key;
            ROLLBACK;
            RETURN;
        END IF;

        COMMIT;
        o_status := success;

    WHEN e_null_constraint THEN
        o_status := null_field;

    WHEN e_no_parent_key THEN
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END store_object_permission;


---------------------------------------------------------------------
-- STORE_DEFAULT_OBJ_PERMISSION
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation.

PROCEDURE store_default_obj_permission
(
    i_space_id IN NUMBER,
    i_object_type IN VARCHAR2,
    i_group_id IN NUMBER,
    i_actions IN NUMBER,
    o_status OUT NUMBER
)

IS

    stored_proc_name VARCHAR2(100):= 'SECURITY.STORE_DEFAULT_OBJ_PERMISSION';

BEGIN

    begin
        -- Try to insert default object permission
        -- This might fail if there is already a value there
        INSERT INTO PN_DEFAULT_OBJECT_PERMISSION (space_id, object_type, group_id, actions)
            VALUES (i_space_id, i_object_type, i_group_id, i_actions);

    exception
        WHEN e_unique_constraint THEN
        begin
            -- Already has permissions, update them instead
            UPDATE PN_DEFAULT_OBJECT_PERMISSION
                SET actions = i_actions
                WHERE space_id = i_space_id
                AND object_type = i_object_type
                AND group_id = i_group_id;
        end;

    end;

    COMMIT;
    o_status := success;

EXCEPTION
    WHEN e_null_constraint THEN
        o_status := null_field;

    WHEN e_no_parent_key THEN
        o_status := no_parent_key;

    WHEN OTHERS THEN
    begin
        o_status := generic_error;
        ROLLBACK;
        BASE.LOG_ERROR(stored_proc_name, sqlcode, sqlerrm);
    end;

END store_default_obj_permission;


---------------------------------------------------------------------
-- REMOVE_GROUP_PERMISSION
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       11-Apr-00   Creation.
-- Adam        30-Apr-00   Added remove group from space

PROCEDURE remove_group_permission
(
    i_group_id IN NUMBER,
    o_status OUT NUMBER
)
IS
    v_group_type_id pn_group_type.group_type_id%TYPE;
    v_space_type pn_object.object_type%TYPE;
    v_space_id pn_object.object_id%TYPE;
BEGIN
    --Make sure that the group wasn't the everyone group.  If it was, change a
    --project or business to reflect that they no longer are allowing everyone
    select
      group_type_id, o.object_type, o.object_id into v_group_type_id, v_space_type, v_space_id
    from
      pn_group g,
      pn_space_has_group shg,
      pn_object o
    where
      g.group_id = i_group_id
      and g.group_id = shg.group_id
      and shg.is_owner = 1
      and shg.space_id = o.object_id(+);

    if (v_group_type_id = 600 and v_space_type = 'business') then
      update pn_business_space
      set includes_everyone = 0
      where business_space_id = v_space_id;
    elsif (v_group_type_id = 600 and v_space_type = 'project') then
      update pn_project_space
      set includes_everyone = 0
      where project_id = v_space_id;
    end if;


    -- hard delete from permission tables
    DELETE FROM pn_default_object_permission
        WHERE group_id = i_group_id;

    DELETE FROM pn_module_permission
        WHERE group_id = i_group_id;

    DELETE FROM pn_object_permission
        WHERE group_id = i_group_id;

    DELETE FROM pn_page_permission
        WHERE group_id = i_group_id;

    -- remove the group from the space(s) it belongs to
    DELETE FROM pn_space_has_group
        WHERE group_id = i_group_id;

    -- remove group membership where other groups are a member of this
    -- one or this group is a member of other groups
    delete from pn_group_has_group
        where group_id = i_group_id
        or member_group_id = i_group_id;

    -- soft delete from group table
    -- This is necessary because other parts of the system (Workflow)
    -- may break if the group is gone
    UPDATE pn_group SET record_status = 'D'
        WHERE group_id = i_group_id;

END remove_group_permission;


---------------------------------------------------------------------
-- apply_admin_permissions
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        21-Nov-00   Creation.
--
-- Get's the list of existing secured object id's and remediates the
-- security permissions for the new ADMIN group.
-- ONLY FOR USE BY CREATE_PARENT_ADMIN_GROUP
---------------------------------------------------------------------
procedure apply_admin_permissions
(
    i_new_group_id IN NUMBER,
    i_space_admin_group_id IN NUMBER
) IS

    pragma autonomous_transaction;

    -- variable definition
    v_object_id     NUMBER;
    stored_proc_name VARCHAR2(100):= 'SECURITY.APPLY_ADMIN_PERMISSIONS';

    G_ALL_ACTIONS NUMBER := 65535;

    -- cursor definition
    CURSOR c_existing_objects (admin_group_id NUMBER) IS
        select object_id from pn_object_permission
            where group_id = admin_group_id;

BEGIN

    OPEN c_existing_objects(i_space_admin_group_id);
	<<object_loop>>
	LOOP
        -- ALL OBJECT TYPES IN THE SYSTEM
		FETCH c_existing_objects INTO v_object_id;
		EXIT WHEN c_existing_objects%NOTFOUND;

		INSERT INTO pn_object_permission (
            object_id,
            group_id,
            actions)
		VALUES (
			v_object_id,
			i_new_group_id,
            G_ALL_ACTIONS); -- space administrators get all actions for all types

	END LOOP object_loop;
	CLOSE c_existing_objects;

    commit;

    EXCEPTION

    WHEN OTHERS THEN
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;


END;



---------------------------------------------------------------------
-- CREATE_PARENT_ADMIN_GROUP
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        21-Nov-00   Creation.


Function create_parent_admin_role
(
    i_space_id IN NUMBER,
    i_parent_space_id IN NUMBER
)
RETURN  NUMBER IS

    -- Local variables
    v_group_id      pn_group.group_id%type;
    v_module_id     pn_module.module_id%type;
    v_actions       pn_module.default_permission_actions%type;
    v_object_type   pn_object_type.object_type%type;
    v_current_admin_group   pn_group.group_id%type;

    -- All action bits set = (2^16)-1
    G_ALL_ACTIONS NUMBER := 65535;

    G_VIEW_ACTION NUMBER := 1;

    -- error logging
    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'SECURITY.CREATE_PARENT_ADMIN_GROUP';


    CURSOR  c_module IS
		SELECT m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

        -- cursor definitions
   	CURSOR  c_object_type IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;

    CURSOR  c_group_id IS
        select g.group_id from pn_space_has_group shg, pn_group g
        where shg.space_id = i_parent_space_id
        and g.group_type_id = SECURITY.GROUP_TYPE_SPACEADMIN
        and shg.group_id = g.group_id;


BEGIN
    -- get the existing Space Admin Group for remediation of existing objects
    select g.group_id into v_current_admin_group from pn_space_has_group shg, pn_group g
    where shg.space_id = i_space_id
    and g.group_type_id = SECURITY.GROUP_TYPE_SPACEADMIN
    and shg.group_id = g.group_id;

    -- Get the Space Administrator role from the parent space
    OPEN c_group_id;
    <<group_loop>>
    LOOP
        --ALL Space Administrator Roles from the parent space
        FETCH c_group_id INTO v_group_id;
        EXIT WHEN c_group_id%NOTFOUND;

        -- Add the group to the project_space
        insert into pn_space_has_group
            (space_id, group_id, is_owner)
        values
            (i_space_id, v_group_id, 0);


        -- Set all object_type permissions
        -- for administrator the actions are set to G_ALL_ACTIONS or (2^16)-1
        OPEN c_object_type;
    	<<type_loop>>
    	LOOP
            -- ALL OBJECT TYPES IN THE SYSTEM
    		FETCH c_object_type INTO v_object_type, v_actions;
    		EXIT WHEN c_object_type%NOTFOUND;

    		INSERT INTO pn_default_object_permission (
    			space_id,
    		    object_type,
                group_id,
                actions)
    		VALUES (
    			i_space_id,
    			v_object_type,
                v_group_id,
                G_ALL_ACTIONS); -- space administrators get all actions for all types

    	END LOOP type_loop;
    	CLOSE c_object_type;


        -- Default Module permissions for Space Administrator
        OPEN c_module;
    	<<module_loop>>
    	LOOP

    		FETCH c_module INTO v_module_id, v_actions;
    		EXIT WHEN c_module%NOTFOUND;

            INSERT INTO pn_module_permission (
    		    module_id,
                space_id,
                group_id,
                actions)
    		VALUES (
    			v_module_id,
                i_space_id,
                v_group_id,         -- Must follow the group creation for Space Administrator above
                G_ALL_ACTIONS);     -- The default module permission allows all Space Administrator to do everything


    	END LOOP module_loop;
    	CLOSE c_module;

        -- remediate existing objects
        apply_admin_permissions (v_group_id, v_current_admin_group);

    END LOOP group_loop;
    CLOSE c_group_id;

    -- return the ID of the newly created space administrator group.
    RETURN v_group_id;

 EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        BASE.LOG_ERROR(stored_proc_name, sqlcode, sqlerrm);
      END;


END;


---------------------------------------------------------------------
-- F_CREATE_SPACE_ADMIN_GROUP
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Roger       8-Apr-00    Creation.
-- Create a new space administrator group.
--
-- The creator is the initial administrator
-- PUT THE CREATOR OF THE PROJECT INTO THE PROJECT ADMINISTRATORS GROUP
--
-- NOTE: the 'Space Administrator' group is a special group that can not be
-- created using the STORE_GROUP procedure since it needs special permissions
-- not created by the STORE_GROUP procedure.


Function f_create_space_admin_group
(
    i_creator_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_description IN VARCHAR
)
RETURN  NUMBER IS

    -- Local variables
    v_group_id      pn_group.group_id%type;
    v_module_id     pn_module.module_id%type;
    v_actions       pn_module.default_permission_actions%type;
    v_object_type   pn_object_type.object_type%type;

    -- All action bits set = (2^16)-1
    G_ALL_ACTIONS NUMBER := 65535;

    -- error logging
    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'CREATE_SPACE_ADMIN_GROUP';


    CURSOR c_methodology IS SELECT methodology_id FROM
           pn_methodology_view where
           parent_space_id = i_space_id ;

    CURSOR  c_module IS
		SELECT m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

        -- cursor definitions
   	CURSOR  c_object_type IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;


BEGIN

    v_group_id := BASE.CREATE_OBJECT('group', i_creator_person_id, 'A');

    insert into pn_group
        (group_id, group_name, group_desc, is_principal, record_status, is_system_group, group_type_id)
    values
        (v_group_id, '@prm.security.group.type.spaceadministrator.name', i_description, 0, 'A', 1, SECURITY.GROUP_TYPE_SPACEADMIN);

    --SECURITY.CREATE_SECURITY_PERMISSIONS(v_group_id, 'group', p_project_id, v_creator);

    -- Add the project creator (person) to the group
     insert into pn_group_has_person
        (group_id, person_id)
     values
        (v_group_id, i_creator_person_id);

    -- Add the group to the project_space
    insert into pn_space_has_group
        (space_id, group_id, is_owner)
    values
        (i_space_id, v_group_id, 1);

    -- Copies the System Group into the Object Permissions Table
    insert into pn_object_permission
        (object_id, group_id, actions)
    values
        (v_group_id, v_group_id, G_ALL_ACTIONS); -- G_ALL_ACTIONS is a 16 bit MAXINT


    -- Set all object_type permissions
    -- for administrator the actions are set to G_ALL_ACTIONS or (2^16)-1
    OPEN c_object_type;
	<<type_loop>>
	LOOP
        -- ALL OBJECT TYPES IN THE SYSTEM
		FETCH c_object_type INTO v_object_type, v_actions;
		EXIT WHEN c_object_type%NOTFOUND;

		INSERT INTO pn_default_object_permission (
			space_id,
		    object_type,
            group_id,
            actions)
		VALUES (
			i_space_id,
			v_object_type,
            v_group_id,
            G_ALL_ACTIONS); -- space administrators get all actions for all types

	END LOOP type_loop;
	CLOSE c_object_type;


    -- Default Module permissions for Space Administrator
    OPEN c_module;
	<<module_loop>>
	LOOP

		FETCH c_module INTO v_module_id, v_actions;
		EXIT WHEN c_module%NOTFOUND;

        INSERT INTO pn_module_permission (
		    module_id,
            space_id,
            group_id,
            actions)
		VALUES (
			v_module_id,
            i_space_id,
            v_group_id,         -- Must follow the group creation for Space Administrator above
            G_ALL_ACTIONS);     -- The default module permission allows all Space Administrator to do everything


	END LOOP module_loop;
	CLOSE c_module;

    -- return the ID of the newly created space administrator group.
    RETURN v_group_id;

 EXCEPTION

    WHEN OTHERS THEN
      base.log_error(stored_proc_name, sqlcode, sqlerrm);
      raise;

END;


Function f_create_power_user_group
(
    i_creator_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_description IN VARCHAR
)
RETURN  NUMBER IS

    -- Local variables
    v_group_id      pn_group.group_id%type;
    v_module_id     pn_module.module_id%type;
    v_actions       pn_module.default_permission_actions%type;
    v_object_type   pn_object_type.object_type%type;

    -- By default, power user will receive view, modify, create, and share = 128|4|2|1
    G_PERMISSIBLE_ACTIONS NUMBER := 135;

    -- error logging
    stored_proc_name VARCHAR2(100):= 'CREATE_POWER_USER_GROUP';

    CURSOR  c_module IS
		SELECT m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

        -- cursor definitions
   	CURSOR  c_object_type IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;


BEGIN

    v_group_id := BASE.CREATE_OBJECT('group', i_creator_person_id, 'A');

    insert into pn_group
        (group_id, group_name, group_desc, is_principal, record_status, is_system_group, group_type_id)
    values
        (v_group_id, '@prm.security.group.type.poweruser.name', i_description, 0, 'A', 1, SECURITY.GROUP_TYPE_POWERUSER);

    SECURITY.CREATE_SECURITY_PERMISSIONS(v_group_id, 'group', i_space_id, i_creator_person_id);

    -- Add the group to the project_space
    insert into pn_space_has_group
        (space_id, group_id, is_owner)
    values
        (i_space_id, v_group_id, 1);


    OPEN c_object_type;
	<<type_loop>>
	LOOP
        -- ALL OBJECT TYPES IN THE SYSTEM
		FETCH c_object_type INTO v_object_type, v_actions;
		EXIT WHEN c_object_type%NOTFOUND;

		INSERT INTO pn_default_object_permission (
			space_id,
		    object_type,
            group_id,
            actions)
		VALUES (
			i_space_id,
			v_object_type,
            v_group_id,
            G_PERMISSIBLE_ACTIONS);

	END LOOP type_loop;
	CLOSE c_object_type;


    -- Default Module permissions for Power User Group
    OPEN c_module;
	<<module_loop>>
	LOOP

		FETCH c_module INTO v_module_id, v_actions;
		EXIT WHEN c_module%NOTFOUND;

        INSERT INTO pn_module_permission (
		    module_id,
            space_id,
            group_id,
            actions)
		VALUES (
			v_module_id,
            i_space_id,
            v_group_id,
            G_PERMISSIBLE_ACTIONS);


	END LOOP module_loop;
	CLOSE c_module;

    -- return the ID of the newly created power user group.
    RETURN v_group_id;

 EXCEPTION

    WHEN OTHERS THEN
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;

END;

---------------------------------------------------------------------
-- STORE_GROUP
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Roger       7-Apr-00    Created from JDBC code in net.project.security.Group.java
-- Adam        26-Apr-00   group is now added to space within this procedure
--                         all external references to add group to space have been
--                         removed

Procedure STORE_GROUP

(   i_group_name IN VARCHAR2,
    i_group_desc IN VARCHAR2,
    i_is_principal IN NUMBER,
    i_is_system_group IN NUMBER,
    i_group_type_id in number,
    i_creator_person_id IN NUMBER,
    i_space_id IN NUMBER,
    io_group_id IN OUT NUMBER
)
IS
    v_module_id     pn_module.module_id%type;
    v_actions       pn_module.default_permission_actions%type;
    v_object_type   pn_object_type.object_type%type;
    v_principal_owner_id pn_person.person_id%type;
    v_space_type    pn_object_type.object_type%type;

    CURSOR  c_module IS
		SELECT m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

        -- cursor definitions
   	CURSOR  c_object_type IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;
BEGIN
    -- null group_id means we are storing the group for the first time.
    IF (io_group_id IS NULL) THEN

        -- Principal groups have the owner's id stamped on them
        IF (i_is_principal = '1') THEN
            v_principal_owner_id := i_creator_person_id;
        end if;

        -- Create new ID for the group and set default security permissions
        io_group_id := BASE.CREATE_OBJECT('group', i_creator_person_id, 'A');

        -- insert into the group table
        insert into pn_group
            (group_id, group_name, group_desc, is_principal, is_system_group, record_status, group_type_id, principal_owner_id)
        values
             (io_group_id, i_group_name, i_group_desc, i_is_principal, i_is_system_group, 'A', i_group_type_id, v_principal_owner_id);

        -- insert the group into the space
        insert into pn_space_has_group (space_id, group_id, is_owner) values (i_space_id, io_group_id, 1);

        -- Principal Groups don't get default permissions.
        IF (i_is_principal <> '1') THEN

            -- Apply module_permissions to the new group.
            -- Groups are special objects that need module permissions for security checks.
            OPEN c_module;
    	    <<module_loop>>
    	    LOOP

    		  FETCH c_module INTO v_module_id, v_actions;
    		  EXIT WHEN c_module%NOTFOUND;

                    INSERT INTO pn_module_permission (
    		            module_id,
                        space_id,
                        group_id,
                        actions)
    		        VALUES (
    			        v_module_id,
                        i_space_id,
                        io_group_id,
                        v_actions);

    	      END LOOP module_loop;
    	      CLOSE c_module;

            -- Add pn_default_object_permissions for group
            -- Make entries in default_object_permissions table for each module for the new group
            OPEN c_object_type;
        	<<type_loop>>
        	LOOP

        		FETCH c_object_type INTO v_object_type, v_actions;
        		EXIT WHEN c_object_type%NOTFOUND;

        		INSERT INTO pn_default_object_permission (
        			space_id,
        		    object_type,
                    group_id,
                    actions)
        		VALUES (
        			i_space_id,
        			v_object_type,
                    io_group_id,
                    v_actions);

        	END LOOP type_loop;
        	CLOSE c_object_type;

            -- Apply default object permissions to the new group
            SECURITY.CREATE_SECURITY_PERMISSIONS(io_group_id, 'group', i_space_id, i_creator_person_id);

        END IF; -- if not principal group


    -- Caller passed a group_id, do an update of the group's properties.
    ELSE

        update pn_group set
            group_name = i_group_name,
            group_desc = i_group_desc,
            is_principal = i_is_principal,
            is_system_group = i_is_system_group
        where
            group_id = io_group_id;

    END IF;

    --We're going to do some denormalization here so that project spaces or business
    --spaces don't have to look into the groups to see if the everyone group has been
    --invited.  This way, they have an easy was to generate a portfolio.
    if (i_group_type_id = 600) then
        --First, determine what type of space we are using
        select object_type into v_space_type from pn_object where object_id = i_space_id;

        if (v_space_type = 'project') then
            update pn_project_space
            set includes_everyone = 1
            where project_id = i_space_id;
        elsif (v_space_type = 'business') then
            update pn_business_space
            set includes_everyone = 1
            where business_space_id = i_space_id;
        end if;
    end if;

    -- Handle Exceptions
    EXCEPTION
        when others then
        begin
           rollback;
           if c_module%isopen then
                close c_module;
           end if;
           if c_object_type%isopen then
                close c_object_type;
           end if;

            base.log_error(SQLCODE, SQLERRM, 'SECURITY.STORE_GROUP');
            raise;
        end;

END; -- Procedure STORE_GROUP


/**
 * Creates a Team Member group
 * Does not COMMIT or ROLLBACK
 * i_creator_id the person creating the group
 * i_space_id the space owning the group
 * o_group_id the generated group_id
 */
procedure create_teammember_group (
    i_creator_id in number,
    i_space_id in number,
    o_group_id out number
)
is

    v_group_id pn_group.group_id%type;

begin

    SECURITY.STORE_GROUP('@prm.security.group.type.teammember.name', '@prm.security.group.type.teammember.description', 0, 1, SECURITY.GROUP_TYPE_TEAMMEMBER, i_creator_id, i_space_id, v_group_id);
    o_group_id := v_group_id;

end create_teammember_group;

/**
 * Creates a principal group
 * Does not COMMIT or ROLLBACK
 * i_creator_id the person creating the group
 * i_space_id the space owning the group
 * o_group_id the generated group_id
 */
procedure create_principal_group (
    i_creator_id in number,
    i_space_id in number,
    o_group_id out number
)
is

    v_group_id pn_group.group_id%type;

begin

    SECURITY.STORE_GROUP('@prm.security.group.type.principal.name', '@prm.security.group.type.principal.decription', 1, 1, SECURITY.GROUP_TYPE_PRINCIPAL, i_creator_id, i_space_id, v_group_id);
    o_group_id := v_group_id;

end create_principal_group;


/**
 * Inherits a group into a space.
 * Does NOT COMMIT OR ROLLBACK
 * i_space_id the space into which the group is to be inherited
 * i_group_id the group to inherit
 * i_permission the type of permission to give
 */
procedure inherit_group (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
)
is
    v_already_inherited number := 0;

    -- Cursor to select all modules and their default permissions
    -- in the specified space where the module is active
    CURSOR module_cur (i_space_id number) IS
		SELECT shm.space_id, m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

    -- Cursor to select all object types and default permission actions
   	CURSOR object_type_cur IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;

begin

    begin
        select 1 into v_already_inherited
        from pn_space_has_group
        where
            space_id = i_space_id
        and group_id = i_group_id
        and is_owner = 0;
    exception
        when NO_DATA_FOUND then
            -- Suck up error.  v_already_inherited remains 0
            null;
    end;

    if (v_already_inherited = 0) then

        -- Add the group to PN_SPACE_HAS_GROUP
        -- Inherited groups are not owned by the space
        insert into pn_space_has_group
            (space_id, group_id, is_owner)
        values
            (i_space_id, i_group_id, 0);


        -- Now update the security settings for the newly inherited group
        if (i_permission = SECURITY.PERMISSION_SELECTION_NONE) then
            -- No permissions to be applied
            null;

        elsif (i_permission = SECURITY.PERMISSION_SELECTION_VIEW) then

            -- View permission only

            -- First create the module permissions
            for module_rec in module_cur(i_space_id) loop
                INSERT INTO pn_module_permission
                    (module_id, space_id, group_id, actions)
        		VALUES
                    (module_rec.module_id, module_rec.space_id, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
            end loop;

            -- Next create the default object permissions
            for object_type_rec in object_type_cur loop
                INSERT INTO pn_default_object_permission
                    (space_id, object_type, group_id, actions)
                VALUES
                    (i_space_id, object_type_rec.object_type, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
            end loop;

        else
            -- Default permissions

            -- First create the module permissions
            for module_rec in module_cur(i_space_id) loop
                INSERT INTO pn_module_permission
                    (module_id, space_id, group_id, actions)
        		VALUES
                    (module_rec.module_id, module_rec.space_id, i_group_id, module_rec.default_permission_actions);
            end loop;

            -- Next create the default object permissions
            for object_type_rec in object_type_cur loop
                INSERT INTO pn_default_object_permission
                    (space_id, object_type, group_id, actions)
                VALUES
                    (i_space_id, object_type_rec.object_type, i_group_id, object_type_rec.default_permission_actions);
            end loop;

        end if;

        -- Note:
        -- We do NOT apply permissions to the group itself in this space
        -- so that the group is entirely UN-EDITABLE in the space

    end if;

exception
    when others then
    begin
        BASE.LOG_ERROR('SECURITY.INHERIT_GROUP', sqlcode, sqlerrm);
        raise;
    end;

end inherit_group;

/**
 * Inherits a group from other space into a space.
 * Does NOT COMMIT OR ROLLBACK
 * i_space_id the space into which the group is to be inherited
 * i_src_space_id the space from which the group is inheritted
 * i_group_id the group to inherit
 * i_permission the type of permission to give
 */
procedure inherit_group_from_space (
    i_space_id in number,
    i_src_space_id number,
    i_group_id in number,
    i_permission in varchar2
)
is
    v_already_inherited number := 0;

    -- Cursor to select all modules and their default permissions
    -- in the specified space where the module is active
    CURSOR module_cur (i_space_id number) IS
		SELECT shm.space_id, m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

    -- Cursor to select all modules and their permissions from associated space
    CURSOR inr_module_cur (i_space_id number, i_src_space_id number, i_group_id number) IS
		SELECT pmp.space_id, m.module_id,  pmp.actions
		FROM pn_module m,  pn_module_permission pmp, pn_space_has_module shm where
            pmp.space_id = i_src_space_id
            and m.module_id = pmp.module_id
			and pmp.group_id = i_group_id
			and shm.space_id = pmp.space_id
            and m.module_id = shm.module_id
			and m.module_id in (select pn_space_has_module.module_id from pn_space_has_module where space_id = i_space_id  )
            and shm.is_active = 1;


    -- Cursor to select all object types and default permission actions
   	CURSOR object_type_cur IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;

begin

    begin
        select 1 into v_already_inherited
        from pn_space_has_group
        where
            space_id = i_space_id
        and group_id = i_group_id
        and is_owner = 0;
    exception
        when NO_DATA_FOUND then
            -- Suck up error.  v_already_inherited remains 0
            null;
    end;

    if (v_already_inherited = 0) then

        -- Add the group to PN_SPACE_HAS_GROUP
        -- Inherited groups are not owned by the space
        insert into pn_space_has_group
            (space_id, group_id, is_owner)
        values
            (i_space_id, i_group_id, 0);


        -- Now update the security settings for the newly inherited group
        if (i_permission = SECURITY.PERMISSION_SELECTION_NONE) then
            -- No permissions to be applied
            null;

        elsif (i_permission = SECURITY.PERMISSION_SELECTION_VIEW) then

            -- View permission only

            -- First create the module permissions
            for module_rec in module_cur(i_space_id) loop
                INSERT INTO pn_module_permission
                    (module_id, space_id, group_id, actions)
        		VALUES
                    (module_rec.module_id, module_rec.space_id, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
            end loop;

            -- Next create the default object permissions
            for object_type_rec in object_type_cur loop
                INSERT INTO pn_default_object_permission
                    (space_id, object_type, group_id, actions)
                VALUES
                    (i_space_id, object_type_rec.object_type, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
            end loop;

        elsif (i_permission = SECURITY.PERMISSION_SELECTION_INHERIT) then
            -- inherited permissions

            -- First create the module permissions
            for module_rec in inr_module_cur(i_space_id, i_src_space_id, i_group_id) loop
                INSERT INTO pn_module_permission
                    (module_id, space_id, group_id, actions)
        		VALUES
                    (module_rec.module_id, i_space_id, i_group_id, module_rec.actions);
			end loop;

            -- Next create the default object permissions
            for object_type_rec in object_type_cur loop
                INSERT INTO pn_default_object_permission
                    (space_id, object_type, group_id, actions)
                VALUES
                    (i_space_id, object_type_rec.object_type, i_group_id, object_type_rec.default_permission_actions);
            end loop;
		else
		 	-- Default permissions

            -- First create the module permissions
            for module_rec in module_cur(i_space_id) loop
                INSERT INTO pn_module_permission
                    (module_id, space_id, group_id, actions)
        		VALUES
                    (module_rec.module_id, module_rec.space_id, i_group_id, module_rec.default_permission_actions);
            end loop;

            -- Next create the default object permissions
            for object_type_rec in object_type_cur loop
                INSERT INTO pn_default_object_permission
                    (space_id, object_type, group_id, actions)
                VALUES
                    (i_space_id, object_type_rec.object_type, i_group_id, object_type_rec.default_permission_actions);
            end loop;
        end if;

    end if;

exception
    when others then
    begin
        BASE.LOG_ERROR('SECURITY.INHERIT_GROUP_FROM_SPACE', sqlcode, sqlerrm);
        raise;
    end;

end inherit_group_from_space;

procedure grant_module_permissions (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
)
is
    -- Cursor to select all modules and their default permissions
    -- in the specified space where the module is active
    CURSOR module_cur (i_space_id number) IS
		SELECT shm.space_id, m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;
begin
    if (i_permission = SECURITY.PERMISSION_SELECTION_NONE) then
        -- No permissions are to be available.  Remove existing ones.
        delete from pn_module_permission
        where space_id = i_space_id
          and group_id = i_group_id;
    elsif (i_permission = SECURITY.PERMISSION_SELECTION_VIEW) then
        --Delete any existing permission for this group -- we are going to replace them
        delete from pn_module_permission
        where space_id = i_space_id
          and group_id = i_group_id;

        for module_rec in module_cur(i_space_id) loop
            INSERT INTO pn_module_permission
                (module_id, space_id, group_id, actions)
    		VALUES
                (module_rec.module_id, module_rec.space_id, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
        end loop;
    else
        --Delete any existing permission for this group -- we are going to replace them
        delete from pn_module_permission
        where space_id = i_space_id
          and group_id = i_group_id;

        for module_rec in module_cur(i_space_id) loop
            INSERT INTO pn_module_permission
                (module_id, space_id, group_id, actions)
    		VALUES
                (module_rec.module_id, module_rec.space_id, i_group_id, module_rec.default_permission_actions);
        end loop;
    end if;
end grant_module_permissions;

procedure set_newobject_permissions (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
)
is
    -- Cursor to select all object types and default permission actions
   	CURSOR object_type_cur IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;

begin
    if (i_permission = SECURITY.PERMISSION_SELECTION_NONE) then
        -- No permissions granted on new objects for this group
        delete from pn_default_object_permission where group_id = i_group_id;

    elsif (i_permission = SECURITY.PERMISSION_SELECTION_VIEW) then
        -- View permission only

        -- First, remove any existing permissions for this group
        delete from pn_default_object_permission where group_id = i_group_id;

        -- Create the default object permissions
        for object_type_rec in object_type_cur loop
            INSERT INTO pn_default_object_permission
                (space_id, object_type, group_id, actions)
            VALUES
                (i_space_id, object_type_rec.object_type, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
        end loop;

    else
        -- Default permissions

        -- First, remove any existing permissions for this group
        delete from pn_default_object_permission where group_id = i_group_id;

        -- Create the default object permissions
        for object_type_rec in object_type_cur loop
            INSERT INTO pn_default_object_permission
                (space_id, object_type, group_id, actions)
            VALUES
                (i_space_id, object_type_rec.object_type, i_group_id, object_type_rec.default_permission_actions);
        end loop;

    end if;
end set_newobject_permissions;

procedure retrofit_security_permissions (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
)
is
    --Cursor to select all of the existing objects in the space which are secured
    CURSOR existing_objects(i_space_id number) is
        select obj.object_id, obj.object_type, ot.default_permission_actions
        from pn_object_space os, pn_object obj, pn_object_type ot
        where os.object_id = obj.object_id
          and obj.object_type = ot.object_type
          and ot.is_securable=1
          and space_id = i_space_id;
begin
    if (i_permission = SECURITY.PERMISSION_SELECTION_NONE) then
        --Nothing to do, the caller didn't really want permissions
        null;
    elsif (i_permission = SECURITY.PERMISSION_SELECTION_VIEW) then
        for existing_obj in existing_objects(i_space_id) loop
            if (existing_obj.object_id <> i_group_id) then
                INSERT INTO pn_object_permission
                (object_id, group_id, actions)
                values
                (existing_obj.object_id, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
            end if;
        end loop;
    else
        --Default permissions
        for existing_obj in existing_objects(i_space_id) loop
            if (existing_obj.object_id <> i_group_id) then
                INSERT INTO pn_object_permission
                (object_id, group_id, actions)
                values
                (existing_obj.object_id, i_group_id, existing_obj.default_permission_actions);
            end if;
        end loop;
    end if;
end retrofit_security_permissions;

/**
 * Removes an inherited group.
 * i_space_id the space from which to remove the group
 * i_group_id the group that is inherited in that space
 */
procedure remove_inherited_group (
    i_space_id in number,
    i_group_id in number
)
is

begin

    -- Remove group from space
    -- Check for "is_owner=0" a safety feature
    -- to ensure this is not be called for an owned group
    delete from pn_space_has_group
    where
        space_id = i_space_id
    and group_id = i_group_id
    and is_owner = 0;

    -- Now remove permissions
    DELETE FROM pn_default_object_permission
        WHERE
            space_id = i_space_id
        and group_id = i_group_id;

    DELETE FROM pn_module_permission
        WHERE
            space_id = i_space_id
        and group_id = i_group_id;

    -- Page permissions?
    -- 11/12/2001 - Tim - Copied from another stored procedure
    -- this appears to be entirely obsolete
    -- no harm keeping it here though
    DELETE FROM pn_page_permission
        WHERE
            space_id = i_space_id
        and group_id = i_group_id;

    -- Note: we cannot remove object permissions
    -- since those permissions are not stored by space
    -- Therefore, if a de-inherited group is re-inherited
    -- it will retain all existing permissions
    -- Is this good or bad?


    -- Now remove this group as a member from other groups
    -- in this space
    delete from pn_group_has_group ghg
        where exists (
            select 1 from
                pn_space_has_group shg
            where
                shg.space_id = i_space_id
            and shg.group_id = ghg.group_id)
        and ghg.member_group_id = i_group_id;


end;


-------------------------------------------------------------------------------
-- COPY_GROUPS
--
-- copy all the groups (roles) from the source
-- space to the destination space.
-- This excludes System groups (Space Admin, Team Member, Principle); that is
-- only copies User-defined groups.
-- This is because Space Admin and Team Member are already created in the space,
-- Principle groups are created when a user is invited to the space
-- Only copies Owned groups currently (non-owned groups shouldn't be copied
-- and it may be to dangerous to propogated inherited groups)
-------------------------------------------------------------------------------
PROCEDURE COPY_GROUPS
(
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    i_created_by_id     in varchar2,
    o_return_value      OUT number
)
    is
        pragma autonomous_transaction;

        -- Cursor to read all groups for a given space
        cursor group_cur (i_space_id in number)
        is
            select g.group_id , group_name, group_desc, group_type_id
            from pn_space_has_group shg, pn_group g
            where shg.space_id = i_space_id
            and g.group_id = shg.group_id
            and (g.is_system_group = 0 or g.group_type_id = 600)
            and shg.is_owner = 1;

        group_rec           group_cur%rowtype;
        v_group_id          pn_group.group_id%type := null;
        v_from_space_id     pn_space_has_class.space_id%type := to_number(i_from_space_id);
        v_to_space_id       pn_space_has_class.space_id%type := to_number(i_to_space_id);
        v_created_by_id     number := to_number(i_created_by_id);

    begin

        for group_rec in group_cur(i_from_space_id)
        loop
            v_group_id := null;
            STORE_GROUP(
                group_rec.group_name,
                group_rec.group_desc,
                0,
                0,
                group_rec.group_type_id,
                v_created_by_id,
                v_to_space_id,
                v_group_id);

                COPY_MOD_PERM_USER_DEF_GROUPS (
                    group_rec.group_id ,
                    v_group_id ,
                    i_from_space_id ,
                    i_to_space_id ,
                    o_return_value ) ;


                COPY_DEF_PERM_USER_DEF_GROUPS (
                    group_rec.group_id ,
                    v_group_id ,
                    i_from_space_id ,
                    i_to_space_id ,
                    o_return_value ) ;

        end loop;

        commit;
        if group_cur%isopen then
            close group_cur;
        end if;

        o_return_value := base.operation_successful;

    exception
        when others then
        begin
           --dbms_output.put_line('copy_all Error '||TO_CHAR(SQLCODE)||': '||SQLERRM);
           if group_cur%isopen then
                close group_cur;
           end if;
           rollback;
        end;

    end COPY_GROUPS;


-------------------------------------------------------------------------------
-- COPY_MODULE_PERMISSIONS
--
-- copy all the module permissions for non-principal groups from the source
-- space t the destination space.
-------------------------------------------------------------------------------
Procedure COPY_MODULE_PERMISSIONS
(
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    i_created_by_id     in varchar2,
    o_return_value      OUT number
)
IS

    v_from_space_id     pn_space_has_class.space_id%type := to_number(i_from_space_id);
    v_to_space_id       pn_space_has_class.space_id%type := to_number(i_to_space_id);
    v_created_by_id     number := to_number(i_created_by_id);
    v_to_group_id       pn_group.group_id%type;
    v_business_admin_id pn_group.group_id%type;

    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.COPY_MODULE_PERMISSIONS';

    -- All module permissions for the space for non-principal groups
    CURSOR  c_module_permissions (i_space_id number) IS
        select g.group_type_id , mp.module_id, mp.actions
        from pn_module_permission mp, pn_group g
        where mp.space_id = i_space_id
        and g.group_id = mp.group_id
        and g.group_type_id <> SECURITY.GROUP_TYPE_USERDEFINED
        and g.group_type_id <> SECURITY.GROUP_TYPE_EVERYONE
        and g.is_principal <> 1;

    module_rec  c_module_permissions%rowtype;

BEGIN

    -- Locate the group id of the owning space's
    -- Space Administrator group;  this will be the
    -- space id of the business space admin group
    -- We need this in order to avoid trying to change
    -- permissions for it later
     BEGIN
        select g.group_id into v_business_admin_id
        from
            pn_space_has_space shs,
            pn_group g,
            pn_space_has_group shg
        where
            shs.child_space_id = i_to_space_id
        and shs.relationship_parent_to_child = 'owns'
        and shg.space_id  = shs.parent_space_id
        and g.group_type_id = SECURITY.GROUP_TYPE_SPACEADMIN
        and g.group_id = shg.group_id
        and shg.is_owner = 1;
    EXCEPTION
      WHEN no_data_found THEN
      BEGIN
        v_business_admin_id := null;
      END;
    END;


    for module_rec in c_module_permissions(i_from_space_id)
    loop
        -- get the group_id for the destination space's group that matches the source group's name.
        -- Note that if there is a business administrator group in this
        -- space, then we ignore it as a group
        if (v_business_admin_id is not null) then
            select g.group_id into v_to_group_id
            from pn_space_has_group shg, pn_group g
            where shg.space_id = i_to_space_id
            and g.group_id = shg.group_id
            and g.group_type_id = module_rec.group_type_id
            and g.group_id <> v_business_admin_id;
        else
            select g.group_id into v_to_group_id
            from pn_space_has_group shg, pn_group g
            where shg.space_id = i_to_space_id
            and g.group_id = shg.group_id
            and g.group_type_id = module_rec.group_type_id;
        end if;

        -- update the module permissions for the destination space's group.
        update pn_module_permission
        set actions = module_rec.actions
        where space_id = v_to_space_id
        and group_id = v_to_group_id
        and module_id = module_rec.module_id;
    end loop;

    commit;
    if c_module_permissions%isopen then
        close c_module_permissions;
    end if;
    o_return_value := base.operation_successful;
END COPY_MODULE_PERMISSIONS;



-------------------------------------------------------------------------------
-- COPY_DEFAULT_PERMISSIONS
--
-- copy all the default object permissions for non-principal groups from the source
-- space to the destination space.
-------------------------------------------------------------------------------
Procedure COPY_DEFAULT_PERMISSIONS
(
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    i_created_by_id     in varchar2,
    o_return_value      OUT number
)
IS

    v_from_space_id     pn_space_has_class.space_id%type := to_number(i_from_space_id);
    v_to_space_id       pn_space_has_class.space_id%type := to_number(i_to_space_id);
    v_created_by_id     number := to_number(i_created_by_id);
    v_to_group_id       pn_group.group_id%type;
    v_business_admin_id pn_group.group_id%type;

    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.COPY_DEFAULT_PERMISSIONS';

    -- All default object permissions for the space for non-principal groups
    CURSOR  c_default_permissions (i_space_id number) IS
        select g.group_type_id , op.object_type, op.actions
        from pn_default_object_permission op, pn_group g
        where op.space_id = i_space_id
        and g.group_id = op.group_id
        and g.group_type_id <> SECURITY.GROUP_TYPE_USERDEFINED
        and g.group_type_id <> SECURITY.GROUP_TYPE_EVERYONE
        and g.is_principal <> 1;

    permission_rec  c_default_permissions%rowtype;

BEGIN

    -- Locate the group id of the owning space's
    -- Space Administrator group;  this will be the
    -- space id of the business space admin group
    -- We need this in order to avoid trying to change
    -- permissions for it later
     BEGIN
        select g.group_id into v_business_admin_id
        from
            pn_space_has_space shs,
            pn_group g,
            pn_space_has_group shg
        where
            shs.child_space_id = i_to_space_id
        and shs.relationship_parent_to_child = 'owns'
        and shg.space_id  = shs.parent_space_id
        and g.group_type_id = SECURITY.GROUP_TYPE_SPACEADMIN
        and g.group_id = shg.group_id
        and shg.is_owner = 1;
    EXCEPTION
      WHEN no_data_found THEN
      BEGIN
        v_business_admin_id := null;
      END;
    END;

    for permission_rec in c_default_permissions(i_from_space_id)
    loop
        -- get the group_id for the destination space's group that matches the source group's name.
        -- Note that if there is a business administrator group in this
        -- space, then we ignore it as a group
        if (v_business_admin_id is not null) then
            select g.group_id into v_to_group_id
            from pn_space_has_group shg, pn_group g
            where shg.space_id = i_to_space_id
            and g.group_id = shg.group_id
            and g.group_type_id = permission_rec.group_type_id
            and g.group_id <> v_business_admin_id;
        else
            update pn_default_object_permission
            set actions = permission_rec.actions
            where space_id = v_to_space_id
            and group_id = v_to_group_id
            and object_type = permission_rec.object_type;
        end if;

    end loop;

    commit;
    if c_default_permissions%isopen then
        close c_default_permissions;
    end if;
    o_return_value := base.operation_successful;

    -- Log exceptions
    EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        --dbms_output.put_line('COPY_DEFAULT_PERMISSIONS Error '||TO_CHAR(SQLCODE)||': '||SQLERRM);
        if c_default_permissions%isopen then
            close c_default_permissions;
        end if;
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        o_return_value := SQLCODE;
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END COPY_DEFAULT_PERMISSIONS;

/**
 * This procedure copies the module permissions for the User Defined Group.
 */

Procedure COPY_MOD_PERM_USER_DEF_GROUPS
(
    i_from_group_id     in varchar2,
    i_to_group_id       in varchar2,
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    o_return_value      OUT number
)
IS
    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.COPY_MODULE_PERM_USER_DEF_GROUPS';
    v_module_id pn_module_permission.module_id%type;
    v_actions pn_module_permission.actions%type;

    CURSOR  c_module_permissions IS
        select mp.module_id , mp.actions
        from pn_module_permission mp , pn_group g
        where mp.group_id = i_from_group_id
        and mp.space_id =  i_from_space_id
        and g.group_id = mp.group_id
        and g.group_type_id = SECURITY.GROUP_TYPE_USERDEFINED;

    module_rec  c_module_permissions%rowtype;

BEGIN

       for module_rec in c_module_permissions
       loop

        -- update the module permissions for the destination space's group.

            update pn_module_permission
            set actions = module_rec.actions
            where space_id = i_to_space_id
            and group_id = i_to_group_id
            and module_id = module_rec.module_id;

       end loop;

    o_return_value := base.operation_successful;
    -- Log exceptions
    EXCEPTION

    WHEN OTHERS THEN
      BEGIN

        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        o_return_value := SQLCODE;
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END COPY_MOD_PERM_USER_DEF_GROUPS;

/**
 * This procedure copies the default permissions for the User Defined Group.
 */

Procedure COPY_DEF_PERM_USER_DEF_GROUPS
(
    i_from_group_id     in varchar2,
    i_to_group_id       in varchar2,
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    o_return_value      OUT number
)
IS
    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.COPY_MODULE_PERM_USER_DEF_GROUPS';
    v_module_id pn_module_permission.module_id%type;
    v_actions pn_module_permission.actions%type;

    CURSOR  c_default_permissions IS

        select g.group_type_id , op.object_type, op.actions
        from pn_default_object_permission op, pn_group g
        where op.space_id = i_from_space_id
        and g.group_id = i_from_group_id
        and g.group_id = op.group_id
        and g.group_type_id = SECURITY.GROUP_TYPE_USERDEFINED;

    permission_rec  c_default_permissions%rowtype;

BEGIN

       for permission_rec in c_default_permissions
       loop

        -- update the default object permissions for the destination space's group.

            update pn_default_object_permission
            set actions = permission_rec.actions
            where space_id = i_to_space_id
            and group_id = i_to_group_id
            and object_type = permission_rec.object_type;

       end loop;

    o_return_value := base.operation_successful;
    -- Log exceptions
    EXCEPTION

    WHEN OTHERS THEN
      BEGIN

        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        o_return_value := SQLCODE;
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
      END;

END COPY_DEF_PERM_USER_DEF_GROUPS;


END; -- Package Body SECURITY
/

CREATE OR REPLACE PACKAGE security IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Robin       06-Apr-00  Creation.
-- Roger       06-Apr-00  Moved create_security_permissions to package.
-- Robin       24-Apr-00  Added function create_space_admin_group and store_group to package.
-- Robin       11-May-00  Changed error codes to coincide with new table.
-- Roger Bly    1/7/01    Added procedures for copying groups and permissions to another space.

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

-- Constants for group_types
GROUP_TYPE_USERDEFINED constant number := 100;
GROUP_TYPE_SPACEADMIN constant number := 200;
GROUP_TYPE_TEAMMEMBER constant number := 300;
GROUP_TYPE_PRINCIPAL constant number := 400;
GROUP_TYPE_POWERUSER constant number := 500;
GROUP_TYPE_EVERYONE constant number := 600;

-- Constants for specifying the permission to store when inheriting
-- see inherit_group
PERMISSION_SELECTION_NONE constant varchar2(100) := 'none';
PERMISSION_SELECTION_VIEW constant varchar2(100) := 'view';
PERMISSION_SELECTION_DEFAULT constant varchar2(100) := 'default';
PERMISSION_SELECTION_INHERIT constant varchar2(100) := 'inherit';
-- View permission bitmask
VIEW_PERMISSION_BITMASK constant number := 1;

APPLICATION_SPACE_ID constant number := 4;


PROCEDURE CREATE_SECURITY_PERMISSIONS
(
    p_object_id IN pn_object.object_id%type,
    p_object_type IN pn_object_type.object_type%type,
    p_space_id IN pn_project_space.project_id%type,
    p_person_id IN pn_person.person_id%type
);

PROCEDURE APPLY_DOCUMENT_PERMISSIONS
(
    p_object_id IN pn_object.object_id%type,
    p_parent_id IN pn_object.object_id%type,
    p_object_type IN pn_object_type.object_type%type,
    p_space_id IN pn_project_space.project_id%type,
    p_person_id IN pn_person.person_id%type
);

PROCEDURE add_person_to_group
(
    group_id IN pn_group.group_id%type,
    person_id IN pn_person.person_id%type,
    o_status OUT NUMBER
);

PROCEDURE store_module_permission
(
    i_space_id IN NUMBER,
    i_group_id IN NUMBER,
    i_module_id IN NUMBER,
    i_actions IN NUMBER,
    o_status OUT NUMBER
);

PROCEDURE store_object_permission
(
    i_object_id IN NUMBER,
    i_group_id IN NUMBER,
    i_actions IN NUMBER,
    o_status OUT NUMBER
);

PROCEDURE store_default_obj_permission
(
    i_space_id IN NUMBER,
    i_object_type IN VARCHAR2,
    i_group_id IN NUMBER,
    i_actions IN NUMBER,
    o_status OUT NUMBER
);

PROCEDURE remove_group_permission
(
    i_group_id IN NUMBER,
    o_status OUT NUMBER
);

FUNCTION f_create_space_admin_group
(
    i_creator_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_description IN VARCHAR
)
RETURN  NUMBER;

FUNCTION f_create_power_user_group
(
    i_creator_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_description IN VARCHAR
)
RETURN  NUMBER;

procedure apply_admin_permissions
(
    i_new_group_id IN NUMBER,
    i_space_admin_group_id IN NUMBER
);

FUNCTION create_parent_admin_role
(
    i_space_id IN NUMBER,
    i_parent_space_id IN NUMBER
)
RETURN  NUMBER;


Procedure store_group
(
    i_group_name IN VARCHAR2,
    i_group_desc IN VARCHAR2,
    i_is_principal IN NUMBER,
    i_is_system_group IN NUMBER,
    i_group_type_id in number,
    i_creator_person_id IN NUMBER,
    i_space_id IN NUMBER,
    io_group_id IN OUT NUMBER
);

procedure create_teammember_group (
    i_creator_id in number,
    i_space_id in number,
    o_group_id out number
);

procedure create_principal_group (
    i_creator_id in number,
    i_space_id in number,
    o_group_id out number
);

procedure inherit_group (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
);

procedure inherit_group_from_space (
    i_space_id in number,
	i_src_space_id number,
    i_group_id in number,
    i_permission in varchar2
);

procedure grant_module_permissions (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
);

procedure set_newobject_permissions (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
);

procedure retrofit_security_permissions (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
);

procedure remove_inherited_group (
    i_space_id in number,
    i_group_id in number
);

PROCEDURE COPY_GROUPS
(
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    i_created_by_id     in varchar2,
    o_return_value      OUT number
);


Procedure COPY_MODULE_PERMISSIONS
(
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    i_created_by_id     in varchar2,
    o_return_value      OUT number
);


Procedure COPY_DEFAULT_PERMISSIONS
(
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    i_created_by_id     in varchar2,
    o_return_value      OUT number
);

Procedure COPY_MOD_PERM_USER_DEF_GROUPS
(
    i_from_group_id     in varchar2,
    i_to_group_id       in varchar2,
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    o_return_value      OUT number
);

Procedure COPY_DEF_PERM_USER_DEF_GROUPS
(
    i_from_group_id     in varchar2,
    i_to_group_id       in varchar2,
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    o_return_value      OUT number
);

END; -- Package Specification SECURITY
/

CREATE OR REPLACE PACKAGE BODY sharing
IS
--
-- To modify this template, edit file PKGBODY.TXT in TEMPLATE
-- directory of SQL Navigator
--
-- Purpose: Briefly explain the functionality of the package body
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Sachin      10-Jul-07  Added a function.


--------------------------------------------------------------------
-- GET_PROPAGATE_TO_CHILDREN
--------------------------------------------------------------------

   FUNCTION GET_PROPAGATE_TO_CHILDREN (
       in_plan_id NUMBER
   ) return NUMBER
   AS
        v_propagate NUMBER;
   BEGIN
     SELECT
       COUNT(*) into v_propagate
     FROM
       pn_shareable
     WHERE
       object_id = in_plan_id
       and propagate_to_children = 1;

     IF (v_propagate > 0) THEN
            RETURN 1;
     END IF;
     RETURN v_propagate;
   END;

   PROCEDURE STORE_SHARE (
       i_object_id in NUMBER,
       i_permission_type in NUMBER,
       i_container_id in NUMBER,
       i_space_id in NUMBER,
       i_propagate_to_children in NUMBER,
       i_allowable_actions in NUMBER
   ) IS
      -- Enter the procedure variables here. As shown below
      share_exists number;
   BEGIN
      select count(object_id) into share_exists from pn_shareable where object_id = i_object_id;

      if (share_exists > 0) then
         update pn_shareable
         set object_id = i_object_id,
             permission_type = i_permission_type,
             container_id = i_container_id,
             space_id = i_space_id,
             propagate_to_children = i_propagate_to_children,
             allowable_actions = i_allowable_actions
         where
             object_id = i_object_id;
      else
         insert into pn_shareable
            (object_id, permission_type, container_id, space_id, propagate_to_children, allowable_actions)
         values
            (i_object_id, i_permission_type, i_container_id, i_space_id, i_propagate_to_children, i_allowable_actions);
      end if;
   END;

   PROCEDURE INHERIT_SHARE_FROM_PLAN (
       i_plan_id in NUMBER
   ) IS
     v_permission_type NUMBER;
     v_space_id NUMBER;
   BEGIN
     --First, find the permission type and space type that the plan currently has.
     select
       permission_type, space_id into v_permission_type, v_space_id
     from
       pn_shareable
     where
       object_id = i_plan_id;

     --If the sharing type for the schedule is more permissive than the sharing type
     --for the task, we'll use the plan's sharing type.
     if (v_permission_type = 1) then
       delete from pn_shareable_permissions sp
       where exists
         (select 1 from pn_plan_has_task pht
          where pht.plan_id = i_plan_id
            and pht.task_id = sp.object_id);

       update pn_shareable shrd
       set permission_type = v_permission_type
       where exists
         (select 1 from pn_plan_has_task pht
          where pht.plan_id = i_plan_id
            and pht.task_id = shrd.object_id);
     elsif (v_permission_type = 2) then
       --Add any additional permissions that the schedule has to the object's
       --permissions
       insert into pn_shareable_permissions
       (object_id, permitted_object_id, share_type)
       select
         pht.task_id, sp.permitted_object_id, sp.share_type
       from
         pn_plan_has_task pht,
         pn_shareable_permissions sp
       where
         sp.object_id = i_plan_id
         and pht.plan_id = i_plan_id
         and not exists
           (select 1 from pn_shareable_permissions sp2
            where object_id = pht.task_id
              and sp.permitted_object_id = sp2.permitted_object_id);
     end if;

     --Insert into shareable any objects that didn't already have sharing enabled
     insert into pn_shareable
     (object_id, permission_type, container_id, space_id)
     select
       pht.task_id, v_permission_type, i_plan_id, v_space_id
     from
       pn_plan_has_task pht,
       pn_object o
     where
       pht.task_id = o.object_id
       and o.record_status = 'A'
       and plan_id = i_plan_id
       and not exists (select 1 from pn_shareable where object_id = pht.task_id);
   END;

   PROCEDURE INHERIT_FROM_PARENT (
       i_parent_id in NUMBER,
       i_child_id in NUMBER
   )
   IS
   BEGIN
     insert into pn_shareable
       (object_id, permission_type, container_id, space_id, propagate_to_children)
     select
       i_child_id, permission_type, container_id, space_id, propagate_to_children
     from
       pn_shareable
     where
       object_id = i_parent_id
       and propagate_to_children = 1;
   END;

   PROCEDURE ADD_EXTERNAL (
       i_exported_object_id NUMBER,
       i_export_container_id NUMBER,
       i_import_container_id NUMBER,
       i_import_space_id NUMBER,
       i_imported_object_id NUMBER,
       i_read_only NUMBER
   ) IS
      export_exists number;
      export_space_id number;
   BEGIN
      select count(i_imported_object_id) into export_exists
      from
        pn_shared
      where
        exported_object_id = i_exported_object_id
        and import_container_id = i_import_container_id;

      if (export_exists > 0) then
        update pn_shared
        set import_space_id = i_import_space_id,
            imported_object_id = i_imported_object_id,
            read_only = i_read_only
        where
            exported_object_id = i_exported_object_id
            and import_container_id = i_import_container_id;
      else
        --Find the space id of the exporting object
        select space_id into export_space_id
        from pn_shareable
        where object_id = i_exported_object_id;

        insert into pn_shared
          (exported_object_id, import_container_id, import_space_id, imported_object_id, export_space_id, export_container_id, read_only)
        values
          (i_exported_object_id, i_import_container_id, i_import_space_id, i_imported_object_id, export_space_id, i_export_container_id, i_read_only);
      end if;
   END;

END;
/

CREATE OR REPLACE PACKAGE sharing IS
   DEFAULT_PROPAGATION NUMBER := 0;
   DEFAULT_ALLOWABLE_ACTIONS NUMBER := 2;

   -- Returns of we have to propagate permissions to children
   FUNCTION GET_PROPAGATE_TO_CHILDREN (
       in_plan_id NUMBER
   ) return NUMBER;

   -- The sharing package is used to facilitate cross space sharing.  It allows
   -- objects to be "shared" from one space so others can see the share.
   PROCEDURE STORE_SHARE (
       i_object_id in NUMBER,
       i_permission_type in NUMBER,
       i_container_id in NUMBER,
       i_space_id in NUMBER,
       i_propagate_to_children in NUMBER,
       i_allowable_actions in NUMBER
   );

   --Set all tasks shared that can be shared.  (That is, tasks that are not
   --already shared from another schedule -- transitive sharing is not allowed.)
   --Tasks will get their plan's permission type and their permissions.
   PROCEDURE INHERIT_SHARE_FROM_PLAN (
       i_plan_id in NUMBER
   );

   PROCEDURE INHERIT_FROM_PARENT (
       i_parent_id in NUMBER,
       i_child_id in NUMBER
   );

   PROCEDURE ADD_EXTERNAL (
       i_exported_object_id NUMBER,
       i_export_container_id NUMBER,
       i_import_container_id NUMBER,
       i_import_space_id NUMBER,
       i_imported_object_id NUMBER,
       i_read_only NUMBER
   );
END; -- Package spec
/

CREATE OR REPLACE PACKAGE BODY space IS


Procedure add_member
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_invitation_code IN number,
    o_status OUT NUMBER
) IS

    stored_proc_name VARCHAR2(100):= 'space.add_member';

    v_timestamp DATE := SYSDATE;

BEGIN

        INSERT INTO pn_space_has_person
            (space_id, person_id, relationship_person_to_space, relationship_space_to_person,
             responsibilities, record_status)
          SELECT i_space_id, i_person_id, 'member', 'has', invitee_responsibilities, 'A'
             FROM pn_invited_users
             WHERE invitation_code = i_invitation_code;

        IF SQL%NOTFOUND THEN
            o_status := no_data;
            RETURN;
        END IF;


EXCEPTION
    WHEN NO_DATA_FOUND THEN
        o_status := no_data;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_null_constraint THEN
        o_status := null_field;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_check_constraint THEN
        o_status := check_violated;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_unique_constraint THEN
        o_status := dupe_key;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);
    WHEN e_no_parent_key THEN
        o_status := no_parent_key;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN OTHERS THEN
        o_status := generic_error;

        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

END add_member;



Procedure accept_invitation
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_invitation_code IN number
) IS

    v_timestamp DATE := SYSDATE;
    v_status NUMBER;
    v_group_id pn_group.group_id%TYPE;

BEGIN

    -- now that the user has accepted, add them to the team member group
    -- and create a "Principle Group" for this space

        INSERT INTO pn_group_has_person
            (group_id, person_id)
          SELECT g.group_id, i_person_id
             FROM pn_group g, pn_space_has_group sg
             WHERE space_id = i_space_id
               AND g.group_type_id = SECURITY.GROUP_TYPE_TEAMMEMBER
               AND g.group_id = sg.group_id;

        -- portfolio
        INSERT INTO pn_portfolio_has_space
            (portfolio_id, space_id)
          SELECT membership_portfolio_id, i_space_id
             FROM pn_person
             WHERE person_id = i_person_id;

        -- principal group
        v_group_id := NULL;
        security.create_principal_group(i_person_id, i_space_id, v_group_id);

        v_status := NULL;
        security.add_person_to_group(v_group_id, i_person_id, v_status);

     -- now updated the users status information for the space
     UPDATE pn_invited_users
       SET date_responded = v_timestamp, invited_status = ACCEPTED_INVITATION_RESPONSE , invitation_acted_upon = '1'
     WHERE invitation_code = i_invitation_code
         AND space_id = i_space_id and person_id = i_person_id ;

    -- finally, update the assignment
    UPDATE pn_assignment
    set status_id = 20 --accepted
    where space_id = i_person_id
    and person_id = i_person_id
    and object_id = i_space_id;

EXCEPTION
    WHEN e_unique_constraint THEN
        -- Quietly proceed; this handling moved from Java code
        -- Accepting an invitation twice is permitted
        null;

END accept_invitation;



Procedure decline_invitation
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_invitation_code IN number,
    o_status OUT NUMBER
) IS

    stored_proc_name VARCHAR2(100):= 'space.decline_invitation';

    v_timestamp DATE := SYSDATE;


BEGIN

     -- now updated the users status information for the space
     UPDATE pn_invited_users
       SET date_responded = v_timestamp, invited_status = DECLINED_INVITATION_RESPONSE , invitation_acted_upon ='1'
     WHERE invitation_code = i_invitation_code
         AND space_id = i_space_id and person_id = i_person_id ;

    IF SQL%NOTFOUND THEN
        o_status := no_data;
        ROLLBACK;
        RETURN;
    END IF;

    -- finally, update the assignment
    UPDATE pn_assignment
    set status_id = 70 -- rejected
    where space_id = i_person_id
    and person_id = i_person_id
    and object_id = i_space_id;

    -- decline any tasks associated to this user that reside in
    -- the space we just deleted.
    UPDATE pn_assignment
    set status_id = 70
    where space_id = i_space_id
    and person_id = i_person_id;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        o_status := no_data;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_null_constraint THEN
        o_status := null_field;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_check_constraint THEN
        o_status := check_violated;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_unique_constraint THEN
        o_status := dupe_key;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);
    WHEN e_no_parent_key THEN
        o_status := no_parent_key;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN OTHERS THEN
        o_status := generic_error;

        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

END; -- decline invite


Procedure invite_person
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER ,
    i_email IN VARCHAR2,
    i_firstname IN VARCHAR2,
    i_lastname IN VARCHAR2,
    i_responsibilities IN VARCHAR2,
    i_invitor_id IN NUMBER,
    i_invite_status IN VARCHAR2,
    o_invitation_code OUT NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'space.invite_person';
v_sysdate DATE;

BEGIN

    SELECT sysdate INTO v_sysdate FROM dual;

    INSERT INTO pn_invited_users
        (invitation_code, space_id, person_id , invitee_email, invitee_firstname, invitee_lastname,
         invitee_responsibilities, invitor_id, date_invited, date_responded, invited_status)
      VALUES
        (pn_invite_sequence.nextval, i_space_id, i_person_id , i_email, i_firstname, i_lastname,
         i_responsibilities, i_invitor_id, v_sysdate, null, i_invite_status);

    SELECT pn_invite_sequence.currval INTO o_invitation_code FROM dual;


    COMMIT;
    o_status := success;

EXCEPTION

    WHEN e_null_constraint THEN
        ROLLBACK;
        o_status := null_field;
                err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_check_constraint THEN
        ROLLBACK;
        o_status := check_violated;
                err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_status := dupe_key;
                err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_status := no_parent_key;
                err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN OTHERS THEN
        o_status := generic_error;
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

END;

END; -- package body
/

CREATE OR REPLACE PACKAGE space IS
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Phil        7/1/00     Creation.

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);


DECLINED_INVITATION_RESPONSE VARCHAR2(20) := 'Rejected';
ACCEPTED_INVITATION_RESPONSE VARCHAR2(20) := 'Accepted';
DELETED_INVITATION_RESPONSE VARCHAR2(20) := 'Deleted';
INVITED_INVITATION_RESPONSE VARCHAR2(20) := 'Invited';



TYPE ReferenceCursor            IS REF CURSOR;


Procedure add_member
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_invitation_code IN number,
    o_status OUT NUMBER
);

Procedure accept_invitation
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_invitation_code IN number
);

Procedure decline_invitation
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_invitation_code IN number,
    o_status OUT NUMBER
);

Procedure invite_person
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER ,
    i_email IN VARCHAR2,
    i_firstname IN VARCHAR2,
    i_lastname IN VARCHAR2,
    i_responsibilities IN VARCHAR2,
    i_invitor_id IN NUMBER,
    i_invite_status IN VARCHAR2,
    o_invitation_code OUT NUMBER,
    o_status OUT NUMBER
);

END; -- Package spec
/

CREATE OR REPLACE PACKAGE BODY task IS
-- Purpose: Repository for TASK Related procedures
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
   -- Deepak   11-Oct-01 Added Log event procedure , more later

----------------------------------------------------------------------

----------------------------------------------------------------------
-- LOGS_EVENT
----------------------------------------------------------------------
PROCEDURE log_event
(
    task_id IN varchar2,
    whoami IN varchar2,
    action IN varchar2,
    action_name IN varchar2,
    notes IN varchar2
)
IS
    v_task_id     pn_task.task_id%type := TO_NUMBER(task_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_task_history.task_history_id%type;
    v_action          pn_task_history.action%type := action;
    v_action_name     pn_task_history.action_name%type := action_name;
    v_action_comment  pn_task_history.action_comment%type := notes;

BEGIN
    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- insert a history record for this document

    insert into pn_task_history (
        task_id,
        task_history_id,
        action,
        action_name,
        action_by_id,
        action_date,
        action_comment)
    values (
        v_task_id,
        v_history_id,
        v_action,
        v_action_name,
        v_whoami,
        SYSDATE,
        v_action_comment
    );
END;  -- Procedure LOG_EVENT
----------------------------------------------------------------------

----------------------------------------------------------------------


   -- Enter further code below as specified in the Package spec.
END;
/

CREATE OR REPLACE PACKAGE task IS

-- Purpose: Repository for TASK Related procedures
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
   -- Deepak   11-Oct-01 Added Log event procedure , more later

    -- Package constants
   G_task_object_type           constant pn_object.object_type%type := 'task';

   -- Raise-able errors
   unspecified_error  exception;
   pragma exception_init(unspecified_error, -20000);
   space_not_found    exception;

----------------------------------------------------------------------
-- LOG_EVENT
-- Logs the event that happens to the NEWS item
----------------------------------------------------------------------
   PROCEDURE log_event
    (
        task_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    );
----------------------------------------------------------------------

END; -- Package spec
/

CREATE OR REPLACE PACKAGE BODY TIMESHEET IS
-- Purpose: Provides procedures for creating and modifying timesheets

   PROCEDURE STORE_TIMESHEET
   (
        i_timesheet_id IN NUMBER,
        i_person_id IN NUMBER,
        i_approve_reject_by_id IN NUMBER,
        i_start_date IN DATE,
        i_end_date IN DATE,
        i_work IN NUMBER,
        i_work_units IN NUMBER,
        i_date_submitted IN DATE,
        i_date_approve_reject IN DATE,
        i_record_status IN CHAR,
        i_status_id IN NUMBER,
        i_comments IN VARCHAR2,
        o_timesheet_id OUT NUMBER
   )

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Sachin      12-Mar-07  Creation.


IS

v_timesheet_id NUMBER(20);

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100) := 'STORE_TIMESHEET';

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);


BEGIN
-- NEW TIMESHEET, INSERT
    IF ((i_timesheet_id IS NULL) OR (i_timesheet_id = ''))    THEN

        v_timesheet_id := BASE.CREATE_OBJECT('timesheet', i_person_id, i_record_status);
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_timesheet_id, 'timesheet', i_person_id, i_person_id);

        INSERT INTO pn_timesheet (
             object_id, person_id, status_id, approve_reject_by_id,
             start_date, end_date, work, work_units, date_submitted, date_approve_reject, record_status, comments
        ) VALUES (
             v_timesheet_id, i_person_id, i_status_id, i_approve_reject_by_id,
             i_start_date, i_end_date, i_work, i_work_units, i_date_submitted, i_date_approve_reject, i_record_status, i_comments
        );
        o_timesheet_id := v_timesheet_id;

-- EXISTING TIMESHEET, UPDATE
    ELSE

        o_timesheet_id := i_timesheet_id;

        -- Perform the actual timesheet update
        UPDATE
    	    pn_timesheet
        SET
        	object_id = i_timesheet_id,
            person_id = i_person_id,
            status_id = i_status_id,
            approve_reject_by_id = i_approve_reject_by_id,
            start_date = i_start_date,
            end_date = i_end_date,
            work = i_work,
            work_units = i_work_units,
            date_submitted = i_date_submitted,
            date_approve_reject = i_date_approve_reject,
            record_status = i_record_status,
            comments = i_comments
        WHERE
        	object_id = i_timesheet_id;

    END IF; -- insert/update

    EXCEPTION
    -- handle the exceptions and as of now set o_timesheet id = ''
        WHEN e_unique_constraint THEN
            o_timesheet_id := '';

        WHEN e_null_constraint THEN
            o_timesheet_id := '';

        WHEN e_no_parent_key THEN
            o_timesheet_id := '';

        WHEN e_check_constraint THEN
            o_timesheet_id := '';

        WHEN OTHERS THEN
            err_num:=SQLCODE;
            err_msg:=SUBSTR(SQLERRM,1,120);
            o_timesheet_id := '';
            ROLLBACK;
            INSERT INTO pn_sp_error_log
                VALUES (sysdate,stored_proc_name,err_num,err_msg);
            COMMIT;

END STORE_TIMESHEET;


   PROCEDURE INSERT_ASSIGNMENT
   (
        i_timesheet_id IN NUMBER,
        i_space_id IN NUMBER,
        i_activity_id NUMBER
   )

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Sachin      14-Mar-07  Creation.

IS

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100) := 'INSERT_ASSIGNMENT';

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);

BEGIN

    INSERT INTO pn_assignment_timesheet (
             timesheet_id, space_id, object_id
        ) VALUES (
             i_timesheet_id, i_space_id, i_activity_id
        );

    EXCEPTION
    -- handle the exceptions as throwing them is not needed as of now
        WHEN e_unique_constraint THEN
    -- entry already exists so do nothing
        null;

        WHEN e_null_constraint THEN
    -- nothing gets entered
        null;

        WHEN e_no_parent_key THEN
        null;

    -- as of now we just handle this
        WHEN OTHERS THEN
            err_num:=SQLCODE;
            err_msg:=SUBSTR(SQLERRM,1,120);
            ROLLBACK;
            INSERT INTO pn_sp_error_log
                VALUES (sysdate,stored_proc_name,err_num,err_msg);
            COMMIT;

END INSERT_ASSIGNMENT;


   PROCEDURE REMOVE_TIMESHEET
   (
        i_timesheet_id IN NUMBER,
        i_record_status IN CHAR
   )

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Sachin      14-Mar-07  Creation.

IS

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100) := 'REMOVE_TIMESHEET';

e_null_constraint EXCEPTION;

PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);

BEGIN

    UPDATE pn_timesheet
    SET
        record_status = i_record_status
    WHERE
        object_id = i_timesheet_id;

 EXCEPTION
    -- handle the exceptions as throwing them is not needed as of now
        WHEN e_null_constraint THEN
    -- nothing gets updated
        null;

        WHEN OTHERS THEN
            err_num:=SQLCODE;
            err_msg:=SUBSTR(SQLERRM,1,120);
            ROLLBACK;
            INSERT INTO pn_sp_error_log
                VALUES (sysdate,stored_proc_name,err_num,err_msg);
            COMMIT;

END REMOVE_TIMESHEET;

   PROCEDURE STORE_ACTIVITY
   (
        i_object_id IN NUMBER,
        i_person_id IN NUMBER,
        i_name IN VARCHAR,
        o_object_id OUT NUMBER
   )

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Sachin      20-Apr-07  Creation.

IS

v_object_id NUMBER(20);

err_num NUMBER;
err_msg VARCHAR2(120);
stored_proc_name VARCHAR2(100) := 'STORE_ACTIVITY';

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);

BEGIN

-- NEW ACTIVITY, INSERT
    IF ((i_object_id IS NULL) OR (i_object_id = ''))    THEN

        v_object_id := BASE.CREATE_OBJECT('activity', i_person_id, 'A');
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_object_id, 'activity', i_person_id, i_person_id);

        INSERT INTO pn_activity (
                object_id, space_id, name
            ) VALUES (
                 v_object_id, i_person_id, i_name
            );
        o_object_id := v_object_id;

-- EXISTING ACTIVITY, UPDATE
    ELSE

        o_object_id := i_object_id;

        -- Perform the actual timesheet update
        UPDATE
    	    pn_activity
        SET
        	name = i_name
        WHERE
        	object_id = i_object_id;

    END IF; -- insert/update

    EXCEPTION
    -- handle the exceptions as throwing them is not needed as of now
        WHEN e_unique_constraint THEN
    -- entry already exists so do nothing
        null;

        WHEN e_null_constraint THEN
    -- nothing gets entered
        null;

        WHEN e_no_parent_key THEN
        null;

    -- as of now we just handle this
        WHEN OTHERS THEN
            err_num:=SQLCODE;
            err_msg:=SUBSTR(SQLERRM,1,120);
            ROLLBACK;
            INSERT INTO pn_sp_error_log
                VALUES (sysdate,stored_proc_name,err_num,err_msg);
            COMMIT;

END STORE_ACTIVITY;

END;
/

CREATE OR REPLACE PACKAGE TIMESHEET IS

-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Sachin      3/12/07 created


   PROCEDURE STORE_TIMESHEET
   (
        i_timesheet_id IN NUMBER,
        i_person_id IN NUMBER,
        i_approve_reject_by_id IN NUMBER,
        i_start_date IN DATE,
        i_end_date IN DATE,
        i_work IN NUMBER,
        i_work_units IN NUMBER,
        i_date_submitted IN DATE,
        i_date_approve_reject IN DATE,
        i_record_status IN CHAR,
        i_status_id IN NUMBER,
        i_comments IN VARCHAR2,
        o_timesheet_id OUT NUMBER
   );

   PROCEDURE INSERT_ASSIGNMENT
   (
        i_timesheet_id IN NUMBER,
        i_space_id IN NUMBER,
        i_activity_id NUMBER
   );

   PROCEDURE REMOVE_TIMESHEET
   (
        i_timesheet_id IN NUMBER,
        i_record_status IN CHAR
   );

   PROCEDURE STORE_ACTIVITY
   (
        i_object_id IN NUMBER,
        i_person_id IN NUMBER,
        i_name IN VARCHAR,
        o_object_id OUT NUMBER
   );

END;
/

CREATE OR REPLACE PACKAGE BODY user_domain IS

  FUNCTION getCountUsersForDomain
  ( i_domain_id IN number)
  RETURN  number

  IS

    v_count number := -1;

  BEGIN

    select count(u.user_id) into v_count from pn_user u, pn_person p
    where u.domain_id = i_domain_id
    and u.user_id = p.person_id
    and p.user_status = 'Active';

    return v_count;

  End;


PROCEDURE CREATE_MIGRATION_RECORD
(
        i_domain_migration_id in number,
        i_source_domain_id in number ,
        i_migration_status_id in number
) is

    -- general variables

     v_sysdate DATE := SYSDATE;
     v_cnt number (7) := 0;
     v_tot number (7) := 0;


    stored_proc_name VARCHAR2(100):= 'USER_DOMAIN.CREATE_MIGRATION_RECORD';

      CURSOR C_USERS IS SELECT user_id FROM pn_user_view
            WHERE domain_id = i_source_domain_id and user_status = 'Active' and user_id <> 1;


BEGIN

    FOR REC IN C_USERS LOOP

        v_tot := v_tot + 1;
        v_cnt := v_cnt + 1;

        if (v_cnt >= 50) then
            commit;
            v_cnt := 0;
         end if;

            INSERT INTO pn_user_domain_migration ( user_id , domain_migration_id , migration_status_id , 	activity_date , is_current )
                    VALUES ( REC.user_id , i_domain_migration_id ,i_migration_status_id , v_sysdate , '1');

            UPDATE pn_user_domain_migration SET is_current = '0' WHERE domain_migration_id <> 		i_domain_migration_id and user_id = REC.user_id;


    END LOOP;

EXCEPTION

    WHEN OTHERS THEN

        BASE.LOG_ERROR (stored_proc_name, SQLCODE, SQLERRM);
        raise;

END;


PROCEDURE removeUser
(
        i_domain_id in number,
        i_user_id in varchar2
)   IS

    -- general variables

    v_sysdate DATE := SYSDATE;

    -- debugging / error logging variables

    stored_proc_name VARCHAR2(100):= 'USER_DOMAIN.removeUser';

BEGIN

    delete from pn_user where domain_id = i_domain_id and user_id = i_user_id;

EXCEPTION

    WHEN OTHERS THEN

        BASE.LOG_ERROR (stored_proc_name, SQLCODE, SQLERRM);
        raise;

END;

   -- Enter further code below as specified in the Package spec.
END;
/

CREATE OR REPLACE PACKAGE user_domain IS


  FUNCTION getCountUsersForDomain
     ( i_domain_id IN number)
     RETURN  number;

     PROCEDURE removeUser
    (
        i_domain_id in number,
        i_user_id in varchar2
    );

    PROCEDURE CREATE_MIGRATION_RECORD
    (
        i_domain_migration_id in number,
        i_source_domain_id in number ,
        i_migration_status_id in number
    );

END; -- Package spec
/

CREATE OR REPLACE PACKAGE BODY util
IS

-- Purpose: Provides utility classes to use inside of PL/SQL code.
--
-- MODIFICATION HISTORY
-- Person      Date          Comments
-- ---------   ------        ------------------------------------------
-- Matt        040204        Created
-- Carlos      09-Aug-2007   Added the add_working_days function

  FUNCTION compare_dates (date1 IN DATE, date2 IN DATE)
    RETURN BOOLEAN
  IS
    v_comparison   BOOLEAN;
  BEGIN
    if (date1 IS NULL AND date2 IS NULL)
    then
      v_comparison := true;
    elsif (   (date1 IS NULL AND date2 IS NOT NULL)
           OR (date1 IS NOT NULL and date2 IS NULL)
          )
    then
      v_comparison := false;
    elsif (date1 <> date2)
    then
      v_comparison := false;
    else
      v_comparison := true;
    end if;

    return v_comparison;
  END compare_dates;

  FUNCTION compare_strings (string1 IN VARCHAR, string2 IN VARCHAR)
    RETURN BOOLEAN
  IS
    v_comparison   BOOLEAN;
  BEGIN
    if (string1 IS NULL and string2 IS NULL)
    then
      v_comparison := true;
    elsif (   (string1 IS NULL AND string2 IS NOT NULL)
           OR (string1 IS NOT NULL and string2 IS NULL)
          )
    then
      v_comparison := false;
    elsif (string1 <> string2)
    then
      v_comparison := false;
    else
      v_comparison := true;
    end if;

    return v_comparison;
  END compare_strings;

  FUNCTION compare_numbers (number1 IN NUMBER, number2 IN NUMBER)
    RETURN BOOLEAN
  IS
    v_comparison   BOOLEAN;
  BEGIN
    if (number1 IS NULL and number2 IS NULL)
    then
      v_comparison := true;
    elsif (   (number1 IS NULL AND number2 IS NOT NULL)
           OR (number1 IS NOT NULL and number2 IS NULL)
          )
    then
      v_comparison := false;
    elsif (number1 <> number2)
    then
      v_comparison := false;
    else
      v_comparison := true;
    end if;

    return v_comparison;
  END compare_numbers;

-------------------------------------------------------------------------------
-- ADD_WORKING_DAYS
-------------------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date          Comments
-- ---------   ------        --------------------------------------------------
-- Carlos      09-Aug-2007   Created

-- PURPOSE:
-- This function takes the start_date value and adds the number of days in the
-- days variable, skipping Saturdays and Sundays.

-- NOTE:
-- This function does not skip holidays as Oracle has no way of recognizing
-- which days are holidays. However, you could try populating a holiday table
-- and then query this table to determine additional days to skip.
-------------------------------------------------------------------------------
  FUNCTION add_working_days
     (start_date IN DATE, days IN NUMBER)
     RETURN DATE
  IS
     v_counter NUMBER;
     v_new_date DATE;
     v_day_number NUMBER;

     -- Error Logging
     stored_proc_name VARCHAR2(100):= 'UTIL.ADD_WORKING_DAYS';
  BEGIN

     /* This routine will add a specified number of days (ie: days) to a date (ie: start_date). */
     /* It will skip all weekend days - Saturdays and Sundays */
     v_counter := 1;
     v_new_date := start_date;

     /* Loop to determine how many days to add */
     WHILE v_counter <= days
     LOOP
        /* Add a day */
        v_new_date := v_new_date + 1;
        v_day_number := to_char(v_new_date, 'd');

        /* Increment counter if day falls between Monday to Friday (1 = Sunday)*/
        IF v_day_number >= 2 AND v_day_number <= 6 THEN
           v_counter := v_counter + 1;
        END if;

     END LOOP;

  RETURN v_new_date;

  -- Log exceptions
  EXCEPTION
  WHEN OTHERS THEN
    BEGIN
      BASE.LOG_ERROR(stored_proc_name, SQLCODE, SQLERRM);
      RAISE;
    END;

  END add_working_days;

END; -- Package
/

CREATE OR REPLACE PACKAGE util IS

-- Purpose: Provides utilities helpful in PL/SQL
--
-- MODIFICATION HISTORY
-- ---------   ------        ------------------------------------------
-- Matt        040204        Created
-- Carlos      09-Aug-2007   Added the add_working_days function
--

FUNCTION compare_dates (
  date1 IN DATE,
  date2 IN DATE
)
RETURN BOOLEAN;

FUNCTION compare_strings (
  string1 IN VARCHAR,
  string2 IN VARCHAR
)
RETURN BOOLEAN;

FUNCTION compare_numbers (
  number1 IN NUMBER,
  number2 IN NUMBER
)
RETURN BOOLEAN;

FUNCTION add_working_days (
  start_date IN DATE,
  days IN NUMBER
)
RETURN DATE;

TYPE cursor_type is REF CURSOR;

END; -- Package spec
/

CREATE OR REPLACE Package Body vote
IS

PROCEDURE create_vote
    (
        in_space_id IN number,
        in_question IN varchar2,
        in_title    IN varchar2,
        in_whoami   IN number,
        object_id   OUT number
    )

IS

-- variable declaration

    v_space_id     pn_vote_questionair.space_id%type := TO_NUMBER(in_space_id);
    v_question        pn_vote_questionair.question%type := in_question;
    v_title           pn_vote_questionair.title%type := in_title;
    v_new_id          pn_vote_questionair.vote_id%type ;
    v_whoami          pn_person.person_id%type := in_whoami;
    G_active_record_status   pn_document.record_status%type := 'A';

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'VOTE.CREATE_VOTE';



BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_new_id FROM dual;


      -- register new container in pn_object

      insert into pn_object (
          object_id,
          date_created,
          object_type,
          created_by,
          record_status )
      values (
          v_new_id,
          SYSDATE,
          'vote',
          v_whoami,
          G_active_record_status);

      -- create new document container

      insert into pn_vote_questionair (
          space_id,
          vote_id,
          question,
          title)
      values (
         v_space_id,
         v_new_id,
         v_question,
         v_title
         );

      object_id := v_new_id;
      COMMIT;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END;  -- Procedure CREATE_VOTE














PROCEDURE create_response
    (
        in_vote_id IN pn_vote_response.vote_id%type,
        in_response IN pn_vote_response.response%type
    )

IS

-- variable declaration

    v_vote_id         pn_vote_response.vote_id%type := TO_NUMBER(in_vote_id);
    v_response        pn_vote_response.response%type := in_response;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'VOTE.CREATE_RESPONSE';



BEGIN
    SET TRANSACTION READ WRITE;

      insert into pn_vote_response (
          vote_id,
          response )
      values (
          v_vote_id,
          v_response );

      COMMIT;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END;  -- Procedure CREATE_RESPONSE



PROCEDURE add_voter
    (
        in_vote_id IN pn_person_has_vote.vote_id%type,
        in_person_id IN pn_person_has_vote.person_id%type
    )

IS

-- variable declaration

    v_vote_id         pn_person_has_vote.vote_id%type := TO_NUMBER(in_vote_id);
    v_person_id        pn_person_has_vote.person_id%type := in_person_id;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'VOTE.CREATE_RESPONSE';



BEGIN
    SET TRANSACTION READ WRITE;

      insert into pn_person_has_vote (
          person_id,
          vote_id )
      values (
          v_person_id,
          v_vote_id );

      COMMIT;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END;  -- Procedure ADD_VOTER







-----------------------------------------------------------------
-- GET_VOTE_LIST
-----------------------------------------------------------------

FUNCTION GET_CONTAINER_LIST
( space_id IN pn_vote_questionair.space_id%type )
    RETURN ReferenceCursor

   IS

    v_space_id          pn_vote_questionair.space_id%type := to_number(space_id);

    v_vote_id           pn_vote_questionair.vote_id%type;
    v_question          pn_vote_questionair.question%type;
    v_title             pn_vote_questionair.title%type;

    v_root_container_id pn_doc_container.doc_container_id%type;
    v_is_root           NUMBER := 0;
    v_is_hidden         NUMBER := 0;

    v_collection_id     pn_object.object_id%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.GET_CONTAINER_LIST';

    voteList            ReferenceCursor;

    BEGIN

    open voteList for
        select * from pn_vote_questionair where space_id = v_space_id;

    RETURN voteList;

 EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;


END; -- Procedure GET_VOTE_LIST









   -- Enter further code below as specified in the Package spec.
END; -- Package Body VOTE
/

CREATE OR REPLACE Package vote
  IS


TYPE ReferenceCursor            IS REF CURSOR;


PROCEDURE create_vote
    (
        in_space_id IN number,
        in_question IN varchar2,
        in_title    IN varchar2,
        in_whoami   IN number,
        object_id   OUT number
    );

PROCEDURE create_response
    (
        in_vote_id IN pn_vote_response.vote_id%type,
        in_response IN pn_vote_response.response%type
    );

PROCEDURE add_voter
    (
        in_vote_id IN pn_person_has_vote.vote_id%type,
        in_person_id IN pn_person_has_vote.person_id%type
    );

FUNCTION GET_CONTAINER_LIST
( space_id IN pn_vote_questionair.space_id%type )
    RETURN ReferenceCursor;


END; -- Package Specification VOTE
/

CREATE OR REPLACE PACKAGE BODY workflow IS
--==================================================================
-- Purpose: Workflow procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        05-Sep-00  Created it.
-- TimM        12-Sep-00  Added i_space_id and insert of
--      pn_space_has_workflow records.
-- TimM        20-Oct-00  Added copy procedures
-- TimM        27-Oct-00  Modify CREATE_STEP, MODIFY_STEP, COPY_STEPS
--                        to deal with entry_status_id
-- TimM        01-Nov-00  Changed signature of ADD_ENVELOPE_OBJECT to
--                        change i_object_properties to i_object_properties_id
-- TimM        12-15-00   Change CREATE_HISTORY to use history_message_id for CLOB storage
--==================================================================

/*--------------------------------------------------------------------
  PROCEDURE PROTOTYPES
  Required for private procedures so that they can be used
  lexically earlier than their definition.
--------------------------------------------------------------------*/

    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean;

    function getSpaceID(i_workflow_id in number)
        return number;

    procedure copy_workflow(
        i_to_space_id in number,
        i_workflow_id in number,
        i_created_by_id in number,
        i_copy_groups   in number);

    procedure copy_steps(
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_created_by_id     in number,
        i_copy_groups       in number);

    procedure copy_transitions (
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_created_by_id     in number,
        i_copy_groups       in number);

/*--------------------------------------------------------------------
  END OF PROCEDURE PROTOTYPES
--------------------------------------------------------------------*/


----------------------------------------------------------------------
-- CREATE_WORKFLOW
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    PROCEDURE create_workflow
      ( i_space_id       IN varchar2,
        i_workflow_name  IN varchar2,
        i_workflow_description IN varchar2,
        i_notes          IN varchar2,
        i_owner_id       IN varchar2,
        i_is_published   IN number,
        i_strictness_id  IN varchar2,
        i_is_generic     in number,
        i_created_by_id  IN varchar2,
        o_workflow_id    OUT varchar2,
        o_return_value   OUT number)
    IS
        -- Convert input varchar2() to numbers
        v_space_id      pn_space_has_workflow.space_id%type := to_number(i_space_id);
        v_owner_id      pn_workflow.owner_id%type := to_number(i_owner_id);
        v_strictness_id pn_workflow.strictness_id%type := to_number(i_strictness_id);
        v_created_by_id pn_workflow.created_by_id%type := to_number(i_created_by_id);

        v_datetime      date;
        v_workflow_id    pn_workflow.workflow_id%type;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_WORKFLOW';

    BEGIN

        -- Create new entry in pn_object
        v_workflow_id := base.create_object(
            G_workflow_object_type,
            v_created_by_id,
            base.active_record_status);

        SECURITY.CREATE_SECURITY_PERMISSIONS(v_workflow_id, G_workflow_object_type, v_space_id, v_created_by_id);

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        -- Create the workflow record
        INSERT INTO pn_workflow (
            workflow_id,
            workflow_name,
            workflow_description,
            notes,
            owner_id,
            is_published,
            strictness_id,
            created_by_id,
            created_datetime,
            is_generic,
            crc,
            record_status)
        VALUES (
            v_workflow_id,
            i_workflow_name,
            i_workflow_description,
            i_notes,
            v_owner_id,
            i_is_published,
            v_strictness_id,
            v_created_by_id,
            v_datetime,
            i_is_generic,
            v_datetime,
            base.active_record_status);

        -- Associate workflow with space
        INSERT INTO pn_space_has_workflow (
            space_id,
            workflow_id)
        VALUES (
            v_space_id,
            v_workflow_id);

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

        -- Set output parameters
        o_workflow_id := to_char(v_workflow_id);
        o_return_value := BASE.OPERATION_SUCCESSFUL;

      EXCEPTION
      WHEN OTHERS THEN
        BEGIN
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        END;

      END;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_WORKFLOW
-- Update an existing workflow
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure modify_workflow (
        i_workflow_id    in varchar2,
        i_workflow_name  in varchar2,
        i_workflow_description in varchar2,
        i_notes          in varchar2,
        i_owner_id       in varchar2,
        i_is_published   in number,
        i_strictness_id  in varchar2,
        i_is_generic     in number,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_workflow_id    pn_workflow.workflow_id%type := to_number(i_workflow_id);
        v_owner_id       pn_workflow.owner_id%type := to_number(i_owner_id);
        v_strictness_id  pn_workflow.strictness_id%type := to_number(i_strictness_id);
        v_modified_by_id pn_workflow.created_by_id%type := to_number(i_modified_by_id);

        v_datetime       date;
        v_current_crc    pn_workflow.crc%type;

        stored_proc_name varchar2(100):= 'WORKFLOW.MODIFY_WORKFLOW';
    begin
        -- Check workflow has not changed
        select crc into v_current_crc
            from pn_workflow
            where workflow_id = v_workflow_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            update
                pn_workflow wf
            set
                wf.workflow_name = i_workflow_name,
                wf.workflow_description = i_workflow_description,
                wf.notes = i_notes,
                wf.owner_id = v_owner_id,
                wf.modified_by_id = v_modified_by_id,
                wf.modified_datetime = v_datetime,
                wf.is_published = i_is_published,
                wf.strictness_id = v_strictness_id,
                wf.is_generic = i_is_generic,
                wf.crc = v_datetime,
                wf.record_status = base.active_record_status
            where
                wf.workflow_id = v_workflow_id;

            o_return_value := BASE.OPERATION_SUCCESSFUL;
        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

    exception
        when record_locked then
        begin
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;
    end modify_workflow;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_WORKFLOW
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_workflow (
        i_workflow_id    in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_workflow_id    pn_workflow.workflow_id%type := to_number(i_workflow_id);
        v_modified_by_id pn_workflow.created_by_id%type := to_number(i_modified_by_id);

        v_datetime       date;
        v_current_crc    pn_workflow.crc%type;

        stored_proc_name varchar2(100):= 'WORKFLOW.REMOVE_WORKFLOW';
    begin
        -- Check workflow has not changed, may raise record_locked exception
        select crc into v_current_crc
            from pn_workflow
            where workflow_id = v_workflow_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then
            select sysdate into v_datetime from dual;
            update
                pn_workflow wf
            set
                wf.modified_by_id = v_modified_by_id,
                wf.modified_datetime = v_datetime,
                wf.crc = sysdate,
                wf.record_status = base.deleted_record_status
            where
                wf.workflow_id = v_workflow_id;

            o_return_value := BASE.OPERATION_SUCCESSFUL;
        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */

    exception
        when record_locked then
        begin
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;
    end remove_workflow;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_STEP
----------------------------------------------------------------------
    procedure create_step (
        i_workflow_id       IN varchar2,
        i_step_name         IN varchar2,
        i_step_sequence      in varchar2,
        i_step_description  IN varchar2,
        i_is_initial_step   IN number,
        i_is_final_step     IN number,
        i_entry_status_id   in number,
        i_subscription_id   in number,
        i_created_by_id     IN varchar2,
        i_is_notes_null     in number,
        o_notes_clob        out clob,
        o_step_id           OUT varchar2)
    is
        -- Convert input varchar2() to numbers
        v_workflow_id    pn_workflow_step.workflow_id%type := to_number(i_workflow_id);
          v_step_sequence pn_workflow_step.step_sequence%type := to_number (i_step_sequence);
        v_entry_status_id pn_workflow_step.entry_status_id%type := to_number(i_entry_status_id);
        v_subscription_id pn_workflow_step.subscription_id%type := to_number(i_subscription_id);
        v_created_by_id  pn_workflow_step.created_by_id%type := to_number(i_created_by_id);
        v_datetime      date;
        v_step_id       pn_workflow_step.step_id%type;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_STEP';

    begin

        -- If this is an initial step, make sure there are no other initial steps
        if (i_is_initial_step = 1) then
            update pn_workflow_step
            set
                is_initial_step = 0
            where
                workflow_id = v_workflow_id;
        end if;

        -- Create new entry in pn_object
        v_step_id := base.create_object(
            G_step_object_type,
            v_created_by_id,
            base.active_record_status);

        SECURITY.CREATE_SECURITY_PERMISSIONS(v_step_id, G_step_object_type, getSpaceID(v_workflow_id), v_created_by_id);

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        if (i_is_notes_null = 1) then
            -- Create the step record with null notes
            insert into pn_workflow_step (
                step_id, workflow_id, step_name, step_description, notes_clob,
                is_initial_step, is_final_step, entry_status_id, subscription_id,
                created_by_id, created_datetime, crc, record_status, step_sequence)
            values (
                v_step_id, v_workflow_id, i_step_name, i_step_description, null,
                i_is_initial_step, i_is_final_step, v_entry_status_id, v_subscription_id,
                v_created_by_id, v_datetime, v_datetime, base.active_record_status, v_step_sequence)
            returning
                notes_clob into o_notes_clob;
        else
            -- Create the step record with empty_clob() notes
            insert into pn_workflow_step (
                step_id, workflow_id, step_name, step_description, notes_clob,
                is_initial_step, is_final_step, entry_status_id, subscription_id,
                created_by_id, created_datetime, crc, record_status, step_sequence)
            values (
                v_step_id, v_workflow_id, i_step_name, i_step_description, empty_clob(),
                i_is_initial_step, i_is_final_step, v_entry_status_id, v_subscription_id,
                v_created_by_id, v_datetime, v_datetime, base.active_record_status, v_step_sequence)
            returning
                notes_clob into o_notes_clob;

        end if;

        -- Set output parameters
        o_step_id := to_char(v_step_id);

      exception
      when others then
          base.log_error(stored_proc_name, sqlcode, sqlerrm);
          raise;
  end create_step;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_STEP
-- Update an existing step
----------------------------------------------------------------------
    procedure modify_step (
        i_step_id        in varchar2,
        i_step_name      in varchar2,
        i_step_sequence      in varchar2,
        i_step_description in varchar2,
        i_is_initial_step in number,
        i_is_final_step  in number,
        i_entry_status_id   in number,
        i_subscription_id   in number,
        i_modified_by_id in varchar2,
        i_crc            in date,
        i_is_notes_null     in number,
        o_notes_clob        out clob)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_step_id       pn_workflow_step.step_id%type := to_number(i_step_id);
        v_step_sequence pn_workflow_step.step_sequence%type := to_number (i_step_sequence);
        v_entry_status_id pn_workflow_step.entry_status_id%type := to_number(i_entry_status_id);
        v_subscription_id pn_workflow_step.subscription_id%type := to_number(i_subscription_id);
        v_modified_by_id pn_workflow_step.modified_by_id%type := to_number(i_modified_by_id);

        v_datetime      date;
        v_current_crc    pn_workflow_step.crc%type;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW.MODIFY_STEP';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_step
            where step_id = v_step_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            if (i_is_notes_null = 1) then
                -- Update record with null notes
                update
                    pn_workflow_step wfstep
                set
                    wfstep.step_name = i_step_name,
                    wfstep.step_sequence = v_step_sequence,
                    wfstep.step_description = i_step_description,
                    wfstep.notes_clob = null,
                    wfstep.is_final_step = i_is_final_step,
                    wfstep.is_initial_step = i_is_initial_step,
                    wfstep.entry_status_id = v_entry_status_id,
                    wfstep.subscription_id = v_subscription_id,
                    wfstep.modified_by_id = v_modified_by_id,
                    wfstep.modified_datetime = v_datetime,
                    wfstep.crc = v_datetime
                where
                    wfstep.step_id = v_step_id
                returning
                    notes_clob into o_notes_clob;
            else
                -- Update record with empty_clob() notes for subsequent writing
                update
                    pn_workflow_step wfstep
                set
                    wfstep.step_name = i_step_name,
                    wfstep.step_sequence = v_step_sequence,
                    wfstep.step_description = i_step_description,
                    wfstep.notes_clob = empty_clob(),
                    wfstep.is_final_step = i_is_final_step,
                    wfstep.is_initial_step = i_is_initial_step,
                    wfstep.entry_status_id = v_entry_status_id,
                    wfstep.subscription_id = v_subscription_id,
                    wfstep.modified_by_id = v_modified_by_id,
                    wfstep.modified_datetime = v_datetime,
                    wfstep.crc = v_datetime
                where
                    wfstep.step_id = v_step_id
                returning
                    notes_clob into o_notes_clob;
            end if;

        else
            -- crc different
            raise record_modified;
        end if;

    exception
        when others then
        begin
           base.log_error(stored_proc_name, sqlcode, sqlerrm);
           raise;
        end;
    end modify_step;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_STEP
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_step (
        i_step_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_step_id       pn_workflow_step.step_id%type := to_number(i_step_id);
        v_modified_by_id pn_workflow_step.modified_by_id%type := to_number(i_modified_by_id);

        v_datetime      date;
        v_current_crc    pn_workflow_step.crc%type;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW.REMOVE_STEP';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_step
            where step_id = v_step_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then
            select sysdate into v_datetime from dual;
            -- Update and set to DELETED
            update
                pn_workflow_step wfstep
            set
                wfstep.modified_by_id = v_modified_by_id,
                wfstep.modified_datetime = v_datetime,
                wfstep.crc = sysdate,
                wfstep.record_status = base.deleted_record_status
            where
                wfstep.step_id = v_step_id;

        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK -- THIS IS LEFT TO THE CALLING ENVIRONMENT
        */
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when record_locked then
        begin
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;
    end remove_step;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_TRANSITION
----------------------------------------------------------------------
    procedure create_transition
      ( i_workflow_id            in varchar2,
        i_transition_verb        in varchar2,
        i_transition_description in varchar2,
        i_begin_step_id          in varchar2,
        i_end_step_id            in varchar2,
        i_created_by_id          in varchar2,
        o_transition_id          out varchar2,
        o_return_value           out number)
    is
        -- Convert input varchar2() to numbers
        v_workflow_id    pn_workflow_transition.workflow_id%type := to_number(i_workflow_id);
        v_created_by_id  pn_workflow_transition.created_by_id%type := to_number(i_created_by_id);
        v_begin_step_id  pn_workflow_transition.begin_step_id%type := to_number(i_begin_step_id);
        v_end_step_id    pn_workflow_transition.end_step_id%type := to_number(i_end_step_id);

        v_datetime      date;
        v_transition_id pn_workflow_transition.transition_id%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_TRANSITION';

    begin

        -- Create new entry in pn_object
        v_transition_id := base.create_object(
            G_transition_object_type,
            v_created_by_id,
            base.active_record_status);

        SECURITY.CREATE_SECURITY_PERMISSIONS(v_transition_id, G_transition_object_type, getSpaceID(v_workflow_id), v_created_by_id);

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        -- Create the transition record
        insert into pn_workflow_transition (
            workflow_id,
            transition_id,
            transition_verb,
            transition_description,
            begin_step_id,
            end_step_id,
            created_by_id,
            created_datetime,
            crc,
            record_status)
        values (
            v_workflow_id,
            v_transition_id,
            i_transition_verb,
            i_transition_description,
            v_begin_step_id,
            v_end_step_id,
            v_created_by_id,
            v_datetime,
            v_datetime,
            base.active_record_status);

        -- Make it so.
        COMMIT;

        -- Set output parameters
        o_transition_id := to_char(v_transition_id);
        o_return_value := BASE.OPERATION_SUCCESSFUL;

      exception
      when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log values (
                sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;

      end create_transition;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_TRANSITION
-- Update an existing transition
----------------------------------------------------------------------
    procedure modify_transition (
        i_transition_id          in varchar2,
        i_transition_verb        in varchar2,
        i_transition_description in varchar2,
        i_begin_step_id          in varchar2,
        i_end_step_id            in varchar2,
        i_modified_by_id         in varchar2,
        i_crc                    in date,
        o_return_value           out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_transition_id  pn_workflow_transition.transition_id%type := to_number(i_transition_id);
        v_modified_by_id pn_workflow_transition.modified_by_id%type := to_number(i_modified_by_id);
        v_begin_step_id  pn_workflow_transition.begin_step_id%type := to_number(i_begin_step_id);
        v_end_step_id    pn_workflow_transition.end_step_id%type := to_number(i_end_step_id);

        v_datetime      date;
        v_current_crc    pn_workflow_transition.crc%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.MODIFY_TRANSITION';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_transition
            where transition_id = v_transition_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            -- Update record
            update
                pn_workflow_transition tran
            set
               tran.transition_verb = i_transition_verb,
               tran.transition_description = i_transition_description,
               tran.begin_step_id = v_begin_step_id,
               tran.end_step_id = v_end_step_id,
               tran.modified_by_id = v_modified_by_id,
               tran.modified_datetime = v_datetime,
               tran.crc = v_datetime
            where
                tran.transition_id = v_transition_id;

        else
            -- crc different
            raise record_modified;
        end if;

        commit;
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when record_locked then
        begin
            rollback;
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            rollback;
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log
               values (sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;
    end modify_transition;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_TRANSITION
-- DOES NOT COMMIT OR ROLLBACK
-- NO OUTPUT PARMS TO FACILITATE BATCHING
-- Raises workflow.unspecified_error when there is a problem
----------------------------------------------------------------------
    procedure remove_transition(
        i_transition_id  in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date)
    is
        v_return_value      number;
    begin
        -- Call remove transition and suck up return value
        remove_transition(i_transition_id, i_modified_by_id, i_crc, v_return_value);
        if v_return_value != base.operation_successful then
            raise workflow.unspecified_error;
        end if;
    end remove_transition;

----------------------------------------------------------------------
-- REMOVE_TRANSITION
-- DOES NOT COMMIT OR ROLLBACK
-- Raises workflow.unspecified_error when there is a problem
----------------------------------------------------------------------
    procedure remove_transition (
        i_transition_id  in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        v_transition_id  pn_workflow_transition.transition_id%type := to_number(i_transition_id);
        v_modified_by_id pn_workflow_transition.modified_by_id%type := to_number(i_modified_by_id);
        v_datetime      date;
        v_current_crc    pn_workflow_transition.crc%type;

        -- Standard error handling
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.REMOVE_TRANSITION';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_transition
            where transition_id = v_transition_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then
            select sysdate into v_datetime from dual;
            update
                pn_workflow_transition tran
            set
               tran.modified_by_id = v_modified_by_id,
               tran.modified_datetime = v_datetime,
               tran.crc = sysdate,
               tran.record_status = base.deleted_record_status
            where
                tran.transition_id = v_transition_id;

        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK
         */
        o_return_value := base.operation_successful;

    exception
        when record_locked then
        begin
            o_return_value := base.update_record_locked;
        end;
        when record_modified then
        begin
            o_return_value := base.update_record_out_of_sync;
        end;
        when others then
        begin
            o_return_value := base.plsql_exception;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;
    end remove_transition;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_STEP_GROUP
----------------------------------------------------------------------
    procedure create_step_group (
        i_step_id                in varchar2,
        i_workflow_id            in varchar2,
        i_group_id               in varchar2,
        i_is_participant         in number,
        i_created_by_id          in varchar2,
        o_return_value           out number)
    is
        -- Convert input varchar2() to numbers
        v_step_id        pn_workflow_step_has_group.step_id%type := to_number(i_step_id);
        v_workflow_id    pn_workflow_step_has_group.workflow_id%type := to_number(i_workflow_id);
        v_group_id       pn_workflow_step_has_group.group_id%type := to_number(i_group_id);
        v_created_by_id  pn_workflow_step_has_group.created_by_id%type := to_number(i_created_by_id);

        v_datetime      date;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_STEP_GROUP';

    begin

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        -- Check to see if row exists in deleted form (since we don't have generated PK)
        declare
            v_dummy number;
        begin
            select
                1 into v_dummy
            from
                pn_workflow_step_has_group
            where
                step_id = v_step_id and
                workflow_id = v_workflow_id and
                group_id = v_group_id
            for update nowait;

            -- If it exists, set status to ACTIVE
            update
                pn_workflow_step_has_group sg
            set
                sg.is_participant = i_is_participant,
                sg.modified_by_id = v_created_by_id,
                sg.modified_datetime = v_datetime,
                sg.crc = v_datetime,
                sg.record_status = base.active_record_status
            where
                sg.step_id = v_step_id and
                sg.workflow_id = v_workflow_id and
                sg.group_id = v_group_id;

        exception
            when NO_DATA_FOUND then
            begin
                -- Create the step_has_group record
                insert into pn_workflow_step_has_group (
                    step_id,
                    workflow_id,
                    group_id,
                    is_participant,
                    created_by_id,
                    created_datetime,
                    crc,
                    record_status)
                values (
                    v_step_id,
                    v_workflow_id,
                    v_group_id,
                    i_is_participant,
                    v_created_by_id,
                    v_datetime,
                    v_datetime,
                    base.active_record_status);
            end;
        end;

        -- Make it so.
        COMMIT;

        -- Set output parameters
        o_return_value := BASE.OPERATION_SUCCESSFUL;

      exception
      when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log values (
                sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;

    end create_step_group;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_STEP_GROUP
-- Remove a step group
----------------------------------------------------------------------
    procedure remove_step_group (
        i_step_id                in varchar2,
        i_workflow_id            in varchar2,
        i_group_id               in varchar2,
        i_modified_by_id         in varchar2,
        i_crc                    in date,
        o_return_value           out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_step_id        pn_workflow_step_has_group.step_id%type := to_number(i_step_id);
        v_workflow_id    pn_workflow_step_has_group.workflow_id%type := to_number(i_workflow_id);
        v_group_id       pn_workflow_step_has_group.group_id%type := to_number(i_group_id);
        v_modified_by_id pn_workflow_step_has_group.modified_by_id%type := to_number(i_modified_by_id);

        v_datetime      date;
        v_current_crc    pn_workflow_transition.crc%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.REMOVE_STEP_GROUP';

    begin
        -- Check record has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_step_has_group
            where
                step_id = v_step_id and
                workflow_id = v_workflow_id and
                group_id = v_group_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            -- Set status to DELETED
            update
                pn_workflow_step_has_group sg
            set
                sg.modified_by_id = v_modified_by_id,
                sg.modified_datetime = v_datetime,
                sg.crc = v_datetime,
                sg.record_status = base.deleted_record_status
            where
                sg.step_id = v_step_id and
                sg.workflow_id = v_workflow_id and
                sg.group_id = v_group_id;

        else
            -- crc different
            raise record_modified;
        end if;

        commit;
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when record_locked then
        begin
            rollback;
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            rollback;
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log
               values (sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;


     end remove_step_group;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_STEP_GROUP
-- Update an existing step group
----------------------------------------------------------------------
    procedure modify_step_group (
        i_step_id                in varchar2,
        i_workflow_id            in varchar2,
        i_group_id               in varchar2,
        i_is_participant         in number,
        i_modified_by_id         in varchar2,
        i_crc                    in date,
        o_return_value           out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_step_id        pn_workflow_step_has_group.step_id%type := to_number(i_step_id);
        v_workflow_id    pn_workflow_step_has_group.workflow_id%type := to_number(i_workflow_id);
        v_group_id       pn_workflow_step_has_group.group_id%type := to_number(i_group_id);
        v_modified_by_id pn_workflow_step_has_group.modified_by_id%type := to_number(i_modified_by_id);

        v_datetime      date;
        v_current_crc    pn_workflow_transition.crc%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.MODIFY_STEP_GROUP';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_step_has_group
            where
                step_id = v_step_id and
                workflow_id = v_workflow_id and
                group_id = v_group_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            -- Update record
            update
                pn_workflow_step_has_group sg
            set
                sg.is_participant = i_is_participant,
                sg.modified_by_id = v_modified_by_id,
                sg.modified_datetime = v_datetime,
                sg.crc = v_datetime
            where
                sg.step_id = v_step_id and
                sg.workflow_id = v_workflow_id and
                sg.group_id = v_group_id;

        else
            -- crc different
            raise record_modified;
        end if;

        commit;
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when record_locked then
        begin
            rollback;
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            rollback;
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log
               values (sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;

    end modify_step_group;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_ENVELOPE
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure create_envelope
      ( i_workflow_id    in varchar2,
        i_strictness_id  in varchar2,
        i_envelope_name  in varchar2,
        i_envelope_description in varchar2,
        i_created_by_id  in varchar2,
        i_status_id      in varchar2,
        i_priority_id    in varchar2,
        i_is_comments_null in number,
        o_comments_clob  out clob,
        o_envelope_id    out varchar2)
    is
        -- User defined exceptions
        initial_step_not_found exception;  -- Could not find initial step for workflow

        -- Convert input varchar2() to numbers
        v_workflow_id   pn_workflow_envelope.workflow_id%type   := to_number(i_workflow_id);
        v_strictness_id pn_workflow_envelope.strictness_id%type := to_number(i_strictness_id);
        v_created_by_id pn_workflow_envelope.created_by_id%type := to_number(i_created_by_id);
        v_status_id     pn_envelope_version.status_id%type      := to_number(i_status_id);
        v_priority_id   pn_envelope_version.priority_id%type    := to_number(i_priority_id);

        v_datetime      date;
        v_envelope_id   pn_workflow_envelope.envelope_id%type;
        v_envelope_version_id pn_envelope_version.version_id%type;
        v_step_id       pn_envelope_version.step_id%type;
        v_entry_status_id pn_workflow_step.entry_status_id%type := null;

        -- Standard error handling
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_ENVELOPE';

    begin
        /*
            Locate initial step for the workflow
            to which this envelope is attached

            04/09/01 - Tim - Handles erroneous scenario where there may be more
            than one initial step: Accounts for older "create_step" code that
            did not prohibit multiple initial steps.
         */
        begin
            select
               step_id, entry_status_id into v_step_id, v_entry_status_id
            from
               pn_workflow_step
            where
               rownum = 1 and
               workflow_id = v_workflow_id and
               is_initial_step = 1 and
               record_status = base.active_record_status;
        exception
            when NO_DATA_FOUND then
            begin
                raise initial_step_not_found;
            end;
        end;

        /** If the initial step doesn't have a new status, we should set that
            initial status to new by default. */
        if (v_entry_status_id = '') or (v_entry_status_id is null) then
            v_status_id := '100';
        end if;

        /* Override passed in status id if the step had an entry status id */
        if v_entry_status_id is not null then
            v_status_id := v_entry_status_id;
        end if;

        -- Create new entry in pn_object
        v_envelope_id := base.create_object(
            G_envelope_object_type,
            v_created_by_id,
            base.active_record_status);

        -- **** TBC **** -> Should SPACE be inferred from workflow or passed in and stored with envelope ?????
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_envelope_id, G_envelope_object_type, getSpaceID(v_workflow_id), v_created_by_id);

        -- Create envelope version object entry
        v_envelope_version_id := base.create_object(
            G_envelope_version_object_type,
            v_created_by_id,
            base.active_record_status);

        -- **** TBC **** -> Should SPACE be inferred from workflow or passed in and stored with envelope ?????
        SECURITY.CREATE_SECURITY_PERMISSIONS(v_envelope_version_id, G_envelope_version_object_type, getSpaceID(v_workflow_id), v_created_by_id);

        -- Get current datetime
        select sysdate into v_datetime from dual;

        -- Insert envelope
        insert into pn_workflow_envelope (
            envelope_id,
            workflow_id,
            strictness_id,
            current_version_id,
            envelope_name,
            envelope_description,
            created_by_id,
            created_datetime,
            crc,
            record_status)
        values (
            v_envelope_id,
            v_workflow_id,
            v_strictness_id,
            v_envelope_version_id,
            i_envelope_name,
            i_envelope_description,
            v_created_by_id,
            v_datetime,
            v_datetime,
            base.active_record_status);

        if (i_is_comments_null > 0) then
            -- Insert envelope version with null comments
            insert into  pn_envelope_version
                ( version_id, envelope_id, status_id, step_id, workflow_id,
                  transition_id, priority_id, comments_clob,
                  created_by_id, created_datetime, crc, record_status)
            values
                ( v_envelope_version_id, v_envelope_id, v_status_id, v_step_id, v_workflow_id,
                  null, v_priority_id, null,
                  v_created_by_id, v_datetime, v_datetime, base.active_record_status)
            returning
                comments_clob into o_comments_clob;

        else
            -- Insert envelope version with empty comments for subsequent streaming
            insert into  pn_envelope_version
                ( version_id, envelope_id, status_id, step_id, workflow_id,
                  transition_id, priority_id, comments_clob,
                  created_by_id, created_datetime, crc, record_status)
            values
                ( v_envelope_version_id, v_envelope_id, v_status_id, v_step_id, v_workflow_id,
                  null, v_priority_id, empty_clob(),
                  v_created_by_id, v_datetime, v_datetime, base.active_record_status)
            returning
                comments_clob into o_comments_clob;

        end if;

        -- Update envelope with current version id
        update
            pn_workflow_envelope
        set
            current_version_id = v_envelope_version_id
        where
            envelope_id = v_envelope_id;


        /*
            IMPORTANT - THIS PROCEDURE DOES NOT COMMIT OR ROLLBACK
            SINCE IT MUST BE POSSIBLE TO ADD OJBECTS TO THE ENVELOPE
            ONCE CREATED
         */

        -- Set output parameters
        o_envelope_id := to_char(v_envelope_id);

    exception
        when initial_step_not_found then
        begin
            base.log_error(stored_proc_name, sqlcode, 'No initial step found for workflow '||v_workflow_id);
            raise;
        end;
        when others then
        begin
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        end;
    end create_envelope;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- ADD_ENVELOPE_OBJECT
-- DOES NOT COMMIT OR ROLLBACK
-- This routine will Raise an exception if there is a problem
-- There are no output parameters to facilitate batching
----------------------------------------------------------------------
    procedure add_envelope_object
      ( i_envelope_id    in varchar2,
        i_object_id       in varchar2,
        i_object_type     in varchar2,
        i_object_version_id in varchar2,
        i_object_properties_id in varchar2,
        i_created_by_id  in varchar2)
    is
        envelope_not_found exception;   -- Could not find envelope/current version

        v_envelope_id   pn_workflow_envelope.envelope_id%type   := to_number(i_envelope_id);
        v_object_id     pn_object.object_id%type                := to_number(i_object_id);
        v_object_version_id pn_envelope_version_has_object.object_version_id%type := to_number(i_object_version_id);
        v_object_type pn_envelope_version_has_object.object_type%type := i_object_type;
	v_object_properties_id pn_envelope_version_has_object.object_properties_id%type := to_number(i_object_properties_id);
        -- i_created_by_id not currently used

        v_envelope_version_id   pn_envelope_version.version_id%type;
        v_datetime  date;
        v_dummy     number;

        -- Standard error handling
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.ADD_ENVELOPE_OBJECT';

    begin
        /* Make sure we got a version id (it is optional) */
        if i_object_version_id is null or i_object_version_id = '' then
            v_object_version_id := 0;
        end if;

	/* bfd - 2107 make object type unlinked when the document attached to envolop is deleted...*/
	if i_object_type is null or i_object_type = '' then
            v_object_type := 'unlinked';
        end if;

        /* Determine current version id of envelope */
        begin
            select
                current_version_id into v_envelope_version_id
            from
                pn_workflow_envelope
            where
                envelope_id = v_envelope_id;
        exception
            when NO_DATA_FOUND then
            begin
                raise envelope_not_found;
            end;
        end;

        /* Get current date and time */
        select sysdate into v_datetime from dual;

        /*
            Try to locate existing Envelope Has Object record
            If none exists, create one
         */
        begin
            select
                1 into v_dummy
            from
                pn_envelope_has_object
            where
                object_id = v_object_id and
                envelope_id = v_envelope_id;
        exception
            when NO_DATA_FOUND then
            begin
                /* Create envelope_has_object record */
                insert into pn_envelope_has_object (
                    envelope_id,
                    object_id,
                    crc,
                    record_status)
                values (
                    v_envelope_id,
                    v_object_id,
                    v_datetime,
                    base.active_record_status);
            end;
        end;

        /*
            Create envelope_version_has_object record
         */
        insert into pn_envelope_version_has_object (
            envelope_id,
            version_id,
            object_id,
            object_type,
            object_version_id,
            object_properties_id,
            crc,
            record_status)
        values (
            v_envelope_id,
            v_envelope_version_id,
            v_object_id,
            v_object_type,
            v_object_version_id,
            v_object_properties_id,
            v_datetime,
            base.active_record_status);

            /* NO COMMIT -- LEFT UP TO CALLING ENVIRONMENT TO COMMIT */
    exception
        when envelope_not_found then
        begin
            raise workflow.unspecified_error;
        end;
        when others then
        begin
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        end;

    end add_envelope_object;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_ENVELOPE
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure modify_envelope
      ( i_envelope_id    in varchar2,
        i_strictness_id  in varchar2,
        i_envelope_name  in varchar2,
        i_envelope_description in varchar2,
        i_modified_by_id  in varchar2,
        i_step_id        in varchar2,
        i_transition_id  in varchar2,
        i_status_id      in varchar2,
        i_priority_id    in varchar2,
        i_crc            in date,
        i_is_comments_null in number,
        o_comments_clob  out clob)
    is
        -- User defined exceptions
        step_not_found exception;   -- Could not find step for specified i_step_id
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        v_envelope_id       pn_workflow_envelope.envelope_id%type   := to_number(i_envelope_id);
        v_strictness_id     pn_workflow_envelope.strictness_id%type := to_number(i_strictness_id);
        v_modified_by_id    pn_workflow_envelope.modified_by_id%type:= to_number(i_modified_by_id);
        v_step_id           pn_envelope_version.step_id%type        := to_number(i_step_id);
        v_transition_id     pn_envelope_version.transition_id%type  := to_number(i_transition_id);
        v_status_id         pn_envelope_version.status_id%type      := to_number(i_status_id);
        v_priority_id       pn_envelope_version.priority_id%type    := to_number(i_priority_id);

        v_datetime      date;
        v_current_crc    pn_workflow_transition.crc%type;
        v_envelope_version_id   pn_envelope_version.version_id%type;
        v_workflow_id           pn_workflow_envelope.workflow_id%type;
        v_entry_status_id pn_workflow_step.entry_status_id%type := null;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW.MODIFY_ENVELOPE';
    begin

        -- Check envelope has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_envelope
            where
                envelope_id = v_envelope_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            /* Grab the entry status id from the step */
            begin
                select
                   entry_status_id into v_entry_status_id
                from
                    pn_workflow_step
                where
                    step_id = v_step_id;
            exception
                when NO_DATA_FOUND then
                begin
                    raise step_not_found;
                end;
            end;

            /* Override passed in status id if the step had an entry status id */
            if v_entry_status_id is not null then
                v_status_id := v_entry_status_id;
            end if;

            -- Create envelope version object entry
            v_envelope_version_id := base.create_object(
                G_envelope_version_object_type,
                v_modified_by_id,
                base.active_record_status);

            select sysdate into v_datetime from dual;

            -- Update Envelope record
            update
                pn_workflow_envelope wfe
            set
               wfe.current_version_id = v_envelope_version_id,
               wfe.strictness_id = v_strictness_id,
               wfe.envelope_name = i_envelope_name,
               wfe.envelope_description = i_envelope_description,
               wfe.modified_by_id = v_modified_by_id,
               wfe.modified_datetime = v_datetime,
               wfe.crc = sysdate
            where
                 wfe.envelope_id = v_envelope_id
            returning
                wfe.workflow_id into v_workflow_id;

            -- **** TBC **** should space be inferred from workflow, or from envelope (if I stored it on the envelope)
            SECURITY.CREATE_SECURITY_PERMISSIONS(v_envelope_version_id, G_envelope_version_object_type, getSpaceID(v_workflow_id), v_modified_by_id);

            if (i_is_comments_null > 0) then
                -- Insert envelope version record with null comments
                insert into pn_envelope_version
                    ( version_id, envelope_id, status_id, step_id, workflow_id,
                      transition_id, priority_id, comments_clob,
                      created_by_id, created_datetime, crc, record_status)
                values
                    ( v_envelope_version_id, v_envelope_id, v_status_id, v_step_id, v_workflow_id,
                      v_transition_id, v_priority_id, null,
                      v_modified_by_id, v_datetime, v_datetime, base.active_record_status)
                returning
                    comments_clob into o_comments_clob;

            else
                -- Insert envelope version record with empty comments
                insert into pn_envelope_version
                    ( version_id, envelope_id, status_id, step_id, workflow_id,
                      transition_id, priority_id, comments_clob,
                      created_by_id, created_datetime, crc, record_status)
                values
                    ( v_envelope_version_id, v_envelope_id, v_status_id, v_step_id, v_workflow_id,
                      v_transition_id, v_priority_id, empty_clob(),
                      v_modified_by_id, v_datetime, v_datetime, base.active_record_status)
                returning
                    comments_clob into o_comments_clob;

            end if;

        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK
         */

    exception
        when others then
        begin
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        end;

    end modify_envelope;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_HISTORY
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    PROCEDURE create_history
      ( i_envelope_id       IN varchar2,
        i_history_action_id IN varchar2,
        i_action_by_id      IN varchar2,
        i_action_datetime   IN date,
        i_history_message_id IN varchar2,
        o_history_id        OUT varchar2,
        o_return_value      OUT number)
    is

        v_envelope_id   pn_envelope_history.envelope_id%type := to_number(i_envelope_id);
        v_history_action_id pn_envelope_history_action.history_action_id%type := to_number(i_history_action_id);
        v_history_message_id pn_envelope_history.history_message_id%type := to_number(i_history_message_id);
        v_action_by_id  pn_envelope_history.action_by_id%type := to_number(i_action_by_id);

        v_history_id    pn_envelope_history.history_id%type;
        v_datetime      date;

        -- Standard error handling
        stored_proc_name VARCHAR2(100):= 'WORKFLOW.CREATE_HISTORY';

    begin

        -- Create new entry in pn_object
        v_history_id := base.create_object(
            G_history_object_type,
            v_action_by_id,
            base.active_record_status);

        select sysdate into v_datetime from dual;

        -- DO I NEED SECURITY FOR HISTORY???? IF YES, I NEED SPACE_ID on ENVELOPE
        --SECURITY.CREATE_SECURITY_PERMISSIONS(v_history_version_id, G_history_object_type, getSpaceID(v_workflow_id), v_modified_by_id);

        -- Insert history record
        insert into pn_envelope_history (
            history_id,
            envelope_id,
            action_by_id,
            history_action_id,
            action_datetime,
            history_message_id,
            crc,
            record_status)
        values (
            v_history_id,
            v_envelope_id,
            v_action_by_id,
            v_history_action_id,
            i_action_datetime,
            v_history_message_id,
            v_datetime,
            base.active_record_status);

        /*
            NO COMMIT OR ROLLBACK
         */
        o_history_id := v_history_id;
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;

    end create_history;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- IS_OBJECT_IN_ACTIVE_ENVELOPE
-- Indicates whether an object is in an active envelope
-- Input Parameters
--      i_object_id     the object id to check for as a number
-- Returns
--      0   - object is not in an active envelope
--      1   - object is in an active envelope
----------------------------------------------------------------------
    function is_object_in_active_envelope
        ( i_object_id   in varchar2)
        return number
    is
    begin
        return is_object_in_active_envelope(to_number(i_object_id));
    end;



    function is_object_in_active_envelope
        ( i_object_id   in number)
        return number
    is
        v_object_id     pn_envelope_has_object_view.object_id%type := to_number(i_object_id);
        v_dummy         number;
        v_result        number := 0;

         stored_proc_name VARCHAR2(100):= 'WORKFLOW.IS_OBJECT_IN_ACTIVE_ENVELOPE';

    begin
        select 1 into v_dummy
        from
            pn_envelope_has_object_view eo
        where
            record_status = 'A' and
            object_id = v_object_id and
            is_active = 1 and
            rownum = 1;

        -- If we got here, then there was one row
        v_result := 1;

        return v_result;

    exception
        when no_data_found then
        begin
            v_result := 0;
            return v_result;
        end;

        when others then
        begin
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
            raise;
        end;
    end is_object_in_active_envelope;




----------------------------------------------------------------------
-- IS_ACTIVE_ENVELOPE
----------------------------------------------------------------------
    FUNCTION IS_ACTIVE_ENVELOPE
      ( i_envelope_id   IN varchar2)
      RETURN number
    IS
        v_envelope_id   pn_workflow_envelope.envelope_id%type := to_number(i_envelope_id);
        v_dummy         number;
        v_result        number := 1;

    BEGIN
        -- Check the envelope's current version has a status which is not Inactive
        BEGIN
            SELECT 1 INTO v_dummy
            FROM
                pn_workflow_envelope wfe
            WHERE
                wfe.envelope_id = v_envelope_id AND exists (
                    SELECT 1
                    FROM
                        pn_envelope_version wfev,
                        pn_workflow_status wfs
                    WHERE
                        wfev.envelope_id = wfe.envelope_id AND
                        wfev.version_id = wfe.current_version_id AND
                        wfev.workflow_id = wfe.workflow_id AND
                        wfs.status_id = wfev.status_id AND
                        wfs.is_inactive = 0                     -- Not Inactive (= Active!)
                    );
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_result := 0;
        END;

        RETURN v_result;
    END IS_ACTIVE_ENVELOPE;
----------------------------------------------------------------------

/*--------------------------------------------------------------------
  WORKFLOW COPY PROCEDURES
--------------------------------------------------------------------*/

----------------------------------------------------------------------
-- COPY_ALL
-- Copies all workflows for a space to another space, not including groups
----------------------------------------------------------------------
    PROCEDURE copy_all
      ( i_from_space_id     IN varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        o_return_value      OUT number)
    is
    begin
        workflow.copy_all(i_from_space_id, i_to_space_id, i_created_by_id, 0, o_return_value);
    end copy_all;

----------------------------------------------------------------------
-- COPY_ALL
-- Copies all workflows for a space to another space
-- AUTONOMOUS TRANSACTION
----------------------------------------------------------------------
    PROCEDURE copy_all
      ( i_from_space_id     IN varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        i_copy_groups       in number,
        o_return_value      OUT number)
    is
        pragma autonomous_transaction;

        -- Cursor to read all workflows for a given space (where workflow not
        -- deleted).
        cursor workflow_cur (i_space_id in number)
        is
            select spwf.workflow_id
            from
                pn_space_has_workflow spwf,
                pn_workflow wf
            where
                space_id = i_space_id and
                spwf.workflow_id = wf.workflow_id and
                wf.record_status <> base.deleted_record_status;

        v_from_space_id pn_space_has_workflow.space_id%type := to_number(i_from_space_id);
        v_to_space_id pn_space_has_workflow.space_id%type := to_number(i_to_space_id);

        v_created_by_id     number := to_number(i_created_by_id);

    begin
        for workflow_rec in workflow_cur(i_from_space_id) loop
            copy_workflow(v_to_space_id, workflow_rec.workflow_id, v_created_by_id, i_copy_groups);
        end loop;

        commit;
        o_return_value := base.operation_successful;

    exception
        when others then
        begin
            rollback;
            base.log_error('WORKFLOW.COPY_ALL', sqlcode, sqlerrm);
            o_return_value := base.plsql_exception;
        end;
    end copy_all;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE copy_workflow
-- Copy specified workflow to specified space_id
-- AUTONOMOUS_TRANSACTION
----------------------------------------------------------------------
    procedure copy_workflow(
        i_to_space_id in number,
        i_workflow_id in number,
        i_created_by_id in number,
        i_copy_groups in number)
    is
        PRAGMA AUTONOMOUS_TRANSACTION;

        -- Exceptions
        workflow_not_found exception;

        -- Cursor for reading workflow
        cursor workflow_cur(i_workflow_id in number) is
            select *
            from pn_workflow
            where workflow_id = i_workflow_id;
        workflow_rec workflow_cur%rowtype;

        -- Cursor for reading object types
        cursor object_type_cur(i_workflow_id in number) is
            select *
            from pn_workflow_has_object_type
            where workflow_id = i_workflow_id;

        v_workflow_id       number;
        v_datetime          date;
        v_created_by_id     number := i_created_by_id;

    begin
        -- Get current datetime
        select sysdate into v_datetime from dual;

        -- Copy the workflow record
        open workflow_cur(i_workflow_id);
        fetch workflow_cur into workflow_rec;
        if workflow_cur%found then
            -- Create new entry in pn_object
            v_workflow_id := base.create_object(
                G_workflow_object_type,
                v_created_by_id,
                base.active_record_status);

            insert into pn_workflow (
                workflow_id,
                workflow_name,
                workflow_description,
                notes,
                owner_id,
                is_published,
                strictness_id,
                created_by_id,
                created_datetime,
                is_generic,
                crc,
                record_status)
            values (
                v_workflow_id,
                workflow_rec.workflow_name,
                workflow_rec.workflow_description,
                workflow_rec.notes,
                workflow_rec.owner_id,
                workflow_rec.is_published,
                workflow_rec.strictness_id,
                v_created_by_id,
                v_datetime,
                workflow_rec.is_generic,
                sysdate,
                base.active_record_status);

            -- Create the space has workflow record
            insert into pn_space_has_workflow
                (space_id, workflow_id)
            values
                (i_to_space_id, v_workflow_id);

            SECURITY.CREATE_SECURITY_PERMISSIONS(v_workflow_id, G_workflow_object_type, i_to_space_id, v_created_by_id);

        else
            raise workflow_not_found;
        end if;
        close workflow_cur;

        -- Copy the object types
        for object_type_rec in object_type_cur(i_workflow_id) loop
            insert into pn_workflow_has_object_type (
                workflow_id,
                object_type,
                sub_type_id
            ) values (
                v_workflow_id,
                object_type_rec.object_type,
                object_type_rec.sub_type_id
            );
        end loop;

        -- Copy steps
        copy_steps(i_workflow_id, v_workflow_id, v_created_by_id, i_copy_groups);

        -- Copy transitions
        copy_transitions(i_workflow_id, v_workflow_id, v_created_by_id, i_copy_groups);

        -- Now clean up intermediate data created by copy_steps
        delete from pn_workflow_step_copy
        where
            workflow_id = i_workflow_id and
            to_workflow_id = v_workflow_id;

        /*
            SUCCESSFULLY COPIED WORKFLOW
         */
        commit;

    exception
        when others then
        begin
            rollback;

            if workflow_cur%isopen then
                close workflow_cur;
            end if;

            raise;
        end;
    end copy_workflow;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE copy_steps
-- copies steps from specified workflow id to specified workflow id
-- sets created by id to specified value
-- DOES NOT COMMIT
----------------------------------------------------------------------
    procedure copy_steps(
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_created_by_id     in number,
        i_copy_groups       in number)
    is
        -- Cursor for reading steps
        cursor step_cur(i_workflow_id in number) is
            select *
            from pn_workflow_step
            where workflow_id = i_workflow_id;

        -- Cursor for step has group records
        cursor step_has_group_cur(i_workflow_id in number, i_step_id in number) is
            select *
            from pn_workflow_step_has_group
            where
                workflow_id = i_workflow_id and
                step_id = i_step_id;

        v_step_id       pn_workflow_step.step_id%type;
        v_from_step_id  pn_workflow_step.step_id%type;
        v_datetime date;
        v_created_by_id number  := i_created_by_id;

    begin
        -- Get current datetime
        select sysdate into v_datetime from dual;

        -- Copy the steps
        for step_rec in step_cur(i_from_workflow_id) loop
            v_from_step_id := step_rec.step_id;

             -- Create new entry in pn_object
            v_step_id := base.create_object(
                G_step_object_type,
                v_created_by_id,
                base.active_record_status);

            SECURITY.CREATE_SECURITY_PERMISSIONS(v_step_id, G_step_object_type, getSpaceID(i_to_workflow_id), v_created_by_id);

            insert into pn_workflow_step (
                step_id,
                workflow_id,
                step_name,
                step_description,
                notes_clob,
                is_initial_step,
                is_final_step,
                entry_status_id,
                subscription_id,
                created_by_id,
                created_datetime,
                crc,
                record_status)
            values (
                v_step_id,
                i_to_workflow_id,
                step_rec.step_name,
                step_rec.step_description,
                step_rec.notes_clob,
                step_rec.is_initial_step,
                step_rec.is_final_step,
                step_rec.entry_status_id,
                step_rec.subscription_id,
                v_created_by_id,
                v_datetime,
                sysdate,
                base.active_record_status);

            -- Now store from and to values of step id so that other copy routines
            -- may correctly update their step_id columns
            insert into pn_workflow_step_copy (
                workflow_id,
                step_id,
                to_workflow_id,
                to_step_id)
            values (
                step_rec.workflow_id,
                v_from_step_id,
                i_to_workflow_id,
                v_step_id);

            -- Only copy groups if specified; group copy makes no sense
            -- when copying between spaces (since we have no idea what
            -- the corresponding groups might be in the target space)
            if i_copy_groups = 1 then

                -- Copy the step groups for the current step
                for step_has_group_rec in step_has_group_cur(i_from_workflow_id, v_from_step_id) loop
                    insert into pn_workflow_step_has_group (
                        step_id,
                        workflow_id,
                        group_id,
                        is_participant,
                        created_by_id,
                        created_datetime,
                        crc,
                        record_status)
                    values (
                        v_step_id,
                        i_to_workflow_id,
                        step_has_group_rec.group_id,
                        step_has_group_rec.is_participant,
                        v_created_by_id,
                        v_datetime,
                        sysdate,
                        base.active_record_status);
                end loop;

            end if;

        end loop;

    end copy_steps;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE copy_transitions
-- copies transitions from specified workflow id to specified workflow id
-- sets created by id to specified value
-- DOES NOT COMMIT
----------------------------------------------------------------------
    procedure copy_transitions (
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_created_by_id     in number,
        i_copy_groups       in number)
    is
        -- Cursor for reading transitions
        cursor transition_cur(i_workflow_id in number) is
            select *
            from pn_workflow_transition
            where workflow_id = i_workflow_id;

        v_transition_id     pn_workflow_transition.transition_id%type;
        v_from_transition_id pn_workflow_transition.transition_id%type;
        v_datetime          date;
        v_created_by_id     number  := i_created_by_id;
        v_new_begin_step_id pn_workflow_transition.begin_step_id%type;
        v_new_end_step_id   pn_workflow_transition.end_step_id%type;

    begin
        -- Get current datetime
        select sysdate into v_datetime from dual;

        -- Copy the transitions
        for transition_rec in transition_cur(i_from_workflow_id) loop
            v_from_transition_id := transition_rec.transition_id;

            -- Lookup new step id values for this transition
            select to_step_id into v_new_begin_step_id
            from
                pn_workflow_step_copy
            where
                workflow_id = transition_rec.workflow_id and
                step_id = transition_rec.begin_step_id;

            select to_step_id into v_new_end_step_id
            from
                pn_workflow_step_copy
            where
                workflow_id = transition_rec.workflow_id and
                step_id = transition_rec.end_step_id;

            -- Create new entry in pn_object
            v_transition_id := base.create_object(
                G_transition_object_type,
                v_created_by_id,
                base.active_record_status);

            SECURITY.CREATE_SECURITY_PERMISSIONS(v_transition_id, G_transition_object_type, getSpaceID(i_to_workflow_id), v_created_by_id);

            insert into pn_workflow_transition (
                workflow_id,
                transition_id,
                transition_verb,
                transition_description,
                begin_step_id,
                end_step_id,
                created_by_id,
                created_datetime,
                crc,
                record_status)
            values (
                i_to_workflow_id,
                v_transition_id,
                transition_rec.transition_verb,
                transition_rec.transition_description,
                v_new_begin_step_id,
                v_new_end_step_id,
                v_created_by_id,
                v_datetime,
                sysdate,
                base.active_record_status);

            workflow_rule.copy_rules(i_from_workflow_id, i_to_workflow_id,
                                     v_from_transition_id, v_transition_id, v_created_by_id,
                                     i_copy_groups);

        end loop;

    end copy_transitions;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE crc_matches
-- Compare two crc values
-- i_original_crc - first crc value
-- i_new_crc - second cec value
-- returns true if both values are identical, false otherwise
----------------------------------------------------------------------
    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean
    is
        v_crc_match     boolean := false;

    begin
        if (i_original_crc = i_new_crc) then
            v_crc_match := true;
        end if;
        return v_crc_match;
    end crc_matches;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE getSpaceID
-- get the space id of a workflow
-- i_workflow_id - the workflow id to get space id from
-- returns space_id
-- throws exception if there is a problem
----------------------------------------------------------------------
    function getSpaceID(i_workflow_id in number)
        return number
    is
        v_space_id  pn_space_has_workflow.space_id%type := null;

    begin
        select
            sp.space_id into v_space_id
        from
            pn_space_has_workflow sp
        where
            sp.workflow_id = i_workflow_id;

        return v_space_id;
    end getSpaceID;
----------------------------------------------------------------------

END; -- Package Body WORKFLOW
/

CREATE OR REPLACE PACKAGE workflow IS
--==================================================================
-- Purpose: PNET Workflow procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        05-Sep-00  Created it.
-- TimM        12-Sep-00  Added i_space_id to CREATE_WORKFLOW
-- TimM        18-Sep-00  Added CREATE_STEP, MODIFY_STEP
-- TimM        19-Sep-00  Added CREATE_TRANSITION, MODIFY_TRANSITION
-- TimM        21-Sep-00  Added CREATE/MODIFY/REMOVE STEP_HAS_GROUP
-- TimM        02-Oct-00  Added CREATE_ENVELOPE
-- TimM        11-Oct-00  Added MODIFY_ENVELOPE
-- TimM        27-Oct-00  Changed signature of STEP_MODIFY, STEP_CREATE
--                        for entry_status_id
-- TimM        01-Nov-00  Changed signature of ADD_ENVELOPE_OBJECT to
--                        change i_object_properties to i_object_properties_id
--==================================================================

   -- Package constants
   G_workflow_object_type       constant pn_object.object_type%type := 'workflow';
   G_step_object_type           constant pn_object.object_type%type := 'workflow_step';
   G_transition_object_type     constant pn_object.object_type%type := 'workflow_transition';
   G_rule_object_type           constant pn_object.object_type%type := 'workflow_rule';
   G_envelope_object_type       constant pn_object.object_type%type := 'workflow_envelope';
   G_envelope_version_object_type constant pn_object.object_type%type := 'workflow_envelope_version';
   G_history_object_type        constant pn_object.object_type%type := 'workflow_envelope_history';

   -- Raise-able errors
   unspecified_error  exception;
   pragma exception_init(unspecified_error, -20000);
   space_not_found    exception;

----------------------------------------------------------------------
-- CREATE_WORKFLOW
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    PROCEDURE create_workflow
      ( i_space_id       IN varchar2,
        i_workflow_name  IN varchar2,
        i_workflow_description IN varchar2,
        i_notes          IN varchar2,
        i_owner_id       IN varchar2,
        i_is_published   IN number,
        i_strictness_id  IN varchar2,
        i_is_generic     in number,
        i_created_by_id  IN varchar2,
        o_workflow_id    OUT varchar2,
        o_return_value   OUT number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_WORKFLOW
-- Update an existing workflow
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure modify_workflow (
        i_workflow_id    in varchar2,
        i_workflow_name  in varchar2,
        i_workflow_description in varchar2,
        i_notes          in varchar2,
        i_owner_id       in varchar2,
        i_is_published   in number,
        i_strictness_id  in varchar2,
        i_is_generic     in number,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_WORKFLOW
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_workflow (
        i_workflow_id    in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_STEP
----------------------------------------------------------------------
    procedure create_step
      ( i_workflow_id       in varchar2,
        i_step_name         in varchar2,
        i_step_sequence     in varchar2,
        i_step_description  in varchar2,
        i_is_initial_step   in number,
        i_is_final_step     in number,
        i_entry_status_id   in number,
        i_subscription_id   in number,
        i_created_by_id     in varchar2,
        i_is_notes_null     in number,
        o_notes_clob        out clob,
        o_step_id           out varchar2);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_STEP
-- Update an existing step
----------------------------------------------------------------------
    procedure modify_step (
        i_step_id        in varchar2,
        i_step_name      in varchar2,
        i_step_sequence      in varchar2,
        i_step_description in varchar2,
        i_is_initial_step in number,
        i_is_final_step  in number,
        i_entry_status_id   in number,
        i_subscription_id   in number,
        i_modified_by_id in varchar2,
        i_crc            in date,
        i_is_notes_null     in number,
        o_notes_clob        out clob);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_STEP
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_step (
        i_step_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_TRANSITION
----------------------------------------------------------------------
    procedure create_transition
      ( i_workflow_id            in varchar2,
        i_transition_verb        in varchar2,
        i_transition_description in varchar2,
        i_begin_step_id          in varchar2,
        i_end_step_id            in varchar2,
        i_created_by_id          in varchar2,
        o_transition_id          out varchar2,
        o_return_value           out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_TRANSITION
-- Update an existing transition
----------------------------------------------------------------------
    procedure modify_transition (
        i_transition_id          in varchar2,
        i_transition_verb        in varchar2,
        i_transition_description in varchar2,
        i_begin_step_id          in varchar2,
        i_end_step_id            in varchar2,
        i_modified_by_id         in varchar2,
        i_crc                    in date,
        o_return_value           out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_TRANSITION
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    -- no output parms to facilitate batching
    procedure remove_transition (
        i_transition_id  in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date);

    procedure remove_transition (
        i_transition_id  in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_STEP_GROUP
----------------------------------------------------------------------
    procedure create_step_group (
        i_step_id                in varchar2,
        i_workflow_id            in varchar2,
        i_group_id               in varchar2,
        i_is_participant         in number,
        i_created_by_id          in varchar2,
        o_return_value           out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_STEP_GROUP
-- Update an existing step group
----------------------------------------------------------------------
    procedure modify_step_group (
        i_step_id                in varchar2,
        i_workflow_id            in varchar2,
        i_group_id               in varchar2,
        i_is_participant         in number,
        i_modified_by_id         in varchar2,
        i_crc                    in date,
        o_return_value           out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_STEP_GROUP
-- Remove a step group
----------------------------------------------------------------------
    procedure remove_step_group (
        i_step_id                in varchar2,
        i_workflow_id            in varchar2,
        i_group_id               in varchar2,
        i_modified_by_id         in varchar2,
        i_crc                    in date,
        o_return_value           out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_ENVELOPE
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure create_envelope
      ( i_workflow_id    in varchar2,
        i_strictness_id  in varchar2,
        i_envelope_name  in varchar2,
        i_envelope_description in varchar2,
        i_created_by_id  in varchar2,
        i_status_id      in varchar2,
        i_priority_id    in varchar2,
        i_is_comments_null in number,
        o_comments_clob  out clob,
        o_envelope_id    out varchar2);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- ADD_ENVELOPE_OBJECT
-- DOES NOT COMMIT OR ROLLBACK
-- Adds an object to the specified envelope by creating
-- pn_envelope_has object and pn_envelope_version_has_object
-- records (for the current version)
----------------------------------------------------------------------
    procedure add_envelope_object
      ( i_envelope_id    in varchar2,
        i_object_id       in varchar2,
        i_object_type     in varchar2,
        i_object_version_id in varchar2,
        i_object_properties_id in varchar2,
        i_created_by_id  in varchar2);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_ENVELOPE
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure modify_envelope
      ( i_envelope_id    in varchar2,
        i_strictness_id  in varchar2,
        i_envelope_name  in varchar2,
        i_envelope_description in varchar2,
        i_modified_by_id  in varchar2,
        i_step_id        in varchar2,
        i_transition_id  in varchar2,
        i_status_id      in varchar2,
        i_priority_id    in varchar2,
        i_crc            in date,
        i_is_comments_null in number,
        o_comments_clob  out clob);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- CREATE_HISTORY
----------------------------------------------------------------------
    PROCEDURE create_history
      ( i_envelope_id       IN varchar2,
        i_history_action_id IN varchar2,
        i_action_by_id      IN varchar2,
        i_action_datetime   IN date,
        i_history_message_id IN varchar2,
        o_history_id        OUT varchar2,
        o_return_value      OUT number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- IS_ACTIVE_ENVELOPE
-- Returns:
--      1 - Envelope is active
--      0 - Envelope is not active
----------------------------------------------------------------------
    FUNCTION IS_ACTIVE_ENVELOPE
      ( i_envelope_id   IN varchar2)
      RETURN number;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- IS_OBJECT_IN_ACTIVE_ENVELOPE
-- Indicates whether an object is in an active envelope
-- Input Parameters
--      i_object_id     the object id to check for
-- Returns
--      0   - object is not in an active envelope
--      1   - object is in an active envelope
----------------------------------------------------------------------
    function is_object_in_active_envelope
        ( i_object_id   in number)
        return number;

    function is_object_in_active_envelope
        ( i_object_id   in varchar2)
        return number;

/**
 * COPY_ALL
 * See below for details
 */
    PROCEDURE copy_all
      ( i_from_space_id     IN varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        o_return_value      OUT number);

/**
 * COPY_ALL
 * Copies all workflows for a space to another space
 * @param i_from_space_id space id of space from which to copy
 * @param i_to_space_id space id of space to which to copy
 * @param i_created_by_id user id of person performing copy
 * @param i_copy_groups 1 = copy groups associated with workflows;
 *                      0 = do not copy groups associated with workflows
 *   Specify "1" when copying a workflow within the same space
 *   Otherwise "0" should be specified, since the groups will be different
 *   between spaces.
 * @param o_return_value Returns a status code indicating the success
 */
    PROCEDURE copy_all
      ( i_from_space_id     IN varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        i_copy_groups       in number,
        o_return_value      OUT number);
----------------------------------------------------------------------

END; -- Package Specification WORKFLOW
/

CREATE OR REPLACE PACKAGE BODY workflow_rule IS
--==================================================================
-- Purpose: Workflow procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        26-Sep-00  Created it.
-- TimM        26-Oct-00  Added copy_rules from WORKFLOW package and
--                        added remove_rule
--==================================================================

/*--------------------------------------------------------------------
  PRIVATE PROCEDURE PROTOTYPES
  Required for private procedures so that they can be used
  lexically earlier than their definition.
--------------------------------------------------------------------*/

    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean;

    procedure copy_rule_auth(
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_from_transition_id    in number,
        i_to_transition_id      in number,
        i_from_rule_id          in number,
        i_to_rule_id            in number,
        i_created_by_id         in number,
        i_copy_groups           in number);

/*--------------------------------------------------------------------
  END OF PROCEDURE PROTOTYPES
--------------------------------------------------------------------*/

----------------------------------------------------------------------
-- CREATE_RULE
----------------------------------------------------------------------
    procedure create_rule
      ( i_workflow_id    in varchar2,
        i_transition_id  in varchar2,
        i_rule_name      in varchar2,
        i_rule_description in varchar2,
        i_notes          in varchar2,
        i_rule_status_id in varchar2,
        i_rule_type_id   in varchar2,
        i_created_by_id  in varchar2,
        o_rule_id        out varchar2,
        o_return_value   out number)
    is
        -- Convert input varchar2() to numbers
        v_workflow_id    pn_workflow_rule.workflow_id%type := to_number(i_workflow_id);
        v_transition_id  pn_workflow_rule.transition_id%type := to_number(i_transition_id);
        v_rule_status_id pn_workflow_rule.rule_status_id%type := to_number(i_rule_status_id);
        v_rule_type_id   pn_workflow_rule.rule_type_id%type := to_number(i_rule_type_id);
        v_created_by_id  pn_workflow_rule.created_by_id%type := to_number(i_created_by_id);

        v_datetime      date;
        v_rule_id       pn_workflow_rule.rule_id%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW_RULE.CREATE_RULE';

    begin

        -- Create new entry in pn_object
        v_rule_id := base.create_object(
            G_rule_object_type,
            v_created_by_id,
            base.active_record_status);

        -- Get current datetime
        SELECT SYSDATE INTO v_datetime FROM dual;

        -- Create the step record
        insert into pn_workflow_rule (
            rule_id,
            workflow_id,
            transition_id,
            rule_name,
            rule_description,
            notes,
            rule_status_id,
            rule_type_id,
            created_by_id,
            created_datetime,
            crc,
            record_status)
        values (
            v_rule_id,
            v_workflow_id,
            v_transition_id,
            i_rule_name,
            i_rule_description,
            i_notes,
            v_rule_status_id,
            v_rule_type_id,
            v_created_by_id,
            v_datetime,
            v_datetime,
            base.active_record_status);

        -- Make it so.
        COMMIT;

        -- Set output parameters
        o_rule_id := to_char(v_rule_id);
        o_return_value := BASE.OPERATION_SUCCESSFUL;

      exception
      when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log values (
                sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;
    end create_rule;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_RULE
-- Update existing rule
----------------------------------------------------------------------
    procedure modify_rule (
        i_rule_id        in varchar2,
        i_rule_name      in varchar2,
        i_rule_description in varchar2,
        i_notes          in varchar2,
        i_rule_status_id in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_rule_id        pn_workflow_rule.rule_id%type := to_number(i_rule_id);
        v_modified_by_id pn_workflow_rule.modified_by_id%type := to_number(i_modified_by_id);
        v_rule_status_id pn_workflow_rule.rule_status_id%type := to_number(i_rule_status_id);

        v_datetime      date;
        v_current_crc    pn_workflow_rule.crc%type;

        -- Standard error handling
        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'WORKFLOW_RULE.MODIFY_RULE';

    begin
        -- Check step has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_rule
            where rule_id = v_rule_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then

            select sysdate into v_datetime from dual;

            -- Update record
            update
                pn_workflow_rule wfrule
            set
                wfrule.rule_name = i_rule_name,
                wfrule.rule_description = i_rule_description,
                wfrule.notes = i_notes,
                wfrule.rule_status_id = v_rule_status_id,
                wfrule.modified_by_id = v_modified_by_id,
                wfrule.modified_datetime = v_datetime,
                wfrule.crc = v_datetime
            where
                wfrule.rule_id = v_rule_id;

        else
            -- crc different
            raise record_modified;
        end if;

        commit;
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when record_locked then
        begin
            rollback;
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            rollback;
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
           o_return_value := BASE.PLSQL_EXCEPTION;
           rollback;
           err_num := sqlcode;
           err_msg := substr(sqlerrm,1,120);
           insert into pn_sp_error_log
               values (sysdate, stored_proc_name, err_num, err_msg);
           commit;
        end;
    end modify_rule;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_RULE
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_rule (
        i_rule_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number)
    is
        -- User defined exceptions
        record_modified exception;  -- CRC differs
        record_locked   exception;  -- Record being updated by another user
        pragma exception_init (record_locked, -00054);

        -- Convert input varchar2() to numbers
        v_rule_id        pn_workflow_rule.rule_id%type := to_number(i_rule_id);
        v_modified_by_id pn_workflow_rule.modified_by_id%type := to_number(i_modified_by_id);

        v_datetime      date;
        v_current_crc    pn_workflow_rule.crc%type;

        stored_proc_name VARCHAR2(100):= 'WORKFLOW_RULE.REMOVE_RULE';

    begin
        -- Check rule has not changed and lock it at the same time
        select crc into v_current_crc
            from pn_workflow_rule
            where rule_id = v_rule_id
            for update nowait;

        if (crc_matches(i_crc, v_current_crc)) then
            select sysdate into v_datetime from dual;
            -- Update record
            update
                pn_workflow_rule wfrule
            set
                wfrule.modified_by_id = v_modified_by_id,
                wfrule.modified_datetime = v_datetime,
                wfrule.crc = sysdate,
                wfrule.record_status = base.deleted_record_status
            where
                wfrule.rule_id = v_rule_id;

        else
            -- crc different
            raise record_modified;
        end if;

        /*
            NO COMMIT OR ROLLBACK
         */
        o_return_value := BASE.OPERATION_SUCCESSFUL;

    exception
        when record_locked then
        begin
            o_return_value := BASE.UPDATE_RECORD_LOCKED;
        end;
        when record_modified then
        begin
            -- Record out of sync
            o_return_value := BASE.UPDATE_RECORD_OUT_OF_SYNC;
        end;
        when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error(stored_proc_name, sqlcode, sqlerrm);
        end;
    end remove_rule;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- copy_rules
-- DOES NOT COMMIT
----------------------------------------------------------------------
    procedure copy_rules (
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_from_transition_id    in number,
        i_to_transition_id      in number,
        i_created_by_id    in number,
        i_copy_groups      in number)
    is
        -- Cursor for reading rules
        cursor rule_cur(i_workflow_id in number, i_transition_id in number) is
            select *
            from pn_workflow_rule
            where
                workflow_id = i_workflow_id and
                transition_id = i_transition_id;

        unknown_rule_type   exception;

        v_rule_id           pn_workflow_rule.rule_id%type;
        v_datetime          date;
        v_created_by_id     number  := i_created_by_id;

        v_from_rule_id      pn_workflow_rule.rule_id%type;

    begin
        -- Get current datetime
        select sysdate into v_datetime from dual;

        -- Copy the rules
        for rule_rec in rule_cur(i_from_workflow_id, i_from_transition_id) loop
            v_from_rule_id := rule_rec.rule_id;

            -- Create new entry in pn_object
            v_rule_id := base.create_object(
                G_rule_object_type,
                v_created_by_id,
                base.active_record_status);

            insert into pn_workflow_rule (
                rule_id,
                workflow_id,
                transition_id,
                rule_name,
                rule_description,
                notes,
                rule_status_id,
                rule_type_id,
                created_by_id,
                created_datetime,
                crc,
                record_status)
            values (
                v_rule_id,
                i_to_workflow_id,
                i_to_transition_id,
                rule_rec.rule_name,
                rule_rec.rule_description,
                rule_rec.notes,
                rule_rec.rule_status_id,
                rule_rec.rule_type_id,
                v_created_by_id,
                v_datetime,
                sysdate,
                base.active_record_status);

            /*
                Now copy specific rule
             */
            if rule_rec.rule_type_id = '100' then
                -- Authorization rule
                copy_rule_auth(i_from_workflow_id, i_to_workflow_id,
                               i_from_transition_id, i_to_transition_id,
                               v_from_rule_id, v_rule_id, v_created_by_id,
                               i_copy_groups);

            -- else if rule_rec.rule_type_id = 'xxx' then

            else
                raise unknown_rule_type;
            end if;

         end loop;

    end copy_rules;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE copy_rule_auth
-- DOES NOT COMMIT
----------------------------------------------------------------------
    procedure copy_rule_auth(
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_from_transition_id    in number,
        i_to_transition_id      in number,
        i_from_rule_id          in number,
        i_to_rule_id            in number,
        i_created_by_id         in number,
        i_copy_groups           in number)

    is

         -- Cursor for reading pn_wf_rule_auth record (there should be 1)
         cursor rule_auth_cur(i_from_workflow_id in number,
                              i_from_transition_id in number,
                              i_from_rule_id in number) is
            select *
            from pn_wf_rule_auth
            where
                workflow_id = i_from_workflow_id and
                transition_id = i_from_transition_id and
                rule_id = i_from_rule_id;
        rule_auth_rec   rule_auth_cur%rowtype;

        -- Cursor for reading rule_auth_has_group (multiple)
        cursor rule_auth_group_cur(i_from_workflow_id in number,
                                   i_from_transition_id in number,
                                   i_from_rule_id in number) is
            select *
            from pn_wf_rule_auth_has_group
            where
                workflow_id = i_from_workflow_id and
                transition_id = i_from_transition_id and
                rule_id = i_from_rule_id;

        v_new_step_id   pn_wf_rule_auth_has_group.step_id%type;

    begin

        open rule_auth_cur(i_from_workflow_id, i_from_transition_id, i_from_rule_id);
        fetch rule_auth_cur into rule_auth_rec;
        if rule_auth_cur%found then
            insert into pn_wf_rule_auth (
                rule_id,
                workflow_id,
                transition_id,
                crc,
                record_status
            ) values (
                i_to_rule_id,
                i_to_workflow_id,
                i_to_transition_id,
                sysdate,
                base.active_record_status
            );

            -- Only copy rule groups if flag indicates we are
            -- copying groups
            if i_copy_groups = 1 then

                for rule_auth_group_rec in rule_auth_group_cur(
                        i_from_workflow_id, i_from_transition_id, i_from_rule_id) loop

                    -- First determine new step id
                    select to_step_id into v_new_step_id
                    from
                        pn_workflow_step_copy
                    where
                        workflow_id = rule_auth_group_rec.workflow_id and
                        step_id = rule_auth_group_rec.step_id;

                    insert into pn_wf_rule_auth_has_group (
                        rule_id,
                        workflow_id,
                        transition_id,
                        group_id,
                        step_id
                    ) values (
                        i_to_rule_id,
                        i_to_workflow_id,
                        i_to_transition_id,
                        rule_auth_group_rec.group_id,
                        v_new_step_id
                    );
                end loop;

            end if; -- i_copy_groups = 1

        else
            -- Hmm, no data; thats ok, not an error
            null;
        end if;

    exception
        when others then
        begin
            if rule_auth_cur%isopen then
                close rule_auth_cur;
            end if;

            raise;
        end;

    end copy_rule_auth;
----------------------------------------------------------------------

----------------------------------------------------------------------
-- PRIVATE crc_matches
-- Compare two crc values
-- i_original_crc - first crc value
-- i_new_crc - second cec value
-- returns true if both values are identical, false otherwise
----------------------------------------------------------------------
    function crc_matches (i_original_crc in date, i_new_crc in date)
        return boolean
    is
        v_crc_match     boolean := false;

    begin
        if (i_original_crc = i_new_crc) then
            v_crc_match := true;
        end if;
        return v_crc_match;
    end crc_matches;
----------------------------------------------------------------------

END; -- Package Body WORKFLOW_RULE
/

CREATE OR REPLACE PACKAGE workflow_rule IS
--==================================================================
-- Purpose: PNET Workflow Rule procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- TimM        26-Sep-00  Created it.
--==================================================================

   -- Package constants
   G_workflow_object_type       pn_object.object_type%type := 'workflow';
   G_step_object_type           pn_object.object_type%type := 'workflow_step';
   G_transition_object_type     pn_object.object_type%type := 'workflow_transition';
   G_rule_object_type           pn_object.object_type%type := 'workflow_rule';

----------------------------------------------------------------------
-- CREATE_RULE
----------------------------------------------------------------------
    procedure create_rule
      ( i_workflow_id    in varchar2,
        i_transition_id  in varchar2,
        i_rule_name      in varchar2,
        i_rule_description in varchar2,
        i_notes          in varchar2,
        i_rule_status_id in varchar2,
        i_rule_type_id   in varchar2,
        i_created_by_id  in varchar2,
        o_rule_id        out varchar2,
        o_return_value   out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- MODIFY_RULE
-- Update existing rule
----------------------------------------------------------------------
    procedure modify_rule (
        i_rule_id        in varchar2,
        i_rule_name      in varchar2,
        i_rule_description in varchar2,
        i_notes          in varchar2,
        i_rule_status_id in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- REMOVE_RULE
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure remove_rule (
        i_rule_id        in varchar2,
        i_modified_by_id in varchar2,
        i_crc            in date,
        o_return_value   out number);
----------------------------------------------------------------------

----------------------------------------------------------------------
-- COPY_RULES
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure copy_rules (
        i_from_workflow_id  in number,
        i_to_workflow_id    in number,
        i_from_transition_id    in number,
        i_to_transition_id      in number,
        i_created_by_id     in number,
        i_copy_groups       in number);
----------------------------------------------------------------------

END; -- Package Specification WORKFLOW_RULE
/

