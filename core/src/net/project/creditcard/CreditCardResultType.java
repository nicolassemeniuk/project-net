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
package net.project.creditcard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;

/**
 * A typed enumeration of CreditCardResultType classes.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class CreditCardResultType {
    /** This list contains all of the possible types for this typed enumeration. */
    private static List types = new ArrayList();
    /** The credit card has been charged and no errors were detected. */
    public static final CreditCardResultType SUCCESS = new CreditCardResultType("10", "prm.global.creditcard.resulttype.success");
    /** We were unable to connect or stay connected to the credit card processor. */
    public static final CreditCardResultType NETWORK_ERROR = new CreditCardResultType("20", "prm.global.creditcard.resulttype.networkerror");
    /** An error was found in the configuration file, such as an invalid server being specified. */
    public static final CreditCardResultType CONFIGURATION_ERROR = new CreditCardResultType("30", "prm.global.creditcard.resulttype.configurationerror");
    /** An error in programming code in the processor is causing the transaction to not be processed. */
    public static final CreditCardResultType INTERNAL_ERROR = new CreditCardResultType("40", "prm.global.creditcard.resulttype.internalerror");
    /** An invalid merchant account or password was specified. */
    public static final CreditCardResultType MERCHANT_ACCOUNT_ERROR = new CreditCardResultType("50", "prm.global.creditcard.resulttype.merchantaccounterror");
    /** The user's credit card was declined. */
    public static final CreditCardResultType TRANSACTION_DECLINED_ERROR = new CreditCardResultType("60", "prm.global.creditcard.resulttype.transactiondeclinederror");
    /** Information about the account, such as the address or expiration date, was invalid. */
    public static final CreditCardResultType INVALID_ACCOUNT_DATA = new CreditCardResultType("70", "prm.global.creditcard.resulttype.unrecognizedaccountdata");
    /** An error type was found that has not been categorized or identified. */
    public static final CreditCardResultType UNRECOGNIZED_ERROR_TYPE = new CreditCardResultType("80", "prm.global.creditcard.resulttype.unrecognizederror");
    /**
     * This is the error type that will be indicated if we haven't previously
     * categorized that error type.  Currently, this defaults to
     * {@link #UNRECOGNIZED_ERROR_TYPE}
     */
    public static final CreditCardResultType DEFAULT = UNRECOGNIZED_ERROR_TYPE;

    /**
     * Get the CreditCardResultType that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>CreditCardResultType</code> we want to find.
     * @return a <code>CreditCardResultType</code> corresponding to the supplied ID, or
     * the DEFAULT <code>CreditCardResultType</code> if one cannot be found.
     */
    public static CreditCardResultType getForID(String id) {
        CreditCardResultType toReturn = DEFAULT;

        for (Iterator it = types.iterator(); it.hasNext();) {
            CreditCardResultType type = (CreditCardResultType)it.next();
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
    /** A Unique identifier for this CreditCardResultType. */
    private String id;
    /** A token used to find a human-readable name for this CreditCardResultType. */
    private String displayToken;

    /**
     * Private constructor which creates a new CreditCardResultType instance.
     *
     * @param id a <code>String</code> value which is a unique identifier for 
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private CreditCardResultType(String id, String displayToken) {
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
     * Return a human-readable display name for this CreditCardResultType.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * CreditCardResultType.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }
}
