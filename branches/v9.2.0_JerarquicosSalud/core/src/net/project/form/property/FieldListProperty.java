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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.form.property;

import java.util.ArrayList;
import java.util.Iterator;

import net.project.form.Form;
import net.project.form.FormField;

/**
 * Custom property that provides a list of fields defined on the form.
 */
class FieldListProperty extends DomainListProperty {

    public FieldListProperty(String id, String displayName, Form form, IFieldChecker fieldChecker) {
        super(id, displayName);
        addFormFields(form, fieldChecker);
    }

    /**
     * Adds all the form fields for the specified form as domain values to
     * this property.
     * @param form the form whose fields to add
     */
    private void addFormFields(Form form, IFieldChecker fieldChecker) {
        FormField formField = null;
        ArrayList formFields = form.getFields();
        
        if (formFields != null) {
            
            // Add each form field as a domain value to this property
            Iterator it = formFields.iterator();
            while (it.hasNext()) {
                formField = (FormField) it.next();

                if (fieldChecker.doAdd(formField)) {
                    addDomainValue(formField.getID(), formField.getFieldLabel());
                }

            }
        
        }
    }

    /**
     * Callback interface to determine whether a formField should be added
     * to this field list property.
     */
    protected static interface IFieldChecker {

        /**
         * Indicates whether the form field should be added.
         * @param formField the form field
         * @return true if the form field should be added; false otherwise
         */
        public boolean doAdd(FormField formField);

    }

}
