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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.util.HTMLUtils;
import net.project.xml.XMLUtils;

/**
 * A form's selection menu field.
 */
public class SelectionListField extends FormField {
    public static final int DATA_COLUMN_SIZE = 20;

    /**
     * @param form the Form that this field belongs to.
     * @param field_id the id of the field in the database.
     */
    public SelectionListField(Form form, String field_id) {
        super(form, field_id);
        m_data_column_size = DATA_COLUMN_SIZE;      // NUMBER(20)
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
     * Does this field support domain lists.
     * 
     * @return true if this field has a domain list, false otherwise.
     */
    public boolean hasDomain() {
        // SingleSelectionListFields always have domains
        return true;
    }

    /**
     * @return the database storage type for the field. i.e. NUMBER(20)
     */
    public String dbStorageType() {
        return (m_db_datatype + "(" + m_data_column_size + ")");
    }

    public String formatFieldData(FieldData field_data) {
        StringBuffer dataList = new StringBuffer();
        boolean firstValue = true;
        if ((field_data) == null || (m_domain == null)) {
            return null;
        } else{
        	if(m_domain.getDomainValues() == null){
        		return null;
        	}
        }

        //MAFTODO - We need to change the field_data array to contain real FieldDomainValue objects.
        List values = new ArrayList();
        for (int j = 0; j < field_data.size(); j++) {
            FieldDomainValue fieldDomainValue = m_domain.getValue((String)field_data.get(j));
            if (fieldDomainValue != null) {
                values.add(fieldDomainValue);
            }
        }
        Collections.sort(values);

        for (Iterator it = values.iterator(); it.hasNext();) {
            FieldDomainValue fieldDomainValue = (FieldDomainValue) it.next();

            if (!firstValue) {
                dataList.append(", ");
            }
            dataList.append(fieldDomainValue.m_domain_value_name);
            firstValue = false;
        }

        return dataList.toString();
    }

    /**
     * Outputs an HTML representation of this object to the specified stream.
     */
    public void writeHtml(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        FieldDomainValue domain_value = null;
        FormFieldProperty property = null;
        String data_value = "";
        int num_properties;
        int prop_cnt;
        String selected = null;

        // the label
        out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + ":&nbsp;&nbsp;</td>\n");
        out.print("<td");

        if (m_column_span > 1) {
            out.print(" colspan=\"" + (m_column_span * 2) + "\"");
        }

        out.print(">");

        // the field tag
        out.print("<select ");
        // field name.  set name="fieldXXXX" in the tag.
        out.print(" name=\"" + m_data_column_name + "\" ");

        // TODO -- THIS SHOULD BE AN IN_TAG PROPERTY
        // does the field support multiple selection.
        if (isMultiSelect()) {
            out.print("multiple size=\"4\" ");
        }

        if (isValueRequired) {
            out.print(" required=\"true\" ");
        }

        // field properties  (html tag attributes)
        num_properties = m_properties.size();

        for (prop_cnt = 0; prop_cnt < num_properties; prop_cnt++) {
            property = (FormFieldProperty) m_properties.get(prop_cnt);

            if (property.m_type.equals("in_tag")) {
                if (property.m_name != null && !property.m_name.equals("")) {
                    out.print(property.m_name);
                }
                if (property.m_value != null && !property.m_value.equals("")) {
                    out.print("=\"" + property.m_value + "\" ");
                } else {
                    out.print(" ");
                }
            }
        }
        out.print(">\n");

        // Write the domain options
        if (field_data != null) {
            // the Options tags
            // get the "selected" domain_value_id from the form's data.  If one is not set, use the FieldDomainValue with m_is_default set.
            try {
                data_value = (String) field_data.get(0);
            } catch (IndexOutOfBoundsException e) {
                data_value = null;
            }
        }
        // which option is selected in the list
        if ((data_value != null) && !data_value.equals("")) {
            selected = data_value;
        } else {
            selected = null;
        }

        // write the domain values
        if ((m_domain != null) && m_domain.getDomainValues() != null) {
            List domainValues = m_domain.getDomainValues();

            for (Iterator it = domainValues.iterator(); it.hasNext();) {
                domain_value = (FieldDomainValue) it.next();

                if ((domain_value.m_domain_value_id != null) && !domain_value.m_domain_value_id.equals("") &&
                    (domain_value.m_domain_value_name != null) && !domain_value.m_domain_value_name.equals("")) {
                    out.print("<option value=\"" + domain_value.m_domain_value_id + "\"");

                    // if we have user data, then select the proper value in the domain
                    if (selected != null) {
                        if (isItemSelected(domain_value, field_data)) {
                            out.print(" selected>");
                        } else {
                            out.print(">");
                        }
                    }
                    // no user data, select the default value.
                    else {
                        if (domain_value.m_is_default) {
                            out.print(" selected>");
                        } else {
                            out.print(">");
                        }
                    }
                    out.print(HTMLUtils.escape(domain_value.m_domain_value_name));
                    out.print("</option>\n");
                }
            }
        }


        out.print("</select>\n");

        // Add appropriate script for indicating field is required
        if (isValueRequired()) {
            out.println(getRequiredValueJavascript());
        }

        out.print("</td>\n");
    }

    /**
     * Outputs an disabled (since it can not be made read only)HTML
     * representation of this object to the specified stream.
     */
    public void writeHtmlReadOnly(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        FormFieldProperty property = null;
        String data_value = "";
        int num_properties;
        int prop_cnt;
        String selected = null;

        // the label
        out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + ":&nbsp;&nbsp;</td>\n");
        out.print("<td");

        if (m_column_span > 1) {
            out.print(" colspan=\"" + (m_column_span * 2) + "\"");
        }

        out.print(">");

        // the field tag
        out.print("<select ");
        // Make this field disabled as it can not be made read- only
        out.print(" " + "disabled" + " ");
        // field name.  set name="fieldXXXX" in the tag.
        out.print(" name=\"" + m_data_column_name + "\" ");

        // TODO -- THIS SHOULD BE AN IN_TAG PROPERTY
        // does the field support multiple selection.
        if (isMultiSelect()) {
            out.print("multiple size=\"4\" ");
        }

        // field properties  (html tag attributes)
        num_properties = m_properties.size();

        for (prop_cnt = 0; prop_cnt < num_properties; prop_cnt++) {
            property = (FormFieldProperty) m_properties.get(prop_cnt);

            if (property.m_type.equals("in_tag")) {
                if (property.m_name != null && !property.m_name.equals("")) {
                    out.print(property.m_name);
                }
                if (property.m_value != null && !property.m_value.equals("")) {
                    out.print("=\"" + property.m_value + "\" ");
                } else {
                    out.print(" ");
                }
            }
        }
        out.print(">\n");

        // Write the domain options
        if (field_data != null) {
            // the Options tags
            // get the "selected" domain_value_id from the form's data.  If one is not set, use the FieldDomainValue with m_is_default set.
            try {
                data_value = (String) field_data.get(0);
            } catch (IndexOutOfBoundsException e) {
                data_value = null;
            }
        }
        // which option is selected in the list
        if ((data_value != null) && !data_value.equals("")) {
            selected = data_value;
        } else {
            selected = null;
        }

        // write the domain values
        if ((m_domain != null) && m_domain.getDomainValues() != null) {

            for (Iterator it = m_domain.getDomainValues().iterator(); it.hasNext();) {
                FieldDomainValue domainValue = (FieldDomainValue) it.next();

                if ((domainValue.m_domain_value_id != null) && !domainValue.m_domain_value_id.equals("") &&
                    (domainValue.m_domain_value_name != null) && !domainValue.m_domain_value_name.equals("")) {
                    out.print("<option value=\"" + domainValue.m_domain_value_id + "\"");

                    // if we have user data, then select the proper value in the domain
                    if (selected != null) {
                        if (isItemSelected(domainValue, field_data)) {
                            out.print(" selected>");
                        } else {
                            out.print(">");
                        }
                    }
                    // no user data, select the default value.
                    else {
                        if (domainValue.m_is_default) {
                            out.print(" selected>");
                        } else {
                            out.print(">");
                        }
                    }
                    out.print(HTMLUtils.escape(domainValue.m_domain_value_name));
                    out.print("</option>\n");
                }
            }
        }


        out.print("</select>\n");
        out.print("</td>\n");
    }

    
    public String writeHtmlReadOnly(FieldData field_data){
    	
    	StringBuffer fieldHtml = new StringBuffer();
		String data_value = "";
		String selected = null;

		// the label
		fieldHtml.append("<td align=\"left\" width=\"1%\" class=\"tableHeader\">" + HTMLUtils.escape(m_field_label) + ": </td>");
		fieldHtml.append("<td class=\"tableContent\">");

		// Write the domain options
		if (field_data != null) {
			// the Options tags
			// get the "selected" domain_value_id from the form's data. If one
			// is not set, use the FieldDomainValue with m_is_default set.
			try {
				data_value = (String) field_data.get(0);
			} catch (IndexOutOfBoundsException e) {
				data_value = null;
			}
		}
		// which option is selected in the list
		if ((data_value != null) && !data_value.equals("")) {
			selected = data_value;
		} else {
			selected = null;
		}

		// write the domain values
		if ((m_domain != null) && m_domain.getDomainValues() != null) {

			for (Iterator it = m_domain.getDomainValues().iterator(); it.hasNext();) {
				FieldDomainValue domainValue = (FieldDomainValue) it.next();

				if ((domainValue.m_domain_value_id != null)
						&& !domainValue.m_domain_value_id.equals("")
						&& (domainValue.m_domain_value_name != null)
						&& !domainValue.m_domain_value_name.equals("")) {
/*					fieldHtml.append("<option value=\""
							+ domainValue.m_domain_value_id + "\"");*/

					// if we have user data, then select the proper value in the
					// domain
					if (selected != null) {
						if (isItemSelected(domainValue, field_data)) {							
							//fieldHtml.append(" selected>");
							fieldHtml.append(HTMLUtils.escape(domainValue.m_domain_value_name));
						}/* else {
							fieldHtml.append(">");
						}*/
					}
					// no user data, select the default value.
					else {
						if (domainValue.m_is_default) {
							//fieldHtml.append(" selected>");
							fieldHtml.append(HTMLUtils.escape(domainValue.m_domain_value_name));
						} /*else {
							fieldHtml.append(">");
						}*/
					}
					//fieldHtml.append(HTMLUtils.escape(domainValue.m_domain_value_name));
					//fieldHtml.append("</option>\n");
				}
			}
		}

		//fieldHtml.append("</select>\n");
		fieldHtml.append("</td>");
		
		return fieldHtml.toString();
}    
    
    /**
     * Outputs an HTML representation of the field's filter to the specified
     * stream.
     */
    public void writeFilterHtml(FieldFilter filter, java.io.PrintWriter out)
        throws java.io.IOException {
        FormFieldProperty property = null;
        String defaultValue = null;
        int num_properties;
        int prop_cnt;

        // the label
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + m_field_label + "</td>");
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\"> &nbsp;&nbsp; = &nbsp;&nbsp; </td>");
        out.println("<td align=\"left\">");

        // the field tag
        out.print("<select");

        // field name.  set name="fieldXXXXfilter" in the tag.
        out.print(" name=\"filter__" + m_field_id + "\" ");

        // filters use multiple selection.
        out.print("multiple size=\"4\" ");

        // field properties (html tag attributes)
        num_properties = m_properties.size();
        for (prop_cnt = 0; prop_cnt < num_properties; prop_cnt++) {
            property = (FormFieldProperty) m_properties.get(prop_cnt);

            if (property.m_type.equals("in_tag")) {
                if (property.m_name != null && !property.m_name.equals("")) {
                    out.print(property.m_name);
                }
                if (property.m_value != null && !property.m_value.equals("")) {
                    out.print("=\"" + property.m_value + "\" ");
                } else {
                    out.print(" ");
                }
            }
        }
        out.print(">\n");


        // DEFAULT VALUE
        // Handle default value if there are not filter values for this field
        if ((filter == null) || (filter.size() < 1)) {
            defaultValue = "All";
        }

        // OPTION LIST
        // write option list for all people on the roster.

        // write the "None" option
        out.print("<option value=\"\"");
        if ((defaultValue != null) && defaultValue.equals("All")) {
            out.print(" selected");
        }
        out.print(">" + PropertyProvider.get("prm.form.designer.lists.create.filters.selectionlist.option.all.name") + "</option>\n");


        // write the domain values
        if ((m_domain != null) && m_domain.getDomainValues() != null) {
            for (Iterator it = m_domain.getDomainValues().iterator(); it.hasNext();) {
                FieldDomainValue domain_value = (FieldDomainValue) it.next();
                if ((domain_value.m_domain_value_id != null) && !domain_value.m_domain_value_id.equals("") &&
                    (domain_value.m_domain_value_name != null) && !domain_value.m_domain_value_name.equals("")) {
                    out.print("<option value=\"" + domain_value.m_domain_value_id + "\"");

                    // if this domain value is selected
                    if ((defaultValue == null) && isFilterValueSelected(domain_value, filter)) {
                        out.print(" selected>");
                    } else {
                        out.print(">");
                    }

                    out.print(HTMLUtils.escape(domain_value.m_domain_value_name));
                    out.print("</option>\n");
                }
            }
        }

        out.print("</select>\n");
        out.print("</td>\n");
    }

    /**
     * Process the HTTP request to extract the filter selections for this
     * field.
     * 
     * @return the FieldFilter containing the filter information for the field,
     *         null if their was no filter information for the field in the
     *         request.
     */
    public FieldFilter processFilterHttpPost(javax.servlet.ServletRequest request) {
        String[] values;
        int num_values = 0;
        FieldFilter filter = null;
        FieldFilterConstraint constraint = null;

        values = request.getParameterValues("filter__" + m_field_id);

        // If we have filter values selected for this field and "All" is not selected.
        // "All" is the same as nothing selected, no filter.
        if ((values != null) && ((num_values = values.length) > 0) && !values[0].equals("All") && !values[0].equals("")) {
            filter = new FieldFilter(this);
            constraint = new FieldFilterConstraint(1);
            // SelectionListField only support the "=" operator.
            constraint.setOperator("=");

            for (int val = 0; val < num_values; val++) {
                constraint.add(values[val]);
            }

            filter.addConstraint(constraint);
            return filter;
        } else {
            return null;
        }
    }

    /**
     * Is the specified person in the field data list and should be selected in
     * the option list?
     * 
     * @param value the field domain value to check for.
     * @param field_data the data list that contains all the domain values to be
     * selected.
     * @return true if the value is in the field_data list, false otherwise.
     */
    private boolean isItemSelected(FieldDomainValue value, FieldData field_data) {
        if ((value == null) || (field_data == null)) {
            return false;
        }

        for (int i = 0; i < field_data.size(); i++) {
            if (value.getID().equals(field_data.get(i))) {
                return true;
            }
        }

        return false;
    }

    private boolean isFilterValueSelected(FieldDomainValue value, FieldFilter filter) {
        FieldFilterConstraint constraint = null;

        if ((value == null) || (filter == null)) {
            return false;
        }

        // for each field constraint
        for (int i = 0; i < filter.size(); i++) {
            constraint = filter.getConstraint(i);

            // for each value in the constraint
            for (int j = 0; j < constraint.size(); j++) {
                if (value.getID().equals(constraint.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Returns the XML Propeties for this SelectionListField. This includes the
     * domain list of selectable values.
     * 
     * @return xml the XML properties
     */
    public String getXMLProperties() {
        StringBuffer xml = new StringBuffer();

        xml.append(super.getXMLProperties());
        xml.append(getDomainXML());

        return xml.toString();
    }

    /**
     * Returns the XML Properties for this SelectionListField's domain of
     * values.
     * 
     * @return the XML domain list
     */
    private String getDomainXML() {
        StringBuffer xml = new StringBuffer();
        Iterator it = null;
        FieldDomainValue value = null;

        // Begin XML
        xml.append("<DomainValues>\n");

        // Build XML structure for domain values only if a domain
        // with at least one value is present
        if (getDomain() != null && getDomain().getValues() != null) {

            it = getDomain().getValues().iterator();
            while (it.hasNext()) {
                value = (FieldDomainValue) it.next();

                // Only add a domain value element if both the ID and name
                // are present
                if (value.getID() != null &&
                    value.getID().length() > 0 &&
                    value.getName() != null &&
                    value.getName().length() > 0) {

                    // Build XML Structure for a single domain value
                    xml.append("<DomainValue id=\"");
                    xml.append(value.getID());
                    xml.append("\">");
                    xml.append(XMLUtils.escape(value.getName()));
                    xml.append("</DomainValue>\n");

                }

            } //end while

        } //end if

        // Finish XML
        xml.append("</DomainValues>\n");


        return xml.toString();
    }

    /**
     * @return true/false depending upon whether it is selectable for display in
     *         the list.
     */
    public boolean isSelectable() {
        return true;
    }


//    FieldDomainValue getDomainValue(FieldData fieldData, int seq) {
//        (FieldDomainValue)m_domain.domainValues.get()
//    }

    /**
     * Generic field data comparison for single-value text fields. Specialized
     * form fields should overload this method. Compares the two field's first
     * data element only. Lexicographic compare is used.
     * 
     * @return 0 if equal, -1 if data1 < data2, 1 if data1 > data2, per contract
     *         of java.util.Comparator. @see java.util.Comparator
     */
    public int compareData(FieldData data1, FieldData data2) {
        //A map to the actual FieldDomainValues, not just their ID's
        Map dataValues = m_domain.getDomainValueMap();

        int currentSeqIndex = 0;
        while (true) {
            FieldDomainValue value1 = (FieldDomainValue) (data1.size() > currentSeqIndex ? dataValues.get(data1.get(currentSeqIndex)) : null);
            FieldDomainValue value2 = (FieldDomainValue) (data2.size() > currentSeqIndex ? dataValues.get(data2.get(currentSeqIndex)) : null);

            //First, handle null values
            if (value1 == null && value2 == null) {
                return 0;
            } else if (value1 == null) {
                return -1;
            } else if (value2 == null) {
                return 1;
            }

            //Now handle sequence numbers -- nothing should be null because of the
            //handling above.
            int seq1 = value1.getSequence();
            int seq2 = value2.getSequence();

            if (seq1 < seq2) {
                return -1;
            } else if (seq1 > seq2) {
                return 1;
            } else {
                currentSeqIndex++;
            }
        }
    }

}
