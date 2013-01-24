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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.util;

/**
 * This class encapsulates an error found within the processing of an html form
 * page.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ErrorDescription {
    /** The html form field which caused the error.  (If one exists.) */
    private String fieldName;
    /** The human-readable text of the error that occurred. */
    private String errorText;

    /**
     * Standard constructor which creates a new instance of the ErrorDescription
     * object.
     *
     * @param errorText the human-readable text of the error that occurred.
     */
    public ErrorDescription(String errorText) {
        this.errorText = errorText;
    }

    /**
     * Standard constructor which creates a new instance of the ErrorDescription
     * object.
     *
     * @param fieldName a <code>String</code> containing the html field name
     * that caused the error to occur.
     * @param errorText a <code>String</code> containing the human-readable text
     * of the error that occurred.
     */
    public ErrorDescription(String fieldName, String errorText) {
        this.fieldName = fieldName;
        this.errorText = errorText;
    }

    /**
     * Get the name of the html form field that caused the error to occur.
     *
     * @return a <code>String</code> value containing the name of the html form
     * field that caused the error to occur.
     */
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    /**
     * Provides a string representation of this ErrorDescription, suitable
     * for debugging only.
     * @return the error description of the form <code>[FieldName: ]ErrorText</code>
     */
    public String toString() {
        String result = "";
        if (this.fieldName != null) {
            result += this.fieldName + ": ";
        }
        result += this.errorText;

        return result;
    }
}
