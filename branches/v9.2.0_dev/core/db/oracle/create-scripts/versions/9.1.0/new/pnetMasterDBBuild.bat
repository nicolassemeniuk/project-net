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
rem The script will create two complete Project.net Oracle schemas within the
rem existing Oracle DB instance given below.  The "pnet" schema is used to store the all the
rem tables except the form's data storage table.   The "pnet_user" schema will contain the 
rem dynamically-created forms data tables.   After running this database creation script, it is
rem normal for no tables to be created in the pnet_user schema.   The project.net application
rem will add tables to the pnet_user schema when the user designs forms using the application UI.
rem =============================================================================================

rem SET PATH TO PROJECT.NET DATABASE CREATION SCRIPTS
rem
rem The full path to the project.net create-scripts directory
rem For example:
rem   PNET_BUILD_DB_SCRIPTS_PATH=C:\pnet\deploy\core\db\oracle\create-scripts

set PNET_BUILD_DB_SCRIPTS_PATH=D:\pnet\trunk2\core\db\oracle\create-scripts\versions\9.1.0\new
			
rem SET DATABASE NAME
rem
rem Database instance name in which to create the Project.net schemas.
rem This database name must be accessible by SQL*Plus from the machine where you run this script.
rem For example:
rem   PNET_BUILD_DB_DATABASE_NAME=pnetdb
rem   PNET_BUILD_DB_DATABASE_NAME=XE

set PNET_BUILD_DB_DATABASE_NAME=genesys


rem SET LOG FILE
rem
rem Path to log file for logging of installation (directories must exist first)
rem For example:
rem  PNET_BUILD_DB_LOG_PATH=c:\temp\pnet_%PNET_BUILD_DB_DATABASE_NAME%_db_build.log

set PNET_BUILD_DB_LOG_PATH=d:\temp\pnet_%PNET_BUILD_DB_DATABASE_NAME%_db_build.log


rem SET PATH TO DATABASE DATAFILES
rem 
rem Note: this is not required if you have already manually created tablespaces in your database.
rem This is the file system location of the database tablespace datafiles on the database server.  
rem The path for new datafiles is platform dependent.
rem You must use a path convention that is appropriate for the platform (operating system) where 
rem the database is *installed*.
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


rem SET DATABASE SYSTEM USER/PASSWD
rem
rem Database "system" account and password
rem Some scripts must connect as "system" (to create schemas, roles and tablespaces)
rem For example:
rem   PNET_BUILD_SYSTEM_ACCOUNT=system
rem   PNET_BUILD_SYSTEM_PASSWORD=manager

set PNET_BUILD_SYSTEM_ACCOUNT=system
set PNET_BUILD_SYSTEM_PASSWORD=manager


rem SET PROJECT.NET DATABASE SCHEMA OWNER
rem
rem Schema owner name and password
rem All application objects are owned by this user.
rem The user will be created.

set PNET_BUILD_USER_NAME=pnet
set PNET_BUILD_USER_PASSWORD=pnet


rem SET PROJECT.NET DATABASE CONNECTION USER
rem
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
