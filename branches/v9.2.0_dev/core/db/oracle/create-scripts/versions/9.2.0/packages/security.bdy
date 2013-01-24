create or replace
PACKAGE BODY security IS

---------------------------------------------------------------------
-- STORE_MODULE_PERMISSION
---------------------------------------------------------------------
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  -------------------------------------------
-- Phil Dixon   3/30/00 initial create
-- Roger Bly    4/6/00  added person_id (object creator), give all object permissions to creator.
-- Roger Bly    4/6/00  moved to SECURITY PACKAGE
-- Roger Bly    4/10/00 Fixed bug in create_security_permissions for creator's principal group.
-- Roger Bly    1/7/01  Added procedures for copying groups and permissions to another space.
-- Tim Morrow  10/23/01 Fixed for new column GROUP_TYPE_ID in PN_GROUP
-- Tim Morrow  11/12/01 Added inherit_group procedure
-- Tim Morrow  28/02/03 Fixed CREATE_SECURITY_PERMISSIONS to not rollback and to
--                      raise exceptions that occur

PROCEDURE CREATE_SECURITY_PERMISSIONS
(
    p_object_id IN pn_object.object_id%type,
    p_object_type IN pn_object_type.object_type%type,
    p_space_id IN pn_project_space.project_id%type,
    p_person_id IN pn_person.person_id%type
)
IS

    v_group_id                      pn_group.group_id%type;
    v_space_group                   pn_group.group_id%type;
    v_actions                       pn_object_type.default_permission_actions%type;
    v_creator_principal_group_id    pn_group.group_id%type;
    v_module_id                     pn_module.module_id%type;

    -- All action bits set = (2^16)-1
    G_ALL_ACTIONS NUMBER := 65535;

    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.CREATE_SECURITY_PERMISSIONS';

    -- All default object permissions for the space and object type
    CURSOR  c_default_permissions (the_space_id pn_project_space.project_id%type, the_object_type pn_object_type.object_type%type) IS
        select dop.group_id, dop.actions
        from pn_default_object_permission dop, pn_group g
        where dop.space_id = the_space_id and
        dop.object_type = the_object_type and
        g.group_id = dop.group_id and
        g.is_principal <> 1;

    -- All groups currently defined for the space.
    CURSOR  c_groups (the_space_id pn_project_space.project_id%type) IS
		SELECT g.group_id, g.is_principal
		FROM pn_space_has_group shg, pn_group g
        WHERE shg.space_id = the_space_id and g.group_id = shg.group_id and g.record_status = 'A';

BEGIN

    -- Get the object creator's (person) principal group_id for this space.
    -- The object creator gets all permissions on the object.
    BEGIN
        select
            g.group_id into v_creator_principal_group_id
        from
            pn_space_has_group shg, pn_group g, pn_group_has_person ghp
        where
            shg.space_id = p_space_id and
            ghp.group_id = shg.group_id and
            ghp.person_id = p_person_id and
            g.group_id = shg.group_id and
            g.is_principal = 1;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_creator_principal_group_id := NULL;
    END;

    -- For this group is the creator's principal group,
    -- give them all permissions on the object they are creating.
    IF (v_creator_principal_group_id IS NOT NULL)
    THEN
        INSERT INTO pn_object_permission
          (object_id, group_id, actions)
        values
          (p_object_id, v_creator_principal_group_id, G_ALL_ACTIONS);
    END IF;


    -- COPIES ALL OBJECT PERMISSIONS AND ALL OF THE GROUPS (except principal groups)
    -- FOR THIS PROJECT TO THE OBJECT_PERMISSION TABLE

    OPEN c_default_permissions(p_space_id, p_object_type);
    <<permission_loop>>
	LOOP

		FETCH c_default_permissions INTO v_group_id, v_actions;
		EXIT WHEN c_default_permissions%NOTFOUND;

            IF (v_group_id IS NOT NULL)
            THEN
                INSERT INTO pn_object_permission
                    (object_id, group_id, actions)
                values
                    (p_object_id, v_group_id, v_actions);
            END IF;

    END LOOP permission_loop;
	CLOSE c_default_permissions;

    -- Log exceptions
    EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        BASE.LOG_ERROR(stored_proc_name, SQLCODE, SQLERRM);
        raise;
      END;

END; -- CREATE_SECURITY_PERMISSIONS

---------------------------------------------------------------------
-- APPLY_DOCUMENT_PERMISSIONS
---------------------------------------------------------------------
PROCEDURE APPLY_DOCUMENT_PERMISSIONS
(
    p_object_id IN pn_object.object_id%type,
    p_parent_id IN pn_object.object_id%type,
    p_object_type IN pn_object_type.object_type%type,
    p_space_id IN pn_project_space.project_id%type,
    p_person_id IN pn_person.person_id%type
)
IS

    v_group_id                      pn_group.group_id%type;
    v_actions                       pn_object_type.default_permission_actions%type;
    v_creator_principal_group_id    pn_group.group_id%type;

    -- All action bits set = (2^16)-1
    G_ALL_ACTIONS NUMBER := 65535;

    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.APPLY_DOCUMENT_PERMISSIONS';

    -- All default object permissions for the space system groups
    CURSOR  c_default_permissions (the_space_id pn_project_space.project_id%type, the_object_type pn_object_type.object_type%type) IS
        select dop.group_id, dop.actions
        from pn_default_object_permission dop, pn_group g
        where dop.space_id = the_space_id and
        dop.object_type = the_object_type and
        g.group_id = dop.group_id and
        g.is_system_group = 1 and g.is_principal <> 1;

    -- All default object permissions for the space system groups
    CURSOR  c_parent_permissions (the_parent pn_object_permission.object_id%type) IS
        select group_id, actions
        from pn_object_permission
        where object_id = the_parent;

BEGIN
    -- remove the objects current security settings
    DELETE FROM pn_object_permission
    WHERE object_id = p_object_id;

    IF (p_parent_id IS NOT NULL)
    THEN
        -- the object has a parent, therefore inherit the parents security settings
        OPEN c_parent_permissions(p_parent_id);
        <<permission_loop>>
	    LOOP
		  FETCH c_parent_permissions INTO v_group_id, v_actions;
		  EXIT WHEN c_parent_permissions%NOTFOUND;

            IF (v_group_id IS NOT NULL)
            THEN
                INSERT INTO pn_object_permission
                    (object_id, group_id, actions)
                values
                    (p_object_id, v_group_id, v_actions);
            END IF;

        END LOOP permission_loop;
	    CLOSE c_parent_permissions;


        IF (p_person_id IS NOT NULL)
        THEN
            -- Get the object creator's (person) principal group_id for this space.
            -- The object creator gets all permissions on the object.
            BEGIN
                select
                    g.group_id into v_creator_principal_group_id
                from
                    pn_space_has_group shg, pn_group g, pn_group_has_person ghp
                where
                    shg.space_id = p_space_id and
                    ghp.group_id = shg.group_id and
                    ghp.person_id = p_person_id and
                    g.group_id = shg.group_id and
                    g.is_principal = 1;
            EXCEPTION
            WHEN NO_DATA_FOUND THEN
                v_creator_principal_group_id := NULL;
            END;

            -- For this group is the creator's principal group,
            -- give them all permissions on the object they are creating.
            IF (v_creator_principal_group_id IS NOT NULL)
            THEN
                DELETE FROM pn_object_permission
                WHERE object_id = p_object_id and
                    group_id = v_creator_principal_group_id;

                INSERT INTO pn_object_permission
                    (object_id, group_id, actions)
                values
                    (p_object_id, v_creator_principal_group_id, G_ALL_ACTIONS);
            END IF; -- (v_creator_principal_group_id IS NOT NULL)

        END IF; -- (p_pesron_id IS NOT NULL)
    ELSE
    -- the object does not have a parent therefore system groups get full permissions

    OPEN c_default_permissions(p_space_id, p_object_type);
    <<permission_loop>>
	LOOP

		FETCH c_default_permissions INTO v_group_id, v_actions;
		EXIT WHEN c_default_permissions%NOTFOUND;

            IF (v_group_id IS NOT NULL)
            THEN
                INSERT INTO pn_object_permission
                    (object_id, group_id, actions)
                values
                    (p_object_id, v_group_id, G_ALL_ACTIONS);
            END IF;

    END LOOP permission_loop;
	CLOSE c_default_permissions;

    END IF;

    -- Log exceptions
    EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        BASE.LOG_ERROR(stored_proc_name, sqlcode, sqlerrm);
        raise;
      END;

END; -- APPLY_DOCUMENT_PERMISSIONS

---------------------------------------------------------------------
-- ADD Person To Group
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        27-Apr-00   Creation.

PROCEDURE add_person_to_group
(
    group_id IN pn_group.group_id%type,
    person_id IN pn_person.person_id%type,
    o_status OUT NUMBER
)
IS

stored_proc_name VARCHAR2(100):= 'SECURITY.ADD_PERSON_TO_GROUP';

BEGIN

    insert into pn_group_has_person
        (group_id, person_id)
    values
        (group_id, person_id);

    o_status := success;

EXCEPTION
    WHEN OTHERS THEN
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;
END;


---------------------------------------------------------------------
-- STORE_MODULE_PERMISSION
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation.

PROCEDURE store_module_permission
(
    i_space_id IN NUMBER,
    i_group_id IN NUMBER,
    i_module_id IN NUMBER,
    i_actions IN NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'SECURITY.STORE_MODULE_PERMISSION';

BEGIN

    INSERT INTO PN_MODULE_PERMISSION (space_id, group_id, module_id, actions)
        VALUES (i_space_id, i_group_id, i_module_id, i_actions);

    COMMIT;
    o_status := success;

EXCEPTION
    WHEN e_unique_constraint THEN
        UPDATE PN_MODULE_PERMISSION
            SET actions = i_actions
            WHERE space_id = i_space_id
            AND group_id = i_group_id
            AND module_id = i_module_id;

        IF SQL%NOTFOUND THEN
            o_status := no_parent_key;
            ROLLBACK;
            RETURN;
        END IF;

        COMMIT;
        o_status := success;

    WHEN e_null_constraint THEN
        o_status := null_field;

    WHEN e_no_parent_key THEN
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END store_module_permission;


---------------------------------------------------------------------
-- STORE_OBJECT_PERMISSION
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation.

PROCEDURE store_object_permission
(
    i_object_id IN NUMBER,
    i_group_id IN NUMBER,
    i_actions IN NUMBER,
    o_status OUT NUMBER
)

IS

stored_proc_name VARCHAR2(100):= 'SECURITY.STORE_OBJECT_PERMISSION';

BEGIN

    INSERT INTO PN_OBJECT_PERMISSION (object_id, group_id, actions)
        VALUES (i_object_id, i_group_id, i_actions);

    COMMIT;
    o_status := success;

EXCEPTION
    WHEN e_unique_constraint THEN
        UPDATE PN_OBJECT_PERMISSION
            SET actions = i_actions
            WHERE object_id = i_object_id
            AND group_id = i_group_id;

        IF SQL%NOTFOUND THEN
            o_status := no_parent_key;
            ROLLBACK;
            RETURN;
        END IF;

        COMMIT;
        o_status := success;

    WHEN e_null_constraint THEN
        o_status := null_field;

    WHEN e_no_parent_key THEN
        o_status := no_parent_key;

    WHEN OTHERS THEN
        o_status := generic_error;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;

END store_object_permission;


---------------------------------------------------------------------
-- STORE_DEFAULT_OBJ_PERMISSION
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       06-Apr-00   Creation.
-- Avinash     25-Nov-08   apply default permissions to old objects

PROCEDURE store_default_obj_permission
(
    i_space_id IN NUMBER,
    i_object_type IN VARCHAR2,
    i_group_id IN NUMBER,
    i_actions IN NUMBER,
    o_status OUT NUMBER
)

IS

    stored_proc_name VARCHAR2(100):= 'SECURITY.STORE_DEFAULT_OBJ_PERMISSION';

    CURSOR  c_old_objects_to_update(the_space_id pn_project_space.project_id%type, the_group_id pn_default_object_permission.group_id%type, the_object_type pn_object_type.object_type%type) IS
        SELECT ps.object_id, pop.actions
        FROM pn_default_object_permission pop, pn_object_space ps, pn_object po
        WHERE pop.space_id = ps.space_id AND
        ps.space_id = the_space_id AND
        pop.group_id = the_group_id AND
        ps.object_id = po.object_id AND
        po.object_type = pop.object_type AND
        po.object_type = the_object_type AND
        po.record_status = 'A';

    v_object_id pn_object.object_id%TYPE;
    v_actions pn_default_object_permission.actions%TYPE;
    v_result NUMBER;
BEGIN

    begin
        -- Try to insert default object permission
        -- This might fail if there is already a value there
        INSERT INTO PN_DEFAULT_OBJECT_PERMISSION (space_id, object_type, group_id, actions)
            VALUES (i_space_id, i_object_type, i_group_id, i_actions);

    exception
        WHEN e_unique_constraint THEN
        begin
            -- Already has permissions, update them instead
            UPDATE PN_DEFAULT_OBJECT_PERMISSION
                SET actions = i_actions
                WHERE space_id = i_space_id
                AND object_type = i_object_type
                AND group_id = i_group_id;
        end;

    end;

    COMMIT;

    OPEN c_old_objects_to_update(i_space_id, i_group_id, i_object_type);
      <<recursive_update_loop>>
      LOOP
        FETCH c_old_objects_to_update INTO v_object_id, v_actions;
        EXIT WHEN c_old_objects_to_update%NOTFOUND;
          store_object_permission(v_object_id, i_group_id, v_actions, v_result);
      END LOOP recursive_update_loop;
    CLOSE c_old_objects_to_update;

    o_status := success;

EXCEPTION
    WHEN e_null_constraint THEN
        o_status := null_field;

    WHEN e_no_parent_key THEN
        o_status := no_parent_key;

    WHEN OTHERS THEN
    begin
        o_status := generic_error;
        ROLLBACK;
        BASE.LOG_ERROR(stored_proc_name, sqlcode, sqlerrm);
    end;

END store_default_obj_permission;


---------------------------------------------------------------------
-- REMOVE_GROUP_PERMISSION
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Robin       11-Apr-00   Creation.
-- Adam        30-Apr-00   Added remove group from space

PROCEDURE remove_group_permission
(
    i_group_id IN NUMBER,
    o_status OUT NUMBER
)
IS
    v_group_type_id pn_group_type.group_type_id%TYPE;
    v_space_type pn_object.object_type%TYPE;
    v_space_id pn_object.object_id%TYPE;
BEGIN
    --Make sure that the group wasn't the everyone group.  If it was, change a
    --project or business to reflect that they no longer are allowing everyone
    select
      group_type_id, o.object_type, o.object_id into v_group_type_id, v_space_type, v_space_id
    from
      pn_group g,
      pn_space_has_group shg,
      pn_object o
    where
      g.group_id = i_group_id
      and g.group_id = shg.group_id
      and shg.is_owner = 1
      and shg.space_id = o.object_id(+);

    if (v_group_type_id = 600 and v_space_type = 'business') then
      update pn_business_space
      set includes_everyone = 0
      where business_space_id = v_space_id;
    elsif (v_group_type_id = 600 and v_space_type = 'project') then
      update pn_project_space
      set includes_everyone = 0
      where project_id = v_space_id;
    end if;


    -- hard delete from permission tables
    DELETE FROM pn_default_object_permission
        WHERE group_id = i_group_id;

    DELETE FROM pn_module_permission
        WHERE group_id = i_group_id;

    DELETE FROM pn_object_permission
        WHERE group_id = i_group_id;

    DELETE FROM pn_page_permission
        WHERE group_id = i_group_id;

    -- remove the group from the space(s) it belongs to
    DELETE FROM pn_space_has_group
        WHERE group_id = i_group_id;

    -- remove group membership where other groups are a member of this
    -- one or this group is a member of other groups
    delete from pn_group_has_group
        where group_id = i_group_id
        or member_group_id = i_group_id;

    -- soft delete from group table
    -- This is necessary because other parts of the system (Workflow)
    -- may break if the group is gone
    UPDATE pn_group SET record_status = 'D'
        WHERE group_id = i_group_id;

END remove_group_permission;


---------------------------------------------------------------------
-- apply_admin_permissions
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        21-Nov-00   Creation.
--
-- Get's the list of existing secured object id's and remediates the
-- security permissions for the new ADMIN group.
-- ONLY FOR USE BY CREATE_PARENT_ADMIN_GROUP
---------------------------------------------------------------------
procedure apply_admin_permissions
(
    i_new_group_id IN NUMBER,
    i_space_admin_group_id IN NUMBER
) IS

    pragma autonomous_transaction;

    -- variable definition
    v_object_id     NUMBER;
    stored_proc_name VARCHAR2(100):= 'SECURITY.APPLY_ADMIN_PERMISSIONS';

    G_ALL_ACTIONS NUMBER := 65535;

    -- cursor definition
    CURSOR c_existing_objects (admin_group_id NUMBER) IS
        select object_id from pn_object_permission
            where group_id = admin_group_id;

BEGIN

    OPEN c_existing_objects(i_space_admin_group_id);
	<<object_loop>>
	LOOP
        -- ALL OBJECT TYPES IN THE SYSTEM
		FETCH c_existing_objects INTO v_object_id;
		EXIT WHEN c_existing_objects%NOTFOUND;

		INSERT INTO pn_object_permission (
            object_id,
            group_id,
            actions)
		VALUES (
			v_object_id,
			i_new_group_id,
            G_ALL_ACTIONS); -- space administrators get all actions for all types

	END LOOP object_loop;
	CLOSE c_existing_objects;

    commit;

    EXCEPTION

    WHEN OTHERS THEN
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        ROLLBACK;
        INSERT INTO pn_sp_error_log
            VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;


END;



---------------------------------------------------------------------
-- CREATE_PARENT_ADMIN_GROUP
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Phil        21-Nov-00   Creation.


Function create_parent_admin_role
(
    i_space_id IN NUMBER,
    i_parent_space_id IN NUMBER
)
RETURN  NUMBER IS

    -- Local variables
    v_group_id      pn_group.group_id%type;
    v_module_id     pn_module.module_id%type;
    v_actions       pn_module.default_permission_actions%type;
    v_object_type   pn_object_type.object_type%type;
    v_current_admin_group   pn_group.group_id%type;

    -- All action bits set = (2^16)-1
    G_ALL_ACTIONS NUMBER := 65535;

    G_VIEW_ACTION NUMBER := 1;

    -- error logging
    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'SECURITY.CREATE_PARENT_ADMIN_GROUP';


    CURSOR  c_module IS
		SELECT m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

        -- cursor definitions
   	CURSOR  c_object_type IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;

    CURSOR  c_group_id IS
        select g.group_id from pn_space_has_group shg, pn_group g
        where shg.space_id = i_parent_space_id
        and g.group_type_id = SECURITY.GROUP_TYPE_SPACEADMIN
        and shg.group_id = g.group_id;


BEGIN
    -- get the existing Space Admin Group for remediation of existing objects
    select g.group_id into v_current_admin_group from pn_space_has_group shg, pn_group g
    where shg.space_id = i_space_id
    and g.group_type_id = SECURITY.GROUP_TYPE_SPACEADMIN
    and shg.group_id = g.group_id;

    -- Get the Space Administrator role from the parent space
    OPEN c_group_id;
    <<group_loop>>
    LOOP
        --ALL Space Administrator Roles from the parent space
        FETCH c_group_id INTO v_group_id;
        EXIT WHEN c_group_id%NOTFOUND;

        -- Add the group to the project_space
        insert into pn_space_has_group
            (space_id, group_id, is_owner)
        values
            (i_space_id, v_group_id, 0);


        -- Set all object_type permissions
        -- for administrator the actions are set to G_ALL_ACTIONS or (2^16)-1
        OPEN c_object_type;
    	<<type_loop>>
    	LOOP
            -- ALL OBJECT TYPES IN THE SYSTEM
    		FETCH c_object_type INTO v_object_type, v_actions;
    		EXIT WHEN c_object_type%NOTFOUND;

    		INSERT INTO pn_default_object_permission (
    			space_id,
    		    object_type,
                group_id,
                actions)
    		VALUES (
    			i_space_id,
    			v_object_type,
                v_group_id,
                G_ALL_ACTIONS); -- space administrators get all actions for all types

    	END LOOP type_loop;
    	CLOSE c_object_type;


        -- Default Module permissions for Space Administrator
        OPEN c_module;
    	<<module_loop>>
    	LOOP

    		FETCH c_module INTO v_module_id, v_actions;
    		EXIT WHEN c_module%NOTFOUND;

            INSERT INTO pn_module_permission (
    		    module_id,
                space_id,
                group_id,
                actions)
    		VALUES (
    			v_module_id,
                i_space_id,
                v_group_id,         -- Must follow the group creation for Space Administrator above
                G_ALL_ACTIONS);     -- The default module permission allows all Space Administrator to do everything


    	END LOOP module_loop;
    	CLOSE c_module;

        -- remediate existing objects
        apply_admin_permissions (v_group_id, v_current_admin_group);

    END LOOP group_loop;
    CLOSE c_group_id;

    -- return the ID of the newly created space administrator group.
    RETURN v_group_id;

 EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        BASE.LOG_ERROR(stored_proc_name, sqlcode, sqlerrm);
      END;


END;


---------------------------------------------------------------------
-- F_CREATE_SPACE_ADMIN_GROUP
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Roger       8-Apr-00    Creation.
-- Create a new space administrator group.
--
-- The creator is the initial administrator
-- PUT THE CREATOR OF THE PROJECT INTO THE PROJECT ADMINISTRATORS GROUP
--
-- NOTE: the 'Space Administrator' group is a special group that can not be
-- created using the STORE_GROUP procedure since it needs special permissions
-- not created by the STORE_GROUP procedure.


Function f_create_space_admin_group
(
    i_creator_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_description IN VARCHAR
)
RETURN  NUMBER IS

    -- Local variables
    v_group_id      pn_group.group_id%type;
    v_module_id     pn_module.module_id%type;
    v_actions       pn_module.default_permission_actions%type;
    v_object_type   pn_object_type.object_type%type;

    -- All action bits set = (2^16)-1
    G_ALL_ACTIONS NUMBER := 65535;

    -- error logging
    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'CREATE_SPACE_ADMIN_GROUP';


    CURSOR c_methodology IS SELECT methodology_id FROM
           pn_methodology_view where
           parent_space_id = i_space_id ;

    CURSOR  c_module IS
		SELECT m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

        -- cursor definitions
   	CURSOR  c_object_type IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;


BEGIN

    v_group_id := BASE.CREATE_OBJECT('group', i_creator_person_id, 'A');

    insert into pn_group
        (group_id, group_name, group_desc, is_principal, record_status, is_system_group, group_type_id)
    values
        (v_group_id, '@prm.security.group.type.spaceadministrator.name', i_description, 0, 'A', 1, SECURITY.GROUP_TYPE_SPACEADMIN);

    --SECURITY.CREATE_SECURITY_PERMISSIONS(v_group_id, 'group', p_project_id, v_creator);

    -- Add the project creator (person) to the group
     insert into pn_group_has_person
        (group_id, person_id)
     values
        (v_group_id, i_creator_person_id);

    -- Add the group to the project_space
    insert into pn_space_has_group
        (space_id, group_id, is_owner)
    values
        (i_space_id, v_group_id, 1);

    -- Copies the System Group into the Object Permissions Table
    insert into pn_object_permission
        (object_id, group_id, actions)
    values
        (v_group_id, v_group_id, G_ALL_ACTIONS); -- G_ALL_ACTIONS is a 16 bit MAXINT


    -- Set all object_type permissions
    -- for administrator the actions are set to G_ALL_ACTIONS or (2^16)-1
    OPEN c_object_type;
	<<type_loop>>
	LOOP
        -- ALL OBJECT TYPES IN THE SYSTEM
		FETCH c_object_type INTO v_object_type, v_actions;
		EXIT WHEN c_object_type%NOTFOUND;

		INSERT INTO pn_default_object_permission (
			space_id,
		    object_type,
            group_id,
            actions)
		VALUES (
			i_space_id,
			v_object_type,
            v_group_id,
            G_ALL_ACTIONS); -- space administrators get all actions for all types

	END LOOP type_loop;
	CLOSE c_object_type;


    -- Default Module permissions for Space Administrator
    OPEN c_module;
	<<module_loop>>
	LOOP

		FETCH c_module INTO v_module_id, v_actions;
		EXIT WHEN c_module%NOTFOUND;

        INSERT INTO pn_module_permission (
		    module_id,
            space_id,
            group_id,
            actions)
		VALUES (
			v_module_id,
            i_space_id,
            v_group_id,         -- Must follow the group creation for Space Administrator above
            G_ALL_ACTIONS);     -- The default module permission allows all Space Administrator to do everything


	END LOOP module_loop;
	CLOSE c_module;

    -- return the ID of the newly created space administrator group.
    RETURN v_group_id;

 EXCEPTION

    WHEN OTHERS THEN
      base.log_error(stored_proc_name, sqlcode, sqlerrm);
      raise;

END;


Function f_create_power_user_group
(
    i_creator_person_id IN NUMBER,
    i_space_id IN NUMBER,
    i_description IN VARCHAR
)
RETURN  NUMBER IS

    -- Local variables
    v_group_id      pn_group.group_id%type;
    v_module_id     pn_module.module_id%type;
    v_actions       pn_module.default_permission_actions%type;
    v_object_type   pn_object_type.object_type%type;

    -- By default, power user will receive view, modify, create, and share = 128|4|2|1
    G_PERMISSIBLE_ACTIONS NUMBER := 135;

    -- error logging
    stored_proc_name VARCHAR2(100):= 'CREATE_POWER_USER_GROUP';

    CURSOR  c_module IS
		SELECT m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

        -- cursor definitions
   	CURSOR  c_object_type IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;


BEGIN

    v_group_id := BASE.CREATE_OBJECT('group', i_creator_person_id, 'A');

    insert into pn_group
        (group_id, group_name, group_desc, is_principal, record_status, is_system_group, group_type_id)
    values
        (v_group_id, '@prm.security.group.type.poweruser.name', i_description, 0, 'A', 1, SECURITY.GROUP_TYPE_POWERUSER);

    SECURITY.CREATE_SECURITY_PERMISSIONS(v_group_id, 'group', i_space_id, i_creator_person_id);

    -- Add the group to the project_space
    insert into pn_space_has_group
        (space_id, group_id, is_owner)
    values
        (i_space_id, v_group_id, 1);


    OPEN c_object_type;
	<<type_loop>>
	LOOP
        -- ALL OBJECT TYPES IN THE SYSTEM
		FETCH c_object_type INTO v_object_type, v_actions;
		EXIT WHEN c_object_type%NOTFOUND;

		INSERT INTO pn_default_object_permission (
			space_id,
		    object_type,
            group_id,
            actions)
		VALUES (
			i_space_id,
			v_object_type,
            v_group_id,
            G_PERMISSIBLE_ACTIONS);

	END LOOP type_loop;
	CLOSE c_object_type;


    -- Default Module permissions for Power User Group
    OPEN c_module;
	<<module_loop>>
	LOOP

		FETCH c_module INTO v_module_id, v_actions;
		EXIT WHEN c_module%NOTFOUND;

        INSERT INTO pn_module_permission (
		    module_id,
            space_id,
            group_id,
            actions)
		VALUES (
			v_module_id,
            i_space_id,
            v_group_id,
            G_PERMISSIBLE_ACTIONS);


	END LOOP module_loop;
	CLOSE c_module;

    -- return the ID of the newly created power user group.
    RETURN v_group_id;

 EXCEPTION

    WHEN OTHERS THEN
        base.log_error(stored_proc_name, sqlcode, sqlerrm);
        raise;

END;

---------------------------------------------------------------------
-- STORE_GROUP
---------------------------------------------------------------------
--
-- MODIFICATION HISTORY
-- Person      Date        Comments
-- ---------   ---------   ------------------------------------------
-- Roger       7-Apr-00    Created from JDBC code in net.project.security.Group.java
-- Adam        26-Apr-00   group is now added to space within this procedure
--                         all external references to add group to space have been
--                         removed

Procedure STORE_GROUP

(   i_group_name IN VARCHAR2,
    i_group_desc IN VARCHAR2,
    i_is_principal IN NUMBER,
    i_is_system_group IN NUMBER,
    i_group_type_id in number,
    i_creator_person_id IN NUMBER,
    i_space_id IN NUMBER,
    io_group_id IN OUT NUMBER
)
IS
    v_module_id     pn_module.module_id%type;
    v_actions       pn_module.default_permission_actions%type;
    v_object_type   pn_object_type.object_type%type;
    v_principal_owner_id pn_person.person_id%type;
    v_space_type    pn_object_type.object_type%type;

    CURSOR  c_module IS
		SELECT m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

        -- cursor definitions
   	CURSOR  c_object_type IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;
BEGIN
    -- null group_id means we are storing the group for the first time.
    IF (io_group_id IS NULL) THEN

        -- Principal groups have the owner's id stamped on them
        IF (i_is_principal = '1') THEN
            v_principal_owner_id := i_creator_person_id;
        end if;

        -- Create new ID for the group and set default security permissions
        io_group_id := BASE.CREATE_OBJECT('group', i_creator_person_id, 'A');

        -- insert into the group table
        insert into pn_group
            (group_id, group_name, group_desc, is_principal, is_system_group, record_status, group_type_id, principal_owner_id)
        values
             (io_group_id, i_group_name, i_group_desc, i_is_principal, i_is_system_group, 'A', i_group_type_id, v_principal_owner_id);

        -- insert the group into the space
        insert into pn_space_has_group (space_id, group_id, is_owner) values (i_space_id, io_group_id, 1);

        -- Principal Groups don't get default permissions.
        IF (i_is_principal <> '1') THEN

            -- Apply module_permissions to the new group.
            -- Groups are special objects that need module permissions for security checks.
            OPEN c_module;
    	    <<module_loop>>
    	    LOOP

    		  FETCH c_module INTO v_module_id, v_actions;
    		  EXIT WHEN c_module%NOTFOUND;

                    INSERT INTO pn_module_permission (
    		            module_id,
                        space_id,
                        group_id,
                        actions)
    		        VALUES (
    			        v_module_id,
                        i_space_id,
                        io_group_id,
                        v_actions);

    	      END LOOP module_loop;
    	      CLOSE c_module;

            -- Add pn_default_object_permissions for group
            -- Make entries in default_object_permissions table for each module for the new group
            OPEN c_object_type;
        	<<type_loop>>
        	LOOP

        		FETCH c_object_type INTO v_object_type, v_actions;
        		EXIT WHEN c_object_type%NOTFOUND;

        		INSERT INTO pn_default_object_permission (
        			space_id,
        		    object_type,
                    group_id,
                    actions)
        		VALUES (
        			i_space_id,
        			v_object_type,
                    io_group_id,
                    v_actions);

        	END LOOP type_loop;
        	CLOSE c_object_type;

            -- Apply default object permissions to the new group
            SECURITY.CREATE_SECURITY_PERMISSIONS(io_group_id, 'group', i_space_id, i_creator_person_id);

        END IF; -- if not principal group


    -- Caller passed a group_id, do an update of the group's properties.
    ELSE

        update pn_group set
            group_name = i_group_name,
            group_desc = i_group_desc,
            is_principal = i_is_principal,
            is_system_group = i_is_system_group
        where
            group_id = io_group_id;

    END IF;

    --We're going to do some denormalization here so that project spaces or business
    --spaces don't have to look into the groups to see if the everyone group has been
    --invited.  This way, they have an easy was to generate a portfolio.
    if (i_group_type_id = 600) then
        --First, determine what type of space we are using
        select object_type into v_space_type from pn_object where object_id = i_space_id;

        if (v_space_type = 'project') then
            update pn_project_space
            set includes_everyone = 1
            where project_id = i_space_id;
        elsif (v_space_type = 'business') then
            update pn_business_space
            set includes_everyone = 1
            where business_space_id = i_space_id;
        end if;
    end if;

    -- Handle Exceptions
    EXCEPTION
        when others then
        begin
           rollback;
           if c_module%isopen then
                close c_module;
           end if;
           if c_object_type%isopen then
                close c_object_type;
           end if;

            base.log_error(SQLCODE, SQLERRM, 'SECURITY.STORE_GROUP');
            raise;
        end;

END; -- Procedure STORE_GROUP


/**
 * Creates a Team Member group
 * Does not COMMIT or ROLLBACK
 * i_creator_id the person creating the group
 * i_space_id the space owning the group
 * o_group_id the generated group_id
 */
procedure create_teammember_group (
    i_creator_id in number,
    i_space_id in number,
    o_group_id out number
)
is

    v_group_id pn_group.group_id%type;

begin

    SECURITY.STORE_GROUP('@prm.security.group.type.teammember.name', '@prm.security.group.type.teammember.description', 0, 1, SECURITY.GROUP_TYPE_TEAMMEMBER, i_creator_id, i_space_id, v_group_id);
    o_group_id := v_group_id;

end create_teammember_group;

/**
 * Creates a principal group
 * Does not COMMIT or ROLLBACK
 * i_creator_id the person creating the group
 * i_space_id the space owning the group
 * o_group_id the generated group_id
 */
procedure create_principal_group (
    i_creator_id in number,
    i_space_id in number,
    o_group_id out number
)
is

    v_group_id pn_group.group_id%type;

begin

    SECURITY.STORE_GROUP('@prm.security.group.type.principal.name', '@prm.security.group.type.principal.decription', 1, 1, SECURITY.GROUP_TYPE_PRINCIPAL, i_creator_id, i_space_id, v_group_id);
    o_group_id := v_group_id;

end create_principal_group;


/**
 * Inherits a group into a space.
 * Does NOT COMMIT OR ROLLBACK
 * i_space_id the space into which the group is to be inherited
 * i_group_id the group to inherit
 * i_permission the type of permission to give
 */
procedure inherit_group (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
)
is
    v_already_inherited number := 0;

    -- Cursor to select all modules and their default permissions
    -- in the specified space where the module is active
    CURSOR module_cur (i_space_id number) IS
		SELECT shm.space_id, m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

    -- Cursor to select all object types and default permission actions
   	CURSOR object_type_cur IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;

begin

    begin
        select 1 into v_already_inherited
        from pn_space_has_group
        where
            space_id = i_space_id
        and group_id = i_group_id
        and is_owner = 0;
    exception
        when NO_DATA_FOUND then
            -- Suck up error.  v_already_inherited remains 0
            null;
    end;

    if (v_already_inherited = 0) then

        -- Add the group to PN_SPACE_HAS_GROUP
        -- Inherited groups are not owned by the space
        insert into pn_space_has_group
            (space_id, group_id, is_owner)
        values
            (i_space_id, i_group_id, 0);


        -- Now update the security settings for the newly inherited group
        if (i_permission = SECURITY.PERMISSION_SELECTION_NONE) then
            -- No permissions to be applied
            null;

        elsif (i_permission = SECURITY.PERMISSION_SELECTION_VIEW) then

            -- View permission only

            -- First create the module permissions
            for module_rec in module_cur(i_space_id) loop
                INSERT INTO pn_module_permission
                    (module_id, space_id, group_id, actions)
        		VALUES
                    (module_rec.module_id, module_rec.space_id, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
            end loop;

            -- Next create the default object permissions
            for object_type_rec in object_type_cur loop
                INSERT INTO pn_default_object_permission
                    (space_id, object_type, group_id, actions)
                VALUES
                    (i_space_id, object_type_rec.object_type, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
            end loop;

        else
            -- Default permissions

            -- First create the module permissions
            for module_rec in module_cur(i_space_id) loop
                INSERT INTO pn_module_permission
                    (module_id, space_id, group_id, actions)
        		VALUES
                    (module_rec.module_id, module_rec.space_id, i_group_id, module_rec.default_permission_actions);
            end loop;

            -- Next create the default object permissions
            for object_type_rec in object_type_cur loop
                INSERT INTO pn_default_object_permission
                    (space_id, object_type, group_id, actions)
                VALUES
                    (i_space_id, object_type_rec.object_type, i_group_id, object_type_rec.default_permission_actions);
            end loop;

        end if;

        -- Note:
        -- We do NOT apply permissions to the group itself in this space
        -- so that the group is entirely UN-EDITABLE in the space

    end if;

exception
    when others then
    begin
        BASE.LOG_ERROR('SECURITY.INHERIT_GROUP', sqlcode, sqlerrm);
        raise;
    end;

end inherit_group;

/**
 * Inherits a group from other space into a space.
 * Does NOT COMMIT OR ROLLBACK
 * i_space_id the space into which the group is to be inherited
 * i_src_space_id the space from which the group is inheritted
 * i_group_id the group to inherit
 * i_permission the type of permission to give
 */
procedure inherit_group_from_space (
    i_space_id in number,
    i_src_space_id number,
    i_group_id in number,
    i_permission in varchar2
)
is
    v_already_inherited number := 0;

    -- Cursor to select all modules and their default permissions
    -- in the specified space where the module is active
    CURSOR module_cur (i_space_id number) IS
		SELECT shm.space_id, m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;

    -- Cursor to select all modules and their permissions from associated space
    CURSOR inr_module_cur (i_space_id number, i_src_space_id number, i_group_id number) IS
		SELECT pmp.space_id, m.module_id,  pmp.actions
		FROM pn_module m,  pn_module_permission pmp, pn_space_has_module shm where
            pmp.space_id = i_src_space_id
            and m.module_id = pmp.module_id
			and pmp.group_id = i_group_id
			and shm.space_id = pmp.space_id
            and m.module_id = shm.module_id
			and m.module_id in (select pn_space_has_module.module_id from pn_space_has_module where space_id = i_space_id  )
            and shm.is_active = 1;


    -- Cursor to select all object types and default permission actions
   	CURSOR object_type_cur IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;

begin

    begin
        select 1 into v_already_inherited
        from pn_space_has_group
        where
            space_id = i_space_id
        and group_id = i_group_id
        and is_owner = 0;
    exception
        when NO_DATA_FOUND then
            -- Suck up error.  v_already_inherited remains 0
            null;
    end;

    if (v_already_inherited = 0) then

        -- Add the group to PN_SPACE_HAS_GROUP
        -- Inherited groups are not owned by the space
        insert into pn_space_has_group
            (space_id, group_id, is_owner)
        values
            (i_space_id, i_group_id, 0);


        -- Now update the security settings for the newly inherited group
        if (i_permission = SECURITY.PERMISSION_SELECTION_NONE) then
            -- No permissions to be applied
            null;

        elsif (i_permission = SECURITY.PERMISSION_SELECTION_VIEW) then

            -- View permission only

            -- First create the module permissions
            for module_rec in module_cur(i_space_id) loop
                INSERT INTO pn_module_permission
                    (module_id, space_id, group_id, actions)
        		VALUES
                    (module_rec.module_id, module_rec.space_id, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
            end loop;

            -- Next create the default object permissions
            for object_type_rec in object_type_cur loop
                INSERT INTO pn_default_object_permission
                    (space_id, object_type, group_id, actions)
                VALUES
                    (i_space_id, object_type_rec.object_type, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
            end loop;

        elsif (i_permission = SECURITY.PERMISSION_SELECTION_INHERIT) then
            -- inherited permissions

            -- First create the module permissions
            for module_rec in inr_module_cur(i_space_id, i_src_space_id, i_group_id) loop
                INSERT INTO pn_module_permission
                    (module_id, space_id, group_id, actions)
        		VALUES
                    (module_rec.module_id, i_space_id, i_group_id, module_rec.actions);
			end loop;

            -- Next create the default object permissions
            for object_type_rec in object_type_cur loop
                INSERT INTO pn_default_object_permission
                    (space_id, object_type, group_id, actions)
                VALUES
                    (i_space_id, object_type_rec.object_type, i_group_id, object_type_rec.default_permission_actions);
            end loop;
		else
		 	-- Default permissions

            -- First create the module permissions
            for module_rec in module_cur(i_space_id) loop
                INSERT INTO pn_module_permission
                    (module_id, space_id, group_id, actions)
        		VALUES
                    (module_rec.module_id, module_rec.space_id, i_group_id, module_rec.default_permission_actions);
            end loop;

            -- Next create the default object permissions
            for object_type_rec in object_type_cur loop
                INSERT INTO pn_default_object_permission
                    (space_id, object_type, group_id, actions)
                VALUES
                    (i_space_id, object_type_rec.object_type, i_group_id, object_type_rec.default_permission_actions);
            end loop;
        end if;

    end if;

exception
    when others then
    begin
        BASE.LOG_ERROR('SECURITY.INHERIT_GROUP_FROM_SPACE', sqlcode, sqlerrm);
        raise;
    end;

end inherit_group_from_space;

procedure grant_module_permissions (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
)
is
    -- Cursor to select all modules and their default permissions
    -- in the specified space where the module is active
    CURSOR module_cur (i_space_id number) IS
		SELECT shm.space_id, m.module_id, m.default_permission_actions
		FROM pn_module m, pn_space_has_module shm where
            shm.space_id = i_space_id
            and m.module_id = shm.module_id
            and shm.is_active = 1 ;
begin
    if (i_permission = SECURITY.PERMISSION_SELECTION_NONE) then
        -- No permissions are to be available.  Remove existing ones.
        delete from pn_module_permission
        where space_id = i_space_id
          and group_id = i_group_id;
    elsif (i_permission = SECURITY.PERMISSION_SELECTION_VIEW) then
        --Delete any existing permission for this group -- we are going to replace them
        delete from pn_module_permission
        where space_id = i_space_id
          and group_id = i_group_id;

        for module_rec in module_cur(i_space_id) loop
            INSERT INTO pn_module_permission
                (module_id, space_id, group_id, actions)
    		VALUES
                (module_rec.module_id, module_rec.space_id, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
        end loop;
    else
        --Delete any existing permission for this group -- we are going to replace them
        delete from pn_module_permission
        where space_id = i_space_id
          and group_id = i_group_id;

        for module_rec in module_cur(i_space_id) loop
            INSERT INTO pn_module_permission
                (module_id, space_id, group_id, actions)
    		VALUES
                (module_rec.module_id, module_rec.space_id, i_group_id, module_rec.default_permission_actions);
        end loop;
    end if;
end grant_module_permissions;

procedure set_newobject_permissions (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
)
is
    -- Cursor to select all object types and default permission actions
   	CURSOR object_type_cur IS
		SELECT object_type, default_permission_actions
		FROM pn_object_type;

begin
    if (i_permission = SECURITY.PERMISSION_SELECTION_NONE) then
        -- No permissions granted on new objects for this group
        delete from pn_default_object_permission where group_id = i_group_id;

    elsif (i_permission = SECURITY.PERMISSION_SELECTION_VIEW) then
        -- View permission only

        -- First, remove any existing permissions for this group
        delete from pn_default_object_permission where group_id = i_group_id;

        -- Create the default object permissions
        for object_type_rec in object_type_cur loop
            INSERT INTO pn_default_object_permission
                (space_id, object_type, group_id, actions)
            VALUES
                (i_space_id, object_type_rec.object_type, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
        end loop;

    else
        -- Default permissions

        -- First, remove any existing permissions for this group
        delete from pn_default_object_permission where group_id = i_group_id;

        -- Create the default object permissions
        for object_type_rec in object_type_cur loop
            INSERT INTO pn_default_object_permission
                (space_id, object_type, group_id, actions)
            VALUES
                (i_space_id, object_type_rec.object_type, i_group_id, object_type_rec.default_permission_actions);
        end loop;

    end if;
end set_newobject_permissions;

procedure retrofit_security_permissions (
    i_space_id in number,
    i_group_id in number,
    i_permission in varchar2
)
is
    --Cursor to select all of the existing objects in the space which are secured
    CURSOR existing_objects(i_space_id number) is
        select obj.object_id, obj.object_type, ot.default_permission_actions
        from pn_object_space os, pn_object obj, pn_object_type ot
        where os.object_id = obj.object_id
          and obj.object_type = ot.object_type
          and ot.is_securable > 0
          and space_id = i_space_id;
begin
    if (i_permission = SECURITY.PERMISSION_SELECTION_NONE) then
        --Nothing to do, the caller didn't really want permissions
        null;
    elsif (i_permission = SECURITY.PERMISSION_SELECTION_VIEW) then
        for existing_obj in existing_objects(i_space_id) loop
            if (existing_obj.object_id <> i_group_id) then
                INSERT INTO pn_object_permission
                (object_id, group_id, actions)
                values
                (existing_obj.object_id, i_group_id, SECURITY.VIEW_PERMISSION_BITMASK);
            end if;
        end loop;
    else
        --Default permissions
        for existing_obj in existing_objects(i_space_id) loop
            if (existing_obj.object_id <> i_group_id) then
                INSERT INTO pn_object_permission
                (object_id, group_id, actions)
                values
                (existing_obj.object_id, i_group_id, existing_obj.default_permission_actions);
            end if;
        end loop;
    end if;
end retrofit_security_permissions;

/**
 * Removes an inherited group.
 * i_space_id the space from which to remove the group
 * i_group_id the group that is inherited in that space
 */
procedure remove_inherited_group (
    i_space_id in number,
    i_group_id in number
)
is

begin

    -- Remove group from space
    -- Check for "is_owner=0" a safety feature
    -- to ensure this is not be called for an owned group
    delete from pn_space_has_group
    where
        space_id = i_space_id
    and group_id = i_group_id
    and is_owner = 0;

    -- Now remove permissions
    DELETE FROM pn_default_object_permission
        WHERE
            space_id = i_space_id
        and group_id = i_group_id;

    DELETE FROM pn_module_permission
        WHERE
            space_id = i_space_id
        and group_id = i_group_id;

    -- Page permissions?
    -- 11/12/2001 - Tim - Copied from another stored procedure
    -- this appears to be entirely obsolete
    -- no harm keeping it here though
    DELETE FROM pn_page_permission
        WHERE
            space_id = i_space_id
        and group_id = i_group_id;

    -- Note: we cannot remove object permissions
    -- since those permissions are not stored by space
    -- Therefore, if a de-inherited group is re-inherited
    -- it will retain all existing permissions
    -- Is this good or bad?


    -- Now remove this group as a member from other groups
    -- in this space
    delete from pn_group_has_group ghg
        where exists (
            select 1 from
                pn_space_has_group shg
            where
                shg.space_id = i_space_id
            and shg.group_id = ghg.group_id)
        and ghg.member_group_id = i_group_id;


end;


-------------------------------------------------------------------------------
-- COPY_GROUPS
--
-- copy all the groups (roles) from the source
-- space to the destination space.
-- This excludes System groups (Space Admin, Team Member, Principle); that is
-- only copies User-defined groups.
-- This is because Space Admin and Team Member are already created in the space,
-- Principle groups are created when a user is invited to the space
-- Only copies Owned groups currently (non-owned groups shouldn't be copied
-- and it may be to dangerous to propogated inherited groups)
-------------------------------------------------------------------------------
PROCEDURE COPY_GROUPS
(
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    i_created_by_id     in varchar2,
    o_return_value      OUT number
)
    is
        pragma autonomous_transaction;

        -- Cursor to read all groups for a given space
        cursor group_cur (i_space_id in number)
        is
            select g.group_id , group_name, group_desc, group_type_id
            from pn_space_has_group shg, pn_group g
            where shg.space_id = i_space_id
            and g.group_id = shg.group_id
            and (g.is_system_group = 0 or g.group_type_id = 600)
            and shg.is_owner = 1;

        group_rec           group_cur%rowtype;
        v_group_id          pn_group.group_id%type := null;
        v_from_space_id     pn_space_has_class.space_id%type := to_number(i_from_space_id);
        v_to_space_id       pn_space_has_class.space_id%type := to_number(i_to_space_id);
        v_created_by_id     number := to_number(i_created_by_id);

    begin

        for group_rec in group_cur(i_from_space_id)
        loop
            v_group_id := null;
            STORE_GROUP(
                group_rec.group_name,
                group_rec.group_desc,
                0,
                0,
                group_rec.group_type_id,
                v_created_by_id,
                v_to_space_id,
                v_group_id);

   
               -- delete default module permissions
               delete from
				pn_module_permission
				where
				space_id=v_to_space_id and
				group_id=v_group_id;
				
			-- now copy all properties from original module permissions 
			for mod_rec in (select module_id, actions from pn_module_permission
							where space_id=i_from_space_id and group_id=group_rec.group_id ) loop
				insert into pn_module_permission(space_id, group_id, module_id, actions) values
				( v_to_space_id, v_group_id, mod_rec.module_id, mod_rec.actions);
			end loop;

        end loop;

        commit;
        if group_cur%isopen then
            close group_cur;
        end if;

        o_return_value := base.operation_successful;

    exception
        when others then
        begin
           --dbms_output.put_line('copy_all Error '||TO_CHAR(SQLCODE)||': '||SQLERRM);
           if group_cur%isopen then
                close group_cur;
           end if;
           rollback;
        end;

    end COPY_GROUPS;


-------------------------------------------------------------------------------
-- COPY_MODULE_PERMISSIONS
--
-- copy all the module permissions for non-principal groups from the source
-- space t the destination space.
-------------------------------------------------------------------------------
Procedure COPY_MODULE_PERMISSIONS
(
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    i_created_by_id     in varchar2,
    o_return_value      OUT number
)
IS

    v_from_space_id     pn_space_has_class.space_id%type := to_number(i_from_space_id);
    v_to_space_id       pn_space_has_class.space_id%type := to_number(i_to_space_id);
    v_created_by_id     number := to_number(i_created_by_id);
    v_to_group_id       pn_group.group_id%type;
    v_business_admin_id pn_group.group_id%type;
    busines_id number :=0;
    group_type_id number :=0;

    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.COPY_MODULE_PERMISSIONS';

   group_counter number := 0;
    -- All module permissions for the space for non-principal groups
    CURSOR  c_module_permissions (i_space_id number) IS
        select g.group_type_id , mp.module_id, mp.actions
        from pn_module_permission mp, pn_group g
        where mp.space_id = i_space_id
        and g.group_id = mp.group_id
        and g.group_type_id <> SECURITY.GROUP_TYPE_USERDEFINED
        and g.group_type_id <> SECURITY.GROUP_TYPE_EVERYONE
        and g.is_principal <> 1;

    module_rec  c_module_permissions%rowtype;

BEGIN

    -- Locate the group id of the owning space's
    -- Space Administrator group;  this will be the
    -- space id of the business space admin group
    -- We need this in order to avoid trying to change
    -- permissions for it later
     BEGIN
        select g.group_id into v_business_admin_id
        from
            pn_space_has_space shs,
            pn_group g,
            pn_space_has_group shg
        where
            shs.child_space_id = i_to_space_id
        and shs.relationship_parent_to_child = 'owns'
        and shg.space_id  = shs.parent_space_id
        and g.group_type_id = SECURITY.GROUP_TYPE_SPACEADMIN
        and g.group_id = shg.group_id
        and shg.is_owner = 1;
    EXCEPTION
      WHEN no_data_found THEN
      BEGIN
        v_business_admin_id := null;
      END;
    END;

   begin
    for module_rec in c_module_permissions(i_from_space_id)
    loop
        -- get the group_id for the destination space's group that matches the source group's name.
        -- Note that if there is a business administrator group in this
        -- space, then we ignore it as a group
        if (v_business_admin_id is not null) then
            
            busines_id := v_business_admin_id;
            group_type_id := module_rec.group_type_id;
            
            select count(*) into group_counter
            from pn_space_has_group shg, pn_group g
            where shg.space_id = i_to_space_id
            and g.group_id = shg.group_id
            and g.group_type_id = module_rec.group_type_id
            and g.group_id <> v_business_admin_id;
            
            if(group_counter > 1) then
               select
                  shg.group_id into v_to_group_id
               from
                  pn_space_has_group shg
               where
                  shg.space_id in (select
                                       parent_space_id
                                    from
                                       pn_space_has_space
                                    where
                                       child_space_id=i_to_space_id and
                                       relationship_parent_to_child='owns') and
                  shg.group_id in (select 
                                       g.group_id
                                    from 
                                       pn_space_has_group shg, pn_group g
                                    where shg.space_id = i_to_space_id
                                    and g.group_id = shg.group_id
                                    and g.group_type_id = module_rec.group_type_id 
                                    and g.group_id <> v_business_admin_id );
            
            else
               select g.group_id into v_to_group_id
               from pn_space_has_group shg, pn_group g
               where shg.space_id = i_to_space_id
               and g.group_id = shg.group_id
               and g.group_type_id = module_rec.group_type_id
               and g.group_id <> v_business_admin_id;
            end if;
            
        else
            select g.group_id into v_to_group_id
            from pn_space_has_group shg, pn_group g
            where shg.space_id = i_to_space_id
            and g.group_id = shg.group_id
            and g.group_type_id = module_rec.group_type_id;
        end if;

        -- update the module permissions for the destination space's group.
        update pn_module_permission
        set actions = module_rec.actions
        where space_id = v_to_space_id
        and group_id = v_to_group_id
        and module_id = module_rec.module_id;
    end loop;

    commit;
    if c_module_permissions%isopen then
        close c_module_permissions;
    end if;
    o_return_value := base.operation_successful;
    EXCEPTION
      WHEN others THEN
      BEGIN
        BASE.LOG_ERROR(stored_proc_name, SQLCODE, SQLERRM||' v_business_admin_id:'||busines_id||' group_type_id:'||group_type_id);
      END;
    END;
    
    
END COPY_MODULE_PERMISSIONS;



-------------------------------------------------------------------------------
-- COPY_DEFAULT_PERMISSIONS
--
-- copy all the default object permissions for non-principal groups from the source
-- space to the destination space.
-------------------------------------------------------------------------------
Procedure COPY_DEFAULT_PERMISSIONS
(
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    i_created_by_id     in varchar2,
    o_return_value      OUT number
)
IS

    v_from_space_id     pn_space_has_class.space_id%type := to_number(i_from_space_id);
    v_to_space_id       pn_space_has_class.space_id%type := to_number(i_to_space_id);
    v_created_by_id     number := to_number(i_created_by_id);
    v_to_group_id       pn_group.group_id%type;
    v_business_admin_id pn_group.group_id%type;

    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.COPY_DEFAULT_PERMISSIONS';

    -- All default object permissions for the space for non-principal groups
    CURSOR  c_default_permissions (i_space_id number) IS
        select g.group_type_id , op.object_type, op.actions
        from pn_default_object_permission op, pn_group g
        where op.space_id = i_space_id
        and g.group_id = op.group_id
        and g.group_type_id <> SECURITY.GROUP_TYPE_USERDEFINED
        and g.group_type_id <> SECURITY.GROUP_TYPE_EVERYONE
        and g.is_principal <> 1;

    permission_rec  c_default_permissions%rowtype;
    group_counter number := 1;
BEGIN

    -- Locate the group id of the owning space's
    -- Space Administrator group;  this will be the
    -- space id of the business space admin group
    -- We need this in order to avoid trying to change
    -- permissions for it later
     BEGIN
        select g.group_id into v_business_admin_id
        from
            pn_space_has_space shs,
            pn_group g,
            pn_space_has_group shg
        where
            shs.child_space_id = i_to_space_id
        and shs.relationship_parent_to_child = 'owns'
        and shg.space_id  = shs.parent_space_id
        and g.group_type_id = SECURITY.GROUP_TYPE_SPACEADMIN
        and g.group_id = shg.group_id
        and shg.is_owner = 1;
    EXCEPTION
      WHEN no_data_found THEN
      BEGIN
        v_business_admin_id := null;
      END;
    END;

    for permission_rec in c_default_permissions(i_from_space_id)
    loop
        -- get the group_id for the destination space's group that matches the source group's name.
        -- Note that if there is a business administrator group in this
        -- space, then we ignore it as a group
        if (v_business_admin_id is not null) then
            
            select count(*) into group_counter
            from pn_space_has_group shg, pn_group g
            where shg.space_id = i_to_space_id
            and g.group_id = shg.group_id
            and g.group_type_id = permission_rec.group_type_id
            and g.group_id <> v_business_admin_id;
            
            if(group_counter > 1) then
               select
                  shg.group_id into v_to_group_id
               from
                  pn_space_has_group shg
               where
                  shg.space_id in (select
                                       parent_space_id
                                    from
                                       pn_space_has_space
                                    where
                                       child_space_id=i_to_space_id and
                                       relationship_parent_to_child='owns') and
                  shg.group_id in (select 
                                       g.group_id
                                    from 
                                       pn_space_has_group shg, pn_group g
                                    where shg.space_id = i_to_space_id
                                    and g.group_id = shg.group_id
                                    and g.group_type_id = permission_rec.group_type_id 
                                    and g.group_id <> v_business_admin_id );
            
            else
               select g.group_id into v_to_group_id
               from pn_space_has_group shg, pn_group g
               where shg.space_id = i_to_space_id
               and g.group_id = shg.group_id
               and g.group_type_id = permission_rec.group_type_id
               and g.group_id <> v_business_admin_id;
            end if;
            
            
        else
            update pn_default_object_permission
            set actions = permission_rec.actions
            where space_id = v_to_space_id
            and group_id = v_to_group_id
            and object_type = permission_rec.object_type;
        end if;

    end loop;

    commit;
    if c_default_permissions%isopen then
        close c_default_permissions;
    end if;
    o_return_value := base.operation_successful;

    -- Log exceptions
    EXCEPTION

    WHEN OTHERS THEN
      BEGIN
        --dbms_output.put_line('COPY_DEFAULT_PERMISSIONS Error '||TO_CHAR(SQLCODE)||': '||SQLERRM);
        if c_default_permissions%isopen then
            close c_default_permissions;
        end if;
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        o_return_value := SQLCODE;
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END COPY_DEFAULT_PERMISSIONS;

/**
 * This procedure copies the module permissions for the User Defined Group.
 */

Procedure COPY_MOD_PERM_USER_DEF_GROUPS
(
    i_from_group_id     in varchar2,
    i_to_group_id       in varchar2,
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    o_return_value      OUT number
)
IS
    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.COPY_MODULE_PERM_USER_DEF_GROUPS';
    v_module_id pn_module_permission.module_id%type;
    v_actions pn_module_permission.actions%type;

    CURSOR  c_module_permissions IS
        select mp.module_id , mp.actions
        from pn_module_permission mp , pn_group g
        where mp.group_id = i_from_group_id
        and mp.space_id =  i_from_space_id
        and g.group_id = mp.group_id
        and g.group_type_id = SECURITY.GROUP_TYPE_USERDEFINED;

    module_rec  c_module_permissions%rowtype;

BEGIN

       for module_rec in c_module_permissions
       loop

        -- update the module permissions for the destination space's group.

            update pn_module_permission
            set actions = module_rec.actions
            where space_id = i_to_space_id
            and group_id = i_to_group_id
            and module_id = module_rec.module_id;

       end loop;

    o_return_value := base.operation_successful;
    -- Log exceptions
    EXCEPTION

    WHEN OTHERS THEN
      BEGIN

        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        o_return_value := SQLCODE;
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;

END COPY_MOD_PERM_USER_DEF_GROUPS;

/**
 * This procedure copies the default permissions for the User Defined Group.
 */

Procedure COPY_DEF_PERM_USER_DEF_GROUPS
(
    i_from_group_id     in varchar2,
    i_to_group_id       in varchar2,
    i_from_space_id     in varchar2,
    i_to_space_id       in varchar2,
    o_return_value      OUT number
)
IS
    -- Error Logging
    stored_proc_name VARCHAR2(100):= 'SECURITY.COPY_MODULE_PERM_USER_DEF_GROUPS';
    v_module_id pn_module_permission.module_id%type;
    v_actions pn_module_permission.actions%type;

    CURSOR  c_default_permissions IS

        select g.group_type_id , op.object_type, op.actions
        from pn_default_object_permission op, pn_group g
        where op.space_id = i_from_space_id
        and g.group_id = i_from_group_id
        and g.group_id = op.group_id
        and g.group_type_id = SECURITY.GROUP_TYPE_USERDEFINED;

    permission_rec  c_default_permissions%rowtype;

BEGIN

       for permission_rec in c_default_permissions
       loop

        -- update the default object permissions for the destination space's group.

            update pn_default_object_permission
            set actions = permission_rec.actions
            where space_id = i_to_space_id
            and group_id = i_to_group_id
            and object_type = permission_rec.object_type;

       end loop;

    o_return_value := base.operation_successful;
    -- Log exceptions
    EXCEPTION

    WHEN OTHERS THEN
      BEGIN

        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);
        o_return_value := SQLCODE;
        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
      END;

END COPY_DEF_PERM_USER_DEF_GROUPS;


END; -- Package Body SECURITY
/