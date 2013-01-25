@echo off
:: Title			: Restaurar Base de datos de Project.net
:: Date			: 24/11/2012
:: Author		: Nicolás Semeniuk
:: Version		: 0.1
:: Description		: Script para restaurar la base de datos de Project.net
:: Options		: 

echo -----------------------------------------------------------------------
echo 	Script para importar la BD de Project.net
echo -----------------------------------------------------------------------
echo -----------------------------------------------------------------------

:: Seleccionar la carpeta donde estan los archivos
set /p folder= Por favor, ingrese la carpeta donde se encuentran los archivos:
set /p clean_and_create_users_dir= Por favor, ingrese la carpeta donde se encuentran el archivo clean_and_create_users.sql:
set /p compile_views_dir= Por favor, ingrese la carpeta donde se encuentran el archivo compile_views.sql:

:: Cambiar ubicacion
cd %folder%

:: Limpia los datos que ya se encuentren en la base de datos
echo -----------------------------------------------------------------------
echo Limpiando datos de la base de datos...
echo -----------------------------------------------------------------------
sqlplus system/system@XE @%clean_and_create_users_dir%\clean_and_create_users.sql

echo -----------------------------------------------------------------------
echo Descomprimiendo archivos...
echo -----------------------------------------------------------------------
7za e pnet_db_dump.7z 
7za e pnet_user_db_dump.7z 

echo -----------------------------------------------------------------------
echo Restaurando la base de datos...
echo -----------------------------------------------------------------------
set NLS_LANG=AMERICAN_AMERICA.AL32UTF8
imp userid=pnet/pnet@XE file=pnet_db_dump.dmp fromuser=pnet touser=pnet log=pnet_restore.log
imp userid=pnet_user/pnet_user@XE file=pnet_user_db_dump.dmp fromuser=pnet_user touser=pnet_user log=pnet_user_restore.log

echo -----------------------------------------------------------------------
echo Compilando vistas...
echo -----------------------------------------------------------------------
:: Por si hubo errores al generar algunas vistas
sqlplus pnet/pnet@XE @%compile_views_dir%\compile_views.sql

echo -----------------------------------------------------------------------
echo Restaurando documentos...
echo -----------------------------------------------------------------------
7za e docVault.7z -oC:\docVault -r

echo -----------------------------------------------------------------------
echo Restauracion Terminada!
echo -----------------------------------------------------------------------

pause