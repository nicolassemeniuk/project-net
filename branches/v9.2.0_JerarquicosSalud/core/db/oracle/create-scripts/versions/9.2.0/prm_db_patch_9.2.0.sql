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
* APPLYING Database patch for PRM version 9.2.0 */
spool prm_db_patch_9.2.0.log

PROMPT ================================================================================
PROMPT == Initial Steps
PROMPT ================================================================================

/**
 * Update master database version table (database_version) with current 
 * version level
 */
PROMPT Executing update_database_version.sql
@versions/9.2.0/update_database_version.sql
COMMIT;


PROMPT ================================================================================
PROMPT == Tables
PROMPT ================================================================================

PROMPT Altering PN_METHODOLOGY_SPACE table
@versions/9.2.0/tables/alter_pn_methodology_space.sql
COMMIT;

PROMPT Creating PN_METHODOLOGY_MODULES table
@versions/9.2.0/tables/cr_pn_methodology_modules.sql
COMMIT;

PROMPT Altering PN_SHARED_FORMS_VISIBLITY table
@versions/9.2.0/tables/alter_pn_shared_forms_visibility.sql
COMMIT;

PROMPT Altering PN_SUBSCRIPTION table
@versions/9.2.0/tables/alter_pn_subscription.sql
COMMIT;

PROMPT Insert insert_blog_notification_type to enable notification in blog
@versions/9.2.0/tables/insert_blog_notification_type.sql
COMMIT;

PROMPT Insert insert_wiki_notification_type to enable notification in wiki
@versions/9.2.0/tables/insert_wiki_notification_type.sql
COMMIT;

PROMPT Insert insert_old_blog_object_names 
@versions/9.2.0/tables/insert_old_blog_object_names.sql
COMMIT;

PROMPT Insert insert_project_notification_type to enable notification in project 
@versions/9.2.0/tables/insert_project_notification_type.sql
COMMIT;

PROMPT Altering pn_view to add new column is_view_shared 
@versions/9.2.0/tables/alter_pn_view.sql
COMMIT;

PROMPT creating pn_business_has_view table
@versions/9.2.0/tables/create_pn_business_has_view.sql
COMMIT;

PROMPT Altering pn_plan table
@versions/9.2.0/tables/alter_pn_plan.sql
COMMIT;

PROMPT Altering alter_pn_plan_version table
@versions/9.2.0/tables/alter_pn_plan_version.sql
COMMIT;

PROMPT creating pn_charge_code table
@versions/9.2.0/tables/create_pn_charge_code.sql
COMMIT;

PROMPT creating pn_object_has_charge_code table
@versions/9.2.0/tables/create_pn_object_has_charge_code.sql
COMMIT;

PROMPT insert pn_object_type
@versions/9.2.0/tables/insert_pn_object_type.sql
COMMIT;

PROMPT altering pn_user table
@versions/9.2.0/tables/alter_pn_user.sql
COMMIT;

PROMPT Insert insert_missing_wiki_notification_object from pn_wiki_page to pn_object table
@versions/9.2.0/tables/insert_missing_wiki_notification_object.sql
COMMIT;

PROMPT ================================================================================
PROMPT == Packages
PROMPT ================================================================================

PROMPT Executing notification package
@versions/9.2.0/packages/notification.spc
@versions/9.2.0/packages/notification.bdy
COMMIT;

PROMPT Executing methodology package
@versions/9.2.0/packages/methodology.spc
@versions/9.2.0/packages/methodology.bdy
COMMIT;

PROMPT Executing schedule package
@versions/9.2.0/packages/schedule.spc
@versions/9.2.0/packages/schedule.bdy
COMMIT;

PROMPT Executing project package
@versions/9.2.0/packages/project.spc
@versions/9.2.0/packages/project.bdy
COMMIT;

PROMPT Executing discussion package
@versions/9.2.0/packages/discussion.spc
@versions/9.2.0/packages/discussion.bdy
COMMIT;

PROMPT Executing business package
@versions/9.2.0/packages/business.spc
@versions/9.2.0/packages/business.bdy
COMMIT;

PROMPT Executing util package
@versions/9.2.0/packages/util.spc
@versions/9.2.0/packages/util.bdy
COMMIT;

PROMPT Executing space package
@versions/9.2.0/packages/space.spc
@versions/9.2.0/packages/space.bdy
COMMIT;

PROMPT Updating security package
@versions/9.2.0/packages/security.spc
@versions/9.2.0/packages/security.bdy
COMMIT;

PROMPT ================================================================================
PROMPT == Views
PROMPT ================================================================================

PROMPT Creating PN_METHODOLOGY_VIEW view
@versions/9.2.0/views/pn_methodology_view.vw
COMMIT;

PROMPT Creating PN_METHODOLOGY_BY_USER_VIEW view
@versions/9.2.0/views/pn_methodology_by_user_view.vw
COMMIT;

PROMPT Creating PN_WF_STEP_HAS_GROUP_VIEW view
@versions/9.2.0/views/pn_wf_step_has_group_view.vw
COMMIT;

PROMPT Creating PN_USER_VIEW view
@versions/9.2.0/views/pn_user_view.vw
COMMIT;

PROMPT ================================================================================
PROMPT == Indices
PROMPT ================================================================================

PROMPT ================================================================================
PROMPT == Triggers
PROMPT ================================================================================


PROMPT ================================================================================
PROMPT == Final Steps
PROMPT ================================================================================


/**
 * Recompile all objects */
PROMPT Executing recompile_objects.sql
@versions/9.2.0/recompile_objects.sql
COMMIT;

/**
 * Update system properties */
PROMPT Executing update_system_properties.sql
set define off
@versions/9.2.0/update_system_properties.sql
COMMIT;
set define on

/**
 * Update system properties */
PROMPT Executing update-opensource-tokens.sql
@versions/9.2.0/update-opensource-tokens.sql
COMMIT;


spool off

spool debug_9.2.0.log
PROMPT Checking invalid objects for PNET user
prompt ======================================
@versions/9.2.0/debug/check_invalid_objects.sql
COMMIT;
spool off

spool prm_db_patch_9.2.0.log append
PROMPT creating private synonyms 
@versions/9.2.0/synonyms/create_private_synonyms.sql &1 &2 &3
COMMIT;

PROMPT Recreating all synonyms for PNET_USER
prompt
prompt Recreating private table synonyms
prompt =======================================
prompt
@versions/9.2.0/new/synonyms/create_private_table_synonyms.sql
prompt
prompt Recreating private view synonyms
prompt ======================================
prompt
@versions/9.2.0/new/synonyms/create_private_view_synonyms.sql
prompt
prompt Recreating private package synonyms
prompt =======================================
prompt
@versions/9.2.0/new/synonyms/create_private_package_synonyms.sql
prompt
prompt Recreating private sequence synonyms
prompt =======================================
prompt
@versions/9.2.0/new/synonyms/create_private_sequence_synonyms.sql

COMMIT;
spool off

spool debug_9.2.0.log append
PROMPT Checking invalid objects for PNET_USER user
prompt ======================================
@versions/9.2.0/debug/check_invalid_objects.sql
COMMIT;
spool off;

exit;
