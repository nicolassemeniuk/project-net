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
/*
* -------------------------NOW CREATING USERS  ------------------------- */
PROMPT Dropping PNET user
drop user pnet cascade;

PROMPT Dropping PNET_USER user
drop user pnet_user cascade;

PROMPT Dropping PNET_ROLE role
drop role pnet_role;

def pnet_password = "PNET" (CHAR)
def pnet_user_password = "PNET_USER" (CHAR)
accept pnet_password char  prompt 'Please insert password for PNET user [default PNET]:'
accept pnet_user_password char  prompt 'Please insert password for PNET_USER user [default PNET_USER]:'


/*
* CREATE ROLE pnet_role */
PROMPT Creating PNET_ROLE
create role pnet_role;

/*
* CREATE USER PNET */



create user PNET identified by "&pnet_password"
   default tablespace data01
   temporary tablespace temp;
   grant create session to PNET;
   GRANT ALTER session TO PNET;
   grant create table to PNET;
   grant create view to PNET;
   grant create sequence to PNET;
   grant create procedure to PNET;
   grant create public synonym to PNET;
   grant select_catalog_role to PNET;
   grant create snapshot to PNET;
   grant create database link to PNET;
   grant create trigger to PNET;
   grant create type to PNET;
   grant drop public synonym to PNET;

/*
* ALTER USER PNET */
ALTER USER PNET 
QUOTA UNLIMITED ON data01
QUOTA UNLIMITED ON index01;
ALTER USER PNET 
QUOTA UNLIMITED ON FORMS_DATA
QUOTA UNLIMITED ON FORMS_INDEX;


/*
* CREATE USER PNET_USER */
create user PNET_USER identified by "&pnet_user_password"
   default tablespace data01
   temporary tablespace temp;
   grant create session to PNET_USER;
   grant pnet_role to PNET_USER;

ALTER USER PNET_USER
   QUOTA UNLIMITED ON FORMS_DATA
   QUOTA UNLIMITED ON FORMS_INDEX;


/*
* APPLY GRANTS */
-- pnet_role -- temporarily, until tables are set

grant create table to pnet_role;
grant create any synonym to pnet_role;
grant select any table to pnet_role;
grant insert any table to pnet_role;
grant delete any table to pnet_role;
grant update any table to pnet_role;
-- needed for forms
grant create any table to pnet_role;
grant execute any procedure to pnet_role;
grant select any sequence to pnet_role;
grant select_catalog_role to pnet_role;
-- needed for forms
grant alter any table to pnet_role;
grant create trigger to pnet_role;
-- Needed for scheduling
grant execute any type TO pnet_role;
grant pnet_role to PNET;
exit;
