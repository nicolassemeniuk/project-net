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
   Script to generate the update_system_properties.sql script.
 
   Constructing the insert statements it replaces single-quotes with
   doubled-up single quotes and replaces 
   cr-lf combinations with text to insert chr(10) and lf characters with text 
   to insert chr(10)
   
   Filename: update_system_properties.sql
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

select 'Creating script ..\versions\&database_version_directory\update_system_properties.sql' from dual;

spool ..\versions\&database_version_directory\update_system_properties.sql
set term off

select '/** $'||'RCSfile: update_system_properties.sql,v $ */' from dual;
select '' from dual;

select 'set scan off' from dual;
select 'set feedback off' from dual;
select '' from dual;

select 'prompt Deleting...' from dual;
select '-- First delete any existing temporary tokens' from dual;
select 'DELETE FROM pn_property WHERE context_id = 2000;' from dual;
select 'prompt Done' from dual;
select '' from dual;

select '--' from dual;
select '-- Insert all Properties' from dual;
select '-- Note: "&" must be escaped to "\&"' from dual;
select 'prompt Inserting...' from dual;
select '' from dual;

select 'INSERT INTO pn_property' || CHR(10) ||
       '  (CONTEXT_ID,LANGUAGE,PROPERTY_TYPE,PROPERTY,PROPERTY_VALUE,PROPERTY_VALUE_CLOB,RECORD_STATUS,IS_SYSTEM_PROPERTY,IS_TRANSLATABLE_PROPERTY)' || CHR(10) ||
       'VALUES' || CHR(10) ||
       '  (''2000'',''' || language || ''',''' || property_type || ''',''' || property || ''',''' || replace(property_value, '''', '''''') || ''',''' || replace(replace(replace(dbms_lob.substr(property_value_clob, 4000, 1), '''', ''''''), chr(13)||chr(10), '''||chr(10)||'''), chr(10), '''||chr(10)||''') || ''',''' || record_status || ''',''' || is_system_property || ''',''' || is_translatable_property || ''')' || CHR(10) ||
       '/' from pn_property where context_id = 2000;

select '' from dual;
select 'prompt Done' from dual;
select '' from dual;
select '--' from dual;
select '-- End of insert all properties' from dual;
select '--' from dual;
select '' from dual;

select 'set feedback on' from dual;
select 'set scan on' from dual;
select '' from dual;

select 'INSERT INTO database_version_update' from dual;
select '(MAJOR_VERSION,MINOR_VERSION,SUB_MINOR_VERSION,PATCH_FILENAME,PATCH_DESCRIPTION,TIMESTAMP)'  from dual;
select 'VALUES'  from dual;
select '(x,x,x,''update_system_properties.sql'',''Updated pn_property to contain new system properties.'', SYSDATE)' from dual;
select '/' from dual;

spool off
set term on
select 'Done.' from dual;

exit;