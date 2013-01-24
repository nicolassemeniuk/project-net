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

import net.project.persistence.PersistenceException;

/**
 * This class describes a form field.
 * @author Bern McCarty
 * @author Roger Bly
 * @since Version 1
 */
public interface IFormField {
    public int rowNum();
    public int columnNum();
    public String getDataColumnName(); 
    public void setDataColumnName(String columnName);  
    public String getID();      

    /**
     * Store form field into the database. Create a new form field in the
     * database if needed, otherwise update the existing form field in the
     * database.
     */
    public void store() throws PersistenceException;

    /**
     * Get all the properties for the FormField from the database.
     */
    public void loadProperties();

    /**
     * @return the database storage type for the field. i.e. NUMBER(20)
     */
    public String dbStorageType();

    /**
     * @return true/false depending upon whether it is selectable for display in
     * the list.
     */
    public boolean isSelectable();

    /**
     * Can this field be used for filtering.
     *
     * @return true if the field can be used to filter form data.
     */
    public boolean isFilterable();

    /**
     * Can this field be used for searching.
     *
     * @return true if the field can be used to filter form data.
     */
    public boolean isSearchable();

    /**
     * Can this field be used for sorting.
     *
     * @return true if the field can be used to sort form data.
     */
    public boolean isSortable();

    /**
     * Outputs an HTML representation of this field to the specified stream.
     * This will typically include table divisions spanning up to 4 columns.
     */
    public void writeHtml(FieldData field_data, java.io.PrintWriter out) throws java.io.IOException;

    /**
     * Outputs an Readonly HTML representation of this field to the specified
     * stream. This will typically include table divisions spanning up to 4
     * columns.
     */
    public void writeHtmlReadOnly(FieldData field_data, java.io.PrintWriter out) throws java.io.IOException;
    
    public String writeHtmlReadOnly(FieldData field_data);

    /**
     * Outputs an HTML representation of the field's filter to the specified
     * stream.
     */
    public void writeFilterHtml(FieldFilter filter, java.io.PrintWriter out) throws java.io.IOException;

    /**
     * Process the HTTP request to extract the filter selections for this field.
     *
     * @return the FieldFilter containing the filter information for the field,
     * null if their was no filter information for the field in the request.
     */
    public FieldFilter processFilterHttpPost(javax.servlet.ServletRequest request);

    /**
     * Process the field data to the correct internal format after receiving
     * from http post
     *
     * @param data the field data to process
     */
    public void processFieldData(FieldData data);

    /**
     * Formats the field data and returns it in a form suitable for using in a
     * CSV file.
     *
     * @param  data the field data to be formatted and returned.
     * @return a comma separated list representation of the field_data formatted
     * correctly for this type of field.
     */
    public String formatFieldDataCSV(FieldData data);

    /**
     * Formats the field data and returns it as a string suitable for using
     * in the list view.
     *
     * @param data the field data to be formatted and returned.
     * @return a string representation of the field data formatted correctly for
     * this type of field.
     */
    public String formatFieldDataListView(FieldData data);

    /**
     * Return an SQL representation of the field filter to be used in a WHERE
     * clause.
     *
     * @param filter the filter values to use in generating the SQL.
     */
    public String getFilterSQL(FieldFilter filter, String joinOperator);

    /**
     * Does this field support domain lists.
     *
     * @return true if this field has a domain list, false otherwise.
     */
    public boolean hasDomain();
    
    /**
     * Does this field appear in form designer
     * Field like assignedUserField appear only in list designer and 
     * on data list but can not be added as field on form designer nor
     * their appear on form data edit form 
     * 
     * @return true if field is visible in form designer, false otherwise
     */
    public boolean isDesignable();
}

