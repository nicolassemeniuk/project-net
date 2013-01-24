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

