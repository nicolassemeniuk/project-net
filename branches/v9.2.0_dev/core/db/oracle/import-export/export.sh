#!/bin/sh
#: Title		: Script para exportar la bases de datos y el document vault
#: Date			: 13/11/2012
#: Author		: Ramiro Savoie
#: Version		: 0.1
#: Description		:
#: Options		:

# sudo service tomcat6 stop
# Crea una carpeta Backup con la fecha de hoy
fecha=`date +%d-%m-%y`
mkdir "Backup ($fecha)" && cd "Backup ($fecha)" || exit 1

exp userid=pnet/pnet@XE file=pnet_db_dump.dmp owner=pnet log=pnet_dump.log
exp userid=pnet_user/pnet_user@XE file=pnet_user_db_dump.dmp owner=pnet_user log=pnet_user_dump.log

tar czf pnet_db_dump.dmp.tar.gz pnet_db_dump.dmp
rm pnet_db_dump.dmp

tar czf pnet_user_db_dump.dmp.tar.gz pnet_user_db_dump.dmp
rm pnet_user_db_dump.dmp

echo 'Backup de la base de datos listo'

tar czf docVault.tar.gz -C /opt/pnet/ docVault

echo 'Backup del document vault listo'
