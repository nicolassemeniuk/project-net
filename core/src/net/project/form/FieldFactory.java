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
|    FieldFactory.java
|   $Revision: 19237 $
|       $Date: 2009-05-18 02:40:05 -0300 (lun, 18 may 2009) $
|     $Author: vivana $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.form;

import org.apache.log4j.Logger;


/**  
 * This class provides static factory methods for creating FormField subclasses 
 * of various types such as TextField, TextAreaField, DataField, 
 * SinglePersonField, etc.      
 */
public class FieldFactory {

    /** 
     * Return a new instance of a field whose class corresponds to the element 
     * type denoted by the specified element id.
     * @param element_id the id of the element type of field to make
     * @param form the Form to which the field will belong
     * @param field_id the field's database id for loading the 
     * field's properties from persistence.
     * @return a new instance of the requested field of the appropraite class or
     * <code>null</code> if no class corresponds to the element for the specified
     * element id.
     */
    public static FormField makeField(int element_id, Form form, String field_id) {

        switch (element_id) {
        case FormElement.TEXT_ELEMENT:
            return makeTextField(form, field_id);

        case FormElement.TEXTAREA_ELEMENT:
            return makeTextAreaField(form, field_id);

        case FormElement.MENU_ELEMENT:
            return makeSelectionListField(form, field_id);

        case FormElement.DATE_ELEMENT:
            return makeDateField(form, field_id);

        case FormElement.CHECKBOX_ELEMENT:
            return makeCheckBoxField(form, field_id);

//         case FormElement.TIME_ELEMENT:
//             return makeTimeField( form, field_id);

//         case FormElement.DATETIME_ELEMENT:
//             return makeDateTimeField(form, field_id);

        case FormElement.PERSON_ELEMENT:
            return makePersonListField(form, field_id);

        case FormElement.ASSIGNED_USER_ELEMENT:
            return makeAssignedUserField(form, field_id);            
            
        case FormElement.ASSIGNOR_USER_ELEMENT:
            return makeAssignorUserField(form, field_id);            
            
        case FormElement.CREATOR_USER_ELEMENT:
            return makeCreatorUserField(form, field_id);            
            
        case FormElement.MODIFY_USER_ELEMENT:
            return makeModifyUserField(form, field_id);            
            
        case FormElement.CREATE_DATE_ELEMENT:
            return makeCreateDateField(form, field_id);
            
        case FormElement.MODIFY_DATE_ELEMENT:
            return makeModifyDateField(form, field_id);            
            
        case FormElement.MILESTONE:
            return makeMilestoneField(form, field_id);

        case FormElement.NUMBER_ELEMENT:
            return makeNumberField(form, field_id);

        case FormElement.CURRENCY_ELEMENT:
            return makeCurrencyField(form, field_id);

        case FormElement.HORIZONTAL_SEPARATOR:
            return makeHorizontalSeparatorField(form, field_id);

        case FormElement.INSTRUCTION:
            return makeInstructionField(form, field_id);
	
        case FormElement.CALCULATION_ELEMENT:
        	return makeCalculationField(form, field_id);
        	
        case FormElement.FORM_ID_ELEMENT:
        	return makeFormIDField(form, field_id);        	
        	
        case FormElement.SPACE_OWNER_ELEMENT:
        	return makeSpaceOwnerField(form, field_id);        	

        default:
        	Logger.getLogger(FieldFactory.class).error("FieldFactory.makeField: Unknown element type with id: " + element_id);
            return null;
        }
    }

    /**
     * @return a new instance of a TextField.    
     */
    private static TextField makeTextField(Form form, String field_id) {
        return new TextField(form, field_id);
    }


    /** 
     * @return a new instance of a TextAreaField.    
     */
    private static TextAreaField makeTextAreaField(Form form, String field_id) {
        return new TextAreaField(form, field_id);
    }


    /** 
     * @return a new instance of a SingleSelectionListField.    
     */
    private static SelectionListField makeSelectionListField(Form form, String field_id) {
        return new SelectionListField(form, field_id);
    }


    /** 
     * @return a new instance of a SingleSelectionListField.    
     */
    private static CheckboxField makeCheckBoxField( Form form, String field_id) {
        return new CheckboxField(form, field_id);
    }


    /** 
     * @return a new instance of a SinglePersonListField.    
     */
    private static PersonListField makePersonListField(Form form, String field_id) {
        return new PersonListField(form, field_id);
    }

    /** 
     * @return a new instance of a DateField.    
     */
    private static DateField makeDateField(Form form, String field_id) {
        return new DateField(form, field_id);
    }


//     /**
//      * @return a new instance of a TimeField.
//      */
//     private static TimeField makeTimeField(Form form, String field_id) {
//         return new TimeField(form, field_id);
//     }


//     /**
//      * @return a new instance of a DateTimeField.
//      */
//     private static DateTimeField makeDateTimeField(Form form, String field_id) {
//         return new DateTimeField(form, field_id);
//     }


    /** 
     * @return a new instance of a MilestoneListField.    
     */
    private static MilestoneListField makeMilestoneField(Form form, String field_id) {
        return new MilestoneListField(form, field_id);
    }


    /** 
     * @return a new instance of a NumberField.    
     */
    private static NumberField makeNumberField(Form form, String field_id) {
        return new NumberField(form, field_id);
    }


    /** 
     * @return a new instance of a CurrencyField.    
     */
    private static CurrencyField makeCurrencyField(Form form, String field_id) {
        return new CurrencyField(form, field_id);
    }


    /** 
     * @return a new instance of a DateField.    
     */
    private static HorizontalSeparatorField makeHorizontalSeparatorField(Form form, String field_id) {
        return new HorizontalSeparatorField(form, field_id);
    }


    /** 
     * @return a new instance of a DateField.    
     */
    private static InstructionField makeInstructionField(Form form, String field_id) {
        return new InstructionField(form, field_id);
    }

    /** 
     * @return a new instance of a CalculationField.    
     */
    private static CalculationField makeCalculationField(Form form, String field_id) {
        return new CalculationField(form, field_id);
    }
    
    /** 
     * @return a new instance of a AssignedUserField.    
     */
    private static AssignedUserField makeAssignedUserField(Form form, String field_id) {
        return new AssignedUserField(form, field_id);
    }    
    
    /** 
     * @return a new instance of a AssignorUserField.    
     */
    private static AssignorUserField makeAssignorUserField(Form form, String field_id) {
        return new AssignorUserField(form, field_id);
    }
    
    /** 
     * @return a new instance of a CreatorUserField.    
     */
    private static CreatorUserField makeCreatorUserField(Form form, String field_id) {
        return new CreatorUserField(form, field_id);
    }    
    
    /** 
     * @return a new instance of a ModifyUserField.    
     */
    private static ModifyUserField makeModifyUserField(Form form, String field_id) {
        return new ModifyUserField(form, field_id);
    }    
    
    /** 
     * @return a new instance of a FormIDField.    
     */
    private static FormID makeFormIDField(Form form, String field_id) {
        return new FormID(form, field_id);
    }
    
    /** 
     * @return a new instance of a CreateDateField.    
     */
    private static CreateDateField makeCreateDateField(Form form, String field_id) {
        return new CreateDateField(form, field_id);
    }
    
    /** 
     * @return a new instance of a ModifyDateField.    
     */
    private static ModifyDateField makeModifyDateField(Form form, String field_id) {
        return new ModifyDateField(form, field_id);
    }    
    
    /** 
     * @return a new instance of a ModifyDateField.    
     */
    private static MetaDataSpaceField makeSpaceOwnerField(Form form, String field_id) {
        return new MetaDataSpaceField(form, field_id);
    }    
}
