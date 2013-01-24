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
-- prompt 
-- prompt  Creating table PN_WIKI_ASSIGNMENT
-- prompt  ======================================
-- prompt 
create table PN_WIKI_ASSIGNMENT
(	
	OBJECT_ID  NUMBER(20) NOT NULL,
	WIKI_PAGE_ID NUMBER(20) NOT NULL,
	 CONSTRAINT PN_WIKI_PAGE_ID_FK FOREIGN KEY (WIKI_PAGE_ID)
	 REFERENCES PNET.PN_WIKI_PAGE (WIKI_PAGE_ID),
	 CONSTRAINT PN_OBJECT_ID_FK FOREIGN KEY (OBJECT_ID)
	 REFERENCES PNET.PN_OBJECT (OBJECT_ID),
	 CONSTRAINT PN_WIKI_ASSIGNMENT_UQ UNIQUE (OBJECT_ID)
)
/

-- Create synonym PN_WIKI_ASSIGNMENT
-- prompt  creating synonym for PN_WIKI_ASSIGNMENT table.
CREATE OR REPLACE PUBLIC SYNONYM PN_WIKI_ASSIGNMENT FOR PNET.PN_WIKI_ASSIGNMENT
/
