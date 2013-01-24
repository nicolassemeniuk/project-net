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
CREATE OR REPLACE PACKAGE BODY document IS
-----------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- Robin       032900  Created package body for Phil's procedures
-- Adam        052200  Parameters to CREATE_DISCUSSION_GROUP changed
-- Tim         091201  Overhauled document copy procedures
--                     due to Java-ization of document copy
-- Matt        080702  Integrated get_path_for_object and
--                     get_container_list provided by VerticalBuilder
--                     to eliminate use of pn_tmp_doc_path_info
-----------------------------------------------------------------


-----------------------------------------------------------------
-- CREATE_BOOKMARK
-----------------------------------------------------------------
PROCEDURE create_bookmark
(
        i_name in varchar2,
        i_description in varchar2,
        i_url in varchar2,
        i_container_id in number,
        i_status_id in number,
        i_owner_id in number,
        i_comments in varchar2,
        i_created_by_id in number,
        o_status out number,
        o_object_id out number
)   IS

    -- general variables

    v_sysdate DATE := SYSDATE;
    v_space_id number(20);


    -- debugging / error logging variables

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CREATE_BOOKMARK';

BEGIN

    o_object_id := base.create_object (G_bookmark_object_type, i_created_by_id, 'A');

    insert into pn_bookmark
    (
        BOOKMARK_ID,
        NAME,
        DESCRIPTION,
        URL,
        STATUS_ID,
        OWNER_ID,
        COMMENTS,
        MODIFIED_DATE,
        MODIFIED_BY_ID,
        RECORD_STATUS,
        CRC
    ) values (
        o_object_id,
        i_name,
        i_description,
        i_url,
        i_status_id,
        i_owner_id,
        i_comments,
        v_sysdate,
        i_created_by_id,
        G_active_record_status,
        v_sysdate
    );

    -- apply default object permissions for the document
    v_space_id := get_space_for_container_id (i_container_id);

    SECURITY.APPLY_DOCUMENT_PERMISSIONS (o_object_id, i_container_id, G_bookmark_object_type, v_space_id, i_created_by_id);

    -- finally, add this bookmark to it's parent container
    add_object_to_container (o_object_id, i_container_id);
    o_status := 0;

EXCEPTION

    WHEN OTHERS THEN

        o_status := SQLCODE;
        o_object_id := -1;

        err_num := o_status;
        err_msg:=SUBSTR(SQLERRM,1,120);

        BASE.LOG_ERROR (stored_proc_name, err_num, err_msg);

END;



-----------------------------------------------------------------
-- MODIFY_BOOKMARK
-----------------------------------------------------------------
PROCEDURE modify_bookmark
(
        i_id in number,
        i_name in varchar2,
        i_description in varchar2,
        i_url in varchar2,
        i_container_id in number,
        i_status_id in number,
        i_owner_id in number,
        i_comments in varchar2,
        i_created_by_id in number,
        o_status out number
)   IS

    -- general variables

    v_sysdate DATE := SYSDATE;


    -- debugging / error logging variables

    stored_proc_name VARCHAR2(100):= 'DOCUMENT.MODIFY_BOOKMARK';

BEGIN

    update pn_bookmark
    set
        name = i_name,
        description = i_description,
        url = i_url,
        status_id = i_status_id,
        owner_id = i_owner_id,
        comments = i_comments,
        modified_date = v_sysdate,
        modified_by_id = i_created_by_id,
        record_status = 'A',
        crc = v_sysdate
    where
        bookmark_id = i_id;

    o_status := 0;

EXCEPTION

    WHEN OTHERS THEN

        o_status := SQLCODE;
        BASE.LOG_ERROR (stored_proc_name, o_status, SQLERRM);

END;



procedure create_doc_space
(
    i_in_space_id in pn_doc_space.doc_space_id%type,
    i_creator_id in pn_person.person_id%type,
    o_doc_space_id out number,
    o_return_value out number
) as

    v_doc_space_id pn_doc_space.doc_space_id%type;

BEGIN

     -- create doc_space object
    v_doc_space_id := BASE.CREATE_OBJECT('doc_space', 1, 'A');

    INSERT INTO pn_doc_space
        (doc_space_id, doc_space_name, crc, record_status)
      VALUES
        (v_doc_space_id, 'default', SYSDATE, 'A');

    INSERT INTO pn_space_has_doc_space
        (space_id, doc_space_id, is_owner)
      VALUES
        (i_in_space_id, v_doc_space_id, 1);

    -- link new doc_space to doc provider
    INSERT INTO pn_doc_provider_has_doc_space
        (doc_provider_id, doc_space_id)

      SELECT doc_provider_id, v_doc_space_id
        FROM pn_doc_provider
        WHERE is_default = 1;

    o_doc_space_id := v_doc_space_id;
    o_return_value := BASE.OPERATION_SUCCESSFUL;

EXCEPTION
    when others then
        o_return_value := BASE.PLSQL_EXCEPTION;


END;



-----------------------------------------------------------------
-- CREATE_DOC
-----------------------------------------------------------------

PROCEDURE create_doc
    (
        tmp_doc IN varchar2,
        container_id IN varchar2,
        whoami IN varchar2,
        space_id IN varchar2,
        ignore_name_constraint IN NUMBER,
        i_discussion_group_description IN VARCHAR2,
        v_return_value OUT NUMBER
    )

IS
    -- variable declaration

    v_tmp_doc_id      pn_tmp_document.tmp_doc_id%type := TO_NUMBER(tmp_doc);
    v_container_id    pn_doc_container.doc_container_id%type := TO_NUMBER(container_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_space_id        pn_project_space.project_id%type := TO_NUMBER(space_id);
    v_document_id     pn_document.doc_id%type;
    v_version_id      pn_doc_version.doc_version_id%type;
    v_history_id      pn_doc_history.doc_history_id%type;
    v_content_id      pn_doc_content_element.doc_content_id%type;
    v_tmp_doc_rec     pn_tmp_document%rowtype;
    v_discussion_group_id   pn_discussion_group.discussion_group_id%type;
    --v_status          NUMBER;
    v_welcome_message_post_clob   pn_post_body_clob.clob_field%type;
    v_discussion_grp_charter_clob pn_discussion_group.discussion_group_charter_clob%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CREATE';

-- global (static) variable declaration

    G_initial_version number := 1;
    G_initial_label   pn_tmp_document.doc_version_label%type := 'Initial Version';
    G_action          pn_doc_history.action%type := 'create';
    G_action_comment  pn_doc_history.action_comment%type := 'Created new document: ';

BEGIN

    SET TRANSACTION READ WRITE;

    -- document_id is set to the value of the tmp_doc_id.
    -- Needed because JDBC can't deal with returns.  -Roger 9/8/99
    v_document_id := v_tmp_doc_id;
    -- doc_id := v_tmp_doc_id;

    -- SELECT pn_object_sequence.nextval INTO v_document_id FROM dual;
    SELECT pn_object_sequence.nextval INTO v_version_id FROM dual;
    SELECT pn_object_sequence.nextval INTO v_content_id FROM dual;
    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- get a tmp_document record
    select * INTO v_tmp_doc_rec from pn_tmp_document where tmp_doc_id = v_tmp_doc_id;


    IF ((ignore_name_constraint = 1) OR
        (document.f_verify_unique_name (v_tmp_doc_rec.doc_name, v_container_id, G_document_object_type)))
        THEN

      -- "register" the document object
      insert into pn_object (
          object_id,
          date_created,
          object_type,
          created_by,
          record_status )
      values (
          v_document_id,
          SYSDATE,
          G_document_object_type,
          v_whoami,
          G_active_record_status);


      -- insert the document object
      insert into pn_document
      (
          doc_id,
          doc_name,
          doc_description,
          doc_type_id,
          doc_status_id,
          current_version_id,
          record_status,
          crc
      ) values
      (
          v_document_id,
          v_tmp_doc_rec.doc_name,
          v_tmp_doc_rec.doc_description,
          v_tmp_doc_rec.doc_type_id,
          v_tmp_doc_rec.doc_status_id,
          v_version_id,
          G_active_record_status,
          SYSDATE
      );

      -- apply default object permissions for the document
      SECURITY.APPLY_DOCUMENT_PERMISSIONS (v_document_id, v_container_id, G_document_object_type, v_space_id, v_whoami);


      -- register the "document_version" object
      insert into pn_object (
          object_id,
          date_created,
          object_type,
          created_by,
          record_status )
      values (
          v_version_id,
          SYSDATE,
          G_document_version_object_type,
          v_whoami,
          G_active_record_status);

      -- create a document version
      insert into pn_doc_version (
          doc_id,
          doc_version_id,
          doc_version_num,
          doc_version_label,
          doc_version_name,
          source_file_name,
          short_file_name,
          doc_author_id,
          doc_description,
          date_modified,
          modified_by_id,
          doc_comment,
          crc,
          record_status)
      values (
          v_document_id,
          v_version_id,
          G_initial_version,
          G_initial_label,
          v_tmp_doc_rec.doc_name,
          v_tmp_doc_rec.source_file_name,
          v_tmp_doc_rec.short_file_name,
          v_tmp_doc_rec.doc_author_id,
          v_tmp_doc_rec.doc_description,
          SYSDATE,
          v_whoami,
          v_tmp_doc_rec.doc_comment,
          SYSDATE,
          G_active_record_status
      );

      -- no need to apply security permissions to the the version

      -- create the content element(s) for the document version
      insert into pn_doc_content_element (
          doc_content_id,
          doc_format_id,
          record_status,
          file_size,
          file_handle,
          repository_id)
      values (
          v_content_id,
          v_tmp_doc_rec.doc_format_id,
          G_active_record_status,
          v_tmp_doc_rec.file_size,
          v_tmp_doc_rec.file_handle,
          v_tmp_doc_rec.repository_id
      );

      -- link the content to the doc_version
      insert into pn_doc_version_has_content (
          doc_id,
          doc_version_id,
          doc_content_id)
      values (
          v_document_id,
          v_version_id,
          v_content_id
      );

      -- insert the doc into the correct container
      insert into pn_doc_container_has_object (
          doc_container_id,
          object_id)
      values (
          v_container_id,
          v_document_id
      );

      -- create the docs discussion group
      -- We don't stream any data to the charter CLOB nor the welcome message
      DISCUSSION.CREATE_DISCUSSION_GROUP(
        NULL,
        v_space_id,
        v_document_id,
        v_tmp_doc_rec.doc_name,
        v_whoami,
        i_discussion_group_description ,
        0, -- do not create a welcome message
        NULL,
        v_discussion_group_id,
        v_welcome_message_post_clob,
        v_discussion_grp_charter_clob);

      -- apply the documents permissions to the discussion group
      SECURITY.APPLY_DOCUMENT_PERMISSIONS(
        v_discussion_group_id,
        v_document_id,
        'discussion_group',
        v_space_id,
        v_whoami);

      v_return_value := 0;
      COMMIT;
    ELSE
      v_return_value := 5001;
      ROLLBACK;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        v_return_value := 2000;
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;


END;  -- Procedure CREATE_DOC


-----------------------------------------------------------------
-- IS_PARENT_CONTAINER
-----------------------------------------------------------------


FUNCTION IS_PARENT_CONTAINER
( parent_container_id IN varchar2,
  child_container_id IN varchar2
)
    RETURN NUMBER

   IS

        v_parent_id     pn_doc_container.doc_container_id%type := to_number(parent_container_id);
        v_child_id      pn_doc_container.doc_container_id%type := to_number(child_container_id);

        v_return_value  NUMBER := 0;

        v_tmp_id        pn_doc_container.doc_container_id%type;

        err_num NUMBER;
        err_msg VARCHAR2(120);
        stored_proc_name VARCHAR2(100):= 'DOCUMENT.IS_PARENT_CONTAINER';

BEGIN

    select doc_container_id into v_tmp_id from pn_doc_container_has_object
        where object_id = v_child_id;

    if (v_parent_id = v_tmp_id) then
       v_return_value := 1;
    end if;

    while (v_return_value = 0 AND v_tmp_id IS NOT NULL)
    loop

        select doc_container_id into v_tmp_id from pn_doc_container_has_object
            where object_id = v_tmp_id;

        if (v_parent_id = v_tmp_id) then
            v_return_value := 1;
        end if;

    end loop; -- end loop

    return v_return_value;

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

END; -- is_parent_container

-----------------------------------------------------------------
-- GET_PATH_FOR_OBJECT
-----------------------------------------------------------------
FUNCTION GET_PATH_FOR_OBJECT ( object_id IN VARCHAR2, root_object_id IN varchar2 )
  RETURN ReferenceCursor IS
    v_object_id pn_object.object_id%type := to_number ( object_id );
    v_object_type pn_object.object_type%type;
    v_object_name pn_document.doc_name%type;
    v_root_container_id pn_object.object_id%type := to_number(root_object_id);
    v_collection_id NUMBER;
    v_reached_top_level BOOLEAN := FALSE;
    err_num NUMBER;
    err_msg VARCHAR2 ( 120 );
    stored_proc_name VARCHAR2 ( 100 ) := 'DOCUMENT.GET_PATH_FOR_OBJECT';
    pathInfo ReferenceCursor;
BEGIN
    SELECT pn_object_sequence.NEXTVAL
      INTO v_collection_id
      FROM dual;

    -- now that we have collected the path for this document...
    OPEN pathInfo FOR
    select path.parent_id container_id,
           o.object_id,
           decode(o.object_type,
                  G_document_object_type,d.doc_name,
                  G_container_object_type,c.container_name,
                  null) object_name,
           v_collection_id,
           path.depth
    from pn_doc_container c,
         pn_document d,
         pn_object o,
         (select doc_container_id as parent_id,
                 object_id,
                 level depth
          from pn_doc_container_has_object
          start with object_id = v_object_id
          connect by prior doc_container_id = object_id) path
    where
        c.doc_container_id(+) = o.object_id
        and d.doc_id(+) = o.object_id
        and o.object_id = path.object_id
        and c.is_hidden = 0
    union
    select to_number(null),
           c.doc_container_id,
           c.container_name,
           v_collection_id,
           to_number(null)
    from pn_doc_container c
    where
        c.doc_container_id = v_root_container_id
    order by 5;

    RETURN pathInfo;
EXCEPTION
    WHEN OTHERS THEN
    BEGIN
        ROLLBACK;
        err_num := SQLCODE;
        err_msg := SUBSTR ( SQLERRM,
                    1,
                    120 );
        INSERT INTO pn_sp_error_log
        VALUES ( SYSDATE,
                 stored_proc_name,
                 err_num,
                 err_msg );
        COMMIT;
    END;
END;

-----------------------------------------------------------------
-- GET_PARENT_CONTAINER_INFO
-----------------------------------------------------------------

FUNCTION GET_PARENT_CONTAINER_INFO
( object_id IN varchar2)
    RETURN ReferenceCursor

   IS

    v_object_id         pn_object.object_id%type := to_number(object_id);
    v_container_id      pn_doc_container.doc_container_id%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.GET_PARENT_CONTAINER_INFO';

    containerInfo            ReferenceCursor;

BEGIN

    BEGIN
        select doc_container_id into v_container_id from pn_doc_container_has_object
            where object_id = v_object_id;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_container_id := null;

    END;

    open containerInfo for
       select doc_container_id, container_name, container_description, date_modified,
           modified_by_id, is_hidden, crc, record_status
           from pn_doc_container where doc_container_id = v_container_id;


    RETURN containerInfo;

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


END; -- Procedure GET_PARENT_CONTAINER_INFO


-----------------------------------------------------------------
-- get_parent_space_for_object
-----------------------------------------------------------------

FUNCTION get_parent_space_for_object
( object_id IN varchar2)
 RETURN ReferenceCursor

   IS

    v_object_id         pn_object.object_id%type := to_number(object_id);
    v_container_id     pn_object.object_id%type;
    v_doc_space_id     pn_object.object_id%type;
    v_space_id         pn_object.object_id%type;
    v_object_type      pn_object.object_type%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.GET_PARENT_SPACE_FOR_OBJECT';

    spaceInfo            ReferenceCursor;

    G_project_space_type    pn_object.object_type%type := 'project';
    G_personal_space_type    pn_object.object_type%type := 'person';
    G_business_space_type    pn_object.object_type%type := 'business';



BEGIN

    -- first get the ID of this object's container
    select doc_container_id into v_container_id from pn_doc_container_has_object
        where object_id = v_object_id;

    -- then get the ID of the doc_space_which contains the "container"
    select doc_space_id into v_doc_space_id from pn_doc_space_has_container
        where doc_container_id = v_container_id;

    -- now get the space which 'owns' the doc space
    select space_id into v_space_id from pn_space_has_doc_space
        where doc_space_id = v_doc_space_id;

    -- now get the object type of the space
    select object_type into v_object_type from pn_object
        where object_id = v_space_id;


    if (v_object_type = G_project_space_type) then
        open spaceInfo for
            select project_id as object_id, project_name as name, 'Project' as object_type from pn_project_space
            where project_id = v_space_id;

    elsif (v_object_type = G_business_space_type) then
        open spaceInfo for
            select business_id as object_id, business_name as name, 'Business' as object_type from pn_business
            where business_id = v_space_id;

    elsif (v_object_type = G_personal_space_type) then
        open spaceInfo for
            select person_id as object_id, 'Personal Space' as name, 'Personal' as object_type from pn_person
            where person_id = v_space_id;

    end if;

    RETURN spaceInfo;

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


END; -- Procedure get_parent_space_for_object





-----------------------------------------------------------------
-- GET_CONTAINER_LIST
-----------------------------------------------------------------

FUNCTION GET_CONTAINER_LIST ( doc_space_id IN pn_doc_space.doc_space_id%type )
  RETURN ReferenceCursor IS
    v_doc_space_id pn_doc_space.doc_space_id%type := to_number ( doc_space_id );
    v_object_id pn_object.object_id%type;
    v_object_name pn_doc_container.container_name%type;
    v_parent_id pn_doc_container.doc_container_id%type;
    v_parent_name pn_doc_container.container_name%type;
    v_root_container_id pn_doc_container.doc_container_id%type;
    v_is_root NUMBER := 0;
    v_is_hidden NUMBER := 0;
    v_collection_id pn_object.object_id%type;
    err_num NUMBER;
    err_msg VARCHAR2 ( 120 );
    stored_proc_name VARCHAR2 ( 100 ) := 'DOCUMENT.GET_CONTAINER_LIST';
    containerList ReferenceCursor;
BEGIN
    SELECT pn_object_sequence.NEXTVAL
      INTO v_collection_id
      FROM dual;
    SELECT doc_container_id
      INTO v_root_container_id
      FROM pn_doc_space_has_container
     WHERE doc_space_id = v_doc_space_id
       AND is_root = 1;
     OPEN containerList FOR
         select o.doc_container_id as object_id,
                o.container_name as object_name,
                p.doc_container_id as parent_id,
                p.container_name as parent_name,
                0 as is_root
         from pn_doc_container p, pn_doc_container o,
              (select doc_container_id as parent_id,
                      object_id
               from pn_doc_container_has_object tree
               where exists (select c.doc_container_id
                             from pn_doc_space_has_container dshc,
                                  pn_doc_container c
                             where dshc.doc_space_id = v_doc_space_id
                             and dshc.doc_container_id = c.doc_container_id
                             and c.doc_container_id = tree.object_id
                             and  c.record_status = 'A'
                            )
               start with doc_container_id = v_root_container_id
               connect by prior object_id = doc_container_id) path
         where p.doc_container_id = path.parent_id
         and o.record_status = 'A'
         and o.is_hidden <> 1
         and o.doc_container_id = path.object_id
         union
         select c.doc_container_id,
                c.container_name,
                to_number(null),
                null,
                1
         from pn_doc_container c
         where c.doc_container_id = v_root_container_id;
    RETURN containerList;
EXCEPTION
    WHEN OTHERS THEN
    BEGIN
        ROLLBACK;
        err_num := SQLCODE;
        err_msg := SUBSTR ( SQLERRM,
                    1,
                    120 );
        INSERT INTO pn_sp_error_log
        VALUES ( SYSDATE,
                 stored_proc_name,
                 err_num,
                 err_msg );
        COMMIT;
    END;
END;

-----------------------------------------------------------------
-- CREATE_CONTAINER
-----------------------------------------------------------------

PROCEDURE create_container
    (
        in_container_id IN varchar2,
        whoami IN varchar2,
        folder_name IN varchar2,
        description IN varchar2,
        space_id    IN  varchar2,
        is_hidden IN varchar2,
        object_id OUT number,
        v_return_value OUT number
    )

IS

-- variable declaration

    v_in_container    pn_doc_container.doc_container_id%type := TO_NUMBER(in_container_id);
    v_doc_space_id    pn_doc_space.doc_space_id%type;
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_space_id        pn_project_space.project_id%type := TO_NUMBER(space_id);
    v_folder_name     pn_doc_container.container_name%type := folder_name;
    v_description     pn_doc_container.container_description%type := description;
    v_is_hidden       pn_doc_container.is_hidden%type := is_hidden;
    v_new_id          pn_doc_container.doc_container_id%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CREATE_CONTAINER';



BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_new_id FROM dual;

    SELECT distinct doc_space_id into v_doc_space_id
        from pn_doc_space_has_container where doc_container_id = v_in_container;


    IF (document.f_verify_unique_name (v_folder_name, v_in_container, G_container_object_type)) THEN

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
          G_container_object_type,
          v_whoami,
          G_active_record_status);

      -- create new document container

      insert into pn_doc_container (
          doc_container_id,
          container_name,
          container_description,
          record_status,
          is_hidden,
          modified_by_id,
          date_modified,
          crc)
      values (
          v_new_id,
          v_folder_name,
          v_description,
          G_active_record_status,
          v_is_hidden,
          v_whoami,
          SYSDATE,
          SYSDATE);

      -- apply default object permissions for the object
      SECURITY.APPLY_DOCUMENT_PERMISSIONS (v_new_id, v_in_container, G_document_version_object_type, v_space_id, v_whoami);

     -- register in doc_space_has_container

     insert into pn_doc_space_has_container (
          doc_space_id,
          doc_container_id,
          is_root)
      values (
          v_doc_space_id,
          v_new_id,
          0);


     -- make the new folder an object of the exiting folder

      insert into pn_doc_container_has_object (
          doc_container_id,
          object_id)
      values (
          v_in_container,
          v_new_id);

      object_id := v_new_id;

      v_return_value := 0;
      COMMIT;

   ELSE
      object_id := -1;
      v_return_value := 5001;
      rollback;
   END IF;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        v_return_value := 2000;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END;  -- Procedure CREATE_CONTAINER


-----------------------------------------------------------------
-- MODIFY_CONTAINER
-----------------------------------------------------------------

PROCEDURE modify_container
    (
        parent_id IN varchar2,
        my_container_id IN varchar2,
        folder_name IN varchar2,
        description in varchar2,
        whoami IN varchar2,
        is_hidden IN varchar2,
        orig_crc IN DATE,
        v_return_value OUT number
    )
IS

    -- variable declaration
    v_parent_id         pn_doc_container.doc_container_id%type := TO_NUMBER(parent_id);
    v_container_id      pn_doc_container.doc_container_id%type := TO_NUMBER(my_container_id);
    v_whoami            pn_person.person_id%type := TO_NUMBER(whoami);
    v_current_crc       pn_doc_container.crc%type;
    v_current_name      pn_doc_container.container_name%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.MODIFY_CONTAINER';


BEGIN
    SET TRANSACTION READ WRITE;

    select crc into v_current_crc from pn_doc_container where doc_container_id = v_container_id;

    IF (document.f_verify_crc (orig_crc, v_current_crc)) THEN
        select container_name into v_current_name from pn_doc_container
            where doc_container_id = v_container_id;

        IF ((folder_name = v_current_name) OR
            document.f_verify_unique_name (folder_name, v_parent_id, G_container_object_type)) THEN

            UPDATE pn_doc_container
            SET
                container_name = folder_name,
                container_description = description,
                is_hidden = is_hidden,
                modified_by_id = v_whoami,
                date_modified = SYSDATE
            WHERE
                doc_container_id = v_container_id;

            v_return_value := 0;
         ELSE
		IF ((folder_name!=v_current_name)AND(lower(folder_name) = lower(v_current_name))) THEN
			UPDATE pn_doc_container
			SET
    				container_name = folder_name,
    				container_description = description,
    				is_hidden = is_hidden,
    				modified_by_id = v_whoami,
    				date_modified = SYSDATE
			WHERE
    				doc_container_id = v_container_id;
    			v_return_value := 0;
		ELSE
  			v_return_value := 5001;
		END IF;
          END IF;
     ELSE
        v_return_value := 1000;
     END IF;

    if (v_return_value = 0) THEN
        COMMIT;
    else
        ROLLBACK;
    END IF;

    EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        v_return_value := 2000;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;



END; -- Procedure MODIFY_CONTAINER

-----------------------------------------------------------------
-- ADD_OBJECT_TO_CONTAINER
-----------------------------------------------------------------

PROCEDURE add_object_to_container
    (
        in_object_id IN varchar2,
        in_container_id IN varchar2
    )

IS

    -- variable declaration

    v_in_container    pn_doc_container.doc_container_id%type := TO_NUMBER(in_container_id);
    v_object_id       pn_doc_container.doc_container_id%type := TO_NUMBER(in_object_id);

BEGIN

    insert into pn_doc_container_has_object (
        doc_container_id,
        object_id)
    values (
        v_in_container,
        v_object_id);

EXCEPTION
    WHEN OTHERS THEN
        BEGIN
            RAISE;
        END;

END;  -- Procedure ADD_OBJECT_TO_CONTAINER


-----------------------------------------------------------------
-- CHECK_IN
-----------------------------------------------------------------

PROCEDURE check_in
    (
        tmp_doc IN varchar2,
        whoami  IN varchar2,
        space_id IN varchar2,
        orig_crc IN DATE,
        v_return_value OUT number
    )

IS

-- variable declaration

    v_tmp_doc_id      pn_tmp_document.tmp_doc_id%type := TO_NUMBER(tmp_doc);
    v_doc_id          pn_document.doc_id%type;
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_space_id        pn_project_space.project_id%type := TO_NUMBER(space_id);
    v_version_num     pn_doc_version.doc_version_num%type;
    v_version_id      pn_doc_version.doc_version_id%type;
    v_content_id      pn_doc_content_element.doc_content_id%type;
    v_tmp_doc_rec     pn_tmp_document%rowtype;
    v_doc_properties  pn_document%rowtype;
    v_current_version_rec   pn_doc_version%rowtype;

    v_current_crc   DATE;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CHECK_IN';


-- global (static) variable declaration

    G_active_record_status   pn_document.record_status%type := 'A';

BEGIN

    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_version_id FROM dual;
    SELECT pn_object_sequence.nextval INTO v_content_id FROM dual;

      -- get a tmp_document record
      select * INTO v_tmp_doc_rec from pn_tmp_document where tmp_doc_id = v_tmp_doc_id;
      v_doc_id := v_tmp_doc_rec.doc_id;

--     dbms_output.put_line('GOT TMP_DOC_REC');

     select crc into v_current_crc from pn_document where doc_id = v_tmp_doc_rec.doc_id;

    -- dbms_output.put_line('GOT TMP_DOC_REC');

    IF (document.f_verify_crc (orig_crc, v_current_crc)) THEN




      -- get the properties for the document
      select * INTO v_doc_properties from pn_document where doc_id = v_doc_id;

      -- get the current version info for the document
      select * INTO v_current_version_rec from pn_doc_version
          where doc_id = v_doc_id and doc_version_id = v_doc_properties.current_version_id;

       -- increment the current version id
       v_version_num := v_current_version_rec.doc_version_num + 1;

      -- register the document version
      insert into pn_object (
          object_id,
          date_created,
          object_type,
          created_by,
          record_status )
      values (
          v_version_id,
          SYSDATE,
          G_document_version_object_type,
          v_whoami,
          G_active_record_status);


      -- create a document version
      insert into pn_doc_version (
          doc_id,
          doc_version_id,
          doc_version_num,
          doc_version_label,
          doc_version_name,
          source_file_name,
          short_file_name,
          doc_author_id,
          doc_description,
          date_modified,
          modified_by_id,
          doc_comment,
          crc,
          record_status)
      values (
          v_doc_id,
          v_version_id,
          v_version_num,
          NULL,
          v_doc_properties.doc_name,
          v_tmp_doc_rec.source_file_name,
          v_tmp_doc_rec.short_file_name,
          v_current_version_rec.doc_author_id,
          v_current_version_rec.doc_description,
          SYSDATE,
          v_whoami,
          v_tmp_doc_rec.doc_comment,
          SYSDATE,
          G_active_record_status
      );

      -- no need to apply security permissions to the version

      -- update the document current_version_id
      update pn_document set
          current_version_id = v_version_id,
          doc_status_id = v_tmp_doc_rec.doc_status_id
      where doc_id = v_doc_id;


      -- create the content element(s) for the document version
      insert into pn_doc_content_element (
          doc_content_id,
          doc_format_id,
          record_status,
          file_size,
          file_handle,
          repository_id)
      values (
          v_content_id,
          v_tmp_doc_rec.doc_format_id,
          G_active_record_status,
          v_tmp_doc_rec.file_size,
          v_tmp_doc_rec.file_handle,
          v_tmp_doc_rec.repository_id
      );

      -- link the content to the doc_version
      insert into pn_doc_version_has_content (
          doc_id,
          doc_version_id,
          doc_content_id)
      values (
          v_doc_id,
          v_version_id,
          v_content_id
      );

      v_return_value := 0;
      COMMIT;
    ELSE
      v_return_value := 1000;
      ROLLBACK;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        v_return_value := 2000;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;


END;  -- Procedure check_in


-----------------------------------------------------------------
-- CHECK_OUT
-----------------------------------------------------------------

PROCEDURE check_out
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        cko_due IN date,
        notes IN varchar2,
        orig_crc IN DATE,
        v_return_value OUT number

    )

IS
    v_document_id     pn_document.doc_id%type := TO_NUMBER(doc_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_cko_due         pn_doc_version.checkout_due%type := cko_due;
    v_notes           pn_doc_version.doc_comment%type := notes;
    v_history_id      pn_doc_history.doc_history_id%type;

    v_current_crc     DATE;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CHECK_OUT';



BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    select crc into v_current_crc from pn_document where doc_id = v_document_id;

    IF (document.f_verify_crc (orig_crc, v_current_crc)) THEN

        -- update the current version record to reflect the check out
        UPDATE
            pn_doc_version
        SET
            is_checked_out = 1,
            checked_out_by_id = v_whoami,
    	    checkout_due = v_cko_due,
            doc_comment = v_notes,
            date_checked_out = SYSDATE,
            crc = SYSDATE
	   WHERE
            doc_id = v_document_id
    	AND
            doc_version_id = (select current_version_id from pn_document where doc_id = v_document_id);

        v_return_value := 0;
        COMMIT;
    ELSE
        v_return_value := 1000;
        ROLLBACK;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        v_return_value := 2000;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END;  -- Procedure CHECK_OUT

-----------------------------------------------------------------
-- REMOVE_DOC
-----------------------------------------------------------------

PROCEDURE remove_doc
    (
        doc_id IN varchar2,
        last_modified IN date,
        record_status IN varchar2,
        whoami IN varchar2
    )

IS
    v_doc_id          pn_document.doc_id%type := TO_NUMBER(doc_id);
    v_last_modified   pn_doc_version.date_modified%type := last_modified;
    v_record_status   pn_document.record_status%type := record_status;
    v_whoami          pn_doc_version.modified_by_id%type := TO_NUMBER(whoami);

    v_current_version_id    pn_document.current_version_id%type;

BEGIN
    SET TRANSACTION READ WRITE;

    select current_version_id INTO v_current_version_id from pn_document where doc_id = v_doc_id;

    update
        pn_document
    set
        record_status = v_record_status
    where
        doc_id = v_doc_id;

    update
        pn_doc_version
    set
        date_modified = v_last_modified,
        modified_by_id = v_whoami
    where
        doc_version_id = v_current_version_id;

COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        rollback ;

END;  -- Procedure REMOVE_DOC

-----------------------------------------------------------------
-- REMOVE_CONTAINER
-----------------------------------------------------------------

PROCEDURE remove_container
    (
        container_id IN varchar2,
        record_status IN varchar2,
        whoami IN varchar2
    )

IS
    v_container_id          pn_doc_container.doc_container_id%type := TO_NUMBER(container_id);
    v_record_status         pn_document.record_status%type := record_status;
    v_whoami                pn_person.person_id%type := TO_NUMBER(whoami);

 BEGIN
    SET TRANSACTION READ WRITE;

    update
        pn_doc_container
    set
        record_status = v_record_status,
        modified_by_id = v_whoami,
        date_modified = SYSDATE
    where
        doc_container_id = v_container_id;


COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        rollback ;

END;  -- Procedure REMOVE_CONTAINER

-----------------------------------------------------------------
-- LOG_EVENT
-----------------------------------------------------------------

PROCEDURE log_event
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    )

IS
    v_document_id     pn_document.doc_id%type := TO_NUMBER(doc_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_doc_history.doc_history_id%type;
    v_action          pn_doc_history.action%type := action;
    v_action_name     pn_doc_history.action_name%type := action_name;
    v_action_comment  pn_doc_history.action_comment%type := notes;

BEGIN
    SET TRANSACTION READ WRITE;

    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- insert a history record for this document

    insert into pn_doc_history (
        doc_id,
        doc_history_id,
        action,
        action_name,
        action_by_id,
        action_date,
        action_comment)
    values (
        v_document_id,
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

-----------------------------------------------------------------
-- MOVE_OBJECT
-----------------------------------------------------------------

PROCEDURE move_object
    (
        in_object_id IN varchar2,
        in_container_id IN varchar2
    )

IS

    -- variable declaration

    v_new_container_id    pn_doc_container.doc_container_id%type := TO_NUMBER(in_container_id);
    v_object_id       pn_doc_container.doc_container_id%type := TO_NUMBER(in_object_id);
    v_current_container_id  pn_doc_container.doc_container_id%type;
BEGIN
    SET TRANSACTION READ WRITE;

    select doc_container_id into v_current_container_id from pn_doc_container_has_object
    where object_id = v_object_id;

    delete from pn_doc_container_has_object
        where
            doc_container_id = v_current_container_id
        and
            object_id = v_object_id;

     insert into pn_doc_container_has_object (doc_container_id, object_id)
        values (v_new_container_id, v_object_id);


   COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        rollback ;

END;  -- Procedure MOVE_OBJECT

-----------------------------------------------------------------
-- UNDO_CHECK_OUT
-----------------------------------------------------------------

PROCEDURE undo_check_out
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        orig_crc IN date,
        v_return_value OUT number

    )

IS
    v_doc_id          pn_document.doc_id%type := TO_NUMBER(doc_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);

    v_current_crc     DATE;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.CHECK_OUT';


BEGIN
    SET TRANSACTION READ WRITE;

    select crc into v_current_crc from pn_document where doc_id = v_doc_id;

    IF (document.f_verify_crc (orig_crc, v_current_crc)) THEN

      update
         pn_doc_version
      set
         is_checked_out = 0,
  	   checked_out_by_id = NULL,
  	   checkout_due = NULL,
  	   date_checked_out = NULL,
  	   crc = SYSDATE
      where
  	   doc_id = v_doc_id
      and
  	   doc_version_id = (select current_version_id from pn_document
  				where doc_id = v_doc_id);

      v_return_value := 0;
      COMMIT;
    ELSE
      v_return_value := 1000;
      ROLLBACK;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        v_return_value := 2000;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;
END;  -- Procedure UNDO_CHECK_OUT

-----------------------------------------------------------------
-- MODIFY_PROPERTIES
-----------------------------------------------------------------

PROCEDURE modify_properties
    (
        tmp_doc IN varchar2,
        whoami  IN varchar2,
        orig_crc IN date,
        return_value OUT NUMBER
    )

IS

-- variable declaration

    v_tmp_doc_id      pn_tmp_document.tmp_doc_id%type := TO_NUMBER(tmp_doc);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_doc_history.doc_history_id%type;
    v_tmp_doc_rec     pn_tmp_document%rowtype;
    v_current_crc     DATE;
    v_container_id    pn_doc_container.doc_container_id%type;

    v_name          pn_document.doc_name%type;

    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'DOCUMENT.MODIFY_PROPERTIES';

    -- global (static) variable declaration

    G_active_record_status   pn_document.record_status%type := 'A';
    G_action          pn_doc_history.action%type := 'modify_properties';
    G_action_comment  pn_doc_history.action_comment%type := 'Modified Document Properties: ';

BEGIN

    SET TRANSACTION READ WRITE;

    -- get an ID for the history record
    select pn_object_sequence.nextval into v_history_id from dual;

    -- get a tmp_document record
    select * INTO v_tmp_doc_rec from pn_tmp_document where tmp_doc_id = v_tmp_doc_id;
    select crc into v_current_crc from pn_document where doc_id = v_tmp_doc_rec.doc_id;
    select doc_name into v_name from pn_document where doc_id = v_tmp_doc_rec.doc_id;

    IF (document.f_verify_crc (orig_crc, v_current_crc)) THEN

        select distinct doc_container_id into v_container_id from pn_doc_container_has_object where object_id = v_tmp_doc_rec.doc_id;

        IF ((LOWER(v_tmp_doc_rec.doc_name) = LOWER(v_name)) OR
            document.f_verify_unique_name (v_tmp_doc_rec.doc_name, v_container_id, G_document_object_type)) THEN


        -- update the document object
        update pn_document
        SET
            doc_name = v_tmp_doc_rec.doc_name,
            doc_description = v_tmp_doc_rec.doc_description,
            doc_type_id = v_tmp_doc_rec.doc_type_id,
            doc_status_id = v_tmp_doc_rec.doc_status_id,
            record_status = G_active_record_status,
            crc = SYSDATE
        WHERE doc_id = v_tmp_doc_rec.doc_id;

        -- update the document version
        update pn_doc_version
        SET
            doc_author_id = v_tmp_doc_rec.doc_author_id,
            doc_description = v_tmp_doc_rec.doc_description,
            date_modified = SYSDATE,
            modified_by_id = v_whoami,
            doc_comment = v_tmp_doc_rec.doc_comment,
            crc = SYSDATE,
            record_status = G_active_record_status
        WHERE
            doc_version_id = v_tmp_doc_rec.doc_version_id
        and
            doc_id = v_tmp_doc_rec.doc_id;

        return_value := 0;
        COMMIT;
        ELSE
            return_value := 5001;
            ROLLBACK;
        END IF; -- verify unique name

    ELSE
        return_value := 1000;
        ROLLBACK;

    END IF;


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

END;  -- Procedure modify_properties

Procedure REMOVE_OBJECT_FROM_CONTAINER
(
    in_object_id IN varchar2,
    in_container_id IN varchar2
)
IS

    -- variable declaration

    v_container_id    pn_doc_container.doc_container_id%type := TO_NUMBER(in_container_id);
    v_object_id       pn_doc_container.doc_container_id%type := TO_NUMBER(in_object_id);

BEGIN
    SET TRANSACTION READ WRITE;

    delete from pn_doc_container_has_object
    where
        doc_container_id = v_container_id
    and
        object_id = v_object_id;

   COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        rollback ;

END;  -- Procedure REMOVE_OBJECT_FROM_CONTAINER

------------------------------------------------------------------
-- F_VERIFY_UNIQUE_NAME
------------------------------------------------------------------
--
-- Purpose: Briefly explain the functionality of the function
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  -------------------------------------------
-- Phil Dixon  4/1/00

Function F_VERIFY_UNIQUE_NAME
(
    new_name IN varchar2,
    containerID IN pn_doc_container.doc_container_id%type,
    object_type IN pn_object.object_type%type
)

  RETURN BOOLEAN

  IS


    -- variables
    v_container_id      pn_doc_container.doc_container_id%type  := TO_NUMBER(containerID);
    v_object_id         pn_object.object_id%type;
    v_name              pn_document.doc_name%type;

    err_msg VARCHAR2(120);
    v_foo   NUMBER := -1;


    v_is_unique_name       BOOLEAN := true;

    -- Static variable definition
    G_document_object_type  pn_object.object_type%type  := 'document';
    G_container_object_type pn_object.object_type%type  := 'doc_container';

   --
   CURSOR  c_container_objects (container_id pn_doc_container.doc_container_id%type, object_type pn_object.object_type%type) IS
        select dcho.object_id
        from pn_doc_container_has_object dcho, pn_object o
        where doc_container_id = container_id
        and o.object_type = object_type
        and o.object_id = dcho.object_id;


BEGIN

    OPEN c_container_objects(v_container_id, object_type);
	<<object_loop>>
	LOOP
        -- OBJECTS CONTAINED BY v_container_id
		FETCH c_container_objects INTO v_object_id;
		EXIT WHEN c_container_objects%NOTFOUND;

        if (object_type = G_document_object_type) then

            BEGIN
                select doc_name into v_name from pn_document
                where doc_id = v_object_id and record_status = 'A';
            EXCEPTION
                when NO_DATA_FOUND then
                begin
                    v_foo := 1;
                end;
            END;

             if (lower(v_name) = lower(new_name)) then
                v_is_unique_name := false;
            end if;

        elsif (object_type = G_container_object_type) then

            BEGIN
                select container_name into v_name from pn_doc_container
                where doc_container_id = v_object_id and record_status = 'A';
            EXCEPTION
                when NO_DATA_FOUND then
                begin
                    v_foo := 1;
                end;
            END;

            if (lower(v_name) = lower(new_name)) then
                v_is_unique_name := false;
            end if;

       end if;


	END LOOP object_loop;
	CLOSE c_container_objects;

    RETURN v_is_unique_name;

EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_msg := SUBSTR(SQLERRM, 1, 120);
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,'document.f_verify_unique_name',-019284,err_msg);
        COMMIT;
      END;



END; -- Function F_VERIFY_UNIQUE_NAME


------------------------------------------------------------------
-- F_VERIFY_CRC
------------------------------------------------------------------
Function F_VERIFY_CRC
(
    v_orig_crc IN DATE,
    v_current_crc IN DATE
)
  RETURN BOOLEAN

IS

  v_crc_match  BOOLEAN := false;

BEGIN
    if (v_orig_crc = v_current_crc) THEN
        v_crc_match := true;
    else
        v_crc_match := false;
    END IF;

    RETURN (v_crc_match);

END; -- Function F_VERIFY_CRC


------------------------------------------------------------------
-- GET_COUNT_DOCS_BY_SPACE
------------------------------------------------------------------

FUNCTION GET_COUNT
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_count             NUMBER(9);

BEGIN

    select count(doc_id) into v_count from pn_doc_by_space_view
    where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                            where space_id = in_space_id);

    return v_count;

END; -- get_count_docs_by_space


------------------------------------------------------------------
-- GET_COUNT_DOCS_OPEN_BY_SPACE
------------------------------------------------------------------

FUNCTION GET_COUNT_OPEN
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_count             NUMBER(9);

BEGIN

    select count(doc_id) into v_count from pn_doc_by_space_view
    where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                            where space_id = in_space_id)
    and doc_status_id not in (400);

    return v_count;

END; -- get_count_docs_by_space


------------------------------------------------------------------
-- GET_COUNT_DOCS_CLOSED_BY_SPACE
------------------------------------------------------------------

FUNCTION GET_COUNT_CLOSED
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_count             NUMBER(9);

BEGIN

    select count(doc_id) into v_count from pn_doc_by_space_view
    where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                            where space_id = in_space_id)
    and doc_status_id = 400;

    return v_count;

END; -- get_count_docs_by_space


------------------------------------------------------------------
-- GET_COUNT_DOCS_CLOSED_BY_SPACE
------------------------------------------------------------------

FUNCTION GET_COUNT_CLOSED_LAST_WEEK
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_count             NUMBER(9);

BEGIN

    select count(doc_id) into v_count from pn_doc_by_space_view
    where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                            where space_id = in_space_id)
    and doc_status_id = 400
    and date_modified > (SYSDATE - 7);

    return v_count;

END; -- get_count_docs_by_space


------------------------------------------------------------------
-- GET_NEXT_REPOSITORY_BASE_ID
------------------------------------------------------------------

FUNCTION GET_NEXT_REPOSITORY_BASE_ID
RETURN NUMBER

AS
    v_next_repository_id             pn_doc_repository_base.repository_id%type := 0;
    v_current_repository_id     pn_doc_repository_base.repository_id%type := 0;

BEGIN

    select repository_sequence into v_current_repository_id
        from pn_next_doc_repository;

    select min(repository_id) into v_next_repository_id from pn_doc_repository_base
        where repository_id > v_current_repository_id
        and is_active = 1;

      if (v_next_repository_id IS NULL) then

        select min(repository_id) into v_next_repository_id from pn_doc_repository_base
            where is_active = 1;

    end if;

    if (v_next_repository_id IS NOT NULL) then

        update pn_next_doc_repository
            set repository_sequence = v_next_repository_id;

    end if;

    return v_current_repository_id;

END;


FUNCTION GET_CONTAINER_FOR_DOC_ID
(    i_doc_id        pn_document.doc_id%type    )
RETURN NUMBER

AS

BEGIN

   return get_container_for_object_id (i_doc_id);

END;


FUNCTION GET_CONTAINER_FOR_object_ID
(    i_object_id        pn_object.object_id%type    )
RETURN NUMBER

AS

    v_container_id  pn_doc_container.doc_container_id%type;

BEGIN

    select distinct doc_container_id into v_container_id
    from pn_doc_container_has_object where object_id = i_object_id;

    return v_container_id;

END;


function get_doc_space_for_id
(
    i_id pn_object.object_id%type
) return number

as

    v_container_id pn_doc_container.doc_container_id%type;
    v_doc_space_id pn_doc_space.doc_space_id%type;

begin

    v_container_id := get_container_for_object_id (i_id);

    select doc_space_id into v_doc_space_id
    from pn_doc_space_has_container where doc_container_id = v_container_id;

    return v_doc_space_id;

end;




FUNCTION GET_SPACE_FOR_ID
(    i_id        pn_object.object_id%type    )
RETURN NUMBER

AS

    v_container_id  pn_doc_container.doc_container_id%type;
    v_doc_space_id  pn_doc_space.doc_space_id%type;
    v_owner_id      pn_object.object_id%type;

BEGIN

    v_container_id := get_container_for_doc_id ( i_id );

    select doc_space_id into v_doc_space_id
    from pn_doc_space_has_container where doc_container_id = v_container_id;

    select space_id into v_owner_id
    from pn_space_has_doc_space where doc_space_id = v_doc_space_id;

    return v_owner_id;

END;



FUNCTION GET_SPACE_FOR_CONTAINER_ID
(    i_container_id        pn_doc_container.doc_container_id%type    )
RETURN NUMBER

AS

    v_doc_space_id  pn_doc_space.doc_space_id%type;
    v_owner_id      pn_object.object_id%type;

BEGIN

    select doc_space_id into v_doc_space_id
    from pn_doc_space_has_container where doc_container_id = i_container_id;

    select space_id into v_owner_id
    from pn_space_has_doc_space where doc_space_id = v_doc_space_id;

    return v_owner_id;

END;

-----------------------------------------------------------------
-- PROCEDURE:  COPY_CONTAINER
-- DOES NOT COMMIT OR ROLLBACK
-- Copies a container, but NONE of its contents
-----------------------------------------------------------------
procedure copy_container (
    i_from_container_id         in      pn_doc_container.doc_container_id%type,
    i_new_parent_container_id   in      pn_doc_container.doc_container_id%type,
    i_creator_id                in      pn_person.person_id%type,
    o_new_container_id          out     pn_doc_container.doc_container_id%type,
    o_return_value              out     number
)
is
    -- variable declaration
    v_new_container_id pn_doc_container.doc_container_id%type;
    v_container_rec pn_doc_container%rowtype;
    v_dshc_rec pn_doc_space_has_container%rowtype;
    v_timestamp date := sysdate;
begin
    -- Get current container record
    select *
      into v_container_rec
      from pn_doc_container
     where doc_container_id = i_from_container_id
       and lower (record_status) = 'a';

    if (v_container_rec.is_hidden = 0) then
        if (f_verify_unique_name (
                v_container_rec.container_name,
                i_new_parent_container_id,
                g_container_object_type
            )
           ) then
            -- register new container in pn_object
            v_new_container_id := base.create_object (
                                      g_container_object_type,
                                      i_creator_id,
                                      g_active_record_status
                                  );

            -- create new document container
            insert into pn_doc_container
                        (doc_container_id, container_name, container_description,
                         record_status, modified_by_id, date_modified, crc)
                 values (v_new_container_id, v_container_rec.container_name,
                         v_container_rec.container_description,
                         v_container_rec.record_status, i_creator_id,
                         v_timestamp, v_timestamp);

            -- apply default object permissions for the object
            security.apply_document_permissions (
                v_new_container_id,
                i_new_parent_container_id,
                g_container_object_type,
                get_space_for_container_id (v_container_rec.doc_container_id),
                i_creator_id
            );
            -- Get the doc_space for the parent container
            -- and insert the new container into that doc space
            select *
              into v_dshc_rec
              from pn_doc_space_has_container
             where doc_container_id = i_new_parent_container_id;

            insert into pn_doc_space_has_container
                        (doc_space_id, doc_container_id, is_root)
                 values (v_dshc_rec.doc_space_id, v_new_container_id, 0);

            -- make the new container an object of the parent container
            insert into pn_doc_container_has_object
                        (doc_container_id, object_id)
                 values (i_new_parent_container_id, v_new_container_id);

            -- SUCCESS --
            o_new_container_id := v_new_container_id;
            o_return_value := BASE.OPERATION_SUCCESSFUL;
        else
            -- NAME VIOLATION
            o_return_value := BASE.DOC_UNIQUE_NAME_CONSTRAINT;
        end if;
    end if;
exception
    when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error ('DOCUMENT.COPY_CONTAINER', sqlcode, sqlerrm);
        end;
end; -- PROCEDURE COPY_CONTAINER


-----------------------------------------------------------------
-- COPY_DOCUMENT
-- DOES NOT COMMIT OR ROLLBACK
-- Copies a document
-----------------------------------------------------------------
procedure copy_document (
    i_from_document_id   in     pn_document.doc_id%type,
    i_to_container_id    in     pn_doc_container.doc_container_id%type,
    i_creator_id         in     pn_person.person_id%type,
    i_discussion_group_description IN VARCHAR2,
    o_new_document_id    out    pn_document.doc_id%type,
    o_return_value       out    number
)
is
    -- variable declaration
    v_new_doc_id pn_document.doc_id%type;
    v_new_version_id pn_doc_version.doc_version_id%type;
    v_new_content_id pn_doc_content_element.doc_content_id%type;
    v_current_version_id pn_doc_version.doc_version_id%type;
    v_discussion_group_id pn_discussion_group.discussion_group_id%type;
    v_welcome_message_post_clob pn_post_body_clob.clob_field%type;
    v_discussion_grp_charter_clob pn_discussion_group.discussion_group_charter_clob%type;
    v_doc_record pn_document%rowtype;
    v_version_rec pn_doc_version%rowtype;
    v_content_rec pn_doc_content_element%rowtype;
    v_timestamp date := sysdate;
    --v_status number;

begin
    select *
      into v_doc_record
      from pn_document
     where doc_id = i_from_document_id;

    if (document.f_verify_unique_name (
            v_doc_record.doc_name,
            i_to_container_id,
            g_document_object_type
        )
       ) then
        v_new_doc_id := base.create_object (
                            g_document_object_type,
                            i_creator_id,
                            g_active_record_status
                        );
        v_new_version_id := base.create_object (
                                g_document_version_object_type,
                                i_creator_id,
                                g_active_record_status
                            );
        select pn_object_sequence.nextval
          into v_new_content_id
          from dual;

        insert into pn_document
                    (doc_id, doc_name, doc_type_id, doc_description,
                     current_version_id, doc_status_id, crc, record_status)
             values (v_new_doc_id, v_doc_record.doc_name,
                     v_doc_record.doc_type_id, v_doc_record.doc_description,
                     v_new_version_id, v_doc_record.doc_status_id, v_timestamp,
                     g_active_record_status);

        -- apply default object permissions for the document
        security.apply_document_permissions (
            v_new_doc_id,
            i_to_container_id,
            g_document_object_type,
            get_space_for_id (v_doc_record.doc_id),
            i_creator_id
        );

        select *
          into v_version_rec
          from pn_doc_version
         where doc_version_id = v_doc_record.current_version_id;

        insert into pn_doc_version
                    (doc_version_id, doc_id, doc_version_name, source_file_name,
                     doc_description, date_modified, modified_by_id,
                     is_checked_out, checked_out_by_id, date_checked_out,
                     doc_comment, doc_version_num, doc_version_label,
                     checkout_due, doc_author_id, short_file_name, crc,
                     record_status)
             values (v_new_version_id, v_new_doc_id,
                     v_version_rec.doc_version_name,
                     v_version_rec.source_file_name,
                     v_version_rec.doc_description, v_version_rec.date_modified,
                     v_version_rec.modified_by_id, 0, -- Will not carry checked out documents
                                                     null, -- see above
                                                          null,
                     'Copied Document', 1, -- will renumber as first version for new document
                                          null, null, i_creator_id,
                     v_version_rec.short_file_name, v_timestamp,
                     g_active_record_status);

        select *
          into v_content_rec
          from pn_doc_content_element
         where doc_content_id = (select doc_content_id
                                   from pn_doc_version_has_content
                                  where doc_version_id =
                                                v_doc_record.current_version_id
                                    and doc_id = v_doc_record.doc_id);

        -- create the content element(s) for the document version
        insert into pn_doc_content_element
                    (doc_content_id, doc_format_id, record_status, file_size,
                     file_handle, repository_id)
             values (v_new_content_id, v_content_rec.doc_format_id,
                     g_active_record_status, v_content_rec.file_size,
                     v_content_rec.file_handle,
                     DOCUMENT.GET_NEXT_REPOSITORY_BASE_ID);

        -- link the content to the doc_version
        insert into pn_doc_version_has_content
                    (doc_id, doc_version_id, doc_content_id)
             values (v_new_doc_id, v_new_version_id, v_new_content_id);

        -- insert the doc into the correct container
        insert into pn_doc_container_has_object
                    (doc_container_id, object_id)
             values (i_to_container_id, v_new_doc_id);

        -- create the docs discussion group
        -- We leave the charter CLOB empty and the welcome message
        discussion.create_discussion_group (
            null,
            get_space_for_id (v_doc_record.doc_id),
            v_new_doc_id,
            v_doc_record.doc_name,
            i_creator_id,
            i_discussion_group_description ,
            0, -- do not creaet a welcome message
            NULL,
            v_discussion_group_id,
            v_welcome_message_post_clob,
            v_discussion_grp_charter_clob
        );
        -- apply the documents permissions to the discussion group
        security.apply_document_permissions (
            v_discussion_group_id,
            v_new_doc_id,
            'discussion_group',
            get_space_for_id (v_doc_record.doc_id),
            i_creator_id
        );
        -- SUCCESS --
        o_new_document_id := v_new_doc_id;
        o_return_value := BASE.OPERATION_SUCCESSFUL;
    else
        o_return_value := BASE.DOC_UNIQUE_NAME_CONSTRAINT;
    end if;
exception
    when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error ('DOCUMENT.COPY_DOCUMENT', sqlcode, sqlerrm);
        end;
end; -- PROCEDURE copy_document

-----------------------------------------------------------------
-- COPY_BOOKMARK
-- DOES NOT COMMIT OR ROLLBACK
-- Copies a bookmark
-----------------------------------------------------------------
procedure copy_bookmark (
    i_from_bookmark_id  in      pn_bookmark.bookmark_id%type,
    i_to_container_id   in      pn_doc_container.doc_container_id%type,
    i_creator_id        in      pn_person.person_id%type,
    o_new_bookmark_id   out     pn_bookmark.bookmark_id%type,
    o_return_value      out     number
)
is
    -- variable declaration
    v_new_bookmark_id pn_document.doc_id%type;
    v_bookmark_rec pn_bookmark%rowtype;
    v_sysdate date;
    v_space_id number;
begin
    select sysdate
      into v_sysdate
      from dual;

    -- Get bookmark to copy from
    select *
      into v_bookmark_rec
      from pn_bookmark
     where bookmark_id = i_from_bookmark_id;

    -- Create new bookmark id
    v_new_bookmark_id :=
                  base.create_object (g_bookmark_object_type, i_creator_id, 'A');

    -- Copy bookmark
    insert into pn_bookmark
                (bookmark_id, name, description, url, status_id, owner_id,
                 comments, modified_date, modified_by_id, record_status, crc)
         values (v_new_bookmark_id, v_bookmark_rec.name,
                 v_bookmark_rec.description, v_bookmark_rec.url,
                 v_bookmark_rec.status_id, i_creator_id, v_bookmark_rec.comments,
                 v_sysdate, i_creator_id, g_active_record_status, v_sysdate);

    -- Find the space in which owning container belongs
    v_space_id := get_space_for_container_id (i_to_container_id);
    -- apply default object permissions for the document
    security.apply_document_permissions (
        v_new_bookmark_id,
        i_to_container_id,
        g_bookmark_object_type,
        v_space_id,
        i_creator_id
    );
    -- finally, add this bookmark to it's parent container
    add_object_to_container (v_new_bookmark_id, i_to_container_id);

    -- SUCCESS --
    o_new_bookmark_id := v_new_bookmark_id;
    o_return_value := BASE.OPERATION_SUCCESSFUL;

exception
    when others then
        begin
            o_return_value := BASE.PLSQL_EXCEPTION;
            base.log_error ('DOCUMENT.COPY_BOOKMARK', sqlcode, sqlerrm);
        end;
end copy_bookmark;




FUNCTION has_links
(
    i_id IN pn_object.object_id%type
) RETURN NUMBER
AS

    v_has_links             NUMBER  := 0;
    v_count                 NUMBER  := 0;

BEGIN

    select
      count(*) into v_count
    from
      pn_object_link ol,
      pn_object o1,
      pn_object o2
    where
      o1.object_id = ol.to_object_id
      and o1.record_status = 'A'
      and o2.object_id = ol.from_object_id
      and o2.record_status = 'A'
      and (ol.from_object_id = i_id or ol.to_object_id = i_id);

    if (v_count > 0) then
        v_has_links := 1;
    end if;

    return v_has_links;


EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        v_has_links := 0;
        return v_has_links;
      END;


END; -- has_links

END; -- Package Body DOCUMENT
/

