CREATE TABLE PN_FINANCIAL_SPACE (
  FINANCIAL_SPACE_ID number(20),
  FINANCIAL_SPACE_NAME varchar(40),
  FINANCIAL_SPACE_DESCRIPTION varchar(240),
  RECORD_STATUS varchar(1),
  constraint FINANCIAL_SPACE_PK primary key (FINANCIAL_SPACE_ID),
  constraint RECORD_STATUS_FS1 check (RECORD_STATUS in ('A','D'))
);
CREATE OR REPLACE SYNONYM PNET_USER.PN_FINANCIAL_SPACE FOR PNET.PN_FINANCIAL_SPACE;

CREATE TABLE PN_PERSON_SALARY (
  PERSON_SALARY_ID number(20),
  PERSON_ID number(20),
  START_DATE date,
  END_DATE date,
  COST_BY_HOUR number(20,2),
  RECORD_STATUS varchar(1),
  constraint PERSON_SALARY_PK primary key (PERSON_SALARY_ID),
  constraint PERSON_SALARY_FK FOREIGN KEY (PERSON_ID) REFERENCES PN_PERSON(PERSON_ID),
  constraint RECORD_STATUS_PS1 check (RECORD_STATUS in ('A','D'))
);
CREATE OR REPLACE SYNONYM PNET_USER.PN_PERSON_SALARY FOR PNET.PN_PERSON_SALARY;

CREATE OR REPLACE SYNONYM PNET_USER.FINANCIAL FOR PNET.FINANCIAL;

CREATE OR REPLACE FORCE VIEW "PNET"."PN_SPACE_VIEW" ("SPACE_ID", "SPACE_NAME", "SPACE_DESC", "SPACE_TYPE", "RECORD_STATUS", "PARENT_SPACE_NAME")
AS
  SELECT person_id space_id,
    display_name space_name ,
    NULL space_desc ,
    'person' space_type ,
    record_status,
    NULL parent_space_name
  FROM pn_person
  UNION
  SELECT business_id space_id,
    business_name space_name,
    business_desc space_desc ,
    'business' space_type,
    record_status,
    NULL parent_space_name
  FROM pn_business
  UNION
  SELECT project_id space_id,
    project_name space_name,
    project_desc space_desc ,
    'project' space_type,
    record_status,
    NULL parent_space_name
  FROM pn_project_space
  UNION
  select financial_space_id space_id, financial_space_name space_name, '' space_desc ,  'financial' space_type,  record_status, NULL parent_space_name
from pn_financial_space
  UNION
  SELECT m.methodology_id space_id,
    m.methodology_name space_name,
    m.methodology_desc space_desc ,
    'methodology' space_type,
    m.record_status,
    DECODE(b.business_id,NULL,'Personal',b.business_name) parent_space_name
  FROM pn_methodology_space m,
    pn_space_has_space s,
    pn_business b
  WHERE m.methodology_id = s.child_space_id
  AND s.parent_space_id  = b.business_id(+)
  UNION
  SELECT application_id space_id,
    application_name space_name,
    application_desc space_desc ,
    'application' space_type,
    record_status,
    NULL parent_space_name
  FROM pn_application_space
  UNION
  SELECT configuration_id space_id,
    configuration_name space_name,
    configuration_desc space_desc ,
    'configuration' space_type,
    record_status,
    NULL parent_space_name
  FROM pn_configuration_space
  UNION
  SELECT 5,
    '@prm.enterprise.space.type.enterprise.name',
    '',
    'enterprise',
    'A',
    ''
  FROM dual
  UNION
  SELECT 10,
    '@prm.resource.space.type.resource.name',
    '',
    'resources',
    'A',
    ''
  FROM dual;

CREATE OR REPLACE SYNONYM PNET_USER.PN_SPACE_VIEW FOR PNET.PN_SPACE_VIEW;
