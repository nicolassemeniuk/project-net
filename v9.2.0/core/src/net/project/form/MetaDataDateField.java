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

import net.project.security.SessionManager;

import org.apache.log4j.Logger;

public abstract class MetaDataDateField extends DateField{

	public static final int DATA_COLUMN_SIZE = 0; // static field, no storage.

	/**
	 * @param form the Form that this field belongs to.
	 * @param field_id the id of the field in the database.
	 */
	public MetaDataDateField(Form form, String field_id) {
		super(form, field_id);
		m_data_column_size = DATA_COLUMN_SIZE;
	//	m_dateFormatter = new net.project.util.DateFormat(SessionManager.getUser());
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
	 * "MetaDataDateField User" is exportable field
	 */
    public boolean isExportable() {
        return true;
    }	
	
    protected String getSQLSelectColumn(){
    	return ("TO_CHAR(" + getSQLName() + ", '" + DATABASE_DATETIME_FORMAT_PATTERN + "')");
    }
    public abstract String getSQLName();    
    
    public void loadProperties(){
    	
    }

    public void processHttpPost(javax.servlet.ServletRequest request, FieldData fieldData){
    	
    }
    
    public boolean isValidFieldData(FieldData data, StringBuffer errorValue, StringBuffer errorMessagePatternBuffer){
    	return true;
    }
    
    public void processFieldData(FieldData data){
    	
    }
 
    public String formatFieldData(FieldData field_data) {
        String formatString = null;
        String dateString = null;
        
        // display nothing if field data is null
        //if (field_data == null && field_data.get(0) == null)
        if (field_data == null || field_data.get(0) == null) {
            return "";
        }
        java.util.Date tmpDate = null;
        // parse the date string into a date object so we can format it various ways
        try {
            tmpDate = m_dateFormatter.parseDateString((String)field_data.get(0), "M/d/yyyy");

        } catch (net.project.util.InvalidDateException ide) {
        	Logger.getLogger(DateField.class).debug("DateField.formatFieldData: InvalidDateException: " + ide);
            tmpDate = null;
        }
        // coudn't parse date. 
        if (tmpDate == null)
            return "";

        formatString = SessionManager.getUser().getDateFormat();
        dateString = m_dateFormatter.formatDate(tmpDate, formatString);
        return dateString;
    }    
    
    /**
     * Return an SQL representation of the field filter to be used in a WHERE
     * clause.
     *
     * @param filter the filter values to use in generating the SQL.
     * @param joinOperator String containing the boolean operator to join this
     * field filter to the preceeding filter. "and" or "or".
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
            if(i>0)
            	sb.append(" and ");
            for (int j = 0; j < num_values; j++) {
                if ((constraint.get(j) != null) && !(constraint.get(j)).equals("")) {
                    if (j != 0)
                        sb.append(" or ");
                    if(constraint.getOperator().equals("<"))
                    	sb.append("(" + this.getSQLName() + " " + constraint.getOperator() + " (TO_DATE('" + constraint.get(j) + "', 'MM/DD/YYYY HH24:MI') + rownum +1))");
                    else
                    	sb.append("(" + this.getSQLName() + " " + constraint.getOperator() + " TO_DATE('" + constraint.get(j) + "', 'MM/DD/YYYY HH24:MI'))");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }
        
    
}
