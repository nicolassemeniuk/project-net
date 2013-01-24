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
|   $Revision: 14743 $
|       $Date: 2006-02-06 22:26:39 +0530 (Mon, 06 Feb 2006) $
|     $Author: andrewr $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.directory.ldap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides an enumeration of <code>NonAuthenticatedAccessType</code> selections.
 */
public class NonAuthenticatedAccessType {

    //
    // Static members
    //

    /** 
     * All nonAuthenticatedAccess types, provided for easy lookup of 
     * nonAuthenticatedAccess types by id.
     */
    private static List allNonAuthenticatedAccessTypes = new ArrayList();
    
    /**
     * Returns the NonAuthenticatedAccessType for an internal id.
     * @param nonAuthenticatedAccessTypeID the internal id of the NonAuthenticatedAccess type to get
     * @return the <code>NonAuthenticatedAccessType</code> with mathing internal id, 
     * or null if none matched the id
     */
    public static NonAuthenticatedAccessType forID(String nonAuthenticatedAccessTypeID) {
        NonAuthenticatedAccessType foundType = null;

        for (Iterator it = NonAuthenticatedAccessType.allNonAuthenticatedAccessTypes.iterator(); it.hasNext() && foundType == null; ) {
            
            NonAuthenticatedAccessType nextType = (NonAuthenticatedAccessType) it.next();
            if (nextType.id.equals(nonAuthenticatedAccessTypeID)) {
                foundType = nextType;
            }
        
        }

        return foundType;
    }


    //
    // Instance members
    //

    /** The internal ID of the NonAuthenticatedAccessType. */
    private String id = null;

    /** 
     * Creates a NonAuthenticatedAccessType with the specified internal id.
     * @param id the internal id for the nonAuthenticatedAccess type
     */
    private NonAuthenticatedAccessType(String id) {
        this.id = id;
        allNonAuthenticatedAccessTypes.add(this);
    }

    /**
     * Returns the internal id of this NonAuthenticatedAccessType.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /** 
     * Indicates whether the specified object is equal to this NonAuthenticatedAccessType.
     * @param obj the NonAuthenticatedAccessType object to compare
     * @return true if the specified object is of type <code>NonAuthenticatedAccessType</code>
     * and has a matching internal id
     */
    public boolean equals(Object obj) {
        boolean isEqual = false;

        if (obj == this) {
            isEqual = true;
        
        } else {
            
            if (obj != null &&
                obj instanceof NonAuthenticatedAccessType &&
                ((NonAuthenticatedAccessType) obj).getID().equals(this.id)) {

                isEqual = true;
            }
        
        }

        return isEqual;
    }

    public int hashCode() {
        return getID().hashCode();
    }

    public String toString() {
        return getID();
    }

    //
    // Constants
    //

    /**
     * Anonymous nonauthenticated access.
     */
    public static NonAuthenticatedAccessType ANONYMOUS = new NonAuthenticatedAccessType("anonymous");

    /**
     * Specific user required for nonauthenticated access.
     */
    public static NonAuthenticatedAccessType SPECIFIC_USER = new NonAuthenticatedAccessType("specific_user");

}
