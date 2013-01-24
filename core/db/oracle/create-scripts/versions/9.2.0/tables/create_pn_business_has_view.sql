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
prompt Creating table PN_BUSINESS_HAS_VIEW
prompt ====================================
prompt
create table PNET.PN_BUSINESS_HAS_VIEW
(
  BUSINESS_ID NUMBER(20) not null,
  VIEW_ID  NUMBER(20) not null
)
tablespace DATA01
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
alter table PNET.PN_BUSINESS_HAS_VIEW
  add constraint PN_BUSINESS_HAS_VIEW_PK primary key (BUSINESS_ID, VIEW_ID)
  using index 
  tablespace DATA01
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
alter table PNET.PN_BUSINESS_HAS_VIEW
  add constraint PN_BUSINESS_HAS_VIEW_FK1 foreign key (BUSINESS_ID)
  references PNET.PN_BUSINESS (BUSINESS_ID);

alter table PNET.PN_BUSINESS_HAS_VIEW
  add constraint PN_BUSINESS_HAS_VIEW_FK2 foreign key (VIEW_ID)
  references PNET.PN_VIEW (VIEW_ID);