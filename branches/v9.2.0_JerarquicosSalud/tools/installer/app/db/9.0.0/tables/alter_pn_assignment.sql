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
-- Alter the pn_assignment table to add assignor_id
	
ALTER TABLE PN_ASSIGNMENT ADD (
ASSIGNOR_ID     NUMBER(20))
/
 	
ALTER TABLE PN_ASSIGNMENT
ADD CONSTRAINT ASSIGNMENT_FK3 FOREIGN KEY (ASSIGNOR_ID)
REFERENCES PN_PERSON (PERSON_ID)
/
 	
CREATE INDEX ASSIGNMENT_IDX5 ON PN_ASSIGNMENT (ASSIGNOR_ID)  TABLESPACE INDEX01
/