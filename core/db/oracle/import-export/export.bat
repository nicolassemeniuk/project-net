@echo off
:: Title		: export
:: Date			: 01/05/2013
:: Author		: Marcelo Insaurralde, Nicolás Semeniuk
:: Version		: 0.2
:: Description	: Script to export the Project.net database and document vault
:: Options		:

echo -----------------------------------------------------------------------
echo 	Script to export Project.net database
echo -----------------------------------------------------------------------

echo -----------------------------------------------------------------------
echo Creating Directory
echo -----------------------------------------------------------------------
:: Create a Backup folder with today's date.
for /f "tokens=1-4 delims=/ " %%a in ('date /t') do (set mydate=%%c-%%b-%%a)
mkdir C:\Backup_pnet_db_%mydate%
cd C:\Backup_pnet_db_%mydate%

echo -----------------------------------------------------------------------
echo 	The backups files will be created on the path: 
echo			C:\Backup_pnet_db_%mydate%
echo -----------------------------------------------------------------------

set /p doc_vault= Please, insert the path where the Document Vault is (e.g. C:\docvault):

echo -----------------------------------------------------------------------
echo Stopping Tomcat...
echo -----------------------------------------------------------------------
net stop Tomcat6

::Database
::--------------------------------------------------------------------------

echo -----------------------------------------------------------------------
echo Exporting Database....
echo -----------------------------------------------------------------------
set NLS_LANG=AMERICAN_AMERICA.AL32UTF8
exp userid=pnet/pnet@XE file=pnet_db_dump.dmp owner=pnet log=pnet_dump.log
exp userid=pnet_user/pnet_user@XE file=pnet_user_db_dump.dmp owner=pnet_user log=pnet_user_dump.log
echo -----------------------------------------------------------------------
echo Database Backup Ready.
echo -----------------------------------------------------------------------

echo -----------------------------------------------------------------------
echo Compressing Database Files...
echo -----------------------------------------------------------------------
7za a -t7z pnet_db_dump.7z pnet_db_dump.dmp
del pnet_db_dump.dmp
7za a -t7z pnet_user_db_dump.7z pnet_user_db_dump.dmp
del pnet_user_db_dump.dmp
echo -----------------------------------------------------------------------
echo Database Files Compressed.
echo -----------------------------------------------------------------------

::Documents
::--------------------------------------------------------------------------

echo -----------------------------------------------------------------------
echo Compressing Document Vault Files...
echo -----------------------------------------------------------------------
7za a -t7z docVault.7z %doc_vault%
echo -----------------------------------------------------------------------
echo Document Vault Files Compressed.
echo -----------------------------------------------------------------------

echo -----------------------------------------------------------------------
echo Export Completed!
echo -----------------------------------------------------------------------

pause