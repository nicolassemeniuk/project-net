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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19714 $
|       $Date: 2009-08-11 15:09:52 -0300 (mar, 11 ago 2009) $
|     $Author: vivana $
|
+-----------------------------------------------------------------------------*/
package net.project.form;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletRequest;

import net.project.base.property.PropertyProvider;

public class FormID extends FormField {	
    /**
     * Bean constructor.
     */
    public FormID() {
        setDataColumnName("seq_num");
    }

    /**
	 * @param form the Form that this field belongs to.
	 * @param field_id the id of the field in the database.
	 */
	public FormID(Form form, String field_id) {
		super(form, field_id);
		setDataColumnName("seq_num");
	}
    
	public String getDataColumnName(){
		return "seq_num";
	}
	
    
    /**
     * Formats the field data.
     * @param fieldData the fieldData to format
     * @return the formatted field data
     */
    public String formatFieldData(FieldData fieldData) {
        return fieldData.get(0).toString();
    }

    /**
     * @return the database storage type for the field. i.e. NUMBER(20)
     */
    public String dbStorageType() {
        return null;
    }

    /**
     * @return true/false depending upon whether it is selectable for display in
     * the list.
     */
    public boolean isSelectable() {
        return false;
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
        return true;
    }

    /**
     * Can this field be used for sorting.
     *
     * @return true if the field can be used to sort form data.
     */
    public boolean isSortable() {
        return true;
    }
           
    @Override
	public boolean isDesignable() {
		return false;
	}

	/**
     * Outputs an HTML representation of this field to the specified stream.
     * This will typically include table divisions spanning up to 4 columns.
     */
    public void writeHtml(FieldData field_data, PrintWriter out) throws IOException {
        throw new RuntimeException("Operation not supported");
    }

    /**
     * Outputs an HTML representation of the field's filter to the specified
     * stream.
     */
    public void writeFilterHtml(FieldFilter filter, PrintWriter out) throws IOException {
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + PropertyProvider.get("prm.form.search.formid.fieldname") + "</td>");
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\"> &nbsp;&nbsp; = &nbsp;&nbsp; </td>");
        out.println("<td align=\"left\">");

        // the field tag
        out.print("<input type=\"text\" name=\"filter__id\" ");

        out.print("</td>\n");
    }

    /**
     * Process the HTTP request to extract the filter selections for this field.
     *
     * @return the FieldFilter containing the filter information for the field,
     * null if their was no filter information for the field in the request.
     */
    public FieldFilter processFilterHttpPost(ServletRequest request) {
        String value;
        FieldFilter filter = null;
        FieldFilterConstraint constraint = null;

        value = request.getParameter("filter__id");

        if ((value != null) && value.equals(""))
            value = null;

        filter = new FieldFilter(this);
        constraint = new FieldFilterConstraint(1);
        constraint.setOperator("=");
        constraint.add(value);
        filter.addConstraint(constraint);
        return filter;
    }

}
