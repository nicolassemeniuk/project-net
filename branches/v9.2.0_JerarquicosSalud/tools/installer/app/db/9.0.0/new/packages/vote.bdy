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
CREATE OR REPLACE Package Body vote
IS

PROCEDURE create_vote
    (
        in_space_id IN number,
        in_question IN varchar2,
        in_title    IN varchar2,
        in_whoami   IN number,
        object_id   OUT number
    )

IS

-- variable declaration

    v_space_id     pn_vote_questionair.space_id%type := TO_NUMBER(in_space_id);
    v_question        pn_vote_questionair.question%type := in_question;
    v_title           pn_vote_questionair.title%type := in_title;
    v_new_id          pn_vote_questionair.vote_id%type ;
    v_whoami          pn_person.person_id%type := in_whoami;
    G_active_record_status   pn_document.record_status%type := 'A';

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'VOTE.CREATE_VOTE';



BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_new_id FROM dual;


      -- register new container in pn_object

      insert into pn_object (
          object_id,
          date_created,
          object_type,
          created_by,
          record_status )
      values (
          v_new_id,
          SYSDATE,
          'vote',
          v_whoami,
          G_active_record_status);

      -- create new document container

      insert into pn_vote_questionair (
          space_id,
          vote_id,
          question,
          title)
      values (
         v_space_id,
         v_new_id,
         v_question,
         v_title
         );

      object_id := v_new_id;
      COMMIT;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END;  -- Procedure CREATE_VOTE














PROCEDURE create_response
    (
        in_vote_id IN pn_vote_response.vote_id%type,
        in_response IN pn_vote_response.response%type
    )

IS

-- variable declaration

    v_vote_id         pn_vote_response.vote_id%type := TO_NUMBER(in_vote_id);
    v_response        pn_vote_response.response%type := in_response;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'VOTE.CREATE_RESPONSE';



BEGIN
    SET TRANSACTION READ WRITE;

      insert into pn_vote_response (
          vote_id,
          response )
      values (
          v_vote_id,
          v_response );

      COMMIT;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END;  -- Procedure CREATE_RESPONSE



PROCEDURE add_voter
    (
        in_vote_id IN pn_person_has_vote.vote_id%type,
        in_person_id IN pn_person_has_vote.person_id%type
    )

IS

-- variable declaration

    v_vote_id         pn_person_has_vote.vote_id%type := TO_NUMBER(in_vote_id);
    v_person_id        pn_person_has_vote.person_id%type := in_person_id;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'VOTE.CREATE_RESPONSE';



BEGIN
    SET TRANSACTION READ WRITE;

      insert into pn_person_has_vote (
          person_id,
          vote_id )
      values (
          v_person_id,
          v_vote_id );

      COMMIT;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END;  -- Procedure ADD_VOTER







-----------------------------------------------------------------
-- GET_VOTE_LIST
-----------------------------------------------------------------

FUNCTION GET_CONTAINER_LIST
( space_id IN pn_vote_questionair.space_id%type )
    RETURN ReferenceCursor

   IS

    v_space_id          pn_vote_questionair.space_id%type := to_number(space_id);

    v_vote_id           pn_vote_questionair.vote_id%type;
    v_question          pn_vote_questionair.question%type;
    v_title             pn_vote_questionair.title%type;

    v_root_container_id pn_doc_container.doc_container_id%type;
    v_is_root           NUMBER := 0;
    v_is_hidden         NUMBER := 0;

    v_collection_id     pn_object.object_id%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.GET_CONTAINER_LIST';

    voteList            ReferenceCursor;

    BEGIN

    open voteList for
        select * from pn_vote_questionair where space_id = v_space_id;

    RETURN voteList;

 EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;


END; -- Procedure GET_VOTE_LIST









   -- Enter further code below as specified in the Package spec.
END; -- Package Body VOTE
/

