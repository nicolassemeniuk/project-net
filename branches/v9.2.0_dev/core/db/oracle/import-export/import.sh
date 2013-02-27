#!/bin/sh
#: Title		: Script para importar la bases de datos
#: Date			: 13/11/2012
#: Author		: Ramiro Savoie
#: Version		: 0.5
#: Description		:
#: Options		:

# No puede borrarse un usuario si esta conectado
sudo service tomcat6 stop

# Limpia los datos que ya se encuentren en la base de datos
sqlplus system/system@XE @clean_and_create_users.sql

# TODO Hacer un chequeo de existencia del archivo y parametrizar la ruta
tar zxf pnet_db_dump.dmp.tar.gz
tar zxf pnet_user_db_dump.dmp.tar.gz
# pnet: usuario propietario del esquema
imp userid=pnet/pnet@XE file=pnet_db_dump.dmp fromuser=pnet touser=pnet log=pnet_restore.log
# pnet_user: usuario que accede a los datos del esquema
imp userid=pnet_user/pnet_user@XE file=pnet_user_db_dump.dmp fromuser=pnet_user touser=pnet_user log=pnet_user_restore.log

# Por si hubo errores al generar algunas vistas
sqlplus pnet/pnet@XE @compile_views.sql

printf "\033[31m \n%s \033[0m \n" "Backup de la base de datos restaurado"

# TODO Hacer un chequeo de existencia del archivo y parametrizar la ruta
tar zxf docVault.tar.gz
sudo cp -R docVault /opt/pnet/

printf "\033[31m \n%s \033[0m \n" "Backup del document vault restaurado"

sudo service tomcat6 start

printf "\033[31m \n%s \033[0m \n" "Durmiendo 1 minuto mientras arranca el Tomcat"
sleep 1m

# Para que vaya inicializando las caches de Hibernate
wget --delete-after 'http://localhost:9090/pnet/LoginProcessing.jsp' --user-agent=Mozilla/5.0 --post-data "language=en&theAction=submit&J_USERNAME=appadmin&J_PASSWORD=appadmin&userDomain=1000"

# Limpieza
rm -rf docVault
rm -f *dmp
