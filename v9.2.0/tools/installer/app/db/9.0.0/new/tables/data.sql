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
-- set feedback off
-- set define off
-- prompt  Disabling foreign key constraints for PN_OBJECT...
alter table PN_OBJECT disable constraint OBJECT_FK1
/
alter table PN_OBJECT disable constraint OBJECT_FK2
/
-- prompt  Disabling foreign key constraints for PN_PERSON...
alter table PN_PERSON disable constraint PERSON_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_ADDRESS...
alter table PN_ADDRESS disable constraint ADDRESS_FK1
/
alter table PN_ADDRESS disable constraint ADDRESS_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_AUTHENTICATOR...
alter table PN_AUTHENTICATOR disable constraint PN_AUTHENTICATOR_FK1
/
-- prompt  Disabling foreign key constraints for PN_BRAND...
alter table PN_BRAND disable constraint PN_BRAND_FK1
/
-- prompt  Disabling foreign key constraints for PN_BRAND_SUPPORTS_LANGUAGE...
alter table PN_BRAND_SUPPORTS_LANGUAGE disable constraint PN_BRAND_SUPPORTS_LANGUAGE_FK1
/
-- prompt  Disabling foreign key constraints for PN_CALENDAR...
alter table PN_CALENDAR disable constraint CALENDAR_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_CLASS...
alter table PN_CLASS disable constraint CLASS_FK1
/
alter table PN_CLASS disable constraint CLASS_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_CLASS_DOMAIN...
alter table PN_CLASS_DOMAIN disable constraint CLASS_DOMAIN_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_CLASS_DOMAIN_VALUES...
alter table PN_CLASS_DOMAIN_VALUES disable constraint CLASS_DOMAIN_VALUES_FK1
/
alter table PN_CLASS_DOMAIN_VALUES disable constraint CLASS_DOMAIN_VAL_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_CLASS_FIELD_PROPERTY...
alter table PN_CLASS_FIELD_PROPERTY disable constraint CLASS_FIELD_PROPERTY_FK1
/
-- prompt  Disabling foreign key constraints for PN_CLASS_TYPE_ELEMENT...
alter table PN_CLASS_TYPE_ELEMENT disable constraint CLASS_TYPE_ELEMENT_FK1
/
alter table PN_CLASS_TYPE_ELEMENT disable constraint CLASS_TYPE_ELEMENT_FK2
/
-- prompt  Disabling foreign key constraints for PN_GROUP...
alter table PN_GROUP disable constraint PN_GROUP_FK2
/
alter table PN_GROUP disable constraint PN_GROUP_FK3
/
-- prompt  Disabling foreign key constraints for PN_DEFAULT_OBJECT_PERMISSION...
alter table PN_DEFAULT_OBJECT_PERMISSION disable constraint DEFAULT_OBJ_PERM_FK1
/
alter table PN_DEFAULT_OBJECT_PERMISSION disable constraint DEFAULT_OBJ_PERM_FK2
/
-- prompt  Disabling foreign key constraints for PN_DIRECTORY_HAS_PERSON...
alter table PN_DIRECTORY_HAS_PERSON disable constraint DIRECTORY_PERSON_FK1
/
alter table PN_DIRECTORY_HAS_PERSON disable constraint DIRECTORY_PERSON_FK2
/
-- prompt  Disabling foreign key constraints for PN_DOC_CONTAINER...
alter table PN_DOC_CONTAINER disable constraint DOC_CONTAINER_FK1
/
alter table PN_DOC_CONTAINER disable constraint DOC_CONTAINER_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_DOC_PROVIDER...
alter table PN_DOC_PROVIDER disable constraint DOC_PROVIDER_FK1
/
-- prompt  Disabling foreign key constraints for PN_DOC_SPACE...
alter table PN_DOC_SPACE disable constraint DOC_SPACE_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_DOC_PROVIDER_HAS_DOC_SPACE...
alter table PN_DOC_PROVIDER_HAS_DOC_SPACE disable constraint DOC_PROV_DOC_SPACE_FK1
/
alter table PN_DOC_PROVIDER_HAS_DOC_SPACE disable constraint DOC_PROV_DOC_SPACE_FK2
/
-- prompt  Disabling foreign key constraints for PN_DOC_SPACE_HAS_CONTAINER...
alter table PN_DOC_SPACE_HAS_CONTAINER disable constraint DOC_SPACE_CONTAINER_FK1
/
alter table PN_DOC_SPACE_HAS_CONTAINER disable constraint DOC_SPACE_CONTAINER_FK2
/
-- prompt  Disabling foreign key constraints for PN_ELEMENT_DISPLAY_CLASS...
alter table PN_ELEMENT_DISPLAY_CLASS disable constraint ELEMENT_DISPLAY_CLASS_FK1
/
alter table PN_ELEMENT_DISPLAY_CLASS disable constraint ELEMENT_DISPLAY_CLASS_FK2
/
-- prompt  Disabling foreign key constraints for PN_ELEMENT_PROPERTY...
alter table PN_ELEMENT_PROPERTY disable constraint ELEMENT_PROPERTY_FK1
/
alter table PN_ELEMENT_PROPERTY disable constraint ELEMENT_PROPERTY_FK2
/
-- prompt  Disabling foreign key constraints for PN_EVENT_TYPE...
alter table PN_EVENT_TYPE disable constraint PN_EVENT_TYPE_FK1
/
-- prompt  Disabling foreign key constraints for PN_NOTIFICATION_TYPE...
alter table PN_NOTIFICATION_TYPE disable constraint NOTIFICATION_TYPE_FK1
/
-- prompt  Disabling foreign key constraints for PN_EVENT_HAS_NOTIFICATION...
alter table PN_EVENT_HAS_NOTIFICATION disable constraint PN_EVENT_HAS_NOTIFICATION_FK1
/
alter table PN_EVENT_HAS_NOTIFICATION disable constraint PN_EVENT_HAS_NOTIFICATION_FK2
/
-- prompt  Disabling foreign key constraints for PN_GLOBAL_CODE...
alter table PN_GLOBAL_CODE disable constraint GLOBAL_CODE_FK1
/
-- prompt  Disabling foreign key constraints for PN_GROUP_HAS_PERSON...
alter table PN_GROUP_HAS_PERSON disable constraint GROUP_HAS_PERSON_FK1
/
alter table PN_GROUP_HAS_PERSON disable constraint GROUP_HAS_PERSON_FK2
/
-- prompt  Disabling foreign key constraints for PN_LOGIN_HISTORY...
alter table PN_LOGIN_HISTORY disable constraint LOGIN_HISTORY_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_MODULE...
alter table PN_SPACE_HAS_MODULE disable constraint SPACE_HAS_MODULE_FK1
/
-- prompt  Disabling foreign key constraints for PN_MODULE_PERMISSION...
alter table PN_MODULE_PERMISSION disable constraint MODULE_PERMISSION_FK1
/
alter table PN_MODULE_PERMISSION disable constraint MODULE_PERMISSION_FK2
/
-- prompt  Disabling foreign key constraints for PN_OBJECT_PERMISSION...
alter table PN_OBJECT_PERMISSION disable constraint OBJECT_PERMISSION_FK1
/
alter table PN_OBJECT_PERMISSION disable constraint OBJECT_PERMISSION_FK2
/
-- prompt  Disabling foreign key constraints for PN_OBJECT_TYPE_SUPPORTS_ACTION...
alter table PN_OBJECT_TYPE_SUPPORTS_ACTION disable constraint OBJ_TYPE_SUP_ACTION_FK1
/
alter table PN_OBJECT_TYPE_SUPPORTS_ACTION disable constraint OBJ_TYPE_SUP_ACTION_FK2
/
-- prompt  Disabling foreign key constraints for PN_PERSON_AUTHENTICATOR...
alter table PN_PERSON_AUTHENTICATOR disable constraint PERSON_AUTH_FK2
/
alter table PN_PERSON_AUTHENTICATOR disable constraint PN_PERSON_AUTH_FK1
/
-- prompt  Disabling foreign key constraints for PN_PERSON_NOTIFICATION_ADDRESS...
alter table PN_PERSON_NOTIFICATION_ADDRESS disable constraint PERSON_NOTIFICATION_ADDR_FK1
/
alter table PN_PERSON_NOTIFICATION_ADDRESS disable constraint PERSON_NOTIFICATION_ADDR_FK2
/
-- prompt  Disabling foreign key constraints for PN_PERSON_PROFILE...
alter table PN_PERSON_PROFILE disable constraint PN_PERSON_PROFILE_FK1
/
-- prompt  Disabling foreign key constraints for PN_PORTFOLIO...
alter table PN_PORTFOLIO disable constraint PORTFOLIO_OBJ_FK
/
-- prompt  Disabling foreign key constraints for PN_PORTFOLIO_HAS_SPACE...
alter table PN_PORTFOLIO_HAS_SPACE disable constraint PORTFOLIO_SPACE_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_CALENDAR...
alter table PN_SPACE_HAS_CALENDAR disable constraint SPACE_HAS_CALENDAR_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_DIRECTORY...
alter table PN_SPACE_HAS_DIRECTORY disable constraint SPACE_DIRECTORY_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_DOC_SPACE...
alter table PN_SPACE_HAS_DOC_SPACE disable constraint SPACE_HAS_DOC_SPACE_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_GROUP...
alter table PN_SPACE_HAS_GROUP disable constraint SPACE_HAS_GROUP_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_PERSON...
alter table PN_SPACE_HAS_PERSON disable constraint SPACE_PERSON_FK1
/
-- prompt  Disabling foreign key constraints for PN_SPACE_HAS_PORTFOLIO...
alter table PN_SPACE_HAS_PORTFOLIO disable constraint SPACE_PORTFOLIO_FK1
/
-- prompt  Disabling foreign key constraints for PN_STATE_LOOKUP...
alter table PN_STATE_LOOKUP disable constraint STATE_LOOKUP_FK1
/
-- prompt  Disabling foreign key constraints for PN_USER...
alter table PN_USER disable constraint PN_USER_FK1
/
alter table PN_USER disable constraint PN_USER_FK2
/
-- prompt  Disabling foreign key constraints for PN_USER_DEFAULT_CREDENTIALS...
alter table PN_USER_DEFAULT_CREDENTIALS disable constraint PN_USER_DEF_CREDENTIALS_FK1
/
-- prompt  Loading DATABASE_VERSION...
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 0, 0, to_date('20-05-2008 16:28:55', 'dd-mm-yyyy hh24:mi:ss'), 'Version 2.0.0 Final Build (19:18 PST, 5/07/01)')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 0, 1, to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 'Minor product-level patch.  Fixed workflow bugs and added a number of new properties')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 0, 2, to_date('20-05-2008 16:29:30', 'dd-mm-yyyy hh24:mi:ss'), 'Minor product-level patch.  Fixed bugs and minor feature adds.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 0, 3, to_date('20-05-2008 16:29:30', 'dd-mm-yyyy hh24:mi:ss'), 'Minor product-level patch.  Fixed bugs and minor feature adds.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 0, 4, to_date('20-05-2008 16:29:34', 'dd-mm-yyyy hh24:mi:ss'), 'Minor product-level patch.  Fixed bugs and minor feature adds.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 1, 0, to_date('20-05-2008 16:29:35', 'dd-mm-yyyy hh24:mi:ss'), 'Product Revision.  Feature adds and bug fixes.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 1, 1, to_date('20-05-2008 16:29:41', 'dd-mm-yyyy hh24:mi:ss'), 'Minor Product Update.  Feature adds and bug fixes.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 1, 4, to_date('20-05-2008 16:29:48', 'dd-mm-yyyy hh24:mi:ss'), 'Minor Product Update.  Feature adds and bug fixes.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 1, 5, to_date('20-05-2008 16:30:08', 'dd-mm-yyyy hh24:mi:ss'), 'Minor Product Update.  Patch release for 2.1.4.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 1, 6, to_date('20-05-2008 16:30:14', 'dd-mm-yyyy hh24:mi:ss'), 'Minor Product Update.  Patch release for 2.1.5.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 1, 7, to_date('20-05-2008 16:30:19', 'dd-mm-yyyy hh24:mi:ss'), 'Minor Product Update.  Patch release for 2.1.6.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 2, 0, to_date('20-05-2008 16:30:24', 'dd-mm-yyyy hh24:mi:ss'), 'Product Revision.  Feature adds and bug fixes.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 2, 1, to_date('20-05-2008 16:30:47', 'dd-mm-yyyy hh24:mi:ss'), 'Minor update.  Feature adds and bug fixes.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 2, 2, to_date('20-05-2008 16:30:53', 'dd-mm-yyyy hh24:mi:ss'), 'General maintainance release that adds Licensing and bug fixes')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 2, 21, to_date('20-05-2008 16:31:08', 'dd-mm-yyyy hh24:mi:ss'), '2.2.2 Patch update 1')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 2, 3, to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'), 'General maintenance release which adds additional LDAP and Licensing features.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 2, 31, to_date('20-05-2008 16:31:31', 'dd-mm-yyyy hh24:mi:ss'), 'Patch 1 - Bug fixes')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (2, 2, 4, to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'), 'General maintenance release which adds additional reporting and portfolio management features.')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 4, 1, to_date('20-05-2008 16:32:17', 'dd-mm-yyyy hh24:mi:ss'), 'Actual 7.4 release')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 5, 0, to_date('20-05-2008 16:32:27', 'dd-mm-yyyy hh24:mi:ss'), '7.5 Release')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 5, 1, to_date('20-05-2008 16:32:49', 'dd-mm-yyyy hh24:mi:ss'), '7.5 Patch 1 Release')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 6, 0, to_date('20-05-2008 16:32:57', 'dd-mm-yyyy hh24:mi:ss'), '7.6 Release')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 6, 2, to_date('20-05-2008 16:33:03', 'dd-mm-yyyy hh24:mi:ss'), '7.6 Release Patch 2')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 6, 3, to_date('20-05-2008 16:33:09', 'dd-mm-yyyy hh24:mi:ss'), '7.6.3 Release')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 6, 4, to_date('20-05-2008 16:33:23', 'dd-mm-yyyy hh24:mi:ss'), '7.6.4 Patch')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 6, 5, to_date('20-05-2008 16:33:36', 'dd-mm-yyyy hh24:mi:ss'), '7.6.5 Patch')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 6, 6, to_date('20-05-2008 16:33:44', 'dd-mm-yyyy hh24:mi:ss'), '7.6.6 Patch')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 6, 7, to_date('20-05-2008 16:33:54', 'dd-mm-yyyy hh24:mi:ss'), '7.6.7 Patch')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 6, 8, to_date('20-05-2008 16:34:08', 'dd-mm-yyyy hh24:mi:ss'), '7.6.8 Patch')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 7, 0, to_date('20-05-2008 16:34:22', 'dd-mm-yyyy hh24:mi:ss'), '7.7.0')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (7, 7, 2, to_date('20-05-2008 16:34:40', 'dd-mm-yyyy hh24:mi:ss'), 'Project.net v7.7.2')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (8, 0, 0, to_date('20-05-2008 16:34:40', 'dd-mm-yyyy hh24:mi:ss'), '8.0.0 Release')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (8, 1, 0, to_date('20-05-2008 16:34:46', 'dd-mm-yyyy hh24:mi:ss'), '8.1.0 Release')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (8, 1, 1, to_date('20-05-2008 16:34:49', 'dd-mm-yyyy hh24:mi:ss'), '8.1.1 Release')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (8, 1, 2, to_date('20-05-2008 16:34:51', 'dd-mm-yyyy hh24:mi:ss'), '8.1.2 Release')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (8, 1, 3, to_date('20-05-2008 16:34:53', 'dd-mm-yyyy hh24:mi:ss'), '8.1.3 Release')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (8, 1, 4, to_date('20-05-2008 16:34:58', 'dd-mm-yyyy hh24:mi:ss'), '8.1.4 Release')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (8, 2, 0, to_date('20-05-2008 16:34:58', 'dd-mm-yyyy hh24:mi:ss'), '8.2.0')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (8, 3, 0, to_date('20-05-2008 16:35:15', 'dd-mm-yyyy hh24:mi:ss'), '8.3.0')
/
insert into DATABASE_VERSION (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, TIMESTAMP, DESCRIPTION)
values (8, 4, 0, to_date('20-05-2008 16:36:02', 'dd-mm-yyyy hh24:mi:ss'), '8.4.0')
/
commit
/
-- prompt  40 records loaded
-- prompt  Loading DATABASE_VERSION_UPDATE...
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'cr_table_pn_user.sql', 'Created new pn_user table to support user-specific properties and decouple the user from pn_person.', to_date('20-05-2008 16:30:25', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'cr_table_pn_user_domain_supports_config.sql', 'User Domain / Configuration mutli-join table.', to_date('20-05-2008 16:30:25', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'cr_table_pn_user_default_credentials.sql', 'Created new table to support authentication for the Native (default) authentication provider.  These credentials superceed pn_person_profile username and password.', to_date('20-05-2008 16:30:25', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'cr_table_pn_ldap_configuration_context.sql', 'Created new table to support ldap configurations.', to_date('20-05-2008 16:30:26', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_pn_element.sql', 'Added calculation form field', to_date('20-05-2008 16:30:26', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'add_datetime_field.sql', 'Adds PN_TIME_FORMAT table, renames Datetime form element', to_date('20-05-2008 16:30:26', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'alter_pn_person_profile_time_format.sql', 'Adds time_format_id column to PN_PERSON_PROFILE', to_date('20-05-2008 16:30:26', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_pn_event_type.sql', 'Inserted new events types', to_date('20-05-2008 16:30:26', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_pn_notification_type.sql', 'Insert new notification types ', to_date('20-05-2008 16:30:26', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_pn_event_has_notification.sql', 'Inserted new notification events', to_date('20-05-2008 16:30:26', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_forms_action_lookup.sql', 'Creates PN_FORMS_ACTION_LOOKUP table for  lookup of events related to forms  ', to_date('20-05-2008 16:30:26', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_forms_history.sql', 'Creates PN_FORMS_HISTORY table for logging events', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_group_action_lookup.sql', 'Creatse PN_GROUP_ACTION_LOOKUP table for  lookup of events related to groups  ', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_group_history.sql', 'Creates PN_GROUP_HISTORY table for logging events related to groups', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_news_history.sql', 'Creates PN_NEWS_HISTORY table for logging events', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_news_history.sql', 'Creates PN_NEWS_HISTORY table for logging events', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_post_action_lookup.sql', 'Creates PN_POST_ACTION_LOOKUP table for  lookup of events related to Posted Messges  ', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_post_history.sql', 'Creates PN_POST_HISTORY table for logging events', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_task_action_lookup.sql', 'Creates PN_TASK_ACTION_LOOKUP table for  lookup of events related to tasks ', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_task_history.sql', 'Creates PN_TASK_HISTORY table for logging events', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_notification_object_type.sql', 'New table created for storing Notification Object types ', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_pn_notification_object_type.sql', 'Insert new notification object types ', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'alter_pn_notification_type.sql', 'Add a new Foreign key to pn_notification_type  ', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_discussion_action_lookup.sql', 'Created a new table for Discussion Action lookup', to_date('20-05-2008 16:30:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_discussion_history.sql', 'Created a new table for logging events regarding discussion', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'post_body_clob.sql', 'Creates PN_POST_BODY_CLOB table for storing Data for posted messages as CLOB', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'alter_pn_post.sql', 'Alters PN_POST table for storing Data for posted messages as CLOB', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_pn_global_domain.sql', 'Two new project status types inserted', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'update_pn_module.sql', 'Updated DIRECTORY module name and permissions', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'migrate_roster_to_directory_permission.sql', 'Changed all usage of ROSTER permission to DIRECTORY permission', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'delete_roster_permission_from_businesses.sql', 'Deleted obsolete ROSTER permission from businesses', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'create_pn_group_type.sql', 'Created PN_GROUP_TYPE table for security changes', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_pn_group_type.sql', 'Inserted rows into PN_GROUP_TYPE', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'alter_pn_group.sql', 'Updated group table for security changes', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'alter_pn_space_has_group.sql', 'Updated pn_space_has_group table for security changes', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'create_pn_group_has_group.sql', 'Create PN_GROUP_HAS_GROUP table for security changes', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_authenticator_types.sql', 'Inserted default authenticator types', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_default_authenticators.sql', 'Inserted default authentication providers (currently native and ldap)', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_default_user_domain.sql', 'Inserted default project.net user domain', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_user_domain_module.sql', 'Inserted a security module for user domains', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'insert_user_domain_object_type.sql', 'Inserted an object type for user domains', to_date('20-05-2008 16:30:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'migrate_user_data.sql', 'Migrated all user login identity data to the new pn_user table (username, person_id)', to_date('20-05-2008 16:30:30', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'migrate_user_domain_supports_config.sql', 'Retrofit all existing configurations to support the default user authenticatoin domain', to_date('20-05-2008 16:30:30', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'update_sequence_numbers.sql', 'Updated the domain_value_seq field of pn_class_domain_values to have valid values instead of zero', to_date('20-05-2008 16:30:30', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'cr_table_pn_calculation_field_formula.sql', 'Create Calculation table for Forms changes', to_date('20-05-2008 16:30:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'alter_pn_class_list_field.sql', 'Updated PN_CLASS_LIST_FIELD to add IS_CALCULATE_TOTAL', to_date('20-05-2008 16:30:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'cr_table_pn_notification_sched_status.sql', 'Create Calculation table for Forms changes', to_date('20-05-2008 16:30:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pn_space_has_featured_menuitem.sql', 'Adds the ability to display certain instances of a tool in the side menu bar', to_date('20-05-2008 16:30:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pkg_profile.sql', 'Updated the profile package for security group changes', to_date('20-05-2008 16:30:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pkg_news.sql', 'Updated the news package for notification changes', to_date('20-05-2008 16:30:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pkg_task.sql', 'Updated the task package for notification changes', to_date('20-05-2008 16:30:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pkg_business.sql', 'Updated the business package for security group changes', to_date('20-05-2008 16:30:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pkg_project.sql', 'Updated the project package for security group changes', to_date('20-05-2008 16:30:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pkg_application.sql', 'Updated the application package for security group changes', to_date('20-05-2008 16:30:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pkg_configuration.sql', 'Updated the configuration package for security group changes', to_date('20-05-2008 16:30:36', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pkg_methodology.sql', 'Replaced methodology package', to_date('20-05-2008 16:30:36', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pkg_security.sql', 'Updated the security package for security group changes', to_date('20-05-2008 16:30:37', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pkg_space.sql', 'Updated the space package for security group changes', to_date('20-05-2008 16:30:37', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'pkg_user_domain.sql', 'Created new USER_DOMAIN package.', to_date('20-05-2008 16:30:38', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_person_view.sql', 'Updated to add a last_login column and drop a number of columns migrated to pn_user_view.', to_date('20-05-2008 16:30:38', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_group_member_count_view.sql', 'Created PN_GROUP_MEMBER_COUNT_VIEW', to_date('20-05-2008 16:30:38', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_group_view.sql', 'Created PN_GROUP_VIEW', to_date('20-05-2008 16:30:38', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_wf_step_has_group_view.sql', 'Updated view to work better with new group view', to_date('20-05-2008 16:30:38', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_class_inst_active_cnt_view.sql', 'View for returning the Active count for  Form Data', to_date('20-05-2008 16:30:38', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_user_view.sql', 'Created view for refactored user support.', to_date('20-05-2008 16:30:39', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_user_domain_view.sql', 'Created to support new user domain authentication services.', to_date('20-05-2008 16:30:39', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_news_history_view.sql', 'View for news history ', to_date('20-05-2008 16:30:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_task_history_view.sql', 'View for task history ', to_date('20-05-2008 16:30:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_forms_history_view.sql', 'View for forms history ', to_date('20-05-2008 16:30:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_discussion_history_view.sql', 'View for discussion history ', to_date('20-05-2008 16:30:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'view_pn_envelope_has_object_view.sql', 'Recreated PN_ENVELOPE_HAS_OBJECT_VIEW', to_date('20-05-2008 16:30:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 1, 'create_pn_space_has_subscription.sql', 'Created a synonym for table pn_space_has_subscription', to_date('20-05-2008 16:30:47', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 1, 'pkg_discussion.sql', 'DISCUSSION Package modifications.  Includes fixes for Template Document copy.', to_date('20-05-2008 16:30:47', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 1, 'pkg_security.sql', 'Made changes for business templates ', to_date('20-05-2008 16:30:48', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 1, 'pkg_space.sql', 'Updated the space package including fix for invitation decline bug', to_date('20-05-2008 16:30:48', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'update_pn_object_type.sql', ' Reduce the Default Action Permission for Process and Phase to VIEW only ', to_date('20-05-2008 16:30:53', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'update_pn_module.sql', ' Reduce the Default Action Permission for  Module to VIEW,CREATE,and MODIFY only  ', to_date('20-05-2008 16:30:53', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'alter_pn_invited_users.sql', ' Added a new INVITATION_ACTED_UPON column and a new UNIQUE KEY constraint ', to_date('20-05-2008 16:30:53', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'update_pn_person.sql', ' Set RECORD_STATUS of each rows of pn_person to A ', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'alter_pn_user.sql', 'Changes PK to allow foreign key definition', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_license_certificate.sql', 'Create table PN_LICENSE_CERTIFICATE', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_license_certificate_lob.sql', 'Creates table PN_LICENSE_CERTIFICATE_LOB', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_payment_model_type.sql', 'Creates table PN_PAYMENT_MODEL_TYPE', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_payment_mode.sql', 'Creates PN_PAYMENT_MODEL', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_payment_model_charge.sql', 'Create table PN_PAYMENT_MODEL_CHARGE', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_payment_model_creditcard.sql', 'Create table PN_PAYMENT_MODEL_CREDITCARD', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_payment_information', 'Create table PN_PAYMENT_INFORMATION', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_license.sql', 'Create table PN_LICENSE', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_person_has_license.sql', 'Create table PN_PERSON_HAS_LICENSE', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_license_person_history.sql', 'Creates table PN_LICENSE_PERSON_HISTORY', to_date('20-05-2008 16:30:54', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_bill.sql', 'Create table PN_BILL', to_date('20-05-2008 16:30:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_license_master_prop_clob.sql', 'Create table PN_LICENSE_MASTER_PROP_CLOB', to_date('20-05-2008 16:30:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'insert_object_type.sql', 'Create new object type License', to_date('20-05-2008 16:30:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'insert_payment_model_type.sql', 'Inserts Payment Model Types', to_date('20-05-2008 16:30:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'alter_pn_class.sql', 'Added two fields, supports_discussion_group and supports_document_vault', to_date('20-05-2008 16:30:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'cr_pn_doc_handler.sql', 'Create a table containing a registry of file types and the classes they invoke when run from the doc vault', to_date('20-05-2008 16:30:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'cr_pn_object_has_doc_container.sql', 'Class that indicates an object other than a personal, business, or project space has a document container', to_date('20-05-2008 16:30:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'alter_pn_language.sql', ' Disabled the Japanese Language ', to_date('20-05-2008 16:30:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'pkg_profile.sql', 'Includes fixes for duplicate login names and email ID related bugs ', to_date('20-05-2008 16:30:56', 'dd-mm-yyyy hh24:mi:ss'))
/
commit
/
-- prompt  100 records committed...
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'pkg_space.sql', 'Includes fixes for bug which allowed single Assignment to acted upon more than once ', to_date('20-05-2008 16:30:56', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'pkg_profile.sql', ' New Role of Power User added ', to_date('20-05-2008 16:30:57', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'pkg_document.sql', 'Updates to path handling which allows an arbitrary path root to be defined.', to_date('20-05-2008 16:30:59', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'pkg_forms.sql', 'Made log event an autonomous transaction', to_date('20-05-2008 16:30:59', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'view_pn_methodology_by_user_view.sql', ' Modify the view to add record_status column ', to_date('20-05-2008 16:31:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_appadmin_document_vault.sql', 'Creates a document vault for the application administrator', to_date('20-05-2008 16:31:01', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'pkg_project.sql', 'Added new Power User User ', to_date('20-05-2008 16:31:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'alter_status_messages.sql', '  Alter STATUS MESSAGES  to increase the size of title column to 30 characters', to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'alter_pn_user.sql', '  Altered  PN_USER  to check their login status', to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'update_pn_person_profile.sql', ' This script updates all NULL values and Upper case values of EN to lower case ones  ', to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'update_pn_object_type.sql', '   Changed the value of IS_SECURABLE to 0 for  the object types of scheduled_subscription ,subscription ,brand ', to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'update_pn_space_has_module.sql', ' This script disables all modules related to BUDGET ,PROJECT METRICS , VOTES and Site Works ', to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'update_pn_doc_format.sql', 'Added two additional document format types (DOT, POT) to support Word and PPT template files.', to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'update_pn_person.sql', ' Set RECORD_STATUS of each rows of pn_person to A ', to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'alter_pn_person.sql', 'Altered PN_PERSON to modify USER_STATUS constraint', to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'create_pn_directory_provider_type.sql', 'Create table PN_DIRECTORY_PROVIDER_TYPE', to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'insert_pn_directory_provider_type.sql', 'Insert PN_DIRECTORY_PROVIDER_TYPE rows', to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'alter_pn_user_domain.sql', 'Alter table PN_USER_DOMAIN', to_date('20-05-2008 16:31:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'create_pn_ldap_directory_config.sql', 'Create table PN_LDAP_DIRECTORY_CONFIG', to_date('20-05-2008 16:31:16', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'create_pn_ldap_directory_attr_map.sql', 'Create table PN_LDAP_DIRECTORY_ATTR_MAP', to_date('20-05-2008 16:31:16', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'create_pn_domain_migration.sql', 'Added  PN_DOMAIN_MIGRATION for Domain Migration', to_date('20-05-2008 16:31:16', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'create_pn_user_domain_migration.sql', ' Added  PN_USER_DOMAIN_MIGRATION for tracking Domain Migration for Individual Users', to_date('20-05-2008 16:31:16', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'update_pn_user_domain.sql', 'Added registration instructions token to global domain', to_date('20-05-2008 16:31:16', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'create_pn_license_status_reasons.sql', ' Created  PN_LICENSE_STATUS_REASONS  maintains license status reason codes and corresponding messages. ', to_date('20-05-2008 16:31:16', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'alter_pn_license.sql', '  Altered  PN_LICENSE  to keep track of status and status reason and also of the responsible person for license', to_date('20-05-2008 16:31:16', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'update_pn_license.sql', ' Set LICENSE_STATUS of each rows of pn_license to 100 ', to_date('20-05-2008 16:31:16', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'alter_pn_bill.sql', '  Altered  PN_BILL to add creation_date column with a NOT NULL constraint', to_date('20-05-2008 16:31:17', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'create_pn_invoice.sql', ' Created  PN_INVOICE  maintains the invoice ids for the invoice clobs stored in pn_invoice_lob. ', to_date('20-05-2008 16:31:17', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 2, 'create_pn_ledger.sql', 'Create table PN_LEDGER', to_date('20-05-2008 16:31:17', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'create_pn_invoice_lob.sql', ' Created  PN_INVOICE_LOB  maintains the invoice lobs for the invoices. ', to_date('20-05-2008 16:31:17', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'domain_controller.sql', ' This script acts as the Controller for Adding a DOMAIN_ID column to the PN_DEFAULTS_CREDENTIALS ,  populating it from PN_USER Table  and adding new Constraints based on that ', to_date('20-05-2008 16:31:18', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_calendar.sql', 'Made alterations to log the changes in Meeting Host ', to_date('20-05-2008 16:31:18', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkb_message.sql', 'Made changes so that procedures raises exception instead of ignoring them ', to_date('20-05-2008 16:31:18', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_security.sql', 'Made changes for altering module permissions for Power User user ', to_date('20-05-2008 16:31:19', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_business.sql', 'Made changes to disable un-used modules ', to_date('20-05-2008 16:31:20', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_project.sql', 'Made changes to disable un-used modules ', to_date('20-05-2008 16:31:20', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_profile.sql', 'Update package PROFILE', to_date('20-05-2008 16:31:22', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_methodology.sql', 'Made changes to add Power User role', to_date('20-05-2008 16:31:22', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_notification.sql', 'Made changes for adding Form Data types events to Form Based Subscriptions ', to_date('20-05-2008 16:31:23', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_user_domain.sql', 'Made alterations to show only active users in user counts ', to_date('20-05-2008 16:31:23', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_application.sql', 'Added new function to calculate the rate of change of new users ', to_date('20-05-2008 16:31:23', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_message.sql', 'Made changes to DELETE_MESSAGE Procedure call', to_date('20-05-2008 16:32:05', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_document.sql', 'Fixed bug in document path and improved performance of paths and container lists', to_date('20-05-2008 16:32:06', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_business.sql', 'Tokenized a string ', to_date('20-05-2008 16:32:06', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_methodology.sql', 'Tokenized a string ', to_date('20-05-2008 16:32:07', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_methodology.sql', 'Started creating a schedule as part of a template.', to_date('20-05-2008 16:32:07', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_profile.sql', 'Tokenized a string and modified create_person, create_person_profile', to_date('20-05-2008 16:32:08', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_calendar.sql', 'Removed the hardcoded Meeting Host string', to_date('20-05-2008 16:32:08', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_space.sql', 'Tokenized a string', to_date('20-05-2008 16:32:08', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_security.sql', 'Made changes to fix Template Copy Bug', to_date('20-05-2008 16:32:09', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_discussion.sql', 'Tokenized a string ', to_date('20-05-2008 16:32:09', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_process.sql', 'Only copy deliverables that are currently active.', to_date('20-05-2008 16:32:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_notification.sql', 'Changed error handling code in create_scheduled_subscription procedure.', to_date('20-05-2008 16:32:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'view_pn_project_view.sql', 'Replace view PN_PROJECT_VIEW', to_date('20-05-2008 16:32:11', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'view_pn_business_space_view.sql', 'Create view PN_BUSINESS_SPACE_VIEW', to_date('20-05-2008 16:32:12', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'view_pn_person_view.sql', 'Replace view PN_PERSON_VIEW', to_date('20-05-2008 16:32:12', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 4, 1, 'alter_pn_notification.sql', 'Altered PN_NOTIFICATION added CUSTOMIZATION_USER_ID column', to_date('20-05-2008 16:32:17', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 4, 1, 'create_pn_property_change', 'Created new table PN_PROPERTY_CHANGE to store property update dates', to_date('20-05-2008 16:32:17', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 4, 1, 'alter_pn_project_space.sql', 'Altered table PN_PROJECT_SPACE setting default values on improvement code columns', to_date('20-05-2008 16:32:17', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 4, 1, 'pkg_notification.sql', 'Changed error handling code in create_scheduled_subscription procedure.', to_date('20-05-2008 16:32:18', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 4, 1, 'pkg_workflow.sql', 'Reinforced create envelope method so it wont fail if it is missing a new status for an initial step.', to_date('20-05-2008 16:32:19', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 4, 1, 'pkg_form.sql', 'Fixed to create security for copied form lists
/ fixes linker error', to_date('20-05-2008 16:32:19', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_char_to_varchar.sql', 'Converted all CHAR columns to VARCHAR2 columns', to_date('20-05-2008 16:32:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_pn_discussion_group.sql', 'PN_DISCUSSION_GROUP.DISCUSSION_GROUP_CHARTER convert to CLOB', to_date('20-05-2008 16:32:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'drop_pn_discussion_history_view.sql', 'Drop obsolete view', to_date('20-05-2008 16:32:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_PN_NEWS.sql', 'PN_NEWS.MESSAGE convert to CLOB', to_date('20-05-2008 16:32:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_pn_task_comment.sql', 'pn_task_comment.text convert to CLOB', to_date('20-05-2008 16:32:30', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_pn_workflow_step.sql', 'pn_workflow_step.notes convert to CLOB', to_date('20-05-2008 16:32:30', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_pn_deliverable.sql', 'pn_deliverable.deliverable_comments convert to CLOB', to_date('20-05-2008 16:32:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_pn_domain_migration.sql', 'pn_domain_migration.admin_message convert to CLOB', to_date('20-05-2008 16:32:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_pn_scheduled_subscription.sql', 'pn_scheduled_subscription.custom_message convert to CLOB', to_date('20-05-2008 16:32:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_pn_subscription.sql', 'pn_subscription.custom_message convert to CLOB', to_date('20-05-2008 16:32:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_pn_agenda_item.sql', 'pn_agenda_item.meeting_notes convert to CLOB', to_date('20-05-2008 16:32:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_pn_calendar_event.sql', 'pn_calendar_event.event_desc and pn_calendar_event.event_purpose convert to CLOB', to_date('20-05-2008 16:32:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_space_tables.sql', 'Converted all space description columns to 1000 characters', to_date('20-05-2008 16:32:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_pn_envelope_version.sql', 'pn_envelope_version.comments convert to CLOB', to_date('20-05-2008 16:32:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_pn_license_status_reasons.sql', 'pn_license_status_reasons.message reduced to 1000 characters', to_date('20-05-2008 16:32:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_PN_METHODOLOGY_SPACE.sql', 'PN_METHODOLOGY_SPACE.USE_SCENARIO convert to CLOB', to_date('20-05-2008 16:32:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_PN_USER_DOMAIN.sql', 'PN_USER_DOMAIN.REGISTRATION_INSTRUCTIONS convert to CLOB', to_date('20-05-2008 16:32:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_PN_CLASS_FIELD.sql', 'PN_CLASS_FIELD.INSTRUCTIONS convert to CLOB', to_date('20-05-2008 16:32:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_property_tables.sql', 'pn_property.property_value convert to CLOB', to_date('20-05-2008 16:32:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'cr_credit_card_transaction.sql', 'Created new table to store credit card transactions.  (Without cc number).', to_date('20-05-2008 16:32:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'cr_pn_cc_transaction_payment.sql', 'Created table to link credit card transaction to licenses.', to_date('20-05-2008 16:32:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'alter_user_domain.sql', 'Add supports_credit_card_number flag and sets all existing domains to support them.', to_date('20-05-2008 16:32:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'cr_license_purchaser.sql', 'Created new table to store person who purchased license.', to_date('20-05-2008 16:32:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_news.sql', 'Fix date parameter
/ Change PN_NEWS.MESSAGE to PN_NEWS.MESSAGE_CLOB', to_date('20-05-2008 16:32:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_profile.sql', 'Stop altering the transaction while logging history.', to_date('20-05-2008 16:32:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_base.sql', 'Modifications for database globalization', to_date('20-05-2008 16:32:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_security.sql', 'Misc globalization changes.', to_date('20-05-2008 16:32:36', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_discussion.sql', 'CLOB conversions', to_date('20-05-2008 16:32:36', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_document.sql', 'Misc globalization modifications', to_date('20-05-2008 16:32:38', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_schedule.sql', 'Change PN_TASK_COMMENT.TEXT to PN_TASK_COMMENT.TEXT_CLOB', to_date('20-05-2008 16:32:38', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_workflow.sql', 'Modifications for converting PN_WORKFLOW_STEP.NOTES to CLOB', to_date('20-05-2008 16:32:39', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_process.sql', 'Converted PN_DELIVERABLE.DELIVERABLE_COMMENTS to CLOB', to_date('20-05-2008 16:32:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_notification.sql', 'Converted PN_SUBSCRIPTION and PN_SCHEDULED_SUBSCRIPTION.CUSTOM_MESSAGE to CLOB', to_date('20-05-2008 16:32:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_calendar.sql', 'Converted PN_AGENDA_ITEM.MEETING_NOTES to CLOB', to_date('20-05-2008 16:32:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_project.sql', 'Signature change to SCHEDULE.CREATE_PLAN', to_date('20-05-2008 16:32:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_methodology.sql', 'Converted PN_METHODOLOGY_SPACE.USE_SCENARIO to CLOB', to_date('20-05-2008 16:32:42', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_forms.sql', 'Converted PN_CLASS_FIELD.INSTRUCTIONS to CLOB', to_date('20-05-2008 16:32:42', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'pkg_configuration.sql', 'Removed obsolete procedure', to_date('20-05-2008 16:32:42', 'dd-mm-yyyy hh24:mi:ss'))
/
commit
/
-- prompt  200 records committed...
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'view_pn_user_domain_view.sql', 'Convert PN_USER_DOMAIN.REGISTRATION_INSTRUCTIONS to CLOB', to_date('20-05-2008 16:32:42', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'view_pn_news_view.sql', 'CLOB column change', to_date('20-05-2008 16:32:43', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'view_pn_workflow_step_view.sql', 'Changed PN_WORKFLOW_STEP.NOTES to NOTES_CLOB', to_date('20-05-2008 16:32:43', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'view_pn_envelope_version_view.sql', 'Convert PN_ENVELOPE_VERSION.COMMENTS to CLOB', to_date('20-05-2008 16:32:43', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'view_pn_workflow_envelope_view.sql', 'Convert PN_ENVELOPE_VERSION.COMMENTS to CLOB', to_date('20-05-2008 16:32:43', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'view_pn_envelope_has_object_view.sql', 'Changed PN_WORKFLOW_STEP.NOTES to NOTES_CLOB', to_date('20-05-2008 16:32:43', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'view_pn_envelope_has_person_view.sql', 'Changed PN_WORKFLOW_STEP.NOTES to NOTES_CLOB', to_date('20-05-2008 16:32:43', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 0, 'view_pn_methodology_view.sql', 'Converted PN_METHODOLOGY_SPACE.USE_SCENARIO to CLOB', to_date('20-05-2008 16:32:44', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 5, 1, 'alter_pn_task.sql', 'Increase the length of the task_name field to 255 characters.', to_date('20-05-2008 16:32:49', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 0, 'alter_pn_task.sql', 'Changes to task table to support automatic calculation.', to_date('20-05-2008 16:32:57', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 0, 'alter_pn_plan.sql', 'Add the Autocalculate task endpoints field.', to_date('20-05-2008 16:32:57', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 0, 'add_space_has_space_idx.sql', 'Add missing primary key to pn_space_has_space table for performance improvements.', to_date('20-05-2008 16:32:57', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 0, 'add_task_dependencies_idx.sql', 'Add missing index to pn_task_dependency table.', to_date('20-05-2008 16:32:57', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 0, 'add_space_access_history_pk.sql', 'Add missing primary key to space access history table for performance improvements.', to_date('20-05-2008 16:32:57', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 0, 'pkg_project.sql', 'Start setting the project start and end date as the schedule start and end date.', to_date('20-05-2008 16:32:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'view_pn_user_domain_view.sql', 'Replaced view PN_USER_DOMAIN_VIEW', to_date('20-05-2008 16:31:24', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'view_pn_user_view.sql', 'Replace view PN_USER_VIEW', to_date('20-05-2008 16:31:24', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'view_pn_person_view.sql', 'Replace view PN_PERSON_VIEW', to_date('20-05-2008 16:31:24', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 31, 'invitation_controller.sql', ' This script acts as the Controller for Adding a PERSON_ID column to the PN_INVITED_USERS ,  populating it  and adding new Constraints based on that', to_date('20-05-2008 16:31:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'create_client_database_version_table.sql', 'This script creates the client_database_version table to collect custom database versions done specifically for a given client.', to_date('20-05-2008 16:31:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'update_pn_module.sql', 'Increase the default action permission for the Document Module to ALL', to_date('20-05-2008 16:31:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'update_pn_class.sql', ' This script updates PN_CLASS for all existing FORMS whose ownership has changed', to_date('20-05-2008 16:31:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'delete_all_licensing_and_billing_info.sql', '  Deleted all existing licensing and billing info.', to_date('20-05-2008 16:31:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 32, 'alter_pn_person.sql', 'Altered PN_PERSON to drop PERSON_EMAIL_AK2 constraint', to_date('20-05-2008 16:31:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'alter_pn_notification.sql', 'Altered the PN_NOTIFICATION Table for increasing the size of Delivery Address column', to_date('20-05-2008 16:31:32', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_application.sql', 'Added new function to calculate the rate of change of new users ', to_date('20-05-2008 16:31:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_business.sql', 'Made changes to disable un-used modules ', to_date('20-05-2008 16:31:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_message.sql', 'Made changes so that procedures raises exception instead of ignoring them ', to_date('20-05-2008 16:31:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 33, 'pkg_profile.sql', 'Updated the package for changes to PN_INVITED_USERS table', to_date('20-05-2008 16:31:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_configuratiom.sql', 'Removed the redundant procedure INVITE_PERSON_TO_CONFIGURATION ', to_date('20-05-2008 16:31:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_space.sql', 'Removed the redundant procedure', to_date('20-05-2008 16:31:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'pkg_product.sql', 'Added changes that were supposed to be in 2.1.5', to_date('20-05-2008 16:31:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 34, 'view_pn_user_view.sql', 'Replace view PN_USER_VIEW', to_date('20-05-2008 16:31:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'view_pn_person_view.sql', 'Replace view PN_PERSON_VIEW', to_date('20-05-2008 16:31:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 35, 'view_pn_space_view.sql', 'Created a new view called PN_SPACE_VIEW to query generic properties of Spaces', to_date('20-05-2008 16:31:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'appadmin_calendar.sql', ' This script adds Calendar to Application Administrator''s Personal Space ', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'cr_pn_task_dependency.sql', 'Created new table to store task dependencies', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'cr_pn_task_has_constraint.sql', 'Created new table to store task constraints', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_task_times.sql', 'Convert old task time ids to TimeQuantityUnit ids', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'alter_task_number_types.sql', 'Changed column types of pn_task and pn_task_baseline work and work_complete fields to support fractional amounts', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_schedule.sql', 'Extracted method for creating a plan to schedule package from project.', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'add_template_plans.sql', 'Adds a plan (schedule) to every existing methodology that doesnt have one.', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'cr_report_module.sql', 'Created new report module', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'insert_project_space_has_module.sql', 'Insert report module for every project space', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'insert_project_module_permissions.sql', 'Script to add module permissions for reports to existing projects.', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'cr_pivot_table.sql', 'Created new helper table used to create arbitrary rows for queries.', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'insert_pivot_rows.sql', 'Added rows to the pivot table', to_date('20-05-2008 16:31:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'alter_pn_tmp_document.sql', ' Increased the length of SHORT_FILE_NAME column ', to_date('20-05-2008 16:31:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'alter_pn_doc_version.sql', ' Increased the length of SHORT_FILE_NAME column ', to_date('20-05-2008 16:31:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_doc_format.sql', ' Tokenized the column FORMAT_NAME ', to_date('20-05-2008 16:31:59', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_global_domain.sql', ' Tokenized the column CODE_NAME ', to_date('20-05-2008 16:31:59', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_module.sql', ' Tokenized the column DESCRIPTION', to_date('20-05-2008 16:31:59', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_object_type.sql', ' Tokenized the column OBJECT_TYPE_DESC', to_date('20-05-2008 16:31:59', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_event_type.sql', ' Tokenized the column DESCRIPTION', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_notification_delivery_type.sql', ' Tokenized the column NAME', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_security_action.sql', ' Tokenized the column DESCRIPTION', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_subscription_recurrence.sql', ' Tokenized the column NAME', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_directory_provider_type.sql', 'Tokenized the column NAME', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_notification_delivery_type.sql', ' Tokenized the column NAME', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_workflow_strictness.sql', ' Tokenized the column STRICTNESS_NAME', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_workflow_status.sql', ' Tokenized the column STATUS_NAME', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_workflow_rule_status.sql', ' Tokenized the column STATUS_NAME', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_workflow_rule_type.sql', ' Tokenized the column NOTES', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_envelope_history_action.sql', ' Tokenized the column ACTION_NAME', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_payment_model_type.sql', ' Tokenized the column DESCRIPTION', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_state_lookup.sql', ' Tokenized the column STATE_NAME', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 3, 'update_pn_cal_event_has_attendee.sql', 'This script updates pn_cal_event_has_attendee and nulls out the attendee_comment', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_group.sql', ' Tokenized the column GROUP_NAME , GROUP_DESC', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_assignment.sql', ' Tokenized the column ROLE', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, ' update_pn_language.sql', ' Tokenized the column LANGUAGE_NAME', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, ' update_pn_element.sql', ' Tokenized the column ELEMENT_LABEL', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_class_field.sql', 'Updated pn_class_field to tokenize FIELD_LABEL', to_date('20-05-2008 16:32:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'update_pn_country_lookup.sql', ' Tokenized the column COUNTRY_NAME', to_date('20-05-2008 16:32:01', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 41, 'alter_pn_project_space.sql', 'Altered table PN_PROJECT_SPACE', to_date('20-05-2008 16:32:01', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'create_pn_finder_ingredients.sql', 'Created PN_FINDER_INGREDIENTS table to store finder ingredients xml', to_date('20-05-2008 16:32:01', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'create_pn_view.sql', 'Created PN_VIEW table to store view definitions', to_date('20-05-2008 16:32:01', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'create_pn_space_view_context.sql', 'Created PN_SPACE_VIEW_CONTEXT table to store space view relationships', to_date('20-05-2008 16:32:02', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'create_pn_view_default_setting.sql', 'Created PN_VIEW_DEFAULT_SETTING table to default view settings', to_date('20-05-2008 16:32:03', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'insert_pn_language.sql', ' Added entry for the German language ', to_date('20-05-2008 16:32:03', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'alter_pn_person_profile.sql', 'Altered PN_PERSON_PROFILE:- Added locale_code column, dropped PERSON_PROFILE_FK2 constraint, modified the datatype and increased the size of TIMEZONE_CODE column', to_date('20-05-2008 16:32:03', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'alter_pn_property.sql', 'Altered PN_PROPERTY:- Added is_translatable_property column', to_date('20-05-2008 16:32:03', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'alter_table_tmp_pn_property.sql', 'Altered table to add a new column is_translatable_property.', to_date('20-05-2008 16:32:03', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_project.sql', 'Made changes related to Power User Role, Tokenized a string', to_date('20-05-2008 16:32:04', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_project.sql', 'Added report module to project space', to_date('20-05-2008 16:32:04', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_project.sql', 'Added new properties to project', to_date('20-05-2008 16:32:04', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_project.sql', 'Refactor creation of schedule to SCHEDULE.CREATE_PLAN.', to_date('20-05-2008 16:32:04', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_configuration.sql', 'Added a new Power User Role ', to_date('20-05-2008 16:32:04', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_application.sql', 'Added a new Power User Role ', to_date('20-05-2008 16:32:05', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 1, 'cr_table_database_update.sql', 'Created a table to store all subsequent production updates to the database.  This table will be used to track versions and their supported changes over time', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 1, 'load_property_types.sql', 'Adds and additional property type to the pn_property_type table: css', to_date('20-05-2008 16:29:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 2, 'pkg_configuration.sql', 'Configuration package modified to support Configuration Space security.', to_date('20-05-2008 16:29:30', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 2, 'update_configuration_security.sql', 'Updates existing Configuration Spaces to allow for security settings.', to_date('20-05-2008 16:29:30', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 2, 'load_property_types.sql', 'Adds and additional property type to the pn_property_type table: largetext', to_date('20-05-2008 16:29:30', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 2, 'cr_table_pn_property_category.sql', 'Added a new table to support category context for configuration tokens (pn_property_category)', to_date('20-05-2008 16:29:30', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 2, 'load_property_category.sql', 'Loaded system data FOR pn_property_category FOR DEFAULT categories', to_date('20-05-2008 16:29:30', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 2, 'cr_table_pn_prop_category_has_property.sql', 'Added a new table to support category context for configuration tokens (pn_prop_category_has_property)', to_date('20-05-2008 16:29:30', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 3, 'update_app_space_security.sql', 'Updates existing Application Space security settings to support forms module.', to_date('20-05-2008 16:29:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 3, 'update_configuration_security.sql', 'Updates existing Configuration Space security settings to support forms module.', to_date('20-05-2008 16:29:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 3, 'update_property_category.sql', 'Updates Property Category Support.', to_date('20-05-2008 16:29:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 3, 'update_property_category_has_properties.sql', 'Updates Property Category Support.', to_date('20-05-2008 16:29:34', 'dd-mm-yyyy hh24:mi:ss'))
/
commit
/
-- prompt  300 records committed...
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 4, 'alter_table_pn_brand_has_host.sql', 'Adds a "record_status" column to pn_brand_has_host.', to_date('20-05-2008 16:29:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 4, 'cr_table_tmp_pn_property.sql', 'Created table to support new method for updating system properties.', to_date('20-05-2008 16:29:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 0, 4, 'pkg_configuration.sql', 'Update configuration package to support removal of hostnames on configuration delete.', to_date('20-05-2008 16:29:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 0, 'update_property_categories.sql', 'Updated pn_property_category and pn_prop_category_has_property.', to_date('20-05-2008 16:29:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 0, 'update_form_tables.sql', 'Updated pn_class_field and pn_element to tokenize meta-data.', to_date('20-05-2008 16:29:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 0, 'update_assignment_tables.sql', 'Updated pn_assignment to include new due_datetime column.', to_date('20-05-2008 16:29:35', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 1, 'fix_notification_events.sql', 'Fix invalid notification events.', to_date('20-05-2008 16:29:48', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 1, 'update_notification_events.sql', 'Update invalid notification events.', to_date('20-05-2008 16:29:48', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'insert_bookmark_object_type.sql', 'Created new object_type to support URL objects in the document vault.', to_date('20-05-2008 16:29:49', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'cr_table_pn_bookmark.sql', 'Created new table to support URL objects in the document vault.', to_date('20-05-2008 16:29:49', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'cr_tmp_person_table.sql', 'Created tmp table to for data migration for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:29:49', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'cr_table_pn_person_profile.sql', 'Create a new profile table for pn_persons.', to_date('20-05-2008 16:29:49', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'migrate_data_to_pn_person_profile.sql', 'Copy data from the tmp table into pn_person_profile.', to_date('20-05-2008 16:29:49', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'alter_pn_person.sql', 'Remove a number of columns and constraints from pn_person for inclusion in pn_person_profile.', to_date('20-05-2008 16:29:52', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'cr_person_has_alternate_email.sql', 'Created  table to support alternate email addresses for a person.', to_date('20-05-2008 16:29:52', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'alter_business.sql', 'Added a number of new columns to the business table to support billing.', to_date('20-05-2008 16:29:52', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'alter_address.sql', 'Made Zipcode nullable.', to_date('20-05-2008 16:29:52', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'cr_table_pn_business_category.sql', 'Created new table to support business categories.', to_date('20-05-2008 16:29:52', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'cr_table_pn_user_has_master_business.sql', 'Created new table to billing.', to_date('20-05-2008 16:29:52', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'insert_business_categories.sql', 'Inserted business category data.', to_date('20-05-2008 16:29:52', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'syn_pn_space_has_methodology.sql', 'Added missing public synonym PN_SPACE_HAS_METHODOLOGY', to_date('20-05-2008 16:29:52', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'pkg_business.sql', 'Updated Business Package.', to_date('20-05-2008 16:29:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'pkg_profile.sql', 'Updated the profile package for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:29:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'pkg_space.sql', 'Created the space package for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:29:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'pkg_project.sql', 'Updated the project package for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:29:56', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_pn_bookmark_view.sql', 'Create new bookmark view.', to_date('20-05-2008 16:29:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_doc_container_list_view.sql', 'Updated pn_doc_container_list_view to support bookmarks.', to_date('20-05-2008 16:29:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_doc_by_space_view.sql', 'Updated.', to_date('20-05-2008 16:29:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_pn_person_view.sql', 'Created pn_person_view for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:29:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_pn_doc_history_view.sql', 'Updated pn_doc_history_view for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:29:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_envelop_history_view.sql', 'Updated pn_envelope_history_view for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:29:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_pn_envelope_version_view.sql', 'Updated pn_envelope_version_view for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:29:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_pn_news_view.sql', 'Updated pn_news_view for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:29:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_pn_workflow_envelope.sql', 'Updated pn_workflow_envelope_view for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:29:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_pn_workflow_rule_view.sql', 'Updated pn_workflow_rule_view for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:30:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_pn_wf_step_has_group_view.sql', 'Updated pn_wf_step_has_group_view for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:30:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_pn_workflow_step_view.sql', 'Updated pn_workflow_step_view for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:30:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_workflow_transition_view.sql', 'Updated pn_workflow_transition_view for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:30:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_pn_workflow_view.sql', 'Updated pn_workflow_view for 2.1.2 person infrastructure upgrade.', to_date('20-05-2008 16:30:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'view_pn_business_view.sql', 'Create new business view.', to_date('20-05-2008 16:30:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'create_invitee_stubs.sql', 'Created person stubs for invitees who have not registered.', to_date('20-05-2008 16:30:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 4, 'create_space_person_entries.sql', 'Added persons to spaces to which they are invited', to_date('20-05-2008 16:30:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 2, 'load_prop_category_has_property.sql', 'Loaded system data FOR pn_prop_category has property.  This is a default list of category relationships', to_date('20-05-2008 16:30:00', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 5, 'pkg_profile.sql', 'Updated the profile package to fix remove person issue.  The SP would error when removing a person that was participating in a workflow.', to_date('20-05-2008 16:30:09', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 5, 'pkg_security.sql', 'Updated the security package to fix methodology create issue when project belongs to business.', to_date('20-05-2008 16:30:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 6, 'pkg_forms.sql', 'Updated the forms package to fix forms copy error.  It was making deleted fields become pending on the copied form.', to_date('20-05-2008 16:30:14', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 6, 'alter_pn_person.sql', 'Added a Disabled status to the user_status check constraint.', to_date('20-05-2008 16:30:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 1, 7, 'pkg_profile.sql', 'Updated the profile package to fix directory issues when removing a person.', to_date('20-05-2008 16:30:20', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'cr_table_pn_authenticator_type.sql', 'Normalized the authenticator types into this table.', to_date('20-05-2008 16:30:24', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'cr_pn_authenticator.sql', 'Recreated to support new authentication provider model.', to_date('20-05-2008 16:30:24', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 0, 'cr_table_pn_user_domain.sql', 'New table supports user/authentication domains.', to_date('20-05-2008 16:30:24', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 0, 'pkg_schedule.sql', 'Add additional statement required for task endpoint calculation.', to_date('20-05-2008 16:32:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 2, 'fix_task_type.sql', 'Make sure that tasks and summary tasks have appropriate type.  (They haven''t ever been right.)', to_date('20-05-2008 16:33:03', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 2, 'create_task_indices.sql', 'Add additional indices to task_id and parent_task_id for performance of connect by clauses.', to_date('20-05-2008 16:33:03', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 2, 'pkg_schedule.sql', 'Implemented summary tasks.', to_date('20-05-2008 16:33:04', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:33:09', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:33:09', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'create_date_fields.sql', 'Added start date and end date fields to assignments to replace due_datetime.', to_date('20-05-2008 16:33:09', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'add_overallocation_flag.sql', 'New flag to prevent dialog box from showing up.', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'add_work_fields.sql', 'Adds a new field to PN_ASSIGNMENT to store work.', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'assign_work_values.sql', 'Assign values into the work fields of existing assignments.', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'remove_task_status.sql', 'Removed status_id column from task.', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'add_milestone_flag.sql', 'Added milestone flag and removed milestone task type', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'fix_task_type.sql', 'Make sure that tasks and summary tasks have appropriate type.', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'add_early_late_fields.sql', 'Added fields to store est,lst,eft,lst.', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'update_sequence_numbers.sql', 'Updated the seq field of pn_task to have valid values that match the current start date ordering.', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'alter_pn_plan.sql', 'Added default_calendar_id column and timezone_id column', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'add_duration_precision.sql', 'Make it possible to store duration with a fractional part.', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'update_duration_units.sql', 'Correctly set duration_units to days', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'add_work_percent_complete.sql', 'Add a work percent complete column.', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'fix_pn_task_dependency.sql', 'Deleted erroneous inter-plan task dependencies', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'ensure_start_end_dates.sql', 'Updated tasks to ensure no null start or end dates', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'update_form_document_container.sql', 'Update literal text to token', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'alter_pn_property.sql', 'Make is_translatable_property not null with no default value', to_date('20-05-2008 16:33:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'alter_pn_default_object_permission.sql', 'Added index to improvde security selects', to_date('20-05-2008 16:33:11', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'alter_pn_address.sql', 'Make address columns optional', to_date('20-05-2008 16:33:11', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'alter_pn_scheduled_subscription.sql', 'Added space_id column', to_date('20-05-2008 16:33:11', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'create_status_triggers.sql', 'Create triggers that will keep the pn_object.record_status column updated', to_date('20-05-2008 16:33:12', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'update_pno_record_status.sql', 'Fixed record_status on PN_OBJECT records', to_date('20-05-2008 16:33:13', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'object_type_index.sql', 'Convert the object type index from being a standard index to a bitmap index.', to_date('20-05-2008 16:33:13', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'pkg_schedule.sql', 'Modified schedule package', to_date('20-05-2008 16:33:14', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'pkg_security', 'Modified security package', to_date('20-05-2008 16:33:14', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'pkg_document.sql', 'Modified document package to fix document paths', to_date('20-05-2008 16:33:16', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'pkg_profile.sql', 'Modified profile package to fix login history problems', to_date('20-05-2008 16:33:16', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'pkg_project.sql', 'Modified project package to add error handling', to_date('20-05-2008 16:33:17', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'pkg_notification.sql', 'Added columns to scheduled subscription and made available to notification', to_date('20-05-2008 16:33:18', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 3, 'view_pn_doc_by_space_view.sql', 'Update view to include the record status of the enclosing workspace.', to_date('20-05-2008 16:33:18', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 5, 'create_status_triggers.sql', 'Create triggers that will keep the pn_object.record_status column updated', to_date('20-05-2008 16:33:25', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 5, 'create_object_name_tbl.sql', 'Created a new table which has object names for disparate tables', to_date('20-05-2008 16:33:25', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 5, 'create_name_triggers.sql', 'Create triggers that will keep the pn_object_name.name column updated', to_date('20-05-2008 16:33:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 5, 'update_pno_record_status.sql', 'Fixed record_status on PN_OBJECT records', to_date('20-05-2008 16:33:27', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 5, 'pkg_schedule.sql', 'Updated package', to_date('20-05-2008 16:33:28', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 5, 'pkg_project.sql', 'Updated package', to_date('20-05-2008 16:33:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 5, 'pkg_methodology.sql', 'Updated package', to_date('20-05-2008 16:33:29', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 6, 'pkg_schedule.sql', 'Updated package', to_date('20-05-2008 16:33:37', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 7, 'pkg_schedule.sql', 'Updated package', to_date('20-05-2008 16:33:45', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 6, 'fix_work_percent_complete.sql', 'Correct the work_percent_complete which was wrong due to a bug introduced in the scheduling package.', to_date('20-05-2008 16:33:45', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 8, 'alter_pn_user.sql', 'Added last_brand_id column to pn_user', to_date('20-05-2008 16:33:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 8, 'alter_pn_notification.sql', 'Added sender_id column to pn_notification', to_date('20-05-2008 16:33:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 8, 'pn_user_view.sql', 'Added u.last_brand_id column to pn_user_view', to_date('20-05-2008 16:33:55', 'dd-mm-yyyy hh24:mi:ss'))
/
commit
/
-- prompt  400 records committed...
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 6, 8, 'pn_notification_package.sql', 'Made several changes to the pn_notification_package', to_date('20-05-2008 16:33:55', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'fix_token_translateable.sql', 'Fixed translateable flag on tokens recently added', to_date('20-05-2008 16:34:08', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:08', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:08', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:08', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:08', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:08', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:08', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:08', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:09', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:09', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:09', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:09', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:10', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'alter_pn_class_field.sql', 'Modfy pn_class_field for mandatory properties', to_date('20-05-2008 16:34:12', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:12', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:13', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'pkg_schedule.sql', 'Updated package', to_date('20-05-2008 16:34:14', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:14', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:14', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:14', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_pn_workingtime_calendar.sql', 'Create working time calendar table', to_date('20-05-2008 16:34:22', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 1, 'fix_schedule_start_date.sql', 'Tried to find start dates for existing legacy plans.', to_date('20-05-2008 16:34:22', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 1, 'pkg_project.sql', 'Updated package', to_date('20-05-2008 16:34:23', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 1, 'pkg_schedule.sql', 'Updated package', to_date('20-05-2008 16:34:23', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 1, 'pkg_task.sql', 'Updated package', to_date('20-05-2008 16:34:23', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 1, 'pkg_util.sql', 'Updated package', to_date('20-05-2008 16:34:23', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 2, 'alter_pn_workflow_step.sql', 'Added step_sequence column to pn_workflow_step', to_date('20-05-2008 16:34:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 2, 'alter_pn_assignment_work.sql', 'Start storing assignment work at the time of logging.', to_date('20-05-2008 16:34:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 2, 'create_meeting_trigger.sql', 'Create a trigger that will keep object_name updated with the name of meetings', to_date('20-05-2008 16:34:31', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 2, 'pkg_workflow.sql', 'Updated create/edit procedures to store step_sequence', to_date('20-05-2008 16:34:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 2, 'pkg_calendar.sql', 'Improved error handling in calendar package.', to_date('20-05-2008 16:34:33', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 2, 'pkg_schedule.sql', 'Start storing scheduled work in assignment work log', to_date('20-05-2008 16:34:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 2, 'pn_workflow_step_view.sql', 'Added step_sequence column to the view', to_date('20-05-2008 16:34:34', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_object_index.sql', 'Makes some table queries faster.', to_date('20-05-2008 16:34:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'create_project_idx.sql', 'Speed up loading of a project.', to_date('20-05-2008 16:34:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'discussion_notification.sql', 'Allows some additional notification types for discussions.', to_date('20-05-2008 16:34:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'assignment_idx4.sql', 'Speed up assignment loads which filter based on record status.', to_date('20-05-2008 16:34:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'phase_has_task_idx.sql', 'Added missing index for pn_phase_has_task table.', to_date('20-05-2008 16:34:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'task_idx1.sql', 'Added missing index on pn_task', to_date('20-05-2008 16:34:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'task_idx3.sql', 'Made loading of task finder faster by adding another index.', to_date('20-05-2008 16:34:40', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'cr_tables.sql', 'Adds new tables for cross space sharing framework.', to_date('20-05-2008 16:34:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'cr_share_action.sql', 'Creates a new action called sharing to facilitate crossspace sharing.', to_date('20-05-2008 16:34:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'add_token_categories.sql', 'Put some more tokens into categories to make configuration easier for customers.', to_date('20-05-2008 16:34:41', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'pkg_security.sql', 'Changed security package so inherited roles won''t interfere with the detection of the space administrator group.', to_date('20-05-2008 16:34:45', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (7, 7, 0, 'pkg_sharing.sql', 'New package for sharing.  Right now, it can store shares.', to_date('20-05-2008 16:34:45', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (8, 2, 0, 'cr_trashcan_action.sql', 'Creates new actions for the trashcan feature.', to_date('20-05-2008 16:34:58', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (8, 2, 0, 'insert_trashcan_module_permissions.sql', 'Script to add trashcan module permisions to different spaces.', to_date('20-05-2008 16:34:59', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (8, 2, 0, 'pkg_document.sql', 'Modified document package to add hard delete record status', to_date('20-05-2008 16:35:03', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_business.sql', 'Tokenized a string ', to_date('20-05-2008 16:35:03', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (8, 2, 0, 'pkg_process.sql', 'Fix for BFD-2864: Assigning summary tasks to a phase does not set the start/end date of phase.', to_date('20-05-2008 16:35:05', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (8, 2, 0, 'pkg_discussion.sql', 'Now the object_has_discussion doesn''t take into account deleted posts', to_date('20-05-2008 16:35:06', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (8, 2, 0, 'cr_business_space_module.sql', 'Creates report module for business space.', to_date('20-05-2008 16:35:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (8, 2, 0, 'insert_report_module_permissions.sql', 'Script to add report module permisions to business spaces.', to_date('20-05-2008 16:35:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (8, 2, 0, 'cr_business_report_types.sql', 'Adds report types for business space.', to_date('20-05-2008 16:35:15', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (2, 2, 4, 'pkg_business.sql', 'Tokenized a string ', to_date('20-05-2008 16:35:18', 'dd-mm-yyyy hh24:mi:ss'))
/
insert into DATABASE_VERSION_UPDATE (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, PATCH_FILENAME, PATCH_DESCRIPTION, TIMESTAMP)
values (8, 4, 0, 'insert_wiki_module_permissions.sql', 'Script to add wiki module permisions to project spaces.', to_date('20-05-2008 16:36:04', 'dd-mm-yyyy hh24:mi:ss'))
/
commit
/
-- prompt  456 records loaded
-- prompt  Loading PN_COUNTRY_LOOKUP...
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AF', '@prm.global.resource.country.afghanistan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AL', '@prm.global.resource.country.albania.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('DZ', '@prm.global.resource.country.algeria.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AS', '@prm.global.resource.country.americansamoa.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AD', '@prm.global.resource.country.andorra.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AO', '@prm.global.resource.country.angola.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AI', '@prm.global.resource.country.anguilla.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AQ', '@prm.global.resource.country.antartica.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AG', '@prm.global.resource.country.antiguabarbuda.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AR', '@prm.global.resource.country.argentina.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AM', '@prm.global.resource.country.armenia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AW', '@prm.global.resource.country.aruba.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AU', '@prm.global.resource.country.australia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AT', '@prm.global.resource.country.austria.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AZ', '@prm.global.resource.country.azerbaijan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BS', '@prm.global.resource.country.bahamas.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BH', '@prm.global.resource.country.bahrain.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BD', '@prm.global.resource.country.bangladesh.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BB', '@prm.global.resource.country.barbados.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BY', '@prm.global.resource.country.belarus.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BE', '@prm.global.resource.country.belgium.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BZ', '@prm.global.resource.country.belize.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BJ', '@prm.global.resource.country.benin.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BM', '@prm.global.resource.country.bermuda.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BT', '@prm.global.resource.country.bhutan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BO', '@prm.global.resource.country.bolivia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BA', '@prm.global.resource.country.bosniaherzegovina.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BW', '@prm.global.resource.country.botswana.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BV', '@prm.global.resource.country.bouvetisland.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BR', '@prm.global.resource.country.brazil.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('IO', '@prm.global.resource.country.britishindianoceanteritory.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BN', '@prm.global.resource.country.brunei.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BG', '@prm.global.resource.country.bulgaria.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BF', '@prm.global.resource.country.burkinafaso.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('BI', '@prm.global.resource.country.burundi.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('KH', '@prm.global.resource.country.cambodia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CM', '@prm.global.resource.country.cameroon.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CA', '@prm.global.resource.country.canada.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CV', '@prm.global.resource.country.capeverde.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('KY', '@prm.global.resource.country.caymanislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CF', '@prm.global.resource.country.centralafricanrepublic.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TD', '@prm.global.resource.country.chad.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CL', '@prm.global.resource.country.chile.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CN', '@prm.global.resource.country.china.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CX', '@prm.global.resource.country.christmasisland.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CC', '@prm.global.resource.country.cocosislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CO', '@prm.global.resource.country.columbia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('KM', '@prm.global.resource.country.comoros.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CG', '@prm.global.resource.country.congo.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CD', '@prm.global.resource.country.drcongo.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CK', '@prm.global.resource.country.cookislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CR', '@prm.global.resource.country.costarica.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CI', '@prm.global.resource.country.cotedivoire.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('HR', '@prm.global.resource.country.croatia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CU', '@prm.global.resource.country.cuba.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CY', '@prm.global.resource.country.cyprus.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CZ', '@prm.global.resource.country.czechrepublic.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('DK', '@prm.global.resource.country.denmark.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('DJ', '@prm.global.resource.country.djibouti.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('DM', '@prm.global.resource.country.dominica.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('DO', '@prm.global.resource.country.dominicanrepublic.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TP', '@prm.global.resource.country.easttimor.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('EC', '@prm.global.resource.country.ecuador.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('EG', '@prm.global.resource.country.egypt.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SV', '@prm.global.resource.country.elsalvador.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GQ', '@prm.global.resource.country.equatorialguinea.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('ER', '@prm.global.resource.country.eritrea.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('EE', '@prm.global.resource.country.estonia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('ET', '@prm.global.resource.country.ethiopia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('FK', '@prm.global.resource.country.falklandislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('FO', '@prm.global.resource.country.faroeislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('FJ', '@prm.global.resource.country.fiji.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('FI', '@prm.global.resource.country.finland.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('FR', '@prm.global.resource.country.france.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GF', '@prm.global.resource.country.frenchguiana.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PF', '@prm.global.resource.country.frenchploynesia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TF', '@prm.global.resource.country.frenchsouthernterritories.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GA', '@prm.global.resource.country.gabon.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GM', '@prm.global.resource.country.gambia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GE', '@prm.global.resource.country.georgia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('DE', '@prm.global.resource.country.germany.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GH', '@prm.global.resource.country.ghana.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GI', '@prm.global.resource.country.gibraltar.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GR', '@prm.global.resource.country.greece.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GL', '@prm.global.resource.country.greenland.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GD', '@prm.global.resource.country.grenada.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GP', '@prm.global.resource.country.guadeloupe.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GU', '@prm.global.resource.country.guam.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GT', '@prm.global.resource.country.guatemala.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GN', '@prm.global.resource.country.guinea.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GW', '@prm.global.resource.country.guineabissau.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GY', '@prm.global.resource.country.guyana.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('HT', '@prm.global.resource.country.haiti.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('HM', '@prm.global.resource.country.heardislandsmcdonaldislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('VA', '@prm.global.resource.country.vatican.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('HN', '@prm.global.resource.country.honduras.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('HK', '@prm.global.resource.country.hongkong.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('HU', '@prm.global.resource.country.hungary.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('IS', '@prm.global.resource.country.iceland.name')
/
commit
/
-- prompt  100 records committed...
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('ID', '@prm.global.resource.country.indonesia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('JM', '@prm.global.resource.country.jamaica.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('KP', '@prm.global.resource.country.dprkorea.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('KW', '@prm.global.resource.country.kuwait.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('LA', '@prm.global.resource.country.loas.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('LY', '@prm.global.resource.country.libya.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MK', '@prm.global.resource.country.macedonia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MW', '@prm.global.resource.country.malawi.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MQ', '@prm.global.resource.country.martinique.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('FM', '@prm.global.resource.country.micronesia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MN', '@prm.global.resource.country.mongolia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NP', '@prm.global.resource.country.nepal.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NZ', '@prm.global.resource.country.newzealand.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MP', '@prm.global.resource.country.northernmarianaislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PS', '@prm.global.resource.country.palestine.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PN', '@prm.global.resource.country.pitcairn.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('RU', '@prm.global.resource.country.russia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('KN', '@prm.global.resource.country.saintkittsnevis.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('VC', '@prm.global.resource.country.saintvincentgrenadines.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SA', '@prm.global.resource.country.saudiarabia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SK', '@prm.global.resource.country.slovakia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GS', '@prm.global.resource.country.southgeorgiasouthsandwichislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SJ', '@prm.global.resource.country.svalbardandjanmayen.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SY', '@prm.global.resource.country.syria.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TZ', '@prm.global.resource.country.tanzania.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TT', '@prm.global.resource.country.trinidadtobago.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TC', '@prm.global.resource.country.turkscaicos.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('GB', '@prm.global.resource.country.unitedkingdom.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('UM', '@prm.global.resource.country.usminor.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('VG', '@prm.global.resource.country.britishvirginislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('EH', '@prm.global.resource.country.westernsahara.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('XX', '@prm.global.resource.country.nonuscountries.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TO', '@prm.global.resource.country.tongo.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TN', '@prm.global.resource.country.tunisia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TR', '@prm.global.resource.country.turkey.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TM', '@prm.global.resource.country.turkmenistan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TV', '@prm.global.resource.country.tuvalu.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('UG', '@prm.global.resource.country.uganda.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('UA', '@prm.global.resource.country.ukraine.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AE', '@prm.global.resource.country.unitedarabemirates.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('US', '@prm.global.resource.country.unitedstates.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('UY', '@prm.global.resource.country.uruguay.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('UZ', '@prm.global.resource.country.uzbekistan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('VU', '@prm.global.resource.country.vanuatu.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('VE', '@prm.global.resource.country.venezuela.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('VN', '@prm.global.resource.country.vietnam.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('VI', '@prm.global.resource.country.usvirginislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('WF', '@prm.global.resource.country.wallisfutuna.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('YE', '@prm.global.resource.country.yemen.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('YU', '@prm.global.resource.country.yugoslavia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('ZM', '@prm.global.resource.country.zambia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('ZW', '@prm.global.resource.country.zimbabwe.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('IN', '@prm.global.resource.country.india.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('IR', '@prm.global.resource.country.iran.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('IQ', '@prm.global.resource.country.iraq.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('IE', '@prm.global.resource.country.ireland.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('IL', '@prm.global.resource.country.israel.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('IT', '@prm.global.resource.country.italy.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('JP', '@prm.global.resource.country.japan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('JO', '@prm.global.resource.country.jordan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('KZ', '@prm.global.resource.country.kazakstan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('KE', '@prm.global.resource.country.kenya.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('KI', '@prm.global.resource.country.kiribati.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('KR', '@prm.global.resource.country.republicofkorea.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('KG', '@prm.global.resource.country.kyrgyzstan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('LV', '@prm.global.resource.country.latvia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('LB', '@prm.global.resource.country.lebanon.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('LS', '@prm.global.resource.country.lesotho.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('LR', '@prm.global.resource.country.liberia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('LI', '@prm.global.resource.country.liechtenstein.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('LT', '@prm.global.resource.country.lithuania.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('LU', '@prm.global.resource.country.luxembourg.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MO', '@prm.global.resource.country.macau.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MG', '@prm.global.resource.country.madagascar.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MY', '@prm.global.resource.country.malaysia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MV', '@prm.global.resource.country.maldives.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('ML', '@prm.global.resource.country.mali.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MT', '@prm.global.resource.country.malta.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MH', '@prm.global.resource.country.marshallislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MR', '@prm.global.resource.country.mauritania.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MU', '@prm.global.resource.country.mauritius.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('YT', '@prm.global.resource.country.mayotte.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MX', '@prm.global.resource.country.mexico.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MD', '@prm.global.resource.country.moldova.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MC', '@prm.global.resource.country.monaco.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MS', '@prm.global.resource.country.montserrat.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MA', '@prm.global.resource.country.morocco.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MZ', '@prm.global.resource.country.mozambique.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('MM', '@prm.global.resource.country.myanmar.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NA', '@prm.global.resource.country.namibia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NR', '@prm.global.resource.country.nauru.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NL', '@prm.global.resource.country.netherlands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('AN', '@prm.global.resource.country.netherlandsantilles.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NC', '@prm.global.resource.country.newcaledonia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NI', '@prm.global.resource.country.nicaragua.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NE', '@prm.global.resource.country.niger.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NG', '@prm.global.resource.country.nigeria.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NU', '@prm.global.resource.country.niue.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NF', '@prm.global.resource.country.norflokisland.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('NO', '@prm.global.resource.country.norway.name')
/
commit
/
-- prompt  200 records committed...
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('OM', '@prm.global.resource.country.oman.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PK', '@prm.global.resource.country.pakistan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PW', '@prm.global.resource.country.palau.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PA', '@prm.global.resource.country.panama.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PG', '@prm.global.resource.country.papuanewguinea.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PY', '@prm.global.resource.country.paraguay.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PE', '@prm.global.resource.country.peru.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PH', '@prm.global.resource.country.philipines.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PL', '@prm.global.resource.country.poland.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PT', '@prm.global.resource.country.portugal.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PR', '@prm.global.resource.country.puertorico.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('QA', '@prm.global.resource.country.qatar.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('RE', '@prm.global.resource.country.reunionislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('RO', '@prm.global.resource.country.romania.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('RW', '@prm.global.resource.country.rawanda.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SH', '@prm.global.resource.country.sainthelena.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('LC', '@prm.global.resource.country.saintlucia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('PM', '@prm.global.resource.country.saintpierremiquelon.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('WS', '@prm.global.resource.country.westernsamoa.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SM', '@prm.global.resource.country.sanmarino.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('ST', '@prm.global.resource.country.saotomeprincipe.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SN', '@prm.global.resource.country.senegal.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SC', '@prm.global.resource.country.seychelles.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SL', '@prm.global.resource.country.sierraleone.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SG', '@prm.global.resource.country.singapore.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SI', '@prm.global.resource.country.slovenia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SB', '@prm.global.resource.country.solomonislands.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SO', '@prm.global.resource.country.somalia.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('ZA', '@prm.global.resource.country.southafrica.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('ES', '@prm.global.resource.country.spain.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('LK', '@prm.global.resource.country.srilanka.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SD', '@prm.global.resource.country.sudan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SR', '@prm.global.resource.country.suriname.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SZ', '@prm.global.resource.country.swaziland.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('SE', '@prm.global.resource.country.sweden.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('CH', '@prm.global.resource.country.switerland.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TW', '@prm.global.resource.country.taiwan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TJ', '@prm.global.resource.country.tajikistan.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TH', '@prm.global.resource.country.thailand.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TG', '@prm.global.resource.country.togo.name')
/
insert into PN_COUNTRY_LOOKUP (COUNTRY_CODE, COUNTRY_NAME)
values ('TK', '@prm.global.resource.country.tokelau.name')
/
commit
/
-- prompt  240 records loaded
-- prompt  Loading PN_OBJECT_TYPE...
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('address', 'pn_address', '@prm.global.address.objecttype.description', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('agenda_item', 'pn_agenda_item', '@prm.calendar.agenda.objecttype.description', 'meeting', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('business', 'pn_business', '@prm.business.objecttype.description', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('calendar', 'pn_calendar', '@prm.calendar.objecttype.description', null, 7, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('form', 'pn_class', '@prm.form.objecttype.description', null, 1, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('form_domain', 'pn_class_domain', '@prm.form.domain.objecttype.description', 'form', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('form_domain_value', 'pn_class_domain_values', '@prm.form.domainvalue.objecttype.description', 'form_domain', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('form_field', 'pn_class_field', '@prm.form.formfield.objecttype.description', 'form', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('form_list', 'pn_class_list', '@prm.form.formlist.objecttype.description', 'form', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('form_data', null, '@prm.form.formdata.objecttype.description', 'form', 15, 1, 1)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('form_data_version', null, '@prm.form.formdataversion.objecttype.description', 'form', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('form_filter_value', 'pn_class_list_filter', '@prm.form.fieldfilter.objecttype.description', 'form_list', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('deliverable', 'pn_deliverable', '@prm.process.deliverable.objecttype.description', 'phase', 1, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('discussion_group', 'pn_discussion_group', '@prm.discussion.discussiongroup.objecttype.description', null, 5, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('doc_container', 'pn_doc_container', '@prm.document.container.objecttype.description', 'doc_space', 5, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('doc_space', 'pn_doc_space', '@prm.document.documentspace.objecttype.description', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('document', 'pn_document', '@prm.document.document.objecttype.description', 'doc_container', 1, 1, 1)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('document_version', 'pn_doc_version', '@prm.document.documentversion.objecttype.description', 'document', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('domain_value', 'pn_class_domain_values', 'Form Field Domain Value', 'class_domain', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('event', 'pn_calendar_event', '@prm.calendar.event.objecttype.description', 'calendar', 5, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('facility', 'pn_facility', '@prm.global.facility.objecttype.description', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('group', 'pn_group', '@prm.security.group.objecttype.description', null, 1, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('meeting', 'pn_meeting', '@prm.calendar.meeting.objecttype.description', 'calendar', 5, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('person', 'pn_person', '@prm.global.person.objecttype.description', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('plan', 'pn_plan', '@prm.schedule.plan.objecttype.description', null, 7, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('task', 'pn_task', '@prm.schedule.task.objecttype.description', 'schedule', 5, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('portfolio', 'pn_portfolio', '@prm.portfolio.portfolio.objecttype.description', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('post', 'pn_post', '@prm.discussion.post.objecttype.description', null, 1, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('process', 'pn_process', '@prm.process.process.objecttype.description', null, 1, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('gate', 'pn_gate', '@prm.process.gate.objecttype.description', 'phase', 1, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('phase', 'pn_phase', '@prm.process.phase.objecttype.description', 'process', 1, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('project', 'pn_project', '@prm.project.project.objecttype.description', 'process', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('methodology', 'pn_methodology_space', '@prm.methodology.methodology.objecttype.description', 'space', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('workflow', 'pn_workflow', '@prm.workflow.workflow.objecttype.description', null, 1, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('workflow_step', 'pn_workflow_step', '@prm.workflow.step.objecttype.description', 'workflow', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('workflow_transition', 'pn_workflow_transition', '@prm.workflow.transition.objecttype.description', 'workflow', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('workflow_rule', 'pn_workflow_rule', '@prm.workflow.rule.objecttype.description', 'workflow_transition', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('workflow_envelope', 'pn_workflow_envelope', '@prm.workflow.envelope.objecttype.description', 'workflow', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('workflow_envelope_history', 'pn_envelope_history', '@prm.workflow.envelope.history.objecttype.description', 'workflow_envelope', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('workflow_envelope_version', 'pn_envelope_version', '@prm.workflow.envelope.version.objecttype.description', 'workflow_envelope', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('subscription', 'pn_subscription', '@prm.notification.subscription.objecttype.description', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('scheduled_subscription', 'pn_scheduled_subscription', '@prm.notification.scheduledsubscription.objecttype.description', 'subscription', 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('brand', 'pn_brand', 'Brand', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('news', 'pn_news', '@prm.news.news.objecttype.description', null, 1, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('application', 'pn_application_space', '@prm.application.space.objecttype.description', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('configuration', 'pn_configuration_space', '@prm.configuration.space.objecttype.description', null, 7, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('bookmark', 'pn_bookmark', '@prm.document.bookmark.objecttype.description', 'doc_container', 1, 0, 1)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('user_domain', 'pn_user_domain', '@prm.security.domain.objecttype.description', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('license', 'pn_license', '@prm.license.license.objecttype.description', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('baseline', 'pn_baseline', '@prm.schedule.baseline.objecttype.description', null, 1, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('enterprise', 'pn_enterprise_space', '@prm.enterprise.enterprise.objecttype.description', null, 1, 1, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('timesheet', 'pn_timesheet', 'Timesheet for a resource', null, 15, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('activity', 'pn_activity', 'Adhoc Activity for a resource', 'timesheet', 15, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('resource', 'pn_resource_space', '@prm.resource.space.objecttype.description', null, 15, 0, 0)
/
insert into PN_OBJECT_TYPE (OBJECT_TYPE, MASTER_TABLE_NAME, OBJECT_TYPE_DESC, PARENT_OBJECT_TYPE, DEFAULT_PERMISSION_ACTIONS, IS_SECURABLE, IS_WORKFLOWABLE)
values ('allocation', 'pn_person_allocation', 'Resource allocation', null, 15, 0, 0)
/
commit
/
-- prompt  55 records loaded
-- prompt  Loading PN_OBJECT...
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (21, 'form', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (22, 'form', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (23, 'form', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (24, 'form', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (25, 'form', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (29, 'form', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (31, 'form', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (40, 'form', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (41, 'form', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (200, 'form_domain', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (400, 'form_domain_value', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (401, 'form_domain_value', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (402, 'form_domain_value', to_date('20-05-2008 16:28:58', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (1, 'person', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (2, 'address', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (3, 'portfolio', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (4, 'application', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5000, 'group', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5001, 'group', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5002, 'group', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5003, 'portfolio', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5004, 'configuration', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5005, 'group', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5006, 'group', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5007, 'group', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (100, 'group', to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (32, 'form', to_date('20-05-2008 16:29:49', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (33, 'form', to_date('20-05-2008 16:29:49', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5008, 'group', to_date('20-05-2008 16:31:01', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5009, 'doc_space', to_date('20-05-2008 16:31:01', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5010, 'doc_container', to_date('20-05-2008 16:31:01', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5011, 'calendar', to_date('20-05-2008 16:31:41', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5, 'enterprise', to_date('20-05-2008 16:34:10', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5020, 'group', to_date('20-05-2008 16:34:11', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5021, 'group', to_date('20-05-2008 16:34:11', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5022, 'group', to_date('20-05-2008 16:34:11', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (5023, 'group', to_date('20-05-2008 16:34:11', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (26, 'form', to_date('20-05-2008 16:34:53', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (30, 'form', to_date('20-05-2008 16:34:53', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
insert into PN_OBJECT (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
values (10, 'resource', to_date('20-05-2008 16:36:04', 'dd-mm-yyyy hh24:mi:ss'), 1, 'A')
/
commit
/
-- prompt  40 records loaded
-- prompt  Loading PN_PERSON...
insert into PN_PERSON (PERSON_ID, EMAIL, FIRST_NAME, LAST_NAME, DISPLAY_NAME, USER_STATUS, MEMBERSHIP_PORTFOLIO_ID, RECORD_STATUS, CREATED_DATE)
values (1, 'admin@project.net', 'Application', 'Administrator', 'Application Administrator', 'Active', 3, 'A', null)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_ADDRESS...
insert into PN_ADDRESS (ADDRESS_ID, ADDRESS1, ADDRESS2, ADDRESS3, ADDRESS4, ADDRESS5, ADDRESS6, ADDRESS7, CITY, CITY_DISTRICT, REGION, STATE_PROVENCE, COUNTRY_CODE, ZIPCODE, OFFICE_PHONE, FAX_PHONE, HOME_PHONE, MOBILE_PHONE, PAGER_PHONE, PAGER_EMAIL, WEBSITE_URL, RECORD_STATUS)
values (2, '3760 Convoy Street', 'Suite 340', null, null, null, null, null, 'San Diego', null, null, 'CA', 'US', '92117', '858.496.8860', null, null, null, null, null, null, 'A')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_APPLICATION_SPACE...
insert into PN_APPLICATION_SPACE (APPLICATION_ID, APPLICATION_NAME, APPLICATION_DESC, CREATED_BY_ID, CREATED_DATETIME, CRC, RECORD_STATUS)
values (4, 'Application Administration', 'Application Administration Space', 1, to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_AUTHENTICATOR_TYPE...
insert into PN_AUTHENTICATOR_TYPE (TYPE, DESCRIPTION, RECORD_STATUS)
values ('native', 'PRMServer Native Authenticator.  Uses the PRM schema (db) as the credential store.', 'A')
/
insert into PN_AUTHENTICATOR_TYPE (TYPE, DESCRIPTION, RECORD_STATUS)
values ('ldap', 'LDAP Authenticator.  Uses an LDAP Directory Service as a users profile and credential store.', 'A')
/
commit
/
-- prompt  2 records loaded
-- prompt  Loading PN_AUTHENTICATOR...
insert into PN_AUTHENTICATOR (AUTHENTICATOR_ID, AUTHENTICATOR_TYPE, NAME, DESCRIPTION, RECORD_STATUS)
values (1, 'native', 'Default Native Authentication Provider', 'Project.net default authentication provider.  This provider uses the PRM schema as is credential store.', 'A')
/
insert into PN_AUTHENTICATOR (AUTHENTICATOR_ID, AUTHENTICATOR_TYPE, NAME, DESCRIPTION, RECORD_STATUS)
values (2, 'ldap', 'Default LDAP Authenication Provider', 'Default implementation of an LDAP Authentication Provider.', 'A')
/
commit
/
-- prompt  2 records loaded
-- prompt  Loading PN_LANGUAGE...
insert into PN_LANGUAGE (LANGUAGE_CODE, LANGUAGE_NAME, CHARACTER_SET, IS_ACTIVE)
values ('en', '@prm.resource.language.english.name', 'ISO8859_1', 1)
/
insert into PN_LANGUAGE (LANGUAGE_CODE, LANGUAGE_NAME, CHARACTER_SET, IS_ACTIVE)
values ('es', '@prm.resource.language.spanish.name', 'ISO8859_1', 1)
/
insert into PN_LANGUAGE (LANGUAGE_CODE, LANGUAGE_NAME, CHARACTER_SET, IS_ACTIVE)
values ('fr', '@prm.resource.language.french.name', 'ISO8859_1', 1)
/
insert into PN_LANGUAGE (LANGUAGE_CODE, LANGUAGE_NAME, CHARACTER_SET, IS_ACTIVE)
values ('ja', '@prm.resource.language.japanese.name', 'Shift_JIS', 0)
/
insert into PN_LANGUAGE (LANGUAGE_CODE, LANGUAGE_NAME, CHARACTER_SET, IS_ACTIVE)
values ('de', '@prm.resource.language.german.name', 'ISO8859_1', 1)
/
commit
/
-- prompt  5 records loaded
-- prompt  Loading PN_BRAND...
insert into PN_BRAND (BRAND_ID, BRAND_ABBRV, BRAND_NAME, BRAND_DESC, DEFAULT_LANGUAGE, IS_SYSTEM_DEFAULT, RECORD_STATUS)
values (2000, 'pnet', 'System Default', 'System Default Brand', 'en', 1, 'A')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_BRAND_SUPPORTS_LANGUAGE...
insert into PN_BRAND_SUPPORTS_LANGUAGE (BRAND_ID, LANGUAGE_CODE)
values (2000, 'en')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_BUSINESS_CATEGORY...
insert into PN_BUSINESS_CATEGORY (BUSINESS_CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION)
values (10, 'owner', 'Owner')
/
insert into PN_BUSINESS_CATEGORY (BUSINESS_CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION)
values (20, 'manufacturer', 'Manufacturer')
/
insert into PN_BUSINESS_CATEGORY (BUSINESS_CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION)
values (30, 'manufacturer_rep', 'Manufacturer Rep')
/
insert into PN_BUSINESS_CATEGORY (BUSINESS_CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION)
values (40, 'distributor', 'Distributor')
/
insert into PN_BUSINESS_CATEGORY (BUSINESS_CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION)
values (50, 'engineering_firm', 'Engineering Firm')
/
insert into PN_BUSINESS_CATEGORY (BUSINESS_CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION)
values (60, 'general_contractor', 'General Contractor')
/
insert into PN_BUSINESS_CATEGORY (BUSINESS_CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION)
values (70, 'construction_manager', 'Construction Manager')
/
insert into PN_BUSINESS_CATEGORY (BUSINESS_CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION)
values (80, 'subcontractor', 'Subcontractor')
/
insert into PN_BUSINESS_CATEGORY (BUSINESS_CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION)
values (90, 'general_government', 'General Government')
/
commit
/
-- prompt  9 records loaded
-- prompt  Loading PN_CALENDAR...
insert into PN_CALENDAR (CALENDAR_ID, CALENDAR_NAME, CALENDAR_DESCRIPTION, IS_BASE_CALENDAR, RECORD_STATUS)
values (5011, 'Application Administrator''s Personal Calendar', 'Application Administrator''s Personal ' || chr(9) || '' || chr(9) || '' || chr(9) || 'Calendar', 1, 'A')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_CATEGORY...
insert into PN_CATEGORY (CATEGORY_ID, NAME, DESCRIPTION)
values (100, 'Banking', 'Banking Category')
/
insert into PN_CATEGORY (CATEGORY_ID, NAME, DESCRIPTION)
values (101, 'Trading', 'Trading')
/
insert into PN_CATEGORY (CATEGORY_ID, NAME, DESCRIPTION)
values (200, 'Systems Integration', 'System Integration')
/
insert into PN_CATEGORY (CATEGORY_ID, NAME, DESCRIPTION)
values (201, 'Software Development', 'Software Development')
/
insert into PN_CATEGORY (CATEGORY_ID, NAME, DESCRIPTION)
values (202, 'eBusiness Implementation', 'ebusiness Implementation')
/
commit
/
-- prompt  5 records loaded
-- prompt  Loading PN_CLASS_TYPE...
insert into PN_CLASS_TYPE (CLASS_TYPE_ID, CLASS_TYPE_NAME, CLASS_TYPE_DESC)
values (1, 'element form', 'A system element form class')
/
insert into PN_CLASS_TYPE (CLASS_TYPE_ID, CLASS_TYPE_NAME, CLASS_TYPE_DESC)
values (100, 'form', 'A form class')
/
insert into PN_CLASS_TYPE (CLASS_TYPE_ID, CLASS_TYPE_NAME, CLASS_TYPE_DESC)
values (200, 'checklist', 'A checklist class')
/
insert into PN_CLASS_TYPE (CLASS_TYPE_ID, CLASS_TYPE_NAME, CLASS_TYPE_DESC)
values (300, 'survey', 'A survey class')
/
insert into PN_CLASS_TYPE (CLASS_TYPE_ID, CLASS_TYPE_NAME, CLASS_TYPE_DESC)
values (400, 'property_sheet', 'A non-versioned property sheet')
/
insert into PN_CLASS_TYPE (CLASS_TYPE_ID, CLASS_TYPE_NAME, CLASS_TYPE_DESC)
values (401, 'versioned_property_sheet', 'A versioned property sheet')
/
commit
/
-- prompt  6 records loaded
-- prompt  Loading PN_CLASS...
insert into PN_CLASS (CLASS_ID, CLASS_NAME, CLASS_DESC, CLASS_ABBREVIATION, CLASS_TYPE_ID, OWNER_SPACE_ID, METHODOLOGY_ID, MAX_ROW, MAX_COLUMN, NEXT_DATA_SEQ, DATA_TABLE_SEQ, MASTER_TABLE_NAME, DATA_TABLE_KEY, IS_SEQUENCED, IS_SYSTEM_CLASS, CRC, RECORD_STATUS, SUPPORTS_DISCUSSION_GROUP, SUPPORTS_DOCUMENT_VAULT)
values (21, 'text element', 'text element property sheet', null, 1, 1, 1, 0, 0, 1, 1, null, null, 1, 0, null, 'A', 0, 0)
/
insert into PN_CLASS (CLASS_ID, CLASS_NAME, CLASS_DESC, CLASS_ABBREVIATION, CLASS_TYPE_ID, OWNER_SPACE_ID, METHODOLOGY_ID, MAX_ROW, MAX_COLUMN, NEXT_DATA_SEQ, DATA_TABLE_SEQ, MASTER_TABLE_NAME, DATA_TABLE_KEY, IS_SEQUENCED, IS_SYSTEM_CLASS, CRC, RECORD_STATUS, SUPPORTS_DISCUSSION_GROUP, SUPPORTS_DOCUMENT_VAULT)
values (22, 'textarea element', 'textarea element property sheet', null, 1, 1, 1, 0, 0, 1, 1, null, null, 1, 0, null, 'A', 0, 0)
/
insert into PN_CLASS (CLASS_ID, CLASS_NAME, CLASS_DESC, CLASS_ABBREVIATION, CLASS_TYPE_ID, OWNER_SPACE_ID, METHODOLOGY_ID, MAX_ROW, MAX_COLUMN, NEXT_DATA_SEQ, DATA_TABLE_SEQ, MASTER_TABLE_NAME, DATA_TABLE_KEY, IS_SEQUENCED, IS_SYSTEM_CLASS, CRC, RECORD_STATUS, SUPPORTS_DISCUSSION_GROUP, SUPPORTS_DOCUMENT_VAULT)
values (23, 'select menu element', 'menu element property sheet', null, 1, 1, 1, 0, 0, 1, 1, null, null, 1, 0, null, 'A', 0, 0)
/
insert into PN_CLASS (CLASS_ID, CLASS_NAME, CLASS_DESC, CLASS_ABBREVIATION, CLASS_TYPE_ID, OWNER_SPACE_ID, METHODOLOGY_ID, MAX_ROW, MAX_COLUMN, NEXT_DATA_SEQ, DATA_TABLE_SEQ, MASTER_TABLE_NAME, DATA_TABLE_KEY, IS_SEQUENCED, IS_SYSTEM_CLASS, CRC, RECORD_STATUS, SUPPORTS_DISCUSSION_GROUP, SUPPORTS_DOCUMENT_VAULT)
values (24, 'date element', 'date element property sheet', null, 1, 1, 1, 0, 0, 1, 1, null, null, 1, 0, null, 'A', 0, 0)
/
insert into PN_CLASS (CLASS_ID, CLASS_NAME, CLASS_DESC, CLASS_ABBREVIATION, CLASS_TYPE_ID, OWNER_SPACE_ID, METHODOLOGY_ID, MAX_ROW, MAX_COLUMN, NEXT_DATA_SEQ, DATA_TABLE_SEQ, MASTER_TABLE_NAME, DATA_TABLE_KEY, IS_SEQUENCED, IS_SYSTEM_CLASS, CRC, RECORD_STATUS, SUPPORTS_DISCUSSION_GROUP, SUPPORTS_DOCUMENT_VAULT)
values (25, 'checkbox element', 'checkbox element property sheet', null, 1, 1, 1, 0, 0, 1, 1, null, null, 1, 0, null, 'A', 0, 0)
/
insert into PN_CLASS (CLASS_ID, CLASS_NAME, CLASS_DESC, CLASS_ABBREVIATION, CLASS_TYPE_ID, OWNER_SPACE_ID, METHODOLOGY_ID, MAX_ROW, MAX_COLUMN, NEXT_DATA_SEQ, DATA_TABLE_SEQ, MASTER_TABLE_NAME, DATA_TABLE_KEY, IS_SEQUENCED, IS_SYSTEM_CLASS, CRC, RECORD_STATUS, SUPPORTS_DISCUSSION_GROUP, SUPPORTS_DOCUMENT_VAULT)
values (29, 'single person select menu element', 'single person select menu element property sheet', null, 1, 1, 1, 0, 0, 1, 1, null, null, 1, 0, null, 'A', 0, 0)
/
insert into PN_CLASS (CLASS_ID, CLASS_NAME, CLASS_DESC, CLASS_ABBREVIATION, CLASS_TYPE_ID, OWNER_SPACE_ID, METHODOLOGY_ID, MAX_ROW, MAX_COLUMN, NEXT_DATA_SEQ, DATA_TABLE_SEQ, MASTER_TABLE_NAME, DATA_TABLE_KEY, IS_SEQUENCED, IS_SYSTEM_CLASS, CRC, RECORD_STATUS, SUPPORTS_DISCUSSION_GROUP, SUPPORTS_DOCUMENT_VAULT)
values (31, 'milestone select element', 'milestone select menu element property sheet', null, 1, 1, 1, 0, 0, 1, 1, null, null, 1, 0, null, 'A', 0, 0)
/
insert into PN_CLASS (CLASS_ID, CLASS_NAME, CLASS_DESC, CLASS_ABBREVIATION, CLASS_TYPE_ID, OWNER_SPACE_ID, METHODOLOGY_ID, MAX_ROW, MAX_COLUMN, NEXT_DATA_SEQ, DATA_TABLE_SEQ, MASTER_TABLE_NAME, DATA_TABLE_KEY, IS_SEQUENCED, IS_SYSTEM_CLASS, CRC, RECORD_STATUS, SUPPORTS_DISCUSSION_GROUP, SUPPORTS_DOCUMENT_VAULT)
values (40, 'horizontal separator', 'horizontal separator field property sheet', null, 1, 1, 1, 0, 0, 1, 1, null, null, 1, 0, null, 'A', 0, 0)
/
insert into PN_CLASS (CLASS_ID, CLASS_NAME, CLASS_DESC, CLASS_ABBREVIATION, CLASS_TYPE_ID, OWNER_SPACE_ID, METHODOLOGY_ID, MAX_ROW, MAX_COLUMN, NEXT_DATA_SEQ, DATA_TABLE_SEQ, MASTER_TABLE_NAME, DATA_TABLE_KEY, IS_SEQUENCED, IS_SYSTEM_CLASS, CRC, RECORD_STATUS, SUPPORTS_DISCUSSION_GROUP, SUPPORTS_DOCUMENT_VAULT)
values (41, 'instruction', 'instruction text field property sheet', null, 1, 1, 1, 0, 0, 1, 1, null, null, 1, 0, null, 'A', 0, 0)
/
insert into PN_CLASS (CLASS_ID, CLASS_NAME, CLASS_DESC, CLASS_ABBREVIATION, CLASS_TYPE_ID, OWNER_SPACE_ID, METHODOLOGY_ID, MAX_ROW, MAX_COLUMN, NEXT_DATA_SEQ, DATA_TABLE_SEQ, MASTER_TABLE_NAME, DATA_TABLE_KEY, IS_SEQUENCED, IS_SYSTEM_CLASS, CRC, RECORD_STATUS, SUPPORTS_DISCUSSION_GROUP, SUPPORTS_DOCUMENT_VAULT)
values (32, 'number element', 'number element property sheet', null, 1, 1, 1, 0, 0, 1, 1, null, null, 1, 0, null, 'A', 0, 0)
/
insert into PN_CLASS (CLASS_ID, CLASS_NAME, CLASS_DESC, CLASS_ABBREVIATION, CLASS_TYPE_ID, OWNER_SPACE_ID, METHODOLOGY_ID, MAX_ROW, MAX_COLUMN, NEXT_DATA_SEQ, DATA_TABLE_SEQ, MASTER_TABLE_NAME, DATA_TABLE_KEY, IS_SEQUENCED, IS_SYSTEM_CLASS, CRC, RECORD_STATUS, SUPPORTS_DISCUSSION_GROUP, SUPPORTS_DOCUMENT_VAULT)
values (33, 'currency element', 'currency element property sheet', null, 1, 1, 1, 0, 0, 1, 1, null, null, 1, 0, null, 'A', 0, 0)
/
commit
/
-- prompt  11 records loaded
-- prompt  Loading PN_CLASS_DOMAIN...
insert into PN_CLASS_DOMAIN (DOMAIN_ID, DOMAIN_NAME, DOMAIN_TYPE, DOMAIN_DESC, RECORD_STATUS)
values (200, 'Column Selection', 'system', 'Column span selection', 'A')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_CLASS_DOMAIN_VALUES...
insert into PN_CLASS_DOMAIN_VALUES (DOMAIN_ID, DOMAIN_VALUE_ID, DOMAIN_VALUE_NAME, DOMAIN_VALUE_SEQ, DOMAIN_VALUE_DESC, IS_DEFAULT, SOURCE_VALUE_ID, RECORD_STATUS)
values (200, 400, '@prm.global.form.elementproperty.column.domain.left', 1, 'Left column only', 1, null, 'A')
/
insert into PN_CLASS_DOMAIN_VALUES (DOMAIN_ID, DOMAIN_VALUE_ID, DOMAIN_VALUE_NAME, DOMAIN_VALUE_SEQ, DOMAIN_VALUE_DESC, IS_DEFAULT, SOURCE_VALUE_ID, RECORD_STATUS)
values (200, 401, '@prm.global.form.elementproperty.column.domain.right', 2, 'Right column only', 0, null, 'A')
/
insert into PN_CLASS_DOMAIN_VALUES (DOMAIN_ID, DOMAIN_VALUE_ID, DOMAIN_VALUE_NAME, DOMAIN_VALUE_SEQ, DOMAIN_VALUE_DESC, IS_DEFAULT, SOURCE_VALUE_ID, RECORD_STATUS)
values (200, 402, '@prm.global.form.elementproperty.column.domain.both', 3, 'Spanning both the left and right columns', 0, null, 'A')
/
commit
/
-- prompt  3 records loaded
-- prompt  Loading PN_CLIENT_TYPE...
insert into PN_CLIENT_TYPE (CLIENT_TYPE_ID, CLIENT_NAME, CLIENT_DESC)
values (100, 'html', 'HTML 3.0 complient browsers with JavaScript 1.2 support')
/
insert into PN_CLIENT_TYPE (CLIENT_TYPE_ID, CLIENT_NAME, CLIENT_DESC)
values (200, 'java', 'Java 1.1 complient browser')
/
insert into PN_CLIENT_TYPE (CLIENT_TYPE_ID, CLIENT_NAME, CLIENT_DESC)
values (300, 'xml', 'xml client')
/
commit
/
-- prompt  3 records loaded
-- prompt  Loading PN_CLASS_FIELD_PROPERTY...
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 21, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 21, 'size', 100, 'in_tag', '20')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 21, 'maxlength', 100, 'in_tag', '80')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 22, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 22, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 22, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 23, 'tag', 100, 'tag', 'select')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 24, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 24, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 24, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 25, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 25, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 25, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 21, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 21, 'size', 100, 'in_tag', '20')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 21, 'maxlength', 100, 'in_tag', '80')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 22, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 22, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 22, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 23, 'tag', 100, 'tag', 'select')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 24, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 24, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 24, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 25, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 25, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 25, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (23, 21, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (23, 21, 'size', 100, 'in_tag', '20')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (23, 21, 'maxlength', 100, 'in_tag', '80')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (23, 22, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (23, 22, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (23, 22, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (23, 23, 'tag', 100, 'tag', 'select')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (24, 21, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (24, 21, 'size', 100, 'in_tag', '20')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (24, 21, 'maxlength', 100, 'in_tag', '80')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (24, 22, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (24, 22, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (24, 22, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (24, 23, 'tag', 100, 'tag', 'select')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (25, 21, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (25, 21, 'size', 100, 'in_tag', '20')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (25, 21, 'maxlength', 100, 'in_tag', '80')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (25, 22, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (25, 22, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (25, 22, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (25, 23, 'tag', 100, 'tag', 'select')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (29, 21, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (29, 21, 'size', 100, 'in_tag', '20')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (29, 21, 'maxlength', 100, 'in_tag', '80')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (29, 22, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (29, 22, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (29, 22, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (29, 23, 'tag', 100, 'tag', 'select')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (31, 21, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (31, 21, 'size', 100, 'in_tag', '20')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (31, 21, 'maxlength', 100, 'in_tag', '80')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (31, 22, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (31, 22, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (31, 22, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (31, 23, 'tag', 100, 'tag', 'select')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (40, 21, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (40, 21, 'size', 100, 'in_tag', '20')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (40, 21, 'maxlength', 100, 'in_tag', '80')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (40, 22, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (40, 22, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (40, 22, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (40, 23, 'tag', 100, 'tag', 'select')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (41, 21, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (41, 21, 'size', 100, 'in_tag', '20')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (41, 21, 'maxlength', 100, 'in_tag', '80')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (41, 22, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (41, 22, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (41, 22, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (41, 23, 'tag', 100, 'tag', 'select')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 21, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 21, 'size', 100, 'in_tag', '20')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 21, 'maxlength', 100, 'in_tag', '80')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 22, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 22, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 22, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 23, 'tag', 100, 'tag', 'select')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 24, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 24, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 24, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 25, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 25, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 25, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 29, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 29, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 29, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 21, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 21, 'size', 100, 'in_tag', '20')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 21, 'maxlength', 100, 'in_tag', '80')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 22, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 22, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 22, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 23, 'tag', 100, 'tag', 'select')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 24, 'tag', 100, 'tag', 'input type=text')
/
commit
/
-- prompt  100 records committed...
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 24, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 24, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 25, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 25, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 25, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 29, 'tag', 100, 'tag', 'input type=text')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 29, 'size', 100, 'in_tag', '4')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 29, 'maxlength', 100, 'in_tag', '3')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (21, 26, 'tag', 100, 'tag', 'input type=checkbox')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (22, 26, 'tag', 100, 'tag', 'input type=checkbox')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (23, 25, 'tag', 100, 'tag', 'input type=checkbox')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (29, 26, 'tag', 100, 'tag', 'input type=checkbox')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (31, 25, 'tag', 100, 'tag', 'input type=checkbox')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (32, 30, 'tag', 100, 'tag', 'input type=checkbox')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (33, 30, 'tag', 100, 'tag', 'input type=checkbox')
/
insert into PN_CLASS_FIELD_PROPERTY (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID, PROPERTY_TYPE, VALUE)
values (24, 26, 'tag', 100, 'tag', 'input type=checkbox')
/
commit
/
-- prompt  115 records loaded
-- prompt  Loading PN_ELEMENT...
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (1, 'text', 'One-line text field', null, '@prm.global.form.element.text.label', 'VARCHAR2', 'A')
/
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (2, 'textarea', 'Scrolled text box', null, '@prm.global.form.element.textarea.label', 'VARCHAR2', 'A')
/
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (3, 'selection_menu', 'selection menu', null, '@prm.global.form.element.selectionmenu.label', 'NUMBER', 'A')
/
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (4, 'date', 'Date/Time Field', null, '@prm.global.form.element.date.label', 'DATE', 'A')
/
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (5, 'checkbox (yes, no)', 'check box field (true or false)', null, '@prm.global.form.element.checkbox.label', 'NUMBER', 'A')
/
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (9, 'person_selection', 'menu for seleciton of people', null, '@prm.global.form.element.personselection.label', 'NUMBER', 'A')
/
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (11, 'milestone', 'menu for selection of milestone', null, '@prm.global.form.element.milestone.label', 'NUMBER', 'A')
/
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (20, 'horizontal separator', 'horizontal form separator with title text', null, '@prm.global.form.element.horizontalseparator.label', null, 'A')
/
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (21, 'instruction', 'static instruction text', null, '@prm.global.form.element.instruction.label', null, 'A')
/
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (12, 'number', 'A number', null, '@prm.global.form.element.number.label', 'NUMBER', 'A')
/
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (13, 'currency', 'A currency value', null, '@prm.global.form.element.currency.label', 'NUMBER', 'A')
/
insert into PN_ELEMENT (ELEMENT_ID, ELEMENT_NAME, ELEMENT_DESC, ELEMENT_TYPE, ELEMENT_LABEL, DB_FIELD_DATATYPE, RECORD_STATUS)
values (100, 'calculation_field', 'A Calculated Field', null, '@prm.global.form.element.calculation.label', 'VARCHAR2', 'A')
/
commit
/
-- prompt  12 records loaded
-- prompt  Loading PN_CLASS_TYPE_ELEMENT...
insert into PN_CLASS_TYPE_ELEMENT (CLASS_TYPE_ID, ELEMENT_ID, RECORD_STATUS)
values (1, 1, 'A')
/
insert into PN_CLASS_TYPE_ELEMENT (CLASS_TYPE_ID, ELEMENT_ID, RECORD_STATUS)
values (1, 2, 'A')
/
insert into PN_CLASS_TYPE_ELEMENT (CLASS_TYPE_ID, ELEMENT_ID, RECORD_STATUS)
values (1, 3, 'A')
/
insert into PN_CLASS_TYPE_ELEMENT (CLASS_TYPE_ID, ELEMENT_ID, RECORD_STATUS)
values (1, 4, 'A')
/
insert into PN_CLASS_TYPE_ELEMENT (CLASS_TYPE_ID, ELEMENT_ID, RECORD_STATUS)
values (1, 5, 'A')
/
insert into PN_CLASS_TYPE_ELEMENT (CLASS_TYPE_ID, ELEMENT_ID, RECORD_STATUS)
values (1, 9, 'A')
/
insert into PN_CLASS_TYPE_ELEMENT (CLASS_TYPE_ID, ELEMENT_ID, RECORD_STATUS)
values (1, 11, 'A')
/
insert into PN_CLASS_TYPE_ELEMENT (CLASS_TYPE_ID, ELEMENT_ID, RECORD_STATUS)
values (1, 20, 'A')
/
insert into PN_CLASS_TYPE_ELEMENT (CLASS_TYPE_ID, ELEMENT_ID, RECORD_STATUS)
values (1, 21, 'A')
/
insert into PN_CLASS_TYPE_ELEMENT (CLASS_TYPE_ID, ELEMENT_ID, RECORD_STATUS)
values (1, 12, 'A')
/
insert into PN_CLASS_TYPE_ELEMENT (CLASS_TYPE_ID, ELEMENT_ID, RECORD_STATUS)
values (1, 13, 'A')
/
commit
/
-- prompt  11 records loaded
-- prompt  Loading PN_CODE_TYPE...
insert into PN_CODE_TYPE (CODE_TYPE_ID, CODE_TYPE_NAME, DESCRIPTION)
values (100, 'allocation_units', 'Resource allocation units')
/
insert into PN_CODE_TYPE (CODE_TYPE_ID, CODE_TYPE_NAME, DESCRIPTION)
values (110, 'allocation_cell', 'Resource allocation entry cell type')
/
insert into PN_CODE_TYPE (CODE_TYPE_ID, CODE_TYPE_NAME, DESCRIPTION)
values (200, 'month', 'calendar months')
/
insert into PN_CODE_TYPE (CODE_TYPE_ID, CODE_TYPE_NAME, DESCRIPTION)
values (300, 'month_abbr', 'abbreviated calendar months')
/
insert into PN_CODE_TYPE (CODE_TYPE_ID, CODE_TYPE_NAME, DESCRIPTION)
values (400, 'task_units', 'Units for task times.')
/
commit
/
-- prompt  5 records loaded
-- prompt  Loading PN_CONFIGURATION_SPACE...
insert into PN_CONFIGURATION_SPACE (CONFIGURATION_ID, CONFIGURATION_NAME, CONFIGURATION_DESC, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS, BRAND_ID)
values (5004, 'Project.net Configuration', 'Project.net default configuration', 1, to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 'A', 2000)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_DATE_FORMAT...
insert into PN_DATE_FORMAT (DATE_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (100, 'M/d/yyyy', 'MM/DD/YYYY', '01/10/2001')
/
insert into PN_DATE_FORMAT (DATE_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (200, 'd/M/yyyy', 'DD/MM/YYYY', '10/01/2001')
/
insert into PN_DATE_FORMAT (DATE_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (300, 'M-d-yyyy', 'MM-DD-YYYY', '01-10-2001')
/
insert into PN_DATE_FORMAT (DATE_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (400, 'd-M-yyyy', 'DD-MM-YYYY', '10-01-2001')
/
insert into PN_DATE_FORMAT (DATE_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (500, 'M.d.yyyy', 'MM.DD.YYYY', '01.10.2001')
/
insert into PN_DATE_FORMAT (DATE_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (600, 'd.M.yyyy', 'DD.MM.YYYY', '10.01.2001')
/
insert into PN_DATE_FORMAT (DATE_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (700, 'yyyy/M/d', 'YYYY/MM/DD', '2001/01/10')
/
insert into PN_DATE_FORMAT (DATE_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (800, 'yyyy.M.d', 'YYYY.MM.DD', '2001.01.10')
/
insert into PN_DATE_FORMAT (DATE_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (900, 'yyyy/d/M', 'YYYY/DD/MM', '2001/10/01')
/
insert into PN_DATE_FORMAT (DATE_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (1000, 'yyyy.d.M', 'YYYY.DD.MM', '2001.10.01')
/
commit
/
-- prompt  10 records loaded
-- prompt  Loading PN_GROUP_TYPE...
insert into PN_GROUP_TYPE (GROUP_TYPE_ID, CLASS_NAME)
values (100, 'net.project.security.group.UserDefinedGroup')
/
insert into PN_GROUP_TYPE (GROUP_TYPE_ID, CLASS_NAME)
values (200, 'net.project.security.group.SpaceAdministratorGroup')
/
insert into PN_GROUP_TYPE (GROUP_TYPE_ID, CLASS_NAME)
values (300, 'net.project.security.group.TeamMemberGroup')
/
insert into PN_GROUP_TYPE (GROUP_TYPE_ID, CLASS_NAME)
values (400, 'net.project.security.group.PrincipalGroup')
/
insert into PN_GROUP_TYPE (GROUP_TYPE_ID, CLASS_NAME)
values (500, 'net.project.security.group.PowerUserGroup')
/
insert into PN_GROUP_TYPE (GROUP_TYPE_ID, CLASS_NAME)
values (600, 'net.project.security.group.EveryoneGroup')
/
commit
/
-- prompt  6 records loaded
-- prompt  Loading PN_GROUP...
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (5000, '@prm.security.group.type.spaceadministrator.name', '@prm.application.security.group.type.spaceadmin.description', 0, 1, 'A', 200, null)
/
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (5001, '@prm.security.group.type.principal.name', '@prm.security.group.type.principal.description', 1, 1, 'A', 400, 1)
/
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (5002, '@prm.security.group.type.teammember.name', '@prm.security.group.type.teammember.description', 0, 1, 'A', 300, null)
/
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (5005, '@prm.security.group.type.spaceadministrator.name', '@prm.configuration.security.group.type.spaceadmin.description', 0, 1, 'A', 200, null)
/
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (5006, '@prm.security.group.type.principal.name', '@prm.security.group.type.principal.description', 1, 1, 'A', 400, 1)
/
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (5007, '@prm.security.group.type.teammember.name', '@prm.security.group.type.teammember.description', 0, 1, 'A', 300, null)
/
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (100, '@prm.workflow.group.envelopecreator.name', '@prm.workflow.group.envelopecreator.description', 0, 0, 'A', 100, null)
/
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (5008, '@prm.security.group.type.spaceadministrator.name', 'Personal Space Admin', 0, 1, 'A', 200, null)
/
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (5020, '@prm.security.group.type.spaceadministrator.name', '@prm.project.security.group.type.spaceadmin.description', 0, 1, 'A', 200, null)
/
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (5021, '@prm.security.group.type.poweruser.name', '@prm.security.group.type.poweruser.description', 0, 1, 'A', 500, null)
/
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (5022, '@prm.security.group.type.principal.name', '@prm.security.group.type.principal.decription', 1, 1, 'A', 400, 1)
/
insert into PN_GROUP (GROUP_ID, GROUP_NAME, GROUP_DESC, IS_PRINCIPAL, IS_SYSTEM_GROUP, RECORD_STATUS, GROUP_TYPE_ID, PRINCIPAL_OWNER_ID)
values (5023, '@prm.security.group.type.teammember.name', '@prm.security.group.type.teammember.description', 0, 1, 'A', 300, null)
/
commit
/
-- prompt  12 records loaded
-- prompt  Loading PN_DEFAULT_OBJECT_PERMISSION...
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'post', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'process', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'gate', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'phase', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'project', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'methodology', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_step', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_transition', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_rule', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_envelope', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_envelope_history', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_envelope_version', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'subscription', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'scheduled_subscription', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'brand', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'news', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'application', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'configuration', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'bookmark', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'user_domain', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'license', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'baseline', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'enterprise', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'address', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'agenda_item', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'business', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'calendar', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_domain', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_domain_value', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_field', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_list', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_data', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_data_version', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_filter_value', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'deliverable', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'discussion_group', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'doc_container', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'doc_space', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'document', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'document_version', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'domain_value', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'event', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'facility', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'group', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'meeting', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'person', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'plan', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'task', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'portfolio', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'post', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'process', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'gate', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'phase', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'project', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'methodology', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_step', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_transition', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_rule', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_envelope', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_envelope_history', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_envelope_version', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'subscription', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'scheduled_subscription', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'brand', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'news', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'application', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'configuration', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'bookmark', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'user_domain', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'license', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'baseline', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'enterprise', 5021, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'address', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'agenda_item', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'business', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'calendar', 5023, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_domain', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_domain_value', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_field', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_list', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_data', 5023, 15)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_data_version', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_filter_value', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'deliverable', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'discussion_group', 5023, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'doc_container', 5023, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'doc_space', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'document', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'document_version', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'domain_value', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'event', 5023, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'facility', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'group', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'meeting', 5023, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'person', 5023, 1)
/
commit
/
-- prompt  100 records committed...
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'plan', 5023, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'task', 5023, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'portfolio', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'post', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'process', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'gate', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'phase', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'project', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'methodology', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_step', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_transition', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_rule', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_envelope', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_envelope_history', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'workflow_envelope_version', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'subscription', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'scheduled_subscription', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'brand', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'news', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'application', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'configuration', 5023, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'bookmark', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'user_domain', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'license', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'baseline', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'enterprise', 5023, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'timesheet', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'address', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'agenda_item', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'business', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'calendar', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_domain', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_domain_value', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_field', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_list', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_data', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_data_version', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_filter_value', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'deliverable', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'discussion_group', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'doc_container', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'doc_space', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'document', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'document_version', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'domain_value', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'event', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'facility', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'group', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'meeting', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'person', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'plan', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'task', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'portfolio', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'post', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'process', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'gate', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'phase', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'project', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'methodology', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_step', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_transition', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_rule', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_envelope', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_envelope_history', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_envelope_version', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'subscription', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'scheduled_subscription', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'brand', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'news', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'application', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'configuration', 5000, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'address', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'agenda_item', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'business', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'calendar', 5002, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_domain', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_domain_value', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_field', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_list', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_data', 5002, 15)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_data_version', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'form_filter_value', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'deliverable', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'discussion_group', 5002, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'doc_container', 5002, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'doc_space', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'document', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'document_version', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'domain_value', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'event', 5002, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'facility', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'group', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'meeting', 5002, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'person', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'plan', 5002, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'task', 5002, 5)
/
commit
/
-- prompt  200 records committed...
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'portfolio', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'post', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'process', 5002, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'gate', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'phase', 5002, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'project', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'methodology', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_step', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_transition', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_rule', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_envelope', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_envelope_history', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'workflow_envelope_version', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'subscription', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'scheduled_subscription', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'brand', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'news', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'application', 5002, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (4, 'configuration', 5002, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'address', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'agenda_item', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'business', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'calendar', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_domain', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_domain_value', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_field', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_list', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_data', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_data_version', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_filter_value', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'deliverable', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'discussion_group', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'doc_container', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'doc_space', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'document', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'document_version', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'domain_value', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'event', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'facility', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'group', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'meeting', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'person', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'plan', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'task', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'portfolio', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'post', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'process', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'gate', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'phase', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'project', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'methodology', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_step', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_transition', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_rule', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_envelope', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_envelope_history', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_envelope_version', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'subscription', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'scheduled_subscription', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'brand', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'news', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'application', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'configuration', 5005, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'address', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'agenda_item', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'business', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'calendar', 5007, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_domain', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_domain_value', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_field', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_list', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_data', 5007, 15)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_data_version', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'form_filter_value', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'deliverable', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'discussion_group', 5007, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'doc_container', 5007, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'doc_space', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'document', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'document_version', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'domain_value', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'event', 5007, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'facility', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'group', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'meeting', 5007, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'person', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'plan', 5007, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'task', 5007, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'portfolio', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'post', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'process', 5007, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'gate', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'phase', 5007, 5)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'project', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'methodology', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow', 5007, 1)
/
commit
/
-- prompt  300 records committed...
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_step', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_transition', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_rule', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_envelope', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_envelope_history', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'workflow_envelope_version', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'subscription', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'scheduled_subscription', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'brand', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'news', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'application', 5007, 1)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5004, 'configuration', 5007, 7)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'address', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'agenda_item', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'business', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'calendar', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'form', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'form_domain', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'form_domain_value', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'form_field', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'form_list', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'form_data', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'form_data_version', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'form_filter_value', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'deliverable', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'discussion_group', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'doc_container', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'doc_space', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'document', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'document_version', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'domain_value', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'event', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'facility', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'group', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'meeting', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'person', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'plan', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'task', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'portfolio', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'post', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'process', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'gate', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'phase', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'project', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'methodology', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'workflow', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'workflow_step', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'workflow_transition', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'workflow_rule', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'workflow_envelope', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'workflow_envelope_history', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'workflow_envelope_version', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'subscription', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'scheduled_subscription', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'brand', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'news', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'application', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'configuration', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'bookmark', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'user_domain', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (1, 'license', 5008, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'address', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'agenda_item', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'business', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'calendar', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_domain', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_domain_value', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_field', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_list', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_data', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_data_version', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'form_filter_value', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'deliverable', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'discussion_group', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'doc_container', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'doc_space', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'document', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'document_version', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'domain_value', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'event', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'facility', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'group', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'meeting', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'person', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'plan', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'task', 5020, 65535)
/
insert into PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE, GROUP_ID, ACTIONS)
values (5, 'portfolio', 5020, 65535)
/
commit
/
-- prompt  387 records loaded
-- prompt  Loading PN_DIRECTORY...
insert into PN_DIRECTORY (DIRECTORY_ID, DIRECTORY_NAME, DIRECTORY_DESC, DISPLAY_CLASS_ID, DIRECTORY_TYPE_ID, DIRECTORY_HOST, DIRECTORY_VENDOR, BIND_USERNAME, DIRECTORY_URL, BIND_PASSWORD, SEARCH_ROOT, DIRECTORY_PORT, DIRECTORY_SECURE_PORT, DIRECTORY_CIPHER_KEY, CONNECT_SECURE, IS_DEFAULT, RECORD_STATUS)
values (100, 'Project.net', 'Project.net Default Directory', null, null, null, 'Project.net, Inc.', null, null, null, null, null, null, null, null, 1, 'A')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_DIRECTORY_HAS_PERSON...
insert into PN_DIRECTORY_HAS_PERSON (DIRECTORY_ID, PERSON_ID, IS_DEFAULT)
values (100, 1, 1)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_DIRECTORY_PROVIDER_TYPE...
insert into PN_DIRECTORY_PROVIDER_TYPE (PROVIDER_TYPE_ID, NAME, DESCRIPTION, SERVICE_PROVIDER_CLASS_NAME, CONFIGURATOR_CLASS_NAME, CONFIGURATION_CLASS_NAME)
values (1, '@prm.directory.provider.type.native.name', 'Native Directory Services', 'net.project.base.directory.nativedir.NativeDirectoryProvider', 'net.project.base.directory.nativedir.NativeDirectoryConfigurator', 'net.project.base.directory.nativedir.NativeDirectoryConfiguration')
/
insert into PN_DIRECTORY_PROVIDER_TYPE (PROVIDER_TYPE_ID, NAME, DESCRIPTION, SERVICE_PROVIDER_CLASS_NAME, CONFIGURATOR_CLASS_NAME, CONFIGURATION_CLASS_NAME)
values (2, '@prm.directory.provider.type.ldap.name', 'LDAP Directory Services', 'net.project.base.directory.ldap.LDAPDirectoryProvider', 'net.project.base.directory.ldap.LDAPDirectoryConfigurator', 'net.project.base.directory.ldap.LDAPDirectoryConfiguration')
/
commit
/
-- prompt  2 records loaded
-- prompt  Loading PN_DISCIPLINE_LOOKUP...
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (100, 'Architecture', 'Architecture')
/
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (200, 'HVAC', 'HVAC')
/
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (300, 'Plumbing', 'Plumbing')
/
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (400, 'Electrical', 'Electrical')
/
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (500, 'Structural', 'Structural')
/
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (600, 'Civil', 'Civil')
/
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (700, 'Site', 'Site')
/
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (800, 'Specialty', 'Specialty')
/
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (900, 'Owner/Operator', 'Owner/Operator')
/
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (1000, 'General Construction', 'General Construction')
/
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (1100, 'Construction Manager', 'Construction Manager')
/
insert into PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE, DISCIPLINE_NAME, DISCIPLINE_DESCRIPTION)
values (999, 'Other', 'Other discipline entered by user')
/
commit
/
-- prompt  12 records loaded
-- prompt  Loading PN_DISCUSSION_ACTION_LOOKUP...
insert into PN_DISCUSSION_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('discussion_group_create', 'A')
/
insert into PN_DISCUSSION_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('discussion_group_modify', 'A')
/
insert into PN_DISCUSSION_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('discussion_group_remove', 'A')
/
insert into PN_DISCUSSION_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('post_create', 'A')
/
insert into PN_DISCUSSION_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('post_modify', 'A')
/
insert into PN_DISCUSSION_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('post_remove', 'A')
/
insert into PN_DISCUSSION_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('reply_create', 'A')
/
commit
/
-- prompt  7 records loaded
-- prompt  Loading PN_DOC_ACTION_LOOKUP...
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('doc_create', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('doc_modify_properties', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('doc_view', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('doc_view_version', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('doc_view_properties', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('doc_check_out', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('doc_check_in', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('doc_undo_check_out', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('container_create', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('doc_remove_object', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('container_remove', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('doc_move_object', 'A')
/
insert into PN_DOC_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('undo_doc_remove_object', 'A')
/
commit
/
-- prompt  13 records loaded
-- prompt  Loading PN_DOC_CONTAINER...
insert into PN_DOC_CONTAINER (DOC_CONTAINER_ID, CONTAINER_NAME, CONTAINER_DESCRIPTION, DATE_MODIFIED, MODIFIED_BY_ID, IS_HIDDEN, CRC, RECORD_STATUS)
values (5010, 'Top folder', 'Top level document folder', to_date('20-05-2008 16:31:01', 'dd-mm-yyyy hh24:mi:ss'), 1, 0, to_date('20-05-2008 16:31:01', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_DOC_FORMAT...
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (101, '@prm.document.format.3dvrml.name', null, null, 'x-world/x-vrml', 'wrl', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (102, '@prm.document.format.3dworld.name', null, null, 'x-world/x-svr', 'svr', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (103, '@prm.document.format.3dworld.name', null, null, 'x-world/x-vrt', 'vrt', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (104, '@prm.document.format.adobepdf.name', null, null, 'application/pdf', 'pdf', '/images/appicons/acrobat.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (105, '@prm.document.format.astound.name', null, null, 'application/astound', 'asd', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (106, '@prm.document.format.astound.name', null, null, 'application/astound', 'asn', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (107, '@prm.document.format.audio.name', null, null, 'audio/echospeech', 'es', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (108, '@prm.document.format.audio.name', null, null, 'audio/echospeech', 'esl', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (109, '@prm.document.format.audio.name', null, null, 'audio/x-liveaudio', 'lam', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (110, '@prm.document.format.audio.name', null, null, 'audio/x-pac', 'pac', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (111, '@prm.document.format.audio.name', null, null, 'audio/x-epac', 'pae', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (112, '@prm.document.format.audioaiff.name', null, null, 'audio/x-aiff', 'aif', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (113, '@prm.document.format.audioaiff.name', null, null, 'audio/x-aiff', 'aifc', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (114, '@prm.document.format.audioaiff.name', null, null, 'audio/x-aiff', 'aiff', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (115, '@prm.document.format.audioau.name', null, null, 'audio/basic', 'au', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (116, '@prm.document.format.audioau.name', null, null, 'audio/basic', 'snd', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (117, '@prm.document.format.audiomidi.name', null, null, 'audio/x-midi', 'mid', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (118, '@prm.document.format.audiomidi.name', null, null, 'audio/x-midi', 'midi', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (119, '@prm.document.format.audiowav.name', null, null, 'audio/x-wav', 'wav', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (120, '@prm.document.format.autocad.name', null, null, 'image/vnd.dwf', 'dwf', '/images/appicons/autocad.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (121, '@prm.document.format.autocad.name', null, null, 'image/vnd.dwg', 'dwg', '/images/appicons/autocad.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (122, '@prm.document.format.autocad.name', null, null, 'image/vnd.dxf', 'dxf', '/images/appicons/autocad.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (123, '@prm.document.format.binary.name', null, null, 'application/octet-stream', 'bin', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (124, '@prm.document.format.binhex.name', null, null, 'application/mac-binhex40', 'hqx', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (125, '@prm.document.format.bourneshell.name', null, null, 'application/x-sh', 'sh', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (126, '@prm.document.format.calstype1ccittgr4raster.name', null, null, 'image/cals', 'cal', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (127, '@prm.document.format.calstype1ccittgr4raster.name', null, null, 'image/cals', 'cals', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (128, '@prm.document.format.computergraphicsmetafile.name', null, null, 'image/cgm', 'cgm', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (129, '@prm.document.format.cooltalk.name', null, null, 'x-conference/x-cooltalk', 'ice', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (130, '@prm.document.format.dvivideo.name', null, null, 'application/x-dvi', 'dvi', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (131, '@prm.document.format.framemaker.name', null, null, 'application/x-maker', 'fm', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (132, '@prm.document.format.html.name', null, null, 'text/html', 'htm', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (133, '@prm.document.format.html.name', null, null, 'text/html', 'html', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (134, '@prm.document.format.image.name', null, null, 'image/vnd.dwg', 'dwg', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (136, '@prm.document.format.image.name', null, null, 'image/vnd.svf', 'svf', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (137, '@prm.document.format.imagebmp.name', null, null, 'image/bmp', 'bmp', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (139, '@prm.document.format.imagegif.name', null, null, 'image/gif', 'gif', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (140, '@prm.document.format.imageief.name', null, null, 'image/ief', 'ief', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (141, '@prm.document.format.imageifs.name', null, null, 'image/ifs', 'ifs', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (143, '@prm.document.format.imagejpeg.name', null, null, 'image/jpeg', 'jpe', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (144, '@prm.document.format.imagejpeg.name', null, null, 'image/jpeg', 'jpeg', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (146, '@prm.document.format.imagejpeg.name', null, null, 'image/jpeg', 'pjp', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (147, '@prm.document.format.imagejpeg.name', null, null, 'image/jpeg', 'pjpeg', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (148, '@prm.document.format.imagepbm.name', null, null, 'image/x-portable-bitmap', 'pbm', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (150, '@prm.document.format.imagephotocd.name', null, null, 'image/x-photo-cd', 'pcd', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (151, '@prm.document.format.imagepnm.name', null, null, 'image/x-portable-anymap', 'pnm', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (152, '@prm.document.format.imageppm.name', null, null, 'image/x-portable-pixmap', 'ppm', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (154, '@prm.document.format.imagergb.name', null, null, 'image/x-rgb', 'rgb', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (155, '@prm.document.format.imagetiff.name', null, null, 'image/tiff', 'tif', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (156, '@prm.document.format.imagetiff.name', null, null, 'image/tiff', 'tiff', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (158, '@prm.document.format.imagexbitmap.name', null, null, 'image/x-xbitmap', 'xbm', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (159, '@prm.document.format.imagexpixmap.name', null, null, 'image/x-xpixmap', 'xpm', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (161, '@prm.document.format.installfile.name', null, null, 'application/x-NET-Install', 'ins', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (162, '@prm.document.format.javaarchive.name', null, null, 'application/java-archive', 'jar', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (163, '@prm.document.format.javaclass.name', null, null, 'application/java-vm', 'class', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (165, '@prm.document.format.jso.name', null, null, 'application/java-serialized-object', 'ser', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (166, '@prm.document.format.latex.name', null, null, 'application/x-latex', 'latex', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (168, '@prm.document.format.microstationdesignfile.name', null, null, 'application/microstation', 'dgn', '/images/appicons/microstation.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (169, '@prm.document.format.microstationhiddenlinefile.name', null, null, 'application/microstation', 'hln', '/images/appicons/microstation.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (170, '@prm.document.format.microstationredlinefile.name', null, null, 'application/microstation', 'rdl', '/images/appicons/microstation.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (171, '@prm.document.format.msaccess.name', null, null, 'application/x-msaccess', 'mdb', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (173, '@prm.document.format.mscardfile.name', null, null, 'application/x-mscardfile', 'crd', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (174, '@prm.document.format.msclip.name', null, null, 'application/x-msclip', 'clp', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (176, '@prm.document.format.msexcel.name', null, null, 'application/vnd.ms-excel', 'xlc', '/images/appicons/excel.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (177, '@prm.document.format.msexcel.name', null, null, 'application/x-excel', 'xlc', '/images/appicons/excel.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (179, '@prm.document.format.msexcel.name', null, null, 'application/vnd.ms-excel', 'xlm', '/images/appicons/excel.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (180, '@prm.document.format.msexcel.name', null, null, 'application/x-excel', 'xlm', '/images/appicons/excel.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (181, '@prm.document.format.msexcel.name', null, null, 'application/vnd.ms-excel', 'xls', '/images/appicons/excel.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (183, '@prm.document.format.msexcel.name', null, null, 'application/vnd.ms-excel', 'xlt', '/images/appicons/excel.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (184, '@prm.document.format.msexcel.name', null, null, 'application/vnd.ms-excel', 'xlw', '/images/appicons/excel.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (186, '@prm.document.format.msmediaview.name', null, null, 'application/x-msmediaview', 'm13', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (187, '@prm.document.format.msmediaview.name', null, null, 'application/x-msmediaview', 'm14', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (188, '@prm.document.format.msmetafile.name', null, null, 'application/x-msmetafile', 'wmf', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (190, '@prm.document.format.mspowerpoint.name', null, null, 'application/vnd.ms-powerpoint', 'pot', '/images/appicons/powerpoint.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (191, '@prm.document.format.mspowerpoint.name', null, null, 'application/vnd.ms-powerpoint', 'pps', '/images/appicons/powerpoint.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (193, '@prm.document.format.msproject.name', null, null, 'application/vnd.ms-project', 'mpp', '/images/appicons/project.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (194, '@prm.document.format.mspublisher.name', null, null, 'application/x-mspublisher', 'pub', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (195, '@prm.document.format.msschedule.name', null, null, 'application/x-msschedule', 'scd', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (197, '@prm.document.format.msword.name', null, null, 'application/msword', 'doc', '/images/appicons/word.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (198, '@prm.document.format.mswrite.name', null, null, 'application/x-mswrite', 'wri', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (199, '@prm.document.format.nodesc.name', null, null, 'application/x-netcdf', 'cdf', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (201, '@prm.document.format.nodesc.name', null, null, 'application/x-pkcs7-crl', 'crl', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (202, '@prm.document.format.nodesc.name', null, null, 'application/x-pointplus', 'css', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (204, '@prm.document.format.nodesc.name', null, null, 'text/x-setext', 'etx', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (205, '@prm.document.format.nodesc.name', null, null, 'application/fastman', 'lcc', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (206, '@prm.document.format.nodesc.name', null, null, 'application/x-mocha', 'moc', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (207, '@prm.document.format.nodesc.name', null, null, 'application/x-mocha', 'mocha', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (208, '@prm.document.format.nodesc.name', null, null, 'application/x-netcdf', 'nc', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (209, '@prm.document.format.nodesc.name', null, null, 'application/oda', 'oda', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (210, '@prm.document.format.nodesc.name', null, null, 'application/x-salsa', 'slc', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (211, '@prm.document.format.nodesc.name', null, null, 'application/studiom', 'smp', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (212, '@prm.document.format.nodesc.name', null, null, 'text/x-speech', 'talk', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (213, '@prm.document.format.nodesc.name', null, null, 'application/x-timbuktu', 'tbp', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (214, '@prm.document.format.nodesc.name', null, null, 'application/x-tkined', 'tki', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (215, '@prm.document.format.nodesc.name', null, null, 'application/x-tkined', 'tkined', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (216, '@prm.document.format.perl.name', null, null, 'application/x-perl', 'pl', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (217, '@prm.document.format.postscript.name', null, null, 'application/postscript', 'ai', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (218, '@prm.document.format.postscript.name', null, null, 'application/postscript', 'eps', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (219, '@prm.document.format.postscript.name', null, null, 'application/postscript', 'ps', '/images/file.gif', 'A')
/
commit
/
-- prompt  100 records committed...
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (220, '@prm.document.format.proxyfile.name', null, null, 'application/x-ns-proxy-autoconfig', 'proxy', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (221, '@prm.document.format.realaudio.name', null, null, 'audio/x-pn-realaudio', 'ra', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (222, '@prm.document.format.realaudio.name', null, null, 'audio/x-pn-realaudio', 'ram', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (223, '@prm.document.format.richtext.name', null, null, 'text/richtext', 'rtx', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (224, '@prm.document.format.rtffile.name', null, null, 'application/rtf', 'rtf', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (225, '@prm.document.format.shellarchive.name', null, null, 'application/x-shar', 'shar', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (226, '@prm.document.format.sprite.name', null, null, 'application/x-sprite', 'spr', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (227, '@prm.document.format.sprite.name', null, null, 'application/x-sprite', 'sprite', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (228, '@prm.document.format.stuffitarchive.name', null, null, 'application/x-stuffit', 'sit', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (229, '@prm.document.format.tcl.name', null, null, 'application/x-tcl', 'tcl', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (230, '@prm.document.format.tex.name', null, null, 'application/x-tex', 'tex', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (231, '@prm.document.format.texinfo.name', null, null, 'application/x-texinfo', 'texi', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (232, '@prm.document.format.texinfo.name', null, null, 'application/x-texinfo', 'texinfo', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (233, '@prm.document.format.text.name', null, null, 'application/x-csh', 'csh', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (234, '@prm.document.format.text.name', null, null, 'text/plain', 'txt', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (235, '@prm.document.format.texttsv.name', null, null, 'text/tab-separated-values', 'tsv', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (236, '@prm.document.format.thename.name', null, null, 'application/x-mif', 'mi NODESC', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (237, '@prm.document.format.thename.name', null, null, 'application/x-mif', 'mif', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (238, '@prm.document.format.timbuktu.name', null, null, 'application/timbuktu', 'tbt', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (239, '@prm.document.format.troff.name', null, null, 'application/x-troff-man', 'man', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (240, '@prm.document.format.troff.name', null, null, 'application/x-troff-me', 'me', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (241, '@prm.document.format.troff.name', null, null, 'application/x-troff-ms', 'ms', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (242, '@prm.document.format.troff.name', null, null, 'application/x-troff', 'roff', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (243, '@prm.document.format.troff.name', null, null, 'application/x-troff', 't', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (244, '@prm.document.format.troff.name', null, null, 'application/x-troff', 'tr', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (245, '@prm.document.format.unixtar.name', null, null, 'application/x-tar', 'tar', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (246, '@prm.document.format.video.name', null, null, 'video/vivo', 'viv', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (247, '@prm.document.format.video.name', null, null, 'video/vivo', 'vivo', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (248, '@prm.document.format.video.name', null, null, 'video/wavelet', 'wv', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (249, '@prm.document.format.videoavi.name', null, null, 'video/msvideo', 'avi', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (250, '@prm.document.format.videofvi.name', null, null, 'video/isivideo', 'fvi', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (251, '@prm.document.format.videompeg.name', null, null, 'video/mpeg', 'mpe', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (252, '@prm.document.format.videompeg.name', null, null, 'video/mpeg', 'mpeg', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (253, '@prm.document.format.videompeg.name', null, null, 'video/mpeg', 'mpegv', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (254, '@prm.document.format.videompeg.name', null, null, 'video/mpeg', 'mpg', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (255, '@prm.document.format.videompeg.name', null, null, 'video/mpeg', 'mpv', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (256, '@prm.document.format.videompeg.name', null, null, 'video/mpeg', 'vbs', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (257, '@prm.document.format.videompeg2.name', null, null, 'video/x-mpeg2', 'mp2v', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (258, '@prm.document.format.videompeg2.name', null, null, 'video/x-mpeg2', 'mpv2', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (259, '@prm.document.format.videomsvideo.name', null, null, 'video/x-msvideo', 'avi', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (260, '@prm.document.format.videoquicktime.name', null, null, 'video/quicktime', 'moov', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (261, '@prm.document.format.videoquicktime.name', null, null, 'video/quicktime', 'mov', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (262, '@prm.document.format.videoquicktime.name', null, null, 'video/quicktime', 'qt', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (263, '@prm.document.format.videosgimovie.name', null, null, 'video/x-sgi-movie', 'movie', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (264, '@prm.document.format.visio.name', null, null, 'application/visio', 'vsd', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (265, '@prm.document.format.win95nthelp.name', null, null, 'application/winhlp', 'hlp', '/images/appicons/help.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (266, '@prm.document.format.xmldocument.name', null, null, 'text/plain', 'xml', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (267, '@prm.document.format.ziparchive.name', null, null, 'application/zip', 'zip', '/images/appicons/zip.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (-999, '@prm.document.format.unknown.name', null, null, 'application/pnet', null, '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (300, '@prm.document.format.mswordtemplate.name', 'Microsoft Word Template', null, 'application/msword', 'dot', '/images/appicons/word.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (301, '@prm.document.format.mspowerpointtemplate.name', 'Microsoft Powerpoint Template', null, 'application/vnd.ms-powerpoint', 'dot', '/images/appicons/powerpoint.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (135, '@prm.document.format.image.name', null, null, 'image/png', 'png', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (138, '@prm.document.format.imagefif.name', null, null, 'image/fif', 'fif', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (142, '@prm.document.format.imagejpeg.name', null, null, 'image/jpeg', 'jfif', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (145, '@prm.document.format.imagejpeg.name', null, null, 'image/jpeg', 'jpg', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (149, '@prm.document.format.imagepgm.name', null, null, 'image/x-portable-graymap', 'pgm', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (153, '@prm.document.format.imageraster.name', null, null, 'image/x-cmu-raster', 'ras', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (157, '@prm.document.format.imagewavelet.name', null, null, 'image/wavelet', 'wi', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (160, '@prm.document.format.imagexwb.name', null, null, 'image/x-xwindowdump', 'xwd', '/images/appicons/image.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (164, '@prm.document.format.javascript.name', null, null, 'application/x-javascript', 'js', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (167, '@prm.document.format.microstationcelllibrary.name', null, null, 'application/octet-stream', 'cel', '/images/appicons/microstation.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (172, '@prm.document.format.msasp.name', null, null, 'application/x-asap', 'asp', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (175, '@prm.document.format.msexcel.name', null, null, 'application/vnd.ms-excel', 'xla', '/images/appicons/excel.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (178, '@prm.document.format.msexcel.name', null, null, 'application/x-excel', 'xll', '/images/appicons/excel.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (182, '@prm.document.format.msexcel.name', null, null, 'application/x-excel', 'xls', '/images/appicons/excel.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (185, '@prm.document.format.msexcel.name', null, null, 'application/x-excel', 'xlw', '/images/appicons/excel.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (189, '@prm.document.format.msmoney.name', null, null, 'application/x-msmoney', 'mny', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (192, '@prm.document.format.mspowerpoint.name', null, null, 'application/vnd.ms-powerpoint', 'ppt', '/images/appicons/powerpoint.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (196, '@prm.document.format.msterminal.name', null, null, 'application/x-msterminal', 'trm', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (200, '@prm.document.format.nodesc.name', null, null, 'application/x-fortezza-ckl', 'ckl', '/images/file.gif', 'A')
/
insert into PN_DOC_FORMAT (DOC_FORMAT_ID, FORMAT_NAME, DESCRIPTION, APPLICATION, MIME_TYPE, FILE_EXT, APP_ICON_URL, RECORD_STATUS)
values (203, '@prm.document.format.nodesc.name', null, null, 'application/pre-encrypted', 'enc', '/images/file.gif', 'A')
/
commit
/
-- prompt  170 records loaded
-- prompt  Loading PN_DOC_PROVIDER_TYPE...
insert into PN_DOC_PROVIDER_TYPE (DOC_PROVIDER_TYPE_ID, DOC_PROVIDER_TYPE_NAME, DOC_PROVIDER_TYPE_DESC)
values (100, 'Project.net default', 'Project.net default document provider')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_DOC_PROVIDER...
insert into PN_DOC_PROVIDER (DOC_PROVIDER_ID, DOC_PROVIDER_TYPE_ID, DOC_PROVIDER_NAME, DOC_PROVIDER_DESCRIPTION, IS_DEFAULT, CRC, RECORD_STATUS)
values (100, 100, 'Project.net Database document vault', 'Default document vault provided by the Project.net application', 1, to_date('20-05-2008 16:28:56', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_DOC_SPACE...
insert into PN_DOC_SPACE (DOC_SPACE_ID, DOC_SPACE_NAME, CRC, RECORD_STATUS)
values (5009, 'default', to_date('20-05-2008 16:31:01', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_DOC_PROVIDER_HAS_DOC_SPACE...
insert into PN_DOC_PROVIDER_HAS_DOC_SPACE (DOC_PROVIDER_ID, DOC_SPACE_ID)
values (100, 5009)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_DOC_REPOSITORY_BASE...
insert into PN_DOC_REPOSITORY_BASE (REPOSITORY_ID, REPOSITORY_PATH, IS_ACTIVE)
values (101, '\\lion\PNET\docVault1', 1)
/
insert into PN_DOC_REPOSITORY_BASE (REPOSITORY_ID, REPOSITORY_PATH, IS_ACTIVE)
values (102, '\\lion\PNET\docVault2', 1)
/
insert into PN_DOC_REPOSITORY_BASE (REPOSITORY_ID, REPOSITORY_PATH, IS_ACTIVE)
values (103, '\\lion\PNET\docVault3', 1)
/
commit
/
-- prompt  3 records loaded
-- prompt  Loading PN_DOC_SPACE_HAS_CONTAINER...
insert into PN_DOC_SPACE_HAS_CONTAINER (DOC_SPACE_ID, DOC_CONTAINER_ID, IS_ROOT)
values (5009, 5010, 1)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_DOC_TYPE...
insert into PN_DOC_TYPE (DOC_TYPE_ID, PROPERTY_SHEET_CLASS_ID, TYPE_NAME, TYPE_DESCRIPTION, RECORD_STATUS)
values (100, null, 'General', 'Generic Document Type', null)
/
insert into PN_DOC_TYPE (DOC_TYPE_ID, PROPERTY_SHEET_CLASS_ID, TYPE_NAME, TYPE_DESCRIPTION, RECORD_STATUS)
values (200, null, 'Meeting Agenda', 'Meeting Agenda Document Type', null)
/
insert into PN_DOC_TYPE (DOC_TYPE_ID, PROPERTY_SHEET_CLASS_ID, TYPE_NAME, TYPE_DESCRIPTION, RECORD_STATUS)
values (300, null, 'Meeting Minutes', 'Meeting Minutes Document Type', null)
/
insert into PN_DOC_TYPE (DOC_TYPE_ID, PROPERTY_SHEET_CLASS_ID, TYPE_NAME, TYPE_DESCRIPTION, RECORD_STATUS)
values (400, null, 'Other', 'Even more Generic Document Type', null)
/
commit
/
-- prompt  4 records loaded
-- prompt  Loading PN_ELEMENT_DISPLAY_CLASS...
insert into PN_ELEMENT_DISPLAY_CLASS (ELEMENT_ID, CLASS_ID)
values (1, 21)
/
insert into PN_ELEMENT_DISPLAY_CLASS (ELEMENT_ID, CLASS_ID)
values (2, 22)
/
insert into PN_ELEMENT_DISPLAY_CLASS (ELEMENT_ID, CLASS_ID)
values (3, 23)
/
insert into PN_ELEMENT_DISPLAY_CLASS (ELEMENT_ID, CLASS_ID)
values (4, 24)
/
insert into PN_ELEMENT_DISPLAY_CLASS (ELEMENT_ID, CLASS_ID)
values (5, 25)
/
insert into PN_ELEMENT_DISPLAY_CLASS (ELEMENT_ID, CLASS_ID)
values (9, 29)
/
insert into PN_ELEMENT_DISPLAY_CLASS (ELEMENT_ID, CLASS_ID)
values (11, 31)
/
insert into PN_ELEMENT_DISPLAY_CLASS (ELEMENT_ID, CLASS_ID)
values (12, 32)
/
insert into PN_ELEMENT_DISPLAY_CLASS (ELEMENT_ID, CLASS_ID)
values (13, 33)
/
insert into PN_ELEMENT_DISPLAY_CLASS (ELEMENT_ID, CLASS_ID)
values (20, 40)
/
insert into PN_ELEMENT_DISPLAY_CLASS (ELEMENT_ID, CLASS_ID)
values (21, 41)
/
commit
/
-- prompt  11 records loaded
-- prompt  Loading PN_ELEMENT_PROPERTY...
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (1, 1, 100, 'tag', 'tag', 'input type=text', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (2, 1, 100, 'tag', 'tag', 'textarea', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (2, 2, 100, 'cols', 'in_tag', '40', '80', 'Length', '1', 1)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (2, 3, 100, 'rows', 'in_tag', '3', '10', 'Height', '2', 1)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (3, 1, 100, 'tag', 'tag', 'select', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (3, 2, 100, 'multiple', 'in_tag', '0', null, 'Multiple Selection', null, 1)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (3, 3, 100, 'size', 'in_tag', '10', '40', 'Display Size', '1', 1)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (4, 1, 100, 'tag', 'tag', 'input type=text', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (5, 1, 100, 'tag', 'tag', 'input type=checkbox', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (9, 1, 100, 'tag', 'tag', 'select', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (11, 1, 100, 'tag', 'tag', 'select', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (20, 1, 100, 'type', 'type', 'static', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (20, 2, 100, 'tag', 'tag', 'static', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (21, 1, 100, 'type', 'type', 'text', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (21, 2, 100, 'tag', 'tag', 'text', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (12, 1, 100, 'tag', 'tag', 'input type="text"', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (12, 2, 100, 'size', 'in_tag', '10', '80', 'Size', '2', 1)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (13, 1, 100, 'tag', 'tag', 'input type="text"', null, null, null, 0)
/
insert into PN_ELEMENT_PROPERTY (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID, PROPERTY, PROPERTY_TYPE, DEFAULT_VALUE, MAX_VALUE, PROPERTY_LABEL, MIN_VALUE, IS_USER_CHANGABLE)
values (13, 2, 100, 'size', 'in_tag', '10', '80', 'Size', '2', 1)
/
commit
/
-- prompt  19 records loaded
-- prompt  Loading PN_ENVELOPE_HISTORY_ACTION...
insert into PN_ENVELOPE_HISTORY_ACTION (HISTORY_ACTION_ID, ACTION_NAME, ACTION_DESCRIPTION, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (100, '@prm.workflow.envelope.history.action.enteredstep.name', 'Workflow entered a step.', 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
insert into PN_ENVELOPE_HISTORY_ACTION (HISTORY_ACTION_ID, ACTION_NAME, ACTION_DESCRIPTION, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (200, '@prm.workflow.envelope.history.action.performedtransistion.name', 'Workflow transition performed.', 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
insert into PN_ENVELOPE_HISTORY_ACTION (HISTORY_ACTION_ID, ACTION_NAME, ACTION_DESCRIPTION, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (300, '@prm.workflow.envelope.history.action.notificationsent.name', 'Workflow notification sent.', 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
commit
/
-- prompt  3 records loaded
-- prompt  Loading PN_EVENT_TYPE...
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (100, 'doc_create', '@prm.document.container.event.createdocument.description', 'doc_container', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (200, 'container_create', '@prm.document.container.event.createcontainer.description', 'doc_container', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (400, 'doc_modify_properties', '@prm.document.event.modifyproperties.description', 'document', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (300, 'doc_copy_object', '@prm.document.document.event.copy.description', 'document', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (500, 'doc_move_object', '@prm.document.document.event.move.description', 'document', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (600, 'doc_remove_object', '@prm.document.document.event.remove.description', 'document', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (700, 'container_remove', '@prm.document.container.event.remove.description', 'doc_container', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (800, 'doc_check_in', '@prm.document.document.event.checkin.description', 'document', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (900, 'doc_check_out', '@prm.document.document.event.checkout.description', 'document', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (1100, 'doc_view', '@prm.document.document.event.view.description', 'document', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (1200, 'doc_view_version', '@prm.document.document.event.viewversion.description', 'document', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (1000, 'doc_undo_check_out', '@prm.document.document.event.undocheckout.description', 'document', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (1300, 'doc_view_properties', '@prm.document.document.event.viewproperties.description', 'document', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2300, 'envelope_step_enter', '@prm.workflow.step.event.envelopeenters.description', 'workflow_step', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2000, 'task_assignment', '@prm.schedule.task.assign.description', 'task', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2010, 'business_invite', '@prm.business.event.inviteparticipant.description', 'business', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2040, 'meeting_invite', '@prm.calendar.meeting.event.invite.description', 'meeting', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2310, 'news_create', '@prm.news.event.create.description', 'news', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2312, 'news_remove', '@prm.news.event.remove.description', 'news', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2311, 'news_modify', '@prm.news.event.modify.description', 'news', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2320, 'discussion_group_create', '@prm.discussion.discussiongroup.event.create.description', 'discussion_group', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2070, 'participant_delete', '@prm.project.event.participantdelete.description', 'project', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2321, 'discussion_group_modify', '@prm.discussion.discussiongroup.event.modify.description', 'discussion_group', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2322, 'discussion_group_remove', '@prm.discussion.discussiongroup.event.remove.description', 'discussion_group', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2330, 'task_create', '@prm.schedule.task.event.create.description', 'task', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2331, 'task_modify', '@prm.schedule.task.event.modify.description', 'task', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2332, 'task_remove', '@prm.schedule.task.event.remove.description', 'task', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2340, 'form_create', '@prm.form.form.event.create.description', 'form', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2341, 'form_modify', '@prm.form.form.event.modify.description', 'form', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2342, 'form_remove', '@prm.form.form.event.remove.description', 'form', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2350, 'form_data_create', '@prm.form.formdata.event.create.description', 'form_data', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2351, 'form_data_modify', '@prm.form.formdata.event.modify.description', 'form_data', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2352, 'form_data_remove', '@prm.form.formdata.event.remove.description', 'form_data', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2333, 'task_status_change', '@prm.schedule.task.event.changestatus.description', 'task', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2360, 'discussion_create_post', '@prm.discussion.post.event.create.description', 'discussion_group', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2361, 'post_modify', '@prm.discussion.post.event.modify.description', 'post', 'D', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2362, 'post_remove', '@prm.discussion.post.event.remove.description', 'post', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2363, 'reply_create', '@prm.discussion.post.event.createreply.description', 'post', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2370, 'role_create', '@prm.security.group.event.create.description', 'group', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2371, 'role_modify', '@prm.security.group.event.modify.description', 'group', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2372, 'role_remove', '@prm.security.group.event.remove.description', 'group', 'A', null)
/
insert into PN_EVENT_TYPE (EVENT_TYPE_ID, NAME, DESCRIPTION, OBJECT_TYPE, RECORD_STATUS, CRC)
values (2323, 'discussion_remove_post', '@prm.notification.type.deletepost.description', 'discussion_group', 'A', null)
/
commit
/
-- prompt  42 records loaded
-- prompt  Loading PN_NOTIFICATION_TYPE...
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (100, 'doc_create', '@prm.notification.type.createdocument.description', '@prm.notification.type.createdocument.default.message', 'doc_container', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (200, 'container_create', '@prm.notification.type.createcontainer.description', '@prm.notification.type.createcontainer.default.message', 'doc_container', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (300, 'doc_copy_object', '@prm.notification.type.copydocument.description', '@prm.notification.type.copydocument.default.message', 'document', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (400, 'doc_modify_properties', '@prm.notification.type.modifydocumentproperties.description', '@prm.notification.type.modifydocumentproperties.default.message', 'document', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (500, 'doc_move_object', '@prm.notification.type.movedocument.description', '@prm.notification.type.movedocument.default.message', 'document', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (600, 'doc_remove_object', '@prm.notification.type.removedocument.description', '@prm.notification.type.removedocument.default.message', 'document', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (700, 'container_remove', '@prm.notification.type.removecontainer.description', '@prm.notification.type.removecontainer.default.message', 'doc_container', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (800, 'doc_check_in', '@prm.notification.type.checkindocument.description', '@prm.notification.type.checkindocument.default.message', 'document', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (900, 'doc_check_out', '@prm.notification.type.checkoutdocument.description', '@prm.notification.type.checkoutdocument.default.message', 'document', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (1000, 'doc_undo_check_out', '@prm.notification.type.undocheckoutdocument.description', '@prm.notification.type.undocheckoutdocument.default.message', 'document', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (1100, 'doc_view', '@prm.notification.type.viewdocument.description', '@prm.notification.type.viewdocument.default.message', 'document', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (1200, 'doc_view_version', '@prm.notification.type.viewdocumentversion.description', '@prm.notification.type.viewdocumentversion.default.message', 'document', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (1300, 'doc_view_properties', '@prm.notification.type.viewdocumentproperties.description', '@prm.notification.type.viewdocumentproperties.default.message', 'document', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2300, 'envelope_step_enter', '@prm.notification.type.envelopeentersworkflowstep.description', '@prm.notification.type.envelopeentersworkflowstep.default.message', 'workflow_step', null, 1, null, null, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2000, 'task_assignment', '@prm.notification.type.taskassignment.description', '@prm.notification.type.taskassignment.default.message', 'task', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2010, 'business_invite', '@prm.notification.type.businessinvite.description', '@prm.notification.type.businessinvite.default.message', 'business', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2020, 'admin_fogotten_login', '@prm.notification.type.forgottenlogin.description', '@prm.notification.type.forgottenlogin.default.message', 'person', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2030, 'admin_forgotten_password', '@prm.notification.type.forgottenpassword.description', '@prm.notification.type.forgottenpassword.default.message', 'person', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2040, 'meeting_invite', '@prm.notification.type.meetinginvite.description', '@prm.notification.type.meetinginvite.default.message', 'meeting', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2050, 'project_invite', '@prm.notification.type.projectinvite.description', '@prm.notification.type.projectinvite.default.message', 'project', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2060, 'registration_confirmation', '@prm.notification.type.registrationconfirmation.description', '@prm.notification.type.registrationconfirmation.default.message', 'person', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2070, 'participant_deletion', '@prm.notification.type.participantdeletion.description', '@prm.notification.type.participantdeletion.default.message', 'project', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2310, 'news_create', '@prm.notification.type.createnews.description', '@prm.notification.type.createnews.default.message', 'news', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2312, 'news_remove', '@prm.notification.type.deletenews.description', '@prm.notification.type.deletenews.default.message', 'news', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2311, 'news_modify', '@prm.notification.type.modifynews.description', '@prm.notification.type.modifynews.default.message', 'news', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2320, 'discussion_group_create', '@prm.notification.type.creatediscussiongroup.description', '@prm.notification.type.creatediscussiongroup.default.message', 'discussion_group', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2321, 'discussion_group_modify', '@prm.notification.type.modifydiscussiongroup.description', '@prm.notification.type.modifydiscussiongroup.default.message', 'discussion_group', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2322, 'discussion_group_remove', '@prm.notification.type.deletediscussiongroup.description', '@prm.notification.type.deletediscussiongroup.default.message', 'discussion_group', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2330, 'task_create', '@prm.notification.type.createtask.description', '@prm.notification.type.createtask.default.message', 'task', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2331, 'task_modify', '@prm.notification.type.modifytask.description', '@prm.notification.type.modifytask.default.message', 'task', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2332, 'task_remove', '@prm.notification.type.deletetask.description', '@prm.notification.type.deletetask.default.message', 'task', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2340, 'form_create', '@prm.notification.type.createform.description', '@prm.notification.type.createform.default.message', 'form', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2341, 'form_modify', '@prm.notification.type.modifyform.description', '@prm.notification.type.modifyform.default.message', 'form', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2342, 'form_remove', '@prm.notification.type.deleteform.description', '@prm.notification.type.deleteform.default.message', 'form', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2350, 'form_data_create', '@prm.notification.type.createformdata.description', '@prm.notification.type.createformdata.default.message', 'form_data', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2351, 'form_data_modify', '@prm.notification.type.modifyformdata.description', '@prm.notification.type.modifyformdata.default.message', 'form_data', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2352, 'form_data_remove', '@prm.notification.type.deleteformdata.description', '@prm.notification.type.deleteformdata.default.message', 'form_data', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2333, 'task_status_change', '@prm.notification.type.changetaskstatus.description', '@prm.notification.type.changetaskstatus.default.message', 'task', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2360, 'discussion_create_post', '@prm.notification.type.createpost.description', '@prm.notification.type.createpost.default.message', 'discussion_group', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2361, 'post_modify', '@prm.notification.type.modifypost.description', '@prm.notification.type.modifypost.default.message', 'post', null, 1, null, 1, 'D', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2362, 'post_remove', '@prm.notification.type.deletepost.description', '@prm.notification.type.deletepost.default.message', 'post', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2363, 'reply_create', '@prm.notification.type.createreply.description', '@prm.notification.type.createreply.default.message', 'post', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2370, 'role_create', '@prm.notification.type.createrole.description', '@prm.notification.type.createrole.default.message', 'group', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2371, 'role_modify', '@prm.notification.type.modifyrole.description', '@prm.notification.type.modifyrole.default.message', 'group', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2372, 'role_delete', '@prm.notification.type.deleterole.description', '@prm.notification.type.deleterole.default.message', 'group', null, 1, null, 1, 'A', null)
/
insert into PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, NAME, DESCRIPTION, DEFAULT_MESSAGE, OBJECT_TYPE, CREATE_DATE, CREATED_BY_ID, MODIFIED_DATE, MODIFIED_BY_ID, RECORD_STATUS, CRC)
values (2323, 'discussion_remove_post', '@prm.notification.type.deletepost.description', '@prm.notification.type.deletepost.default.message', 'discussion_group', null, 1, null, 1, 'A', null)
/
commit
/
-- prompt  46 records loaded
-- prompt  Loading PN_EVENT_HAS_NOTIFICATION...
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (100, 100)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (200, 200)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (400, 400)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (500, 500)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (600, 600)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (700, 700)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (800, 800)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (900, 900)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (1000, 1000)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (1100, 1100)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (1200, 1200)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (1300, 1300)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2000, 2000)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2010, 2010)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2040, 2040)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2070, 2070)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2300, 2300)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2310, 2310)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2311, 2311)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2312, 2312)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2320, 2320)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2321, 2321)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2322, 2322)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2323, 2323)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2330, 2330)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2331, 2331)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2332, 2332)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2333, 2333)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2340, 2340)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2341, 2341)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2342, 2342)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2350, 2350)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2351, 2351)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2352, 2352)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2360, 2360)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2361, 2361)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2362, 2362)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2363, 2363)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2370, 2370)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2371, 2371)
/
insert into PN_EVENT_HAS_NOTIFICATION (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID)
values (2372, 2372)
/
commit
/
-- prompt  41 records loaded
-- prompt  Loading PN_FORMS_ACTION_LOOKUP...
insert into PN_FORMS_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('form_create', 'A')
/
insert into PN_FORMS_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('form_modify', 'A')
/
insert into PN_FORMS_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('form_remove', 'A')
/
insert into PN_FORMS_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('form_data_create', 'A')
/
insert into PN_FORMS_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('form_data_modify', 'A')
/
insert into PN_FORMS_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('form_data_remove', 'A')
/
commit
/
-- prompt  6 records loaded
-- prompt  Loading PN_GLOBAL_CODE...
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (100, 100, 'Hours', 'hours allocated per time period', null, 100, 1, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (200, 100, 'Days', 'days allocated per time period', null, 200, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (300, 100, 'Percent', 'percent allocated per time period', null, 300, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (10, 110, 'Calendar Month', 'Resource allocation by Calendar Month', null, 10, 1, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (20, 110, 'Fiscal Month', 'Resource allocation by Fiscal Month', null, 20, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (0, 200, 'January', 'The month of January', null, 0, 1, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (1, 200, 'February', 'The month of February', null, 1, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (2, 200, 'March', 'The month of March', null, 2, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (3, 200, 'April', 'The month of April', null, 3, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (4, 200, 'May', 'The month of May', null, 4, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (5, 200, 'June', 'The month of June', null, 5, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (6, 200, 'July', 'The month of July', null, 6, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (7, 200, 'August', 'The month of August', null, 7, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (8, 200, 'September', 'The month of September', null, 8, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (9, 200, 'October', 'The month of October', null, 9, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (10, 200, 'November', 'The month of November', null, 10, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (11, 200, 'December', 'The month of December', null, 11, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (0, 300, 'Jan', 'The month of January', null, 0, 1, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (1, 300, 'Feb', 'The month of February', null, 1, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (2, 300, 'Mar', 'The month of March', null, 2, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (3, 300, 'Apr', 'The month of April', null, 3, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (4, 300, 'May', 'The month of May', null, 4, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (5, 300, 'Jun', 'The month of June', null, 5, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (6, 300, 'Jul', 'The month of July', null, 6, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (7, 300, 'Aug', 'The month of August', null, 7, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (8, 300, 'Sep', 'The month of September', null, 8, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (9, 300, 'Oct', 'The month of October', null, 9, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (10, 300, 'Nov', 'The month of November', null, 10, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (11, 300, 'Dec', 'The month of December', null, 11, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (10, 400, 'h', 'Hours', null, 11, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (20, 400, 'd', 'Days', null, 11, 1, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (30, 400, 'w', 'Weeks', null, 11, 0, 'A')
/
insert into PN_GLOBAL_CODE (CODE, CODE_TYPE_ID, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values (40, 400, 'm', 'Months', null, 11, 0, 'A')
/
commit
/
-- prompt  33 records loaded
-- prompt  Loading PN_GLOBAL_DOMAIN...
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_document', 'doc_status_id', 100, '@prm.document.status.notstarted.name', null, null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_document', 'doc_status_id', 200, '@prm.document.status.inprogress.name', null, null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_document', 'doc_status_id', 300, '@prm.document.status.pending.name', null, null, 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_document', 'doc_status_id', 400, '@prm.document.status.complete.name', null, null, 400, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'space_type_id', 100, '@prm.project.space.type.project.name', 'Project Space', null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'space_type_id', 200, '@prm.project.space.type.program.name', 'Program Space', null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'status_code_id', 100, '@prm.project.status.notstarted.name', 'Project has not been started yet', null, 100, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'status_code_id', 200, '@prm.project.status.inprocess.name', 'Project is underway', null, 200, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'status_code_id', 300, '@prm.project.status.onhold.name', 'Project is not being actively being worked on', null, 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'status_code_id', 400, '@prm.project.status.completed.name', 'Project has been completed', null, 400, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'color_code_id', 100, '@prm.project.color.green.name', 'Project is on track to met commitments.', '/images/green_light.gif', 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'color_code_id', 200, '@prm.project.color.yellow.name', 'Project commitments are at risk.  Recovery plan in place to meet commitments.', '/images/yellow_light.gif', 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'color_code_id', 300, '@prm.project.color.red.name', 'Project will not meet commitments.', '/images/red_light.gif', 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_person_has_skill', 'proficiency_code', 100, 'Beginner', 'Just Beginning to know and apply', null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_person_has_skill', 'proficiency_code', 200, 'Intermediate', 'Has general knowledge and some experience', null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_person_has_skill', 'proficiency_code', 300, 'Advanced', 'Has advanced knowledge and significant experience', null, 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_person_has_skill', 'proficiency_code', 400, 'Expert', 'Higest Level of knowledge and experience', null, 400, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_post', 'urgency_id', 100, '@prm.discussion.post.urgency.normal.name', 'Posting has normal urgency', null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_post', 'urgency_id', 200, '@prm.discussion.post.urgency.high.name', 'Posting is very urgent', null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_post', 'urgency_id', 300, '@prm.discussion.post.urgency.low.name', 'Posting has low urgency', null, 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_deliverable', 'deliverable_type_id', 100, '@prm.process.deliverable.type.methodologydefined.name', 'Standard deliverable defined by the methodology', null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_deliverable', 'deliverable_type_id', 200, '@prm.process.deliverable.type.projectdefined.name', 'Custom deliverable defined by the project', null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_deliverable', 'deliverable_type_id', 300, '@prm.process.deliverable.type.personal.name', 'Private deliverable visible only by you.', null, 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_phase', 'status_id', 10, '@prm.process.phase.status.notstarted.name', 'Work on this phase has not begun.', null, 10, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_phase', 'status_id', 20, '@prm.process.phase.status.inprocess.name', 'Work on this phase is in process.', null, 20, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_phase', 'status_id', 30, '@prm.process.phase.status.completed.name', 'This phase is complete.', null, 30, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_phase', 'status_id', 40, '@prm.process.phase.status.waived.name', 'This phase is not required.', null, 40, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_gate', 'status_id', 10, '@prm.process.gate.status.notscheduled.name', 'A gate review data has not been set.', null, 10, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_gate', 'status_id', 20, '@prm.process.gate.status.scheduled.name', 'A get review date has been set.', null, 20, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_gate', 'status_id', 30, '@prm.process.gate.status.passed.name', 'This gate is was successfully passed.', null, 30, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_gate', 'status_id', 40, '@prm.process.gate.status.rescheduled.name', 'Gate review was held, but more work is needed.  Another gate review needed.', null, 40, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_gate', 'status_id', 50, '@prm.process.gate.status.stopped.name', 'The project was stopped at this gate.', null, 50, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_gate', 'status_id', 60, '@prm.process.gate.status.waived.name', 'This gate is not required.', null, 60, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'visibility_id', 200, '@prm.project.visibility.personal.name', 'Personal Space Visibility', null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_assignment', 'status_id', 10, '@prm.global.assignment.status.assigned.name', 'Assigned', null, 10, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_assignment', 'status_id', 30, '@prm.global.assignment.status.inprocess.name', 'Some work has been done', null, 30, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_deliverable', 'status_id', 10, '@prm.process.deliverable.status.notstarted.name', 'Work has not begun.', null, 10, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_deliverable', 'status_id', 30, '@prm.process.deliverable.status.completedinreview.name', 'Deliverable has been completed, ready for review.', null, 30, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_deliverable', 'status_id', 40, '@prm.process.deliverable.status.completedapproved.name', 'Deliverable completed and approved.', null, 40, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_task', 'status_id', 10, '@prm.schedule.task.status.notstarted.name', 'Work has not begun.', null, 10, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_task', 'status_id', 20, '@prm.schedule.task.status.inprocess.name', 'Work in process.', null, 20, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_task', 'status_id', 40, '@prm.schedule.task.status.cancelled.name', 'This task is not required.', null, 40, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_task', 'priority', 10, '@prm.schedule.task.priority.low.name', 'Low Priority', null, 10, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_task', 'priority', 30, '@prm.schedule.task.priority.high.name', 'High Priority', null, 30, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_resource_assignment', 'status_id', 20, '@prm.schedule.resource.assignment.status.accepted.name', 'Accepted by the assignee', null, 20, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_resource_assignment', 'status_id', 40, '@prm.schedule.resource.assignment.status.completedconfirmationpending.name', 'Completed the assignment, assignor''s confirmation', null, 40, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_resource_assignment', 'status_id', 50, '@prm.schedule.resource.assignment.status.completedconfirmation.name', 'Completed the assignment, comfirmed by the assignor', null, 50, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_resource_assignment', 'status_id', 70, '@prm.schedule.resource.assignment.status.rejected.name', 'Rejected the assignment', null, 70, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'frequency_type_id', 10, '@prm.calendar.frequency.type.onetime.name', 'One time', null, 10, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'frequency_type_id', 20, '@prm.calendar.frequency.type.daily.name', 'Daily', null, 20, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'frequency_type_id', 40, '@prm.calendar.frequency.type.biweekly.name', 'Once every other week', null, 40, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'frequency_type_id', 50, '@prm.calendar.frequency.type.monthly.name', 'Monthly', null, 50, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'frequency_type_id', 70, '@prm.calendar.frequency.type.yearly.name', 'Yearly', null, 70, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'facility_type', 10, '@prm.calendar.facility.type.physical.name', 'Physical Facility', null, 10, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'facility_type', 30, '@prm.calendar.facility.type.webex.name', 'WebEx Facility', null, 30, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'event_type_id', 10, '@prm.calendar.event.type.meeting.name', 'Meeting', null, 10, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'event_type_id', 20, '@prm.calendar.event.type.event.name', 'Event', null, 20, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'event_type_id', 40, '@prm.calendar.event.type.task.name', 'Scheduled Task', null, 40, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'event_type_id', 50, '@prm.calendar.event.type.gate.name', 'Process Gate Review', null, 50, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_agenda_item', 'status_id', 20, '@prm.calendar.agenda.status.covered.name', 'Covered in the Meeting', null, 20, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_agenda_item', 'status_id', 30, '@prm.calendar.agenda.status.postponed.name', 'Not covered, postoned to a future meeting', null, 30, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_cal_event_has_attendee', 'status_id', 20, '@prm.calendar.event.attendee.status.acceptedinvitation.name', 'Covered in the Meeting', null, 20, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_cal_event_has_attendee', 'status_id', 40, '@prm.calendar.event.attendee.status.attended.name', 'Not covered, postoned to a future meeting', null, 40, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_cal_event_has_attendee', 'status_id', 50, '@prm.calendar.event.attendee.status.absent.name', 'Not covered, postoned to a future meeting', null, 50, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_cal_event_has_attendee', 'status_id', 60, '@prm.calendar.event.attendee.status.tardy.name', 'Not covered, postoned to a future meeting', null, 60, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_assignment', 'status_id', 50, '@prm.global.assignment.status.completedconfirmed.name', 'Completed the assignment, comfirmed by the assignor', null, 50, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_assignment', 'status_id', 60, '@prm.global.assignment.status.delegated.name', 'Delegated', null, 60, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_assignment', 'status_id', 70, '@prm.global.assignment.status.rejected.name', 'Rejected the assignment', null, 70, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 0, 'None', null, null, 0, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 100, 'Alteration', 'Alteration', null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 200, 'Addition', 'Addition', null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 300, 'Decommissioning', 'Decommissioning', null, 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 400, 'Demolition', 'Demolition', null, 400, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 500, 'Due Diligence', 'Due Diligence', null, 500, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 600, 'Feasibility Study', 'Feasibility Study', null, 600, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 700, 'Maintenance', 'Maintenance', null, 700, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 800, 'Master Plan', 'Master Plan', null, 800, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 900, 'New Construction', 'New Construction', null, 900, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 1000, 'Operations', 'Operations', null, 1000, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 1100, 'Refit', 'Refit', null, 1100, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'project_type_code', 1200, 'Renovation', 'Renovation', null, 1200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 0, 'None', null, null, 0, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 100, 'Agriculture', 'Agriculture', null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 200, 'Clean Facility - Bio', 'Clean Facility - Bio', null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 300, 'Clean Facility - Electronics', 'Clean Facility - Electronics', null, 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 400, 'Commercial', 'Commercial', null, 400, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 500, 'Distribution', 'Distribution', null, 500, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 600, 'Education', 'Education', null, 600, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 700, 'Environmental', 'Environmental', null, 700, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 800, 'Food Processing', 'Food Processing', null, 800, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 900, 'Healthcare', 'Healthcare', null, 900, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 1000, 'Industrial', 'Industrial', null, 1000, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 1100, 'Land Planning', 'Land Planning', null, 1100, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 1200, 'Manufacturing', 'Manufacturing', null, 1200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 1300, 'Marine', 'Marine', null, 1300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 1400, 'Office', 'Office', null, 1400, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 1500, 'Power', 'Power', null, 1500, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 1600, 'Residential - Multi', 'Residential - Multi', null, 1600, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 1700, 'Residential - Single', 'Residential - Single', null, 1700, 0, 'A')
/
commit
/
-- prompt  100 records committed...
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 1800, 'Retail', 'Retail', null, 1800, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 1900, 'Process Plant', 'Process Plant', null, 1900, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 2000, 'Pharmaceutical', 'Pharmaceutical', null, 2000, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 2100, 'Sports/Entertainment', 'Sports/Entertainment', null, 2100, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 2200, 'Transportation', 'Transportation', null, 2200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 2300, 'Utility', 'Utility', null, 2300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'use_classification_code', 2400, 'Wholesale', 'Wholesale', null, 2400, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'site_condition_code', 0, 'None', null, null, 0, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'site_condition_code', 100, 'Greenfield', 'Greenfield', null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'site_condition_code', 200, 'Brownfield', 'Brownfield', null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'site_condition_code', 300, 'Existing', 'Existing', null, 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'services_code', 100, 'Architecture Only', null, null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'services_code', 200, 'Engineering Only', null, null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'services_code', 300, 'Achitecture/Engineering', null, null, 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'services_code', 400, 'EPC', null, null, 400, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'services_code', 500, 'EPCM', null, null, 500, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'services_code', 600, 'EPCMV', null, null, 600, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'services_code', 700, 'CM Only', null, null, 700, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('aec_property_group', 'services_code', 800, 'Program Management', null, null, 800, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_news', 'priority_id', 200, '@prm.news.priority.normal.name', 'Normal priority', null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_news', 'priority_id', 100, '@prm.news.priority.high.name', 'High priority', null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_news', 'priority_id', 300, '@prm.news.priority.low.name', 'Low priority', null, 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_envelope_version', 'priority_id', 100, '@prm.workflow.envelope.version.priority.high.name', 'High priority', null, 200, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_envelope_version', 'priority_id', 200, '@prm.workflow.envelope.version.priority.normal.name', 'Normal priority', null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_envelope_version', 'priority_id', 300, '@prm.workflow.envelope.version.priority.low.name', 'Low priority', null, 300, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'status_code_id', 500, '@prm.project.status.proposed.name', 'Project have been Proposed', null, 500, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'status_code_id', 600, '@prm.project.status.inplanning.name', 'Project is in the Planning stage ', null, 600, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_project_space', 'visibility_id', 100, '@prm.project.visibility.business.name', 'Business Space Visibility', null, 100, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_assignment', 'status_id', 20, '@prm.global.assignment.status.accepted.name', 'Assigned', null, 20, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_deliverable', 'status_id', 20, '@prm.process.deliverable.status.inprocess.name', 'Work is currently be done.', null, 20, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_deliverable', 'status_id', 50, '@prm.process.deliverable.status.waived.name', 'This deliverable is not required.', null, 50, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_task', 'status_id', 30, '@prm.schedule.task.status.completed.name', 'Task complete.', null, 30, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_task', 'priority', 20, '@prm.schedule.task.priority.medium.name', 'Medium Priority', null, 20, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_resource_assignment', 'status_id', 10, '@prm.schedule.resource.assignment.status.assigned.name', 'Assigned', null, 10, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_resource_assignment', 'status_id', 30, '@prm.schedule.resource.assignment.status.inprocess.name', 'Some work has been done', null, 30, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_resource_assignment', 'status_id', 60, '@prm.schedule.resource.assignment.status.delegated.name', 'Delegated', null, 60, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_resource_assignment', 'status_id', 80, '@prm.schedule.resource.assignment.status.returned.name', 'Needs additional work or rework.', null, 80, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'frequency_type_id', 30, '@prm.calendar.frequency.type.weekly.name', 'Once a week', null, 30, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'frequency_type_id', 60, '@prm.calendar.frequency.type.quarterly.name', 'Quarterly', null, 60, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'facility_type', 20, '@prm.calendar.facility.type.teleconference.name', 'Teleconference Facility', null, 20, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_calendar_event', 'event_type_id', 30, '@prm.calendar.event.type.milestone.name', 'Milestone', null, 30, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_agenda_item', 'status_id', 10, '@prm.calendar.agenda.status.scheduled.name', 'Scheduled for the Meeting', null, 10, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_cal_event_has_attendee', 'status_id', 10, '@prm.calendar.event.attendee.status.invited.name', 'Scheduled for the Meeting', null, 10, 1, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_cal_event_has_attendee', 'status_id', 30, '@prm.calendar.event.attendee.status.declinedinvitation.name', 'Not covered, postoned to a future meeting', null, 30, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_assignment', 'status_id', 40, '@prm.global.assignment.status.completedconfirmationpending.name', 'Completed the assignment, assignor''s confirmation', null, 40, 0, 'A')
/
insert into PN_GLOBAL_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, CODE_NAME, CODE_DESC, CODE_URL, PRESENTATION_SEQUENCE, IS_DEFAULT, RECORD_STATUS)
values ('pn_assignment', 'status_id', 80, '@prm.global.assignment.status.returned.name', 'Needs additional work or rework', null, 80, 0, 'A')
/
commit
/
-- prompt  145 records loaded
-- prompt  Loading PN_GROUP_ACTION_LOOKUP...
insert into PN_GROUP_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('role_create', 'A')
/
insert into PN_GROUP_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('role_modify', 'A')
/
insert into PN_GROUP_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('role_remove', 'A')
/
commit
/
-- prompt  3 records loaded
-- prompt  Loading PN_GROUP_HAS_PERSON...
insert into PN_GROUP_HAS_PERSON (GROUP_ID, PERSON_ID)
values (5000, 1)
/
insert into PN_GROUP_HAS_PERSON (GROUP_ID, PERSON_ID)
values (5001, 1)
/
insert into PN_GROUP_HAS_PERSON (GROUP_ID, PERSON_ID)
values (5002, 1)
/
insert into PN_GROUP_HAS_PERSON (GROUP_ID, PERSON_ID)
values (5005, 1)
/
insert into PN_GROUP_HAS_PERSON (GROUP_ID, PERSON_ID)
values (5006, 1)
/
insert into PN_GROUP_HAS_PERSON (GROUP_ID, PERSON_ID)
values (5007, 1)
/
insert into PN_GROUP_HAS_PERSON (GROUP_ID, PERSON_ID)
values (5008, 1)
/
insert into PN_GROUP_HAS_PERSON (GROUP_ID, PERSON_ID)
values (5020, 1)
/
insert into PN_GROUP_HAS_PERSON (GROUP_ID, PERSON_ID)
values (5022, 1)
/
insert into PN_GROUP_HAS_PERSON (GROUP_ID, PERSON_ID)
values (5023, 1)
/
commit
/
-- prompt  10 records loaded
-- prompt  Loading PN_INDUSTRY_CLASSIFICATION...
insert into PN_INDUSTRY_CLASSIFICATION (INDUSTRY_ID, NAME, DESCRIPTION)
values (100, 'Finance', 'Financial Industry')
/
insert into PN_INDUSTRY_CLASSIFICATION (INDUSTRY_ID, NAME, DESCRIPTION)
values (200, 'Information Technology', 'IT Industry')
/
commit
/
-- prompt  2 records loaded
-- prompt  Loading PN_INDUSTRY_HAS_CATEGORY...
insert into PN_INDUSTRY_HAS_CATEGORY (INDUSTRY_ID, CATEGORY_ID)
values (100, 100)
/
insert into PN_INDUSTRY_HAS_CATEGORY (INDUSTRY_ID, CATEGORY_ID)
values (100, 101)
/
insert into PN_INDUSTRY_HAS_CATEGORY (INDUSTRY_ID, CATEGORY_ID)
values (200, 200)
/
insert into PN_INDUSTRY_HAS_CATEGORY (INDUSTRY_ID, CATEGORY_ID)
values (200, 201)
/
insert into PN_INDUSTRY_HAS_CATEGORY (INDUSTRY_ID, CATEGORY_ID)
values (200, 202)
/
commit
/
-- prompt  5 records loaded
-- prompt  Loading PN_JOB_DESCRIPTION_LOOKUP...
insert into PN_JOB_DESCRIPTION_LOOKUP (JOB_DESCRIPTION_CODE, JOB_DESCRIPTION)
values (100, 'Engineer')
/
insert into PN_JOB_DESCRIPTION_LOOKUP (JOB_DESCRIPTION_CODE, JOB_DESCRIPTION)
values (200, 'Drafter')
/
insert into PN_JOB_DESCRIPTION_LOOKUP (JOB_DESCRIPTION_CODE, JOB_DESCRIPTION)
values (300, 'Designer')
/
insert into PN_JOB_DESCRIPTION_LOOKUP (JOB_DESCRIPTION_CODE, JOB_DESCRIPTION)
values (400, 'Contractor')
/
insert into PN_JOB_DESCRIPTION_LOOKUP (JOB_DESCRIPTION_CODE, JOB_DESCRIPTION)
values (500, 'Sub-contractor')
/
insert into PN_JOB_DESCRIPTION_LOOKUP (JOB_DESCRIPTION_CODE, JOB_DESCRIPTION)
values (600, 'Sales Representative')
/
insert into PN_JOB_DESCRIPTION_LOOKUP (JOB_DESCRIPTION_CODE, JOB_DESCRIPTION)
values (700, 'Manager')
/
insert into PN_JOB_DESCRIPTION_LOOKUP (JOB_DESCRIPTION_CODE, JOB_DESCRIPTION)
values (999, 'Other')
/
insert into PN_JOB_DESCRIPTION_LOOKUP (JOB_DESCRIPTION_CODE, JOB_DESCRIPTION)
values (90, 'Architect')
/
commit
/
-- prompt  9 records loaded
-- prompt  Loading PN_LOGIN_HISTORY...
insert into PN_LOGIN_HISTORY (PERSON_ID, LOGIN_DATE, LOGIN_NAME_USED, LOGIN_CONCURRENCY)
values (1, to_date('20-05-2008 16:29:27', 'dd-mm-yyyy hh24:mi:ss'), 'appadmin', 1)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_MODULE...
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (10, 'document', '@prm.document.module.description', 65535)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (20, 'discussion', '@prm.discussion.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (30, 'form', '@prm.form.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (40, 'process', '@prm.process.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (50, 'roster', '@prm.roster.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (60, 'schedule', '@prm.schedule.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (70, 'calendar', '@prm.calendar.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (90, 'budget', '@prm.budget.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (100, 'metric', '@prm.metric.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (110, 'news', '@prm.news.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (120, 'weather', '@prm.weather.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (130, 'resource', '@prm.resource.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (140, 'directory', '@prm.directory.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (150, 'project_space', '@prm.projectspace.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (160, 'personal_space', '@prm.personalspace.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (170, 'business_space', '@prm.businessspace.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (180, 'security', '@prm.security.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (190, 'methodology_space', '@prm.methodologyspace.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (200, 'workflow', '@prm.workflow.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (210, 'vote', '@prm.vote.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (240, 'application_space', '@prm.applicationspace.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (250, 'configuration_space', '@prm.configurationspace.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (300, 'user_domain', '@prm.userdomain.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (310, 'report', '@prm.report.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (320, 'enterprise_space', '@prm.enterprise.module.description', 65535)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (330, 'trashcan', '@prm.trashcan.module.description', 65535)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (360, 'resource_space', '@prm.resource.module.description', 1)
/
insert into PN_MODULE (MODULE_ID, NAME, DESCRIPTION, DEFAULT_PERMISSION_ACTIONS)
values (340, 'wiki', '@prm.wiki.module.description', 207)
/
commit
/
-- prompt  28 records loaded
-- prompt  Loading PN_SPACE_HAS_MODULE...
insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
values (4, 140, 1)
/
insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
values (4, 240, 1)
/
insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
values (4, 250, 1)
/
insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
values (5004, 140, 1)
/
insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
values (5004, 250, 1)
/
insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
values (4, 30, 1)
/
insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
values (5004, 30, 1)
/
insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
values (5, 140, 1)
/
insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
values (5, 310, 1)
/
insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
values (5, 320, 1)
/
insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
values (1, 330, 1)
/
--insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
--values (10, 360, 1)
/
--insert into PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID, IS_ACTIVE)
--values (1, 360, 1)
/
commit
/
-- prompt  13 records loaded
-- prompt  Loading PN_MODULE_PERMISSION...
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (4, 5000, 140, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (4, 5000, 240, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (4, 5000, 250, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (4, 5002, 140, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (4, 5002, 240, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (4, 5002, 250, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5004, 5005, 140, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5004, 5005, 250, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5004, 5007, 140, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5004, 5007, 250, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (4, 5000, 30, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (4, 5001, 30, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (4, 5002, 30, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5004, 5005, 30, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5004, 5006, 30, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5004, 5007, 30, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5, 5020, 140, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5, 5020, 310, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5, 5020, 320, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5, 5021, 140, 7)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5, 5021, 310, 7)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5, 5021, 320, 7)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5, 5023, 140, 1)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5, 5023, 310, 1)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (5, 5023, 320, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (1, 5008, 330, 65535)
/
insert into PN_MODULE_PERMISSION (SPACE_ID, GROUP_ID, MODULE_ID, ACTIONS)
values (1, 5008, 360, 65535)
/
commit
/
-- prompt  27 records loaded
-- prompt  Loading PN_NEWS_ACTION_LOOKUP...
insert into PN_NEWS_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('news_create', 'A')
/
insert into PN_NEWS_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('news_modify', 'A')
/
insert into PN_NEWS_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('news_remove', 'A')
/
commit
/
-- prompt  3 records loaded
-- prompt  Loading PN_NEXT_DOC_REPOSITORY...
insert into PN_NEXT_DOC_REPOSITORY (REPOSITORY_SEQUENCE)
values (101)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_NOTIFICATION_DELIVERY_TYPE...
insert into PN_NOTIFICATION_DELIVERY_TYPE (DELIVERY_TYPE_ID, NAME, DESCRIPTION, RECORD_STATUS)
values (100, '@prm.notification.deliverytype.email.name', 'Send Notification via Email', 'A')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_OBJECT_NAME...
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5011, 'Application Administrator''s Personal Calendar')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (200, 'Column Selection')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (400, '@prm.global.form.elementproperty.column.domain.left')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (401, '@prm.global.form.elementproperty.column.domain.right')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (402, '@prm.global.form.elementproperty.column.domain.both')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5010, 'Top folder')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5009, 'default')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5000, '@prm.security.group.type.spaceadministrator.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5001, '@prm.security.group.type.principal.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5002, '@prm.security.group.type.teammember.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5005, '@prm.security.group.type.spaceadministrator.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5006, '@prm.security.group.type.principal.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5007, '@prm.security.group.type.teammember.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (100, '@prm.workflow.group.envelopecreator.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5008, '@prm.security.group.type.spaceadministrator.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5003, 'owner')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (2000, 'System Default')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5004, 'Project.net Configuration')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (1000, 'Global Domain')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5, '@prm.enterprise.enterprise.objecttype.description')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5020, '@prm.security.group.type.spaceadministrator.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5021, '@prm.security.group.type.poweruser.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5022, '@prm.security.group.type.principal.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (5023, '@prm.security.group.type.teammember.name')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (1, 'Application Administrator')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (3, 'Personal Portfolio')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (4, 'Application Administration')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (21, 'text element')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (22, 'textarea element')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (23, 'select menu element')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (24, 'date element')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (25, 'checkbox element')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (29, 'single person select menu element')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (31, 'milestone select element')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (40, 'horizontal separator')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (41, 'instruction')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (32, 'number element')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (33, 'currency element')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (26, '@prm.global.form.elementproperty.required.label')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (30, '@prm.global.form.elementproperty.required.label')
/
insert into PN_OBJECT_NAME (OBJECT_ID, NAME)
values (10, '@prm.resource.space.objecttype.description')
/
commit
/
-- prompt  41 records loaded
-- prompt  Loading PN_OBJECT_PERMISSION...
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5000, 5000, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5002, 5001, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5002, 5000, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5002, 5002, 1)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5005, 5005, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5007, 5006, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5007, 5005, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5007, 5007, 1)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5004, 5005, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5004, 5007, 7)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5008, 5008, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5010, 5008, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5011, 5008, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5020, 5020, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5021, 5020, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5023, 5022, 65535)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5023, 5021, 7)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5023, 5023, 1)
/
insert into PN_OBJECT_PERMISSION (OBJECT_ID, GROUP_ID, ACTIONS)
values (5023, 5020, 65535)
/
commit
/
-- prompt  19 records loaded
-- prompt  Loading PN_OBJECT_SPACE...
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (21, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (22, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (23, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (24, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (25, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (29, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (31, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (32, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (33, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (40, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (41, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (100, 4)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (100, 5)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (100, 5004)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5000, 4)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5001, 4)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5002, 4)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5005, 5004)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5006, 5004)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5007, 5004)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5008, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5010, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5011, 1)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5020, 5)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5021, 5)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5022, 5)
/
insert into PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID)
values (5023, 5)
/
commit
/
-- prompt  27 records loaded
-- prompt  Loading PN_SECURITY_ACTION...
insert into PN_SECURITY_ACTION (ACTION_ID, NAME, DESCRIPTION, BIT_MASK)
values (10, 'view', '@prm.security.action.view.description', 1)
/
insert into PN_SECURITY_ACTION (ACTION_ID, NAME, DESCRIPTION, BIT_MASK)
values (20, 'modify', '@prm.security.action.modify.description', 2)
/
insert into PN_SECURITY_ACTION (ACTION_ID, NAME, DESCRIPTION, BIT_MASK)
values (30, 'create', '@prm.security.action.create.description', 4)
/
insert into PN_SECURITY_ACTION (ACTION_ID, NAME, DESCRIPTION, BIT_MASK)
values (40, 'remove', '@prm.security.action.remove.description', 8)
/
insert into PN_SECURITY_ACTION (ACTION_ID, NAME, DESCRIPTION, BIT_MASK)
values (70, 'modify_permission', '@prm.security.action.modifypermission.description', 64)
/
insert into PN_SECURITY_ACTION (ACTION_ID, NAME, DESCRIPTION, BIT_MASK)
values (80, 'share', '@prm.security.action.share.description', 128)
/
insert into PN_SECURITY_ACTION (ACTION_ID, NAME, DESCRIPTION, BIT_MASK)
values (90, 'list_Deleted', '@prm.security.action.listdeleted.description', 256)
/
insert into PN_SECURITY_ACTION (ACTION_ID, NAME, DESCRIPTION, BIT_MASK)
values (100, 'remove_Deleted', '@prm.security.action.removedeleted.description', 512)
/
insert into PN_SECURITY_ACTION (ACTION_ID, NAME, DESCRIPTION, BIT_MASK)
values (110, 'undo_delete', '@prm.security.action.undodeleted.description', 1024)
/
commit
/
-- prompt  9 records loaded
-- prompt  Loading PN_OBJECT_TYPE_SUPPORTS_ACTION...
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('business', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form_domain', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form_domain', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form_domain', 70, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form_field', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form_field', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form_field', 70, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form_data', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form_data', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form_data', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form_data', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('form_data', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('deliverable', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('deliverable', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('deliverable', 40, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('deliverable', 70, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('discussion_group', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('discussion_group', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('discussion_group', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('discussion_group', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('discussion_group', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('doc_container', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('doc_container', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('doc_container', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('doc_container', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('doc_container', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('document', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('document', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('document', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('document', 70, 70)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('document_version', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('document_version', 70, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('gate', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('gate', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('gate', 40, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('gate', 70, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('group', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('group', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('group', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('group', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('meeting', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('meeting', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('meeting', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('meeting', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('event', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('event', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('event', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('event', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('task', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('task', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('task', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('task', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('phase', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('phase', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('phase', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('phase', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('phase', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('portfolio', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('portfolio', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('portfolio', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('portfolio', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('portfolio', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('process', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('process', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('process', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('process', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('process', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('project', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('project', 70, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('workflow', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('workflow', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('workflow', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('workflow', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('workflow', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('news', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('news', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('news', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('news', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('news', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('application', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('application', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('application', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('application', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('application', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('configuration', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('configuration', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('configuration', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('configuration', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('configuration', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('task', 80, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('plan', 80, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('doc_container', 90, 60)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('doc_container', 100, 70)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('doc_container', 110, 80)
/
commit
/
-- prompt  100 records committed...
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('document', 90, 90)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('document', 100, 100)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('post', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('post', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('post', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('post', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('post', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('enterprise', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('enterprise', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('enterprise', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('enterprise', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('enterprise', 70, 50)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('timesheet', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('timesheet', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('timesheet', 30, 30)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('timesheet', 40, 40)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('activity', 10, 10)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('activity', 20, 20)
/
insert into PN_OBJECT_TYPE_SUPPORTS_ACTION (OBJECT_TYPE, ACTION_ID, PRESENTATION_SEQ)
values ('activity', 30, 30)
/
commit
/
-- prompt  118 records loaded
-- prompt  Loading PN_PAYMENT_MODEL_TYPE...
insert into PN_PAYMENT_MODEL_TYPE (MODEL_TYPE_ID, CLASS_NAME, DESCRIPTION)
values (100, 'net.project.billing.payment.ChargeCode', '@prm.billing.payment.chargecode.description')
/
insert into PN_PAYMENT_MODEL_TYPE (MODEL_TYPE_ID, CLASS_NAME, DESCRIPTION)
values (200, 'net.project.billing.payment.CreditCard', '@prm.billing.payment.creditcard.description')
/
insert into PN_PAYMENT_MODEL_TYPE (MODEL_TYPE_ID, CLASS_NAME, DESCRIPTION)
values (300, 'net.project.billing.payment.Trial', '@prm.billing.payment.trial.description')
/
commit
/
-- prompt  3 records loaded
-- prompt  Loading PN_PERSON_AUTHENTICATOR...
insert into PN_PERSON_AUTHENTICATOR (PERSON_ID, AUTHENTICATOR_ID)
values (1, 1)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_PERSON_NOTIFICATION_ADDRESS...
insert into PN_PERSON_NOTIFICATION_ADDRESS (PERSON_ID, DELIVERY_TYPE_ID, DELIVERY_ADDRESS, IS_DEFAULT)
values (1, 100, 'admin@project.net', 1)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_PERSON_PROFILE...
insert into PN_PERSON_PROFILE (PERSON_ID, PREFIX_NAME, MIDDLE_NAME, SECOND_LAST_NAME, SUFFIX_NAME, COMPANY_NAME, COMPANY_DIVISION, JOB_DESCRIPTION_CODE, ADDRESS_ID, LANGUAGE_CODE, TIMEZONE_CODE, PERSONAL_SPACE_NAME, VERIFICATION_CODE, ALTERNATE_EMAIL_1, ALTERNATE_EMAIL_2, ALTERNATE_EMAIL_3, LOCALE_CODE)
values (1, null, null, null, null, 'Project.net', null, null, 2, 'en', 'PST', 'Application Administrator', null, null, null, null, 'en_US')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_PORTFOLIO...
insert into PN_PORTFOLIO (PORTFOLIO_ID, PORTFOLIO_NAME, PORTFOLIO_DESC, PORTFOLIO_TYPE, CONTENT_TYPE, RECORD_STATUS)
values (3, 'Personal Portfolio', 'Personal Portfolio', 'static', null, 'A')
/
insert into PN_PORTFOLIO (PORTFOLIO_ID, PORTFOLIO_NAME, PORTFOLIO_DESC, PORTFOLIO_TYPE, CONTENT_TYPE, RECORD_STATUS)
values (5003, 'owner', 'Configurations owned by this application space', 'static', 'configuration', 'A')
/
commit
/
-- prompt  2 records loaded
-- prompt  Loading PN_PORTFOLIO_HAS_SPACE...
insert into PN_PORTFOLIO_HAS_SPACE (PORTFOLIO_ID, SPACE_ID, IS_PRIVATE)
values (3, 5004, null)
/
insert into PN_PORTFOLIO_HAS_SPACE (PORTFOLIO_ID, SPACE_ID, IS_PRIVATE)
values (5003, 5004, null)
/
commit
/
-- prompt  2 records loaded
-- prompt  Loading PN_POST_ACTION_LOOKUP...
insert into PN_POST_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('post_create', 'A')
/
insert into PN_POST_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('post_modify', 'A')
/
insert into PN_POST_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('post_remove', 'A')
/
insert into PN_POST_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('reply_create', 'A')
/
commit
/
-- prompt  4 records loaded
-- prompt  Loading PN_PROF_CERT_LOOKUP...
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (100, 'AIA', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (200, 'FAIA', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (300, 'CSI', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (400, 'FCSI', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (500, 'CCS', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (600, 'CCCA', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (700, 'CDT', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (800, 'ASLA', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (900, 'FASLA', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (110, 'DBIA', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (120, 'PE', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (130, 'RA', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (140, 'CPM', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (150, 'Ph.D', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (160, 'MD', null)
/
insert into PN_PROF_CERT_LOOKUP (PROF_CERT_CODE, PROF_CERT_NAME, PROF_CERT_DESCRIPTION)
values (170, 'CPA', null)
/
commit
/
-- prompt  16 records loaded
-- prompt  Loading PN_PROJECT_SPACE_META_PROP...
insert into PN_PROJECT_SPACE_META_PROP (PROPERTY_ID, PROPERTY_NAME, PROPERTY_TYPE)
values (1, 'ExternalProjectID', 1)
/
insert into PN_PROJECT_SPACE_META_PROP (PROPERTY_ID, PROPERTY_NAME, PROPERTY_TYPE)
values (2, 'ProjectManager', 1)
/
insert into PN_PROJECT_SPACE_META_PROP (PROPERTY_ID, PROPERTY_NAME, PROPERTY_TYPE)
values (3, 'ProgramManager', 1)
/
insert into PN_PROJECT_SPACE_META_PROP (PROPERTY_ID, PROPERTY_NAME, PROPERTY_TYPE)
values (4, 'Initiative', 1)
/
insert into PN_PROJECT_SPACE_META_PROP (PROPERTY_ID, PROPERTY_NAME, PROPERTY_TYPE)
values (5, 'ProjectCharter', 1)
/
insert into PN_PROJECT_SPACE_META_PROP (PROPERTY_ID, PROPERTY_NAME, PROPERTY_TYPE)
values (6, 'FunctionalArea', 3)
/
insert into PN_PROJECT_SPACE_META_PROP (PROPERTY_ID, PROPERTY_NAME, PROPERTY_TYPE)
values (7, 'TypeOfExpense', 3)
/
commit
/
-- prompt  7 records loaded
-- prompt  Loading PN_PROPERTY_CATEGORY...
insert into PN_PROPERTY_CATEGORY (CATEGORY_ID, NAME, DESCRIPTION)
values (100, 'Basic Branding', 'All properties necessary for basic branding and brand-specific configuration')
/
insert into PN_PROPERTY_CATEGORY (CATEGORY_ID, NAME, DESCRIPTION)
values (200, 'Logo', 'All properties specific for logos and logo configuration')
/
insert into PN_PROPERTY_CATEGORY (CATEGORY_ID, NAME, DESCRIPTION)
values (300, 'Legal', 'All properties relating to legal information')
/
insert into PN_PROPERTY_CATEGORY (CATEGORY_ID, NAME, DESCRIPTION)
values (400, 'Footer', 'All properties for the footer')
/
insert into PN_PROPERTY_CATEGORY (CATEGORY_ID, NAME, DESCRIPTION)
values (500, 'Project Create', 'All properties relating to project creation')
/
commit
/
-- prompt  5 records loaded
-- prompt  Loading PN_PROPERTY_SHEET_TYPE...
insert into PN_PROPERTY_SHEET_TYPE (PROPERTY_SHEET_TYPE, PROPERTY_SHEET_NAME, PROPERTY_SHEET_DESC, PROPERTIES_TABLE_NAME)
values (100, 'AEC Project Properties', 'Properties for AEC Projects', 'AEC_PROPERTY_GROUP')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_PROP_CATEGORY_HAS_PROPERTY...
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.application.title')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.brand.abbreviation')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.brand.defaultcountrycode')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.brand.defaultlanguagecode')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.brand.defaulttimezone')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.brand.legal-name')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.brand.logo.login')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.brand.logo.login.href')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.brand.name')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.css.application')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.css.business')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.css.configuration')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.css.login')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.css.methodology')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.css.personal')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.css.project')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.css.registration')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.header.banner.image')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.header.business.image.off')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.header.business.image.on')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.header.personal.image.off')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.header.personal.image.on')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.header.project.image.off')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.header.project.image.on')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.login.channel.login.title')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.login.pagetitle')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (100, 'prm.global.poweredby.isenabled')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (200, 'prm.global.brand.logo.login')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (200, 'prm.global.brand.logo.login.href')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (200, 'prm.global.poweredby.logo.login.display')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (300, 'prm.global.legal.eula.href')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (300, 'prm.global.legal.termsofuse')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (300, 'prm.global.legal.termsofuse.projectcreate')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (300, 'prm.global.legal.termsofuse.registration')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.copyright')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.copyright.href')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.copyright.href.isenabled')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.copyright.line2')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.copyright.line2.href')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.copyright.line2.href.isenabled')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.copyright.line2.isenabled')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.copyright.line2.newline')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.copyright.newline')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.poweredby')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.poweredby.href')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.footer.poweredby.href.isenabled')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (400, 'prm.global.poweredby.isenabled')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.authcode.authorizedusing')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.authcode.enter')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.authcode.info1')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.authcode.isenabled')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.authcode.type.evaluation')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.authcode.type.pnet')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.applymethodology')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.businessowner')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.businessowner.none')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.colorcode')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.completionpercent')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.description')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.description.heading')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.enddate')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.general.heading')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.methodology')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.methodology.none')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.name')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.otherproperties.heading')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.projectstatus.heading')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.properties.heading')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.startdate')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.status')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.title')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.title.step2')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.create.wizard.title.step3')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.project.businessowner')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.project.colorcode')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.project.completionpercent')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.project.description')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.project.enddate')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.project.name')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.project.startdate')
/
insert into PN_PROP_CATEGORY_HAS_PROPERTY (CATEGORY_ID, PROPERTY)
values (500, 'prm.project.project.status')
/
commit
/
-- prompt  81 records loaded
-- prompt  Loading PN_REPORT_SEQUENCE...
insert into PN_REPORT_SEQUENCE (REPORT_TYPE, SEQUENCE)
values ('ltr', 1)
/
insert into PN_REPORT_SEQUENCE (REPORT_TYPE, SEQUENCE)
values ('tcdr', 2)
/
insert into PN_REPORT_SEQUENCE (REPORT_TYPE, SEQUENCE)
values ('rar', 3)
/
insert into PN_REPORT_SEQUENCE (REPORT_TYPE, SEQUENCE)
values ('wcr', 4)
/
insert into PN_REPORT_SEQUENCE (REPORT_TYPE, SEQUENCE)
values ('oar', 5)
/
insert into PN_REPORT_SEQUENCE (REPORT_TYPE, SEQUENCE)
values ('str', 6)
/
insert into PN_REPORT_SEQUENCE (REPORT_TYPE, SEQUENCE)
values ('fisr', 7)
/
insert into PN_REPORT_SEQUENCE (REPORT_TYPE, SEQUENCE)
values ('fits', 8)
/
insert into PN_REPORT_SEQUENCE (REPORT_TYPE, SEQUENCE)
values ('nur', 9)
/
commit
/
-- prompt  9 records loaded
-- prompt  Loading PN_SPACE_HAS_CALENDAR...
insert into PN_SPACE_HAS_CALENDAR (SPACE_ID, CALENDAR_ID)
values (1, 5011)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_SPACE_HAS_DIRECTORY...
insert into PN_SPACE_HAS_DIRECTORY (SPACE_ID, DIRECTORY_ID)
values (4, 100)
/
insert into PN_SPACE_HAS_DIRECTORY (SPACE_ID, DIRECTORY_ID)
values (5, 100)
/
insert into PN_SPACE_HAS_DIRECTORY (SPACE_ID, DIRECTORY_ID)
values (5004, 100)
/
commit
/
-- prompt  3 records loaded
-- prompt  Loading PN_SPACE_HAS_DOC_SPACE...
insert into PN_SPACE_HAS_DOC_SPACE (SPACE_ID, DOC_SPACE_ID, IS_OWNER)
values (1, 5009, 1)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_SPACE_HAS_GROUP...
insert into PN_SPACE_HAS_GROUP (SPACE_ID, GROUP_ID, IS_OWNER)
values (4, 5000, 1)
/
insert into PN_SPACE_HAS_GROUP (SPACE_ID, GROUP_ID, IS_OWNER)
values (4, 5001, 1)
/
insert into PN_SPACE_HAS_GROUP (SPACE_ID, GROUP_ID, IS_OWNER)
values (4, 5002, 1)
/
insert into PN_SPACE_HAS_GROUP (SPACE_ID, GROUP_ID, IS_OWNER)
values (5004, 5005, 1)
/
insert into PN_SPACE_HAS_GROUP (SPACE_ID, GROUP_ID, IS_OWNER)
values (5004, 5006, 1)
/
insert into PN_SPACE_HAS_GROUP (SPACE_ID, GROUP_ID, IS_OWNER)
values (5004, 5007, 1)
/
insert into PN_SPACE_HAS_GROUP (SPACE_ID, GROUP_ID, IS_OWNER)
values (1, 5008, 1)
/
insert into PN_SPACE_HAS_GROUP (SPACE_ID, GROUP_ID, IS_OWNER)
values (5, 5020, 1)
/
insert into PN_SPACE_HAS_GROUP (SPACE_ID, GROUP_ID, IS_OWNER)
values (5, 5021, 1)
/
insert into PN_SPACE_HAS_GROUP (SPACE_ID, GROUP_ID, IS_OWNER)
values (5, 5022, 1)
/
insert into PN_SPACE_HAS_GROUP (SPACE_ID, GROUP_ID, IS_OWNER)
values (5, 5023, 1)
/
commit
/
-- prompt  11 records loaded
-- prompt  Loading PN_SPACE_HAS_PERSON...
insert into PN_SPACE_HAS_PERSON (SPACE_ID, PERSON_ID, RELATIONSHIP_PERSON_TO_SPACE, MEMBER_TYPE_ID, RELATIONSHIP_SPACE_TO_PERSON, RESPONSIBILITIES, MEMBER_TITLE, RECORD_STATUS, SECURE_KEY)
values (4, 1, 'member', null, null, null, null, 'A', null)
/
insert into PN_SPACE_HAS_PERSON (SPACE_ID, PERSON_ID, RELATIONSHIP_PERSON_TO_SPACE, MEMBER_TYPE_ID, RELATIONSHIP_SPACE_TO_PERSON, RESPONSIBILITIES, MEMBER_TITLE, RECORD_STATUS, SECURE_KEY)
values (5004, 1, 'member', null, null, null, null, 'A', null)
/
insert into PN_SPACE_HAS_PERSON (SPACE_ID, PERSON_ID, RELATIONSHIP_PERSON_TO_SPACE, MEMBER_TYPE_ID, RELATIONSHIP_SPACE_TO_PERSON, RESPONSIBILITIES, MEMBER_TITLE, RECORD_STATUS, SECURE_KEY)
values (1, 1, 'Person''s Personal Space', null, null, null, null, 'A', null)
/
insert into PN_SPACE_HAS_PERSON (SPACE_ID, PERSON_ID, RELATIONSHIP_PERSON_TO_SPACE, MEMBER_TYPE_ID, RELATIONSHIP_SPACE_TO_PERSON, RESPONSIBILITIES, MEMBER_TITLE, RECORD_STATUS, SECURE_KEY)
values (5, 1, 'member', null, null, null, null, 'A', null)
/
insert into PN_SPACE_HAS_PERSON (SPACE_ID, PERSON_ID, RELATIONSHIP_PERSON_TO_SPACE, MEMBER_TYPE_ID, RELATIONSHIP_SPACE_TO_PERSON, RESPONSIBILITIES, MEMBER_TITLE, RECORD_STATUS, SECURE_KEY)
values (10, 1, 'member', null, null, null, null, 'A', null)
/
commit
/
-- prompt  5 records loaded
-- prompt  Loading PN_SPACE_HAS_PORTFOLIO...
insert into PN_SPACE_HAS_PORTFOLIO (SPACE_ID, PORTFOLIO_ID, IS_DEFAULT)
values (4, 5003, 1)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_SPACE_TYPE_HAS_REPORT_TYPE...
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('business', 'psr')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('enterprise', 'ppr')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('enterprise', 'wcr')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('project', 'fisr')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('project', 'fits')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('project', 'ltr')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('project', 'nur')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('project', 'oar')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('project', 'psrs')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('project', 'rar')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('project', 'str')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('project', 'tcdr')
/
insert into PN_SPACE_TYPE_HAS_REPORT_TYPE (SPACE_TYPE, REPORT_TYPE)
values ('project', 'wcr')
/
commit
/
-- prompt  13 records loaded
-- prompt  Loading PN_SPAM_LOOKUP...
insert into PN_SPAM_LOOKUP (SPAM_TYPE_CODE, SPAM_TYPE)
values (100, 'Information')
/
insert into PN_SPAM_LOOKUP (SPAM_TYPE_CODE, SPAM_TYPE)
values (200, 'Product Information')
/
insert into PN_SPAM_LOOKUP (SPAM_TYPE_CODE, SPAM_TYPE)
values (300, 'Partner Information')
/
commit
/
-- prompt  3 records loaded
-- prompt  Loading PN_SP_ERROR_CODES...
insert into PN_SP_ERROR_CODES (ERROR_CODE, ERROR_NAME, ERROR_DESCRIPTION)
values (0, 'Success', 'Operation completed successfully')
/
insert into PN_SP_ERROR_CODES (ERROR_CODE, ERROR_NAME, ERROR_DESCRIPTION)
values (101, 'Generic Error', 'Generic Database Error - See SP_ERROR_LOG table')
/
insert into PN_SP_ERROR_CODES (ERROR_CODE, ERROR_NAME, ERROR_DESCRIPTION)
values (102, 'No Data Found', 'No Data Found')
/
insert into PN_SP_ERROR_CODES (ERROR_CODE, ERROR_NAME, ERROR_DESCRIPTION)
values (103, 'Duplicate Key', 'Unique constraint in key')
/
insert into PN_SP_ERROR_CODES (ERROR_CODE, ERROR_NAME, ERROR_DESCRIPTION)
values (104, 'Null Field', 'Tried to insert NULL in not null field')
/
insert into PN_SP_ERROR_CODES (ERROR_CODE, ERROR_NAME, ERROR_DESCRIPTION)
values (105, 'No Parent Key', 'No parent for foreign key')
/
insert into PN_SP_ERROR_CODES (ERROR_CODE, ERROR_NAME, ERROR_DESCRIPTION)
values (106, 'Check Violated', 'Invalid entry for column')
/
insert into PN_SP_ERROR_CODES (ERROR_CODE, ERROR_NAME, ERROR_DESCRIPTION)
values (107, 'Value Too Large', 'Value too large for column')
/
insert into PN_SP_ERROR_CODES (ERROR_CODE, ERROR_NAME, ERROR_DESCRIPTION)
values (120, 'Is Active', 'Can not delete because message is active status')
/
commit
/
-- prompt  9 records loaded
-- prompt  Loading PN_STATE_LOOKUP...
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('AL', 'Alabama', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('AK', 'Alaska', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('AS', 'American Samoa', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('AZ', 'Arizona', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('AR', 'Arkansas', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('CA', 'California', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('CO', 'Colorado', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('CT', 'Connecticut', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('DE', 'Delaware', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('DC', 'District of Columbia', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('FM', 'Federated States of Micronesia', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('FL', 'Florida', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('GA', 'Georgia', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('GU', 'Guam', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('HI', 'Hawaii', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('ID', 'Idaho', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('IL', 'Illinois', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('IN', 'Indiana', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('IA', 'Iowa', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('KS', 'Kansas', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('KY', 'Kentucky', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('LA', 'Lousiana', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('ME', 'Maine', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('MH', 'Marshall Islands', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('MD', 'Maryland', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('MA', 'Massachusetts', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('MI', 'Michigan', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('MN', 'Minnesota', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('MS', 'Mississippi', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('MO', 'Missouri', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('MT', 'Montana', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NE', 'Nebraska', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NV', 'Nevada', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NH', 'New Hampshire', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NJ', 'New Jersey', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NM', 'New Mexico', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NY', 'New York', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NC', 'North Carolina', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('ND', 'North Dakota', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('MP', 'Northern Mariana Islands', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('OH', 'Ohio', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('OK', 'Oklahoma', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('OR', 'Oregon', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('PW', 'Palau', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('PA', 'Pennsylvania', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('PR', 'Puerto Rico', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('RI', 'Rhode Island', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('SC', 'South Carolina', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('SD', 'South Dakota', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('TN', 'Tennessee', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('TX', 'Texas', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('UT', 'Utah', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('VT', 'Vermont', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('VI', 'Virgin Islands', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('VA', 'Virginia', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('WA', 'Washington', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('WV', 'West Virginia', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('WI', 'Wisconsin', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('WY', 'Wyoming', 'US')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('XX', '@prm.resource.statelookup.nonusstate.name', 'XX')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('PE', 'Prince Edward Island', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NB', 'New Brunswick', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NS', 'Nova Scotia', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NF', 'Newfoundland', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('QB', 'Quebec', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('ON', 'Ontario', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('MB', 'Manitoba', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('SK', 'Saskatchewan', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('AB', 'Alberta', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('BC', 'British Columbia', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NT', 'North West Territories', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('YT', 'Yukon Territories', 'CA')
/
insert into PN_STATE_LOOKUP (STATE_CODE, STATE_NAME, COUNTRY_CODE)
values ('NU', 'Nunavut', 'CA')
/
commit
/
-- prompt  73 records loaded
-- prompt  Loading PN_SUBSCRIPTION_RECURRENCE...
insert into PN_SUBSCRIPTION_RECURRENCE (RECURRENCE_ID, NAME, DESCRIPTION, RECORD_STATUS, CRC)
values (100, '@prm.notification.subscription.recurrence.immediately.name', 'notify as soon as event occurs', 'A', null)
/
insert into PN_SUBSCRIPTION_RECURRENCE (RECURRENCE_ID, NAME, DESCRIPTION, RECORD_STATUS, CRC)
values (200, '@prm.notification.subscription.recurrence.daily.name', 'notify every day', 'A', null)
/
insert into PN_SUBSCRIPTION_RECURRENCE (RECURRENCE_ID, NAME, DESCRIPTION, RECORD_STATUS, CRC)
values (300, '@prm.notification.subscription.recurrence.weekly.name', 'notify every week', 'A', null)
/
insert into PN_SUBSCRIPTION_RECURRENCE (RECURRENCE_ID, NAME, DESCRIPTION, RECORD_STATUS, CRC)
values (400, '@prm.notification.subscription.recurrence.monthly.name', 'notify every month', 'A', null)
/
commit
/
-- prompt  4 records loaded
-- prompt  Loading PN_TASK_ACTION_LOOKUP...
insert into PN_TASK_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('task_create', 'A')
/
insert into PN_TASK_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('task_modify', 'A')
/
insert into PN_TASK_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('task_remove', 'A')
/
insert into PN_TASK_ACTION_LOOKUP (ACTION, RECORD_STATUS)
values ('task_status_change', 'A')
/
commit
/
-- prompt  4 records loaded
-- prompt  Loading PN_TIMEZONE_LOOKUP...
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('MIT', null, '-11:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('HST', null, '-10:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('AST', null, '-09:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('PST', null, '-08:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('PNT', null, '-07:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('MST', null, '-07:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('CST', null, '-06:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('IET', null, '-05:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('EST', null, '-05:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('PRT', null, '-04:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('CNT', null, '-03:30')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('AGT', null, '-03:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('BET', null, '-03:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('GMT', null, '+00:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('UTC', null, '+00:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('ECT', null, '+01:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('CAT', null, '+02:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('ART', null, '+02:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('EET', null, '+02:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('EAT', null, '+03:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('MET', null, '+03:30')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('NET', null, '+04:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('PLT', null, '+05:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('IST', null, '+05:30')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('BST', null, '+06:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('VST', null, '+07:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('CTT', null, '+08:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('JST', null, '+09:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('AET', null, '+10:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('SST', null, '+11:00')
/
insert into PN_TIMEZONE_LOOKUP (TIMEZONE_CODE, TIMEZONE_DESCRIPTION, GMT_OFFSET)
values ('NST', null, '+12:00')
/
commit
/
-- prompt  31 records loaded
-- prompt  Loading PN_TIME_FORMAT...
insert into PN_TIME_FORMAT (TIME_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (10, 'h:mm a', 'HH:MM PM', '2:30 PM')
/
insert into PN_TIME_FORMAT (TIME_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (20, 'h:mm a z', 'HH:MM [AM/PM] [timezone]', '2:30 PM PST')
/
insert into PN_TIME_FORMAT (TIME_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (30, 'H:mm', 'HH:MM', '14:30')
/
insert into PN_TIME_FORMAT (TIME_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (40, 'H:mm z', 'HH:MM [timezone]', '14:30 PST')
/
insert into PN_TIME_FORMAT (TIME_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (50, 'a h:mm', '[AM/PM] HH:MM', 'PM 2:30')
/
insert into PN_TIME_FORMAT (TIME_FORMAT_ID, FORMAT_STRING, DISPLAY, EXAMPLE)
values (60, 'z a h:mm', '[timezone] [AM/PM] HH:MM', 'JST PM 2:30')
/
commit
/
-- prompt  6 records loaded
-- prompt  Loading PN_USER...
insert into PN_USER (USER_ID, USERNAME, DOMAIN_ID, RECORD_STATUS, IS_LOGIN, LAST_BRAND_ID, LAST_LOGIN)
values (1, 'appadmin', 1000, 'A', 0, null, null)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_USER_DEFAULT_CREDENTIALS...
insert into PN_USER_DEFAULT_CREDENTIALS (USER_ID, PASSWORD, JOG_PHRASE, JOG_ANSWER, DOMAIN_ID)
values (1, '8672387DD6391DBFF85FA516E3A5B406', '01035B6E96A1AAB8', 'F7847A913AF917FCC55AE7C64548CD1A', 1000)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_USER_DOMAIN...
INSERT INTO pn_user_domain (DOMAIN_ID,NAME,DESCRIPTION,RECORD_STATUS, DIRECTORY_PROVIDER_TYPE_ID, IS_VERIFICATION_REQUIRED, REGISTRATION_INSTRUCTIONS_CLOB, SUPPORTS_CREDIT_CARD_PURCHASES)
VALUES ('1000','Global Domain','Project.net global default domain', 'A', 1, 1,'{@prm.application.domain.defaultregistrationinstructions}', 1)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_USER_DOMAIN_SUPPORTS_CONFIG...
insert into PN_USER_DOMAIN_SUPPORTS_CONFIG (DOMAIN_ID, CONFIGURATION_ID)
values (1000, 5004)
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_WORKFLOW_RULE_STATUS...
insert into PN_WORKFLOW_RULE_STATUS (RULE_STATUS_ID, STATUS_NAME, STATUS_DESCRIPTION, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (100, '@prm.workflow.rule.status.enforced.name', 'The rule is enforced.', 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
insert into PN_WORKFLOW_RULE_STATUS (RULE_STATUS_ID, STATUS_NAME, STATUS_DESCRIPTION, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (200, '@prm.workflow.rule.status.disabled.name', 'The rule is disabled.', 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
commit
/
-- prompt  2 records loaded
-- prompt  Loading PN_WORKFLOW_RULE_TYPE...
insert into PN_WORKFLOW_RULE_TYPE (TABLE_NAME, RULE_TYPE_ID, RULE_TYPE_NAME, RULE_TYPE_DESCRIPTION, NOTES, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values ('pn_wf_rule_authorization', 100, 'Authorization', 'Indicates who is authorized to perform transition.', '@prm.workflow.rule.type.authorization.notes', 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading PN_WORKFLOW_STATUS...
insert into PN_WORKFLOW_STATUS (STATUS_ID, STATUS_NAME, STATUS_DESCRIPTION, IS_INACTIVE, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (100, '@prm.workflow.status.new.name', 'Workflow envelope is open', 0, 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
insert into PN_WORKFLOW_STATUS (STATUS_ID, STATUS_NAME, STATUS_DESCRIPTION, IS_INACTIVE, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (200, '@prm.workflow.status.inprogress.name', 'Workflow envelope is open and in progress', 0, 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
insert into PN_WORKFLOW_STATUS (STATUS_ID, STATUS_NAME, STATUS_DESCRIPTION, IS_INACTIVE, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (300, '@prm.workflow.status.suspended.name', 'Workflow envelope is open but suspended', 0, 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
insert into PN_WORKFLOW_STATUS (STATUS_ID, STATUS_NAME, STATUS_DESCRIPTION, IS_INACTIVE, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (400, '@prm.workflow.status.completedclosed.name', 'Workflow envelope is closed', 1, 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
insert into PN_WORKFLOW_STATUS (STATUS_ID, STATUS_NAME, STATUS_DESCRIPTION, IS_INACTIVE, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (500, '@prm.workflow.status.cancelledclosed.name', 'Workflow envelope is closed but was cancelled', 1, 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
commit
/
-- prompt  5 records loaded
-- prompt  Loading PN_WORKFLOW_STRICTNESS...
insert into PN_WORKFLOW_STRICTNESS (STRICTNESS_ID, STRICTNESS_NAME, STRICTNESS_DESCRIPTION, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (100, '@prm.workflow.strictness.relaxed.name', 'Relaxed rule enforcement', 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
insert into PN_WORKFLOW_STRICTNESS (STRICTNESS_ID, STRICTNESS_NAME, STRICTNESS_DESCRIPTION, CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, CRC, RECORD_STATUS)
values (200, '@prm.workflow.strictness.strict.name', 'Strict rule enforcement', 1, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), null, null, to_date('20-05-2008 16:28:57', 'dd-mm-yyyy hh24:mi:ss'), 'A')
/
commit
/
-- prompt  2 records loaded
-- prompt  Loading PRODUCT_VERSION...
insert into PRODUCT_VERSION (PRODUCT, MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, BUILD_VERSION, TIMESTAMP, DESCRIPTION)
values ('PRMServer', 2, 0, 0, 1, to_date('20-05-2008 16:28:55', 'dd-mm-yyyy hh24:mi:ss'), 'PRMServer v2.0 Final Build (22:55PST 3/31/01)')
/
commit
/
-- prompt  1 records loaded
-- prompt  Loading QRTZ_LOCKS...
insert into QRTZ_LOCKS (LOCK_NAME)
values ('CALENDAR_ACCESS')
/
insert into QRTZ_LOCKS (LOCK_NAME)
values ('JOB_ACCESS')
/
insert into QRTZ_LOCKS (LOCK_NAME)
values ('MISFIRE_ACCESS')
/
insert into QRTZ_LOCKS (LOCK_NAME)
values ('STATE_ACCESS')
/
insert into QRTZ_LOCKS (LOCK_NAME)
values ('TRIGGER_ACCESS')
/
commit
/
-- prompt  5 records loaded
-- prompt  Loading PN_PIVOT...
INSERT INTO PN_PIVOT
SELECT hunthous.x+tenthous.x+thous.x+huns.x+tens.x+ones.x from
(select 0 x from dual
  union select 1 from dual
  union select 2 from dual
  union select 3 from dual
  union select 4 from dual
  union select 5 from dual
  union select 6 from dual
  union select 7 from dual
  union select 8 from dual
  union select 9 from dual
) ones,
(select 0 x from dual
  union select 10 from dual
  union select 20 from dual
  union select 30 from dual
  union select 40 from dual
  union select 50 from dual
  union select 60 from dual
  union select 70 from dual
  union select 80 from dual
  union select 90 from dual
) tens,
(select 0 x from dual
  union select 100 from dual
  union select 200 from dual
  union select 300 from dual
  union select 400 from dual
  union select 500 from dual
  union select 600 from dual
  union select 700 from dual
  union select 800 from dual
  union select 900 from dual
) huns,
(select 0 x from dual
  union select 1000 from dual
  union select 2000 from dual
  union select 3000 from dual
  union select 4000 from dual
  union select 5000 from dual
  union select 6000 from dual
  union select 7000 from dual
  union select 8000 from dual
  union select 9000 from dual
) thous,
(select 0 x from dual
  union select 10000 from dual
  union select 20000 from dual
  union select 30000 from dual
  union select 40000 from dual
  union select 50000 from dual
  union select 60000 from dual
  union select 70000 from dual
  union select 80000 from dual
  union select 90000 from dual
) tenthous,
(select 0 x from dual
  union select 100000 from dual
  union select 200000 from dual
  union select 300000 from dual
  union select 400000 from dual
  union select 500000 from dual
  union select 600000 from dual
  union select 700000 from dual
  union select 800000 from dual
  union select 900000 from dual
) hunthous
/
commit
/
-- prompt  1000000 records loaded
-- prompt  Loading PN_CLASS_FIELD...
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 21, 21, null, 'field_label', 1, null, '@prm.global.form.elementproperty.fieldlabel.label', null, 0, 1, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 1)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 21, 22, null, 'row_num', 1, null, '@prm.global.form.elementproperty.row.label', null, 0, 2, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 21, 23, 200, 'column_id', 3, null, '@prm.global.form.elementproperty.column.label', null, 0, 2, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 21, 24, null, 'size', 1, null, '@prm.global.form.elementproperty.length.label', null, 0, 3, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 21, 25, null, 'data_column_size', 1, null, '@prm.global.form.elementproperty.maxlength.label', null, 0, 3, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 22, 21, null, 'field_label', 1, null, '@prm.global.form.elementproperty.fieldlabel.label', null, 0, 1, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 1)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 22, 22, null, 'row_num', 1, null, '@prm.global.form.elementproperty.row.label', null, 0, 2, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 22, 23, 200, 'column_id', 3, null, '@prm.global.form.elementproperty.column.label', null, 0, 2, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 22, 24, null, 'cols', 1, null, '@prm.global.form.elementproperty.length.label', null, 0, 3, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 22, 25, null, 'rows', 1, null, '@prm.global.form.elementproperty.height.label', null, 0, 3, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 23, 21, null, 'field_label', 1, null, '@prm.global.form.elementproperty.fieldlabel.label', null, 0, 1, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 1)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 23, 22, null, 'row_num', 1, null, '@prm.global.form.elementproperty.row.label', null, 0, 2, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 23, 23, 200, 'column_id', 3, null, '@prm.global.form.elementproperty.column.label', null, 0, 2, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 23, 24, null, 'is_multi_select', 5, null, '@prm.global.form.elementproperty.allowmultiple.label', null, 0, 3, 1, 1, 2, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 24, 21, null, 'field_label', 1, null, '@prm.global.form.elementproperty.fieldlabel.label', null, 0, 1, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 1)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 24, 22, null, 'row_num', 1, null, '@prm.global.form.elementproperty.row.label', null, 0, 2, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 24, 24, 200, 'column_id', 3, null, '@prm.global.form.elementproperty.column.label', null, 0, 2, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 24, 25, null, 'use_default', 5, null, '@prm.global.form.elementproperty.defaultcreationdate.label', null, 0, 4, 1, 1, 2, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 25, 21, null, 'field_label', 1, null, '@prm.global.form.elementproperty.fieldlabel.label', null, 0, 1, 1, 1, 2, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 1)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 25, 22, null, 'row_num', 1, null, '@prm.global.form.elementproperty.row.label', null, 0, 2, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 25, 23, 200, 'column_id', 3, null, '@prm.global.form.elementproperty.column.label', null, 0, 2, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 29, 21, null, 'field_label', 1, null, '@prm.global.form.elementproperty.fieldlabel.label', null, 0, 1, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 1)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 29, 22, null, 'row_num', 1, null, '@prm.global.form.elementproperty.row.label', null, 0, 2, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 29, 23, 200, 'column_id', 3, null, '@prm.global.form.elementproperty.column.label', null, 0, 2, 1, 2, 1, null, 0, null, 1, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 29, 24, null, 'is_multi_select', 5, null, '@prm.global.form.elementproperty.allowmultiple.label', null, 0, 3, 1, 1, 2, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 29, 25, null, 'use_default', 5, null, '@prm.global.form.elementproperty.defaultpersonsubmitting.label', null, 0, 4, 1, 1, 2, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 31, 21, null, 'field_label', 1, null, '@prm.global.form.elementproperty.fieldlabel.label', null, 0, 1, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 1)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 31, 22, null, 'row_num', 1, null, '@prm.global.form.elementproperty.row.label', null, 0, 2, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 31, 24, 200, 'column_id', 3, null, '@prm.global.form.elementproperty.column.label', null, 0, 2, 1, 2, 1, null, 0, null, 1, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 40, 21, null, 'field_label', 1, null, '@prm.global.form.elementproperty.fieldlabel.label', null, 0, 1, 1, 1, 2, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 1)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 40, 22, null, 'row_num', 1, null, '@prm.global.form.elementproperty.row.label', null, 0, 2, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 40, 24, 200, 'column_id', 3, null, '@prm.global.form.elementproperty.column.label', null, 0, 2, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 41, 21, null, 'field_label', 1, null, '@prm.global.form.elementproperty.instructiontitle.label', null, 0, 1, 1, 1, 2, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 1)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 41, 22, null, 'row_num', 1, null, '@prm.global.form.elementproperty.row.label', null, 0, 2, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 41, 24, 200, 'column_id', 3, null, '@prm.global.form.elementproperty.column.label', null, 0, 2, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 41, 25, 200, 'instructions_clob', 2, null, '@prm.global.form.elementproperty.instructiontext.label', null, 0, 3, 1, 1, 2, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 32, 21, null, 'field_label', 1, null, '@prm.global.form.elementproperty.fieldlabel.label', null, 0, 1, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 1)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 32, 22, null, 'row_num', 1, null, '@prm.global.form.elementproperty.row.label', null, 0, 2, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 32, 23, 200, 'column_id', 3, null, '@prm.global.form.elementproperty.column.label', null, 0, 2, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 32, 24, null, 'size', 1, null, '@prm.global.form.elementproperty.length.label', null, 0, 3, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 32, 25, null, 'data_column_size', 1, null, '@prm.global.form.elementproperty.totalnumberofdigits.label', null, 0, 4, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 32, 29, null, 'data_column_scale', 1, null, '@prm.global.form.elementproperty.numberofdecimaldigits.label', null, 0, 4, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 33, 21, null, 'field_label', 1, null, '@prm.global.form.elementproperty.fieldlabel.label', null, 0, 1, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 1)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 33, 22, null, 'row_num', 1, null, '@prm.global.form.elementproperty.row.label', null, 0, 2, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 33, 23, 200, 'column_id', 3, null, '@prm.global.form.elementproperty.column.label', null, 0, 2, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 33, 24, null, 'size', 1, null, '@prm.global.form.elementproperty.length.label', null, 0, 3, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 33, 25, null, 'data_column_size', 1, null, '@prm.global.form.elementproperty.totalnumberofdigits.label', null, 0, 4, 1, 1, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 33, 29, null, 'data_column_scale', 1, null, '@prm.global.form.elementproperty.numberofdecimaldigits.label', null, 0, 4, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 21, 26, null, 'is_value_required', 5, null, '@prm.global.form.elementproperty.required.label', null, 0, 1, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 22, 26, null, 'is_value_required', 5, null, '@prm.global.form.elementproperty.required.label', null, 0, 1, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 23, 25, null, 'is_value_required', 5, null, '@prm.global.form.elementproperty.required.label', null, 0, 1, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 29, 26, null, 'is_value_required', 5, null, '@prm.global.form.elementproperty.required.label', null, 0, 1, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 31, 25, null, 'is_value_required', 5, null, '@prm.global.form.elementproperty.required.label', null, 0, 1, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 32, 30, null, 'is_value_required', 5, null, '@prm.global.form.elementproperty.required.label', null, 0, 1, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 33, 30, null, 'is_value_required', 5, null, '@prm.global.form.elementproperty.required.label', null, 0, 1, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
insert into PN_CLASS_FIELD (SPACE_ID, CLASS_ID, FIELD_ID, DOMAIN_ID, DATA_COLUMN_NAME, ELEMENT_ID, DATA_TABLE_NAME, FIELD_LABEL, DATA_COLUMN_SIZE, DATA_COLUMN_EXISTS, ROW_NUM, ROW_SPAN, COLUMN_NUM, COLUMN_SPAN, COLUMN_ID, USE_DEFAULT, FIELD_GROUP, HAS_DOMAIN, MAX_VALUE, MIN_VALUE, DEFAULT_VALUE, IS_MULTI_SELECT, SOURCE_FIELD_ID, CRC, RECORD_STATUS, DATA_COLUMN_SCALE, IS_VALUE_REQUIRED)
values (1, 24, 26, null, 'is_value_required', 5, null, '@prm.global.form.elementproperty.required.label', null, 0, 1, 1, 2, 1, null, 0, null, 0, null, null, null, 0, null, null, 'A', null, 0)
/
commit
/
-- prompt  56 records loaded
-- prompt  Disabling foreign key constraints for PN_NOTIFICATION_OBJECT_TYPE...
alter table PN_NOTIFICATION_OBJECT_TYPE disable constraint NOTIFICATION_OBJECT_TYPE_FK1
/
-- prompt  Loading PN_NOTIFICATION_OBJECT_TYPE...
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('group', '@prm.notification.objecttype.group.name', '0')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('project', '@prm.notification.objecttype.project.name', '0')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('news', '@prm.notification.objecttype.news.name', '1')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('discussion_group', '@prm.notification.objecttype.discussiongroup.name', '1')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('task', '@prm.notification.objecttype.task.name', '1')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('form', '@prm.notification.objecttype.form.name', '1')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('form_data', '@prm.notification.objecttype.formdata.name', '1')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('post', '@prm.notification.objecttype.post.name', '1')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('document', '@prm.notification.objecttype.document.name', '1')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('doc_container', '@prm.notification.objecttype.doccontainer.name', '1')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('business', '@prm.notification.objecttype.business.name', '0')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('workflow_step', '@prm.notification.objecttype.workflowstep.name', '0')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('person', '@prm.notification.objecttype.person.name', '0')
/
insert into PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE, DISPLAY_NAME, IS_SUBSCRIBABLE)
values ('meeting', '@prm.notification.objecttype.meeting.name', '0')
/
commit
/
-- prompt  14 records loaded
-- prompt  Enabling foreign key constraints for PN_NOTIFICATION_OBJECT_TYPE...
alter table PN_NOTIFICATION_OBJECT_TYPE enable constraint NOTIFICATION_OBJECT_TYPE_FK1
/

/*
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

*/
-- prompt  
-- prompt  Inserting resource module permission to existing users
-- prompt  

insert into pn_space_has_module
  (space_id, module_id, is_active)
select 
   10, module_id, '1'
from
   pn_module
where
   module_id = 360
/

insert into pn_space_has_module
          (space_id, module_id, is_active)
        values
          (1,360,1)
/
          
-- prompt  
-- prompt  Done inserting resource module permission to existing users.
-- prompt  

-- prompt  
-- prompt  Inserting wiki module permission to existing users
-- prompt  

insert into pn_space_has_module
  (space_id, module_id, is_active)
select 
   project_id, 340, '1'
from
   pn_project_space
/

insert into pn_space_has_module
          (space_id, module_id, is_active)
        values
          (1,340,1)
/
          
insert into pn_module_permission
          (space_id, group_id, module_id, actions)
        values
          (1, 5008, 340, 207)
/

-- prompt  
-- prompt  Done inserting wiki module permission to existing users.
-- prompt 


-- prompt  Enabling foreign key constraints for PN_OBJECT...
alter table PN_OBJECT enable constraint OBJECT_FK1
/
alter table PN_OBJECT enable constraint OBJECT_FK2
/
-- prompt  Enabling foreign key constraints for PN_PERSON...
alter table PN_PERSON enable constraint PERSON_OBJ_FK
/
-- prompt  Enabling foreign key constraints for PN_ADDRESS...
alter table PN_ADDRESS enable constraint ADDRESS_FK1
/
alter table PN_ADDRESS enable constraint ADDRESS_OBJ_FK
/
-- prompt  Enabling foreign key constraints for PN_AUTHENTICATOR...
alter table PN_AUTHENTICATOR enable constraint PN_AUTHENTICATOR_FK1
/
-- prompt  Enabling foreign key constraints for PN_BRAND...
alter table PN_BRAND enable constraint PN_BRAND_FK1
/
-- prompt  Enabling foreign key constraints for PN_BRAND_SUPPORTS_LANGUAGE...
alter table PN_BRAND_SUPPORTS_LANGUAGE enable constraint PN_BRAND_SUPPORTS_LANGUAGE_FK1
/
-- prompt  Enabling foreign key constraints for PN_CALENDAR...
alter table PN_CALENDAR enable constraint CALENDAR_OBJ_FK
/
-- prompt  Enabling foreign key constraints for PN_CLASS...
alter table PN_CLASS enable constraint CLASS_FK1
/
alter table PN_CLASS enable constraint CLASS_OBJ_FK
/
-- prompt  Enabling foreign key constraints for PN_CLASS_DOMAIN...
alter table PN_CLASS_DOMAIN enable constraint CLASS_DOMAIN_OBJ_FK
/
-- prompt  Enabling foreign key constraints for PN_CLASS_DOMAIN_VALUES...
alter table PN_CLASS_DOMAIN_VALUES enable constraint CLASS_DOMAIN_VALUES_FK1
/
alter table PN_CLASS_DOMAIN_VALUES enable constraint CLASS_DOMAIN_VAL_OBJ_FK
/
-- prompt  Enabling foreign key constraints for PN_CLASS_FIELD_PROPERTY...
alter table PN_CLASS_FIELD_PROPERTY enable constraint CLASS_FIELD_PROPERTY_FK1
/
-- prompt  Enabling foreign key constraints for PN_CLASS_TYPE_ELEMENT...
alter table PN_CLASS_TYPE_ELEMENT enable constraint CLASS_TYPE_ELEMENT_FK1
/
alter table PN_CLASS_TYPE_ELEMENT enable constraint CLASS_TYPE_ELEMENT_FK2
/
-- prompt  Enabling foreign key constraints for PN_GROUP...
alter table PN_GROUP enable constraint PN_GROUP_FK2
/
alter table PN_GROUP enable constraint PN_GROUP_FK3
/
-- prompt  Enabling foreign key constraints for PN_DEFAULT_OBJECT_PERMISSION...
alter table PN_DEFAULT_OBJECT_PERMISSION enable constraint DEFAULT_OBJ_PERM_FK1
/
alter table PN_DEFAULT_OBJECT_PERMISSION enable constraint DEFAULT_OBJ_PERM_FK2
/
-- prompt  Enabling foreign key constraints for PN_DIRECTORY_HAS_PERSON...
alter table PN_DIRECTORY_HAS_PERSON enable constraint DIRECTORY_PERSON_FK1
/
alter table PN_DIRECTORY_HAS_PERSON enable constraint DIRECTORY_PERSON_FK2
/
-- prompt  Enabling foreign key constraints for PN_DOC_CONTAINER...
alter table PN_DOC_CONTAINER enable constraint DOC_CONTAINER_FK1
/
alter table PN_DOC_CONTAINER enable constraint DOC_CONTAINER_OBJ_FK
/
-- prompt  Enabling foreign key constraints for PN_DOC_PROVIDER...
alter table PN_DOC_PROVIDER enable constraint DOC_PROVIDER_FK1
/
-- prompt  Enabling foreign key constraints for PN_DOC_SPACE...
alter table PN_DOC_SPACE enable constraint DOC_SPACE_OBJ_FK
/
-- prompt  Enabling foreign key constraints for PN_DOC_PROVIDER_HAS_DOC_SPACE...
alter table PN_DOC_PROVIDER_HAS_DOC_SPACE enable constraint DOC_PROV_DOC_SPACE_FK1
/
alter table PN_DOC_PROVIDER_HAS_DOC_SPACE enable constraint DOC_PROV_DOC_SPACE_FK2
/
-- prompt  Enabling foreign key constraints for PN_DOC_SPACE_HAS_CONTAINER...
alter table PN_DOC_SPACE_HAS_CONTAINER enable constraint DOC_SPACE_CONTAINER_FK1
/
alter table PN_DOC_SPACE_HAS_CONTAINER enable constraint DOC_SPACE_CONTAINER_FK2
/
-- prompt  Enabling foreign key constraints for PN_ELEMENT_DISPLAY_CLASS...
alter table PN_ELEMENT_DISPLAY_CLASS enable constraint ELEMENT_DISPLAY_CLASS_FK1
/
alter table PN_ELEMENT_DISPLAY_CLASS enable constraint ELEMENT_DISPLAY_CLASS_FK2
/
-- prompt  Enabling foreign key constraints for PN_ELEMENT_PROPERTY...
alter table PN_ELEMENT_PROPERTY enable constraint ELEMENT_PROPERTY_FK1
/
alter table PN_ELEMENT_PROPERTY enable constraint ELEMENT_PROPERTY_FK2
/
-- prompt  Enabling foreign key constraints for PN_EVENT_TYPE...
alter table PN_EVENT_TYPE enable constraint PN_EVENT_TYPE_FK1
/
-- prompt  Enabling foreign key constraints for PN_NOTIFICATION_TYPE...
alter table PN_NOTIFICATION_TYPE enable constraint NOTIFICATION_TYPE_FK1
/
-- prompt  Enabling foreign key constraints for PN_EVENT_HAS_NOTIFICATION...
alter table PN_EVENT_HAS_NOTIFICATION enable constraint PN_EVENT_HAS_NOTIFICATION_FK1
/
alter table PN_EVENT_HAS_NOTIFICATION enable constraint PN_EVENT_HAS_NOTIFICATION_FK2
/
-- prompt  Enabling foreign key constraints for PN_GLOBAL_CODE...
alter table PN_GLOBAL_CODE enable constraint GLOBAL_CODE_FK1
/
-- prompt  Enabling foreign key constraints for PN_GROUP_HAS_PERSON...
alter table PN_GROUP_HAS_PERSON enable constraint GROUP_HAS_PERSON_FK1
/
alter table PN_GROUP_HAS_PERSON enable constraint GROUP_HAS_PERSON_FK2
/
-- prompt  Enabling foreign key constraints for PN_LOGIN_HISTORY...
alter table PN_LOGIN_HISTORY enable constraint LOGIN_HISTORY_FK1
/
-- prompt  Enabling foreign key constraints for PN_SPACE_HAS_MODULE...
alter table PN_SPACE_HAS_MODULE enable constraint SPACE_HAS_MODULE_FK1
/
-- prompt  Enabling foreign key constraints for PN_MODULE_PERMISSION...
alter table PN_MODULE_PERMISSION enable constraint MODULE_PERMISSION_FK1
/
alter table PN_MODULE_PERMISSION enable constraint MODULE_PERMISSION_FK2
/
-- prompt  Enabling foreign key constraints for PN_OBJECT_PERMISSION...
alter table PN_OBJECT_PERMISSION enable constraint OBJECT_PERMISSION_FK1
/
alter table PN_OBJECT_PERMISSION enable constraint OBJECT_PERMISSION_FK2
/
-- prompt  Enabling foreign key constraints for PN_OBJECT_TYPE_SUPPORTS_ACTION...
alter table PN_OBJECT_TYPE_SUPPORTS_ACTION enable constraint OBJ_TYPE_SUP_ACTION_FK1
/
alter table PN_OBJECT_TYPE_SUPPORTS_ACTION enable constraint OBJ_TYPE_SUP_ACTION_FK2
/
-- prompt  Enabling foreign key constraints for PN_PERSON_AUTHENTICATOR...
alter table PN_PERSON_AUTHENTICATOR enable constraint PERSON_AUTH_FK2
/
alter table PN_PERSON_AUTHENTICATOR enable constraint PN_PERSON_AUTH_FK1
/
-- prompt  Enabling foreign key constraints for PN_PERSON_NOTIFICATION_ADDRESS...
alter table PN_PERSON_NOTIFICATION_ADDRESS enable constraint PERSON_NOTIFICATION_ADDR_FK1
/
alter table PN_PERSON_NOTIFICATION_ADDRESS enable constraint PERSON_NOTIFICATION_ADDR_FK2
/
-- prompt  Enabling foreign key constraints for PN_PERSON_PROFILE...
alter table PN_PERSON_PROFILE enable constraint PN_PERSON_PROFILE_FK1
/
-- prompt  Enabling foreign key constraints for PN_PORTFOLIO...
alter table PN_PORTFOLIO enable constraint PORTFOLIO_OBJ_FK
/
-- prompt  Enabling foreign key constraints for PN_PORTFOLIO_HAS_SPACE...
alter table PN_PORTFOLIO_HAS_SPACE enable constraint PORTFOLIO_SPACE_FK1
/
-- prompt  Enabling foreign key constraints for PN_SPACE_HAS_CALENDAR...
alter table PN_SPACE_HAS_CALENDAR enable constraint SPACE_HAS_CALENDAR_FK1
/
-- prompt  Enabling foreign key constraints for PN_SPACE_HAS_DIRECTORY...
alter table PN_SPACE_HAS_DIRECTORY enable constraint SPACE_DIRECTORY_FK1
/
-- prompt  Enabling foreign key constraints for PN_SPACE_HAS_DOC_SPACE...
alter table PN_SPACE_HAS_DOC_SPACE enable constraint SPACE_HAS_DOC_SPACE_FK1
/
-- prompt  Enabling foreign key constraints for PN_SPACE_HAS_GROUP...
alter table PN_SPACE_HAS_GROUP enable constraint SPACE_HAS_GROUP_FK1
/
-- prompt  Enabling foreign key constraints for PN_SPACE_HAS_PERSON...
alter table PN_SPACE_HAS_PERSON enable constraint SPACE_PERSON_FK1
/
-- prompt  Enabling foreign key constraints for PN_SPACE_HAS_PORTFOLIO...
alter table PN_SPACE_HAS_PORTFOLIO enable constraint SPACE_PORTFOLIO_FK1
/
-- prompt  Enabling foreign key constraints for PN_STATE_LOOKUP...
alter table PN_STATE_LOOKUP enable constraint STATE_LOOKUP_FK1
/
-- prompt  Enabling foreign key constraints for PN_USER...
alter table PN_USER enable constraint PN_USER_FK1
/
alter table PN_USER enable constraint PN_USER_FK2
/
-- prompt  Enabling foreign key constraints for PN_USER_DEFAULT_CREDENTIALS...
alter table PN_USER_DEFAULT_CREDENTIALS enable constraint PN_USER_DEF_CREDENTIALS_FK1
/


-- set feedback on
-- set define on
-- prompt  Done.
