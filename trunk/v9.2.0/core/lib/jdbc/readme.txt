Information on shipped JDBC drivers
===================================
02/25/2003 - Tim	ojdbc14.jar from Oracle 9.
10/26/2005 - Roger	Updated ojdbc14.jar to JDBC 3.0 version shipped with Oracle 10.2.0.1.0
			This version supports 

This driver is used for comiling driver-dependent java classes.  
Application servers usually specify their own database drivers.

ojdbc14.jar  -  Oracle 10g JDBC Drivers for Java 1.4 and 1.5 (java5).
                Provides thin driver for any 8i, 9i, 10g database.

We now include only THIN driver support.  For OCI drivers, install the Oracle client
on the application server machines.  This will provdes the correct driver.

For other Oracle drivers, see Oracle's driver download page at:
http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html

To use OCI driver, classpath must be modified to include correct OCI driver
from Oracle client installation.  For example:
  c:\oracle\ora81\jdbc\lib\classes12.zip
  c:\oracle\ora81\jdbc\lib\ojdbc14.jar
 

