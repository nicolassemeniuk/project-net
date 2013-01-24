/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
/*
 * CREATE TABLESPACES
 *
 * NOTE: Must run as SYSTEM databse user.
 * create tablespaces for project.net application Schemas: PNET and PNET_USER
 * Table spaces are created as locally managed by default for simplicity and
 * better performance.  
 * 
 * You may wish to manually create these tablespaces prior to running these 
 * schema creation scripts, then comment out the call to this file.
 *
 * All these tablespaces can be mapped to the same tablespace name in the
 * pnetMasterDBBuild file.  Small to medium sized databases on high-performance
 * storage hardware can use one tablespace for all tables and indicies.  Large
 * installations should consider using separate tablespaces for tables and indicies
 * for performance and managability.
 */

/* Create tablespace data01 */
CREATE TABLESPACE data01 
	DATAFILE '&1.data01.dbf' SIZE 300M
	AUTOEXTEND ON MAXSIZE UNLIMITED
    EXTENT MANAGEMENT LOCAL AUTOALLOCATE;
    
/* Create index tablespace */

CREATE tablespace index01
       DATAFILE '&1.index01.dbf' SIZE 300M
       AUTOEXTEND ON MAXSIZE UNLIMITED
       EXTENT MANAGEMENT LOCAL AUTOALLOCATE;


/* 
 * The forms (tracking databases) features of project.net dynamically create tables and
 * indices in the PNET_USER schema at runtime.  You may want to have these tables and
 * indices in separate tablespaces.
 */

/* Create forms data tablespace */

CREATE TABLESPACE forms_data
       DATAFILE '&1.forms_data_01.dbf' SIZE 100M
       AUTOEXTEND ON MAXSIZE UNLIMITED
       EXTENT MANAGEMENT LOCAL AUTOALLOCATE;


/* Create forms index tablespace */

CREATE TABLESPACE forms_index
       DATAFILE '&1.forms_index_01.dbf' SIZE 100M 
       AUTOEXTEND ON MAXSIZE UNLIMITED
       EXTENT MANAGEMENT LOCAL AUTOALLOCATE;

exit;
