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
CREATE OR REPLACE PACKAGE document IS
-----------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Robin       29-Mar-00  Created package for Phil's procedures.
-- Robin       25-Apr-00  Added remove_object_from_container.

-----------------------------------------------------------------

  -- Package Variables

    -- to be used to contain the return value for apply permissions
    v_applied_security                  BOOLEAN := false;


  -- Package Global Variables (be careful...)

    G_document_object_type              pn_object.object_type%type := 'document';
    G_document_version_object_type      pn_object.object_type%type := 'document_version';
    G_container_object_type             pn_object.object_type%type := 'doc_container';
    G_bookmark_object_type              pn_object.object_type%type := 'bookmark';
    G_active_record_status              pn_document.record_status%type := 'A';


      TYPE ReferenceCursor            IS REF CURSOR;

  PROCEDURE copy_container
  (
    i_from_container_id         in      pn_doc_container.doc_container_id%type,
    i_new_parent_container_id   in      pn_doc_container.doc_container_id%type,
    i_creator_id                in      pn_person.person_id%type,
    o_new_container_id          out     pn_doc_container.doc_container_id%type,
    o_return_value              out     number
  );


  PROCEDURE copy_document
  (
    i_from_document_id   in     pn_document.doc_id%type,
    i_to_container_id    in     pn_doc_container.doc_container_id%type,
    i_creator_id         in     pn_person.person_id%type,
    i_discussion_group_description IN VARCHAR2,
    o_new_document_id    out    pn_document.doc_id%type,
    o_return_value       out    number
  );

  PROCEDURE copy_bookmark
  (
    i_from_bookmark_id  in      pn_bookmark.bookmark_id%type,
    i_to_container_id   in      pn_doc_container.doc_container_id%type,
    i_creator_id        in      pn_person.person_id%type,
    o_new_bookmark_id   out     pn_bookmark.bookmark_id%type,
    o_return_value      out     number
  );


  procedure create_doc_space
  (
    i_in_space_id in pn_doc_space.doc_space_id%type,
    i_creator_id in pn_person.person_id%type,
    o_doc_space_id out number,
    o_return_value out number
  );


  PROCEDURE create_doc
    (
        tmp_doc IN varchar2,
        container_id IN varchar2,
        whoami IN varchar2,
        space_id IN varchar2,
        ignore_name_constraint IN NUMBER,
        i_discussion_group_description IN VARCHAR2,
        v_return_value OUT NUMBER
    );

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
);


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
);

  FUNCTION IS_PARENT_CONTAINER
    (
        parent_container_id IN varchar2,
        child_container_id IN varchar2
    ) RETURN NUMBER;

  FUNCTION GET_PATH_FOR_OBJECT
    (
        object_id IN varchar2,
        root_object_id IN varchar2
    ) RETURN ReferenceCursor;

  FUNCTION GET_PARENT_CONTAINER_INFO
    (
        object_id IN varchar2
    ) RETURN ReferenceCursor;

  FUNCTION get_parent_space_for_object
    (
        object_id IN varchar2
    ) RETURN ReferenceCursor;

  FUNCTION GET_CONTAINER_LIST
     (
        doc_space_id IN pn_doc_space.doc_space_id%type
      ) RETURN ReferenceCursor;

  PROCEDURE create_container
    (
        in_container_id IN varchar2,
        whoami IN varchar2,
        folder_name IN varchar2,
        description IN varchar2,
        space_id    IN varchar2,
        is_hidden   IN varchar2,
        object_id OUT number,
        v_return_value OUT number
    );

  PROCEDURE modify_container
    (
        parent_id IN varchar2,
        my_container_id IN varchar2,
        folder_name IN varchar2,
        description in varchar2,
        whoami IN varchar2,
        is_hidden IN varchar2,
        orig_crc IN date,
        v_return_value OUT number
    );

  PROCEDURE add_object_to_container
    (
        in_object_id IN varchar2,
        in_container_id IN varchar2
    );

  PROCEDURE check_in
    (
        tmp_doc IN varchar2,
        whoami  IN varchar2,
        space_id IN varchar2,
        orig_crc IN date,
        v_return_value OUT number
    );

  PROCEDURE check_out
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        cko_due IN date,
        notes IN varchar2,
        orig_crc IN date,
        v_return_value OUT number
    );

  PROCEDURE remove_doc
    (
        doc_id IN varchar2,
        last_modified IN date,
        record_status IN varchar2,
        whoami IN varchar2
    );

  PROCEDURE remove_container
    (
        container_id IN varchar2,
        record_status IN varchar2,
        whoami IN varchar2
    );

  PROCEDURE log_event
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    );

  PROCEDURE move_object
    (
        in_object_id IN varchar2,
        in_container_id IN varchar2
    );

  PROCEDURE undo_check_out
    (
        doc_id IN varchar2,
        whoami IN varchar2,
        orig_crc IN date,
        v_return_value OUT number
    );

  PROCEDURE modify_properties
    (
        tmp_doc IN varchar2,
        whoami  IN varchar2,
        orig_crc IN date,
        return_value OUT NUMBER
    );

 Procedure REMOVE_OBJECT_FROM_CONTAINER
    (
        in_object_id IN varchar2,
        in_container_id IN varchar2
    );

Function F_VERIFY_UNIQUE_NAME
(
    new_name IN varchar2,
    containerID IN pn_doc_container.doc_container_id%type,
    object_type IN pn_object.object_type%type
)
RETURN BOOLEAN;

Function F_VERIFY_CRC
(
    v_orig_crc IN DATE,
    v_current_crc IN DATE
)
RETURN BOOLEAN;

FUNCTION GET_COUNT
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER;

FUNCTION GET_COUNT_CLOSED_LAST_WEEK
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER;

FUNCTION GET_COUNT_CLOSED
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER;

FUNCTION GET_COUNT_OPEN
(
    in_space_id IN pn_object.object_id%type
) RETURN NUMBER;

FUNCTION GET_NEXT_REPOSITORY_BASE_ID
RETURN NUMBER;

FUNCTION GET_CONTAINER_FOR_DOC_ID
(    i_doc_id        pn_document.doc_id%type    )
RETURN NUMBER;

FUNCTION GET_CONTAINER_FOR_object_ID
(    i_object_id        pn_object.object_id%type    )
RETURN NUMBER;


FUNCTION GET_SPACE_FOR_ID
(
    i_id IN pn_object.object_id%type
) RETURN NUMBER;

FUNCTION GET_SPACE_FOR_CONTAINER_ID
(    i_container_id        pn_doc_container.doc_container_id%type    )
RETURN NUMBER;

function get_doc_space_for_id
(
    i_id pn_object.object_id%type
) return number;

function has_links
(       i_id IN pn_object.object_id%type )
RETURN NUMBER;

/*
FUNCTION GET_NEXT_REPOSITORY_BASE_PATH
(
    in_repository_id pn_doc_repository_base.repository_id%type
) RETURN pn_doc_repository_base.repository_path%type;
*/


END; -- Package Specification DOCUMENT
/

