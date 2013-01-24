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
 * Provides an enumeration of the ids for the types of license models.
 */
public class LicenseModelTypeID implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Maintains the finite list of ids.
     */
    private static ArrayList typeIDs = new ArrayList();
    

    /**
     * Returns the LicenseModelTypeID object for the specified string id.
     * @param id for which to get the matching LicenseModelTypeID
     * @return the object with the specified internal id or null if
     * there one is not found
     */
    public static LicenseModelTypeID forID(String id) {
        LicenseModelTypeID groupTypeID = null;
        boolean isFound = false;
        
        // Loop over all the items, finding the one with matching internal id
        Iterator it = LicenseModelTypeID.typeIDs.iterator();
        while (it.hasNext() & !isFound) {
            groupTypeID = (LicenseModelTypeID) it.next();
            if (groupTypeID.getID().equals(id)) {
                isFound = true;                
            }
        }

        return groupTypeID;
    }


    //
    // Instance members
    //

    /** The internal id of a LicenseModelTypeID. */
    private String id = null;


    private LicenseModelTypeID(String id) {
        this.id = id;
        // Add this to the collection of all typeIDs
        LicenseModelTypeID.typeIDs.add(this);
    }

    /**
     * Returns the value of this LicenseModelTypeID.
     * @return this object's ID
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the value of this LicenseModelTypeID.
     * @return this object's ID
     */
    public String toString() {
        return getID();
    }
    
    public boolean equals(Object obj) {
        if (obj != null &&
            obj instanceof LicenseModelTypeID &&
            ((LicenseModelTypeID) obj).getID().equals(this.getID())) {
            
            return true;
        }
        
        return false;
    }

    //
    // Static Constants located at end of class to ensure all other
    // static initializations occur before this
    //

    /** UsageLimit license model type id, currently <code>100</code>. */
    public static LicenseModelTypeID USAGE_LIMIT = new LicenseModelTypeID("100");
    
    /** TimeLimit license model type id, currently <code>200</code>. */
    public static LicenseModelTypeID TIME_LIMIT = new LicenseModelTypeID("200");

    /** NodeLocked license model type id, currently <code>300</code>. */
    public static LicenseModelTypeID NODE_LOCKED = new LicenseModelTypeID("300");

}
