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
package net.project.license.cost;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Provides the collection of available license cost types.
 */
public class LicenseCostTypes extends ArrayList implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Returns the loaded collection of license cost types.
     * @return the loaded license cost types
     */
    public static LicenseCostTypes getAll() {
        LicenseCostTypes types = new LicenseCostTypes();

        LicenseCostType base = new LicenseCostType();
        base.setID(LicenseCostTypeID.BASE);
        base.setClassName("net.project.license.cost.BaseCost");
        base.setXMLElementName("Base");
        types.add(base);

        LicenseCostType maintenance = new LicenseCostType();
        maintenance.setID(LicenseCostTypeID.MAINTENANCE);
        maintenance.setClassName("net.project.license.cost.MaintenanceCost");
        maintenance.setXMLElementName("Maintenance");
        types.add(maintenance);

        return types;
    }

    //
    // Instance members
    //

    /**
     * Creates a new, empty license cost types list
     */
    private LicenseCostTypes() {
        super();
    }


    /**
     * Returns the license cost type for the given id.
     * @param licenseCostTypeID the id of the license cost type to get
     * @return the license cost type, or null if there is no license cost type for
     * the specified id
     */
    public LicenseCostType getLicenseCostType(LicenseCostTypeID licenseCostTypeID) {
        LicenseCostType licenseCostType = null;
        boolean isFound = false;

        // Iteratate over the types in this collection
        // Until we find one with matching id
        for (Iterator it = iterator(); it.hasNext() & !isFound; ) {
            licenseCostType = (LicenseCostType) it.next();
            if (licenseCostType.getID().equals(licenseCostTypeID)) {
                isFound = true;
            }
        }

        return licenseCostType;
    }

    /**
     * Returns the license cost tyep for the given xml element name.
     * @param xmlElementName the xml element name
     * @return the cost type for that element name, or null if there is no cost
     * type for the specified element name
     */
    protected LicenseCostType getLicenseCostTypeForElementName(String xmlElementName) {
        LicenseCostType licenseCostType = null;
        boolean isFound = false;

        // Iteratate over the types in this collection
        // Until we find one with matching id
        for (Iterator it = iterator(); it.hasNext() & !isFound; ) {
            licenseCostType = (LicenseCostType) it.next();
            if (licenseCostType.getXMLElementName().equals(xmlElementName)) {
                isFound = true;
            }
        }

        return licenseCostType;
    }

}
