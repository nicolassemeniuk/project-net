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

import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleFinder;
import net.project.util.HTMLUtils;

/**
 * A form's milestone selection menu field.
 */
public class MilestoneListField extends FormField {
    public static final int DATA_COLUMN_SIZE = 20; // NUMBER(20)


    /**
     * Construct a single Milestone field.
     *
     * @param form the Form that this field belongs to.
     * @param field_id the id of the field in the database.
     */
    public MilestoneListField(Form form, String field_id) {
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
        Schedule schedule = null;
        ScheduleEntry task = null;

        schedule = getSchedule();
        
        if (schedule != null) {
        	task = schedule.getEntry((String)field_data.get(0));
        }

        // Get the Milestone.
        // If the milestone has been deleted or no milestones exist, this will return null.
        if (task == null)
            return "None";
        else
            return task.getName();
    }


    /**
     * Outputs an HTML representation of this object to the specified jspWriter
     * stream.
     */
    public void writeHtml(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        FormFieldProperty property = null;
        int p;
        String data_value = "";
        int num_properties;
        int prop_cnt;
        String selected = null;

        Schedule schedule = null;
        ScheduleEntry task = null;
        List scheduleEntries = null;


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

        if (isValueRequired) {
            out.print("required=\"true\" ");
            out.print("noneSelectedIndex=0 ");
        }

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
        out.print(">\n");

        // write the data value in the field if there is one.
        if (field_data != null) {
            // the Options tags
            // get the "selected" person_id from the form's data.  If one is not set, use the current user's person_id
            try {
                data_value = (String)field_data.get(0);        // only one value for textare field
            } catch (IndexOutOfBoundsException e) {
                data_value = null;
            }
        }
        // which option is selected in the list
        if ((data_value != null) && !data_value.equals(""))
            selected = data_value;

        // write the None option
        out.print("<option value=\"\">" + PropertyProvider.get("prm.form.designer.lists.create.filters.milestonelist.option.none.name") + "</option>\n");

        schedule = getSchedule();
        
        if (schedule != null) {
            scheduleEntries = schedule.getEntries();        	
        }

        // write option list for all milestones.
        if ((scheduleEntries != null) && (scheduleEntries.size() > 0)) {
            for (p = 0; p < scheduleEntries.size(); p++) {
                task = (ScheduleEntry)scheduleEntries.get(p);
                out.print("<option value=\"" + task.getID() + "\"");

                if ((selected != null) && task.getID().equals(selected))
                    out.print(" selected>");
                else
                    out.print(">");

                out.print(HTMLUtils.escape(task.getNameMaxLength40()));
                out.print("</option>\n");
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
     * Outputs a disabled HTML representation of this object to the specified
     * jspWriter stream.
     */
    public void writeHtmlReadOnly(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        FormFieldProperty property = null;
        int p;
        String data_value = "";
        int num_properties;
        int prop_cnt;
        String selected = null;

        Schedule schedule = null;
        ScheduleEntry task = null;
        List scheduleEntries = null;


        // the label
        out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + ":&nbsp;&nbsp;</td>\n");
        out.print("<td align=\"left\" ");
        if (m_column_span > 1)
            out.print("colspan=\"" + ((m_column_span * 2) - 1) + "\"");

        out.print(">");

        // the field tag
        out.print("<select");
        //Make this field disabled(it can not be made readonly)
        out.print(" " + "disabled" + " ");

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
        out.print(">\n");

        // write the data value in the field if there is one.
        if (field_data != null) {
            // the Options tags
            // get the "selected" person_id from the form's data.  If one is not set, use the current user's person_id
            try {
                data_value = (String)field_data.get(0);        // only one value for textare field
            } catch (IndexOutOfBoundsException e) {
                data_value = null;
            }
        }
        // which option is selected in the list
        if ((data_value != null) && !data_value.equals(""))
            selected = data_value;

        // write the None option
        out.print("<option value=\"\">" + PropertyProvider.get("prm.form.designer.lists.create.filters.milestonelist.option.none.name") + "</option>\n");

        schedule = getSchedule();
        
        if (schedule != null) {
            scheduleEntries = schedule.getEntries();        	
        }
        
        // write option list for all milestones.
        if ((scheduleEntries != null) && (scheduleEntries.size() > 0)) {
            for (p = 0; p < scheduleEntries.size(); p++) {
                task = (ScheduleEntry)scheduleEntries.get(p);
                out.print("<option value=\"" + task.getID() + "\"");

                if ((selected != null) && task.getID().equals(selected))
                    out.print(" selected>");
                else
                    out.print(">");

                out.print(HTMLUtils.escape(task.getName()));
                out.print("</option>\n");
            }
        }
        out.print("</select>\n");
        out.print("</td>\n");
    }

    public String writeHtmlReadOnly(FieldData field_data){
    	StringBuffer fieldHtml = new StringBuffer();
	    int p;
	    String data_value = "";
	    String selected = null;
	    Schedule schedule = null;
	    ScheduleEntry task = null;
	    List scheduleEntries = null;
		
	    // the label
	    fieldHtml.append("<td align=\"left\" width=\"1%\" class=\"tableHeader\">" + HTMLUtils.escape(m_field_label) + ": </td>");
	    fieldHtml.append("<td align=\"left\" class=\"tableContent\">");
		
	    // write the data value in the field if there is one.
	    if (field_data != null) {
	        try {
	            data_value = (String)field_data.get(0);
	        } catch (IndexOutOfBoundsException e) {
	            data_value = null;
	        }
	    }
	    // which option is selected in the list
	    if ((data_value != null) && !data_value.equals(""))
	        selected = data_value;
	
	    // write the None option
	    //fieldHtml.append("<option value=\"\">" + PropertyProvider.get("prm.form.designer.lists.create.filters.milestonelist.option.none.name") + "</option>\n");
	
	    schedule = getSchedule();	    
	    if (schedule != null) {
	        scheduleEntries = schedule.getEntries();        	
	    }	    
	    // write option list for all milestones.
	    if ((scheduleEntries != null) && (scheduleEntries.size() > 0)) {
	        for (p = 0; p < scheduleEntries.size(); p++) {
	            task = (ScheduleEntry)scheduleEntries.get(p);
	            if ((selected != null) && task.getID().equals(selected))
	            	fieldHtml.append(HTMLUtils.escape(task.getName()));
	        }
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
        Schedule schedule = null;
        ScheduleEntry task = null;
        List scheduleEntries = null;
        String defaultValue = null;

        // the label
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + "</td>");
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\"> &nbsp;&nbsp; = &nbsp;&nbsp; </td>");
        out.println("<td align=\"left\">");

        // the field tag
        out.print("<select name=\"filter__" + m_field_id + "\" ");

        // filters use multiple selection.
        out.println("multiple size=\"4\">");

        // DEFAULT VALUE
        // Handle default value if there are not filter values for this field
        if ((filter == null) || (filter.size() < 1))
            defaultValue = "All";
        else
            defaultValue = null;

        // OPTION LIST
        // write option list for all milestones on the schedule.

        // write the "None" option
        out.print("<option value=\"\"");
        if ((defaultValue != null) && defaultValue.equals("All"))
            out.print(" selected");
        out.print(">" + PropertyProvider.get("prm.form.designer.lists.create.filters.milestonelist.option.all.name") + "</option>\n");

        schedule = getSchedule();
        if (schedule != null) {
        	scheduleEntries = schedule.getEntries();        	
        }

        // write option list for all milestones.
        if ((scheduleEntries != null) && (scheduleEntries.size() > 0)) {
            for (int t = 0; t < scheduleEntries.size(); t++) {
                task = (ScheduleEntry)scheduleEntries.get(t);
                out.print("<option value=\"" + task.getID() + "\"");

                // if this milestone is selected
                if ((defaultValue == null) && isFilterValueSelected(task, filter))
                    out.print(" selected>");
                else
                    out.print(">");

                out.print(HTMLUtils.escape(task.getName()));
                out.print("</option>\n");
            }
        }
        out.print("</select>\n");
        out.print("</td>\n");
    }


    /**
     * Process the HTTP request to extract the filter selections for this field.
     *
     * @return the FieldFilter containing the filter information for the field,
     * null if their was no filter information for the field in the request.
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
            // MilestoneListField only support the "=" operator.
            constraint.setOperator("=");

            for (int val = 0; val < num_values; val++)
                constraint.add(values[val]);

            filter.addConstraint(constraint);
            return filter;
        } else
            return null;
    }


    private boolean isFilterValueSelected(ScheduleEntry value, FieldFilter filter) {
        FieldFilterConstraint constraint = null;

        if ((value == null) || (filter == null))
            return false;

        // for each field constraint
        for (int i = 0; i < filter.size(); i++) {
            constraint = filter.getConstraint(i);

            // for each value in the constraint
            for (int j = 0; j < constraint.size(); j++) {
                if (value.getID().equals(constraint.get(j)))
                    return true;
            }
        }

        return false;
    }


    private Schedule getSchedule() {
    	List schedulesList = null; 
        Schedule schedule = null;
        try {
            ScheduleFinder finder = new ScheduleFinder();
            finder.setTypesToLoad(ScheduleFinder.LOAD_MILESTONES);
            finder.setLoadTaskAssignments(false);
            finder.setLoadTaskDependencies(false);
            // Changed so in case of Personal Workspace (e.g. not having Schedule) no exception thrown
            schedulesList = finder.findBySpaceID(m_form.getOwningSpaceID());
            if (!schedulesList.isEmpty() ) {
            	schedule = (Schedule) schedulesList.get(0);
            }
        } catch (PersistenceException pe) {
            // No schedule entries.
        }

        return schedule;
    }

    public List getMilestoneList() {
    	Schedule schedule = getSchedule();
    	if (schedule != null) {
    		return schedule.getEntries();	
    	} else {
    		return null;
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


