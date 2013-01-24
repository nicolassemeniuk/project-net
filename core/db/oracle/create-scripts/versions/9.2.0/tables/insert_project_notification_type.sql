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

-- Disable constraints
prompt Disabling foreign key constraints for PN_EVENT_TYPE...
alter table PN_EVENT_TYPE disable constraint PN_EVENT_TYPE_FK1;

prompt Disabling foreign key constraints for PN_NOTIFICATION_TYPE...
alter table PN_NOTIFICATION_TYPE disable constraint NOTIFICATION_TYPE_FK1;

prompt Disabling foreign key constraints for PN_EVENT_HAS_NOTIFICATION...
alter table PN_EVENT_HAS_NOTIFICATION disable constraint PN_EVENT_HAS_NOTIFICATION_FK1;
alter table PN_EVENT_HAS_NOTIFICATION disable constraint PN_EVENT_HAS_NOTIFICATION_FK2;

-- Project notification object type update.
update PN_NOTIFICATION_OBJECT_TYPE pnot 
SET is_subscribable = '1' 
WHERE pnot.object_type='project';

-- Inserting project event types
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2071, 'project_modified', 'prm.project.event.projectedited.description', 'project', 'A', null );

insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2072, 'project_deleted', 'prm.project.event.projectdeleted.description', 'project', 'A', null );

insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2073, 'participant_invited', 'prm.project.event.participantinvited.description', 'project', 'A', null );

insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC) 
values (2074, 'participant_modified', 'prm.project.event.participantedited.description', 'project', 'A', null );

-- Inserting project notification types
insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE)
values (2071, 'project_modified','prm.notification.type.projectedited.description','prm.notification.type.projectedited.defaultmessage', 'project', null, 1, null, 1, 'A', null,0);

insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE)
values (2072, 'project_deleted','prm.notification.type.projectdeleted.description','prm.notification.type.projectdeleted.defaultmessage', 'project', null, 1, null, 1, 'A', null,0);

insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE)
values (2073, 'participant_invited', 'prm.notification.type.participantinvited.description', 'prm.notification.type.participantinvited.defaultmessage', 'project', null, 1, null, 1, 'A', null,0);

insert into PN_NOTIFICATION_TYPE ( NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC, IS_CREATE_TYPE)
values (2074, 'participant_modified', 'prm.notification.type.participantedited.description', 'prm.notification.type.participantedited.defaultmessage', 'project', null, 1, null, 1, 'A', null,0);

-- Inserting project event has notification
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2071, 2071);

insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2072, 2072);

insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2073, 2073);

insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID) 
values (2074, 2074);

-- Enable constraints
prompt Enabling foreign key constraints for PN_EVENT_TYPE...
alter table PN_EVENT_TYPE enable constraint PN_EVENT_TYPE_FK1;

prompt Enabling foreign key constraints for PN_NOTIFICATION_TYPE...
alter table PN_NOTIFICATION_TYPE enable constraint NOTIFICATION_TYPE_FK1;

prompt Enabling foreign key constraints for PN_EVENT_HAS_NOTIFICATION...
alter table PN_EVENT_HAS_NOTIFICATION enable constraint PN_EVENT_HAS_NOTIFICATION_FK1;
alter table PN_EVENT_HAS_NOTIFICATION enable constraint PN_EVENT_HAS_NOTIFICATION_FK2;