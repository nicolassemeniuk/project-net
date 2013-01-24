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
/**  
   $RCSfile$
   Generates a DDL script to recompile all views and packages.
   
   Filename: recompile_objects.sql
*/
set newpage 0
set space 0
set linesize 32767
set pagesize 0
set echo off
set feedback off
set heading off
set verify off
set trimspool on

define database_version_directory = &1

select 'Creating script ..\versions\&database_version_directory\recompile_objects.sql' from dual;

spool ..\versions\&database_version_directory\recompile_objects.sql
set term off

select '/** $'||'RCSfile: recompile_objects.sql,v $ */' from dual;
select '' from dual;

select '/**' from dual;
select ' * Recompile Views' from dual;
select ' */' from dual;

select 'alter '||object_type||' '||owner||'.'||object_name||' compile;'
from all_objects
where owner = 'PNET' and object_type = 'VIEW'
order by object_name;
select '' from dual;

select '/**' from dual;
select ' * Recompile Packages' from dual;
select ' */' from dual;

select 'alter '||object_type||' '||owner||'.'||object_name||' compile body;'
from all_objects
where owner = 'PNET' and object_type = 'PACKAGE'
order by object_name;

select '/**' from dual;
select ' * Recompile Triggers' from dual;
select ' */' from dual;

select 'alter '||object_type||' PNET.'||object_name|| ' compile;'
from user_objects
where object_type = 'TRIGGER'
order by object_type, object_name;

spool off
set term on
select 'Done.' from dual;

exit;