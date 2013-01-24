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

rem core\build.bat
rem
rem Build the Project.net application using ant.
rem Adds Ant taskdefs to the classpath before executing the standard Ant batch script

setlocal

rem Find the ant tool.  Search order: 
rem 1. standard ..\tools\ant directory (preferred since known to work with build.
rem 2. user's ANT_HOME environment setting.
rem 3. Let user's PATH setting find ant, but warn.
if exist ..\tools\ant\bin (
   set _ANT_CMD=..\tools\ant\bin\ant.bat
   echo build.bat:  Overriding your ANT_HOME setting.  ant found in standard project.net tools directory.
   goto done_setting_ant_path
) 
if not "%ANT_HOME%" == "" (
   set _ANT_CMD="%ANT_HOME%\bin\ant.bat"
   echo build.bat:  Using your ANT_HOME setting.
) else (
   set _ANT_CMD=ant
   echo build.bat:  ant not found in ..\tools\ant\bin and ANT_HOME is not set.
   echo build.bat:  Your PATH environment variable will be used to find ant. 
)
:done_setting_ant_path


rem Clear the CLASSPATH
rem The build classpath is specified in build.xml; Ant will add its own classes.
rem Add external dependencies (usually optional Ant tasks) to the classpath
set CLASSPATH=..\test\unit-test\lib\junit.jar

rem Additional parameters to Ant class
rem     -emacs causes ant to stop printing command name (better for reading errors etc.)
rem     -verbose prints out trace information
set _BUILD_PROPERTIES=-emacs

rem Show the ant path and version being used.
echo build.bat:  Using %_ANT_CMD%
call %_ANT_CMD% -version 

rem Execute the build
%_ANT_CMD% %_BUILD_PROPERTIES% %* -f build.xml

rem Restore changed/set environment vars to their values at setlocal above.
endlocal
