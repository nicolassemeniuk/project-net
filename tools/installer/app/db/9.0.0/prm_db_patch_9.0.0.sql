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
/**
* APPLYING Database patch for PRM version 9.0.0 */
spool prm_db_patch_9.0.0.log

PROMPT ================================================================================
PROMPT == Initial Steps
PROMPT ================================================================================

/**
 * Update master database version table (database_version) with current 
 * version level
 */
PROMPT Executing update_database_version.sql
@versions/9.0.0/update_database_version.sql
COMMIT;


PROMPT ================================================================================
PROMPT == Tables
PROMPT ================================================================================

PROMPT Altering PN_ASSIGNMENT table
@versions/9.0.0/tables/alter_pn_assignment.sql
COMMIT;

PROMPT Altering PN_WIKI_PAGE table
@versions/9.0.0/tables/alter_pn_wiki_page.sql
COMMIT;

PROMPT Altering PN_PERSON_PROFILE table
@versions/9.0.0/tables/alter_pn_person_profile.sql
COMMIT;

PROMPT Altering PN_WIKI_HISTORY table
@versions/9.0.0/tables/alter_pn_wiki_history.sql
COMMIT;

PROMPT Creating PN_PROJECT_SPACE_META_COMBO table
@versions/9.0.0/tables/cr_pn_project_space_meta_combo.sql
COMMIT;

PROMPT Altering PN_WORKINGTIME_CALENDAR table
@versions/9.0.0/tables/alter_pn_workingtime_calendar.sql
COMMIT;

PROMPT Altering PN_CLASS table
@versions/9.0.0/tables/alter_pn_class.sql
COMMIT;

PROMPT Adding PN_ELEMENT table record
@versions/9.0.0/tables/insert_pn_element.sql
COMMIT;

PROMPT Adding PN_LANGUAGE table record
@versions/9.0.0/tables/insert_pn_language.sql
COMMIT;

PROMPT Altering table PN_PERSON
@versions/9.0.0/tables/alter_pn_person.sql
COMMIT;

PROMPT Creating PN_WIKI_ASSIGNMENT table
@versions/9.0.0/tables/cr_pn_wiki_assignment.sql
COMMIT;

PROMPT Altering PN_WORKINGTIME_CALENDAR_ENTRY table
@versions/9.0.0/tables/alter_pn_workingtime_calendar_entry.sql
COMMIT;

PROMPT Altering PN_WEBLOG_COMMENT table
@versions/9.0.0/tables/alter_pn_weblog_comment.sql
COMMIT;

PROMPT Creating weblog synonyms
@versions/9.0.0/tables/create_weblog_synonyms.sql
COMMIT;

PROMPT Altering PN_PROJECT_SPACE
@versions/9.0.0/tables/alter_pn_project_space.sql
COMMIT;

PROMPT Altering PN_WEBLOG_ENTRY
@versions/9.0.0/tables/alter_pn_weblog_entry.sql
COMMIT;

PROMPT Altering pn_notification_type
@versions/9.0.0/tables/alter_pn_notification_type.sql
COMMIT;

PROMPT Updating pn_notification_type records
@versions/9.0.0/tables/update_pn_notification_type.sql
COMMIT;

PROMPT Updating alter_pn_class_field records
@versions/9.0.0/tables/alter_pn_class_field.sql
COMMIT;

PROMPT ================================================================================
PROMPT == Packages
PROMPT ================================================================================

PROMPT Executing schedule package
@versions/9.0.0/packages/schedule.spc
@versions/9.0.0/packages/schedule.bdy
COMMIT;

PROMPT Executing space package
@versions/9.0.0/packages/space.spc
@versions/9.0.0/packages/space.bdy
COMMIT;

PROMPT Executing forms package
@versions/9.0.0/packages/forms.bdy
COMMIT;

PROMPT Executing security package
@versions/9.0.0/packages/security.spc
@versions/9.0.0/packages/security.bdy
COMMIT;

PROMPT ================================================================================
PROMPT == Views
PROMPT ================================================================================

PROMPT Creating PN_PERSON_VIEW view
@versions/9.0.0/views/pn_person_view.vw
COMMIT;

PROMPT ================================================================================
PROMPT == Indices
PROMPT ================================================================================

PROMPT ================================================================================
PROMPT == Triggers
PROMPT ================================================================================

PROMPT Dropping previosely created PN_OBJECT_NAME_AFT_UPD_NAME trigger
@versions/9.0.0/triggers/pn_object_name_aft_upd_name.tgr
COMMIT;

PROMPT ================================================================================
PROMPT == Final Steps
PROMPT ================================================================================


-- PROMPT Executing update_existing_object_permissions.sql
-- @versions/9.0.0/update_existing_object_permissions.sql
-- COMMIT;

/**
 * Recompile all objects */
PROMPT Executing recompile_objects.sql
@versions/9.0.0/recompile_objects.sql
COMMIT;

/**
 * Update system properties */
PROMPT Executing update_system_properties.sql
set define off
@versions/9.0.0/update_system_properties.sql
COMMIT;
set define on

/**
 * Update system properties */
PROMPT Executing update_opensource_tokens.sql
@versions/9.0.0/update_opensource_tokens.sql
COMMIT;

PROMPT altering pnet_user tables
@versions/9.0.0/tables/alter_pnet_user_tables.sql

PROMPT Updating pn_object_type records
@versions/9.0.0/tables/update_pn_object_type.sql
COMMIT;

PROMPT Inserting  pn_class_field records
@versions/9.0.0/tables/insert_pn_class_field.sql
COMMIT;

spool off;
exit;
