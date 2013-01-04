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

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.project.base.PnetException;
import net.project.form.soa.FormExport;
import net.project.form.soa.FormServiceImpl;
import net.project.form.soa.binding.DomainValue;
import net.project.form.soa.binding.FieldPropertyValue;
import net.project.form.soa.binding.FieldPropertyValues;
import net.project.form.soa.binding.FormDef;
import net.project.form.soa.binding.FormFields;
import net.project.form.soa.binding.FormLists;
import net.project.form.soa.binding.ListField;
import net.project.form.soa.binding.ObjectFactory;


import org.apache.log4j.Logger;

import com.sun.istack.ByteArrayDataSource;

public class FormLoadSaveHelper {
	private static Logger log = Logger.getLogger(FormLoadSaveHelper.class);

	private static String version = "1.0";
	
	public static DataHandler exportToDataHandler(int formId) {
		FormExport formExport = new FormExport();

		formExport.setFormId(formId);
		log.info("Form Id being exported " + formId);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {

			FormDesigner formDesigner = new FormDesigner();
			formDesigner.setID(String.valueOf(formId));
			formDesigner.load();

			JAXBContext jaxbContext = JAXBContext.newInstance("net.project.form.soa.binding");
			Marshaller marshaller = jaxbContext.createMarshaller();

			ObjectFactory factory = new ObjectFactory();

			FormDef form = factory.createFormDef();

			form.setName(formDesigner.getName());
			form.setDescription(formDesigner.getDescription());
			form.setAbbreviation(formDesigner.getAbbreviation());
			form.setIncludeAssignments(formDesigner.getSupportsAssignment());
			form.setCreationDate(new Date());
			form.setVersion(version);

			List<FormField> designerFields = formDesigner.getFields();
			FormFields formFields = factory.createFormFields();

			for (FormField designerField : designerFields) {
				net.project.form.soa.binding.FormField field = factory.createFormField();
				field.setId(new BigInteger(designerField.getID()));
				field.setElementId(new BigInteger(designerField.getElementID()));
				field.setElementName(designerField.getElementName());
				field.setLabel(designerField.getFieldLabel());
/*				field.setColumnId(Integer.valueOf(designerField.getColumnID() != null ? designerField.getColumnID() : "0"));
				field.setColumnNum(Integer.valueOf(designerField.getColumnNum() != null ? designerField.getColumnNum() : "0"));
				field.setColumnSpan(Integer.valueOf(designerField.getColumnSpan() != null ? designerField.getColumnSpan() : "0"));*/

				if (designerField.getColumnID() != null){
					if (designerField.getColumnID().equals(Form.LEFT_COLUMN)){
						field.setColumn("LEFT");
					}else if (designerField.getColumnID().equals(Form.RIGHT_COLUMN)){
						field.setColumn("RIGHT");
					}else{
						field.setColumn("BOTH");
					}
				}else{
					field.setColumn("NONE");
				}
				
				field.setRowSpan(Integer.valueOf(designerField.getRowSpan() != null ? designerField.getRowSpan() : "0"));
				field.setRowNum(Integer.valueOf(designerField.getRowNum() != null ? designerField.getRowNum() : "0"));

				field.setMaxValue(designerField.getMaxValue());
				field.setMinValue(designerField.getMinValue());
				field.setDefaultValue(designerField.getDefaultValue());
				field.setRequired(designerField.isValueRequired());
				field.setFieldGroup(designerField.getFieldGroup());

				field.setUseDefault(designerField.useDefault());
				field.setMultiselect(designerField.isMultiSelect());
				field.setDataColumnSize(designerField.getDataColumnSize());
				field.setDataColumnScale(designerField.getDataColumnScale());
				field.setInstruction(designerField.getInstructions());

				if (designerField.getDomain() != null) {
					FieldDomain designerDomain = designerField.getDomain();
					if (designerDomain.getValues() != null && designerDomain.getValues().size() > 0) {
						net.project.form.soa.binding.DomainValues domainValues = factory.createDomainValues();

						for (Iterator it = designerDomain.getValues().iterator(); it.hasNext();) {
							FieldDomainValue designerDomainValue = (FieldDomainValue) it.next();
							DomainValue domainValue = factory.createDomainValue();
							domainValue.setId(new BigInteger(designerDomainValue.getID()));
							domainValue.setContent(designerDomainValue.getName());
							domainValues.getDomainValue().add(domainValue);
						}
						field.setDomainValues(domainValues);
					}
				}

				if (designerField.getProperties() != null && designerField.getProperties().size() > 0) {
					FieldPropertyValues fieldPropertyValues = factory.createFieldPropertyValues();
					List<FormFieldProperty> designerFieldProperties = designerField.getProperties();
					for (FormFieldProperty designerFieldProperty : designerFieldProperties) {
						FieldPropertyValue fieldPropertyValue = factory.createFieldPropertyValue();

						fieldPropertyValue.setClientTypeId(designerField.getClientTypeID());
						fieldPropertyValue.setPropertyName(designerFieldProperty.getName());
						fieldPropertyValue.setPropertyType(designerFieldProperty.getType());
						fieldPropertyValue.setPropertyValue(designerFieldProperty.getValue());

						fieldPropertyValues.getFieldPropertyValue().add(fieldPropertyValue);
					}

					field.setFieldPropertyValues(fieldPropertyValues);
				}

				formFields.getFormField().add(field);

			}
			form.setFormFields(formFields);

			List<FormList> designerLists = formDesigner.getLists();
			FormLists formLists = factory.createFormLists();
			for (FormList designerList : designerLists) {
				net.project.form.soa.binding.FormList list = factory.createFormList();

				//list.setId(new BigInteger(designerList.getID()));
				list.setIsAdmin(designerList.isAdminList());
				list.setIsDefault(designerList.isSystemDefault());
				list.setIsShared(designerList.isShared());
				list.setDescription(designerList.getDescription());
				list.setName(designerList.getName());

				List<FormField> designerListFields = designerList.getFields();
				HashMap<String, ListFieldProperties> designerListFieldsProperties = designerList.getListFieldProperties();
				for (FormField designerListField : designerListFields) {
					ListFieldProperties listFieldProp = designerListFieldsProperties.get(designerListField.getID());

					ListField listField = factory.createListField();
					listField.setId(new BigInteger(designerListField.getID()));
					if (listFieldProp.getFieldOrder() != null) {
						listField.setFieldOrder(Integer.valueOf(listFieldProp.getFieldOrder()));
					}
					listField.setFieldWidth(listFieldProp.getFieldWidth());
					listField.setIsListField(listFieldProp.isListField());
					listField.setWrapMode(listFieldProp.isNoWrap());
					listField.setSortAscending(listFieldProp.isSortAscending());
					listField.setIsSortField(listFieldProp.isSortField());
					listField.setIsSubfield(listFieldProp.isSubfield());
					listField.setSortOrder(listFieldProp.getSortOrder());
					listField.setIsCalculateTotal(listFieldProp.isCalculateTotal());

					net.project.form.soa.binding.FieldFilter filter = factory.createFieldFilter();
					FieldFilter designerFilter = designerList.getFieldFilter(designerListField);
					if (designerFilter != null && designerFilter.getConstraintsIterator() != null) {
						for (Iterator it = designerFilter.getConstraintsIterator(); it.hasNext();) {
							FieldFilterConstraint fieldFilterConstraint = (FieldFilterConstraint) it.next();
							filter.setJoinOperator(fieldFilterConstraint.getOperator());
							int num_values = fieldFilterConstraint.size();
							for (int j = 0; j < num_values; j++) {
								filter.getFilterValue().add((String) fieldFilterConstraint.get(j));
							}
						}
					}
					listField.setFieldFilter(filter);
					list.getListField().add(listField);
				}

				formLists.getFormList().add(list);
			}
			form.setFormLists(formLists);

			marshaller.marshal(form, out);

		} catch (Exception e) {
			log.error("Error exporting form :" + e.getMessage());
		}

		DataSource source = new ByteArrayDataSource(out.toByteArray(), "application/octet-stream");
		return new DataHandler(source);
	}

	public static void loadFromDataHandler(DataHandler formData, int userId, int spaceId) throws PnetException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("net.project.form.soa.binding");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			FormDef formDef = (FormDef) unmarshaller.unmarshal(formData.getInputStream());
			log.info("status:" + (formDef != null));

			FormServiceImpl service = new FormServiceImpl();
			service.storeFormData(formDef, userId, spaceId);
		} catch (Exception e) {
			log.error("Error importing form :" + e.getMessage());
			throw new PnetException(e);
		}

	}

}
