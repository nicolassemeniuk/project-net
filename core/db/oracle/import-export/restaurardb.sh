#!/bin/sh
#: Title		: Restaurar/Cargar la base de datos inicial
#: Date			: 18/07/2012
#: Author		: Ramiro Savoie
#: Version		: 0.1
#: Description		: Borra la base de datos de Project.net, si existe y carga una inicial.
#:			  Presupone que los scripts estan en /opt/pnet/ con las rutas ya configuradas
#: Options		:

# No se puede borrar la db si hay algun usuario conectado
sudo service tomcat6 stop

echo 'Borrando la base de datos'
cd /opt/pnet/v9.2.05_OS/database/create-scripts/tools
sqlplus system/system@XE @drop_pnet_and_user_schemas.sql

echo 'Creando la base de datos inicial'
cd /opt/pnet/v9.2.05_OS/database/create-scripts/versions/9.2.0/new
./pnetMasterDBBuild.sh

sudo service tomcat6 start

# Para que ya vaya arrancando los Beans
wget --delete-after  'http://localhost:9090/pnet'
