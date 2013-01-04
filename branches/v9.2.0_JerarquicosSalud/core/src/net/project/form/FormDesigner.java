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

 package net.project.form;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.ObjectType;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.database.ObjectManager;
import net.project.events.EventType;
import net.project.gui.navbar.FeaturedItemsAssociation;
import net.project.notification.EventCodes;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.Conversion;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;


/**
 * Creates and modifies the design of a Form.
 * Provides XML formatting by the form designer user interface.
 */
public class FormDesigner extends Form {
    /**
     * The default type for a new field being added to the form.  FormField.TEXT
     * unless changed by calling setDefaultFieldElementID().
     *
     * @see FormField for static element_id types.
     */
    protected String m_defaultFieldElementID = FormField.TEXT;

    /** SpaceID's that are sharing the design of this form. */
    private ArrayList m_spacesToShareWith = new ArrayList();

    
    private boolean clearExternalClassId;       
    
    
    private boolean changeSharingStatus;
    
    /**
	 * @return the clearExternalClassId
	 */
	public boolean isClearExternalClassId() {
		return clearExternalClassId;
	}

	/**
	 * @param clearExternalClassId the clearExternalClassId to set
	 */
	public void setClearExternalClassId(boolean clearExternalClassId) {
		this.clearExternalClassId = clearExternalClassId;
	}
	
	
	public boolean isChangeSharingStatus() {
		return changeSharingStatus;
	}

	public void setChangeSharingStatus(boolean changeSharingStatus) {
		this.changeSharingStatus = changeSharingStatus;
	}

	/**
     * Set a list of spaces that the design of this form will be shared with.
     * Whatever space id's are in this list will entirely replace any space id's
     * that exist in the database.
     *
     * @param spacesToShareWith a <code>String[]</code> value containing id's of
     * spaces.
     */
    public void setSpacesToShareWithArray(String[] spacesToShareWith) {
        m_spacesToShareWith.clear();

        if (spacesToShareWith != null) {
            for (int i = 0; i < spacesToShareWith.length; i++) {
                m_spacesToShareWith.add(spacesToShareWith[i]);
            }
        }
    }

    /**
     * Set a list of spaces that the design of this form will be shared with.
     * Whatever space id's are in this list will entirely replace any space id's
     * that exist in the database.
     *
     * @param spacesToShareWith av <code>Arraylist</code> value containing id's of
     * spaces.
     */
    public void setSpacesToShareWith(ArrayList spacesToShareWith) {
        m_spacesToShareWith.clear();
        m_spacesToShareWith.addAll(spacesToShareWith);
    }

    /**
     * Get a list of space id's with which we are going to share the design of
     * this form.
     *
     * @return an <code>ArrayList</code> value containing space id's.
     */
    public ArrayList getSpacesToShareWith() {
        return this.m_spacesToShareWith;
    }

    /**
     * Load the list of spaces that this form currently shares this document with.
     *
     * @exception PersistenceException if an error occurs while trying to load the
     * list of "INFORMATION_PROVIDER" spaces from the database.
     */
    private void loadSharingPolicy() throws PersistenceException {
        DBBean db = new DBBean();

        try {
            //Select all the sharing policies except for the default policy that
            //this form has with this space.
            db.prepareStatement("select space_id from pn_space_has_class " +
                "where class_id = ? and space_id <> ?");
            db.pstmt.setString(1, getID());
            db.pstmt.setString(2, getSpace().getID());
            db.executePrepared();

            //Add the id's of each into an array of strings.
            m_spacesToShareWith.clear();
            while (db.result.next()) {
                m_spacesToShareWith.add(db.result.getString("space_id"));
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(FormDesigner.class).error("Unable to load space sharing list for this form.");
            throw new PersistenceException("" + sqle, sqle);
        } finally {
            db.release();
        }
    }

    private void loadSharingStatus() throws PersistenceException {
    	DBBean db = new DBBean();
    	try {
		    	db.prepareStatement("select visible, is_owner from pn_space_has_class " +
		      							"where class_id = ? and space_id = ?");
		    	db.pstmt.setString(1, getID());
		    	db.pstmt.setString(2, getSpace().getID());
		    	db.executePrepared();
		    	
		    	if (db.result.next()){
		    		this.setSharedFormVisible(db.result.getString("visible").equals("1"));
		    	}
    	  } catch (SQLException sqle) {
    		  Logger.getLogger(FormDesigner.class).error("Unable to load space sharing status for this form.");
              throw new PersistenceException("" + sqle, sqle);
          } finally {
              db.release();
          }
    }
    
    /**
     * Creates a new FormDesigner, with Pending status.
     */
    public FormDesigner() {
        m_record_status = "P";
    }


    public void setRecordStatus(String recordStatus) {
        m_record_status = recordStatus;

        //Fires Event when the Form is getting deleted
        if (m_record_status.equals("D")) {

            try {

                FormEvent event = new FormEvent();
                event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
                event.setTargetObjectID(m_class_id);
                event.setTargetObjectType(EventCodes.REMOVE_FORM);
                event.setTargetObjectXML(this.getXMLBody());
                event.setEventType(EventCodes.REMOVE_FORM);
                event.setUser(SessionManager.getUser());
                event.setDescription("Remove Form : \"" + getName() + "\"");
                event.store();
                
    			// publishing event to asynchronous queue
    	        try {
    	        	net.project.events.FormEvent formEvent = (net.project.events.FormEvent) EventFactory.getEvent(ObjectType.FORM, EventType.DELETED);
    	        	formEvent.setObjectID(m_class_id);
    	        	formEvent.setObjectType(ObjectType.FORM);
    	        	formEvent.setName(getName());
    	        	formEvent.setObjectRecordStatus("D");
    	        	formEvent.publish();
    			} catch (EventException e) {
    				Logger.getLogger(FormDesigner.class).error("FormDesigner.setRecordStatus():: Form Remove Event Publishing Failed! "+ e);
    			}

            } catch (PersistenceException pe) {
            	Logger.getLogger(FormDesigner.class).error("FormDesigner.setRecordStatus threw an Persistence Exception: " + pe);
            }

        }
    }


    public String getRecordStatus() {
        return m_record_status;
    }


    /** Set the key column for this database data table. */
    public void setDataTableKey(String key) {
        m_dataTableKey = key;
    }


    /**
     * Get the default FormField element_id (field type).  Defaults
     * FormField.TEXT.
     */
    public String getDefaultFieldElementID() {
        return m_defaultFieldElementID;
    }


    /** Sets the default FormField element_id (field type). */
    public void setDefaultFieldElementID(String element_id) {
        m_defaultFieldElementID = element_id;
    }


    /**
     * Sets whether this form class is sequenced (multiple data records stored
     * sequentially).
     *
     * @param isSequenced true if the form stores multiple sequenced data
     * records, false if the form stores only a single record.
     */
    public void setSequenced(boolean isSequenced) {
        m_isSequenced = isSequenced;
    }


    /**
     * Delete the specified field from the form (and the database).
     */
    public void removeField(FormFieldDesigner xField) {
        FormField field;

        if (xField == null)
            throw new NullPointerException("xField is null. can't remove field");

        for (int i = 0; i < m_fields.size(); i++) {
            field = (FormField)m_fields.get(i);
            if (field.getID().equals(xField.getID())) {
                // delete the field from the database
                xField.remove();
                // remove the field from the form
                m_fields.remove(i);
            }
        }
    }

    public void hideField(FormFieldDesigner xField) {
        if (xField == null)
            throw new NullPointerException("xField is null. can't hide field");
        FormField field;
        for (int i = 0; i < m_fields.size(); i++) {
            field = (FormField)m_fields.get(i);
            if (field.getID().equals(xField.getID())) {
                // hide field
            	xField.setHiddenForEaf(field.isHiddenForEaf());
            	xField.hideField();
            }
        }        
        
        
    }
    
    
    /**
     * Update the properties of the specified field.  Only updates if a field if
     * found on the form that who's ID matches the passed field's ID.
     */
    public void updateField(FormFieldDesigner sourceField) {
        FormField destField;

        if (sourceField == null)
            throw new NullPointerException("sourceField is null");

        for (int i = 0; i < m_fields.size(); i++) {
            destField = (FormField)m_fields.get(i);
            if (destField.getID().equals(sourceField.getID())) {
                destField.m_form = sourceField.m_form;
                destField.m_domain = sourceField.m_domain;
                destField.m_properties = sourceField.m_properties;
                destField.m_class_id = sourceField.m_class_id;
                destField.m_field_id = sourceField.m_field_id;
                destField.m_client_type_id = sourceField.m_client_type_id;
                destField.m_db_datatype = sourceField.m_db_datatype;
                destField.m_element_id = sourceField.m_element_id;
                destField.m_element_name = sourceField.m_element_name;
                destField.m_element_label = sourceField.m_element_label;
                destField.m_field_label = sourceField.m_field_label;
                destField.m_tag = sourceField.m_tag;
                destField.m_data_table_name = sourceField.m_data_table_name;
                destField.m_data_column_name = sourceField.m_data_column_name;
                destField.m_data_column_size = sourceField.m_data_column_size;
                destField.m_data_column_scale = sourceField.m_data_column_scale;
                destField.m_data_column_exists = sourceField.m_data_column_exists;
                destField.m_field_group = sourceField.m_field_group;
                destField.m_row_num = sourceField.m_row_num;
                destField.m_column_num = sourceField.m_column_num;
                destField.m_row_span = sourceField.m_row_span;
                destField.m_max_value = sourceField.m_max_value;
                destField.m_min_value = sourceField.m_min_value;
                destField.m_default_value = sourceField.m_default_value;
                destField.m_crc = sourceField.m_crc;
                //destField.m_sort_ascending = sourceField.m_sort_ascending;
            }
        }
    }


    /**
     * Delete the specified list from the form (and the database).
     */
    public void removeList(FormListDesigner xList) throws PersistenceException {
        FormList list;

        if (xList == null)
            throw new NullPointerException("xList is null. can't remove list");

        for (int i = 0; i < m_lists.size(); i++) {
            list = (FormList)m_lists.get(i);
            if (list.getID().equals(xList.getID())) {
                // delete the list from the database
                xList.remove();
                // remove the list from the form
                m_lists.remove(i);


            }
        }
    }


    /**
     * Get the next data table sequence number for this form (class).
     *
     * @return the sequence number.  -1 returned if there is an error getting from the database.
     * TODO -- convert this to JDBC Statement to make atomic.
     */
    private int dbGetNextDataTableSeq() throws FormException {
        int data_table_seq = 0;

        // we need the select and update to be atomic.
        //beginTransaction(m_app);

        // get the next data object sequence number for this form.
        try {
            db.setQuery("select data_table_seq from pn_class where class_id =" + m_class_id);
            db.executeQuery();

            if ((!db.result.next()) || ((data_table_seq = db.result.getInt(1)) == -1)) {
                //rollbackTransaction(m_app);
                throw new FormException("Cound not get new data table sequence number from the database.  Contact Administrator.");
            }
            // Increment the sequence number in the database.
            db.release();
            db.setQuery("update pn_class set data_table_seq=" + (data_table_seq + 1) + " where class_id=" + m_class_id);
            db.executeQuery();
        } catch (SQLException sqle) {
        	Logger.getLogger(FormDesigner.class).error("FormDesigner.dbGetNextDataTableSeq threw an SQL Exception: " + sqle);
        } finally {
            db.release();
        }

        return data_table_seq;
    }

    public boolean tableExists(String dataTableName) throws SQLException {
        boolean exists = false;
        DBBean db = new DBBean();
        try {
            db.executeQuery("select table_name from user_tables where table_name = " +
                DBFormat.varchar2(dataTableName.toUpperCase()));
            exists = db.result.next();
        } finally {
            db.release();
        }

        return exists;
    }


    /**
     * Create or update the data table for this form's pn_class. Indices on the
     * table will also be created.
     *
     * @param dataTableName the name the database table will be give when
     * created.
     * @param fieldList the FormFields that should be included in the new data
     * table.  The data table hold data for these and only these fields.
     * @param isBaseTable true if the table is the base table for the form,
     * false if a table for a non-base scope such as a user.
     */
    private void createDataTable(String dataTableName, List fieldList, boolean isBaseTable) throws FormException {
        int fieldCount;
        boolean haveFields = false;

        if (m_class_id == null) {
            throw new FormException("undefined pn_class.class_id when creating data table.  Data table: " + dataTableName);
        }

        if ((dataTableName == null) || dataTableName.equals("") || (dataTableName.length() > MAX_TABLE_NAME_SIZE)) {
            throw new FormException("Bad data table name in createDataTable.  class_id: " + m_class_id);
        }

        // no fields to add to data table
        //Check to see if the table has been created
        boolean tableExists;
        try {
            fieldCount = (fieldList == null ? 0 : fieldList.size());
            tableExists = tableExists(dataTableName);

            if (tableExists && (fieldList == null || fieldCount < 1)) {
                return;
            }
        } catch (SQLException sqle) {
            throw new FormException(sqle);
        }

        if (m_fields.size() < 1) {
            throw new FormException("Must add at least one field to the form before activating.  class_id: " + m_class_id);
        }

        // CREATE TABLE
        DBBean db = new DBBean();
        try {

            // Data table does not exist yet.
            // Each class has only one base table (in the creating/owning classes' scope) that contains the seq_num and CRC.
            // other data tables for this class (in other space scopes) are always joined to the base data table.
            if (!tableExists) {
                createTable(isBaseTable, dataTableName, fieldCount, fieldList, db);

            }
            // UPDATE TABLE
            // Data table already exists, see if we need to add or increase the size of columns
            else {
                updateDataTable(dataTableName, fieldCount, fieldList, haveFields, db);

            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FormDesigner.class).error("Unexpected Form Designer Error: " + sqle);
            throw new FormException("FormDesigner.createDataTable:  Could not create data table for the form. Class_id: " + m_class_id, sqle);
        } finally {
            db.release();
        }

    }

    private void updateDataTable(String dataTableName, int fieldCount, List fieldList, boolean haveFields, DBBean db) throws SQLException {
        String query;
        int i;
        IFormField field;
        // TODO --resize existing columns to larger... ignore if smaller.
        //           . lots of exception handeling here

        query = "alter table " + dataTableName.toUpperCase() + " add (";

        // for each FormField
        for (i = 0; i < fieldCount; i++) {
            field = (IFormField)fieldList.get(i);

            // Keep track of the max_row and max_column... will be saved in pn_class table by dbActivate()  later.
            if (field.rowNum() > m_max_row)
                m_max_row = field.rowNum();

            if (field.columnNum() > m_max_column)
                m_max_column = field.columnNum();

            // Ignore static fields.
            // Static fields such as "instructions" and separators will not have dbStorageType and do not need to be stored in the database.
            if (field.dbStorageType() != null) {
                haveFields = true;
                field.setDataColumnName("field" + field.getID());

                // User data fields can contain nulls
                query += field.getDataColumnName() + " " + field.dbStorageType() + " NULL";

                // if not the last database column, add comma.
                if (i < fieldCount - 1)
                    query = query + ", ";
            }
        }

        query = query + ")";

        // execute the SQL update table
        // only if we have valid fields.
        if (haveFields) {
            db.setQuery(query);
            db.executeQuery();
        }
    }

    private void createTable(boolean is_base_table, String data_table_name, int num_fields, List field_list, DBBean db) throws SQLException {
        String query_string;
        IFormField field;
        
        // get the database tablespace names for creating the new form table and indexes.
        String dataTablespaceName = SessionManager.getDataTablespace();
        String indexTablespaceName = SessionManager.getIndexTablespace();
        
        if (is_base_table) {
            query_string = "create table " + data_table_name.toUpperCase() +
                " (object_id NUMBER(20) NOT NULL, version_id NUMBER(20) NULL, previous_version_id NUMBER(20) NULL, " +
                "multi_data_seq NUMBER(3) NOT NULL, is_current NUMBER(1) NOT NULL, seq_num NUMBER(10) NOT NULL, create_person_id NUMBER(20) NOT NULL, creator_email VARCHAR(100), " +
                "modify_person_id NUMBER(20) NOT NULL, date_modified DATE NOT NULL, date_created DATE NOT NULL, crc DATE NULL";
        } else {
            query_string = "create table PNET" + data_table_name.toUpperCase() + " (object_id NUMBER(20) NOT NULL, version_id NUMBER(20) NOT NULL, multi_data_seq NUMBER(3) NOT NULL";
        }
        
        // Columns for each FormField
        for (int i = 0; i < num_fields; i++) {
            field = (IFormField)field_list.get(i);

            // Keep track of the max_row and max_column... Save in pn_class table later.
            if (field.rowNum() > m_max_row)
                m_max_row = field.rowNum();

            if (field.columnNum() > m_max_column)
                m_max_column = field.columnNum();

            // Ignore static fields.
            // Static fields such as "instructions" and separators will not have dbStorageType and do not need to be stored in the database.
            if (field.dbStorageType() != null) {
                field.setDataColumnName("field" + field.getID());

                // User data fields can contain nulls
                query_string += ",  " + field.getDataColumnName() + " " + field.dbStorageType() + " NULL";
            }
        }

        query_string += ",";

        // primary key
        query_string += " CONSTRAINT " + data_table_name.toUpperCase() +
            "PK PRIMARY KEY (object_id, version_id, multi_data_seq) " + 
            "USING INDEX PCTFREE 1 TABLESPACE " + indexTablespaceName + ") " +
            "TABLESPACE " + dataTablespaceName;

        // create the table
        db.setQuery(query_string);
        db.executeQuery();

        // Create an index on the date_modified column, which is
        // necessary for the form reports to work correctly.
        query_string = " CREATE INDEX " + data_table_name.toUpperCase() +
            "_IDX1 on " + data_table_name + "(date_modified) " +
            "TABLESPACE " + indexTablespaceName;
        db.executeQuery(query_string);
        db.release();
    }


    /**
     * Indicates whether this form may be activated for general use.
     * A form must have at least one field and at least one list defined.
     * @return true if the form may be activated; false otherwise
     */
    public boolean isActivateAllowed() {
        return hasFields() && hasLists();
    }

    /**
     * Activate this form for general use.  This should only be called if
     * {@link #isActivateAllowed} is <b><code>true</code></b>.<br>
     * Usually the form pn_class has been newly created.
     * Assumes the pnet_class and class_field tables have been populated.
     * Creates the data tables and sets the record status to 'A' (active).<br>
     * {@link #store} should be called before this method to make sure the Form
     * object is in sync with the database.
     *
     * @param spaceScope the Space scope to which the changes will be applied
     * (methodology, business, project, personal, etc.)
     * @throws FormException if there is a problem activating the form, such
     * as an empty mandatory field or no fields defined.
     */
    public void activate(Space spaceScope) throws FormException {
        // TODO -- Need to deal with data_table column adds, column resizes when modifying a form.
        // TODO -- convert to create multiple data tables.  (or assume one for now)

        FormField field;
        int i;
        int num_fields;
        String data_table_name;
        String query_string;
        boolean is_base_table = false;

        DBBean dbean = new DBBean();

        if ((m_class_id == null) || (m_owner_space_id == null) || (spaceScope.getID() == null))
            throw new FormException("One or more required fields null: m_class_id, m_owner_space_id, scope_space_id");

        if ((m_fields == null) || ((num_fields = m_fields.size()) < 1))
            throw new FormException("m_fields null or has size() of zero.");

        if (!hasLists()) {
            throw new FormException("You must define at least one list before activating the form.");
        }

        // if the changes are being applied to the class' base scope, we are working with the base data table
        // Oracle has a 30 char table name max.  m_class_id has a max=20 and data_table_seq has a max=7
        if (spaceScope.getID().equals(m_owner_space_id)) {
            is_base_table = true;
            data_table_name = "cd" + m_class_id;
        } else {
            data_table_name = "cd" + m_class_id + "_" + dbGetNextDataTableSeq();
        }

        ArrayList new_fields = new ArrayList(num_fields);

        try {
            // Set database connection to be atomic (manual commit)
            dbean.setAutoCommit(false);

            storeFormDesign(dbean);

            // UPDATE FIELDS
            for (i = 0; i < num_fields; i++) {
                field = (FormField)m_fields.get(i);

                // Only update pending fields
                if (field.m_record_status.equals("P")) {
                    // update non-static fields with data table storage.
                    if ((!field.m_data_column_exists) && (field.dbStorageType() != null)) {
                        field.m_data_column_exists = true;
                        field.m_data_table_name = data_table_name;
                        field.m_data_column_name = "field" + field.m_field_id;
                        new_fields.add(field);

                        query_string = "update pn_class_field set data_table_name=" + DBFormat.varchar2(field.m_data_table_name) + ", data_column_name=" + DBFormat.varchar2(field.m_data_column_name) +
                            ", data_column_exists=1, record_status='A'  where class_id=" + m_class_id + " and space_id=" + spaceScope.getID() + " and field_id=" + field.m_field_id;
                        dbean.setQuery(query_string);
                        dbean.executeQuery();
                    }else{// if (field.isDesignable()){
                    	field.m_data_table_name = data_table_name;
                        query_string = "update pn_class_field set data_table_name=" + DBFormat.varchar2(field.m_data_table_name) + 
                        ",  record_status='A'  where class_id=" + m_class_id + " and space_id=" + spaceScope.getID() + " and field_id=" + field.m_field_id;
                        dbean.setQuery(query_string);
                        dbean.executeQuery();                    	
                    } 
/*                    else {
                        query_string = "update pn_class_field set record_status='A'  where class_id=" + m_class_id + " and space_id=" + spaceScope.getID() + " and field_id=" + field.m_field_id;
                        dbean.setQuery(query_string);
                        dbean.executeQuery();
                    }*/
                }

            }

            // Create or update data table.
            try {
                createDataTable(data_table_name, new_fields, is_base_table);
            } catch (FormException fe) {
            	Logger.getLogger(FormDesigner.class).error("Creation of data table " + data_table_name + " failed.");
                throw new SQLException("Creation of table has failed");
            }


            // UPDATE CLASS table
            // store the data_table_name, max_row, max_column, etc.
            query_string = "update pn_class set record_status='A' ," + "max_row=" + m_max_row + ", max_column=" + m_max_column;

            if (is_base_table)
                query_string += ", master_table_name=" + DBFormat.varchar2(data_table_name) +
                    ", data_table_key='object_id', is_sequenced=" + DBFormat.bool(m_isSequenced) +
                    " where class_id=" + m_class_id;

            //db.release();
            dbean.setQuery(query_string);
            dbean.executeQuery();

            db.commit();
            db.release();

            // ADD CLASS TO SPACE
            // add this class to the specified space context.
            addToSpace(spaceScope);

        } catch (SQLException e) {
                try {
                    dbean.rollback();
                } catch (SQLException sqle) {
                    // report earlier error
                }
            throw new FormException("Changes to the form could not be published.", e);
        } catch (PersistenceException e) {
            try {
                dbean.rollback();
            } catch (SQLException sqle) {
                // report earlier error
            }
            throw new FormException("Changes to the form could not be published.", e);
        } finally {
            dbean.release();
        }

    }


    /**
     * Set the status of the form.
     *
     * <pre>
     * ACTIVE = read/write access for the owning space (notwithstanding other security settings).
     * READ_ONLY = read-only access.
     * PENDING = form in design, will not show up in space for users.
     * </pre>
     */
    public void setStatus(int status)
        throws FormException {
        try {
            switch (status) {
                case ACTIVE:
                	if(getRecordStatus().equals("P") && isShared()){
                		setChangeSharingStatus(true);
                	}
                    setRecordStatus("A");
                    activate(this.getSpace());
                    break;
                case READ_ONLY:
                    setRecordStatus("R");
                    store();
                    break;
                case PENDING:
                	if(getRecordStatus().equals("A") && isShared()){
                		setChangeSharingStatus(true);
                	}
                	
                    setRecordStatus("P");
                    store();
                    break;
            }
        } catch (PersistenceException pe) {
            throw new FormException("could not update status of form in the database.");
        }
    }

    public void setShareFormVisibilty(boolean visible, String spaceId) throws PersistenceException{
        try{
	    	db.prepareStatement("update pn_space_has_class set visible = ?  where class_id = ? and space_id = ?");
	    	db.pstmt.setString(1, visible ? "1" : "0");
	        db.pstmt.setString(2, getID());
	        db.pstmt.setString(3, spaceId);
	        db.executePrepared();
	    	
	        FeaturedItemsAssociation featuredItemsAssociation = new FeaturedItemsAssociation(); 
	        featuredItemsAssociation.clear();
	        featuredItemsAssociation.setObjectID(getID());
	        featuredItemsAssociation.setSpaceID(spaceId);        
	        if (visible){
	        	featuredItemsAssociation.store();
	        }else{
	        	featuredItemsAssociation.remove();
	        }
        
    	} catch (Exception e) {
    		 throw new PersistenceException("could not update shared form visiblity status ");
    	}
    }
    
    /**
     * Clears the list of workflows, adds the specified workflow
     * and sets it to default.  If workflowID is null, workflows are simply
     * cleared.
     *
     * @param workflowID the id of the workflow to add and set default
     */
    public void setWorkflowID(String workflowID) {
        clearWorkflows();
        if (workflowID != null && !workflowID.equals("")) {
            m_workflows.add(new Workflow(workflowID, true));
        }
    }

    /**
     * Returns the default workflow id for this form
     *
     * @return the default workflow id as a string, or null if there is no default
     */
    public String getDefaultWorkflowID() {
        Form.Workflow workflow;
        String defaultWorkflowID = null;

        workflow = getDefaultWorkflow();
        if (workflow != null) {
            defaultWorkflowID = workflow.workflowID;
        }
        return defaultWorkflowID;
    }

    /**
     * Clears the properties of the FormDesigner including  the space, user and
     * id.
     */
    public void clear() {
        super.clear();

        m_class_id = null;
        m_user = null;
        m_space = null;
        m_record_status = "P";
        m_spacesToShareWith = new ArrayList();
    }


    /**
     * Load this Form (class_id) from the database for design.  All the
     * FormLists and FormFields in the user's (m_person_id) scope will be
     * restored from the database also.
     */
    public void load()
        throws PersistenceException {
        String query_string;

        // New form coming in from the database, clear everything from the old one.
        // clear();


        // get form (class) info
        query_string = "select class_name, class_type_id, class_desc, class_abbreviation, max_row, max_column, owner_space_id, " +
            "methodology_id, master_table_name, data_table_key, is_sequenced, record_status, crc, " +
            "supports_discussion_group, supports_document_vault, supports_assignment, " +
            "supports_external_access, external_class_id, hide_assignment_fields_in_eaf, shared  " +
            "from pn_class where class_id=" + m_class_id;        
        try {
            db.setQuery(query_string);
            db.executeQuery();

            if (!db.result.next())
                throw new PersistenceException("Form could not be loaded from database.  Contact Administrator.");

            m_class_name = db.result.getString(1);
            m_class_type_id = db.result.getString(2);
            m_class_description = db.result.getString(3);
            m_class_abbreviation = db.result.getString(4);
            m_max_row = db.result.getInt(5);
            m_max_column = db.result.getInt(6);
            m_owner_space_id = db.result.getString(7);
            m_methodology_id = db.result.getString(8);
            m_master_table_name = db.result.getString(9);
            m_dataTableKey = db.result.getString(10);
            m_isSequenced = db.result.getBoolean(11);
            m_record_status = db.result.getString(12);
            m_crc = db.result.getTimestamp(13);
            m_supports_discussion_group = db.result.getBoolean(14);
            m_supports_document_vault = db.result.getBoolean(15);
            m_supports_assignment = db.result.getBoolean(16);
            m_supports_external_access = db.result.getBoolean(17);
            m_external_class_id = db.result.getString(18);
            m_hide_assignment_fields_in_eaf = db.result.getBoolean(19);
            shared = db.result.getBoolean(20);

            // Temporary thing ---- getting owner space
            ownerSpace = SpaceFactory.constructSpaceFromID(m_owner_space_id);
            ownerSpace.load();

            // get FormFields and FormLists in the users's scope for this Form.
            // fields must be retrieved first.  loadLists() set's the display list and default list.
            this.loadFields(true);
            this.loadLists(true);
            this.loadWorkflows();
            this.loadSharingPolicy();
            this.loadSharingStatus();
            m_isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(FormDesigner.class).error("FormDesigner.load threw an SQL Exception: " + sqle);
            throw new PersistenceException("Failed to load form designer", sqle);
        } finally {
            db.release();
        }

    }

    /**
     * Store the basic definition variables for this form to the database. This
     * method updates only the user-changable variables in the pn_class table.
     * record_status should be set to one of: 'A' = active, 'P' = pending
     * 'D' = soft deleted.<br>
     * Also creates the class_has_workflow records.
     *
     * @throws PersistenceException (actually, doesn't throw this currently)
     */
    public void store() throws PersistenceException {
        DBBean dbean = new DBBean();
        try {
            dbean.setAutoCommit(false);
            storeFormDesign(dbean);
            dbean.commit();
            dbean.release();

        } catch (Exception e) {
            // Catch all dammed Exception
            throw new PersistenceException("Activation of Form failed because of " + e, e);

        } finally {
            if (dbean.connection != null) {
                try {
                    dbean.rollback();
                } catch (SQLException sqle2) {
                    // Continue to release()
                }
            }
            dbean.release();
        } //end try

    }

    /*private void updateAsingableField() throws PersistenceException{
    	boolean assigmentFieldExist =  this.getSupportsAssignment();
    	if (assigmentFieldExist){    		
    		boolean addAssignableUserField = true;
    		List<FormField> fields = this.getFields();
    		for(FormField field : fields){
    			if (field  instanceof AssignedUserField) {
    				addAssignableUserField = false;
				}
    		}
    		
    		if (addAssignableUserField){
	    		FormFieldDesigner field = new FormFieldDesigner() ;
	    		field.setForm(this);
	    		field.setClientTypeID("100");
	    		field.setElementID("110");
	    		field.setElementLabel("Assigned User");
	    		field.setFieldLabel("Assigned User");
	    		field.setDataTableName(this.getMasterTableName());
	    		field.store();
	    		
	    		field = new FormFieldDesigner() ;
	    		field.setForm(this);
	    		field.setClientTypeID("100");
	    		field.setElementID("130");
	    		field.setElementLabel("Assignor User");
	    		field.setFieldLabel("Assignor User");
	    		field.setDataTableName(this.getMasterTableName());
	    		field.store();	    		
    		}
    	}else{
    		List<FormField> fields = this.getFields();
    		String assignedUserFieldId = null;
    		String assignorUserFieldId = null;
    		for(FormField field : fields){
    			if (field  instanceof AssignedUserField) {
    				assignedUserFieldId = field.getID();
				}else if (field instanceof AssignorUserField){
					assignorUserFieldId = field.getID();
				}
    		}
    		if (assignedUserFieldId != null){
	    		FormFieldDesigner field = new FormFieldDesigner() ;
	    		field.setID(assignedUserFieldId);
	    		field.setForm(this); 
	    		this.removeField(field);
    		}
    		if (assignorUserFieldId != null){
	    		FormFieldDesigner field = new FormFieldDesigner() ;
	    		field.setID(assignorUserFieldId);
	    		field.setForm(this); 
	    		this.removeField(field);
    		}    		    	
    	}
    }*/
    
    private void customFieldActivation(String elementId, String elementLabel, String fieldLabel, String dataColumnName) throws PersistenceException{
		FormFieldDesigner field = new FormFieldDesigner() ;
		field.setForm(this);
		field.setClientTypeID("100");
		field.setElementID(elementId);
		field.setElementLabel(elementLabel);
		field.setFieldLabel(fieldLabel);
		if (dataColumnName != null && dataColumnName.length() > 0)
			field.setDataColumnName(dataColumnName);
		field.setDataTableName(this.getMasterTableName());
		field.store();
		
		
		if (this.getRecordStatus().equals("A")){
			field.activate();
		}
    	FormField frmField = FieldFactory.makeField(Integer.valueOf(elementId), (Form)this, dataColumnName);
    	try {
			BeanUtils.copyProperties(frmField, field);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getFields().add(frmField);
    }
    
    private void updateCustomFormFields() throws PersistenceException{
    	boolean formIdFieldExist =  false;  		
    	boolean formCreatorFieldExist =  false;
    	boolean formModifyFieldExist =  false;
    	boolean formCreateDateFieldExist =  false;
    	boolean formModifyDateFieldExist =  false;
    	boolean formSpaceOwnerFieldExist =  false;
    	boolean formAssignedUserFieldExist =  false;
    	boolean formAssignorUserFieldExist =  false;
		List<FormField> fields = this.getFields();
		for(FormField field : fields){
			if (field  instanceof FormID) {
				formIdFieldExist = true;
			}else if (field instanceof CreatorUserField){
				formCreatorFieldExist = true;
			}else if (field instanceof ModifyUserField){
				formModifyFieldExist = true;
			}else if (field instanceof CreateDateField){
				formCreateDateFieldExist = true;
			}else if (field instanceof ModifyDateField){
				formModifyDateFieldExist = true;
			}else if (field instanceof MetaDataSpaceField){
				formSpaceOwnerFieldExist = true;
			}else if (field instanceof AssignedUserField){
				formAssignedUserFieldExist = true;
			}else if (field instanceof AssignorUserField){
				formAssignorUserFieldExist = true;
			}
		}
		
		if (!formIdFieldExist){
			customFieldActivation("120", "Form ID", "Form ID", "seq_num");
		}
		
		if (!formCreatorFieldExist){
			customFieldActivation("140", "Creator", "Creator", "");			
		}
		
		if (!formModifyFieldExist){
			customFieldActivation("150", "Modified by", "Modified by", "");			
		}		

		if (!formCreateDateFieldExist){
			customFieldActivation("160", "Created on", "Created on", "");			
		}		
		
		if (!formModifyDateFieldExist){
			customFieldActivation("170", "Modified on", "Modified on", "");
		}
		if (!formSpaceOwnerFieldExist){
			customFieldActivation("180", "Space name", "Space name", "");			
		}
		boolean assigmentFieldExist =  this.getSupportsAssignment();
		if (!formAssignedUserFieldExist && assigmentFieldExist){
			customFieldActivation("110", "Assigned User", "Assigned User", "");			
		}
		if (!formAssignorUserFieldExist && assigmentFieldExist){
			customFieldActivation("130", "Assignor User", "Assignor User", "");			
		}		
		if(!assigmentFieldExist && (formAssignedUserFieldExist || formAssignorUserFieldExist)){    	
    		String assignedUserFieldId = null;
    		String assignorUserFieldId = null;
    		for(FormField field : fields){
    			if (field  instanceof AssignedUserField ) {
    				assignedUserFieldId = field.getID();
				}else if (field instanceof AssignorUserField){
					assignorUserFieldId = field.getID();
				}
    		}
    		if (assignedUserFieldId != null){
	    		FormFieldDesigner field = new FormFieldDesigner() ;
	    		field.setID(assignedUserFieldId);
	    		field.setForm(this); 
	    		this.removeField(field);
    		}
    		if (assignorUserFieldId != null){
	    		FormFieldDesigner field = new FormFieldDesigner() ;
	    		field.setID(assignorUserFieldId);
	    		field.setForm(this); 
	    		this.removeField(field);
    		} 			
		}
    }
    
    /**
     * Store the basic definition variables for this form to the database.  This
     * method updates only the user-changable variables in the pn_class table.
     * record_status should be set to one of: 'A' = active, 'P' = pending
     * 'D' = soft deleted.<br>
     *
     * Also creates the class_has_workflow records.
     *
     * @throws PersistenceException (actually, doesn't throw this currently)
     */
    private void storeFormDesign(DBBean dbean) throws PersistenceException {

        // can't store without Space context.
        if (m_space == null)
            throw new NullPointerException("Space must be set before calling store().");

        // owner space is the space that the from was designed from
        m_owner_space_id = m_space.getID();
        
        // if existing class_id, update
        if (m_class_id != null) {
            modifyForm(dbean);

            //Fires Event when the Form is modified
            FormEvent event = new FormEvent();
            event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
            event.setTargetObjectID(m_class_id);
            event.setTargetObjectType(EventCodes.MODIFY_FORM);
            event.setTargetObjectXML(this.getXMLBody());
            event.setEventType(EventCodes.MODIFY_FORM);
            event.setName(EventCodes.getName(EventCodes.MODIFY_FORM));
            event.setUser(SessionManager.getUser());
            event.setDescription("Modify Form : \"" + getName() + "\"");
            event.store();
            
			// publishing event to asynchronous queue
	        try {
	        	net.project.events.FormEvent formEvent = (net.project.events.FormEvent) EventFactory.getEvent(ObjectType.FORM, EventType.EDITED);
	        	formEvent.setObjectID(m_class_id);
	        	formEvent.setObjectType(ObjectType.FORM);
	        	formEvent.setName(getName());
	        	formEvent.setObjectRecordStatus(getRecordStatus());
	        	formEvent.publish();
			} catch (EventException e) {
				Logger.getLogger(FormDesigner.class).error("FormDesigner.storeFormDesign():: Form Modify Event Publishing Failed! "+ e);
			}

        }
        // if new form, insert
        else {
            createForm(dbean);
            //Fires Event when the Form is created
            FormEvent event = new FormEvent();
            event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
            event.setTargetObjectID(m_class_id);
            event.setTargetObjectType(EventCodes.CREATE_FORM);
            event.setTargetObjectXML(this.getXMLBody());
            event.setEventType(EventCodes.CREATE_FORM);
            event.setName(EventCodes.getName(EventCodes.CREATE_FORM));
            event.setUser(SessionManager.getUser());
            event.setDescription("Create Form : \"" + getName() + "\"");
            event.store();
            
			// publishing event to asynchronous queue
	        try {
	        	net.project.events.FormEvent formEvent = (net.project.events.FormEvent) EventFactory.getEvent(ObjectType.FORM, EventType.NEW);
	        	formEvent.setObjectID(m_class_id);
	        	formEvent.setObjectType(ObjectType.FORM);
	        	formEvent.setName(getName());
	        	formEvent.setObjectRecordStatus(getRecordStatus());
	        	formEvent.publish();
			} catch (EventException e) {
				Logger.getLogger(FormDesigner.class).error("FormDesigner.storeFormDesign():: Form Create Event Publishing Failed! "+ e);
			}
        }
        //updateAsingableField();
        updateCustomFormFields();
    }

    /**
     * Create the query that is going to be used to update the form when it is
     * being modified.  This method has been separated as a refactoring for the
     * size of the modifyForm() method.
     *
     * @since Gecko Update 2 (ProductionLink)
     * @return a <code>String</code> value containing the update statement.
     */
    private String createUpdateQueryForModify(java.sql.Timestamp newCrc) {
        StringBuffer updateQuery = new StringBuffer();

        updateQuery.append("update pn_class set class_name=" + DBFormat.varchar2(m_class_name));
        updateQuery.append(", class_type_id=" + DBFormat.varchar2(m_class_type_id));
        updateQuery.append(", class_desc=" + DBFormat.varchar2(m_class_description));
        updateQuery.append(", class_abbreviation=" + DBFormat.varchar2(m_class_abbreviation));
        updateQuery.append(", owner_space_id= " + DBFormat.number(m_owner_space_id));
        if (m_methodology_id != null)
            updateQuery.append(", methodology_id= " + DBFormat.number(m_methodology_id));
        updateQuery.append(", record_status= " + DBFormat.varchar2(m_record_status));
        updateQuery.append(", crc = " + DBFormat.crc(newCrc));
        updateQuery.append(", supports_discussion_group = " + DBFormat.bool(m_supports_discussion_group));
        updateQuery.append(", supports_document_vault = " + DBFormat.bool(m_supports_document_vault));
        updateQuery.append(", supports_assignment = " + DBFormat.bool(m_supports_assignment));
        updateQuery.append(", supports_external_access = " + DBFormat.bool(m_supports_external_access));
        if (isClearExternalClassId()){
        	updateQuery.append(", external_class_id = null ");
        }else {
        	if (m_external_class_id != null && m_external_class_id.trim().length() > 0){
        		updateQuery.append(", external_class_id = " + m_external_class_id);
        	}else {
        		updateQuery.append(", external_class_id = " + GregorianCalendar.getInstance().getTimeInMillis());
        	}
        }
        updateQuery.append(", hide_assignment_fields_in_eaf = " + DBFormat.bool(m_hide_assignment_fields_in_eaf));
        updateQuery.append(", shared = " + DBFormat.bool(shared));
        updateQuery.append(" where class_id=" + m_class_id);

        return updateQuery.toString();
    }

    /**
     * Lock the database record of this form so no others can update it while we
     * are.
     *
     * @param db a <code>DBBean</code> value that is already in the middle of a
     * transaction.
     */
    private java.sql.Timestamp lockFormRecord(DBBean db) throws SQLException, PersistenceException {
        java.sql.Timestamp newCrc = new java.sql.Timestamp(new java.util.Date().getTime());
        java.util.Date crc;
        StringBuffer lockQuery = new StringBuffer();

        // Build statement to lock pn_class record
        lockQuery.append("select crc from pn_class where class_id = ").append(m_class_id);
        lockQuery.append(" for update nowait ");

        db.executeQuery(lockQuery.toString());

        // Now get and check crc of locked record.  It must match the crc of
        // this form object in order to continue.
        if (db.result.next()) {
            // We got a row from the query
            crc = db.result.getTimestamp("crc");
            if (!crc.equals(m_crc)) {
                throw new PersistenceException("Form has been modified by another user.");
            }

        } else {
            // There is a problem if we got no rows from the query
        	Logger.getLogger(FormDesigner.class).error("Form.store failed to lock form. No rows found with query " + lockQuery.toString());
            throw new PersistenceException("Error storing form");
        }

        db.closeStatement();
        db.closeResultSet();

        return newCrc;
    }

    private void disableFormSharing(DBBean db) throws SQLException{
        db.prepareStatement("delete from pn_space_has_class where class_id = ? and space_id <> ?");
        db.pstmt.setString(1, getID());
        db.pstmt.setString(2, getSpace().getID());
        db.executePrepared();
        
        db.prepareStatement("delete from pn_space_has_featured_menuitem where object_id = ? and space_id <> ?");
        db.pstmt.setString(1, getID());
        db.pstmt.setString(2, getSpace().getID());
        db.executePrepared();            	
    }
    
    private void createFormHierarchicalSharingPolicy(DBBean db) throws SQLException, PersistenceException {
        //First, delete any existing form id's that are being shared.
    	disableFormSharing(db);
    	
        if(this.isShared()){
      	    db.prepareStatement("UPDATE pn_class_list SET is_shared = 1 where class_id = ? ");
            db.pstmt.setString(1, getID());
            db.executePrepared();        	
        	
            StringBuffer query = new StringBuffer();
            query.append("select distinct shs.child_space_id, shs.child_space_type, shs.parent_space_id, shs.parent_space_type, ");
            query.append("shs.relationship_child_to_parent, shs.relationship_parent_to_child "); 
            query.append("from pn_space_has_space shs ");
            query.append("where shs.child_space_type = 'project' or shs.child_space_type = 'business' ");            
            query.append("start with shs.parent_space_id = ? connect by shs.parent_space_id = prior shs.child_space_id ");
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, getSpace().getID());
            db.executePrepared();
            ResultSet rs = db.result;
            if (rs.getFetchSize() >0){	
            	List<String> childSpaceIds = new ArrayList<String>();
            	HashMap<String, List<String>> connections = new HashMap<String,List<String>>();
            	HashMap<String, String> projectBusiness = new HashMap<String,String>();
            	while (rs.next()){
            		if (!childSpaceIds.contains(rs.getString("child_space_id")))
            			childSpaceIds.add(rs.getString("child_space_id"));
            		
            		String childId = rs.getString("child_space_id");
            		String parentId = rs.getString("parent_space_id");            		
            		if (connections.get(parentId) == null){
            			List<String> childs = new ArrayList<String>();
            			childs.add(childId);
            			connections.put(parentId, childs);
            		}else{
            			connections.get(parentId).add(childId);
            		}            		
            		if (connections.get(childId) == null){
            			List<String> childs = new ArrayList<String>();
            			connections.put(childId, childs);
            		}
            		//create a map with connection between project and business
            		//if project does not have business owner it will have 0 as value in map - 
            		//those are projects that should not get selected form as shared 
            		if (rs.getString("child_space_type").equals("project")){
            			String parentBusinessId = rs.getString("parent_space_type").equals("project") ? "0" : rs.getString("parent_space_id"); 
            			if (projectBusiness.get(rs.getString("child_space_id")) == null || 
            					projectBusiness.get(rs.getString("child_space_id")).equals("0")){
            				projectBusiness.put(rs.getString("child_space_id"), parentBusinessId);
            			}
            		}            		
            	}
            	  db.prepareStatement("delete from PN_SHARED_FORMS_VISIBLITY where class_id = ? ");
                  db.pstmt.setString(1, getID());
                  db.executePrepared();            	
            	                  
            	for(Map.Entry<String, List<String>> entry : connections.entrySet()){
            		List<String> childIds = new ArrayList<String>();
            		for(String childId : entry.getValue()){
            			childIds.addAll(FormManager.getChildSpaceIds(childId, connections));
            		}            		
            		childIds.addAll(entry.getValue());
            		childIds.add(entry.getKey());

            		StringBuffer childIdsStr = new StringBuffer();
            		for (String childId : childIds){
            			childIdsStr.append(childId).append(", ");	
            		}
            		if (projectBusiness.get(entry.getKey()) == null || !projectBusiness.get(entry.getKey()).equals("0")){
            			InsertIntoSharedFormsVisiblityTable(db, entry.getKey(), getID(), childIdsStr.toString().substring(0, childIdsStr.length() > 0 ? childIdsStr.toString().lastIndexOf(",") : 0));
            		}
            	}
            	
	            db.prepareStatement("insert into pn_space_has_class (space_id, class_id, is_owner, visible) values (?, ?, 0, 0)");
	            for (String childSpaceId : childSpaceIds){
	            	if (projectBusiness.get(childSpaceId) == null || !projectBusiness.get(childSpaceId).equals("0")){
	            		db.pstmt.setString(1, childSpaceId);
	            		db.pstmt.setString(2, getID());
	            		db.pstmt.addBatch();
	            	}
	            }
	            db.pstmt.executeBatch();
            }
        }        
    }
    
    private void InsertIntoSharedFormsVisiblityTable(DBBean db, String spaceId, String classId, String childIds) throws PersistenceException {
    	
    	//Steps to insert in to SharedFormsVisiblityTable
        // 1. Insert the Clob, initializing it to the empty_clob()
        // 2. Reload the Clob locater
        // 3. Stream the data to the Clob

        // Statement of the form:
        //INSERT INTO  pn_shared_forms_visiblity (space_id, class_id, child_ids) values (?, ?, empty_clob())");
        StringBuffer insertQuery = new StringBuffer();
        insertQuery.append("INSERT INTO ").append("pn_shared_forms_visiblity").append(" (");
        insertQuery.append("space_id").append(", ").append("class_id").append(", ").append("child_ids");
        insertQuery.append(") VALUES (?, ?, empty_clob()) ");

        PreparedStatement pstmt = null;
        try {
            // Execute the insert statement to create the empty clob locater
            pstmt = db.getConnection().prepareStatement(insertQuery.toString());
            pstmt.setString(1, spaceId);
            pstmt.setString(2, classId);
            pstmt.executeUpdate();
        } catch (SQLException sqle) {
            throw new PersistenceException("Clob store operation failed: " + sqle, sqle);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException sqle) {
                throw new PersistenceException("Clob store operation failed: " + sqle, sqle);
            }
        }
        
        //Now load this empty clob...
        
        ResultSet rs = null;
        // The Clob locater for the clob data
        java.sql.Clob clob = null;

        StringBuffer loadQuery = new StringBuffer();
        loadQuery.append("SELECT ").append("child_ids").append(" ");
        loadQuery.append("FROM ").append("pn_shared_forms_visiblity").append(" ");
        loadQuery.append("WHERE ").append("space_id").append(" = ? ");
        loadQuery.append("AND ").append("class_id").append(" = ? ");
        loadQuery.append("FOR UPDATE NOWAIT ");
        
        try {
			pstmt = db.getConnection().prepareStatement(loadQuery.toString());
			pstmt.setString(1, spaceId);
	        pstmt.setString(2, classId);
	        rs = pstmt.executeQuery();
	        
	        // Check that we got one row
	        if (rs.next()) {
	            // Save the Clob as an oracle CLOB
	            // Reads the returned column matching the data column name specified
	            clob = rs.getClob("child_ids");
	        }
		} catch (SQLException sqle) {
			throw new PersistenceException("Clob store operation failed: " + sqle, sqle);
			
		} finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException sqle) {
                throw new PersistenceException("Clob store operation failed: " + sqle, sqle);
            }
        }
		
		if(clob != null){
			// Now write the data to it
			ClobHelper.write(clob, childIds);
		}
    }
    
    /**
     * Insert entries into the pn_space_has_class table to allow the design of
     * this form to be seen in other spaces.
     *
     * @since Gecko Update 2 (ProductionLink)
     * @throws SQLException if there is an error while accessing the database.
     */
    private void createFormSharingPolicy(DBBean db) throws SQLException {
        try {
            //First, delete any existing form id's that are being shared.
            db.prepareStatement("delete from pn_space_has_class where class_id = ? and space_id <> ?");
            db.pstmt.setString(1, getID());
            db.pstmt.setString(2, getSpace().getID());
            db.executePrepared();

            //A non-null value indicates that there are spaces that need to be shared.
            //A null value indicates that we aren't sharing this form and the above
            //deletion was appropriate
            if ((m_spacesToShareWith != null) && (m_spacesToShareWith.size() != 0)) {
                //Set up the query that will insert rows into the pn_space_has_class table.
                db.prepareStatement("insert into pn_space_has_class " +
                    "  (space_id, class_id, is_owner) " +
                    "values (?, ?, 0)");

                //Now, insert each of the shares into the database.
                Iterator it = m_spacesToShareWith.iterator();
                while (it.hasNext()) {
                    String next = (String)it.next();
                    db.pstmt.setString(1, next);
                    db.pstmt.setString(2, getID());
                    db.pstmt.addBatch();
                }

                //Execute the batched list of prepared statements.
                db.pstmt.executeBatch();
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FormDesigner.class).error(sqle.toString());
            throw sqle;
        }
    }


    /**
     * Modifies an existing form
     * <p><b>Preconditions:</b>
     * <li>none</li>
     * </p>
     * <p><b>Postconditions:</b>
     * <li>DBBean db is released</li>
     * <li><code>pn_class</code> record modified,
     * <code>pn_class_has_workflow</code> records inserted</li>
     * </p>
     * @throws PersistenceException if there is a problem modifying the form
     */
    private void modifyForm(DBBean db) throws PersistenceException {
        StringBuffer deleteQuery = new StringBuffer();
        String updateQuery;
        java.sql.Timestamp newCrc;

        // Construct queries
        // Build statement to delete all class_has_workflow records for this class
        deleteQuery.append("delete from pn_class_has_workflow where class_id = " + m_class_id + " ");

        try {
            db.openConnection();
            db.connection.setAutoCommit(false);

            // First, attempt to lock the PN_CLASS record
            try {
                newCrc = lockFormRecord(db);
            } catch (SQLException sqle) {
            	Logger.getLogger(FormDesigner.class).error("Form.store error locking form. " +
                    sqle);
                throw new PersistenceException("Error storing form", sqle);
            } //end try

            //Create the update statement for the form
            updateQuery = createUpdateQueryForModify(newCrc);

            // Update the class record and delete the class_has_workflow records
            db.createStatement();
            db.stmt.addBatch(updateQuery);
            db.stmt.addBatch(deleteQuery.toString());
            db.stmt.executeBatch();

            // Now insert the class_has_workflow_records
            insertWorkflows(db);

            //Update the form sharing policy
            //createFormSharingPolicy(db);
            if (isChangeSharingStatus() && getRecordStatus().equals("A")){
            	createFormHierarchicalSharingPolicy(db);
            }else if (isChangeSharingStatus() && getRecordStatus().equals("P")){
            	disableFormSharing(db);
            }

            // Success!  Commit work
            db.commit();
            // Now retain crc just inserted
            m_crc = newCrc;

        } catch (SQLException sqle) {
        	Logger.getLogger(FormDesigner.class).error("FormDesigner.store threw an SQL Exception: " + sqle);
            throw new PersistenceException("Error occured storing form", sqle);

        } finally {
            db.release();
        }
    }

    /**
     * Creates a new form, owned by the current space.
     * <p><b>@Preconditions:</b>
     * <li>none</li>
     * </p>
     * <p><b>@Postconditions:</b>
     * <li>DBBean db is released</li>
     * <li><code>pn_class</code> record created,
     * <code>pn_class_has_workflow</code> records inserted</li>
     * </p>
     * @throws PersistenceException if there is a problem creating the form
     */
    private void createForm(DBBean db) throws PersistenceException {
        java.sql.Timestamp newCrc = new java.sql.Timestamp(new java.util.Date().getTime());
        // get new id and register in the pn_object table.
        m_class_id = ObjectManager.dbCreateObjectWithPermissions(ObjectType.FORM, "A", getSpace().getID(), getUser().getID());
        Long externalClassId = m_supports_external_access ? GregorianCalendar.getInstance().getTimeInMillis() : null;
        try {
            db.openConnection();
            db.setAutoCommit(false);
            // Update the class record
            db.prepareStatement("insert into pn_class (class_id, class_name, class_type_id, " +
                "  class_desc, class_abbreviation, owner_space_id, methodology_id, " +
                "  max_row, max_column, next_data_seq, record_status, data_table_key, " +
                "  crc, supports_discussion_group, supports_document_vault, supports_assignment, " +
                "supports_external_access, external_class_id, hide_assignment_fields_in_eaf, shared) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?, ?, ?)");
            db.pstmt.setString(1, m_class_id);
            db.pstmt.setString(2, m_class_name);
            db.pstmt.setString(3, m_class_type_id);
            db.pstmt.setString(4, m_class_description);
            db.pstmt.setString(5, m_class_abbreviation);
            db.pstmt.setString(6, m_owner_space_id);
            db.pstmt.setString(7, m_methodology_id);
            db.pstmt.setInt(8, 0);
            db.pstmt.setInt(9, 0);
            db.pstmt.setInt(10, 1);
            db.pstmt.setString(11, m_record_status);
            db.pstmt.setString(12, m_dataTableKey);
            db.pstmt.setTimestamp(13, newCrc);
            db.pstmt.setBoolean(14, m_supports_discussion_group);
            db.pstmt.setBoolean(15, m_supports_document_vault);
            db.pstmt.setBoolean(16, m_supports_assignment);
            db.pstmt.setBoolean(17, m_supports_external_access);            
            if (externalClassId == null ){
            	db.pstmt.setNull(18, java.sql.Types.INTEGER);
            }else {
            	db.pstmt.setLong(18, externalClassId);
            }
            db.pstmt.setBoolean(19, m_hide_assignment_fields_in_eaf);
            db.pstmt.setBoolean(20, shared);
            db.executePrepared();
            // Now insert the class_has_workflow_records
            insertWorkflows(db);

            // Success!  Commit work
            db.commit();
            // Now retain crc just inserted
            m_crc = newCrc;

            // add the form to the space.
            // The form is owned by this space
            db.release();

            db.executeQuery("insert into pn_space_has_class (space_id, class_id, is_owner) " +
                "values (" + getSpace().getID() + ", " + getID() + ", " +
                Conversion.booleanToInt(true) + ") ");
        } catch (SQLException sqle) {
        	Logger.getLogger(FormDesigner.class).error("FormDesigner.store threw an SQL Exception: " + sqle);
            throw new PersistenceException("Error occured storing form", sqle);
        } finally {
            if (db.connection != null) {
                try {
                    db.connection.rollback();
                } catch (SQLException sqle2) {
                    // Continue to release()
                }
            }
            db.release();
        } //end try
    }


    /**
     * Inserts the pn_class_has_workflow records into the database
     * <p><b>Preconditions:</b><br>
     *     <li>db.connection object exists and has autocommit turned OFF
     * </p>
     * <p><b>Postconditions:</b>
     *     <li>records are inserted in the database
     *     <li>No commit or rollback has been performed
     * </p>
     * Note - no exceptions are handled here
     * @throws SQLException if there are any problems - a rollback MUST be done
     */
    private void insertWorkflows(DBBean db) throws SQLException {
        Iterator it;
        Form.Workflow workflow;
        StringBuffer insertQuery = new StringBuffer();

        try {
            it = m_workflows.iterator();
            if (it.hasNext()) {
                insertQuery.append("insert into pn_class_has_workflow (class_id, workflow_id, is_default) ");
                insertQuery.append("values (?, ?, ?) ");
                db.prepareStatement(insertQuery.toString());

                // set prepared statement parameters for each workflow in this class
                while (it.hasNext()) {
                    workflow = (Form.Workflow)it.next();
                    db.pstmt.setString(1, m_class_id);
                    db.pstmt.setString(2, workflow.workflowID);
                    db.pstmt.setString(3, (workflow.isDefault ? "1" : "0"));
                    db.pstmt.addBatch();
                }

                // Insert all the workflows... don't care about the results other than
                // success or failure.
                db.pstmt.executeBatch();
                db.closePStatement();

            }

        } catch (Exception e) {
        	Logger.getLogger(FormDesigner.class).error("An unexpected error has occurred in FormDesigner.insertWorkflows: " + e);
        } finally {
            // Clean up after myself
            db.closePStatement();
        }
    }

    /**
     * Copies a field with the specified id within this form.
     *
     * @param fieldID the id of the field to copy
     * @param user the current user performing the copy
     * @throws FormException if the field is not found
     * @throws PersistenceException if there is an error copying
     */
    public String copyField(String fieldID, User user) throws FormException, PersistenceException {
        DBBean db = new DBBean();
        String newFieldID = null;
        FormField field = getField(fieldID);

        if (field == null) {
            throw new FormException("Unable to copy field, field not found with id: " + fieldID);
        }

        try {
            db.openConnection();
            db.setAutoCommit(false);

            newFieldID = new FormFieldCopier().copy(db, fieldID, this.getID(), user);

            db.commit();

        } catch (SQLException sqle) {
        	Logger.getLogger(FormDesigner.class).error("FormDesigner.copyField threw an SQLException: " + sqle);
            throw new PersistenceException("Error copying field", sqle);

        } finally {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                // Do nothing but release
            }

            db.release();
        }

        return newFieldID;
    }

    public boolean isDeleted(String classId) throws PersistenceException {
        
        String query = null;
        String status;

        query = "select record_status from pn_class where class_id=" + classId;       
        try {
            db.setQuery(query);
            db.executeQuery();
           
            if (!db.result.next())
                throw new PersistenceException("Form could not be loaded from database.  Contact Administrator.");
           
            status = db.result.getString("record_status");
       
            return status.equalsIgnoreCase("D");

        } catch (SQLException sqle) {
            Logger.getLogger(FormDesigner.class).error("FormDesigner threw an SQL Exception: " + sqle);
            throw new PersistenceException("Failed to load form designer", sqle);
        } finally {
            db.release();
        }
    }
    

}
