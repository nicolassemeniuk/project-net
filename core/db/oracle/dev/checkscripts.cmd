@echo off
rem 
rem Copyright 2000-2009 Project.net Inc.
rem 
rem This file is part of Project.net.
rem Project.net is free software: you can redistribute it and/or modify it under the terms of 
rem the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
rem 
rem Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
rem without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
rem See the GNU General Public License for more details.
rem 
rem You should have received a copy of the GNU General Public License along with Project.net.
rem If not, see http://www.gnu.org/licenses/gpl-3.0.html

rem
rem $RCSfile$
rem
rem Checks database scripts for issues

echo ^> Checking for forward slash preceded by spaces
find ../CreateScripts/versions -name '*.sql' | xargs grep '^[[:space:]]\+/[[:space:]]*$'

rem Skips 2.* version directories since we didn't check back then
echo ^> Checking for missing CVS keyword RCSfile
find ../CreateScripts/versions ! -path '*/versions/2.*' -name '*.sql' ! -name 'update_database_version.sql' ! -name 'prm_db_patch_*.sql' ! -name 'recompile_objects.sql' | xargs grep -L '$RCSfile:'

echo ^> Checking for missing 'insert into database_version_update...' SQL
find ../CreateScripts/versions ! -path '*/versions/2.*' -name '*.sql' ! -name 'update_database_version.sql' ! -name 'prm_db_patch_*.sql' ! -name 'recompile_objects.sql' | xargs grep -iL 'insert into database_version_update'

echo ^> Checking for missing version number customization of 
echo   'insert into database_version_update...' SQL
find ../CreateScripts/versions ! -path '*/versions/2.*' -name '*.sql' ! -name 'update_database_version.sql' ! -name 'prm_db_patch_*.sql' ! -name 'recompile_objects.sql' | xargs grep -il 'x,x,x'

echo ^> Done.
