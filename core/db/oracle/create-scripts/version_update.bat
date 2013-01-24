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

rem ----------------------------------------------------------------------------
rem
rem     $Id: version_update.bat 19763 2009-08-17 08:18:07Z puno $
rem
rem     Executes a database version update script
rem
rem     Execute with no parameters to see usage information
rem ----------------------------------------------------------------------------

rem Capture command line parameters
set _SQLPLUS_LOGIN=%1
set _UPDATE_VERSION=%2
set _PNET_USER_USERNAME=%3
set _PNET_USER_PASSWORD=%4
set _DATABASE_NAME=%5

rem Check all required parameters are present
if "%_SQLPLUS_LOGIN%"=="" goto usage
if "%_UPDATE_VERSION%"=="" goto usage
if "%_PNET_USER_USERNAME%"=="" goto usage
if "%_PNET_USER_PASSWORD%"=="" goto usage
if "%_DATABASE_NAME%"=="" goto usage

rem Check that update script exists
set _UPDATE_SCRIPT=versions\%_UPDATE_VERSION%\prm_db_patch_%_UPDATE_VERSION%.sql
if not exist %_UPDATE_SCRIPT% goto scriptnotfound

rem Execute command
set _COMMAND=sqlplus %_SQLPLUS_LOGIN% @%_UPDATE_SCRIPT% %_PNET_USER_USERNAME% %_PNET_USER_PASSWORD% %_DATABASE_NAME%
echo Executing %_COMMAND%
%_COMMAND%
goto done


:usage
echo Usage:
echo     version_update user/password@database update_version_number pnet_user_username pnet_user_password database_name
echo
goto done


:scriptnotfound
echo Update script not found: %_UPDATE_SCRIPT%
goto done


:done
set _SQLPLUS_LOGIN=
set _UPDATE_VERSION=
set _UPDATE_SCRIPT=
set _PNET_USER_USERNAME=
set _PNET_USER_PASSWORD=
set _DATABASE_NAME=
set _COMMAND=

