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
package net.project.form.report;

import net.project.form.FieldData;
import net.project.form.FormField;

/**
 * A simple helper class which translates data from the database into
 * human-readable form according to the needs of a given <code>FormField</code>.
 *
 * This object doesn't really add anything novel.  It just automates the
 * creation of <code>FieldData</code> objects and passes them to
 * {@link net.project.form.FormField#formatFieldData}.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FormDataFormatter {
    /**
     * Format an object containing form data extracted from the database into
     * human-readable form.
     *
     * @param field a <code>FormField</code> which is the source of the
     * formatting.
     * @param data a <code>Object</code> which contains data from the database.
     * @return a <code>String</code> properly formatted for reading.
     */
    public static String formatSimpleData(FormField field, Object data) {
        FieldData fieldData = new FieldData();
        fieldData.add(data);

        String returnValue;
        if (field == null) {
            returnValue = "";
        } else {
            returnValue = field.formatFieldData(fieldData);
        }
        return returnValue;
    }
}
