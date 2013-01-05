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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.form.property;

/**
 * This is an enumeration of special property IDs that are used
 * as IDs of custom properties.
 * These are handled by a property sheet in a special manner - usually to set
 * managed field properties.
 */
class SpecialPropertyID implements java.io.Serializable {

    private String propertyID = null;

    private SpecialPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public String toString() {
        return this.propertyID;
    }

    public static final SpecialPropertyID FIELD_LABEL = new SpecialPropertyID("field_label");
    public static final SpecialPropertyID ROW_NUM = new SpecialPropertyID("row_num");
    public static final SpecialPropertyID COLUMN = new SpecialPropertyID("column_id");
    public static final SpecialPropertyID MAX_LENGTH = new SpecialPropertyID("data_column_size");
    public static final SpecialPropertyID IS_REQUIRED = new SpecialPropertyID("is_value_required");
    public static final SpecialPropertyID HIDDEN_FOR_EAF = new SpecialPropertyID("hidden_for_eaf");
   // public static final SpecialPropertyID PRECISION_NUM = new SpecialPropertyID("precision_num");

}
