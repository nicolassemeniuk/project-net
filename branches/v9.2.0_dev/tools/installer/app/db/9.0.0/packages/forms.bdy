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
CREATE OR REPLACE PACKAGE BODY forms IS
--==================================================================
-- FORMS MODULE procedures and functions
--
-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Roger       03-01-00  Created it.
-- Tim         07-27-01  Updated to preserve IS_OWNER column on copy
-- Tim         09-11-01  Added and reworked copy_form procedures
--                       to facilitate copying forms from application
-- Carlos      06-07-06  Updated the Copy_Form procedure in order to
--                       be able to copy checkboxes information in
--                       form definition page.
-- Umesh       11-12-08  Modified copy_fields and copy_field to enable copying 
--                       of calculated fields and their associated formulas
-- Umesh       11-19-08  while copying form, dont change the status to pending.
--                       The active status forms are again activated in java layer. (test2:bug104)
--
--==================================================================

   procedure copy_fields(
        i_from_space_id     in number,
        i_to_space_id       in number,
        i_from_class_id     in number,
        i_to_class_id       in number,
        i_created_by_id     in number);

    procedure copy_lists (
        i_from_space_id     in number,
        i_to_space_id       in number,
        i_from_class_id     in number,
        i_to_class_id       in number,
        i_created_by_id     in number);

    procedure copy_field (
        i_from_space_id     in number,
        i_to_space_id       in number,
        i_from_class_id     in number,
        i_to_class_id       in number,
        i_from_field_id     in number,
        i_created_by_id     in number,
        o_new_field_id      out number);


---------------------------------------------------------------------
-- GET_NEW_FIELD_ID
-- get the new field_id that was created for the passed source field_id.
---------------------------------------------------------------------
Function get_new_field_id (
        i_class_id in number,
        i_source_field_id in number
)
RETURN  NUMBER IS

    v_field_id      pn_class_field.field_id%type;
    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'FORM.get_new_field_id';

BEGIN
    select field_id into v_field_id
    from pn_class_field
    where class_id = i_class_id
    and source_field_id = i_source_field_id;

    RETURN v_field_id;

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
END;




---------------------------------------------------------------------
-- GET_NEW_FILTER_VALUE
-- get the new filter domain value that corresponds to the source
-- filter's domain value.
---------------------------------------------------------------------
Function get_new_filter_value (
        i_to_class_id in number,
        i_list_id in number,
        i_field_id in number,
        i_filter_value in number
)
RETURN NUMBER IS

    o_domain_value_id pn_class_domain_values.domain_value_id%type;
    err_num NUMBER;
    err_msg VARCHAR2(120);
    stored_proc_name VARCHAR2(100):= 'FORM.get_new_filter_value';

BEGIN

    IF i_filter_value IS NULL
    THEN
        RETURN NULL;
    ELSE
        select cdv.domain_value_id into o_domain_value_id
        from pn_class_list_field clf, pn_class_field cf, pn_class_domain_values cdv
        where clf.class_id = i_to_class_id /* dest */
        and clf.list_id = i_list_id /* dest */
        and clf.field_id = i_field_id /* dest */
        and cf.class_id = clf.class_id
        and cf.field_id = clf.field_id
        and cdv.domain_id = cf.domain_id
        and cdv.source_value_id = i_filter_value; /* source */

        RETURN o_domain_value_id;
    END IF;

 EXCEPTION

    When NO_DATA_FOUND THEN
    Begin
        return NULL;
    end;

    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        err_num:=SQLCODE;
        err_msg:=SUBSTR(SQLERRM,1,120);

        INSERT INTO pn_sp_error_log
             VALUES (sysdate,stored_proc_name,err_num,err_msg);
        COMMIT;
      END;
END;


----------------------------------------------------------------------
-- COPY_FIELD
-- Copies a single field into the specified form
-- Raises exception if a problem occurs
-- NO COMMIT OR ROLLBACK performed
----------------------------------------------------------------------
procedure copy_field (
    i_from_field_id     in number,
    i_to_class_id       in number,
    i_created_by_id     in number,
    o_new_field_id      out number,
    o_return_status     out number
) is

    v_from_space_id pn_space_has_class.class_id%type;
    v_to_space_id pn_space_has_class.class_id%type;
    v_from_class_id pn_class.class_id%type;
    v_new_field_id pn_class_field.field_id%type;

begin

    select space_id, class_id into v_from_space_id, v_from_class_id
    from pn_class_field
    where field_id = i_from_field_id;

    select space_id into v_to_space_id
    from pn_space_has_class
    where class_id = i_to_class_id;

    copy_field(v_from_space_id, v_to_space_id,
               v_from_class_id, i_to_class_id,
               i_from_field_id, i_created_by_id, o_new_field_id);

    o_return_status := BASE.OPERATION_SUCCESSFUL;

exception
    when others then
    begin
        o_return_status := BASE.PLSQL_EXCEPTION;
        base.log_error('FORMS.COPY_FIELD', SQLCODE, SQLERRM);
        raise;
    end;
end copy_field;


----------------------------------------------------------------------
-- COPY_ALL
-- Copies all forms from a space to another space
-- AUTONOMOUS TRANSACTION
----------------------------------------------------------------------
    PROCEDURE copy_all
      ( i_from_space_id     in varchar2,
        i_to_space_id       in varchar2,
        i_created_by_id     in varchar2,
        o_return_value      OUT number)
    is
        pragma autonomous_transaction;

        -- Cursor to read all forms for a given space
        cursor form_cur (i_space_id in number)
        is
            select shc.class_id, shc.is_owner, c.record_status
            from pn_space_has_class shc, pn_class c
            where space_id = i_space_id
            and c.class_id= shc.class_id
            and c.record_status <> 'D';

        v_from_space_id     pn_space_has_class.space_id%type := to_number(i_from_space_id);
        v_to_space_id       pn_space_has_class.space_id%type := to_number(i_to_space_id);
        v_created_by_id     number := to_number(i_created_by_id);
        v_new_form_id       pn_class.class_id%type;

    begin
        for form_rec in form_cur(i_from_space_id)
        loop
            copy_form(v_from_space_id, v_to_space_id, form_rec.class_id, form_rec.is_owner, v_created_by_id, v_new_form_id);
        end loop;

        commit;
        o_return_value := base.operation_successful;


    exception
        when others then
        begin

           rollback;
           o_return_value := base.plsql_exception;
        end;

    end copy_all;

----------------------------------------------------------------------
-- copy_form_within_space
-- Copy specified form within its current space
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
procedure copy_form_within_space (
    i_from_space_id in number,
    i_from_form_id in number,
    i_created_by_id in number,
    o_new_form_id out number,
    o_return_status out number
) is

    v_is_from_space_owner pn_space_has_class.is_owner%type;
    v_new_form_id pn_class.class_id%type;

begin

    -- Fetch the owner information for form
    select is_owner into v_is_from_space_owner
    from pn_space_has_class
    where space_id = i_from_space_id and class_id = i_from_form_id;

    -- copy the form within the from space, preserving the owner flag
    copy_form(i_from_space_id, i_from_space_id, i_from_form_id, v_is_from_space_owner,
              i_created_by_id, v_new_form_id);

    o_new_form_id := v_new_form_id;
    o_return_status := BASE.OPERATION_SUCCESSFUL;

exception
    when others then
    begin
        o_return_status := BASE.PLSQL_EXCEPTION;
        base.log_error('FORMS.COPY_FORM_WITHIN_SPACE', SQLCODE, SQLERRM);
        raise;
    end;
end copy_form_within_space;

----------------------------------------------------------------------
-- copy_form_to_space
-- Copy specified form within its current space
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
procedure copy_form_to_space (
    i_from_space_id in number,
    i_to_space_id in number,
    i_from_form_id in number,
    i_created_by_id in number,
    o_new_form_id out number,
    o_return_status out number
) is

    v_is_from_space_owner pn_space_has_class.is_owner%type;
    v_new_form_id pn_class.class_id%type;

begin

    -- Fetch the owner information for form
    select is_owner into v_is_from_space_owner
    from pn_space_has_class
    where space_id = i_from_space_id and class_id = i_from_form_id;

    -- copy the form within the from space, preserving the owner flag
    copy_form(i_from_space_id, i_to_space_id, i_from_form_id, v_is_from_space_owner,
              i_created_by_id, v_new_form_id);

    o_new_form_id := v_new_form_id;
    o_return_status := BASE.OPERATION_SUCCESSFUL;

exception
    when others then
    begin
        o_return_status := BASE.PLSQL_EXCEPTION;
        base.log_error('FORMS.COPY_FORM_TO_SPACE', SQLCODE, SQLERRM);
        raise;
    end;
end copy_form_to_space;

----------------------------------------------------------------------
-- copy_form
-- Copy specified form to specified space_id
-- DOES NOT COMMIT OR ROLLBACK
----------------------------------------------------------------------
    procedure copy_form (
        i_from_space_id in number,
        i_to_space_id in number,
        i_class_id in number,
        i_is_from_space_owner in number,
        i_created_by_id in number,
        o_new_form_id out number)
    is

        -- Exceptions
        form_not_found exception;

        -- Cursor for reading form
        cursor form_cur(i_class_id in number) is
            select *
            from pn_class
            where class_id = i_class_id;

        -- Cursor for reading if original form is displayed in Tools Menu
        cursor form_featured_menuitem(i_from_space_id in number) is
               select *
               from pn_space_has_featured_menuitem
               where space_id = i_from_space_id;

        form_rec                form_cur%rowtype;
        form_feature_rec        form_featured_menuitem%rowtype;
        v_to_class_id           number;
        v_display_in_tools_menu boolean;

    begin

        -- Copy the master form record
        open form_cur(i_class_id);
        fetch form_cur into form_rec;
        if form_cur%found
        then

            v_to_class_id := BASE.CREATE_OBJECT(G_form_object_type, i_created_by_id, base.active_record_status);

            insert into pn_class (
                CLASS_ID,
                CLASS_NAME,
                CLASS_DESC,
                CLASS_ABBREVIATION,
                CLASS_TYPE_ID,
                OWNER_SPACE_ID,
                METHODOLOGY_ID,
                MAX_ROW,
                MAX_COLUMN,
                NEXT_DATA_SEQ,
                DATA_TABLE_SEQ,
                MASTER_TABLE_NAME,
                DATA_TABLE_KEY,
                IS_SEQUENCED,
                IS_SYSTEM_CLASS,
                CRC,
                RECORD_STATUS,
                SUPPORTS_DISCUSSION_GROUP,
                SUPPORTS_DOCUMENT_VAULT,
                SUPPORTS_ASSIGNMENT)
            values (
                v_to_class_id,
                form_rec.CLASS_NAME,
                form_rec.CLASS_DESC,
                form_rec.CLASS_ABBREVIATION,
                form_rec.CLASS_TYPE_ID,
                i_to_space_id,
                form_rec.METHODOLOGY_ID,
                form_rec.MAX_ROW,
                form_rec.MAX_COLUMN,
                1,                          -- reset data_seq
                1,                          -- reset data_table_seq
                null,                       -- data table does not exist yet.
                form_rec.DATA_TABLE_KEY,
                form_rec.IS_SEQUENCED,
                form_rec.IS_SYSTEM_CLASS,
                sysdate,
                form_rec.RECORD_STATUS,
                form_rec.SUPPORTS_DISCUSSION_GROUP,
                form_rec.SUPPORTS_DOCUMENT_VAULT,
                form_rec.SUPPORTS_ASSIGNMENT);

            -- Add the form to the destination space
            insert into pn_space_has_class
                (space_id, class_id, is_owner)
            values
                (i_to_space_id, v_to_class_id, i_is_from_space_owner);

            -- Add the "Display in Tools Menu" option if applicable
            open form_featured_menuitem(i_from_space_id);
            fetch form_featured_menuitem into form_feature_rec;
            if form_cur%found
            then
                insert into pn_space_has_featured_menuitem
                       (SPACE_ID,
                        OBJECT_ID)
                values (i_to_space_id,
                        v_to_class_id);
            end if;

            SECURITY.CREATE_SECURITY_PERMISSIONS(v_to_class_id, G_form_object_type, i_to_space_id, i_created_by_id);

        else
            raise form_not_found;
        end if;
        close form_cur;

        -- Copy the other items for this form.
        copy_fields(i_from_space_id, i_to_space_id, i_class_id, v_to_class_id, i_created_by_id);
        copy_lists(i_from_space_id, i_to_space_id, i_class_id, v_to_class_id, i_created_by_id);

        -- SUCCESS --
        o_new_form_id := v_to_class_id;

    exception
        when others then
        begin
            if form_cur%isopen then
                close form_cur;
            end if;

            base.log_error('FORMS.COPY_FORM', SQLCODE, SQLERRM);

            raise;
        end;
    end copy_form;


----------------------------------------------------------------------
-- copy_fields
-- copies all the fields from one form to another
-- DOES NOT COMMIT
----------------------------------------------------------------------
procedure copy_fields(
    i_from_space_id     in number,
    i_to_space_id       in number,
    i_from_class_id  in number,
    i_to_class_id    in number,
    i_created_by_id  in number)
is

    -- Cursor for reading fields
    -- This must read all fields (including deleted ones)
    -- to avoid breaking any relationships with form lists etc.
    cursor field_cur(i_class_id in number) is
        select field_id
        from pn_class_field
        where class_id = i_class_id;

    -- cursor for getting all calculated fields which need updates
    cursor cal_field_prop_cur(i_class_id in number) is
        select fp.value as old_id, f.field_id as new_id
        from pn_class_field_property fp, pn_class_field f
        where
        f.class_id = fp.class_id AND
        to_char( f.source_field_id ) = fp.value AND
        fp.class_id = i_class_id;
        
    -- cursor for getting all calculated field formulas which need updates
    cursor cal_field_formula_cur(i_class_id in number) is
        select ff.op_value as old_id, f.field_id as new_id
        from pn_calculation_field_formula ff, pn_class_field f
        where
        f.class_id = ff.class_id AND
        to_char( f.source_field_id ) = ff.op_value AND
        ff.class_id = i_class_id;
        
    field_rec field_cur%rowtype;
    v_new_field_id pn_class_field.field_id%type;

begin

    -- Copy the form fields
    for field_rec in field_cur(i_from_class_id)
    loop
      begin
        copy_field(i_from_space_id, i_to_space_id, i_from_class_id,
                   i_to_class_id, field_rec.field_id, i_created_by_id, v_new_field_id);

      exception -- if there is any error in copying field, log error
        when others then
          BASE.LOG_ERROR ('copy_fields', SQLCODE, 'Error copying field:' || field_rec.field_id || ' - ' || SUBSTR(SQLERRM,1,80));
      end;
      
    end loop;

    -- set active field back to pending, no data table yet.
    update pn_class_field
    set record_status='P'
    where record_status='A'
    and class_id = i_to_class_id;


    -- update calculated field referances
    for cal_field_rec in cal_field_prop_cur(i_to_class_id)
    loop
      update pn_class_field_property 
        set value = cal_field_rec.new_id
        where value = cal_field_rec.old_id
        and class_id = i_to_class_id;
    end loop;  

    -- update calculated field formula referances
    for cal_field_formula_rec in cal_field_formula_cur(i_to_class_id)
    loop
      update pn_calculation_field_formula
        set op_value = cal_field_formula_rec.new_id
        where op_value = cal_field_formula_rec.old_id
        and class_id = i_to_class_id;
    end loop;
    
end copy_fields;


----------------------------------------------------------------------
-- copy_field
-- copies a single field
-- DOES NOT COMMIT
----------------------------------------------------------------------
procedure copy_field (
    i_from_space_id     in number,
    i_to_space_id       in number,
    i_from_class_id     in number,
    i_to_class_id       in number,
    i_from_field_id     in number,
    i_created_by_id     in number,
    o_new_field_id      out number
) is
    -- Cursor for field properties
    cursor field_prop_cur(i_class_id in number, i_field_id in number) is
        select *
        from pn_class_field_property
        where
            class_id = i_class_id and
            field_id = i_field_id;

    -- Cursor for field formulas
    cursor field_formula_cur(i_class_id in number, i_field_id in number) is
        select *
        from pn_calculation_field_formula
        where
            class_id = i_class_id and
            field_id = i_field_id;
            
    -- Cursor for reading domain values
    cursor domain_value_cur(i_domain_id in number) is
        select *
        from pn_class_domain_values
        where domain_id = i_domain_id;

    v_field_id          pn_class_field.field_id%type;
    v_source_field_id   pn_class_field.field_id%type;
    v_from_domain_id    pn_class_domain.domain_id%type;
    v_domain_id         pn_class_domain.domain_id%type;
    v_domain_value_id   pn_class_domain_values.domain_value_id%type;
    field_rec           pn_class_field%rowtype;
    field_prop_rec      field_prop_cur%rowtype;
    domain_value_rec    domain_value_cur%rowtype;
    domain_rec          pn_class_domain%rowtype;
    v_new_record_status pn_class_field.record_status%type;

begin

    -- Fetch the field
    select * into field_rec
    from pn_class_field
    where class_id = i_from_class_id and field_id = i_from_field_id;

    v_source_field_id := i_from_field_id;
    v_from_domain_id := field_rec.domain_id;

    -- Determine the record status for the copied field;
    -- Active fields become pending, all others (pending, deleted)
    -- remain the same
    if (field_rec.record_status = BASE.ACTIVE_RECORD_STATUS) then
        v_new_record_status := BASE.PENDING_RECORD_STATUS;
    else
        v_new_record_status := field_rec.record_status;
    end if;

    -- create new domain_id
    if (v_from_domain_id IS NOT NULL) then
    begin
        -- Create is for the domain_id
        v_domain_id := base.create_object(
            G_domain_object_type,
            i_created_by_id,
            base.active_record_status);

        select * into domain_rec
        from pn_class_domain
        where domain_id = v_from_domain_id;

        insert into pn_class_domain (
            domain_id,
            domain_name,
            domain_type,
            domain_desc,
            record_status
        ) values (
            v_domain_id,
            domain_rec.domain_name,
            domain_rec.domain_type,
            domain_rec.domain_desc,
            domain_rec.record_status
        );

    end;
    else
        v_domain_id:= NULL;
    end if;


    --
    -- Create the new field
    --
    v_field_id := base.create_object(
        G_field_object_type,
        i_created_by_id,
        base.active_record_status);


    insert into pn_class_field (
        CLASS_ID,
        FIELD_ID,
        SOURCE_FIELD_ID,
        ELEMENT_ID,
        SPACE_ID,
        ROW_NUM,
        ROW_SPAN,
        COLUMN_NUM,
        COLUMN_SPAN,
        FIELD_GROUP,
        DATA_TABLE_NAME,
        DATA_COLUMN_SIZE,
        data_column_scale,
        DATA_COLUMN_EXISTS,
        HAS_DOMAIN,
        MAX_VALUE,
        MIN_VALUE,
        DEFAULT_VALUE,
        INSTRUCTIONS_CLOB,
        IS_MULTI_SELECT,
        DOMAIN_ID,
        FIELD_LABEL,
        USE_DEFAULT,
        COLUMN_ID,
        DATA_COLUMN_NAME,
        CRC,
        RECORD_STATUS,
        IS_VALUE_REQUIRED)
    values (
        i_to_class_id,
        v_field_id,
        v_source_field_id,              -- the field_id that this field was copied from
        field_rec.ELEMENT_ID,
        i_to_space_id,
        field_rec.ROW_NUM,
        field_rec.ROW_SPAN,
        field_rec.COLUMN_NUM,
        field_rec.COLUMN_SPAN,
        field_rec.FIELD_GROUP,
        null,                           -- data table name is set later
        field_rec.DATA_COLUMN_SIZE,
        field_rec.data_column_scale,
        0,                              -- column does not exist yet. no datatable.
        field_rec.HAS_DOMAIN,
        field_rec.MAX_VALUE,
        field_rec.MIN_VALUE,
        field_rec.DEFAULT_VALUE,
        field_rec.INSTRUCTIONS_CLOB,
        field_rec.IS_MULTI_SELECT,
        v_domain_id,
        field_rec.FIELD_LABEL,
        field_rec.USE_DEFAULT,
        field_rec.COLUMN_ID,
        null,                           -- data table created later.
        sysdate,
        v_new_record_status,
        field_rec.IS_VALUE_REQUIRED
    );

    -- Copy all the properties for this field
    for field_prop_rec in field_prop_cur(i_from_class_id, v_source_field_id)
    loop
        insert into pn_class_field_property (
            CLASS_ID,
            FIELD_ID,
            CLIENT_TYPE_ID,
            PROPERTY_TYPE,
            PROPERTY,
            VALUE)
        values (
             i_to_class_id,
             v_field_id,
             field_prop_rec.CLIENT_TYPE_ID,
             field_prop_rec.PROPERTY_TYPE,
             field_prop_rec.PROPERTY,
             field_prop_rec.VALUE);
    end loop; -- field properties

    -- Copy all formulas for this field (if available)
    for field_formula_rec in field_formula_cur(i_from_class_id, v_source_field_id)
    loop
        insert into pn_calculation_field_formula (
            CLASS_ID,
            FIELD_ID,
            ORDER_ID,
            OP_VALUE,
            OP_TYPE)
        values (
             i_to_class_id,
             v_field_id,
             field_formula_rec.ORDER_ID,
             field_formula_rec.OP_VALUE,
             field_formula_rec.OP_TYPE);
    end loop; -- field formulas

    -- Copy the domain values if the domain is not null.
    if (v_from_domain_id IS NOT NULL) then
        -- Copy the domain values for the field
        for domain_value_rec in domain_value_cur(v_from_domain_id)
        loop
            -- Create new domain_value_id
            v_domain_value_id := base.create_object(
                G_domain_value_object_type,
                i_created_by_id,
                base.active_record_status);
            insert into pn_class_domain_values (
                DOMAIN_ID,
                DOMAIN_VALUE_ID,
                DOMAIN_VALUE_NAME,
                DOMAIN_VALUE_DESC,
                IS_DEFAULT,
                DOMAIN_VALUE_SEQ,
                source_value_id,
                RECORD_STATUS)
            values (
                v_domain_id,
                v_domain_value_id,
                domain_value_rec.DOMAIN_VALUE_NAME,
                domain_value_rec.DOMAIN_VALUE_DESC,
                domain_value_rec.IS_DEFAULT,
                domain_value_rec.DOMAIN_VALUE_SEQ,
                domain_value_rec.DOMAIN_VALUE_ID,
                domain_value_rec.RECORD_STATUS);
        end loop; -- domain values
    end if;

    o_new_field_id := v_field_id;

end copy_field;

----------------------------------------------------------------------
-- copy_lists
-- copies form lists from one form to another.
-- DOES NOT COMMIT
----------------------------------------------------------------------
    procedure copy_lists (
        i_from_space_id     in number,
        i_to_space_id       in number,
        i_from_class_id     in number,
        i_to_class_id       in number,
        i_created_by_id     in number)
    is
        -- Cursor for reading form lists
        cursor list_cur(i_class_id in number) is
            select *
            from pn_class_list
            where class_id = i_class_id
            and record_status != 'D';

        -- Cursor for form list fields
        cursor list_field_cur(i_class_id in number, i_list_id in number) is
            select *
            from pn_class_list_field
            where class_id = i_class_id
            and list_id = i_list_id;

        -- Cursor for form list filters
        cursor list_filter_cur(i_class_id in number, i_list_id in number) is
            select clf.*, cf.element_id
            from pn_class_list_filter clf, pn_class_field cf
            where clf.class_id = i_class_id
            and clf.list_id = i_list_id
            and cf.field_id = clf.field_id;

        v_list_id           pn_class_list.list_id%type;
        v_from_list_id      pn_class_list.list_id%type;
        v_field_id          pn_class_field.field_id%type;
        v_filter_value_id   pn_class_list_filter.value_id%type;
        v_filter_value      pn_class_domain_values.domain_value_id%type;
        v_is_default        pn_space_has_class_list.is_default%type;
        list_rec            list_cur%rowtype;
        list_field_rec      list_field_cur%rowtype;
        list_filter_rec     list_filter_cur%rowtype;

    begin

        -- For each form list
        for list_rec in list_cur(i_from_class_id)
        loop
           v_from_list_id := list_rec.list_id;
           -- create new list_id
           v_list_id := BASE.CREATE_OBJECT(
                G_list_object_type,
                i_created_by_id,
                base.active_record_status);

            insert into pn_class_list (
                CLASS_ID,
                LIST_ID,
                LIST_NAME,
                FIELD_CNT,
                LIST_DESC,
                OWNER_SPACE_ID,
                CRC,
                RECORD_STATUS)
            values (
                i_to_class_id,
                v_list_id,
                list_rec.LIST_NAME,
                list_rec.FIELD_CNT,
                list_rec.LIST_DESC,
                i_to_space_id,
                sysdate,
                base.active_record_status);

            SECURITY.CREATE_SECURITY_PERMISSIONS(v_list_id, G_list_object_type, i_to_space_id, i_created_by_id);

            -- For each form list field
            for list_field_rec in list_field_cur(i_from_class_id, v_from_list_id)
            loop
                v_field_id := get_new_field_id(i_to_class_id, list_field_rec.field_id);
                -- create new class_list_field
                insert into pn_class_list_field (
                    CLASS_ID,
                    LIST_ID,
                    FIELD_ID,
                    FIELD_ORDER,
                    SORT_ORDER,
                    WRAP_MODE,
                    IS_SUBFIELD,
                    IS_SORT_FIELD,
                    FIELD_WIDTH,
                    SORT_ASCENDING,
                    IS_LIST_FIELD)
                values (
                    i_to_class_id,
                    v_list_id,
                    v_field_id,
                    list_field_rec.FIELD_ORDER,
                    list_field_rec.SORT_ORDER,
                    list_field_rec.WRAP_MODE,
                    list_field_rec.IS_SUBFIELD,
                    list_field_rec.IS_SORT_FIELD,
                    list_field_rec.FIELD_WIDTH,
                    list_field_rec.SORT_ASCENDING,
                    list_field_rec.IS_LIST_FIELD);
            end loop; -- class_list_field

                -- create new class_list_filter
                for list_filter_rec in list_filter_cur(i_from_class_id, v_from_list_id)
                loop
                    -- look up the new destination field_id that
                    -- corresponds the old source field_id.
                    v_field_id := get_new_field_id(
                        i_to_class_id,
                        list_filter_rec.field_id);

                    -- If the field has a domain (ie. menu field), look up the new destination
                    -- filter_value_id that corresponds the old source filter_value_id.
                    -- Otherwise, just copy the exact filter value.
                    if list_filter_rec.element_id = G_selection_menu_id
                    then
                        v_filter_value := get_new_filter_value(
                            i_to_class_id,
                            v_list_id,
                            v_field_id,
                            list_filter_rec.filter_value);
                    else
                        v_filter_value := list_filter_rec.filter_value;
                    end if;


                    if (v_filter_value IS NOT NULL)
                    then
                        v_filter_value_id := BASE.CREATE_OBJECT(
                            G_filter_value_object_type,
                            i_created_by_id,
                            base.active_record_status);

                        insert into pn_class_list_filter (
                            CLASS_ID,
                            LIST_ID,
                            FIELD_ID,
                            VALUE_ID,
                            FILTER_VALUE,
                            OPERATOR)
                        values (
                            i_to_class_id,
                            v_list_id,
                            v_field_id,
                            v_filter_value_id,
                            v_filter_value,
                            list_filter_rec.OPERATOR);
                   end if;

                end loop; -- class_list_filter

                -- Get the defualt setting for the list
                select is_default into v_is_default
                from pn_space_has_class_list
                where space_id = i_from_space_id
                and class_id = i_from_class_id
                and list_id = v_from_list_id;

                insert into pn_space_has_class_list (
                    SPACE_ID,
                    CLASS_ID,
                    LIST_ID,
                    IS_DEFAULT)
                values (
                    i_to_space_id,
                    i_to_class_id,
                    v_list_id,
                    v_is_default);

        end loop; -- class_list

        -- For DEBUG
        EXCEPTION
        WHEN OTHERS THEN
          RAISE;

    end copy_lists;


PROCEDURE log_event
    (
        object_id IN varchar2,
        whoami IN varchar2,
        action IN varchar2,
        action_name IN varchar2,
        notes IN varchar2
    )

IS
     pragma autonomous_transaction;

    v_object_id     pn_object.object_id%type := TO_NUMBER(object_id);
    v_whoami          pn_person.person_id%type := TO_NUMBER(whoami);
    v_history_id      pn_forms_history.forms_history_id%type;
    v_action          pn_forms_history.action%type := action;
    v_action_name     pn_forms_history.action_name%type := action_name;
    v_action_comment  pn_forms_history.action_comment%type := notes;

BEGIN

    SELECT pn_object_sequence.nextval INTO v_history_id FROM dual;

    -- insert a history record for this document

    insert into pn_forms_history (
        object_id,
        forms_history_id,
        action,
        action_name,
        action_by_id,
        action_date,
        action_comment)
    values (
        v_object_id,
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
         base.log_error('FORMS.LOG_EVENT', sqlcode, sqlerrm);
        raise;
    END;
END;  -- Procedure LOG_EVENT
----------------------------------------------------------------------


END; -- Package Body FORM
/

