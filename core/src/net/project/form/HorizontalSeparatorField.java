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

/**
 * User instructions as static text. This field is static and stores no user
 * data.
 */
public class HorizontalSeparatorField extends FormField {
    public static final int DATA_COLUMN_SIZE = 0;  // static field, no storage.

    /**
     * @param form the Form that this field belongs to.
     * @param field_id the id of the field in the database.
     */
    public HorizontalSeparatorField(Form form, String field_id) {
        super(form, field_id);
        m_data_column_size = DATA_COLUMN_SIZE;
    }


    /**
     * Can this field be used for filtering.
     *
     * @return true if the field can be used to filter form data.
     */
    public boolean isFilterable() {
        return false;
    }


    /**
     * Can this field be used for searching.
     *
     * @return true if the field can be used to filter form data.
     */
    public boolean isSearchable() {
        return false;
    }


    /**
     * Can this field be used for sorting.
     *
     * @return true if the field can be used to sort form data.
     */
    public boolean isSortable() {
        return false;
    }


    /**
     * @return the database storage type for the field; this field type is
     * static and does not store data.
     */
    public String dbStorageType() {
        return null;
    }


    /**
     * @return a string representation of the field_data formatted correctly for
     * this type of field.
     * @param field_data the data item to be formatted and returned.
     */
    public String formatFieldData(FieldData field_data) {
        return m_field_label;
    }


    /**
     * Outputs an HTML representation of this object to the specified stream.
     */
    public void writeHtml(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        // Table frame for the TextArea
        out.print("<td align=\"left\" class=\"channelHeader\" ");

        if (m_column_span > 1)
            out.print("colspan=\"" + (m_column_span * 2) + "\"");
        else
            out.print("colspan=2");

        out.println(">");

        // the field label is the text for the separator.
        out.println(m_field_label);

        out.println("</th>");
    }

    /**
     * Outputs an Read-Only HTML representation of this object to the specified
     * stream.
     */
    public void writeHtmlReadOnly(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        writeHtml(field_data, out);
    }

    public String writeHtmlReadOnly(FieldData field_data){
    	return "";
    }    

    /**
     * Outputs an HTML representation of the field's filter to the specified
     * stream.
     */
    public void writeFilterHtml(FieldFilter filter, java.io.PrintWriter out)
        throws java.io.IOException {
    }


    /**
     * Process the HTTP request to extract the filter selections for this field.
     *
     * @return the FieldFilter containing the filter information for the field,
     * null if their was no filter information for the field in the request.
     */
    public FieldFilter processFilterHttpPost(javax.servlet.ServletRequest request) {
        return null;
    }


    /**
     * Not supported by this field type.
     *
     * @return always returns null.
     */
    public String formatFieldDataCSV(FieldData field_data) {
        return null;
    }

    /**
     * @return true/false depending upon whether it is selectable for display in
     * the list.
     */
    public boolean isSelectable() {
        return false;
    }

}



