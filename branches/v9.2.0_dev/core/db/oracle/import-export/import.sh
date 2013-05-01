#!/bin/sh
#: Title		: import
#: Date			: 01/05/2013
#: Author		: Ramiro Savoie, Nicolás Semeniuk
#: Version		: 0.6
#: Description	: Script to import the Project.net database and document vault
#: Options		:

# Can't erase a connected user
sudo service tomcat6 stop

# Clean the current database data
sqlplus system/system@XE @clean_and_create_users.sql

# TODO parametrizar la ruta
if [ -f pnet_db_dump.dmp.tar.gz ]
then
	tar zxf pnet_db_dump.dmp.tar.gz
else
	printf "\033[31m \n%s \033[0m \n" "The file to uncompress is not found, file: pnet_db_dump.dmp.tar.gz"
	exit
fi

if [ -f pnet_user_db_dump.dmp.tar.gz ]
then
	tar zxf pnet_user_db_dump.dmp.tar.gz
else
	printf "\033[31m \n%s \033[0m \n" "The file to uncompress is not found, file: pnet_user_db_dump.dmp.tar.gz"
	exit
fi

# pnet: owner user of the scheme
imp userid=pnet/pnet@XE file=pnet_db_dump.dmp fromuser=pnet touser=pnet log=pnet_restore.log
# pnet_user: user that has access to the scheme data
imp userid=pnet_user/pnet_user@XE file=pnet_user_db_dump.dmp fromuser=pnet_user touser=pnet_user log=pnet_user_restore.log

# In case there was errors generating the views
sqlplus pnet/pnet@XE @compile_views.sql

printf "\033[31m \n%s \033[0m \n" "Database restored"

# TODO parametrizar la ruta
if [ -f docVault.tar.gz ]
then
	tar zxf docVault.tar.gz
else
	printf "\033[31m \n%s \033[0m \n" "The file to uncompress is not found, file: docVault.tar.gz"
	exit
fi

sudo cp -R docVault /opt/pnet/

printf "\033[31m \n%s \033[0m \n" "Document Vault restored"

printf "\033[31m \n%s \033[0m \n" "Import Completed!"

sudo service tomcat6 start

printf "\033[31m \n%s \033[0m \n" "Waiting 1 minute to start Tomcat"
sleep 1m

# Para que vaya inicializando las caches de Hibernate
wget --delete-after 'http://localhost:9090/pnet/LoginProcessing.jsp' --user-agent=Mozilla/5.0 --post-data "language=en&theAction=submit&J_USERNAME=appadmin&J_PASSWORD=appadmin&userDomain=1000"

# Limpieza
rm -rf docVault
rm -f *dmp
