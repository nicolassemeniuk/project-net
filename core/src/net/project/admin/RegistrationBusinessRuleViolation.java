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

/**
 * Capture information about one failure that occurred while validating the
 * information stored within a user object.
 *
 * @since Gecko Update 4
 */
public class RegistrationBusinessRuleViolation {
    private String errorMessage;
    private String fieldName;

    /**
     * Creates a new <code>RegistrationBusinessRuleViolation</code> instance.
     */
    public RegistrationBusinessRuleViolation() {
        super();
    }
    
    /**
     * Creates a new <code>RegistrationBusinessRuleViolation</code> instance,
     * setting values for the appropriate fields by default.
     *
     * @param errorMessage a <code>String</code> value containing the error
     * message that occurred while validating a field.
     * @param fieldName a <code>String</code> value containing the name of the
     * field where the business rule validation failed.
     */
    public RegistrationBusinessRuleViolation(String errorMessage, String fieldName) {
        super();
        this.errorMessage = errorMessage;
        this.fieldName = fieldName;
    }
    
    /**
     * Set the text that describes the business rule failure that has occurred
     * while attempting to validate the user information.
     *
     * @param errorMessage a <code>String</code> value containing the text
     * describing the business rule failure.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    /**
     * Get the text that describes the business rule failure that occurred
     * while attempting to validate the user information.
     *
     * @return a <code>String</code> value containing the text describing
     * the business rule failure.
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Identify the field name (generally the database fieldname) where this
     * business rule failure occurred.
     *
     * @param fieldName a <code>String</code> value containing the database
     * field name where this business rule violation occurred.
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get the database field name where this business rule failure occurred.
     *
     * @return a <code>String</code> value containing the database field name.
     */
    public String getFieldName() {
        return fieldName;
    }
}
