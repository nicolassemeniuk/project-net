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
package net.project.base.quantity;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a collection of units of measure.
 */
public class UnitOfMeasureCollection extends ArrayList {

    //
    // Static members
    //

    /**
     * Returns the loaded collection of units of measure.
     * @return the loaded units of measure
     */
    public static UnitOfMeasureCollection getAll() {
        UnitOfMeasureCollection uomCollection = new UnitOfMeasureCollection();
        UnitOfMeasure uom = null;

        // Ideally we would load these from the database
        // meanwhile, hardcode the available units of measure
        uomCollection.add(UnitOfMeasure.EA);

        return uomCollection;
    }

    //
    // Instance members
    //

    /**
     * Creates a new, empty license model types list
     */
    private UnitOfMeasureCollection() {
        super();
    }


    /**
     * Returns the unit of measure for the specified id.
     * @param id the id of the unit of measure to get
     * @return the unit of measure, or null if there is no unit of measure for
     * the specified id
     */
    public UnitOfMeasure getForID(String id) {
        UnitOfMeasure uom = null;
        boolean isFound = false;

        // Iteratate over the items in this collection
        // Until we find one with matching id
        for (Iterator it = iterator(); it.hasNext() & !isFound; ) {
            uom = (UnitOfMeasure) it.next();
            if (uom.getID().equals(id)) {
                isFound = true;
            }
        }

        return uom;
    }


}
