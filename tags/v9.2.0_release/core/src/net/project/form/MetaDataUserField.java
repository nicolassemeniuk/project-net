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

import net.project.base.property.PropertyProvider;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.security.SessionManager;

public abstract class MetaDataUserField extends PersonListField {
	
	public static final int DATA_COLUMN_SIZE = 0; // static field, no storage.

	/**
	 * @param form the Form that this field belongs to.
	 * @param field_id the id of the field in the database.
	 */
	public MetaDataUserField(Form form, String field_id) {
		super(form, field_id);
		m_data_column_size = DATA_COLUMN_SIZE;
	}

	/**
	 * @return the database storage type for the field; this field type is
	 * static and does not store data.
	 */
	public String dbStorageType() {
		return null;
	}

    public boolean isDesignable(){
    	return false;
    }	
    
	/**
	 * "Assigned User" is exportable field
	 */
    public boolean isExportable() {
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
     * field filter to the preceding filter. "and" or "or".
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
                    sb.append("( "+  getSQLSelectColumn() + " " + constraint.getOperator() + " " + constraintValue + ")");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }    

    protected abstract String getSQLSelectColumn();
    
    public abstract String getSQLName();
        
        
    public String formatFieldDataCSV(FieldData field_data) {
    	return formatFieldData(field_data);
    }
    
    public String formatFieldData(FieldData field_data) {
        Roster roster = null;
        Person person = null;

        roster = m_form.getSpace().getRoster();
        person = roster.getPerson((String)field_data.get(0));

        // Person assigned to this field must have been removed from the roster.
        if (person != null) {
            // we have already got our person from the Roster 
            // So do nothing else do a lookup
        } else if ((person = roster.getAnyPerson((String)field_data.get(0))) == null && field_data.size() == 1) {
            return PropertyProvider.get("prm.form.designer.lists.create.filters.personlist.option.none.name");
        }

       
        String dataString = "";
        if (field_data.size() > 0){
            if (field_data.get(0) != null && 
            		(person = roster.getPerson((String)field_data.get(0))) != null && !person.getStatus().getID().equals("Deleted")) {
            	dataString =  person.getDisplayName() ;        	
            }
        }

        return dataString;
           
    } 	
	

}
