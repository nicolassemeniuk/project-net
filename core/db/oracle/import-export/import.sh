#!/bin/sh
#: Title		: Script para importar la bases de datos
#: Date			: 13/11/2012
#: Author		: Ramiro Savoie
#: Version		: 0.1
#: Description		:
#: Options		:

# Limpia los datos que ya se encuentren en la base de datos
sqlplus system/system@XE @clean_and_create_users.sql

tar zxf pnet_db_dump.dmp.tar.gz
tar zxf pnet_user_db_dump.dmp.tar.gz
imp userid=pnet/pnet@XE file=pnet_db_dump.dmp fromuser=pnet touser=pnet log=pnet_restore.log
imp userid=pnet_user/pnet_user@XE file=pnet_user_db_dump.dmp fromuser=pnet_user touser=pnet_user log=pnet_user_restore.log

# Por si hubo errores al generar algunas vistas
sqlplus pnet/pnet@XE @compile_views.sql

echo 'Backup de la base de datos listo'

tar zxf docVault.tar.gz
sudo cp -R docVault /opt/pnet/

echo 'Backup del document vault listo'

sudo service tomcat6 restart
