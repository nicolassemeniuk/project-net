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
|    TextAreaField.java
|   $Revision: 20642 $
|       $Date: 2010-03-30 10:57:51 -0300 (mar, 30 mar 2010) $
|     $Author: umesha $
+----------------------------------------------------------------------*/
package net.project.form;

import net.project.base.property.PropertyProvider;
import net.project.util.HTMLUtils;


/**  
    A form's multi-line text input field.        
*/
public class TextAreaField extends FormField
{
    /**
     * Specifies the size of the database storage column for a text area field.
     * Note:  This no longer implies the max chunk-size of data in a text area
     * row since multi-byte database potentially cannot store the full 4000
     * characters in a 4000 character column.  However, we're maintaining the
     * column size at 4000 characters to ensure consistency with old data
     * tables.
     * Currently <code>4000</code>.
     */
    public static final int DATA_COLUMN_SIZE = 4000;

    /**
     * Specifies the size to split up text at.  We no longer split into 4000
     * character chunks since 1000 characters is potentially the largest number
     * that can be stored in a VARCHAR2 column in a multi-byte database.
     * Currently <code>1000</code>.
     */
    public static final int DATA_CHUNK_SIZE = 1000;

    /**
     * @param form the Form that this field belongs to.
     * @param field_id the id of the field in the database.
     */
    public TextAreaField(Form form, String field_id) {
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
        return true;
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
     * @return the database storage type for the field. i.e. VARCHAR2(20) or
     * CLOB
     */
    public String dbStorageType() {
        return (m_db_datatype + "(" + m_data_column_size + ")");
    }

    /**
     * The text area splits up the field data into maximum column size
     * chunks.<br> Assume the max column size is 4000. If data contains one 4001
     * character string, after processing it will contain two elements : a 4000
     * character string and a 1 character string.<br> Note - If the data passed
     * in is already processed, it will be re-processed.
     *
     * @param data the field data to process
     */
    public void processFieldData(FieldData data) {
        String oldData;
        String section;
        int startPos = 0, endPos;

        // Reconstitute old data into one string
        oldData = TextAreaField.getString(data);

        // Now process
        data.clear();
        if (oldData == null || oldData.length() == 0) {
            data.add("");
        } else {
            while (startPos < oldData.length()) {
                endPos = DATA_CHUNK_SIZE + startPos;
                if (endPos > oldData.length()) {
                    endPos = oldData.length();
                }
                section = oldData.substring(startPos, endPos);
                data.add(section);
                startPos += DATA_CHUNK_SIZE;
            }
        }

    }


    public String formatFieldData(FieldData fieldData) {
        return TextAreaField.getString(fieldData);
    }

    public String formatFieldDataListView(FieldData field_data) {
        String data = TextAreaField.getString(field_data);
        if (data.length() > 2000) {
            data = data.substring(0, 2000) + "...";
        }

        return HTMLUtils.escape(data);
    }


    /**
     * Outputs an HTML representation of this object to the specified stream.
     */
    public void writeHtml(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        FormFieldProperty property;
        String data_value = "";
        int num_properties;
        int prop_cnt;

        // Table frame for the TextArea
        out.print("<td align=\"left\" ");

        // if (m_column_span > 1)
        out.print("colspan=\"" + (m_column_span * 2) + "\" ");

        out.println(">");

        out.println("<table class=\"tableContent\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
        out.println("<tr>");
        out.println("<td>" + HTMLUtils.escape(m_field_label) + ":</td>");

        m_field_label = HTMLUtils.escape(m_field_label).replaceAll("\"","");
        // The resize and popup tools
        out.println("<td align=\"right\">");
        out.println("<a href=\"javascript:resizeTextArea('" + m_data_column_name + "', 2);\" tabindex=\"-1\">" + PropertyProvider.get("prm.form.individualform.create.up.link") + "</a>");
        out.println("&nbsp;&nbsp;<a href=\"javascript:resizeTextArea('" + m_data_column_name + "', -2);\" tabindex=\"-1\">" + PropertyProvider.get("prm.form.individualform.create.down.link") + "</a>");
        out.println("&nbsp;&nbsp;<a href=\"javascript:popupTextArea('" + m_data_column_name + "','" + m_field_label + "');\" tabindex=\"-1\">" + PropertyProvider.get("prm.form.individualform.create.popedit.link") + "</a>");
        out.println("</td>\n</tr>\n<tr>");

        // the textarea
        out.print("<td align=left colspan=2>");

        // the field tag
        out.print("\n<" + m_tag + " ");
        // FIELD NAME.  set name="fieldXXXX" in the tag.
        out.print(" name=\"" + m_data_column_name + "\" ");

        if (isValueRequired) {
            out.print(" required=\"true\" ");
        }

        // field properties  (html tag attributes)
        num_properties = m_properties.size();

        for (prop_cnt = 0; prop_cnt < num_properties; prop_cnt++) {
            property = (FormFieldProperty)m_properties.get(prop_cnt);

            if (property.m_type.equals("in_tag")) {
                if (property.m_name != null && !property.m_name.equals(""))
                    out.print(" " + property.m_name);
                if (property.m_value != null && !property.m_value.equals(""))
                    out.print("=\"" + property.m_value + "\" ");
                else
                    out.print(" ");
            }
        }
        out.print(" wrap=\"virtual\">");

        // write the data value in the field if there is one.
        if (field_data != null) {
            data_value = getString(field_data);
        }
        // Note - out.print imporant here to avoid trailing CR in textarea
        if (data_value != null)
            out.print(data_value);
        out.println("</textarea>");

        // Add appropriate script for indicating field is required
        if (isValueRequired()) {
            out.println(getRequiredValueJavascript());
        }

        out.println("</td>");
        out.println("</tr>");
        out.println("</table>");
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

        // Table frame for the TextArea
        out.print("<td align=\"left\" ");

        // if (m_column_span > 1)
        out.print("colspan=\"" + (m_column_span * 2) + "\" ");

        out.println(">");

        out.println("<table class=\"tableContent\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
        out.println("<tr>");
        out.println("<td>" + HTMLUtils.escape(m_field_label) + ":</td>");
        m_field_label = HTMLUtils.escape(m_field_label).replaceAll("\"","");

        // The resize and popup tools
        out.println("<td align=\"right\">");
        out.println("<a href=\"javascript:resizeTextArea('" + m_data_column_name + "', 2);\" tabindex=\"-1\">" + PropertyProvider.get("prm.form.individualform.create.up.link") + "</a>");
        out.println("&nbsp;&nbsp;<a href=\"javascript:resizeTextArea('" + m_data_column_name + "', -2);\" tabindex=\"-1\">" + PropertyProvider.get("prm.form.individualform.create.down.link") + "</a>");
        out.println("&nbsp;&nbsp;<a href=\"javascript:popupTextArea('" + m_data_column_name + "','" + m_field_label + "');\" tabindex=\"-1\">" + PropertyProvider.get("prm.form.individualform.create.popedit.link") + "</a>");
        out.println("</td>\n</tr>\n<tr>");

        // the textarea
        out.print("<td align=left colspan=2>");

        // the field tag
        out.print("\n<" + m_tag + " ");
        out.print("readonly" + " ");
        // FIELD NAME.  set name="fieldXXXX" in the tag.
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
                else
                    out.print(" ");
            }
        }
        out.print(" wrap=\"virtual\">");

        // write the data value in the field if there is one.
        if (field_data != null) {
            data_value = getString(field_data);
        }
        // Note - out.print imporant here to avoid trailing CR in textarea
        if (data_value != null)
            out.print(data_value);
        out.println("</textarea>");
        out.println("</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("</td>");
    }


    public String writeHtmlReadOnly(FieldData field_data){
    
    FormFieldProperty property;
    String data_value = "";
    int num_properties;
    int prop_cnt;
    StringBuffer fieldHtml = new StringBuffer();

    // Table frame for the TextArea
    fieldHtml.append("<td align=\"left\" ");

    // if (m_column_span > 1)
    fieldHtml.append("colspan=\"" + (m_column_span * 2) + "\" ");

    fieldHtml.append(">");

    fieldHtml.append("<table  border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
    fieldHtml.append("<tr>");
    fieldHtml.append("<td class=\"tableHeader\">" + HTMLUtils.escape(m_field_label) + ":</td>");

    // The resize and popup tools
    fieldHtml.append("</tr>\n<tr>");

    // the textarea
    fieldHtml.append("<td align=\"left\" colspan=\"2\" class=\"tableContent\">");

    // the field tag
    //fieldHtml.append("\n<" + m_tag + " ");
    //fieldHtml.append("readonly" + " ");
    // FIELD NAME.  set name="fieldXXXX" in the tag.
    //fieldHtml.append(" name=\"" + m_data_column_name + "\" ");

    // field properties  (html tag attributes)
   // num_properties = m_properties.size();

/*    for (prop_cnt = 0; prop_cnt < num_properties; prop_cnt++) {
        property = (FormFieldProperty)m_properties.get(prop_cnt);

        if (property.m_type.equals("in_tag")) {
            if (property.m_name != null && !property.m_name.equals(""))
            	fieldHtml.append(" " + property.m_name);
            if (property.m_value != null && !property.m_value.equals(""))
            	fieldHtml.append("=\"" + property.m_value + "\" ");
            else
            	fieldHtml.append(" ");
        }
    }
    fieldHtml.append(" wrap=\"virtual\">");*/

    // write the data value in the field if there is one.
    if (field_data != null) {
        data_value = getString(field_data);
    }
    // Note - out.print imporant here to avoid trailing CR in textarea
    if (data_value != null)
    	fieldHtml.append(data_value);
    /*fieldHtml.append("</textarea>");*/
    fieldHtml.append("</td>");
    fieldHtml.append("</tr>");
    fieldHtml.append("</table>");
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

        // Search field for textarea is a textarea itself
        out.print("<textarea cols=\"60\" rows=\"3\" name=\"filter__" + m_field_id + "\">");
        if (filterValue != null) {
            out.println(filterValue);
        }
        out.print("</textarea>");

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
        // TextArea only supports the "like" operator.
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

                    sb.append("(UPPER(" + this.getDataColumnName() + ") LIKE UPPER('%" + (String) constraint.get(j) + "%'))");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }


    /**
     * Compares field data for two text area fields. Two text area fields' data
     * are equal iff text is byte-for-byte identical.<br> Note: This
     * inefficient. It would be more efficient to compare each row of data
     * individually, to avoid manipulating large strings (in the case where the
     * data has been broken into multiple rows).
     *
     * @return 0 if equal, -1 if data1 < data2, 1 if data1 > data2, per contract
     * of java.util.Comparator.
     * @see java.util.Comparator
     */
    public int compareData(FieldData data1, FieldData data2) {
        String data1String, data2String;

        if ((data1String = TextAreaField.getString(data1)) != null &&
            (data2String = TextAreaField.getString(data2)) != null) {
            return data1String.toLowerCase().compareTo(data2String.toLowerCase());
        } else {
            if (data1String == null) {
                return 1;
            } else {
                return -1;
            } //end if
        } //end if
    }


    /**
     * Returns raw string version of text area data such that it may be
     * used for comparisons etc.
     *
     * @param data the field data to return as string
     * @return the data from field data as a string or the empty string if data
     * is null
     */
    private static String getString(FieldData data) {
        StringBuffer dataBuff = new StringBuffer();

        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) != null)
                    dataBuff.append(data.get(i));
            }
        }
        return dataBuff.toString();
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


}


