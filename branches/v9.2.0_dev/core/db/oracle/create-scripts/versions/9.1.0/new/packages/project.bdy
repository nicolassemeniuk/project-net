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

