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

