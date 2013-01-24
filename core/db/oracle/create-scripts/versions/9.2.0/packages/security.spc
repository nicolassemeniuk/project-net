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


END; -- Package Specification SECURITY
/

