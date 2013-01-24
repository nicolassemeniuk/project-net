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
   Updates pn_doc_repository_base rows.
*/
set echo off
set feedback off
set heading off
set verify off

prompt Enter the path to the document repository root for this database
prompt For example: \\ox\docvault\v76stage\
prompt This assumes there are 3 sub-directories at this path called
prompt vault1, vault2 and vault3
accept path prompt 'Enter a path (a trailing slash is required): '

prompt 101 ==> &path.vault1
update pn_doc_repository_base set repository_path = '&path.vault1' where repository_id = 101;
prompt 102 ==> &path.vault2
update pn_doc_repository_base set repository_path = '&path.vault2' where repository_id = 102;
prompt 103 ==> &path.vault3
update pn_doc_repository_base set repository_path = '&path.vault3' where repository_id = 103;
commit;

