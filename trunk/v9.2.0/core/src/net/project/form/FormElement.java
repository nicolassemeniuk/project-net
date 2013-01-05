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
|   $Revision: 19237 $
|       $Date: 2009-05-18 02:40:05 -0300 (lun, 18 may 2009) $
|     $Author: vivana $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.form;

import java.util.ArrayList;
import java.util.Collection;

import net.project.base.property.PropertyProvider;

/**  
 * A FormElement is a type of form field.
 * When a form field is added to a Form, it is of a certain element type.  Each
 * FormElement is defined in the database.  FormElements are used by the
 * {@link FieldFactory} for loading a Form's fields.
 */
public class FormElement {

    //
    // Element IDs
    //
    
    // Instructions:
    // When adding a new element id here, be sure to update FieldFactory
    // To return a field of the appropriate type

    /**
     * The ID of the Text Element type.
     * A Text Element is for storing a single line of text up to the maximum
     * storage allowed in the persistent store.
     */
    public static final int TEXT_ELEMENT = 1;

    /**
     * The ID of the Text Area Element type.
     * A Text Area element is for storing multi-line text.
     */
    public static final int TEXTAREA_ELEMENT = 2;

    /**
     * The ID of the Menu Element type.
     * A Menu Element provides a selection list of choices.
     * It may permit single- or multiple-selection.
     */
    public static final int MENU_ELEMENT = 3;

    /**
     * The ID of the Date Element type.
     * A Date Element is for storing dates.
     */
    public static final int DATE_ELEMENT = 4;

    /**
     * The ID of the Checkbox Element type.
     * A Checkbox Element stores a true or false value.
     */
    public static final int CHECKBOX_ELEMENT = 5;
    
    //public static final int MULTI_MENU_ELEMENT = 6;
    //public static final int TIME_ELEMENT = 7;
    //public static final int DATETIME_ELEMENT = 8;
    
    /**
     * The ID of the Person Element type.
     * A Person Element provides a selection list of Team Members.
     * It allows the selection of a single value.
     */
    public static final int PERSON_ELEMENT = 9;
    
    //public static final int MULTI_PERSON_ELEMENT = 10;
    
    /**
     * The ID of the Milestone Element type.
     * A Milestone Element provides a selection list of the current milestones
     * in a space.
     * It allows the selection of a single value.
     */
    public static final int MILESTONE = 11;

    /**
     * The ID of the Number Element type.
     * A Number Element is for storing a number.
     */
    public static final int NUMBER_ELEMENT = 12;

    /**
     * The ID of the Currency Element type.
     * A Currency Element is for storing a currency value.
     */
    public static final int CURRENCY_ELEMENT = 13;

    /**
     * The ID of the Horizontal Separator Element type.
     * A Horizontal Separator Element displays a horizontal line on a form.
     */
    public static final int HORIZONTAL_SEPARATOR =20;

    /**
     * The ID of the Instruction Element type.
     * An Instruction Element provides a read-only display of instructions.
     */
    public static final int INSTRUCTION =21;


    //
    // Begin Custom elements (not defined in database)
    // This is the new way of adding a Form Field
    // Custom property sheets may then be used for capturing properties
    //

    /**
     * The ID of the Calculation Element type.
     * A Calculation Elements provides a calculated field value, calculated
     * from other fields on the form.
     */
    public static final int CALCULATION_ELEMENT = 100;

    /**
     * The ID of the Assigned user Element type.
     * Assigned user provide is for display form record assigned user 
     */
    public static final int ASSIGNED_USER_ELEMENT = 110;
    
    /**
     * The ID of the Form ID Element type.
     * Form ID is for read only display of form record ID 
     */
    public static final int FORM_ID_ELEMENT = 120;

    /**
     * The ID of the Assignor user Element type.
     * Assignor user provide is for display form record assignor  
     */
    public static final int ASSIGNOR_USER_ELEMENT = 130;    
    
    /**
     * The ID of the Creator user Element type.
     * Creator user provide is for display form record creator  
     */
    public static final int CREATOR_USER_ELEMENT = 140;    
    
    /**
     * The ID of the Modify user Element type.
     * Modify user provide is for display form record modifier  
     */
    public static final int MODIFY_USER_ELEMENT = 150;    
    
    /**
     * The ID of the Create date Element type.
     * Create date provide is for display form record create date  
     */
    public static final int CREATE_DATE_ELEMENT = 160;
    
    /**
     * The ID of the Modify date Element type.
     * Modify date provide is for display form record modify date  
     */
    public static final int MODIFY_DATE_ELEMENT = 170;    
    
    /**
     * The ID of the record space owner Element type.   
     */
    public static final int SPACE_OWNER_ELEMENT = 180;    
    
    /**
     * Returns all custom FormElement objects.
     * @return collection of custom form elements
     */
    public static Collection getCustomFormElements() {
        ArrayList elements = new ArrayList();
        FormElement.CustomFormElement element = null;

        // Calculation Element
        element = new CustomFormElement(FormElement.CALCULATION_ELEMENT, "calculation", PropertyProvider.get("prm.form.designer.fields.create.type.option.calculated.name"), null, FormField.CALCULATION_FIELD);
        elements.add(element);

        return elements;
    }


    //
    // Persistent properties
    // from pn_element database table.
    //

    protected String m_elementID = null;
    protected String m_elementType = null;
    protected String m_elementName = null;
    protected String m_elementDescription = null;
    protected String m_elementLabel = null;
    protected String m_db_fieldDatatype = null;
    protected String m_displayClassID = FormField.TEXT;
    

    public String getID() {
        return m_elementID;
    }


    public String getType() {
        return m_elementType;
    }


    public String getName() {
        return m_elementName;
    }


    public String getDescription() {
        return m_elementDescription;
    }


    public String getLabel() {
        return m_elementLabel;
    }


    public String getDatatype() {
        return m_db_fieldDatatype;
    }


    public String getDisplayClassID() {
        return m_displayClassID;
    }

    /**
     * CustomFormElement is a special element whose properties are not
     * defined by a form-based property sheet.
     * Database or non-database form fields may be represented by CustomFormElements
     */
    private static class CustomFormElement extends FormElement {

        CustomFormElement(int id, String name, String label, String dataType, String displayClassID) {
           this.m_elementID = Integer.toString(id);
           this.m_elementName = name;
           this.m_elementLabel = label;
           this.m_db_fieldDatatype = dataType;
           this.m_displayClassID = displayClassID;
        }

    } //end CustomFormElement

}
