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
package net.project.license.create;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A LicenseSelectionType represents a selection during registration
 * made by a user to indicate how they will acquire a license.
 * This includes entering a charge code, entering a license key etc.
 */
public class LicenseSelectionType implements java.io.Serializable {

    /** Collection of all types for ease of lookup. */
    private static ArrayList typeCollection = new ArrayList();

    /** This type's id. */
    private String typeID = null;
    

    /**
     * Returns the LicenseSelectionType for the specified id.
     * @return the object which has the specified ID; or <code>null</code> if
     * none is found
     */
    public static LicenseSelectionType forID(String typeID) {
        LicenseSelectionType nextType = null;
        LicenseSelectionType foundType = null;

        // Loop over all types in the static collection
        // Stop when one is found with the matching typeID
        Iterator it = LicenseSelectionType.typeCollection.iterator();
        while (it.hasNext() && (foundType == null)) {
            nextType = (LicenseSelectionType) it.next();
            if (nextType.getID().equals(typeID)) {
                foundType = nextType;
            }
        }

        return foundType;
    }


    /**
     * Creates a new selection type with the specified id.
     * @param typeID the id of this type
     */
    private LicenseSelectionType(String typeID) {
        this.typeID = typeID;
        LicenseSelectionType.typeCollection.add(this);
    }


    /**
     * Returns this object's id.
     * @return the id
     */
    public String getID() {
        return this.typeID;
    }


    /**
     * Indicates whether the specified LicenseSelectionType is equal to
     * this one.
     * @return true if the specified object is equal to this one; equality
     * is based on matching IDs.
     */
    public boolean equals(Object obj) {
        if (obj instanceof LicenseSelectionType &&
            obj != null &&
            ((LicenseSelectionType) obj).getID().equals(this.typeID)) {
            
            return true;
        }

        return false;
    }


    //
    // Available constants
    // Made available at end to ensure any other static values have been initialized
    //

    /** Specify a charge code. */
    public static LicenseSelectionType CHARGE_CODE = new LicenseSelectionType("1");

    /** Enter an existing license key. */
    public static LicenseSelectionType ENTERED_LICENSE_KEY = new LicenseSelectionType("2");

    /** Request a trial license. */
    public static LicenseSelectionType TRIAL = new LicenseSelectionType("3");

    /** Utilize a default charge code. */
    public static LicenseSelectionType DEFAULT_CHARGE_CODE = new LicenseSelectionType("4");
   
    /** Utilize a default license key. */
    public static LicenseSelectionType DEFAULT_LICENSE_KEY = new LicenseSelectionType("5");

    /** Use a credit card to create a new license. */
    public static LicenseSelectionType CREDIT_CARD = new LicenseSelectionType("6");
}
