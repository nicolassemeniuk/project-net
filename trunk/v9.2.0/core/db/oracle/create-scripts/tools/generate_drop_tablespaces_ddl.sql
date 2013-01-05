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
   Generates a DDL script to drop all application tablespaces.
   
   Filename: drop_tablespaces.sql
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

select 'Creating script drop_tablespaces.sql' from dual;

spool drop_tablespaces.sql
set term off

select 'set heading off' from dual;
select 'prompt WARNING: This script will drop all application tablespaces.' from dual;
select '' from dual;
select 'select ''Dropping...'' from dual;' from dual;
select '' from dual;

select 
  'DROP TABLESPACE ' || tablespace_name || ';'
from 
  dba_tablespaces 
where 
  tablespace_name like '%_INDEX'
  or tablespace_name like '%_DATA'
  or tablespace_name like '%_LOB';

select 'select ''Done.'' from dual;' from dual;
select 'exit;' from dual;
select '' from dual;

spool off
set term on
select 'Done.' from dual;
exit;