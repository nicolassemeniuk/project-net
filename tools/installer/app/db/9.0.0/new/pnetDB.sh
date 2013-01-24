# 
# Copyright 2000-2009 Project.net Inc.
# 
# This file is part of Project.net.
# Project.net is free software: you can redistribute it and/or modify it under the terms of 
# the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
# 
# Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
# See the GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License along with Project.net.
# If not, see http://www.gnu.org/licenses/gpl-3.0.html

#!/bin/sh
# ----------------------------------------------------------------------------
#	
#   Executes core database build scripts.
#   Called by pnetMasterDBBuild.sh
# ----------------------------------------------------------------------------

cd %PNET_BUILD_DB_SCRIPTS_PATH%

echo.
echo =====================================
echo Building Tablespaces and Users
echo =====================================
echo.

sqlplus %PNET_BUILD_SYSTEM_ACCOUNT%/%PNET_BUILD_SYSTEM_PASSWORD%@%PNET_BUILD_DB_DATABASE_NAME% @admin/cr_tablespaces.sql %PNET_BUILD_DB_DATAFILE_PATH% > %PNET_BUILD_DB_LOG_PATH%

sqlplus %PNET_BUILD_SYSTEM_ACCOUNT%/%PNET_BUILD_SYSTEM_PASSWORD%@%PNET_BUILD_DB_DATABASE_NAME% @admin/cr_users.sql  %PNET_BUILD_USER_NAME% %PNET_BUILD_USER_PASSWORD% %PNET_BUILD_APP_USER_NAME% %PNET_BUILD_APP_USER_PASSWORD% >> %PNET_BUILD_DB_LOG_PATH%

echo.
echo =====================================
echo Creating all database objects
echo =====================================
echo.

sqlplus %PNET_BUILD_USER_NAME%/%PNET_BUILD_USER_PASSWORD%@%PNET_BUILD_DB_DATABASE_NAME% @main.sql %PNET_BUILD_APP_USER_NAME% %PNET_BUILD_APP_USER_PASSWORD% %PNET_BUILD_DB_DATABASE_NAME% >> %PNET_BUILD_DB_LOG_PATH%

cd ../../..

echo.
echo =====================================
echo Applying 9.0.0 patch
echo =====================================
echo.

sqlplus %PNET_BUILD_USER_NAME%/%PNET_BUILD_USER_PASSWORD%@%PNET_BUILD_DB_DATABASE_NAME% @versions/9.0.0/prm_db_patch_9.0.0.sql >> %PNET_BUILD_DB_LOG_PATH%

echo.
echo =====================================
echo Done.
echo =====================================
echo.
