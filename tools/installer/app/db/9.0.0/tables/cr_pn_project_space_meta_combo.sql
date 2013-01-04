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
-- prompt  Creating table PN_PROJECT_SPACE_META_COMBO
-- prompt  ======================================
-- prompt 
create table PN_PROJECT_SPACE_META_COMBO
(	
	PROPERTY_ID		NUMBER(3) not null,
	COMBO_VALUE 	VARCHAR2(500) not null,
	COMBO_LABEL 	VARCHAR2(500) not null
) 
/

alter table PN_PROJECT_SPACE_META_COMBO
  add constraint PN_PROJECT_SPACE_META_COMBO_PK primary key (PROPERTY_ID, COMBO_VALUE)
/
  
-- prompt  creating synonym for PN_PROJECT_SPACE_META_COMBO table.                                                                                                    
CREATE OR REPLACE PUBLIC SYNONYM PN_PROJECT_SPACE_META_COMBO FOR PNET.PN_PROJECT_SPACE_META_COMBO
/         

-- prompt 
-- prompt  Inserting combo value for project space properties
-- prompt 
insert into PN_PROJECT_SPACE_META_COMBO (PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) 
values (7, 'Deductible Expense', 'Deductible Expense')
/
insert into PN_PROJECT_SPACE_META_COMBO (PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) 
values (7, 'Current Expense', 'Current Expense')
/
insert into PN_PROJECT_SPACE_META_COMBO (PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) 
values (7, 'Capital Expense', 'Capital Expense')
/
insert into PN_PROJECT_SPACE_META_COMBO (PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) 
values (6, 'Marketing', 'Marketing')
/
insert into PN_PROJECT_SPACE_META_COMBO (PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) 
values (6, 'Manufacturing', 'Manufacturing')
/
insert into PN_PROJECT_SPACE_META_COMBO (PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) 
values (6, 'Administration', 'Administration')
/
insert into PN_PROJECT_SPACE_META_COMBO (PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) 
values (6, 'Services', 'Services')
/
insert into PN_PROJECT_SPACE_META_COMBO (PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) 
values (6, 'Sales', 'Sales')
/
insert into PN_PROJECT_SPACE_META_COMBO (PROPERTY_ID, COMBO_VALUE, COMBO_LABEL) 
values (6, 'RD', 'RD')
/
