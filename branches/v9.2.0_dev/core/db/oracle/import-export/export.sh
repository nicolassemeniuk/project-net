#!/bin/sh
#: Title		: Script para exportar la bases de datos y el document vault
#: Date			: 13/11/2012
#: Author		: Ramiro Savoie
#: Version		: 0.1
#: Description		:
#: Options		:

DOCVAULT='/opt/pnet'

# No es necesario detener la aplicacion, pero deberia hacerse si es existen muchas solicitudes a la base de datos
# sudo service tomcat6 stop

# Crea una carpeta Backup con la fecha de hoy
fecha=`date +%d-%m-%y`
mkdir "Backup ($fecha)" && cd "Backup ($fecha)" || exit 1

# pnet: usuario propietario del esquema
exp userid=pnet/pnet@XE file=pnet_db_dump.dmp owner=pnet log=pnet_dump.log
if [ -f pnet_db_dump.dmp ]
then
	# Comprimimos los dump y borramos los datos originales
	tar czf pnet_db_dump.dmp.tar.gz pnet_db_dump.dmp
	rm pnet_db_dump.dmp
	printf "\033[32m \n%s \033[0m \n" "Usuario pnet exportado exitosamente"
else
	printf "\033[31m \n%s \033[0m \n" "Se ha producido un error al exportar el usuario pnet, revisar los logs"
	exit
fi

# pnet_user: usuario que accede a los datos del esquema
exp userid=pnet_user/pnet_user@XE file=pnet_user_db_dump.dmp owner=pnet_user log=pnet_user_dump.log
if [ -f pnet_user_db_dump.dmp ]
then
	# Comprimimos los dump y borramos los datos originales
	tar czf pnet_user_db_dump.dmp.tar.gz pnet_user_db_dump.dmp
	rm pnet_user_db_dump.dmp
	printf "\033[32m \n%s \033[0m \n" "Usuario pnet_user exportado exitosamente"
else
	printf "\033[31m \n%s \033[0m \n" "Se ha producido un error al exportar el usuario pnet_user, revisar los logs"
	exit
fi

printf "\033[32m \n%s \033[0m \n" "Usuarios exportados exitosamente"

tar czf docVault.tar.gz -C $DOCVAULT docVault

if [ -f docVault.tar.gz ]
then
	printf "\033[32m \n%s \033[0m \n" "Document Vault exportado exitosamente"
else
	printf "\033[32m \n%s \033[0m \n" "Se ha producido un error al exportar el Document Vault"
fi
