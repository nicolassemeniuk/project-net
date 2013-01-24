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
create table PNET.PN_OBJECT_HAS_CHARGE_CODE
(
  OBJECT_ID NUMBER(20) not null,
  CODE_ID  NUMBER(20) not null,
  SPACE_ID  NUMBER(20) not null
)
/

alter table PNET.PN_OBJECT_HAS_CHARGE_CODE
  add constraint PN_OBJECT_HAS_CHARGE_CODE_PK primary key (OBJECT_ID, SPACE_ID)
/

alter table PNET.PN_OBJECT_HAS_CHARGE_CODE
  add constraint PN_OBJECT_HAS_CHARGE_CODE_FK1 foreign key (OBJECT_ID)
  references PNET.PN_OBJECT (OBJECT_ID)
/

alter table PNET.PN_OBJECT_HAS_CHARGE_CODE
  add constraint PN_OBJECT_HAS_CHARGE_CODE_FK2 foreign key (CODE_ID)
  references PNET.PN_CHARGE_CODE (CODE_ID)
/