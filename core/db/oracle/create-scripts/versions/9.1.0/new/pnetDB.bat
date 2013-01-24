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

cd %PNET_BUILD_DB_SCRIPTS_PATH%

echo =====================================
echo Building Tablespaces and Users
echo =====================================

sqlplus %PNET_BUILD_SYSTEM_ACCOUNT%/%PNET_BUILD_SYSTEM_PASSWORD%@%PNET_BUILD_DB_DATABASE_NAME% @admin/cr_tablespaces.sql %PNET_BUILD_DB_DATAFILE_PATH% > %PNET_BUILD_DB_LOG_PATH%

sqlplus %PNET_BUILD_SYSTEM_ACCOUNT%/%PNET_BUILD_SYSTEM_PASSWORD%@%PNET_BUILD_DB_DATABASE_NAME% @admin/cr_users.sql  %PNET_BUILD_USER_NAME% %PNET_BUILD_USER_PASSWORD% %PNET_BUILD_APP_USER_NAME% %PNET_BUILD_APP_USER_PASSWORD% >> %PNET_BUILD_DB_LOG_PATH%


echo =====================================
echo Creating all database objects
echo =====================================

sqlplus %PNET_BUILD_USER_NAME%/%PNET_BUILD_USER_PASSWORD%@%PNET_BUILD_DB_DATABASE_NAME% @main.sql %PNET_BUILD_APP_USER_NAME% %PNET_BUILD_APP_USER_PASSWORD% %PNET_BUILD_DB_DATABASE_NAME% >> %PNET_BUILD_DB_LOG_PATH%

cd ../../..

echo =====================================
echo Applying 9.1.0 upgrade patch
echo =====================================

sqlplus %PNET_BUILD_USER_NAME%/%PNET_BUILD_USER_PASSWORD%@%PNET_BUILD_DB_DATABASE_NAME% @versions/9.1.0/prm_db_patch_9.1.0.sql %PNET_BUILD_APP_USER_NAME% %PNET_BUILD_APP_USER_PASSWORD% %PNET_BUILD_DB_DATABASE_NAME% >> %PNET_BUILD_DB_LOG_PATH%

echo =====================================
echo Done.
echo =====================================

