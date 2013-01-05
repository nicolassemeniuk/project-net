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
package net.project.form.soa;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;

import net.project.base.ObjectType;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.database.ObjectManager;
import net.project.form.Form;
import net.project.form.soa.binding.DomainValue;
import net.project.form.soa.binding.FieldFilter;
import net.project.form.soa.binding.FieldPropertyValue;
import net.project.form.soa.binding.FormDef;
import net.project.form.soa.binding.FormField;
import net.project.form.soa.binding.FormList;
import net.project.form.soa.binding.ListField;
import net.project.persistence.PersistenceException;
import net.project.util.Conversion;

public class FormServiceImpl {

	public void storeFormData(FormDef formDef, int userId, int spaceId)
			throws PersistenceException {

		DBBean dbean = new DBBean();

		try {
			dbean.setAutoCommit(false);
			storeImportedDesign(dbean, formDef, String.valueOf(userId), String.valueOf(spaceId));
			dbean.commit();
			dbean.release();

		} catch (Exception e) {
			throw new PersistenceException("Import of Form design failed because of " + e, e);

		} finally {
			if (dbean.connection != null) {
				try {
					dbean.rollback();
				} catch (SQLException sqle2) {
					// Continue to release()
				}
			}
			dbean.release();
		} //end try		

	}

	private void storeImportedDesign(DBBean db, FormDef formDef, String userId,
			String spaceId) throws PersistenceException {

		HashMap<BigInteger, String> fieldIds = new HashMap<BigInteger, String>();
		HashMap<String, String> domainValueIds = new HashMap<String, String>();
		
		
		java.sql.Timestamp newCrc = new java.sql.Timestamp(new java.util.Date().getTime());
		// get new id and register in the pn_object table.
		String classId = ObjectManager.dbCreateObjectWithPermissions(ObjectType.FORM, "A", spaceId, userId);

		try {
			db.openConnection();

			db.prepareStatement("insert into pn_class (class_id, class_name, class_type_id, "
							+ "  class_desc, class_abbreviation, owner_space_id,  "
							+ "  max_row, max_column, next_data_seq, record_status, "
							+ "  crc, supports_discussion_group, supports_document_vault, supports_assignment) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			db.pstmt.setString(1, classId);
			db.pstmt.setString(2, formDef.getName());
			//db.pstmt.setString(3, formDef.getClassTypeId().toString());
			db.pstmt.setString(3, "100");
			db.pstmt.setString(4, formDef.getDescription());
			db.pstmt.setString(5, formDef.getAbbreviation());
			db.pstmt.setString(6, spaceId);

			db.pstmt.setInt(7, 0);
			db.pstmt.setInt(8, 0);
			db.pstmt.setInt(9, 1);
			db.pstmt.setString(10, "P");
			db.pstmt.setTimestamp(11, newCrc);
			db.pstmt.setBoolean(12, false);
			db.pstmt.setBoolean(13, false);
			db.pstmt.setBoolean(14, formDef.isIncludeAssignments());
			db.executePrepared();

			db.commit();
			db.release();

			db.executeQuery("insert into pn_space_has_class (space_id, class_id, is_owner) "
							+ "values ("+ spaceId +  ", " +	 classId + ", " + Conversion.booleanToInt(true) + ") ");
			db.release();
			
			String sql;
			//insert fields
			for (FormField field : formDef.getFormFields().getFormField()) {
				String domainId = null;
				
				if (field.getDomainValues() != null) {
					domainId = ObjectManager.dbCreateObjectWithPermissions(ObjectType.FORM_DOMAIN, "A", spaceId, userId);
					sql = "insert into pn_class_domain (domain_id, record_status) values ("+ domainId + ",  'A')";
					db.executeQuery(sql);
					db.release();

					int seqence = 1;
					for (DomainValue domainValue : field.getDomainValues().getDomainValue()) {
						String domainValueId = ObjectManager.dbCreateObject(ObjectType.FORM_DOMAIN_VALUE, "A");
						
						domainValueIds.put(domainValue.getId().toString(), domainValueId);
						
						// update the FieldDomain properties
						sql = "insert into pn_class_domain_values (domain_id, domain_value_id, domain_value_name, domain_value_seq, is_default, record_status) values ("
								+ domainId	+ ", " 	+ domainValueId + ", " 	+ DBFormat.varchar2(domainValue.getContent()) + ", " + String.valueOf(seqence) + ", 0, 'A' )";
						db.executeQuery(sql);
						db.release();
						seqence = seqence + 1;
					}
				}
		
				java.sql.Timestamp newFieldCrc = new java.sql.Timestamp(new java.util.Date().getTime());
				String fieldId = ObjectManager.dbCreateObjectWithPermissions(ObjectType.FORM_FIELD, "A", spaceId, userId);
				fieldIds.put(field.getId(), fieldId);
				
				String columnNum = "";
				String columnSpan = "";
				String columnId = "";
				if (field.getColumn() != null && field.getColumn().trim().length() > 0){
					if (field.getColumn().equalsIgnoreCase("LEFT")){
						columnNum = "1";
						columnSpan = "1";
						columnId = DBFormat.number(Form.LEFT_COLUMN);						
					} else if (field.getColumn().equalsIgnoreCase("BOTH")){
						columnNum = "1";
						columnSpan = "2";
						columnId = DBFormat.number(Form.BOTH_COLUMNS);												
					} else {
						columnNum = "2";
						columnSpan = "1";
						columnId = DBFormat.number(Form.RIGHT_COLUMN);						
					}
				}else {
					columnNum = field.getColumnNum().toString();
					columnSpan = field.getColumnSpan().toString();
					columnId = DBFormat.number(field.getColumnId().toString());
				}
				
	            sql  = "insert into pn_class_field (class_id, field_id, space_id, element_id, field_label, " +
	            		" data_column_size, data_column_scale, " +
	            		"data_column_exists, row_num, row_span, column_num, column_span, " +
	            		" field_group, instructions_clob, is_multi_select, use_default, " +
	            		" column_id, is_value_required ";
	            if (domainId != null)
	            	sql += " , domain_id";
	            sql  += " , crc, record_status, max_value, min_value, default_value) " +
	            	"values (" + classId + "," + fieldId + "," + spaceId + ", " + field.getElementId().toString() +  "," 
	            			+ DBFormat.varchar2(field.getLabel()) + "," + 
	            	  field.getDataColumnSize()  + "," + field.getDataColumnScale() + "," + 
	            	 DBFormat.bool(false) + ", " + field.getRowNum().toString() + "," + field.getRowSpan().toString() +
	            	"," + columnNum + "," + columnSpan + "," + DBFormat.varchar2(field.getFieldGroup()) + ", empty_clob() " +
	            	"," + DBFormat.bool(field.isMultiselect()) + "," + DBFormat.bool(field.isUseDefault()) + "," + columnId + "," + DBFormat.bool(field.isRequired());

	            if (domainId != null)
                sql += "," + domainId ;

	            sql += ", " + DBFormat.crc(newFieldCrc) + ", 'P'," + DBFormat.varchar2(field.getMaxValue()) + "," + DBFormat.varchar2(field.getMinValue()) + "," + DBFormat.varchar2(field.getDefaultValue()) + ")";
				
				db.executeQuery(sql);
				
			    // Now stream the instructions
	            // First we have to select the newly inserted row in order to
	            // get the clob locater for the empty_clob() that was inserted
	            // by the previous statement
	            StringBuffer selectQuery = new StringBuffer();
	            selectQuery.append("select instructions_clob from pn_class_field ");
	            selectQuery.append("where space_id = ? and class_id = ? and field_id = ? ");
	            selectQuery.append("for update nowait");

	            int index = 0;
	            db.prepareStatement(selectQuery.toString());
	            db.pstmt.setString(++index, spaceId);
	            db.pstmt.setString(++index, classId);
	            db.pstmt.setString(++index, fieldId);
	            db.executePrepared();

	            if (db.result.next()) {
	                ClobHelper.write(db.result.getClob("instructions_clob"), field.getInstruction());
	            } else {
	                throw new PersistenceException("Error updating instructions clob.  Inserted or updated field record not found.");

	            }

	            db.commit();
	            db.release();	
				
				if (field.getFieldPropertyValues() != null && field.getFieldPropertyValues().getFieldPropertyValue() != null && 
						field.getFieldPropertyValues().getFieldPropertyValue().size() > 0){
					
					for (FieldPropertyValue property : field.getFieldPropertyValues().getFieldPropertyValue()){
						boolean isDeafultValueProperty = property.getPropertyName().equals("DefaultValue");
						String propertyValue = isDeafultValueProperty ? domainValueIds.get(property.getPropertyValue()) : property.getPropertyValue();  
	                    sql = "insert into pn_class_field_property (class_id, field_id, client_type_id, property_type, property, value) " +
                        "values (" + classId + "," + fieldId + "," + property.getClientTypeId() + "," + DBFormat.varchar2(property.getPropertyType()) + "," +
                        DBFormat.varchar2(property.getPropertyName()) + "," + DBFormat.varchar2(propertyValue) + ")";
	    				db.executeQuery(sql);
	    				db.release();	                    
	    				
	    				if (isDeafultValueProperty){
		                    sql = "update pn_class_domain_values set is_default = 1 where domain_value_id = " + domainValueIds.get(property.getPropertyValue()); 
		    				db.executeQuery(sql);
		    				db.release();	    					
	    				}
					}
				}				
			}

			for (FormField field : formDef.getFormFields().getFormField()) {				
				if (field.getElementId().intValue() == 100 &&  field.getFieldPropertyValues() != null && field.getFieldPropertyValues().getFieldPropertyValue() != null && 
						field.getFieldPropertyValues().getFieldPropertyValue().size() > 0){
					
					HashMap<Integer, String> calcElements = new HashMap<Integer, String>();
					
					for (FieldPropertyValue property : field.getFieldPropertyValues().getFieldPropertyValue()){												
						if( GenericValidator.isInt(property.getPropertyValue())) {
							BigInteger newFieldId = new BigInteger(property.getPropertyValue());
							if (fieldIds.get(newFieldId) != null){
			                    sql = "update pn_class_field_property set value = "+ DBFormat.varchar2(fieldIds.get(newFieldId)) + 
			                    		" where field_id = " + fieldIds.get(field.getId()) + " AND value = " +
			                    		DBFormat.varchar2(property.getPropertyValue());
			    				db.executeQuery(sql);
			    				db.release();							
							}
						}
						
						if (GenericValidator.isInt(property.getPropertyName())){
							Integer calcOrder = Integer.valueOf(property.getPropertyName());
							if(calcOrder >= 100){
								if( GenericValidator.isInt(property.getPropertyValue())) {
									BigInteger newFieldId = new BigInteger(property.getPropertyValue());
									calcElements.put(calcOrder, fieldIds.get(newFieldId));
								}else{
									calcElements.put(calcOrder, property.getPropertyValue());
								}
							}
						}						
					}
					
					int calcSize = calcElements.size();
					for(int idx = 0; idx <calcSize; idx++){
					String value = calcElements.get(100+ idx);
					if (idx % 2 == 1){
						if(value.equals("101_1")){
							value = "+";
						}if(value.equals("101_2")){
							value = "-";
						}if(value.equals("101_3")){
							value = "*";
						}if(value.equals("101_4")){
							value = "/";
						}
					}
					String elementType = idx % 2 ==  0 ? "operand" : "operator";
		                sql = "insert into pn_calculation_field_formula (class_id, field_id, order_id, op_value, op_type)" +
                        "values(" + classId + "," + fieldIds.get(field.getId()) + "," + String.valueOf(idx+1)  + 
                        	", '" + value + "'," + "'" + elementType + "'" + ")";						
		                db.executeQuery(sql);
	    				db.release();								
					}
					
				}
			}
			
			
			//insert lists
			for (FormList formList : formDef.getFormLists().getFormList() ){
				
			   java.sql.Timestamp listCrc = new java.sql.Timestamp(new java.util.Date().getTime());	
               String listId = ObjectManager.dbCreateObjectWithPermissions(ObjectType.FORM_LIST, "A", spaceId, userId);

                sql  = "insert into pn_class_list (class_id, list_id, owner_space_id, list_name, list_desc, field_cnt, record_status, crc, is_shared, is_admin) values (" +
                    classId + ", " + listId + ", " + spaceId + ", " + DBFormat.varchar2(formList.getName()) + ", " + DBFormat.varchar2(formList.getDescription()) + ", " +
                   String.valueOf(formList.getListField().size()) + ",'A', " + DBFormat.crc(listCrc) + ", " + DBFormat.bool(formList.isIsShared()) + ", " + DBFormat.bool(formList.isIsAdmin()) + ")";
				db.executeQuery(sql);
				db.release();
				
                sql = "insert into pn_space_has_class_list (space_id, class_id, list_id, is_default) " +
                "values (" + spaceId + ", " + classId + ", " + listId + " , " + DBFormat.bool(formList.isIsDefault()) + ")";				
				db.executeQuery(sql);
				db.release();
                
				//add list fields
				for (ListField listField : formList.getListField()){
					//add list filed properties
                    sql = "insert into pn_class_list_field (class_id, list_id, field_id, field_width, field_order, wrap_mode, is_list_field, is_subfield, is_sort_field, sort_order, sort_ascending, is_calculate_total) " +
                    "values (" + classId + "," + listId + "," + fieldIds.get(listField.getId()) + "," +
                    DBFormat.number(listField.getFieldWidth()) + "," +
                    DBFormat.number(listField.getFieldOrder() != null ? listField.getFieldOrder().toString() : null) + "," +
                    DBFormat.bool(listField.isWrapMode()) + "," +
                    DBFormat.bool(listField.isIsListField()) + "," +
                    DBFormat.bool(listField.isIsSubfield()) + "," +
                    DBFormat.bool(listField.isIsSortField()) + "," +
                    (listField.getSortOrder() != null ? listField.getSortOrder().toString() : null ) + "," +
                    DBFormat.bool(listField.isSortAscending()) + "," +
                    DBFormat.bool(listField.isIsCalculateTotal()) + ")";					

    				db.executeQuery(sql);
    				db.release();

    				//add list field filters
    				FieldFilter filter =  listField.getFieldFilter();
    				for (String filterValue : filter.getFilterValue()){
    					
    					if (domainValueIds.get(filterValue) != null){
    						filterValue = domainValueIds.get(filterValue);
    					}
    					
    					String valueId = ObjectManager.dbCreateObjectWithPermissions(ObjectType.FORM_FILTER_VALUE, "A", spaceId, userId);
                        sql =  "insert into pn_class_list_filter (class_id, list_id, field_id, value_id, operator, filter_value) " +
                        "values (" + classId + "," + listId + "," + fieldIds.get(listField.getId()) + "," + valueId + "," +
                        DBFormat.varchar2(filter.getJoinOperator()) + "," + DBFormat.varchar2(filterValue) + ")";
        				db.executeQuery(sql);
        				db.release();    					    					
    				}    				
				}
			}
			

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			Logger.getLogger(FormServiceImpl.class).error(
					"FormServiceImpl.storeImportedDesign threw an SQL Exception: "
							+ sqle);
			throw new PersistenceException("Error occured storing form", sqle);
		} finally {
			if (db.connection != null) {
				try {
					db.connection.rollback();
				} catch (SQLException sqle2) {
					// Continue to release()
				}
			}
			//db.release();
		}

	}

}
