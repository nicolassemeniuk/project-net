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
             modified_date, record_status, crc, is_used)
         VALUES
            (v_object_id, i_name, i_description, null, i_is_global,
             NULL, i_creator_id, i_creator_id, v_timestamp,
             v_timestamp, g_active_record_status, v_timestamp, 1)
         returning
             use_scenario_clob into o_use_scenario_clob;

    else
        -- Insert empty_clob() use scenario for subsequent streaming
        INSERT INTO pn_methodology_space
            (methodology_id, methodology_name, methodology_desc, use_scenario_clob,
             is_global, status_id, created_by_id, modified_by_id, created_date,
             modified_date, record_status, crc, is_used)
         VALUES
            (v_object_id, i_name, i_description, empty_clob(), i_is_global,
             NULL, i_creator_id, i_creator_id, v_timestamp,
             v_timestamp, g_active_record_status, v_timestamp, 1)
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
      ) or m.person_id = i_user_id or m.is_global = 1 and s.parent_space_name != 'Personal')
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

