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
CREATE OR REPLACE PACKAGE sharing IS
   DEFAULT_PROPAGATION NUMBER := 0;
   DEFAULT_ALLOWABLE_ACTIONS NUMBER := 2;

   -- Returns of we have to propagate permissions to children
   FUNCTION GET_PROPAGATE_TO_CHILDREN (
       in_plan_id NUMBER
   ) return NUMBER;

   -- The sharing package is used to facilitate cross space sharing.  It allows
   -- objects to be "shared" from one space so others can see the share.
   PROCEDURE STORE_SHARE (
       i_object_id in NUMBER,
       i_permission_type in NUMBER,
       i_container_id in NUMBER,
       i_space_id in NUMBER,
       i_propagate_to_children in NUMBER,
       i_allowable_actions in NUMBER
   );

   --Set all tasks shared that can be shared.  (That is, tasks that are not
   --already shared from another schedule -- transitive sharing is not allowed.)
   --Tasks will get their plan's permission type and their permissions.
   PROCEDURE INHERIT_SHARE_FROM_PLAN (
       i_plan_id in NUMBER
   );

   PROCEDURE INHERIT_FROM_PARENT (
       i_parent_id in NUMBER,
       i_child_id in NUMBER
   );

   PROCEDURE ADD_EXTERNAL (
       i_exported_object_id NUMBER,
       i_export_container_id NUMBER,
       i_import_container_id NUMBER,
       i_import_space_id NUMBER,
       i_imported_object_id NUMBER,
       i_read_only NUMBER
   );
END; -- Package spec
/

