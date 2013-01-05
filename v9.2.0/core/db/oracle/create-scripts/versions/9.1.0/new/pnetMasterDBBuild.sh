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
# The script will create two complete Project.net Oracle schemas within the
# existing Oracle DB instance given below.  The "pnet" schema is used to store the all the
# tables except the form's data storage table.   The "pnet_user" schema will contain the 
# dynamically-created forms data tables.   After running this database creation script, it is
# normal for no tables to be created in the pnet_user schema.   The project.net application
# will add tables to the pnet_user schema when the user designs forms using the application UI.
# =============================================================================================
###############################################################################################
# SET PATH TO PROJECT.NET DATABASE CREATION SCRIPTS
#
# This is the full path to the CreateScripts directory
# For example:
#   PNET_BUILD_DB_SCRIPTS_PATH=/opt/pnet/deploy/prm/database/CreateScripts   (production installer)
#   PNET_BUILD_DB_SCRIPTS_PATH=.                                             (development)
#   PNET_BUILD_DB_SCRIPTS_PATH=/home/trunk/core/db/oracle/create-scripts/versions/9.1.0/new
############################################################################################### 
export PNET_BUILD_DB_SCRIPTS_PATH=/pnet/db/oracle/create-scripts/versions/9.1.0/new/
echo $PNET_BUILD_DB_SCRIPTS_PATH
###############################################################################################
# SET DATABASE INSTANCE NAME
#
# Database instance name in which to install
# This must be accessible by SQL*Plus from the machine from which these
# scripts are run
# For example:
#   PNET_BUILD_DB_DATABASE_NAME=pnetdb
###############################################################################################
export PNET_BUILD_DB_DATABASE_NAME=test
echo $PNET_BUILD_DB_DATABASE_NAME
###############################################################################################
# PNET_BUILD_DB_LOG_PATH
# Path to log file for logging of installation (directories must exist first)
# For example:
#  PNET_BUILD_DB_LOG_PATH=/tmp/pnet_${PNET_BUILD_DB_DATABASE_NAME}_db_build.log
###############################################################################################
export PNET_BUILD_DB_LOG_PATH=/tmp/pnet_test_db_build.log
echo $PNET_BUILD_DB_LOG_PATH
###############################################################################################
# SET PATH TO DATABASE DATAFILES
#
# Note: this is not required if you have already manually created tablespaces in your database.
# This is the file system location of the database tablespace datafiles on the database server.  
# The path for new datafiles is platform dependent.
# You must use a path convention that is appropriate for the platform (operating system) where 
# the database is *installed*.
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
export PNET_BUILD_DB_DATAFILE_PATH=/Users/oracle/oradata/test/
echo $PNET_BUILD_DB_DATAFILE_PATH
###############################################################################################
# SET DATABASE SYSTEM USER/PASSWD
#
# Database "system" account and password
# Some scripts must connect as "system" (to create schemas, roles and tablespaces)
# For example:
#   PNET_BUILD_SYSTEM_ACCOUNT=system
#   PNET_BUILD_SYSTEM_PASSWORD=manager
################################################################################################
export PNET_BUILD_SYSTEM_ACCOUNT=system
export PNET_BUILD_SYSTEM_PASSWORD=manager
echo $PNET_BUILD_SYSTEM_ACCOUNT
echo $PNET_BUILD_SYSTEM_PASSWORD
###############################################################################################
# SET PROJECT.NET DATABASE SCHEMA OWNER
#
# Schema owner name and password
# All application objects are owned by this user.
# The database user will be created.
################################################################################################
export PNET_BUILD_USER_NAME=pnet
export PNET_BUILD_USER_PASSWORD=pnet
echo $PNET_BUILD_USER_NAME
echo $PNET_BUILD_USER_PASSWORD
###############################################################################################
# SET PROJECT.NET DATABASE CONNECTION USER
#
# Application's database connection username and password.
# The application connects as this database user.
# The database user will be created.
###############################################################################################
export PNET_BUILD_APP_USER_NAME=pnet_user
export PNET_BUILD_APP_USER_PASSWORD=pnet_user
echo $PNET_BUILD_APP_USER_NAME
echo $PNET_BUILD_APP_USER_PASSWORD
#
# cd $PNET_BUILD_DB_SCRIPTS_PATH
echo =====================================
echo Building Tablespaces and Users
echo =====================================
sqlplus $PNET_BUILD_SYSTEM_ACCOUNT/$PNET_BUILD_SYSTEM_PASSWORD@$PNET_BUILD_DB_DATABASE_NAME @admin/cr_tablespaces.sql $PNET_BUILD_DB_DATAFILE_PATH > $PNET_BUILD_DB_LOG_PATH
sqlplus $PNET_BUILD_SYSTEM_ACCOUNT/$PNET_BUILD_SYSTEM_PASSWORD@$PNET_BUILD_DB_DATABASE_NAME @admin/cr_users.sql  $PNET_BUILD_USER_NAME $PNET_BUILD_USER_PASSWORD $PNET_BUILD_APP_USER_NAME $PNET_BUILD_APP_USER_PASSWORD >> $PNET_BUILD_DB_LOG_PATH
echo =====================================
echo Creating all database objects
echo =====================================
sqlplus $PNET_BUILD_USER_NAME/$PNET_BUILD_USER_PASSWORD@$PNET_BUILD_DB_DATABASE_NAME @main.sql $PNET_BUILD_APP_USER_NAME $PNET_BUILD_APP_USER_PASSWORD $PNET_BUILD_DB_DATABASE_NAME >> $PNET_BUILD_DB_LOG_PATH
cd ../../..
echo =====================================
echo Applying 9.1.0 upgrade patch
echo =====================================
sqlplus $PNET_BUILD_USER_NAME/$PNET_BUILD_USER_PASSWORD@$PNET_BUILD_DB_DATABASE_NAME @versions/9.1.0/prm_db_patch_9.1.0.sql $PNET_BUILD_APP_USER_NAME $PNET_BUILD_APP_USER_PASSWORD $PNET_BUILD_DB_DATABASE_NAME >> $PNET_BUILD_DB_LOG_PATH
echo =====================================
echo Done.
echo =====================================