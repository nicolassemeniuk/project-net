
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

/* Creating pn_wiki_attachment tables */


CREATE TABLE PN_WIKI_ATTACHMENT 
(	WIKI_ATTACHMENT_ID NUMBER(20,0) NOT NULL , 
	WIKI_PAGE_ID NUMBER(20,0) NOT NULL , 
	ATTACHMENT_NAME VARCHAR2(240 BYTE) NOT NULL , 
	FILE_ID NUMBER(20,0) NOT NULL , 
	ATTACHED_ON_DATE DATE NOT NULL , 
	ATTACHED_BY NUMBER(20,0) NOT NULL , 
	DESCRIPTION CLOB, 
	RECORD_STATUS VARCHAR2(1 BYTE) DEFAULT 'A', 
	CONSTRAINT WIKI_ATTACHMENT_PK PRIMARY KEY (WIKI_ATTACHMENT_ID) , 
	CONSTRAINT WIKI_ATTACHMENT_UQ UNIQUE (WIKI_PAGE_ID, ATTACHMENT_NAME) , 
	CONSTRAINT WIKI_ATTACHMENT_WP_ID_FK FOREIGN KEY (WIKI_PAGE_ID)
	REFERENCES PNET.PN_WIKI_PAGE (WIKI_PAGE_ID) , 
	CONSTRAINT WIKI_ATTACHMENT_A_BY_FK FOREIGN KEY (ATTACHED_BY)
	REFERENCES PNET.PN_PERSON (PERSON_ID) 
) 
/