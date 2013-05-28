CREATE TABLE PN_PROJECT_FINANCIAL (
  PROJECT_ID number(20),
  COST_CALCULATION_METHOD varchar(20),
  EXTRA_COSTS number(20,2),
  constraint PROJECT_FINANCIAL_PK primary key (PROJECT_ID),
  constraint PROJECT_FINANCIAL_FK FOREIGN KEY (PROJECT_ID) REFERENCES PN_PROJECT_SPACE(PROJECT_ID)
);
CREATE OR REPLACE SYNONYM PNET_USER.PN_PROJECT_FINANCIAL FOR PNET.PN_PROJECT_FINANCIAL;

CREATE TABLE PN_FINANCIAL_SPACE (
  FINANCIAL_SPACE_ID number(20),
  FINANCIAL_SPACE_NAME varchar(40),
  RECORD_STATUS varchar(1),
  constraint FINANCIAL_SPACE_PK primary key (FINANCIAL_SPACE_ID),
  constraint RECORD_STATUS_FS1 check (RECORD_STATUS in ('A','D'))
);
CREATE OR REPLACE SYNONYM PNET_USER.PN_FINANCIAL_SPACE FOR PNET.PN_FINANCIAL_SPACE;


insert into PN_OBJECT values (1000,'financial','01/01/2001',47902,'A');


create or replace
PACKAGE FINANCIAL IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Nicolas    23-May-13  Initial Package Creation.

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

Procedure invite_person_to_financial
(
    i_financial_id IN NUMBER,
    i_person_id IN NUMBER,
    i_email IN VARCHAR2,
    i_firstname IN VARCHAR2,
    i_lastname IN VARCHAR2,
    i_responsibilities IN VARCHAR2,
    i_invitor_id IN NUMBER,
    o_invitation_code OUT NUMBER,
    o_status OUT NUMBER
);


PROCEDURE CREATE_FINANCIAL
(
    i_creator_id IN VARCHAR2,
    i_financial_id IN VARCHAR2
);



END;


create or replace
PACKAGE BODY FINANCIAL IS


Procedure invite_person_to_financial
(
    i_financial_id IN NUMBER,
    i_person_id IN NUMBER,
    i_email IN VARCHAR2,
    i_firstname IN VARCHAR2,
    i_lastname IN VARCHAR2,
    i_responsibilities IN VARCHAR2,
    i_invitor_id IN NUMBER,
    o_invitation_code OUT NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'FINANCIAL.INVITE_PERSON_TO_FINANCIAL';
v_sysdate DATE;
v_invited_status CONSTANT VARCHAR2(20) := 'Invited';

BEGIN

    SELECT sysdate INTO v_sysdate FROM dual;

    INSERT INTO pn_invited_users
        (invitation_code, space_id, person_id , invitee_email, invitee_firstname, invitee_lastname,
         invitee_responsibilities, invitor_id, date_invited, date_responded, invited_status)
      VALUES
        (pn_invite_sequence.nextval, i_financial_id, i_person_id , i_email, i_firstname, i_lastname,
         i_responsibilities, i_invitor_id, v_sysdate, null, v_invited_status);

    SELECT pn_invite_sequence.currval INTO o_invitation_code FROM dual;


    COMMIT;
    o_status := success;

EXCEPTION

    WHEN e_null_constraint THEN
        ROLLBACK;
        o_status := null_field;

    WHEN e_check_constraint THEN
        ROLLBACK;
        o_status := check_violated;

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_status := dupe_key;

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (v_sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END invite_person_to_financial;




PROCEDURE CREATE_FINANCIAL
(
    i_creator_id IN VARCHAR2,
    i_financial_id IN VARCHAR2
    
) 
IS

  v_status NUMBER := 0;
  v_power_user NUMBER := 0;
  v_group_id NUMBER(20) :=0;
  v_directory_id NUMBER(20) :=0;

BEGIN
            
  /****************************************************************************************************
  *  FINANCIAL SECURITY
  ****************************************************************************************************/
  
      -- SPACE_HAS_MODULES
      -- The new financial space initially has access to all modules.
      -- Copy all pn_module entries to pn_space_has_modules for this project
  
      INSERT INTO pn_space_has_module (
          space_id,
          module_id,
          is_active)
      SELECT i_financial_id, module_id, '1'
      FROM
          pn_module
      WHERE
          module_id IN (10, 20, 30, 70, 110, 140, 170, 180, 190, 200, 210, 310, 330, 340, 390);
  
      -- Disable below mentioned modules for the time-being  
      UPDATE pn_space_has_module SET is_active = '0' WHERE  space_id = i_financial_id AND module_id IN (90 , 100 , 120 , 210 ) ;
  
      -- SPACE ADMINISTRATOR GROUP
      -- The financial creator is the inital space administrator of this new project
      v_status := security.F_CREATE_SPACE_ADMIN_GROUP(i_creator_id, i_financial_id, '@prm.business.security.group.type.spaceadmin.description');
  
      -- SPACE ADMINISTRATOR GROUP
      -- The financial creator is the inital space administrator of this new project
      v_power_user := security.F_CREATE_POWER_USER_GROUP(i_creator_id, i_financial_id, '@prm.security.group.type.poweruser.description');
  
  
  
      -- PRINCIPAL GROUP
      -- The financial creator (person) must be put into a principal group for this space.
      v_group_id := NULL;
      SECURITY.CREATE_PRINCIPAL_GROUP(i_creator_id, i_financial_id, v_group_id);
  
      -- add financial creator (person) to their principal group
      v_status := NULL;
      SECURITY.ADD_PERSON_TO_GROUP(v_group_id, i_creator_id, v_status);
  
      -- TEAM MEMBER GROUP
      -- The creator is the only initial team member
      v_group_id := NULL;
      SECURITY.CREATE_TEAMMEMBER_GROUP(i_creator_id, i_financial_id, v_group_id);
  
      v_status := NULL;
      SECURITY.ADD_PERSON_TO_GROUP(v_group_id, i_creator_id, v_status);
  
  
  /****************************************************************************************************
  *  END FINANCIAL SECURITY
  ****************************************************************************************************/
              
     -- Add the creator as the first member of the business directory
      INSERT INTO pn_space_has_person
          (space_id, person_id, relationship_person_to_space, record_status)
          VALUES
          (i_financial_id, i_creator_id, 'employee', 'A');
  
    -- Every financial space gets the default directory
     Select directory_id into v_directory_id from pn_directory where is_default = 1;
    
     INSERT INTO pn_space_has_directory
            (directory_id, space_id)
            VALUES
            (v_directory_id, i_financial_id); 
    

  END;

END;


CREATE OR REPLACE SYNONYM PNET_USER.FINANCIAL FOR PNET.FINANCIAL;
