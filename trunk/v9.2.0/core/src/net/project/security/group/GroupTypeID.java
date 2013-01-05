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

 package net.project.security.group;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * These represent the Group Type IDs. The constants here may be used to
 * refer to different types of groups.
 * @author Carlos
 * @version %I%, %G%
 * @since 8.3.0
 */
public class GroupTypeID implements Serializable {

    //
    // Static members
    //

    /**
     * Maintains the finite list of ids.
     */
    private static Map typeIDs = new HashMap();
    

    /**
     * Returns the GroupTypeID object for the specified string id.
     * @param id for which to get the matching GroupTypeID
     * @return the object with the specified internal id or null if
     * there one is not found
     */
    public static GroupTypeID forID(String id) {
        return (GroupTypeID) typeIDs.get(id);
    }


    //
    // Instance members
    //

    /** The internal id of a GroupTypeID. */
    private String id = null;


    private GroupTypeID(String id) {
        this.id = id;
        // Add this to the collection of all typeIDs
        GroupTypeID.typeIDs.put(this.id, this);
    }

    /**
     * Returns the value of this GroupTypeID.
     * @return this object's ID
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the value of this GroupTypeID.
     * @return this object's ID
     */
    public String toString() {
        return getID();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupTypeID)) return false;

        final GroupTypeID groupTypeID = (GroupTypeID) o;

        if (!id.equals(groupTypeID.id)) return false;

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }

    /** User Defined Group Type ID, currently <code>100</code>. */
    public static GroupTypeID USER_DEFINED = new GroupTypeID("100");

    /** Space Administrator Group Type ID, currently <code>200</code>. */
    public static GroupTypeID SPACE_ADMINISTRATOR = new GroupTypeID("200");

    /** Team Member Group Type ID, currently <code>300</code>. */
    public static GroupTypeID TEAM_MEMBER = new GroupTypeID("300");

    /** Principal Group Type ID, currently <code>400</code>. */
    public static GroupTypeID PRINCIPAL = new GroupTypeID("400");

    /** Power  User Group Type ID, currently <code>500</code>. */
    public static GroupTypeID POWER_USER = new GroupTypeID("500");

    /** Everyone Group Type ID. */
    public static GroupTypeID EVERYONE = new GroupTypeID("600");
    
	/**
	 * Resource Manager Group Type ID, currently <code>700</code>.
	 * 
	 * @author Carlos
	 */
	public static GroupTypeID RESOURCE_MANAGER = new GroupTypeID("700");
}
