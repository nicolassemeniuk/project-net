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
CREATE OR REPLACE Package vote
  IS


TYPE ReferenceCursor            IS REF CURSOR;


PROCEDURE create_vote
    (
        in_space_id IN number,
        in_question IN varchar2,
        in_title    IN varchar2,
        in_whoami   IN number,
        object_id   OUT number
    );

PROCEDURE create_response
    (
        in_vote_id IN pn_vote_response.vote_id%type,
        in_response IN pn_vote_response.response%type
    );

PROCEDURE add_voter
    (
        in_vote_id IN pn_person_has_vote.vote_id%type,
        in_person_id IN pn_person_has_vote.person_id%type
    );

FUNCTION GET_CONTAINER_LIST
( space_id IN pn_vote_questionair.space_id%type )
    RETURN ReferenceCursor;


END; -- Package Specification VOTE
/

