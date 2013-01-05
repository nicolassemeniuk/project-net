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

-- disable constraints
prompt Disabling foreign key constraints for PN_EVENT_TYPE...
alter table PN_EVENT_TYPE disable constraint PN_EVENT_TYPE_FK1;
prompt Disabling foreign key constraints for PN_NOTIFICATION_TYPE...
alter table PN_NOTIFICATION_TYPE disable constraint NOTIFICATION_TYPE_FK1;
prompt Disabling foreign key constraints for PN_EVENT_HAS_NOTIFICATION...
alter table PN_EVENT_HAS_NOTIFICATION disable constraint PN_EVENT_HAS_NOTIFICATION_FK1;
alter table PN_EVENT_HAS_NOTIFICATION disable constraint PN_EVENT_HAS_NOTIFICATION_FK2;

--wiki notification object type 
insert into PN_NOTIFICATION_OBJECT_TYPE ( OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('wiki','@prm.notification.objecttype.wiki.name', 1);

--wiki event type 
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2410, 'wiki_page_create', 'prm.notification.type.wiki.new.description', 'wiki', 'A', null  );

insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2411, 'wiki_page_modify', 'prm.notification.type.wiki.edited.description', 'wiki', 'A', null  );

insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2412, 'wiki_page_deleted', 'prm.notification.type.wiki.deleted.description', 'wiki', 'A', null  );

insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2413, 'wiki_upload_image', 'prm.notification.type.wiki.uploadimage.description', 'wiki', 'A', null  );

--wiki notification type 
insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, 
CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE ) 
values (2410, 'wiki_page_create','prm.notification.type.wiki.create.description','prm.notification.type.wiki.create.defaultmessage', 'wiki', null, 
1, null, 1, 'A', null,0);

insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, 
CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE ) 
values (2411, 'wiki_page_modify','prm.notification.type.wiki.edited.description','prm.notification.type.wiki.edited.defaultmessage', 'wiki', null, 
1, null, 1, 'A', null,0);

insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, 
CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE ) 
values (2412, 'wiki_page_deleted','prm.notification.type.wiki.deleted.description','prm.notification.type.wiki.deleted.defaultmessage', 'wiki', null, 
1, null, 1, 'A', null,0);

insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, 
CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE ) 
values (2413, 'wiki_upload_image','prm.notification.type.wiki.uploadimage.description','prm.notification.type.wiki.uploadimage.defaultmessage', 'wiki', null, 
1, null, 1, 'A', null,0);

--wiki event has notification
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2410,2410);

insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2411,2411);

insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2412,2412);

insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2413,2413);

-- enable constraints
prompt Enabling foreign key constraints for PN_EVENT_TYPE...
alter table PN_EVENT_TYPE enable constraint PN_EVENT_TYPE_FK1;
prompt Enabling foreign key constraints for PN_NOTIFICATION_TYPE...
alter table PN_NOTIFICATION_TYPE enable constraint NOTIFICATION_TYPE_FK1;
prompt Enabling foreign key constraints for PN_EVENT_HAS_NOTIFICATION...
alter table PN_EVENT_HAS_NOTIFICATION enable constraint PN_EVENT_HAS_NOTIFICATION_FK1;
alter table PN_EVENT_HAS_NOTIFICATION enable constraint PN_EVENT_HAS_NOTIFICATION_FK2;