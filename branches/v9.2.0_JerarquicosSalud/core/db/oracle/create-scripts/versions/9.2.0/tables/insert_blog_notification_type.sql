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

--blog notification object type 
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('blog','@prm.notification.objecttype.blog.name', 1);

--blog event types
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2400, 'blog_entry_create', 'prm.notification.type.blog.new.description', 'blog', 'A', null );

insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2401, 'blog_entry_modified', 'prm.notification.type.blog.edited.description', 'blog', 'A', null );

insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2402, 'blog_entry_deleted', 'prm.notification.type.blog.deleted.description', 'blog', 'A', null );

insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2403, 'blog_comment_added', 'prm.notification.type.blog.commented.description', 'blog', 'A', null );

--blog notification types
insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, 
CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE ) 
values (2400, 'blog_entry_create','prm.notification.type.blog.new.description','prm.notification.type.blog.new.defaultmessage', 'blog', null, 
1, null, 1, 'A', null,0);

insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, 
CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE ) 
values (2401, 'blog_entry_modified','prm.notification.type.blog.edited.description','prm.notification.type.blog.edited.defaultmessage', 'blog', null, 
1, null, 1, 'A', null,0);

insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, 
CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE ) 
values (2402, 'blog_entry_deleted','prm.notification.type.blog.deleted.description','prm.notification.type.blog.deleted.defaultmessage', 'blog', null, 
1, null, 1, 'A', null,0);

insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, 
CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE ) 
values (2403, 'blog_comment_added','prm.notification.type.blog.commented.description','prm.notification.type.blog.commented.defaultmessage', 'blog', null, 
1, null, 1, 'A', null,0);

-- blog event has notification
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2400,2400);

insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2401,2401);

insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2402,2402);

insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2403,2403);

-- enable constraints
prompt Enabling foreign key constraints for PN_EVENT_TYPE...
alter table PN_EVENT_TYPE enable constraint PN_EVENT_TYPE_FK1;
prompt Enabling foreign key constraints for PN_NOTIFICATION_TYPE...
alter table PN_NOTIFICATION_TYPE enable constraint NOTIFICATION_TYPE_FK1;
prompt Enabling foreign key constraints for PN_EVENT_HAS_NOTIFICATION...
alter table PN_EVENT_HAS_NOTIFICATION enable constraint PN_EVENT_HAS_NOTIFICATION_FK1;
alter table PN_EVENT_HAS_NOTIFICATION enable constraint PN_EVENT_HAS_NOTIFICATION_FK2;