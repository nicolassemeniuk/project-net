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
CREATE OR REPLACE PACKAGE BODY discussion IS

---------------------------------------------------------------------
-- CREATE_DISCUSSION_GROUP
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Robin       04-May-00  Created from existing procedure, added error handling
-- Adam        22-May-00  added ability to update discussion group properties
-- Roger       07-Jan-01  Added initial post to new group. Added group copy.
-- Tim         17-Dec-01  Removed COMMIT and ROLLBACK to enable this to be
--                        part of a greater transaction (e.g. DOCUMENT.COPY_DOCUMENT)
-- Tim         28-Feb-03  Made DISCUSSION_GROUP_CHARTER into a CLOB
/**
 * Creates OR updates a disucssion group.
 * During an update, the discussion group charter CLOB is set to empty_clob()
 * After calling this method, the charter must be streamed to the CLOB
 */
Procedure CREATE_DISCUSSION_GROUP
(
      i_discussion_group_id IN NUMBER,
      i_space_id IN VARCHAR2,
      i_object_id IN VARCHAR2,
      i_discussion_group_name IN VARCHAR2,
      i_person_id IN NUMBER,
      i_discussion_group_description IN VARCHAR2,
      i_create_welcome_post IN NUMBER,
      i_welcome_message_subject IN VARCHAR2,
      o_discussion_group_id OUT NUMBER,
      o_welcome_message_post_clob OUT CLOB,
      o_discussion_grp_charter_clob OUT CLOB
)

IS

    stored_proc_name VARCHAR2(100):= 'DISCUSSION.CREATE_DISCUSSION_GROUP';
    v_post_id   pn_post.post_id%type;
    v_status    number;
    v_message   varchar(2000);
    v_post_date date;
    v_post_body_id pn_post.post_body_id%type;

BEGIN
    IF (i_discussion_group_id IS NULL) THEN
        -- Create the object
        o_discussion_group_id := BASE.CREATE_OBJECT('discussion_group', i_person_id, 'A');
        -- Apply default security permissions
        SECURITY.CREATE_SECURITY_PERMISSIONS(o_discussion_group_id, 'discussion_group', i_space_id, i_person_id);

        -- Create the discussion_group
        -- We return the empty_clob into the clob locater output parameter
        INSERT INTO pn_discussion_group
            (discussion_group_name, discussion_group_id, discussion_group_description, discussion_group_charter_clob, record_status)
        VALUES
            (i_discussion_group_name, o_discussion_group_id, i_discussion_group_description, empty_clob(), 'A')
        RETURNING discussion_group_charter_clob into o_discussion_grp_charter_clob;

        -- This space is creating the discussion group, so it is the owner.
        INSERT INTO pn_object_has_discussion
            (object_id, discussion_group_id)
        VALUES
            (i_object_id, o_discussion_group_id);


        if (i_create_welcome_post > 0) THEN

            v_post_body_id := BASE.CREATE_OBJECT('post', i_person_id, 'A');

            insert into pn_post_body_clob
                (object_id , clob_field)
            values
                (v_post_body_id, empty_clob())
            returning clob_field into o_welcome_message_post_clob;

            CREATE_POST(
                i_space_id,
                o_discussion_group_id,
                null,
                i_person_id,
                i_welcome_message_subject,
                100,
                v_post_body_id,
                v_post_date,
                v_post_id);
        END IF;

    ELSE
        -- A. Klatzkin 5/22/00
        -- discussion group already exists so just update
        o_discussion_group_id := i_discussion_group_id;

        -- Return the CLOB locater into the OUT variable so it can
        -- be updated outside of this procedure
        -- Clears out the charter so that a subsequent write will overwrite
        -- all data
        UPDATE pn_discussion_group
        SET
           discussion_group_name = i_discussion_group_name,
           discussion_group_description = i_discussion_group_description,
           discussion_group_charter_clob = empty_clob()
        WHERE
           discussion_group_id = i_discussion_group_id
        RETURNING discussion_group_charter_clob into o_discussion_grp_charter_clob;

    END IF;

EXCEPTION
    WHEN OTHERS THEN
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;

END create_discussion_group;

----------------------------------------------------------------------
-- LOGS_EVENT
----------------------------------------------------------------------
 PROCEDURE log_event
    (
        discussion_group_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    )

IS
    v_discussion_group_id     pn_discussion_group.discussion_group_id%type := TO_NUMBER(discussion_group_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_discussion_history.discussion_group_history_id%type;
    v_action          pn_discussion_history.action%type := action;
    v_action_name     pn_discussion_history.action_name%type := action_name;
    v_action_comment  pn_discussion_history.action_comment%type := notes;

BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- insert a history record for this document

    insert into pn_discussion_history (
        discussion_group_id,
        discussion_group_history_id,
        action,
        action_name,
        action_by_id,
        action_date,
        action_comment)
    values (
        v_discussion_group_id,
        v_history_id,
        v_action,
        v_action_name,
        v_whoami,
        SYSDATE,
        v_action_comment
    );

COMMIT;

EXCEPTION
    WHEN OTHERS THEN
    BEGIN
        DBMS_OUTPUT.put_line('exception');
        rollback ;
    END;
END;  -- Procedure LOG_EVENT
----------------------------------------------------------------------

---------------------------------------------------------------------
-- CREATE_POST
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Robin       04-May-00  Created from existing procedure, added
--                        error handling

Procedure CREATE_POST
(
    i_space_id IN NUMBER,
    i_discussion_group_id IN NUMBER,
    i_parent_id IN NUMBER,
    i_person_id  IN NUMBER,
    i_subject IN VARCHAR2,
    i_urgency_id IN NUMBER,
    i_post_body_id IN NUMBER,
    o_post_date OUT DATE,
    o_post_id OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'DISCUSSION.CREATE_POST';
v_current_date date := SYSDATE;

BEGIN
    -- Create the object
    o_post_id := BASE.CREATE_OBJECT('post', i_person_id, 'A');

    -- Apply default security permissions
    SECURITY.CREATE_SECURITY_PERMISSIONS(o_post_id, 'post', i_space_id, i_person_id);

    --Create the post
    INSERT INTO pn_post
        (person_id, parent_id, discussion_group_id, post_id, subject, urgency_id, post_body_id, date_posted, record_status)
    VALUES
        (i_person_id, i_parent_id, i_discussion_group_id, o_post_id, i_subject, i_urgency_id, i_post_body_id, v_current_date, 'A');


    -- We say that the originator of the post has also read the post
    INSERT INTO pn_post_reader
        (person_id, discussion_group_id, post_id, date_read)
    VALUES
        (i_person_id, i_discussion_group_id, o_post_id, v_current_date);

    o_post_date := v_current_date;

EXCEPTION
    WHEN OTHERS THEN
        BASE.LOG_ERROR(stored_proc_name, SQLCODE, SQLERRM);
        raise;

END create_post;    -- A new post with a unique post_id number has been inserted into the data base



---------------------------------------------------------------------
-- UPDATE_POST_READER
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   --------   -------------------------------------------
-- Robin       04-May-00  Created from existing procedure, added
--                        error handling

Procedure UPDATE_POST_READER
(
    i_person_id IN NUMBER,
    i_discussion_group_id IN NUMBER,
    i_post_id IN NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'DISCUSSION.UPDATE_POST_READER';
reader_count NUMBER(6);

BEGIN
    -- See if the person has already read this post
    select count(pr.person_id) into reader_count
    from pn_post_reader pr
    where
        pr.person_id = i_person_id and
        pr.discussion_group_id = i_discussion_group_id and
        pr.post_id = i_post_id;


    -- If the person has not read this post, mark it read.
    -- Otherwise, update date_read
    --IF (reader_count > 0) THEN
    --    update pn_post_reader pr
    --    set date_read = SYSDATE
    --    where
    --        pr.person_id = i_person_id and
    --        pr.discussion_group_id = i_discussion_group_id and
    --        pr.post_id = i_post_id;

    IF (reader_count <= 0) THEN
        INSERT INTO	pn_post_reader
            (person_id, discussion_group_id, post_id, date_read)
          VALUES
            (i_person_id, i_discussion_group_id, i_post_id, SYSDATE);
    END IF;

    COMMIT;
    o_status := success;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        o_status := no_data;

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
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END update_post_reader;


----------------------------------------------------------------------
-- COPY_ALL
-- Copies all discussion groups for a space to another space
-- AUTONOMOUS TRANSACTION
----------------------------------------------------------------------
    PROCEDURE copy_all
      ( i_from_space_id     in varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        o_return_value      OUT number)
    is
        pragma autonomous_transaction;

        -- Cursor to read all discussion groups for a given space
        cursor group_cur (i_space_id in number)
        is
            select discussion_group_id
            from pn_object_has_discussion
            where object_id = i_space_id;

        group_rec           group_cur%rowtype;
        v_from_space_id     pn_space_has_class.space_id%type := to_number(i_from_space_id);
        v_to_space_id       pn_space_has_class.space_id%type := to_number(i_to_space_id);
        v_created_by_id     number := to_number(i_created_by_id);

    begin

        for group_rec in group_cur(i_from_space_id)
        loop
            copy_discussion_group(v_from_space_id, v_to_space_id, group_rec.discussion_group_id, v_created_by_id);
        end loop;

        if group_cur%isopen then
            close group_cur;
        end if;
        commit;
        o_return_value := base.operation_successful;

    exception
        when others then
        begin
           --dbms_output.put_line('copy_all Error '||TO_CHAR(SQLCODE)||': '||SQLERRM);
           if group_cur%isopen then
                close group_cur;
           end if;
           rollback;
           o_return_value := base.plsql_exception;
        end;

    end copy_all;





----------------------------------------------------------------------
-- COPY_DISCUSSION_GROUP
-- Copies a single discussion group from one space to another space.
-- New objects ids are assigned so that the two discussion groups
-- are completely independant copies of each other.
-- AUTONOMOUS_TRANSACTION
----------------------------------------------------------------------
    procedure copy_discussion_group (
        i_from_space_id in number,
        i_to_space_id in number,
        i_discussion_group_id in number,
        i_created_by_id in number)
    is
        PRAGMA AUTONOMOUS_TRANSACTION;

        -- Exceptions
        group_not_found exception;

        group_rec               pn_discussion_group%rowtype;
        v_discussion_group_id   pn_discussion_group.discussion_group_id%type;
        v_space_id              pn_object_has_discussion.object_id%type;
        v_welcome_message_post_clob pn_post_body_clob.clob_field%type;
        v_discussion_grp_charter_clob pn_discussion_group.discussion_group_charter_clob%type;

    begin

        select * into group_rec
        from pn_discussion_group
        where discussion_group_id = i_discussion_group_id;

        v_space_id := i_to_space_id;

        CREATE_DISCUSSION_GROUP
        (
            null,
            v_space_id,          -- varchar2
            v_space_id,
            group_rec.discussion_group_name,
            i_created_by_id,
            group_rec.discussion_group_description,
            0, -- Do not create-welcome-message
            NULL,
            v_discussion_group_id,  -- out
            v_welcome_message_post_clob, -- out
            v_discussion_grp_charter_clob -- out
        );

        -- Now stream the current clob data to the new clob locater
        if (group_rec.discussion_group_charter_clob is not null) then
            dbms_lob.append(v_discussion_grp_charter_clob,group_rec.discussion_group_charter_clob);
        end if;

        commit;

    exception
        when others then
        begin
            rollback;
            BASE.LOG_ERROR('DISCUSSION.COPY_DISCUSSION_GROUP', SQLCODE, SQLERRM);
            raise;
        end;
    end copy_discussion_group;


FUNCTION object_has_discussion
(
    i_object_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_has_disc             NUMBER  := 0;
    v_group_id              pn_post.discussion_group_id%type;
    v_count                 NUMBER  := 0;

BEGIN

    select distinct discussion_group_id into v_group_id from pn_object_has_discussion
    where object_id = i_object_id;

    select count(*) into v_count from pn_post
    where discussion_group_id = v_group_id
    and record_status = 'A';

    if (v_count > 0) then
        v_has_disc := 1;
    end if;

    return v_has_disc;


EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        v_has_disc := 0;
        return v_has_disc;
      END;


END; -- has_links

END; -- Package Body DISCUSSION
/

