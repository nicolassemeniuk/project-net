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
|
+----------------------------------------------------------------------*/
package net.project.admin;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class stores a collection of business rule violations that occurred
 * while trying to validate information about user registration.
 *
 * @since 7.4
 */
public class RegistrationBusinessRuleException extends Exception {
    ArrayList validationErrors = new ArrayList();
        
    /**
     * Creates a new <code>RegistrationBusinessRuleException</code> instance.
     *
     * @param validationErrors an <code>ArrayList</code> value containing the
     * errors that occurred while validating a User object.
     */
    public RegistrationBusinessRuleException(ArrayList validationErrors) {
        this.validationErrors = validationErrors;
    }
    
    /**
     * Get the errors that were found while verifying the user in a format
     * suitable for display on an HTML page.
     *
     * @return a <code>String</code> value containing the validation errors
     * formatted as HTML.
     */
    public String getHTMLFormattedErrors() {
        StringBuffer htmlFormattedErrors = new StringBuffer();
        Iterator it = validationErrors.iterator();
        
        //Iterate through the violations and add them to a string to be returned
        //to the user.
        while (it.hasNext()) {
            RegistrationBusinessRuleViolation violation = (RegistrationBusinessRuleViolation)it.next();
            htmlFormattedErrors.append(violation.getErrorMessage()).append("<BR>\n");
        }
        
        return htmlFormattedErrors.toString();
    }

    /**
     * Get the name of the object where the error occurred.  For example, if the
     * first error occurred while validating the "first_name" field,
     * <code>getErrorFieldName(0)</code> would return "first_name".
     *
     * @param index an <code>int</code> value indicating which field in the list
     * you wish to see the field name for.
     * @return a <code>String</code> value which indicates the field name that
     * corresponds to the index you supplied as a parameter.
     */
    public String getErrorFieldName(int index) {
        return ((RegistrationBusinessRuleViolation)validationErrors.get(index)).getFieldName();
    }
}
