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


/*
* CREATE ROLE pnet_role */
create role pnet_role;

/*
* CREATE USER PNET */

create user &1 identified by "&2"
   default tablespace data01
   temporary tablespace temp;
   grant create session to &1;
   GRANT ALTER session TO &1;
   grant create table to &1;
   grant create view to &1;
   grant create sequence to &1;
   grant create procedure to &1;
   grant create public synonym to &1;
   grant select_catalog_role to &1;
   grant create snapshot to &1;
   grant create database link to &1;
   grant create trigger to &1;
   grant create type to &1;
   grant drop public synonym to &1;

/*
* ALTER USER &1 */
ALTER USER &1 
QUOTA UNLIMITED ON data01
QUOTA UNLIMITED ON index01;
ALTER USER &1 
QUOTA UNLIMITED ON FORMS_DATA
QUOTA UNLIMITED ON FORMS_INDEX;


/*
* CREATE USER PNET_USER */
create user &3 identified by "&4"
   default tablespace data01
   temporary tablespace temp;
   grant create session to &3;
   grant pnet_role to &3;

ALTER USER &3
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
grant pnet_role to &1;
exit;
