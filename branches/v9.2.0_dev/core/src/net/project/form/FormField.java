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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.database.ObjectManager;
import net.project.gui.html.IHTMLOption;
import net.project.persistence.PersistenceException;
import net.project.util.Conversion;
import net.project.util.ParseString;
import net.project.util.StringUtils;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * A FormField represents a designed field on a Form.
 */
public abstract class FormField implements IFormField, java.io.Serializable, IHTMLOption {
    // NOTE:  these integer ids must match the class_ids in the PN_ELEMENT, PN_CLASS_ELEMENT,
    //              and PN_CLASS tables for the field element display classes.
    //              Numbers assigned to the element display classes in the load_form_tables.sql database creation script.
    //
    //  Types of Fields

    /** A single line text field */
    public static final String TEXT = "21";
    /** A multi-line scrolled text box. */
    public static final String TEXTAREA = "22";
    /** A single item selection list. */
    public static final String SINGLE_MENU = "23";
    /** A date field */
    public static final String DATE = "24";
    /** A yes/no checkbox */
    public static final String CHECKBOX = "25";
    /** A multiple item selection list. */
    public static final String MULTI_MENU = "26";
    /** A time field */
    public static final String TIME = "27";
    /** A date and time field */
    public static final String DATE_TIME = "28";
    /** A single person selection list */
    public static final String SINGLE_PERSON = "29";
    /**  A multiple person selection list. */
    public static final String MULTI_PERSON = "30";
    /** A milestone selection list. */
    public static final String MILESTONE = "31";
    /** A number field. */
    public static final String NUMBER = "32";
    /** A currency field. */
    public static final String CURRENCY = "33";

    //
    // Static Form Fields
    //

    /** A form horizontal separator */
    public static final String HORIZONTAL_SEPARATOR = "40";
    /** An user instruction text line.  */
    public static final String INSTRUCTION = "41";


    //
    // Custom Form Fields
    //

    /** A Calculated Field */
    public static final String CALCULATION_FIELD = "100";

    /** An assigned user field - shows up in list and list designer only **/
    public static final String ASSIGNED_USER_FIELD = "110";
    
    /** An assigned user field - shows up in list and list designer only **/
    public static final String ASSIGNOR_USER_FIELD = "130";    
    
    /** An creator user field - shows up in list and list designer only **/
    public static final String CREATOR_USER_FIELD = "140";    
    
    /** An modify user field - shows up in list and list designer only **/
    public static final String MODIFY_USER_FIELD = "150";    

    /** An create date - shows up in list and list designer only **/
    public static final String CREATE_DATE_FIELD = "160";    
    
    /** An modify date field - shows up in list and list designer only **/
    public static final String MODIFY_DATE_FIELD = "170";    
    
    /** An record space owner field - shows up in list and list designer only **/
    public static final String SPACE_OWNER_FIELD = "180";    

    protected Form m_form;
    protected FieldDomain m_domain = null;
    //protected boolean m_has_domain = false;

    /**
     * the list of FormFieldProperty for rendering the field to the display.
     * (the "tag" is m_tag)
     */
    protected ArrayList m_properties = null;

    /** member variable used in both Form and FormList rendering */
    protected String m_class_id = null;
    protected String m_field_id = null;
    protected String m_client_type_id = Form.HTML_CLIENT;
    protected String m_db_datatype = null;
    protected String m_element_id = TEXT;  // default type for field is single line text.
    protected String m_element_name = null;
    protected String m_element_label = null;
    protected String m_elementDisplayClassID = FormField.TEXT;
    protected String m_field_label = null;
    protected String m_tag = null;
    protected String m_data_table_name = null;
    protected String m_data_column_name = null;
    /** Max number of digits for column. */
    protected int m_data_column_size = -1;
    /** Scale is for number fields; when scale is 0 it is ignored. */
    protected int m_data_column_scale = 0;
    protected boolean m_data_column_exists = false;
    protected String m_field_group = null;
    protected int m_row_num = -1;
    protected int m_column_num = -1;
    protected int m_row_span = -1;
    protected int m_column_span = -1;
    //protected int m_precision_num = -1;
    protected String m_column_id = null;
    protected String m_max_value = null;
    protected String m_min_value = null;
    protected String m_default_value = null;
    protected String m_instructions = null;
    protected String m_record_status = null;
    protected java.util.Date m_crc = null;
    protected boolean m_useDefault = false;
    protected boolean m_isMultiSelect = false;

    /** Indicates whether this field requires a value. */
    protected boolean isValueRequired = false;

    /** Indicates whether this field is hidden on EAF forms. */
    protected boolean hiddenForEaf = false;    
    
    // db access bean
    private final DBBean db = new DBBean();
    protected boolean m_isLoaded = false;
    
    /** Name of the database table for form */
    protected String formTableName;    

    /**
     * db access bean. Needed for loadProperties(). Calling the form's bean will
     * trash the Form.loadFields() bean. TODO -- get this bean out of here. Get
     * field properties from FieldFactory or in main Form.loadFields() query.
     * Will speed things up.
     */
    private final DBBean property_db = new DBBean();

    /**
     * Construct a FormField.
     *
     * @param form the Form that this field belongs to.
     * @param field_id the id of the field in the database.
     */
    public FormField(Form form, String field_id) {
        m_form = form;
        m_class_id = form.m_class_id;
        m_field_id = field_id;
        m_properties = new ArrayList(10);
    }

    /**
     * Bean constructor.
     */
    public FormField() {
        m_properties = new ArrayList(10);
    }


    /**
     * Set the Form that own's this field.
     */
    public void setForm(Form form) {
        m_form = form;
        m_class_id = form.m_class_id;
    }

    public Form getForm() {
        return this.m_form;
    }

    /**
     * Set the field's database ID.
     */
    public void setID(String fieldID) {
        m_field_id = fieldID;
        m_isLoaded = false;
    }


    /**
     * Get the field's database ID.
     */
    public String getID() {
        return m_field_id;
    }


    /**
     * Get the field's element ID.
     */
    public String getElementID() {
        return m_element_id;
    }

    /**
     * Set the field's element ID.
     */
    public void setElementID(String elementID) {
        m_element_id = elementID;
    }


    /**
     * Get the field's classID.
     */
    public String getClassID() {
        return m_class_id;
    }

    /**
     * Set the field's classID.
     */
    public void setClassID(String classID) {
        m_class_id = classID;
    }

    /**
     * Set the field's domainID and load domain and domain values from
     * persistence.
     */
    public void setDomainID(String domainID) {
        m_domain = new FieldDomain();
        m_domain.setForm(m_form);
        m_domain.setField(this);
        m_domain.setID(domainID);

        try {
            m_domain.load();
        } catch (PersistenceException pe) {
            m_domain = null;
        }
    }


    /**
     * Can this field store data to the database. Instruction,
     * horizontalSeparator fields are static and not storable.
     */
    public boolean isStorable() {
        return (this.dbStorageType() != null);
    }

    /**
     * Can this field be available in exported cvs format
     * By default all fields whichever is Storable is exportable
     * Subclass of FormField can override this property, if required
     */
    public boolean isExportable() {
        return isStorable();
    }
    
    
    /**
     * Get the field's Database datatype.
     */
    public String getDatatype() {
        return m_db_datatype;
    }

    /**
     * Set the field's Database datatype.
     */
    public void setDatatype(String datatype) {
        m_db_datatype = datatype;
    }

    /** Get the field's client type ID (html, wap, java, xml, etc.).  */
    public String getClientTypeID() {
        return m_client_type_id;
    }

    /** Set the field's client type ID (html, wap, java, xml, etc.). */
    public void setClientTypeID(String clientTypeID) {
        m_client_type_id = clientTypeID;
    }


    /** Get the field's client rendering tag, if supported.  */
    public String getTag() {
        return m_tag;
    }


    /** Get the field's  ElementName */
    public String getElementName() {
        return m_element_name;
    }

    public void setElementName(String elementName) {
        this.m_element_name = elementName;
    }

    /** Get the field's ElementLabel. */
    public String getElementLabel() {
        return m_element_label;
    }

    public void setElementLabel(String elementLabel) {
        this.m_element_label = elementLabel;
    }

    /**
     * Set the display class ID for the element this field is a type of. Used by
     * form designer to display field property sheet forms.
     */
    public void setElementDisplayClassID(String value) {
        m_elementDisplayClassID = value;
    }

    /**
     * Return the display class ID for the element this field is a type of. Used
     * by form designer to display field property sheet forms.
     */
    public String getElementDisplayClassID() {
        return m_elementDisplayClassID;
    }

    /** Get the field's FieldLabel. */
    public String getFieldLabel() {
        return m_field_label;
    }

    /** Set the field's FieldLabel. */
    public void setFieldLabel(String value) {
        m_field_label = value;
    }


    /** Get the field's DataTableName. */
    public String getDataTableName() {
        return m_data_table_name;
    }

    /** Set the field's DataTableName. */
    public void setDataTableName(String value) {
        m_data_table_name = value;
    }


    /** Get the field's DataColumnName. */
    public String getDataColumnName() {
        return m_data_column_name;
    }

    /** Set the field's DataColumnName. */
    public void setDataColumnName(String value) {
        m_data_column_name = value;
    }


    /** set the field's FieldGroup */
    public void setFieldGroup(String value) {
        m_field_group = value;
    }

    /** get the field's FieldGroup */
    public String getFieldGroup() {
        return m_field_group;
    }


    /** get the field's DataColumnSize */
    public String getDataColumnSize() {
        return new Integer(getDataColumnSizeNumber()).toString();
    }

    public int getDataColumnSizeNumber() {
        return m_data_column_size;
    }

    /** set the field's DataColumnSize */
    public void setDataColumnSize(String value) {
        m_data_column_size = Integer.parseInt(value);
    }

    /**
     * Returns this field's column scale.
     *
     * @return the scale
     */
    public String getDataColumnScale() {
        return new Integer(getDataColumnScaleNumber()).toString();
    }

    public int getDataColumnScaleNumber() {
        return m_data_column_scale;
    }

    /**
     * Sets this field's column scale. The scale is typically used for NUMBER
     * fields that require decimal digits.
     *
     * @param value the scale value
     */
    public void setDataColumnScale(String value) {
        m_data_column_scale = Integer.parseInt(value);
    }


    /** get the field's RowNum as a String */
    public String getRowNum() {
        return new Integer(m_row_num).toString();
    }

    /** get the field's RowNum as an int*/
    public int rowNum() {
        return m_row_num;
    }

    /** set the field's RowNum */
    public void setRowNum(String value) {
        m_row_num = Integer.parseInt(value);
    }

    /** get the field's ColumnNum */
    public String getColumnNum() {
        return new Integer(m_column_num).toString();
    }

    /** get the field's ColumnNum as an int*/
    public int columnNum() {
        return m_column_num;
    }

    /** set the field's ColumnNum */
    public void setColumnNum(String value) {
        m_column_num = Integer.parseInt(value);
    }


    /** get the field's RowSpan */
    public String getRowSpan() {
        return new Integer(m_row_span).toString();
    }

    /** set the field's RowSpan */
    public void setRowSpan(String value) {
        m_row_span = Integer.parseInt(value);
    }

    /** get the field's ColumnSpan */
    public String getColumnSpan() {
        return new Integer(m_column_span).toString();
    }

    /** set the field's ColumnSpan */
    public void setColumnSpan(String value) {
        m_column_span = Integer.parseInt(value);
    }


    public void setColumnID(String columnID) {
        this.m_column_id = columnID;
    }

    public String getColumnID() {
        return this.m_column_id;
    }

    /** set the field's MaxValue */
    public void setMaxValue(String value) {
        m_max_value = value;
    }

    /** get the field's MaxValue */
    public String getMaxValue() {
        return m_max_value;
    }


    /** set the field's MinValue */
    public void setMinValue(String value) {
        m_min_value = value;
    }

    /** get the field's MinValue */
    public String getMinValue() {
        return m_min_value;
    }


    /** set the field's DefaultValue */
    public void setDefaultValue(String value) {
        m_default_value = value;
    }

    /** get the field's DefaultValue */
    public String getDefaultValue() {
        return m_default_value;
    }


    /** set the field's instructions */
    public void setInstructions(String value) {
        m_instructions = value;
    }

    /** get the field's instructions */
    public String getInstructions() {
        return m_instructions;
    }


    /**
     * Set the field's data default behavior.
     *
     * @param value if true, use the default value defined for the field,
     * otherwise use a null or empty field.
     */
    public void setUseDefault(boolean value) {
        m_useDefault = value;
    }

    /**
     * Get the field's data default behavior.
     *
     * @return true if the default value defined for the field should be used,
     * false otherwise.
     */
    public boolean useDefault() {
        return m_useDefault;
    }


    /**
     * Set the field as a multiple selection field. Not all field types support
     * multiple selection and this option may be ignored.
     */
    public void setMultiSelect(boolean value) {
        m_isMultiSelect = value;
    }

    /**
     * Is the field a multiple selection field. Not all field types support
     * multiple selection and this option may be ignored.
     */
    public boolean isMultiSelect() {
        return m_isMultiSelect;
    }


    /**
     * Has this field already been "applied" and have a database column in the
     * database?
     */
    public boolean dataColumnExists() {
        return m_data_column_exists;
    }


    /**
     * Returns the SQL table and column name of this FormField.  This is of the
     * form <code>tablename.columnname</code>.
     *
     * @return the SQL table and column name
     */
    public String getSQLName() {
        return m_data_table_name + "." + m_data_column_name;
    }


    /**
     * Returns the column expression to use in a SQL select statement.
     * The default is simply the same value returned by {@link #getSQLName}.
     * Specialized FormFields may return a SQL function that is manipulates
     * the column value in a SQL select statement, for example:
     * <code>TO_CHAR(tablename.columnname, 'FMMM/DD/YYYY')</code>
     *
     * @return the column expression used to indentify a this FormField in a
     * SQL select statement
     */
    protected String getSQLSelectColumn() {
        return getSQLName();
    }


    /**
     * Add a property to this the FormField.
     *
     * @param property a FormFieldProperty object.
     */
    public void addProperty(FormFieldProperty property) {
        this.m_properties.add(property);
    }


    /**
     * Get the Properties for this field.
     *
     * @return the ArrayList of FormFieldProperty for this field.
     */
    public ArrayList getProperties() {
        return m_properties;
    }


    /**
     * Get the named property.
     *
     * @param name the name of the property to return.
     * @return the FormFieldProperty with the specified name.
     */
    public FormFieldProperty getProperty(String name) {
        FormFieldProperty property;
        ArrayList propList = this.getProperties();
        Iterator iter = propList.iterator();

        if (name == null)
            return null;

        while (iter.hasNext()) {
            property = (FormFieldProperty)iter.next();

            // try to get the formField corresponding to this field_id
            if ((property.getName() != null) && !property.getName().equals("") && property.getName().equals(name))
                return property;
        }

        // nothing found
        return null;
    }


    /**
     * Get the named property value.
     *
     * @param name the name of the property to return.
     * @return the String property value for the specified name.
     */
    public String getPropertyValue(String name) {
        return getProperty(name).getValue();
    }


    /**
     * Sets the domain for this field.  This method has the side effect of
     * storing the FieldDomain automatically.
     */
    public void setDomain(FieldDomain domain) {
        m_domain = domain;
        if (m_domain != null) {
            try {
                m_domain.store();
            } catch (PersistenceException pe) {
                // domain not ready to store yet.
            }
        }
    }

    /**
     * Set the domain for this field without storing it.
     *
     * @param domain a <code>FieldDomain</code> which applies to this Form Field.
     */
    void setDomainNoStore(FieldDomain domain) {
        m_domain = domain;
    }


    /**
     * @return the domain for this field.
     */
    public FieldDomain getDomain() {
        return m_domain;
    }


    /**
     * Does this field support domain lists. Subclasses should override this
     * method to return true if they support domain lists.
     *
     * @return true if this field has a domain list, false otherwise.
     */
    public boolean hasDomain() {
        if ((m_domain != null) && (m_domain.getID() != null))
            return true;
        else
            return false;
    }

    public void setValueRequired(boolean valueRequired) {
        isValueRequired = valueRequired;
    }

    /**
     * Indicates whether this form field requires a value.
     * @return true if a value is required; false otherwise
     */
    public boolean isValueRequired() {
        return this.isValueRequired;
    }

    /** Has this FormField been loaded from database persistence. */
    public boolean isLoaded() {
        return m_isLoaded;
    }

    public void setLoaded(boolean isLoaded) {
        this.m_isLoaded = isLoaded;
    }

    /**
     * Store form field into the database. Create a new form field in the
     * database if needed, otherwise update the existing form field in the
     * database.
     */
    public void store() throws PersistenceException {

        FormFieldProperty property;
        String query_string;
        int i;
        int num_properties;
        // Get the time now for writing to crc fields.  It is a timestamp to
        // facilitate comparison with values read from the database.
        java.sql.Timestamp newCrc = new java.sql.Timestamp(new java.util.Date().getTime());

        if ((m_class_id == null) || m_class_id.equals(""))
            throw new NullPointerException("class_id is null, can't store");

        try {

            db.setAutoCommit(false);

            // Execute an Insert or Update based on whether we have a current
            // field ID
            
            //Avinash: --------------------------------------------------------------------
            if ((m_field_id == null) || m_field_id.equals("") || m_field_id.equals("null") ) {
           //Avinash: --------------------------------------------------------------------- 	

            	// We are INSERTING a new field
            	
                // get new id and register in the pn_object table.
                m_field_id = ObjectManager.dbCreateObjectWithPermissions(ObjectType.FORM_FIELD, "A", m_form.getSpace().getID(), m_form.getUser().getID());

                query_string = "insert into pn_class_field (class_id, field_id, space_id, element_id, field_label, data_table_name, data_column_name, " +
                    "data_column_size, data_column_scale, data_column_exists, row_num, row_span, column_num, column_span, field_group, instructions_clob, is_multi_select, use_default, column_id, is_value_required, hidden_for_eaf ";

                if (hasDomain())
                    query_string += " , domain_id";

                query_string += " , crc, record_status, max_value, min_value, default_value) " +
                    "values (" + m_class_id + "," + m_field_id + "," + m_form.m_space.getID() + ", " + m_element_id +
                    "," + DBFormat.varchar2(m_field_label) + "," + DBFormat.varchar2(m_data_table_name) +
                    "," + DBFormat.varchar2(m_data_column_name) + "," + m_data_column_size + "," + m_data_column_scale + "," + DBFormat.bool(m_data_column_exists) + ", " + m_row_num + "," + m_row_span +
                    "," + m_column_num + "," + m_column_span + "," + DBFormat.varchar2(m_field_group) + ", empty_clob() " +
                    "," + DBFormat.bool(m_isMultiSelect) + "," + DBFormat.bool(m_useDefault) + "," + DBFormat.number(m_column_id) + "," + DBFormat.bool(isValueRequired)+
                    ", " + DBFormat.bool(hiddenForEaf) ;

                if (hasDomain())
                    query_string += "," + m_domain.getID();

                query_string += ", " + DBFormat.crc(newCrc) + ", 'P'," + DBFormat.varchar2(m_max_value) + "," + DBFormat.varchar2(m_min_value) + "," + DBFormat.varchar2(m_default_value) + ")";

                db.setQuery(query_string);
                db.executeQuery();

                // After successful insert, set new crc
                m_crc = newCrc;

            } else {

                // We are UPDATING a field

                StringBuffer sb = new StringBuffer();
                StringBuffer lockQuery = new StringBuffer();
                java.sql.Timestamp crc;                  // Crc of record in database

                sb.append("update pn_class_field set");
                sb.append(" element_id=" + m_element_id);
                sb.append(", field_label=" + DBFormat.varchar2(m_field_label));
                sb.append(", data_table_name=" + DBFormat.varchar2(m_data_table_name));
                sb.append(", data_column_name=" + DBFormat.varchar2(m_data_column_name));
                sb.append(", data_column_size=" + m_data_column_size);
                sb.append(", data_column_scale=" + m_data_column_scale);
                sb.append(", data_column_exists=" + DBFormat.bool(m_data_column_exists));
                sb.append(", row_num=" + m_row_num);
                sb.append(", row_span=" + m_row_span);
                sb.append(", column_num=" + m_column_num);
                sb.append(", column_span=" + m_column_span);
                sb.append(", field_group=" + DBFormat.varchar2(m_field_group));
                sb.append(", instructions_clob = empty_clob() ");
                sb.append(", is_multi_select=" + DBFormat.bool(m_isMultiSelect));
                sb.append(", use_default=" + DBFormat.bool(m_useDefault));
                sb.append(", is_value_required=" + DBFormat.bool(isValueRequired));
                sb.append(", hidden_for_eaf=" + DBFormat.bool(hiddenForEaf));
                if (hasDomain()) {
                    sb.append(", domain_id=" + m_domain.getID());
                }
                sb.append(", max_value=" + DBFormat.varchar2(m_max_value));
                sb.append(", min_value=" + DBFormat.varchar2(m_min_value));
                sb.append(", default_value=" + DBFormat.varchar2(m_default_value));
                sb.append(", column_id=" + DBFormat.number(m_column_id));
                sb.append(", crc = " + DBFormat.crc(newCrc));
                sb.append(" where class_id=" + m_class_id);
                sb.append(" and field_id=" + m_field_id);
                sb.append(" and space_id=" + m_form.m_space.getID());

                lockQuery.append("select crc from pn_class_field ");
                lockQuery.append("where class_id = " + m_class_id + " ");
                lockQuery.append("and field_id = " + m_field_id + " ");
                lockQuery.append("and space_id = " + m_form.m_space.getID() + " ");
                lockQuery.append("for update nowait ");

                try {
                    db.executeQuery(lockQuery.toString());

                    // Now get and check crc of locked record.  It must match the crc of
                    // this form field object in order to continue. Comparison based
                    // on getTime() due to differences of
                    if (db.result.next()) {
                        // We got a row from the query
                        crc = db.result.getTimestamp("crc");
                        if (!crc.equals(m_crc)) {
                        	Logger.getLogger(FormField.class).debug("FormField.store database record conflict.  My crc: " + m_crc + ", database crc: " + crc);
                            throw new PersistenceException("Form field has been modified by another user");
                        }

                    } else {
                        // There is a problem if we got no rows from the query
                    	Logger.getLogger(FormField.class).error("FormField.store failed to lock form field. No rows found with query " + lockQuery.toString());
                        throw new PersistenceException("Error storing form field");
                    }

                    db.closeStatement();
                    db.closeResultSet();

                } catch (SQLException sqle) {
                	Logger.getLogger(FormField.class).error("FormField.store error locking form field with query " + lockQuery.toString() + sqle);
                    throw new PersistenceException("Error storing form field", sqle);

                } //end try

                // At this point, the pn_class_field record is locked

                // Now update record
                db.setQuery(sb.toString());
                db.executeQuery();

                // After successful insert, set new crc
                m_crc = newCrc;

            }

            // At this point, the current row in PN_CLASS_FIELD is locked

            // Now stream the instructions
            // First we have to select the newly inserted row in order to
            // get the clob locater for the empty_clob() that was inserted
            // by the previous statement
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("select instructions_clob from pn_class_field ");
            selectQuery.append("where space_id = ? and class_id = ? and field_id = ? ");
            selectQuery.append("for update nowait");

            int index = 0;
            db.prepareStatement(selectQuery.toString());
            db.pstmt.setString(++index, m_form.m_space.getID());
            db.pstmt.setString(++index, m_class_id);
            db.pstmt.setString(++index, m_field_id);
            db.executePrepared();

            if (db.result.next()) {
                ClobHelper.write(db.result.getClob("instructions_clob"), m_instructions);

            } else {
                throw new PersistenceException("Error updating instructions clob.  Inserted or updated field record not found.");

            }

            // COMMIT all inserts and updates
            // THis will release any locks
            db.commit();

            // FIELD PROPERTIES
            // save all the properties for this field.
            num_properties = m_properties.size();

            for (i = 0; i < num_properties; i++) {
                property = (FormFieldProperty)m_properties.get(i);

                query_string = "select property from pn_class_field_property" +
                    " where class_id=" + m_class_id + " and field_id=" + m_field_id + " and client_type_id=" + m_client_type_id + " and property=" + DBFormat.varchar2(property.m_name);

                db.release();
                db.setQuery(query_string);
                db.executeQuery();

                // If we are INSERTING a new property
                if (!db.result.next()) {
                    //System.out.println("%%%%%%%%%%%%%%%%% Inserting Property :" + property.m_name + " : " + property.m_value);
                    query_string = "insert into pn_class_field_property (class_id, field_id, client_type_id, property_type, property, value) " +
                        "values (" + m_class_id + "," + m_field_id + "," + m_client_type_id + "," + DBFormat.varchar2(property.m_type) + "," +
                        DBFormat.varchar2(property.m_name) + "," + DBFormat.varchar2(property.m_value) + ")";

                    property_db.setQuery(query_string);
                    property_db.executeQuery();
                } else {
                    // UPDATING and existing property
                    //System.out.println("%%%%%%%%%%%%%%%%% Updating Property :" + property.m_name);
                    // insert failed, must already exist.  Try update.
                    query_string = "update pn_class_field_property set property_type=" + DBFormat.varchar2(property.m_type) + ", value=" + DBFormat.varchar2(property.m_value) +
                        " where class_id=" + m_class_id + " and field_id=" + m_field_id + " and client_type_id=" + m_client_type_id + " and property=" + DBFormat.varchar2(property.m_name);

                    property_db.setQuery(query_string);
                    property_db.executeQuery();
                }
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FormField.class).error("FormField.store failed " + sqle);
            throw new PersistenceException("Error occured while trying to store form field: " + sqle, sqle);

        } finally {
            if (db.connection != null) {
                try {
                    db.connection.rollback();
                } catch (SQLException sqle2) {
                    // Can do nothing except release()
                }
            }
            db.release();
            property_db.release();
        }

    }


    /**
     * Load the field from the database. All field definition and properties
     * will be loaded.
     */
    public void load() throws PersistenceException {

        String domainID = null;
        String query_string;

        // select pn_class_field info
        query_string = "select e.element_id, e.element_name, e.db_field_datatype, cf.field_label, cf.data_table_name, cf.data_column_name, " +
            "cf.data_column_size, cf.data_column_scale, cf.data_column_exists, cf.row_num, cf.row_span, cf.column_num, cf.column_span, cf.field_group, cf.domain_id," +
            "cf.max_value, cf.min_value, cf.default_value, cf.instructions_clob, e.element_label, edc.class_id, cf.is_multi_select, cf.use_default, cf.column_id, " +
            "cf.crc, cf.is_value_required, cf.hidden_for_eaf " +
            "from pn_class_field cf, pn_element e, pn_element_display_class edc where cf.class_id=" + m_class_id + " and cf.field_id=" + m_field_id +
            "and e.element_id = cf.element_id and edc.element_id = cf.element_id";

        try {
            db.setQuery(query_string);
            db.executeQuery();

            if (db.result.next()) {
                m_element_id = db.result.getString("element_id");
                m_element_name = db.result.getString("element_name");
                m_db_datatype = db.result.getString("db_field_datatype");
                m_field_label = db.result.getString("field_label");
                m_data_table_name = db.result.getString("data_table_name");
                m_data_column_name = db.result.getString("data_column_name");
                m_data_column_size = db.result.getInt("data_column_size");
                m_data_column_scale = db.result.getInt("data_column_scale");
                m_data_column_exists = db.result.getBoolean("data_column_exists");
                m_row_num = db.result.getInt("row_num");
                m_row_span = db.result.getInt("row_span");
                m_column_num = db.result.getInt("column_num");
                m_column_span = db.result.getInt("column_span");
                m_field_group = db.result.getString("field_group");
                domainID = db.result.getString("domain_id");
                m_max_value = db.result.getString("max_value");
                m_min_value = db.result.getString("min_value");
                m_default_value = db.result.getString("default_value");
                m_instructions = ClobHelper.read(db.result.getClob("instructions_clob"));
                m_element_label = db.result.getString("element_label");
                m_elementDisplayClassID = db.result.getString("class_id");
                m_isMultiSelect = Conversion.toBool(db.result.getInt("is_multi_select"));
                m_useDefault = Conversion.toBool(db.result.getInt("use_default"));
                m_column_id = db.result.getString("column_id");
                m_crc = db.result.getTimestamp("crc");
                isValueRequired = Conversion.toBoolean(db.result.getInt("is_value_required"));
                hiddenForEaf = Conversion.toBoolean(db.result.getInt("hidden_for_eaf"));                
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FormField.class).error("FormField.load failed " + sqle);
            throw new PersistenceException("Error occured while trying to load form field", sqle);
        } finally {
            db.release();
        }

        // get the field properties (& tag)
        this.loadProperties();

        // get the domain values
        if (domainID != null) {
            m_domain = new FieldDomain(m_form);
            m_domain.setField(this);
            m_domain.setID(domainID);
            m_domain.load();
        }
        m_isLoaded = true;
    }


    /**
     * Soft delete this field.
     */
    public void remove() {
        try {
            db.setQuery("update pn_class_field set record_status='D' where class_id=" + m_class_id + " and field_id=" + m_field_id);
            db.executeQuery();
            m_isLoaded = false;
        } catch (SQLException sqle) {
        	Logger.getLogger(FormField.class).error("FormField.remove failed " + sqle);
        } finally {
            db.release();
        }
    }

    
    /**
     * Hide this field for EAF form.
     */
    public void hideField() {
        try {
            db.setQuery("update pn_class_field set hidden_for_eaf = " + DBFormat.bool(!hiddenForEaf) + "  where class_id=" + m_class_id + " and field_id=" + m_field_id);
            db.executeQuery();
            m_isLoaded = false;
        } catch (SQLException sqle) {
        	Logger.getLogger(FormField.class).error("FormField.hide failed " + sqle);
        } finally {
            db.release();
        }
    }    
    
    /**
     * Activate field - used in case of some special non-designable fields
     * when they are added after form is activated (eg. FormID field)
     */
    public void activate() {
        try {
            db.setQuery("update pn_class_field set record_status='A' where class_id=" + m_class_id + " and field_id=" + m_field_id);
            db.executeQuery();
            m_isLoaded = false;
        } catch (SQLException sqle) {
        	Logger.getLogger(FormField.class).error("FormField.activate failed " + sqle);
        } finally {
            db.release();
        }
    }    

    /**
     * Load all the properties for the FormField from the database.
     */
    public void loadProperties() {
        String query_string;
        FormFieldProperty property;
        String name;
        String value;
        String property_type;


        if (m_properties == null) {
            m_properties = new ArrayList();
        }

        // get the ClassField properties
        // WARNING:  This query MUST use the property_db database bean so it will not conflict with the db database bean in the outer calling loop.  -Roger
        query_string = "select property, property_type, value from pn_class_field_property where class_id=" + m_class_id + " and field_id=" + m_field_id + " and client_type_id=" + m_client_type_id;
        try {
            property_db.setQuery(query_string);
            property_db.executeQuery();

            // nothing to do if there are no properties for this field.
            if (!property_db.result.next())
                return;

            do {
                name = property_db.result.getString(1);
                property_type = property_db.result.getString(2);
                value = property_db.result.getString(3);
                property = new FormFieldProperty(name, value, property_type);
                m_properties.add(property);
            } while (property_db.result.next());
        } catch (SQLException sqle) {
        	Logger.getLogger(FormField.class).error("FormField.loadProperties failed " + sqle);
        } finally {
            property_db.release();
        }
    }

    /**
     * Clear the properties of this form field.
     * <p/>
     * The Form, User, Space context
     * will not be cleared, but all other field properties will be.
     */
    public void clear() {
        m_domain = null;
        m_properties.clear();
        m_class_id = null;
        m_field_id = null;
        m_client_type_id = Form.HTML_CLIENT;
        m_db_datatype = null;
        m_element_id = FormField.TEXT;     // Default field type is text.
        m_elementDisplayClassID = FormField.TEXT;
        m_element_name = null;
        m_element_label = null;
        m_field_label = null;
        m_tag = null;
        m_data_table_name = null;
        m_data_column_name = null;
        m_data_column_size = -1;
        m_data_column_scale = 0;
        m_data_column_exists = false;
        m_field_group = null;
        m_row_num = -1;
        m_column_num = -1;
        m_row_span = -1;
        m_column_span = -1;
        m_max_value = null;
        m_min_value = null;
        m_default_value = null;
        m_instructions = null;
        m_crc = null;
        isValueRequired = false;
        hiddenForEaf = false;
        //m_sort_ascending = true;
    }

    /**
     * Converts the objects properties to XML representation without the
     * FormField node or XML version tags. Sub-classes should override this with
     * an implementation that includes this XML as well as class-specific
     * properties (such as domain values). This method returns the object as XML
     * text.
     *
     * @return XML representation
     */
    public String getXMLProperties() {
        StringBuffer xml = new StringBuffer();

        xml.append("<id>" + m_field_id + "</id>\n");
        xml.append("<datatype>" + XMLUtils.escape(m_db_datatype) + "</datatype>\n");
        xml.append("<data_table_name>" + XMLUtils.escape(m_data_table_name) + "</data_table_name>\n");
        xml.append("<data_column_name>" + XMLUtils.escape(m_data_column_name) + "</data_column_name>\n");
        xml.append("<element_id>" + m_element_id + "</element_id>\n");
        xml.append("<element_name>" + XMLUtils.escape(m_element_name) + "</element_name>\n");
        xml.append("<element_label>" + XMLUtils.escape(m_element_label) + "</element_label>\n");
        xml.append("<element_display_class_id>" + m_elementDisplayClassID + "</element_display_class_id>\n");
        xml.append("<label>" + XMLUtils.escape(m_field_label) + "</label>\n");
        xml.append("<field_group>" + XMLUtils.escape(m_field_group) + "</field_group>\n");
        xml.append("<row_num>" + m_row_num + "</row_num>\n");
        xml.append("<column_num>" + m_column_num + "</column_num>\n");
        xml.append("<column_id>" + m_column_id + "</column_id>\n");
        if (m_column_id != null) {
            xml.append("<column_name>");
            if (m_column_id.equals(Form.RIGHT_COLUMN)) {
                xml.append(PropertyProvider.get("prm.global.form.elementproperty.column.domain.right"));
            } else if (m_column_id.equals(Form.LEFT_COLUMN)) {
                xml.append(PropertyProvider.get("prm.global.form.elementproperty.column.domain.left"));
            } else if (m_column_id.equals(Form.BOTH_COLUMNS)) {
                xml.append(PropertyProvider.get("prm.global.form.elementproperty.column.domain.both"));
            }
            xml.append("</column_name>\n");
        }
        xml.append("<row_span>" + m_row_span + "</row_span>\n");
        xml.append("<column_span>" + m_column_span + "</column_span>\n");
        xml.append("<max_value>" + XMLUtils.escape(m_max_value) + "</max_value>\n");
        xml.append("<min_value>" + XMLUtils.escape(m_min_value) + "</min_value>\n");
        xml.append("<default_value>" + XMLUtils.escape(m_default_value) + "</default_value>\n");
        xml.append("<required>" + isValueRequired + "</required>");
        xml.append("<designable>" + isDesignable()+ "</designable>");
        xml.append("<hiddenForEaf>" + isHiddenForEaf()+ "</hiddenForEaf>");
        xml.append("<support_external_access>" + getForm().getSupportsExternalAccess() + "</support_external_access>" );

        // get the domain for this field, if it has one.
        if (m_domain != null)
            m_domain.getXML();

        return xml.toString();
    }


    /**
     * Converts the object to XML node representation without the XML version
     * tag. This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {
        return "<FormField>\n" + getXMLProperties() + "</FormField>\n";
    }


    /**
     * Converts the object to XML representation. This method returns the object
     * as XML text.
     *
     * @return XML representation
     */
    public String getXML() {
        return ("<?xml version=\"1.0\" ?>\n" + getXMLBody());
    }

    /**
     * Validates that the specified FieldData contains numbers.
     *
     * @param data the field data to validate
     * @param errorMessagePatternBuffer the buffer into which the error message
     * pattern is written
     * @return true if all values in the field data are numbers; false otherwise
     */
    public boolean isValidFieldData(FieldData data, StringBuffer errorValue, StringBuffer errorMessagePatternBuffer) {
        return true;
    }


    /**
     * Process the field data to the correct internal format after receiving from
     * http post.  This allows a form field to store the data in any way it
     * chooses.<br>
     * The default implementation of this method does nothing to the data.
     *
     * @param data the field data to process
     */
    public void processFieldData(FieldData data) {
        // Default : do nothing
    }

    /**
     * Process the specified value for insertion into persistence storage.
     * The Default behavior is to return <code>"NULL"</code> if the value is null;
     * otherwise the value formatted as a number
     * Most sub-classes will want to override this to format the value correctly
     * for insertion.
     *
     * @return <code>"NULL"</code> if the value is null or the value formatted
     * as a number.
     */
    public String processDataValueForInsert(String value) {
        if (value == null) {
            return "NULL";
        } else {
            return net.project.database.DBFormat.number(value);
        }
    }

    /**
     * Return an SQL representation of the field filter to be used in a WHERE
     * clause.
     *
     * @param filter the filter values to use in generating the SQL.
     * @param joinOperator String containing the boolean operator to join this
     * field filter to the preceeding filter. "and" or "or".
     */
    public String getFilterSQL(FieldFilter filter, String joinOperator) {
        FieldFilterConstraint constraint;
        int num_constraints;
        int num_values;
        StringBuffer sb = new StringBuffer();

        sb.append(" " + joinOperator + " (");

        // For each constraint
        num_constraints = filter.m_constraints.size();
        for (int i = 0; i < num_constraints; i++) {
            constraint = (FieldFilterConstraint)filter.m_constraints.get(i);
            num_values = constraint.size();

            // For each constraint value
            for (int j = 0; j < num_values; j++) {
                if ((constraint.get(j) != null) && !(constraint.get(j)).equals("")) {
                    if (j != 0)
                        sb.append(" or ");
                    sb.append("(");
                    
                    if(StringUtils.isNotEmpty(this.getFormTableName()))
                    	sb.append(this.getFormTableName() +".");
                    
                    sb.append(this.getDataColumnName() + " " + constraint.getOperator() + " " + constraint.get(j) + ")");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Formats the field data and returns it in a form suitable for using
     * in a CSV file.
     * @param fieldData the field data to be formatted and returned.
     * @return a comma separated list representation of the field_data formatted correctly for this type of field.
     */
    public String formatFieldDataCSV(FieldData fieldData) {
        if (formatFieldData(fieldData) != null)
            return ("\"" + ParseString.escapeDoubleQuotes(formatFieldData(fieldData)) + "\"");
        else
            return "";
    }

    /**
     * Formats the field data and returns it as a string suitable for using
     * in the list view.
     * @param fieldData the field data to be formatted and returned.
     * @return a string representation of the field data formatted correctly for this type of field.
     */
    public String formatFieldDataListView(FieldData fieldData) {
        return formatFieldData(fieldData);
    }

    /**
     * Formats the field data.
     * @param fieldData the fieldData to format
     * @return the formatted field data
     */
    public abstract String formatFieldData(FieldData fieldData);


    /**
     * Generic field data comparison for single-value text fields. Specialized
     * form fields should overload this method. Compares the two field's first
     * data element only. Lexicographic compare is used.
     *
     * @return 0 if equal, -1 if data1 < data2, 1 if data1 > data2, per contract
     * of java.util.Comparator.
     * @see java.util.Comparator
     * @deprecated As of 7.7.0; No replacement.  This method is never called and its
     * implementation cannot be certified.  It may be removed in a future release.
     */
    public int compareData(String data1, String data2) {
        if ((data1 != null) && (data2 != null)) {
            // From String.compareTo() Javadoc:
            // the value 0 if the argument is a string lexicographically equal to this string;
            // a value less than 0 if the argument is a string lexicographically greater than this string;
            // and a value greater than 0 if the argument is a string lexicographically less than this string.

            return data1.toLowerCase().compareTo(data2.toLowerCase());
        } else {
            // null fields are considered "greater than" defined fields.
            if (data1 == null)
                return 1;
            else
                return -1;
        }
    }

    /**
     * Generic field data comparison for single-value text fields. Specialized
     * form fields should overload this method. Compares the two field's first
     * data element only. Lexicographic compare is used.
     *
     * @return 0 if equal, -1 if data1 < data2, 1 if data1 > data2, per contract
     * of java.util.Comparator.
     * @see java.util.Comparator
     */
    public int compareData(FieldData data1, FieldData data2) {
        if ((data1.get(0) != null) && (data2.get(0) != null)) {
            // From String.compareTo() Javadoc:
            // the value 0 if the argument is a string lexicographically equal to this string;
            // a value less than 0 if the argument is a string lexicographically greater than this string;
            // and a value greater than 0 if the argument is a string lexicographically less than this string.
            return ((String)data1.get(0)).toLowerCase().compareTo(((String)data2.get(0)).toLowerCase());
        } else {
            // null fields are considered "greater than" defined fields.
            if (data1.get(0) == null)
                return 1;
            else
                return -1;
        }
    }

    public void writeHtmlReadOnly(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        System.out.println("......... Inside FormField.writeHtmlReadOnly().... The field must override this method ");
        // This must be implemented by each of the Field subclasses
    }

    public String writeHtmlReadOnly(FieldData field_data)  {
    	System.out.println("......... Inside FormField.writeHtmlReadOnly().... The field must override this method ");
    	//	This must be implemented by each of the Field subclasses
    	return "";
}    
    

    /**
     * Default method for getting the data for the field from the HTTP post.
     * Will get 1-N values for a single form field. Fields that define multiple
     * http form fields will need to override this method.
     */
    public void processHttpPost(javax.servlet.ServletRequest request, FieldData fieldData) {
        //sjmittal: this check is done so as to prevent excpetions in tomcat as I think
        //they don't allow null has keys in its parameter map implementation.
        if(this.m_data_column_name == null)
            return;
        
        String[] values;

        // Add all parameter values to field data
        values = request.getParameterValues(this.m_data_column_name);
        if (values != null && values.length > 0) {
            for (int val = 0; val < values.length; val++) {
            	if(this instanceof CheckboxField){
            		fieldData.add("1");
            	}else {
            		fieldData.add(values[val]);
            	}
            }
        } else if(this instanceof CheckboxField){
        	fieldData.add("0");
        }
    }

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     *
     * @return a <code>String</code> value which will become the value="?" part
     * of the option tag.
     */
    public String getHtmlOptionValue() {
        return getID();
    }

    /**
     * Returns the value for the content part of the HTML option.
     *
     * @return a <code>String</code> value that will be displayed for this
     * html option.
     */
    public String getHtmlOptionDisplay() {
        return getFieldLabel();
    }

    /**
     * Helper method to return Javascipt code for indicating a field is required.
     * <p/>
     * Sub-classes may call this from <code>writeHtml</code> to generate the function call
     * to indicate a field is required.  The view page to which the HTML is written is
     * expected to implement the function.
     * @return Javascript code of the form <code>&lt;script&gt;addRequiredField('<i>columnname</i>', '<i>message</i>');&lt;/script&gt;</code>
     */
    protected String getRequiredValueJavascript() {
        //Looks something like <script language="JavaScript">fieldsNames["companyName"]="Project.net, Inc"</script>
        //This inserts the name of the form field into an associative array so 
        return "<script language=\"JavaScript\">fieldNames[\""+m_data_column_name+"\"]=" +
            "\""+m_field_label+"\"</script>";
    }

    
    /**
     * Is field visible on form design
     * By default all fields are designable
     * Subclass of FormField can override this property, if required
     */    
    public boolean isDesignable(){
    	return true;
    }

    /**
     * Is field hidden for external non-pnet users
     * Subclass of FormField can override this property, if required
     */    
	public boolean isHiddenForEaf() {
		return hiddenForEaf;
	}

	public void setHiddenForEaf(boolean hiddenForEaf) {
		this.hiddenForEaf = hiddenForEaf;
	}

	/**
	 * @return the formTableName
	 */
	public String getFormTableName() {
		return formTableName;
	}

	/**
	 * @param formTableName the formTableName to set
	 */
	public void setFormTableName(String formTableName) {
		this.formTableName = formTableName;
	}
    
    
}
