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

