@echo off
:: Title		: import
:: Date			: 01/05/2013
:: Author		: Nicolás Semeniuk
:: Version		: 0.2
:: Description	: Script to import the Project.net database and document vault
:: Options		: 

echo -----------------------------------------------------------------------
echo 	Script to import Project.net database
echo -----------------------------------------------------------------------
echo -----------------------------------------------------------------------

:: Select the folders where the files are
set /p folder= Please, insert the folder where the backup files are (e.g. C:\Backup_pnet_db_XXXX-XX-XX):
set /p clean_and_create_users_dir= Please, insert the folder where the file clean_and_create_users.sql is:
set /p compile_views_dir= Please, insert the folder where the file compile_views.sql is:
set /p doc_vault= Please, insert the path where the current installation Document Vault is (e.g. C:\docvault):

:: Change location
cd %folder%

echo -----------------------------------------------------------------------
echo Cleaning Database Data...
echo -----------------------------------------------------------------------
sqlplus system/system@XE @%clean_and_create_users_dir%\clean_and_create_users.sql

echo -----------------------------------------------------------------------
echo Decompressing Files...
echo -----------------------------------------------------------------------
7za e pnet_db_dump.7z 
7za e pnet_user_db_dump.7z 

echo -----------------------------------------------------------------------
echo Restoring Database...
echo -----------------------------------------------------------------------
set NLS_LANG=AMERICAN_AMERICA.AL32UTF8
imp userid=pnet/pnet@XE file=pnet_db_dump.dmp fromuser=pnet touser=pnet log=pnet_restore.log
imp userid=pnet_user/pnet_user@XE file=pnet_user_db_dump.dmp fromuser=pnet_user touser=pnet_user log=pnet_user_restore.log

echo -----------------------------------------------------------------------
echo Compiling views...
echo -----------------------------------------------------------------------
:: In case there was errors generating the views
sqlplus pnet/pnet@XE @%compile_views_dir%\compile_views.sql

echo -----------------------------------------------------------------------
echo Restoring Document Vault...
echo -----------------------------------------------------------------------
7za e docVault.7z -o%doc_vault% -r

echo -----------------------------------------------------------------------
echo Import Completed!
echo -----------------------------------------------------------------------

pause