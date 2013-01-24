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

public class CreatorUserField extends MetaDataUserField {
	
	/**
	 * @param form the Form that this field belongs to.
	 * @param field_id the id of the field in the database.
	 */
	public CreatorUserField(Form form, String field_id) {
		super(form, field_id);
		m_data_column_size = DATA_COLUMN_SIZE;
	}


    protected String getSQLSelectColumn() {
        return m_form.m_master_table_name + ".create_person_id ";
    }    
    
    public String getSQLName() {
    	return m_form.m_master_table_name + ".create_person_id ";
    }    


}
