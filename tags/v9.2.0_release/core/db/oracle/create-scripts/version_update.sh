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
#     $Id: version_update.sh 19763 2009-08-17 08:18:07Z puno $
#
#     Executes a database version update script
#
#     Execute with no parameters to see usage information
# ----------------------------------------------------------------------------

print_usage() {
   echo Usage:
   echo     version_update user/password@database update_version_number pnet_user_username pnet_user_password database_name
}

# Capture command line parameters
_SQLPLUS_LOGIN=$1
_UPDATE_VERSION=$2
_PNET_USER_USERNAME=$3
_PNET_USER_PASSWORD=$4
_DATABASE_NAME=$5

# Check all required parameters are present
if [ -z $_SQLPLUS_LOGIN ]; then
   
   print_usage

elif [ -z $_UPDATE_VERSION ]; then

   print_usage
   
else

   # Check that update script exists
   _UPDATE_SCRIPT=versions/${_UPDATE_VERSION}/prm_db_patch_${_UPDATE_VERSION}.sql

   if [ -f $_UPDATE_SCRIPT ]; then

      # Execute command
      _COMMAND="sqlplus $_SQLPLUS_LOGIN @$_UPDATE_SCRIPT $_PNET_USER_USERNAME $_PNET_USER_PASSWORD $_DATABASE_NAME "
      echo Executing $_COMMAND
      $_COMMAND

   else

      echo Update script not found: $_UPDATE_SCRIPT

   fi

fi

_SQLPLUS_LOGIN=
_UPDATE_VERSION=
_UPDATE_SCRIPT=
_SQLPLUS_LOGIN=
_PNET_USER_USERNAME=
_PNET_USER_PASSWORD=
_DATABASE_NAME=

