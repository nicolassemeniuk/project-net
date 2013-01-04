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
|   $Revision: 19714 $
|       $Date: 2009-08-11 15:09:52 -0300 (mar, 11 ago 2009) $
|     $Author: vivana $
|
+----------------------------------------------------------------------*/
package net.project.form;

import java.util.Iterator;

import net.project.database.DBFormat;
import net.project.util.HTMLUtils;

/**
 * A form's single line text input field.
 */
public class TextField extends FormField {


    /**
     * @param form the Form that this field belongs to.
     * @param field_id the id of the field in the database.
     */
    public TextField(Form form, String field_id) {
        super(form, field_id);
    }


    /**
     * Can this field be used for filtering.
     *
     * @return true if the field can be used to filter form data.
     */
    public boolean isFilterable() {
        return true;
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


    /**
     * @return the database storage type for the field. i.e. VARCHAR2(80)
     */
    public String dbStorageType() {
        return (m_db_datatype + "(" + m_data_column_size + ")");
    }

    public String formatFieldDataListView(FieldData field_data) {
    	  if ((field_data == null) || (field_data.get(0) == null))
              return "";
          else
              return HTMLUtils.escape((String)field_data.get(0));    	
    }

    public String formatFieldData(FieldData field_data) {
        if ((field_data == null) || (field_data.get(0) == null))
            return "";
        else
            return ((String)field_data.get(0));
    }


    /**
     * Outputs an HTML representation of this object to the specified stream.
     */
    public void writeHtml(FieldData field_data, java.io.PrintWriter out) throws java.io.IOException {
        FormFieldProperty property;
        String data_value = "";
        int num_properties;
        int prop_cnt;

        out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + ":&nbsp;&nbsp;</td>\n");
        out.print("<td align=\"left\" ");
        if (m_column_span > 1)
            out.print("colspan=\"" + ((m_column_span * 2) - 1) + "\"");

        // the field tag
        out.print(">\n<" + m_tag + " ");
        // field name.  set name="fieldXXXX" in the tag.
        out.print(" name=\"" + m_data_column_name + "\" ");

        // field properties  (html tag attributes)
        num_properties = m_properties.size();

        for (prop_cnt = 0; prop_cnt < num_properties; prop_cnt++) {
            property = (FormFieldProperty)m_properties.get(prop_cnt);

            if (property.m_type.equals("in_tag")) {
                if (property.m_name != null && !property.m_name.equals(""))
                    out.print(" " + property.m_name);
                if (property.m_value != null && !property.m_value.equals(""))
                    out.print("=\"" + property.m_value + "\" ");
                //else
                //out.print(" ");
            }
        }

        // write the data value in the field if there is one.
        if (field_data != null) {
            try {
                data_value = (String)field_data.get(0);        // only one value for text field
            } catch (IndexOutOfBoundsException e) {
                data_value = null;
            }
        }

        if (isValueRequired()) {
            out.println(" required=\"true\"");
        }

    /*    if (data_value != null)
            out.println(" value=\"" + HTMLUtils.escape(data_value) + "\">");
        else
            out.println(" value=\"\">");*/

        if (data_value != null) {
            if ("data_column_scale".equals(m_data_column_name) && data_value.equals("-1")) {
                data_value = "";
            }
            out.println(" value=\"" + HTMLUtils.escape(data_value) + "\">");
        }
        else
            out.println(" value=\"\">");        
        

        // Add appropriate script for indicating field is required
        if (isValueRequired()) {
            out.println(getRequiredValueJavascript());
        }

        out.println("</td>");
    }

    /**
     * Outputs an Read-Only HTML representation of this object to the specified
     * stream.
     */
    public void writeHtmlReadOnly(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        FormFieldProperty property;
        String data_value = "";
        int num_properties;
        int prop_cnt;

        out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + ":&nbsp;&nbsp;</td>\n");
        out.print("<td align=\"left\" ");
        if (m_column_span > 1)
            out.print("colspan=\"" + ((m_column_span * 2) - 1) + "\"");

        // the field tag
        out.print(">\n<" + m_tag + " ");
        out.print("readonly" + " ");
        // field name.  set name="fieldXXXX" in the tag.
        out.print(" name=\"" + m_data_column_name + "\" ");

        // field properties  (html tag attributes)
        num_properties = m_properties.size();

        for (prop_cnt = 0; prop_cnt < num_properties; prop_cnt++) {
            property = (FormFieldProperty)m_properties.get(prop_cnt);

            if (property.m_type.equals("in_tag")) {
                if (property.m_name != null && !property.m_name.equals(""))
                    out.print(" " + property.m_name);
                if (property.m_value != null && !property.m_value.equals(""))
                    out.print("=\"" + property.m_value + "\" ");
                //else
                //out.print(" ");
            }
        }

        // write the data value in the field if there is one.
        if (field_data != null) {
            try {
                data_value = (String)field_data.get(0);        // only one value for text field
            } catch (IndexOutOfBoundsException e) {
                data_value = null;
            }
        }

        if (data_value != null)
            out.println(" value=\"" + HTMLUtils.escape(data_value) + "\">");
        else
            out.println(" value=\"\">");

        out.println("</td>");
    }

    public String writeHtmlReadOnly(FieldData field_data) {    
		    String data_value = "";
		    StringBuffer fieldHtml = new StringBuffer();
		    fieldHtml.append("<td align=\"left\" width=\"1%\" class=\"tableHeader\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + ": </td>");
		    fieldHtml.append("<td align=\"left\" class=\"tableContent\"");
		    if (m_column_span > 1)
		    	fieldHtml.append("colspan=\"" + ((m_column_span * 2) - 1) + "\"");
		    fieldHtml.append(">"); 
		    // write the data value in the field if there is one.
		    if (field_data != null) {
		        try {
		            data_value = (String)field_data.get(0);  // only one value for text field
		        } catch (IndexOutOfBoundsException e) {
		            data_value = null;
		        }
		    }
		    if (data_value != null)
		    	fieldHtml.append(HTMLUtils.escape(data_value));
		    else
		    	fieldHtml.append("");
		    fieldHtml.append("</td>");
		    return fieldHtml.toString();
    }    
    

    /**
     * Outputs an HTML representation of the field's filter to the specified
     * stream.
     */
    public void writeFilterHtml(FieldFilter filter, java.io.PrintWriter out)
        throws java.io.IOException {
        FieldFilterConstraint constraint;
        String filterValue = null;
        FormFieldProperty property;
        int num_properties;
        int prop_cnt;

        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + "</td>");
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\"> &nbsp;&nbsp; = &nbsp;&nbsp; </td>");
        out.println("<td align=\"left\">");

        if ((filter != null) && (filter.size() > 0)) {
            constraint = filter.getConstraint(0);
            if ((constraint != null) && (constraint.size() > 0))
                filterValue = (String)constraint.get(0);
            if ((filterValue != null) && filterValue.equals(""))
                filterValue = null;
        }

        // the field tag
        out.print("<" + m_tag + " ");
        // field name.  set name="fieldXXXX" in the tag.
        out.print(" name=\"filter__" + m_field_id + "\" ");

        // field properties  (html tag attributes)
        num_properties = m_properties.size();

        for (prop_cnt = 0; prop_cnt < num_properties; prop_cnt++) {
            property = (FormFieldProperty)m_properties.get(prop_cnt);

            if (property.m_type.equals("in_tag")) {
                if (property.m_name != null && !property.m_name.equals(""))
                    out.print(" " + property.m_name);
                if (property.m_value != null && !property.m_value.equals(""))
                    out.print("=\"" + property.m_value + "\" ");
            }
        }

        // output the filter value
        if (filterValue != null)
            out.println(" value=\"" + filterValue + "\">");
        else
            out.println(" value=\"\">");

        out.print("</td>\n");
    }


    /**
     * Process the HTTP request to extract the filter selections for this field.
     *
     * @return the FieldFilter containing the filter information for this field,
     * null if their was no filter information for this field in the request.
     */
    public FieldFilter processFilterHttpPost(javax.servlet.ServletRequest request) {
        String value;
        FieldFilter filter;
        FieldFilterConstraint constraint;

        value = request.getParameter("filter__" + m_field_id);

        if ((value != null) && value.equals(""))
            value = null;

        filter = new FieldFilter(this);
        constraint = new FieldFilterConstraint(1);
        // TextField only supports the "like" operator.
        constraint.setOperator("like");
        constraint.add(value);
        filter.addConstraint(constraint);
        return filter;
    }


    /**
     * Return an SQL representation of the field filter to be used in a WHERE
     * clause.
     *
     * @param filter filter values to use in the generated SQL.
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

                    sb.append("(UPPER(" + this.getDataColumnName() + ") LIKE UPPER('%" +
                        DBFormat.escape((String)constraint.get(j)) +
                        "%'))");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Process the specified value for insertion into persistence storage.
     *
     * @return <code>"NULL"</code> when value is null; otherwise the value
     * delimited by ' characters.
     * @see net.project.database.DBFormat#varchar2
     */
    public String processDataValueForInsert(String value) {
        if (value == null) {
            return "NULL";
        } else {
            return net.project.database.DBFormat.varchar2(value);
        }
    }

    /**
     * @return true/false depending upon whether it is selectable for display
     * in the list.
     */
    public boolean isSelectable() {
        return true;
    }


    /**
     * Validates that the specified FieldData is of appropriate length.
     *
     * @param data the field data to validate
     * @param errorMessagePatternBuffer the buffer into which the error message
     * pattern is written
     * @return true if data is correct length for field
     */
    public boolean isValidFieldData(FieldData data, StringBuffer errorValue, StringBuffer errorMessagePatternBuffer) {
        boolean isValid = true;
        String value;

        Iterator it = data.iterator();
        while (it.hasNext()) {
            value = (String)it.next();

            // Skip null or empty values; they are valid
            if (value != null && value.length() > 0) {

                // If length is greater than size for field, its an error
                if (value.length() > this.m_data_column_size) {
                    errorValue.append(value);
                    errorMessagePatternBuffer.append("{0} is not a valid currency value for this field");
                    isValid = false;
                }

            }
        }

        return isValid;
    }

}


