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
prompt Creating table CLIENT_DATABASE_VERSION
prompt ======================================
prompt
create table CLIENT_DATABASE_VERSION
(
  MAJOR_VERSION         NUMBER(3) not null,
  MINOR_VERSION         NUMBER(3) not null,
  SUB_MINOR_VERSION     NUMBER(3) not null,
  CLIENT_NAME           VARCHAR2(50) not null,
  PRM_MAJOR_VERSION     NUMBER(3),
  PRM_MINOR_VERSION     NUMBER(3),
  PRM_SUB_MINOR_VERSION NUMBER(3),
  TIMESTAMP             DATE not null,
  DESCRIPTION           VARCHAR2(1000) not null
)
;
alter table CLIENT_DATABASE_VERSION
  add constraint CLIENT_DATABASE_VERSION_PK primary key (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION);

prompt
prompt Creating table DATABASE_VERSION
prompt ===============================
prompt
create table DATABASE_VERSION
(
  MAJOR_VERSION     NUMBER(3) not null,
  MINOR_VERSION     NUMBER(3) not null,
  SUB_MINOR_VERSION NUMBER(3) not null,
  TIMESTAMP         DATE not null,
  DESCRIPTION       VARCHAR2(1000) not null
)
;
alter table DATABASE_VERSION
  add constraint DATABASE_VERSION_PK primary key (MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION);

prompt
prompt Creating table DATABASE_VERSION_UPDATE
prompt ======================================
prompt
create table DATABASE_VERSION_UPDATE
(
  MAJOR_VERSION     NUMBER(3) not null,
  MINOR_VERSION     NUMBER(3) not null,
  SUB_MINOR_VERSION NUMBER(3) not null,
  PATCH_FILENAME    VARCHAR2(250) not null,
  PATCH_DESCRIPTION VARCHAR2(4000) not null,
  TIMESTAMP         DATE not null
)
;

prompt
prompt Creating table PN_OBJECT
prompt ========================
prompt
create table PN_OBJECT
(
  OBJECT_ID     NUMBER(20) not null,
  OBJECT_TYPE   VARCHAR2(80) not null,
  DATE_CREATED  DATE not null,
  CREATED_BY    NUMBER(20) not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_OBJECT
  add constraint OBJECT_PK primary key (OBJECT_ID);
alter table PN_OBJECT
  add constraint RECORD_STATUS_VALID20
  check (record_status in ('A','P','D','H'));
create index OBJECT_IDX1 on PN_OBJECT (OBJECT_TYPE) TABLESPACE INDEX01;
create index OBJECT_IDX2 on PN_OBJECT (CREATED_BY) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PERSON
prompt ========================
prompt
create table PN_PERSON
(
  PERSON_ID               NUMBER(20) not null,
  EMAIL                   VARCHAR2(240) not null,
  FIRST_NAME              VARCHAR2(40) not null,
  LAST_NAME               VARCHAR2(60) not null,
  DISPLAY_NAME            VARCHAR2(240) not null,
  USER_STATUS             VARCHAR2(80) not null,
  MEMBERSHIP_PORTFOLIO_ID NUMBER(20) not null,
  RECORD_STATUS           VARCHAR2(1) not null,
  CREATED_DATE            DATE
--  IMAGE_ID            	  NUMBER(20)
)
;
alter table PN_PERSON
  add constraint PERSON_PK primary key (PERSON_ID);
alter table PN_PERSON
  add constraint PERSON_EMAIL_AK2 unique (EMAIL);
alter table PN_PERSON
  add constraint PERSON_OBJ_FK foreign key (PERSON_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_PERSON
  add constraint PN_USER_STATUS_VALID
  check (user_status in ('Active','Invited','Unconfirmed', 'Unregistered', 'Disabled', 'Deleted'));
alter table PN_PERSON
  add constraint RECORD_STATUS_VALID2
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_OBJECT_TYPE
prompt =============================
prompt
create table PN_OBJECT_TYPE
(
  OBJECT_TYPE                VARCHAR2(80) not null,
  MASTER_TABLE_NAME          VARCHAR2(80),
  OBJECT_TYPE_DESC           VARCHAR2(500),
  PARENT_OBJECT_TYPE         VARCHAR2(80),
  DEFAULT_PERMISSION_ACTIONS NUMBER(10) not null,
  IS_SECURABLE               NUMBER(1) not null,
  IS_WORKFLOWABLE            NUMBER(1) not null
)
;
alter table PN_OBJECT_TYPE
  add constraint OBJECT_TYPE_PK primary key (OBJECT_TYPE);

prompt
prompt Creating table PN_DOC_TYPE
prompt ==========================
prompt
create table PN_DOC_TYPE
(
  DOC_TYPE_ID             NUMBER(20) not null,
  PROPERTY_SHEET_CLASS_ID NUMBER(20),
  TYPE_NAME               VARCHAR2(80),
  TYPE_DESCRIPTION        VARCHAR2(500),
  RECORD_STATUS           VARCHAR2(1)
)
;
alter table PN_DOC_TYPE
  add constraint DOC_TYPE_PK primary key (DOC_TYPE_ID);

prompt
prompt Creating table PN_DOCUMENT
prompt ==========================
prompt
create table PN_DOCUMENT
(
  DOC_ID             NUMBER(20) not null,
  DOC_NAME           VARCHAR2(256),
  DOC_TYPE_ID        NUMBER(20),
  DOC_DESCRIPTION    VARCHAR2(4000),
  CURRENT_VERSION_ID NUMBER(20) not null,
  DOC_STATUS_ID      NUMBER(20),
  CRC                DATE not null,
  RECORD_STATUS      VARCHAR2(1) not null,
  OLD_STORAGE_ID     NUMBER(20)
)
;
alter table PN_DOCUMENT
  add constraint DOCUMENT_PK primary key (DOC_ID);
alter table PN_DOCUMENT
  add constraint DOCUMENT_FK1 foreign key (DOC_TYPE_ID)
  references PN_DOC_TYPE (DOC_TYPE_ID);
alter table PN_DOCUMENT
  add constraint DOCUMENT_OBJ_FK foreign key (DOC_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_DOCUMENT
  add constraint RECORD_STATUS_VALID29
  check (record_status in ('A','P','D','H'));
create unique index DOCUMENT_IDX1 on PN_DOCUMENT (CURRENT_VERSION_ID);
-- create bitmap index DOCUMENT_IDX2 on PN_DOCUMENT (DOC_STATUS_ID);
create index DOCUMENT_IDX3 on PN_DOCUMENT (DOC_TYPE_ID) TABLESPACE INDEX01;
-- create bitmap index DOCUMENT_IDX4 on PN_DOCUMENT (RECORD_STATUS) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PROJECT_SPACE
prompt ===============================
prompt
create table PN_PROJECT_SPACE
(
  PROJECT_ID                     NUMBER(20) not null,
  PROJECT_DESC                   VARCHAR2(1000),
  PROJECT_NAME                   VARCHAR2(80) not null,
  STATUS_CODE_ID                 NUMBER(20),
  COLOR_CODE_ID                  NUMBER(20),
  IS_SUBPROJECT                  NUMBER(1) default 0 not null,
  PERCENT_COMPLETE               NUMBER(3) default 0,
  START_DATE                     DATE,
  END_DATE                       DATE,
  PROJECT_LOGO_ID                NUMBER(20),
  DATE_MODIFIED                  DATE,
  MODIFIED_BY_ID                 NUMBER(20),
  CRC                            DATE not null,
  RECORD_STATUS                  VARCHAR2(1) not null,
  DEFAULT_CURRENCY_CODE          VARCHAR2(3) not null,
  SPONSOR_DESC                   VARCHAR2(1000),
  IMPROVEMENT_CODE_ID            NUMBER(20) not null,
  CURRENT_STATUS_DESCRIPTION     VARCHAR2(4000),
  FINANCIAL_STATUS_COLOR_CODE_ID NUMBER(20),
  FINANCIAL_STATUS_IMP_CODE_ID   NUMBER(20) not null,
  BUDGETED_TOTAL_COST_VALUE      NUMBER,
  BUDGETED_TOTAL_COST_CC         VARCHAR2(3),
  CURRENT_EST_TOTAL_COST_VALUE   NUMBER,
  CURRENT_EST_TOTAL_COST_CC      VARCHAR2(3),
  ACTUAL_TO_DATE_COST_VALUE      NUMBER,
  ACTUAL_TO_DATE_COST_CC         VARCHAR2(3),
  ESTIMATED_ROI_COST_VALUE       NUMBER,
  ESTIMATED_ROI_COST_CC          VARCHAR2(3),
  COST_CENTER                    VARCHAR2(1000),
  SCHEDULE_STATUS_COLOR_CODE_ID  NUMBER(20),
  SCHEDULE_STATUS_IMP_CODE_ID    NUMBER(20) not null,
  RESOURCE_STATUS_COLOR_CODE_ID  NUMBER(20),
  RESOURCE_STATUS_IMP_CODE_ID    NUMBER(20) not null,
  PRIORITY_CODE_ID               NUMBER(20),
  RISK_RATING_CODE_ID            NUMBER(20),
  VISIBILITY_ID                  NUMBER(20) not null,
  PERCENT_CALCULATION_METHOD     VARCHAR2(80) default 'manual' not null,
  INCLUDES_EVERYONE              NUMBER(1) default 0 not null
)
;
alter table PN_PROJECT_SPACE
  add constraint PROJECT_SPACE_PK primary key (PROJECT_ID);
alter table PN_PROJECT_SPACE
  add constraint PROJECT_OBJ_FK foreign key (PROJECT_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_PROJECT_SPACE
  add constraint PROJECT_SPACE_FK1 foreign key (MODIFIED_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_PROJECT_SPACE
  add constraint PROJECT_SPACE_FK2 foreign key (PROJECT_LOGO_ID)
  references PN_DOCUMENT (DOC_ID);
alter table PN_PROJECT_SPACE
  add constraint RECORD_STATUS_VALID3
  check (record_status in ('A','P','D','H'));
create index PROJECT_SPACE_IDX1 on PN_PROJECT_SPACE (COLOR_CODE_ID) TABLESPACE INDEX01;
create index PROJECT_SPACE_IDX2 on PN_PROJECT_SPACE (MODIFIED_BY_ID) TABLESPACE INDEX01;
create index PROJECT_SPACE_IDX3 on PN_PROJECT_SPACE (PROJECT_LOGO_ID) TABLESPACE INDEX01;

prompt
prompt Creating table HELP_FEEDBACK
prompt ============================
prompt
create table HELP_FEEDBACK
(
  PERSON_ID  NUMBER(20) not null,
  TIMESTAMP  DATE not null,
  PROJECT_ID NUMBER(20),
  SUBJECT    VARCHAR2(30) not null,
  KEY_ID     NUMBER(10),
  COMMENTS   VARCHAR2(1000)
)
;
alter table HELP_FEEDBACK
  add constraint HELP_FEEDBACK_PK primary key (PERSON_ID, TIMESTAMP);
alter table HELP_FEEDBACK
  add constraint HELP_FEEDBACK_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table HELP_FEEDBACK
  add constraint HELP_FEEDBACK_FK2 foreign key (PROJECT_ID)
  references PN_PROJECT_SPACE (PROJECT_ID);

prompt
prompt Creating table PN_ACTIVITY
prompt ==========================
prompt
create table PN_ACTIVITY
(
  OBJECT_ID NUMBER(20) not null,
  SPACE_ID  NUMBER(20) not null,
  NAME      VARCHAR2(400) not null
)
;
alter table PN_ACTIVITY
  add constraint ACTIVITY_PK primary key (OBJECT_ID);
alter table PN_ACTIVITY
  add constraint ACTIVITY_FK1 foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);

prompt
prompt Creating table PN_COUNTRY_LOOKUP
prompt ================================
prompt
create table PN_COUNTRY_LOOKUP
(
  COUNTRY_CODE VARCHAR2(2) not null,
  COUNTRY_NAME VARCHAR2(500) not null
)
;
alter table PN_COUNTRY_LOOKUP
  add constraint COUNTRY_LOOKUP_PK primary key (COUNTRY_CODE);

prompt
prompt Creating table PN_ADDRESS
prompt =========================
prompt
create table PN_ADDRESS
(
  ADDRESS_ID     NUMBER(20) not null,
  ADDRESS1       VARCHAR2(80),
  ADDRESS2       VARCHAR2(80),
  ADDRESS3       VARCHAR2(80),
  ADDRESS4       VARCHAR2(80),
  ADDRESS5       VARCHAR2(80),
  ADDRESS6       VARCHAR2(80),
  ADDRESS7       VARCHAR2(80),
  CITY           VARCHAR2(320),
  CITY_DISTRICT  VARCHAR2(50),
  REGION         VARCHAR2(50),
  STATE_PROVENCE VARCHAR2(80),
  COUNTRY_CODE   VARCHAR2(2),
  ZIPCODE        VARCHAR2(20),
  OFFICE_PHONE   VARCHAR2(20),
  FAX_PHONE      VARCHAR2(20),
  HOME_PHONE     VARCHAR2(20),
  MOBILE_PHONE   VARCHAR2(20),
  PAGER_PHONE    VARCHAR2(20),
  PAGER_EMAIL    VARCHAR2(240),
  WEBSITE_URL    VARCHAR2(240),
  RECORD_STATUS  VARCHAR2(1) not null
)
;
alter table PN_ADDRESS
  add constraint ADDRESS_PK primary key (ADDRESS_ID);
alter table PN_ADDRESS
  add constraint ADDRESS_FK1 foreign key (COUNTRY_CODE)
  references PN_COUNTRY_LOOKUP (COUNTRY_CODE);
alter table PN_ADDRESS
  add constraint ADDRESS_OBJ_FK foreign key (ADDRESS_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_ADDRESS
  add constraint RECORD_STATUS_VALID
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_FACILITY
prompt ==========================
prompt
create table PN_FACILITY
(
  FACILITY_ID   NUMBER(20) not null,
  ADDRESS_ID    NUMBER(20),
  NAME          VARCHAR2(80) not null,
  DESCRIPTION   VARCHAR2(500),
  FACILITY_TYPE VARCHAR2(80) not null,
  ROOM_NAME     VARCHAR2(80),
  ROOM_NUMBER   VARCHAR2(80),
  BUILDING      VARCHAR2(80),
  FLOOR         VARCHAR2(80),
  CAMPUS        VARCHAR2(80),
  IS_BRIDGE     NUMBER(1),
  PHONE_NUMBER  VARCHAR2(20),
  PASSWORD      VARCHAR2(80),
  FACILITY_URL  VARCHAR2(240)
)
;
alter table PN_FACILITY
  add constraint FACILITY_PK primary key (FACILITY_ID);
alter table PN_FACILITY
  add constraint FACILITY_FK1 foreign key (ADDRESS_ID)
  references PN_ADDRESS (ADDRESS_ID);
alter table PN_FACILITY
  add constraint FACILITY_OBJ_FK foreign key (FACILITY_ID)
  references PN_OBJECT (OBJECT_ID);

prompt
prompt Creating table PN_CALENDAR_EVENT
prompt ================================
prompt
create table PN_CALENDAR_EVENT
(
  CALENDAR_EVENT_ID  NUMBER(20) not null,
  EVENT_NAME         VARCHAR2(80) not null,
  EVENT_TYPE_ID      NUMBER(20) not null,
  FACILITY_ID        NUMBER(20) not null,
  FREQUENCY_TYPE_ID  NUMBER(20) not null,
  START_DATE         DATE,
  END_DATE           DATE,
  RECORD_STATUS      VARCHAR2(1) not null,
  EVENT_DESC_CLOB    CLOB,
  EVENT_PURPOSE_CLOB CLOB
)
;
alter table PN_CALENDAR_EVENT
  add constraint CALENDAR_EVENT_PK primary key (CALENDAR_EVENT_ID);
alter table PN_CALENDAR_EVENT
  add constraint CALENDAR_EVENT_FK1 foreign key (FACILITY_ID)
  references PN_FACILITY (FACILITY_ID);
alter table PN_CALENDAR_EVENT
  add constraint RECORD_STATUS_VALID5
  check (record_status in ('A','P','D','H'));
create index CALENDAR_EVENT_IDX1 on PN_CALENDAR_EVENT (FACILITY_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_MEETING
prompt =========================
prompt
create table PN_MEETING
(
  MEETING_ID           NUMBER(20) not null,
  HOST_ID              NUMBER(20) not null,
  CALENDAR_EVENT_ID    NUMBER(20) not null,
  NEXT_AGENDA_ITEM_SEQ NUMBER(8) not null
)
;
alter table PN_MEETING
  add constraint MEETING_PK primary key (MEETING_ID);
alter table PN_MEETING
  add constraint MEETING_FK1 foreign key (HOST_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_MEETING
  add constraint MEETING_FK2 foreign key (CALENDAR_EVENT_ID)
  references PN_CALENDAR_EVENT (CALENDAR_EVENT_ID);
alter table PN_MEETING
  add constraint MEETING_OBJ_FK foreign key (MEETING_ID)
  references PN_OBJECT (OBJECT_ID);

prompt
prompt Creating table PN_AGENDA_ITEM
prompt =============================
prompt
create table PN_AGENDA_ITEM
(
  MEETING_ID         NUMBER(20) not null,
  AGENDA_ITEM_ID     NUMBER(20) not null,
  ITEM_NAME          VARCHAR2(80) not null,
  ITEM_DESC          VARCHAR2(500),
  TIME_ALLOTED       VARCHAR2(80),
  OWNER_ID           NUMBER(20),
  STATUS_ID          NUMBER(20) not null,
  ITEM_SEQUENCE      NUMBER(8),
  RECORD_STATUS      VARCHAR2(1) not null,
  MEETING_NOTES_CLOB CLOB
)
;
alter table PN_AGENDA_ITEM
  add constraint AGENDA_ITEM_PK primary key (MEETING_ID, AGENDA_ITEM_ID);
alter table PN_AGENDA_ITEM
  add constraint AGENDA_ITEM_FK1 foreign key (MEETING_ID)
  references PN_MEETING (MEETING_ID);
alter table PN_AGENDA_ITEM
  add constraint AGENDA_ITEM_FK2 foreign key (OWNER_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_AGENDA_ITEM
  add constraint AGENDA_ITEM_OBJ_FK foreign key (AGENDA_ITEM_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_AGENDA_ITEM
  add constraint RECORD_STATUS_VALID6
  check (record_status in ('A','P','D','H'));
create index AGENDA_ITEM_IDX1 on PN_AGENDA_ITEM (AGENDA_ITEM_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_APPLICATION_SPACE
prompt ===================================
prompt
create table PN_APPLICATION_SPACE
(
  APPLICATION_ID   NUMBER(20) not null,
  APPLICATION_NAME VARCHAR2(80) not null,
  APPLICATION_DESC VARCHAR2(1000),
  CREATED_BY_ID    NUMBER(20) not null,
  CREATED_DATETIME DATE not null,
  CRC              DATE not null,
  RECORD_STATUS    VARCHAR2(1) not null
)
;
alter table PN_APPLICATION_SPACE
  add constraint APPLICATION_SPACE_PK primary key (APPLICATION_ID);

prompt
prompt Creating table PN_SPACE_HAS_PERSON
prompt ==================================
prompt
create table PN_SPACE_HAS_PERSON
(
  SPACE_ID                     NUMBER(20) not null,
  PERSON_ID                    NUMBER(20) not null,
  RELATIONSHIP_PERSON_TO_SPACE VARCHAR2(80),
  MEMBER_TYPE_ID               NUMBER(20),
  RELATIONSHIP_SPACE_TO_PERSON VARCHAR2(80),
  RESPONSIBILITIES             VARCHAR2(500),
  MEMBER_TITLE                 VARCHAR2(80),
  RECORD_STATUS                VARCHAR2(1) not null,
  SECURE_KEY                   VARCHAR2(1024)
)
;
alter table PN_SPACE_HAS_PERSON
  add constraint SPACE_HAS_PERSON_PK primary key (SPACE_ID, PERSON_ID);
alter table PN_SPACE_HAS_PERSON
  add constraint SPACE_PERSON_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_SPACE_HAS_PERSON
  add constraint RECORD_STATUS_VALID51
  check (record_status in ('A','P','D','H'));
create index SPACE_HAS_PERSON_IDX1 on PN_SPACE_HAS_PERSON (PERSON_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_ASSIGNMENT
prompt ============================
prompt
create table PN_ASSIGNMENT
(
  SPACE_ID            NUMBER(20) not null,
  PERSON_ID           NUMBER(20) not null,
  OBJECT_ID           NUMBER(20) not null,
  STATUS_ID           NUMBER(3) not null,
  PERCENT_ALLOCATED   NUMBER,
  ROLE                VARCHAR2(80),
  IS_PRIMARY_OWNER    NUMBER(1) default 0 not null,
  RECORD_STATUS       VARCHAR2(1) not null,
  START_DATE          DATE,
  END_DATE            DATE,
  WORK                NUMBER,
  WORK_UNITS          NUMBER,
  WORK_COMPLETE       NUMBER,
  WORK_COMPLETE_UNITS NUMBER,
  DATE_CREATED        DATE,
  MODIFIED_DATE       DATE,
  MODIFIED_BY         NUMBER(20),
  IS_COMPLETE         NUMBER,
  PERCENT_COMPLETE    NUMBER,
  ACTUAL_START        DATE,
  ACTUAL_FINISH       DATE,
  ESTIMATED_FINISH    DATE
--  ASSIGNOR_ID     	  NUMBER(20)
)
;
alter table PN_ASSIGNMENT
  add constraint ASSIGNMENT_PK primary key (SPACE_ID, PERSON_ID, OBJECT_ID);
alter table PN_ASSIGNMENT
  add constraint ASSIGNMENT_FK1 foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_ASSIGNMENT
  add constraint ASSIGNMENT_FK2 foreign key (SPACE_ID, PERSON_ID)
  references PN_SPACE_HAS_PERSON (SPACE_ID, PERSON_ID);
alter table PN_ASSIGNMENT
  add constraint RECORD_STATUS_VALID54
  check (record_status in ('A','P','D','H'));
-- ALTER TABLE PN_ASSIGNMENT
--  ADD CONSTRAINT ASSIGNMENT_FK3 FOREIGN KEY (ASSIGNOR_ID)
--  REFERENCES PN_PERSON (PERSON_ID);
-- CREATE INDEX ASSIGNMENT_IDX5 ON PN_ASSIGNMENT (ASSIGNOR_ID)  TABLESPACE INDEX01;  
create index ASSIGNMENT_IDX1 on PN_ASSIGNMENT (OBJECT_ID) TABLESPACE INDEX01;
create index ASSIGNMENT_IDX2 on PN_ASSIGNMENT (PERSON_ID) TABLESPACE INDEX01;
create index ASSIGNMENT_IDX3 on PN_ASSIGNMENT (START_DATE, END_DATE) TABLESPACE INDEX01;
create index ASSIGNMENT_IDX4 on PN_ASSIGNMENT (RECORD_STATUS) TABLESPACE INDEX01;

prompt
prompt Creating table PN_OBJECT_SPACE
prompt ==============================
prompt
create table PN_OBJECT_SPACE
(
  OBJECT_ID NUMBER(20) not null,
  SPACE_ID  NUMBER(20) not null
)
;
alter table PN_OBJECT_SPACE
  add constraint OBJECT_SPACE_PK primary key (OBJECT_ID, SPACE_ID);
create index OBJECT_SPACE_IDX2 on PN_OBJECT_SPACE (SPACE_ID) TABLESPACE INDEX01;
create index OBJECT_SPACE_IDX_1 on PN_OBJECT_SPACE (OBJECT_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_TIMESHEET
prompt ===========================
prompt
create table PN_TIMESHEET
(
  OBJECT_ID            NUMBER(20) not null,
  PERSON_ID            NUMBER(20) not null,
  STATUS_ID            NUMBER(3) not null,
  START_DATE           DATE not null,
  END_DATE             DATE not null,
  WORK                 NUMBER(20) not null,
  WORK_UNITS           NUMBER(3) not null,
  DATE_SUBMITTED       DATE,
  DATE_APPROVE_REJECT  DATE,
  COMMENTS             VARCHAR2(4000),
  APPROVE_REJECT_BY_ID NUMBER(20),
  RECORD_STATUS        CHAR(1) not null
)
;
alter table PN_TIMESHEET
  add constraint TIMESHEET_PK primary key (OBJECT_ID);
alter table PN_TIMESHEET
  add constraint TIMESHEET_OBJ_FK1 foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_TIMESHEET
  add constraint TIMESHEET_PER_FK2 foreign key (APPROVE_REJECT_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_TIMESHEET
  add constraint TIMESHEET_PER_FK3 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_TIMESHEET
  add constraint RECORD_STATUS_VALID55
  check (record_status in ('A','P','D','H'));
alter table PN_TIMESHEET
  add constraint STATUS_ID_VALID55
  check (status_id in (10,20,30,40,50));
create index TIMESHEET_IDX1 on PN_TIMESHEET (PERSON_ID, STATUS_ID) TABLESPACE INDEX01;
create index TIMESHEET_IDX2 on PN_TIMESHEET (START_DATE, END_DATE, PERSON_ID, STATUS_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_ASSIGNMENT_TIMESHEET
prompt ======================================
prompt
create table PN_ASSIGNMENT_TIMESHEET
(
  OBJECT_ID    NUMBER(20) not null,
  SPACE_ID     NUMBER(20) not null,
  TIMESHEET_ID NUMBER(20) not null
)
;
alter table PN_ASSIGNMENT_TIMESHEET
  add constraint ASSIGNMENT_TIMESHEET_PK primary key (OBJECT_ID, SPACE_ID, TIMESHEET_ID);
alter table PN_ASSIGNMENT_TIMESHEET
  add constraint ASSIGNMENT_TIMESHEET_FK1 foreign key (TIMESHEET_ID)
  references PN_TIMESHEET (OBJECT_ID);
alter table PN_ASSIGNMENT_TIMESHEET
  add constraint OBJECTSPACE_TIMESHEET_FK2 foreign key (OBJECT_ID, SPACE_ID)
  references PN_OBJECT_SPACE (OBJECT_ID, SPACE_ID);

prompt
prompt Creating table PN_ASSIGNMENT_WORK
prompt =================================
prompt
create table PN_ASSIGNMENT_WORK
(
  ASSIGNMENT_WORK_ID   NUMBER(20) not null,
  OBJECT_ID            NUMBER(20) not null,
  PERSON_ID            NUMBER(20) not null,
  WORK_START           DATE,
  WORK_END             DATE,
  WORK                 NUMBER,
  WORK_UNITS           NUMBER,
  WORK_REMAINING       NUMBER,
  WORK_REMAINING_UNITS NUMBER,
  PERCENT_COMPLETE     NUMBER,
  LOG_DATE             DATE,
  MODIFIED_BY          NUMBER,
  SCHEDULED_WORK       NUMBER,
  SCHEDULED_WORK_UNITS NUMBER,
  COMMENTS             VARCHAR2(4000)
)
;
comment on table PN_ASSIGNMENT_WORK
  is 'Contains history of all work that users have logged against a task.';
alter table PN_ASSIGNMENT_WORK
  add constraint ASSIGNMENT_WORK_PK primary key (ASSIGNMENT_WORK_ID);
create index ASSIGNMENT_WORK_IDX1 on PN_ASSIGNMENT_WORK (OBJECT_ID, PERSON_ID) TABLESPACE INDEX01;
create index PN_ASS_WORK_OBJECT_ID_IDX on PN_ASSIGNMENT_WORK (OBJECT_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_AUTHENTICATOR_TYPE
prompt ====================================
prompt
create table PN_AUTHENTICATOR_TYPE
(
  TYPE          VARCHAR2(20) not null,
  DESCRIPTION   VARCHAR2(240),
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_AUTHENTICATOR_TYPE
  add constraint PN_AUTHENTICATOR_TYPE_PK primary key (TYPE);

prompt
prompt Creating table PN_AUTHENTICATOR
prompt ===============================
prompt
create table PN_AUTHENTICATOR
(
  AUTHENTICATOR_ID   NUMBER(20) not null,
  AUTHENTICATOR_TYPE VARCHAR2(20) not null,
  NAME               VARCHAR2(80),
  DESCRIPTION        VARCHAR2(240),
  RECORD_STATUS      VARCHAR2(1) not null
)
;
alter table PN_AUTHENTICATOR
  add constraint AUTHENTICATOR_PK primary key (AUTHENTICATOR_ID);
alter table PN_AUTHENTICATOR
  add constraint PN_AUTHENTICATOR_FK1 foreign key (AUTHENTICATOR_TYPE)
  references PN_AUTHENTICATOR_TYPE (TYPE);

prompt
prompt Creating table PN_BASELINE
prompt ==========================
prompt
create table PN_BASELINE
(
  BASELINE_ID           NUMBER(20) not null,
  OBJECT_ID             NUMBER(20) not null,
  NAME                  VARCHAR2(255),
  DESCRIPTION           VARCHAR2(2000),
  IS_DEFAULT_FOR_OBJECT NUMBER(1),
  DATE_CREATED          DATE,
  DATE_MODIFIED         DATE,
  RECORD_STATUS         VARCHAR2(1) not null
)
;
alter table PN_BASELINE
  add constraint BASELINE_PK primary key (BASELINE_ID);

prompt
prompt Creating table PN_BASELINE_HAS_PLAN
prompt ===================================
prompt
create table PN_BASELINE_HAS_PLAN
(
  BASELINE_ID     NUMBER(20) not null,
  PLAN_ID         NUMBER(20) not null,
  PLAN_VERSION_ID NUMBER(20) not null
)
;
alter table PN_BASELINE_HAS_PLAN
  add constraint BASELINE_HAS_PLAN_PK primary key (BASELINE_ID, PLAN_ID);
alter table PN_BASELINE_HAS_PLAN
  add constraint BASELINE_HAS_PLAN_FK foreign key (BASELINE_ID)
  references PN_BASELINE (BASELINE_ID);

prompt
prompt Creating table PN_BASELINE_HAS_TASK
prompt ===================================
prompt
create table PN_BASELINE_HAS_TASK
(
  BASELINE_ID     NUMBER(20) not null,
  TASK_ID         NUMBER(20) not null,
  TASK_VERSION_ID NUMBER(20) not null
)
;
alter table PN_BASELINE_HAS_TASK
  add constraint BASELINE_HAS_TASK_PK primary key (BASELINE_ID, TASK_ID);
alter table PN_BASELINE_HAS_TASK
  add constraint BASELINE_HAS_TASK_FK foreign key (BASELINE_ID)
  references PN_BASELINE (BASELINE_ID);

prompt
prompt Creating table PN_LICENSE_CERTIFICATE
prompt =====================================
prompt
create table PN_LICENSE_CERTIFICATE
(
  CERTIFICATE_ID NUMBER(20) not null
)
;
alter table PN_LICENSE_CERTIFICATE
  add constraint PN_LICENSE_CERTIFICATE_PK primary key (CERTIFICATE_ID);

prompt
prompt Creating table PN_LICENSE_STATUS_REASONS
prompt ========================================
prompt
create table PN_LICENSE_STATUS_REASONS
(
  REASON_CODE NUMBER(3) not null,
  SHORT_NAME  VARCHAR2(200) not null,
  MESSAGE     VARCHAR2(1000) not null
)
;
alter table PN_LICENSE_STATUS_REASONS
  add constraint PN_STATUS_REASON_CODE_PK primary key (REASON_CODE);

prompt
prompt Creating table PN_PAYMENT_MODEL_TYPE
prompt ====================================
prompt
create table PN_PAYMENT_MODEL_TYPE
(
  MODEL_TYPE_ID NUMBER(20) not null,
  CLASS_NAME    VARCHAR2(1000) not null,
  DESCRIPTION   VARCHAR2(500) not null
)
;
alter table PN_PAYMENT_MODEL_TYPE
  add constraint PN_PAYMENT_MODEL_TYPE_PK primary key (MODEL_TYPE_ID);

prompt
prompt Creating table PN_PAYMENT_MODEL
prompt ===============================
prompt
create table PN_PAYMENT_MODEL
(
  PAYMENT_MODEL_ID NUMBER(20) not null,
  MODEL_TYPE_ID    NUMBER(20) not null
)
;
alter table PN_PAYMENT_MODEL
  add constraint PN_PAYMENT_MODEL_PK primary key (PAYMENT_MODEL_ID);
alter table PN_PAYMENT_MODEL
  add constraint PN_PAYMENT_MODEL_FK1 foreign key (MODEL_TYPE_ID)
  references PN_PAYMENT_MODEL_TYPE (MODEL_TYPE_ID) on delete set null;

prompt
prompt Creating table PN_PAYMENT_INFORMATION
prompt =====================================
prompt
create table PN_PAYMENT_INFORMATION
(
  PAYMENT_ID       NUMBER(20) not null,
  PAYMENT_MODEL_ID NUMBER(20) not null,
  PARTY_ID         NUMBER(20)
)
;
alter table PN_PAYMENT_INFORMATION
  add constraint PN_PAYMENT_INFORMATION_PK primary key (PAYMENT_ID);
alter table PN_PAYMENT_INFORMATION
  add constraint PN_PAYMENT_INFORMATION_FK1 foreign key (PAYMENT_MODEL_ID)
  references PN_PAYMENT_MODEL (PAYMENT_MODEL_ID) on delete set null;

prompt
prompt Creating table PN_LICENSE
prompt =========================
prompt
create table PN_LICENSE
(
  LICENSE_ID          NUMBER(20) not null,
  CERTIFICATE_ID      NUMBER(20) not null,
  PAYMENT_ID          NUMBER(20) not null,
  LICENSE_KEY_VALUE   VARCHAR2(100) not null,
  IS_TRIAL            NUMBER(1) not null,
  LICENSE_STATUS      NUMBER(3),
  STATUS_REASON_CODE  NUMBER(3),
  RESPONSIBLE_USER_ID NUMBER(20)
)
;
alter table PN_LICENSE
  add constraint PN_LICENSE_PK primary key (LICENSE_ID);
alter table PN_LICENSE
  add constraint LICENSE_KEY_UNIQUE unique (LICENSE_KEY_VALUE);
alter table PN_LICENSE
  add constraint PN_LICENSE_FK1 foreign key (CERTIFICATE_ID)
  references PN_LICENSE_CERTIFICATE (CERTIFICATE_ID) on delete set null;
alter table PN_LICENSE
  add constraint PN_LICENSE_FK2 foreign key (PAYMENT_ID)
  references PN_PAYMENT_INFORMATION (PAYMENT_ID) on delete set null;
alter table PN_LICENSE
  add constraint PN_LICENSE_FK4 foreign key (STATUS_REASON_CODE)
  references PN_LICENSE_STATUS_REASONS (REASON_CODE);
alter table PN_LICENSE
  add constraint PN_LICENSE_FK5 foreign key (RESPONSIBLE_USER_ID)
  references PN_PERSON (PERSON_ID);

prompt
prompt Creating table PN_BILL
prompt ======================
prompt
create table PN_BILL
(
  BILL_ID                       NUMBER(20) not null,
  UNIT_PRICE_VALUE              NUMBER not null,
  QUANTITY_AMOUNT               NUMBER not null,
  QUANTITY_UOM_ID               NUMBER not null,
  CATEGORY_ID                   NUMBER not null,
  PART_DETAILS_PART_NUMBER      VARCHAR2(500) not null,
  PART_DETAILS_PART_DESCRIPTION VARCHAR2(1000),
  GROUP_TYPE_ID                 NUMBER(20) not null,
  GROUP_VALUE                   VARCHAR2(500),
  GROUP_DESCRIPTION             VARCHAR2(1000),
  BILL_STATUS_ID                NUMBER(20) not null,
  RECORD_STATUS                 VARCHAR2(1),
  ORIGINATING_LICENSE_ID        NUMBER(20) not null,
  ORIGINATING_PERSON_ID         NUMBER(20) not null,
  ORIGINATING_PAYMENT_ID        NUMBER(20) not null,
  CREATION_DATETIME             DATE default (sysdate) not null,
  DUE_DATETIME                  DATE default (sysdate) not null
)
;
alter table PN_BILL
  add constraint PN_BILL_PK primary key (BILL_ID);
alter table PN_BILL
  add constraint PN_BILL_FK1 foreign key (ORIGINATING_LICENSE_ID)
  references PN_LICENSE (LICENSE_ID);
alter table PN_BILL
  add constraint PN_BILL_FK2 foreign key (ORIGINATING_PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_BILL
  add constraint PN_BILL_FK3 foreign key (ORIGINATING_PAYMENT_ID)
  references PN_PAYMENT_INFORMATION (PAYMENT_ID);

prompt
prompt Creating table PN_BOOKMARK
prompt ==========================
prompt
create table PN_BOOKMARK
(
  BOOKMARK_ID    NUMBER(20) not null,
  NAME           VARCHAR2(240) not null,
  DESCRIPTION    VARCHAR2(500),
  URL            VARCHAR2(500) not null,
  STATUS_ID      NUMBER(20),
  OWNER_ID       NUMBER(20),
  COMMENTS       VARCHAR2(500),
  MODIFIED_DATE  DATE,
  MODIFIED_BY_ID NUMBER(20),
  RECORD_STATUS  VARCHAR2(1) not null,
  CRC            DATE not null
)
;
alter table PN_BOOKMARK
  add constraint PN_BOOKMARK_PK primary key (BOOKMARK_ID);
alter table PN_BOOKMARK
  add constraint PN_BOOKMARK_FK1 foreign key (OWNER_ID)
  references PN_PERSON (PERSON_ID) on delete set null;
alter table PN_BOOKMARK
  add constraint PN_BOOKMARK_FK2 foreign key (MODIFIED_BY_ID)
  references PN_PERSON (PERSON_ID) on delete set null;

prompt
prompt Creating table PN_LANGUAGE
prompt ==========================
prompt
create table PN_LANGUAGE
(
  LANGUAGE_CODE VARCHAR2(2) not null,
  LANGUAGE_NAME VARCHAR2(500) not null,
  CHARACTER_SET VARCHAR2(500) not null,
  IS_ACTIVE     NUMBER(1) default 1 not null
)
;
alter table PN_LANGUAGE
  add constraint PN_LANGUAGE_PK primary key (LANGUAGE_CODE);

prompt
prompt Creating table PN_BRAND
prompt =======================
prompt
create table PN_BRAND
(
  BRAND_ID          NUMBER(20) not null,
  BRAND_ABBRV       VARCHAR2(20) not null,
  BRAND_NAME        VARCHAR2(80) not null,
  BRAND_DESC        VARCHAR2(1000),
  DEFAULT_LANGUAGE  VARCHAR2(2),
  IS_SYSTEM_DEFAULT NUMBER(1) default 0 not null,
  RECORD_STATUS     VARCHAR2(1)
)
;
alter table PN_BRAND
  add constraint BRAND_PK primary key (BRAND_ID);
alter table PN_BRAND
  add constraint PN_BRAND_FK1 foreign key (DEFAULT_LANGUAGE)
  references PN_LANGUAGE (LANGUAGE_CODE) on delete set null;

prompt
prompt Creating table PN_BRAND_HAS_HOST
prompt ================================
prompt
create table PN_BRAND_HAS_HOST
(
  BRAND_ID      NUMBER(20) not null,
  HOST_NAME     VARCHAR2(120) not null,
  RECORD_STATUS VARCHAR2(1)
)
;
alter table PN_BRAND_HAS_HOST
  add constraint PN_BRAND_HAS_HOST_PK primary key (BRAND_ID, HOST_NAME);
alter table PN_BRAND_HAS_HOST
  add constraint PN_BRAND_HAS_HOST_FK1 foreign key (BRAND_ID)
  references PN_BRAND (BRAND_ID) on delete set null;

prompt
prompt Creating table PN_BRAND_SUPPORTS_LANGUAGE
prompt =========================================
prompt
create table PN_BRAND_SUPPORTS_LANGUAGE
(
  BRAND_ID      NUMBER(20) not null,
  LANGUAGE_CODE VARCHAR2(2) not null
)
;
alter table PN_BRAND_SUPPORTS_LANGUAGE
  add constraint PN_BRAND_SUPPORTS_LANGUAGE_PK primary key (BRAND_ID, LANGUAGE_CODE);
alter table PN_BRAND_SUPPORTS_LANGUAGE
  add constraint PN_BRAND_SUPPORTS_LANGUAGE_FK1 foreign key (LANGUAGE_CODE)
  references PN_LANGUAGE (LANGUAGE_CODE) on delete set null;

prompt
prompt Creating table PN_BUSINESS
prompt ==========================
prompt
create table PN_BUSINESS
(
  BUSINESS_ID          NUMBER(20) not null,
  ADDRESS_ID           NUMBER(20) not null,
  BUSINESS_NAME        VARCHAR2(80),
  BUSINESS_DESC        VARCHAR2(1000),
  BUSINESS_TYPE        VARCHAR2(80),
  LOGO_IMAGE_ID        NUMBER(20),
  IS_LOCAL             NUMBER(1) default 1 not null,
  REMOTE_HOST_ID       NUMBER(20),
  REMOTE_BUSINESS_ID   NUMBER(20),
  RECORD_STATUS        VARCHAR2(1) not null,
  IS_MASTER            NUMBER(1) default 0 not null,
  BUSINESS_CATEGORY_ID NUMBER(20),
  BRAND_ID             NUMBER(20),
  BILLING_ACCOUNT_ID   NUMBER(20)
)
;
alter table PN_BUSINESS
  add constraint BUSINESS_PK primary key (BUSINESS_ID);
alter table PN_BUSINESS
  add constraint BUSINESS_FK1 foreign key (ADDRESS_ID)
  references PN_ADDRESS (ADDRESS_ID);
alter table PN_BUSINESS
  add constraint BUSINESS_OBJ_FK foreign key (BUSINESS_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_BUSINESS
  add constraint RECORD_STATUS_VALID7
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_BUSINESS_CATEGORY
prompt ===================================
prompt
create table PN_BUSINESS_CATEGORY
(
  BUSINESS_CATEGORY_ID NUMBER(20) not null,
  CATEGORY_NAME        VARCHAR2(80) not null,
  CATEGORY_DESCRIPTION VARCHAR2(80) not null
)
;
alter table PN_BUSINESS_CATEGORY
  add constraint BUSINESS_CATEGORY_PK primary key (BUSINESS_CATEGORY_ID);

prompt
prompt Creating table PN_BUSINESS_SPACE
prompt ================================
prompt
create table PN_BUSINESS_SPACE
(
  BUSINESS_SPACE_ID     NUMBER(20) not null,
  BUSINESS_ID           NUMBER(20) not null,
  SPACE_TYPE            NUMBER(20),
  COMPLETE_PORTFOLIO_ID NUMBER(20),
  RECORD_STATUS         VARCHAR2(1) not null,
  INCLUDES_EVERYONE     NUMBER(1) default 0 not null
)
;
alter table PN_BUSINESS_SPACE
  add constraint BUSINESS_SPACE_PK primary key (BUSINESS_SPACE_ID);
alter table PN_BUSINESS_SPACE
  add constraint BUSINESS_SPACE_FK1 foreign key (BUSINESS_ID)
  references PN_BUSINESS (BUSINESS_ID);
alter table PN_BUSINESS_SPACE
  add constraint RECORD_STATUS_VALID8
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_CALCULATION_FIELD_FORMULA
prompt ===========================================
prompt
create table PN_CALCULATION_FIELD_FORMULA
(
  CLASS_ID NUMBER(20) not null,
  FIELD_ID NUMBER(20) not null,
  ORDER_ID NUMBER(20) not null,
  OP_VALUE VARCHAR2(80),
  OP_TYPE  VARCHAR2(80)
)
;
alter table PN_CALCULATION_FIELD_FORMULA
  add constraint CALCULATION_FIELD_PROPERTY_PK primary key (CLASS_ID, FIELD_ID, ORDER_ID);

prompt
prompt Creating table PN_CALENDAR
prompt ==========================
prompt
create table PN_CALENDAR
(
  CALENDAR_ID          NUMBER(20) not null,
  CALENDAR_NAME        VARCHAR2(255),
  CALENDAR_DESCRIPTION VARCHAR2(500),
  IS_BASE_CALENDAR     NUMBER(1) default 1 not null,
  RECORD_STATUS        VARCHAR2(1) not null
)
;
alter table PN_CALENDAR
  add constraint CALENDAR_PK primary key (CALENDAR_ID);
alter table PN_CALENDAR
  add constraint CALENDAR_OBJ_FK foreign key (CALENDAR_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_CALENDAR
  add constraint RECORD_STATUS_VALID9
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_CALENDAR_HAS_EVENT
prompt ====================================
prompt
create table PN_CALENDAR_HAS_EVENT
(
  CALENDAR_ID       NUMBER(20) not null,
  CALENDAR_EVENT_ID NUMBER(20) not null
)
;
alter table PN_CALENDAR_HAS_EVENT
  add constraint CALENDAR_HAS_EVENT_PK primary key (CALENDAR_ID, CALENDAR_EVENT_ID);
alter table PN_CALENDAR_HAS_EVENT
  add constraint CALENDAR_HAS_EVENT_FK1 foreign key (CALENDAR_ID)
  references PN_CALENDAR (CALENDAR_ID);
alter table PN_CALENDAR_HAS_EVENT
  add constraint CALENDAR_HAS_EVENT_FK2 foreign key (CALENDAR_EVENT_ID)
  references PN_CALENDAR_EVENT (CALENDAR_EVENT_ID);
create unique index CALENDAR_HAS_EVENT_IDX1 on PN_CALENDAR_HAS_EVENT (CALENDAR_EVENT_ID, CALENDAR_ID);

prompt
prompt Creating table PN_CAL_EVENT_HAS_ATTENDEE
prompt ========================================
prompt
create table PN_CAL_EVENT_HAS_ATTENDEE
(
  PERSON_ID         NUMBER(20) not null,
  CALENDAR_EVENT_ID NUMBER(20) not null,
  STATUS_ID         NUMBER(20) not null,
  ATTENDEE_COMMENT  VARCHAR2(80)
)
;
alter table PN_CAL_EVENT_HAS_ATTENDEE
  add constraint CAL_EVENT_HAS_ATTENDEE_PK primary key (PERSON_ID, CALENDAR_EVENT_ID);
alter table PN_CAL_EVENT_HAS_ATTENDEE
  add constraint CAL_EVENT_ATTENDEE_FK1 foreign key (CALENDAR_EVENT_ID)
  references PN_CALENDAR_EVENT (CALENDAR_EVENT_ID);
alter table PN_CAL_EVENT_HAS_ATTENDEE
  add constraint CAL_EVENT_ATTENDEE_FK2 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
create index CAL_EVENT_HAS_ATTENDEE_IDX1 on PN_CAL_EVENT_HAS_ATTENDEE (CALENDAR_EVENT_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_CATEGORY
prompt ==========================
prompt
create table PN_CATEGORY
(
  CATEGORY_ID NUMBER(20) not null,
  NAME        VARCHAR2(80) not null,
  DESCRIPTION VARCHAR2(500)
)
;
alter table PN_CATEGORY
  add constraint CATEGORY_PK primary key (CATEGORY_ID);

prompt
prompt Creating table PN_CATEGORY_HAS_CATEGORY
prompt =======================================
prompt
create table PN_CATEGORY_HAS_CATEGORY
(
  PARENT_CATEGORY_ID NUMBER(20) not null,
  CHILD_CATEGORY_ID  NUMBER(20) not null
)
;
alter table PN_CATEGORY_HAS_CATEGORY
  add constraint CATEGORY_HAS_CATEGORY_PK primary key (PARENT_CATEGORY_ID, CHILD_CATEGORY_ID);

prompt
prompt Creating table PN_CREDIT_CARD_TRANSACTION
prompt =========================================
prompt
create table PN_CREDIT_CARD_TRANSACTION
(
  TRANSACTION_ID              NUMBER(20) not null,
  VENDOR_TRANSACTION_ID       VARCHAR2(20),
  DATE_SUBMITTED              DATE not null,
  TRANSACTION_TYPE            VARCHAR2(5) not null,
  TRANSACTION_AMOUNT          NUMBER not null,
  TRANSACTION_AMOUNT_CURRENCY VARCHAR2(3) not null,
  AUTHORIZATION_CODE          VARCHAR2(20),
  IS_DUPLICATE                NUMBER(1)
)
;
alter table PN_CREDIT_CARD_TRANSACTION
  add constraint CREDIT_CARD_TRANSACTION_PK primary key (TRANSACTION_ID);

prompt
prompt Creating table PN_CC_TRANSACTION_PAYMENT
prompt ========================================
prompt
create table PN_CC_TRANSACTION_PAYMENT
(
  TRANSACTION_ID NUMBER(20) not null,
  PAYMENT_ID     NUMBER(20) not null
)
;
alter table PN_CC_TRANSACTION_PAYMENT
  add constraint CC_TRANSACTION_PAYMENT_PK primary key (TRANSACTION_ID, PAYMENT_ID);
alter table PN_CC_TRANSACTION_PAYMENT
  add constraint CC_TRANSACTION_PAYMENT_FK1 foreign key (TRANSACTION_ID)
  references PN_CREDIT_CARD_TRANSACTION (TRANSACTION_ID) on delete set null;
alter table PN_CC_TRANSACTION_PAYMENT
  add constraint CC_TRANSACTION_PAYMENT_FK2 foreign key (PAYMENT_ID)
  references PN_PAYMENT_INFORMATION (PAYMENT_ID) on delete set null;

prompt
prompt Creating table PN_CLASS_TYPE
prompt ============================
prompt
create table PN_CLASS_TYPE
(
  CLASS_TYPE_ID   NUMBER(20) not null,
  CLASS_TYPE_NAME VARCHAR2(80) not null,
  CLASS_TYPE_DESC VARCHAR2(500)
)
;
alter table PN_CLASS_TYPE
  add constraint CLASS_TYPE_PK primary key (CLASS_TYPE_ID);

prompt
prompt Creating table PN_CLASS
prompt =======================
prompt
create table PN_CLASS
(
  CLASS_ID                  NUMBER(20) not null,
  CLASS_NAME                VARCHAR2(80),
  CLASS_DESC                VARCHAR2(500),
  CLASS_ABBREVIATION        VARCHAR2(20),
  CLASS_TYPE_ID             NUMBER(20),
  OWNER_SPACE_ID            NUMBER(20),
  METHODOLOGY_ID            NUMBER(20),
  MAX_ROW                   NUMBER(8) default 0 not null,
  MAX_COLUMN                NUMBER(8) default 0 not null,
  NEXT_DATA_SEQ             NUMBER(8) default 1 not null,
  DATA_TABLE_SEQ            NUMBER(7) default 1 not null,
  MASTER_TABLE_NAME         VARCHAR2(80),
  DATA_TABLE_KEY            VARCHAR2(80),
  IS_SEQUENCED              NUMBER(1) default 1 not null,
  IS_SYSTEM_CLASS           NUMBER(1) default 0 not null,
  CRC                       DATE,
  RECORD_STATUS             VARCHAR2(1) not null,
  SUPPORTS_DISCUSSION_GROUP NUMBER(1),
  SUPPORTS_DOCUMENT_VAULT   NUMBER(1),
  SUPPORTS_ASSIGNMENT       NUMBER(1) default 0
)
;
alter table PN_CLASS
  add constraint CLASS_PK primary key (CLASS_ID);
alter table PN_CLASS
  add constraint CLASS_FK1 foreign key (CLASS_TYPE_ID)
  references PN_CLASS_TYPE (CLASS_TYPE_ID);
alter table PN_CLASS
  add constraint CLASS_OBJ_FK foreign key (CLASS_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_CLASS
  add constraint RECORD_STATUS_VALID10
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_CLASS_DOMAIN
prompt ==============================
prompt
create table PN_CLASS_DOMAIN
(
  DOMAIN_ID     NUMBER(20) not null,
  DOMAIN_NAME   VARCHAR2(80),
  DOMAIN_TYPE   VARCHAR2(80),
  DOMAIN_DESC   VARCHAR2(500),
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_CLASS_DOMAIN
  add constraint CLASS_DOMAIN_PK primary key (DOMAIN_ID);
alter table PN_CLASS_DOMAIN
  add constraint CLASS_DOMAIN_OBJ_FK foreign key (DOMAIN_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_CLASS_DOMAIN
  add constraint RECORD_STATUS_VALID11
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_CLASS_DOMAIN_VALUES
prompt =====================================
prompt
create table PN_CLASS_DOMAIN_VALUES
(
  DOMAIN_ID         NUMBER(20) not null,
  DOMAIN_VALUE_ID   NUMBER(20) not null,
  DOMAIN_VALUE_NAME VARCHAR2(80) not null,
  DOMAIN_VALUE_SEQ  NUMBER(8),
  DOMAIN_VALUE_DESC VARCHAR2(500),
  IS_DEFAULT        NUMBER(1) not null,
  SOURCE_VALUE_ID   NUMBER(20),
  RECORD_STATUS     VARCHAR2(1) not null
)
;
alter table PN_CLASS_DOMAIN_VALUES
  add constraint CLASS_DOMAIN_VALUES_PK primary key (DOMAIN_ID, DOMAIN_VALUE_ID);
alter table PN_CLASS_DOMAIN_VALUES
  add constraint CLASS_DOMAIN_VALUES_FK1 foreign key (DOMAIN_ID)
  references PN_CLASS_DOMAIN (DOMAIN_ID);
alter table PN_CLASS_DOMAIN_VALUES
  add constraint CLASS_DOMAIN_VAL_OBJ_FK foreign key (DOMAIN_VALUE_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_CLASS_DOMAIN_VALUES
  add constraint RECORD_STATUS_VALID12
  check (record_status in ('A','P','D','H'));
create index CLASS_DOMAIN_VALUES_IDX1 on PN_CLASS_DOMAIN_VALUES (DOMAIN_VALUE_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_ELEMENT
prompt =========================
prompt
create table PN_ELEMENT
(
  ELEMENT_ID        NUMBER(20) not null,
  ELEMENT_NAME      VARCHAR2(80) not null,
  ELEMENT_DESC      VARCHAR2(500),
  ELEMENT_TYPE      NUMBER(20),
  ELEMENT_LABEL     VARCHAR2(80),
  DB_FIELD_DATATYPE VARCHAR2(80),
  RECORD_STATUS     VARCHAR2(1) not null
)
;
alter table PN_ELEMENT
  add constraint ELEMENT_PK primary key (ELEMENT_ID);
alter table PN_ELEMENT
  add constraint RECORD_STATUS_VALID13
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_CLASS_FIELD
prompt =============================
prompt
create table PN_CLASS_FIELD
(
  SPACE_ID           NUMBER not null,
  CLASS_ID           NUMBER(20) not null,
  FIELD_ID           NUMBER(20) not null,
  DOMAIN_ID          NUMBER(20),
  DATA_COLUMN_NAME   VARCHAR2(80),
  ELEMENT_ID         NUMBER(20),
  DATA_TABLE_NAME    VARCHAR2(80),
  FIELD_LABEL        VARCHAR2(80),
  DATA_COLUMN_SIZE   NUMBER(8),
  DATA_COLUMN_EXISTS NUMBER(1) default 0 not null,
  ROW_NUM            NUMBER(8) default 1 not null,
  ROW_SPAN           NUMBER(8) default 1 not null,
  COLUMN_NUM         NUMBER(8) default 1 not null,
  COLUMN_SPAN        NUMBER(8) default 1,
  COLUMN_ID          NUMBER(20),
  USE_DEFAULT        NUMBER(1) default 0 not null,
  FIELD_GROUP        VARCHAR2(80),
  HAS_DOMAIN         NUMBER(1) default 0 not null,
  MAX_VALUE          VARCHAR2(80),
  MIN_VALUE          VARCHAR2(80),
  DEFAULT_VALUE      VARCHAR2(80),
  IS_MULTI_SELECT    NUMBER(1) default 0 not null,
  SOURCE_FIELD_ID    NUMBER(20),
  CRC                DATE,
  RECORD_STATUS      VARCHAR2(1) not null,
  DATA_COLUMN_SCALE  NUMBER(8),
  INSTRUCTIONS_CLOB  CLOB,
  IS_VALUE_REQUIRED  NUMBER(1) default 0
)
;
alter table PN_CLASS_FIELD
  add constraint CLASS_FIELD_PK primary key (SPACE_ID, CLASS_ID, FIELD_ID);
alter table PN_CLASS_FIELD
  add constraint CLASS_FIELD_FK1 foreign key (ELEMENT_ID)
  references PN_ELEMENT (ELEMENT_ID);
alter table PN_CLASS_FIELD
  add constraint CLASS_FIELD_FK2 foreign key (CLASS_ID)
  references PN_CLASS (CLASS_ID);
alter table PN_CLASS_FIELD
  add constraint CLASS_FIELD_FK3 foreign key (DOMAIN_ID)
  references PN_CLASS_DOMAIN (DOMAIN_ID);
alter table PN_CLASS_FIELD
  add constraint CLASS_FIELD_OBJ_FK foreign key (FIELD_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_CLASS_FIELD
  add constraint RECORD_STATUS_VALID14
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_CLIENT_TYPE
prompt =============================
prompt
create table PN_CLIENT_TYPE
(
  CLIENT_TYPE_ID NUMBER(20) not null,
  CLIENT_NAME    VARCHAR2(80) not null,
  CLIENT_DESC    VARCHAR2(500)
)
;
alter table PN_CLIENT_TYPE
  add constraint CLIENT_TYPE_PK primary key (CLIENT_TYPE_ID);

prompt
prompt Creating table PN_CLASS_FIELD_PROPERTY
prompt ======================================
prompt
create table PN_CLASS_FIELD_PROPERTY
(
  CLASS_ID       NUMBER(20) not null,
  FIELD_ID       NUMBER(20) not null,
  PROPERTY       VARCHAR2(80) not null,
  CLIENT_TYPE_ID NUMBER(20) not null,
  PROPERTY_TYPE  VARCHAR2(80),
  VALUE          VARCHAR2(80)
)
;
alter table PN_CLASS_FIELD_PROPERTY
  add constraint CLASS_FIELD_PROPERTY_PK primary key (CLASS_ID, FIELD_ID, PROPERTY, CLIENT_TYPE_ID);
alter table PN_CLASS_FIELD_PROPERTY
  add constraint CLASS_FIELD_PROPERTY_FK1 foreign key (CLIENT_TYPE_ID)
  references PN_CLIENT_TYPE (CLIENT_TYPE_ID);
create index CLASS_FIELD_PROPERTY_IDX1 on PN_CLASS_FIELD_PROPERTY (CLIENT_TYPE_ID, CLASS_ID, FIELD_ID, PROPERTY) TABLESPACE INDEX01;

prompt
prompt Creating table PN_CLASS_HAS_WORKFLOW
prompt ====================================
prompt
create table PN_CLASS_HAS_WORKFLOW
(
  CLASS_ID    NUMBER(20) not null,
  WORKFLOW_ID NUMBER(20) not null,
  IS_DEFAULT  NUMBER(1)
)
;
alter table PN_CLASS_HAS_WORKFLOW
  add constraint CLASS_HAS_WORKFLOW_PK primary key (CLASS_ID, WORKFLOW_ID);

prompt
prompt Creating table PN_CLASS_INSTANCE
prompt ================================
prompt
create table PN_CLASS_INSTANCE
(
  CLASS_INSTANCE_ID NUMBER(20) not null,
  CLASS_ID          NUMBER(20) not null,
  CRC               DATE not null,
  RECORD_STATUS     VARCHAR2(1) not null,
  SPACE_ID          NUMBER(20) not null
)
;
alter table PN_CLASS_INSTANCE
  add constraint CLASS_INSTANCE_PK primary key (CLASS_INSTANCE_ID);

prompt
prompt Creating table PN_CLASS_LIST
prompt ============================
prompt
create table PN_CLASS_LIST
(
  CLASS_ID       NUMBER(20) not null,
  LIST_ID        NUMBER(20) not null,
  LIST_NAME      VARCHAR2(80),
  FIELD_CNT      NUMBER(8),
  LIST_DESC      VARCHAR2(500),
  IS_SHARED      NUMBER(1) default 0 not null,
  IS_ADMIN       NUMBER(1) default 0 not null,
  OWNER_SPACE_ID NUMBER(20) not null,
  CRC            DATE,
  RECORD_STATUS  VARCHAR2(1) not null
)
;
alter table PN_CLASS_LIST
  add constraint CLASS_LIST_PK primary key (CLASS_ID, LIST_ID);
alter table PN_CLASS_LIST
  add constraint CLASS_LIST_FK1 foreign key (CLASS_ID)
  references PN_CLASS (CLASS_ID);
alter table PN_CLASS_LIST
  add constraint RECORD_STATUS_VALID17
  check (record_status in ('A','P','D','H'));
create index CLASS_LIST_IDX1 on PN_CLASS_LIST (LIST_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_CLASS_LIST_FIELD
prompt ==================================
prompt
create table PN_CLASS_LIST_FIELD
(
  CLASS_ID           NUMBER(20) not null,
  LIST_ID            NUMBER(20) not null,
  FIELD_ID           NUMBER(20) not null,
  FIELD_WIDTH        NUMBER(8),
  FIELD_ORDER        NUMBER(8),
  WRAP_MODE          NUMBER(1) default 0 not null,
  IS_SUBFIELD        NUMBER(1) default 0,
  IS_LIST_FIELD      NUMBER(1) default 0,
  IS_SORT_FIELD      NUMBER(1) default 0 not null,
  SORT_ORDER         NUMBER(2),
  SORT_ASCENDING     NUMBER(1) default 1,
  IS_CALCULATE_TOTAL NUMBER(1)
)
;
alter table PN_CLASS_LIST_FIELD
  add constraint CLASS_LIST_FIELD_PK primary key (CLASS_ID, LIST_ID, FIELD_ID);
alter table PN_CLASS_LIST_FIELD
  add constraint CLASS_LIST_FIELD_FK1 foreign key (CLASS_ID, LIST_ID)
  references PN_CLASS_LIST (CLASS_ID, LIST_ID);
create index CLASS_LIST_FIELD_IDX1 on PN_CLASS_LIST_FIELD (FIELD_ID, CLASS_ID, LIST_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_CLASS_LIST_FILTER
prompt ===================================
prompt
create table PN_CLASS_LIST_FILTER
(
  CLASS_ID     NUMBER(20) not null,
  LIST_ID      NUMBER(20) not null,
  FIELD_ID     NUMBER(20) not null,
  VALUE_ID     NUMBER(20) not null,
  OPERATOR     VARCHAR2(10) not null,
  FILTER_VALUE VARCHAR2(80) not null
)
;
alter table PN_CLASS_LIST_FILTER
  add constraint CLASS_LIST_FILTER_PK primary key (CLASS_ID, LIST_ID, FIELD_ID, VALUE_ID);
create index CLASS_LIST_FILTER_IDX1 on PN_CLASS_LIST_FILTER (CLASS_ID, LIST_ID) TABLESPACE INDEX01;
create index CLASS_LIST_FILTER_IDX2 on PN_CLASS_LIST_FILTER (CLASS_ID, LIST_ID, FIELD_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_CLASS_TYPE_ELEMENT
prompt ====================================
prompt
create table PN_CLASS_TYPE_ELEMENT
(
  CLASS_TYPE_ID NUMBER(20) not null,
  ELEMENT_ID    NUMBER(20) not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_CLASS_TYPE_ELEMENT
  add constraint CLASS_TYPE_ELEMENT_PK primary key (CLASS_TYPE_ID, ELEMENT_ID);
alter table PN_CLASS_TYPE_ELEMENT
  add constraint CLASS_TYPE_ELEMENT_FK1 foreign key (ELEMENT_ID)
  references PN_ELEMENT (ELEMENT_ID);
alter table PN_CLASS_TYPE_ELEMENT
  add constraint CLASS_TYPE_ELEMENT_FK2 foreign key (CLASS_TYPE_ID)
  references PN_CLASS_TYPE (CLASS_TYPE_ID);
alter table PN_CLASS_TYPE_ELEMENT
  add constraint RECORD_STATUS_VALID19
  check (record_status in ('A','P','D','H'));
create index CLASS_TYPE_ELEMENT_IDX1 on PN_CLASS_TYPE_ELEMENT (ELEMENT_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_CODE_TYPE
prompt ===========================
prompt
create table PN_CODE_TYPE
(
  CODE_TYPE_ID   NUMBER(20) not null,
  CODE_TYPE_NAME VARCHAR2(80),
  DESCRIPTION    VARCHAR2(500)
)
;
alter table PN_CODE_TYPE
  add constraint CODE_TYPE_PK primary key (CODE_TYPE_ID);

prompt
prompt Creating table PN_CONFIGURATION_SPACE
prompt =====================================
prompt
create table PN_CONFIGURATION_SPACE
(
  CONFIGURATION_ID   NUMBER(20) not null,
  CONFIGURATION_NAME VARCHAR2(80) not null,
  CONFIGURATION_DESC VARCHAR2(1000),
  CREATED_BY_ID      NUMBER(20) not null,
  CREATED_DATETIME   DATE not null,
  MODIFIED_BY_ID     NUMBER(20),
  MODIFIED_DATETIME  DATE,
  CRC                DATE not null,
  RECORD_STATUS      VARCHAR2(1) not null,
  BRAND_ID           NUMBER(20) not null
)
;
alter table PN_CONFIGURATION_SPACE
  add constraint CONFIGURATION_SPACE_PK primary key (CONFIGURATION_ID);

prompt
prompt Creating table PN_CUSTOM_CODE
prompt =============================
prompt
create table PN_CUSTOM_CODE
(
  OBJECT_ID             NUMBER(20) not null,
  CODE_TYPE_ID          NUMBER(20) not null,
  CODE_NAME             VARCHAR2(80) not null,
  CODE_DESC             VARCHAR2(80),
  CODE_URL              VARCHAR2(240),
  PRESENTATION_SEQUENCE NUMBER(8),
  IS_DEFAULT            NUMBER(1) not null,
  RECORD_STATUS         VARCHAR2(1) not null
)
;
alter table PN_CUSTOM_CODE
  add constraint CUSTOM_CODE_PK primary key (OBJECT_ID, CODE_TYPE_ID);
alter table PN_CUSTOM_CODE
  add constraint CUSTOM_CODE_FK1 foreign key (CODE_TYPE_ID)
  references PN_CODE_TYPE (CODE_TYPE_ID);
alter table PN_CUSTOM_CODE
  add constraint CUSTOM_CODE_FK2 foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_CUSTOM_CODE
  add constraint RECORD_STATUS_VALID21
  check (record_status in ('A','P','D','H'));
create index CUSTOM_CODE_IDX1 on PN_CUSTOM_CODE (CODE_TYPE_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_CUSTOM_DOMAIN
prompt ===============================
prompt
create table PN_CUSTOM_DOMAIN
(
  OBJECT_ID             NUMBER(20) not null,
  TABLE_NAME            VARCHAR2(80) not null,
  COLUMN_NAME           VARCHAR2(80) not null,
  CODE                  NUMBER(8) not null,
  CODE_NAME             VARCHAR2(80) not null,
  CODE_DESC             VARCHAR2(80),
  CODE_URL              VARCHAR2(240),
  PRESENTATION_SEQUENCE NUMBER(8),
  IS_DEFAULT            NUMBER(1) not null,
  RECORD_STATUS         VARCHAR2(1) not null
)
;
alter table PN_CUSTOM_DOMAIN
  add constraint CUSTOM_DOMAIN_PK primary key (OBJECT_ID, TABLE_NAME, COLUMN_NAME, CODE);
alter table PN_CUSTOM_DOMAIN
  add constraint RECORD_STATUS_VALID23
  check (record_status in ('A','P','D','H'));
create unique index CUSTOM_DOMAIN_IDX1 on PN_CUSTOM_DOMAIN (TABLE_NAME, COLUMN_NAME, CODE, OBJECT_ID);

prompt
prompt Creating table PN_DATE_FORMAT
prompt =============================
prompt
create table PN_DATE_FORMAT
(
  DATE_FORMAT_ID NUMBER(20) not null,
  FORMAT_STRING  VARCHAR2(80) not null,
  DISPLAY        VARCHAR2(80) not null,
  EXAMPLE        VARCHAR2(80)
)
;
alter table PN_DATE_FORMAT
  add constraint PN_DATE_FORMAT_PK primary key (DATE_FORMAT_ID);

prompt
prompt Creating table PN_GROUP_TYPE
prompt ============================
prompt
create table PN_GROUP_TYPE
(
  GROUP_TYPE_ID NUMBER(20) not null,
  CLASS_NAME    VARCHAR2(1000) not null
)
;
alter table PN_GROUP_TYPE
  add constraint PN_GROUP_TYPE_PK primary key (GROUP_TYPE_ID);

prompt
prompt Creating table PN_GROUP
prompt =======================
prompt
create table PN_GROUP
(
  GROUP_ID           NUMBER(20) not null,
  GROUP_NAME         VARCHAR2(80) not null,
  GROUP_DESC         VARCHAR2(255),
  IS_PRINCIPAL       NUMBER(1) default 0 not null,
  IS_SYSTEM_GROUP    NUMBER(1) default 0 not null,
  RECORD_STATUS      VARCHAR2(1) not null,
  GROUP_TYPE_ID      NUMBER(20) not null,
  PRINCIPAL_OWNER_ID NUMBER(20)
)
;
alter table PN_GROUP
  add constraint GROUP_PK primary key (GROUP_ID);
alter table PN_GROUP
  add constraint GROUP_OBJ_FK foreign key (GROUP_ID)
  references PN_OBJECT (OBJECT_ID)
  disable;
alter table PN_GROUP
  add constraint PN_GROUP_FK2 foreign key (GROUP_TYPE_ID)
  references PN_GROUP_TYPE (GROUP_TYPE_ID);
alter table PN_GROUP
  add constraint PN_GROUP_FK3 foreign key (PRINCIPAL_OWNER_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_GROUP
  add constraint RECORD_STATUS_VALID25
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_DEFAULT_OBJECT_PERMISSION
prompt ===========================================
prompt
create table PN_DEFAULT_OBJECT_PERMISSION
(
  SPACE_ID    NUMBER(20) not null,
  OBJECT_TYPE VARCHAR2(80) not null,
  GROUP_ID    NUMBER(20) not null,
  ACTIONS     NUMBER(10) not null
)
;
alter table PN_DEFAULT_OBJECT_PERMISSION
  add constraint DEFAULT_OBJ_PERM_PK primary key (SPACE_ID, OBJECT_TYPE, GROUP_ID);
alter table PN_DEFAULT_OBJECT_PERMISSION
  add constraint DEFAULT_OBJ_PERM_FK1 foreign key (OBJECT_TYPE)
  references PN_OBJECT_TYPE (OBJECT_TYPE);
alter table PN_DEFAULT_OBJECT_PERMISSION
  add constraint DEFAULT_OBJ_PERM_FK2 foreign key (GROUP_ID)
  references PN_GROUP (GROUP_ID);
create index DEFAULT_OBJ_PERM_IDX1 on PN_DEFAULT_OBJECT_PERMISSION (GROUP_ID) TABLESPACE INDEX01;
create index DEFAULT_OBJ_PERM_IDX2 on PN_DEFAULT_OBJECT_PERMISSION (OBJECT_TYPE) TABLESPACE INDEX01;
create index DEFAULT_OBJ_PERM_IDX3 on PN_DEFAULT_OBJECT_PERMISSION (SPACE_ID, OBJECT_TYPE) TABLESPACE INDEX01;

prompt
prompt Creating table PN_DELIVERABLE
prompt =============================
prompt
create table PN_DELIVERABLE
(
  DELIVERABLE_ID             NUMBER(20) not null,
  DELIVERABLE_NAME           VARCHAR2(80) not null,
  DELIVERABLE_DESC           VARCHAR2(4000),
  STATUS_ID                  NUMBER(8) not null,
  METHODOLOGY_DELIVERABLE_ID NUMBER(20),
  IS_OPTIONAL                NUMBER(1) not null,
  RECORD_STATUS              VARCHAR2(1) not null,
  DELIVERABLE_COMMENTS_CLOB  CLOB
)
;
alter table PN_DELIVERABLE
  add constraint DELIVERABLE_PK primary key (DELIVERABLE_ID);
alter table PN_DELIVERABLE
  add constraint DELIVERABLE_OBJ_FK foreign key (DELIVERABLE_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_DELIVERABLE
  add constraint RECORD_STATUS_VALID26
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_NOTIFICATION_DELIVERY_TYPE
prompt ============================================
prompt
create table PN_NOTIFICATION_DELIVERY_TYPE
(
  DELIVERY_TYPE_ID NUMBER(20) not null,
  NAME             VARCHAR2(80) not null,
  DESCRIPTION      VARCHAR2(500),
  RECORD_STATUS    VARCHAR2(1)
)
;
alter table PN_NOTIFICATION_DELIVERY_TYPE
  add constraint NOTIFICATION_DELIVERY_PK primary key (DELIVERY_TYPE_ID);

prompt
prompt Creating table PN_DELIVERY_ADDRESS
prompt ==================================
prompt
create table PN_DELIVERY_ADDRESS
(
  ADDRESS_ID       NUMBER(20) not null,
  ADDRESS_GROUP_ID NUMBER(20) not null,
  ADDRESS          VARCHAR2(80) not null,
  DELIVERY_TYPE_ID NUMBER(20) not null
)
;
alter table PN_DELIVERY_ADDRESS
  add constraint PN_DELIVERY_ADDRESS_PK primary key (ADDRESS_ID);
alter table PN_DELIVERY_ADDRESS
  add constraint PN_DELIVERY_ADDRESS_FK1 foreign key (DELIVERY_TYPE_ID)
  references PN_NOTIFICATION_DELIVERY_TYPE (DELIVERY_TYPE_ID);

prompt
prompt Creating table PN_DIRECTORY
prompt ===========================
prompt
create table PN_DIRECTORY
(
  DIRECTORY_ID          NUMBER(20) not null,
  DIRECTORY_NAME        VARCHAR2(80),
  DIRECTORY_DESC        VARCHAR2(500),
  DISPLAY_CLASS_ID      NUMBER(20),
  DIRECTORY_TYPE_ID     NUMBER(20),
  DIRECTORY_HOST        VARCHAR2(240),
  DIRECTORY_VENDOR      VARCHAR2(80),
  BIND_USERNAME         VARCHAR2(80),
  DIRECTORY_URL         VARCHAR2(240),
  BIND_PASSWORD         VARCHAR2(80),
  SEARCH_ROOT           VARCHAR2(240),
  DIRECTORY_PORT        VARCHAR2(80),
  DIRECTORY_SECURE_PORT VARCHAR2(80),
  DIRECTORY_CIPHER_KEY  VARCHAR2(240),
  CONNECT_SECURE        NUMBER(1),
  IS_DEFAULT            NUMBER(1) default 1 not null,
  RECORD_STATUS         VARCHAR2(1) not null
)
;
alter table PN_DIRECTORY
  add constraint DIRECTORY_PK primary key (DIRECTORY_ID);
alter table PN_DIRECTORY
  add constraint RECORD_STATUS_VALID24
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_DIRECTORY_FIELD
prompt =================================
prompt
create table PN_DIRECTORY_FIELD
(
  DIRECTORY_FIELD_ID    NUMBER(20) not null,
  DIRECTORY_ID          NUMBER(20) not null,
  IS_LOCALLY_STORED     NUMBER(1) not null,
  DIRECTORY_FIELD_NAME  VARCHAR2(255),
  PN_PERSON_COLUMN_NAME VARCHAR2(80),
  RECORD_STATUS         VARCHAR2(1) not null
)
;
alter table PN_DIRECTORY_FIELD
  add constraint DIRECTORY_FIELD_PK primary key (DIRECTORY_FIELD_ID);
alter table PN_DIRECTORY_FIELD
  add constraint DIRECTORY_FIELD_FK1 foreign key (DIRECTORY_ID)
  references PN_DIRECTORY (DIRECTORY_ID);
alter table PN_DIRECTORY_FIELD
  add constraint RECORD_STATUS_VALID27
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_DIRECTORY_HAS_PERSON
prompt ======================================
prompt
create table PN_DIRECTORY_HAS_PERSON
(
  DIRECTORY_ID NUMBER(20) not null,
  PERSON_ID    NUMBER(20) not null,
  IS_DEFAULT   NUMBER(1) default 1 not null
)
;
alter table PN_DIRECTORY_HAS_PERSON
  add constraint DIRECTORY_PERSON_PK primary key (DIRECTORY_ID, PERSON_ID);
alter table PN_DIRECTORY_HAS_PERSON
  add constraint DIRECTORY_PERSON_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_DIRECTORY_HAS_PERSON
  add constraint DIRECTORY_PERSON_FK2 foreign key (DIRECTORY_ID)
  references PN_DIRECTORY (DIRECTORY_ID);
create unique index DIRECTORY_PERSON_IDX1 on PN_DIRECTORY_HAS_PERSON (PERSON_ID, DIRECTORY_ID);

prompt
prompt Creating table PN_DIRECTORY_PROVIDER_TYPE
prompt =========================================
prompt
create table PN_DIRECTORY_PROVIDER_TYPE
(
  PROVIDER_TYPE_ID            NUMBER(20) not null,
  NAME                        VARCHAR2(80) not null,
  DESCRIPTION                 VARCHAR2(500),
  SERVICE_PROVIDER_CLASS_NAME VARCHAR2(1000) not null,
  CONFIGURATOR_CLASS_NAME     VARCHAR2(1000) not null,
  CONFIGURATION_CLASS_NAME    VARCHAR2(1000) not null
)
;
alter table PN_DIRECTORY_PROVIDER_TYPE
  add constraint DIRECTORY_PROVIDER_TYPE_PK primary key (PROVIDER_TYPE_ID);

prompt
prompt Creating table PN_DISCIPLINE_LOOKUP
prompt ===================================
prompt
create table PN_DISCIPLINE_LOOKUP
(
  DISCIPLINE_CODE        NUMBER(4) not null,
  DISCIPLINE_NAME        VARCHAR2(30) not null,
  DISCIPLINE_DESCRIPTION VARCHAR2(80)
)
;
alter table PN_DISCIPLINE_LOOKUP
  add constraint DISCIPLINE_LOOKUP_PK primary key (DISCIPLINE_CODE);

prompt
prompt Creating table PN_DISCUSSION_ACTION_LOOKUP
prompt ==========================================
prompt
create table PN_DISCUSSION_ACTION_LOOKUP
(
  ACTION        VARCHAR2(80) not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_DISCUSSION_ACTION_LOOKUP
  add constraint DISCUSSION_ACTION_LOOKUP_PK primary key (ACTION);
alter table PN_DISCUSSION_ACTION_LOOKUP
  add constraint RECORD_STATUS_VALID180
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_DISCUSSION_GROUP
prompt ==================================
prompt
create table PN_DISCUSSION_GROUP
(
  DISCUSSION_GROUP_ID           NUMBER(20) not null,
  DISCUSSION_GROUP_NAME         VARCHAR2(256),
  DISCUSSION_GROUP_DESCRIPTION  VARCHAR2(80),
  RECORD_STATUS                 VARCHAR2(1) not null,
  DISCUSSION_GROUP_CHARTER_CLOB CLOB
)
;
alter table PN_DISCUSSION_GROUP
  add constraint DISCUSSION_GROUP_PK primary key (DISCUSSION_GROUP_ID);
alter table PN_DISCUSSION_GROUP
  add constraint DISCUSSION_GROUP_OBJ_FK foreign key (DISCUSSION_GROUP_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_DISCUSSION_GROUP
  add constraint RECORD_STATUS_VALID28
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_DISCUSSION_HISTORY
prompt ====================================
prompt
create table PN_DISCUSSION_HISTORY
(
  DISCUSSION_GROUP_ID         NUMBER(20) not null,
  DISCUSSION_GROUP_HISTORY_ID NUMBER(20) not null,
  ACTION_BY_ID                NUMBER(20),
  ACTION                      VARCHAR2(80),
  ACTION_COMMENT              VARCHAR2(255),
  ACTION_DATE                 DATE,
  ACTION_NAME                 VARCHAR2(80)
)
;
alter table PN_DISCUSSION_HISTORY
  add constraint DISCUSSION_HISTORY_PK primary key (DISCUSSION_GROUP_ID, DISCUSSION_GROUP_HISTORY_ID);
alter table PN_DISCUSSION_HISTORY
  add constraint DISCUSSION_HISTORY_FK1 foreign key (DISCUSSION_GROUP_ID)
  references PN_DISCUSSION_GROUP (DISCUSSION_GROUP_ID);
alter table PN_DISCUSSION_HISTORY
  add constraint DISCUSSION_HISTORY_FK2 foreign key (ACTION_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_DISCUSSION_HISTORY
  add constraint DISCUSSION_HISTORY_FK3 foreign key (ACTION)
  references PN_DISCUSSION_ACTION_LOOKUP (ACTION);
create unique index DISCUSSION_HISTORY_IDX1 on PN_DISCUSSION_HISTORY (DISCUSSION_GROUP_HISTORY_ID, DISCUSSION_GROUP_ID);

prompt
prompt Creating table PN_DOC_ACTION_LOOKUP
prompt ===================================
prompt
create table PN_DOC_ACTION_LOOKUP
(
  ACTION        VARCHAR2(80) not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_DOC_ACTION_LOOKUP
  add constraint DOC_ACTION_LOOKUP_PK primary key (ACTION);
alter table PN_DOC_ACTION_LOOKUP
  add constraint RECORD_STATUS_VALID30
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_DOC_CHECKOUT_LOCATION
prompt =======================================
prompt
create table PN_DOC_CHECKOUT_LOCATION
(
  DOC_ID            NUMBER(20) not null,
  PERSON_ID         NUMBER(20) not null,
  CLIENT_MACHINE_ID NUMBER(20) not null,
  ABSOLUTE_FILENAME VARCHAR2(240)
)
;
alter table PN_DOC_CHECKOUT_LOCATION
  add constraint DOC_CHECKOUT_LOC_PK primary key (DOC_ID, PERSON_ID, CLIENT_MACHINE_ID);
alter table PN_DOC_CHECKOUT_LOCATION
  add constraint DOC_CHECKOUT_LOCATION_FK1 foreign key (DOC_ID)
  references PN_DOCUMENT (DOC_ID);
alter table PN_DOC_CHECKOUT_LOCATION
  add constraint DOC_CHECKOUT_LOCATION_FK2 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);

prompt
prompt Creating table PN_DOC_CONFIGURATION
prompt ===================================
prompt
create table PN_DOC_CONFIGURATION
(
  DOC_CONFIGURATION_ID   NUMBER(20) not null,
  CREATED_BY             NUMBER(20) not null,
  DATE_FROZEN            DATE not null,
  DOC_CONFIGURATION_NAME VARCHAR2(80) not null,
  CRC                    DATE not null,
  RECORD_STATUS          VARCHAR2(1) not null
)
;
alter table PN_DOC_CONFIGURATION
  add constraint DOC_CONFIG_PK primary key (DOC_CONFIGURATION_ID);
alter table PN_DOC_CONFIGURATION
  add constraint DOC_CONFIGURATION_FK1 foreign key (CREATED_BY)
  references PN_PERSON (PERSON_ID);
alter table PN_DOC_CONFIGURATION
  add constraint RECORD_STATUS_VALID33
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_DOC_VERSION
prompt =============================
prompt
create table PN_DOC_VERSION
(
  DOC_VERSION_ID    NUMBER(20) not null,
  DOC_ID            NUMBER(20) not null,
  DOC_VERSION_NAME  VARCHAR2(256),
  SOURCE_FILE_NAME  VARCHAR2(240),
  DOC_DESCRIPTION   VARCHAR2(4000),
  DATE_MODIFIED     DATE,
  MODIFIED_BY_ID    NUMBER(20),
  IS_CHECKED_OUT    NUMBER(1) default 0 not null,
  CHECKED_OUT_BY_ID NUMBER(20),
  DATE_CHECKED_OUT  DATE,
  DOC_COMMENT       VARCHAR2(500),
  DOC_VERSION_NUM   NUMBER(20) not null,
  DOC_VERSION_LABEL VARCHAR2(240),
  CHECKOUT_DUE      DATE,
  DOC_AUTHOR_ID     NUMBER(20),
  SHORT_FILE_NAME   VARCHAR2(240),
  CRC               DATE not null,
  RECORD_STATUS     VARCHAR2(1) not null
)
;
alter table PN_DOC_VERSION
  add constraint DOC_VERSION_PK primary key (DOC_VERSION_ID);
alter table PN_DOC_VERSION
  add constraint DOC_VERSION_FK1 foreign key (MODIFIED_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_DOC_VERSION
  add constraint DOC_VERSION_FK2 foreign key (CHECKED_OUT_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_DOC_VERSION
  add constraint DOC_VERSION_FK3 foreign key (DOC_AUTHOR_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_DOC_VERSION
  add constraint DOC_VERSION_FK4 foreign key (DOC_ID)
  references PN_DOCUMENT (DOC_ID);
alter table PN_DOC_VERSION
  add constraint DOC_VERSION_OBJ_FK foreign key (DOC_VERSION_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_DOC_VERSION
  add constraint RECORD_STATUS_VALID34
  check (record_status in ('A','P','D','H'));
create index DOC_VERSION_IDX1 on PN_DOC_VERSION (DOC_ID) TABLESPACE INDEX01;
create index DOC_VERSION_IDX2 on PN_DOC_VERSION (MODIFIED_BY_ID) TABLESPACE INDEX01;
create index DOC_VERSION_IDX3 on PN_DOC_VERSION (CHECKED_OUT_BY_ID) TABLESPACE INDEX01;
create index DOC_VERSION_IDX4 on PN_DOC_VERSION (DOC_AUTHOR_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_DOC_CONFIGURATION_HAS_DOC
prompt ===========================================
prompt
create table PN_DOC_CONFIGURATION_HAS_DOC
(
  DOC_CONFIGURATION_ID NUMBER(20) not null,
  DOC_VERSION_ID       NUMBER(20) not null,
  DOC_ID               NUMBER(20) not null
)
;
alter table PN_DOC_CONFIGURATION_HAS_DOC
  add constraint DOC_CONFIG_HAS_DOC_PK primary key (DOC_CONFIGURATION_ID, DOC_VERSION_ID);
alter table PN_DOC_CONFIGURATION_HAS_DOC
  add constraint DOC_CONFIG_HAS_DOC_FK1 foreign key (DOC_VERSION_ID)
  references PN_DOC_VERSION (DOC_VERSION_ID) on delete cascade;
alter table PN_DOC_CONFIGURATION_HAS_DOC
  add constraint DOC_CONFIG_HAS_DOC_FK2 foreign key (DOC_CONFIGURATION_ID)
  references PN_DOC_CONFIGURATION (DOC_CONFIGURATION_ID) on delete cascade;
create index DOC_CONFIG_DOC_IDX1 on PN_DOC_CONFIGURATION_HAS_DOC (DOC_VERSION_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_DOC_CONTAINER
prompt ===============================
prompt
create table PN_DOC_CONTAINER
(
  DOC_CONTAINER_ID      NUMBER(20) not null,
  CONTAINER_NAME        VARCHAR2(80),
  CONTAINER_DESCRIPTION VARCHAR2(500),
  DATE_MODIFIED         DATE,
  MODIFIED_BY_ID        NUMBER(20),
  IS_HIDDEN             NUMBER(1) default 0 not null,
  CRC                   DATE not null,
  RECORD_STATUS         VARCHAR2(1) not null
)
;
alter table PN_DOC_CONTAINER
  add constraint DOC_CONTAINER_PK primary key (DOC_CONTAINER_ID);
alter table PN_DOC_CONTAINER
  add constraint DOC_CONTAINER_FK1 foreign key (MODIFIED_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_DOC_CONTAINER
  add constraint DOC_CONTAINER_OBJ_FK foreign key (DOC_CONTAINER_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_DOC_CONTAINER
  add constraint RECORD_STATUS_VALID36
  check (record_status in ('A','P','D','H'));
-- create bitmap index DOC_CONTAINER_BIDX1 on PN_DOC_CONTAINER (IS_HIDDEN);

prompt
prompt Creating table PN_DOC_CONTAINER_HAS_OBJECT
prompt ==========================================
prompt
create table PN_DOC_CONTAINER_HAS_OBJECT
(
  DOC_CONTAINER_ID NUMBER(20) not null,
  OBJECT_ID        NUMBER(20) not null
)
;
alter table PN_DOC_CONTAINER_HAS_OBJECT
  add constraint DOC_CONTAINER_OBJ_PK primary key (DOC_CONTAINER_ID, OBJECT_ID);
alter table PN_DOC_CONTAINER_HAS_OBJECT
  add constraint DOC_CONTAINER_OBJECT_FK1 foreign key (DOC_CONTAINER_ID)
  references PN_DOC_CONTAINER (DOC_CONTAINER_ID) on delete cascade;
alter table PN_DOC_CONTAINER_HAS_OBJECT
  add constraint DOC_CONTAINER_OBJECT_FK2 foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
create index DOC_CONTAIN_OBJ_IDX1 on PN_DOC_CONTAINER_HAS_OBJECT (OBJECT_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_DOC_FORMAT
prompt ============================
prompt
create table PN_DOC_FORMAT
(
  DOC_FORMAT_ID NUMBER(20) not null,
  FORMAT_NAME   VARCHAR2(240) not null,
  DESCRIPTION   VARCHAR2(500),
  APPLICATION   VARCHAR2(240),
  MIME_TYPE     VARCHAR2(240) not null,
  FILE_EXT      VARCHAR2(80),
  APP_ICON_URL  VARCHAR2(240),
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_DOC_FORMAT
  add constraint DOC_FORMAT_PK primary key (DOC_FORMAT_ID);
alter table PN_DOC_FORMAT
  add constraint RECORD_STATUS_VALID39
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_DOC_REPOSITORY_BASE
prompt =====================================
prompt
create table PN_DOC_REPOSITORY_BASE
(
  REPOSITORY_ID   NUMBER(4) not null,
  REPOSITORY_PATH VARCHAR2(240) not null,
  IS_ACTIVE       NUMBER(1) not null
)
;
alter table PN_DOC_REPOSITORY_BASE
  add constraint DOC_REPOSITORY_BASE_PK primary key (REPOSITORY_ID);

prompt
prompt Creating table PN_DOC_CONTENT_ELEMENT
prompt =====================================
prompt
create table PN_DOC_CONTENT_ELEMENT
(
  DOC_CONTENT_ID   NUMBER(20) not null,
  DOC_FORMAT_ID    NUMBER(20),
  REPOSITORY_ID    NUMBER(4) not null,
  DISPLAY_SEQUENCE NUMBER(8),
  FILE_SIZE        NUMBER(20),
  FILE_HANDLE      VARCHAR2(240),
  RECORD_STATUS    VARCHAR2(1) not null
)
;
alter table PN_DOC_CONTENT_ELEMENT
  add constraint DOC_CONT_ELEMENT_PK primary key (DOC_CONTENT_ID);
alter table PN_DOC_CONTENT_ELEMENT
  add constraint DOC_CONTENT_ELEMENT_FK1 foreign key (DOC_FORMAT_ID)
  references PN_DOC_FORMAT (DOC_FORMAT_ID) on delete cascade;
alter table PN_DOC_CONTENT_ELEMENT
  add constraint DOC_CONTENT_ELEMENT_FK2 foreign key (REPOSITORY_ID)
  references PN_DOC_REPOSITORY_BASE (REPOSITORY_ID);
alter table PN_DOC_CONTENT_ELEMENT
  add constraint RECORD_STATUS_VALID37
  check (record_status in ('A','P','D','H'));
create index DOC_CONTENT_ELEMENT_IDX1 on PN_DOC_CONTENT_ELEMENT (DOC_FORMAT_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_DOC_CONTENT_RENDITION
prompt =======================================
prompt
create table PN_DOC_CONTENT_RENDITION
(
  DOC_RENDITION_ID NUMBER(20) not null,
  DOC_CONTENT_ID   NUMBER(20) not null,
  RENDERED_AS      VARCHAR2(500),
  RECORD_STATUS    VARCHAR2(1) not null
)
;
alter table PN_DOC_CONTENT_RENDITION
  add constraint DOC_CONTENT_REND_PK primary key (DOC_RENDITION_ID, DOC_CONTENT_ID);
alter table PN_DOC_CONTENT_RENDITION
  add constraint DOC_CONTENT_REND_FK1 foreign key (DOC_CONTENT_ID)
  references PN_DOC_CONTENT_ELEMENT (DOC_CONTENT_ID) on delete cascade;
alter table PN_DOC_CONTENT_RENDITION
  add constraint RECORD_STATUS_VALID99
  check (record_status in ('A','P','D','H'));
create unique index DOC_CONTENT_REND_IDX1 on PN_DOC_CONTENT_RENDITION (DOC_CONTENT_ID, DOC_RENDITION_ID);

prompt
prompt Creating table PN_DOC_HANDLER
prompt =============================
prompt
create table PN_DOC_HANDLER
(
  DOC_FORMAT_ID  NUMBER(20) not null,
  ACTION         VARCHAR2(200),
  ACTION_HANDLER VARCHAR2(200),
  IS_DEFAULT     NUMBER(1)
)
;
alter table PN_DOC_HANDLER
  add constraint FK_DOC_FORMAT_ID foreign key (DOC_FORMAT_ID)
  references PN_DOC_FORMAT (DOC_FORMAT_ID);
create index PK_DOC_HANDLER on PN_DOC_HANDLER (DOC_FORMAT_ID, ACTION) TABLESPACE INDEX01;

prompt
prompt Creating table PN_DOC_HISTORY
prompt =============================
prompt
create table PN_DOC_HISTORY
(
  DOC_ID         NUMBER(20) not null,
  DOC_HISTORY_ID NUMBER(20) not null,
  ACTION_BY_ID   NUMBER(20),
  ACTION         VARCHAR2(80),
  ACTION_COMMENT VARCHAR2(255),
  ACTION_DATE    DATE,
  ACTION_NAME    VARCHAR2(80)
)
;
alter table PN_DOC_HISTORY
  add constraint DOC_HISTORY_PK primary key (DOC_ID, DOC_HISTORY_ID);
alter table PN_DOC_HISTORY
  add constraint DOC_HISTORY_FK1 foreign key (DOC_ID)
  references PN_DOCUMENT (DOC_ID);
alter table PN_DOC_HISTORY
  add constraint DOC_HISTORY_FK2 foreign key (ACTION_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_DOC_HISTORY
  add constraint DOC_HISTORY_FK3 foreign key (ACTION)
  references PN_DOC_ACTION_LOOKUP (ACTION);
create unique index DOC_HISTORY_IDX1 on PN_DOC_HISTORY (DOC_HISTORY_ID, DOC_ID);

prompt
prompt Creating table PN_DOC_PROVIDER_TYPE
prompt ===================================
prompt
create table PN_DOC_PROVIDER_TYPE
(
  DOC_PROVIDER_TYPE_ID   NUMBER(20) not null,
  DOC_PROVIDER_TYPE_NAME VARCHAR2(80),
  DOC_PROVIDER_TYPE_DESC VARCHAR2(500)
)
;
alter table PN_DOC_PROVIDER_TYPE
  add constraint DOC_PROVIDER_TYPE_PK primary key (DOC_PROVIDER_TYPE_ID);

prompt
prompt Creating table PN_DOC_PROVIDER
prompt ==============================
prompt
create table PN_DOC_PROVIDER
(
  DOC_PROVIDER_ID          NUMBER(20) not null,
  DOC_PROVIDER_TYPE_ID     NUMBER(20) not null,
  DOC_PROVIDER_NAME        VARCHAR2(240),
  DOC_PROVIDER_DESCRIPTION VARCHAR2(500),
  IS_DEFAULT               NUMBER(1) default 0 not null,
  CRC                      DATE not null,
  RECORD_STATUS            VARCHAR2(1) not null
)
;
alter table PN_DOC_PROVIDER
  add constraint DOC_PROVIDER_PK primary key (DOC_PROVIDER_ID);
alter table PN_DOC_PROVIDER
  add constraint DOC_PROVIDER_FK1 foreign key (DOC_PROVIDER_TYPE_ID)
  references PN_DOC_PROVIDER_TYPE (DOC_PROVIDER_TYPE_ID) on delete cascade;
alter table PN_DOC_PROVIDER
  add constraint RECORD_STATUS_VALID40
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_DOC_SPACE
prompt ===========================
prompt
create table PN_DOC_SPACE
(
  DOC_SPACE_ID   NUMBER(20) not null,
  DOC_SPACE_NAME VARCHAR2(80),
  CRC            DATE not null,
  RECORD_STATUS  VARCHAR2(1) not null
)
;
alter table PN_DOC_SPACE
  add constraint DOC_SPACE_PK primary key (DOC_SPACE_ID);
alter table PN_DOC_SPACE
  add constraint DOC_SPACE_OBJ_FK foreign key (DOC_SPACE_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_DOC_SPACE
  add constraint RECORD_STATUS_VALID41
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_DOC_PROVIDER_HAS_DOC_SPACE
prompt ============================================
prompt
create table PN_DOC_PROVIDER_HAS_DOC_SPACE
(
  DOC_PROVIDER_ID NUMBER(20) not null,
  DOC_SPACE_ID    NUMBER(20) not null
)
;
alter table PN_DOC_PROVIDER_HAS_DOC_SPACE
  add constraint DOC_PROV_DOC_SPACE_PK primary key (DOC_PROVIDER_ID, DOC_SPACE_ID);
alter table PN_DOC_PROVIDER_HAS_DOC_SPACE
  add constraint DOC_PROV_DOC_SPACE_FK1 foreign key (DOC_PROVIDER_ID)
  references PN_DOC_PROVIDER (DOC_PROVIDER_ID) on delete cascade;
alter table PN_DOC_PROVIDER_HAS_DOC_SPACE
  add constraint DOC_PROV_DOC_SPACE_FK2 foreign key (DOC_SPACE_ID)
  references PN_DOC_SPACE (DOC_SPACE_ID) on delete cascade;
create unique index DOC_PROV_DOC_SPACE_IDX1 on PN_DOC_PROVIDER_HAS_DOC_SPACE (DOC_SPACE_ID, DOC_PROVIDER_ID);

prompt
prompt Creating table PN_DOC_SPACE_HAS_CONTAINER
prompt =========================================
prompt
create table PN_DOC_SPACE_HAS_CONTAINER
(
  DOC_SPACE_ID     NUMBER(20) not null,
  DOC_CONTAINER_ID NUMBER(20) not null,
  IS_ROOT          NUMBER(1) default 0 not null
)
;
alter table PN_DOC_SPACE_HAS_CONTAINER
  add constraint DOC_SPACE_CONTAINER_PK primary key (DOC_SPACE_ID, DOC_CONTAINER_ID);
alter table PN_DOC_SPACE_HAS_CONTAINER
  add constraint DOC_SPACE_CONTAINER_FK1 foreign key (DOC_SPACE_ID)
  references PN_DOC_SPACE (DOC_SPACE_ID) on delete cascade;
alter table PN_DOC_SPACE_HAS_CONTAINER
  add constraint DOC_SPACE_CONTAINER_FK2 foreign key (DOC_CONTAINER_ID)
  references PN_DOC_CONTAINER (DOC_CONTAINER_ID) on delete cascade;
create index DOC_SPACE_CONTAINER_IDX1 on PN_DOC_SPACE_HAS_CONTAINER (DOC_CONTAINER_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_DOC_VERSION_HAS_CONTENT
prompt =========================================
prompt
create table PN_DOC_VERSION_HAS_CONTENT
(
  DOC_VERSION_ID NUMBER(20) not null,
  DOC_CONTENT_ID NUMBER(20) not null,
  DOC_ID         NUMBER(20) not null
)
;
alter table PN_DOC_VERSION_HAS_CONTENT
  add constraint DOC_VERSION_CONTENT_PK primary key (DOC_VERSION_ID, DOC_CONTENT_ID);
alter table PN_DOC_VERSION_HAS_CONTENT
  add constraint DOC_VERSION_CONTENT_FK2 foreign key (DOC_CONTENT_ID)
  references PN_DOC_CONTENT_ELEMENT (DOC_CONTENT_ID) on delete cascade;
alter table PN_DOC_VERSION_HAS_CONTENT
  add constraint DOC_VERSION_HAS_CONTENT_FK1 foreign key (DOC_VERSION_ID)
  references PN_DOC_VERSION (DOC_VERSION_ID) on delete cascade;
create index DOC_VERSION_CONTENT_IDX1 on PN_DOC_VERSION_HAS_CONTENT (DOC_CONTENT_ID) TABLESPACE INDEX01;
create index DOC_VERSION_CONTENT_IDX2 on PN_DOC_VERSION_HAS_CONTENT (DOC_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_USER_DOMAIN
prompt =============================
prompt
create table PN_USER_DOMAIN
(
  DOMAIN_ID                      NUMBER(20) not null,
  NAME                           VARCHAR2(80) not null,
  DESCRIPTION                    VARCHAR2(240),
  RECORD_STATUS                  VARCHAR2(1) not null,
  DIRECTORY_PROVIDER_TYPE_ID     NUMBER(20) not null,
  IS_VERIFICATION_REQUIRED       NUMBER(1),
  REGISTRATION_INSTRUCTIONS_CLOB CLOB,
  SUPPORTS_CREDIT_CARD_PURCHASES NUMBER(1)
)
;
alter table PN_USER_DOMAIN
  add constraint PN_USER_DOMAIN_PK primary key (DOMAIN_ID);
alter table PN_USER_DOMAIN
  add constraint PN_USER_DOMAIN_FK1 foreign key (DIRECTORY_PROVIDER_TYPE_ID)
  references PN_DIRECTORY_PROVIDER_TYPE (PROVIDER_TYPE_ID);

prompt
prompt Creating table PN_DOMAIN_MIGRATION
prompt ==================================
prompt
create table PN_DOMAIN_MIGRATION
(
  DOMAIN_MIGRATION_ID NUMBER(20) not null,
  FROM_DOMAIN         NUMBER(20) not null,
  TO_DOMAIN           NUMBER(20) not null,
  ADMIN_MESSAGE_CLOB  CLOB
)
;
alter table PN_DOMAIN_MIGRATION
  add constraint PN_DOMAIN_MIGRATION_PK primary key (DOMAIN_MIGRATION_ID);
alter table PN_DOMAIN_MIGRATION
  add constraint PN_DOMAIN_MIGRATION_FK1 foreign key (FROM_DOMAIN)
  references PN_USER_DOMAIN (DOMAIN_ID);
alter table PN_DOMAIN_MIGRATION
  add constraint PN_DOMAIN_MIGRATION_FK2 foreign key (TO_DOMAIN)
  references PN_USER_DOMAIN (DOMAIN_ID);

prompt
prompt Creating table PN_ELEMENT_DISPLAY_CLASS
prompt =======================================
prompt
create table PN_ELEMENT_DISPLAY_CLASS
(
  ELEMENT_ID NUMBER(20) not null,
  CLASS_ID   NUMBER(20) not null
)
;
alter table PN_ELEMENT_DISPLAY_CLASS
  add constraint ELEMENT_DISPLAY_CLASS_PK primary key (ELEMENT_ID, CLASS_ID);
alter table PN_ELEMENT_DISPLAY_CLASS
  add constraint ELEMENT_DISPLAY_CLASS_FK1 foreign key (ELEMENT_ID)
  references PN_ELEMENT (ELEMENT_ID);
alter table PN_ELEMENT_DISPLAY_CLASS
  add constraint ELEMENT_DISPLAY_CLASS_FK2 foreign key (CLASS_ID)
  references PN_CLASS (CLASS_ID);
create index ELEMENT_DISPLAY_CLASS_IDX1 on PN_ELEMENT_DISPLAY_CLASS (CLASS_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_ELEMENT_PROPERTY
prompt ==================================
prompt
create table PN_ELEMENT_PROPERTY
(
  ELEMENT_ID        NUMBER(20) not null,
  PROPERTY_ID       NUMBER(20) not null,
  CLIENT_TYPE_ID    NUMBER(20) not null,
  PROPERTY          VARCHAR2(80) not null,
  PROPERTY_TYPE     VARCHAR2(80),
  DEFAULT_VALUE     VARCHAR2(80),
  MAX_VALUE         VARCHAR2(80),
  PROPERTY_LABEL    VARCHAR2(80),
  MIN_VALUE         VARCHAR2(80),
  IS_USER_CHANGABLE NUMBER(1) default 0 not null
)
;
alter table PN_ELEMENT_PROPERTY
  add constraint ELEMENT_PROPERTY_PK primary key (ELEMENT_ID, PROPERTY_ID, CLIENT_TYPE_ID);
alter table PN_ELEMENT_PROPERTY
  add constraint ELEMENT_PROPERTY_FK1 foreign key (ELEMENT_ID)
  references PN_ELEMENT (ELEMENT_ID);
alter table PN_ELEMENT_PROPERTY
  add constraint ELEMENT_PROPERTY_FK2 foreign key (CLIENT_TYPE_ID)
  references PN_CLIENT_TYPE (CLIENT_TYPE_ID);
create index ELEMENT_PROPERTY_IDX1 on PN_ELEMENT_PROPERTY (CLIENT_TYPE_ID, ELEMENT_ID, PROPERTY_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_WORKFLOW_STRICTNESS
prompt =====================================
prompt
create table PN_WORKFLOW_STRICTNESS
(
  STRICTNESS_ID          NUMBER(20) not null,
  STRICTNESS_NAME        VARCHAR2(80),
  STRICTNESS_DESCRIPTION VARCHAR2(500),
  CREATED_BY_ID          NUMBER(20) not null,
  CREATED_DATETIME       DATE not null,
  MODIFIED_BY_ID         NUMBER(20),
  MODIFIED_DATETIME      DATE,
  CRC                    DATE not null,
  RECORD_STATUS          VARCHAR2(1) not null
)
;
alter table PN_WORKFLOW_STRICTNESS
  add constraint WORKFLOW_STRICTNESS_PK primary key (STRICTNESS_ID);

prompt
prompt Creating table PN_WORKFLOW
prompt ==========================
prompt
create table PN_WORKFLOW
(
  WORKFLOW_ID          NUMBER(20) not null,
  WORKFLOW_NAME        VARCHAR2(80),
  STRICTNESS_ID        NUMBER(20) not null,
  WORKFLOW_DESCRIPTION VARCHAR2(500),
  CREATED_BY_ID        NUMBER(20) not null,
  NOTES                VARCHAR2(4000),
  CREATED_DATETIME     DATE not null,
  MODIFIED_BY_ID       NUMBER(20),
  OWNER_ID             NUMBER(20) not null,
  MODIFIED_DATETIME    DATE,
  IS_PUBLISHED         NUMBER(1) not null,
  IS_GENERIC           NUMBER(1) not null,
  CRC                  DATE not null,
  RECORD_STATUS        VARCHAR2(1) not null
)
;
alter table PN_WORKFLOW
  add constraint WORKFLOW_PK primary key (WORKFLOW_ID);
alter table PN_WORKFLOW
  add constraint WORKFLOW_TO_STRICTNESS_FK foreign key (STRICTNESS_ID)
  references PN_WORKFLOW_STRICTNESS (STRICTNESS_ID);

prompt
prompt Creating table PN_WORKFLOW_ENVELOPE
prompt ===================================
prompt
create table PN_WORKFLOW_ENVELOPE
(
  STRICTNESS_ID        NUMBER(20) not null,
  ENVELOPE_ID          NUMBER(20) not null,
  WORKFLOW_ID          NUMBER(20) not null,
  CURRENT_VERSION_ID   NUMBER(20) not null,
  ENVELOPE_NAME        VARCHAR2(80),
  ENVELOPE_DESCRIPTION VARCHAR2(500),
  CREATED_BY_ID        NUMBER(20) not null,
  CREATED_DATETIME     DATE not null,
  MODIFIED_BY_ID       NUMBER(20),
  MODIFIED_DATETIME    DATE,
  CRC                  DATE not null,
  RECORD_STATUS        VARCHAR2(1) not null
)
;
alter table PN_WORKFLOW_ENVELOPE
  add constraint WORKFLOW_ENVELOPE_PK primary key (ENVELOPE_ID);
alter table PN_WORKFLOW_ENVELOPE
  add constraint ENVELOPE_TO_STRICTNESS_FK foreign key (STRICTNESS_ID)
  references PN_WORKFLOW_STRICTNESS (STRICTNESS_ID);
alter table PN_WORKFLOW_ENVELOPE
  add constraint ENVELOPE_TO_WORKFLOW_FK foreign key (WORKFLOW_ID)
  references PN_WORKFLOW (WORKFLOW_ID);
create index WORKFLOW_ENVELOPE_IDX1 on PN_WORKFLOW_ENVELOPE (WORKFLOW_ID) TABLESPACE INDEX01;
create index WORKFLOW_ENVELOPE_IDX2 on PN_WORKFLOW_ENVELOPE (STRICTNESS_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_ENVELOPE_HAS_OBJECT
prompt =====================================
prompt
create table PN_ENVELOPE_HAS_OBJECT
(
  ENVELOPE_ID   NUMBER(20) not null,
  OBJECT_ID     NUMBER(20) not null,
  CRC           DATE not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_ENVELOPE_HAS_OBJECT
  add constraint ENVELOPE_HAS_OBJECT_PK primary key (OBJECT_ID, ENVELOPE_ID);
alter table PN_ENVELOPE_HAS_OBJECT
  add constraint ENVELOPE_OBJECT_TO_ENVELOPE_FK foreign key (ENVELOPE_ID)
  references PN_WORKFLOW_ENVELOPE (ENVELOPE_ID);
alter table PN_ENVELOPE_HAS_OBJECT
  add constraint ENVELOPE_OBJECT_TO_OBJECT_FK foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);

prompt
prompt Creating table PN_ENVELOPE_HISTORY_ACTION
prompt =========================================
prompt
create table PN_ENVELOPE_HISTORY_ACTION
(
  HISTORY_ACTION_ID  NUMBER(20) not null,
  ACTION_NAME        VARCHAR2(80),
  ACTION_DESCRIPTION VARCHAR2(500),
  CREATED_BY_ID      NUMBER(20) not null,
  CREATED_DATETIME   DATE not null,
  MODIFIED_BY_ID     NUMBER(20),
  MODIFIED_DATETIME  DATE,
  CRC                DATE not null,
  RECORD_STATUS      VARCHAR2(1) not null
)
;
alter table PN_ENVELOPE_HISTORY_ACTION
  add constraint ENVELOP_HISTORY_ACTION_PK primary key (HISTORY_ACTION_ID);

prompt
prompt Creating table PN_ENVELOPE_HISTORY_CLOB
prompt =======================================
prompt
create table PN_ENVELOPE_HISTORY_CLOB
(
  OBJECT_ID  NUMBER(20) not null,
  CLOB_FIELD CLOB
)
;
alter table PN_ENVELOPE_HISTORY_CLOB
  add constraint ENVELOPE_HISTORY_CLOB_PK primary key (OBJECT_ID);

prompt
prompt Creating table PN_ENVELOPE_HISTORY
prompt ==================================
prompt
create table PN_ENVELOPE_HISTORY
(
  HISTORY_ID         NUMBER(20) not null,
  ENVELOPE_ID        NUMBER(20) not null,
  ACTION_BY_ID       NUMBER(20) not null,
  HISTORY_ACTION_ID  NUMBER(20) not null,
  HISTORY_MESSAGE_ID NUMBER(20),
  ACTION_DATETIME    DATE not null,
  CRC                DATE not null,
  RECORD_STATUS      VARCHAR2(1) not null
)
;
alter table PN_ENVELOPE_HISTORY
  add constraint ENVELOPE_HISTORY_PK primary key (HISTORY_ID, ENVELOPE_ID);
alter table PN_ENVELOPE_HISTORY
  add constraint ENVELOPE_HISTORY_TO_ACTION_FK foreign key (HISTORY_ACTION_ID)
  references PN_ENVELOPE_HISTORY_ACTION (HISTORY_ACTION_ID);
alter table PN_ENVELOPE_HISTORY
  add constraint ENVELOPE_HISTORY_TO_CLOB_FK foreign key (HISTORY_MESSAGE_ID)
  references PN_ENVELOPE_HISTORY_CLOB (OBJECT_ID);
alter table PN_ENVELOPE_HISTORY
  add constraint ENVELOPE_HISTORY_TO_WF_ENV_FK foreign key (ENVELOPE_ID)
  references PN_WORKFLOW_ENVELOPE (ENVELOPE_ID);
create index ENVELOPE_HISTORY_IDX1 on PN_ENVELOPE_HISTORY (HISTORY_MESSAGE_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_ENVELOPE_OBJECT_CLOB
prompt ======================================
prompt
create table PN_ENVELOPE_OBJECT_CLOB
(
  OBJECT_ID  NUMBER(20) not null,
  CLOB_FIELD CLOB
)
;
alter table PN_ENVELOPE_OBJECT_CLOB
  add constraint ENVELOPE_OBJECT_CLOB_PK primary key (OBJECT_ID);

prompt
prompt Creating table PN_WORKFLOW_STATUS
prompt =================================
prompt
create table PN_WORKFLOW_STATUS
(
  STATUS_ID          NUMBER(20) not null,
  STATUS_NAME        VARCHAR2(80),
  STATUS_DESCRIPTION VARCHAR2(500),
  IS_INACTIVE        NUMBER(1) not null,
  CREATED_BY_ID      NUMBER(20) not null,
  CREATED_DATETIME   DATE not null,
  MODIFIED_BY_ID     NUMBER(20),
  MODIFIED_DATETIME  DATE,
  CRC                DATE not null,
  RECORD_STATUS      VARCHAR2(1) not null
)
;
alter table PN_WORKFLOW_STATUS
  add constraint WORKFLOW_STATUS_PK primary key (STATUS_ID);

prompt
prompt Creating table PN_SUBSCRIPTION
prompt ==============================
prompt
create table PN_SUBSCRIPTION
(
  SUBSCRIPTION_ID      NUMBER(20) not null,
  NAME                 VARCHAR2(80) not null,
  DESCRIPTION          VARCHAR2(500),
  SUBSCRIPTION_TYPE_ID NUMBER(20),
  CREATED_DATE         DATE,
  CREATED_BY_ID        NUMBER(20),
  MODIFIED_DATE        DATE,
  MODIFIED_BY          NUMBER(20),
  RECORD_STATUS        VARCHAR2(1) not null,
  CRC                  DATE,
  DELIVERY_INTERVAL    NUMBER(3),
  SUBSCRIBER_BATCH_ID  NUMBER(20) not null,
  CUSTOM_MESSAGE_CLOB  CLOB
)
;
alter table PN_SUBSCRIPTION
  add constraint SUBSCRIPTION_PK primary key (SUBSCRIPTION_ID);

prompt
prompt Creating table PN_WORKFLOW_STEP
prompt ===============================
prompt
create table PN_WORKFLOW_STEP
(
  STEP_ID           NUMBER(20) not null,
  STEP_NAME         VARCHAR2(80),
  WORKFLOW_ID       NUMBER(20) not null,
  STEP_DESCRIPTION  VARCHAR2(500),
  IS_FINAL_STEP     NUMBER(1) not null,
  IS_INITIAL_STEP   NUMBER(1) not null,
  ENTRY_STATUS_ID   NUMBER(20),
  SUBSCRIPTION_ID   NUMBER(20),
  CREATED_BY_ID     NUMBER(20) not null,
  CREATED_DATETIME  DATE not null,
  MODIFIED_BY_ID    NUMBER(20),
  MODIFIED_DATETIME DATE,
  CRC               DATE not null,
  RECORD_STATUS     VARCHAR2(1) not null,
  NOTES_CLOB        CLOB,
  STEP_SEQUENCE     NUMBER(5)
)
;
comment on column PN_WORKFLOW_STEP.SUBSCRIPTION_ID
  is 'Present if one or more groups is notified against this step.';
alter table PN_WORKFLOW_STEP
  add constraint WORKFLOW_STEP_PK primary key (STEP_ID, WORKFLOW_ID);
alter table PN_WORKFLOW_STEP
  add constraint WORKFLOW_STEP_TO_SUBS_FK foreign key (SUBSCRIPTION_ID)
  references PN_SUBSCRIPTION (SUBSCRIPTION_ID);
alter table PN_WORKFLOW_STEP
  add constraint WORKFLOW_STEP_TO_WORKFLOW_FK foreign key (WORKFLOW_ID)
  references PN_WORKFLOW (WORKFLOW_ID);
create index WORKFLOW_STEP_IDX2 on PN_WORKFLOW_STEP (WORKFLOW_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_WORKFLOW_TRANSITION
prompt =====================================
prompt
create table PN_WORKFLOW_TRANSITION
(
  WORKFLOW_ID            NUMBER(20) not null,
  TRANSITION_ID          NUMBER(20) not null,
  TRANSITION_VERB        VARCHAR2(80),
  TRANSITION_DESCRIPTION VARCHAR2(500),
  BEGIN_STEP_ID          NUMBER(20) not null,
  END_STEP_ID            NUMBER(20) not null,
  CREATED_BY_ID          NUMBER(20) not null,
  CREATED_DATETIME       DATE not null,
  MODIFIED_BY_ID         NUMBER(20),
  MODIFIED_DATETIME      DATE,
  CRC                    DATE not null,
  RECORD_STATUS          VARCHAR2(1) not null
)
;
alter table PN_WORKFLOW_TRANSITION
  add constraint WORKFLOW_TRANSITION_PK primary key (TRANSITION_ID, WORKFLOW_ID);
alter table PN_WORKFLOW_TRANSITION
  add constraint WORKFLOW_TRANSITION_TO_WF_FK foreign key (WORKFLOW_ID)
  references PN_WORKFLOW (WORKFLOW_ID);
alter table PN_WORKFLOW_TRANSITION
  add constraint WORKFLOW_TRAN_TO_BEGINSTEP_FK foreign key (BEGIN_STEP_ID, WORKFLOW_ID)
  references PN_WORKFLOW_STEP (STEP_ID, WORKFLOW_ID);
alter table PN_WORKFLOW_TRANSITION
  add constraint WORKFLOW_TRAN_TO_ENDSTEP_FK foreign key (END_STEP_ID, WORKFLOW_ID)
  references PN_WORKFLOW_STEP (STEP_ID, WORKFLOW_ID);
create unique index WORKFLOW_TRANSITION_IDX1 on PN_WORKFLOW_TRANSITION (TRANSITION_ID);
create index WORKFLOW_TRANSITION_IDX2 on PN_WORKFLOW_TRANSITION (BEGIN_STEP_ID) TABLESPACE INDEX01;
create index WORKFLOW_TRANSITION_IDX3 on PN_WORKFLOW_TRANSITION (END_STEP_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_ENVELOPE_VERSION
prompt ==================================
prompt
create table PN_ENVELOPE_VERSION
(
  VERSION_ID       NUMBER(20) not null,
  ENVELOPE_ID      NUMBER(20) not null,
  STATUS_ID        NUMBER(20) not null,
  STEP_ID          NUMBER(20) not null,
  WORKFLOW_ID      NUMBER(20) not null,
  TRANSITION_ID    NUMBER(20),
  PRIORITY_ID      NUMBER(20) not null,
  CREATED_BY_ID    NUMBER(20) not null,
  CREATED_DATETIME DATE not null,
  CRC              DATE not null,
  RECORD_STATUS    VARCHAR2(1) not null,
  COMMENTS_CLOB    CLOB
)
;
alter table PN_ENVELOPE_VERSION
  add constraint ENVELOPE_VERSION_PK primary key (VERSION_ID, ENVELOPE_ID);
alter table PN_ENVELOPE_VERSION
  add constraint ENVELOPE_VERSION_TO_STATUS_FK foreign key (STATUS_ID)
  references PN_WORKFLOW_STATUS (STATUS_ID);
alter table PN_ENVELOPE_VERSION
  add constraint ENVELOPE_VERSION_TO_STEP_FK foreign key (STEP_ID, WORKFLOW_ID)
  references PN_WORKFLOW_STEP (STEP_ID, WORKFLOW_ID);
alter table PN_ENVELOPE_VERSION
  add constraint ENVELOPE_VERSION_TO_WF_ENV_FK foreign key (ENVELOPE_ID)
  references PN_WORKFLOW_ENVELOPE (ENVELOPE_ID);
alter table PN_ENVELOPE_VERSION
  add constraint ENVELOPE_VERSION_TO_WF_TRAN_FK foreign key (TRANSITION_ID, WORKFLOW_ID)
  references PN_WORKFLOW_TRANSITION (TRANSITION_ID, WORKFLOW_ID);
create index ENVELOPE_VERSION_IDX1 on PN_ENVELOPE_VERSION (WORKFLOW_ID) TABLESPACE INDEX01;
create index ENVELOPE_VERSION_IDX2 on PN_ENVELOPE_VERSION (TRANSITION_ID) TABLESPACE INDEX01;
create index ENVELOPE_VERSION_IDX3 on PN_ENVELOPE_VERSION (STEP_ID) TABLESPACE INDEX01;
create index ENVELOPE_VERSION_IDX4 on PN_ENVELOPE_VERSION (STATUS_ID) TABLESPACE INDEX01;
create unique index ENVELOPE_VERSION_IDX5 on PN_ENVELOPE_VERSION (VERSION_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_ENVELOPE_VERSION_HAS_OBJECT
prompt =============================================
prompt
create table PN_ENVELOPE_VERSION_HAS_OBJECT
(
  OBJECT_ID            NUMBER(20) not null,
  VERSION_ID           NUMBER(20) not null,
  ENVELOPE_ID          NUMBER(20) not null,
  OBJECT_TYPE          VARCHAR2(80) not null,
  OBJECT_VERSION_ID    NUMBER(20) not null,
  OBJECT_PROPERTIES_ID NUMBER(20),
  CRC                  DATE not null,
  RECORD_STATUS        VARCHAR2(1) not null
)
;
alter table PN_ENVELOPE_VERSION_HAS_OBJECT
  add constraint ENVELOPE_VERSION_HAS_OBJECT_PK primary key (VERSION_ID, ENVELOPE_ID, OBJECT_ID);
alter table PN_ENVELOPE_VERSION_HAS_OBJECT
  add constraint ENVELOPE_VERSOBJ_TO_ENVOBJ_FK foreign key (OBJECT_ID, ENVELOPE_ID)
  references PN_ENVELOPE_HAS_OBJECT (OBJECT_ID, ENVELOPE_ID);
alter table PN_ENVELOPE_VERSION_HAS_OBJECT
  add constraint ENVELOPE_VERSOBJ_TO_ENVVERS_FK foreign key (VERSION_ID, ENVELOPE_ID)
  references PN_ENVELOPE_VERSION (VERSION_ID, ENVELOPE_ID);
alter table PN_ENVELOPE_VERSION_HAS_OBJECT
  add constraint ENVELOPE_VERSOBJ_TO_OBJCLOB_FK foreign key (OBJECT_PROPERTIES_ID)
  references PN_ENVELOPE_OBJECT_CLOB (OBJECT_ID);

prompt
prompt Creating table PN_EVENT_TYPE
prompt ============================
prompt
create table PN_EVENT_TYPE
(
  EVENT_TYPE_ID NUMBER(20) not null,
  NAME          VARCHAR2(80),
  DESCRIPTION   VARCHAR2(500),
  OBJECT_TYPE   VARCHAR2(80) not null,
  RECORD_STATUS VARCHAR2(1),
  CRC           DATE
)
;
alter table PN_EVENT_TYPE
  add constraint PN_EVENT_TYPE_PK primary key (EVENT_TYPE_ID);
alter table PN_EVENT_TYPE
  add constraint PN_EVENT_TYPE_FK1 foreign key (OBJECT_TYPE)
  references PN_OBJECT_TYPE (OBJECT_TYPE);

prompt
prompt Creating table PN_NOTIFICATION_OBJECT_TYPE
prompt ==========================================
prompt
create table PN_NOTIFICATION_OBJECT_TYPE
(
  OBJECT_TYPE     VARCHAR2(80) not null,
  DISPLAY_NAME    VARCHAR2(80),
  IS_SUBSCRIBABLE VARCHAR2(1) default 1
)
;
alter table PN_NOTIFICATION_OBJECT_TYPE
  add constraint NOTIFICATION_OBJECT_TYPE_PK primary key (OBJECT_TYPE);
alter table PN_NOTIFICATION_OBJECT_TYPE
  add constraint NOTIFICATION_OBJECT_TYPE_FK1 foreign key (OBJECT_TYPE)
  references PN_OBJECT_TYPE (OBJECT_TYPE);

prompt
prompt Creating table PN_NOTIFICATION_TYPE
prompt ===================================
prompt
create table PN_NOTIFICATION_TYPE
(
  NOTIFICATION_TYPE_ID NUMBER(20) not null,
  NAME                 VARCHAR2(80),
  DESCRIPTION          VARCHAR2(500),
  DEFAULT_MESSAGE      VARCHAR2(4000),
  OBJECT_TYPE          VARCHAR2(80),
  CREATE_DATE          DATE,
  CREATED_BY_ID        NUMBER(20),
  MODIFIED_DATE        DATE,
  MODIFIED_BY_ID       NUMBER(20),
  RECORD_STATUS        VARCHAR2(1) not null,
  CRC                  DATE
)
;
alter table PN_NOTIFICATION_TYPE
  add constraint NOTIFICATION_TYPE_PK primary key (NOTIFICATION_TYPE_ID);
alter table PN_NOTIFICATION_TYPE
  add constraint NOTIFICATION_TYPE_FK1 foreign key (OBJECT_TYPE)
  references PN_NOTIFICATION_OBJECT_TYPE (OBJECT_TYPE);

prompt
prompt Creating table PN_EVENT_HAS_NOTIFICATION
prompt ========================================
prompt
create table PN_EVENT_HAS_NOTIFICATION
(
  NOTIFICATION_TYPE_ID NUMBER(20) not null,
  EVENT_TYPE_ID        NUMBER(20) not null
)
;
alter table PN_EVENT_HAS_NOTIFICATION
  add constraint EVENT_HAS_NOTIFICATION_PK primary key (NOTIFICATION_TYPE_ID, EVENT_TYPE_ID);
alter table PN_EVENT_HAS_NOTIFICATION
  add constraint PN_EVENT_HAS_NOTIFICATION_FK1 foreign key (NOTIFICATION_TYPE_ID)
  references PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID);
alter table PN_EVENT_HAS_NOTIFICATION
  add constraint PN_EVENT_HAS_NOTIFICATION_FK2 foreign key (EVENT_TYPE_ID)
  references PN_EVENT_TYPE (EVENT_TYPE_ID);

prompt
prompt Creating table PN_FINDER_INGREDIENTS
prompt ====================================
prompt
create table PN_FINDER_INGREDIENTS
(
  INGREDIENTS_ID   NUMBER(20) not null,
  INGREDIENTS_DATA CLOB not null
)
;
alter table PN_FINDER_INGREDIENTS
  add constraint PN_FINDER_INGREDIENTS_PK primary key (INGREDIENTS_ID);

prompt
prompt Creating table PN_FORMS_ACTION_LOOKUP
prompt =====================================
prompt
create table PN_FORMS_ACTION_LOOKUP
(
  ACTION        VARCHAR2(80) not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_FORMS_ACTION_LOOKUP
  add constraint FORMS_ACTION_LOOKUP_PK primary key (ACTION);
alter table PN_FORMS_ACTION_LOOKUP
  add constraint RECORD_STATUS_VALID80
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_FORMS_HISTORY
prompt ===============================
prompt
create table PN_FORMS_HISTORY
(
  OBJECT_ID        NUMBER(20) not null,
  FORMS_HISTORY_ID NUMBER(20) not null,
  ACTION_BY_ID     NUMBER(20),
  ACTION           VARCHAR2(80),
  ACTION_COMMENT   VARCHAR2(255),
  ACTION_DATE      DATE,
  ACTION_NAME      VARCHAR2(80)
)
;
alter table PN_FORMS_HISTORY
  add constraint FORMS_HISTORY_PK primary key (OBJECT_ID, FORMS_HISTORY_ID);
alter table PN_FORMS_HISTORY
  add constraint FORMS_HISTORY_FK1 foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_FORMS_HISTORY
  add constraint FORMS_HISTORY_FK2 foreign key (ACTION_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_FORMS_HISTORY
  add constraint FORMS_HISTORY_FK3 foreign key (ACTION)
  references PN_FORMS_ACTION_LOOKUP (ACTION);
create unique index FORMS_HISTORY_IDX1 on PN_FORMS_HISTORY (FORMS_HISTORY_ID, OBJECT_ID);

prompt
prompt Creating table PN_PHASE
prompt =======================
prompt
create table PN_PHASE
(
  PHASE_ID                  NUMBER(20) not null,
  PROCESS_ID                NUMBER(20) not null,
  PHASE_NAME                VARCHAR2(80) not null,
  PHASE_DESC                VARCHAR2(4000),
  START_DATE                DATE,
  END_DATE                  DATE,
  SEQUENCE                  NUMBER not null,
  STATUS_ID                 NUMBER(20) not null,
  ENTERED_PERCENT_COMPLETE  NUMBER,
  RECORD_STATUS             VARCHAR2(1) not null,
  PROGRESS_REPORTING_METHOD VARCHAR2(80) default 'manual' not null
)
;
alter table PN_PHASE
  add constraint PHASE_PK primary key (PHASE_ID);
alter table PN_PHASE
  add constraint PHASE_OBJ_FK foreign key (PHASE_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_PHASE
  add constraint RECORD_STATUS_VALID48
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_GATE
prompt ======================
prompt
create table PN_GATE
(
  GATE_ID       NUMBER(20) not null,
  PHASE_ID      NUMBER(20) not null,
  GATE_NAME     VARCHAR2(80) not null,
  GATE_DESC     VARCHAR2(4000),
  GATE_DATE     DATE,
  STATUS_ID     NUMBER(20) not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_GATE
  add constraint GATE_PK primary key (GATE_ID);
alter table PN_GATE
  add constraint GATE_FK1 foreign key (PHASE_ID)
  references PN_PHASE (PHASE_ID);
alter table PN_GATE
  add constraint GATE_OBJ_FK foreign key (GATE_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_GATE
  add constraint RECORD_STATUS_VALID46
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_PROCESS
prompt =========================
prompt
create table PN_PROCESS
(
  PROCESS_ID          NUMBER(20) not null,
  PROCESS_NAME        VARCHAR2(80) not null,
  PROCESS_DESC        VARCHAR2(500),
  CURRENT_PHASE_ID    NUMBER(20),
  LAST_GATE_PASSED_ID NUMBER(20),
  RECORD_STATUS       VARCHAR2(1) not null
)
;
alter table PN_PROCESS
  add constraint PROCESS_PK primary key (PROCESS_ID);
alter table PN_PROCESS
  add constraint PROCESS_FK1 foreign key (LAST_GATE_PASSED_ID)
  references PN_GATE (GATE_ID);
alter table PN_PROCESS
  add constraint PROCESS_FK2 foreign key (CURRENT_PHASE_ID)
  references PN_PHASE (PHASE_ID);
alter table PN_PROCESS
  add constraint PROCESS_OBJ_FK foreign key (PROCESS_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_PROCESS
  add constraint RECORD_STATUS_VALID47
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_GLOBAL_CODE
prompt =============================
prompt
create table PN_GLOBAL_CODE
(
  CODE                  NUMBER(8) not null,
  CODE_TYPE_ID          NUMBER(20) not null,
  CODE_NAME             VARCHAR2(80) not null,
  CODE_DESC             VARCHAR2(80),
  CODE_URL              VARCHAR2(240),
  PRESENTATION_SEQUENCE NUMBER(8),
  IS_DEFAULT            NUMBER(1) not null,
  RECORD_STATUS         VARCHAR2(1) not null
)
;
alter table PN_GLOBAL_CODE
  add constraint GLOBAL_CODE_PK primary key (CODE, CODE_TYPE_ID);
alter table PN_GLOBAL_CODE
  add constraint GLOBAL_CODE_FK1 foreign key (CODE_TYPE_ID)
  references PN_CODE_TYPE (CODE_TYPE_ID);
alter table PN_GLOBAL_CODE
  add constraint RECORD_STATUS_VALID22
  check (record_status in ('A','P','D','H'));
create unique index GLOBAL_CODE_IDX1 on PN_GLOBAL_CODE (CODE_TYPE_ID, CODE);

prompt
prompt Creating table PN_GLOBAL_DOMAIN
prompt ===============================
prompt
create table PN_GLOBAL_DOMAIN
(
  TABLE_NAME            VARCHAR2(80) not null,
  COLUMN_NAME           VARCHAR2(80) not null,
  CODE                  NUMBER(8) not null,
  CODE_NAME             VARCHAR2(80) not null,
  CODE_DESC             VARCHAR2(80),
  CODE_URL              VARCHAR2(240),
  PRESENTATION_SEQUENCE NUMBER(8),
  IS_DEFAULT            NUMBER(1) not null,
  RECORD_STATUS         VARCHAR2(1) not null
)
;
alter table PN_GLOBAL_DOMAIN
  add constraint GLOBAL_DOMAIN_PK primary key (TABLE_NAME, COLUMN_NAME, CODE);
alter table PN_GLOBAL_DOMAIN
  add constraint RECORD_STATUS_VALID42
  check (record_status in ('A','P','D','H'));
create unique index GLOBAL_DOMAIN_IDX1 on PN_GLOBAL_DOMAIN (CODE, TABLE_NAME, COLUMN_NAME);

prompt
prompt Creating table PN_GROUP_ACTION_LOOKUP
prompt =====================================
prompt
create table PN_GROUP_ACTION_LOOKUP
(
  ACTION        VARCHAR2(80) not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_GROUP_ACTION_LOOKUP
  add constraint GROUP_ACTION_LOOKUP_PK primary key (ACTION);
alter table PN_GROUP_ACTION_LOOKUP
  add constraint RECORD_STATUS_VALID100
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_GROUP_HAS_GROUP
prompt =================================
prompt
create table PN_GROUP_HAS_GROUP
(
  GROUP_ID        NUMBER(20) not null,
  MEMBER_GROUP_ID NUMBER(20) not null
)
;
alter table PN_GROUP_HAS_GROUP
  add constraint PN_GROUP_HAS_GROUP_PK primary key (GROUP_ID, MEMBER_GROUP_ID);
alter table PN_GROUP_HAS_GROUP
  add constraint PN_GROUP_HAS_GROUP_FK1 foreign key (GROUP_ID)
  references PN_GROUP (GROUP_ID);
alter table PN_GROUP_HAS_GROUP
  add constraint PN_GROUP_HAS_GROUP_FK2 foreign key (MEMBER_GROUP_ID)
  references PN_GROUP (GROUP_ID);

prompt
prompt Creating table PN_GROUP_HAS_PERSON
prompt ==================================
prompt
create table PN_GROUP_HAS_PERSON
(
  GROUP_ID  NUMBER(20) not null,
  PERSON_ID NUMBER(20) not null
)
;
alter table PN_GROUP_HAS_PERSON
  add constraint GROUP_HAS_PERSON_PK primary key (GROUP_ID, PERSON_ID);
alter table PN_GROUP_HAS_PERSON
  add constraint GROUP_HAS_PERSON_FK1 foreign key (GROUP_ID)
  references PN_GROUP (GROUP_ID);
alter table PN_GROUP_HAS_PERSON
  add constraint GROUP_HAS_PERSON_FK2 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
create index GROUP_HAS_PERSON_IDX1 on PN_GROUP_HAS_PERSON (PERSON_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_GROUP_HISTORY
prompt ===============================
prompt
create table PN_GROUP_HISTORY
(
  GROUP_ID         NUMBER(20) not null,
  GROUP_HISTORY_ID NUMBER(20) not null,
  ACTION_BY_ID     NUMBER(20),
  ACTION           VARCHAR2(80),
  ACTION_COMMENT   VARCHAR2(255),
  ACTION_DATE      DATE,
  ACTION_NAME      VARCHAR2(80)
)
;
alter table PN_GROUP_HISTORY
  add constraint GROUP_HISTORY_PK primary key (GROUP_ID, GROUP_HISTORY_ID);
alter table PN_GROUP_HISTORY
  add constraint GROUP_HISTORY_FK1 foreign key (GROUP_ID)
  references PN_GROUP (GROUP_ID);
alter table PN_GROUP_HISTORY
  add constraint GROUP_HISTORY_FK2 foreign key (ACTION_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_GROUP_HISTORY
  add constraint GROUP_HISTORY_FK3 foreign key (ACTION)
  references PN_GROUP_ACTION_LOOKUP (ACTION);
create unique index GROUP_HISTORY_IDX1 on PN_GROUP_HISTORY (GROUP_HISTORY_ID, GROUP_ID);

prompt
prompt Creating table PN_INDUSTRY_CLASSIFICATION
prompt =========================================
prompt
create table PN_INDUSTRY_CLASSIFICATION
(
  INDUSTRY_ID NUMBER(20) not null,
  NAME        VARCHAR2(80) not null,
  DESCRIPTION VARCHAR2(500)
)
;
alter table PN_INDUSTRY_CLASSIFICATION
  add constraint INDUSTRY_CLASSIFICATION_PK primary key (INDUSTRY_ID);

prompt
prompt Creating table PN_INDUSTRY_HAS_CATEGORY
prompt =======================================
prompt
create table PN_INDUSTRY_HAS_CATEGORY
(
  INDUSTRY_ID NUMBER(20) not null,
  CATEGORY_ID NUMBER(20) not null
)
;
alter table PN_INDUSTRY_HAS_CATEGORY
  add constraint INDUSTRY_HAS_CATEGORY_PK primary key (INDUSTRY_ID, CATEGORY_ID);

prompt
prompt Creating table PN_INVITED_USERS
prompt ===============================
prompt
create table PN_INVITED_USERS
(
  INVITATION_CODE          NUMBER(6) not null,
  SPACE_ID                 NUMBER(20) not null,
  INVITEE_EMAIL            VARCHAR2(240) not null,
  INVITOR_ID               NUMBER(20) not null,
  DATE_INVITED             DATE not null,
  DATE_RESPONDED           DATE,
  INVITEE_FIRSTNAME        VARCHAR2(40),
  INVITEE_LASTNAME         VARCHAR2(60),
  INVITEE_RESPONSIBILITIES VARCHAR2(500),
  INVITED_STATUS           VARCHAR2(20) not null,
  INVITATION_ACTED_UPON    NUMBER(1) default 0 not null,
  PERSON_ID                NUMBER(20) not null
)
;
alter table PN_INVITED_USERS
  add constraint INVITED_USERS_PK primary key (INVITATION_CODE);
alter table PN_INVITED_USERS
  add constraint PN_INVITED_USERS_UK1 unique (SPACE_ID, PERSON_ID);
alter table PN_INVITED_USERS
  add constraint INVITED_USERS_FK1 foreign key (INVITOR_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_INVITED_USERS
  add constraint INVITED_STATUS_VALID
  check (invited_status in ('Invited','Accepted','Rejected','Deleted'));
create index INVITED_USERS_IDX1 on PN_INVITED_USERS (SPACE_ID) TABLESPACE INDEX01;
create index INVITED_USERS_IDX2 on PN_INVITED_USERS (INVITEE_EMAIL) TABLESPACE INDEX01;

prompt
prompt Creating table PN_INVOICE
prompt =========================
prompt
create table PN_INVOICE
(
  INVOICE_ID        NUMBER(20) not null,
  CREATION_DATETIME DATE default (sysdate) not null
)
;
alter table PN_INVOICE
  add constraint PN_INVOICE_PK primary key (INVOICE_ID);

prompt
prompt Creating table PN_INVOICE_LOB
prompt =============================
prompt
create table PN_INVOICE_LOB
(
  INVOICE_ID       NUMBER(20) not null,
  INVOICE_LOB_DATA CLOB
)
;
alter table PN_INVOICE_LOB
  add constraint PN_INVOICE_LOB_PK primary key (INVOICE_ID);

prompt
prompt Creating table PN_JAVA_ERROR_LOG
prompt ================================
prompt
create table PN_JAVA_ERROR_LOG
(
  ERROR_DATE    DATE,
  PERSON_ID     NUMBER(20),
  ERROR_NAME    VARCHAR2(255),
  ERROR_MESSAGE VARCHAR2(4000),
  STACK_TRACE   VARCHAR2(4000),
  SEVERITY      VARCHAR2(80)
)
;

prompt
prompt Creating table PN_JOB_DESCRIPTION_FEEDBACK
prompt ==========================================
prompt
create table PN_JOB_DESCRIPTION_FEEDBACK
(
  OTHER_JOB_DESCRIPTION VARCHAR2(80)
)
;

prompt
prompt Creating table PN_JOB_DESCRIPTION_LOOKUP
prompt ========================================
prompt
create table PN_JOB_DESCRIPTION_LOOKUP
(
  JOB_DESCRIPTION_CODE NUMBER(4) not null,
  JOB_DESCRIPTION      VARCHAR2(80) not null
)
;
alter table PN_JOB_DESCRIPTION_LOOKUP
  add constraint JOB_DESCRIPTION_LOOKUP_PK primary key (JOB_DESCRIPTION_CODE);

prompt
prompt Creating table PN_LDAP_CONFIGURATION_CONTEXT
prompt ============================================
prompt
create table PN_LDAP_CONFIGURATION_CONTEXT
(
  CONTEXT_ID                  NUMBER(20) not null,
  PROTOCOL                    VARCHAR2(20) not null,
  HOST                        VARCHAR2(120) not null,
  PORT                        VARCHAR2(20) not null,
  SEARCH_BASE                 VARCHAR2(500) not null,
  AUTHENTICATION_METHOD       VARCHAR2(20) not null,
  AUTHENTICATED_USER_DN       VARCHAR2(500),
  AUTHENTICATED_USER_PASSWORD VARCHAR2(240),
  DESCRIPTION                 VARCHAR2(500)
)
;
alter table PN_LDAP_CONFIGURATION_CONTEXT
  add constraint PN_LDAP_CONFIGURATION_PK primary key (CONTEXT_ID);

prompt
prompt Creating table PN_LDAP_DIRECTORY_CONFIG
prompt =======================================
prompt
create table PN_LDAP_DIRECTORY_CONFIG
(
  CONTEXT_ID                    NUMBER(20) not null,
  HOSTNAME_VALUES               VARCHAR2(4000),
  SECURE_HOSTNAME_VALUES        VARCHAR2(4000),
  IS_USE_SSL                    NUMBER(1),
  SEARCH_BASE_DN                VARCHAR2(4000),
  SEARCH_TYPE_ID                VARCHAR2(80),
  USERNAME_ATTRIBUTE_NAME       VARCHAR2(1000),
  SEARCH_SUBTREES               VARCHAR2(1000),
  NON_AUTH_ACCESS_TYPE_ID       VARCHAR2(80),
  SPECIFIC_USER_RELATIVE_DN     VARCHAR2(4000),
  SPECIFIC_USER_PASSWORD        VARCHAR2(1000),
  IS_AVAILABLE_DIRECTORY_SEARCH NUMBER(1),
  DIRECTORY_SEARCH_DISPLAY_NAME VARCHAR2(80),
  SEARCH_FILTER_EXPRESSION      VARCHAR2(1000),
  ALLOWS_AUTOMATIC_REGISTRATION NUMBER(1)
)
;
alter table PN_LDAP_DIRECTORY_CONFIG
  add constraint PN_LDAP_DIRECTORY_CONFIG_PK primary key (CONTEXT_ID);

prompt
prompt Creating table PN_LDAP_DIRECTORY_ATTR_MAP
prompt =========================================
prompt
create table PN_LDAP_DIRECTORY_ATTR_MAP
(
  CONTEXT_ID                 NUMBER(20) not null,
  ATTRIBUTE_ID               VARCHAR2(250) not null,
  LDAP_ATTRIBUTE_NAME        VARCHAR2(1000) not null,
  LDAP_ATTRIBUTE_VALUE_INDEX NUMBER(3)
)
;
alter table PN_LDAP_DIRECTORY_ATTR_MAP
  add constraint PN_LDAP_DIRECTORY_ATTR_MAP_PK primary key (CONTEXT_ID, ATTRIBUTE_ID);
alter table PN_LDAP_DIRECTORY_ATTR_MAP
  add constraint PN_LDAP_DIRECTORY_ATTR_MAP_FK1 foreign key (CONTEXT_ID)
  references PN_LDAP_DIRECTORY_CONFIG (CONTEXT_ID) on delete set null;

prompt
prompt Creating table PN_LEDGER
prompt ========================
prompt
create table PN_LEDGER
(
  LEDGER_ID                     NUMBER(20) not null,
  BILL_ID                       NUMBER(20) not null,
  RESPONSIBLE_PARTY_ID          NUMBER(20) not null,
  ORIGINATING_PAYMENT_ID        NUMBER(20) not null,
  DUE_SINCE_DATETIME            DATE not null,
  UNIT_PRICE_VALUE              NUMBER not null,
  QUANTITY_AMOUNT               NUMBER not null,
  QUANTITY_UOM_ID               NUMBER not null,
  CATEGORY_ID                   NUMBER not null,
  PART_DETAILS_PART_NUMBER      VARCHAR2(500) not null,
  PART_DETAILS_PART_DESCRIPTION VARCHAR2(1000),
  GROUP_TYPE_ID                 NUMBER(20) not null,
  GROUP_VALUE                   VARCHAR2(500),
  GROUP_DESCRIPTION             VARCHAR2(1000),
  RECORD_STATUS                 VARCHAR2(1),
  INVOICE_ID                    NUMBER(20),
  INVOICE_DATE                  DATE,
  INVOICE_STATUS_ID             NUMBER(20) not null
)
;
alter table PN_LEDGER
  add constraint PN_LEDGER_PK primary key (LEDGER_ID);
alter table PN_LEDGER
  add constraint PN_LEDGER_FK1 foreign key (BILL_ID)
  references PN_BILL (BILL_ID);
alter table PN_LEDGER
  add constraint PN_LEDGER_FK2 foreign key (ORIGINATING_PAYMENT_ID)
  references PN_PAYMENT_INFORMATION (PAYMENT_ID);
alter table PN_LEDGER
  add constraint PN_LEDGER_FK3 foreign key (INVOICE_ID)
  references PN_INVOICE (INVOICE_ID);

prompt
prompt Creating table PN_LICENSE_CERTIFICATE_LOB
prompt =========================================
prompt
create table PN_LICENSE_CERTIFICATE_LOB
(
  CERTIFICATE_ID       NUMBER(20) not null,
  CERTIFICATE_LOB_DATA CLOB
)
;
alter table PN_LICENSE_CERTIFICATE_LOB
  add constraint PN_LICENSE_CERTIFICATE_LOB_FK1 foreign key (CERTIFICATE_ID)
  references PN_LICENSE_CERTIFICATE (CERTIFICATE_ID) on delete set null;

prompt
prompt Creating table PN_LICENSE_MASTER_PROP_CLOB
prompt ==========================================
prompt
create table PN_LICENSE_MASTER_PROP_CLOB
(
  MASTER_PROP_ID       NUMBER(20) not null,
  MASTER_PROP_LOB_DATA CLOB
)
;
alter table PN_LICENSE_MASTER_PROP_CLOB
  add constraint PN_MASTER_PROP_CLOB_PK primary key (MASTER_PROP_ID);

prompt
prompt Creating table PN_LICENSE_PERSON_HISTORY
prompt ========================================
prompt
create table PN_LICENSE_PERSON_HISTORY
(
  HISTORY_ID       NUMBER(20) not null,
  LICENSE_ID       NUMBER(20) not null,
  PERSON_ID        NUMBER(20) not null,
  CREATED_DATETIME DATE not null
)
;
alter table PN_LICENSE_PERSON_HISTORY
  add constraint PN_LICENSE_PERSON_HISTORY_PK primary key (HISTORY_ID);

prompt
prompt Creating table PN_LICENSE_PURCHASER
prompt ===================================
prompt
create table PN_LICENSE_PURCHASER
(
  LICENSE_ID NUMBER(20) not null,
  PERSON_ID  NUMBER(20) not null
)
;
alter table PN_LICENSE_PURCHASER
  add constraint PN_LICENSE_PURCHASER_PK primary key (LICENSE_ID);
alter table PN_LICENSE_PURCHASER
  add constraint PN_LICENSE_PURCHASER_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_LICENSE_PURCHASER
  add constraint PN_LICENSE_PURCHASER_FK2 foreign key (LICENSE_ID)
  references PN_LICENSE (LICENSE_ID);

prompt
prompt Creating table PN_LOGIN_HISTORY
prompt ===============================
prompt
create table PN_LOGIN_HISTORY
(
  PERSON_ID         NUMBER(20) not null,
  LOGIN_DATE        DATE not null,
  LOGIN_NAME_USED   VARCHAR2(80),
  LOGIN_CONCURRENCY NUMBER(20) default 1 not null
)
;
alter table PN_LOGIN_HISTORY
  add constraint LOGIN_HISTORY_PK primary key (PERSON_ID, LOGIN_DATE, LOGIN_CONCURRENCY);
alter table PN_LOGIN_HISTORY
  add constraint LOGIN_HISTORY_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
create index LOGIN_HISTORY_IDX1 on PN_LOGIN_HISTORY (LOGIN_DATE) TABLESPACE INDEX01;

prompt
prompt Creating table PN_METHODOLOGY_IN_INDUSTRY
prompt =========================================
prompt
create table PN_METHODOLOGY_IN_INDUSTRY
(
  INDUSTRY_ID    NUMBER(20) not null,
  METHODOLOGY_ID NUMBER(20) not null
)
;
alter table PN_METHODOLOGY_IN_INDUSTRY
  add constraint METHODOLOGY_IN_INDUSTRY_PK primary key (INDUSTRY_ID, METHODOLOGY_ID);

prompt
prompt Creating table PN_METHODOLOGY_SPACE
prompt ===================================
prompt
create table PN_METHODOLOGY_SPACE
(
  METHODOLOGY_ID    NUMBER(20) not null,
  METHODOLOGY_NAME  VARCHAR2(80),
  METHODOLOGY_DESC  VARCHAR2(1000),
  STATUS_ID         NUMBER(20),
  CREATED_BY_ID     NUMBER(20) not null,
  CREATED_DATE      DATE,
  MODIFIED_BY_ID    NUMBER(20) not null,
  MODIFIED_DATE     DATE,
  RECORD_STATUS     VARCHAR2(1),
  CRC               DATE,
  USE_SCENARIO_CLOB CLOB,
  IS_GLOBAL         NUMBER(1) default 0 not null
)
;
comment on column PN_METHODOLOGY_SPACE.IS_GLOBAL
  is 'If set to true implies that a given template is globally accessible.';
alter table PN_METHODOLOGY_SPACE
  add constraint METHODOLOGY_SPACE_PK primary key (METHODOLOGY_ID);

prompt
prompt Creating table PN_MODULE
prompt ========================
prompt
create table PN_MODULE
(
  MODULE_ID                  NUMBER(20) not null,
  NAME                       VARCHAR2(80) not null,
  DESCRIPTION                VARCHAR2(500),
  DEFAULT_PERMISSION_ACTIONS NUMBER(10) not null
)
;
alter table PN_MODULE
  add constraint MODULE_PK primary key (MODULE_ID);

prompt
prompt Creating table PN_MODULE_HAS_OBJECT_TYPE
prompt ========================================
prompt
create table PN_MODULE_HAS_OBJECT_TYPE
(
  MODULE_ID   NUMBER(20) not null,
  OBJECT_TYPE VARCHAR2(80) not null,
  IS_OWNER    NUMBER(1) default 0 not null
)
;
alter table PN_MODULE_HAS_OBJECT_TYPE
  add constraint MODULE_OBJECT_TYPE_PK primary key (MODULE_ID, OBJECT_TYPE);
alter table PN_MODULE_HAS_OBJECT_TYPE
  add constraint MODULE_OBJECT_TYPE_FK1 foreign key (OBJECT_TYPE)
  references PN_OBJECT_TYPE (OBJECT_TYPE);
alter table PN_MODULE_HAS_OBJECT_TYPE
  add constraint MODULE_OBJECT_TYPE_FK2 foreign key (MODULE_ID)
  references PN_MODULE (MODULE_ID);
create unique index MODULE_OBJECT_TYPE_IDX1 on PN_MODULE_HAS_OBJECT_TYPE (OBJECT_TYPE, MODULE_ID);

prompt
prompt Creating table PN_SPACE_HAS_MODULE
prompt ==================================
prompt
create table PN_SPACE_HAS_MODULE
(
  SPACE_ID  NUMBER(20) not null,
  MODULE_ID NUMBER(20) not null,
  IS_ACTIVE NUMBER(1) default 1 not null
)
;
alter table PN_SPACE_HAS_MODULE
  add constraint SPACE_MODULE_PK primary key (SPACE_ID, MODULE_ID);
alter table PN_SPACE_HAS_MODULE
  add constraint SPACE_HAS_MODULE_FK1 foreign key (MODULE_ID)
  references PN_MODULE (MODULE_ID);
create unique index SPACE_MODULE_IDX1 on PN_SPACE_HAS_MODULE (MODULE_ID, SPACE_ID);

prompt
prompt Creating table PN_MODULE_PERMISSION
prompt ===================================
prompt
create table PN_MODULE_PERMISSION
(
  SPACE_ID  NUMBER(20) not null,
  GROUP_ID  NUMBER(20) not null,
  MODULE_ID NUMBER(20) not null,
  ACTIONS   NUMBER(10) not null
)
;
alter table PN_MODULE_PERMISSION
  add constraint MODULE_PERMISSION_PK primary key (SPACE_ID, GROUP_ID, MODULE_ID);
alter table PN_MODULE_PERMISSION
  add constraint MODULE_PERMISSION_FK1 foreign key (SPACE_ID, MODULE_ID)
  references PN_SPACE_HAS_MODULE (SPACE_ID, MODULE_ID);
alter table PN_MODULE_PERMISSION
  add constraint MODULE_PERMISSION_FK2 foreign key (GROUP_ID)
  references PN_GROUP (GROUP_ID);
create index MODULE_PERMISSION_IDX1 on PN_MODULE_PERMISSION (MODULE_ID) TABLESPACE INDEX01;
create index MODULE_PERMISSION_IDX2 on PN_MODULE_PERMISSION (GROUP_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_NEWS
prompt ======================
prompt
create table PN_NEWS
(
  NEWS_ID           NUMBER(20) not null,
  TOPIC             VARCHAR2(80),
  PRIORITY_ID       NUMBER(20) not null,
  POSTED_BY_ID      NUMBER(20),
  POSTED_DATETIME   DATE,
  CREATED_BY_ID     NUMBER(20) not null,
  CREATED_DATETIME  DATE not null,
  MODIFIED_BY_ID    NUMBER(20),
  MODIFIED_DATETIME DATE,
  CRC               DATE not null,
  RECORD_STATUS     VARCHAR2(1) not null,
  NOTIFICATION_ID   NUMBER,
  MESSAGE_CLOB      CLOB
)
;
alter table PN_NEWS
  add constraint NEWS_PK primary key (NEWS_ID);

prompt
prompt Creating table PN_NEWS_ACTION_LOOKUP
prompt ====================================
prompt
create table PN_NEWS_ACTION_LOOKUP
(
  ACTION        VARCHAR2(80) not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_NEWS_ACTION_LOOKUP
  add constraint NEWS_ACTION_LOOKUP_PK primary key (ACTION);

prompt
prompt Creating table PN_NEWS_HISTORY
prompt ==============================
prompt
create table PN_NEWS_HISTORY
(
  NEWS_ID         NUMBER(20) not null,
  NEWS_HISTORY_ID NUMBER(20) not null,
  ACTION_BY_ID    NUMBER(20),
  ACTION          VARCHAR2(80),
  ACTION_COMMENT  VARCHAR2(255),
  ACTION_DATE     DATE,
  ACTION_NAME     VARCHAR2(80)
)
;
alter table PN_NEWS_HISTORY
  add constraint NEWS_HISTORY_PK primary key (NEWS_ID, NEWS_HISTORY_ID);
alter table PN_NEWS_HISTORY
  add constraint NEWS_HISTORY_FK1 foreign key (NEWS_ID)
  references PN_NEWS (NEWS_ID);
alter table PN_NEWS_HISTORY
  add constraint NEWS_HISTORY_FK2 foreign key (ACTION_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_NEWS_HISTORY
  add constraint NEWS_HISTORY_FK3 foreign key (ACTION)
  references PN_NEWS_ACTION_LOOKUP (ACTION);
create unique index NEWS_HISTORY_IDX1 on PN_NEWS_HISTORY (NEWS_HISTORY_ID, NEWS_ID);

prompt
prompt Creating table PN_NEXT_DOC_REPOSITORY
prompt =====================================
prompt
create table PN_NEXT_DOC_REPOSITORY
(
  REPOSITORY_SEQUENCE NUMBER(4) not null
)
;
alter table PN_NEXT_DOC_REPOSITORY
  add constraint NEXT_DOC_REPOSITORY_PK primary key (REPOSITORY_SEQUENCE);

prompt
prompt Creating table PN_NOTIFICATION_CLOB
prompt ===================================
prompt
create table PN_NOTIFICATION_CLOB
(
  OBJECT_ID  NUMBER(20) not null,
  CLOB_FIELD CLOB
)
;
alter table PN_NOTIFICATION_CLOB
  add constraint NOTIFICATION_CLOB_PK primary key (OBJECT_ID);

prompt
prompt Creating table PN_NOTIFICATION
prompt ==============================
prompt
create table PN_NOTIFICATION
(
  NOTIFICATION_ID       NUMBER(20) not null,
  DELIVERY_TYPE_ID      NUMBER(20),
  DELIVERY_ADDRESS      VARCHAR2(240) not null,
  CREATED_DATE          DATE,
  CREATED_BY_ID         VARCHAR2(80),
  MODIFIED_DATE         DATE,
  MODIFIED_BY_ID        NUMBER(20) not null,
  RECORD_STATUS         VARCHAR2(1),
  CRC                   DATE,
  NOTIFICATION_CLOB_ID  NUMBER(20),
  DELIVERY_FROM_ADDRESS VARCHAR2(80),
  CUSTOMIZATION_USER_ID NUMBER(20),
  SENDER_ID             NUMBER(20)
)
;
comment on column PN_NOTIFICATION.SENDER_ID
  is 'will be populated based on the value of created_by in pn_scheduled_subscriptions';
alter table PN_NOTIFICATION
  add constraint NOTIFICATION_PK primary key (NOTIFICATION_ID);
alter table PN_NOTIFICATION
  add constraint PN_NOTIFICATION_FK1 foreign key (DELIVERY_TYPE_ID)
  references PN_NOTIFICATION_DELIVERY_TYPE (DELIVERY_TYPE_ID);
alter table PN_NOTIFICATION
  add constraint PN_NOTIFICATION_FK2 foreign key (NOTIFICATION_CLOB_ID)
  references PN_NOTIFICATION_CLOB (OBJECT_ID);

prompt
prompt Creating table PN_NOTIFICATION_LOG
prompt ==================================
prompt
create table PN_NOTIFICATION_LOG
(
  NOTIFICATION_LOG_ID NUMBER(20) not null,
  NOTIFICATION_ID     NUMBER(20),
  NUMBER_OF_ATTEMPTS  NUMBER,
  DELIVERY_STATUS     NUMBER(20) not null,
  DELIVERY_TIME       DATE,
  DELIVERY_NOTES      VARCHAR2(255),
  RECORD_STATUS       VARCHAR2(1)
)
;
alter table PN_NOTIFICATION_LOG
  add constraint NOTIFICATION_LOG_PK primary key (NOTIFICATION_LOG_ID);
alter table PN_NOTIFICATION_LOG
  add constraint PN_NOTIFICATION_LOG_FK1 foreign key (NOTIFICATION_ID)
  references PN_NOTIFICATION (NOTIFICATION_ID);

prompt
prompt Creating table PN_NOTIFICATION_QUEUE
prompt ====================================
prompt
create table PN_NOTIFICATION_QUEUE
(
  NOTIFICATION_ID   NUMBER(20) not null,
  POSTED_DATE       DATE,
  POSTED_BY_ID      VARCHAR2(80),
  RECORD_STATUS     VARCHAR2(1),
  BATCH_ID          NUMBER(20),
  DELIVERY_STATUS   VARCHAR2(80),
  NUMBER_OF_RETRIES NUMBER default 0,
  IS_IMMEDIATE      NUMBER(1)
)
;
alter table PN_NOTIFICATION_QUEUE
  add constraint NOTIFICATION_QUEUE_PK primary key (NOTIFICATION_ID);
alter table PN_NOTIFICATION_QUEUE
  add constraint PN_NOTIFICATION_QUEUE_FK1 foreign key (NOTIFICATION_ID)
  references PN_NOTIFICATION (NOTIFICATION_ID);

prompt
prompt Creating table PN_NOTIFICATION_SCHED_STATUS
prompt ===========================================
prompt
create table PN_NOTIFICATION_SCHED_STATUS
(
  SCHEDULER_ID        NUMBER(20) not null,
  LAST_CHECK_DATETIME DATE not null
)
;
alter table PN_NOTIFICATION_SCHED_STATUS
  add constraint NOTIFICATION_SCHED_STATUS_PK primary key (SCHEDULER_ID);

prompt
prompt Creating table PN_OBJECT_HAS_DISCUSSION
prompt =======================================
prompt
create table PN_OBJECT_HAS_DISCUSSION
(
  OBJECT_ID           NUMBER(20) not null,
  DISCUSSION_GROUP_ID NUMBER(20) not null
)
;
alter table PN_OBJECT_HAS_DISCUSSION
  add constraint SPACE_HAS_DISCUSSION_PK primary key (OBJECT_ID, DISCUSSION_GROUP_ID);
alter table PN_OBJECT_HAS_DISCUSSION
  add constraint OBJECT_HAS_DISC_FK1 foreign key (DISCUSSION_GROUP_ID)
  references PN_DISCUSSION_GROUP (DISCUSSION_GROUP_ID);
alter table PN_OBJECT_HAS_DISCUSSION
  add constraint OBJECT_HAS_DISC_FK2 foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
create index OBJECT_DISCUSSION_IDX1 on PN_OBJECT_HAS_DISCUSSION (DISCUSSION_GROUP_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_OBJECT_HAS_DOC_CONTAINER
prompt ==========================================
prompt
create table PN_OBJECT_HAS_DOC_CONTAINER
(
  OBJECT_ID    NUMBER(20) not null,
  CONTAINER_ID NUMBER(20) not null
)
;

prompt
prompt Creating table PN_OBJECT_HAS_SUBSCRIPTION
prompt =========================================
prompt
create table PN_OBJECT_HAS_SUBSCRIPTION
(
  SUBSCRIPTION_ID NUMBER(20) not null,
  OBJECT_ID       NUMBER(20) not null
)
;
alter table PN_OBJECT_HAS_SUBSCRIPTION
  add constraint OBJECT_HAS_SUBSCRIPTION_PK primary key (SUBSCRIPTION_ID, OBJECT_ID);
alter table PN_OBJECT_HAS_SUBSCRIPTION
  add constraint PN_OBJECT_HAS_SUBSCRIPTION_FK1 foreign key (SUBSCRIPTION_ID)
  references PN_SUBSCRIPTION (SUBSCRIPTION_ID);

prompt
prompt Creating table PN_OBJECT_IN_CATEGORY
prompt ====================================
prompt
create table PN_OBJECT_IN_CATEGORY
(
  CATEGORY_ID NUMBER(20) not null,
  OBJECT_ID   NUMBER(20) not null
)
;
alter table PN_OBJECT_IN_CATEGORY
  add constraint OBJECT_IN_CATEGORY_PK primary key (CATEGORY_ID, OBJECT_ID);

prompt
prompt Creating table PN_OBJECT_LINK
prompt =============================
prompt
create table PN_OBJECT_LINK
(
  FROM_OBJECT_ID NUMBER(20) not null,
  TO_OBJECT_ID   NUMBER(20) not null,
  CONTEXT        NUMBER(4) not null
)
;
alter table PN_OBJECT_LINK
  add constraint OBJECT_LINK_PK primary key (FROM_OBJECT_ID, TO_OBJECT_ID, CONTEXT);
alter table PN_OBJECT_LINK
  add constraint OBJECT_LINK_FK1 foreign key (FROM_OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_OBJECT_LINK
  add constraint OBJECT_LINK_FK2 foreign key (TO_OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
create unique index OBJECT_LINK_IDX1 on PN_OBJECT_LINK (TO_OBJECT_ID, FROM_OBJECT_ID);

prompt
prompt Creating table PN_OBJECT_NAME
prompt =============================
prompt
create table PN_OBJECT_NAME
(
  OBJECT_ID NUMBER(20) not null,
  NAME      VARCHAR2(500) not null
)
;
alter table PN_OBJECT_NAME
  add constraint OBJECT_NAME_PK primary key (OBJECT_ID);

prompt
prompt Creating table PN_OBJECT_PERMISSION
prompt ===================================
prompt
create table PN_OBJECT_PERMISSION
(
  OBJECT_ID NUMBER(20) not null,
  GROUP_ID  NUMBER(20) not null,
  ACTIONS   NUMBER(10) not null
)
;
alter table PN_OBJECT_PERMISSION
  add constraint OBJECT_PERMISSION_PK primary key (OBJECT_ID, GROUP_ID);
alter table PN_OBJECT_PERMISSION
  add constraint OBJECT_PERMISSION_FK1 foreign key (GROUP_ID)
  references PN_GROUP (GROUP_ID);
alter table PN_OBJECT_PERMISSION
  add constraint OBJECT_PERMISSION_FK2 foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
create unique index OBJECT_PERMISSION_IDX1 on PN_OBJECT_PERMISSION (GROUP_ID, OBJECT_ID);

prompt
prompt Creating table PN_OBJECT_TYPE_SUBSCRIPTION
prompt ==========================================
prompt
create table PN_OBJECT_TYPE_SUBSCRIPTION
(
  SUBSCRIPTION_ID NUMBER(20) not null,
  OBJECT_TYPE     VARCHAR2(80) not null
)
;
alter table PN_OBJECT_TYPE_SUBSCRIPTION
  add constraint PN_OBJECT_TYPE_SUBSCRIPTION_PK primary key (SUBSCRIPTION_ID, OBJECT_TYPE);
alter table PN_OBJECT_TYPE_SUBSCRIPTION
  add constraint PN_OBJECT_TYPE_SUB_FK1 foreign key (SUBSCRIPTION_ID)
  references PN_SUBSCRIPTION (SUBSCRIPTION_ID);

prompt
prompt Creating table PN_SECURITY_ACTION
prompt =================================
prompt
create table PN_SECURITY_ACTION
(
  ACTION_ID   NUMBER(20) not null,
  NAME        VARCHAR2(80) not null,
  DESCRIPTION VARCHAR2(255),
  BIT_MASK    NUMBER(20) not null
)
;
alter table PN_SECURITY_ACTION
  add constraint SECURITY_ACTION_PK primary key (ACTION_ID);

prompt
prompt Creating table PN_OBJECT_TYPE_SUPPORTS_ACTION
prompt =============================================
prompt
create table PN_OBJECT_TYPE_SUPPORTS_ACTION
(
  OBJECT_TYPE      VARCHAR2(80) not null,
  ACTION_ID        NUMBER(20) not null,
  PRESENTATION_SEQ NUMBER(3)
)
;
alter table PN_OBJECT_TYPE_SUPPORTS_ACTION
  add constraint OBJ_TYPE_SUP_ACTION_PK primary key (OBJECT_TYPE, ACTION_ID);
alter table PN_OBJECT_TYPE_SUPPORTS_ACTION
  add constraint OBJ_TYPE_SUP_ACTION_FK1 foreign key (ACTION_ID)
  references PN_SECURITY_ACTION (ACTION_ID);
alter table PN_OBJECT_TYPE_SUPPORTS_ACTION
  add constraint OBJ_TYPE_SUP_ACTION_FK2 foreign key (OBJECT_TYPE)
  references PN_OBJECT_TYPE (OBJECT_TYPE);
create unique index OBJ_TYPE_SUP_ACTION_IDX1 on PN_OBJECT_TYPE_SUPPORTS_ACTION (ACTION_ID, OBJECT_TYPE);

prompt
prompt Creating table PN_PAGE_PERMISSION
prompt =================================
prompt
create table PN_PAGE_PERMISSION
(
  SPACE_ID NUMBER(20) not null,
  PAGE     VARCHAR2(240) not null,
  GROUP_ID NUMBER(20) not null,
  ACTIONS  NUMBER(10) not null
)
;
alter table PN_PAGE_PERMISSION
  add constraint PAGE_PERMISSION_PK primary key (SPACE_ID, PAGE, GROUP_ID);
alter table PN_PAGE_PERMISSION
  add constraint PAGE_PERMISSION_FK1 foreign key (GROUP_ID)
  references PN_GROUP (GROUP_ID);
create index PAGE_PERMISSION_IDX1 on PN_PAGE_PERMISSION (GROUP_ID, SPACE_ID, PAGE) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PAYMENT_MODEL_CHARGE
prompt ======================================
prompt
create table PN_PAYMENT_MODEL_CHARGE
(
  PAYMENT_MODEL_ID NUMBER(20) not null,
  CHARGE_CODE      VARCHAR2(500) not null
)
;
alter table PN_PAYMENT_MODEL_CHARGE
  add constraint PN_PAYMENT_MODEL_CHARGE_PK primary key (PAYMENT_MODEL_ID);
alter table PN_PAYMENT_MODEL_CHARGE
  add constraint PN_PAYMENT_MODEL_CHARGE_FK foreign key (PAYMENT_MODEL_ID)
  references PN_PAYMENT_MODEL (PAYMENT_MODEL_ID) on delete set null;

prompt
prompt Creating table PN_PAYMENT_MODEL_CREDITCARD
prompt ==========================================
prompt
create table PN_PAYMENT_MODEL_CREDITCARD
(
  PAYMENT_MODEL_ID  NUMBER(20) not null,
  CARD_NUMBER       VARCHAR2(100) not null,
  CARD_EXPIRY_MONTH NUMBER(2) not null,
  CARD_EXPIRY_YEAR  NUMBER(4) not null
)
;
alter table PN_PAYMENT_MODEL_CREDITCARD
  add constraint PN_PAYMENT_MODEL_CREDITCARD_PK primary key (PAYMENT_MODEL_ID);

prompt
prompt Creating table PN_PERSON_ALLOCATION
prompt ===================================
prompt
create table PN_PERSON_ALLOCATION
(
  SPACE_ID        NUMBER(20) not null,
  PERSON_ID       NUMBER(20) not null,
  ALLOCATION_ID   NUMBER(20) not null,
  HOURS_ALLOCATED NUMBER(5,2),
  ALLOCATION_DATE DATE
)
;
alter table PN_PERSON_ALLOCATION
  add constraint PERSON_ALLOCATION_PK primary key (SPACE_ID, PERSON_ID, ALLOCATION_ID);
alter table PN_PERSON_ALLOCATION
  add constraint PERSON_ALLOCATION_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
create index PERSON_ALLOCATION_IDX1 on PN_PERSON_ALLOCATION (PERSON_ID) TABLESPACE INDEX01;
create index PERSON_ALLOCATION_IDX2 on PN_PERSON_ALLOCATION (ALLOCATION_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PERSON_AUTHENTICATOR
prompt ======================================
prompt
create table PN_PERSON_AUTHENTICATOR
(
  PERSON_ID        NUMBER(20) not null,
  AUTHENTICATOR_ID NUMBER(20) not null
)
;
alter table PN_PERSON_AUTHENTICATOR
  add constraint PERSON_AUTHENTICATOR_PK primary key (PERSON_ID, AUTHENTICATOR_ID);
alter table PN_PERSON_AUTHENTICATOR
  add constraint PERSON_AUTH_FK2 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_PERSON_AUTHENTICATOR
  add constraint PN_PERSON_AUTH_FK1 foreign key (AUTHENTICATOR_ID)
  references PN_AUTHENTICATOR (AUTHENTICATOR_ID);
create index PERSON_AUTHENTICATOR_IDX1 on PN_PERSON_AUTHENTICATOR (AUTHENTICATOR_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PERSON_COSTS
prompt ==============================
prompt
create table PN_PERSON_COSTS
(
  PERSONID           NUMBER not null,
  RATETABLE          NUMBER not null,
  SPACEID            NUMBER,
  COSTPERUSE         NUMBER(20),
  OVERTIMERATE       NUMBER(20),
  OVERTIMERATEFORMAT NUMBER(20),
  RATESFROM          DATE,
  RATESTO            DATE,
  STANDARDRATE       NUMBER(20),
  STANDARDRATEFORMAT NUMBER(20)
)
;
alter table PN_PERSON_COSTS
  add constraint PN_PERSON_COSTS_PK primary key (PERSONID, RATETABLE);

prompt
prompt Creating table PN_PERSON_HAS_ALTERNATE_EMAIL
prompt ============================================
prompt
create table PN_PERSON_HAS_ALTERNATE_EMAIL
(
  PERSON_ID NUMBER(20) not null,
  EMAIL     VARCHAR2(240) not null
)
;
alter table PN_PERSON_HAS_ALTERNATE_EMAIL
  add constraint PERSON_HAS_ALT_EMAIL_PK primary key (PERSON_ID, EMAIL);

prompt
prompt Creating table PN_PERSON_HAS_DISCIPLINE
prompt =======================================
prompt
create table PN_PERSON_HAS_DISCIPLINE
(
  PERSON_ID        NUMBER(20) not null,
  DISCIPLINE_CODE  NUMBER(4) not null,
  OTHER_DISCIPLINE VARCHAR2(30)
)
;
alter table PN_PERSON_HAS_DISCIPLINE
  add constraint PERSON_DISCIPLINE_PK primary key (PERSON_ID, DISCIPLINE_CODE);
alter table PN_PERSON_HAS_DISCIPLINE
  add constraint PERSON_DISCIPLINE_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_PERSON_HAS_DISCIPLINE
  add constraint PERSON_DISCIPLINE_FK2 foreign key (DISCIPLINE_CODE)
  references PN_DISCIPLINE_LOOKUP (DISCIPLINE_CODE);
create index PERSON_DISCIPLINE_IDX1 on PN_PERSON_HAS_DISCIPLINE (DISCIPLINE_CODE) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PERSON_HAS_LICENSE
prompt ====================================
prompt
create table PN_PERSON_HAS_LICENSE
(
  PERSON_ID  NUMBER(20) not null,
  LICENSE_ID NUMBER(20) not null
)
;
alter table PN_PERSON_HAS_LICENSE
  add constraint PN_PERSON_HAS_LICENSE_PK primary key (PERSON_ID);
alter table PN_PERSON_HAS_LICENSE
  add constraint PN_PERSON_HAS_LICENSE_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID) on delete set null;
alter table PN_PERSON_HAS_LICENSE
  add constraint PN_PERSON_HAS_LICENSE_FK2 foreign key (LICENSE_ID)
  references PN_LICENSE (LICENSE_ID) on delete set null;

prompt
prompt Creating table PN_PROF_CERT_LOOKUP
prompt ==================================
prompt
create table PN_PROF_CERT_LOOKUP
(
  PROF_CERT_CODE        NUMBER(4) not null,
  PROF_CERT_NAME        VARCHAR2(30) not null,
  PROF_CERT_DESCRIPTION VARCHAR2(40)
)
;
alter table PN_PROF_CERT_LOOKUP
  add constraint PROF_CERT_LOOKUP_PK primary key (PROF_CERT_CODE);

prompt
prompt Creating table PN_PERSON_HAS_PROF_CERT
prompt ======================================
prompt
create table PN_PERSON_HAS_PROF_CERT
(
  PERSON_ID       NUMBER(20) not null,
  PROF_CERT_CODE  NUMBER(4) not null,
  OTHER_PROF_CERT VARCHAR2(30)
)
;
alter table PN_PERSON_HAS_PROF_CERT
  add constraint PERSON_PROF_CERT_PK primary key (PERSON_ID, PROF_CERT_CODE);
alter table PN_PERSON_HAS_PROF_CERT
  add constraint PERSON_PROF_CERT_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_PERSON_HAS_PROF_CERT
  add constraint PERSON_PROF_CERT_FK2 foreign key (PROF_CERT_CODE)
  references PN_PROF_CERT_LOOKUP (PROF_CERT_CODE);
create index PERSON_PROF_CERT_IDX1 on PN_PERSON_HAS_PROF_CERT (PROF_CERT_CODE) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SKILL_CATEGORY
prompt ================================
prompt
create table PN_SKILL_CATEGORY
(
  SKILL_CATEGORY_ID   NUMBER(20) not null,
  SKILL_CATEGORY_NAME VARCHAR2(80) not null,
  SKILL_CATEGORY_DESC VARCHAR2(500),
  RECORD_STATUS       VARCHAR2(1) not null
)
;
alter table PN_SKILL_CATEGORY
  add constraint SKILL_CATEGORY_PK primary key (SKILL_CATEGORY_ID);
alter table PN_SKILL_CATEGORY
  add constraint RECORD_STATUS_VALID43
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_SKILL
prompt =======================
prompt
create table PN_SKILL
(
  SKILL_ID          NUMBER(20) not null,
  SKILL_NAME        VARCHAR2(80) not null,
  SKILL_CATEGORY_ID NUMBER(20) not null,
  SKILL_DESC        VARCHAR2(500),
  PARENT_SKILL_ID   NUMBER(20),
  RECORD_STATUS     VARCHAR2(1) not null
)
;
alter table PN_SKILL
  add constraint SKILL_PK primary key (SKILL_ID);
alter table PN_SKILL
  add constraint SKILL_FK1 foreign key (SKILL_CATEGORY_ID)
  references PN_SKILL_CATEGORY (SKILL_CATEGORY_ID);
alter table PN_SKILL
  add constraint RECORD_STATUS_VALID44
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_PERSON_HAS_SKILL
prompt ==================================
prompt
create table PN_PERSON_HAS_SKILL
(
  PERSON_ID         NUMBER(20) not null,
  SKILL_ID          NUMBER(20) not null,
  PROFICIENCY_CODE  NUMBER(20) not null,
  MONTHS_EXPERIENCE NUMBER(8),
  MOST_RECENT_USE   DATE
)
;
alter table PN_PERSON_HAS_SKILL
  add constraint PERSON_HAS_SKILL_PK primary key (PERSON_ID, SKILL_ID);
alter table PN_PERSON_HAS_SKILL
  add constraint PERSON_HAS_SKILL_FK1 foreign key (SKILL_ID)
  references PN_SKILL (SKILL_ID);
create index PERSON_HAS_SKILL_IDX1 on PN_PERSON_HAS_SKILL (SKILL_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_STATE_LOOKUP
prompt ==============================
prompt
create table PN_STATE_LOOKUP
(
  STATE_CODE   VARCHAR2(2) not null,
  STATE_NAME   VARCHAR2(80) not null,
  COUNTRY_CODE VARCHAR2(2)
)
;
alter table PN_STATE_LOOKUP
  add constraint STATE_LOOKUP_PK primary key (STATE_CODE);
alter table PN_STATE_LOOKUP
  add constraint STATE_LOOKUP_FK1 foreign key (COUNTRY_CODE)
  references PN_COUNTRY_LOOKUP (COUNTRY_CODE);
create index STATE_LOOKUP_IDX1 on PN_STATE_LOOKUP (COUNTRY_CODE) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PERSON_HAS_STATE_REG
prompt ======================================
prompt
create table PN_PERSON_HAS_STATE_REG
(
  PERSON_ID       NUMBER(20) not null,
  STATE_CODE      VARCHAR2(2) not null,
  OTHER_REG_STATE VARCHAR2(20)
)
;
alter table PN_PERSON_HAS_STATE_REG
  add constraint PERSON_STATE_REG_PK primary key (PERSON_ID, STATE_CODE);
alter table PN_PERSON_HAS_STATE_REG
  add constraint PERSON_STATE_REG_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_PERSON_HAS_STATE_REG
  add constraint PERSON_STATE_REG_FK2 foreign key (STATE_CODE)
  references PN_STATE_LOOKUP (STATE_CODE);
create index PERSON_STATE_REG_IDX1 on PN_PERSON_HAS_STATE_REG (STATE_CODE) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PERSON_HAS_VOTE
prompt =================================
prompt
create table PN_PERSON_HAS_VOTE
(
  PERSON_ID NUMBER(20) not null,
  VOTE_ID   NUMBER(20) not null
)
;

prompt
prompt Creating table PN_PERSON_NOTIFICATION_ADDRESS
prompt =============================================
prompt
create table PN_PERSON_NOTIFICATION_ADDRESS
(
  PERSON_ID        NUMBER(20) not null,
  DELIVERY_TYPE_ID NUMBER(20) not null,
  DELIVERY_ADDRESS VARCHAR2(240) not null,
  IS_DEFAULT       NUMBER(1)
)
;
alter table PN_PERSON_NOTIFICATION_ADDRESS
  add constraint PERSON_NOTIFICATION_ADDRESS_PK primary key (PERSON_ID);
alter table PN_PERSON_NOTIFICATION_ADDRESS
  add constraint PERSON_NOTIFICATION_ADDR_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_PERSON_NOTIFICATION_ADDRESS
  add constraint PERSON_NOTIFICATION_ADDR_FK2 foreign key (DELIVERY_TYPE_ID)
  references PN_NOTIFICATION_DELIVERY_TYPE (DELIVERY_TYPE_ID);

prompt
prompt Creating table PN_PERSON_NOTIFICATION_PREFS
prompt ===========================================
prompt
create table PN_PERSON_NOTIFICATION_PREFS
(
  PERSON_ID   NUMBER(20) not null,
  DAILY_TIME  NUMBER,
  WEEKLY_DAY  NUMBER,
  WEEKLY_TIME NUMBER
)
;
alter table PN_PERSON_NOTIFICATION_PREFS
  add constraint PERSON_NOTIFICATION_PREFS_PK primary key (PERSON_ID);
alter table PN_PERSON_NOTIFICATION_PREFS
  add constraint PERSON_NOTIF_PREFS_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);

prompt
prompt Creating table PN_PERSON_SURVEY
prompt ===============================
prompt
create table PN_PERSON_SURVEY
(
  PERSON_ID            NUMBER(20) not null,
  SPAM_ALLOWED         VARCHAR2(1) not null,
  SPAM_METHOD          VARCHAR2(20),
  MODELVISTA_SOURCE    VARCHAR2(20) not null,
  PREVIOUS_BENTLEY_EXP VARCHAR2(1) not null,
  REFERRAL_PAGE        VARCHAR2(240) not null
)
;
alter table PN_PERSON_SURVEY
  add constraint PERSON_SURVEY_PK primary key (PERSON_ID);
alter table PN_PERSON_SURVEY
  add constraint PERSON_SURVEY_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_PERSON_SURVEY
  add constraint REFERRAL_PAGE_VALID
  check (referral_page in ('viecon','primavera','aecdirect'));
alter table PN_PERSON_SURVEY
  add constraint SPAM_METHOD_VALID
  check (spam_method in ('Any','Email','Postal Mail','Phone'));
alter table PN_PERSON_SURVEY
  add constraint Y_OR_N_VALID
  check (spam_allowed in ('Y','N'));
alter table PN_PERSON_SURVEY
  add constraint Y_OR_N_VALID2
  check (previous_bentley_exp in ('Y','N'));

prompt
prompt Creating table PN_SPAM_LOOKUP
prompt =============================
prompt
create table PN_SPAM_LOOKUP
(
  SPAM_TYPE_CODE NUMBER(4) not null,
  SPAM_TYPE      VARCHAR2(40) not null
)
;
alter table PN_SPAM_LOOKUP
  add constraint SPAM_LOOKUP_PK primary key (SPAM_TYPE_CODE);

prompt
prompt Creating table PN_PERSON_PICKS_SPAM
prompt ===================================
prompt
create table PN_PERSON_PICKS_SPAM
(
  PERSON_ID      NUMBER(20) not null,
  SPAM_TYPE_CODE NUMBER(4) not null
)
;
alter table PN_PERSON_PICKS_SPAM
  add constraint PERSON_SPAM_PK primary key (PERSON_ID, SPAM_TYPE_CODE);
alter table PN_PERSON_PICKS_SPAM
  add constraint PERSON_SPAM_FK1 foreign key (PERSON_ID)
  references PN_PERSON_SURVEY (PERSON_ID);
alter table PN_PERSON_PICKS_SPAM
  add constraint PERSON_SPAM_FK2 foreign key (SPAM_TYPE_CODE)
  references PN_SPAM_LOOKUP (SPAM_TYPE_CODE);
create index PERSON_SPAM_IDX1 on PN_PERSON_PICKS_SPAM (SPAM_TYPE_CODE) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PERSON_PROFILE
prompt ================================
prompt
create table PN_PERSON_PROFILE
(
  PERSON_ID            NUMBER(20) not null,
  PREFIX_NAME          VARCHAR2(80),
  MIDDLE_NAME          VARCHAR2(80),
  SECOND_LAST_NAME     VARCHAR2(80),
  SUFFIX_NAME          VARCHAR2(80),
  COMPANY_NAME         VARCHAR2(120),
  COMPANY_DIVISION     VARCHAR2(120),
  JOB_DESCRIPTION_CODE NUMBER(4),
  ADDRESS_ID           NUMBER(20) not null,
  LANGUAGE_CODE        VARCHAR2(80),
  TIMEZONE_CODE        VARCHAR2(60) not null,
  PERSONAL_SPACE_NAME  VARCHAR2(240) not null,
  VERIFICATION_CODE    VARCHAR2(80),
  ALTERNATE_EMAIL_1    VARCHAR2(240),
  ALTERNATE_EMAIL_2    VARCHAR2(240),
  ALTERNATE_EMAIL_3    VARCHAR2(240),
  LOCALE_CODE          VARCHAR2(20)
)
;
comment on column PN_PERSON_PROFILE.ALTERNATE_EMAIL_1
  is 'This should eventually be refactored into pn_person_has_alternate_email table.';
comment on column PN_PERSON_PROFILE.ALTERNATE_EMAIL_2
  is 'This should eventually be refactored into pn_person_has_alternate_email table.';
comment on column PN_PERSON_PROFILE.ALTERNATE_EMAIL_3
  is 'This should eventually be refactored into pn_person_has_alternate_email table.';
alter table PN_PERSON_PROFILE
  add constraint PN_PERSON_PROFILE_PK primary key (PERSON_ID);
alter table PN_PERSON_PROFILE
  add constraint PN_PERSON_PROFILE_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID) on delete set null;

prompt
prompt Creating table PN_PERSON_PROPERTIES
prompt ===================================
prompt
create table PN_PERSON_PROPERTIES
(
  SPACE_ID  NUMBER(20) not null,
  PERSON_ID NUMBER(20) not null,
  CONTEXT   VARCHAR2(256) not null,
  PROPERTY  VARCHAR2(256) not null,
  VALUE     VARCHAR2(256) not null
)
;
alter table PN_PERSON_PROPERTIES
  add constraint PERSON_PROPERTIES_PK primary key (SPACE_ID, PERSON_ID, CONTEXT, PROPERTY, VALUE);
alter table PN_PERSON_PROPERTIES
  add constraint PERSON_PROPERTIES_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
create index PERSON_PROPERTIES_IDX1 on PN_PERSON_PROPERTIES (PERSON_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PERSON_SKILL_COMMENT
prompt ======================================
prompt
create table PN_PERSON_SKILL_COMMENT
(
  PERSON_ID      NUMBER(20) not null,
  SKILL_ID       NUMBER(20) not null,
  COMMENT_ID     NUMBER(20) not null,
  DATE_ADDED     DATE,
  ADDED_BY       NUMBER(20) not null,
  PERSON_COMMENT VARCHAR2(4000),
  RECORD_STATUS  VARCHAR2(1) not null
)
;
alter table PN_PERSON_SKILL_COMMENT
  add constraint PERSON_SKILL_COMMENT_PK primary key (PERSON_ID, SKILL_ID, COMMENT_ID);
alter table PN_PERSON_SKILL_COMMENT
  add constraint PERSON_SKILL_COMMENT_FK1 foreign key (PERSON_ID, SKILL_ID)
  references PN_PERSON_HAS_SKILL (PERSON_ID, SKILL_ID);
alter table PN_PERSON_SKILL_COMMENT
  add constraint PERSON_SKILL_COMMENT_FK2 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_PERSON_SKILL_COMMENT
  add constraint PERSON_SKILL_COMMENT_FK3 foreign key (ADDED_BY)
  references PN_PERSON (PERSON_ID);
alter table PN_PERSON_SKILL_COMMENT
  add constraint RECORD_STATUS_VALID45
  check (record_status in ('A','P','D','H'));
create index PERSON_SKILL_COMMENT_IDX1 on PN_PERSON_SKILL_COMMENT (COMMENT_ID, PERSON_ID, SKILL_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PERSON_USED_SKILL
prompt ===================================
prompt
create table PN_PERSON_USED_SKILL
(
  SPACE_ID    NUMBER(20) not null,
  PERSON_ID   NUMBER(20) not null,
  SKILL_ID    NUMBER(20) not null,
  START_DATE  DATE,
  END_DATE    DATE,
  DESCRIPTION VARCHAR2(500)
)
;
alter table PN_PERSON_USED_SKILL
  add constraint PERSON_USED_SKILL_PK primary key (SPACE_ID, PERSON_ID, SKILL_ID);
alter table PN_PERSON_USED_SKILL
  add constraint PERSON_USED_SKILL_FK1 foreign key (PERSON_ID, SKILL_ID)
  references PN_PERSON_HAS_SKILL (PERSON_ID, SKILL_ID);
create index PERSON_USED_SKILL_IDX1 on PN_PERSON_USED_SKILL (PERSON_ID, SKILL_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PHASE_HAS_DELIVERABLE
prompt =======================================
prompt
create table PN_PHASE_HAS_DELIVERABLE
(
  PHASE_ID       NUMBER(20) not null,
  DELIVERABLE_ID NUMBER(20) not null
)
;
alter table PN_PHASE_HAS_DELIVERABLE
  add constraint PHASE_DELIVERABLE_PK primary key (PHASE_ID, DELIVERABLE_ID);
alter table PN_PHASE_HAS_DELIVERABLE
  add constraint PHASE_DELIV_FK1 foreign key (DELIVERABLE_ID)
  references PN_DELIVERABLE (DELIVERABLE_ID);
alter table PN_PHASE_HAS_DELIVERABLE
  add constraint PHASE_DELIV_FK2 foreign key (PHASE_ID)
  references PN_PHASE (PHASE_ID);
create unique index PHASE_DELIVERABLE_IDX1 on PN_PHASE_HAS_DELIVERABLE (DELIVERABLE_ID, PHASE_ID);

prompt
prompt Creating table PN_TASK
prompt ======================
prompt
create table PN_TASK
(
  TASK_ID                        NUMBER(20) not null,
  TASK_NAME                      VARCHAR2(255) not null,
  TASK_DESC                      VARCHAR2(4000),
  TASK_TYPE                      VARCHAR2(80),
  DURATION                       NUMBER,
  WORK                           NUMBER,
  WORK_UNITS                     NUMBER(20),
  WORK_COMPLETE                  NUMBER,
  DATE_START                     DATE,
  WORK_COMPLETE_UNITS            NUMBER(20),
  DATE_FINISH                    DATE,
  ACTUAL_START                   DATE,
  ACTUAL_FINISH                  DATE,
  PRIORITY                       NUMBER(6),
  PERCENT_COMPLETE               NUMBER(6),
  DATE_CREATED                   DATE,
  DATE_MODIFIED                  DATE,
  MODIFIED_BY                    NUMBER(20),
  DURATION_UNITS                 NUMBER(20),
  PARENT_TASK_ID                 NUMBER(20),
  RECORD_STATUS                  VARCHAR2(1) not null,
  CRITICAL_PATH                  NUMBER,
  SEQ                            NUMBER,
  IGNORE_TIMES_FOR_DATES         NUMBER(1),
  IS_MILESTONE                   NUMBER(1),
  EARLY_START                    DATE,
  EARLY_FINISH                   DATE,
  LATE_START                     DATE,
  LATE_FINISH                    DATE,
  WORK_PERCENT_COMPLETE          NUMBER,
  CALCULATION_TYPE_ID            NUMBER(20) default 10,
  UNALLOCATED_WORK_COMPLETE      NUMBER default 0,
  UNALLOCATED_WORK_COMPLETE_UNIT NUMBER(20) default 4,
  CONSTRAINT_TYPE                VARCHAR2(10),
  CONSTRAINT_DATE                DATE,
  DEADLINE                       DATE,
  WORK_MS                        NUMBER,
  WORK_COMPLETE_MS               NUMBER,
  UNASSIGNED_WORK                NUMBER default 0,
  UNASSIGNED_WORK_UNITS          NUMBER default 4
)
;
alter table PN_TASK
  add constraint TASK_PK primary key (TASK_ID);
alter table PN_TASK
  add constraint TASK_OBJ_FK foreign key (TASK_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_TASK
  add constraint RECORD_STATUS_VALID49
  check (record_status in ('A','P','D','H'));
create index TASK_IDX1 on PN_TASK (RECORD_STATUS) TABLESPACE INDEX01;
create index TASK_IDX2 on PN_TASK (TASK_ID, PARENT_TASK_ID) TABLESPACE INDEX01;
create index TASK_IDX3 on PN_TASK (PARENT_TASK_ID, TASK_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PHASE_HAS_TASK
prompt ================================
prompt
create table PN_PHASE_HAS_TASK
(
  PHASE_ID NUMBER(20) not null,
  TASK_ID  NUMBER(20) not null
)
;
alter table PN_PHASE_HAS_TASK
  add constraint PHASE_HAS_TASK_PK primary key (PHASE_ID, TASK_ID);
alter table PN_PHASE_HAS_TASK
  add constraint PHASE_HAS_TASK_TO_PHASE_FK foreign key (PHASE_ID)
  references PN_PHASE (PHASE_ID);
alter table PN_PHASE_HAS_TASK
  add constraint PHASE_HAS_TASK_TO_TASK_FK foreign key (TASK_ID)
  references PN_TASK (TASK_ID);
create index PHASE_HAS_TASK_IDX1 on PN_PHASE_HAS_TASK (TASK_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_PIVOT
prompt =======================
prompt
create table PN_PIVOT
(
  X NUMBER
)
;
create unique index IDX_PIVOT on PN_PIVOT (X);

prompt
prompt Creating table PN_PLAN
prompt ======================
prompt
create table PN_PLAN
(
  PLAN_ID                      NUMBER(20) not null,
  PLAN_NAME                    VARCHAR2(250) not null,
  PLAN_DESC                    VARCHAR2(500),
  DATE_START                   DATE,
  DATE_END                     DATE,
  AUTOCALCULATE_TASK_ENDPOINTS NUMBER,
  OVERALLOCATION_WARNING       NUMBER,
  DEFAULT_CALENDAR_ID          NUMBER,
  TIMEZONE_ID                  VARCHAR2(1000),
  BASELINE_START               DATE,
  BASELINE_END                 DATE,
  MODIFIED_DATE                DATE,
  MODIFIED_BY                  NUMBER,
  BASELINE_ID                  NUMBER,
  DEFAULT_TASK_CALC_TYPE_ID    NUMBER(20) default 10 not null,
  EARLIEST_START_DATE          DATE,
  EARLIEST_FINISH_DATE         DATE,
  LATEST_START_DATE            DATE,
  LATEST_FINISH_DATE           DATE,
  CONSTRAINT_TYPE_ID           VARCHAR2(10) default 50,
  CONSTRAINT_DATE              DATE
)
;
alter table PN_PLAN
  add constraint PLAN_PK primary key (PLAN_ID);
alter table PN_PLAN
  add constraint PLAN_OBJ_FK foreign key (PLAN_ID)
  references PN_OBJECT (OBJECT_ID);

prompt
prompt Creating table PN_PLAN_HAS_TASK
prompt ===============================
prompt
create table PN_PLAN_HAS_TASK
(
  PLAN_ID NUMBER(20) not null,
  TASK_ID NUMBER(20) not null
)
;
alter table PN_PLAN_HAS_TASK
  add constraint PLAN_HAS_TASK_PK primary key (PLAN_ID, TASK_ID);
alter table PN_PLAN_HAS_TASK
  add constraint PLAN_HAS_TASK_FK1 foreign key (PLAN_ID)
  references PN_PLAN (PLAN_ID);
alter table PN_PLAN_HAS_TASK
  add constraint PLAN_HAS_TASK_FK2 foreign key (TASK_ID)
  references PN_TASK (TASK_ID);
create unique index PLAN_HAS_TASK_IDX1 on PN_PLAN_HAS_TASK (TASK_ID, PLAN_ID);

prompt
prompt Creating table PN_PLAN_VERSION
prompt ==============================
prompt
create table PN_PLAN_VERSION
(
  PLAN_ID                      NUMBER(20) not null,
  PLAN_VERSION_ID              NUMBER(20) not null,
  PLAN_NAME                    VARCHAR2(250) not null,
  PLAN_DESC                    VARCHAR2(500),
  DATE_START                   DATE,
  DATE_END                     DATE,
  AUTOCALCULATE_TASK_ENDPOINTS NUMBER,
  OVERALLOCATION_WARNING       NUMBER,
  DEFAULT_CALENDAR_ID          NUMBER,
  TIMEZONE_ID                  VARCHAR2(1000),
  BASELINE_START               DATE,
  BASELINE_END                 DATE,
  MODIFIED_DATE                DATE,
  MODIFIED_BY                  NUMBER,
  BASELINE_ID                  NUMBER,
  DEFAULT_TASK_CALC_TYPE_ID    NUMBER(20) not null,
  EARLIEST_START_DATE          DATE,
  EARLIEST_FINISH_DATE         DATE,
  LATEST_START_DATE            DATE,
  LATEST_FINISH_DATE           DATE,
  CONSTRAINT_TYPE_ID           VARCHAR2(10) default 50,
  CONSTRAINT_DATE              DATE
)
;
alter table PN_PLAN_VERSION
  add constraint PLAN_VERSION_PK primary key (PLAN_ID, PLAN_VERSION_ID);

prompt
prompt Creating table PN_PORTFOLIO
prompt ===========================
prompt
create table PN_PORTFOLIO
(
  PORTFOLIO_ID   NUMBER(20) not null,
  PORTFOLIO_NAME VARCHAR2(80) not null,
  PORTFOLIO_DESC VARCHAR2(500),
  PORTFOLIO_TYPE VARCHAR2(40),
  CONTENT_TYPE   VARCHAR2(40),
  RECORD_STATUS  VARCHAR2(1)
)
;
alter table PN_PORTFOLIO
  add constraint PORTFOLIO_PK primary key (PORTFOLIO_ID);
alter table PN_PORTFOLIO
  add constraint PORTFOLIO_OBJ_FK foreign key (PORTFOLIO_ID)
  references PN_OBJECT (OBJECT_ID);

prompt
prompt Creating table PN_PORTFOLIO_HAS_CONFIGURATION
prompt =============================================
prompt
create table PN_PORTFOLIO_HAS_CONFIGURATION
(
  PORTFOLIO_ID     NUMBER(20) not null,
  CONFIGURATION_ID NUMBER(20) not null,
  IS_PRIVATE       NUMBER(1) default 0 not null
)
;
alter table PN_PORTFOLIO_HAS_CONFIGURATION
  add constraint PORTFOLIO_HAS_CONFIGURATION_PK primary key (PORTFOLIO_ID, CONFIGURATION_ID);
alter table PN_PORTFOLIO_HAS_CONFIGURATION
  add constraint PORTFOLIO_CONFIGURATION_FK1 foreign key (PORTFOLIO_ID)
  references PN_PORTFOLIO (PORTFOLIO_ID);
alter table PN_PORTFOLIO_HAS_CONFIGURATION
  add constraint PORTFOLIO_CONFIGURATION_FK2 foreign key (CONFIGURATION_ID)
  references PN_CONFIGURATION_SPACE (CONFIGURATION_ID);

prompt
prompt Creating table PN_PORTFOLIO_HAS_SPACE
prompt =====================================
prompt
create table PN_PORTFOLIO_HAS_SPACE
(
  PORTFOLIO_ID NUMBER(20) not null,
  SPACE_ID     NUMBER(20) not null,
  IS_PRIVATE   NUMBER(1)
)
;
alter table PN_PORTFOLIO_HAS_SPACE
  add constraint PORTFOLIO_SPACE_PK primary key (PORTFOLIO_ID, SPACE_ID);
alter table PN_PORTFOLIO_HAS_SPACE
  add constraint PORTFOLIO_SPACE_FK1 foreign key (PORTFOLIO_ID)
  references PN_PORTFOLIO (PORTFOLIO_ID);
create index PORTFOLIO_SPACE_IDX1 on PN_PORTFOLIO_HAS_SPACE (SPACE_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_POST_BODY_CLOB
prompt ================================
prompt
create table PN_POST_BODY_CLOB
(
  OBJECT_ID  NUMBER(20) not null,
  CLOB_FIELD CLOB
)
;
alter table PN_POST_BODY_CLOB
  add constraint POST_BODY_CLOB_PK primary key (OBJECT_ID);

prompt
prompt Creating table PN_POST
prompt ======================
prompt
create table PN_POST
(
  POST_ID             NUMBER(20) not null,
  DISCUSSION_GROUP_ID NUMBER(20) not null,
  SUBJECT             VARCHAR2(256) not null,
  PERSON_ID           NUMBER(20) not null,
  DATE_POSTED         DATE not null,
  PARENT_ID           NUMBER(20),
  URGENCY_ID          NUMBER(20) not null,
  RECORD_STATUS       VARCHAR2(1) not null,
  POST_BODY_ID        NUMBER(20)
)
;
alter table PN_POST
  add constraint POST_PK primary key (POST_ID, DISCUSSION_GROUP_ID);
alter table PN_POST
  add constraint POST_CLOB_FK foreign key (POST_BODY_ID)
  references PN_POST_BODY_CLOB (OBJECT_ID);
alter table PN_POST
  add constraint POST_FK1 foreign key (DISCUSSION_GROUP_ID)
  references PN_DISCUSSION_GROUP (DISCUSSION_GROUP_ID);
alter table PN_POST
  add constraint POST_FK2 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_POST
  add constraint POST_OBJ_FK foreign key (POST_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_POST
  add constraint RECORD_STATUS_VALID50
  check (record_status in ('A','P','D','H'));
create unique index POST_IDX1 on PN_POST (DISCUSSION_GROUP_ID, POST_ID);
create index POST_IDX2 on PN_POST (PERSON_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_POST_ACTION_LOOKUP
prompt ====================================
prompt
create table PN_POST_ACTION_LOOKUP
(
  ACTION        VARCHAR2(80) not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_POST_ACTION_LOOKUP
  add constraint POST_ACTION_LOOKUP_PK primary key (ACTION);
alter table PN_POST_ACTION_LOOKUP
  add constraint RECORD_STATUS_VALID90
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_POST_HISTORY
prompt ==============================
prompt
create table PN_POST_HISTORY
(
  POST_ID             NUMBER(20) not null,
  DISCUSSION_GROUP_ID NUMBER(20) not null,
  POST_HISTORY_ID     NUMBER(20) not null,
  ACTION_BY_ID        NUMBER(20),
  ACTION              VARCHAR2(80),
  ACTION_COMMENT      VARCHAR2(255),
  ACTION_DATE         DATE,
  ACTION_NAME         VARCHAR2(80)
)
;
alter table PN_POST_HISTORY
  add constraint POST_HISTORY_PK primary key (POST_ID, DISCUSSION_GROUP_ID, POST_HISTORY_ID);
alter table PN_POST_HISTORY
  add constraint POST_HISTORY_FK1 foreign key (POST_ID, DISCUSSION_GROUP_ID)
  references PN_POST (POST_ID, DISCUSSION_GROUP_ID);
alter table PN_POST_HISTORY
  add constraint POST_HISTORY_FK2 foreign key (ACTION_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_POST_HISTORY
  add constraint POST_HISTORY_FK3 foreign key (ACTION)
  references PN_POST_ACTION_LOOKUP (ACTION);
create unique index POST_HISTORY_IDX1 on PN_POST_HISTORY (POST_HISTORY_ID, POST_ID, DISCUSSION_GROUP_ID);

prompt
prompt Creating table PN_POST_READER
prompt =============================
prompt
create table PN_POST_READER
(
  PERSON_ID           NUMBER(20) not null,
  POST_ID             NUMBER(20) not null,
  DISCUSSION_GROUP_ID NUMBER(20) not null,
  DATE_READ           DATE not null
)
;
alter table PN_POST_READER
  add constraint POST_READER_PK primary key (PERSON_ID, POST_ID, DISCUSSION_GROUP_ID);
alter table PN_POST_READER
  add constraint POST_READER_FK1 foreign key (POST_ID, DISCUSSION_GROUP_ID)
  references PN_POST (POST_ID, DISCUSSION_GROUP_ID);
alter table PN_POST_READER
  add constraint POST_READER_FK2 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
create unique index POST_READER_IDX1 on PN_POST_READER (POST_ID, DISCUSSION_GROUP_ID, PERSON_ID);

prompt
prompt Creating table PN_PROJECT_SPACE_META_PROP
prompt =========================================
prompt
create table PN_PROJECT_SPACE_META_PROP
(
  PROPERTY_ID   NUMBER(20) not null,
  PROPERTY_NAME VARCHAR2(1000),
  PROPERTY_TYPE NUMBER
)
;
comment on table PN_PROJECT_SPACE_META_PROP
  is 'Definitions of the additional project space properties.';
alter table PN_PROJECT_SPACE_META_PROP
  add constraint PN_PROJECT_SPACE_META_PROP_PK primary key (PROPERTY_ID);

prompt
prompt Creating table PN_PROJECT_SPACE_META_VALUE
prompt ==========================================
prompt
create table PN_PROJECT_SPACE_META_VALUE
(
  PROJECT_ID     NUMBER(20) not null,
  PROPERTY_ID    NUMBER(20) not null,
  PROPERTY_VALUE VARCHAR2(1000)
)
;
comment on table PN_PROJECT_SPACE_META_VALUE
  is 'Values of the additional project space properties.';
alter table PN_PROJECT_SPACE_META_VALUE
  add constraint PN_PROJECT_SPACE_META_V_PK primary key (PROJECT_ID, PROPERTY_ID);
alter table PN_PROJECT_SPACE_META_VALUE
  add constraint PN_PROJECT_SPACE_META_V_FK1 foreign key (PROJECT_ID)
  references PN_PROJECT_SPACE (PROJECT_ID);
alter table PN_PROJECT_SPACE_META_VALUE
  add constraint PN_PROJECT_SPACE_META_V_FK2 foreign key (PROPERTY_ID)
  references PN_PROJECT_SPACE_META_PROP (PROPERTY_ID);

prompt
prompt Creating table PN_PROPERTY
prompt ==========================
prompt
create table PN_PROPERTY
(
  CONTEXT_ID               NUMBER(20) not null,
  LANGUAGE                 VARCHAR2(2) not null,
  PROPERTY_TYPE            VARCHAR2(40) not null,
  PROPERTY                 VARCHAR2(500) not null,
  PROPERTY_VALUE           VARCHAR2(4000),
  RECORD_STATUS            VARCHAR2(1),
  IS_SYSTEM_PROPERTY       NUMBER(1) default 0 not null,
  IS_TRANSLATABLE_PROPERTY NUMBER(1) not null,
  PROPERTY_VALUE_CLOB      CLOB
)
;
comment on table PN_PROPERTY
  is 'For brand name terminology lookups for ';
alter table PN_PROPERTY
  add constraint PN_PROPERTY_PK primary key (CONTEXT_ID, LANGUAGE, PROPERTY);
alter table PN_PROPERTY
  add constraint PN_PROPERTY_FK2 foreign key (LANGUAGE)
  references PN_LANGUAGE (LANGUAGE_CODE) on delete set null;

prompt
prompt Creating table PN_PROPERTY_CATEGORY
prompt ===================================
prompt
create table PN_PROPERTY_CATEGORY
(
  CATEGORY_ID NUMBER(20) not null,
  NAME        VARCHAR2(80) not null,
  DESCRIPTION VARCHAR2(500)
)
;
alter table PN_PROPERTY_CATEGORY
  add constraint PN_PROPERTY_CATEGORY_PK primary key (CATEGORY_ID);

prompt
prompt Creating table PN_PROPERTY_CHANGE
prompt =================================
prompt
create table PN_PROPERTY_CHANGE
(
  CONTEXT_ID            NUMBER(20) not null,
  LANGUAGE              VARCHAR2(2) not null,
  LAST_UPDATED_DATETIME DATE not null
)
;
alter table PN_PROPERTY_CHANGE
  add constraint PN_PROPERTY_CHANGE_PK primary key (CONTEXT_ID, LANGUAGE);

prompt
prompt Creating table PN_PROPERTY_SHEET_TYPE
prompt =====================================
prompt
create table PN_PROPERTY_SHEET_TYPE
(
  PROPERTY_SHEET_TYPE   NUMBER(5) not null,
  PROPERTY_SHEET_NAME   VARCHAR2(80) not null,
  PROPERTY_SHEET_DESC   VARCHAR2(255),
  PROPERTIES_TABLE_NAME VARCHAR2(80)
)
;
alter table PN_PROPERTY_SHEET_TYPE
  add constraint PROPERTY_SHEET_TYPE_PK primary key (PROPERTY_SHEET_TYPE);

prompt
prompt Creating table PN_PROPERTY_SHEET
prompt ================================
prompt
create table PN_PROPERTY_SHEET
(
  PROPERTY_SHEET_ID   NUMBER(20) not null,
  PROPERTY_SHEET_TYPE NUMBER(5),
  PROPERTY_GROUP_ID   NUMBER(20)
)
;
alter table PN_PROPERTY_SHEET
  add constraint PROPERTY_SHEET_PK primary key (PROPERTY_SHEET_ID);
alter table PN_PROPERTY_SHEET
  add constraint PROPERTY_SHEET_FK2 foreign key (PROPERTY_SHEET_TYPE)
  references PN_PROPERTY_SHEET_TYPE (PROPERTY_SHEET_TYPE);

prompt
prompt Creating table PN_PROP_CATEGORY_HAS_PROPERTY
prompt ============================================
prompt
create table PN_PROP_CATEGORY_HAS_PROPERTY
(
  CATEGORY_ID NUMBER(20) not null,
  PROPERTY    VARCHAR2(500) not null
)
;
alter table PN_PROP_CATEGORY_HAS_PROPERTY
  add constraint PN_PROP_CATEGORY_HAS_PROP_PK primary key (CATEGORY_ID, PROPERTY);

prompt
prompt Creating table PN_REPORT_SEQUENCE
prompt =================================
prompt
create table PN_REPORT_SEQUENCE
(
  REPORT_TYPE VARCHAR2(10) not null,
  SEQUENCE    NUMBER
)
;
alter table PN_REPORT_SEQUENCE
  add constraint REPORT_SEQUENCE_PK primary key (REPORT_TYPE);

prompt
prompt Creating table PN_RESOURCE_TYPE
prompt ===============================
prompt
create table PN_RESOURCE_TYPE
(
  ID          NUMBER(20) not null,
  NAME        VARCHAR2(300) not null,
  DESCRIPTION VARCHAR2(1000)
)
;
alter table PN_RESOURCE_TYPE
  add primary key (ID);

prompt
prompt Creating table PN_RESOURCE
prompt ==========================
prompt
create table PN_RESOURCE
(
  ID   NUMBER(20) not null,
  TYPE NUMBER(20) not null
)
;
alter table PN_RESOURCE
  add primary key (ID);
alter table PN_RESOURCE
  add foreign key (TYPE)
  references PN_RESOURCE_TYPE (ID);

prompt
prompt Creating table PN_RESOURCE_TYPE_FIELD
prompt =====================================
prompt
create table PN_RESOURCE_TYPE_FIELD
(
  ID            NUMBER(20) not null,
  NAME          VARCHAR2(300) not null,
  DESCRIPTION   VARCHAR2(1000) not null,
  REQUIRED      NUMBER(1) not null,
  SHOW          NUMBER(1) not null,
  RESOURCE_TYPE NUMBER(20) not null
)
;
alter table PN_RESOURCE_TYPE_FIELD
  add primary key (ID);
alter table PN_RESOURCE_TYPE_FIELD
  add foreign key (RESOURCE_TYPE)
  references PN_RESOURCE_TYPE (ID);

prompt
prompt Creating table PN_RESOURCE_FIELD_VALUE
prompt ======================================
prompt
create table PN_RESOURCE_FIELD_VALUE
(
  ID    NUMBER(20) not null,
  VALUE VARCHAR2(500) not null,
  FIELD NUMBER(20) not null,
  RES   NUMBER(20) not null
)
;
alter table PN_RESOURCE_FIELD_VALUE
  add primary key (ID);
alter table PN_RESOURCE_FIELD_VALUE
  add foreign key (FIELD)
  references PN_RESOURCE_TYPE_FIELD (ID);
alter table PN_RESOURCE_FIELD_VALUE
  add foreign key (RES)
  references PN_RESOURCE (ID);

prompt
prompt Creating table PN_RESOURCE_LIST
prompt ===============================
prompt
create table PN_RESOURCE_LIST
(
  ID           INTEGER not null,
  NAME         VARCHAR2(80),
  CREATED_BY   NUMBER,
  STATUS       VARCHAR2(1),
  DATE_CREATED DATE
)
;
alter table PN_RESOURCE_LIST
  add constraint PN_RESOURCE_LIST_PK primary key (ID);

prompt
prompt Creating table PN_RESOURCE_LIST_HAS_PERSONS
prompt ===========================================
prompt
create table PN_RESOURCE_LIST_HAS_PERSONS
(
  RESOURCE_LIST_ID NUMBER(20) not null,
  PERSON_ID        NUMBER(20) not null
)
;
alter table PN_RESOURCE_LIST_HAS_PERSONS
  add constraint PN_RESOURCE_LIST_HAS_PERS_PK primary key (RESOURCE_LIST_ID, PERSON_ID);
alter table PN_RESOURCE_LIST_HAS_PERSONS
  add constraint PN_RESOURCE_LIST_HAS_PERS_FK1 foreign key (RESOURCE_LIST_ID)
  references PN_RESOURCE_LIST (ID);
alter table PN_RESOURCE_LIST_HAS_PERSONS
  add constraint PN_RESOURCE_LIST_HAS_PERS_FK2 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);

prompt
prompt Creating table PN_RESOURCE_TYPE_FIELD_BOOL
prompt ==========================================
prompt
create table PN_RESOURCE_TYPE_FIELD_BOOL
(
  ID NUMBER(20) not null
)
;
alter table PN_RESOURCE_TYPE_FIELD_BOOL
  add primary key (ID);
alter table PN_RESOURCE_TYPE_FIELD_BOOL
  add foreign key (ID)
  references PN_RESOURCE_TYPE_FIELD (ID);

prompt
prompt Creating table PN_RESOURCE_TYPE_FIELD_COMP
prompt ==========================================
prompt
create table PN_RESOURCE_TYPE_FIELD_COMP
(
  ID           NUMBER(20) not null,
  FIRST_FIELD  NUMBER(20) not null,
  SECOND_FIELD NUMBER(20) not null,
  SEPARATOR    VARCHAR2(10)
)
;
alter table PN_RESOURCE_TYPE_FIELD_COMP
  add primary key (ID);
alter table PN_RESOURCE_TYPE_FIELD_COMP
  add foreign key (ID)
  references PN_RESOURCE_TYPE_FIELD (ID);
alter table PN_RESOURCE_TYPE_FIELD_COMP
  add foreign key (FIRST_FIELD)
  references PN_RESOURCE_TYPE_FIELD (ID);
alter table PN_RESOURCE_TYPE_FIELD_COMP
  add foreign key (SECOND_FIELD)
  references PN_RESOURCE_TYPE_FIELD (ID);

prompt
prompt Creating table PN_RESOURCE_TYPE_FIELD_CURR
prompt ==========================================
prompt
create table PN_RESOURCE_TYPE_FIELD_CURR
(
  ID            NUMBER(20) not null,
  DIGITS_TOTALS NUMBER(10) not null,
  DECIMALS      NUMBER(5) not null
)
;
alter table PN_RESOURCE_TYPE_FIELD_CURR
  add primary key (ID);
alter table PN_RESOURCE_TYPE_FIELD_CURR
  add foreign key (ID)
  references PN_RESOURCE_TYPE_FIELD (ID);

prompt
prompt Creating table PN_RESOURCE_TYPE_FIELD_DATE
prompt ==========================================
prompt
create table PN_RESOURCE_TYPE_FIELD_DATE
(
  ID     NUMBER(20) not null,
  FORMAT VARCHAR2(20) not null
)
;
alter table PN_RESOURCE_TYPE_FIELD_DATE
  add primary key (ID);
alter table PN_RESOURCE_TYPE_FIELD_DATE
  add foreign key (ID)
  references PN_RESOURCE_TYPE_FIELD (ID);

prompt
prompt Creating table PN_RESOURCE_TYPE_FIELD_NUM
prompt =========================================
prompt
create table PN_RESOURCE_TYPE_FIELD_NUM
(
  ID            NUMBER(20) not null,
  DIGITS_TOTALS NUMBER(10) not null,
  DECIMALS      NUMBER(5) not null
)
;
alter table PN_RESOURCE_TYPE_FIELD_NUM
  add primary key (ID);
alter table PN_RESOURCE_TYPE_FIELD_NUM
  add foreign key (ID)
  references PN_RESOURCE_TYPE_FIELD (ID);

prompt
prompt Creating table PN_RESOURCE_TYPE_FIELD_TEXT
prompt ==========================================
prompt
create table PN_RESOURCE_TYPE_FIELD_TEXT
(
  ID        NUMBER(20) not null,
  MAXLENGTH NUMBER(10) not null
)
;
alter table PN_RESOURCE_TYPE_FIELD_TEXT
  add primary key (ID);
alter table PN_RESOURCE_TYPE_FIELD_TEXT
  add foreign key (ID)
  references PN_RESOURCE_TYPE_FIELD (ID);

prompt
prompt Creating table PN_SCHEDULED_SUBSCRIPTION
prompt ========================================
prompt
create table PN_SCHEDULED_SUBSCRIPTION
(
  SCHEDULED_SUBSCRIPTION_ID NUMBER(20) not null,
  NAME                      VARCHAR2(80),
  DESCRIPTION               VARCHAR2(500),
  NOTIFICATION_TYPE_ID      NUMBER(20) not null,
  TARGET_OBJECT_ID          NUMBER(20),
  TARGET_OBJECT_TYPE        VARCHAR2(80),
  TARGET_OBJECT_XML         VARCHAR2(4000),
  INITIATOR_ID              NUMBER(20),
  EVENT_TIME                DATE,
  EVENT_TYPE                NUMBER(20),
  IS_QUEUED                 NUMBER,
  CREATE_DATE               DATE,
  CREATED_BY_ID             NUMBER(20) not null,
  MODIFIED_DATE             DATE,
  MODIFIED_BY_ID            NUMBER(20) not null,
  RECORD_STATUS             VARCHAR2(1),
  CRC                       DATE,
  DELIVERY_INTERVAL         NUMBER(4),
  DELIVERY_DATE             DATE,
  TARGET_OBJECT_CLOB_ID     NUMBER(20),
  BATCH_ID                  NUMBER(20),
  SUBSCRIBER_BATCH_ID       NUMBER(20),
  CUSTOM_MESSAGE_CLOB       CLOB,
  SPACE_ID                  NUMBER(20)
)
;
alter table PN_SCHEDULED_SUBSCRIPTION
  add constraint SCHEDULED_SUBSCRIPTION_PK primary key (SCHEDULED_SUBSCRIPTION_ID);
alter table PN_SCHEDULED_SUBSCRIPTION
  add constraint PN_SCHEDULED_SUBSCRIPTION_FK1 foreign key (NOTIFICATION_TYPE_ID)
  references PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID);
alter table PN_SCHEDULED_SUBSCRIPTION
  add constraint PN_SCHEDULED_SUBSCRIPTION_FK2 foreign key (INITIATOR_ID)
  references PN_PERSON (PERSON_ID);

prompt
prompt Creating table PN_SCHEDULE_NTWK_ID_TO_CHECK
prompt ===========================================
prompt
create global temporary table PN_SCHEDULE_NTWK_ID_TO_CHECK
(
  ID NUMBER
)
on commit preserve rows;

prompt
prompt Creating table PN_SCHEDULE_NTWK_ID_TO_RETURN
prompt ============================================
prompt
create global temporary table PN_SCHEDULE_NTWK_ID_TO_RETURN
(
  ID NUMBER
)
on commit preserve rows;

prompt
prompt Creating table PN_SHAREABLE
prompt ===========================
prompt
create table PN_SHAREABLE
(
  OBJECT_ID             NUMBER(20) not null,
  PERMISSION_TYPE       NUMBER not null,
  CONTAINER_ID          NUMBER(20),
  SPACE_ID              NUMBER(20),
  ALLOWABLE_ACTIONS     NUMBER(20) default 2,
  PROPAGATE_TO_CHILDREN NUMBER(15,5) default 0
)
;
alter table PN_SHAREABLE
  add constraint SHAREABLE_PK primary key (OBJECT_ID);
alter table PN_SHAREABLE
  add constraint SHAREABLE_FK foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID) on delete set null;
create index SHAREABLE_IDX1 on PN_SHAREABLE (SPACE_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SHAREABLE_PERMISSIONS
prompt =======================================
prompt
create table PN_SHAREABLE_PERMISSIONS
(
  OBJECT_ID           NUMBER(20) not null,
  PERMITTED_OBJECT_ID NUMBER(20) not null,
  SHARE_TYPE          VARCHAR2(10)
)
;
alter table PN_SHAREABLE_PERMISSIONS
  add constraint SHAREABLE_PERMISSIONS_PK primary key (OBJECT_ID, PERMITTED_OBJECT_ID);
alter table PN_SHAREABLE_PERMISSIONS
  add constraint SHAREABLE_PERMISSIONS_FK foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID) on delete set null;

prompt
prompt Creating table PN_SHARED
prompt ========================
prompt
create table PN_SHARED
(
  EXPORTED_OBJECT_ID  NUMBER(20) not null,
  IMPORT_CONTAINER_ID NUMBER(20) not null,
  IMPORT_SPACE_ID     NUMBER(20) not null,
  IMPORTED_OBJECT_ID  NUMBER(20),
  EXPORT_SPACE_ID     NUMBER(20),
  EXPORT_CONTAINER_ID NUMBER(20),
  READ_ONLY           NUMBER(1) default 0,
  TRAVERSED           NUMBER default 0
)
;
alter table PN_SHARED
  add constraint SHARED_PK primary key (EXPORTED_OBJECT_ID, IMPORT_CONTAINER_ID);
create index SHARED_IDX1 on PN_SHARED (EXPORTED_OBJECT_ID) TABLESPACE INDEX01;
create unique index SHARED_IDX2 on PN_SHARED (IMPORTED_OBJECT_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SKILL_HAS_SUBSKILL
prompt ====================================
prompt
create table PN_SKILL_HAS_SUBSKILL
(
  PARENT_SKILL_ID NUMBER(20) not null,
  CHILD_SKILL_ID  NUMBER not null
)
;
alter table PN_SKILL_HAS_SUBSKILL
  add constraint SKILL_HAS_SUBSKILL_PK primary key (PARENT_SKILL_ID, CHILD_SKILL_ID);
alter table PN_SKILL_HAS_SUBSKILL
  add constraint SKILL_SUBSKILL_FK1 foreign key (CHILD_SKILL_ID)
  references PN_SKILL (SKILL_ID);
alter table PN_SKILL_HAS_SUBSKILL
  add constraint SKILL_SUBSKILL_FK2 foreign key (PARENT_SKILL_ID)
  references PN_SKILL (SKILL_ID);
create index SKILL_HAS_SUBSKILL_IDX1 on PN_SKILL_HAS_SUBSKILL (CHILD_SKILL_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_ACCESS_HISTORY
prompt ======================================
prompt
create table PN_SPACE_ACCESS_HISTORY
(
  SPACE_ID    NUMBER(20) not null,
  PERSON_ID   NUMBER(20) not null,
  ACCESS_DATE DATE
)
;
alter table PN_SPACE_ACCESS_HISTORY
  add constraint SPACE_ACCESS_HISTORY_PK primary key (PERSON_ID, SPACE_ID);

prompt
prompt Creating table PN_SPACE_HAS_CALENDAR
prompt ====================================
prompt
create table PN_SPACE_HAS_CALENDAR
(
  SPACE_ID    NUMBER(20) not null,
  CALENDAR_ID NUMBER(20) not null
)
;
alter table PN_SPACE_HAS_CALENDAR
  add constraint SPACE_HAS_CALENDAR_PK primary key (SPACE_ID, CALENDAR_ID);
alter table PN_SPACE_HAS_CALENDAR
  add constraint SPACE_HAS_CALENDAR_FK1 foreign key (CALENDAR_ID)
  references PN_CALENDAR (CALENDAR_ID);
create index SPACE_HAS_CALENDAR_IDX1 on PN_SPACE_HAS_CALENDAR (CALENDAR_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_CLASS
prompt =================================
prompt
create table PN_SPACE_HAS_CLASS
(
  SPACE_ID NUMBER(20) not null,
  CLASS_ID NUMBER(20) not null,
  IS_OWNER NUMBER(1) not null
)
;
alter table PN_SPACE_HAS_CLASS
  add constraint SPACE_CLASS_PK primary key (SPACE_ID, CLASS_ID);
alter table PN_SPACE_HAS_CLASS
  add constraint SPACE_CLASS_FK1 foreign key (CLASS_ID)
  references PN_CLASS (CLASS_ID);
create index SPACE_CLASS_IDX1 on PN_SPACE_HAS_CLASS (CLASS_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_CLASS_LIST
prompt ======================================
prompt
create table PN_SPACE_HAS_CLASS_LIST
(
  SPACE_ID   NUMBER(20) not null,
  CLASS_ID   NUMBER(20) not null,
  LIST_ID    NUMBER(20) not null,
  IS_DEFAULT NUMBER(1) not null
)
;
alter table PN_SPACE_HAS_CLASS_LIST
  add constraint SPACE_CLASS_LIST_PK primary key (SPACE_ID, CLASS_ID, LIST_ID);
alter table PN_SPACE_HAS_CLASS_LIST
  add constraint SPACE_CLASS_LIST_FK1 foreign key (CLASS_ID, LIST_ID)
  references PN_CLASS_LIST (CLASS_ID, LIST_ID);
create index SPACE_CLASS_LIST_IDX1 on PN_SPACE_HAS_CLASS_LIST (CLASS_ID, LIST_ID, SPACE_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_DIRECTORY
prompt =====================================
prompt
create table PN_SPACE_HAS_DIRECTORY
(
  SPACE_ID     NUMBER(20) not null,
  DIRECTORY_ID NUMBER(20) not null
)
;
alter table PN_SPACE_HAS_DIRECTORY
  add constraint SPACE_DIRECTORY_PK primary key (SPACE_ID, DIRECTORY_ID);
alter table PN_SPACE_HAS_DIRECTORY
  add constraint SPACE_DIRECTORY_FK1 foreign key (DIRECTORY_ID)
  references PN_DIRECTORY (DIRECTORY_ID);
create unique index SPACE_DIRECTORY_IDX1 on PN_SPACE_HAS_DIRECTORY (DIRECTORY_ID, SPACE_ID);

prompt
prompt Creating table PN_SPACE_HAS_DIRECTORY_FIELD
prompt ===========================================
prompt
create table PN_SPACE_HAS_DIRECTORY_FIELD
(
  SPACE_ID           NUMBER(20) not null,
  DIRECTORY_FIELD_ID NUMBER(20) not null,
  DIRECTORY_ID       NUMBER(20),
  ROW_NUM            NUMBER(8),
  COLUMN_NUM         NUMBER(8)
)
;
alter table PN_SPACE_HAS_DIRECTORY_FIELD
  add constraint SPACE_DIR_FIELD_PK primary key (SPACE_ID, DIRECTORY_FIELD_ID);
alter table PN_SPACE_HAS_DIRECTORY_FIELD
  add constraint SPACE_DIR_FIELD_FK1 foreign key (DIRECTORY_ID)
  references PN_DIRECTORY (DIRECTORY_ID);
alter table PN_SPACE_HAS_DIRECTORY_FIELD
  add constraint SPACE_DIR_FIELD_FK2 foreign key (DIRECTORY_FIELD_ID)
  references PN_DIRECTORY_FIELD (DIRECTORY_FIELD_ID);
create index SPACE_DIR_FIELD_IDX1 on PN_SPACE_HAS_DIRECTORY_FIELD (DIRECTORY_FIELD_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_DOC_PROVIDER
prompt ========================================
prompt
create table PN_SPACE_HAS_DOC_PROVIDER
(
  SPACE_ID        NUMBER(20) not null,
  DOC_PROVIDER_ID NUMBER(20) not null,
  IS_DEFAULT      NUMBER(1)
)
;
alter table PN_SPACE_HAS_DOC_PROVIDER
  add constraint SPACE_DOC_PROVIDER_PK primary key (SPACE_ID, DOC_PROVIDER_ID);
alter table PN_SPACE_HAS_DOC_PROVIDER
  add constraint SPACE_HAS_DOC_PROVIDER_FK1 foreign key (DOC_PROVIDER_ID)
  references PN_DOC_PROVIDER (DOC_PROVIDER_ID);
create index SPACE_DOC_PROVIDER_IDX1 on PN_SPACE_HAS_DOC_PROVIDER (DOC_PROVIDER_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_DOC_SPACE
prompt =====================================
prompt
create table PN_SPACE_HAS_DOC_SPACE
(
  SPACE_ID     NUMBER(20) not null,
  DOC_SPACE_ID NUMBER(20) not null,
  IS_OWNER     NUMBER(1) default 0 not null
)
;
alter table PN_SPACE_HAS_DOC_SPACE
  add constraint SPACE_DOC_SPACE_PK primary key (SPACE_ID, DOC_SPACE_ID);
alter table PN_SPACE_HAS_DOC_SPACE
  add constraint SPACE_HAS_DOC_SPACE_FK1 foreign key (DOC_SPACE_ID)
  references PN_DOC_SPACE (DOC_SPACE_ID) on delete cascade;
create index SPACE_DOC_SPACE_IDX1 on PN_SPACE_HAS_DOC_SPACE (DOC_SPACE_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_FEATURED_MENUITEM
prompt =============================================
prompt
create table PN_SPACE_HAS_FEATURED_MENUITEM
(
  SPACE_ID  NUMBER(20) not null,
  OBJECT_ID NUMBER(20) not null
)
;
alter table PN_SPACE_HAS_FEATURED_MENUITEM
  add constraint PK_SPACE_HAS_FEATURED_MENUITEM primary key (SPACE_ID, OBJECT_ID);
alter table PN_SPACE_HAS_FEATURED_MENUITEM
  add constraint FK_OBJECT_ID foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_SPACE_HAS_FEATURED_MENUITEM
  add constraint FK_SPACE_ID foreign key (SPACE_ID)
  references PN_OBJECT (OBJECT_ID);

prompt
prompt Creating table PN_SPACE_HAS_GROUP
prompt =================================
prompt
create table PN_SPACE_HAS_GROUP
(
  SPACE_ID NUMBER(20) not null,
  GROUP_ID NUMBER(20) not null,
  IS_OWNER NUMBER(1) not null
)
;
alter table PN_SPACE_HAS_GROUP
  add constraint SPACE_HAS_GROUP_PK primary key (SPACE_ID, GROUP_ID);
alter table PN_SPACE_HAS_GROUP
  add constraint SPACE_HAS_GROUP_FK1 foreign key (GROUP_ID)
  references PN_GROUP (GROUP_ID);
create index SPACE_HAS_GROUP_IDX1 on PN_SPACE_HAS_GROUP (GROUP_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_METHODOLOGY
prompt =======================================
prompt
create table PN_SPACE_HAS_METHODOLOGY
(
  SPACE_ID       NUMBER(20) not null,
  METHODOLOGY_ID NUMBER(20) not null,
  PERSON_ID      NUMBER(20),
  DATE_APPLIED   DATE
)
;
alter table PN_SPACE_HAS_METHODOLOGY
  add constraint SPACE_HAS_METHODOLOGY_PK primary key (SPACE_ID, METHODOLOGY_ID);
alter table PN_SPACE_HAS_METHODOLOGY
  add constraint SPACE_HAS_METHODOLOGY_FK1 foreign key (METHODOLOGY_ID)
  references PN_METHODOLOGY_SPACE (METHODOLOGY_ID);

prompt
prompt Creating table PN_SPACE_HAS_NEWS
prompt ================================
prompt
create table PN_SPACE_HAS_NEWS
(
  SPACE_ID NUMBER(20) not null,
  NEWS_ID  NUMBER(20) not null
)
;
alter table PN_SPACE_HAS_NEWS
  add constraint SPACE_HAS_NEWS_PK primary key (SPACE_ID, NEWS_ID);
alter table PN_SPACE_HAS_NEWS
  add constraint SPACE_HAS_NEWS_TO_NEWS_FK foreign key (NEWS_ID)
  references PN_NEWS (NEWS_ID);

prompt
prompt Creating table PN_SPACE_HAS_PLAN
prompt ================================
prompt
create table PN_SPACE_HAS_PLAN
(
  SPACE_ID NUMBER(20) not null,
  PLAN_ID  NUMBER(20) not null
)
;
alter table PN_SPACE_HAS_PLAN
  add constraint SPACE_HAS_PLAN_PK primary key (SPACE_ID, PLAN_ID);
alter table PN_SPACE_HAS_PLAN
  add constraint SPACE_HAS_PLAN_FK1 foreign key (PLAN_ID)
  references PN_PLAN (PLAN_ID);
create index SPACE_HAS_PLAN_IDX1 on PN_SPACE_HAS_PLAN (PLAN_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_PORTFOLIO
prompt =====================================
prompt
create table PN_SPACE_HAS_PORTFOLIO
(
  SPACE_ID     NUMBER(20) not null,
  PORTFOLIO_ID NUMBER(20) not null,
  IS_DEFAULT   NUMBER(1) not null
)
;
alter table PN_SPACE_HAS_PORTFOLIO
  add constraint SPACE_PORTFOLIO_PK primary key (SPACE_ID, PORTFOLIO_ID);
alter table PN_SPACE_HAS_PORTFOLIO
  add constraint SPACE_PORTFOLIO_FK1 foreign key (PORTFOLIO_ID)
  references PN_PORTFOLIO (PORTFOLIO_ID);
create index SPACE_PORTFOLIO_IDX1 on PN_SPACE_HAS_PORTFOLIO (PORTFOLIO_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_PROCESS
prompt ===================================
prompt
create table PN_SPACE_HAS_PROCESS
(
  SPACE_ID   NUMBER(20) not null,
  PROCESS_ID NUMBER(20) not null
)
;
alter table PN_SPACE_HAS_PROCESS
  add constraint SPACE_PROCESS_PK primary key (SPACE_ID, PROCESS_ID);
alter table PN_SPACE_HAS_PROCESS
  add constraint SPACE_PROCESS_FK1 foreign key (PROCESS_ID)
  references PN_PROCESS (PROCESS_ID);
create index SPACE_PROCESS_IDX1 on PN_SPACE_HAS_PROCESS (PROCESS_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_PROPERTY_SHEET
prompt ==========================================
prompt
create table PN_SPACE_HAS_PROPERTY_SHEET
(
  SPACE_ID          NUMBER not null,
  PROPERTY_SHEET_ID NUMBER(20) not null
)
;
alter table PN_SPACE_HAS_PROPERTY_SHEET
  add constraint SPACE_PROPERTY_SHEET_PK primary key (SPACE_ID, PROPERTY_SHEET_ID);
alter table PN_SPACE_HAS_PROPERTY_SHEET
  add constraint SPACE_PROPERTY_SHEET_FK1 foreign key (PROPERTY_SHEET_ID)
  references PN_PROPERTY_SHEET (PROPERTY_SHEET_ID);

prompt
prompt Creating table PN_SPACE_HAS_SKILL
prompt =================================
prompt
create table PN_SPACE_HAS_SKILL
(
  SPACE_ID NUMBER(20) not null,
  SKILL_ID NUMBER(20) not null
)
;
alter table PN_SPACE_HAS_SKILL
  add constraint SPACE_HAS_SKILL_PK primary key (SPACE_ID, SKILL_ID);
alter table PN_SPACE_HAS_SKILL
  add constraint SPACE_HAS_SKILL_FK1 foreign key (SKILL_ID)
  references PN_SKILL (SKILL_ID);
create index SPACE_HAS_SKILL_IDX1 on PN_SPACE_HAS_SKILL (SKILL_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_SKILL_CATEGORY
prompt ==========================================
prompt
create table PN_SPACE_HAS_SKILL_CATEGORY
(
  SPACE_ID          NUMBER(20) not null,
  SKILL_CATEGORY_ID NUMBER(20) not null
)
;
alter table PN_SPACE_HAS_SKILL_CATEGORY
  add constraint SPACE_SKILL_CATEGORY_PK primary key (SPACE_ID, SKILL_CATEGORY_ID);
alter table PN_SPACE_HAS_SKILL_CATEGORY
  add constraint SPACE_HAS_SKILL_CATEGORY_FK1 foreign key (SKILL_CATEGORY_ID)
  references PN_SKILL_CATEGORY (SKILL_CATEGORY_ID);
create index SPACE_SKILL_CATEGORY_IDX1 on PN_SPACE_HAS_SKILL_CATEGORY (SKILL_CATEGORY_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_SPACE
prompt =================================
prompt
create table PN_SPACE_HAS_SPACE
(
  PARENT_SPACE_ID              NUMBER(20) not null,
  CHILD_SPACE_ID               NUMBER(20) not null,
  CREATED_BY                   NUMBER(20),
  DATE_CREATED                 DATE,
  RELATIONSHIP_PARENT_TO_CHILD VARCHAR2(80),
  RELATIONSHIP_CHILD_TO_PARENT VARCHAR2(80),
  RECORD_STATUS                VARCHAR2(1) not null,
  PARENT_SPACE_TYPE            VARCHAR2(40),
  CHILD_SPACE_TYPE             VARCHAR2(40)
)
;
alter table PN_SPACE_HAS_SPACE
  add constraint SPACE_HAS_SPACE_PK primary key (PARENT_SPACE_ID, CHILD_SPACE_ID);
alter table PN_SPACE_HAS_SPACE
  add constraint RECORD_STATUS_VALID52
  check (record_status in ('A','P','D','H'));
create index SPACE_SPACE_IDX1 on PN_SPACE_HAS_SPACE (CHILD_SPACE_ID) TABLESPACE INDEX01;
create index SPACE_SPACE_IDX2 on PN_SPACE_HAS_SPACE (RELATIONSHIP_PARENT_TO_CHILD) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_SUBSCRIPTION
prompt ========================================
prompt
create table PN_SPACE_HAS_SUBSCRIPTION
(
  SPACE_ID        NUMBER(20) not null,
  SUBSCRIPTION_ID NUMBER(20) not null
)
;
alter table PN_SPACE_HAS_SUBSCRIPTION
  add constraint SPACE_HAS_SUB_PK primary key (SPACE_ID, SUBSCRIPTION_ID);
alter table PN_SPACE_HAS_SUBSCRIPTION
  add constraint PN_SPACE_HAS_SUB_FK1 foreign key (SUBSCRIPTION_ID)
  references PN_SUBSCRIPTION (SUBSCRIPTION_ID);

prompt
prompt Creating table PN_ZIPCODE_FEED_LOOKUP
prompt =====================================
prompt
create table PN_ZIPCODE_FEED_LOOKUP
(
  ZIPCODE VARCHAR2(20) not null,
  FEED    VARCHAR2(120) not null
)
;
alter table PN_ZIPCODE_FEED_LOOKUP
  add constraint ZIPCODE_FEED_LOOKUP_PK primary key (ZIPCODE);

prompt
prompt Creating table PN_SPACE_HAS_WEATHER
prompt ===================================
prompt
create table PN_SPACE_HAS_WEATHER
(
  SPACE_ID     NUMBER(20) not null,
  CITY         VARCHAR2(80) not null,
  STATE_CODE   VARCHAR2(2) not null,
  COUNTRY_CODE VARCHAR2(2) not null,
  POSTAL_CODE  VARCHAR2(20),
  NAME         VARCHAR2(80) not null,
  IS_PRIMARY   NUMBER(1) not null
)
;
alter table PN_SPACE_HAS_WEATHER
  add constraint SPACE_WEATHER_PK primary key (SPACE_ID, CITY, STATE_CODE, COUNTRY_CODE);
alter table PN_SPACE_HAS_WEATHER
  add constraint SPACE_WEATHER_FK1 foreign key (STATE_CODE)
  references PN_STATE_LOOKUP (STATE_CODE);
alter table PN_SPACE_HAS_WEATHER
  add constraint SPACE_WEATHER_FK2 foreign key (COUNTRY_CODE)
  references PN_COUNTRY_LOOKUP (COUNTRY_CODE);
alter table PN_SPACE_HAS_WEATHER
  add constraint SPACE_WEATHER_FK3 foreign key (POSTAL_CODE)
  references PN_ZIPCODE_FEED_LOOKUP (ZIPCODE);
create index SPACE_WEATHER_IDX1 on PN_SPACE_HAS_WEATHER (POSTAL_CODE) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_HAS_WORKFLOW
prompt ====================================
prompt
create table PN_SPACE_HAS_WORKFLOW
(
  SPACE_ID    NUMBER(20) not null,
  WORKFLOW_ID NUMBER(20) not null
)
;
alter table PN_SPACE_HAS_WORKFLOW
  add constraint PN_SPACE_HAS_WORKFLOW_PK primary key (SPACE_ID, WORKFLOW_ID);
alter table PN_SPACE_HAS_WORKFLOW
  add constraint SPACE_WORKFLOW_TO_WORKFLOW_FK foreign key (WORKFLOW_ID)
  references PN_WORKFLOW (WORKFLOW_ID);
create index SPACE_HAS_WORKFLOW_IDX1 on PN_SPACE_HAS_WORKFLOW (WORKFLOW_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_SPACE_TYPE_HAS_REPORT_TYPE
prompt ============================================
prompt
create table PN_SPACE_TYPE_HAS_REPORT_TYPE
(
  SPACE_TYPE  VARCHAR2(80) not null,
  REPORT_TYPE VARCHAR2(10) not null
)
;
alter table PN_SPACE_TYPE_HAS_REPORT_TYPE
  add constraint SPACE_TYPE_REPORT_TYPE_PK primary key (SPACE_TYPE, REPORT_TYPE);

prompt
prompt Creating table PN_VIEW
prompt ======================
prompt
create table PN_VIEW
(
  VIEW_ID           NUMBER(20) not null,
  NAME              VARCHAR2(80) not null,
  DESCRIPTION       VARCHAR2(250),
  CREATED_BY_ID     NUMBER(20) not null,
  CREATED_DATETIME  DATE not null,
  MODIFIED_BY_ID    NUMBER(20) not null,
  MODIFIED_DATETIME DATE not null,
  RECORD_STATUS     VARCHAR2(1) not null,
  INGREDIENTS_ID    NUMBER(20) not null
)
;
alter table PN_VIEW
  add constraint PN_VIEW_PK primary key (VIEW_ID);
alter table PN_VIEW
  add constraint PN_VIEW_FK1 foreign key (INGREDIENTS_ID)
  references PN_FINDER_INGREDIENTS (INGREDIENTS_ID);

prompt
prompt Creating table PN_SPACE_VIEW_CONTEXT
prompt ====================================
prompt
create table PN_SPACE_VIEW_CONTEXT
(
  SPACE_ID NUMBER(20) not null,
  VIEW_ID  NUMBER(20) not null
)
;
alter table PN_SPACE_VIEW_CONTEXT
  add constraint PN_SPACE_VIEW_CONTEXT_PK primary key (SPACE_ID, VIEW_ID);
alter table PN_SPACE_VIEW_CONTEXT
  add constraint PN_SPACE_VIEW_CONTEXT_FK1 foreign key (VIEW_ID)
  references PN_VIEW (VIEW_ID);

prompt
prompt Creating table PN_SP_ERROR_CODES
prompt ================================
prompt
create table PN_SP_ERROR_CODES
(
  ERROR_CODE        NUMBER(20) not null,
  ERROR_NAME        VARCHAR2(80) not null,
  ERROR_DESCRIPTION VARCHAR2(2000)
)
;
alter table PN_SP_ERROR_CODES
  add constraint SP_ERROR_CODES_PK primary key (ERROR_CODE);

prompt
prompt Creating table PN_SP_ERROR_LOG
prompt ==============================
prompt
create table PN_SP_ERROR_LOG
(
  TIMESTAMP        DATE,
  STORED_PROC_NAME VARCHAR2(60),
  ERROR_CODE       NUMBER(20),
  ERROR_MSG        VARCHAR2(240)
)
;

prompt
prompt Creating table PN_SUBSCRIPTION_HAS_GROUP
prompt ========================================
prompt
create table PN_SUBSCRIPTION_HAS_GROUP
(
  SUBSCRIPTION_ID     NUMBER(20) not null,
  DELIVERY_TYPE_ID    NUMBER(20) not null,
  SUBSCRIBER_BATCH_ID NUMBER(20) not null,
  DELIVERY_GROUP_ID   NUMBER(20) not null
)
;
alter table PN_SUBSCRIPTION_HAS_GROUP
  add constraint PN_SUBSCRIPTION_HAS_GROUP_PK primary key (SUBSCRIPTION_ID, DELIVERY_GROUP_ID);
alter table PN_SUBSCRIPTION_HAS_GROUP
  add constraint PN_SUBSCRIPTION_HAS_GROUP_FK1 foreign key (SUBSCRIPTION_ID)
  references PN_SUBSCRIPTION (SUBSCRIPTION_ID);
alter table PN_SUBSCRIPTION_HAS_GROUP
  add constraint PN_SUBSCRIPTION_HAS_GROUP_FK2 foreign key (DELIVERY_TYPE_ID)
  references PN_NOTIFICATION_DELIVERY_TYPE (DELIVERY_TYPE_ID);

prompt
prompt Creating table PN_SUBSCRIPTION_RECURRENCE
prompt =========================================
prompt
create table PN_SUBSCRIPTION_RECURRENCE
(
  RECURRENCE_ID NUMBER(20) not null,
  NAME          VARCHAR2(80),
  DESCRIPTION   VARCHAR2(500),
  RECORD_STATUS VARCHAR2(1),
  CRC           DATE
)
;
alter table PN_SUBSCRIPTION_RECURRENCE
  add constraint SUBSCRIPTION_RECURRENCE_PK primary key (RECURRENCE_ID);

prompt
prompt Creating table PN_SUBSCRIPTION_TYPE
prompt ===================================
prompt
create table PN_SUBSCRIPTION_TYPE
(
  SUBSCRIPTION_TYPE_ID NUMBER(20) not null,
  TABLE_NAME           VARCHAR2(80),
  NAME                 VARCHAR2(80),
  DESCRIPTION          VARCHAR2(500),
  RECORD_STATUS        VARCHAR2(1),
  CRC                  DATE
)
;
alter table PN_SUBSCRIPTION_TYPE
  add constraint SUBSCRIPTION_TYPE_PK primary key (SUBSCRIPTION_TYPE_ID);

prompt
prompt Creating table PN_SUB_HAS_NOTIFY_TYPE
prompt =====================================
prompt
create table PN_SUB_HAS_NOTIFY_TYPE
(
  NOTIFICATION_TYPE_ID NUMBER(20) not null,
  SUBSCRIPTION_ID      NUMBER(20) not null
)
;
alter table PN_SUB_HAS_NOTIFY_TYPE
  add constraint PN_SUB_HAS_NOTIFY_TYPE_PK primary key (NOTIFICATION_TYPE_ID, SUBSCRIPTION_ID);
alter table PN_SUB_HAS_NOTIFY_TYPE
  add constraint PN_SUB_HAS_NOTIFY_TYPE_FK1 foreign key (NOTIFICATION_TYPE_ID)
  references PN_NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID);
alter table PN_SUB_HAS_NOTIFY_TYPE
  add constraint PN_SUB_HAS_NOTIFY_TYPE_FK2 foreign key (SUBSCRIPTION_ID)
  references PN_SUBSCRIPTION (SUBSCRIPTION_ID);

prompt
prompt Creating table PN_SYSTEM_CONFIG
prompt ===============================
prompt
create table PN_SYSTEM_CONFIG
(
  SYSTEM_SPACE_ID NUMBER(20) not null
)
;
alter table PN_SYSTEM_CONFIG
  add constraint SYSTEM_CONFIG_PK primary key (SYSTEM_SPACE_ID);

prompt
prompt Creating table PN_SYSTEM_SETTING
prompt ================================
prompt
create table PN_SYSTEM_SETTING
(
  NAME  VARCHAR2(1000) not null,
  VALUE VARCHAR2(1000)
)
;
comment on table PN_SYSTEM_SETTING
  is 'Provides system settings irrespective of configuration and across all instances of a web application.';
alter table PN_SYSTEM_SETTING
  add constraint PN_SYSTEM_SETTING_PK primary key (NAME);

prompt
prompt Creating table PN_TASK_ACTION_LOOKUP
prompt ====================================
prompt
create table PN_TASK_ACTION_LOOKUP
(
  ACTION        VARCHAR2(80) not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_TASK_ACTION_LOOKUP
  add constraint TASK_ACTION_LOOKUP_PK primary key (ACTION);
alter table PN_TASK_ACTION_LOOKUP
  add constraint RECORD_STATUS_VALID60
  check (record_status in ('A','P','D','H'));

prompt
prompt Creating table PN_TASK_BASELINE
prompt ===============================
prompt
create table PN_TASK_BASELINE
(
  TASK_ID             NUMBER(20) not null,
  BASELINE_ID         NUMBER(20) not null,
  TASK_NAME           VARCHAR2(255),
  CREATED_BY          NUMBER(20) not null,
  BASELINE_SET_DATE   DATE,
  TASK_TYPE           VARCHAR2(80),
  TASK_DURATION       NUMBER(18),
  PRIORITY            NUMBER(6),
  TASK_WORK           NUMBER,
  STATUS_ID           NUMBER(20),
  WORK_UNITS          VARCHAR2(80),
  TASK_WORK_COMPLETE  NUMBER,
  DATE_START          DATE,
  DURATION_UNITS      NUMBER(20),
  DATE_FINISH         DATE,
  RECORD_STATUS       VARCHAR2(1) not null,
  WORK_COMPLETE_UNITS NUMBER(20),
  PARENT_TASK_ID      NUMBER(20),
  CRITICAL_PATH       NUMBER(1)
)
;
alter table PN_TASK_BASELINE
  add constraint TASK_BASELINE_PK primary key (TASK_ID, BASELINE_ID);
alter table PN_TASK_BASELINE
  add constraint TASK_BASELINE_FK1 foreign key (TASK_ID)
  references PN_TASK (TASK_ID);
alter table PN_TASK_BASELINE
  add constraint RECORD_STATUS_VALID53
  check (record_status in ('A','P','D','H'));
create index TASK_BASELINE_IDX1 on PN_TASK_BASELINE (BASELINE_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_TASK_COMMENT
prompt ==============================
prompt
create table PN_TASK_COMMENT
(
  TASK_ID          NUMBER(20) not null,
  BASELINE_ID      NUMBER(20) not null,
  CREATED_BY_ID    NUMBER(20) not null,
  CREATED_DATETIME DATE not null,
  SEQ              NUMBER(20) not null,
  TEXT_CLOB        CLOB
)
;
alter table PN_TASK_COMMENT
  add constraint TASK_COMMENT_PK primary key (TASK_ID, BASELINE_ID, SEQ);

prompt
prompt Creating table PN_TASK_CYCLE_DEPENDENCIES
prompt =========================================
prompt
create global temporary table PN_TASK_CYCLE_DEPENDENCIES
(
  ID NUMBER(20)
)
on commit delete rows;

prompt
prompt Creating table PN_TASK_CYCLE_WORK
prompt =================================
prompt
create global temporary table PN_TASK_CYCLE_WORK
(
  ID NUMBER(20) not null
)
on commit delete rows;

prompt
prompt Creating table PN_TASK_DEPENDENCY
prompt =================================
prompt
create table PN_TASK_DEPENDENCY
(
  TASK_ID            NUMBER(20) not null,
  DEPENDENCY_ID      NUMBER(20) not null,
  DEPENDENCY_TYPE_ID NUMBER(20) not null,
  LAG                NUMBER,
  LAG_UNITS          VARCHAR2(5)
)
;
alter table PN_TASK_DEPENDENCY
  add constraint TASK_DEPENDENCY_PK primary key (TASK_ID, DEPENDENCY_ID, DEPENDENCY_TYPE_ID);
create index TASK_DEPENDENCY_IDX1 on PN_TASK_DEPENDENCY (DEPENDENCY_ID) TABLESPACE INDEX01;
create index TASK_DEPENDENCY_IDX2 on PN_TASK_DEPENDENCY (TASK_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_TASK_DEPENDENCY_VERSION
prompt =========================================
prompt
create table PN_TASK_DEPENDENCY_VERSION
(
  TASK_ID            NUMBER(20) not null,
  TASK_VERSION_ID    NUMBER(20) not null,
  DEPENDENCY_ID      NUMBER(20) not null,
  DEPENDENCY_TYPE_ID NUMBER(20) not null,
  LAG                NUMBER,
  LAG_UNITS          VARCHAR2(5)
)
;
alter table PN_TASK_DEPENDENCY_VERSION
  add constraint TASK_DEPENDENCY_VERSION_PK primary key (TASK_ID, TASK_VERSION_ID, DEPENDENCY_ID, DEPENDENCY_TYPE_ID);
create index TASK_DEPENDENCY_VERSION_IDX1 on PN_TASK_DEPENDENCY_VERSION (DEPENDENCY_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_TASK_HISTORY
prompt ==============================
prompt
create table PN_TASK_HISTORY
(
  TASK_ID         NUMBER(20) not null,
  TASK_HISTORY_ID NUMBER(20) not null,
  ACTION_BY_ID    NUMBER(20),
  ACTION          VARCHAR2(80),
  ACTION_COMMENT  VARCHAR2(500),
  ACTION_DATE     DATE,
  ACTION_NAME     VARCHAR2(80)
)
;
alter table PN_TASK_HISTORY
  add constraint TASK_HISTORY_PK primary key (TASK_ID, TASK_HISTORY_ID);
alter table PN_TASK_HISTORY
  add constraint TASK_HISTORY_FK1 foreign key (TASK_ID)
  references PN_TASK (TASK_ID);
alter table PN_TASK_HISTORY
  add constraint TASK_HISTORY_FK2 foreign key (ACTION_BY_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_TASK_HISTORY
  add constraint TASK_HISTORY_FK3 foreign key (ACTION)
  references PN_TASK_ACTION_LOOKUP (ACTION);
create unique index TASK_HISTORY_IDX1 on PN_TASK_HISTORY (TASK_HISTORY_ID, TASK_ID);

prompt
prompt Creating table PN_TASK_VERSION
prompt ==============================
prompt
create table PN_TASK_VERSION
(
  TASK_ID                        NUMBER(20) not null,
  TASK_VERSION_ID                NUMBER(20) not null,
  TASK_NAME                      VARCHAR2(255) not null,
  TASK_DESC                      VARCHAR2(4000),
  TASK_TYPE                      VARCHAR2(80),
  DURATION                       NUMBER,
  WORK                           NUMBER,
  WORK_UNITS                     NUMBER(20),
  WORK_COMPLETE                  NUMBER,
  DATE_START                     DATE,
  WORK_COMPLETE_UNITS            NUMBER(20),
  DATE_FINISH                    DATE,
  ACTUAL_START                   DATE,
  ACTUAL_FINISH                  DATE,
  PRIORITY                       NUMBER(6),
  PERCENT_COMPLETE               NUMBER(6),
  DATE_CREATED                   DATE,
  DATE_MODIFIED                  DATE,
  MODIFIED_BY                    NUMBER(20),
  DURATION_UNITS                 NUMBER(20),
  PARENT_TASK_ID                 NUMBER(20),
  PARENT_TASK_VERSION_ID         NUMBER(20),
  RECORD_STATUS                  VARCHAR2(1) not null,
  CRITICAL_PATH                  NUMBER,
  SEQ                            NUMBER,
  IGNORE_TIMES_FOR_DATES         NUMBER(1),
  IS_MILESTONE                   NUMBER(1),
  EARLY_START                    DATE,
  EARLY_FINISH                   DATE,
  LATE_START                     DATE,
  LATE_FINISH                    DATE,
  WORK_PERCENT_COMPLETE          NUMBER,
  CONSTRAINT_TYPE                VARCHAR2(10),
  CONSTRAINT_DATE                DATE,
  DEADLINE                       DATE,
  BASELINE_ID                    NUMBER(20),
  PLAN_VERSION_ID                NUMBER(20),
  LEGACY_FLAG                    NUMBER,
  CALCULATION_TYPE_ID            NUMBER(20),
  UNALLOCATED_WORK_COMPLETE      NUMBER(20) default 0,
  UNALLOCATED_WORK_COMPLETE_UNIT NUMBER(20) default 4,
  WORK_MS                        NUMBER(20),
  WORK_COMPLETE_MS               NUMBER(20)
)
;
alter table PN_TASK_VERSION
  add constraint TASK_VERSION_PK primary key (TASK_ID, TASK_VERSION_ID);
create index TASK_VERSION_IDX2 on PN_TASK_VERSION (TASK_ID, PARENT_TASK_ID) TABLESPACE INDEX01;
create index TASK_VERSION_IDX3 on PN_TASK_VERSION (PARENT_TASK_ID, TASK_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_TIMEZONE_LOOKUP
prompt =================================
prompt
create table PN_TIMEZONE_LOOKUP
(
  TIMEZONE_CODE        VARCHAR2(3) not null,
  TIMEZONE_DESCRIPTION VARCHAR2(60),
  GMT_OFFSET           VARCHAR2(8) not null
)
;
alter table PN_TIMEZONE_LOOKUP
  add constraint TIMEZONE_LOOKUP_PK primary key (TIMEZONE_CODE);

prompt
prompt Creating table PN_TIME_FORMAT
prompt =============================
prompt
create table PN_TIME_FORMAT
(
  TIME_FORMAT_ID NUMBER(20) not null,
  FORMAT_STRING  VARCHAR2(80) not null,
  DISPLAY        VARCHAR2(80) not null,
  EXAMPLE        VARCHAR2(80)
)
;
alter table PN_TIME_FORMAT
  add constraint PN_TIME_FORMAT_PK primary key (TIME_FORMAT_ID);

prompt
prompt Creating table PN_TMP_DOCUMENT
prompt ==============================
prompt
create table PN_TMP_DOCUMENT
(
  TMP_DOC_ID            NUMBER(20) not null,
  DOC_ID                NUMBER(20),
  DOC_VERSION_ID        NUMBER(20),
  DOC_NAME              VARCHAR2(240),
  DOC_DESCRIPTION       VARCHAR2(500),
  CURRENT_VERSION_ID    NUMBER(20),
  DOC_VERSION_NAME      VARCHAR2(240),
  VER_DOC_DESCRIPTION   VARCHAR2(20),
  SOURCE_FILE_NAME      VARCHAR2(240),
  MODIFIED_BY_ID        NUMBER(20),
  DATE_MODIFIED         DATE,
  IS_CHECKED_OUT        NUMBER(1),
  CHECKED_OUT_BY_ID     NUMBER(20),
  DATE_CHECKED_OUT      DATE,
  DOC_COMMENT           VARCHAR2(4000),
  VER_RECORD_STATUS     VARCHAR2(1),
  DOC_CONTENT_ID        NUMBER(20),
  DOC_FORMAT_ID         NUMBER(20),
  DISPLAY_SEQUENCE      NUMBER(8),
  CONTENT_RECORD_STATUS VARCHAR2(1),
  DOC_VERSION_NUM       NUMBER(20),
  DOC_VERSION_LABEL     VARCHAR2(240),
  FILE_SIZE             NUMBER(20),
  FILE_HANDLE           VARCHAR2(240),
  DOC_HISTORY_ID        NUMBER(20),
  ACTION_ID             NUMBER(20),
  ACTION_BY_ID          NUMBER(20),
  ACTION_COMMENT        VARCHAR2(255),
  ACTION_DESC           VARCHAR2(255),
  DOC_CONTAINER_ID      NUMBER(20),
  CHECKOUT_DUE          DATE,
  DOC_STATUS_ID         NUMBER(20),
  DOC_TYPE_ID           NUMBER(20),
  DOC_AUTHOR_ID         NUMBER(20),
  SHORT_FILE_NAME       VARCHAR2(240),
  REPOSITORY_ID         NUMBER(4),
  RECORD_STATUS         VARCHAR2(1)
)
;
alter table PN_TMP_DOCUMENT
  add constraint TMP_DOCUMENT_PK primary key (TMP_DOC_ID);

prompt
prompt Creating table PN_TMP_DOC_PATH_INFO
prompt ===================================
prompt
create table PN_TMP_DOC_PATH_INFO
(
  COLLECTION_ID NUMBER(20) not null,
  OBJECT_ID     NUMBER(20),
  OBJECT_NAME   VARCHAR2(80),
  PARENT_ID     NUMBER(20),
  PARENT_NAME   VARCHAR2(80),
  IS_ROOT       NUMBER(1) default 0 not null
)
;
create index TMP_DOC_PATH_IDX1 on PN_TMP_DOC_PATH_INFO (COLLECTION_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_TMP_HEARTBEAT_METRICS
prompt =======================================
prompt
create table PN_TMP_HEARTBEAT_METRICS
(
  COLLECTION_ID          NUMBER(20) not null,
  MODULE_NAME            VARCHAR2(80),
  TOTAL                  NUMBER(9),
  TOTAL_OPEN             NUMBER(9),
  TOTAL_CLOSED           NUMBER(9),
  TOTAL_CLOSED_LAST_WEEK NUMBER(9)
)
;
alter table PN_TMP_HEARTBEAT_METRICS
  add constraint TMP_HEARTBEAT_METRICS_PK primary key (COLLECTION_ID);

prompt
prompt Creating table PN_USER
prompt ======================
prompt
create table PN_USER
(
  USER_ID       NUMBER(20) not null,
  USERNAME      VARCHAR2(32) not null,
  DOMAIN_ID     NUMBER(20) not null,
  RECORD_STATUS VARCHAR2(1) not null,
  IS_LOGIN      NUMBER(1) default 0 not null,
  LAST_BRAND_ID NUMBER(20),
  LAST_LOGIN    DATE
)
;
comment on column PN_USER.USER_ID
  is 'Foreign key to pn_person.person_id';
comment on column PN_USER.USERNAME
  is 'The username is our key to the authentication provider for lookups';
comment on column PN_USER.DOMAIN_ID
  is 'FK to pn_authentication_domain';
comment on column PN_USER.LAST_BRAND_ID
  is 'Null column updated each time a user logs in';
comment on column PN_USER.LAST_LOGIN
  is 'denormalized entry - also found in login_history';
alter table PN_USER
  add constraint PN_USER_PK primary key (USER_ID);
alter table PN_USER
  add constraint PN_USER_AK1 unique (DOMAIN_ID, USERNAME);
alter table PN_USER
  add constraint PN_USER_AK2 unique (USER_ID, DOMAIN_ID);
alter table PN_USER
  add constraint PN_USER_FK1 foreign key (USER_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_USER
  add constraint PN_USER_FK2 foreign key (DOMAIN_ID)
  references PN_USER_DOMAIN (DOMAIN_ID);

prompt
prompt Creating table PN_USER_DEFAULT_CREDENTIALS
prompt ==========================================
prompt
create table PN_USER_DEFAULT_CREDENTIALS
(
  USER_ID    NUMBER(20) not null,
  PASSWORD   VARCHAR2(240) not null,
  JOG_PHRASE VARCHAR2(240) not null,
  JOG_ANSWER VARCHAR2(240) not null,
  DOMAIN_ID  NUMBER(20) not null
)
;
alter table PN_USER_DEFAULT_CREDENTIALS
  add constraint PN_USER_DEFAULT_CREDENTIALS_PK primary key (USER_ID, DOMAIN_ID);
alter table PN_USER_DEFAULT_CREDENTIALS
  add constraint PN_USER_DEF_CREDENTIALS_FK1 foreign key (USER_ID, DOMAIN_ID)
  references PN_USER (USER_ID, DOMAIN_ID);

prompt
prompt Creating table PN_USER_DOMAIN_MIGRATION
prompt =======================================
prompt
create table PN_USER_DOMAIN_MIGRATION
(
  USER_ID             NUMBER(20) not null,
  DOMAIN_MIGRATION_ID NUMBER(20) not null,
  MIGRATION_STATUS_ID NUMBER(3) not null,
  ACTIVITY_DATE       DATE not null,
  IS_CURRENT          NUMBER(1) not null
)
;
alter table PN_USER_DOMAIN_MIGRATION
  add constraint PN_USER_DOMAIN_MIGRATION_PK primary key (USER_ID, DOMAIN_MIGRATION_ID);
alter table PN_USER_DOMAIN_MIGRATION
  add constraint PN_USER_DOMAIN_MIGRATION_FK1 foreign key (USER_ID)
  references PN_USER (USER_ID) on delete cascade;
alter table PN_USER_DOMAIN_MIGRATION
  add constraint PN_USER_DOMAIN_MIGRATION_FK2 foreign key (DOMAIN_MIGRATION_ID)
  references PN_DOMAIN_MIGRATION (DOMAIN_MIGRATION_ID);

prompt
prompt Creating table PN_USER_DOMAIN_SUPPORTS_CONFIG
prompt =============================================
prompt
create table PN_USER_DOMAIN_SUPPORTS_CONFIG
(
  DOMAIN_ID        NUMBER(20) not null,
  CONFIGURATION_ID NUMBER(20) not null
)
;

prompt
prompt Creating table PN_USER_HAS_MASTER_BUSINESS
prompt ==========================================
prompt
create table PN_USER_HAS_MASTER_BUSINESS
(
  PERSON_ID   NUMBER(20) not null,
  BUSINESS_ID NUMBER(20) not null
)
;
alter table PN_USER_HAS_MASTER_BUSINESS
  add constraint USER_HAS_MASTER_BUSINESS_PK primary key (PERSON_ID, BUSINESS_ID);

prompt
prompt Creating table PN_VIEW_DEFAULT_SETTING
prompt ======================================
prompt
create table PN_VIEW_DEFAULT_SETTING
(
  CONTEXT_ID  NUMBER(20) not null,
  SCENARIO_ID NUMBER(20) not null,
  VIEW_ID     NUMBER(20) not null
)
;
alter table PN_VIEW_DEFAULT_SETTING
  add constraint PN_VIEW_DEFAULT_SETTING_PK primary key (CONTEXT_ID, SCENARIO_ID);
alter table PN_VIEW_DEFAULT_SETTING
  add constraint PN_VIEW_DEFAULT_SETTING_FK1 foreign key (VIEW_ID)
  references PN_VIEW (VIEW_ID) on delete set null;

prompt
prompt Creating table PN_VOTES
prompt =======================
prompt
create table PN_VOTES
(
  VOTE_ID  NUMBER(20) not null,
  QUESTION VARCHAR2(20) not null,
  RESPONSE VARCHAR2(20) not null,
  COMMENTS VARCHAR2(20) not null
)
;

prompt
prompt Creating table PN_VOTE_QUESTIONAIR
prompt ==================================
prompt
create table PN_VOTE_QUESTIONAIR
(
  SPACE_ID NUMBER(20) not null,
  VOTE_ID  NUMBER(20) not null,
  QUESTION VARCHAR2(20) not null,
  TITLE    VARCHAR2(20) not null
)
;

prompt
prompt Creating table PN_VOTE_RESPONSE
prompt ===============================
prompt
create table PN_VOTE_RESPONSE
(
  VOTE_ID  NUMBER(20) not null,
  RESPONSE VARCHAR2(20) not null
)
;

prompt
prompt Creating table PN_WEBLOG
prompt ========================
prompt
create table PN_WEBLOG
(
  WEBLOG_ID              NUMBER(20) not null,
  NAME                   VARCHAR2(240) not null,
  DESCRIPTION            VARCHAR2(240) not null,
  PERSON_ID              NUMBER(20) not null,
  ALLOW_COMMENTS         NUMBER(1) default 1 not null,
  EMAIL_COMMENTS         NUMBER(1) default 0 not null,
  EMAIL_FROM_ADDRESS     VARCHAR2(240),
  EMAIL_ADDRESS          VARCHAR2(240) not null,
  LOCALE                 VARCHAR2(20),
  TIMEZONE               VARCHAR2(50),
  IS_ENABLED             NUMBER(1) default 1 not null,
  IS_ACTIVE              NUMBER(1) default 1 not null,
  CREATED_DATE           DATE,
  DEFAULT_ALLOW_COMMENTS NUMBER(1) default 1 not null,
  DEFAULT_COMMENT_DAYS   NUMBER(11) default 7 not null,
  SPACE_ID               NUMBER(20) not null
)
;
alter table PN_WEBLOG
  add constraint WEBLOG_PK primary key (WEBLOG_ID);
alter table PN_WEBLOG
  add constraint WEBLOG_PERSON_FK foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
create index WEBLOG_IDX1 on PN_WEBLOG (PERSON_ID) TABLESPACE INDEX01;
create index WEBLOG_IDX2 on PN_WEBLOG (IS_ENABLED) TABLESPACE INDEX01;

prompt
prompt Creating table PN_WEBLOG_ENTRY
prompt ==============================
prompt
create table PN_WEBLOG_ENTRY
(
  WEBLOG_ENTRY_ID NUMBER(20) not null,
  PERSON_ID       NUMBER(20) not null,
  ANCHOR          VARCHAR2(240) not null,
  TITLE           VARCHAR2(240) not null,
  TEXT            CLOB not null,
  PUB_TIME        DATE,
  UPDATE_TIME     DATE not null,
  WEBLOG_ID       NUMBER(20) not null,
  PUBLISH_ENTRY   NUMBER(1) default 1 not null,
  LINK            VARCHAR2(240),
  ALLOW_COMMENTS  NUMBER(1) default 0 not null,
  COMMENT_DAYS    NUMBER(11) default 7 not null,
  RIGHT_TO_LEFT   NUMBER(1) default 0 not null,
  LOCALE          VARCHAR2(20),
  STATUS          VARCHAR2(20) not null,
  SUMMARY         CLOB,
  CONTENT_TYPE    VARCHAR2(48),
  CONTENT_SRC     VARCHAR2(240)
)
;
alter table PN_WEBLOG_ENTRY
  add constraint WEBLOG_ENTRY_PK primary key (WEBLOG_ENTRY_ID);
alter table PN_WEBLOG_ENTRY
  add constraint WEBLOG_ENTRY_PERSON_FK foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
alter table PN_WEBLOG_ENTRY
  add constraint WEBLOG_ENTRY_WEBLOG_FK foreign key (WEBLOG_ID)
  references PN_WEBLOG (WEBLOG_ID);
create index WEBLOG_ENTRY_IDX1 on PN_WEBLOG_ENTRY (WEBLOG_ID) TABLESPACE INDEX01;
create index WEBLOG_ENTRY_IDX3 on PN_WEBLOG_ENTRY (PERSON_ID) TABLESPACE INDEX01;
create index WEBLOG_ENTRY_IDX4 on PN_WEBLOG_ENTRY (STATUS) TABLESPACE INDEX01;
create index WEBLOG_ENTRY_IDX5 on PN_WEBLOG_ENTRY (LOCALE) TABLESPACE INDEX01;
create index WEBLOG_ENTRY_IDX6 on PN_WEBLOG_ENTRY (STATUS, PUB_TIME, WEBLOG_ID) TABLESPACE INDEX01;
create index WEBLOG_ENTRY_IDX7 on PN_WEBLOG_ENTRY (WEBLOG_ID, PUB_TIME, STATUS) TABLESPACE INDEX01;

prompt
prompt Creating table PN_WEBLOG_COMMENT
prompt ================================
prompt
create table PN_WEBLOG_COMMENT
(
  COMMENT_ID      NUMBER(20) not null,
  WEBLOG_ENTRY_ID NUMBER(20) not null,
  NAME            VARCHAR2(240),
  EMAIL           VARCHAR2(240),
  URL             VARCHAR2(240),
  CONTENT         CLOB,
  POST_TIME       DATE not null,
  NOTIFY          NUMBER(1) default 0 not null,
  REMOTE_HOST     VARCHAR2(128),
  REFERRER        VARCHAR2(240),
  USER_AGENT      VARCHAR2(240),
  STATUS          VARCHAR2(20) not null,
  CONTENT_TYPE    VARCHAR2(128) default 'text/plain' not null
)
;
alter table PN_WEBLOG_COMMENT
  add constraint WEBLOG_COMMENT_PK primary key (COMMENT_ID);
alter table PN_WEBLOG_COMMENT
  add constraint WEBLOG_COMMENT_ENTRY_FK foreign key (WEBLOG_ENTRY_ID)
  references PN_WEBLOG_ENTRY (WEBLOG_ENTRY_ID);
create index WEBLOG_COMMENT_IDX1 on PN_WEBLOG_COMMENT (WEBLOG_ENTRY_ID) TABLESPACE INDEX01;
create index WEBLOG_COMMENT_IDX2 on PN_WEBLOG_COMMENT (STATUS) TABLESPACE INDEX01;

prompt
prompt Creating table PN_WEBLOG_ENTRY_ATTRIBUTE
prompt ========================================
prompt
create table PN_WEBLOG_ENTRY_ATTRIBUTE
(
  WEBLOG_ENTRY_ATTRIBUTE_ID NUMBER(20) not null,
  WEBLOG_ENTRY_ID           NUMBER(20) not null,
  NAME                      VARCHAR2(240) not null,
  VALUE                     VARCHAR2(240) not null,
  IS_IMPORTANT              NUMBER(1) not null
)
;
alter table PN_WEBLOG_ENTRY_ATTRIBUTE
  add constraint ENTRY_ATTRIBUTE_PK primary key (WEBLOG_ENTRY_ATTRIBUTE_ID);
alter table PN_WEBLOG_ENTRY_ATTRIBUTE
  add constraint ENTRY_ATTRIBUTE_NAME_UQ unique (WEBLOG_ENTRY_ID, NAME);
alter table PN_WEBLOG_ENTRY_ATTRIBUTE
  add constraint ENTRY_ATTRIBUTE_FK foreign key (WEBLOG_ENTRY_ID)
  references PN_WEBLOG_ENTRY (WEBLOG_ENTRY_ID);
create index ENTRY_ATTRIBUTE_IDX1 on PN_WEBLOG_ENTRY_ATTRIBUTE (WEBLOG_ENTRY_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_WORKFLOW_RULE_STATUS
prompt ======================================
prompt
create table PN_WORKFLOW_RULE_STATUS
(
  RULE_STATUS_ID     NUMBER(20) not null,
  STATUS_NAME        VARCHAR2(80),
  STATUS_DESCRIPTION VARCHAR2(500),
  CREATED_BY_ID      NUMBER(20) not null,
  CREATED_DATETIME   DATE not null,
  MODIFIED_BY_ID     NUMBER(20),
  MODIFIED_DATETIME  DATE,
  CRC                DATE not null,
  RECORD_STATUS      VARCHAR2(1) not null
)
;
alter table PN_WORKFLOW_RULE_STATUS
  add constraint WORKFLOW_RULE_STATUS_PK primary key (RULE_STATUS_ID);

prompt
prompt Creating table PN_WORKFLOW_RULE_TYPE
prompt ====================================
prompt
create table PN_WORKFLOW_RULE_TYPE
(
  TABLE_NAME            VARCHAR2(80) not null,
  RULE_TYPE_ID          NUMBER(20) not null,
  RULE_TYPE_NAME        VARCHAR2(80),
  RULE_TYPE_DESCRIPTION VARCHAR2(500),
  NOTES                 VARCHAR2(4000),
  CREATED_BY_ID         NUMBER(20) not null,
  CREATED_DATETIME      DATE not null,
  MODIFIED_BY_ID        NUMBER(20),
  MODIFIED_DATETIME     DATE,
  CRC                   DATE not null,
  RECORD_STATUS         VARCHAR2(1) not null
)
;
alter table PN_WORKFLOW_RULE_TYPE
  add constraint WORKFLOW_RULE_TYPE_PK primary key (RULE_TYPE_ID);

prompt
prompt Creating table PN_WORKFLOW_RULE
prompt ===============================
prompt
create table PN_WORKFLOW_RULE
(
  RULE_STATUS_ID    NUMBER(20) not null,
  RULE_ID           NUMBER(20) not null,
  RULE_TYPE_ID      NUMBER(20) not null,
  TRANSITION_ID     NUMBER(20) not null,
  WORKFLOW_ID       NUMBER(20) not null,
  RULE_NAME         VARCHAR2(80),
  RULE_DESCRIPTION  VARCHAR2(500),
  NOTES             VARCHAR2(4000),
  CREATED_BY_ID     NUMBER(20) not null,
  CREATED_DATETIME  DATE not null,
  MODIFIED_BY_ID    NUMBER(20),
  MODIFIED_DATETIME DATE,
  CRC               DATE not null,
  RECORD_STATUS     VARCHAR2(1) not null
)
;
alter table PN_WORKFLOW_RULE
  add constraint WORKFLOW_RULE_PK primary key (RULE_ID, WORKFLOW_ID, TRANSITION_ID);
alter table PN_WORKFLOW_RULE
  add constraint WORKFLOW_RULE_TO_RULESTATUS_FK foreign key (RULE_STATUS_ID)
  references PN_WORKFLOW_RULE_STATUS (RULE_STATUS_ID);
alter table PN_WORKFLOW_RULE
  add constraint WORKFLOW_RULE_TO_RULE_TYPE_FK foreign key (RULE_TYPE_ID)
  references PN_WORKFLOW_RULE_TYPE (RULE_TYPE_ID);
alter table PN_WORKFLOW_RULE
  add constraint WORKFLOW_RULE_TO_TRANSITION_FK foreign key (TRANSITION_ID, WORKFLOW_ID)
  references PN_WORKFLOW_TRANSITION (TRANSITION_ID, WORKFLOW_ID);

prompt
prompt Creating table PN_WF_RULE_AUTH
prompt ==============================
prompt
create table PN_WF_RULE_AUTH
(
  RULE_ID       NUMBER(20) not null,
  WORKFLOW_ID   NUMBER(20) not null,
  TRANSITION_ID NUMBER(20) not null,
  CRC           DATE not null,
  RECORD_STATUS VARCHAR2(1) not null
)
;
alter table PN_WF_RULE_AUTH
  add constraint WF_RULE_AUTH_PK primary key (RULE_ID, WORKFLOW_ID, TRANSITION_ID);
alter table PN_WF_RULE_AUTH
  add constraint WF_RULE_AUTH_TO_WRKFLW_RULE_FK foreign key (RULE_ID, WORKFLOW_ID, TRANSITION_ID)
  references PN_WORKFLOW_RULE (RULE_ID, WORKFLOW_ID, TRANSITION_ID);

prompt
prompt Creating table PN_WORKFLOW_STEP_HAS_GROUP
prompt =========================================
prompt
create table PN_WORKFLOW_STEP_HAS_GROUP
(
  GROUP_ID          NUMBER(20) not null,
  STEP_ID           NUMBER(20) not null,
  WORKFLOW_ID       NUMBER(20) not null,
  IS_PARTICIPANT    NUMBER(1) not null,
  CREATED_BY_ID     NUMBER(20) not null,
  CREATED_DATETIME  DATE not null,
  MODIFIED_BY_ID    NUMBER(20),
  MODIFIED_DATETIME DATE,
  CRC               DATE not null,
  RECORD_STATUS     VARCHAR2(1) not null
)
;
alter table PN_WORKFLOW_STEP_HAS_GROUP
  add constraint WORKFLOW_STEP_HAS_GROUP_PK primary key (WORKFLOW_ID, GROUP_ID, STEP_ID);
alter table PN_WORKFLOW_STEP_HAS_GROUP
  add constraint WORKFLOW_STEPGROUP_TO_GROUP_FK foreign key (GROUP_ID)
  references PN_GROUP (GROUP_ID);
alter table PN_WORKFLOW_STEP_HAS_GROUP
  add constraint WORKFLOW_STEP_GROUP_TO_STEP_FK foreign key (STEP_ID, WORKFLOW_ID)
  references PN_WORKFLOW_STEP (STEP_ID, WORKFLOW_ID);
create unique index WORKFLOW_STEP_HAS_GROUP_IDX1 on PN_WORKFLOW_STEP_HAS_GROUP (STEP_ID, GROUP_ID);

prompt
prompt Creating table PN_WF_RULE_AUTH_HAS_GROUP
prompt ========================================
prompt
create table PN_WF_RULE_AUTH_HAS_GROUP
(
  RULE_ID       NUMBER(20) not null,
  WORKFLOW_ID   NUMBER(20) not null,
  TRANSITION_ID NUMBER(20) not null,
  GROUP_ID      NUMBER(20) not null,
  STEP_ID       NUMBER(20) not null
)
;
alter table PN_WF_RULE_AUTH_HAS_GROUP
  add constraint WF_RULE_AUTH_HAS_GROUP_PK primary key (RULE_ID, WORKFLOW_ID, TRANSITION_ID, GROUP_ID, STEP_ID);
alter table PN_WF_RULE_AUTH_HAS_GROUP
  add constraint WF_RULE_AUTHGRP_TO_RULEAUTH_FK foreign key (RULE_ID, WORKFLOW_ID, TRANSITION_ID)
  references PN_WF_RULE_AUTH (RULE_ID, WORKFLOW_ID, TRANSITION_ID);
alter table PN_WF_RULE_AUTH_HAS_GROUP
  add constraint WF_RULE_AUTH_HAS_GROUP_FK1 foreign key (WORKFLOW_ID, GROUP_ID, STEP_ID)
  references PN_WORKFLOW_STEP_HAS_GROUP (WORKFLOW_ID, GROUP_ID, STEP_ID);


prompt
prompt Creating table PN_WIKI_PAGE
prompt ===========================
prompt
create table PN_WIKI_PAGE
(
  WIKI_PAGE_ID     NUMBER(20) not null,
  PAGE_NAME        VARCHAR2(240) not null,
  CONTENT          CLOB not null,
  PARENT_PAGE_NAME VARCHAR2(240),
  EDIT_DATE        DATE not null,
  EDITED_BY        NUMBER(20) not null,
  OWNER_OBJECT_ID  NUMBER(20) not null
)
;
alter table PN_WIKI_PAGE
  add constraint WIKI_PAGE_PK primary key (WIKI_PAGE_ID);
alter table PN_WIKI_PAGE
  add constraint WIKI_PAGE_UQ unique (PAGE_NAME, OWNER_OBJECT_ID);
alter table PN_WIKI_PAGE
  add constraint WIKI_PAGE_EDITED_BY_FK foreign key (EDITED_BY)
  references PN_PERSON (PERSON_ID);
alter table PN_WIKI_PAGE
  add constraint WIKI_PAGE_OWNER_OBJECT_FK foreign key (OWNER_OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
create index WIKI_PAGE_IDX1 on PN_WIKI_PAGE (OWNER_OBJECT_ID) TABLESPACE INDEX01;
create index WIKI_PAGE_IDX2 on PN_WIKI_PAGE (EDITED_BY) TABLESPACE INDEX01;

prompt
prompt Creating table PN_WIKI_HISTORY
prompt ==============================
prompt
create table PN_WIKI_HISTORY
(
  WIKI_HISTORY_ID NUMBER(20) not null,
  WIKI_PAGE_ID    NUMBER(20) not null,
  CONTENT         CLOB not null,
  EDIT_DATE       DATE not null,
  EDITED_BY       NUMBER(20) not null,
  COMMENT_TEXT    CLOB not null
)
;
alter table PN_WIKI_HISTORY
  add constraint WIKI_HISTORY_PK primary key (WIKI_HISTORY_ID);
alter table PN_WIKI_HISTORY
  add constraint WIKI_HISTORY_EDITED_BY_FK foreign key (EDITED_BY)
  references PN_PERSON (PERSON_ID);
alter table PN_WIKI_HISTORY
  add constraint WIKI_HISTORY_PAGE_ID_FK foreign key (WIKI_PAGE_ID)
  references PN_WIKI_PAGE (WIKI_PAGE_ID);
create index WIKI_HISTORY_IDX1 on PN_WIKI_HISTORY (EDIT_DATE) TABLESPACE INDEX01;

/* table created in the patch afterwards
prompt
prompt Creating table PN_WIKI_ATTACHMENT
prompt ==============================
prompt
create table PNET.PN_WIKI_ATTACHMENT
(
	WIKI_ATTACHMENT_ID NUMBER(20,0) not null, 
	WIKI_PAGE_ID NUMBER(20,0) not null, 
	ATTACHMENT_NAME VARCHAR2(240 BYTE) not null,
	FILE_ID NUMBER(20,0) not null, 
	ATTACHED_ON_DATE DATE not null, 
	ATTACHED_BY NUMBER(20,0) not null,
	DESCRIPTION CLOB, 
	RECORD_STATUS VARCHAR2(1) DEFAULT 'A'
)
;
alter table PN_WIKI_ATTACHMENT
	 add constraint WIKI_ATTACHMENT_PK primary key (WIKI_ATTACHMENT_ID);
alter table PN_WIKI_ATTACHMENT
	 add constraint WIKI_ATTACHMENT_WP_ID_FK foreign key (WIKI_PAGE_ID)
	 references PNET.PN_WIKI_PAGE (WIKI_PAGE_ID);
alter table PN_WIKI_ATTACHMENT
	 add constraint WIKI_ATTACHMENT_A_BY_FK FOREIGN KEY (ATTACHED_BY)
	 references PNET.PN_PERSON (PERSON_ID);
alter table PN_WIKI_ATTACHMENT
	 add constraint WIKI_ATTACHMENT_UQ unique (WIKI_PAGE_ID, ATTACHMENT_NAME);
*/

prompt
prompt Creating table PN_WORKFLOW_HAS_OBJECT_TYPE
prompt ==========================================
prompt
create table PN_WORKFLOW_HAS_OBJECT_TYPE
(
  WORKFLOW_ID NUMBER(20) not null,
  OBJECT_TYPE VARCHAR2(80) not null,
  SUB_TYPE_ID VARCHAR2(80)
)
;
alter table PN_WORKFLOW_HAS_OBJECT_TYPE
  add constraint WORKFLOW_HAS_OBJECT_TYPE_PK primary key (WORKFLOW_ID, OBJECT_TYPE);
alter table PN_WORKFLOW_HAS_OBJECT_TYPE
  add constraint WORKFLOW_OBJECT_TYPE_TO_WF_FK foreign key (WORKFLOW_ID)
  references PN_WORKFLOW (WORKFLOW_ID);
alter table PN_WORKFLOW_HAS_OBJECT_TYPE
  add constraint WORKFLOW_OBJTYPE_TO_OBJTYPE_FK foreign key (OBJECT_TYPE)
  references PN_OBJECT_TYPE (OBJECT_TYPE);

prompt
prompt Creating table PN_WORKFLOW_STEP_COPY
prompt ====================================
prompt
create table PN_WORKFLOW_STEP_COPY
(
  WORKFLOW_ID    NUMBER(20) not null,
  STEP_ID        NUMBER(20) not null,
  TO_WORKFLOW_ID NUMBER(20) not null,
  TO_STEP_ID     NUMBER(20) not null
)
;
alter table PN_WORKFLOW_STEP_COPY
  add constraint PN_WORKFLOW_STEP_COPY_PK primary key (STEP_ID, TO_WORKFLOW_ID, TO_STEP_ID);

prompt
prompt Creating table PN_WORKINGTIME_CALENDAR
prompt ======================================
prompt
create table PN_WORKINGTIME_CALENDAR
(
  CALENDAR_ID        NUMBER(20) not null,
  PLAN_ID            NUMBER(20) not null,
  IS_BASE_CALENDAR   VARCHAR2(1) not null,
  NAME               VARCHAR2(255),
  PARENT_CALENDAR_ID NUMBER(20),
  RESOURCE_PERSON_ID NUMBER(20),
  RECORD_STATUS      VARCHAR2(1) not null
)
;
alter table PN_WORKINGTIME_CALENDAR
  add constraint PN_WORKINGTIME_CALENDAR_PK primary key (CALENDAR_ID);
alter table PN_WORKINGTIME_CALENDAR
  add constraint PN_WORKINGTIME_CALENDAR_FK1 foreign key (PLAN_ID)
  references PN_PLAN (PLAN_ID);
alter table PN_WORKINGTIME_CALENDAR
  add constraint PN_WORKINGTIME_CALENDAR_FK2 foreign key (PARENT_CALENDAR_ID)
  references PN_WORKINGTIME_CALENDAR (CALENDAR_ID);
alter table PN_WORKINGTIME_CALENDAR
  add constraint PN_WORKINGTIME_CALENDAR_FK3 foreign key (RESOURCE_PERSON_ID)
  references PN_PERSON (PERSON_ID);
create index PN_WORKINGTIME_CALENDAR_IDX1 on PN_WORKINGTIME_CALENDAR (PLAN_ID) TABLESPACE INDEX01;

prompt
prompt Creating table PN_WORKINGTIME_CALENDAR_ENTRY
prompt ============================================
prompt
create table PN_WORKINGTIME_CALENDAR_ENTRY
(
  CALENDAR_ID    NUMBER(20) not null,
  ENTRY_ID       NUMBER(20) not null,
  IS_WORKING_DAY VARCHAR2(1) not null,
  IS_DAY_OF_WEEK VARCHAR2(1) not null,
  DAY_NUMBER     NUMBER(1),
  START_DATE     DATE,
  END_DATE       DATE,
  TIME1_START    DATE,
  TIME1_END      DATE,
  TIME2_START    DATE,
  TIME2_END      DATE,
  TIME3_START    DATE,
  TIME3_END      DATE,
  TIME4_START    DATE,
  TIME4_END      DATE,
  TIME5_START    DATE,
  TIME5_END      DATE,
  RECORD_STATUS  VARCHAR2(1) not null
)
;
alter table PN_WORKINGTIME_CALENDAR_ENTRY
  add constraint PN_WORKINGTIME_CAL_ENTRY_PK primary key (CALENDAR_ID, ENTRY_ID);
alter table PN_WORKINGTIME_CALENDAR_ENTRY
  add constraint PN_WORKINGTIME_CAL_ENTRY_FK1 foreign key (CALENDAR_ID)
  references PN_WORKINGTIME_CALENDAR (CALENDAR_ID);

prompt
prompt Creating table PRODUCT_VERSION
prompt ==============================
prompt
create table PRODUCT_VERSION
(
  PRODUCT           VARCHAR2(40) not null,
  MAJOR_VERSION     NUMBER(3) not null,
  MINOR_VERSION     NUMBER(3) not null,
  SUB_MINOR_VERSION NUMBER(3) not null,
  BUILD_VERSION     NUMBER(3) not null,
  TIMESTAMP         DATE not null,
  DESCRIPTION       VARCHAR2(1000) not null
)
;
alter table PRODUCT_VERSION
  add constraint PRODUCT_VERSION_PK primary key (PRODUCT, MAJOR_VERSION, MINOR_VERSION, SUB_MINOR_VERSION, BUILD_VERSION);

prompt
prompt Creating table QRTZ_JOB_DETAILS
prompt ===============================
prompt
create table QRTZ_JOB_DETAILS
(
  JOB_NAME          VARCHAR2(80) not null,
  JOB_GROUP         VARCHAR2(80) not null,
  DESCRIPTION       VARCHAR2(120),
  JOB_CLASS_NAME    VARCHAR2(128) not null,
  IS_DURABLE        VARCHAR2(1) not null,
  IS_VOLATILE       VARCHAR2(1) not null,
  IS_STATEFUL       VARCHAR2(1) not null,
  REQUESTS_RECOVERY VARCHAR2(1) not null,
  JOB_DATA          BLOB
)
;
alter table QRTZ_JOB_DETAILS
  add primary key (JOB_NAME, JOB_GROUP);
create index IDX_QRTZ_J_REQ_RECOVERY on QRTZ_JOB_DETAILS (REQUESTS_RECOVERY) TABLESPACE INDEX01;

prompt
prompt Creating table QRTZ_TRIGGERS
prompt ============================
prompt
create table QRTZ_TRIGGERS
(
  TRIGGER_NAME   VARCHAR2(80) not null,
  TRIGGER_GROUP  VARCHAR2(80) not null,
  JOB_NAME       VARCHAR2(80) not null,
  JOB_GROUP      VARCHAR2(80) not null,
  IS_VOLATILE    VARCHAR2(1) not null,
  DESCRIPTION    VARCHAR2(120),
  NEXT_FIRE_TIME NUMBER(13),
  PREV_FIRE_TIME NUMBER(13),
  TRIGGER_STATE  VARCHAR2(16) not null,
  TRIGGER_TYPE   VARCHAR2(8) not null,
  START_TIME     NUMBER(13) not null,
  END_TIME       NUMBER(13),
  CALENDAR_NAME  VARCHAR2(80),
  MISFIRE_INSTR  NUMBER(2)
)
;
alter table QRTZ_TRIGGERS
  add primary key (TRIGGER_NAME, TRIGGER_GROUP);
alter table QRTZ_TRIGGERS
  add foreign key (JOB_NAME, JOB_GROUP)
  references QRTZ_JOB_DETAILS (JOB_NAME, JOB_GROUP);
create index IDX_QRTZ_T_NEXT_FIRE_TIME on QRTZ_TRIGGERS (NEXT_FIRE_TIME) TABLESPACE INDEX01;
create index IDX_QRTZ_T_NEXT_STATE on QRTZ_TRIGGERS (TRIGGER_STATE) TABLESPACE INDEX01;
create index IDX_QRTZ_T_VOLATILE on QRTZ_TRIGGERS (IS_VOLATILE) TABLESPACE INDEX01;

prompt
prompt Creating table QRTZ_BLOB_TRIGGERS
prompt =================================
prompt
create table QRTZ_BLOB_TRIGGERS
(
  TRIGGER_NAME  VARCHAR2(80) not null,
  TRIGGER_GROUP VARCHAR2(80) not null,
  BLOB_DATA     BLOB
)
;
alter table QRTZ_BLOB_TRIGGERS
  add primary key (TRIGGER_NAME, TRIGGER_GROUP);
alter table QRTZ_BLOB_TRIGGERS
  add foreign key (TRIGGER_NAME, TRIGGER_GROUP)
  references QRTZ_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

prompt
prompt Creating table QRTZ_CALENDARS
prompt =============================
prompt
create table QRTZ_CALENDARS
(
  CALENDAR_NAME VARCHAR2(80) not null,
  CALENDAR      BLOB not null
)
;
alter table QRTZ_CALENDARS
  add primary key (CALENDAR_NAME);

prompt
prompt Creating table QRTZ_CRON_TRIGGERS
prompt =================================
prompt
create table QRTZ_CRON_TRIGGERS
(
  TRIGGER_NAME    VARCHAR2(80) not null,
  TRIGGER_GROUP   VARCHAR2(80) not null,
  CRON_EXPRESSION VARCHAR2(80) not null,
  TIME_ZONE_ID    VARCHAR2(80)
)
;
alter table QRTZ_CRON_TRIGGERS
  add primary key (TRIGGER_NAME, TRIGGER_GROUP);
alter table QRTZ_CRON_TRIGGERS
  add foreign key (TRIGGER_NAME, TRIGGER_GROUP)
  references QRTZ_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

prompt
prompt Creating table QRTZ_FIRED_TRIGGERS
prompt ==================================
prompt
create table QRTZ_FIRED_TRIGGERS
(
  ENTRY_ID          VARCHAR2(95) not null,
  TRIGGER_NAME      VARCHAR2(80) not null,
  TRIGGER_GROUP     VARCHAR2(80) not null,
  IS_VOLATILE       VARCHAR2(1) not null,
  INSTANCE_NAME     VARCHAR2(80) not null,
  FIRED_TIME        NUMBER(13) not null,
  STATE             VARCHAR2(16) not null,
  JOB_NAME          VARCHAR2(80),
  JOB_GROUP         VARCHAR2(80),
  IS_STATEFUL       VARCHAR2(1),
  REQUESTS_RECOVERY VARCHAR2(1)
)
;
alter table QRTZ_FIRED_TRIGGERS
  add primary key (ENTRY_ID);
create index IDX_QRTZ_FT_JOB_GROUP on QRTZ_FIRED_TRIGGERS (JOB_GROUP) TABLESPACE INDEX01;
create index IDX_QRTZ_FT_JOB_NAME on QRTZ_FIRED_TRIGGERS (JOB_NAME) TABLESPACE INDEX01;
create index IDX_QRTZ_FT_JOB_REQ_RECOVERY on QRTZ_FIRED_TRIGGERS (REQUESTS_RECOVERY) TABLESPACE INDEX01;
create index IDX_QRTZ_FT_JOB_STATEFUL on QRTZ_FIRED_TRIGGERS (IS_STATEFUL) TABLESPACE INDEX01;
create index IDX_QRTZ_FT_TRIG_GROUP on QRTZ_FIRED_TRIGGERS (TRIGGER_GROUP) TABLESPACE INDEX01;
create index IDX_QRTZ_FT_TRIG_INST_NAME on QRTZ_FIRED_TRIGGERS (INSTANCE_NAME) TABLESPACE INDEX01;
create index IDX_QRTZ_FT_TRIG_NAME on QRTZ_FIRED_TRIGGERS (TRIGGER_NAME) TABLESPACE INDEX01;
create index IDX_QRTZ_FT_TRIG_VOLATILE on QRTZ_FIRED_TRIGGERS (IS_VOLATILE) TABLESPACE INDEX01;

prompt
prompt Creating table QRTZ_JOB_LISTENERS
prompt =================================
prompt
create table QRTZ_JOB_LISTENERS
(
  JOB_NAME     VARCHAR2(80) not null,
  JOB_GROUP    VARCHAR2(80) not null,
  JOB_LISTENER VARCHAR2(80) not null
)
;
alter table QRTZ_JOB_LISTENERS
  add primary key (JOB_NAME, JOB_GROUP, JOB_LISTENER);
alter table QRTZ_JOB_LISTENERS
  add foreign key (JOB_NAME, JOB_GROUP)
  references QRTZ_JOB_DETAILS (JOB_NAME, JOB_GROUP);

prompt
prompt Creating table QRTZ_LOCKS
prompt =========================
prompt
create table QRTZ_LOCKS
(
  LOCK_NAME VARCHAR2(40) not null
)
;
alter table QRTZ_LOCKS
  add primary key (LOCK_NAME);

prompt
prompt Creating table QRTZ_PAUSED_TRIGGER_GRPS
prompt =======================================
prompt
create table QRTZ_PAUSED_TRIGGER_GRPS
(
  TRIGGER_GROUP VARCHAR2(80) not null
)
;
alter table QRTZ_PAUSED_TRIGGER_GRPS
  add primary key (TRIGGER_GROUP);

prompt
prompt Creating table QRTZ_SCHEDULER_STATE
prompt ===================================
prompt
create table QRTZ_SCHEDULER_STATE
(
  INSTANCE_NAME     VARCHAR2(80) not null,
  LAST_CHECKIN_TIME NUMBER(13) not null,
  CHECKIN_INTERVAL  NUMBER(13) not null,
  RECOVERER         VARCHAR2(80)
)
;
alter table QRTZ_SCHEDULER_STATE
  add primary key (INSTANCE_NAME);

prompt
prompt Creating table QRTZ_SIMPLE_TRIGGERS
prompt ===================================
prompt
create table QRTZ_SIMPLE_TRIGGERS
(
  TRIGGER_NAME    VARCHAR2(80) not null,
  TRIGGER_GROUP   VARCHAR2(80) not null,
  REPEAT_COUNT    NUMBER(7) not null,
  REPEAT_INTERVAL NUMBER(12) not null,
  TIMES_TRIGGERED NUMBER(7) not null
)
;
alter table QRTZ_SIMPLE_TRIGGERS
  add primary key (TRIGGER_NAME, TRIGGER_GROUP);
alter table QRTZ_SIMPLE_TRIGGERS
  add foreign key (TRIGGER_NAME, TRIGGER_GROUP)
  references QRTZ_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

prompt
prompt Creating table QRTZ_TRIGGER_LISTENERS
prompt =====================================
prompt
create table QRTZ_TRIGGER_LISTENERS
(
  TRIGGER_NAME     VARCHAR2(80) not null,
  TRIGGER_GROUP    VARCHAR2(80) not null,
  TRIGGER_LISTENER VARCHAR2(80) not null
)
;
alter table QRTZ_TRIGGER_LISTENERS
  add primary key (TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_LISTENER);
alter table QRTZ_TRIGGER_LISTENERS
  add foreign key (TRIGGER_NAME, TRIGGER_GROUP)
  references QRTZ_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

prompt
prompt Creating table STATUS_MESSAGES
prompt ==============================
prompt
create table STATUS_MESSAGES
(
  MESSAGE_ID       NUMBER(4) not null,
  TITLE            VARCHAR2(30) not null,
  MESSAGE          VARCHAR2(500) not null,
  ACTIVE_INDICATOR VARCHAR2(1) not null,
  TIMESTAMP        DATE not null
)
;
alter table STATUS_MESSAGES
  add constraint STATUS_MESSAGES_PK primary key (MESSAGE_ID);
alter table STATUS_MESSAGES
  add constraint ACTIVE_INDICATOR_VALID
  check (active_indicator in ('A','I'));

prompt
prompt Creating table TMP_PN_PERSON_212
prompt ================================
prompt
create table TMP_PN_PERSON_212
(
  PERSON_ID               NUMBER(20) not null,
  USERNAME                VARCHAR2(32) not null,
  PASSWORD                VARCHAR2(60) not null,
  JOG_QUESTION            VARCHAR2(240) not null,
  JOG_ANSWER              VARCHAR2(240) not null,
  EMAIL                   VARCHAR2(240) not null,
  PREFIX_NAME             VARCHAR2(20),
  FIRST_NAME              VARCHAR2(40) not null,
  MIDDLE_NAME             VARCHAR2(40),
  LAST_NAME               VARCHAR2(60) not null,
  SECOND_LAST_NAME        VARCHAR2(60),
  SUFFIX_NAME             VARCHAR2(20),
  DISPLAY_NAME            VARCHAR2(240) not null,
  COMPANY_NAME            VARCHAR2(120),
  COMPANY_DIVISION        VARCHAR2(120),
  JOB_DESCRIPTION_CODE    NUMBER(4),
  ADDRESS_ID              NUMBER(20) not null,
  USER_STATUS             VARCHAR2(80) not null,
  LANGUAGE_CODE           VARCHAR2(80),
  TIMEZONE_CODE           VARCHAR2(3) not null,
  DATE_FORMAT_ID          NUMBER(20) not null,
  MEMBERSHIP_PORTFOLIO_ID NUMBER(20) not null,
  PERSONAL_SPACE_NAME     VARCHAR2(240) not null,
  VERIFICATION_CODE       VARCHAR2(80),
  RECORD_STATUS           VARCHAR2(1) not null
)
;

prompt
prompt Creating table TMP_PN_PROPERTY
prompt ==============================
prompt
create table TMP_PN_PROPERTY
(
  CONTEXT_ID               NUMBER(20) not null,
  LANGUAGE                 VARCHAR2(2) not null,
  PROPERTY_TYPE            VARCHAR2(40) not null,
  PROPERTY                 VARCHAR2(500) not null,
  PROPERTY_VALUE           VARCHAR2(4000),
  RECORD_STATUS            VARCHAR2(1),
  IS_SYSTEM_PROPERTY       NUMBER(1) default 0 not null,
  IS_TRANSLATABLE_PROPERTY NUMBER(1) default 0 not null
)
;
comment on table TMP_PN_PROPERTY
  is 'temporary table for updating system properties';

prompt
prompt Adding more foreign keys for PN_OBJECT....
prompt ==============================
prompt
alter table PN_OBJECT
  add constraint OBJECT_FK1 foreign key (CREATED_BY)
  references PN_PERSON (PERSON_ID);
alter table PN_OBJECT
  add constraint OBJECT_FK2 foreign key (OBJECT_TYPE)
  references PN_OBJECT_TYPE (OBJECT_TYPE);
prompt
prompt Adding more foreign keys for PN_PHASE....
prompt ==============================
prompt
alter table PN_PHASE
  add constraint PHASE_FK1 foreign key (PROCESS_ID)
  references PN_PROCESS (PROCESS_ID);


prompt
prompt Creating table PN_TIMELOG
prompt ========================
prompt
create table PN_TIMELOG
(
  TIMELOG_ID NUMBER(20,0) NOT NULL ENABLE, 
  OBJECT_ID NUMBER(20,0) NOT NULL ENABLE, 
  PERSON_ID NUMBER(20,0) NOT NULL ENABLE, 
  FROM_DATE DATE NOT NULL ENABLE, 
  TO_DATE DATE NOT NULL ENABLE, 
  DATE_SUBMITTED DATE NOT NULL ENABLE, 
  RECORD_STATUS VARCHAR2(1 BYTE) NOT NULL ENABLE 
)
;
alter table PN_TIMELOG
  add constraint PN_TIMELOG_PK primary key (TIMELOG_ID);
alter table PN_TIMELOG
  add constraint PN_TIMELOG_PN_OBJECT_FK1 foreign key (OBJECT_ID)
  references PN_OBJECT (OBJECT_ID);
alter table PN_TIMELOG
  add constraint PN_TIMELOG_PN_PERSON_FK1 foreign key (PERSON_ID)
  references PN_PERSON (PERSON_ID);
create index TIMELOG_IDX1 on PN_TIMELOG (PERSON_ID) TABLESPACE INDEX01;
create index TIMELOG_IDX2 on PN_TIMELOG (OBJECT_ID) TABLESPACE INDEX01;

/*
prompt
prompt Creating table PN_PROJECT_SPACE_META_COMBO
prompt ======================================
prompt
create table PN_PROJECT_SPACE_META_COMBO
(	
	PROPERTY_ID		NUMBER(3) not null,
	COMBO_VALUE 	VARCHAR2(500) not null,
	COMBO_LABEL 	VARCHAR2(500) not null
) ;

alter table PN_PROJECT_SPACE_META_COMBO
  add constraint PN_PROJECT_SPACE_META_COMBO_PK primary key (PROPERTY_ID, COMBO_VALUE);
*/