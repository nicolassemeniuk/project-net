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
package net.project.billing.bill.category;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Provides the collection of available categories.
 */
public class CategoryCollection 
        extends ArrayList
        implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Returns the loaded collection of categories.
     * @return the loaded categories
     */
    public static CategoryCollection getAll() {
        CategoryCollection categoryCollection = new CategoryCollection();
        Category category = null;

        category = new Category();
        category.setID(CategoryID.LICENSE_USAGE_TYPE_A);
        categoryCollection.add(category);
        
        category = new Category();
        category.setID(CategoryID.LICENSE_MAINTENANCE_TYPE_A);
        categoryCollection.add(category);

        category = new Category();
        category.setID(CategoryID.LICENSE_MAINTENANCE_TYPE_B);
        categoryCollection.add(category);

        category = new Category();
        category.setID(CategoryID.LICENSE_MAINTENANCE_TYPE_C);
        categoryCollection.add(category);
        
        return categoryCollection;
    }

    //
    // Instance members
    //

    /**
     * Creates a new, empty categories list
     */
    private CategoryCollection() {
        super();
    }


    /**
     * Returns the category for the given id.
     * @param categoryID the id of the category to get
     * @return the category, or null if there is no category for
     * the specified id
     */
    public Category getCategory(CategoryID categoryID) {
        Category category = null;
        boolean isFound = false;

        // Iteratate over the categoryCollection in this collection
        // Until we find one with matching id
        for (Iterator it = iterator(); it.hasNext() & !isFound; ) {
            category = (Category) it.next();
            if (category.getID().equals(categoryID)) {
                isFound = true;
            }
        }

        return category;
    }

}
