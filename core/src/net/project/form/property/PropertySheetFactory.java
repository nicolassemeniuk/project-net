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

import net.project.form.FieldPropertySheet;
import net.project.form.FormField;

/**
 * Factory for creating new Property Sheets.
 */
public class PropertySheetFactory {

    /**
     * Returns a Property Sheet for the form field for the specified class id.
     * @param displayClassID the class id of the form field for which to get
     * the property sheet.
     * @return the property sheet
     */
    public static IPropertySheet getPropertySheetForDisplayClass(String displayClassID) {
        
        if (FormField.DATE.equals(displayClassID)) {
            // Date Field specifies a custom property sheet
            return new DateTimeFieldPropertySheet();
        }
        else if (FormField.CALCULATION_FIELD.equals(displayClassID)) {
            // Calculation field specifies a custom property sheet
            return new CalculationFieldPropertySheet();
        } 
        else if(displayClassID != null) {
            // All other kinds of display class use a common FieldPropertySheet
            return new FieldPropertySheet();   
        } 
        else 
        {
	    return null;
	}

    }

    public static IPropertySheet getCalculationFieldPropertySheet(){
	
        // Returns CalculationFieldPropertySheet specifically for certain purposes e.g. in load properties() of CalculationField
	return new CalculationFieldPropertySheet();
    }
}
