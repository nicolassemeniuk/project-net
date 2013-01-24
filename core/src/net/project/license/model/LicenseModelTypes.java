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
package net.project.license.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Provides the collection of available license model types.
 */
public class LicenseModelTypes 
    extends ArrayList
    implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Returns the loaded collection of license model types.
     * @return the loaded license model types
     */
    public static LicenseModelTypes getAll() {
        LicenseModelTypes types = new LicenseModelTypes();

        // Ideally these should be loaded from the database to allow
        // customers to add additional ones
        // (see net.project.bill.payment.PaymentModelTypes for example)
        LicenseModelType usageLimit = new LicenseModelType();
        usageLimit.setID(LicenseModelTypeID.USAGE_LIMIT);
        usageLimit.setClassName("net.project.license.model.UsageLimit");
        usageLimit.setXMLElementName("UsageLimit");
        types.add(usageLimit);

        LicenseModelType timeLimit = new LicenseModelType();
        timeLimit.setID(LicenseModelTypeID.TIME_LIMIT);
        timeLimit.setClassName("net.project.license.model.TimeLimit");
        timeLimit.setXMLElementName("TimeLimit");
        types.add(timeLimit);

        LicenseModelType nodeLocked = new LicenseModelType();
        nodeLocked.setID(LicenseModelTypeID.NODE_LOCKED);
        nodeLocked.setClassName("net.project.license.model.NodeLocked");
        nodeLocked.setXMLElementName("NodeLocked");
        types.add(nodeLocked);

        return types;
    }

    //
    // Instance members
    //

    /**
     * Creates a new, empty license model types list
     */
    private LicenseModelTypes() {
        super();
    }


    /**
     * Returns the license model type for the given id.
     * @param licenseModelTypeID the id of the license model type to get
     * @return the license model type, or null if there is no license model type for
     * the specified id
     */
    public LicenseModelType getLicenseModelType(LicenseModelTypeID licenseModelTypeID) {
        LicenseModelType licenseModelType = null;
        boolean isFound = false;

        // Iteratate over the types in this collection
        // Until we find one with matching id
        for (Iterator it = iterator(); it.hasNext() & !isFound; ) {
            licenseModelType = (LicenseModelType) it.next();
            if (licenseModelType.getID().equals(licenseModelTypeID)) {
                isFound = true;
            }
        }

        return licenseModelType;
    }

    /**
     * Returns the license model tyep for the given xml element name.
     * @param xmlElementName the xml element name
     * @return the model type for that element name, or null if there is no model
     * type for the specified element name
     */
    protected LicenseModelType getLicenseModelTypeForElementName(String xmlElementName) {
        LicenseModelType licenseModelType = null;
        boolean isFound = false;

        // Iteratate over the types in this collection
        // Until we find one with matching id
        for (Iterator it = iterator(); it.hasNext() & !isFound; ) {
            licenseModelType = (LicenseModelType) it.next();
            if (licenseModelType.getXMLElementName().equals(xmlElementName)) {
                isFound = true;
            }
        }

        return licenseModelType;
    }

}
