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

