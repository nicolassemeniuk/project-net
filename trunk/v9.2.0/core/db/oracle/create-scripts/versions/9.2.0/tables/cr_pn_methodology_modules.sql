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
prompt
prompt Creating table PN_METHODOLOGY_MODULES
prompt ======================================
prompt
create table PN_METHODOLOGY_MODULES
(	
	METHODOLOGY_ID		NUMBER(20) not null,
	MODULE_ID			NUMBER(20) not null,
	PRIMARY KEY (METHODOLOGY_ID, MODULE_ID),
	CONSTRAINT PN_METH_MODULES_METHODOLOGY_FK FOREIGN KEY (METHODOLOGY_ID) 
		REFERENCES PNET.PN_METHODOLOGY_SPACE (METHODOLOGY_ID),
	CONSTRAINT PN_METH_MODULES_MODULE_FK FOREIGN KEY (MODULE_ID) 
		REFERENCES PNET.PN_MODULE (MODULE_ID)
);
