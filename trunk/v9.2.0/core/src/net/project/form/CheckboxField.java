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

import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;
import net.project.util.HTMLUtils;

/**
 * A form's checkbox field.
 */
public class CheckboxField extends FormField {
    public static final int DATA_COLUMN_SIZE = 1; // NUMBER(1)


    /**
     * @param form the Form that this field belongs to.
     * @param field_id the id of the field in the database.
     */
    public CheckboxField(Form form, String field_id) {
        super(form, field_id);
        m_data_column_size = DATA_COLUMN_SIZE;
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
        return false;
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
     * @return the database storage type for the field.  i.e. NUMBER(1)
     */
    public String dbStorageType() {
        return (m_db_datatype + "(" + m_data_column_size + ")");
    }

    public String formatFieldDataListView(FieldData fieldData) {
        String data = formatFieldData(fieldData);

        if (data.equals("1")) {
            return "<img src=\""+ SessionManager.getJSPRootURL() +"/images/blk_check.gif\" width=\"20\" height=\"15\" border=\"0\">";
        } else {
            return null;
        }
    }

    public String formatFieldDataCSV(FieldData fieldData) {
        String data = formatFieldData(fieldData);

        if (data.equals("1")) {
            return "yes";
        } else {
            return "no";
        }
    }

    public String formatFieldData(FieldData fieldData) {
        if (fieldData.size() > 0 &&
            (String)fieldData.get(0) != null &&
            (fieldData.get(0)).equals("1")) {

            return "1";

        } else {
            return "0";

        }
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


        out.print("<td align=\"left\" ");
        if (m_column_span > 1)
            out.print("colspan=\"" + ((m_column_span * 4)) + "\"");
        else
            out.print("colspan=\"2\"");

        // the field tag
        out.print(">");
        out.print("<" + m_tag + " ");

        // field name.  set name="fieldXXXX" in the tag.
        out.print(" name=\"" + m_data_column_name + "\" ");

        // field properties  (html tag attributes)
        num_properties = m_properties.size();

        for (prop_cnt = 0; prop_cnt < num_properties; prop_cnt++) {
            property = (FormFieldProperty)m_properties.get(prop_cnt);

            if (property.m_type.equals("in_tag")) {
                if (property.m_name != null && !property.m_name.equals(""))
                    out.print(property.m_name);
                if (property.m_value != null && !property.m_value.equals(""))
                    out.print("=\"" + property.m_value + "\" ");
                else
                    out.print(" ");
            }
        }

        // write the data value in the field if there is one.
        if (field_data != null) {
            try {
                data_value = (String)field_data.get(0);        // only one value for text field
            } catch (IndexOutOfBoundsException e) {
                data_value = "";
            }
        }
        out.print(" value=\"1\"");
        if ((data_value != null) && data_value.equals("1")) out.print(" checked");
        out.print(">\n");

        out.print("&nbsp;" + HTMLUtils.escape(m_field_label) + "\n");
        out.print("</td>\n");
    }


    /**
     * Outputs an HTML representation of this object to the specified stream.
     */
    public void writeHtmlReadOnly(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        FormFieldProperty property;
        String data_value = "";
        int num_properties;
        int prop_cnt;


        out.print("<td align=\"left\" ");
        if (m_column_span > 1)
            out.print("colspan=\"" + ((m_column_span * 4)) + "\"");
        else
            out.print("colspan=\"2\"");

        // the field tag
        out.print(">");
        out.print("<" + m_tag + " ");
        out.print("readonly" + " ");


        // field name.  set name="fieldXXXX" in the tag.
        out.print(" name=\"" + m_data_column_name + "\" ");

        // field properties  (html tag attributes)
        num_properties = m_properties.size();

        for (prop_cnt = 0; prop_cnt < num_properties; prop_cnt++) {
            property = (FormFieldProperty)m_properties.get(prop_cnt);

            if (property.m_type.equals("in_tag")) {
                if (property.m_name != null && !property.m_name.equals(""))
                    out.print(property.m_name);
                if (property.m_value != null && !property.m_value.equals(""))
                    out.print("=\"" + property.m_value + "\" ");
                else
                    out.print(" ");
            }
        }

        // write the data value in the field if there is one.
        if (field_data != null) {
            try {
                data_value = (String)field_data.get(0);        // only one value for text field
            } catch (IndexOutOfBoundsException e) {
                data_value = "";
            }
        }
        out.print(" value=\"1\"");
        if ((data_value != null) && data_value.equals("1")) out.print(" checked");
        out.print(">\n");

        out.print("&nbsp;" + HTMLUtils.escape(m_field_label) + "\n");
        out.print("</td>\n");
    }

    public String writeHtmlReadOnly(FieldData field_data){
    
    	StringBuffer fieldHtml = new StringBuffer();
		String data_value = "";

		fieldHtml.append("<td align=\"left\" ");
		if (m_column_span > 1)
			fieldHtml.append("colspan=\"" + ((m_column_span * 4)) + "\"");
		else
			fieldHtml.append("colspan=\"2\"");

		// write the data value in the field if there is one.
		if (field_data != null) {
			try {
				data_value = (String) field_data.get(0); 														
			} catch (IndexOutOfBoundsException e) {
				data_value = "";
			}
		}
		fieldHtml.append(">");
		//fieldHtml.append(" value=\"1\"");
		fieldHtml.append(" " + HTMLUtils.escape(m_field_label) + "");
		if ((data_value != null) && data_value.equals("1")){
			fieldHtml.append(" checked ");
		} 

		
		fieldHtml.append("</td>");
		
		return fieldHtml.toString();
}
    
    
    /**
     * Outputs an HTML representation of the field's filter to the specified
     * stream.
     */
    public void writeFilterHtml(FieldFilter filter, java.io.PrintWriter out)
        throws java.io.IOException {
        FieldFilterConstraint constraint = null;
        String filterValue = null;

        // the label
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + "</td>");
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\"> &nbsp;&nbsp; = &nbsp;&nbsp; </td>");
        out.println("<td align=\"left\">");

        if ((filter != null) && (filter.size() > 0)) {
            constraint = filter.getConstraint(0);
            if ((constraint != null) && (constraint.size() > 0))
                filterValue = (String)constraint.get(0);
        }

        // All
        out.print("<input type=radio name=\"filter__" + m_field_id + "\" value=All ");
        if ((filterValue == null) || filterValue.equals(""))
            out.print("checked ");
        out.println(">" + PropertyProvider.get("prm.form.designer.fields.create.type.checkbox.all.text"));

        // Yes
        out.print("<input type=radio name=\"filter__" + m_field_id + "\" value=1 ");
        if ((filterValue != null) && filterValue.equals("1"))
            out.print("checked ");
        out.println(">" + PropertyProvider.get("prm.form.designer.fields.create.type.checkbox.yes.text"));

        // No
        out.print("<input type=radio name=\"filter__" + m_field_id + "\" value=0 ");
        if ((filterValue != null) && filterValue.equals("0"))
            out.print("checked ");
        out.println(">" + PropertyProvider.get("prm.form.designer.fields.create.type.checkbox.no.text"));

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
        FieldFilter filter = null;
        FieldFilterConstraint constraint = null;

        value = request.getParameter("filter__" + m_field_id);

        if ((value == null) || (value.equals("All")) || (value.equals(""))) {
            value = null;
        }

        filter = new FieldFilter(this);
        constraint = new FieldFilterConstraint(1);
        // CheckboxField only supports the "=" operator.
        constraint.setOperator("=");
        constraint.add(value);
        filter.addConstraint(constraint);
        return filter;
    }

    /**
     * Process the specified value for insertion into persistence storage.
     *
     * @return <code>"0"</code> when value is null; otherwise the value
     * formatted as a number.
     */
    public String processDataValueForInsert(String value) {
        if (value == null) {
            return "0";
        } else {
            return net.project.database.DBFormat.number(value);
        }
    }


    /**
     * @return true/false depending upon whether it is selectable for display in
     * the list.
     */
    public boolean isSelectable() {
        return true;
    }


}


