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

rem =============================================================================================
rem Project.net, Inc.
rem Master database build script for Oracle
rem
rem The parameters below must be customized before executing this script.
rem The script will create an complete Project.net Oracle schema within the
rem Existing Oracle DB instanace given below.
rem =============================================================================================

rem PNET_BUILD_DB_SCRIPTS_PATH
rem This is the full path to the CreateScripts directory
rem For example:
rem   PNET_BUILD_DB_SCRIPTS_PATH=c:\pnet\deploy\core\db\oracle\create-scripts

set PNET_BUILD_DB_SCRIPTS_PATH=E:\projects\trunk\core\db\oracle\create-scripts\versions\9.0.0\new


rem PNET_BUILD_DB_DATABASE_NAME
rem Database instance name in which to install
rem This must be accessible by SQL*Plus from the machine from which these
rem scripts are run
rem For example:
rem   PNET_BUILD_DB_DATABASE_NAME=pnetdb

set PNET_BUILD_DB_DATABASE_NAME=form


rem PNET_BUILD_DB_LOG_PATH
rem Path to log file for logging of installation (directories must exist first)
rem For example:
rem  PNET_BUILD_DB_LOG_PATH=c:\temp\pnet_%PNET_BUILD_DB_DATABASE_NAME%_db_build.log

set PNET_BUILD_DB_LOG_PATH=c:\pnet_%PNET_BUILD_DB_DATABASE_NAME%_db_build.log


rem PNET_BUILD_DB_DATAFILE_PATH
rem Server-side location of new datafiles (required only if tablespaces are not
rem pre-created).  The path for new datafiles is platform dependant - use a
rem path convention that is appropriate for the platform on which the database
rem is installed.
rem For example:
rem   Windows database server:
rem   PNET_BUILD_DB_DATAFILE_PATH=
rem           C:\oracle\oradata\%PNET_BUILD_DB_DATABASE_NAME%\
rem
rem   Unix/Linux database server:
rem   PNET_BUILD_DB_DATAFILE_PATH=
rem           /usr/oracle/oradata/${PNET_BUILD_DB_DATABASE_NAME}/    
rem
rem Note: The trailing slash on the path is required

set PNET_BUILD_DB_DATAFILE_PATH=D:\oracle\product\10.2.0\oradata\%PNET_BUILD_DB_DATABASE_NAME%\


rem PNET_BUILD_SYSTEM_ACCOUNT
rem PNET_BUILD_SYSTEM_PASSWORD
rem Database "system" account and password
rem Some scripts must connect as "system" (to create schemas, roles and 
rem tablespaces)
rem For example:
rem   PNET_BUILD_SYSTEM_ACCOUNT=system
rem   PNET_BUILD_SYSTEM_PASSWORD=manager

set PNET_BUILD_SYSTEM_ACCOUNT=system
set PNET_BUILD_SYSTEM_PASSWORD=manager


rem PNET_BUILD_USER_NAME
rem PNET_BUILD_USER_PASSWORD
rem Schema owner name and password
rem All application objects are owned by this user.
rem The user will be created.

set PNET_BUILD_USER_NAME=pnet
set PNET_BUILD_USER_PASSWORD=pnet


rem PNET_BUILD_APP_USER_NAME
rem PNET_BUILD_APP_USER_PASSWORD
rem Application name and password 
rem The application connects as this user
rem (also specified in application configuration file)

set PNET_BUILD_APP_USER_NAME=pnet_user
set PNET_BUILD_APP_USER_PASSWORD=pnet_user


rem
rem End of customization
rem

rem Initialization
if not exist %PNET_BUILD_DB_SCRIPTS_PATH%\pnetDB.bat goto error1

echo.
echo    Starting Project.net Master Database Build Script.
echo    -- this script will build a new database schema --
echo.
echo    ** THE DATABASE SCHEMA WILL BE BUILT USING THE "%PNET_BUILD_DB_DATABASE_NAME%" DB INSTANCE**
echo.

if "%1"=="noconfirm" goto build

echo Press any key to continue or CTRL+C to abort
pause > nul

:build
%PNET_BUILD_DB_SCRIPTS_PATH%\pnetDB.bat
goto done

:error1
echo Unable to locate file %PNET_BUILD_DB_SCRIPTS_PATH%\pnetDB.bat
goto done

:done
echo Done.
