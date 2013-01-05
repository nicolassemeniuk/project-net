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
package net.project.form.report.formitemsummaryreport;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for a map which maps the value of a form field to the number of times
 * that form field appeared in the data for a form.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class SummarizedFieldData {
    /**
     * A map of field values to the number of times that field value appeared in
     * the result set.
     */
    Map fieldValues = new HashMap();

    /**
     * Get a map which maps field data to the number of times that field data
     * occurred in the result set.
     *
     * @return a <code>Map</code> which maps field data to the number of times
     * that field data occurred.
     */
    public Map getFieldValues() {
        return fieldValues;
    }

    /**
     * Set a map which maps field data values to a count of the number of times
     * that field data occurred.
     *
     * @param fieldValues a <code>Map</code> which maps field data to the number
     * of times that field data occurred.
     */
    public void setFieldValues(Map fieldValues) {
        this.fieldValues = fieldValues;
    }
}
