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
package net.project.billing.payment;

import java.sql.SQLException;

import net.project.persistence.PersistenceException;

public class PaymentDAO implements java.io.Serializable {

    /**
     * Returns the select part to load one or more payment informations.
     * <li><code>PN_PAYMENT_INFORMATION</code> aliased to <code>pi</code>
     * <li>No <code>WHERE</code> clause
     * @return the select part of a query
     */
    public static String getQueryLoadPaymentInformation() {
        StringBuffer query = new StringBuffer();
        query.append("select pi.payment_id, pi.payment_model_id, pi.party_id ");
        query.append("from pn_payment_information pi ");
        return query.toString();
    }

    /**
     * Populates a payment information object from a resultset.
     * This fully populates the payment information (including model, party etc.)
     * @param result the resultset containing data to populate
     * @param payment the payment object to populate
     * @throws SQLException if there is a problem reading columns from the result
     * @throws PersistenceException if there is a problem populating the payment information
     */
    public static void populatePaymentInformation(java.sql.ResultSet result, PaymentInformation payment)
            throws SQLException, PersistenceException {

        String partyID = result.getString("party_id");

        payment.setID(result.getString("payment_id"));
        payment.loadModel(result.getString("payment_model_id"));

        if (partyID != null) {
            payment.loadParty(partyID);
        }

    }


}

