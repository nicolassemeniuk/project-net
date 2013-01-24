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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.crossspace;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.project.base.property.PropertyProvider;

/**
 * A typed enumeration of SharePermissionType classes.
 * 
 * @author Matthew Flower
 * @since Version 8.0.0
 */
public class TradeAgreement implements Comparable, Serializable {
    /**
     * This list contains all of the possible types for this typed enumeration.
     */
    private static Map types = new HashMap();
    public static final TradeAgreement NO_ACCESS = new TradeAgreement("0", "No access allowed");
    public static final TradeAgreement ALL_ACCESS = new TradeAgreement("1", "Full Access");
    public static final TradeAgreement SPECIFIED_ACCESS = new TradeAgreement("2", "Access to specified users and spaces");

    /**
     * This type is only used when we need to update a share and we don't know 
     * what permission it currently has.
     */
    public static final TradeAgreement LEAVE_UNCHANGED = new TradeAgreement("-1", "");
    public static final TradeAgreement DEFAULT = ALL_ACCESS;

    /**
     * Get the SharePermissionType that corresponds to a given ID.
     * 
     * @param id a <code>String</code> value which is the primary key of a
     * <code>SharePermissionType</code> we want to find.
     * @return a <code>SharePermissionType</code> corresponding to the supplied
     *         ID, or the DEFAULT <code>SharePermissionType</code> if one cannot
     *         be found.
     */
    public static TradeAgreement getForID(String id) {
        TradeAgreement toReturn = (TradeAgreement) types.get(id);
        if (toReturn == null) {
            toReturn = DEFAULT;
        }
        return toReturn;
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /**
     * A Unique identifier for this SharePermissionType
     */
    private String id;
    /**
     * A token used to find a human-readable name for this SharePermissionType
     */
    private String displayToken;

    /**
     * Private constructor which creates a new SharePermissionType instance.
     * 
     * @param id a <code>String</code> value which is a unique identifier for
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private TradeAgreement(String id, String displayToken) {
        this.id = id;
        this.displayToken = displayToken;
        types.put(id, this);
    }

    /**
     * Get the unique identifier for this type enumeration.
     * 
     * @return a <code>String</code> value containing the unique id for this
     *         type.
     */
    public String getID() {
        return id;
    }

    /**
     * Return a human-readable display name for this SharePermissionType.
     * 
     * @return a <code>String</code> containing a human-readable version of this
     *         SharePermissionType.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TradeAgreement)) {
            return false;
        }

        final TradeAgreement tradeAgreement = (TradeAgreement) o;

        if (id != null ? !id.equals(tradeAgreement.id) : tradeAgreement.id != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }


    public int compareTo(Object o) {
        Integer thisID = new Integer(id);
        Integer otherID = new Integer(((TradeAgreement)o).id);

        return thisID.compareTo(otherID);
    }
}
