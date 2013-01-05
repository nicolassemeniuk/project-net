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

