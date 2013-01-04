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
  Script to generate a private synonym creation script for the PNET_USER
  object in the PNET schema.   The project.net database is created with public
  synonyms by defualt.   You may drop all project.net public synonyms and create
  private synonyms using the script created by this.

  Run this script when it is necessary to (re-)create all synonyms.

  It produces the following file in the output directory:
    create_private_synonyms.sql

  This script makes no updates in the database.

  You will be prompted to enter the name of a directory into which to write
  the generated script.  Enter a directory path without a trailing slash.
*/
set linesize 32767
set pagesize 0
set echo off
set feedback off
set heading off
set verify off
set markup html off spool off
set trimspool on

spool create_private_synonyms.sql

SELECT   'create synonym ' || object_name || ' for pnet.' || object_name || ';'
  FROM   all_objects
 WHERE   owner = 'PNET'
   AND   object_type IN ('PACKAGE', 'TABLE', 'VIEW', 'SEQUENCE', 'TRIGGER', 'TYPE')
ORDER BY object_type
/

select 'exit;' from dual
/

spool off
exit;
