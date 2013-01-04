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
CREATE OR REPLACE PACKAGE forms IS

   -- Package constants
   G_form_object_type           constant pn_object.object_type%type := 'form';
   G_field_object_type          constant pn_object.object_type%type := 'form_field';
   G_list_object_type           constant pn_object.object_type%type := 'form_list';
   G_domain_object_type         constant pn_object.object_type%type := 'form_domain';
   G_domain_value_object_type   constant pn_object.object_type%type := 'form_domain_value';
   G_filter_value_object_type   constant pn_object.object_type%type := 'form_filter_value';
   G_selection_menu_id          constant number := 3;
   G_person_selection_id        constant number := 9;

   -- Raise-able errors
   unspecified_error  exception;
   pragma exception_init(unspecified_error, -20000);
   space_not_found    exception;



----------------------------------------------------------------------
-- COPY_ALL
-- Copies all forms for a space to another space
----------------------------------------------------------------------
   procedure copy_all (
        i_from_space_id     in varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        o_return_value      out number);


----------------------------------------------------------------------
-- COPY_FORM
-- Copies a single form from one space to another space.
-- New objects ids are assigned so that the two forms are completely
-- independant copies of each other.
----------------------------------------------------------------------
  procedure copy_form (
        i_from_space_id in number,
        i_to_space_id in number,
        i_class_id in number,
        i_is_from_space_owner in number,
        i_created_by_id in number,
        o_new_form_id out number);
--
-- Simplied COPY_FORM that copies within the from space
--
procedure copy_form_within_space (
    i_from_space_id in number,
    i_from_form_id in number,
    i_created_by_id in number,
    o_new_form_id out number,
    o_return_status out number);

----------------------------------------------------------------------
-- copy_form_to_space
-- Copies a single form from one space to another space
-- Determines whether the from space owns the form or not
-- then calls COPY_FORM
----------------------------------------------------------------------
procedure copy_form_to_space (
    i_from_space_id in number,
    i_to_space_id in number,
    i_from_form_id in number,
    i_created_by_id in number,
    o_new_form_id out number,
    o_return_status out number);

----------------------------------------------------------------------
-- COPY_FIELD
-- Copies a single field into the specified form
----------------------------------------------------------------------
    procedure copy_field (
        i_from_field_id     in number,
        i_to_class_id       in number,
        i_created_by_id     in number,
        o_new_field_id      out number,
        o_return_status     out number);

----------------------------------------------------------------------
-- LOG_EVENT
-- Logs the event that happens to the NEWS item
----------------------------------------------------------------------
   PROCEDURE log_event
    (
        object_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    );
----------------------------------------------------------------------


END; -- Package Specification FORM
/

