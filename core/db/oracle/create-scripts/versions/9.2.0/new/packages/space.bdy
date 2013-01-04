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
CREATE OR REPLACE PACKAGE BODY space IS


Procedure add_member
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_invitation_code IN number,
    o_status OUT NUMBER
) IS

    stored_proc_name VARCHAR2(100):= 'space.add_member';

    v_timestamp DATE := SYSDATE;

BEGIN

        INSERT INTO pn_space_has_person
            (space_id, person_id, relationship_person_to_space, relationship_space_to_person,
             responsibilities, record_status)
          SELECT i_space_id, i_person_id, 'member', 'has', invitee_responsibilities, 'A'
             FROM pn_invited_users
             WHERE invitation_code = i_invitation_code;

        IF SQL%NOTFOUND THEN
            o_status := no_data;
            RETURN;
        END IF;


EXCEPTION
    WHEN NO_DATA_FOUND THEN
        o_status := no_data;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_null_constraint THEN
        o_status := null_field;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_check_constraint THEN
        o_status := check_violated;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_unique_constraint THEN
        o_status := dupe_key;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);
    WHEN e_no_parent_key THEN
        o_status := no_parent_key;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN OTHERS THEN
        o_status := generic_error;

        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

END add_member;



Procedure accept_invitation
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_invitation_code IN number
) IS

    v_timestamp DATE := SYSDATE;
    v_status NUMBER;
    v_group_id pn_group.group_id%TYPE;

BEGIN

    -- now that the user has accepted, add them to the team member group
    -- and create a "Principle Group" for this space

        INSERT INTO pn_group_has_person
            (group_id, person_id)
          SELECT g.group_id, i_person_id
             FROM pn_group g, pn_space_has_group sg
             WHERE space_id = i_space_id
               AND g.group_type_id = SECURITY.GROUP_TYPE_TEAMMEMBER
               AND g.group_id = sg.group_id;

        -- portfolio
        INSERT INTO pn_portfolio_has_space
            (portfolio_id, space_id)
          SELECT membership_portfolio_id, i_space_id
             FROM pn_person
             WHERE person_id = i_person_id;

        -- principal group
        v_group_id := NULL;
        security.create_principal_group(i_person_id, i_space_id, v_group_id);

        v_status := NULL;
        security.add_person_to_group(v_group_id, i_person_id, v_status);

     -- now updated the users status information for the space
     UPDATE pn_invited_users
       SET date_responded = v_timestamp, invited_status = ACCEPTED_INVITATION_RESPONSE , invitation_acted_upon = '1'
     WHERE invitation_code = i_invitation_code
         AND space_id = i_space_id and person_id = i_person_id ;

    -- finally, update the assignment
    UPDATE pn_assignment
    set status_id = 20 --accepted
    where space_id = i_person_id
    and person_id = i_person_id
    and object_id = i_space_id;

EXCEPTION
    WHEN e_unique_constraint THEN
        -- Quietly proceed; this handling moved from Java code
        -- Accepting an invitation twice is permitted
        null;

END accept_invitation;



Procedure decline_invitation
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER,
    i_invitation_code IN number,
    o_status OUT NUMBER
) IS

    stored_proc_name VARCHAR2(100):= 'space.decline_invitation';

    v_timestamp DATE := SYSDATE;


BEGIN

     -- now updated the users status information for the space
     UPDATE pn_invited_users
       SET date_responded = v_timestamp, invited_status = DECLINED_INVITATION_RESPONSE , invitation_acted_upon ='1'
     WHERE invitation_code = i_invitation_code
         AND space_id = i_space_id and person_id = i_person_id ;

    IF SQL%NOTFOUND THEN
        o_status := no_data;
        ROLLBACK;
        RETURN;
    END IF;

    -- finally, update the assignment
    UPDATE pn_assignment
    set status_id = 70 -- rejected
    where space_id = i_person_id
    and person_id = i_person_id
    and object_id = i_space_id;

    -- decline any tasks associated to this user that reside in
    -- the space we just deleted.
    UPDATE pn_assignment
    set status_id = 70
    where space_id = i_space_id
    and person_id = i_person_id;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        o_status := no_data;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_null_constraint THEN
        o_status := null_field;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_check_constraint THEN
        o_status := check_violated;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_unique_constraint THEN
        o_status := dupe_key;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);
    WHEN e_no_parent_key THEN
        o_status := no_parent_key;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN OTHERS THEN
        o_status := generic_error;

        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

END; -- decline invite


Procedure invite_person
(
    i_space_id IN NUMBER,
    i_person_id IN NUMBER ,
    i_email IN VARCHAR2,
    i_firstname IN VARCHAR2,
    i_lastname IN VARCHAR2,
    i_responsibilities IN VARCHAR2,
    i_invitor_id IN NUMBER,
    o_invitation_code OUT NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'space.invite_person';
v_sysdate DATE;

BEGIN

    SELECT sysdate INTO v_sysdate FROM dual;

    INSERT INTO pn_invited_users
        (invitation_code, space_id, person_id , invitee_email, invitee_firstname, invitee_lastname,
         invitee_responsibilities, invitor_id, date_invited, date_responded, invited_status)
      VALUES
        (pn_invite_sequence.nextval, i_space_id, i_person_id , i_email, i_firstname, i_lastname,
         i_responsibilities, i_invitor_id, v_sysdate, null, 'Accepted');

    SELECT pn_invite_sequence.currval INTO o_invitation_code FROM dual;


    COMMIT;
    o_status := success;

EXCEPTION

    WHEN e_null_constraint THEN
        ROLLBACK;
        o_status := null_field;
                err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_check_constraint THEN
        ROLLBACK;
        o_status := check_violated;
                err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_unique_constraint THEN
        ROLLBACK;
        o_status := dupe_key;
                err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN e_no_parent_key THEN
        ROLLBACK;
        o_status := no_parent_key;
                err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

    WHEN OTHERS THEN
        o_status := generic_error;
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

END;

END; -- package body
/

