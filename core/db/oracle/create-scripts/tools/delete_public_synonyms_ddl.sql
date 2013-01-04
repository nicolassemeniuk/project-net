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
set linesize 32767
set pagesize 0
set echo off
set feedback off
set heading off
set trimspool on

define my_dir = &1

spool &my_dir/delete_public_synonyms.sql
SELECT 'drop public synonym ' || synonym_name || ';'
  FROM dba_synonyms
 WHERE owner = 'PUBLIC'
   AND synonym_name LIKE 'PN%'
ORDER BY object_type
/

select 'exit;' from dual
/

spool off

exit;
