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
package net.project.billing.bill.group;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Provides the collection of available group types.
 */
public class GroupTypes 
    extends ArrayList
    implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Returns the loaded collection of group types.
     * @return the loaded group types
     */
    public static GroupTypes getAll() {
        GroupTypes types = new GroupTypes();
        GroupType type = null;

        type = new GroupType();
        type.setID(GroupTypeID.CHARGE_CODE);
        type.setDescription("Charge Code");
        types.add(type);
        
        type = new GroupType();
        type.setID(GroupTypeID.CREDIT_CARD);
        type.setDescription("Credit Card");
        types.add(type);
        
        type = new GroupType();
        type.setID(GroupTypeID.TRIAL);
        type.setDescription("Trial");
        types.add(type);
        
        return types;
    }

    //
    // Instance members
    //

    /**
     * Creates a new, empty group types list
     */
    private GroupTypes() {
        super();
    }


    /**
     * Returns the group type for the given id.
     * @param GroupTypeID the id of the group type to get
     * @return the group type, or null if there is no group type for
     * the specified id
     */
    public GroupType getGroupType(GroupTypeID groupTypeID) {
        GroupType groupType = null;
        boolean isFound = false;

        // Iteratate over the types in this collection
        // Until we find one with matching id
        for (Iterator it = iterator(); it.hasNext() & !isFound; ) {
            groupType = (GroupType) it.next();
            if (groupType.getID().equals(groupTypeID)) {
                isFound = true;
            }
        }

        return groupType;
    }

}
