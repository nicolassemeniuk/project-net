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
  Script to generate a "create or replace public synonym ..." script for each table and
  view in the PNET schema.
    
  Run this script when it is necessary to re-create all public synonyms.

  It produces the following file in the output directory:
    create_public_synonyms.sql

  This script makes no updates in the database.

  You will be prompted to enter the name of a directory into which to write
  the generated script.  Enter a directory path without a trailing slash.

*/
set linesize 32767
set pagesize 0
set echo off
set feedback off
set heading off
set trimspool on

define my_dir = &1 

spool &my_dir/create_public_synonyms.sql
SELECT   'create or replace public synonym ' || object_name || ' for pnet.' || object_name || ';'
  FROM   all_objects
 WHERE   owner = 'PNET'
   AND   object_type IN ('PACKAGE', 'TABLE', 'VIEW', 'SEQUENCE')
ORDER BY object_type
/

select 'exit;' from dual
/

spool off

exit;
