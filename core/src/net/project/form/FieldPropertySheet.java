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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18506 $
|       $Date: 2008-12-07 23:11:29 -0200 (dom, 07 dic 2008) $
|     $Author: vivana $
|
+----------------------------------------------------------------------*/
package net.project.form;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;

import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.persistence.PersistenceException;
import net.project.space.Space;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Displays and stores a HTML form property sheet for defining user-defined form
 * (class) fields.
 */
public class FieldPropertySheet extends Form implements net.project.form.property.IPropertySheet {

    /**
     * the field this property sheet represents
     */
    protected FormField m_managedField = null;

    /**
     * the ArrayList of FieldData for the field being managed by this property
     * sheet
     */
    protected ArrayList m_field_data = null;

    /**
     * the list of possible field types (elements) to display a property sheet
     * for
     */
    protected FormElementList m_elementList = null;

    /**
     * Used by loadProperty() method so that the DBBean db is not corrupted.
     * This is a member variable because the loadProperty() method is called
     * multiple times. *
     */
    private DBBean prop_db = new DBBean();

    /**
     * Constructor.
     *
     * @param user the User context.
     * @param space the Space context.
     * @param class_id the class_id of this form.
     */
    public FieldPropertySheet(net.project.security.User user, Space space, String class_id) {
        super(user, space, class_id);
        m_isSequenced = false;
        m_isLoaded = false;
    }

    /**
     * Bean constructor
     */
    public FieldPropertySheet() {
        super();
        m_isSequenced = false;
        m_isLoaded = false;
    }

    /**
     * Set the Form that this property sheet is for.
     */
    public void setForm(Form form) {
        // FieldPropertySheet doesn't care about the form
    }

    /**
     * Set the FormField that this propertysheet is for
     */
    public void setManagedField(FormField field) {
        m_managedField = field;
    }


    /**
     * Get the FormField that this propertysheet is for
     */
    public FormField getManagedField() {
        return m_managedField;
    }


    /**
     * Does this form contain FormFields?
     *
     * @return true if this Form contains as least one FormField, false
     *         otherwise.
     */
    public boolean hasFields() {
        return (m_fields != null && m_fields.size() > 0);
    }


    /**
     * Field propertySheets never have lists.
     *
     * @return false always.
     */
    public boolean hasLists() {
        return false;
    }


    /**
     * Does this field property sheet support domain lists.
     */
    public boolean hasDomain() {
        if (m_class_id.equals(FormField.SINGLE_MENU) || m_class_id.equals(FormField.MULTI_MENU)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * *********************************************************************************************
     * Implementing IJDBCPersistence Interface ************************************************************************************************
     *
     * /** Is this FieldPropertySheet loaded from persistence?
     */
    public boolean isLoaded() {
        return m_isLoaded;
    }


    /**
     * Load the property sheet for the current managed field.
     */
    public void load() throws PersistenceException {

        String query_string = null;

        // New form coming in from the database, clear everything from the old one.
        //clear();

        query_string = "select class_name, class_type_id, class_desc, class_abbreviation, max_row, max_column, owner_space_id, " +
            "methodology_id, master_table_name, data_table_key, is_sequenced from pn_class where class_id=" + m_class_id;

        try {
            db.setQuery(query_string);
            db.executeQuery();

            if (!db.result.next()) {
                throw new PersistenceException("Form could not be loaded from database.  Contact Administrator.");
            }

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

            // get FormFields.  false ==> load only fields with record_status = 'A'
            this.loadFields(false);

            // load the field's data (properties)
            this.loadData();
            m_isLoaded = true;
        } catch (SQLException sqle) {
            m_isLoaded = false;
            Logger.getLogger(FieldPropertySheet.class).error("FieldPropertySheet.load failed " + sqle);
            throw new PersistenceException("failed to load field property sheet", sqle);
        } finally {
            db.release();
        }

    }


    /**
     * Is the passed property stored in the pn_class_field table as a static
     * column?
     */
    private boolean isStaticProperty(String name) {
        if (name.equals("class_id")) {
            return true;
        } else if (name.equals("field_id")) {
            return true;
        } else if (name.equals("element_id")) {
            return true;
        } else if (name.equals("client_type_id")) {
            return true;
        } else if (name.equals("field_label")) {
            return true;
        } else if (name.equals("row_num")) {
            return true;
        } else if (name.equals("row_span")) {
            return true;
        } else if (name.equals("column_id")) {
            return true;
        } else if (name.equals("column_span")) {
            return true;
        } else if (name.equals("field_group")) {
            return true;
        } else if (name.equals("domain_id")) {
            return true;
        } else if (name.equals("data_column_size")) {
            return true;
        } else if (name.equals("data_column_scale")) {
            return true;
        } else if (name.equals("instructions_clob")) {
            return true;
        } else if (name.equals("use_default")) {
            return true;
        } else if (name.equals("is_multi_select")) {
            return true;
        } else if (name.equals("is_value_required")) {
            return true;
        } else if (name.equals("hidden_for_eaf")) {
            return true;            
        } else {
            return false;
        }
    }

    /**
     * Indicates whether the specified static property is actually stored as a
     * clob.
     *
     * @param name the name of the static property
     * @return true if the property is a clob; false otherwise
     */
    private boolean isStaticPropertyClob(String name) {
        if (name.equals("instructions_clob")) {
            return true;
        }
        return false;
    }

    /**
     * Get the data from the database for this fieldPropertySheet.
     *
     * @throws PersistenceException if no fields are available
     */
    public void loadData() throws PersistenceException {

        FormField field = null;
        FieldData field_data = null;
        FormFieldProperty fieldProperty = null;
        int i;
        int staticFieldCnt = 0;
        int num_fields;

        if ((m_managedField == null) || (m_managedField.getID() == null)) {
            return;
        }

        if ((num_fields = m_fields.size()) < 1) {
            throw new PersistenceException("No fields defined for this form.");
        }

        /*
            For the managed field:
            1. get the field properties that are stored as static columns in the pn_class_field table
            2. get properties from the pn_class_field_property table for other property sheet fields.
         */

        // Save the data in memory. Data for the fields of one form.
        m_field_data = new ArrayList(num_fields);

        // Select statement of the form select field_id, row_num, row_span, instruction_clob, etc.
        // It selects all the "static" fields since these are available in
        // the table "pn_class_field"
        String selectStatement = "select field_id";
        for (i = 0; i < num_fields; i++) {
            field = (FormField) m_fields.get(i);

            if (isStaticProperty(field.m_data_column_name)) {
                selectStatement = selectStatement + "," + field.m_data_column_name;
            }
        }

        // The from string
        String from_string = " from pn_class_field ";

        // The where string
        String where_string = " where field_id=" + m_managedField.getID();

        try {
            db.setQuery(selectStatement + from_string + where_string);
            db.executeQuery();

            if (!db.result.next()) {
                m_isLoaded = false;
                return;
            }

            // now fill the data array from both the static and dynamic properties.
            m_data.setID(m_managedField.getID());
            staticFieldCnt = 2;  // skip field_id

            for (i = 0; i < num_fields; i++) {
                field = (FormField) m_fields.get(i);
                field_data = new FieldData(1);

                if (isStaticProperty(field.m_data_column_name)) {

                    if (isStaticPropertyClob(field.m_data_column_name)) {
                        // Read from the clob column
                        field_data.add(ClobHelper.read(db.result.getClob(staticFieldCnt++)));
                    } else {
                        // Read as a string
                        field_data.add(db.result.getString(staticFieldCnt++));
                    }

                } else {
                    // get the property and add to field_data.
                    fieldProperty = loadProperty(m_managedField.getID(), field.getDataColumnName());
                    if (fieldProperty != null) {
                        field_data.add(fieldProperty.getValue());
                    }
                }

                // Add the field data to the field data list.
                m_field_data.add(i, field_data);
                // Add the field data to the FormData.
                m_data.put(field.getSQLName(), field_data);
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("failed to load data in field property sheet: " + sqle, sqle);

        } finally {
            db.release();

        }

    }


    /**
     * Load and return a  property for the FormField from the database.
     */
    public FormFieldProperty loadProperty(String fieldID, String property)
        throws PersistenceException {

        if ((fieldID == null) || (property == null)) {
            throw new PersistenceException("fieldID or property is null, can't load.");
        }

        String query_string = "select property, property_type, value from pn_class_field_property where field_id=" + fieldID +
            " and property=" + DBFormat.varchar2(property) + " and client_type_id=" + HTML_CLIENT;

        try {
            prop_db.setQuery(query_string);
            prop_db.executeQuery();

            // this is OK, the designing user may have changed the field type (element type) of this field before applying.
            if (!prop_db.result.next()) {
                //System.out.println("FormField Property Not found, field_id: " + fieldID + ", property: " + property);
                //throw new PersistenceException("FormField Property Not found, field_id: " + fieldID + ", property: " + property);
                return null;
            } else {
                return new FormFieldProperty(prop_db.result.getString(1), prop_db.result.getString(3), prop_db.result.getString(2));
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(FieldPropertySheet.class).error("FieldPropertySheet.FormFieldProperty failed " + sqle);
            throw new PersistenceException("failed to load property", sqle);

        } finally {
            prop_db.release();
        }
    }


    /**
     * Store the data for a fieldPropertySheet.
     */
    public void store()
        throws PersistenceException {
        m_managedField.store();
    }


    /**
     * Soft delete the field.
     */
    public void remove() throws PersistenceException {
        throw new PersistenceException("Removal of property sheets not supported");
    }

    public int getDesignerFieldCount() {
        return m_fields.size();
    }

    /**
     * Generates an HTML form described by the class_id.  Fills in the form with
     * data if the data_object_id exists.
     *
     * @param out the stream to write the generated HTML to.
     */
    public void writeHtml(java.io.PrintWriter out) throws java.io.IOException {

        FormField field;
        FieldData field_data;
        int r, c;
        int field_cnt;
        int current_row;
        int current_column;
        int num_fields;

        // System.out.println("Form.writeHtml():  class= " + m_class_id + "   data_object_id= " + m_data.m_data_object_id);

        if (m_class_id == null) {
            throw new NullPointerException("m_class_id is null");
        }

        if (m_fields == null || (num_fields = m_fields.size()) < 1) {
            throw new NullPointerException("m_fields is null or empty list.");
        }

        // Render the form's HTML.
        out.print("<!-- Begin rendered form -->\n");
        out.print("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n");

        current_row = 0;
        current_column = 0;

        // For each field on the form.  Fields are sorted by row, column
        for (field_cnt = 0; field_cnt < num_fields; field_cnt++) {
            field = (FormField) m_fields.get(field_cnt);
            if (!field.hiddenForEaf || (field.hiddenForEaf && m_managedField.getForm().getSupportsExternalAccess())){
	            if (m_data != null) {
	                field_data = (FieldData) m_data.get(field.getSQLName());
	            } else {
	                field_data = null;
	            }
	
	            // Assumes four HTML table columns (2 user columns) max allowed.
	            // User column = field label in column i plus input element in column i+1
	
	            // NEW TABLE ROW
	            if (field.m_row_num > current_row) {
	                // if not the first row.
	                if (field_cnt != 0) {
	                    // finish padding out current row with empy columns
	                    for (c = current_column; c < field.m_column_num; c++) {
	                        out.print("<td>&nbsp;</td>\n<td>&nbsp;</td>\n");
	                    }
	
	                    out.print("</tr>\n");
	                }
	
	                // leave blank rows if needed.  Fields can skip rows for spacing.
	                for (r = current_row; r < field.m_row_num - 1; r++) {
	                    out.print("<tr><td colspan=\"" + m_max_column * 2 + "\">&nbsp;</td></tr>\n");
	                }
	
	                out.print("\n<tr class=\"tableHeader\">\n");
	                current_row = field.m_row_num;
	                current_column = 1;
	            }
	
	            // indent to the desired column
	            for (c = current_column; c < field.m_column_num; c++) {
	                out.print("<td>&nbsp;</td>\n<td>&nbsp;</td>\n");
	            }
	
	            // next available col on this row
	            current_column = current_column + field.m_column_span;
	
	            // RENDER FIELD
	            // Outputs HTML for field label,  field, field data wapped in <td> element
	            field.writeHtml(field_data, out);
            }
        }  // for fields

        out.print("</tr>\n");
        out.print("</table>\n");
        out.print("<!-- End rendered form -->\n");
    }


    /**
     * Get the field definition from an HTTP form post (from the user)
     */
    public void processHttpPost(javax.servlet.ServletRequest request)
        throws FormException {

        Enumeration names;
        String name;
        String value;
        FormFieldProperty property;

        // Temporary columnSize and scale
        int potentialColumnSize = -1;
        int potentialColumnScale = -1;

        /*
        The form is expected to always return the following Hidden fields:
            class_id, field_id, element_id

        The form may return 0 or more of the following standard properties
        which are stored in the class_field table:
            row_num, row_span, column_id, field_group, domain_id, data_column_size,
            instructions_clob, is_multi_select, use_default, dattype
        Other properties are stored in the pn_class_field_property table.
        */

        // checkboxes must be cleared since request only returns value if checked.
        m_managedField.m_isMultiSelect = false;
        m_managedField.m_useDefault = false;


        names = request.getParameterNames();
        while (names.hasMoreElements()) {
            name = (String) names.nextElement();
            value = request.getParameter(name);
            // The FormField member variables being posted by the property sheet.
            if (name.equals("classID")) {
                m_managedField.m_class_id = value;
            } else if (name.equals("fieldID")) {
                m_managedField.m_field_id = value;
            } else if (name.equals("clientTypeID")) {
                m_managedField.m_client_type_id = value;
            } else if (name.equals("elementID")) {
                m_managedField.m_element_id = value;
            } else if (name.equals("elementName")) {
                m_managedField.m_element_name = value;
            } else if (name.equals("elementLabel")) {
                m_managedField.m_element_label = value;
            } else if (name.equals("field_label")) {
                m_managedField.m_field_label = value;
            } else if (name.equals("row_num")) {
                try {
                    m_managedField.m_row_num = Integer.parseInt(value);
                } catch (java.lang.NumberFormatException nfe) {
                    m_managedField.m_row_num = -1;
                }
            } else if (name.equals("row_span")) {
                try {
                    m_managedField.m_row_span = Integer.parseInt(value);
                } catch (java.lang.NumberFormatException nfe) {
                    m_managedField.m_row_span = -1;
                }
            } else if (name.equals("column_id")) {
                if (value.equals(LEFT_COLUMN)) {
                    m_managedField.m_column_num = 1;
                    m_managedField.m_column_span = 1;
                    m_managedField.m_column_id = LEFT_COLUMN;
                } else if (value.equals(RIGHT_COLUMN)) {
                    m_managedField.m_column_num = 2;
                    m_managedField.m_column_span = 1;
                    m_managedField.m_column_id = RIGHT_COLUMN;
                } else if (value.equals(BOTH_COLUMNS)) {
                    m_managedField.m_column_num = 1;
                    m_managedField.m_column_span = 2;
                    m_managedField.m_column_id = BOTH_COLUMNS;
                }
            } else if (name.equals("field_group")) {
                m_managedField.m_field_group = value;
            } else if (name.equals("domain_id")) {
                //m_domain_id = value;
                if ((m_managedField.m_domain != null) && (m_managedField.m_domain.m_domain_id != null) && !m_managedField.m_domain.m_domain_id.equals("")) {
                    m_managedField.m_domain = new FieldDomain(m_managedField.m_form, value);
                } else {
                    m_managedField.m_domain = null;
                }
            } else if (name.equals("data_column_size")) {
                // the # characters of storage the user requested.
                if (Conversion.toInt(value) != -1) {
                    // That means user has specified some value, therefore assign the same to maximum length field.
                    potentialColumnSize = Conversion.toInt(value);

                } else {
                    //Since Max Length is not provided find out if the display length is provided and assign it as the Max Length.
                    String displayLength = request.getParameter("size");
                    if (Conversion.toInt(displayLength) != -1) {
                        // Assign the specified display length as the maximum length
                        // Since user has not specified any value
                        potentialColumnSize = Conversion.toInt(displayLength);
                    } else {
                        // Since the "display length" is also not specified
                        // we assign the default value of 20 to maximum length field.
                        potentialColumnSize = 20;
                    }
                }
            } else if (name.equals("data_column_scale")) {
                // the scale for number fields
                potentialColumnScale = Conversion.toInt(value);
            } else if (name.equals("instructions_clob")) {
                m_managedField.m_instructions = value;
            } else if (name.equals("is_multi_select")) {
                m_managedField.m_isMultiSelect = Conversion.toBoolean(value);
            } else if (name.equals("use_default")) {
                m_managedField.m_useDefault = Conversion.toBoolean(value);
            } else if (name.equals("datatype")) {
                property = new FormFieldProperty(name, value, "system");
                m_managedField.m_properties.add(property);
            } else if (name.equals("is_value_required")) {
                m_managedField.isValueRequired = Conversion.toBoolean(value);
            } else if (name.equals("hidden_for_eaf")) {            	
                m_managedField.hiddenForEaf = Conversion.toBoolean(value);
            }            
            // IGNORE FORM TRASH
            // The following are known name-value pairs returned by the form
            // that we want to ignore so they won't be stored in the properties table.
            else if (name.equals("theAction") || name.equals("ElementID") || name.equals("id") || name.equals("module") || name.equals("action")) {
                // ignore
            } else {
                // PROPERTIES
                // Everything else is property-value pairs for the class_field_property table.
                // insert one at a time; no array inserts for portability.
                property = new FormFieldProperty(name, value, "in_tag");
                m_managedField.m_properties.add(property);
            }
        }
        //fix for bug-5244
        //since is_value_reuquired is checkbox it exist in request param only when
        //checked otherwise it is null
        if (request.getParameter("is_value_required") == null){ 
        	m_managedField.isValueRequired = false;
        }

        if (request.getParameter("hidden_for_eaf") == null){
        	m_managedField.hiddenForEaf = false;
        }        
        
        // CHECK CRITICAL fields
        if ((m_managedField.m_class_id == null) || (m_managedField.m_class_id.equals(""))) {
            throw new FormException("No class_id returned by form post.");
        }

        //
        // Handle Special fields
        //

        // Column Size & Scale cause maxlength property to be added
        // Additional complexity required for determining appropriate max length
        // based on Oracle's size and scale rules
        if (potentialColumnSize != -1) {

            if (m_managedField.dataColumnExists() &&
                (m_managedField.m_data_column_size < potentialColumnSize ||
                m_managedField.m_data_column_scale < potentialColumnScale)) {

                // Field is already in database and user is trying to
                // increase column size or scale;  we don't currently
                // handle this

                // Do nothing

            } else {
                // Field is not already in database, or user is reducing size
                // of column size or scale
                m_managedField.m_data_column_size = potentialColumnSize;
                m_managedField.m_data_column_scale = potentialColumnScale;

            }

            int maxLength = calculateMaxLength(m_managedField.m_data_column_size, m_managedField.m_data_column_scale);
            property = new FormFieldProperty("maxlength", Integer.toString(maxLength), "in_tag");
            m_managedField.m_properties.add(property);
        }

    }

    /**
     * Calculate maxLength property from columnSize and columnScale.
     */
    private int calculateMaxLength(int columnSize, int columnScale) {
        int maxLength = columnSize;

        if (columnScale != -1) {
            if (columnScale > columnSize) {
                // Scale greater than size.  In Oracle terms, that means
                // the scale specifies the max number of digits to right
                // of decimal point
                maxLength = columnScale;
            }

            if (columnScale > 0) {
                // We have a scale.  User may then enter decimal point
                maxLength += 1;
            }
        }

        return maxLength;
    }


    /**
     * Converts the Form to XML representation without the XML version tag. This
     * method returns the From as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer(300);

        try {
            if (!isLoaded()) {
                this.load();
            }
        } catch (PersistenceException pe) {
            System.out.println("Could not load() form. ********** ");
            return "";
        }

        // form list properties
        xml.append("<FieldPropertySheet>\n");
        xml.append("<id>" + m_class_id + "</id>\n");
        xml.append("<class_type_id>" + m_class_type_id + "</class_type_id>\n");
        xml.append("<name>" + XMLUtils.escape(m_class_name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(m_class_description) + "</description>\n");
        xml.append("<abbreviation>" + XMLUtils.escape(m_class_abbreviation) + "</abbreviation>\n");
        xml.append("<master_table_name>" + XMLUtils.escape(m_master_table_name) + "</master_table_name>\n");
        xml.append("<max_row>" + m_max_row + "</max_row>\n");
        xml.append("<max_column>" + m_max_column + "</max_column>\n");

        // FormFields
        for (int i = 0; i < m_fields.size(); i++) {
            xml.append(((FormField) m_fields.get(i)).getXMLBody());
        }

        xml.append("</FieldPropertySheet>\n");
        return xml.toString();
    }


    /**
     * Converts the object to XML representation of the Form including XML
     * subnodes for all FormFields and FormLists. This method returns the Form
     * as XML text.
     *
     * @return XML representation of the form
     */
    public String getXML() {
        return ("<?xml version=\"1.0\" ?>\n" + getXMLBody());
    }


}
