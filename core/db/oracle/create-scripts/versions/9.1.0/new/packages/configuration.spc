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

