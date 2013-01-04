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

