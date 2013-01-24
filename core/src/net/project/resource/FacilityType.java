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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.resource;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A FacilityType is a Code representing the type of a facility.
 * For example: WebEx, Physical, Teleconference.
 */
public class FacilityType extends net.project.code.Code implements java.io.Serializable {
    /** Maintains list of facility types. */
    private static ArrayList types = new ArrayList();

    /**
     * Return a FacilityType for the specified ID.
     * If the id does not correspond to a facility type, the {@link FacilityType#UNKNOWN}
     * facility type is returned.
     */
    public static FacilityType forID(String id) {
        Iterator it = types.iterator();
        FacilityType type = null;

        while (it.hasNext()) {
            type = (FacilityType) it.next();
            if ( type.getID().equals(id) ) {
                return type;
            }
        }

        return UNKNOWN;
    }

    /**
     * Creates a new, empty FacilityType.
     */
    public FacilityType(){
    }

    /**
     * Creates a new FacilityType with the specified id.
     */
    private FacilityType(String id) {
        super.objectID = id;
    }

    /**
     * Returns the id of this facility type.
     * Same as {@link #getObjectID()}, named for more consistency
     * @return the id of this facility type
     */
    public String getID() {
        return getObjectID();
    }

    public boolean equals(Object obj) {
        // If passes Code equality test
        // and obj has class FacilityType then objects are equal
        if (super.equals(obj) &&
            obj instanceof FacilityType) {

            return true;

        }
        return false;
    }

    //
    // Public static constants located at end of class to ensure
    // other statics are initialized first before calling constructor.
    //

    /**
     * Physical Facility Type.
     * This should only be used for comparison to other FacilityType objects;
     * it is not-loaded and hence has no properties set.
     */
    public static final FacilityType PHYSICAL = new FacilityType("10");
    
    /**
     * Teleconference Facility Type.
     * This should only be used for comparison to other FacilityType objects;
     * it is not-loaded and hence has no properties set.
     */
    public static final FacilityType TELECOM = new FacilityType("20");
    
    /**
     * WebEx Facility Type.
     * This should only be used for comparison to other FacilityType objects;
     * it is not-loaded and hence has no properties set.
     */
    public static final FacilityType WEBEX = new FacilityType("30");

    /**
     * Unknown Facility Type.
     *
     * This is not a real facility type.  It is used when the attempt to load
     * a facility from the database fails or when a newly created Calendar
     * Event doesn't have a facility yet.
     */
    public static final FacilityType UNKNOWN = new FacilityType("-1");


    // Add FacilityTypes to list for easier processing later
    static {
        types.add(PHYSICAL);
        types.add(TELECOM);
        types.add(WEBEX);
        types.add(UNKNOWN);
    }

}
