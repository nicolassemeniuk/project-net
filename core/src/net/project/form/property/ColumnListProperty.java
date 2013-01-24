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

import net.project.base.property.PropertyProvider;
import net.project.form.Form;

/**
 * A ColumnListProperty is the property that provides a list of columns
 * to select from.
 */
class ColumnListProperty extends DomainListProperty {

    /**
     * Creates a new ColumnListProperty with the id {@link SpecialPropertyID#COLUMN}
     * and specified displayName.
     */
    public ColumnListProperty(String displayName) {
        super(SpecialPropertyID.COLUMN.toString(), displayName);
        addColumns();
    }

    /**
     * Adds the columns in this column list.
     */
    private void addColumns() {
        addDomainValue(Form.LEFT_COLUMN, PropertyProvider.get("prm.form.property.columnlistproperty.addcolumn.left.label"));
        addDomainValue(Form.RIGHT_COLUMN, PropertyProvider.get("prm.form.property.columnlistproperty.addcolumn.right.label"));
        addDomainValue(Form.BOTH_COLUMNS, PropertyProvider.get("prm.form.property.columnlistproperty.addcolumn.both.label"));
    }

}
