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
CREATE OR REPLACE PACKAGE methodology IS

    G_methodology_object_type              pn_object.object_type%type := 'methodology';
    G_active_record_status                 pn_methodology_space.record_status%type := 'A';
    TYPE ReferenceCursor            IS REF CURSOR;

----------------------------------------------------------------------
-- CREATE_METHODOLOGY
----------------------------------------------------------------------
    PROCEDURE create_methodology
      (
        i_name IN pn_methodology_space.methodology_name%type,
        i_description IN pn_methodology_space.methodology_desc%type,
        i_parent_space_id IN pn_methodology_space.methodology_id%type,
        i_creator_id IN pn_person.person_id%type,
        i_industry_id IN pn_industry_classification.industry_id%type,
        i_category_id IN pn_category.category_id%type,
        i_is_global IN pn_methodology_space.is_global%type,
        i_is_use_scenario_null IN NUMBER,
        o_use_scenario_clob OUT pn_methodology_space.use_scenario_clob%type,
        o_object_id OUT number,
        i_based_on_space_id IN pn_methodology_space.based_on_space_id%type
      );

----------------------------------------------------------------------
-- MODIFY_METHODOLOGY
-- Update an existing methodology
----------------------------------------------------------------------
procedure modify_methodology (
    i_methodology_id    in number,
    i_name              in pn_methodology_space.methodology_name%type,
    i_description       in pn_methodology_space.methodology_desc%type,
    i_parent_space_id   in pn_methodology_space.methodology_id%type,
    i_modified_by_id    in pn_person.person_id%type,
    i_industry_id       in pn_industry_classification.industry_id%type,
    i_category_id       in pn_category.category_id%type,
    i_is_global 		in pn_methodology_space.is_global%type,
    i_crc               in date,
    i_is_use_scenario_null in number,
    o_use_scenario_clob out pn_methodology_space.use_scenario_clob%type
);

----------------------------------------------------------------------
-- GET_METHODOLOGY_FOR_USER
-- Retrieves a list of methodologies a user is able to use when creating
-- projects.
----------------------------------------------------------------------
FUNCTION get_methodology_for_user (
         i_user_id IN pn_person.person_id%TYPE
) RETURN ReferenceCursor;
END; -- Package Specification METHODOLOGY
/

