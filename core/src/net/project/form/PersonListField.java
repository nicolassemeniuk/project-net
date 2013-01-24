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
|     $RCSfile$
|    PersonListField.java
|    $Revision: 19849 $
|        $Date: 2009-08-25 07:05:28 -0300 (mar, 25 ago 2009) $
|      $Author: dpatil $
|                                                   
+----------------------------------------------------------------------*/
package net.project.form;

import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.resource.Person;
import net.project.resource.PersonList;
import net.project.resource.Roster;
import net.project.security.SessionManager;
import net.project.util.HTMLUtils;
import net.project.xml.XMLUtils;


/**
 * A form's person selection menu field.
 */
public class PersonListField extends FormField {
    public static final int DATA_COLUMN_SIZE = 20; // NUMBER(20)
    public static final String CURRENT_USER_MAGIC_NUMBER = "-2";


    /**
     * Construct a TextField.
     *
     * @param form the Form that this field belongs to.
     * @param field_id the id of the field in the database.
     */
    public PersonListField(Form form, String field_id) {
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
     * @return the database storage type for the field. i.e. NUMBER(20)
     */
    public String dbStorageType() {
        return (m_db_datatype + "(" + m_data_column_size + ")");
    }


    public String formatFieldData(FieldData field_data) {
        Roster roster = null;
        Person person = null;
        StringBuffer dataList = new StringBuffer();
        boolean personListed = false;

        roster = getRoster();
        person = roster.getPerson((String)field_data.get(0));

        // Person assigned to this field must have been removed from the roster.
        if (person != null) {
            // we have already got our person from the Roster 
            // So do nothing else do a lookup
        } else if ((person = roster.getAnyPerson((String)field_data.get(0))) == null && field_data.size() == 1) {
            return PropertyProvider.get("prm.form.designer.lists.create.filters.personlist.option.none.name");
        }

        // Build comma-separated list of people for multi selection lists.
        for (int i = 0; i < field_data.size(); i++) {
            StringBuffer currentFieldData = new StringBuffer();

            if (field_data.get(i) != null && (person = roster.getPerson((String)field_data.get(i))) != null && !person.getStatus().getID().equals("Deleted")) {
                String status = roster.getInvitationStatus(person.getID());

                if (status != null && status.equals("Rejected")) {
                    currentFieldData.append("<font color='#999999'><i>").append(person.getDisplayName()).append("</i></font>");
                } else {
                    currentFieldData.append(person.getDisplayName());
                }

            } else if (field_data.get(i) != null && (person = roster.getPerson((String)field_data.get(i))) != null && (person.getStatus().getID().equals("Deleted"))) {
                currentFieldData.append("<font color='#999999'><i>").append(person.getDisplayName()).append("</i></font>");
            } else if (field_data.get(i) != null && (person = roster.getAnyPerson((String)field_data.get(i))) != null) {
                currentFieldData.append("<font color='#999999'><i>").append(person.getDisplayName()).append("</i></font>");
            }

            //See if we are really adding a person to the list
            if (currentFieldData.length() > 0) {
                if (personListed == true) {
                    //This is not the first name we've added into the list.  Separate them with a comma.
                    currentFieldData.insert(0, ", ");
                } else {
                    personListed = true;
                }

                //Append this person's data to the list of person data
                dataList.append(currentFieldData.toString());
            }
        }

        return dataList.toString();
    }

    /**
     * Formats the field data and returns it in a form suitable for using
     * in a CSV file.
     *
     * @param field_data the field data to be formatted and returned.
     * @return a comma separated list representation of the field_data formatted
     * correctly for this type of field.
     */
    public String formatFieldDataCSV(FieldData field_data) {
        Roster roster = null;
        Person person = null;
        StringBuffer dataList = new StringBuffer();
        boolean surroundWithQuotes = field_data.size() > 1;

        roster = getRoster();
        person = roster.getPerson((String)field_data.get(0));



        // Person assigned to this field must have been removed from the roster.
        if (person != null) {
            // we have already got our person from the Roster 
            // So do nothing else do a lookup
        } else if ((person = roster.getAnyPerson((String)field_data.get(0))) == null && field_data.size() == 1) {
            return "\"" + PropertyProvider.get("prm.form.designer.lists.create.filters.personlist.option.none.name") + "\"";
        }

//        if (surroundWithQuotes == true) {
         //dataList.append("\"");
//        }

        // Build comma-separated list of people for multi selection lists.
        for (int i = 0; i < field_data.size(); i++) {
            if ((i >= 1) && (field_data.get(0) != null)) {
                dataList.append(", ");
            } else if (field_data.get(0) == null && i >= 2) {
                dataList.append(", ");
            }

            if (field_data.get(i) != null && (person = roster.getPerson((String)field_data.get(i))) != null && !person.getStatus().getID().equals("Deleted")) {
                dataList.append(person.getDisplayName());
            } else if (field_data.get(i) != null && (person = roster.getPerson((String)field_data.get(i))) != null && (person.getStatus().getID().equals("Deleted"))) {
                dataList.append(person.getDisplayName());
            } else if (field_data.get(i) != null && (person = roster.getAnyPerson((String)field_data.get(i))) != null) {
                dataList.append(person.getDisplayName());
            }
        }
//        if (surroundWithQuotes == true) {
//         dataList.append("\"");
//        }

        String dataString = dataList.toString().trim();
        if(dataString.length() > 0 ){
	        while (dataString.trim().length() > 0 && (dataString.lastIndexOf(",") == (dataString.length() -1))) {
	        	if(dataString.length() > 0){
	        		dataString = dataString.substring(0, dataString.length() - 1).trim();
	        	}else {
	        		dataString =  "";
	        	}
	        }
        }        
        
        return "\"" +  dataString + "\"" ;
    }

    /**
     * Outputs an HTML representation of this object to the specified jspWriter
     * stream. The field is encapulated within an html table td cell.
     */
    public void writeHtml(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        FormFieldProperty property = null;
        Roster roster = null;
        int num_properties;
        int prop_cnt;
        String defaultValue = null;

        // the label
        out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + ":&nbsp;&nbsp;</td>\n");
        out.print("<td align=\"left\" ");
        if (m_column_span > 1)
            out.print("colspan=\"" + ((m_column_span * 2) - 1) + "\"");
        out.print(">");

        // the field tag
        out.print("<select");

        // field name.  set name="fieldXXXX" in the tag.
        out.print(" name=\"" + m_data_column_name + "\" ");

        // TODO -- THIS SHOULD BE AN IN_TAG PROPERTY
        // does the field support multiple selection.
        if (isMultiSelect())
            out.print("multiple size=\"4\" ");

        if (isValueRequired) {
            out.print("required=\"true\" ");
            out.print("noneSelectedIndex=0 ");
        }

        // field properties (html tag attributes)
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
        out.print(">\n");


        // DEFAULT VALUE
        // Handle default value if there is not data for this field
        if ((field_data == null) || (field_data.size() < 1)) {
            if (useDefault()) {
                defaultValue = m_form.getUser().getID();
            } else
                defaultValue = "None";
        }

        // GET THE ROSTER
        // list of people to display in select
        roster = getRoster();

        out.print("<option value=\"\"");

        if ((defaultValue != null) && defaultValue.equals("None")) {
            out.print(" selected ");
        } else if (field_data != null && field_data.size() == 1 && field_data.contains(null)) {
            out.print(" selected ");
        }

        out.print(" >" + PropertyProvider.get("prm.form.designer.lists.create.filters.personlist.option.none.name") + "</option>\n");


        if (field_data != null) {
            out.println(roster.getSelectionList(field_data));
        } else if (useDefault() && field_data == null) {
            out.println(roster.getSelectionList(m_form.getUser().getID()));
        } else {
            out.println(roster.getSelectionList(""));
        }

        out.print("</select>\n");

        // Add appropriate script for indicating field is required
        if (isValueRequired()) {
            out.println(getRequiredValueJavascript());
        }

        out.print("</td>\n");
    }


    /**
     * Outputs an disabled(since it can not be made Read-Only) HTML
     * representation of this object to the specified jspWriter stream. The
     * field is encapulated within an html table td cell.
     */
    public void writeHtmlReadOnly(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        FormFieldProperty property = null;
        Roster roster = null;
        int num_properties;
        int prop_cnt;
        String defaultValue = null;

        // the label
        out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + ":&nbsp;&nbsp;</td>\n");
        out.print("<td align=\"left\" ");
        if (m_column_span > 1)
            out.print("colspan=\"" + ((m_column_span * 2) - 1) + "\"");
        out.print(">");

        // the field tag
        out.print("<select");
        //This field can not be made read-only hence make it disabled
        out.print(" " + "disabled" + " ");

        // field name.  set name="fieldXXXX" in the tag.
        out.print(" name=\"" + m_data_column_name + "\" ");

        // TODO -- THIS SHOULD BE AN IN_TAG PROPERTY
        // does the field support multiple selection.
        if (isMultiSelect())
            out.print("multiple size=\"4\" ");

        // field properties (html tag attributes)
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
        out.print(">\n");

        // GET THE ROSTER
        // list of people to display in select
        roster = getRoster();

        out.print("<option value=\"\"");

        if ((defaultValue != null) && defaultValue.equals("None")) {
            out.print(" selected ");
        } else if (field_data != null && field_data.size() == 1 && field_data.contains(null)) {
            out.print(" selected ");
        }

        out.print(" >" + PropertyProvider.get("prm.form.designer.lists.create.filters.personlist.option.none.name") + "</option>\n");


        if (field_data != null) {
            out.println(roster.getSelectionList(field_data));
        } else if (useDefault() && field_data == null) {
            out.println(roster.getSelectionList(m_form.getUser().getID()));
        } else {
            out.println(roster.getSelectionList(""));
        }

        out.print("</select>\n");
        out.print("</td>\n");
    }


    public String writeHtmlReadOnly(FieldData field_data){
    	StringBuffer fieldHtml = new StringBuffer();
	    Roster roster = null;
	    String defaultValue = null;
	
	    // the label
	    fieldHtml.append("<td align=\"left\" width=\"1%\" class=\"tableHeader\">" + HTMLUtils.escape(m_field_label) + ": </td>");
	    fieldHtml.append("<td align=\"left\" class=\"tableContent\">");
		
	    // GET THE ROSTER
	    // list of people to display in select
	    roster = getRoster();
	
	    if ((defaultValue != null) && defaultValue.equals("None")) {
	    	fieldHtml.append(" selected ");
	    } else if (field_data != null && field_data.size() == 1 && field_data.contains(null)) {
	    	fieldHtml.append(" selected ");
	    }	
		
	    String option =  "";
	    if (field_data != null) {
	    	option = roster.getSelectionList(field_data);
	    } else if (useDefault() && field_data == null) {
	    	option = roster.getSelectionList(m_form.getUser().getID());
	    } else {
	    	option = roster.getSelectionList("");
	    }
	    
    	String[] options = option.split("</option>");
    	StringBuffer selectedUsers = new StringBuffer();
    	for(int i=0; i< options.length; i++){
    	   if (options[i].contains("selected")){
    		   selectedUsers.append(options[i].substring(options[i].indexOf(">")+1)).append(", ");
    	   }
    	}
	    if (selectedUsers.toString().length() == 0){
	    	fieldHtml.append(PropertyProvider.get("prm.form.designer.lists.create.filters.personlist.option.none.name"));
	    }else{
	    	fieldHtml.append(selectedUsers.toString().substring(0, selectedUsers.toString().length()-2));
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
        FormFieldProperty property = null;
        Roster roster = null;
        int num_properties;
        int prop_cnt;
        String defaultValues = null;

        // the label
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + "</td>");
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
        out.print(">\n");


        // Create a list of the default values
        StringBuffer defaultValuesBuffer = new StringBuffer();

        if (filter != null) {
            Iterator it = filter.getConstraintsIterator();

            while (it.hasNext()) {
                FieldFilterConstraint constraint = (FieldFilterConstraint)it.next();

                Iterator it2 = constraint.iterator();
                while (it2.hasNext()) {
                    defaultValuesBuffer.append((defaultValuesBuffer.length() > 0 ? "," : "")).
                        append((String)it2.next());
                }
            }

        }

        if (defaultValuesBuffer.length() == 0) {
            defaultValuesBuffer.append("All");
        }

        defaultValues = defaultValuesBuffer.toString();

        // GET THE ROSTER
        // list of people to display in select
        roster = getRoster();


        // OPTION LIST
        // write option list for all people on the roster.

        // write the "None" option
        out.print("<option value=\"\"");
        if ((defaultValues != null) && defaultValues.equals("All"))
            out.print(" selected");
        out.print(">" + PropertyProvider.get("prm.form.designer.lists.create.filters.personlist.option.all.name") + "</option>\n");

        // write the "Current user" option
        out.print("<option value=\""+CURRENT_USER_MAGIC_NUMBER + "\"");
        if (defaultValues != null && defaultValues.indexOf(CURRENT_USER_MAGIC_NUMBER) != -1)
            out.print(" selected>");
        else
            out.print(">");
        out.print(PropertyProvider.get("prm.form.designer.lists.create.filters.personlist.option.currentuser.name"));
        out.print("</option>\n");

        if(filter != null)
            out.println(roster.getSelectionList(defaultValues));
        else
            out.println(roster.getSelectionList(""));

        out.print("</select>\n");
        out.print("</td>\n");

    }


    /**
     * Process the HTTP request to extract the filter selections for this field.
     *
     * @return the FieldFilter containing the filter information for this field,
     * null if their was no filter information for this field in the request.
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
            // PersonListField only support the "=" operator.
            constraint.setOperator("=");

            for (int val = 0; val < num_values; val++) {
                constraint.add(values[val]);
            }

            filter.addConstraint(constraint);
            return filter;
        } else
            return null;
    }


    /**
     * Compare data from two PersonListFields. Compares the two person fields by
     * full name. Case-insensitive lexicographic compare is used. null fields
     * are considered "greater than" defined fields.
     *
     * @return 0 if equal, -1 if data1 < data2, 1 if data1 > data2, per contract
     * of java.util.Comparator.
     * @see java.util.Comparator
     */
    public int compareData(FieldData data1, FieldData data2) {
        String person1, person2;
        int compareValue;
        int data1Size, data2Size;

        data1Size = data1.size();
        data2Size = data2.size();

        for (int i = 0; i < data1Size; i++) {
            // end of data2 list, data1 > data2
            if (data2Size <= i)
                return 1;

            // field data may contain null values
            if ((data1.get(i) == null) || (data2.get(i) == null)) {
                // data1 ith datam is null, data1 > data2
                if (data1.get(i) == null)
                    return 1;
                // data2 ith datam is null, data1 < data2
                else
                    return -1;
            }
            // normal compare of full names.
            else {
                person1 = getRoster().getAnyPerson(((String)data1.get(i))).getDisplayName().toLowerCase();
                person2 = getRoster().getAnyPerson(((String)data2.get(i))).getDisplayName().toLowerCase();

                // if the ith person is different, return the comparison, otherwise go on to the next person.
                if ((compareValue = person1.compareTo(person2)) != 0)
                    return compareValue;
            }
        }

        // if data2 still has more values, data1 < data2.
        // otherwise the fields are identical.
        if (data2Size > data1Size)
            return -1;
        else
            return 0;
    }


    /**
     * Get the roster of people for the current space.
     */
    private Roster getRoster() {
        return m_form.getSpace().getRoster();
    }

    /**
     * Returns the XML Propeties for this PersonListField.
     * This includes the domain list of selectable values.
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
     * Returns the XML Properties for this PersonListField's domain of values.
     *
     * @return the XML domain list
     */
    private String getDomainXML() {
        StringBuffer xml = new StringBuffer();
        Iterator it = null;
        Person person = null;

        // Begin XML
        xml.append("<DomainValues>\n");

        it = getRoster().iterator();
        while (it.hasNext()) {
            person = (Person)it.next();

            // Build XML Structure for a single domain value
            xml.append("<DomainValue id=\"");
            xml.append(person.getID());
            xml.append("\">");
            xml.append(XMLUtils.escape(person.getDisplayName()));
            xml.append("</DomainValue>\n");

        }

        // Finish XML
        xml.append("</DomainValues>\n");

        return xml.toString();
    }

    /**
     * Returns the PersonList for this PersonListField's domain of values.
     *
     * @return the Personlist
     */

    public PersonList getPersonList() {
        return getRoster();
    }

    /**
     * @return true/false depending upon whether it is selectable for display
     * in the list.
     */
    public boolean isSelectable() {
        return true;
    }

    /**
     * Return an SQL representation of the field filter to be used in a WHERE
     * clause.
     *
     * The PersonList variety of this method differs from the standard FormField
     * version because it needs to be aware of the "current user" field type,
     * which it needs to change dynamically right before SQL is run.
     *
     * @param filter the filter values to use in generating the SQL.
     * @param joinOperator String containing the boolean operator to join this
     * field filter to the preceeding filter. "and" or "or".
     */
    public String getFilterSQL(FieldFilter filter, String joinOperator) {
        FieldFilterConstraint constraint = null;
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

                    String constraintValue = (String)constraint.get(j);
                    if (constraintValue.equals(CURRENT_USER_MAGIC_NUMBER)) {
                        constraintValue = SessionManager.getUser().getID();
                    }
                    sb.append("(" + this.getDataColumnName() + " " + constraint.getOperator() + " " + constraintValue + ")");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }
}



