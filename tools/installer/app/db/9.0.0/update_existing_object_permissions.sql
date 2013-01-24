-- This script updates all object permissions as per default permissions of roles

-- Why this script is requied?
--    When we create a role, and update role permissions, these permissions are updated for role.
--    Objects created after the role creation inherit the default permissions of roles.
--    If the role permissions are updated after creation of some pnet objects then 
--    the default permissions are applied to role only and the existing objects created 
--    still have old permissions action bits set.

-- What does this script do?
--    This script re-applies the object permissions with default role permissions for all objects for project type space

-- How to avoid this in future?
--    Procedure security.store_default_obj_permission is modified to re-apply role permissions to existing objects

-- How to use this script?
--    This script need to be called only once (no harm if called multiple times) and takes couple of seconds only.
--

DECLARE
 
   CURSOR c_old_objects_to_update IS
      SELECT ps.object_id, pop.group_id, pop.actions
      FROM pn_default_object_permission pop, pn_object_space ps, pn_object po, pn_project_space ps, pn_group pg
      WHERE pop.space_id = ps.space_id AND
        ps.object_id = po.object_id AND
        po.object_type = pop.object_type AND
        ps.space_id = ps.project_id AND
        pop.group_id = pg.group_id AND
        pg.is_principal <> '1' AND
        po.record_status = 'A' AND
        ps.record_status = 'A' AND
        pg.record_status = 'A';

   v_result NUMBER;

BEGIN

    FOR rec in c_old_objects_to_update LOOP
        -- store/update permissions in pn_object_permissions
        security.store_object_permission(rec.object_id, rec.group_id, rec.actions, v_result);          
    END LOOP;

EXCEPTION
    WHEN others THEN
        BEGIN
            dbms_output.put_line('Error occurred while updating old object permissions.');
            RAISE;
        END;
END;
/