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
CREATE OR REPLACE PACKAGE BODY sharing
IS
--
-- To modify this template, edit file PKGBODY.TXT in TEMPLATE
-- directory of SQL Navigator
--
-- Purpose: Briefly explain the functionality of the package body
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Sachin      10-Jul-07  Added a function.


--------------------------------------------------------------------
-- GET_PROPAGATE_TO_CHILDREN
--------------------------------------------------------------------

   FUNCTION GET_PROPAGATE_TO_CHILDREN (
       in_plan_id NUMBER
   ) return NUMBER
   AS
        v_propagate NUMBER;
   BEGIN
     SELECT
       COUNT(*) into v_propagate
     FROM
       pn_shareable
     WHERE
       object_id = in_plan_id
       and propagate_to_children = 1;

     IF (v_propagate > 0) THEN
            RETURN 1;
     END IF;
     RETURN v_propagate;
   END;

   PROCEDURE STORE_SHARE (
       i_object_id in NUMBER,
       i_permission_type in NUMBER,
       i_container_id in NUMBER,
       i_space_id in NUMBER,
       i_propagate_to_children in NUMBER,
       i_allowable_actions in NUMBER
   ) IS
      -- Enter the procedure variables here. As shown below
      share_exists number;
   BEGIN
      select count(object_id) into share_exists from pn_shareable where object_id = i_object_id;

      if (share_exists > 0) then
         update pn_shareable
         set object_id = i_object_id,
             permission_type = i_permission_type,
             container_id = i_container_id,
             space_id = i_space_id,
             propagate_to_children = i_propagate_to_children,
             allowable_actions = i_allowable_actions
         where
             object_id = i_object_id;
      else
         insert into pn_shareable
            (object_id, permission_type, container_id, space_id, propagate_to_children, allowable_actions)
         values
            (i_object_id, i_permission_type, i_container_id, i_space_id, i_propagate_to_children, i_allowable_actions);
      end if;
   END;

   PROCEDURE INHERIT_SHARE_FROM_PLAN (
       i_plan_id in NUMBER
   ) IS
     v_permission_type NUMBER;
     v_space_id NUMBER;
   BEGIN
     --First, find the permission type and space type that the plan currently has.
     select
       permission_type, space_id into v_permission_type, v_space_id
     from
       pn_shareable
     where
       object_id = i_plan_id;

     --If the sharing type for the schedule is more permissive than the sharing type
     --for the task, we'll use the plan's sharing type.
     if (v_permission_type = 1) then
       delete from pn_shareable_permissions sp
       where exists
         (select 1 from pn_plan_has_task pht
          where pht.plan_id = i_plan_id
            and pht.task_id = sp.object_id);

       update pn_shareable shrd
       set permission_type = v_permission_type
       where exists
         (select 1 from pn_plan_has_task pht
          where pht.plan_id = i_plan_id
            and pht.task_id = shrd.object_id);
     elsif (v_permission_type = 2) then
       --Add any additional permissions that the schedule has to the object's
       --permissions
       insert into pn_shareable_permissions
       (object_id, permitted_object_id, share_type)
       select
         pht.task_id, sp.permitted_object_id, sp.share_type
       from
         pn_plan_has_task pht,
         pn_shareable_permissions sp
       where
         sp.object_id = i_plan_id
         and pht.plan_id = i_plan_id
         and not exists
           (select 1 from pn_shareable_permissions sp2
            where object_id = pht.task_id
              and sp.permitted_object_id = sp2.permitted_object_id);
     end if;

     --Insert into shareable any objects that didn't already have sharing enabled
     insert into pn_shareable
     (object_id, permission_type, container_id, space_id)
     select
       pht.task_id, v_permission_type, i_plan_id, v_space_id
     from
       pn_plan_has_task pht,
       pn_object o
     where
       pht.task_id = o.object_id
       and o.record_status = 'A'
       and plan_id = i_plan_id
       and not exists (select 1 from pn_shareable where object_id = pht.task_id);
   END;

   PROCEDURE INHERIT_FROM_PARENT (
       i_parent_id in NUMBER,
       i_child_id in NUMBER
   )
   IS
   BEGIN
     insert into pn_shareable
       (object_id, permission_type, container_id, space_id, propagate_to_children)
     select
       i_child_id, permission_type, container_id, space_id, propagate_to_children
     from
       pn_shareable
     where
       object_id = i_parent_id
       and propagate_to_children = 1;
   END;

   PROCEDURE ADD_EXTERNAL (
       i_exported_object_id NUMBER,
       i_export_container_id NUMBER,
       i_import_container_id NUMBER,
       i_import_space_id NUMBER,
       i_imported_object_id NUMBER,
       i_read_only NUMBER
   ) IS
      export_exists number;
      export_space_id number;
   BEGIN
      select count(i_imported_object_id) into export_exists
      from
        pn_shared
      where
        exported_object_id = i_exported_object_id
        and import_container_id = i_import_container_id;

      if (export_exists > 0) then
        update pn_shared
        set import_space_id = i_import_space_id,
            imported_object_id = i_imported_object_id,
            read_only = i_read_only
        where
            exported_object_id = i_exported_object_id
            and import_container_id = i_import_container_id;
      else
        --Find the space id of the exporting object
        select space_id into export_space_id
        from pn_shareable
        where object_id = i_exported_object_id;

        insert into pn_shared
          (exported_object_id, import_container_id, import_space_id, imported_object_id, export_space_id, export_container_id, read_only)
        values
          (i_exported_object_id, i_import_container_id, i_import_space_id, i_imported_object_id, export_space_id, i_export_container_id, i_read_only);
      end if;
   END;

END;
/

