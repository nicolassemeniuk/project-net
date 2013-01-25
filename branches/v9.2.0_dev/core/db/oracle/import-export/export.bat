@echo off
:: Title		: Exportar base de datos.
:: Date			: 24/11/2012
:: Author		: Marcelo Insaurralde, Nicolás Semeniuk
:: Version		: 0.1
:: Description	: Script para exportar la bases de datos y el document vault.
:: Options		:

echo -----------------------------------------------------------------------
echo 	Script para exportar la BD de Project.net
echo -----------------------------------------------------------------------
echo -----------------------------------------------------------------------

echo -----------------------------------------------------------------------
echo Deteniendo Tomcat
echo -----------------------------------------------------------------------
net stop Tomcat6

echo -----------------------------------------------------------------------
echo Creando Directorio
echo -----------------------------------------------------------------------
:: Crear una carpeta con la fecha de hoy.
for /f "tokens=1-4 delims=/ " %%a in ('date /t') do (set mydate=%%c-%%b-%%a)
mkdir C:\Backup_pnet_db_%mydate%
cd C:\Backup_pnet_db_%mydate%

:: Exportar BD.
echo -----------------------------------------------------------------------
echo Exportando la Base de Datos....
echo -----------------------------------------------------------------------
set NLS_LANG=AMERICAN_AMERICA.AL32UTF8
exp userid=pnet/pnet@XE file=pnet_db_dump.dmp owner=pnet log=pnet_dump.log
exp userid=pnet_user/pnet_user@XE file=pnet_user_db_dump.dmp owner=pnet_user log=pnet_user_dump.log

echo -----------------------------------------------------------------------
echo Backup de la base de datos listo.
echo -----------------------------------------------------------------------

echo -----------------------------------------------------------------------
echo Comprimiendo Archivos...
echo -----------------------------------------------------------------------
::Base de datos
7za a -t7z pnet_db_dump.7z pnet_db_dump.dmp
del pnet_db_dump.dmp

7za a -t7z pnet_user_db_dump.7z pnet_user_db_dump.dmp
del pnet_user_db_dump.dmp

::Documentos
7za a -t7z docVault.7z C:\docVault

echo -----------------------------------------------------------------------
echo Backup de Base de Datos y Documentos Listo.
echo -----------------------------------------------------------------------

pause