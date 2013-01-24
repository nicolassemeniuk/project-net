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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.form;

import net.project.form.property.IPropertySheet;
import net.project.form.property.PropertySheetFactory;
import net.project.xml.XMLFormatter;

/**
 * Creates and modifies the design of a FormField..
 */
public class FormFieldDesigner extends FormField {
    /** Is this a newly created field that has not been added to the form yet/ */
    protected boolean m_isNewField = false;
    /** Contains XML formatting information and utilities specific to this object. */
    protected XMLFormatter m_formatter;


    /**
     * Sole Constructor.
     */
    public FormFieldDesigner() {
        super();
        m_element_id = FormField.TEXT;      // default field type is single line TEXT.
        m_formatter = new XMLFormatter();
    }


    /**
     * Is this a newly created field that has not been added to the form yet?
     */
    public boolean isNewField() {
        return m_isNewField;
    }


    /**
     * Sets whether this is a newly created field that has not been added to the
     * form yet.
     *
     * @param status true if is a new field not yet on the form, false if the
     * field already exists on the form.
     */
    public void setIsNewField(boolean status) {
        m_isNewField = status;
    }


    /**
     * Set this field to be of the specified type (element)
     */
    public void setElement(FormElement element) {
        if (element == null)
            throw new NullPointerException("can't set element to null");

        m_element_id = element.getID();
        m_element_name = element.getName();
        m_element_label = element.getLabel();
        m_db_datatype = element.getDatatype();
        m_elementDisplayClassID = element.getDisplayClassID();
    }

    /**
     * Returns the Property Sheet used for designing this form field. The
     * Property Sheet's ID is set to the appropriate display class id and the
     * Property Sheet's managed field is set to this field.
     *
     * @return the property sheet
     */
    public IPropertySheet getPropertySheet() {
        IPropertySheet propertySheet = PropertySheetFactory.getPropertySheetForDisplayClass(getElementDisplayClassID());
        propertySheet.setID(getElementDisplayClassID());
        propertySheet.setManagedField(this);
        return propertySheet;
    }

    /**
     * Set the properties of this FormFieldDesigner from an instance of it's
     * base class, FormField.
     */
    public void setFormField(FormField field) {
        this.m_form = field.m_form;
        this.m_domain = field.m_domain;
        this.m_properties = field.m_properties;
        this.m_class_id = field.m_class_id;
        this.m_field_id = field.m_field_id;
        this.m_client_type_id = field.m_client_type_id;
        this.m_db_datatype = field.m_db_datatype;
        this.m_element_id = field.m_element_id;
        this.m_element_name = field.m_element_name;
        this.m_element_label = field.m_element_label;
        this.m_elementDisplayClassID = field.m_elementDisplayClassID;
        this.m_field_label = field.m_field_label;
        this.m_tag = field.m_tag;
        this.m_data_table_name = field.m_data_table_name;
        this.m_data_column_name = field.m_data_column_name;
        this.m_data_column_size = field.m_data_column_size;
        this.m_data_column_scale = field.m_data_column_scale;
        this.m_data_column_exists = field.m_data_column_exists;
        this.m_field_group = field.m_field_group;
        this.m_row_num = field.m_row_num;
        this.m_column_num = field.m_column_num;
        this.m_row_span = field.m_row_span;
        this.m_max_value = field.m_max_value;
        this.m_min_value = field.m_min_value;
        this.m_default_value = field.m_default_value;
        this.m_instructions = field.m_instructions;
        this.m_crc = field.m_crc;
        this.isValueRequired = field.isValueRequired;
        //this.m_sort_ascending = field.m_sort_ascending;
    }


    /**
     * Set the database storage type.
     */
    public void setDBStorageType(String type) {
        m_db_datatype = type;
    }

    /**
     * Get the database storage type.
     */
    public String dbStorageType() {
        return (m_db_datatype);
    }


    /**
     * Not supported by FormFieldDesigner. Needed to extend abtract class.
     * Implemented only by specific field types.
     */
    public void writeHtml(FieldData field_data, java.io.PrintWriter out) throws java.io.IOException {
    }


    /**
     * Not supported by FormFieldDesigner. Needed to extend abtract class.
     * Implemented only by specific field types.
     *
     * @return always returns false.
     */
    public boolean isFilterable() {
        return false;
    }


    /**
     * Not supported by FormFieldDesigner. Needed to extend abtract class.
     * Implemented only by specific field types.
     *
     * @return always returns false.
     */
    public boolean isSearchable() {
        return false;
    }


    /**
     * Not supported by FormFieldDesigner. Needed to extend abtract class.
     * Implemented only by specific field types.
     *
     * @return always returns false.
     */
    public boolean isSortable() {
        return true;
    }


    /**
     * Not supported by FormFieldDesigner. Needed to extend abtract class.
     * Implemented only by specific field types.
     */
    public void writeFilterHtml(FieldFilter filter, java.io.PrintWriter out)
        throws java.io.IOException {
    }


    /**
     * Not supported by FormFieldDesigner. Needed to extend abtract class.
     *
     * @return always returns null.
     */
    public FieldFilter processFilterHttpPost(javax.servlet.ServletRequest request) {
        return null;
    }

    /**
     * Not supported by FormFieldDesigner. Needed to extend abtract class.
     *
     * @return always returns null.
     */
    public String formatFieldData(FieldData field_data) {
        return null;
    }


    /**
     * Not supported by FormFieldDesigner. Needed to extend abtract class.
     *
     * @return always returns null.
     */
    public String formatFieldDataCSV(FieldData field_data) {
        return null;
    }


    /**
     * Not supported by FormFieldDesigner.
     *
     * @return always returns null.
     */
    public String getFilterSQL(FieldFilter filter) {
        return null;
    }


    /********************************************************************************************************************
     *   Designer specific Presentation of XML
     ********************************************************************************************************************/

    /**
     * Sets the stylesheet file name used to render an XML document. This method
     * accepts the name of the stylesheet used to convert the XML representation
     * of an object to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML
     * representation of the object.
     */
    public void setStylesheet(String styleSheetFileName) {
        m_formatter.setStylesheet(styleSheetFileName);
    }


    /**
     * Gets the presentation of the Form This method will apply the stylesheet
     * to the XML representation of the Form and return the resulting text.
     *
     * @return presetation of the Form
     */
    public String getPresentation() {
        return m_formatter.getPresentation(getXML());
    }

    /**
     * Not applicable to FormFieldDesigner. Abstract method must be implemented.
     *
     * @return false always
     */
    public boolean isSelectable() {
        return false;
    }

}




