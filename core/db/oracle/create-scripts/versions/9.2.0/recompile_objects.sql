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
 * Recompile Views
 */
alter VIEW PNET.PN_BASELINE_TASK compile
/
alter VIEW PNET.PN_BOOKMARK_VIEW compile
/
alter VIEW PNET.PN_BUSINESS_VIEW compile
/
alter VIEW PNET.PN_BUSINESS_SPACE_VIEW compile
/
alter VIEW PNET.PN_CLASS_INST_ACTIVE_CNT_VIEW compile
/
alter VIEW PNET.PN_CURRENT_TASK_VERSION compile
/
alter VIEW PNET.PN_DOC_BY_SPACE_VIEW compile
/
alter VIEW PNET.PN_DOC_CONTAINER_OBJECTS_VIEW compile
/
alter VIEW PNET.PN_DOC_CONTAINER_LIST_VIEW compile
/
alter VIEW PNET.PN_DOC_CONTENT_ELEMENTS_VIEW compile
/
alter VIEW PNET.PN_DOC_HISTORY_VIEW compile
/
alter VIEW PNET.PN_DOC_VERSION_VIEW compile
/
alter VIEW PNET.PN_DOC_DELETED_BY_SPACE_VIEW compile
/
alter VIEW PNET.PN_DOC_DEL_CONTAINER_LIST_VIEW compile
/
alter VIEW PNET.PN_DOC_DEl_HISTORY_VIEW compile
/
alter VIEW PNET.PN_DOC_DEl_VERSION_VIEW compile
/
alter VIEW PNET.PN_ENVELOPE_ACTIVE_VIEW compile
/
alter VIEW PNET.PN_WORKFLOW_ENVELOPE_VIEW compile
/
alter VIEW PNET.PN_ENVELOPE_STEPPERSON_X1_VIEW compile
/
alter VIEW PNET.PN_ENVELOPE_STEP_PERSON_VIEW compile
/
alter VIEW PNET.PN_ENVELOPE_VERSION_VIEW compile
/
alter VIEW PNET.PN_ENVELOPE_HAS_OBJECT_VIEW compile
/
alter VIEW PNET.PN_ENVELOPE_HAS_PERSON_VIEW compile
/
alter VIEW PNET.PN_ENVELOPE_HISTORY_VIEW compile
/
alter VIEW PNET.PN_FORMS_HISTORY_VIEW compile
/
alter VIEW PNET.PN_GROUP_MEMBER_COUNT_VIEW compile
/
alter VIEW PNET.PN_GROUP_VIEW compile
/
alter VIEW PNET.PN_METHODOLOGY_VIEW compile
/
alter VIEW PNET.PN_METHODOLOGY_BY_USER_VIEW compile
/
alter VIEW PNET.PN_NEWS_HISTORY_VIEW compile
/
alter VIEW PNET.PN_NEWS_VIEW compile
/
alter VIEW PNET.PN_PERSON_VIEW compile
/
alter VIEW PNET.PN_PHASE_HAS_TASK_VIEW compile
/
alter VIEW PNET.PN_PORTFOLIO_HAS_PROJECTS_VIEW compile
/
alter VIEW PNET.PN_PORTFOLIO_VIEW compile
/
alter VIEW PNET.PN_PROJECT_VIEW compile
/
alter VIEW PNET.PN_SPACE_VIEW compile
/
alter VIEW PNET.PN_TASK_HISTORY_VIEW compile
/
alter VIEW PNET.PN_USER_DOMAIN_VIEW compile
/
alter VIEW PNET.PN_USER_VIEW compile
/
alter VIEW PNET.PN_WF_STEP_HAS_GROUP_VIEW compile
/
alter VIEW PNET.PN_WORKFLOW_RULE_VIEW compile
/
alter VIEW PNET.PN_WORKFLOW_VIEW compile
/
alter VIEW PNET.PN_WORKFLOW_STEP_VIEW compile
/
alter VIEW PNET.PN_WORKFLOW_TRANSITION_VIEW compile
/

/**
 * Recompile Packages
 */
alter PACKAGE PNET.APPLICATION compile body
/
alter PACKAGE PNET.BASE compile body
/
alter PACKAGE PNET.BUSINESS compile body
/
alter PACKAGE PNET.CALENDAR compile body
/
alter PACKAGE PNET.CONFIGURATION compile body
/
alter PACKAGE PNET.DISCUSSION compile body
/
alter PACKAGE PNET.DOCUMENT compile body
/
alter PACKAGE PNET.FORMS compile body
/
alter PACKAGE PNET.HELP compile body
/
alter PACKAGE PNET.MESSAGE compile body
/
alter PACKAGE PNET.METHODOLOGY compile body
/
alter PACKAGE PNET.NEWS compile body
/
alter PACKAGE PNET.NOTIFICATION compile body
/
alter PACKAGE PNET.PROCESS compile body
/
alter PACKAGE PNET.PRODUCT compile body
/
alter PACKAGE PNET.PROFILE compile body
/
alter PACKAGE PNET.PROJECT compile body
/
alter PACKAGE PNET.SECURITY compile body
/
alter PACKAGE PNET.SHARING compile body
/
alter PACKAGE PNET.SCHEDULE compile body
/
alter PACKAGE PNET.SPACE compile body
/
alter PACKAGE PNET.TASK compile body
/
alter PACKAGE PNET.TIMESHEET compile body
/
alter PACKAGE PNET.USER_DOMAIN compile body
/
alter PACKAGE PNET.UTIL compile body
/
alter PACKAGE PNET.VOTE compile body
/
alter PACKAGE PNET.WORKFLOW compile body
/
alter PACKAGE PNET.WORKFLOW_RULE compile body
/
/**
 * Recompile Triggers
 */
alter TRIGGER PNET.ADDRESS_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.AGENDA_ITEM_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.APPLICATION_AFT_UPD_NAME compile
/
alter TRIGGER PNET.APPLICATION_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.BOOKMARK_AFT_UPD_NAME compile
/
alter TRIGGER PNET.BOOKMARK_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.BRAND_AFT_UPD_NAME compile
/
alter TRIGGER PNET.BRAND_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.BUSINESS_AFT_UPD_NAME compile
/
alter TRIGGER PNET.BUSINESS_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.CALENDAR_AFT_UPD_NAME compile
/
alter TRIGGER PNET.CALENDAR_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.DELIVERABLE_AFT_INS compile
/
alter TRIGGER PNET.CLASS_AFT_INS compile
/
alter TRIGGER PNET.CONFIGURATION_AFT_UPD_NAME compile
/
alter TRIGGER PNET.CONFIGURATION_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.DELIVERABLE_AFT_UPD_NAME compile
/
alter TRIGGER PNET.DELIVERABLE_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.DISC_GROUP_AFT_UPD_NAME compile
/
alter TRIGGER PNET.DISC_GROUP_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.DOCUMENT_AFT_UPD_NAME compile
/
alter TRIGGER PNET.DOCUMENT_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.DOC_CONTAINER_AFT_UPD_NAME compile
/
alter TRIGGER PNET.DOC_CONTAINER_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.DOC_SPACE_AFT_UPD_NAME compile
/
alter TRIGGER PNET.DOC_SPACE_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.DOMAIN_VALUE_AFT_UPD_NAME compile
/
alter TRIGGER PNET.DOMAIN_VALUE_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.ENVELOPE_AFT_UPD_NAME compile
/
alter TRIGGER PNET.ENVELOPE_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.ENV_HISTORY_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.ENV_VERSION_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.EVENT_AFT_UPD_NAME compile
/
alter TRIGGER PNET.EVENT_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.FORM_AFT_UPD_NAME compile
/
alter TRIGGER PNET.FORM_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.FORM_DOMAIN_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.FORM_FIELD_AFT_UPD_NAME compile
/
alter TRIGGER PNET.FORM_FIELD_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.FORM_INST_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.FORM_LIST_AFT_UPD_NAME compile
/
alter TRIGGER PNET.FORM_LIST_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.GATE_AFT_INS compile
/
alter TRIGGER PNET.GATE_AFT_UPD_NAME compile
/
alter TRIGGER PNET.GATE_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.GROUP_AFT_UPD_NAME compile
/
alter TRIGGER PNET.GROUP_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.METHODOLOGY_AFT_UPD_NAME compile
/
alter TRIGGER PNET.METHODOLOGY_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.NEWS_AFT_UPD_NAME compile
/
alter TRIGGER PNET.NEWS_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.PERSON_AFT_UPD_NAME compile
/
alter TRIGGER PNET.PERSON_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.PHASE_AFT_INS compile
/
alter TRIGGER PNET.PHASE_AFT_UPD_NAME compile
/
alter TRIGGER PNET.PHASE_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.PLAN_AFT_UPD_NAME compile
/
alter TRIGGER PNET.PORTFOLIO_AFT_UPD_NAME compile
/
alter TRIGGER PNET.PORTFOLIO_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.POST_AFT_UPD_NAME compile
/
alter TRIGGER PNET.POST_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.PROCESS_AFT_UPD_NAME compile
/
alter TRIGGER PNET.PROCESS_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.PROJECT_AFT_UPD_NAME compile
/
alter TRIGGER PNET.PROJECT_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.STEP_AFT_UPD_NAME compile
/
alter TRIGGER PNET.STEP_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.SUBSCRIPTION_AFT_UPD_NAME compile
/
alter TRIGGER PNET.SUBSCRIPTION_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.S_SUBSCRIPTION_AFT_UPD_NAME compile
/
alter TRIGGER PNET.S_SUBSCRIPTION_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.TASK_AFT_UPD_NAME compile
/
alter TRIGGER PNET.TASK_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.TRANSITION_AFT_UPD_NAME compile
/
alter TRIGGER PNET.TRANSITION_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.USER_DOMAIN_AFT_UPD_NAME compile
/
alter TRIGGER PNET.USER_DOMAIN_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.WORKFLOW_AFT_UPD_NAME compile
/
alter TRIGGER PNET.WORKFLOW_AFT_UPD_STATUS compile
/
alter TRIGGER PNET.WORKFLOW_RULE_AFT_UPD_NAME compile
/
alter TRIGGER PNET.WORKFLOW_RULE_AFT_UPD_STATUS compile
/
