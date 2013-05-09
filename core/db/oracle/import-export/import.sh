#!/bin/sh
#: Title		: import
#: Date			: 08/05/2013
#: Author		: Ramiro Savoie, Nicolás Semeniuk
#: Version		: 0.6
#: Description	: Script to import the Project.net database and document vault
#: Options		:

# Can't erase a connected user
sudo service tomcat6 stop

read -p "Please, insert the folder where the backup files are (e.g. /opt/Backup_pnet_db_XXXX-XX-XX) [Press ENTER for current directory]: " FOLDER
if [ -z "$FOLDER" ]
then
	FOLDER=`pwd`
fi

read -p "Please, insert the path where the current installation Document Vault is (e.g. /opt/docVault) [Press ENTER for current directory]: " DOC_VAULT
if [ -z "$CLEAN_AND_CREATE_USERS_DIR" ]
then
	DOC_VAULT=`pwd`
fi

read -p "Please, insert the folder where the file clean_and_create_users.sql is [Press ENTER for current directory]: " CLEAN_AND_CREATE_USERS_DIR
if [ -z "$CLEAN_AND_CREATE_USERS_DIR" ]
then
	CLEAN_AND_CREATE_USERS_DIR=`pwd`
fi

read -p "Please, insert the folder where the file compile_views.sql is [Press ENTER for current directory]: " COMPILE_VIEWS_DIR
if [ -z "$COMPILE_VIEWS_DIR" ]
then
	COMPILE_VIEWS_DIR=`pwd`
fi

if [ -d "$CLEAN_AND_CREATE_USERS_DIR" ]
then
	cd "$CLEAN_AND_CREATE_USERS_DIR"
else
	printf "\033[31m \n%s \033[0m \n" "The directory $CLEAN_AND_CREATE_USERS_DIR is not found"
	exit
fi

# Clean the current database data
sqlplus system/system@XE @clean_and_create_users.sql

if [ -d "$FOLDER" ]
then
	cd "$FOLDER"
else
	printf "\033[31m \n%s \033[0m \n" "The directory $FOLDER is not found"
	exit
fi

if [ -f pnet_db_dump.dmp.zip ]
then
	# tar zxf pnet_db_dump.dmp.tar.gz
	unzip pnet_db_dump.dmp.zip
else
	printf "\033[31m \n%s \033[0m \n" "The file to uncompress is not found, file: pnet_db_dump.dmp.zip"
	exit
fi

if [ -f pnet_user_db_dump.dmp.zip ]
then
	# tar zxf pnet_user_db_dump.dmp.tar.gz
	unzip pnet_user_db_dump.dmp.zip
else
	printf "\033[31m \n%s \033[0m \n" "The file to uncompress is not found, file: pnet_user_db_dump.dmp.zip"
	exit
fi

# pnet: owner user of the scheme
imp userid=pnet/pnet@XE file=pnet_db_dump.dmp fromuser=pnet touser=pnet log=pnet_restore.log
# pnet_user: user that has access to the scheme data
imp userid=pnet_user/pnet_user@XE file=pnet_user_db_dump.dmp fromuser=pnet_user touser=pnet_user log=pnet_user_restore.log

if [ -d "$COMPILE_VIEWS_DIR" ]
then
	cd "$COMPILE_VIEWS_DIR"
else
	printf "\033[31m \n%s \033[0m \n" "The directory $COMPILE_VIEWS_DIR is not found"
	exit
fi

# In case there was errors generating the views
sqlplus pnet/pnet@XE @compile_views.sql

printf "\033[32m \n%s \033[0m \n" "Database restored"

if [ -d "$DOC_VAULT" ]
then
	cd "$DOC_VAULT"
else
	printf "\033[31m \n%s \033[0m \n" "The directory $DOC_VAULT is not found"
	exit
fi

if [ -f docVault.zip ]
then
	# tar zxf docVault.tar.gz
	unzip docVault.zip
else
	printf "\033[31m \n%s \033[0m \n" "The file to uncompress is not found, file: docVault.zip"
	exit
fi

sudo cp -R docVault /opt/pnet/

printf "\033[32m \n%s \033[0m \n" "Document Vault restored"

printf "\033[32m \n%s \033[0m \n" "Import Completed!"

sudo service tomcat6 start

