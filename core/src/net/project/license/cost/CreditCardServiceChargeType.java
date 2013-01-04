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
package net.project.license.cost;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;

/**
 * A typed enumeration of CreditCardServiceChargeType classes.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class CreditCardServiceChargeType {
    /** This list contains all of the possible types for this typed enumeration. */
    private static List types = new ArrayList();

    public static final CreditCardServiceChargeType PERCENTAGE =
        new CreditCardServiceChargeType("10", "prm.global.creditcard.servicechargetypes.percentage.name");
    public static final CreditCardServiceChargeType FIXED_COST =
        new CreditCardServiceChargeType("20", "prm.global.creditcard.servicechargetypes.fixedcost.name");
    public static final CreditCardServiceChargeType NO_CHARGE =
        new CreditCardServiceChargeType("30", "prm.global.creditcard.servicechargetypes.nocharge.name");
    public static final CreditCardServiceChargeType DEFAULT = PERCENTAGE;

    /**
     * Get the CreditCardServiceChargeType that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>CreditCardServiceChargeType</code> we want to find.
     * @return a <code>CreditCardServiceChargeType</code> corresponding to the supplied ID, or
     * the DEFAULT <code>CreditCardServiceChargeType</code> if one cannot be found.
     */
    public static CreditCardServiceChargeType getForID(String id) {
        CreditCardServiceChargeType toReturn = DEFAULT;

        for (Iterator it = types.iterator(); it.hasNext();) {
            CreditCardServiceChargeType type = (CreditCardServiceChargeType)it.next();
            if (type.getID().equals(id)) {
                toReturn = type;
                break;
            }
        }

        return toReturn;
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /** A Unique identifier for this CreditCardServiceChargeType */
    private String id;
    /** A token used to find a human-readable name for this CreditCardServiceChargeType */
    private String displayToken;

    /**
     * Private constructor which creates a new CreditCardServiceChargeType instance.
     *
     * @param id a <code>String</code> value which is a unique identifier for 
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private CreditCardServiceChargeType(String id, String displayToken) {
        this.id = id;
        this.displayToken = displayToken;
        types.add(this);
    }

    /**
     * Get the unique identifier for this type enumeration.
     *
     * @return a <code>String</code> value containing the unique id for this 
     * type.
     */
    public String getID() {
        return id;
    }

    /**
     * Return a human-readable display name for this CreditCardServiceChargeType.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * CreditCardServiceChargeType.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }
}
