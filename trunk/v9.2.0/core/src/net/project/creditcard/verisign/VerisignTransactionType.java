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
package net.project.creditcard.verisign;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;

/**
 * A typed enumeration of VerisignTransactionType classes.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class VerisignTransactionType {
    /** This list contains all of the possible types for this typed enumeration. */
    private static List types = new ArrayList();
    /** Transaction type for making a sale. */
    public static final VerisignTransactionType SALE = new VerisignTransactionType("S", "prm.global.creditcard.verisign.transactiontype.sale");
    /** Transaction type for crediting money to a user's account. */
    public static final VerisignTransactionType CREDIT = new VerisignTransactionType("C", "prm.global.creditcard.verisign.transactiontype.credit");
    /** Transaction type to reverse a previous transaction. */
    public static final VerisignTransactionType VOID = new VerisignTransactionType("V", "prm.global.creditcard.verisign.transactiontype.void");
    /** Checks the result and status of a transaction id. */
    public static final VerisignTransactionType INQUIRY = new VerisignTransactionType("I", "prm.global.creditcard.verisign.transactiontype.inquiry");
    /**
     * Transaction type to see if there is sufficient money in the account and
     * if the credit card information is correct.
     */
    public static final VerisignTransactionType AUTHORIZATION = new VerisignTransactionType("A", "prm.global.creditcard.verisign.transactiontype.authorization");
    /**
     * Account type which is run after a transaction.  In VeriSign terms, this
     * is usually done after you "Ship" a product.
     */
    public static final VerisignTransactionType DELAYED_CAPTURE = new VerisignTransactionType("D", "prm.global.creditcard.verisign.transactiontype.delayedcapture");
    /**
     * The default type to be returned if there is not a type code which
     * matches one of our predefined types.
     */
    public static final VerisignTransactionType DEFAULT = SALE;

    /**
     * Get the VerisignTransactionType that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>VerisignTransactionType</code> we want to find.
     * @return a <code>VerisignTransactionType</code> corresponding to the supplied ID, or
     * the DEFAULT <code>VerisignTransactionType</code> if one cannot be found.
     */
    public static VerisignTransactionType getForID(String id) {
        VerisignTransactionType toReturn = DEFAULT;

        for (Iterator it = types.iterator(); it.hasNext();) {
            VerisignTransactionType type = (VerisignTransactionType)it.next();
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
    /** A Unique identifier for this VerisignTransactionType. */
    private String id;
    /** A token used to find a human-readable name for this VerisignTransactionType. */
    private String displayToken;

    /**
     * Private constructor which creates a new VerisignTransactionType instance.
     *
     * @param id a <code>String</code> value which is a unique identifier for
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private VerisignTransactionType(String id, String displayToken) {
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
     * Return a human-readable display name for this VerisignTransactionType.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * VerisignTransactionType.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }
}
