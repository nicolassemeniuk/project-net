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
* APPLYING Database patch for PRM version 9.1.0 */
spool prm_db_patch_9.1.0.log

PROMPT ================================================================================
PROMPT == Initial Steps
PROMPT ================================================================================

/**
 * Update master database version table (database_version) with current 
 * version level
 */
PROMPT Executing update_database_version.sql
@versions/9.1.0/update_database_version.sql
COMMIT;


PROMPT ================================================================================
PROMPT == Tables
PROMPT ================================================================================

PROMPT Altering PN_METHODOLOGY_SPACE table
@versions/9.1.0/tables/alter_pn_methodology_space.sql
COMMIT;

PROMPT Altering PN_ASSIGNMENT table
@versions/9.1.0/tables/alter_pn_assignment.sql
COMMIT;

PROMPT Altering PN_WIKI_PAGE table
@versions/9.1.0/tables/alter_pn_wiki_page.sql
COMMIT;

PROMPT Altering PN_PERSON_PROFILE table
@versions/9.1.0/tables/alter_pn_person_profile.sql
COMMIT;

PROMPT Altering PN_WIKI_HISTORY table
@versions/9.1.0/tables/alter_pn_wiki_history.sql
COMMIT;

PROMPT Creating PN_PROJECT_SPACE_META_COMBO table
@versions/9.1.0/tables/cr_pn_project_space_meta_combo.sql
COMMIT;

PROMPT Altering PN_WORKINGTIME_CALENDAR table
@versions/9.1.0/tables/alter_pn_workingtime_calendar.sql
COMMIT;

PROMPT Altering PN_CLASS table
@versions/9.1.0/tables/alter_pn_class.sql
COMMIT;

PROMPT Adding PN_ELEMENT table record
@versions/9.1.0/tables/insert_pn_element.sql
COMMIT;

PROMPT Adding PN_LANGUAGE table record
@versions/9.1.0/tables/insert_pn_language.sql
COMMIT;

PROMPT Altering table PN_PERSON
@versions/9.1.0/tables/alter_pn_person.sql
COMMIT;

PROMPT Creating PN_WIKI_ASSIGNMENT table
@versions/9.1.0/tables/cr_pn_wiki_assignment.sql
COMMIT;

PROMPT Altering PN_WORKINGTIME_CALENDAR_ENTRY table
@versions/9.1.0/tables/alter_pn_workingtime_calendar_entry.sql
COMMIT;

PROMPT Altering PN_WEBLOG_COMMENT table
@versions/9.1.0/tables/alter_pn_weblog_comment.sql
COMMIT;

PROMPT Altering PN_PROJECT_SPACE
@versions/9.1.0/tables/alter_pn_project_space.sql
COMMIT;

PROMPT Altering PN_WEBLOG_ENTRY
@versions/9.1.0/tables/alter_pn_weblog_entry.sql
COMMIT;

PROMPT Altering pn_notification_type
@versions/9.1.0/tables/alter_pn_notification_type.sql
COMMIT;

PROMPT Updating pn_notification_type records
@versions/9.1.0/tables/update_pn_notification_type.sql
COMMIT;

PROMPT Updating alter_pn_class_field records
@versions/9.1.0/tables/alter_pn_class_field.sql
COMMIT;

PROMPT Updating security, adding missing modules to all projects
@versions/9.1.0/tables/alter_security.sql
COMMIT;

PROMPT Altering pn_module table
@versions/9.1.0/tables/alter_pn_module.sql
COMMIT;

PROMPT Altering pn_wiki_page_table table
@versions/9.1.0/tables/alter_pn_wiki_page_table.sql
COMMIT;

PROMPT Altering pn_task table
@versions/9.1.0/tables/alter_pn_task.sql
COMMIT;

PROMPT Altering pn_shared_forms_visiblity table
@versions/9.1.0/tables/create_pn_shared_forms_visiblity.sql
COMMIT;

PROMPT Creating PN_ACTIVITY_LOG table
@versions/9.1.0/tables/create_pn_activity_log.sql
COMMIT;

PROMPT Altering PN_ACTIVITY_LOG table
@versions/9.1.0/tables/alter_pn_activity_log.sql
COMMIT;

PROMPT Creating PN_ACTIVITY_LOG_MARKED table
@versions/9.1.0/tables/create_pn_activity_log_marked.sql
COMMIT;

PROMPT Creating blog related object types
@versions/9.1.0/tables/create_blog_object_type.sql
COMMIT;

PROMPT Adding PN_SPACE_TYPE_HAS_REPORT_TYPE table record
@versions/9.1.0/tables/insert_pn_space_type_has_report_type.sql
COMMIT;

PROMPT Adding PN_REPORT_SEQUENCE table record
@versions/9.1.0/tables/insert_pn_report_sequence.sql
COMMIT;

PROMPT Updating blog security permissions
@versions/9.1.0/tables/update_blog_security.sql
COMMIT;

PROMPT Adding new document types for Word, Excel and Powerpoint 07
@versions/9.1.0/tables/insert_new_doc_types.sql
COMMIT;

PROMPT Inserting old wiki page ids with page name in pn_object_name table
@versions/9.1.0/tables/insert_object_name_for_old_wiki_pages.sql
COMMIT;

PROMPT Adding new module permissions for old businesses
@versions/9.1.0/tables/insert_time_summary_report_module.sql
COMMIT;

PROMPT Updating wiki page names by removing special characters
@versions/9.1.0/tables/update_wiki_page_names.sql
COMMIT;

PROMPT Altering PN_CLASS_INSTANCE table
@versions/9.1.0/tables/alter_pn_class_instance.sql
COMMIT;

PROMPT Updating sequence nos of form data in PN_CLASS_INSTANCE table
@versions/9.1.0/tables/update_pn_class_instance.sql
COMMIT;

PROMPT Updating pn_methodology_space table
@versions/9.1.0/tables/update_pn_methodology_space.sql
COMMIT;

PROMPT Altering PN_SHARED_FORMS_VISIBLITY table
@versions/9.1.0/tables/alter_pn_shared_forms_visibility.sql
COMMIT;

PROMPT ================================================================================
PROMPT == Packages
PROMPT ================================================================================

PROMPT Executing schedule package
@versions/9.1.0/packages/schedule.spc
@versions/9.1.0/packages/schedule.bdy
COMMIT;

PROMPT Executing space package
@versions/9.1.0/packages/space.spc
@versions/9.1.0/packages/space.bdy
COMMIT;

PROMPT Executing forms package
@versions/9.1.0/packages/forms.spc
@versions/9.1.0/packages/forms.bdy
COMMIT;

PROMPT Executing security package
@versions/9.1.0/packages/security.spc
@versions/9.1.0/packages/security.bdy
COMMIT;

PROMPT Executing workflow package
@versions/9.1.0/packages/workflow.spc
@versions/9.1.0/packages/workflow.bdy
COMMIT;

PROMPT Executing process package
@versions/9.1.0/packages/process.spc
@versions/9.1.0/packages/process.bdy
COMMIT;

PROMPT Executing project package
@versions/9.1.0/packages/project.bdy
COMMIT;

PROMPT Executing business package
@versions/9.1.0/packages/business.spc
@versions/9.1.0/packages/business.bdy
COMMIT;

PROMPT Executing methodology package
@versions/9.1.0/packages/methodology.spc
@versions/9.1.0/packages/methodology.bdy
COMMIT;

PROMPT ================================================================================
PROMPT == Views
PROMPT ================================================================================

PROMPT Creating PN_PERSON_VIEW view
@versions/9.1.0/views/pn_person_view.vw
COMMIT;


PROMPT Creating PN_CLASS_INST_ACTIVE_CNT_VIEW view
@versions/9.1.0/views/pn_class_inst_active_cnt_view.vw
COMMIT;


PROMPT ================================================================================
PROMPT == Indices
PROMPT ================================================================================

PROMPT ================================================================================
PROMPT == Triggers
PROMPT ================================================================================

--PROMPT Dropping previosely created PN_OBJECT_NAME_AFT_UPD_NAME trigger
--@versions/9.1.0/triggers/pn_object_name_aft_upd_name.tgr
--COMMIT;

PROMPT ================================================================================
PROMPT == Final Steps
PROMPT ================================================================================


-- PROMPT Executing update_existing_object_permissions.sql
-- @versions/9.1.0/update_existing_object_permissions.sql
-- COMMIT;

/**
 * Recompile all objects */
PROMPT Executing recompile_objects.sql
@versions/9.1.0/recompile_objects.sql
COMMIT;

/**
 * Update system properties */
PROMPT Executing update_system_properties.sql
set define off
@versions/9.1.0/update_system_properties.sql
COMMIT;
set define on

/**
 * Update system properties */
PROMPT Executing update-opensource-tokens.sql
@versions/9.1.0/update-opensource-tokens.sql
COMMIT;

PROMPT altering pnet_user tables
@versions/9.1.0/tables/alter_pnet_user_tables.sql

PROMPT Updating pn_object_type records
@versions/9.1.0/tables/update_pn_object_type.sql
COMMIT;

PROMPT Inserting  pn_class_field records
@versions/9.1.0/tables/insert_pn_class_field.sql
COMMIT;

PROMPT creating pn_wiki_attachment table
@versions/9.1.0/tables/create_pn_wiki_attachment.sql
COMMIT;
spool off

spool debug_9.1.0.log
PROMPT Checking invalid objects for PNET user
prompt ======================================
@versions/9.1.0/debug/check_invalid_objects.sql
COMMIT;
spool off

spool prm_db_patch_9.1.0.log append
PROMPT creating private synonyms 
@versions/9.1.0/synonyms/create_private_synonyms.sql &1 &2 &3
COMMIT;

PROMPT Recreating all synonyms for PNET_USER
prompt
prompt Recreating private table synonyms
prompt =======================================
prompt
@versions/9.1.0/new/synonyms/create_private_table_synonyms.sql
prompt
prompt Recreating private view synonyms
prompt ======================================
prompt
@versions/9.1.0/new/synonyms/create_private_view_synonyms.sql
prompt
prompt Recreating private package synonyms
prompt =======================================
prompt
@versions/9.1.0/new/synonyms/create_private_package_synonyms.sql
prompt
prompt Recreating private sequence synonyms
prompt =======================================
prompt
@versions/9.1.0/new/synonyms/create_private_sequence_synonyms.sql

COMMIT;
spool off

spool debug_9.1.0.log append
PROMPT Checking invalid objects for PNET_USER user
prompt ======================================
@versions/9.1.0/debug/check_invalid_objects.sql
COMMIT;
spool off;

exit;
