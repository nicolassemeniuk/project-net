#!/bin/sh
#: Title		: export
#: Date			: 01/05/2013
#: Author		: Ramiro Savoie, Nicolás Semeniuk
#: Version		: 0.2
#: Description	: Script to export the Project.net database and document vault
#: Options		:

# We don't have to stop the application, but it should be done if there are too many querys on the database.
# sudo service tomcat6 stop

# Create a Backup folder with today's date
today=`date +%d-%m-%y`
mkdir "Backup ($today)" && cd "Backup ($today)" || exit 1

# pnet: owner user of the scheme
exp userid=pnet/pnet@XE file=pnet_db_dump.dmp owner=pnet log=pnet_dump.log
if [ -f pnet_db_dump.dmp ]
then
	# Compress dump and erase original data
	# tar czf pnet_db_dump.dmp.tar.gz pnet_db_dump.dmp
	zip -r pnet_db_dump.dmp.zip pnet_db_dump.dmp
	rm pnet_db_dump.dmp
	printf "\033[32m \n%s \033[0m \n" "Successfully exported user pnet"
else
	printf "\033[31m \n%s \033[0m \n" "An error occurred when exporting the user pnet, check the logs"
	exit
fi

# pnet_user: user that has access to the scheme data
exp userid=pnet_user/pnet_user@XE file=pnet_user_db_dump.dmp owner=pnet_user log=pnet_user_dump.log
if [ -f pnet_user_db_dump.dmp ]
then
	# Compress dump and erase original data
	# tar czf pnet_user_db_dump.dmp.tar.gz pnet_user_db_dump.dmp
	zip -r pnet_user_db_dump.dmp.zip pnet_user_db_dump.dmp
	rm pnet_user_db_dump.dmp
	printf "\033[32m \n%s \033[0m \n" "Successfully exported user pnet_user"
else
	printf "\033[31m \n%s \033[0m \n" "An error occurred when exporting the user pnet_user, check the logs"
	exit
fi

printf "\033[32m \n%s \033[0m \n" "Successfully exported all users"

read -p "Please, insert the path where the Document Vault is (e.g. /opt/pnet) [DEFAULT /opt/pnet]: " DOCVAULT
if [ -z "$DOCVAULT" ]
then
	DOCVAULT='/opt/pnet'
fi

# tar czf docVault.tar.gz -C $DOCVAULT docVault
CURRENT=`pwd`
cd "$DOCVAULT"
zip -r "$CURRENT/docVault.zip" docVault
cd "$CURRENT"

if [ -f docVault.zip ]
then
	printf "\033[32m \n%s \033[0m \n" "Successfully exported Document Vault"
else
	printf "\033[31m \n%s \033[0m \n" "An error occurred when exporting the Document Vault"
fi

printf "\033[32m \n%s \033[0m \n" "Export Completed!"
