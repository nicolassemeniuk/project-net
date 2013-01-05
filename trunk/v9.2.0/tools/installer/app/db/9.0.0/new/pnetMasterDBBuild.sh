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
# =============================================================================================
# Project.net, Inc.
# Master database build script for Oracle
#
# The parameters below must be customized before executing this script.
# The script will create an complete Project.net Oracle schema within the
# Existing Oracle DB instanace given below.
# =============================================================================================


###############################################################################################
# PNET_BUILD_DB_SCRIPTS_PATH
# This is the full path to the CreateScripts directory
# For example:
#   PNET_BUILD_DB_SCRIPTS_PATH=/pnet/deploy/prm/database/CreateScripts  (production installer)
#   PNET_BUILD_DB_SCRIPTS_PATH=../create-scripts  						(development)
############################################################################################### 
export PNET_BUILD_DB_SCRIPTS_PATH=../create-scripts


###############################################################################################
# PNET_BUILD_DB_DATABASE_NAME
# Database instance name in which to install
# This must be accessible by SQL*Plus from the machine from which these
# scripts are run
# For example:
#   PNET_BUILD_DB_DATABASE_NAME=pnetdb
###############################################################################################
export PNET_BUILD_DB_DATABASE_NAME=pnetdb


###############################################################################################
# PNET_BUILD_DB_LOG_PATH
# Path to log file for logging of installation (directories must exist first)
# For example:
#  PNET_BUILD_DB_LOG_PATH=/tmp/pnet_${PNET_BUILD_DB_DATABASE_NAME}_db_build.log
###############################################################################################
export PNET_BUILD_DB_LOG_PATH=/tmp/pnet_${PNET_BUILD_DB_DATABASE_NAME}_db_build.log


###############################################################################################
# PNET_BUILD_DB_DATAFILE_PATH
# Server-side location of new datafiles (required only if tablespaces are not
# pre-created).  The path for new datafiles is platform dependant - use a
# path convention that is appropriate for the platform on which the database
# is installed.
# For example:
#   Windows database server:
#   PNET_BUILD_DB_DATAFILE_PATH=
#           C:\\oracle\\oradata\\${PNET_BUILD_DB_DATABASE_NAME}\\  
#
#   Unix/Linux database server:
#   PNET_BUILD_DB_DATAFILE_PATH=
#           /usr/oracle/oradata/${PNET_BUILD_DB_DATABASE_NAME}/    
#
# Note: The trailing slash on the path is required
###############################################################################################
export PNET_BUILD_DB_DATAFILE_PATH=


###############################################################################################
# PNET_BUILD_SYSTEM_ACCOUNT
# PNET_BUILD_SYSTEM_PASSWORD
# Database "system" account and password
# Some scripts must connect as "system" (to create schemas, roles and tablespaces)
# For example:
#   PNET_BUILD_SYSTEM_ACCOUNT=system
#   PNET_BUILD_SYSTEM_PASSWORD=manager
################################################################################################
export PNET_BUILD_SYSTEM_ACCOUNT=system
export PNET_BUILD_SYSTEM_PASSWORD=manager


###############################################################################################
# PNET_BUILD_USER_NAME
# PNET_BUILD_USER_PASSWORD
# Schema owner name and password
# All application objects are owned by this user.
# The database user will be created.
################################################################################################
export PNET_BUILD_USER_NAME=pnet
export PNET_BUILD_USER_PASSWORD=pnet


###############################################################################################
# PNET_BUILD_APP_USER_NAME
# PNET_BUILD_APP_USER_PASSWORD
# Application's database connection username and password.
# The application connects as this database user.
# The database user will be created.
###############################################################################################
export PNET_BUILD_APP_USER_NAME=pnet_user
export PNET_BUILD_APP_USER_PASSWORD=pnet_user

#
# End of customization
#


# Initialization
if [ -f $PNET_BUILD_DB_SCRIPTS_PATH/admin/pnetDB.sh ]; then

   echo
   echo    Starting Project.net Master Database Build Script.
   echo    -- this script will build a new database schema --
   echo
   echo    === THE DATABASE SCHEMA WILL BE BUILT WITHIN THE \'${PNET_BUILD_DB_DATABASE_NAME}\' DB INSTANCE ===
   echo

   echo Type "yes" to continue or anything else to abort
   read answer

   if [ "$answer" = "yes" ]; then
   
       . $PNET_BUILD_DB_SCRIPTS_PATH/admin/pnetDB.sh

   else
   
      echo Aborted at user request
      
   fi

else

    echo Unable to locate file $PNET_BUILD_DB_SCRIPTS_PATH/admin/pnetDB.sh

fi

echo Done

