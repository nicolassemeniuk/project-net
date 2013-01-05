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
CREATE OR REPLACE Package help
IS

/*-------------------------------------------------------------------------------
Description: Package contains procedures for inserting and deleting
             HELP_FEEDBACK table.

Modification History
Date      Programmer  Code  Reason
========  ==========  ====  =================================================
06/21/00  RStutman          Creation.
--------------------------------------------------------------------------------*/

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
value_too_large CONSTANT NUMBER:=107;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;
e_value_too_large EXCEPTION;

PRAGMA EXCEPTION_INIT (e_value_too_large, -01401);
PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);


PROCEDURE add_feedback  (i_person_id IN NUMBER,
                         i_project_id IN NUMBER,
                         i_subject IN VARCHAR2,
                         i_key_id IN NUMBER,
                         i_comments IN VARCHAR2,
                         o_status OUT NUMBER);


END; -- Package Specification HELP
/

